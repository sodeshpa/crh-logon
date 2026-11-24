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
import com.crh.logon.admin.web.forms.useradmin.UserAdminForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapVendorEntity;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_UserAdminForm" path="/useradmin/UserAdminExternalEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="UserAdminTile.def"
 * @struts.action-forward name="error" path="UserAdminTile.def"
 */
public class UserAdminExternalEntryAction extends AdminActionSupport {


    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(UserAdminExternalEntryAction.class);

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
			String forwardName = "success";
			UserEntity userLoggedIn = getUser();
			boolean isUserExtern = false;		    
			
			SapBpEntity sapBp = userLoggedIn.getSapBp();
			if (sapBp != null) {
				Long sapBpId = sapBp.getSapBpId();
				if (sapBpId != null) {
					isUserExtern = true;
				}
			}			
			SapVendorEntity sapVendor = userLoggedIn.getSapVendor();			
			if (sapVendor != null) {
				Long sapVendorId = sapVendor.getSapVendorId();
				if (sapVendorId != null) {
					isUserExtern = true;
				}				
			}
			if (isUserExtern == false) {
				MessageUtils.addActionError(request, "useradmin.errors.noExternal");
			
				setSelectedTab("admin", request);
				setSelectedSub1Tab("useradmin", request);
				setSelectedSub2Tab("useradminlogon", request);				
				
				forwardName = "error";
				return result = mapping.findForward(forwardName);
			}		
			
			setSelectedTab("admin", request);
			setSelectedSub1Tab("useradmin", request);
			setSelectedSub2Tab("useradminexternal", request);
			UserAdminForm helpForm;
			
		    helpForm = (UserAdminForm) form;		    
		    helpForm.setUserLoggedIn(userLoggedIn);		
		    
		    Authorization.authorize(Authorization.USER_ADMIN_LOGON_EXTERN);
	    	new UserAdminHelper().initEntryAction(request, helpForm, "admin");
	    	helpForm.setUserExternal(true);
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
