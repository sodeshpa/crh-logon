/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.scheduling.pages;

import java.util.Properties;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ch.adnovum.jcan.test.selenium.util.WebElementUtil;

import com.crh.logon.admin.web.pages.LogonBasePage;
import com.crh.logon.admin.web.pages.TestProperties.ExternalOrderKey;

/**
 * @author AdNovum Informatik AG
 */
public class ExternalOrderForm extends LogonBasePage {
	public ExternalOrderForm(WebDriver driver) {
		super(driver);
	}

	/* (non-Javadoc)
	 * @see LogonBasePage#verifyCorrectPage()
	 */
	@Override
	protected void verifyCorrectPage() {
		WebElement externalForm = driver.findElement(By.id("ExternalOrderForm"));
		Assert.assertNotNull(externalForm);
	}

	@FindBy(id = "selectedPoolForExternal")
	private WebElement poolForExternalSelect;

	@FindBy(id = "deliveryFromDatePicker")
	private WebElement deliveryFromDatePickerInput;

	@FindBy(id = "orderQuantityForExtarnal")
	private WebElement orderQuantityForExtarnalInput;

	@FindBy(xpath = "//input[@onclick='saveAndClose();']")
	private WebElement saveAndCloseButton;

	public void selectPoolByName(String poolName) {
		super.selectComboboxByVisibleText(poolForExternalSelect, poolName);
	}

	public void selectCarrierById(String carrierId) {
		super.selectComboboxByValue(poolForExternalSelect, carrierId);
	}

	public void selectShippingpoint(String shippingpointId) {
		super.selectExtJsComboByVarNameAndValue("cmbShippingPointForExternal", shippingpointId, "selectedShippingPointForExternal");
	}

	public void selectMaterial(String materialId) {
		super.selectExtJsComboByVarNameAndValue("cmbMaterialForExternal", materialId, "selectedMaterialForExternal");
	}

	public void selectShipto(String shiptoId) {
		super.selectExtJsComboByVarNameAndValue("cmbShiptoForExternal", shiptoId, "selectedShiptoForExternal");
	}

	public void enterQuantity(String value) {
		orderQuantityForExtarnalInput.clear();
		orderQuantityForExtarnalInput.sendKeys(value);
	}

	public void clickSaveAndCloseButton() {
		saveAndCloseButton.click();
		super.expectSuccessMessageShown(true);
	}

	public String getDeliveryFromDatePickerValue() {
		return deliveryFromDatePickerInput.getAttribute("value");
	}

	/**
	 * Enter loaded quantity
	 * @param quantity loaded quantity
	 */
	public void enterLoadedQuantity(String quantity) {
		WebElement loadedQuantityText = driver.findElement(
				By.id("loadedQuantityForExtarnal"));
		Assert.assertNotNull(loadedQuantityText);
		loadedQuantityText.clear();
		loadedQuantityText.sendKeys(quantity);
	}

	/**
	 * Select unit of measure in the list
	 * @param uom unit of measure
	 */
	public void selectUom(String uom) {
		WebElement uomCombo = driver.findElement(By.name("uomCdLoadedForExternal"));
		Assert.assertNotNull(uomCombo);
		this.selectComboboxByVisibleText(uomCombo, uom);
	}

	/**
	 * Create an external order and return the 
	 * @param inProperties
	 * @return external order result include date / order number
	 */
	public ExternalOrderResult createExternalOrder(Properties inProperties) {
		selectPoolByName(inProperties.getProperty(ExternalOrderKey.POOL_NAME.getKeyString()));
		selectCarrierById(inProperties.getProperty(ExternalOrderKey.CARRIER_ID.getKeyString()));
		selectShippingpoint(inProperties.getProperty(ExternalOrderKey.SHIPPING_POINT_ID.getKeyString()));
		selectShipto(inProperties.getProperty(ExternalOrderKey.SHIPTO.getKeyString()));
		selectMaterial(inProperties.getProperty(ExternalOrderKey.MATERIAL.getKeyString()));
		enterQuantity(inProperties.getProperty(ExternalOrderKey.QUANTITY.getKeyString()));

		ExternalOrderResult externalOrderResult = new ExternalOrderResult();
		externalOrderResult.setDeliveryFromDate(getDeliveryFromDatePickerValue());

		clickSaveAndCloseButton();

		return externalOrderResult;
	}
}
