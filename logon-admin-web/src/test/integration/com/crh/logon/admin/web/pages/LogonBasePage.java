/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.pages;

import static org.openqa.selenium.support.ui.ExpectedConditions.attributeToBe;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

import java.util.Arrays;
import java.util.List;

import ch.adnovum.jcan.test.selenium.util.AdNWebDriverWait;
import ch.adnovum.jcan.test.selenium.util.TestProperties;
import ch.adnovum.jcan.test.selenium.util.TestPropertiesLoader;
import ch.adnovum.jcan.test.selenium.util.TimeoutUtil;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.admin.web.util.LogonSeleniumUtils;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

/**
 * @author AdNovum Informatik AG
 */
public abstract class LogonBasePage {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LogonBasePage.class);

	protected static final long DEFAULT_AJAX_TIMEOUT = 15; // seconds

	private static final long DEFAULT_MAX_LOADING_MESSAGE_TIMEOUT = 30; // seconds

	protected static final String DIV_GRAYOUT = "ext-el-mask";

	protected final WebDriver driver;
	
	protected static TimeoutUtil timeoutUtil = new TimeoutUtil();
	
	protected static final TestPropertiesLoader testProperties = TestProperties.INSTANCE;

	@FindBy(id = "subnavigation")
	protected WebElement subnavigation;

	protected List<WebElement> menuItems;

	// ... Add more @FindBy's that are general to the application

	public LogonBasePage(WebDriver driver) {
		this.driver = driver;
		LOG.info("Navigating to page: " + this.getClass().getSimpleName());
	}

	/**
	 * Override this method to check that we're really on the expected page
	 */
	protected abstract void verifyCorrectPage();

	protected WebElement findMenuLink(String title) {
		WebElement result = null;
		for (WebElement link : menuItems) {
			if (title.equals(link.getText().trim())) {
				result = link;
				break;
			}
		}
		Assert.assertNotNull("Menu item not found: " + title, result);
		return result;
	}

	/**
	 * @see LogonSeleniumUtils#clickAndExpectErrorMsg(WebDriver, By, String)
	 */
	public void clickAndExpectErrorMsg(By elementToBeClicked, String expectedText) {
		LogonSeleniumUtils.clickAndExpectErrorMsg(driver, elementToBeClicked, expectedText);
	}

	/**
	 * @see LogonSeleniumUtils#clickAndExpectSuccessMsg(WebDriver, By, String)
	 */
	public void clickAndExpectSuccessMsg(By elementToBeClicked, String expectedText) {
		LogonSeleniumUtils.clickAndExpectSuccessMsg(driver, elementToBeClicked, expectedText);
	}

	protected boolean isElementPresentAndVisible(WebElement element) {
		boolean isElementVisible = false;
		try {
			isElementVisible = element.isDisplayed();
		}
		catch (NoSuchElementException e) {
			isElementVisible = false;
		}
		catch (NullPointerException e) {
			isElementVisible = false;
		}
		return isElementVisible;
	}

	public void expectErrorMessageShown(boolean remove) {
		LogonSeleniumUtils.expectErrorMessageShown(driver, remove);
	}

	public void expectSuccessMessageShown(boolean remove) {
		LogonSeleniumUtils.expectSuccessMessageShown(driver, remove);
	}

	/**
	 * Input value to the text field
	 * @param id element id
	 * @param value value to be input
	 */
	public void inputTextField(String id, String value) {
		this.inputTextField(By.xpath("//input[@id='" + id + "']"), value);
	}

	/**
	 * Input value to the text field
	 * @param by xpath
	 * @param value value to be input
	 */
	public void inputTextField(By by, String value) {
		new AdNWebDriverWait(driver).until(elementToBeClickable(by));
		WebElement textField = driver.findElement(by);
		Assert.assertNotNull("Cannot find text element", textField);
		textField.clear();
		textField.sendKeys(value);
	}

	/**
	 * Select image check box by id
	 * @param id image check box id
	 */
	public void selectCheckBox(String id) {
		String xpath = "//input[@id='" + id + "']/preceding-sibling::img";
		this.selectCheckBox(By.xpath(xpath));
	}

	/**
	 * Select input check box by By
	 * @param by xpath
	 */
	public void selectCheckBox(By by) {
		new AdNWebDriverWait(driver).until(elementToBeClickable(by));
		WebElement checkbox = driver.findElement(by);
		Assert.assertNotNull("Checkbox is null!", checkbox);
		if (!checkbox.isSelected()) {
			checkbox.click();
		}
	}

	public void selectCheckBox(WebElement checkbox, boolean isSelect) {
		if ((checkbox.isSelected() && !isSelect) || (!checkbox.isSelected() && isSelect)) {
			checkbox.click();
		}
	}

	/**
	 * Select combo box by display text
	 * @param comboboxElement the combo box element
	 * @param value the value would be selected
	 */
	public void selectComboboxByVisibleText(WebElement comboboxElement, String value) {
		Select select = new Select(comboboxElement);
		select.selectByVisibleText(value);
	}

	public void selectComboboxByValue(WebElement comboboxElement, String value) {
		new AdNWebDriverWait(driver, 20).until(visibilityOf(comboboxElement));
		Select select = new Select(comboboxElement);
		select.selectByValue(value);
	}

	/**
	 * Select the value in the combo box but it an input text field
	 * @param id
	 * @param value
	 */
	public void selectComboboxTextField(String id, String value) {
		// search for the row list
		By by = By.id(id);
		new AdNWebDriverWait(driver).until(elementToBeClickable(by));
		WebElement combobox = driver.findElement(by);
		Assert.assertNotNull(combobox);
		combobox.clear();
		combobox.sendKeys(value);
		combobox.sendKeys(Keys.RETURN);
	}

	/**
	 * Select the value in the combo box by the first given number of characters but it's an input text field.
	 * The last 3 characters are typed one by one. Reason: if the input field loses focus, the ext framework does
	 * not trigger search. By typing characters one by one chance is less that the field looses focus. 
	 * @param id
	 * @param value
	 */
	public void searchInComboboxTextFieldByFirstChars(String id, String value) {
		// search for the row list
		By by = By.id(id);
		new AdNWebDriverWait(driver).until(elementToBeClickable(by));
		WebElement combobox = driver.findElement(by);
		Assert.assertNotNull(combobox);
		combobox.clear();
		combobox.sendKeys(value.substring(0, value.length() - 4));
		for (int i = value.length() - 4 ; i < value.length(); i++) {
			String charValue = String.valueOf(value.charAt(i));
			combobox.sendKeys(charValue);
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				LOG.error("Interrupted waiting");
			}
			new Actions(driver).moveToElement(combobox);
		}
	}
	
	public void selectComboboxTextFieldByFirstChars(String id, String value) {
		searchInComboboxTextFieldByFirstChars(id, value);
		By by = By.id(id);
		WebElement combobox = driver.findElement(by);
		if (!combobox.getAttribute("value").equals(value)) {
			LOG.info("The value in the combobox is not the same what was typed in. Expected: " + value + " found: " + combobox.getAttribute("value") + " retry typing in again.");
			//Sometimes what happens is, that the typeahead functionality of the combobox overwrites the first characters typed in. As a result
			//if you want to add 'AD CEM Plant 8101' to the combobox, 'AD AFR Plant 8101' is typed, which is naturally wrong and the test fails,
			//however it is just a test issue. Do here: If the value was not set as expected, give another try.
			searchInComboboxTextFieldByFirstChars(id, value);
			//second time do not retry again, but fail
			Assert.assertEquals("The value in the combobox is not the same what was typed in.", value, combobox.getAttribute("value"));
		}
		combobox.sendKeys(Keys.RETURN);
	}

	/**
	 * Wait for ajax loading message to disappear
	 */
	public void waitForLoading(WebDriver driver) {
		new AdNWebDriverWait(driver, 300)
				.until(invisibilityOfElementLocated(By.id("loading-msg")));
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
	public void selectExtJsCombo(String jsComboVarName, String by, String valueToSelect) {
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

	/**
	 * This method works the same as selectExtJsCombo but for the case that the type of the search field is string.
	 * 
	 * @param jsComboVarName
	 *            Name of an existing (global) JavaScript Ext.form.ComboBox object
	 * @param by
	 *            Field name used for searching
	 * @param valueToSelect
	 *            
	 */
	public void selectExtJsComboWithStringValue(String jsComboVarName, String by,
			String valueToSelect) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			String javaScriptForSelect = "var record = " + jsComboVarName + ".findRecord('" + by
					+ "', '" + valueToSelect + "');" + "var index = " + jsComboVarName
					+ ".store.indexOf(record);" + jsComboVarName + ".onSelect(record, index);";
			js.executeScript(javaScriptForSelect);
			waitForLoading(driver);
		}
		catch (Exception e) {
			Assert.fail("Error selecting combo: " + jsComboVarName);
		}
	}

	/**
	 * Selects a given value of an Ext.form.ComboBox (global JavaScript variable)
	 * 
	 * This method does not make any clicks, but only uses the JS interface of extjs (2.3). That
	 * involves the firing of 'select' event as well.
	 * 
	 * @param jsComboVarName
	 *            Name of an existing (global) JavaScript Ext.form.ComboBox object
	 * @param valueToSelect
	 * @param hiddenFieldId id of the hidden input field
	 *            Should be a number represented as string, e.g. '-1001'.
	 */
	public void selectExtJsComboByVarNameAndValue(String jsComboVarName, String valueToSelect,String hiddenFieldId) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String javaScriptForSelect = "var record = " + jsComboVarName + ".findRecord('value', "
				+ valueToSelect + ");" + "var index = " + jsComboVarName
				+ ".store.indexOf(record);" + jsComboVarName + ".onSelect(record, index);";
		js.executeScript(javaScriptForSelect);
		new AdNWebDriverWait(driver).until(attributeToBe(By.id(hiddenFieldId), "value", valueToSelect));
	}

	/**
	 * 
	 * Selects multiple options from an Ext.form.TriggerField(see example in<b>Order Search/Order Status</b>)
	 * 
	 * @param comboTextFieldId the input combo that visible in page
	 * @param divEditItemsId the div list of all item when click on image icon
	 * @param isSelectAll
	 * @param isSelectNone
	 * @param selectedItems items to check
	 */
	public void selectComboxWithMultipleCheckedItem(String comboTextFieldId, String divEditItemsId,
			boolean isSelectAll, boolean isSelectNone, String... selectedItems) {
		Assert.assertFalse("isSelectAll and isSelectNone can not be both true", isSelectAll
				&& isSelectNone);

		By by = By.id(comboTextFieldId);
		new AdNWebDriverWait(driver).until(elementToBeClickable(by));
		WebElement comboTextField = driver.findElement(by);
		Assert.assertNotNull(comboTextField);

		by = By.id(divEditItemsId);
		WebElement divEditItems = driver.findElement(by);
		Assert.assertNotNull(divEditItems);

		// Get dropbox icon next to combobox
		WebElement dropdownIcon = comboTextField.findElement(By.xpath("./following-sibling::img"));
		dropdownIcon.click();

		// Wait until(ajax completed) div for selected items is displayed
		new AdNWebDriverWait(driver, 30).until(visibilityOf(divEditItems));

		// Element is set, find again
		divEditItems = driver.findElement(by);

		if (isSelectAll) {
			LOG.info("Select all " + comboTextFieldId);
			WebElement allButton = divEditItems.findElement(By.xpath(".//input[@type='button' and contains(@onclick, 'setAllCheckboxes')][1]"));
			allButton.click();
			waitForLoading(driver);
			return;
		}

		if (isSelectNone) {
			LOG.info("Select none " + comboTextFieldId);
			WebElement noneButton = divEditItems.findElement(By.xpath(".//input[@type='button' and contains(@onclick, 'setAllCheckboxes')][2]"));
			noneButton.click();
			waitForLoading(driver);
			return;
		}

		Assert.assertTrue("No item selected", selectedItems.length > 0);

		// Get all row items
		List<WebElement> itemRows = divEditItems.findElements(By.xpath(".//table//tr"));
		for (WebElement itemRow : itemRows) {
			WebElement itemCheckbox = itemRow.findElement(By.xpath(".//input[@type='checkbox']"));
			String itemValue = itemRow.findElement(By.xpath("./td[2]")).getText().trim();

			if (Arrays.asList(selectedItems).contains(itemValue) && !itemCheckbox.isSelected()) {
				itemCheckbox.click();
				waitForLoading(driver);
			}
		}
	}

	/**
	 * Types in the ext combobox the text, and selects the first row of results in the drop-down list. 
	 * 
	 * @param comboBoxTransformElement - the dom element name - input field name 
	 * @param typeText - text to type in the Ext.Combobox, which will trigger its loading
	 * @param columnIndex - if not specified, the first column will be clicked
	 * @param rowIndex - if not specified, the first row will be clicked
	 */
	public final int typeAndSelectIndexInExtCombobox(String comboBoxTransformElement,
			String typeText, String columnIndex, String rowIndex) {
		WebElement combobox = driver.findElement(By.id(comboBoxTransformElement));
		combobox.sendKeys(typeText);
		waitForLoading(driver);

		// wait until table cell (rowIndex, columnIndex) is present:
		// ATTENTION: the browser window must have focus, otherwise the drop-down box might not show up!
		//            in other words, tests might fail if you check your emails while running them ;-)
		String tableCellXPath = "//div[@class='x-combo-list-inner']/table/tbody/tr["
				+ (rowIndex == null ? "1" : rowIndex) + "]/td["
				+ (columnIndex == null ? "1" : columnIndex) + "]";

		WebElement tableCell = driver.findElement(By.xpath(tableCellXPath));

		// count the number of result rows in the combobox drop-down menu: 
		String tableRows = "//div[contains(@class,'x-layer x-combo-list')]/descendant::tr[contains(@class,'listitem zebra')]";
		final int countOfRows = driver.findElements(By.xpath(tableRows)).size();
		LOG.debug("There are " + countOfRows + " table rows in the combobox drop-down list.");

		tableCell.click();

		return countOfRows;

	}

	/**
	 * Types in the ext combobox the text, and selects the first entry that matches one or more Strings.
	 * 
	 * @param comboBoxTransformElement - the dom element name - input field name 
	 * @param typeText - text to type in the Ext.Combobox, which will trigger its loading
	 * @param lookForColumnTexts 
	 */
	public final int typeAndSelectIndexInExtComboboxColumnFields(String comboBoxTransformElement,
			String typeText, String... lookForColumnTexts) {
		WebElement combobox = driver.findElement(By.id(comboBoxTransformElement));
		combobox.sendKeys(typeText);

		String textClause = "";
		for (String text : lookForColumnTexts) {
			textClause += (textClause.equals("") ? "" : " and ") + "contains(string(),'" + text
					+ "')";
		}
		LOG.debug("textClause: " + textClause);

		String matchingRowsXPath = "//div[@class='x-combo-list-inner']/descendant::tr["
				+ textClause + "]/td[1]";

		// count the number of result rows in the combobox drop-down menu: 
		final int countOfRows = driver.findElements(By.xpath(matchingRowsXPath)).size();
		LOG.debug("There are " + countOfRows + " table rows in the combobox drop-down list.");

		WebElement firstMatchingRow = driver.findElement(By.xpath(matchingRowsXPath));
		firstMatchingRow.click();

		return countOfRows;

	}
}
