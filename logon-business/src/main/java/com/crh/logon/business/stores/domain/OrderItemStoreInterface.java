/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain;

import java.util.List;

import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericCrudProviderInterface;
import com.crh.logon.persistence.entities.common.OrderItemEntity;

/**
 * @author AdNovum Informatik AG
 */
@Local
public interface OrderItemStoreInterface extends GenericCrudProviderInterface<OrderItemEntity> {

	/**
	 * Loads a list of oder items for a given load id. 
	 * @param loadId - ID of the {@code LoadEntity} for which we seek the order items
	 */
	List<OrderItemEntity> findByLoadId(Long loadId);

	/**
	 * Returns the bimodal truck order items where the rail order item id equals to the one provided
	 * @param railOrderItemId - ID of the {@code OrderItemEntity} which is a rail item and for which we seek the truck item
	 */
	List<OrderItemEntity> findBimodalTruckOrderItemByRailOrderItemId(Long railOrderItemId);

}
