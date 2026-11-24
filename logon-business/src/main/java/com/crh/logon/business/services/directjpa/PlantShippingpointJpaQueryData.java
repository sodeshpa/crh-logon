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

import com.crh.logon.persistence.entities.admin.UserPlantAccEntity;
import com.crh.logon.persistence.entities.admin.UserShippingpointAccEntity;
import com.crh.logon.persistence.entities.views.PlantShippingpointEntity;
import com.crh.logon.persistence.utils.DaoProvider;

/**
 * @author AdNovum Informatik AG
 */
public class PlantShippingpointJpaQueryData<E extends Enum<E>> extends JpaQueryDataBase<E> {
	
	/**
	 * Contains the column names from Database object 'VLGNU_PLANT_SHIPPINGPOINT'. This enum records are used
	 * to construct {@link EnumSet} with column names. The {@link EnumSet} is used to
	 * generate the SQL select query.
	 *  
	 * If more columns needed, add it here.
	 * 
	 * @author AdNovum Informatik AG
	 */
	public enum ColumnNames {
		COMPOSITE_ID(PlantShippingpointEntity.ATTR_COMPOSITE_ID),
		SHIPPINGPOINT_NAME(PlantShippingpointEntity.ATTR_SHIPPINGPOINT_NAME),
		SAP_SHIPPINGPOINT_NO(PlantShippingpointEntity.ATTR_SAP_SHIPPINGPOINT_NO),
		PLANT_NAME(PlantShippingpointEntity.ATTR_PLANT_NAME),
		SHIPPINGPOINT_ID(PlantShippingpointEntity.ATTR_SHIPPINGPOINT_ID),
		PLANT_CD(PlantShippingpointEntity.ATTR_PLANT_CD);
		
		private final String columnName;
		
		private ColumnNames(String columnName) {
			this.columnName = columnName;
		}
		
		public String toString() {
			return columnName;
		}		
	}
	
	/**
	 * Used to construct the select query column names. 
	 * Contains the following column names: "COMPOSITE_ID, SHIPPINGPOINT_NAME, SAP_SHIPPINGPOINT_NO, PLANT_NAME
	 * HIPPINGPOINT_ID, PLANT_CD"
	 */
	public static final EnumSet<ColumnNames> COLUMNS_SET_1 = EnumSet.of(ColumnNames.COMPOSITE_ID,
			ColumnNames.SHIPPINGPOINT_NAME, ColumnNames.SAP_SHIPPINGPOINT_NO, ColumnNames.PLANT_NAME,
			ColumnNames.SHIPPINGPOINT_ID, ColumnNames.PLANT_CD); 

	private List<String> plantCdList;
	private List<String> compositeIdList;
	
	/**
	 * 
	 * @param daoProvider
	 * @param uid
	 */
	public PlantShippingpointJpaQueryData(DaoProvider daoProvider, Long uid) {
		this(daoProvider, uid, null);
	}
	
	/**
	 * 
	 * @param daoProvider
	 * @param uid
	 * @param companycodeCd
	 */
	public PlantShippingpointJpaQueryData(DaoProvider daoProvider, Long uid, List<String> plantCdList) {
		super(daoProvider, uid);
		this.plantCdList = plantCdList;
	}

	protected Collection<String> getFromCollection() {
		List<String> result = new ArrayList<String>();
		result.add(PlantShippingpointEntity.class.getName());
		return result;
	}
	
	protected Collection<String> getWhereCollection() {
		List<String> result = new ArrayList<String>();
		StringBuilder sqlSelectQuery = new StringBuilder();
		int paramNum = 1;
				
		if (getPlantCdList() != null && !getPlantCdList().isEmpty()) {
			sqlSelectQuery.append(PlantShippingpointEntity.ATTR_PLANT_CD);
			sqlSelectQuery.append(generateInClause(getPlantCdList(), paramNum));
			addAllQueryParam(getPlantCdList());
			
			result.add(sqlSelectQuery.toString());
			paramNum = getNextQueryParamIndex();
		}		
		
		if (getCompositeIdList() != null && !getCompositeIdList().isEmpty()) {
			sqlSelectQuery = new StringBuilder();
			sqlSelectQuery.append(PlantShippingpointEntity.ATTR_COMPOSITE_ID);
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
		sqlSelectQuery.append(PlantShippingpointEntity.ATTR_SHIPPINGPOINT_NAME);
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
		restList.add(getAccessRestriction(UserPlantAccEntity.class, PlantShippingpointEntity.ATTR_PLANT_CD, paramNum));
		restList.add(getAccessRestriction(UserShippingpointAccEntity.class, PlantShippingpointEntity.ATTR_SHIPPINGPOINT_ID, paramNum));
		
		return restList;
	}

	public List<String> getPlantCdList() {
		return plantCdList;
	}

	public void setPlantCdList(List<String> plantCdList) {
		this.plantCdList = plantCdList;
	}

	public List<String> getCompositeIdList() {
		return compositeIdList;
	}

	public void setCompositeIdList(List<String> compositeIdList) {
		this.compositeIdList = compositeIdList;
	}
	
	
}
