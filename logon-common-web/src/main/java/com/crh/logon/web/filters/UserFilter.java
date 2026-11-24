/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.filters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;

import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.TimeZone;

import com.crh.logon.constants.Constant;
import com.crh.logon.utils.AuthUtils;
import com.crh.logon.utils.LogonException;
import com.crh.logon.web.utils.ServiceLocatorProvider;
import org.apache.struts.Globals;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.UserService;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.persistence.entities.admin.RoleEntity;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.utils.Reason;
import com.crh.logon.web.utils.UserProvider;

/**
 * This filter ensures that we have the current user's UserEntity in the session.
 * 
 * @author AdNovum Informatik AG
 */
public class UserFilter implements Filter {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(UserFilter.class);
	
	private static class UserLocaleRequestWrapper extends HttpServletRequestWrapper {
		
		private Locale locale;
		
		public UserLocaleRequestWrapper(HttpServletRequest request, Locale locale) {
			
			super(request);
			this.locale = locale;
		}
		
		public Locale getLocale() {
			
			return this.locale;
		}
	}
	
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
			ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator();

			HttpServletRequest req = (HttpServletRequest)request;
			HttpSession session = req.getSession();
			
			UserEntity user = (UserEntity)session.getAttribute(Constant.USER_FILTER_USER_SESSION_ATTR_KEY);
			RoleEntity role = (RoleEntity)session.getAttribute(Constant.USER_FILTER_ROLE_SESSION_ATTR_KEY);
			HashMap groups = (HashMap)session.getAttribute(Constant.USER_FILTER_GROUPS_SESSION_ATTR_KEY);
			TimeZone timezone = (TimeZone)session.getAttribute(Constant.USER_FILTER_TIMEZONE_SESSION_ATTR_KEY);

			// temporarily allow to call services (user service / timezone service)
			AuthUtils.setAuthorized(true);
			try {
				if (user == null || groups == null) {
					UserService userService = serviceLocator.getUserService();

					groups = AuthUtils.groups();
					Long userId = AuthUtils.getUserId();
					if (userId == null) {
						throw new LogonException("userId == null", Reason.RES_ERROR_INIT);
					}
					user = userService.fetchUserEntity(userId);
					if (user == null) {
						throw new LogonException("user == null", Reason.RES_ERROR_INIT);
					}
					role = userService.fetchRole(user);
					if (role == null) {
						throw new LogonException("role == null", Reason.RES_ERROR_INIT);
					}

					timezone = serviceLocator.getTimeZoneService().loadForUser(user);

					session.setAttribute(Constant.USER_FILTER_USER_SESSION_ATTR_KEY, user);
					session.setAttribute(Constant.USER_FILTER_ROLE_SESSION_ATTR_KEY, role);
					session.setAttribute(Constant.USER_FILTER_GROUPS_SESSION_ATTR_KEY, groups);
					session.setAttribute(Constant.USER_FILTER_TIMEZONE_SESSION_ATTR_KEY, timezone);
					session.setAttribute(Constant.LOGON_LOGIN_IP_ADDRESS, LogonUtil.getClientIP());

					LOG.debug("UserId:			   " + user.getUserId());
					LOG.debug("LdapHolspace:	   " + user.getLdapHolspace());
					LOG.debug("ExtLoginActiveFlag: " + user.getExtLoginActiveFlag());
					LOG.debug("ExtLogin:		   " + user.getExtLogin());
					LOG.debug("PdaUserNumber:	   " + user.getPdaUserNo());
					LOG.debug("UserTypeCd:		   " + user.getUserType().getUserTypeCd());
					LOG.debug("PdaPinValidToDat:   " + user.getPdaPinValidToDat());
					LOG.debug("Role:			   " + role.getRoleName());
					LOG.debug("Timezone:		   " + timezone);
				}
			}
			finally {
				// revoke authorization, now the action must authorize explicitly
				AuthUtils.deauthorize();
			}

			UserProvider.setUser(user);
			UserProvider.setRole(role);
			UserProvider.setGroups(groups);
			UserProvider.setTimezone(timezone);

			// set the user locale
			String langCode = user.getApplicationLang().getLangCd();
			Locale locale = new Locale(langCode);
			// struts
			session.setAttribute(Globals.LOCALE_KEY, locale);
			// JSTL
			Config.set(session, Config.FMT_LOCALE, locale);
			
			chain.doFilter(new UserLocaleRequestWrapper((HttpServletRequest)request, locale), response);
		}
		finally {
			UserProvider.setUser(null);
			UserProvider.setRole(null);
			UserProvider.setGroups(null);
            UserProvider.setTimezone(null);
			// the threadlocals within AuthUtils will go back into the threadpool again
			// -> must deauthorize!
			AuthUtils.deauthorize();
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
			LOG.info(this.getClass().getName() + " initialized!");
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public static void resetUser(HttpServletRequest request){
		// [[
		Object[][] params = {{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(UserFilter.class, "resetUser", params);
		}
		// ]]
		try {
			HttpSession session = request.getSession();
			session.removeAttribute(Constant.USER_FILTER_USER_SESSION_ATTR_KEY);
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
