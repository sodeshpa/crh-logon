/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.masterdata.rail.pages;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ch.adnovum.jcan.test.selenium.util.TestProperties;
import ch.adnovum.jcan.test.selenium.util.WebElementUtil;

import com.crh.logon.admin.web.pages.LogonBasePage;

/**
 * @author AdNovum Informatik AG
 */
public class WagonPage extends LogonBasePage {
public static final String URL = TestProperties.INSTANCE.getApplicationTestUrl()	+ "/logon/admin/WagonSearchEntry.do";
	
	public static final String DATE_FORMAT = "dd.MM.yy HH:mm";
	
	@FindBy(id = "wagonDetailMain.arrivalLastKnownStationDate")
	private WebElement arrivalLastKnownStationDate;
	
	@FindBy(id = "wagonDetailMain.arrivalLastKnownStationTime")
	private WebElement arrivalLastKnownStationTime;
	
	//xpath to retrieve all rows in wagon result table
	private final String wagonResultsXpath = "//div[@id='divSearchResults']/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/descendant::tr";
	
	public WagonPage (WebDriver driver) {
		super(driver);
		this.driver.get(URL);
	}

	@Override
	public void verifyCorrectPage() {
		String xpath = "//a[contains(@href, 'admin/WagonSearchEntry.do') and @class='highlight']";
		assertTrue("Order page expected!",
				isElementPresentAndVisible(driver.findElement(By.xpath(xpath))));
	}
	
	public String searchForWagon(String wagonNo){
		WebElement wagonText = driver.findElement(By.id("wagonNoCombo"));
		Assert.assertNotNull(wagonText);
		wagonText.sendKeys(wagonNo);
		wagonText.sendKeys(Keys.TAB);
		waitForLoading(driver);
		
		WebElement buttonSearch = driver.findElement(By.id("buttonSearch"));
		Assert.assertNotNull(buttonSearch);
		buttonSearch.click();
		waitForLoading(driver);
		
		WebElement wagonDetail = driver.findElement(By.xpath(wagonResultsXpath));
		
		return selectWagon(wagonDetail);
	}
	
	public void setToDelivered(){
		WebElement button = driver.findElement(By.xpath("//input[contains(@onclick, 'reportWagonEmpty')]"));
		Assert.assertNotNull(button);
		button.click();
		waitForLoading(driver);
		
		WebElement confirmPopup = driver.findElement( 
				By.xpath("//div[@class='x-window-bwrap']//div[contains(@class, 'x-panel-btns-center')]//button[text()='Yes']"));
		Assert.assertNotNull(confirmPopup);
		confirmPopup.click();
		
		// TODO 3-Nov-2014/nhattan: At local, sometime need to click delivered button twice
		button = driver.findElement(By.xpath("//input[contains(@onclick, 'reportWagonEmpty')]"));
		if(button != null){
			button.click();
			waitForLoading(driver);
		}
	}

	/**
	 * searches for the first empty wagon and clicks on it
	 */
	public String selectFirstEmptyWagon() {
		List<WebElement> allWagonRowElements = this.driver.findElements(By.xpath(wagonResultsXpath));

		WebElement firstEmptyWagonRowElement = searchAnEmptyWagon(allWagonRowElements);
		assertNotNull("No empty wagon found!", firstEmptyWagonRowElement);
		
		return selectWagon(firstEmptyWagonRowElement);
	}

	/**
	 * @param wagonRowElement
	 * @return
	 */
	private String selectWagon(WebElement wagonRowElement) {
		WebElement wagonNoTdElement = wagonRowElement.findElements(By.tagName("td")).get(0);
		
		String wagonNo = wagonNoTdElement.findElement(By.xpath("./div")).getText().trim();
		wagonNoTdElement.click();

		return wagonNo;
	}

	/**
	 * @param wagonRowElements
	 * @return
	 */
	private WebElement searchAnEmptyWagon(List<WebElement> wagonRowElements) {
		WebElement firstEmptyWagonRowElement = null;
		
		for (WebElement element : wagonRowElements) {
			String orderNoValue = element.findElements(By.tagName("td")).get(9).getText();
			
			// If element has order
			if(orderNoValue == null || orderNoValue.trim().isEmpty()){
				firstEmptyWagonRowElement = element;
				break;
			}
		}
		
		return firstEmptyWagonRowElement;
	}
	
	

	public void selectLocation(String locationName) {
		WebElement locationCombo = driver.findElement(
				By.id(Wagon_Element.LOCATION_COMBO.getId()));
		assertNotNull("Cannot find location combobox", locationCombo);
		
		triggerComboLoad(Wagon_Element.LOCATION_COMBO);
		selectExtJsComboWithStringValue(Wagon_Element.LOCATION_COMBO.getJsVar(), Wagon_Element.LOCATION_COMBO.getJsSearchField(), locationName);
	}
	
	public void selectTrack(String trackName){
		WebElement trackCombo = driver.findElement(
				By.id(Wagon_Element.TRACK_COMBO.getId()));
		assertNotNull("Cannot find track combobox", trackCombo);
		
		triggerComboLoad(Wagon_Element.TRACK_COMBO);
		selectExtJsComboWithStringValue(Wagon_Element.TRACK_COMBO.getJsVar(), Wagon_Element.TRACK_COMBO.getJsSearchField(), trackName);
	}
	
	public void setArrivalLastKnownStationDate(String dateInString){
		arrivalLastKnownStationDate.clear();
		arrivalLastKnownStationDate.sendKeys(dateInString);
	}
	
	public void setArrivalLastKnownStationTime(String timeInString){
		arrivalLastKnownStationTime.clear();
		arrivalLastKnownStationTime.sendKeys(timeInString);
	}
	
	public void saveWagon() {
		WebElement saveButton = driver.findElement(By.xpath("//input[contains(@onclick, 'saveWagon()')]"));
		Assert.assertNotNull(saveButton);
		
		saveButton.click();
	}

	/**
	 * clicks on the trigger of the location combo to load location list
	 */
	private void triggerComboLoad(Wagon_Element element) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String jsLoad = element.getJsVar() + ".triggers[0].dom.click()";
		js.executeScript(jsLoad);
		waitForLoading(driver);
	}
	
	/**
	 * gets the value of the station no combo
	 * @return
	 */
	public String getStationNo() {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String jsLoad = "return " + Wagon_Element.LOCATION_NO_COMBO.getJsVar() + ".value";
		Object value = js.executeScript(jsLoad);
		return value != null ? String.valueOf(value) : "";
	}

	public enum Wagon_Element {
		WAGON_SEARCH_COMBO("wagonNoCombo","wagonNoCombo","wagonNo"),
		LOCATION_COMBO("wagonDetailMain.lastKnownRailwayStationId", "lastKnownStationCombo", "railwayStationName"),
		LOCATION_NO_COMBO("", "lastKnownStationCombo", ""),
		TRACK_COMBO("wagonDetailMain.currentPlantTrackCd", "currentTrackCombo", "trackName");

		private String id;

		private String jsVar;

		private String jsSearchField;

		Wagon_Element(String id, String jsVar, String jsSearchField) {
			this.id = id;
			this.jsVar = jsVar;
			this.jsSearchField = jsSearchField;
		}

		public String getId() {
			return id;
		}

		public String getJsVar() {
			return jsVar;
		}

		public String getJsSearchField() {
			return jsSearchField;
		}
	}
}
