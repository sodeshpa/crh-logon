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

import com.crh.logon.admin.web.forms.useradmin.UserAdminAuthPopupForm;
import com.crh.logon.admin.web.forms.useradmin.UserAdminForm;
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
 * @struts.action name="admin_UserAdminAuthPopupForm" path="/useradmin/UserAdminAuthPopupEntry" scope="session"
 *  validate="true"
 *
 * @struts.action-forward name="success" path="UserAdminAuthPopupTile.def"
 */
public class UserAdminAuthPopupEntryAction extends UserAdminAction {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(UserAdminAuthPopupEntryAction.class);

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
			Authorization.authorize(Authorization.PUBLIC_FUNCTION);
			String forwardName = "success";
			
			UserAdminAuthPopupForm authForm = (UserAdminAuthPopupForm) form;
			authForm.clear();
			
			String restrictionType = (String) request.getParameter("restrictionType");
			int restrictionTypeInt = Integer.parseInt(restrictionType);
			
			authForm.setRestrictionType(restrictionTypeInt);
			
			UserAdminForm uaForm = (UserAdminForm) request.getSession().getAttribute("admin_UserAdminForm");
			
			switch (restrictionTypeInt) {
			case UserAdminForm.RESTR_TYPE_ROLE:
				if (uaForm.getSelectableRoleMap() == null) {
					initRoles(uaForm, false);
				}
				break;
			case UserAdminForm.RESTR_TYPE_SOLDTO:
				if (uaForm.getSelectableSoldToShipToSoldToMap() == null) {
					initCustomerShipTos(uaForm, false, false);
				}
				break;
			case UserAdminForm.RESTR_TYPE_SHIPTO:
				if (uaForm.getSelectableSoldToShipToSoldToMap() == null) {
					initCustomerShipTos(uaForm, false, false);
				}
				break;
			case UserAdminForm.RESTR_TYPE_PLANT:
				if (uaForm.getSelectablePlantShippingPointPlantMap() == null) {
					initPlantShippingPoints(uaForm, false, false);
				}
				break;
			case UserAdminForm.RESTR_TYPE_SHIPPINGPOINT:
				if (uaForm.getSelectablePlantShippingPointShippingPointMap() == null) {
					initPlantShippingPoints(uaForm, false, false);
				}
				break;
			case UserAdminForm.RESTR_TYPE_POOL:
				if (uaForm.getSelectablePoolMap() == null) {
					initPools(uaForm, false, false, request.getLocale());
				}
				break;
			case UserAdminForm.RESTR_TYPE_CARRIER:
				if (uaForm.getSelectableCarrierMap() == null) {
					initCarriers(uaForm, false, false);
				}
				break;
			case UserAdminForm.RESTR_TYPE_COMPANYCODE:
				if (uaForm.getSelectableCompCodeSareaCompCodeMap() == null) {
					initCompCodeSareas(uaForm, false, false);
				}
				break;	
			case UserAdminForm.RESTR_TYPE_SAREA:
				if (uaForm.getSelectableCompCodeSareaSareaMap() == null) {
					initCompCodeSareas(uaForm, false, false);
				}
				break;
			case UserAdminForm.RESTR_TYPE_SGROUP:
				if (uaForm.getSelectableSgroupMap() == null) {
					initSgroups(uaForm, false, false);
				}
				break;
			case UserAdminForm.RESTR_TYPE_PURCHASINGORG:
				if (uaForm.getSelectablePurchOrgMap() == null) {
					initPurchOrgs(uaForm, false, false);
				}
				break;	
			default:
				break;
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
