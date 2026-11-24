/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision: $
 * Date of last edit : $Date: $
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
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

public class OnHoldOrderTest extends AbstractSchedulingPageHelper {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(OnHoldOrderTest.class);
	private static final String IN_PROPERTIES_PATH = "etc/test/input/OnHoldOrderTest.properties";
	private static final String OUT_PROPERTIES_PATH = "etc/test/output/OnHoldOrderTest.properties";

	/**
	 * Identifier of Release Button
	 */
	private static final String RELEASE_BUTON_ID = "buttonTransmit";
	
	/**
	 * XPath for the Order link in Order Detail
	 */
	private static final String ORDER_LINK_XPATH = "//div[@id='DivEditAssignment']/table/tbody/tr[1]/td/table/tbody/tr[4]/td[8]/a";

	/**
	 * XPath of the Save and Close button in Order Detail
	 */
	private static final String SAVE_AND_CLOSE_BUTTON_XPATH = "//form[@id='OrderDetailForm']/table/tbody/tr[3]/td/table/tbody/tr/td[3]/nobr/input[3]";

	/**
	 * XPath of On Hold check box in Order Detail
	 */
	private static final String ON_HOLD_CHECKBOX_XPATH = "//div[@id='mainSectionId']/div[1]/table/tbody/tr[5]/td[2]/input";

	public OnHoldOrderTest() {
		super(OnHoldOrderTest.class.getSimpleName(), IN_PROPERTIES_PATH, null);
	}

	@Override
	protected void modifyProperties(Properties properties) {
		
	}
	
	/**
	 * @TestSpec logon-1291:On-hold order
	 * @TestSpec LOGON-8941: Automate Testcase: logon-1291:On-hold order 
	 * 
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Test
	public void testOnHoldOrder() throws FileNotFoundException, InterruptedException,
			IOException {
		// create new order
		createOrder(IN_PROPERTIES_PATH, OUT_PROPERTIES_PATH);
		LOG.info("Order created");

		propertyManager.loadProperties(OUT_PROPERTIES_PATH);
		Properties orderProperties = propertyManager.getPropertiesForPath(OUT_PROPERTIES_PATH);
		String custOrderNo = orderProperties.getProperty(TestProperties.OrderPageKey.CUST_ORDER_NO.getKeyString());
		String orderNo = orderProperties.getProperty(TestProperties.OrderPageKey.ORDER_NO.getKeyString());
		LOG.info("orderNo: " + orderNo);

		//login as admin
		LogonSeleniumTestCaseBase.loginToLogonAsAdminEN();
		// initialize Scheduling page
		SchedulingPage schedulingPage = LogonPageFactory.initElements(seleniumDriver,
				SchedulingPage.class);
		//set search criteria
		schedulingPage.selectPool(getInProperty(TestProperties.SchedulePageKey.POOL_NAME));
		schedulingPage.selectCarriers(true, false);
		schedulingPage.selectDay(orderProperties.getProperty(TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));
		
		// set commercial carrier for order
		schedulingPage.selectCommercialCarriersBelongToCustOrderNo(custOrderNo,
				getInProperty(TestProperties.SchedulePageKey.SCHEDULING_CARRIER_NAME_1));
		LOG.info("Select commercial carrier: " + getInProperty(TestProperties.SchedulePageKey.SCHEDULING_CARRIER_NAME_1));

		// submit the order to carrier
		schedulingPage.submitOrderToCarrier(orderNo);
		// confirm order
		schedulingPage.confirmOrder(orderNo);
		
		// schedule the order
		schedulingPage.schedulingAnOrder(orderNo,
				getInProperty(TestProperties.SchedulePageKey.TRUCK_ID_FOR_CARRIER_NAME_1));
		
		// try to release order
		schedulingPage.selectScheduledOrderInRealTruckSection(custOrderNo);
		schedulingPage.verifyDisabledButton(RELEASE_BUTON_ID);
		schedulingPage.doubleClickScheduledOrder(custOrderNo);
		
		// click on Order Link
		WebElement orderLink = seleniumDriver.findElement(
				By.xpath(ORDER_LINK_XPATH));
		orderLink.click();
		schedulingPage.waitForLoading(seleniumDriver);
		
		// untick On Hold for order check box
		WebElement onHoldCheckBox = seleniumDriver.findElement(
				By.xpath(ON_HOLD_CHECKBOX_XPATH));
		Assert.assertNotNull(onHoldCheckBox);
		onHoldCheckBox.click();
		
		// save and close
		WebElement saveOrderAndCloseButton = seleniumDriver.findElement(
				By.xpath(SAVE_AND_CLOSE_BUTTON_XPATH));
		Assert.assertNotNull(saveOrderAndCloseButton);
		saveOrderAndCloseButton.click();
		schedulingPage.waitForLoading(seleniumDriver);
		
		// release order
		schedulingPage.releaseOrder(custOrderNo);
		LogonSeleniumTestCaseBase.logoutOfLogon();
	}
}
