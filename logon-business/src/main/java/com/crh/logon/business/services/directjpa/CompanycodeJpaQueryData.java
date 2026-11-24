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
import com.crh.logon.persistence.entities.views.PlantSareaCompcodeEntity;
import com.crh.logon.persistence.utils.DaoProvider;

/**
 * @author AdNovum Informatik AG
 */
public class CompanycodeJpaQueryData<E extends Enum<E>> extends JpaQueryDataBase<E> {
	
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
		COMPANYCODE_CD(PlantSareaCompcodeEntity.ATTR_COMPANYCODE_CD),
		COMPANYCODE_NAME(PlantSareaCompcodeEntity.ATTR_COMPANYCODE_NAME);
		
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
	 * Contains the following column names: "COMPANYCODE_CD, COMPANYCODE_NAME"
	 * 
	 * DO NOT extend this {@link EnumSet}. Is used as "select distinct", the service which uses
	 * needs only the company information.
	 */
	public static final EnumSet<ColumnNames> COLUMNS_SET_1 = EnumSet.of(ColumnNames.COMPANYCODE_CD,
			ColumnNames.COMPANYCODE_NAME); 

	public CompanycodeJpaQueryData(DaoProvider daoProvider, long uid) {
		super(daoProvider, uid);
	}	
	
	protected Collection<String> getFromCollection() {
		List<String> result = new ArrayList<String>();
		result.add(PlantSareaCompcodeEntity.class.getName());
		return result;
	}

	protected Collection<String> getWhereCollection() {
		List<String> result = new ArrayList<String>();
		result.add(EMPTY_WHERE_COND);
		return result;
	}
	
	protected Collection<String> getRestrictionCollection() {
		int paramNum = getNextQueryParamIndex();
		List<String> result = new ArrayList<String>();
		result.add(getAccessRestriction(UserCompanycodeAccEntity.class, PlantSareaCompcodeEntity.ATTR_COMPANYCODE_CD, paramNum));
		return result;
		
	}
	
	protected String getOrderBy() {
		StringBuilder sqlSelectQuery = new StringBuilder();
		sqlSelectQuery.append(" order by ");
		sqlSelectQuery.append(PlantSareaCompcodeEntity.ATTR_COMPANYCODE_NAME);
		sqlSelectQuery.append(" asc");
		
		return sqlSelectQuery.toString();
	}
	
}
