/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.security.utils;

import java.io.FileInputStream;
import java.util.Properties;

import javax.security.auth.login.LoginException;

import org.apache.commons.lang.SystemUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class LogonProperties {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LogonProperties.class);

	/**
	 * Load the 'instance' property value form the 'logon.properties'.
	 * @return
	 * @throws LoginException
	 */
    public static String getInstance() throws LoginException {
		// [[
		String result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonProperties.class, "getInstance");
		}
		// ]]
		try {
			Properties prop = new Properties();
			try {
				prop.load(new FileInputStream("etc/logon.properties"));
			}
			catch (Exception e) {
				String errorMsg = "Couldn't load 'logon.properties'";
				LOG.error(errorMsg, e);
				throw new LoginException(errorMsg);
			}
			String instanceFormat = (String)prop.get("instance");
			final String instance = String.format(instanceFormat, SystemUtils.USER_NAME);		
			LOG.info("getInstance(): '" + instance + "'");
			
			return result = instance;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
}
