/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ch.adnovum.jcan.test.selenium.util.AdNWebDriverWait;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class LogonSeleniumUtils {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LogonSeleniumUtils.class);

	private static final String ALERT_ERROR = ".alert-error"; // TODO (schwab Aug 26, 2014): adapt to logon app

	private static final String ALERT_SUCCESS = ".x-icon-success";

	private static final String ALERT_CLOSE = ".x-tool-close";	

	/**
	 * Wait for ajax loading message to disappear
	 */
	public static void waitForLoading(WebDriver driver) {
		new AdNWebDriverWait(driver, 150).until(invisibilityOfElementLocated(By.id("loading-msg")));
	}
	
	/**
	 * Selects a given value of an Ext.form.ComboBox (global JavaScript variable)
	 * 
	 * This method does not make any clicks, but only uses the JS interface of extjs (2.3). That
	 * involves the firing of 'select' event as well.
	 * 
	 * @param jsComboVarName
	 *            Name of an existing (global) JavaScript Ext.form.ComboBox object
	 * @param by
	 *            Field name used for searching
	 * @param valueToSelect
	 *            Should be a number represented as string, e.g. '-1001'.
	 */
	public static void selectExtJsCombo(WebDriver driver, String jsComboVarName, String by,			
			String valueToSelect) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			String javaScriptForSelect = "var record = " + jsComboVarName + ".findRecord('" + by
					+ "', " + valueToSelect + ");" + "var index = " + jsComboVarName
					+ ".store.indexOf(record);" + jsComboVarName + ".onSelect(record, index);";
			js.executeScript(javaScriptForSelect);
			waitForLoading(driver);
		}
		catch (Exception e) {
			Assert.fail("Error selecting combo: " + jsComboVarName);
		}
	}

	public static void waitForElementToStopMoving(final WebDriver driver, final WebElement element) {
		new AdNWebDriverWait(driver).until(new ExpectedCondition<Boolean>() {

			private Point prevLocation;

			@Override
			public Boolean apply(WebDriver input) {
				Point location = element.getLocation();
				boolean moved = !location.equals(prevLocation);

				if (moved) {
					prevLocation = location;
				}

				return !moved;
			}

		});
	}

	/**
	 * TODO 5-Nov-2014/nhattan: should merge with isErrorMessageShow function
	 * 
	 * Clicks an element and expects an ERROR message with a given text. All messages are closed after.
	 * 
	 * @param elementToBeClicked A By locator of the element to be clicked that triggers the popup
	 * @param expectedText Expect this message to be shown in the green message popup
	 */
	public static void clickAndExpectErrorMsg(WebDriver driver, By elementToBeClicked, String expectedText) {
		clickAndExpectMsg(driver, elementToBeClicked, expectedText, false);
	}

	/**
	 * TODO 5-Nov-2014/nhattan: should merge with isSuccessMessageShow function
	 * Clicks an element and expects a SUCCESS message with a given text. All messages are closed after.
	 * 
	 * @param elementToBeClicked A By locator of the element to be clicked that triggers the popup
	 * @param expectedText Expect this message to be shown in the green message popup
	 */
	public static void clickAndExpectSuccessMsg(WebDriver driver, By elementToBeClicked, String expectedText) {
		clickAndExpectMsg(driver, elementToBeClicked, expectedText, true);
	}

	/**
	 * TODO 5-Nov-2014/nhattan: should merge with isSuccessMessageShow function
	 * Clicks an element and expects an error/success popup message with a given text. All messages are closed after.
	 * 
	 * @param elementToBeClicked A By locator of the element to be clicked that triggers the popup
	 * @param expectedText Expect this message to be shown in the green message popup
	 */
	private static void clickAndExpectMsg(WebDriver driver, By elementToBeClicked, String expectedText, boolean success) {
		long timeBeforeClicking = System.currentTimeMillis();
		driver.findElement(elementToBeClicked).click();
		waitForLoading(driver);

		// find messages (which are stored in a <div> with class "x-window-body").
		// usually, there is only one such element, but in some cases several are present on the page.
		List<WebElement> xWindowBodyElements = driver.findElements(By.className("x-notification"));

		// check if there is a message containing the expected text:
		if (xWindowBodyElements.isEmpty()) {
			fail("A pop-up message was expected, but none could be found.");
		}

		String msgTxt = "{ ";
		boolean found = false;
		for (WebElement elem : xWindowBodyElements) {
			msgTxt += "\"" + elem.getText() + "\" ";
			if (elem.getText().contains(expectedText)) {
				found = true;
			}
		}
		msgTxt += "}";

		// check if there is a visible error message containing the given text:
		long duration = Math.round(System.currentTimeMillis() - timeBeforeClicking);
		assertTrue("The expected message '" + expectedText + "' did not occur! "
				+ "Instead, " + duration
				+ "ms after clicking, the following message was found: " + msgTxt, found);

		closeAlertMessages(driver);
	}

	public static void expectErrorMessageShown(WebDriver driver, boolean remove) {
		By selector = By.cssSelector(ALERT_ERROR);
		expectAlertMessageShown(driver, selector, remove);
	}

	public static void expectSuccessMessageShown(WebDriver driver, boolean remove) {
		By selector = By.cssSelector(ALERT_SUCCESS);
		expectAlertMessageShown(driver, selector, remove);
	}

	private static void expectAlertMessageShown(WebDriver driver, final By selector, boolean remove) {
		List<WebElement> alertMessages = new AdNWebDriverWait(driver)
				.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(selector));

		List<String> messageTitles = new ArrayList<String>();
		for (WebElement alertMsg : alertMessages) {
			messageTitles.add("\"" + alertMsg.getAttribute("title") + "\"");
		}
		LOG.debug("alert messages on UI: " + StringUtils.join(messageTitles, ", "));

		if (remove) {
			closeAlertMessages(driver);
		}
	}

	private static void closeAlertMessages(WebDriver driver) {
		List<WebElement> closeButtons = driver.findElements(By.cssSelector(ALERT_CLOSE));
		for (WebElement elem : closeButtons) {
			LogonSeleniumUtils.waitForElementToStopMoving(driver, elem);
			elem.click();
		}
	}

}
