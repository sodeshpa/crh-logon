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
import javax.servlet.http.HttpSession;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.ConfigService;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.web.utils.ServiceLocatorProvider;
import com.crh.logon.utils.AuthUtils;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonProperties;
import com.crh.logon.utils.time.DateTimeZoneFactory;
import com.crh.logon.web.listeners.SpringBeanListener;
import com.crh.logon.web.utils.ConfigBeanProvider;

/**
 * this filter makes config bean available through the ConfigBeanProvider and enforces some
 * settings (such as the max session lifetime)
 * 
 * @author AdNovum Informatik AG
 */
public class ConfigBeanFilter implements Filter {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ConfigBeanFilter.class);
    private static final String FILTER_CONFIG_CONTEXT = "context";
	private String context;

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
			// we make sure that with every request we have the current config bean
			// which is used for every request anyways.
            AuthUtils.setAuthorized(true);
			ConfigService configService = serviceLocator.getConfigService();
			ConfigBean cb = configService.getConfigBean();
			
			// make sure that we store the 'local' logonproperties into the ConfigBean
			LogonProperties logonProperties = SpringBeanListener.getLogonProperties(request);
			cb.setLogonProperties(logonProperties);
			ConfigBeanProvider.setConfigBean(cb);

			// set our global default timezone id
			String defaultTimeZoneId = cb.getAppDefaultTimezone();
			DateTimeZoneFactory.setDefaultTimeZoneId(defaultTimeZoneId);

			HttpSession session = ((HttpServletRequest)request).getSession();
		    /**
		     * [min]. Maximale Lebenszeit einer Session f�r Crh-Angestellte.
		     * Nach dieser Zeit wird die Session un�ltig, ein erneutes Anmelden
		     * ist notwendig. LOGON zerst�rt die Session (Java EE Session).
		     */
			long maxLifetime = cb.getAppSessionCrhSessionLifetimeMin();
			int inactivityTimeout = 60 * cb.getAppSessionCrhSessionTimeoutMin();
			
			if (context != null && context.equals("cust")) {
				LOG.debug("using cust settings...");
				maxLifetime = cb.getAppSessionWebSessionLifetimeMin();
				inactivityTimeout = 60 * cb.getAppSessionWebSessionTimeoutMin();
			}
			session.setMaxInactiveInterval(inactivityTimeout);
			long curLifetime =  ((new Date()).getTime() - session.getCreationTime()) / (60 * 1000);
			LOG.debug("checking: " + curLifetime + "m > " +  maxLifetime + "m");
			LOG.debug("inactivityTimeout: " + inactivityTimeout);
			if (curLifetime > maxLifetime) {
				LOG.debug("max. life time exceeded! invalidating session...");
				session.invalidate();
				// redirect to the login expired page
				request.getRequestDispatcher("/LoginInactivity.jsp").forward(request, response);
				return;
			}
			
			// revoke authorization, now the action must authorize explicitly
			AuthUtils.deauthorize();
			chain.doFilter(request, response);
		}
		finally {
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
			context = config.getInitParameter(FILTER_CONFIG_CONTEXT);
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
	
}
