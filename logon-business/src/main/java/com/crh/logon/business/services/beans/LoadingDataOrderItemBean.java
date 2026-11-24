/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans;

import com.crh.logon.beans.LoadedQuantityData;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;

/**
 * @author AdNovum Informatik AG
 */
public class LoadingDataOrderItemBean {

	private final LoadedQuantityData loadedQuantityData;
	private final OrderItemEntity orderItem;
	private final OrderEntity order;

	public LoadingDataOrderItemBean(LoadedQuantityData loadedQuantityData, OrderItemEntity orderItem) {
		this.loadedQuantityData = loadedQuantityData;
		this.orderItem = orderItem;
		this.order = orderItem.getOrder();
	}

	public LoadedQuantityData getLoadedQuantityData() {
		return loadedQuantityData;
	}

	public OrderItemEntity getOrderItem() {
		return orderItem;
	}

	public OrderEntity getOrder() {
		return order;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((orderItem == null) ? 0 : orderItem.getOrderItemId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LoadingDataOrderItemBean other = (LoadingDataOrderItemBean) obj;
		if (orderItem == null) {
			if (other.orderItem != null)
				return false;
		}
		else if (!orderItem.getOrderItemId().equals(other.orderItem.getOrderItemId()))
			return false;
		return true;
	}	

}