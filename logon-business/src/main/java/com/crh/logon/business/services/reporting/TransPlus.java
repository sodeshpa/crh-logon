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

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.time.DateTimeUtils;

/**
 * Represents the report object. This class is referenced from the reports.xml. 
 * For the coding style this is mostly a copy-paste of an existing report class.
 * @author AdNovum Informatik AG
 */
public class TransPlus extends ReportMetaData {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(TransPlus.class);

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private Date deliveryFromDat;
	private Date deliveryToDat;
	private String queryString;
	private List<String> selectedPoolIDs;
	private List<String> selectedCarrierIDs;
	private List<String> selectedOrderStatusCds;

	@Override
	public void init(ServiceLocator serviceLocator, long uid) {

		List qParamList = new ArrayList();
		Object[] qParams = getQParams();
		List<Object> qParamsAsList = Arrays.asList(qParams);
		List filterQParamList = new ArrayList(qParamsAsList);

		List qFormatParams = new ArrayList();
		String whereClause = getWhereClause();
		qFormatParams.add(whereClause);
		qParamList.addAll(filterQParamList);

		String rawQueryString = getRawQueryString();
		if (rawQueryString == null) {
			throw new LogonException("Report query not found", Reason.RES_ERROR_IMPL);
		}
		
		MessageFormat formatter = new MessageFormat("");

		formatter.applyPattern(rawQueryString);

		Object[] qParamsToArray = qFormatParams.toArray();
		this.queryString = formatter.format(qParamsToArray);
		
	}
	
	public String getQueryString() {
		return queryString;
	}

	protected String getWhereClause() {

		StringBuilder whereClauseSb = null;

		StringBuilder emptyStringBuilder = new StringBuilder();
		if (deliveryFromDat != null) {
			whereClauseSb = emptyStringBuilder;
			whereClauseSb.append(ATTR_DELIVERY_FROM_DAT + " >= to_date(?, 'yyyy/mm/dd')");
		}
		if (deliveryToDat != null) {
			whereClauseSb = whereClauseSb != null ? whereClauseSb.append(" AND ") : emptyStringBuilder;
			whereClauseSb.append(ATTR_DELIVERY_TO_DAT+ " < to_date(?, 'yyyy/mm/dd')");
		}
		if ((selectedPoolIDs != null) && (!selectedPoolIDs.isEmpty())) {
			whereClauseSb = whereClauseSb != null ? whereClauseSb.append(" AND ") : emptyStringBuilder;
			whereClauseSb.append("(");
			List<String> selectedPoolIDListParams = new ArrayList<String>();
			for (int i = 0; i < selectedPoolIDs.size(); ++i) {
				selectedPoolIDListParams.add(ATTR_O_POOL_ID + " = ?");
			}
			whereClauseSb.append(StringUtils.join(selectedPoolIDListParams, " OR "));
			whereClauseSb.append(")");
		}
		if ((selectedCarrierIDs != null) && (!selectedCarrierIDs.isEmpty())) {
			whereClauseSb = whereClauseSb != null ? whereClauseSb.append(" AND ") : emptyStringBuilder;
			whereClauseSb.append("(");
			List<String> selectedCarrierIDListParams = new ArrayList<String>();
			for (int i = 0; i < selectedCarrierIDs.size(); ++i) {
				selectedCarrierIDListParams.add(ATTR_COMM_CARRIER_SAP_BP_ID + " = ?");
			}
			whereClauseSb.append(StringUtils.join(selectedCarrierIDListParams, " OR "));
			whereClauseSb.append(")");
		}
		if ((selectedOrderStatusCds != null) && (!selectedOrderStatusCds.isEmpty())) {
			whereClauseSb = whereClauseSb != null ? whereClauseSb.append(" AND ") : emptyStringBuilder;
			whereClauseSb.append("(");
			List<String> selectedOrderStatusCdListParams = new ArrayList<String>();
			for (int i = 0; i < selectedOrderStatusCds.size(); ++i) {
				selectedOrderStatusCdListParams.add(ATTR_ORDER_STATUS_CD + " = ?");
			}
			whereClauseSb.append(StringUtils.join(selectedOrderStatusCdListParams, " OR "));
			whereClauseSb.append(")");
		}
		if (whereClauseSb == null) {
			return " 1 = 1 ";
		}

		return whereClauseSb.toString();
	}

	public Object[] getQParams() {

		SimpleDateFormat simpleDateFormat = getSimpleDateFormatWithGMTZone();
		
		List qParams = new ArrayList();
		if (deliveryFromDat != null) {
			qParams.add(simpleDateFormat.format(DateUtils.truncate(deliveryFromDat, Calendar.HOUR_OF_DAY)));
		}
		if (deliveryToDat != null) {
			qParams.add(simpleDateFormat.format(DateUtils.addDays(DateUtils.truncate(deliveryToDat,Calendar.HOUR_OF_DAY), 1)));
		}
		if (selectedPoolIDs != null) {
			for (String selectedPoolID : selectedPoolIDs) {
				qParams.add(selectedPoolID);
			}
		}
		if (selectedCarrierIDs != null) {
			for (String selectedCarrierID : selectedCarrierIDs) {
				qParams.add(selectedCarrierID);
			}
		}
		if (selectedOrderStatusCds != null) {
			for (String selectedOrderStatusCd : selectedOrderStatusCds) {
				qParams.add(selectedOrderStatusCd);
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
	
	public List<String> getSelectedPoolIDs() {
		return selectedPoolIDs;
	}

	public void setSelectedPoolIDs(List<String> selectedPoolIDs) {
		this.selectedPoolIDs = selectedPoolIDs;
	}

	public List<String> getSelectedOrderStatusCds() {
		return selectedOrderStatusCds;
	}

	public void setSelectedOrderStatusCds(List<String> selectedOrderStatusCds) {
		this.selectedOrderStatusCds = selectedOrderStatusCds;
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

		List<String> parameterPoolIDs = getSelectedOptions(PARAM_POOL_ID_LIST, request);
		setSelectedPoolIDs(parameterPoolIDs);

		List<String> parameterCarrierIDs = getSelectedOptions(PARAM_CARRIER_ID_LIST, request);
		setSelectedCarrierIDs(parameterCarrierIDs);

		List<String> parameterOrderStatusCds = getSelectedOptions(PARAM_ORDER_STATUS_CD_LIST, request);
		setSelectedOrderStatusCds(parameterOrderStatusCds);

	}
}
