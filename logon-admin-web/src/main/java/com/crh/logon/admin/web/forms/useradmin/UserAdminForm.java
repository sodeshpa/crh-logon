/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.useradmin;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.interfaces.DispokarteService;
import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.business.services.interfaces.UserAdminService;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.admin.RoleEntity;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.PersonEntity;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.refdata.TextTxEntity;
import com.crh.logon.persistence.entities.refdata.UserTypeTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapVendorEntity;
import com.crh.logon.persistence.entities.views.CarrierVendorEntity;
import com.crh.logon.utils.AuthUtils;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.NumberUtils;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.BoxListEntity;
import com.crh.logon.web.utils.ConfigBeanProvider;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.ServiceLocatorProvider;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_UserAdminForm"
 */
public class UserAdminForm extends ActionForm {
    private static final long serialVersionUID = -8910104051337644556L;

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(UserAdminForm.class);

    public static final String formName = "admin_UserAdminForm";
    
    public static final String USER_DETAIL_PANEL_NAME = "userDetailsPanel";
    public static final String EXTERNAL_USER_DETAIL_PANEL_NAME = "userExternalAccessPanel";
    public static final String TEXT_KEY_HELP_INFO = "help.useradmin.authorization_dep";

    public static final int RESTR_TYPE_SOLDTO = 0;
    public static final int RESTR_TYPE_SHIPTO = 1;
    public static final int RESTR_TYPE_PLANT = 2;
    public static final int RESTR_TYPE_SHIPPINGPOINT = 3;
    public static final int RESTR_TYPE_POOL = 4;
    public static final int RESTR_TYPE_CARRIER = 5;
    public static final int RESTR_TYPE_SAREA = 6;
    public static final int RESTR_TYPE_SGROUP = 7;
    public static final int RESTR_TYPE_ROLE = 8;
    public static final int RESTR_TYPE_COMPANYCODE = 9;
    public static final int RESTR_TYPE_PURCHASINGORG = 10;
    
    public static final int IMG_TYPE_SIGNATURE = 0;
    public static final int IMG_TYPE_PHOTOIMAGE = 1;
    
    public static final String SEARCH_STATE_WITHOUT_FILTER = "0";
    public static final String SEARCH_STATE_WITH_FILTER = "1";
    
    private Locale locale;
    private Long functionToCheck;
    private boolean userExternal;
    private Long functionToAuthorize;
    
    private String searchState;
    
    private String inputAction="";
    
    private UserEntity userEntity;
    private CarrierVendorEntity carrierVendor;
    private UserEntity userLoggedIn;
    private DriverEntity driverEntity;
    private List allowedUserTypeList;
    private List allUserTypeList;
    private List userLanguageList;
    
    //search fields
    private String searchFirstNameOrName;
    private String searchHolspaceOrExtLogin;
    private String searchCustomerOrCarrier;
    private boolean searchStatus = true;
    private boolean driverFlag;
    private String searchSelRoleId;
    private String searchShipTo;
    
    //input fields
    private boolean activeFlag;
    private String customerName;
    private String vendorName;
    
    private String certifiedFromDat;
    private String certifiedToDat;
    
    private String pdaPinValidToDatStr;
    private String pdaPinValidToTimeStr;
    private String pdaUserNoStr;
    private String pdaUserNoSuggestion;
    
    //external access input fields
    private String extLogin;
    
    //lists
	private List shiptoList;
	private List roleList;
	
	//selections
	private Map selectableRoleMap;
	private Map selectedRoleMap;
	private Map grantableRoleMap;
	
	private Map selectableShipToMap;
	private Map selectedShipToMap;
	
	//this kind of naming is necessary to simplify jsp code. Because for example shipto selection can be in two different contexts in the user administration. In case of managing
	//customer user types, there is just one simple shipto selection panel, without customer selection. therefore stans selectable/selectedShipToMap. In the other case, where 
	//a combined customer-shipto selection exists, we use selectable/selectedSoldToShipTo... maps. Hovever it is redundant in the form, jsp code ges much simplier, since
	//we only need to check the existance of the certain maps on the jsp side
	
	private Map selectableSoldToShipToShipToMap;
	private Map selectedSoldToShipToShipToMap;
	private Map selectableSoldToShipToSoldToMap;
	private Map selectedSoldToShipToSoldToMap;
	
	private Map selectablePoolMap;
	private Map selectedPoolMap;
	private Map selectableCarrierMap;
	private Map selectedCarrierMap;
	
	private Map selectablePlantShippingPointPlantMap;
	private Map selectedPlantShippingPointPlantMap;
	private Map selectablePlantShippingPointShippingPointMap;
	private Map selectedPlantShippingPointShippingPointMap;
	
	private Map selectableCompCodeSareaCompCodeMap;
	private Map selectedCompCodeSareaCompCodeMap;
	private Map selectableCompCodeSareaSareaMap;
	private Map selectedCompCodeSareaSareaMap;
	
	private Map selectableSgroupMap;
	private Map selectedSgroupMap;
	private Map selectablePurchOrgMap;
	private Map selectedPurchOrgMap;
	
	private Long [] selectedRoleIds;
	private String [] selectedShipToIds;
	private Long [] selectedPoolIds;
	private Long [] selectedCarrierIds;
	private String [] selectedSoldToIds;
	private String [] selectedPlantIds;
	private String [] selectedShippingPointIds;
	private String [] selectedCompCodeIds;
	private String [] selectedSareaIds;
	private String [] selectedSgroupIds;
	private String [] selectedPurchOrgIds;
	
    private boolean updateUserStatusValue;
    private String userId;
    
    //additional
    private int restrictionType;
    
    private byte[] signature;
    private String signatureContentType;
    private String signatureFilename;
    private byte[] photoImage;
    private String photoImageContentType;
    private String photoImageFilename;

	private List<TimeZone> timeZoneList;
	private TimeZone selectedTimeZone;

