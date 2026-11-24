/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ordercreation;

import java.io.Serializable;
import java.util.Map;

import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.MaterialPackageTxEntity;
import com.crh.logon.persistence.entities.refdata.SapContractTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.views.SapMaterialDescEntity;

/**
 * Class is supposed to resolve language dependent fields of the delivery settings of the provided
 * product map. 
 * @author AdNovum Informatik AG
 */
public class DeliverySettingLanguageDependentResolver implements Serializable {
	
	private  Map<Long, SapMaterialDescEntity> materialDescriptionMap;
	private  Map<Long, DeliveryTypeTxEntity> delivTypeTxMap;
	private  Map<String, MaterialPackageTxEntity> materialPackageTxMap;
	private  Map<Long, PoolTxEntity> poolTxMap;
	private  Map<String, UomTxEntity> uomTxMap;
	private Map<String, SapContractTypeTxEntity> contractTypeTxMap;
	
	public void setMaterialDescriptionMap(Map<Long, SapMaterialDescEntity> materialDescriptionMap) {
		this.materialDescriptionMap = materialDescriptionMap;
	}


	public void setDelivTypeTxMap(Map<Long, DeliveryTypeTxEntity> delivTypeTxMap) {
		this.delivTypeTxMap = delivTypeTxMap;
	}


	public void setMaterialPackageTxMap(Map<String, MaterialPackageTxEntity> materialPackageTxMap) {
		this.materialPackageTxMap = materialPackageTxMap;
	}


	public void setPoolTxMap(Map<Long, PoolTxEntity> poolTxMap) {
		this.poolTxMap = poolTxMap;
	}


	public void setUomTxMap(Map<String, UomTxEntity> uomTxMap) {
		this.uomTxMap = uomTxMap;
	}
	
	public void setContractTypeTxMap(Map<String, SapContractTypeTxEntity> contractTypeTxMap){
		this.contractTypeTxMap = contractTypeTxMap;
	}

	
	public Map<Long, SapMaterialDescEntity> getMaterialDescriptionMap() {
		return materialDescriptionMap;
	}


	public Map<Long, DeliveryTypeTxEntity> getDelivTypeTxMap() {
		return delivTypeTxMap;
	}


	public Map<String, MaterialPackageTxEntity> getMaterialPackageTxMap() {
		return materialPackageTxMap;
	}


	public Map<Long, PoolTxEntity> getPoolTxMap() {
		return poolTxMap;
	}


	public Map<String, UomTxEntity> getUomTxMap() {
		return uomTxMap;
	}
	
	public Map<String, SapContractTypeTxEntity> getContractTypeTxMap(){
		return contractTypeTxMap;
	}
	
}
