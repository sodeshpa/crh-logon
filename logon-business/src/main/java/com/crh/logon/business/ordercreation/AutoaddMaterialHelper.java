/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ordercreation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.ordercreation.dto.AutoaddMaterialDTO;
import com.crh.logon.business.ordercreation.dto.AutoaddMaterialFilterDTO;
import com.crh.logon.business.services.localinterfaces.BestellungServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;


/**
 * @author AdNovum Informatik AG
 */
public class AutoaddMaterialHelper {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(AutoaddMaterialHelper.class);
	
	/**
	 * LRQ 018.0 Pallet Material
	 * Adding autoAddItems to order. Iterating over the orderitems autoAddmaterials are added if mat_plant is configured for the material.   
	 * 
	 * @param order
	 * @throws AutoaddMaterialException
	 */
	public void autoAddItems(OrderEntity order, BestellungServiceLocalInterface bestellungService) throws AutoaddMaterialException {
		List<OrderItemEntity> originalItems = Collections.<OrderItemEntity>unmodifiableList(order.getOrderItems());

		List<Long> materialIdsOfOrder = new ArrayList<Long>();
		for (OrderItemEntity orderItem : order.getOrderItems()) {
			SapMaterialEntity material = orderItem.getSapMaterial();
			Long materialId = material.getSapMaterialId();
			materialIdsOfOrder.add(materialId);
		}
		
		Set<AutoaddMaterialFilterDTO> autoAddMaterialFilterDTOs = new HashSet<AutoaddMaterialFilterDTO>();
		List<OrderItemEntity> newOrderItems = new ArrayList<OrderItemEntity>();
		newOrderItems.addAll(originalItems);
		
		for (OrderItemEntity orderItem: originalItems) {
			if (orderItem.getProductionPlant() == null) {
				continue;
			}
			final AutoaddMaterialFilterDTO autoaddMaterialFilterDTO = new AutoaddMaterialFilterDTO(orderItem.getProductionPlant().getPlantCd(), orderItem.getSapMaterial().getSapMaterialId(), orderItem.getOrderedUom().getUomCd());
			autoAddMaterialFilterDTOs.add(autoaddMaterialFilterDTO);
		}
		
		Map<AutoaddMaterialFilterDTO, AutoaddMaterialDTO> autoAddMaterialData = bestellungService.fetchAutoaddMaterials(order.getSarea(), order.getShiptoSapBp().getSapBpId(), order.getSoldtoSapBp().getSapBpId(), order.getSapContract().getSapContractId(), autoAddMaterialFilterDTOs);
		
		if (autoAddMaterialData == null) {
			return; 
		}
		for (OrderItemEntity orderItem: originalItems){
			OrderItemEntity autoaddEntry = createAutoAddItem(orderItem, newOrderItems, materialIdsOfOrder, autoAddMaterialData);
			
			if(autoaddEntry != null){
				newOrderItems.add(autoaddEntry);
				order.setOrderItems(newOrderItems);
			}
		}
	}
		
