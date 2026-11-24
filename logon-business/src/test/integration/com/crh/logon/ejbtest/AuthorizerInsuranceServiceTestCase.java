/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.ejbtest;

import java.util.Iterator;
import java.util.List;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class AuthorizerInsuranceServiceTestCase extends JunitSupport {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(
			AuthorizerInsuranceServiceTestCase.class.getName());
	
	public AuthorizerInsuranceServiceTestCase(String testCaseName) {
		super(testCaseName);
	}
	
	/**
	 * Tests the sample service
	 */
	public void testAuthorizerInsuranceService() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testAuthorizerInsuranceService");
		}
		// ]]
		try {
			List res = serviceLocator.getAuthorizerInsuranceService().test();
			for (Iterator iterator = res.iterator(); iterator.hasNext();) {
				String msg = (String) iterator.next();
				LOG.info(msg);
			}
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






