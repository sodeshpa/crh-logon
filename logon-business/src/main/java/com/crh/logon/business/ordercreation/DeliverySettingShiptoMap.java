/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ordercreation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.business.ordercreation.dto.SimpleVehicleTypeWrapper;
import com.crh.logon.business.ordercreation.dto.TruckTypeWrapper;
import com.crh.logon.business.ordercreation.dto.WagonTypeWrapper;
import com.crh.logon.constants.CallerContext;
import com.crh.logon.constants.ShiptoSalesAreaError;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;

public class DeliverySettingShiptoMap extends InvalidMapSupport implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Map <Long, DeliverySettingSalesAreaMap> sareaMaps = new HashMap<Long, DeliverySettingSalesAreaMap>(); // Long sareaId, DeliverySettingSalesAreaMap 

	private ShiptoEntity shipto;
	private SapBpEntity soldTo;
	private GlobalTransportBackgroundData globalTransportData;
	private List<TruckTypeWrapper> truckTypeWrappers;
	private List<WagonTypeWrapper> wagonTypeWrappers;
	private List<SimpleVehicleTypeWrapper> shipTypeWrappers;
	private List<SimpleVehicleTypeWrapper> blockTrainTypeWrappers;
	
	private final CallerContext caller;
	
	public DeliverySettingShiptoMap(CallerContext caller) {
		this.caller = caller;
	}

	/**
	 * Returns the {@link DeliverySettingSalesAreaMap} for the given {@link SareaEntity}'s id.
	 * 
	 * @param sareaId
	 * @return
	 */
	public DeliverySettingSalesAreaMap getSalesAreaMapForSareaId(Long sareaId) {		
		return sareaMaps.get(sareaId);
	}


	@Override
	protected void updateInvalidMap(Object obj, ShiptoSalesAreaError message) {
		super.updateInvalidMap(obj, message);
		
		/*
		 * Usually the errors are required to be checked/shown for a specific Sales Area only.  But now we
		 * have flags for Customer Credit Block not only in Sales Area Data, but General Data too. Hence, let
		 * the Sales Areas report the errors of the Ship-To too. (For now this should be okay.)
		 */
		if (obj instanceof ShiptoEntity) {
			for (DeliverySettingSalesAreaMap sareaMap: sareaMaps.values()) {
				final ShiptoSareaEntity shiptoSarea = sareaMap.getShiptoSarea();
				sareaMap.updateInvalidMap(shiptoSarea, message);
			}
		}
	}


	/**
	 * Returns the map containing all errors which were identified while validating the sales area maps.
	 * The map will contain ALL errors of ALL initialized sarea objects.
	 * 
	 * @return
	 */
	@Override
	 public Map<Object, List<ShiptoSalesAreaError>> getInvalidMap() {
		 
		final Map<Object, List<ShiptoSalesAreaError>> invalidMap = new HashMap<Object, List<ShiptoSalesAreaError>>();

		invalidMap.putAll(super.getInvalidMap());
		for (DeliverySettingSalesAreaMap deliverySettingSalesAreaMap : sareaMaps.values()) {
			invalidMap.putAll(deliverySettingSalesAreaMap.getInvalidMap());
		}
		return invalidMap;
	 }


	 public ShiptoEntity getShipto() {
		 return shipto;
	 }

	 public SapBpEntity getSoldTo() {
		 return soldTo;
	 }

	 /**
	  * Returns the material + service delivery settings valid for ALL sales areas.
	  * 
	  * @return
	  */
	 public Collection <DeliverySettingDTO> getPossibleDeliverySettings() {
		 final Set<DeliverySettingDTO> set = new HashSet<DeliverySettingDTO>();
		 set.addAll(getMaterialDeliverySettingList());
		 set.addAll(getServiceDeliverySettingList());
		 return set;
	 }
	 /**
	  * Returns the material delivery settings valid for ALL sales areas.
	  * 
	  * @return
	  */
	 public Collection <DeliverySettingDTO> getMaterialDeliverySettingList() {
		 final Set<DeliverySettingDTO> set = new HashSet<DeliverySettingDTO>();
		 for (DeliverySettingSalesAreaMap deliverySettingSalesAreaMap : sareaMaps.values()) {				
			 set.addAll(deliverySettingSalesAreaMap.getMaterialDeliverySettingList());
		 }
		 List<DeliverySettingDTO> delSettingsWithAutoPayerFlagsAndPresetPayer = new ArrayList<DeliverySettingDTO>();
		 for (DeliverySettingDTO delSetting: set) {
			if (!delSetting.isAutoPayerSettingValid()) {
				delSettingsWithAutoPayerFlagsAndPresetPayer.add(delSetting);
			}
		 }
		 set.removeAll(delSettingsWithAutoPayerFlagsAndPresetPayer);
		 return set;
	 }
	 
	 /**
	  * Returns the material DEFAULT delivery settings valid for ALL sales areas.
	  * 
	  * @return
	  */
	 public Collection <DeliverySettingDTO> getMaterialRestrictionDeliverySettingList() {
		 final Set<DeliverySettingDTO> set = new HashSet<DeliverySettingDTO>();
		 for (DeliverySettingSalesAreaMap deliverySettingSalesAreaMap : sareaMaps.values()) {				
			 set.addAll(deliverySettingSalesAreaMap.getMaterialRestrictionDeliverySettings().getPossibleDeliverySettings());
		 }
		 return set;
	 }
	 
	 /**
	  * Returns the material DEFAULT delivery settings valid for ALL sales areas.
	  * 
	  * @return
	  */
	 public Collection <DeliverySettingDTO> getServiceRestrictionDeliverySettingList() {
		 final Set<DeliverySettingDTO> set = new HashSet<DeliverySettingDTO>();
		 for (DeliverySettingSalesAreaMap deliverySettingSalesAreaMap : sareaMaps.values()) {				
			 set.addAll(deliverySettingSalesAreaMap.getServiceRestrictionDeliverySettings().getPossibleDeliverySettings());
		 }
		 return set;
	 }
	 
	 /**
	  * Returns the service delivery settings valid for ALL sales areas.
	  * 
	  * @return
	  */
	 public Collection <DeliverySettingDTO> getServiceDeliverySettingList() {
		 final Set<DeliverySettingDTO> set = new HashSet<DeliverySettingDTO>();
		 for (DeliverySettingSalesAreaMap deliverySettingSalesAreaMap : sareaMaps.values()) {				
			 set.addAll(deliverySettingSalesAreaMap.getServiceDeliverySettingList());
		 }
		 return set;
	 }


	public Map<Long, DeliverySettingSalesAreaMap> getSareaMaps() {
		return sareaMaps;
	}
	
	public void setSareaMaps(Map<Long, DeliverySettingSalesAreaMap> sareaMaps) {
		this.sareaMaps = sareaMaps;
	}
	
	public void setShipto(ShiptoEntity shipto) {
		this.shipto = shipto;
	}
	
	public void setSoldTo(SapBpEntity soldTo) {
		this.soldTo = soldTo;
	}
	
	public void setGlobalTransportData(GlobalTransportBackgroundData globalTransportData) {
		this.globalTransportData = globalTransportData;
	}
	
	public void setTruckTypeWrappers(List<TruckTypeWrapper> truckTypeWrappers) {
		this.truckTypeWrappers = truckTypeWrappers;
	}
	
	public List<TruckTypeWrapper> getTruckTypeWrappers() {
		return truckTypeWrappers;
	}
	
	public void setWagonTypeWrappers(List<WagonTypeWrapper> wagonTypeWrappers) {
		this.wagonTypeWrappers = wagonTypeWrappers;
	}
	
	public List<WagonTypeWrapper> getWagonTypeWrappers() {
		return wagonTypeWrappers;
	}
	
	public List<SimpleVehicleTypeWrapper> getShipTypeWrappers() {
		return shipTypeWrappers;
	}
	
	public void setShipTypeWrappers(List<SimpleVehicleTypeWrapper> shipTypeWrappers) {
		this.shipTypeWrappers = shipTypeWrappers;
	}
	
	public List<SimpleVehicleTypeWrapper> getBlockTrainTypeWrappers() {
		return blockTrainTypeWrappers;
	}
	
	public void setBlockTrainTypeWrappers(List<SimpleVehicleTypeWrapper> blockTrainTypeWrappers) {
		this.blockTrainTypeWrappers = blockTrainTypeWrappers;
	}

	/**
	  * Returns the delivery setting for the given composite identifier. 
	  * @param s
	 * @param source
	  * @return
	  */
	 public DeliverySettingDTO getDeliverySettingByCompositeIdentifier(String s) {
		 for (DeliverySettingSalesAreaMap deliverySettingSalesAreaMap : sareaMaps.values()) {				
			 final DeliverySettingDTO setting = deliverySettingSalesAreaMap.getDeliverySettingByCompositeIdentifier(s);
			 if (setting != null) {
				 return setting;
			 }
		 }
		 return null;
	 }
	 
	 public DeliverySettingDTO getMaterialDeliverySettingByCompositeIdentifier(String s) {
		 for (DeliverySettingSalesAreaMap deliverySettingSalesAreaMap : sareaMaps.values()) {				
			 final DeliverySettingDTO setting = deliverySettingSalesAreaMap.getMaterialDeliverySettingByCompositeIdentifier(s);
			 if (setting != null) {
				 return setting;
			 }
		 }
		 return null;
	 }
	 
	 public DeliverySettingDTO getServiceDeliverySettingByCompositeIdentifier(String s) {
		 for (DeliverySettingSalesAreaMap deliverySettingSalesAreaMap : sareaMaps.values()) {				
			 DeliverySettingDTO setting = deliverySettingSalesAreaMap.getServiceDeliverySettingByCompositeIdentifier(s);
			 if (setting != null) {
				 return setting;
			 }
		 }
		 return null;
	 }

	 public GlobalTransportBackgroundData getGlobalTransportData() {
		 return globalTransportData;
	 }

	 /**
	  * Validate all the sales areas initialized for this shipto.
	  * @param deliveryService
	  */
	public void validateSalesAreas() {
		 boolean allSareaBlockedToShipto = true;
		 for (DeliverySettingSalesAreaMap salesAreaMap : sareaMaps.values()) {
			 salesAreaMap.validateSapBpSareaRelatedData(true);
			 
			 ShiptoSalesAreaFinancialbackgroundData financialData = salesAreaMap.getFinancialbackgroundData(); 
			 allSareaBlockedToShipto &= financialData.isSoldtoSapBpSalesAreaBlocked();
			 
			 final DeliverySettingProductMap productMap = salesAreaMap.getMaterialRestrictionDeliverySettings();
			 productMap.validateDeliverySettings();
			 
			 final DeliverySettingProductMap serviceMap = salesAreaMap.getServiceRestrictionDeliverySettings();
			 serviceMap.validateDeliverySettings();
		}

		increaseSalesAreaBlockLevelForShipto(allSareaBlockedToShipto);
	 }
	 
	 
	/**
	 * @return the caller
	 */
	public CallerContext getCaller() {
		return caller;
	}
	
	/**
	 * Increase the warning level of the shipTo based on the 
	 * belonging sales areas block information.
	 * If all belonging sales are are blocked for the Shipto,
	 * shipto warning message will be added and sales area level
	 * warnings will be removed from the errors list.
	 * 
	 *  @param allSareaBlockedToShipto If true, all sareas are blocked
	 *  for this shipto object.
	 * 
	 * */
	private void increaseSalesAreaBlockLevelForShipto(boolean allSareaBlockedToShipto) {
		if(allSareaBlockedToShipto) {
			
			//remove block_some warning from belonging sales areas
			for (DeliverySettingSalesAreaMap salesAreaMap : sareaMaps.values()) {
				ShiptoSareaEntity salesAreaShipto = salesAreaMap.getShiptoSarea();
				List<ShiptoSalesAreaError> errorsOfShiptoSalesArea = salesAreaMap.getInvalidMap().get(salesAreaShipto);
				errorsOfShiptoSalesArea.remove(ShiptoSalesAreaError.BLOCK_SOME_SALES_AREA);
			}
			
			//add general warning to shipTo because every sales area is blocked
			updateInvalidMap(shipto, ShiptoSalesAreaError.BLOCK_ALL_SALES_AREA);
		}
	} 
	
}
