/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.Calendar;
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

import com.crh.logon.business.services.dispatchers.PdaSessionServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.CronstatusServiceLocalInterface;
import com.crh.logon.business.services.impl.cron.ScheduledTask;
import com.crh.logon.business.services.localinterfaces.PdaSessionServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UserActivityServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.eventbeans.LogonEventOutBean;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.PdaSessionEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.refdata.PdaSessionStatusBsEntity;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class PdaSessionServiceBean extends PdaSessionServiceDispatcher implements PdaSessionServiceLocalInterface {

	private static final JcanLogger LOG = 
		JcanLoggerFactory.getLogger(PdaSessionServiceBean.class);
	
    private static final PdaSessionStatusBsEntity PDA_SESSION_STATUS_INITIALIZING = new PdaSessionStatusBsEntity();
    private static final PdaSessionStatusBsEntity PDA_SESSION_STATUS_READY = new PdaSessionStatusBsEntity();
    private static final PdaSessionStatusBsEntity PDA_SESSION_STATUS_TERMINATED = new PdaSessionStatusBsEntity();
    private static final PdaSessionStatusBsEntity PDA_SESSION_STATUS_EVENTS_PENDING = new PdaSessionStatusBsEntity();
    private static final PdaSessionStatusBsEntity PDA_SESSION_STATUS_TIMED_OUT = new PdaSessionStatusBsEntity();
    
    static {
        PDA_SESSION_STATUS_INITIALIZING.setPdaSessionStatusCd(Constant.PDA_SESSION_STATUS_INITIALIZING);
        PDA_SESSION_STATUS_READY.setPdaSessionStatusCd(Constant.PDA_SESSION_STATUS_READY);
        PDA_SESSION_STATUS_TERMINATED.setPdaSessionStatusCd(Constant.PDA_SESSION_STATUS_TERMINATED);
        PDA_SESSION_STATUS_EVENTS_PENDING.setPdaSessionStatusCd(Constant.PDA_SESSION_STATUS_EVENTS_PENDING);
        PDA_SESSION_STATUS_TIMED_OUT.setPdaSessionStatusCd(Constant.PDA_SESSION_STATUS_TIMED_OUT);
    }
    
    @EJB private CronstatusServiceLocalInterface cronstatusService;
    @EJB private UserActivityServiceLocalInterface userActivityService;

    public PdaSessionEntity getSession(Long sessionId) {
		// [[
		PdaSessionEntity result = null;
		Object[][] params = {{"sessionId", sessionId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getSession", params);
		}
		// ]]
		try {
			
			FilterImpl filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(PdaSessionEntity.ATTR_PDA_SESSION_ID, sessionId));
			
			return result = getDaoProvider().getPdaSessionEntityDao().findSingle(filter);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }

    
    /**
     * Retrieves a truck's current session or null if there is no active session.
     * @param truck The truck to retrieve the session for 
     */
    public PdaSessionEntity getCurrentSession(TruckEntity truck) {
		// [[
		PdaSessionEntity result = null;
		Object[][] params = {{"truck", truck}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getCurrentSession", params);
		}
		// ]]
		try {
            if (!truck.getPdaAvailableFlag()) {
                LOG.info("No pda available for truck: " + truck.getTruckId());
            	return result = null;
            }
            Filter filter = new FilterImpl();
            filter.addExpression(Restrictions.eq(DriverEntity.ATTR_CURRENT_TRUCK, truck));
            DriverEntity driver = getDaoProvider().getDriverEntityDao().findSingle(filter);
            if (driver == null) {
                LOG.info("Could not find available driver and session for the following truck: " + truck.getTruckId());
            	return result = null;
            }
            
            filter = new FilterImpl();
            filter.addExpression(Restrictions.eq(PdaSessionEntity.ATTR_USER, driver.getUser()));
            filter.addExpression(Restrictions.neq(PdaSessionEntity.ATTR_PDA_SESSION_STATUS_CD, Constant.PDA_SESSION_STATUS_TERMINATED));
            List sessions = getDaoProvider().getPdaSessionEntityDao().find(filter);
            if (sessions.size() == 1) {
            	
            	return (PdaSessionEntity)sessions.get(0);
            }
            // No or multiple sessions found. This can happen after high load on 
            // the server. Unfortunately, it's unknown how this happens exactly 
            // (see ADN62589) since requests are synchronized by PDA-ID and 
            // cleaned up during login.
            for (Iterator it=sessions.iterator();it.hasNext();) {
            	PdaSessionEntity pdaSession = (PdaSessionEntity)it.next();
            	LOG.info("Closing invalid session: " + pdaSession.getPdaSessionId() + " pda=" + pdaSession.getPdaDevice().getPdaDeviceId() + " status=" + pdaSession.getPdaSessionStatus().getPdaSessionStatusCd() + " user=" + pdaSession.getUser().getUserId());
            	closeSession(pdaSession, true);
            }
            
            LOG.info("No session available for driver " + driver.getDriverId() + " of truck: " + truck.getTruckId());
            	
            return null;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }

    
    /**
     * close the session if exists
     * 
     * @param eventBean the close event
     */
    public void closeSession(PdaSessionEntity session, boolean isExpired) {
		// [[
		Object[][] params = {{"session", session},
							{"isExpired", isExpired + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "closeSession", params);
		}
		// ]]
		try {
            session.setPdaSessionStatus(PDA_SESSION_STATUS_TERMINATED);
            userActivityService.recordLogOut(session.getUser().getUserId(), Constant.LOGIN_TYPE_PDA, isExpired, null);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }

    
    public void updateSession(PdaSessionEntity session, String eventName) {
		// [[
		Object[][] params = {{"session", session},
							{"eventName", eventName}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateSession", params);
		}
		// ]]
		try {
            session.setLastReceivedEventName(eventName);
            session.setLastUpdateDat(new Date());
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }

    
    /**
     * 
     * @param out
     * @param session
     */
    public void initOutgoingEvent(LogonEventOutBean out, PdaSessionEntity session) {
		// [[
		Object[][] params = {{"out", out},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initOutgoingEvent", params);
		}
		// ]]
		try {
            out.setSenderPDAId(String.valueOf(session.getPdaDevice().getPdaDeviceNo()));
            out.setSessionId(String.valueOf(session.getPdaSessionId()));
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
    
    
    /**
     * 
     * @param out
     * @param session
     */
    public void initOutgoingEvents(List outs, PdaSessionEntity session) {
		// [[
		Object[][] params = {{"outs", outs},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initOutgoingEvents", params);
		}
		// ]]
		try {
            if (outs != null) {
                for (Iterator it = outs.iterator(); it.hasNext();) {
                    LogonEventOutBean out = (LogonEventOutBean) it.next();
                    initOutgoingEvent(out, session);
                }
            }
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }    
    
    
    @ScheduledTask(name = "pda_session_reaper", second = "30", minute = "*/5", hour = "*", configurationMethodName = "getPdaSessionReaperCronEnabled")	
    public void cleanPdaSessions() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "cleanPdaSessions");
		}
		// ]]
		try {
        	int pdaSessionTimeoutMin = getConfigBean().getAppSessionPDASessionTimeoutMin();
        	Calendar cal = Calendar.getInstance();
        	cal.add(Calendar.MINUTE, -1 * pdaSessionTimeoutMin);
        	Date maxUpdateDat = cal.getTime();
        	
        	ReadWriteDao<PdaSessionEntity> dao = getDaoProvider().getPdaSessionEntityDao();
        	Filter<PdaSessionEntity> filter = new FilterImpl<PdaSessionEntity>();
        	filter.addExpression(Restrictions.neq(PdaSessionEntity.ATTR_PDA_SESSION_STATUS_CD, Constant.PDA_SESSION_STATUS_TERMINATED));
        	filter.addExpression(Restrictions.neq(PdaSessionEntity.ATTR_PDA_SESSION_STATUS_CD, Constant.PDA_SESSION_STATUS_TIMED_OUT));
        	filter.addExpression(Restrictions.lt(PdaSessionEntity.ATTR_LAST_UPDATE_DAT, maxUpdateDat));
        	List<PdaSessionEntity> inactiveSessionList = dao.find(filter);
        	if (inactiveSessionList != null) {
        		for (PdaSessionEntity session : inactiveSessionList) {        			
        			session.setPdaSessionStatus(PDA_SESSION_STATUS_TIMED_OUT);
        			dao.merge(session);
        		}
        		if (!inactiveSessionList.isEmpty()) {
        			long runNo = cronstatusService._increaseCronjobRunNumber(Constant.CRONSTATUS_PDA_SESSION_REAPER);
        			LOG.info("Number of updated sessions: " + inactiveSessionList.size() + "; LAST_RUN_NUMBER: " + runNo + ";");
        		}
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
}
