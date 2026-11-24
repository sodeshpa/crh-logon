/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.webservices.SalesDocumentNotification.skeleton;

import com.crh.logon.exception.WsServerSideExceptionHandler;
import com.crh.logon.utils.EntityHelper;
import org.apache.axis2.AxisFault;

import com.holcim.logon.sapdata.request.SalesDocumentNotification;
import com.crh.logon.utils.SkeletonHelper;
import com.crh.logon.webservices.mapper.com_holcim_logon_sapdata_request_SalesDocumentNotificationMapper;
import com.crh.logon.webservices.services.SapService;
import com.crh.logon.webservices.SalesDocumentNotification.documents.SalesDocumentNotificationPutDocument;
import com.crh.logon.webservices.SalesDocumentNotification.documents.SalesDocumentNotificationPutDocument.SalesDocumentNotificationPut;
import com.crh.logon.webservices.SalesDocumentNotification.documents.SalesDocumentNotificationPutResponseDocument;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class SalesDocumentNotificationImportSkeleton implements
SalesDocumentNotificationImportSkeletonInterface {

	private static final JcanLogger LOG = JcanLoggerFactory
			.getLogger(SalesDocumentNotificationImportSkeleton.class);

	private static final String WEBSERVICE_NAME = "SalesDocumentNotificationImport";
	private static final String OPERATION_PUT_NAME = "sales_document_notification_put";

	public SalesDocumentNotificationPutResponseDocument sales_document_notification_put(
			SalesDocumentNotificationPutDocument requestDocument) throws AxisFault {
		// [[
		SalesDocumentNotificationPutResponseDocument result = null;
		Object[][] params = {{"requestDocument", requestDocument}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sales_document_notification_put", params);
		}
		// ]]
		try {
			// Request-objects
			SalesDocumentNotificationPut pCondput = requestDocument.getSalesDocumentNotificationPut();
			SalesDocumentNotification requestData = (pCondput == null ? null : pCondput.getSalesdocument());
			
			SkeletonHelper.checkSoapRequest(requestDocument, requestData);
			
			SapService service = SkeletonHelper.getSapService();
			com.crh.logon.persistence.entities.sapdata.SalesDocumentNotification bpEntity = com_holcim_logon_sapdata_request_SalesDocumentNotificationMapper
					.mapSrvToEntity(requestData);
			
			// Setting controlling attributes used by the stored procedures of
			// the staging area.
			EntityHelper.setControllingAttributes(bpEntity);
			
			// Data reception: calling SapService to store the entity
			service.writeSalesDocumentNotification(bpEntity);
			
			result = (SalesDocumentNotificationPutResponseDocument) SkeletonHelper
					.composeResponse(SalesDocumentNotificationPutResponseDocument.class,
							WEBSERVICE_NAME, OPERATION_PUT_NAME, null);
		}
		catch (Throwable throwable) {
			WsServerSideExceptionHandler.handle(
					throwable, WEBSERVICE_NAME, OPERATION_PUT_NAME);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
		
		return result;
	}

}
