/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.JPAQuery;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.gdi.jpa.filter.SelectExpression;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.localinterfaces.AsyncMopEventOutServiceLocalInterface;
import com.crh.logon.business.services.dispatchers.AssignmentServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.AssignmentServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.MonitoringTablesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.utils.AssignmentStatusUtil;
import com.crh.logon.business.services.utils.AssignmentUtils;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.refdata.BizeventTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.views.OrdItemAssignmtLoadEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
@AdnTrace
public class AssignmentServiceBean extends AssignmentServiceDispatcher implements AssignmentServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(AssignmentServiceBean.class);
	
	@EJB private AsyncMopEventOutServiceLocalInterface asyncMopEventOutService;
    @EJB private RefDataGenericsServiceLocalInterface referentialDataService;
    @EJB private OrderServiceLocalInterface orderService;
    @EJB private MonitoringTablesServiceLocalInterface monitoringTablesService;
	
	public void cancelLoading(BizeventEntity event, AssignmentEntity assignment) {
		
		final List<AssignmentPartEntity> loadAps = new ArrayList<AssignmentPartEntity>();
		final List<AssignmentPartEntity> unloadAps = new ArrayList<AssignmentPartEntity>();
		AssignmentUtils.getSortedAssignmentParts(assignment, loadAps, unloadAps);
		
		assignment.setAssignStatus(AssignmentStatusUtil.ASSIGNMENT_STATUS_NEW);

		for (AssignmentPartEntity unloadAp : unloadAps) {
			// More restrictive check than in changeOrderStatus required
			OrderEntity order = unloadAp.getOrder();
			Long statusCd = order.getOrderStatus().getOrderStatusCd();
			if (!(statusCd.equals(Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER)
					|| statusCd.equals(Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK)
					|| statusCd.equals(Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT)
					|| statusCd.equals(Constant.ORDER_STATUS_LOADING))) {
				throw new LogonException(String.format(
							"Order %s is in status %d, cancelLoading not allowed.",
							order.getOrderNumber(), statusCd),
						Reason.RES_ORDER_WRONG_STATUS);
			}
			orderService.changeOrderStatus(order, Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT);
			
		}
		
		for (AssignmentPartEntity loadAp : loadAps) {
			LoadEntity load = loadAp.getLoad();
			
			LOG.debug("Sending LOADING ENDED message to PDA related to load" + load.getLoadId());
			if ( (loadAp != null) && (load.getTruck() != null) ) {
				asyncMopEventOutService.sendLoadingEnded(load.getTruck(), loadAp,
						load.getTareWeightT(), load.getTareWeightT(), BigDecimal.ZERO,
						load.getNetQuantityUom(), load.getSapMaterial());
			}
			
			// for re-initialization of net quantity unit of measure
			final Collection<OrderItemEntity> orderItems = orderService.getOrderItems(load);
			final UomBsEntity netQuantityUom;
			if (orderItems != null && orderItems.size() > 0) {
				final OrderItemEntity orderItem =orderItems.iterator().next();
				netQuantityUom = AssignmentUtils.appropriateUomForLoad(orderItem);
			}
			else {
				netQuantityUom = null;
			}
			
			load.setLoadingTs(null);
			load.setTareWeightT(null);
			load.setNetQuantity(null);
			load.setNetQuantityUom(netQuantityUom);
			load.setGrossWeightT(null);
			load.setWsOriginalTareMeasureT(null);
			load.setWsOriginalGrossMeasureT(null);
			
			loadAp.setAssignPartStatus(AssignmentStatusUtil.ASSIGNMENT_PART_STATUS_NEW);
			loadAp.setRealStart(null);
		}
	}
	
	/**
	 * Reverts the deliveries from the assignment from status 'loading' to status 'arrived at loadingpoint'.
	 * Basically does the same thing as CancelLoadingRequestHandler.perform() method.
	 * 
	 * @param assignmentId The id of the existing assignment to cancel the loading on.
	 */
	public void cancelLoading(Long assignmentId) {
		if (assignmentId == null) {
			throw new LogonException(Reason.RES_ERROR_LOADING_ASSIGNMENT_NOT_FOUND);
		}
		final ReadOnlyDao<AssignmentEntity> dao = getDaoProvider().getAssignmentEntityDao();
		final Filter<AssignmentEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(AssignmentEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
		filter.addExpression(Restrictions.eq(AssignmentEntity.ATTR_ASSIGNMENT_ID, assignmentId));

		final AssignmentEntity assignment = dao.findSingle(filter);
		if (assignment == null) {
			throw new LogonException(Reason.RES_ERROR_LOADING_ASSIGNMENT_NOT_FOUND);
		}

		// Creating the cancel loading bizevent entity
		BizeventEntity bizevent = new BizeventEntity();
		bizevent.setBizeventType(referentialDataService.findBsEntity(BizeventTypeBsEntity.class, Constant.BIZEVENT_TYPE_LOADING_CANCELLED));
		bizevent.setEventTs(new Date());
        
		bizevent = monitoringTablesService.processBizevent(bizevent);
		
		bizevent.setTruck(assignment.getTruck());
		bizevent.setAssignment(assignment);
		
		cancelLoading(bizevent, assignment);
	}
	
	/**
	 * Checks if at least one active assignment exists for the same wagon.
	 * 
	 * @param wagon The wagon object
	 * 
	 * @return true if at least one active assignment exists for the wagon, 
	 * otherwise false.
	 */
	public boolean isActiveAssignmentExistsForWagon(WagonEntity wagon) {
		// [[
		boolean result = false;
		Object[][] params = {{"wagon", wagon}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "isActiveAssignmentExistsForWagon", params);
		}
		// ]]
		try {
			final ReadOnlyDao<AssignmentEntity> dao = getDaoProvider().getAssignmentEntityDao();
			final Filter<AssignmentEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(AssignmentEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			filter.addExpression(Restrictions.eq(AssignmentEntity.ATTR_WAGON, wagon));
			
			filter.addExpression(Restrictions.neq(AssignmentEntity.ATTR_ASSIGN_STATUS_CD, 
					Constant.ASSIGNMENT_STATUS_FINISHED));
			filter.addExpression(Restrictions.neq(AssignmentEntity.ATTR_ASSIGN_STATUS_CD, 
					Constant.ASSIGNMENT_STATUS_CANCELLED));

			// at least one active assignment should exist
			final List<AssignmentEntity> assignmentList = dao.findNRecs(0, 1, filter);
			return !assignmentList.isEmpty();
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
	 * Return the id of the active assignment containing this order (in one of its assignment
	 * parts) or null if that does not exist.
	 * @param orderId
	 * @return The id of the active assignment containing this order or null
	 */
	public Long findActiveAssignmentIdForOrderId(Long orderId) {
		// [[
		Long result = null;
		Object[][] params = {{"orderId", orderId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findActiveAssignmentIdForOrderId", params);
		}
		// ]]
		try {
			ReadOnlyDao<AssignmentPartEntity> dao = getDaoProvider().getAssignmentPartEntityDao();
			Filter<AssignmentPartEntity> f = dao.createFilter();
			JPAQuery<AssignmentPartEntity> query = dao.createJPAQuery();
			
			f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ORDER_ID, orderId));
			f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT + "." 
					+ AssignmentEntity.ATTR_ACTIVE_FLAG, true));
			
			query.setFilter(f);
			query.addSelect(SelectExpression.simpleSelect(AssignmentPartEntity.ATTR_ASSIGNMENT_ID));
			
			return result = (Long) dao.findSingle(query);
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
	 * Updates LDS related fields in {@code AssignmentEntity}
	 * @param orderItemAssignmentLoad
	 */
	public AssignmentEntity updateLdsInformation(OrdItemAssignmtLoadEntity orderItemAssignmentLoad) {
		
		ReadWriteDao<AssignmentEntity> assignmentEntityDao = getDaoProvider().getAssignmentEntityDao();
		AssignmentEntity assignmentEntity = assignmentEntityDao.findById(orderItemAssignmentLoad.getAssignmentId());
		assignmentEntity.setLdsDriverName(orderItemAssignmentLoad.getLdsDriverName());
		assignmentEntity.setLdsAttorneyName(orderItemAssignmentLoad.getLdsAttorneyName());
		assignmentEntity.setLdsAttorneyStart(orderItemAssignmentLoad.getLdsAttorneyStart());
		assignmentEntity.setLdsCarrierCrm(orderItemAssignmentLoad.getLdsCarrierCrm());
		assignmentEntity.setLdsDispatcherId(orderItemAssignmentLoad.getLdsDispatcherId());
		assignmentEntity.setLdsDispatcherName(orderItemAssignmentLoad.getLdsDispatcherName());
		assignmentEntity.setLdsDispatcherTel(orderItemAssignmentLoad.getLdsDispatcherTel());
		assignmentEntity.setLdsDriverCardNo(orderItemAssignmentLoad.getLdsDriverCardNo());
		assignmentEntity.setLdsDriverId(orderItemAssignmentLoad.getLdsDriverId());
		assignmentEntity.setLdsSealNumbers(orderItemAssignmentLoad.getLdsSealNumbers());
		return assignmentEntityDao.merge(assignmentEntity);
		
	}
}
