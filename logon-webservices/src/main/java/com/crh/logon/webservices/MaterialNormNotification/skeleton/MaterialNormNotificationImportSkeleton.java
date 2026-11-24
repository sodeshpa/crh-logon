/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.webservices.MaterialNormNotification.skeleton;

import org.apache.axis2.AxisFault;

import com.crh.logon.exception.WsServerSideExceptionHandler;
import com.holcim.logon.sapdata.request.MaterialNormNotification;
import com.crh.logon.utils.EntityHelper;
import com.crh.logon.utils.SkeletonHelper;
import com.crh.logon.webservices.mapper.com_holcim_logon_sapdata_request_MaterialNormNotificationMapper;
import com.crh.logon.webservices.services.SapService;
import com.crh.logon.webservices.MaterialNormNotification.documents.MaterialNormNotificationPutDocument;
import com.crh.logon.webservices.MaterialNormNotification.documents.MaterialNormNotificationPutDocument.MaterialNormNotificationPut;
import com.crh.logon.webservices.MaterialNormNotification.documents.MaterialNormNotificationPutResponseDocument;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class MaterialNormNotificationImportSkeleton implements
MaterialNormNotificationImportSkeletonInterface {

	private static final JcanLogger LOG = JcanLoggerFactory
			.getLogger(MaterialNormNotificationImportSkeleton.class);

	private static final String WEBSERVICE_NAME = "MaterialNormNotificationImport";
	private static final String OPERATION_PUT_NAME = "material_norm_notification_put";

	public MaterialNormNotificationPutResponseDocument material_norm_notification_put(
			MaterialNormNotificationPutDocument requestDocument) throws AxisFault {
		// [[
		MaterialNormNotificationPutResponseDocument result = null;
		Object[][] params = {{"requestDocument", requestDocument}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "material_norm_notification_put", params);
		}
		// ]]
		try {
			// Request-objects
			MaterialNormNotificationPut pCondput = requestDocument.getMaterialNormNotificationPut();
			MaterialNormNotification requestData = (pCondput == null ? null : pCondput.getMaterialnorm());
			
			SkeletonHelper.checkSoapRequest(requestDocument, requestData);
			
			SapService service = SkeletonHelper.getSapService();
			com.crh.logon.persistence.entities.sapdata.MaterialNormNotification bpEntity = com_holcim_logon_sapdata_request_MaterialNormNotificationMapper
					.mapSrvToEntity(requestData);
			
			// Setting controlling attributes used by the stored procedures of
			// the staging area.
			EntityHelper.setControllingAttributes(bpEntity);
			
			// Data reception: calling SapService to store the entity
			service.writeMaterialNormNotification(bpEntity);
			
			result = (MaterialNormNotificationPutResponseDocument) SkeletonHelper
					.composeResponse(MaterialNormNotificationPutResponseDocument.class,
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
