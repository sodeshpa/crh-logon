/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.business.stores.domain;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.common.base.GenericReadOnlyProvider;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class ShiptoSiloStore extends GenericReadOnlyProvider<ShiptoSiloEntity> implements ShiptoSiloStoreInterface {

	public List<ShiptoSiloEntity> findActiveOnlyByShiptoId(Long shiptoId) {
		Filter<ShiptoSiloEntity> shiptoSiloFilter = new FilterImpl<ShiptoSiloEntity>();
		shiptoSiloFilter.addExpression(Restrictions.eq(ShiptoSiloEntity.ATTR_SHIPTO_ID, shiptoId));
		shiptoSiloFilter.addExpression(Restrictions.eq(ShiptoSiloEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
		return getDaoProvider().getShiptoSiloEntityDao().find(shiptoSiloFilter);
	}

}
