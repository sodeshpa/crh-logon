/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;

import java.io.Serializable;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class ShippingPointCreateBean implements Serializable {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ShippingPointCreateBean.class);
	
	private static final long serialVersionUID = 1L;

	private Long shippingpointId;
	private String plantCd;
	private String plantName;
	private String sapShippingpointNo;
	private String shippingpointName;
	
	public Long getShippingpointId() {
		return shippingpointId;
	}

	public void setShippingpointId(Long shippingpointId) {
		this.shippingpointId = shippingpointId;
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
	
	public String getSapShippingpointNo() {
		return sapShippingpointNo;
	}
	
	public void setSapShippingpointNo(String sapShippingpointNo) {
		this.sapShippingpointNo = sapShippingpointNo;
	}
	
	public String getShippingpointName() {
		return shippingpointName;
	}
	
	public void setShippingpointName(String shippingpointName) {
		this.shippingpointName = shippingpointName;
	}
}
