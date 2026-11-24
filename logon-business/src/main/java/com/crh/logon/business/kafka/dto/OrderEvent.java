/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.kafka.dto;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderEvent implements KafkaDTO {

	private Header header;

	private Order order;

	private Delivery delivery;

	@JsonProperty("return")
	private MaterialList returnMaterials;

	private Load load;

	private Plant plant;

	private Vehicle vehicle;

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Delivery getDelivery() {
		return delivery;
	}

	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}

	public MaterialList getReturnMaterials() {
		return returnMaterials;
	}

	public void setReturnMaterials(MaterialList returnMaterials) {
		this.returnMaterials = returnMaterials;
	}

	public Load getLoad() {
		return load;
	}

	public void setLoad(Load load) {
		this.load = load;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public Plant getPlant() { return plant; }

	public void setPlant(Plant plant) { this.plant = plant; }

	public static class Order extends MaterialList {

		private Long orderNumber;

		private String incoterm;

		private SoldTo soldTo;

		private Boolean isMobileOrder;

		private SalesOffice salesOffice;

		private SalesDirection salesDirection;

		private String source;

		private Creation creation;

		public Long getOrderNumber() {
			return orderNumber;
		}

		public void setOrderNumber(Long orderNumber) {
			this.orderNumber = orderNumber;
		}

		public String getIncoterm() {
			return incoterm;
		}

		public void setIncoterm(String incoterm) {
			this.incoterm = incoterm;
		}

		public SoldTo getSoldTo() {
			return soldTo;
		}

		public void setSoldTo(SoldTo soldTo) {
			this.soldTo = soldTo;
		}

		public Boolean isMobileOrder() { return isMobileOrder; }

		public void setMobileOrder(Boolean mobileOrder) { isMobileOrder = mobileOrder; }

		public SalesOffice getSalesOffice() { return salesOffice; }

		public void setSalesOffice(SalesOffice salesOffice) { this.salesOffice = salesOffice; }

		public SalesDirection getSalesDirection() { return salesDirection; }

		public void setSalesDirection(SalesDirection salesDirection) { this.salesDirection = salesDirection; }

		public Creation getCreation() { return creation; }

		public void setCreation(Creation creation) { this.creation = creation; }

		public String getSource() { return source; }

		public void setSource(String source) { this.source = source; }
	}

	public static class Delivery extends MaterialList {

		private ZonedDateTime fromDate;

		private ZonedDateTime toDate;

		private String type;

		private Target shipTo;

		private String pool;

		private Transport transport;

		public ZonedDateTime getFromDate() {
			return fromDate;
		}

		public void setFromDate(ZonedDateTime fromDate) {
			this.fromDate = fromDate;
		}

		public ZonedDateTime getToDate() {
			return toDate;
		}

		public void setToDate(ZonedDateTime toDate) {
			this.toDate = toDate;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Target getShipTo() {
			return shipTo;
		}

		public void setShipTo(Target shipTo) {
			this.shipTo = shipTo;
		}

		public String getPool() { return pool; }

		public void setPool(String pool) { this.pool = pool; }

		public Transport getTransport() {
			return transport;
		}

		public void setTransport(Transport transport) {
			this.transport = transport;
		}
	}

	public static class Quantity {

		private BigDecimal value;

		private String unitOfMeasure;

		public Quantity() {
		}

		public Quantity(BigDecimal value, String unitOfMeasure) {
			this.value = value;
			this.unitOfMeasure = unitOfMeasure;
		}

		public BigDecimal getValue() {
			return value;
		}

		public void setValue(BigDecimal value) {
			this.value = value;
		}

		public String getUnitOfMeasure() {
			return unitOfMeasure;
		}

		public void setUnitOfMeasure(String unitOfMeasure) {
			this.unitOfMeasure = unitOfMeasure;
		}
	}

	public static class Load extends MaterialList {

		private BigDecimal tareWeight;

		public BigDecimal getTareWeight() {
			return tareWeight;
		}

		public void setTareWeight(BigDecimal tareWeight) {
			this.tareWeight = tareWeight;
		}
	}

	public static class MaterialList {

		private List<Material> materials;

		public List<Material> getMaterials() {
			return materials;
		}

		public void setMaterials(List<Material> materials) {
			this.materials = materials;
		}

		public void addMaterial(Material material) {
			if (materials == null) {
				materials = new ArrayList<>();
			}
			materials.add(material);
		}
	}

	public static class Material {

		private String code;

		private String name;

		@JsonProperty("package")
		private String packageType;

		private Quantity quantity;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public String getPackageType() {
			return packageType;
		}

		public void setPackageType(String packageType) {
			this.packageType = packageType;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Quantity getQuantity() {
			return quantity;
		}

		public void setQuantity(Quantity quantity) {
			this.quantity = quantity;
		}
	}

	public static class SoldTo {

		private String code;

		private String name;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public static class Target extends ShippingPoint {

		private Address address;

		public Address getAddress() { return address; }

		public void setAddress(Address address) { this.address = address; }
	}

	public static class GeoPoint {

		private static final MathContext COORD_PRECISION = new MathContext(4);

		private BigDecimal latitude;

		private BigDecimal longitude;

		public GeoPoint(Double latitude, Double longitude) {
			this.latitude = new BigDecimal(latitude, COORD_PRECISION);
			this.longitude = new BigDecimal(longitude, COORD_PRECISION);
		}

		public BigDecimal getLatitude() {
			return latitude;
		}

		public void setLatitude(BigDecimal latitude) {
			this.latitude = latitude;
		}

		public BigDecimal getLongitude() {
			return longitude;
		}

		public void setLongitude(BigDecimal longitude) {
			this.longitude = longitude;
		}
	}

	public static class Vehicle {

		private String truckPlate;

		private String trailerPlate;

		private Carrier carrier;

		private GeoPoint geoPoint;

		public String getTruckPlate() {
			return truckPlate;
		}

		public void setTruckPlate(String truckPlate) {
			this.truckPlate = truckPlate;
		}

		public String getTrailerPlate() {
			return trailerPlate;
		}

		public void setTrailerPlate(String trailerPlate) {
			this.trailerPlate = trailerPlate;
		}

		public Carrier getCarrier() {
			return carrier;
		}

		public void setCarrier(Carrier carrier) {
			this.carrier = carrier;
		}

		public GeoPoint getGeoPoint() {
			return geoPoint;
		}

		public void setGeoPoint(GeoPoint geoPoint) {
			this.geoPoint = geoPoint;
		}
	}

	public static class Carrier {

		private Long code;

		private String name;

		public Carrier(Long code, String name) {
			this.code = code;
			this.name = name;
		}

		public Long getCode() {
			return code;
		}

		public void setCode(Long code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public static class Header {

		private UUID id;

		private ZonedDateTime time;

		private String type;

		public Header(ZonedDateTime time, String type) {
			this.id = UUID.randomUUID();
			this.time = time;
			this.type = type;
		}

		public UUID getId() {
			return id;
		}

		public void setId(UUID id) {
			this.id = id;
		}

		public ZonedDateTime getTime() {
			return time;
		}

		public void setTime(ZonedDateTime time) {
			this.time = time;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getSource() {
			return "Logon";
		}

	}

	public static class Plant {

		private String code;

		private String name;

		private ShippingPoint shippingPoint;

		public String getCode() { return code; }

		public void setCode(String code) { this.code = code; }

		public String getName() { return name; }

		public void setName(String name) { this.name = name; }

		public ShippingPoint getShippingPoint() { return shippingPoint; }

		public void setShippingPoint(ShippingPoint shippingPoint) {
			this.shippingPoint = shippingPoint;
		}
	}

	public static class ShippingPoint{

		private String code;

		private String name;

		private GeoPoint geoPoint;

		public String getCode() { return code; }

		public void setCode(String code) { this.code = code; }

		public String getName() { return name; }

		public void setName(String name) { this.name = name; }

		public GeoPoint getGeoPoint() { return geoPoint; }

		public void setGeoPoint(GeoPoint geoPoint) { this.geoPoint = geoPoint; }
	}

	public static class Address {

		private String country;

		private String region;

		private String postalCode;

		private String city;

		public String getCountry() { return country; }

		public void setCountry(String country) { this.country = country; }

		public String getRegion() { return region; }

		public void setRegion(String region) { this.region = region; }

		public String getPostalCode() { return postalCode; }

		public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

		public String getCity() { return city; }

		public void setCity(String city) { this.city = city; }
	}

	public static class SalesOffice {

		private String code;

		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCode() { return code; }

		public void setCode(String code) { this.code = code; }
	}

	public static class SalesDirection {

		private String code;

		public String getCode() { return code; }

		public void setCode(String code) { this.code = code; }
	}

	public static class Creation {

		private ZonedDateTime date;

		private User user;

		public ZonedDateTime getDate() { return date; }

		public void setDate(ZonedDateTime date) { this.date = date; }

		public User getUser() { return user; }

		public void setUser(User user) { this.user = user; }
	}

	public static class User {

		private String firstName;

		private String lastName;

		public String getFirstName() { return firstName; }

		public void setFirstName(String firstName) { this.firstName = firstName; }

		public String getLastName() { return lastName; }

		public void setLastName(String lastName) { this.lastName = lastName; }
	}

	public static class Duration {

		private Long theoricalDurationMinutes;

		private Long realDurationMinutes;

		public Long getTheoricalDurationMinutes() {
			return theoricalDurationMinutes;
		}

		public void setTheoricalDurationMinutes(Long theoricalDurationMinutes) {
			this.theoricalDurationMinutes = theoricalDurationMinutes;
		}

		public Long getRealDurationMinutes() {
			return realDurationMinutes;
		}

		public void setRealDurationMinutes(Long realDurationMinutes) {
			this.realDurationMinutes = realDurationMinutes;
		}
	}

	public static class Distance {

		private Long theoricalDistanceMeters;

		private Long realDistanceMeters;

		public Long getTheoricalDistanceMeters() {
			return theoricalDistanceMeters;
		}

		public void setTheoricalDistanceMeters(Long theoricalDistanceMeters) {
			this.theoricalDistanceMeters = theoricalDistanceMeters;
		}

		public Long getRealDistanceMeters() {
			return realDistanceMeters;
		}

		public void setRealDistanceMeters(Long realDistanceMeters) {
			this.realDistanceMeters = realDistanceMeters;
		}
	}

	public static class Transport {

		private Duration duration;

		private Distance distance;

		public Transport(Duration duration, Distance distance) {
			this.duration = duration;
			this.distance = distance;
		}

		public Duration getDuration() {
			return duration;
		}

		public void setDuration(Duration duration) {
			this.duration = duration;
		}

		public Distance getDistance() {
			return distance;
		}

		public void setDistance(Distance distance) {
			this.distance = distance;
		}
	}
}
