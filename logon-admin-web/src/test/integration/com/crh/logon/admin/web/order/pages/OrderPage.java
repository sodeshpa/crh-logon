/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.order.pages;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.textToBePresentInElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import ch.adnovum.jcan.test.selenium.util.AdNWebDriverWait;
import ch.adnovum.jcan.test.selenium.util.TestProperties;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.admin.web.pages.LogonBasePage;
import com.crh.logon.business.services.utils.Message;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


/**
 * @author AdNovum Informatik AG
 */
public class OrderPage extends LogonBasePage {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(OrderPage.class);

	public static final String SAVE_ORDER_BUTTON_ID = "saveOrderBtn";

	public static final String ON_HOLD_ORDER_LABEL = "On-Hold order";

	public static final String URL = TestProperties.INSTANCE.getApplicationTestUrl()
			+ "/logon/order/bestellung/BestellungShipToEntry.do";

	@FindBy(xpath = "//input[@id='editorList[0].orderItems[0].sapMatId']/following-sibling::input")
	private WebElement materialCombo;

	private final String MAX_REF_NUMBER_XPATH = "//div[@id='divOpenCustorders']/table/tbody/tr/td/table/tbody/tr[1]/td[1]/a";

	public OrderPage(WebDriver driver) {
		super(driver);
		this.driver.get(URL);
	}

	@Override
	public void verifyCorrectPage() {
		Assert.assertTrue("Order page expected!",
				isElementPresentAndVisible(driver.findElement(By.id("ShipToForm"))));
	}

	/**
	 * Select the first shipto which matches the input texts
	 * @param keySearch
	 */
	public void selectShipto(String keySearch) {
		typeAndSelectIndexInExtCombobox("search", keySearch, "1", "1");
		waitUntilMaterialComboIsRendered();
	}

	protected void waitUntilMaterialComboIsRendered() {
		new AdNWebDriverWait(driver, 60).until(visibilityOf(materialCombo));
	}

	public int searchMaterialAndCountResults(String materialName, boolean isServiceMaterial,
			String... lookForTexts) {
		if (isServiceMaterial) {
			WebElement serviceMaterialCheckbox = driver.findElement(
					By.xpath("//div[contains(@class, 'x-form-check-wrap-inner')]"));
			serviceMaterialCheckbox.click();
		}
		materialCombo.clear();
		materialCombo.sendKeys(materialName);

		String textClause = "";
		for (String text : lookForTexts) {
			textClause += (textClause.equals("") ? "" : " and ") + "contains(string(),'" + text
					+ "')";
		}
		LOG.debug("textClause: " + textClause);

		String matchingRowsXPath = "//div[@class='x-combo-list-inner']/descendant::tr["
				+ textClause + "]";
		// count the number of result rows in the combobox drop-down menu:
		timeoutUtil.setImplicitTimeoutToDefault(driver);
		int countOfRows = driver.findElements(By.xpath(matchingRowsXPath)).size();
		LOG.debug("There are " + countOfRows + " table rows in the combobox drop-down list.");

		return countOfRows;
	}

	public void clickSaveOrderButton() {
		WebElement saveOrderButton = driver.findElement(By.id(SAVE_ORDER_BUTTON_ID));
		Assert.assertNotNull(saveOrderButton);
		saveOrderButton.click();
		waitForLoading(driver);
		expectSuccessMessageShown(true);
	}

