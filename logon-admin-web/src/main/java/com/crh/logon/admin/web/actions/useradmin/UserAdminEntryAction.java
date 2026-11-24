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
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_UserAdminForm" path="/useradmin/UserAdminEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="UserAdminTile.def"
 * @struts.action-forward name="failure" path="UserAdminTile.def"
 * @struts.action-forward name="detail" path="UserAdminDetailTile.def"
 */
public class UserAdminEntryAction extends AdminActionSupport {


    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(UserAdminEntryAction.class);

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
			Authorization.authorize(Authorization.USER_ADMIN_LOGON);
			setSelectedTab("admin", request);
			setSelectedSub1Tab("useradmin", request);
			setSelectedSub2Tab("useradminlogon", request);
			
			UserAdminForm helpForm;
			String forwardName = "success";
			//			
		    helpForm = (UserAdminForm) form;
		    helpForm.clear();
			//			
			//			Locale locale = getLocale(request);
			//			// FIXME remove this later
			//			if (!locale.equals(Locale.ENGLISH) && !locale.equals(Locale.GERMAN) && !locale.equals(Locale.FRENCH) && !locale.equals(Locale.ITALIAN)) {
			//				locale = Locale.ENGLISH;
			//			}
			//			
			//			
		    UserEntity userLoggedIn = getUser();
		    helpForm.setUserLoggedIn(userLoggedIn);
		    
			//			Long functionToAuthorize = Authorization.USER_ADMIN_LOGON;
			////			helpForm.setFunctionToCheck(Authorization.USER_ADMIN_LOGON);
			//			
			//			if(userLoggedIn.getSapBp() != null && userLoggedIn.getSapBp().getSapBpId() != null) {
			//				functionToAuthorize = Authorization.USER_ADMIN_LOGON_EXTERN;
			//
			//				helpForm.setUserExternal(true);
			////				helpForm.setFunctionToCheck(Authorization.USER_ADMIN_LOGON_EXTERN);
			//			}
			//			else if(userLoggedIn.getSapVendor() != null && userLoggedIn.getSapVendor().getSapVendorId() != null) {
			//				functionToAuthorize = Authorization.USER_ADMIN_LOGON_EXTERN;
			//				helpForm.setUserExternal(true);
			////				helpForm.setFunctionToCheck(Authorization.USER_ADMIN_LOGON_EXTERN);
			//			}
		    
			
		    new UserAdminHelper().initEntryAction(request, helpForm, "admin");
		    
			//			helpForm.setLocale(locale);
			//			
			//			List userLanguageList = getRefDataGenericsService().findLangTxEntityList(locale);
			//			helpForm.setUserLanguageList(userLanguageList);
		    
			//			//loading roles
			//			UserAdminService userAdminService = getServiceLocator().getUserAdminService();
			//			List rolesToDisplay = new ArrayList();
			//			//setting an additional role for indicating the empty role
			//			RoleEntity emptyRole = new RoleEntity();
			//			emptyRole.setRoleName("");
			//			rolesToDisplay.add(emptyRole);
			//			rolesToDisplay.addAll(userAdminService.fetchRoleList(RoleEntity.ATTR_ROLE_ID, true));
			//			helpForm.setRoleList(rolesToDisplay);
			//			
			//			helpForm.setSearchState(UserAdminForm.SEARCH_STATE_WITHOUT_FILTER);
			//			helpForm.setFunctionToAuthorize(functionToAuthorize);
			//			//loading user types
			//			List userTypes = getRefDataGenericsService().findTxEntityList(UserTypeTxEntity.class, request.getLocale());
			//			helpForm.setUserTypeList(userTypes);
			//			if(Authorization.isAllowed(functionToCheckIfAllowed)) {
			//				helpForm.setAllowedToAdministrate(true);
			//			helpForm.setSearchState(UserAdminForm.SEARCH_STATE_WITHOUT_FILTER);
			//			}
			//			else {
			//				//user is allowed just to manage his/her data
			//				helpForm.setAllowedToAdministrate(false);
			//				helpForm.setUserId(userLoggedIn.getUserId().toString());
			//				forwardName = new UserAdminAction().handleModifyUser(request, helpForm);
			//			}
		    
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
