/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.useradmin;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.useradmin.UserAdminAuthPopupForm;
import com.crh.logon.admin.web.forms.useradmin.UserAdminExtLoginPwdPopupForm;
import com.crh.logon.admin.web.forms.useradmin.UserAdminForm;
import com.crh.logon.business.services.interfaces.UserAdminService;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.persistence.entities.admin.RoleEntity;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.admin.UserRoleEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.ImageEntity;
import com.crh.logon.persistence.entities.common.SignatureEntity;
import com.crh.logon.persistence.entities.refdata.MimeTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.UserTypeBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapVendorEntity;
import com.crh.logon.utils.AuthUtils;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.PasswordUtil;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.BoxListEntity;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.UserProvider;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_UserAdminForm" path="/useradmin/UserAdmin" scope="session"
 *  validate="true" input="UserAdminDetail.def"
 *
 * @struts.action-forward name="fetchfirst" path="/useradmin/UserAdminEntry.do"
 * @struts.action-forward name="input" path="UserAdminTile.def"
 * @struts.action-forward name="detail" path="UserAdminDetail.def"
 * @struts.action-forward name="detailSelf" path="SelfAdminDetail.def"
 * @struts.action-forward name="detailReadonly" path="UserAdminDetailReadonly.def"
 * @struts.action-forward name="search" path="UserAdminSearch.def"
 * @struts.action-forward name="newUser" path="UserAdminType.def"
 * @struts.action-forward name="searchWithFilter" path="UserAdminSearch.def"  
 * @struts.action-forward name="searchWithoutFilter" path="UserAdminSearch.def"  
 * @struts.action-forward name="poolCarrier" path="UserAdminPoolCarrierSelection.def"  
 * @struts.action-forward name="shipTo" path="UserAdminShipToSelection.def"  
 * @struts.action-forward name="customerShipTo" path="UserAdminCustomerShipToSelection.def"  
 * @struts.action-forward name="plantShippingPoint" path="UserAdminPlantShippingPointSelection.def"  
 * @struts.action-forward name="sorgSgroup" path="UserAdminSorgSgroupSelection.def"
 * @struts.action-forward name="authpopup" path="UserAdminAuthRestrictionPopupTile.def"
 */
@AdnTrace
public class UserAdminAction extends AbstractUserAdminAction {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(UserAdminAction.class);

	private static final int SELECTION_OPERATION_ADD_CUSTOMERS = 4;

	private static final int SELECTION_OPERATION_ADD_SHIPTOS = 5;

	private static final int SELECTION_OPERATION_REMOVE_CUSTOMER = 6;

	private static final int SELECTION_OPERATION_REMOVE_SHIPTO = 7;

	private static final int SELECTION_OPERATION_ADD_PLANTS = 8;

	private static final int SELECTION_OPERATION_ADD_SHIPPINGPOINTS = 9;

	private static final int SELECTION_OPERATION_REMOVE_PLANT = 10;

	private static final int SELECTION_OPERATION_REMOVE_SHIPPINGPOINT = 11;

	private static final int SELECTION_OPERATION_REMOVE__ALL_SHIPTOS = 12;

	private static final int SELECTION_OPERATION_REMOVE__ALL_SHIPPINGPOINTS = 12;

	private static final int SELECTION_OPERATION_ADD_COMPANYCODES = 20;

	private static final int SELECTION_OPERATION_ADD_SAREAS = 21;

	private static final int SELECTION_OPERATION_REMOVE_COMPANYCODE = 22;

	private static final int SELECTION_OPERATION_REMOVE_SAREA = 23;

	private static final int SELECTION_OPERATION_REMOVE__ALL_COMPANYCODES = 24;

	private static final int SELECTION_OPERATION_REMOVE__ALL_SAREAS = 25;

	private static final int SELECTION_OPERATION_REMOVE__ALL_CUSTOMER = 26;

	private static final String FORWARD_DETAIL = "detail";

	private static final String FORWARD_DETAIL_SELF = "detailSelf";

