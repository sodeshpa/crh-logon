/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.utils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.handlers.GeneralExceptionHandler;
import com.crh.logon.web.validators.ExtJsInvalidFieldMark;
import com.crh.logon.web.validators.ValidatorMessage;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.MessageResourcesFactory;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import ch.nevis.jcan.log.AdnTrace;

/**
 * This class provides helper functions which deal with messages such 
 * as success messages and error messages. The idea is that 'Forms' and
 * 'Actions' use the same mechanisms when informing the user.
 * 
 * @author AdNovum Informatik AG
 */
public class MessageUtils {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(MessageUtils.class);
	
	public static final String WARNINGS_KEY = "org.apache.struts.action.ACTION_WARNINGS";
	public static final String EXCEPTION_MSG_KEY = "org.apache.struts.action.ACTION_EXCEPTION";
	public static final String ACTION_NAME = "actionName";
	public static final String FORM_NAME = "formName";

    /**
     * Adds an error element in the ActionError.
     * 
     * @param request HttpServletRequest request passed to struts action.
     * @param message a ValidatorMessage object which contains all information about one error
     * 
     * @adnlogger.notrace
     */
    public static ActionErrors addActionError(HttpServletRequest request, ValidatorMessage message) {
        ActionErrors errors = getActionErrors(request);

        if (errors == null) {
            errors = new ActionErrors();
        }
        
        if (message == null) {
            return errors;
        }
        
        Object[] additionalInfos = null;
        List additionalInfoList = message.getAdditionalInfoList();
        if (!additionalInfoList.isEmpty()) {
            additionalInfos = additionalInfoList.toArray();
        }
        
        errors.add(ActionMessages.GLOBAL_MESSAGE,
                new ActionMessage(message.getResourceKey(), additionalInfos));
        
        saveErrors(request, errors);
        return errors;
    }
    
    /**
     * adds an error element in the ActionError.
     * 
     * @param request HttpServletRequest request passed to struts action.
     * @param keys a List of String objects with resource keys describing the error
     * 
     * @adnlogger.notrace
     */
    public static ActionErrors addActionError(HttpServletRequest request, List keys) {
        if (keys == null) {
            return null;
        }
        for (Iterator iter = keys.iterator(); iter.hasNext();) {
            String resourceKey = (String) iter.next();
            addActionError(request, null, resourceKey, null, null);
        }
        /*
         * a shortcut to simplify above code: we could have called 
         *     errors = MessageUtils.addActionError(...)
         * every time to collect all errors, but since errors all end up in
         * a List inside the request, we just fetch it from there.
         */
        return (ActionErrors) request.getAttribute(Globals.ERROR_KEY);
    }
    
    /**
     * adds an error element in the ActionError.
     * 
     * @param request HttpServletRequest request passed to struts action.
     * @param titleKey LitDict key that should be used as error text.
     * 
     * @adnlogger.notrace
     */
    public static ActionErrors addActionError(HttpServletRequest request, String titleKey) {
        return addActionError(request, null, titleKey, null, null);
    }
    
    /**
     * adds an error element in the ActionError.
     * 
     * @param request HttpServletRequest request passed to struts action.
     * @param titleKey LitDict key that should be used as error text.
     * @param additionalInfo allows to provide additional Info
     * 
     * @adnlogger.notrace
     */
    public static ActionErrors addActionError(HttpServletRequest request,
            String titleKey, String additionalInfo) {
        return addActionError(request, null, titleKey, additionalInfo, null);
    }

    /**
     * adds an error element in the ActionError.
     * 
     * @param request HttpServletRequest request passed to struts action.
     * @param titleKey LitDict key that should be used as error text.
     * @param additionalInfo1 allows to provide additional Info
     * @param additionalInfo2 allows to provide even more additional Info
     * 
     * @adnlogger.notrace
     */
    public static ActionErrors addActionError(HttpServletRequest request,
            String titleKey, String additionalInfo1, String additionalInfo2) {
        return addActionError(request, null, titleKey, additionalInfo1, additionalInfo2);
    }

    /**
     * adds an error element in the ActionError.
     * 
     * @param request HttpServletRequest request passed to struts action.
     * @param ex the exception, if the error was caused by one
     * @param titleKey LitDict key that should be used as error text.
     * @param additionalInfo1 allows to provide additional Info
     * 
     * @adnlogger.notrace
     */
    public static ActionErrors addActionError(HttpServletRequest request, Throwable ex,
            String titleKey, String additionalInfo1) {
        return addActionError(request, ex, titleKey, additionalInfo1, null);
    }
    
