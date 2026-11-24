
package com.crh.logon.business.services.eventbeans.in.logon_lds_interface;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.utils.AssignmentUtils;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.WeighSystemSpBsEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.CommExecCarrierPfEntity;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.GetLoadOrdersForVehicleRequest;
import com.holcim.logon.eventbeans.in.logon_lds_interface.GetLoadOrdersForVehicleResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.LoadOrderData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.LocationData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.VehicleData;
import org.apache.commons.lang.StringUtils;

@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class GetLoadOrdersForVehicleRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {

	private static final String FIELD_VEHICLE = "vehicle";

	private static final String FIELD_LOCATION = "location";

	private static final String FIELD_TARE_WEIGHT_UOM = "tareWeightUom";

	@Override
	protected void validate(LogonEventRequest eventBean) {
		final GetLoadOrdersForVehicleRequest request = (GetLoadOrdersForVehicleRequest) eventBean;

		validateMandatory(FIELD_CLIENT_TYPE, request.getClientType());
		validateMandatory(FIELD_VEHICLE, request.getVehicle());
		validateMandatory(FIELD_LOCATION, request.getLocation());
		if (request.getTareWeight() != null) {
			validateMandatory(FIELD_TARE_WEIGHT_UOM, request.getTareWeightUom());
		}
	}

	@Override
	protected LogonEventResponse perform(LogonEventRequest eventBean) {

		final GetLoadOrdersForVehicleRequest request = (GetLoadOrdersForVehicleRequest) eventBean;
		final GetLoadOrdersForVehicleResponse response = createLogonOKEventResponse(GetLoadOrdersForVehicleResponse.class);

		Collection<AssignmentEntity> assignments = null;

		final WeighSystemSpBsEntity weighingSystemType = getWeighingSystemType(request.getClientType());
		final boolean allowTours = weighingSystemType.getEnableTours();

		final VehicleData vehicle = request.getVehicle();
		final List<String> materialRestrictions = request.getMaterialRestriction();
		final TruckEntity truck = getTruck(vehicle, FIELD_VEHICLE, true);

		if (LogonUtil.isPlanningTruck(truck)) {
			throw newApplicationError(String.format(
					"Received a planning truck (truckPlateNo = %s, sapTruckNo = %s), getLoadOrdersForVehicle not possible.",
					vehicle.getTruckPlateNo(), vehicle.getSapTruckNo()));
		}

		final LocationData location = request.getLocation();
		final PlantBsEntity plant = getPlant(location.getSupplyPlantNo(), FIELD_LOCATION + ".supplyPlantNo");

		final ShippingpointEntity sp;
		final LoadingpointEntity lp;
		if (StringUtils.isNotEmpty(location.getLoadingpointNo()) &&
				StringUtils.isNotEmpty(location.getSupplyPlantNo())) {
			lp = getLoadingpoint(location.getSupplyPlantNo(),
					location.getLoadingpointNo());
			sp = lp.getShippingpoint();
		}
		else {
			lp = null;
			sp = null;
		}

		// Level 1: exactly one assignment, assigned to the specific truck requested
		final AssignmentEntity firstRealOrder = getFirstRealOrder(truck, materialRestrictions, plant, sp, lp, allowTours);
		if (firstRealOrder != null) {
			assignments = Collections.singletonList(firstRealOrder);
		}

		// Level 2: all assignments of all compatible carriers
		if (assignments == null) {
			assignments = getPlannedOrders(truck.getCarrier(), materialRestrictions, plant, sp, allowTours);
		}

		// Level 3: all assignments of a the generic carrier
		if (assignments == null) {
			assignments = getPlannedOrdersOfTheGenericCarrier(materialRestrictions, plant, sp, allowTours);
		}

		final BigDecimal tareWeight = request.getTareWeight();
		final String tareWeightUom = request.getTareWeightUom();
		if (tareWeight != null) {
			response.setTareCheckResult(tareCheck(truck, tareWeight, tareWeightUom, FIELD_TARE_WEIGHT_UOM));
		}

		final Collection<LoadOrderData> loadOrderDataList = response.getLoadOrder();
		if (assignments != null) {
			for (AssignmentEntity assignment : assignments) {
				if (assignment.getLoadOrderId() == null) {
					assignment.setLoadOrderId(fetchSequenceNextVal("SLGNC_LOAD_ORDER"));
					for (LoadEntity load : AssignmentUtils.getLoadsFromAssignment(assignment)) {
						load.setLoadOrderItemId(fetchSequenceNextVal("SLGNC_LOAD_ORDER_ITEM"));
					}
				}
				loadOrderDataList.add(ldsService.createLoadOrderData(assignment));
			}
		}

		return response;
	}

	private String getOrderNumberString(AssignmentEntity assignment) {

		final List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);
		final List<Long> orderNumberList = new ArrayList<Long>();
		for (OrderEntity order : orders) {
			orderNumberList.add(order.getOrderNumber());
		}
		final String result = StringUtils.join(orderNumberList.toArray());
		return result;
	}

	private String getMaterialErrorString(AssignmentEntity assignment, TruckEntity truck,
			SapMaterialEntity material, LoadingpointEntity lp) {

		final String intro = "The truck [%s] should load the LOGON order(s) [%s]";
		final String loadOrderId = " with load order id [%d]";
		final String matRestriction = ", but that is not possible because material [%s] is not available";
		final String loadingPoint = " at loading point [%s]";
		final String end = ". Please check the silo-material allocation in the LDS or load at another loading point.";

		final String result = String.format("%s%s%s%s%s",
				String.format(intro, truck.getTruckPlateNo(), getOrderNumberString(assignment)),
				(assignment.getLoadOrderId() != null ? String.format(loadOrderId, assignment.getLoadOrderId()) : ""),
				String.format(matRestriction, material.getSapMaterialNo()),
				(lp != null ? String.format(loadingPoint, lp.getLoadingpointName()) : ""),
				end);

		return result;
	}

	private String getLocationErrorString(AssignmentEntity assignment, TruckEntity truck,
			PlantBsEntity isP, PlantBsEntity shouldP,
			ShippingpointEntity isSP, ShippingpointEntity shouldSP) {

		final String intro = "The truck [%s] should load the LOGON order(s) [%s]";
		final String loadOrderId = " with load order id [%d]";
		final String at = " at";
		final String shippingPoint = " shipping point [%s] of";
		final String plant = " plant [%s]";
		final String but = ", but the location received is";
		final String end =
				". Please assign the orders mentioned to another truck or send the truck to the correct shipping point/plant.";

		final String result = String.format("%s%s%s%s%s%s%s%s%s",
				String.format(intro, truck.getTruckPlateNo(), getOrderNumberString(assignment)),
				(assignment.getLoadOrderId() != null ? String.format(loadOrderId, assignment.getLoadOrderId()) : ""),
				at,
				String.format(shippingPoint, shouldSP.getShippingpointName()),
				String.format(plant, shouldP.getPlantName()),
				but,
				(isSP != null ? String.format(shippingPoint, isSP.getShippingpointName()) : ""),
				String.format(plant, isP.getPlantName()),
				end);

		return result;
	}

	private AssignmentEntity getFirstRealOrder(TruckEntity truck, List<String> materialRestrictions,
			PlantBsEntity plant, ShippingpointEntity sp, LoadingpointEntity lp, boolean allowTours) {

		final Filter<AssignmentPartEntity> filter = getDaoProvider().getAssignmentPartEntityDao().createFilter();

		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_TRUCK, truck));

		final SortedSet<AssignmentEntity> assignments = getAssignmentsFilteredUsingOrderAttributes(filter, allowTours);

		if (assignments.isEmpty()) {
			return null;
		}

		AssignmentEntity firstAssignment = assignments.iterator().next();
		// does it match the material restrictions?
		if (materialRestrictions != null && !materialRestrictions.isEmpty()) {
			for (LoadEntity load : AssignmentUtils.getLoadsFromAssignment(firstAssignment)) {
				if (!materialRestrictions.contains(load.getSapMaterial().getSapMaterialIfcNo())) {
					final String msg = getMaterialErrorString(firstAssignment, truck,
							load.getSapMaterial(), lp);
					throw newApplicationError(msg);
				}
			}
		}
		// is it for the desired plant and shipping point?
		if (sp != null) {
			for (OrderEntity order : AssignmentUtils.getOrdersFromAssignment(firstAssignment)) {
				if (!order.getShippingpoint().equals(sp)) {
					final String msg = getLocationErrorString(firstAssignment, truck,
							plant, order.getShippingpoint().getPlant(),
							sp, order.getShippingpoint());
					throw newApplicationError(msg);
				}
			}
		}
		else if (plant != null) {
			for (OrderEntity order : AssignmentUtils.getOrdersFromAssignment(firstAssignment)) {
				if (!order.getShippingpoint().getPlant().equals(plant)) {
					final String msg = getLocationErrorString(firstAssignment, truck,
							plant, order.getShippingpoint().getPlant(),
							sp, order.getShippingpoint());
					throw newApplicationError(msg);
				}
			}
		}

		return firstAssignment;
	}

	private SortedSet<AssignmentEntity> getPlannedOrders(CarrierEntity executingCarrier, List<String> materialRestrictions,
			PlantBsEntity plant, ShippingpointEntity sp, boolean allowTours) {

		final ReadOnlyDao<CommExecCarrierPfEntity> pfDao = getDaoProvider().getCommExecCarrierPfEntityDao();
		final Filter<CommExecCarrierPfEntity> pfFilter = pfDao.createFilter();

		pfFilter.addExpression(Restrictions.eq(CommExecCarrierPfEntity.ATTR_EXEC_CARRIER_ID, executingCarrier.getCarrierId()));
		List<CommExecCarrierPfEntity> pfList = pfDao.find(pfFilter);

		if (pfList.isEmpty()) {
			return null;
		}

		List<Long> commercialCarriers = new ArrayList<Long>(pfList.size());
		for (CommExecCarrierPfEntity pf : pfList) {
			commercialCarriers.add(pf.getCommCarrierId());
		}

		final Filter<AssignmentPartEntity> filter = getDaoProvider().getAssignmentPartEntityDao().createFilter();

		filter.addExpression(Restrictions.eq(
				AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_TRUCK + "." + TruckEntity.ATTR_CARRIER + "."
						+ CarrierEntity.ATTR_ACTIVE_FLAG,
				Boolean.TRUE));

		filter.addExpression(Restrictions.in(
				AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_TRUCK + "." + TruckEntity.ATTR_CARRIER + "."
						+ CarrierEntity.ATTR_CARRIER_ID,
				commercialCarriers));

		filter.addExpression(Restrictions.eq(
				AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_TRUCK + "."
						+ TruckEntity.ATTR_SAP_VEHICLE_CHAR_CD,
				Constant.VEHICLE_CHARACTERISTIC_PLANNING));

		SortedSet<AssignmentEntity> assignments = getAssignmentsFilteredUsingOrderAttributes(filter, allowTours);
		filterAssignmentsByMaterialRestrictions(assignments, materialRestrictions);
		filterAssignmentsByPlantOrShippingpoint(assignments, plant, sp);

		if (!assignments.isEmpty()) {
			return assignments;
		}
		else {
			return null;
		}

	}

	private SortedSet<AssignmentEntity> getPlannedOrdersOfTheGenericCarrier(List<String> materialRestrictions,
			PlantBsEntity plant, ShippingpointEntity sp, boolean allowTours) {

		final Filter<AssignmentPartEntity> filter = getDaoProvider().getAssignmentPartEntityDao().createFilter();

		filter.addExpression(Restrictions.eq(
				AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_TRUCK + "." + TruckEntity.ATTR_CARRIER_ID,
				Constant.ARTIFICIAL_GENERIC_CARRIER_ID));

		SortedSet<AssignmentEntity> assignments = getAssignmentsFilteredUsingOrderAttributes(filter, allowTours);
		filterAssignmentsByMaterialRestrictions(assignments, materialRestrictions);
		filterAssignmentsByPlantOrShippingpoint(assignments, plant, sp);

		if (!assignments.isEmpty()) {
			return assignments;
		}
		else {
			return null;
		}
	}
}
