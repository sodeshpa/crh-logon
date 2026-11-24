/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils;

import com.crh.logon.persistence.entities.sap.SapMaterialEntity;

/**
 * @author AdNovum Informatik AG
 */
public class MaterialWrapper implements Comparable {

	private String materialDescription;
	private SapMaterialEntity sapMaterialEntity;

	
	public MaterialWrapper(String materialDescription, SapMaterialEntity sapMaterialEntity) {
		this.materialDescription = materialDescription;
		this.sapMaterialEntity = sapMaterialEntity;
	}
	
	public int compareTo(Object other) {
		MaterialWrapper otherBean = (MaterialWrapper) other;
		if ( otherBean != null ) {
			return this.materialDescription.compareToIgnoreCase(otherBean.materialDescription);
		}
		return 1;
	}

	public String getMaterialDescription() {
		return materialDescription;
	}

	public void setMaterialDescription(String materialDescription) {
		this.materialDescription = materialDescription;
	}

	public SapMaterialEntity getSapMaterialEntity() {
		return sapMaterialEntity;
	}

	public void setSapMaterialEntity(SapMaterialEntity sapMaterialEntity) {
		this.sapMaterialEntity = sapMaterialEntity;
	}
}
