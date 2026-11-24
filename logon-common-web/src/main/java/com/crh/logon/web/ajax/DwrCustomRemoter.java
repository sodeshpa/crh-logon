package com.crh.logon.web.ajax;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;

import javax.ejb.EJBException;
import javax.servlet.http.HttpServletRequest;

import com.crh.logon.utils.LogonException;
import org.directwebremoting.AjaxFilter;
import org.directwebremoting.AjaxFilterChain;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.extend.AjaxFilterManager;
import org.directwebremoting.extend.Call;
import org.directwebremoting.extend.Creator;
import org.directwebremoting.extend.Remoter;
import org.directwebremoting.extend.Reply;
import org.directwebremoting.util.Continuation;
import org.directwebremoting.util.Logger;

import com.crh.logon.business.services.utils.Message;

/**
 * Slightly customized version of the DefaultRemoter class of DWR:
 * The execute() method is overridden and various exceptions are wrapped into a LogonBusinessException
 * 
 * Since the original execute() method of the DefaultRemoter is only very slightly adjusted, 
 * this Remoter implementation does not follow the general AdNovum coding conventions 
 * in order to make version upgrades and file comparing less painful.  
 * 
 * The source of the DefaultRemoter implementation can be found here: 
 * @see /share/install/globe/depend/dwr-2.0.3/java/org/directwebremoting/impl/DefaultRemoter.java 
 */
public class DwrCustomRemoter extends org.directwebremoting.impl.DefaultRemoter implements Remoter
{
	
