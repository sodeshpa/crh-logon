/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.controllers.scheduling.containers;

import com.crh.logon.persistence.entities.common.OrderItemEntity;

/**
 * @author AdNovum Informatik AG
 * 
 * Contains the orderitem paired with the loading data that is being transfered to it
 */
public class OrderItemLoadTransferContainer {

	private OrderItemEntity targetOrderItem;
	private LoadTransferContainer loadTransferSource;
	
	public OrderItemLoadTransferContainer(OrderItemEntity targetOrderItem,
			LoadTransferContainer loadTransferSource) {
		this.targetOrderItem = targetOrderItem;
		this.loadTransferSource = loadTransferSource;
	}
	
	public OrderItemEntity getTargetOrderItem() {
		return targetOrderItem;
	}
	public void setTargetOrderItem(OrderItemEntity targetOrderItem) {
		this.targetOrderItem = targetOrderItem;
	}
	public LoadTransferContainer getLoadTransferSource() {
		return loadTransferSource;
	}
	public void setLoadTransferSource(LoadTransferContainer loadTransferSource) {
		this.loadTransferSource = loadTransferSource;
	}
}
