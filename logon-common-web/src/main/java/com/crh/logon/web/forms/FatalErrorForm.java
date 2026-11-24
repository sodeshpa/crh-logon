/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.forms;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * Stores an otherwise unhanded exception, which occurred during the
 * execution of a struts action, for later use by a JSP error page.
 * These are exceptions from which the application cannot gracefully
 * recover, caught in the outermost try-catch block of the action.
 * The JSP error page displays the exception message and stack trace.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="FatalErrorForm"
 */
public class FatalErrorForm extends ActionForm implements Serializable {

    private static final long serialVersionUID = 6805827787321838433L;

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(FatalErrorForm.class);

    private Throwable exception;
    private String errorId;
    private String reason;
    
    public void traceState() {
        if (exception != null) {
            LOG.error("A fatal error occurred.");
            LOG.error("Error: " + errorId);
        	LOG.error(ExceptionUtil.exceptionString(exception, null));
        }
        else {
            LOG.info("errId: " + errorId);
        }
    }
    
    public void setException(Throwable ex) {
        exception = ex;
   }

    public void setErrorId(String errorId) {
        this.errorId = errorId;
    }

    public String getErrorId() {
        return errorId;
    }
    
    public String getDescription() {
        if (exception != null) {
            return exception.getMessage();
        }
        return "[no description]";
    }

    public String getErrorType() {
        if (exception != null) {
            String classname =  exception.getClass().getName();
            String errorType = classname;
            int i = classname.lastIndexOf(".");
            if (i >= 0) {
                errorType = classname.substring(i + 1);
            }
            return errorType;
        }
        return "[unknown error type]";
    }

    public String getStackTrace() {
    	if (exception instanceof LogonException) {
    		LogonException e = (LogonException)exception;
    		if (e.getReason() == Reason.RES_ERROR_SECURITY_NOT_ALLOWED) {
    			return "\n" + e.getMessage();
    		}
    	}
        if (exception != null) {
            return ExceptionUtil.exceptionString(exception, null);
        }
        return "[no stack trace]";
    }

    public String getCause() {
        return "";
    }

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}
