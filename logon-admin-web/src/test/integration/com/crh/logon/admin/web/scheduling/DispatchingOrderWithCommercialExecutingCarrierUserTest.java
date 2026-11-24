/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.scheduling;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.crh.logon.admin.web.pages.LogonPageFactory;
import com.crh.logon.admin.web.pages.TestProperties;
import com.crh.logon.admin.web.scheduling.pages.SchedulingPage;
import org.junit.Test;
import org.junit.runner.RunWith;



public class DispatchingOrderWithCommercialExecutingCarrierUserTest extends AbstractSchedulingPageHelper {

	private final static String IN_PROPERTIES_PATH = "etc/test/input/DispatchingOrderWithCommercialExecutingCarrierUser.properties";

	private final static String OUT_PROPERTIES_PATH = "etc/test/output/DispatchingOrderWithCommercialExecutingCarrierUserOutput.properties";

	/**
	 * 
	 */
	public DispatchingOrderWithCommercialExecutingCarrierUserTest() {
		super(DispatchingOrderWithCommercialExecutingCarrierUserTest.class.getSimpleName(),
				IN_PROPERTIES_PATH, null);
	}

	@Test
	public void testDispatchingOrderWithCommercialExecutingCarrierUser()
			throws FileNotFoundException, InterruptedException, IOException {
		this.createOrder(IN_PROPERTIES_PATH, OUT_PROPERTIES_PATH);
		Properties inProperties = propertyManager.getPropertiesForPath(IN_PROPERTIES_PATH);

		propertyManager.loadProperties(OUT_PROPERTIES_PATH);
		Properties orderProperties = propertyManager.getPropertiesForPath(OUT_PROPERTIES_PATH);

		schedulingOrderWithCrhPlanner(inProperties, orderProperties, false);
		deallocateOrderAndSetNewCarrierWithUser6(inProperties, orderProperties);
		verifyScheduledOrderWithUser1(inProperties, orderProperties);
	}

	private void deallocateOrderAndSetNewCarrierWithUser6(Properties inProperties,
			Properties orderProperties) {
		loginToLogon("/logon/admin/AdminEntry.do", "user_6", "1234");

		String custOrderNr = orderProperties.getProperty(TestProperties.OrderPageKey.CUST_ORDER_NO.getKeyString());
		String orderNr = orderProperties.getProperty(TestProperties.OrderPageKey.ORDER_NO.getKeyString());

		// initialize scheduling page
		SchedulingPage schedulingPage = initSchedulePage(
				inProperties.getProperty(TestProperties.SchedulePageKey.SCHEDULING_CARRIER_NAME_1.getKeyString()),
				orderProperties.getProperty(TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));
		schedulingPage.deallocateOrder(custOrderNr);
		schedulingPage.selectExecuteCarriersBelongToCustOrderNo(custOrderNr,
				inProperties.getProperty(TestProperties.SchedulePageKey.SCHEDULING_CARRIER_NAME_2.getKeyString()));

		schedulingPage.schedulingAnOrder(
				orderNr,
				inProperties.getProperty(TestProperties.SchedulePageKey.TRUCK_ID_FOR_CARRIER_NAME_2.getKeyString()));
		schedulingPage.releaseOrderInRealTruckSection(custOrderNr);

		logoutOfLogon();
	}

	private void verifyScheduledOrderWithUser1(Properties inProperties, Properties orderProperties) {
		loginToLogon("/logon/admin/AdminEntry.do", "user_1", "1234");

		// initialize scheduling page
		SchedulingPage schedulingPage = LogonPageFactory.initElements(seleniumDriver,
				SchedulingPage.class);
		schedulingPage.selectCarriers(true, false);
		schedulingPage.selectDay(orderProperties.getProperty(TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));

		logoutOfLogon();
	}
}
