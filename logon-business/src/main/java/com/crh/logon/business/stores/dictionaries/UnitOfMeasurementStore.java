/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.business.stores.dictionaries;

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

import com.crh.logon.business.common.base.GenericDictionaryProvider;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity_;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity_;
import com.crh.logon.persistence.entities.refdata.UomTxPK;
import com.crh.logon.persistence.entities.refdata.UomTxPK_;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class UnitOfMeasurementStore extends GenericDictionaryProvider<UomBsEntity, UomTxEntity, String> implements UnitOfMeasurementStoreInterface{
	
	
	public List<UomTxEntity> findTextsByCodes(Set<String> unitOfMeasurementCodes, Locale locale) {
		if (unitOfMeasurementCodes.isEmpty()) {
    		return Collections.emptyList();
    	}
		
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<UomTxEntity> query = criteriaBuilder.createQuery(UomTxEntity.class);
		Root<UomTxEntity> unitOfMeasurement = query.from(UomTxEntity.class);
		
		Path<UomTxPK> unitOfMeasurementPrimaryKey = unitOfMeasurement.get(UomTxEntity_.uomTxPK);
		Path<UomBsEntity> unitOfMeasurementBase = unitOfMeasurementPrimaryKey.get(UomTxPK_.uomBs);
		
		Predicate languageCondition = criteriaBuilder.equal(unitOfMeasurementPrimaryKey.get(UomTxPK_.langCd), locale.getLanguage());
		Predicate unitOfMeasurementCodesCondition = unitOfMeasurementBase.get(UomBsEntity_.uomCd).in(unitOfMeasurementCodes);
		
		query.select(unitOfMeasurement);
		query.where( criteriaBuilder.and(unitOfMeasurementCodesCondition, languageCondition));
		
		TypedQuery<UomTxEntity> unitOfMeasurementsQuery = em.createQuery(query);
		
		try {
			return unitOfMeasurementsQuery.getResultList();
		} 
		catch (NoResultException noResultException) {
			return null;
		}
	}

	@Override
	public UomTxEntity findText(String id, String languageCode) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<UomTxEntity> query = criteriaBuilder.createQuery(UomTxEntity.class);
		Root<UomTxEntity> unitOfMeasurement = query.from(UomTxEntity.class);

		Path<UomTxPK> unitOfMeasurementPrimaryKey = unitOfMeasurement.get(UomTxEntity_.uomTxPK);
		Path<UomBsEntity> unitOfMeasurementBase = unitOfMeasurementPrimaryKey.get(UomTxPK_.uomBs);
		
		Predicate languageCondition = criteriaBuilder.equal(unitOfMeasurementPrimaryKey.get(UomTxPK_.langCd), languageCode);
		Predicate unitOfMeasurementCode = criteriaBuilder.equal(unitOfMeasurementBase.get(UomBsEntity_.uomCd), id);
	
		query.select(unitOfMeasurement);
		query.where( criteriaBuilder.and(unitOfMeasurementCode, languageCondition));
		
		TypedQuery<UomTxEntity> unitOfMeasurementsQuery = em.createQuery(query);
		
		try {
			return unitOfMeasurementsQuery.getSingleResult();
		} 
		catch (NoResultException noResultException) {
			return null;
		}
	}
}
