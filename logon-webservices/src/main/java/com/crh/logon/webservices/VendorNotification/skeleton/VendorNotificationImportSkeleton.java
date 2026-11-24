/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.webservices.VendorNotification.skeleton;

import com.crh.logon.exception.WsServerSideExceptionHandler;
import com.crh.logon.utils.EntityHelper;
import org.apache.axis2.AxisFault;

import com.holcim.logon.sapdata.request.VendorNotification;
import com.crh.logon.utils.SkeletonHelper;
import com.crh.logon.webservices.mapper.com_holcim_logon_sapdata_request_VendorNotificationMapper;
import com.crh.logon.webservices.services.SapService;
import com.crh.logon.webservices.VendorNotification.documents.VendorNotificationPutDocument;
import com.crh.logon.webservices.VendorNotification.documents.VendorNotificationPutDocument.VendorNotificationPut;
import com.crh.logon.webservices.VendorNotification.documents.VendorNotificationPutResponseDocument;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class VendorNotificationImportSkeleton implements
		VendorNotificationImportSkeletonInterface {

	private static final JcanLogger LOG = JcanLoggerFactory
			.getLogger(VendorNotificationImportSkeleton.class);

	public static String WEBSERVICE_NAME = "VendorNotificationImport";
	public static String OPERATION_PUT_NAME = "vendor_notification_put";

	public VendorNotificationPutResponseDocument vendor_notification_put(
			VendorNotificationPutDocument requestDocument) throws AxisFault {
		// [[
		VendorNotificationPutResponseDocument result = null;
		Object[][] params = {{"requestDocument", requestDocument}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "vendor_notification_put", params);
		}
		// ]]
		try {
			// Request-objects
			VendorNotificationPut pCondput = requestDocument.getVendorNotificationPut();
			VendorNotification requestData = (pCondput == null ? null : pCondput
					.getVendor());
			
			SkeletonHelper.checkSoapRequest(requestDocument, requestData);
			
			SapService service = SkeletonHelper.getSapService();
			com.crh.logon.persistence.entities.sapdata.VendorNotification vnEntity = com_holcim_logon_sapdata_request_VendorNotificationMapper
					.mapSrvToEntity(requestData);
			
			// Setting controlling attributes used by the stored procedures of
			// the staging area.
			EntityHelper.setControllingAttributes(vnEntity);
			
			// Data reception: calling SapService to store the entity
			service.writeVendorNotification(vnEntity);
			
			result = (VendorNotificationPutResponseDocument) SkeletonHelper
					.composeResponse(VendorNotificationPutResponseDocument.class,
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
