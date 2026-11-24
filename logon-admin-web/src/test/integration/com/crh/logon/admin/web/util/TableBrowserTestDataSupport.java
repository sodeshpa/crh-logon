/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.util;

import java.util.Properties;

import com.crh.logon.admin.web.admin.tablebrowser.pages.TableBrowserPage;
import com.crh.logon.admin.web.admin.tablebrowser.pages.TlgncLoaddingpointPage;
import com.crh.logon.admin.web.admin.tablebrowser.pages.TlgncShippingpointBrowsePage;
import com.crh.logon.admin.web.pages.TestProperties;
import org.junit.Assert;
import org.junit.Ignore;
import org.openqa.selenium.WebDriver;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.pages.LogonPageFactory;

/**
 * Support for selenium test to be able to use table browser. For example tests which require unique 
 * label, and the test has to make sure to cleanup after creating something, the object can be deleted
 * in table browser leaving also an audit mark that it had been removed.
 * 
 * Added to have at a common place to be able to track what table browser changes the selenium tests are
 * doing.
 * 
 * @author AdNovum Informatik AG
 */
@Ignore
public class TableBrowserTestDataSupport {

	static final JcanLogger LOG = JcanLoggerFactory.getLogger(TableBrowserTestDataSupport.class);

	WebDriver seleniumDriver;

	public TableBrowserTestDataSupport(WebDriver seleniumDriver) {
		this.seleniumDriver = seleniumDriver;
	}

	/**
	 * Deletes the shippingpoint, created by the ShippingpointTest.java.
	 * @param shippingPointProp 
	 * @return
	 */
	public void deleteShippingpointTestShippingpoint(Properties shippingPointProp) {
		TableBrowserPage tableBrowserPage = LogonPageFactory.initElements(seleniumDriver, TableBrowserPage.class);
		
		TlgncShippingpointBrowsePage tlgncShippingpointBrowsePage = tableBrowserPage.viewTablePage(TlgncShippingpointBrowsePage.class);
		
		// filter the table:
		String filter = "SAP_SHIPPINGPOINT_NO='"
				+ shippingPointProp.getProperty(TestProperties.ShippingPointKey.SHIPPING_POINT_SAP_NO.getKeyString())
				+ "'";
		
		tlgncShippingpointBrowsePage.filterWithWhereClause(filter);

		// count number of result rows:
		int numRows = tlgncShippingpointBrowsePage.countNumberOfRows();
		
		LOG.info(numRows + " results present");

		Assert.assertFalse("Only one result should be found with query filter criteria " + filter,
				numRows > 1);
		
		if (numRows == 0) {
			LOG.info("Shippingpoint not present, skipping deletion.");
			return;
		}

		// verify that the correct shipping point name is listed in the filtered table:
		String spName = tlgncShippingpointBrowsePage.getValueAtColumn(TlgncShippingpointBrowsePage.ShippingpointColumn.SHIPPINGPOINT_NAME);
		
		Assert.assertEquals(
				shippingPointProp.getProperty(TestProperties.ShippingPointKey.SHIPPING_POINT_NAME.getKeyString()),
				spName);

		// get the shippingpoint_id from the table:
		String shipId = tlgncShippingpointBrowsePage.getValueAtColumn(TlgncShippingpointBrowsePage.ShippingpointColumn.SHIPPINGPOINT_ID);

		int numDeletedLoadingPoints = deleteLoadingPointsBelongingToShippingPoint(shipId);
		
		tableBrowserPage = LogonPageFactory.initElements(seleniumDriver, TableBrowserPage.class);
		tlgncShippingpointBrowsePage = tableBrowserPage.viewTablePage(TlgncShippingpointBrowsePage.class);
		
		tlgncShippingpointBrowsePage.filterWithWhereClause(filter);

		tlgncShippingpointBrowsePage.deleteFirstRow();

		LOG.info("Successfully deleted shipping point with id=" + shipId + " and "
				+ numDeletedLoadingPoints + " loading points belonging to it.");
	}

	/**
	 * Deletes all loading points associated with a given shipping point and returns the number of deleted rows.
	 */
	private int deleteLoadingPointsBelongingToShippingPoint(String shipId) {
		TableBrowserPage tableBrowserPage = LogonPageFactory.initElements(seleniumDriver, TableBrowserPage.class);
		TlgncLoaddingpointPage tlgncLoaddingpointPage = tableBrowserPage.viewTablePage(TlgncLoaddingpointPage.class);

		tlgncLoaddingpointPage.filterWithWhereClause("SHIPPINGPOINT_ID=" + shipId);

		int numRows = tlgncLoaddingpointPage.countNumberOfRows();
		
		if (numRows == 0) {
			LOG.info("No loading points for the test shipping point " + shipId);
			return 0;
		}

		for (int i = 1; i <= numRows; i++) {
			String shippingpointId = tlgncLoaddingpointPage.getValueAtColumn(TlgncLoaddingpointPage.LoadingpointColumn.SHIPPINGPOINT_ID);
			Assert.assertEquals(shipId, shippingpointId);

			String loadingPointId = tlgncLoaddingpointPage.getValueAtColumn(TlgncLoaddingpointPage.LoadingpointColumn.LOADINGPOINT_ID);

			// click on the icon to delete the first result row:
			tlgncLoaddingpointPage.deleteRow(loadingPointId);

			// verify that deleted loading points disappear from the table: 
			Assert.assertEquals(numRows - i, tlgncLoaddingpointPage.countNumberOfRows());

			LOG.info("Successfully deleted loading point entry with id=" + loadingPointId);
		}

		return numRows;
	}
}