	private static final String FORWARD_DETAIL_READONLY = "detailReadonly";

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {

		String forwardName = "input";
		UserAdminForm userAdminForm;
		// Get the struts form.
		userAdminForm = (UserAdminForm) form;
		if (("storeExternalAccess".equals(request.getParameter("formAction")))
				|| ("cancelExternalAccess".equals(request.getParameter("formAction")))) {
			Authorization.authorize(Authorization.PUBLIC_FUNCTION);
		}
		else {
			Authorization.authorize(Authorization.USER_ADMIN_LOGON);
		}
		UserEntity user = userAdminForm.getUserEntity();

		// Check if reset button clicked.
		if (isCancelled(request)) {
			// Clear all fields and discard search results.
			userAdminForm.clear();
		}
		else if (request.getParameter("searchUser") != null) {
			userAdminForm.setSearchState(UserAdminForm.SEARCH_STATE_WITH_FILTER);
			forwardName = "input";
		}
		else if (request.getParameter("resetSearchUser") != null) {
			forwardName = "fetchfirst";
		}
		else if ("storeExternalAccess".equals(request.getParameter("formAction"))) {

			//set back the active flag, because it is deselectig by reset, is there an other way for this ?
			UserAdminExtLoginPwdPopupForm extAccForm = (UserAdminExtLoginPwdPopupForm) request.getSession().getAttribute(
					"admin_UserAdminExtLoginPwdPopupForm");

			switch (extAccForm.getFormType()) {
			case UserAdminExtLoginPwdPopupForm.FORMTYPE_EXTPWD:
				boolean validationPassed = handleExtLoginString(userAdminForm, request);

				if (validationPassed) {
					// hash password
					String hashPwd = PasswordUtil.createHash(extAccForm.getExtLoginPwdNew());
					user.setExtLoginPwd(hashPwd);
					//setting new expiration date
					int passwordLifeTime = getConfigBean().getAppPolicyPasswordLifeTimeD();
					if (passwordLifeTime > 0) {
						Calendar cal = Calendar.getInstance();
						cal.add(Calendar.DATE, passwordLifeTime);
						user.setExtLoginPwdValidToDat(cal.getTime());
						// if the user is a customer/vendor user then the password never expires
						user = LogonUtil.setExtLoginPxdValidForCustomerVendor(user);
					}
					else {
						// valid forever
						user.setExtLoginPwdValidToDat(null);
					}
				}
				else {
					userAdminForm.getUserEntity().setExtLoginActiveFlag(Boolean.FALSE);
				}
				break;
			case UserAdminExtLoginPwdPopupForm.FORMTYPE_PDAPIN:
				String hashPin = PasswordUtil.createHash(extAccForm.getExtLoginPwdNew());
				user.setPdaPin(hashPin);
				Date pdaPinValidToDat = getPdaPinValidToDate();
				user.setPdaPinValidToDat(pdaPinValidToDat);
				userAdminForm.setPdaPinValidTo(pdaPinValidToDat);
				break;
			default:
				throw new LogonException("Unknown formtype " + extAccForm.getFormType(),
						Reason.RES_ERROR_IMPL);
			}

			if (extAccForm.isSelf()) {
				forwardName = FORWARD_DETAIL_SELF;
			}
			else {
				UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
				forwardName = resolveUserEditRedirect(userType);
			}
		}
		else if ("cancelExternalAccess".equals(request.getParameter("formAction"))) {
			UserAdminExtLoginPwdPopupForm extAccForm = (UserAdminExtLoginPwdPopupForm) request.getSession().getAttribute(
					"admin_UserAdminExtLoginPwdPopupForm");

			if (extAccForm.getFormType() == UserAdminExtLoginPwdPopupForm.FORMTYPE_EXTPWD) {
				if (extAccForm.isAccessChange()) {
					// if password is not set, we need to disable the flag
					if (StringUtils.empty(userAdminForm.getUserEntity().getExtLoginPwd())) {
						userAdminForm.getUserEntity().setExtLoginActiveFlag(Boolean.FALSE);
					}
				}
			}

			if (extAccForm.isSelf()) {
				forwardName = FORWARD_DETAIL_SELF;
			}
			else {
				UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
				forwardName = resolveUserEditRedirect(userType);
			}
		}
		else if ("addRestriction".equals(request.getParameter("formAction"))) {
			UserAdminAuthPopupForm authForm = (UserAdminAuthPopupForm) request.getSession().getAttribute(
					"admin_UserAdminAuthPopupForm");

			String restrictionType = (String) request.getParameter("restrictionType");

			if (restrictionType == null) {
				MessageUtils.addActionError(request, "useradmin.invalid.restrictiontype");
				UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
				return mapping.findForward(resolveUserEditRedirect(userType));
			}

			int restrictionTypeInt = Integer.parseInt(restrictionType);

			switch (restrictionTypeInt) {
			case UserAdminForm.RESTR_TYPE_ROLE:
				Long[] selectedRoleIds = authForm.getSelectedRoleIds();
				userAdminForm.setSelectedRoleIds(selectedRoleIds);
				handleSelections(userAdminForm.getSelectableRoleMap(),
						userAdminForm.getSelectedRoleMap(), selectedRoleIds, null);
				break;
			case UserAdminForm.RESTR_TYPE_SOLDTO:
				String[] selectedSoldToIds = authForm.getSelectedSoldToIds();
				userAdminForm.setSelectedSoldToIds(selectedSoldToIds);
				handleCustomerShipToSelections(userAdminForm, request,
						SELECTION_OPERATION_ADD_CUSTOMERS);
				break;
			case UserAdminForm.RESTR_TYPE_SHIPTO:
				String[] selectedShipTos = authForm.getSelectedShipToIds();
				userAdminForm.setSelectedShipToIds(selectedShipTos);
				handleCustomerShipToSelections(userAdminForm, request,
						SELECTION_OPERATION_ADD_SHIPTOS);
				break;
			case UserAdminForm.RESTR_TYPE_PLANT:
				String[] selectedPlantIds = authForm.getSelectedPlantIds();
				userAdminForm.setSelectedPlantIds(selectedPlantIds);
				handlePlantShippingPointSelections(userAdminForm, request,
						SELECTION_OPERATION_ADD_PLANTS);
				break;
			case UserAdminForm.RESTR_TYPE_SHIPPINGPOINT:
				String[] selectedShippingPoints = authForm.getSelectedShippingPointIds();
				userAdminForm.setSelectedShippingPointIds(selectedShippingPoints);
				handlePlantShippingPointSelections(userAdminForm, request,
						SELECTION_OPERATION_ADD_SHIPPINGPOINTS);
				break;
			case UserAdminForm.RESTR_TYPE_POOL:
				Long[] selectedPoolIds = authForm.getSelectedPoolIds();
				userAdminForm.setSelectedPoolIds(selectedPoolIds);
				handleSelections(userAdminForm.getSelectablePoolMap(),
						userAdminForm.getSelectedPoolMap(), selectedPoolIds, null);
				break;
			case UserAdminForm.RESTR_TYPE_CARRIER:
				Long[] selectedCarrierIds = authForm.getSelectedCarrierIds();
				userAdminForm.setSelectedCarrierIds(selectedCarrierIds);
				handleSelections(userAdminForm.getSelectableCarrierMap(),
						userAdminForm.getSelectedCarrierMap(), selectedCarrierIds, null);
				break;
			case UserAdminForm.RESTR_TYPE_COMPANYCODE:
				String[] selectedCompCodeIds = authForm.getSelectedCompCodeIds();
				userAdminForm.setSelectedCompCodeIds(selectedCompCodeIds);
				handleCompCodeSareaSelections(userAdminForm, request,
						SELECTION_OPERATION_ADD_COMPANYCODES);
				break;
			case UserAdminForm.RESTR_TYPE_SAREA:
				String[] selectedSareaIds = authForm.getSelectedSareaIds();
				userAdminForm.setSelectedSareaIds(selectedSareaIds);
				handleCompCodeSareaSelections(userAdminForm, request,
						SELECTION_OPERATION_ADD_SAREAS);
				break;
			case UserAdminForm.RESTR_TYPE_SGROUP:
				String[] selectedSgroupIds = authForm.getSelectedSgroupIds();
				userAdminForm.setSelectedSgroupIds(selectedSgroupIds);
				handleSelections(userAdminForm.getSelectableSgroupMap(),
						userAdminForm.getSelectedSgroupMap(), selectedSgroupIds, null);
				break;
			case UserAdminForm.RESTR_TYPE_PURCHASINGORG:
				String[] selectedPurchOrgIds = authForm.getSelectedPurchOrgIds();
				userAdminForm.setSelectedPurchOrgIds(selectedPurchOrgIds);
				handleSelections(userAdminForm.getSelectablePurchOrgMap(),
						userAdminForm.getSelectedPurchOrgMap(), selectedPurchOrgIds, null);
				break;
			default:
				break;
			}
			UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
			forwardName = resolveUserEditRedirect(userType);
		}
		else if ("removeShipTo".equals(request.getParameter("formAction"))) {
			if (request.getParameter("shipToId") != null) {
				String shipToIdIdToRemove = (String) request.getParameter("shipToId");
				handleSelections(userAdminForm.getSelectableShipToMap(),
						userAdminForm.getSelectedShipToMap(), null, shipToIdIdToRemove);
			}
			UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
			forwardName = resolveUserEditRedirect(userType);
		}
		else if ("removeShipToCS".equals(request.getParameter("formAction"))) {
			handleCustomerShipToSelections(userAdminForm, request,
					SELECTION_OPERATION_REMOVE_SHIPTO);
			UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
			forwardName = resolveUserEditRedirect(userType);
		}
		else if ("removeCustomer".equals(request.getParameter("formAction"))) {
			handleCustomerShipToSelections(userAdminForm, request,
					SELECTION_OPERATION_REMOVE_CUSTOMER);
			UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
			forwardName = resolveUserEditRedirect(userType);
		}
		else if ("removePool".equals(request.getParameter("formAction"))) {

			if (request.getParameter("poolId") != null) {
				String poolIdToRemove = (String) request.getParameter("poolId");
				handleSelections(userAdminForm.getSelectablePoolMap(),
						userAdminForm.getSelectedPoolMap(), null, Long.parseLong(poolIdToRemove));
			}

			UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
			forwardName = resolveUserEditRedirect(userType);
		}
		else if ("removeCarrier".equals(request.getParameter("formAction"))) {
			if (request.getParameter("carrierId") != null) {
				String carrierIdToRemove = (String) request.getParameter("carrierId");
				handleSelections(userAdminForm.getSelectableCarrierMap(),
						userAdminForm.getSelectedCarrierMap(), null,
						Long.parseLong(carrierIdToRemove));
			}
			UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
			forwardName = resolveUserEditRedirect(userType);
		}
		else if ("removePlant".equals(request.getParameter("formAction"))) {
			handlePlantShippingPointSelections(userAdminForm, request,
					SELECTION_OPERATION_REMOVE_PLANT);
			UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
			forwardName = resolveUserEditRedirect(userType);
		}
		else if ("removeShippingPoint".equals(request.getParameter("formAction"))) {
			handlePlantShippingPointSelections(userAdminForm, request,
					SELECTION_OPERATION_REMOVE_SHIPPINGPOINT);
			UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
			forwardName = resolveUserEditRedirect(userType);
		}
		else if ("removeCompCode".equals(request.getParameter("formAction"))) {
			handleCompCodeSareaSelections(userAdminForm, request,
					SELECTION_OPERATION_REMOVE_COMPANYCODE);
			UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
			forwardName = resolveUserEditRedirect(userType);
		}
		else if ("removeSarea".equals(request.getParameter("formAction"))) {
			handleCompCodeSareaSelections(userAdminForm, request, SELECTION_OPERATION_REMOVE_SAREA);
			UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
			forwardName = resolveUserEditRedirect(userType);
		}
		else if ("removeSgroup".equals(request.getParameter("formAction"))) {
			if (request.getParameter("sgroupId") != null) {
				String sorgIdToRemove = (String) request.getParameter("sgroupId");
				handleSelections(userAdminForm.getSelectableSgroupMap(),
						userAdminForm.getSelectedSgroupMap(), null, sorgIdToRemove);
			}
			UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
			forwardName = resolveUserEditRedirect(userType);
		}
		else if ("removePurchOrg".equals(request.getParameter("formAction"))) {
			if (request.getParameter("purchOrgId") != null) {
				String purchOrgIdToRemove = (String) request.getParameter("purchOrgId");
				handleSelections(userAdminForm.getSelectablePurchOrgMap(),
						userAdminForm.getSelectedPurchOrgMap(), null, purchOrgIdToRemove);
			}
			UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
			forwardName = resolveUserEditRedirect(userType);
		}
		else if ("removeRole".equals(request.getParameter("formAction"))) {
			if (request.getParameter("roleId") != null) {
				String roleIdToRemove = (String) request.getParameter("roleId");
				handleSelections(userAdminForm.getSelectableRoleMap(),
						userAdminForm.getSelectedRoleMap(), null, Long.parseLong(roleIdToRemove));
			}
			UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
			forwardName = resolveUserEditRedirect(userType);
		}
		else if ("accessChange".equals(userAdminForm.getInputAction())) {
			UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
			forwardName = resolveUserEditRedirect(userType);
		}
		else if ("disableExternalAccess".equals(userAdminForm.getInputAction())
				|| "disableExternalAccess".equals(request.getParameter("formAction"))) {
			userAdminForm.getUserEntity().setExtLoginActiveFlag(Boolean.FALSE);
			UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
			forwardName = resolveUserEditRedirect(userType);
		}
		else if ("selectUserType".equals(userAdminForm.getInputAction())) {

			if (user.getUserType() == null || user.getUserType().getUserTypeCd() == null) {
				MessageUtils.addActionError(request, "useradmin.errors.noType");
				return mapping.findForward(FORWARD_DETAIL);
			}

			Long userType = userAdminForm.getUserEntity().getUserType().getUserTypeCd();
			UserTypeBsEntity userTypeBsEntity = getRefDataGenericsService().findBsEntity(
					UserTypeBsEntity.class, userType);
			user.setUserType(userTypeBsEntity);
			initSelectedRoleMap(userAdminForm);
			// if the user is a customer/vendor user then the password never expires
			user = LogonUtil.setExtLoginPxdValidForCustomerVendor(user);

			forwardName = resolveUserEditRedirect(userAdminForm.getUserEntity().getUserType());
		}
		else if ("imageUpload".equals(userAdminForm.getInputAction())) {
			if (LOG.isDebugEnabled()) {
				if (userAdminForm.getSignature() != null) {
					LOG.debug("Signature is set " + userAdminForm.getSignatureFilename() + " "
							+ userAdminForm.getSignatureContentType());
				}
				if (userAdminForm.getPhotoImage() != null) {
					LOG.debug("Photo is set " + userAdminForm.getPhotoImageFilename() + " "
							+ userAdminForm.getPhotoImageContentType());
				}
			}

			UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
			forwardName = resolveUserEditRedirect(userType);
		}
		else if ("newUser".equals(userAdminForm.getInputAction())) {

			userAdminForm.initMaps();
			userAdminForm.clearDetailFields();

			initUser(request, userAdminForm, null);

			initRestrictionMaps(userAdminForm, true, true, request.getLocale());

			UserEntity userLoggedIn = userAdminForm.getUserLoggedIn();
			Long sapBpId = userLoggedIn.getSapBp() == null ? null : userLoggedIn.getSapBp().getSapBpId();
			String sapId = userLoggedIn.getSapVendor() == null ? null : userLoggedIn.getSapVendor().getSapId();
			if (sapBpId != null || sapId != null) {

				forwardName = FORWARD_DETAIL;
			}
			else {
				forwardName = "newUser";
			}

		}
		else if ("modifyUser".equals(userAdminForm.getInputAction())) {

			Long userId = Long.valueOf(userAdminForm.getUserId());
			if (userId.equals(getUser().getUserId())) {
				throw new LogonException(" Logon user can not modify itself ",
						Reason.RES_ERROR_SECURITY_TAMPERING);
			}

			forwardName = handleModifyUser(request, userAdminForm);
			initRestrictionMaps(userAdminForm, true, false, request.getLocale());

		}
		else if ("driverFlagChange".equals(userAdminForm.getInputAction())) {
			if (userAdminForm.isDriverFlag()) {
				if (userAdminForm.getDriverEntity() == null) {
					userAdminForm.setDriverEntity(new DriverEntity());
				}
			}
			else {
				if (userAdminForm.getDriverEntity().getDriverId() == null) {
					userAdminForm.setDriverEntity(null);
				}
			}
			UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
			forwardName = resolveUserEditRedirect(userType);
		}
		else if ("selectSoldTo".equals(userAdminForm.getInputAction())) {

			String soldToSapId = userAdminForm.getUserEntity().getSapBp().getSapId();

			if (StringUtils.empty(soldToSapId)) {
				MessageUtils.addActionError(request, "useradmin.errors.noCustomer");
				return mapping.findForward(FORWARD_DETAIL);
			}

			SapBpEntity sapBpEntity = getServiceLocator().getDispokarteService().fetchSoldtoSapBpBySapId(
					soldToSapId);
			userAdminForm.getUserEntity().setSapBp(sapBpEntity);

			addSoldtoRestriction(userAdminForm, sapBpEntity);

			UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
			forwardName = resolveUserEditRedirect(userType);
		}
		else if ("selectShipTo".equals(userAdminForm.getInputAction())) {
			String[] selectedShipTos = userAdminForm.getSelectedShipToIds();
			handleSelections(userAdminForm.getSelectableShipToMap(),
					userAdminForm.getSelectedShipToMap(), selectedShipTos, null);
			forwardName = "shipTo";
		}
		else if ("selectVendor".equals(userAdminForm.getInputAction())) {
			String vendorSapId = userAdminForm.getUserEntity().getSapVendor().getSapId();

			if (vendorSapId == null) {
				MessageUtils.addActionError(request, "useradmin.errors.noVendor");
				UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
				return mapping.findForward(resolveUserEditRedirect(userType));
			}
			SapVendorEntity sapVendorEntity = getUserAdminService().fetchSapVendorBySapId(
					vendorSapId);
			userAdminForm.getUserEntity().setSapVendor(sapVendorEntity);

			addVendorRestriction(userAdminForm, sapVendorEntity);
			UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
			forwardName = resolveUserEditRedirect(userType);
		}

		else if ("removeAllCustomers".equals(userAdminForm.getInputAction())) {
			handleCustomerShipToSelections(userAdminForm, request,
					SELECTION_OPERATION_REMOVE__ALL_CUSTOMER);
			forwardName = FORWARD_DETAIL;
		}
		else if ("removeAllShipTo".equals(userAdminForm.getInputAction())) {
			handleCustomerShipToSelections(userAdminForm, request,
					SELECTION_OPERATION_REMOVE__ALL_SHIPTOS);
			forwardName = FORWARD_DETAIL;
		}
		else if ("removeAllPlants".equals(userAdminForm.getInputAction())) {
			deletePlants(userAdminForm);
			forwardName = FORWARD_DETAIL;
		}
		else if ("removeAllShippingPoints".equals(userAdminForm.getInputAction())) {
			deleteShippingPoints(userAdminForm);
			forwardName = FORWARD_DETAIL;
		}
		else if ("removeAllCompCodes".equals(userAdminForm.getInputAction())) {
			handleCompCodeSareaSelections(userAdminForm, request,
					SELECTION_OPERATION_REMOVE__ALL_COMPANYCODES);
			forwardName = FORWARD_DETAIL;
		}
		else if ("removeAllSareas".equals(userAdminForm.getInputAction())) {
			handleCompCodeSareaSelections(userAdminForm, request,
					SELECTION_OPERATION_REMOVE__ALL_SAREAS);
			forwardName = FORWARD_DETAIL;
		}
		else if ("removeAllRoles".equals(userAdminForm.getInputAction())) {
			initRoles(userAdminForm, false);
			forwardName = FORWARD_DETAIL;
		}
		else if ("deleteAllPools".equals(userAdminForm.getInputAction())) {
			deletePolls(userAdminForm);
			forwardName = FORWARD_DETAIL;
		}
		else if ("deleteAllCarriers".equals(userAdminForm.getInputAction())) {
			deleteAllCarriers(userAdminForm);
			forwardName = FORWARD_DETAIL;
		}
		else if ("deleteAllSgroups".equals(userAdminForm.getInputAction())) {
			deleteAllSalesGroups(userAdminForm);
			forwardName = FORWARD_DETAIL;
		}
		else if ("deleteAllPurchOrgs".equals(userAdminForm.getInputAction())) {
			deleteAllPurchOrg(userAdminForm);
			forwardName = FORWARD_DETAIL;
		}
		// Check if submit save user adding button clicked.
		else if ("saveUserModification".equals(userAdminForm.getInputAction())) {
			String extLogin = userAdminForm.getExtLogin();
			List extLoginUniqueCheckUserList = getUserAdminService().fetchUserEntityListForExtLogin(
					extLogin);
			if (extLoginUniqueCheckUserList != null && !extLoginUniqueCheckUserList.isEmpty()) {
				if (extLoginUniqueCheckUserList.size() > 1) {
					MessageUtils.addActionError(request, "useradmin.errors.extloginnotunique");
					UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
					return mapping.findForward(resolveUserEditRedirect(userType));
				}
				else {
					UserEntity userWithSameExtLogin = (UserEntity) extLoginUniqueCheckUserList.get(0);
					if (!userWithSameExtLogin.getUserId().equals(user.getUserId())) {
						MessageUtils.addActionError(request, "useradmin.errors.extloginnotunique");
						UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
						return mapping.findForward(resolveUserEditRedirect(userType));
					}
				}
			}

			user.setExtLogin(extLogin);
			DriverEntity driver = userAdminForm.getDriverEntity();
			boolean driverFlag = userAdminForm.isDriverFlag();
			if (driver != null) {
				driver.setActiveFlag(driverFlag);
				try {
					driver.setCertifiedFromDat(userAdminForm.getCertifiedFrom());
				}
				catch (Exception e) {
					MessageUtils.addActionWarning(request,
							"useradmin.errors.invalid.certified.from");
				}
				try {
					driver.setCertifiedToDat(userAdminForm.getCertifiedTo());
				}
				catch (Exception e) {
					MessageUtils.addActionWarning(request, "useradmin.errors.invalid.certified.to");
				}
			}

			try {
				user.setPdaPinValidToDat(userAdminForm.getPdaPinValidTo());
			}
			catch (LogonException e) {
				/* TODO: uncomment, if ADN61832 is needed. */
				//if (! StringUtils.empty(user.getPdaPin())) {
				MessageUtils.addActionWarning(request, "useradmin.errors.invalid.pdavalidto");
				/* TODO: uncomment, if ADN61832 is needed. */
				//}
			}
			String pdaUserNoStr = userAdminForm.getPdaUserNoStr();
			Long pdaUserNo = null;
			if (!StringUtils.empty(pdaUserNoStr)) {
				try {
					pdaUserNo = Long.valueOf(pdaUserNoStr);
				}
				catch (NumberFormatException nfe) {
					MessageUtils.addActionError(request, "useradmin.errors.invalid.pdauserno");
					UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
					return mapping.findForward(resolveUserEditRedirect(userType));
				}
				user.setPdaUserNo(pdaUserNo);
			}
			else {
				user.setPdaUserNo(null);
			}

			boolean validationPassed = setImagesIfUploaded(request, userAdminForm, user);

			if (!validationPassed) {
				UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
				return mapping.findForward(resolveUserEditRedirect(userType));
			}

			Map userSelections = createSelectionsMap(userAdminForm, request);
			try {

				if ((MessageUtils.getActionErrors(request) == null)
						|| (MessageUtils.getActionErrors(request).size() == 0)) {
					user = modifyUser(user, driver, userSelections,
							userAdminForm.getSelectedTimeZoneCd(), false);
				}
				else {
					UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
					return mapping.findForward(resolveUserEditRedirect(userType));
				}
			}
			catch (RuntimeException e) {
				if (e.getCause() instanceof LogonException) {
					LogonException cause = ((LogonException) e.getCause());
					if (Reason.RES_USER_LDAP_HOLSPACE_NOT_UNIQUE.getReason().equals(
							cause.getReason().getReason())) {
						LOG.error("User ldap holpace not unique", e);
						MessageUtils.addActionError(request,
								"useradmin.errors.ldapholspacenotunique");
						UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
						return mapping.findForward(resolveUserEditRedirect(userType));
					}
					else if (Reason.RES_USER_PDA_USER_NUMBER_NOT_UNIQUE.getReason().equals(
							cause.getReason().getReason())) {
						LOG.error("User ldap pda user number not unique", e);
						MessageUtils.addActionError(request,
								"useradmin.errors.userpdausernumbernotunique");
						UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
						return mapping.findForward(resolveUserEditRedirect(userType));
					}
					else if (Reason.RES_IMAGE_NAME_NOT_UNIQUE.getReason().equals(
							cause.getReason().getReason())) {
						LOG.error("Image name not unique", e);
						MessageUtils.addActionError(request, "useradmin.errors.imagenamenotunique");
						UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
						return mapping.findForward(resolveUserEditRedirect(userType));
					}

					throw e;
				}

				throw new LogonException(e);
			}
			initUser(request, userAdminForm, user.getUserId());
			handleExtLoginString(userAdminForm, request);
			MessageUtils.addActionMessage(request, "message.successUpd");
			
			UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
			forwardName = resolveUserEditRedirect(userType);
		}
		else if ("saveUserAdding".equals(userAdminForm.getInputAction())) {
			String extLogin = userAdminForm.getExtLogin();

			List extLoginUniqueCheckUserList = getUserAdminService().fetchUserEntityListForExtLogin(
					extLogin);
			if (extLoginUniqueCheckUserList != null && !extLoginUniqueCheckUserList.isEmpty()) {
				MessageUtils.addActionError(request, "useradmin.errors.extloginnotunique");
				return mapping.findForward(FORWARD_DETAIL);
			}

			user.setExtLogin(extLogin);

			DriverEntity driver = userAdminForm.getDriverEntity();
			Map userSelections = createSelectionsMap(userAdminForm, request);
			boolean driverFlag = userAdminForm.isDriverFlag();
			if (driver != null) {
				driver.setActiveFlag(driverFlag);
				try {
					driver.setCertifiedFromDat(userAdminForm.getCertifiedFrom());
				}
				catch (Exception e) {
					MessageUtils.addActionWarning(request,
							"useradmin.errors.invalid.certified.from");
				}
				try {
					driver.setCertifiedToDat(userAdminForm.getCertifiedTo());
				}
				catch (Exception e) {
					MessageUtils.addActionWarning(request, "useradmin.errors.invalid.certified.to");
				}
			}

			try {
				user.setPdaPinValidToDat(userAdminForm.getPdaPinValidTo());
			}
			catch (LogonException e) {
				/* TODO: uncomment, if ADN61832 is needed. */
				//if (! StringUtils.empty(user.getPdaPin())) {
				MessageUtils.addActionWarning(request, "useradmin.errors.invalid.pdavalidto");
				/* TODO: uncomment, if ADN61832 is needed. */
				//}
			}
			String pdaUserNoStr = userAdminForm.getPdaUserNoStr();
			Long pdaUserNo = null;
			if (!StringUtils.empty(pdaUserNoStr)) {
				try {
					pdaUserNo = Long.valueOf(pdaUserNoStr);
				}
				catch (NumberFormatException nfe) {
					MessageUtils.addActionError(request, "useradmin.errors.invalid.pdauserno");
					return mapping.findForward(FORWARD_DETAIL);
				}
				user.setPdaUserNo(pdaUserNo);
			}
			else {
				user.setPdaUserNo(null);
			}

			boolean validationPassed = setImagesIfUploaded(request, userAdminForm, user);

			if (!validationPassed) {
				return mapping.findForward(FORWARD_DETAIL);
			}

			try {

				if ((MessageUtils.getActionErrors(request) == null)
						|| (MessageUtils.getActionErrors(request).size() == 0)) {

					updateTimeZoneOnUser(user, userAdminForm.getSelectedTimeZoneCd());
					user = getServiceLocator().getUserAdminService().addUser(getUser().getUserId(),
							user, driver, userSelections, driverFlag);
				}
				else {
					return mapping.findForward(FORWARD_DETAIL);
				}
			}
			catch (RuntimeException e) {
				if (e.getCause() instanceof LogonException) {
					LogonException cause = ((LogonException) e.getCause());
					if (Reason.RES_USER_LDAP_HOLSPACE_NOT_UNIQUE.getReason().equals(
							cause.getReason().getReason())) {
						MessageUtils.addActionError(request,
								"useradmin.errors.ldapholspacenotunique");
						return mapping.findForward(FORWARD_DETAIL);
					}
					else if (Reason.RES_USER_PDA_USER_NUMBER_NOT_UNIQUE.getReason().equals(
							cause.getReason().getReason())) {
						MessageUtils.addActionError(request,
								"useradmin.errors.userpdausernumbernotunique");
						return mapping.findForward(FORWARD_DETAIL);
					}
					else if (Reason.RES_IMAGE_NAME_NOT_UNIQUE.getReason().equals(
							cause.getReason().getReason())) {
						MessageUtils.addActionError(request, "useradmin.errors.imgaenamenotunique");
						return mapping.findForward(FORWARD_DETAIL);
					}

					throw e;
				}
				else {

					throw new LogonException(e);
				}
			}
			initUser(request, userAdminForm, user.getUserId());
			handleExtLoginString(userAdminForm, request);
			userAdminForm.setUserId(user.getUserId().toString());
			MessageUtils.addActionMessage(request, "message.successUpd");
			forwardName = FORWARD_DETAIL;
		}
		else {
			forwardName = "fetchfirst";
		}

		return mapping.findForward(forwardName);

	}

