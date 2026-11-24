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

import com.crh.logon.admin.web.loading.pages.LoadingPage;
import com.crh.logon.admin.web.pages.TestProperties;
import com.crh.logon.admin.web.scheduling.pages.SchedulingPage;
import org.junit.Test;
import org.junit.runner.RunWith;


import com.crh.logon.admin.web.order.pages.OrderStatus;
import com.crh.logon.admin.web.pages.LogonPageFactory;

/**
 * @TestSpec LOGON-9036 Automate Testcase: logon-1075:Take over load
 */

public class TakeOverLoadTest extends AbstractSchedulingPageHelper {

	private final static String IN_PROPERTIES_PATH = "etc/test/input/TakeOverLoadTest.properties";

	private final static String OUT_PROPERTIES_PATH = "etc/test/output/TakeOverLoadTestOutput.properties";

	public TakeOverLoadTest() {
		super(TakeOverLoadTest.class.getSimpleName(), IN_PROPERTIES_PATH, null);
	}

	@Override
	protected void modifyProperties(Properties properties) {
		setDeliveryDate(new Date(), properties);
	}

	@Test
	public void testTakeOverLoad() throws FileNotFoundException, InterruptedException, IOException {
		//create 2 order with the same data
		//create order 1
		this.createOrder(IN_PROPERTIES_PATH, OUT_PROPERTIES_PATH);
		Properties order1Properties = propertyManager.loadProperties(OUT_PROPERTIES_PATH);
		String custOrderNr1 = order1Properties.getProperty(TestProperties.OrderPageKey.CUST_ORDER_NO.getKeyString());
		String orderNr1 = order1Properties.getProperty(TestProperties.OrderPageKey.ORDER_NO.getKeyString());

		//create order 2
		this.createOrder(IN_PROPERTIES_PATH, OUT_PROPERTIES_PATH);
		Properties order2Properties = propertyManager.loadProperties(OUT_PROPERTIES_PATH);
		String custOrderNr2 = order2Properties.getProperty(TestProperties.OrderPageKey.CUST_ORDER_NO.getKeyString());

		Properties inProperties = propertyManager.getPropertiesForPath(IN_PROPERTIES_PATH);

		//create truck
		loginToLogonAsAdminEN();
		String truckId = this.createTruck(inProperties);
		logoutOfLogon();

		loginToLogonAsAdminEN();
		//dispatching order1 to truck
		SchedulingPage schedulePage = dispatchingOrderToTruckAndRelease(truckId, inProperties,
				order1Properties);
		//check the status of order
		schedulePage.verifyOrderStatus(custOrderNr1, OrderStatus.RELEASED_FOR_EXECUTION.getStatus());

		schedulePage.enterShippingPointForScheduledOrder(custOrderNr1);
		schedulePage.verifyOrderStatus(custOrderNr1,
				OrderStatus.ARRIVED_AT_LOADING_POINT.getStatus());

		//load order 1
		loadOrder(orderNr1, getInProperty(TestProperties.OrderPageKey.QUANTITY),
				getInProperty(TestProperties.LoadingPageKey.LOADING_POINT));

		schedulePage = initSchedulePage(
				inProperties.getProperty(TestProperties.SchedulePageKey.POOL_NAME.getKeyString()),
				inProperties.getProperty(TestProperties.SchedulePageKey.CARRIER_NAME.getKeyString()),
				order1Properties.getProperty(TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));
		//verify status of order 1
		schedulePage.verifyOrderStatus(custOrderNr1, OrderStatus.SHIPPING.getStatus());
		
		//deallocate order 1
		schedulePage.deallocateOrder(custOrderNr1);

		//dispatching order 2 to truck
		schedulePage = dispatchingOrderToTruckAndRelease(truckId, inProperties, order2Properties);
		schedulePage.verifyOrderStatus(custOrderNr2, OrderStatus.SHIPPING.getStatus());
		logoutOfLogon();
	}

	private void loadOrder(String orderNr, String netQty, String loadingPoint) {
		LoadingPage loadingPage = LogonPageFactory.initElements(seleniumDriver, LoadingPage.class);
		loadingPage.searchOrder(orderNr);
		loadingPage.enterNetQuantity(netQty);
		loadingPage.selectLoadingPoint(loadingPoint);
		loadingPage.checkAndSave();
	}

	/**
	 * dispatching the order to truck and release.
	 * @param truckId the truck id
	 * @param inProperties
	 * @param orderProperties
	 */
	private SchedulingPage dispatchingOrderToTruckAndRelease(String truckId,
			Properties inProperties, Properties orderProperties) {
		String custOrderNr = orderProperties.getProperty(TestProperties.OrderPageKey.CUST_ORDER_NO.getKeyString());
		SchedulingPage schedulePage = initSchedulePage(
				inProperties.getProperty(TestProperties.SchedulePageKey.POOL_NAME.getKeyString()),
				inProperties.getProperty(TestProperties.SchedulePageKey.CARRIER_NAME.getKeyString()),
				orderProperties.getProperty(TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));

		schedulePage.schedulingAnOrder(
				orderProperties.getProperty(TestProperties.OrderPageKey.ORDER_NO.getKeyString()), truckId);

		//verify that truck contains the order
		assertTrue(schedulePage.isTruckContainsOrder(truckId, custOrderNr));
		//check the status of order
		schedulePage.verifyOrderStatus(custOrderNr, OrderStatus.SCHEDULED.getStatus());

		//release the order
		schedulePage.releaseOrder(custOrderNr);
		return schedulePage;
	}
}
