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

import com.crh.logon.persistence.entities.admin.UserPurchorgAccEntity;
import com.crh.logon.persistence.entities.refdata.PurchasingorgBsEntity;
import com.crh.logon.persistence.utils.DaoProvider;

/**
 * @author AdNovum Informatik AG
 */
public class PurchasingorgBsJpaQueryData<E extends Enum<E>> extends JpaQueryDataBase<E> {

	/**
	 * Contains the column names from Database object 'TLGNR_PURCHASINGORG_BS'. This enum records are used
	 * to construct {@link EnumSet} with column names. The {@link EnumSet} is used to
	 * generate the SQL select query.
	 *  
	 * If more columns needed, add it here.
	 * 
	 * @author AdNovum Informatik AG
	 */
	public enum ColumnNames {
		PURCHASINGORG_CD(PurchasingorgBsEntity.ATTR_PURCHASINGORG_CD),
		PURCHASINGORG_NAME(PurchasingorgBsEntity.ATTR_PURCHASINGORG_NAME);
		
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
	 * Contains the following column names: "PURCHASINGORG_CD, PURCHASINGORG_NAME"
	 */
	public static final EnumSet<ColumnNames> COLUMNS_SET_1 = EnumSet.of(ColumnNames.PURCHASINGORG_CD, ColumnNames.PURCHASINGORG_NAME);
	
	/**
	 * 
	 * @param daoProvider
	 * @param uid
	 */
	public PurchasingorgBsJpaQueryData(DaoProvider daoProvider, long uid) {
		super(daoProvider, uid);
	}
	
	protected Collection<String> getFromCollection() {
		List<String> result = new ArrayList<String>();
		result.add(PurchasingorgBsEntity.class.getName());
		return result;
	}

	protected Collection<String> getWhereCollection() {
		List<String> result = new ArrayList<String>();
		result.add(EMPTY_WHERE_COND);// add true restriction "1=1"
		
		return result;
	}
	
	protected String getOrderBy() {
		StringBuilder sqlSelectQuery = new StringBuilder();
		sqlSelectQuery.append(" order by ");
		sqlSelectQuery.append(PurchasingorgBsEntity.ATTR_PURCHASINGORG_NAME);
		sqlSelectQuery.append(" asc");
		
		return sqlSelectQuery.toString();
	}

	protected Collection<String> getRestrictionCollection() {
		Collection<String> restList = new ArrayList<String>();
		restList.add(getAccessRestriction(UserPurchorgAccEntity.class, PurchasingorgBsEntity.ATTR_PURCHASINGORG_CD, getNextQueryParamIndex()));

		return restList;
	}
}
