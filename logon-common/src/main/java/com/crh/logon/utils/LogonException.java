/*
 * Author            : AdNovum Informatik AG

 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.utils;

import javax.ejb.ApplicationException;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * An exception which can be used as a wrapper for rethrowing other exceptions,
 * such as might occur during data access, which should then be handled in the
 * web tier, resulting in an appropriate error message being displayed instead
 * of the fatal error page with the stack trace.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 */
@ApplicationException(rollback=true)
public class LogonException extends RuntimeException {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LogonException.class);
	
    private Reason reason;
    private String errorDesc;
    private final String id;
    private Object[][] params;
    
    public LogonException() {
        super();
    	id = generateErrorId();
        /* we should never throw a business exception without explanation */
        reason = Reason.RES_IMPL_BUG;
    }
    
    public LogonException(Reason reason) {
        super(reason.getReason());
    	id = generateErrorId();
        this.reason = reason;
    }

    public LogonException(Reason reason, Object[][] params) {
        super(reason.getReason());
    	id = generateErrorId();
        this.reason = reason;
        this.params = params;
    }

    public LogonException(Throwable cause, Reason reason) {
        super(reason.getReason(), cause);
    	id = getId(cause);
        this.reason = reason;
    }

    public LogonException(Throwable cause, Reason reason, Object[][] params) {
        super(reason.getReason(), cause);
        id = getId(cause);
        this.reason = reason;
        this.params = params;
    }

    public LogonException(String message, Reason reason) {
        super(message);
    	id = generateErrorId();
        this.reason = reason;
    }

    public LogonException(String message, Reason reason, Object[][] params) {
        super(message);
    	id = generateErrorId();
        this.reason = reason;
        this.params = params;
   }

    public LogonException(Throwable cause) {
        super(cause);
        id = getId(cause);
        if (cause instanceof LogonException) {
            LogonException c = (LogonException)cause;
            reason = c.getReason();
        }
        else {
        	/* we should never throw a business exception without explanation */
            reason = Reason.RES_IMPL_BUG;
        }
    }

    public LogonException(String message, Throwable cause) {
        super(message, cause);
        id = getId(cause);
        if (cause instanceof LogonException) {
            LogonException c = (LogonException)cause;
            reason = c.getReason();
        }
        else {
	    	/* we should never throw a business exception without explanation */
	        reason = Reason.RES_IMPL_BUG;
        }
    }

    public LogonException(String message, Throwable cause, Object[][] params) {
    	super(message, cause);
    	id = getId(cause);
    	if (cause instanceof LogonException) {
            LogonException c = (LogonException)cause;
            reason = c.getReason();
        }
        else {
	    	/* we should never throw a business exception without explanation */
	    	reason = Reason.RES_IMPL_BUG;
        }
    	this.params = params;
    }

    public static void whereAmI() {
    	final int maxdepth = 5;
    	try {
    		throw new Throwable();
    	}
    	catch (Throwable t) {
    		StackTraceElement[] stack = t.getStackTrace();
    		String msg = "";
    		for (int i = 0; i < ((stack.length < maxdepth) ? stack.length : maxdepth); i++) {
    			StackTraceElement element = stack[i];
    			String fn = element.getFileName();
    			if (fn == null) {
    				fn = "Unknown Source";
    			}
    			else {
    				fn += ":" + element.getLineNumber();
    			}
    			msg += element.getClassName() + "." + element.getMethodName() + "(" + fn + ") ";
    		}
    		LOG.info("at " + msg);
    	}
    }
    
    private String getId(Throwable cause) {
    	
    	if (cause instanceof LogonException) {
            LogonException c = (LogonException)cause;
            
            return c.getId();
        }
        
    	return generateErrorId();
    }

    private String generateErrorId() {
    	String tid = StringUtils.generateErrorId();
    	LOG.info("business exception created: " + tid);
    	whereAmI();
    	return tid;
    }

    public Reason getReason() {
        return reason;
    }

    public boolean isReason(Reason rsn) {
        return this.reason.getReason().equals(rsn.getReason());
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	public String getId() {
        return id;
    }

	public Object[][] getParams() {
		return params;
	}

	public void setParams(Object[][] params) {
		this.params = params;
	}
}
