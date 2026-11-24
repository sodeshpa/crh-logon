/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.listeners;

import java.util.Date;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.UserActivityService;
import com.crh.logon.constants.Constant;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.web.utils.ServiceLocatorProvider;
import com.crh.logon.persistence.entities.admin.UserEntity;

/**
 * @author AdNovum Informatik AG
 */
public class LogonSessionListener implements HttpSessionListener {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LogonSessionListener.class);

	public void sessionCreated(HttpSessionEvent event) {
		// [[
		Object[][] params = {{"event", event}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sessionCreated", params);
		}
		// ]]
		try {
			/* this is all now done in ConfigBeanFilter. two reasons:
			 * 1. HttpSessionListener don't have init params, so there is no
			 *	easy way to know in which context we're running (to find out
			 *	if this is the ext, dmz or int app)
			 * 2. it's all in the same place: max lifetime enforcement and
			 *	setting of the max inactivity time.
			 * we keep this listener for tracing purposes.
			 */
			/*
			HttpSession session = event.getSession();
			AuthUtils.setAuthorized(true);
			ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator();
			ConfigService configService = serviceLocator.fetchConfigService();
			
			ConfigBean cb = configService.getConfigBean();
			int timeout = 60 * cb.getAppSessionCrhSessionTimeoutMin();
			session.setMaxInactiveInterval(timeout);
			LOG.info("setting MaxInactiveInterval for '" + session.getId() + "' to " + 
					DateTimeUtils.formatDuration(timeout) + " (" + timeout + ")");
			*/
			; //PMD
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	public void sessionDestroyed(HttpSessionEvent event) {
		// [[
		Object[][] params = {{"event", event}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sessionDestroyed", params);
		}
		// ]]
		try {
			HttpSession session = event.getSession();
			long cur = (new Date()).getTime();
			LOG.info("session id:		  " + "'" + session.getId() + "' destroyed");
			LOG.info("creation time:	   " + new Date(session.getCreationTime()));
			LOG.info("last access time:	" + new Date(session.getLastAccessedTime()));
			LOG.info("cur session time:	" + DateTimeUtils.formatDuration(cur - session.getCreationTime()));
			LOG.info("cur inactivity time: " + DateTimeUtils.formatDuration(cur - session.getLastAccessedTime()));
			LOG.info("max inactive time:   " + DateTimeUtils.formatDuration(session.getMaxInactiveInterval() * 1000));
			
    		try {
    			String loginType = (String) session.getAttribute(Constant.LOGON_LOGIN_TYPE_SESSION_ATTR_KEY);
    			if (loginType != null && !Constant.LOGIN_TYPE_LOGGED_OUT.equals(loginType)) {
	        		UserEntity user = (UserEntity) session.getAttribute(Constant.USER_FILTER_USER_SESSION_ATTR_KEY);
	        		UserActivityService uas = ServiceLocatorProvider.getServiceLocator().getUserActivityService();
	        		String ipAddress = (String) session.getAttribute(Constant.LOGON_LOGIN_IP_ADDRESS);
	        		if(user != null && uas != null) {
		        		uas.recordLogOut(user.getUserId(), loginType, true, ipAddress);	        			
	        		}
	        		session.setAttribute(Constant.LOGON_LOGIN_TYPE_SESSION_ATTR_KEY, Constant.LOGIN_TYPE_LOGGED_OUT);
    			}
    		}
    		catch (Exception e) {
    			LOG.error("could not record logout user activity", e);
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
	
	/*
	private String formatDate (long time) {
		return format.format(new Date(time));
	}
	
	private String formatDuration (long dur) {
		return String.format("%dh%02dm%02ds", dur/3600, (dur%3600)/60, (dur%60));
	}
	*/
}
