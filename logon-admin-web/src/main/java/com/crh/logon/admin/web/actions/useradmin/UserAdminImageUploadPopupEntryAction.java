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
import com.crh.logon.admin.web.forms.useradmin.UserAdminImageUploadPopupForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_UserAdminImageUploadForm" path="/useradmin/UserAdminImageUploadPopupEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="UserAdminImageUploadPopupTile.def"
 */
public class UserAdminImageUploadPopupEntryAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(UserAdminImageUploadPopupEntryAction.class);

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
			String forwardName = "success";
			UserAdminImageUploadPopupForm imageForm = (UserAdminImageUploadPopupForm) form;
			imageForm.clear();
			
			if(request.getParameter("type") != null) {
				String type = (String) request.getParameter("type");
				
				imageForm.setType(type);
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
