/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.webservices.VendorNotification.skeleton;

import org.apache.axis2.AxisFault;

import com.crh.logon.webservices.VendorNotification.documents.VendorNotificationPutDocument;
import com.crh.logon.webservices.VendorNotification.documents.VendorNotificationPutResponseDocument;

public interface VendorNotificationImportSkeletonInterface {
	
	public VendorNotificationPutResponseDocument vendor_notification_put(
			VendorNotificationPutDocument requestDocument) throws AxisFault;

}
