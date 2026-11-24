/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.filters;

import java.io.IOException;

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

import com.crh.logon.web.utils.ConfigBeanProvider;

/**
 * @author AdNovum Informatik AG
 *
 */
public class SessionIdFilter implements Filter {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SessionIdFilter.class);

	private final String SECURE = "; Secure";

	private final String HTTP_ONLY = "; HttpOnly";
	
	public static final String PATH_ATTR = "; Path=";
	
	/**
	 *  The AJP Connector (check logon/container/conf/jboss-web.deployer/server.xml) 'emptySessionPath'
	 *  attribute is set to 'true', check the attribute description:
	 *  "
	 *  If set to true, all paths for session cookies will be set to /. This can be useful for 
	 *  portlet specification implementations, but will greatly affect performance if many 
	 *  applications are accessed on a given server by the client. If not specified, this attribute is set to false.
	 *  "
	 *  
	 *  -->
	 *  
	 *  Use this path ("/") when overwriting the "SET-COOKIE" attribute. (NOTE: The server also sends this path)
	 */
	public static final String DEFAULT_SESSION_COOKIE_PATH = "/";

	public SessionIdFilter() {
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		LOG.debug("start SessionIdFilter");

		HttpServletResponse response = (HttpServletResponse) res;
		HttpServletRequest request = (HttpServletRequest) req;
		HttpSession session = request.getSession();

		boolean useHttpOnlySessionIdFilter = ConfigBeanProvider.getConfigBean().getLogonProperties().getUseHttpOnlySessionIdFilter();
		boolean useHttpOnlySecureSessionIdFilter = ConfigBeanProvider.getConfigBean().getLogonProperties().getUseHttpOnlySecureSessionIdFilter();
		
		// switchable with servlet filter parameter
		if (useHttpOnlySessionIdFilter && response.containsHeader("SET-COOKIE") && !isSignedHttpOnly(session)) {
			// Run only first access. Second will be SSO session ID (provides authorization crossing module), 
			// and we must not override that.
			String cookie = "JSESSIONID=" + session.getId();
			cookie +=  PATH_ATTR +
					DEFAULT_SESSION_COOKIE_PATH;

			LOG.debug("Cookie original: " + cookie);

			if (useHttpOnlySecureSessionIdFilter) {
				// Https
				cookie += SECURE;
			}
			cookie += HTTP_ONLY;
			response.setHeader("SET-COOKIE", cookie);
			// already flagged -> we store this state
			session.setAttribute("signedHttpOnly", true);

			LOG.debug("Cookie manipulated: " + cookie);
		}

		chain.doFilter(request, response);

		LOG.debug("finish SessionIdFilter");
	}

	/**
	 * @param response
	 * @return
	 */
	private boolean isSignedHttpOnly(HttpSession session) {
		Object signedHttpOnly = session.getAttribute("signedHttpOnly");
		return signedHttpOnly != null && Boolean.parseBoolean(signedHttpOnly.toString());
	}

	/* 
	 * false httpOnlyEnabled servlet filter parameter switch off HttpOnly mode.
	 */
	public void init(FilterConfig config) {
		LOG.info("setting up '" + SessionIdFilter.class + "'");
	}

	public void destroy() {
		LOG.info("destroying '" + SessionIdFilter.class + "'");
	}
}
