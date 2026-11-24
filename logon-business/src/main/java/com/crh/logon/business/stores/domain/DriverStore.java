/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;


import com.crh.logon.business.common.base.GenericReadOnlyProvider;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class DriverStore extends GenericReadOnlyProvider<DriverEntity> 
	implements DriverStoreInterface  {

	public DriverEntity getDriverFromTruck(TruckEntity truck) {	
		DriverEntity driver = null;
	
		if (truck != null) {	
			final ReadWriteDao<DriverEntity> dao = getDaoProvider().getDriverEntityDao();
			final Filter<DriverEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(DriverEntity.ATTR_CURRENT_TRUCK_ID, truck.getTruckId()));
			
			driver = dao.findSingle(filter);			
		}
		
		return driver;
	}
}
