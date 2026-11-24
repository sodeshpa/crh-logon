/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.reporting;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.time.DateTimeUtils;

/**
 * Metadata for the special report "Freight Carrier Revenue PDF". The reason this report is handled specially
 * is that it is also used on the Scheduling screen (Export popup).
 * 
 * @author AdNovum Informatik AG
 */
public class FreightCarrierRevenuePdfMetaData extends ReportMetaData {

	private String langCd;
	
	private Date deliveryFromDat;
	private Date deliveryToDat;
	private List<Long> selectedCarrierIDs;

	/*
	 * Introduced to avoid hard-coded reportCds in JS code. (Value actually does not matter, just the
	 * existence of this field.)
	 */
	private final boolean previewDisabled = true;

	/*
	 * Introduced because of an IE bug that makes files of content type "application/pdf" unable to be
	 * opened in a new window. Additionally, if an error message is displayed due to no data during
	 * generation, this way the same window will not be duplicated. (Still not the best solution though,
	 * because you will lose form data.) (Value actually does not matter, just the existence of this field.)
	 */
	private final boolean openInSameWindow = true;

	@Override
	public void init(ServiceLocator serviceLocator, long uid) {
		// not used.
	}

	@Override
	protected String getWhereClause() {
		// Not used.
		return null;
	}

	@Override
	public Object[] getQParams() {
		// Not used.
		return null;
	}

	@Override
	public String getQueryString() {
		// Not used.
		return null;
	}

	public Date getDeliveryFromDat() {
		return deliveryFromDat;
	}

	public void setDeliveryFromDat(Date deliveryFromDat) {
		this.deliveryFromDat = deliveryFromDat;
	}

	public Date getDeliveryToDat() {
		return deliveryToDat;
	}

	public void setDeliveryToDat(Date deliveryToDat) {
		this.deliveryToDat = deliveryToDat;
	}

	public List<Long> getSelectedCarrierIDs() {
		return this.selectedCarrierIDs;
	}

	public void setSelectedCarrierIDs(List<Long> selectedCarrierIDs) {
		this.selectedCarrierIDs = selectedCarrierIDs;
	}

	public boolean isPreviewDisabled() {
		return previewDisabled;
	}

	public boolean isOpenInSameWindow() {
		return openInSameWindow;
	}

	public String getLangCd() {
		return langCd;
	}

	public void setLangCd(String langCd) {
		this.langCd = langCd;
	}

	@Override
	public void setupFilters(HttpServletRequest request) throws ParseException {
		String parameterLangCD = request.getParameter(PARAM_LANG_CD);
		setLangCd(parameterLangCD);

		if (!StringUtils.empty(request.getParameter(PARAM_FROM_DAT))) {
			Date parsedFromDate = DateTimeUtils.parseDayMonthYearInGMTZone(request.getParameter(PARAM_FROM_DAT));
			parsedFromDate = DateTimeUtils.getMorningOfDayInGMTZone(parsedFromDate);
			setDeliveryFromDat(parsedFromDate);
		}

		if (!StringUtils.empty(request.getParameter(PARAM_TO_DAT))) {
			Date parsedToDate = DateTimeUtils.parseDayMonthYearInGMTZone(request.getParameter(PARAM_TO_DAT));
			parsedToDate = DateTimeUtils.getMidnightOfDayInGMTZone(parsedToDate);
			setDeliveryToDat(parsedToDate);
		}

		List<String> parameterCarrierIDs = getSelectedOptions(
				PARAM_CARRIER_ID_LIST, request);
		List<Long> parameterCarrierIDsAsLong = new ArrayList<Long>();
		for (String parameterCarrierID : parameterCarrierIDs) {
			Long parameterCarrierIDAsLong = Long.valueOf(parameterCarrierID);
			parameterCarrierIDsAsLong.add(parameterCarrierIDAsLong);
		}
		setSelectedCarrierIDs(parameterCarrierIDsAsLong);
	}

}
