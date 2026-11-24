/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.directjpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import com.crh.logon.persistence.entities.admin.UserSapVendorAccEntity;
import com.crh.logon.persistence.entities.views.UserAdminEntity;
import com.crh.logon.persistence.entities.views.UserRoleSelectionEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
public class UserAdminJpaQueryData <E extends Enum<E>> extends JpaQueryDataBase<E> {

	/**
	 * Contains the column names from Database object 'VLGNU_USER_ADMIN'. This enum records are used
	 * to construct {@link EnumSet} with column names. The {@link EnumSet} is used to
	 * generate the SQL select query.
	 *  
	 * If more columns needed, add it here.
	 * 
	 * @author AdNovum Informatik AG
	 */
	public enum ColumnNames {
		USER_ID(UserAdminEntity.ATTR_USER_ID),
		SAP_BP_ID(UserAdminEntity.ATTR_SAP_BP_ID),		
		CUSTOMER(UserAdminEntity.ATTR_CUSTOMER),
		SAP_VENDOR_ID(UserAdminEntity.ATTR_SAP_VENDOR_ID),
		CARRIER(UserAdminEntity.ATTR_CARRIER),
		PERSON_ID(UserAdminEntity.ATTR_PERSON_ID),
		FIRSTNAME(UserAdminEntity.ATTR_FIRSTNAME),
		NAME(UserAdminEntity.ATTR_NAME),
		COMPOSITE_NAME(UserAdminEntity.ATTR_COMPOSITE_NAME),
		LDAP_HOLSPACE(UserAdminEntity.ATTR_LDAP_HOLSPACE),
		EXT_LOGIN(UserAdminEntity.ATTR_EXT_LOGIN),
		PDA_USER_NO(UserAdminEntity.ATTR_PDA_USER_NO),
		CERTIFIED_FROM_DAT(UserAdminEntity.ATTR_CERTIFIED_FROM_DAT),
		CERTIFIED_TO_DAT(UserAdminEntity.ATTR_CERTIFIED_TO_DAT),
		ACTIVE_FLAG(UserAdminEntity.ATTR_ACTIVE_FLAG),
		ROLE_NAMES(UserAdminEntity.ATTR_ROLE_NAMES);
		
		private final String columnName;
		
		private ColumnNames(String columnName) {
			this.columnName = columnName;
		}
		
		public String toString() {
			return columnName;
		}		
	}
	
	/**
	 * Used to generate the select query column names. 
	 * Contains the following column names: "USER_ID, SAP_BP_ID, CUSTOMER, 
	 * CARRIER, PERSON_ID, FIRSTNAME, NAME, COMPOSITE_NAME, LDAP_HOLSPACE,
	 * EXT_LOGIN, PDA_USER_NO, ACTIVE_FLAG, ROLE_NAMES, CERTIFIED_FROM_DATE,
	 * CERTIFIED_TO_DATE"
	 */
	public static final EnumSet<ColumnNames> COLUMNS_SET_1 = EnumSet.of(ColumnNames.USER_ID,
			ColumnNames.SAP_BP_ID, ColumnNames.CUSTOMER, ColumnNames.SAP_VENDOR_ID, 
			ColumnNames.CARRIER, ColumnNames.PERSON_ID, ColumnNames.FIRSTNAME, ColumnNames.NAME,
			ColumnNames.COMPOSITE_NAME, ColumnNames.LDAP_HOLSPACE, ColumnNames.EXT_LOGIN, ColumnNames.PDA_USER_NO,
			ColumnNames.ACTIVE_FLAG, ColumnNames.ROLE_NAMES, ColumnNames.CERTIFIED_FROM_DAT, ColumnNames.CERTIFIED_TO_DAT);
	
    protected static final String IN_UserRoleSelection =
		" IN (select urs."  + UserRoleSelectionEntity.ATTR_USER_ID +
              " from "  + UserRoleSelectionEntity.class.getName()    + " urs " +
              " where urs." + UserRoleSelectionEntity.ATTR_USER_ROLE_ID + " is not null and urs." + UserRoleSelectionEntity.ATTR_ROLE_ID;
	
	private String compositeName;
	private String ldapHolspace;
	private String extLogin;
	private String holspaceOrExtLoginOrPdaUserNo;
	private Long pdaUserNo;
	private String costumer;
	private String carrier;
	private String customerOrCarrier;
	private Boolean activeFlag;
	private Long roleId;
	private Long userId;
	private Date certifiedFromDat;
	private Date certifiedToDat;
	private String orderBy;
	private boolean asc = false;
	
