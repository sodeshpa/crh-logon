/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.scheduling;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import com.crh.logon.admin.web.scheduling.pages.SchedulingPage;
import org.junit.Test;
import org.junit.runner.RunWith;


import com.crh.logon.admin.web.order.pages.OrderStatus;
import com.crh.logon.admin.web.pages.TestProperties.OrderPageKey;
import com.crh.logon.admin.web.pages.TestProperties.SchedulePageKey;
import com.crh.logon.utils.StringUtils;

/**
 * @TestSpec LOGON-8939 Automate Testcase: logon-1310:Dispatching orders to real truck - transport category all mat package types.
 */

public class DispatchingOrderToRealTruckTest extends AbstractSchedulingPageHelper {

	private final static String IN_PROPERTIES_PATH = "etc/test/input/DispatchingOrderToRealTruckTest.properties";

	private final static String BAG_ORDER_PROPERTIES_PATH = "etc/test/input/DispatchingBagOrderToRealTruckTest.properties";

	private final static String BULK_DUMPED_ORDER_PROPERTIES_PATH = "etc/test/input/DispatchingBulkDumpedOrderToRealTruckTest.properties";
	
	private final static String PLANNING_ORDER_PROPERTIES_PATH = "etc/test/input/DispatchingPlanningOrderToRealTruckTest.properties";

	private final static String OUT_PROPERTIES_PATH = "etc/test/output/DispatchingOrderToRealTruckTestOutput.properties";

	public DispatchingOrderToRealTruckTest() {
		super(DispatchingOrderToRealTruckTest.class.getSimpleName(), IN_PROPERTIES_PATH, null);
	}

	@Override
	protected void modifyProperties(Properties properties) {
		setDeliveryDate(new Date(), properties);
	}

	@Test
	public void testDispatchingBulkOrderToTruck() throws FileNotFoundException, IOException,
			InterruptedException {
		createAndDispatchOrder("");
	}

	@Test
	public void testDispatchingBagOrderToTruck() throws FileNotFoundException, IOException,
			InterruptedException {
		createAndDispatchOrder(BAG_ORDER_PROPERTIES_PATH);
	}

	@Test
	public void testDispatchingBulkDumpedOrderToTruck() throws FileNotFoundException, IOException,
			InterruptedException {
		createAndDispatchOrder(BULK_DUMPED_ORDER_PROPERTIES_PATH);
	}

	@Test
	public void testDispatchingPlanningOrderToTruck() throws FileNotFoundException, IOException,
			InterruptedException {
		createAndDispatchOrder(PLANNING_ORDER_PROPERTIES_PATH);
	}

	/**
	 * create an order and dispatch it to the truck.
	 * @param orderPath the order path
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private void createAndDispatchOrder(String orderPath) throws FileNotFoundException,
			InterruptedException, IOException {
		if (StringUtils.empty(orderPath)) {
			orderPath = ORDER_IN_PROPERTIES_PATH;
		}
		this.createOrder(orderPath, IN_PROPERTIES_PATH, OUT_PROPERTIES_PATH);
		Properties orderProperties = propertyManager.loadProperties(OUT_PROPERTIES_PATH);
		Properties inProperties = propertyManager.getPropertiesForPath(IN_PROPERTIES_PATH);

		//create truck
		loginToLogonAsAdminEN();
		String truckId = this.createTruck(inProperties);
		logoutOfLogon();

		loginToLogonAsAdminEN();
		dispatchingOrderToTruckAndEnterShippingpoint(truckId, inProperties, orderProperties);
		logoutOfLogon();
	}

	/**
	 * dispatching the order and click enter shipping point.
	 * @param truckId the truck id
	 * @param inProperties
	 * @param orderProperties
	 */
	private void dispatchingOrderToTruckAndEnterShippingpoint(String truckId,
			Properties inProperties, Properties orderProperties) {
		String custOrderNr = orderProperties.getProperty(OrderPageKey.CUST_ORDER_NO.getKeyString());
		SchedulingPage schedulePage = initSchedulePage(
				inProperties.getProperty(SchedulePageKey.POOL_NAME.getKeyString()),
				inProperties.getProperty(SchedulePageKey.CARRIER_NAME.getKeyString()),
				orderProperties.getProperty(OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));

		schedulePage.schedulingAnOrder(
				orderProperties.getProperty(OrderPageKey.ORDER_NO.getKeyString()), truckId);

		//verify that truck contains the order
		assertTrue(schedulePage.isTruckContainsOrder(truckId, custOrderNr));
		//check the status of order
		schedulePage.verifyOrderStatus(custOrderNr, OrderStatus.SCHEDULED.getStatus());

		//release the order
		schedulePage.releaseOrder(custOrderNr);
		//check the status of order
		schedulePage.verifyOrderStatus(custOrderNr, OrderStatus.RELEASED_FOR_EXECUTION.getStatus());

		schedulePage.enterShippingPointForScheduledOrder(custOrderNr);
		//check the status of order
		schedulePage.verifyOrderStatus(custOrderNr,
				OrderStatus.ARRIVED_AT_LOADING_POINT.getStatus());
	}
}