	//LOGON-9548: selected rolemap must contain default values for certain
	private void initSelectedRoleMap(UserAdminForm form) {

		UserEntity entity = form.getUserEntity();
		Long userTypeCdValue = entity.getUserType().getUserTypeCd();
		Map<Long, RoleEntity> defaults = getUserAdminService().getDefaultRoleForUserType(
				userTypeCdValue);

		for (Map.Entry<Long, RoleEntity> entry : defaults.entrySet()) {
			Long key = entry.getKey();
			RoleEntity value = entry.getValue();
			form.getSelectedRoleMap().put(key, value);
		}

	}

	/**
	 * @param request
	 * @param userAdminForm
	 * @param user
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private boolean setImagesIfUploaded(HttpServletRequest request, UserAdminForm userAdminForm,
			UserEntity user) throws FileNotFoundException, IOException {

		boolean result = false;

		boolean validationPassed = true;

		String contentType = null;
		MimeTypeBsEntity mime = null;

		if (userAdminForm.getSignature() != null) {
			contentType = userAdminForm.getSignatureContentType();

			SignatureEntity signature = null;
			if (user.getSignature() == null) {
				signature = new SignatureEntity();
			}
			else {
				signature = user.getSignature();
			}
			signature.setData(userAdminForm.getSignature());
			try {
				mime = getUserAdminService().fetchMimeTypeByMimeTypeName(contentType);
			}
			catch (Exception e) {
				LOG.error("Unsupported mime type: " + contentType);
				MessageUtils.addActionError(request, "useradmin.errors.mimeunsupported",
						contentType);
				return result = validationPassed = false;
			}
			signature.setDataMimeType(mime);
			user.setSignature(signature);
		}
		if (userAdminForm.getPhotoImage() != null) {
			contentType = userAdminForm.getPhotoImageContentType();

			ImageEntity photoImage = null;
			if (user.getPhotoImage() == null) {
				photoImage = new ImageEntity();
			}
			else {
				photoImage = user.getPhotoImage();
			}
			photoImage.setData(userAdminForm.getPhotoImage());
			photoImage.setImageName(userAdminForm.getPhotoImageFilename());
			mime = getUserAdminService().fetchMimeTypeByMimeTypeName(contentType);
			if (mime == null) {
				LOG.error("Unsupported mime type: " + contentType);
				MessageUtils.addActionError(request, "useradmin.errors.mimeunsupported",
						contentType);
				return result = validationPassed = false;
			}
			photoImage.setDataMimeType(mime);
			user.setPhotoImage(photoImage);
		}

		return result = validationPassed;
	}

	/**
	 * @param request
	 * @param userAdminForm
	 * @return
	 */
	public String handleModifyUser(HttpServletRequest request, UserAdminForm userAdminForm) {



		Long userId = Long.valueOf(userAdminForm.getUserId());
		userAdminForm.initMaps();
		initUser(request, userAdminForm, Long.valueOf(userId));
		handleExtLoginString(userAdminForm, request);
		initRoles(userAdminForm, true);
		initGrantableRoles(userAdminForm);

		UserTypeBsEntity userType = userAdminForm.getUserEntity().getUserType();
		
		return resolveUserEditRedirect(userType);

	}

