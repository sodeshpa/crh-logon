/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.cust.web.actions.passwordchange;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.cust.web.forms.passwordchange.PasswordChangeForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.filters.UserFilter;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="cust_PasswordChangeForm" path="/passwordchange/PasswordChange" scope="session"
 *  validate="true" input="PasswordChangeTile.def"
 *
 * @struts.action-forward name="pwd"  path="PasswordChangeTile.def"
 * @struts.action-forward name="save" path="/webbestellung/WebBestellungEntry.do"
 */
public class PasswordChangeAction extends ActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PasswordChangeAction.class);

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
			String forwardName = "pwd";
			
			// Get the struts form(s).
			PasswordChangeForm helpForm = (PasswordChangeForm) form;
			
			if (request.getParameter("save") != null) {
				if (logDebugEnabled) {
					LOG.debug("Action: save button is pressed");
				}
				
				Authorization.authorize(Authorization.PUBLIC_FUNCTION);
				getUserAdminService().externalPasswordChange(getUser().getExtLogin(), helpForm.getHashedExtLoginPwd(), false);
				
				UserFilter.resetUser(request);
				
				if (helpForm.getOriginalUrl() != null) {
					response.sendRedirect(helpForm.getOriginalUrl());
					forwardName = null;
				}
				else {
					//to make sure that we reload user from db
					forwardName = "save";
				}
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
