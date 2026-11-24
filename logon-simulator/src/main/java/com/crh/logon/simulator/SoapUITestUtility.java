/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.simulator;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.apache.xmlbeans.XmlException;

import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.model.support.PropertiesMap;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestRunner;
import com.eviware.soapui.model.testsuite.TestSuite;
import com.eviware.soapui.model.testsuite.TestRunner.Status;
import com.eviware.soapui.support.SoapUIException;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public abstract class SoapUITestUtility {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SoapUITestUtility.class);

   protected static void runSoapUITest(String testcase, String suitename, String testxml) {
		// [[
		Object[][] params = {{"testcase", testcase},
							{"suitename", suitename},
							{"testxml", testxml}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SoapUITestUtility.class, "runSoapUITest", params);
		}
		// ]]
		try {
			WsdlProject project = new WsdlProject(testxml);
			
			List testSuiteList = project.getTestSuiteList();
			LOG.error("List of all TestSuites");
			for (Iterator iterator = testSuiteList.iterator(); iterator.hasNext();) {
				TestSuite object = (TestSuite) iterator.next();
				LOG.error(object.getName());
			}
			
			TestSuite testSuite = project.getTestSuiteByName("MOPEventPort_TestSuite");
			List testCaseList = testSuite.getTestCaseList();
			LOG.error("List of all TestCases in MOPEventPort_TestSuite");
			for (Iterator iterator = testCaseList.iterator(); iterator.hasNext();) {
				TestCase object = (TestCase) iterator.next();
				LOG.debug(object.getName());
			}
			
			TestCase testCase = testSuite.getTestCaseByName(testcase);
			
			// create empty properties and run synchronously
			TestRunner runner = testCase.run(new PropertiesMap(), false);
			
			Assert.assertEquals(Status.FINISHED, runner.getStatus());
		}
		catch (XmlException e) {
			LOG.error("exception caught: ", e);
			Assert.fail("Could not start soapui");
		}
		catch (IOException e) {
			LOG.error("exception caught: ", e);
			Assert.fail("Could not start soapui");
		}
		catch (SoapUIException e) {
			LOG.error("exception caught: ", e);
			Assert.fail("Could not start soapui");
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