	protected void initRoles(UserAdminForm userAdminForm, boolean assigned) {

		Long loginUserId = UserProvider.getUser().getUserId();
		List selectableRoleList = getServiceLocator().getUserAdminService().fetchGrantableRoleList(
				loginUserId);
		Map selectableRoleMap = new LinkedHashMap();
		Map assignedRoleMap = new LinkedHashMap();

		// FIXME 05-Sep-2012/stich: had to add this because of NPE. check whether whole initRoles makes sense
		if (userAdminForm.getUserEntity().getUserId() != null) {
			List assignedRoleList = getUserAdminService().fetchAssignedRoleList(
					userAdminForm.getUserEntity().getUserId());
			fillRoleMap(assignedRoleList, assignedRoleMap, true);
		}
		// FIXME 05-Sep-2012/stich: had to add 'userAdminForm.getGrantableRoleMap() != null' to avoid NPE
		if (!assigned && userAdminForm.getGrantableRoleMap() != null) {
			removeAssignedRoles(assignedRoleMap, userAdminForm.getGrantableRoleMap());
		}

		fillRoleMap(selectableRoleList, selectableRoleMap, false);
		userAdminForm.setSelectableRoleMap(selectableRoleMap);
		userAdminForm.setSelectedRoleMap(assignedRoleMap);
	}

