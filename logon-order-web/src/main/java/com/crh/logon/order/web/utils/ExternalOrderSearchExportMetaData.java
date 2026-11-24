/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

import com.crh.logon.business.services.reporting.ExportMetaData;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.constants.Constant;

/**
 * @author AdNovum Informatik AG
 */
public class ExternalOrderSearchExportMetaData extends ExportMetaData {

	private static final String RAW_QUERY_STRING = 
		"SELECT " +
			"EXT_ORDER_NUMBER AS EXT_ORDER_NUMBER, " +
			"EXT_DELIVERY_NOTE," +
			"DELIVERY_FROM_DAT, " +
			"DELIVERY_TO_DAT, " +
			"SHIPPINGPOINT_NAME as SHIPPINGPOINT, " +
			"SP_LOAD_UNLOAD, " +
			"SP_CONTACT_PERSON_TXT, " +
			"SP_STREET_HOUSE_NO, " +
			"SP_COUNTRY, " +
			"SP_POSTAL_CODE, " +
			"SP_CITY, " +
			"SHIPTO_NAME as SHIPTO, " +
			"SHIPTO_LOAD_UNLOAD, " +
			"SHIPTO_CONTACT_PERSON_TXT, " +
			"SHIPTO_STREET_HOUSE_NO, " +
			"SHIPTO_COUNTRY, " +
			"SHIPTO_POSTAL_CODE, " +
			"SHIPTO_CITY, " +
			"STATUS_TXT as STATUS, " +
			"MATERIAL_NAME as PRODUCT, " +
			"ORDERED_QUANTITY, " +
			"ORDERED_UOM_NAME as ORDERED_UOM, " +
			"DELIVERED_QUANTITY, " +
			"DELIVERED_UOM_NAME as DELIVERED_UOM, " +
			"TRUCK_NAME as VEHICLE " +
		"FROM " + Constant.SCHEMA_USER_PREPEND + "VLGNC_EXT_ORDER_SEARCH " +
		"WHERE {0} ";
	
	private String langCd;
	private Long orderNumber;
	private Date deliveryFromDat;
	private Date deliveryToDat;
	private List statusList;
	private Long truckId;
	private String extDeliveryNote;

	private Object[] qParams;
	private String queryString;
	
	public Object[] getQParams() {
		
		return qParams;
	}
	
	public String getQueryString() {
		
		return queryString;
	}
	
	private StringBuilder addEqToWhereClause(StringBuilder sb, String attrName, Object attrValue, Collection qParams) {
		
		if (attrValue != null) {
			sb = (sb != null ? sb.append(" AND ") : new StringBuilder());
			sb.append(attrName + " = ?");
			qParams.add(attrValue);
		}
	
		return sb;
	}
	
	private StringBuilder addIlikeToWhereClause(StringBuilder sb, String attrName, Object attrValue, Collection qParams) {
		
		if (attrValue != null) {
			sb = (sb != null ? sb.append(" AND ") : new StringBuilder());
			sb.append("upper(" + attrName + ") like upper(?)");
			qParams.add("%" + attrValue + "%");
		}
		
		return sb;
	}
	
	private StringBuilder addInToWhereClause(StringBuilder sb, String attrName, Collection attrValue, Collection qParams) {
		
		if ( (attrValue != null) && (!attrValue.isEmpty()) ) {
			sb = sb!=null?sb.append(" AND "):new StringBuilder();
			sb.append(attrName + " IN (");
			for (Iterator it=attrValue.iterator();it.hasNext();) {
				Object attrElemValue = it.next();
				sb.append("?");
				qParams.add(attrElemValue);
				if (it.hasNext()) {
					sb.append(",");	
				}
			}
			sb.append(")");
		}
	
		return sb;
	}
	
	private String getWhereClause(Collection qParams) {
		
		StringBuilder whereClauseSb = null;
		
		SimpleDateFormat simpleDateFormat = getSimpleDateFormatWithGMTZone();
		
		whereClauseSb = addEqToWhereClause(whereClauseSb, "LANG_CD", langCd, qParams);
		whereClauseSb = addEqToWhereClause(whereClauseSb, "EXT_ORDER_NUMBER", orderNumber, qParams);
		
		if (deliveryFromDat != null) {
			whereClauseSb = whereClauseSb != null ? whereClauseSb.append(" AND ") : new StringBuilder();
			whereClauseSb.append("DELIVERY_FROM_DAT >= to_date(?, 'yyyy/mm/dd')");
			qParams.add(simpleDateFormat.format(DateUtils.truncate(deliveryFromDat, Calendar.HOUR_OF_DAY)));
		}
		if (deliveryToDat != null) {
			whereClauseSb = whereClauseSb != null ? whereClauseSb.append(" AND ") : new StringBuilder();
			whereClauseSb.append("DELIVERY_TO_DAT < to_date(?, 'yyyy/mm/dd')");
			qParams.add(simpleDateFormat.format(DateUtils.addDays(DateUtils.truncate(deliveryToDat, Calendar.HOUR_OF_DAY),1)));
		}
		whereClauseSb = addInToWhereClause(whereClauseSb, "ORDER_STATUS_CD", statusList, qParams);
		whereClauseSb = addEqToWhereClause(whereClauseSb, "TRUCK_ID", truckId, qParams);
		whereClauseSb = addIlikeToWhereClause(whereClauseSb, "EXT_DELIVERY_NOTE", getExtDeliveryNote(), qParams);
		
		return whereClauseSb != null ? whereClauseSb.toString() : null;
	}

	public void init(ServiceLocator serviceLocator, long uid) {
		
		List qParamList = new ArrayList();
		List qFormatParams = new ArrayList();
		
		qFormatParams.add(getWhereClause(qParamList));
		
		// Data access authorization
		// TODO authorization
		/*
		addAccessRestriction(ExportMetaData.USER_POOL_ACC, "POOL_ID", qParamList, qFormatParams, serviceLocator, uid);
		addAccessRestriction(ExportMetaData.USER_SAP_VENDOR_ACC, "SAP_VENDOR_ID", qParamList, qFormatParams, serviceLocator, uid);
		*/
		MessageFormat formatter = new MessageFormat("");
		formatter.applyPattern(RAW_QUERY_STRING);
					
		this.queryString = formatter.format(qFormatParams.toArray());
		this.qParams = qParamList.toArray();
	}
	
	public String getLangCd() {
		return langCd;
	}

	public void setLangCd(String langCd) {
		this.langCd = langCd;
	}

	public void setOrderNumber(Long orderNumber) {
		
		this.orderNumber = orderNumber;
	}
	
	public Long getOrderNumber() {
		
		return this.orderNumber;
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

	public List getStatusList() {
		return statusList;
	}

	public void setStatusList(List statusList) {
		this.statusList = statusList;
	}

	public Long getTruckId() {
		return truckId;
	}

	public void setTruckId(Long truckId) {
		this.truckId = truckId;
	}

	public String getExtDeliveryNote() {
		return extDeliveryNote;
	}

	public void setExtDeliveryNote(String extDeliveryNote) {
		this.extDeliveryNote = extDeliveryNote;
	}

}
