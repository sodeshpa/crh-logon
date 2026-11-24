/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.business.stores.dictionaries;


import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericReadOnlyInterface;
import com.crh.logon.persistence.entities.refdata.OutputDocumentTypeBsEntity;

@Local
public interface OutputDocumentTypeStoreInterface extends GenericReadOnlyInterface<OutputDocumentTypeBsEntity> {
	
	/**
	 * It returns with the entity instance of the DeliveryNoteType from the OutputDocumentTypes  
	 *
	 */
	public OutputDocumentTypeBsEntity getDeliveryNoteType();
	
	/**
	 * It returns with the entity instance of the CollectiveDeliveryNoteType from the OutputDocumentTypes  
	 *
	 */
	public OutputDocumentTypeBsEntity getCollectiveDeliveryNoteType();
	
	/**
	 * It returns with the entity instance of the ExportInvoiceType from the OutputDocumentTypes  
	 *
	 */
	public OutputDocumentTypeBsEntity getExportInvoiceType();
	
	/**
	 * It returns with the entity instance of the CashSalesType from the OutputDocumentTypes  
	 *
	 */
	public OutputDocumentTypeBsEntity getCashSalesType();
	
}
