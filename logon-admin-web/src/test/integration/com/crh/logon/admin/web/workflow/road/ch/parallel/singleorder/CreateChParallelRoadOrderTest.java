/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.workflow.road.ch.parallel.singleorder;

import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import com.crh.logon.admin.web.order.pages.OrderResult;
import com.crh.logon.admin.web.pages.LogonSeleniumTestCaseBase;
import com.crh.logon.admin.web.pages.TestProperties;
import com.crh.logon.admin.web.workflow.PropertyTransferTest;
import com.crh.logon.admin.web.workflow.road.RoadWorkflowTestProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.crh.logon.admin.web.order.pages.OrderPage;
import com.crh.logon.admin.web.pages.LogonPageFactory;
import com.crh.logon.utils.time.DateTimeUtils;

/**
 * Input properties: see IN_PATH.
 * Out properties: see OUT_PATH.
 * 
 * CreateChParallelRoadOrderTest:
 *     creates a CH parallel system order (adnant test-pda-workflow-part0)
 * DispatchSingleChParallelRoadOrderTest:
 *     dispatches to a truck (test-pda-workflow-part1) (has to run parallel with test-pda-workflow-part3)
 * SOAP UI test:
 *     integration-tests-soapui-projects will wait for the parallel message
 *     DispatchChParallelOrderTest triggers (test-pda-workflow-part2) (has to run parallel with test-pda-workflow-part2)
 * VerifyChParallelOrderTest:
 *     loads call center, and checks that the order is in status FINISHED (test-pda-workflow-part3)
 * 
 * See the test steps and their timing in /logon/build-test.xml
 * See targets test-pda-workflow-part0 (logon-admin-web), test-pda-workflow-part1(logon-admin-web),
 * test-pda-workflow-part2, test-pda-workflow-part3(logon-admin-web)
 * build-test.xml.
 * 
 * Note: The order can be in status finished in case it successfully went through all these tests,
 * so in case this test fails, look for an error in either of them.
 * Note: This test has not been migrated to use the PageObjectPattern. Please 
 * don't use it as a blueprint for new tests. See also LOGON-8463.
 * 
 * @author AdNovum Informatik AG
 */
public class CreateChParallelRoadOrderTest extends PropertyTransferTest {
	protected final static String IN_PATH = "etc/test/input/ChParallelRoadOrderTest.properties";

	protected final static String OUT_PATH = "etc/test/output/workflow.properties";

	@Before
	public void login() throws Exception {
		// login with order taker:
		LogonSeleniumTestCaseBase.loginToLogon("/logon/order/bestellung/BestellungShipToEntry.do", "adn_role_6", "adn_role_6");
	}

	@After
	public void logout() throws Exception {
		LogonSeleniumTestCaseBase.logoutOfLogon();
	}

	public CreateChParallelRoadOrderTest() {
		super(CreateChParallelRoadOrderTest.class.getSimpleName(), IN_PATH, OUT_PATH);
	}

	@Test(timeout = 900000)
	public void testCreateChRoadOrder() throws Exception {
		Properties properties = propertyManager.getTestRelatedStoredProperties(getInPropertiesPath());
		String txt = properties.getProperty(TestProperties.OrderPageKey.SOLDTO.getKeyString()) + " "
				+ properties.getProperty(TestProperties.OrderPageKey.SHIPTO.getKeyString()); // e.g., 73161 1209037

		OrderPage orderPage = LogonPageFactory.initElements(seleniumDriver, OrderPage.class);
		orderPage.selectShipto(txt);

		/*
		 * Apr 8, 2015/fszabo: NB failed because of the new check for the delivery window. 
		 * Testcase needed to be adjusted: a bigger delivery window is given for the custorder,
		 * including today. Related ticket: LOGON-8669
		 */
		properties.setProperty(TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString(),
				DateTimeUtils.formatDayMonthYear(new Date(), Locale.ENGLISH));

		OrderResult order = orderPage.createOrderWithSampledata(properties);

		// write the returned order number to file (for use in later test): 
		setOutProperty(RoadWorkflowTestProperties.ROAD_WORKFLOW_KEY.ORDER_NO, order.getOrderNr());
		setOutProperty(RoadWorkflowTestProperties.ROAD_WORKFLOW_KEY.CUST_ORDER_NO, order.getCustOrderNr());

		orderPage.changeDeliveryTime(order.getOrderNr(),
				DateTimeUtils.formatDayMonthYear(new Date(), Locale.ENGLISH),
				DateTimeUtils.formatDayMonthYear(new Date(), Locale.ENGLISH));
	}
}
