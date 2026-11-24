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

import com.crh.logon.business.services.reporting.ExportMetaData;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.constants.Constant;
import org.apache.commons.lang.time.DateUtils;

/**
 * @author AdNovum Informatik AG
 */
public class OrderSearchExportMetaData extends ExportMetaData {

	private static final String RAW_QUERY_STRING = 
		"SELECT ORDER_NUMBER || '' ('' || ORDER_ITEM_NO || '')'' AS ORDER_NUMBER, DISTRIBUTION_DEST_CD AS TYPE, " +
		"SOLDTO_NAME1 AS SOLDTO, SHIPTO_NAME1 AS SHIPTO, SHIPTO_CITY AS CITY, " +
		"STATUS_TXT AS STATUS, MATERIAL_TXT AS PRODUCT, ORDERED_QUANTITY, ORDERED_UOM_TXT AS ORDERED_UOM, " +
		"LOADED_QUANTITY, LOADED_UOM_TXT AS LOADED_UOM, RETURNED_QUANTITY, RETURNED_UOM_TXT AS RETURNED_UOM, " +
		"DELIVERED_QUANTITY, DELIVERED_UOM_TXT AS DELIVERED_UOM, DELIVERY_TYPE_TXT AS DELIVERY_TYPE, " +
		"SHIPPINGPOINT_NAME AS SHIPPINGPOINT, DELIVERY_FROM_DAT, DELIVERY_TO_DAT, PLANNED_DELIVERY_DAT, " +
		"POOL_NAME AS POOL, COMM_CARRIER_NAME AS CARRIER, EXEC_CARRIER_NAME, BILLTO_NAME1 AS BILLTO, DRIVER_FULL_NAME AS DRIVER, " +
		"VEHICLE_NAME AS VEHICLE, ORDER_VALIDATION_CODE, LDS_DELIVERY_NOTE_NO, SHIPTO_ADDRESS, NOTE_ORDER_MGMT, NOTE_DELIVERY, NOTE_DISPATCHING," +
		"NOTE_TRUCK_DRIVER,  NOTE_RAILWAY_DISP, ORDER_NUMBER_FROM_SEQ_USAGE, LOADING_TS, MATERIAL_PACKAGE_CD, " +
		"CUSTORDER_CUSTREF_NO, CHECK_UNLOAD_LOCATION " +
		"FROM " + Constant.SCHEMA_USER_PREPEND + "VLGNC_ORDER_SEARCH_EXPORT " +
		"WHERE {0} AND ({1}) AND ({2}) AND ( ({3}) OR ({4}) ) AND ({5}) AND ({6}) AND ({7}) AND ({8}) AND ({9}) AND ({10}) AND ({11}) ";
	
	private String langCd;
	private Long orderNumber;
	private Long soldtoSapBpId;
	private Long shiptoSapBpId;
	private Long billtoSapBpId;
	private Long sapMaterialId;
	private Date deliveryFromDat;
	private Date deliveryToDat;
	private List deliveryTypeCdList;
	private List orderCreationTypeCdList;
	private List destinationCdList;
	private Long shippingpointId;
	private Long poolId;
	private Long execCarrierId;
	private List carrierIdList;
	private List statusList;
	private String sareaId;
	private String shiptoSofficeCd;
	private String shiptoSgroupCd;
	private List plantCdList;
	private Long wagonId;
	private Long truckId;
	private String ldsDeliveryNoteNo;
	private String materialPackageCd;
	
	private Object[] qParams;
	private String queryString;
	private String countQueryString;
	
	public Object[] getQParams() {
		return qParams;
	}

	public String getCountQueryString() {
		return countQueryString;
	}
	
	public String getQueryString() {
		return queryString;
	}
	
