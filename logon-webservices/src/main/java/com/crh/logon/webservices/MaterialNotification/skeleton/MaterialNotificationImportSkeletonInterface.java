/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.webservices.MaterialNotification.skeleton;

import org.apache.axis2.AxisFault;

import com.crh.logon.webservices.MaterialNotification.documents.MaterialNotificationPutDocument;
import com.crh.logon.webservices.MaterialNotification.documents.MaterialNotificationPutResponseDocument;

public interface MaterialNotificationImportSkeletonInterface {

	public MaterialNotificationPutResponseDocument material_notification_put(
			MaterialNotificationPutDocument requestDocument) throws AxisFault;

}
