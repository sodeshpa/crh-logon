/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.security.auth;

import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;

import com.crh.logon.constants.Constant;

/**
 * @author AdNovum Informatik AG
 */
public class LoginModuleUtil {
	
	private static final Logger log = Logger.getLogger(LoginModuleUtil.class);

	public static HttpServletRequest getHttpRequest() {
		HttpServletRequest request = null;
		try {
			request = (HttpServletRequest) PolicyContext.getContext(Constant.POLICY_CONTEXT_REQUEST_KEY);
		}
		catch (PolicyContextException e) {
			log.error("could not obtain request from policy context", e);
		}
		return request;
	}
	
	public static HttpSession getHttpSession() {
		HttpSession session = null;
		HttpServletRequest request = LoginModuleUtil.getHttpRequest();
		if (request != null) {
			session = request.getSession(true);
		}
		else {
			log.error("request is null");
		}
		return session;
	}
	
	public static void setSessionLoginType(String loginType) {
		HttpSession session = LoginModuleUtil.getHttpSession();
		if (session != null) {
			// debug
			if (log.isDebugEnabled()) {
				Object oldLoginType = session.getAttribute(Constant.LOGON_LOGIN_TYPE_SESSION_ATTR_KEY);
				if (oldLoginType != null) {
					if (oldLoginType instanceof String) {
						String oldLoginTypeStr = (String) oldLoginType;
						if (!oldLoginTypeStr.equals(loginType)) {
							log.debug("login type will be changed from " + oldLoginTypeStr + " to " + loginType);
						}
					}
					else {
						log.debug("oldLoginType is not a string: " + oldLoginType);
					}
				}
				else {
					log.debug("login type was not set before");
				}
			}
			
			// set login type
			log.info("set login type to " + loginType);
			session.setAttribute(Constant.LOGON_LOGIN_TYPE_SESSION_ATTR_KEY, loginType);
		}
		else {
			log.error("session is null, could not register login type");
		}
	}
}
