package com.crh.logon.business.stores.domain.views;

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

import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.common.base.GenericReadOnlyProvider;
import com.crh.logon.persistence.entities.views.CustordItemSumQtyExtEntity;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class QuantityPerMaterialInCustomerOrderStore extends GenericReadOnlyProvider<CustordItemSumQtyExtEntity> implements QuantityPerMaterialInCustomerOrderStoreInterface {
	
	public List<CustordItemSumQtyExtEntity> findByCustorderId(Long filterCustorderId, String language) {
		
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		
		CriteriaQuery<CustordItemSumQtyExtEntity> query = criteriaBuilder.createQuery(CustordItemSumQtyExtEntity.class);
		Root<CustordItemSumQtyExtEntity> root = query.from(CustordItemSumQtyExtEntity.class);
	
		Path<Long> custorderId = root.get(CustordItemSumQtyExtEntity.ATTR_CUSTORDER_ID);		
		Path<String> langCd = root.get(CustordItemSumQtyExtEntity.ATTR_LANG_CD);
		
		Predicate custorderCondition = criteriaBuilder.equal(custorderId, filterCustorderId);

		Predicate languageCondition = criteriaBuilder.equal(langCd, language);
		
		query.select(root);
		query.where(criteriaBuilder.and(custorderCondition, languageCondition));
		query.orderBy(criteriaBuilder.asc(root.get(CustordItemSumQtyExtEntity.ATTR_CUSTORDER_ITEM_NO)));
		
		TypedQuery<CustordItemSumQtyExtEntity> quantityPerMaterialInCustomerOrderQuery = em.createQuery(query);
		
		try {
			return quantityPerMaterialInCustomerOrderQuery.getResultList();
		} catch (NoResultException noResultException) {
			return null;
		}
	}
	
}
