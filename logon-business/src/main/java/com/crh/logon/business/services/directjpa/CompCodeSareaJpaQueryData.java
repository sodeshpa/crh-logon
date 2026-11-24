/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.directjpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import com.crh.logon.persistence.entities.admin.UserCompanycodeAccEntity;
import com.crh.logon.persistence.entities.admin.UserSareaAccEntity;
import com.crh.logon.persistence.entities.views.CompCodeSareaEntity;
import com.crh.logon.persistence.utils.DaoProvider;

/**
 * @author AdNovum Informatik AG
 */
public class CompCodeSareaJpaQueryData<E extends Enum<E>> extends JpaQueryDataBase<E> {

	/**
	 * Contains the column names from Database object 'VLGNU_COMP_CODE_SAREA'. This enum records are used
	 * to construct {@link EnumSet} with column names. The {@link EnumSet} is used to
	 * generate the SQL select query.
	 *  
	 * If more columns needed, add it here.
	 * 
	 * @author AdNovum Informatik AG
	 */
	public enum ColumnNames {
		COMPOSITE_ID(CompCodeSareaEntity.ATTR_COMPOSITE_ID),
		SAREA_DISPLAY_LONGNAME(CompCodeSareaEntity.ATTR_SAREA_DISPLAY_LONGNAME),
		COMPANYCODE_CD(CompCodeSareaEntity.ATTR_COMPANYCODE_CD),
		SAREA_ID(CompCodeSareaEntity.ATTR_SAREA_ID);
		
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
	 * Contains the following column names: "COMPOSITE_ID, SAREA_DISPLAY_LONGNAME, SAREA_ID, COMPANYCODE_CD"
	 */
	public static final EnumSet<ColumnNames> COLUMNS_SET_1 = EnumSet.of(ColumnNames.COMPOSITE_ID,
			ColumnNames.SAREA_DISPLAY_LONGNAME, ColumnNames.SAREA_ID, ColumnNames.COMPANYCODE_CD);
	
	private List companyCodeList;
	private List<String> compositeIdList;
	
	/**
	 * 
	 * @param daoProvider
	 * @param uid
	 */
	public CompCodeSareaJpaQueryData(DaoProvider daoProvider, Long uid) {
		this(daoProvider, uid, null);
	}
	/**
	 * 
	 * @param daoProvider
	 * @param uid
	 * @param companyCodeList
	 */
	public CompCodeSareaJpaQueryData(DaoProvider daoProvider, Long uid, List companyCodeList) {
		super(daoProvider, uid);
		this.companyCodeList = companyCodeList;
	}
	
	protected Collection<String> getFromCollection() {
		List<String> result = new ArrayList<String>();
		result.add(CompCodeSareaEntity.class.getName());
		return result;
	}

	protected Collection<String> getWhereCollection() {
		List<String> result = new ArrayList<String>();
		result.add(EMPTY_WHERE_COND);// add true restriction "1=1"
		
		int paramNum = 1;
		StringBuilder sqlSelectQuery = new StringBuilder();
		if (getCompanyCodeList() != null && !getCompanyCodeList().isEmpty()) {
			sqlSelectQuery.append(CompCodeSareaEntity.ATTR_COMPANYCODE_CD);
			sqlSelectQuery.append(generateInClause(getCompanyCodeList(), paramNum));
			addAllQueryParam(getCompanyCodeList());

			result.add(sqlSelectQuery.toString());
			paramNum = getNextQueryParamIndex();
		}
		
		if (getCompositeIdList() != null && !getCompositeIdList().isEmpty()) {
			sqlSelectQuery = new StringBuilder();
			sqlSelectQuery.append(CompCodeSareaEntity.ATTR_COMPOSITE_ID);
			sqlSelectQuery.append(generateInClause(getCompositeIdList(), paramNum));
			addAllQueryParam(getCompositeIdList());
			
			result.add(sqlSelectQuery.toString());
			paramNum = getNextQueryParamIndex();
		}
		
		return result;
	}
	
	protected String getOrderBy() {
		StringBuilder sqlSelectQuery = new StringBuilder();
		sqlSelectQuery.append(" order by ");
		sqlSelectQuery.append(CompCodeSareaEntity.ATTR_SAREA_DISPLAY_LONGNAME);
		sqlSelectQuery.append(" asc");
		
		return sqlSelectQuery.toString();
	}

	protected Collection<String> getRestrictionCollection() {
		Collection<String> restList = new ArrayList<String>();
		if (getUid() == null) { // if the user id not defined --> do not generate restriction
			restList.add(EMPTY_WHERE_COND);// add true restriction "1=1"
			return restList;			
		}
		
		int param = getNextQueryParamIndex();
		restList.add(getAccessRestriction(UserCompanycodeAccEntity.class, CompCodeSareaEntity.ATTR_COMPANYCODE_CD, param));
		restList.add(getAccessRestriction(UserSareaAccEntity.class, CompCodeSareaEntity.ATTR_SAREA_ID, param));

		return restList;
	}
	
	public List getCompanyCodeList() {
		return companyCodeList;
	}
	
	public void setCompanyCodeList(List companyCodeList) {
		this.companyCodeList = companyCodeList;
	}
	public List<String> getCompositeIdList() {
		return compositeIdList;
	}
	public void setCompositeIdList(List<String> compositeIdList) {
		this.compositeIdList = compositeIdList;
	}	
	
	
}
