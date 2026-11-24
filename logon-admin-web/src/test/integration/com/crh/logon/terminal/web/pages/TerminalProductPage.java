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

import ch.adnovum.jcan.test.selenium.util.WebElementUtil;

import com.crh.logon.admin.web.pages.LogonBasePage;
import com.crh.logon.admin.web.pages.LogonPageFactory;

/**
 * @author AdNovum Informatik AG
 */
public class TerminalProductPage extends LogonBasePage {

	/**
	 * @param driver
	 */
	public TerminalProductPage(WebDriver driver) {
		super(driver);
	}
	
	public void verifyCorrectPage() {
		WebElement titleElement = driver.findElement(By.id("compositeId"));
		Assert.assertNotNull(titleElement);
	}
	
	public TerminalQuantityPage selectTemplate(String templateName){
		WebElement productButton = driver.findElement(By.xpath("//input[@value='" + templateName + "']"));
		Assert.assertNotNull(productButton);
		productButton.click();
		
		return LogonPageFactory.initElements(driver, TerminalQuantityPage.class);
	}
	
	public boolean hasTemplate(String templateName){
		WebElement productButton = driver.findElement(By.xpath("//input[contains(@onclick, 'selectProduct') and @value='" + templateName + "']"));
		return productButton != null;
	}
}
