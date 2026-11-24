/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.beans;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author AdNovum Informatik AG
 */
public class WagonPreLoadBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Long sapMaterialId;
	private final String prodPlantCd;
	private final BigDecimal tareWeightT;
	private final BigDecimal grossWeightT;
	private final BigDecimal netQuantity;
	private final String netQuantityUomCd;


	/**
	 * @param sapMaterialId not null
	 * @param prodPlantCd not null
	 * @param tareWeightT not null
	 * @param grossWeightT not null
	 */
	public WagonPreLoadBean(Long sapMaterialId, String prodPlantCd, BigDecimal tareWeightT,
			BigDecimal grossWeightT) {
		this.sapMaterialId = sapMaterialId;
		this.prodPlantCd = prodPlantCd;
		this.tareWeightT = tareWeightT;
		this.grossWeightT = grossWeightT;
		this.netQuantity = null;
		this.netQuantityUomCd = null;
		validate();
	}


	/**
	 * @param sapMaterialId not null
	 * @param prodPlantCd not null
	 * @param tareWeightT not null
	 * @param netQuantity not null
	 * @param netQuantityUomCd not null
	 */
	public WagonPreLoadBean(Long sapMaterialId, String prodPlantCd, BigDecimal tareWeightT,
			BigDecimal netQuantity, String netQuantityUomCd) {
		this.sapMaterialId = sapMaterialId;
		this.prodPlantCd = prodPlantCd;
		this.tareWeightT = tareWeightT;
		this.grossWeightT = null;
		this.netQuantity = netQuantity;
		this.netQuantityUomCd = netQuantityUomCd;
		validate();
	}


	private void validate() {
		if (sapMaterialId == null || prodPlantCd == null) {
			throw new IllegalArgumentException("Both the material and the production plant are " 
					+ "mandatory!");
		}
		if (grossWeightT == null && (netQuantity == null || netQuantityUomCd == null)) {
			throw new IllegalArgumentException("Either the gross weight or both the net quantity "
					+ "and the net quantity unit of measure are mandatory!");
		}
	}


	public Long getSapMaterialId() {
		return sapMaterialId;
	}


	public String getProdPlantCd() {
		return prodPlantCd;
	}


	public BigDecimal getTareWeightT() {
		return tareWeightT;
	}


	public BigDecimal getGrossWeightT() {
		return grossWeightT;
	}


	public BigDecimal getNetQuantity() {
		return netQuantity;
	}


	public String getNetQuantityUomCd() {
		return netQuantityUomCd;
	}
}
