/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.business.stores.dictionaries;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.common.base.GenericReadOnlyProvider;
import com.crh.logon.constants.ArchivingLogEvent;
import com.crh.logon.persistence.entities.refdata.ArchivingLogEventBsEntity;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class ArchivingLogEventStore extends GenericReadOnlyProvider<ArchivingLogEventBsEntity> implements ArchivingLogEventStoreInterface {

	public ArchivingLogEventBsEntity getStartArchivingEvent() {
		return em.find(ArchivingLogEventBsEntity.class, ArchivingLogEvent.START_ORDER_ARCHIVING.getValue());
	}

	public ArchivingLogEventBsEntity getTransmitToArchivingServiceEvent() {
		return em.find(ArchivingLogEventBsEntity.class, ArchivingLogEvent.TRANSMIT_DATA_TO_ARCHIVING_SERVICE.getValue());

	}

	public ArchivingLogEventBsEntity getReceiveResponseFromArchivingServiceEvent() {
		return em.find(ArchivingLogEventBsEntity.class, ArchivingLogEvent.RECEIVE_RESPONSE_FROM_ARCHIVING_SERVICE.getValue());

	}
	
	public ArchivingLogEventBsEntity getUnableToDeleteOrderServiceEvent() {
		return em.find(ArchivingLogEventBsEntity.class, ArchivingLogEvent.UNABLE_TO_DELETE_ORDER.getValue());
	}
	
	public ArchivingLogEventBsEntity getWholeBatchFailedServiceEvent() {
		return em.find(ArchivingLogEventBsEntity.class, ArchivingLogEvent.WHOLE_BATCH_FAILED.getValue());
	}
}
