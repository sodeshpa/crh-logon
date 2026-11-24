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
public class TerminalProductTypePage extends LogonBasePage {
	/**
	 * @param driver
	 */
	public TerminalProductTypePage(WebDriver driver) {
		super(driver);
	}
	
	public void verifyCorrectPage() {
		WebElement titleElement = driver.findElement(By.id("productType"));
		Assert.assertNotNull(titleElement);
	}
	
	public TerminalProductPage selectProductType(String productType) {
		WebElement productTypeButton = driver.findElement(By.xpath("//input[contains(@onclick, 'selectProductType') and @value='" + productType + "']"));
		Assert.assertNotNull(productTypeButton);
		productTypeButton.click();
		
		return LogonPageFactory.initElements(driver, TerminalProductPage.class);
	}
}