	/**
	 * 
	 * @param daoProvider
	 * @param uid
	 */
	public UserAdminJpaQueryData(DaoProvider daoProvider, Long uid) {
		super(daoProvider, uid);
	}
	
	
	protected Collection<String> getFromCollection() {
		List<String> result = new ArrayList<String>();
		result.add(UserAdminEntity.class.getName());
		return result;
	}

	protected Collection<String> getWhereCollection() {
		List<String> result = new ArrayList<String>();
		StringBuilder sqlSelectQuery = new StringBuilder();
		
		int paramNum = 1;
		if (!StringUtils.empty(getCompositeName())) { // ignore case like
			sqlSelectQuery.append("UPPER(" + UserAdminEntity.ATTR_COMPOSITE_NAME + ")");
			sqlSelectQuery.append(" LIKE ?" + paramNum++);
			addQueryParam("%" + getCompositeName().toUpperCase() + "%");
			result.add(sqlSelectQuery.toString());
			paramNum = getNextQueryParamIndex();
		}
		
		if (!StringUtils.empty(getHolspaceOrExtLoginOrPdaUserNo())) {
			sqlSelectQuery = new StringBuilder();
			sqlSelectQuery.append("(");
			sqlSelectQuery.append(StringUtils.join(getHolspaceOrExtLoginOrPdaUserNoCollection(), " or "));
			sqlSelectQuery.append(")");
			
			result.add(sqlSelectQuery.toString());
			paramNum = getNextQueryParamIndex();
		}
		
		if (!StringUtils.empty(getCustomerOrCarrier())) {
			sqlSelectQuery = new StringBuilder();
			sqlSelectQuery.append("(");
			sqlSelectQuery.append(StringUtils.join(getCustomerOrCarrierCollection(), " or "));
			sqlSelectQuery.append(")");
			
			result.add(sqlSelectQuery.toString());
			paramNum = getNextQueryParamIndex();
		}
		
		if (getActiveFlag() != null) {
			sqlSelectQuery = new StringBuilder();
			sqlSelectQuery.append(UserAdminEntity.ATTR_ACTIVE_FLAG);
			sqlSelectQuery.append(" = ?" + paramNum++);
			addQueryParam(getActiveFlag());
			result.add(sqlSelectQuery.toString());
		}
		
		if (getRoleId() != null) {
			sqlSelectQuery = new StringBuilder();
			sqlSelectQuery.append(UserAdminEntity.ATTR_USER_ID);
			sqlSelectQuery.append(IN_UserRoleSelection);
			sqlSelectQuery.append(" = ?" + paramNum++);
			sqlSelectQuery.append(")");
			addQueryParam(getRoleId());
			result.add(sqlSelectQuery.toString());
		}
		
		if (getUserId() != null) {
			sqlSelectQuery = new StringBuilder();
			sqlSelectQuery.append(UserAdminEntity.ATTR_USER_ID);
			sqlSelectQuery.append(" != ?" + paramNum++); // NOT equals case
			addQueryParam(getUserId());
			result.add(sqlSelectQuery.toString());
		}
		
		if (result.isEmpty()) { // add true restriction "1=1"
			result.add(EMPTY_WHERE_COND);
		}
		
		return result;
	}
	
	protected String getOrderBy() {
		StringBuilder sqlSelectQuery = new StringBuilder();
		sqlSelectQuery.append(" order by ");
		if(StringUtils.empty(orderBy)) {
			sqlSelectQuery.append(UserAdminEntity.ATTR_NAME);
			sqlSelectQuery.append(",");
			sqlSelectQuery.append(UserAdminEntity.ATTR_FIRSTNAME);
		} else {
			sqlSelectQuery.append(orderBy);
		}
		if(asc) {
			sqlSelectQuery.append(" asc");
		} else {
			sqlSelectQuery.append(" desc");
		}
		
		return sqlSelectQuery.toString();
	}

	protected Collection<String> getRestrictionCollection() {
		Collection<String> restList = new ArrayList<String>();
		if (getUid() == null) { // if the user id not defined --> do not generate restriction
			restList.add(EMPTY_WHERE_COND);// add true restriction "1=1"
			return restList;			
		}
		
		int paramNum = getNextQueryParamIndex();
		restList.add(getAccessRestriction(UserSapVendorAccEntity.class, UserSapVendorAccEntity.ATTR_SAP_VENDOR_ID, paramNum));
		
		return restList;
	}
	
