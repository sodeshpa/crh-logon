/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.utils.dispocard;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.reporting.ExportMetaData;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.constants.Constant;

/**
 * @author AdNovum Informatik AG
 */
public class DispocardSearchExportMetaData extends ExportMetaData {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DispocardSearchExportMetaData.class);

	// db fields
	private static final String QUERY_COLUMN_1 = "SHIPTO_NR";
	private static final String QUERY_COLUMN_2 = "SHIPTO_NAME";
	private static final String QUERY_COLUMN_3 = "SAREA_DISPLAY_LONGNAME";
	private static final String QUERY_COLUMN_4 = "SAP_CONTRACT_NO";
	public  static final String QUERY_COLUMN_5 = "ENABLED_FLAG";
	private static final String QUERY_COLUMN_6 = "SAP_MATERIAL_NO";
	private static final String QUERY_COLUMN_7 = "MATERIAL_TXT";
	private static final List<String> QUERY_COLUMNS = new ArrayList<String>();
	static {
		QUERY_COLUMNS.add(QUERY_COLUMN_1);
		QUERY_COLUMNS.add(QUERY_COLUMN_2);
		QUERY_COLUMNS.add(QUERY_COLUMN_3);
		QUERY_COLUMNS.add(QUERY_COLUMN_4);
		QUERY_COLUMNS.add(QUERY_COLUMN_5);
		QUERY_COLUMNS.add(QUERY_COLUMN_6);
		QUERY_COLUMNS.add(QUERY_COLUMN_7);
	}
	
	// db fields mapped to resources keys, Map<DB_FIELD_NAME, RESOURCES_KEY>
	// NOTE: all columns should be mapped here
	public static final Map<String, String> columnNamesMap = new HashMap<String, String>();
	static {
		columnNamesMap.put(QUERY_COLUMN_1, "dispocardsearch.table.header.shiptoNumber");
		columnNamesMap.put(QUERY_COLUMN_2, "dispocardsearch.table.header.shiptoName");
		columnNamesMap.put(QUERY_COLUMN_3, "dispocardsearch.table.header.sareaName");
		columnNamesMap.put(QUERY_COLUMN_4, "dispocardsearch.table.header.contractNumber");
		columnNamesMap.put(QUERY_COLUMN_5, "dispocardsearch.table.header.active");
		columnNamesMap.put(QUERY_COLUMN_6, "dispocardsearch.table.header.materialNumber");
		columnNamesMap.put(QUERY_COLUMN_7, "dispocardsearch.table.header.materialName");
	}

	private static final String RAW_QUERY_STRING = 
		"SELECT {0} FROM " + Constant.SCHEMA_USER_PREPEND + "VLGNC_SHIPTO_SAREA_RESTR_SEAR " +
		"WHERE {1} AND ({2}) AND ({3}) AND ({4}) AND ({5}) AND ({6})";
	
	// filters
	private String langCd;
	private Long sapMaterialId;
	private List deliveryTypeCdList;
	private Long poolId;
	private Long carrierId;
	private String plantCd;

	private Object[] qParams;
	private String queryString;
	
	/**
	 * Default constructor
	 */
	public DispocardSearchExportMetaData() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "DispocardSearchExportMetaData");
		}
		// ]]
		try {
			; // Avoid PMD
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public void init(ServiceLocator serviceLocator, long uid) {
		// [[
		Object[][] params = {{"serviceLocator", serviceLocator},
							{"uid", uid + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init", params);
		}
		// ]]
		try {
			
			List qParamList = new ArrayList();
			List qFormatParams = new ArrayList();
			
			qFormatParams.add(getQueryColumns());
			qFormatParams.add(getWhereClause(qParamList));
			
			// Data access authorization
			addAccessRestriction(ExportMetaData.USER_POOL_ACC, "POOL_ID", qParamList, qFormatParams, serviceLocator, uid);
			addAccessRestriction(ExportMetaData.USER_PLANT_ACC, "PLANT_CD", qParamList, qFormatParams, serviceLocator, uid);
			addAccessRestriction(ExportMetaData.USER_SAREA_ACC, "SAREA_ID", qParamList, qFormatParams, serviceLocator, uid);
			addAccessRestriction(ExportMetaData.USER_SHIPPINGPOINT_ACC, "SHIPPINGPOINT_ID", qParamList, qFormatParams, serviceLocator, uid);
			addAccessRestriction(ExportMetaData.USER_SHIPTO_ACC, "SHIPTO_SAP_BP_ID", qParamList, qFormatParams, serviceLocator, uid);
			
			MessageFormat formatter = new MessageFormat("");
			formatter.applyPattern(RAW_QUERY_STRING);
			this.queryString = formatter.format(qFormatParams.toArray());
			this.qParams = qParamList.toArray();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public Object[] getQParams() {		
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("getQParams(): '" + qParams + "'");
		}
		// ]]
		return this.qParams;
	}
	
	public String getQueryString() {		
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("getQueryString(): '" + queryString + "'");
		}
		// ]]
		return this.queryString;
	}
	
	private StringBuilder addEqToWhereClause(StringBuilder sb, String attrName, Object attrValue, Collection qParams) {
		// [[
		StringBuilder result = null;
		Object[][] params = {{"sb", sb},
							{"attrName", attrName},
							{"attrValue", attrValue},
							{"qParams", qParams}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addEqToWhereClause", params);
		}
		// ]]
		try {			
			if (attrValue != null) {
				sb = sb != null ? sb.append(" AND ") : new StringBuilder();
				sb.append(attrName + " = ?");
				qParams.add(attrValue);
			}
			
			return result = sb;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private StringBuilder addInToWhereClause(StringBuilder sb, String attrName, Collection attrValue, Collection qParams) {
		// [[
		StringBuilder result = null;
		Object[][] params = {{"sb", sb},
							{"attrName", attrName},
							{"attrValue", attrValue},
							{"qParams", qParams}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addInToWhereClause", params);
		}
		// ]]
		try {		
			if ((attrValue != null) && (!attrValue.isEmpty())) {
				sb = sb != null ? sb.append(" AND ") : new StringBuilder();
				sb.append(attrName + " IN (");
				for (Iterator it = attrValue.iterator(); it.hasNext();) {
					Object attrElemValue = it.next();
					sb.append("?");
					qParams.add(attrElemValue);
					if (it.hasNext()) {
						sb.append(",");
					}
				}
				sb.append(")");
			}
			
			return result = sb;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private String getWhereClause(Collection qParams) {
		// [[
		String result = null;
		Object[][] params = {{"qParams", qParams}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getWhereClause", params);
		}
		// ]]
		try {			
			StringBuilder whereClauseSb = null;
			
			whereClauseSb = addEqToWhereClause(whereClauseSb, "LANG_CD", getLangCd(), qParams);
			whereClauseSb = addEqToWhereClause(whereClauseSb, "SAP_MATERIAL_ID", getSapMaterialId(), qParams);
			whereClauseSb = addInToWhereClause(whereClauseSb, "DELIVERY_TYPE_CD", getDeliveryTypeCdList(), qParams);
			whereClauseSb = addEqToWhereClause(whereClauseSb, "POOL_ID", getPoolId(), qParams);
			whereClauseSb = addEqToWhereClause(whereClauseSb, "COMM_CARRIER_ID", getCarrierId(), qParams);
			whereClauseSb = addEqToWhereClause(whereClauseSb, "PLANT_CD", getPlantCd(), qParams);
									
			return result = whereClauseSb != null ? whereClauseSb.toString() : null;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private String getQueryColumns() {
		// [[
		String result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getQueryColumns");
		}
		// ]]
		try {
			StringBuilder builder = new StringBuilder();
			
			for (String queryColumn : QUERY_COLUMNS) {
				builder.append(queryColumn);
				builder.append(",");
			}
			return result = builder.substring(0, builder.length() -1);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public String getLangCd() {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("getLangCd(): '" + langCd + "'");
		}
		// ]]
		return langCd;
	}

	public void setLangCd(String langCd) {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("setLangCd(): '" + langCd + "'");
		}
		// ]]
		this.langCd = langCd;
	}

	public Long getSapMaterialId() {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("getSapMaterialId(): '" + sapMaterialId + "'");
		}
		// ]]
		return sapMaterialId;
	}

	public void setSapMaterialId(Long sapMaterialId) {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("setSapMaterialId(): '" + sapMaterialId + "'");
		}
		// ]]
		this.sapMaterialId = sapMaterialId;
	}

	public List getDeliveryTypeCdList() {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("getDeliveryTypeCdList(): '" + deliveryTypeCdList + "'");
		}
		// ]]
		return deliveryTypeCdList;
	}

	public void setDeliveryTypeCdList(List deliveryTypeCdList) {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("setDeliveryTypeCdList(): '" + deliveryTypeCdList + "'");
		}
		// ]]
		this.deliveryTypeCdList = deliveryTypeCdList;
	}

	public Long getPoolId() {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("getPoolId(): '" + poolId + "'");
		}
		// ]]
		return poolId;
	}

	public void setPoolId(Long poolId) {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("setPoolId(): '" + poolId + "'");
		}
		// ]]
		this.poolId = poolId;
	}

	public Long getCarrierId() {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("getCarrierId(): '" + carrierId + "'");
		}
		// ]]
		return carrierId;
	}

	public void setCarrierId(Long carrierId) {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("setCarrierId(): '" + carrierId + "'");
		}
		// ]]
		this.carrierId = carrierId;
	}

	public String getPlantCd() {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("getPlantCd(): '" + plantCd + "'");
		}
		// ]]
		return plantCd;
	}

	public void setPlantCd(String plantCd) {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("setPlantCd(): '" + plantCd + "'");
		}
		// ]]
		this.plantCd = plantCd;
	}

}
