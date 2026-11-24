/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.masterdata.road;

import java.util.Date;
import java.util.Locale;

import com.crh.logon.admin.web.masterdata.road.pages.TrailerPage;
import com.crh.logon.admin.web.pages.CarrierPreSelectionPage;
import com.crh.logon.admin.web.pages.LogonPageFactory;
import com.crh.logon.admin.web.pages.LogonSeleniumTestCaseBase;
import com.crh.logon.utils.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author AdNovum Informatik AG
 */
@Ignore
public class TrailerTest extends LogonSeleniumTestCaseBase {
	String POOL = "Pool Eins";
	String CARRIER = "carrier -11";
	String TARE = "15.5";
	
	private String fasttime;
	private String carrier;
	private String maxMeasure;
	private String taraMeasure;
	private String trailerName;
	private String trailerPlateNumber;
	private String sapNr;
	
	@Before
	public void setUpTest() {
		this.fasttime = "_" + DateTimeUtils.formatDayMonthYear(new Date(), Locale.ENGLISH) + "_" + DateTimeUtils.formatHoursMinutes(new Date());			
		this.carrier = CARRIER;
		this.maxMeasure = "50";
		this.taraMeasure = TARE;
		this.trailerName = "a" + fasttime;
		this.trailerPlateNumber = "j" + fasttime;
		this.sapNr = "7111281";
		loginToLogon("/logon/admin/AdminEntry.do", "logonadmin_de", "logonadmin_de");
	}

	@Test
	public void createAndEditTrailer() throws Exception {
		CarrierPreSelectionPage carrierPreSelectionPage = LogonPageFactory.initElements(seleniumDriver, CarrierPreSelectionPage.class);
		TrailerPage trailerPage = carrierPreSelectionPage.selectCarrier(carrier, TrailerPage.class);
		
		trailerPage.clickAddNewTrailerButton();
		
		trailerPage.typeTrailerName(trailerName);
		trailerPage.typePopupPlateNr(trailerPlateNumber);
		trailerPage.typePopupMaxMeasure(maxMeasure);
		trailerPage.typePopupTaraMeasure(taraMeasure);
		trailerPage.selectSapNumber(sapNr);
		
		trailerPage.clickSaveNewTrailerButton();
		
		trailerPage.selectTrailer(trailerName);
		
		trailerPage.clickSaveTrailerButton();
		
		trailerPage.checkStatus(true);
		
		trailerPage.selectTrailerTransportType("1");  //1,2,4
		trailerPage.selectTrailerType("1"); //1,2
		trailerPage.selectBranding("false"); //true, false
		trailerPage.selectNrChambers("0"); //0,1,2
		trailerPage.clickSaveTrailerButton();
		trailerPage.expectSuccessMessageShown(true);
		
		trailerPage.selectTrailerTransportType("2");
		trailerPage.selectTrailerType("1");
		trailerPage.selectBranding("false");
		trailerPage.selectNrChambers("0");
		trailerPage.clickSaveTrailerButton();
		trailerPage.expectSuccessMessageShown(true);
		
		trailerPage.selectTrailerTransportType("4");
		trailerPage.selectTrailerType("1");
		trailerPage.selectBranding("false");
		trailerPage.selectNrChambers("0");
		trailerPage.clickSaveTrailerButton();
		trailerPage.expectSuccessMessageShown(true);
		
		trailerPage.selectTrailerTransportType("1");
		trailerPage.selectTrailerType("2");
		trailerPage.selectBranding("false");
		trailerPage.selectNrChambers("0");
		trailerPage.clickSaveTrailerButton();
		trailerPage.expectSuccessMessageShown(true);
		
		trailerPage.selectTrailerTransportType("1");
		trailerPage.selectTrailerType("1");
		trailerPage.selectBranding("true");
		trailerPage.selectNrChambers("0");
		trailerPage.clickSaveTrailerButton();
		trailerPage.expectSuccessMessageShown(true);
		
		trailerPage.selectTrailerTransportType("1");
		trailerPage.selectTrailerType("1");
		trailerPage.selectBranding("false");
		trailerPage.selectNrChambers("1");
		trailerPage.clickSaveTrailerButton();
		trailerPage.expectSuccessMessageShown(true);
		
		trailerPage.selectTrailerTransportType("1");
		trailerPage.selectTrailerType("1");
		trailerPage.selectBranding("false");
		trailerPage.selectNrChambers("2");
		trailerPage.clickSaveTrailerButton();
		trailerPage.expectSuccessMessageShown(true);

		
		trailerPage.selectTrailerTransportType("2");
		trailerPage.selectTrailerType("2");
		trailerPage.selectBranding("true");
		trailerPage.selectNrChambers("1");
		trailerPage.clickSaveTrailerButton();
		trailerPage.expectSuccessMessageShown(true);
		
		
		trailerPage.selectTrailerTransportType("4");
		trailerPage.selectTrailerType("2");
		trailerPage.selectBranding("true");
		trailerPage.selectNrChambers("2");
		trailerPage.clickSaveTrailerButton();
		trailerPage.expectSuccessMessageShown(true);
		
		
		trailerPage.selectTrailerTransportType("4");
		trailerPage.selectTrailerType("2");
		trailerPage.selectBranding("false");
		trailerPage.selectNrChambers("2");
		trailerPage.clickSaveTrailerButton();
		trailerPage.expectSuccessMessageShown(true);
	}
}

