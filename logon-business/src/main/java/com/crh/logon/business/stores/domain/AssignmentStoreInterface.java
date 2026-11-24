/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain;

import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericCrudProviderInterface;
import com.crh.logon.persistence.entities.common.AssignmentEntity;

/**
 * @author AdNovum Informatik AG
 */
@Local
public interface AssignmentStoreInterface extends GenericCrudProviderInterface<AssignmentEntity> {

	/**
	 * Finds and returns an {@code AssignmentEntity} using the provided orderId as a filter criteria
	 * @param orderId - key used for the lookup
	 * @return found entity or null
	 */
	AssignmentEntity findByOrderId(long orderId);
	
	public AssignmentEntity getAssignmentForOrderId(Long orderId);
	
	public AssignmentEntity getAssignmentForExtOrderId(Long extOrderId);
	
}
