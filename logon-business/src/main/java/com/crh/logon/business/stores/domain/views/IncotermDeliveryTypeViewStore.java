/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain.views;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.common.base.GenericReadOnlyProvider;
import com.crh.logon.persistence.entities.views.IncotermDelivTypeEntity;
import com.crh.logon.persistence.entities.views.IncotermDelivTypeEntity_;

@Stateless
@AdnTrace
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class IncotermDeliveryTypeViewStore extends GenericReadOnlyProvider<IncotermDelivTypeEntity> implements IncotermDeliveryTypeViewStoreInterface {

	@Override
	public List<IncotermDelivTypeEntity> findBySalesAreaIds(Collection<Long> salesAreaIds) {
		
		if (salesAreaIds.isEmpty()) {
			return Collections.emptyList();
		}
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<IncotermDelivTypeEntity> cq = cb.createQuery(IncotermDelivTypeEntity.class);
		Root<IncotermDelivTypeEntity> incotermDeliveryTypeView = cq.from(IncotermDelivTypeEntity.class);
		Predicate salesAreaCondition = incotermDeliveryTypeView.get(IncotermDelivTypeEntity_.sareaId).in(salesAreaIds);
		
		cq.select(incotermDeliveryTypeView);
		cq.where(salesAreaCondition);
		
		TypedQuery<IncotermDelivTypeEntity> query = em.createQuery(cq);
		return query.getResultList();
	}
	
	@Override
	public List<IncotermDelivTypeEntity> findBySalesAreaId(Long salesAreaId) {
		
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<IncotermDelivTypeEntity> criteriaQuery = criteriaBuilder.createQuery(IncotermDelivTypeEntity.class);
		Root<IncotermDelivTypeEntity> incotermDeliveryTypeView = criteriaQuery.from(IncotermDelivTypeEntity.class);
		Path<String> salesAreaIdPath = incotermDeliveryTypeView.get(IncotermDelivTypeEntity.ATTR_SAREA_ID);
		Predicate salesAreaCondition = criteriaBuilder.equal(salesAreaIdPath, salesAreaId);
		
		criteriaQuery.select(incotermDeliveryTypeView);
		criteriaQuery.where(salesAreaCondition);
		
		TypedQuery<IncotermDelivTypeEntity> query = em.createQuery(criteriaQuery);
		return query.getResultList();
	}

}
