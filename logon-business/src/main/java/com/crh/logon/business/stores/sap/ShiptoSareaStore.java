package com.crh.logon.business.stores.sap;

import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import com.crh.logon.business.common.base.GenericReadOnlyProvider;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.views.SoldshipSareaFlexSrchEntity;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class ShiptoSareaStore extends GenericReadOnlyProvider<ShiptoSareaEntity> implements ShiptoSareaStoreInterface {


	public ShiptoSareaEntity findShiptoSareaWithActiveShipto(final SapBpEntity shiptoSapBpEntity,
			final SareaEntity sarea) {
		
		Query query = em.createQuery("SELECT x FROM ShiptoSareaEntity x WHERE x.sarea = :salesArea AND x.shipto=any(SELECT s FROM ShiptoEntity s WHERE s.activeFlag = TRUE AND s.shiptoSapBp = :shiptoSapBp)");
		query.setParameter("salesArea", sarea);
		query.setParameter("shiptoSapBp", shiptoSapBpEntity);

		try{
			return (ShiptoSareaEntity) query.getSingleResult();
		}
		catch (NoResultException noResultException) {
			return null;
		}
	}

	public ShiptoSareaEntity getShiptoSarea(Long shiptoId, Long sareaId) {
		final ReadOnlyDao<ShiptoSareaEntity> shiptoSareaDao = getDaoProvider().getShiptoSareaEntityDao();
		final Filter<ShiptoSareaEntity> filter = shiptoSareaDao.createFilter();

		filter.addExpression(Restrictions.eq(ShiptoSareaEntity.ATTR_SHIPTO_ID, shiptoId));
		filter.addExpression(Restrictions.eq(ShiptoSareaEntity.ATTR_SAREA_ID, sareaId));
		ShiptoSareaEntity shiptoSarea = shiptoSareaDao.findSingle(filter);

		return shiptoSarea;
	}

	public List<SoldshipSareaFlexSrchEntity> getCommonSalesAreasForShiptoSoldto(ShiptoEntity shipTo, SapBpEntity soldTo, SareaEntity sarea) {
		final ReadOnlyDao<SoldshipSareaFlexSrchEntity> dao = getDaoProvider().getSoldshipSareaFlexSrchEntityDao();
		final Filter<SoldshipSareaFlexSrchEntity> filter = dao.createFilter();

		filter.addExpression(Restrictions.eq(SoldshipSareaFlexSrchEntity.ATTR_SHIPTO_SAP_BP_ID, shipTo.getShiptoSapBp().getSapBpId()));
		if (soldTo != null) {
			filter.addExpression(Restrictions.eq(SoldshipSareaFlexSrchEntity.ATTR_SOLDTO_SAP_BP_ID, soldTo.getSapBpId()));
		}
		if ( sarea  != null ) {
			filter.addExpression(Restrictions.eq(SoldshipSareaFlexSrchEntity.ATTR_SAREA_ID, sarea.getSareaId()));
		}
		return dao.find(filter);
	}
}
