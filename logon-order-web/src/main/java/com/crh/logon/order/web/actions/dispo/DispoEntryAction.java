/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.dispo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.PoolAdminService;
import com.crh.logon.business.services.interfaces.UserService;
import com.crh.logon.business.services.utils.DispoCarrierWrapper;
import com.crh.logon.business.services.utils.PreferencesKey;
import com.crh.logon.business.services.utils.UserPreferenceUtil;
import com.crh.logon.constants.Constant;
import com.crh.logon.order.web.forms.dispo.DispoForm;
import com.crh.logon.persistence.entities.admin.PreferencesEntity;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusTxEntity;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.DropdownEntity;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.PlantSareaUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_dispo_DispoForm"
 *  path="/dispo/DispoEntry" scope="session"
 *  validate="false"
 * 
 * @struts.action-forward name="success" path="DispoTile2.def"
 * @struts.action-forward name="initfailed" path="/dispokarte/DispoEntry.do"
 */
public class DispoEntryAction extends OrderActionSupport {
	
	Long[] STATUS_LIST_WORKFLOW = {
			Constant.ORDER_STATUS_DISPATCHED,
			Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER,
			Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK,
			Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT,
			Constant.ORDER_STATUS_LOADING,
			Constant.ORDER_STATUS_LOADED,
			Constant.ORDER_STATUS_SHIPPING,
			Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT,
			Constant.ORDER_STATUS_UNLOADING,
			Constant.ORDER_STATUS_DELIVERED_PENDING_LDS_DATA,
			Constant.ORDER_STATUS_DELIVERED,
			Constant.ORDER_STATUS_FINISHED
			};

	private static final JcanLogger LOG =
		JcanLoggerFactory.getLogger(DispoEntryAction.class);

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
			Authorization.authorize(Authorization.DISPATCHING_R);

			setSelectedTab("dispo", request);
			
			String forwardName = "success";
			
			// Get the struts form.
			DispoForm helpForm = (DispoForm) form;
			
			PoolAdminService poolAdminService = getServiceLocator().getPoolAdminService();
			
			if (helpForm.getStartHour() < 0){
				helpForm.setStartHour(getConfigBean().getAppDefaultDispoStartHour());
			}
			
			if (helpForm.getHeightFactorReal() < 0 || helpForm.getHeightFactorPlanning() < 0){
				helpForm.setHeightFactor((float)getConfigBean().getAppDefaultDispoHeightFactor());
			}
			
			if (helpForm.getMaxNrOfSelectableAssignments() < 0) {
				helpForm.setMaxNrOfSelectableAssignments(getConfigBean().getAppDefaultDispoMaxNrOfSelectableAssignments());
			}
			
			if (helpForm.getPools() == null) {
				List<PoolTxEntity> pools = poolAdminService.fetchTruckPoolsWhereCarrierExists(request.getLocale());
				helpForm.setPools(pools);
			}
			if (helpForm.getPools() == null || helpForm.getPools().isEmpty()) {
				/*
				 * If there are no pools to choose from (eg. because of data authorization), the screen could not be used.
				 * (A warning message describes the situation for the user.)
				 */
				MessageUtils.addActionWarning(request, "dispo.texte.nopoolsorcarriers");
			}		
			if (helpForm.getAllPools() == null) {
				boolean disableAuthorization = false;
				List<PoolTxEntity> assignedPools = poolAdminService.fetchPoolTxEntitiesByTransportationMode(request.getLocale(), Constant.TRANSPORTATION_MODE_TRUCK, disableAuthorization);
				helpForm.setAllPools(assignedPools);
			}
			if (helpForm.getDeliveringPlants() == null) {
				final List<DropdownEntity> deliveringPlants = PlantSareaUtils.getPlantList(getPlantSareaService());
				helpForm.setDeliveringPlants(deliveringPlants);
			}
			if (helpForm.getOrderStatusList() == null) {
				final List<OrderStatusTxEntity> statusList = buildValidStatusSelectList(request.getLocale());
				helpForm.setOrderStatusList(statusList);
			}
			
			if (!helpForm.isApplyUserDefaultSettings()) {
				copyPreferencesToForm(helpForm, request.getLocale());
			}

