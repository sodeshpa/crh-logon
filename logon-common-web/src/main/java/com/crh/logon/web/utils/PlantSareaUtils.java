/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.PlantSareaService;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;

/**
 * @author AdNovum Informatik AG
 */
public class PlantSareaUtils {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PlantSareaUtils.class);	
    
	/**
	 * Load active plants for the given <code>companycodeCd</code> and <code>productionPlantFlag</code> with 
	 * the user authorization for:
	 * 	- TLGNU_USER_COMPANYCODE_ACC
	 * 	- TLGNU_USER_SAREA_ACC
	 *	- TLGNU_USER_PLANT_ACC
	 * 
	 * @param plantSareaService
	 * 				The {@link PlantSareaService} service bean.
	 * 
	 * @param companycodeCd
	 * 				The TLGNR_COMPANYCODE_BS.COMPANYCODE_CD to filter, nullable (when null, all plants are loaded)
	 * 
	 * @param productionPlantFlag
	 * 				when <code>true</code>, only the production plants
	 * 				when <code>false</code>, delivery plants which are not part of production plants
	 * 				when <code>null</code>, load all plants (delivery plants)
	 * 
	 * @return
	 * 			A list of {@link DropdownEntity} objects.
	 */
	public static List<DropdownEntity> getPlantByCompanycodeCd(PlantSareaService plantSareaService,
			String companycodeCd) {
		// [[
		List<DropdownEntity> result = null;
		Object[][] params = {{"plantSareaService", plantSareaService},
							{"companycodeCd", companycodeCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(PlantSareaUtils.class, "getPlantByCompanycodeCd", params);
		}
		// ]]
		try {
			return result = getPlantsByCompanycodeCdAndProdPlantFlag(plantSareaService, companycodeCd, null, true);
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
	 * Load all plants for the given <code>companycodeCd</code> and <code>productionPlantFlag</code> with 
	 * the user authorization for:
	 * 	- TLGNU_USER_COMPANYCODE_ACC
	 * 	- TLGNU_USER_SAREA_ACC
	 *	- TLGNU_USER_PLANT_ACC
	 * 
	 * @param plantSareaService
	 * 				The {@link PlantSareaService} service bean.
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
	 * 
	 * @return
	 * 			A list of {@link DropdownEntity} objects.
	 */
	public static List<DropdownEntity> getPlantsByCompanycodeCdAndProdPlantFlag(
			PlantSareaService plantSareaService, String companycodeCd, Boolean productionPlantFlag, Boolean isActive) {
		// [[
		List<DropdownEntity> result = null;
		Object[][] params = {{"plantSareaService", plantSareaService},
							{"companycodeCd", companycodeCd},
							{"productionPlantFlag", productionPlantFlag}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(PlantSareaUtils.class, "getPlantsByCompanycodeCdAndProdPlantFlag", params);
		}
		// ]]
		try {
			return result = getPlantList(plantSareaService, companycodeCd, null, productionPlantFlag, isActive);
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
	 * @param plantSareaService
	 * 				The {@link PlantSareaService} service bean.
	 * @param companycodeCd
	 * 				The TLGNR_COMPANYCODE_BS.COMPANYCODE_CD to filter, nullable (when null, all plants are loaded)
	 * @param plantNameSrchTxt
	 * 				The name or part of the plant name to look up. JPA Case-insensitive like matching is used. Nullable.
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
	 * 
	 * @return
	 * 			A list of {@link DropdownEntity} objects.
	 */
	public static List<DropdownEntity> getPlantList(PlantSareaService plantSareaService,
			String companycodeCd, String plantNameSrchTxt, Boolean productionPlantFlag,
			Boolean isActive) {
		// [[
		List<DropdownEntity> result = null;
		Object[][] params = {{"plantSareaService", plantSareaService},
							{"companycodeCd", companycodeCd},
							{"plantNameSrchTxt", plantNameSrchTxt},
							{"productionPlantFlag", productionPlantFlag},
							{"isActive", isActive}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(PlantSareaUtils.class, "getPlantList", params);
		}
		// ]]
		try {
			List<Object[]> plantList = plantSareaService.getPlantList(companycodeCd, plantNameSrchTxt, productionPlantFlag, isActive);
			
			if (plantList == null || plantList.isEmpty()) {
				return result = Collections.EMPTY_LIST; 
			}		
			
			result = new ArrayList<DropdownEntity>();
			for (Iterator<Object[]> iter = plantList.iterator(); iter.hasNext();) {
				Object[] entity = iter.next();
				
				DropdownEntity dropdownEntity = new DropdownEntity(String.valueOf(entity[1]), String.valueOf(entity[0]));
				result.add(dropdownEntity);
			}
			return result;
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
	 * Load all active plants for the given <code>companycodeCd</code> and <code>productionPlantFlag</code> with 
	 * the user authorization for:
	 * 	- TLGNU_USER_COMPANYCODE_ACC
	 * 	- TLGNU_USER_SAREA_ACC
	 *	- TLGNU_USER_PLANT_ACC
	 * 
	 * @param plantSareaService
	 * 				The {@link PlantSareaService} service bean.
	 * 
	 * @param plantSareaService
	 * @return
	 * 			A list of {@link DropdownEntity} objects.
	 */
	public static List<DropdownEntity> getPlantList(PlantSareaService plantSareaService) {
		// [[
		List<DropdownEntity> result = null;
		Object[][] params = {{"plantSareaService", plantSareaService}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(PlantSareaUtils.class, "getPlantList", params);
		}
		// ]]
		try {
			return result = getPlantList(plantSareaService, null, null, null, true);
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
	 * 
	 * @param refData
	 * @return
	 */
	public static List<DropdownEntity> getCompanycodeBsEntityList(PlantSareaService plantSareaService) {
		// [[
		List<DropdownEntity> result = null;
		Object[][] params = {{"plantSareaService", plantSareaService}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(PlantSareaUtils.class, "getWagonTypeTxEntityList", params);
		}
		// ]]
		try {
			
			
			List plantList = plantSareaService.getCompanycodeList();
			
			if (plantList == null || plantList.isEmpty()) {
				return result = Collections.EMPTY_LIST; 
			}		
			
			result = new ArrayList<DropdownEntity>();
			for (Iterator<Object[]> iter = plantList.iterator(); iter.hasNext();) {
				Object[] entity = iter.next();
				
				DropdownEntity dropdownEntity = new DropdownEntity(String.valueOf(entity[1]), String.valueOf(entity[0]));
				result.add(dropdownEntity);
			}
			return result;
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
	 * LOGON-4999: Truck Home Plant
	 * Checks if shippingpoint exists in the shippingpoints list with the given plant code.
	 */
	public static boolean isShippingpointExistForPlantCd(List<ShippingpointEntity> shippingpoints,
			String plantCd) {
		
		for (ShippingpointEntity sp : shippingpoints) {
			if (sp.getPlant().getPlantCd().equals(plantCd)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * LOGON-4999: Truck Home Plant
	 * Filters the shippingpoints list to only return those which has the given plant code set.
	 */
	public static List<ShippingpointEntity> getShippingpointsForPlantCd(
			List<ShippingpointEntity> shippingpoints, String plantCd) {

		List<ShippingpointEntity> filtered = new ArrayList<ShippingpointEntity>();

		for (ShippingpointEntity sp : shippingpoints) {
			if (sp.getPlant().getPlantCd().equals(plantCd)) {
				filtered.add(sp);
			}
		}
		return filtered;
	}

}
