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

/**
 * @author AdNovum Informatik AG
 */
public class TerminalReviewOrderPage extends LogonBasePage {
	
	
	@FindBy(xpath = "//input[contains(@onclick, 'cancelCreateOrder')]")
	private WebElement cancelOrderButton;
	
	@FindBy(xpath = "//input[contains(@onclick, 'addNewItem')]")
	private WebElement addNewItemButton;
	
	@FindBy(xpath = "//input[contains(@onclick, 'logout')]")
	private WebElement logoutButton;
	
	/**
	 * @param driver
	 */
	public TerminalReviewOrderPage(WebDriver driver) {
		super(driver);
	}
	
	
	public void verifyCorrectPage() {
		WebElement orderTableElement = driver.findElement(By.xpath("//table[@id='pickupOrderItemTable']"));
		Assert.assertNotNull(orderTableElement);
	}
	
	public void clickCancelButton(){
		cancelOrderButton.click();
	}
	
	public void clickAddNewItemButton(){
		addNewItemButton.click();
	}
	
	public void clickLogoutButton(){
		logoutButton.click();
	}
}
