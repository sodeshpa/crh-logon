/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.common.base.GenericCrudProvider;
import com.crh.logon.persistence.entities.common.OrderRepEntity;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class OrderRepStore extends GenericCrudProvider<OrderRepEntity> implements OrderRepStoreInterface {

	public OrderRepEntity findAndIntializeByOrderId(Long orderId) {

		ReadOnlyDao<OrderRepEntity> dao = getDaoProvider().getOrderRepEntityDao();
		Filter<OrderRepEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(OrderRepEntity.ATTR_ORDER_ID, orderId));

		OrderRepEntity orderRep = dao.findSingle(filter);
		if (orderRep != null) {
			orderRep.initialize();
		}
		return orderRep;

	}

}