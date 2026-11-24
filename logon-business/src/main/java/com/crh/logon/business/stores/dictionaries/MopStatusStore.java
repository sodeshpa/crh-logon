/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.business.stores.dictionaries;

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
import com.crh.logon.persistence.entities.refdata.MopStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.MopStatusBsEntity_;
import com.crh.logon.persistence.entities.refdata.MopStatusTxEntity;
import com.crh.logon.persistence.entities.refdata.MopStatusTxEntity_;
import com.crh.logon.persistence.entities.refdata.MopStatusTxPK;
import com.crh.logon.persistence.entities.refdata.MopStatusTxPK_;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class MopStatusStore extends GenericDictionaryProvider<MopStatusBsEntity, MopStatusTxEntity, String> implements MopStatusStoreInterface {

	@Override
	public MopStatusTxEntity findText(String mopStatusCode,
			String languageCode) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<MopStatusTxEntity> query = criteriaBuilder.createQuery(MopStatusTxEntity.class);
		Root<MopStatusTxEntity> mopStatus = query.from(MopStatusTxEntity.class);

		Path<MopStatusTxPK> mopStatusPrimaryKey = mopStatus.get(MopStatusTxEntity_.mopStatusTxPK);
		Path<MopStatusBsEntity> mopStatusBase = mopStatusPrimaryKey.get(MopStatusTxPK_.mopStatusBs);
		
		Predicate languageCondition = criteriaBuilder.equal(mopStatusPrimaryKey.get(MopStatusTxPK_.langCd), languageCode);
		Predicate unitOfMeasurementCode = criteriaBuilder.equal(mopStatusBase.get(MopStatusBsEntity_.mopStatusCd), mopStatusCode);
	
		query.select(mopStatus);
		query.where( criteriaBuilder.and(unitOfMeasurementCode, languageCondition));
		
		TypedQuery<MopStatusTxEntity> mopStatusQuery = em.createQuery(query);
		
		try {
			return mopStatusQuery.getSingleResult();
		} 
		catch (NoResultException noResultException) {
			return null;
		}
	}
	
	
}
