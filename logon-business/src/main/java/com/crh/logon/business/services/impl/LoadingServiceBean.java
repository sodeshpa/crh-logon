/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.beans.LoadedQuantityData;
import com.crh.logon.business.services.dispatchers.LoadingServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.AsyncMopEventOutServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LoadingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.MonitoringTablesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.utils.BizEventUtil;
import com.crh.logon.business.services.utils.ConfSeqLock;
import com.crh.logon.business.services.utils.DbUtils;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.EventSource;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialPlantEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
@AdnTrace
public class LoadingServiceBean extends LoadingServiceDispatcher implements LoadingServiceLocalInterface{
	
	private static final boolean VALIDATE_COMPOSITION = true;

	private static final boolean ACCURATE = true;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LoadingServiceBean.class);

	private static final BigDecimal NO_GROSS_QUANTITY = null;

	private static final boolean SEND_GOODS_MOVEMENTS = true;

	private static final ConfSeqLock WITHOUT_LOCK = null;
	
	@EJB private OrderServiceLocalInterface orderService;
	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;
	@EJB private MonitoringTablesServiceLocalInterface monitoringTablesService;
	@EJB private AsyncMopEventOutServiceLocalInterface asyncMopEventOutService;
	
	public void updateLoadAndSendGoodsMovements(LoadEntity load, LoadingpointEntity lp,
			BigDecimal tareWeightT, BigDecimal grossWeightT, 
			BigDecimal netQuantity, UomBsEntity netQuantityUom, 
			Date loadingStartTime, Date loadingEndTime, 
			EventSource source, ConfSeqLock lock) {
		updateLoad(load, lp, tareWeightT, grossWeightT, netQuantity, netQuantityUom,
				loadingStartTime, loadingEndTime, SEND_GOODS_MOVEMENTS, source, lock);
	}

	public void updateLoad(LoadEntity load, LoadingpointEntity loadingpoint, BigDecimal tareWeightT,
			BigDecimal grossWeightT, BigDecimal netQuantity, UomBsEntity netQuantityUom,
			Date loadingStartTime, Date loadingEndTime,
			boolean isGoodsMovementSendingRequired,
			EventSource source, ConfSeqLock lock) {

			Long loadId = load.getLoadId();
			LoadEntity managedLoad = em.find(LoadEntity.class, loadId);
			BigDecimal origTareWeightT = managedLoad.getTareWeightT();
			BigDecimal origNetQuantity = managedLoad.getNetQuantity();
			UomBsEntity origNetQuantityUom = managedLoad.getNetQuantityUom();
			
			final boolean isChanged = (
					(netQuantity == null && origNetQuantity != null) ||
					(netQuantity != null && origNetQuantity == null) ||
					(tareWeightT == null && origTareWeightT != null) ||
					(tareWeightT != null && origTareWeightT == null) ||
					(netQuantity != null && origNetQuantity != null && !netQuantity.equals(origNetQuantity)) ||
					(netQuantityUom != null && origNetQuantityUom != null && !netQuantityUom.equals(origNetQuantityUom)) ||
					(tareWeightT != null && origTareWeightT != null && !tareWeightT.equals(origTareWeightT)));
			
			if (!isChanged) {
				return;
			}
			
			final boolean isLoadingAborted = (netQuantity != null && (BigDecimal.ZERO.compareTo(netQuantity) == 0));
			
			final AssignmentPartEntity ap = findAssignmentPartForLoad(managedLoad);
			if (ap == null) {
				LOG.error("No load type assignment part was present for load " + loadId);
				throw new LogonException("auftragbearbeitung.errors.savefailed", Reason.RES_ORDER_PROCESSING_ERROR);
			}
			
			final AssignmentEntity assignment = ap.getAssignment();
			final OrderEntity singleOrder = findOrderForLoad(managedLoad);
			final Boolean reverseDirectedOrder = singleOrder != null
					&& LogonUtil.isOrderWithReversedDirection(singleOrder);
			
			Date now = new Date();
			if (managedLoad.getLoadingTs() != null && !isLoadingAborted) {
				final OrderEntity notYetLoadedOrder = findNotYetLoadedOrderForLoad(managedLoad,
						reverseDirectedOrder);
				if (notYetLoadedOrder == null) {
					LOG.info("SPECIAL CASE - UPDATING THE MEASURE DATA FROM LOADING TAB");
					
					// LOGON-6715 Support selection of UoM for load
					managedLoad.setNetQuantityUom(netQuantityUom);
					//only update the measure data
					orderService.resetMeasureData(managedLoad, tareWeightT, NO_GROSS_QUANTITY,
							netQuantity,
							now);
					
					// LOGON-6570 (Railwaycar will not back in the status Loading or Empty)
					if (netQuantity != null && isGoodsMovementSendingRequired) {
						orderService.createAndSaveGoodsMovementEntity(managedLoad);
					}
					
					// send message only in case a change has been made to the values, do NOT send
					// the message in case of deletion (most likely temporary deletion)
					if (managedLoad.getTareWeightT() != null
							&& managedLoad.getNetQuantity() != null
							&& assignment.getTruck() != null) {
						LOG.info("Sending LOADING STARTED/ENDED message to PDA related to load"
								+ managedLoad.getLoadId());
						asyncMopEventOutService.sendLoadingStartedLoadingEnded(
								assignment.getTruck(), ap, managedLoad.getTareWeightT(),
								managedLoad.getGrossWeightT(), managedLoad.getNetQuantity(),
								managedLoad.getNetQuantityUom(), managedLoad.getSapMaterial());
					}
					
					if (netQuantity != null) {
						BizeventEntity event = new BizeventEntity();
						event.setEventTs(now);
						event.setTruck(managedLoad.getTruck());
						event.setTrailer(managedLoad.getTrailer());
						event.setContainer(managedLoad.getContainer());
						event.setWagon(managedLoad.getWagon());
						event.setAssignment(assignment);
						
						if (loadingpoint != null) {
							event.setShippingpoint(loadingpoint.getShippingpoint());
						}
						
						event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_LOADED);
						
						event = monitoringTablesService.processBizevent(event);
						
						String eventDescription = ((managedLoad.getGrossWeightT() != null) ? 
								managedLoad.getGrossWeightT().toPlainString()
								: "");
						eventDescription += " " + Constant.UOM_TONS_TXT;
						event.setDescription(eventDescription);
					}
					return;
				}
			}
			
			if (managedLoad.getLoadingTs() == null && (tareWeightT != null || netQuantity != null)) {
				BizeventEntity event = new BizeventEntity();
				if (loadingStartTime != null) {
					event.setEventTs(loadingStartTime);
				}
				else {
					event.setEventTs(now);
				}
				event.setOrder(singleOrder);
				event.setTruck(managedLoad.getTruck());
				event.setContainer(managedLoad.getContainer());
				event.setWagon(managedLoad.getWagon());
				
				if (loadingpoint != null) {
					event.setShippingpoint(loadingpoint.getShippingpoint());
				}
				
				assignment.initialize();
				event.setAssignment(assignment);
				event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_LOADING);
				
				orderService.startLoad(event, managedLoad, loadingpoint, tareWeightT,
						source);
				
				monitoringTablesService.processBizevent(event);
			}
			
			if (netQuantity != null) {
				BizeventEntity event = new BizeventEntity();
				if (loadingEndTime != null) {
					event.setEventTs(loadingEndTime);
				}
				else {
					event.setEventTs(now);
				}
				event.setOrder(singleOrder);
				event.setTruck(managedLoad.getTruck());
				event.setContainer(managedLoad.getContainer());
				event.setWagon(managedLoad.getWagon());
				
				if (loadingpoint != null) {
					event.setShippingpoint(loadingpoint.getShippingpoint());
				}
				
				assignment.initialize();
				event.setAssignment(assignment);
				event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_LOADED);
				
				// TODO 27-Mar-2013/stich: load properties should be part of endLoad, this is tmp only
				managedLoad.setNetQuantityUom(netQuantityUom);

				orderService.endLoad(event, managedLoad, loadingpoint,
						tareWeightT, grossWeightT, netQuantity,
						true,
						true,
						isGoodsMovementSendingRequired,
						source,
						lock);
				
				monitoringTablesService.processBizevent(event);
			}
	}

	public void updateSharedLoadAndSendGoodsMovements(LoadEntity load, LoadingpointEntity loadingpoint,
			Map<Long, LoadedQuantityData> loadingData,
			BigDecimal tareWeightT, BigDecimal grossWeightT,
			Date loadingStartTime, Date loadingEndTime,
			EventSource source, ConfSeqLock lock) {
		updateSharedLoad(load, loadingpoint, loadingData, tareWeightT, grossWeightT, loadingStartTime,
				loadingEndTime, SEND_GOODS_MOVEMENTS, source, lock);
	}
	
	public void updateSharedLoad(LoadEntity load, LoadingpointEntity loadingpoint,
			Map<Long, LoadedQuantityData> loadingData,
			BigDecimal tareWeightT, BigDecimal grossWeightT,
			Date loadingStartTime, Date loadingEndTime,
			boolean isGoodsMovementSendingRequired,
			EventSource source, ConfSeqLock lock) {

			final Set<Long> orderItemIds = loadingData.keySet();
			final ReadWriteDao<OrderItemEntity> dao = getDaoProvider().getOrderItemEntityDao();
			final Filter<OrderItemEntity> f = dao.createFilter();
			f.addExpression(Restrictions.in(OrderItemEntity.ATTR_ORDER_ITEM_ID, orderItemIds));
			List<OrderItemEntity> orderItems = dao.find(f);
			
			UomBsEntity commonNetQuantityUom = null;
			BigDecimal netQuantity = BigDecimal.ZERO;
			for (OrderItemEntity orderItem : orderItems) {
				LoadedQuantityData ld = loadingData.get(orderItem.getOrderItemId());
				if (ld.getLoadedQuantity() != null) {
					if (commonNetQuantityUom == null) {
						commonNetQuantityUom = referentialDataService.findBsEntity(UomBsEntity.class, 
								ld.getLoadedUom());
					}
					else if (!commonNetQuantityUom.getUomCd().equals(ld.getLoadedUom())) {
						String msg = String.format("Load [%d] is shared by order items with different " +
								"loaded quantity units", load.getLoadId());
						throw new LogonException(msg, Reason.RES_ERROR_UNSPECIFIED);
					}
					orderItem.setLoadedQuantity(ld.getLoadedQuantity());
					orderItem.setLoadedUom(commonNetQuantityUom);
					orderService.updateOrderItemNumeratorAndDenominator(orderItem,
							Constant.ORDER_ITEM_NUM_DENOM_TYPE_LR);
					netQuantity = netQuantity.add(ld.getLoadedQuantity());
				}
			}
			
			if (tareWeightT == null) {
				tareWeightT = load.getTareWeightT();
			}
			
			updateLoad(load, loadingpoint, tareWeightT, grossWeightT, netQuantity, commonNetQuantityUom,
					loadingStartTime, loadingEndTime, isGoodsMovementSendingRequired, source, lock);
	}


	public void setOrdersToLoaded(Collection<OrderEntity> orders, LoadingpointEntity loadingpoint, 
			BigDecimal tareWeightT, BigDecimal grossWeightT, 
			Date loadingStartTime, Date loadingEndTime, 
			EventSource source, ConfSeqLock lock) {

			final Map<LoadEntity, Map<Long, LoadedQuantityData>> loadMap = 
					new HashMap<LoadEntity, Map<Long, LoadedQuantityData>>();
			
			// collect all loads and their loading data
			for (OrderEntity order : orders) {
				List<OrderItemEntity> orderItems = order.getOrderItems();
				final Collection<OrderItemEntity> materialOrderItems = 
						LogonPackageHandlingUtil.getMaterialItems(orderItems);
				for (OrderItemEntity orderItem : materialOrderItems) {
					LoadEntity load = orderItem.getLoad();
					if (!loadMap.containsKey(load)) {
						loadMap.put(load, new HashMap<Long, LoadedQuantityData>());
					}
					final Map<Long, LoadedQuantityData> orderItemMap = loadMap.get(load);
					UomBsEntity orderedUom = orderItem.getOrderedUom();
					BigDecimal orderedQuantity = orderItem.getOrderedQuantity();
					String uomCd = orderedUom.getUomCd();
					Long orderItemId = orderItem.getOrderItemId();
					LoadedQuantityData loadedQuantityData = new LoadedQuantityData(orderedQuantity, uomCd);
					orderItemMap.put(orderItemId, loadedQuantityData);
				}
			}
			
			// call endLoad for all these loads
			for (LoadEntity load : loadMap.keySet()) {
				Map<Long, LoadedQuantityData> loadedQuantityDataMap = loadMap.get(load);
				updateSharedLoadAndSendGoodsMovements(load, loadingpoint, loadedQuantityDataMap, tareWeightT, grossWeightT,
						loadingStartTime, loadingEndTime, source, lock);
			}
	}


	/**
	 * Performs startLoad and endLoad for a load which is to be pre-loaded.
	 * @param load not null
	 * @param loadingpoint not null
	 * @param tareWeightT
	 * @param grossWeightT
	 * @param netQuantity
	 * @param netQuantityUom
	 * @param loadingStartTime
	 * @param loadingEndTime
	 * @param source
	 */
	public void preLoad(LoadEntity load, 
			LoadingpointEntity loadingpoint, 
			BigDecimal tareWeightT, BigDecimal grossWeightT, 
			BigDecimal netQuantity, UomBsEntity netQuantityUom,
			Date loadingStartTime, Date loadingEndTime,
			EventSource source) {

			WagonEntity wagon = load.getWagon();
			ShippingpointEntity sp = loadingpoint.getShippingpoint();
			
			// Start loading
			BizeventEntity startEvent = new BizeventEntity();
			startEvent.setBizeventType(BizEventUtil.BIZEVENT_TYPE_LOADING);
			startEvent.setWagon(wagon);
			startEvent.setEventTs(loadingStartTime);
			startEvent.setShippingpoint(sp);
			
			orderService.startLoad(startEvent, load, loadingpoint, tareWeightT, source);
			
			monitoringTablesService.processBizevent(startEvent);
			
			// End loading
			BizeventEntity endEvent = new BizeventEntity();
			endEvent.setBizeventType(BizEventUtil.BIZEVENT_TYPE_LOADING);
			endEvent.setWagon(wagon);
			endEvent.setEventTs(loadingEndTime);
			endEvent.setShippingpoint(sp);
			
			// TODO 27-Mar-2013/stich: load properties should be part of endLoad, this is tmp only
			load.setNetQuantityUom(netQuantityUom);
			
			orderService.endLoadAndSendGoodsMovements(endEvent, load, loadingpoint, tareWeightT,
					grossWeightT, netQuantity, ACCURATE, VALIDATE_COMPOSITION, source, WITHOUT_LOCK);
			
			monitoringTablesService.processBizevent(endEvent);
	}


	public LoadEntity createAndPersistWagonLoad(WagonEntity wagon, 
			SapMaterialEntity material, UomBsEntity uom, PlantBsEntity prodPlant) {
		LoadEntity load = new LoadEntity();
		
		load.setLoadNumber(fetchSequenceNextVal("SLGNC_LOAD_NUMBER"));
		load.setWagon(wagon);
		load.setChamberNumber(1L);
		load.setSapMaterial(material);
		load.setNetQuantityUom(uom);
		load.setProductionPlant(prodPlant);
		load.setAccurateQuantitiesFlag(Boolean.TRUE);
		load.setWsTareQuitWarnFlag(Boolean.FALSE);
		
		getDaoProvider().getLoadEntityDao().persist(load);
		
		return load;
	}


	@SuppressWarnings("unchecked")
	public List<Long> fetchOrderItemIdsForLoadId(Long loadId) {
		String query = "SELECT " + OrderItemEntity.ATTR_ORDER_ITEM_ID + " " +
		               "FROM " + OrderItemEntity.class.getSimpleName() + " " +
		               "WHERE " + OrderItemEntity.ATTR_LOAD_ID + " = ?1";
		return (List<Long>)getDaoProvider().getDirectJpaDao().find(query, loadId);
	}


	public Set<OrderEntity> findOrdersForLoadId(Long loadId) {
		
		final Set<OrderEntity> orders = new HashSet<OrderEntity>();
		
		final ReadOnlyDao<OrderItemEntity> dao = getDaoProvider().getOrderItemEntityDao();
		final Filter<OrderItemEntity> f = dao.createFilter();
		
		f.addExpression(Restrictions.eq(OrderItemEntity.ATTR_LOAD_ID, loadId));
		final List<OrderItemEntity> orderItems = dao.find(f);
		
		for (OrderItemEntity item : orderItems) {
			orders.add(item.getOrder());
		}
		
		return orders;
	}


	public AssignmentPartEntity findAssignmentPartForLoad(LoadEntity load) {
			final ReadOnlyDao<AssignmentPartEntity> dao = getDaoProvider().getAssignmentPartEntityDao();
			final Filter<AssignmentPartEntity> f = dao.createFilter();
			f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_LOAD_ID, load.getLoadId()));
			return dao.findSingle(f);
	}

	public AssignmentPartEntity findAssignmentPartForLoad(Long loadId) {
			final ReadOnlyDao<AssignmentPartEntity> dao = getDaoProvider().getAssignmentPartEntityDao();
			final Filter<AssignmentPartEntity> f = dao.createFilter();
			f.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_LOAD_ID, loadId));
			return dao.findSingle(f);
	}


	private OrderEntity findNotYetLoadedOrderForLoad(LoadEntity load, Boolean isReverseOrder) {
			final ReadOnlyDao<OrderItemEntity> dao = getDaoProvider().getOrderItemEntityDao();
			final Filter<OrderItemEntity> f = dao.createFilter();
			
			if (!isReverseOrder) {
				Long[] notYetLoadedStatuses = {
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
			
				f.addExpression(Restrictions.in(
						OrderItemEntity.ATTR_ORDER + '.' + OrderEntity.ATTR_ORDER_STATUS_CD, 
						notYetLoadedStatuses));
			}
			else {
				Long[] notYetLoadedStatuses = {
						Constant.ORDER_STATUS_NEW,
						Constant.ORDER_STATUS_TRANSMITTED_TO_POOL,
						Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK,
						Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER,
						Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK,
						Constant.ORDER_STATUS_DISPATCHED,
						Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER,
						Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK,
						Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT,
						Constant.ORDER_STATUS_LOADING,
						Constant.ORDER_STATUS_UNLOADING
				};
			
				f.addExpression(Restrictions.in(
						OrderItemEntity.ATTR_ORDER + '.' + OrderEntity.ATTR_ORDER_STATUS_CD, 
						notYetLoadedStatuses));
			}
			
			f.addExpression(Restrictions.eq(OrderItemEntity.ATTR_LOAD_ID, load.getLoadId()));
			
			final List<OrderItemEntity> orderItems = dao.find(f);
			if (orderItems.isEmpty()) {
				return null;
			}
			else {
				return orderItems.get(0).getOrder();
			}
	}

	private OrderEntity findOrderForLoad(LoadEntity load) {

			final ReadOnlyDao<OrderItemEntity> dao = getDaoProvider().getOrderItemEntityDao();
			final Filter<OrderItemEntity> f = dao.createFilter();
			
			f.addExpression(Restrictions.eq(OrderItemEntity.ATTR_LOAD_ID, load.getLoadId()));
			
			final List<OrderItemEntity> orderItems = dao.find(f);
			if (orderItems.size() == 0) {
				return null;
			}
			else {
				return orderItems.get(0).getOrder();
			}
	}

	public Map<Long, String> findNHMForSapMaterialIds(Set<Long> sapMaterialIds){
			if (sapMaterialIds == null || sapMaterialIds.isEmpty()) {
				return Collections.<Long, String>emptyMap();
			}

			Map<Long, String> nhmMap = new HashMap<Long, String>();

			DaoProvider daoProvider = getDaoProvider();

			ReadOnlyDao<SapMaterialPlantEntity> materialPlantDAO = daoProvider.getSapMaterialPlantEntityDao();

			List<Long> sapMaterialIdList = new ArrayList<Long>(sapMaterialIds);

			Filter<SapMaterialPlantEntity> filter = new FilterImpl<SapMaterialPlantEntity>();
			filter.addExpression(DbUtils.createSplittedInRestrictionOfIds(SapMaterialPlantEntity.ATTR_SAP_MATERIAL_ID, sapMaterialIdList));
			List<SapMaterialPlantEntity> materialPlants = materialPlantDAO.find(filter);

			for (SapMaterialPlantEntity materialPlant : materialPlants) {
				SapMaterialEntity sapMaterial = materialPlant.getSapMaterial();
				nhmMap.put(sapMaterial.getSapMaterialId(), materialPlant.getNhmCode());
			}

			return nhmMap;
	}
		
}
