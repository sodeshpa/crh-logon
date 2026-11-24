/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.servlets;

/**
 * @author AdNovum Informatik AG
 */
import javax.servlet.http.HttpServlet;

import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.web.utils.ServiceLocatorProvider;

public abstract class LogonServlet extends HttpServlet {
	
	public LogonServlet() {
		super();
	}
	
	protected ServiceLocator getServiceLocator() {
		return ServiceLocatorProvider.getServiceLocator();
	}
}