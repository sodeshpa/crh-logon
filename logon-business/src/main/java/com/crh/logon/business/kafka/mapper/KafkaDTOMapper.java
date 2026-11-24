/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.kafka.mapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import com.crh.logon.business.kafka.dto.OrderEvent;
import com.crh.logon.business.services.interfaces.DispokarteService;
import com.crh.logon.business.services.interfaces.PoolAdminService;
import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.business.services.interfaces.TruckAdminService;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.PersonEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.MaterialPackageBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderCreationTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusTxEntity;
import com.crh.logon.persistence.entities.refdata.PackageTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.PackageTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.BpSareaSofficeSgroupEntity;
import com.crh.logon.persistence.entities.views.SapMaterialDescEntity;

@Singleton
public class KafkaDTOMapper {

	private static final String DEFAULT_LANGUAGE = "fr";

	private static final Locale DEFAULT_LOCALE = new Locale(DEFAULT_LANGUAGE);

	private static final ZoneId ZONE_UTC = ZoneId.of("UTC");

	@EJB
	private DispokarteService dispokarteServiceBean;

	@EJB
	private RefDataGenericsService refDataGenericsService;

	@EJB
	private RefDataGenericsServiceLocalInterface referentialDataService;

	@EJB
	private TruckAdminService truckAdminService;

	@EJB
	private PoolAdminService poolAdminService;

	public OrderEvent toNewOrder(OrderEntity orderEntity) {
		if (orderEntity == null) {
			return null;
		}

		OrderEvent orderEvent = new OrderEvent();
		Helper helper = new Helper();

		orderEvent.setHeader(toHeader(null, null));
		orderEvent.setOrder(toOrder(orderEntity, helper));
		orderEvent.setDelivery(toInitialDelivery(orderEntity));
		orderEvent.setPlant(toPlant(orderEntity));

		helper.complete();

		return orderEvent;
	}

	public OrderEvent toCancelledOrder(OrderEntity orderEntity) {
		if (orderEntity == null) {
			return null;
		}

		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setHeader(toHeader(null, null));
		orderEvent.setOrder(toMinimalOrder(orderEntity));

		return orderEvent;
	}

	public OrderEvent toReturnOrder(OrderEntity orderEntity) {
		if (orderEntity == null) {
			return null;
		}

		OrderEvent orderEvent = new OrderEvent();
		Helper helper = new Helper();

		orderEvent.setHeader(toHeader(null, null));
		orderEvent.setOrder(toMinimalOrder(orderEntity));
		OrderEvent.MaterialList materialList = new OrderEvent.MaterialList();

		orderEntity.getOrderItems().stream()
				.filter(it -> it.getReturnedQuantity() != null && !BigDecimal.ZERO.equals(it.getReturnedQuantity()))
				.forEachOrdered(it -> helper
						.addMaterial(materialList, it.getReturnedQuantity(), it.getReturnedUom(), it.getSapMaterial()));
		orderEvent.setReturnMaterials(materialList);

		helper.complete();

		return orderEvent;
	}

	public OrderEvent toDeliveryOrder(OrderEntity orderEntity, String type, Date time) {
		if (orderEntity == null) {
			return null;
		}

		OrderEvent orderEvent = new OrderEvent();
		Helper helper = new Helper();

		orderEvent.setHeader(toHeader(type, time));
		orderEvent.setOrder(toMinimalWithSoldToAndMobileOrder(orderEntity));
		orderEvent.setVehicle(toVehicle(orderEntity));
		orderEvent.setDelivery(toDelivery(orderEntity, helper));

		OrderEvent.MaterialList loadMaterials = new OrderEvent.MaterialList();

		orderEntity.getOrderItems().stream()
				.filter(it -> it.getLoadedQuantity() != null && !BigDecimal.ZERO.equals(it.getLoadedQuantity()))
				.forEachOrdered(
						it -> helper.addMaterial(loadMaterials, it.getLoadedQuantity(), it.getLoadedUom(), it.getSapMaterial()));

		OrderEvent.Load load = new OrderEvent.Load();
		load.setMaterials(loadMaterials.getMaterials());

		if (type.equals(Constant.EVENT_STARTLOADING_FR)) {
			orderEntity.getOrderItems()
					.stream()
					.filter(orderItemEntity -> orderItemEntity != null && orderItemEntity.getLoad() != null
							&& orderItemEntity.getLoad().getTareWeightT() != null)
					.findFirst().ifPresent(orderItem -> load.setTareWeight(orderItem.getLoad().getTareWeightT()));

		}
		orderEvent.setLoad(load);

		helper.complete();

		return orderEvent;
	}

