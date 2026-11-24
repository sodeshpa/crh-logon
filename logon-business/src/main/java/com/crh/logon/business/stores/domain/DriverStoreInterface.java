/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain;

import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericReadOnlyInterface;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;

/**
 * @author AdNovum Informatik AG
 */

@Local
public interface DriverStoreInterface extends GenericReadOnlyInterface<DriverEntity> {

	public DriverEntity getDriverFromTruck(TruckEntity truck);
}
