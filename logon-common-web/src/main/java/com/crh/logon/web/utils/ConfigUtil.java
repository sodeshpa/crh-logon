/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.utils;

import java.net.InetAddress;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonProperties;

public class ConfigUtil {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ConfigUtil.class);

	private static final String SEPARATOR = " | ";

	private static final String applicationString = ConfigUtil.createApplicationString();

	/**
	 * 
	 * @return
	 * The application string which contains the following elements:	
	 * <b>instance | build version | db host | SID | application server</b>
	 * The db name and SID are only displayed in DEV and INT environments.
	 */
	public static String getApplicationString() {
		return applicationString;
	}

	private static String createApplicationString() {
		StringBuilder applicationString = new StringBuilder("");
		try {
			ConfigBean configBean = ConfigBeanProvider.getConfigBean();
			LogonProperties logonProperties = configBean.getLogonProperties();
			String instance = logonProperties.getInstance();

			applicationString.append(instance + SEPARATOR);
			applicationString.append(logonProperties.getBuildVersion() + SEPARATOR);

			String[] instanceParts = null;
			if (instance != null) {
				instanceParts = instance.split("\\.");
			}
			String environment = null;
			if (instanceParts != null && instanceParts.length > 1) {
				environment = instanceParts[1];
			}
			String dbConnectionUrl = configBean.getDbConnectionUrl();
			if ((logonProperties.getDevmode() || "int".equals(environment))
					&& dbConnectionUrl != null) {
				String[] elems = dbConnectionUrl.split(":");
				if (elems != null && elems.length > 2) {
					applicationString.append(elems[3].replace("@", "") + SEPARATOR);
					applicationString.append(elems[elems.length - 1] + SEPARATOR);
				}
			}
			else {
				LOG.info("The db connection is not displayed in the \"" + environment
						+ "\" environment.");
			}
			InetAddress localHost = InetAddress.getLocalHost();
			if (localHost != null) {
				applicationString.append(localHost.getHostName());
			}
		}
		catch (Exception e) {
			LOG.error("Error while constructing the application string.", e);
		}

		return applicationString.toString();
	}
}
