/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.services.eventbeans.in.mop;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.localinterfaces.AsyncMopEventOutServiceLocalInterface;
import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.PdaSessionServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UserActivityServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.constants.Constant;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.LoginRequest;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.MopeventHeaderOutEntity;
import com.crh.logon.persistence.entities.common.PdaDeviceEntity;
import com.crh.logon.persistence.entities.common.PdaSessionEntity;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.refdata.PdaSessionStatusBsEntity;
import com.crh.logon.utils.PasswordUtil;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class LoginRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LoginRequestHandler.class);

    @EJB private UserActivityServiceLocalInterface userActivityService;
    @EJB private RefDataGenericsServiceLocalInterface referentialDataService;
    @EJB private PdaSessionServiceLocalInterface pdaSessionService;
    @EJB private AsyncMopEventOutServiceLocalInterface asyncMopEventOutService;

    
    private static final String FIELD_PDA_ID = "PdaId";
    private static final String FIELD_PIN = "PIN";
        
    protected void validate(LogonEventRequest eventBean) {
    	
    	LoginRequest bean = (LoginRequest)eventBean;
    	validateMandatory(FIELD_PDA_ID, bean.getSenderPdaId());
    	rfidMandatory(FIELD_PDA_USER_LABEL, bean.getPdaUserRfidIdentity(), bean.getPdaUserLabel());
    	validateMandatory(FIELD_PIN, bean.getPin());
    }
    
    
    /**
     * creates a session for the given user and pdaDevice
     * @param eventBean
     * @param user
     * @param pdaDevice
     * @param force Create a new session even if a session for this user already exists.
     * @return the id of the created session
     * @throws LogonEventException if force is false and an active session on another device already exists. 
     */
    public PdaSessionEntity createSession(String eventName, UserEntity user, PdaDeviceEntity pdaDevice, boolean force) {
		// [[
		PdaSessionEntity result1 = null;
		Object[][] params = {{"eventName", eventName},
							{"user", user},
							{"pdaDevice", pdaDevice},
							{"force", force + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createSession", params);
		}
		// ]]
		try {
            ReadWriteDao<PdaSessionEntity> dao = getDaoProvider().getPdaSessionEntityDao();
            
            Filter filter = new FilterImpl();
            filter.addExpression(Restrictions.neq(PdaSessionEntity.ATTR_PDA_SESSION_STATUS_CD, Constant.PDA_SESSION_STATUS_TERMINATED));
            filter.addExpression(Restrictions.or(Restrictions.eq(PdaSessionEntity.ATTR_USER_ID, user.getUserId()), Restrictions.eq(PdaSessionEntity.ATTR_PDA_DEVICE, pdaDevice)));
            List openSessions = dao.find(filter);
			// inactivate old session with different pdadevice
            for (Iterator it = openSessions.iterator();it.hasNext();) {
            	PdaSessionEntity session = (PdaSessionEntity)it.next();
            	if ( (session.getPdaDevice().getPdaDeviceId().equals(pdaDevice.getPdaDeviceId())) || (force) ) {
            		pdaSessionService.closeSession(session, true);
            	}
            	else {
            		
            		throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.SESSION_EXISTS));
            	}
            }
            
            PdaSessionEntity session = new PdaSessionEntity();
            session.setPdaSessionStatus(referentialDataService.findBsEntity(PdaSessionStatusBsEntity.class, Constant.PDA_SESSION_STATUS_INITIALIZING));
            session.setPdaDevice(pdaDevice);
            session.setUser(user);
            session = dao.persist(session);
            pdaSessionService.updateSession(session, eventName);
			// set last user for pda device
            pdaDevice.setLastUser(user);
			
            // set needs resending flag to false for the given pda
            filter = new FilterImpl();
            filter.addExpression(Restrictions.eq(MopeventHeaderOutEntity.ATTR_PDA_NO, pdaDevice.getPdaDeviceNo()));
            filter.addExpression(Restrictions.eq(MopeventHeaderOutEntity.ATTR_NEEDS_RESENDING_FLAG, Boolean.TRUE));
            List outevents = getDaoProvider().getMopeventHeaderOutEntityDao().find(filter);
            for (Iterator iterator = outevents.iterator(); iterator.hasNext();) {
                MopeventHeaderOutEntity outevent = (MopeventHeaderOutEntity) iterator.next();
                outevent.setNeedsResendingFlag(Boolean.FALSE);
            }
            
            return result1 = session;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result1);
			}
		// ]]
		}
    }

    
    protected LogonEventResponse perform(LogonEventRequest eventBean) {
		// [[
		LogonEventResponse result = null;
		Object[][] params = {{"eventBean", eventBean}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "perform", params);
		}
		// ]]
		try {
			LoginRequest bean = (LoginRequest)eventBean;
            
            // Get user
            RfidobjectEntity rfidObject = null;
            try {
            	rfidObject = getRfidObject(bean.getPdaUserRfidIdentity(), bean.getPdaUserLabel(), Constant.RFIDOBJECT_TYPE_PDA_USER);
            } catch (LogonEventException e) {
            	// ignore this and throw an unknown user exception instead
            }
            if (rfidObject == null) {
            	
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.UNKNOWN_USER));
            }
            Filter filter = new FilterImpl();
            filter.addExpression(Restrictions.eq(UserEntity.ATTR_PDA_USER_RFIDOBJECT, rfidObject));
            UserEntity user = getDaoProvider().getUserEntityDao().findSingle(filter);
            if (user == null) {
            	
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.UNKNOWN_USER));
            }
            
            Filter<DriverEntity> driverFilter = new FilterImpl<DriverEntity>();
            driverFilter.addExpression(Restrictions.eq(DriverEntity.ATTR_USER_ID, user.getUserId()));
            DriverEntity driver = getDaoProvider().getDriverEntityDao().findSingle(driverFilter);
            
            if(driver != null) {
            	Date currentDate = new Date();
	            if(currentDate.before(driver.getCertifiedFromDat()) || currentDate.after(driver.getCertifiedToDat())) {
	            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.INVALID_CERTIFICATE));
	            }
            }
            
            // Check PIN
    		String pinInput = bean.getPin();
    		if (!PasswordUtil.validatePassword(pinInput, user.getPdaPin())) {
    			userActivityService.recordPdaLogInAttempt(user.getPdaUserNo(), false);
    			throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.WRONG_PIN));
    		}
            
			// Get PDA
            filter = new FilterImpl();
            filter.addExpression(Restrictions.eq(PdaDeviceEntity.ATTR_PDA_DEVICE_NO, bean.getSenderPdaId()));
            filter.addExpression(Restrictions.eq(PdaDeviceEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
            PdaDeviceEntity pdaDevice = getDaoProvider().getPdaDeviceEntityDao().findSingle(filter);
            
            if (pdaDevice == null) {
            	userActivityService.recordPdaLogInAttempt(user.getPdaUserNo(), false);
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.UNKNOWN_PDA));
            }
            
            Date pinValidTo = user.getPdaPinValidToDat();
            if ( (pinValidTo == null) || (pinValidTo.after(new Date())) ) {
            	PdaSessionEntity session = createSession(bean.getEventName(), user, pdaDevice, bean.isForceLogin()!=null?bean.isForceLogin().booleanValue():false);
            	asyncMopEventOutService.sendSetupToPDA(user, session);
            	
            	userActivityService.recordPdaLogInAttempt(user.getPdaUserNo(), true);
            	return result = createLogonEventResponse(MopResponseCode.LOGIN_OK, session.getPdaSessionId().toString());
            }
            
            userActivityService.recordPdaLogInAttempt(user.getPdaUserNo(), false);
            throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.OUTDATED_PIN));
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }
}
