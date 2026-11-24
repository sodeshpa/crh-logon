/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.views.BimodalRailOrderEntity;
import com.crh.logon.utils.time.DateTimeUtils;

/**
 * Stores additional information for BimodalRailOrderEntity objects, that we use to display the list of
 * wagons on the Order Taking screen when a user selects the bimodal rail order part for the bimodal truck
 * order part.
 * 
 * Also used on bimodal rail monitoring screen, which is mainly for the same purpose, but has filtering 
 * possibility.
 * 
 * Note that we also use this wrapper to create new entries in that list, with which a user can create
 * a new bimodal rail order part (for which there will be no entity data actually, but restriction data
 * from the DispoCard).
 * 
 * @author AdNovum Informatik AG
 */
public class BimodalRailOrderWrapper implements Serializable {

	private BimodalRailOrderEntity entity;
	private String depTruckOrderDelivery;
	private String remainingLoad;
	private String dedicatedShiptoName;
	private Map<Long, String> dedicatedShiptoMap = new HashMap<Long, String>();
	private Long bimodalShiptoSareaRestrId;
	private String bimodalShiptoName;
	private String bimodalSoldtoSapId;
	private String bimodalSoldtoName;
	private Double railQty;
	private String actualDelivery;
	
	public BimodalRailOrderWrapper(BimodalRailOrderEntity entity) {
		this.entity = entity;
		depTruckOrderDelivery = "-";
	}

	public BimodalRailOrderEntity getEntity() {
		return entity;
	}

	public void setEntity(BimodalRailOrderEntity entity) {
		this.entity = entity;
	}

	public String getDepTruckOrderDelivery() {
		return depTruckOrderDelivery;
	}

	public void setDepTruckOrderDelivery(String depTruckOrderDelivery) {
		this.depTruckOrderDelivery = depTruckOrderDelivery;
	}

	public String getRemainingLoad() {
		return remainingLoad;
	}

	public void setRemainingLoad(String remainingLoad) {
		this.remainingLoad = remainingLoad;
	}

	public String getDedicatedShiptoName() {
		return dedicatedShiptoName;
	}

	public void setDedicatedShiptoName(String dedicatedShiptoName) {
		this.dedicatedShiptoName = dedicatedShiptoName;
	}
	
	public Long getBimodalShiptoSareaRestrId() {
		return bimodalShiptoSareaRestrId;
	}

	public void setBimodalShiptoSareaRestrId(Long bimodalShiptoSareaRestrId) {
		this.bimodalShiptoSareaRestrId = bimodalShiptoSareaRestrId;
	}

	public String getBimodalShiptoName() {
		return bimodalShiptoName;
	}

	public void setBimodalShiptoName(String bimodalShiptoName) {
		this.bimodalShiptoName = bimodalShiptoName;
	}

	public String getBimodalSoldtoSapId() {
		return bimodalSoldtoSapId;
	}

	public void setBimodalSoldtoSapId(String bimodalSoldtoSapId) {
		this.bimodalSoldtoSapId = bimodalSoldtoSapId;
	}

	public String getBimodalSoldtoName() {
		return bimodalSoldtoName;
	}

	public void setBimodalSoldtoName(String bimodalSoldtoName) {
		this.bimodalSoldtoName = bimodalSoldtoName;
	}

	public Double getRailQty() {
		return railQty;
	}

	public void setRailQty(Double railQty) {
		this.railQty = railQty;
	}

	public String getIdleTime() {
		final Long wagonIdleTimeMin = getEntity().getWagonIdleTimeMin();
		if ( wagonIdleTimeMin != null && ((getEntity().getOrderStatusCd().longValue() == Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT) ||
				(getEntity().getOrderStatusCd().longValue() == Constant.ORDER_STATUS_UNLOADING) ||
				(getEntity().getOrderStatusCd().longValue() == Constant.ORDER_STATUS_DELIVERED) )) {			
			return DateTimeUtils.getFormattedTimeLengthFromMillis(wagonIdleTimeMin*60000L);
		}
		else {
			return "";	
		}
		
		
	}
	
	public String getDedicatedShiptoNames() {
		String dedicatedShiptoNames = "";
		int i = 0;
		for (Map.Entry<Long, String> entry : dedicatedShiptoMap.entrySet() ) {
			 
			if ( i < dedicatedShiptoMap.size()-1 ) {
				dedicatedShiptoNames +=  entry.getValue() + " (+) "; 
			}
			else {
				dedicatedShiptoNames += entry.getValue();	
			}
			i++;
		}
		return dedicatedShiptoNames;
	}

	public void setDedicatedShiptoMap(Map<Long, String> dedicatedShiptoMap) {
		this.dedicatedShiptoMap = dedicatedShiptoMap;
	}

	public String getActualDelivery() {
		return actualDelivery;
	}

	public void setActualDelivery(String actualDelivery) {
		this.actualDelivery = actualDelivery;
	}
}
