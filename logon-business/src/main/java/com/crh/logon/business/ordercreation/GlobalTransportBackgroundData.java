/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ordercreation;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.crh.logon.business.ordercreation.dto.CommercialCarrierDTO;
import com.crh.logon.business.ordercreation.dto.ExecutingCarrierDTO;
import com.crh.logon.business.ordercreation.dto.PoolDTO;
import com.crh.logon.constants.CallerContext;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.SimpleVehicleTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.WagonTypeTxEntity;

/**
 * Class containing the background data for general transport attributes relations like pool + carrier,uom
 * which should be available for the caller.
 * 
 * All maps, lists, etc. loaded into this structure should be shipto independent.
 * 
 * @author AdNovum Informatik AG
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
public class GlobalTransportBackgroundData implements Serializable{

	private static final long serialVersionUID = 1L;

	private Map<String, String> timeUomMap;
	private List <PoolDTO> pools;
	private Map <Long, PoolTxEntity> poolTxMap;
	private Map <Long, List<CommercialCarrierDTO>> poolCarrierMap;
	private Map <Long, List<ExecutingCarrierDTO>> commercialExecutingCarrierMap;
	private List<DeliveryTypeTxEntity> deliveryTypeTxList;
	
    private Map <Long, TruckTypeTxEntity> truckTypeTxMap;
	private Map <Long, WagonTypeTxEntity> wagonTypeTxMap;
	private Map <Long, SimpleVehicleTypeTxEntity> shipTypeTxMap;
	private Map <Long, SimpleVehicleTypeTxEntity> blockTrainTypeTxMap;


	/**
	 * Maps truck types to transport categories.
	 * Directly corresponds to the table TLGNC_TRUCK_TYPE_TRSP_CAT.
	 */
	// See LOGON-5043.
	private Map <Long,Set<Long>> truckTypeToTrspCat;
	
	public Map<Long, TruckTypeTxEntity> getTruckTypeTxMap() {
		return truckTypeTxMap;
	}

	public void setTruckTypeTxMap(Map<Long, TruckTypeTxEntity> truckTypeTxMap) {
		this.truckTypeTxMap = truckTypeTxMap;
	}

	public Map<Long, WagonTypeTxEntity> getWagonTypeTxMap() {
		return wagonTypeTxMap;
	}

	public void setWagonTypeTxMap(Map<Long, WagonTypeTxEntity> wagonTypeTxMap) {
		this.wagonTypeTxMap = wagonTypeTxMap;
	}
	
	public Map<Long, SimpleVehicleTypeTxEntity> getShipTypeTxMap() {
		return shipTypeTxMap;
	}
	
	public void setShipTypeTxMap(Map<Long, SimpleVehicleTypeTxEntity> shipTypeTxMap) {
		this.shipTypeTxMap = shipTypeTxMap;
	}
	
	public Map<Long, SimpleVehicleTypeTxEntity> getBlockTrainTypeTxMap() {
		return blockTrainTypeTxMap;
	}
	
	public void setBlockTrainTypeTxMap(Map<Long, SimpleVehicleTypeTxEntity> blockTrainTypeTxMap) {
		this.blockTrainTypeTxMap = blockTrainTypeTxMap;
	}

	/**
	 * the usually supported transport categories for all truck types
	 * @return the map that contains for each truck type the transport categories usually supported by this truck
	 */
	public Map<Long, Set<Long>> getTruckTypeToTrspCat() {
		return truckTypeToTrspCat;
	}

	/**
	 * the usually supported transport categories for all truck types
	 * @param truckTypeToTrspCat the truckTypeToTrspCat to set
	 */
	public void setTruckTypeToTrspCat(Map<Long, Set<Long>> truckTypeToTrspCat) {
		this.truckTypeToTrspCat = truckTypeToTrspCat;
	}

	private CallerContext context;

	public CallerContext getContext() {
		return context;
	}

	public void setContext(CallerContext context) {
		this.context = context;
	}

	public void setPools(List<PoolDTO> pools) {
		this.pools = pools;
	}

	public void setPoolTxMap(Map<Long,PoolTxEntity> poolTxMap) {
		this.poolTxMap = poolTxMap;
	}

	public void setPoolCarrierMap(Map<Long, List<CommercialCarrierDTO>> poolCarrierMap) {
		this.poolCarrierMap = poolCarrierMap;
	}

	public void setCommercialExecutingCarrierMap(
			Map<Long, List<ExecutingCarrierDTO>> commercialExecutingCarrierMap) {
		this.commercialExecutingCarrierMap = commercialExecutingCarrierMap;
	}

	public void setDeliveryTypeTxList(List<DeliveryTypeTxEntity> deliveryTypeTxList) {
		this.deliveryTypeTxList = deliveryTypeTxList;
	}

	public List<PoolDTO> getPools() {
		return pools;
	}
	
	
	public Map<Long,PoolTxEntity> getPoolTxMap() {
		return poolTxMap;
	}
		
	public List<DeliveryTypeTxEntity> getDeliveryTypeTxList() {
		return deliveryTypeTxList;
	}

	public Map<Long, List<CommercialCarrierDTO>> getPoolCarrierMap() {
		return poolCarrierMap;
	}

	public Map<Long, List<ExecutingCarrierDTO>> getCommercialExecutingCarrierMap() {
		return commercialExecutingCarrierMap;
	}

	public Map<String, String> getTimeUomMap() {
		return timeUomMap;
	}
	
	public void setTimeUomMap(Map<String, String> timeUomMap) {
		this.timeUomMap = timeUomMap;
	}
}