	/**
	 * @param assignedRoleMap
	 * @param grantableRoleMap
	 */
	private void removeAssignedRoles(Map assignedRoleMap, Map grantableRoleMap) {
		for (Object key : grantableRoleMap.keySet()) {
			assignedRoleMap.remove(key);
		}
	}

	protected void deletePolls(UserAdminForm userAdminForm) {
		Map<Object, BoxListEntity> assignedPoolMap = userAdminForm.getSelectedPoolMap();
		Map<Object, BoxListEntity> selectablePoolMap = userAdminForm.getSelectablePoolMap();

		deleteAllRestrictions(assignedPoolMap, selectablePoolMap);

		userAdminForm.setSelectablePoolMap(selectablePoolMap);
		userAdminForm.setSelectedPoolMap(assignedPoolMap);
	}

	protected void deleteAllCarriers(UserAdminForm userAdminForm) {
		Map<Object, BoxListEntity> selectedRestrMap = userAdminForm.getSelectedCarrierMap();
		Map<Object, BoxListEntity> selectableRestrMap = userAdminForm.getSelectableCarrierMap();

		deleteAllRestrictions(selectedRestrMap, selectableRestrMap);

		userAdminForm.setSelectableCarrierMap(selectableRestrMap);
		userAdminForm.setSelectedCarrierMap(selectedRestrMap);
	}

