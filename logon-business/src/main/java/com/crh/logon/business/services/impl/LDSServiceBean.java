/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.JPAQuery;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.gdi.jpa.filter.SelectExpression;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.common.generics.BusinessPartner;
import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.business.services.beans.UomConversionResult;
import com.crh.logon.business.services.dispatchers.LDSServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.BestellungServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DispokarteServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LDSServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.MaterialEnhancementsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderDetailServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.PoolAdminServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TruckAdminServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UomConverterServiceLocalInterface;
import com.crh.logon.business.services.utils.AssignmentUtils;
import com.crh.logon.business.services.utils.BusinessPartnerUtil;
import com.crh.logon.business.services.utils.InvoiceBean;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.OrderUtils;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.authorization.Authorizer;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.MatPlantEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.SimpleVehicleEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.SapVehicleTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.ShipppointTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.TruckTransportCatBsEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.WeighSystemSpBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialDescriptionEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.sap.SapVehicleEntity;
import com.crh.logon.persistence.entities.sap.SapVendorEntity;
import com.crh.logon.persistence.entities.views.RepTransplusEntity;
import com.crh.logon.persistence.entities.views.SoldshipSareaFlexSrchEntity;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonConversionException;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.webservices.clients.LogonToLDS;
import com.crh.logon.webservices.clients.beans.logon_lds_interface.DeleteLoadOrderDTO;
import com.crh.logon.webservices.clients.beans.logon_lds_interface.LoadOrderNotificationDTO;
import com.crh.logon.webservices.clients.beans.logon_lds_interface.LoadOrderNotificationDTO.MessageFunction;
import com.crh.logon.webservices.clients.beans.logon_lds_interface.PdaLoginDTO;
import com.holcim.logon.eventbeans.in.logon_lds_interface.BusinessPartnerData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.CreatedOrder;
import com.holcim.logon.eventbeans.in.logon_lds_interface.CreatedOrderData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.LoadOrderData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.LoadOrderItemData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.LocationData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.MaterialData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.OrderData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.OrderItemData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.PriceData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.ReducedLoadOrderData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.ReducedLoadOrderItemData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.ReducedOrderData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.ReducedOrderItemData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.SchedulingData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.ServiceMaterialData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.ShiptoMaterialData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.VehicleData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.VendorData;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class LDSServiceBean extends LDSServiceDispatcher implements LDSServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LDSServiceBean.class);
	
	@EJB private BestellungServiceLocalInterface bestellungService;
	@EJB private MaterialEnhancementsServiceLocalInterface materialEnhancementsService;
	@EJB private OrderDetailServiceLocalInterface orderDetailService;
	@EJB private OrderServiceLocalInterface orderService;
	@EJB private UomConverterServiceLocalInterface unitOfMeasurementConverterService;
	@EJB private DispokarteServiceLocalInterface dispokarteService;
	@EJB private TruckAdminServiceLocalInterface truckAdminService;
	@EJB private PoolAdminServiceLocalInterface poolAdminService;


	
	public void sendPdaLogin(AssignmentEntity assignment, ShippingpointEntity sp, LoadingpointEntity lp) {
		
		String configInfix = sp.getWeighSystemSp().getConfigTableInfix();
		if (configInfix == null) {
			
			throw new LogonException("Cannot send to weighing system with weigh_system_sp_cd '" + sp.getWeighSystemSp().getWeighSystemSpCd() + "'", Reason.RES_ERROR_UNSPECIFIED);
		}
		PdaLoginDTO pdaLogin = createPdaLoginDTO(assignment, sp, lp);
		try {
			ConfigBean configBean = getConfigBean();
			LogonToLDS logonToLDS = new LogonToLDS(configBean);
			logonToLDS.sendPdaLogin(pdaLogin, configInfix, sp.getLdsServer());
		}
		catch (LogonException e) {
			LOG.error("pdaLogin: Error while sending to weighing system (assignmentId: " +  assignment.getAssignmentId() + ")");
		}
	}
	
	public void sendLoadOrderNotification(AssignmentEntity assignment, ShippingpointEntity sp) {
		WeighSystemSpBsEntity weighSystemSp = sp.getWeighSystemSp();
		if (!weighSystemSp.getPushModeOn()) {
			return;
		}
		
		// tours are currently not supported, except for simple vehicles
		final boolean isTour = AssignmentUtils.isTour(assignment);
		ShipppointTypeBsEntity shipppointType = sp.getShipppointType();
		Long shipppointTypeCd = shipppointType.getShipppointTypeCd();
		if (isTour 
				// tours allowed in case of ship & block train
				&& (shipppointTypeCd.longValue() != Constant.SHIPPINGPOINT_TYPE_SIMPLE_VEHICLE)
				// tours allowed in case the weighing system generally allows it
				&& (weighSystemSp.getEnableTours().equals(Boolean.FALSE))) {
			return;
		}
		
		String configInfix = weighSystemSp.getConfigTableInfix();
		if (configInfix == null) {
			Long weighSystemSpCd = weighSystemSp.getWeighSystemSpCd();
			String message = "Cannot send to weighing system with weigh_system_sp_cd '" + weighSystemSpCd + "'";
			throw new LogonException(message, Reason.RES_ERROR_UNSPECIFIED);
		}

		MessageFunction msgfn = MessageFunction.UPDATE;
		if (assignment.getLoadOrderId() == null) {
			msgfn = MessageFunction.NEW;
			Long fetchSequenceNextVal = fetchSequenceNextVal("SLGNC_LOAD_ORDER");
			assignment.setLoadOrderId(fetchSequenceNextVal);
			List<LoadEntity> loadsFromAssignment = AssignmentUtils.getLoadsFromAssignment(assignment);
			for (LoadEntity load : loadsFromAssignment) {
				if (load.getLoadOrderItemId() == null) {
					fetchSequenceNextVal = fetchSequenceNextVal("SLGNC_LOAD_ORDER_ITEM");
					load.setLoadOrderItemId(fetchSequenceNextVal);
				}
			}
		}
		
		final LoadOrderNotificationDTO loadOrderNotification = createLoadOrderNotificationDTO(assignment, msgfn);
		try {
			String ldsServer = sp.getLdsServer();
			ConfigBean configBean = getConfigBean();
			LogonToLDS logonToLDS = new LogonToLDS(configBean);
			logonToLDS.sendLoadOrderNotification(loadOrderNotification, configInfix, ldsServer);
		}
		catch (LogonException e) {
			Long assignmentId = assignment.getAssignmentId();
			String message = "loadOrderNotification: Error while sending to weighing system (assignmentId: " + assignmentId + ")";
			LOG.error(message);
		}
	}
	
	public void sendDeleteLoadOrder(AssignmentEntity assignment, ShippingpointEntity sp) {
		
		// if the loadOrderId is null, no need to delete, since it has never been sent to the lds (or already deleted)
		if (assignment.getLoadOrderId() == null) {
			return;
		}
		
		if (!sp.getWeighSystemSp().getPushModeOn()) {
			// see LOGON-6329
			assignment.setLoadOrderId(null);
			for (LoadEntity load : AssignmentUtils.getLoadsFromAssignment(assignment)) {
				load.setLoadOrderItemId(null);
			}
			
			return;
		}
		
		String configInfix = sp.getWeighSystemSp().getConfigTableInfix();
		if (configInfix == null) {
			
			throw new LogonException("Cannot send to weighing system with weigh_system_sp_cd '" + sp.getWeighSystemSp().getWeighSystemSpCd() + "'", Reason.RES_ERROR_UNSPECIFIED);
		}
		DeleteLoadOrderDTO deleteLoadOrder = createDeleteLoadOrderDTO(assignment);
		try {
			assignment.setLoadOrderId(null);
			for (LoadEntity load : AssignmentUtils.getLoadsFromAssignment(assignment)) {
				load.setLoadOrderItemId(null);
			}
			ConfigBean configBean = getConfigBean();
			LogonToLDS logonToLDS = new LogonToLDS(configBean);
			logonToLDS.sendDeleteLoadOrder(deleteLoadOrder, configInfix, sp.getLdsServer());
		}
		catch (LogonException e) {
			LOG.error("deleteLoadorder: Error while sending to weighing system (assignmentId: " + assignment.getAssignmentId() + ")");
		}
	}
	
	public PdaLoginDTO createPdaLoginDTO(AssignmentEntity assignment, ShippingpointEntity sp, LoadingpointEntity lp) {
		
		PdaLoginDTO request = new PdaLoginDTO();

		List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);

		// retrieve vehicle data from first order / first load
		OrderEntity firstOrder = orders.get(0);
		request.setLocationData(createLocationData(sp, lp));
		request.setVehicleData(createVehicleData(firstOrder, assignment));
		
		return request;
	}
	
	private LoadOrderNotificationDTO createLoadOrderNotificationDTO(AssignmentEntity assignment, MessageFunction msgfn) {
		
		LoadOrderNotificationDTO request = new LoadOrderNotificationDTO();
		request.setMessageFunction(msgfn);
		request.setLoadOrder(createLoadOrderData(assignment));
		
		// LOGON-9049 - 2015.01.07
		// have to trim custorderCustrefNo to 15 length as the LDS systems are still expecting that instead of the newly modified 35 char length.
		if (request.getLoadOrder() != null) {
			for (OrderData order : request.getLoadOrder().getOrder()) {
				if (order.getCustorderCustrefNo() != null
						&& order.getCustorderCustrefNo().length() > 15) {
					order.setCustorderCustrefNo(order.getCustorderCustrefNo().substring(0, 15));
				}
			}
		}
		
		return request;
	}
	
	// used for testing
	public LoadOrderNotificationDTO createLoadOrderNotificationDTO(AssignmentEntity assignment, MessageFunction msgfn, Long loadOrderId) {
		
		LoadOrderNotificationDTO request = createLoadOrderNotificationDTO(assignment, msgfn);
		
		if (msgfn.equals(MessageFunction.NEW)) {
			request.getLoadOrder().setLoadOrderId(loadOrderId.toString());
			for (LoadOrderItemData item : request.getLoadOrder().getLoadOrderItem()) {
				item.setLoadOrderItemId(loadOrderId.toString());
			}
			for (OrderData order : request.getLoadOrder().getOrder()) {
				for (OrderItemData item : order.getOrderItem()) {
					item.setLoadId(loadOrderId.toString());
				}
			}
		}
		
		return request;
	}
	
	public DeleteLoadOrderDTO createDeleteLoadOrderDTO(AssignmentEntity assignment) {
		
		DeleteLoadOrderDTO request = new DeleteLoadOrderDTO();
		
		request.setLoadOrderId(assignment.getLoadOrderId().toString());
		
		return request;
	}

	public ReducedLoadOrderData createReducedLoadOrderData(AssignmentEntity assignment) {
		ReducedLoadOrderData loadOrder = new ReducedLoadOrderData();
		
		if (assignment.getLoadOrderId() != null) {
			loadOrder.setLoadOrderId(assignment.getLoadOrderId().toString());
		}
		
		List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);
		List<LoadEntity> loads = AssignmentUtils.getLoadsFromAssignment(assignment);
		
		List<ReducedLoadOrderItemData> loadOrderItems = loadOrder.getLoadOrderItem();
		for (LoadEntity load : loads) {
			loadOrderItems.add(createReducedLoadOrderItemData(load));
		}
		
		for (OrderEntity order : orders) {
			loadOrder.getOrder().add(createReducedOrderData(order));
		}
		
		return loadOrder;		
	}

	public CreatedOrderData createOrderData(AssignmentEntity assignment, Locale locale) {
		CreatedOrderData createdOrderData = new CreatedOrderData();

		List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);

		for (OrderEntity order : orders) {
			createdOrderData.getOrder().add(createOrder(order, locale));
		}

		return createdOrderData;
	}
	
	public LoadOrderData createLoadOrderData(AssignmentEntity assignment) {
		
		LoadOrderData loadOrder = new LoadOrderData();
		
		// coming from the test tab, the load order id might be null (and will be set afterwards)
		if (assignment.getLoadOrderId() != null) {
			loadOrder.setLoadOrderId(assignment.getLoadOrderId().toString());
		}
		
		final List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);
		final List<LoadEntity> loads = AssignmentUtils.getLoadsFromAssignment(assignment);
		
		// retrieve global information from first order / first load
		final OrderEntity firstOrder = orders.get(0);
		final LoadEntity firstLoad = loads.get(0);
		
		loadOrder.setSupplyPlantNo(firstOrder.getShippingpoint().getPlant().getPlantCd());
		loadOrder.setShippingpointNo(firstOrder.getShippingpoint().getSapShippingpointNo());
		
		final List<LoadOrderItemData> loadOrderItems = loadOrder.getLoadOrderItem();
		for (LoadEntity load : loads) {
			loadOrderItems.add(createLoadOrderItemData(load));
		}
		
		loadOrder.setScheduling(createSchedulingData(firstLoad));
		VehicleData createdVehicleData = createVehicleData(firstOrder, assignment);
		loadOrder.setVehicle(createdVehicleData);
		
		for (OrderEntity order : orders) {
			loadOrder.getOrder().add(createOrderData(order));
		}
		
		StringBuilder sb = new StringBuilder();
		for (OrderEntity order : orders) {
			if (order.getNoteRailwayDisp() != null) {
				sb.append(order.getNoteRailwayDisp());
				sb.append(";");
			}
		}
		loadOrder.setNoteLds(sb.toString());
		
		return loadOrder;
		
	}
	
	private static boolean isExactDeliveryQuantity(OrderItemEntity itemEntity) {
		if (LogonPackageHandlingUtil.isBag(itemEntity)) {
			return true;
		}
		else if (LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(itemEntity)) {
			return !itemEntity.getOrderedUom().getFuzzyQuantityFlag();
		}
		else {
			return true;
		}
	}
	
	private LoadOrderItemData createLoadOrderItemData(LoadEntity load) {
		
		final LoadOrderItemData loadOrderItem = new LoadOrderItemData();
		
		final Collection<OrderItemEntity> orderItems = orderService.getOrderItems(load);
		if (orderItems == null || orderItems.isEmpty()) {
			throw new LogonException("Inconsistent state: load must be referenced by an order item at this point",
					Reason.RES_IMPL_BUG);
		}
		final OrderItemEntity orderItem = orderItems.iterator().next();
		
		if (load.getLoadOrderItemId() != null) {
			loadOrderItem.setLoadOrderItemId(load.getLoadOrderItemId().toString());
		}
		loadOrderItem.setMaterial(createMaterialData(load));
		if (load.getLoadingpoint() != null) {
			loadOrderItem.setLoadingpointNo(load.getLoadingpoint().getWeighSystemLoadingpointNo());
		}
		loadOrderItem.setExactDeliveryQuantity(isExactDeliveryQuantity(orderItem));
		loadOrderItem.setChamberNo(getAsBigDecimal(load.getChamberNumber()));
		
		return loadOrderItem;
	}

	private ReducedLoadOrderItemData createReducedLoadOrderItemData(LoadEntity load) {
		
		ReducedLoadOrderItemData loadOrderItem = new ReducedLoadOrderItemData();
		
		Collection<OrderItemEntity> orderItems = orderService.getOrderItems(load);
		if (orderItems == null || orderItems.isEmpty()) {
			throw new LogonException("Inconsistent state: load must be referenced by an order item at this point",
					Reason.RES_IMPL_BUG);
		}
		
		if (load.getLoadOrderItemId() != null) {
			loadOrderItem.setLoadOrderItemId(load.getLoadOrderItemId().toString());
		}
		
		return loadOrderItem;
	}
	
	private SchedulingData createSchedulingData(LoadEntity firstLoad) {
		
		SchedulingData scheduling = new SchedulingData();
		
		// FIXME 08-Aug-2012/stich: change if required
		scheduling.setReleasedForLoading(Boolean.TRUE);
		AssignmentPartEntity firstLoadAp = orderService.getAssignmentPartLoad(firstLoad);
		if (firstLoadAp != null) {
			scheduling.setPlannedLoadingTime(firstLoadAp.getPlannedStart());
		}
		
		return scheduling;
	}
	
	public LocationData createLocationData(ShippingpointEntity sp, LoadingpointEntity lp) {
		
		LocationData location = new LocationData();
		
		location.setSupplyPlantNo(sp.getPlant().getPlantCd());
		location.setShippingpointNo(sp.getSapShippingpointNo());
		if (lp != null) {
			location.setLoadingpointNo(lp.getWeighSystemLoadingpointNo());
			location.setLoadingpointName(lp.getLoadingpointName());
		}
		
		return location;
	}


	private VehicleData createVehicleData(OrderEntity order, AssignmentEntity assignment)
	{
		final VehicleData vehicle = new VehicleData();
		
		// No vehicle data for pickup orders
		DeliveryTypeBsEntity deliveryType = order.getDeliveryType();
		Long deliveryTypeCd = deliveryType.getDeliveryTypeCd();
		if (LogonUtil.isPickupOrder(deliveryTypeCd)) {
			return vehicle;
		}
		
		CarrierEntity commCarrier = order.getCommCarrier();
		if (commCarrier != null) {
			Long carrierId = commCarrier.getCarrierId();
			if (!carrierId.equals(Constant.ARTIFICIAL_GENERIC_CARRIER_ID)) {
				SapVendorEntity sapVendor = commCarrier.getSapVendor();
				VendorData createVendorData = createVendorData(sapVendor);
				vehicle.setCommercialCarrier(createVendorData);
			}
		}
		CarrierEntity execCarrier = order.getExecCarrier();
		if (execCarrier != null) {
			SapVendorEntity sapVendor = execCarrier.getSapVendor();
			VendorData createVendorData = createVendorData(sapVendor);
			vehicle.setExecutingCarrier(createVendorData);
		}
		
		final TruckEntity truck = assignment.getTruck();
		final SimpleVehicleEntity simpleVehicle = assignment.getSimpleVehicle();
		
		if (truck != null) {
			Long truckId = truck.getTruckId();
			TrailerEntity trailer = truckAdminService.fetchTrailerForTruck(truckId, true);
			if (LogonUtil.isPlanningTruck(truck)) {
				String sapVehicleType = truck.getTruckType().getTruckTypeShortName();
				vehicle.setVehicleType(sapVehicleType);
			}
			else {
				SapVehicleTypeBsEntity sapVehicleType = truck.getSapVehicleType();
				if (sapVehicleType != null) {
					String sapVehicleTypeCd = sapVehicleType.getSapVehicleTypeCd();
					vehicle.setVehicleType(sapVehicleTypeCd);
				}
				vehicle.setTruckId(truckId.toString());
				String truckPlateNo = truck.getTruckPlateNo();
				vehicle.setTruckPlateNo(truckPlateNo);
				String sapVehicleNo = truck.getSapVehicleNo();
				if (sapVehicleNo != null) {
					SapVehicleEntity sapVehicle = getSapVehicle(sapVehicleNo);
					if (sapVehicle != null) {
						String sapVehicleIfcNo = sapVehicle.getSapVehicleIfcNo();
						vehicle.setSapTruckNo(sapVehicleIfcNo);
					}
				}
			}
			// FIXME 02-Jul-2012/stich: allowedVehicleType (else clause)
			
			BigDecimal tareWeight = truck.getTareWeightT();
			
			if (trailer != null) {
				if (!LogonUtil.isPlanningTrailer(trailer)) {
					Long trailerId = trailer.getTrailerId();
					String stringTrailerId = trailerId.toString();
					vehicle.setTrailerId(stringTrailerId);
					String trailerPlateNo = trailer.getTrailerPlateNo();
					vehicle.setTrailerPlateNo(trailerPlateNo);
					String sapVehicleNo = trailer.getSapVehicleNo();
					if (sapVehicleNo != null) {
						SapVehicleEntity sapVehicle = getSapVehicle(sapVehicleNo);
						if (sapVehicle != null) {
							String sapVehicleIfcNo = sapVehicle.getSapVehicleIfcNo();
							vehicle.setSapTrailerNo(sapVehicleIfcNo);
						}
					}
					BigDecimal tareWeightT = trailer.getTareWeightT();
					tareWeight = tareWeight.add(tareWeightT);
				}
			
				vehicle.setVehicleCompTareWeight(tareWeight);
				TruckTransportCatBsEntity truckTransportCat = truck.getTruckTransportCat();
				Long truckTransportCatCd = truckTransportCat.getTruckTransportCatCd();
				if (truckTransportCatCd.equals(Constant.TRUCK_TRSP_CAT_TRAILER)) {
					BigDecimal maxGrossWeightT = trailer.getMaxGrossWeightT();
					vehicle.setVehicleMaxGrossWeight(maxGrossWeightT);
				}
			}
			else {
				BigDecimal maxGrossWeightT = truck.getMaxGrossWeightT();
				vehicle.setVehicleMaxGrossWeight(maxGrossWeightT);
			}
		}
		else if (simpleVehicle != null)
		{
			String vehicleRegNo = simpleVehicle.getVehicleRegNo();
			vehicle.setTruckPlateNo(vehicleRegNo);
			String sapVehicleNo = simpleVehicle.getSapVehicleNo();
			vehicle.setSapTruckNo(sapVehicleNo);
			String sapVehicleTypeCd = simpleVehicle.getSapVehicleTypeCd();
			vehicle.setVehicleType(sapVehicleTypeCd);
			
			BigDecimal tareWeightT = simpleVehicle.getTareWeightT();
			vehicle.setVehicleCompTareWeight(tareWeightT);
			BigDecimal maxGrossWeightT = simpleVehicle.getMaxGrossWeightT();
			vehicle.setVehicleMaxGrossWeight(maxGrossWeightT);
		}
		
		return vehicle;
	}


	private static VendorData createVendorData(SapVendorEntity sapVendor) {
		
		VendorData vendor = new VendorData();
		
		vendor.setSapVendorId(sapVendor.getSapIfcId());
		vendor.setName1(sapVendor.getName1());
		vendor.setName2(sapVendor.getName2());
		vendor.setStreet(sapVendor.getStreet() + (sapVendor.getHouseNumber() != null ? (" " + sapVendor.getHouseNumber()) : ""));
		vendor.setZipCode(sapVendor.getPostalCode());
		vendor.setCity(sapVendor.getCity());
		if (sapVendor.getCountry() != null) {
			vendor.setCountry(sapVendor.getCountry().getSapCountryCd());
		}
		vendor.setPhoneNumber(sapVendor.getPhoneNumber());
		vendor.setTaxNumber(sapVendor.getTaxNumber3());
		
		return vendor;
	}
	
	public OrderData createOrderData(OrderEntity oe) {
		
		final OrderData order = new OrderData();
		
		/*
		 * Customer order related information
		 */
		final CustorderEntity custorder = oe.getCustorder();
		if (custorder != null) {
			
			Long custorderNumber = custorder.getCustorderNumber();
			if (custorderNumber != null) {
				order.setLogonCustomerOrderNo(custorderNumber.toString());
			}
			
			order.setLdsCustomerOrderNo(custorder.getLdsCustorderNo());
			order.setCustorderCustrefNo(custorder.getCustorderCustrefNo());
			order.setPrintCollectiveDeliveryNote(custorder.getDelivnoteCollPaperFlag());
		}
		
		/*
		 * (Single delivery) order related information
		 */
		final Long orderNumber = oe.getOrderNumber();
		if (orderNumber != null) {
			order.setLogonOrderNo(orderNumber.toString());
		}

		order.setSequentialLogonOrderNo(oe.getOrderNumberFromSeqUsage());
		order.setContractType(oe.getSapContract().getSapContractType().getSapContractTypeCd());
		order.setContractNumber(oe.getSapContract().getSapContractNo());
		order.setIncoterms(oe.getSapContract().getIncotermsCd());

		
		/*
		 * Vendors
		 */
		if (oe.getCommCarrier() != null && !oe.getCommCarrier().getCarrierId().equals(Constant.ARTIFICIAL_GENERIC_CARRIER_ID)) {
			order.setCommercialCarrier(createVendorData(oe.getCommCarrier().getSapVendor()));
		}
		if (oe.getExecCarrier() != null) {
			order.setExecutingCarrier(createVendorData(oe.getExecCarrier().getSapVendor()));
		}
		
		/*
		 * Business partners
		 */
		BusinessPartnerData soldToPartnerData = createBusinessPartnerData(BusinessPartnerUtil.getSoldtoBusinessPartner(oe));
		BusinessPartnerData shipToPartnerData = createBusinessPartnerData(BusinessPartnerUtil.getShiptoBusinessPartner(oe));
		
		order.setSoldTo(soldToPartnerData);
		order.setShipTo(shipToPartnerData);
		
		if (oe.getBilltoSapBp() != null) {
			BusinessPartnerData billToPartnerData = createBusinessPartnerData(BusinessPartnerUtil.getBilltoBusinessPartner(oe));
			order.setBillTo(billToPartnerData);
		}
		if (oe.getPayerSapBp() != null) {
			BusinessPartnerData payerPartnerData = createBusinessPartnerData(BusinessPartnerUtil.getPayerBusinessPartner(oe));
			order.setPayer(payerPartnerData);
		}
		
		order.setSupplyPlantNo(oe.getShippingpoint().getPlant().getPlantCd());
		order.setShippingpointNo(oe.getShippingpoint().getSapShippingpointNo());

		if (oe.getDeliveryType().getDeliveryTypeCd().equals(Constant.DELIVERY_TYPE_PICKUP)) {
			order.setDeliveryType(Constant.INCOTERMS_EXW);
		}
		else {
			order.setDeliveryType(Constant.INCOTERMS_DDP);
		}

		order.setNoteDelivery(oe.getNoteDelivery());
		order.setNoteScheduling(oe.getNoteDispatching());
		order.setDriverComment(oe.getNoteTruckDriver());
		
		// autoScheduleRelease: N/A for outgoing interface

		order.setEarliestDeliveryTime(oe.getDeliveryFromDat());
		order.setLatestDeliveryTime(oe.getDeliveryToDat());

		if (oe.getOrderId() != null) {
			AssignmentPartEntity unloadAssignmentPart = orderService.getAssignmentPartUnload(
					oe);
			//can happen that a material had been added before creating an assignment, and the orderdata has to be resent anyway
			if ( unloadAssignmentPart != null ) {				
				order.setPlannedDeliveryTime(unloadAssignmentPart.getPlannedStart());
			}
		}
		
		// deliveryFreqDelayBtwnDeliveries, deliveryFreqDelayBtwnDeliveriesUom, deliveryFreqQtyPerHour, deliveryFreqQtyPerHourUom, deliveryTimeTolerance: N/A for outgoing interface

		// FIXME 08-Aug-2012/stich: more conditions: print at all, loading point based as usual ...
		order.setPrintDeliveryNoteLds(BooleanUtils.negate(oe.getDelivnoteSinglePaperFlag()));
		
        // TODO (pascal Nov 10, 2012): Remove after Release 2.1.0.6 (Nov 26)
		// LOGON-5630: DeliveryNoteCollFlag is no longer saved on indiviadual Order but on CustOrder
		//order.setPrintCollectiveDeliveryNote(oe.getDelivnoteCollPaperFlag());
		
		order.setChromReduce(oe.getShiptoSapBp().getChromatReductionMonths());
		order.setOrderValidationCode(oe.getOrderValidationCode());
		
		final List<OrderItemEntity> sortedOrderItems = OrderUtils.getSortedOrderItems(oe);
		final List<PriceData> prices = new ArrayList<PriceData>();
		for (OrderItemEntity oie : sortedOrderItems) {
			if (oie.getSapMaterial().getMaterialPackage().getMaterialPackageCd().equals(Constant.MATERIAL_PACKAGE_SERVICE)) {
				final ServiceMaterialData serviceMaterial = createServiceMaterialData(oie);
				order.getServiceMaterial().add(serviceMaterial);
				if (serviceMaterial.getPriceData() != null) {
					prices.add(serviceMaterial.getPriceData());
				}
			}
			else {
				final OrderItemData orderItem = createOrderItemData(oie);
				order.getOrderItem().add(orderItem);
				if (orderItem.getPriceData() != null) {
					prices.add(orderItem.getPriceData());
				}
			}
		}
		
		// exactCustomerOrderQuantity: N/A for outgoing interface
		order.setExactDeliveryQuantity(isExactDeliveryQuantity(sortedOrderItems.get(0)));
		
		if (isCashSales(oe)) {
			String currency = null;
			BigDecimal netPrice = BigDecimal.ZERO;
			BigDecimal vatValue = BigDecimal.ZERO;
			BigDecimal grossPrice = BigDecimal.ZERO;
			for (PriceData pd : prices) {
				if (pd != null) {
					currency = pd.getCurrency();
					netPrice = netPrice.add(pd.getNetPrice());
					vatValue = vatValue.add(pd.getVatValue());
					grossPrice = grossPrice.add(pd.getGrossPrice());
				}
			}
			
			final PriceData priceData = new PriceData();
			
			priceData.setCurrency(currency);
			priceData.setNetPrice(netPrice);
			priceData.setVatValue(vatValue);
			priceData.setGrossPrice(grossPrice);
			
			order.setOrderPriceData(priceData);
		}
		
		return order;
	}

	private ReducedOrderData createReducedOrderData(OrderEntity oe) {

		ReducedOrderData order = new ReducedOrderData();

		Long orderNumber = oe.getOrderNumber();
		if (orderNumber != null) {
			order.setLogonOrderNo(orderNumber.toString());
		}

		List<OrderItemEntity> sortedOrderItems = OrderUtils.getSortedOrderItems(oe);
		for (OrderItemEntity oie : sortedOrderItems) {
			if (oie.getSapMaterial() == null || 
					oie.getSapMaterial().getMaterialPackage() == null || 
					!oie.getSapMaterial().getMaterialPackage().getMaterialPackageCd().equals(Constant.MATERIAL_PACKAGE_SERVICE)) {
				ReducedOrderItemData orderItem = createReducedOrderItemData(oie);
				order.getOrderItem().add(orderItem);
			}
		}
		
		order.setOrderValidationCode(oe.getOrderValidationCode());
		
		return order;
	}

	public CreatedOrder createOrder(OrderEntity oe, Locale locale) {


		CreatedOrder createdOrder = new CreatedOrder();
		if (oe.getOrderNumber() != null) {
			createdOrder.setLogonOrderNo(oe.getOrderNumber().toString());
		}
		if (oe.getCommCarrier() != null) {
			createdOrder.setCommercialCarrier(createVendorData(oe.getCommCarrier().getSapVendor()));
		}
		if (oe.getExecCarrier() != null) {
			createdOrder.setExecutingCarrier(createVendorData(oe.getExecCarrier().getSapVendor()));
		}
		if (oe.getPool() != null) {
			PoolTxEntity poolTx = poolAdminService.fetchPoolTxEntity(oe.getPool().getPoolId(), locale);
			createdOrder.setPoolName(poolTx.getName());
		}

		createdOrder.setOrderStatusCd(String.valueOf(oe.getOrderStatus().getOrderStatusCd()));

		return createdOrder;
	}

	private static BusinessPartnerData createBusinessPartnerData(BusinessPartner bpData) {
		
		BusinessPartnerData businessPartner = new BusinessPartnerData();
		
		businessPartner.setSapBpId(bpData.getSapIfcId());
		businessPartner.setIsOneTime(bpData.getTreatAsOnetimeFlag());

		businessPartner.setName1(bpData.getFirstName());
		businessPartner.setName2(bpData.getLastName());
		businessPartner.setStreet(bpData.getStreetHouseNumber());
		businessPartner.setZipCode(bpData.getPostalCode());
		businessPartner.setCity(bpData.getCity());
		businessPartner.setCountry(bpData.getSapCountryCd());
		businessPartner.setVatRegistrationNumber(bpData.getVatRegistrationNumber());
		businessPartner.setPhoneNumber(bpData.getPhoneNumber());
		
		return businessPartner;
	}
	
	private MaterialData createMaterialData(LoadEntity load) {
		final MaterialData material = new MaterialData();
		if (load == null) {
			return material;
		}
		final SapMaterialEntity me = load.getSapMaterial();
		
		material.setMaterialNo(me.getSapMaterialIfcNo());

		//get MatPlantEntity that stores the information if autoAddMaterial exists for the plant+material combo. 
		final PlantBsEntity productionPlant = me.getProdPlant();		
		if (productionPlant != null) {
			final MatPlantEntity matPlant = bestellungService.fetchMatPlantEntity(
					productionPlant.getPlantCd(), 
					me.getSapMaterialId());
			if (matPlant != null) {
				material.setBrandSalesName(matPlant.getBrandSalesName());
			}
		}

		// FIXME 02-Jul-2012/stich: sapMaterialDescription
	/*
	 * there can be multiple shiptos to a single load (thus I can not use this approach), so....
	 * from where shall I get that language?
	 * 
		final String sapMaterialDescription = getDispokarteService().fetchSapMaterialDescription(
				me.getSapMaterialId(), 
				language);				
		material.setSapMaterialDescription(sapMaterialDescription);
	*/		
		
		BigDecimal quantity = BigDecimal.ZERO;
		UomBsEntity orderedUom = null;
		material.setProdPlantNo(load.getProductionPlant().getPlantCd());
		if (load.getMixDesign() != null) {
			material.setMixDesignID(load.getMixDesign().getMixDesignCd());
		}

		final Collection<OrderItemEntity> orderItems = orderService.getOrderItems(load);
		if ((orderItems != null) && (!orderItems.isEmpty())) {
			for (OrderItemEntity oie : orderItems) {
				if (orderedUom == null) {
					orderedUom = oie.getOrderedUom();
				}
				else if (!StringUtils.equals(orderedUom.getUomCd(), oie.getOrderedUom().getUomCd())) {
					throw new LogonException("Load " + oie.getLoad().getLoadId() + " is shared by order items of different UoMs", Reason.RES_ERROR_IMPL);
				}
				quantity = quantity.add(oie.getOrderedQuantity());
			}
		}

		material.setQuantity(quantity);
		material.setQuantityUom(getUomToSend(orderedUom));
		material.setBaseUom(getUomToSend(me.getBaseUom()));
		
		final UomConversionResult ucr;
		try {
			TruckTypeBsEntity truckType = null;
			TruckEntity truck = load.getTruck();
			if (truck != null) {
				truckType = truck.getTruckType();
			}
			ucr = unitOfMeasurementConverterService.convert(quantity, orderedUom, me.getBaseUom(), me, truckType, null, true);
		}
		catch (LogonConversionException e)
		{
			final String baseUomCd = me.getBaseUom().getUomCd();
			final String baseMatNo = me.getSapMaterialNo();
			final String orderedUomCd = orderedUom != null ? orderedUom.getUomCd() : "n/a";
			final String msg = "Could not convert ordered UOM " + orderedUomCd + " to base UOM " +
			baseUomCd + " of SAP material " + baseMatNo + ". (" + e.getMessage() + ")";
			
			LOG.error(msg);
			throw new LogonException(msg, e);
		}
		material.setNumerator(BigDecimal.valueOf(ucr.getNumerator()));
		material.setDenominator(BigDecimal.valueOf(ucr.getDenominator()));
		
		return material;
	}
	
	private MaterialData createMaterialData(OrderItemEntity oie) {
		
		final MaterialData material = new MaterialData();
		final SapMaterialEntity me = oie.getSapMaterial();

		final SapContractItemEntity contractItem = getGroupMemberForContractItem(oie.getSapContractItem());

		material.setMaterialNo(me.getSapMaterialIfcNo());
		//services can be added without a contract number
		material.setContractItemNumber(contractItem != null ? contractItem.getContractItemNo().toString() : null);

		//service materials might not have production plant
		final PlantBsEntity productionPlant = oie.getProductionPlant();		
		if (productionPlant != null) {
			final MatPlantEntity matPlant = bestellungService.fetchMatPlantEntity(
					productionPlant.getPlantCd(), 
					me.getSapMaterialId());
			if (matPlant != null) {
				material.setBrandSalesName(matPlant.getBrandSalesName());
			}
		}

		final SapMaterialDescriptionEntity sapMaterialDescription = dispokarteService.fetchSapMaterialDescription(
				me.getSapMaterialId(), 
				oie.getOrder().getShiptoSapBp().getLang().getLangCd());
		if (sapMaterialDescription != null) {
			material.setSapMaterialDescription(sapMaterialDescription.getTxt());
		}

		if (oie.getMixDesign() != null) {
			material.setMixDesignID(oie.getMixDesign().getMixDesignCd());
		}
		material.setQuantity(oie.getOrderedQuantity());
		material.setQuantityUom(getUomToSend(oie.getOrderedUom()));
		material.setBaseUom(getUomToSend(me.getBaseUom()));
		material.setNumerator(getAsBigDecimal(oie.getOrderedUomNumerator()));
		material.setDenominator(getAsBigDecimal(oie.getOrderedUomDenominator()));
		
		return material;
	}

	private SapContractItemEntity getGroupMemberForContractItem(SapContractItemEntity item) {

		if (item == null || item.getSapContract() == null) {
			return item;
		}
		
		SapContractEntity contract = item.getSapContract();
		boolean quantityControlledContract = LogonUtil.isQuantityControlledContract(contract);
		if (!quantityControlledContract) {
			// we are not dealing with a quantity-restricted contract
			// this is a valid case
			return item;
		}

		if (!item.getIsApGroupLeader()) {
			LOG.error(String.format("Contract item (id: %d) belongs to a quantity-controlled contract (id: %d) but itself is not a group leader", 
					item.getSapContractItemId(), item.getSapContract().getSapContractId()));
			LOG.error(String.format("Using current contract item %d with contract-no %d", 
					item.getSapContractItemId(), item.getSapContract().getSapContractId()));
			return item;
		}

		ReadWriteDao<SapContractItemEntity> dao = getDaoProvider().getSapContractItemEntityDao();

		Filter<SapContractItemEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(SapContractItemEntity.ATTR_AP_GROUP_LEADER_ID, item.getSapContractItemId()));
		filter.addExpression(Order.desc(SapContractItemEntity.ATTR_CTL_CRE_TS));

		List<SapContractItemEntity> items = dao.findNRecs(0, 1, filter);

		if (items.isEmpty()) {
			LOG.error("No group members found for AP group leader: " + item.getSapContractItemId());
			LOG.error("Using current contract item " + item.getSapContractItemId() + " with contract-no " + item.getContractItemNo());
			throw new LogonException(String.format("Cannot find any Group member for Group leader with sap contract item id : %d", item.getSapContractItemId()), Reason.RES_ERROR_IMPL);
		}

		return items.get(0);

	}

	private ServiceMaterialData createServiceMaterialData(OrderItemEntity oie) {
		
		ServiceMaterialData serviceMaterial = new ServiceMaterialData();
		
		serviceMaterial.setMaterial(createMaterialData(oie));

		if (isCashSales(oie.getOrder())) {
			serviceMaterial.setPriceData(createPriceData(oie));
		}
		
		return serviceMaterial;
	}

	public ShiptoMaterialData createShiptoMaterialData(DeliverySettingDTO dsDTO) {

		final ShiptoMaterialData shiptoMaterial = new ShiptoMaterialData();

		final SapContractItemEntity ci = dsDTO.getSapContractItem();
		final SapContractEntity c = ci.getSapContract();
		
		shiptoMaterial.setContractNumber(c.getSapContractNo());
		shiptoMaterial.setIncoterms(c.getIncotermsCd());
		shiptoMaterial.setContractItemNumber(ci.getContractItemNo().toString());
		shiptoMaterial.setMaterialNo(ci.getSapMaterial().getSapMaterialIfcNo());
		shiptoMaterial.setProdPlantNo(dsDTO.getProductionPlantCd());
		/**
		 * Make sure that the shipto is always the one received.
		 * See also LOGON-5503
		 */
		shiptoMaterial.setShipTo(dsDTO.getShiptoSarea().getShipto().getShiptoSapBp().getSapIfcId());
		shiptoMaterial.setSoldTo(c.getSoldtoSapBp().getSapIfcId());
		
		return shiptoMaterial;
	}

	private OrderItemData createOrderItemData(OrderItemEntity oie) {
		
		OrderItemData item = new OrderItemData();
		
		item.setLogonOrderItemNumber(getAsBigDecimal(oie.getOrderItemNo()));
		
		// find the corresponding customer order item (no direct link exists unfortunately)
		final CustorderEntity ce = oie.getOrder().getCustorder();
		if (ce != null) {
			final ReadOnlyDao<CustorderItemEntity> dao = getDaoProvider().getCustorderItemEntityDao();
			final Filter<CustorderItemEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(CustorderItemEntity.ATTR_CUSTORDER_ID,
					ce.getCustorderId()));
			filter.addExpression(Restrictions.eq(CustorderItemEntity.ATTR_SAP_MATERIAL_ID,
					oie.getSapMaterial().getSapMaterialId()));
			final List<CustorderItemEntity> custorderItems = dao.find(filter);
			if (!custorderItems.isEmpty()) {
				CustorderItemEntity cie = custorderItems.get(0);
				item.setLdsOrderItemNumber(cie.getLdsCustorderItemNo());
			}
		}
		
		// orderItemData might be asked in state below dispatched when )there is
		// no load
		if (oie.getLoad() != null && oie.getLoad().getLoadOrderItemId() != null) {
			item.setLoadId(oie.getLoad().getLoadOrderItemId().toString());
		}
		
		item.setMaterial(createMaterialData(oie));
		
		if (LogonUtil.isPlanningMaterial(oie.getSapMaterial())) {
			createPossibleMaterial(item, oie);
		}
		
		if (oie.getLoadingpoint() != null) {
			item.setLoadingpointNo(oie.getLoadingpoint().getWeighSystemLoadingpointNo());
			String loadingPointName = oie.getLoadingpoint().getLoadingpointName().toString();
			item.setLoadingpointName(loadingPointName);
		}
		
		if (isCashSales(oie.getOrder())) {
			item.setPriceData(createPriceData(oie));
		}
		
		return item;
	}

	private ReducedOrderItemData createReducedOrderItemData(OrderItemEntity oie) {
		
		ReducedOrderItemData item = new ReducedOrderItemData();
		
		item.setLogonOrderItemNumber(getAsBigDecimal(oie.getOrderItemNo()));
		
		return item;
	}
	
	private void createPossibleMaterial(OrderItemData orderItemData, OrderItemEntity orderItemEntity) {
		List<OrderItemData.PossibleMaterial> possibleMaterialList = orderItemData.getPossibleMaterial();
		
		if (orderItemEntity.getSapContractItem() == null) {
			throw new LogonException("planning material order item must have a contract item", Reason.RES_IMPL_BUG);
		}
		List<SapContractItemEntity> replacingContractItemList = materialEnhancementsService.getReplacingContractItemList(
				orderItemEntity.getSapContractItem().getSapContractItemId());
		for (SapContractItemEntity contractItem : replacingContractItemList) {
			SapMaterialEntity material = contractItem.getSapMaterial();
			if (!orderItemEntity.getSapMaterial().getSapMaterialId().equals(material.getSapMaterialId())) {
				
				List<PlantBsEntity> prodPlantList = null;
				PlantBsEntity prodPlant = materialEnhancementsService.getDefaultProductionPlant(contractItem);
				if (prodPlant == null) {
					prodPlantList = orderDetailService.loadSapMaterialPlantList(material.getSapMaterialId(), true);
					if (prodPlantList.isEmpty()) {
						throw new LogonException("there is no production plant found to the material", Reason.RES_IMPL_BUG);
					}
					for (PlantBsEntity plant : prodPlantList) {
						addPossibleMaterialsToList(possibleMaterialList, material.getSapMaterialNo(), plant.getPlantCd());
					}
				}
				else {
					addPossibleMaterialsToList(possibleMaterialList, material.getSapMaterialNo(), prodPlant.getPlantCd());
				}
			}
		}
	}
	
	private void addPossibleMaterialsToList(List<OrderItemData.PossibleMaterial> possibleMaterialList, String materialNo, String plantCd) {
		OrderItemData.PossibleMaterial possibleMaterial = new OrderItemData.PossibleMaterial();
		possibleMaterial.setMaterialNo(materialNo);
		possibleMaterial.setProdPlantNo(plantCd);
		possibleMaterialList.add(possibleMaterial);
	}
	
	private PriceData createPriceData(OrderItemEntity oie) {
		
		PriceData priceData = null;
		
		final InvoiceBean invoiceBean = orderService.getInvoiceBean(oie);
		if (invoiceBean != null) {
			priceData = new PriceData();
			priceData.setCurrency(invoiceBean.getCurrency());
			priceData.setNetPrice(invoiceBean.getNetPrice());
			priceData.setVatValue(invoiceBean.getVatValue());
			priceData.setGrossPrice(invoiceBean.getGrossPrice());
		}
		
		return priceData;
	}
	
	private SapVehicleEntity getSapVehicle(String sapVehicleNo) {
		
		Authorizer.disableNextAccessRestriction();
		
		final ReadOnlyDao<SapVehicleEntity> vehicleDao = getDaoProvider().getSapVehicleEntityDao();
		final Filter<SapVehicleEntity> f = new FilterImpl<SapVehicleEntity>();
		f.addExpression(Restrictions.eq(SapVehicleEntity.ATTR_SAP_VEHICLE_NO, sapVehicleNo));
		
		return vehicleDao.findSingle(f);
	}
	
	private boolean isCashSales(OrderEntity order) {
		SapContractEntity contractEntity = order.getSapContract();
		SareaEntity sareaEntity = order.getSarea();
		return orderService.isCashSales(contractEntity, sareaEntity);
	}
	
	private static BigDecimal getAsBigDecimal(Long value) {
		if (value != null) {
			return BigDecimal.valueOf(value.longValue());
		}
		return null;
	}
	
	private static String getUomToSend(UomBsEntity uom) {
		if (uom != null) {
			return StringUtils.defaultString(uom.getSendToSap(), uom.getUomCd());
		}
		return null;
	}


	private static Filter<RepTransplusEntity> getOrdersForCarriersFilter(ReadOnlyDao<RepTransplusEntity> dao,
			List<String> selectedSapVendors,
			Date fromDateTime, Date toDateTime) {
		final Filter<RepTransplusEntity> filter = dao.createFilter();

		filter.addExpression(Restrictions.in(RepTransplusEntity.ATTR_COMM_CARRIER_V_SAP_ID, selectedSapVendors));
		filter.addExpression(Restrictions.ge(RepTransplusEntity.ATTR_O_CTL_MODIFICATION_DAT, fromDateTime));
		filter.addExpression(Restrictions.le(RepTransplusEntity.ATTR_O_CTL_MODIFICATION_DAT, toDateTime));
		filter.addExpression(Restrictions.in(RepTransplusEntity.ATTR_O_ORDER_STATUS_CD, new Long[] {
				Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER,
				Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK
		}));

		return filter;
	}

	public List<RepTransplusEntity> getOrdersForCarriers(List<Long> orderNumbers, List<String> selectedSapVendors) {
		final ReadOnlyDao<RepTransplusEntity> dao = getDaoProvider().getRepTransplusEntityDao();
		final Filter<RepTransplusEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.in(RepTransplusEntity.ATTR_O_ORDER_NUMBER, orderNumbers));
		filter.addExpression(Restrictions.in(RepTransplusEntity.ATTR_COMM_CARRIER_V_SAP_ID, selectedSapVendors));
		filter.addExpression(Restrictions.in(RepTransplusEntity.ATTR_O_ORDER_STATUS_CD, new Long[] {
				Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER,
				Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK
		}));
		// setting the sorting to make the paging work
		filter.getOrderByExpressions().add(Order.asc(RepTransplusEntity.ATTR_O_ORDER_NUMBER));
		filter.getOrderByExpressions().add(Order.asc(RepTransplusEntity.ATTR_OI_ORDER_ITEM_NO));
		return dao.find(filter);
	}

	/**
	 * Counts the total number of objects the getOrdersForCarriers() with the same input could return without paging.
	 * @param selectedSapVendors
	 * @param fromDateTime
	 * @param toDateTime
	 * @return
	 */
	public long countOrdersForCarriers(List<String> selectedSapVendors, Date fromDateTime, Date toDateTime) {
		final ReadOnlyDao<RepTransplusEntity> dao = getDaoProvider().getRepTransplusEntityDao();
		final Filter<RepTransplusEntity> filter = getOrdersForCarriersFilter(dao, selectedSapVendors, fromDateTime, toDateTime);

		// because this table contains order items, but the paging is per orders, we have to count the individual orders
		JPAQuery<RepTransplusEntity> orderQuery = dao.createJPAQuery();
		orderQuery.setFilter(filter);
		orderQuery.addSelect(SelectExpression.countDistinct(RepTransplusEntity.ATTR_O_ORDER_NUMBER));

		return (long) dao.findSingle(orderQuery);
	}

	public List<Long> getOrderNumbersForCarriers(List<String> selectedSapVendors, Date fromDateTime, Date toDateTime) {
		final ReadOnlyDao<RepTransplusEntity> dao = getDaoProvider().getRepTransplusEntityDao();
		final Filter<RepTransplusEntity> filter = getOrdersForCarriersFilter(dao, selectedSapVendors, fromDateTime, toDateTime);
		filter.getOrderByExpressions().add(Order.asc(RepTransplusEntity.ATTR_O_ORDER_NUMBER));

		// because this table contains order items, but the paging is per orders, we have to count the individual orders
		JPAQuery<RepTransplusEntity> orderQuery = dao.createJPAQuery();
		orderQuery.setFilter(filter);
		orderQuery.addSelect(SelectExpression.simpleSelect(RepTransplusEntity.ATTR_O_ORDER_NUMBER));
		orderQuery.setDistinct(true);

		return (List<Long>) dao.find(orderQuery);
	}
}