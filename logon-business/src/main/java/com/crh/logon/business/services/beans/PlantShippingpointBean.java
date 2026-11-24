/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans;

import java.io.Serializable;

/**
 * @author AdNovum Informatik AG
 */
public class PlantShippingpointBean implements Serializable {

	private String plantCd;
	private Long shippingpointId;
	
	public PlantShippingpointBean(String plantCd, long shippingpointId) {
		this.plantCd = plantCd;
		this.shippingpointId = shippingpointId;
	}
	
	public String getPlantCd() {
		return plantCd;
	}
	public void setPlantCd(String plantCd) {
		this.plantCd = plantCd;
	}
	public Long getShippingpointId() {
		return shippingpointId;
	}
	public void setShippingpointId(Long shippingpointId) {
		this.shippingpointId = shippingpointId;
	}
	
	
}
