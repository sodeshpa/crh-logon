/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.common.base.GenericCrudProvider;
import com.crh.logon.business.stores.dictionaries.ArchivingLogEventStoreInterface;
import com.crh.logon.persistence.entities.common.ArchivingLogEntity;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class ArchivingLogStore extends GenericCrudProvider<ArchivingLogEntity> implements ArchivingLogStoreInterface {

	@EJB ArchivingLogEventStoreInterface archivingLogEventStore;
	
	public void startArchiving(Long orderId) {
		ArchivingLogEntity logEntry = new ArchivingLogEntity();
		logEntry.setOrderId(orderId);
		logEntry.setArchivingLogEvent(archivingLogEventStore.getStartArchivingEvent());
		em.persist(logEntry);
	}

	public void transmitDataToArchivingService(Long orderId, String sourceMessage) {
		ArchivingLogEntity logEntry = new ArchivingLogEntity();
		logEntry.setOrderId(orderId);
		logEntry.setLogText(sourceMessage);
		logEntry.setArchivingLogEvent(archivingLogEventStore.getTransmitToArchivingServiceEvent());
		em.persist(logEntry);		
	}

	public void receivedResponseFromArchivingService(Long orderId,
			String responseMessage) {
		ArchivingLogEntity logEntry = new ArchivingLogEntity();
		logEntry.setOrderId(orderId);
		logEntry.setLogText(responseMessage);
		logEntry.setArchivingLogEvent(archivingLogEventStore.getReceiveResponseFromArchivingServiceEvent());
		em.persist(logEntry);
	}	
	
	public void unableToDeleteOrder(Long orderId, String message){
		ArchivingLogEntity logEntry = new ArchivingLogEntity();
		logEntry.setOrderId(orderId);
		logEntry.setLogText(message);
		logEntry.setArchivingLogEvent(archivingLogEventStore.getUnableToDeleteOrderServiceEvent());
		em.persist(logEntry);
	}
	
	public void wholeBatchFailed(){
		ArchivingLogEntity logEntry = new ArchivingLogEntity();
		logEntry.setArchivingLogEvent(archivingLogEventStore.getWholeBatchFailedServiceEvent());
		em.persist(logEntry);
	}
}