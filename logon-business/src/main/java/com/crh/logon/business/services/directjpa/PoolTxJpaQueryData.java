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

import com.crh.logon.persistence.entities.admin.UserPoolAccEntity;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
public class PoolTxJpaQueryData<E extends Enum<E>> extends JpaQueryDataBase<E> {
	private final static String POOL_ID_FIELD_NAME = "poolTxPK.poolId";
	private final static String LANG_CD_FIELD_NAME = "poolTxPK.langCd";
	
	/**
	 * Contains the column names from Database object 'TLGNC_POOL_TX'. This enum records are used
	 * to construct {@link EnumSet} with column names. The {@link EnumSet} is used to
	 * generate the SQL select query.
	 *  
	 * If more columns needed, add it here.
	 * 
	 * @author AdNovum Informatik AG
	 */
	public enum ColumnNames {
		POOL_ID_FIELD_NAME("poolTxPK.poolId"),
		NAME(PoolTxEntity.ATTR_NAME);
		
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
	 * Contains the following column names: "POOL_ID, NAME"
	 */
	public static final EnumSet<ColumnNames> COLUMNS_SET_1 = EnumSet.of(ColumnNames.POOL_ID_FIELD_NAME,
			ColumnNames.NAME); 
	
	private String langCd;
	
	/**
	 * 
	 * @param daoProvider
	 * @param uid
	 * @param langCd
	 */
	public PoolTxJpaQueryData(DaoProvider daoProvider, long uid, String langCd) {
		super(daoProvider, uid);
		
		this.langCd = langCd;
	}
	
	protected Collection<String> getFromCollection() {
		List<String> result = new ArrayList<String>();
		result.add(PoolTxEntity.class.getName());
		return result;
	}

	protected Collection<String> getWhereCollection() {
		List<String> result = new ArrayList<String>();
		if (StringUtils.empty(getLangCd())) {
			result.add(EMPTY_WHERE_COND);
			return result;
		}
		
		StringBuilder sqlSelectQuery = new StringBuilder();
		int paramNum = 1;
		sqlSelectQuery.append(LANG_CD_FIELD_NAME);
		sqlSelectQuery.append(" = ?" + paramNum);
		result.add(sqlSelectQuery.toString());
		addQueryParam(getLangCd());

		return result;
	}
	
	protected String getOrderBy() {
		StringBuilder sqlSelectQuery = new StringBuilder();
		sqlSelectQuery.append(" order by ");
		sqlSelectQuery.append(PoolTxEntity.ATTR_NAME);
		sqlSelectQuery.append(" asc");
		
		return sqlSelectQuery.toString();
	}

	protected Collection<String> getRestrictionCollection() {
		Collection<String> restList = new ArrayList<String>();
		int paramNum = getNextQueryParamIndex();
		restList.add(getAccessRestriction(UserPoolAccEntity.class, POOL_ID_FIELD_NAME, paramNum));
		
		return restList;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getLangCd() {
		return langCd;
	}

	/**
	 * 
	 * @param langCd
	 */
	public void setLangCd(String langCd) {
		this.langCd = langCd;
	}

}
