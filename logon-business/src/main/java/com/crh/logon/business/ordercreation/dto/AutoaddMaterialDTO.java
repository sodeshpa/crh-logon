/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ordercreation.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;


/**
 * Class holding basic data to initialize order items based on 
 * autoadd material defaults.
 * 
 * @author AdNovum Informatik AG
 */
public class AutoaddMaterialDTO implements Serializable{

	private static final long serialVersionUID = 1L;

	private final PlantBsEntity productionPlant;
	private final SapMaterialEntity sapMaterial;
	private final SapContractItemEntity sapContractItem;
	private final java.math.BigDecimal autoadd1PerNOfUom;

	public AutoaddMaterialDTO(PlantBsEntity productionPlant,
			SapMaterialEntity sapMaterial,
			SapContractItemEntity sapContractItem, BigDecimal autoadd1PerNOfUom) {
		super();
		this.productionPlant = productionPlant;
		this.sapMaterial = sapMaterial;
		this.sapContractItem = sapContractItem;
		this.autoadd1PerNOfUom = autoadd1PerNOfUom;
	}
	
	public PlantBsEntity getProductionPlant() {
		return productionPlant;
	}
	public SapMaterialEntity getSapMaterial() {
		return sapMaterial;
	}
	
	/**
	 * Can be null, as in certain cases services appear
	 * as autoadd materials and those might not have a
	 * contract item.
	 * @return
	 */
	public SapContractItemEntity getSapContractItem() {
		return sapContractItem;
	}
	
	public java.math.BigDecimal getAutoadd1PerNOfUom() {
		return autoadd1PerNOfUom;
	}
}
