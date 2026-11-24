/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;

import com.crh.logon.persistence.entities.common.MatShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.views.SapMaterialPlantDescEntity;

/**
 * @author AdNovum Informatik AG
 */
public class StorageLocationBean {
	// Entities with original data
	private ShippingpointEntity shippingpoint; 
	private SapMaterialPlantDescEntity sapMaterialPlantDesc;
	private MatShippingpointEntity matShippingpoint;
	
	// Edited data shown on the screen
	private boolean storageLocationIsDefault;
	private String storageLocation;
	private boolean storageLocationWasSet = false; 
	
	public boolean getStorageLocationIsDefault() {
		if (storageLocationWasSet) {
			return storageLocationIsDefault;
		}
		else {
			return storageLocationIsDefault = matShippingpoint == null;
		}
	}
	
	public void setStorageLocationIsDefault(boolean storageLocationIsDefault) {
		this.storageLocationIsDefault = storageLocationIsDefault;
	}
	
	public boolean getStorageLocationIsDefaultDisabled() {
		boolean isDefaultDisabled = false;
		
		if (matShippingpoint != null) {
			if (matShippingpoint.getVarHandlingCostMat() != null ||
					matShippingpoint.getFutureVarHandlingCostMat() != null ||
					matShippingpoint.getFutureValidFromDat() != null ||
					matShippingpoint.getCurrency() != null) {
				isDefaultDisabled = true;
			}
		}
		
		return isDefaultDisabled;
	}
	
	public String getStorageLocation() {
		String storageLocation = "";
		
		if (storageLocationWasSet) {
			storageLocation = this.storageLocation;
		}
		else {
			if (matShippingpoint == null) {
				storageLocation = shippingpoint.getStorageLocation();
			}
			else {
				storageLocation = matShippingpoint.getStorageLocation();
			}
		}
		
		return this.storageLocation = storageLocation;
	}
	
	public void setStorageLocation(String storageLocation) {
		this.storageLocation = storageLocation;
		storageLocationWasSet = true;
	}

	public boolean getStorageLocationReadOnly() {
		return getStorageLocationIsDefault();
	}
	
	public ShippingpointEntity getShippingpoint() {
		return shippingpoint;
	}

	public void setShippingpoint(ShippingpointEntity shippingpoint) {
		this.shippingpoint = shippingpoint;
	}

	public SapMaterialPlantDescEntity getSapMaterialPlantDesc() {
		return sapMaterialPlantDesc;
	}

	public void setSapMaterialPlantDesc(SapMaterialPlantDescEntity sapMaterialPlantDesc) {
		this.sapMaterialPlantDesc = sapMaterialPlantDesc;
	}

	public MatShippingpointEntity getMatShippingpoint() {
		return matShippingpoint;
	}
	
	public void setMatShippingpoint(MatShippingpointEntity matShippingpoint) {
		this.matShippingpoint = matShippingpoint;
	}
}
