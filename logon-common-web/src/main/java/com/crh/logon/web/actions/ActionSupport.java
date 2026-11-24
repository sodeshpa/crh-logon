/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.actions;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.interfaces.*;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.business.services.utils.validation.ValidationErrorReason;
import com.crh.logon.business.services.utils.validation.ValidationException;
import com.crh.logon.constants.CallerContext;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonProperties;
import com.crh.logon.web.forms.FatalErrorForm;
import com.crh.logon.web.utils.ConfigBeanProvider;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.ServiceLocatorProvider;
import com.crh.logon.web.utils.UserProvider;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionMessages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Locale;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 */
public class ActionSupport extends Action {
    private static final JcanLogger LOG =
       	JcanLoggerFactory.getLogger(ActionSupport.class);
    
    private ServiceLocator serviceLocator;

    public final static String PROP_BUILD_VERSION = "buildversion";
    private final static String TABS_BEAN_NAME = "ApplicationTabsBean";
    private final static String SUBTABS_1_BEAN_NAME = "ApplicationSubTabs1Bean";
    private final static String SUBTABS_2_BEAN_NAME = "ApplicationSubTabs2Bean";
    private final static String RELOAD_WINDOW_TARGET = "reloadWindowTarget";

    private static final String HOME_ACTION_ADMIN = "/AdminEntry.do";

	private static final String HOME_ACTION_CUST = "/CustEntry.do";

	private static final String HOME_ACTION_ORDER = "/OrderEntry.do";

