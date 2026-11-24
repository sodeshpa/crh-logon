/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.loading.pages;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ch.adnovum.jcan.test.selenium.util.TestProperties;
import ch.adnovum.jcan.test.selenium.util.WebElementUtil;

import com.crh.logon.admin.web.pages.LogonBasePage;

public class LoadingPage extends LogonBasePage {

	public static final String URL = TestProperties.INSTANCE.getApplicationTestUrl()
			+ "/logon/order/auftrag/AuftragBearbeitungEntry.do";

	@FindBy(id = "orderListSearchButton")
	private WebElement searchButton;

	/**
	 * @param driver
	 */
	public LoadingPage(WebDriver driver) {
		super(driver);
		this.driver.get(URL);
	}

	/* (non-Javadoc)
	 * @see LogonBasePage#verifyCorrectPage()
	 */
	@Override
	protected void verifyCorrectPage() {
		Assert.assertTrue(
				"Loading page expected!",
				isElementPresentAndVisible(driver.findElement(
						By.xpath("//a[contains(@class, 'highlight')]/span/span[contains(text(), 'Loading')]"))));
	}

	/**
	 * Search for an order by order number
	 * @param orderNr order number
	 */
	public void searchOrder(String orderNr) {
		inputTextField("searchOrderNo", orderNr);
		searchButton.click();
		checkExistedOrder(orderNr);
	}

	/**
	 * Select the loadingPoint after search an order
	 * @param loadingPoint loading point name
	 */
	public void selectLoadingPoint(String loadingPoint) {
		WebElement loadingPointCombo = driver.findElement(
				By.xpath("//select[contains(@id, 'inputLoadingPointId') and contains(@onchange, 'changeWeighSystemButton')]"));
		Assert.assertNotNull(loadingPointCombo);
		selectComboboxByValue(loadingPointCombo, loadingPoint);
	}

	/**
	 * Search the road order number and enter tare weight / net quantity to it
	 * @param properties testing information properties
	 * @param bimodalWorkflowProperties the bimodal work flow information that being saved in some previous step
	 */
	public void enterTareWeightAndNetQuantity(String tareWeight, String netQuantity) {
		enterTareWeight(tareWeight);
		enterNetQuantity(netQuantity);
	}

	/**
	 * Click the green mark to save the row
	 */
	public void checkAndSave() {
		WebElement checkAndSave = driver.findElement(
				By.xpath("//img[contains(@onclick, 'checkAndSave') and contains(@title, 'Save row values')]"));
		Assert.assertNotNull(checkAndSave);
		checkAndSave.click();
	}

	/**
	 * Input tare weight
	 * @param tare weight
	 */
	public void enterTareWeight(String tareWeight) {
		WebElement tareWeightField = driver.findElement(
				By.xpath("//input[contains(@onchange, 'updateGrossOrNetto') and contains(@id, 'inputTaraMeasure')]"));
		enterValue(tareWeight, tareWeightField);
	}

	/**
	 * Input net quantity
	 * @param net quantity
	 */
	public void enterNetQuantity(String netQuantity) {
		WebElement netQuantityField = driver.findElement(
				By.xpath("//input[contains(@onchange, 'updateGrossOrNetto') and contains(@id, 'inputNettoMeasure')]"));
		enterValue(netQuantity, netQuantityField);
	}

	/**
	 * Input net quantity for bag
	 * @param net quantity
	 */
	public void enterNetQuantityForBag(String netQuantity) {
		WebElement netQuantityField = driver.findElement(
				By.xpath("//input[contains(@id, 'inputOrderItemLoadedQuantity')]"));
		enterValue(netQuantity, netQuantityField);
	}

	/**
	 * Select unit of measure
	 * @param uom 
	 */
	public void selectUnitOfMeasure(String uom) {
		WebElement uomCombo = driver.findElement(By.id("bagChooseUomSelect"));
		Assert.assertNotNull(uomCombo);
		selectComboboxByValue(uomCombo, uom);
	}

	/**
	 * @param value the value wanted to enter to the field
	 * @param field web element
	 */
	private void enterValue(String value, WebElement field) {
		Assert.assertNotNull(field);
		field.clear();
		field.sendKeys(value);
	}

	/**
	 * Check the existed order after searching and selecting that order
	 * @param roadPartOrderNr road order number
	 */
	private void checkExistedOrder(String roadPartOrderNr) {
		WebElement orderList = driver.findElement(
				By.xpath("//div[contains(@class, 'x-grid3-body')]//table[contains(@class, 'x-grid3-row-table')]"));
		Assert.assertNotNull(orderList);
		WebElement orderLoadingRow = orderList.findElement(By.xpath("//div[contains(text(), '"
				+ roadPartOrderNr + "')]"));
		Assert.assertNotNull(orderLoadingRow);
		orderLoadingRow.click();
	}

}
