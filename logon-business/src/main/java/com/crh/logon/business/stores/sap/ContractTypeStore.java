package com.crh.logon.business.stores.sap;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
import com.crh.logon.persistence.entities.refdata.SapContractTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.SapContractTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.SapContractTypeTxPK;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class ContractTypeStore extends GenericReadOnlyProvider<UomTxEntity> implements ContractTypeStoreInterface {

	public List <SapContractTypeTxEntity> fetchTexts(Set<String> contractTypeTextIds, Locale locale) {

		if (contractTypeTextIds.isEmpty()) {
    		return Collections.emptyList();
    	}
		
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<SapContractTypeTxEntity> query = criteriaBuilder.createQuery(SapContractTypeTxEntity.class);
		Root<SapContractTypeTxEntity> sapContractTypeText = query.from(SapContractTypeTxEntity.class);
	
		Path<SapContractTypeTxPK> sapContractTypePrimaryKey = sapContractTypeText.get("sapContractTypeTxPK");		
		Path<SapContractTypeBsEntity> sapContractTypeBase = sapContractTypePrimaryKey.get("sapContractTypeBs");
		
		Predicate languageCondition = criteriaBuilder.equal(sapContractTypePrimaryKey.get("langCd"), locale.getLanguage());
		Predicate contractTypeIdsCondition = sapContractTypeBase.get("sapContractTypeCd").in(contractTypeTextIds);
		
		query.select(sapContractTypeText);
		query.where( criteriaBuilder.and(contractTypeIdsCondition, languageCondition));
		
		TypedQuery<SapContractTypeTxEntity> sapContractTypeQuery = em.createQuery(query);
		
		try {
			return sapContractTypeQuery.getResultList();
		} catch (NoResultException noResultException) {
			return null;
		}
	}		
}
