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

/**
 * @author AdNovum Informatik AG
 *
 * TODO: implement more elaborate exception handling for cases such as
 * HibernateOptimisticLockingException (i.e. more user friendly behavior)
 * For now we just use the GeneralExceptionHandler untouched.
 */
public class DataAccessExceptionHandler extends GeneralExceptionHandler {
	private static final JcanLogger LOG =
		JcanLoggerFactory.getLogger(DataAccessExceptionHandler.class);
    public ActionForward execute(Exception ex,
            ExceptionConfig ae,
            ActionMapping mapping,
            ActionForm formInstance,
            HttpServletRequest request,
            HttpServletResponse response)
    throws ServletException
    {
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
	        return result = super.execute(ex, ae, mapping, formInstance, request, response);
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
