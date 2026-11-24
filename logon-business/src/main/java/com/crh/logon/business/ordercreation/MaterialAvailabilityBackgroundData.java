/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ordercreation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.ordercreation.dto.LoadingPointDTO;
import com.crh.logon.business.ordercreation.dto.PlantDTO;
import com.crh.logon.business.ordercreation.dto.ShippingpointDTO;
import com.crh.logon.persistence.entities.common.FireqMaterialMixdesignEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.refdata.OrderSpiTxEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;

/**
 * 
 * @author AdNovum Informatik AG
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
public class MaterialAvailabilityBackgroundData implements Serializable{

	private static final long serialVersionUID = 1L;
	
	//the materials for which the whole structure is initialized for
	private List<Long> sapMaterialIds = new ArrayList<Long>();

	public void setSapMaterialIds(List<Long> sapMaterialIds) {
		this.sapMaterialIds = sapMaterialIds;
	}


	public void setShiptoSarea(ShiptoSareaEntity shiptoSarea) {
		this.shiptoSarea = shiptoSarea;
	}


	public void setSilos(List<ShiptoSiloEntity> silos) {
		this.silos = silos;
	}


	public void setMaterialLoadingpointMap(Map<Long, Set<Long>> materialLoadingpointMap) {
		this.materialLoadingpointMap = materialLoadingpointMap;
	}


	public void setMaterialPlantListMap(Map<Long, List<PlantDTO>> materialPlantListMap) {
		this.materialPlantListMap = materialPlantListMap;
	}


	public void setShippingPointLoadingPointMap(
			Map<Long, List<LoadingPointDTO>> shippingPointLoadingPointMap) {
		this.shippingPointLoadingPointMap = shippingPointLoadingPointMap;
	}


	public void setPlantShippingPointMap(Map<String, List<ShippingpointDTO>> plantShippingPointMap) {
		this.plantShippingPointMap = plantShippingPointMap;
	}


	public void setMaterialUomTxListMap(Map<Long, List<UomTxEntity>> materialUomTxListMap) {
		this.materialUomTxListMap = materialUomTxListMap;
	}


	private static final JcanLogger LOG =
		JcanLoggerFactory.getLogger(MaterialAvailabilityBackgroundData.class);

	private ShiptoSareaEntity shiptoSarea;
	
	//data structures storing mainly material id + related lists
	private List <ShiptoSiloEntity> silos = new ArrayList <ShiptoSiloEntity>();
	private Map <Long, Set<Long>> materialLoadingpointMap = new HashMap <Long, Set<Long>>();
	private Map <Long, List<PlantDTO>> materialPlantListMap = new HashMap <Long, List<PlantDTO>> ();
	private Map <Long, List<LoadingPointDTO>> shippingPointLoadingPointMap = new HashMap <Long, List<LoadingPointDTO>>();
	private Map <String, List<ShippingpointDTO>> plantShippingPointMap = new HashMap<String, List<ShippingpointDTO>> ();
	private Map <Long, List<UomTxEntity>> materialUomTxListMap = new HashMap <Long, List<UomTxEntity>> ();
	private Map <Long, List<FireqMaterialMixdesignEntity>> materialMixDesignMap = new HashMap <Long, List<FireqMaterialMixdesignEntity>> ();
	private List<OrderSpiTxEntity> serviceProcessIndicatorList;

	public Map<Long, List<FireqMaterialMixdesignEntity>> getMaterialMixDesignMap() {
		return materialMixDesignMap;
	}


	public void setMaterialMixDesignMap(Map<Long, List<FireqMaterialMixdesignEntity>> materialMixDesign) {
		this.materialMixDesignMap = materialMixDesign;
	}

	
	public List<ShiptoSiloEntity> getValidSilos() {
		return silos;
	}


	public Map<Long, List<UomTxEntity>> getMaterialUomTxListMap() {
		return materialUomTxListMap;
	}

	public List<Long> getSapMaterialIds() {
		return sapMaterialIds;
	}


	public ShiptoSareaEntity getShiptoSarea() {
		return shiptoSarea;
	}


	public List<ShiptoSiloEntity> getSilos() {
		return silos;
	}


	public Map<Long, Set<Long>> getMaterialLoadingpointMap() {
		return materialLoadingpointMap;
	}


	public Map<Long, List<PlantDTO>> getMaterialPlantListMap() {
		return materialPlantListMap;
	}


	public Map<Long, List<LoadingPointDTO>> getShippingPointLoadingPointMap() {
		return shippingPointLoadingPointMap;
	}


	public Map<String, List<ShippingpointDTO>> getPlantShippingPointMap() {
		return plantShippingPointMap;
	}


	public List<OrderSpiTxEntity> getServiceProcessIndicatorList() {
		return serviceProcessIndicatorList;
	}


	public void setServiceProcessIndicatorList(List<OrderSpiTxEntity> serviceProcessIndicatorList) {
		this.serviceProcessIndicatorList = serviceProcessIndicatorList;
	}


}
