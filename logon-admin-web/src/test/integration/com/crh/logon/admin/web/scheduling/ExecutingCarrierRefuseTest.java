/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.scheduling;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import ch.adnovum.jcan.test.selenium.util.WebElementUtil;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.pages.TestProperties.OrderPageKey;
import com.crh.logon.admin.web.pages.TestProperties.SchedulePageKey;
import com.crh.logon.admin.web.scheduling.pages.SchedulingPage;


public class ExecutingCarrierRefuseTest extends AbstractSchedulingPageHelper {

	private final static String IN_PROPERTIES_PATH = "etc/test/input/ExecutingCarrierRefuse.properties";

	private final static String OUT_PROPERTIES_PATH = "etc/test/output/ExecutingCarrierRefuseOutput.properties";

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ExecutingCarrierRefuseTest.class);

	/**
	 * @param className
	 * @param inPropertiesPath
	 * @param outPropertiesPath
	 */
	public ExecutingCarrierRefuseTest() {
		super(ExecutingCarrierRefuseTest.class.getSimpleName(), IN_PROPERTIES_PATH,
				OUT_PROPERTIES_PATH);
	}

	@Test
	public void testExecutingCarrierRefuse() throws FileNotFoundException, InterruptedException,
			IOException {
		this.createOrder(IN_PROPERTIES_PATH, OUT_PROPERTIES_PATH);
		Properties inProperties = propertyManager.getPropertiesForPath(IN_PROPERTIES_PATH);

		propertyManager.loadProperties(OUT_PROPERTIES_PATH);
		Properties orderProperties = propertyManager.getPropertiesForPath(OUT_PROPERTIES_PATH);

		schedulingOrderWithCrhPlanner(inProperties, orderProperties, true);
		confirmOrderWithUser1(inProperties, orderProperties);

		LOG.info("decline Order With user_2)"
				+ orderProperties.getProperty(OrderPageKey.ORDER_NO.getKeyString()));

		declineOrderWithUser2(inProperties, orderProperties);
		resetCarrierDeclineWithUser1(inProperties, orderProperties);
	}

	/**
	 * Confirm an order with user_1 account
	 * @param inProperties the input properties
	 * @param orderProperties the order properties created in the previous step
	 */
	private void confirmOrderWithUser1(Properties inProperties, Properties orderProperties) {
		loginToLogon("/logon/admin/AdminEntry.do", "user_1", "1234");

		// initialize scheduling page
		SchedulingPage schedulingPage = initSchedulePage(
				inProperties.getProperty(SchedulePageKey.SCHEDULING_CARRIER_NAME_1.getKeyString()),
				orderProperties.getProperty(OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));
		schedulingPage.confirmOrder(orderProperties.getProperty(OrderPageKey.ORDER_NO.getKeyString()));
		schedulingPage.selectExecuteCarriersBelongToCustOrderNo(
				orderProperties.getProperty(OrderPageKey.CUST_ORDER_NO.getKeyString()),
				inProperties.getProperty(SchedulePageKey.SCHEDULING_CARRIER_NAME_2.getKeyString()));
		logoutOfLogon();
	}

	/**
	 * Decline an order with user_2 account
	 * @param inProperties the input properties
	 * @param orderProperties the order properties created in the previous step
	 */
	private void declineOrderWithUser2(Properties inProperties, Properties orderProperties) {
		loginToLogon("/logon/admin/AdminEntry.do", "user_2", "1234");

		// initialize scheduling page
		SchedulingPage schedulingPage = initSchedulePage(
				inProperties.getProperty(SchedulePageKey.SCHEDULING_CARRIER_NAME_2.getKeyString()),
				orderProperties.getProperty(OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));
		schedulingPage.declineOrder(
				orderProperties.getProperty(OrderPageKey.ORDER_NO.getKeyString()),
				inProperties.getProperty(SchedulePageKey.DECLINE_REASON.getKeyString()));
		logoutOfLogon();
	}

	/**
	 * Reset carrier decline with user_1 account
	 * @param orderProperties the order properties created in the previous step
	 */
	private void resetCarrierDeclineWithUser1(Properties inProperties, Properties orderProperties) {
		loginToLogon("/logon/admin/AdminEntry.do", "user_1", "1234");

		initSchedulePage(
				inProperties.getProperty(SchedulePageKey.SCHEDULING_CARRIER_NAME_1.getKeyString()),
				orderProperties.getProperty(OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));

		WebElement redQuestionMark = seleniumDriver.findElement(
				By.xpath("//div[contains(@class, 'orderCaption') and contains(text(), '"
						+ orderProperties.getProperty(OrderPageKey.ORDER_NO.getKeyString())
						+ "')]//following::img[contains(@src, 'images/exclamation.gif') and contains(@onclick, 'resetCarrierDeclineNote')]"));
		Assert.assertNotNull(redQuestionMark);
		redQuestionMark.click();
		logoutOfLogon();
	}
}
