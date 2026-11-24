/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain;

import java.sql.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.common.base.GenericCrudProvider;
import com.crh.logon.business.ordercreation.dto.OrderDeliveryMinMaxDateDTO;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.refdata.ArchivingStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusBsEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class OrderStore extends GenericCrudProvider<OrderEntity> implements OrderStoreInterface {
	
	private static final String DELETE_ORDER_STOREDPROCEDURE = "BEGIN " + Constant.SCHEMA_USER_PREPEND + "archiving_delete_data(?); END;";

	@EJB RefDataGenericsServiceLocalInterface referentialDataService;
	
	
	public OrderEntity findAndIntializeById(Long orderId) {

		DaoProvider daoProvider = getDaoProvider();
		ReadWriteDao<OrderEntity> orderDao = daoProvider.getOrderEntityDao();

		OrderEntity order = orderDao.findById(orderId);
		if (order != null) {
			order.initialize();
		}
		return order;

	}
	
	public void markOrderAsArchived(Long archivableOrderId) {		
		OrderEntity archivableOrder = findById(archivableOrderId);
		OrderStatusBsEntity orderStatus = archivableOrder.getOrderStatus(); 
		Long currentOrderStatusCode = orderStatus.getOrderStatusCd(); 
		
		if (currentOrderStatusCode.equals(Constant.ORDER_STATUS_FINISHED)
			||currentOrderStatusCode.equals(Constant.ORDER_STATUS_DELETED)) {
				archivableOrder.setArchivingStatus(referentialDataService.findBsEntity(ArchivingStatusBsEntity.class, 
				Constant.ARCHIVING_STATUS_DELETABLE));
		}
		else { 
				throw new LogonException("Cannot mark order as archived : orderId " + archivableOrderId + ", current orderStatus: " + currentOrderStatusCode, Reason.RES_IMPL_BUG);
		}		
	}
	
	@Override
	public void markOrderAsNonArchivable(Long archivableOrderId) {		
		OrderEntity archivableOrder = findById(archivableOrderId);
		
		archivableOrder.setArchivingStatus(referentialDataService.findBsEntity(ArchivingStatusBsEntity.class, 
				Constant.ARCHIVING_STATUS_ARCHIVING_FAILED));	
	}
	
	@Override
	public void markOrderAsDeletable(Long orderId) {
		OrderEntity order = findById(orderId);
		order.setArchivingStatus(referentialDataService.findBsEntity(ArchivingStatusBsEntity.class, 
				Constant.ARCHIVING_STATUS_DELETABLE));
	}
	
	@Override
	public void markOrderAsDeletionFailed(Long orderId) {
		OrderEntity order = findById(orderId);
		order.setArchivingStatus(referentialDataService.findBsEntity(ArchivingStatusBsEntity.class, 
				Constant.ARCHIVING_STATUS_DELETION_FAILED));
	}

	public void removeOrder(Long orderId) {							
		Query query = em.createNativeQuery(DELETE_ORDER_STOREDPROCEDURE);
		query.setParameter(1, orderId);
		query.executeUpdate();	
	}
	
	public OrderDeliveryMinMaxDateDTO findMinMaxOrderDeliveryFromToDate(Long custorderId, Long orderId) {

		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<OrderDeliveryMinMaxDateDTO> query = criteriaBuilder.createQuery(OrderDeliveryMinMaxDateDTO.class);
		Root<OrderEntity> orderRoot = query.from(OrderEntity.class);

		Expression<String> custorder = orderRoot.get(OrderEntity.ATTR_CUSTORDER);
		Expression<String> order = orderRoot.get(OrderEntity.ATTR_ORDER_ID);
		Expression<Date> deliveryFromDat = orderRoot.get(OrderEntity.ATTR_DELIVERY_FROM_DAT);
		Expression<Date> deliveryToDat = orderRoot.get(OrderEntity.ATTR_DELIVERY_TO_DAT);
		
		Predicate custorderEqual = criteriaBuilder.equal(custorder, custorderId);
		Predicate orderNotEqual = criteriaBuilder.notEqual(order, orderId);
	
		query.multiselect(criteriaBuilder.least(deliveryFromDat), criteriaBuilder.greatest(deliveryToDat));
		query.where( custorderEqual, orderNotEqual);
		query.groupBy(orderRoot.get(OrderEntity.ATTR_CUSTORDER));
		
		TypedQuery<OrderDeliveryMinMaxDateDTO> orderDeliveryMinMaxQuery = em.createQuery(query);
		
		try {
			return orderDeliveryMinMaxQuery.getSingleResult();
		} 
		catch (NoResultException noResultException) {
			return null;
		}
	}	
	
	public long getCountAssignmentForOrderId(Long orderId) {
		ReadWriteDao<AssignmentPartEntity> asspDao = getDaoProvider().getAssignmentPartEntityDao();
		
		Filter<AssignmentPartEntity> filter = new FilterImpl<>();
		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ORDER_ID, orderId));
		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_ACTIVE_FLAG, true));
		long count = asspDao.count(filter);
		
		return count;
	}

}

