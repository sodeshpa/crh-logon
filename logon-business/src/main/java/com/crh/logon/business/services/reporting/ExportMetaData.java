/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.reporting;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.crh.logon.business.services.interfaces.SqlService;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.admin.UserRegionAccEntity;
import com.crh.logon.persistence.entities.common.RegionPoolEntity;
import com.crh.logon.persistence.entities.common.PoolEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
public abstract class ExportMetaData implements Serializable {

	private static final long serialVersionUID = 1L;

	protected static final String USER_POOL_ACC = Constant.SCHEMA_USER_PREPEND
			+ "TLGNU_USER_POOL_ACC";

	protected static final String USER_PLANT_ACC = Constant.SCHEMA_USER_PREPEND
			+ "TLGNU_USER_PLANT_ACC";

	protected static final String USER_SAP_VENDOR_ACC = Constant.SCHEMA_USER_PREPEND
			+ "TLGNU_USER_SAP_VENDOR_ACC";

	protected static final String USER_SAREA_ACC = Constant.SCHEMA_USER_PREPEND
			+ "TLGNU_USER_SAREA_ACC";

	protected static final String USER_COMPANYCODE_ACC = Constant.SCHEMA_USER_PREPEND
			+ "TLGNU_USER_COMPANYCODE_ACC";

	protected static final String USER_SGROUP_ACC = Constant.SCHEMA_USER_PREPEND
			+ "TLGNU_USER_SGROUP_ACC";

	protected static final String USER_SHIPPINGPOINT_ACC = Constant.SCHEMA_USER_PREPEND
			+ "TLGNU_USER_SHIPPINGPOINT_ACC";

	protected static final String USER_SHIPTO_ACC = Constant.SCHEMA_USER_PREPEND
			+ "TLGNU_USER_SHIPTO_ACC";

	protected static final String USER_SOLDTO_BP_ACC = Constant.SCHEMA_USER_PREPEND
			+ "TLGNU_USER_SOLDTO_BP_ACC";
	
	protected static final String USER_REGION_ACC = Constant.SCHEMA_USER_PREPEND
			+ "TLGNU_USER_REGION_ACC";
	
	private static final String POOL_TBL_POOL_ID = "POOL_ID";
	private static final String REGION_BS_TBL_REGION_CD = "REGION_CD";
	private static final String USER_ACC_USER_ID = "USER_ID";
	
	private static final String IN_USER_POOL_ACC =
		" IN (select POOL_ID" +
		" from " + USER_POOL_ACC +
		" where USER_ID = ?)";
	
    private static final String IN_USER_PLANT_ACC =
        " IN (select PLANT_CD" +
        " from "  + USER_PLANT_ACC +
        " where USER_ID = ?)";
    
	private static final String IN_USER_SAP_VENDOR_ACC =
        " IN (select SAP_VENDOR_ID" +
        " from "  + USER_SAP_VENDOR_ACC +
        " where USER_ID = ?)";
	
	private static final String IN_USER_SAREA_ACC =
        " IN (select SAREA_ID" +
        " from "  + USER_SAREA_ACC +
        " where USER_ID = ?)";
	
	private static final String IN_USER_COMPANYCODE_ACC =
        " IN (select COMPANYCODE_CD" +
        " from "  + USER_COMPANYCODE_ACC +
        " where USER_ID = ?)";
	
	private static final String IN_USER_SGROUP_ACC =
        " IN (select SGROUP_CD" +
        " from "  + USER_SGROUP_ACC +
        " where USER_ID = ?)";
	
	private static final String IN_USER_SHIPPINGPOINT_ACC =
        " IN (select SHIPPINGPOINT_ID" +
        " from "  + USER_SHIPPINGPOINT_ACC +
        " where USER_ID = ?)";

	private static final String IN_USER_SHIPTO_ACC =
        " IN (select SHIPTO_SAP_BP_ID" +
        " from "  + USER_SHIPTO_ACC +
        " where USER_ID = ?)";

	private static final String IN_USER_SOLDTO_BP_ACC =
        " IN (select SOLDTO_SAP_BP_ID" +
        " from "  + USER_SOLDTO_BP_ACC +
        " where USER_ID = ?)";
	
