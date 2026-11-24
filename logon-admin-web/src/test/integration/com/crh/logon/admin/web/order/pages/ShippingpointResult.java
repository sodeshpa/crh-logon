/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.order.pages;

/**
 * @author AdNovum Informatik AG
 */
public class ShippingpointResult {
	private String shippingPointSapNo;
	private String shippingPointName;
	/**
	 * @return the shippingPointSapNo
	 */
	public String getShippingPointSapNo() {
		return shippingPointSapNo;
	}
	/**
	 * @param shippingPointSapNo the shippingPointSapNo to set
	 */
	public void setShippingPointSapNo(String shippingPointSapNo) {
		this.shippingPointSapNo = shippingPointSapNo;
	}
	/**
	 * @return the shippingPointName
	 */
	public String getShippingPointName() {
		return shippingPointName;
	}
	/**
	 * @param shippingPointName the shippingPointName to set
	 */
	public void setShippingPointName(String shippingPointName) {
		this.shippingPointName = shippingPointName;
	}
	/**
	 * @param shippingPointSapNo
	 * @param shippingPointName
	 */
	public ShippingpointResult(String shippingPointSapNo, String shippingPointName) {
		super();
		this.shippingPointSapNo = shippingPointSapNo;
		this.shippingPointName = shippingPointName;
	}
}
