/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision: $
 * Date of last edit : $Date: $
 */

package com.crh.logon.admin.web.scheduling;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.pages.LogonPageFactory;
import com.crh.logon.admin.web.pages.TestProperties.OrderPageKey;
import com.crh.logon.admin.web.pages.TestProperties.SchedulePageKey;
import com.crh.logon.admin.web.scheduling.pages.SchedulingPage;


public class TruckStatusChangesWhileSchedulingTest extends AbstractSchedulingPageHelper {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(TruckStatusChangesWhileSchedulingTest.class);

	private static final String IN_PROPERTIES_PATH = "etc/test/input/TruckStatusChangesWhileSchedulingTest.properties";

	private static final String OUT_PROPERTIES_PATH = "etc/test/output/TruckStatusChangesWhileSchedulingTest.properties";

	public TruckStatusChangesWhileSchedulingTest() {
		super(TruckStatusChangesWhileSchedulingTest.class.getSimpleName(), IN_PROPERTIES_PATH, null);
	}

	@Override
	protected void modifyProperties(Properties properties) {
		setDeliveryDate(new Date(), properties);
	}
	
	/**
	 * @TestSpec logon-837:Truck status changes (while scheduling)
	 * @TestSpec LOGON-8889: Automate Testcase: logon-837:Truck status changes (while scheduling)
	 * 
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Test
	public void testTruckStatusChanges() throws FileNotFoundException, InterruptedException,
			IOException {
		// create new order
		this.createOrder(IN_PROPERTIES_PATH, OUT_PROPERTIES_PATH);

		propertyManager.loadProperties(OUT_PROPERTIES_PATH);
		Properties orderProperties = propertyManager.getPropertiesForPath(OUT_PROPERTIES_PATH);
		String custOrderNr = orderProperties.getProperty(OrderPageKey.CUST_ORDER_NO.getKeyString());
		String orderNr = orderProperties.getProperty(OrderPageKey.ORDER_NO.getKeyString());
		LOG.info("orderNr: " + orderNr);

		//login as crh_planner
		loginToLogon(ORDER_URL, CRH_PLANNER_NAME, CRH_PLANNER_PASSWORD);
		// initialize Scheduling page
		SchedulingPage schedulingPage = LogonPageFactory.initElements(seleniumDriver,
				SchedulingPage.class);
		//set search criteria
		schedulingPage.selectPool(getInProperty(SchedulePageKey.POOL_NAME));
		schedulingPage.selectCarriers(true, false);
		schedulingPage.selectDay(orderProperties.getProperty(OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));
		// set commercial carrier for order
		schedulingPage.selectCommercialCarriersBelongToCustOrderNo(custOrderNr,
				getInProperty(SchedulePageKey.SCHEDULING_CARRIER_NAME_1));
		LOG.info("Select commercial carrier: " + getInProperty(SchedulePageKey.SCHEDULING_CARRIER_NAME_1));

		// submit the order to carrier
		schedulingPage.submitOrderToCarrier(orderNr);
		// confirm order
		schedulingPage.confirmOrder(orderNr);
		//set the Executing carrier to Carrier
		schedulingPage.selectExecuteCarriersBelongToCustOrderNo(custOrderNr,
				getInProperty(SchedulePageKey.SCHEDULING_CARRIER_NAME_2));
		// schedule the order
		schedulingPage.schedulingAnOrder(orderNr,
				getInProperty(SchedulePageKey.TRUCK_ID_FOR_CARRIER_NAME_1));
		// release order
		schedulingPage.releaseOrder(custOrderNr);
		// enter shipping point
		schedulingPage.enterShippingPointForScheduledOrder(custOrderNr);
	}
}
