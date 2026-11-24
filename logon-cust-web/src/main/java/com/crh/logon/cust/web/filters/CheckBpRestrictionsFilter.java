/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.cust.web.filters;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.UserService;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.utils.AuthUtils;
import com.crh.logon.utils.LogonException;
import com.crh.logon.web.utils.ServiceLocatorProvider;

/**
 * This filter ensures that the user has some Bp restrictions.
 * without restrictions, we invalidate the session.
 * 
 * The check is done periodically after a certain amount of time (MAXAGE)
 * 
 * @author AdNovum Informatik AG
 */
public class CheckBpRestrictionsFilter implements Filter {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(CheckBpRestrictionsFilter.class);
	private static final long MAXAGE = 300; // in seconds
	
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
		HttpServletRequest req = (HttpServletRequest)request;
		HttpSession session = req.getSession();
		try {
			String key = this.getClass().getName() ;
			Object attr = session.getAttribute(key);
			Date now = new Date();
			if (attr != null) {
				Date prev = (Date)attr;
				long ageInMs = now.getTime() - prev.getTime();
				LOG.debug("check if: " + ageInMs + " > " + MAXAGE * 1000);
				if (ageInMs > MAXAGE * 1000) {
					LOG.debug("MAXAGE exceeded, checking of BpRestrictions forced...");
					attr = null;
				}
			}
			if (attr == null) {
				// temporarily allow to call services
	            AuthUtils.setAuthorized(true);
				ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator();
				UserService userService = serviceLocator.getUserService();
				userService.hasBpRestrictions();
				session.setAttribute(key, now);
				// revoke authorization, now the action must authorize explicitly
				AuthUtils.deauthorize();
			}
			chain.doFilter(request, response);
		}
		catch (RuntimeException exception) {
			HttpServletResponse resp = (HttpServletResponse)response;
			resp.setContentType("text/html");
			PrintWriter pw = resp.getWriter();
			Throwable cause = exception.getCause();
			String errid = "";
			if (cause instanceof LogonException) {
				errid = "(" + ((LogonException)cause).getId() + ")";
			}
			pw.println("<br/><hr/>");
			pw.println("Access not allowed! " + errid);
			pw.println("<br/><hr/>");
			session.invalidate();
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
