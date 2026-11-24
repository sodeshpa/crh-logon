/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.auftrag;

import java.io.Serializable;
import java.util.List;

import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;

/**
 * This bean is for storing material data, which can replace a planning material.
 * It can be used for displaying data on Order Details screen.
 *  
 * @author AdNovum Informatik AG
 */
public class PlanningMaterialBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long sapMaterialId;
	private String sapMaterialName;
	private Long contractItemNo;
	private Long sapContractItemId;
	private String plantCd;
	private String plantName;
	private List <PlantBsEntity> prodPlantList;
	private List<UomTxEntity> uomList;
	private String netQuantityUom;
	
	
	public Long getSapMaterialId() {
		return sapMaterialId;
	}
	public void setSapMaterialId(Long sapMaterialId) {
		this.sapMaterialId = sapMaterialId;
	}
	public String getSapMaterialName() {
		return sapMaterialName;
	}
	public void setSapMaterialName(String sapMaterialName) {
		this.sapMaterialName = sapMaterialName;
	}
	public Long getContractItemNo() {
		return contractItemNo;
	}
	public void setContractItemNo(Long contractItemNo) {
		this.contractItemNo = contractItemNo;
	}
	public Long getSapContractItemId() {
		return sapContractItemId;
	}
	public void setSapContractItemId(Long sapContractItemId) {
		this.sapContractItemId = sapContractItemId;
	}
	public String getPlantCd() {
		return plantCd;
	}
	public void setPlantCd(String plantCd) {
		this.plantCd = plantCd;
	}
	public String getPlantName() {
		return plantName;
	}
	public void setPlantName(String plantName) {
		this.plantName = plantName;
	}
	public List<PlantBsEntity> getProdPlantList() {
		return prodPlantList;
	}
	public void setProdPlantList(List<PlantBsEntity> prodPlantList) {
		this.prodPlantList = prodPlantList;
	}
	public List<UomTxEntity> getUomList() {
		return uomList;
	}
	public void setUomList(List<UomTxEntity> uomList) {
		this.uomList = uomList;
	}
	public String getNetQuantityUom() {
		return netQuantityUom;
	}
	public void setNetQuantityUom(String netQuantityUom) {
		this.netQuantityUom = netQuantityUom;
	}
	
}
