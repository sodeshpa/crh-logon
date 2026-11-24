/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.directjpa;

import java.io.Serializable;
import java.util.EnumSet;

import ch.nevis.gdi.jpa.BaseEntity;

/**
 * @author AdNovum Informatik AG
 */
public interface JpaQueryData<E extends Enum<E>> extends Serializable {
	
	/**
	 * Load the JPA SQL query.
	 * 
	 * @return
	 */
	public String getJpaQuery();
	
	/**
	 * Get the query parameters used in the JPA SQL query.
	 * 
	 * @return
	 */
	public Object[] getQueryParams();
	
	/**
	 * Add an {@link EnumSet} with the column names to load. These columns will
	 * be generated into SQL select columns script.
	 * 
	 * @param arg
	 */
	public void addSelectColumnNames(EnumSet<E> arg);
	
	/**
	 * Returns the index for the specified column from the SQL select query columns.
	 * 
	 * If the column name was not specified for the select SQL query, <tt>-1</tt> is returned. 
	 * 
	 * @param columnName
	 * 			The name of the column whose index to get.
	 * @return
	 */
	public int getIndexOf(String columnName);
	
	/**
	 * Return <code>true</code> if Jpa Query contains restriction for some tlgnc_user...acc object,
	 * <code>false</code> otherwise.
	 * 
	 * This function can be called after getJpaQuery(), because the restriction are generated there.
	 * 
	 * @return
	 * 		Return <code>true</code> when the user has restriction, <code>false</code> otherwise
	 */
	public boolean hasUserRestriction();
	
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
	public boolean hasUserRestriction(Class<? extends BaseEntity> restrictionEntity);
}
