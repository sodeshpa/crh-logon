/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.directjpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.nevis.gdi.jpa.BaseEntity;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.admin.UserCompanycodeAccEntity;
import com.crh.logon.persistence.entities.admin.UserPlantAccEntity;
import com.crh.logon.persistence.entities.admin.UserPoolAccEntity;
import com.crh.logon.persistence.entities.admin.UserPurchorgAccEntity;
import com.crh.logon.persistence.entities.admin.UserSapVendorAccEntity;
import com.crh.logon.persistence.entities.admin.UserSareaAccEntity;
import com.crh.logon.persistence.entities.admin.UserSgroupAccEntity;
import com.crh.logon.persistence.entities.admin.UserShippingpointAccEntity;
import com.crh.logon.persistence.entities.admin.UserShiptoAccEntity;
import com.crh.logon.persistence.entities.admin.UserSoldtoBpAccEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.refdata.SorgBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpSareaEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
public abstract class JpaQueryDataBase<E extends Enum<E>> implements JpaQueryData<E> {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(JpaQueryDataBase.class);

	private final static Map<Class<? extends BaseEntity>, Restriction<?>> RESTRICTION_MAP;
	static {
		RESTRICTION_MAP = new HashMap<Class<? extends BaseEntity>, Restriction<?>>();
		RESTRICTION_MAP.put(UserSareaAccEntity.class,
				new Restriction<UserSareaAccEntity>(
				UserSareaAccEntity.class, UserSareaAccEntity.ATTR_SAREA_ID, UserSareaAccEntity.ATTR_USER_ID));
		
		RESTRICTION_MAP.put(UserPlantAccEntity.class,
				new Restriction<UserPlantAccEntity>(
				UserPlantAccEntity.class, UserPlantAccEntity.ATTR_PLANT_CD, UserPlantAccEntity.ATTR_USER_ID));
				
		RESTRICTION_MAP.put(UserCompanycodeAccEntity.class,
				new Restriction<UserCompanycodeAccEntity>(
				UserCompanycodeAccEntity.class, UserCompanycodeAccEntity.ATTR_COMPANYCODE_CD, UserCompanycodeAccEntity.ATTR_USER_ID));		
		
		RESTRICTION_MAP.put(UserPoolAccEntity.class,
				new Restriction<UserPoolAccEntity>(
						UserPoolAccEntity.class, UserPoolAccEntity.ATTR_POOL_ID, UserPoolAccEntity.ATTR_USER_ID));		
		
		RESTRICTION_MAP.put(UserShippingpointAccEntity.class,
				new Restriction<UserShippingpointAccEntity>(
						UserShippingpointAccEntity.class, UserShippingpointAccEntity.ATTR_SHIPPINGPOINT_ID, UserShippingpointAccEntity.ATTR_USER_ID));		
		
		RESTRICTION_MAP.put(UserSapVendorAccEntity.class,
				new Restriction<UserSapVendorAccEntity>(
						UserSapVendorAccEntity.class, UserSapVendorAccEntity.ATTR_SAP_VENDOR_ID, UserSapVendorAccEntity.ATTR_USER_ID));		
		
		RESTRICTION_MAP.put(UserShiptoAccEntity.class,
				new Restriction<UserShiptoAccEntity>(
						UserShiptoAccEntity.class, UserShiptoAccEntity.ATTR_SHIPTO_SAP_BP_ID, UserShiptoAccEntity.ATTR_USER_ID));		
		
		RESTRICTION_MAP.put(UserSoldtoBpAccEntity.class,
				new Restriction<UserSoldtoBpAccEntity>(
						UserSoldtoBpAccEntity.class, UserSoldtoBpAccEntity.ATTR_SOLDTO_SAP_BP_ID, UserSoldtoBpAccEntity.ATTR_USER_ID));		
		
		RESTRICTION_MAP.put(UserPurchorgAccEntity.class,
				new Restriction<UserPurchorgAccEntity>(
						UserPurchorgAccEntity.class, UserPurchorgAccEntity.ATTR_PURCHASINGORG_CD, UserPurchorgAccEntity.ATTR_USER_ID));		
		
		RESTRICTION_MAP.put(UserSgroupAccEntity.class,
				new Restriction<UserSgroupAccEntity>(
						UserSgroupAccEntity.class, UserSgroupAccEntity.ATTR_SGROUP_CD, UserSgroupAccEntity.ATTR_USER_ID));		

	}
	
