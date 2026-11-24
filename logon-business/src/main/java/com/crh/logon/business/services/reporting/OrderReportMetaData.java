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
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.time.DateUtils;

import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.time.DateTimeUtils;

/**
 * @author AdNovum Informatik AG
 */
public abstract class OrderReportMetaData extends ReportMetaData {
	
	private String langCd;
	private Date deliveryFromDat;
	private Date deliveryToDat;
	private List distributionDestCdList;
	private List materialPackageCdList;
	private Boolean finishedCommercial;
	
	protected String getWhereClause() {
		
		StringBuilder whereClauseSb = null;
		
		if (langCd != null) {
			whereClauseSb = new StringBuilder();
			whereClauseSb.append(ATTR_LANG_CD + " = ?");
		}
		if (deliveryFromDat != null) {
			whereClauseSb = whereClauseSb!=null?whereClauseSb.append(" AND "):new StringBuilder();
			whereClauseSb.append(ATTR_DELIVERY_ACTUAL_DAT + " >= to_date(?, 'yyyy/mm/dd')");
		}
		if (deliveryToDat != null) {
			whereClauseSb = whereClauseSb!=null?whereClauseSb.append(" AND "):new StringBuilder();
			whereClauseSb.append(ATTR_DELIVERY_ACTUAL_DAT + " < to_date(?, 'yyyy/mm/dd')");
		}
		if ( (distributionDestCdList != null) && (!distributionDestCdList.isEmpty()) ) {
			whereClauseSb = whereClauseSb!=null?whereClauseSb.append(" AND "):new StringBuilder();
			whereClauseSb.append(ATTR_DISTRIBUTION_DEST_CD + " IN (");
			for (Iterator it=distributionDestCdList.iterator();it.hasNext();) {
				it.next();
				whereClauseSb.append("?");
				if (it.hasNext()) {
					whereClauseSb.append(",");	
				}
			}
			whereClauseSb.append(")");
		}
		if ( (materialPackageCdList != null) && (!materialPackageCdList.isEmpty()) ) {
			whereClauseSb = whereClauseSb!=null?whereClauseSb.append(" AND "):new StringBuilder();
			whereClauseSb.append(ATTR_MATERIAL_PACKAGE_CD + " IN (");
			for (Iterator it=materialPackageCdList.iterator();it.hasNext();) {
				it.next();
				whereClauseSb.append("?");
				if (it.hasNext()) {
					whereClauseSb.append(",");	
				}
			}
			whereClauseSb.append(")");
		}		
		if (finishedCommercial != null) {
			whereClauseSb = whereClauseSb!=null?whereClauseSb.append(" AND "):new StringBuilder();
			whereClauseSb.append(ATTR_REPORT_FINISHED_COMM_FLAG + " = ?");
		}
		
		return whereClauseSb!=null?whereClauseSb.toString():" 1=1 ";
	}
	
	public Object[] getQParams() {
		
		List qParams = new ArrayList();
		
		SimpleDateFormat simpleDateFormat = getSimpleDateFormatWithGMTZone();
		
		if (langCd != null) {
			qParams.add(langCd);
		}
		if (deliveryFromDat != null) {
			qParams.add(simpleDateFormat.format(DateUtils.truncate(deliveryFromDat, Calendar.HOUR_OF_DAY)));
		}
		if (deliveryToDat != null) {
			qParams.add(simpleDateFormat.format(DateUtils.addDays(DateUtils.truncate(deliveryToDat, Calendar.HOUR_OF_DAY),1)));
		}
		if ( (distributionDestCdList != null) && (!distributionDestCdList.isEmpty()) ) {
			for (Iterator it=distributionDestCdList.iterator();it.hasNext();) {
				String s = it.next().toString();
				qParams.add(s);	
			}			
		}
		if ( (materialPackageCdList != null) && (!materialPackageCdList.isEmpty()) ) {
			for (Iterator it=materialPackageCdList.iterator();it.hasNext();) {
				String s = it.next().toString();
				qParams.add(s);
			}
		}
		if (finishedCommercial != null) {
			qParams.add(finishedCommercial.booleanValue()?new Integer(1):new Integer(0));
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

	public List getDistributionDestCdList() {
		return distributionDestCdList;
	}

	public void setDistributionDestCdList(List distributionDestCdList) {
		this.distributionDestCdList = distributionDestCdList;
	}

	public List getMaterialPackageCdList() {
		return materialPackageCdList;
	}

	public void setMaterialPackageCdList(List materialPackageCdList) {
		this.materialPackageCdList = materialPackageCdList;
	}

	public Boolean getFinishedCommercial() {
		return finishedCommercial;
	}

	public void setFinishedCommercial(Boolean finishedCommercial) {
		this.finishedCommercial = finishedCommercial;
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
			setDeliveryFromDat(fromDate);
		}

		if (!StringUtils.empty(request.getParameter(PARAM_TO_DAT))) {
			Date toDate = DateTimeUtils.parseDayMonthYearInGMTZone(request.getParameter(PARAM_TO_DAT));
			toDate = DateTimeUtils.getMidnightOfDayInGMTZone(toDate);
			setDeliveryToDat(toDate);
		}
		
		List<String> distributionDestCDList = getSelectedOptions(
				PARAM_DISTRIBUTION_DEST_CD_LIST, request);
		setDistributionDestCdList(distributionDestCDList);

		List<String> materialPackageCDList = getSelectedOptions(
				PARAM_MATERIAL_PACKAGE_CD_LIST, request);
		setMaterialPackageCdList(materialPackageCDList);

		// Finished/finished commercial is what needs to be determined,
		// currently implemented as a flag,
		// but should actually be an order status. Transform from
		// (non-existing)order_status_cd=99 to finished-commercial or not.
		List selectedStatusList = getSelectedOptions(PARAM_ORDER_STATUS_CD_LIST, request);
		if ((selectedStatusList != null) && (selectedStatusList.size() == 1)) {
			boolean isFinishedCommercial = selectedStatusList
					.contains(VIRTUAL_ORDER_STATUS_FINISHED_COMMERCIAL);
			setFinishedCommercial(isFinishedCommercial);
		}

	}
}
