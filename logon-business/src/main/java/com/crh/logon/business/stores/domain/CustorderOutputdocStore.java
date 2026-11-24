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
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.common.base.GenericCrudProvider;
import com.crh.logon.persistence.entities.common.CustorderOutputdocEntity;
import com.crh.logon.persistence.utils.DaoProvider;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class CustorderOutputdocStore extends GenericCrudProvider<CustorderOutputdocEntity> implements CustorderOutputdocStoreInterface {

	public List<CustorderOutputdocEntity> findByCustomerOrderIdOrderedByCreationTimeStamp(Long customerOrderId) {

		DaoProvider daoProvider = getDaoProvider();
		ReadOnlyDao<CustorderOutputdocEntity> customerOrderOutputDocDao = daoProvider.getCustorderOutputdocEntityDao();

		Filter<CustorderOutputdocEntity> filter = customerOrderOutputDocDao.createFilter();
		filter.addExpression(Restrictions.eq(CustorderOutputdocEntity.ATTR_CUSTORDER_ID, customerOrderId));
		filter.addExpression(Order.desc(CustorderOutputdocEntity.ATTR_CTL_CRE_TS));

		return customerOrderOutputDocDao.find(filter);

	}

}