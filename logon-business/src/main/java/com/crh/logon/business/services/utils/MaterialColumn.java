/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.math.BigDecimal;

import com.crh.logon.persistence.entities.sap.SapMaterialDescriptionEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;

/**
 * Represents a column for the Adjust Deliveries popup.
 * 
 * Contains the material name and the proposed ordered qty.
 * 
 * @author AdNovum Informatik AG
 */
public class MaterialColumn implements Serializable {

	private static final long serialVersionUID = -5718996061604982313L;
	
	private SapMaterialDescriptionEntity materialDescription;
	private SapMaterialEntity material;
	private BigDecimal orderedQuantity;
	
	public MaterialColumn(SapMaterialDescriptionEntity materialDescription,
			SapMaterialEntity material, BigDecimal orderedQuantity) {
		this.materialDescription = materialDescription;
		this.material = material;
		this.orderedQuantity = orderedQuantity;
	}

	public SapMaterialDescriptionEntity getMaterialDescription() {
		return materialDescription;
	}

	public BigDecimal getOrderedQuantity() {
		return orderedQuantity;
	}

	public SapMaterialEntity getMaterial() {
		return material;
	}
	
	public void setOrderedQuantity(BigDecimal orderedQuantity) {
		this.orderedQuantity = orderedQuantity;
	}
	
}