	public void selectCreatingNewRailPartFromBimodalOrder() {
		String textValue = "Create new rail order for";
		WebElement loaddingPointCombo = driver.findElement(
				By.xpath("//input[@type='hidden' and contains(@id, 'bimodalRailOrderItemId')]/following-sibling::img"));

		Assert.assertNotNull(loaddingPointCombo);

		loaddingPointCombo.click();

		By xpath = By.xpath("//div[contains(@class, 'x-combo-list') and contains(@style, 'visibility: visible')]");
		WebElement comboxBox = new AdNWebDriverWait(driver, 30).until(presenceOfElementLocated(xpath));
		Assert.assertNotNull(comboxBox);

		LOG.info("Waiting for text to be present in dropdown menu (we had timing issues here)");
		new AdNWebDriverWait(driver, 30).until(textToBePresentInElementLocated(xpath, textValue));
		Assert.assertTrue("Dropdown menu contains '" + textValue + "'",
				comboxBox.getText().contains(textValue));

		String xpathLinkForCreatingNewRailOrder = ".//tbody/tr[last()]//nobr[contains(text(), '"
				+ textValue + "')]";

		WebElement itemWillSelect = comboxBox.findElement(By.xpath(xpathLinkForCreatingNewRailOrder));
		Assert.assertNotNull(itemWillSelect);

		LOG.info("The page has been changed sometime(StaleElementReferenceException), need to find the element again?");
		comboxBox.findElement(By.xpath(xpathLinkForCreatingNewRailOrder)).click();

		// Wait until rail order is showed
		new AdNWebDriverWait(driver, 100).until(elementToBeClickable(By.id(SAVE_ORDER_BUTTON_ID)));

		waitForLoading(driver);
	}

	/**
	 *
	 * @param properties
	 * @param additionColumnsText
	 * @return
	 */
	public BimodalOrderResult createBimodalOrderWithTemplate(Properties properties, String... additionColumnsText) {
		selectMaterialContractItemFromCombo("editorList[0].orderItems[0].sapMatId", properties,
				additionColumnsText);

		selectSapAutomaticRelease(false);

		this.resetTemplateDeliveryTime();
		this.selectCreatingNewRailPartFromBimodalOrder();

		this.clickSaveOrderButton();

		WebElement firstOpenOrderAfter = driver.findElement(
				By.xpath(MAX_REF_NUMBER_XPATH));
		long maxCustOrderNrAfter = Long.parseLong(firstOpenOrderAfter.getText());

		LOG.info("bimodal cust order created: " + maxCustOrderNrAfter);
		return getBimodalOrderResult(String.valueOf(maxCustOrderNrAfter));
	}

	private BimodalOrderResult getBimodalOrderResult(String custOrderNo) {
		expandCustomerOrder(custOrderNo);

		List<String> roadDeliveriesOrderNumbers = getListDeliveryOrdersNumber();
		String deliveryFromDate = getOrderDeliveryFromDate();
		String railDeliveryOrderNo = getRailPartOrderNumber();

		BimodalOrderResult bimodalOrderResult = new BimodalOrderResult(custOrderNo, railDeliveryOrderNo,
				roadDeliveriesOrderNumbers);
		bimodalOrderResult.setDeliveryFromDate(deliveryFromDate);

		return bimodalOrderResult;
	}

	/**
	 * Select the first orderNumber
	 * @param custOrderNo
	 */
	private OrderResult getOrderResult(String custOrderNo) {
		expandCustomerOrder(custOrderNo);
		List<String> roadDeliveriesOrderNumbers = getListDeliveryOrdersNumber();
		String deliveryFromDate = getOrderDeliveryFromDate();

		return new OrderResult(roadDeliveriesOrderNumbers.get(0), custOrderNo, deliveryFromDate);
	}

	/**
	 * Expand the customer order so we can see all the order number in the list
	 * @param custOrderNo
	 */
	private void expandCustomerOrder(String custOrderNo) {
		WebElement custOrderLink = driver.findElement(
				By.xpath("//div[@id='divOpenCustorders']//a[contains(@href, 'toggleShowOrdersForCustorder') and contains(text(),'"
						+ custOrderNo + "')]"));
		Assert.assertNotNull(custOrderLink);

		// expand customer order detail
		custOrderLink.click();
		waitForLoading(driver);
	}

