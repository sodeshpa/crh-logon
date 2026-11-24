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

public class OnTimeOrderTest extends AbstractSchedulingPageHelper {
	private final static String IN_PROPERTIES_PATH = "etc/test/input/OnTimeOrderTest.properties";

	private final static String OUT_PROPERTIES_PATH = "etc/test/output/OnTimeoOrderTestOutput.properties";

	/**
	 * 
	 */
	public OnTimeOrderTest() {
		super(OnTimeOrderTest.class.getSimpleName(), IN_PROPERTIES_PATH, null);
	}

	/**
	 * @TestSpec LOGON-8940: Automate Testcase: logon-1290:On-time order
	 * 
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Test
	public void testOnTimeOrder() throws FileNotFoundException, InterruptedException, IOException {
		this.createOrder(IN_PROPERTIES_PATH, OUT_PROPERTIES_PATH);
		Properties orderProperties = propertyManager.loadProperties(OUT_PROPERTIES_PATH);

		LogonSeleniumTestCaseBase.loginToLogonAsAdminEN();
		// initialize Scheduling page
		SchedulingPage schedulingPage = initSchedulePage(getInProperty(TestProperties.SchedulePageKey.POOL_NAME),
				getInProperty(TestProperties.OrderPageKey.CARRIER_NAME),
				orderProperties.getProperty(TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));

		String orderNr = orderProperties.getProperty(TestProperties.OrderPageKey.ORDER_NO.getKeyString());
		String custOrderNr = orderProperties.getProperty(TestProperties.OrderPageKey.CUST_ORDER_NO.getKeyString());
		schedulingPage.selectUnallocatedOnTimeOrder(orderNr);

		String truckId = getInProperty(TestProperties.SchedulePageKey.TRUCK_ID1);

		// schedule an on time order to the truck
		schedulingPage.schedulingAnOrder(orderNr, truckId);
		Assert.assertTrue(schedulingPage.isTruckContainsOrder(truckId, custOrderNr));

		// release an on time order
		schedulingPage.releaseOnTimeOrder(custOrderNr);

		// verify on time order has light green color
		Assert.assertTrue(schedulingPage.isOnTimeOrderRelease(custOrderNr));
	}
}
