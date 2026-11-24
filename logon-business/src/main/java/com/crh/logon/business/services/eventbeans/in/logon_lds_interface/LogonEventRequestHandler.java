/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.eventbeans.in.logon_lds_interface;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.ejb.EJB;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.kafka.KafkaPublisherService;
import com.crh.logon.business.ordercreation.DeliverySettingShiptoMap;
import com.crh.logon.business.services.beans.UomConversionResult;
import com.crh.logon.business.services.localinterfaces.DeliverySettingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LDSServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.MaterialEnhancementsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderDetailServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TruckAdminServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UomConverterServiceLocalInterface;
import com.crh.logon.business.services.utils.AssignmentUtils;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.business.stores.sap.BusinessPartnerInterface;
import com.crh.logon.business.stores.sap.ShiptoSareaStoreInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.FireqMaterialMixdesignEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.refdata.AssignStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.BizeventTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.WeighSystemSpBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.sap.SapVehicleEntity;
import com.crh.logon.persistence.entities.views.SoldshipSareaFlexSrchEntity;
import com.crh.logon.utils.LogonConversionException;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.FirstWeighingData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.LoadedMaterialData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.Request;
import com.holcim.logon.eventbeans.in.logon_lds_interface.Response;
import com.holcim.logon.eventbeans.in.logon_lds_interface.ResponseCode;
import com.holcim.logon.eventbeans.in.logon_lds_interface.SecondWeighingData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.VehicleData;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
public abstract class LogonEventRequestHandler extends com.crh.logon.business.services.eventbeans.in.LogonEventRequestHandler {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LogonEventRequestHandler.class);
	
	protected static final String FIELD_CLIENT_TYPE = "header.clientType";

	@EJB DeliverySettingServiceLocalInterface deliverySettingService;
	@EJB RefDataGenericsServiceLocalInterface referentialDataService;
	@EJB MaterialEnhancementsServiceLocalInterface materialEnhancementsService;
	@EJB UomConverterServiceLocalInterface unitOfMeasurementConverterService;
	@EJB LDSServiceLocalInterface ldsService;
	@EJB OrderServiceLocalInterface orderService;
	@EJB OrderDetailServiceLocalInterface orderDetailService;
	@EJB TruckAdminServiceLocalInterface truckAdminService;
	@EJB KafkaPublisherService kafkaPublisherService;
	@EJB BusinessPartnerInterface businessPartnerStore;
	@EJB
	ShiptoSareaStoreInterface shiptoSareaStore;


	public LogonEventRequestHandler() {
		super();
	}

	@Override
	protected final Locale getLocale(LogonEventRequest bean) {
		return new Locale(((Request) bean).getLanguage());
	}

	protected final <T extends LogonEventResponse> T createLogonOKEventResponse(Class<T> clazz) {
		T response;
		try {
			response = clazz.newInstance();
		}
		catch (Exception e) {

			throw new LogonException("Error creating new LogonEventResponse", Reason.RES_ERROR_IMPL);
		}
		BigInteger responseCode = ResponseCode.OK.value();
		response.setCode(responseCode);
		response.setContextValue(null);
		resolveMessageText(response, responseCode, null, (Object[]) null);
		return response;
	}

	@Override
	protected final LogonEventResponse createLogonEventResponse(BigInteger statusCode, String contextValue, Object... fmtParams) { // NOPMD
		
		LogonEventResponse response = createBasicLogonEventResponse(
				statusCode, contextValue);
		
		resolveMessageText(response, statusCode, contextValue, fmtParams);
		
		return response;
	}

	@Override
	protected Response createBasicLogonEventResponse(
			BigInteger statusCode, String contextValue) {
		Response response = null;
		try {
			response = Response.class.newInstance();
		}
		catch (Exception e) {
			
			throw new LogonException("Error creating new LogonEventResponse", Reason.RES_ERROR_IMPL);
		}
		response.setCode(statusCode);
		response.setContextValue(contextValue);
		return response;
	}

	protected final Long parse(String fieldValue, String fieldName, boolean required) {
		if (StringUtils.isNotEmpty(fieldValue)) {
	        try {
	        	return Long.parseLong(fieldValue);
	        }
	        catch (NumberFormatException e) {
	        	throw newFieldParseError(fieldName, fieldValue);
	        }
		}
		else if (required) {
			throw newMandatoryFieldEmpty(fieldName, fieldValue);
		}
		else {
			return null;
		}
	}
	
	/**
	 * Retrieves active assignments based on a filter. The filter may only filter by assignment, assignment part or
	 * order attributes. Also, if a filter condition fails for one order of the assignment, it must also fail for
	 * any other order of the assignment, since otherwise the assignment would be included anyway.
	 * @param filter May only filter for common (within an assignment) order attributes or assignment (part) attributes
	 * @param allowTours Set to false if tours should be filtered out from the results
	 * @return A list of matching assignments
	 */
	public final SortedSet<AssignmentEntity> getAssignmentsFilteredUsingOrderAttributes(Filter<AssignmentPartEntity> filter, boolean allowTours) {
		
		final ReadOnlyDao<AssignmentPartEntity> dao = getDaoProvider().getAssignmentPartEntityDao();
		
		filter.addExpression(Restrictions.in(
				AssignmentPartEntity.ATTR_ORDER + "." + OrderEntity.ATTR_ORDER_STATUS_CD,
				Arrays.asList(new Long[] {
						Constant.ORDER_STATUS_LOADING,
						Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER,
						Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK,
						Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT
				})));
		
		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
		filter.addExpression(Restrictions.not(Restrictions.in(
				AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_ASSIGN_STATUS + "." + AssignStatusBsEntity.ATTR_ASSIGN_STATUS_CD, 
    			Arrays.asList(new Long[] {Constant.ASSIGNMENT_STATUS_FINISHED, Constant.ASSIGNMENT_STATUS_CANCELLED}))));
		
		final List<AssignmentPartEntity> assignmentParts = dao.find(filter);
		
		final SortedSet<AssignmentEntity> assignments = new TreeSet<AssignmentEntity>(new AssignmentUtils.PlannedStartTimeComparator());

		for (AssignmentPartEntity assignmentPart : assignmentParts) {
			assignments.add(assignmentPart.getAssignment());
		}
		
		// filter out tours
		if (!allowTours) {
			for (Iterator<AssignmentEntity> i = assignments.iterator(); i.hasNext();) {
				
				AssignmentEntity assignment = i.next();
				
				if (AssignmentUtils.isTour(assignment)) {
					i.remove();
				}
			}
		}
		
		return assignments;
	}
	
	public void filterAssignmentsByMaterialRestrictions(Set<AssignmentEntity> assignments, List<String> materialRestrictions) {
		
		if (materialRestrictions == null || materialRestrictions.isEmpty()) {
			return;
		}
		
		for (Iterator<AssignmentEntity> i = assignments.iterator(); i.hasNext();) {
			
			AssignmentEntity assignment = i.next();
			
			for (LoadEntity load : AssignmentUtils.getLoadsFromAssignment(assignment)) {
				if (!materialRestrictions.contains(load.getSapMaterial().getSapMaterialIfcNo())) {
					i.remove();
					break;
				}
			}
		}
	}
	
	public void filterAssignmentsByPlantOrShippingpoint(Set<AssignmentEntity> assignments, PlantBsEntity plant, ShippingpointEntity sp) {
		
		if (sp != null) {
			for (Iterator<AssignmentEntity> i = assignments.iterator(); i.hasNext();) {
				
				AssignmentEntity assignment = i.next();
				
				for (OrderEntity order : AssignmentUtils.getOrdersFromAssignment(assignment)) {
					if (!order.getShippingpoint().equals(sp)) {
						i.remove();
						break;
					}
				}
			}
		}
		else if (plant != null) {
			for (Iterator<AssignmentEntity> i = assignments.iterator(); i.hasNext();) {
				
				AssignmentEntity assignment = i.next();
				
				for (OrderEntity order : AssignmentUtils.getOrdersFromAssignment(assignment)) {
					if (!order.getShippingpoint().getPlant().equals(plant)) {
						i.remove();
						break;
					}
				}
			}
		}
	}
	
	protected final BizeventEntity createBizevent(long eventType, Date eventTs, boolean persist) {
		BizeventEntity bizevent = new BizeventEntity();
		
		final BizeventTypeBsEntity type = new BizeventTypeBsEntity();
		type.setBizeventTypeCd(eventType);
		
		bizevent.setBizeventType(type);
		bizevent.setEventTs(eventTs);
		
		if (persist) {
			final ReadWriteDao<BizeventEntity> dao = getDaoProvider().getBizeventEntityDao();
			bizevent = dao.persist(bizevent, true);
		}
		
		return bizevent;
	}

	protected final TruckEntity getTruck(VehicleData vehicleData, String vehicleDataFieldName,
			boolean failIfNotFound) {

		if (vehicleData == null) {
			throw newMandatoryFieldMissing(vehicleDataFieldName);
		}

		final String truckPlateNo = vehicleData.getTruckPlateNo();
		if (truckPlateNo == null) {
			throw newMandatoryFieldMissing(vehicleDataFieldName + ".truckPlateNo");
		}

		final String sapTruckNo = vehicleData.getSapTruckNo();
		final TruckEntity truck;

		final ReadOnlyDao<TruckEntity> dao = getDaoProvider().getTruckEntityDao();
		final Filter<TruckEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(TruckEntity.ATTR_TRUCK_PLATE_NO, truckPlateNo));
		if (sapTruckNo != null) {
			final ReadOnlyDao<SapVehicleEntity> sapDao = getDaoProvider().getSapVehicleEntityDao();
			final Filter<SapVehicleEntity> sapFilter = sapDao.createFilter();
			sapFilter.addExpression(Restrictions.eq(SapVehicleEntity.ATTR_SAP_VEHICLE_PLATE_NO, truckPlateNo));
			sapFilter.addExpression(Restrictions.eq(SapVehicleEntity.ATTR_SAP_VEHICLE_IFC_NO, sapTruckNo));
			final SapVehicleEntity sapVehicleEntity = sapDao.findSingle(sapFilter);
			if (sapVehicleEntity != null) {
				filter.addExpression(Restrictions.eq(TruckEntity.ATTR_SAP_VEHICLE_NO, sapVehicleEntity.getSapVehicleNo()));
			}
			else {
				throw newEntityNotFoundError(SapVehicleEntity.class, "truckPlateNo", truckPlateNo, "sapTruckNo = " + sapTruckNo);
			}
		}
		filter.addExpression(Restrictions.eq(TruckEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));

		truck = dao.findSingle(filter);

		if (truck == null && failIfNotFound) {
			throw newEntityNotFoundError(TruckEntity.class, "truckPlateNo", truckPlateNo,
					"sapTruckNo = " + sapTruckNo);
		}

		return truck;
	}

	protected final TrailerEntity getTrailer(String trailerPlateNo, String trailerPlateNoFieldName,
			boolean failIfNotFound) {
		
		if (trailerPlateNo == null) {
			throw newMandatoryFieldMissing(trailerPlateNoFieldName);
		}
		
		final TrailerEntity trailer;
		
		final ReadOnlyDao<TrailerEntity> dao = getDaoProvider().getTrailerEntityDao();
		final Filter<TrailerEntity> f = dao.createFilter();
		
		f.addExpression(Restrictions.eq(TrailerEntity.ATTR_TRAILER_PLATE_NO, trailerPlateNo));
		f.addExpression(Restrictions.eq(TrailerEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
		trailer = dao.findSingle(f);
		
		if (trailer == null && failIfNotFound) {
			throw newEntityNotFoundError(TrailerEntity.class, "trailerPlateNo", trailerPlateNo);
		}
		
		return trailer;
	}

	protected final OrderEntity getOrder(String logonOrderNoStr, String fieldName) {

        final Long logonOrderNo = parse(logonOrderNoStr, fieldName, true);
		final ReadOnlyDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
		final Filter<OrderEntity> orderFilter = orderDao.createFilter();
		orderFilter.addExpression(Restrictions.eq(OrderEntity.ATTR_ORDER_NUMBER, logonOrderNo));
		final OrderEntity order = orderDao.findSingle(orderFilter);
		
		if (order == null) {
			throw newEntityNotFoundError(OrderEntity.class, fieldName, logonOrderNoStr);
		}
    	        
    	return order;
    }
	
	protected final LoadEntity getLoad(String loadOrderItemIdStr, String fieldName) {

        final Long loadOrderItemId = parse(loadOrderItemIdStr, fieldName, true);
		final ReadOnlyDao<LoadEntity> dao = getDaoProvider().getLoadEntityDao();
		final Filter<LoadEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(LoadEntity.ATTR_LOAD_ORDER_ITEM_ID, loadOrderItemId));
		final LoadEntity load = dao.findSingle(filter);
		
		if (load == null) {
			throw newEntityNotFoundError(LoadEntity.class, fieldName, loadOrderItemIdStr);
		}
        
    	return load;
    }
	
	protected final List<AssignmentPartEntity> getAssignmentPartsForLoading(String loadOrderIdStr,
			String fieldName) {

        final Long loadOrderId = parse(loadOrderIdStr, fieldName, true);
		final ReadOnlyDao<AssignmentPartEntity> dao = getDaoProvider().getAssignmentPartEntityDao();
		final Filter<AssignmentPartEntity> filter = dao.createFilter();
        filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT + '.' + AssignmentEntity.ATTR_LOAD_ORDER_ID, loadOrderId));
        filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT_PART_TYPE_CD, Constant.ASSIGNMENT_PART_TYPE_LOAD));            
        final List<AssignmentPartEntity> apes = dao.find(filter);
		
		if ((apes == null) || (apes.isEmpty())) {
			throw newEntityNotFoundError(AssignmentPartEntity.class, fieldName, loadOrderIdStr);
		}    	
    	
    	return apes;
    }

	protected final AssignmentPartEntity getAssignmentPartForLoading(String loadOrderIdStr,
			String loadOrderIdFieldName, String loadOrderItemIdStr,
			String loadOrderItemIdFieldName) {
		
		final Long loadOrderId = parse(loadOrderIdStr, loadOrderIdFieldName, true);
		final Long loadOrderItemId = parse(loadOrderItemIdStr, loadOrderItemIdFieldName, true);
		
		final ReadOnlyDao<AssignmentPartEntity> dao = getDaoProvider().getAssignmentPartEntityDao();
		final Filter<AssignmentPartEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_LOAD + '.' + LoadEntity.ATTR_LOAD_ORDER_ITEM_ID, loadOrderItemId));
		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT + '.' + AssignmentEntity.ATTR_LOAD_ORDER_ID, loadOrderId));
		final AssignmentPartEntity ape = dao.findSingle(filter);
		
		if (ape == null) {
			throw newEntityNotFoundError(AssignmentPartEntity.class, loadOrderIdFieldName, loadOrderIdStr , loadOrderItemIdFieldName + " = " + loadOrderItemIdStr);
		}
		return ape;
	}
	
	protected final AssignmentPartEntity getAssignmentPartForLoading(String loadOrderItemIdStr, String loadOrderItemIdFieldName) {
		
		final Long loadOrderItemId = parse(loadOrderItemIdStr, loadOrderItemIdFieldName, true);
		
		final ReadOnlyDao<AssignmentPartEntity> dao = getDaoProvider().getAssignmentPartEntityDao();
		final Filter<AssignmentPartEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_LOAD + '.' + LoadEntity.ATTR_LOAD_ORDER_ITEM_ID, loadOrderItemId));
		final AssignmentPartEntity ape = dao.findSingle(filter);
		
		if (ape == null) {
			throw newEntityNotFoundError(AssignmentPartEntity.class, loadOrderItemIdFieldName, loadOrderItemIdStr);
		}
		return ape;
	}
	
	protected final AssignmentEntity getAssignment(String loadOrderIdStr, String fieldName) {
		
		final AssignmentEntity ae;
		final Long loadOrderId = parse(loadOrderIdStr, fieldName, true);
		final ReadOnlyDao<AssignmentEntity> dao = getDaoProvider().getAssignmentEntityDao();
		final Filter<AssignmentEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(AssignmentEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
		filter.addExpression(Restrictions.eq(AssignmentEntity.ATTR_LOAD_ORDER_ID, loadOrderId));
		
		ae = dao.findSingle(filter);
		if (ae == null) {
			throw newEntityNotFoundError(AssignmentEntity.class, fieldName, loadOrderIdStr);
		}
		return ae;
	}
	
	protected LoadingpointEntity getLoadingpointForLoad(LoadEntity load, ShippingpointEntity sp, SecondWeighingData secondWeighingData, 
			FirstWeighingData firstWeighingData, String secondWeighingFieldName, String firstWeighingFieldName) {
		LoadingpointEntity lp = null;
	
		if (secondWeighingData != null
				&& StringUtils.isNotEmpty(secondWeighingData.getW2WeighingPointNo())) {
			lp = getLoadingpoint(sp.getPlant().getPlantCd(),
					secondWeighingData.getW2WeighingPointNo());
		}
		else if (firstWeighingData != null
				&& StringUtils.isNotEmpty(firstWeighingData.getW1WeighingPointNo())) {
			lp = getLoadingpoint(sp.getPlant().getPlantCd(),
					firstWeighingData.getW1WeighingPointNo());
		}
	
		if (lp == null) {
			if (load.getLoadingpoint() != null) {
				// loading point has been set in startLoading
				lp = load.getLoadingpoint();
			}
			else {
				// this is not acceptable (goods movement, for instance)
				throw newApplicationError(String.format(
						"Loadingpoint cannot be identified (no StartLoading has been issued previously, and both %s and %s are empty).",
						String.format(secondWeighingFieldName),
						String.format(firstWeighingFieldName)));
			}
		}
		return lp;
	}

	protected final BigDecimal getAsT(BigDecimal weight, String weightUom, String uomFieldName) {
		return getAsT(weight, getUom(weightUom, uomFieldName));
	}

	protected final BigDecimal getAsT(BigDecimal quantity, UomBsEntity uom) {

		final BigDecimal result;
		result = getAsT(quantity, uom, null);
		
		return result;
	}
	
	protected final BigDecimal getAsT(BigDecimal quantity, UomBsEntity uom, SapMaterialEntity material) {
		
		final BigDecimal result;
		
		final UomBsEntity uomT = referentialDataService.findBsEntity(UomBsEntity.class, Constant.UOM_TONS);
		final UomConversionResult ucr;
		try {
			ucr = unitOfMeasurementConverterService.convert(quantity, uom, uomT, material, null, null, false);
		}
		catch (LogonConversionException e) {
			final String msg = "Could not convert from UOM " + uom + " to tons for material " +
					material + " (" + e.getMessage() + ")";
			LOG.error(msg);
			throw new LogonException(msg, e);
		}
		result = ucr.getQuantity();
		
		return result;
	}

	protected final Boolean tareCheck(TruckEntity truck, BigDecimal tareWeight, String tareWeightUom, String uomFieldName) {
		return tareCheck(truck, tareWeight, getUom(tareWeightUom, uomFieldName));
	}
	
	protected final Boolean tareCheck(TruckEntity truck, BigDecimal tareWeight, UomBsEntity tareWeightUom) {
		final BigDecimal checkInT = truckAdminService.getTareWeightTComposition(truck);
		if ((checkInT != null) && (checkInT.compareTo(BigDecimal.ZERO) != 0) ) {
			final BigDecimal toleranceInT = BigDecimal.valueOf((long) getConfigBean().getAppDefaultTruckLoadTolerance());
			final BigDecimal measuredInT = getAsT(tareWeight, tareWeightUom);			    			 
			final BigDecimal diffInT = measuredInT.subtract(checkInT);
			return (diffInT.abs().compareTo(toleranceInT) <= 0);
		}
		else {
			return null;
		}
	}
	
	protected final void verifyNoPlanning(AssignmentEntity assignment) {
		
		TruckEntity truck = assignment.getTruck();
		
		if (LogonUtil.isPlanningTruck(truck)) {
			throw newApplicationError("Load order is still on a planning truck.");
		}
		
		List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);
		
		for (OrderEntity order : orders) {
			CarrierEntity commCarrier = order.getCommCarrier();
			Long carrierId = commCarrier.getCarrierId();
			
			if (carrierId.equals(Constant.ARTIFICIAL_GENERIC_CARRIER_ID)) {
				throw newApplicationError("Order [" + order.getOrderNumber()
						+ "] is still assigned to generic carrier.");
			}
		}
		
		verifyNoPlanningMaterial(orders);
	}

	protected PlantBsEntity getPlantForPlanningMaterial(String plantNo, CustorderItemEntity custorderItemEntity) {
		PlantBsEntity result = referentialDataService.findBsEntity(PlantBsEntity.class, plantNo);
		if (result != null) {
			return result;
		}
		
		SapMaterialEntity custorderSapMaterial = custorderItemEntity.getSapContractItem().getSapMaterial();
		List<SapContractItemEntity> replacingContractItemList = materialEnhancementsService.getReplacingContractItemList(
				custorderItemEntity.getSapContractItem().getSapContractItemId());
		if (replacingContractItemList.size() == 1) {
			throw newApplicationError(String.format(
					"No replacing material found to this planning material."));
		}
		for (SapContractItemEntity contractItem : replacingContractItemList) {
			SapMaterialEntity material = contractItem.getSapMaterial();
			if (!custorderSapMaterial.getSapMaterialId().equals(material.getSapMaterialId())) {
				
				List<PlantBsEntity> prodPlantList = null;
				PlantBsEntity prodPlant = materialEnhancementsService.getDefaultProductionPlant(contractItem);
				if (prodPlant == null) {
					prodPlantList = orderDetailService.loadSapMaterialPlantList(material.getSapMaterialId(), true);
					if (prodPlantList.isEmpty()) {
						throw newApplicationError(String.format(
								"No valid production plant found to this planning material item."));
					}
					return prodPlantList.get(0);
				}
				else {
					return prodPlant;
				}
			}
		}
		return result;
	}
	
	protected final FireqMaterialMixdesignEntity processMixDesign(LoadedMaterialData loadedMaterial, String loadedMaterialFieldName) {

		FireqMaterialMixdesignEntity result = null;
		
		final String materialNo = loadedMaterial.getMaterialNo();
		final String prodPlantNo = loadedMaterial.getProdPlantNo();
		final String aggRmxCode = loadedMaterial.getAggRmxCode();
		final String mixDesignID = loadedMaterial.getMixDesignID();
		
		if ((materialNo != null) && (prodPlantNo != null) && (aggRmxCode != null) && (mixDesignID != null)) {
			FireqMaterialMixdesignEntity mixDesign = getMixDesign(mixDesignID, loadedMaterialFieldName + ".mixDesignID");			
			if (mixDesign == null) {
				mixDesign = new FireqMaterialMixdesignEntity();
			}
					
			final SapMaterialEntity sapMaterial = getMaterial(loadedMaterial.getMaterialNo(), loadedMaterialFieldName + ".materialNo");
			final PlantBsEntity plant = getPlant(loadedMaterial.getProdPlantNo(), loadedMaterialFieldName + ".prodPlantNo");
	
			mixDesign.setAggRmxCode(loadedMaterial.getAggRmxCode());
			mixDesign.setMixDesignCd(loadedMaterial.getMixDesignID());
			mixDesign.setPlant(plant);
			mixDesign.setSapMaterial(sapMaterial);
			
			getDaoProvider().getFireqMaterialMixdesignEntityDao().persist(mixDesign);
			
			result = mixDesign;
		}
		
		return result;
	}
	
	protected final FireqMaterialMixdesignEntity getMixDesign(String mixDesignCd, String mixDesignCdFieldName) {
		final FireqMaterialMixdesignEntity mixDesign; 
		final ReadWriteDao<FireqMaterialMixdesignEntity> dao = getDaoProvider().getFireqMaterialMixdesignEntityDao();
		final Filter<FireqMaterialMixdesignEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(FireqMaterialMixdesignEntity.ATTR_MIX_DESIGN_CD, mixDesignCd));
		mixDesign = dao.findSingle(filter);
		if (mixDesign == null) {
			throw newEntityNotFoundError(FireqMaterialMixdesignEntity.class, mixDesignCdFieldName, mixDesignCd);			
		}
		return mixDesign;
	}

	protected final DeliverySettingShiptoMap getDeliverySettingShiptoMap(ShiptoEntity shipTo, SapBpEntity soldTo, Long sapMaterialId) {
		
		
		final List<SoldshipSareaFlexSrchEntity> commonSalesAreasForShiptoSoldto = shiptoSareaStore.getCommonSalesAreasForShiptoSoldto(shipTo,
				soldTo, null);
		
		if (commonSalesAreasForShiptoSoldto.isEmpty()) {
			if (soldTo != null) {
				throw newApplicationError(String.format(
						"The shipto and soldto specified has no common sales areas, or are not each others partners. shipTo = %s,  soldTo = %s",
						shipTo.getShiptoSapBp().getSapBpId(),
						soldTo.getSapBpId()));
			}
			else {
				throw newApplicationError(String.format(
						"The shipto specified has no sales area. shipTo = %s ", 
						shipTo.getShiptoSapBp().getSapBpId()));
			}			
		}
		else if (soldTo == null) {
			final List<Long> soldtoBpIds = new ArrayList<Long>();
			for (SoldshipSareaFlexSrchEntity SSFSE : commonSalesAreasForShiptoSoldto) {
				soldtoBpIds.add(SSFSE.getSoldtoSapBpId());
			}

			final ReadOnlyDao<SapBpEntity> soldToDao = getDaoProvider().getSapBpEntityDao();
			final Filter<SapBpEntity> soldToFilter = soldToDao.createFilter();
			soldToFilter.addExpression(Restrictions.in(SapBpEntity.ATTR_SAP_BP_ID, soldtoBpIds));
			soldToFilter.addExpression(Restrictions.eq(SapBpEntity.ATTR_BP_IS_SOLDTO, Boolean.TRUE));
			soldTo = soldToDao.findSingle(soldToFilter);
		}

		final List<Long> sareaIds = new ArrayList<Long>();
		for (SoldshipSareaFlexSrchEntity SSFSE : commonSalesAreasForShiptoSoldto) {
			sareaIds.add(SSFSE.getSareaId());
		}
		
		final ReadOnlyDao<ShiptoSareaEntity> dao = getDaoProvider().getShiptoSareaEntityDao();
		final Filter<ShiptoSareaEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(ShiptoSareaEntity.ATTR_SHIPTO, shipTo));
		filter.addExpression(Restrictions.in(ShiptoSareaEntity.ATTR_SAREA_ID, sareaIds));
		final ShiptoSareaEntity shiptoSalesArea = dao.findSingle(filter);
		
		if (sapMaterialId != null) {
			return deliverySettingService.initDeliverySettingShiptoMapForLDS(soldTo, 
					super.getLocale(), 
					shiptoSalesArea, 
					sapMaterialId);
		}
		else {
			return deliverySettingService.initDeliverySettingShiptoMapForLDS(soldTo, 
					super.getLocale(),
					shiptoSalesArea, null);
		}
	}
	
	protected BizeventEntity createBizeventForLoading(LoadEntity load, Date loadingStartTime,
			ShippingpointEntity sp, OrderEntity uniqueOrder, AssignmentEntity assignment, 
			Date eventTime) {
		final Date date = (Date) ObjectUtils.defaultIfNull(loadingStartTime, eventTime);
		final BizeventEntity bizevent = createBizevent(Constant.BIZEVENT_TYPE_LOADING, date, false);
		bizevent.setTruck(load.getTruck());
		bizevent.setTrailer(load.getTrailer());
		bizevent.setContainer(load.getContainer());
		bizevent.setShippingpoint(sp);
		bizevent.setOrder(uniqueOrder);
		bizevent.setAssignment(assignment);
		return bizevent;
	}

	protected BizeventEntity createBizeventForUnloading(LoadEntity load, Date loadingStartTime,
			ShippingpointEntity sp, OrderEntity uniqueOrder, AssignmentEntity assignment, 
			Date eventTime) {
		final Date date = (Date) ObjectUtils.defaultIfNull(loadingStartTime, eventTime);
		final BizeventEntity bizevent = createBizevent(Constant.BIZEVENT_TYPE_UNLOADING, date, false);
		bizevent.setTruck(load.getTruck());
		bizevent.setTrailer(load.getTrailer());
		bizevent.setContainer(load.getContainer());
		bizevent.setShippingpoint(sp);
		bizevent.setOrder(uniqueOrder);
		bizevent.setAssignment(assignment);
		return bizevent;
	}
	

	protected void setFirstWeighingValuesOnLoad(LoadEntity load, FirstWeighingData firstWeighingData, LoadingpointEntity lp, Boolean isTareDeviationDriverACK) {
		if (lp != null) {			
			load.setWsStartLoadingpoint(lp);
		}
	    load.setWsEichbeleg1(firstWeighingData.getLdsW1RecordId());
	    load.setWsEichbeleg1Time(firstWeighingData.getLdsW1Time());
	    load.setWsEichbeleg1Balance(firstWeighingData.getLdsW1BalanceId());
	    load.setWsStartLoadingpoint(lp);
		load.setWsTareQuitWarnFlag((Boolean) ObjectUtils.defaultIfNull(isTareDeviationDriverACK, Boolean.FALSE));
	}
	
	protected BigDecimal calcTareWeight(LoadEntity load, FirstWeighingData firstWeighingData, String uomFieldName, Boolean isTareWarnIgnore) {
		final BigDecimal tareWeight = firstWeighingData.getW1Quantity();
    	final BigDecimal tareWeightT;
    	if (StringUtils.isNotEmpty(firstWeighingData.getW1UoM()) && (tareWeight != null)) {
    		final UomBsEntity tareWeightUom = getUom(firstWeighingData.getW1UoM(), uomFieldName);
    		tareWeightT = getAsT(tareWeight, tareWeightUom);
    		load.setWsOriginalTareMeasureT(tareWeightT);
            
    		if (BooleanUtils.isFalse(isTareWarnIgnore)) {
    	    	
    	    	final Boolean tareCheck = tareCheck(load.getTruck(), tareWeight, tareWeightUom);
    	    	
    	    	if (BooleanUtils.isFalse(tareCheck) /*&& BooleanUtils.isNotTrue(request.isTareDeviationDriverACK()) // LOGON-5349 */) {
    	    		throw newApplicationError("Tare check failed.");
    	    	}			
    		}
    	}
    	else {
    		tareWeightT = null;
    	}
    	return tareWeightT;
	}


	protected WeighSystemSpBsEntity getWeighingSystemType(String clientType) {
		final Filter<WeighSystemSpBsEntity> f = new FilterImpl<WeighSystemSpBsEntity>();
		f.addExpression(Restrictions.eq(WeighSystemSpBsEntity.ATTR_CLIENT_TYPE, clientType));
		f.addExpression(Restrictions.eq(WeighSystemSpBsEntity.ATTR_CTL_ACT_CD, Boolean.TRUE));
		final List<WeighSystemSpBsEntity> weighingSystemTypes = 
				referentialDataService.findBsEntityFiltered(WeighSystemSpBsEntity.class, f);
		
		if (weighingSystemTypes.isEmpty()) {
			throw newEntityNotFoundError(WeighSystemSpBsEntity.class, FIELD_CLIENT_TYPE, clientType);
		}
		
		return weighingSystemTypes.get(0);
	}

	protected Date toDBDate(OffsetDateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		final String timeZoneCode = TimeZone.getDBTimeZone().getId();
		final ZonedDateTime dateTimeInDBTime = dateTime.atZoneSameInstant(ZoneId.of(timeZoneCode));
		return Date.from(dateTimeInDBTime.toInstant());
	}

	protected OffsetDateTime toOffsetDateTimeInDBTimeZone(Date date) {
		if (date == null) {
			return null;
		}
		return OffsetDateTime.ofInstant(date.toInstant(), ZoneId.of(TimeZone.getDBTimeZone().getId()));
	}
}
