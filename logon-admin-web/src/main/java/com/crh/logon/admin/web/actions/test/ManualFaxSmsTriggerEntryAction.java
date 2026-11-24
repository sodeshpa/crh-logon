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

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.forms.test.ManualFaxSmsTriggerForm;
import com.crh.logon.business.services.interfaces.TestService;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_ManualFaxSmsTriggerForm" path="/test/ManualFaxSmsTriggerEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="/test/ManualFaxSmsTriggerTile.def"
 */
public class ManualFaxSmsTriggerEntryAction extends AdminActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(ManualFaxSmsTriggerEntryAction.class);

	public ActionForward execute(
		ActionMapping mapping, ActionForm form,
		HttpServletRequest request,	HttpServletResponse response)
		throws IOException, ServletException
	{
		Authorization.authorize(Authorization.TEST_FAX_SMS);
		setSelectedTab("test", request);
		setSelectedSub1Tab("testfaxsms", request);
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
			Authorization.authorize(Authorization.TEST_FAX_SMS);
			setSelectedTab("test", request);
			setSelectedSub1Tab("testfaxsms", request);
			ManualFaxSmsTriggerForm fsForm;
			String forwardName = "success";
			
			final TestService testService = getServiceLocator().getTestService();
			
		    // Get the struts form.
			fsForm = (ManualFaxSmsTriggerForm) form;
		    fsForm.clear(
		    		testService.getFaxImplementations(),
		    		testService.getSmsImplementations());

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
