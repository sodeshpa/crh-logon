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
public class TerminalQuantityPage extends LogonBasePage {
	@FindBy(id = "continueButton")
	private WebElement continueButton;
	
	/**
	 * @param driver
	 */
	public TerminalQuantityPage(WebDriver driver) {
		super(driver);
	}
	
	public void verifyCorrectPage() {
		WebElement titleElement = driver.findElement(By.id("inputQuantity"));
		Assert.assertNotNull(titleElement);
	}

	public void selectQuantity(int number){
		WebElement quantityNumberButton = driver.findElement(By.xpath("//input[contains(@onclick, 'selectNumber(" + number + ")')]"));
		Assert.assertNotNull(quantityNumberButton);
		quantityNumberButton.click();
	}
	
	public void clickContinueButton(){
		continueButton.click();
	}
}