	protected void deleteAllSalesGroups(UserAdminForm userAdminForm) {
		Map<Object, BoxListEntity> selectedRestrMap = userAdminForm.getSelectedSgroupMap();
		Map<Object, BoxListEntity> selectableRestrMap = userAdminForm.getSelectableSgroupMap();

		deleteAllRestrictions(selectedRestrMap, selectableRestrMap);

		userAdminForm.setSelectableSgroupMap(selectableRestrMap);
		userAdminForm.setSelectedSgroupMap(selectedRestrMap);
	}

	protected void deleteAllPurchOrg(UserAdminForm userAdminForm) {
		Map<Object, BoxListEntity> selectedRestrMap = userAdminForm.getSelectedPurchOrgMap();
		Map<Object, BoxListEntity> selectableRestrMap = userAdminForm.getSelectablePurchOrgMap();

		deleteAllRestrictions(selectedRestrMap, selectableRestrMap);

		userAdminForm.setSelectablePurchOrgMap(selectableRestrMap);
		userAdminForm.setSelectedPurchOrgMap(selectedRestrMap);
	}

	protected void deletePlants(UserAdminForm userAdminForm) {
		Map<Object, BoxListEntity> assignedPlantMap = userAdminForm.getSelectedPlantShippingPointPlantMap();
		Map<Object, BoxListEntity> selectablePlantMap = userAdminForm.getSelectablePlantShippingPointPlantMap();

		deleteAllRestrictions(assignedPlantMap, selectablePlantMap);

		userAdminForm.setSelectablePlantShippingPointPlantMap(selectablePlantMap);
		userAdminForm.setSelectedPlantShippingPointPlantMap(assignedPlantMap);

		refreshShippingPointsMap(userAdminForm);
	}

	protected void deleteShippingPoints(UserAdminForm userAdminForm) {
		Map<Object, BoxListEntity> assignedSpMap = userAdminForm.getSelectedPlantShippingPointShippingPointMap();
		Map<Object, BoxListEntity> selectableSpMap = userAdminForm.getSelectablePlantShippingPointShippingPointMap();

		deleteAllRestrictions(assignedSpMap, selectableSpMap);

		userAdminForm.setSelectablePlantShippingPointShippingPointMap(selectableSpMap);
		userAdminForm.setSelectedPlantShippingPointShippingPointMap(assignedSpMap);
	}

	/**
	 * @param userAdminForm
	 */
	private void initGrantableRoles(UserAdminForm userAdminForm) {
		Long loginUserId = UserProvider.getUser().getUserId();
		List grantableRoles = getServiceLocator().getUserAdminService().fetchGrantableRoleList(
				loginUserId);
		Map grantableRoleMap = new LinkedHashMap();
		for (Object o : grantableRoles) {
			RoleEntity role = (RoleEntity) o;
			grantableRoleMap.put(role.getRoleId(), role.getRoleName());
		}
		userAdminForm.setGrantableRoleMap(grantableRoleMap);
	}

	/**
	 * @adnlog.exclude sourceList
	 * @adnlog.exclude destMap
	 */
	private void fillRoleMap(List sourceList, Map destMap, boolean assigned) {

		if (assigned) {
			for (Iterator iterator = sourceList.listIterator(); iterator.hasNext();) {
				UserRoleEntity userRoleEntity = (UserRoleEntity) iterator.next();
				destMap.put(userRoleEntity.getRole().getRoleId(), userRoleEntity.getRole());
			}
		}
		else {
			for (Iterator iterator = sourceList.listIterator(); iterator.hasNext();) {
				RoleEntity roleEntity = (RoleEntity) iterator.next();
				destMap.put(roleEntity.getRoleId(), roleEntity);
			}
		}

	}

	private void handleCustomerShipToSelections(UserAdminForm userAdminForm,
			HttpServletRequest request, int operation) {

		switch (operation) {
		case SELECTION_OPERATION_ADD_CUSTOMERS:
			handleSelections(userAdminForm.getSelectableSoldToShipToSoldToMap(),
					userAdminForm.getSelectedSoldToShipToSoldToMap(),
					userAdminForm.getSelectedSoldToIds(), null);

			refreshCustomerShipToMap(userAdminForm);
			break;
		case SELECTION_OPERATION_ADD_SHIPTOS:
			String[] selectedShipTos = userAdminForm.getSelectedShipToIds();
			handleSelections(userAdminForm.getSelectableSoldToShipToShipToMap(),
					userAdminForm.getSelectedSoldToShipToShipToMap(), selectedShipTos, null);
			break;
		case SELECTION_OPERATION_REMOVE_CUSTOMER:
			String soldToIdToRemove = (String) request.getParameter("customerId");
			handleSelections(userAdminForm.getSelectableSoldToShipToSoldToMap(),
					userAdminForm.getSelectedSoldToShipToSoldToMap(), null, soldToIdToRemove);

			refreshCustomerShipToMap(userAdminForm);
			break;
		case SELECTION_OPERATION_REMOVE_SHIPTO:
			if (request.getParameter("shipToId") != null) {
				String shipToIdToRemove = (String) request.getParameter("shipToId");
				handleSelections(userAdminForm.getSelectableSoldToShipToShipToMap(),
						userAdminForm.getSelectedSoldToShipToShipToMap(), null, shipToIdToRemove);
			}
			break;
		case SELECTION_OPERATION_REMOVE__ALL_CUSTOMER:
			Map<Object, BoxListEntity> selectedRestrMap = userAdminForm.getSelectedSoldToShipToSoldToMap();
			Map<Object, BoxListEntity> selectableRestMap = userAdminForm.getSelectableSoldToShipToSoldToMap();

			deleteAllRestrictions(selectedRestrMap, selectableRestMap);

			userAdminForm.setSelectedSoldToShipToSoldToMap(selectedRestrMap);
			userAdminForm.setSelectableSoldToShipToSoldToMap(selectableRestMap);

			refreshCustomerShipToMap(userAdminForm);
			break;
		case SELECTION_OPERATION_REMOVE__ALL_SHIPTOS:
			selectedRestrMap = userAdminForm.getSelectedSoldToShipToShipToMap();
			selectableRestMap = userAdminForm.getSelectableSoldToShipToShipToMap();

			deleteAllRestrictions(selectedRestrMap, selectableRestMap);

			userAdminForm.setSelectedSoldToShipToShipToMap(selectedRestrMap);
			userAdminForm.setSelectableSoldToShipToShipToMap(selectableRestMap);
			break;
		default:
			break;
		}

	}