    /**
     * adds an error element in the ActionError.
     * 
     * @param request HttpServletRequest request passed to struts action.
     * @param ex the exception, if the error was caused by one
     * @param titleKey LitDict key that should be used as error text.

     * @adnlogger.notrace
     */
    public static ActionErrors addActionError(HttpServletRequest request, Throwable ex,
            String titleKey) {
        return addActionError(request, ex, titleKey, null, null);
    }

    public static ActionErrors addActionError(HttpServletRequest request, Throwable ex) {
        return addActionError(request, ex, null, null, null);
    }

    /**
     * Creates a struts <code>ActionErrors</code> object for use by the struts
     * html:errors tag, and attaches it to the request. The parameter key is
     * looked up in the message resource file and that text is appended via a
     * replacement parameter to the message.
     *
     * @param request HttpServletRequest request passed to struts action.
     * @param ex the exception, if the error was caused by one
     * @param titleKey LitDict key that should be used as error text.
     * @param additionalInfo1 allows to provide additional Info
     * @param additionalInfo2 allows to provide even more additional Info
     */
    protected static ActionErrors addActionError(HttpServletRequest request, Throwable ex,
            String titleKey, String additionalInfo1, String additionalInfo2) {
		// [[
		ActionErrors result = null;
		Object[][] params = {{"request", request},
							{"ex", ex},
							{"titleKey", titleKey},
							{"additionalInfo1", additionalInfo1},
							{"additionalInfo2", additionalInfo2}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(MessageUtils.class, "addActionError", params);
		}
		// ]]
		try {
	        ActionErrors errors = getActionErrors(request);
			
	        if (errors == null) {
	            errors = new ActionErrors();
	        }
        
	        if (titleKey == null) {
	        	titleKey = "error.unspecified";
	        }
        
	        GeneralExceptionHandler.prepareErrorForm(request, ex);
        
	        if (additionalInfo1 != null && additionalInfo2 != null) {
	            errors.add(ActionMessages.GLOBAL_MESSAGE,
	                    new ActionMessage(titleKey, additionalInfo1, additionalInfo2));
	        }
	        else if (additionalInfo1 != null) {
	            errors.add(ActionMessages.GLOBAL_MESSAGE,
	                    new ActionMessage(titleKey, additionalInfo1));
	        }
	        else {
	            errors.add(ActionMessages.GLOBAL_MESSAGE,
	                    new ActionMessage(titleKey));
	        }
			
	        saveErrors(request, errors);
	        return result = errors;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }

    @AdnTrace
	protected static ActionErrors addActionError(HttpServletRequest request, ActionMessage message) {
			ActionErrors errors = getActionErrors(request);
			if (errors == null) {
				errors = new ActionErrors();
			}
		GeneralExceptionHandler.prepareErrorForm(request, null);

		errors.add(ActionMessages.GLOBAL_MESSAGE,
						message);
			saveErrors(request, errors);
			return errors;
	}


	/**
     * Stores an error element in the ActionError.
     *
     * @param request HttpServletRequest request passed to struts action.
     * @param titleKey LitDict key that should be used as error text.
     * 
     * @adnlogger.notrace
     */
    public static ActionMessages addActionMessage(HttpServletRequest request, String titleKey) {
        return addActionMessage(request, titleKey, null);
    }

    /**
     * Stores an error element in the ActionError.
     *
     * @param request HttpServletRequest request passed to struts action.
     * @param titleKey LitDict key that should be used as error text.
     * @param additionalInfo allows to provide additional Info
     * 
     * @adnlogger.notrace
     */
    public static ActionMessages addActionMessage(HttpServletRequest request,
            String titleKey, String additionalInfo) {
        return addActionMessage(request, titleKey, additionalInfo, null);
    }

    /**
     * Stores an error element in the ActionError.
     *
     * @param request HttpServletRequest request passed to struts action.
     * @param titleKey LitDict key that should be used as error text.
     * @param additionalInfo1 allows to provide additional Info
     * @param additionalInfo2 allows to provide even more additional Info
     * 
     * @adnlogger.notrace
     */
    public static ActionMessages addActionMessage(HttpServletRequest request,
            String titleKey, String additionalInfo1, String additionalInfo2) {
        return addActionMessage(request, titleKey, additionalInfo1, additionalInfo2, null);
    }

    /**
     * Stores an error element in the ActionError.
     *
     * @param request HttpServletRequest request passed to struts action.
     * @param titleKey LitDict key that should be used as error text.
     * @param additionalInfo1 allows to provide additional Info
     * @param additionalInfo2 allows to provide additional Info
     * @param additionalInfo3 allows to provide additional Info
     */
    public static ActionMessages addActionMessage(HttpServletRequest request,
            String titleKey, String additionalInfo1, String additionalInfo2, String additionalInfo3) {
		// [[
		ActionMessages result = null;
		Object[][] params = {{"request", request},
							{"titleKey", titleKey},
							{"additionalInfo1", additionalInfo1},
							{"additionalInfo2", additionalInfo2},
							{"additionalInfo3", additionalInfo3}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(MessageUtils.class, "addActionMessage", params);
		}
		// ]]
		try {
	        ActionMessages messages = getActionMessages(request);
			
	        if (messages == null) {
	            messages = new ActionMessages();
	        }
			
	        if (additionalInfo1 != null && additionalInfo2 != null && additionalInfo3 != null) {
	            messages.add(ActionMessages.GLOBAL_MESSAGE,
	                    new ActionMessage(titleKey, additionalInfo1, additionalInfo2, additionalInfo3));
	        }
	        else if (additionalInfo1 != null && additionalInfo2 != null) {
	            messages.add(ActionMessages.GLOBAL_MESSAGE,
	                    new ActionMessage(titleKey, additionalInfo1, additionalInfo2));
	        }
	        else if (additionalInfo1 != null) {
	            messages.add(ActionMessages.GLOBAL_MESSAGE,
	                    new ActionMessage(titleKey, additionalInfo1));
	        }
	        else {
	            messages.add(ActionMessages.GLOBAL_MESSAGE,
	                    new ActionMessage(titleKey));
	        }
	        
	        saveMessages(request, messages);
	        return result = messages;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }
    
    
    /**
     * Stores an error element in the ActionError.
     *
     * @param request HttpServletRequest request passed to struts action.
     * @param titleKey LitDict key that should be used as error text.
     * 
     * @adnlogger.notrace
     */
    public static ActionMessages addActionWarning(HttpServletRequest request, String titleKey) {
        return addActionWarning(request, titleKey, null);
    }

    /**
     * Stores an error element in the ActionError.
     *
     * @param request HttpServletRequest request passed to struts action.
     * @param titleKey LitDict key that should be used as error text.
     * @param additionalInfo allows to provide additional Info
     * 
     * @adnlogger.notrace
     */
    public static ActionMessages addActionWarning(HttpServletRequest request,
            String titleKey, String additionalInfo) {
        return addActionWarning(request, titleKey, additionalInfo, null);
    }

    /**
     * Stores an error element in the ActionError.
     *
     * @param request HttpServletRequest request passed to struts action.
     * @param titleKey LitDict key that should be used as error text.
     * @param additionalInfo1 allows to provide additional Info
     * @param additionalInfo2 allows to provide even more additional Info
     * 
     * @adnlogger.notrace
     */
    public static ActionMessages addActionWarning(HttpServletRequest request,
            String titleKey, String additionalInfo1, String additionalInfo2) {
        return addActionWarning(request, titleKey, additionalInfo1, additionalInfo2, null);
    }

    /**
     * Stores an error element in the ActionError.
     *
     * @param request HttpServletRequest request passed to struts action.
     * @param titleKey LitDict key that should be used as error text.
     * @param additionalInfo1 allows to provide additional Info
     * @param additionalInfo2 allows to provide additional Info
     * @param additionalInfo3 allows to provide additional Info
     */
    public static ActionMessages addActionWarning(HttpServletRequest request,
            String titleKey, String additionalInfo1, String additionalInfo2, String additionalInfo3) {
		// [[
		ActionMessages result = null;
		Object[][] params = {{"request", request},
							{"titleKey", titleKey},
							{"additionalInfo1", additionalInfo1},
							{"additionalInfo2", additionalInfo2},
							{"additionalInfo3", additionalInfo3}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(MessageUtils.class, "addActionWarning", params);
		}
		// ]]
		try {
			ActionMessages warnings = getActionWarnings(request);
			
			if (warnings == null) {
				warnings = new ActionMessages();
			}
			
			if (additionalInfo1 != null && additionalInfo2 != null && additionalInfo3 != null) {
				warnings.add(MessageUtils.WARNINGS_KEY,
						new ActionMessage(titleKey, additionalInfo1, additionalInfo2, additionalInfo3));
			}
			else if (additionalInfo1 != null && additionalInfo2 != null) {
				warnings.add(MessageUtils.WARNINGS_KEY,
						new ActionMessage(titleKey, additionalInfo1, additionalInfo2));
			}
			else if (additionalInfo1 != null) {
				warnings.add(MessageUtils.WARNINGS_KEY,
						new ActionMessage(titleKey, additionalInfo1));
			}
			else {
				warnings.add(MessageUtils.WARNINGS_KEY,
						new ActionMessage(titleKey));
			}
				
			saveWarnings(request, warnings);
			return result = warnings;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }
    
    public static void setActionException(HttpServletRequest request, Throwable ex, String errorId) {
		// [[
		Object[][] params = {{"request", request},
							{"ex", ex},
							{"errorId", errorId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(MessageUtils.class, "setActionException", params);
		}
		// ]]
		try {
			String errorDesc = null;
			if (ex instanceof LogonException) {
				LogonException logonException = (LogonException)ex;
				errorDesc = logonException.getErrorDesc();
				if (errorDesc == null) {
					if (MessageUtils.messageExists(request, request.getLocale(), logonException.getReason().getReason())) {
						errorDesc = MessageUtils.getMessage(request, request.getLocale(), logonException.getReason().getReason());
					}
				}
				if (errorId == null) {
					errorId = logonException.getId();
				}
			}
			
			if (errorDesc == null) {
				errorDesc = MessageUtils.getMessage(request, request.getLocale(), "fatalerror.unknownerror");
			}
			if (errorId == null) {
				errorId = StringUtils.generateErrorId();
				LOG.error("The following errorId is generated for a non-LogonException: " + errorId);
			}
			ActionMessages exceptions = new ActionMessages();
			exceptions.add(MessageUtils.EXCEPTION_MSG_KEY, new ActionMessage("fatalerror.errormessage", errorDesc, errorId));
			request.setAttribute(MessageUtils.EXCEPTION_MSG_KEY, exceptions);
			request.setAttribute(Globals.EXCEPTION_KEY, ex);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
    
    /**
     * Returns the action errors currently stored in the request.
     *
     * @param request HttpServletRequest
     * @return ActionErrors
     */
    public static ActionErrors getActionErrors(HttpServletRequest request) {
        return (ActionErrors) request.getAttribute(Globals.ERROR_KEY);
    }

    /**
     * Returns the action messages currently stored in the request.
     *
     * @param request HttpServletRequest
     * @return ActionErrors
     */
    public static ActionMessages getActionMessages(HttpServletRequest request) {
        return (ActionMessages) request.getAttribute(Globals.MESSAGE_KEY);
    }
    
    /**
     * Returns the action messages currently stored in the request.
     *
     * @param request HttpServletRequest
     * @return ActionErrors
     */
    public static ActionMessages getActionWarnings(HttpServletRequest request) {
        return (ActionMessages) request.getAttribute(MessageUtils.WARNINGS_KEY);
    }
    
    /**
     * Returns the action messages currently stored in the request.
     *
     * @param request HttpServletRequest
     * @return ActionErrors
     */
    public static ActionMessages getActionExceptions(HttpServletRequest request) {
    	return (ActionMessages) request.getAttribute(Globals.EXCEPTION_KEY);
    }
    
    /**
     * this is basically from struts, but allows us to use the same 
     * mechanism for adding messages (and errors):
     * 
     * traditional:
     *   - validate in form returns errors which is then stored in request by the requestprocessor
     *   - actions would store messages with saveMassages() explicitly.
     */
    protected static void saveErrors(HttpServletRequest request, ActionErrors errors) {
		// [[
		Object[][] params = {{"request", request},
							{"errors", errors}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(MessageUtils.class, "saveErrors", params);
		}
		// ]]
		try {
	        // Remove any error messages attribute if none are required
	        if ((errors == null) || errors.isEmpty()) {
	            request.removeAttribute(Globals.ERROR_KEY);
	            return;
	        }
	        // Save the error messages we need
	        request.setAttribute(Globals.ERROR_KEY, errors);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
    
    /**
     * this is basically from struts, but allows us to use the same 
     * mechanism for adding messages (and errors):
     * 
     * traditional:
     *   - validate in form returns errors which is then stored in request by the requestprocessor
     *   - actions would store messages with saveMassages() explicitly.
     */
    protected static void saveMessages(HttpServletRequest request, ActionMessages messages) {
		// [[
		Object[][] params = {{"request", request},
							{"messages", messages}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(MessageUtils.class, "saveMessages", params);
		}
		// ]]
		try {
	        // Remove any messages attribute if none are required
	        if ((messages == null) || messages.isEmpty()) {
	            request.removeAttribute(Globals.MESSAGE_KEY);
	            return;
	        }
	        // Save the messages we need
	        request.setAttribute(Globals.MESSAGE_KEY, messages);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
    
    /**
     * this is basically from struts, but allows us to use the same 
     * mechanism for adding warnings (and errors):
     * 
     * traditional:
     *   - validate in form returns errors which is then stored in request by the requestprocessor
     *   - actions would store messages with saveMassages() explicitly.
     */
    protected static void saveWarnings(HttpServletRequest request, ActionMessages warnings) {
		// [[
		Object[][] params = {{"request", request},
							{"warnings", warnings}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(MessageUtils.class, "saveWarnings", params);
		}
		// ]]
		try {
			// Remove any messages attribute if none are required
			if ((warnings == null) || warnings.isEmpty()) {
				request.removeAttribute(MessageUtils.WARNINGS_KEY);
				return;
			}
			// Save the messages we need
			request.setAttribute(MessageUtils.WARNINGS_KEY, warnings);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }

	
    /**
     * adds an error element in the ActionError.
     * 
     * @param request HttpServletRequest request passed to struts action.
     * @param keys a List of String objects with resource keys describing the error
     * 
     * @adnlogger.notrace
     */
	/*
    public static void addInvalidMarkForHtmlInputElem(HttpServletRequest request, String titleKey, String inputId) {
    	ExtJsInvalidFieldMark mark = new ExtJsInvalidFieldMark(titleKey, inputId, false); 
    	getExtJsInvalidMarks(request).add(mark);
    }
    
    public static void addInvalidMarkForExtJsFormElem(HttpServletRequest request, String titleKey, String variableName) {
    	ExtJsInvalidFieldMark mark = new ExtJsInvalidFieldMark(titleKey, variableName, true); 
    	getExtJsInvalidMarks(request).add(mark);
    }
    */
    
    public static void addExtJsInvalidFieldMark(HttpServletRequest request, String titleKey, String fieldId, String type, boolean isResize) {
    	addActionWarning(request, titleKey);
    	ExtJsInvalidFieldMark mark = new ExtJsInvalidFieldMark(titleKey, fieldId, type, isResize);
    	getExtJsInvalidMarks(request).add(mark);
    }
    
    public static void addExtJsInvalidFieldMarkSide(HttpServletRequest request, String titleKey, String fieldId, boolean isResize) {
    	addExtJsInvalidFieldMark(request, titleKey, fieldId, "side", isResize);
    }
    
    public static void addExtJsInvalidFieldMarkQtip(HttpServletRequest request, String titleKey, String fieldId) {
    	addExtJsInvalidFieldMark(request, titleKey, fieldId, "qtip", false);
    }
    
    private static List getExtJsInvalidMarks(HttpServletRequest request) {
    	Object marks = request.getAttribute("EXTJS_INVALID_MARKS");
    	if (marks == null) {
    		marks = new ArrayList();
    		request.setAttribute("EXTJS_INVALID_MARKS", marks);    		
    	}
    	return (List)marks;
    }
        
    public static String getMessage(HttpServletRequest request, String key) {
    	
    	MessageResources messageResources = ((MessageResources)request.getAttribute(Globals.MESSAGES_KEY));
    	
    	return messageResources.getMessage(request.getLocale(), key);
    }
    
    public static String getMessage(HttpServletRequest request, Locale locale, String key) {
    	
    	MessageResources messageResources = ((MessageResources)request.getAttribute(Globals.MESSAGES_KEY));
    	
    	return messageResources.getMessage(locale, key);
    }
    
    public static String getMessage(HttpServletRequest request, Locale locale, String key, Object[] args) {
    	
    	MessageResources messageResources = ((MessageResources)request.getAttribute(Globals.MESSAGES_KEY));
    	
    	return messageResources.getMessage(locale, key, args);
    }
    
    public static boolean messageExists(HttpServletRequest request, Locale locale, String key) {
    	
    	MessageResources messageResources = ((MessageResources)request.getAttribute(Globals.MESSAGES_KEY));
    	MessageResourcesFactory factory = messageResources.getFactory();
    	MessageResources clone = factory.createResources(messageResources.getConfig());
    	clone.setReturnNull(true);
    	
    	return (clone.getMessage(locale, key) != null);
    }
    
}
