/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.beans.LoadedQuantityData;
import com.crh.logon.business.services.dispatchers.EndLoadingServiceDispatcher;
import com.crh.logon.business.services.eventbeans.in.StartLoadingRequestHandlerLocalInterface;
import com.crh.logon.business.services.impl.endload.ParsedEndLoadingPart;
import com.crh.logon.business.services.impl.endload.ParsedPalletReturn;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.interfaces.ConfigurableSequencesService;
import com.crh.logon.business.services.interfaces.EndLoadingService;
import com.crh.logon.business.services.localinterfaces.EndLoadingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LDSServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LoadingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.MonitoringTablesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UomConverterServiceLocalInterface;
import com.crh.logon.business.services.utils.AssignmentUtils;
import com.crh.logon.business.services.utils.ConfSeqLock;
import com.crh.logon.business.services.utils.LogonEventContextBean;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.LogonStatusUtils;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.EventSource;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.refdata.BizeventTypeBsEntity;
import com.crh.logon.persistence.entities.common.FireqMaterialMixdesignEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusBsEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.holcim.logon.eventbeans.in.logon_lds_interface.AttorneyData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.DispatcherData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.DriverData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.EndLoadingPartData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.EndLoadingRequest;
import com.holcim.logon.eventbeans.in.logon_lds_interface.FirstWeighingData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.LoadedMaterialData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.LocationData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.SecondWeighingData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.SetLoadOrder2LoadedRequest;
import com.holcim.logon.eventbeans.in.logon_lds_interface.StartLoadingRequest;
import com.holcim.logon.eventbeans.in.logon_lds_interface.StartLoadingResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.VehicleData;
import com.holcim.logon.eventbeans.in.parallel_rail.EndLoadRequest;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
@AdnTrace
public class EndLoadingServiceBean extends EndLoadingServiceDispatcher implements EndLoadingServiceLocalInterface {
	
	private static final boolean ACCURATE = true;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(EndLoadingServiceBean.class);

	private static final boolean VALIDATE_COMPOSITION = true;

	private static final BigDecimal NO_TARE_WEIGHT = null;

	private static final Date NO_LOADING_START_TIME = null;

	@EJB private OrderServiceLocalInterface orderService;
	@EJB private LoadingServiceLocalInterface loadingService;
	@EJB private MonitoringTablesServiceLocalInterface monitoringTablesService;
	@EJB private StartLoadingRequestHandlerLocalInterface startLoadingRequestHandler;
	@EJB private LDSServiceLocalInterface ldsService;
	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;
	@EJB private UomConverterServiceLocalInterface unitOfMeasurementConverterService;
	
	public void endLoading(AssignmentEntity assignment,
			ShippingpointEntity sp,
			EndLoadingRequest request,
			List<ParsedEndLoadingPart> endLoadingParts,
			List<ParsedPalletReturn> palletReturns,
			Map<Long, String> ldsDeliveryNoteNumberMap) {

			final List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);
			final Set<OrderEntity> orderSet = new HashSet<OrderEntity>(orders);
			
			ConfSeqLock lock = null;
			ConfigurableSequencesService configurableSequenceService = 
					getServiceFor(ConfigurableSequencesService.class);
			try {
				lock = configurableSequenceService.acquireLockForOrders(orderSet);
				getServiceFor(EndLoadingService.class)._endLoading(assignment.getAssignmentId(),
						sp,
						request,
						endLoadingParts,
						palletReturns,
						ldsDeliveryNoteNumberMap,
						lock);
			}
			finally {
				try {
					configurableSequenceService.releaseLock(lock);
				}
				catch (LogonException e) {
					LOG.error(e.getMessage());
				}
			}
			
