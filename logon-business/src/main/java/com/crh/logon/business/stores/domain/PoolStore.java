/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.business.stores.domain;

import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import com.crh.logon.business.common.base.GenericReadOnlyProvider;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.authorization.Authorizer;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.PoolCarrierEntity;
import com.crh.logon.persistence.entities.common.PoolEntity;
import com.crh.logon.persistence.utils.DaoProvider;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class PoolStore extends GenericReadOnlyProvider<PoolEntity> implements PoolStoreInterface {

	public List<PoolEntity> findAllActiveOnlyUnrestricted() {
		DaoProvider daoProvider = getDaoProvider();
		ReadWriteDao<PoolEntity> poolDao = daoProvider.getPoolEntityDao();
		Authorizer.disableNextAccessRestriction();
		Filter<PoolEntity> filt = new FilterImpl<PoolEntity>();
		filt.addExpression(Restrictions.eq(PoolEntity.ATTR_CTL_ACT_CD, Boolean.TRUE));
		return poolDao.find(filt);
	}

	public Optional<PoolEntity> getFirstTruckPoolByCarrier(CarrierEntity carrier) {
		final ReadOnlyDao<PoolCarrierEntity> poolCarrierDao = getDaoProvider().getPoolCarrierEntityDao();
		final Filter<PoolCarrierEntity> poolCarrierFilter = poolCarrierDao.createFilter();
		poolCarrierFilter.addExpression(Restrictions.eq(PoolCarrierEntity.ATTR_CARRIER_ID, carrier.getCarrierId()));
		poolCarrierFilter.addExpression(Restrictions.eq(PoolCarrierEntity.ATTR_POOL + "." + PoolEntity.ATTR_SAP_TRSP_MODE_CD
				,  Constant.TRANSPORTATION_MODE_TRUCK));
		final Optional<PoolCarrierEntity> poolCarrier = poolCarrierDao.findNRecs(0,1,poolCarrierFilter).stream().findFirst();
		return poolCarrier.map(PoolCarrierEntity::getPool);
	}
}
