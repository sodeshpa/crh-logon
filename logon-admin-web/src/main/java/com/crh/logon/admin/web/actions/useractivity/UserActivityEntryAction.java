/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.useractivity;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.beans.UserActivityLogBean;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.useractivity.UserActivityForm;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;

/**
 * @author AdNovum Informatik AG
 * 
 * @struts.action name="admin_UserActivityForm" path="/useractivity/UserActivityEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="UserActivityTile.def"
 * @struts.action-forward name="failure" path="UserActivityTile.def"
 */
public class UserActivityEntryAction extends ActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(UserActivityEntryAction.class);
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
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
			Authorization.authorize(Authorization.PUBLIC_FUNCTION);
			setSelectedTab("admin", request);
			setSelectedSub1Tab("useractivity", request);
			
			UserActivityForm userActivityForm = (UserActivityForm) form;
			if (UserActivityLogBean.isAdminViewAllowed()) {
				userActivityForm.setAdminView(true);
				
				// set dormant user inactivity period (lastLoginBefore)
				int dormantUserInactivityPeriod = getConfigBean().getAppPolicyDormantUserInactivityPeriod();
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_MONTH, -dormantUserInactivityPeriod);
				userActivityForm.setLastLoginBefore(getUserTimezone().fmtAsDayMonthYear4(cal.getTime()));
			}
			else {
				userActivityForm.setAdminView(false);
			}
			
			String forwardName = "success";
			return result =  mapping.findForward(forwardName);
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
