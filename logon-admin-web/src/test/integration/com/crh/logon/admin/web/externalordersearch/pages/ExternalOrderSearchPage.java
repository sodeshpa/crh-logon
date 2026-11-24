/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.externalordersearch.pages;

import com.crh.logon.admin.web.scheduling.pages.ExternalOrderForm;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ch.adnovum.jcan.test.selenium.util.TestProperties;
import ch.adnovum.jcan.test.selenium.util.WebElementUtil;

import com.crh.logon.admin.web.pages.LogonBasePage;
import com.crh.logon.admin.web.pages.LogonPageFactory;

public class ExternalOrderSearchPage extends LogonBasePage {

	public static final String URL = TestProperties.INSTANCE.getApplicationTestUrl()
			+ "/logon/order/bestellung/BestellungExternalSearchEntry.do";

	/**
	 * @param driver
	 */
	public ExternalOrderSearchPage(WebDriver driver) {
		super(driver);
		this.driver.get(URL);
	}

	/* (non-Javadoc)
	 * @see LogonBasePage#verifyCorrectPage()
	 */
	@Override
	protected void verifyCorrectPage() {
		String xpath = "//a[contains(@class, 'highlight') and contains(@href, 'bestellung/BestellungExternalSearchEntry.do')]";

		Assert.assertTrue("External Order Search page expected!",
				isElementPresentAndVisible(driver.findElement(By.xpath(xpath))));
	}

	/**
	 * Search an order
	 * @param orderNr wanted to search
	 */
	public void searchAnOrder(String deliveryDate, String orderNr) {
		enterDeliveryFromDate(deliveryDate);
		enterOrderNumber(orderNr);
		clickSearchButton();
	}

	/**
	 * Enter the delivery from date
	 * @param deliveryDate
	 */
	public void enterDeliveryFromDate(String deliveryDate) {
		inputTextField("deliveryFromDatStr", deliveryDate);
	}

	/**
	 * Enter order number to the text field
	 * @param orderNr input order number
	 */
	public void enterOrderNumber(String orderNr) {
		inputTextField("orderNumber", orderNr);
	}

	/**
	 * Click the search button on external order search
	 */
	public void clickSearchButton() {
		WebElement searchButton = driver.findElement(By.id("buttonSearch"));
		Assert.assertNotNull(searchButton);
		searchButton.click();
	}

	/**
	 * Open external order detail
	 * @param orderNr order number
	 * @return external order form
	 */
	public ExternalOrderForm openOrderDetail(String orderNr) {
		WebElement orderRow = driver.findElement(
				By.xpath("//a[contains(@href, 'ExternalOrderEntry.do') and contains(text(), '"
						+ orderNr + "')]"));
		Assert.assertNotNull(orderRow);
		orderRow.click();

		return LogonPageFactory.initElements(driver, ExternalOrderForm.class);
	}
}
