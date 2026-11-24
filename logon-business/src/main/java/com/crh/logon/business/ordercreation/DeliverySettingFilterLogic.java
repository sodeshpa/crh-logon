/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ordercreation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.ordercreation.dto.DeliverySetting;
import com.crh.logon.business.ordercreation.dto.LoadingPointDTO;
import com.crh.logon.business.ordercreation.dto.PoolDTO;
import com.crh.logon.business.ordercreation.dto.SupplyPlantDTO;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.ordercreation.dto.CommercialCarrierDTO;
import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.business.ordercreation.dto.ExecutingCarrierDTO;
import com.crh.logon.business.ordercreation.dto.PlantDTO;
import com.crh.logon.business.ordercreation.dto.ShippingpointDTO;
import com.crh.logon.business.ordercreation.dto.TruckTypeWrapper;
import com.crh.logon.business.ordercreation.dto.TruckWagonTypeWrapper;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.PackageTypeTrspCatMapping;
import com.crh.logon.persistence.entities.common.FireqMaterialMixdesignEntity;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.entities.common.ShiptoBilltoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.common.ShsareaRestrTruckTypeEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeTxPK;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.utils.StringUtils;

/**
 * Class for the purpose to provide static functions to return the valid lists - valid delivery option configuration - for
 * a specified delivery setting. Given a specific delivery setting (i.e. a selection of all values of all dropdowns and other
 * inputs), this class determines the set of valid elements for each dropdown field. 
 * For example, if the selected delivery type is "road direct", then in the truck/wagon type selection field only truck types can be shown. 
 * 
 * @author AdNovum Informatik AG
 */
