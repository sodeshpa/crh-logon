/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.business.stores.domain;

import java.util.List;
import java.util.Optional;
import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericReadOnlyInterface;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.PoolEntity;

@Local
public interface PoolStoreInterface extends GenericReadOnlyInterface<PoolEntity> {
	
	/**
	 * fetch a list with all the pools 
	 * 
	 * @return a list with all the PoolST
	 */
	public List <PoolEntity> findAllActiveOnlyUnrestricted();

	/**
	 * Return the only truck pool for the given carrier.
	 * @param carrier
	 * @return
	 */
	Optional<PoolEntity> getFirstTruckPoolByCarrier(CarrierEntity carrier);
}
