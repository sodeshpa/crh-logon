/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain.views;

import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericReadOnlyInterface;
import com.crh.logon.persistence.entities.views.IncotermDelivTypeEntity;

@Local
public interface IncotermDeliveryTypeViewStoreInterface extends GenericReadOnlyInterface<IncotermDelivTypeEntity> {
	
	/**
	 * Finds all IncotermDeliveryTypeViewEntities for a given list of sales areas.
	 * @param salesAreaIds collection of sales area ids
	 * @return List<IncotermDeliveryTypeEntity> for the given sareaIds
	 */
	public List<IncotermDelivTypeEntity> findBySalesAreaIds(Collection<Long> salesAreaIds);
	
	/**
	 * Finds all IncotermDeliveryTypeViewEntities for a given sales area.
	 * @param salesAreaIds collection of sales area ids
	 * @return List<IncotermDelivTypeEntity> for the given sareaId
	 */
	public List<IncotermDelivTypeEntity> findBySalesAreaId(Long salesAreaId);
}