	/**
	 * @param roadDeliveriesOrderNumbers
	 */
	private List<String> getListDeliveryOrdersNumber() {
		List<String> roadDeliveriesOrderNumbers = new ArrayList<String>();

		List<WebElement> roadPartOrdersLinks = driver.findElements(By.xpath("//div[@id='divOpenOrdersForCustorder']//a[contains(@href, 'showPopup')]"));
		for (WebElement el : roadPartOrdersLinks) {
			roadDeliveriesOrderNumbers.add(el.getText().trim());
		}

		// open popup single delivery order details
		roadPartOrdersLinks.get(0).click();
		waitForLoading(driver);

		WebElement closeButton= driver.findElement(By.xpath("//input[contains(@onclick,'closePopup')]"));
		Assert.assertNotNull(closeButton);
		closeButton.click();

		new AdNWebDriverWait(driver, 150).until(invisibilityOfElementLocated(By.id("grayout")));

		return roadDeliveriesOrderNumbers;
	}

	/**
	 * @return the delivery from date of an order
	 */
	private String getOrderDeliveryFromDate() {
		WebElement deliveryFromDate = driver.findElement(By.id("deliveryFromDate"));
		Assert.assertNotNull(deliveryFromDate);
		LOG.info("Order delivery from date: " + deliveryFromDate);

		return deliveryFromDate.getAttribute("value");
	}

	/**
	 * @return the rail part order number
	 */
	private String getRailPartOrderNumber() {
		// get rail part number
		WebElement firstSingleOrderRailPart = new AdNWebDriverWait(driver, 30).until(presenceOfElementLocated(
				By.xpath("//div[@id='logonPopup']//a[contains(@href, 'reloadOrder')]")));
		String railDeliveryOrderNo = firstSingleOrderRailPart.getText().trim();
		LOG.info("Bimodal railDeliveryOrderNo: " + railDeliveryOrderNo);

		return railDeliveryOrderNo;
	}

	/**
	 * Work around<br>
	 * Sometime <b>delivery from</b> is after <b>delivery to</b>
	 */
	public void resetTemplateDeliveryTime() {
		WebElement deliveryFrom = driver.findElement(
				By.xpath("//input[contains(@id, 'deliveryFromTime')]"));
		Assert.assertNotNull(deliveryFrom);
		deliveryFrom.clear();
		deliveryFrom.sendKeys("07:00");

		WebElement deliveryTo = driver.findElement(
				By.xpath("//input[contains(@id, 'deliveryToTime')]"));
		Assert.assertNotNull(deliveryTo);
		deliveryTo.clear();
		deliveryTo.sendKeys("17:00");
		deliveryTo.sendKeys(Keys.TAB);
		waitForLoading(driver);
	}

	public void changeDeliveryTime(String orderNumber, String deliveryFromDate,
			String deliveryToDate) {
		driver.findElement(By.linkText(orderNumber)).click();

		WebElement elemFrom = driver.findElement(By.id("deliveryFromDate"));
		elemFrom.clear();
		elemFrom.sendKeys(deliveryFromDate);

		WebElement elemTo = driver.findElement(By.id("deliveryToDate"));
		elemTo.clear();
		elemTo.sendKeys(deliveryToDate);

		// click on 'Speichern und Schliessen' and expect 'Auftrag erfolgreich gespeichert':
		clickAndExpectSuccessMsg(By.xpath("//input[contains(@onclick,'saveCloseOrder')]"),
				Message.lookup(Locale.GERMAN, "orderdetail.message.success.save"));
	}

