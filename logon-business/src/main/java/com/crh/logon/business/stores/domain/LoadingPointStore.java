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

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.common.base.GenericCrudProvider;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.utils.DaoProvider;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class LoadingPointStore extends GenericCrudProvider<LoadingpointEntity> implements LoadingPointStoreInterface {

	public List<LoadingpointEntity> findLoadingPointsByShippingPointId(Long shippingPointId) {
		DaoProvider daoProvider = getDaoProvider();
		ReadOnlyDao<LoadingpointEntity> loadingPointDao = daoProvider.getLoadingpointEntityDao();
		Filter<LoadingpointEntity> filter = loadingPointDao.createFilter();
		filter.addExpression(Restrictions.eq(LoadingpointEntity.ATTR_SHIPPINGPOINT_ID, shippingPointId));
		return loadingPointDao.find(filter);
	}

}
