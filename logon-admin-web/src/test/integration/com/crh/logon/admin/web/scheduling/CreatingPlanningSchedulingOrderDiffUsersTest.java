/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.scheduling;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;


import com.crh.logon.admin.web.pages.TestProperties.OrderPageKey;
import com.crh.logon.admin.web.pages.TestProperties.SchedulePageKey;
import com.crh.logon.admin.web.scheduling.pages.SchedulingPage;


public class CreatingPlanningSchedulingOrderDiffUsersTest extends AbstractSchedulingPageHelper {

	private final static String IN_PROPERTIES_PATH = "etc/test/input/CreatingPlanningSchedulingOrderDiffUsers.properties";

	private final static String OUT_PROPERTIES_PATH = "etc/test/output/CreatingPlanningSchedulingOrderDiffUsersOutput.properties";

	/**
	 * @param className
	 * @param inPropertiesPath
	 * @param outPropertiesPath
	 */
	public CreatingPlanningSchedulingOrderDiffUsersTest() {
		super(CreatingPlanningSchedulingOrderDiffUsersTest.class.getSimpleName(), null, null);
	}

	@Test
	public void testCreatingPlanningSchedulingOrderDiffUsers() throws FileNotFoundException,
			InterruptedException, IOException {

		this.createOrder(IN_PROPERTIES_PATH, OUT_PROPERTIES_PATH);
		Properties inProperties = propertyManager.getPropertiesForPath(IN_PROPERTIES_PATH);

		propertyManager.loadProperties(OUT_PROPERTIES_PATH);
		Properties orderProperties = propertyManager.getPropertiesForPath(OUT_PROPERTIES_PATH);

		schedulingOrderWithCrhPlanner(inProperties, orderProperties, false);
		deallocateAndSetNewCarrierWithUser1(inProperties, orderProperties);
		schedulingOrderWithUser2(inProperties, orderProperties);
		verifyScheduledOrderWithUser1(inProperties, orderProperties);
	}

	/**
	 * @param inProperties
	 * @param orderProperties
	 */
	private void deallocateAndSetNewCarrierWithUser1(Properties inProperties,
			Properties orderProperties) {
		loginToLogon("/logon/admin/AdminEntry.do", "user_1", "1234");

		String custOrderNr = orderProperties.getProperty(OrderPageKey.CUST_ORDER_NO.getKeyString());

		// initialize scheduling page
		SchedulingPage schedulingPage = initSchedulePage(
				inProperties.getProperty(SchedulePageKey.SCHEDULING_CARRIER_NAME_1.getKeyString()),
				orderProperties.getProperty(OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));

		schedulingPage.deallocateOrder(custOrderNr);
		schedulingPage.selectExecuteCarriersBelongToCustOrderNo(custOrderNr,
				inProperties.getProperty(SchedulePageKey.SCHEDULING_CARRIER_NAME_2.getKeyString()));

		logoutOfLogon();
	}

	/**
	 * @param inProperties
	 * @param orderProperties
	 */
	private void schedulingOrderWithUser2(Properties inProperties, Properties orderProperties) {
		loginToLogon("/logon/admin/AdminEntry.do", "user_2", "1234");

		SchedulingPage schedulingPage = initSchedulePage(
				inProperties.getProperty(SchedulePageKey.SCHEDULING_CARRIER_NAME_2.getKeyString()),
				orderProperties.getProperty(OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));

		// schedule order to truck
		schedulingPage.schedulingAnOrder(
				orderProperties.getProperty(OrderPageKey.ORDER_NO.getKeyString()),
				inProperties.getProperty(SchedulePageKey.TRUCK_ID_FOR_CARRIER_NAME_2.getKeyString()));
		// release order
		schedulingPage.releaseOrder(orderProperties.getProperty(OrderPageKey.CUST_ORDER_NO.getKeyString()));

		logoutOfLogon();
	}

	/**
	 * 
	 */
	private void verifyScheduledOrderWithUser1(Properties inProperties, Properties orderProperties) {
		loginToLogon("/logon/admin/AdminEntry.do", "user_1", "1234");

		// initialize scheduling page
		SchedulingPage schedulingPage = initSchedulePage(
				inProperties.getProperty(SchedulePageKey.SCHEDULING_CARRIER_NAME_1.getKeyString()),
				orderProperties.getProperty(OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));

		// select the scheduled order of ADN Carrier 2
		schedulingPage.selectScheduledOrder(orderProperties.getProperty(OrderPageKey.CUST_ORDER_NO.getKeyString()));

		// check if the deallocate button is enable for user 1 to cancel if necessary
		schedulingPage.verifyEnableButton("buttonCancel");

		logoutOfLogon();
	}
}
