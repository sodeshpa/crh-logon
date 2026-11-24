/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain;

import java.util.List;

import com.crh.logon.business.common.base.GenericCrudProviderInterface;
import com.crh.logon.persistence.entities.common.CustorderOutputdocEntity;

/**
 * @author AdNovum Informatik AG
 */
public interface CustorderOutputdocStoreInterface extends GenericCrudProviderInterface<CustorderOutputdocEntity> {

	/**
	 * Fetches a list of {@code CustorderOutputdocEntity}s where where the custorderId equals to the parameter 
	 * The result list is sorted by the entity's creation timestamp.
	 * @param customerOrderId - filter clause
	 * @return result list of entities if none found empty list
	 */
	List<CustorderOutputdocEntity> findByCustomerOrderIdOrderedByCreationTimeStamp(Long customerOrderId);

}
