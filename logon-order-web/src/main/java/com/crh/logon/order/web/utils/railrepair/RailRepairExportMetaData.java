/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils.railrepair;

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
public class RailRepairExportMetaData extends ExportMetaData {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailRepairExportMetaData.class);

	// SQL select script fields
	public static final String QUERY_COLUMN_1 = "WAGON_NO";
	private static final String QUERY_COLUMN_2 = "WAGON_KEEPER_NAME";
	private static final String QUERY_COLUMN_3 = "SHIPTO_NAME";
	private static final String QUERY_COLUMN_4 = "ORDER_NO";
	public  static final String QUERY_COLUMN_5 = "CREATED";
	public static final String QUERY_COLUMN_6 = "ARRIVED";
	public static final String QUERY_COLUMN_7 = "WAGON_IDLE_TIME_MIN";
	private static final String QUERY_COLUMN_8 = "DAMAGECODE";
	public static final String QUERY_COLUMN_9 = "MAINTYPE"; 
	private static final String QUERY_COLUMN_9_1 = "MAINT_G10_MAIN_TYPE_TXT";
	private static final String QUERY_COLUMN_9_2 = "MAINT_G20_MAIN_TYPE_TXT";
	private static final String QUERY_COLUMN_9_3 = "MAINT_G30_MAIN_TYPE_TXT";
	private static final String QUERY_COLUMN_9_4 = "MAINT_G40_MAIN_TYPE_TXT";
	private static final String QUERY_COLUMN_9_5 = "MAINT_G42_MAIN_TYPE_TXT";
	private static final String QUERY_COLUMN_9_6 = "MAINT_G43_MAIN_TYPE_TXT";
	private static final String QUERY_COLUMN_9_7 = "MAINT_G48_MAIN_TYPE_TXT";
	private static final String QUERY_COLUMN_9_8 = "MAINT_P3M_MAIN_TYPE_TXT";
	private static final String QUERY_COLUMN_9_9 = "MAINT_P5_MAIN_TYPE_TXT";
	private static final String QUERY_COLUMN_9_10 = "MAINT_C_MAIN_TYPE_TXT";
	private static final String QUERY_COLUMN_10 = "REMARKS";
	public static final String QUERY_COLUMN_11 = "OFFER";
	private static final String QUERY_COLUMN_11_1 = "OFFER_DESIRED_FLAG";
	private static final String QUERY_COLUMN_11_2 = "HAS_OFFER_ATTACHMENT_FLAG";
	private static final String QUERY_COLUMN_11_3 = "OFFER_ACCEPT_DAT";
	public static final String QUERY_COLUMN_12 = "DOCUMENT";
	private static final String QUERY_COLUMN_12_1 = "HAS_DOCU_ATTACHMENT_FLAG";
	private static final String QUERY_COLUMN_12_2 = "DOCUMENTATION_ACCEPT_FLAG";
	public static final String QUERY_COLUMN_13 = "INVOICE";
	private static final String QUERY_COLUMN_13_1 = "HAS_INVOICE_ATTACHMENT_FLAG";
	private static final String QUERY_COLUMN_13_2 = "INVOICE_PAID_FLAG";
	private static final String QUERY_COLUMN_14 = "ORDER_STATUS_TXT";
	private static final String QUERY_COLUMN_15 = "PLANT_ENTER";
	private static final String QUERY_COLUMN_16 = "PLANT_LEAVE";
	
	/**
	 * SQL select statement Query columns. 
	 */
	private static final List<String> QUERY_COLUMNS = new ArrayList<String>();
	static {
		QUERY_COLUMNS.add(QUERY_COLUMN_1);
		QUERY_COLUMNS.add(QUERY_COLUMN_2);
		QUERY_COLUMNS.add(QUERY_COLUMN_3);
		QUERY_COLUMNS.add(QUERY_COLUMN_4);
		QUERY_COLUMNS.add(QUERY_COLUMN_14);
		QUERY_COLUMNS.add(QUERY_COLUMN_5);
		QUERY_COLUMNS.add(QUERY_COLUMN_6);
		QUERY_COLUMNS.add(QUERY_COLUMN_15);
		QUERY_COLUMNS.add(QUERY_COLUMN_16);
		QUERY_COLUMNS.add(QUERY_COLUMN_7);
		QUERY_COLUMNS.add(QUERY_COLUMN_8);
		QUERY_COLUMNS.add(generateColumn9(QUERY_COLUMN_9, QUERY_COLUMN_9_1, QUERY_COLUMN_9_2,
				QUERY_COLUMN_9_3, QUERY_COLUMN_9_4, QUERY_COLUMN_9_5, QUERY_COLUMN_9_6,
				QUERY_COLUMN_9_7, QUERY_COLUMN_9_8, QUERY_COLUMN_9_9, QUERY_COLUMN_9_10));
		QUERY_COLUMNS.add(QUERY_COLUMN_10);
		QUERY_COLUMNS.add(generateColumn11());
		QUERY_COLUMNS.add(generateColumn12());
		QUERY_COLUMNS.add(generateColumn13());
	}
	
	/**
	 * SQL select columns mapped to resources keys, Map<DB_FIELD_NAME, RESOURCES_KEY>
	 * NOTE: all columns MUST be mapped here (used at the header in the .CSV)
	 */
	public static final Map<String, String> columnNamesMap = new HashMap<String, String>();
	static {
		columnNamesMap.put(QUERY_COLUMN_1, "railrepair.column.wagonNo");
		columnNamesMap.put(QUERY_COLUMN_2, "railrepair.column.wagonKeeperName");
		columnNamesMap.put(QUERY_COLUMN_3, "railrepair.column.shiptoName");
		columnNamesMap.put(QUERY_COLUMN_4, "railrepair.column.orderNo");
		columnNamesMap.put(QUERY_COLUMN_5, "railrepair.column.created");
		columnNamesMap.put(QUERY_COLUMN_6, "railrepair.column.arrived");
		columnNamesMap.put(QUERY_COLUMN_7, "railrepair.column.wagonIdleTime");
		columnNamesMap.put(QUERY_COLUMN_8, "railrepair.column.damagecode");
		columnNamesMap.put(QUERY_COLUMN_9, "railrepair.column.mainttype");
		columnNamesMap.put(QUERY_COLUMN_10, "railrepair.column.remarks");
		columnNamesMap.put(QUERY_COLUMN_11, "railrepair.column.offer");
		columnNamesMap.put(QUERY_COLUMN_12, "railrepair.column.document");
		columnNamesMap.put(QUERY_COLUMN_13, "railrepair.column.invoice");
		columnNamesMap.put(QUERY_COLUMN_14, "railrepair.column.orderStatus");
		columnNamesMap.put(QUERY_COLUMN_15, "railrepair.column.plant.enter");
		columnNamesMap.put(QUERY_COLUMN_16, "railrepair.column.plant.leave");
	}

	private static final String COL_VAL_NEIN_KEY = "NEIN";
	private static final String COL_VAL_OFFEN_KEY = "OFFEN";
	private static final String COL_VAL_ERHALTEN_KEY = "ERHALTEN";
	private static final String COL_VAL_AKZEPTIERT_KEY = "AKZEPTIERT";
	
	/**
	 * Map containing the special column values from the select, mapped to resource keys.
	 */
	public static final Map<String, String> columnValKeyMap = new HashMap<String, String>();
	static {
		columnValKeyMap.put(COL_VAL_NEIN_KEY, "railrepair.value.nein");
		columnValKeyMap.put(COL_VAL_OFFEN_KEY, "railrepair.value.offen");
		columnValKeyMap.put(COL_VAL_ERHALTEN_KEY, "railrepair.value.erhalten");
		columnValKeyMap.put(COL_VAL_AKZEPTIERT_KEY, "railrepair.value.akzeptiert");
	}

	private static final String RAW_QUERY_STRING = "SELECT {0} FROM "
			+ Constant.SCHEMA_USER_PREPEND + "VLGNC_RAIL_REPAIR "
			+ "WHERE {1} AND ({2})";
	
	// filters
	private String langCd;
	private List<Long> shiptoSapBpIdList;
	private List<Long> wagonKeeperIdList;
	private List<Long> combinedOrderStatusFlagList;
	private String wagonNo;

	private Object[] qParams;
	private String queryString;
	
	/**
	 * Generate the SQL column query for the column 9 ('MAINTYPE').
	 * 
	 * The generated SQL Script:
	 * "
	 * nvl2(MAINT_G10_MAIN_TYPE_TXT, MAINT_G10_MAIN_TYPE_TXT || '/n', '') || 
	 * nvl2(MAINT_G20_MAIN_TYPE_TXT, MAINT_G20_MAIN_TYPE_TXT || '/n', '') || 
	 * nvl2(MAINT_G30_MAIN_TYPE_TXT, MAINT_G30_MAIN_TYPE_TXT || '/n', '') || 
 	 * ... (for all values from <code>columnNames</code>)
	 * nvl2(MAINT_C_MAIN_TYPE_TXT, MAINT_C_MAIN_TYPE_TXT || '/n', '') AS MAINTYPE
	 * "
	 * @param asColumnName
	 * @param columnNames
	 * 			
	 * @return
	 */
	private static String generateColumn9(String asColumnName, String... columnNames) {
		// [[
		String result = null;
		Object[][] params = {{"asColumnName", asColumnName}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(RailRepairExportMetaData.class, "generateColumn9", params);
		}
		// ]]
		try {
			StringBuilder builder = new StringBuilder();
			
			for (String columnName : columnNames) {
				builder.append("nvl2(");
				builder.append(columnName);
				builder.append(", ");			
				builder.append(columnName);			
				builder.append(" || ' '");			
				builder.append(", '')");			
				builder.append(" || ");
			}
			
			builder.delete(builder.length() -4, builder.length());
			builder.append(" AS ");
			builder.append(asColumnName);
			
			return result = builder.toString();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Generate the SQL column query for the column 11 ('OFFER').
	 * The value of the column will be a key from the {@link #columnValKeyMap}.
	 * 
	 * The generated SQL Script is:
	 * "
	 * CASE
     *	WHEN {@link #QUERY_COLUMN_11_3} <> null THEN {@link #COL_VAL_AKZEPTIERT_KEY}
     *	WHEN {@link #QUERY_COLUMN_11_2} = 1 THEN {@link #COL_VAL_ERHALTEN_KEY}
     *	WHEN {@link #QUERY_COLUMN_11_1} = 1 THEN {@link #COL_VAL_OFFEN_KEY} 
     *	ELSE {@link #COL_VAL_NEIN_KEY} 
     * END
     * AS {@link #QUERY_COLUMN_11}
     * "
	 * @return
	 */
	private static String generateColumn11() {
		// [[
		String result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(RailRepairExportMetaData.class, "generateColumn11");
		}
		// ]]
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("CASE ");
			builder.append("WHEN ");
			builder.append(QUERY_COLUMN_11_3);
			builder.append(" <> null");
			builder.append(" THEN '");
			builder.append(COL_VAL_AKZEPTIERT_KEY);
			builder.append("'");
			builder.append(" WHEN ");
			builder.append(QUERY_COLUMN_11_2);
			builder.append(" = 1");
			builder.append(" THEN '");
			builder.append(COL_VAL_ERHALTEN_KEY);
			builder.append("'");
			builder.append(" WHEN ");
			builder.append(QUERY_COLUMN_11_1);
			builder.append(" = 1");
			builder.append(" THEN '");
			builder.append(COL_VAL_OFFEN_KEY);
			builder.append("'");
			builder.append(" ELSE '");
			builder.append(COL_VAL_NEIN_KEY);
			builder.append("'");
			builder.append(" END");
			builder.append(" AS ");
			builder.append(QUERY_COLUMN_11);
			
			return result = builder.toString();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Generate the SQL column query for the column 12 ('DOCUMENT').
	 * The value of the column will be a key from the {@link #columnValKeyMap}.
	 * 
	 * The generated SQL Script is:
	 * "
	 * CASE
	 *	WHEN {@link #QUERY_COLUMN_12_2} = 1 THEN {@link #COL_VAL_ERHALTEN_KEY}
	 *	WHEN {@link #QUERY_COLUMN_12_1} = 1 THEN {@link #COL_VAL_OFFEN_KEY} 
	 *	ELSE {@link #COL_VAL_NEIN_KEY} 
	 * END
	 * AS {@link #QUERY_COLUMN_12}
	 * "
	 * @return
	 */
	private static String generateColumn12() {
		// [[
		String result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(RailRepairExportMetaData.class, "generateColumn12");
		}
		// ]]
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("CASE");
			builder.append(" WHEN ");
			builder.append(QUERY_COLUMN_12_2);
			builder.append(" = 1");
			builder.append(" THEN '");
			builder.append(COL_VAL_AKZEPTIERT_KEY);
			builder.append("'");
			builder.append(" WHEN ");
			builder.append(QUERY_COLUMN_12_1);
			builder.append(" = 1");
			builder.append(" THEN '");
			builder.append(COL_VAL_ERHALTEN_KEY);
			builder.append("'");
			builder.append(" ELSE '");
			builder.append(COL_VAL_OFFEN_KEY);
			builder.append("'");
			builder.append(" END");
			builder.append(" AS ");
			builder.append(QUERY_COLUMN_12);
			
			return result = builder.toString();
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}
	
	/**
	 * Generate the SQL column query for the column 13 ('INVOICE').
	 * The value of the column will be a key from the {@link #columnValKeyMap}.
	 * 
	 * The generated SQL Script is:
	 * "
	 * CASE
	 *	WHEN {@link #QUERY_COLUMN_13_2} = 1 THEN {@link #COL_VAL_ERHALTEN_KEY}
	 *	WHEN {@link #QUERY_COLUMN_13_1} = 1 THEN {@link #COL_VAL_OFFEN_KEY} 
	 *	ELSE {@link #COL_VAL_NEIN_KEY} 
	 * END
	 * AS {@link #QUERY_COLUMN_13}
	 * "
	 * @return
	 */
	private static String generateColumn13() {
		// [[
		String result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(RailRepairExportMetaData.class, "generateColumn13");
		}
		// ]]
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("CASE");
			builder.append(" WHEN ");
			builder.append(QUERY_COLUMN_13_2);
			builder.append(" = 1");
			builder.append(" THEN '");
			builder.append(COL_VAL_AKZEPTIERT_KEY);
			builder.append("'");
			builder.append(" WHEN ");
			builder.append(QUERY_COLUMN_13_1);
			builder.append(" = 1");
			builder.append(" THEN '");
			builder.append(COL_VAL_ERHALTEN_KEY);
			builder.append("'");
			builder.append(" ELSE '");
			builder.append(COL_VAL_OFFEN_KEY);
			builder.append("'");
			builder.append(" END");
			builder.append(" AS ");
			builder.append(QUERY_COLUMN_13);
			
			return result = builder.toString();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Default constructor
	 */
	public RailRepairExportMetaData() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "CarrierExportMetaData");
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
	
	private StringBuilder addEqOrIsNullToWhereClause(StringBuilder sb, String attrName, Object attrValue, Collection qParams) {
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
				sb.append("( ");
				sb.append(attrName + " = ?");
				sb.append(" OR ");
				sb.append(attrName);
				sb.append(" is null");
				sb.append(")");
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
			
			whereClauseSb = addEqOrIsNullToWhereClause(whereClauseSb, "LANG_CD", getLangCd(), qParams);
			whereClauseSb = addInToWhereClause(whereClauseSb, "shipto_sap_bp_id", getShiptoSapBpIdList(), qParams);
			whereClauseSb = addInToWhereClause(whereClauseSb, "wagon_keeper_id", getWagonKeeperIdList(), qParams);
			whereClauseSb = addEqToWhereClause(whereClauseSb, "wagon_no", getWagonNo(), qParams);
			whereClauseSb = addInToWhereClause(whereClauseSb, "combined_order_status_flag", getCombinedOrderStatusFlagList(), qParams);
									
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

	public List<Long> getShiptoSapBpIdList() {
		return shiptoSapBpIdList;
	}

	public void setShiptoSapBpIdList(List<Long> shiptoSapBpIdList) {
		this.shiptoSapBpIdList = shiptoSapBpIdList;
	}

	public List<Long> getWagonKeeperIdList() {
		return wagonKeeperIdList;
	}

	public void setWagonKeeperIdList(List<Long> wagonKeeperIdList) {
		this.wagonKeeperIdList = wagonKeeperIdList;
	}

	public List<Long> getCombinedOrderStatusFlagList() {
		return combinedOrderStatusFlagList;
	}

	public void setCombinedOrderStatusFlagList(List<Long> combinedOrderStatusFlagList) {
		this.combinedOrderStatusFlagList = combinedOrderStatusFlagList;
	}

	public String getWagonNo() {
		return wagonNo;
	}

	public void setWagonNo(String wagonNo) {
		this.wagonNo = wagonNo;
	}
}
