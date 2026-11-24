/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.order.pages;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ch.adnovum.jcan.test.selenium.util.TestProperties;
import ch.adnovum.jcan.test.selenium.util.WebElementUtil;

import com.crh.logon.admin.web.pages.LogonBasePage;
import com.crh.logon.admin.web.pages.LogonPageFactory;

/**
 * @author AdNovum Informatik AG
 */
public class OrderShiptoSearchPage extends LogonBasePage {
	public static final String URL = TestProperties.INSTANCE.getApplicationTestUrl() + "/logon/order/bestellung/BestellungShipToEntry.do";
	
	@FindBy(id = "search")
	private WebElement shiptoSearch;

	public OrderShiptoSearchPage(WebDriver driver) {
		super(driver);
		this.driver.get(URL);
	}

	@Override
	public void verifyCorrectPage() {
		Assert.assertTrue("Order page expected!", isElementPresentAndVisible(driver.findElement(By.id("ShipToForm"))));
	}
	
	/**
	 * Select the first shipto which matches the input texts
	 * @param lookForTexts
	 */
	public OrderPage selectShipto(String lookForTexts) {
		typeAndSelectIndexInExtCombobox("search", lookForTexts,  "1", "1");
		
		return LogonPageFactory.initElements(driver, OrderPage.class);
	}

	public void enterShiptoSearch(String text) {
		Assert.assertNotNull("Shipto search is null", shiptoSearch);
		shiptoSearch.clear();
		shiptoSearch.sendKeys(text);
	}
}
