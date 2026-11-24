/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.bestellung;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.forms.bestellung.BestellungExternalSearchForm;
import com.crh.logon.order.web.utils.ExternalOrderSearchExportMetaData;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.UserService;
import com.crh.logon.business.services.reporting.SqlDataExporter;
import com.crh.logon.business.services.utils.PreferencesKey;
import com.crh.logon.business.services.utils.UserPreferenceUtil;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.admin.PreferencesEntity;
import com.crh.logon.persistence.entities.refdata.OrderCreationTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusTxEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.time.DateTimeFormat;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_bestellung_BestellungExternalSearchForm" path="/bestellung/BestellungExternalSearchEntry" scope="session" validate="false"
 *
 * @struts.action-forward name="success" path="BestellungSearchTile.def"
 */
public class BestellungExternalSearchEntryAction extends AbstractBestellungSearchEntryAction {
	
	Long[] STATUS_LIST_NEW = {	
			Constant.ORDER_STATUS_NEW
			};
	
	Long[] STATUS_LIST_POOL = {	
			Constant.ORDER_STATUS_TRANSMITTED_TO_POOL,
			Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK
			};
		
	Long[] STATUS_LIST_WORKFLOW = {
			Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER,
			Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK,			
			Constant.ORDER_STATUS_DISPATCHED,
			Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER,	
			Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK,	
			Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL,			
			Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT,
			Constant.ORDER_STATUS_LOADING,
			Constant.ORDER_STATUS_LOADED,
			Constant.ORDER_STATUS_SHIPPING,
			Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT,
			Constant.ORDER_STATUS_UNLOADING,
			Constant.ORDER_STATUS_DELIVERED,
			Constant.ORDER_STATUS_FINISHED
			};

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(BestellungExternalSearchEntryAction.class);

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
			Authorization.authorize(Authorization.EXTERNAL_ORDER_LIST);
			setSelectedTab("extorderlist", request);
			setSelectedSub1Tab(null, request);
			setSelectedSub2Tab(null, request);
			BestellungExternalSearchForm searchForm = (BestellungExternalSearchForm)form;
			
