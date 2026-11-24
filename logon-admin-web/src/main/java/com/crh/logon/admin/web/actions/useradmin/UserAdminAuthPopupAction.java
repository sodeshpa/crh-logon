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

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.forms.useradmin.UserAdminAuthPopupForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_UserAdminAuthPopupForm" path="/useradmin/UserAdminAuthPopup" scope="session"
 *  validate="true" input="UserAdminAuthPopupTile.def"
 *
 * @struts.action-forward name="reload" path="/useradmin/UserAdminAuthPopupEntry.do"
 * @struts.action-forward name="save" path="UserAdminAuthPopupClose.def"
 */
public class UserAdminAuthPopupAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(UserAdminAuthPopupAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
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
			// Get the struts form(s).
			Authorization.authorize(Authorization.PUBLIC_FUNCTION);
			String forwardName = "pwd";
			
			if (request.getParameter("save") != null) {
				if (logDebugEnabled) {
					LOG.debug("Action: save button is pressed");
				}
				UserAdminAuthPopupForm authForm = (UserAdminAuthPopupForm) form;
				
				forwardName = "save";
			}
			
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
