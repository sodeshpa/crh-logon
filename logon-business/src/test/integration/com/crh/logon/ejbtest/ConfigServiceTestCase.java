/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.ejbtest;

import java.util.Map;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.ConfigService;

/**
 * @author AdNovum Informatik AG
 */
public class ConfigServiceTestCase extends JunitSupport {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(
			ConfigServiceTestCase.class.getName());
	
	public ConfigServiceTestCase(String testCaseName) {
		super(testCaseName);
	}
	
	/**
	 * Tests the sample service
	 */
	@SuppressWarnings("unchecked")
	public void testFetchAllConfigs() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testFetchAllConfigs");
		}
		// ]]
		try {
			ConfigService configService = serviceLocator.getConfigService();
			Map<String, String> t;
			t = configService.fetchAllConfigs(null);
			printMap(t);
			t = configService.fetchAllConfigs("dev");
			printMap(t);
			t = configService.fetchAllConfigs("this does not exists, i hope");
			printMap(t);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	private void printMap(Map<String, String>  map) {
		for (Map.Entry<String, String> entry : map.entrySet()) {
			LOG.info(entry.getKey() + ": " + entry.getValue());
		}
		LOG.info("---------------------");
	}
}