	private Collection<String> getHolspaceOrExtLoginOrPdaUserNoCollection() {
		List<String> result = new ArrayList<String>();
		StringBuilder sqlSelectQuery = new StringBuilder();
		
		int paramNum = getNextQueryParamIndex();
		sqlSelectQuery = new StringBuilder();
		sqlSelectQuery.append("UPPER(" + UserAdminEntity.ATTR_LDAP_HOLSPACE + ")");
		sqlSelectQuery.append(" LIKE ?" + paramNum);
		result.add(sqlSelectQuery.toString());
		
		sqlSelectQuery = new StringBuilder();
		sqlSelectQuery.append("UPPER(" + UserAdminEntity.ATTR_EXT_LOGIN + ")");
		sqlSelectQuery.append(" LIKE ?" + paramNum++);
		addQueryParam("%" + getHolspaceOrExtLoginOrPdaUserNo().toUpperCase() + "%");
		result.add(sqlSelectQuery.toString());
		
		Long pdaUserNo = null;
		try {
			pdaUserNo = Long.valueOf(getHolspaceOrExtLoginOrPdaUserNo());
			sqlSelectQuery = new StringBuilder();
			sqlSelectQuery.append(UserAdminEntity.ATTR_PDA_USER_NO);
			sqlSelectQuery.append(" = ?" + paramNum++);
			addQueryParam(pdaUserNo);
			result.add(sqlSelectQuery.toString());
		}
		catch (NumberFormatException e) {
			// do nothing here
			; // Avoid pmd
		}
		
		return result;
	}	
	
	private Collection<String> getCustomerOrCarrierCollection() {
		List<String> result = new ArrayList<String>();
		StringBuilder sqlSelectQuery = new StringBuilder();
		
		int paramNum = getNextQueryParamIndex();
		sqlSelectQuery = new StringBuilder();
		sqlSelectQuery.append("UPPER(" + UserAdminEntity.ATTR_CUSTOMER + ")");
		sqlSelectQuery.append(" LIKE ?" + paramNum);
		result.add(sqlSelectQuery.toString());
		
		sqlSelectQuery = new StringBuilder();
		sqlSelectQuery.append("UPPER(" + UserAdminEntity.ATTR_CARRIER + ")");
		sqlSelectQuery.append(" LIKE ?" + paramNum++);
		addQueryParam("%" + getCustomerOrCarrier().toUpperCase() + "%");
		result.add(sqlSelectQuery.toString());
		
		return result;
	}
	
	public String getCompositeName() {
		return compositeName;
	}
	
	public void setCompositeName(String compositeName) {
		this.compositeName = compositeName;
	}
	
	public String getLdapHolspace() {
		return ldapHolspace;
	}
	
	public void setLdapHolspace(String ldapHolspace) {
		this.ldapHolspace = ldapHolspace;
	}
	
	public String getExtLogin() {
		return extLogin;
	}
	
	public void setExtLogin(String extLogin) {
		this.extLogin = extLogin;
	}
	
	public Long getPdaUserNo() {
		return pdaUserNo;
	}
	
	public void setPdaUserNo(Long pdaUserNo) {
		this.pdaUserNo = pdaUserNo;
	}
	
	public String getCostumer() {
		return costumer;
	}
	
	public void setCostumer(String costumer) {
		this.costumer = costumer;
	}
	
	public String getCarrier() {
		return carrier;
	}
	
	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}
	
	public Boolean getActiveFlag() {
		return activeFlag;
	}
	
	public void setActiveFlag(Boolean activeFlag) {
		this.activeFlag = activeFlag;
	}
	
	public Long getRoleId() {
		return roleId;
	}
	
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getHolspaceOrExtLoginOrPdaUserNo() {
		return holspaceOrExtLoginOrPdaUserNo;
	}

	public void setHolspaceOrExtLoginOrPdaUserNo(String holspaceOrExtLoginOrPdaUserNo) {
		this.holspaceOrExtLoginOrPdaUserNo = holspaceOrExtLoginOrPdaUserNo;
	}

	public String getCustomerOrCarrier() {
		return customerOrCarrier;
	}

	public void setCustomerOrCarrier(String customerOrCarrier) {
		this.customerOrCarrier = customerOrCarrier;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getCertifiedFromDat() {
		return certifiedFromDat;
	}

	public void setCertifiedFromDat(Date certifiedFromDate) {
		this.certifiedFromDat = certifiedFromDate;
	}

	public Date getCertifiedToDat() {
		return certifiedToDat;
	}

	public void setCertifiedToDat(Date certifiedToDat) {
		this.certifiedToDat = certifiedToDat;
	}

	public boolean isAsc() {
		return asc;
	}

	public void setAsc(boolean asc) {
		this.asc = asc;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	
}
