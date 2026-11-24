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
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_UserAdminForm" path="/useradmin/SelfAdminEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="SelfAdminTile.def"
 * @struts.action-forward name="failure" path="SelfAdminTile.def"
 */
public class SelfAdminEntryAction extends AbstractUserAdminAction {


    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(SelfAdminEntryAction.class);

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
			Authorization.authorize(Authorization.PUBLIC_FUNCTION);
			setSelectedTab("admin", request);
			setSelectedSub1Tab("selfadmin", request);
			UserAdminForm helpForm;
			String forwardName = "success";
		    helpForm = (UserAdminForm) form;
		    
		    UserEntity userLoggedIn = getUser();
		    helpForm.setUserLoggedIn(userLoggedIn);
		    helpForm.setUserId(userLoggedIn.getUserId().toString());
		    
		    
		    new UserAdminHelper().initEntryAction(request, helpForm, "self");
		    new UserAdminAction().handleModifyUser(request, helpForm);

		    initRestrictionMaps(helpForm, true, true, request.getLocale());
		    
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
