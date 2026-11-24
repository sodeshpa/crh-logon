/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.test;

import java.io.IOException;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.forms.test.CronJobsForm;
import com.crh.logon.business.services.localinterfaces.CronJobServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LocalServices;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_CronJobsForm" path="/test/CronJobs" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="/test/CronJobsTile.def"
 */
public class CronJobsAction extends AdminActionSupport {

	private static final String UNSELECTED_ITEM = "-1";	
	public static final JcanLogger LOG = JcanLoggerFactory.getLogger(CronJobsAction.class);

	public ActionForward execute(
		ActionMapping mapping, ActionForm form,
		HttpServletRequest request,	HttpServletResponse response)
		throws IOException, ServletException {
		
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
			Authorization.authorize(Authorization.TEST_CRONJOBS);
			setSelectedTab("test", request);
			setSelectedSub1Tab("cronjobs", request);
			
			String forwardName = "success";
			
			CronJobServiceLocalInterface cronJobService = null;
			try {
				cronJobService = (CronJobServiceLocalInterface) LocalServices.getLocalInterfaceFor("CronJobService", CronJobServiceLocalInterface.class.getName());
			} catch (NamingException e) {
				e.printStackTrace();
			}
			CronJobsForm cronJobsForm = (CronJobsForm) form;
						
			if (cronJobsForm.getSelectedJob() != null && !cronJobsForm.getSelectedJob().isEmpty() ){
				String cronJobName = cronJobsForm.getSelectedJob();
				if (isCronJobSelected(cronJobName)) {
					cronJobService.callCronJob(cronJobName);
					MessageUtils.addActionMessage(request, "errors.detail", "Cron job started");
				}
			}
			
			cronJobsForm.clear();
			cronJobsForm.setCronJobsList(cronJobService.getCronJobNames());
			
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
	
	private boolean isCronJobSelected(String selectedCronJob) {
		return !UNSELECTED_ITEM.equals(selectedCronJob);
	}

}
