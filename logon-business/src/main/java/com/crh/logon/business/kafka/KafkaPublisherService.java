/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.kafka;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.TransactionSynchronizationRegistry;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.kafka.KafkaProducerService.KafkaRecordDTO;
import com.crh.logon.business.kafka.mapper.KafkaDTOMapper;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.holcim.logon.eventbeans.in.logon_lds_interface.Request;
import com.holcim.logon.eventbeans.in.mop.DeliveryNoteRequest;
import com.holcim.logon.eventbeans.in.mop.MopRequest;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class KafkaPublisherService {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(KafkaPublisherService.class);

	private static final String TRX_ATTR_MESSAGES = "KafkaPublisherService.messages";

	private static final String TOPIC_DELIVERY = "cem.event.logon-delivery";

	private static final String WITH_SIGNATURE = " with signature";

	@Resource(mappedName = "java:comp/TransactionSynchronizationRegistry")
	private TransactionSynchronizationRegistry transactionRegistry;

	@EJB
	private KafkaProducerService kafkaProducerService;

	@EJB
	private KafkaDTOSerializer kafkaDTOSerializer;

	@Inject
	private KafkaDTOMapper kafkaDTOMapper;

	public void publishMOPEvent(OrderEntity orderEntity, MopRequest mopRequest) {
		if (checkIfNotSendToKafka(orderEntity)) {
			return;
		}
		String eventName = mopRequest.getEventName();
		if (mopRequest instanceof DeliveryNoteRequest && ((DeliveryNoteRequest) mopRequest).getSignature() != null) {
			eventName = eventName.concat(WITH_SIGNATURE);
		}
		publish(new KafkaRecordDTO(TOPIC_DELIVERY, kafkaDTOSerializer
				.serialize(kafkaDTOMapper.toDeliveryOrder(orderEntity, eventName, mopRequest.getEventTime()))));
	}

	public void publishLDSEvent(OrderEntity orderEntity, Request ldsRequest, String eventName) {
		if (checkIfNotSendToKafka(orderEntity)) {
			return;
		}
		publish(new KafkaRecordDTO(TOPIC_DELIVERY, kafkaDTOSerializer
				.serialize(kafkaDTOMapper.toDeliveryOrder(orderEntity, eventName, ldsRequest.getEventTime()))));
	}

	public void publishOrderStatusChange(OrderEntity orderEntity) {
		if (checkIfNotSendToKafka(orderEntity)) {
			return;
		}
		if (Constant.ORDER_STATUS_DELETED != orderEntity.getOrderStatus().getOrderStatusCd()) {
			// deleted orders are not published
			publish(new KafkaRecordDTO(TOPIC_DELIVERY, kafkaDTOSerializer
					.serialize(kafkaDTOMapper.toDeliveryOrder(orderEntity, kafkaDTOMapper.toStatusText(orderEntity), null))));
		}
	}

	public void publishNewOrder(OrderEntity orderEntity) {
		if (checkIfNotSendToKafka(orderEntity)) {
			return;
		}
		publish(new KafkaRecordDTO("cem.event.logon-new-order",
				kafkaDTOSerializer.serialize(kafkaDTOMapper.toNewOrder(orderEntity))));
	}

	public void publishCancelledOrder(OrderEntity orderEntity) {
		if (checkIfNotSendToKafka(orderEntity)) {
			return;
		}
		publish(new KafkaRecordDTO("cem.event.logon-cancel-order",
				kafkaDTOSerializer.serialize(kafkaDTOMapper.toCancelledOrder(orderEntity))));
	}

	public void publishReturnOrder(OrderEntity orderEntity) {
		if (checkIfNotSendToKafka(orderEntity)) {
			return;
		}
		publish(new KafkaRecordDTO("cem.event.logon-return-order",
				kafkaDTOSerializer.serialize(kafkaDTOMapper.toReturnOrder(orderEntity))));
	}

	private boolean checkIfNotSendToKafka(OrderEntity orderEntity) {
		try {
			if (kafkaProducerService.isKafkaNotConfigured() || orderEntity == null) {
				return true;
			}
		}
		catch (Exception e) {
			LOG.error("Failed to check if kafka is configured", e);
			return true;
		}

		// Only SAreas of France should be reported
		SareaEntity sarea = orderEntity.getSarea();
		String sorgCd = sarea.getSorg().getSorgCd();
		return !sorgCd.startsWith("FR") && !sorgCd.startsWith("AD");
	}

	private void publish(KafkaRecordDTO dto) {
		try {
			switch (transactionRegistry.getTransactionStatus()) {
				case Status.STATUS_NO_TRANSACTION:
					// in case there is no transaction, just publish it directly
					LOG.debug("publish w/o transaction is called: " + this);
					kafkaProducerService.publish(dto);
					break;
				case Status.STATUS_ACTIVE:
					LOG.debug("publish w/ transaction is called: " + this);
					List<KafkaRecordDTO> msgs = (List<KafkaRecordDTO>) transactionRegistry.getResource(TRX_ATTR_MESSAGES);
					if (msgs == null) {
						msgs = new ArrayList<>();
						transactionRegistry.putResource(TRX_ATTR_MESSAGES, msgs);
						transactionRegistry.registerInterposedSynchronization(new Synchronization() {
							@Override
							public void beforeCompletion() {
								// we have to use before commit otherwise we lose the resources, but the JPA commit can still fail
								// FIXME 01-Apr-2021/marty: Find a better solution for transaction handling
								List<KafkaRecordDTO> msgs = (List<KafkaRecordDTO>) transactionRegistry.getResource(TRX_ATTR_MESSAGES);
								if (msgs != null && !msgs.isEmpty()) {
									try {
										// because it's an async method, the container will not immediately process this call
										// however the container will pass in this object as reference, so the producer would only get an empty list
										kafkaProducerService.publish(new ArrayList<>(msgs));
									}
									catch (Exception e) {
										// To avoid inconsistency after commit
										LOG.error("Couldn't publish records after transaction committed", e);
									}
								}
							}

							@Override
							public void afterCompletion(int status) {

							}
						});
					}
					msgs.add(dto);
					break;
				default:
					throw new LogonException("We cannot publish for an inconsistent transaction", Reason.RES_IMPL_BUG);
			}
		}
		catch (Exception e) {
			LOG.error("Couldn't publish records", e);
		}
	}
}

