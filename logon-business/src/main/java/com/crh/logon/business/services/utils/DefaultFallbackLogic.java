/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.util.Iterator;
import java.util.List;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.utils.ConfigBean;

/**
 * This class is for retrieve the default values for certain properties of certain objects. 
 * All of them has some fallback logic (one or two level). Provides methods to get all level 
 * default values for the object, and meethods to retrieve the currently used value, which is 
 * always the object's field value if set, if not set one level higher default value, and so on...
 */
public class DefaultFallbackLogic { 

	public static class LevelOneFallbackStructure{
		//own value of the field
		private Long levelZeroDefaultValue = null;

		//logon default is the next level
		private Long levelOneDefaultValue = null;

		public Long getLevelZeroDefaultValue() {
			return levelZeroDefaultValue;
		}

		public Long getLevelOneDefaultValue() {
			return levelOneDefaultValue;
		}
	}

	public static class LevelTwoFallbackStructure{
		//own value of the field
		private Long levelZeroDefaultValue = null;

		//one level higher default value
		private Long levelOneDefaultValue = null;

		//logon default is the next level
		private Long levelTwoDefaultValue = null;

		public Long getLevelZeroDefaultValue() {
			return levelZeroDefaultValue;
		}

		public Long getLevelOneDefaultValue() {
			return levelOneDefaultValue;
		}

		public Long getLevelTwoDefaultValue() {
			return levelTwoDefaultValue;
		}
	}
	
	/**
	 * The ConfigBean needed to be setted, becuase the different parts 
	 * of the application reaches the ConfigBean differently.
	 */
	private ConfigBean configBean;

	private static final String C_NAME = 
			DefaultFallbackLogic.class.getName();

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(C_NAME);

	public DefaultFallbackLogic(ConfigBean configBean) {
		this.configBean = configBean;
	}
	
	/**
	 * Converts LevelOneFallbackStructure instance to LevelTwoFallbackStructure.
	 * Helper method for getPlannedUnloadingTime()
	 * 
	 * @param levelOne - LevelOneFallbackStructure instance to convert
	 * @return
	 */
	public LevelTwoFallbackStructure convertToLevelTwoFallbackStructure(LevelOneFallbackStructure levelOne) {
		if (levelOne == null) {
			throw new IllegalArgumentException("LevelOneFallbackStructure parameter is null!");
		}
		LevelTwoFallbackStructure levelTwo = new LevelTwoFallbackStructure();
		levelTwo.levelZeroDefaultValue = levelOne.levelZeroDefaultValue;
		levelTwo.levelOneDefaultValue = levelOne.levelOneDefaultValue;
		// Note: levelOne has no levelTwoDefaultValue to set for levelTwo
		return levelTwo;
	}

	public LevelTwoFallbackStructure getPlannedUnloadingTime(OrderItemEntity orderItem,
			ShiptoEntity shipto) {

		LevelTwoFallbackStructure levelTwoFallbackStructure = new LevelTwoFallbackStructure();

		SapMaterialEntity firstSapMaterial = orderItem.getSapMaterial();
		if(firstSapMaterial != null) {
			if (orderItem.getShiptoSilo() != null) {
				// First priority is the ShiptoSilo if it is set
				levelTwoFallbackStructure = getShiptoSiloUnloadingTime(orderItem.getShiptoSilo());
			}
			else {
				// Then try to decide by material type
				if (firstSapMaterial.getMaterialIsCem()) {
					levelTwoFallbackStructure = convertToLevelTwoFallbackStructure(getShiptoUnloadingtimeCem(shipto));
				}
				else if (firstSapMaterial.getMaterialIsAgg()) {
					levelTwoFallbackStructure = convertToLevelTwoFallbackStructure(getShiptoUnloadingtimeAgg(shipto));
				}
				else if (firstSapMaterial.getMaterialIsLf()) {
					levelTwoFallbackStructure = convertToLevelTwoFallbackStructure(getShiptoUnloadingtimeLf(shipto));
				}
				else {
					// If there is no material set (eg. service), we return value 0.
					levelTwoFallbackStructure = new LevelTwoFallbackStructure();
					levelTwoFallbackStructure.levelZeroDefaultValue = 0l;
					levelTwoFallbackStructure.levelOneDefaultValue = 0l;
					levelTwoFallbackStructure.levelTwoDefaultValue = 0l;
				}
			}
		}

		return levelTwoFallbackStructure;
	}
	
