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
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.common.base.GenericCrudProvider;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity_;
import com.crh.logon.persistence.utils.DaoProvider;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class OrderItemStore extends GenericCrudProvider<OrderItemEntity> implements OrderItemStoreInterface {

	public List<OrderItemEntity> findByLoadId(Long loadId) {

		DaoProvider daoProvider = getDaoProvider();
		ReadOnlyDao<OrderItemEntity> dao = daoProvider.getOrderItemEntityDao();

		Filter<OrderItemEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(OrderItemEntity.ATTR_LOAD_ID, loadId));

		return dao.find(filter);

	}

	@Override
	public List<OrderItemEntity> findBimodalTruckOrderItemByRailOrderItemId(Long railOrderItemId) {

		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

		CriteriaQuery<OrderItemEntity> query = criteriaBuilder.createQuery(OrderItemEntity.class);

		Root<OrderItemEntity> bimodalQueryRoot = query.from(OrderItemEntity.class);

		Predicate bimodalRailOrderItemMatches = criteriaBuilder.equal(bimodalQueryRoot.get(OrderItemEntity_.bimodalRailOrderItem), railOrderItemId);

		query.where(bimodalRailOrderItemMatches);

		TypedQuery<OrderItemEntity> typedQuery = em.createQuery(query);

		try {
			return typedQuery.getResultList();
		} 
		catch (NoResultException noResultException) {
			return null;
		}

	}

}