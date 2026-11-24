/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain;

import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericCrudProviderInterface;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;

/**
 * @author AdNovum Informatik AG
 */
@Local
public interface AssignmentPartStoreInterface extends GenericCrudProviderInterface<AssignmentPartEntity> {

	/**
	 * Finds and returns an {@code AssignmentPartEntity} using the provided loadId as a filter criteria
	 * @param loadId - key used for the lookup
	 * @return found entity or null
	 */
	AssignmentPartEntity findByLoadId(long loadId);

	public AssignmentPartEntity getActivePartForOrder(OrderEntity order);
}
