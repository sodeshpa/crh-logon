/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class ShippingPointSendSapSpBean {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ShippingPointSendSapSpBean.class);
	
	private static final long serialVersionUID = 1L;

	private String plantCd;
	private String shippingpointId;
	private String shippingpointName;
	private String sapShippingpointNo;
	
	public String getPlantCd() {
		return plantCd;
	}
	
	public void setPlantCd(String plantCd) {
		this.plantCd = plantCd;
	}
	
	public String getShippingpointId() {
		return shippingpointId;
	}
	
	public void setShippingpointId(String shippingpointId) {
		this.shippingpointId = shippingpointId;
	}

	public String getShippingpointName() {
		return shippingpointName;
	}

	public void setShippingpointName(String shippingpointName) {
		this.shippingpointName = shippingpointName;
	}

	public String getSapShippingpointNo() {
		return sapShippingpointNo;
	}

	public void setSapShippingpointNo(String sapShippingpointNo) {
		this.sapShippingpointNo = sapShippingpointNo;
	}
}
