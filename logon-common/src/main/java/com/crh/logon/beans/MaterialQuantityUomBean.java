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
public class MaterialQuantityUomBean implements Serializable {

	private static final long serialVersionUID = 8365534576865101640L;
	
	private final Long sapMaterialId;
	private final BigDecimal quantity;
	private final String uomCd;
	
	public MaterialQuantityUomBean(Long sapMaterialId, BigDecimal quantity, String uomCd) {
		this.sapMaterialId = sapMaterialId;
		this.quantity = quantity;
		this.uomCd = uomCd;
	}

	public Long getSapMaterialId() {
		return sapMaterialId;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public String getUomCd() {
		return uomCd;
	}
}