	private void handlePlantShippingPointSelections(UserAdminForm userAdminForm,
			HttpServletRequest request, int operation) {

		switch (operation) {
		case SELECTION_OPERATION_ADD_PLANTS:
			handleSelections(userAdminForm.getSelectablePlantShippingPointPlantMap(),
					userAdminForm.getSelectedPlantShippingPointPlantMap(),
					userAdminForm.getSelectedPlantIds(), null);
			refreshShippingPointsMap(userAdminForm);
			break;
		case SELECTION_OPERATION_ADD_SHIPPINGPOINTS:
			String[] selectedShippingPoints = userAdminForm.getSelectedShippingPointIds();
			handleSelections(userAdminForm.getSelectablePlantShippingPointShippingPointMap(),
					userAdminForm.getSelectedPlantShippingPointShippingPointMap(),
					selectedShippingPoints, null);
			break;
		case SELECTION_OPERATION_REMOVE_PLANT:
			String plantIdToRemove = (String) request.getParameter("plantId");
			handleSelections(userAdminForm.getSelectablePlantShippingPointPlantMap(),
					userAdminForm.getSelectedPlantShippingPointPlantMap(), null, plantIdToRemove);

			refreshShippingPointsMap(userAdminForm);

			break;
		case SELECTION_OPERATION_REMOVE_SHIPPINGPOINT:
			if (request.getParameter("shippingPointId") != null) {
				String shippingPointIdToRemove = (String) request.getParameter("shippingPointId");
				handleSelections(userAdminForm.getSelectablePlantShippingPointShippingPointMap(),
						userAdminForm.getSelectedPlantShippingPointShippingPointMap(), null,
						shippingPointIdToRemove);
			}
			break;
		case SELECTION_OPERATION_REMOVE__ALL_SHIPPINGPOINTS:
			userAdminForm.getSelectablePlantShippingPointShippingPointMap().putAll(
					userAdminForm.getSelectedPlantShippingPointShippingPointMap());
			userAdminForm.getSelectedPlantShippingPointShippingPointMap().clear();
			break;
		default:
			break;
		}
	}

	private void refreshShippingPointsMap(UserAdminForm userAdminForm) {

		Set selectedPlantCdSet = userAdminForm.getSelectedPlantShippingPointPlantMap().keySet();

		// special case when all plants removed --> remove all selected shippingpoints and return
		if (selectedPlantCdSet.isEmpty()) {
			userAdminForm.setSelectablePlantShippingPointShippingPointMap(new LinkedHashMap());
			userAdminForm.setSelectedPlantShippingPointShippingPointMap(new LinkedHashMap());

			return;
		}

		UserEntity user = userAdminForm.getUserEntity();
		Long modifyUserId = user == null ? null : user.getUserId();
		Long userId = AuthUtils.getUserId();

		String format = "%s, %s (%s)"; // format the output text for the list box
		Map curSelectableMap = getUserAdminService().fetchAssignedShippingPointList(userId,
				new ArrayList(selectedPlantCdSet), true, format);
		Map curAssignedMap = null;
		if (modifyUserId != null) {
			curAssignedMap = getUserAdminService().fetchAssignedShippingPointList(modifyUserId,
					new ArrayList(selectedPlantCdSet), true, format);
		}

		Map assignedMap = userAdminForm.getSelectedPlantShippingPointShippingPointMap();
		Map selectableMap = userAdminForm.getSelectablePlantShippingPointShippingPointMap();

		refreshMaps(assignedMap, selectableMap, curAssignedMap, curSelectableMap);

	}

	private void refreshSareaMap(UserAdminForm userAdminForm) {

		Set selectedCompCodeCdSet = userAdminForm.getSelectedCompCodeSareaCompCodeMap().keySet();

		// special case when all plants removed --> remove all selected shippingpoints and return
		if (selectedCompCodeCdSet.isEmpty()) {
			userAdminForm.setSelectableCompCodeSareaSareaMap(new LinkedHashMap());
			userAdminForm.setSelectedCompCodeSareaSareaMap(new LinkedHashMap());

			// refresh the plant/shippingpoint restriction map's
			refreshPlantMap(userAdminForm);
			return;
		}

		// prepare services 
		UserAdminService userAdminService = getUserAdminService();

		UserEntity user = userAdminForm.getUserEntity();
		Long modifyUserId = user == null ? null : user.getUserId();
		Long userId = AuthUtils.getUserId();

		List selectedCompCodeCdList = new ArrayList(selectedCompCodeCdSet);

		Map curSelectableMap = userAdminService.fetchAssignedSareaList(userId,
				selectedCompCodeCdList, true);
		Map curAssignedMap = null;
		if (modifyUserId != null) {
			curAssignedMap = userAdminService.fetchAssignedSareaList(modifyUserId,
					selectedCompCodeCdList, true);
		}

		Map assignedMap = userAdminForm.getSelectedCompCodeSareaSareaMap();
		Map selectableMap = userAdminForm.getSelectableCompCodeSareaSareaMap();

		refreshMaps(assignedMap, selectableMap, curAssignedMap, curSelectableMap);

		// refresh the plant/shippingpoint restriction map's
		refreshPlantMap(userAdminForm);

	}

	private void refreshPlantMap(UserAdminForm userAdminForm) {

		Set selectedCompCodeCdSet = userAdminForm.getSelectedCompCodeSareaCompCodeMap().keySet();
		Set selectedSareaCdSet = userAdminForm.getSelectedCompCodeSareaSareaMap().keySet();

		// special case when all companycode and/or sarea removed --> remove all selected shippingpoints and return
		if ((selectedCompCodeCdSet == null || selectedCompCodeCdSet.isEmpty())
				&& (selectedSareaCdSet == null || selectedSareaCdSet.isEmpty())) {

			userAdminForm.setSelectablePlantShippingPointPlantMap(new LinkedHashMap());
			userAdminForm.setSelectedPlantShippingPointPlantMap(new LinkedHashMap());
			userAdminForm.setSelectablePlantShippingPointShippingPointMap(new LinkedHashMap());
			userAdminForm.setSelectedPlantShippingPointShippingPointMap(new LinkedHashMap());

			return;
		}

		// prepare services 
		UserAdminService userAdminService = getUserAdminService();

		UserEntity user = userAdminForm.getUserEntity();
		Long modifyUserId = user == null ? null : user.getUserId();
		Long userId = AuthUtils.getUserId();

		List selectedCompCodeCdList = new ArrayList(selectedCompCodeCdSet);
		List selectedSareaCdList = new ArrayList(selectedSareaCdSet);

		Map curSelectableMap = userAdminService.fetchAssignedPlantList(userId,
				selectedCompCodeCdList, selectedSareaCdList, true);
		Map curAssignedMap = null;
		if (modifyUserId != null) {
			curAssignedMap = userAdminService.fetchAssignedPlantList(modifyUserId,
					selectedCompCodeCdList, selectedSareaCdList, true);
		}

		Map assignedMap = userAdminForm.getSelectedPlantShippingPointPlantMap();
		Map selectableMap = userAdminForm.getSelectablePlantShippingPointPlantMap();

		refreshMaps(assignedMap, selectableMap, curAssignedMap, curSelectableMap);

		refreshShippingPointsMap(userAdminForm);
	}

