/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.utils;

import com.crh.logon.business.services.utils.ServiceLocator;

/**
 * ServiceLocatorProvider is initialized by LogonInitListener.
 * It must not be used before the LogonInitListener got its turn to set
 * the ServiceLocator
 * 
 * This class is implemented as an enum in order to make sure that we 
 * don't create uneccessary ServiceLocators, and to give actions an
 * easy way to access a ServiceLocator.
 * 
 * @author AdNovum Informatik AG
 */
public enum ServiceLocatorProvider {
	inst;
	private ServiceLocator serviceLocator;
	
	public static ServiceLocator getServiceLocator() {
		return inst.serviceLocator;
	}
	
	public static void setServiceLocator(ServiceLocator serviceLocator) {
		inst.serviceLocator = serviceLocator;
	}
}
