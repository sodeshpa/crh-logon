/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.masterdata.pages;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ch.adnovum.jcan.test.selenium.util.TestProperties;
import ch.adnovum.jcan.test.selenium.util.WebElementUtil;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.pages.LogonBasePage;
import com.crh.logon.admin.web.pages.LogonPageFactory;

/**
 * @author AdNovum Informatik AG
 */
public class ShiptoSearchPage extends LogonBasePage {
	private String url = TestProperties.INSTANCE.getApplicationTestUrl() + "/logon/order/dispokarte/DispokarteShipToEntry.do";
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ShiptoSearchPage.class);
	
	@FindBy(id = "search")
	private WebElement shiptoNameSearchInputField;
	
	public ShiptoSearchPage(WebDriver driver){
		super(driver);
		this.driver.get(url);
	}
	
	@Override
	public void verifyCorrectPage() {
		assertTrue("Subnavigation bar is visible", isElementPresentAndVisible(subnavigation));
		String activeSubnavigation = subnavigation.findElement(By.className("highlight")).getText();
		LOG.info("Active subnavigation menu: " + activeSubnavigation);
		assertTrue("ShipTo sub-navigation is highlighted",
				activeSubnavigation.contains("Ship To"));
	}
	
	public ShiptoGeneralTab searchForShipto(String shiptoSearchKey){
		searchInComboboxTextFieldByFirstChars("search", shiptoSearchKey);
		
		WebElement tableElement = driver.findElement( 
				By.xpath("//div[contains(@class, 'x-combo-list') and contains(@style, 'visibility: visible')]/div[contains(@class, 'x-combo-list-inner')]//table"));
		Assert.assertNotNull(tableElement);
		
		// Get first shipto from search results
		WebElement shiptoRow = tableElement.findElement(By.xpath("./tbody/tr[1]/td[1]"));

		Assert.assertNotNull(shiptoRow);
		shiptoRow.click();
		
		return LogonPageFactory.initElements(driver, ShiptoGeneralTab.class);
	}
}