	/**
	 * 
	 * Creates an orderItemEntity and returns it with the found delivery setting for that entity
	 * 
	 * @param sourceOrderItemEntity - the original orderItemEntity for which the autoadd material is checked and added
	 * @param orderItemsToCheckExistence - all the orderItems in the order. The order can not contain the same material twice.
	 * @param autoAddMaterials Set containing initialization data for the autoadded materials
	 * @return
	 * @throws AutoaddMaterialException
	 */
	public OrderItemEntity createAutoAddItem(OrderItemEntity sourceOrderItemEntity, List<OrderItemEntity> orderItemsToCheckExistence, List<Long> originalMaterialList, Map<AutoaddMaterialFilterDTO, AutoaddMaterialDTO> autoAddMaterials)
			throws AutoaddMaterialException {
		
		//get MatPlantEntity that stores the information if autoAddMaterial exists for the plant+material combo. 
		PlantBsEntity productionPlant = sourceOrderItemEntity.getProductionPlant();
		
		final Map<Long, OrderItemEntity> alreadyPresentMaterials = buildAlreadyPresentMapBasedOnIds(orderItemsToCheckExistence); 
		
		final SapMaterialEntity sourceSapMaterial = sourceOrderItemEntity.getSapMaterial();
		final UomBsEntity orderedUom = sourceOrderItemEntity.getOrderedUom();
		final BigDecimal orderedQuantity = sourceOrderItemEntity.getOrderedQuantity();
		
		
		OrderItemEntity autoAddOrderItemEntity = createAutoAddMaterial(productionPlant, alreadyPresentMaterials,
				sourceSapMaterial, orderedUom, orderedQuantity,
				originalMaterialList, autoAddMaterials);
		
		if ( autoAddOrderItemEntity != null ) {
			autoAddOrderItemEntity.setProductionPlant(sourceOrderItemEntity.getProductionPlant());
			autoAddOrderItemEntity.setLoadingpoint(sourceOrderItemEntity.getLoadingpoint());
			autoAddOrderItemEntity.setCurrency(sourceOrderItemEntity.getCurrency());
			autoAddOrderItemEntity.setOrderItemNo(new Long(10 * (orderItemsToCheckExistence.size()+1)));
			autoAddOrderItemEntity.setOrder(sourceOrderItemEntity.getOrder());
		}
		return autoAddOrderItemEntity;
	}

