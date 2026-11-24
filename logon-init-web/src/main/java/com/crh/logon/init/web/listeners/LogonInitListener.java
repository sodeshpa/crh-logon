/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.init.web.listeners;

import java.nio.charset.Charset;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.crh.logon.business.services.interfaces.ConfigService;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonProperties;
import com.crh.logon.web.listeners.SpringBeanListener;
import com.crh.logon.web.utils.ServiceLocatorProvider;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 *
 * This Listener initializes the config backed. this is the only container
 * independent method to initialize the ejb-tier. Another way would be
 * using JMX MBeans, but this ties us to a container.
 */
public class LogonInitListener implements ServletContextListener {
	 /* don't get a logger here! we postpone it on purpose.
	  * see class-level comment of Log4jConfigurationListener
	  */
    private static JcanLogger LOG = null; // NOPMD - OK by design

	public void contextInitialized(ServletContextEvent event) {
		LOG = JcanLoggerFactory.getLogger(LogonInitListener.class);
		// [[
		Object[][] params = {{"event", event}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "contextInitialized", params);
		}
		// ]]
		try {
			ServletContext servletContext = event.getServletContext();
			LogonProperties logonProperties = SpringBeanListener.getLogonProperties(servletContext);
			ServiceLocator serviceLocator = SpringBeanListener.getServiceLocator(servletContext);
			ConfigService configService = serviceLocator.getConfigService();
			ServiceLocatorProvider.setServiceLocator(serviceLocator);
			
			/*
			 * initialize the config backend: this will call a service which
			 * then fetches the configuration from the database.
			 */
			if (!configService.isInitialized()) {
				ConfigBean cb = configService.initializeConfigBean(logonProperties);
				cb.log();
			}
			
			if (logonProperties.getExecAuthorizeInsurance()) {
				serviceLocator.getAuthorizerInsuranceService().test();
			}
			
			if (logonProperties.getDevmode()) {
				LOG.info("************************************************");
				LOG.info("************************************************");
				LOG.info("********	   DEV MODE ENABLED!		********");
				LOG.info("************************************************");
				LOG.info("************************************************");
				LOG.info("_________logon de____________logon dev");
				LOG.info("______logon dev mode_______logon dev mode l");
				LOG.info("____logon dev mode logo___logon dev mode logo");
				LOG.info("___logon dev mode logon dev mode lo_______logo");
				LOG.info("__logon dev mode logon dev mode lo_________logo");
				LOG.info("_logon dev mode logon dev mode logon _______logo");
				LOG.info("_logon dev mode logon dev mode logon dev ______l");
				LOG.info("logon dev mode logon dev mode logon dev mode__log");
				LOG.info("logon dev mode logon dev mode logon dev mode lo_l");
				LOG.info("logon dev mode logon dev mode logon dev mode logo");
				LOG.info("logon dev mode logon dev mode logon dev mode logo");
				LOG.info("_logon dev mode logon dev mode logon dev mode lo");
				LOG.info("__logon dev mode logon dev mode logon dev mode ");
				LOG.info("____logon dev mode logon dev mode logon dev mo");
				LOG.info("______logon dev mode logon dev mode logon d");
				LOG.info("_________logon dev mode logon dev mode l");
				LOG.info("____________logon dev mode logon dev ");
				LOG.info("______________logon dev mode logon");
				LOG.info("_________________logon dev mod");
				LOG.info("___________________logon dev");
				LOG.info("_____________________logon ");
				LOG.info("______________________logo");
				LOG.info("_______________________lo");
				LOG.info("JVM default charset: " + Charset.defaultCharset().toString());
			}
			LOG.info("logon initialized!");
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		LOG.info("destroying '" + this.getClass() + "'");
	}
}
