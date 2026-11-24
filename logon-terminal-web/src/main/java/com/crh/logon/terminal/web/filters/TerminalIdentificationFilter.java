/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.terminal.web.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.persistence.entities.common.PickupLoadingpointEntity;
import com.crh.logon.terminal.web.utils.LoadingpointProvider;
import com.crh.logon.utils.AuthUtils;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.web.utils.ServiceLocatorProvider;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * This filter ensures that we have the current terminal's LoadingpointEntity in the session.
 * 
 * @author AdNovum Informatik AG
 */
public class TerminalIdentificationFilter implements Filter {
	
	private static String loadingpointKey = TerminalIdentificationFilter.class.getName() + "loadingpoint";
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(TerminalIdentificationFilter.class);
	
	public static final String REQUEST_PARAMETER_LOADINGPOINT_IDENFITIER="loadingpoint";

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// [[
		Object[][] params = {{"request", request},
							{"response", response},
							{"chain", chain}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "doFilter", params);
		}
		// ]]
		try {
			
			HttpServletRequest req = (HttpServletRequest)request;
			HttpSession session = req.getSession();
			
			PickupLoadingpointEntity loadingpoint = (PickupLoadingpointEntity)session.getAttribute(loadingpointKey);
			String parameter = null;
			if( loadingpoint == null ) {	
				AuthUtils.setAuthorized(true);
				ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator();
				try{				
					parameter = request.getParameter(REQUEST_PARAMETER_LOADINGPOINT_IDENFITIER);
					
					LOG.debug("******************************************");
					LOG.debug("RECEIVED IDENTIFIER PARAMETER: " + parameter);
					LOG.debug("******************************************");
					if (parameter != null) {						
						loadingpoint = serviceLocator.getPickupService().getLoadingpointEntityForIdentifier(parameter);
						if (loadingpoint == null) {
							//if still null it could not be identified
							throw new LogonException("NO LOADINGPOINT COULD BE FOUND TO PARAMETER " + parameter, Reason.RES_ERROR_INIT);
						}
						session.setAttribute(loadingpointKey, loadingpoint);
					}
					else{
						throw new LogonException("NO PARAMETER RECEIVED FROM WHICH LOADINGPOINT COULD HAVE BEEN DETERMINED", Reason.RES_ERROR_INIT);
					}
				}
				catch (Exception e) {
					LOG.error("Could not determine the loadingpoint for request paramter " + parameter);
					throw new LogonException("loadingpoint == null", Reason.RES_ERROR_INIT);
				}	
			}
			LoadingpointProvider.setPickupLoadingpoint(loadingpoint);
			chain.doFilter(request, response);
		}
		finally {
			LoadingpointProvider.setPickupLoadingpoint(null);
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	public void destroy() {
		
	}

	public void init(FilterConfig arg0) throws ServletException {
		LOG.info(this.getClass().getName() + " initialized!");
	}



}
