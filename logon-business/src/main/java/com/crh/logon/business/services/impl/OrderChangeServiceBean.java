/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.text.SimpleDateFormat;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Expression;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.JPAQuery;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.gdi.jpa.filter.SelectExpression;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.beans.LoadShippingOrdersBean;
import com.crh.logon.business.services.beans.ShippingOrderLoadBean;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.utils.BizEventUtil;
import com.crh.logon.business.services.utils.OrderChangeConstants;
import com.crh.logon.business.services.utils.OrderStatistic;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.beans.ShippingOrderFetchListBean;
import com.crh.logon.business.services.beans.OrderChangeLogic;
import com.crh.logon.business.services.dispatchers.OrderChangeServiceDispatcher;
import com.crh.logon.business.services.localinterfaces.BestellungServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.ConfigServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.MonitoringTablesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderChangeServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TimeZoneServiceLocalInterface;
import com.crh.logon.business.services.utils.DbUtils;
import com.crh.logon.business.services.utils.SchedulingUtils;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.authorization.Authorizer;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.LdpointMatRestrEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.OrderOutputdocEntity;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusTxEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialAltUomEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.OrdItemAssignmtLoadEntity;
import com.crh.logon.persistence.entities.views.OrdLoadSchedInfoEntity;
import com.crh.logon.persistence.entities.views.ShippBimodalRoadRailEntity;
import com.crh.logon.persistence.entities.views.ShippingStatEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.WagonNumberUtils;

