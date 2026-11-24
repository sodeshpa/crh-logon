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
@TargetTable(name = "TLGNC_SHIPTO")
public class TlgncShiptoPage extends EntryBrowserPage {

	/**
	 * @param driver
	 */
	public TlgncShiptoPage(WebDriver driver) {
		super(driver);
	}

	/* (non-Javadoc)
	 * @see LogonBasePage#verifyCorrectPage()
	 */
	@Override
	protected void verifyCorrectPage() {
		WebElement selectionDropdown = driver.findElement(By.id("tableSelectionDropdown"));
		
		Assert.assertNotNull(selectionDropdown);
		Assert.assertTrue("TLGNC_SHIPTO".equals(selectionDropdown.getAttribute("value")));
	}
	
	public enum ShiptoColumn implements Column {
		SHIPTO_ID(2),
		SHIPTO_SAP_BP_ID(3);
		
		private int columnOrder;
		
		private ShiptoColumn(int columnOrder) {
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
