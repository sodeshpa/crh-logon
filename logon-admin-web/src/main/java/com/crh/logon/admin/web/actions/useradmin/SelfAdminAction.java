/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.useradmin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.forms.useradmin.UserAdminForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_UserAdminForm" path="/useradmin/SelfAdmin" scope="session"
 *  validate="true" input="SelfAdminTile.def"
 *
 * @struts.action-forward name="fetchfirst" path="/useradmin/SelfAdminEntry.do"
 * @struts.action-forward name="input" path="SelfAdminTile.def"
 * @struts.action-forward name="detailSelf" path="SelfAdminDetail.def"
 */
public class SelfAdminAction extends AbstractUserAdminAction {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(SelfAdminAction.class);
    
	public ActionForward execute(
		ActionMapping mapping, ActionForm form,
		HttpServletRequest request,	HttpServletResponse response)
		throws IOException, ServletException
	{	
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
			String forwardName = null;
			UserAdminForm userAdminForm;
			// Get the struts form.
			userAdminForm = (UserAdminForm) form;
			Authorization.authorize(Authorization.PUBLIC_FUNCTION);
			UserEntity user = userAdminForm.getUserEntity();
			
			// Check if reset button clicked.
			if (isCancelled(request)) {
				// Clear all fields and discard search results.
				userAdminForm.clear();
			}
			else if (request.getParameter("cancelUserSelf") != null) {
				forwardName = "fetchfirst";
			}
			else if ("saveUserModificationSelf".equals(userAdminForm.getInputAction())) {
				if ( (user == null) || (getUser() == null) || (!user.getUserId().equals(getUser().getUserId())) ) {
						
					throw new LogonException("Self admin can only change the user logged in", Reason.RES_ERROR_SECURITY_TAMPERING);
				}

				user = modifyUser(user, null, null, userAdminForm.getSelectedTimeZoneCd(), true);
				initUser(request, userAdminForm, user.getUserId());
				handleExtLoginString(userAdminForm, request);
				MessageUtils.addActionMessage(request, "message.successUpd");
				forwardName = "detailSelf";
			}
			else {
				
				throw new LogonException("Invalid action", Reason.RES_ERROR_IMPL);
			}
			
			return result = forwardName!=null?mapping.findForward(forwardName):mapping.getInputForward();
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