	public static final String IN_POOL_OF_REGION_RESTR = 
            " IN (select pool." + POOL_TBL_POOL_ID +
            " from "  + UserRegionAccEntity.TABLE_NAME + " ucc_acc, " + 
            RegionPoolEntity.TABLE_NAME + " cp, " + 
            PoolEntity.TABLE_NAME + " pool " +
            " where ucc_acc." + REGION_BS_TBL_REGION_CD + " = cp." + REGION_BS_TBL_REGION_CD + " and " +
            "cp." + POOL_TBL_POOL_ID + " = pool." + POOL_TBL_POOL_ID + " and " +
            "ucc_acc." + USER_ACC_USER_ID + " = ? ) ";

	private static final Map<String, String> ACC_TABLE_TO_IN_CLAUSE;
	private static final Boolean VALIDATE_NULL = true;
	private static final Boolean DO_NOT_VALIDATE_NULL = false;
	static {
		ACC_TABLE_TO_IN_CLAUSE = new HashMap<String, String>();
		ACC_TABLE_TO_IN_CLAUSE.put(USER_POOL_ACC, IN_USER_POOL_ACC);
		ACC_TABLE_TO_IN_CLAUSE.put(USER_PLANT_ACC, IN_USER_PLANT_ACC);
		ACC_TABLE_TO_IN_CLAUSE.put(USER_SAP_VENDOR_ACC, IN_USER_SAP_VENDOR_ACC);
		ACC_TABLE_TO_IN_CLAUSE.put(USER_SAREA_ACC, IN_USER_SAREA_ACC);
		ACC_TABLE_TO_IN_CLAUSE.put(USER_COMPANYCODE_ACC, IN_USER_COMPANYCODE_ACC);
		ACC_TABLE_TO_IN_CLAUSE.put(USER_SGROUP_ACC, IN_USER_SGROUP_ACC);
		ACC_TABLE_TO_IN_CLAUSE.put(USER_SHIPPINGPOINT_ACC, IN_USER_SHIPPINGPOINT_ACC);
		ACC_TABLE_TO_IN_CLAUSE.put(USER_SHIPTO_ACC, IN_USER_SHIPTO_ACC);
		ACC_TABLE_TO_IN_CLAUSE.put(USER_SOLDTO_BP_ACC, IN_USER_SOLDTO_BP_ACC);
		ACC_TABLE_TO_IN_CLAUSE.put(USER_REGION_ACC, IN_POOL_OF_REGION_RESTR);
	}
	
	public abstract void init(ServiceLocator serviceLocator, long uid);
	
	public abstract String getQueryString();
	
	public abstract Object[] getQParams();
	
	protected String getAccessRestriction(String accTable, String field, ServiceLocator serviceLocator, long uid, Boolean validateFieldForNull) {
		int count = -1;
		SqlService sqlService = serviceLocator.fetchSqlService();
		long qSession = sqlService.openQuerySession("select count(*) from " + accTable + " x where user_id = ?", new Object[] { uid });
		List rows = sqlService.fetchRows(qSession, 1);
		if ( (rows != null) && (!rows.isEmpty()) ) {
			Object[] row = (Object[])rows.get(0);
			count = ((BigDecimal)row[0]).intValue();
		}
		sqlService.closeQuerySession(qSession);
		sqlService.remove();
		
		if (count > 0) {
			StringBuilder returnValue = new StringBuilder();
			if (validateFieldForNull) {
				returnValue.append(field + " is null or ");
			}
			returnValue.append(field + ACC_TABLE_TO_IN_CLAUSE.get(accTable));
			return returnValue.toString();
		}
		else if (count < 0) {
			throw new LogonException("Could not determine access restriction", Reason.RES_ERROR_IMPL);
		}
		
		return null;
	}
	
	protected void addAccessRestriction(String accTable, String field, List qParamList, List qFormatParams, ServiceLocator serviceLocator, long uid) {
		String accRestriction;
		if (USER_SAP_VENDOR_ACC.equals(accTable)) {
			accRestriction = getAccessRestriction(accTable, field, serviceLocator, uid, DO_NOT_VALIDATE_NULL);			
		}
		else {
			accRestriction = getAccessRestriction(accTable, field, serviceLocator, uid, VALIDATE_NULL);			
		}
		if (accRestriction != null) {
			qParamList.add(new BigDecimal("" + uid));
		}
		else {
			accRestriction = "1=1";
		}
		qFormatParams.add(accRestriction);
	}
	
	protected SimpleDateFormat getSimpleDateFormatWithGMTZone() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf;
	}
}