	private LogonException getLogonException(Throwable t) {
		
		LogonException logonBusinessEx;
		
		if (t instanceof LogonException){
        	logonBusinessEx = (LogonException)t;
        }
        else if (t.getCause() instanceof LogonException){
        	logonBusinessEx = (LogonException)t.getCause();
        }
		else if (t.getCause() instanceof EJBException) {
			final EJBException ejbException = (EJBException) t.getCause();
			if (ejbException.getCause() instanceof LogonException) {
				logonBusinessEx = (LogonException) ejbException.getCause();
			}
			else {
				logonBusinessEx = new LogonException("Fatal Error (Ajax)", t);
			}
		}
        else {
        	logonBusinessEx = new LogonException("Fatal Error (Ajax)", t);
        }
		if (logonBusinessEx.getErrorDesc() == null) {
			WebContext webcx = WebContextFactory.get();
			if (webcx != null) {
				HttpServletRequest request = webcx.getHttpServletRequest();
				if (request != null) {
					String errorDesc = null;
					if (Message.messageExists(request.getLocale(), logonBusinessEx.getReason().getReason())) {
						errorDesc = Message.lookup(request.getLocale(), logonBusinessEx.getReason().getReason());
					}
					else {
						errorDesc = Message.lookup(request.getLocale(), "fatalerror.unknownerror");
					}
					logonBusinessEx.setErrorDesc(errorDesc);				
				}
			}
		}
		
		return logonBusinessEx;
	}
	
	
    /**
     * Execute a single call object
     * @param call The call to execute
     * @return A Reply to the Call
     */
    public Reply execute(Call call)
    {
        try
        {
            Method method = call.getMethod();
            if (method == null || call.getException() != null)
            {
                return new Reply(call.getCallId(), null, getLogonException(call.getException()));
            }

            // Get a list of the available matching methods with the coerced
            // parameters that we will use to call it if we choose to use that
            // method.
            Creator creator = creatorManager.getCreator(call.getScriptName());

            // We don't need to check accessControl.getReasonToNotExecute()
            // because the checks are made by the doExec method, but we do check
            // if we can display it
            accessControl.assertExecutionIsPossible(creator, call.getScriptName(), method);

            // Get ourselves an object to execute a method on unless the
            // method is static
            Object object = null;
            String scope = creator.getScope();
            boolean create = false;

            if (!Modifier.isStatic(method.getModifiers()))
            {
                WebContext webcx = WebContextFactory.get();

                // Check the various scopes to see if it is there
                if (scope.equals(Creator.APPLICATION))
                {
                    object = webcx.getServletContext().getAttribute(call.getScriptName());
                }
                else if (scope.equals(Creator.SESSION))
                {
                    object = webcx.getSession().getAttribute(call.getScriptName());
                }
                else if (scope.equals(Creator.SCRIPT))
                {
                    object = webcx.getScriptSession().getAttribute(call.getScriptName());
                }
                else if (scope.equals(Creator.REQUEST))
                {
                    object = webcx.getHttpServletRequest().getAttribute(call.getScriptName());
                }
                // Creator.PAGE scope means we create one every time anyway

                // If we don't have an object the call the creator
                if (object == null)
                {
                    create = true;
                    object = creator.getInstance();
                }

                // Remember it for next time
                if (create)
                {
                    if (scope.equals(Creator.APPLICATION))
                    {
                        // This might also be done at application startup by
                        // DefaultCreatorManager.addCreator(String, Creator)
                        webcx.getServletContext().setAttribute(call.getScriptName(), object);
                    }
                    else if (scope.equals(Creator.SESSION))
                    {
                        webcx.getSession().setAttribute(call.getScriptName(), object);
                    }
                    else if (scope.equals(Creator.SCRIPT))
                    {
                        webcx.getScriptSession().setAttribute(call.getScriptName(), object);
                    }
                    else if (scope.equals(Creator.REQUEST))
                    {
                        webcx.getHttpServletRequest().setAttribute(call.getScriptName(), object);
                    }
                    // Creator.PAGE scope means we create one every time anyway
                }
            }

            // Some debug
            log.info("Exec: " + call.getScriptName() + "." + call.getMethodName() + "()");
            if (log.isDebugEnabled())
            {
                StringBuffer buffer = new StringBuffer();

                if (create)
                {
                    buffer.append("--Object created, ");
                    if (!scope.equals(Creator.PAGE))
                    {
                        buffer.append(" stored in ");
                        buffer.append(scope);
                    }
                    else
                    {
                        buffer.append(" not stored");
                    }
                }
                else
                {
                    buffer.append("--Object found in ");
                    buffer.append(scope);
                }
                buffer.append(". ");

                // It would be good to debug the params but it's not easy
                //buffer.append("Call params (");
                //for (int j = 0; j < inctx.getParameterCount(callNum); j++)
                //{
                //    if (j != 0)
                //    {
                //        buffer.append(", ");
                //    }
                //    InboundVariable param = inctx.getParameter(callNum, j);
                //    buffer.append(param.toString());
                //}
                //buffer.append(") ");

                buffer.append("id=");
                buffer.append(call.getCallId());

                log.debug(buffer.toString());
            }

            // Execute the filter chain method.toString()
            final Iterator it = ajaxFilterManager.getAjaxFilters(call.getScriptName());
            AjaxFilterChain chain = new AjaxFilterChain()
            {
                public Object doFilter(Object obj, Method meth, Object[] p) throws Exception
                {
                    AjaxFilter next = (AjaxFilter) it.next();
                    return next.doFilter(obj, meth, p, this);
                }
            };
            Object reply = chain.doFilter(object, method, call.getParameters());
            return new Reply(call.getCallId(), reply);
        }
        catch (InvocationTargetException ex)
        {
            // Allow Jetty RequestRetry exception to propogate to container
            Continuation.rethrowIfContinuation(ex);
            
            LogonException logonBusinessEx = getLogonException(ex);
            
            log.error("Method execution failed: ", logonBusinessEx);
            return new Reply(call.getCallId(), null, logonBusinessEx);
        }
        catch (Exception ex)
        {
            // Allow Jetty RequestRetry exception to propogate to container
            Continuation.rethrowIfContinuation(ex);

            log.error("Method execution failed: ", ex);
            return new Reply(call.getCallId(), null, ex);
        }
    }

    /**
     * Accessor for the AjaxFilterManager
     * @param ajaxFilterManager The AjaxFilterManager to set.
     */
    public void setAjaxFilterManager(AjaxFilterManager ajaxFilterManager)
    {
        this.ajaxFilterManager = ajaxFilterManager;
    }
    
    /**
     * What AjaxFilters apply to which Ajax calls?
     */
    private AjaxFilterManager ajaxFilterManager = null;

    private static final Logger log = Logger.getLogger(DwrCustomRemoter.class);
    
}