	public String toStatusText(OrderEntity orderEntity) {
		if (orderEntity == null) {
			return null;
		}

		Long orderStatusCd = orderEntity.getOrderStatus().getOrderStatusCd();
		return Optional.ofNullable(
				refDataGenericsService.findTxEntity(OrderStatusTxEntity.class, DEFAULT_LOCALE, orderStatusCd))
				.map(OrderStatusTxEntity::getTxt).orElseGet(orderStatusCd::toString);
	}

	private OrderEvent.Order toOrder(OrderEntity orderEntity, Helper helper) {
		OrderEvent.Order order = toMinimalWithSoldToOrder(orderEntity);

		Long orderCreationTypeCd = orderEntity.getOrderCreationType().getOrderCreationTypeCd();
		order.setSource(refDataGenericsService.findTxEntity(OrderCreationTypeTxEntity.class, DEFAULT_LOCALE,
				orderCreationTypeCd).getTxt());

		order.setCreation(toCreation(orderEntity));
		order.setSalesOffice(toSalesOffice(orderEntity));
		order.setSalesDirection(toSalesDirection(orderEntity));
		order.setIncoterm(orderEntity.getSapContract().getIncotermsCd());

		orderEntity.getOrderItems()
				.forEach(it -> helper.addMaterial(order, it.getOrderedQuantity(), it.getOrderedUom(), it.getSapMaterial()));

		return order;
	}

	private static OrderEvent.Order toMinimalOrder(OrderEntity orderEntity) {
		OrderEvent.Order order = new OrderEvent.Order();

		order.setOrderNumber(orderEntity.getOrderNumber());

		return order;
	}

	private static OrderEvent.Order toMinimalWithSoldToOrder(OrderEntity orderEntity) {
		OrderEvent.Order order = toMinimalOrder(orderEntity);

		order.setSoldTo(toSoldTo(orderEntity));

		return order;
	}

	private static OrderEvent.Order toMinimalWithSoldToAndMobileOrder(OrderEntity orderEntity) {
		OrderEvent.Order order = toMinimalWithSoldToOrder(orderEntity);

		orderEntity.getOrderItems()
				.stream()
				.filter(orderItemEntity -> orderItemEntity != null && orderItemEntity.getLoad() != null
						&& orderItemEntity.getLoad().getTruck() != null)
				.findFirst().ifPresent(orderItem -> order.setMobileOrder(orderItem.getLoad().getTruck().getPdaAvailableFlag()));

		return order;
	}

	private OrderEvent.Delivery toMinimalDelivery(OrderEntity orderEntity) {
		SapBpEntity sapBp = orderEntity.getShiptoSapBp();
		ShiptoEntity shiptoEntity = dispokarteServiceBean.fetchShiptoBySapId(sapBp.getSapId());

		OrderEvent.Delivery delivery = new OrderEvent.Delivery();

		OrderEvent.Target shipTo = new OrderEvent.Target();
		shipTo.setCode(sapBp.getSapId());
		shipTo.setName(sapBp.getName1());
		if (shiptoEntity != null && shiptoEntity.getCoordLat() != null && shiptoEntity.getCoordLon() != null) {
			shipTo.setGeoPoint(new OrderEvent.GeoPoint(shiptoEntity.getCoordLat(), shiptoEntity.getCoordLon()));
		}
		shipTo.setAddress(toAddress(orderEntity));
		delivery.setShipTo(shipTo);

		return delivery;
	}

	private OrderEvent.Delivery toInitialDelivery(OrderEntity orderEntity) {
		OrderEvent.Delivery delivery = toMinimalDelivery(orderEntity);

		delivery.setFromDate(toUTCDateTime(orderEntity.getDeliveryFromDat()));
		delivery.setToDate(toUTCDateTime(orderEntity.getDeliveryToDat()));

		Long deliveryTypeCd = orderEntity.getDeliveryType().getDeliveryTypeCd();
		delivery.setType(Optional.ofNullable(
				refDataGenericsService.findTxEntity(DeliveryTypeTxEntity.class, DEFAULT_LOCALE, deliveryTypeCd))
				.map(DeliveryTypeTxEntity::getTxt).orElseGet(deliveryTypeCd::toString));

		delivery.setTransport(toTheoricalTransport(orderEntity));

		return delivery;
	}

	private OrderEvent.Delivery toDelivery(OrderEntity orderEntity, Helper helper) {
		OrderEvent.Delivery delivery = toMinimalDelivery(orderEntity);

		if (orderEntity.getPool() != null && orderEntity.getPool().getPoolId() != null) {
			Long poolId = orderEntity.getPool().getPoolId();
			delivery.setPool(poolAdminService.fetchPoolTxEntity(poolId, DEFAULT_LOCALE).getName());
		}

		orderEntity.getOrderItems().stream()
				.filter(it -> it.getDeliveredQuantity() != null && !BigDecimal.ZERO.equals(it.getDeliveredQuantity()))
				.forEachOrdered(it -> helper
						.addMaterial(delivery, it.getDeliveredQuantity(), it.getDeliveredUom(), it.getSapMaterial()));

		delivery.setTransport(toRealTransport(orderEntity));

		return delivery;
	}

