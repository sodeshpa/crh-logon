/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.masterdata.road.pages;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import java.util.Properties;

import ch.adnovum.jcan.test.selenium.util.AdNWebDriverWait;
import com.crh.logon.admin.web.pages.LogonBasePage;
import com.crh.logon.admin.web.pages.TestProperties;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * @author AdNovum Informatik AG
 */
public class TruckPage extends LogonBasePage {
	
	@FindBy(xpath = "//input[contains(@onclick, 'addNewTruck')]")
	private WebElement addNewTruckButton;

	@FindBy(id = "characteristic")
	private WebElement characteristic;

	@FindBy(id = "inputTruckName")
	private WebElement truckNameInput;

	@FindBy(id = "popupPlateNr")
	private WebElement popupPlateNrInput;

	@FindBy(id = "popupTaraMeasure")
	private WebElement popupTaraMeasureInput;

	@FindBy(id = "inputMaxMeasure")
	private WebElement maxMeasureInput;

	@FindBy(xpath = "//input[contains(@onclick, 'saveNewTruck')]")
	private WebElement saveNewTruckButton;

	@FindBy(xpath = "//input[contains(@onclick, 'saveTruck')]")
	private WebElement saveTruckButton;

	@FindBy(id = "inputStatus")
	private WebElement statusCheckbox;

	@FindBy(name = "truckPriority")
	private WebElement truckPrioritySelect;

	@FindBy(name = "inputTkategorie")
	private WebElement tkategorieSelect;

	@FindBy(name = "inputLkwType")
	private WebElement lkwTypeSelect;

	@FindBy(name = "inputDustfilter")
	private WebElement dustfilterSelect;

	@FindBy(name = "inputBranding")
	private WebElement brandingSelect;

	@FindBy(name = "inputTrailerSelect")
	private WebElement trailerSelect;

	@FindBy(id = "selectPool")
	private WebElement poolSelect;

	@FindBy(id = "inputPdaFlag")
	private WebElement pdaFlagCheckbox;

	@FindBy(id = "inputVolumeM3")
	private WebElement volumeM3Input;

	public TruckPage(WebDriver driver) {
		super(driver);
	}

	@Override
	public void verifyCorrectPage() {
		String activeSubnavigation = subnavigation.findElement(By.className("highlight")).getText();
		assertThat("MasterData / Road / Truck page expected!", activeSubnavigation,
				containsString("Road"));
	}

	/**
	 * Create new truck with some basic data
	 * @param properties the truck information to create
	 */
	public String createNewTruck(Properties properties) {
		this.clickAddNewTruckButton();

		this.selectComboboxByVisibleText(characteristic,
				properties.getProperty(TestProperties.TruckPageKey.CHARACTERISTIC.getKeyString()));

		// in case of creating planning truck then the sap number is null
		String sapNr = properties.getProperty(TestProperties.TruckPageKey.SAP_NUMBER.getKeyString());
		if (!StringUtils.isEmpty(sapNr)) {
			this.selectSapNumber(sapNr);
		}

		String timestamp = Long.toString(System.currentTimeMillis());
		this.typePopupPlateNr(properties.getProperty(TestProperties.TruckPageKey.PLATE_NUMBER.getKeyString())
				+ timestamp);
		String truckName = properties.getProperty(TestProperties.TruckPageKey.TRUCK_NAME.getKeyString())
				+ timestamp;

		this.typeTruckName(truckName);
		this.typePopupTaraMeasure(properties.getProperty(TestProperties.TruckPageKey.TARE_WEIGHT.getKeyString()));

		this.clickSaveNewTruckButton();
		this.waitForLoading(driver);
		expectSuccessMessageShown(false);

		String truckId = selectTruck(truckName);
		
		this.waitForLoading(driver);
		this.selectPriority(properties.getProperty(TestProperties.TruckPageKey.PRIORITY.getKeyString()));
		this.selectCategoryByName(properties.getProperty(TestProperties.TruckPageKey.CATEGORY.getKeyString()));
		this.selectLkwTypeByText(properties.getProperty(TestProperties.TruckPageKey.TYPE.getKeyString()));
		this.typeMaxMeasure(properties.getProperty(TestProperties.TruckPageKey.MAX_GROSS_WEIGHT.getKeyString()));
		this.assignPool(properties.getProperty(TestProperties.TruckPageKey.POOL.getKeyString()));

		WebElement activeCheckbox = driver.findElement(By.id("inputStatus"));
		Assert.assertNotNull(activeCheckbox);
		activeCheckbox.click();

		if (Boolean.valueOf(properties.getProperty(TestProperties.TruckPageKey.HAS_PDA.getKeyString()))) {
			this.checkForPdaOnTruck(true);
		}
		else {
			this.checkForPdaOnTruck(false);
		}

		this.clickSaveTruckButton();

		return truckId;
	}

	public void clickAddNewTruckButton() {
		addNewTruckButton.click();
	}

