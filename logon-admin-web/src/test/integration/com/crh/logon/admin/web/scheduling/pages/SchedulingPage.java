/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.scheduling.pages;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.adnovum.jcan.test.selenium.util.AdNWebDriverWait;
import ch.adnovum.jcan.test.selenium.util.TestProperties;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.admin.web.pages.LogonBasePage;
import com.crh.logon.admin.web.pages.LogonPageFactory;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

public class SchedulingPage extends LogonBasePage {

	public static final String URL = TestProperties.INSTANCE.getApplicationTestUrl()
			+ "/logon/order/dispo/DispoEntry.do";

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SchedulingPage.class);

	@FindBy(name = "selectedPool")
	private WebElement pools;

	@FindBy(id = "DispoDayPicker")
	private WebElement day;

	@FindBy(name = "hourSpan")
	private WebElement hourSpan;

	@FindBy(id = "timegrid")
	private WebElement timeGrid;

	@FindBy(id = "devModeAutoUpdate")
	private WebElement devModeAutoUpdate;

	@FindBy(id = "buttonCreateExternalOrder")
	private WebElement creatingExternalOrderButton;

	/**
	 * @param driver
	 */
	public SchedulingPage(WebDriver driver) {
		super(driver);
		this.driver.get(URL);
	}

	/* (non-Javadoc)
	 * @see LogonBasePage#verifyCorrectPage()
	 */
	@Override
	protected void verifyCorrectPage() {
		String xpath = "//a[contains(@class, 'highlight') and contains(@href, 'dispo/DispoEntry.do')]";

		Assert.assertTrue("Scheduling page expected!",
				isElementPresentAndVisible(driver.findElement(By.xpath(xpath))));
	}

	// try to avoid stale element exceptions by removing the 'auto-update' checkmark:
	public void removeDevModeAutoUpdate() {
		new AdNWebDriverWait(driver, 150).until(elementToBeClickable(devModeAutoUpdate));
		devModeAutoUpdate.click();
	}

	/**
	 * Renew the scheduling page
	 * @return new scheduling page
	 */
	public SchedulingPage renewPage() {
		return LogonPageFactory.initElements(driver, SchedulingPage.class);
	}

	/**
	 * get the assignment part of order.
	 * @param custOrderNr custOrderNr the customer order number.
	 * @return assignment part
	 */
	private Map getAssignmentPartOfOrder(String custOrderNr) {
		WebElement orderElement = driver.findElement(
				By.xpath("//div[@custordernr='" + custOrderNr + "']"));

		Assert.assertNotNull(orderElement);

		WebElement assignmentElement = orderElement.findElement(By.xpath(".."));
		Assert.assertNotNull(assignmentElement);

		JavascriptExecutor js = (JavascriptExecutor) driver;
		ArrayList parts = (ArrayList) js.executeScript("return arguments[0].assignment.parts;",
				assignmentElement);

		for (Object part : parts) {
			Map map = (Map) part;
			Map assignmentPart = (Map) map.get("assignmentPart");
			Object object = assignmentPart.get("custorderNumber");
			if (object != null && custOrderNr.equals(object.toString())) {
				return assignmentPart;
			}
		}
		Assert.assertTrue("Cannot find the assigment part of custOrderNr " + custOrderNr, false);
		return null;
	}

	/**
	 * get the order id base on cust order number.
	 * @param custOrderNr the customer order number.
	 * @return the order id
	 */
	public String getOrderIdBaseOnCustOrderNr(String custOrderNr) {
		Map assignmentPart = getAssignmentPartOfOrder(custOrderNr);
		return assignmentPart.get("orderId").toString();
	}

	/**
	 * verify status of given order.
	 * @param custOrderNr the customer order number
	 * @param expectedStatus expected order status
	 */
	public void verifyOrderStatus(String custOrderNr, String expectedStatus) {
		String orderStatus = getOrderStatusTextBaseOnCustOrderNr(custOrderNr);
		Assert.assertEquals(expectedStatus, orderStatus);
	}

	/**
	 * get the order status base on cust order number.
	 * @param custOrderNr the customer order number.
	 * @return status of the order
	 */
	public String getOrderStatusTextBaseOnCustOrderNr(String custOrderNr) {
		Map assignmentPart = getAssignmentPartOfOrder(custOrderNr);
		return assignmentPart.get("orderStatusTxt").toString();
	}

	/**
	 * Scheduling an order and then forward to loading page
	 * @param orderNr The order number
	 * @param truckId truck id
	 * 
	 * @return assignment id
	 */
	public void schedulingAnOrder(String orderNr, String truckId) {
		LOG.info("Dispatch to the truck with ID: " + truckId);
		// calling ajax service which makes the assignment (instead of a drag-and-drop):

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("DispoAjaxService.createAssignment(" + truckId + "," + orderNr
				+ ",611,15,124,20,false, function (){refreshOrders();refreshAssignments();});");

		waitForLoading(driver);
	}

	/**
	 * Scheduling an external order and then forward to loading page
	 * @param orderNr The order number
	 * @param truckId truck id
	 * 
	 * @return assignment id
	 */
	public void schedulingAnExternalOrder(String orderNr, String truckId) {
		LOG.info("Dispatch to the truck with ID: " + truckId);
		// calling ajax service which makes the assignment (instead of a drag-and-drop):

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("DispoAjaxService.createAssignment(" + truckId + "," + orderNr
				+ ",627,15,168,15,true,false, function (){refreshOrders();refreshAssignments();});");

		waitForLoading(driver);
	}

	/**
	 * move a scheduled order to other truck.
	 * @param orderNr the order number
	 * @param custOrderNr the customer number
	 * @param truckId the truck id
	 */
	public void moveOrderToOtherTruck(String orderNr, String custOrderNr, String truckId) {
		LOG.info("Dispatch to the truck with ID: " + truckId);
		//find order id
		String orderId = getOrderIdBaseOnCustOrderNr(custOrderNr);
		String pattern = "dispoBarOverlay";

		WebElement orderRow = driver.findElement(
				By.xpath("//div[contains(@custordernr, '" + custOrderNr
						+ "')]//div[contains(@class, '" + pattern + "')]"));

		Assert.assertNotNull(orderRow);

		String orderRowId = orderRow.getAttribute("id");
		int index = orderRowId.indexOf(pattern);

		Assert.assertTrue(index == 0);

		//extract assignment id
		String assignmentId = orderRowId.substring(pattern.length() + 1);

		String ajaxStr = "DispoAjaxService.moveAssignment(" + truckId + "," + assignmentId + ","
				+ orderId + ",600, null, function (){refreshOrders();refreshAssignments();});";
		LOG.info("calling: " + ajaxStr);

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(ajaxStr);

		waitForLoading(driver);
	}

	/**
	 * Merge two assignment of two scheduled orders in one truck
	 * @param firstCustOrder first customer order number
	 * @param secondCustOrder second customer order number
	 */
	public void mergeAssignment(String firstCustOrder, String secondCustOrder) {
		String firstAssignmentId = getAssignmentIdByCustOrder(firstCustOrder);
		String secondAssignmentId = getAssignmentIdByCustOrder(secondCustOrder);

		// calling ajax service which merges two assignments (instead of a drag-and-drop):
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("DispoAjaxService.mergeAssignments(" + firstAssignmentId + ","
				+ secondAssignmentId
				+ ",null,false, function (){refreshOrders();refreshAssignments();});");

		waitForLoading(driver);
	}

	/**
	 * Get assignment id by customer order number
	 * @param custOrderNr customer order number
	 * @return assignment part id
	 */
	private String getAssignmentIdByCustOrder(String custOrderNr) {
		Map assignmentPartOfOrder = getAssignmentPartOfOrder(custOrderNr);
		return assignmentPartOfOrder.get("assignmentId").toString();
	}

	/**
	 * Select the scheduled order and then release it
	 * @param custOrderNr the customer order number wanted to release
	 */
	public void releaseOrder(String custOrderNr) {
		selectScheduledOrder(custOrderNr);
		clickReleaseButton();
	}

	/**
	 * Select the scheduled on time order and then release it
	 * @param custOrderNr the customer order number wanted to release
	 */
	public void releaseOnTimeOrder(String custOrderNr) {
		selectScheduledOnTimeOrder(custOrderNr);
		clickReleaseButton();
	}

	/**
	 * Select the scheduled order and then release it
	 * @param custOrderNr the customer order number wanted to release
	 */
	public void releaseOrderByAssignmentId(String assignmentId) {
		selectScheduledOrderByAssignmentId(assignmentId);
		clickReleaseButton();
	}

	/**
	 * Release an external order
	 */
	public void releaseExternalOrder() {
		selectScheduledExternalOrder();
		clickReleaseButton();
	}

	/**
	 * Click arrived button
	 */
	public void clickArriveButton() {
		WebElement arrivedButton = driver.findElement(
				By.xpath("//input[@type='button' and @id='buttonAtShipto']"));
		Assert.assertNotNull(arrivedButton);
		arrivedButton.click();
	}

	/**
	 * Click unload button
	 */
	public void clickUnloadButton() {
		WebElement unloadButton = driver.findElement(
				By.xpath("//input[@type='button' and @id='buttonUnloaded']"));
		Assert.assertNotNull(unloadButton);
		unloadButton.click();
	}

	/**
	 * Click return order finish button
	 */
	public void clickReturnOrderFinishButton() {
		WebElement returnButton = driver.findElement(
				By.xpath("//input[@type='button' and @id='buttonReturned']"));
		Assert.assertNotNull(returnButton);
		returnButton.click();
	}

	/**
	 * Click release button
	 */
	private void clickReleaseButton() {
		WebElement releaseButton = driver.findElement(
				By.xpath("//input[@type='button' and @id='buttonTransmit']"));
		Assert.assertNotNull(releaseButton);
		releaseButton.click();
	}

	public Set<String> getAllAssignmentIds() {
		final Set<String> existingAssignmentIds = new HashSet<String>();

		String assignmentXPath = "//div[contains(@id, 'dispoBarOverlay_') and @class='dispoBarOverlay']";
		final List<WebElement> existingAssignments = driver.findElements(By.xpath(assignmentXPath));

		for (WebElement assignment : existingAssignments) {
			final String assignmentId = assignment.getAttribute("id").substring(16);
			existingAssignmentIds.add(assignmentId);
		}

		return existingAssignmentIds;
	}

	/**
	 * Select the scheduled order and then release it
	 * @param custOrderNr the customer order number wanted to release
	 */
	public void releaseOrderInRealTruckSection(String custOrderNr) {
		selectScheduledOrderInRealTruckSection(custOrderNr);

		clickReleaseButton();
	}

	/**
	 * Select the scheduled order and then deallocate it
	 * @param custOrderNr the customer order number wanted to release
	 */
	public void deallocateOrder(String custOrderNr) {
		LOG.info("Deallocate order: " + custOrderNr);
		selectScheduledOrder(custOrderNr);

		WebElement deAllocateButton = driver.findElement(
				By.xpath("//input[@type='button' and @id='buttonCancel']"));
		Assert.assertNotNull(deAllocateButton);

		deAllocateButton.click();
		waitForLoading(driver);
	}

	/**
	 * Select the pool name in the combo box
	 * @param poolName pool name wanted to select
	 */
	public void selectPool(String poolName) {
		this.selectComboboxByVisibleText(pools, poolName);
		waitForLoading(driver);
	}

	/**
	 * Get the selected pool
	 * @return string of the selected pool
	 */
	public String getPool() {
		Select pool = new Select(driver.findElement(By.name("selectedPool")));
		Assert.assertNotNull(pool);
		WebElement option = pool.getFirstSelectedOption();
		Assert.assertNotNull(option);

		return option.getText();
	}

	/**
	 * Check if the hide planning truck option is selected
	 * @return
	 */
	public boolean isHidePlanningTruckChecked() {
		clickDropdownButton("displayField", "divDisplayEdit");

		WebElement hidePlanningTruckCheckbox = driver.findElement(
				By.id("hidePlanningTrucksCheckbox"));
		Assert.assertNotNull(hidePlanningTruckCheckbox);

		return hidePlanningTruckCheckbox.isSelected();
	}

	/**
	 * Check whether the carrier name is selected
	 * @param carrierName the selected carrier wanted to check
	 * @return true/false
	 */
	public boolean isCarrierChecked(String carrierName) {
		clickDropdownButton("CarriersField", "DivCarriersEdit");

		WebElement checkbox = driver.findElement(
				By.xpath("//td[contains(text(), '" + carrierName
						+ "')]/preceding-sibling::td/input[contains(@checked, 'checked') "
						+ "and contains(@name, 'selectedCarriersCheckbox')]"));
		Assert.assertNotNull(checkbox);

		return checkbox.isSelected();
	}

	/**
	 * Click on dropdown image button
	 */
	private void clickDropdownButton(String comboTextFieldId, String divEditItemsId) {
		WebElement comboTextField = driver.findElement(By.id(comboTextFieldId));
		Assert.assertNotNull(comboTextField);
		WebElement divEditItems = driver.findElement(By.id(divEditItemsId));
		Assert.assertNotNull(divEditItems);
		WebElement dropdownIcon = comboTextField.findElement(By.xpath("./following-sibling::img"));

		dropdownIcon.click();
	}

	/**
	 * Select the carrier check box based on carrier name in the list
	 * 
	 * @param isAll
	 * @param isNone
	 * @param carriers
	 */
	public void selectCarriers(boolean isAll, boolean isNone, String... carriers) {
		selectComboxWithMultipleCheckedItem("CarriersField", "DivCarriersEdit", isAll, isNone,
				carriers);
		waitForLoading(driver);
	}

	/**
	 * Select the plans combo box with options
	 * @param isAll true if you want to select all
	 * @param isNone true if you want to deselect all
	 * @param plants list of the plant name you want to select
	 */
	public void selectPlants(boolean isAll, boolean isNone, String... plants) {
		selectComboxWithMultipleCheckedItem("deliveringPlantsField", "divDeliveringPlantsEdit",
				isAll, isNone, plants);
	}

	/**
	 * Select the time grid value
	 * @param value wanted to select
	 */
	public void selectTimeGrid(String value) {
		Select timeGridSelect = new Select(timeGrid);
		Assert.assertNotNull(timeGridSelect);
		timeGridSelect.selectByVisibleText(value);
	}

	/**
	 * Get the selected time grid
	 * @return selected time grid value
	 */
	public String getTimeGrid() {
		Select timeGridSelect = new Select(timeGrid);
		Assert.assertNotNull(timeGridSelect);
		WebElement selectedOption = timeGridSelect.getFirstSelectedOption();
		Assert.assertNotNull(selectedOption);

		return selectedOption.getText();
	}

	/**
	 * Select the plans combo box with options
	 * @param isAll true if you want to select all
	 * @param isNone true if you want to deselect all
	 * @param status list of the status you want to select
	 */
	public void selectOrderStatus(boolean isAll, boolean isNone, String... status) {
		selectComboxWithMultipleCheckedItem("orderStatusField", "divOrderStatusEdit", isAll,
				isNone, status);
	}

	/**
	 * Select the date you want to see the order
	 * @param date format: 05.11.2014
	 */
	public void selectDay(String date) {
		inputTextField("DispoDayPicker", date);
		day.sendKeys(Keys.TAB);
		waitForLoading(driver);
	}

	/**
	 * Select the hour span combo box based on 24h format
	 * @param value
	 */
	public void selectHourSpan(String value) {
		Select hourSpanSelect = new Select(hourSpan);
		Assert.assertNotNull(hourSpanSelect);
		hourSpanSelect.selectByVisibleText(value);
	}

	/**
	 * Get the selected hour span
	 * @return selected hour span value
	 */
	public String getHourSpan() {
		Select hourSpanSelect = new Select(hourSpan);
		Assert.assertNotNull(hourSpanSelect);
		WebElement selectedOption = hourSpanSelect.getFirstSelectedOption();
		Assert.assertNotNull(selectedOption);

		return selectedOption.getText();
	}

	/**
	 * Select the display combo box
	 * @param value
	 */
	public void selectDisplay(String value) {
		this.selectComboxWithMultipleCheckedItem("displayField", "divDisplayEdit", false, false,
				value);
	}

	/**
	 * Save personal settings
	 */
	public void savePersonalSettings() {
		WebElement button = driver.findElement(
				By.xpath("//input[@id = 'buttonSavePreferences' and not(@disable)]"));
		Assert.assertNotNull(button);
		button.click();
	}

	/**
	 * Reset personal settings
	 */
	public void resetPersonalSettings() {
		WebElement button = driver.findElement(
				By.xpath("//input[@id = 'buttonResetField' and not(@disable)]"));
		Assert.assertNotNull(button);
		button.click();
	}

	/**
	 * Select the scheduled order based on customer order number
	 * @param custOrderNr customer order number wanted to select
	 */
	public void selectScheduledOrder(String custOrderNr) {
		WebElement orderRow = driver.findElement(
				By.xpath("//div[contains(@custordernr, '" + custOrderNr
						+ "')]//div[contains(@class, 'dispoBarOverlay')]"));
		Assert.assertNotNull(orderRow);
		Actions actions = new Actions(driver);
		actions.moveToElement(orderRow);
		actions.click(orderRow);
		actions.perform();
	}

	/**
	 * Select the scheduled on time order based on customer order number
	 * @param custOrderNr customer order number wanted to select
	 */
	public void selectScheduledOnTimeOrder(String custOrderNr) {
		WebElement orderRow = driver.findElement(
				By.xpath("//div[contains(@custordernr, '"
						+ custOrderNr
						+ "')]//div[contains(@class, 'toEr')]/ancestor::div[contains(@class, 'auftragSollIst')]/following-sibling::div[contains(@class, 'dispoBarOverlay')]"));
		Assert.assertNotNull(orderRow);
		orderRow.click();
	}

	/**
	 * select the scheduled external other
	 * @param custOrderNr
	 */
	public void selectScheduledExternalOrder() {
		WebElement orderRow = driver.findElement(
				By.xpath("//div[contains(@class, 'dispoBarOverlay') and contains(@islate, 'false')]"));
		Assert.assertNotNull(orderRow);
		orderRow.click();
	}

	/**
	 * Select the scheduled order based on customer order number
	 * @param custOrderNr customer order number wanted to select
	 */
	public void selectScheduledOrderInRealTruckSection(String custOrderNr) {
		WebElement orderRow = driver.findElement(
				By.xpath("//div[@id='dispocontainer']//div[contains(@custordernr, '" + custOrderNr
						+ "')]//div[contains(@class, 'dispoBarOverlay')]"));
		Assert.assertNotNull(orderRow);
		orderRow.click();
	}

	/**
	 * Select the scheduled order based on assignment id
	 * @param custOrderNr customer order number wanted to select
	 */
	public void selectScheduledOrderByAssignmentId(String assignmentId) {
		String assignmentXPath = "//div[contains(@id, 'dispoBarOverlay_" + assignmentId
				+ "') and @class='dispoBarOverlay']";

		WebElement orderRow = driver.findElement(By.xpath(assignmentXPath));
		Assert.assertNotNull(orderRow);
		orderRow.click();
	}

	/**
	 * doubleclick the scheduled order based on customer order number.
	 * @param custOrderNr customer order number wanted to select
	 */
	public void doubleClickScheduledOrder(String custOrderNr) {
		Actions action = new Actions(driver);

		WebElement orderRow = driver.findElement(
				By.xpath("//div[@custordernr='" + custOrderNr
						+ "']//div[contains(@class, 'dispoBarOverlay')]"));
		Assert.assertNotNull(orderRow);
		action.doubleClick(orderRow).perform();
	}

	/**
	 * Select the unallocated order at the bottom of the page
	 * @param orderNo
	 */
	public void selectUnallocatedOrder(String orderNo) {
		LOG.info("Order to unallocated " + orderNo);
		WebElement orderRow = driver.findElement(
				By.xpath("//div[contains(@class, 'orderCaption') and contains(text(), '" + orderNo
						+ "')]"));
		Assert.assertNotNull(orderRow);
		orderRow.click();
	}

	/**
	 * open the external order detail
	 * @param orderNo wanted to see the detail
	 * @return external order form
	 */
	public ExternalOrderForm doubleClickUnallocatedOrder(String orderNo) {
		Actions action = new Actions(driver);

		WebElement orderRow = driver.findElement(
				By.xpath("//div[contains(@class, 'orderCaption') and contains(text(), '" + orderNo
						+ "')]"));
		Assert.assertNotNull(orderRow);
		action.doubleClick(orderRow).perform();

		return LogonPageFactory.initElements(driver, ExternalOrderForm.class);
	}

	/**
	 * Select the unallocated on time order at the bottom of the page
	 * @param orderNo
	 */
	public void selectUnallocatedOnTimeOrder(String orderNo) {
		WebElement orderRow = driver.findElement(
				By.xpath("//div[contains(@class, 'orderCaption') and contains(text(), '" + orderNo
						+ "')]/ancestor::div[contains(@class, 'loadOtPl')]/div"));
		Assert.assertNotNull(orderRow);
		orderRow.click();
	}

	/**
	 * Choose commercial carrier for the unallocated order
	 * @param custOrderNo the customer order number
	 */
	public void selectCommercialCarriersBelongToCustOrderNo(String custOrderNo, String carrierName) {
		WebElement carrierCombobox = driver.findElement(
				By.xpath("//select[contains(@id, '" + custOrderNo
						+ "') and contains(@onchange, 'setOrderPoolOrCommCarrier')]"));
		Assert.assertNotNull(carrierCombobox);
		this.selectComboboxByVisibleText(carrierCombobox, carrierName);
	}

	/**
	 * Choose commercial carrier for the unallocated order
	 * @param custOrderNo the customer order number
	 */
	public void selectExecuteCarriersBelongToCustOrderNo(String custOrderNo, String carrierName) {
		int TIMES_TO_ATTEMPT = 4;
		int count = 0;
		while (count < TIMES_TO_ATTEMPT) {
			LOG.info("Attempt to select combobox again if there is StaleElementReferenceException: "
					+ (count + 1));
			try {
				WebElement carrierCombobox = driver.findElement(
						By.xpath("//select[contains(@id, '" + custOrderNo
								+ "') and contains(@onchange, 'setOrderExecCarrier')]"));
				Assert.assertNotNull(carrierCombobox);

				this.selectComboboxByVisibleText(carrierCombobox, carrierName);
				break;
			}
			catch (StaleElementReferenceException e) {
				count++;
			}
		}

		if (count == TIMES_TO_ATTEMPT) {
			Assert.fail("Failed to assign order: " + custOrderNo + " to carrier: " + carrierName
					+ ". problem with StaleElementReferenceException");
		}
	}

	/**
	 * Decline an order
	 * @param orderNo the order number wanted to cancel
	 */
	public void declineOrder(String orderNo, String reason) {
		selectUnallocatedOrder(orderNo);
		WebElement declineOrder = driver.findElement(
				By.xpath("//input[@id = 'buttonCancelOrder' and not(@disable)]"));
		Assert.assertNotNull(declineOrder);
		declineOrder.click();
		WebElement inputDialog = driver.findElement(By.id("inputDialogText"));
		Assert.assertNotNull(inputDialog);
		inputDialog.clear();
		inputDialog.sendKeys(reason);
		WebElement dialogSaveButton = driver.findElement(By.id("inputDialogSave"));
		Assert.assertNotNull(dialogSaveButton);
		dialogSaveButton.click();

		waitForLoading(driver);
	}

	/**
	 * Confirm an order
	 * @param orderNo the order number wanted to accept
	 */
	public void confirmOrder(String orderNo) {
		selectUnallocatedOrder(orderNo);
		WebElement confirmOrder = driver.findElement(
				By.xpath("//input[@id = 'buttonAcceptOrder' and not(@disable)]"));
		Assert.assertNotNull(confirmOrder);
		confirmOrder.click();
		waitForLoading(driver);
	}

	/**
	 * Verify if the button is already enable or not
	 * @param buttonId
	 */
	public void verifyEnableButton(String buttonId) {
		WebElement button = driver.findElement(
				By.xpath("//input[@id = '" + buttonId + "' and not(@disable)]"));
		Assert.assertNotNull(button);
	}

	/**
	 * Verify the button is disable
	 * @param buttonId
	 */
	public void verifyDisabledButton(String buttonId) {
		WebElement button = driver.findElement(By.id(buttonId));
		Assert.assertFalse(button.isEnabled());
	}

	/**
	 * Submit order to commercial carrier
	 * @param orderNo the order number wanted to submit
	 */
	public void submitOrderToCarrier(String orderNo) {
		this.selectUnallocatedOrder(orderNo);
		WebElement submitToCarrierButton = driver.findElement(
				By.id("buttonTransmitToCarrier"));
		Assert.assertNotNull(submitToCarrierButton);
		submitToCarrierButton.click();
		waitForLoading(driver);
	}

	/**
	 * verify that a truck contains an order or not.
	 * @param truckId the truck id
	 * @param custOrderNr the cust order number that we want to check
	 * @param isContained the boolean value specifies that truck contains order nor not
	 */
	public boolean isTruckContainsOrder(String truckId, String custOrderNr) {
		WebElement orderRow = driver.findElement(
				By.xpath("//tr[@id='trc" + truckId + "']//div[@custordernr='" + custOrderNr + "']"));

		return orderRow != null;
	}

	/**
	 * Check whether the on time order is released or not
	 * @param custOrderNr
	 * @return true/false
	 */
	public boolean isOnTimeOrderRelease(String custOrderNr) {
		WebElement orderRow = driver.findElement(
				By.xpath("//div[contains(@custordernr, '"
						+ custOrderNr
						+ "')]//div[contains(@class, 'toEr')]/ancestor::div[contains(@class, 'auftragSollIst')]/following-sibling::div[contains(@class, 'dispoBarOverlay')]"));

		if (orderRow != null) {
			return true;
		}

		return false;
	}

	/**
	 * verify that the popup is appeared or not.
	 * @param appear true to appear, false to disappear
	 */
	public void verifyPopup(boolean appear) {
		WebElement popup = driver.findElement(
				By.xpath("//div[@id='logonPopup' and not(contains(@style,'visibility: hidden')) and (node())]"));
		if (appear) {
			Assert.assertNotNull(popup);
		}
		else {
			Assert.assertNull(popup);
		}
	}

	/**
	 * Confirm the release transport
	 * "You are about to release an assignment which is assigned to the truck of another company. Please inform the company if you proceed!"
	 */
	public void confirmReleaseTransport() {
		WebElement saveButton = driver.findElement(By.id("inputDialogSave"));
		Assert.assertNotNull(saveButton);
		saveButton.click();
	}

	/**
	 * Move to external order form
	 * @return external order form page
	 */
	public ExternalOrderForm clickCreatingExternalOrderButton() {
		creatingExternalOrderButton.click();
		waitForLoading(driver);

		return LogonPageFactory.initElements(driver, ExternalOrderForm.class);
	}

	/**
	 * Obtain the order number, assuming that the created order is the last one in the table.
	 * @return last order number
	 */
	public String getLastOrderItemId() {
		WebElement orderNumberElement = driver.findElement(
				By.xpath("//div[contains(@id, 'orderid_')]/div[last()]"));
		Assert.assertNotNull("No orders found", orderNumberElement);
		return orderNumberElement.getText();
	}

	/**
	 * check Executing Carriers select box is enable or disabled.
	 * @param custOrderNr the customer order number
	 * @param status true: check if the select is enabled and vice versa
	 */
	public void verifyExecutingCarrierSelectStatus(String custOrderNr, boolean status) {
		WebElement executingCarrierElement = driver.findElement(
				By.xpath("//select[contains(@id, 'poolExecCarrierIdent_" + custOrderNr + "')]"));
		Assert.assertNotNull(executingCarrierElement);

		String disabled = executingCarrierElement.getAttribute("disabled");
		if (status) {
			Assert.assertNull(disabled);
		}
		else {
			Assert.assertTrue(Boolean.valueOf(disabled));
		}
	}

	/**
	 * Click Enter Shippingpoint Button for Today's Order
	 * @param custOrderNr
	 */
	public void enterShippingPointForScheduledOrder(String custOrderNr) {
		selectScheduledOrder(custOrderNr);
		WebElement enterShippingPointButton = driver.findElement(
				By.id("buttonEnterShippingpoint"));
		Assert.assertNotNull(enterShippingPointButton);
		enterShippingPointButton.click();
		waitForLoading(driver);
	}
}
