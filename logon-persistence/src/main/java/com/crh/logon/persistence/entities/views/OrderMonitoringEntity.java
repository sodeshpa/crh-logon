/*
 * Author			: AdNovum Informatik AG
 * Version Number	: $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.persistence.entities.views;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.gdi.jpa.BaseEntity;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;



@SuppressWarnings("unused")
@Entity
@Table(name = "VLGNC_ORDER_MONITORING")
public class OrderMonitoringEntity implements BaseEntity {
	private static final String GDI_JPA_GENERATOR_MARKER_VIEW = "VIEW_MARKER";
	private static final JcanLogger LOG_ENTITIES = JcanLoggerFactory.getLogger("persistence.entities");
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "VLGNC_ORDER_MONITORING";

	@Id
	@Column(name = "COMPOSITE_ID")
	private String compositeId;

	@Column(name = "ORDER_ID")
	private Long orderId;

	@Column(name = "ORDER_CREATION_TYPE_CD")
	private Long orderCreationTypeCd;

	@Column(name = "SHIPTO_SAP_BP_ID")
	private Long shiptoSapBpId;

	@Column(name = "SOLDTO_SAP_BP_ID")
	private Long soldtoSapBpId;

	@Column(name = "BILLTO_SAP_BP_ID")
	private Long billtoSapBpId;

	@Column(name = "SAREA_ID")
	private Long sareaId;

	@Column(name = "SAP_MATERIAL_ID")
	private Long sapMaterialId;

	@Column(name = "ORDER_NUMBER")
	private Long orderNumber;

	@Column(name = "LDS_DELIVERY_NOTE_NO")
    private String ldsDeliveryNoteNo;
    
    @Column(name = "NOTE_ORDER_MGMT")
    private String noteOrderMgmt;

    @Column(name = "NOTE_DELIVERY")
    private String noteDelivery;

    @Column(name = "NOTE_DISPATCHING")
    private String noteDispatching;

    @Column(name = "NOTE_TRUCK_DRIVER")
    private String noteTruckDriver;

    @Column(name = "NOTE_RAILWAY_DISP")
    private String noteRailwayDisp;
    
    @Column(name = "ORDER_NUMBER_FROM_SEQ_USAGE")
    private String orderNumberFromSeqUsage;
    
    @Column(name = "SHIPTO_ADDRESS")
    private String shiptoAddress;
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LOADING_TS")
    private Date loadingTs;

    @Column(name = "ORDER_ITEM_NO")
	private Long orderItemNo;

	@Column(name = "ORDERED_UOM_TXT")
	private String orderedUomTxt;

	@Column(name = "ORDERED_QUANTITY")
	private java.math.BigDecimal orderedQuantity;

	@Column(name = "LOADED_UOM_TXT")
	private String loadedUomTxt;

	@Column(name = "LOADED_QUANTITY")
	private java.math.BigDecimal loadedQuantity;

	@Column(name = "DELIVERED_UOM_TXT")
	private String deliveredUomTxt;

	@Column(name = "DELIVERED_QUANTITY")
	private java.math.BigDecimal deliveredQuantity;

	@Column(name = "RETURNED_UOM_TXT")
	private String returnedUomTxt;

	@Column(name = "RETURNED_QUANTITY")
	private java.math.BigDecimal returnedQuantity;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DELIVERY_FROM_DAT")
	private Date deliveryFromDat;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DELIVERY_TO_DAT")
	private Date deliveryToDat;

	@Column(name = "DISTRIBUTION_DEST_CD")
	private String distributionDestCd;

	@Column(name = "ORDER_STATUS_CD")
	private Long orderStatusCd;

	@Column(name = "DELIVERY_TYPE_CD")
	private Long deliveryTypeCd;

	@Column(name = "SHIPPINGPOINT_ID")
	private Long shippingpointId;

	@Column(name = "POOL_ID")
	private Long poolId;

	@Column(name = "COMM_CARRIER_ID")
	private Long commCarrierId;

	@Column(name = "EXEC_CARRIER_ID")
    private Long execCarrierId;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PLANNED_DELIVERY_DAT")
	private Date plannedDeliveryDat;

	@Column(name = "ORDER_VALIDATION_CODE")
	private String orderValidationCode;

	@Column(name = "SOLDTO_NAME1")
	private String soldtoName1;

	@Column(name = "SHIPTO_NAME1")
	private String shiptoName1;

	@Column(name = "SHIPTO_CITY")
	private String shiptoCity;

	@Column(name = "BILLTO_NAME1")
	private String billtoName1;

	@Column(name = "SHIPTO_SGROUP_CD")
	private String shiptoSgroupCd;

	@Column(name = "SHIPTO_SOFFICE_CD")
	private String shiptoSofficeCd;

	@Column(name = "LANG_CD")
	private String langCd;

	@Column(name = "STATUS_TXT")
	private String statusTxt;

	@Column(name = "WARN_STATUS_CODE")
	private Long warnStatusCode;

	@Column(name = "TIME_TO_RED_LINE")
	private Long timeToRedLine;

	@Column(name = "POOL_NAME")
	private String poolName;

	@Column(name = "COMM_CARRIER_NAME")
	private String commCarrierName;

	@Column(name = "EXEC_CARRIER_NAME")
	private String execCarrierName;

	@Column(name = "SAP_VENDOR_ID")
	private Long sapVendorId;

	@Column(name = "PLANT_CD")
	private String plantCd;

	@Column(name = "SHIPPINGPOINT_NAME")
	private String shippingpointName;

	@Column(name = "MATERIAL_TXT")
	private String materialTxt;

	@Column(name = "DELIVERY_TYPE_TXT")
	private String deliveryTypeTxt;

	@Column(name = "COMPANYCODE_CD")
	private String companycodeCd;

	@Column(name = "DRIVER_FULL_NAME")
	private String driverFullName;

	@Column(name = "VEHICLE_NAME")
	private String vehicleName;

	@Column(name = "MATERIAL_PACKAGE_CD")
	private String materialPackageCd;

	@Column(name = "SHIPTO_TIMEZONE_CD")
	private String shiptoTimezoneCd;


	// Fieldnames
	public static final String ATTR_COMPOSITE_ID = "compositeId";
	public static final String ATTR_ORDER_ID = "orderId";
	public static final String ATTR_ORDER_CREATION_TYPE_CD = "orderCreationTypeCd";
	public static final String ATTR_SHIPTO_SAP_BP_ID = "shiptoSapBpId";
	public static final String ATTR_SOLDTO_SAP_BP_ID = "soldtoSapBpId";
	public static final String ATTR_BILLTO_SAP_BP_ID = "billtoSapBpId";
	public static final String ATTR_SAREA_ID = "sareaId";
	public static final String ATTR_SAP_MATERIAL_ID = "sapMaterialId";
	public static final String ATTR_ORDER_NUMBER = "orderNumber";
	public static final String ATTR_ORDER_ITEM_NO = "orderItemNo";
	public static final String ATTR_ORDERED_UOM_TXT = "orderedUomTxt";
	public static final String ATTR_ORDERED_QUANTITY = "orderedQuantity";
	public static final String ATTR_LOADED_UOM_TXT = "loadedUomTxt";
	public static final String ATTR_LOADED_QUANTITY = "loadedQuantity";
	public static final String ATTR_DELIVERED_UOM_TXT = "deliveredUomTxt";
	public static final String ATTR_DELIVERED_QUANTITY = "deliveredQuantity";
	public static final String ATTR_RETURNED_UOM_TXT = "returnedUomTxt";
	public static final String ATTR_RETURNED_QUANTITY = "returnedQuantity";
	public static final String ATTR_DELIVERY_FROM_DAT = "deliveryFromDat";
	public static final String ATTR_DELIVERY_TO_DAT = "deliveryToDat";
	public static final String ATTR_DISTRIBUTION_DEST_CD = "distributionDestCd";
	public static final String ATTR_ORDER_STATUS_CD = "orderStatusCd";
	public static final String ATTR_DELIVERY_TYPE_CD = "deliveryTypeCd";
	public static final String ATTR_SHIPPINGPOINT_ID = "shippingpointId";
	public static final String ATTR_POOL_ID = "poolId";
	public static final String ATTR_COMM_CARRIER_ID = "commCarrierId";
	public static final String ATTR_EXEC_CARRIER_ID = "execCarrierId";
	public static final String ATTR_PLANNED_DELIVERY_DAT = "plannedDeliveryDat";
	public static final String ATTR_ORDER_VALIDATION_CODE = "orderValidationCode";
	public static final String ATTR_SOLDTO_NAME1 = "soldtoName1";
	public static final String ATTR_SHIPTO_NAME1 = "shiptoName1";
	public static final String ATTR_SHIPTO_CITY = "shiptoCity";
	public static final String ATTR_BILLTO_NAME1 = "billtoName1";
	public static final String ATTR_SHIPTO_SGROUP_CD = "shiptoSgroupCd";
	public static final String ATTR_SHIPTO_SOFFICE_CD = "shiptoSofficeCd";
	public static final String ATTR_LANG_CD = "langCd";
	public static final String ATTR_STATUS_TXT = "statusTxt";
	public static final String ATTR_WARN_STATUS_CODE = "warnStatusCode";
	public static final String ATTR_TIME_TO_RED_LINE = "timeToRedLine";
	public static final String ATTR_EXEC_CARRIER_NAME = "execCarrierName";
	public static final String ATTR_COMM_CARRIER_NAME = "commCarrierName";
	public static final String ATTR_SAP_VENDOR_ID = "sapVendorId";
	public static final String ATTR_PLANT_CD = "plantCd";
	public static final String ATTR_SHIPPINGPOINT_NAME = "shippingpointName";
	public static final String ATTR_MATERIAL_TXT = "materialTxt";
	public static final String ATTR_DELIVERY_TYPE_TXT = "deliveryTypeTxt";
	public static final String ATTR_COMPANYCODE_CD = "companycodeCd";
	public static final String ATTR_DRIVER_FULL_NAME = "driverFullName";
	public static final String ATTR_VEHICLE_NAME = "vehicleName";
	public static final String ATTR_MATERIAL_PACKAGE_CD = "materialPackageCd";
	public static final String ATTR_SHIPTO_TIMEZONE_CD = "shiptoTimezoneCd";
    public static final String ATTR_LDS_DELIVERY_NOTE_NO = "ldsDeliveryNoteNo";
    public static final String ATTR_NOTE_ORDER_MGMT = "noteOrderMgmt";
    public static final String ATTR_NOTE_DELIVERY = "noteDelivery";
    public static final String ATTR_NOTE_DISPATCHING = "noteDispatching";
    public static final String ATTR_NOTE_TRUCK_DRIVER = "noteTruckDriver";
    public static final String ATTR_NOTE_RAILWAY_DISP = "noteRailwayDisp";
    public static final String ATTR_ORDER_NUMBER_FROM_SEQ_USAGE = "orderNumberFromSeqUsage";
    public static final String ATTR_SHIPTO_ADDRESS = "shiptoAddress";
    public static final String ATTR_LOADING_TS = "loadingTs";
    
	// Field length on DB
	public static final long DBSIZE_COMPOSITE_ID = 45;
	public static final long DBSIZE_ORDER_ID = 12;
	public static final long DBSIZE_ORDER_CREATION_TYPE_CD = 2;
	public static final long DBSIZE_SHIPTO_SAP_BP_ID = 12;
	public static final long DBSIZE_SOLDTO_SAP_BP_ID = 12;
	public static final long DBSIZE_BILLTO_SAP_BP_ID = 12;
	public static final long DBSIZE_SAREA_ID = 12;
	public static final long DBSIZE_SAP_MATERIAL_ID = 12;
	public static final long DBSIZE_ORDER_NUMBER = 12;
	public static final long DBSIZE_ORDER_ITEM_NO = 3;
	public static final long DBSIZE_ORDERED_UOM_TXT = 20;
	public static final long DBSIZE_ORDERED_QUANTITY = 12;
	public static final long DBSIZE_PRECISION_ORDERED_QUANTITY = 4;
	public static final long DBSIZE_LOADED_UOM_TXT = 20;
	public static final long DBSIZE_LOADED_QUANTITY = 12;
	public static final long DBSIZE_PRECISION_LOADED_QUANTITY = 4;
	public static final long DBSIZE_DELIVERED_UOM_TXT = 20;
	public static final long DBSIZE_DELIVERED_QUANTITY = 12;
	public static final long DBSIZE_PRECISION_DELIVERED_QUANTITY = 4;
	public static final long DBSIZE_RETURNED_UOM_TXT = 20;
	public static final long DBSIZE_RETURNED_QUANTITY = 12;
	public static final long DBSIZE_PRECISION_RETURNED_QUANTITY = 4;
	public static final long DBSIZE_DISTRIBUTION_DEST_CD = 4;
	public static final long DBSIZE_ORDER_STATUS_CD = 2;
	public static final long DBSIZE_DELIVERY_TYPE_CD = 2;
	public static final long DBSIZE_SHIPPINGPOINT_ID = 12;
	public static final long DBSIZE_POOL_ID = 12;
	public static final long DBSIZE_COMM_CARRIER_ID = 12;
    public static final long DBSIZE_EXEC_CARRIER_ID = 12;
	public static final long DBSIZE_ORDER_VALIDATION_CODE = 2;
	public static final long DBSIZE_SOLDTO_NAME1 = 35;
	public static final long DBSIZE_SHIPTO_NAME1 = 35;
	public static final long DBSIZE_SHIPTO_CITY = 35;
	public static final long DBSIZE_BILLTO_NAME1 = 35;
	public static final long DBSIZE_SHIPTO_SGROUP_CD = 4;
	public static final long DBSIZE_SHIPTO_SOFFICE_CD = 4;
	public static final long DBSIZE_LANG_CD = 2;
	public static final long DBSIZE_STATUS_TXT = 50;
	public static final long DBSIZE_WARN_STATUS_CODE = 2;
	public static final long DBSIZE_TIME_TO_RED_LINE = 12;
	public static final long DBSIZE_COMM_CARRIER_NAME = 40;
	public static final long DBSIZE_EXEC_CARRIER_NAME = 40;
	public static final long DBSIZE_SAP_VENDOR_ID = 12;
	public static final long DBSIZE_PLANT_CD = 4;
	public static final long DBSIZE_SHIPPINGPOINT_NAME = 80;
	public static final long DBSIZE_MATERIAL_TXT = 40;
	public static final long DBSIZE_DELIVERY_TYPE_TXT = 40;
	public static final long DBSIZE_COMPANYCODE_CD = 4;
	public static final long DBSIZE_DRIVER_FULL_NAME = 81;
	public static final long DBSIZE_VEHICLE_NAME = 30;
	public static final long DBSIZE_MATERIAL_PACKAGE_CD = 4;
	public static final long DBSIZE_SHIPTO_TIMEZONE_CD = 32;
    public static final long DBSIZE_LDS_DELIVERY_NOTE_NO = 12;
    public static final long DBSIZE_NOTE_ORDER_MGMT = 256;
    public static final long DBSIZE_NOTE_DELIVERY = 300;
    public static final long DBSIZE_NOTE_DISPATCHING = 256;
    public static final long DBSIZE_NOTE_TRUCK_DRIVER = 256;
    public static final long DBSIZE_NOTE_RAILWAY_DISP = 256;
    public static final long DBSIZE_SHIPTO_ADDRESS = 256;
    public static final long DBSIZE_ORDER_NUMBER_FROM_SEQ_USAGE = 12;
    
	/**
	 * COMPOSITE_ID is nullable
	 * Field length on DB: 45
	 */
	public void setCompositeId(String compositeId) {
		this.compositeId = compositeId;
	}

	/**
	 * COMPOSITE_ID is nullable
	 * Field length on DB: 45
	 */
	public String getCompositeId() {
		return compositeId;
	}


	/**
	 * ORDER_ID is NOT nullable
	 * Field length on DB: 12
	 */
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	/**
	 * ORDER_ID is NOT nullable
	 * Field length on DB: 12
	 */
	public Long getOrderId() {
		return orderId;
	}

	/**
	 * ORDER_CREATION_TYPE_CD is NOT nullable
	 * Field length on DB: 2
	 */
	public void setOrderCreationTypeCd(Long orderCreationTypeCd) {
		this.orderCreationTypeCd = orderCreationTypeCd;
	}

	/**
	 * ORDER_CREATION_TYPE_CD is NOT nullable
	 * Field length on DB: 2
	 */
	public Long getOrderCreationTypeCd() {
		return orderCreationTypeCd;
	}

	/**
	 * SHIPTO_SAP_BP_ID is NOT nullable
	 * Field length on DB: 12
	 */
	public void setShiptoSapBpId(Long shiptoSapBpId) {
		this.shiptoSapBpId = shiptoSapBpId;
	}

	/**
	 * SHIPTO_SAP_BP_ID is NOT nullable
	 * Field length on DB: 12
	 */
	public Long getShiptoSapBpId() {
		return shiptoSapBpId;
	}

	/**
	 * SOLDTO_SAP_BP_ID is NOT nullable
	 * Field length on DB: 12
	 */
	public void setSoldtoSapBpId(Long soldtoSapBpId) {
		this.soldtoSapBpId = soldtoSapBpId;
	}

	/**
	 * SOLDTO_SAP_BP_ID is NOT nullable
	 * Field length on DB: 12
	 */
	public Long getSoldtoSapBpId() {
		return soldtoSapBpId;
	}

	/**
	 * BILLTO_SAP_BP_ID is nullable
	 * Field length on DB: 12
	 */
	public void setBilltoSapBpId(Long billtoSapBpId) {
		this.billtoSapBpId = billtoSapBpId;
	}

	/**
	 * BILLTO_SAP_BP_ID is nullable
	 * Field length on DB: 12
	 */
	public Long getBilltoSapBpId() {
		return billtoSapBpId;
	}

	/**
	 * SAREA_ID is NOT nullable
	 * Field length on DB: 12
	 */
	public void setSareaId(Long sareaId) {
		this.sareaId = sareaId;
	}

	/**
	 * SAREA_ID is NOT nullable
	 * Field length on DB: 12
	 */
	public Long getSareaId() {
		return sareaId;
	}

	/**
	 * SAP_MATERIAL_ID is NOT nullable
	 * Field length on DB: 12
	 */
	public void setSapMaterialId(Long sapMaterialId) {
		this.sapMaterialId = sapMaterialId;
	}

	/**
	 * SAP_MATERIAL_ID is NOT nullable
	 * Field length on DB: 12
	 */
	public Long getSapMaterialId() {
		return sapMaterialId;
	}

	/**
	 * ORDER_NUMBER is nullable
	 * Field length on DB: 12
	 */
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	/**
	 * ORDER_NUMBER is nullable
	 * Field length on DB: 12
	 */
	public Long getOrderNumber() {
		return orderNumber;
	}

	/**
	 * ORDER_ITEM_NO is NOT nullable
	 * Field length on DB: 3
	 */
	public void setOrderItemNo(Long orderItemNo) {
		this.orderItemNo = orderItemNo;
	}

	/**
	 * ORDER_ITEM_NO is NOT nullable
	 * Field length on DB: 3
	 */
	public Long getOrderItemNo() {
		return orderItemNo;
	}

	/**
	 * ORDERED_UOM_TXT is nullable
	 * Field length on DB: 20
	 */
	public void setOrderedUomTxt(String orderedUomTxt) {
		this.orderedUomTxt = orderedUomTxt;
	}

	/**
	 * ORDERED_UOM_TXT is nullable
	 * Field length on DB: 20
	 */
	public String getOrderedUomTxt() {
		return orderedUomTxt;
	}

	/**
	 * ORDERED_QUANTITY is nullable
	 * 
	 */
	public void setOrderedQuantity(java.math.BigDecimal orderedQuantity) {
		this.orderedQuantity = orderedQuantity;
	}

	/**
	 * ORDERED_QUANTITY is nullable
	 * 
	 */
	public java.math.BigDecimal getOrderedQuantity() {
		return orderedQuantity;
	}

	/**
	 * LOADED_UOM_TXT is nullable
	 * Field length on DB: 20
	 */
	public void setLoadedUomTxt(String loadedUomTxt) {
		this.loadedUomTxt = loadedUomTxt;
	}

	/**
	 * LOADED_UOM_TXT is nullable
	 * Field length on DB: 20
	 */
	public String getLoadedUomTxt() {
		return loadedUomTxt;
	}

	/**
	 * LOADED_QUANTITY is nullable
	 * 
	 */
	public void setLoadedQuantity(java.math.BigDecimal loadedQuantity) {
		this.loadedQuantity = loadedQuantity;
	}

	/**
	 * LOADED_QUANTITY is nullable
	 * 
	 */
	public java.math.BigDecimal getLoadedQuantity() {
		return loadedQuantity;
	}

	/**
	 * DELIVERED_UOM_TXT is nullable
	 * Field length on DB: 20
	 */
	public void setDeliveredUomTxt(String deliveredUomTxt) {
		this.deliveredUomTxt = deliveredUomTxt;
	}

	/**
	 * DELIVERED_UOM_TXT is nullable
	 * Field length on DB: 20
	 */
	public String getDeliveredUomTxt() {
		return deliveredUomTxt;
	}

	/**
	 * DELIVERED_QUANTITY is nullable
	 * 
	 */
	public void setDeliveredQuantity(java.math.BigDecimal deliveredQuantity) {
		this.deliveredQuantity = deliveredQuantity;
	}

	/**
	 * DELIVERED_QUANTITY is nullable
	 * 
	 */
	public java.math.BigDecimal getDeliveredQuantity() {
		return deliveredQuantity;
	}

	/**
	 * RETURNED_UOM_TXT is nullable
	 * Field length on DB: 20
	 */
	public void setReturnedUomTxt(String returnedUomTxt) {
		this.returnedUomTxt = returnedUomTxt;
	}

	/**
	 * RETURNED_UOM_TXT is nullable
	 * Field length on DB: 20
	 */
	public String getReturnedUomTxt() {
		return returnedUomTxt;
	}

	/**
	 * RETURNED_QUANTITY is nullable
	 * 
	 */
	public void setReturnedQuantity(java.math.BigDecimal returnedQuantity) {
		this.returnedQuantity = returnedQuantity;
	}

	/**
	 * RETURNED_QUANTITY is nullable
	 * 
	 */
	public java.math.BigDecimal getReturnedQuantity() {
		return returnedQuantity;
	}

	/**
	 * DELIVERY_FROM_DAT is NOT nullable
	 * 
	 */
	public void setDeliveryFromDat(Date deliveryFromDat) {
		this.deliveryFromDat = deliveryFromDat;
	}

	/**
	 * DELIVERY_FROM_DAT is NOT nullable
	 * 
	 */
	public Date getDeliveryFromDat() {
		return deliveryFromDat;
	}

	/**
	 * DELIVERY_TO_DAT is NOT nullable
	 * 
	 */
	public void setDeliveryToDat(Date deliveryToDat) {
		this.deliveryToDat = deliveryToDat;
	}

	/**
	 * DELIVERY_TO_DAT is NOT nullable
	 * 
	 */
	public Date getDeliveryToDat() {
		return deliveryToDat;
	}

	/**
	 * DISTRIBUTION_DEST_CD is NOT nullable
	 * Field length on DB: 4
	 */
	public void setDistributionDestCd(String distributionDestCd) {
		this.distributionDestCd = distributionDestCd;
	}

	/**
	 * DISTRIBUTION_DEST_CD is NOT nullable
	 * Field length on DB: 4
	 */
	public String getDistributionDestCd() {
		return distributionDestCd;
	}

	/**
	 * ORDER_STATUS_CD is NOT nullable
	 * Field length on DB: 2
	 */
	public void setOrderStatusCd(Long orderStatusCd) {
		this.orderStatusCd = orderStatusCd;
	}

	/**
	 * ORDER_STATUS_CD is NOT nullable
	 * Field length on DB: 2
	 */
	public Long getOrderStatusCd() {
		return orderStatusCd;
	}

	/**
	 * DELIVERY_TYPE_CD is NOT nullable
	 * Field length on DB: 2
	 */
	public void setDeliveryTypeCd(Long deliveryTypeCd) {
		this.deliveryTypeCd = deliveryTypeCd;
	}

	/**
	 * DELIVERY_TYPE_CD is NOT nullable
	 * Field length on DB: 2
	 */
	public Long getDeliveryTypeCd() {
		return deliveryTypeCd;
	}

	/**
	 * SHIPPINGPOINT_ID is nullable
	 * Field length on DB: 12
	 */
	public void setShippingpointId(Long shippingpointId) {
		this.shippingpointId = shippingpointId;
	}

	/**
	 * SHIPPINGPOINT_ID is nullable
	 * Field length on DB: 12
	 */
	public Long getShippingpointId() {
		return shippingpointId;
	}

	/**
	 * POOL_ID is nullable
	 * Field length on DB: 12
	 */
	public void setPoolId(Long poolId) {
		this.poolId = poolId;
	}

	/**
	 * POOL_ID is nullable
	 * Field length on DB: 12
	 */
	public Long getPoolId() {
		return poolId;
	}

	/**
	 * COMM_CARRIER_ID is nullable
	 * Field length on DB: 12
	 */
	public void setCommCarrierId(Long commCarrierId) {
		this.commCarrierId = commCarrierId;
	}

	/**
	 * COMM_CARRIER_ID is nullable
	 * Field length on DB: 12
	 */
	public Long getCommCarrierId() {
		return commCarrierId;
	}

	 /**
     * EXEC_CARRIER_ID is nullable
     * Field length on DB: 12
     */
    public void setExecCarrierId(Long execCarrierId) {
        this.execCarrierId = execCarrierId;
    }

    /**
     * EXEC_CARRIER_ID is nullable
     * Field length on DB: 12
     */
    public Long getExecCarrierId() {
        return execCarrierId;
    }
	
	/**
	 * PLANNED_DELIVERY_DAT is nullable
	 * 
	 */
	public void setPlannedDeliveryDat(Date plannedDeliveryDat) {
		this.plannedDeliveryDat = plannedDeliveryDat;
	}

	/**
	 * PLANNED_DELIVERY_DAT is nullable
	 * 
	 */
	public Date getPlannedDeliveryDat() {
		return plannedDeliveryDat;
	}

	/**
	 * ORDER_VALIDATION_CODE is nullable
	 * Field length on DB: 2
	 */
	public void setOrderValidationCode(String orderValidationCode) {
		this.orderValidationCode = orderValidationCode;
	}

	/**
	 * ORDER_VALIDATION_CODE is nullable
	 * Field length on DB: 2
	 */
	public String getOrderValidationCode() {
		return orderValidationCode;
	}

	/**
	 * SOLDTO_NAME1 is nullable
	 * Field length on DB: 35
	 */
	public void setSoldtoName1(String soldtoName1) {
		this.soldtoName1 = soldtoName1;
	}

	/**
	 * SOLDTO_NAME1 is nullable
	 * Field length on DB: 35
	 */
	public String getSoldtoName1() {
		return soldtoName1;
	}

	/**
	 * SHIPTO_NAME1 is nullable
	 * Field length on DB: 35
	 */
	public void setShiptoName1(String shiptoName1) {
		this.shiptoName1 = shiptoName1;
	}

	/**
	 * SHIPTO_NAME1 is nullable
	 * Field length on DB: 35
	 */
	public String getShiptoName1() {
		return shiptoName1;
	}

	/**
	 * SHIPTO_CITY is NOT nullable
	 * Field length on DB: 35
	 */
	public void setShiptoCity(String shiptoCity) {
		this.shiptoCity = shiptoCity;
	}

	/**
	 * SHIPTO_CITY is NOT nullable
	 * Field length on DB: 35
	 */
	public String getShiptoCity() {
		return shiptoCity;
	}

	/**
	 * BILLTO_NAME1 is nullable
	 * Field length on DB: 35
	 */
	public void setBilltoName1(String billtoName1) {
		this.billtoName1 = billtoName1;
	}

	/**
	 * BILLTO_NAME1 is nullable
	 * Field length on DB: 35
	 */
	public String getBilltoName1() {
		return billtoName1;
	}

	/**
	 * SHIPTO_SGROUP_CD is nullable
	 * Field length on DB: 4
	 */
	public void setShiptoSgroupCd(String shiptoSgroupCd) {
		this.shiptoSgroupCd = shiptoSgroupCd;
	}

	/**
	 * SHIPTO_SGROUP_CD is nullable
	 * Field length on DB: 4
	 */
	public String getShiptoSgroupCd() {
		return shiptoSgroupCd;
	}

	/**
	 * SHIPTO_SOFFICE_CD is nullable
	 * Field length on DB: 4
	 */
	public void setShiptoSofficeCd(String shiptoSofficeCd) {
		this.shiptoSofficeCd = shiptoSofficeCd;
	}

	/**
	 * SHIPTO_SOFFICE_CD is nullable
	 * Field length on DB: 4
	 */
	public String getShiptoSofficeCd() {
		return shiptoSofficeCd;
	}

	/**
	 * LANG_CD is NOT nullable
	 * Field length on DB: 2
	 */
	public void setLangCd(String langCd) {
		this.langCd = langCd;
	}

	/**
	 * LANG_CD is NOT nullable
	 * Field length on DB: 2
	 */
	public String getLangCd() {
		return langCd;
	}

	/**
	 * STATUS_TXT is NOT nullable
	 * Field length on DB: 50
	 */
	public void setStatusTxt(String statusTxt) {
		this.statusTxt = statusTxt;
	}

	/**
	 * STATUS_TXT is NOT nullable
	 * Field length on DB: 50
	 */
	public String getStatusTxt() {
		return statusTxt;
	}

	/**
	 * WARN_STATUS_CODE is nullable
	 * Field length on DB: 2
	 */
	public void setWarnStatusCode(Long warnStatusCode) {
		this.warnStatusCode = warnStatusCode;
	}

	/**
	 * WARN_STATUS_CODE is nullable
	 * Field length on DB: 2
	 */
	public Long getWarnStatusCode() {
		return warnStatusCode;
	}

	/**
	 * TIME_TO_RED_LINE is nullable
	 * Field length on DB: 12
	 */
	public void setTimeToRedLine(Long timeToRedLine) {
		this.timeToRedLine = timeToRedLine;
	}

	/**
	 * TIME_TO_RED_LINE is nullable
	 * Field length on DB: 12
	 */
	public Long getTimeToRedLine() {
		return timeToRedLine;
	}

	/**
	 * POOL_NAME is nullable
	 * Field length on DB: 30
	 */
	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	/**
	 * POOL_NAME is nullable
	 * Field length on DB: 30
	 */
	public String getPoolName() {
		return poolName;
	}

	/**
	 * EXEC_CARRIER_NAME is nullable
	 * Field length on DB: 40
	 */
	public void setExecCarrierName(String execCarrierName) {
		this.execCarrierName = execCarrierName;
	}

	/**
	 * EXEC_CARRIER_NAME is nullable
	 * Field length on DB: 40
	 */
	public String getExecCarrierName() {
		return execCarrierName;
	}

	/**
	 * COMM_CARRIER_NAME is nullable
	 * Field length on DB: 40
	 */
	public void setCommCarrierName(String commCarrierName) {
		this.commCarrierName = commCarrierName;
	}

	/**
	 * COMM_CARRIER_NAME is nullable
	 * Field length on DB: 40
	 */
	public String getCommCarrierName() {
		return commCarrierName;
	}

	/**
	 * SAP_VENDOR_ID is nullable
	 * Field length on DB: 12
	 */
	public void setSapVendorId(Long sapVendorId) {
		this.sapVendorId = sapVendorId;
	}

	/**
	 * SAP_VENDOR_ID is nullable
	 * Field length on DB: 12
	 */
	public Long getSapVendorId() {
		return sapVendorId;
	}

	/**
	 * PLANT_CD is nullable
	 * Field length on DB: 4
	 */
	public void setPlantCd(String plantCd) {
		this.plantCd = plantCd;
	}

	/**
	 * PLANT_CD is nullable
	 * Field length on DB: 4
	 */
	public String getPlantCd() {
		return plantCd;
	}

	/**
	 * SHIPPINGPOINT_NAME is nullable
	 * Field length on DB: 80
	 */
	public void setShippingpointName(String shippingpointName) {
		this.shippingpointName = shippingpointName;
	}

	/**
	 * SHIPPINGPOINT_NAME is nullable
	 * Field length on DB: 80
	 */
	public String getShippingpointName() {
		return shippingpointName;
	}

	/**
	 * MATERIAL_TXT is NOT nullable
	 * Field length on DB: 40
	 */
	public void setMaterialTxt(String materialTxt) {
		this.materialTxt = materialTxt;
	}

	/**
	 * MATERIAL_TXT is NOT nullable
	 * Field length on DB: 40
	 */
	public String getMaterialTxt() {
		return materialTxt;
	}

	/**
	 * DELIVERY_TYPE_TXT is NOT nullable
	 * Field length on DB: 40
	 */
	public void setDeliveryTypeTxt(String deliveryTypeTxt) {
		this.deliveryTypeTxt = deliveryTypeTxt;
	}

	/**
	 * DELIVERY_TYPE_TXT is NOT nullable
	 * Field length on DB: 40
	 */
	public String getDeliveryTypeTxt() {
		return deliveryTypeTxt;
	}

	/**
	 * COMPANYCODE_CD is NOT nullable
	 * Field length on DB: 4
	 */
	public void setCompanycodeCd(String companycodeCd) {
		this.companycodeCd = companycodeCd;
	}

	/**
	 * COMPANYCODE_CD is NOT nullable
	 * Field length on DB: 4
	 */
	public String getCompanycodeCd() {
		return companycodeCd;
	}

	/**
	 * DRIVER_FULL_NAME is nullable
	 * Field length on DB: 81
	 */
	public void setDriverFullName(String driverFullName) {
		this.driverFullName = driverFullName;
	}

	/**
	 * DRIVER_FULL_NAME is nullable
	 * Field length on DB: 81
	 */
	public String getDriverFullName() {
		return driverFullName;
	}

	/**
	 * VEHICLE_NAME is nullable
	 * Field length on DB: 30
	 */
	public void setVehicleName(String vehicleName) {
		this.vehicleName = vehicleName;
	}

	/**
	 * VEHICLE_NAME is nullable
	 * Field length on DB: 30
	 */
	public String getVehicleName() {
		return vehicleName;
	}

	/**
	 * MATERIAL_PACKAGE_CD is NOT nullable
	 * Field length on DB: 4
	 */
	public void setMaterialPackageCd(String materialPackageCd) {
		this.materialPackageCd = materialPackageCd;
	}

	/**
	 * MATERIAL_PACKAGE_CD is NOT nullable
	 * Field length on DB: 4
	 */
	public String getMaterialPackageCd() {
		return materialPackageCd;
	}

	/**
	 * SHIPTO_TIMEZONE_CD is nullable
	 * Field length on DB: 32
	 */
	public void setShiptoTimezoneCd(String shiptoTimezoneCd) {
		this.shiptoTimezoneCd = shiptoTimezoneCd;
	}

	/**
	 * SHIPTO_TIMEZONE_CD is nullable
	 * Field length on DB: 32
	 */
	public String getShiptoTimezoneCd() {
		return shiptoTimezoneCd;
	}
	
	/**
     * LDS_DELIVERY_NOTE_NO is nullable
     * Field length on DB: 12
     */
    public void setLdsDeliveryNoteNo(String ldsDeliveryNoteNo) {
        this.ldsDeliveryNoteNo = ldsDeliveryNoteNo;
    }

    /**
     * LDS_DELIVERY_NOTE_NO is nullable
     * Field length on DB: 12
     */
    public String getLdsDeliveryNoteNo() {
        return ldsDeliveryNoteNo;
    }

    /**
     * ORDER_NUMBER_FROM_SEQ_USAGE is nullable
     * Field length on DB: 12
     * Order number according to the use case specified by 'order_number_seq_usage_cd', based on the configurable sequence counter from table tlngc_conf_seq_counters. Only meaningful if order_number_seq_usage_cd is not empty.
     */
    public void setOrderNumberFromSeqUsage(String orderNumberFromSeqUsage) {
        this.orderNumberFromSeqUsage = orderNumberFromSeqUsage;
    }

    /**
     * ORDER_NUMBER_FROM_SEQ_USAGE is nullable
     * Field length on DB: 12
     * Order number according to the use case specified by 'order_number_seq_usage_cd', based on the configurable sequence counter from table tlngc_conf_seq_counters. Only meaningful if order_number_seq_usage_cd is not empty.
     */
    public String getOrderNumberFromSeqUsage() {
        return orderNumberFromSeqUsage;
    }
    
    /**
     * NOTE_ORDER_MGMT is nullable
     * Field length on DB: 256
     * Notes for the order management about the ship-to. Not modifiable by Call Center Agent. The initial content is composed of tlgnc_shipto.note_order_mgmt and tlgnc_shipto_sarea_restr.note_order_mgmt is set at order creation time.
     */
    public void setNoteOrderMgmt(String noteOrderMgmt) {
        this.noteOrderMgmt = noteOrderMgmt;
    }

    /**
     * NOTE_ORDER_MGMT is nullable
     * Field length on DB: 256
     * Notes for the order management about the ship-to. Not modifiable by Call Center Agent. The initial content is composed of tlgnc_shipto.note_order_mgmt and tlgnc_shipto_sarea_restr.note_order_mgmt is set at order creation time.
     */
    public String getNoteOrderMgmt() {
        return noteOrderMgmt;
    }

    /**
     * NOTE_DELIVERY is nullable
     * Field length on DB: 300
     * Additional text for the delivery note (PDF). The initial content is composed of tlgnc_shipto.note_delivery and tlgnc_shipto_sarea_restr.note_delivery, set at order creation time and can be modified.
     */
    public void setNoteDelivery(String noteDelivery) {
        this.noteDelivery = noteDelivery;
    }

    /**
     * NOTE_DELIVERY is nullable
     * Field length on DB: 300
     * Additional text for the delivery note (PDF). The initial content is composed of tlgnc_shipto.note_delivery and tlgnc_shipto_sarea_restr.note_delivery, set at order creation time and can be modified.
     */
    public String getNoteDelivery() {
        return noteDelivery;
    }

    /**
     * NOTE_DISPATCHING is nullable
     * Field length on DB: 256
     * Notes for the Dispatching (Pool/Carrier). The initial content is composed of tlgnc_shipto.note_dispatching and tlgnc_shipto_sarea_restr.note_dispatching, set at order creation time and can be modified.
     */
    public void setNoteDispatching(String noteDispatching) {
        this.noteDispatching = noteDispatching;
    }

    /**
     * NOTE_DISPATCHING is nullable
     * Field length on DB: 256
     * Notes for the Dispatching (Pool/Carrier). The initial content is composed of tlgnc_shipto.note_dispatching and tlgnc_shipto_sarea_restr.note_dispatching, set at order creation time and can be modified.
     */
    public String getNoteDispatching() {
        return noteDispatching;
    }

    /**
     * NOTE_TRUCK_DRIVER is nullable
     * Field length on DB: 256
     * Notes for the Driver. The initial content is composed of tlgnc_shipto.note_truck_driver and tlgnc_shipto_sarea_restr.note_truck_driver, set at order creation time and can be modified.
     */
    public void setNoteTruckDriver(String noteTruckDriver) {
        this.noteTruckDriver = noteTruckDriver;
    }

    /**
     * NOTE_TRUCK_DRIVER is nullable
     * Field length on DB: 256
     * Notes for the Driver. The initial content is composed of tlgnc_shipto.note_truck_driver and tlgnc_shipto_sarea_restr.note_truck_driver, set at order creation time and can be modified.
     */
    public String getNoteTruckDriver() {
        return noteTruckDriver;
    }

    /**
     * NOTE_RAILWAY_DISP is nullable
     * Field length on DB: 256
     * Notes for the Railway disposition, but in the UI called 'Spedition'. This is misleading, because the terminal screen for Trucks is called 'Spedition', but has definitely nothing to do with it. Anyway: The initial content is composed of tlgnc_shipto.note_railway_disp and tlgnc_shipto_sarea_restr.note_railway_disp, set at order creation time and can be modified.
     */
    public void setNoteRailwayDisp(String noteRailwayDisp) {
        this.noteRailwayDisp = noteRailwayDisp;
    }

    /**
     * NOTE_RAILWAY_DISP is nullable
     * Field length on DB: 256
     * Notes for the Railway disposition, but in the UI called 'Spedition'. This is misleading, because the terminal screen for Trucks is called 'Spedition', but has definitely nothing to do with it. Anyway: The initial content is composed of tlgnc_shipto.note_railway_disp and tlgnc_shipto_sarea_restr.note_railway_disp, set at order creation time and can be modified.
     */
    public String getNoteRailwayDisp() {
        return noteRailwayDisp;
    }
    
    /**
	 * @return the shiptoAddress
	 */
	public String getShiptoAddress() {
		return shiptoAddress;
	}

	/**
	 * @param shiptoAddress the shiptoAddress to set
	 */
	public void setShiptoAddress(String shiptoAddress) {
		this.shiptoAddress = shiptoAddress;
	}
	
	 /**
     * LOADING_TS is nullable
     * 
     * Date and Time when START loading occured.
     */
    public void setLoadingTs(Date loadingTs) {
        this.loadingTs = loadingTs;
    }

    /**
     * LOADING_TS is nullable
     * 
     * Date and Time when START loading occured.
     */
    public Date getLoadingTs() {
        return loadingTs;
    }
    
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this);
		tsb.append(super.toString());
		if (LOG_ENTITIES.isDebugEnabled()) {
			tsb.append("compositeId", "compositeId");
			tsb.append("orderId", "orderId");
			tsb.append("orderCreationTypeCd", "orderCreationTypeCd");
			tsb.append("shiptoSapBpId", "shiptoSapBpId");
			tsb.append("soldtoSapBpId", "soldtoSapBpId");
			tsb.append("billtoSapBpId", "billtoSapBpId");
			tsb.append("sareaId", "sareaId");
			tsb.append("sapMaterialId", "sapMaterialId");
			tsb.append("orderNumber", "orderNumber");
			tsb.append("orderItemNo", "orderItemNo");
			tsb.append("orderedUomTxt", "orderedUomTxt");
			tsb.append("orderedQuantity", "orderedQuantity");
			tsb.append("loadedUomTxt", "loadedUomTxt");
			tsb.append("loadedQuantity", "loadedQuantity");
			tsb.append("deliveredUomTxt", "deliveredUomTxt");
			tsb.append("deliveredQuantity", "deliveredQuantity");
			tsb.append("returnedUomTxt", "returnedUomTxt");
			tsb.append("returnedQuantity", "returnedQuantity");
			tsb.append("deliveryFromDat", "deliveryFromDat");
			tsb.append("deliveryToDat", "deliveryToDat");
			tsb.append("distributionDestCd", "distributionDestCd");
			tsb.append("orderStatusCd", "orderStatusCd");
			tsb.append("deliveryTypeCd", "deliveryTypeCd");
			tsb.append("shippingpointId", "shippingpointId");
			tsb.append("transportDurationMin", "transportDurationMin");
			tsb.append("plannedLoadingTimeMin", "plannedLoadingTimeMin");
			tsb.append("poolId", "poolId");
			tsb.append("commCarrierId", "commCarrierId");
			tsb.append("plannedDeliveryDat", "plannedDeliveryDat");
			tsb.append("orderValidationCode", "orderValidationCode");
			tsb.append("soldtoName1", "soldtoName1");
			tsb.append("shiptoName1", "shiptoName1");
			tsb.append("shiptoCity", "shiptoCity");
			tsb.append("billtoName1", "billtoName1");
			tsb.append("sgroupCd", "sgroupCd");
			tsb.append("shiptoSofficeCd", "shiptoSofficeCd");
			tsb.append("langCd", "langCd");
			tsb.append("statusTxt", "statusTxt");
			tsb.append("warnStatusCode", "warnStatusCode");
			tsb.append("timeToRedLine", "timeToRedLine");
			tsb.append("commCarrierName", "commCarrierName");
			tsb.append("sapVendorId", "sapVendorId");
			tsb.append("plantCd", "plantCd");
			tsb.append("shippingpointName", "shippingpointName");
			tsb.append("materialTxt", "materialTxt");
			tsb.append("deliveryTypeTxt", "deliveryTypeTxt");
			tsb.append("sorgCd", "sorgCd");
			tsb.append("companycodeCd", "companycodeCd");
			tsb.append("driverFullName", "driverFullName");
			tsb.append("vehicleName", "vehicleName");
			tsb.append("materialPackageCd", "materialPackageCd");
			tsb.append("shiptoTimezoneCd", "shiptoTimezoneCd");
            tsb.append("ldsDeliveryNoteNo", "ldsDeliveryNoteNo");
            tsb.append("noteOrderMgmt", "noteOrderMgmt");
            tsb.append("noteDelivery", "noteDelivery");
            tsb.append("noteDispatching", "noteDispatching");
            tsb.append("noteTruckDriver", "noteTruckDriver");
            tsb.append("noteRailwayDisp", "noteRailwayDisp");
            tsb.append("shiptoAddress", "shiptoAddress");
            tsb.append("loadingTs", "loadingTs");
		}
		else if (LOG_ENTITIES.isInfoEnabled()) {
			tsb.append("compositeId", compositeId); // ID
		}
		return tsb.toString();
	}
}
