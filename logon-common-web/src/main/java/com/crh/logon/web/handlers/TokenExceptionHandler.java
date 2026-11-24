/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.handlers;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.config.ExceptionConfig;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.TokenException;

/**
 * @author AdNovum Informatik AG
 * 
 * we use struts tokens to ensure that a request from a rendered form is submitted
 * exactly once. This is to prevent doubleclicks or stray clicks from impatient users.
 */
public class TokenExceptionHandler extends GeneralExceptionHandler {
	private static final JcanLogger LOG =
		JcanLoggerFactory.getLogger(TokenExceptionHandler.class);
    public ActionForward execute(Exception ex,
            ExceptionConfig ae,
            ActionMapping mapping,
            ActionForm formInstance,
            HttpServletRequest request,
            HttpServletResponse response)
    throws ServletException {
		// [[
		ActionForward result = null;
		Object[][] params = {{"ex", ex},
							{"ae", ae},
							{"mapping", mapping},
							{"formInstance", formInstance},
							{"request", request},
							{"response", response}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "execute", params);
		}
		// ]]
		try {
            MessageUtils.addActionError(request, ex, TokenException.MSG_KEY);
            MessageUtils.addActionError(request, ex, TokenException.MSG_KEY_1);
            MessageUtils.addActionError(request, ex, TokenException.MSG_KEY_2);
            MessageUtils.addActionError(request, ex, TokenException.MSG_KEY_3);
	        return result = mapping.findForward("startPage");
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