	/**
     * Default constructor.
     */
    public UserAdminForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "UserAdminForm");
		}
		// ]]
		try {
	        init();
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
     * Initializets the form. This has the effect of clearing all the input
     * fields on the web form.
     */
    private void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			setUserExternal(false);
			setFunctionToAuthorize(null);
			initUser(null, null, null); 
			initMaps();
			extLogin = null;
			pdaUserNoSuggestion = null;
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
     * Clears all the input fields on the web form, discards any existing
     * search results, and resets any existing pages. Called in response to
     * clicking the reset button on the web form.
     */
    public void clear() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "clear");
		}
		// ]]
		try {
	       init();
	       clearSearchFields();
	       clearDetailFields();
	       this.inputAction = null;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
    
    public void clearSearchFields() {
	       this.searchState = null;
	       this.searchFirstNameOrName = null;
	       this.searchHolspaceOrExtLogin = null;
	       this.searchCustomerOrCarrier = null;
	       this.searchSelRoleId = null;
	       this.searchStatus = false;
    }
    
    public void clearDetailFields() {
    	this.customerName = null;
    	pdaUserNoSuggestion = null;
    }
    
    public void initMaps() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initMaps");
		}
		// ]]
		try {
			this.selectedRoleIds = null;
			this.selectedShipToIds = null;
			this.selectedPoolIds = null;
			this.selectedCarrierIds = null;
			this.selectedSoldToIds = null;
			this.selectedPlantIds = null;
			this.selectedShippingPointIds = null;
			this.selectedSareaIds = null;
			this.selectedSgroupIds = null;
			this.selectedCompCodeIds = null;
			this.selectedPurchOrgIds = null;
			
			this.selectableSoldToShipToSoldToMap = null;
			this.selectedSoldToShipToSoldToMap = new LinkedHashMap();
			this.selectableSoldToShipToShipToMap = null;
			this.selectedSoldToShipToShipToMap	= new LinkedHashMap();
			this.selectableShipToMap = null;
			this.selectedShipToMap = new LinkedHashMap();
			this.selectableRoleMap = null;
			this.selectedRoleMap = new LinkedHashMap();
			this.selectablePoolMap = null;
			this.selectedPoolMap = new LinkedHashMap();
			this.selectableCarrierMap = null;
			this.selectedCarrierMap = new LinkedHashMap();
			this.selectablePlantShippingPointPlantMap = null;
			this.selectedPlantShippingPointPlantMap = new LinkedHashMap();
			this.selectablePlantShippingPointShippingPointMap = null;
			this.selectedPlantShippingPointShippingPointMap = new LinkedHashMap();
			this.selectableCompCodeSareaCompCodeMap = null;
			this.selectedCompCodeSareaCompCodeMap = new LinkedHashMap();
			this.selectableCompCodeSareaSareaMap = null;
			this.selectedCompCodeSareaSareaMap = new LinkedHashMap();
			this.selectableSgroupMap = null;
			this.selectedSgroupMap = new LinkedHashMap();
			this.selectablePurchOrgMap = null;
			this.selectedPurchOrgMap = new LinkedHashMap();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
    
    public void initUser(UserEntity user, List driverEntityList, CarrierVendorEntity carrier) {
		// [[
		Object[][] params = {{"user", user},
							{"driverEntityList", driverEntityList},
							{"carrier", carrier}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initUser", params);
		}
		// ]]
		try {
	    	signature = null;
	    	photoImage = null;
	    	signatureContentType = null;
	        signatureFilename = null;
	        photoImage = null;
	        photoImageContentType = null;
	        photoImageFilename = null;
	        pdaPinValidToDatStr = null;
	        pdaPinValidToTimeStr = null;
	        driverFlag = false;
	        driverEntity = null;
	        certifiedFromDat = "";
            certifiedToDat = "";
			extLogin = "";
			
			userEntity = user;

			if (userEntity != null) {
	            if (userEntity.getSapBp() == null) {
	                userEntity.setSapBp(new SapBpEntity());
	            }
	            if (userEntity.getSapVendor() == null) {
	                userEntity.setSapVendor(new SapVendorEntity());
	            }
				if (userEntity.getTimezone() != null) {
					setSelectedTimeZoneCd(userEntity.getTimezone().getTimezoneCd());
				}
				else {
					setSelectedTimeZoneCd(ConfigBeanProvider.getConfigBean().getAppDefaultTimezone());
				}

	            setPdaPinValidTo(userEntity.getPdaPinValidToDat());
			
	            pdaUserNoStr = userEntity.getPdaUserNo() == null ? "" : userEntity.getPdaUserNo().toString();
			}
			if (driverEntityList != null && driverEntityList.size() == 1) {
			    DriverEntity driver = (DriverEntity) driverEntityList.get(0);
			    setDriverEntity(driver);
                driverFlag = driverEntity.getActiveFlag();
			}
			carrierVendor = carrier;
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
     * Override struts method.
     * Note: this is not associated with or invoked by the reset button in the
     * web form, but rather is called on every request before the struts action
     * method gets control and, more importantly, before the request parameters
     * are automatically copied into the form. The primary use is to reset
     * checkboxes and struts multiboxes and the like for which the browser only
     * sends the form parameters when they are set.
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
		// [[
		Object[][] params = {{"mapping", mapping},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "reset", params);
		}
		// ]]
		try {
				//there should not by any request parameter related to the passwords/pins -> handled with the popups
				if (request.getParameter("userEntity.extLoginPwd") != null) {
					throw new LogonException("Tricky external password change detected!", com.crh.logon.utils.Reason.RES_ERROR_SECURITY_NOT_ALLOWED);
				}
				if (request.getParameter("userEntity.pdaPin") != null) {
					throw new LogonException("Tricky pda pin change detected!", com.crh.logon.utils.Reason.RES_ERROR_SECURITY_NOT_ALLOWED);
				}

				this.searchState = null;
				this.driverFlag = false;
				
				//setting flag to empty
				if(userEntity != null){
					getUserEntity().getPerson().setActiveFlag(Boolean.FALSE);
					getUserEntity().setExtLoginActiveFlag(Boolean.FALSE);
					getUserEntity().setSignatureValidFlag(Boolean.FALSE);
					getUserEntity().setSendDailyReportsFlag(Boolean.FALSE);
				}
				if (request.getParameter("cancelUser") == null) {
					this.searchStatus = false;
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
     * Override struts method.
     * Perform form-specific validation of input parameters.
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		// [[
		ActionErrors result = null;
		Object[][] params = {{"mapping", mapping},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validate", params);
		}
		// ]]
		try {
			ActionErrors errors = super.validate(mapping, request);
			
	        if (errors == null) {
	            errors = new ActionErrors();
	        }
	        if ("removeCustomer".equals(request.getParameter("formAction"))) {
				String soldToIdToRemove = (String) request.getParameter("customerId");
				if (StringUtils.empty(soldToIdToRemove)) {
	        		errors.add(ActionMessages.GLOBAL_MESSAGE,
	        				new ActionMessage("useradmin.errors.soldtoremoval.nosoldto"));
				}
				else if (userEntity.getSapBp() != null) {
					String soldToItselfSapId = userEntity.getSapBp().getSapId();
					if(soldToIdToRemove.equals(soldToItselfSapId)) {
		        		errors.add(ActionMessages.GLOBAL_MESSAGE,
		        				new ActionMessage("useradmin.errors.soldtoremoval.self"));
					}
				}
	        }
	        else if ("removeCarrier".equals(request.getParameter("formAction"))) {
	        	String carrierIdToRemove = (String) request.getParameter("carrierId");
				if (StringUtils.empty(carrierIdToRemove)) {
	        		errors.add(ActionMessages.GLOBAL_MESSAGE,
	        				new ActionMessage("useradmin.errors.carrierremoval.nocarrier"));
				}
				else if (userEntity.getSapVendor() != null) {
					Long sapVendorItselfSapVendorId = userEntity.getSapVendor().getSapVendorId();
					if(Long.valueOf(carrierIdToRemove).equals(sapVendorItselfSapVendorId)) {
		        		errors.add(ActionMessages.GLOBAL_MESSAGE,
		        				new ActionMessage("useradmin.errors.carrierremoval.self"));
					}
				}
	        }
	        else if ("saveUserAdding".equals(inputAction) ||"saveUserModification".equals(inputAction)) {
	        	//checking sapbp/sapvendor id set on user (customer/vendor types)
	        	if (StringUtils.empty(userEntity.getPerson().getFirstname())) {
	        		errors.add(ActionMessages.GLOBAL_MESSAGE,
	        				new ActionMessage("useradmin.errors.fieldEmpty", new String [] {"First name"}));
	        	}
	        	else if (userEntity.getPerson().getFirstname().length() > PersonEntity.DBSIZE_FIRSTNAME)
	        	{
	        		errors.add(ActionMessages.GLOBAL_MESSAGE,
	        				new ActionMessage("useradmin.errors.fieldInputTooLong", new String [] {"First name", Long.toString(PersonEntity.DBSIZE_FIRSTNAME)}));
	        	}
	        	if (StringUtils.empty(userEntity.getPerson().getName())) {
	        		errors.add(ActionMessages.GLOBAL_MESSAGE,
	        				new ActionMessage("useradmin.errors.fieldEmpty", new String [] {"Name"}));
	        	}
	        	else if (userEntity.getPerson().getName().length() > PersonEntity.DBSIZE_NAME) {
	        		errors.add(ActionMessages.GLOBAL_MESSAGE,
	        				new ActionMessage("useradmin.errors.fieldInputTooLong", new String [] {"Name", Long.toString(PersonEntity.DBSIZE_NAME)}));
	        	}
	        	if(StringUtils.empty(userEntity.getPerson().getEmail())) {
	        		errors.add(ActionMessages.GLOBAL_MESSAGE,
	        				new ActionMessage("useradmin.errors.fieldEmpty", new String [] {"E-mail"}));
	        	}
	        	else if (userEntity.getPerson().getEmail().length() > PersonEntity.DBSIZE_EMAIL) {
	        		errors.add(ActionMessages.GLOBAL_MESSAGE,
	        				new ActionMessage("useradmin.errors.fieldInputTooLong", new String [] {"E-mail", Long.toString(PersonEntity.DBSIZE_EMAIL)}));
	        	}
	        	if (userEntity.getPerson().getPhoneNr() != null && userEntity.getPerson().getPhoneNr().length() > PersonEntity.DBSIZE_PHONE_NR) {
	        		errors.add(ActionMessages.GLOBAL_MESSAGE,
	        				new ActionMessage("useradmin.errors.fieldInputTooLong", new String [] {"Phone Nr.", Long.toString(PersonEntity.DBSIZE_PHONE_NR)}));
	        	}
				if (!StringUtils.empty(userEntity.getPerson().getMobileNr())) {
					if (userEntity.getPerson().getMobileNr().length() > PersonEntity.DBSIZE_MOBILE_NR) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
								"useradmin.errors.fieldInputTooLong", new String[] { "Mobile Nr.",
										Long.toString(PersonEntity.DBSIZE_MOBILE_NR) }));
					}
					else if (!NumberUtils.validateMobileNr(userEntity.getPerson().getMobileNr())) { // validate mobile number (needed for SMS sending)
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
								"useradmin.errors.mobileNrNotValid"));						
					}
				}
	        	if (userEntity.getExtLoginActiveFlag() == Boolean.TRUE) {
	        		if (StringUtils.empty(userEntity.getExtLoginPwd())) {
	        			errors.add(ActionMessages.GLOBAL_MESSAGE,
		        				new ActionMessage("useradmin.errors.fieldEmpty", new String [] {"External Login Password"}));
	        		}
	        	}
	        	if (! StringUtils.empty(pdaUserNoStr) && pdaUserNoStr.length() > UserEntity.DBSIZE_PDA_USER_NO) {
	        		errors.add(ActionMessages.GLOBAL_MESSAGE,
	        				new ActionMessage("useradmin.errors.fieldInputTooLong", new String [] {"PDA User Number.", Long.toString(UserEntity.DBSIZE_PDA_USER_NO)}));
	        	}
	        	if (! StringUtils.empty(userEntity.getPdaPin()) && userEntity.getPdaPin().length() > UserEntity.DBSIZE_PDA_PIN) {
	        		errors.add(ActionMessages.GLOBAL_MESSAGE,
	        				new ActionMessage("useradmin.errors.fieldInputTooLong", new String [] {"PDA Pin.", Long.toString(UserEntity.DBSIZE_PDA_PIN)}));
	        	}
	        	if (userEntity.getPdaUserRfidobject() != null && userEntity.getPdaUserRfidobject().getRfidobjectId() != null) {
	        		if ( userEntity.getPdaUserRfidobject().getRfidobjectId().toString().length() > RfidobjectEntity.DBSIZE_RFIDOBJECT_ID) {
	        			errors.add(ActionMessages.GLOBAL_MESSAGE,
	        					new ActionMessage("useradmin.errors.fieldInputTooLong", new String [] {"PDA RFID Object Id.", Long.toString(RfidobjectEntity.DBSIZE_RFIDOBJECT_ID)}));
	        		}
	        	}
	        	if (driverEntity != null) {
	        		if (driverEntity.getDriverComment() != null && driverEntity.getDriverComment().length() > DriverEntity.DBSIZE_DRIVER_COMMENT) {
	        			errors.add(ActionMessages.GLOBAL_MESSAGE,
	        					new ActionMessage("useradmin.errors.fieldInputTooLong", new String [] {"Driver Comment.", Long.toString(DriverEntity.DBSIZE_DRIVER_COMMENT)}));
	        		}
	        	}

	        	validateAssignedRestr(errors, request);
	        	
	        	// Validate user with type 'vendor' and 'costumer' the required fields and restrictions are set
	        	validateBpAndVendor(errors, request);
	        }
	        if ("modifyUser".equals(inputAction)){
	        	if(StringUtils.empty(userId)){
	        		errors.add(ActionMessages.GLOBAL_MESSAGE,
							new ActionMessage("useradmin.errors.fieldEmpty", new String [] {"user Id"}));
	        	}
	        	else {
	        		try{
	        			Long.parseLong(userId);
	        		}
	        		catch (NumberFormatException nfe) {
	        			errors.add(ActionMessages.GLOBAL_MESSAGE,
	        					new ActionMessage("useradmin.errors.fieldEmpty", new String [] {"user Id not parsable"}));
	        		}

	        		validateModifyUserAuth(errors, request);
	        	}
	        }
	        if("saveUserModificationSelf".equals(inputAction)) {
				if (!StringUtils.empty(userEntity.getPerson().getMobileNr())
						&& !NumberUtils.validateMobileNr(userEntity.getPerson().getMobileNr())) { // validate mobile number (needed for SMS sending)
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
							"useradmin.errors.mobileNrNotValid"));						
				}
	        }

	        return result = errors;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }
    
    private void validateModifyUserAuth(ActionErrors errors, HttpServletRequest request) {
		// [[
		Object[][] params = {{"errors", errors},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validateModifyUserAuth", params);
		}
		// ]]
		try {
			if (("storeExternalAccess".equals(request.getParameter("formAction")))
					|| ("cancelExternalAccess".equals(request.getParameter("formAction")))) {
				Authorization.authorize(Authorization.PUBLIC_FUNCTION);
			}
			else {
				Authorization.authorize(Authorization.USER_ADMIN_LOGON);
			}
			ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator();
			UserAdminService userAdminService = serviceLocator.getUserAdminService();
			
			if (!userAdminService.canModifyUser(Long.valueOf(this.userId))) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"useradmin.errors.cannot.modify.user"));
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
    
    private void validateAssignedRestr(ActionErrors errors, HttpServletRequest request) {
    	
    	/*
    	 * Validation rules:
    	 *  1.) If the logged in user has restriction for a field the create/modified user must also have at least one (cannot be empty)
    	 *  2.) If the restriction depends on an other (ex: the shippingpoint on plant), than if
    	 *  the master (ex: plant) has restriction, the sub (ex: shippingpoint) cannot be empty.
    	 */
    	
    	Authorization.authorize(Authorization.PUBLIC_FUNCTION);
    	
    	Long userId = AuthUtils.getUserId(); // the logged in user
    	UserEntity user = getUserEntity();
		Long modifiedUserId = user == null ? null : user.getUserId();
		
		if (userId.equals(modifiedUserId)) {
			return; // should never happen, but make sure that nothing happens even if requested
		}
		
		// prepare service
		ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator();
		UserAdminService userAdminService = serviceLocator.getUserAdminService();
		
		// fetch logged in user assigment's
		Map plant = userAdminService.fetchAssignedPlantList(userId, null, null, false);
		Map shippingPoint = userAdminService.fetchAssignedShippingPointList(userId, null, false, "%s %s");
		Map poolTxMap = userAdminService.fetchPoolsAssignedToUser(userId, false, locale);
		Map carrierMap = userAdminService.fetchAssignedCarrierList(userId, false, "%s %s");
		Map soldToMap = userAdminService.fetchAssignedSoldtoList(userId, false);
		Map shipToMap = userAdminService.fetchAssignedShiptoList(userId, null, false, "%s %s");
		Map compCodeMap = userAdminService.fetchAssignedCompCodeList(userId, false);
		Map sareaMap = userAdminService.fetchAssignedSareaList(userId, null, false);
		Map sgroupMap = userAdminService.fetchAssignedSgroupList(userId, false, "%s %s");
		Map purchOrgMap = userAdminService.fetchAssignedPurchOrgList(userId, false);
		
		Set<String> failedAssignmentRestrSet = new HashSet<String>();
		Map<String, BoxListEntity> selectedPlantMap = getSelectedPlantShippingPointPlantMap();
		if (plant != null && !plant.isEmpty()) {
			if (selectedPlantMap == null || selectedPlantMap.isEmpty()) {
				
				String msg = MessageUtils.getMessage(request, "useradmin.texte.assigned.plants");
				failedAssignmentRestrSet.add(msg);
			}
		}
		
		Map<String, BoxListEntity> selectedShippingPointMap = getSelectedPlantShippingPointShippingPointMap();		
		if (shippingPoint != null && !shippingPoint.isEmpty()) {
			if (selectedShippingPointMap == null || selectedShippingPointMap.isEmpty()) {
				
				String msg = MessageUtils.getMessage(request, "useradmin.texte.assigned.shippingpoints");
				failedAssignmentRestrSet.add(msg);
			}		
		}
				
		Map<String, BoxListEntity> selectedPoolMap = getSelectedPoolMap();		
		if (poolTxMap != null && !poolTxMap.isEmpty()) {
			if (selectedPoolMap == null || selectedPoolMap.isEmpty()) {
				
				String msg = MessageUtils.getMessage(request, "useradmin.texte.assigned.pools");
				failedAssignmentRestrSet.add(msg);
			}		
		}
		
		Map<String, BoxListEntity> selectedCarrierMap = getSelectedCarrierMap();		
		if (carrierMap != null && !carrierMap.isEmpty()) {
			if (selectedCarrierMap == null || selectedCarrierMap.isEmpty()) {
				
				String msg = MessageUtils.getMessage(request, "useradmin.texte.assigned.carrier");
				failedAssignmentRestrSet.add(msg);
			}		
		}
		
		Map<String, BoxListEntity> selectedSoldToMap = getSelectedSoldToShipToSoldToMap();		
		if (soldToMap != null && !soldToMap.isEmpty()) {
			if (selectedSoldToMap == null || selectedSoldToMap.isEmpty()) {
				
				String msg = MessageUtils.getMessage(request, "useradmin.texte.assigned.customers");
				failedAssignmentRestrSet.add(msg);
			}				
		}
		
		Map<String, BoxListEntity> selectedShiptoToMap = getSelectedSoldToShipToShipToMap();		
		if (shipToMap != null && !shipToMap.isEmpty()) {
			if (selectedShiptoToMap == null || selectedShiptoToMap.isEmpty()) {
				
				String msg = MessageUtils.getMessage(request, "useradmin.texte.assigned.shipto");
				failedAssignmentRestrSet.add(msg);
			}				
		}
		
		Map<String, BoxListEntity> selectedCompCodeMap = getSelectedCompCodeSareaCompCodeMap();	
		if (compCodeMap != null && !compCodeMap.isEmpty()) {
			if (selectedCompCodeMap == null || selectedCompCodeMap.isEmpty()) {
				
				String msg = MessageUtils.getMessage(request, "useradmin.texte.assigned.compcode");
				failedAssignmentRestrSet.add(msg);
			}				
		}
		
		Map<String, BoxListEntity> selectedSareaMap = getSelectedCompCodeSareaSareaMap();		
		if (sareaMap != null && !sareaMap.isEmpty()) {
			if (selectedSareaMap == null || selectedSareaMap.isEmpty()) {
				
				String msg = MessageUtils.getMessage(request, "useradmin.texte.assigned.sareas");
				failedAssignmentRestrSet.add(msg);
			}				
		}
				
		Map<String, BoxListEntity> selectedSgroupMap = getSelectedSgroupMap();		
		if (sgroupMap != null && !sgroupMap.isEmpty()) {
			if (selectedSgroupMap == null || selectedSgroupMap.isEmpty()) {
				
				String msg = MessageUtils.getMessage(request, "useradmin.texte.assigned.sgroups");
				failedAssignmentRestrSet.add(msg);
			}		
		}
		
		Map<String, BoxListEntity> selectedPurchOrgMap = getSelectedPurchOrgMap();		
		if (purchOrgMap != null && !purchOrgMap.isEmpty()) {
			if (selectedPurchOrgMap == null || selectedPurchOrgMap.isEmpty()) {
				
				String msg = MessageUtils.getMessage(request, "useradmin.texte.assigned.purchorg");
				failedAssignmentRestrSet.add(msg);
			}		
		}
		
		if (failedAssignmentRestrSet.isEmpty()) {
			return; // no error found
		}
		
		errors.add(ActionMessages.GLOBAL_MESSAGE,
				new ActionMessage("useradmin.errors.assigmentListEmpty.title"));
		

		for (Iterator<String> iter = failedAssignmentRestrSet.iterator(); iter.hasNext();) {
			errors.add(ActionMessages.GLOBAL_MESSAGE,
					new ActionMessage("useradmin.errors.assigmentListEmpty", new String [] {iter.next()}));
		}
    }
    
    /**
     * Validate user with type 'vendor' and 'costumer' the required fields and restrictions are set.
     * 
     * @param errors
     * @param request
     * @return
     */
	private void validateBpAndVendor(ActionErrors errors, HttpServletRequest request) {
		// [[
		Object[][] params = {{"errors", errors},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validateBpAndVendor", params);
		}
		// ]]
		try {
			final int userType = userEntity.getUserType().getUserTypeCd().intValue();
			if (Constant.USER_TYPE_CRH == userType) {
				return; // nothing to validate
			}
			
			if (("storeExternalAccess".equals(request.getParameter("formAction")))
					|| ("cancelExternalAccess".equals(request.getParameter("formAction")))) {
				Authorization.authorize(Authorization.PUBLIC_FUNCTION);
			}
			else {
				Authorization.authorize(Authorization.USER_ADMIN_LOGON);
			}
			
			ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator();
			UserAdminService userAdminService = serviceLocator.getUserAdminService();
			DispokarteService dispokarteService = serviceLocator.getDispokarteService();
			
			if (Constant.USER_TYPE_VENDOR == userType || Constant.USER_TYPE_DRIVER == userType) { // validate vendor
				SapVendorEntity sapVendor = userEntity.getSapVendor();
				
				final String vendorSapId = sapVendor == null ? null : sapVendor.getSapId();
				if (!StringUtils.empty(vendorSapId)) { // load the vendor (to check if exists)
					sapVendor = userAdminService.fetchSapVendorBySapId(vendorSapId);
				}
				
				Map selectedCarriersMap = getSelectedCarrierMap();
				if (sapVendor == null || selectedCarriersMap == null || !selectedCarriersMap.containsKey(sapVendor.getSapVendorId())) {
					errors.add(ActionMessages.GLOBAL_MESSAGE,
							new ActionMessage("useradmin.errors.missing.vendorid"));
				}
			}			
			else if (Constant.USER_TYPE_CUSTOMER == userType) { // validate costumer
				SapBpEntity sapBp = userEntity.getSapBp();
				
				final Long bpSapId = sapBp == null ? null : sapBp.getSapBpId();
				
				if (bpSapId != null) {// load the bp_sap (to check if exists)
					sapBp = dispokarteService.getSoldtoSapBpById(bpSapId);
				}
				
				Map selectedSoldtoMap = getSelectedSoldToShipToSoldToMap();
				if (sapBp == null || selectedSoldtoMap == null || !selectedSoldtoMap.containsKey(sapBp.getSapId())) {
					errors.add(ActionMessages.GLOBAL_MESSAGE,
							new ActionMessage("useradmin.errors.missing.bpid"));
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

    public String getAuthorizationHelpInfoText() {
		// [[
		String result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getAuthorizationHelpInfoText");
		}
		// ]]
		try {
			// prepare service
			Authorization.authorize(Authorization.PUBLIC_FUNCTION);
			ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator();
			RefDataGenericsService refDataService = serviceLocator.getRefDataGenericsService();
			
			TextTxEntity entity = refDataService.findTxEntity(TextTxEntity.class, getLocale(), TEXT_KEY_HELP_INFO);
			result = "";
			if (entity != null) {
				result = StringUtils.nvl(entity.getTxt());
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
    
	public String getSearchFirstNameOrName() {
		return searchFirstNameOrName;
	}

	public void setSearchFirstNameOrName(String searchFirstNameOrName) {
		this.searchFirstNameOrName = searchFirstNameOrName;
	}

	public String getSearchHolspaceOrExtLogin() {
		return searchHolspaceOrExtLogin;
	}

	public void setSearchHolspaceOrExtLogin(String searchHolspaceOrExtLogin) {
		this.searchHolspaceOrExtLogin = searchHolspaceOrExtLogin;
	}

	public String getSearchSelRoleId() {
		return searchSelRoleId;
	}

	public void setSearchSelRoleId(String searchSelRoleId) {
		this.searchSelRoleId = searchSelRoleId;
	}

	public String getSearchShipTo() {
		return searchShipTo;
	}

	public void setSearchShipTo(String searchShipTo) {
		this.searchShipTo = searchShipTo;
	}

	public String getInputAction() {
		return inputAction;
	}

	public void setInputAction(String inputAction) {
		this.inputAction = inputAction;
	}

	public boolean isUpdateUserStatusValue() {
		return updateUserStatusValue;
	}

	public void setUpdateUserStatusValue(boolean updateUserStatusValue) {
		this.updateUserStatusValue = updateUserStatusValue;
	}

	public boolean isSearchStatus() {
		return searchStatus;
	}

	public void setSearchStatus(boolean searchStatus) {
		this.searchStatus = searchStatus;
	}

	public String getSearchCustomerOrCarrier() {
		return searchCustomerOrCarrier;
	}

	public void setSearchCustomerOrCarrier(String searchCustomerOrCarrier) {
		this.searchCustomerOrCarrier = searchCustomerOrCarrier;
	}

	public UserEntity getUserEntity() {
		return userEntity;
	}
	
	public void setUserEntity(UserEntity userEntity) {
		this.userEntity = userEntity;
	}
	
	public List getShiptoList() {
		return shiptoList;
	}

	public void setShiptoList(List shiptoList) {
		this.shiptoList = shiptoList;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String[] getSelectedShipToIds() {
		return selectedShipToIds;
	}

	public void setSelectedShipToIds(String[] selectedShipToIds) {
		this.selectedShipToIds = selectedShipToIds;
	}

	public Map getSelectableShipToMap() {
		return selectableShipToMap;
	}

	public void setSelectableShipToMap(Map selectableShipToMap) {
		this.selectableShipToMap = selectableShipToMap;
	}

	public Map getSelectedShipToMap() {
		return selectedShipToMap;
	}

	public void setSelectedShipToMap(Map selectedShipToMap) {
		this.selectedShipToMap = selectedShipToMap;
	}

	public boolean isActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(boolean activeFlag) {
		this.activeFlag = activeFlag;
	}

	public List getAllowedUserTypeList() {
		return allowedUserTypeList;
	}

	public void setAllowedUserTypeList(List userTypeList) {
		this.allowedUserTypeList = userTypeList;
	}
	
    public Object getSelectedRoleMapEntry(String key) {
		// [[
		Object result = null;
		Object[][] params = {{"key", key}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getSelectedRoleMapEntry", params);
		}
		// ]]
		try {
			boolean booleanResult = false;
			if(selectedRoleMap.get(Long.valueOf(key)) != null){
				booleanResult = true;
			}
			return result = String.valueOf(booleanResult);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }

    public void setSelectedRoleMapEntry(String key, Object value) {
		// [[
		Object[][] params = {{"key", key},
							{"value", value}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setSelectedRoleMapEntry", params);
		}
		// ]]
		try {
			RoleEntity role = (RoleEntity) this.selectableRoleMap.get(Long.valueOf(key));
			this.selectedRoleMap.put(role.getRoleId(), role);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
    
	public Map getSelectedRoleMap() {
		return selectedRoleMap;
	}

	public void setSelectedRoleMap(Map selectedRoleMap) {
		this.selectedRoleMap = selectedRoleMap;
	}

	public Map getSelectableRoleMap() {
		return selectableRoleMap;
	}

	public void setSelectableRoleMap(Map selectableRoleMap) {
		this.selectableRoleMap = selectableRoleMap;
	}

	public List getRoleList() {
		return roleList;
	}

	public void setRoleList(List roleList) {
		this.roleList = roleList;
	}

	public String getSearchState() {
		return searchState;
	}

	public void setSearchState(String searchState) {
		this.searchState = searchState;
	}

	public String getSearchStateWithFilter() {
		return SEARCH_STATE_WITH_FILTER;
	}

	public String getSearchStateWithOutFilter() {
		return SEARCH_STATE_WITHOUT_FILTER;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getUserType() {
		if (this.userEntity.getUserType() != null){
			if (this.userEntity.getUserType().getUserTypeCd() != null) {
				for (Iterator iterator = this.allUserTypeList.iterator(); iterator.hasNext();) {
					UserTypeTxEntity type = (UserTypeTxEntity) iterator.next();
					
					if(type.getUserTypeTxPK().getUserTypeBs().getUserTypeCd().equals(this.userEntity.getUserType().getUserTypeCd())){
						return type.getTxt();
					}
				}
			}
		}
		return "";
		
	}

	public Map getSelectedPoolMap() {
		return selectedPoolMap;
	}

	public void setSelectedPoolMap(Map selectedPoolMap) {
		this.selectedPoolMap = selectedPoolMap;
	}

	public Map getSelectedCarrierMap() {
		return selectedCarrierMap;
	}

	public void setSelectedCarrierMap(Map selectedCarrierMap) {
		this.selectedCarrierMap = selectedCarrierMap;
	}

	public Map getSelectablePoolMap() {
		return selectablePoolMap;
	}

	public void setSelectablePoolMap(Map selectablePoolMap) {
		this.selectablePoolMap = selectablePoolMap;
	}

	public Map getSelectableCarrierMap() {
		return selectableCarrierMap;
	}

	public void setSelectableCarrierMap(Map selectableCarrierMap) {
		this.selectableCarrierMap = selectableCarrierMap;
	}

	public Long[] getSelectedPoolIds() {
		return selectedPoolIds;
	}

	public void setSelectedPoolIds(Long[] selectedPoolIds) {
		this.selectedPoolIds = selectedPoolIds;
	}

	public Long[] getSelectedCarrierIds() {
		return selectedCarrierIds;
	}

	public void setSelectedCarrierIds(Long[] selectedCarrierIds) {
		this.selectedCarrierIds = selectedCarrierIds;
	}

	public String[] getSelectedSoldToIds() {
		return selectedSoldToIds;
	}

	public void setSelectedSoldToIds(String[] selectedSoldToIds) {
		this.selectedSoldToIds = selectedSoldToIds;
	}

	public DriverEntity getDriverEntity() {
		return driverEntity;
	}

	public void setDriverEntity(DriverEntity driverEntity) {
	    this.driverEntity = driverEntity;
	    if (driverEntity != null) {
			Date certFromDat =  driverEntity.getCertifiedFromDat();
			
			if (certFromDat != null) {
				certifiedFromDat = this.selectedTimeZone.fmtAsDayMonthYear(certFromDat);
			}
			
			Date certToDat =  driverEntity.getCertifiedToDat();
			if (certToDat  != null) {
				certifiedToDat = this.selectedTimeZone.fmtAsDayMonthYear(certToDat);
			}
		}
		else {
			certifiedFromDat = "";
			certifiedToDat = "";
		}
	}

	public String getCertifiedFromDat() {
		return certifiedFromDat;
	}

	public void setCertifiedFromDat(String certifiedFromDat) {
		this.certifiedFromDat = certifiedFromDat;
	}

	public String getCertifiedToDat() {
		return certifiedToDat;
	}

	public void setCertifiedToDat(String certifiedToDat) {
		this.certifiedToDat = certifiedToDat;
	}
	
	public Date getCertifiedFrom() throws ParseException {
		return this.selectedTimeZone.parseDayMonthYear(certifiedFromDat);
	}
	
	public Date getCertifiedTo() throws ParseException {
		return this.selectedTimeZone.parseDayMonthYear(certifiedToDat);
	}
	
	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String[] getSelectedPlantIds() {
		return selectedPlantIds;
	}

	public void setSelectedPlantIds(String[] selectedPlantIds) {
		this.selectedPlantIds = selectedPlantIds;
	}

	public String[] getSelectedShippingPointIds() {
		return selectedShippingPointIds;
	}

	public void setSelectedShippingPointIds(String[] selectedShippingPointIds) {
		this.selectedShippingPointIds = selectedShippingPointIds;
	}

	public Map getSelectableSoldToShipToShipToMap() {
		return selectableSoldToShipToShipToMap;
	}

	public void setSelectableSoldToShipToShipToMap(Map selectableSoldToShipToShipToMap) {
		this.selectableSoldToShipToShipToMap = selectableSoldToShipToShipToMap;
	}

	public Map getSelectedSoldToShipToShipToMap() {
		return selectedSoldToShipToShipToMap;
	}

	public void setSelectedSoldToShipToShipToMap(Map selectedSoldToShipToShipToMap) {
		this.selectedSoldToShipToShipToMap = selectedSoldToShipToShipToMap;
	}

	public Map getSelectableSoldToShipToSoldToMap() {
		return selectableSoldToShipToSoldToMap;
	}

	public void setSelectableSoldToShipToSoldToMap(Map selectableSoldToShipToSoldToMap) {
		this.selectableSoldToShipToSoldToMap = selectableSoldToShipToSoldToMap;
	}

	public Map getSelectedSoldToShipToSoldToMap() {
		return selectedSoldToShipToSoldToMap;
	}

	public void setSelectedSoldToShipToSoldToMap(Map selectedSoldToShipToSoldToMap) {
		this.selectedSoldToShipToSoldToMap = selectedSoldToShipToSoldToMap;
	}

	public Map getSelectablePlantShippingPointPlantMap() {
		return selectablePlantShippingPointPlantMap;
	}

	public void setSelectablePlantShippingPointPlantMap(Map selectablePlantShippingPointPlantMap) {
		this.selectablePlantShippingPointPlantMap = selectablePlantShippingPointPlantMap;
	}

	public Map getSelectedPlantShippingPointPlantMap() {
		return selectedPlantShippingPointPlantMap;
	}

	public void setSelectedPlantShippingPointPlantMap(Map selectedPlantShippingPointPlantMap) {
		this.selectedPlantShippingPointPlantMap = selectedPlantShippingPointPlantMap;
	}

	public Map getSelectablePlantShippingPointShippingPointMap() {
		return selectablePlantShippingPointShippingPointMap;
	}

	public void setSelectablePlantShippingPointShippingPointMap(
			Map selectablePlantShippingPointShippingPointMap) {
		this.selectablePlantShippingPointShippingPointMap = selectablePlantShippingPointShippingPointMap;
	}

	public Map getSelectedPlantShippingPointShippingPointMap() {
		return selectedPlantShippingPointShippingPointMap;
	}

	public void setSelectedPlantShippingPointShippingPointMap(
			Map selectedPlantShippingPointShippingPointMap) {
		this.selectedPlantShippingPointShippingPointMap = selectedPlantShippingPointShippingPointMap;
	}
	
	public Map getSelectableCompCodeSareaCompCodeMap() {
		return selectableCompCodeSareaCompCodeMap;
	}

	public void setSelectableCompCodeSareaCompCodeMap(Map selectableCompCodeSareaCompCodeMap) {
		this.selectableCompCodeSareaCompCodeMap = selectableCompCodeSareaCompCodeMap;
	}

	public Map getSelectedCompCodeSareaCompCodeMap() {
		return selectedCompCodeSareaCompCodeMap;
	}

	public void setSelectedCompCodeSareaCompCodeMap(Map selectedCompCodeSareaCompCodeMap) {
		this.selectedCompCodeSareaCompCodeMap = selectedCompCodeSareaCompCodeMap;
	}

	public Map getSelectableCompCodeSareaSareaMap() {
		return selectableCompCodeSareaSareaMap;
	}

	public void setSelectableCompCodeSareaSareaMap(Map selectableCompCodeSareaSareaMap) {
		this.selectableCompCodeSareaSareaMap = selectableCompCodeSareaSareaMap;
	}

	public Map getSelectedCompCodeSareaSareaMap() {
		return selectedCompCodeSareaSareaMap;
	}

	public void setSelectedCompCodeSareaSareaMap(Map selectedCompCodeSareaSareaMap) {
		this.selectedCompCodeSareaSareaMap = selectedCompCodeSareaSareaMap;
	}

	public Map getSelectableSgroupMap() {
		return selectableSgroupMap;
	}

	public void setSelectableSgroupMap(Map selectableSgroupMap) {
		this.selectableSgroupMap = selectableSgroupMap;
	}

	public Map getSelectedSgroupMap() {
		return selectedSgroupMap;
	}

	public void setSelectedSgroupMap(Map selectedSgroupMap) {
		this.selectedSgroupMap = selectedSgroupMap;
	}
	
	public Map getSelectablePurchOrgMap() {
		return selectablePurchOrgMap;
	}

	public void setSelectablePurchOrgMap(Map selectablePurchOrgMap) {
		this.selectablePurchOrgMap = selectablePurchOrgMap;
	}

	public Map getSelectedPurchOrgMap() {
		return selectedPurchOrgMap;
	}

	public void setSelectedPurchOrgMap(Map selectedPurchOrgMap) {
		this.selectedPurchOrgMap = selectedPurchOrgMap;
	}
	
	public String[] getSelectedCompCodeIds() {
		return selectedCompCodeIds;
	}

	public void setSelectedCompCodeIds(String[] selectedCompCodeIds) {
		this.selectedCompCodeIds = selectedCompCodeIds;
	}
	
	public String[] getSelectedSareaIds() {
		return selectedSareaIds;
	}

	public void setSelectedSareaIds(String[] selectedSareaIds) {
		this.selectedSareaIds = selectedSareaIds;
	}

	public String[] getSelectedSgroupIds() {
		return selectedSgroupIds;
	}

	public void setSelectedSgroupIds(String[] selectedSgroupIds) {
		this.selectedSgroupIds = selectedSgroupIds;
	}

	public String[] getSelectedPurchOrgIds() {
		return selectedPurchOrgIds;
	}

	public void setSelectedPurchOrgIds(String[] selectedPurchOrgIds) {
		this.selectedPurchOrgIds = selectedPurchOrgIds;
	}
		
	public String getPdaPinValidToDatStr() {
		return pdaPinValidToDatStr;
	}

	public void setPdaPinValidToDatStr(String pdaPinValidToDatStr) {
		this.pdaPinValidToDatStr = pdaPinValidToDatStr;
	}

	public String getPdaPinValidToTimeStr() {
		return pdaPinValidToTimeStr;
	}

	public void setPdaPinValidToTimeStr(String pdaPinValidToTimeStr) {
		this.pdaPinValidToTimeStr = pdaPinValidToTimeStr;
	}
	
	public Date getPdaPinValidTo() throws LogonException {
		try {
			return this.selectedTimeZone.parseDayMonthYearHourMinute(pdaPinValidToDatStr,
																	 pdaPinValidToTimeStr);
		}
		catch (Exception e) {
			throw new LogonException(e);
		}
	}

	public void setPdaPinValidTo(Date pdaPinValidTo) {
	    if (pdaPinValidTo != null) {
	        pdaPinValidToDatStr = this.selectedTimeZone.fmtAsDayMonthYear(pdaPinValidTo);
	        pdaPinValidToTimeStr = this.selectedTimeZone.fmtAsHourMinute(pdaPinValidTo);
	    }
	    else {
            pdaPinValidToDatStr = null;
            pdaPinValidToTimeStr = null;
	    }
	}

	public boolean isDriverFlag() {
		return driverFlag;
	}

	public void setDriverFlag(boolean driverFlag) {
		this.driverFlag = driverFlag;
	}

	public String getExtLogin() {
		return extLogin;
	}

	public void setExtLogin(String extLogin) {
		this.extLogin = extLogin;
	}

	public List getUserLanguageList() {
		return userLanguageList;
	}

	public void setUserLanguageList(List userLanguageList) {
		this.userLanguageList = userLanguageList;
	}

	public Long getFunctionToCheck() {
		return functionToCheck;
	}

	public void setFunctionToCheck(Long functionToCheck) {
		this.functionToCheck = functionToCheck;
	}

	public boolean isUserExternal() {
		return userExternal;
	}

	public void setUserExternal(boolean userExternal) {
		this.userExternal = userExternal;
	}

	public UserEntity getUserLoggedIn() {
		return userLoggedIn;
	}

	public void setUserLoggedIn(UserEntity userLoggedIn) {
		this.userLoggedIn = userLoggedIn;
	}

	public Long getFunctionToAuthorize() {
		return functionToAuthorize;
	}

	public void setFunctionToAuthorize(Long functionToAuthorize) {
		this.functionToAuthorize = functionToAuthorize;
	}

	public int getRestrictionType() {
		return restrictionType;
	}

	public void setRestrictionType(int restrictionType) {
		this.restrictionType = restrictionType;
	}

	public Long[] getSelectedRoleIds() {
		return selectedRoleIds;
	}

	public void setSelectedRoleIds(Long[] selectedRoleIds) {
		this.selectedRoleIds = selectedRoleIds;
	}

	public String getPdaUserNoStr() {
		return pdaUserNoStr;
	}

	public void setPdaUserNoStr(String pdaUserNoStr) {
		this.pdaUserNoStr = pdaUserNoStr;
	}
	
	public String getPdaUserNoSuggestion() {
		return pdaUserNoSuggestion;
	}

	public void setPdaUserNoSuggestion(String pdaUserNoSuggestion) {
		this.pdaUserNoSuggestion = pdaUserNoSuggestion;
	}

	public byte[] getPhotoImage() {
		return photoImage;
	}

	public void setPhotoImage(byte[] photoImage) {
		this.photoImage = photoImage;
	}

	public byte[] getSignature() {
		return signature;
	}

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}

	public String getSignatureContentType() {
		return signatureContentType;
	}

	public void setSignatureContentType(String signatureContentType) {
		this.signatureContentType = signatureContentType;
	}

	public String getPhotoImageContentType() {
		return photoImageContentType;
	}

	public void setPhotoImageContentType(String photoImageContentType) {
		this.photoImageContentType = photoImageContentType;
	}

	public String getSignatureFilename() {
		return signatureFilename;
	}

	public void setSignatureFilename(String signatureFilename) {
		this.signatureFilename = signatureFilename;
	}

	public String getPhotoImageFilename() {
		return photoImageFilename;
	}

	public void setPhotoImageFilename(String photoImageFilename) {
		this.photoImageFilename = photoImageFilename;
	}

	public CarrierVendorEntity getCarrierVendor() {
		return carrierVendor;
	}

	public void setCarrierVendor(CarrierVendorEntity carrierVendor) {
		this.carrierVendor = carrierVendor;
	}

	/**
	 * @return the grantableRoleMap
	 */
	public Map getGrantableRoleMap() {
		return grantableRoleMap;
	}

	/**
	 * @param grantableRoleMap the grantableRoleMap to set
	 */
	public void setGrantableRoleMap(Map grantableRoleMap) {
		this.grantableRoleMap = grantableRoleMap;
	}
	
	/**
	 * Returns a string representation of the date and time when the user's
	 * external password expires. 
	 * @return A string representation of the expiration date/time.
	 */
	public String getExtPwdValidToStr()
	{
		return formattedExtPwdTimestamp(userEntity.getExtLoginPwdValidToDat(), true);
	}
	
	/**
	 * Returns a string representation of the date and time when the user's
	 * external password has been locked. 
	 * @return A string representation of the date/time when the password was locked.
	 */
	public String getExtPwdLockedOnStr()
	{
		return formattedExtPwdTimestamp(userEntity.getExtLoginPwdLockedTs(), false);
	}

	/**
	 * Returns a string representation of the date and time of the latest
	 * attempt to reset the user's password.
	 * @return A string representation of the date/time of the last reset attempt.
	 */
	public String getExtPwdResetOnStr()
	{
		return formattedExtPwdTimestamp(userEntity.getExtPwdLastResetAttemptTs(), true);
	}


	/**
	 * Formats a password-related timestamp. 
	 * This method takes a timestamp and formats it for display in the
	 * "password" section on the user admin page. That is, if the specified timestamp
	 * is not null, it is formatted as "&lt;date&gt; &lt;time&gt;". Else a "not defined"
	 * placeholder (litdict key <code>useradmin.field.pwd.nodata</code>) is returned.
	 * @param timestamp A timestamp, can be null.
	 * @param showTime Set to true if the time should be included in the string. Otherwise
	 * only the date will be included.
	 * @return A nicely formatted string or an "undefined" indicator
	 */
	String formattedExtPwdTimestamp(final Date timestamp, final boolean showTime) {
		if (timestamp == null) {
			return Message.lookup(locale, "useradmin.field.pwd.nodata");
		}
		else {
			final StringBuilder sb = new StringBuilder();
			sb.append(this.selectedTimeZone.fmtAsDayMonthYear(timestamp));
			
			if (showTime) {
				sb.append("&nbsp;").append(this.selectedTimeZone.fmtAsHourMinute(timestamp));
			}
			
			return sb.toString();
		}
	}

	public void setTimeZoneList(List<TimeZone> timeZones) {
		this.timeZoneList = timeZones;
	}

	public List<TimeZone> getTimeZoneList() {
		return this.timeZoneList;
	}

	public String getSelectedTimeZoneCd() {
		return this.selectedTimeZone.getTimezoneCd();
	}

	public void setSelectedTimeZoneCd(String selectedTimeZoneCd) {
		this.selectedTimeZone = new TimeZone(selectedTimeZoneCd, locale);
	}

	public List getAllUserTypeList() {
		return allUserTypeList;
	}

	public void setAllUserTypeList(List allUserTypeList) {
		this.allUserTypeList = allUserTypeList;
	}
}

