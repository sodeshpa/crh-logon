/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.useradmin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.forms.AdminRFIDPopupForm;
import com.crh.logon.admin.web.forms.useradmin.UserAdminForm;
import com.crh.logon.business.services.interfaces.UserAdminService;
import com.crh.logon.business.services.utils.RFIDBean;
import com.crh.logon.business.services.utils.RFIDBean.AbstractRFIDBean;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.PersonEntity;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.refdata.LangBsEntity;
import com.crh.logon.persistence.entities.refdata.TimezoneBsEntity;
import com.crh.logon.persistence.entities.refdata.UserTypeBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapVendorEntity;
import com.crh.logon.persistence.entities.views.CarrierVendorEntity;
import com.crh.logon.utils.AuthUtils;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.BoxListEntity;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.UserProvider;

/**
 * @author AdNovum Informatik AG
 */
public class AbstractUserAdminAction extends AdminActionSupport {

	private static final JcanLogger LOG =
		JcanLoggerFactory.getLogger(AbstractUserAdminAction.class);

	private CarrierVendorEntity getCarrierVendorEntity(UserEntity user) {
	    if (user.getSapVendor() != null) {
            return getUserAdminService().fetchCarrierVendor(user.getSapVendor().getSapVendorId());
        }
	    return null;
	}
	/**
	 * @param request
	 * @param userAdminForm
	 * @param userId
	 */
	protected void initUser(HttpServletRequest request, UserAdminForm userAdminForm, Long userId) {
		// [[
		Object[][] params = {{"request", request},
							{"userAdminForm", userAdminForm},
							{"userId", userId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initUser", params);
		}
		// ]]
		try {
		    UserEntity user = null;
		    List driverEntityList = null;
		    CarrierVendorEntity carrier = null;
		    if (userId == null) {
		        // initiate basic values
		        user = new UserEntity();
                user.setPerson(new PersonEntity());
                UserTypeBsEntity userTypeBsEntity = new UserTypeBsEntity();
                userTypeBsEntity.setCtlActCd(Boolean.TRUE);
                user.setUserType(userTypeBsEntity);
                user.setSendDailyReportsFlag(Boolean.FALSE);
                user.setSapBp(new SapBpEntity());
                user.setSapVendor(new SapVendorEntity());
                user.setApplicationLang(new LangBsEntity());
                Date pdaPinValidToDate = getPdaPinValidToDate();
                user.setPdaPinValidToDat(pdaPinValidToDate);
                
                // set defaults from the logged in user
                UserEntity userLoggedIn = userAdminForm.getUserLoggedIn();
                user.getApplicationLang().setLangCd(userLoggedIn.getApplicationLang().getLangCd());
                UserTypeBsEntity userType = userAdminForm.getUserLoggedIn().getUserType();
                
                Long sapBpId = userLoggedIn.getSapBp() == null ? null : userLoggedIn.getSapBp().getSapBpId();
                String sapId = userLoggedIn.getSapVendor() == null ? null : userLoggedIn.getSapVendor().getSapId();
                if (sapBpId != null) {
                    SapBpEntity sapBpEntity = getDispokarteService().getSoldtoSapBpById(Long.valueOf(sapBpId));
                    user.setSapBp(sapBpEntity);
                    user.setUserType(userType);
                }
                else if (sapId != null) {
                    SapVendorEntity sapVendorEntity = getUserAdminService().fetchSapVendorBySapId(sapId);
                    user.setSapVendor(sapVendorEntity);
                    user.setUserType(userType);
                    carrier = getCarrierVendorEntity(userLoggedIn);
                }
                
		    }
		    else {
		        user = getUserAdminService().loadUserDetail(userId);
		        driverEntityList = getUserAdminService().loadDriverDetail(userId);
		        carrier = getCarrierVendorEntity(user);
		    }
		    
		    if (carrier == null) {
                carrier = new CarrierVendorEntity();
            }
			
		    // initialize form from the user
		    userAdminForm.initUser(user, driverEntityList, carrier);
		    if (user.getPdaUserNo() == null) {
		    	Long pdaUserNoSuggestion = getUserAdminService().getFreePdaUserNo(1000, 9999);
		    	if (pdaUserNoSuggestion != null) {
		    		userAdminForm.setPdaUserNoSuggestion(pdaUserNoSuggestion.toString());
		    	}
		    }
			
		    // initialize popup form
            AdminRFIDPopupForm.resetForm(request.getSession());
            if (user.getPdaUserRfidobject() == null) {
                user.setPdaUserRfidobject(new RfidobjectEntity());
                user.getPdaUserRfidobject().setRfids(new ArrayList());
                user.getPdaUserRfidobject().setRfidobjectType(RFIDBean.RFIDOBJECT_TYPE_PDA_USER);			
            }
            AbstractRFIDBean bean = RFIDBean.UserRFIDBean(user);
            AdminRFIDPopupForm.initForm(request.getSession(), bean);
			
            // error messages
		    if (driverEntityList != null && driverEntityList.size() > 1) {
                MessageUtils.addActionError(request, "useradmin.errors.driverNotUnique");
		    }
		    
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	protected boolean handleExtLoginString(UserAdminForm form, HttpServletRequest request) {
		// [[
		boolean result = false;
		Object[][] params = {{"form", form},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "handleExtLoginString", params);
		}
		// ]]
		try {
			String extLogin = null;
			boolean validationPassed = true;
			UserEntity user = form.getUserEntity();
			
			if(user.getUserType() == null || user.getUserType().getUserTypeCd() == null){
				MessageUtils.addActionError(request, "useradmin.errors.noType");
			}
			
			String email = null;
			
			//making necessary validation depending on usertype
			
			switch (form.getUserEntity().getUserType().getUserTypeCd().intValue()) {
			case Constant.USER_TYPE_CUSTOMER:
				if (Boolean.TRUE.equals(user.getExtLoginActiveFlag()) && StringUtils.empty(form.getExtLogin()) && StringUtils.empty(user.getExtLogin())) {
					email = form.getUserEntity().getPerson().getEmail();
					if (StringUtils.empty(email)) {
						MessageUtils.addActionWarning(request, "useradmin.warnings.noEmail");
					}
					else {
						form.setExtLogin(email);
					}
				}
				else if (StringUtils.empty(form.getExtLogin())) {
					form.setExtLogin(user.getExtLogin());
				}
				break;
			case Constant.USER_TYPE_CRH:
				if (StringUtils.empty(user.getLdapHolspace())) {
					MessageUtils.addActionError(request, "useradmin.errors.noHolspace");
					return result = validationPassed = false;
				}
				if ( Boolean.TRUE.equals(user.getExtLoginActiveFlag()) && (StringUtils.empty(form.getExtLogin()) && (StringUtils.empty(user.getExtLogin()))) ) {
					extLogin = user.getLdapHolspace(); // TODO (istvan May 6, 2009): truncate
					
					// TODO (istvan May 6, 2009): extract @, test if ok
					if (extLogin.contains("@")) {
						int indexOfAt = extLogin.indexOf("@");
						extLogin = extLogin.substring(0, indexOfAt);
					}
					form.setExtLogin(extLogin);
				}
				else if (StringUtils.empty(form.getExtLogin())) {
					form.setExtLogin(user.getExtLogin());
				}
				break;
			case Constant.USER_TYPE_VENDOR:
				if (Boolean.TRUE.equals(user.getExtLoginActiveFlag()) && StringUtils.empty(form.getExtLogin()) && StringUtils.empty(user.getExtLogin())) {
					email = form.getUserEntity().getPerson().getEmail();
					if (StringUtils.empty(email)) {
						MessageUtils.addActionWarning(request, "useradmin.warnings.noEmail");
					}
					else {
						form.setExtLogin(email);
					}
				}
				else if (StringUtils.empty(form.getExtLogin())) {
					form.setExtLogin(user.getExtLogin());
				}
				break;	
			default:
				break;
			}
			
			return result = validationPassed;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	protected Date getPdaPinValidToDate() {
		// [[
		Date result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getPdaPinValidToDate");
		}
		// ]]
		try {
            ConfigBean cfg = getConfigService().getConfigBean();
            int pdaPinLifeTime = cfg.getAppPolicyPINLifeTimeD();
            
            Date pdaPinValidToDate = new Date(System.currentTimeMillis());
            
            Calendar cal = DateTimeUtils.getDateAsCalendar(pdaPinValidToDate);
            cal.add(Calendar.DATE, pdaPinLifeTime);
            pdaPinValidToDate = cal.getTime();
            
            return result = pdaPinValidToDate;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }
    /**
     * @adnlog.exclude srcMap
     * @adnlog.exclude destMap
     */
    protected void handleSelections(Map srcMap, Map destMap, Object [] addedIds, Object idToRemove) {
		// [[
		Object[][] params = {{"srcMap", srcMap},
							{"addedIds", addedIds},
							{"idToRemove", idToRemove}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "handleSelections", params);
		}
		// ]]
		try {
            if(addedIds != null) {
                for (int i = 0; i < addedIds.length; i++) {
                    if(addedIds[i] != null) {
                        destMap.put(addedIds[i], srcMap.get(addedIds[i]));
                        srcMap.remove(addedIds[i]);
                    }
                }
            }
            if(idToRemove != null) {
                srcMap.put(idToRemove, destMap.get(idToRemove));
                destMap.remove(idToRemove);
            }
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
    
    protected void initRestrictionMaps(UserAdminForm form, boolean assigned, boolean grantLoggedInUserAuth, Locale locale) {
		// [[
		Object[][] params = {{"form", form},
							{"assigned", assigned + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initRestrictionMaps", params);
		}
		// ]]
		try {
			initCarriers(form, assigned, grantLoggedInUserAuth);
			initPools(form, assigned, grantLoggedInUserAuth, locale);
			initCustomerShipTos(form, assigned, grantLoggedInUserAuth);
			initCompCodeSareas(form, assigned, grantLoggedInUserAuth);
			initPlantShippingPoints(form, assigned, grantLoggedInUserAuth);
			initSgroups(form, assigned, grantLoggedInUserAuth);
			initPurchOrgs(form, assigned, grantLoggedInUserAuth);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }

    /**
     * 
     * @param userAdminForm
     * @param assigned
     */
	protected void initCarriers(UserAdminForm userAdminForm, boolean assigned, boolean grantLoggedInUserAuth) {
		// [[
		Object[][] params = {{"userAdminForm", userAdminForm},
							{"assigned", assigned + ""},
							{"grantLoggedInUserAuth", grantLoggedInUserAuth + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initCarriers", params);
		}
		// ]]
		try {
			UserEntity user = userAdminForm.getUserEntity();
			Long modifyUserId = user == null ? null : user.getUserId();
			Long userId = getUser().getUserId();
			
			Map<Object, BoxListEntity> selectableCarrierMap = new LinkedHashMap<Object, BoxListEntity>();
			Map<Object, BoxListEntity> assignedCarrierToMap = new LinkedHashMap<Object, BoxListEntity>();
			
			String format = "%s (%s) (%s)"; // format the output text for the list box
			if (!userId.equals(modifyUserId)) {
				format = "%s (%s)";				
			}
			
			Map<Object, String> carrierMap = getServiceLocator().getUserAdminService().fetchAssignedCarrierList(
					userId, true, format);
			
			mapToBoxListMap(carrierMap, selectableCarrierMap, true);
			if (assigned) {
				if (grantLoggedInUserAuth) {// set the assigned restrictions from the looged in user
					carrierMap = getServiceLocator().getUserAdminService().fetchAssignedCarrierList(
							userId, false, format);
				}
				else {
					carrierMap = getServiceLocator().getUserAdminService().fetchAssignedCarrierList(
							modifyUserId, false, format);
				}
				mapToBoxListMap(carrierMap, assignedCarrierToMap, false);
			
				refreshSelectableMap(assignedCarrierToMap, selectableCarrierMap);
			}
			
			userAdminForm.setSelectableCarrierMap(selectableCarrierMap);
			userAdminForm.setSelectedCarrierMap(assignedCarrierToMap);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	/**
	 * Initialize the selected and selectable plant/shippingpoint user restriction.
	 * 
	 * This function should be after the CompanyCode/sarea initialization.
	 * @param userAdminForm
	 * @param assigned
	 */
	public void initPlantShippingPoints(UserAdminForm userAdminForm, boolean assigned, boolean grantLoggedInUserAuth) {
		// [[
		Object[][] params = {{"userAdminForm", userAdminForm},
							{"assigned", "" + assigned}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initPlantShippingPoints", params);
		}
		// ]]
		try {
			// prepare services 
			UserAdminService userAdminService = getUserAdminService();
			
			Long modifyUserId = userAdminForm.getUserEntity() == null ? null : userAdminForm.getUserEntity().getUserId();
			Long userId = getUser().getUserId();
			
			Map<Object, String> plantMap = userAdminService.fetchAssignedPlantList(userId, null, null, true);
			
			Map<String, BoxListEntity> selectablePlantMap = new LinkedHashMap<String, BoxListEntity>();
			Map<String, BoxListEntity> assignedPlantMap = new LinkedHashMap<String, BoxListEntity>();
			
			Map<String, BoxListEntity> selectableShippingPointMap = new LinkedHashMap<String, BoxListEntity>();
			Map<String, BoxListEntity> assignedShippingPointMap = new LinkedHashMap<String, BoxListEntity>();
			
			mapToBoxListMap(plantMap, selectablePlantMap, true);			
						
			if (assigned) {
				if (grantLoggedInUserAuth) {// set the assigned restrictions from the looged in user
					plantMap = userAdminService.fetchAssignedPlantList(userId, null, null, false);
				}
				else {
					plantMap = userAdminService.fetchAssignedPlantList(modifyUserId, null, null, false);
				}
				mapToBoxListMap(plantMap, assignedPlantMap, false);
				
				refreshSelectableMap(assignedPlantMap, selectablePlantMap);
				
				if (assignedPlantMap != null && !assignedPlantMap.isEmpty()) {
					String format = "%s, %s"; // format the output text for the list box
					if (!userId.equals(modifyUserId)) {
						format = "%s, %s (%s)";
					}			
			
					List assignedPlantIdList = new ArrayList();
					assignedPlantIdList.addAll(assignedPlantMap.keySet());
					
					Map<Object, String> shippingPointMap = userAdminService.fetchAssignedShippingPointList(userId, assignedPlantIdList, true, format);
					mapToBoxListMap(shippingPointMap, selectableShippingPointMap, true);
					
					if (grantLoggedInUserAuth) {// set the assigned restrictions from the looged in user
						shippingPointMap = userAdminService.fetchAssignedShippingPointList(userId, null, false, format);
					}
					else {
						shippingPointMap = userAdminService.fetchAssignedShippingPointList(modifyUserId, null, false, format);
					}					
					mapToBoxListMap(shippingPointMap, assignedShippingPointMap, false);
			
					refreshSelectableMap(assignedShippingPointMap, selectableShippingPointMap);
				}
			}
			
			userAdminForm.setSelectablePlantShippingPointPlantMap(selectablePlantMap);
			userAdminForm.setSelectedPlantShippingPointPlantMap(assignedPlantMap);

			userAdminForm.setSelectablePlantShippingPointShippingPointMap(selectableShippingPointMap);
			userAdminForm.setSelectedPlantShippingPointShippingPointMap(assignedShippingPointMap);
			
			
			Set selectedCompCodeCdSet = userAdminForm.getSelectedCompCodeSareaCompCodeMap().keySet();
			Set selectedSareaCdSet = userAdminForm.getSelectedCompCodeSareaSareaMap().keySet();
			
			// special case when all companycode and/or sarea removed --> remove all selected shippingpoints and return
			if ((selectedCompCodeCdSet == null || selectedCompCodeCdSet.isEmpty())
					&& (selectedSareaCdSet == null || selectedSareaCdSet.isEmpty())) {

				userAdminForm.setSelectablePlantShippingPointPlantMap(new LinkedHashMap());
				userAdminForm.setSelectablePlantShippingPointShippingPointMap(new LinkedHashMap());
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	
	/**
	* Initialize the pool information.
	* 		
	* @param userAdminForm
	* @param assigned
	* @param locale
	*/
	protected void initPools(UserAdminForm userAdminForm, boolean assigned, boolean grantLoggedInUserAuth, Locale locale) {
		// [[
		Object[][] params = { { "userAdminForm", userAdminForm }, { "assigned", assigned + "" },
				{ "locale", locale } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initPools", params);
		}
		// ]]
		try {
			Map<String, BoxListEntity> selectablePoolMap = new LinkedHashMap<String, BoxListEntity>();
			Map<String, BoxListEntity> assignedPoolMap = new LinkedHashMap<String, BoxListEntity>();
			
			Long modifyUserId = userAdminForm.getUserEntity() == null ? null : userAdminForm.getUserEntity().getUserId();
			Long userId = getUser().getUserId();

			Map<Object, String> poolTxMap = getServiceLocator().getUserAdminService().fetchPoolsAssignedToUser(
					userId, true, locale);
			mapToBoxListMap(poolTxMap, selectablePoolMap, true);

			if (assigned) {
				if (grantLoggedInUserAuth) {// set the assigned restrictions from the looged in user
					poolTxMap = getServiceLocator().getUserAdminService().fetchPoolsAssignedToUser(
							userId, false, locale);
				}
				else {
					poolTxMap = getServiceLocator().getUserAdminService().fetchPoolsAssignedToUser(
							modifyUserId, false, locale);
				}
				mapToBoxListMap(poolTxMap, assignedPoolMap, false);

				refreshSelectableMap(assignedPoolMap, selectablePoolMap);
			}

			userAdminForm.setSelectablePoolMap(selectablePoolMap);
			userAdminForm.setSelectedPoolMap(assignedPoolMap);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	protected void initCustomerShipTos(UserAdminForm userAdminForm, boolean assigned, boolean grantLoggedInUserAuth) {
		// [[
		Object[][] params = {{"userAdminForm", userAdminForm},
							{"assigned", assigned + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initCustomerShipTos", params);
		}
		// ]]
		try {			
			// prepare services 
			UserAdminService userAdminService = getUserAdminService();
			
			UserEntity user = userAdminForm.getUserEntity();
			Long modifyUserId = user == null ? null : user.getUserId();
			Long userId = getUser().getUserId();
			
			Map soldToMap = getUserAdminService().fetchAssignedSoldtoList(userId, true);
			
			Map selectableSoldToMap = new LinkedHashMap();
			Map assignedSoldToMap = new LinkedHashMap();
			
			Map selectableShipToMap = new LinkedHashMap();
			Map assignedShipToMap = new LinkedHashMap();
			
			mapToBoxListMap(soldToMap, selectableSoldToMap, true);
			
			if (assigned) {
				if (grantLoggedInUserAuth) { // set the assigned restrictions from the looged in user
					soldToMap = userAdminService.fetchAssignedSoldtoList(userId, false);
				}
				else {
					soldToMap = userAdminService.fetchAssignedSoldtoList(modifyUserId, false);
				}
				mapToBoxListMap(soldToMap, assignedSoldToMap, false);
				refreshSelectableMap(assignedSoldToMap, selectableSoldToMap);
			
				if (!assignedSoldToMap.isEmpty()) {
					String format = "%s, %s"; // format the output text for the list box
					if (!userId.equals(modifyUserId)) {
						format = "%s, %s (%s)";
					}

					Map shipToMap = userAdminService.fetchAssignedShiptoList(userId, new ArrayList(
							assignedSoldToMap.keySet()), true, format);
					mapToBoxListMap(shipToMap, selectableShipToMap, true);
					
					if (grantLoggedInUserAuth) {// set the assigned restrictions from the looged in user
						shipToMap = userAdminService.fetchAssignedShiptoList(userId, null, false,
								format);
					}
					else {
						shipToMap = getServiceLocator().getUserAdminService().fetchAssignedShiptoList(
								modifyUserId, null, false, format);
					}
					mapToBoxListMap(shipToMap, assignedShipToMap, false);
					
					refreshSelectableMap(assignedShipToMap, selectableShipToMap);
				}
			
			}
			userAdminForm.setSelectableSoldToShipToSoldToMap(selectableSoldToMap);
			userAdminForm.setSelectedSoldToShipToSoldToMap(assignedSoldToMap);
			
			userAdminForm.setSelectableSoldToShipToShipToMap(selectableShipToMap);
			userAdminForm.setSelectedSoldToShipToShipToMap(assignedShipToMap);
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	protected void initCompCodeSareas(UserAdminForm userAdminForm, boolean assigned, boolean grantLoggedInUserAuth) {
		// [[
		Object[][] params = { { "userAdminForm", userAdminForm }, { "assigned", assigned + "" } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initCompCodeSareas", params);
		}
		// ]]
		try {
			// prepare services 
			UserAdminService userAdminService = getUserAdminService();
			
			UserEntity user = userAdminForm.getUserEntity();
			Long modifyUserId = user == null ? null : user.getUserId();
			Long userId = getUser().getUserId();

			Map selectableCompCodeMap = new LinkedHashMap();
			Map assignedCompCodeMap = new LinkedHashMap();

			Map selectableSareaMap = new LinkedHashMap();
			Map assignedSareaMap = new LinkedHashMap();

			Map compCodeMap = userAdminService.fetchAssignedCompCodeList(userId, true);
			mapToBoxListMap(compCodeMap, selectableCompCodeMap, true);
			
			if (assigned) {
				if (grantLoggedInUserAuth) {// set the assigned restrictions from the looged in user
					compCodeMap = userAdminService.fetchAssignedCompCodeList(userId, false);
				}
				else {
					compCodeMap = userAdminService.fetchAssignedCompCodeList(modifyUserId, false);
				}
				mapToBoxListMap(compCodeMap, assignedCompCodeMap, false);
				refreshSelectableMap(assignedCompCodeMap, selectableCompCodeMap);
				
				if (assignedCompCodeMap != null && !assignedCompCodeMap.isEmpty()) {
					Map sareaMap = userAdminService.fetchAssignedSareaList(userId, new ArrayList(
							assignedCompCodeMap.keySet()), true);
					mapToBoxListMap(sareaMap, selectableSareaMap, true);
					
					if (grantLoggedInUserAuth) {// set the assigned restrictions from the looged in user
						sareaMap = userAdminService.fetchAssignedSareaList(userId, null, false);
					}
					else {
						sareaMap = userAdminService.fetchAssignedSareaList(modifyUserId, null,
								false);
					}
					mapToBoxListMap(sareaMap, assignedSareaMap, false);
					refreshSelectableMap(assignedSareaMap, selectableSareaMap);
				}
			}

			userAdminForm.setSelectableCompCodeSareaCompCodeMap(selectableCompCodeMap);
			userAdminForm.setSelectedCompCodeSareaCompCodeMap(assignedCompCodeMap);

			userAdminForm.setSelectableCompCodeSareaSareaMap(selectableSareaMap);
			userAdminForm.setSelectedCompCodeSareaSareaMap(assignedSareaMap);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	protected void initSgroups(UserAdminForm userAdminForm, boolean assigned, boolean grantLoggedInUserAuth) {
		// [[
		Object[][] params = { { "userAdminForm", userAdminForm }, { "assigned", assigned + "" } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initSgroups", params);
		}
		// ]]
		try {
			// prepare services 
			UserAdminService userAdminService = getUserAdminService();
						
			UserEntity user = userAdminForm.getUserEntity();
			Long modifyUserId = user == null ? null : user.getUserId();
			Long userId = getUser().getUserId();
			
			String format = "%s"; // format the output text for the list box
			if (!userId.equals(modifyUserId)) {
				format = "%s (%s)";
			}
			Map selectableSgroupMap = new LinkedHashMap();
			Map assignedSgroupMap = new LinkedHashMap();

			Map sgroupMap = userAdminService.fetchAssignedSgroupList(userId, true, format);
			mapToBoxListMap(sgroupMap, selectableSgroupMap, true);
			if (assigned) {
				if (!grantLoggedInUserAuth && modifyUserId == null) {
					LOG.error("Cannot initialize the assigned Sgroups for the modified user, because the user id is null");
					return;
				}
				
				if (grantLoggedInUserAuth) {// set the assigned restrictions from the looged in user
					sgroupMap = userAdminService.fetchAssignedSgroupList(userId, false, format);
				}
				else {
					sgroupMap = userAdminService.fetchAssignedSgroupList(modifyUserId, false, format);
				}
				mapToBoxListMap(sgroupMap, assignedSgroupMap, false);
				
				refreshSelectableMap(assignedSgroupMap, selectableSgroupMap);
			}

			userAdminForm.setSelectableSgroupMap(selectableSgroupMap);
			userAdminForm.setSelectedSgroupMap(assignedSgroupMap);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}
	
	protected void initPurchOrgs(UserAdminForm userAdminForm, boolean assigned, boolean grantLoggedInUserAuth) {
		// [[
		Object[][] params = { { "userAdminForm", userAdminForm }, { "assigned", assigned + "" } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initPurchOrgs", params);
		}
		// ]]
		try {
			// prepare services 
			UserAdminService userAdminService = getUserAdminService();
						
			UserEntity user = userAdminForm.getUserEntity();
			Long modifyUserId = user == null ? null : user.getUserId();
			Long userId = getUser().getUserId();		
			
			Map selectablePurchOrgMap = new LinkedHashMap();
			Map assignedPurchOrgMap = new LinkedHashMap();

			Map purchOrgMap = userAdminService.fetchAssignedPurchOrgList(userId, true);
			mapToBoxListMap(purchOrgMap, selectablePurchOrgMap, true);
			if (assigned) {
				if (!grantLoggedInUserAuth && modifyUserId == null) {
					LOG.error("Cannot initialize the assigned PurchOrgs for the modified user, because the user id is null");
					return;
				}
				
				if (grantLoggedInUserAuth) {// set the assigned restrictions from the looged in user
					purchOrgMap = userAdminService.fetchAssignedPurchOrgList(userId, false);
				}
				else {
					purchOrgMap = userAdminService.fetchAssignedPurchOrgList(modifyUserId, false);
				}
				mapToBoxListMap(purchOrgMap, assignedPurchOrgMap, false);
				
				refreshSelectableMap(assignedPurchOrgMap, selectablePurchOrgMap);
			}

			userAdminForm.setSelectablePurchOrgMap(selectablePurchOrgMap);
			userAdminForm.setSelectedPurchOrgMap(assignedPurchOrgMap);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}
	
	protected void refreshSelectableMap(Map<?, BoxListEntity> assignedMap,
			Map<?, BoxListEntity> selectableMap) {
		// [[
		Object[][] params = {{"assignedMap", assignedMap},
							{"selectableMap", selectableMap}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "refreshSelectableMap", params);
		}
		// ]]
		try {
			if (assignedMap == null || selectableMap == null) {
				LOG.warn("One of the parameters is null: 'assignedMap': " + assignedMap == null ? "<NULL>"
						: "not null" + ", 'selectableMap' is: " + assignedMap == null ? "<NULL>"
								: "not null" + ".");
				return; // should never happen
			}
			
			for (Iterator iter = assignedMap.keySet().iterator(); iter.hasNext();) {
				Object key = iter.next();
				if (selectableMap.containsKey(key)) {
					selectableMap.remove(key);
			
					BoxListEntity entity = assignedMap.get(key);
					if (entity != null) {
						entity.setCanRemove(true);
					}
				}
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	/**
	 * 
	 * @param assignedMap
	 * @param selectableMap
	 * @param curAssignedMap
	 * @param curSelectableMap
	 */
	public void refreshMaps(Map<Object, BoxListEntity> assignedMap, Map selectableMap, Map curAssignedMap, Map curSelectableMap) {
		// [[
		Object[][] params = {{"assignedMap", assignedMap},
							{"selectableMap", selectableMap},
							{"curAssignedMap", curAssignedMap},
							{"curSelectableMap", curSelectableMap}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "refreshMaps", params);
		}
		// ]]
		try {
			if (assignedMap == null) {
				LOG.info("The 'assignedMap' is null, a new map is initialized!");
				assignedMap = new LinkedHashMap();
			}
			
			if (selectableMap == null) {
				LOG.info("The 'selectableMap' is null, a new map is initialized!");
				selectableMap = new LinkedHashMap();
			}
			
			for (Iterator iter = assignedMap.keySet().iterator(); iter.hasNext();) {
				Object key = iter.next();
				BoxListEntity entity = assignedMap.get(key);
				
				if (entity.getCanRemove()) {
					/*
					 * If the entity from the assigned map is not part of the current selectable
					 * options, remove. The entity can be removed because the logged in user
					 * has authorization to this entity.
					 * The current selectable options, contains records for the logged in user.  
					 */
					if (curSelectableMap == null || !curSelectableMap.containsKey(key)) {
						LOG.info("The record: '"
								+ entity
								+ "' is removed from the assigned map, as the record on which is " +
								"dependent is removed, and cannot be part of the restriction assigments");
						iter.remove();
					}
				}
				/*
				 * A non removable record also needs to be removed when the parent (the record on 
				 * which depends) is removed. This can happen when the master user (logged in user)
				 * has authorization for the parent record, and the parent record has more
				 * child objects (i.e.: plant can have many shipping points), but the logged in user
				 * has restriction only for some of them or none of them.
				 */
				else if (curAssignedMap == null || !curAssignedMap.containsKey(key)) {
					LOG.info("The record: '"
							+ entity
							+ "' is removed from the assigned map, as the record on which is " +
							"dependent is removed, and cannot be part of the restriction assigments");
					iter.remove();					
				}
			}
			
			LOG.info("Set the fresh data for the selectable map!");
			selectableMap.clear();
			mapToBoxListMap(curSelectableMap, selectableMap, true);
			
			refreshSelectableMap(assignedMap, selectableMap);			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	protected void refreshCustomerShipToMap(UserAdminForm userAdminForm) {
		// [[
		Object[][] params = {{"userAdminForm", userAdminForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "refreshCustomerShipToMap", params);
		}
		// ]]
		try {
			Set selectedSoldtoSet = userAdminForm.getSelectedSoldToShipToSoldToMap().keySet();
			
			// special case when all costumers removed --> remove all selected shiptos and return
			if (selectedSoldtoSet.isEmpty()) {
				userAdminForm.setSelectableSoldToShipToShipToMap(new LinkedHashMap());
				userAdminForm.setSelectedSoldToShipToShipToMap(new LinkedHashMap());
				
				return;
			}
			
			UserEntity user = userAdminForm.getUserEntity();
			Long modifyUserId = user == null ? null : user.getUserId();
			Long userId = AuthUtils.getUserId();
			
			String format = "%s, %s (%s)"; // format the output text for the list box
			Map curSelectableMap = getUserAdminService().fetchAssignedShiptoList(userId,
					new ArrayList(selectedSoldtoSet), true, format);
			Map curAssignedMap = null;
			if (modifyUserId != null) { // at create user there is no "modifyUserId" set
				curAssignedMap = getUserAdminService().fetchAssignedShiptoList(modifyUserId,
						new ArrayList(selectedSoldtoSet), true, format);
			}
			
			Map assignedMap = userAdminForm.getSelectedSoldToShipToShipToMap();
			Map selectableMap = userAdminForm.getSelectableSoldToShipToShipToMap();
			
			refreshMaps(assignedMap, selectableMap, curAssignedMap, curSelectableMap);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * Create a {@link Map<Object, BoxListEntity>} from the given {@link Map<Object, String>}.
	 * 
	 * @param sourceMap
	 * 			Map containing the displayable objects
	 * @param destMap
	 * 			A Map of {@link Map<Object, BoxListEntity>} constructed from the given
	 * 			 <code>sourceList</code> param. NOT NULLABLE.
	 * @param canRemove
	 * 			Set the {@link BoxListEntity#setCanRemove(Boolean)} 
	 * 
	 */
	protected void mapToBoxListMap(Map<Object, String> sourceMap, Map destMap, boolean canRemove) {
		// [[
		Object[][] params = {{"sourceMap", sourceMap},
							{"destMap", destMap},
							{"canRemove", canRemove + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "mapToBoxListMap", params);
		}
		// ]]
		try {
			if (sourceMap == null) {
				return; // nothing to do.
			}
			
			BoxListEntity valEntity = null;
			for (Iterator<Object> iterator = sourceMap.keySet().iterator(); iterator.hasNext();) {
				Object key = iterator.next();
				valEntity = new BoxListEntity(key, sourceMap.get(key), canRemove);
				
				destMap.put(key, valEntity);
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	protected void addVendorRestriction(UserAdminForm userAdminForm, SapVendorEntity sapVendorEntity) {
		// [[
		Object[][] params = {{"userAdminForm", userAdminForm},
							{"sapVendorEntity", sapVendorEntity}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addVendorRestriction", params);
		}
		// ]]
		try {
			if (sapVendorEntity == null) {
				LOG.warn("Cannot add vendor restriction: 'No vendor initialized in the User Admin form'.");
				return; // nothing to do
			}
			
			//adding vendor itself to the selected list
			Long sapVendorItselfSapVendorId = sapVendorEntity.getSapVendorId();
			Map selectedCarrierMap = userAdminForm.getSelectedCarrierMap();
			
			if (sapVendorItselfSapVendorId == null || selectedCarrierMap == null) {
				LOG.warn("The SAP vendor ID is not defined for the given SapVendor entity: sap vendor id: '" + sapVendorEntity.getSapVendorId() + "'.");
				return;
			}
			
			if (selectedCarrierMap.containsKey(sapVendorItselfSapVendorId)) {
				return; // the carrier is already part of carrier restriction
			}
			
			//adding selected vendor to its map
			userAdminForm.setSelectedCarrierIds(new Long[] { sapVendorItselfSapVendorId });
			handleSelections(userAdminForm.getSelectableCarrierMap(), selectedCarrierMap,
					userAdminForm.getSelectedCarrierIds(), null);

		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
		
	protected void addSoldtoRestriction(UserAdminForm userAdminForm, SapBpEntity sapBp) {
		// [[
		Object[][] params = {{"userAdminForm", userAdminForm},
							{"sapBp", sapBp}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addSoldtoRestriction", params);
		}
		// ]]
		try {
			if (sapBp == null) {
				LOG.warn("Cannot add vendor restriction: 'No Soldto initialized in the User Admin form'.");
				return; // nothing to do
			}
			
			//adding soldto itself to the selected list
			String soldToItselfSapId = null;
			soldToItselfSapId = sapBp.getSapId();
			
			Map selectedSoldtoMap = userAdminForm.getSelectedSoldToShipToSoldToMap();
			
			if (soldToItselfSapId == null || selectedSoldtoMap == null) {
				LOG.warn("The SAP id is not defined for the given SapBp entity: sapBp id: '" + sapBp.getSapBpId() + "'.");
				return; 
			}

			if (selectedSoldtoMap.containsKey(soldToItselfSapId)) {
				return; // the costumer is already part of the restriction list
			}
			
			userAdminForm.setSelectedSoldToIds(new String[] { soldToItselfSapId });
			handleSelections(userAdminForm.getSelectableSoldToShipToSoldToMap(), selectedSoldtoMap,
					userAdminForm.getSelectedSoldToIds(), null);
		
			refreshCustomerShipToMap(userAdminForm);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	/**
	 * Remove all entries from the <code>selectedRestrMap</code> map and put into <code>selectableRestMap</code>, 
	 * if the {@ link BoxListEntity#getCanRemove()} is <code>true</code>.
	 * 
	 * @param selectedRestrMap
	 * 			Map, containing all restriction of the edited user.
	 * @param selectableRestMap
	 * 			Map, containing the selectable restrictions for the edited user.
	 */
	protected void deleteAllRestrictions(Map<Object, BoxListEntity> selectedRestrMap, Map<Object, BoxListEntity> selectableRestMap) {
		// [[
		Object[][] params = {{"selectedRestrMap", selectedRestrMap},
							{"selectableRestMap", selectableRestMap}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "deleteAllRestrictions", params);
		}
		// ]]
		try {		
			if (selectableRestMap == null) {
				selectableRestMap = new LinkedHashMap<Object, BoxListEntity>();
			}
			
			if (selectedRestrMap != null) {
				for (Iterator iterator = selectedRestrMap.keySet().iterator(); iterator.hasNext();) {
					Object key = iterator.next();
					BoxListEntity entity = (BoxListEntity)selectedRestrMap.get(key);
					if (entity.getCanRemove()) {
						iterator.remove();
						
						selectableRestMap.put(entity.getValue(), entity);
					}
				}
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * Helper method used by all admin functions requiring to update the user.
	 */
	protected UserEntity modifyUser(UserEntity user,
							  DriverEntity driver,
							  Map userSelections,
							  String selectedTimeZoneCd,
							  boolean self) {

		// [[
		UserEntity result = null;
		Object[][] params = {{"user", user},
							 {"driver", driver},
							 {"userSelections", userSelections},
							 {"selectedTimeZoneCd", selectedTimeZoneCd},
							 {"self", self}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "modifyUser", params);
		}
		// ]]
		try {
			boolean updatedTimeZone = updateTimeZoneOnUser(user, selectedTimeZoneCd);

			result = getServiceLocator().getUserAdminService().modifyUser(
					getUser().getUserId(), user, driver, userSelections, self);

			if (self && updatedTimeZone) {
				TimeZone tz = getServiceLocator().getTimeZoneService().loadForUser(result);
				UserProvider.setTimezone(tz);
				LOG.debug("Updated global user TimeZone with " + tz);
			}

			return result;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	protected boolean updateTimeZoneOnUser(UserEntity user, String selectedTimeZoneCd) {
		// [[
		boolean result = false;
		Object[][] params = {{"user", user},
							 {"selectedTimeZoneCd", selectedTimeZoneCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateTimeZoneOnUser", params);
		}
		// ]]
		try {
			String currentTimeZoneCd = user.getTimezone() != null ? user.getTimezone().getTimezoneCd() : Constant.INVALID_OBJECT_ID_STR;
			boolean updateTimezone = !StringUtils.areEqual(currentTimeZoneCd, selectedTimeZoneCd);

			if (updateTimezone) {
				TimezoneBsEntity tzEntity = getServiceLocator()
						.getRefDataGenericsService()
						.findBsEntity(TimezoneBsEntity.class, selectedTimeZoneCd);

				user.setTimezone(tzEntity);
			}

			return result = updateTimezone;
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
