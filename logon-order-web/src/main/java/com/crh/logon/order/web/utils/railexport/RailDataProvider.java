package com.crh.logon.order.web.utils.railexport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.crh.logon.order.web.forms.raildispo.DispoForm;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.beans.RailDispoExportOrderRow;
import com.crh.logon.business.services.beans.RailDispoExportParameters;
import com.crh.logon.business.services.beans.RailDispoExportWagonRow;
import com.crh.logon.business.services.beans.UomConversionResult;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LoadingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.MonitoringTablesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.ShippingPointServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UomConverterServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UserServiceLocalInterface;
import com.crh.logon.business.services.utils.AssignmentPartWrapper;
import com.crh.logon.business.services.utils.ContainerWrapper;
import com.crh.logon.business.services.utils.DispoLoadtargetPrioWrapper;
import com.crh.logon.business.services.utils.LoadWrapper;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.OrderWrapper;
import com.crh.logon.business.services.utils.RailAssignmentWrapper;
import com.crh.logon.business.services.utils.RailLoadUtils;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.business.services.utils.WagonWrapper;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.MatCompatMatEntity;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.refdata.TimezoneBsEntity;
import com.crh.logon.persistence.entities.views.DispoAssignPartListEntity;
import com.crh.logon.persistence.entities.views.DispoExtOrderListEntity;
import com.crh.logon.persistence.entities.views.DispoOrderItemListEntity;
import com.crh.logon.persistence.entities.views.DispoOrderListEntity;
import com.crh.logon.persistence.entities.views.DispoPlantTrackListEntity;
import com.crh.logon.persistence.entities.views.DispoRailAssListEntity;
import com.crh.logon.persistence.entities.views.DispoWContainerListEntity;
import com.crh.logon.persistence.entities.views.DispoWagonListEntity;
import com.crh.logon.persistence.entities.views.DispoWagonLoadListEntity;
import com.crh.logon.persistence.entities.views.DispoWagonTypeListEntity;
import com.crh.logon.persistence.entities.views.RailPlantEntity;
import com.crh.logon.persistence.entities.views.RailPlantSpEntity;
import com.crh.logon.persistence.entities.views.SapbpRailwayStationEntity;
import com.crh.logon.utils.AuthUtils;
import com.crh.logon.utils.LogonConversionException;
import com.crh.logon.utils.time.DateTimeFormat;
import com.crh.logon.utils.time.DateTimeZoneFactory;
import com.crh.logon.web.utils.Authorization;

