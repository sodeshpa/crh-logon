/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;


import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.localinterfaces.MonitoringTablesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.PoolAdminServiceLocalInterface;
import com.crh.logon.business.services.utils.BizEventUtil;
import com.crh.logon.business.services.dispatchers.PoolAssignmentServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.BizeventAuditServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.PoolAssignmentServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.PoolEntity;
import com.crh.logon.persistence.entities.views.ManPoolAssignmentListEntity;
import com.crh.logon.persistence.entities.views.PoolCarrierCarrierEntity;
import com.crh.logon.utils.ListRange;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class PoolAssignmentServiceBean extends PoolAssignmentServiceDispatcher implements PoolAssignmentServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PoolAssignmentServiceBean.class);

	@EJB private BizeventAuditServiceLocalInterface bizeventAuditService;
	@EJB private OrderServiceLocalInterface orderService;
	@EJB private MonitoringTablesServiceLocalInterface monitoringTablesService;
	@EJB private PoolAdminServiceLocalInterface poolAdminService;
	
	public ListRange searchManPoolAssignments(int skip, int num, String sort, boolean asc, Map searchCrit) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"sort", sort},
							{"asc", asc + ""},
							{"searchCrit", searchCrit}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "searchManPoolAssignments", params);
		}
		// ]]
		try {
			final ReadOnlyDao<ManPoolAssignmentListEntity> dao = getDaoProvider().getManPoolAssignmentListEntityDao();
			final Filter<ManPoolAssignmentListEntity> filter = dao.createFilter();
			
			filter.addExpression(asc ? Order.asc(sort) : Order.desc(sort));
			if (searchCrit.containsKey(ManPoolAssignmentListEntity.ATTR_LANG_CD)) {
				filter.addExpression(Restrictions.eq(ManPoolAssignmentListEntity.ATTR_LANG_CD, (String)searchCrit.get(ManPoolAssignmentListEntity.ATTR_LANG_CD)));
			}
			final ListRange listRange = new ListRange();
			listRange.setData(dao.findNRecs(skip, num, filter).toArray());
			listRange.setTotalSize(dao.count(filter));
			
			return result = listRange;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public ListRange fetchAssignablePoolList(int skip, int num, String query, Locale locale) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"query", query},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAssignablePoolList", params);
		}
		// ]]
		try {
			
			final List<?> poolTxs = poolAdminService.fetchPoolTxEntities(skip, num, locale);		    
			final Long count = poolAdminService.countPoolTxEntities(locale);		    
			final ListRange listRange = new ListRange();
			listRange.setData(poolTxs.toArray());
			listRange.setTotalSize(count);
			
			return result = listRange;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public ListRange fetchAssignableCarrierList(int skip, int num, Long poolId) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"poolId", poolId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAssignableCarrierList", params);
		}
		// ]]
		try {

			final ReadOnlyDao<PoolCarrierCarrierEntity> dao = getDaoProvider().getPoolCarrierCarrierEntityDao();
			final Filter<PoolCarrierCarrierEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(PoolCarrierCarrierEntity.ATTR_POOL_ID, poolId));
			filter.addExpression(Order.asc(PoolCarrierCarrierEntity.ATTR_CARRIER_NAME));
			
			ListRange listRange = new ListRange();
			listRange.setData(dao.findNRecs(skip, num, filter).toArray());
			listRange.setTotalSize(dao.count(filter));
			
			return result = listRange;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Set (or removes) the carrier for an order, if orderStaus allows it.
	 * OrderStatus is set to transmitted to carrier or 
	 * transmitted to pool * (if carrier is removed)
	 * 
	 * @param orderNumber
	 * @param carrierId null, if carrier is to be removed
	 * @param reason optional reason for the operation, used only when carrierId is null (optionel even then)
	 */
	
	public void setOrderCarrier(Long orderNumber, Long carrierId, String reason) {
		// [[
		Object[][] params = {{"orderNumber", orderNumber},
							{"carrierId", carrierId},
							{"reason", reason}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setOrderCarrier", params);
		}
		// ]]
		try {
			final ReadWriteDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
			final Filter<OrderEntity> filter = orderDao.createFilter();
			filter.addExpression(Restrictions.eq(OrderEntity.ATTR_ORDER_NUMBER, orderNumber));
			final OrderEntity order = orderDao.findSingle(filter);
			
			final CarrierEntity oldCarrier = order.getCommCarrier();
			
			CarrierEntity newCarrier = null;
			if (carrierId != null) {
				newCarrier = getDaoProvider().getCarrierEntityDao().findById(carrierId.longValue());
			}
			orderService.setCommercialCarrier(order, newCarrier);
			
			// check whether carrier was really changed
			boolean isCarrierChanged = false;
			if ((oldCarrier == null && newCarrier != null)
					|| (oldCarrier != null && newCarrier == null)
					|| (oldCarrier != null && newCarrier != null && !oldCarrier.getCarrierId().equals(newCarrier.getCarrierId()))) {
				isCarrierChanged = true;
			}
			
			if (isCarrierChanged) {			
				final BizeventEntity setCommCarrierEvent = new BizeventEntity();
				setCommCarrierEvent.setBizeventType(BizEventUtil.BIZEVENT_TYPE_SET_COMM_CARRIER);
				setCommCarrierEvent.setOrder(order);
				setCommCarrierEvent.setDescription(reason);
				setCommCarrierEvent.setEventTs(new Date());
				monitoringTablesService.processBizevent(setCommCarrierEvent);
				
				// if the new carrier is null (carrier was reset) then set the order status back to transmitted to pool
				if (order.getCommCarrier() == null) {
					orderService.changeOrderStatus(order, Constant.ORDER_STATUS_TRANSMITTED_TO_POOL);
					bizeventAuditService.persistAuditData(setCommCarrierEvent);
				}
				
				bizeventAuditService.persistAuditData(setCommCarrierEvent);
			}
			
			// if the actual carrier is not null (independently whether it was changed or not) then transmit to carrier
			if (order.getCommCarrier() != null) {
				final BizeventEntity transmitToCommCarrierEvent = new BizeventEntity();
				transmitToCommCarrierEvent.setBizeventType(BizEventUtil.BIZEVENT_TYPE_TRANSMITTED_TO_COMM_CARRIER);
				transmitToCommCarrierEvent.setOrder(order);
				transmitToCommCarrierEvent.setEventTs(new Date());
				monitoringTablesService.processBizevent(transmitToCommCarrierEvent);
				
				orderService.transmitToCarrier(transmitToCommCarrierEvent);
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	/**
	 * Set (or removes) the pool for an order, if orderStaus allows it.
	 * OrderStatus is set to transmitted to pool or 
	 * new (if pool is removed)
	 * 
	 * @param orderNumber
	 * @param poolId null, if pool is to be removed
	 * @param reason optional reason for the operation, used only when poolId is null (optional even then)
	 */
	public void setOrderPool(Long orderNumber, Long poolId, String reason) {
		// [[
		Object[][] params = {{"orderNumber", orderNumber},
							{"poolId", poolId},
							{"reason", reason}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setOrderPool", params);
		}
		// ]]
		try {
			final ReadWriteDao<OrderEntity> dao = getDaoProvider().getOrderEntityDao();
			final Filter<OrderEntity> f = dao.createFilter();
			f.addExpression(Restrictions.eq(OrderEntity.ATTR_ORDER_NUMBER, orderNumber));
			final OrderEntity order = dao.findSingle(f);
			
			final PoolEntity oldPool = order.getPool();
			
			PoolEntity newPool = null;
			if (poolId != null) {
				newPool = getDaoProvider().getPoolEntityDao().findById(poolId);
			}
			order.setPool(newPool);
			
			// check whether pool was really changed
			boolean isPoolChanged = false;
			if ((oldPool == null && newPool != null)
					|| (oldPool != null && newPool == null)
					|| (oldPool != null && newPool != null && !oldPool.getPoolId().equals(newPool.getPoolId()))) {
				isPoolChanged = true;
			}

			if (isPoolChanged) {
				final BizeventEntity setPoolEvent = new BizeventEntity();
				setPoolEvent.setBizeventType(BizEventUtil.BIZEVENT_TYPE_SET_POOL);
				setPoolEvent.setOrder(order);
				setPoolEvent.setDescription(reason);
				setPoolEvent.setEventTs(new Date());
				monitoringTablesService.processBizevent(setPoolEvent);
				
				// if the new pool is null (pool was reset) then set the order status back to new
				if (order.getPool() == null) {
					orderService.changeOrderStatus(order, Constant.ORDER_STATUS_NEW);
				}
				
				bizeventAuditService.persistAuditData(setPoolEvent);
			}
			
			// if the actual pool is not null (independently whether it was changed or not) then transmit to pool
			if (order.getPool() != null) {
				final BizeventEntity transmitToPoolEvent = new BizeventEntity();
				transmitToPoolEvent.setBizeventType(BizEventUtil.BIZEVENT_TYPE_TRANSMITTED_TO_POOL);
				transmitToPoolEvent.setOrder(order);
				transmitToPoolEvent.setEventTs(new Date());
				monitoringTablesService.processBizevent(transmitToPoolEvent);
				
				orderService.transmitToPool(transmitToPoolEvent);
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
}
