/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.eventbeans.in.parallel_rail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import com.crh.logon.business.services.utils.BizEventUtil;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.OrderPrioWrapper;
import org.apache.commons.lang.time.DateUtils;

import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.utils.DispoLoadtargetPrioWrapper;
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.business.services.utils.UOMConverter;
import com.crh.logon.constants.Constant;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.parallel_rail.GetWagonLoadOrderRequest;
import com.holcim.logon.eventbeans.in.parallel_rail.GetWagonLoadOrderResponse;
import com.holcim.logon.eventbeans.in.parallel_rail.LoadOrder;
import com.holcim.logon.eventbeans.in.parallel_rail.ParallelRailResponseCode;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.MatPlantEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.DispoLoadtargetEntity;


/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class GetWagonLoadOrderRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(GetWagonLoadOrderRequestHandler.class);
	private static final String PREFIX_ORDER = "order_";
	private static final String PREFIX_MATERIAL = "mat_";
	private static final long DEFAULT_PRIO = 1L;
	
	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;	
	@EJB private DispoServiceLocalInterface dispoService;
	@EJB private OrderServiceLocalInterface orderService;
	
	
	protected  void validate(LogonEventRequest eventBean) {
		
		GetWagonLoadOrderRequest bean = (GetWagonLoadOrderRequest)eventBean;
		validateMandatory(FIELD_WERK_ID, bean.getWerkId());
		validateMandatory(FIELD_VERLADESTRASSE_ID, bean.getVerladestrasseId());
		validateMandatory(FIELD_WAGON, bean.getWagon());
		validateMandatory(FIELD_TARE_WEIGHT, bean.getTareWeightKg());
	}
	
	
	private BigDecimal getMaxLoadKg(WagonEntity wagon, SapMaterialEntity sapMaterial, PlantBsEntity productionPlant) {
		
		BigDecimal maxLoadKg = null; // the max. limited by weight
		BigDecimal maxLoadDensityKg = null; // the max. limited by volume
		
		Filter filter = new FilterImpl();
		// Get max by volume * density
		filter.addExpression(Restrictions.eq(MatPlantEntity.ATTR_SAP_MATERIAL, sapMaterial));
		filter.addExpression(Restrictions.eq(MatPlantEntity.ATTR_PLANT, productionPlant));
		MatPlantEntity matPlant = getDaoProvider().getMatPlantEntityDao().findSingle(filter);
		if ( (matPlant != null) && (matPlant.getBulkDensityKgpm3() != null) && (wagon.getVolumeM3() != null) ) {
			maxLoadDensityKg = matPlant.getBulkDensityKgpm3().multiply(new BigDecimal("" + wagon.getVolumeM3()));
		}
		// Get max by tons
		BigDecimal maxGrossWeightT = wagon.getMaxGrossWeightT();
		if (maxGrossWeightT == null) {
			maxGrossWeightT = wagon.getWagonType().getMaxGrossWeightT();
		}
		if (maxGrossWeightT != null) {
			maxLoadKg = maxGrossWeightT.subtract(wagon.getTareWeightT());
			maxLoadKg = maxLoadKg.multiply(Constant.TON_TO_KG);
		}
		
		if (maxLoadKg == null) {
			
			return maxLoadDensityKg;
		}
		else if (maxLoadDensityKg == null) {
			
			return maxLoadKg;
		}
		if (maxLoadKg.compareTo(maxLoadDensityKg) < 0) {
				
			return maxLoadKg;
		}

		return maxLoadDensityKg;
	}
	
	
	private BigDecimal getMaxLoadKg(ContainerEntity container, SapMaterialEntity sapMaterial, PlantBsEntity productionPlant) {
		
		BigDecimal maxLoadKg = null; // the max. limited by weight
		BigDecimal maxLoadDensityKg = null; // the max. limited by volume
		
		Filter filter = new FilterImpl();
		// Get max by volume * density
		filter.addExpression(Restrictions.eq(MatPlantEntity.ATTR_SAP_MATERIAL, sapMaterial));
		filter.addExpression(Restrictions.eq(MatPlantEntity.ATTR_PLANT, productionPlant));
		MatPlantEntity matPlant = getDaoProvider().getMatPlantEntityDao().findSingle(filter);
		if ( (matPlant != null) && (matPlant.getBulkDensityKgpm3() != null) && (container.getVolumeM3() != null) ) {
			maxLoadDensityKg = matPlant.getBulkDensityKgpm3().multiply(new BigDecimal("" + container.getVolumeM3()));
			LOG.debug(container.getContainerName() + " max load by density " + maxLoadDensityKg + " kg");
		}
		// Get max by tons
		BigDecimal maxNetWeightT = container.getMaxNetWeightT();
		if (maxNetWeightT != null) {
			maxLoadKg = maxNetWeightT.multiply(Constant.TON_TO_KG);
			LOG.debug(container.getContainerName() + " max load by net weight " + maxLoadKg + " kg");
		}
		
		if (maxLoadKg == null) {
			
			return maxLoadDensityKg;
		}
		else if (maxLoadDensityKg == null) {
			
			return maxLoadKg;
		}
		if (maxLoadKg.compareTo(maxLoadDensityKg) < 0) {
				
			return maxLoadKg;
		}
		
		return maxLoadDensityKg;
	}
	
	
	private LoadOrder createLoadOrder(GetWagonLoadOrderRequest bean, GetWagonLoadOrderResponse wagonLoadOrderResponse, LoadEntity load, DispoLoadtargetEntity loadTarget, BigDecimal amountT, String plantCd, List containerList, ContainerEntity container) {
		
		LoadOrder loadOrder = new LoadOrder();
		if (load.getActualChromatReductionMonth() != null) {
			loadOrder.setChromReduce(load.getActualChromatReductionMonth());
		}
		else {
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(MatPlantEntity.ATTR_PLANT_CD, plantCd));
			filter.addExpression(Restrictions.eq(MatPlantEntity.ATTR_SAP_MATERIAL, load.getSapMaterial()));
			List matPlantList = getDaoProvider().getMatPlantEntityDao().find(filter);
			if ( (matPlantList != null) && (matPlantList.size() == 1) ) {
				Long chromRed = ((MatPlantEntity)matPlantList.get(0)).getChromatReductionMonths();
				if (chromRed != null) {
					loadOrder.setChromReduce(chromRed);
				}
			}
		}
		loadOrder.setVerladeauftragID(load.getLoadOrderId());
		loadOrder.setMaterial(load.getSapMaterial().getSapMaterialIfcNo());
		
		// Get the amount to load
		BigDecimal netAmountKg = null;
		WagonEntity wagon = getWagon(bean.getWagon());
		BigDecimal tareWeightT = getKgAsTons(bean.getTareWeightKg());
		
		BigDecimal leftOverQuantityT = tareWeightT.subtract(wagon.getTareWeightT());
		for (Iterator it=containerList.iterator();it.hasNext();) {
			ContainerEntity otherContainer = (ContainerEntity)it.next();
			leftOverQuantityT = leftOverQuantityT.subtract(otherContainer.getTareWeightT());
		}
		BigDecimal limitT = wagon.getWagonType().getLimitCreditNoteT();
		if (limitT == null) {
			limitT = new BigDecimal(getConfigBean().getAppDefaultWeighingWagonLimitCreditNoteT());
		}
		
		BigDecimal maxLoadKg = null;
		if (container != null) {
			if (containerList.size() > 1) {
				if (leftOverQuantityT.compareTo(limitT) > 0) {
					// In case of > 1 container, it's not known which container has left over; warning
					wagonLoadOrderResponse.setCode(ParallelRailResponseCode.OK_WITH_REMARK.value());
					wagonLoadOrderResponse.setContextValue(Constant.PARALLEL_REMARK_LEVEL_WARNING);
					wagonLoadOrderResponse.setTxt(Message.lookup(getLocale(), "parallel.warning.container_overload", leftOverQuantityT.toString()));
				}
			}
			maxLoadKg = getMaxLoadKg(container, load.getSapMaterial(), load.getProductionPlant());
		}
		else {
			maxLoadKg = getMaxLoadKg(wagon, load.getSapMaterial(), load.getProductionPlant());
		}
		if (containerList.size() <= 1) {
			// quantity needs to be corrected
			maxLoadKg = maxLoadKg.subtract(leftOverQuantityT.multiply(Constant.TON_TO_KG));
		}
		
		if (amountT != null) {
			// order quantity is not fuzzy
			netAmountKg = amountT.multiply(Constant.TON_TO_KG);
			if ( (containerList.size() <= 1) && (leftOverQuantityT.compareTo(limitT) > 0) ) {
				// ordered quantity needs to be adjusted if there's more left over than the credit note limit
				netAmountKg = netAmountKg.subtract(leftOverQuantityT.multiply(Constant.TON_TO_KG));
			}
			if ( (maxLoadKg != null) && (maxLoadKg.compareTo(netAmountKg) < 0) ) {
				// amount restricted by max capacity
				netAmountKg = maxLoadKg;
			}
		}
		else {
			// no amount specified; full load
			netAmountKg = maxLoadKg;
		}
				
		if (netAmountKg != null) {
			loadOrder.setNetWeightKg(netAmountKg);
		}
		
		OrderEntity order = null;
		Collection orders = orderService.getOrders(load);
		if (!orders.isEmpty()) {
			if (orders.size() == 1) {
				order = (OrderEntity)orders.iterator().next();
			}
			else {
				
				throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.APPLICATION_ERROR, "multiple orders found for load"));
			}
		}
		if (order != null) {
			if (order.getShiptoSapBp() != null) {
				loadOrder.setCustomer(order.getShiptoSapBp().getSapIfcId());
			}
			loadOrder.setShipTo(order.getShiptoSapBp().getSapIfcId());
			loadOrder.setLogonOrderID(order.getOrderNumber());
			loadOrder.setOrderComment(order.getNoteRailwayDisp());
		}
		if (container != null) {
			loadOrder.getContainerLabelList().add(container.getContainerName());
		}
		loadOrder.setContainerCompound(getContainerCompound(load, loadTarget));
				
		return loadOrder;
	}
	
	
	private String getContainerCompound(LoadEntity load, DispoLoadtargetEntity loadTarget) {
		
		String containerCompound = "";
		OrderEntity order = null;
		if (load != null) {
			Collection orders = orderService.getOrders(load);
			if (!orders.isEmpty()) {
				if (orders.size() == 1) {
					order = (OrderEntity)orders.iterator().next();
				}
				else {
					
					throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.APPLICATION_ERROR, "multiple orders found for load"));
				}
			}
			if (order != null) {
				
				return PREFIX_ORDER + order.getOrderNumber();
			}
			containerCompound = PREFIX_MATERIAL + load.getSapMaterial().getSapMaterialIfcNo();
		}
		if (loadTarget != null) {
			
			containerCompound += "_" + loadTarget.getShippingpointLoadtargetId();
		}
		
		return containerCompound;
	}
	
	
	private ContainerEntity getContainer(LoadEntity load, DispoLoadtargetEntity loadTarget, List allContainers, Map usedContainers) {
		
		ContainerEntity result = null;
		if (allContainers.isEmpty()) {
			
			return null;
		}
		if (load.getContainer() != null) {
			
			return load.getContainer();
		}
		String containerCompound = getContainerCompound(load, loadTarget);
		List usedContainerList = (List)usedContainers.get(containerCompound);
		if (usedContainerList == null) {
			usedContainerList = new ArrayList();
			usedContainers.put(containerCompound, usedContainerList);
		}
		for (Iterator it=allContainers.iterator();it.hasNext();) {
			ContainerEntity container = (ContainerEntity)it.next();
			if (!usedContainerList.contains(container)) {
				usedContainerList.add(container);
				result = container;
				break;
			}
		}
		
		return result;
	}
	
	
	private boolean isAvailableMaterial(List materialRestrictions, SapMaterialEntity sapMaterial) {
		
		if ( (materialRestrictions == null) || (materialRestrictions.isEmpty()) || (sapMaterial == null) ) {
			
			return false;
		}
		if (materialRestrictions.contains(sapMaterial.getSapMaterialIfcNo())) {
			
			return true;
		}
		if (materialRestrictions.size() == 1) {
			String singleMatRest = (String)materialRestrictions.get(0);
			
			if (singleMatRest.startsWith("regex:")) {
				// For testing
				return sapMaterial.getSapMaterialIfcNo().matches(singleMatRest.substring(6));
			}
		}
		
		return false;
	}
	
	
	private void finishOrder(AssignmentPartEntity finishedAp, OrderEntity order) {
		
		BizeventEntity bizevent = new BizeventEntity();
		bizevent.setBizeventType(BizEventUtil.BIZEVENT_TYPE_DELIVERED);
		bizevent.setAssignment(finishedAp.getAssignment());
		bizevent.setShippingpoint(finishedAp.getShippingpoint());
		bizevent.setShipto(finishedAp.getShipto());
		bizevent.setCoordLat(finishedAp.getCoordLat());
		bizevent.setCoordLon(finishedAp.getCoordLon());
		bizevent.setRailwayStation(finishedAp.getShipto().getShiptoSapBp().getRailwayStation());
		bizevent.setOrder(order);
		bizevent.setEventTs(new Date());
		bizevent.setWagon(finishedAp.getAssignment().getWagon());
		// This bizevent should not be persisted here (see OrderServiceBean._setUnloadedImpl)
		// 
		// bizevent = getMonitoringTablesService().processBizevent(bizevent);
		orderService.setUnloaded(bizevent, null, false, false, true);
	}
	
	
	protected  LogonEventResponse perform(LogonEventRequest eventBean) {

		GetWagonLoadOrderRequest bean = (GetWagonLoadOrderRequest)eventBean;
		List loadOrderList = new ArrayList();
		GetWagonLoadOrderResponse wagonLoadOrderResponse = (GetWagonLoadOrderResponse)createLogonEventResponse(GetWagonLoadOrderResponse.class, ParallelRailResponseCode.OK);
		
		WagonEntity wagon = getWagon(bean.getWagon());
		LoadingpointEntity lp = getLoadingpoint(bean.getWerkId(), bean.getVerladestrasseId());
		if (lp == null) {
			
			throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.MANDATORY_FIELD_ERROR, FIELD_VERLADESTRASSE_ID), bean.getVerladestrasseId());
		}
		BigDecimal tareWeightT = getKgAsTons(bean.getTareWeightKg());
		
		// Update container administration
		List containerList = new ArrayList();
		// Clear containers as currently registered for this wagon
		Filter filter = new FilterImpl();
		filter.addExpression(Restrictions.eq(ContainerEntity.ATTR_CURRENT_WAGON, wagon));
		List prevContainerList = getDaoProvider().getContainerEntityDao().find(filter);
		if (!prevContainerList.isEmpty()) {
			for (Iterator it=prevContainerList.iterator();it.hasNext();) {
				ContainerEntity container = (ContainerEntity)it.next();
				if (!bean.getContainerLabelList().contains(container.getContainerName())) {
					container.setCurrentWagon(null);
				}
			}
		}
		if ( (bean.getContainerLabelList() != null) && (!bean.getContainerLabelList().isEmpty()) ) {
			// Register the newly provided list of containers to this wagon
			for (Iterator it=bean.getContainerLabelList().iterator();it.hasNext();) {
				String containerLabel = (String)it.next();
				ContainerEntity container = getContainer(containerLabel);
				if (container == null) {
					
					throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.APPLICATION_ERROR, Message.lookup(getLocale(), "parallel.error.unknown_container", containerLabel)));
				}
				if ( (container.getCurrentWagon() == null) || (!container.getCurrentWagon().getWagonId().equals(wagon.getWagonId())) ) {
					// Container was assigned to another wagon, check for not-finished assignments
//					filter = new FilterImpl();
//					filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_LOAD + "." + LoadEntity.ATTR_CONTAINER, container));
//					filter.addExpression(Restrictions.isNotNull(AssignmentPartEntity.ATTR_LOAD + "." + LoadEntity.ATTR_LOADING_TS));
//					filter.addExpression(Restrictions.isNull(AssignmentPartEntity.ATTR_LOAD + "." + LoadEntity.ATTR_UNLOADING_TS));
//					List assignmentPartList = getDaoProvider().getAssignmentPartEntityDao().find(filter);
					// java.lang.NullPointerException at org.hibernate.hql.ast.tree.FromClause.findIntendedAliasedFromElementBasedOnCrazyJPARequirements(FromClause.java:120)
					// boon 20100121: Does work when aliases are used everywhere. Don't think it's a good idea to make changes to the DAO layer now so I decided to use direct JPA here (there's no authorization restrictions here). 
					List assignmentPartList = getDaoProvider().getDirectJpaDao().find(
							"SELECT a FROM " + AssignmentPartEntity.class.getSimpleName() + " a " +
							"WHERE a." + AssignmentPartEntity.ATTR_LOAD + "." + LoadEntity.ATTR_CONTAINER + " = ?1" + 
							" AND a." + AssignmentPartEntity.ATTR_LOAD + "." + LoadEntity.ATTR_LOADING_TS + " IS NOT NULL" +
							" AND a." + AssignmentPartEntity.ATTR_LOAD + "." + LoadEntity.ATTR_UNLOADING_TS + " IS NULL", 
							container);
					if (!assignmentPartList.isEmpty()) {
						for (Iterator it2=assignmentPartList.iterator();it2.hasNext();) {
							AssignmentPartEntity loadAp = (AssignmentPartEntity)it2.next();
							OrderEntity order = null;
							AssignmentPartEntity finishedAp = null;
							List loadAps = new ArrayList();
							for (Iterator it3=loadAp.getAssignment().getAssignmentParts().iterator();it3.hasNext();) {
								AssignmentPartEntity ap = (AssignmentPartEntity)it3.next();
								if (ap.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_LOAD) {
									loadAps.add(ap);
								}
								else if ( (ap.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_UNLOAD) && (ap.getOrder() != null) ) {
									order = ap.getOrder();
								}
								else if (ap.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_FINISHED) {
									finishedAp = ap;
								}
							}
							if ( (order != null) && (order.getOrderStatus().getOrderStatusCd().longValue() > Constant.ORDER_STATUS_LOADED) ) {
								if ( (finishedAp != null) && (finishedAp.getPlannedStart().compareTo(new Date()) < 0) ) {
									// The load for this contaienr is definately unloaded
									loadAp.getLoad().setUnloadingTs(new Date());
									// Check if the order should be closed based on the other order items' loads
									boolean orderInProgress = false;
									List orderItems = (List) LogonPackageHandlingUtil.getMaterialItems(order);
									for (Iterator it4=orderItems.iterator();it4.hasNext();) {
										OrderItemEntity orderItem = (OrderItemEntity)it4.next();
										if (orderItem.getLoad().getUnloadingTs() == null) {
											orderInProgress = true;
										}
									}
									if (!orderInProgress) {
										finishOrder(finishedAp, order);
									}
								}	
								else {
									
									throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.APPLICATION_ERROR, Message.lookup(getLocale(), "parallel.error.container_assigned_to_order", container.getContainerName())));
								}
							}
						}
					}
				}
				container.setCurrentTrailer(null);
				container.setCurrentTruck(null);
				container.setCurrentWagon(wagon);
				containerList.add(container);
			}
		}
		// Flush so the new container config is known from now on
		em.flush();
		
		// If there are lingering (pre-)loads from a previous call, remove them
		filter = new FilterImpl();
		filter.addExpression(Restrictions.eq(LoadEntity.ATTR_WAGON, wagon));
		filter.addExpression(Restrictions.isNull(LoadEntity.ATTR_LOADING_TS));
		filter.addExpression(Restrictions.isNull(LoadEntity.ATTR_UNLOADING_TS));
		filter.addExpression(Restrictions.isNotNull(LoadEntity.ATTR_LOAD_ORDER_ID));
		List loadList = getDaoProvider().getLoadEntityDao().find(filter);
		for (Iterator it=loadList.iterator();it.hasNext();) {
			LoadEntity tempLoad = (LoadEntity)it.next();
			filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(OrderItemEntity.ATTR_LOAD, tempLoad));
			if (getDaoProvider().getOrderItemEntityDao().find(filter).isEmpty()) {
				getDaoProvider().getLoadEntityDao().remove(tempLoad);
			}
		}
		
		// See if the wagon is already loaded
		filter = new FilterImpl();
		filter.addExpression(Restrictions.eq(LoadEntity.ATTR_WAGON, wagon));
		if (!containerList.isEmpty()) {
			filter.addExpression(Restrictions.in(LoadEntity.ATTR_CONTAINER, containerList));
		}
		else {
			filter.addExpression(Restrictions.isNull(LoadEntity.ATTR_CONTAINER));
		}
		filter.addExpression(Restrictions.isNotNull(LoadEntity.ATTR_LOADING_TS));
		filter.addExpression(Restrictions.isNull(LoadEntity.ATTR_UNLOADING_TS));
		filter.addExpression(Restrictions.isNotNull(LoadEntity.ATTR_NET_QUANTITY));
		if (!getDaoProvider().getLoadEntityDao().find(filter).isEmpty()) {
			
			throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.APPLICATION_ERROR, Message.lookup(getLocale(), "parallel.error.wagon_loaded")));
		}
		
		Map usedContainers = new HashMap(); // This is to keep track of which containers where already assigned to a load order as part of a container compound
		// Get order being loaded at this terminal (in case the same order is requested twice)
		filter = new FilterImpl();
		if (!containerList.isEmpty()) {
			filter.addExpression(Restrictions.or(Restrictions.eq(LoadEntity.ATTR_WAGON, wagon), Restrictions.in(LoadEntity.ATTR_CONTAINER, containerList)));	
		}
		else {
			filter.addExpression(Restrictions.eq(LoadEntity.ATTR_WAGON, wagon));
		}
		filter.addExpression(Restrictions.isNotNull(LoadEntity.ATTR_LOADING_TS));
		filter.addExpression(Restrictions.isNull(LoadEntity.ATTR_UNLOADING_TS));
		filter.addExpression(Restrictions.isNull(LoadEntity.ATTR_NET_QUANTITY));
		loadList = getDaoProvider().getLoadEntityDao().find(filter);
		if (!loadList.isEmpty()) {
			LOG.info(loadList.size() + " loads in state 'loading' were found for wagon " + wagon.getWagonNo());
			for (Iterator it=loadList.iterator();it.hasNext();) {
				LoadEntity load = (LoadEntity)it.next();
				LOG.debug("Checking load " + load.getLoadId());
				filter = new FilterImpl();
				filter.addExpression(Restrictions.eq(OrderItemEntity.ATTR_LOAD, load));
				OrderItemEntity orderItem = getDaoProvider().getOrderItemEntityDao().findSingle(filter);
				if (orderItem != null) {
					OrderEntity order = orderItem.getOrder();
					LOG.debug("Checking order " + order.getOrderId());
					if ( ((order.getOrderStatus().getOrderStatusCd().longValue() > Constant.ORDER_STATUS_LOADED) && (order.getOrderStatus().getOrderStatusCd().longValue() != Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL)) 
							|| (!order.getShippingpoint().getShippingpointId().equals(lp.getShippingpoint().getShippingpointId())) ) {
						
						throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.APPLICATION_ERROR, "Invalid load for order " + orderItem.getOrder().getOrderNumber() + " found"));
					}
					BigDecimal amount = UOMConverter.getUomConvertedValue(orderItem.getOrderedUom(), orderItem.getOrderedQuantity(), false);
					if (load.getLoadOrderId() == null) {
						load.setLoadOrderId(fetchSequenceNextVal("SLGNC_LOAD_ORDER"));
					}
					ContainerEntity container = getContainer(load, null, containerList, usedContainers);
					LoadOrder loadOrder = createLoadOrder(bean, wagonLoadOrderResponse, load, null, amount, orderItem.getProductionPlant().getPlantCd(), containerList, container);
					if (!isAvailableMaterial(bean.getMaterialRestrictions(), load.getSapMaterial())) {
						
						throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.APPLICATION_ERROR, Message.lookup(getLocale(), "parallel.error.material_mismatch")));
					}
					if (loadOrder != null) {
						loadOrder.setPriority(DEFAULT_PRIO);
						loadOrderList.add(loadOrder);
					}
				}
				else {
					
					throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.APPLICATION_ERROR, "A pre-load is in progress with verladeAuftragID=" + load.getLoadOrderId()));
				}
			}
			if (!loadOrderList.isEmpty()) {
				LOG.debug("Found " + loadOrderList.size() + " load orders based on loads in progress");
			}
		}
		
		if (loadOrderList.isEmpty()) {
			// Get manually dispatched order
			filter = new FilterImpl();
			if (!containerList.isEmpty()) {
				filter.addExpression(Restrictions.or(Restrictions.eq(LoadEntity.ATTR_WAGON, wagon), Restrictions.in(LoadEntity.ATTR_CONTAINER, containerList)));	
			}
			else {
				filter.addExpression(Restrictions.eq(LoadEntity.ATTR_WAGON, wagon));
			}
			filter.addExpression(Restrictions.isNull(LoadEntity.ATTR_LOADING_TS));
			filter.addExpression(Restrictions.isNull(LoadEntity.ATTR_UNLOADING_TS));
			loadList = getDaoProvider().getLoadEntityDao().find(filter);
			LOG.debug("Found " + loadList.size() + " empty loads for wagon");
			if (!loadList.isEmpty()) {
				for (Iterator it=loadList.iterator();it.hasNext();) {
					LoadEntity load = (LoadEntity)it.next();
					LOG.debug("Checking load " + load.getLoadId());
					filter = new FilterImpl();
					filter.addExpression(Restrictions.eq(OrderItemEntity.ATTR_LOAD, load));
					OrderItemEntity orderItem = getDaoProvider().getOrderItemEntityDao().findSingle(filter);
					if (orderItem != null) {
						if ( (containerList.isEmpty()) && (!dispoService.checkWagonAssignmentMat(wagon.getWagonId(), getLocale(), tareWeightT.doubleValue()))  ) {
							
							throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.APPLICATION_ERROR, "Wagon cannot be loaded with the material of the dispatched order"));
						}
						OrderEntity order = orderItem.getOrder();
						LOG.debug("Checking order " + order.getOrderId());
						if ( ((order.getOrderStatus().getOrderStatusCd().longValue() > Constant.ORDER_STATUS_LOADED) && (order.getOrderStatus().getOrderStatusCd().longValue() != Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL)) 
								|| (!order.getShippingpoint().getShippingpointId().equals(lp.getShippingpoint().getShippingpointId())) ) {
							
							throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.APPLICATION_ERROR, "Invalid load for order " + orderItem.getOrder().getOrderNumber() + " found"));
						}
						BigDecimal amount = UOMConverter.getUomConvertedValue(orderItem.getOrderedUom(), orderItem.getOrderedQuantity(), false);
						if (load.getLoadOrderId() == null) {
							load.setLoadOrderId(fetchSequenceNextVal("SLGNC_LOAD_ORDER"));
						}
						
						ContainerEntity container = getContainer(load, null, containerList, usedContainers);
						LoadOrder loadOrder = createLoadOrder(bean, wagonLoadOrderResponse, load, null, amount, orderItem.getProductionPlant().getPlantCd(), containerList, container);
						if (!isAvailableMaterial(bean.getMaterialRestrictions(), load.getSapMaterial())) {
							
							throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.APPLICATION_ERROR, Message.lookup(getLocale(), "parallel.error.material_mismatch")));
						}
						if (loadOrder != null) {
							loadOrder.setPriority(DEFAULT_PRIO);
							loadOrderList.add(loadOrder);
						}
					}
					else {
						LOG.debug("Load has no order");
					}
				}
				if (!loadOrderList.isEmpty()) {
					LOG.debug("Found " + loadOrderList.size() + " load orders based on existing orders for wagon");
				}
			}
		}
		
		if (loadOrderList.isEmpty()) {
			long maxPrio = 0L;
			// Check for existing orders
			Collection possibleOrders = dispoService.fetchPossibleOrdersToLoad(wagon.getWagonId(), lp.getShippingpoint().getPlant().getPlantCd(), DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH), getLocale(), tareWeightT.doubleValue());
			if (possibleOrders != null) {
				for (Iterator it=possibleOrders.iterator();it.hasNext();) {
					OrderPrioWrapper orderPrioWrapper = (OrderPrioWrapper)it.next();
					Long orderId = orderPrioWrapper.getOrder().getLogonOrder().getOrderId();
					if (orderId != null) {
						OrderEntity order = getDaoProvider().getOrderEntityDao().findById(orderId.longValue());
						LOG.debug("Checking possible order to load " + order.getOrderId());
						List orderItems = (List)LogonPackageHandlingUtil.getMaterialItems(order);
						for (Iterator it2=orderItems.iterator();it2.hasNext();) {
							OrderItemEntity orderItem = (OrderItemEntity)it2.next();
							if (!isAvailableMaterial(bean.getMaterialRestrictions(), orderItem.getSapMaterial())) {
								LOG.debug("Material mismatch with possible order");
								continue;
							}
							LoadEntity load = orderItem.getLoad();
							if (load == null) {
								load = new LoadEntity();
								load.setChamberNumber(Long.valueOf(1));
								// if the ordered UoM is an alternative UoM then the UoM dimension could be null
								// load.setNetQuantityUom(referentialDataService.findBsEntity(UomBsEntity.class,  
								//		orderItem.getOrderedUom().getUomDimension().getUomDimensionCd()));
								
								// use the SAP material base UoMs dimension for UomBsEntity lookup 
								UomBsEntity sapMaterialBaseUom = orderItem.getSapMaterial().getBaseUom();
								if (sapMaterialBaseUom.getUomDimension() != null) {
									load.setNetQuantityUom(referentialDataService.findBsEntity(UomBsEntity.class,  
											sapMaterialBaseUom.getUomDimension().getUomDimensionCd()));
								}
								else {
									throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.APPLICATION_ERROR, Message.lookup(getLocale(), "parallel.error.orderitem", orderItem.getOrder().getOrderNumber(), orderItem.getOrderItemNo())));
								}
								
								load.setProductionPlant(orderItem.getProductionPlant());
								load.setSapMaterial(orderItem.getSapMaterial());
								load.setAccurateQuantitiesFlag(Boolean.TRUE);
								load.setWsTareQuitWarnFlag(Boolean.FALSE);
								load.setLoadOrderId(fetchSequenceNextVal("SLGNC_LOAD_ORDER"));
								load.setLoadNumber(fetchSequenceNextVal("SLGNC_LOAD_NUMBER"));
								getDaoProvider().getLoadEntityDao().persist(load);
								orderItem.setLoad(load);
							}
							if (load.getLoadOrderId() == null) {
								load.setLoadOrderId(fetchSequenceNextVal("SLGNC_LOAD_ORDER"));
							}
							BigDecimal amount = UOMConverter.getUomConvertedValue(orderItem.getOrderedUom(), orderItem.getOrderedQuantity(), false);
							ContainerEntity container = getContainer(load, null, containerList, usedContainers);
							LoadOrder loadOrder = createLoadOrder(bean, wagonLoadOrderResponse, load, null, amount, orderItem.getProductionPlant().getPlantCd(), containerList, container);
							if (loadOrder != null) {
								long prio = DEFAULT_PRIO;
								if (orderPrioWrapper.getPrio() != null) {
									prio = orderPrioWrapper.getPrio().longValue();
								}
								if (prio > maxPrio) {
									maxPrio = prio;
								}
								loadOrder.setPriority(prio);
								loadOrderList.add(loadOrder);
							}
						}
					}
				}
			}
			int possibleOrderCount = loadOrderList.size();
			if (!loadOrderList.isEmpty()) {
				LOG.debug("Found " + loadOrderList.size() + " load orders based on existing possible orders");
			}
			maxPrio += 1;
		
			// Get a pre-load order
			Collection preLoads = dispoService.fetchPossiblePreloads(wagon.getWagonId(), lp.getShippingpoint().getPlant().getPlantCd(), getLocale(), DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH), tareWeightT.doubleValue());
			if (preLoads != null) {
				for (Iterator it=preLoads.iterator();it.hasNext();) {
					DispoLoadtargetPrioWrapper loadTargetPrio = (DispoLoadtargetPrioWrapper)it.next();
					DispoLoadtargetEntity loadTarget = loadTargetPrio.getTarget();
					SapMaterialEntity sapMaterial = getDaoProvider().getSapMaterialEntityDao().findById(loadTarget.getSapMaterialId());
					if (!isAvailableMaterial(bean.getMaterialRestrictions(), sapMaterial)) {
						LOG.debug("Material mismatch with pre-load");
						continue;
					}
					LoadEntity load = new LoadEntity();
					load.setChamberNumber(Long.valueOf(1));
					load.setNetQuantityUom(referentialDataService.findBsEntity(UomBsEntity.class, Constant.UOM_TONS));
					load.setProductionPlant(referentialDataService.findBsEntity(PlantBsEntity.class, loadTarget.getProductionPlantCd()));
					load.setSapMaterial(sapMaterial);
					load.setWagon(wagon);
					load.setAccurateQuantitiesFlag(Boolean.TRUE);
					load.setWsTareQuitWarnFlag(Boolean.FALSE);
					load.setLoadOrderId(fetchSequenceNextVal("SLGNC_LOAD_ORDER"));
					load.setLoadNumber(fetchSequenceNextVal("SLGNC_LOAD_NUMBER"));
					load.setLdsOfflineMode(Boolean.FALSE);
					getDaoProvider().getLoadEntityDao().persist(load);
					BigDecimal amount = UOMConverter.getUomConvertedValue(referentialDataService.findBsEntity(UomBsEntity.class, loadTarget.getLoadUomCd()), loadTarget.getLoadQuantity(), false);
					ContainerEntity container = getContainer(load, loadTarget, containerList, usedContainers);
					load.setContainer(container);
					LoadOrder loadOrder = createLoadOrder(bean, wagonLoadOrderResponse, load, loadTarget, amount, loadTarget.getPlantCd(), containerList, container);
					if (loadOrder != null) {
						if (loadOrder.getOrderComment() != null) {
							loadOrder.setOrderComment(loadOrder.getOrderComment() + " - " + loadTarget.getPreloadComment());
						}
						else {
							loadOrder.setOrderComment(loadTarget.getPreloadComment());
						}
						loadOrder.setPriority(maxPrio);
						loadOrderList.add(loadOrder);
						maxPrio += 1;
					}
				}
			}
			if ( (!loadOrderList.isEmpty()) && (loadOrderList.size() > possibleOrderCount) ) {
				LOG.debug("Found " + (loadOrderList.size()-possibleOrderCount) + " load orders based on pre-loads");
			}
		}
		
		// Set response
		wagonLoadOrderResponse.getLoadOrderList().addAll(loadOrderList);
		
		return wagonLoadOrderResponse;
	}
}