/**
 * Used from AuftragBearbeitungForm, EntryAction.
 * 
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class OrderChangeServiceBean extends OrderChangeServiceDispatcher implements OrderChangeServiceLocalInterface {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(OrderChangeServiceBean.class);
	
	@EJB private ConfigServiceLocalInterface configService;
    @EJB private DispoServiceLocalInterface dispoService; 	
    @EJB private BestellungServiceLocalInterface bestellungService;
	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;
	@EJB private OrderServiceLocalInterface orderService;
	@EJB private MonitoringTablesServiceLocalInterface monitoringTablesService;
	@EJB private TimeZoneServiceLocalInterface timeZoneService;

    
	private final static Long[] FULLFILLED_STATUS_CODES = {	
		Constant.ORDER_STATUS_LOADED,
		Constant.ORDER_STATUS_SHIPPING,
		Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT,
		Constant.ORDER_STATUS_UNLOADING,
		Constant.ORDER_STATUS_DELIVERED,
		Constant.ORDER_STATUS_FINISHED,
		Constant.ORDER_STATUS_DELETED
	};
	
	
	/**
	 * Loads all visible orders for loading screen's grid.
	 */
	public LoadShippingOrdersBean loadShippingOrders(int start, int count,
													 String selectedTransportCat, Long selectedShippingPointId,
													 List<Long> selectedStatusList, boolean sortAscending, String orderByColumn,
													 String languageCd, String selectedPlantCd, Date fromDate, Date toDate,
													 String searchText, List<Long> assignmentIdList, String searchOrderNo,
													 Locale userLocale, TimeZone userTimeZone,
													 Map<Long, List<LdpointMatRestrEntity>> orderLoadingPointListMapping,
													 Map<Long, ShippBimodalRoadRailEntity> bimodalRoadAndRelatedRailInfo) {
		// [[
		LoadShippingOrdersBean result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"selectedTransportCat", selectedTransportCat},
							{"selectedShippingPointId", selectedShippingPointId},
							{"selectedStatusList", selectedStatusList},
							{"sortAscending", sortAscending + ""},
							{"orderByColumn", orderByColumn},
							{"languageCd", languageCd},
							{"selectedPlantCd", selectedPlantCd},
							{"fromDate", fromDate},
							{"toDate", toDate},
							{"searchText", searchText},
							{"assignmentIdList", assignmentIdList},
							{"searchOrderNo", searchOrderNo},
							{"userLocale", userLocale},
							{"userTimeZone", userTimeZone},
							{"orderLoadingPointListMapping", orderLoadingPointListMapping},
							{"bimodalRoadAndRelatedRailInfo", bimodalRoadAndRelatedRailInfo}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadShippingOrders", params);
		}
		// ]]
		try {
			final boolean INITIALIZE_FOR_EDIT = false;
			Map<Long, Date> truckNextMinPlannedStartMap = getTruckNextOrderMapping(assignmentIdList, languageCd);
			
			ShippingOrderFetchListBean bean = null;
			
			if (StringUtils.empty(searchOrderNo) || assignmentIdList.isEmpty()) {
				bean = loadOrdersBean(selectedPlantCd, selectedShippingPointId, 
						selectedStatusList, 
						fromDate, toDate, searchText,
						languageCd, orderByColumn, sortAscending, count, selectedTransportCat, 
						assignmentIdList, searchOrderNo, start);
			}
			else {
				bean = loadOrdersBeanForAssignmentIds(start, count, assignmentIdList, languageCd, orderByColumn, sortAscending);
			}
			
			//Adding the bean to the result which contains also the "initial loadibility" condition flags.
			List<OrdItemAssignmtLoadEntity> orderItemAssignmentLoadList = (List<OrdItemAssignmtLoadEntity>) bean.getOrderItemAssignmentLoadList();
			List<ShippingOrderLoadBean> beanList = new ArrayList<ShippingOrderLoadBean>(orderItemAssignmentLoadList.size());
			
			TimeZone defaultTimeZone = timeZoneService.createDefault(userLocale);
			
			List<String> fuzzyUomCds = fetchFuzzyUomCds();
			
			boolean checkBimodal = false;
			
			List<Long> sapMaterialIds = new ArrayList<Long>();
			
			for (OrdItemAssignmtLoadEntity orderItemAssignmentLoad : orderItemAssignmentLoadList) {
				Long sapMaterialId = orderItemAssignmentLoad.getSapMaterialId();
				sapMaterialIds.add(sapMaterialId);
			}
			
			Map<Long, SapMaterialEntity> sapMaterialMap = new HashMap<Long, SapMaterialEntity>();
			List<SapMaterialEntity> sapMaterialEntities = bestellungService.getSapMaterialsById(sapMaterialIds);
			for (SapMaterialEntity sapMaterial : sapMaterialEntities) {
				Long sapMaterialId = sapMaterial.getSapMaterialId();
				sapMaterialMap.put(sapMaterialId, sapMaterial);
			}
			
			Map<Long, List<SapMaterialAltUomEntity>> alternativeUomMap = getAlternativeUomMap(fuzzyUomCds, sapMaterialIds);
			
			for (OrdItemAssignmtLoadEntity orderItemAssignmentLoad : orderItemAssignmentLoadList) {
				
				Long sapMaterialId = orderItemAssignmentLoad.getSapMaterialId();
				SapMaterialEntity sapMaterial = sapMaterialMap.get(sapMaterialId); 
				List<SapMaterialAltUomEntity> sapMatAltUoms = alternativeUomMap.get(sapMaterialId);
				if (sapMatAltUoms == null) {
					sapMatAltUoms = new ArrayList<SapMaterialAltUomEntity>(0);
				}
				
				ShippingOrderLoadBean shipppingOrderLoadBean = getShippingOrderLoadBean(
						orderItemAssignmentLoad, checkBimodal, userLocale,
						orderLoadingPointListMapping, bimodalRoadAndRelatedRailInfo,
						truckNextMinPlannedStartMap, sapMaterial, sapMatAltUoms, INITIALIZE_FOR_EDIT);
			
				TimeZone shippTimeZone;
				if (orderItemAssignmentLoad.getTimezoneCd() == null) {
					shippTimeZone = defaultTimeZone;
				}
				else {
					shippTimeZone = new TimeZone(orderItemAssignmentLoad.getTimezoneCd(), userLocale);
				}
			
				shipppingOrderLoadBean.setLoadingPlannedStart(
						shippTimeZone.fmtAsDayMonthYearHourMinuteWithOffset(
								orderItemAssignmentLoad.getLoadingPlannedStart(),
								userTimeZone));
				shipppingOrderLoadBean.setLoadingRealStart(
						shippTimeZone.fmtAsDayMonthYearHourMinuteWithOffset(
								orderItemAssignmentLoad.getLoadingRealStart(),
								userTimeZone));
			
				beanList.add(shipppingOrderLoadBean);
			}
			
			
			//Calculation of real start time in a postprocess.
			Map<Long, Date> orderToPlannedLoad = calculateRealstartForOrders(orderItemAssignmentLoadList);
			
			LoadShippingOrdersBean loadShippingOrdersBean = new LoadShippingOrdersBean(bean, beanList, 
					orderToPlannedLoad, truckNextMinPlannedStartMap);
			
			return result = loadShippingOrdersBean;
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
	 * Fetches the alternative UoM-s for the given material id-s.
	 * 
	 * @param fuzzyUomCds A valid list containing the codes of the fuzzy UoMs- to be excluded.
	 * @param sapMaterialIds A valid list of id-s of the sap materials.
	 * 
	 * @return A map which holds the list of SapMaterialAltUom entities by the id-s of the sap materials.
	 */
	private Map<Long, List<SapMaterialAltUomEntity>> getAlternativeUomMap(List<String> fuzzyUomCds, 
			List<Long> sapMaterialIds) {
		// [[
		Map<Long,List<SapMaterialAltUomEntity>> result = null;
		Object[][] params = {{"fuzzyUomCds", fuzzyUomCds},
							{"sapMaterialIds", sapMaterialIds}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getAlternativeUomMap", params);
		}
		// ]]
		try {
			
			Map<Long, List<SapMaterialAltUomEntity>> sapMatAltUomMap = new HashMap<Long, List<SapMaterialAltUomEntity>>();
			List<SapMaterialAltUomEntity> sapMaterialAltUomEntities = getAlternativeUoms(sapMaterialIds, fuzzyUomCds);
			for (SapMaterialAltUomEntity sapMaterialAltUom : sapMaterialAltUomEntities) {
				SapMaterialEntity sapMaterialOfAltUom = sapMaterialAltUom.getSapMaterial();
				Long sapMaterialId = sapMaterialOfAltUom.getSapMaterialId();
				
				List<SapMaterialAltUomEntity> sapMaterialAltUomsOfMaterial = sapMatAltUomMap.get(sapMaterialId);
				if (sapMaterialAltUomsOfMaterial == null) {
					sapMaterialAltUomsOfMaterial = new ArrayList<SapMaterialAltUomEntity>();
					sapMatAltUomMap.put(sapMaterialId, sapMaterialAltUomsOfMaterial);
				}
				sapMaterialAltUomsOfMaterial.add(sapMaterialAltUom);
			}
			
			return result = sapMatAltUomMap;
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
	 * Collects the editability rules for the selected order-load on shipping screen within a serialized bean.
	 * @param checkBimodal flag highlighting to check for Bimodal loading case as well.
	 * @param initForEdit flag highlighting to initialize attributes required only for edit mode.
	 */
	public ShippingOrderLoadBean getShippingOrderLoadBean(OrdItemAssignmtLoadEntity ordItemAssignmentLoad,
			boolean checkBimodal,
			Locale locale,
			Map<Long, List<LdpointMatRestrEntity>> orderLoadingPointListMapping,
			Map<Long, ShippBimodalRoadRailEntity> bimodalRoadAndRelatedRailInfo,
			Map<Long, Date> truckNextMinPlannedStartMap, 
			SapMaterialEntity sapMaterialEntity, 
			List<SapMaterialAltUomEntity> sapMatAltUomList, boolean initForEdit) {
		// [[
		ShippingOrderLoadBean result = null;
		Object[][] params = {{"ordItemAssignmentLoad", ordItemAssignmentLoad},
							{"checkBimodal", checkBimodal + ""},
							{"locale", locale},
							{"orderLoadingPointListMapping", orderLoadingPointListMapping},
							{"bimodalRoadAndRelatedRailInfo", bimodalRoadAndRelatedRailInfo},
							{"truckNextMinPlannedStartMap", truckNextMinPlannedStartMap},
							{"sapMaterialEntity", sapMaterialEntity},
							{"sapMatAltUomList", sapMatAltUomList},
							{"initForEdit", initForEdit + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getShippingOrderLoadBean", params);
		}
		// ]]
		try {
			
			String languageCd = locale.getLanguage();
			
			ShippingOrderLoadBean bean = new ShippingOrderLoadBean(ordItemAssignmentLoad);
			
			if (truckNextMinPlannedStartMap == null) {
				truckNextMinPlannedStartMap = getTruckNextOrderMapping(ordItemAssignmentLoad.getAssignmentId(), languageCd);
				bean.setTruckNextMinPlannedStartMap(truckNextMinPlannedStartMap);
			}
			
			if (checkBimodal) {
				Map<Long, ShippBimodalRoadRailEntity> bimodalRoadAndRelatedRailInfoMap = getBimodalRailOrder(ordItemAssignmentLoad);
				bean.setBimodalRoadAndRelatedRailInfoMap(bimodalRoadAndRelatedRailInfoMap);
				
				ShippBimodalRoadRailEntity shippBimodalRoadRailEntity = bimodalRoadAndRelatedRailInfoMap.get(ordItemAssignmentLoad.getOrderId());
				
				if (shippBimodalRoadRailEntity != null) {
					//wagonNumber
					String wagonNumber = WagonNumberUtils.toDisplayNo(shippBimodalRoadRailEntity.getWagonNo());
					bean.setWagonNumber(wagonNumber);
					
					Double wagonNetQuantity = shippBimodalRoadRailEntity.getWagonLoadNetQuantity().doubleValue();
					
					if (wagonNetQuantity != null) {
						//formatter for numbers
						DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
						symbols.setDecimalSeparator(',');
						symbols.setGroupingSeparator(' ');
						DecimalFormat formatter = new DecimalFormat("###,###.##", symbols);
						
						//bimodal available space info
						//String availableLabel = MessageUtils.getMessage(request, "auftragbearbeitung.texte.available");
						String wagonNetQtyString = formatter.format(wagonNetQuantity);
						String wagonEmptyQtyString = wagonNetQtyString;
						
						Double alreadyLoadedQuantity = shippBimodalRoadRailEntity.getAlreadyLoadedQuantity();
						if (alreadyLoadedQuantity != null) {
							wagonEmptyQtyString = formatter.format(wagonNetQuantity - alreadyLoadedQuantity);
						}
						//bean.setAvailableSpaceInfoBimodal(availableLabel + ": " + wagonEmptyQtyString + "/" + wagonNetQtyString);
						bean.setAvailableSpaceInfoBimodal(wagonEmptyQtyString + "/" + wagonNetQtyString);
					}
				}
			}
			
			// LOGON-6715 include alternative UoMs
			String[][] alternativeMaterialData = getAlternativeMaterialData(sapMaterialEntity, sapMatAltUomList);
			bean.setUnitsOfMeasurement(alternativeMaterialData);
			
			// Enable functionality
			OrderChangeLogic orderChangeLogic = new OrderChangeLogic(truckNextMinPlannedStartMap, 
					orderLoadingPointListMapping, bimodalRoadAndRelatedRailInfo);
			
			Boolean enableUpdateTareMeasureData = !orderChangeLogic.disableSimpleChangeFunctionality(ordItemAssignmentLoad, 
					OrderChangeLogic.FUNCTIONALITY_UPDATE_TARA_MEASURE_DATA);
			
			bean.setEnableUpdateTareMeasureData(enableUpdateTareMeasureData);
			
			Boolean enableUpdateNettoMeasureData = !orderChangeLogic.disableSimpleChangeFunctionality(ordItemAssignmentLoad, 
					OrderChangeLogic.FUNCTIONALITY_UPDATE_NETTO_MEASURE_DATA);
			
			bean.setEnableUpdateNettoMeasureData(enableUpdateNettoMeasureData);

			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
			boolean isLoadingOnCurrentDay = true;
			if (ordItemAssignmentLoad.getLoadingPlannedStart() != null){
				isLoadingOnCurrentDay = fmt.format(ordItemAssignmentLoad.getLoadingPlannedStart()).equals(fmt.format(new Date()));
			}
			bean.setLoadingOnCurrentDay(isLoadingOnCurrentDay);
			
			Boolean enableUpdateLoadingPoint = !orderChangeLogic.disableSimpleChangeFunctionality(ordItemAssignmentLoad, 
					OrderChangeLogic.FUNCTIONALITY_UPDATE_LOADINGPOINT_DATA);
			
			bean.setEnableUpdateLoadingPoint(enableUpdateLoadingPoint);
			
			Boolean enableTransmitToWeighSystem = !orderChangeLogic.disableSimpleChangeFunctionality(ordItemAssignmentLoad, 
					OrderChangeLogic.FUNCTIONALITY_TRANSMIT_TO_WEIGH_SYSTEM);
			
			bean.setEnableTransmitToWeighSystem(enableTransmitToWeighSystem);
			
			Boolean enableCancelWeighSystem = !orderChangeLogic.disableSimpleChangeFunctionality(ordItemAssignmentLoad, 
					OrderChangeLogic.FUNCTIONALITY_CANCEL_WEIGH_SYSTEM);
			
			bean.setEnableCancelWeighSystem(enableCancelWeighSystem);
			
			Boolean enableUpdateOrderItemLoadedQuantity = !orderChangeLogic.disableSimpleChangeFunctionality(ordItemAssignmentLoad, 
					OrderChangeLogic.FUNCTIONALITY_UPDATE_ORDER_ITEM_LOADED_QUANTITY);
			
			bean.setEnableUpdateOrderItemLoadedQuantity(enableUpdateOrderItemLoadedQuantity);
			
			Boolean enableLoadContainer = !orderChangeLogic.disableSimpleChangeFunctionality(ordItemAssignmentLoad, 
					OrderChangeLogic.FUNCTIONALITY_LOAD_CONTAINER);
			
			bean.setEnableLoadContainer(enableLoadContainer);
			
			if ( initForEdit ) {				
				bean.initializeForEdit();
			}
			
			return result = bean;
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
	 * Creates a two-dimensional array of the alt UoM data, to be used on the loading screen.
	 * 
	 * @param sapMaterial The sap material entity.
	 * @param sapMatAltUomList The valid list of SapMaterialAltUom entities for the given material.
	 * 
	 * @return A two-dimensional array with every row follows:
	 * <ul>
	 * <li> [0] - UoM code,</li>
	 * <li> [1] - multiplier to be able to calculate the value in the base UoM of the material,</li>
	 * <li> [2] - boolean - is the UoM the base UoM of the material.</li>
	 * <li> [3] - boolean - preselected - initialized only in edit view</li>
	 */
	private String[][] getAlternativeMaterialData(SapMaterialEntity sapMaterial, List<SapMaterialAltUomEntity> sapMatAltUomList) {
		// [[
		String[][] result = null;
		Object[][] params = {{"sapMaterial", sapMaterial},
							{"sapMatAltUomList", sapMatAltUomList}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getAlternativeMaterialData", params);
		}
		// ]]
		try {
			String baseUomCd = sapMaterial.getBaseUom().getUomCd();
			
			//[3] is initialized only in edit mode in ShippingLoadOrderBean
			String[][] altMatArr = new String[sapMatAltUomList.size()][4];
			boolean baseMaterialFound = false;
			for (int i = 0; i < sapMatAltUomList.size(); i++) {
				SapMaterialAltUomEntity alt = sapMatAltUomList.get(i);
			
				String altUomCd = alt.getUom().getUomCd();
				altMatArr[i][0] = altUomCd; // textual value of uom
			
				double multiplierForTons;
				if (alt.getNumeratorForConversion() != null
						&& alt.getDenominatorForConversion() != null) {
					multiplierForTons = (double) alt.getNumeratorForConversion()
							/ alt.getDenominatorForConversion();
				}
				else {
					multiplierForTons = 1;
				}
			
				altMatArr[i][1] = String.valueOf(multiplierForTons); // multiplier to get to tons
			
				if (baseUomCd.equals(altUomCd)) {
					// is base uom?
					altMatArr[i][2] = "true";
					baseMaterialFound = true;
				}
			
			}
			if (!baseMaterialFound) {
				String[][] cp = new String[altMatArr.length + 1][4];
				for (int i = 0; i < altMatArr.length; i++) {
					cp[i] = altMatArr[i];
				}
				cp[altMatArr.length][0] = sapMaterial.getBaseUom().getUomCd();
				cp[altMatArr.length][1] = String.valueOf(1);
				cp[altMatArr.length][2] = String.valueOf(true);
				altMatArr = cp;
			
			}
			
			return result = altMatArr;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	private Map<Long, Date> calculateRealstartForOrders(List<OrdItemAssignmtLoadEntity> orderList) {
		List<OrdLoadSchedInfoEntity> timeInfos = null;
		Map<Long, Date> orderToPlannedLoad = new HashMap<Long, Date>();
		
		List<Long> orderIds = new ArrayList<Long>();
		if (orderList != null && !orderList.isEmpty()) {
			for (OrdItemAssignmtLoadEntity ordItemAssignmtLoad : orderList) {

				//only calculate if there is no real start info
				if(ordItemAssignmtLoad.getLoadingRealStart() == null && 
						ordItemAssignmtLoad.getOrderStatusCd().longValue() >= Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER) {
					
					orderIds.add(ordItemAssignmtLoad.getOrderId());
				}
			}
		}
		if (orderIds == null || orderIds.isEmpty() ) {
			return orderToPlannedLoad;
		}
		timeInfos = getAssignmentTimeInfoForOrders(orderIds);
		orderToPlannedLoad = getPlannedLoadMap(timeInfos);
			
		return orderToPlannedLoad;
	}
	
	/**
	 * Returns a map, which contains the calculated loading timestamp for the order ids.
	 * @param timeInfos
	 * @return
	 */
	private Map<Long, Date> getPlannedLoadMap(List<OrdLoadSchedInfoEntity> timeInfos) {
		Iterator<OrdLoadSchedInfoEntity> it = timeInfos.iterator();
		Map<Long, Date> calculatedStartMap = new HashMap<Long, Date>();
				
		while (it.hasNext()) {
			OrdLoadSchedInfoEntity info = it.next();
			Date calculatedStartLoad = null;
			Long assignTrStatusCd = info.getAssignTrStatusCd();
			Date plannedStartLoad = info.getPlannedStartLoad();
			Long driverId = info.getDriverId();
			
			calculatedStartLoad = SchedulingUtils.getCalculatedTimeOfLoad( driverId, assignTrStatusCd,
					plannedStartLoad, dispoService, configService);
			calculatedStartMap.put(info.getOrderId(), calculatedStartLoad);
		}
		return calculatedStartMap;
	}
	
	/**
	 * Returns a map with the truckentities as keys, and the next planned start of loading for the certain
	 * truck. The filter should not be applied to the query itself, just for the retrieval of the involved trucks,
	 * the planned start time we are interested in!
	 * @param assignmentIdList - list of assignment ids.
	 * @param languageCd normal filter parameter
	 * 
	 * @return
	 */
	public Map<Long, Date> getTruckNextOrderMapping(List<Long> assignmentIdList, String languageCd) {
		 // [[
		 Map<Long,Date> result = null;
		 Object[][] params = {{"assignmentIdList", assignmentIdList},
							 {"languageCd", languageCd}};
		 boolean logDebugEnabled = LOG.isDebugEnabled();
		 if (logDebugEnabled) {
			 LOG.entering(this, "getTruckNextOrderMapping", params);
		 }
		 // ]]
		 try {
			 if (assignmentIdList == null || assignmentIdList.isEmpty()) {
				 return result = Collections.<Long, Date>emptyMap();
			 }
			 
			 List<Long> involvedTruckIdList = getInvolvedTruckIds(assignmentIdList);
			 if (involvedTruckIdList == null || involvedTruckIdList.isEmpty()) {
				 return result = Collections.<Long, Date>emptyMap();
			 }	
			 
			 Map<Long, Date> pairs = new HashMap<Long, Date>();
			 Long [] loadCandidateStatuses = {
					Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER,
					Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK ,
					Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT,
					Constant.ORDER_STATUS_LOADING,
					Constant.ORDER_STATUS_LOADED,
					Constant.ORDER_STATUS_SHIPPING,
					Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT,
					Constant.ORDER_STATUS_UNLOADING
				};
				
			 Filter filter = new FilterImpl();
			 filter.addExpression(Restrictions.in(OrdItemAssignmtLoadEntity.ATTR_ORDER_STATUS_CD, Arrays.asList(loadCandidateStatuses)));
			 filter.addExpression(Restrictions.isNull(OrdItemAssignmtLoadEntity.ATTR_UNLOADING_TS));
			 filter.addExpression(Restrictions.isNull(OrdItemAssignmtLoadEntity.ATTR_LOADING_TS));
			 filter.addExpression(Restrictions.eq(OrdItemAssignmtLoadEntity.ATTR_LANG_CD, languageCd));
			 filter.addExpression(Restrictions.in(OrdItemAssignmtLoadEntity.ATTR_TRUCK_ID, involvedTruckIdList));
			 ReadOnlyDao<OrdItemAssignmtLoadEntity> dao = getDaoProvider().getOrdItemAssignmtLoadEntityDao();
			 JPAQuery jpaQuery = dao.createJPAQuery();
			 jpaQuery.setFilter(filter);
			 jpaQuery.addGroupBy(OrdItemAssignmtLoadEntity.ATTR_TRUCK_ID);
			 jpaQuery.addSelect(SelectExpression.simpleSelect(OrdItemAssignmtLoadEntity.ATTR_TRUCK_ID));
			 jpaQuery.addSelect(SelectExpression.min(OrdItemAssignmtLoadEntity.ATTR_LOADING_MIN_PLANNED_START));
			 
			 //the truck's next order has to be determined without adding authorization for other plants shippingpoints shiptos
			 Authorizer.disableNextAccessRestriction();
			 List<Object[]> plannedLoadingDataList = dao.find(jpaQuery);
			 for (Object[] plannedLoadingData : plannedLoadingDataList) {
				 Long truckId = (Long) plannedLoadingData[0];
				 Date plannedLoadingDate = (Date) plannedLoadingData[1];
				 pairs.put(truckId, plannedLoadingDate);
			 }
			 return result = pairs;
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
	 * Returns a map with the truckentities as keys, and the next planned start of loading for the certain
	 * assignment. The filter should not be applied to the query itself, just for the retrieval of the involved trucks,
	 * the planned start time we are interested in!
	 * @param assignmentId - list of assignment ids.
	 * @param languageCd normal filter parameter
	 * 
	 * @return
	 */
	public Map<Long, Date> getTruckNextOrderMapping(Long assignmentId, String languageCd) {
		 // [[
		 Map<Long,Date> result = null;
		 Object[][] params = {{"assignmentId", assignmentId},
							 {"languageCd", languageCd}};
		 boolean logDebugEnabled = LOG.isDebugEnabled();
		 if (logDebugEnabled) {
			 LOG.entering(this, "getTruckNextOrderMapping", params);
		 }
		 // ]]
		 try {
			 if (assignmentId == null) {
				 return result = Collections.<Long, Date>emptyMap();
			 }
			 
			 List<Long> assignmentIdList = new ArrayList<Long>();
			 assignmentIdList.add(assignmentId);
			 
			 return result = getTruckNextOrderMapping(assignmentIdList, languageCd);
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
	 * Returns the list of truck id-s from VLGNC_ORD_ITEM_ASSIGNMT_LOAD, filtered with the given 
	 * assignment id-s.
	 * 
	 * @param assignmentIdList The list of assignment id-s.
	 * 
	 * @return list of truck id-s.
	 */
	private List<Long> getInvolvedTruckIds(List<Long> assignmentIdList) {
		// [[
		List<Long> result = null;
		Object[][] params = {{"assignmentIdList", assignmentIdList}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getInvolvedTruckIdList", params);
		}
		// ]]
		try {
			ReadOnlyDao<OrdItemAssignmtLoadEntity> dao = getDaoProvider().getOrdItemAssignmtLoadEntityDao();
			
			Filter involvedAssignmentFilter = new FilterImpl();
			involvedAssignmentFilter.addExpression(Restrictions.in(OrdItemAssignmtLoadEntity.ATTR_ASSIGNMENT_ID, assignmentIdList));
			involvedAssignmentFilter.addExpression(Restrictions.isNull(OrdItemAssignmtLoadEntity.ATTR_UNLOADING_TS));
			involvedAssignmentFilter.addExpression(Restrictions.isNull(OrdItemAssignmtLoadEntity.ATTR_LOADING_TS));
			JPAQuery jpaQuery = dao.createJPAQuery();
			jpaQuery.setFilter(involvedAssignmentFilter);
			jpaQuery.addSelect(SelectExpression.simpleSelect(OrdItemAssignmtLoadEntity.ATTR_TRUCK_ID));
			jpaQuery.setDistinct(true);
			
			Authorizer.disableNextAccessRestriction();
			return result = dao.find(jpaQuery);
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
	 * Returns a map with the bimodal road orders, and some information about their related bimodal rail order.
	 * What information is available see in type ShippBimodalRoadRailEntity.
	 * @return
	 */
	public HashMap<Long, ShippBimodalRoadRailEntity> getBimodalRailOrders(String plantCd, Long shippingPointId, List statusIdList, Date fromDate, Date toDate,
			String generalFilterString, String languageCd, String transportCompositionChoice, String searchOrderNo, int maxRowNum){
		 // [[
		 HashMap<Long, ShippBimodalRoadRailEntity> result = null;
		 Object[][] params = {{"plantCd", plantCd},
							 {"shippingPointId", shippingPointId},
							 {"statusIdList", statusIdList},
							 {"fromDate", fromDate},
							 {"toDate", toDate},
							 {"generalFilterString", generalFilterString},
							 {"languageCd", languageCd},
							 {"transportCompositionChoice", transportCompositionChoice},
							 {"maxRowNum", maxRowNum + ""}};
		 boolean logDebugEnabled = LOG.isDebugEnabled();
		 if (logDebugEnabled) {
			 LOG.entering(this, "getBimodalRailOrders", params);
		 }
		 // ]]
		 try {
			 //get the assignment ids on screen
			 final ReadOnlyDao<OrdItemAssignmtLoadEntity> dao = getDaoProvider().getOrdItemAssignmtLoadEntityDao();
			 final Filter<OrdItemAssignmtLoadEntity> involvedBimodalRoadOrderFilter = getFilterForOrderSearch(plantCd, shippingPointId, statusIdList, fromDate, toDate, languageCd, generalFilterString, transportCompositionChoice, null, searchOrderNo);
			 //get the involved bimodal road orders
			 involvedBimodalRoadOrderFilter.addExpression(Restrictions.eq(OrdItemAssignmtLoadEntity.ATTR_DELIVERY_TYPE_CD, Constant.DELIVERY_TYPE_BIMODAL_ROAD));
			 final JPAQuery<OrdItemAssignmtLoadEntity> jpaQuery = dao.createJPAQuery();
			 jpaQuery.setFilter(involvedBimodalRoadOrderFilter);
			 jpaQuery.addSelect(SelectExpression.simpleSelect(OrdItemAssignmtLoadEntity.ATTR_ORDER_ID));
			 jpaQuery.setDistinct(true);
			 
			 final HashMap<Long, ShippBimodalRoadRailEntity> pairs = new HashMap<Long, ShippBimodalRoadRailEntity>();
			 final List<OrdItemAssignmtLoadEntity> involvedBimodalRoadOrders = dao.find(jpaQuery);
			 if (involvedBimodalRoadOrders == null || involvedBimodalRoadOrders.isEmpty()) {
				 return result = pairs;
			 }
			 
			 final ReadOnlyDao<ShippBimodalRoadRailEntity> dao2 = getDaoProvider().getShippBimodalRoadRailEntityDao();
			 final Filter<ShippBimodalRoadRailEntity> filter = dao2.createFilter();
			 if ((involvedBimodalRoadOrders != null) && (!involvedBimodalRoadOrders.isEmpty()) ){
				 filter.addExpression(Restrictions.in(ShippBimodalRoadRailEntity.ATTR_ORDER_ID, involvedBimodalRoadOrders));
			 }
			 final List<ShippBimodalRoadRailEntity> bimodalRoadRails = dao2.find(filter);
			 
			 for (ShippBimodalRoadRailEntity ent : bimodalRoadRails) {
				 pairs.put(ent.getOrderId(), ent);
			 }
			 return result = pairs;
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
	 * Returns a map with one bimodal order, and some information about it's related bimodal rail order.
	 * What information is available see in type ShippBimodalRoadRailEntity.
	 * @return
	 */
	public Map<Long, ShippBimodalRoadRailEntity> getBimodalRailOrder(OrdItemAssignmtLoadEntity order){
		// [[
		Map<Long,ShippBimodalRoadRailEntity> result = null;
		Object[][] params = {{"order", order}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getBimodalRailOrder", params);
		}
		// ]]
		try {
			HashMap pairs = new HashMap();
			ReadOnlyDao<ShippBimodalRoadRailEntity> dao2 = getDaoProvider().getShippBimodalRoadRailEntityDao();
			Filter fi = new FilterImpl();
			fi.addExpression(Restrictions.eq(ShippBimodalRoadRailEntity.ATTR_ORDER_ID, order.getOrderId()));
			List bimodalRoadRails = dao2.find(fi);
			
			Iterator it1 = bimodalRoadRails.iterator();
			while (it1.hasNext()) {
				ShippBimodalRoadRailEntity ent = (ShippBimodalRoadRailEntity)it1.next();
				pairs.put(ent.getOrderId(), ent);
			}
			return result = pairs;
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
	 * Fetches all loadingpoints for the shippingpoint, which produces the given sap_material.
	 * @param shippingPointId - Long id of the shippingpoint
	 * @param productCds - List of ShippingPointMaterialEntities
	 * @return list of {@link LdpointMatRestrEntity}
	 */
	public List<LdpointMatRestrEntity> fetchLoadingPoints(Long shippingPointId, Long productCd) {
		// [[
		List<LdpointMatRestrEntity> result = null;
		Object[][] params = {{"shippingPointId", shippingPointId},
							{"productCd", productCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchLoadingPoints", params);
		}
		// ]]
		try {
			//get loadingpoints for the shippingpoint
			ReadWriteDao<LoadingpointEntity> dao = getDaoProvider().getLoadingpointEntityDao();
			Filter filter = new FilterImpl();
			
			filter.addExpression(Restrictions.eq(LoadingpointEntity.ATTR_SHIPPINGPOINT_ID, shippingPointId));
			List loadingPoints = dao.find(filter);
			
			ReadWriteDao<LdpointMatRestrEntity> dao1 = getDaoProvider().getLdpointMatRestrEntityDao();
			
			Filter andFilter = new FilterImpl();
			if (loadingPoints != null && !loadingPoints.isEmpty()){
				andFilter.addExpression(Restrictions.and(
						Restrictions.eq(LdpointMatRestrEntity.ATTR_LOADINGPOINT + "." + LoadingpointEntity.ATTR_ACTIVE_FLAG, true),						 
						Restrictions.eq(LdpointMatRestrEntity.ATTR_SAP_MATERIAL_ID, productCd), 
						Restrictions.in(LdpointMatRestrEntity.ATTR_LOADINGPOINT, loadingPoints)
				)
				);				
			}
			else{
				return result = new ArrayList();
			}
			
			return result = dao1.findNRecs(0, 100, andFilter);
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
	 * Fetches all loadingpoints for the shippingpoint, which produces the given sap_material. RfidObjects will be included in the result.
	 * @param shippingPointId - Long id of the shippingpoint
	 * @param productCds - List of ShippingPointMaterialEntities
	 * @return list of {@link LdpointMatRestrEntity}
	 */
	public List fetchLoadingPointsWithRFIds(Long shippingPointId, Long productCd){
		// [[
		List result = null;
		Object[][] params = {{"shippingPointId", shippingPointId},
							{"productCd", productCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchLoadingPointsWithRFids", params);
		}
		// ]]
		try {
			List<LdpointMatRestrEntity> loadingpointsList = fetchLoadingPoints(shippingPointId, productCd);
			
			for (LdpointMatRestrEntity loadingpointMaterialRestriction : loadingpointsList){
					LoadingpointEntity loadingpoint = loadingpointMaterialRestriction.getLoadingpoint();
					RfidobjectEntity rfidObject = loadingpoint.getRfidobject();
				   
					if ( rfidObject != null ){
							rfidObject.getRfids().size(); // this is running a SELECT *
					}
			}
			
			return result = loadingpointsList;
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
	 * @param transportCompositionChoice truck and trailer transport constellations allowed for the filters
	 * @param assignmentIdList this is the list of assignment Ids, which MUST be in the order list, no matter if they do not match the filter the cause is that assignments should not be separated because of filters
	 * @param searchOrderNo overrides all other filters, only searches for orderId
	 */
	
	public List loadOrders(String plantCd, Long shippingpointId, List statusIdList, Date fromDate, Date toDate, String generalFilterString, 
		String languageCd, String orderBy, boolean sortAsc, int maxRowNum, String transportCompositionChoice, List assignmentIdList, String searchOrderNo) {		
		// [[
		List result = null;
		Object[][] params = {{"plantCd", plantCd},
							{"shippingpointId", shippingpointId},
							{"statusIdList", statusIdList},
							{"fromDate", fromDate},
							{"toDate", toDate},
							{"generalFilterString", generalFilterString},
							{"languageCd", languageCd},
							{"orderBy", orderBy},
							{"sortAsc", sortAsc + ""},
							{"maxRowNum", maxRowNum + ""},
							{"transportCompositionChoice", transportCompositionChoice},
							{"assignmentIdList", assignmentIdList}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadOrders", params);
		}
		// ]]
		try {
			ReadOnlyDao<OrdItemAssignmtLoadEntity> dao = getDaoProvider().getOrdItemAssignmtLoadEntityDao();
						
			Filter<OrdItemAssignmtLoadEntity> filter = getFilterForOrderLoad(plantCd, shippingpointId, statusIdList, fromDate, toDate, generalFilterString, languageCd, orderBy, sortAsc, maxRowNum, transportCompositionChoice, assignmentIdList, searchOrderNo);
			
			result =  dao.findNRecs(0, maxRowNum, filter);
			LOG.debug("********************************");
			LOG.debug("*********LOADED ORDERS**********");
			LOG.debug("********************************");
			
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
	
	/**
	 * @param transportCompositionChoice truck and trailer transport constellations allowed for the filters
	 * @param assignmentIdList this is the list of assignment Ids, which MUST be in the order list, no matter if they do not match the filter the cause is that assignments should not be separated because of filters
	 * @param searchOrderNo overrides all other filters, only searches for orderId
	 */
	public ShippingOrderFetchListBean loadOrdersBean(String plantCd, Long shippingpointId, List statusIdList, Date fromDate, Date toDate, String generalFilterString, 
		String languageCd, String orderBy, boolean sortAsc, int maxRowNum, String transportCompositionChoice, List assignmentIdList, 
		String searchOrderNo, int start) {		
		// [[
		ShippingOrderFetchListBean result = null;
		Object[][] params = {{"plantCd", plantCd},
							{"shippingpointId", shippingpointId},
							{"statusIdList", statusIdList},
							{"fromDate", fromDate},
							{"toDate", toDate},
							{"generalFilterString", generalFilterString},
							{"languageCd", languageCd},
							{"orderBy", orderBy},
							{"sortAsc", sortAsc + ""},
							{"maxRowNum", maxRowNum + ""},
							{"transportCompositionChoice", transportCompositionChoice},
							{"assignmentIdList", assignmentIdList},
							{"searchOrderNo", searchOrderNo},
							{"start", start + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadOrdersBean", params);
		}
		// ]]
		try {
			ReadOnlyDao<OrdItemAssignmtLoadEntity> dao = getDaoProvider().getOrdItemAssignmtLoadEntityDao();
						
			Filter<OrdItemAssignmtLoadEntity> filter = getFilterForOrderLoad(plantCd, shippingpointId, statusIdList, fromDate, toDate, generalFilterString, languageCd, orderBy, sortAsc, maxRowNum, transportCompositionChoice, assignmentIdList, searchOrderNo);
			
			List<OrdItemAssignmtLoadEntity> orderItemAssignmentLoadList =  dao.findNRecs(start, maxRowNum, filter);
			Long totalCount = dao.count(filter);
			
			LOG.debug("********************************");
			LOG.debug("*********LOADED ORDERS**********");
			LOG.debug("********************************");
			
			result = new ShippingOrderFetchListBean();
			result.setOrderItemAssignmentLoadList(orderItemAssignmentLoadList);
			result.setTotalCount(totalCount);
			
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
	
	/**
	 * @param transportCompositionChoice truck and trailer transport constellations allowed for the filters
	 * @param assignmentIdList this is the list of assignment Ids, which MUST be in the order list, no matter if they do not match the filter the cause is that assignments should not be separated because of filters
	 * @param searchOrderNo overrides all other filters, only searches for orderId
	 */
	public Filter<OrdItemAssignmtLoadEntity> getFilterForOrderLoad(String plantCd, Long shippingpointId, List statusIdList, Date fromDate, Date toDate, String generalFilterString, 
		String languageCd, String orderBy, boolean sortAsc, int maxRowNum, String transportCompositionChoice, List assignmentIdList, String searchOrderNo) {		
		// [[
		Filter result = null;
		Object[][] params = {{"plantCd", plantCd},
							{"shippingpointId", shippingpointId},
							{"statusIdList", statusIdList},
							{"fromDate", fromDate},
							{"toDate", toDate},
							{"generalFilterString", generalFilterString},
							{"languageCd", languageCd},
							{"orderBy", orderBy},
							{"sortAsc", sortAsc + ""},
							{"maxRowNum", maxRowNum + ""},
							{"transportCompositionChoice", transportCompositionChoice},
							{"assignmentIdList", assignmentIdList}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadOrders", params);
		}
		// ]]
		try {
			ReadOnlyDao<OrdItemAssignmtLoadEntity> dao = getDaoProvider().getOrdItemAssignmtLoadEntityDao();
			Filter<OrdItemAssignmtLoadEntity> filter = (Filter<OrdItemAssignmtLoadEntity>) dao.createFilter();
			
			//			if (idList.isEmpty()){
			if (assignmentIdList.isEmpty()){
				//if there is no restriction to assignment ids, (that means that there is no dispatched orders yet in fact) use the normal filter
				filter = getFilterForOrderSearch(plantCd,
						shippingpointId, statusIdList, fromDate, toDate, languageCd, generalFilterString, transportCompositionChoice, null, searchOrderNo);
				if (filter == null) {
					return result = null;
				}
			}	
			else{				
				//if there is any restriction to ids, those MUST be included in the resultList 
				filter = getFilterForOrderSearch(plantCd,
						shippingpointId, statusIdList, fromDate, toDate, languageCd, generalFilterString, transportCompositionChoice, assignmentIdList, searchOrderNo);
				}
			
			if (StringUtils.notEmpty(orderBy)) {
				if (sortAsc){
					if (orderBy.equals(OrdItemAssignmtLoadEntity.ATTR_LOADING_MIN_PLANNED_START)) {						
						filter.addExpression(Order.asc(orderBy));
					}
					else{
						filter.addExpression(Order.ascCaseInsensitive(orderBy));
					}
				}
				else {
					if (orderBy.equals(OrdItemAssignmtLoadEntity.ATTR_LOADING_MIN_PLANNED_START)) {						
						filter.addExpression(Order.desc(orderBy));
					}
					else{
						filter.addExpression(Order.descCaseInsensitive(orderBy));
					}
				}
			}
			//inbuilt constant filter, the results always has to be listed below each other in case they are related to the same assignment (truck)
			//if you add an order, please make sure that they will not break the assignment grouping!
			filter.addExpression(Order.asc(OrdItemAssignmtLoadEntity.ATTR_ASSIGNMENT_ID));
			filter.addExpression(Order.asc(OrdItemAssignmtLoadEntity.ATTR_LOAD_ID));
			filter.addExpression(Order.asc(OrdItemAssignmtLoadEntity.ATTR_CHAMBER_NUMBER));
			filter.addExpression(Order.asc(OrdItemAssignmtLoadEntity.ATTR_ORDER_ID));
			
			return result = filter;
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
	 * @param shippingPointId
	 * @param statusIdList
	 * @param fromDate
	 * @param toDate
	 * @param generalFilterString
	 * @param languageCd
	 * @param transportCompositionChoice
	 * @param searchOrderNo overrides all other filters, only searches for orderId
	 * @return
	 */
	public List<Long> getInvolvedAssignmentIdList(String plantCd, Long shippingPointId,
			List<Long> statusIdList, Date fromDate, Date toDate,
			String generalFilterString, String languageCd, String transportCompositionChoice, String searchOrderNo, int maxRowNum) {
		// [[
		List<Long> result = null;
		Object[][] params = {{"plantCd", plantCd},
							{"shippingPointId", shippingPointId},
							{"statusIdList", statusIdList},
							{"fromDate", fromDate},
							{"toDate", toDate},
							{"generalFilterString", generalFilterString},
							{"languageCd", languageCd},
							{"transportCompositionChoice", transportCompositionChoice},
							{"searchOrderNo", searchOrderNo},
							{"maxRowNum", maxRowNum + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getInvolvedAssignmentIdList", params);
		}
		// ]]
		try {
			
			ReadOnlyDao<OrdItemAssignmtLoadEntity> dao = getDaoProvider().getOrdItemAssignmtLoadEntityDao();
			Filter<OrdItemAssignmtLoadEntity> filter = getFilterForOrderSearch(plantCd,
					shippingPointId, statusIdList, fromDate, toDate, languageCd, generalFilterString, transportCompositionChoice, null, searchOrderNo);
			if (filter == null) {
				return result = new ArrayList<Long>();
			}
			
			JPAQuery<OrdItemAssignmtLoadEntity> jpaQuery = dao.createJPAQuery();
			jpaQuery.setFilter(filter);
			jpaQuery.addSelect(SelectExpression.simpleSelect(OrdItemAssignmtLoadEntity.ATTR_ASSIGNMENT_ID));
			jpaQuery.setDistinct(true);
			List<Long> assignmentIdList = dao.findNRecs(0, maxRowNum, jpaQuery);
			return result = assignmentIdList;
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
	 * Returns a Filter for OrderAssignmentEntityDao according to the given parameters.
	 * @param plantCd TODO
	 * @param shippingpointId 
	 * @param statusIdList
	 * @param fromDate
	 * @param toDate
	 * @param languageCd
	 * @param generalFilterString 
	 * @param transportConditionChoice TODO
	 * @param assignmentIdList assignmentIds present in this list must be included in the result list no matter if they would be filtered out
	 * @param materialIdList
	 * @param searchOrderNo overrides all other filters, only searches for orderId
	 * @return
	 */
	private static Filter<OrdItemAssignmtLoadEntity> getFilterForOrderSearch(String plantCd,
			Long shippingpointId, List<Long> statusIdList, Date fromDate, Date toDate,
			String languageCd, String generalFilterString, String transportConditionChoice,
			List<Long> assignmentIdList, String searchOrderNo) {
		
		final Filter<OrdItemAssignmtLoadEntity> filter = new FilterImpl<OrdItemAssignmtLoadEntity>();		
		// LOGON-6715 generalFilterString is now treated like searchOrderNo in terms of search criteria selection
		if (searchOrderNo.isEmpty() && generalFilterString.isEmpty()) {
		
			List<Expression> filterExpressions = new ArrayList<Expression>();
			if (plantCd != null){
				//this is crucial, cannot be left out!
				filterExpressions.add(Restrictions.eq(OrdItemAssignmtLoadEntity.ATTR_PLANT_CD, plantCd));
			}
			else{
				throw new LogonException();
			}
	//		filterExpressions.add(Restrictions.eq(OrdItemAssignmtLoadEntity.ATTR_SAP_TRSP_MODE_CD, Constant.TRANSPORTATION_MODE_TRUCK));
	
			if (shippingpointId != null) {					
				filterExpressions.add(Restrictions.eq(OrdItemAssignmtLoadEntity.ATTR_SHIPPINGPOINT_ID, shippingpointId));
			}
	
			if (statusIdList != null && !statusIdList.isEmpty()) {				
				filterExpressions.add(Restrictions.in(OrdItemAssignmtLoadEntity.ATTR_ORDER_STATUS_CD, statusIdList));
			}				
			
			//either delivery from, delivery to or loading planned start is between the time frame we should have a hit
			if (fromDate != null && toDate != null) {			
				filterExpressions.add( 					
						Restrictions.or
						(
								Restrictions.and( 
										Restrictions.ge(OrdItemAssignmtLoadEntity.ATTR_DELIVERY_FROM_DAT, fromDate),
										Restrictions.le(OrdItemAssignmtLoadEntity.ATTR_DELIVERY_FROM_DAT, toDate) 
								),
								Restrictions.and(
										Restrictions.ge(OrdItemAssignmtLoadEntity.ATTR_DELIVERY_TO_DAT, fromDate),
										Restrictions.le(OrdItemAssignmtLoadEntity.ATTR_DELIVERY_TO_DAT, toDate)
								),
								Restrictions.and(
										Restrictions.le(OrdItemAssignmtLoadEntity.ATTR_DELIVERY_FROM_DAT, fromDate),
										Restrictions.ge(OrdItemAssignmtLoadEntity.ATTR_DELIVERY_TO_DAT, toDate)
								),							
								Restrictions.and(
										Restrictions.isNotNull(OrdItemAssignmtLoadEntity.ATTR_LOADING_PLANNED_START),
										Restrictions.and(
												Restrictions.ge(OrdItemAssignmtLoadEntity.ATTR_LOADING_PLANNED_START, fromDate),
												Restrictions.le(OrdItemAssignmtLoadEntity.ATTR_LOADING_PLANNED_START, toDate)
										)
								)
						)
				);
			}
			if (transportConditionChoice != null){
				if (transportConditionChoice.equals(OrderChangeConstants.SHIPPING_UI_TRANSPORTATION_CHOICE_BAG)) {
					Long [] filtersForBag = {Constant.PACKAGE_TYPE_BBAG,Constant.PACKAGE_TYPE_SERVICE};
					filterExpressions.add(Restrictions.in(OrdItemAssignmtLoadEntity.ATTR_LOGON_PACKAGE_TYPE_CD, Arrays.asList(filtersForBag)));
				}
				else if (transportConditionChoice.equals(OrderChangeConstants.SHIPPING_UI_TRANSPORTATION_CHOICE_BULK_POWDER)) {
					Long [] filtersForBulk = {Constant.PACKAGE_TYPE_BULK_POWDER, Constant.PACKAGE_TYPE_SERVICE};
					filterExpressions.add(Restrictions.in(OrdItemAssignmtLoadEntity.ATTR_LOGON_PACKAGE_TYPE_CD, Arrays.asList(filtersForBulk)));
				}
				else if (transportConditionChoice.equals(OrderChangeConstants.SHIPPING_UI_TRANSPORTATION_CHOICE_BULK_DUMPED)) {
					Long [] filtersForBulk = {Constant.PACKAGE_TYPE_BULK_DUMPED, Constant.PACKAGE_TYPE_SERVICE};
					filterExpressions.add(Restrictions.in(OrdItemAssignmtLoadEntity.ATTR_LOGON_PACKAGE_TYPE_CD, Arrays.asList(filtersForBulk)));
				}
			}
			Expression normalFilter = null;
			//all this is because the and can only handle exp0 exp1 and the others, otherwise it makes no difference
			if (filterExpressions.size() == 1){
				Expression exp0 = (Expression) filterExpressions.get(0);
				filterExpressions.remove(0);
				normalFilter = exp0;
			}
			else{
				Expression exp0 = (Expression) filterExpressions.get(0);
				filterExpressions.remove(0);
				normalFilter = exp0;
				Expression exp1 = (Expression) filterExpressions.get(0);
				filterExpressions.remove(0);
				normalFilter = Restrictions.and(exp0, exp1,(Expression[])filterExpressions.toArray(new Expression [0]));	
			}
			if (assignmentIdList != null && !assignmentIdList.isEmpty()) {
				//this filter contains entries, which are according to the assignments OR they meet the filter requirements
				filter.addExpression(Restrictions.or(Restrictions.in(OrdItemAssignmtLoadEntity.ATTR_ASSIGNMENT_ID, assignmentIdList), normalFilter));
				//language must be filtered anyway
				filter.addExpression(Restrictions.eq(OrdItemAssignmtLoadEntity.ATTR_LANG_CD, languageCd));
			}
			else{
				filter.addExpression(normalFilter);
				filter.addExpression(Restrictions.eq(OrdItemAssignmtLoadEntity.ATTR_LANG_CD, languageCd));
			}
		}
		else {
			if (!searchOrderNo.isEmpty()) {
				// use only the searchOrderNo as criteria
				try {
					Long orderNo = Long.parseLong(searchOrderNo);
					filter.addExpression(Restrictions.eq(OrdItemAssignmtLoadEntity.ATTR_ORDER_NUMBER, orderNo));
				}
				catch (NumberFormatException e) {
					LOG.error("parameter for orderNumber is not a proper Long value: " + searchOrderNo + ". using -1 instead.");
					//add filter which will surely not return any result
					filter.addExpression(Restrictions.eq(OrdItemAssignmtLoadEntity.ATTR_ORDER_NUMBER, -1L));
				}
				filter.addExpression(Restrictions.eq(OrdItemAssignmtLoadEntity.ATTR_LANG_CD, languageCd));
			}
			else { 
				// LOGON-6715 If we have a general filter expression, we use it as the only search criterion
				filter.addExpression(
					Restrictions.or(
							Restrictions.ilike(OrdItemAssignmtLoadEntity.ATTR_BP_NAME, "%"+ generalFilterString+ "%"), 
							Restrictions.ilike(OrdItemAssignmtLoadEntity.ATTR_COMM_CARRIER_NAME, "%" + generalFilterString + "%"),
							Restrictions.ilike(OrdItemAssignmtLoadEntity.ATTR_EXEC_CARRIER_NAME, "%" + generalFilterString + "%"),
							Restrictions.ilike(OrdItemAssignmtLoadEntity.ATTR_LOADINGPOINT_NAME, "%" + generalFilterString + "%"),
							Restrictions.ilike(OrdItemAssignmtLoadEntity.ATTR_MATERIAL_TXT, "%" + generalFilterString + "%"),
							Restrictions.ilike(OrdItemAssignmtLoadEntity.ATTR_MATERIAL_NO, "%" + generalFilterString + "%"),
							Restrictions.ilike(OrdItemAssignmtLoadEntity.ATTR_POOL_NAME, "%" + generalFilterString + "%"),
							Restrictions.ilike(OrdItemAssignmtLoadEntity.ATTR_SHIPPINGPOINT_NAME, "%" + generalFilterString + "%"),
							Restrictions.ilike(OrdItemAssignmtLoadEntity.ATTR_STATUS_TXT, "%" + generalFilterString + "%"),
							Restrictions.ilike(OrdItemAssignmtLoadEntity.ATTR_ORDER_UNIT_TXT, "%" + generalFilterString + "%"),
							Restrictions.ilike(OrdItemAssignmtLoadEntity.ATTR_TRUCK_PLATE_NO, "%" + generalFilterString + "%"),
							Restrictions.ilike(OrdItemAssignmtLoadEntity.ATTR_TRUCK_NAME, "%" + generalFilterString + "%"),							
							Restrictions.ilike(OrdItemAssignmtLoadEntity.ATTR_TRUCK_NAME, "%" + generalFilterString + "%")
					)
				);
				filter.addExpression(Restrictions.eq(OrdItemAssignmtLoadEntity.ATTR_LANG_CD, languageCd));					
				
			}
		}
		
		return filter;
	}
	
	public ShippingOrderFetchListBean loadOrdersBeanForAssignmentIds(int start, int maxRowNum,
			List<Long> assignmentIdList, String languageCd, String orderBy, boolean sortAsc) {

		ShippingOrderFetchListBean result = null;

		ReadOnlyDao<OrdItemAssignmtLoadEntity> dao = getDaoProvider()
				.getOrdItemAssignmtLoadEntityDao();

		Filter<OrdItemAssignmtLoadEntity> filter = dao.createFilter();
		addAssignmentIdFilter(filter, assignmentIdList, languageCd, orderBy, sortAsc);

		List<OrdItemAssignmtLoadEntity> orderItemAssignmentLoadList = dao
				.findNRecs(start, maxRowNum, filter);
		Long totalCount = dao.count(filter);

		result = new ShippingOrderFetchListBean();
		result.setOrderItemAssignmentLoadList(orderItemAssignmentLoadList);
		result.setTotalCount(totalCount);

		return result;

	}
	
	private void addAssignmentIdFilter(Filter<OrdItemAssignmtLoadEntity> filter, List<Long> assignmentIdList, String languageCd, String orderBy, boolean sortAsc) {
		//this filter contains entries, which are according to the assignments
		filter.addExpression(Restrictions.in(OrdItemAssignmtLoadEntity.ATTR_ASSIGNMENT_ID, assignmentIdList));
		//language must be filtered anyway
		filter.addExpression(Restrictions.eq(OrdItemAssignmtLoadEntity.ATTR_LANG_CD, languageCd));
		
		if (StringUtils.notEmpty(orderBy)) {
			if (sortAsc){
				if (orderBy.equals(OrdItemAssignmtLoadEntity.ATTR_LOADING_MIN_PLANNED_START)) {						
					filter.addExpression(Order.asc(orderBy));
				}
				else{
					filter.addExpression(Order.ascCaseInsensitive(orderBy));
				}
			}
			else {
				if (orderBy.equals(OrdItemAssignmtLoadEntity.ATTR_LOADING_MIN_PLANNED_START)) {						
					filter.addExpression(Order.desc(orderBy));
				}
				else{
					filter.addExpression(Order.descCaseInsensitive(orderBy));
				}
			}
		}
		//inbuilt constant filter, the results always has to be listed below each other in case they are related to the same assignment (truck)
		//if you add an order, please make sure that they will not break the assignment grouping!
		filter.addExpression(Order.asc(OrdItemAssignmtLoadEntity.ATTR_ASSIGNMENT_ID));
		filter.addExpression(Order.asc(OrdItemAssignmtLoadEntity.ATTR_LOAD_ID));
		filter.addExpression(Order.asc(OrdItemAssignmtLoadEntity.ATTR_CHAMBER_NUMBER));
		filter.addExpression(Order.asc(OrdItemAssignmtLoadEntity.ATTR_ORDER_ID));
		
	}
	
	/**
	 * Returns a Filter for OrderAssignmentEntityDao according to the given parameters.
	 * @param plantCd TODO
	 * @param shippingpointId 
	 * @param statusIdList
	 * @param fromDate
	 * @param toDate
	 * @param languageCd
	 * @return
	 */
	private Filter<ShippingStatEntity> getStatFilter(String plantCd, Long shippingpointId, List<Long> statusIdList, Date fromDate, Date toDate, String languageCd) {
		
		final Filter<ShippingStatEntity> filter = new FilterImpl<ShippingStatEntity>();
		
		if (plantCd != null){
			//this is crucial, cannot be left out!
			filter.addExpression(Restrictions.eq(ShippingStatEntity.ATTR_PLANT_CD, plantCd));
		}
		else{
			throw new LogonException();
		}
//		filter.addExpression(Restrictions.eq(ShippingStatEntity.ATTR_SAP_TRSP_MODE_CD, Constant.TRANSPORTATION_MODE_TRUCK));

		if (shippingpointId != null) {					
			filter.addExpression(Restrictions.eq(ShippingStatEntity.ATTR_SHIPPINGPOINT_ID, shippingpointId));
		}

		if (statusIdList != null && !statusIdList.isEmpty()) {				
			filter.addExpression(Restrictions.in(ShippingStatEntity.ATTR_ORDER_STATUS_CD, statusIdList));
		}				
		
		//either delivery from, delivery to or loading planned start is between the time frame we should have a hit
		if (fromDate != null && toDate != null) {			
			filter.addExpression( 					
					Restrictions.or
					(
							Restrictions.and( 
									Restrictions.ge(ShippingStatEntity.ATTR_DELIVERY_FROM_DAT, fromDate),
									Restrictions.le(ShippingStatEntity.ATTR_DELIVERY_FROM_DAT, toDate) 
							),
							Restrictions.and(
									Restrictions.ge(ShippingStatEntity.ATTR_DELIVERY_TO_DAT, fromDate),
									Restrictions.le(ShippingStatEntity.ATTR_DELIVERY_TO_DAT, toDate)
							),
							Restrictions.and(
									Restrictions.le(ShippingStatEntity.ATTR_DELIVERY_FROM_DAT, fromDate),
									Restrictions.ge(ShippingStatEntity.ATTR_DELIVERY_TO_DAT, toDate)
							),							
							Restrictions.and(
									Restrictions.isNotNull(ShippingStatEntity.ATTR_LOADING_PLANNED_START),
									Restrictions.and(
											Restrictions.ge(ShippingStatEntity.ATTR_LOADING_PLANNED_START, fromDate),
											Restrictions.le(ShippingStatEntity.ATTR_LOADING_PLANNED_START, toDate)
									)
							)
					)
			);
		}

		filter.addExpression(Restrictions.eq(ShippingStatEntity.ATTR_LANG_CD, languageCd));
		
		return filter;
	}

	/**
	 * Sets an OrderStatistic object's fields according to the material, and status statistic, queried according to
	 * the given parameters. See type OrderStatistic. ShippingPointList must contain filter parameter, otherwise
	 * the user had no option, and so he is not allowed to see any of the orders.
	 * @adnlog.exclude return
	 */
	public OrderStatistic getStatistics(String plantCd, Long shippingPointId, Date fromDate, Date toDate, String languageCd ) {
		// [[
		Object[][] params = {{"plantCd", plantCd},
							{"shippingPointId", shippingPointId},
							{"fromDate", fromDate},
							{"toDate", toDate},
							{"languageCd", languageCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getStatistics", params);
		}
		// ]]
		try {
			OrderStatistic stat = new OrderStatistic();
			Map<String, OrderStatistic.MaterialStatisticEntry> materialAndWeight =
					getSumGroupByMaterial(plantCd, shippingPointId, fromDate, toDate, languageCd);
			
			Map<String, OrderStatistic.MaterialStatisticEntry> materialAndSupposedWeightForLkws = getSpecialSumGroupByMaterial(
					plantCd, shippingPointId, fromDate, toDate, languageCd, Constant.UOM_LKW);
			
			stat.setMaterialAndWeight(materialAndWeight, materialAndSupposedWeightForLkws);

			Map<String, OrderStatistic.MaterialStatisticEntry> materialAndSupposedWeightForContainers = getSpecialSumGroupByMaterial(
					plantCd, shippingPointId, fromDate, toDate, languageCd, Constant.UOM_CONTAINER);

			stat.setMaterialAndWeight(materialAndWeight, materialAndSupposedWeightForContainers);
			
			stat.orderMaterialAndWeightDesc();
						
			//completed weight is based on the filled in loads - not depending on any kind of special handling of units
			double weightCompleted = getOrderQueryWeightSum(Arrays.asList(FULLFILLED_STATUS_CODES), plantCd, shippingPointId, fromDate, toDate, languageCd, true);
			
			//simple weight sum is based on ordered weight, OR the weight of the current truck/container on which the order was dispatched
			double weightAll = getOrderQueryWeightSum(null, plantCd, shippingPointId, fromDate, toDate, languageCd, false);
			
			double weightOrderedAsLkwUnit = getOrderQuerySpecialWeightSum(null, shippingPointId, plantCd, fromDate, toDate, languageCd, Constant.UOM_LKW);
			weightAll += weightOrderedAsLkwUnit;
			
			double weightOrderedAsContainerUnit = getOrderQuerySpecialWeightSum(null, shippingPointId, plantCd, fromDate, toDate, languageCd, Constant.UOM_CONTAINER);
			weightAll += weightOrderedAsContainerUnit;
			
			stat.setCompletedWeightOfOrders(weightCompleted);
			stat.setTotalWeightOfOrders(weightAll);
			
			long completedOrderNumber = getCountOrderResult(plantCd, shippingPointId, Arrays.asList(FULLFILLED_STATUS_CODES), fromDate, toDate, languageCd);
			stat.setCompletedNumberOfOrders(completedOrderNumber);
						
			long totalOrderNumber = getCountOrderResult(plantCd, shippingPointId, null, fromDate, toDate, languageCd);
			stat.setTotalNumberOfOrders(totalOrderNumber);
			
			LOG.debug("********************************");
			LOG.debug("*********LOADED STATS**********");
			LOG.debug("********************************");
			

			return stat;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	
	}


	public List findOrderStatuses(Locale locale, List statusCdList) {
		// [[
		List result = null;
		Object[][] params = {{"locale", locale},
							{"statusCdList", statusCdList}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findOrderStatuses", params);
		}
		// ]]
		try {
			final Filter<OrderStatusTxEntity> filter = new FilterImpl<OrderStatusTxEntity>();
			filter.addExpression(Restrictions.eq("orderStatusTxPK.langCd", locale.getLanguage()));
			if ((statusCdList != null) && (!statusCdList.isEmpty())) {
				filter.addExpression(Restrictions.in("orderStatusTxPK.orderStatusBs.orderStatusCd", statusCdList));
			}
			filter.addExpression(Restrictions.eq("orderStatusTxPK.orderStatusBs.possibleTruckStatusFlag", Boolean.TRUE));
			return result = referentialDataService.findTxEntityFiltered(OrderStatusTxEntity.class, filter);
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
	 * Returns a List with Object[3] containing the name, id, and total sum ordered from the material with name+id.
	 * Example: Object[3]: {'beton', 56, 34.2t}. The ordered weight is in tons (calculated from unit quantity * unit nominator).
	 * @param plantCd TODO
	 * @param shippingPointId - null if not applicable; acceptable shipping points (more)
	 * @param fromDate - null if not set delivery from date; interval filter start param
	 * @param toDate - null if not set, delivery to date; interval filter end param
	 * @return
	 */
	private Map<String, OrderStatistic.MaterialStatisticEntry> getSumGroupByMaterial(String plantCd, Long shippingPointId, Date fromDate, Date toDate, String languageCd) {
		
		final ReadOnlyDao<ShippingStatEntity> dao = getDaoProvider().getShippingStatEntityDao();		
		final Filter<ShippingStatEntity> filt = getStatFilter(plantCd, shippingPointId, null, fromDate, toDate, languageCd);
		if (filt == null) {
			return new HashMap<String, OrderStatistic.MaterialStatisticEntry>();
		}
		//only those count, which can at least count back to tons according to nominators
		filt.addExpression(Restrictions.eq(ShippingStatEntity.ATTR_ORDER_UOM_DIMENSION_CD, Constant.UOM_TONS));
		
		//this is handled separately because needs to differentiate between 3 cases:
		//1) if container is loaded -> you know the net
		//2) if the order is dispatched on a truck + trailer + container you know the masterdata tare weight / capacity. (-> container.MAX_NET_WEIGHT_T, similar to the chambers of truck/trailer)
		//3) if not yet dispatched: just go for the ordered_uom_cd. This uom you never have to check for a certain code. Only the ordered_uom_cd.uom_dimension_cd must be 'TO'.
		
//		filt.addExpression(Restrictions.neq(OrdItemAssignmtLoadEntity.ATTR_ORDERED_UOM_CD, Constant.UOM_LKW));
		
		filt.addExpression(Restrictions.or(
				//neither LKW nor CONTAINER
				Restrictions.and(
						Restrictions.neq(ShippingStatEntity.ATTR_ORDERED_UOM_CD, Constant.UOM_LKW),
						Restrictions.neq(ShippingStatEntity.ATTR_ORDERED_UOM_CD, Constant.UOM_CONTAINER)
				),
				//or if it is LKW or CONTAINER
				Restrictions.and(
						//if it happens to be LKW, it's exact weight could not be retained, since it is not dispatched yet - use the ordered measure
						Restrictions.eq(ShippingStatEntity.ATTR_ORDERED_UOM_CD, Constant.UOM_LKW),
						Restrictions.isNull(ShippingStatEntity.ATTR_TRUCK_MAX_NET_WEIGHT_T)
					),
				Restrictions.and(
							//if it happens to be CONTAINER, it's exact weight could not be retained, since it is not dispatched yet - use the ordered measure
							Restrictions.eq(ShippingStatEntity.ATTR_ORDERED_UOM_CD, Constant.UOM_CONTAINER),
							Restrictions.isNull(ShippingStatEntity.ATTR_CONTAINER_MAX_NET_WEIGHT_T)
						)					
				)
				);
		
		final JPAQuery<ShippingStatEntity> jpaQuery = dao.createJPAQuery();
		jpaQuery.setFilter(filt);
		jpaQuery.addGroupBy(ShippingStatEntity.ATTR_MATERIAL_TXT);
		jpaQuery.addSelect(SelectExpression.simpleSelect(ShippingStatEntity.ATTR_MATERIAL_TXT));
		jpaQuery.addGroupBy(ShippingStatEntity.ATTR_SAP_MATERIAL_ID);
		jpaQuery.addSelect(SelectExpression.simpleSelect(ShippingStatEntity.ATTR_SAP_MATERIAL_ID));
		jpaQuery.addSelect(SelectExpression.sum(ShippingStatEntity.ATTR_ORDERED_MEASURE));
		jpaQuery.addGroupBy(ShippingStatEntity.ATTR_PRODUCTION_PLANT_CD);
		jpaQuery.addSelect(SelectExpression.simpleSelect(ShippingStatEntity.ATTR_PRODUCTION_PLANT_CD));
		jpaQuery.addGroupBy(ShippingStatEntity.ATTR_PRODUCTION_PLANT_NAME);
		jpaQuery.addSelect(SelectExpression.simpleSelect(ShippingStatEntity.ATTR_PRODUCTION_PLANT_NAME));
		
		@SuppressWarnings("unchecked")
		final List<Object[]> statMaterial = dao.find(jpaQuery);		
		final Map<String, OrderStatistic.MaterialStatisticEntry> materialAndWeight = new HashMap<String, OrderStatistic.MaterialStatisticEntry>();
		
		for (Object[] pair : statMaterial) {
			String name = (String) pair[0];
			Long id = (Long) pair[1];
			Double weight = ((Double)pair[2]);
			String productionPlantCd = (String)pair[3];
			String productionPlantName = (String)pair[4];
			double weightDouble = 0d;
			if (weight != null) {
				weightDouble = weight.doubleValue();
			}
			OrderStatistic.MaterialStatisticEntry o = new OrderStatistic().new MaterialStatisticEntry(id.longValue(), name, weightDouble, productionPlantCd, productionPlantName );
			materialAndWeight.put(id.toString() + productionPlantCd, o);
		}

		return  materialAndWeight;
	}
	
	/**
	 * Returns a List with Object[3] containing the name, id, and total sum ordered from the material with name+id.
	 * Example: Object[3]: {'beton', 56, 34.2t}. The  SUPPOSED ordered weight. ONLY SUPPOSED, since the unit is LKW, and it
	 * does not have a common calculation to tons. The weight is max_gross_weight-tare_weight.
	 * @param plantCd TODO
	 * @param shippingPointId - null if not applicable; acceptable shipping points (more)
	 * @param fromDate - null if not set delivery from date; interval filter start param
	 * @param toDate - null if not set, delivery to date; interval filter end param
	 * @param specialUOM UOM_LKW or UOM_CONTAINER, which need a special sum calculation
	 * @return
	 */
	private Map<String, OrderStatistic.MaterialStatisticEntry> getSpecialSumGroupByMaterial(String plantCd, Long shippingPointId, Date fromDate, Date toDate, String languageCd, String specialUOM) {
		
		final ReadOnlyDao<ShippingStatEntity> dao = getDaoProvider().getShippingStatEntityDao();
		final Filter<ShippingStatEntity> filt = getStatFilter(plantCd, shippingPointId, null, fromDate, toDate, languageCd);		
		if (filt == null) {
			return new HashMap<String, OrderStatistic.MaterialStatisticEntry>();
		}
		final JPAQuery<ShippingStatEntity> jpaQuery = dao.createJPAQuery();
		jpaQuery.setFilter(filt);
		jpaQuery.addGroupBy(ShippingStatEntity.ATTR_MATERIAL_TXT);
		jpaQuery.addSelect(SelectExpression.simpleSelect(ShippingStatEntity.ATTR_MATERIAL_TXT));
		jpaQuery.addGroupBy(ShippingStatEntity.ATTR_SAP_MATERIAL_ID);
		jpaQuery.addSelect(SelectExpression.simpleSelect(ShippingStatEntity.ATTR_SAP_MATERIAL_ID));
		jpaQuery.addGroupBy(ShippingStatEntity.ATTR_PRODUCTION_PLANT_CD);
		jpaQuery.addSelect(SelectExpression.simpleSelect(ShippingStatEntity.ATTR_PRODUCTION_PLANT_CD));
		jpaQuery.addGroupBy(ShippingStatEntity.ATTR_PRODUCTION_PLANT_NAME);
		jpaQuery.addSelect(SelectExpression.simpleSelect(ShippingStatEntity.ATTR_PRODUCTION_PLANT_NAME));
		
		if (specialUOM.equals(Constant.UOM_LKW)) {
			
			filt.addExpression(Restrictions.eq(ShippingStatEntity.ATTR_ORDERED_UOM_CD, Constant.UOM_LKW));
			filt.addExpression(Restrictions.isNotNull(ShippingStatEntity.ATTR_TRUCK_MAX_NET_WEIGHT_T));
			jpaQuery.addSelect(SelectExpression.sum(ShippingStatEntity.ATTR_TRUCK_MAX_NET_WEIGHT_T));
		}
		else if (specialUOM.equals(Constant.UOM_CONTAINER)) {
			filt.addExpression(Restrictions.eq(ShippingStatEntity.ATTR_ORDERED_UOM_CD, Constant.UOM_CONTAINER));
			filt.addExpression(Restrictions.isNotNull(ShippingStatEntity.ATTR_CONTAINER_MAX_NET_WEIGHT_T));
			jpaQuery.addSelect(SelectExpression.sum(ShippingStatEntity.ATTR_CONTAINER_MAX_NET_WEIGHT_T));
		}
			

		@SuppressWarnings("unchecked")
		final List<Object[]> statMaterial = dao.find(jpaQuery);		
		final Map<String, OrderStatistic.MaterialStatisticEntry> materialAndWeight = new HashMap<String, OrderStatistic.MaterialStatisticEntry>();
				
		for (Object[] pair : statMaterial) {
			String name = (String) pair[0];
			Long id = (Long) pair[1];
			String productionPlantCd = (String)pair[2];
			String productionPlantName = (String)pair[3];
			Double weight = ((Double)pair[4]);
			double weightDouble = 0d;
			if (weight != null) {
				weightDouble = weight.doubleValue();
			}
			OrderStatistic.MaterialStatisticEntry o = new OrderStatistic().new MaterialStatisticEntry(id.longValue(), name, weightDouble, productionPlantCd, productionPlantName );
			materialAndWeight.put(id.toString() + productionPlantCd, o);
		}

		return  materialAndWeight;
	}

	/**
	 * Returns the sum of ordered weight, only for such items, which can be calculated to tons.
	 * given according to the filter parameters. Needed for the statistics.
	 * @param statusCdList - list of status codes, in case null, ORDERED_MEASURE id count otherwise net measure, in case ORDERED_MEASURE is used, it must be considered if the order is
	 * already dispatched to a truck, and that will be summed up in a later step, so those must be left out
	 * @param plantCd TODO
	 * @param shippingPointId - null if not applicable; acceptable shipping points (more)
	 * @param fromDate - null if not set delivery from date; interval filter start param
	 * @param toDate - null if not set, delivery to date; interval filter end param
	 * @param useNetMeasure TODO
	 * @return
	 */
	private double getOrderQueryWeightSum(List<Long> statusCdList, String plantCd, Long shippingPointId, Date fromDate, Date toDate, String languageCd, boolean useNetMeasure) {
		
		final ReadOnlyDao<ShippingStatEntity> dao = getDaoProvider().getShippingStatEntityDao();		
		final Filter<ShippingStatEntity> filt = getStatFilter(plantCd, shippingPointId, statusCdList, fromDate, toDate, languageCd);
				
		if (filt == null) {
			return 0d;
		}
		//only those count, which can at least count back to tons according to nominators
		filt.addExpression(Restrictions.eq(ShippingStatEntity.ATTR_ORDER_UOM_DIMENSION_CD, Constant.UOM_TONS));
		
		final JPAQuery<ShippingStatEntity> jpaQuery = dao.createJPAQuery();
		jpaQuery.setFilter(filt);
		
		if (!useNetMeasure) {
			filt.addExpression(
					
							
					Restrictions.or(
					//neither LKW nor CONTAINER
							Restrictions.and(
									Restrictions.neq(ShippingStatEntity.ATTR_ORDERED_UOM_CD, Constant.UOM_LKW),
									Restrictions.neq(ShippingStatEntity.ATTR_ORDERED_UOM_CD, Constant.UOM_CONTAINER)
							),
					//or if it is LKW or CONTAINER
					Restrictions.and(
							//if it happens to be LKW, it's exact weight could not be retained, since it is not dispatched yet - use the ordered measure
							Restrictions.eq(ShippingStatEntity.ATTR_ORDERED_UOM_CD, Constant.UOM_LKW),
							Restrictions.isNull(ShippingStatEntity.ATTR_TRUCK_MAX_NET_WEIGHT_T)
						),
					Restrictions.and(
								//if it happens to be CONTAINER, it's exact weight could not be retained, since it is not dispatched yet - use the ordered measure
								Restrictions.eq(ShippingStatEntity.ATTR_ORDERED_UOM_CD, Constant.UOM_CONTAINER),
								Restrictions.isNull(ShippingStatEntity.ATTR_CONTAINER_MAX_NET_WEIGHT_T)
							)					
					)
					);
			
			jpaQuery.addSelect(SelectExpression.sum(ShippingStatEntity.ATTR_ORDERED_MEASURE));
		}
		else{
			jpaQuery.addSelect(SelectExpression.sum(ShippingStatEntity.ATTR_LOAD_NET_MEASURE));
		}
		
		@SuppressWarnings("unchecked")
		final List<Double> list = dao.find(jpaQuery);
		double weight = 0d;
		if (!list.isEmpty()) {
			Double bd = (Double) list.get(0);
			if (bd != null){					
				weight = bd.doubleValue();
			}
		}
		
		return weight;
	}	
	
	/**
	 * ATTR_TRUCK_MAX_NET_WEIGHT_T, ATTR_CONTAINER_MAX_NET_WEIGHT_T. in case there is no information about the calculation to tons the
	 * truck related default values are considered. These must be involved in the statistics.
	 * @param statusCdList - list of status codes
	 * @param shippingPointId - null if not applicable; acceptable shipping points (more)
	 * @param plantCd TODO
	 * @param fromDate - null if not set delivery from date; interval filter start param
	 * @param toDate - null if not set, delivery to date; interval filter end param
	 * @param specialUOM TODO
	 * @return
	 */
	private double getOrderQuerySpecialWeightSum(List<Long> statusCdList, Long shippingPointId, String plantCd, Date fromDate, Date toDate, String languageCd, String specialUOM) {
		
		final ReadOnlyDao<ShippingStatEntity> dao = getDaoProvider().getShippingStatEntityDao();		
		final Filter<ShippingStatEntity> filt = getStatFilter(plantCd, shippingPointId, statusCdList, fromDate, toDate, languageCd);
		if (filt == null) {
			return 0d;
		}
		
		final JPAQuery<ShippingStatEntity> jpaQuery = dao.createJPAQuery();
		jpaQuery.setFilter(filt);
		if (specialUOM.equals(Constant.UOM_LKW)) {			
			filt.addExpression(Restrictions.eq(ShippingStatEntity.ATTR_ORDERED_UOM_CD, Constant.UOM_LKW));
			
			jpaQuery.addSelect(SelectExpression.sum(ShippingStatEntity.ATTR_TRUCK_MAX_NET_WEIGHT_T));
		}
		else if (specialUOM.equals(Constant.UOM_CONTAINER)) {
			filt.addExpression(Restrictions.eq(ShippingStatEntity.ATTR_ORDERED_UOM_CD, Constant.UOM_CONTAINER));
			
			jpaQuery.addSelect(SelectExpression.sum(ShippingStatEntity.ATTR_CONTAINER_MAX_NET_WEIGHT_T));
		}
				
		@SuppressWarnings("unchecked")
		final List<Double> list = dao.find(jpaQuery);
		double weight = 0d;
		if (!list.isEmpty()){
			Double bd = (Double) list.get(0);
			if (bd != null){					
				weight = bd.doubleValue();
			}
		}
		
		return weight;
	}
	

	/**
	 * Simply returns the count of order results given according to the filter parameters.
	 * @param plantCd TODO
	 * @param shippingPointId - null if not applicable; acceptable shipping points (more)
	 * @param statusIdList = null if not applicable, acceptable status cd list
	 * @param fromDate - null if not set delivery from date; interval filter start param
	 * @param toDate - null if not set, delivery to date; interval filter end param
	 * @param languageCd - language code (for ex. 'en')
	 * @return
	 */
	private long getCountOrderResult(String plantCd, Long shippingPointId, List<Long> statusIdList, Date fromDate, Date toDate, String languageCd) {
					
		final ReadOnlyDao<ShippingStatEntity> dao = getDaoProvider().getShippingStatEntityDao();		
		final Filter<ShippingStatEntity> filter = getStatFilter(plantCd, shippingPointId, statusIdList, fromDate, toDate, languageCd);
		
		if (filter == null) {
			return 0l;
		}
		// TODO boon, 20100906: Use count-distinct
		final JPAQuery<ShippingStatEntity> jpaQuery = dao.createJPAQuery();
		jpaQuery.addSelect(SelectExpression.simpleSelect(ShippingStatEntity.ATTR_ORDER_ID));
		jpaQuery.setDistinct(true);
		jpaQuery.setFilter(filter);
		
		final List<?> result = dao.find(jpaQuery);
		long count = result == null ? 0l : (long) result.size();
		return count;
	}	

	/**
	 * Loads all those shipping points, which are authenticated for the user, and which belong to 
	 * the selected plant.
	 * @param plantCd
	 * @return
	 */
	public List<ShippingpointEntity> loadShippingPointsForPlant(String plantCd) {
		// [[
		List<ShippingpointEntity> result = null;
		Object[][] params = {{"plantCd", plantCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadShippingPointsForPlant", params);
		}
		// ]]
		try {
			final ReadOnlyDao<ShippingpointEntity> dao = getDaoProvider().getShippingpointEntityDao();			
			final Filter<ShippingpointEntity> filter = dao.createFilter();
			
			filter.addExpression(Restrictions.eq(ShippingpointEntity.ATTR_PLANT_CD, plantCd));
			filter.addExpression(Order.asc(ShippingpointEntity.ATTR_SHIPPINGPOINT_NAME));
			return result = dao.find(filter);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public boolean preCheckTruckIsEmpty(Long truckId, Long assignmentId){
		// [[
		boolean result = false;
		Object[][] params = {{"truckId", truckId},
							{"assignmentId", assignmentId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "preCheckTruckIsEmpty", params);
		}
		// ]]
		try {
			if (assignmentId == null){
				return result = false;
			}
			
			final List<AssignmentPartEntity> list = orderService.getUnloadedAssignmnentPartsForTruck(truckId, assignmentId);
			final boolean isempty = (list == null || list.isEmpty());
						
			return result = isempty;
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
	 * @param load
	 * @param partDao
	 * @return
	 */
	public AssignmentPartEntity findLoadAssignmentPartForLoad(final LoadEntity load) {
		
		final ReadOnlyDao<AssignmentPartEntity> partDao = getDaoProvider().getAssignmentPartEntityDao();
		final Filter<AssignmentPartEntity> f = partDao.createFilter();
		
		f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_LOAD_ID, load.getLoadId()));
		
		return partDao.findSingle(f);
	}

	/**
	 * @param partDao
	 * @param load
	 * @return
	 */
	public OrderEntity findNotLoadedOrderForLoad(LoadEntity load) {
		// [[
		OrderEntity result = null;
		Object[][] params = {{"load", load}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findNotLoadedOrderForLoad", params);
		}
		// ]]
		try {
			final ReadOnlyDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
			final Collection<OrderItemEntity> orderItemList = orderService.getOrderItems(load);
			final List<Long> orderIdList = new ArrayList<Long>();
			
			for (OrderItemEntity orderItem : orderItemList) {
				orderIdList.add(orderItem.getOrder().getOrderId());
			}
			Long [] notLoadedStatuses = {
				Constant.ORDER_STATUS_NEW,
				Constant.ORDER_STATUS_TRANSMITTED_TO_POOL,
				Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK,
				Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER,
				Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK,
				Constant.ORDER_STATUS_DISPATCHED,
				Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER,	
				Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK,	
				Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT,
				Constant.ORDER_STATUS_LOADING
			};
			
			//drive to part
			final Filter<OrderEntity> filter = orderDao.createFilter();
			filter.addExpression(Restrictions.in(OrderEntity.ATTR_ORDER_STATUS_CD, Arrays.asList(notLoadedStatuses)));
			if ((orderIdList != null) && (!orderIdList.isEmpty())) {
				filter.addExpression(Restrictions.in(OrderEntity.ATTR_ORDER_ID, orderIdList));
			}
			
			final List<OrderEntity> orderList = orderDao.find(filter);			
			if ((orderList != null) && (!orderList.isEmpty())) {
				return result = (OrderEntity) orderList.get(0);
			}
			else{
				return result = null;
			}
			
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
	 * @param partDao
	 * @param assignment
	 * @return
	 */
	public AssignmentPartEntity findFirstUnloadPartForAssignment(
			final AssignmentEntity assignment) {
		ReadWriteDao<AssignmentPartEntity> partDao = getDaoProvider().getAssignmentPartEntityDao();
		//drive to part
		Filter partFilter2 = new FilterImpl();
		partFilter2.addExpression(
				Restrictions.and(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_ID, assignment.getAssignmentId()), 
				Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_PART_TYPE_CD, Constant.ASSIGNMENT_PART_TYPE_UNLOAD)));
		partFilter2.addExpression(Order.asc(AssignmentPartEntity.ATTR_ASSIGNMENT_PART_SEQ_NO));
		List part2 = partDao.find(partFilter2);
		
		if ( part2 == null || part2.size() < 1 ){
			///no unload assignemnt part found
			LOG.error("Unload part was not present for this assignment " + assignment.getAssignmentId());
			throw new LogonException("auftragbearbeitung.errors.savefailed", Reason.RES_ORDER_PROCESSING_ERROR);
		}
		
		AssignmentPartEntity driveToPart = (AssignmentPartEntity) part2.get(0);
		return driveToPart;
	}


	/**
	 * @param partDao
	 * @param order
	 * @return
	 */
	public AssignmentPartEntity findUnloadPartForOrder(
			final OrderEntity order) {
		ReadWriteDao<AssignmentPartEntity> partDao = getDaoProvider().getAssignmentPartEntityDao();
		//unload part
		Filter filter = new FilterImpl();
		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ORDER_ID, order.getOrderId()));
		AssignmentPartEntity unloadPart = partDao.findSingle(filter);
		
		if (unloadPart == null){
			///no unload assignment part found
			LOG.error("Unload part was not present for this order " + order.getOrderId());
			throw new LogonException("auftragbearbeitung.errors.savefailed", Reason.RES_ORDER_PROCESSING_ERROR);
		}
		
		return unloadPart;
	}


	/**
	 * Returns a map with the orderIds as keys and the OrderOutputdocEntity.orderOutputdocIds as values
	 * @param orderIds - list of orderIds
	 * @return
	 */
	public Map getOrderDeliveryNoteMap(List orderIds) {
		 // [[
		 Map result = null;
		 Object[][] params = {{"orderIds", orderIds}};
		 boolean logDebugEnabled = LOG.isDebugEnabled();
		 if (logDebugEnabled) {
			 LOG.entering(this, "getOrderDeliveryNoteMap", params);
		 }
		 // ]]
		 try {
			 if (orderIds == null || orderIds.isEmpty()) {
				 return result = new HashMap();
			 }
		 	
		 	Map resultMap = new HashMap();
			ReadWriteDao<OrderOutputdocEntity> dao = getDaoProvider().getOrderOutputdocEntityDao();
		 	
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.in(OrderOutputdocEntity.ATTR_ORDER_ID, orderIds));
			
			JPAQuery jpaQuery = dao.createJPAQuery();
			jpaQuery.addGroupBy("order.orderId");
			jpaQuery.addSelect(SelectExpression.simpleSelect("order.orderId"));
			jpaQuery.addSelect(SelectExpression.max("pdfOutputdoc.outputdocId"));
			jpaQuery.setFilter(filter);
			List list = dao.find(jpaQuery);
		 	
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				Object[] object = (Object[]) iterator.next();
				Long orderId = (Long) object[0];
				Long pdfOutputId = (Long) object[1];
				resultMap.put(orderId, pdfOutputId);
			}
			return result = resultMap;
		 	
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
	 * Cancels the load on the weighing system and creates a BizEvent type 
	 * CANCEL_PARALLEL. 
	 */
	public void cancelOnWeighSystem(Long assignmentId, Long shippingpointId) {
		// [[
		Object[][] params = {{"assignmentId", assignmentId},
							{"shippingpointId", shippingpointId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "cancelOnWeighSystem", params);
		}
		// ]]
		try {
			BizeventEntity event = new BizeventEntity();
			AssignmentEntity assignment = getDaoProvider().getAssignmentEntityDao().findById(assignmentId);
			ShippingpointEntity sp = getDaoProvider().getShippingpointEntityDao().findById(shippingpointId);
			event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_CANCEL_ON_WEIGH_SYSTEM);
			event.setAssignment(assignment);
			event.setTruck(assignment.getTruck());
			event.setEventTs(new Date());
			monitoringTablesService.processBizevent(event);
			
			orderService.cancelOnWeighSystem(assignment, sp);
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
	 * Retrieves a list with assignment related time info.
	 * @param orderIdList
	 * @param context depending on the context filter is going to be added
	 * @return
	 */
	private List<OrdLoadSchedInfoEntity> getAssignmentTimeInfoForOrders(List<Long> orderIdList) {
		// [[
		List result1 = null;
		Object[][] params = {{"orderIdList", orderIdList}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getAssignmentTimeInfoForOrders", params);
		}
		// ]]
		try {
			List<OrdLoadSchedInfoEntity> arrivalTimes = new ArrayList<OrdLoadSchedInfoEntity>();
			if (!orderIdList.isEmpty()) {
				ReadOnlyDao<OrdLoadSchedInfoEntity> dao = getDaoProvider().getOrdLoadSchedInfoEntityDao();
				Filter filt = new FilterImpl();
				filt.addExpression(Restrictions.in(OrdLoadSchedInfoEntity.ATTR_ORDER_ID, orderIdList));
				arrivalTimes = dao.find(filt);
			}
			return result1 = arrivalTimes;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result1);
			}
		// ]]
		}
	}
	
	/**
	 * See description of orderservice.
	 * @param orderItemId
	 * @return
	 */
	public List getContainers(Long orderItemId) {
		// [[
		List result = null;
		Object[][] params = {{"orderItemId", orderItemId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getContainers", params);
		}
		// ]]
		try {
			if (orderItemId == null) {
				return result = new ArrayList();
			}
			ReadWriteDao<OrderItemEntity> dao = getDaoProvider().getOrderItemEntityDao();
			OrderItemEntity item = dao.findById(orderItemId);
			List resultList = orderService.getContainers(item);
			return result = resultList;
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
	 * Fetches one row from the view.
	 * @param compositeId {@link OrdItemAssignmtLoadEntity.compositeId} which was selected on the Shipping screen's grid.
	 * @return
	 */
	public OrdItemAssignmtLoadEntity fetchOrdItemAssignmtLoadEntity(String compositeId, String language) {
		// [[
		OrdItemAssignmtLoadEntity result = null;
		Object[][] params = {{"compositeId", compositeId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchOrdItemAssignmtLoadEntity", params);
		}
		// ]]
		try {
			ReadOnlyDao<OrdItemAssignmtLoadEntity> ordItemAssignmtLoadEntityDao = getDaoProvider().getOrdItemAssignmtLoadEntityDao();
			Filter<OrdItemAssignmtLoadEntity> filter = ordItemAssignmtLoadEntityDao.createFilter();
			filter.addExpression(Restrictions.eq(OrdItemAssignmtLoadEntity.ATTR_COMPOSITE_ID, compositeId));
			filter.addExpression(Restrictions.eq(OrdItemAssignmtLoadEntity.ATTR_LANG_CD, language));
			return result = ordItemAssignmtLoadEntityDao.findSingle(filter);
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
	 * Fetches alternativ UoM for a given material (introduced in LOGON-6715)
	 * @param sapMaterial The material in question
	 * @return 
	 */
	public List<SapMaterialAltUomEntity> getAlternativeUoms(SapMaterialEntity sapMaterial, 
			List<String> fuzzyUomCds) {
		// [[
		List<SapMaterialAltUomEntity> result = null;
		Object[][] params = {{"sapMaterial", sapMaterial},
							{"fuzzyUomCds", fuzzyUomCds}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getAlternativeUoms", params);
		}
		// ]]
		try {
			ReadOnlyDao<SapMaterialAltUomEntity> sapMatAltDao = getDaoProvider()
					.getSapMaterialAltUomEntityDao();
			Filter<SapMaterialAltUomEntity> sapMatAltFilter = sapMatAltDao.createFilter();
			sapMatAltFilter.addExpression(Restrictions.eq(SapMaterialAltUomEntity.ATTR_SAP_MATERIAL, sapMaterial));
			
			// exclude vague units of measurement (fuzzy_quantity_flag)
			sapMatAltFilter.addExpression(Restrictions.not(
					Restrictions.in(SapMaterialAltUomEntity.ATTR_UOM_CD, fuzzyUomCds)));
			
			List<SapMaterialAltUomEntity> sapMatAltUoms = sapMatAltDao.find(sapMatAltFilter);
			return result = sapMatAltUoms;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private List<SapMaterialAltUomEntity> getAlternativeUoms(List<Long> sapMaterialIds, 
			List<String> fuzzyUomCds) {
		// [[
		List<SapMaterialAltUomEntity> result = null;
		Object[][] params = {{"sapMaterialIds", sapMaterialIds},
							{"fuzzyUomCds", fuzzyUomCds}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getAlternativeUoms", params);
		}
		// ]]
		try {
			if (sapMaterialIds == null || sapMaterialIds.isEmpty()) {
				return result = Collections.<SapMaterialAltUomEntity>emptyList();
			}
			
			ReadOnlyDao<SapMaterialAltUomEntity> sapMatAltDao = getDaoProvider()
					.getSapMaterialAltUomEntityDao();
			Filter<SapMaterialAltUomEntity> sapMatAltFilter = sapMatAltDao.createFilter();
			sapMatAltFilter.addExpression(
					DbUtils.createSplittedInRestrictionOfIds(SapMaterialAltUomEntity.ATTR_SAP_MATERIAL_ID, sapMaterialIds));
			
			// exclude vague units of measurement (fuzzy_quantity_flag)
			if (fuzzyUomCds != null && !fuzzyUomCds.isEmpty()) {
				sapMatAltFilter.addExpression(Restrictions.not(Restrictions.in(
						SapMaterialAltUomEntity.ATTR_UOM_CD, fuzzyUomCds)));
			}
			
			List<SapMaterialAltUomEntity> sapMatAltUoms = sapMatAltDao.find(sapMatAltFilter);
			return result = sapMatAltUoms;
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
	 * Fetches the list of Unit of Measure entities which are fuzzy and returns 
	 * their codes in a list.
	 * 
	 * @return The list of (fuzzy) UoM codes. 
	 */
	public List<String> fetchFuzzyUomCds() {
		// [[
		List<String> result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getFuzzyUomCds");
		}
		// ]]
		try {			
			Filter<UomBsEntity> filter = new FilterImpl<UomBsEntity>();
			filter.addExpression(Restrictions.eq(UomBsEntity.ATTR_FUZZY_QUANTITY_FLAG, Boolean.TRUE));
			
			List<UomBsEntity> fuzzyUomEntities = referentialDataService.findBsEntityFiltered(UomBsEntity.class, filter);
			
			List<String> fuzzyUomCds = new ArrayList<String>();
			for (UomBsEntity uomBs : fuzzyUomEntities) {
				String uomBsCd = uomBs.getUomCd();
				fuzzyUomCds.add(uomBsCd);
			}
			
			return result = fuzzyUomCds;
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
	 * Returns the next part for the assignment.
	 * @param assignmentId
	 * @return
	 */
	public AssignmentPartEntity getNextAssignmentPart(Long assignmentId, Long seqNo) {
		final ReadWriteDao<AssignmentPartEntity> assignmentPartDao = getDaoProvider().getAssignmentPartEntityDao();
		final Filter<AssignmentPartEntity> filter = assignmentPartDao.createFilter();

		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_ID, assignmentId));
		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_PART_SEQ_NO, seqNo));
		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT + "."+ AssignmentEntity.ATTR_ACTIVE_FLAG, true));

		AssignmentPartEntity assignmentPart = assignmentPartDao.findSingle(filter);

		return assignmentPart;
	}
}

