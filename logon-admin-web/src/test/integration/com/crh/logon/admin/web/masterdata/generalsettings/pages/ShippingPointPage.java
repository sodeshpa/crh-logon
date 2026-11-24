/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.masterdata.generalsettings.pages;

import java.util.Calendar;
import java.util.Properties;
import java.util.Random;

import com.crh.logon.admin.web.order.pages.ShippingpointResult;
import com.crh.logon.admin.web.pages.LogonBasePage;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import ch.adnovum.jcan.test.selenium.util.TestProperties;
import ch.adnovum.jcan.test.selenium.util.WebElementUtil;

/**
 * @author AdNovum Informatik AG
 */
public class ShippingPointPage extends LogonBasePage {

	public ShippingPointPage(WebDriver driver) {
		super(driver);
		driver.get(TestProperties.INSTANCE.getApplicationTestUrl() + "/logon/admin/ShippingPointEntry.do");
	}

	@Override
	public void verifyCorrectPage() {
		Assert.assertTrue(
				"Shipping Point page expected!",
				isElementPresentAndVisible(driver.findElement(
						By.xpath("//a[contains(@class, 'highlight') and contains(@href, 'admin/ShippingPointEntry.do')]"))));
	}

	/**
	 * This is the common function for creating a new shipping point <br>
	 * It is <b>needed</b> in other test cases (creating bimodal template test, creating bimodal order, ...)
	 * @param properties the shipping point data will be loaded from properties file
	 */
	public ShippingpointResult createShippingPoint(Properties properties) {
		// search plant and create new shipping point
		selectPlant(properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.ShippingPointKey.PLANT_NAME.getKeyString()));
		selectShippingPoint(properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.ShippingPointKey.SEARCH_SHIPPING_POINT_NAME.SEARCH_SHIPPING_POINT_NAME.getKeyString()));
		
		
		String shippingpointNo = generateShippingpointNo ();
		String shippingpointName = "selenium test shippoint " + shippingpointNo;
		
		// fill information and save shipping point
		this.inputTextField(Element.SAP_SPOINT_NO_TXT.getId(), shippingpointNo);
		
		this.inputTextField(Element.SPOINT_NAME_TXT.getId(), shippingpointName);
		saveNewShippingPoint();
		
		return new ShippingpointResult(shippingpointNo, shippingpointName);
	}
	
	/**
	 * Return shipping point no with format: 'xxxx' with x is a number <br>
	 * Example: 2222
	 * @return
	 */
	private String generateShippingpointNo () {
		final int min = 2000, max = 6000;
		
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		
		Calendar cal = Calendar.getInstance();
		
		// MAX for MINUTE_OF_DAY = 23 * 60  + 59 = 1439
		int MINUTE_OF_DAY = (cal.get(Calendar.HOUR_OF_DAY) * 60) + cal.get(Calendar.MINUTE);
	
		randomNum += MINUTE_OF_DAY;

		return String.valueOf(randomNum);
	}

	public void editShippingPoint(Properties properties) {
		// search plant and create new shipping point
		selectPlant(properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.ShippingPointKey.PLANT_NAME.getKeyString()));
		waitForLoading(driver);
		selectShippingPoint(properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.ShippingPointKey.SHIPPING_POINT_NAME.getKeyString()));
		waitForLoading(driver);
		fillShippingPointDetail(properties);
		saveShippingPointDetail();
	}

	/**
	 * Search for plant name and then select it
	 */
	public void selectPlant(String plantName) {
		this.selectComboboxTextFieldByFirstChars("plants", plantName);
		waitForLoading(driver);
	}

	/**
	 * Search for plant name and then select it
	 */
	public void selectShippingPoint(String shippingPointName) {
		this.selectComboboxTextFieldByFirstChars("shippingPoints", shippingPointName);
	}

	/**
	 * Save new shipping point and expect successful message
	 */
	public void saveNewShippingPoint() {
		WebElement saveButton = driver.findElement(
				By.id(Element.BUTTON_SAVE.getId()));
		Assert.assertNotNull(saveButton);
		saveButton.click();
	}

	/**
	 * Fill the shipping point detail information
	 * @param properties the shipping point detail information
	 */
	public void fillShippingPointDetail(Properties properties) {
		selectShippingPointType(properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.ShippingPointKey.SHIPPING_POINT_TYPE.getKeyString()));
		selectShippingPointCondition(properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.ShippingPointKey.SHIPPING_POINT_CONDITION.getKeyString()));
		this.selectCheckBox(Element.SAP_SPOINT.getId());
		this.inputTextField(Element.COORD_LON.getId(),
				properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.ShippingPointKey.COORD_LON.getKeyString()));
		this.inputTextField(Element.COORD_LAT.getId(),
				properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.ShippingPointKey.COORD_LAT.getKeyString()));
		this.inputTextField(Element.RADIUS.getId(),
				properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.ShippingPointKey.GEOFENCE_RADIUS.getKeyString()));
	}

	/**
	 * Fill the shipping point detail for bimodal information
	 * @param properties
	 */
	public void fillShippingPointDetailForBimodal(Properties properties) {
		fillShippingPointDetail(properties);
		this.inputTextField(Element.RAIL_ARRIVAL_WARNING_MIN.getId(),
				properties.getProperty(com.crh.logon.admin.web.pages.TestProperties.ShippingPointKey.RAIL_ARRIVAL_WARNING_MIN.getKeyString()));
	}

	/**
	 * Save shipping point detail information
	 */
	public void saveShippingPointDetail() {
		WebElement saveButton = driver.findElement(
				By.xpath("//input[contains(@onclick,'submitWithAction') and contains(@onclick,'save')]"));
		Assert.assertNotNull(saveButton);
		saveButton.click();
	}

	/**
	 * Create loading point
	 * @param shippingPointProp 
	 */
	public void createLoadingPoint(Properties shippingPointProp, String rfidName) {
		addNewLoadingPoint();
		this.inputTextField(Element.LOADING_POINT_NAME.getId(),
				shippingPointProp.getProperty(com.crh.logon.admin.web.pages.TestProperties.ShippingPointKey.LOADING_POINT_NAME.getKeyString()));
		String tabsXPath = "//div[@id='LoadingStreetTabs']/ul/li";
		int tabIndex = driver.findElements(By.xpath(tabsXPath)).size() - 1;
		selectPackageType(
				shippingPointProp.getProperty(com.crh.logon.admin.web.pages.TestProperties.ShippingPointKey.PACKAGE_TYPE_CD_LABEL.getKeyString()),
				tabIndex);
		selectWeightSystemType(
				shippingPointProp.getProperty(com.crh.logon.admin.web.pages.TestProperties.ShippingPointKey.WEIGHT_SYSTEM_LP_CD_LABEL.getKeyString()),
				tabIndex);
		uncheckLoadingTimeFlag(tabIndex);
		this.inputTextField("loadingPointWrapperList[" + tabIndex + "].loadingTime",
				shippingPointProp.getProperty(com.crh.logon.admin.web.pages.TestProperties.ShippingPointKey.LOADING_TIME.getKeyString()));
		this.inputTextField(
				By.name("loadingPointWrapperList[" + tabIndex + "].lpEntity.loadingpointComment"),
				shippingPointProp.getProperty(com.crh.logon.admin.web.pages.TestProperties.ShippingPointKey.LOADING_POINT_COMMENT.getKeyString()));
		this.inputTextField(
				By.name("loadingPointWrapperList[" + tabIndex
						+ "].lpEntity.loadingpointObjectLabel"),
				shippingPointProp.getProperty(com.crh.logon.admin.web.pages.TestProperties.ShippingPointKey.LOADING_POINT_OBJECT_LABEL.getKeyString()));
		selectDeliveryNotePrinter(
				shippingPointProp.getProperty(com.crh.logon.admin.web.pages.TestProperties.ShippingPointKey.DELIVERY_NOTE_PRINTER_LABEL.getKeyString()),
				tabIndex);
		//TODO: fix the RFID adding stuff
		//addNewRfidToLoadingPoint(rfidName);
		saveLoadingPoint();
	}

	/**
	 * Add new rfid to loading point
	 * @param rfidName rfid name
	 */
	public void addNewRfidToLoadingPoint(String rfidName) {
		showRfidPopup();
		searchRfidName(rfidName);
		saveRfid();
	}

	private void searchRfidName(String rfidName) {
		this.inputTextField("rfidTagIdDummy", rfidName);
		WebElement listSapNumber = driver.findElement(
				By.xpath("//div[contains(@class, 'x-combo-list') and contains(@style, 'visibility: visible')]/div[contains(@class, 'x-combo-list-inner')]"));
		Assert.assertNotNull(listSapNumber);

		WebElement rfidRow = driver.findElement(By.xpath("//div[contains(text(), '"
				+ rfidName + "')]"));
		Assert.assertNotNull(rfidRow);
		rfidRow.click();
	}

	/**
	 * Show Rfid Popup
	 */
	private void showRfidPopup() {
		WebElement showRfidPopup = driver.findElement(
				By.xpath("//input[contains(@onclick,'javascript:updateObjectLabelShowRFIDPopup')]"));
		Assert.assertNotNull(showRfidPopup);
		showRfidPopup.click();
	}

	/**
	 * Save new rfid to loading point
	 */
	private void saveRfid() {
		WebElement rfidSaveButton = driver.findElement(By.id("rfIdSaveButton"));
		Assert.assertNotNull(rfidSaveButton);
		rfidSaveButton.click();
	}

	/**
	 * Select the delivery note printer
	 * @param deliveryNotePrinterLabel delivery note printer label
	 * @param tabIndex the loading point tab index
	 */
	public void selectDeliveryNotePrinter(String deliveryNotePrinterLabel, int tabIndex) {
		Select printTypeSelect = new Select(driver.findElement(By.name("loadingPointWrapperList["
				+ tabIndex + "].delivNotePrintTypeCd")));
		printTypeSelect.selectByVisibleText(deliveryNotePrinterLabel);
	}

	/**
	 * Select the printer
	 * @param deliveryNotePrinterLabel delivery note printer label
	 * @param tabIndex the loading point tab index
	 */
	public void selectPrinter(String printerLabel, int tabIndex) {
		this.selectComboboxByVisibleText(
				driver.findElement(By.name("loadingPointWrapperList[" + tabIndex + "].printerCd")),
				printerLabel);
	}

	/**
	 * Select shipping point type
	 * @param shippingPointCondition shipping point type label
	 */
	public void selectShippingPointType(String shippingPointType) {
		this.selectComboboxByVisibleText(driver.findElement(By.id(Element.SPOINT_TYPE_SELECT.getId())),
				shippingPointType);
	}

	/**
	 * Select shipping point condition
	 * @param shippingPointCondition shipping point condition label
	 */
	public void selectShippingPointCondition(String shippingPointCondition) {
		this.selectComboboxByVisibleText(driver.findElement(By.name(Element.SPOINT_CONDITION_SELECT.getId())),
				shippingPointCondition);
	}

	/**
	 * @param tabIndex the loading point tab index
	 */
	public void uncheckLoadingTimeFlag(int tabIndex) {
		WebElement loadingPointFlag = driver.findElement(
				By.id("loadingPointWrapperList[" + tabIndex + "].loadingTimeFlag"));
		Assert.assertNotNull(loadingPointFlag);
		loadingPointFlag.click();
	}

	/**
	 * Select the loading point package type
	 * @param packageTypeCdLabel package type code label
	 * @param property the loading point tab index
	 */
	public void selectPackageType(String packageTypeCdLabel, int tabIndex) {
		this.selectComboboxByVisibleText(
				driver.findElement(By.name("loadingPointWrapperList[" + tabIndex
						+ "].packageTypeCd")), packageTypeCdLabel);   
	}

	/**
	 * Select the loading
	 * @param weightSystemSpCdLabel weight system code label
	 * @param tabIndex the loading point tab index
	 */
	public void selectWeightSystemType(String weightSystemSpCdLabel, int tabIndex) {
		this.selectComboboxByVisibleText(
				driver.findElement(By.name("loadingPointWrapperList[" + tabIndex
						+ "].weighSystemLpCd")), weightSystemSpCdLabel);
	}

	/**
	 * Add new loading point by click the green plus
	 */
	public void addNewLoadingPoint() {
		WebElement greenPlus = driver.findElement(
				By.xpath("//form[@id='ShippingPointForm']/nobr/a[1]/img"));
		Assert.assertNotNull(greenPlus);
		greenPlus.click();
	}

	/**
	 * Save loading point information
	 */
	public void saveLoadingPoint() {
		WebElement saveButton = driver.findElement(
				By.xpath("//input[contains(@onclick,'submitWithAction') and contains(@onclick,'save')]"));
		Assert.assertNotNull(saveButton);
		saveButton.click();
	}

	/**
	 * Delete current shipping point
	 */
	public void deleteShippingPoint() {
		WebElement removeLoadingPoint = driver.findElement(
				By.xpath("//a[contains(@href,'javascript:removeLoadingPoint')]"));
		Assert.assertNotNull(removeLoadingPoint);
		removeLoadingPoint.click();
		WebElement okButton = driver.findElement(
				By.xpath("//button[text()='L\u00f6schen']"));
		okButton.click();

		// verify that no loading point tabs are present:
		Assert.assertEquals("", driver.findElement(By.id("DivLoadingStreet")).getText().trim());
	}

	@Override
	public void selectCheckBox(String id) {
		WebElement flag = driver.findElement(By.id(id));
		Assert.assertNotNull("Check box with id " + id + "not found", flag);

		if (!flag.isSelected()) {
			flag.click();
		}
	}
	
	public String getShippingpointNo(){
		WebElement shippingpointNoInput = driver.findElement(By.id("shippingPoint.sapShippingpointNo"));
		Assert.assertNotNull(shippingpointNoInput);
		
		return shippingpointNoInput.getAttribute("value");
	}
	
	public String getShippingpointName(){
		WebElement shippingpointNameInput = driver.findElement(By.id("shippingPoint.shippingpointName"));
		Assert.assertNotNull(shippingpointNameInput);
		
		return shippingpointNameInput.getAttribute("value");
	}

	public enum Element {
		SAP_SPOINT_NO_TXT("shippingPointCreateBean.sapShippingpointNo"),
		SPOINT_NAME_TXT("shippingPointCreateBean.shippingpointName"),
		BUTTON_SAVE("buttonSave"),
		SAP_SPOINT("shippingPoint.knownAlsoInSapFlag"),
		RAIL_ARRIVAL_WARNING_MIN("railArrivalWarningMin"),
		SPOINT_TYPE_SELECT("shippointTypeDD"),
		SPOINT_CONDITION_SELECT("shippingPoint.sapShippingCond.sapShippingCondCd"),
		COORD_LON("coordLon"),
		COORD_LAT("coordLat"),
		RADIUS("radius"),
		LOADING_POINT_NAME("loadingpointName");

		private String id;

		private Element(String id) {
			this.id = id;
		}

		public String getId() {
			return this.id;
		}
	}
}
