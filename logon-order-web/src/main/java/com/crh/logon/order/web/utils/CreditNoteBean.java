/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author AdNovum Informatik AG
 */
public class CreditNoteBean implements Serializable {

	private Long orderNumber = null;

	private Long orderItemNo = null;

	private String plantName = null;

	private String wagonNo = null;

	private String soldtoName = null;

	private String shiptoName = null;

	private String billtoName = null;

	private String sapMaterialNo = null;

	private String materialTxt = null;

	private String deliveredQuantity = null;

	private String returnedQuantity = null;

	private String yesFlag = null;

	private String noFlag = null;

	private Long orderId = null;

	private Long orderItemId = null;

	// Fieldnames
	public static final String ATTR_ORDER_NUMBER = "orderNumber";
	public static final String ATTR_ORDER_ITEM_NO = "orderItemNo";
	public static final String ATTR_PLANT_NAME = "plantName";
	public static final String ATTR_WAGON_NO = "wagonNo";
	public static final String ATTR_SOLDTO_NAME = "soldtoName";
	public static final String ATTR_SHIPTO_NAME = "shiptoName";
	public static final String ATTR_BILLTO_NAME = "billtoName";
	public static final String ATTR_SAP_MATERIAL_NO = "sapMaterialNo";
	public static final String ATTR_MATERIAL_TXT = "materialTxt";
	public static final String ATTR_DELIVERED_QUANTITY = "deliveredQuantity";
	public static final String ATTR_RETURNED_QUANTITY = "returnedQuantity";
	public static final String ATTR_YES_FLAG = "yesFlag";
	public static final String ATTR_NO_FLAG = "noFlag";
	public static final String ATTR_ORDER_ID = "orderId";
	public static final String ATTR_ORDER_ITEM_ID = "orderItemId";

	public CreditNoteBean() {
		super();
	}

	public CreditNoteBean(Long orderNumber, String soldtoName, String materialTxt,
			String yesFlag, String noFlag) {
		this.orderNumber = orderNumber;
		this.soldtoName = soldtoName;
		this.materialTxt = materialTxt;
		this.yesFlag = yesFlag;
		this.noFlag = noFlag;
	}

	public Long getOrderNumber() {
		return this.orderNumber;
	}

	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getSoldtoName() {
		return this.soldtoName;
	}

	public void setSoldtoName(String soldtoName) {
		this.soldtoName = soldtoName;
	}

	public String getMaterialTxt() {
		return this.materialTxt;
	}

	public void setMaterialTxt(String materialTxt) {
		this.materialTxt = materialTxt;
	}

	public String getYesFlag() {
		return this.yesFlag;
	}

	public void setYesFlag(String yesFlag) {
		this.yesFlag = yesFlag;
	}

	public String getNoFlag() {
		return this.noFlag;
	}

	public void setNoFlag(String noFlag) {
		this.noFlag = noFlag;
	}

	public String getWagonNo() {
		return this.wagonNo;
	}

	public void setWagonNo(String wagonNo) {
		this.wagonNo = wagonNo;
	}

	public String getDeliveredQuantity() {
		return this.deliveredQuantity;
	}

	public void setDeliveredQuantity(String deliveredQuantity) {
		this.deliveredQuantity = deliveredQuantity;
	}

	public String getPlantName() {
		return this.plantName;
	}

	public void setPlantName(String plantName) {
		this.plantName = plantName;
	}

	public Long getOrderItemNo() {
		return this.orderItemNo;
	}

	public void setOrderItemNo(Long orderItemNo) {
		this.orderItemNo = orderItemNo;
	}

	public String getSapMaterialNo() {
		return this.sapMaterialNo;
	}

	public void setSapMaterialNo(String sapMaterialNo) {
		this.sapMaterialNo = sapMaterialNo;
	}

	public String getBilltoName() {
		return this.billtoName;
	}

	public void setBilltoName(String billtoName) {
		this.billtoName = billtoName;
	}

	public String getReturnedQuantity() {
		return this.returnedQuantity;
	}

	public void setReturnedQuantity(String returnedQuantity) {
		this.returnedQuantity = returnedQuantity;
	}

	public String getShiptoName() {
		return this.shiptoName;
	}

	public void setShiptoName(String shiptoName) {
		this.shiptoName = shiptoName;
	}

	public Long getOrderId() {
		return this.orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getOrderItemId() {
		return this.orderItemId;
	}

	public void setOrderItemId(Long orderItemId) {
		this.orderItemId = orderItemId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		ToStringBuilder stringBuilder = new ToStringBuilder(this);

		stringBuilder.append(ATTR_ORDER_NUMBER, this.orderNumber);
		stringBuilder.append(ATTR_ORDER_ITEM_NO, this.orderItemNo);
		stringBuilder.append(ATTR_PLANT_NAME, this.plantName);
		stringBuilder.append(ATTR_WAGON_NO, this.wagonNo);
		stringBuilder.append(ATTR_SOLDTO_NAME, this.soldtoName);
		stringBuilder.append(ATTR_SHIPTO_NAME, this.shiptoName);
		stringBuilder.append(ATTR_BILLTO_NAME, this.billtoName);
		stringBuilder.append(ATTR_SAP_MATERIAL_NO, this.sapMaterialNo);
		stringBuilder.append(ATTR_MATERIAL_TXT, this.materialTxt);
		stringBuilder.append(ATTR_DELIVERED_QUANTITY, this.deliveredQuantity);
		stringBuilder.append(ATTR_RETURNED_QUANTITY, this.returnedQuantity);
		stringBuilder.append(ATTR_YES_FLAG, this.yesFlag);
		stringBuilder.append(ATTR_NO_FLAG, this.noFlag);
		stringBuilder.append(ATTR_ORDER_ID, this.orderId);
		stringBuilder.append(ATTR_ORDER_ITEM_ID, this.orderItemId);

		return stringBuilder.toString();
	}

}