public class DeliverySettingFilterLogic implements Serializable 
{
	private static final long serialVersionUID = 1L;
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DeliverySettingFilterLogic.class);
	

	/**
	 * The method returns THE valid option configuration for the provided delivery setting.  
	 * 
	 * Returns a {@link DeliveryOptionConfigurationDTO} which contains all the valid delivery settings for the 
	 * provided {@link DeliverySettingDTO}. 
	 *   
	 */ 
	public static DeliveryOptionConfigurationDTO getDeliveryOptionConfiguration(final DeliverySetting setting, final DeliverySettingSalesAreaMap salesAreaMap) {
		// [[
		DeliveryOptionConfigurationDTO result = null;
		Object[][] params = {{"setting", setting}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DeliverySettingSalesAreaMap.class.getName(), "getDeliveryOptionConfiguration", params);
		}
		// ]]
		try {
			final MaterialAvailabilityBackgroundData materialAvailabilityBackgroundData = salesAreaMap.getMaterialAvailabilityBackgroundData();
			final DeliverySettingShiptoMap shiptoSettings = salesAreaMap.getShiptoSettings();
			final GlobalTransportBackgroundData globalTransportData = shiptoSettings.getGlobalTransportData();
			final ShiptoSalesAreaFinancialbackgroundData financialbackgroundData = salesAreaMap.getFinancialbackgroundData();
			
			final DeliveryOptionConfigurationDTO optionConfiguration = new DeliveryOptionConfigurationDTO();
			
			final List <PlantDTO> plantList = new ArrayList<PlantDTO>();			
			
			final List <PlantDTO> activeSareaProdPlantList = filterProductionPlantOptions(setting.getSapMaterialId(), materialAvailabilityBackgroundData);
			List<FireqMaterialMixdesignEntity> mixDesignList = new ArrayList<FireqMaterialMixdesignEntity>();
			
			if ( setting.getProductionPlantCd() != null ) {
				
				List <FireqMaterialMixdesignEntity> mixDesignForMaterial = materialAvailabilityBackgroundData.getMaterialMixDesignMap().get(setting.getSapMaterial().getSapMaterialId());
				if ( mixDesignForMaterial != null ) {
					for (FireqMaterialMixdesignEntity entity : mixDesignForMaterial ) {
						if (entity.getPlant().getPlantCd().equals(setting.getProductionPlantCd() )) {
							mixDesignList.add(entity);
						}
					}
				}
				optionConfiguration.setMaterialMixDesignList(mixDesignList);
				
			}
			
			// obtain and filter delivery type list
			List <DeliveryTypeTxEntity> deliveryTypes = new ArrayList<DeliveryTypeTxEntity>();
			final List<DeliveryTypeTxEntity> globalDeliveryTypeTxList = globalTransportData.getDeliveryTypeTxList();
			deliveryTypes = filterDeliveryTypesByShiptoType(shiptoSettings.getShipto().getShiptoType() != null ? shiptoSettings.getShipto().getShiptoType().getShiptoTypeCd() : null, globalDeliveryTypeTxList);
			filterDeliveryTypeListByMaterial(deliveryTypes, setting);
			
			Map<String, Set<Long>> incotermCdDeliveryTypeCdMap = salesAreaMap.getDeliveryTypesByIncotermMap();
			filterDeliveryTypeListByIncoterm(deliveryTypes, setting, incotermCdDeliveryTypeCdMap);
			optionConfiguration.setDeliveryTypeList(deliveryTypes);

			// obtain and filter UoM list
			List <UomTxEntity> uoms = new ArrayList<UomTxEntity>();
			if (setting.getSapMaterialId() != null) {
				final List <UomTxEntity> mabdUoms = materialAvailabilityBackgroundData.getMaterialUomTxListMap().get(setting.getSapMaterialId());
				if (mabdUoms != null && !mabdUoms.isEmpty()) {
					uoms.addAll(mabdUoms);
				}
			}
			filterUomListByMaterial(uoms, setting);
			filterUomListByDeliveryType(uoms, setting);			
			optionConfiguration.setUomList(uoms);
			
			/**
			 * If the material has a production plant defined, the only valid production plant
			 * is that one! In the meantime it still needs to be a valid production plant, has to be present in
			 * the normal list.
			 */
			if(setting.getSapMaterial().getProdPlant() != null){
				SapMaterialEntity sapMaterial = setting.getSapMaterial();
				PlantBsEntity productionPlant = sapMaterial.getProdPlant();
				String productionPlantCd = productionPlant.getPlantCd();
				for (PlantDTO plant : activeSareaProdPlantList) {
					String currentPlantCd = plant.getPlantCd();
					if (currentPlantCd.equals(productionPlantCd)) {
						plantList.add(plant);
						break;
					}
				}
			}
			else {
				plantList.addAll(activeSareaProdPlantList);
			}
			optionConfiguration.setProdPlantList(plantList);
			
			filterShippingpointSupplyPlantOptions(setting, optionConfiguration, materialAvailabilityBackgroundData);
			
			List<PoolTxEntity> validPoolList = filterTransportMode(setting.getDeliveryTypeCd(), globalTransportData);
			optionConfiguration.setPoolList(validPoolList);
	
			List <CommercialCarrierDTO> validCommercialCarrierList = filterForPoolSetting(setting, validPoolList, globalTransportData);
			optionConfiguration.setCommercialCarrierList(validCommercialCarrierList);

			List<ExecutingCarrierDTO> validExecutingCarrierList = filterForCommercialCarrierSetting(setting, validCommercialCarrierList, globalTransportData);
			optionConfiguration.setExecutingCarrierList(validExecutingCarrierList);

			List<LoadingPointDTO> filterValidLoadingPoints = filterValidLoadingPoints(setting, materialAvailabilityBackgroundData);
			optionConfiguration.setLoadingpointList(filterValidLoadingPoints);
			
			List <ShiptoSiloEntity> silos =  filterSilosForMaterial(materialAvailabilityBackgroundData, setting);
			optionConfiguration.setSiloList(silos);
			
			optionConfiguration.setDeliveryFreqEveryTimeUomMap(globalTransportData.getTimeUomMap());
			optionConfiguration.setDeliveryTimeToleranceUomMap(globalTransportData.getTimeUomMap());
			
			List <TruckWagonTypeWrapper> vehicleTypeWrappers = filterVehicleTypeWrappers(
					shiptoSettings, 
					setting.getSapMaterial(),
					setting.getDeliveryTypeCd());
			
			List<ShiptoBilltoEntity> validBilltos = new ArrayList<ShiptoBilltoEntity>();
			List<SapBpEntity> validPayerList = new ArrayList<SapBpEntity>();
			
			if (financialbackgroundData != null) {
				validBilltos = financialbackgroundData.getValidBilltos();
				validPayerList = financialbackgroundData.getValidPayerList();
			}
			
			optionConfiguration.setBilltoList(validBilltos);
			optionConfiguration.setPayerList(validPayerList);
			
			optionConfiguration.setVehicleTypeWrapperList(vehicleTypeWrappers);
			
			optionConfiguration.setServiceProcessIndicatorList(materialAvailabilityBackgroundData.getServiceProcessIndicatorList());
			
			return result = optionConfiguration;
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
	 * Returns the list of {link {@link TruckWagonTypeWrapper} wrappers depending on the delivery type code and the 
	 * type of the material.
	 * 
	 * @param setting
	 * @return
	 */
	public static List<TruckWagonTypeWrapper> filterVehicleTypeWrappers(DeliverySettingShiptoMap shiptoSettings, SapMaterialEntity sapMaterial, Long deliveryTypeCd) {
		// [[
		List<TruckWagonTypeWrapper> result = null;
		Object[][] params = {{"shiptoSettings", shiptoSettings},
							{"sapMaterial", sapMaterial},
							{"deliveryTypeCd", deliveryTypeCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DeliverySettingFilterLogic.class, "filterVehicleTypeWrappers", params);
		}
		// ]]
		try {
			LOG.info("Filtering vehicle type wrappers.");
			Long packageTypeCd = sapMaterial.getMaterialPackage().getLogonPackageType().getPackageTypeCd();
			/* marius/2012-10-11: Changed substantially for LOGON 2.1 as part of ticket LOGON-5043.
			 */
			List returnList;
			
			if (LogonUtil.isRailOrder(deliveryTypeCd)) {
				LOG.debug("This is a rail order.");
				returnList = filterOutDisabledTypes(shiptoSettings.getWagonTypeWrappers());
			}
			else if (LogonUtil.isTruckOrder(deliveryTypeCd)
					|| LogonUtil.isPickupOrder(deliveryTypeCd)
					|| LogonUtil.isReturnDeliveredOrder(deliveryTypeCd)) {
				LOG.debug("This is a truck or pickup order.");
				final List<TruckTypeWrapper> truckTypesFromShipto = shiptoSettings.getTruckTypeWrappers();
				Map<Long,Set<Long>> truckTypeToTrspCat = shiptoSettings.getGlobalTransportData().getTruckTypeToTrspCat();
				truckTypeToTrspCat = filterTruckTypeCategoryInCaseOfLandfill(truckTypeToTrspCat, sapMaterial);
				returnList = filterTruckTypesForPackageType(filterOutDisabledTypes(truckTypesFromShipto), packageTypeCd, truckTypeToTrspCat);
			}
			else if (LogonUtil.isShipOrder(deliveryTypeCd) || LogonUtil.isShipPickupOrder(deliveryTypeCd) ) {				
				LOG.debug("This is a ship order.");
				returnList = shiptoSettings.getShipTypeWrappers();
			}
			else if (LogonUtil.isBlockTrainOrder(deliveryTypeCd) || LogonUtil.isBlockTrainPickupOrder(deliveryTypeCd) ) {
				LOG.debug("This is a block-train order.");
				returnList = shiptoSettings.getBlockTrainTypeWrappers();
			}
			else {
				// Unknown order type - return an empty list of vehicles. Most harmless solution.
				LOG.warn("Could not determine allowed truck or wagon types for delivery type code '{}'.", new Object[] {deliveryTypeCd} );
				returnList = new LinkedList<TruckWagonTypeWrapper>();
			}
			
			return result = returnList; 
			
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
	 * Filters out the truckType categories in case of landfill material
	 * @param truckTypeToTrspCat
	 * @param sapMaterial
	 * @return
	 */
	private static Map<Long, Set<Long>> filterTruckTypeCategoryInCaseOfLandfill(
			Map<Long, Set<Long>> truckTypeToTransportCategories,
			SapMaterialEntity sapMaterial) {
		// [[
		Map<Long,Set<Long>> result = null;
		Object[][] params = {{"truckTypeToTransportCategories", truckTypeToTransportCategories},
							{"sapMaterial", sapMaterial}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DeliverySettingFilterLogic.class, "filterTruckTypeCategoryInCaseOfLandfill", params);
		}
		// ]]
		try {		
			if (!sapMaterial.getMaterialIsLf()){
				return result = truckTypeToTransportCategories;
			}
			else{
				Map<Long, Set<Long>> returnTruckTypeToTransportCategory = new HashMap<Long, Set<Long>>();
							
				for (Map.Entry<Long, Set<Long>> truckTypeToTransportCategory : truckTypeToTransportCategories.entrySet()) {
					Set<Long> transportCategories = truckTypeToTransportCategory.getValue();
					
					//LOGON-6857: landfill is bulk dumped package type
					if (transportCategories.contains(Constant.TRUCK_TRSP_CAT_BULK_DUMPED)
							|| transportCategories.contains(Constant.TRUCK_TRSP_CAT_ALL_MAT_PACKAGE_TYPES)){
						Long truckTypeId = truckTypeToTransportCategory.getKey();
						returnTruckTypeToTransportCategory.put(truckTypeId, transportCategories);
					}											   
				}				
				return result = returnTruckTypeToTransportCategory;
			}		
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
	 * Filters out disabled truck or wagon types.
	 * @param list A list of TruckTypeWrappers or WagonTypeWrappers
	 * @return only those elements in <code>list</code> whose priority is nonzero.
	 */
	static <T extends TruckWagonTypeWrapper> List<T> filterOutDisabledTypes(List<T> list)
	{
		// [[
		List<T> result = null;
		Object[][] params = {{"list", list}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DeliverySettingFilterLogic.class, "filterOutDisabledTypes", params);
		}
		// ]]
		try {
			final List<T> filtered = new LinkedList<T>();
			for (T w : list) {
				if (w.getPriority() != 0) {
					filtered.add(w);
				}
			}
			return result = filtered;
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
	 * Filters a list of truck types by retaining only those suitable to transport
	 * a certain type of package.
	 * The information to determine this comes from two places:
	 * <ol>
	 *   <li>Table 5 in the SDS for LRQ 185.0/185.1. This table specifies which 
	 *   are the allowed transport categories for a for a given package type.</li>
	 *   <li>The table <code>TLGNC_TRUCK_TYPE_TRSP_CAT</code>; it specifies which
	 *   transport category or categories a truck type "usually" has. "Usually" here
	 *   is because which actual truck transports the order will only be known at scheduling time,
	 *   so we can not be hundred percent sure about this.
	 * @param truckTypes a list of truck types
	 * @param packageTypeCd the package type of a material
	 * @param truckTypeToTrspCat specifies which are the supported transport categories of a truck type
	 * @return a new list containing only those elements from <code>truckTypes</code> that usually have a
	 * suitable transport category
	 */
	static List<TruckTypeWrapper> filterTruckTypesForPackageType(List<TruckTypeWrapper> truckTypes, Long packageTypeCd, Map<Long,Set<Long>> truckTypeToTrspCat) 
	{
		// [[
		List<TruckTypeWrapper> result = null;
		Object[][] params = {{"truckTypes", truckTypes},
							{"packageTypeCd", packageTypeCd},
							{"truckTypeToTrspCat", truckTypeToTrspCat}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DeliverySettingFilterLogic.class, "filterTruckTypesForPackageType", params);
		}
		// ]]
		try {
			LOG.debug("Filtering truck types suitable for package type with code '{}'", new Object[] {packageTypeCd});
			/* The following is simply list filtering, 
			 * with truckTypeSuitsPackageType() as the filter method.
			 * In Scala this would be simpler :-) 
			 */
			final List<TruckTypeWrapper> filteredList = new LinkedList<TruckTypeWrapper>();
			for (TruckTypeWrapper truckType : truckTypes)
			{
				if (truckTypeSuitsPackageType(truckType.getTypeCd(), packageTypeCd, truckTypeToTrspCat)) {
					filteredList.add(truckType);
				}
			}
			return result = filteredList;
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
	 * This method implements the filter for the truck type filtering; see
	 * the javadoc of <code>filterTruckTypesForPackageType</code>.
	 * @param truckType
	 * @param packageTypeCd
	 * @param truckTypeToTrspCat specifies which are the supported transport categories of a truck type
	 * @return true if the truck type is ok for the package type, false otherwise.
	 */
	static boolean truckTypeSuitsPackageType(Long truckType, Long packageTypeCd, Map<Long,Set<Long>> truckTypeToTrspCat)
	{
		// [[
		boolean result = false;
		Object[][] params = {{"truckType", truckType},
							{"packageTypeCd", packageTypeCd},
							{"truckTypeToTrspCat", truckTypeToTrspCat}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DeliverySettingFilterLogic.class, "truckTypeSuitsPackageType", params);
		}
		// ]]
		try {
			final Set<Long> allowedCategories = PackageTypeTrspCatMapping.getTruckTrspCatForPackageType(packageTypeCd);
			final Set<Long> supportedCategories;
			if (truckTypeToTrspCat.containsKey(truckType)) {
				supportedCategories = truckTypeToTrspCat.get(truckType);
			} else {
				supportedCategories = new HashSet<Long>();
			}
			
			if (supportedCategories.isEmpty()) {
				LOG.warn("No supported transport categories for truck type with code '{}'", new Object[] {truckType});
			}
			
			// Compute the intersection of required and supported categories
			// by removing all unsupported categories from requiredCategories.
			if (intersectionNotEmpty(supportedCategories, allowedCategories)) 
			{
				return result = true;
			}
			else 
			{
				return result = false;
			}
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
	 * Checks if the intersection of two sets is not empty
	 * @param a A set
	 * @param b Another set
	 * @return true if and only if there is at least one element that
	 * is both in <code>a</code> and in <code>b</code>. The test is done
	 * using equals(), so it is a weaker test than reference equality. 
	 */
	static <T extends Object> boolean intersectionNotEmpty(Set<T> a, Set<T> b)
	{
		for (T t : a) {
			if (b.contains(t)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * @param  truckTypeEntities - list of enabled truck types 
	 * @return a list of vehicle type codes if available from truckTypeEntities
	 */
	private static List<Long> collectVehicleTypeCds(List<ShsareaRestrTruckTypeEntity> truckTypeEntities) {
		// [[
		List<Long> result = null;
		Object[][] params = {{"truckTypeEntities", truckTypeEntities}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DeliverySettingFilterLogic.class, "collectVehicleTypeCds", params);
		}
		// ]]
		try {
			
			if (truckTypeEntities == null || truckTypeEntities.isEmpty()) {
				result = Collections.<Long>emptyList();
			} else {
				result = new ArrayList<Long>();
				
				for (ShsareaRestrTruckTypeEntity entity : truckTypeEntities) {
					TruckTypeBsEntity truckType = entity.getTruckType();
					if (truckType != null) {
						result.add(truckType.getTruckTypeCd());
					}
				}
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
	  * Returns the carriers available for the provided pool list.
	  * @param setting
	  * @param validPoolList
	 * @param globalTransportBackgroundData 
	  * @return
	  */
	 private static List<CommercialCarrierDTO> filterForPoolSetting(DeliverySetting setting,
			 List<PoolTxEntity> validPoolList, GlobalTransportBackgroundData globalTransportBackgroundData) {
		 
		 Map <Long, List<CommercialCarrierDTO>> poolCarrierMap = globalTransportBackgroundData.getPoolCarrierMap();
		 if ( setting.getPoolId() != null ) {			
			final List<CommercialCarrierDTO> carrierList = poolCarrierMap.get(setting.getPoolId());
			return carrierList != null ? carrierList : new ArrayList();
		 }
		 else {
			 return new ArrayList();
		 }
	 }

	/**
	* Returns the executing carriers available for Commercial Carrier Id of the provided {@code DeliverySetting}.
	* If delivery type is rail returns empty list
	* @param setting - {@code DeliverySetting} with information on selection
	* @param validCommercialCarrierList - the settings Commercial Carrier Id is only valid if contained in this list
	* @param globalTransportBackgroundData - contains all previously fetched transportation data
	* @return list of Executing Carriers for the Commercial Carrier selected in the {@code DeliverySetting}
	*/
	private static List<ExecutingCarrierDTO> filterForCommercialCarrierSetting(
			DeliverySetting setting,
			List<CommercialCarrierDTO> validCommercialCarrierList,
			GlobalTransportBackgroundData globalTransportBackgroundData) {

		Long deliveryTypeCd = setting.getDeliveryTypeCd();
		if (LogonUtil.isRailOrder(deliveryTypeCd)) {
			return Collections.<ExecutingCarrierDTO>emptyList();
		}

		Long commercialCarrierId = setting.getCommercialCarrierId();
		if (commercialCarrierId == null) {
			return Collections.<ExecutingCarrierDTO>emptyList();
		}

		boolean validCommercialCarrier = false;
		for (CommercialCarrierDTO commercialCarrier : validCommercialCarrierList) {
			if (commercialCarrier.getCarrierId().equals(commercialCarrierId)) {
				validCommercialCarrier = true;
				break;
			}
		}

		if (!validCommercialCarrier) {
			return Collections.<ExecutingCarrierDTO>emptyList();
		}

		Map<Long, List<ExecutingCarrierDTO>> commercialExecutingCarrierMap = globalTransportBackgroundData.getCommercialExecutingCarrierMap();
		List<ExecutingCarrierDTO> foundExecutingCarriers = commercialExecutingCarrierMap.get(commercialCarrierId);
		if (foundExecutingCarriers == null) {
			return Collections.<ExecutingCarrierDTO>emptyList();
		}
		else {
			return foundExecutingCarriers;
		}

	}

	/**
	  * Filters the list of pools according to transportation mode, and returns a set of valid pools.
	 * @param deliveryTypeCd
	 * @param globalTransportBanckgroundData
	 * @param pools
	  * @return
	  */
	 private static List<PoolTxEntity> filterTransportMode(Long deliveryTypeCd, GlobalTransportBackgroundData globalTransportBackgroundData) {
		 
		 if ( deliveryTypeCd == null || deliveryTypeCd.equals(Long.valueOf(Constant.DROPDOWN_NOT_SELECTED))) {
			 return Collections.emptyList();
		 }
		 
		 List<DeliveryTypeTxEntity> deliveryTypeTxList = globalTransportBackgroundData.getDeliveryTypeTxList();
		 
		 DeliveryTypeBsEntity deliveryTypeBs = null;
		 for (DeliveryTypeTxEntity deliveryTypeTx : deliveryTypeTxList) {
			 DeliveryTypeTxPK deliveryTypeTxPk = deliveryTypeTx.getDeliveryTypeTxPK();
			 deliveryTypeBs = deliveryTypeTxPk.getDeliveryTypeBs();
			 if (deliveryTypeBs.getDeliveryTypeCd().equals(deliveryTypeCd)) {
				 break;
			 }
		 }
		 
		 String matchTransportMode = deliveryTypeBs.getSapTrspMode().getSapTrspModeCd();
		 
		 List<PoolTxEntity> validPoolTx = new ArrayList<PoolTxEntity>();
		 List<PoolDTO> poolList = globalTransportBackgroundData.getPools();
		 for(PoolDTO poolDTO : poolList) {
			 String sapTransportModeCd = poolDTO.getSapTrspModeCd();
			 if (sapTransportModeCd.equals(matchTransportMode)) {
				Long poolId = poolDTO.getPoolId();
				PoolTxEntity pool = globalTransportBackgroundData.getPoolTxMap().get(poolId);
				validPoolTx.add(pool);
			 }
		 }
		 
		 return validPoolTx;
	 }

	/**
	 * Filtering the loadingpoints according to material restrictions, and the selected shippingpoint.
	 * Step 1: in case shippingpoint is set, only loadingpoint for that shippingpoint are valid
	 * Step 2: from the list defined in step 1 only those are valid, which have a loadpoint "restriction" enabled. 
	 * @param setting
	 * @param materialAvailability 
	 * @return
	 */
	private static List<LoadingPointDTO> filterValidLoadingPoints(DeliverySetting setting, MaterialAvailabilityBackgroundData materialAvailability) {
		Long shippingpointId = setting.getShippingpointId(); 
		Map<Long, List<LoadingPointDTO>> shippingpointLoadingpointMap = materialAvailability.getShippingPointLoadingPointMap();
		List<LoadingPointDTO> shippingpointLoadingpointList;
		if ( shippingpointId != null ) {
			shippingpointLoadingpointList =  shippingpointLoadingpointMap.get(shippingpointId); 
			if ( shippingpointLoadingpointList == null ) {
				shippingpointLoadingpointList = new ArrayList<LoadingPointDTO>();
			}
		}
		else {
			shippingpointLoadingpointList = new ArrayList<LoadingPointDTO>();
			for (List<LoadingPointDTO> loadingpointList : shippingpointLoadingpointMap.values()) {
				shippingpointLoadingpointList.addAll(loadingpointList);
			}
		}
	
		List <LoadingPointDTO> result = DeliverySettingFilterLogic.filterLoadingpointsForMaterial(setting, shippingpointLoadingpointList, materialAvailability);
	
		return result;	
	}

	/**
	 * Filter the loadingpoints with filtering for loadingpoint restrictions. 
	 * loadingpoints are valid.
	 * @param setting
	 * @param loadingPointList
	 * @param materialAvailability 
	 * @return
	 */
	private  static List <LoadingPointDTO> filterLoadingpointsForMaterial(DeliverySetting setting,
			List<LoadingPointDTO> loadingPointList, MaterialAvailabilityBackgroundData materialAvailability) {
		Long materialPackageCd = setting.getSapMaterial().getMaterialPackage().getLogonPackageType().getPackageTypeCd();
		/**
		 * Keep only those loading points which do provide the certain material.
		 */
		List<LoadingPointDTO> result = new ArrayList<LoadingPointDTO>();
		Set <Long> lpForRestr = materialAvailability.getMaterialLoadingpointMap().get(setting.getSapMaterial().getSapMaterialId());
		
		if ( lpForRestr == null ) {
			return result;
		}
		else {
			for (LoadingPointDTO loadingpoint : loadingPointList) {
				Long packageTypeCd = loadingpoint.getPackageTypeCd();
				if (packageTypeCd.equals(materialPackageCd) && lpForRestr.contains( loadingpoint.getLoadingpointId() )) {
					result.add(loadingpoint);
				}
			}
		}
		return result;
	}

	/**
	 * Filters the shippingpoint according to the settings provided.
	 * @param setting
	 * @param optionConfiguration
	 * @param materialAvailability 
	 */
	private static void filterShippingpointSupplyPlantOptions(DeliverySetting setting, DeliveryOptionConfigurationDTO optionConfiguration, MaterialAvailabilityBackgroundData materialAvailability) {
		List<ShippingpointDTO> plantShippingpointList = new ArrayList();
		List <SupplyPlantDTO> supplyPlants = new ArrayList<SupplyPlantDTO>();
		Long delTypeCd = setting.getDeliveryTypeCd();
	
		final List <PlantDTO> plants = materialAvailability.getMaterialPlantListMap().get(setting.getSapMaterialId());
		if ( plants != null ) {
			Set<String> activeSareaPlantCdsForMaterial = new HashSet<String>();
			for(PlantDTO plant : plants) {
				String plantCd = plant.getPlantCd();
				activeSareaPlantCdsForMaterial.add(plantCd);
			}
			
			materialAvailability.getPlantShippingPointMap();
			Map<String, List<ShippingpointDTO>> plantShippingpointMap = materialAvailability.getPlantShippingPointMap();
			Set<ShippingpointDTO> shippingpointSet = new HashSet<ShippingpointDTO>();
			for (List<ShippingpointDTO> shippingpointList : plantShippingpointMap.values()) {
				shippingpointSet.addAll(shippingpointList);
			}
			Boolean supplyPlantCdInSettingIsEmpty =StringUtils.empty(setting.getSupplyPlantCd());
			
			for (ShippingpointDTO shippingpoint : shippingpointSet) {
				String plantCd = shippingpoint.getPlantCd();
				if ( activeSareaPlantCdsForMaterial.contains(plantCd)
						&& DeliverySettingFilterLogic.matchShippingpointDeliveryType(shippingpoint.getShipppointTypeCd(), delTypeCd)) {
						if (supplyPlantCdInSettingIsEmpty || plantCd.equals(setting.getSupplyPlantCd())) {
							plantShippingpointList.add(shippingpoint);
						}
						
						SupplyPlantDTO supplyPlant = new SupplyPlantDTO(shippingpoint.getPlantCd(), shippingpoint.getPlantName());
						if (!supplyPlants.contains(supplyPlant)){
							supplyPlants.add(supplyPlant);
						}
				}
			}
			
		}
	
		optionConfiguration.setShippingpoinList(plantShippingpointList);
		optionConfiguration.setSupplyPlantList(supplyPlants);
	}

	/**
	 * Filters the shippingpoint according to the settings provided.
	 * @param setting
	 * @param materialAvailability 
	 * @param optionConfiguration
	 */
	static List<PlantDTO> filterProductionPlantOptions(Long sapMaterialId, MaterialAvailabilityBackgroundData materialAvailability) {
		final List <PlantDTO> plants = materialAvailability.getMaterialPlantListMap().get(sapMaterialId);
		if ( plants != null ) {			
			final List<PlantDTO> productionPlants = new ArrayList<PlantDTO>();
			for ( PlantDTO plant : plants ) {
				if ( plant.getProductionPlant() ) {					
					productionPlants.add(plant);
				}
			}
			return productionPlants;
		}
		else {
			return Collections.<PlantDTO>emptyList();
		}
	}

	/**
	 * Checks if a shippingpoint matches the delivery type code, and is possible to use in 
	 * the provided delivery type context.
	 * @param shippingpoint
	 * @param deliveryTypeCd
	 * @return
	 */
	public static boolean matchShippingpointDeliveryType(final Long shippingTypeCd, Long deliveryTypeCd) {

		if ( deliveryTypeCd == null || deliveryTypeCd.equals(Long.valueOf(Constant.DROPDOWN_NOT_SELECTED))) {
			//in case not set, all shippingpoints are considered valid
			return true;
		}
		
		return ((deliveryTypeCd == Constant.DELIVERY_TYPE_ROAD_DIRECT && shippingTypeCd == Constant.SHIPPINGPOINT_TYPE_ROAD)
				|| (deliveryTypeCd == Constant.DELIVERY_TYPE_RAIL_DIRECT && shippingTypeCd == Constant.SHIPPINGPOINT_TYPE_RAIL)
				|| (deliveryTypeCd == Constant.DELIVERY_TYPE_BIMODAL_RAIL && shippingTypeCd == Constant.SHIPPINGPOINT_TYPE_RAIL)
				|| (deliveryTypeCd == Constant.DELIVERY_TYPE_BIMODAL_ROAD && shippingTypeCd == Constant.SHIPPINGPOINT_TYPE_FVL_BIMODAL_TRUCK)
				|| (deliveryTypeCd != Constant.DELIVERY_TYPE_BIMODAL_ROAD && shippingTypeCd == Constant.SHIPPINGPOINT_TYPE_CONTAINER)	// TODO clarify
				|| (deliveryTypeCd == Constant.DELIVERY_TYPE_PICKUP)
				|| (deliveryTypeCd == Constant.DELIVERY_TYPE_SERVICE)	// service only materials
				|| (deliveryTypeCd == Constant.DELIVERY_TYPE_RETURN_PICKUP)
				|| (deliveryTypeCd == Constant.DELIVERY_TYPE_RETURN_DELIVERED)
				|| ((deliveryTypeCd == Constant.DELIVERY_TYPE_SHIP	// all types of simple vehicles 
					|| deliveryTypeCd == Constant.DELIVERY_TYPE_BLOCK_TRAIN
					|| deliveryTypeCd == Constant.DELIVERY_TYPE_BLOCKTRAIN_PICKUP
					|| deliveryTypeCd == Constant.DELIVERY_TYPE_SHIP_PICKUP)
					&& shippingTypeCd == Constant.SHIPPINGPOINT_TYPE_SIMPLE_VEHICLE
				)
		);
	}

	private static  List<DeliveryTypeTxEntity>filterDeliveryTypesByShiptoType(Long shipToType, List<DeliveryTypeTxEntity> globalDeliveryTypeTxList) {
		if ( shipToType != null ) {				
			// filter delivery types according to the selected shiptotype
			ListIterator dtIt = globalDeliveryTypeTxList.listIterator();
			List deliveryTypeList = new ArrayList();
			while (dtIt.hasNext()) {
				DeliveryTypeTxEntity dtTxEntity = (DeliveryTypeTxEntity) dtIt.next();
				long typeCd = dtTxEntity.getDeliveryTypeTxPK().getDeliveryTypeBs().getDeliveryTypeCd().longValue();
				if ( typeCd == Constant.DELIVERY_TYPE_SHIP ||  typeCd == Constant.DELIVERY_TYPE_BLOCK_TRAIN || 
					 typeCd == Constant.DELIVERY_TYPE_BLOCKTRAIN_PICKUP || typeCd == Constant.DELIVERY_TYPE_SHIP_PICKUP) {
					deliveryTypeList.add(dtTxEntity);					
				}
				else if ( Constant.DELIVERY_TYPE_PICKUP == typeCd &&
						// NOTE: ADN52225: pickup is selectable in the restriction even is the "Pickup possible" 
						// checkbox is unchecked. Order Taking will just ignore the value anyway in these cases. 
						//shipTo.getSelfServiceFlag() && 
						(Constant.SHIPTO_TYPE_STANDARD  == shipToType || Constant.SHIPTO_TYPE_PLANT == shipToType)) {
					deliveryTypeList.add(dtTxEntity);
				}
				else if ( Constant.DELIVERY_TYPE_ROAD_DIRECT == typeCd &&
						(Constant.SHIPTO_TYPE_STANDARD  == shipToType || Constant.SHIPTO_TYPE_CONTAINER_TERMINAL  == shipToType || Constant.SHIPTO_TYPE_PLANT == shipToType)) {
					deliveryTypeList.add(dtTxEntity);
				}
				else if ( Constant.DELIVERY_TYPE_BIMODAL_ROAD == typeCd && 
						Constant.SHIPTO_TYPE_STANDARD  == shipToType || Constant.SHIPTO_TYPE_PLANT == shipToType) {
					deliveryTypeList.add(dtTxEntity);
				}
				else if ( Constant.DELIVERY_TYPE_BIMODAL_RAIL == typeCd && 
						Constant.SHIPTO_TYPE_FVL  == shipToType) {
					deliveryTypeList.add(dtTxEntity);
				}
				else if ( Constant.DELIVERY_TYPE_RAIL_DIRECT == typeCd &&
						(Constant.SHIPTO_TYPE_STANDARD  == shipToType || Constant.SHIPTO_TYPE_CONTAINER_TERMINAL  == shipToType || Constant.SHIPTO_TYPE_MAINTENANCE == shipToType || Constant.SHIPTO_TYPE_PLANT == shipToType) ) {
					deliveryTypeList.add(dtTxEntity);
				}
				else if (Constant.SHIPTO_TYPE_LOCATION_NOT_FIXED  == shipToType) {
					deliveryTypeList.add(dtTxEntity);
				}
				else if (Constant.DELIVERY_TYPE_SERVICE == typeCd) {
					deliveryTypeList.add(dtTxEntity);
				}
				// TODO (20130426/lajos): adapt logic for landfill (LRQ 075.0)
				else if (LogonUtil.isReturnPalletOnly(typeCd)
						&& (Constant.SHIPTO_TYPE_STANDARD == shipToType || Constant.SHIPTO_TYPE_PLANT == shipToType)) {
					deliveryTypeList.add(dtTxEntity);
				} 
			}
			return deliveryTypeList;
		}
		else {
			return globalDeliveryTypeTxList;
		}
	}
	
	
	private static final void filterDeliveryTypeListByMaterial(final List<DeliveryTypeTxEntity> deliveryTypes, final DeliverySetting setting) {
		// [[
		Object[][] params = {{"deliveryTypes", deliveryTypes},
							{"setting", setting}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DeliverySettingFilterLogic.class, "filterDeliveryTypeListByMaterial", params);
		}
		// ]]
		try {
			if (setting == null || setting.getSapMaterial() == null || deliveryTypes == null || deliveryTypes.isEmpty()) {
				return;
			}
			final SapMaterialEntity material = setting.getSapMaterial();
			
			final boolean isServiceMaterial = LogonPackageHandlingUtil.isService(material);
			final boolean isPalletMaterial = LogonPackageHandlingUtil.isPallet(material);
			final boolean isLandfillMaterial = LogonUtil.isLandfill(material);
			final boolean isPlanningMaterial = LogonUtil.isPlanningMaterial(material);
			
			final Iterator<DeliveryTypeTxEntity> deliveryTypeIterator = deliveryTypes.iterator();
			while (deliveryTypeIterator.hasNext()) {
				final DeliveryTypeTxEntity deliveryTypeTx = deliveryTypeIterator.next();
				final DeliveryTypeBsEntity deliveryTypeBs = deliveryTypeTx.getDeliveryTypeTxPK().getDeliveryTypeBs();
				final Long deliveryTypeCd = deliveryTypeBs.getDeliveryTypeCd();
				final boolean isServiceOnlyOrder = LogonUtil.isServiceOnlyOrder(deliveryTypeCd);
				final boolean isReturnOrder = LogonUtil.isReturnPalletOnly(deliveryTypeCd);
				final boolean isBlocktrainPickupOrShipPickup = LogonUtil.isBlockTrainPickupOrder(deliveryTypeCd) || LogonUtil.isShipPickupOrder(deliveryTypeCd);			
				
				if (isLandfillMaterial || isPlanningMaterial) {
					boolean isLandfillOrPlanningMaterialCompatibleDeliveryType = 
							LogonUtil.isRoadDirectOrder(deliveryTypeCd) || LogonUtil.isPickupOrder(deliveryTypeCd);
					if (!isLandfillOrPlanningMaterialCompatibleDeliveryType) {
						deliveryTypeIterator.remove();
					}
					continue;
				}
				
				if (isBlocktrainPickupOrShipPickup){
					continue;
				}
				
				if (isServiceOnlyOrder && !isServiceMaterial) {
					deliveryTypeIterator.remove();
					break;
				}
				
				if (isReturnOrder && !isPalletMaterial && !isServiceMaterial) {
					deliveryTypeIterator.remove();
					continue;
				}
			}
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	private static final void filterDeliveryTypeListByIncoterm (final List<DeliveryTypeTxEntity> deliveryTypes
			, final DeliverySetting setting, final Map<String, Set<Long>> incotermDeliveryTypeMap) {
		// [[
		Object[][] params = {{"deliveryTypes", deliveryTypes},
							{"setting", setting},
							{"incotermDeliveryTypeMap", incotermDeliveryTypeMap}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DeliverySettingFilterLogic.class, "filterDeliveryTypeListByIncoterm", params);
		}
		// ]]
		try {
			if (deliveryTypes == null || deliveryTypes.isEmpty() || setting == null || setting.getSapContractItem() == null
					|| incotermDeliveryTypeMap == null || incotermDeliveryTypeMap.isEmpty()) {
				return;
			}
			
			final SapContractEntity sapContract = setting.getSapContractItem().getSapContract();
			if (sapContract == null) {
				return;
			}
			
			final String incotermCd = sapContract.getIncotermsCd();		// empty incoterm means all delivery types are possible, see LOGON-6305
			if (incotermCd == null) {
				return;
			}
			
			final Set<Long> validDeliveryTypeCdList = incotermDeliveryTypeMap.get(incotermCd);
			if (validDeliveryTypeCdList == null || validDeliveryTypeCdList.isEmpty()) {
				deliveryTypes.clear();	// there are no valid delivery types for this incoterm
			}
			
			final Iterator<DeliveryTypeTxEntity> deliveryTypeIterator = deliveryTypes.iterator();
			while (deliveryTypeIterator.hasNext()) {
				final DeliveryTypeTxEntity deliveryTypeTx = deliveryTypeIterator.next();
				final DeliveryTypeBsEntity deliveryTypeBs = deliveryTypeTx.getDeliveryTypeTxPK().getDeliveryTypeBs();
				final Long deliveryTypeCd = deliveryTypeBs.getDeliveryTypeCd();
				
				// no incoterm check for return orders, see LRQ 018.2 / LOGON-6761
				if (LogonUtil.isReturnPalletOnly(deliveryTypeCd)) {
					continue;
				}
				
				if (!validDeliveryTypeCdList.contains(deliveryTypeCd)) {
					deliveryTypeIterator.remove();
				}
			}
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	private static void filterUomListByMaterial(final List<UomTxEntity> uoms, final DeliverySetting setting) {
		final SapMaterialEntity sapMaterial = setting.getSapMaterial();
		if (sapMaterial == null || uoms == null || uoms.isEmpty()) {
			return;
		}
		
		final boolean isBulk = LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(sapMaterial);
		final boolean isPlanning = LogonUtil.isPlanningMaterial(sapMaterial);

		final Iterator<UomTxEntity> uomIterator = uoms.iterator();
		while (uomIterator.hasNext()) {
			final UomTxEntity uomTx = uomIterator.next();
			final UomBsEntity uomBs = uomTx.getUomTxPK().getUomBs();
			final boolean isUomFuzzy = uomBs.getFuzzyQuantityFlag() == null ? false : uomBs.getFuzzyQuantityFlag().booleanValue();

			if (isPlanning && !Constant.UOM_LKW.equals(uomBs.getUomCd())) {
				uomIterator.remove();
				continue;
			}
			else if (isUomFuzzy && !isBulk) {
				uomIterator.remove();
				continue;
			}
		}
	}
	
	private static void filterUomListByDeliveryType(final List<UomTxEntity> uoms, final DeliverySetting setting) {
		// [[
		Object[][] params = {{"uoms", uoms},
							{"setting", setting}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DeliverySettingFilterLogic.class, "filterUomListByDeliveryType", params);
		}
		// ]]
		try {
			final Long deliveryTypeCd = setting.getDeliveryTypeCd();
			if (deliveryTypeCd == null || uoms == null || uoms.isEmpty()) {
				return;
			}
			
			final Iterator<UomTxEntity> uomIterator = uoms.iterator();
			while (uomIterator.hasNext()) {
				final UomTxEntity uomTx = uomIterator.next();
				final UomBsEntity uomBs = uomTx.getUomTxPK().getUomBs();
				final String uomCd = uomBs.getUomCd();
				final boolean isUomFuzzy = uomBs.getFuzzyQuantityFlag() == null ? false : uomBs.getFuzzyQuantityFlag().booleanValue();
				
				if (Constant.UOM_LKW.equals(uomCd)
						&& !(LogonUtil.isRoadDirectOrder(deliveryTypeCd) 
						 	|| LogonUtil.isPickupOrder(deliveryTypeCd) 
							|| LogonUtil.isShipPickupOrder(deliveryTypeCd) 
							|| LogonUtil.isBlockTrainPickupOrder(deliveryTypeCd) 
							|| LogonUtil.isBimodalTruckPartOrder(deliveryTypeCd))) {
							uomIterator.remove();
							continue;
				}
				else if (Constant.UOM_WAGON.equals(uomCd)
						&& !(LogonUtil.isRailOrder(deliveryTypeCd) 
								|| LogonUtil.isBlockTrainOrder(deliveryTypeCd) 
								|| LogonUtil.isShipPickupOrder(deliveryTypeCd) 
								|| LogonUtil.isBlockTrainPickupOrder(deliveryTypeCd))) {
							uomIterator.remove();
							continue;
				}
				else if (Constant.UOM_CONTAINER.equals(uomCd)
						&& (LogonUtil.isPickupOrder(deliveryTypeCd) 								
								|| LogonUtil.isShipPickupOrder(deliveryTypeCd) 
								|| LogonUtil.isBlockTrainPickupOrder(deliveryTypeCd) 
								|| LogonUtil.isBimodalRailPartOrder(deliveryTypeCd))) {
							uomIterator.remove();
							continue;
				}
				else if (isUomFuzzy && LogonUtil.isSimpleVehicleOrder(deliveryTypeCd)) {
					uomIterator.remove();
					continue;
				}
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	

	/**
	 * Filter out those silos, which do contain something, and not the material of the setting.
	 * @param validSilos
	 * @param setting
	 * @return
	 */
	private static List<ShiptoSiloEntity> filterSilosForMaterial(MaterialAvailabilityBackgroundData materialAvailabilityBackgroundData, DeliverySetting setting) {
		List<ShiptoSiloEntity> validSilos = materialAvailabilityBackgroundData.getValidSilos();
		
		List<ShiptoSiloEntity> silos  = new ArrayList();
		silos.addAll(validSilos);
		Iterator siloit = silos.iterator();
		while ( siloit.hasNext() ) {			
			ShiptoSiloEntity siloent = (ShiptoSiloEntity) siloit.next();
			if ( siloent.getSapMaterial() != null
					&& !siloent.getSapMaterial().getSapMaterialId().equals(setting.getSapMaterialId())) {
				siloit.remove();
			}
		}
	
		return silos;
	}


	
}
