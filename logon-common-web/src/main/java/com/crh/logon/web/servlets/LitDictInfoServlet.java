/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.ConfigService;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.taglib.fmt.MessageSupport;

/**
 * Simply shows or tests the version of the deployed application
 * 
 * @author AdNovum Informatik AG
 */
public class LitDictInfoServlet extends LogonServlet {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LitDictInfoServlet.class);
	private ConfigService configService;
	
	@Override
	public void init() throws ServletException {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			super.init();
			configService = getServiceLocator().fetchConfigService();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		// [[
		Object[][] params = {{"req", req},
							{"resp", resp}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "doGet", params);
		}
		// ]]
		try {
			// TODO gergely: do we need auth for this?
			//Authorization.authorize(Authorization.TEST_VERSIONCHECK);
			resp.setContentType("text/html");
			PrintWriter pw = resp.getWriter();
			
            HttpSession session = req.getSession(true);
            boolean enabled = false;
            
            if (session != null && session.getAttribute(MessageSupport.LITDICT_INFO_FLAG_KEY) != null) {
            	enabled = true;
            }
            
            if (session != null && "toggle".equals(req.getParameter("action"))) {
            	enabled = !enabled;
            	if (enabled) {
                    LOG.debug("LitDict info tooltip: ON");
            		session.setAttribute(MessageSupport.LITDICT_INFO_FLAG_KEY, Boolean.TRUE);
            	}
            	else {
                    LOG.debug("LitDict info tooltip: OFF");
            		session.removeAttribute(MessageSupport.LITDICT_INFO_FLAG_KEY);
            	}
            }
			
			pw.println("Displaying LitDict info tooltips: " + (enabled ? "ON" : "OFF"));
			pw.println("<input type=\"button\" value=\"Toggle\" onClick=\"window.location.href='litDictInfo?action=toggle'\" />");
			
			pw.close();
		}
		catch (Throwable t) {
			ExceptionUtil.errException(LOG, t, params);
		}
		finally {
			//AuthUtils.deauthorize();
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

}
