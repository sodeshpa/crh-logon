package com.crh.logon.business.services.impl;

import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.OrderShippingNotificationServiceDispatcher;
import com.crh.logon.business.services.interfaces.NotificationService;
import com.crh.logon.business.services.localinterfaces.OrderShippingNotificationServiceLocalInterface;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;

@Stateless
@AdnTrace
public class OrderShippingNotificationServiceBean extends OrderShippingNotificationServiceDispatcher implements OrderShippingNotificationServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(OrderShippingNotificationServiceBean.class);
	
	@EJB
	NotificationService notificationService;

	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public Future<Boolean> sendEmail(OrderEntity orderEntity, AssignmentEntity assignmentEntity) {
		Boolean result = Boolean.FALSE;
		
		try {
			notificationService.sendLoadConfirmationByEmail(orderEntity, assignmentEntity);
			result = Boolean.TRUE;
		} catch (Exception e) {
			LOG.error("Error at sending email notification", e);
		}
		
		return new AsyncResult<Boolean>(result);
	}

	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public Future<Boolean> sendSMS(OrderEntity orderEntity, AssignmentEntity assignmentEntity) {
		Boolean result = Boolean.FALSE;
		
		try {
			notificationService.sendLoadConfirmationBySms(orderEntity, assignmentEntity);
			result = Boolean.TRUE;
		} catch (Exception e) {
			LOG.error("SMS sending of load confirmation failed for order number " + orderEntity.getOrderNumber(), e);
		}
		
		return new AsyncResult<Boolean>(result);
	}

	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public Future<Boolean> sendFax(OrderEntity orderEntity, AssignmentEntity assignmentEntity) {
		Boolean result = Boolean.FALSE;
		
		try {
			notificationService.sendLoadConfirmationByFax(orderEntity, assignmentEntity);
			result = Boolean.TRUE;
		} catch (Exception e) {
			LOG.error("Fax sending of load confirmation failed for order number " + orderEntity.getOrderNumber(), e);
		}
		
		return new AsyncResult<Boolean>(result);
	}
	

}
