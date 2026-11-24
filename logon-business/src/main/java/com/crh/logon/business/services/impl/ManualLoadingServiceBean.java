/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.ManualLoadingServiceDispatcher;
import com.crh.logon.business.services.localinterfaces.ConfigurableSequencesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.MonitoringTablesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TruckAdminServiceLocalInterface;
import com.crh.logon.business.services.utils.BizEventUtil;
import com.crh.logon.business.services.utils.ConfSeqLock;
import com.crh.logon.beans.LoadedQuantityData;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.LoadingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.ManualLoadingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonStatusUtils;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.constants.EventSource;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.utils.LogonException;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class ManualLoadingServiceBean extends ManualLoadingServiceDispatcher implements ManualLoadingServiceLocalInterface {
	
	private static final boolean VALIDATE_COMPOSITION = true;

	private static final boolean ACCURATE = true;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ManualLoadingServiceBean.class);

	private static final BigDecimal NO_GROSS_WEIGHT = null;

	private static final BigDecimal NO_TARE_WEIGHT = null;
	
	@EJB private ConfigurableSequencesServiceLocalInterface sequenceService;
	@EJB private ManualLoadingServiceLocalInterface manualLoadingService;
	@EJB private LoadingServiceLocalInterface loadingService;
	@EJB private OrderServiceLocalInterface orderService;
	@EJB private MonitoringTablesServiceLocalInterface monitoringTablesService;
	@EJB private TruckAdminServiceLocalInterface truckAdminService;
	

	public void updateBulkLoad(Long loadId, Long loadingpointId,
			BigDecimal tareWeightT, BigDecimal netQuantity, UomBsEntity netQuantityUom, Date loadingStartTime,
			Date loadingEndTime) {
		
		final Set<OrderEntity> orders = loadingService.findOrdersForLoadId(loadId);
		
		ConfSeqLock lock = null;
		try {
			lock = sequenceService.acquireLockForOrders(orders);
			manualLoadingService._updateBulkLoad(loadId, loadingpointId, tareWeightT, netQuantity, netQuantityUom,
					loadingStartTime, loadingEndTime, lock);
		}
		finally {
			try {
				sequenceService.releaseLock(lock);
			}
			catch (LogonException e) {
				LOG.error(e.getMessage());
			}
		}
		
		em.clear();
		final Set<OrderEntity> updatedOrders = loadingService.findOrdersForLoadId(loadId);
		for (OrderEntity order : updatedOrders) {
			if (LogonStatusUtils.isLoadedOrBeyond(order.getOrderStatus())) {
				if (LogonUtil.isPickupOrder(order.getDeliveryType().getDeliveryTypeCd())) {
					// In pickup case, the order is set to delivered right away (non-PDA), so this DN is the final DN.
					orderService.generateAndSendPDFWithoutExplicitLoadingpoint(order, true, true, true);
				}
				else {
					orderService.generateAndSendPDFWithoutExplicitLoadingpoint(order, false, false, true);
				}
			}
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void _updateBulkLoad(Long loadId, Long loadingpointId,
			BigDecimal tareWeightT, BigDecimal netQuantity,
			UomBsEntity netQuantityUom, Date loadingStartTime, Date loadingEndTime, ConfSeqLock lock) {
		
		final LoadEntity load = getDaoProvider().getLoadEntityDao().findById(loadId);
		final LoadingpointEntity loadingpoint;
		if (loadingpointId != null) {
			loadingpoint = getDaoProvider().getLoadingpointEntityDao().findById(loadingpointId);
		}
		else {
			loadingpoint = null;
		}
		
		// LOGON-6715 We should get the UoM from the form submit. If it is null, however,
		// we fall back to the load UoM (case bulk, non-MTQ)
		if(netQuantityUom == null) {
			netQuantityUom = load.getNetQuantityUom();
		}		
		
		loadingService.updateLoadAndSendGoodsMovements(load, loadingpoint, 
				tareWeightT, NO_GROSS_WEIGHT, netQuantity, netQuantityUom, loadingStartTime,
				loadingEndTime, EventSource.LOGON, lock);
	}
	
	public void updateBagLoad(Long loadId, Long loadingpointId,
			Map<Long, LoadedQuantityData> loadingData, Date loadingStartTime, Date loadingEndTime) {
		
		final Set<OrderEntity> orders = loadingService.findOrdersForLoadId(loadId);
		
		ConfSeqLock lock = null;
		try {
			lock = sequenceService.acquireLockForOrders(orders);
			manualLoadingService._updateBagLoad(loadId, loadingpointId, loadingData, loadingStartTime, loadingEndTime, lock);
		}
		finally {
			try {
				sequenceService.releaseLock(lock);
			}
			catch (LogonException e) {
				LOG.error(e.getMessage());
			}
		}
		
		em.clear();
		final Set<OrderEntity> updatedOrders = loadingService.findOrdersForLoadId(loadId);
		for (OrderEntity order : updatedOrders) {
			if (LogonStatusUtils.isLoadedOrBeyond(order.getOrderStatus())){
				DeliveryTypeBsEntity deliveryType = order.getDeliveryType();
				Long deliveryTypeCd = deliveryType.getDeliveryTypeCd();
				SapContractEntity sapContract = order.getSapContract();
				String incotermsCd = sapContract.getIncotermsCd();
				
				if (LogonUtil.isPickupOrder(deliveryTypeCd) 
						|| LogonUtil.isScheculedPickupOrder(deliveryTypeCd, incotermsCd) ) {
					// In pickup case, the order is set to delivered right away (non-PDA), so this DN is the final DN.
					orderService.generateAndSendPDFWithoutExplicitLoadingpoint(order, true, true, true);
				}
				else {
					orderService.generateAndSendPDFWithoutExplicitLoadingpoint(order, false, false, true);
				}
			}
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void _updateBagLoad(Long loadId, Long loadingpointId,
			Map<Long, LoadedQuantityData> loadingData, Date loadingStartTime, Date loadingEndTime,
			ConfSeqLock lock) {
		
		final LoadEntity load = getDaoProvider().getLoadEntityDao().findById(loadId);
		final LoadingpointEntity loadingpoint;
		if (loadingpointId != null) {
			loadingpoint = getDaoProvider().getLoadingpointEntityDao().findById(loadingpointId);
		}
		else {
			loadingpoint = null;
		}
			
		loadingService.updateSharedLoadAndSendGoodsMovements(load, loadingpoint, loadingData,
				NO_TARE_WEIGHT, NO_GROSS_WEIGHT, loadingStartTime, loadingEndTime,
				EventSource.LOGON, lock);
	}


	/**
	 * Updates the truck load from the container load, and will trigger a truck composition change
	 * with the new container.
	 * Besides triggers a <code>startLoad</code> before and an <code>endLoad</code> after the
	 * successful composition change.
	 * @param loadId The truck load is going to be updated with the container's current load
	 * @param containerId The container is going to be added to the truck's composition
	 * @param loadingpointId The loading point for the truck load
	 */
	public void updateContainerLoad(Long loadId, Long containerId, Long loadingpointId)
	{
		final Set<OrderEntity> orders = loadingService.findOrdersForLoadId(loadId);
		
		ConfSeqLock lock = null;
		try {
			lock = sequenceService.acquireLockForOrders(orders);
			manualLoadingService._updateContainerLoad(loadId, containerId, loadingpointId, lock);
		}
		finally {
			try {
				sequenceService.releaseLock(lock);
			}
			catch (LogonException e) {
				LOG.error(e.getMessage());
			}
		}
		
		em.clear();
		final Set<OrderEntity> updatedOrders = loadingService.findOrdersForLoadId(loadId);
		for (OrderEntity order : updatedOrders) {
			if (LogonStatusUtils.isLoadedOrBeyond(order.getOrderStatus())) {
				if (LogonUtil.isPickupOrder(order.getDeliveryType().getDeliveryTypeCd())) {
					// In pickup case, the order is set to delivered right away (non-PDA), so this DN is the final DN.
					orderService.generateAndSendPDFWithoutExplicitLoadingpoint(order, true, true, true);
				}
				else {
					orderService.generateAndSendPDFWithoutExplicitLoadingpoint(order, false, false, true);
				}
			}
		}
	}


	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void _updateContainerLoad(Long loadId, Long containerId, Long loadingpointId, ConfSeqLock lock) {
		final LoadEntity load = getDaoProvider().getLoadEntityDao().findById(loadId);
		final TruckEntity truck = load.getTruck();
		final TrailerEntity trailer = truckAdminService.fetchTrailerForTruck(truck.getTruckId(), false);
		final ContainerEntity container = 
				getDaoProvider().getContainerEntityDao().findById(containerId);
		final AssignmentPartEntity ap = loadingService.findAssignmentPartForLoad(load);
		final AssignmentEntity assignment = ap.getAssignment();
		final LoadingpointEntity loadingpoint;
		if (loadingpointId != null) {
			loadingpoint = getDaoProvider().getLoadingpointEntityDao().findById(loadingpointId);
		}
		else {
			loadingpoint = null;
		}
		
		final List<Long> orderItemIds = loadingService.fetchOrderItemIdsForLoadId(loadId);
		if (orderItemIds.size() != 1) {
			LOG.error("The container load is supposed to be related to a single order item, and not a shared load.");
			throw new LogonException();
		}
		
		assignment.initialize();
		
			//create bizevent loaded, and start the loading
			BizeventEntity loadingBusinessEvent = new BizeventEntity();
			loadingBusinessEvent.setEventTs(new Date());
			loadingBusinessEvent.setTruck(truck);
			loadingBusinessEvent.setTrailer(trailer);
			loadingBusinessEvent.setContainer(container);
			loadingBusinessEvent.setWagon(load.getWagon());
			loadingBusinessEvent.setAssignment(assignment);
			if (loadingpoint != null) {
				loadingBusinessEvent.setShippingpoint(loadingpoint.getShippingpoint());
			}
			loadingBusinessEvent.setBizeventType(BizEventUtil.BIZEVENT_TYPE_LOADING);
			
			orderService.startLoad(loadingBusinessEvent, load, loadingpoint, null, EventSource.LOGON);
			
			loadingBusinessEvent = monitoringTablesService.processBizevent(loadingBusinessEvent);
			
			BizeventEntity scanContainerBusinessEvent = new BizeventEntity();
			scanContainerBusinessEvent.setEventTs(new Date());
			scanContainerBusinessEvent.setTruck(truck);
			scanContainerBusinessEvent.setTrailer(trailer);
			scanContainerBusinessEvent.setContainer(container);
			scanContainerBusinessEvent.setAssignment(assignment);
			scanContainerBusinessEvent.setBizeventType(BizEventUtil.BIZEVENT_TYPE_SCAN_CONTAINER);
			
			//update the truck load from the container load, and do the composition change for the truck - attach the container to the truck
			orderService.updateTruckLoadFromContainerLoad(scanContainerBusinessEvent, ap, container);
			
			scanContainerBusinessEvent = monitoringTablesService.processBizevent(scanContainerBusinessEvent);
			//create bizevent loaded, and end the load
			BizeventEntity loadedBusinessEvent = new BizeventEntity();
			loadedBusinessEvent.setEventTs(new Date());
			loadedBusinessEvent.setTruck(truck);
			loadedBusinessEvent.setTrailer(trailer);
			loadedBusinessEvent.setContainer(container);
			loadedBusinessEvent.setWagon(load.getWagon());
			loadedBusinessEvent.setAssignment(assignment);
			if (loadingpoint != null) {
				loadedBusinessEvent.setShippingpoint(loadingpoint.getShippingpoint());
			}	
			loadedBusinessEvent.setBizeventType(BizEventUtil.BIZEVENT_TYPE_LOADED );
			
			BigDecimal netQuantityOfLoad = load.getNetQuantity();
			orderService.endLoadAndSendGoodsMovements(loadedBusinessEvent, load, loadingpoint, NO_TARE_WEIGHT,
					NO_GROSS_WEIGHT, netQuantityOfLoad, ACCURATE, VALIDATE_COMPOSITION,
					EventSource.LOGON, lock);
			
			loadedBusinessEvent = monitoringTablesService.processBizevent(loadedBusinessEvent);		
	}
}
