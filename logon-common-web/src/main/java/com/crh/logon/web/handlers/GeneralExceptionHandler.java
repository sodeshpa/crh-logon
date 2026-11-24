/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.handlers;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.forms.FatalErrorForm;
import com.crh.logon.web.utils.ConfigBeanProvider;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ExceptionHandler;
import org.apache.struts.config.ExceptionConfig;
import org.apache.struts.util.MessageResources;
import org.apache.struts.validator.Resources;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.utils.Reason;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author AdNovum Informatik AG
 */
public class GeneralExceptionHandler extends ExceptionHandler {
    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(GeneralExceptionHandler.class);

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
			prepareErrorForm(request, ex);
			if (!ConfigBeanProvider.getConfigBean().getLogonProperties().getDevmode()) {
				MessageUtils.setActionException(request, ex, null);
				
				if ( (mapping.getInput() != null) && (request.getParameter("showPopup") == null) ) {			
					return result = mapping.getInputForward();
				}
				else if (request.getParameter("showPopup") != null) {
					return result = mapping.findForward("fatalErrorPopup");
				}
				else {
					return result = mapping.findForward("fatalError");
				}
			}
			else {
				
				return result = mapping.findForward("fatalError");
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }
    
    public static void prepareErrorForm(HttpServletRequest request, Throwable ex) {
        String errorId = fetchErrorIdFromException(ex);
        /* only prepare one error form per request */
        if (request.getAttribute("prepareErrorFormCalled") != null) {
            return;
        }
        request.setAttribute("prepareErrorFormCalled", new Boolean(true));
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        FatalErrorForm form = (FatalErrorForm) session.getAttribute("FatalError");
        if (form == null) {
            form = new FatalErrorForm();
            session.setAttribute("FatalErrorForm", form);
            LOG.debug("FatalErrorForm created.");
        }
        
        Reason reason = fetchReasonFromException(ex);
        MessageResources mr = Resources.getMessageResources(request);
        
        form.setErrorId(errorId);
        form.setException(ex);
        if (reason != null) {
            form.setReason(mr.getMessage(reason.getReason()));
        }
        form.traceState();
    }
    
    /**
     * Convenience method which takes an Throwable and returns its error id if
     * it is of type LogonException, otherwise repeats this process for
     * the cause of the exception until the cause is null.
     * 
     * @param ex
     * @return id or null
     */
    private static String fetchErrorIdFromException(Throwable ex) {
        String result = null;
        while (ex != null) {
            if (ex instanceof LogonException) {
                LogonException tbe = (LogonException) ex;
                result = tbe.getId();
                break;
            }
            if (ex instanceof ServletException) {
                ServletException servletException = (ServletException) ex;
                ex = servletException.getRootCause();
            }
            else {
                ex = ex.getCause();
            }
        }
        if (result == null) {
            result = StringUtils.generateErrorId();
        }
        return result;
    }
    
    private static Reason fetchReasonFromException(Throwable ex) {
        Reason result = null;
        while (ex != null) {
            if (ex instanceof LogonException) {
                LogonException tbe = (LogonException) ex;
                result = tbe.getReason();
                break;
            }
            ex = ex.getCause();
        }
        return result;
    }
}
