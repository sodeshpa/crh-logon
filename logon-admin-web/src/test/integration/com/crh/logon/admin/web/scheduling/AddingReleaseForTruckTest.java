/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.scheduling;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.crh.logon.admin.web.pages.LogonSeleniumTestCaseBase;
import com.crh.logon.admin.web.pages.TestProperties;
import com.crh.logon.admin.web.scheduling.pages.SchedulingPage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;



public class AddingReleaseForTruckTest extends AbstractSchedulingPageHelper {

	private static final String IN_PROPERTIES_PATH = "etc/test/input/AddingReleaseForTruckTest.properties";

	private static final String OUT_PROPERTIES_PATH = "etc/test/output/AddingReleaseForTruckTestOutput.properties";

	public AddingReleaseForTruckTest() {
		super(AddingReleaseForTruckTest.class.getSimpleName(), IN_PROPERTIES_PATH, null);
	}

	@Test
	public void addingReleaseForTruckTest() throws FileNotFoundException, InterruptedException,
			IOException {
		//add new order
		this.createOrder(IN_PROPERTIES_PATH, OUT_PROPERTIES_PATH);
		Properties orderProperties = propertyManager.loadProperties(OUT_PROPERTIES_PATH);

		submitOrderToCarrierWithCrhPlanner(orderProperties);

		confirmOrderAndSetExecutingCarrierWithUser1(orderProperties);

		dispatchOrderWithUser2(orderProperties);
	}

	private void submitOrderToCarrierWithCrhPlanner(Properties orderProperties) {
		LogonSeleniumTestCaseBase.loginToLogon(ORDER_URL, CRH_PLANNER_NAME, CRH_PLANNER_PASSWORD);

		String orderNo = orderProperties.getProperty(TestProperties.OrderPageKey.ORDER_NO.getKeyString());
		SchedulingPage schedulingPage = initSchedulingPageAndSelectOrder(orderProperties,
				getInProperty(TestProperties.SchedulePageKey.CARRIER_NAME));
		schedulingPage.submitOrderToCarrier(orderNo);

		LogonSeleniumTestCaseBase.logoutOfLogon();
	}

	private void confirmOrderAndSetExecutingCarrierWithUser1(Properties orderProperties) {
		LogonSeleniumTestCaseBase.loginToLogon(ORDER_URL, USER1_NAME, USER1_PASSWORD);

		String orderNo = orderProperties.getProperty(TestProperties.OrderPageKey.ORDER_NO.getKeyString());
		String custOrderNo = orderProperties.getProperty(TestProperties.OrderPageKey.CUST_ORDER_NO.getKeyString());

		SchedulingPage schedulingPage = initSchedulingPageAndSelectOrder(orderProperties,
				getInProperty(TestProperties.SchedulePageKey.CARRIER_NAME));
		schedulingPage.verifyExecutingCarrierSelectStatus(custOrderNo, false);
		schedulingPage.confirmOrder(orderNo);
		schedulingPage.verifyExecutingCarrierSelectStatus(custOrderNo, true);

		//set the Executing carrier to Carrier 2
		schedulingPage.selectExecuteCarriersBelongToCustOrderNo(custOrderNo,
				getInProperty(TestProperties.SchedulePageKey.CARRIER_NAME2));

		LogonSeleniumTestCaseBase.logoutOfLogon();
	}

	private void dispatchOrderWithUser2(Properties orderProperties) {
		LogonSeleniumTestCaseBase.loginToLogon(ORDER_URL, USER2_NAME, USER2_PASSWORD);
		String orderNr = orderProperties.getProperty(TestProperties.OrderPageKey.ORDER_NO.getKeyString());
		String custOrderNr = orderProperties.getProperty(TestProperties.OrderPageKey.CUST_ORDER_NO.getKeyString());

		SchedulingPage schedulingPage = initSchedulingPageAndSelectOrder(orderProperties,
				getInProperty(TestProperties.SchedulePageKey.CARRIER_NAME2));
		schedulingPage.schedulingAnOrder(orderNr, getInProperty(TestProperties.SchedulePageKey.TRUCK_ID1));
		//verify that order is moved successfully
		Assert.assertTrue(schedulingPage.isTruckContainsOrder(
				getInProperty(TestProperties.SchedulePageKey.TRUCK_ID1), custOrderNr));

		//deallocate the order
		schedulingPage.deallocateOrder(custOrderNr);

		//verify that the order is deallocate successfully
		schedulingPage.selectUnallocatedOrder(orderNr);
		//verify that truck 1 does not contain order
		// TODO 18-Dec-2014/nhattan: This make nightly build timed out often when it cound not found truck that contains order
		// Assert.assertFalse(schedulingPage.isTruckContainsOrder(getInProperty(SchedulePageKey.TRUCK_ID1),
		//		custOrderNr));

		//move order to truck 2
		schedulingPage.schedulingAnOrder(orderNr, getInProperty(TestProperties.SchedulePageKey.TRUCK_ID2));

		//verify that truck 2 contains the order
		Assert.assertTrue(schedulingPage.isTruckContainsOrder(
				getInProperty(TestProperties.SchedulePageKey.TRUCK_ID2), custOrderNr));

		LogonSeleniumTestCaseBase.logoutOfLogon();
	}

	private SchedulingPage initSchedulingPageAndSelectOrder(Properties orderProperties,
			String carrierName) {
		// initialize Scheduling page
		SchedulingPage schedulingPage = initSchedulePage(getInProperty(TestProperties.SchedulePageKey.POOL_NAME),
				carrierName,
				orderProperties.getProperty(TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));

		schedulingPage.selectUnallocatedOrder(orderProperties.getProperty(TestProperties.OrderPageKey.ORDER_NO.getKeyString()));

		return schedulingPage;
	}
}
