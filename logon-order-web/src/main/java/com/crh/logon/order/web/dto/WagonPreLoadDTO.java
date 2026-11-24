/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.crh.logon.persistence.entities.sap.SapMaterialAltUomEntity;
import com.crh.logon.persistence.entities.views.SapMaterialPlantDescEntity;

/**
 * Simple DTO class to hold and transfer wagon pre-load data in the web tier.
 * 
 * @author AdNovum Informatik AG
 */
public class WagonPreLoadDTO implements Serializable {	
	
	private static final long serialVersionUID = 1L;
	
	private Long sapMaterialId;
	private String prodPlantCd;
	private BigDecimal tareWeightT;
	private BigDecimal grossWeightT;
	private BigDecimal netQuantity;
	private String netQuantityUomCd;
	
	/** This hold the possible production plants to choose for the "preload row". */
	private List<SapMaterialPlantDescEntity> prodPlants;
	
	/** This hold the possible alt UoM-s to choose for the "preload row". */
	private List<SapMaterialAltUomEntity> sapMaterialAltUoms;
	
	public WagonPreLoadDTO() {
		prodPlants = new ArrayList<SapMaterialPlantDescEntity>();
		sapMaterialAltUoms = new ArrayList<SapMaterialAltUomEntity>();
	}

	public Long getSapMaterialId() {
		return sapMaterialId;
	}

	public void setSapMaterialId(Long sapMaterialId) {
		this.sapMaterialId = sapMaterialId;
	}

	public String getProdPlantCd() {
		return prodPlantCd;
	}

	public void setProdPlantCd(String prodPlantCd) {
		this.prodPlantCd = prodPlantCd;
	}

	public BigDecimal getTareWeightT() {
		return tareWeightT;
	}

	public void setTareWeightT(BigDecimal tareWeightT) {
		this.tareWeightT = tareWeightT;
	}

	public BigDecimal getGrossWeightT() {
		return grossWeightT;
	}

	public void setGrossWeightT(BigDecimal grossWeightT) {
		this.grossWeightT = grossWeightT;
	}

	public BigDecimal getNetQuantity() {
		return netQuantity;
	}

	public void setNetQuantity(BigDecimal netQuantity) {
		this.netQuantity = netQuantity;
	}

	public String getNetQuantityUomCd() {
		return netQuantityUomCd;
	}

	public void setNetQuantityUomCd(String netQuantityUomCd) {
		this.netQuantityUomCd = netQuantityUomCd;
	}

	public List<SapMaterialPlantDescEntity> getProdPlants() {
		return prodPlants;
	}

	public void setProdPlants(List<SapMaterialPlantDescEntity> prodPlants) {
		this.prodPlants = prodPlants;
	}

	public List<SapMaterialAltUomEntity> getSapMaterialAltUoms() {
		return sapMaterialAltUoms;
	}

	public void setSapMaterialAltUoms(List<SapMaterialAltUomEntity> sapMaterialAltUoms) {
		this.sapMaterialAltUoms = sapMaterialAltUoms;
	}
}
