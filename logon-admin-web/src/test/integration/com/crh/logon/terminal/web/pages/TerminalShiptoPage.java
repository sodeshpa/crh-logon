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
public class TerminalShiptoPage extends LogonBasePage {
	@FindBy(id = "continueButton")
	private WebElement continueButton;
	
	/**
	 * @param driver
	 */
	public TerminalShiptoPage(WebDriver driver) {
		super(driver);
	}
	
	public void verifyCorrectPage() {
		Assert.assertTrue(continueButton.getAttribute("onclick").contains("selectShipto"));
	}
	
	public void selectShipto(String shiptoName){
		WebElement shiptoOption = driver.findElement(By.xpath("//option[contains(text(),'" + shiptoName + "')]"));
		Assert.assertNotNull(shiptoOption);
		shiptoOption.click();
	}
	
	public TerminalProductTypePage clickContinueButton(){
		continueButton.click();
		return LogonPageFactory.initElements(driver, TerminalProductTypePage.class);
	}
}
