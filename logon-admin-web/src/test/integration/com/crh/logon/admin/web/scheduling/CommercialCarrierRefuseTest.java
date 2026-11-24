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
import com.crh.logon.admin.web.pages.LogonSeleniumTestCaseBase;
import com.crh.logon.admin.web.pages.TestProperties;
import com.crh.logon.admin.web.scheduling.pages.SchedulingPage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import ch.adnovum.jcan.test.selenium.util.WebElementUtil;

public class CommercialCarrierRefuseTest extends AbstractSchedulingPageHelper {

	private final static String IN_PROPERTIES_PATH = "etc/test/input/CommercialCarrierRefuse.properties";

	private final static String OUT_PROPERTIES_PATH = "etc/test/output/CommercialCarrierRefuseOutput.properties";

	/**
	 * @param className
	 * @param inPropertiesPath
	 * @param outPropertiesPath
	 */
	public CommercialCarrierRefuseTest() {
		super(CommercialCarrierRefuseTest.class.getSimpleName(), null, null);
	}

	@Test
	public void testCommercialCarrierRefuse() throws FileNotFoundException, InterruptedException,
			IOException {
		this.createOrder(IN_PROPERTIES_PATH, OUT_PROPERTIES_PATH);

		Properties inProperties = propertyManager.getPropertiesForPath(IN_PROPERTIES_PATH);

		propertyManager.loadProperties(OUT_PROPERTIES_PATH);
		Properties orderProperties = propertyManager.getPropertiesForPath(OUT_PROPERTIES_PATH);

		schedulingOrderWithCrhPlanner(inProperties, orderProperties, true);
		declineOrderWithUser1(inProperties, orderProperties);
		resetCarrierDeclineWithCrhPlanner(orderProperties);
	}

	/**
	 * Decline an order with user_1 account
	 * @param inProperties the input properties
	 * @param orderProperties the order properties created in the previous step
	 */
	private void declineOrderWithUser1(Properties inProperties, Properties orderProperties) {
		LogonSeleniumTestCaseBase.loginToLogon("/logon/admin/AdminEntry.do", "user_1", "1234");

		// initialize scheduling page
		SchedulingPage schedulingPage = initSchedulePage(
				inProperties.getProperty(TestProperties.SchedulePageKey.SCHEDULING_CARRIER_NAME_1.getKeyString()),
				orderProperties.getProperty(TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));
		schedulingPage.waitForLoading(seleniumDriver);
		schedulingPage.declineOrder(
				orderProperties.getProperty(TestProperties.OrderPageKey.ORDER_NO.getKeyString()),
				inProperties.getProperty(TestProperties.SchedulePageKey.DECLINE_REASON.getKeyString()));
		LogonSeleniumTestCaseBase.logoutOfLogon();
	}

	/**
	 * Reset carrier decline with crh_planner account
	 * @param orderProperties the order properties created in the previous step
	 */
	private void resetCarrierDeclineWithCrhPlanner(Properties orderProperties) {
		LogonSeleniumTestCaseBase.loginToLogon("/logon/admin/AdminEntry.do", "crh_planner", "1234");

		SchedulingPage schedulingPage = LogonPageFactory.initElements(seleniumDriver,
				SchedulingPage.class);
		schedulingPage.selectDay(orderProperties.getProperty(TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));

		WebElement redQuestionMark = seleniumDriver.findElement(
				By.xpath("//div[contains(@class, 'orderCaption') and contains(text(), '"
						+ orderProperties.getProperty(TestProperties.OrderPageKey.ORDER_NO.getKeyString())
						+ "')]//following::img[contains(@src, 'images/exclamation.gif') and contains(@onclick, 'resetCarrierDeclineNote')]"));
		Assert.assertNotNull(redQuestionMark);
		redQuestionMark.click();
		LogonSeleniumTestCaseBase.logoutOfLogon();
	}
}
