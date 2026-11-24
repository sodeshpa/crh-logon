/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.services.eventbeans.in.mop;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.AsyncMopEventOutServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.constants.Constant;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.holcim.logon.eventbeans.in.mop.PdaStatusRequest;
import com.crh.logon.persistence.entities.common.PdaDeviceEntity;
import com.crh.logon.persistence.entities.common.PdaSessionEntity;
import com.crh.logon.persistence.entities.refdata.PdaSessionStatusBsEntity;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class PdaStatusRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PdaStatusRequestHandler.class);

	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;
    @EJB private AsyncMopEventOutServiceLocalInterface asyncMopEventOutService;
       
    public PdaSessionEntity getSession(String pdaDeviceNo) {
		// [[
		PdaSessionEntity result = null;
		Object[][] params = {{"pdaDeviceNo", pdaDeviceNo}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getSession", params);
		}
		// ]]
		try {
            Filter filter = new FilterImpl();
            filter.addExpression(Restrictions.neq(PdaSessionEntity.ATTR_PDA_SESSION_STATUS_CD, Constant.PDA_SESSION_STATUS_TERMINATED));
            filter.addExpression(Restrictions.eq(PdaSessionEntity.ATTR_PDA_DEVICE + "." + PdaDeviceEntity.ATTR_PDA_DEVICE_NO, pdaDeviceNo));  
            List sessionList = getDaoProvider().getPdaSessionEntityDao().find(filter);
            if (sessionList.size() > 1) {
            	
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "unable to determine session"));
            }
            else if (!sessionList.isEmpty()) {
            
            	return result = (PdaSessionEntity)sessionList.get(0);
            }
			
            return result = null;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
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
            PdaStatusRequest bean = (PdaStatusRequest)eventBean;
            PdaSessionEntity session = null;
            
            switch (bean.getConnectionStatus()) {
	            case ONLINE:
	            	LOG.debug("PDA online " + bean.getSenderPdaId());				
	            	session = getSession(bean.getSenderPdaId());
	            	if ( (session != null) && ( (session.getPdaSessionStatus().getPdaSessionStatusCd().longValue() == Constant.PDA_SESSION_STATUS_EVENTS_PENDING)) ) {
	            		session.setPdaSessionStatus(referentialDataService.findBsEntity(PdaSessionStatusBsEntity.class, Constant.PDA_SESSION_STATUS_READY));
	            		asyncMopEventOutService.resendOutEvents(session);
	            	}
	            	else if (session == null) {
	            		LOG.debug("no session");
	            	}
	            break;
	            case OFFLINE:
	            	LOG.debug("PDA offline " + bean.getSenderPdaId());
	            break;
	            case INTERRUPTED:
	            	LOG.debug("PDA interrupted " + bean.getSenderPdaId());
	            	session = getSession(bean.getSenderPdaId());
	            	if (session != null) { 
	            		if (session.getPdaSessionStatus().getPdaSessionStatusCd().longValue() == Constant.PDA_SESSION_STATUS_READY) {
	            			session.setPdaSessionStatus(referentialDataService.findBsEntity(PdaSessionStatusBsEntity.class, Constant.PDA_SESSION_STATUS_EVENTS_PENDING));
	            		}
	            		else if (session.getPdaSessionStatus().getPdaSessionStatusCd().longValue() != Constant.PDA_SESSION_STATUS_EVENTS_PENDING) {
	            			session.setPdaSessionStatus(referentialDataService.findBsEntity(PdaSessionStatusBsEntity.class, Constant.PDA_SESSION_STATUS_TERMINATED));					
	            		}
	            	}
	            	else {
	            		LOG.debug("no session");
	            	}
	            break;
	            default:
	            	LOG.error("Unknown pda status: " + bean.getConnectionStatus());
	            break;
            }
            
            return result = createLogonEventResponse(MopResponseCode.OK);
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
