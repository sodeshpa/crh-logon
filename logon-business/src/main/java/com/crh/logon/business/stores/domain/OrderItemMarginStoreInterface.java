package com.crh.logon.business.stores.domain;

import java.util.List;

import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericReadOnlyInterface;
import com.crh.logon.persistence.entities.views.OrderItemMarginEntity;

@Local
public interface OrderItemMarginStoreInterface extends GenericReadOnlyInterface<OrderItemMarginEntity> {

	/**
	 * Loads {@code OrderItemMarginEntity}s for the given orderId
	 * @param orderId - for which to load margins
	 */
	List<OrderItemMarginEntity> findByOrderId(Long orderId);

}
