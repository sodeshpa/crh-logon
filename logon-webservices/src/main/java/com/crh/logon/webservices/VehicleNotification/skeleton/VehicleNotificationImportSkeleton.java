/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.webservices.VehicleNotification.skeleton;

import com.crh.logon.exception.WsServerSideExceptionHandler;
import org.apache.axis2.AxisFault;

import com.holcim.logon.sapdata.request.VehicleNotification;
import com.crh.logon.utils.EntityHelper;
import com.crh.logon.utils.SkeletonHelper;
import com.crh.logon.webservices.mapper.com_holcim_logon_sapdata_request_VehicleNotificationMapper;
import com.crh.logon.webservices.services.SapService;
import com.crh.logon.webservices.VehicleNotification.documents.VehicleNotificationPutDocument;
import com.crh.logon.webservices.VehicleNotification.documents.VehicleNotificationPutDocument.VehicleNotificationPut;
import com.crh.logon.webservices.VehicleNotification.documents.VehicleNotificationPutResponseDocument;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class VehicleNotificationImportSkeleton implements
		VehicleNotificationImportSkeletonInterface {

	private static final JcanLogger LOG = JcanLoggerFactory
			.getLogger(VehicleNotificationImportSkeleton.class);

	public static String WEBSERVICE_NAME = "VehicleNotificationImport";
	public static String OPERATION_PUT_NAME = "vehicle_notification_put";

	public VehicleNotificationPutResponseDocument vehicle_notification_put(
			VehicleNotificationPutDocument requestDocument) throws AxisFault {
		// [[
		VehicleNotificationPutResponseDocument result = null;
		Object[][] params = {{"requestDocument", requestDocument}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "vehicle_notification_put", params);
		}
		// ]]
		try {
			// Request-objects
			VehicleNotificationPut pCondput = requestDocument.getVehicleNotificationPut();
			VehicleNotification requestData = (pCondput == null ? null : pCondput
					.getVehicle());
			
			SkeletonHelper.checkSoapRequest(requestDocument, requestData);
			
			SapService service = SkeletonHelper.getSapService();
			com.crh.logon.persistence.entities.sapdata.VehicleNotification vnEntity = com_holcim_logon_sapdata_request_VehicleNotificationMapper
					.mapSrvToEntity(requestData);
			
			// Setting controlling attributes used by the stored procedures of
			// the staging area.
			EntityHelper.setControllingAttributes(vnEntity);
			
			// Data reception: calling SapService to store the entity
			service.writeVehicleNotification(vnEntity);
			
			result = (VehicleNotificationPutResponseDocument) SkeletonHelper
					.composeResponse(VehicleNotificationPutResponseDocument.class,
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
