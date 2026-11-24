/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.heremap;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Stateless;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.common.base.ConfigReloadListener;
import com.crh.logon.business.common.http.RestHttpClientServiceBean;
import com.crh.logon.business.heremap.routing.ApiClient;
import com.crh.logon.business.heremap.routing.ApiException;
import com.crh.logon.business.heremap.routing.Pair;
import com.crh.logon.business.heremap.routing.api.RoutingApi;
import com.crh.logon.business.heremap.routing.model.ModelReturn;
import com.crh.logon.business.heremap.routing.model.RouterMode;
import com.crh.logon.business.heremap.routing.model.RouterRoute;
import com.crh.logon.business.heremap.routing.model.RouterRouteResponse;
import com.crh.logon.business.heremap.routing.model.RouterSection;
import com.crh.logon.business.heremap.routing.model.RoutingMode;
import com.crh.logon.business.heremap.routing.model.Spans;
import com.crh.logon.business.heremap.routing.model.Truck;
import com.crh.logon.business.heremap.routing.model.TruckType;
import com.crh.logon.business.heremap.routing.model.TunnelCategory;
import com.crh.logon.business.heremap.routing.model.Units;
import com.crh.logon.business.heremap.routing.model.VehicleDeparture;
import com.crh.logon.business.services.beans.UomConversionResult;
import com.crh.logon.business.services.interfaces.ConfigService;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UomConverterServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonConversionException;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.impl.client.CloseableHttpClient;

@Stateless
@AdnTrace
@LocalBean
public class RoutingServiceBean implements ConfigReloadListener {

	private static final BigDecimal TO_TO_KG = BigDecimal.valueOf(1000);

