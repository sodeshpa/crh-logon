/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.init.web.listeners;

import java.io.FileNotFoundException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.util.Log4jConfigurer;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 *
 * This Listener initializes log4j in the constructor. wls9 loads
 * listeners before any servlets (even before servlets with a
 * load-on-startup priority, which is according to servlet spec 2.4,
 * (but NOT <= spec 2.3!)) and calls each listener's constructor in
 * arbitrary order.
 *
 * Therefore, all other listeners must postpone getting a logger to
 * the contextInitialized() call and MAY NOT get a logger when the static
 * part is initialized or inside the constructor.
 */
public class Log4jConfigurationListener implements ServletContextListener {
	 /* don't get a logger here! we postpone it on purpose.
	  * see class-level comment of Log4jConfigurationListener
	  */
    private static JcanLogger LOG = null; // NOPMD - OK by design
    private static final String EXT_CFG_PROP = "external_log4j_config";
    private static final long REFRESH_INTERVAL = 5000;
    
    public Log4jConfigurationListener() {
		String log4jConfigPath = System.getProperty(EXT_CFG_PROP);
		if (log4jConfigPath != null) {
			PropertyConfigurator.configure(log4jConfigPath);
		    try {
				Log4jConfigurer.initLogging(log4jConfigPath, REFRESH_INTERVAL);
			    LOG = JcanLoggerFactory.getLogger(Log4jConfigurationListener.class);
			    LOG.info("logj4 initialized from: " + log4jConfigPath);
			}
			catch (FileNotFoundException e) {
				System.out.println("system property '" + EXT_CFG_PROP + "' was set but");
				System.out.println("doesn't point to an existing file!");
				System.out.println("-> will use log4j.property provided within the application's WAR.");
				System.out.println("HINT: please double check if a file exists on:");
				System.out.println("      '" + log4jConfigPath+ "'");
				System.out.println("      and make sure that it is readable for the container process");
			}
		}
		else {
			System.out.println("system property '" + EXT_CFG_PROP + "' not set!");
			System.out.println("-> will use log4j.property provided within the application's WAR.");
			System.out.println("HINT: if you want to provide your own log4j-configuration,");
			System.out.println("      you need to define a system property '" + EXT_CFG_PROP + "'");
			System.out.println("      which points to a log4j configuration file.");
			/* nothing more to do here: log4j will do above described magic automatically */
		}
	}

	public void contextInitialized(ServletContextEvent arg0) {
	    LOG = JcanLoggerFactory.getLogger(Log4jConfigurationListener.class);
		LOG.info("log4j is up and ready!");
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		LOG.info("destroying '" + this.getClass() + "'");
		/* af20080311: no longer shutting down here because it can't
		 * be guaranteed that we're the last one using the logging.
		 */
		// Log4jConfigurer.shutdownLogging();
	}
}
