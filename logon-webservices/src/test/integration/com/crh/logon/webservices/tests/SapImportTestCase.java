/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.tests;

import junit.framework.TestCase;

import com.crh.logon.business.services.interfaces.SapImportTestService;
import com.crh.logon.business.services.interfaces.TestService;
import com.crh.logon.business.services.utils.ServiceLocatorTest;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.StringUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class SapImportTestCase extends TestCase {

	private static final JcanLogger LOG = 
		JcanLoggerFactory.getLogger(SapImportTestCase.class);

	private static final String CRONSTATUS_BP = "sapimport_bp";
	private static final String CRONSTATUS_COND = "sapimport_cond";
	private static final String CRONSTATUS_MAT = "sapimport_mat";
	private static final String CRONSTATUS_VENDOR = "sapimport_vendor";
	private static final String CRONSTATUS_NETP = "sapimport_netp";
	private static final String PACKAGE_NAME_BP = "BP";
	private static final String PACKAGE_NAME_COND = "COND";
	private static final String PACKAGE_NAME_MAT = "MAT";
	private static final String PACKAGE_NAME_VENDOR = "VEN";
	private static final String PACKAGE_NAME_NETP = "NETP";	
	
	private ServiceLocatorTest serviceLocator;

	public SapImportTestCase(String arg0) {
		super(arg0);
		init();
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

	private void init() {
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
	
	/**
	* The validation to check if there were really 0 errors. 
	* Use the last_run_number to filter tlgnc_log for the relevant cases. 
	* If there is an ERROR => testcase should fail.
	**/
	public void testSapImport() {
		SapImportTestService  sapImportTestService = serviceLocator.getSapImportTestService();
		
		//Check BP
		long lastRunNumberBp = sapImportTestService.getLastRunNumber(CRONSTATUS_BP);
		if (lastRunNumberBp != -1) {
			long errorNumber = sapImportTestService.getErrorsNumber(PACKAGE_NAME_BP, lastRunNumberBp);
			LOG.info("Number of errors of BP import: " + errorNumber + " (lastRunNumber = " + lastRunNumberBp + ")");
			assertEquals(errorNumber, 0);
		}
		
		//Check pricing conditions
		long lastRunNumberCond = sapImportTestService.getLastRunNumber(CRONSTATUS_COND);
		if (lastRunNumberCond != -1) {
			long errorNumber = sapImportTestService.getErrorsNumber(PACKAGE_NAME_COND, lastRunNumberCond);
			LOG.info("Number of errors of pricing conditions import: " + errorNumber +" (lastRunNumber = " + lastRunNumberCond + ")");
			assertEquals(errorNumber, 0);
		}
		
		//Check materials
		long lastRunNumberMat = sapImportTestService.getLastRunNumber(CRONSTATUS_MAT);
		if (lastRunNumberMat != -1) {
			long errorNumber = sapImportTestService.getErrorsNumber(PACKAGE_NAME_MAT, lastRunNumberMat);
			LOG.info("Number of errors of materials import: " + errorNumber + " (lastRunNumber = " + lastRunNumberMat + ")");
			assertEquals(errorNumber, 0);
		}
		
		//Check vendors
		long lastRunNumberVen = sapImportTestService.getLastRunNumber(CRONSTATUS_VENDOR);
		if (lastRunNumberVen != -1) {
			long errorNumber = sapImportTestService.getErrorsNumber(PACKAGE_NAME_VENDOR, lastRunNumberVen);
			LOG.info("Number of errors of vendors import: " + errorNumber +  " (lastRunNumber = " + lastRunNumberVen + ")");
			assertEquals(errorNumber, 0);
		}

		//Check net prices
		long lastRunNumberNetp = sapImportTestService.getLastRunNumber(CRONSTATUS_NETP);
		if (lastRunNumberNetp != -1) {
			long errorNumber = sapImportTestService.getErrorsNumber(PACKAGE_NAME_NETP, lastRunNumberNetp);
			LOG.info("Number of errors of net prices import: " + errorNumber +  " (lastRunNumber = " + lastRunNumberNetp + ")");
			assertEquals(errorNumber, 0);
		}
	}

}
