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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;



@Ignore
// TODO 18-Dec-2014/nhattan: Element not found in the cache - perhaps the page has changed since it was looked up at SchedulingPage.java:466
public class ReleasingOnUnauthorizedTruckTest extends AbstractSchedulingPageHelper {

	private final static String IN_PROPERTIES_PATH = "etc/test/input/ReleasingOnUnauthorizedTruck.properties";

	private final static String OUT_PROPERTIES_PATH = "etc/test/output/ReleasingOnUnauthorizedTruckOutput.properties";

	/**
	 * @param className
	 * @param inPropertiesPath
	 * @param outPropertiesPath
	 */
	public ReleasingOnUnauthorizedTruckTest() {
		super(ReleasingOnUnauthorizedTruckTest.class.getSimpleName(), IN_PROPERTIES_PATH, null);
	}

	/**
	 * @TestSpec logon-1441:Releasing on a not authorized truck should be preceded by a warning
	 * @TestSpec LOGON-8839: Automate Testcase: logon-1441:Releasing on a not authorized truck should be preceded by a warning
	 * 
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Test
	public void testReleasingOnUnauthorizedTruck() throws FileNotFoundException,
			InterruptedException, IOException {

		this.createOrder(IN_PROPERTIES_PATH, OUT_PROPERTIES_PATH);
		Properties orderProperties = propertyManager.loadProperties(OUT_PROPERTIES_PATH);

		dispatchOrderThenDeallocateFromPlanningTruck(orderProperties);
		setExecutingCarrierWithUser1(orderProperties);
		dispatchOrderWithUser2(orderProperties);
		releaseOrderWithUser1(orderProperties);
	}

	/**
	 * Dispatch an order to planning truck and then deallocate it after that
	 * @param orderProperties the order output properties
	 */
	private void dispatchOrderThenDeallocateFromPlanningTruck(Properties orderProperties) {
		LogonSeleniumTestCaseBase.loginToLogon(ORDER_URL, CRH_PLANNER_NAME, CRH_PLANNER_PASSWORD);

		SchedulingPage schedulingPage = initSchedulePage(
				getInProperty(TestProperties.SchedulePageKey.POOL_NAME.getKeyString()),
				getInProperty(TestProperties.SchedulePageKey.SCHEDULING_CARRIER_NAME_1.getKeyString()),
				orderProperties.getProperty(TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));
		schedulingPage.schedulingAnOrder(
				orderProperties.getProperty(TestProperties.OrderPageKey.ORDER_NO.getKeyString()),
				getInProperty(TestProperties.SchedulePageKey.TRUCK_ID_FOR_CARRIER_NAME_1.getKeyString()));
		schedulingPage.deallocateOrder(orderProperties.getProperty(TestProperties.OrderPageKey.CUST_ORDER_NO.getKeyString()));

		LogonSeleniumTestCaseBase.logoutOfLogon();
	}

	/**
	 * 
	 * @param orderProperties
	 */
	private void setExecutingCarrierWithUser1(Properties orderProperties) {
		LogonSeleniumTestCaseBase.loginToLogon(ORDER_URL, USER1_NAME, USER1_PASSWORD);

		SchedulingPage schedulingPage = initSchedulePage(
				getInProperty(TestProperties.SchedulePageKey.POOL_NAME.getKeyString()),
				getInProperty(TestProperties.SchedulePageKey.SCHEDULING_CARRIER_NAME_1.getKeyString()),
				orderProperties.getProperty(TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));
		schedulingPage.selectExecuteCarriersBelongToCustOrderNo(
				orderProperties.getProperty(TestProperties.OrderPageKey.CUST_ORDER_NO.getKeyString()),
				getInProperty(TestProperties.SchedulePageKey.SCHEDULING_CARRIER_NAME_2.getKeyString()));

		LogonSeleniumTestCaseBase.logoutOfLogon();
	}

	/**
	 * 
	 * @param orderProperties
	 */
	private void dispatchOrderWithUser2(Properties orderProperties) {
		LogonSeleniumTestCaseBase.loginToLogon(ORDER_URL, USER2_NAME, USER2_PASSWORD);

		SchedulingPage schedulingPage = initSchedulePage(
				getInProperty(TestProperties.SchedulePageKey.POOL_NAME.getKeyString()),
				getInProperty(TestProperties.SchedulePageKey.SCHEDULING_CARRIER_NAME_2.getKeyString()),
				orderProperties.getProperty(TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));
		schedulingPage.schedulingAnOrder(
				orderProperties.getProperty(TestProperties.OrderPageKey.ORDER_NO.getKeyString()),
				getInProperty(TestProperties.SchedulePageKey.TRUCK_ID_FOR_CARRIER_NAME_2.getKeyString()));

		LogonSeleniumTestCaseBase.logoutOfLogon();
	}

	/**
	 * 
	 * @param orderProperties
	 */
	private void releaseOrderWithUser1(Properties orderProperties) {
		LogonSeleniumTestCaseBase.loginToLogon(ORDER_URL, USER1_NAME, USER1_PASSWORD);

		SchedulingPage schedulingPage = initSchedulePage(
				getInProperty(TestProperties.SchedulePageKey.POOL_NAME.getKeyString()),
				getInProperty(TestProperties.SchedulePageKey.SCHEDULING_CARRIER_NAME_1.getKeyString()),
				orderProperties.getProperty(TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));
		schedulingPage.releaseOrder(orderProperties.getProperty(TestProperties.OrderPageKey.CUST_ORDER_NO.getKeyString()));

		// need to verify warning popup here
		schedulingPage.verifyPopup(true);
		schedulingPage.confirmReleaseTransport();

		LogonSeleniumTestCaseBase.logoutOfLogon();
	}
}
