/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.masterdata.pages;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import java.util.List;

import ch.adnovum.jcan.test.selenium.util.AdNWebDriverWait;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.admin.web.pages.LogonPageFactory;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author AdNovum Informatik AG
 */
public class ShiptoSaleAreaTab extends ShiptoPage {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ShiptoSaleAreaTab.class);

	private int orderTemplateNumberInList;
	private WebElement orderTemplateRow;
	
	@FindBy(xpath = "//input[@id='materialComboSelectedCompositeIdentifier']/following-sibling::input")
	private WebElement materialSearchTextField;
	
	public static int TAB_INDEX = 0;
	
	public ShiptoSaleAreaTab(WebDriver driver){
		super(driver);
	}
	
	/**
	 * @return the orderTemplateNumberInList
	 */
	public int getOrderTemplateNumberInList() {
		return orderTemplateNumberInList;
	}


	@Override
	public void verifyCorrectPage() {
		String tabXpath = "//a[@id='TabSalesArea" + TAB_INDEX + "' and contains(@class,'highlight')]";
		WebElement currentTab = driver.findElement(By.xpath(tabXpath));
		Assert.assertNotNull(currentTab);
	}
	
	public ShiptoSaleAreaTab saveSetting(){
		saveButton.click();
		return LogonPageFactory.initElements(driver, ShiptoSaleAreaTab.class);
	}
	
	public boolean isMaterialSearchTextFieldVailable(){
		return materialSearchTextField.isDisplayed();
	}
	
	/**
	 * Search material: "ADN Normo 4 Bag 25"
	 */
	public void searchMaterial(String materialName, String... materialContracts) {
		// Scroll down to the bottom of page
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollTo(0, document.body.scrollHeight); return true");
		
		// Select material with name
		materialSearchTextField.sendKeys(materialName);
		WebElement materialResult = driver.findElement(By.xpath("//div[contains(@class, 'x-combo-list') and contains(@style, 'visibility: visible')]"));
		Assert.assertNotNull(materialResult);
		
		// Select first material from results
		WebElement materialFirstRow = null;
		if(materialContracts.length == 0){
			materialFirstRow = materialResult.findElement(By.xpath(".//tbody//tr[1]"));
		}
		else {
			String textClause = "";
			for (String text : materialContracts) {
				textClause += (textClause.equals("") ? "" : " and ") + "contains(string(),'" + text + "')";
			}
			materialFirstRow = materialResult.findElement(By.xpath("./descendant::tr[" + textClause + "]/td[1]"));
		}
		
		Assert.assertNotNull(materialFirstRow);
		materialFirstRow.click();
		waitForLoading(driver);
		
		// Located new template that just created, if there are some existing templates, check if the one with empty production plant
		this.orderTemplateRow = driver.findElement( 
				By.xpath("//tr[contains(@id, 'restrictionTr')]/td[position()=3]/nobr[contains(text(), '" + materialName + "')]/div/input[contains(@id, 'productionPlantCd')]/following-sibling::input[contains(@class, 'x-form-empty-field')]/ancestor::tr[1]"));
		Assert.assertNotNull(orderTemplateRow);
		
		this.orderTemplateNumberInList = Integer.valueOf(orderTemplateRow.getAttribute("id").substring("restrictionTr".length()));
	}
	
	public void enterMaterialSearch(String text) {
		String materialInputId = "materialComboSelectedCompositeIdentifier";
			WebElement materialCombobox = driver.findElement(By.xpath("//input[@id='"
					+ materialInputId + "']/following-sibling::input"));	
			
			Assert.assertNotNull("Cannot find material search box", materialCombobox);
			materialCombobox.sendKeys(text);
	}
	
	public void enterDataForSelectTag(Template_Element materialElement, String value){
		LOG.info("Entering data for dropdown menu '" + materialElement.name() + "', value = '" + value + "'");
		WebElement image = orderTemplateRow.findElement(By.xpath(".//input[@id='" + materialElement.getHtmlTagId() + orderTemplateNumberInList + "']/following-sibling::img"));
		Assert.assertNotNull(image);
		image.click();

		By xpath = By.xpath("//div[contains(@class, 'x-combo-list') and contains(@style, 'visibility: visible')]");
		WebElement comboxBox = (new AdNWebDriverWait(driver, 30)).until(presenceOfElementLocated(xpath));
		Assert.assertNotNull(comboxBox);

		/* 
		 TODO 01 Oct 2014/nhattan: LOG.info("Waiting for text to be present in dropdown menu (we had timing issues here)");
		new WebDriverWait(driver, 30).until(ExpectedConditions.textToBePresentInElement(xpath, value));
		Assert.assertTrue("Dropdown menu contains '" + value + "'", comboxBox.getText().contains(value));
		*/

		WebElement itemWillSelect = comboxBox.findElement(By.xpath(".//div[contains(text(), '" + value + "')]"));
		Assert.assertNotNull(itemWillSelect);
		itemWillSelect.click();
	}
	
	public void enterDataForInputTag(Template_Element materialElement, String value){
		orderTemplateRow.findElement(By.xpath("//input[@id='" + materialElement.getHtmlTagId() + orderTemplateNumberInList + "']")).sendKeys(value);
	}
	
	public void checkForCheckbox(Template_Element materialElement){
		WebElement image = orderTemplateRow.findElement(By.xpath(".//input[@id='" + materialElement.getHtmlTagId() + orderTemplateNumberInList + "']/preceding-sibling::img"));
		Assert.assertNotNull(image);
		image.click();
	}
	
	/**
	 * delete all templates with specify name, not used yet
	 * @param materialName
	 */
	public void deleteExistingTemplate(String materialName) {
		List<WebElement> templates = driver.findElements(By.xpath("//nobr[contains(text(), '" + materialName + "')]/ancestor::tr[1]"));
		int templateNumber = templates.size();
		
		if(templateNumber > 0){
			templates.get(0).findElement(By.xpath(".//td[position()=2]/table/tbody/tr[position()=2]/td/a")).click();
		}
		
		if(templateNumber > 1){
			deleteExistingTemplate(materialName);
		}
	}
	
	public boolean isNewTemplateSaved(String materialName) {
		return driver.findElements(By.xpath("//nobr[contains(text(), '" + materialName + "')]/ancestor::tr[1]")).size() > 0;
	}
	
	public void selectExtJsCombo(ExtJsCombo combo, String value) {
		String extjsComboPrefix = "combosettings[" + orderTemplateNumberInList + "].";
		selectExtJsCombo(extjsComboPrefix + combo.getName(), combo.getSearchField(), value);
	}
	
	public void selectExtJsComboWithStringValue(ExtJsCombo combo, String value) {
		String extjsComboPrefix = "combosettings[" + orderTemplateNumberInList + "].";
		selectExtJsComboWithStringValue(extjsComboPrefix + combo.getName(), combo.getSearchField(), value);
	}
	
	public void selectPossibleBillto(String sapBilltoName){
		WebElement possibleBilltoDiv = driver.findElement( 
				By.xpath("//div[@id='DivHaendler']//td[contains(text(), '" + sapBilltoName + "')]/preceding-sibling::td/input[@type='checkbox']"));
		if(!possibleBilltoDiv.isSelected()){
			possibleBilltoDiv.click();
		}
	}
	
	public enum ExtJsCombo {
		PRODUCTION_PLANT("productionPlantCombo", "plantCd"),
		DELIVERY_TYPE("deliveryTypeCombo", "deliveryTypeCd"),
		UOM("uomCombo", "uomCd"),
		SUPPLY_PLANT("supplyPlantCombo", "plantCd"),
		SHIPPINGPOINT("shippingpointCombo", "shippingpointName"),
		LOADINGPOINT("loadingpointCombo", "loadingpointId"),
		POOL("poolCombo", "poolId"),
		COMMERCIAL_CARRIER("commercialCarrierCombo", "carrierId"),
		BILL_TO("billtoCombo", "sapBpId"),
		PAYER("payerCombo", "sapBpId");
		
		private String name;
		private String searchField;
		
		private ExtJsCombo(String name, String searchField) {
			this.name = name;
			this.searchField = searchField;
		}
		
		public String getName() {
			return name;
		}
		
		public String getSearchField() {
			return searchField;
		}
	}
	
	public enum Template_Element{
		PRODUCTIONPLANT("productionPlantCd"),
		DELIVERY_TYPE("deliveryTypeCd"),
		QUANLITY("quantity"),
		UOM("uomCd"),
		SUPPLY_PLANT("supplyPlantCd"),
		SHIPPING_POINT("shippingpointId"),
		LOADING_POINT("loadingpointId"),
		POOL("poolId"),
		COMMERCIAL_CARRIER("commercialCarrierId"),
		BILL_TO("billtoSapBpId"),
		PAYER("payerSapBpId"),
		AVAILABLE_IN_PICKUP_APP("availableInPickupAppFlag"),
		AVAILABLE_IN_CUSTWEB_FLAG("availableInCustWebFlag"),
		QUANTITY("quantity"),
		EXACT_CUST_ORDER_QUANTITY_FLAG("exactCustorderQtyFlag"),
		EXACT_SD_ORDER_QUANTITY_FLAG("exactSdorderQtyFlag"),
		AUTO_POOL_FLAG("autoPool"),
		AUTO_CARRIER_FLAG("autoCarrier");
		
		
		private String htmlTagId;
		
		private Template_Element(String htmlTagId){
			this.htmlTagId = htmlTagId;
		}
		
		public String getHtmlTagId(){
			return htmlTagId;
		}
	}

}
