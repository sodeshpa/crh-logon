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
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.crh.logon.admin.web.forms.useradmin.UserAdminExtLoginPwdPopupForm;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_UserAdminExtLoginPwdPopupForm" path="/useradmin/UserAdminExtLoginPwdPopupEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="UserAdminExtLoginPwdPopupTile.def"
 */
public class UserAdminExtLoginPwdPopupEntryAction extends AdminActionSupport {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {

		String forwardName = "success";
		UserAdminExtLoginPwdPopupForm pwdForm = (UserAdminExtLoginPwdPopupForm) form;
		pwdForm.clear();

		if (request.getParameter("self") != null) {
			pwdForm.setSelf(true);
		}
		else {
			pwdForm.setSelf(false);
		}
		if (request.getParameter("accesschange") != null) {
			pwdForm.setAccessChange(true);
		}
		else {
			pwdForm.setAccessChange(false);
		}
		if (request.getParameter("pwdformtype") != null) {
			pwdForm.setFormType(Integer.parseInt(request.getParameter("pwdformtype")));
		}
		else {
			// switch to default: external access pwd
			pwdForm.setFormType(UserAdminExtLoginPwdPopupForm.FORMTYPE_EXTPWD);
		}

		pwdForm.setSpecialSetOfChars(getConfigBean().getAppPolicyPasswordSpecialSetOfChars());

		return mapping.findForward(forwardName);

	}
	
}