    protected static final String IN_SapBpOfCompanycodeRestr = 
        	" IN (select " + "sap_bp.sapBp.sapBpId" +
            " from "  + UserCompanycodeAccEntity.class.getName() + " ucc_acc, " + 
        				SorgBsEntity.class.getName() + " sorg, " + 
        				SareaEntity.class.getName() + " sarea, " + 
        				SapBpSareaEntity.class.getName() + " sap_bp " +
        	" where ucc_acc." + UserCompanycodeAccEntity.ATTR_COMPANYCODE_CD + " = sorg." + SorgBsEntity.ATTR_COMPANYCODE_CD + " and " +
        	"sorg." + SorgBsEntity.ATTR_SORG_CD + " = sarea." + SareaEntity.ATTR_SORG_CD + " and " +
        	"sarea." + SareaEntity.ATTR_SAREA_ID + " = sap_bp." + SapBpSareaEntity.ATTR_SAREA_ID + " and " +
        	"ucc_acc." + UserCompanycodeAccEntity.ATTR_USER_ID;
    
    protected static final String IN_SapBpOfSareaRestr =
		" IN (select "  + "sap_bp.sapBp.sapBpId" +
              " from "  + UserSareaAccEntity.class.getName()    + " saa, "       +
                          SapBpSareaEntity.class.getName()      + " sap_bp "  +
              " where   saa.sarea.sareaId = sap_bp.sarea.sareaId "            +
   		          " and saa.user.userId";
    
    
	protected static final String EMPTY_WHERE_COND = "1=1";
	
	private DaoProvider daoProvider;
	private Long uid;
	
	private List<Object> queryParams;
	private List<String> queryColumns; 
	private Set<Class<? extends BaseEntity>> restrEntitySet; 
	
	private EnumSet<E> columnsSet;
	
