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
import com.crh.logon.persistence.entities.admin.UserShiptoAccEntity;
import com.crh.logon.persistence.entities.admin.UserSoldtoBpAccEntity;
import com.crh.logon.persistence.entities.views.SoldshipFlexSrchEntity;
import com.crh.logon.persistence.utils.DaoProvider;

/**
 * @author AdNovum Informatik AG
 */
public class SoldshipFlexSrchJpaQueryData<E extends Enum<E>> extends JpaQueryDataBase<E> {
	/**
	 * Contains the column names from Database object 'VLGNS_SOLDSHIP_FLEX_SRCH'. This enum records are used
	 * to construct {@link EnumSet} with column names. The {@link EnumSet} is used to
	 * generate the SQL select query.
	 *  
	 * If more columns needed, add it here.
	 * 
	 * @author AdNovum Informatik AG
	 */
	public enum ColumnNames {
		COMPOSITE_ID(SoldshipFlexSrchEntity.ATTR_COMPOSITE_ID),
		SHIPTO_NR(SoldshipFlexSrchEntity.ATTR_SHIPTO_NR),
		SHIPTO_NAME(SoldshipFlexSrchEntity.ATTR_SHIPTO_NAME),
		SOLDTO_NR(SoldshipFlexSrchEntity.ATTR_SOLDTO_NR),
		SOLDTO_SAP_BP_ID(SoldshipFlexSrchEntity.ATTR_SOLDTO_SAP_BP_ID),
		SHIPTO_SAP_BP_ID(SoldshipFlexSrchEntity.ATTR_SHIPTO_SAP_BP_ID),
		SOLDTO_NAME(SoldshipFlexSrchEntity.ATTR_SOLDTO_NAME);
		
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
	 * Contains the following column names: "COMPOSITE_ID, SOLDTO_NAME, SHIPTO_NAME, 
	 * SOLDTO_SAP_BP_ID, SHIPTO_SAP_BP_ID, SOLDTO_NR"
	 */
	public static final EnumSet<ColumnNames> COLUMNS_SET_1 = EnumSet.of(ColumnNames.COMPOSITE_ID,
			ColumnNames.SOLDTO_NAME, ColumnNames.SHIPTO_NAME, ColumnNames.SOLDTO_SAP_BP_ID, 
			ColumnNames.SHIPTO_SAP_BP_ID, ColumnNames.SOLDTO_NR, ColumnNames.SHIPTO_NR);
	
	private List soldtoNrList;
	private List<String> compositeIdList;
	
	/**
	 * 
	 * @param daoProvider
	 * @param uid
	 * @param soldtoNr
	 */
	public SoldshipFlexSrchJpaQueryData(DaoProvider daoProvider, Long uid, List soldtoNrList) {
		super(daoProvider, uid);
		
		this.soldtoNrList = soldtoNrList;
	}
	
	/**
	 * 
	 * @param daoProvider
	 * @param uid
	 */
	public SoldshipFlexSrchJpaQueryData(DaoProvider daoProvider, Long uid) {
		this(daoProvider, uid, null);
	}
	
	protected Collection<String> getFromCollection() {
		List<String> result = new ArrayList<String>();
		result.add(SoldshipFlexSrchEntity.class.getName());
		return result;
	}

	protected Collection<String> getWhereCollection() {
		List<String> result = new ArrayList<String>();
		StringBuilder sqlSelectQuery = new StringBuilder();
		
		int paramNum = 1;
		if (getSoldtoNrList() != null && !getSoldtoNrList().isEmpty()) {
			sqlSelectQuery.append(SoldshipFlexSrchEntity.ATTR_SOLDTO_NR);
			sqlSelectQuery.append(generateInClause(getSoldtoNrList(), paramNum));
			addAllQueryParam(getSoldtoNrList());

			result.add(sqlSelectQuery.toString());
			paramNum = getNextQueryParamIndex();
		}
		
		if (getCompositeIdList() != null && !getCompositeIdList().isEmpty()) {
			sqlSelectQuery = new StringBuilder();
			sqlSelectQuery.append(SoldshipFlexSrchEntity.ATTR_COMPOSITE_ID);
			sqlSelectQuery.append(generateInClause(getCompositeIdList(), paramNum));
			addAllQueryParam(getCompositeIdList());
			
			result.add(sqlSelectQuery.toString());
			paramNum = getNextQueryParamIndex();
		}
		
		if (result.isEmpty()) { // add true restriction "1=1"
			result.add(EMPTY_WHERE_COND);
		}
		
		return result;
	}
	
	protected String getOrderBy() {
		StringBuilder sqlSelectQuery = new StringBuilder();
		sqlSelectQuery.append(" order by ");
		sqlSelectQuery.append(SoldshipFlexSrchEntity.ATTR_SOLDTO_NR);
		sqlSelectQuery.append(" asc");
		
		return sqlSelectQuery.toString();
	}

	protected Collection<String> getRestrictionCollection() {
		Collection<String> restList = new ArrayList<String>();
		if (getUid() == null) { // if the user id not defined --> do not generate restriction
			restList.add(EMPTY_WHERE_COND);// add true restriction "1=1"
			return restList;			
		}
		
		int paramNum = getNextQueryParamIndex();
		restList.add(getAccessRestriction(UserShiptoAccEntity.class, SoldshipFlexSrchEntity.ATTR_SHIPTO_SAP_BP_ID, paramNum));
		restList.add(getAccessRestriction(UserSoldtoBpAccEntity.class, SoldshipFlexSrchEntity.ATTR_SOLDTO_SAP_BP_ID, paramNum));
		// FIXME (bartha Oct 4, 2012): needs clarification. In the previous implemenation the above comenmted out restrictions was fetched for the load 
		restList.add(getSpecialAccessRestriction(UserCompanycodeAccEntity.class, SoldshipFlexSrchEntity.ATTR_SHIPTO_SAP_BP_ID, IN_SapBpOfCompanycodeRestr, paramNum));
		restList.add(getSpecialAccessRestriction(UserSareaAccEntity.class, SoldshipFlexSrchEntity.ATTR_SHIPTO_SAP_BP_ID, IN_SapBpOfSareaRestr, paramNum));
   
		return restList;
	}

	public List getSoldtoNrList() {
		return soldtoNrList;
	}

	public void setSoldtoNr(List soldtoNrList) {
		this.soldtoNrList = soldtoNrList;
	}

	public List<String> getCompositeIdList() {
		return compositeIdList;
	}

	public void setCompositeIdList(List<String> compositeIdList) {
		this.compositeIdList = compositeIdList;
	}
	
}
