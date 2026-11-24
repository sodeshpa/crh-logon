/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.webservices.MaterialNotification.skeleton;

import com.crh.logon.exception.WsServerSideExceptionHandler;
import com.crh.logon.utils.EntityHelper;
import org.apache.axis2.AxisFault;

import com.holcim.logon.sapdata.request.MaterialNotification;
import com.crh.logon.utils.SkeletonHelper;
import com.crh.logon.webservices.mapper.com_holcim_logon_sapdata_request_MaterialNotificationMapper;
import com.crh.logon.webservices.services.SapService;
import com.crh.logon.webservices.MaterialNotification.documents.MaterialNotificationPutDocument;
import com.crh.logon.webservices.MaterialNotification.documents.MaterialNotificationPutDocument.MaterialNotificationPut;
import com.crh.logon.webservices.MaterialNotification.documents.MaterialNotificationPutResponseDocument;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class MaterialNotificationImportSkeleton implements
MaterialNotificationImportSkeletonInterface {

	private static final JcanLogger LOG = JcanLoggerFactory
			.getLogger(MaterialNotificationImportSkeleton.class);

	private static final String WEBSERVICE_NAME = "MaterialNotificationImport";
	private static final String OPERATION_PUT_NAME = "material_notification_put";

	public MaterialNotificationPutResponseDocument material_notification_put(
			MaterialNotificationPutDocument requestDocument) throws AxisFault {
		// [[
		MaterialNotificationPutResponseDocument result = null;
		Object[][] params = {{"requestDocument", requestDocument}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "material_notification_put", params);
		}
		// ]]
		try {
			// Request-objects
			MaterialNotificationPut pCondput = requestDocument.getMaterialNotificationPut();
			MaterialNotification requestData = (pCondput == null ? null : pCondput.getMaterial());
			
			SkeletonHelper.checkSoapRequest(requestDocument, requestData);
			
			SapService service = SkeletonHelper.getSapService();
			com.crh.logon.persistence.entities.sapdata.MaterialNotification bpEntity = com_holcim_logon_sapdata_request_MaterialNotificationMapper
					.mapSrvToEntity(requestData);
			
			// Setting controlling attributes used by the stored procedures of
			// the staging area.
			EntityHelper.setControllingAttributes(bpEntity);
			
			// Data reception: calling SapService to store the entity
			service.writeMaterialNotification(bpEntity);
			
			result = (MaterialNotificationPutResponseDocument) SkeletonHelper
					.composeResponse(MaterialNotificationPutResponseDocument.class,
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
