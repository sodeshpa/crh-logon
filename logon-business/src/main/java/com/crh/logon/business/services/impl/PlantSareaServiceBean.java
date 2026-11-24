/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.directjpa.CompanycodeJpaQueryData;
import com.crh.logon.business.services.localinterfaces.PlantSareaServiceLocalInterface;
import com.crh.logon.business.services.directjpa.JpaQueryData;
import com.crh.logon.business.services.directjpa.PlantJpaQueryData;
import com.crh.logon.business.services.dispatchers.PlantSareaServiceDispatcher;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.persistence.entities.admin.UserPlantAccEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
public class PlantSareaServiceBean extends PlantSareaServiceDispatcher implements PlantSareaServiceLocalInterface {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PlantSareaServiceBean.class);
	
	/**
	 * Load all plants for the given <code>companycodeCd</code>,  <code>isActive</code> 
	 * and <code>productionPlantFlag</code> with the user authorization for:
	 * 	- TLGNU_USER_COMPANYCODE_ACC
	 * 	- TLGNU_USER_SAREA_ACC
	 *	- TLGNU_USER_PLANT_ACC
	 * 
	 * @param companycodeCd
	 * 				The TLGNR_COMPANYCODE_BS.COMPANYCODE_CD to filter, nullable (when null, all plants are loaded)
	 * @param productionPlantFlag
	 * 				when <code>true</code>, only the production plants
	 * 				when <code>false</code>, delivery plants which are not part of production plants
	 * 				when <code>null</code>, load all plants (delivery plants)
	 * @param isActive
	 * 			<code>true</code> load only active plants
	 * 			<code>false</code> if load only inactive plants
	 * 			<code>NULL</code> load all plants
	 * @return
	 * 			A {@link List} of object array, where the object array [0] = PLANT_CD, and [1] = PLANT_NAME
	 */
	public List getPlantListByCompanycodeCd(String companycodeCd, Boolean productionPlantFlag, Boolean isActive) {
		// [[
		List result = null;
		Object[][] params = {{"companycodeCd", companycodeCd},
							{"productionPlantFlag", productionPlantFlag},
							{"isActive", isActive}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getPlantListByCompanycodeCd", params);
		}
		// ]]
		try {
			return result = getPlantList(companycodeCd, null, productionPlantFlag, isActive);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Load all plants for the given <code>companycodeCd</code>, <code>plantNameSrchTxt</code>, <code>isActive</code> 
	 * and <code>productionPlantFlag</code> with the user authorization for:
	 * 	- TLGNU_USER_COMPANYCODE_ACC
	 * 	- TLGNU_USER_SAREA_ACC
	 *	- TLGNU_USER_PLANT_ACC
	 * 
	 * @param companycodeCd
	 * 				The TLGNR_COMPANYCODE_BS.COMPANYCODE_CD to filter, nullable (when null, all plants are loaded)
	 * @param plantNameSrchTxt
	 * 				The name or part of the plant name to look up. JPA Case-insensitive like matching is used. Nullable.
	 * @param productionPlantFlag
	 * 				when <code>true</code>, only the production plants
	 * 				when <code>false</code>, delivery plants which are not part of production plants
	 * 				when <code>null</code>, load all plants (delivery plants)
	 * @param isActive
	 * 			<code>true</code> load only active plants
	 * 			<code>false</code> if load only inactive plants
	 * 			<code>NULL</code> load all plants
	 * @return
	 * 			A {@link List} of object array, where the object array [0] = PLANT_CD, and [1] = PLANT_NAME
	 */
	public List getPlantList(String companycodeCd, String plantNameSrchTxt, Boolean productionPlantFlag, Boolean isActive) {
		// [[
		List result = null;
		Object[][] params = {{"companycodeCd", companycodeCd},
							{"plantNameSrchTxt", plantNameSrchTxt},
							{"productionPlantFlag", productionPlantFlag},
							{"isActive", isActive}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getPlantListByCompanycodeCd", params);
		}
		// ]]
		try {
			List<String> companycodeCdList = null;
			if (!StringUtils.empty(companycodeCd)) {
				companycodeCdList = new ArrayList<String>();
				companycodeCdList.add(companycodeCd);
			}
			PlantJpaQueryData<PlantJpaQueryData.ColumnNames> jpaSQLData = new PlantJpaQueryData<PlantJpaQueryData.ColumnNames>(
					getDaoProvider(), getUserId(), companycodeCdList, productionPlantFlag);
			jpaSQLData.setIsActive(isActive);
			jpaSQLData.setPlantName(plantNameSrchTxt);
			jpaSQLData.addSelectColumnNames(PlantJpaQueryData.COLUMNS_SET_1);
			jpaSQLData.setRestrToCompcode(true);
			jpaSQLData.setRestrToSarea(true);
			
			return result = getPlantList(jpaSQLData);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Load plants by the given parameters with the user restriction to:
	 *	- TLGNU_USER_PLANT_ACC
	 * 
	 * @param userId
	 * 			The id of the user to load the assigned plants
	 * @param productionPlantFlag
	 * 				when <code>true</code>, only the production plants
	 * 				when <code>false</code>, delivery plants which are not part of production plants
	 * 				when <code>null</code>, load all plants (delivery plants)
	 * @param loadWhenNoRestr
	 * 			when <code>true</code>, will load TLGNC_POOL_TX no matter if the user has no pool restriction,
	 * 			otherwise will load only if the user has pool restriction.
	 */
	public Map getPlantList(Long userId, Boolean productionPlantFlag, boolean loadWhenNoRestr) {
		// [[
		Map result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getPlantList");
		}
		// ]]
		try {
			if (userId == null) {
				throw new IllegalArgumentException("The userId is NULL!");
			}
			
			ReadWriteDao<UserPlantAccEntity> userPlantAccEntityDao = getDaoProvider().getUserPlantAccEntityDao();

			Filter<UserPlantAccEntity> userPlantFilter = new FilterImpl<UserPlantAccEntity>();
			userPlantFilter.addExpression(Restrictions.eq(UserPlantAccEntity.ATTR_USER_ID, userId));
			List userPlantAccList = userPlantAccEntityDao.find(userPlantFilter);
			boolean userHasPlantRestr = userPlantAccList == null || userPlantAccList.isEmpty();
			if (!loadWhenNoRestr && userHasPlantRestr) {
				
				return result = null; // return null, as the user has no restriction
			}
			
			PlantJpaQueryData<PlantJpaQueryData.ColumnNames> jpaSQLData = new PlantJpaQueryData<PlantJpaQueryData.ColumnNames>(getDaoProvider(), userId);
			jpaSQLData.addSelectColumnNames(PlantJpaQueryData.COLUMNS_SET_1);
			
			List<String> textColumns = new ArrayList<String>();
			textColumns.add(PlantJpaQueryData.ColumnNames.PLANT_NAME.toString());
			
			return result = LogonUtil.createMapFromList(getPlantList(jpaSQLData), jpaSQLData,
					PlantJpaQueryData.ColumnNames.PLANT_CD.toString(), textColumns, "%s");
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private List<Object[]> getPlantList(JpaQueryData jpaQuery) {
		
		String sqlQuery =  jpaQuery.getJpaQuery();
		Object[] paramArray = jpaQuery.getQueryParams();
		
		return getDaoProvider().getDirectJpaDao().find(sqlQuery, paramArray);
	}

	/**
	 * Load all companycode with the user authorization for:
	 * 	- TLGNU_USER_COMPANYCODE_ACC
	 * 
	 * (non-Javadoc)
	 * @see PlantSareaServiceDispatcher#getCompanycodeList(java.lang.String)
	 */
	public List getCompanycodeList() {
		// [[
		List result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getCompanycodeList");
		}
		// ]]
		try {
			Long uid = getUserId();
			DaoProvider daoProvider = getDaoProvider();
			
			JpaQueryData<CompanycodeJpaQueryData.ColumnNames> jpaSQLData = new CompanycodeJpaQueryData<CompanycodeJpaQueryData.ColumnNames>(
					daoProvider, uid);
			jpaSQLData.addSelectColumnNames(CompanycodeJpaQueryData.COLUMNS_SET_1);
			String sqlQuery =  jpaSQLData.getJpaQuery();
			Object[] paramArray = jpaSQLData.getQueryParams();
			
			return result = daoProvider.getDirectJpaDao().find(sqlQuery, paramArray);
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
}