public class RailDataProvider {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailDataProvider.class);

	private UserServiceLocalInterface userService;
	private DispoServiceLocalInterface dispoService;
	private UomConverterServiceLocalInterface unitOfMeasurementConverterService;
	private LoadingServiceLocalInterface loadingService;
	private ShippingPointServiceLocalInterface shippingPointService;
	private MonitoringTablesServiceLocalInterface monitoringService;
	
	private TimeZone userTimeZone;
	private DispoForm dispoForm;
	private Locale userLocale;
	private String appDefaultTimeZoneCd;
	private String shippingPtTimeZoneCd;

	// Export caching
	private Map<Long, RailwayStationEntity> shippingPointStations = new HashMap<Long, RailwayStationEntity>();
	private Map<Long, SapbpRailwayStationEntity> shipToStations = new HashMap<Long, SapbpRailwayStationEntity>();
	private Map<Long, LoadingpointEntity> loadingPoints = new HashMap<Long, LoadingpointEntity>();
	private Map<Long, Date> sentToCarEvents = new HashMap<Long, Date>();
	private Map<Long, String> loadNhms = new HashMap<Long, String>();

	public RailDataProvider(
			UserServiceLocalInterface userService, 
			DispoServiceLocalInterface dispoService, 
			UomConverterServiceLocalInterface unitOfMeasurementConverterService,
			LoadingServiceLocalInterface loadingService, 
			ShippingPointServiceLocalInterface shippingPointService, 
			MonitoringTablesServiceLocalInterface monitoringService,
			TimeZone userTimeZone, 
			DispoForm dispoForm, 
			Locale userLocale){
		this.userTimeZone = userTimeZone;
		this.dispoForm = dispoForm;
		this.userLocale = userLocale;
		this.dispoService = dispoService;
		this.unitOfMeasurementConverterService = unitOfMeasurementConverterService;
		this.loadingService = loadingService;
		this.userService = userService;
		this.shippingPointService = shippingPointService;
		this.monitoringService = monitoringService;
	}

	public RailDispoExportParameters getExportParameters() {

		Long userId = AuthUtils.getUserId();
		UserEntity exportingUser = userService.fetchUserEntity(userId);
		String exportingUserName = LogonUtil.getFullName(exportingUser, 100);

		List<DispoPlantTrackListEntity> selectedTracks = dispoForm.getSelectedPlantTracks();
		RailPlantEntity plant = dispoForm.getSelectedRailPlant();
		List<RailPlantSpEntity> shippingPoints = dispoForm.getSelectedShippinpointsStations();
		shippingPtTimeZoneCd = findFirstShippingPointTimezone(shippingPoints);
		appDefaultTimeZoneCd = DateTimeZoneFactory.getDefaultTimeZoneId();

		Date currentDate = new Date();
		DateTime convertedDateTime = convertDateToDataTimeInTimezone(userLocale, currentDate, appDefaultTimeZoneCd, shippingPtTimeZoneCd);
		String[] splitDateTimeAsStringParts = DateTimeFormat.splitDateTimeAsStringParts(convertedDateTime);

		String timeOfExport = splitDateTimeAsStringParts[0] + " " + splitDateTimeAsStringParts[1];
		String utcOffSet = "UTC"+splitDateTimeAsStringParts[2];

		List<WagonWrapper> wagonWrapperList = getWagonList();
		List<OrderWrapper> orderWrapperList = getOrderList();

		prefetchInformation(wagonWrapperList, orderWrapperList);

		List<RailDispoExportWagonRow> wagonRows = convertToWagonRows(wagonWrapperList);
		List<RailDispoExportOrderRow> orderRows = convertToOrderRows(orderWrapperList);

		Map<Long, Boolean> statuses = new HashMap<Long, Boolean>();
		statuses.put(Constant.WAGON_STATUS_AT_SHIPPINGPOINT, dispoForm.getStatusAtSp());
		statuses.put(Constant.WAGON_STATUS_INCOMING, dispoForm.getStatusIncoming());
		statuses.put(Constant.WAGON_STATUS_LOADED, dispoForm.getStatusLoaded());
		statuses.put(Constant.WAGON_STATUS_SUBMITTED, dispoForm.getStatusTransmitted());

		RailDispoExportParameters exportParameters 
			= new RailDispoExportParameters(userLocale, exportingUserName, timeOfExport, utcOffSet, plant, shippingPoints, selectedTracks, orderRows, wagonRows, statuses);

		return exportParameters;
	}

	public List<OrderWrapper> getOrderList() {
		// [[
		List<OrderWrapper> result = new ArrayList<OrderWrapper>(0);
		Object[][] params = {{"dispoForm", dispoForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getOrderList", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.RAIL_DISPATCHING_R);
						
			List<DispoOrderListEntity> oList = dispoService.fetchRailOrdersForDispo(
					new ArrayList<Long>(dispoForm.getSelectedShippingpoints()),
					userLocale, 
					dispoForm.getOrderNumberFilter());
			
			Map<Long, OrderWrapper> oMap = new HashMap<Long, OrderWrapper>();
			Map<Long, List<DispoWagonTypeListEntity>> sMap =
					new HashMap<Long, List<DispoWagonTypeListEntity>>();
			
			for (DispoOrderListEntity dol : oList) {
				List<DispoWagonTypeListEntity> wtlist = sMap.get(dol.getShiptoSapBpId());
				if (wtlist == null){
					wtlist = new LinkedList<DispoWagonTypeListEntity>();
					sMap.put(dol.getShiptoSapBpId(), wtlist);
				}
				OrderWrapper ow = new OrderWrapper(dol, wtlist, getUserTimeZone());
			
				oMap.put(dol.getOrderId(), ow);
			}
			
			
			List<DispoOrderItemListEntity> iList = dispoService.fetchRailOrderItemsForDispo(
					new ArrayList<Long>(oMap.keySet()),
					userLocale.getLanguage(), 
					dispoForm.getProductNameFilter());
			
			for (DispoOrderItemListEntity doil : iList) {
				OrderWrapper ow = oMap.get(doil.getOrderId());
				ow.addItem(doil);
				if(!result.contains(ow)) {
					result.add(ow);
				}
			}
			
			List<DispoWagonTypeListEntity> wList = dispoService.fetchWagonTypesForDispo(
					new ArrayList<Long>(sMap.keySet()), userLocale);
			
			for (DispoWagonTypeListEntity dwtl : wList) {
				List<DispoWagonTypeListEntity> wl = sMap.get(dwtl.getShiptoSapBpId());
				if (wl != null){
					wl.add(dwtl);
				}
			}
			
			dispoForm.setOrderList(result);
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
	
	 public List<WagonWrapper> getWagonList() {
			// [[
			List<WagonWrapper> resultWagonList = new ArrayList<WagonWrapper>();
			Object[][] params = {{"dispoForm", dispoForm},
								{"userLocale", userLocale}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "getWagonList", params);
			}
			// ]]
			try {
				Authorization.authorize(Authorization.RAIL_DISPATCHING_R);			
				String materialNameOrNumberFilter = dispoForm.getProductNameFilter();
				String orderNumberFilter = dispoForm.getOrderNumberFilter();
				String wagonNumberFilter = dispoForm.getWagonNumberFilter();		
				//handle filtering partial conditions like twelve digit length wagonNumber, and ignore the other two filter condition
				if(dispoForm.getRailDispoFiltering()) {
					if(null != wagonNumberFilter && (wagonNumberFilter.length() == Constant.FILTER_FOR_RAIL_WAGON_NR_LENGTH)) {
						orderNumberFilter = "";
						materialNameOrNumberFilter = "";
					}
				}
				
				Set<Long> selectedShippinpointsStationIds =
						dispoForm.getSelectedShippinpointsStationIds();
				
				//Get wagons for shippingpoint
				Date selectedDate = dispoForm.getDate();
				
				// Set default date to today if there is not date specified
				if(selectedDate == null){
					selectedDate = new Date();
				}
				
				List<DispoWagonListEntity> wagonList = dispoService.fetchWagonsForDispo(
						new ArrayList<Long>(selectedShippinpointsStationIds),
						new ArrayList<Long>(dispoForm.getSelectedTracks()),
						dispoForm.getShowNoTrack(),
						userLocale.getLanguage(),
						selectedDate,
						wagonNumberFilter,
						userTimeZone);
				Map<Long, Set<Long>> matCompatMap = new HashMap<Long, Set<Long>>();			
				Map<Long, WagonWrapper> allWagons = new HashMap<Long, WagonWrapper>();
				for (DispoWagonListEntity dwl : wagonList) {
					WagonWrapper ww = new WagonWrapper(dwl, !selectedShippinpointsStationIds.contains(
							dwl.getLastKnownRailwayStationId()), unitOfMeasurementConverterService,
													   getUserTimeZone());
					allWagons.put(dwl.getWagonId(), ww);
					if (dwl.getMatCompatId() != null) {
						matCompatMap.put(dwl.getMatCompatId(), new HashSet<Long>());
					}
				}
				
				//Get belonging containers to the selected wagons
				List<DispoWContainerListEntity> containerList = dispoService.fetchRailContainersForDispo(
						new ArrayList<Long>(allWagons.keySet()),
						userLocale.getLanguage());
				for (DispoWContainerListEntity dwcl : containerList) {
					ContainerWrapper cw = new ContainerWrapper(dwcl, unitOfMeasurementConverterService, getUserTimeZone());
					WagonWrapper ww = allWagons.get(dwcl.getCurrentWagonId());
					if (ww != null) {
						ww.addContainer(cw);
					}
				}
				
				//get the assignments of the selected wagons
				List<DispoRailAssListEntity> assignmentList = dispoService.fetchRailAssignmentsForDispo(
					selectedDate, new ArrayList<Long>(allWagons.keySet()),
						new ArrayList<Long>(dispoForm.getSelectedShippingpoints()), null);
				Map<Long, WagonWrapper> assignedWagons = new HashMap<Long, WagonWrapper>();
				for (DispoRailAssListEntity dral : assignmentList) {
					RailAssignmentWrapper raw = new RailAssignmentWrapper(dral, getUserTimeZone());
					
					//remove those wagons from allWagons which has assignment
					WagonWrapper ww = allWagons.remove(dral.getWagonId());
					if ((ww != null) && (ww.getAssignment() == null)) {
						ww.setAssignment(raw);
						assignedWagons.put(dral.getAssignmentId(), ww);
					}
				}
				Map<Long, WagonWrapper> notAssignedWagons = allWagons;
				
				//get the parts of the selected wagon's assignments
				List<DispoAssignPartListEntity> assignmentPartList = dispoService.fetchAssignmentPartsToOrderNumberForDispo(
						new ArrayList<Long>(assignedWagons.keySet()),
						userLocale.getLanguage(),
						orderNumberFilter);
				Map<Long, RailAssignmentWrapper> oMap = new HashMap<Long, RailAssignmentWrapper>();
				for (DispoAssignPartListEntity dapl : assignmentPartList) {
					RailAssignmentWrapper raw = assignedWagons.get(dapl.getAssignmentId()).getAssignment();
					raw.addPart(dapl);
					oMap.put(dapl.getOrderId(), raw);
				}
				
				//get the contained order items of the selected wagon's assignments parts
				List<DispoOrderItemListEntity> oiList = dispoService.fetchRailOrderItemsForDispo(
						new ArrayList<Long>(oMap.keySet()),
						userLocale.getLanguage(),
						materialNameOrNumberFilter);
				Map<Long, RailAssignmentWrapper> oiMap = new HashMap<Long, RailAssignmentWrapper>();
				for (DispoOrderItemListEntity doil : oiList) {
					RailAssignmentWrapper raw = oMap.get(doil.getOrderId());
					raw.addItem(doil);
					oiMap.put(raw.getAssignment().getAssignmentId(), raw);
				}
				
				List<MatCompatMatEntity> matCompatMatList = dispoService.fetchCompatibleMaterials(
						new ArrayList<Long>(matCompatMap.keySet()));
				for (MatCompatMatEntity mcm : matCompatMatList) {
					Set<Long> cmpList = matCompatMap.get(mcm.getFromMatCompat().getMatCompatId());
					cmpList.add(mcm.getToSapMaterial().getSapMaterialId());
				}
				
				for (RailAssignmentWrapper raw : oiMap.values()) {
					WagonWrapper ww = assignedWagons.get(raw.getAssignment().getAssignmentId());
					if (ww != null){
						if (ww.getWagon().getMatCompatId() != null) {
							ww.setMatCompatSet(matCompatMap.get(ww.getWagon().getMatCompatId()));
						}
						resultWagonList.add(ww);
					}
				}
				
				for ( WagonWrapper assignedWagon : assignedWagons.values() ){
					assignedWagon.initPossibleMaterialList();
				}
				
				//we dont want to typecheck the wagons if material and order number is empty - because the wagon can be incompatible and
				// does not displayed in the resultset.
			List<OrderWrapper> railOrders = dispoForm.getOrderList();
				if(!com.crh.logon.utils.StringUtils.empty(orderNumberFilter)
								|| !com.crh.logon.utils.StringUtils.empty(materialNameOrNumberFilter)) {
				List<WagonWrapper> compatibleAndNotAssignedWagons = matchWagonsTypeWithOrders(
						notAssignedWagons.values(), railOrders);
					resultWagonList.addAll(compatibleAndNotAssignedWagons); 
				}
				else {
					resultWagonList.addAll(notAssignedWagons.values());
				}
				
				final List<WagonWrapper> filteredResult = new ArrayList<WagonWrapper>();
				for (final WagonWrapper ww : resultWagonList) {
					DispoWagonListEntity wagon = ww.getWagon();
					RailAssignmentWrapper assignment = ww.getAssignment();
					Long transportCatCd = wagon.getWagonTrspCatCd();
					
					if (transportCatCd == Constant.WAGON_TKATEGORIE_BAG){
						List<DispoWagonLoadListEntity> loads = dispoService.fetchWagonLoads(
								wagon.getWagonId(),
								((assignment != null)
										? assignment.getAssignment().getAssignmentId()
										: null),
								wagon.getLangCd());
						
						final List<LoadWrapper> wrappedLoads;
						
						if (loads != null) {
							wrappedLoads = new ArrayList<LoadWrapper>();
							
							for (DispoWagonLoadListEntity load : loads) {
								LoadWrapper lw = new LoadWrapper(load, unitOfMeasurementConverterService);
								wrappedLoads.add(lw);
							}
						}
						else {
							wrappedLoads = null;
						}
						
						ww.setLoads(wrappedLoads);
					}
					
					final boolean isReturning = ww.getIsReturning();
					final boolean isLoaded = ww.isLoaded();
					final boolean isTransmitted = ww.isTransmitted();
					final boolean isAtSp = !isReturning && !isLoaded && !isTransmitted;
					
					// Determine whether pre-loading should be enabled
					if (isAtSp 
							&& wagon.getLoadingpointId() != null 
							&& transportCatCd != Constant.WAGON_TKATEGORIE_CONTAINER
							&& assignment == null) {
						ww.setPreLoadEnabled(true);
					}
					
					if (dispoForm.getStatusTransmitted() && isTransmitted) {
						ww.setDeterminedStatus(Constant.WAGON_STATUS_SUBMITTED);
						filteredResult.add(ww);
						continue;
					}
					if (dispoForm.getStatusLoaded() && isLoaded && !isTransmitted) {
						ww.setDeterminedStatus(Constant.WAGON_STATUS_LOADED);
						filteredResult.add(ww);
						continue;
					}
					if (dispoForm.getStatusAtSp() && isAtSp) {
						ww.setDeterminedStatus(Constant.WAGON_STATUS_AT_SHIPPINGPOINT);
						filteredResult.add(ww);
						continue;
					}
					if (dispoForm.getStatusIncoming() && isReturning) {
						ww.setDeterminedStatus(Constant.WAGON_STATUS_INCOMING);
						filteredResult.add(ww);
						continue;
					}
				}
				
				Collection<DispoLoadtargetPrioWrapper> possiblePreloads =
						dispoService.fetchPossiblePreloads(
								dispoForm.getSelectedPlant(),
								userLocale.getLanguage(),
								selectedDate);
				
				for (final WagonWrapper ww : filteredResult) {				
					if (ww.getWagon().getMatCompatId() != null) {
						ww.setMatCompatSet(matCompatMap.get(ww.getWagon().getMatCompatId()));
					}
				RailLoadUtils.computePossibleOrdersToLoad(ww, railOrders, selectedDate, userTimeZone);
					ww.setPossiblePreloads(possiblePreloads);
				}
				
				//If the filtering is enabled the assigned items has to be in the top of the list. No sort is needed
				if(!dispoForm.getRailDispoFiltering()) {
					Collections.sort(filteredResult);
				}
				
				return resultWagonList = filteredResult;
			}
			finally {
			// [[
				if (logDebugEnabled) {
					LOG.exiting(resultWagonList);
				}
			// ]]
			}
	    }

	 /**
	     * Match the wagon types with the selected list of orders
	     * There is no table to get the information thus it has to be done 'manualy'.
	     * 
	     * @param wagons The wagons based on various search at the shipping points
	     * @param orders The selected orders
	     * @return typeCompatibleWagons Those wagons which are compatible one of the orderList.
	     * */
	    private List<WagonWrapper> matchWagonsTypeWithOrders(Collection<WagonWrapper> wagons, List<OrderWrapper> orders) {
	    	//[[    	
	    	List<WagonWrapper> typeCompatibleWagons = new ArrayList<WagonWrapper>();
	    	Object[][] params = {{"wagons", wagons}, 
	    						{"orders", orders}};
	    	boolean logDebugEnabled = LOG.isDebugEnabled();
	    	if (logDebugEnabled) {
	    		LOG.entering(this, "matchWagonsTypeWithOrders", params);
	    	}
	 		// ]]
	    	try {
	    		for(WagonWrapper wagon : wagons) {
	    			for(OrderWrapper order : orders) {
	    				if(RailLoadUtils.matchWagonTypeWithOrder(wagon, order)) {
	    					typeCompatibleWagons.add(wagon);
	    					break;
	    				}
	    			}
	    		}
	    		return typeCompatibleWagons;
	    	}
	    	finally {
	    	// [[
	    		if (logDebugEnabled) {
	    			LOG.exiting(typeCompatibleWagons);
	    		}
	    	// ]]
	    	}
	    }

	List<RailDispoExportWagonRow> convertToWagonRows(List<WagonWrapper> wagonWrapperList) {


		List<RailDispoExportWagonRow> wagonRows = new ArrayList<RailDispoExportWagonRow>();

		for (WagonWrapper wrapper : wagonWrapperList) {

			DispoWagonListEntity wagon = wrapper.getWagon();

			AssignmentPartWrapper loadPartWrapper = null;
			AssignmentPartWrapper unloadPartWrapper = null;
			AssignmentPartWrapper deliveryPartWrapper = null;
			DispoRailAssListEntity assignment = null;

			RailAssignmentWrapper assignmentWrapper = wrapper.getAssignment();
			if (assignmentWrapper != null) {

				assignment = assignmentWrapper.getAssignment();

				LinkedList<AssignmentPartWrapper> parts = assignmentWrapper.getParts();

				// Not all parts available necessarily
				loadPartWrapper = findAssignmentPartByType(parts, Constant.ASSIGNMENT_PART_TYPE_LOAD);
				unloadPartWrapper = findAssignmentPartByType(parts, Constant.ASSIGNMENT_PART_TYPE_UNLOAD);
				deliveryPartWrapper = findAssignmentPartByType(parts, Constant.ASSIGNMENT_PART_TYPE_TO);

			}

			if (wagon.getWagonTrspCatCd() == Constant.WAGON_TKATEGORIE_CONTAINER) {

				List<ContainerWrapper> containers = wrapper.getContainers();

				if (containers == null || containers.isEmpty()) {

					RailDispoExportWagonRow wagonRow = new RailDispoExportWagonRow();
					buildWagonRowBasedOnWagonAttributes(wagonRow, wrapper);

					if (assignmentWrapper != null) {

						// Cannot find order item because there is no container on the wagon to match with
						DispoOrderItemListEntity orderItem = null;
						buildWagonRowBasedOnAssignmentAttributes(wrapper, assignment, loadPartWrapper, unloadPartWrapper, deliveryPartWrapper, orderItem, wagonRow);

					}

					setCurrentWagonStatus(wagonRow, wrapper, unloadPartWrapper);

					wagonRow.setLoadNet(wrapper.getNetQuantityInTons());
					wagonRow.setLoadGross(calculateGrossWeight(wrapper.getNetQuantityInTons(), wagon.getTareWeightT()));

					wagonRows.add(wagonRow);

				}
				else {

					BigDecimal sumTareT = wagon.getTareWeightT();
					BigDecimal sumNetQuantityT = null;
					BigDecimal sumGrossT = null;

					List<RailDispoExportWagonRow> containerRows = new ArrayList<RailDispoExportWagonRow>();

					for (ContainerWrapper containerWrapper : containers) {
	
						DispoWContainerListEntity container = containerWrapper.getContainer();
	
						RailDispoExportWagonRow wagonRow = new RailDispoExportWagonRow();
	
						buildWagonRowBasedOnWagonAttributes(wagonRow, wrapper);
						wagonRow.setContainerNames(container.getContainerName());
						setCurrentWagonStatus(wagonRow, wrapper, unloadPartWrapper);
	
						String loadNHM = loadNhms.get(container.getSapMaterialId());
						if (loadNHM != null) {
							wagonRow.setNhm(loadNHM);
						}
	
						if (assignmentWrapper != null) {
	
							DispoOrderItemListEntity orderItem = findOrderItemByLoadId(unloadPartWrapper, wagon, container);
							buildWagonRowBasedOnAssignmentAttributes(wrapper, assignment, loadPartWrapper, unloadPartWrapper, deliveryPartWrapper, orderItem, wagonRow);
	
						}

						if (wagonRow.getLoadMaterial() == null) {
							wagonRow.setLoadMaterial(container.getMaterialName());
						}
	
						BigDecimal netQuantityInTons = containerWrapper.getNetQuantityInTons();
						if (netQuantityInTons != null) {
							if (sumNetQuantityT == null) {
								sumNetQuantityT = BigDecimal.ZERO;
							}
							sumNetQuantityT = sumNetQuantityT.add(netQuantityInTons);
						}

						if (sumTareT != null) {
							sumTareT = sumTareT.add(container.getTareWeightT());
						}

						wagonRow.setLoadNet(netQuantityInTons);

						containerRows.add(wagonRow);
	
					}

					if (sumNetQuantityT != null) {
						sumGrossT = sumTareT.add(sumNetQuantityT);
					}

					for (RailDispoExportWagonRow wagonRow : containerRows) {
						wagonRow.setLoadTare(sumTareT);
						wagonRow.setLoadGross(sumGrossT);
					}
					wagonRows.addAll(containerRows);

				}

			}
			else {

				// If there are no assignments and no loads we only create one record based on the core attributes of the wagon
				if (assignmentWrapper == null && wrapper.getLoads() == null) {

					RailDispoExportWagonRow wagonRow = new RailDispoExportWagonRow();
					buildWagonRowBasedOnWagonAttributes(wagonRow, wrapper);
					setCurrentWagonStatus(wagonRow, wrapper, unloadPartWrapper);

					wagonRow.setLoadNet(wrapper.getNetQuantityInTons());
					wagonRow.setLoadGross(calculateGrossWeight(wagon.getTareWeightT(), wrapper.getNetQuantityInTons()));

					wagonRows.add(wagonRow);

				}
				// No assignment but with loads
				else if (assignmentWrapper == null && wrapper.getLoads() != null) {

					// Sum gross should be the same for all items connected to the same assignment
					// GROSS = TARE (wagon) + SUM(NET)
					// Using late initialization, only set if net quantity available
					BigDecimal sumNetT = null;

					List<RailDispoExportWagonRow> loadWagonRows = new ArrayList<RailDispoExportWagonRow>();
					List<LoadWrapper> loads = wrapper.getLoads();
					for (LoadWrapper loadWrapper : loads) {

						DispoWagonLoadListEntity load = loadWrapper.getLoad();

						RailDispoExportWagonRow wagonRow = new RailDispoExportWagonRow();

						buildWagonRowBasedOnWagonAttributes(wagonRow, wrapper);
						buildWagonRowBasedOnLoadAttributes(loadWrapper, wagonRow, load);
						setCurrentWagonStatus(wagonRow, wrapper, unloadPartWrapper);

						BigDecimal loadedQuantity = load.getNetQuantity();
						if (loadedQuantity != null) {
							try {

								UomConversionResult ucr = unitOfMeasurementConverterService.convert(
										loadedQuantity, load.getNetQuantityUomCd(), Constant.UOM_TONS, load.getSapMaterialId(), null, null, false);
								BigDecimal netQuantity = ucr.getQuantity();
								if (sumNetT == null) {
									sumNetT = netQuantity;
								}
								else {
									sumNetT = sumNetT.add(netQuantity);
								}

								wagonRow.setLoadNet(netQuantity);

							}
							catch (LogonConversionException conversionException) {
								LOG.error(String.format("Cannot convert %s to %s", load.getNetQuantityUomCd(), Constant.UOM_TONS), conversionException);
							}
						}

						loadWagonRows.add(wagonRow);

					}

					if (sumNetT != null) {
						BigDecimal sumGrossT = sumNetT.add(wagon.getTareWeightT());
						for (RailDispoExportWagonRow wagonRow : loadWagonRows) {
							wagonRow.setLoadGross(sumGrossT);
						}
					}

					wagonRows.addAll(loadWagonRows);

				}
				// Based on assignment
				else {

					// Sum gross should be the same for all items connected to the same assignment
					// GROSS = TARE (wagon) + SUM(NET)
					// Using late initialization, only set if net quantity available
					BigDecimal sumNetT = null;

					List<RailDispoExportWagonRow> orderItemWagonRows = new ArrayList<RailDispoExportWagonRow>();
					List<DispoOrderItemListEntity> orderItems = unloadPartWrapper.getOrderItems();
					for (DispoOrderItemListEntity orderItem : orderItems) {

						RailDispoExportWagonRow wagonRow = new RailDispoExportWagonRow();

						buildWagonRowBasedOnWagonAttributes(wagonRow, wrapper);
						buildWagonRowBasedOnAssignmentAttributes(wrapper, assignment, loadPartWrapper, unloadPartWrapper, deliveryPartWrapper, orderItem, wagonRow);
						setCurrentWagonStatus(wagonRow, wrapper, unloadPartWrapper);

						if (orderItem != null) {

							BigDecimal loadedQuantity = orderItem.getLoadedQuantity();
							if (loadedQuantity != null) {
								try {
	
									UomConversionResult ucr = unitOfMeasurementConverterService.convert(
											loadedQuantity, orderItem.getLoadedUomCd(), Constant.UOM_TONS, orderItem.getSapMaterialId(), null, null, false);
									BigDecimal netQuantity = ucr.getQuantity();
									if (sumNetT == null) {
										sumNetT = netQuantity;
									}
									else {
										sumNetT = sumNetT.add(netQuantity);
									}
	
									wagonRow.setLoadNet(netQuantity);
	
								}
								catch (LogonConversionException conversionException) {
									LOG.error(String.format("Cannot convert %s to %s", orderItem.getLoadedUomCd(), Constant.UOM_TONS), conversionException);
								}
							}

						}

						orderItemWagonRows.add(wagonRow);

					}

					if (sumNetT != null) {
						BigDecimal sumGrossT = sumNetT.add(wagon.getTareWeightT());
						for (RailDispoExportWagonRow wagonRow : orderItemWagonRows) {
							wagonRow.setLoadGross(sumGrossT);
						}
					}

					wagonRows.addAll(orderItemWagonRows);

				}

			}

		}

		return wagonRows;

	}

	private void buildWagonRowBasedOnLoadAttributes(
			LoadWrapper loadWrapper, RailDispoExportWagonRow wagonRow, DispoWagonLoadListEntity load) {

		wagonRow.setLoadMaterial(load.getMaterialDescription());

		String loadNHM = loadNhms.get(load.getSapMaterialId());
		if (loadNHM != null) {
			wagonRow.setNhm(loadNHM);
		}

	}

	private void buildWagonRowBasedOnAssignmentAttributes(
			WagonWrapper wrapper,
			DispoRailAssListEntity assignment,
			AssignmentPartWrapper loadPartWrapper,
			AssignmentPartWrapper unloadPartWrapper, 
			AssignmentPartWrapper deliveryPartWrapper,
			DispoOrderItemListEntity orderItem, 
			RailDispoExportWagonRow wagonRow) {

		if (loadPartWrapper != null){

			DispoAssignPartListEntity loadPart = loadPartWrapper.getAssignmentPart();
			Long shippingpointId = loadPart.getShippingpointId();

			RailwayStationEntity shippingPtStation = shippingPointStations.get(shippingpointId);

			if (shippingPtStation != null) {
				wagonRow.setShippintPtStationNo(shippingPtStation.getRailwayStationNo());
				wagonRow.setShippintPtStationName(shippingPtStation.getRailwayStationName());
				wagonRow.setShippintPtCountryCd(shippingPtStation.getUicCountry().getUicCountryCd());
			}

			wagonRow.setShippintPtName(loadPart.getShippingpointName());
			wagonRow.setDeparture(convertDateToCalendarInShippingPtTimezone(loadPart.getPlannedStart()));

		}

		if (unloadPartWrapper != null) {

			DispoAssignPartListEntity unloadPart = unloadPartWrapper.getAssignmentPart();
			Long shipToSapBpId = unloadPart.getShiptoSapBpId();
			SapbpRailwayStationEntity shiptoStation = shipToStations.get(shipToSapBpId);

			if (shiptoStation != null) {
				wagonRow.setShipToStationNo(shiptoStation.getRailwayStationNo());
				wagonRow.setShipToStationName(shiptoStation.getRailwayStationName());
				wagonRow.setShipToCountryCd(shiptoStation.getUicCountryCd());
			}

			wagonRow.setOrderNo(unloadPart.getOrderNumber());
			wagonRow.setSoldToNumber(unloadPart.getOrderSoldtoNumber());
			wagonRow.setSoldToName(unloadPart.getOrderSoldtoName());
			wagonRow.setSoldToName2(unloadPart.getOrderSoldtoName2());
			wagonRow.setShipToNumber(unloadPart.getShiptoSapId());
			wagonRow.setShipToName(unloadPart.getShiptoName());
			wagonRow.setShipToName2(unloadPart.getShiptoName2());
			wagonRow.setNoteDispatching(unloadPart.getNoteDispatching());
			wagonRow.setNoteRailwayDisp(unloadPart.getNoteRailwayDisp());

			wagonRow.setDeliveryFrom(convertDateToCalendarInShippingPtTimezone(unloadPart.getDeliveryFromDat()));
			wagonRow.setDeliveryTo(convertDateToCalendarInShippingPtTimezone(unloadPart.getDeliveryToDat()));
			wagonRow.setArrival(convertDateToCalendarInShippingPtTimezone(unloadPart.getPlannedStart()));
			wagonRow.setCarrierName(unloadPart.getCommCarrierName());

		}

		if (deliveryPartWrapper != null) {
			DispoAssignPartListEntity deliveryPart = deliveryPartWrapper.getAssignmentPart();
			wagonRow.setManipulationTime(convertDateToCalendarInShippingPtTimezone(deliveryPart.getPlannedStart()));
		}

		if (orderItem != null) {

			wagonRow.setOrderMaterialName(orderItem.getMaterialName());
			wagonRow.setOrderQty(orderItem.getOrderedQuantity());
			wagonRow.setOrderUom(orderItem.getUomName());
			wagonRow.setLoadMaterial(orderItem.getMaterialName());
			wagonRow.setEtranOut(orderItem.getEtranOutbound());
			wagonRow.setEtranRet(orderItem.getEtranReturn());

			String loadNHM = loadNhms.get(orderItem.getSapMaterialId());
			if (loadNHM != null) {
				wagonRow.setNhm(loadNHM);
			}

		}

		Date sentToCar = sentToCarEvents.get(assignment.getAssignmentId());
		Calendar convertedSentToCar = convertDateToCalendarInShippingPtTimezone(sentToCar);
		wagonRow.setToRailCo(convertedSentToCar);


	}

	private void buildWagonRowBasedOnWagonAttributes(
			RailDispoExportWagonRow wagonRow,
			WagonWrapper wrapper) {

		DispoWagonListEntity wagon = wrapper.getWagon();

		wagonRow.setWagonNo(wrapper.getWagonNo());
		wagonRow.setWagonType(wagon.getWagonTypeName());
		wagonRow.setWrittenTareWeightT(wagon.getWrittenTareWeightT());
		wagonRow.setTrackName(wagon.getTrackName());
		wagonRow.setPosition(wagon.getCurrentTrackPositionNo());
		wagonRow.setLoadTare(wagon.getTareWeightT());
		wagonRow.setWagonKeeperName(wagon.getWagonKeeperName());
		wagonRow.setWagonNoteRailwaySpedition(wagon.getWagonNoteRailwaySpedition());
		wagonRow.setWagonNoteDisposition(wagon.getWagonNoteDisposition());

		LoadingpointEntity loadingpointEntity = loadingPoints.get(wagon.getCurrentPlantTrackCd());
		if (loadingpointEntity != null) {
			wagonRow.setLoadingPtName(loadingpointEntity.getLoadingpointName());
		}

		wagonRow.setLoadMaterial(wagon.getMaterialName());
		wagonRow.setEtranOut(wagon.getEtranOutbound());
		wagonRow.setEtranRet(wagon.getEtranReturn());

	}

	/**
	 * The wagon status that was previously set on the Wagon Wrapper might be outdated.
	 * Hence we check and refresh when needed.
	 * Please see 'raildispo.js' where the GUI updates the status as well. 
	 * This is where this logic came from.
	 * @return current wagon status
	 */
	private void setCurrentWagonStatus(RailDispoExportWagonRow wagonRow, WagonWrapper wrapper, AssignmentPartWrapper unloadPart) {

		Long wagonStatus = wrapper.getDeterminedStatus();
		if (wagonStatus == Constant.WAGON_STATUS_INCOMING) {
			wagonRow.setStatus(wagonStatus);
			return;
		}

		Long currentStatus = Constant.WAGON_STATUS_AT_SHIPPINGPOINT;

		List<ContainerWrapper> containers = wrapper.getContainers();
		if (containers != null) {
			for (ContainerWrapper containerWrapper : containers) {
				DispoWContainerListEntity container = containerWrapper.getContainer();
				Long loadId = container.getLoadId();
				Date loadingTs = container.getLoadingTs();
				Long logonPackageTypeCd = container.getLogonPackageTypeCd();
				if (loadId != null && (loadingTs != null || logonPackageTypeCd == null)) {
					currentStatus = Constant.WAGON_STATUS_LOADED;
				}
			}
		}
		DispoWagonListEntity wagon = wrapper.getWagon();
		if (wagon.getLoadId() != null && (wagon.getLoadingTs() != null || wagon.getLogonPackageTypeCd() == null)) {
			currentStatus = Constant.WAGON_STATUS_LOADED;
		}
		if (unloadPart != null) {
			Long[] transmitCheckStatuses = new Long[]{
					Constant.ORDER_STATUS_DISPATCHED,
					Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER,
					Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK,
					Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT,
					Constant.ORDER_STATUS_LOADING,
					Constant.ORDER_STATUS_LOADED
			};
			List<Long> transmitCheckStatusesSet = Arrays.asList(transmitCheckStatuses);
			DispoAssignPartListEntity assignmentPart = unloadPart.getAssignmentPart();
			if (!transmitCheckStatusesSet.contains(assignmentPart.getOrderStatusCd())) {
				currentStatus = Constant.WAGON_STATUS_SUBMITTED;
			}
		}

		wagonRow.setStatus(currentStatus);

	}

	List<RailDispoExportOrderRow> convertToOrderRows(List<OrderWrapper> orderWrapperList) {

		Set<Long> logonOrdersConverted = new HashSet<Long>();
		Set<Long> extOrdersConverted = new HashSet<Long>();

		List<RailDispoExportOrderRow> orderRows = new ArrayList<RailDispoExportOrderRow>();

		for (OrderWrapper wrapper : orderWrapperList) {

			if (wrapper.getLogonOrder() != null) {

				DispoOrderListEntity logonOrder = wrapper.getLogonOrder();

				// The API that prepares the order wrappers for us will return as many copies of the order wrapper 
				// as many order items it has
				if (logonOrdersConverted.contains(logonOrder.getOrderNumber())) {
					continue;
				}

				RailwayStationEntity shippingPtrailwayStation = shippingPointStations.get(logonOrder.getShippingpointId());
				SapbpRailwayStationEntity shipToStation = shipToStations.get(logonOrder.getShiptoSapBpId());

				List<DispoOrderItemListEntity> items = wrapper.getItems();
				for (DispoOrderItemListEntity orderItem : items) {

					RailDispoExportOrderRow orderRow = new RailDispoExportOrderRow();

					orderRow.setOrderNumber(logonOrder.getOrderNumber());
					orderRow.setOrderMaterialName(orderItem.getMaterialName());
					orderRow.setOrderQty(orderItem.getOrderedQuantity());
					orderRow.setOrderUom(orderItem.getUomName());
					orderRow.setShippintPtName(logonOrder.getShippingpointName());
					orderRow.setLoadingPtName(orderItem.getLoadingpointName());
					if (shippingPtrailwayStation != null) {
						orderRow.setShippintPtStationNo(shippingPtrailwayStation.getRailwayStationNo());
						orderRow.setShippintPtStationName(shippingPtrailwayStation.getRailwayStationName());
						orderRow.setShippintPtCountryCd(shippingPtrailwayStation.getUicCountry().getUicCountryCd());
					}
					orderRow.setShipToNumber(logonOrder.getShiptoSapId());
					orderRow.setShipToName(logonOrder.getShiptoName());
					orderRow.setShipToName2(logonOrder.getShiptoName2());
					if (shipToStation != null) {
						orderRow.setShipToStationNo(shipToStation.getRailwayStationNo());
						orderRow.setShipToStationName(shipToStation.getRailwayStationName());
						orderRow.setShipToCountryCd(shipToStation.getUicCountryCd());
					}
					orderRow.setSoldToNumber(logonOrder.getSoldtoSapId());
					orderRow.setSoldToName(logonOrder.getSoldtoName());
					orderRow.setSoldToName2(logonOrder.getSoldtoName2());
					// LOGON-8742 the last departure time should be here
					orderRow.setManipulationTime(convertDateToCalendarInShippingPtTimezone(wrapper.getLastSwitchingTimeDep()));
					orderRow.setDeliveryFrom(convertDateToCalendarInShippingPtTimezone(logonOrder.getDeliveryFromDat()));
					orderRow.setDeliveryTo(convertDateToCalendarInShippingPtTimezone(logonOrder.getDeliveryToDat()));
					orderRow.setCarrierName(logonOrder.getCommCarrierName());
					orderRow.setShipToPrefs(buildWagonTypeList(wrapper.getWagonTypeList()));
					orderRow.setNoteRailwayDisp(logonOrder.getNoteRailwayDisp());
					orderRow.setNoteDispatching(logonOrder.getNoteDispatching());

					logonOrdersConverted.add(logonOrder.getOrderNumber());

					orderRows.add(orderRow);

				}

			}
			else if (wrapper.getExtOrder() != null) {

				DispoExtOrderListEntity extOrder = wrapper.getExtOrder();

				// The API that prepares the order wrappers for us will return as many copies of the order wrapper 
				// as many order items it has
				if (extOrdersConverted.contains(extOrder.getOrderNumber())) {
					continue;
				}

				List<DispoOrderItemListEntity> items = wrapper.getItems();
				for (DispoOrderItemListEntity orderItem : items) {

					RailDispoExportOrderRow orderRow = new RailDispoExportOrderRow();

					orderRow.setOrderNumber(extOrder.getOrderNumber());
					orderRow.setOrderMaterialName(orderItem.getMaterialName());
					orderRow.setOrderQty(orderItem.getOrderedQuantity());
					orderRow.setOrderUom(orderItem.getUomName());
					orderRow.setShippintPtName(extOrder.getShippingpointName());
					orderRow.setShipToName(extOrder.getShiptoName());
					orderRow.setManipulationTime(convertDateToCalendarInShippingPtTimezone(wrapper.getLastSwitchingTimeArr()));
					orderRow.setDeliveryFrom(convertDateToCalendarInShippingPtTimezone(extOrder.getDeliveryFromDat()));
					orderRow.setDeliveryTo(convertDateToCalendarInShippingPtTimezone(extOrder.getDeliveryToDat()));
					orderRow.setCarrierName(extOrder.getExecCarrierName());
					orderRow.setShipToPrefs(buildWagonTypeList(wrapper.getWagonTypeList()));
					orderRow.setNoteRailwayDisp(extOrder.getNoteRailwayDisp());
					orderRow.setNoteDispatching(extOrder.getNoteDispatching());

					extOrdersConverted.add(extOrder.getOrderNumber());

					orderRows.add(orderRow);

				}

			}

		}

		return orderRows;

	}

	/**
	 * Collects all additional 'to-be-fetched' information from the database and 
	 * fetches them to cache maps to avoid cyclic DB queries.
	 */
	private void prefetchInformation(
			List<WagonWrapper> wagonWrapperList, 
			List<OrderWrapper> orderWrapperList) {

		Set<Long> assignmentIds = new HashSet<Long>();
		Set<Long> shippingPointIds = new HashSet<Long>();
		Set<Long> shipToSapBpIds = new HashSet<Long>();
		Set<Long> plantTrackCds = new HashSet<Long>();
		Set<Long> sapMaterialIds = new HashSet<Long>();

		// Wagon data
		for (WagonWrapper wrapper : wagonWrapperList) {

			DispoWagonListEntity wagon = wrapper.getWagon();

			RailAssignmentWrapper railAssignment = wrapper.getAssignment();
			if (railAssignment != null) {

				LinkedList<AssignmentPartWrapper> parts = railAssignment.getParts();

				AssignmentPartWrapper loadPartWrapper = findAssignmentPartByType(parts, Constant.ASSIGNMENT_PART_TYPE_LOAD);
				if (loadPartWrapper != null) {
					DispoAssignPartListEntity loadPart = loadPartWrapper.getAssignmentPart();
					Long shippingpointId = loadPart.getShippingpointId();
					if (shippingpointId != null) {
						shippingPointIds.add(shippingpointId);
					}
				}

				AssignmentPartWrapper unloadPartWrapper = findAssignmentPartByType(parts, Constant.ASSIGNMENT_PART_TYPE_UNLOAD);
				if (unloadPartWrapper != null) {
					DispoAssignPartListEntity unloadPart = unloadPartWrapper.getAssignmentPart();
					Long shipToSapBpId = unloadPart.getShiptoSapBpId();
					if (shipToSapBpId != null) {
						shipToSapBpIds.add(shipToSapBpId);
					}
				}

				DispoRailAssListEntity assignment = railAssignment.getAssignment();
				assignmentIds.add(assignment.getAssignmentId());

				List<DispoOrderItemListEntity> orderItems = unloadPartWrapper.getOrderItems();
				for (DispoOrderItemListEntity orderItem : orderItems) {
					sapMaterialIds.add(orderItem.getSapMaterialId());
				}

			}
			
			Long currentPlantTrackCd = wagon.getCurrentPlantTrackCd();
			if (currentPlantTrackCd != null) {
				plantTrackCds.add(currentPlantTrackCd);
			}

			if (wagon.getWagonTrspCatCd() == Constant.WAGON_TKATEGORIE_CONTAINER) {
				
				List<ContainerWrapper> containers = wrapper.getContainers();
				for (ContainerWrapper containerWrapper : containers) {
					DispoWContainerListEntity container = containerWrapper.getContainer();
					sapMaterialIds.add(container.getSapMaterialId());
				}
				
			}

			List<LoadWrapper> loads = wrapper.getLoads();
			if (loads != null) {
				for (LoadWrapper loadWrapper : loads) {
					DispoWagonLoadListEntity load = loadWrapper.getLoad();
					if (load != null) {
						sapMaterialIds.add(load.getSapMaterialId());
					}
				}
			}

		}


		// Order Data
		for (OrderWrapper wrapper : orderWrapperList) {

			if (wrapper.getLogonOrder() != null) {
			
				DispoOrderListEntity logonOrder = wrapper.getLogonOrder();
				Long shippingpointId = logonOrder.getShippingpointId();
				Long shiptoSapBpId = logonOrder.getShiptoSapBpId();
				if (shippingpointId != null) {
					shippingPointIds.add(shippingpointId);
				}
				if (shiptoSapBpId != null) {
					shipToSapBpIds.add(shiptoSapBpId);
				}

			}

		}

		findEventTimeForAssignments(assignmentIds);
		findStationForShippingPoints(shippingPointIds);
		findStationForSapBps(shipToSapBpIds);
		findLoadingPointForPlantTrackCds(plantTrackCds);
		findNhmForSapMaterialIds(sapMaterialIds);

	}

	private void findNhmForSapMaterialIds(Set<Long> sapMaterialIds) {
		Map<Long, String> nhmResult	= loadingService.findNHMForSapMaterialIds(sapMaterialIds);
		loadNhms.putAll(nhmResult);
	}

	private void findStationForSapBps(Set<Long> sapBpIds) {
		Map<Long, SapbpRailwayStationEntity> stationResult = dispoService.findSapBpRailwayStations(sapBpIds);
		shipToStations.putAll(stationResult);
	}

	private void findLoadingPointForPlantTrackCds(Set<Long> plantTrackCds) {
		Map<Long, LoadingpointEntity> loadingPointsResult = dispoService.findLoadingPointsForPlantTrackCds(plantTrackCds);
		loadingPoints.putAll(loadingPointsResult);
	}

	private void findStationForShippingPoints(Set<Long> shippingPointIds) {
		Map<Long, RailwayStationEntity> shippingPointStationsResult = shippingPointService.findRailwayStationsForShippingPoints(shippingPointIds);
		shippingPointStations.putAll(shippingPointStationsResult);
	}

	private void findEventTimeForAssignments(Set<Long> assignmentIds) {				
		Map<Long, Date> sentToCarEventsResult = monitoringService.findBizeventTimesByAssignmentIds(assignmentIds, Constant.BIZEVENT_TYPE_TRANSMITTED_TO_RAIL);
		sentToCarEvents.putAll(sentToCarEventsResult);
	}

	private String findFirstShippingPointTimezone(List<RailPlantSpEntity> shippingPoints) {
		if (shippingPoints == null || shippingPoints.isEmpty()) {
			return appDefaultTimeZoneCd;
		}
		RailPlantSpEntity firstShippingPointPlant = shippingPoints.get(0);
		Long shippingpointId = firstShippingPointPlant.getShippingpointId();
		ShippingpointEntity shippingPoint = shippingPointService.getShippingPointById(shippingpointId);
		TimezoneBsEntity timezone = shippingPoint.getTimezone();
		if (timezone == null) {
			return appDefaultTimeZoneCd;
		}
		return timezone.getTimezoneCd();
	}

	private BigDecimal calculateGrossWeight(BigDecimal tare, BigDecimal net) {
		if (tare == null || net == null) {
			return null;
		}
		return tare.add(net);
	}

	private DispoOrderItemListEntity findOrderItemByLoadId(AssignmentPartWrapper unloadPartWrapper, DispoWagonListEntity wagon, DispoWContainerListEntity container) {
		if (unloadPartWrapper != null) {
			List<DispoOrderItemListEntity> orderItems = unloadPartWrapper.getOrderItems();
			if (orderItems != null) {
				for (DispoOrderItemListEntity dispoOrderItemListEntity : orderItems) {
					if (dispoOrderItemListEntity.getLoadId() == wagon.getLoadId() ||
						dispoOrderItemListEntity.getLoadId() == container.getLoadId()) {
						return dispoOrderItemListEntity;
					}
				}
			}
		}
		return null;
	}

	private AssignmentPartWrapper findAssignmentPartByType(List<AssignmentPartWrapper> parts, long partTypeCd) {
		for (AssignmentPartWrapper assignmentPartWrapper : parts) {
			final DispoAssignPartListEntity assignmentPart = assignmentPartWrapper.getAssignmentPart();
			if (assignmentPart.getAssignmentPartTypeCd() == partTypeCd) {
				return assignmentPartWrapper;
			}
		}
		return null;
	}

	private String buildWagonTypeList(List<DispoWagonTypeListEntity> wagonTypes) {

		if (wagonTypes == null) {
			return "";
		}
		
		List<String> wagonTypeTexts = new ArrayList<String>();
		for (DispoWagonTypeListEntity wagonType : wagonTypes) {
			wagonTypeTexts.add(wagonType.getWagonTypeName());
		}
		return StringUtils.join(wagonTypeTexts, ';');

	}

	private Calendar convertDateToCalendarInShippingPtTimezone(Date date) {
		if (date == null) {
			return null;
		}
		if (appDefaultTimeZoneCd.equals(shippingPtTimeZoneCd)) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			return calendar;
		}
		DateTime convertedDateTime = convertDateToDataTimeInTimezone(userLocale, date, appDefaultTimeZoneCd, shippingPtTimeZoneCd);
		if (convertedDateTime == null) {
			return null;
		}
		return convertedDateTime.toCalendar(userLocale);
	}

	private DateTime convertDateToDataTimeInTimezone(Locale locale, Date from, String fromTz, String toTz) {

		if (from == null) {
			return null;
		}

		DateTimeZone fromDateTimeZone = DateTimeZoneFactory.forID(fromTz);
		DateTimeZone targetTimeZone = DateTimeZoneFactory.forID(toTz);
		DateTime fromDate = new DateTime(from, fromDateTimeZone);
		DateTime toDate = fromDate.withZone(targetTimeZone);

		return toDate;

	}

	public TimeZone getUserTimeZone() {
		return userTimeZone;
	}

	public void setUserTimeZone(TimeZone userTimeZone) {
		this.userTimeZone = userTimeZone;
	}

}