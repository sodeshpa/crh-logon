/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.admin.tablebrowser.pages;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ch.adnovum.jcan.test.selenium.util.WebElementUtil;

/**
 * @author AdNovum Informatik AG
 */
@TargetTable(name = "TLGNC_SHIPPINGPOINT")
public class TlgncShippingpointBrowsePage extends EntryBrowserPage {
	/**
	 * @param driver
	 */
	public TlgncShippingpointBrowsePage(WebDriver driver) {
		super(driver);
	}

	/* (non-Javadoc)
	 * @see LogonBasePage#verifyCorrectPage()
	 */
	@Override
	protected void verifyCorrectPage() {
		WebElement selectionDropdown = driver.findElement(By.id("tableSelectionDropdown"));
		
		Assert.assertNotNull(selectionDropdown);
		Assert.assertTrue("TLGNC_SHIPPINGPOINT".equals(selectionDropdown.getAttribute("value")));
	}
	
	public String getShippingpointId() {
		WebElement cellSpId = driver.findElement(By.xpath("//div[@id='testDiv']/table/tbody/tr[2]/td[2]"));
		
		Assert.assertNotNull(cellSpId);
		return cellSpId.getText();
	}
	
	public String getShippingpointName() {
		WebElement cellSpId = driver.findElement(By.xpath("//div[@id='testDiv']/table/tbody/tr[2]/td[6]"));
		
		Assert.assertNotNull(cellSpId);
		return cellSpId.getText();
	}
	
	public enum ShippingpointColumn implements Column {
		SHIPPINGPOINT_NAME(6),
		SHIPPINGPOINT_ID(2);
		
		private int columnOrder;
		
		private ShippingpointColumn(int columnOrder) {
			this.columnOrder = columnOrder;
		}
		
		public int getOrderInTable() {
			return this.columnOrder;
		}
		
		@Override
		public String getColumName() {
			return this.toString();
		}
	}
}
