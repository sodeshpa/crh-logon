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

import com.crh.logon.business.ordercreation.dto.LoadingPointDTO;
import com.crh.logon.business.ordercreation.dto.CommercialCarrierDTO;
import com.crh.logon.business.ordercreation.dto.ExecutingCarrierDTO;
import com.crh.logon.business.ordercreation.dto.PlantDTO;
import com.crh.logon.business.ordercreation.dto.ShippingpointDTO;
import com.crh.logon.business.ordercreation.dto.SupplyPlantDTO;
import com.crh.logon.business.ordercreation.dto.TruckWagonTypeWrapper;
import com.crh.logon.persistence.entities.common.FireqMaterialMixdesignEntity;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.entities.common.ShiptoBilltoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.OrderSpiTxEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;

/**
 * This DTO contains a list of possible entities that can be selected for a order creation or dispokarte restriction creation. 
 * @author AdNovum Informatik AG
 */
public class DeliveryOptionConfigurationDTO implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private List<PlantDTO> prodPlantList = new ArrayList<PlantDTO>();
	private List<SupplyPlantDTO> supplyPlantList = new ArrayList<SupplyPlantDTO>();
	private List<ShippingpointDTO> shippingpointList = new ArrayList<ShippingpointDTO>();
	private List<LoadingPointDTO> loadingpointList = new ArrayList<LoadingPointDTO>();
	private List<PoolTxEntity> poolList = new ArrayList<PoolTxEntity>();
	private List<CommercialCarrierDTO> commercialCarrierList = new ArrayList<CommercialCarrierDTO>();
	private List<ExecutingCarrierDTO> executingCarrierList = new ArrayList<ExecutingCarrierDTO>();
	private List<DeliveryTypeTxEntity> deliveryTypeList = new ArrayList<DeliveryTypeTxEntity>();
	private List<SapBpEntity> payerList = new ArrayList<SapBpEntity>();
	private List<ShiptoBilltoEntity> billtoList = new ArrayList<ShiptoBilltoEntity>();
	private List<UomTxEntity> uomList = new ArrayList<UomTxEntity>();
	private List<ShiptoSiloEntity> siloList = new ArrayList<ShiptoSiloEntity>();
	private List<TruckWagonTypeWrapper> vehicleTypeWrapperList = new ArrayList<TruckWagonTypeWrapper>();
	private List<FireqMaterialMixdesignEntity> materialMixDesignList = new ArrayList<FireqMaterialMixdesignEntity>();
	private List<OrderSpiTxEntity> serviceProcessIndicatorList = new ArrayList<OrderSpiTxEntity>();

	/**
	 * Hardcoded lists for the time unit of measures.
	 */
	private Map<String, String> deliveryTimeToleranceUomMap = new HashMap<String, String>();
	private Map<String, String> deliveryFreqEveryTimeUomMap = new HashMap<String, String>();
	
	
	public Map<String, String> getDeliveryTimeToleranceUomMap() {
		return deliveryTimeToleranceUomMap;
	}

	public void setDeliveryTimeToleranceUomMap(Map<String, String> deliveryTimeToleranceUomMap) {
		this.deliveryTimeToleranceUomMap = deliveryTimeToleranceUomMap;
	}

	public Map<String, String> getDeliveryFreqEveryTimeUomMap() {
		return deliveryFreqEveryTimeUomMap;
	}

	public void setDeliveryFreqEveryTimeUomMap(Map<String, String> deliveryFreqEveryTimeUomMap) {
		this.deliveryFreqEveryTimeUomMap = deliveryFreqEveryTimeUomMap;
	}

	public List<FireqMaterialMixdesignEntity> getMaterialMixDesignList() {
		return materialMixDesignList;
	}

	public void setMaterialMixDesignList(List<FireqMaterialMixdesignEntity> materialMixDesignLis) {
		this.materialMixDesignList = materialMixDesignLis;
	}

	public void setShippingpoinList(List<ShippingpointDTO> shippingpoinList) {
		this.shippingpointList = shippingpoinList;
	}

	public void setLoadingpointList(List<LoadingPointDTO> loadingpointList) {
		final Iterator<LoadingPointDTO> iterator = loadingpointList.iterator();
		Set<Long> shippingpointIds = new HashSet<Long>();
		for (ShippingpointDTO shippingpoint : shippingpointList) {
			Long shippingpointId = shippingpoint.getShippingpointId();
			shippingpointIds.add(shippingpointId);
		}
		while (iterator.hasNext()) {
			LoadingPointDTO loadingpointEntity = (LoadingPointDTO) iterator.next();
			if (!shippingpointIds.contains(loadingpointEntity.getShippingpointId())) {
				iterator.remove();
			}
		}
		this.loadingpointList = loadingpointList;
	}

	public void setPoolList(List<PoolTxEntity> poolList) {
		this.poolList = poolList;
	}
	
	public void setCommercialCarrierList(List<CommercialCarrierDTO> commercialCarrierList) {
		this.commercialCarrierList = commercialCarrierList;
	}

	public void setExecutingCarrierList(List<ExecutingCarrierDTO> executingCarrierList) {
		this.executingCarrierList = executingCarrierList;
	}

	public void setProdPlantList(List<PlantDTO> prodSapMatPlantList) {
		this.prodPlantList = prodSapMatPlantList;
	}


	public void setSupplyPlantList(List<SupplyPlantDTO> supPlantList) {
		this.supplyPlantList = supPlantList;
	}

	public List<PlantDTO> getProdPlantList() {
		return prodPlantList;
	}

	public List<SupplyPlantDTO> getSupplyPlantList() {
		return supplyPlantList;
	}

	public List<ShippingpointDTO> getShippingpoinList() {
		return shippingpointList;
	}

	public List<LoadingPointDTO> getLoadingpointList() {
		return loadingpointList;
	}

	public List<CommercialCarrierDTO> getCommercialCarrierList() {
		return commercialCarrierList;
	}

	public List<ExecutingCarrierDTO> getExecutingCarrierList() {
		return executingCarrierList;
	}

	public List<PoolTxEntity> getPoolList() {
		return poolList;
	}

	public List<SapBpEntity> getPayerList() {
		return payerList;
	}

	public void setPayerList(List<SapBpEntity> payerList) {
		this.payerList = payerList;
	}

	public List<ShiptoBilltoEntity> getBilltoList() {
		return billtoList;
	}

	public void setBilltoList(List<ShiptoBilltoEntity> billtoList) {
		this.billtoList = billtoList;
	}


	public List<ShiptoSiloEntity> getSiloList() {
		return siloList;
	}

	public void setSiloList(List<ShiptoSiloEntity> siloList) {
		this.siloList = siloList;
	}

	public List<TruckWagonTypeWrapper> getVehicleTypeWrapperList() {
		return vehicleTypeWrapperList;
	}

	public void setVehicleTypeWrapperList(List<TruckWagonTypeWrapper> truckWagonTypeWrapperList) {
		this.vehicleTypeWrapperList = truckWagonTypeWrapperList;
	}

	public List<UomTxEntity> getUomList() {
		return uomList;
	}

	public void setUomList(List<UomTxEntity> uomList) {
		this.uomList = uomList;
	}

	public List<DeliveryTypeTxEntity> getDeliveryTypeList() {
		return deliveryTypeList;
	}

	public void setDeliveryTypeList(List<DeliveryTypeTxEntity> deliveryTypeList) {
		this.deliveryTypeList = deliveryTypeList;
	}

	public List<OrderSpiTxEntity> getServiceProcessIndicatorList() {
		return serviceProcessIndicatorList;
	}

	public void setServiceProcessIndicatorList(List<OrderSpiTxEntity> serviceProcessIndicatorList) {
		this.serviceProcessIndicatorList = serviceProcessIndicatorList;
	}

}