	private StringBuilder addEqToWhereClause(StringBuilder sb, String attrName, Object attrValue, Collection qParams) {
		if (attrValue != null) {
			sb = sb!=null?sb.append(" AND "):new StringBuilder();
			sb.append(attrName + " = ?");
			qParams.add(attrValue);
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
		whereClauseSb = addEqToWhereClause(whereClauseSb, "ORDER_NUMBER", orderNumber, qParams);
		whereClauseSb = addEqToWhereClause(whereClauseSb, "SOLDTO_SAP_BP_ID", soldtoSapBpId, qParams);
		whereClauseSb = addEqToWhereClause(whereClauseSb, "SHIPTO_SAP_BP_ID", shiptoSapBpId, qParams);
		whereClauseSb = addEqToWhereClause(whereClauseSb, "BILLTO_SAP_BP_ID", billtoSapBpId, qParams);
		whereClauseSb = addEqToWhereClause(whereClauseSb, "SAP_MATERIAL_ID", sapMaterialId, qParams);
		
		if (deliveryFromDat != null) {
			whereClauseSb = whereClauseSb!=null?whereClauseSb.append(" AND "):new StringBuilder();
			whereClauseSb.append("DELIVERY_FROM_DAT >= to_date(?, 'yyyy/mm/dd')");
			qParams.add(simpleDateFormat.format(DateUtils.truncate(deliveryFromDat, Calendar.HOUR_OF_DAY)));
		}
		if (deliveryToDat != null) {
			whereClauseSb = whereClauseSb!=null?whereClauseSb.append(" AND "):new StringBuilder();
			whereClauseSb.append("DELIVERY_TO_DAT < to_date(?, 'yyyy/mm/dd')");
			qParams.add(simpleDateFormat.format(DateUtils.addDays(DateUtils.truncate(deliveryToDat, Calendar.HOUR_OF_DAY),1)));
		}
		whereClauseSb = addInToWhereClause(whereClauseSb, "DELIVERY_TYPE_CD", deliveryTypeCdList, qParams);
		whereClauseSb = addInToWhereClause(whereClauseSb, "ORDER_CREATION_TYPE_CD", orderCreationTypeCdList, qParams);
		whereClauseSb = addInToWhereClause(whereClauseSb, "DISTRIBUTION_DEST_CD", destinationCdList, qParams);
		whereClauseSb = addEqToWhereClause(whereClauseSb, "SHIPPINGPOINT_ID", shippingpointId, qParams);
		whereClauseSb = addEqToWhereClause(whereClauseSb, "POOL_ID", poolId, qParams);
		whereClauseSb = addEqToWhereClause(whereClauseSb, "EXEC_CARRIER_ID", execCarrierId, qParams);
		whereClauseSb = addInToWhereClause(whereClauseSb, "COMM_CARRIER_ID", carrierIdList, qParams);
		whereClauseSb = addInToWhereClause(whereClauseSb, "ORDER_STATUS_CD", statusList, qParams);
		whereClauseSb = addEqToWhereClause(whereClauseSb, "SAREA_ID", sareaId, qParams);
		whereClauseSb = addEqToWhereClause(whereClauseSb, "SHIPTO_SOFFICE_CD", shiptoSofficeCd, qParams);
		whereClauseSb = addEqToWhereClause(whereClauseSb, "SHIPTO_SGROUP_CD", shiptoSgroupCd, qParams);
		whereClauseSb = addInToWhereClause(whereClauseSb, "PLANT_CD", plantCdList, qParams);
		whereClauseSb = addEqToWhereClause(whereClauseSb, "WAGON_ID", wagonId, qParams);
		whereClauseSb = addEqToWhereClause(whereClauseSb, "TRUCK_ID", truckId, qParams);
		whereClauseSb = addEqToWhereClause(whereClauseSb, "LDS_DELIVERY_NOTE_NO", ldsDeliveryNoteNo, qParams);
		whereClauseSb = addEqToWhereClause(whereClauseSb, "MATERIAL_PACKAGE_CD", materialPackageCd, qParams);
		
		
								
		return whereClauseSb!=null?whereClauseSb.toString():null;
	}

	public void init(ServiceLocator serviceLocator, long uid) {
		
		List qParamList = new ArrayList();
		List qFormatParams = new ArrayList();
		
		qFormatParams.add(getWhereClause(qParamList));
		
		// Data access authorization
		addAccessRestriction(ExportMetaData.USER_POOL_ACC, "POOL_ID", qParamList, qFormatParams, serviceLocator, uid);
		addAccessRestriction(ExportMetaData.USER_PLANT_ACC, "PLANT_CD", qParamList, qFormatParams, serviceLocator, uid);
		addAccessRestriction(ExportMetaData.USER_SAP_VENDOR_ACC, "EXEC_SAP_VENDOR_ID", qParamList, qFormatParams, serviceLocator, uid);
		addAccessRestriction(ExportMetaData.USER_SAP_VENDOR_ACC, "COMM_SAP_VENDOR_ID", qParamList, qFormatParams, serviceLocator, uid);
		addAccessRestriction(ExportMetaData.USER_SAREA_ACC, "SAREA_ID", qParamList, qFormatParams, serviceLocator, uid);
		addAccessRestriction(ExportMetaData.USER_COMPANYCODE_ACC, "COMPANYCODE_CD", qParamList, qFormatParams, serviceLocator, uid);
		addAccessRestriction(ExportMetaData.USER_SGROUP_ACC, "SGROUP_CD", qParamList, qFormatParams, serviceLocator, uid);
		addAccessRestriction(ExportMetaData.USER_SHIPPINGPOINT_ACC, "SHIPPINGPOINT_ID", qParamList, qFormatParams, serviceLocator, uid);
		addAccessRestriction(ExportMetaData.USER_SHIPTO_ACC, "SHIPTO_SAP_BP_ID", qParamList, qFormatParams, serviceLocator, uid);
		addAccessRestriction(ExportMetaData.USER_SOLDTO_BP_ACC, "SOLDTO_SAP_BP_ID", qParamList, qFormatParams, serviceLocator, uid);
		addAccessRestriction(ExportMetaData.USER_REGION_ACC, "POOL_ID", qParamList, qFormatParams, serviceLocator, uid);
		
		MessageFormat formatter = new MessageFormat("");
		formatter.applyPattern(RAW_QUERY_STRING);
		this.queryString = formatter.format(qFormatParams.toArray());
		this.countQueryString = "select count(*) from (" + queryString + ")";
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

	public Long getSoldtoSapBpId() {
		return soldtoSapBpId;
	}

	public void setSoldtoSapBpId(Long soldtoSapBpId) {
		this.soldtoSapBpId = soldtoSapBpId;
	}

	public Long getShiptoSapBpId() {
		return shiptoSapBpId;
	}

	public void setShiptoSapBpId(Long shiptoSapBpId) {
		this.shiptoSapBpId = shiptoSapBpId;
	}

	public Long getBilltoSapBpId() {
		return billtoSapBpId;
	}

	public void setBilltoSapBpId(Long billtoSapBpId) {
		this.billtoSapBpId = billtoSapBpId;
	}

	public Long getSapMaterialId() {
		return sapMaterialId;
	}

	public void setSapMaterialId(Long sapMaterialId) {
		this.sapMaterialId = sapMaterialId;
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

	public List getDeliveryTypeCdList() {
		return deliveryTypeCdList;
	}

	public void setDeliveryTypeCdList(List deliveryTypeCdList) {
		this.deliveryTypeCdList = deliveryTypeCdList;
	}

	public List getDestinationCdList() {
		return destinationCdList;
	}

	public List getOrderCreationTypeCdList() {
		return orderCreationTypeCdList;
	}

	public void setOrderCreationTypeCdList(List orderCreationTypeCdList) {
		this.orderCreationTypeCdList = orderCreationTypeCdList;
	}

	public void setDestinationCdList(List destinationCdList) {
		this.destinationCdList = destinationCdList;
	}

	public Long getShippingpointId() {
		return shippingpointId;
	}

	public void setShippingpointId(Long shippingpointId) {
		this.shippingpointId = shippingpointId;
	}

	public Long getPoolId() {
		return poolId;
	}

	public void setPoolId(Long poolId) {
		this.poolId = poolId;
	}

	public List getCarrierIdList() {
		return carrierIdList;
	}

	public void setCarrierIdList(List carrierIdList) {
		this.carrierIdList = carrierIdList;
	}

	public List getStatusList() {
		return statusList;
	}

	public void setStatusList(List statusList) {
		this.statusList = statusList;
	}

	public String getSareaId() {
		return sareaId;
	}

	public void setSareaId(String sareaId) {
		this.sareaId = sareaId;
	}

	public String getShiptoSofficeCd() {
		return shiptoSofficeCd;
	}

	public void setShiptoSofficeCd(String shiptoSofficeCd) {
		this.shiptoSofficeCd = shiptoSofficeCd;
	}

	public String getShiptoSgroupCd() {
		return shiptoSgroupCd;
	}

	public void setShiptoSgroupCd(String shiptoSgroupCd) {
		this.shiptoSgroupCd = shiptoSgroupCd;
	}

	public List getPlantCdList() {
		return plantCdList;
	}

	public void setPlantCdList(List plantCdList) {
		this.plantCdList = plantCdList;
	}

	public Long getWagonId() {
		return wagonId;
	}

	public void setWagonId(Long wagonId) {
		this.wagonId = wagonId;
	}

	public Long getTruckId() {
		return truckId;
	}

	public void setTruckId(Long truckId) {
		this.truckId = truckId;
	}

	public String getLdsDeliveryNoteNo() {
		return ldsDeliveryNoteNo;
	}

	public void setLdsDeliveryNoteNo(String ldsDeliveryNoteNo) {
		this.ldsDeliveryNoteNo = ldsDeliveryNoteNo;
	}

	public Long getExecCarrierId() {
		return execCarrierId;
	}

	public void setExecCarrierId(Long execCarrierId) {
		this.execCarrierId = execCarrierId;
	}

	public String getMaterialPackageCd() {
		return materialPackageCd;
	}

	public void setMaterialPackageCd(String materialPackageCd) {
		this.materialPackageCd = materialPackageCd;
	}
	
}