	/**
	 * TODO 5-Nov-2014/nhattan: big function, splits for re-usable
	 * Creates an order based in the data provided in the properties. All of them must be available.
	 *
	 * @return the order number created
	 * @throws InterruptedException
	 * @throws Exception
	 */
	public final OrderResult createOrderWithSampledata(Properties properties) throws InterruptedException {
		// get the reference number of the first open order (or set it to -1 if no orders are listed):
		long maxCustOrderNrBefore = -1;
		if (driver.findElement(By.id("divOpenCustorders")).getText().trim().isEmpty()) {
			LOG.debug("Before creating a new order, the list of orders was empty.");
		}
		else {
			WebElement firstOpenOrderBefore = driver.findElement(By.xpath(MAX_REF_NUMBER_XPATH));
			maxCustOrderNrBefore = Long.parseLong(firstOpenOrderBefore.getText());
			LOG.debug("Before creating a new order, the first order has custOrderNr: "
					+ maxCustOrderNrBefore);
		}

		// select Material
		selectMaterialContractItemFromCombo("editorList[0].orderItems[0].sapMatId", properties);

		String editorStr = "editorList[0].";
		String itemStr = editorStr + "orderItems[0].";

		selectExtJsCombo(itemStr + "prodPlantCombo", "plantCd",
				properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.OrderPageKey.PRODUCTION_PLANT_CD.getKeyString()));
		selectExtJsComboWithStringValue(editorStr + "delTypeCombo", "txt",
				properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.OrderPageKey.DELIVERY_NAME.getKeyString()));
		selectExtJsComboWithStringValue(itemStr + "uomCombo", "txt",
				properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.OrderPageKey.UOM_TXT.getKeyString()));
		selectExtJsCombo(editorStr + "supplyPlantCombo", "plantCd",
				properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.OrderPageKey.SUPPLY_PLANT_CD.getKeyString()));
		selectExtJsComboWithStringValue(editorStr + "spCombo", "shippingpointName",
				properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.OrderPageKey.SHIPPINGPOINT_NAME.getKeyString()));
		selectExtJsComboWithStringValue(itemStr + "lpCombo", "loadingpointName",
				properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.OrderPageKey.LOADINGPOINT_NAME.getKeyString()));
		selectExtJsComboWithStringValue(editorStr + "poolCombo", "name",
				properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.OrderPageKey.POOL_NAME.getKeyString()));
		selectExtJsComboWithStringValue(editorStr + "commercialCarrierCombo", "displayName",
				properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.OrderPageKey.CARRIER_NAME.getKeyString()));
		if (properties.containsKey(com.crh.logon.admin.web.pages.TestProperties.OrderPageKey.EXECUTING_CARRIER_NAME.getKeyString())) {
			selectExtJsComboWithStringValue(editorStr + "executingCarrierCombo", "displayName",
					properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.OrderPageKey.EXECUTING_CARRIER_NAME.getKeyString()));
		}
		selectExtJsCombo(editorStr + "billToCombo", "sapBpId",
				properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.OrderPageKey.BILLTO.getKeyString()));
		selectExtJsCombo(editorStr + "payerCombo", "sapBpId",
				properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.OrderPageKey.PAYER.getKeyString()));

		// Delivery From Time
		driver.findElement(By.id("deliveryFromTime[0]")).clear();
		driver.findElement(By.id("deliveryFromTime[0]")).sendKeys(
				properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.OrderPageKey.DELIVERY_FROM_TIME.getKeyString()));

		// Delivery To Time
		driver.findElement(By.id("deliveryToTime[0]")).clear();
		driver.findElement(By.id("deliveryToTime[0]")).sendKeys(
				properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.OrderPageKey.DELIVERY_TO_TIME.getKeyString()));

		// Delivery From Date
		if (properties.containsKey(com.crh.logon.admin.web.pages.TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString())) {
			driver.findElement(By.id("deliveryFromDatStr[0]")).clear();
			driver.findElement(By.id("deliveryFromDatStr[0]")).sendKeys(
					properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.OrderPageKey.DELIVERY_FROM_DATE.getKeyString()));
		}

		// Delivery To Date
		if (properties.containsKey(com.crh.logon.admin.web.pages.TestProperties.OrderPageKey.DELIVERY_TO_DATE.getKeyString())) {
			driver.findElement(By.id("deliveryToDatStr[0]")).clear();
			driver.findElement(By.id("deliveryToDatStr[0]")).sendKeys(
					properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.OrderPageKey.DELIVERY_TO_DATE.getKeyString()));
		}

		// On Hold Order is set to true in input file
		if (properties.containsKey(com.crh.logon.admin.web.pages.TestProperties.OrderPageKey.IS_ON_HOLD_ORDER.getKeyString())) {
			selectComboCheckElement(ON_HOLD_ORDER_LABEL, "editorList[0].checkboxCombo");
		}

		// Automatic Release
		if (properties.containsKey(com.crh.logon.admin.web.pages.TestProperties.OrderPageKey.AUTOMATIC_RELEASE.getKeyString())) {
			selectSapAutomaticRelease(Boolean.valueOf(properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.OrderPageKey.AUTOMATIC_RELEASE.getKeyString())));
		}
		else {
			selectSapAutomaticRelease(false);
		}

		// Order Quantity
		driver.findElement(By.id("editorList[0].orderItems[0].quantityStr")).clear();
		driver.findElement(By.id("editorList[0].orderItems[0].quantityStr")).sendKeys(
				properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.OrderPageKey.QUANTITY.getKeyString()));

		// Auto Assign to Pool
		driver.findElement(By.id("editorList[0].autoPoolAssignmentFlag")).click();

		// Auto Assign to Commercial Carrier
		driver.findElement(By.id("editorList[0].autoCarrierAssignmentFlag")).click();

		// input Contact Person Name
		driver.findElement(By.id("editorList[0].order.contactPersonTxt")).clear();
		driver.findElement(By.id("editorList[0].order.contactPersonTxt")).sendKeys(
				"Selenium Test On The Fly Person");

		// input Delivery Note
		driver.findElement(By.id("editorList[0].orderItems[0].itemNoteDelivery")).sendKeys(
				"Selenium Test Lieferschein Zusatztext");

		// input Scheduling Note
		driver.findElement(By.id("editorList[0].orderItems[0].itemNoteDispatching")).sendKeys(
				"Selenium Test Disposition");

		// input Driver Note
		driver.findElement(By.id("editorList[0].orderItems[0].itemNoteTruckDriver")).sendKeys(
				"Selenium Test Fahrer");
		driver.findElement(By.id("editorList[0].orderItems[0].itemNoteRailway")).sendKeys(
				"Selenium Test Spedition");

		// click Save Order button
		clickSaveOrderButton();

		// get the reference number of the first open order and check if it is 1 higher than previously:
		WebElement firstOpenOrderAfter = driver.findElement(By.xpath(MAX_REF_NUMBER_XPATH));
		long maxCustOrderNrAfter = Long.parseLong(firstOpenOrderAfter.getText());

		LOG.debug("Create successful order with order number: " + maxCustOrderNrBefore);

		return getOrderResult(String.valueOf(maxCustOrderNrAfter));
	}

	public void selectSapAutomaticRelease(boolean isWanttoSelect) {
		// click on the the drop-down <img>, which is the (second) following sibling of the <input> combobox
		WebElement triggerImg = driver.findElement(
				By.xpath("//input[@id='editorList[0].checkboxCombo']/following-sibling::img"));

		Assert.assertNotNull(
				"Element not found: //input[@id='editorList[0].checkboxCombo']/following-sibling::img",
				triggerImg);
		triggerImg.click();

		/**
		 * Clicks on the list entry with given text in the currently visible combobox dropdown list. 
		 */
		LOG.debug("Trying to click on 'Automatic release' in combobox.");

		// get the outer <div> with the triggered drop-down list (assumption: only one is visible at any time):
		String dropDownListXPath = "//div[contains(@class,'x-layer x-combo-list') and contains(@style, 'visible')]";
		WebElement dropDownList = driver.findElement(By.xpath(dropDownListXPath));

		// get all the inner list items and try to find the one with matching text:
		List<WebElement> listItems = dropDownList.findElements(By.xpath(".//div[contains(@class, 'x-panel-body')]/div[contains(@class, 'x-panel')]"));

		WebElement foundElem = null;
		String itemStr = "";

		for (WebElement elem : listItems) {
			itemStr += elem.getText() + ", ";

			WebElement label = elem.findElement(By.xpath(".//label[contains(@class, 'x-form-cb-label')]"));

			if (label.getAttribute("for").contains("autoReleaseFlag")) {
				foundElem = elem;
				break;
			}
		}

		Assert.assertNotNull("No list item 'Automatic release' found in {" + itemStr + "}",
				foundElem);

		WebElement checkbox = foundElem.findElement(By.xpath(".//input[@type='checkbox']"));
		WebElement triggerImgCheckbox = foundElem.findElement(By.xpath(".//img[contains(@class, 'x-form-check')]"));

		if ((checkbox.isSelected() && !isWanttoSelect)
				|| (!checkbox.isSelected() && isWanttoSelect)) {
			triggerImgCheckbox.click();
			waitForLoading(driver);
		}
	}

	/**
	 * Properties file should contains following keys: sapContractNumber, sapContractItemNo, materialName
	 * @param materialComboFieldId
	 * @param properties
	 */
	public void selectMaterialContractItemFromCombo(String materialComboFieldId,
			Properties properties, String... additionColumnsText) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String triggerInputField = (String) js.executeScript("return document.getElementById(\""
				+ materialComboFieldId + "\").nextSibling.id;");

		// e.g., 1209037-03(10)
		String typeText = properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.OrderPageKey.SAP_CONTRACT_NUMBER.getKeyString())
				+ properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.OrderPageKey.SAP_CONTRACT_ITEM_NUMBER.getKeyString());

		// We need this to be variable which is similar to the one above because this will be used for XPATH matching 
		String lookForColumnTypeText = properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.OrderPageKey.SAP_CONTRACT_NUMBER.getKeyString())
				+ " "
				+ properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.OrderPageKey.SAP_CONTRACT_ITEM_NUMBER.getKeyString());

		String materialText = properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.OrderPageKey.MATERIAL_NAME.getKeyString());

		List<String> lookForColumnTexts = new ArrayList<String>(Arrays.asList(additionColumnsText));

		lookForColumnTexts.add(lookForColumnTypeText);
		lookForColumnTexts.add(materialText);

		typeAndSelectIndexInExtComboboxColumnFields(triggerInputField, typeText,
				lookForColumnTexts.toArray(new String[lookForColumnTexts.size()]));
		waitForLoading(driver);
	}

	/**
	 * Click on the little drop-down arrow image next to a combo input box
	 * and check the list item with the given label string.
	 *
	 * @param labelString
	 * @param fieldId
	 * @throws InterruptedException
	 */
	public void selectComboCheckElement(String labelString, String fieldId)
			throws InterruptedException {
		// wait until the combobox gets enabled:
		String comboInputXPath = "//input[@id='" + fieldId
				+ "']/following-sibling::input[not(@disabled)]";
		driver.findElement(By.xpath(comboInputXPath));

		// click on the the drop-down <img>, which is the (second) following sibling of the <input> combobox
		String triggerImgXPath = "//input[@id='" + fieldId + "']/following-sibling::img";
		WebElement imgElement = driver.findElement(By.xpath(triggerImgXPath));
		imgElement.click();

		checkFieldAfterTrigger(labelString);
		imgElement.click();
	}

	private void checkFieldAfterTrigger(String text) {
		LOG.debug("Trying to click on '" + text + "' in combobox.");

		// get the outer <div> with the triggered drop-down list (assumption: only one is visible at any time):
		String dropDownListXPath = "//div[contains(@class,'x-layer x-combo-list') and contains(@style, 'visible')]";
		WebElement dropDownList = driver.findElement(By.xpath(dropDownListXPath));

		// get all the inner list items and try to find the one with matching text:
		List<WebElement> listItems = dropDownList.findElements(By.className("x-form-check-wrap-inner"));
		WebElement foundElem = null;
		String itemStr = "";
		for (WebElement elem : listItems) {
			itemStr += elem.getText() + ", ";
			if (elem.getText().contains(text)) {
				foundElem = elem;
				break;
			}
		}

		Assert.assertNotNull("No list item '" + text + "' found in {" + itemStr + "}", foundElem);
		foundElem.click();
	}

	public boolean isSaveButtonEnable() {
		WebElement saveOrderButton = driver.findElement(By.id(SAVE_ORDER_BUTTON_ID));
		Assert.assertNotNull(saveOrderButton);
		return saveOrderButton.isEnabled();
	}

	public boolean isAdjustDeliveriesBtnEnable() {
		WebElement adjustDeliveriesBtn = driver.findElement(
				By.id("adjustDeliveriesBtn"));
		Assert.assertNotNull(adjustDeliveriesBtn);
		return adjustDeliveriesBtn.isEnabled();
	}
}