	private static final DateTimeFormatter DEPARTURE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("UTC"));

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RoutingServiceBean.class);

	// latitude,longitude
	private static final MessageFormat COORD_FORMATTER = new MessageFormat("{0,number,#.####},{1,number,#.####}");

	@EJB
	private ConfigService configService;

	@EJB
	private UomConverterServiceLocalInterface uomConverterService;

	@EJB
	private RefDataGenericsServiceLocalInterface referentialDataService;

	@EJB
	private RestHttpClientServiceBean restHttpClientServiceBean;

	private UomBsEntity kgUomEntity;

	private ApiClient apiClient;
	private RoutingApi routingApi;

	private volatile boolean disabled = true;

	private static <T> void addParam(List<Pair> pairs, String prefix, String fieldName, T value, Function<T, String> serializer) {
		if (value != null) {
			pairs.add(new Pair(prefix + '[' + fieldName + ']', serializer.apply(value)));
		}
	}

	private static <T> void addTruckParam(List<Pair> pairs, String fieldName, T value, Function<T, String> serializer) {
		addParam(pairs, "truck", fieldName, value, serializer);
	}

	@PostConstruct
	public void init() {
		kgUomEntity = referentialDataService.findBsEntity(UomBsEntity.class, Constant.UOM_KG);
		initRoutingApi();
	}

	@Override
	public void configReloaded() {
		initRoutingApi();
	}

	private void initRoutingApi() {
		final ConfigBean configBean = configService.getConfigBean();
		String hereMapApiKey = configBean.getHereMapApiKey();
		if (StringUtils.empty(hereMapApiKey)) {
			LOG.warn("HereMap API Key is missing, Routing is disabled");
			disabled = true;
			return;
		}

		if (apiClient == null) {
			CloseableHttpClient httpClient = restHttpClientServiceBean.getHttpClient();
			ObjectMapper objectMapper = restHttpClientServiceBean.getObjectMapper();
			apiClient = new ApiClient(httpClient, objectMapper) {

				@Override
				public List<Pair> parameterToPairs(String collectionFormat, String name, Object value) {
					if (!"truck".equals(name) || value == null) {
						return super.parameterToPairs(collectionFormat, name, value);
					}
					Truck truck = (Truck) value;
					ArrayList<Pair> pairs = new ArrayList<>();
					addTruckParam(pairs, Truck.JSON_PROPERTY_AXLE_COUNT, truck.getAxleCount(), Object::toString);
					addTruckParam(pairs, Truck.JSON_PROPERTY_GROSS_WEIGHT, truck.getGrossWeight(), Object::toString);
					addTruckParam(pairs, Truck.JSON_PROPERTY_HEIGHT, truck.getHeight(), Object::toString);
					addTruckParam(pairs, Truck.JSON_PROPERTY_LENGTH, truck.getLength(), Object::toString);
					addTruckParam(pairs, Truck.JSON_PROPERTY_SHIPPED_HAZARDOUS_GOODS, truck.getShippedHazardousGoods(), Function.identity());
					addTruckParam(pairs, Truck.JSON_PROPERTY_TRAILER_COUNT, truck.getTrailerCount(), Object::toString);
					addTruckParam(pairs, Truck.JSON_PROPERTY_TUNNEL_CATEGORY, truck.getTunnelCategory(), TunnelCategory::getValue);
					addTruckParam(pairs, Truck.JSON_PROPERTY_TYPE, truck.getType(), TruckType::getValue);
					addTruckParam(pairs, Truck.JSON_PROPERTY_WEIGHT_PER_AXLE, truck.getWeightPerAxle(), Object::toString);
					addTruckParam(pairs, Truck.JSON_PROPERTY_WIDTH, truck.getWidth(), Object::toString);

					return pairs;
				}
			};
		}
		if (routingApi == null) {
			routingApi = new RoutingApi(apiClient);
		}

		apiClient.setApiKey(hereMapApiKey);

		String basePath = configBean.getHereMapBasepathOverride();
		apiClient.setBasePath(basePath != null ? basePath : ApiClient.DEFAULT_BASEPATH);

		disabled = false;
	}

	@Lock(LockType.READ)
	public OffsetDateTime calculateArrivalTime(Origin origin, Target target, List<Via> via, AssignmentEntity assignmentEntity,
			OrderEntity orderEntity) {
		RouterRouteResponse route = route(origin, target, via, null, assignmentEntity, orderEntity, null, null);
		if (route.getRoutes().isEmpty()) {
			LOG.warn("Couldn't calculate route");
			return null;
		}
		RouterRoute routerRoute = route.getRoutes().get(0);
		// fetch the last section
		RouterSection lastSection = routerRoute.getSections().get(routerRoute.getSections().size() - 1);
		VehicleDeparture arrival = lastSection.getArrival();
		return arrival.getTime();
	}

	private RouterRouteResponse route(Origin origin, Target target, List<Via> via, Locale locale,
			AssignmentEntity assignmentEntity, OrderEntity orderEntity, Set<ModelReturn> returns, Set<Spans> spans) {
		if (disabled) {
			throw new LogonException("HereMap not configured", Reason.RES_ERROR_CONFIG_NOT_VALID);
		}

		List<String> viaParam =
				via != null && !via.isEmpty() ? via.stream().map(RoutingServiceBean::toCoords).collect(Collectors.toList())
						: null;
		String departureTime = origin.departure != null ? DEPARTURE_TIME_FORMATTER.format(origin.departure) : null;
		String lang = locale != null ? locale.getLanguage() : null;

		try {
			return routingApi.calculateRoutes(RouterMode.TRUCK, toCoords(origin), toCoords(target), viaParam, departureTime,
					RoutingMode.FAST, 0, null, null, Units.METRIC, lang, returns, spans, toTruck(assignmentEntity, orderEntity),
					null, null, null, null, null);
		}
		catch (ApiException e) {
			throw new LogonException("Couldn't call HereMap: " + e.getCode() + ", " + e.getResponseBody(), e);
		}
	}

	private Truck toTruck(AssignmentEntity assignmentEntity, OrderEntity orderEntity) {
		if (assignmentEntity == null) {
			return null;
		}

		// planning trucks are ignored as they are not real and shouldn't drive on the roads
		TruckEntity truckEntity = assignmentEntity.getTruck();
		if (truckEntity == null) {
			return null;
		}

		BigDecimal grossWeight = truckEntity.getTareWeightT().multiply(TO_TO_KG); // in Kg
		Integer axelCount = null;
		Truck truck = new Truck();

		// axel
		if (truckEntity.getAxisCount() != null) {
			axelCount = Integer.parseInt(truckEntity.getAxisCount());
		}

		try {

			if (orderEntity != null) {
				for (OrderItemEntity orderItem : orderEntity.getOrderItems()) {
					String materialPackageCd = orderItem.getSapMaterial().getMaterialPackage().getMaterialPackageCd();
					if (!Constant.MATERIAL_PACKAGE_BULK_POWDER.equals(materialPackageCd) && !Constant.MATERIAL_PACKAGE_BULK_DUMPED
							.equals(materialPackageCd)) {
						continue;
					}
					// material for single order
					UomConversionResult conversionResult = uomConverterService
							.convert(orderItem.getLoadedQuantity() != null ? orderItem.getLoadedQuantity() : orderItem.getOrderedQuantity(),
									orderItem.getLoadedUom() != null ? orderItem.getLoadedUom() : orderItem.getOrderedUom(),
									kgUomEntity, orderItem.getSapMaterial(), truckEntity.getTruckType(), null, true);
					grossWeight = grossWeight.add(conversionResult.getQuantity());
				}
			}

			if (assignmentEntity.getMaterialQuantity() != null && assignmentEntity.getMaterialUom() != null) {
				// ext order
				UomConversionResult conversionResult = uomConverterService
						.convert(assignmentEntity.getMaterialQuantity(), assignmentEntity.getMaterialUom(), kgUomEntity, null,
								truckEntity.getTruckType(), null, true);
				grossWeight = grossWeight.add(conversionResult.getQuantity());
			}
			else {
				// just in case there are multiple trailers somehow in the assignment...
				Set<Long> addedTrailerIds = new HashSet<>();
				Set<Long> addedContainerIds = new HashSet<>();
				// normal orders
				for (AssignmentPartEntity part : assignmentEntity.getAssignmentParts()) {
					LoadEntity load = part.getLoad();
					if (load != null) {
						// calculate composition weight

						// trailer
						TrailerEntity trailer = load.getTrailer();
						if (trailer != null) {
							if (axelCount != null && trailer.getAxisCount() != null) {
								axelCount += Integer.parseInt(trailer.getAxisCount());
							}
							if (!addedTrailerIds.contains(trailer.getTrailerId())) {
								grossWeight = grossWeight.add(trailer.getTareWeightT().multiply(TO_TO_KG));
								addedTrailerIds.add(trailer.getTrailerId());
							}
						}
						// container
						ContainerEntity container = load.getContainer();
						if (container != null && !addedContainerIds.contains(container.getContainerId())) {
							grossWeight = grossWeight.add(container.getTareWeightT().multiply(TO_TO_KG));
							addedContainerIds.add(container.getContainerId());
						}

						// if load is not yet loaded, we cannot easily fetch the ordered quantity
					}
				}
				truck.setTrailerCount(addedTrailerIds.size());
				truck.setType(addedTrailerIds.isEmpty() ? TruckType.STRAIGHT : TruckType.TRACTOR);
			}
		}
		catch (LogonConversionException e) {
			LOG.error("Couldn't convert the quantity, skip the truck information", e);
			return null;
		}

		truck.setAxleCount(axelCount);
		truck.setGrossWeight(grossWeight.setScale(0, RoundingMode.CEILING).intValue());

		return truck;
	}

	private static String toCoords(Origin origin) {
		return toCoords(origin, origin.course, null);
	}

	private static String toCoords(Target target) {
		return toCoords(target, null, null);
	}

	private static String toCoords(Via via) {
		return toCoords(via, null, via.stopDuration);
	}

	private static String toCoords(Coords coords, Long course, Long stopDurationSeconds) {
		String sb = COORD_FORMATTER.format(new Object[] { coords.lat, coords.lon });
		if (course != null) {
			sb += ";course=" + course;
		}
		if (stopDurationSeconds != null) {
			sb += "!stopDuration=" + stopDurationSeconds;
		}
		return sb;
	}

	private static class Coords {

		private final Double lat;

		private final Double lon;

		public Coords(Double lat, Double lon) {
			if (lat == null) {
				throw new IllegalArgumentException("lat cannot be null");
			}
			if (lon == null) {
				throw new IllegalArgumentException("lon cannot be null");
			}
			this.lat = lat;
			this.lon = lon;
		}
	}

	public static class Origin extends Coords {

		private final OffsetDateTime departure;

		private final Long course;

		public Origin(Double lat, Double lon, OffsetDateTime departure, Long course) {
			super(lat, lon);
			this.departure = departure;
			this.course = course;
		}
	}

	public static class Target extends Coords {

		public Target(Double lat, Double lon) {
			super(lat, lon);
		}
	}

	public static class Via extends Coords {

		private final Long stopDuration;

		public Via(Double lat, Double lon, Long stopDuration) {
			super(lat, lon);
			this.stopDuration = stopDuration;
		}
	}
}
