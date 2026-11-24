/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.webservices.VehicleNotification.skeleton;

import org.apache.axis2.AxisFault;

import com.crh.logon.webservices.VehicleNotification.documents.VehicleNotificationPutDocument;
import com.crh.logon.webservices.VehicleNotification.documents.VehicleNotificationPutResponseDocument;

public interface VehicleNotificationImportSkeletonInterface {
	
	public VehicleNotificationPutResponseDocument vehicle_notification_put(
			VehicleNotificationPutDocument requestDocument) throws AxisFault;

}
