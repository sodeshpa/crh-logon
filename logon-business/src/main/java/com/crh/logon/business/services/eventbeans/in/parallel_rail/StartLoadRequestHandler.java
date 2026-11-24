/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.eventbeans.in.parallel_rail;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.MonitoringTablesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.LogonUtil;
import org.apache.commons.lang.time.DateUtils;

import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;

import com.crh.logon.constants.Constant;
import com.crh.logon.constants.EventSource;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.parallel_rail.ParallelRailResponseCode;
import com.holcim.logon.eventbeans.in.parallel_rail.StartLoadRequest;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.refdata.AssignStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.AssignmentPartTypeBsEntity;
import com.crh.logon.utils.StringUtils;


/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class StartLoadRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {

	@EJB private OrderServiceLocalInterface orderService;
	@EJB private MonitoringTablesServiceLocalInterface monitoringTablesService;
		
	protected void validate(LogonEventRequest eventBean) {
        
		StartLoadRequest bean = (StartLoadRequest)eventBean;
    	validateMandatory(com.crh.logon.business.services.eventbeans.in.LogonEventRequestHandler.FIELD_VERLADEAUFTRAG_ID, bean.getVerladeauftragId());
    	validateMandatory(com.crh.logon.business.services.eventbeans.in.LogonEventRequestHandler.FIELD_VERLADESTRASSE_ID, bean.getVerladestrasseId());
    	validateMandatory(FIELD_WAGON, bean.getWagon());
    }


	public LogonEventResponse perform(LogonEventRequest eventBean)
	{
		StartLoadRequest bean = (StartLoadRequest)eventBean;
		BigDecimal tareWeightT = null;
		
		BizeventEntity bizevent = createBizevent(Constant.BIZEVENT_TYPE_LOADING, bean);
		
		LoadEntity load = getLoadByVerladeauftragId(bean.getVerladeauftragId());
		if (load == null) {
			
			throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.APPLICATION_ERROR, "No load found with verladeAuftragID " + bean.getVerladeauftragId()));
		}
		ContainerEntity container = null;
		if (!StringUtils.empty(bean.getContainerLabel())) {
			container = getContainer(bean.getContainerLabel());
		}
		bizevent.setContainer(container);
		
		OrderItemEntity orderItem = LogonUtil.getBulkDumpOrPowderOrderItem(orderService.getOrderItems(load));
		if (bean.getLogonOrderId() != null) {
			OrderEntity order = orderService.findOrderByOrderNumber(bean.getLogonOrderId(), true);
			if (order == null) {
				
				throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.MANDATORY_FIELD_ERROR, com.crh.logon.business.services.eventbeans.in.LogonEventRequestHandler.FIELD_LOGON_ORDER_ID), bean.getLogonOrderId());
			}
			bizevent.setOrder(order);
		}
		else if (orderItem != null) {
			bizevent.setOrder(orderItem.getOrder());
		}
		
		WagonEntity wagon = bizevent.getWagon();
		if (wagon != null) {
			if (load.getWagon() == null) {
				// Load is not yet reserved for wagon
				if (orderItem != null) {
					// Check if other order items of this order are assigned to another wagon 
					List orderItems = (List) LogonPackageHandlingUtil.getMaterialItems(orderItem.getOrder());
					for (Iterator it=orderItems.iterator();it.hasNext();) {
						OrderItemEntity otherOrderItem = (OrderItemEntity)it.next();
						if ( (otherOrderItem.getLoad() != null) && (otherOrderItem.getLoad().getWagon() != null) && (!wagon.equals(otherOrderItem.getLoad().getWagon())) ) {
							// this or another orderItem is already assigned to another wagon, forbid this load
							
							throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.APPLICATION_ERROR, "Order assigned to another wagon (wagon " + otherOrderItem.getLoad().getWagon().getWagonNo() + ")"));
						}
					}
				}
				// Order is free to take, assign to this wagon 
				load.setWagon(wagon);
				load.setContainer(container);
			}
			else if (!load.getWagon().equals(wagon)) {
				
				throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.APPLICATION_ERROR, "Order assigned to another wagon (wagon " + load.getWagon().getWagonNo() + ")"));
			}
			// Remove all not-loaded-preloads for this wagon and no order.
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(LoadEntity.ATTR_WAGON, wagon));
			filter.addExpression(Restrictions.isNull(LoadEntity.ATTR_LOADING_TS));
			filter.addExpression(Restrictions.isNull(LoadEntity.ATTR_UNLOADING_TS));
			filter.addExpression(Restrictions.neq(LoadEntity.ATTR_LOAD_ID, load.getLoadId()));
			filter.addExpression(Restrictions.isNotNull(LoadEntity.ATTR_LOAD_ORDER_ID));
			if ( (orderItem == null) && (container != null) ) {
				// A preload was selected, remove all other loads for this container
				filter.addExpression(Restrictions.eq(LoadEntity.ATTR_CONTAINER, container));
			}
			List loadList = getDaoProvider().getLoadEntityDao().find(filter);
			for (Iterator it=loadList.iterator();it.hasNext();) {
				LoadEntity tempLoad = (LoadEntity)it.next();
				filter = new FilterImpl();
				filter.addExpression(Restrictions.eq(OrderItemEntity.ATTR_LOAD, tempLoad));
				if (getDaoProvider().getOrderItemEntityDao().count(filter) == 0) {
					getDaoProvider().getLoadEntityDao().remove(tempLoad);
				}
			}
		}
		
		LoadingpointEntity lp = null;
		if ( (StringUtils.empty(bean.getWerkId())) && (orderItem != null) ) {
			lp = getLoadingpoint(orderItem.getOrder().getShippingpoint().getPlant().getPlantCd(), bean.getVerladestrasseId());
		}
		else if (!StringUtils.empty(bean.getWerkId())) {
			lp = getLoadingpoint(bean.getWerkId(), bean.getVerladestrasseId());
		}
		if (lp != null) {
			bizevent.setShippingpoint(lp.getShippingpoint());
		}
		else if (orderItem != null) {
			bizevent.setShippingpoint(orderItem.getOrder().getShippingpoint());
		}
		load.setWsStartLoadingpoint(lp);
		AssignmentPartEntity loadAp = getAssignmentPart(load);
		if (loadAp != null) {
			bizevent.setAssignment(loadAp.getAssignment());
		}
		else {
		//			Filter filter = new FilterImpl();
		//			filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_WAGON, wagon));
		//			filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
		//			filter.addExpression(Restrictions.neq(AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_ASSIGN_STATUS + "." + AssignStatusBsEntity.ATTR_ASSIGN_STATUS_CD, Constant.ASSIGNMENT_STATUS_CANCELLED));
		//			filter.addExpression(Restrictions.neq(AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_ASSIGN_STATUS + "." + AssignStatusBsEntity.ATTR_ASSIGN_STATUS_CD, Constant.ASSIGNMENT_STATUS_FINISHED));
		//			filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_PART_TYPE + "." + AssignmentPartTypeBsEntity.ATTR_ASSIGNMENT_PART_TYPE_CD, Constant.ASSIGNMENT_PART_TYPE_LOAD));
		//			filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_SHIPPINGPOINT, bizevent.getShippingpoint()));
		//			List loadParts = getDaoProvider().getAssignmentPartEntityDao().find(filter);
			// java.lang.NullPointerException at org.hibernate.hql.ast.tree.FromClause.findIntendedAliasedFromElementBasedOnCrazyJPARequirements(FromClause.java:120)
			// boon 20100430: Another one here; Does work when aliases are used everywhere. Don't think it's a good idea to make changes to the DAO layer now so I decided to use direct JPA here (there's no authorization restrictions here).
			List loadParts = getDaoProvider().getDirectJpaDao().find("SELECT a FROM " + AssignmentPartEntity.class.getSimpleName() + " a" +
							" WHERE a." + AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_WAGON + " = ?1" +
							" AND a." + AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_ACTIVE_FLAG + " = ?2" +
							" AND a." + AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_ASSIGN_STATUS + "." + AssignStatusBsEntity.ATTR_ASSIGN_STATUS_CD + " <> ?3" +
							" AND a." + AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_ASSIGN_STATUS + "." + AssignStatusBsEntity.ATTR_ASSIGN_STATUS_CD + " <> ?4" +
							" AND a." + AssignmentPartEntity.ATTR_ASSIGNMENT_PART_TYPE + "." + AssignmentPartTypeBsEntity.ATTR_ASSIGNMENT_PART_TYPE_CD + " = ?5" +
							" AND a." + AssignmentPartEntity.ATTR_SHIPPINGPOINT + " = ?6", wagon, Boolean.TRUE, Constant.ASSIGNMENT_STATUS_CANCELLED, Constant.ASSIGNMENT_STATUS_FINISHED, Constant.ASSIGNMENT_PART_TYPE_LOAD, bizevent.getShippingpoint());
			if (!loadParts.isEmpty()) {
				// In the mean time, an order was manually dispatched to this wagon. Clean up and forbid loading this wagon.
				em.flush();
				getDaoProvider().getBizeventEntityDao().remove(bizevent);
				Filter filter = new FilterImpl();
				filter = new FilterImpl();
				filter.addExpression(Restrictions.eq(LoadEntity.ATTR_WAGON, wagon));
				filter.addExpression(Restrictions.isNull(LoadEntity.ATTR_LOADING_TS));
				filter.addExpression(Restrictions.isNull(LoadEntity.ATTR_UNLOADING_TS));
				List unassignedLoadList = getDaoProvider().getLoadEntityDao().find(filter);
				for (Iterator it=loadParts.iterator();it.hasNext();) {
					AssignmentPartEntity ap = (AssignmentPartEntity)it.next();
					unassignedLoadList.remove(ap.getLoad());
				}
				for (Iterator it=unassignedLoadList.iterator();it.hasNext();) {
					LoadEntity unassignedLoad = (LoadEntity)it.next();
					filter = new FilterImpl();
					filter.addExpression(Restrictions.eq(OrderItemEntity.ATTR_LOAD, unassignedLoad));
					if (getDaoProvider().getOrderItemEntityDao().count(filter) == 0) {
						getDaoProvider().getLoadEntityDao().remove(unassignedLoad);
					}
				}
				
				return createLogonEventResponse(ParallelRailResponseCode.APPLICATION_ERROR, "Another order than the selected one was assigned to the wagon");
			}
		}
		
		if (bean.getTareWeightKg() != null) {
			tareWeightT = getKgAsTons(bean.getTareWeightKg());
		}
		
		orderService.startLoad(bizevent, load, lp, tareWeightT, EventSource.RAIL);
		
		if ( (bizevent.getWagon() != null) && (bizevent.getOrder() != null) && (orderItem != null) && (bizevent.getAssignment() == null) ) {
			// Create an assignment for this automatically dispatched order.
			// In case of multiple containers, the assignment is created when processing the last container. 
			int unassignedOrderItems = 0;
			List orderItems = (List)LogonPackageHandlingUtil.getMaterialItems(bizevent.getOrder());
			for (Iterator it=orderItems.iterator();it.hasNext();) {
				OrderItemEntity otherOrderItem = (OrderItemEntity)it.next();
				if ( (!otherOrderItem.getOrderItemId().equals(orderItem.getOrderItemId())) && (otherOrderItem.getLoad().getContainer() == null) ) {
					unassignedOrderItems+=1;
				}
			}
			if (unassignedOrderItems == 0) {
				AssignmentEntity assignment = orderService.createAssignmentForRailWithOrderDispatchedOrLoaded(bizevent.getWagon().getWagonId(), bizevent.getOrder().getOrderNumber(), DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH), 0, 0, 0);
				bizevent.setAssignment(assignment);
			}
		}
		
		monitoringTablesService.processBizevent(bizevent);
		
		return createLogonEventResponse(ParallelRailResponseCode.OK);
	}
}
