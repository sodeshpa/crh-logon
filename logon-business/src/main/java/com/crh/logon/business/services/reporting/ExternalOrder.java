/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.reporting;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.time.DateUtils;

import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
public class ExternalOrder extends ReportMetaData {

	private Date deliveryFromDat;
	private Date deliveryToDat;
	private String queryString;
	private List<String> selectedCarrierIDs;

	public String getQueryString() {
		return queryString;
	}

	public void init(ServiceLocator serviceLocator, long uid) {

		List qParamList = new ArrayList();
		Object[] qParams = getQParams();
		List<Object> qParamsAsList = Arrays.asList(qParams);
		List filterQParamList = new ArrayList(qParamsAsList);

		List qFormatParams = new ArrayList();
		String whereClause = getWhereClause();
		qFormatParams.add(whereClause);
		qParamList.addAll(filterQParamList);

		MessageFormat formatter = new MessageFormat("");

		String rawQueryString = getRawQueryString();
		if (rawQueryString == null) {
			throw new LogonException("Report query not found", Reason.RES_ERROR_IMPL);
		}

		formatter.applyPattern(rawQueryString);

		Object[] qParamsToArray = qFormatParams.toArray();
		this.queryString = formatter.format(qParamsToArray);
	}

	protected String getWhereClause() {

		StringBuilder whereClauseSb = null;

		StringBuilder emptyStringBuilder = new StringBuilder();
		if (deliveryFromDat != null) {
			whereClauseSb = emptyStringBuilder;
			whereClauseSb.append(ATTR_DELIVERY_ACTUAL_DAT
					+ " >= to_date(?, 'yyyy/mm/dd')");
		}
		if (deliveryToDat != null) {
			whereClauseSb = whereClauseSb != null ? whereClauseSb
					.append(" AND ") : emptyStringBuilder;
			whereClauseSb
					.append(ATTR_DELIVERY_ACTUAL_DAT
					+ " < to_date(?, 'yyyy/mm/dd')");
		}
		if ((selectedCarrierIDs != null) && (!selectedCarrierIDs.isEmpty())) {
			whereClauseSb = whereClauseSb != null ? whereClauseSb
					.append(" AND ") : emptyStringBuilder;
			whereClauseSb.append("(");
			ArrayList<String> selectedCarrierIDListParams = new ArrayList<String>();
			for (int i = 0; i < selectedCarrierIDs.size(); ++i) {
				selectedCarrierIDListParams
						.add(ReportMetaData.ATTR_COMM_CARRIER_V_SAP_ID + " = ?");
			}
			whereClauseSb.append(StringUtils.join(selectedCarrierIDListParams, " OR "));
			whereClauseSb.append(")");
		}
		if (whereClauseSb == null) {
			return " 1 = 1 ";
		}

		return whereClauseSb.toString();
	}

	public Object[] getQParams() {

		List qParams = new ArrayList();
		
		SimpleDateFormat simpleDateFormat = getSimpleDateFormatWithGMTZone();
		
		if (deliveryFromDat != null) {
			qParams.add(simpleDateFormat.format(DateUtils
					.truncate(deliveryFromDat, Calendar.HOUR_OF_DAY)));
		}
		if (deliveryToDat != null) {
			qParams.add(simpleDateFormat.format(DateUtils
					.addDays(DateUtils.truncate(deliveryToDat,
							Calendar.HOUR_OF_DAY), 1)));
		}
		if (selectedCarrierIDs != null) {
			for (String selectedCarrierID : selectedCarrierIDs) {
				qParams.add(selectedCarrierID);
			}
		}
		return qParams.toArray();
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

	public List<String> getSelectedCarrierIDs() {
		return this.selectedCarrierIDs;
	}

	public void setSelectedCarrierIDs(List<String> selectedCarrierIDs) {
		this.selectedCarrierIDs = selectedCarrierIDs;
	}

	@Override
	public void setupFilters(HttpServletRequest request) throws ParseException {
		if (!StringUtils.empty(request.getParameter(PARAM_FROM_DAT))) {
			Date fromDate = DateTimeUtils.parseDayMonthYearInGMTZone(request.getParameter(PARAM_FROM_DAT));
			fromDate = DateTimeUtils.getMorningOfDayInGMTZone(fromDate);
			setDeliveryFromDat(fromDate);
		}
		
		if (!StringUtils.empty(request.getParameter(PARAM_TO_DAT))) {
			Date toDate = DateTimeUtils.parseDayMonthYearInGMTZone(request.getParameter(PARAM_TO_DAT));
			toDate = DateTimeUtils.getMidnightOfDayInGMTZone(toDate);
			setDeliveryToDat(toDate);
		}

		List<String> parameterCarrierIDs = getSelectedOptions(
				ReportMetaData.PARAM_CARRIER_ID_LIST, request);
		setSelectedCarrierIDs(parameterCarrierIDs);
	}
}
