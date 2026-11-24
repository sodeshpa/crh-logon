/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.listeners;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.LogonProperties;
import com.crh.logon.web.utils.ConfigBeanProvider;
import com.crh.logon.web.utils.ServiceLocatorProvider;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.ConfigService;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.utils.ConfigBean;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 */
public class SpringBeanListener implements ServletContextListener {
    private static JcanLogger LOG = null; // NOPMD - OK by design
    public static final String SL_PROPERTIES = "properties";
    public static final String SL_SERVICELOCATOR = "ServiceLocator";
	
	public void contextInitialized(ServletContextEvent event) {
	    LOG = JcanLoggerFactory.getLogger(SpringBeanListener.class);
		// [[
		Object[][] params = {{"event", event}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "contextInitialized", params);
		}
		// ]]
		try {
			ServletContext servletContext = event.getServletContext();
			WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
			LogonProperties logonProperties = (LogonProperties) webApplicationContext.getBean(LogonProperties.class.getName());
			ServiceLocator serviceLocator = (ServiceLocator) webApplicationContext.getBean(SL_SERVICELOCATOR);
			LOG.info(SL_PROPERTIES + ": " + logonProperties);
			servletContext.setAttribute(SL_PROPERTIES, logonProperties);
			servletContext.setAttribute(SL_SERVICELOCATOR, serviceLocator);
			ConfigService configService = serviceLocator.getConfigService();
			ConfigBean cb;
			/*
			 * initialize the config backend: this will call a service which
			 * then fetches the configuration from the database.
			 */
			if (!configService.isInitialized()) {
				cb = configService.initializeConfigBean(logonProperties);
				cb.log();
			}
			else {
				cb = configService.getConfigBean();
			}
			
			// make sure that we store the 'local' logonproperties into the ConfigBean
			cb.setLogonProperties(logonProperties);
			ConfigBeanProvider.setConfigBean(cb);
			ServiceLocatorProvider.setServiceLocator(serviceLocator);
		}
		catch (Throwable t) {
			ExceptionUtil.errException(LOG, t, params);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

    public static LogonProperties getLogonProperties(ServletContext servletContext) {
    	return (LogonProperties) servletContext.getAttribute(SL_PROPERTIES);
    }
    
    public static LogonProperties getLogonProperties(ServletRequest request) {
    	HttpServletRequest req = (HttpServletRequest)request;
    	return (LogonProperties) req.getSession().getServletContext().getAttribute(SL_PROPERTIES);
    }
    
    public static ServiceLocator getServiceLocator(ServletContext servletContext) {
    	return (ServiceLocator) servletContext.getAttribute(SL_SERVICELOCATOR);
    }
    
    public static ServiceLocator getServiceLocator(ServletRequest request) {
    	HttpServletRequest req = (HttpServletRequest)request;
    	return (ServiceLocator) req.getSession().getServletContext().getAttribute(SL_SERVICELOCATOR);
    }
    
	public void contextDestroyed(ServletContextEvent event) {
		LOG.info("destroying '" + this.getClass() + "'");
	}
}
