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
@TargetTable(name = "TLGNC_EXT_MATERIAL")
public class TlgncExtMaterialPage extends EntryBrowserPage {

	/**
	 * @param driver
	 */
	public TlgncExtMaterialPage(WebDriver driver) {
		super(driver);
	}

	@Override
	protected void verifyCorrectPage() {
		WebElement selectionDropdown = driver.findElement(By.id("tableSelectionDropdown"));
		
		Assert.assertNotNull(selectionDropdown);
		Assert.assertTrue("TLGNC_EXT_MATERIAL".equals(selectionDropdown.getAttribute("value")));
	}
	
	public enum ExtMaterialColumn implements Column {
		EXT_MATERIAL_ID(2),
		CARRIER_ID(3),
		ACTIVE_FLAG(4),
		MATERIAL_NAME(5),
		MATERIAL_COMMENT(6);
		
		private int columnOrder;
		
		private ExtMaterialColumn(int columnOrder) {
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
