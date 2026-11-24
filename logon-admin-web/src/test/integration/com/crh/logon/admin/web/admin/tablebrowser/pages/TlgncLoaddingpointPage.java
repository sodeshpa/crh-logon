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
@TargetTable(name = "TLGNC_LOADINGPOINT")
public class TlgncLoaddingpointPage extends EntryBrowserPage {

	/**
	 * @param driver
	 */
	public TlgncLoaddingpointPage(WebDriver driver) {
		super(driver);
	}

	/* (non-Javadoc)
	 * @see LogonBasePage#verifyCorrectPage()
	 */
	@Override
	protected void verifyCorrectPage() {
		WebElement selectionDropdown = driver.findElement(By.id("tableSelectionDropdown"));
		
		Assert.assertNotNull(selectionDropdown);
		Assert.assertTrue("TLGNC_LOADINGPOINT".equals(selectionDropdown.getAttribute("value")));
	}
	
	public enum LoadingpointColumn implements Column {
		LOADINGPOINT_ID(2),
		SHIPPINGPOINT_ID(3);
		
		private int columnOrder;
		
		private LoadingpointColumn(int columnOrder) {
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
