/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.bestellung;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.beans.GeneralDisplayNameDto;
import com.crh.logon.business.services.interfaces.BestellungService;
import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.business.services.interfaces.UserService;
import com.crh.logon.business.services.utils.PreferencesKey;
import com.crh.logon.business.services.utils.UserPreferenceUtil;
import com.crh.logon.order.web.forms.bestellung.BestellungSearchForm;
import com.crh.logon.persistence.entities.admin.PreferencesEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.OrderCreationTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusTxEntity;
import com.crh.logon.persistence.entities.refdata.SgroupBsEntity;
import com.crh.logon.persistence.entities.refdata.SofficeBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.time.DateTimeFormat;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.PlantSareaUtils;

import flexjson.JSONSerializer;

/**
 * @author AdNovum Informatik AG
 */
public abstract class AbstractBestellungSearchEntryAction extends OrderActionSupport {
	
	public static final String ACTION_SAVE_PREFERENCES = "savePreferences";
	public static final String ACTION_RESET = "reset";
	
	private static final String SUCCESSFULLY_SAVED_MESSAGE_KEY = "dispokarte.success.save";
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(AbstractBestellungSearchEntryAction.class);
	
	protected Map getJsonData(Locale locale) {
		
		RefDataGenericsService refData = getRefDataGenericsService();
		Map jsonData = new HashMap();
	    
		List deliveryTypeTxEntityList = refData.findTxEntityList(DeliveryTypeTxEntity.class, 
				false,
	    		locale,
	    		"deliveryTypeTxPK.deliveryTypeBs.deliveryTypeCd",
	    		true
	    );
	    jsonData.put("uomList", getJsonSerializer().serialize(refData.findTxEntityList(UomTxEntity.class, locale)));
	    jsonData.put("deliveryTypeList", getJsonSerializer().serialize(deliveryTypeTxEntityList));
	    jsonData.put("plantList", getJsonSerializer().serialize(PlantSareaUtils.getPlantList(getPlantSareaService())));
	    jsonData.put("shippingpointList", getJsonSerializer()
	    		.include("shippingpointId")
	    		.include("shippingpointName")
	    		.exclude("*").serialize(getBestellungService().fetchShippingpointList()));
	    jsonData.put("sareaList", getJsonSerializer().serialize(getSalesAreaService().findSareaEntityList(true, SareaEntity.ATTR_SAREA_DISPLAY_LONGNAME, true)));
	    jsonData.put("sofficeList", getJsonSerializer().serialize(refData.findBsEntityList(SofficeBsEntity.class, true, SofficeBsEntity.ATTR_SOFFICE_NAME, true)));
	    jsonData.put("sgroupList", getJsonSerializer().serialize(refData.findBsEntityList(SgroupBsEntity.class, true, SgroupBsEntity.ATTR_SGROUP_NAME, true)));
	    
	    List poolTxEntities = getPoolAdminService().fetchPoolTxEntities(locale);
		jsonData.put("poolList", getJsonSerializer().serialize(poolTxEntities));

	    List commCarrierEntities = getCarrierService().fetchAllCommercialCarrierList(null, locale);
		jsonData.put("commCarrierList", getJsonSerializer().serialize(commCarrierEntities));
		
		
		List execCarrierEntities = getCarrierService().fetchDistinctExecutingCarrierList(null, locale);
		jsonData.put("execCarrierList", getJsonSerializer().serialize(execCarrierEntities));
		
		return jsonData;
	}
	
	protected String handleSaveButtonClick(ActionForm searchForm, String screenName) {
		Map<String, Object> fieldValueMap = fillFieldValueMap(searchForm);
		final List<PreferencesEntity> preferencesList = storeGuiValuesInEntity(fieldValueMap);
		final UserService userService = getUserService();
		userService.savePreferences(getUser(), screenName, preferencesList);
		return "success";
	}
	