	private void handleCompCodeSareaSelections(UserAdminForm userAdminForm,
			HttpServletRequest request, int operation) {

		switch (operation) {
		case SELECTION_OPERATION_ADD_COMPANYCODES:
			String[] selectedCompCodeIds = userAdminForm.getSelectedCompCodeIds();
			handleSelections(userAdminForm.getSelectableCompCodeSareaCompCodeMap(),
					userAdminForm.getSelectedCompCodeSareaCompCodeMap(), selectedCompCodeIds, null);
			refreshSareaMap(userAdminForm);
			break;
		case SELECTION_OPERATION_ADD_SAREAS:
			String[] selectedSareas = userAdminForm.getSelectedSareaIds();
			handleSelections(userAdminForm.getSelectableCompCodeSareaSareaMap(),
					userAdminForm.getSelectedCompCodeSareaSareaMap(), selectedSareas, null);

			// refresh the plant/shippingpoint restriction map's
			refreshPlantMap(userAdminForm);
			break;
		case SELECTION_OPERATION_REMOVE_COMPANYCODE:
			if (request.getParameter("compCodeId") != null) {
				String compCodeIdToRemove = (String) request.getParameter("compCodeId");
				handleSelections(userAdminForm.getSelectableCompCodeSareaCompCodeMap(),
						userAdminForm.getSelectedCompCodeSareaCompCodeMap(), null,
						compCodeIdToRemove);
				refreshSareaMap(userAdminForm);
			}
			break;
		case SELECTION_OPERATION_REMOVE_SAREA:
			if (request.getParameter("sareaId") != null) {
				String sareaIdToRemove = (String) request.getParameter("sareaId");
				handleSelections(userAdminForm.getSelectableCompCodeSareaSareaMap(),
						userAdminForm.getSelectedCompCodeSareaSareaMap(), null, sareaIdToRemove);
				// refresh the plant/shippingpoint restriction map's
				refreshPlantMap(userAdminForm);
			}
			break;
		case SELECTION_OPERATION_REMOVE__ALL_COMPANYCODES:
			Map<Object, BoxListEntity> selectedRestrMap = userAdminForm.getSelectedCompCodeSareaCompCodeMap();
			Map<Object, BoxListEntity> selectableRestMap = userAdminForm.getSelectableCompCodeSareaCompCodeMap();

			deleteAllRestrictions(selectedRestrMap, selectableRestMap);

			userAdminForm.setSelectedCompCodeSareaCompCodeMap(selectedRestrMap);
			userAdminForm.setSelectableCompCodeSareaCompCodeMap(selectableRestMap);

			refreshSareaMap(userAdminForm);
			break;
		case SELECTION_OPERATION_REMOVE__ALL_SAREAS:
			selectedRestrMap = userAdminForm.getSelectedCompCodeSareaSareaMap();
			selectableRestMap = userAdminForm.getSelectableCompCodeSareaSareaMap();

			deleteAllRestrictions(selectedRestrMap, selectableRestMap);

			userAdminForm.setSelectedCompCodeSareaSareaMap(selectedRestrMap);
			userAdminForm.setSelectableCompCodeSareaSareaMap(selectableRestMap);

			refreshPlantMap(userAdminForm);

			break;
		default:
			break;
		}
	}

	private Map createSelectionsMap(UserAdminForm form, HttpServletRequest request) {

		Map result = null;

		Map selections = new HashMap();

		List roleIdList = new ArrayList();
		List soldToIdList = new ArrayList();
		List shipToList = new ArrayList();
		List poolIdList = new ArrayList();
		List carrierIdList = new ArrayList();
		List plantIdList = new ArrayList();
		List shippingpointList = new ArrayList();
		List compCodeIdList = new ArrayList();
		List sareaList = new ArrayList();
		List sgroupIdList = new ArrayList();
		List purchOrgIdList = new ArrayList();

		Map roleMap = null;
		Map soldToMap = null;
		Map shipToMap = null;
		Map poolMap = null;
		Map carrierMap = null;
		Map plantMap = null;
		Map shippingPointMap = null;
		Map compCodeMap = null;
		Map sareaMap = null;
		Map sgroupMap = null;
		Map purchOrgMap = null;

		roleMap = form.getSelectedRoleMap();
		if ((roleMap != null) && (!roleMap.isEmpty())) {
			roleIdList.addAll(roleMap.keySet());
			selections.put("roleIds", roleIdList);
		}
		else {
			MessageUtils.addActionError(request, "useradmin.errors.noRole");
		}

		soldToMap = form.getSelectedSoldToShipToSoldToMap();
		if (soldToMap != null) {
			soldToIdList.addAll(soldToMap.keySet());
			selections.put("soldToIds", soldToIdList);
		}
		shipToMap = form.getSelectedSoldToShipToShipToMap();
		if (shipToMap != null) {
			shipToList.addAll(shipToMap.keySet());
			selections.put("shipToIds", shipToList);
		}
		poolMap = form.getSelectedPoolMap();
		if (poolMap != null) {
			poolIdList.addAll(poolMap.keySet());
			selections.put("poolIds", poolIdList);
		}
		carrierMap = form.getSelectedCarrierMap();
		if (carrierMap != null) {
			carrierIdList.addAll(carrierMap.keySet());
			selections.put("carrierIds", carrierIdList);
		}
		plantMap = form.getSelectedPlantShippingPointPlantMap();
		if (plantMap != null) {
			plantIdList.addAll(plantMap.keySet());
			selections.put("plantIds", plantIdList);
		}
		shippingPointMap = form.getSelectedPlantShippingPointShippingPointMap();
		if (shippingPointMap != null) {
			shippingpointList.addAll(shippingPointMap.keySet());
			selections.put("shippingpointIds", shippingpointList);
		}
		compCodeMap = form.getSelectedCompCodeSareaCompCodeMap();
		if (compCodeMap != null) {
			compCodeIdList.addAll(compCodeMap.keySet());
			selections.put("compCodeIds", compCodeIdList);
		}
		sareaMap = form.getSelectedCompCodeSareaSareaMap();
		if (sareaMap != null) {
			sareaList.addAll(sareaMap.keySet());
			selections.put("sareaIds", sareaList);
		}
		sgroupMap = form.getSelectedSgroupMap();
		if (sgroupMap != null) {
			sgroupIdList.addAll(sgroupMap.keySet());
			selections.put("sgroupIds", sgroupIdList);
		}
		purchOrgMap = form.getSelectedPurchOrgMap();
		if (purchOrgMap != null) {
			purchOrgIdList.addAll(purchOrgMap.keySet());
			selections.put("purchOrgIds", purchOrgIdList);
		}

		return result = selections;

	}
	
	/**
	 * Return redirect page based on user function. If it is authorized to edit returns to edit page else returns to readonly page.
	 * @param userType containing function
	 * @return
	 */
	private String resolveUserEditRedirect(UserTypeBsEntity userType) {
		if (Authorization.isAllowed(userType.getFunctionId())) {
			return FORWARD_DETAIL;
		}
		return FORWARD_DETAIL_READONLY;
	}
}
