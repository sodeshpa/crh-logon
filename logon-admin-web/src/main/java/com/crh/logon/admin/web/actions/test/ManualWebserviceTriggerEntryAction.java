/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.test.ManualWebserviceTriggerForm;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_ManualWebserviceTriggerForm" path="/test/ManualWebserviceTriggerEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="/test/ManualWebserviceTriggerTile.def"
 */
public class ManualWebserviceTriggerEntryAction extends AdminActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(ManualWebserviceTriggerEntryAction.class);

	public ActionForward execute(
		ActionMapping mapping, ActionForm form,
		HttpServletRequest request,	HttpServletResponse response)
		throws IOException, ServletException
	{
		Authorization.authorize(Authorization.TEST_WEBSERVICE);
		setSelectedTab("test", request);
		setSelectedSub1Tab("testwebservice", request);
		// [[
		ActionForward result = null;
		Object[][] params = {{"mapping", mapping},
							{"form", form},
							{"request", request},
							{"response", response}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "execute", params);
		}
		// ]]
		try {
			if (!getConfigBean().getWsclientEnableManualWebserviceTrigger()) {
				throw new LogonException("The web service test screen is disabled by the administrator", Reason.RES_ERROR_CONFIG_NOT_VALID);				
			}
			String forwardName = "success";
			ManualWebserviceTriggerForm wsForm = (ManualWebserviceTriggerForm) form;
			wsForm.clear();
			wsForm.getWebServiceOptions().setShippingpointList(getServiceLocator().getTestService().loadShippingpoints(request.getLocale()));
					    
			return result = mapping.findForward(forwardName);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
}
