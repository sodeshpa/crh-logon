/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.order.pages;

import java.util.List;

/**
 * @author AdNovum Informatik AG
 */
public class BimodalOrderResult {
	private String custNo;
	
	private String railDeliveryOrderNo;
	
	private List<String> roadDeliveriesOrdersNo;
	
	private String deliveryFromDate;

	/**
	 * @param custNo
	 * @param railPartOrderNo
	 * @param roadPartOrdersNo
	 */
	public BimodalOrderResult(String custNo, String railDeliveryOrderNo, List<String> roadDeliveriesOrdersNo) {
		super();
		this.custNo = custNo;
		this.railDeliveryOrderNo = railDeliveryOrderNo;
		this.roadDeliveriesOrdersNo = roadDeliveriesOrdersNo;
	}

	/**
	 * @return the custNo
	 */
	public String getCustNo() {
		return custNo;
	}

	/**
	 * @param custNo the custNo to set
	 */
	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	/**
	 * @return the railDeliveryOrderNo
	 */
	public String getRailDeliveryOrderNo() {
		return railDeliveryOrderNo;
	}

	/**
	 * @param railDeliveryOrderNo the railDeliveryOrderNo to set
	 */
	public void setRailDeliveryOrderNo(String railDeliveryOrderNo) {
		this.railDeliveryOrderNo = railDeliveryOrderNo;
	}

	/**
	 * @return the roadDeliveriesOrdersNo
	 */
	public List<String> getRoadDeliveriesOrdersNo() {
		return roadDeliveriesOrdersNo;
	}

	/**
	 * @param roadDeliveriesOrdersNo the roadDeliveriesOrdersNo to set
	 */
	public void setRoadDeliveriesOrdersNo(List<String> roadDeliveriesOrdersNo) {
		this.roadDeliveriesOrdersNo = roadDeliveriesOrdersNo;
	}

	/**
	 * @return the deliveryFromDate
	 */
	public String getDeliveryFromDate() {
		return deliveryFromDate;
	}

	/**
	 * @param deliveryFromDate the deliveryFromDate to set
	 */
	public void setDeliveryFromDate(String deliveryFromDate) {
		this.deliveryFromDate = deliveryFromDate;
	}
}
