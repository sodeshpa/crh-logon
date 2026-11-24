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

import com.crh.logon.persistence.entities.admin.UserSapVendorAccEntity;
import com.crh.logon.persistence.entities.views.CarrierVendorEntity;
import com.crh.logon.persistence.utils.DaoProvider;

/**
 * The object is used to generate a JPA Query to load the Carrier information based on
 * users vendor restriction.
 * 
 * 
 * @author AdNovum Informatik AG
 */
public class CarrierJpaQueryData<E extends Enum<E>> extends JpaQueryDataBase<E> {
	
	/**
	 * Contains the column names from Database object 'VLGNC_CARRIER_VENDOR'. This enum records are used
	 * to construct {@link EnumSet} with column names. The {@link EnumSet} is used to
	 * generate the SQL select query.
	 *  
	 * If more columns needed, add it here.
	 * 
	 * @author AdNovum Informatik AG
	 */
	public enum ColumnNames {
		CARRIER_ID(CarrierVendorEntity.ATTR_CARRIER_ID),
		DISPLAY_NAME(CarrierVendorEntity.ATTR_DISPLAY_NAME),
		NAME1(CarrierVendorEntity.ATTR_NAME1),
		SAP_ID(CarrierVendorEntity.ATTR_SAP_ID),
		SAP_VENDOR_ID(CarrierVendorEntity.ATTR_SAP_VENDOR_ID);
		
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
	 * Contains the following column names: "SAP_VENDOR_ID, DISPLAY_NAME, NAME1, SAP_ID"
	 */
	public static final EnumSet<ColumnNames> COLUMNS_SET_1 = EnumSet.of(ColumnNames.SAP_VENDOR_ID,
			ColumnNames.DISPLAY_NAME, ColumnNames.NAME1, ColumnNames.SAP_ID); 		

	/**
	 * @param uid
	 * @param provider
	 */
	public CarrierJpaQueryData(Long uid, DaoProvider provider) {
		super(provider, uid);
	}
	
	
	protected Collection<String> getFromCollection() {
		List<String> result = new ArrayList<String>();
		result.add(CarrierVendorEntity.class.getName());
		return result;
	}

	protected Collection<String> getWhereCollection() {
		List<String> result = new ArrayList<String>();
		result.add(EMPTY_WHERE_COND);
		return result;
	}
	
	protected Collection<String> getRestrictionCollection() {
		List<String> result = new ArrayList<String>();
		int paramNum = getNextQueryParamIndex();
		result.add(getAccessRestriction(UserSapVendorAccEntity.class, CarrierVendorEntity.ATTR_SAP_VENDOR_ID, paramNum));
		return result;
		
	}
	
	protected String getOrderBy() {
		StringBuilder sqlSelectQuery = new StringBuilder();
		sqlSelectQuery.append(" order by ");
		sqlSelectQuery.append(CarrierVendorEntity.ATTR_DISPLAY_NAME);
		sqlSelectQuery.append(" asc");
		
		return sqlSelectQuery.toString();
	}
}
