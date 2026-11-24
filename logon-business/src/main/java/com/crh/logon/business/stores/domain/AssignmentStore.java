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
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.common.base.GenericCrudProvider;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentEntity_;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity_;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class AssignmentStore extends GenericCrudProvider<AssignmentEntity> implements AssignmentStoreInterface {

	@Override
	public AssignmentEntity findByOrderId(long orderId) {

		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

		CriteriaQuery<AssignmentEntity> query = criteriaBuilder.createQuery(AssignmentEntity.class);

		Root<AssignmentPartEntity> assignmentPartQueryRoot = query.from(AssignmentPartEntity.class);
		Path<AssignmentEntity> assignmentPath = assignmentPartQueryRoot.get(AssignmentPartEntity_.assignment);

		Predicate orderIdMatches = criteriaBuilder.equal(assignmentPartQueryRoot.get(AssignmentPartEntity_.order), orderId);
		Predicate assignmentActive = criteriaBuilder.equal(assignmentPath.get(AssignmentEntity_.activeFlag), Boolean.TRUE);

		query.select(assignmentPartQueryRoot.get(AssignmentPartEntity_.assignment));
		query.where(criteriaBuilder.and(orderIdMatches, assignmentActive));

		TypedQuery<AssignmentEntity> typedQuery = em.createQuery(query);

		try {
			return typedQuery.getSingleResult();
		} 
		catch (NoResultException noResultException) {
			return null;
		}

	}
	
	public AssignmentEntity getAssignmentForOrderId(Long orderId) {
		ReadWriteDao<AssignmentPartEntity> asspDao = getDaoProvider().getAssignmentPartEntityDao();
		
		Filter<AssignmentPartEntity> filter = new FilterImpl<AssignmentPartEntity>();
		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ORDER_ID, orderId));
		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_ACTIVE_FLAG, true));
		AssignmentPartEntity assPart = asspDao.findSingle(filter);
		
		return (assPart != null) ? assPart.getAssignment() : null;
	}

	public AssignmentEntity getAssignmentForExtOrderId(Long extOrderId) {
		ReadWriteDao<AssignmentPartEntity> asspDao = getDaoProvider().getAssignmentPartEntityDao();
		
		Filter<AssignmentPartEntity> filter = new FilterImpl<AssignmentPartEntity>();
		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_EXT_ORDER_ID, extOrderId));
		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_ACTIVE_FLAG, true));
		List<AssignmentPartEntity> assParts = asspDao.find(filter);
		
		return (assParts == null || assParts.isEmpty()) ? null : assParts.get(0).getAssignment();
	}
	
}