	private static OrderEvent.SoldTo toSoldTo(OrderEntity orderEntity) {
		SapBpEntity sapBp = orderEntity.getSoldtoSapBp();

		OrderEvent.SoldTo soldTo = new OrderEvent.SoldTo();
		soldTo.setCode(sapBp.getSapId());
		soldTo.setName(sapBp.getName1());

		return soldTo;
	}

	private static OrderEvent.Target toShippingPoint(OrderEntity orderEntity) {
		ShippingpointEntity shippingpoint = orderEntity.getShippingpoint();
		if (shippingpoint == null) {
			return null;
		}

		OrderEvent.Target target = new OrderEvent.Target();

		target.setCode(shippingpoint.getSapShippingpointNo());
		target.setName(shippingpoint.getShippingpointName());
		if (shippingpoint.getCoordLat() != null && shippingpoint.getCoordLon() != null) {
			target.setGeoPoint(new OrderEvent.GeoPoint(shippingpoint.getCoordLat(), shippingpoint.getCoordLon()));
		}

		return target;
	}

	private static OrderEvent.Plant toPlant(OrderEntity orderEntity) {
		ShippingpointEntity shippingpoint = orderEntity.getShippingpoint();
		if (shippingpoint == null) {
			return null;
		}

		OrderEvent.Plant plant = new OrderEvent.Plant();

		plant.setCode(shippingpoint.getPlant().getPlantCd());
		plant.setName(shippingpoint.getPlant().getPlantName());
		plant.setShippingPoint(toShippingPoint(orderEntity));

		return plant;
	}

	private static OrderEvent.Address toAddress(OrderEntity orderEntity) {
		SapBpEntity sapBp = orderEntity.getShiptoSapBp();

		if (sapBp == null) {
			return null;
		}

		OrderEvent.Address address = new OrderEvent.Address();
		address.setCountry(sapBp.getCountry().getSapCountryCd());
		address.setRegion(sapBp.getRegion());
		address.setPostalCode(sapBp.getPostalCode());
		address.setCity(sapBp.getCity());

		return address;
	}

	private OrderEvent.Creation toCreation(OrderEntity orderEntity) {
		OrderEvent.Creation creation = new OrderEvent.Creation();

		creation.setDate(toUTCDateTime(orderEntity.getCtlCreTs()));
		creation.setUser(toUser(orderEntity));

		return creation;
	}

	private OrderEvent.User toUser(OrderEntity orderEntity) {
		Long userId = orderEntity.getCtlCreUid();
		PersonEntity personEntity = refDataGenericsService.findBsEntity(UserEntity.class, userId).getPerson();

		OrderEvent.User user = new OrderEvent.User();

		user.setFirstName(personEntity.getFirstname());
		user.setLastName(personEntity.getName());

		return user;
	}

	private static OrderEvent.Transport toTheoricalTransport(OrderEntity orderEntity) {
		OrderEvent.Duration duration = new OrderEvent.Duration();
		OrderEvent.Distance distance = new OrderEvent.Distance();

		duration.setTheoricalDurationMinutes(orderEntity.getTransportDurPlanGoogleMin());
		distance.setTheoricalDistanceMeters(orderEntity.getCommercialDistanceM());

		return new OrderEvent.Transport(duration, distance);
	}

	private static OrderEvent.Transport toRealTransport(OrderEntity orderEntity) {
		OrderEvent.Duration duration = new OrderEvent.Duration();
		OrderEvent.Distance distance = new OrderEvent.Distance();

		duration.setRealDurationMinutes(orderEntity.getTransportDurationMin());
		distance.setRealDistanceMeters(orderEntity.getTransportDistanceM());

		return new OrderEvent.Transport(duration, distance);
	}

