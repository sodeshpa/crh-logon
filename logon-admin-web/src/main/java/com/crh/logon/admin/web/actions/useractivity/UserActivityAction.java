/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.useractivity;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.crh.logon.web.actions.ActionSupport;


/**
 * @author AdNovum Informatik AG
 * 
 *  @struts.action name="admin_UserActivityForm" path="/useractivity/UserActivity" scope="session"
 *  validate="true"
 *  
 * @struts.action-forward name="success" path="UserActivityTile.def"
 * @struts.action-forward name="failure" path="UserActivityTile.def"
 */
public class UserActivityAction extends ActionSupport {

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		
		String forwardName = "success";
		return  mapping.findForward(forwardName);
	}
}
