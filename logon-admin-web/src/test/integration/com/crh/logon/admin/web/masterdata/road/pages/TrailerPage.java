/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.masterdata.road.pages;

import ch.adnovum.jcan.test.selenium.util.AdNWebDriverWait;
import com.crh.logon.admin.web.pages.LogonBasePage;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * @author AdNovum Informatik AG
 */
public class TrailerPage extends LogonBasePage {

	@FindBy(id = "inputTrailerName")
	private WebElement trailerNameInput;

	@FindBy(id = "popupPlateNr")
	private WebElement popupPlateNrInput;

	@FindBy(id = "popupMaxMeasure")
	private WebElement popupMaxMeasureInput;

	@FindBy(id = "popupTaraMeasure")
	private WebElement popupTaraMeasureInput;

	@FindBy(xpath = "//input[contains(@onclick, 'javascript:addNewTrailer')]")
	private WebElement addNewTrailerButton;

	@FindBy(xpath = "//input[contains(@onclick,'saveNewTrailer()')]")
	private WebElement saveNewTrailerButton;

	@FindBy(id = "saveButton")
	private WebElement saveButton;

	@FindBy(id = "inputStatus")
	private WebElement statusCheckbox;

	@FindBy(id = "inputTrailerTransportType")
	private WebElement trailerTransportType;

	@FindBy(id = "inputTrailerType")
	private WebElement trailerType;

	@FindBy(id = "inputBranding")
	private WebElement branding;

	@FindBy(id = "inputNrChambers")
	private WebElement nrChambers;

	public TrailerPage(WebDriver driver) {
		super(driver);
	}

	@Override
	protected void verifyCorrectPage() {
	}

	public void typeTrailerName(String value) {
		trailerNameInput.clear();
		trailerNameInput.sendKeys(value);
	}

	public void typePopupPlateNr(String value) {
		popupPlateNrInput.clear();
		popupPlateNrInput.sendKeys(value);
	}

	public void typePopupMaxMeasure(String value) {
		popupMaxMeasureInput.clear();
		popupMaxMeasureInput.sendKeys(value);
	}

	public void typePopupTaraMeasure(String value) {
		popupTaraMeasureInput.clear();
		popupTaraMeasureInput.sendKeys(value);
	}

	public void checkStatus(boolean isSelect) {
		super.selectCheckBox(statusCheckbox, isSelect);
	}

	public void selectTrailerTransportType(String value) {
		super.selectComboboxByValue(trailerTransportType, value);
	}

	public void selectTrailerType(String value) {
		super.selectComboboxByValue(trailerType, value);
	}

	public void selectBranding(String value) {
		super.selectComboboxByValue(branding, value);
	}

	public void selectNrChambers(String value) {
		super.selectComboboxByValue(nrChambers, value);
	}

	public void clickAddNewTrailerButton() {
		addNewTrailerButton.click();
	}

	public void clickSaveNewTrailerButton() {
		saveNewTrailerButton.click();
	}

	public void clickSaveTrailerButton() {
		saveButton.click();
		waitForLoading(driver);
	}

	public void selectTrailer(String trailerName) {

		By trailerNameRow = By.xpath("//a[contains(string(),'" + trailerName + "')]");
		AdNWebDriverWait wait = new AdNWebDriverWait(driver);
		wait.until(ExpectedConditions.elementToBeClickable(trailerNameRow));

		WebElement link = driver.findElement(trailerNameRow);
		link.click();
	}

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
