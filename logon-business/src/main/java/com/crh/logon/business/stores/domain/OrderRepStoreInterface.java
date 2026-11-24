/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain;

import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericCrudProviderInterface;
import com.crh.logon.persistence.entities.common.OrderRepEntity;

/**
 * @author AdNovum Informatik AG
 */
@Local
public interface OrderRepStoreInterface extends GenericCrudProviderInterface<OrderRepEntity> {

	/**
	 * @param orderId - unique id used for the lookup
	 * @return initialized entity
	 */
	OrderRepEntity findAndIntializeByOrderId(Long orderId);

}
