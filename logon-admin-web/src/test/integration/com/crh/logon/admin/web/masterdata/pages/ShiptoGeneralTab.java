/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.masterdata.pages;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ch.adnovum.jcan.test.selenium.util.WebElementUtil;

import com.crh.logon.admin.web.pages.LogonPageFactory;

/**
 * @author AdNovum Informatik AG
 */
public class ShiptoGeneralTab extends ShiptoPage {

	@FindBy(id = "shiptoTypeDD")
	protected WebElement shiptoTypeComboBox;

	@FindBy(id = "matChainPlantDD")
	protected WebElement matChainPlant;

	@FindBy(name = "matChainShippingpointId")
	protected WebElement matChainShippingpoint;

	@FindBy(id = "open1From")
	protected WebElement open1From;

	@FindBy(id = "open1To")
	protected WebElement open1To;

	@FindBy(id = "open2From")
	protected WebElement open2From;

	@FindBy(id = "open2To")
	protected WebElement open2To;

	public ShiptoGeneralTab(WebDriver driver) {
		super(driver);
	}

	@Override
	public void verifyCorrectPage() {
		WebElement currentTab = driver.findElement(
				By.xpath("//a[@id='TabShipTo' and contains(@class,'highlight')]"));
		Assert.assertNotNull(currentTab);
	}

	public ShiptoSaleAreaTab switchToSaleAreaTab() {
		ShiptoSaleAreaTab.TAB_INDEX = 0;
		salesAreaTabLink.click();
		waitForLoading(driver);
		return LogonPageFactory.initElements(driver, ShiptoSaleAreaTab.class);
	}

	public ShiptoSaleAreaTab switchToSaleAreaTab(int index) {
		switch (index) {
		case 1:
			ShiptoSaleAreaTab.TAB_INDEX = 1;
			salesAreaTabLink1.click();
			break;
		case 0:
		default:
			ShiptoSaleAreaTab.TAB_INDEX = 0;
			salesAreaTabLink.click();
			break;
		}
		return LogonPageFactory.initElements(driver, ShiptoSaleAreaTab.class);
	}
	
	/**
	 * Switch to sale area tab and select the service material only template in the drop down list 
	 * @return service material only template page
	 */
	public ShiptoSaleAreaTabServiceMaterialTemplate switchToServiceMaterialTemplate() {
		salesAreaTabLink.click();
		return LogonPageFactory.initElements(driver, ShiptoSaleAreaTabServiceMaterialTemplate.class);
	}

	public void selectShiptoType(String shiptoType) {
		selectComboboxByVisibleText(shiptoTypeComboBox, shiptoType);
	}

	public void selectMaterialFlowPlant(String materialFlowPlant) {
		selectComboboxByVisibleText(matChainPlant, materialFlowPlant);
	}

	public void selectmaterialFlowShippingPoint(String materialFlowShippingPoint) {
		selectComboboxByVisibleText(matChainShippingpoint, materialFlowShippingPoint);
	}

	public ShiptoGeneralTab saveSetting() {
		saveButton.click();
		return LogonPageFactory.initElements(driver, ShiptoGeneralTab.class);
	}

	/**
	 * TODO Oct 01, 2014/nhattan: LOGON-8591 We had a problem about time here. Everytime user click on General Data tab, the opening time changes <br>
	 * The warning with message: <b>Opening times: time must be in ascending order</b> is sometime diplayed
	 */
	public void resetOpeningTimes() {
		selectTime(open1From, "01:00");
		selectTime(open1To, "05:00");
		selectTime(open2From, "07:00");
		selectTime(open2To, "11:00");
	}

	private void selectTime(WebElement element, String value) {
		element.clear();
		element.sendKeys(value);
	}
}
