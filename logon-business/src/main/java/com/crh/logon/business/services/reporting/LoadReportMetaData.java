/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.reporting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.time.DateUtils;

import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.time.DateTimeUtils;

/**
 * @author AdNovum Informatik AG
 */
public abstract class LoadReportMetaData extends ReportMetaData {
	
	public static final String ATTR_LOADING_TS = "LOADING_TS";
	
	private String langCd;
	
	private Date loadingFromDat;
	private Date loadingToDat;
	
	protected String getWhereClause() {
		
		StringBuilder whereClauseSb = null;
		
		if (langCd != null) {
			whereClauseSb = new StringBuilder();
			whereClauseSb.append(ATTR_LANG_CD + " = ?");
		}
		if (loadingFromDat != null) {
			whereClauseSb = whereClauseSb!=null?whereClauseSb.append(" AND "):new StringBuilder();
			whereClauseSb.append(ATTR_LOADING_TS + " >= to_date(?, 'yyyy/mm/dd')");
		}
		if (loadingToDat != null) {
			whereClauseSb = whereClauseSb!=null?whereClauseSb.append(" AND "):new StringBuilder();
			whereClauseSb.append(ATTR_LOADING_TS + " < to_date(?, 'yyyy/mm/dd')");
		}
		
		return whereClauseSb!=null?whereClauseSb.toString():null;
	}
	
	public Object[] getQParams() {
		
		List qParams = new ArrayList();
		
		SimpleDateFormat simpleDateFormat = getSimpleDateFormatWithGMTZone();
		
		if (langCd != null) {
			qParams.add(langCd);
		}
		if (loadingFromDat != null) {
			qParams.add(simpleDateFormat.format(DateUtils.truncate(loadingFromDat, Calendar.HOUR_OF_DAY)));
		}
		if (loadingToDat != null) {
			qParams.add(simpleDateFormat.format(DateUtils.addDays(DateUtils.truncate(loadingToDat, Calendar.HOUR_OF_DAY),1)));
		}
		
		return qParams.toArray();
	}

	public Date getLoadingFromDat() {
		return loadingFromDat;
	}

	public void setLoadingFromDat(Date loadingFromDat) {
		this.loadingFromDat = loadingFromDat;
	}

	public Date getLoadingToDat() {
		return loadingToDat;
	}

	public void setLoadingToDat(Date loadingToDat) {
		this.loadingToDat = loadingToDat;
	}

	public String getLangCd() {
		return langCd;
	}

	public void setLangCd(String langCd) {
		this.langCd = langCd;
	}

	public void setupFilters(HttpServletRequest request) throws ParseException {
		String parameterLangCD = request.getParameter(PARAM_LANG_CD);
		setLangCd(parameterLangCD);
		
		if (!StringUtils.empty(request.getParameter(PARAM_FROM_DAT))) {
			Date fromDate = DateTimeUtils.parseDayMonthYearInGMTZone(request.getParameter(PARAM_FROM_DAT));
			fromDate = DateTimeUtils.getMorningOfDayInGMTZone(fromDate);
			setLoadingFromDat(fromDate);
		}

		if (!StringUtils.empty(request.getParameter(PARAM_TO_DAT))) {
			Date toDate = DateTimeUtils.parseDayMonthYearInGMTZone(request.getParameter(PARAM_TO_DAT));
			toDate = DateTimeUtils.getMidnightOfDayInGMTZone(toDate);
			setLoadingToDat(toDate);
		}
	}
}
