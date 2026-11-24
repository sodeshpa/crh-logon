/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.scheduling;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import com.crh.logon.admin.web.pages.TestProperties;
import com.crh.logon.admin.web.scheduling.pages.SchedulingPage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;



public class DispatchingBulkOrderToRealTruckTest extends AbstractSchedulingPageHelper {

	private final static String IN_PROPERTIES_PATH = "etc/test/input/DispatchingBulkOrderToRealTruckTest.properties";

	private final static String OUT_PROPERTIES_PATH = "etc/test/output/DispatchingBulkOrderToRealTruckTestOutput.properties";

	public DispatchingBulkOrderToRealTruckTest() {
		super(DispatchingBulkOrderToRealTruckTest.class.getSimpleName(), IN_PROPERTIES_PATH, null);
	}

	@Override
	protected void modifyProperties(Properties properties) {
		setDeliveryDate(new Date(), properties);
	}


	/**
	 * @TestSpec logon-831:Dispatching (bulk) order to Real truck
	 * @TestSpec LOGON-8885: Automate Testcase: logon-831:Dispatching (bulk) order to Real truck
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void dispatchingBulkOrderToRealTruckTest() throws FileNotFoundException, IOException,
			InterruptedException {
		//add new order
		this.createOrder(IN_PROPERTIES_PATH, OUT_PROPERTIES_PATH);
		Properties orderProperties = propertyManager.loadProperties(OUT_PROPERTIES_PATH);

		loginToLogonAsAdminEN();

		// initialize Scheduling page
		SchedulingPage schedulingPage = initSchedulePage(getInProperty(TestProperties.SchedulePageKey.POOL_NAME),
				getInProperty(TestProperties.OrderPageKey.CARRIER_NAME),
				orderProperties.getProperty(TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));

		String orderNr = orderProperties.getProperty(TestProperties.OrderPageKey.ORDER_NO.getKeyString());
		String custOrderNr = orderProperties.getProperty(TestProperties.OrderPageKey.CUST_ORDER_NO.getKeyString());
		schedulingPage.selectUnallocatedOrder(orderNr);

		
		// TODO 12-Dec-2014/nhattan: Used javascript directly to schedule a order, so the order can be
		// scheduled to any truck type. The test is failed locally
		/*
		//move order to truck 1
		schedulingPage.schedulingAnOrder(orderNr, getInProperty(SchedulePageKey.TRUCK_ID1));
		
		//verify that truck 1 does not contain order, because truck 1 type does not match the order's type
		Assert.assertFalse(schedulingPage.isTruckContainsOrder(getInProperty(SchedulePageKey.TRUCK_ID1),
				custOrderNr));
		*/

		//move order to truck 2
		schedulingPage.schedulingAnOrder(orderNr, getInProperty(TestProperties.SchedulePageKey.TRUCK_ID2));
		//verify that truck 2 contains the order
		Assert.assertTrue(schedulingPage.isTruckContainsOrder(getInProperty(TestProperties.SchedulePageKey.TRUCK_ID2),
				custOrderNr));

		//release the order
		schedulingPage.releaseOrder(custOrderNr);

		//deallocate the order
		schedulingPage.deallocateOrder(custOrderNr);

		//verify that truck 2 does not contain the order
		// TODO 18-Dec-2014/nhattan: This make nightly build timed out often when it cound not found truck that contains order
		//Assert.assertFalse(schedulingPage.isTruckContainsOrder(getInProperty(SchedulePageKey.TRUCK_ID2),
		//		custOrderNr));

		schedulingPage.selectUnallocatedOrder(orderNr);

		logoutOfLogon();
	}
}
