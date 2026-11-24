/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain;

import java.util.List;

import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericCrudProviderInterface;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;

/**
 * @author AdNovum Informatik AG
 */
@Local
public interface LoadingPointStoreInterface extends GenericCrudProviderInterface<LoadingpointEntity> {

	/**
	 * Fetches a list of {@code LoadingpointEntity}s where where the shippingPointId equals to the parameter 
	 * @param shippingPointId - filter clause
	 * @return result list of entities if none found empty list
	 */
	List<LoadingpointEntity> findLoadingPointsByShippingPointId(Long shippingPointId);

}
