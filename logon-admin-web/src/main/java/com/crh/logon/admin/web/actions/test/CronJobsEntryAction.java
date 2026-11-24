/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.forms.test.CronJobsForm;
import com.crh.logon.business.services.localinterfaces.CronJobServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LocalServices;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_CronJobsForm" path="/test/CronJobsEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="/test/CronJobsTile.def"
 */
@AdnTrace
public class CronJobsEntryAction extends AdminActionSupport {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(CronJobsEntryAction.class);

	public ActionForward execute(
		ActionMapping mapping, ActionForm form,
		HttpServletRequest request,	HttpServletResponse response)
		throws IOException, ServletException
 {
		Authorization.authorize(Authorization.TEST_CRONJOBS);
		setSelectedTab("test", request);
		setSelectedSub1Tab("cronjobs", request);
		CronJobsForm cronJobsForm;
		String forwardName = "success";

		// Get the struts form.
		cronJobsForm = (CronJobsForm) form;
		cronJobsForm.clear();
		try {
			CronJobServiceLocalInterface cronJobService;
			cronJobService = (CronJobServiceLocalInterface) LocalServices.getLocalInterfaceFor(
					"CronJobService", CronJobServiceLocalInterface.class.getName());
			List<String> cronJobNames = cronJobService.getCronJobNames();
			Collections.sort(cronJobNames);
			cronJobsForm.setCronJobsList(cronJobNames);
		}
		catch (NamingException namingException) {
			LOG.error("CronJobService not found");
			namingException.printStackTrace();
		}

		return mapping.findForward(forwardName);
	}
}












