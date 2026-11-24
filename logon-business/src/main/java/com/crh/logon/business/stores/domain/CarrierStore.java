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

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.common.base.GenericReadOnlyProvider;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity_;
import com.crh.logon.persistence.entities.common.PoolCarrierEntity;
import com.crh.logon.persistence.entities.common.PoolCarrierEntity_;
import com.crh.logon.persistence.entities.sap.SapVendorEntity;
import com.crh.logon.persistence.entities.sap.SapVendorEntity_;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class CarrierStore extends GenericReadOnlyProvider<CarrierEntity> implements CarrierStoreInterface {

	@Override
	public List<CarrierEntity> findCommercialCarriersByPoolId(Long poolId) {

		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

		CriteriaQuery<CarrierEntity> query = criteriaBuilder.createQuery(CarrierEntity.class);

		Root<PoolCarrierEntity> poolCarrierQueryRoot = query.from(PoolCarrierEntity.class);

		Path<CarrierEntity> carrierPath = poolCarrierQueryRoot.get(PoolCarrierEntity_.carrier);
		Path<SapVendorEntity> sapVendorPath = carrierPath.get(CarrierEntity_.sapVendor);

		Predicate poolIdCondition = criteriaBuilder.equal(poolCarrierQueryRoot.get(PoolCarrierEntity_.pool), poolId);
		Predicate commercialCarrierCondition = criteriaBuilder.equal(sapVendorPath.get(SapVendorEntity_.vnIsCommCarrier), Boolean.TRUE);

		query.select(poolCarrierQueryRoot.get(PoolCarrierEntity_.carrier));
		query.where(criteriaBuilder.and(poolIdCondition, commercialCarrierCondition));

		TypedQuery<CarrierEntity> typedQuery = em.createQuery(query);

		try {
			return typedQuery.getResultList();
		} 
		catch (NoResultException noResultException) {
			return null;
		}

	}


	public CarrierEntity findCarrierEntityByVendorSapIfcId(String vendorSapIfcId) {
		CarrierEntity result = null;
		final ReadOnlyDao<CarrierEntity> dao = getDaoProvider().getCarrierEntityDao();
		final Filter<CarrierEntity> f = dao.createFilter();
		f.addExpression(Restrictions.eq(CarrierEntity.ATTR_SAP_VENDOR + "." + SapVendorEntity.ATTR_SAP_IFC_ID, vendorSapIfcId));
		f.addExpression(Restrictions.eq(CarrierEntity.ATTR_ACTIVE_FLAG, true));
		result = dao.findSingle(f);

		return result;
	}

}