    /**
     * Default constructor.
     */
    public ActionSupport() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "ActionSupport");
		}
		// ]]
		try {
			serviceLocator = ServiceLocatorProvider.getServiceLocator();
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
	 * Returns the 'homeAction' for the corresponding app (caller).
	 * 
	 * @param contextPath
	 * @param request
	 */
	public static String getHomeAction(String contextPath) {
		// [[
		String result = null;
		Object[][] params = {{"contextPath", contextPath}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ActionSupport.class, "getHomeAction", params);
		}
		// ]]
		try {
			if (StringUtils.isEmpty(contextPath)) {
				result = "#";
			}
			else if (contextPath.startsWith("/logon/admin")) {
				result = ActionSupport.HOME_ACTION_ADMIN;
			}
			else if (contextPath.startsWith("/logon/cust")) {
				result = ActionSupport.HOME_ACTION_CUST;
			}
			else if (contextPath.startsWith("/logon/order")) {
				result = ActionSupport.HOME_ACTION_ORDER;
			}
			else {
				result = "#";
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

    public ServiceLocator getServiceLocator() {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("getServiceLocator(): '" + serviceLocator + "'");
		}
		// ]]
		return serviceLocator;
    }
    
    /* convenience methods... */
	protected TablebrowserService getTablebrowserService() {
		return serviceLocator.getTablebrowserService();
	}
	protected ShippingPointService getShippingPointService() {
		return serviceLocator.getShippingPointService();
	}
	protected GenericBlobService getGenericBlobService() {
		return serviceLocator.getGenericBlobService();
	}
	protected UserAdminService getUserAdminService() {
		return serviceLocator.getUserAdminService();
	}
	protected UserService getUserService() {
		return serviceLocator.getUserService();
	}
	protected RefDataGenericsService getRefDataGenericsService() {
		return serviceLocator.getRefDataGenericsService();
	}

	protected RailSchedulingService getRailSchedulingService() {
		return serviceLocator.getRailSchedulingService();
	}

	protected PDFGeneratorService getPDFGeneratorService() {
		return serviceLocator.getPDFGeneratorService();
	}
	protected FrozenTablesService getFrozenTablesService() {
		return serviceLocator.getFrozenTablesService();
	}
	protected DispokarteService getDispokarteService() {
		return serviceLocator.getDispokarteService();
	}
	protected DispoService getDispoService() {
		return serviceLocator.getDispoService();
	}
	protected ExternalOrderService getExternalOrderService() {
		return serviceLocator.getExternalOrderService();
	}
	protected BestellungService getBestellungService() {
		return serviceLocator.getBestellungService();
	}
	protected BestellungChartService getBestellungChartService() {
		return serviceLocator.getBestellungChartService();
	}
	protected PlantSareaService getPlantSareaService() {
		return serviceLocator.getPlantSareaService();
	}
	protected OrderService getOrderService() {
		return serviceLocator.getOrderService();
	}
	protected LiveKarteService getLiveKarteService() {
		return serviceLocator.getLiveKarteService();
	}
	protected ContainerAdminService getContainerAdminService() {
		return serviceLocator.getContainerAdminService();
	}
	protected ContributionCalcService getContributionCalcService() {
		return serviceLocator.getContributionCalcService();
	}
	protected ZoneMapService getZoneMapService() {
		return serviceLocator.getZoneMapService();
	}
	protected OrderDetailService getOrderDetailService() {
		return serviceLocator.getOrderDetailService();
	}
	protected EmailService getEmailService() {
		return serviceLocator.getEmailService();
	}
	protected OrderSpecialService getOrderSpecialService() {
		return serviceLocator.getOrderSpecialService();
	}
	protected PoolAdminService getPoolAdminService() {
		return serviceLocator.getPoolAdminService();
	}
	protected PoolAssignmentService getPoolAssignmentService() {
		return serviceLocator.getPoolAssignmentService();
	}
	protected ConfigService getConfigService() {
		return serviceLocator.getConfigService();
	}
	protected SalesAreaService getSalesAreaService() {
		return serviceLocator.getSalesAreaService();
	}
	protected CarrierService getCarrierService() {
		return serviceLocator.getCarrierService();
	}
	protected DeliverySettingService getDeliverySettingService() {
		return serviceLocator.getDeliverySettingService();
	}
	protected DeliveryDateService getDeliveryDateService() {
		return serviceLocator.getDeliveryDateService();
	}
	protected GoogleMapsService getGoogleMapsService () {
		return serviceLocator.getGoogleMapsService();
	}

	protected TimeZoneService getTimeZoneService() {
		return serviceLocator.getTimeZoneService();
	}
	protected ShiptoService getShiptoService() {
		return serviceLocator.getShiptoService();
	}

	protected MaterialEnhancementsService getMaterialEnhancementsService () {
		return serviceLocator.getMaterialEnhancementsService();
	}
	
	protected AssignmentService getAssignmentService() {
		return serviceLocator.getAssignmentService();
	}
	
	protected LogonProperties getLogonProperties() {
    	return getConfigBean().getLogonProperties();
    }
    
    protected ConfigBean getConfigBean() {
    	return ConfigBeanProvider.getConfigBean();
    }

    /**
     * Stores an otherwise unhanded exception, which occurred during the
     * execution of a struts action, in a form used by a JSP error page.
     * These are exceptions from which the application cannot gracefully
     * recover, caught in the outermost try-catch block of the action.
     * The JSP error page displays the exception message and stack trace.
     * The struts form for the JSP error page should already have been
     * instantiated by the framework, but if for some reason it was not,
     * then it is instantiated here and stored in the session context.
     *
     * @param request HttpServletRequest request passed to struts action.
     * @param ex Exception a fatal exception of some sort.
     */
    public void setFatalError(HttpServletRequest request, Exception ex) {
		// [[
		Object[][] params = {{"request", request},
							{"ex", ex}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setFatalError", params);
		}
		// ]]
		try {
			if (request == null) {
				LOG.error("request == null!");
				return;
			}
			HttpSession session = request.getSession(false);
			if (session == null) {
				LOG.error("session == null!");
				return;
			}
			FatalErrorForm form = (FatalErrorForm) session.getAttribute("FatalError");
			if (form != null) {
				form.setException(ex);
			}
			else {
				form = new FatalErrorForm();
				form.setException(ex);
				session.setAttribute("FatalError", form);
				System.out.println(this.getClass().getName() + ": FatalErrorForm created.");
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
     * Adds a first ActionError using titleKey and additional info,
     * Then adds another Error for each Exception cause
     * 
     * @param request HttpServletRequest request passed to struts action.
     * @param titleKey LitDict key that should be used as error text.
     * @param additionalInfo allows to provide additional Info
     * @param exception The complete cause list of this exception will be added as messages
     */
    protected void addActionException(HttpServletRequest request,
            String titleKey, String additionalInfo, Throwable exception){
		// [[
		Object[][] params = {{"request", request},
							{"titleKey", titleKey},
							{"additionalInfo", additionalInfo},
							{"exception", exception}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addActionException", params);
		}
		// ]]
		try {
			MessageUtils.addActionError(request, titleKey, additionalInfo);
			while (exception != null){
				MessageUtils.addActionError(request, "errors.detail", exception.getMessage());
				exception = exception.getCause();
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
     * Returns the action errors currently stored in the request.
     *
     * @param request HttpServletRequest
     * @return ActionErrors
     */
    protected ActionMessages getActionErrors(HttpServletRequest request) {
		// [[
		ActionMessages result = null;
		Object[][] params = {{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getActionErrors", params);
		}
		// ]]
		try {
	        return result =  (ActionMessages) request.getAttribute(Globals.ERROR_KEY);
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
     * Returns the action messages currently stored in the request.
     *
     * @param request HttpServletRequest
     * @return ActionErrors
     */
    protected ActionMessages getActionMessages(HttpServletRequest request) {
		// [[
		ActionMessages result = null;
		Object[][] params = {{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getActionMessages", params);
		}
		// ]]
		try {
	        return result =  (ActionMessages) request.getAttribute(Globals.MESSAGE_KEY);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }

    protected UserEntity getUser() {
		// [[
		UserEntity result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getUser");
		}
		// ]]
		try {
			return result = UserProvider.getUser();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }

    public TimeZone getUserTimezone() {
        // [[
        TimeZone result = null;
        boolean logDebugEnabled = LOG.isDebugEnabled();
        if (logDebugEnabled) {
            LOG.entering(this, "getUserTimezone");
        }
        // ]]
        try {
            return result = UserProvider.getTimezone();
        }
        finally {
            // [[
            if (logDebugEnabled) {
                LOG.exiting(result);
            }
            // ]]
        }
    }

    public Locale getUserLocale() {
        // [[
        Locale result = null;
        boolean logDebugEnabled = LOG.isDebugEnabled();
        if (logDebugEnabled) {
            LOG.entering(this, "getUserLocale");
        }
        // ]]
        try {
            return result = UserProvider.getLocale();
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
     * Removes all the selected 'tabnames' from the session in order 
     * to inactivate the corresponding tabs (navigation-components) 
     * on the GUI. 
     * The 'homeAction' of the corresponding app is also removed.
     * 
     * @param request
     */
	@AdnTrace
	protected void inactivateAllTabs(HttpServletRequest request) {

		HttpSession session = request.getSession();

		session.removeAttribute(TABS_BEAN_NAME);
		session.removeAttribute(SUBTABS_1_BEAN_NAME);
		session.removeAttribute(SUBTABS_2_BEAN_NAME);

	}

    /**
     * Sets the http headers that are necessary to make the response
     * an attaches file of the given mime-type with the given name.
     *
	 * A note about the default mimetype. Since this method is only used when exporting a CSV
	 * file, we default to text/plain.
	 *
     * @param mimeType   If the mimetype is null, it defaults to text/plain with UTF-8
	 *                   encoding.
     */
    protected void setDLHeaders(HttpServletRequest request, HttpServletResponse response, String mimeType, String fileName){
		// [[
		Object[][] params = {{"response", response},
							{"mimeType", mimeType},
							{"fileName", fileName}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setDLHeaders", params);
		}
		// ]]
		response.reset();
    	if (mimeType != null){
    		response.setContentType(mimeType);
    	}
		try {
			if (mimeType == null) {
				mimeType = "text/plain;charset=UTF-8";
			}

	        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			response.setContentType(mimeType);
			if (isIE10OrOlder(request)) {
				response.setHeader("Pragma","");
			}
			else {
				response.setHeader("Pragma","public");
				response.setHeader("Cache-Control", "private");
				response.addIntHeader ("Expires", 0);
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

    protected void setSelectedTab(String tab, HttpServletRequest request){
		// [[
		Object[][] params = {{"tab", tab},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setSelectedTab", params);
		}
		// ]]
		try {
			request.getSession().setAttribute(TABS_BEAN_NAME, tab);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
    
    protected void setSelectedSub1Tab(String tab, HttpServletRequest request){
		// [[
		Object[][] params = {{"tab", tab},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setSelectedSub1Tab", params);
		}
		// ]]
		try {
			request.getSession().setAttribute(SUBTABS_1_BEAN_NAME, tab);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
    
    protected void setSelectedSub2Tab(String tab, HttpServletRequest request){
		// [[
		Object[][] params = {{"tab", tab},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setSelectedSub2Tab", params);
		}
		// ]]
		try {
			request.getSession().setAttribute(SUBTABS_2_BEAN_NAME, tab);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
    
    protected void setReloadWindowTarget(String target, HttpServletRequest request){
		// [[
		Object[][] params = {{"target", target},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setReloadWindowTarget", params);
		}
		// ]]
		try {
			request.getSession().setAttribute(RELOAD_WINDOW_TARGET, target);
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
	 * @param request
	 * @param value
	 * @param dbSize
	 * @param precision
	 * @param fieldKey TODO
	 */
	public static void checkFieldMaxValue(HttpServletRequest request, String value, long dbSize,
			long precision, String fieldKey) {
		if (StringUtils.isNotEmpty(value)) {			
			BigDecimal max = LogonUtil.getMaxValueForDbSizeAndPrecision( dbSize, precision );
			try{				
				BigDecimal maxBig = new BigDecimal(value);
				if (maxBig.compareTo(max) > 0) {
					MessageUtils.addActionWarning(request, "warning.exceeded", MessageUtils.getMessage(request, fieldKey), max.toString());
				}
			}
			catch (Exception e) {
				MessageUtils.addActionWarning(request, "warning.format", MessageUtils.getMessage(request, fieldKey));
			}
		}
	}
	
	/**
	 * @param request
	 * @return
	 */
	protected boolean noErrorWarningPresent(HttpServletRequest request) {
		boolean noErrorsPresent = noErrorsPresent(request);
		boolean noWarningsPresent = MessageUtils.getActionWarnings(request) == null || MessageUtils.getActionWarnings(request).isEmpty();
		return noWarningsPresent && noErrorsPresent;
	}

	protected boolean noErrorsPresent(HttpServletRequest request) {
		return MessageUtils.getActionErrors(request) == null || MessageUtils.getActionErrors(request).isEmpty();
	}
	
	public final void processErrors(HttpServletRequest request, ValidationException e, CallerContext context, boolean showAsWarning) {


		Collection<ValidationErrorReason> validationErrorReasons = e.getValidationErrorReasons();

		processValidationErrorReasons(request, validationErrorReasons, context, showAsWarning);
		
		if (showAsWarning) {
			LOG.warn(e.getMessage(), e);
		}
		else {
			LOG.error(e.getMessage(), e);
		}
	}
	public void processValidationErrorReasonsAndTrace(HttpServletRequest request, final Collection<ValidationErrorReason> reasons,
			CallerContext context,
			boolean showAsWarning) {

			processValidationErrorReasons(request, reasons, context, showAsWarning);

		for (ValidationErrorReason reason : reasons) {
			if (showAsWarning) {
				LOG.warn(reason.getCustorderError().getPlainMessage());
			}
			else {
				LOG.error(reason.getCustorderError().getPlainMessage());
			}
		}

	}
	public void processValidationErrorReasons(HttpServletRequest request, final Collection<ValidationErrorReason> reasons,
			CallerContext context,
			boolean showAsWarningGeneral) {


		String titleKey;
		String[] additionalInfo;
		for (ValidationErrorReason reason : reasons) {
			Boolean showAsWarningMessage = reason.getShowAsWarning();
			titleKey = reason.getCustorderError().getMessage(context);
			additionalInfo = reason.getAdditionalInfo();

			String firstAdditionalInfo = "";
			String secondAdditionalInfo = "";

			if(additionalInfo != null && additionalInfo.length > 0){
				firstAdditionalInfo = additionalInfo[0];

				if(additionalInfo.length > 1){
					StringBuilder secondAdditionalInfoList = new StringBuilder();
					for(int i = 1; i < additionalInfo.length; i++){
						secondAdditionalInfoList.append(additionalInfo[i]);
					}
					secondAdditionalInfo = secondAdditionalInfoList.toString();
				}
			}

			// if the reason has his own control for being warning
			// than consider it, otherwise use the general flag
			if (Boolean.TRUE.equals(showAsWarningMessage)) {
				MessageUtils.addActionWarning(request, titleKey, firstAdditionalInfo, secondAdditionalInfo);
			}
			else {
				if (showAsWarningGeneral) {
					MessageUtils.addActionWarning(request, titleKey, firstAdditionalInfo, secondAdditionalInfo);
				}
				else {
					MessageUtils.addActionError(request, titleKey, firstAdditionalInfo, secondAdditionalInfo);
				}
			}
		}
	}

	/**
	 * Checks if the current request comes from IE10 or an older
	 * IE browser.
	 * @param request
	 * @return
	 */
	private boolean isIE10OrOlder(HttpServletRequest request) 
	{
		final String userAgent = request.getHeader("user-agent");
		LOG.debug("Found user-agent header: {}", new Object[] {userAgent});
		
		if (userAgent == null) {
			return false;
		}
		
		// Note that Opera user agent strings can also contan "MSIE":
		// http://www.useragentstring.com/pages/Opera/
		final int msiePos = userAgent.indexOf("MSIE");
		if (msiePos == -1 || userAgent.contains("Opera")) {
			LOG.debug("Current browser is not MSIE.");
			return false;
		}
		
		/*
		 * Sample user agent string for MSIE:
		 * Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; Media Center PC 6.0; InfoPath.2; MS-RTC LM 8
		 */
		final String versionString = userAgent.substring(msiePos + 5, msiePos + 8);
		LOG.debug("Detected version number string: '{}'", new Object[] {versionString});
		
		// Simplified version
		return versionString.equals("10.") || versionString.equals("8.0") || versionString.equals("7.0") || 
				versionString.equals("6.1") || versionString.equals("6.0");
	}
}
