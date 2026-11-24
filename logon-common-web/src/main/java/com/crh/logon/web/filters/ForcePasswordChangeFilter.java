/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.filters;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.UserService;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.web.utils.ServiceLocatorProvider;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.utils.AuthUtils;
import com.crh.logon.web.utils.UserProvider;

/**
 * @author AdNovum Informatik AG
 */
public class ForcePasswordChangeFilter implements Filter {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ForcePasswordChangeFilter.class);
	
	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// [[
		Object[][] params = {{"request", request},
							{"response", response},
							{"chain", chain}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "doFilter", params);
		}
		// ]]
		try {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			
			String contextPath = httpServletRequest.getContextPath();
			String pathInfo = httpServletRequest.getPathInfo();
			String requestURI = httpServletRequest.getRequestURI();
			StringBuffer requestURL = httpServletRequest.getRequestURL();
			if (logDebugEnabled) {
				LOG.debug("context path: " + contextPath);
				LOG.debug("pathinfo: " + pathInfo);
				LOG.debug("request uri: " + requestURI);
			}
			
			boolean filter = true;
			
			if ((requestURI.matches(".*/passwordchange/PasswordChange(Entry)?\\.do.*") || requestURI.endsWith("/Logout.do")) ) {
				filter = false;
				// TODO (istvan May 14, 2009): do we need more checks here ?
			}
			
			if (!filter) {
				//if we coming from pwdchange page, do not check, just bypass
				chain.doFilter(request, response);
				return;
			}
				
			UserEntity userLoggedIn = UserProvider.getUser();
			
			boolean forcePwdChange = true;
			
			if (! userLoggedIn.getExtLoginActiveFlag()) {
				forcePwdChange = false;
			}
			
			Date extLoginPwdValidToDate = userLoggedIn.getExtLoginPwdValidToDat();
			Date now = new Date (System.currentTimeMillis());
			
			if (extLoginPwdValidToDate == null) {
				//active forever
				LOG.debug("extLoginPwdValidToDate == null");
				forcePwdChange = false;
			}
			else if (extLoginPwdValidToDate.after(now)) {
				LOG.debug("ext login valid is after now");
				forcePwdChange = false;
			}
			else {
				// Retrieve the user from DB, the pwd might have been change 
				// (in another session), invalidating the user in this session.
				AuthUtils.setAuthorized(true);
				ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator();
				UserService userService = serviceLocator.getUserService();
				UserEntity user = userService.fetchUserEntity(userLoggedIn.getUserId());
				AuthUtils.deauthorize();
				if ( (user.getExtLoginPwdValidToDat() == null) || (user.getExtLoginPwdValidToDat().after(now)) ) {
					LOG.debug("ext login valid is after now");
					forcePwdChange = false;
					UserFilter.resetUser(httpServletRequest);
				}
			}
			
			if (logDebugEnabled) {
				LOG.debug("ext login valid date: " + extLoginPwdValidToDate);
				LOG.debug("now : " + now);
				LOG.debug("forcePwdChange : " + forcePwdChange);
				LOG.debug("request url: " + requestURL.toString());
			}
			
			if (forcePwdChange) {
				//do the redirect
				((HttpServletResponse)response).sendRedirect("/logon/cust/passwordchange/PasswordChangeEntry.do?" + Constant.KEY_PASSWORD_CHANGE_ORIGINAL_URI + "=" + requestURL.toString());
			}
			else {
				chain.doFilter(request, response);
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

	public void init(FilterConfig config) throws ServletException {
		// [[
		Object[][] params = {{"config", config}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init", params);
		}
		// ]]
		try {
			if (LOG.isInfoEnabled()) {
				LOG.info(this.getClass().getName() + " initialized!");
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
