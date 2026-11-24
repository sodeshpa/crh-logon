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

import com.crh.logon.constants.Constant;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="cust_PasswordChangeForm" path="/passwordchange/PasswordChangeEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="pwdChange" path="PasswordChangeTile.def"
 */
public class PasswordChangeEntryAction extends ActionSupport {


    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(PasswordChangeEntryAction.class);

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
			
			String forwardName = "pwdChange";
		    
			PasswordChangeForm helpForm = (PasswordChangeForm) form;
			helpForm.clear();
			if (request.getParameter(Constant.KEY_PASSWORD_CHANGE_ORIGINAL_URI) != null) {
				helpForm.setOriginalUrl(request.getParameter(Constant.KEY_PASSWORD_CHANGE_ORIGINAL_URI));
			}
			
			helpForm.setSpecialSetOfChars(getConfigBean().getAppPolicyPasswordSpecialSetOfChars());
			
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
