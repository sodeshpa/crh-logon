/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.terminal.web.pages;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ch.adnovum.jcan.test.selenium.util.WebElementUtil;

import com.crh.logon.admin.web.pages.LogonBasePage;
import com.crh.logon.admin.web.pages.LogonPageFactory;

/**
 * @author AdNovum Informatik AG
 */
public class TerminalHomePage extends LogonBasePage {
	
	@FindBy(id = "createButton")
	private WebElement createOrderButton;

	/**
	 * @param driver
	 */
	public TerminalHomePage(WebDriver driver) {
		super(driver);
	}
	
	public void verifyCorrectPage() {
		WebElement orderTableHeaderElement = driver.findElement(By.xpath("//form[@id='terminal_PickupForm']"));
		Assert.assertNotNull(orderTableHeaderElement);
	}
	
	public TerminalShiptoPage clickNewOrderButton(){
		createOrderButton.click();
		return LogonPageFactory.initElements(driver, TerminalShiptoPage.class);
	}
}
