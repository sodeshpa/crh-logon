package com.crh.logon.simulator;

import junit.framework.Test;
import junit.framework.TestCase;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;


public class PdaLoginTest extends TestCase {
	
	private static JcanLogger LOG = JcanLoggerFactory.getLogger(PdaLoginTest.class);
	
	private static final String SOAPUI_TEST_XML = "etc/soapui/cs-outgoing/project.xml";
	private static final String SOAPUI_TEST_SUITE = "MOPEventPort_TestSuite";
	
	public static Test suite() {
	      return new junit.framework.TestSuite(PdaLoginTest.class);
	   }

	   public void setUp() {
	   }

	   /*
	   public void tearDown() {
	   }

	   public void testAllTestCases() throws Exception {
		  // [[
		  boolean logDebugEnabled = LOG.isDebugEnabled();
		  if (logDebugEnabled) {
			  LOG.entering(this, "testAllTestCases");
		  }
		  // ]]
		  try {
		  	SoapUITestCaseRunner runner = new SoapUITestCaseRunner(); 
		  	runner.setProjectFile(SOAPUI_TEST_XML);
		  	runner.run(); 
		  }
		  finally {
		  // [[
			  if (logDebugEnabled) {
				  LOG.exiting();
			  }
		  // ]]
		  }
	   }*/

	   public void testLoginTestCase(){
		  // [[
		  boolean logDebugEnabled = LOG.isDebugEnabled();
		  if (logDebugEnabled) {
			  LOG.entering(this, "testLoginTestCase");
		  }
		  // ]]
		  try {
		  	SoapUITestUtility.runSoapUITest("login", SOAPUI_TEST_SUITE, SOAPUI_TEST_XML);
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