	/**
	 * Gets the planned unloading time value for an OrderEntity.
	 * @param order - OrderEntity instance
	 */
	public Long getPlannedUnloadingTimeValue(Object item, ShiptoEntity shipto) {
		// [[
		Long result = null;
		Object[][] params = {{"item", item},
							{"shipto", shipto}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getPlannedUnloadingTimeValue", params);
		}
		// ]]
		try {
			OrderItemEntity orderItem = null;
			CustorderItemEntity custorderItemEntity = null;
			// TODO (emoke Sep 21, 2012):This will not be needed anymore in case on call center order items will not be present anymore. Then only custorderentity wil be needed here
			if ( item instanceof OrderItemEntity ) {
				orderItem = (OrderItemEntity) item;
			}
			else if ( item instanceof CustorderItemEntity ) {
				custorderItemEntity = (CustorderItemEntity) item;
			}
			if (orderItem == null && custorderItemEntity == null ) {
				throw new IllegalArgumentException("OrderItem is null!");
			}
			if (shipto == null) {
				throw new IllegalArgumentException("ShipTo is null!");
			}
			
			SapMaterialEntity firstSapMaterial = orderItem != null ? orderItem.getSapMaterial() : custorderItemEntity.getSapMaterial();
			
			Long plannedUnloadingTimeMin = null;
			
			if(firstSapMaterial != null) {
				
				final ShiptoSiloEntity shiptoSilo = orderItem != null ? orderItem.getShiptoSilo() : custorderItemEntity.getShiptoSilo();
				
				if (shiptoSilo != null) {
					plannedUnloadingTimeMin = getShiptoSiloUnloadingTimeValue(shiptoSilo);
				}
				else {
					if (firstSapMaterial.getMaterialIsCem()) {
						plannedUnloadingTimeMin = getShiptoUnloadingtimeCemValue(shipto);
					}
					else if (firstSapMaterial.getMaterialIsAgg()) {
						plannedUnloadingTimeMin = getShiptoUnloadingtimeAggValue(shipto);
					}
					else if (firstSapMaterial.getMaterialIsLf()) {
						plannedUnloadingTimeMin = getShiptoUnloadingtimeLfValue(shipto);
					}
					else if (LogonPackageHandlingUtil.isPallet(firstSapMaterial)) {
						plannedUnloadingTimeMin = getShiptoUnloadingtimePalValue();
					}
					else {
						// If there is no material set (eg. service), we return value 0.
						plannedUnloadingTimeMin = 0l;
					}
				}
			}
			
			return result = plannedUnloadingTimeMin;
			
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
	 * Gets the planned loading time for all OrderItemEntity-s of the order.
	 * @param orderItems TODO
	 */
	public void setPlannedLoadingTimeForOrderItem(ShiptoEntity shipto, List<OrderItemEntity> orderItems) {
		// [[
		Object[][] params = {{"shipto", shipto},
							{"orderItems", orderItems}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setPlannedLoadingTimeForOrderItem", params);
		}
		// ]]
		try {
			
			if (orderItems != null) {
				for (Iterator<OrderItemEntity> iterator = orderItems.iterator(); iterator.hasNext();) {
					OrderItemEntity orderItem = iterator.next();				
					SapMaterialEntity sapMaterial = orderItem.getSapMaterial();				
					Long plannedLoadingTimeMin = null;
				
					if (sapMaterial.getMaterialIsCem() || sapMaterial.getMaterialIsAgg()
							|| LogonPackageHandlingUtil.isPallet(sapMaterial)) {
						LoadingpointEntity loadingpoint = orderItem.getLoadingpoint();					
						plannedLoadingTimeMin = getLoadingpointLoadingtimeValue(loadingpoint);
					}
					else if (sapMaterial.getMaterialIsLf()) {
						plannedLoadingTimeMin = getShiptoLoadingtimeLfValue(shipto);
					}
					else {
						// No material type set, we set loading time to 0.
						plannedLoadingTimeMin = 0l;
					}
					
					orderItem.setPlannedLoadingTimeMin(plannedLoadingTimeMin);
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
	 * Returns the shipto's unloadingtime parameter for cement material, if not set, goes for logon default.
	 * @param ShiptoEntity entity
	 * @return
	 */
	public Long getShiptoUnloadingtimeCemValue(ShiptoEntity entity) {
		Long outValue = null;
		LevelOneFallbackStructure values = getShiptoUnloadingtimeCem(entity);
		if ( values.getLevelZeroDefaultValue() != null ) {
			outValue = values.getLevelZeroDefaultValue();
		}
		else if ( values.getLevelOneDefaultValue() != null ) {
			outValue = values.getLevelOneDefaultValue();
		}
		else {
			return outValue = null;
		}
		return outValue;
	}
	
	/**
	 * Returns the shipto's unloadingtime parameter for aggregates, if not set, goes for logon default.
	 * @param ShiptoEntity entity
	 * @return
	 */
	public Long getShiptoUnloadingtimeAggValue(ShiptoEntity entity) {
		Long outValue = null;
		LevelOneFallbackStructure values = getShiptoUnloadingtimeAgg(entity);
		if ( values.getLevelZeroDefaultValue() != null ) {
			outValue = values.getLevelZeroDefaultValue();
		}
		else if ( values.getLevelOneDefaultValue() != null ) {
			outValue = values.getLevelOneDefaultValue();
		}
		else {
			return outValue = null;
		}
		return outValue;
	}
	
	/**
	 * Returns the shipto's unloadingtime parameter for landfill, if not set, goes for logon default.
	 * Note: Currently we use the aggregates fallback for landfill.  
	 * @param ShiptoEntity entity
	 * @return
	 */
	public Long getShiptoUnloadingtimeLfValue(ShiptoEntity shipTo) {
		Long outValue = null;
		
		// We use the aggregates fallback for landfill.
		outValue = getShiptoUnloadingtimeAggValue(shipTo);
		return outValue;
	}
	
	/**
	 * Returns the shipto's unloadingtime parameter for pallet material, if not set, goes for logon default.
	 * @param ShiptoEntity entity
	 * @return
	 */
	public Long getShiptoUnloadingtimePalValue() {
		Long outValue = null;
		LevelOneFallbackStructure values = getShiptoUnloadingtimePal();
		if ( values.getLevelZeroDefaultValue() != null ) {
			outValue = values.getLevelZeroDefaultValue();
		}
		else if ( values.getLevelOneDefaultValue() != null ) {
			outValue = values.getLevelOneDefaultValue();
		}
		else {
			return outValue = null;
		}
		return outValue;
	}
	
	/**
	 * Returns the shipto's loadingtime parameter for lanfill material, if not set, goes for logon default.
	 * @param ShiptoEntity entity
	 * @return
	 */
	public Long getShiptoLoadingtimeLfValue(ShiptoEntity entity) {
		Long outValue = null;
		LevelOneFallbackStructure values = getShiptoLoadingtimeLf(entity);
		if ( values.getLevelZeroDefaultValue() != null ) {
			outValue = values.getLevelZeroDefaultValue();
		}
		else if ( values.getLevelOneDefaultValue() != null ) {
			outValue = values.getLevelOneDefaultValue();
		}
		else {
			return outValue = null;
		}
		return outValue;
	}

	/**
	 * Returns the loadingpoint's unloadingtime parameter, if not set, goes for logon default, and returns that.
	 * @param LoadingpointEntity entity
	 * @return
	 */
	public Long getLoadingpointLoadingtimeValue(LoadingpointEntity entity) {
		Long outValue = null;
		LevelOneFallbackStructure values = getLoadingpointLoadingtime(entity);
		if ( values.getLevelZeroDefaultValue() != null ) {
			outValue = values.getLevelZeroDefaultValue();
		}
		else if ( values.getLevelOneDefaultValue() != null ) {
			outValue = values.getLevelOneDefaultValue();
		}
		else {
			return outValue = null;
		}
		return outValue;
	}	

	/**
	 * Returns the ShiptSilo's unloading time, if not present go for the rules
	 * of ShipTo.
	 * @param ShiptoSiloEntity entity
	 * @return
	 */
	public Long getShiptoSiloUnloadingTimeValue(ShiptoSiloEntity entity) {
		Long outValue = null;
		LevelTwoFallbackStructure values = getShiptoSiloUnloadingTime(entity);
		if ( values.getLevelZeroDefaultValue() != null ) {
			outValue = values.getLevelZeroDefaultValue();
		}
		else if ( values.getLevelOneDefaultValue() != null ) {
			outValue = values.getLevelOneDefaultValue();
		}
		else if ( values.getLevelTwoDefaultValue() != null ){
			outValue = values.getLevelTwoDefaultValue();
		}
		else {			
			return outValue = null;
		}
		return outValue;
	}

	/**
	 * Returns the shipto's  default unloading time values for cement as a LevelOneFallbackStructure.
	 * @param ShiptoEntity entity
	 * @return
	 */
	public DefaultFallbackLogic.LevelOneFallbackStructure getShiptoUnloadingtimeCem(ShiptoEntity entity) {
		LevelOneFallbackStructure values = new LevelOneFallbackStructure();
		if (entity != null) {
			values.levelZeroDefaultValue = entity.getUnloadingTimeCemMin();
		}
		int unloadingTime = configBean.getAppDefaultShiptoUnloadingTimeCEMMin();
		values.levelOneDefaultValue = Long.valueOf(unloadingTime);
		return values;

	}
	
	/**
	 * Returns the shipto's  default unloading time values for aggregates as a LevelOneFallbackStructure.
	 * @param ShiptoEntity entity
	 * @return
	 */
	public DefaultFallbackLogic.LevelOneFallbackStructure getShiptoUnloadingtimeAgg(ShiptoEntity entity) {
		LevelOneFallbackStructure values = new LevelOneFallbackStructure();
		if (entity != null) {
			values.levelZeroDefaultValue = entity.getUnloadingTimeAggMin();
		}
		int unloadingTime = configBean.getAppDefaultShiptoUnloadingTimeAGGMin();
		values.levelOneDefaultValue = Long.valueOf(unloadingTime);
		return values;

	}
	
	/**
	 * Returns the shipto's  default loading time values for landfill as a LevelOneFallbackStructure.
	 * @param ShiptoEntity entity
	 * @return
	 */
	public DefaultFallbackLogic.LevelOneFallbackStructure getShiptoUnloadingtimeLf(ShiptoEntity shipTo) {
		// We use the aggregates fallback for landfill.
		return getShiptoUnloadingtimeAgg(shipTo);
	}
	
	/**
	 * Returns the shipto's  default unloading time values for pallet as a LevelOneFallbackStructure.
	 * @return
	 */
	public DefaultFallbackLogic.LevelOneFallbackStructure getShiptoUnloadingtimePal() {
		LevelOneFallbackStructure values = new LevelOneFallbackStructure();
		int unloadingTime = configBean.getAppDefaultShiptoUnloadingTimePALMin();
		values.levelZeroDefaultValue = Long.valueOf(unloadingTime);
		values.levelOneDefaultValue = 0L;
		return values;
	}
	
	/**
	 * Returns the shipto's  default loading time values for landfill material as a LevelOneFallbackStructure.
	 * @param ShiptoEntity entity
	 * @return
	 */
	public DefaultFallbackLogic.LevelOneFallbackStructure getShiptoLoadingtimeLf(ShiptoEntity entity) {
		LevelOneFallbackStructure values = new LevelOneFallbackStructure();
		if (entity != null) {
			values.levelZeroDefaultValue = entity.getLoadingTimeLfMin();
		}
		int unloadingTime = configBean.getAppDefaultShiptoLoadingTimeLFMin();
		values.levelOneDefaultValue = Long.valueOf(unloadingTime);
		return values;

	}

	/**
	 * Returns the ShiptSilo's unloading time values, if not present go for the rules
	 * of ShipTo.
	 * @param ShiptoSiloEntity entity
	 * @return
	 */
	public DefaultFallbackLogic.LevelTwoFallbackStructure getShiptoSiloUnloadingTime(ShiptoSiloEntity entity) {
		LevelTwoFallbackStructure values = new LevelTwoFallbackStructure();
		if (entity == null) {
			return values;
		}

		values.levelZeroDefaultValue = entity.getUnloadingTime();

		ShiptoEntity shipTo = entity.getShipto();
		if (entity.getSapMaterial() != null) {
			SapMaterialEntity sapMaterialEntity = entity.getSapMaterial();

			LevelOneFallbackStructure shiptoDefaults = new LevelOneFallbackStructure();

			if (sapMaterialEntity.getMaterialIsCem()) {
				shiptoDefaults = getShiptoUnloadingtimeCem(shipTo);
			}
			else if (sapMaterialEntity.getMaterialIsAgg()) {
				shiptoDefaults = getShiptoUnloadingtimeAgg(shipTo);
			}
			else if (sapMaterialEntity.getMaterialIsLf()) {
				// Landfill material unloading time fallback is the same as aggregates
				shiptoDefaults = getShiptoUnloadingtimeAgg(shipTo);
			}

			values.levelOneDefaultValue = shiptoDefaults.levelZeroDefaultValue;
			values.levelTwoDefaultValue = shiptoDefaults.levelOneDefaultValue;
		}
		else {
			// If the ShiptoSiloEntity does not have material set (or the material type is not set), then returning null.
			values.levelZeroDefaultValue = null;
		}

		return values;
	}

	/**
	 * Returns the loadingpoint's loadingtime default values as a LevelOneFallbackStructure.
	 * @param LoadingpointEntity entity
	 * @return
	 */
	public DefaultFallbackLogic.LevelOneFallbackStructure getLoadingpointLoadingtime(LoadingpointEntity loadingPoint) {
		LevelOneFallbackStructure values = new LevelOneFallbackStructure();
		if (loadingPoint != null) {
			values.levelZeroDefaultValue = loadingPoint.getLoadingTime();
		}		
		int loadingTime = configBean.getAppDefaultLoadingpointLoadingTimeMin();
		values.levelOneDefaultValue = Long.valueOf(loadingTime);
		return values;
	}

	public ConfigBean getConfigBean() {
		return configBean;
	}

	public void setConfigBean(ConfigBean configBean) {
		this.configBean = configBean;
	}
}