	public void clickSaveNewTruckButton() {
		saveNewTruckButton.click();
		waitForLoading(driver);
	}

	public void typeTruckName(String value) {
		truckNameInput.clear();
		truckNameInput.sendKeys(value);
	}

	public void typePopupPlateNr(String value) {
		popupPlateNrInput.clear();
		popupPlateNrInput.sendKeys(value);
	}

	public void typePopupTaraMeasure(String value) {
		popupTaraMeasureInput.clear();
		popupTaraMeasureInput.sendKeys(value);
	}

	public void typeMaxMeasure(String value) {
		maxMeasureInput.clear();
		maxMeasureInput.sendKeys(value);
	}

	public void typeVolumeM3(String value) {
		volumeM3Input.clear();
		volumeM3Input.sendKeys(value);
	}

	public void checkStatus(boolean isSelect) {
		super.selectCheckBox(statusCheckbox, isSelect);
	}

	public void selectPriority(String value) {
		this.selectComboboxByVisibleText(truckPrioritySelect, value);
	}

	public void selectCategoryByName(String categoryName) {
		this.selectComboboxByVisibleText(tkategorieSelect, categoryName);
	}

	public void selectCategoryByValue(String value) {
		this.selectComboboxByValue(tkategorieSelect, value);
	}

	public void selectDustfilterByValue(String value) {
		this.selectComboboxByValue(dustfilterSelect, value);
	}

	public void selectBrandingByValue(String value) {
		this.selectComboboxByValue(brandingSelect, value);
	}

	public void selectLkwTypeByText(String text) {
		this.selectComboboxByVisibleText(lkwTypeSelect, text);
	}

	public void selectLkwTypeByValue(String value) {
		this.selectComboboxByValue(lkwTypeSelect, value);
	}

	public void selectTrailerByName(String trailerName) {
		this.selectComboboxByVisibleText(trailerSelect, trailerName);
	}

	public void assignPool(String poolName) {
		WebElement poolRow = poolSelect.findElement(By.xpath(".//option[@value='" + poolName + "']"));
		Assert.assertNotNull(poolRow);

		// Select pool
		poolRow.click();

		// assign - move to right
		WebElement moveIcon = driver.findElement(
				By.xpath("//a[contains(@onclick, 'moveSelectedRight')]"));
		Assert.assertNotNull(moveIcon);
		moveIcon.click();
	}

	public void unassignPool(String poolName) {
		AdNWebDriverWait wait = new AdNWebDriverWait(driver);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className(DIV_GRAYOUT)));
		WebElement moveBackLink = driver.findElement(
				By.xpath("//a[contains(@onclick, 'moveBackPool') and contains(@onclick, '"
						+ poolName + "')]"));
		moveBackLink.click();
	}

	public void checkForPdaOnTruck(boolean isCheck) {
		AdNWebDriverWait wait = new AdNWebDriverWait(driver);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className(DIV_GRAYOUT)));
		this.selectCheckBox(pdaFlagCheckbox, isCheck);
	}

	public void clickSaveTruckButton() {
		saveTruckButton.click();
		AdNWebDriverWait wait = new AdNWebDriverWait(driver);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className(DIV_GRAYOUT)));
	}

	public boolean isPdaFlagChecked() {
		return pdaFlagCheckbox.isSelected();
	}

	/**
	 * search and select the created truck id in the vehicle list
	 * @param truckName the id will be selected in the vehicle list
	 */
	public String selectTruck(String truckName) {
		AdNWebDriverWait wait = new AdNWebDriverWait(driver);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className(DIV_GRAYOUT)));
		
		// get the truck list
		WebElement truckList = driver.findElement(By.id("trucklist"));
		Assert.assertNotNull(truckList);
		
		By truckNameRow = By.xpath(".//a[contains(text(), '" + truckName + "')]");
		
		// select the created truck based on truckId
		WebElement truckRow = truckList.findElement(truckNameRow);

		Assert.assertNotNull(truckRow);

		String onlick = truckRow.getAttribute("onclick"); // ex: resetTruck(2600)
		String truckId = onlick.replaceAll(".*\\(|\\).*", ""); // get 2600
		
		truckRow.click();

		return truckId;
	}

	/**
	 * Search for sap number and select it
	 * @param truckProperties the truck information to create
	 */
	public void selectSapNumber(String sapNr) {
		this.inputTextField("inputSapVehicleNoDummy", sapNr);

		WebElement listSapNumber = driver.findElement(
				By.xpath("//div[contains(@class, 'x-combo-list') and contains(@style, 'visibility: visible')]/div[contains(@class, 'x-combo-list-inner')]"));
		Assert.assertNotNull(listSapNumber);

		// Get first sap number from search results
		WebElement sapRow = listSapNumber.findElement(By.xpath("//div[contains(@class, 'x-combo-list-item')]"));
		Assert.assertNotNull(sapRow);
		sapRow.click();
	}
}

