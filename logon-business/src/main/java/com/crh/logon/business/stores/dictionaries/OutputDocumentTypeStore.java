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
import com.crh.logon.constants.OutputDocumentType;
import com.crh.logon.persistence.entities.refdata.OutputDocumentTypeBsEntity;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class OutputDocumentTypeStore extends GenericReadOnlyProvider<OutputDocumentTypeBsEntity> implements OutputDocumentTypeStoreInterface {

	public OutputDocumentTypeBsEntity getDeliveryNoteType() {
		return em.find(OutputDocumentTypeBsEntity.class, OutputDocumentType.DELIVERY_NOTE.getValue());
	}

	public OutputDocumentTypeBsEntity getCollectiveDeliveryNoteType() {
		return em.find(OutputDocumentTypeBsEntity.class, OutputDocumentType.COLLECTIVE_DELIVERY_NOTE.getValue());
	}

	public OutputDocumentTypeBsEntity getExportInvoiceType() {
		return em.find(OutputDocumentTypeBsEntity.class, OutputDocumentType.EXPORT_INVOICE.getValue());
	}

	public OutputDocumentTypeBsEntity getCashSalesType() {
		return em.find(OutputDocumentTypeBsEntity.class, OutputDocumentType.CASH_SALES.getValue());
	}

}
