/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ordercreation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.ordercreation.dto.DeliverySetting;
import com.crh.logon.business.ordercreation.dto.LoadingPointDTO;
import com.crh.logon.business.ordercreation.dto.PlantDTO;
import com.crh.logon.business.ordercreation.dto.SupplyPlantDTO;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.ordercreation.dto.AbstractDeliverySetting;
import com.crh.logon.business.ordercreation.dto.CommercialCarrierDTO;
import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.business.ordercreation.dto.ShippingpointDTO;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.ShiptoSalesAreaError;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.entities.common.ShiptoBilltoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeTxPK;
import com.crh.logon.persistence.entities.refdata.MaterialPackageBsEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;

public class DeliverySettingSalesAreaMap extends InvalidMapSupport implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DeliverySettingSalesAreaMap.class);

	/**
	 * The shiptoSarea for which the object is initialized for.
	 */
	private final ShiptoSareaEntity shiptoSarea;
	
	/**
	 * "Parent" object.
	 */
	private DeliverySettingShiptoMap shiptoSettings;
	
	/**
	 * Object which is initialized according to the caller, it's purpose is to know all materials valid for the caller.
	 */
	private MaterialSelectionInitializer materialSelectionInitializer;

	/**
	 * Object which is basically containing all financial background data for the shipto sales area, and is able to 
	 * return the valid financial lists for the sales area. It is used when checking the delivery settings
	 * to be valid against these
	 */
	private ShiptoSalesAreaFinancialbackgroundData financialbackgroundData;
	
	/**
	 * Object which is basically containing all background data related to loading of materials determined in the {@link #materialSelectionInitializer}
	 * (where a certain material can be loaded (delivery plant, shippingpoint, etc.))
	 */
	private MaterialAvailabilityBackgroundData materialAvailabilityBackgroundData;
	

	private DeliverySettingProductMap serviceDeliverySettings;
	private DeliverySettingProductMap materialDeliverySettings;
	private DeliverySettingProductMap serviceRestrictionDeliverySettings;
	private DeliverySettingProductMap materialRestrictionDeliverySettings;
	
	// maps incoterms and incoterms to possible delivery types
	private Map <String, Set<Long>> deliveryTypesByIncotermMap;
	
	public DeliverySettingSalesAreaMap(ShiptoSareaEntity shiptoSarea) {
		this.shiptoSarea = shiptoSarea;
	}
	
	public DeliverySettingSalesAreaMap(ShiptoSareaEntity shiptoSarea,
			DeliverySettingShiptoMap shiptoMap) {
		this.shiptoSarea = shiptoSarea;
		this.shiptoSettings = shiptoMap;
	}

	public Map<String, Set<Long>> getDeliveryTypesByIncotermMap() {
		return deliveryTypesByIncotermMap;
	}
	
	public void setDeliveryTypesByIncotermMap(Map<String, Set<Long>> sapIncotermCdDeliveryTypeCdMap) {
		this.deliveryTypesByIncotermMap = sapIncotermCdDeliveryTypeCdMap;
	}
	
	public DeliverySettingProductMap getServiceRestrictionDeliverySettings() {
		return serviceRestrictionDeliverySettings;
	}

	public void setServiceRestrictionDeliverySettings(
			DeliverySettingProductMap serviceRestrictionDeliverySettings) {
		this.serviceRestrictionDeliverySettings = serviceRestrictionDeliverySettings;
	}

	public DeliverySettingProductMap getMaterialRestrictionDeliverySettings() {
		return materialRestrictionDeliverySettings;
	}

	public void setMaterialRestrictionDeliverySettings(
			DeliverySettingProductMap materialRestrictionDeliverySettings) {
		this.materialRestrictionDeliverySettings = materialRestrictionDeliverySettings;
	}

	public DeliverySettingProductMap getServiceDeliverySettings() {
		return serviceDeliverySettings;
	}

	public void setServiceDeliverySettings(DeliverySettingProductMap serviceDeliverySettings) {
		this.serviceDeliverySettings = serviceDeliverySettings;
	}

	public DeliverySettingProductMap getMaterialDeliverySettings() {
		return materialDeliverySettings;
	}

	public void setMaterialDeliverySettings(DeliverySettingProductMap materialDeliverySettings) {
		this.materialDeliverySettings = materialDeliverySettings;
	}
	
    public void setMaterialSelectionInitializer(MaterialSelectionInitializer materialSelectionInitializer) {
		this.materialSelectionInitializer = materialSelectionInitializer;
	}

	public void setFinancialbackgroundData(ShiptoSalesAreaFinancialbackgroundData financialbackgroundData) {
		this.financialbackgroundData = financialbackgroundData;
	}

	public void setMaterialAvailabilityBackgroundData(MaterialAvailabilityBackgroundData materialAvailabilityBackgroundData) {
		this.materialAvailabilityBackgroundData = materialAvailabilityBackgroundData;
	}

	/**
	 * Returns a complete list of {@link DeliverySettingDTO} objects which contains MATERIAL delivery settings
	 * which are there according to SAP data, and also those delivery settings, which are still valid
	 * against the background SAP data. If restrictions are loaded, the basic MATERIAL delivery settings
	 * are replaced by restriction delivery settings.
	 * 
	 * @return
	 */
	public List<DeliverySettingDTO> getMaterialDeliverySettingList() {
		List<DeliverySettingDTO> allRestrictionDeliverySettings = new ArrayList<DeliverySettingDTO>();
		allRestrictionDeliverySettings.addAll(materialRestrictionDeliverySettings.getPossibleDeliverySettings());
		allRestrictionDeliverySettings.addAll(materialDeliverySettings.getPossibleDeliverySettings());
		return allRestrictionDeliverySettings;
	}

	/**
	 * Returns a complete list of {@link DeliverySettingDTO} objects which contains SERVICE delivery settings
	 * which are there according to SAP data, and also those delivery settings, which are still valid
	 * against the background SAP data. If restrictions are loaded, the basic SERVICE delivery settings
	 * are replaced by restriction delivery settings.
	 * 
	 * @return
	 */
	public List<DeliverySettingDTO> getServiceDeliverySettingList() {
		List<DeliverySettingDTO> allRestrictionDeliverySettings = new ArrayList<DeliverySettingDTO>();
		allRestrictionDeliverySettings.addAll(serviceDeliverySettings.getPossibleDeliverySettings());
		allRestrictionDeliverySettings.addAll(serviceRestrictionDeliverySettings.getPossibleDeliverySettings());
		return allRestrictionDeliverySettings;
	}
	
	/**
	 * Returns a complete list of {@link DeliverySettingDTO} objects which contains SERVICE AND MATERIAL delivery settings
	 * which are there according to SAP data, and also those delivery settings, which are still valid
	 * against the background SAP data. If restrictions are loaded, the basic SERVICE AND MATERIAL  delivery settings
	 * are replaced by restriction delivery settings.
	 * 
	 * @return
	 */
	public List<DeliverySettingDTO> getMaterialAndServiceDeliverySettings() {
		List<DeliverySettingDTO> allRestrictionDeliverySettings = new ArrayList<DeliverySettingDTO>();
		allRestrictionDeliverySettings.addAll(getServiceDeliverySettingList());
		allRestrictionDeliverySettings.addAll(getMaterialDeliverySettingList());
		return allRestrictionDeliverySettings;
	}
	
	/**
	 * Returns a complete list of {@link DeliverySettingDTO} objects which contains SERVICE AND MATERIAL delivery settings
	 * which are there according to SAP data.
	 * 
	 * @return
	 */
	public List<DeliverySettingDTO> getBasicDeliverySettings() {
		List<DeliverySettingDTO> allRestrictionDeliverySettings = new ArrayList<DeliverySettingDTO>();
		allRestrictionDeliverySettings.addAll(serviceDeliverySettings.getPossibleDeliverySettings());
		allRestrictionDeliverySettings.addAll(materialDeliverySettings.getPossibleDeliverySettings());
		return allRestrictionDeliverySettings;
	}
	
	/**
	 * Returns a complete list of {@link DeliverySettingDTO} objects which contains SERVICE AND MATERIAL delivery settings
	 * which are formed from {@link ShiptoSareaRestrEntity} objects, but only contains those, which are still valid
	 * against the SAP background data.
	 * 
	 * @return
	 */
	public List<DeliverySettingDTO> getRestrictionDeliverySettings() {
		List<DeliverySettingDTO> allRestrictionDeliverySettings = new ArrayList<DeliverySettingDTO>();
		allRestrictionDeliverySettings.addAll(serviceRestrictionDeliverySettings.getPossibleDeliverySettings());
		allRestrictionDeliverySettings.addAll(materialRestrictionDeliverySettings.getPossibleDeliverySettings());
		return allRestrictionDeliverySettings;
	}

	/**
	 * Checks the validity of Shipto/Soldo-Sales Area related data. Errors are returned in a map by error type.
	 * Everything what is initialized ONLY with parameters shipto sap bp + sarea id + soldto are supposed tobe checked here.
	 * Reason 1: SareaBs CtlActCd is false
	 * Reason 2: SapBpSarea is marked for deletion
	 * Reason 3: Sales group is inactive
	 * Reason 3: Sales office is inactive
	 * @return
	 */
	public void validateSapBpSareaRelatedData(boolean applyBpBlockCheck) {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validateSapBpSareaRelatedData");
		}
		// ]]
		try {
			if (applyBpBlockCheck && financialbackgroundData.isSoldToPartnerBlocked()) {
				updateInvalidMap(shiptoSarea, ShiptoSalesAreaError.BLOCK_BP);
			}
			
			else if (applyBpBlockCheck && financialbackgroundData.isSoldtoSapBpSalesAreaBlocked()) {
				updateInvalidMap(shiptoSarea, ShiptoSalesAreaError.BLOCK_SOME_SALES_AREA);
			}
			
			if (shiptoSarea.getSarea().getCtlActCd().equals(Boolean.FALSE)) {
				 updateInvalidMap(shiptoSarea, ShiptoSalesAreaError.SAREA);
			}
			if (shiptoSarea.getSarea().getSorg().getCtlActCd().equals(Boolean.FALSE)) {
				 updateInvalidMap(shiptoSarea, ShiptoSalesAreaError.SAREA_SORG);
			}
			
			List<ShiptoBilltoEntity> billtos = financialbackgroundData.getShiptoBilltos();
			List<ShiptoBilltoEntity> validBilltos = financialbackgroundData.getValidBilltos();
			
			/**
			 * The shipto billto's invalidity must propagate to the delivery setting's
			 * error!
			 */
			for (ShiptoBilltoEntity billto: billtos) {
				if (!validBilltos.contains(billto) || InvalidMapSupport.containsError(this.getInvalidMap(), ShiptoBilltoEntity.class, ShiptoSalesAreaError.SHIPTO_BILLTO_IN_SAREA)) {
					 updateInvalidMap(billto, ShiptoSalesAreaError.SHIPTO_BILLTO);
				}
			}
			
			boolean shiptoSapBpSareaValid = !financialbackgroundData.getShiptoSareaMarkedForDel();
			
			if (!shiptoSapBpSareaValid) {
				updateInvalidMap(shiptoSarea, ShiptoSalesAreaError.SAREA);
			}
			
			boolean shiptoSapBpSGroupValid = financialbackgroundData.getShiptoSareaSgroupActive();
			
			if (!shiptoSapBpSGroupValid) {
				updateInvalidMap(shiptoSarea, ShiptoSalesAreaError.SALESGROUP);
			}
			
			boolean shiptoSapBpSOfficeValid = financialbackgroundData.getShiptoSareaSofficeActive();
			
			if (!shiptoSapBpSOfficeValid) {
				updateInvalidMap(shiptoSarea, ShiptoSalesAreaError.SAP_BP_SOFFICE);
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
	
	
	public MaterialAvailabilityBackgroundData getMaterialAvailabilityBackgroundData() {
		return materialAvailabilityBackgroundData;
	}

	@Override
	public Map<Object, List<ShiptoSalesAreaError>> getInvalidMap() {
		return super.getInvalidMap();
	}
	
	public ShiptoSareaEntity getShiptoSarea() {
		return shiptoSarea;
	}

	public DeliverySettingShiptoMap getShiptoSettings() {
		return shiptoSettings;
	}


	public MaterialSelectionInitializer getMaterialSelectionInitializer() {
		return materialSelectionInitializer;
	}
	
	
	/**
	 * Partition the list of {@link DeliverySettingDTO} for the contract parameter. 
	 * 
	 * It returns a deliverySettingList which items has the same contract as fisrtSapContract
	 * 
	 */
	// TODO (emoke Oct 10, 2012): Why is this named for first delivery setting? because on the long run the parameter should be the delivery setting itself. 
	//the plan is that those filters which are currently applied in javascript everywhere could come here. Example: bag + bag is possible but bag + bulk not etc.
	//these rules are clear enough, but there is no time to rewrite all code, and cleaup the javascripts as well properly
	public List<DeliverySettingDTO> getPossibleDeliverySettingsForFirstDeliverySetting(
			Long firstSapContractId) {

		return getPossibleDeliverySettingsForFirstDeliverySetting(firstSapContractId, getMaterialAndServiceDeliverySettings());
	}

	// TODO (emoke Oct 10, 2012): Why is this named for first delivery setting? because on the long run the parameter should be the delivery setting itself. 
	//the plan is that those filters which are currently applied in javascript everywhere could come here. Example: bag + bag is possible but bag + bulk not etc.
	//these rules are clear enough, but there is no time to rewrite all code, and cleaup the javascripts as well properly
	public List<DeliverySettingDTO> getServicesForFirstDeliverySetting(Long firstSapContractId, String firstProductionPlantCd){
		List<DeliverySettingDTO> serviceDeliverySettings = getPossibleDeliverySettingsForFirstDeliverySetting(firstSapContractId, getServiceDeliverySettingList());
		
		if (firstProductionPlantCd != null && serviceDeliverySettings != null) {
			final MaterialAvailabilityBackgroundData materialAvailability = getMaterialAvailabilityBackgroundData();
			
			for (Iterator<DeliverySettingDTO> iter = serviceDeliverySettings.iterator(); iter.hasNext(); ) {
				DeliverySettingDTO serviceDeliverySetting = iter.next();
				Long sapMaterialId = serviceDeliverySetting.getSapMaterialId();
				
				List<PlantDTO> plants = DeliverySettingFilterLogic.filterProductionPlantOptions(sapMaterialId, materialAvailability);
				
				Set<String> plantCds = new HashSet<String>();
				for (PlantDTO plant : plants) {
					plantCds.add(plant.getPlantCd());
				}
				
				if (!plantCds.contains(firstProductionPlantCd)) {
					iter.remove();
				}
			}
		}
		
		return serviceDeliverySettings;
	}
	
	// TODO (emoke Oct 10, 2012): Why is this named for first delivery setting? because on the long run the parameter should be the delivery setting itself. 
	//the plan is that those filters which are currently applied in javascript everywhere could come here. Example: bag + bag is possible but bag + bulk not etc.
	//these rules are clear enough, but there is no time to rewrite all code, and cleaup the javascripts as well properly
	public List<DeliverySettingDTO> getMaterialForFirstDeliverySetting(Long firstSapContractId){
		return getPossibleDeliverySettingsForFirstDeliverySetting(firstSapContractId, getMaterialDeliverySettingList());
	}
	
	private List<DeliverySettingDTO> getPossibleDeliverySettingsForFirstDeliverySetting(Long firstSapContractId, List<DeliverySettingDTO> deliverySettingList){
		List<DeliverySettingDTO> deliverySettingsForFirstItem = new ArrayList<DeliverySettingDTO>();

		Map<Long, List<DeliverySettingDTO>> noContractDeliverySettingMap = new HashMap<Long, List<DeliverySettingDTO>>();
		Set<Long> contractSapMaterialIdSet = new HashSet<Long>();
		for (DeliverySettingDTO deliverySetting : deliverySettingList) {
			if (deliverySetting.isAutoPayerSettingValid()) {
					SapContractItemEntity contractItem = deliverySetting.getSapContractItem();
		
					if (contractItem == null ) {
						
						//if there is no contract, only will be added if there is such material from the contract, but as for a material there can be multiple from this, it is a list
						if (!contractSapMaterialIdSet.contains(deliverySetting.getSapMaterialId())) {
							
							if ( !noContractDeliverySettingMap.containsKey(deliverySetting.getSapMaterial().getSapMaterialId())) {
								List<DeliverySettingDTO> noContractDeliverySettings = new ArrayList<DeliverySettingDTO>();
								noContractDeliverySettingMap.put(deliverySetting.getSapMaterial().getSapMaterialId(), noContractDeliverySettings);
							}
							noContractDeliverySettingMap.get(deliverySetting.getSapMaterial().getSapMaterialId()).add(deliverySetting);
						}
					}
					else if (contractItem.getSapContract().getSapContractId().equals(
								firstSapContractId)) {
						//add in any case if the contract matches
						deliverySettingsForFirstItem.add(deliverySetting);
						contractSapMaterialIdSet.add(deliverySetting.getSapMaterialId());
					}
			}
		}
		//if a material is in a contract, do not add it without a contract
		for ( Map.Entry<Long, List<DeliverySettingDTO>> entry : noContractDeliverySettingMap.entrySet() ) {
			if ( !contractSapMaterialIdSet.contains(entry.getKey() )) {
				deliverySettingsForFirstItem.addAll(entry.getValue());
			}
		}
		
		return deliverySettingsForFirstItem;
	}
	

	/**
	 * Find in any of the delivery contexts.
	 * @param s
	 * @return
	 */
	public DeliverySettingDTO getDeliverySettingByCompositeIdentifier(String compositeIdentifier) {
		List<DeliverySettingDTO> materialAndServiceDeliverySettings = getMaterialAndServiceDeliverySettings();
		for (DeliverySettingDTO currentDeliverySetting : materialAndServiceDeliverySettings) {
			String currentCompositeIdentifier = currentDeliverySetting.getCompositeIdentifier();
			if (currentCompositeIdentifier.equals(compositeIdentifier)) {
				return currentDeliverySetting;
			}
		}
		return null;
	}
	
	/**
	 * Find in any of the delivery contexts.
	 * @param s
	 * @return
	 */
	public DeliverySettingDTO getMaterialDeliverySettingByCompositeIdentifier(String compositeIdentifier) {
		List<DeliverySettingDTO> materialDeliverySettings = getMaterialDeliverySettingList();
		for (DeliverySettingDTO currentDeliverySetting : materialDeliverySettings) {
			String currentCompositeIdentifier = currentDeliverySetting.getCompositeIdentifier();
			if (currentCompositeIdentifier.equals(compositeIdentifier)) {
				return currentDeliverySetting;
			}
		}
		return null;
	}
	
	/**
	 * Find in any of the delivery contexts.
	 * @param s
	 * @return
	 */
	public DeliverySettingDTO getServiceDeliverySettingByCompositeIdentifier(String compositeIdentifier) {
		List<DeliverySettingDTO> serviceDeliverySettings = getServiceDeliverySettingList();
		for (DeliverySettingDTO currentDeliverySetting : serviceDeliverySettings) {
			String currentCompositeIdentifier = currentDeliverySetting.getCompositeIdentifier();
			if (currentCompositeIdentifier.equals(compositeIdentifier)) {
				return currentDeliverySetting;
			}
		}
		return null;
	}


	public void setShiptoSettings(DeliverySettingShiptoMap shiptoSettings) {
		this.shiptoSettings = shiptoSettings;
	}


	public ShiptoSalesAreaFinancialbackgroundData getFinancialbackgroundData() {
		return financialbackgroundData;
	}

	/**
	 * Validates a delivery setting {@link DeliverySettingDTO}. In case all the attributes can be found in
	 * the parameter {@link DeliveryOptionConfigurationDTO} the setting counts as valid.
	 * @param setting
	 * @param optionConfiguration
	 * @param settingBasedOn
	 */
	public void validateDeliverySetting(final DeliverySetting setting,
			DeliveryOptionConfigurationDTO optionConfiguration, Map<Object, List<ShiptoSalesAreaError>> invalidMap) {
		validateDeliverySettingWithoutSupplyPlant(setting,optionConfiguration,invalidMap);
		validateSupplyPlantOfDeliverySetting(setting,optionConfiguration,invalidMap);
		validateAutoPayerDeterminationFlagOfDeliverySetting(setting,optionConfiguration,invalidMap);
		validateAvailableInPickupAppFlagOfDeliverySetting(setting,optionConfiguration,invalidMap);
	}
	
	/**
	 * If the dispocard is available in terminal application, but it is not allowed to, 
	 * then it is invalid.
	 * 
	 * Notice: User cannot save dispocard with availableInPickupAppFlag ON, if it is not allowed.
	 * However, there may be invalid dispocards created before LOGON-5073...
	 * 
	 * @param setting
	 * @param optionConfiguration
	 * @param invalidMap
	 */
	private void validateAvailableInPickupAppFlagOfDeliverySetting(
			DeliverySetting setting,
			DeliveryOptionConfigurationDTO optionConfiguration,
			Map<Object, List<ShiptoSalesAreaError>> invalidMap) {
		ShiptoSareaRestrEntity shiptoSareaRestr = setting.getShiptoSareaRestr();

		boolean availableInPickupAppFlagEnabled = isAvailableInPickupAppFlagEnabled(setting);
		setting.setAvailableInPickupAppFlagEnabled(availableInPickupAppFlagEnabled);

		if (shiptoSareaRestr != null && 
			shiptoSareaRestr.getAvailableInPickupAppFlag() &&
			!setting.isAvailableInPickupAppFlagEnabled()) {
			updateInvalidMap(setting, ShiptoSalesAreaError.AVAILABLE_IN_PICKUP_APP_FLAG, invalidMap);
		}
	}

	/**
	 * Checks whether the DeliverySetting's contract allows to use the dispocard for pickup delivery
	 * type orders.
	 * 
	 * @param setting
	 * @return
	 */
	private boolean contractAllowsPickupDeliveryType(DeliverySetting setting) {

		Map<String, Set<Long>> sapIncotermCdDeliveryTypeCdMapBySalesAreaId =
				getDeliveryTypesByIncotermMap();
		if (sapIncotermCdDeliveryTypeCdMapBySalesAreaId == null) {
			return false;
		}
		SapContractItemEntity sapContractItem = setting.getSapContractItem();
		if (sapContractItem == null) {
			return false;
		}
		SapContractEntity sapContract = sapContractItem.getSapContract();
		String incotermsCd = sapContract.getIncotermsCd();
		if (incotermsCd == null) {
			return false;
		}
		Set<Long> deliveryTypeSet = sapIncotermCdDeliveryTypeCdMapBySalesAreaId.get(incotermsCd);
		return deliveryTypeSet.contains(Constant.DELIVERY_TYPE_PICKUP);
	}

	/**
	 * Checks whether a DeliverySetting can be used in Terminal Application order creation.
	 * 
	 * @param setting
	 * @return
	 */
	public boolean isAvailableInPickupAppFlagEnabled(DeliverySetting setting) {
		SapMaterialEntity sapMaterial = setting.getSapMaterial();
		if (sapMaterial == null) {
			return false;
		}
		if (LogonUtil.isPlanningMaterial(sapMaterial)) {
			return false;
		}
		MaterialPackageBsEntity materialPackage = sapMaterial.getMaterialPackage();
		String materialPackageCd = materialPackage.getMaterialPackageCd();
		String productionPlantCd = setting.getProductionPlantCd();
		String uomCd = setting.getUomCd();
		Long shippingpointId = setting.getShippingpointId();
		Long loadingpointId = setting.getLoadingpointId();
		boolean requiredFieldsOK = !LogonUtil.emptyValue(productionPlantCd) &&
				!LogonUtil.emptyValue(uomCd) &&
				!LogonUtil.emptyValue(shippingpointId);
		if (!requiredFieldsOK) {
			return false;
		}
		// Bulk pickup dispocards must have empty loadingpoint
		if (Constant.bulkOrderMaterialPackageCds.contains(materialPackageCd)
				&& !LogonUtil.emptyValue(loadingpointId)) {
			return false;
		}
		boolean contractTypeOK = contractAllowsPickupDeliveryType(setting);
		if (!contractTypeOK) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param setting
	 * @param optionConfiguration
	 * @param invalidMap
	 */
	
	private void validateAutoPayerDeterminationFlagOfDeliverySetting(
			DeliverySetting setting,
			DeliveryOptionConfigurationDTO optionConfiguration,
			Map<Object, List<ShiptoSalesAreaError>> invalidMap) {
		if (!setting.isAutoPayerSettingValid()) {
			updateInvalidMap(setting, ShiptoSalesAreaError.AUTOPAYER, invalidMap);
		}
	}

	/**
	 * Validates a delivery setting {@link DeliverySettingDTO} with omiting supplyPlant check. In case all the attributes can be found in
	 * the parameter {@link DeliveryOptionConfigurationDTO} the setting counts as valid.
	 * @param setting
	 * @param optionConfiguration
	 * @param settingBasedOn
	 */
	public void validateDeliverySettingWithoutSupplyPlant(final DeliverySetting setting,
			DeliveryOptionConfigurationDTO optionConfiguration, Map<Object, List<ShiptoSalesAreaError>> invalidMap) {


		if (setting.getResolutionType() == DeliverySettingDTO.RESOLUTION_MISSING) {
			updateInvalidMap(setting, ShiptoSalesAreaError.CONTRACT_INVALID, invalidMap);
		}

		if (setting.getDeliveryTypeCd() != null) {
			Boolean deliveryTypeFound = false;
			Long deliveryTypeCdFromSetting = setting.getDeliveryTypeCd();
			List<DeliveryTypeTxEntity> deliveryTypeTxs = optionConfiguration.getDeliveryTypeList();
			for (DeliveryTypeTxEntity currentDeliveryTypeTx : deliveryTypeTxs) {
				DeliveryTypeTxPK ccurrentDeliveryTypeTxPK = currentDeliveryTypeTx.getDeliveryTypeTxPK();
				DeliveryTypeBsEntity currentDeliveryType = ccurrentDeliveryTypeTxPK.getDeliveryTypeBs();
				Long currentDeliveryTypeCd = currentDeliveryType.getDeliveryTypeCd();
				if (deliveryTypeCdFromSetting.equals(currentDeliveryTypeCd)) {
					deliveryTypeFound = true;
					break;
				}
			}
			if (!deliveryTypeFound) {
				updateInvalidMap(setting, ShiptoSalesAreaError.DELIVERY_TYPE, invalidMap);
			}
		}
		if (setting.getBilltoSapBpId() != null) {
			/**
			 * In case the sales area does not allow this billto, and it is
			 * not at all in the shipto+billto list, then the billto is invalid.
			 */
			Boolean validBilltoFound = false;
			Long billtoSapBpIdFromSetting = setting.getBilltoSapBpId();
			List<ShiptoBilltoEntity> validBilltos = getFinancialbackgroundData().getValidBilltos();
			for(ShiptoBilltoEntity currentBillto : validBilltos) {
				SapBpEntity currentSapBp = currentBillto.getBilltoSapBp();
				Long currentSapBpId = currentSapBp.getSapBpId();
				if (billtoSapBpIdFromSetting.equals(currentSapBpId)) {
					validBilltoFound = true;
					break;
				}
			}
			if (!validBilltoFound) {
				updateInvalidMap(setting, ShiptoSalesAreaError.BILLTO, invalidMap);
			}
		}

		if (setting.getUomCd() != null) {
			//uom
			boolean valid = false;
			final List<UomTxEntity> globalUomList = optionConfiguration.getUomList();
			if (globalUomList != null) {
				for (UomTxEntity uom : globalUomList) {
					if (uom.getUomTxPK().getUomBs().getUomCd().equals(setting.getUomCd())) {
						valid = true;
						break;
					}
				}
			}
			if (!valid) {
				updateInvalidMap(setting, ShiptoSalesAreaError.UOM, invalidMap);
			}
		}
		if (setting.getProductionPlantCd() != null && !LogonUtil.isPlanningMaterial(setting.getSapMaterial())) {
			//productionPlant
			boolean valid = false;
			if (optionConfiguration.getProdPlantList() != null) {
				for (PlantDTO plant : optionConfiguration.getProdPlantList()) {
					if (plant.getPlantCd().equals(setting.getProductionPlantCd())) {
						valid = true;
						break;
					}
				}
			}
			if (!valid) {
				updateInvalidMap(setting, ShiptoSalesAreaError.PRODUCTION_PLANT, invalidMap);
			}
		}

		if (setting.getShippingpointId() != null) {
			//setting.getShippingpoint()						
			boolean shvalid = false;
			
			//Do not validate shipping point for services
			shvalid = LogonPackageHandlingUtil.isService(setting.getSapMaterial());
			if (!shvalid) {
				//Do not validate shipping point for planning material
				shvalid = LogonUtil.isPlanningMaterial(setting.getSapMaterial());
			}
			if (!shvalid) {
				for (ShippingpointDTO sp : optionConfiguration.getShippingpoinList()) {
					if (sp.getShippingpointId().equals(setting.getShippingpointId())) {
						shvalid = true;
						break;
					}
				}
			}
			if (!shvalid) {
				updateInvalidMap(setting, ShiptoSalesAreaError.SHIPPINGPOINT, invalidMap);
			}
		}
		if (setting.getLoadingpointId() != null) {
			boolean lpValid = false;
			//setting.getLoadingpoint()
			
			//Do not validate loadingpoint for services			
			lpValid = LogonPackageHandlingUtil.isService(setting.getSapMaterial());			
			if (!lpValid) {
				//Do not validate loadingpoint for planning material
				lpValid = LogonUtil.isPlanningMaterial(setting.getSapMaterial());
			}
			if (!lpValid && optionConfiguration.getLoadingpointList() != null) {
				for (LoadingPointDTO lp : optionConfiguration.getLoadingpointList()) {
					if (lp.getLoadingpointId().equals(setting.getLoadingpointId())) {
						lpValid = true;
						break;
					}
				}
			}
			if (!lpValid) {
				updateInvalidMap(setting, ShiptoSalesAreaError.LOADINGPOINT, invalidMap);
			}
		}

		if (setting.getShiptoSiloId() != null) {
			//setting.getSilo()
			boolean valid = false;
			if (LogonPackageHandlingUtil.isService(setting.getSapMaterial())) {
				valid = true;
			}
			else {
				if (optionConfiguration.getSiloList() != null) {
					for (ShiptoSiloEntity siloent : optionConfiguration.getSiloList()) {
						if (siloent.getShiptoSiloId().equals(setting.getShiptoSiloId())){
							valid = true;
							break;
						}
					}				
				}
			}
			
			if (!valid) {
				updateInvalidMap(setting, ShiptoSalesAreaError.SILO, invalidMap);
			}
		}
		if (setting.getPoolId() != null) {

			//setting.getPool()
			//pool
			boolean valid = false;
			for (PoolTxEntity p : optionConfiguration.getPoolList()) {
				if (p.getPoolTxPK().getPoolId().equals(setting.getPoolId())) {
					valid = true;
					break;

				}
			}
			if (!valid) {
				updateInvalidMap(setting, ShiptoSalesAreaError.POOL, invalidMap);
			}
		}
		//setting.getCarrier()
		if (setting.getCommercialCarrierId() != null) {
			boolean carrvalid = false;
			if (optionConfiguration.getCommercialCarrierList() != null) {
				for (CommercialCarrierDTO c : optionConfiguration.getCommercialCarrierList()) {
					if (c.getCarrierId().equals(setting.getCommercialCarrierId())
							&& (setting.getPoolId() == null || setting.getPoolId().equals(
									setting.getPoolId()))) {
						carrvalid = true;
						break;
					}
				}
			}
			if (!carrvalid) {
				updateInvalidMap(setting, ShiptoSalesAreaError.CARRIER, invalidMap);
			}

		}
		if (setting.getPayerSapBpId() != null) {
			Boolean payerFound = false;
			Long sapBpIdOfPayer = setting.getPayerSapBpId();
			List<SapBpEntity> payerList = optionConfiguration.getPayerList();
			for (SapBpEntity payer : payerList) {
				Long currentSapBpId = payer.getSapBpId();
				if (sapBpIdOfPayer.equals(currentSapBpId)) {
					payerFound = true;
					break;
				}
			}
			
			if ( !payerFound ) {
				updateInvalidMap(setting, ShiptoSalesAreaError.PAYER, invalidMap);
			}
		}
		ShiptoSareaRestrEntity shiptoSareaRestr = setting.getShiptoSareaRestr();
		SapMaterialEntity sapMaterial = setting.getSapMaterial();
		PlantBsEntity defaultProdPlant = sapMaterial.getProdPlant();
		if (defaultProdPlant != null && shiptoSareaRestr != null && shiptoSareaRestr.getProductionPlant() != null 
				&& !defaultProdPlant.getPlantCd().equals(shiptoSareaRestr.getProductionPlant().getPlantCd())){
			updateInvalidMap(setting, ShiptoSalesAreaError.PRODUCTION_PLANT, invalidMap);
		}
		
		//vehicle type list is intentionally not checked, as for the moment it's complicated - dispokarte seems to RESTRICT those not just DEFAULTING those. the restrictions are not used everyehere
		
	}

	/**
	 * Validate supplyPlant only
	 * 
	 */
	private void validateSupplyPlantOfDeliverySetting(final DeliverySetting setting,
			DeliveryOptionConfigurationDTO optionConfiguration, Map<Object, List<ShiptoSalesAreaError>> invalidMap) {
		if (setting.getSupplyPlantCd() == null || LogonUtil.isPlanningMaterial(setting.getSapMaterial())) {
			// no validation needed
			return;
		}
		boolean valid = false;
		for (SupplyPlantDTO plant : optionConfiguration.getSupplyPlantList()) {
			if (plant.getPlantCd().equals(setting.getSupplyPlantCd())) {
				valid = true;
				break;
			}
		}
		if (!valid) {
			updateInvalidMap(setting, ShiptoSalesAreaError.SUPPLY_PLANT, invalidMap);
		}
	}

	/**
	 * @param restr
	 * @param obj TODO
	 * @param message
	 * @param invalidMap TODO
	 */
	protected void updateInvalidMap(Object obj, ShiptoSalesAreaError message, Map<Object, List<ShiptoSalesAreaError>> invalidMap ) {
		List<ShiptoSalesAreaError> list = invalidMap.get(obj);
	
		if (list == null) {
			list = new ArrayList<ShiptoSalesAreaError>();
			invalidMap.put(obj, list);
		}
	
		list.add(message);
	}
	
	/**
	 * In some cases there is a need to match a delivery setting to a delivery settings available.
	 * This is supposed to happen for example in case an delivery setting formed from the attributes
	 * of an order must be matched with a delivery setting which is based on a default. Purpose: if
	 * the default is still available, the setting is still valid. The method {@link #findDeliverySettingByCompositeIdentifier(String)}
	 * would not return it as the order knows nothing about the default - if any - on what it was once based on.
	 * The restriction id is not available in the delivery setting formed from the order/order item, but
	 * is part of the composite identifier of the delivery setting based on the default.
	 * 
	 * (Probably this counts as a workaround, but the composite identifier needed to be 
	 * unique, and it is not true without considering the restriction id as part of the identifier.
	 * 
	 * @param deliverySetting
	 * @return if found a complete match return that
	 */
	public DeliverySettingDTO matchDeliverySetting(AbstractDeliverySetting setting) {
		String compositeIdentifier = setting.getCompositeIdentifier();
		
		DeliverySettingDTO firstMatch = null;
		for ( DeliverySettingDTO currentDeliverySetting : getMaterialAndServiceDeliverySettings() ) {
			String currentCompositeIdentifier = currentDeliverySetting.getCompositeIdentifier();
			if (currentCompositeIdentifier.equals(compositeIdentifier) ) {
				return currentDeliverySetting;
			}
			//otherwise match ALL MEANINGFUL attributes of the setting, and return the first one which is a match
			if (firstMatch == null && setting.match(currentDeliverySetting)){
				firstMatch = currentDeliverySetting;
			}
		}
		
		return firstMatch;
		
	}
	
}