			helpForm.setUserTimeZone(getUserTimezone());
			if (helpForm.getDate() == null) {
				helpForm.setDate(getUserTimezone().today());
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
	
	/**
	 * Builds the OrderStatusTxEntity list for the UI filter
	 * @param locale
	 * @return
	 */
	private List<OrderStatusTxEntity> buildValidStatusSelectList(Locale locale) {

		//visible anyway
		List<Long> requestStatuses = new ArrayList<Long>();

		requestStatuses.addAll(Arrays.asList(STATUS_LIST_WORKFLOW));
		
		return getBestellungService().getStatusList(requestStatuses, locale.getLanguage());
	}
	
	/**
	 * Loads the user preferences and sets the struts form values based on them.
	 * 
	 * @param dispoForm - The struts form instance.
	 * @param locale - The locale of the user.
	 */
	private void copyPreferencesToForm(DispoForm dispoForm, Locale locale) {
		dispoForm.clearDefaults();
		
		final UserService userService = getUserService();
		final List<PreferencesEntity> preferencesList = userService.fetchPreferencesList(getUser(),
				Constant.USER_PREFERENCE_SCREEN_TRUCK_SCHEDULING);
		saveDefaultValuesToForm(dispoForm, locale, preferencesList);		
	}
	
	/**
	 * Uses the user preferences and to set the struts form values based.
	 * 
	 * @param dispoForm - The struts form instance.
	 * @param locale - The locale of the user.
	 * @param preferencesMap - The map which containes the user prefernce key-value pairs.
	 */
	private void saveDefaultValuesToForm(DispoForm dispoForm, Locale locale,
			List<PreferencesEntity> preferencesList) {
		
		Map<String,Object> preferencesMap = UserPreferenceUtil.createPreferencesMapfromList(preferencesList);
		
		dispoForm.setApplyUserDefaultSettings(true);
		
		final List<PoolTxEntity> pools = dispoForm.getPools();
		
		// Setting the selected pool
		final Integer defaultSelectedPool = UserPreferenceUtil.getInteger(preferencesMap, PreferencesKey.DISPO_POOL_INDEX);
		
		// Should be a valid pool index
		if (defaultSelectedPool != null && defaultSelectedPool > 0 && defaultSelectedPool < pools.size()) {
			dispoForm.setSelectedPool(defaultSelectedPool.intValue());
		}
		else {
			dispoForm.setSelectedPool(0);
		}
		final PoolTxEntity selectedPoolTxEntity = pools.get(dispoForm.getSelectedPool());
		final Long poolId = selectedPoolTxEntity.getPoolTxPK().getPoolId();
		
		 
		// Loading the carriers based on the selected pool
		final List<DispoCarrierWrapper> carriers = getServiceLocator().getDispoService().fetchCarriersForPool(poolId);
		dispoForm.setCarriers(carriers);
		
		// Setting the selected carrier: either from user setting or none of them selected.
		final Set<Long> selectedCarriers = dispoForm.getSelectedCarriers();
		final Object selectedCarriersJoined = preferencesMap.get(PreferencesKey.DISPO_CARRIER_ID_LIST.name());
		if (selectedCarriersJoined != null) {
			final String selectedCarriersJoinedString = (String) selectedCarriersJoined;
			if (!StringUtils.isEmpty(selectedCarriersJoinedString)) {
				final String[] defaultCarriers = StringUtils.split(
						selectedCarriersJoinedString, ",");
				if (defaultCarriers != null && defaultCarriers.length > 0) {
					for (String defaultCarrierString : defaultCarriers) {
						final Long defaultCarrierId = Long.parseLong(defaultCarrierString);
						// Only those user selected carriers are copnsidered valid, which are in the carriers list.
						if (isCarrierInCarrierList(defaultCarrierId, carriers)) {
							selectedCarriers.add(defaultCarrierId);
						}
					}
				}
			}
		}
		
		dispoForm.initSelectedCarriersCache();
		
		final Integer defaultTimegrid = UserPreferenceUtil.getInteger(preferencesMap, PreferencesKey.DISPO_TIMEGRID);
		if (defaultTimegrid != null) {
			dispoForm.setTimegrid(defaultTimegrid.intValue());
		}
		else if (dispoForm.getTimegrid() < 0){
			dispoForm.setTimegrid(getConfigBean().getAppDefaultDispoTimegrid());
		}
		
		final Integer defaultHourSpan = UserPreferenceUtil.getInteger(preferencesMap, PreferencesKey.DISPO_RANGE);
		
		if (defaultHourSpan != null) {
			dispoForm.setHourSpan(defaultHourSpan.intValue());
		}
		else if (dispoForm.getHourSpan() < 0) {
			dispoForm.setHourSpan(getConfigBean().getAppDefaultDispoTimeRange());
		}
		
		final List<DropdownEntity> deliveringPlants = dispoForm.getDeliveringPlants();
		final Set<String> selectedDeliveringPlants = dispoForm.getSelectedDeliveringPlants();
		final String selectedPlantCdsJoined = UserPreferenceUtil.getString(preferencesMap,PreferencesKey.DISPO_PLANTS_CD_LIST);
		if (selectedPlantCdsJoined != null) {
			final String selectedPlantCdsJoinedString = (String) selectedPlantCdsJoined;
			if (!StringUtils.isEmpty(selectedPlantCdsJoinedString)) {
				final String[] defaultSelectedPlantsArray = StringUtils.split(
						selectedPlantCdsJoinedString, ",");
				if (defaultSelectedPlantsArray != null && defaultSelectedPlantsArray.length > 0) {
					for (String plantCd : defaultSelectedPlantsArray) {
						// Only those user selected plants are considere valid, whiich are in the deliveringPlants list.
						if (isPlantInPlantList(plantCd, deliveringPlants)) {
							selectedDeliveringPlants.add(plantCd);
						}
					}
				}
			}
		}
		else {
			// Select all delivering plants by default
			for (DropdownEntity deliveringPlant: deliveringPlants) {
				selectedDeliveringPlants.add(deliveringPlant.getValue());
			}
		}

		final List<OrderStatusTxEntity> orderStatusList = dispoForm.getOrderStatusList();
		final Set<Long> selectedDeliveryStatuses = dispoForm.getSelectedOrderStatuses();
		final String selectedDeliveryStatusesJoined = UserPreferenceUtil.getString(preferencesMap,PreferencesKey.DISPO_ORDER_STATUS_CD_LIST);
		if (selectedDeliveryStatusesJoined != null) {
			final String selectedDeliveryStatusesJoinedString = (String) selectedDeliveryStatusesJoined;
			if (!StringUtils.isEmpty(selectedDeliveryStatusesJoinedString)) {
				final String[] defaultDeliveryStatusesArray = StringUtils.split(
						selectedDeliveryStatusesJoinedString, ",");
				if (defaultDeliveryStatusesArray != null && defaultDeliveryStatusesArray.length > 0) {
					for (String deliveryStatusCd : defaultDeliveryStatusesArray) {
						final Long deliveryStatusCdLong = Long.parseLong(deliveryStatusCd);
						// Only those user selected order statuses are considere valid, whiich are in the orderStatusList list.
						if (isOrderStatusCdInOrderStatusList(deliveryStatusCdLong, orderStatusList)) {
							selectedDeliveryStatuses.add(deliveryStatusCdLong);
						}
					}
				}
			}
		}
		else {
			// Select all order delivery statuses by default
			for (OrderStatusTxEntity orderStatusTxEntity : orderStatusList) {
				final Long statusCd = orderStatusTxEntity.getOrderStatusTxPK().getOrderStatusBs().getOrderStatusCd();
				selectedDeliveryStatuses.add(statusCd);
			}
		}
		
		final Boolean defaultHidePlanningTrucks = UserPreferenceUtil.getBoolean(preferencesMap, PreferencesKey.DISPO_HIDE_PLANNING_TRUCKS);
		if (defaultHidePlanningTrucks != null) {
			dispoForm.setHidePlanningTrucks(defaultHidePlanningTrucks.booleanValue());
		}
		final Boolean defaultHideRealTrucks = UserPreferenceUtil.getBoolean(preferencesMap, PreferencesKey.DISPO_HIDE_REAL_TRUCKS);
		if (defaultHideRealTrucks != null) {
			dispoForm.setHideRealTrucks(defaultHideRealTrucks.booleanValue());
		}
		final Boolean defaultHideInactiveTrucks = UserPreferenceUtil.getBoolean(preferencesMap, PreferencesKey.DISPO_HIDE_INACTIVE_TRUCKS);
		if (defaultHideInactiveTrucks != null) {
			dispoForm.setHideInactiveTrucks(defaultHideInactiveTrucks.booleanValue());
		}
		final Boolean defaultHideTrucksWithoutDeliveries = UserPreferenceUtil.getBoolean(preferencesMap, PreferencesKey.DISPO_HIDE_TRUCKS_WITHOUT_DELIVERIES);
		if (defaultHideTrucksWithoutDeliveries != null) {
			dispoForm.setHideTrucksWithoutDeliveries(defaultHideTrucksWithoutDeliveries.booleanValue());
		}
		final Boolean defaultHideScheduledCustomerOrders =  UserPreferenceUtil.getBoolean(preferencesMap, PreferencesKey.DISPO_HIDE_SCHEDULED_CUSTORDERS);
		if (defaultHideScheduledCustomerOrders != null) {
			dispoForm.setHideScheduledCustorders(defaultHideScheduledCustomerOrders.booleanValue());
		}
		final Boolean defaultHideClosedCustomerOrders =  UserPreferenceUtil.getBoolean(preferencesMap, PreferencesKey.DISPO_HIDE_CLOSED_CUSTORDERS);
		if (defaultHideClosedCustomerOrders != null) {
			dispoForm.setHideClosedCustorders(defaultHideClosedCustomerOrders.booleanValue());
		}
		final Boolean defaultHideCustomerHeaders =  UserPreferenceUtil.getBoolean(preferencesMap, PreferencesKey.DISPO_HIDE_CUSTORDER_HEADERS);
		if (defaultHideCustomerHeaders != null) {
			dispoForm.setHideCustorderHeaders(defaultHideCustomerHeaders.booleanValue());
		}
		
		final Integer defaultVisualFilter = UserPreferenceUtil.getInteger(preferencesMap, PreferencesKey.DISPO_VISUAL_FILTER);
		if (defaultVisualFilter != null) {
			dispoForm.setSelectedFilterForVisual(defaultVisualFilter.intValue());
		}
		final Boolean defaultHideTrucksAndOrderOfPartners = UserPreferenceUtil.getBoolean(preferencesMap, PreferencesKey.DISPO_HIDE_TRUCKS_AND_ORDER_OF_PARTNERS);
		if (defaultHideTrucksAndOrderOfPartners != null) {
			dispoForm.setHideTrucksAndOrderOfPartners(defaultHideTrucksAndOrderOfPartners.booleanValue());
		}
		
		final Long defaultTruckPriority = UserPreferenceUtil.getLong(preferencesMap, PreferencesKey.DISPO_TRUCK_PRIORITY_FILTER);
		if (defaultTruckPriority != null) {
			dispoForm.setTruckPriority(String.valueOf(defaultTruckPriority));
		}
	}
	
	private boolean isCarrierInCarrierList(Long carrierId, List<DispoCarrierWrapper> carriers) {
		if (carrierId == null || carriers == null || carriers.isEmpty()) {
			return false;
		}
		else {
			for (DispoCarrierWrapper dispoCarrierWrapper : carriers) {
				if (dispoCarrierWrapper.getCarrierId().equals(carrierId)) {
					return true;
				}
			}
			return false;
		}
	}
	
	private boolean isPlantInPlantList(String plantCd, List<DropdownEntity> deliveringPlants) {
		if (plantCd == null || deliveringPlants == null || deliveringPlants.isEmpty()) {
			return false;
		}
		else {
			for (DropdownEntity dropdownEntity : deliveringPlants) {
				if (dropdownEntity.getValue().equals(plantCd)) {
					return true;
				}
			}
			return false;
		}
	}
	
	private boolean isOrderStatusCdInOrderStatusList(Long orderStatusCd,
			List<OrderStatusTxEntity> orderStatusList) {
		if (orderStatusCd == null || orderStatusList == null || orderStatusList.isEmpty()) {
			return false;
		}
		else {
			for (OrderStatusTxEntity orderStatusTxEntity : orderStatusList) {
				if (orderStatusTxEntity.getOrderStatusTxPK().getOrderStatusBs().getOrderStatusCd().equals(
						orderStatusCd)) {
					return true;
				}
			}
			return false;
		}
	}
}
