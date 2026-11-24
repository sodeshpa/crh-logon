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
@TargetTable(name = "TLGNC_CARRIER")
public class TlgncCarrierPage extends EntryBrowserPage {

	/**
	 * @param driver
	 */
	public TlgncCarrierPage(WebDriver driver) {
		super(driver);
	}

	@Override
	protected void verifyCorrectPage() {
		WebElement selectionDropdown = driver.findElement(By.id("tableSelectionDropdown"));
		
		Assert.assertNotNull(selectionDropdown);
		Assert.assertTrue("TLGNC_CARRIER".equals(selectionDropdown.getAttribute("value")));
	}
	
	public enum CarrierColumn implements Column {
		CARRIER_ID(2),
		DISPLAY_NAME(3);
		
		private int columnOrder;
		
		private CarrierColumn(int columnOrder) {
			this.columnOrder = columnOrder;
		}

		@Override
		public int getOrderInTable() {
			return columnOrder;
		}

		@Override
		public String getColumName() {
			return this.toString();
		}
	}
}
