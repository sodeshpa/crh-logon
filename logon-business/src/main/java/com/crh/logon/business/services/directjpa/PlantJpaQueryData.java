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
import com.crh.logon.persistence.entities.admin.UserPlantAccEntity;
import com.crh.logon.persistence.entities.admin.UserSareaAccEntity;
import com.crh.logon.persistence.entities.views.PlantSareaCompcodeEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
public class PlantJpaQueryData<E extends Enum<E>> extends JpaQueryDataBase<E> {

	/**
	 * Contains the column names from Database object 'VLGNC_PLANT_SAREA_COMPCODE'. This enum records are used
	 * to construct {@link EnumSet} with column names. The {@link EnumSet} is used to
	 * generate the SQL select query.
	 *  
	 * If more columns needed, add it here.
	 * 
	 * @author AdNovum Informatik AG
	 */
	public enum ColumnNames {
		PLANT_CD(PlantSareaCompcodeEntity.ATTR_PLANT_CD),
		PLANT_NAME(PlantSareaCompcodeEntity.ATTR_PLANT_NAME);
		
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
	 * Contains the following column names: "PLANT_CD, PLANT_NAME"
	 * 
	 * DO NOT extend this {@link EnumSet}. Is used as "select distinct", the service which uses
	 * needs only the plant information.
	 */
	public static final EnumSet<ColumnNames> COLUMNS_SET_1 = EnumSet.of(ColumnNames.PLANT_CD,
			ColumnNames.PLANT_NAME); 		
	
	private Boolean productionPlant;
	private List<String> companycodeCdList;
	private List<Long> sareaList;
	private Boolean isActive;
	private String plantName;
	
	private boolean restrToSarea;
	private boolean restrToCompcode;

	/**
	 * 
	 * @param daoProvider
	 * @param uid
	 */
	public PlantJpaQueryData(DaoProvider daoProvider, long uid) {
		this(daoProvider, uid, null);
	}
	
	/**
	 * 
	 * @param daoProvider
	 * @param uid
	 * @param companycodeCd
	 */
	public PlantJpaQueryData(DaoProvider daoProvider, long uid, List<String> companycodeCdList) {
		this(daoProvider, uid, companycodeCdList, null);
	}
	
	/**
	 * 
	 * @param daoProvider
	 * @param uid
	 * @param companycodeCd
	 * @param productionPlant
	 */
	public PlantJpaQueryData(DaoProvider daoProvider, long uid, List<String> companycodeCdList, Boolean productionPlant) {
		super(daoProvider, uid);
		this.companycodeCdList = companycodeCdList;
		this.productionPlant = productionPlant;
	}
	
	/**
	 * 
	 * @return
	 */
	protected Collection<String> getRestrictionCollection() {
		int paramNum = getNextQueryParamIndex();
		Collection<String> restList = new ArrayList<String>();
		restList.add(getAccessRestriction(UserPlantAccEntity.class,
				PlantSareaCompcodeEntity.ATTR_PLANT_CD, paramNum));

		if (isRestrToCompcode()) { // add compcode restr
			restList.add(getAccessRestriction(UserCompanycodeAccEntity.class,
					PlantSareaCompcodeEntity.ATTR_COMPANYCODE_CD, paramNum));
		}
		if (isRestrToSarea()) { // add sarea restr
			restList.add(getAccessRestriction(UserSareaAccEntity.class,
					PlantSareaCompcodeEntity.ATTR_SAREA_ID, paramNum));
		}

		return restList;
	}
	
	protected Collection<String> getFromCollection() {
		List<String> result = new ArrayList<String>();
		result.add(PlantSareaCompcodeEntity.class.getName());
		return result;
	}

	protected Collection<String> getWhereCollection() {
		List<String> result = new ArrayList<String>();
		result.add(EMPTY_WHERE_COND);
		StringBuilder sqlSelectQuery = new StringBuilder();
		
		int paramNum = 1;
		if (getCompanycodeCdList() != null && !getCompanycodeCdList().isEmpty()) {
			sqlSelectQuery = new StringBuilder();
			sqlSelectQuery.append(PlantSareaCompcodeEntity.ATTR_COMPANYCODE_CD);
			sqlSelectQuery.append(generateInClause(getCompanycodeCdList(), paramNum));
			addAllQueryParam(getCompanycodeCdList());
			paramNum = getNextQueryParamIndex();
			result.add(sqlSelectQuery.toString());
		}
		if (getSareaList() != null && !getSareaList().isEmpty()) {
			sqlSelectQuery = new StringBuilder();
			sqlSelectQuery.append(PlantSareaCompcodeEntity.ATTR_SAREA_ID);
			sqlSelectQuery.append(generateInClause(getSareaList(), paramNum));
			addAllQueryParam(getSareaList());
			paramNum = getNextQueryParamIndex();
			result.add(sqlSelectQuery.toString());
		}
		if (getProductionPlant() != null) {
			sqlSelectQuery = new StringBuilder();
			sqlSelectQuery.append(PlantSareaCompcodeEntity.ATTR_PRODUCTION_PLANT);
			sqlSelectQuery.append(" = ?" + paramNum++);
			addQueryParam(getProductionPlant());
			result.add(sqlSelectQuery.toString());
		}
		if (getIsActive() != null) {
			sqlSelectQuery = new StringBuilder();
			sqlSelectQuery.append(PlantSareaCompcodeEntity.ATTR_CTL_ACT_CD);
			sqlSelectQuery.append(" = ?" + paramNum++);
			addQueryParam(getIsActive());
			result.add(sqlSelectQuery.toString());
		}
		if (!StringUtils.empty(getPlantName())) {
			sqlSelectQuery = new StringBuilder();
			sqlSelectQuery.append("UPPER(" + PlantSareaCompcodeEntity.ATTR_PLANT_NAME + ")");
			sqlSelectQuery.append(" LIKE ?" + paramNum++);
			addQueryParam("%" + getPlantName().toUpperCase() + "%");
			result.add(sqlSelectQuery.toString());
		}
		return result;
	}
	
	protected String getOrderBy() {
		StringBuilder sqlSelectQuery = new StringBuilder();
		sqlSelectQuery.append(" order by ");
		sqlSelectQuery.append(PlantSareaCompcodeEntity.ATTR_PLANT_NAME);
		sqlSelectQuery.append(" asc");
		
		return sqlSelectQuery.toString();
	}

	public Boolean getProductionPlant() {
		return productionPlant;
	}

	public void setProductionPlant(Boolean productionPlant) {
		this.productionPlant = productionPlant;
	}

	public boolean isRestrToSarea() {
		return restrToSarea;
	}

	public void setRestrToSarea(boolean restrToSarea) {
		this.restrToSarea = restrToSarea;
	}

	public boolean isRestrToCompcode() {
		return restrToCompcode;
	}

	public void setRestrToCompcode(boolean restrToCompcode) {
		this.restrToCompcode = restrToCompcode;
	}

	public List<String> getCompanycodeCdList() {
		return companycodeCdList;
	}

	public void setCompanycodeCdList(List<String> companycodeCdList) {
		this.companycodeCdList = companycodeCdList;
	}

	public List<Long> getSareaList() {
		return sareaList;
	}

	public void setSareaList(List<Long> sareaList) {
		this.sareaList = sareaList;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getPlantName() {
		return plantName;
	}

	public void setPlantName(String plantName) {
		this.plantName = plantName;
	}
	
}
