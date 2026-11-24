/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.auftrag;

import java.io.Serializable;

/**
 * @author AdNovum Informatik AG
 */
public class CustOrderTabOrderItemBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private boolean serviceMaterial;
	private String materialDesc;
	private String orderedQuantity;
	private String orderedUom;
	private String loadedQuantity;
	private String loadedUom;
	private String truckPlateNo;
	
	public boolean isServiceMaterial() {
		return serviceMaterial;
	}
	
	public void setServiceMaterial(boolean serviceMaterial) {
		this.serviceMaterial = serviceMaterial;
	}
	
	public String getMaterialDesc() {
		return materialDesc;
	}
	
	public void setMaterialDesc(String materialDesc) {
		this.materialDesc = materialDesc;
	}
	
	public String getOrderedQuantity() {
		return orderedQuantity;
	}
	
	public void setOrderedQuantity(String orderedQuantity) {
		this.orderedQuantity = orderedQuantity;
	}
	
	public String getOrderedUom() {
		return orderedUom;
	}
	
	public void setOrderedUom(String orderedUom) {
		this.orderedUom = orderedUom;
	}
	
	public String getLoadedQuantity() {
		return loadedQuantity;
	}
	
	public void setLoadedQuantity(String loadedQuantity) {
		this.loadedQuantity = loadedQuantity;
	}
	
	public String getLoadedUom() {
		return loadedUom;
	}

	public void setLoadedUom(String loadedUom) {
		this.loadedUom = loadedUom;
	}

	public String getTruckPlateNo() {
		return truckPlateNo;
	}

	public void setTruckPlateNo(String truckPlateNo) {
		this.truckPlateNo = truckPlateNo;
	}

}
