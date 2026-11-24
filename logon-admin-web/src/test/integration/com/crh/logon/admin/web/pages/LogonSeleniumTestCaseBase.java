/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.pages;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.crh.logon.admin.web.util.LogonSeleniumUtils;

import ch.adnovum.jcan.test.selenium.MouseEventListener;
import ch.adnovum.jcan.test.selenium.ResponseLogger;
import ch.adnovum.jcan.test.selenium.SeleniumRule;
import ch.adnovum.jcan.test.selenium.util.AdNWebDriverWait;
import ch.adnovum.jcan.test.selenium.util.TestProperties;
import ch.adnovum.jcan.test.selenium.util.TestPropertiesLoader;
import ch.adnovum.jcan.test.selenium.util.TimeoutUtil;
import ch.adnovum.jcan.test.selenium.util.WebElementUtil;

/**
 * @author AdNovum Informatik AG
 */
@Ignore("Base class, not executable itself")
public class LogonSeleniumTestCaseBase {
	
	@ClassRule
	@Rule
	public static SeleniumRule selenium = new SeleniumRule();

	public static final String LOGON_BASE_URL = TestProperties.INSTANCE.getApplicationTestUrl();
	
	private static final int SUCCESS_MESSAGE_AUTOHIDE_TIMEOUT = 5000;
	
	protected static WebDriver seleniumDriver;

	protected static ResponseLogger responseLoggerEventListener;

	protected static MouseEventListener keyListener;
	
	@BeforeClass
	public static void execBeforeClass() {
		seleniumDriver = selenium.getDriver();
		responseLoggerEventListener = selenium.getResponseLoggerEventListener();
		keyListener = selenium.getKeyListener();
	}
	
	/**
	 * Logging into LOGON via Selenium.
	 * 
	 * @param url       The URL to be access after loggin in (e.g., "/logon/order")
	 * @param name      The username
	 * @param password  The user's password
	 */
	public static void loginToLogon(String url, String name, String password) {
		if (StringUtils.isEmpty(url)) {
			seleniumDriver.get(LOGON_BASE_URL + "/logon/order");
		}
		else {
			seleniumDriver.get(LOGON_BASE_URL + url);
		}
		seleniumDriver.findElement(By.name("j_username")).sendKeys(name);
		seleniumDriver.findElement(By.name("j_password")).sendKeys(password);
		seleniumDriver.findElement(By.name("submit")).click();
	}
	
	public static void loginToLogonAsAdminEN(){
		loginToLogon("/logon/admin/AdminEntry.do", "logonadmin", "logonadmin");
	}
	
	public static void loginToLogonAsAdminDE(){
		loginToLogon("/logon/admin/AdminEntry.do", "logonadmin_de", "logonadmin_de");
	}

	public static void loginToLogonAsPoolManager() {
		loginToLogon("/logon/admin/AdminEntry.do", "crh_planner", "1234");
	}
	
	public static void loginTerminalWebApp(){
		// Login with user customer: adn_role_12 
		loginToLogon("/logon/terminal/TerminalEntry.do?loadingpoint=ADN_PICKUP", "100012", "100012");
	}
	
	public static void logoutTerminalWebApp(){
		seleniumDriver.get(LOGON_BASE_URL + "/logon/terminal/Logout.do");
	}
	
	public static void logoutOfLogon() {
		responseLoggerEventListener.logResponse(seleniumDriver);
		new AdNWebDriverWait(seleniumDriver).until(ExpectedConditions.invisibilityOfElementLocated((By.id("loading-msg-label"))));
		By by = By.linkText("Logout");
		seleniumDriver.findElement(by).click();
		
		// make sure the logout was successful by searching for a 'login' link:
		seleniumDriver.findElement(By.linkText("Login"));
	}
	
	
	/**
	 * @see LogonSeleniumUtils#clickAndExpectSuccessMsg(WebDriver, By, String)
	 */
	public static void clickAndExpectSuccessMsg(By elementToBeClicked, String expectedText) {
		LogonSeleniumUtils.clickAndExpectSuccessMsg(seleniumDriver, elementToBeClicked, expectedText);
	}
	
	
	/**
	 * Selenium WebDriver does not allow .getText() for elements that are hidden. This function makes it
	 * anyway possible by setting the element to visible, reading its text and hiding it again.
	 * 
	 * @param hiddenElemId String The id of the hidden element whose inner text shall be returned
	 * @return Text inside of the given element.
	 */
	public String getHiddenElementTextById(String hiddenElemId) {
		String text = "";
		WebElement hiddenElem = seleniumDriver.findElement(By.id(hiddenElemId));
		Assert.assertFalse("Function should only be applied to hidden elements!", hiddenElem.isDisplayed());
		
		String js_show = "document.getElementById('" + hiddenElemId + "').style.visibility = 'visible'";
		((JavascriptExecutor)seleniumDriver).executeScript(js_show);
		
		Assert.assertTrue("Failed to make hidden element visible.", hiddenElem.isDisplayed());
		text = hiddenElem.getText();
		
		String js_hide = "document.getElementById('" + hiddenElemId + "').style.visibility = 'hidden'";
		((JavascriptExecutor)seleniumDriver).executeScript(js_hide);
		
		Assert.assertFalse("Failed to hide visible element.", hiddenElem.isDisplayed());
		return text;
	}
	
	
	/**
	 * Clicks on a tab with given label (one the first one if several exist).
	 */
	public final void selectTab(String tabLabel) {
		String tabXPath = "//a/span[@class='outer']/span[@class='inner']";
		List<WebElement> tabs = seleniumDriver.findElements(By.xpath(tabXPath));
		String tabLabels = "";
		for (WebElement tab : tabs) {
			String tabText = tab.getText();
			if (tabText.equals(tabLabel)) {
				tab.click();
				return;
			}
			tabLabels += "'" + tabText + "', ";
		}
		
		Assert.fail("Tab '" + tabLabel + "' could not be found. Instead, the following tabs were found: {" + tabLabels + "}");
	}
}
