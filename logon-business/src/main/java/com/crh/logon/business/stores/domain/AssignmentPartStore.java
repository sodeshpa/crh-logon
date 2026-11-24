/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.common.base.GenericCrudProvider;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity_;
import com.crh.logon.persistence.entities.common.OrderEntity;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class AssignmentPartStore extends GenericCrudProvider<AssignmentPartEntity> implements AssignmentPartStoreInterface {

	@Override
	public AssignmentPartEntity findByLoadId(long loadId) {

		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

		CriteriaQuery<AssignmentPartEntity> query = criteriaBuilder.createQuery(AssignmentPartEntity.class);

		Root<AssignmentPartEntity> assignmentPartQueryRoot = query.from(AssignmentPartEntity.class);

		Predicate loadIdMatches = criteriaBuilder.equal(assignmentPartQueryRoot.get(AssignmentPartEntity_.load), loadId);

		query.where(loadIdMatches);

		TypedQuery<AssignmentPartEntity> typedQuery = em.createQuery(query);

		try {
			return typedQuery.getSingleResult();
		} 
		catch (NoResultException noResultException) {
			return null;
		}

	}
	
	public AssignmentPartEntity getActivePartForOrder(OrderEntity order) {
		final ReadWriteDao<AssignmentPartEntity> assignmentPartDao = getDaoProvider().getAssignmentPartEntityDao();
		final Filter<AssignmentPartEntity> filter = assignmentPartDao.createFilter();
		
		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ORDER, order));
		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT + "."+ AssignmentEntity.ATTR_ACTIVE_FLAG, true));
		
		AssignmentPartEntity assignmentPart = assignmentPartDao.findSingle(filter);
		
		return assignmentPart;
	}

}
