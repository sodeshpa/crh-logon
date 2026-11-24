/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.ejbtest;

import com.crh.logon.utils.StringUtils;
import junit.framework.TestCase;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.ServiceLocatorTest;

/**
 * @author AdNovum Informatik AG
 */
public class JunitSupport extends TestCase {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(
			JunitSupport.class.getName());

	protected ServiceLocatorTest serviceLocator;
	
	public JunitSupport(String testCaseName) {
		super(testCaseName);
		init();
	}
	
	public void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			String containerserver = System.getProperty("containerserver");
			String remoteport = System.getProperty("remoteport");
			if (StringUtils.empty(containerserver)) {
				containerserver = "localhost";
				LOG.debug("Parameter containerserver is missing.");
			}
			serviceLocator = new ServiceLocatorTest("remote://" + containerserver + ":" + remoteport);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
}