	/**
	 * Used to populate fieldValueMap from the values in searchForm.
	 * 
	 * @param searchForm
	 * @param fieldValueMap
	 */
	protected Map<String, Object> fillFieldValueMap(ActionForm actionForm) {
			Map<String, Object> fieldValueMap = new HashMap<String, Object>();
		
			BestellungSearchForm searchForm = (BestellungSearchForm)actionForm;
			
		Date deliveryFromDate = null;
		
		if (StringUtils.isNotEmpty(searchForm.getInDeliveryFromDate())) {
			try {
				deliveryFromDate = DateTimeUtils.parseDayMonthYearInGMTZone(searchForm.getInDeliveryFromDate());
			} catch (ParseException e) {
				throw new LogonException("Invalid date format " + searchForm.getInDeliveryFromDate(), Reason.RES_ERROR_INVALID_DATE_FORMAT);
			}
		}
		fieldValueMap.put(PreferencesKey.SEARCH_DELIVERY_FROM_DATE.name(), deliveryFromDate);
		
		Date deliveryToDate = null;
		if (StringUtils.isNotEmpty(searchForm.getInDeliveryToDate())) {
			try {
				deliveryToDate = DateTimeUtils.parseDayMonthYearInGMTZone(searchForm.getInDeliveryToDate());
			} catch (ParseException e) {
				throw new LogonException("Invalid date format " + searchForm.getInDeliveryToDate(), Reason.RES_ERROR_INVALID_DATE_FORMAT);
			}
		}
		fieldValueMap.put(PreferencesKey.SEARCH_DELIVERY_TO_DATE.name(), deliveryToDate);
		
		fieldValueMap.put(PreferencesKey.SEARCH_LIVE_FLAG.name(), searchForm.getLiveFlag());
		fieldValueMap.put(PreferencesKey.SEARCH_ORDER_CREATION_TYPE.name(), searchForm.getInOrderCreationTypeList());
		fieldValueMap.put(PreferencesKey.SEARCH_ORDER_STATUS_LIST.name(), searchForm.getInOrderStatusList());
		fieldValueMap.put(PreferencesKey.SEARCH_DELIVERY_TYPE_CD_LIST.name(), searchForm.getInDeliveryTypeCdList());
		fieldValueMap.put(PreferencesKey.SEARCH_MAT_PACKAGING_TYPE.name(), searchForm.getMatPackagingType());
		fieldValueMap.put(PreferencesKey.SEARCH_DESTINATION_CD_LIST.name(), searchForm.getInDestinationCdList());
		fieldValueMap.put(PreferencesKey.SEARCH_CARRIER_ID_LIST.name(), searchForm.getInCarrierIdList());
		
		fieldValueMap.put(PreferencesKey.SEARCH_SAP_MATERIAL_ID.name(), BigDecimal.valueOf(searchForm.getSapMaterialId()));
		fieldValueMap.put(PreferencesKey.SEARCH_SAREA_ID.name(), BigDecimal.valueOf(searchForm.getSareaId()));
		
		BigDecimal soldtoSapBpId = searchForm.getSoldtoSapBpId() == null ? null : 
			BigDecimal.valueOf(searchForm.getSoldtoSapBpId());
		fieldValueMap.put(PreferencesKey.SEARCH_SOLDTO_SAP_BP_ID.name(), soldtoSapBpId);
		
		fieldValueMap.put(PreferencesKey.SEARCH_POOL_ID.name(), BigDecimal.valueOf(searchForm.getPoolId()));
		fieldValueMap.put(PreferencesKey.SEARCH_EXEC_CARRIER_ID.name(), BigDecimal.valueOf(searchForm.getExecCarrierId()));
		
		fieldValueMap.put(PreferencesKey.SEARCH_PLANT_CD_LIST.name(), searchForm.getInPlantCdList());
		fieldValueMap.put(PreferencesKey.SEARCH_SHIPTO_SOFFICE_CD.name(), searchForm.getShiptoSofficeCd());
		
		BigDecimal shiptoSapBpId = searchForm.getShiptoSapBpId() == null ? null : 
			BigDecimal.valueOf(searchForm.getShiptoSapBpId());
		fieldValueMap.put(PreferencesKey.SEARCH_SHIPTO_SAP_BP_ID.name(), shiptoSapBpId);
		
		fieldValueMap.put(PreferencesKey.SEARCH_SHIPPINGPOINT_ID.name(), BigDecimal.valueOf(searchForm.getShippingpointId()));
		fieldValueMap.put(PreferencesKey.SEARCH_SHIPTO_SGROUP_CD.name(), searchForm.getShiptoSgroupCd());
		
		BigDecimal billtoSapBpId = searchForm.getBilltoSapBpId() == null ? null : 
			BigDecimal.valueOf(searchForm.getBilltoSapBpId());
		fieldValueMap.put(PreferencesKey.SEARCH_BILLTO_SAP_BP_ID.name(), billtoSapBpId);
		
		fieldValueMap.put(PreferencesKey.SEARCH_VEHICLE_COMPOSITE_ID.name(), searchForm.getVehicleCompositeId());
		fieldValueMap.put(PreferencesKey.SEARCH_VEHICLE_NAME.name(), searchForm.getVehicleName());
		fieldValueMap.put(PreferencesKey.SEARCH_LDS_DELIVERY_NOTE_NO.name(), searchForm.getLdsDeliveryNoteNo());
		
		fieldValueMap.put(PreferencesKey.SEARCH_W_ORDER_NUMBER.name(), BigDecimal.valueOf(searchForm.getwOrderNumber()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_DISTRIB_DEST_CD.name(), BigDecimal.valueOf(searchForm.getwDistributionDestCd()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_SOLDTO_NAME.name(), BigDecimal.valueOf(searchForm.getwSoldtoName1()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_SHIPTO_NAME.name(), BigDecimal.valueOf(searchForm.getwShiptoName1()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_SHIPTO_CITY.name(), BigDecimal.valueOf(searchForm.getwShiptoCity()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_STATUS.name(), BigDecimal.valueOf(searchForm.getwStatus()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_MATERIAL_TXT.name(), BigDecimal.valueOf(searchForm.getwMaterialTxt()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_ORDERED_QUANTITY.name(), BigDecimal.valueOf(searchForm.getwOrderedQuantity()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_ORDERED_UOM_TXT.name(), BigDecimal.valueOf(searchForm.getwOrderedUomTxt()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_LOADED_QUANTITY.name(), BigDecimal.valueOf(searchForm.getwLoadedQuantity()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_LOADED_UOM_TXT.name(), BigDecimal.valueOf(searchForm.getwLoadedUomTxt()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_RETURNED_QUANTITY.name(), BigDecimal.valueOf(searchForm.getwReturnedQuantity()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_RETURNED_UOM_TXT.name(), BigDecimal.valueOf(searchForm.getwReturnedUomTxt()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_DELIVERED_QUANTITY.name(), BigDecimal.valueOf(searchForm.getwDeliveredQuantity()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_DELIVERED_UOM_TXT.name(), BigDecimal.valueOf(searchForm.getwDeliveredUomTxt()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_DELIVERY_TYPE_TXT.name(), BigDecimal.valueOf(searchForm.getwDeliveryTypeTxt()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_SHIPPINGPOINT_NAME.name(), BigDecimal.valueOf(searchForm.getwShippingpointName()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_DELIVERY_FROM_DATE.name(), BigDecimal.valueOf(searchForm.getwDeliveryFromDat()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_DELIVERY_TO_DATE.name(), BigDecimal.valueOf(searchForm.getwDeliveryToDat()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_PLANNED_DEL_DATE.name(), BigDecimal.valueOf(searchForm.getwPlannedDeliveryDat()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_POOL_NAME.name(), BigDecimal.valueOf(searchForm.getwPoolName()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_COMM_CARRIER_NAME.name(), BigDecimal.valueOf(searchForm.getwCommCarrierName()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_EXEC_CARRIER_NAME.name(), BigDecimal.valueOf(searchForm.getwExecCarrierName()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_BILLTO_NAME.name(), BigDecimal.valueOf(searchForm.getwBilltoName1()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_DRIVER_FULL_NAME.name(), BigDecimal.valueOf(searchForm.getwDriverFullName()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_VEHICLE_NAME.name(), BigDecimal.valueOf(searchForm.getwVehicleName()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_ORDER_VALID_CODE.name(), BigDecimal.valueOf(searchForm.getwOrderValidationCode()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_LDS_DELIVERY_NOTE_NO.name(), BigDecimal.valueOf(searchForm.getwLdsDeliveryNoteNo()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_NOTE_DELIVERY.name(), BigDecimal.valueOf(searchForm.getwNoteDelivery()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_NOTE_DISPATCHING.name(), BigDecimal.valueOf(searchForm.getwNoteDispatching()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_NOTE_ORDER_MGMT.name(), BigDecimal.valueOf(searchForm.getwNoteOrderMgmt()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_NOTE_RAILWAY_DISP.name(), BigDecimal.valueOf(searchForm.getwNoteRailwayDisp()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_NOTE_TRUCK_DRIVER.name(), BigDecimal.valueOf(searchForm.getwNoteTruckDriver()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_ORDER_NUMBER_FROM_SEQ_USAGE.name(), BigDecimal.valueOf(searchForm.getwOrderNumberFromSeqUsage()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_LOADING_TS.name(), BigDecimal.valueOf(searchForm.getwLoadingTs()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_SHIPTO_ADDRESS.name(), BigDecimal.valueOf(searchForm.getwShiptoAddress()));
		fieldValueMap.put(PreferencesKey.SEARCH_W_ITRACK_STATUS_CD.name(), BigDecimal.valueOf(searchForm.getwItrackStatusCd()));

		fieldValueMap.put(PreferencesKey.SEARCH_I_ORDER_NUMBER.name(), BigDecimal.valueOf(searchForm.getIdxOrderNumber()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_DISTRIB_DEST_CD.name(), BigDecimal.valueOf(searchForm.getIdxDistributionDestCd()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_SOLDTO_NAME.name(), BigDecimal.valueOf(searchForm.getIdxSoldtoName1()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_SHIPTO_NAME.name(), BigDecimal.valueOf(searchForm.getIdxShiptoName1()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_SHIPTO_CITY.name(), BigDecimal.valueOf(searchForm.getIdxShiptoCity()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_STATUS.name(), BigDecimal.valueOf(searchForm.getIdxStatus()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_MATERIAL_TXT.name(), BigDecimal.valueOf(searchForm.getIdxMaterialTxt()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_ORDERED_QUANTITY.name(), BigDecimal.valueOf(searchForm.getIdxOrderedQuantity()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_ORDERED_UOM_TXT.name(), BigDecimal.valueOf(searchForm.getIdxOrderedUomTxt()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_LOADED_QUANTITY.name(), BigDecimal.valueOf(searchForm.getIdxLoadedQuantity()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_LOADED_UOM_TXT.name(), BigDecimal.valueOf(searchForm.getIdxLoadedUomTxt()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_RETURNED_QUANTITY.name(), BigDecimal.valueOf(searchForm.getIdxReturnedQuantity()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_RETURNED_UOM_TXT.name(), BigDecimal.valueOf(searchForm.getIdxReturnedUomTxt()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_DELIVERED_QUANTITY.name(), BigDecimal.valueOf(searchForm.getIdxDeliveredQuantity()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_DELIVERED_UOM_TXT.name(), BigDecimal.valueOf(searchForm.getIdxDeliveredUomTxt()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_DELIVERY_TYPE_TXT.name(), BigDecimal.valueOf(searchForm.getIdxDeliveryTypeTxt()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_SHIPPINGPOINT_NAME.name(), BigDecimal.valueOf(searchForm.getIdxShippingpointName()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_DELIVERY_FROM_DATE.name(), BigDecimal.valueOf(searchForm.getIdxDeliveryFromDat()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_DELIVERY_TO_DATE.name(), BigDecimal.valueOf(searchForm.getIdxDeliveryToDat()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_PLANNED_DEL_DATE.name(), BigDecimal.valueOf(searchForm.getIdxPlannedDeliveryDat()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_POOL_NAME.name(), BigDecimal.valueOf(searchForm.getIdxPoolName()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_COMM_CARRIER_NAME.name(), BigDecimal.valueOf(searchForm.getIdxCommCarrierName()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_EXEC_CARRIER_NAME.name(), BigDecimal.valueOf(searchForm.getIdxExecCarrierName()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_BILLTO_NAME.name(), BigDecimal.valueOf(searchForm.getIdxBilltoName1()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_DRIVER_FULL_NAME.name(), BigDecimal.valueOf(searchForm.getIdxDriverFullName()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_VEHICLE_NAME.name(), BigDecimal.valueOf(searchForm.getIdxVehicleName()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_ORDER_VALID_CODE.name(), BigDecimal.valueOf(searchForm.getIdxOrderValidationCode()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_LDS_DELIVERY_NOTE_NO.name(), BigDecimal.valueOf(searchForm.getIdxLdsDeliveryNoteNo()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_NOTE_DELIVERY.name(), BigDecimal.valueOf(searchForm.getIdxNoteDelivery()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_NOTE_DISPATCHING.name(), BigDecimal.valueOf(searchForm.getIdxNoteDispatching()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_NOTE_ORDER_MGMT.name(), BigDecimal.valueOf(searchForm.getIdxNoteOrderMgmt()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_NOTE_RAILWAY_DISP.name(), BigDecimal.valueOf(searchForm.getIdxNoteRailwayDisp()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_NOTE_TRUCK_DRIVER.name(), BigDecimal.valueOf(searchForm.getIdxNoteTruckDriver()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_LOADING_TS.name(), BigDecimal.valueOf(searchForm.getIdxLoadingTs()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_SHIPTO_ADDRESS.name(), BigDecimal.valueOf(searchForm.getIdxShiptoAddress()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_ORDER_NUMBER_FROM_SEQ_USAGE.name(), BigDecimal.valueOf(searchForm.getIdxOrderNumberFromSeqUsage()));
		fieldValueMap.put(PreferencesKey.SEARCH_I_ITRACK_STATUS_CD.name(), BigDecimal.valueOf(searchForm.getIdxItrackStatusCd()));
		
		return fieldValueMap;
	}
	
	//called when clicking Save button on Order Search or Delivery Monitoring/Monitoring List
	private List<PreferencesEntity> storeGuiValuesInEntity(Map<String, Object> fieldValueMap) {
		// [[
		List<PreferencesEntity> result = null;
		Object[][] params = {{"fieldValueMap", fieldValueMap}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "storeGuiValuesInEntity", params);
		}
		// ]]
		try {
			if (fieldValueMap == null) {
				return result;
			}
			
			final List<PreferencesEntity> preferencesList = new ArrayList<PreferencesEntity>();
			PreferencesEntity preferencesEntity;
			for (String field : fieldValueMap.keySet()) {
				Object fieldValue = fieldValueMap.get(field);
				preferencesEntity = new PreferencesEntity();
				preferencesEntity.setPreferenceKey(field);
				preferencesList.add(preferencesEntity);
				if (fieldValue != null) {
					if (fieldValue instanceof String) {
						preferencesEntity.setValStr((String)fieldValue);
					}
					else if (fieldValue instanceof BigDecimal) {
						preferencesEntity.setValNum((BigDecimal)fieldValue);
					}
					else if (fieldValue instanceof Boolean) {
						BigDecimal value = (Boolean)fieldValue ? BigDecimal.ONE : BigDecimal.ZERO;
						preferencesEntity.setValNum(value);
					}
					else if (fieldValue instanceof Date) {
						preferencesEntity.setValDat((Date)fieldValue);
					}
				}
				else {
					preferencesEntity.setValStr(null);
					preferencesEntity.setValNum(null);
					preferencesEntity.setValDat(null);
				}
			}
			return result = preferencesList;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	//called when loading default values of the fields of Order Search or Delivery Monitoring/Monitoring List screens from the database
	protected void populateForm(BestellungSearchForm searchForm, Locale locale, 
			List<OrderCreationTypeTxEntity> orderCreationTypeList, 
			List<OrderStatusTxEntity> orderStatusList, String screen) {
		// [[
		Object[][] params = {{"searchForm", searchForm},
							{"locale", locale},
							{"orderCreationTypeList", orderCreationTypeList},
							{"orderStatusList", orderStatusList},
							{"screen", screen}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "populateForm", params);
		}
		// ]]
		try {
			searchForm.clearDefaults();
			final UserService userService = getUserService();
			final BestellungService bestellungService = getBestellungService();
			
			final List<PreferencesEntity> preferencesList = userService.fetchPreferencesList(
					getUser(), screen);
			if (preferencesList != null && !preferencesList.isEmpty()) {
				populateFormWithPreferences(searchForm, locale, preferencesList, orderCreationTypeList, orderStatusList);
			}
			
			Set<Long> sapBpIds = new HashSet<Long>();
			if (searchForm.getSoldtoSapBpId() != null && searchForm.getSoldtoSapBpId() != 0) {
				sapBpIds.add(searchForm.getSoldtoSapBpId());
			}
			if (searchForm.getShiptoSapBpId() != null && searchForm.getShiptoSapBpId() != 0) {
				sapBpIds.add(searchForm.getShiptoSapBpId());
			}
			if (searchForm.getBilltoSapBpId() != null && searchForm.getBilltoSapBpId() != 0) {
				sapBpIds.add(searchForm.getBilltoSapBpId());
			}
			if (!sapBpIds.isEmpty()) {
				List<GeneralDisplayNameDto> sapBpDtoList = bestellungService.getSapBpDisplayTexts(sapBpIds);
				for (GeneralDisplayNameDto sapBpDto : sapBpDtoList) {
					if (sapBpDto.getId().equals(searchForm.getSoldtoSapBpId())) {
						searchForm.setSoldtoDto(sapBpDto);
					}
					if (sapBpDto.getId().equals(searchForm.getShiptoSapBpId())) {
						searchForm.setShiptoDto(sapBpDto);
					}
					if (sapBpDto.getId().equals(searchForm.getBilltoSapBpId())) {
						searchForm.setBilltoDto(sapBpDto);
					}
				}
			}
			if (searchForm.getSapMaterialId() != null && searchForm.getSapMaterialId() != 0) {
				GeneralDisplayNameDto sapMaterialDto = bestellungService.getSapMaterialDisplayTexts(
						searchForm.getSapMaterialId(), locale);
				searchForm.setSapMaterialDto(sapMaterialDto);
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
	 * Used to populate searchForm by the values coming from preferencesList.
	 * 
	 * @param searchForm
	 * @param locale
	 * @param preferencesList
	 * @param orderCreationTypeList
	 * @param orderStatusList
	 */
	protected void populateFormWithPreferences(ActionForm actionForm, 
			Locale locale, List<PreferencesEntity> preferencesList, 
			List<OrderCreationTypeTxEntity> orderCreationTypeList, 
			List<OrderStatusTxEntity> orderStatusList) {
				
		BestellungSearchForm searchForm = (BestellungSearchForm)actionForm;
		Map<String,Object> preferencesMap = UserPreferenceUtil.createPreferencesMapfromList(preferencesList);
		
		searchForm.setApplyUserDefaultSettings(true);

		final Date defaultDeliveryFromDate = (Date)preferencesMap.get(PreferencesKey.SEARCH_DELIVERY_FROM_DATE.name());
		final String defaultDeliveryFromDateString = DateTimeFormat.asLogonDayMonthYearInGMTZone(locale,
                defaultDeliveryFromDate);
		searchForm.setDefaultDeliveryFromDateString(defaultDeliveryFromDateString);

		final Date defaultDeliveryToDate = (Date)preferencesMap.get(PreferencesKey.SEARCH_DELIVERY_TO_DATE.name());
		final String defaultDeliveryToDateString = DateTimeFormat.asLogonDayMonthYearInGMTZone(locale,
                defaultDeliveryToDate);
		searchForm.setDefaultDeliveryToDateString(defaultDeliveryToDateString);


		final String defaultOrderStatusesObject = UserPreferenceUtil.getString(preferencesMap,PreferencesKey.SEARCH_ORDER_STATUS_LIST);
		if (defaultOrderStatusesObject != null && orderStatusList != null && !orderStatusList.isEmpty()) {
			final String defaultOrderStatusesAsString = (String) defaultOrderStatusesObject;

			if (!StringUtils.isEmpty(defaultOrderStatusesAsString)) {
				String[] defaultOrderStatuses = StringUtils.split(defaultOrderStatusesAsString, ",");
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

		final String defaultOrderCreationTypesAsObject = UserPreferenceUtil.getString(preferencesMap,PreferencesKey.SEARCH_ORDER_CREATION_TYPE);
		if (defaultOrderCreationTypesAsObject != null && orderCreationTypeList != null
				&& !orderCreationTypeList.isEmpty()) {
			final String defaultOrderCreationTypesAsString = (String) defaultOrderCreationTypesAsObject;

			if (!StringUtils.isEmpty(defaultOrderCreationTypesAsString)) {
				final String[] defaultOrderCreationTypes = StringUtils.split(
						defaultOrderCreationTypesAsString, ",");
				List<OrderCreationTypeTxEntity> defaultOrderCreationTypeList = null;
				if (defaultOrderCreationTypes != null && defaultOrderCreationTypes.length > 0) {
					defaultOrderCreationTypeList = new ArrayList<OrderCreationTypeTxEntity>();
					for (String defaultOrderCreationTypeString : defaultOrderCreationTypes) {
						for (OrderCreationTypeTxEntity tx : orderCreationTypeList) {
							long defaultOrderCreationType = Long.parseLong(defaultOrderCreationTypeString);
							long typeCode = tx.getOrderCreationTypeTxPK().getOrderCreationTypeBs().getOrderCreationTypeCd();
							if (defaultOrderCreationType == typeCode) {
								defaultOrderCreationTypeList.add(tx);
								break;
							}
						}
					}
				}
				searchForm.setDefaultOrderCreationTypeList(defaultOrderCreationTypeList);
			}
		}

		final String defaultDeliveryTypeCdsObject = UserPreferenceUtil.getString(preferencesMap,PreferencesKey.SEARCH_DELIVERY_TYPE_CD_LIST);
		if (defaultDeliveryTypeCdsObject != null) {
			final String defaultDeliveryTypeCdsAsString = (String) defaultDeliveryTypeCdsObject;
			if (!StringUtils.isEmpty(defaultDeliveryTypeCdsAsString)) {
				final String[] defaultDeliveryTypeCds = StringUtils.split(
						defaultDeliveryTypeCdsAsString, ",");
				List<Long> defaultDeliveryTypeCdList = null;
				if (defaultDeliveryTypeCds != null && defaultDeliveryTypeCds.length > 0) {
					defaultDeliveryTypeCdList = new ArrayList<Long>();
					for (String defaultDeliveryTypeString : defaultDeliveryTypeCds) {
						long defaultDeliveryType = Long.parseLong(defaultDeliveryTypeString);
						defaultDeliveryTypeCdList.add(defaultDeliveryType);
					}
				}
				searchForm.setDefaultDeliveryTypeCdList(defaultDeliveryTypeCdList);
			}
		}

		final String defaultDestinationCdsObject = UserPreferenceUtil.getString(preferencesMap,PreferencesKey.SEARCH_DESTINATION_CD_LIST);
		if (defaultDestinationCdsObject != null) {
			final String defaultDestinationCdsAsString = (String) defaultDestinationCdsObject;
			if (!StringUtils.isEmpty(defaultDestinationCdsAsString)) {
				final String[] defaultDestinationCds = StringUtils.split(
						defaultDestinationCdsAsString, ",");
				List<String> defaultDestinationCdList = null;
				if (defaultDestinationCds != null && defaultDestinationCds.length > 0) {
					defaultDestinationCdList = new ArrayList<String>();
					for (String defaultDeliveryTypeString : defaultDestinationCds) {
						defaultDestinationCdList.add(defaultDeliveryTypeString);
					}
				}
				searchForm.setDefaultDestinationCdList(defaultDestinationCdList);
			}
		}
		
		final String defaultCarrierIdsAsString = UserPreferenceUtil.getString(preferencesMap,PreferencesKey.SEARCH_CARRIER_ID_LIST);
		if (!StringUtils.isEmpty(defaultCarrierIdsAsString)) {
			final String[] defaultCarrierIds = StringUtils.split(
					defaultCarrierIdsAsString, ",");
			List<String> defaultCarrierIdList = null;
			if (defaultCarrierIds != null && defaultCarrierIds.length > 0) {
				defaultCarrierIdList = new ArrayList<String>();
				for (String defaultCarrierIdString : defaultCarrierIds) {
					defaultCarrierIdList.add(defaultCarrierIdString);
				}
			}
			searchForm.setDefaultCarrierIdList(defaultCarrierIdList);
		}
		
		final String defaultPlantCdsAsString = UserPreferenceUtil.getString(preferencesMap,PreferencesKey.SEARCH_PLANT_CD_LIST);
		if (!StringUtils.isEmpty(defaultPlantCdsAsString)) {
			final String[] defaultPlantCds = StringUtils.split(
					defaultPlantCdsAsString, ",");
			List<String> defaultPlantCdList = null;
			if (defaultPlantCds != null && defaultPlantCds.length > 0) {
				defaultPlantCdList = new ArrayList<String>();
				for (String defaultPlantCdString : defaultPlantCds) {
					defaultPlantCdList.add(defaultPlantCdString);
				}
			}
			searchForm.setDefaultPlantCdList(defaultPlantCdList);
		}
		
		searchForm.setLiveFlag(UserPreferenceUtil.getBoolean(preferencesMap, PreferencesKey.SEARCH_LIVE_FLAG));
		searchForm.setMatPackagingType(UserPreferenceUtil.getString(preferencesMap, PreferencesKey.SEARCH_MAT_PACKAGING_TYPE));
		searchForm.setSapMaterialId(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_SAP_MATERIAL_ID));
		searchForm.setSareaId(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_SAREA_ID));
		searchForm.setSoldtoSapBpId(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_SOLDTO_SAP_BP_ID));
		searchForm.setPoolId(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_POOL_ID));
		searchForm.setExecCarrierId(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_EXEC_CARRIER_ID));
		searchForm.setShiptoSofficeCd(UserPreferenceUtil.getString(preferencesMap, PreferencesKey.SEARCH_SHIPTO_SOFFICE_CD));
		searchForm.setShiptoSapBpId(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_SHIPTO_SAP_BP_ID));
		searchForm.setShippingpointId(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_SHIPPINGPOINT_ID));
		searchForm.setShiptoSgroupCd(UserPreferenceUtil.getString(preferencesMap, PreferencesKey.SEARCH_SHIPTO_SGROUP_CD));
		searchForm.setBilltoSapBpId(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_BILLTO_SAP_BP_ID));
		searchForm.setVehicleCompositeId(UserPreferenceUtil.getString(preferencesMap, PreferencesKey.SEARCH_VEHICLE_COMPOSITE_ID));
		searchForm.setVehicleName(UserPreferenceUtil.getString(preferencesMap, PreferencesKey.SEARCH_VEHICLE_NAME));
		searchForm.setLdsDeliveryNoteNo(UserPreferenceUtil.getString(preferencesMap, PreferencesKey.SEARCH_LDS_DELIVERY_NOTE_NO));
		
		//todo orsika 06.05.2013 use map instead of separate variables
//			HashMap<String, Long> columnWidthMap = new HashMap<String, Long>();
//			columnWidthMap.put("orderNumber", UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_ORDER_NUMBER));
//			searchForm.setColumnWidthMap(columnWidthMap);
		
		searchForm.setwOrderNumber(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_ORDER_NUMBER));
		searchForm.setwDistributionDestCd(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_DISTRIB_DEST_CD));
		searchForm.setwSoldtoName1(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_SOLDTO_NAME));
		searchForm.setwShiptoName1(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_SHIPTO_NAME));
		searchForm.setwShiptoCity(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_SHIPTO_CITY));
		searchForm.setwStatus(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_STATUS));
		searchForm.setwMaterialTxt(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_MATERIAL_TXT));
		searchForm.setwOrderedQuantity(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_ORDERED_QUANTITY));
		searchForm.setwOrderedUomTxt(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_ORDERED_UOM_TXT));
		searchForm.setwLoadedQuantity(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_LOADED_QUANTITY));
		searchForm.setwLoadedUomTxt(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_LOADED_UOM_TXT));
		searchForm.setwReturnedQuantity(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_RETURNED_QUANTITY));
		searchForm.setwReturnedUomTxt(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_RETURNED_UOM_TXT));
		searchForm.setwDeliveredQuantity(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_DELIVERED_QUANTITY));
		searchForm.setwDeliveredUomTxt(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_DELIVERED_UOM_TXT));
		searchForm.setwDeliveryTypeTxt(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_DELIVERY_TYPE_TXT));
		searchForm.setwShippingpointName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_SHIPPINGPOINT_NAME));
		searchForm.setwDeliveryFromDat(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_DELIVERY_FROM_DATE));
		searchForm.setwDeliveryToDat(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_DELIVERY_TO_DATE));
		searchForm.setwPlannedDeliveryDat(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_PLANNED_DEL_DATE));
		searchForm.setwPoolName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_POOL_NAME));
		searchForm.setwCommCarrierName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_COMM_CARRIER_NAME));
		searchForm.setwExecCarrierName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_EXEC_CARRIER_NAME));
		searchForm.setwBilltoName1(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_BILLTO_NAME));
		searchForm.setwDriverFullName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_DRIVER_FULL_NAME));
		searchForm.setwVehicleName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_VEHICLE_NAME));
		searchForm.setwOrderValidationCode(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_ORDER_VALID_CODE));
		searchForm.setwLdsDeliveryNoteNo(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_LDS_DELIVERY_NOTE_NO));
		searchForm.setwNoteDelivery(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_NOTE_DELIVERY));
		searchForm.setwNoteDispatching(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_NOTE_DISPATCHING));
		searchForm.setwNoteOrderMgmt(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_NOTE_ORDER_MGMT));
		searchForm.setwNoteRailwayDisp(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_NOTE_RAILWAY_DISP));
		searchForm.setwNoteTruckDriver(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_NOTE_TRUCK_DRIVER));
		searchForm.setwLoadingTs(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_LOADING_TS));
		searchForm.setwShiptoAddress(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_SHIPTO_ADDRESS));
		searchForm.setwOrderNumberFromSeqUsage(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_ORDER_NUMBER_FROM_SEQ_USAGE));
		searchForm.setwItrackStatusCd(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_W_ITRACK_STATUS_CD));
		
		
		searchForm.setIdxOrderNumber(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_ORDER_NUMBER));
		searchForm.setIdxDistributionDestCd(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_DISTRIB_DEST_CD));
		searchForm.setIdxSoldtoName1(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_SOLDTO_NAME));
		searchForm.setIdxShiptoName1(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_SHIPTO_NAME));
		searchForm.setIdxShiptoCity(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_SHIPTO_CITY));
		searchForm.setIdxStatus(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_STATUS));
		searchForm.setIdxMaterialTxt(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_MATERIAL_TXT));
		searchForm.setIdxOrderedQuantity(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_ORDERED_QUANTITY));
		searchForm.setIdxOrderedUomTxt(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_ORDERED_UOM_TXT));
		searchForm.setIdxLoadedQuantity(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_LOADED_QUANTITY));
		searchForm.setIdxLoadedUomTxt(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_LOADED_UOM_TXT));
		searchForm.setIdxReturnedQuantity(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_RETURNED_QUANTITY));
		searchForm.setIdxReturnedUomTxt(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_RETURNED_UOM_TXT));
		searchForm.setIdxDeliveredQuantity(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_DELIVERED_QUANTITY));
		searchForm.setIdxDeliveredUomTxt(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_DELIVERED_UOM_TXT));
		searchForm.setIdxDeliveryTypeTxt(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_DELIVERY_TYPE_TXT));
		searchForm.setIdxShippingpointName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_SHIPPINGPOINT_NAME));
		searchForm.setIdxDeliveryFromDat(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_DELIVERY_FROM_DATE));
		searchForm.setIdxDeliveryToDat(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_DELIVERY_TO_DATE));
		searchForm.setIdxPlannedDeliveryDat(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_PLANNED_DEL_DATE));
		searchForm.setIdxPoolName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_POOL_NAME));
		searchForm.setIdxCommCarrierName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_COMM_CARRIER_NAME));
		searchForm.setIdxExecCarrierName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_EXEC_CARRIER_NAME));
		searchForm.setIdxBilltoName1(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_BILLTO_NAME));
		searchForm.setIdxDriverFullName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_DRIVER_FULL_NAME));
		searchForm.setIdxVehicleName(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_VEHICLE_NAME));
		searchForm.setIdxOrderValidationCode(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_ORDER_VALID_CODE));
		searchForm.setIdxLdsDeliveryNoteNo(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_LDS_DELIVERY_NOTE_NO));
		searchForm.setIdxNoteDelivery(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_NOTE_DELIVERY));
		searchForm.setIdxNoteDispatching(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_NOTE_DISPATCHING));
		searchForm.setIdxNoteOrderMgmt(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_NOTE_ORDER_MGMT));
		searchForm.setIdxNoteRailwayDisp(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_NOTE_RAILWAY_DISP));
		searchForm.setIdxNoteTruckDriver(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_NOTE_TRUCK_DRIVER));
		searchForm.setIdxLoadingTs(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_LOADING_TS));
		searchForm.setIdxShiptoAddress(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_SHIPTO_ADDRESS));
		searchForm.setIdxOrderNumberFromSeqUsage(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_ORDER_NUMBER_FROM_SEQ_USAGE));
		searchForm.setIdxItrackStatusCd(UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.SEARCH_I_ITRACK_STATUS_CD));
	}
	
	/**
	 * Sets the HourOfDay/Minute/Second properties to 0/0/0
	 */
	protected void setToStartOfDay(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
	}
	
	/**
	 * Sets the HourOfDay/Minute/Second properties to 23/59/59
	 */
	protected void setToEndOfDay(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
	}
	
	protected void addSuccessFullySavedMessageForAjaxResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String successMessage = MessageUtils.getMessage(request,
				request.getLocale(), SUCCESSFULLY_SAVED_MESSAGE_KEY);
		
		Map<String, String> resultMessages = new HashMap<String, String>();
		resultMessages.put("success", successMessage);
		
		JSONSerializer jsonSerializer = new JSONSerializer();
		String jsonString =  jsonSerializer.serialize(resultMessages);
		
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(jsonString);
	}
}

