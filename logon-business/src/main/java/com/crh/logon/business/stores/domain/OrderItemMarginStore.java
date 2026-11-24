package com.crh.logon.business.stores.domain;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.common.base.GenericReadOnlyProvider;
import com.crh.logon.persistence.entities.views.OrderItemMarginEntity;
import com.crh.logon.persistence.utils.DaoProvider;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class OrderItemMarginStore extends GenericReadOnlyProvider<OrderItemMarginEntity> implements OrderItemMarginStoreInterface {

	public List<OrderItemMarginEntity> findByOrderId(Long orderId) {

		DaoProvider daoProvider = getDaoProvider();
		ReadOnlyDao<OrderItemMarginEntity> dao = daoProvider.getOrderItemMarginEntityDao();
		Filter<OrderItemMarginEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(OrderItemMarginEntity.ATTR_ORDER_ID, orderId));
		return dao.find(filter);

	}

}
