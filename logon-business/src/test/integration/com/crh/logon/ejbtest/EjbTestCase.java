/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.ejbtest;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.DummyService;
import com.crh.logon.business.services.interfaces.TestService;
import com.crh.logon.utils.ExceptionUtil;

/**
 * @author AdNovum Informatik AG
 */
public class EjbTestCase extends JunitSupport {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(
			EjbTestCase.class.getName());
	
	public EjbTestCase(String arg0) {
		super(arg0);
	}
	
	/**
	 * Tests the sample service
	 */
	public void testSmoke() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testSmoke");
		}
		// ]]
		try {
			TestService testService = serviceLocator.getTestService();
			String result = testService.hello("hello"); 
			LOG.info("service returned: " + result);
		}
		catch (RuntimeException t) {
			ExceptionUtil.errException(LOG, t, null);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public void testMdb() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testMdb");
		}
		// ]]
		try {
			DummyService dummyService = serviceLocator.getDummyService();
			dummyService.hello("world!");
		}
		catch (RuntimeException t) {
			ExceptionUtil.errException(LOG, t, null);
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






