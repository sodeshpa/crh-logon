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


public class DispatchingOrderWithPDATest extends AbstractSchedulingPageHelper {

	private final static String IN_PROPERTIES_PATH = "etc/test/input/DispatchingOrderWithPDA.properties";

	private final static String OUT_PROPERTIES_PATH = "etc/test/output/DispatchingOrderWithPDAOutput.properties";

	/**
	 * @TestSpec logon-834:Dispatching order w/o PDA
	 * @TestSpec LOGON-8888: Automate Testcase: logon-834:Dispatching order w/o PDA
	 * @param className
	 * @param inPropertiesPath
	 * @param outPropertiesPath
	 */
	public DispatchingOrderWithPDATest() {
		super(DispatchingOrderWithPDATest.class.getSimpleName(), IN_PROPERTIES_PATH, null);
	}

	@Test
	public void testDispatchingOrderWithPDA() throws FileNotFoundException, InterruptedException,
			IOException {
		this.createOrder(IN_PROPERTIES_PATH, OUT_PROPERTIES_PATH);

		loginToLogonAsAdminEN();

		Properties inProperties = propertyManager.getPropertiesForPath(IN_PROPERTIES_PATH);
		Properties orderProperties = propertyManager.loadProperties(OUT_PROPERTIES_PATH);
		String truckId = this.createTruck(inProperties);
		dispatchOrder(inProperties, orderProperties, truckId);

		logoutOfLogon();
	}

	/**
	 * Dispatch the order to truck
	 * @param inProperties input properties
	 * @param orderProperties the order output properties
	 * @param truckId the created truck id
	 */
	private void dispatchOrder(Properties inProperties, Properties orderProperties, String truckId) {
		SchedulingPage schedulePage = initSchedulePage(
				inProperties.getProperty(SchedulePageKey.POOL_NAME.getKeyString()),
				inProperties.getProperty(SchedulePageKey.CARRIER_NAME.getKeyString()),
				orderProperties.getProperty(OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));
		schedulePage.schedulingAnOrder(
				orderProperties.getProperty(OrderPageKey.ORDER_NO.getKeyString()), truckId);
	}
}