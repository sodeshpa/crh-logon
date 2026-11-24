/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.business.stores.domain;

import java.util.List;
import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericReadOnlyInterface;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;

@Local
public interface ShiptoSiloStoreInterface extends GenericReadOnlyInterface<ShiptoSiloEntity> {

	/**
	 * Fetches a list of {@code ShiptoSiloEntity}s where the shiptoId matches the one given 
	 * @param shiptoId - filter clause
	 * @return entities found or empty list
	 */
	List<ShiptoSiloEntity> findActiveOnlyByShiptoId(Long shiptoId);

}
