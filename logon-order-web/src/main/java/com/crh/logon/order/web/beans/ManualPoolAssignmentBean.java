/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.beans;


/**
 * @author AdNovum Informatik AG
 */
public class ManualPoolAssignmentBean {
    
	private String manPoolAssignmentListId;
	private Long orderId;
    private String soldtoName1;
    private String shiptoName1;
	private Long sapBpId;
    private String materialTxt;
    private Long orderNumber;
    private String deliveryTypeTxt;
    private String shippingpointName;
    private String deliveryFrom;
    private String deliveryTo;
    private Boolean autoPoolAssignmentFlag;
    private Long poolId;
    private String poolName;
    private Boolean autoCarrierAssignmentFlag;
    private Long commCarrierId;
    private String commCarrierName;
    
	public String getManPoolAssignmentListId() {
		return manPoolAssignmentListId;
	}
	public void setManPoolAssignmentListId(String manPoolAssignmentListId) {
		this.manPoolAssignmentListId = manPoolAssignmentListId;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public String getSoldtoName1() {
		return soldtoName1;
	}
	public void setSoldtoName1(String soldtoName1) {
		this.soldtoName1 = soldtoName1;
	}
	public String getShiptoName1() {
		return shiptoName1;
	}
	public void setShiptoName1(String shiptoName1) {
		this.shiptoName1 = shiptoName1;
	}
	public void setSapBpId(Long sapBpId) {
		this.sapBpId = sapBpId;
	}
	public Long getSapBpId() {
		return sapBpId;
	}
	public String getMaterialTxt() {
		return materialTxt;
	}
	public void setMaterialTxt(String materialTxt) {
		this.materialTxt = materialTxt;
	}
	public Long getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getDeliveryTypeTxt() {
		return deliveryTypeTxt;
	}
	public void setDeliveryTypeTxt(String deliveryTypeTxt) {
		this.deliveryTypeTxt = deliveryTypeTxt;
	}
	public String getShippingpointName() {
		return shippingpointName;
	}
	public void setShippingpointName(String shippingpointName) {
		this.shippingpointName = shippingpointName;
	}
	public String getDeliveryFrom() {
		return deliveryFrom;
	}
	public void setDeliveryFrom(String deliveryFrom) {
		this.deliveryFrom = deliveryFrom;
	}
	public String getDeliveryTo() {
		return deliveryTo;
	}
	public void setDeliveryTo(String deliveryTo) {
		this.deliveryTo = deliveryTo;
	}
	public Boolean getAutoPoolAssignmentFlag() {
		return autoPoolAssignmentFlag;
	}
	public void setAutoPoolAssignmentFlag(Boolean autoPoolAssignmentFlag) {
		this.autoPoolAssignmentFlag = autoPoolAssignmentFlag;
	}
	public Long getPoolId() {
		return poolId;
	}
	public void setPoolId(Long poolId) {
		this.poolId = poolId;
	}
	public String getPoolName() {
		return poolName;
	}
	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}
	public Boolean getAutoCarrierAssignmentFlag() {
		return autoCarrierAssignmentFlag;
	}
	public void setAutoCarrierAssignmentFlag(Boolean autoCarrierAssignmentFlag) {
		this.autoCarrierAssignmentFlag = autoCarrierAssignmentFlag;
	}
	public Long getCommCarrierId() {
		return commCarrierId;
	}
	public void setCommCarrierId(Long carrierId) {
		this.commCarrierId = carrierId;
	}
	public String getCommCarrierName() {
		return commCarrierName;
	}
	public void setCommCarrierName(String carrierName) {
		this.commCarrierName = carrierName;
	}

}
