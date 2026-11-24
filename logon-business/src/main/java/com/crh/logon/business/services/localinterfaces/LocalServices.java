/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.business.services.localinterfaces;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.utils.AuthUtils;
import com.crh.logon.utils.TransferContext;
import com.crh.logon.utils.ConfigBean;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import ch.nevis.jcan.optrace.common.request.RequestContext;
import ch.nevis.jcan.optrace.log.JcanOptraceLogger;
import ch.nevis.jcan.optrace.log.JcanOptraceLoggerFactory;

public class LocalServices {
    // java:app/logon-business/DispoServiceBean!DispoServiceLocal
    // java:global/logon/logon-business/DispoServiceBean!DispoServiceLocal
	private static final String LOGON_APP_DISPO_SERVICE_BEAN_LOCAL = "DispoServiceBean!com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface";
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LocalServices.class);
	public static final String APPLICATION_NAME = "java:app/logon-business";
	private static final String REQUEST_HANDLER_INTERFACE = "Handler!com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface";
	
	/**
	 * Provide a local interface to DispoService EJB
	 * @return
	 * @throws NamingException
	 */
	public static DispoServiceLocalInterface getDispoService(ConfigBean configBean) throws NamingException {
		// [[
		DispoServiceLocalInterface result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LocalServices.class, "getDispoService");
		}
		// ]]
		try {
			InitialContext initContext;
			
			try {
				initContext = new InitialContext();				

				DispoServiceLocalInterface dispoServiceLocal = (DispoServiceLocalInterface)initContext.lookup(APPLICATION_NAME + "/" + LOGON_APP_DISPO_SERVICE_BEAN_LOCAL);
				
				return result = dispoServiceLocal;			
			} 
			catch (NamingException e) {
				throw e;
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

	public static RequestHandlerLocalInterface getLocalInterfaceForRequestHandler(String classNameOfStatelessSessionBean) throws NamingException {		
			InitialContext initContext;
			
			try {
				initContext = new InitialContext();				

				return (RequestHandlerLocalInterface) initContext.lookup(APPLICATION_NAME + "/" + classNameOfStatelessSessionBean + REQUEST_HANDLER_INTERFACE);			
			} 
			catch (NamingException e) {
				throw e;
			}			   				
	}

	public static RequestHandlerLocalInterface getLocalInterfaceFor(String classNameOfStatelessSessionBean) throws NamingException {		
		InitialContext initContext;
		
		try {
			initContext = new InitialContext();				

			return (RequestHandlerLocalInterface) initContext.lookup(APPLICATION_NAME + "/" + classNameOfStatelessSessionBean + REQUEST_HANDLER_INTERFACE);			
		} 
		catch (NamingException e) {
			throw e;
		}			   				
	}
	
	public static Object getLocalInterfaceFor(String classNameOfStatelessSessionBean, String localInterfaceName) throws NamingException {		
		InitialContext initContext;
		
		try {
			initContext = new InitialContext();				

			return initContext.lookup(APPLICATION_NAME + "/" + classNameOfStatelessSessionBean + "!" + localInterfaceName);			
		} 
		catch (NamingException e) {
			throw e;
		}			   				
	}
	
	/**.
	 * Create a TransferContext for local calls
	 * @return
	 */
	public static TransferContext createLocalContext() {       
        return createContext(false);
    }

	/**.
	 * Create a TransferContext for remote calls 
	 * @return
	 */
	public static TransferContext createRemoteContext() {       
        return createContext(true);
    }
	
	
    private static TransferContext createContext(boolean remotecall) {
        Long userId = AuthUtils.getUserId();
        JcanOptraceLogger op = JcanOptraceLoggerFactory.getLogger();
        RequestContext requestContext = op.getRequestContext();        
        String id = requestContext.getId();
        String principal = requestContext.getPrincipal();
        TransferContext ctx = new TransferContext(userId, principal, id, remotecall);
        
        return ctx;
    }

}