	/**
	 * Constructor ...
	 * 
	 * @param daoProvider
	 * @param uid
	 */
	protected JpaQueryDataBase(DaoProvider daoProvider, Long uid) {
		// [[
		Object[][] params = {{"daoProvider", daoProvider},
							{"uid", uid}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "JpaQueryDataBase", params);
		}
		// ]]
		try {
			this.daoProvider = daoProvider;
			this.uid = uid;
			
			this.queryParams = new ArrayList<Object>();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	/**
	 * Load the JPA SQL query.
	 */
	public String getJpaQuery() {
		// [[
		String result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getJpaQuery");
		}
		// ]]
		try {
			initQueryColumnNames();

			StringBuilder sqlSelectQuery = new StringBuilder("select distinct ");
			sqlSelectQuery.append(StringUtils.join(queryColumns, ", "));
			sqlSelectQuery.append(" from ");
			sqlSelectQuery.append(StringUtils.join(getFromCollection(), ", "));
			sqlSelectQuery.append(" where ");
			sqlSelectQuery.append(EMPTY_WHERE_COND);
			sqlSelectQuery.append(" and ");
			sqlSelectQuery.append(StringUtils.join(getWhereCollection(), " and "));
			sqlSelectQuery.append(" and ");
			sqlSelectQuery.append(StringUtils.join(getRestrictionCollection(), " and "));
			sqlSelectQuery.append(getOrderBy());
			
			if (logDebugEnabled) {
				LOG.debug("The generated jpa SQL Query: '" + sqlSelectQuery.toString() + "'");
				LOG.debug("The generated jpa SQL Query parameters: '" + queryParamsToString() + "'");
			}
			
			return result = sqlSelectQuery.toString();
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
	 * Get the Query parameters.
	 */
	public Object[] getQueryParams() {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("getQueryParams(): '" + queryParams + "'");
		}
		// ]]
		return queryParams.toArray();
	}
	
	/**
	 * Get the index of the column in the object array.
	 * 
	 * If the column name was not specified for the select SQL query, <tt>-1</tt> is returned. 
	 * 
	 * @param columnName
	 * 			The name of the column whose index to get.
	 * @return
	 */
	public int getIndexOf(String columnName) {
		// [[
		int result = 0;
		Object[][] params = {{"columnName", columnName}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getIndexOf", params);
		}
		// ]]
		try {
			if (queryColumns == null || queryColumns.isEmpty()) {
				return result = -1;
			}
			
			return result = queryColumns.indexOf(columnName);
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
	 * Return <code>true</code> if Jpa Query contains restriction for the given <code>restrictionEntity</code>,
	 * <code>false</code> otherwise.
	 * 
	 * This function can be called after getJpaQuery(), because the restriction are generated there.
	 * 
	 * @param
	 * 		restrictionEntity
	 * 				The restriction object to be checked.	
	 * @return
	 * 		Return <code>true</code> when the user has restriction, <code>false</code> otherwise
	 */
	public boolean hasUserRestriction(Class<? extends BaseEntity> restrictionEntity) {
		// [[
		boolean result = false;
		Object[][] params = {{"restrictionEntity", restrictionEntity}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "hasUserRestriction", params);
		}
		// ]]
		try {
			if (restrEntitySet == null) {
				return result = false;
			}
			return result = restrEntitySet.contains(restrictionEntity);
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
	 * After the JPA query some restriction are generated, ONLY if the user has any.
	 * 
	 * This function can be called after getJpaQuery(), because the restriction are generated there.
	 * 
	 * @return
	 * 		Return <code>true</code> when the user has restriction, <code>false</code> otherwise
	 */
	public boolean hasUserRestriction() {
		// [[
		boolean result = false;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "hasUserRestriction");
		}
		// ]]
		try {
			if (restrEntitySet == null) {
				return result = false;
			}
			return result = restrEntitySet.isEmpty();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	protected abstract Collection<String> getFromCollection();

	protected abstract Collection<String> getWhereCollection();
	
	protected abstract Collection<String> getRestrictionCollection();
	
	protected abstract String getOrderBy();
		
	/**
	 * 
	 * @param restrictionEntity
	 * @param fieldName
	 * @param paramNum
	 * @return
	 */
	protected final <T extends BaseEntity> String getAccessRestriction(Class<T> restrictionEntity, String fieldName, int paramNum) {
		
		@SuppressWarnings("unchecked")
		final Restriction<T> restriction = (Restriction<T>) RESTRICTION_MAP.get(restrictionEntity);
		if (restriction == null) {
			throw new IllegalArgumentException("No access restriction defined for class " + restrictionEntity.getName());
		}
		
		final Number count = getCount(restriction);
		
		if (count.intValue() > 0) {
			if (restrEntitySet == null) {
				restrEntitySet = new HashSet<Class<? extends BaseEntity>>();
			}
			if (restrEntitySet.isEmpty()) {
				addQueryParam(uid); // add the userId to query param list
			}
			restrEntitySet.add(restrictionEntity);
			return restriction.getRestrictionQueryPart(fieldName, paramNum);
		}
		
		return EMPTY_WHERE_COND;
	}
	
	/**
	 * 
	 * @param restrictionEntity
	 * @param fieldName
	 * @param specialInRestr
	 * @param paramNum
	 * @return
	 */
	protected final <T extends BaseEntity> String getSpecialAccessRestriction(Class<T> restrictionEntity, String fieldName, String specialInRestr, int paramNum) {
		
		@SuppressWarnings("unchecked")
		final Restriction<T> restriction = (Restriction<T>) RESTRICTION_MAP.get(restrictionEntity);
		if (restriction == null) {
			throw new IllegalArgumentException("No access restriction defined for class " + restrictionEntity.getName());
		}

		final Number count = getCount(restriction);
		
		if (count.intValue() > 0) {	
			if (restrEntitySet == null) {
				restrEntitySet = new HashSet<Class<? extends BaseEntity>>();
			}
			if (restrEntitySet.isEmpty()) {
				addQueryParam(uid); // add the userId to query param list
			}
			restrEntitySet.add(restrictionEntity);
			return restriction.getSpecialRestrictionQueryPart(fieldName, specialInRestr, paramNum);
		}
		
		return EMPTY_WHERE_COND;
	}
	
	private final <T extends BaseEntity> Number getCount(final Restriction<T> restriction) {
		final String query = restriction.getCountQuery();
		final Object[] params = new Object[] { this.uid };
		final Number count = (Number) daoProvider.getDirectJpaDao().findSingle(query, params);
		
		if (count == null) {
			throw new LogonException("Could not determine access restriction", Reason.RES_ERROR_IMPL);
		}
		
		return count;
	}

	/**
	 * Add a new record into <code>queryParams</code> List.
	 * 
	 * @param queryParamValue
	 * 				The query parameter value
	 */
	protected void addQueryParam(Object queryParamValue) {
		this.queryParams.add(queryParamValue);
	}
	
	/**
	 * Retrieve the index of the next Query parameter.
	 * 
	 * @return
	 */
	protected int getNextQueryParamIndex() {
		if (queryParams == null) {
			return -1;
		}
		
		return queryParams.size() + 1;
	}
	
	/**
	 * Add all records into <code>queryParams</code> List.
	 * 
	 * @param queryParams
	 * 				The query parameter value
	 */
	protected void addAllQueryParam(List queryParams) {
		if (queryParams == null || queryParams.isEmpty()) {
			return; // do nothing
		}
		this.queryParams.addAll(queryParams);
	}	
	
	protected String queryParamsToString() {
		if (queryParams == null) {
			return null;
		}
		
		StringBuilder builder = new StringBuilder("'");
		for (Iterator<Object> iter = queryParams.iterator(); iter.hasNext();) {
			builder.append("'");
			builder.append(String.valueOf(iter.next()));
			builder.append("'");
			if (iter.hasNext()) {
				builder.append(", ");
			}
		}
		builder.append("}");
		
		return builder.toString();
	}
	
	public  void addSelectColumnNames(EnumSet<E> arg) {
		columnsSet = arg;		
	}
	
	
	private void initQueryColumnNames() {
		
		queryColumns = new ArrayList<String>(); 
		if (columnsSet == null) {
			queryColumns.add("x"); // means "select all"
			return;
		}
		
		for (final Enum<E> column: columnsSet) {
			queryColumns.add(column.toString());
		}
	}
	
	/**
	 * 
	 * @param params
	 * @param paramNum
	 * @return
	 */
	protected String generateInClause(List<? extends Object> params, int paramNum) {
		if (params == null || params.isEmpty()) {
			throw new IllegalArgumentException("The parameter 'params' is null or empty");
		}
		
		StringBuilder builder = new StringBuilder(" in (");
		for (Iterator iter = params.iterator(); iter.hasNext();) {
			iter.next();
			builder.append("?");
			builder.append(paramNum ++);
			
			if (iter.hasNext()) {
				builder.append(", ");
			}
		}
		builder.append(")");
		
		return builder.toString();
	}
	
	/**
	 * 
	 * @return
	 */
	public Long getUid() {
		return uid;
	}

	public DaoProvider getDaoProvider() {
		return daoProvider;
	}

	public void setDaoProvider(DaoProvider daoProvider) {
		this.daoProvider = daoProvider;
	}

	private static final class Restriction<T extends BaseEntity> {

		private final Class<T> restrictionEntity;
		private final String restrictorFieldName;
		private final String userFieldName;
		
		protected Restriction(Class<T> restrictionEntity, String restrictorFieldJavaName, String userFieldJavaName) {
			this.restrictionEntity = restrictionEntity;
			this.restrictorFieldName = restrictorFieldJavaName;
			this.userFieldName = userFieldJavaName;
		}

		private final String getJPATableName() {
			return restrictionEntity.getName();
		}
				
		public final String getCountQuery() {
			return  getCountQuery(1);
		}

		public final String getCountQuery(int paramNum) {
			return "select count(*) from "
					+ getJPATableName() + " x where " + 
					userFieldName + " = ?" + paramNum + ")";
		}

		public final String getRestrictionQueryPart(String fieldName) {
			return  getRestrictionQueryPart(fieldName, 1);
		}
		
		public final String getRestrictionQueryPart(String fieldName, int paramNum) {
			return  "(" + fieldName + " IS NULL OR " + fieldName + " IN (select " + restrictorFieldName + " from "
					+ getJPATableName() + " where " + userFieldName + " = ?"+ paramNum + "))";
		}
		
		public final String getSpecialRestrictionQueryPart(String fieldName, String inRestr, int paramNum) {
			return  "(" + fieldName + " IS NULL OR " + fieldName + inRestr + " = ?"+ paramNum + "))";
		}
	}
	
}
