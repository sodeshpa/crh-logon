/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.beans;


public class ContractItemOrderItemBean {

    private String deliveredQuantity;

    private String sentOrderItemNo;

    private String deletedFlag;

    private String orderItemNo;

    private String orderNumber;
	
	public ContractItemOrderItemBean() {
	}
	
	/**
	 * @param deliveredQuantity
	 * @param sentOrderItemNo
	 * @param deletedFlag
	 * @param orderItemNo
	 * @param orderNumber
	 */
	public ContractItemOrderItemBean(String deliveredQuantity, String sentOrderItemNo,
			String deletedFlag, String orderItemNo, String orderNumber) {
		super();
		this.deliveredQuantity = deliveredQuantity;
		this.sentOrderItemNo = sentOrderItemNo;
		this.deletedFlag = deletedFlag;
		this.orderItemNo = orderItemNo;
		this.orderNumber = orderNumber;
	}


	/**
	 * @return the deliveredQuantity
	 */
	public String getDeliveredQuantity() {
		return deliveredQuantity;
	}

	/**
	 * @param deliveredQuantity the deliveredQuantity to set
	 */
	public void setDeliveredQuantity(String deliveredQuantity) {
		this.deliveredQuantity = deliveredQuantity;
	}

	/**
	 * @return the sentOrderItemNo
	 */
	public String getSentOrderItemNo() {
		return sentOrderItemNo;
	}

	/**
	 * @param sentOrderItemNo the sentOrderItemNo to set
	 */
	public void setSentOrderItemNo(String sentOrderItemNo) {
		this.sentOrderItemNo = sentOrderItemNo;
	}

	/**
	 * @return the deletedFlag
	 */
	public String getDeletedFlag() {
		return deletedFlag;
	}

	/**
	 * @param deletedFlag the deletedFlag to set
	 */
	public void setDeletedFlag(String deletedFlag) {
		this.deletedFlag = deletedFlag;
	}

	/**
	 * @return the orderItemNo
	 */
	public String getOrderItemNo() {
		return orderItemNo;
	}

	/**
	 * @param orderItemNo the orderItemNo to set
	 */
	public void setOrderItemNo(String orderItemNo) {
		this.orderItemNo = orderItemNo;
	}

	/**
	 * @return the orderNumber
	 */
	public String getOrderNumber() {
		return orderNumber;
	}

	/**
	 * @param orderNumber the orderNumber to set
	 */
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ContractItemOrderItemBean [deliveredQuantity=");
		builder.append(deliveredQuantity);
		builder.append(", sentOrderItemNo=");
		builder.append(sentOrderItemNo);
		builder.append(", deletedFlag=");
		builder.append(deletedFlag);
		builder.append(", orderItemNo=");
		builder.append(orderItemNo);
		builder.append(", orderNumber=");
		builder.append(orderNumber);
		builder.append("]");
		return builder.toString();
	}

}
