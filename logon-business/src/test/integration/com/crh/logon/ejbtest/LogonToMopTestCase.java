/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.ejbtest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.crh.logon.eventbeans.LogonEventOutBean;
import com.crh.logon.eventbeans.LogonEventOutBeanFactory;
import com.crh.logon.eventbeans.LogonMessageOutBean;
import com.crh.logon.eventbeans.mop.ReadyForOperation;
import junit.framework.Assert;

import org.apache.xmlbeans.XmlException;
import org.junit.Test;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.model.support.PropertiesMap;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestRunner;
import com.eviware.soapui.model.testsuite.TestRunner.Status;
import com.eviware.soapui.model.testsuite.TestSuite;
import com.eviware.soapui.support.SoapUIException;
import com.crh.logon.business.services.interfaces.MopEventService;


/**
 * @author AdNovum Informatik AG
 */
public class LogonToMopTestCase extends JunitSupport {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(
			LogonToMopTestCase.class.getName());
	
	public LogonToMopTestCase(String testCaseName) {
		super(testCaseName);
	}
	
	/**
	 * runs the soapUI Test messageToLogon
	 * 
	 * a test to run a sopaUI Test Case programmaticaly
	 * DOES not WORK!! -> see runSoapUITest for more infos
	 */
	public void PtestSoapUI(){
		LogonToMopTestCase.runSoapUITest("messageToLogon", "PDA-initiated", "../logon-simulator/etc/soapui/integration-tests/integration-tests-soapui-project.xml");
	}
	
	/**
	 * runSoapUITest
	 * 
	 * this does not work yet!!
	 * 
	 * Exception: 
	 * java.lang.NoClassDefFoundError: groovy/lang/GroovyClassLoader
     * at com.eviware.soapui.support.scripting.groovy.GroovyScriptEngineFactory.createScriptEngine(GroovyScriptEngineFactory.java:36)
 	 * Caused by: java.lang.ClassNotFoundException: groovy.lang.GroovyClassLoader
 	 * 
 	 * See ongoing discussion in soapUI Forum:
 	 * http://www.eviware.com/forums/index.php?topic=2087.0
	 */
	protected static void runSoapUITest(String testcase, String suitename, String testxml) {
		try {
			WsdlProject project = new WsdlProject(testxml);
			TestSuite testSuite = project.getTestSuiteByName(suitename);
			TestCase testCase = testSuite.getTestCaseByName(testcase);

			// create empty properties and run synchronously
	         TestRunner runner = testCase.run(new PropertiesMap(), false);
	         Assert.assertEquals(Status.FINISHED, runner.getStatus());

		}
		catch (XmlException e) {
			LOG.error("Could not start soapui");
			Assert.fail("Could not start soapui");
		}
		catch (IOException e) {
			LOG.error("Could not start soapui");
			Assert.fail("Could not start soapui");
		}
		catch (SoapUIException e) {
			LOG.error("Could not start soapui");
			Assert.fail("Could not start soapui");
		}
	}