			/*
			 * Printing is done outside the transaction which performs the endLoading. Thus we can
			 * guarantee that no delivery note is sent out for an order whose endLoading can still be
			 * rolled back.
			 * In order for the changes to take effect, we must clear the EntityManager and reload the
			 * entire assignment.
			 */
			em.clear();
			final AssignmentEntity updatedAssignment = 
					getDaoProvider().getAssignmentEntityDao().findById(assignment.getAssignmentId());
			final List<OrderEntity> updatedOrders = 
					AssignmentUtils.getOrdersFromAssignment(updatedAssignment);
			for (OrderEntity order : updatedOrders) {
				if (LogonUtil.isPickupOrder(order.getDeliveryType().getDeliveryTypeCd())) {
					// In pickup case, the order is set to delivered right away (non-PDA), so this DN is the final DN.
					orderService.generateAndSendPDFWithoutExplicitLoadingpoint(order, true, true, true);
				}
				else {
					orderService.generateAndSendPDFWithoutExplicitLoadingpoint(order, false, false, true);
				}
			}
	}
	
	public void setLoadOrder2Loaded(AssignmentEntity assignment,
			LoadingpointEntity startLoadingpoint,
			LoadingpointEntity endLoadingpoint,
			BigDecimal tareWeightT, BigDecimal grossWeighT,
			SetLoadOrder2LoadedRequest request) {

			final List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);
			
			ConfSeqLock lock = null;
			ConfigurableSequencesService configurableSequenceService = 
					getServiceFor(ConfigurableSequencesService.class);
			try {
				lock = configurableSequenceService.acquireLockForOrders(orders);
				getServiceFor(EndLoadingService.class)._setLoadOrder2Loaded(assignment.getAssignmentId(),
						startLoadingpoint,
						endLoadingpoint,
						tareWeightT,
						grossWeighT,
						request,
						lock);
			}
			finally{
				try {
					configurableSequenceService.releaseLock(lock);
				}
				catch (LogonException e) {
					LOG.error(e.getMessage());
				}
			}
			
			/*
			 * Printing is done outside the transaction which performs the endLoading. Thus we can
			 * guarantee that no delivery note is sent out for an order whose endLoading can still be
			 * rolled back.
			 * In order for the changes to take effect, we must clear the EntityManager and reload the
			 * entire assignment.
			 */
			em.clear();
			final AssignmentEntity updatedAssignment = 
					getDaoProvider().getAssignmentEntityDao().findById(assignment.getAssignmentId());
			final List<OrderEntity> updatedOrders = 
					AssignmentUtils.getOrdersFromAssignment(updatedAssignment);
			for (OrderEntity order : updatedOrders) {
				if (LogonUtil.isPickupOrder(order.getDeliveryType().getDeliveryTypeCd())) {
					// In pickup case, the order is set to delivered right away (non-PDA), so this DN is the final DN.
					orderService.generateAndSendPDFWithoutExplicitLoadingpoint(order, true, true, true);
				}
				else {
					orderService.generateAndSendPDFWithoutExplicitLoadingpoint(order, false, false, true);
				}
			}
	}


	/**
	 * 
	 * @param detachedBizevent
	 * @param tareWeightT
	 * @param netQuantityT
	 * @param grossWeightT
	 * @param accurateWeights
	 * @param load
	 */
	public void endLoadMop(BizeventEntity detachedBizevent, Long loadId, LoadingpointEntity lp, BigDecimal tareWeightT,
			BigDecimal netQuantityT, BigDecimal grossWeightT, BigDecimal loadedQuantityInPieces, boolean accurateWeights)
	{
			final Set<OrderEntity> orders = loadingService.findOrdersForLoadId(loadId);
			
			ConfSeqLock lock = null;
			final ConfigurableSequencesService configurableSequenceService = 
					getServiceFor(ConfigurableSequencesService.class);
			try {
				lock = configurableSequenceService.acquireLockForOrders(orders);
				getServiceFor(EndLoadingService.class)._endLoadMop(detachedBizevent, loadId, tareWeightT, netQuantityT,
						grossWeightT, loadedQuantityInPieces, accurateWeights, lock, lp);
			}
			finally{
				try {
					configurableSequenceService.releaseLock(lock);
				}
				catch (LogonException e) {
					LOG.error(e.getMessage());
				}
			}
			
			/*
			 * Printing is done outside the transaction which performs the endLoading. Thus we can
			 * guarantee that no delivery note is sent out for an order whose endLoading can still be
			 * rolled back.
			 * In order for the changes to take effect, we must clear the EntityManager and reload the
			 * entire assignment.
			 */
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


	@AdnTrace
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void _endLoadMop(BizeventEntity detachedBizevent, Long loadId, BigDecimal tareWeightT, 
			BigDecimal netQuantityT, BigDecimal grossWeightT, BigDecimal loadedQuantityInPieces,
			boolean accurateWeights, ConfSeqLock lock, LoadingpointEntity lp)
	{
		// At this point, the assignment in the bizevent is not managed. Make it managed first.
		final Long assignmentId = detachedBizevent.getAssignment().getAssignmentId();
		final AssignmentEntity assignment = 
				getDaoProvider().getAssignmentEntityDao().findById(assignmentId);
		detachedBizevent.setAssignment(assignment);
		
		// Now the bizevent itself can be persisted. Processing happens after endLoad only, though.
		final BizeventEntity bizevent = getDaoProvider().getBizeventEntityDao().persist(detachedBizevent);
		
		// For bag orders, loadedQuantity = orderedQuantity
		// Set this on the order items, endLoad will use this information to update the load.
		final LoadEntity load = getDaoProvider().getLoadEntityDao().findById(loadId);
		BigDecimal netQuantityInAnyUom = netQuantityT;
		if (load != null) {
			List<OrderItemEntity> orderItems = orderService.getOrderItems(load);
			if (orderItems.isEmpty()) {
				String message = "a load (" + loadId + ") must have at least 1 orderitem, if it referred by an assignmentpart";
				throw new LogonException(message, Reason.RES_IMPL_BUG);
			}
			boolean sharedLoad = orderItems.size() > 1;
			
			if (load.getLoadedFromLoad() == null) {
				// Check if scanWagon was processed succesfully (ADN55475)
				if (!sharedLoad) {
					OrderItemEntity orderItem = orderItems.iterator().next();
					if (orderItem.getBimodalRailOrderItem() != null) {
						LOG.error("Warning: scanWagon missing or failed for load " + load.getLoadId() + ", correcting data");
						load.setLoadedFromLoad(orderItem.getBimodalRailOrderItem().getLoad());
					}
				}
			}
			if (LogonPackageHandlingUtil.isBag(load)) {
				LogonUtil.copyToLoadedFromOrdered(orderItems);
				if (LogonUtil.isReturnPalletOnly(orderItems.get(0))) {
					OrderItemEntity onlyOneOrderItemOfLoad = orderItems.get(0);
					netQuantityInAnyUom = loadedQuantityInPieces;
					onlyOneOrderItemOfLoad.setLoadedQuantity(loadedQuantityInPieces);
				}
				else {
					netQuantityInAnyUom = LogonUtil.getSumLoadQuantitiesFromOrderItems(orderItems);
				}
			}
		}
		
		orderService.endLoadAndSendGoodsMovements(bizevent, load, lp, tareWeightT, grossWeightT,
				netQuantityInAnyUom, accurateWeights, VALIDATE_COMPOSITION, EventSource.MOP, lock);
		
		monitoringTablesService.processBizevent(bizevent);
	}

	/**
	 * 
	 * @param detachedBizevent
	 * @param lp
	 * @param assignment
	 */
	public void skipLdsLoading(BizeventEntity detachedBizevent, LoadingpointEntity lp)
	{
		final List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(detachedBizevent.getAssignment());

		ConfSeqLock lock = null;
		final ConfigurableSequencesService configurableSequenceService = 
				getServiceFor(ConfigurableSequencesService.class);
		try {
			lock = configurableSequenceService.acquireLockForOrders(orders);
			getServiceFor(EndLoadingService.class)._skipLdsLoading(detachedBizevent, lock, lp);
		}
		finally{
			try {
				configurableSequenceService.releaseLock(lock);
			}
			catch (LogonException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}
	
	@AdnTrace
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void _skipLdsLoading(BizeventEntity detachedBizevent, ConfSeqLock lock, LoadingpointEntity lp){
		// At this point, the assignment in the bizevent is not managed. Make it managed first.
		final Long assignmentId = detachedBizevent.getAssignment().getAssignmentId();
		final AssignmentEntity assignment = 
				getDaoProvider().getAssignmentEntityDao().findById(assignmentId);
		detachedBizevent.setAssignment(assignment);
		List<LoadEntity> loads = AssignmentUtils.getLoadsFromAssignment(assignment);
		// Now the bizevent itself can be persisted. Processing happens after endLoad only, though.
		final BizeventEntity bizevent = getDaoProvider().getBizeventEntityDao().persist(detachedBizevent);
		List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);
		for (OrderEntity oe: orders) {
		 	oe.setLoadConfSmsFlag(false);
			oe.setLoadConfFaxFlag(false);
			oe.setLoadConfEmailFlag(false);
		}
		for (LoadEntity load : loads) {
			// For bag orders, loadedQuantity = orderedQuantity
			// Set this on the order items, endLoad will use this information to update the load.
			if (load != null) {
				List<OrderItemEntity> orderItems = orderService.getOrderItems(load);
				boolean sharedLoad = orderItems.size() > 1;
				if (sharedLoad) {
					String message = "shared load is not supported";
					throw new LogonException(message, Reason.RES_IMPL_BUG);
				}
				
				if (LogonPackageHandlingUtil.isBag(load)) {
					LogonUtil.copyToLoadedFromOrdered(orderItems);
				}
				load.setLdsOfflineMode(Boolean.TRUE);
			}
			
			orderService.endLoad(bizevent, load, lp, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false,
					VALIDATE_COMPOSITION, false, EventSource.MOP, lock);			
		}
		
		monitoringTablesService.processBizevent(bizevent);
	}

	/**
	 * 
	 * @param detachedBizevent
	 * @param loadId
	 * @param lp
	 * @param tareWeightT
	 * @param grossWeightT
	 * @param netQuantityT
	 * @param bean
	 * @return Whether endLoad was successful - or aborted
	 */
	public boolean endLoadRail(BizeventEntity detachedBizevent, Long loadId, LoadingpointEntity lp,
			BigDecimal tareWeightT, BigDecimal grossWeightT, BigDecimal netQuantityT,
			EndLoadRequest bean)
	{
			boolean success = false;
			
			final Set<OrderEntity> orders = loadingService.findOrdersForLoadId(loadId);
			
			ConfSeqLock lock = null;
			final ConfigurableSequencesService configurableSequenceService = 
					getServiceFor(ConfigurableSequencesService.class);
			try {
				lock = configurableSequenceService.acquireLockForOrders(orders);
				success = getServiceFor(EndLoadingService.class)._endLoadRail(detachedBizevent, loadId, lp,
						tareWeightT, grossWeightT, netQuantityT, bean, lock);
			}
			finally{
				try {
					configurableSequenceService.releaseLock(lock);
				}
				catch (LogonException e) {
					LOG.error(e.getMessage());
				}
			}
			
			/*
			 * Printing is done outside the transaction which performs the endLoading. Thus we can
			 * guarantee that no delivery note is sent out for an order whose endLoading can still be
			 * rolled back.
			 * In order for the changes to take effect, we must clear the EntityManager and reload the
			 * entire assignment.
			 */
			em.clear();
			final Set<OrderEntity> updatedOrders = loadingService.findOrdersForLoadId(loadId);
			for (OrderEntity order : updatedOrders) {
				if (LogonStatusUtils.isLoadedOrBeyond(order.getOrderStatus())) {
					orderService.generateAndSendPDFWithoutExplicitLoadingpoint(order, false, false, true);
				}
			}
			
			return success;
	}


	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean _endLoadRail(BizeventEntity detachedBizevent, Long loadId, LoadingpointEntity loadingpoint,
			BigDecimal tareWeightT, BigDecimal grossWeightT, BigDecimal netQuantityT,
			EndLoadRequest bean, ConfSeqLock lock)
	{
		AssignmentEntity assignment = null;
		if (detachedBizevent.getAssignment() != null) {
			// At this point, the assignment in the bizevent is not managed. Make it managed first.
			final Long assignmentId = detachedBizevent.getAssignment().getAssignmentId();
			assignment = getDaoProvider().getAssignmentEntityDao().findById(assignmentId);
			detachedBizevent.setAssignment(assignment);
		}
		
		// Now the bizevent itself can be persisted. Processing happens after endLoad only, though.
		final BizeventEntity bizevent = getDaoProvider().getBizeventEntityDao().persist(detachedBizevent);
		
		final LoadEntity load = getDaoProvider().getLoadEntityDao().findById(loadId);
		
		OrderEntity order = null;
		if (assignment != null) {
			List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);
			if (!orders.isEmpty()) {
				order = orders.get(0);
			}
		}
		
		if (order != null) {
			// standard case
			order.setLdsDeliveryNoteNo(bean.getWsDeliveryNoteNo());
		}
		else {
			// pre-load case
			load.setWsDeliveryNoteNo(bean.getWsDeliveryNoteNo());
		}
		
		load.setWsEndLoadingpoint(loadingpoint);
		load.setWsOriginalGrossMeasureT(grossWeightT);
		load.setWsOriginalTareMeasureT(tareWeightT);
		load.setWsTareQuitWarnFlag(bean.isTareQuit()!=null?bean.isTareQuit():Boolean.FALSE);
		load.setWsEichbeleg1(bean.getWsEichbeleg1());
		load.setWsEichbeleg1Time(bean.getWsEichbeleg1Time());
		load.setWsEichbeleg1Balance(bean.getWsEichbeleg1Balance());
		load.setWsEichbeleg2(bean.getWsEichbeleg2());
		load.setWsEichbeleg2Time(bean.getWsEichbeleg2Time());
		load.setWsEichbeleg2Balance(bean.getWsEichbeleg2Balance());
		
		boolean success = orderService.endLoadAndSendGoodsMovements(
				bizevent, load, loadingpoint,
				tareWeightT, grossWeightT, netQuantityT,
				ACCURATE, VALIDATE_COMPOSITION,
				EventSource.RAIL,
				lock);
		
		monitoringTablesService.processBizevent(bizevent);
		
		return success;
	}


	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void _endLoading(Long assignmentId,
			ShippingpointEntity sp,
			EndLoadingRequest request,
			List<ParsedEndLoadingPart> endLoadingParts,
			List<ParsedPalletReturn> palletReturns,
			Map<Long, String> ldsDeliveryNoteNumberMap,
			ConfSeqLock lock) {

		final AssignmentEntity assignment = getDaoProvider().getAssignmentEntityDao().findById(assignmentId);
		processAssignmentData(request, assignment);
		final List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);
		
		for (OrderEntity oe : orders) {
			// Update notes
			if (StringUtils.isNotEmpty(request.getNoteDelivery())) {
				oe.setNoteDelivery(request.getNoteDelivery());
			}
			if (StringUtils.isNotEmpty(request.getNoteLDS())) {
				oe.setNoteDispatching(request.getNoteLDS());
			}
			
			// Update lds delivery note numbers
			oe.setLdsDeliveryNoteNo(ldsDeliveryNoteNumberMap.get(oe.getOrderNumber()));
			
			// Process pallet returns
			final List<OrderItemEntity> items = oe.getOrderItems();
			for (OrderItemEntity item : items) {
				// Check if there is a pallet return.
				/* Bad algorithm to loop here through all returnPallet objects, but let's
				 * assume that the number of total returned pallet entries is very small (0 or 1).
				 * If it becomes an issue, create a hashmap from the returnPallet data and look
				 * up any return by orderNo and sapMaterialNo.
				 */
				for (ParsedPalletReturn palletReturn : palletReturns)
				{
					final Long returnOrderNo = palletReturn.getOrderNo();
					final String returnSapMatNo = palletReturn.getSapMaterialNo();
					
					if (item.getOrder().getOrderNumber().equals(returnOrderNo)
							&& item.getSapMaterial().getSapMaterialIfcNo().equals(returnSapMatNo))
					{
						item.setReturnedQuantity(palletReturn.getQuantity());
						item.setReturnedUom(palletReturn.getUnitOfMeasurement());
					}
				}
				LoadEntity load = item.getLoad();
				if (load != null) {
					load.setLdsOfflineMode(Boolean.FALSE);
				}
			}
		}
		
		orderService.setDefaultReturnReasonForPallets(orders);
	
		for (ParsedEndLoadingPart endLoadingPart : endLoadingParts) {
			
			final EndLoadingPartData endLoadingPartData = endLoadingPart.getEndLoadingPartData();
			final AssignmentPartEntity ape = 
					getDaoProvider().getAssignmentPartEntityDao().findById(endLoadingPart.getAssignmentPartId());
			
			final LoadEntity load = ape.getLoad();
			final LoadingpointEntity loadingpoint = endLoadingPart.getLoadingPoint();
			final Date loadingEndTime = endLoadingPart.getLoadingEndTime();
			final BigDecimal grossWeightT = endLoadingPart.getGrossWeightInTons();
			final Map<Long, LoadedQuantityData> loadingData = endLoadingPart.getLoadingData();
			final Long mixDesignId = endLoadingPart.getMixDesignId();
			
			final FirstWeighingData firstWeighingData = endLoadingPartData.getFirstWeighing();
			final SecondWeighingData secondWeighingData = endLoadingPartData.getSecondWeighing();
			
			if (firstWeighingData != null && firstWeighingData.getW1Quantity() != null) {
				final LocationData locationData = ldsService.createLocationData(sp, loadingpoint);
				callStartLoadingRequestHandler(request, endLoadingPartData, locationData);
			}
			
			load.setWsEndLoadingpoint(loadingpoint);
			load.setWsEnterShippingpointTime(request.getLdsEnterShippingpointTime());
			load.setWsLeaveShippingpointTime(request.getLdsLeaveShippingpointTime());
			
			if (secondWeighingData != null) {
				load.setWsEichbeleg2(secondWeighingData.getLdsW2RecordId());
				load.setWsEichbeleg2Time(secondWeighingData.getLdsW2Time());
				load.setWsEichbeleg2Balance(secondWeighingData.getLdsW2BalanceId());
				
				if (grossWeightT != null) {
					load.setWsOriginalGrossMeasureT(grossWeightT);
				}
			}
			
			final LoadedMaterialData loadedMaterial = endLoadingPartData.getMaterial();
			processMixDesign(loadedMaterial, mixDesignId, load);
			
			load.setWsSiloNo(StringUtils.stripToNull(loadedMaterial.getSiloNo()));
			load.setWsSampleNo(StringUtils.stripToNull(loadedMaterial.getLdsSampleNo()));
			load.setWsCustomerOrderNo(StringUtils.stripToNull(request.getLdsCustomerOrderNo()));
			load.setWsTareQuitWarnFlag((Boolean) ObjectUtils.defaultIfNull(request.isTareDeviationDriverACK(), Boolean.FALSE));
			VehicleData vehicle = request.getVehicle();
			if (vehicle != null) {				
				load.setWsTruckPlateNo(StringUtils.stripToNull(vehicle.getTruckPlateNo()));
				load.setWsTrailerPlateNo(StringUtils.stripToNull(vehicle.getTrailerPlateNo()));
				
				String carrierName = "";
				if (vehicle.getCommercialCarrier() != null && StringUtils.isNotEmpty(vehicle.getCommercialCarrier().getName1())) {
					carrierName = vehicle.getCommercialCarrier().getName1();
				} else if (vehicle.getExecutingCarrier() != null && StringUtils.isNotEmpty(vehicle.getExecutingCarrier().getName1())) {
					carrierName = vehicle.getExecutingCarrier().getName1();
				}				
				load.setWsCarrier(StringUtils.strip(carrierName));
			}

			loadingService.updateSharedLoadAndSendGoodsMovements(
					load, loadingpoint, loadingData,
					NO_TARE_WEIGHT, grossWeightT, NO_LOADING_START_TIME, loadingEndTime,
					EventSource.LDS,
					lock);
		}
		return;
	}
	
	public void regularization( AssignmentEntity assignment,
			ShippingpointEntity sp,
			EndLoadingRequest request,
			List<ParsedEndLoadingPart> endLoadingParts,
			List<ParsedPalletReturn> palletReturns,
			Map<Long, String> ldsDeliveryNoteNumberMap) {

			final List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);
			final Set<OrderEntity> orderSet = new HashSet<OrderEntity>(orders);
			
			ConfSeqLock lock = null;
			ConfigurableSequencesService configurableSequenceService = 
					getServiceFor(ConfigurableSequencesService.class);
			try {
				lock = configurableSequenceService.acquireLockForOrders(orderSet);
				getServiceFor(EndLoadingService.class)._regularization(assignment.getAssignmentId(),
						sp,
						request,
						endLoadingParts,
						palletReturns,
						ldsDeliveryNoteNumberMap,
						lock);
			}
			finally {
				try {
					configurableSequenceService.releaseLock(lock);
				}
				catch (LogonException e) {
					LOG.error(e.getMessage(),e);
				}
			}
			
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void _regularization(Long assignmentId,
			ShippingpointEntity sp,
			EndLoadingRequest request,
			List<ParsedEndLoadingPart> endLoadingParts,
			List<ParsedPalletReturn> palletReturns,
			Map<Long, String> ldsDeliveryNoteNumberMap,
			ConfSeqLock lock) {

		
		final AssignmentEntity assignment = getDaoProvider().getAssignmentEntityDao().findById(assignmentId);
		processAssignmentData(request, assignment);
		final List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);
		
		BizeventEntity event = new BizeventEntity();

		event.setBizeventType(referentialDataService.findBsEntity(BizeventTypeBsEntity.class,
				Constant.BIZEVENT_TYPE_REGULARIZATION));
		event.setEventTs(request.getEventTime());
		event.setTruck(assignment.getTruck());
		event.setShippingpoint(sp);
		event.setAssignment(assignment);
		getDaoProvider().getBizeventEntityDao().persist(event);
		
		for (OrderEntity oe : orders) {
			// Update notes
			if (StringUtils.isNotEmpty(request.getNoteDelivery())) {
				oe.setNoteDelivery(request.getNoteDelivery());
			}
			if (StringUtils.isNotEmpty(request.getNoteLDS())) {
				oe.setNoteDispatching(request.getNoteLDS());
			}

			//if regularization comes in status transmitted to driver state that means that the driver did not react at all to the order -> close it after loading
			if (oe.getOrderStatus().getOrderStatusCd() == Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER) {
				oe.setNoPdaOrRailEventsFlag(true);	
			}
			oe.setLoadConfEmailFlag(false);
			oe.setLoadConfFaxFlag(false);
			oe.setLoadConfSmsFlag(false);

			
			// Update lds delivery note numbers
			oe.setLdsDeliveryNoteNo(ldsDeliveryNoteNumberMap.get(oe.getOrderNumber()));
			
			// Process pallet returns
			final List<OrderItemEntity> items = oe.getOrderItems();
			for (OrderItemEntity item : items) {
				// Check if there is a pallet return.
				/* Bad algorithm to loop here through all returnPallet objects, but let's
				 * assume that the number of total returned pallet entries is very small (0 or 1).
				 * If it becomes an issue, create a hashmap from the returnPallet data and look
				 * up any return by orderNo and sapMaterialNo.
				 */
				for (ParsedPalletReturn palletReturn : palletReturns)
				{
					final Long returnOrderNo = palletReturn.getOrderNo();
					final String returnSapMatNo = palletReturn.getSapMaterialNo();
					
					if (item.getOrder().getOrderNumber().equals(returnOrderNo)
							&& item.getSapMaterial().getSapMaterialIfcNo().equals(returnSapMatNo))
					{
						item.setReturnedQuantity(palletReturn.getQuantity());
						item.setReturnedUom(palletReturn.getUnitOfMeasurement());
					}
				}
				LoadEntity load = item.getLoad();
				if (load != null) {
					load.setLdsOfflineMode(Boolean.FALSE);
				}
			}
		}
		
		orderService.setDefaultReturnReasonForPallets(orders);
	
		BigDecimal tareWeightT = null;
		Date ldsW1Time = null;
		for (ParsedEndLoadingPart endLoadingPart : endLoadingParts) {
			
			final EndLoadingPartData endLoadingPartData = endLoadingPart.getEndLoadingPartData();
			final AssignmentPartEntity ape = 
					getDaoProvider().getAssignmentPartEntityDao().findById(endLoadingPart.getAssignmentPartId());
			
			final LoadEntity load = ape.getLoad();
			final LoadingpointEntity loadingpoint = endLoadingPart.getLoadingPoint();
			final Date loadingEndTime = endLoadingPart.getLoadingEndTime();
			final BigDecimal grossWeightT = endLoadingPart.getGrossWeightInTons();
			final Map<Long, LoadedQuantityData> loadingData = endLoadingPart.getLoadingData();
			final Long mixDesignId = endLoadingPart.getMixDesignId();
			
			final FirstWeighingData firstWeighingData = endLoadingPartData.getFirstWeighing();
			final SecondWeighingData secondWeighingData = endLoadingPartData.getSecondWeighing();
			
			load.setWsEndLoadingpoint(loadingpoint);
			load.setWsEnterShippingpointTime(request.getLdsEnterShippingpointTime());
			load.setWsLeaveShippingpointTime(request.getLdsLeaveShippingpointTime());
			
			// LDS specific first weighing data
			if (firstWeighingData != null) {
				ldsW1Time = firstWeighingData.getLdsW1Time();
				load.setWsEichbeleg1(firstWeighingData.getLdsW1RecordId());
				load.setWsEichbeleg1Time(firstWeighingData.getLdsW1Time());
				load.setWsEichbeleg1Balance(firstWeighingData.getLdsW1BalanceId());
				load.setWsStartLoadingpoint(loadingpoint);
				if (firstWeighingData.getW1UoM() != null) {
					tareWeightT = firstWeighingData.getW1Quantity();
				}
				load.setWsTareQuitWarnFlag(request.isTareDeviationDriverACK());
			}
			
			if (secondWeighingData != null) {
				load.setWsEichbeleg2(secondWeighingData.getLdsW2RecordId());
				load.setWsEichbeleg2Time(secondWeighingData.getLdsW2Time());
				load.setWsEichbeleg2Balance(secondWeighingData.getLdsW2BalanceId());
				
				if (grossWeightT != null) {
					load.setWsOriginalGrossMeasureT(grossWeightT);
				}
			}
			
			final LoadedMaterialData loadedMaterial = endLoadingPartData.getMaterial();
			processMixDesign(loadedMaterial, mixDesignId, load);
			
			load.setWsSiloNo(StringUtils.stripToNull(loadedMaterial.getSiloNo()));
			load.setWsSampleNo(StringUtils.stripToNull(loadedMaterial.getLdsSampleNo()));
			load.setWsCustomerOrderNo(StringUtils.stripToNull(request.getLdsCustomerOrderNo()));
			load.setWsTareQuitWarnFlag((Boolean) ObjectUtils.defaultIfNull(request.isTareDeviationDriverACK(), Boolean.FALSE));
			VehicleData vehicle = request.getVehicle();
			if (vehicle != null) {				
				load.setWsTruckPlateNo(StringUtils.stripToNull(vehicle.getTruckPlateNo()));
				load.setWsTrailerPlateNo(StringUtils.stripToNull(vehicle.getTrailerPlateNo()));
				
				String carrierName = "";
				if (vehicle.getCommercialCarrier() != null && StringUtils.isNotEmpty(vehicle.getCommercialCarrier().getName1())) {
					carrierName = vehicle.getCommercialCarrier().getName1();
				} else if (vehicle.getExecutingCarrier() != null && StringUtils.isNotEmpty(vehicle.getExecutingCarrier().getName1())) {
					carrierName = vehicle.getExecutingCarrier().getName1();
				}				
				load.setWsCarrier(StringUtils.strip(carrierName));
			}

			
			loadingService.updateSharedLoadAndSendGoodsMovements(
					load, loadingpoint, loadingData,
					tareWeightT, grossWeightT, ldsW1Time, loadingEndTime,
					EventSource.LDS,
					lock);
		}

		for (OrderEntity oe : orders) {
			if (oe.getOrderStatus().getOrderStatusCd() == Constant.ORDER_STATUS_DELIVERED_PENDING_LDS_DATA) {
				orderService.changeOrderStatus(oe,Constant.ORDER_STATUS_DELIVERED);
			}
		}

		return;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void _setLoadOrder2Loaded(Long assignmentId,
			LoadingpointEntity startLoadingpoint,
			LoadingpointEntity endLoadingpoint,
			BigDecimal tareWeightT,
			BigDecimal grossWeightT,
			SetLoadOrder2LoadedRequest request,
			ConfSeqLock lock) {
		
		final AssignmentEntity assignment = getDaoProvider().getAssignmentEntityDao().findById(assignmentId);
		final List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);
		final List<LoadEntity> loads = AssignmentUtils.getLoadsFromAssignment(assignment);
		
		for (OrderEntity oe : orders) {
			// Update notes
			if (StringUtils.isNotEmpty(request.getNoteDelivery())) {
				oe.setNoteDelivery(request.getNoteDelivery());
			}
			if (StringUtils.isNotEmpty(request.getNoteLDS())) {
				oe.setNoteDispatching(request.getNoteLDS());
			}
		}
		
		// Update LDS delivery note number (must be unique per order)
		if (orders.size() == 1) {
			orders.get(0).setLdsDeliveryNoteNo(request.getLdsDeliveryNoteNo());
		}
		
		final FirstWeighingData firstWeighing = request.getFirstWeighing();
		final SecondWeighingData secondWeighing = request.getSecondWeighing();
		for (LoadEntity load : loads) {
			
			// LDS specific general data
			load.setWsEnterShippingpointTime(request.getLdsEnterShippingpointTime());
			load.setWsLeaveShippingpointTime(request.getLdsLeaveShippingpointTime());
			load.setWsCustomerOrderNo(request.getLdsCustomerOrderNo());
			
			// LDS specific first weighing data
			if (firstWeighing != null && load.getLoadingTs() == null) {
				load.setWsEichbeleg1(firstWeighing.getLdsW1RecordId());
				load.setWsEichbeleg1Time(firstWeighing.getLdsW1Time());
				load.setWsEichbeleg1Balance(firstWeighing.getLdsW1BalanceId());
				load.setWsStartLoadingpoint(startLoadingpoint);
				load.setWsOriginalTareMeasureT(tareWeightT);
				load.setWsTareQuitWarnFlag(request.isTareDeviationDriverACK());
			}
			
			// LDS specific second weighing data
			if (secondWeighing != null) {
				load.setWsEichbeleg2(secondWeighing.getLdsW2RecordId());
				load.setWsEichbeleg2Time(secondWeighing.getLdsW2Time());
				load.setWsEichbeleg2Balance(secondWeighing.getLdsW2BalanceId());
				load.setWsEndLoadingpoint(endLoadingpoint);
				load.setWsOriginalGrossMeasureT(grossWeightT);
			}
		}
		
		final LoadingpointEntity lp;
		if (endLoadingpoint != null) {
			lp = endLoadingpoint;
		}
		else if (startLoadingpoint != null) {
			lp = startLoadingpoint;
		}
		else {
			lp = null;
		}
		
		final Date eventTime = request.getEventTime();
		final Date loadingStartTime;
		final Date loadingEndTime;
		
		if (firstWeighing != null && firstWeighing.getLoadingStartTime() != null) {
			loadingStartTime = firstWeighing.getLoadingStartTime();
		}
		else {
			loadingStartTime = eventTime;
		}
		if (secondWeighing != null && secondWeighing.getLoadingEndTime() != null) {
			loadingEndTime = secondWeighing.getLoadingEndTime();
		}
		else {
			loadingEndTime = eventTime;
		}
		
		loadingService.setOrdersToLoaded(orders, lp, tareWeightT, grossWeightT, 
				loadingStartTime, loadingEndTime, EventSource.LDS, lock);
	}
	
	private StartLoadingResponse callStartLoadingRequestHandler(EndLoadingRequest request,
			EndLoadingPartData endLoadingPartData,
			LocationData locationData) {

		final LogonEventContextBean contextBean = new LogonEventContextBean();
		contextBean.setInterfaceTypeCd(Constant.INTERFACE_TYPE_LOGON_LDS_INTERFACE_10);
		
		final FirstWeighingData firstWeighingData = endLoadingPartData.getFirstWeighing();
		
		final StartLoadingRequest startLoadingRequest = new StartLoadingRequest();
		startLoadingRequest.setEventName(request.getEventName());
		startLoadingRequest.setEventTime(request.getEventTime());
		startLoadingRequest.setLanguage(request.getLanguage());
		startLoadingRequest.setSenderInfo(request.getEventName());
		// specific properties
		startLoadingRequest.setFirstWeighing(firstWeighingData);
		startLoadingRequest.setLoadOrderItemId(endLoadingPartData.getLoadOrderItemId());
		startLoadingRequest.setLocation(locationData);
		startLoadingRequest.setMaterial(endLoadingPartData.getMaterial());
		startLoadingRequest.setNoteDelivery(request.getNoteDelivery());
		startLoadingRequest.setNoteLDS(request.getNoteLDS());		
		startLoadingRequest.setTareDeviationDriverACK(request.isTareDeviationDriverACK());
		startLoadingRequest.setSimulateOnly(request.isSimulateOnly());
		// LOGON-5349
		startLoadingRequest.setTareWarnIgnore(true);
		startLoadingRequest.setVehicle(request.getVehicle());
		
		// create & call handler
		return (StartLoadingResponse) startLoadingRequestHandler.handle(startLoadingRequest, false, contextBean);
	}
	
	private void processMixDesign(LoadedMaterialData loadedMaterial, Long mixDesignId, LoadEntity load) {

		final String aggRmxCode = loadedMaterial.getAggRmxCode();
		final String mixDesignCd = loadedMaterial.getMixDesignID();
		
		FireqMaterialMixdesignEntity mixDesign = null;
		if (mixDesignId != null) {
			mixDesign = getDaoProvider().getFireqMaterialMixdesignEntityDao().findById(mixDesignId);
		}
		if ((aggRmxCode != null) && (mixDesignCd != null)) {
			if (mixDesign == null) {
				mixDesign = new FireqMaterialMixdesignEntity();
			}
			
			mixDesign.setAggRmxCode(loadedMaterial.getAggRmxCode());
			mixDesign.setMixDesignCd(loadedMaterial.getMixDesignID());
			mixDesign.setPlant(load.getProductionPlant());
			mixDesign.setSapMaterial(load.getSapMaterial());
			
			getDaoProvider().getFireqMaterialMixdesignEntityDao().persist(mixDesign);
		}
		
		load.setMixDesign(mixDesign);
	}
	
	private void processAssignmentData(EndLoadingRequest request, AssignmentEntity assignment) {
		assignment.setLdsCarrierCrm(StringUtils.stripToNull(request.getCarrierLetter()));
		assignment.setLdsSealNumbers(StringUtils.stripToNull(request.getSeals()));
		
		if (request.getDriver() != null) {
			DriverData driverData = request.getDriver();
			assignment.setLdsDriverName(StringUtils.stripToNull(driverData.getDriverName()));
			assignment.setLdsDriverCardNo(StringUtils.stripToNull(driverData.getIdCardNumber()));
			assignment.setLdsDriverId(StringUtils.stripToNull(driverData.getPersonalUniqueNumber()));
		}
		if (request.getAttorney() != null) {
			AttorneyData attorney = request.getAttorney();
			assignment.setLdsAttorneyName(StringUtils.stripToNull(attorney.getName()));
			assignment.setLdsAttorneyStart(attorney.getStart());
		}
		if (request.getDispatcher() != null) {
			DispatcherData dispatcherData = request.getDispatcher();
			assignment.setLdsDispatcherName(StringUtils.stripToNull(dispatcherData.getName()));
			assignment.setLdsDispatcherTel(StringUtils.stripToNull(dispatcherData.getContact()));
			assignment.setLdsDispatcherId(StringUtils.stripToNull(dispatcherData.getIdNumber()));
		}
	}
}