	private BpSareaSofficeSgroupEntity getSareaSofficeSgroup(OrderEntity orderEntity) {
		SapBpEntity sapBp = orderEntity.getShiptoSapBp();

		if (sapBp == null) {
			return null;
		}
		Long sapBpId = sapBp.getSapBpId();
		Long sareaId = orderEntity.getSarea().getSareaId();

		Filter<BpSareaSofficeSgroupEntity> filter = new FilterImpl<>();
		filter.addExpression(Restrictions.eq(BpSareaSofficeSgroupEntity.ATTR_SAP_BP_ID, sapBpId));
		filter.addExpression(Restrictions.eq(BpSareaSofficeSgroupEntity.ATTR_SAREA_ID, sareaId));

		List<BpSareaSofficeSgroupEntity> result =
				referentialDataService.findBsEntityFiltered(BpSareaSofficeSgroupEntity.class, filter);

		if (result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}

	private OrderEvent.SalesOffice toSalesOffice(OrderEntity orderEntity) {
		OrderEvent.SalesOffice salesOffice = new OrderEvent.SalesOffice();
		BpSareaSofficeSgroupEntity bpSareaSofficeSgroupEntity = getSareaSofficeSgroup(orderEntity);

		if (bpSareaSofficeSgroupEntity != null) {
			salesOffice.setCode(bpSareaSofficeSgroupEntity.getSofficeCd());
			salesOffice.setName(bpSareaSofficeSgroupEntity.getSofficeName());
		}

		return salesOffice;
	}

	private OrderEvent.SalesDirection toSalesDirection(OrderEntity orderEntity) {
		OrderEvent.SalesDirection salesDirection = new OrderEvent.SalesDirection();
		salesDirection.setCode(Objects.requireNonNull(getSareaSofficeSgroup(orderEntity)).getSgroupCd());

		return salesDirection;
	}

	private OrderEvent.Vehicle toVehicle(OrderEntity orderEntity) {
		TrailerEntity trailer = orderEntity.getDeliveredTrailer();
		TruckEntity truck = orderEntity.getDeliveredTruck();
		CarrierEntity carrier = orderEntity.getExecCarrier();

		if (carrier == null && truck == null && trailer == null) {
			return null;
		}

		OrderEvent.Vehicle vehicle = new OrderEvent.Vehicle();

		if (truck != null) {
			vehicle.setTruckPlate(truck.getTruckPlateNo());
			// lat, lon
			Double[] coordinates = truckAdminService.fetchLastPosition(truck.getTruckId());
			if (coordinates[0] != null && coordinates[1] != null) {
				vehicle.setGeoPoint(new OrderEvent.GeoPoint(coordinates[0], coordinates[1]));
			}
		}

		if (trailer != null) {
			vehicle.setTrailerPlate(trailer.getTrailerPlateNo());
		}

		if (carrier != null) {
			vehicle.setCarrier(new OrderEvent.Carrier(carrier.getCarrierId(), carrier.getDisplayName()));
		}

		return vehicle;
	}

	private OrderEvent.Header toHeader(String type, Date time) {
		return new OrderEvent.Header(toUTCDateTime(time != null ? time.toInstant() : Instant.now()), type);
	}

	private static ZonedDateTime toUTCDateTime(Date date) {
		if (date == null) {
			return null;
		}
		return toUTCDateTime(date.toInstant());
	}

	private static ZonedDateTime toUTCDateTime(Instant instant) {
		return ZonedDateTime.ofInstant(instant, ZONE_UTC);
	}

	private class Helper {

		private final Map<Long, List<OrderEvent.Material>> materialMap = new LinkedHashMap<>();

		private final Map<Long, String> packageTypeMap = new HashMap<>();

		public void addMaterial(OrderEvent.MaterialList list, BigDecimal quantity, UomBsEntity uom,
				SapMaterialEntity sapMaterial) {
			OrderEvent.Material material = new OrderEvent.Material();
			material.setCode(sapMaterial.getSapMaterialNo());
			material.setQuantity(new OrderEvent.Quantity(quantity, uom.getUomCd()));

			MaterialPackageBsEntity materialPackage = sapMaterial.getMaterialPackage();
			if (materialPackage != null && materialPackage.getLogonPackageType() != null) {
				PackageTypeBsEntity logonPackageType = materialPackage.getLogonPackageType();
				material.setPackageType(packageTypeMap.computeIfAbsent(logonPackageType.getPackageTypeCd(),
						packageTypeCode -> Optional.ofNullable(
								refDataGenericsService.findTxEntity(PackageTypeTxEntity.class, DEFAULT_LOCALE, packageTypeCode))
								.map(PackageTypeTxEntity::getTxt).orElseGet(packageTypeCode::toString)));
			}

			materialMap.computeIfAbsent(sapMaterial.getSapMaterialId(), (x) -> new ArrayList<>()).add(material);

			list.addMaterial(material);
		}

		public void complete() {
			if (!materialMap.isEmpty()) {
				List<SapMaterialDescEntity> descEntities =
						dispokarteServiceBean.fetchSapMaterialDescByLang(new ArrayList<>(materialMap.keySet()),
								DEFAULT_LANGUAGE);
				descEntities.forEach(desc -> materialMap.get(desc.getSapMaterialId()).forEach(it -> it.setName(desc.getTxt())));
			}
		}
	}
}