			String forwardName;
			String formAction = !StringUtils.empty(searchForm.getFormAction()) ? searchForm.getFormAction() : "";
			if (ACTION_SAVE_PREFERENCES.equals(formAction)) {
				forwardName = handleSaveButtonClick(searchForm, Constant.USER_PREFERENCE_SCREEN_EXTERNAL_ORDER_SEARCH);
				
				addSuccessFullySavedMessageForAjaxResponse(request, response);
	    		
				return result = null;
			}
			else if (ACTION_RESET.equals(formAction)) {
				searchForm.clearDefaults();
				forwardName = "success";
				return result = mapping.findForward(forwardName);
			}
			else if (request.getParameter("export") != null) {
				setDLHeaders(request, response, null, "order_search_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".csv");
				SqlDataExporter exporter = new SqlDataExporter(getServiceLocator());
				ExternalOrderSearchExportMetaData emd = new ExternalOrderSearchExportMetaData();
				emd.setLangCd(request.getLocale().getLanguage());
				if (!StringUtils.empty(request.getParameter("extOrderNumber"))) {
					String validatedOrderNo = request.getParameter("extOrderNumber");
					validatedOrderNo = validatedOrderNo.replaceAll("[^\\d]", "");
					emd.setOrderNumber(Long.valueOf(validatedOrderNo));
				}
				
				if (!StringUtils.empty(request.getParameter("deliveryFromDat"))) {
					try {
						Date fromDate = DateTimeUtils.parseDayMonthYearInGMTZone(request.getParameter("deliveryFromDat"));
						fromDate = DateTimeUtils.getMorningOfDayInGMTZone(fromDate);
						emd.setDeliveryFromDat(fromDate);
					} catch (ParseException e) {
						throw new LogonException("Invalid date format " + request.getParameter("deliveryFromDat"), Reason.RES_ERROR_INVALID_DATE_FORMAT);
					}
				}

				if (!StringUtils.empty(request.getParameter("deliveryToDat"))) {
					try {
						Date toDate = DateTimeUtils.parseDayMonthYearInGMTZone(request.getParameter("deliveryToDat"));
						toDate = DateTimeUtils.getMidnightOfDayInGMTZone(toDate);
						emd.setDeliveryToDat(toDate);
					} catch (ParseException e) {
						throw new LogonException("Invalid date format " + request.getParameter("deliveryToDat"), Reason.RES_ERROR_INVALID_DATE_FORMAT);
					}
				}

				if (!StringUtils.empty(request.getParameter("statusList"))) {
					emd.setStatusList(Arrays.asList(request.getParameter("statusList").split(",")));
				}
				if (!StringUtils.empty(request.getParameter("vehicleId"))) {
					emd.setTruckId(Long.valueOf(request.getParameter("vehicleId")));
				}
				if (!StringUtils.empty(request.getParameter("extDeliveryNote"))) {
					emd.setExtDeliveryNote(request.getParameter("extDeliveryNote"));
				}
				emd.init(getServiceLocator(), getUser().getUserId());
				exporter.exportSqlData(response.getWriter(),
									   emd.getQueryString(),
									   emd.getQParams(),
									   request.getLocale(),
									   getUserTimezone());
				
				return result = null;
			}
			
			List<OrderStatusTxEntity> statusList = buildValidStatusSelectList(request.getLocale());
			searchForm.setOrderStatusList(statusList);
			
			populateForm(searchForm, request.getLocale(), statusList,
					Constant.USER_PREFERENCE_SCREEN_EXTERNAL_ORDER_SEARCH);
			
			forwardName = "success";
			
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

	private List<OrderStatusTxEntity> buildValidStatusSelectList(Locale locale) {
		//visible anyway
		List<Long> requestStatuses = new ArrayList<Long>() ;
		requestStatuses.addAll(Arrays.asList(STATUS_LIST_WORKFLOW));
		
		if( Authorization.isAllowed(Authorization.ORDER_VIEW_3_MONITORING_AUFTRAGSANNAHME)) {
			requestStatuses.addAll(Arrays.asList(STATUS_LIST_NEW));
		}

		if( Authorization.isAllowed(Authorization.ORDER_VIEW_3_MONITORING_FOR_POOL)) {
			requestStatuses.addAll(Arrays.asList(STATUS_LIST_POOL));
		}
		
		return getBestellungService().getStatusList(requestStatuses, locale.getLanguage());
	}
	
	@Override
	protected Map<String, Object> fillFieldValueMap(ActionForm actionForm) {
			Map<String, Object> fieldValueMap = new HashMap<String, Object>();
		BestellungExternalSearchForm searchForm = (BestellungExternalSearchForm)actionForm;
		
		Date deliveryFromDate = null;
		
		if (!StringUtils.empty(searchForm.getInDeliveryFromDate())) {
			try {
				deliveryFromDate = DateTimeUtils.parseDayMonthYearInGMTZone(searchForm.getInDeliveryFromDate());
			} catch (ParseException e) {
				throw new LogonException("Invalid date format " + searchForm.getInDeliveryFromDate(), Reason.RES_ERROR_INVALID_DATE_FORMAT);
			}
		}
		fieldValueMap.put(PreferencesKey.EXTSEARCH_DELIVERY_FROM_DATE.name(), deliveryFromDate);
		
		Date deliveryToDate = null;
		if (!StringUtils.empty(searchForm.getInDeliveryToDate())) {
			try {
				deliveryToDate = DateTimeUtils.parseDayMonthYearInGMTZone(searchForm.getInDeliveryToDate());
			} catch (ParseException e) {
				throw new LogonException("Invalid date format " + searchForm.getInDeliveryToDate(), Reason.RES_ERROR_INVALID_DATE_FORMAT);
			}
		}
		fieldValueMap.put(PreferencesKey.EXTSEARCH_DELIVERY_TO_DATE.name(), deliveryToDate);
		
		fieldValueMap.put(PreferencesKey.EXTSEARCH_ORDER_STATUS_LIST.name(), searchForm.getInOrderStatusList());
		fieldValueMap.put(PreferencesKey.EXTSEARCH_VEHICLE_COMPOSITE_ID.name(), searchForm.getVehicleCompositeId());
		fieldValueMap.put(PreferencesKey.EXTSEARCH_VEHICLE_NAME.name(), searchForm.getVehicleName());
		fieldValueMap.put(PreferencesKey.EXTSEARCH_DELIVERY_NOTE_COMMENT_NUMBER.name(), searchForm.getExtDeliveryNote());
		
		
		
		fieldValueMap.put(PreferencesKey.EXTSEARCH_W_EXT_ORDER_NUMBER.name(), BigDecimal.valueOf(searchForm.getwExtOrderNumber()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_W_EXT_DELIVERY_NOTE.name(), BigDecimal.valueOf(searchForm.getwExtDeliveryNote()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_W_SHIPTO_NAME.name(), BigDecimal.valueOf(searchForm.getwShiptoName()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_W_SHIPTO_LOAD_UNLOAD.name(), BigDecimal.valueOf(searchForm.getwShiptoLoadUnload()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_W_SHIPTO_CONTACT_PERSON_TXT.name(), BigDecimal.valueOf(searchForm.getwShiptoContactPersonTxt()));

		fieldValueMap.put(PreferencesKey.EXTSEARCH_W_SHIPTO_STREET_HOUSE_NO.name(), BigDecimal.valueOf(searchForm.getwShiptoStreetHouseNo()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_W_SHIPTO_COUNTRY.name(), BigDecimal.valueOf(searchForm.getwShiptoCountry()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_W_SHIPTO_POSTAL_CODE.name(), BigDecimal.valueOf(searchForm.getwShiptoPostalCode()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_W_SHIPTO_CITY.name(), BigDecimal.valueOf(searchForm.getwShiptoCity()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_W_STATUS_TXT.name(), BigDecimal.valueOf(searchForm.getwStatusTxt()));

		fieldValueMap.put(PreferencesKey.EXTSEARCH_W_MATERIAL_NAME.name(), BigDecimal.valueOf(searchForm.getwMaterialName()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_W_ORDERED_QUANTITY.name(), BigDecimal.valueOf(searchForm.getwOrderedQuantity()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_W_ORDERED_UOM_NAME.name(), BigDecimal.valueOf(searchForm.getwOrderedUomName()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_W_DELIVERED_QUANTITY.name(), BigDecimal.valueOf(searchForm.getwDeliveredQuantity()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_W_DELIVERED_UOM_NAME.name(), BigDecimal.valueOf(searchForm.getwDeliveredUomName()));

		fieldValueMap.put(PreferencesKey.EXTSEARCH_W_SHIPPINGPOINT_NAME.name(), BigDecimal.valueOf(searchForm.getwShippingpointName()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_W_SP_LOAD_UNLOAD.name(), BigDecimal.valueOf(searchForm.getwSpLoadUnload()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_W_SP_CONTACT_PERSON_TXT.name(), BigDecimal.valueOf(searchForm.getwSpContactPersonTxt()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_W_SP_STREET_HOUSE_NO.name(), BigDecimal.valueOf(searchForm.getwSpStreetHouseNo()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_W_SP_COUNTRY.name(), BigDecimal.valueOf(searchForm.getwSpCountry()));

		fieldValueMap.put(PreferencesKey.EXTSEARCH_W_SP_POSTAL_CODE.name(), BigDecimal.valueOf(searchForm.getwSpPostalCode()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_W_SP_CITY.name(), BigDecimal.valueOf(searchForm.getwSpCity()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_W_DELIVERY_FROM.name(), BigDecimal.valueOf(searchForm.getwDeliveryFrom()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_W_DELIVERY_TO.name(), BigDecimal.valueOf(searchForm.getwDeliveryTo()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_W_POOL_NAME.name(), BigDecimal.valueOf(searchForm.getwPoolName()));

		fieldValueMap.put(PreferencesKey.EXTSEARCH_W_CARRIER_NAME.name(), BigDecimal.valueOf(searchForm.getwCarrierName()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_W_TRUCK_NAME.name(), BigDecimal.valueOf(searchForm.getwTruckName()));
		
		//indexes
		
		fieldValueMap.put(PreferencesKey.EXTSEARCH_I_EXT_ORDER_NUMBER.name(), BigDecimal.valueOf(searchForm.getIdxExtOrderNumber()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_I_EXT_DELIVERY_NOTE.name(), BigDecimal.valueOf(searchForm.getIdxExtDeliveryNote()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_I_SHIPTO_NAME.name(), BigDecimal.valueOf(searchForm.getIdxShiptoName()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_I_SHIPTO_LOAD_UNLOAD.name(), BigDecimal.valueOf(searchForm.getIdxShiptoLoadUnload()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_I_SHIPTO_CONTACT_PERSON_TXT.name(), BigDecimal.valueOf(searchForm.getIdxShiptoContactPersonTxt()));

		fieldValueMap.put(PreferencesKey.EXTSEARCH_I_SHIPTO_STREET_HOUSE_NO.name(), BigDecimal.valueOf(searchForm.getIdxShiptoStreetHouseNo()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_I_SHIPTO_COUNTRY.name(), BigDecimal.valueOf(searchForm.getIdxShiptoCountry()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_I_SHIPTO_POSTAL_CODE.name(), BigDecimal.valueOf(searchForm.getIdxShiptoPostalCode()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_I_SHIPTO_CITY.name(), BigDecimal.valueOf(searchForm.getIdxShiptoCity()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_I_STATUS_TXT.name(), BigDecimal.valueOf(searchForm.getIdxStatusTxt()));

		fieldValueMap.put(PreferencesKey.EXTSEARCH_I_MATERIAL_NAME.name(), BigDecimal.valueOf(searchForm.getIdxMaterialName()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_I_ORDERED_QUANTITY.name(), BigDecimal.valueOf(searchForm.getIdxOrderedQuantity()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_I_ORDERED_UOM_NAME.name(), BigDecimal.valueOf(searchForm.getIdxOrderedUomName()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_I_DELIVERED_QUANTITY.name(), BigDecimal.valueOf(searchForm.getIdxDeliveredQuantity()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_I_DELIVERED_UOM_NAME.name(), BigDecimal.valueOf(searchForm.getIdxDeliveredUomName()));

		fieldValueMap.put(PreferencesKey.EXTSEARCH_I_SHIPPINGPOINT_NAME.name(), BigDecimal.valueOf(searchForm.getIdxShippingpointName()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_I_SP_LOAD_UNLOAD.name(), BigDecimal.valueOf(searchForm.getIdxSpLoadUnload()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_I_SP_CONTACT_PERSON_TXT.name(), BigDecimal.valueOf(searchForm.getIdxSpContactPersonTxt()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_I_SP_STREET_HOUSE_NO.name(), BigDecimal.valueOf(searchForm.getIdxSpStreetHouseNo()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_I_SP_COUNTRY.name(), BigDecimal.valueOf(searchForm.getIdxSpCountry()));

		fieldValueMap.put(PreferencesKey.EXTSEARCH_I_SP_POSTAL_CODE.name(), BigDecimal.valueOf(searchForm.getIdxSpPostalCode()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_I_SP_CITY.name(), BigDecimal.valueOf(searchForm.getIdxSpCity()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_I_DELIVERY_FROM.name(), BigDecimal.valueOf(searchForm.getIdxDeliveryFrom()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_I_DELIVERY_TO.name(), BigDecimal.valueOf(searchForm.getIdxDeliveryTo()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_I_POOL_NAME.name(), BigDecimal.valueOf(searchForm.getIdxPoolName()));

		fieldValueMap.put(PreferencesKey.EXTSEARCH_I_CARRIER_NAME.name(), BigDecimal.valueOf(searchForm.getIdxCarrierName()));
		fieldValueMap.put(PreferencesKey.EXTSEARCH_I_TRUCK_NAME.name(), BigDecimal.valueOf(searchForm.getIdxTruckName()));
		
		return fieldValueMap;
	}

	//called when loading default values of the fields of Order Search or Delivery Monitoring/Monitoring List screens from the database
	protected void populateForm(BestellungExternalSearchForm searchForm, Locale locale, 
			List<OrderStatusTxEntity> orderStatusList, String screen) {
		searchForm.clearDefaults();
		final UserService userService = getUserService();
		
		final List<PreferencesEntity> preferencesList = userService.fetchPreferencesList(
				getUser(), screen);
		if (preferencesList != null && !preferencesList.isEmpty()) {
			populateFormWithPreferences(searchForm, locale, preferencesList, null, orderStatusList);
		}
	}
	
	@Override
	protected void populateFormWithPreferences(ActionForm actionForm, 
			Locale locale, List<PreferencesEntity> preferencesList, 
			List<OrderCreationTypeTxEntity> orderCreationTypeList, 
			List<OrderStatusTxEntity> orderStatusList) {
		
		BestellungExternalSearchForm searchForm = (BestellungExternalSearchForm)actionForm;
		Map<String,Object> preferencesMap = UserPreferenceUtil.createPreferencesMapfromList(preferencesList);
		
		searchForm.setApplyUserDefaultSettings(true);

		final Date defaultDeliveryFromDate = (Date)preferencesMap.get(PreferencesKey.EXTSEARCH_DELIVERY_FROM_DATE.name());
		final String defaultDeliveryFromDateString = DateTimeFormat.asLogonDayMonthYearInGMTZone(locale,
                defaultDeliveryFromDate);
		searchForm.setDefaultDeliveryFromDateString(defaultDeliveryFromDateString);

		final Date defaultDeliveryToDate = (Date)preferencesMap.get(PreferencesKey.EXTSEARCH_DELIVERY_TO_DATE.name());
		final String defaultDeliveryToDateString = DateTimeFormat.asLogonDayMonthYearInGMTZone(locale,
                defaultDeliveryToDate);
		searchForm.setDefaultDeliveryToDateString(defaultDeliveryToDateString);


		final String defaultOrderStatusesObject = UserPreferenceUtil.getString(preferencesMap,PreferencesKey.EXTSEARCH_ORDER_STATUS_LIST);
		if (defaultOrderStatusesObject != null && orderStatusList != null && !orderStatusList.isEmpty()) {
			final String defaultOrderStatusesAsString = (String) defaultOrderStatusesObject;

			if (!StringUtils.empty(defaultOrderStatusesAsString)) {
				String[] defaultOrderStatuses = org.apache.commons.lang.StringUtils.split(defaultOrderStatusesAsString, ",");
				List<Long> defaultOrderStatusList = null;
				if (defaultOrderStatuses != null && defaultOrderStatuses.length > 0) {
					defaultOrderStatusList = new ArrayList<Long>();
					for (String defaultOrderStatusString : defaultOrderStatuses) {
						for (OrderStatusTxEntity tx : orderStatusList) {
							long statusCode = tx.getOrderStatusTxPK().getOrderStatusBs().getOrderStatusCd();
							long defaultOrderStatus = Long.parseLong(defaultOrderStatusString);
							if (statusCode == defaultOrderStatus) {
								defaultOrderStatusList.add(defaultOrderStatus);
								break;
							}
						}
					}
				}
				searchForm.setDefaultOrderStatusList(defaultOrderStatusList);
			}
		}

		searchForm.setVehicleCompositeId(UserPreferenceUtil.getString(preferencesMap, PreferencesKey.EXTSEARCH_VEHICLE_COMPOSITE_ID));
		searchForm.setVehicleName(UserPreferenceUtil.getString(preferencesMap, PreferencesKey.EXTSEARCH_VEHICLE_NAME));
		searchForm.setExtDeliveryNote(UserPreferenceUtil.getString(preferencesMap, PreferencesKey.EXTSEARCH_DELIVERY_NOTE_COMMENT_NUMBER));
		
		//todo orsika 06.05.2013 use map instead of separate variables
//			HashMap<String, Long> columnWidthMap = new HashMap<String, Long>();
//			columnWidthMap.put("orderNumber", UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_ORDER_NUMBER));
//			searchForm.setColumnWidthMap(columnWidthMap);
		

		searchForm.setwExtOrderNumber(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_W_EXT_ORDER_NUMBER));
		searchForm.setwExtDeliveryNote(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_W_EXT_DELIVERY_NOTE));
		searchForm.setwShiptoName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_W_SHIPTO_NAME));
		searchForm.setwShiptoLoadUnload(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_W_SHIPTO_LOAD_UNLOAD));
		searchForm.setwShiptoContactPersonTxt(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_W_SHIPTO_CONTACT_PERSON_TXT));
		
		searchForm.setwShiptoStreetHouseNo(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_W_SHIPTO_STREET_HOUSE_NO));
		searchForm.setwShiptoCountry(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_W_SHIPTO_COUNTRY));
		searchForm.setwShiptoPostalCode(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_W_SHIPTO_POSTAL_CODE));
		searchForm.setwShiptoCity(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_W_SHIPTO_CITY));
		searchForm.setwStatusTxt(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_W_STATUS_TXT));
		
		searchForm.setwMaterialName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_W_MATERIAL_NAME));
		searchForm.setwOrderedQuantity(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_W_ORDERED_QUANTITY));
		searchForm.setwOrderedUomName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_W_ORDERED_UOM_NAME));
		searchForm.setwDeliveredQuantity(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_W_DELIVERED_QUANTITY));
		searchForm.setwDeliveredUomName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_W_DELIVERED_UOM_NAME));
		
		searchForm.setwShippingpointName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_W_SHIPPINGPOINT_NAME));
		searchForm.setwSpLoadUnload(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_W_SP_LOAD_UNLOAD));
		searchForm.setwSpContactPersonTxt(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_W_SP_CONTACT_PERSON_TXT));
		searchForm.setwSpStreetHouseNo(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_W_SP_STREET_HOUSE_NO));
		searchForm.setwSpCountry(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_W_SP_COUNTRY));
		
		searchForm.setwSpPostalCode(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_W_SP_POSTAL_CODE));
		searchForm.setwSpCity(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_W_SP_CITY));
		searchForm.setwDeliveryFrom(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_W_DELIVERY_FROM));
		searchForm.setwDeliveryTo(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_W_DELIVERY_TO));
		searchForm.setwPoolName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_W_POOL_NAME));
		
		searchForm.setwCarrierName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_W_CARRIER_NAME));
		searchForm.setwTruckName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_W_TRUCK_NAME));
		
		//indexes
		searchForm.setIdxExtOrderNumber(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_I_EXT_ORDER_NUMBER));
		searchForm.setIdxExtDeliveryNote(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_I_EXT_DELIVERY_NOTE));
		searchForm.setIdxShiptoName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_I_SHIPTO_NAME));
		searchForm.setIdxShiptoLoadUnload(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_I_SHIPTO_LOAD_UNLOAD));
		searchForm.setIdxShiptoContactPersonTxt(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_I_SHIPTO_CONTACT_PERSON_TXT));
		
		searchForm.setIdxShiptoStreetHouseNo(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_I_SHIPTO_STREET_HOUSE_NO));
		searchForm.setIdxShiptoCountry(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_I_SHIPTO_COUNTRY));
		searchForm.setIdxShiptoPostalCode(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_I_SHIPTO_POSTAL_CODE));
		searchForm.setIdxShiptoCity(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_I_SHIPTO_CITY));
		searchForm.setIdxStatusTxt(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_I_STATUS_TXT));
		
		searchForm.setIdxMaterialName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_I_MATERIAL_NAME));
		searchForm.setIdxOrderedQuantity(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_I_ORDERED_QUANTITY));
		searchForm.setIdxOrderedUomName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_I_ORDERED_UOM_NAME));
		searchForm.setIdxDeliveredQuantity(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_I_DELIVERED_QUANTITY));
		searchForm.setIdxDeliveredUomName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_I_DELIVERED_UOM_NAME));
		
		searchForm.setIdxShippingpointName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_I_SHIPPINGPOINT_NAME));
		searchForm.setIdxSpLoadUnload(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_I_SP_LOAD_UNLOAD));
		searchForm.setIdxSpContactPersonTxt(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_I_SP_CONTACT_PERSON_TXT));
		searchForm.setIdxSpStreetHouseNo(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_I_SP_STREET_HOUSE_NO));
		searchForm.setIdxSpCountry(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_I_SP_COUNTRY));
		
		searchForm.setIdxSpPostalCode(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_I_SP_POSTAL_CODE));
		searchForm.setIdxSpCity(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_I_SP_CITY));
		searchForm.setIdxDeliveryFrom(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_I_DELIVERY_FROM));
		searchForm.setIdxDeliveryTo(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_I_DELIVERY_TO));
		searchForm.setIdxPoolName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_I_POOL_NAME));
		
		searchForm.setIdxCarrierName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_I_CARRIER_NAME));
		searchForm.setIdxTruckName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.EXTSEARCH_I_TRUCK_NAME));
	}
	
}
