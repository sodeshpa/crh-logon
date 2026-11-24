/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.admin.tablebrowser.pages;

import java.util.List;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ch.adnovum.jcan.test.selenium.util.WebElementUtil;

import com.crh.logon.admin.web.pages.LogonBasePage;

/**
 * @author AdNovum Informatik AG
 */
public abstract class EntryBrowserPage extends LogonBasePage {
	private final String RESULT_TABLE_ROWS_XPATH = "//div[@id='testDiv']/table/tbody/tr";
	
	@FindBy(name = "whereClause")
	private WebElement whereClauseInput;
	
	@FindBy(name = "filter")
	private WebElement filterButton;
	
	@FindBy(xpath = "//form[@id='tableForm']/table[4]/tbody/tr[2]/td/table/tbody/tr/td[1]/a/img")
	private WebElement addIcon;
	
	protected String tableName = "";
	
	/**
	 * @param driver
	 */
	public EntryBrowserPage(WebDriver driver) {
		super(driver);
	}
	
	public void filterWithWhereClause(String whereClause){
		whereClauseInput.clear();
		whereClauseInput.sendKeys(whereClause);
		
		filterButton.click();
	}
	
	/**
	 * Count number of result rows:
	 * @return
	 */
	public int countNumberOfRows() {
		List<WebElement> rows = driver.findElements(By.xpath(RESULT_TABLE_ROWS_XPATH));
		return rows.size() - 1; // exclude header
	}
	
	public String getValueAtColumn(Column column) {
		WebElement columnElement = driver.findElement(By.xpath("//div[@id='testDiv']/table/tbody/tr[2]/td[" + column.getOrderInTable() + "]"));
		
		Assert.assertNotNull(columnElement);
		return columnElement.getText();
	}
	
	public void deleteFirstRow() {
		// click on the delete icon:
		WebElement deleteButton = driver.findElement(By.xpath("//a[contains(@onclick, 'deleteRow')]"));
		Assert.assertNotNull(deleteButton);
		deleteButton.click();
		
		WebElement confirmButton = driver.findElement(By.xpath("//td[@class='x-panel-btn-td'][1]//button"));
		Assert.assertNotNull(confirmButton);
		confirmButton.click();
	}
	
	public void deleteRow(String entryId) {
		// click on the delete icon:
		WebElement deleteButton = driver.findElement(By.xpath("//a[contains(@onclick, 'deleteRow') and contains(@onclick, '"
					+ entryId + "')]"));
		Assert.assertNotNull(deleteButton);
		deleteButton.click();
		
		WebElement confirmButton = driver.findElement(By.xpath("//td[@class='x-panel-btn-td'][1]//button"));
		Assert.assertNotNull(confirmButton);
		confirmButton.click();
		expectSuccessMessageShown(true);
	}
	
	public void clickAddRowButton() {
		addIcon.click();
	}
	
	
	// TODO 18-Dec-2014/nhattan: should have a EntryEditPage
	/**
	 * 
	 * Select for a Reference </br></br>
	 * Example: <b>select carrier for extMaterial</b></br>
	 * - idColumnOfReference: <b>CARRIER_ID</b></br>
	 * - referenceWhereClauseFilter: <b>DISPLAY_NAME like '%carrier 11%'</b></br>
	 * @param idColumnOfReference the column to select, usually the id so can click on
	 * @param referenceWhereClauseFilter for filter reference field
	 */
	public void selectReference(Column idColumnOfReference, String referenceWhereClauseFilter) {
		String referenceField = idColumnOfReference.getColumName();
		
		WebElement form = driver.findElement(
				By.xpath("//form[@name='admin_RowEditForm']"));
		Assert.assertNotNull(form);
		
		WebElement buttonBrowseReference = form.findElement(
				By.xpath(".//table[@class='big_form2']//tr/td[contains(text(), '"+ referenceField +"')]/following-sibling::td//input[@type='button']"));
		buttonBrowseReference.click();
		
		waitForLoading(driver);
		
		WebElement whereClauseInput = driver.findElement(
				By.xpath("//form[@name='admin_ReferenceSearchForm']//input[@name='whereClause']"));
		
		whereClauseInput.clear();
		whereClauseInput.sendKeys(referenceWhereClauseFilter);
		
		WebElement buttonFilter = driver.findElement(
				By.xpath("//form[@name='admin_ReferenceSearchForm']//input[@value='Filter']"));
		buttonFilter.click();
		
		WebElement linkReference = driver.findElement(
				By.xpath("//form[@name='admin_ReferenceSearchForm']//div[@class='scrollist']//tr[2]/td[1]/a"));
		linkReference.click();
	}
	
	// TODO 18-Dec-2014/nhattan: should have a EntryEditPage
	public void inputValueForColum(Column column, String value) {
		String referenceField = column.getColumName();
		
		WebElement form = driver.findElement(
				By.xpath("//form[@name='admin_RowEditForm']"));
		Assert.assertNotNull(form);
		
		WebElement inputField = form.findElement(
				By.xpath(".//table[@class='big_form2']//tr/td[contains(text(), '"+ referenceField +"')]/following-sibling::td//input[@type='text']"));
		inputField.clear();
		inputField.sendKeys(value);
	}
		
	// TODO 18-Dec-2014/nhattan: should have a EntryEditPage
	public void clickSaveButton(){
		WebElement saveButton = driver.findElement(By.name("save"));
		Assert.assertNotNull("Can not found the save button", saveButton);
		saveButton.click();
	}
	
	public void clickCancelButton() {
		WebElement cancelButton = driver.findElement(By.name("cancel"));
		Assert.assertNotNull("Can not found the cancel button", cancelButton);
		cancelButton.click();
	}
}
