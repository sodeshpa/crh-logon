/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.reporting;


import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


/**
 * @author AdNovum Informatik AG
 */
public abstract class ReportMetaData extends ExportMetaData {
	
	private String reportCd;
	private String reportName;
	private String rawQueryString;
	private String userTimezoneCd;

	/**
	 * DB column names of report view tables
	 */
	static final String ATTR_COMM_CARRIER_V_SAP_ID = "COMM_CARRIER_V_SAP_ID";
	static final String ATTR_COMM_CARRIER_SAP_BP_ID = "COMM_CARRIER_SAP_BP_ID";
	static final String ATTR_O_POOL_ID = "O_POOL_ID";
	static final String ATTR_REPORT_FINISHED_COMM_FLAG = "REPORT_FINISHED_COMM_FLAG";
	static final String ATTR_MATERIAL_PACKAGE_CD = "MATERIAL_PACKAGE_CD";
	static final String ATTR_DISTRIBUTION_DEST_CD = "DISTRIBUTION_DEST_CD";
	static final String ATTR_DELIVERY_ACTUAL_DAT = "DELIVERY_ACTUAL_DAT";
	static final String ATTR_DELIVERY_FROM_DAT = "O_DELIVERY_FROM_DAT";
	static final String ATTR_DELIVERY_TO_DAT = "O_DELIVERY_TO_DAT";
	static final String ATTR_ORDER_STATUS_CD = "O_ORDER_STATUS_CD";
	static final String ATTR_LANG_CD = "LANG_CD";

	public static final String VIRTUAL_ORDER_STATUS_FINISHED_COMMERCIAL = "-99"; // non existing status, will be converted to REPORT_FINISHED_COMM_FLAG
	public static final String VIRTUAL_ORDER_STATUS_FINISHED = "-98"; // non existing status, will be converted to REPORT_FINISHED_COMM_FLAG
	
	/**
	 * Struts form parameter names
	 */
	public static final String PARAM_POOL_ID_LIST = "poolIdList";
	public static final String PARAM_CARRIER_ID_LIST = "carrierIdList";
	public static final String PARAM_LANG_CD = "langCd";
	public static final String PARAM_ORDER_STATUS_CD_LIST = "orderStatusCdList";
	public static final String PARAM_MATERIAL_PACKAGE_CD_LIST = "materialPackageCdList";
	public static final String PARAM_DISTRIBUTION_DEST_CD_LIST = "destinationCdList";
	public static final String PARAM_TO_DAT = "toDat";
	public static final String PARAM_FROM_DAT = "fromDat";
	public static final String PARAM_REPORT_CD = "reportCd";
	
	protected abstract String getWhereClause();
	
	/**
	 * Setting up the relevant filters for the specific report type
	 * 
	 * @param request
	 */
	public abstract void setupFilters(HttpServletRequest request) throws ParseException;
	
	public String getReportCd() {
		return reportCd;
	}
	
	public void setReportCd(String reportCd) {
		this.reportCd = reportCd;
	}
	
	public String getReportName() {
		return reportName;
	}
	
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getRawQueryString() {
		return rawQueryString;
	}

	public void setRawQueryString(String rawQueryString) {
		this.rawQueryString = rawQueryString;
	}

	public String getUserTimezoneCd() {
		return userTimezoneCd;
	}

	public void setUserTimezoneCd(String userTimezoneCd) {
		this.userTimezoneCd = userTimezoneCd;
	}

	/**
	 * Ext.ux.CheckComboBox sends the selected options in String concatenated by
	 * ','.
	 * 
	 * @param parameterName
	 * @param request
	 */
	public static List<String> getSelectedOptions(String parameterName, HttpServletRequest request) {
		String csvList = request.getParameter(parameterName);
		if (csvList == null || csvList == "") {
			return null;
		}
		String[] optionArray = csvList.split(",");
		return Arrays.asList(optionArray);
	}

}