	/**
	 * Send a 'test' event to PDA
	 */
	public void testTest() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testTest");
		}
		// ]]
		try {
			final String eventType = "test";
			final String parameterName = "test";
			final String testMessage = "Hello World";
			LogonMessageOutBean response = null;
			
			HashMap logonData = new HashMap();
			logonData.put(parameterName, testMessage);
			
			LogonEventOutBean test = (LogonEventOutBean) LogonEventOutBeanFactory.create(eventType,
					logonData);
            test.setEventType(eventType);
			
			LogonMessageOutBean message = new LogonMessageOutBean();
			message.setSenderPDAId("123456789abc");
			message.setSendersTime(new Date());
			message.setSessionId("");
			List events = new ArrayList();
            events.add(test);
            message.setLogonEventBeans(events);
			MopEventService mopEventService = serviceLocator.getMopEventService();
			mopEventService.sendMessage(message);
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
	 * Send a 'test' event to PDA via Service Interface
	 * 
	 * 
	 * irgendwann mal fuer orderDATA Geschichte
	 *		OrderService orderService = serviceLocator.getOrderService();
	 *		orderService.transmitToDriver(;)
	 */
	/* TODO boon, 20100713: NB is failing, needs investigation
	public void testMessageViaService() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testMessageViaService");
		}
		// ]]
		try {
			final String message = "Hello World via junit-service";
			final long driverID = -12;
			DispoService dispoService = serviceLocator.getDispoService();
			dispoService.sendMessageToDriver(driverID, message);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}
	 */
	
	public void testReadyForOperationTest() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testReadyForOperationTest");
		}
		// ]]
		try {
			final String eventType = "readyForOperation";
			LogonMessageOutBean response = null;
			
			HashMap logonData = new HashMap();
			ReadyForOperation readyForOperation = (ReadyForOperation) LogonEventOutBeanFactory.create(
					eventType, logonData);
			LogonMessageOutBean message = new LogonMessageOutBean();
			List events = new ArrayList();
			events.add(readyForOperation);
			message.setLogonEventBeans(events);
			MopEventService mopEventService = serviceLocator.getMopEventService();
			mopEventService.sendMessage(message);
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
	 * resets test condition for adminRFID1 in soapUI.cs-outgoing.LogonMOPEventPort_Scenario
	 * 
	 * more specific: sets rfidobject_id to null in tlgnc_rfidtag where rfidtag_id = 1300
	 */
	@Test
	public void testResetRFID1() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testResetRFID1");
		}
		// ]]
		try {
			;
			/*
			final Long rfidId = new Long(1300);
			//final Long objectType = new Long(4);
			//final String objectLabel = "VS123456";
			
			RFIDService rfidService = serviceLocator.getRFIDService();
			RfidtagEntity rfidTag = rfidService.fetchRFIDTagById(rfidId);
			//RfidobjectEntity rfidobjectEntity = rfidService.findSingleRFID(objectLabel, objectType);
			
			rfidTag.setRfidobject(null);
			rfidService.mergeRFIDTag(rfidTag);
			*/
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
	 * resets test condition for adminRFID2 in soapUI.cs-outgoing.LogonMOPEventPort_Scenario
	 * 
	 * more specific: delets following rfidtag.  name: "sUITe2", rfid_identity: dd12ee34ff 
	 * 
	 * !! builds on new methods in RFIDService fetchRFIDTagByIdentity
	 */
	/*
	public void ResetRFID2() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testResetRFID2");
		}
		// ]]
		try {

			final String rfid_identity = "dd12ee34ff2";
			//final String rfid_name = "sUITe2";
			final Long objectType = new Long(4);
			final String objectLabel = "VS123456";

			RFIDService rfidService = serviceLocator.getRFIDService();
			RfidtagEntity rfidTag = rfidService.fetchRFIDTagByIdentity(rfid_identity);
			RfidobjectEntity rfidobjectEntity = rfidService.findSingleRFID(objectLabel, objectType);

			//rfidTag.setRfidobject(null);
			//rfidService.mergeRFIDTag(rfidTag);
			rfidService.removeRFIDTag(rfidTag);
			//--> java.lang.IllegalArgumentException: Removing a detached instance RfidtagEntity#1322

		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}
	*/
	/**
	 * 
	 * testOrderData
	 * 
	 * triggers the soapUI Test Case "orderDataWithParallel9013_Remote"
	 * 
	 * IMPORTANT: 
	 * The hard coded "truckID" must correspond with the one belonging to 
	 * "truckLabel" in logon-simulator/etc/soapUI/testdata.properties
	 * 
	 * the order belonging to the hard coded "orderNr" 
	 * can be created through the application. status must be "assigned to carrier"
	 * 
	 */
	/*
	public void testOrderData() {
		// [[
		final boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testOrderData");
		}
		// ]]
		try {
			LOG.info("* * *    t e s t O r d e r D a t a    * * *");
			final long orderNr = 10000101;
			final long truckID = 1427; //in DB Hu this is Zwissig79
			//final long truckID = 1700; //in DB Hu this is Zwissig14
			final int loadDuration = 30; //in min.
			final int transportDuration = 120; //in min.
			final int unloadDuration = 30; //in min
			Calendar rightNow = Calendar.getInstance();
			rightNow.set(Calendar.HOUR_OF_DAY, 9);
			Date startTime = rightNow.getTime();
			rightNow.set(Calendar.HOUR_OF_DAY, 17);
			Date endTime = rightNow.getTime();
			
			OrderService orderService = serviceLocator.getOrderService();
			
			// update deliveryFrom/To of the order			
			OrderEntity orderEntity = orderService.findOrderByOrderNumber(orderNr);
			orderEntity.setDeliveryFromDat(startTime);
			orderEntity.setDeliveryToDat(endTime);
			orderService.saveOrder(orderEntity);
			
			
			/**
			 * Fix me: entities from before the refactoring
			 
			/*UnitTestHelperService helperService = serviceLocator.getUnitTestHelperService();
			if (helperService.existsActiveAssignmentToOrderNr(orderNr)) {
				helperService.removeAssignmentFromTestOrder(orderNr);
			}
			
			AssignmentEntity assignmentEntity = orderService.createAssignment(truckID, orderNr, startTime, loadDuration, transportDuration, unloadDuration);
			
			DispoService dispoService = serviceLocator.getDispoService();
			dispoService.transmitToDriver(assignmentEntity.getAssignmentId());
			
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}

	}*/
}