	/**
	 * Returns a map where the key is the unique id of a given material and the value is an OrderItemEntity which has this
	 * material
	 * @param alreadyPresentMaterialIds 
	 * @return created map or empty if input is empty
	 */
	static Map<Long, OrderItemEntity> buildAlreadyPresentMapBasedOnIds(List<OrderItemEntity> orderItems) {
		// [[
		Map<Long,OrderItemEntity> result = null;
		Object[][] params = {{"orderItems", orderItems}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(AutoaddMaterialHelper.class, "buildAlreadyPresentMapBasedOnIds", params);
		}
		// ]]
		try {
			Map<Long, OrderItemEntity> orderItemMap = new HashMap<Long, OrderItemEntity>();
			
			for (OrderItemEntity item : orderItems) {
				SapMaterialEntity sapMaterial = item.getSapMaterial();
				Long sapMaterialId = sapMaterial.getSapMaterialId();
				 orderItemMap.put(sapMaterialId, item);
			}
			
			return result = orderItemMap;
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
	 * Creates or modifies an {@link OrderItemEntity} for to the sap material which 
	 * needs to be added to the order on behalf of the parameter sourceSapMaterial.
	 * 
	 * @throws AutoaddMaterialException
	 */
	private OrderItemEntity createAutoAddMaterial(PlantBsEntity productionPlant,
			final Map<Long, OrderItemEntity> alreadyPresentMaterialIds, final SapMaterialEntity sourceSapMaterial,
			final UomBsEntity orderedUnitOfMeasure, final BigDecimal orderedQuantity,
			final List<Long> originalMaterialList, Map<AutoaddMaterialFilterDTO, AutoaddMaterialDTO> autoAddMaterialData) throws AutoaddMaterialException {
				
		if(productionPlant == null) {
			return null;
		}

		Long sapMaterialId = sourceSapMaterial.getSapMaterialId();

		AutoaddMaterialDTO autoAddMaterialDefault = null;
		String plantCode = productionPlant.getPlantCd();
		AutoaddMaterialFilterDTO filterDTO = new AutoaddMaterialFilterDTO(plantCode, sapMaterialId, orderedUnitOfMeasure.getUomCd());
		if (autoAddMaterialData.containsKey(filterDTO)) {
			autoAddMaterialDefault = autoAddMaterialData.get(filterDTO);
		}
		else {
			// For the plantCode/SapMaterialId/Unit of Measure tuple, there is no auto add material.
			// Returning null means no new Order Item should be added to the existing list of order items.
			return null;
		}

		if (autoAddMaterialDefault != null){
			SapMaterialEntity autoaddMaterialEntity = autoAddMaterialDefault.getSapMaterial();	

			if (autoaddMaterialEntity == null) {
				LOG.info("MatPlantEntity is not properly set for material: " + sapMaterialId + ", plant: " + plantCode);
				return null;
			}
			else {
				// If the material was already added from CC for example auto add should not take place 
				final Long autoAddMaterialId = autoaddMaterialEntity.getSapMaterialId();
				if (originalMaterialList.contains(autoAddMaterialId)) {
					return null;
				}

				//add autoAddOrderItemEntity only if it is not added to the list
				boolean autoAddItemAlreadyAdded = false;
				if (alreadyPresentMaterialIds.containsKey(autoAddMaterialId)) {
					autoAddItemAlreadyAdded = true;
				}

				OrderItemEntity autoAddOrderItemEntity;
				BigDecimal autoadd1PerNOfUom = autoAddMaterialDefault.getAutoadd1PerNOfUom();
				BigDecimal autoAddItemQuantity = orderedQuantity.divide(autoadd1PerNOfUom, 0, BigDecimal.ROUND_UP);

				if (autoAddItemAlreadyAdded) {
					autoAddOrderItemEntity = alreadyPresentMaterialIds.get(autoAddMaterialId);
					BigDecimal beforeQuantity = autoAddOrderItemEntity.getOrderedQuantity();
					BigDecimal newQuantity = beforeQuantity.add(autoAddItemQuantity);
					autoAddOrderItemEntity.setOrderedQuantity(newQuantity);

					initAutoAddLoadedFieldsForServices(autoAddOrderItemEntity, newQuantity);
						
					return null;
				}
				else {
					autoAddOrderItemEntity = new OrderItemEntity();
					autoAddOrderItemEntity.setSapMaterial(autoaddMaterialEntity);
					autoAddOrderItemEntity.setOrderedUom(autoaddMaterialEntity.getBaseUom());
					autoAddOrderItemEntity.setSapContractItem(autoAddMaterialDefault.getSapContractItem());
					autoAddOrderItemEntity.setOrderedQuantity(autoAddItemQuantity);
					
					initAutoAddLoadedFieldsForServices(autoAddOrderItemEntity, autoAddItemQuantity);

					return autoAddOrderItemEntity;
				}
			}
		}
		return null;
	}

	/**
	 * Initializes the loaded fields for service items.
	 * @param autoAddOrderItemEntity - the {@link OrderItemEntity} which is subject of the initialization 
	 * @param newQuantity - the quantity which is set as loaded quantity
	 */
	private void initAutoAddLoadedFieldsForServices(OrderItemEntity autoAddOrderItemEntity,
			BigDecimal newQuantity) {
		if (LogonPackageHandlingUtil.isService(autoAddOrderItemEntity)) {
			autoAddOrderItemEntity.setLoadedQuantity(newQuantity);
			autoAddOrderItemEntity.setLoadedUom(autoAddOrderItemEntity.getOrderedUom());
		}
	}
	
	public class AutoaddMaterialException extends Exception {
		
		private static final long serialVersionUID = 1L;
		
		private String message;
		private String details;
		private Long autoaddSapMaterialId;

		public AutoaddMaterialException(String string) {
			message = string;
		}

		public AutoaddMaterialException(String message, String details) {
			this.message = message;
			this.details = details;
		}

		public AutoaddMaterialException(String message, String details, Long autoaddSapMaterialId) {
			super();
			this.message = message;
			this.details = details;
			this.autoaddSapMaterialId = autoaddSapMaterialId;
		}

		/**
		 * Returns the key off the error message
		 */
		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		/**
		 * Returns the error details, the source material id and the plant id which determines the matplant. 
		 * @return
		 */
		public String getDetails() {
			return details;
		}

		public void setDetails(String details) {
			this.details = details;
		}

		/**
		 * Returns the material Id of the material that could not added automatically
		 * 
		 */
		public Long getAutoaddSapMaterialId() {
			return autoaddSapMaterialId;
		}

		public void setAutoaddSapMaterialId(Long autoaddSapMaterialId) {
			this.autoaddSapMaterialId = autoaddSapMaterialId;
		}


	}
}
