/*
 * Author : AdNovum Informatik AG
 *
 * database vendor: oracle
 * database application id: LGN
 */
package com.crh.logon.persistence.entities.frozen;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import ch.nevis.gdi.jpa.BaseEntity;
import ch.nevis.gdi.jpa.ThreadLocalUserInfo;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;


/**
 * Frozen data of an order at the time it is set to 'finished', i.e. transition from status 15=delivered to status 16=finished . For truck orders, this is the same moment when also sending the order data to SAP. For rail orders sending to SAP happens earlier (to transmit the transportation request to the rail company via SAP), but the freeze of order data happens at the end still. The purpose of the table is to provide a snapshot of all relevant objects and their attributes at the time the order was completed, i.e. including address data of the business partners, load limits of the vehicles, optimial transport type as entered on the Dispokarte at that moment, etc. The data can then be used as a source for advanced reporting and statistics (currently outside of Logon).
 */

@SuppressWarnings("unused")
@Entity
@Table(name = "TLGNF_ORDER_FINAL")
public class OrderFinalEntity implements BaseEntity {
    private static final JcanLogger LOG_ENTITIES = JcanLoggerFactory.getLogger("persistence.entities");
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "TLGNF_ORDER_FINAL";


    @Id
    @SequenceGenerator(name = "slgnf_order_final", allocationSize = 1, sequenceName = "slgnf_order_final")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "slgnf_order_final")
    @Column(name = "ORDER_FINAL_ID")
    private Long orderFinalId;

    @Column(name = "O_ORDER_ID")
    private Long oOrderId;

    @Column(name = "O_ORDER_NUMBER")
    private Long oOrderNumber;

    @Column(name = "O_ORDER_STATUS_CD")
    private Long oOrderStatusCd;

    @Column(name = "O_SHIPTO_SAP_BP_ID")
    private Long oShiptoSapBpId;

    @Column(name = "WE_SAP_ID")
    private String weSapId;

    @Column(name = "WE_MARKED_FOR_DELETION")
    private String weMarkedForDeletion;

    @Column(name = "WE_NAME1")
    private String weName1;

    @Column(name = "WE_NAME2")
    private String weName2;

    @Column(name = "WE_COUNTRY")
    private String weCountry;

    @Column(name = "WE_POSTAL_CODE")
    private String wePostalCode;

    @Column(name = "WE_CITY")
    private String weCity;

    @Column(name = "WE_REGION")
    private String weRegion;

    @Column(name = "WE_STREET")
    private String weStreet;

    @Column(name = "WE_HOUSE_NUMBER")
    private String weHouseNumber;

    @Column(name = "WE_LANG_CD")
    private String weLangCd;

    @Column(name = "WE_CUSTOMER_CLASSIFICATION")
    private String weCustomerClassification;

    @Column(name = "WE_RAILWAY_STATION_ID")
    private Long weRailwayStationId;

    @Column(name = "WE_RS_UIC_COUNTRY_CD")
    private Long weRsUicCountryCd;

    @Column(name = "WE_RS_STATION_NO")
    private String weRsStationNo;

    @Column(name = "WE_RS_STATION_NAME")
    private String weRsStationName;

    @Column(name = "WE_RS_COM_UIC_COUNTRY_CD")
    private Long weRsComUicCountryCd;

    @Column(name = "WE_RS_COM_STATION_NO")
    private String weRsComStationNo;

    @Column(name = "WE_RS_COM_STATION_NAME")
    private String weRsComStationName;

    @Column(name = "WE_TRAIN_ST_UNLOADING_POINT")
    private String weTrainStUnloadingPoint;

    @Column(name = "WE_SBB_CUSTOMER_NUMBER")
    private String weSbbCustomerNumber;

    @Column(name = "WE_BP_IS_SHIPTO")
    private Boolean weBpIsShipto;

    @Column(name = "WE_BP_IS_SOLDTO")
    private Boolean weBpIsSoldto;

    @Column(name = "WE_BP_IS_BILLTO")
    private Boolean weBpIsBillto;

    @Column(name = "WE_SAP_BP_SAREA_ID")
    private Long weSapBpSareaId;

    @Column(name = "WE_SAREA_MARKED_FOR_DELETION")
    private String weSareaMarkedForDeletion;

    @Column(name = "WE_SALES_DISTRICT")
    private String weSalesDistrict;

    @Column(name = "WE_SOFFICE_CD")
    private String weSofficeCd;

    @Column(name = "WE_SOFFICE_NAME")
    private String weSofficeName;

    @Column(name = "WE_SGROUP_CD")
    private String weSgroupCd;

    @Column(name = "WE_SGROUP_NAME")
    private String weSgroupName;

    @Column(name = "WE_SGROUP_EMAIL_ADDRESSES")
    private String weSgroupEmailAddresses;

    @Column(name = "WE_CUSTOMER_GROUP")
    private String weCustomerGroup;

    @Column(name = "WE_ABC_CLASS")
    private String weAbcClass;

    @Column(name = "WE_CURRENCY_CD")
    private String weCurrencyCd;

    @Column(name = "WE_DISTRIBUTION_DEST_CD")
    private String weDistributionDestCd;

    @Column(name = "WE_CUSTOMER_ORDER_BLOCK")
    private Boolean weCustomerOrderBlock;

    @Column(name = "WE_CUSTOMER_DELIVERY_BLOCK")
    private Boolean weCustomerDeliveryBlock;

    @Column(name = "WE_CUSTOM_OFFICE")
    private String weCustomOffice;

    @Column(name = "WE_CHROMAT_REDUCTION_MONTHS")
    private java.math.BigDecimal weChromatReductionMonths;

    @Column(name = "WE_DEFAULT_PLANT_CD")
    private String weDefaultPlantCd;

    @Column(name = "WE_SHIPTO_ID")
    private Long weShiptoId;

    @Column(name = "WE_ACTIVE_FLAG")
    private Boolean weActiveFlag;

    @Column(name = "WE_SHIPTO_TYPE_CD")
    private Long weShiptoTypeCd;

    @Column(name = "WE_SIGNATURE_TYPE_CD")
    private Long weSignatureTypeCd;

    @Column(name = "WE_COORD_LAT")
    private Double weCoordLat;

    @Column(name = "WE_COORD_LON")
    private Double weCoordLon;

    @Column(name = "WE_COORD_FIX_FLAG")
    private Boolean weCoordFixFlag;

    @Column(name = "WE_GEOFENCE_RADIUS")
    private Long weGeofenceRadius;

    @Column(name = "WE_UNLOADING_TIME")
    private Long weUnloadingTime;

    @Column(name = "WE_RAIL_CHAIN_SHIPPINGPOINT_ID")
    private Long weRailChainShippingpointId;

    @Column(name = "WE_RAIL_CHAIN_SP_PLANT_CD")
    private String weRailChainSpPlantCd;

    @Column(name = "WE_RAIL_CHAIN_SP_SAP_SHIPPT_NO")
    private String weRailChainSpSapShipptNo;

    @Column(name = "WE_RAIL_CHAIN_SP_SHIPPT_NAME")
    private String weRailChainSpShipptName;

    @Column(name = "WE_MAT_CHAIN_SHIPPINGPOINT_ID")
    private Long weMatChainShippingpointId;

    @Column(name = "WE_MAT_CHAIN_SP_PLANT_CD")
    private String weMatChainSpPlantCd;

    @Column(name = "WE_MAT_CHAIN_SP_SAP_SHIPPT_NO")
    private String weMatChainSpSapShipptNo;

    @Column(name = "WE_MAT_CHAIN_SP_SHIPPT_NAME")
    private String weMatChainSpShipptName;

    @Column(name = "WE_RAIL_HOLD_BACK_RE_ORDER")
    private Boolean weRailHoldBackReOrder;

    @Column(name = "WE_OPTIMAL_TRUCK_TYPE_CD")
    private Long weOptimalTruckTypeCd;

    @Column(name = "WE_OPTIMAL_WAGON_TYPE_CD")
    private Long weOptimalWagonTypeCd;

    @Column(name = "WE_ST_SAREA_SHIPTO_SAREA_ID")
    private Long weStSareaShiptoSareaId;

    @Column(name = "WE_ST_SAREA_FREE_DELIVERY")
    private Boolean weStSareaFreeDelivery;

    @Column(name = "WE_ST_SAREA_RELEASE_SAP_OPT")
    private Boolean weStSareaReleaseSapOpt;

    @Column(name = "WE_ST_DISTRIBUTION_DEST_CD")
    private String weStDistributionDestCd;

    @Column(name = "O_SOLDTO_SAP_BP_ID")
    private Long oSoldtoSapBpId;
    
    @Column( name = "ACTUAL_DRIVING_TIME_MIN")
    private Long actualDrivingTimeMin;
    
    @Column(name = "AG_SAP_ID")
    private String agSapId;

    @Column(name = "AG_MARKED_FOR_DELETION")
    private String agMarkedForDeletion;

    @Column(name = "AG_NAME1")
    private String agName1;

    @Column(name = "AG_NAME2")
    private String agName2;

    @Column(name = "AG_COUNTRY")
    private String agCountry;

    @Column(name = "AG_POSTAL_CODE")
    private String agPostalCode;

    @Column(name = "AG_CITY")
    private String agCity;

    @Column(name = "AG_REGION")
    private String agRegion;

    @Column(name = "AG_STREET")
    private String agStreet;

    @Column(name = "AG_HOUSE_NUMBER")
    private String agHouseNumber;

    @Column(name = "AG_LANG_CD")
    private String agLangCd;

    @Column(name = "AG_CUSTOMER_CLASSIFICATION")
    private String agCustomerClassification;

    @Column(name = "AG_RAILWAY_STATION_ID")
    private Long agRailwayStationId;

    @Column(name = "AG_TRAIN_ST_UNLOADING_POINT")
    private String agTrainStUnloadingPoint;

    @Column(name = "AG_SBB_CUSTOMER_NUMBER")
    private String agSbbCustomerNumber;

    @Column(name = "AG_BP_IS_SHIPTO")
    private Boolean agBpIsShipto;

    @Column(name = "AG_BP_IS_SOLDTO")
    private Boolean agBpIsSoldto;

    @Column(name = "AG_BP_IS_BILLTO")
    private Boolean agBpIsBillto;

    @Column(name = "AG_SAP_BP_SAREA_ID")
    private Long agSapBpSareaId;

    @Column(name = "AG_SAREA_MARKED_FOR_DELETION")
    private String agSareaMarkedForDeletion;

    @Column(name = "AG_SALES_DISTRICT")
    private String agSalesDistrict;

    @Column(name = "AG_SOFFICE_CD")
    private String agSofficeCd;

    @Column(name = "AG_SOFFICE_NAME")
    private String agSofficeName;

    @Column(name = "AG_SGROUP_CD")
    private String agSgroupCd;

    @Column(name = "AG_SGROUP_NAME")
    private String agSgroupName;

    @Column(name = "AG_SGROUP_EMAIL_ADDRESSES")
    private String agSgroupEmailAddresses;

    @Column(name = "AG_CUSTOMER_GROUP")
    private String agCustomerGroup;

    @Column(name = "AG_ABC_CLASS")
    private String agAbcClass;

    @Column(name = "AG_CURRENCY_CD")
    private String agCurrencyCd;

    @Column(name = "AG_DISTRIBUTION_DEST_CD")
    private String agDistributionDestCd;

    @Column(name = "AG_CUSTOMER_ORDER_BLOCK")
    private Boolean agCustomerOrderBlock;

    @Column(name = "AG_CUSTOMER_DELIVERY_BLOCK")
    private Boolean agCustomerDeliveryBlock;

    @Column(name = "AG_CUSTOM_OFFICE")
    private String agCustomOffice;

    @Column(name = "AG_CHROMAT_REDUCTION_MONTHS")
    private java.math.BigDecimal agChromatReductionMonths;

    @Column(name = "AG_DEFAULT_PLANT_CD")
    private String agDefaultPlantCd;

    @Column(name = "O_BILLTO_SAP_BP_ID")
    private Long oBilltoSapBpId;

    @Column(name = "RE_SAP_ID")
    private String reSapId;

    @Column(name = "RE_MARKED_FOR_DELETION")
    private String reMarkedForDeletion;

    @Column(name = "RE_NAME1")
    private String reName1;

    @Column(name = "RE_NAME2")
    private String reName2;

    @Column(name = "RE_COUNTRY")
    private String reCountry;

    @Column(name = "RE_POSTAL_CODE")
    private String rePostalCode;

    @Column(name = "RE_CITY")
    private String reCity;

    @Column(name = "RE_REGION")
    private String reRegion;

    @Column(name = "RE_STREET")
    private String reStreet;

    @Column(name = "RE_HOUSE_NUMBER")
    private String reHouseNumber;

    @Column(name = "RE_LANG_CD")
    private String reLangCd;

    @Column(name = "RE_CUSTOMER_CLASSIFICATION")
    private String reCustomerClassification;

    @Column(name = "RE_RAILWAY_STATION_ID")
    private Long reRailwayStationId;

    @Column(name = "RE_TRAIN_ST_UNLOADING_POINT")
    private String reTrainStUnloadingPoint;

    @Column(name = "RE_SBB_CUSTOMER_NUMBER")
    private String reSbbCustomerNumber;

    @Column(name = "RE_BP_IS_SHIPTO")
    private Boolean reBpIsShipto;

    @Column(name = "RE_BP_IS_SOLDTO")
    private Boolean reBpIsSoldto;

    @Column(name = "RE_BP_IS_BILLTO")
    private Boolean reBpIsBillto;

    @Column(name = "RE_SAP_BP_SAREA_ID")
    private Long reSapBpSareaId;

    @Column(name = "RE_SAREA_MARKED_FOR_DELETION")
    private String reSareaMarkedForDeletion;

    @Column(name = "RE_SALES_DISTRICT")
    private String reSalesDistrict;

    @Column(name = "RE_SOFFICE_CD")
    private String reSofficeCd;

    @Column(name = "RE_SGROUP_CD")
    private String reSgroupCd;

    @Column(name = "RE_CUSTOMER_GROUP")
    private String reCustomerGroup;

    @Column(name = "RE_ABC_CLASS")
    private String reAbcClass;

    @Column(name = "RE_CURRENCY_CD")
    private String reCurrencyCd;

    @Column(name = "RE_DISTRIBUTION_DEST_CD")
    private String reDistributionDestCd;

    @Column(name = "RE_CUSTOMER_ORDER_BLOCK")
    private Boolean reCustomerOrderBlock;

    @Column(name = "RE_CUSTOMER_DELIVERY_BLOCK")
    private Boolean reCustomerDeliveryBlock;

    @Column(name = "RE_CUSTOM_OFFICE")
    private String reCustomOffice;

    @Column(name = "RE_CHROMAT_REDUCTION_MONTHS")
    private java.math.BigDecimal reChromatReductionMonths;

    @Column(name = "RE_DEFAULT_PLANT_CD")
    private String reDefaultPlantCd;

    @Column(name = "O_SAREA_ID")
    private Long oSareaId;

    @Column(name = "SAREA_DISPLAY_LONGNAME")
    private String sareaDisplayLongname;

    @Column(name = "SAREA_SORG_CD")
    private String sareaSorgCd;

    @Column(name = "SAREA_DISTRIBUTION_CHANNEL")
    private String sareaDistributionChannel;

    @Column(name = "SAREA_DIVISION_CD")
    private String sareaDivisionCd;

    @Column(name = "SAREA_DIVISION_SEGMENT_CD ")
    private String sareaDivisionSegmentCd;
    
    @Column(name = "SAREA_COMPANYCODE_CD")
    private String sareaCompanycodeCd;
    
    @Column(name = "SAREA_COUNTRY_CD")
    private String sareaCountryCd;
    
    @Column(name = "O_POOL_ID")
    private Long oPoolId;

    @Column(name = "O_COMM_CARRIER_ID")
    private Long oCommCarrierId;

    @Column(name = "COMM_CARRIER_DISPLAY_NAME")
    private String commCarrierDisplayName;

    @Column(name = "COMM_CARRIER_SAP_VENDOR_ID")
    private Long commCarrierSapVendorId;

    @Column(name = "COMM_CARRIER_V_SAP_ID")
    private String commCarrierVSapId;

    @Column(name = "COMM_CARRIER_V_ACCOUNT_GROUP")
    private String commCarrierVAccountGroup;

    @Column(name = "COMM_CARRIER_V_MARKED_FOR_DEL")
    private String commCarrierVMarkedForDel;

    @Column(name = "COMM_CARRIER_V_NAME1")
    private String commCarrierVName1;

    @Column(name = "COMM_CARRIER_V_NAME2")
    private String commCarrierVName2;

    @Column(name = "COMM_CARRIER_V_COUNTRY_CD")
    private String commCarrierVCountryCd;

    @Column(name = "COMM_CARRIER_V_POSTAL_CODE")
    private String commCarrierVPostalCode;

    @Column(name = "COMM_CARRIER_V_CITY")
    private String commCarrierVCity;

    @Column(name = "COMM_CARRIER_V_LOCATION_DIST")
    private String commCarrierVLocationDist;

    @Column(name = "COMM_CARRIER_V_REGION")
    private String commCarrierVRegion;

    @Column(name = "COMM_CARRIER_V_STREET")
    private String commCarrierVStreet;

    @Column(name = "COMM_CARRIER_V_HOUSE_NUMBER")
    private String commCarrierVHouseNumber;

    @Column(name = "COMM_CARRIER_V_LANG_CD")
    private String commCarrierVLangCd;

    @Column(name = "COMM_CARRIER_V_VAT_REGIST_NO")
    private String commCarrierVVatRegistNo;

    @Column(name = "COMM_CARRIER_IS_INTERNAL")
    private Boolean commCarrierIsInternal;

    @Column(name = "O_EXEC_CARRIER_ID")
    private Long oExecCarrierId;

    @Column(name = "EXEC_CARRIER_DISPLAY_NAME")
    private String execCarrierDisplayName;

    @Column(name = "EXEC_CARRIER_SAP_VENDOR_ID")
    private Long execCarrierSapVendorId;

    @Column(name = "EXEC_CARRIER_V_SAP_ID")
    private String execCarrierVSapId;

    @Column(name = "EXEC_CARRIER_V_ACCOUNT_GROUP")
    private String execCarrierVAccountGroup;

    @Column(name = "EXEC_CARRIER_V_MARKED_FOR_DEL")
    private String execCarrierVMarkedForDel;

    @Column(name = "EXEC_CARRIER_V_NAME1")
    private String execCarrierVName1;

    @Column(name = "EXEC_CARRIER_V_NAME2")
    private String execCarrierVName2;

    @Column(name = "EXEC_CARRIER_V_COUNTRY_CD")
    private String execCarrierVCountryCd;

    @Column(name = "EXEC_CARRIER_V_POSTAL_CODE")
    private String execCarrierVPostalCode;

    @Column(name = "EXEC_CARRIER_V_CITY")
    private String execCarrierVCity;

    @Column(name = "EXEC_CARRIER_V_LOCATION_DIST")
    private String execCarrierVLocationDist;

    @Column(name = "EXEC_CARRIER_V_REGION")
    private String execCarrierVRegion;

    @Column(name = "EXEC_CARRIER_V_STREET")
    private String execCarrierVStreet;

    @Column(name = "EXEC_CARRIER_V_HOUSE_NUMBER")
    private String execCarrierVHouseNumber;

    @Column(name = "EXEC_CARRIER_V_LANG_CD")
    private String execCarrierVLangCd;

    @Column(name = "EXEC_CARRIER_V_VAT_REGIST_NO")
    private String execCarrierVVatRegistNo;

    @Column(name = "EXEC_CARRIER_IS_INTERNAL")
    private Boolean execCarrierIsInternal;

    @Column(name = "O_SHIPPINGPOINT_ID")
    private Long oShippingpointId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SENT_KPI_TO_SAP_BW_TS")
    private Date sentKpiToSapBwTs;

    @Column(name = "SP_PLANT_CD")
    private String spPlantCd;

    @Column(name = "SP_PLANT_NAME")
    private String spPlantName;

    @Column(name = "SP_SAP_SHIPPINGPOINT_NO")
    private String spSapShippingpointNo;

    @Column(name = "SP_SHIPPINGPOINT_NAME")
    private String spShippingpointName;

    @Column(name = "SP_SHIPPPOINT_TYPE_CD")
    private Long spShipppointTypeCd;

    @Column(name = "SP_SAP_SHIPPING_COND_CD")
    private String spSapShippingCondCd;

    @Column(name = "SP_STORAGE_LOCATION")
    private String spStorageLocation;

    @Column(name = "SP_RAILWAY_STATION_ID")
    private Long spRailwayStationId;

    @Column(name = "SP_RS_UIC_COUNTRY_CD")
    private Long spRsUicCountryCd;

    @Column(name = "SP_RS_RAILWAY_STATION_NO")
    private String spRsRailwayStationNo;

    @Column(name = "SP_RS_RAILWAY_STATION_NAME")
    private String spRsRailwayStationName;

    @Column(name = "SP_RAILWAY_STATION_LP")
    private String spRailwayStationLp;

    @Column(name = "SP_WEIGH_SYSTEM_SP_CD")
    private Long spWeighSystemSpCd;

    @Column(name = "SP_LDS_SERVER")
    private String spLdsServer;

    @Column(name = "SP_SHIPPINGPOINT_COMMENT")
    private String spShippingpointComment;

    @Column(name = "SP_COORD_LAT")
    private Double spCoordLat;

    @Column(name = "SP_COORD_LON")
    private Double spCoordLon;

    @Column(name = "SP_COORD_FIX_FLAG")
    private Boolean spCoordFixFlag;

    @Column(name = "SP_GEOFENCE_RADIUS")
    private Long spGeofenceRadius;

    @Column(name = "SP_FVL_MIX_SHIPTO_PER_WAGON_FL")
    private Boolean spFvlMixShiptoPerWagonFl;

    @Column(name = "SP_SEND_SAP_SHIPPINGPOINT_ID")
    private Long spSendSapShippingpointId;

    @Column(name = "SP_SEND_SAP_SP_PLANT_CD")
    private String spSendSapSpPlantCd;

    @Column(name = "SP_SEND_SAP_SP_SAP_SHIPPT_NO")
    private String spSendSapSpSapShipptNo;

    @Column(name = "SP_SEND_SAP_SP_SHIPPT_NAME")
    private String spSendSapSpShipptName;

    @Column(name = "O_ACT_DEP_RAILWAY_STATION_ID")
    private Long oActDepRailwayStationId;

    @Column(name = "O_ACT_DEP_RS_UIC_COUNTRY_CD")
    private Long oActDepRsUicCountryCd;

    @Column(name = "O_ACT_DEP_RS_STATION_NO")
    private String oActDepRsStationNo;

    @Column(name = "O_ACT_DEP_RS_STATION_NAME")
    private String oActDepRsStationName;

    @Column(name = "O_ACT_DEP_RS_COM_UIC_CNTRY_CD")
    private Long oActDepRsComUicCntryCd;

    @Column(name = "O_ACT_DEP_RS_COM_STATION_NO")
    private String oActDepRsComStationNo;

    @Column(name = "O_ACT_DEP_RS_COM_STATION_NAME")
    private String oActDepRsComStationName;

    @Column(name = "O_ORDER_CREATION_TYPE_CD")
    private Long oOrderCreationTypeCd;

    @Column(name = "O_DELIVERY_TYPE_CD")
    private Long oDeliveryTypeCd;

    @Column(name = "O_DISTRIBUTION_DEST_CD")
    private String oDistributionDestCd;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "O_CALC_START_LOAD_FROM_DAT")
    private Date oCalcStartLoadFromDat;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "O_CALC_START_LOAD_TO_DAT")
    private Date oCalcStartLoadToDat;

    @Column(name = "O_PLAN_LOADING_TIME_MIN")
    private Long oPlanLoadingTimeMin;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "O_CALC_END_LOAD_FROM_DAT")
    private Date oCalcEndLoadFromDat;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "O_CALC_END_LOAD_TO_DAT")
    private Date oCalcEndLoadToDat;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "O_ACTUAL_START_LOAD_DAT")
    private Date oActualStartLoadDat;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "O_ACTUAL_END_LOAD_DAT")
    private Date oActualEndLoadDat;

    @Column(name = "O_CALC_LATE_START_LOAD_DAT")
    private Boolean oCalcLateStartLoadDat;

    @Column(name = "O_CALC_LATE_END_LOAD_DAT")
    private Boolean oCalcLateEndLoadDat;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "O_DELIVERY_FROM_DAT")
    private Date oDeliveryFromDat;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "O_DELIVERY_TO_DAT")
    private Date oDeliveryToDat;

    @Column(name = "O_DELIVERY_NET_TIME_WINDOW")
    private Long oDeliveryNetTimeWindow;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "O_ACTUAL_DELIVERY_DAT")
    private Date oActualDeliveryDat;

    @Column(name = "O_CALC_LATE_DELIVERY_FLAG")
    private Boolean oCalcLateDeliveryFlag;

    @Column(name = "O_TRANSP_DUR_PLAN_GOOGLE_MIN")
    private Long oTranspDurPlanGoogleMin;

    @Column(name = "O_TRANSP_DUR_PLAN_MIN")
    private Long oTranspDurPlanMin;

    @Column(name = "O_TRANSP_DISTANCE_PLAN_M")
    private Long oTranspDistancePlanM;

    @Column(name = "O_CUSTOMS_NOTIFICATION_NUMBER")
    private Long oCustomsNotificationNumber;

    @Column(name = "O_DELIVERY_TRSP_ZONE_CD")
    private String oDeliveryTrspZoneCd;

    @Column(name = "O_SPI_CD")
    private String oSpiCd;

    @Column(name = "O_CONTACT_PERSON_TXT")
    private String oContactPersonTxt;

    @Column(name = "O_CONTACT_PERSON_SIGNATURE_TXT")
    private String oContactPersonSignatureTxt;

    @Column(name = "O_NO_PDA_OR_RAIL_EVENTS_FLAG")
    private Boolean oNoPdaOrRailEventsFlag;

    @Column(name = "O_RELEASE_SAP_FLAG")
    private Boolean oReleaseSapFlag;

    @Column(name = "O_FREE_DELIVERY_FLAG")
    private Boolean oFreeDeliveryFlag;

    @Column(name = "O_SAP_SUPPRESS_MIN_CHARGE_FLAG")
    private Boolean oSapSuppressMinChargeFlag;

    @Column(name = "O_ORDER_CONF_EMAIL_FLAG")
    private Boolean oOrderConfEmailFlag;

    @Column(name = "O_ORDER_CONF_EMAIL_ADRESSES")
    private String oOrderConfEmailAdresses;

    @Column(name = "O_ORDER_CONF_FAX_FLAG")
    private Boolean oOrderConfFaxFlag;

    @Column(name = "O_ORDER_CONF_FAX_NUMBER")
    private String oOrderConfFaxNumber;

    @Column(name = "O_ORDER_CONF_SMS_FLAG")
    private Boolean oOrderConfSmsFlag;

    @Column(name = "O_ORDER_CONF_SMS_NUMBER")
    private String oOrderConfSmsNumber;

    @Column(name = "O_DELIVNOTE_EMAIL_FLAG")
    private Boolean oDelivnoteEmailFlag;

    @Column(name = "O_DELIVNOTE_FAX_FLAG")
    private Boolean oDelivnoteFaxFlag;

    @Column(name = "O_DELIVNOTE_SINGLE_PAPER_FLAG")
    private Boolean oDelivnoteSinglePaperFlag;

    @Column(name = "O_CUST_WEBORDER_NOTE")
    private String oCustWeborderNote;

    @Column(name = "O_NOTE_ORDER_MGMT")
    private String oNoteOrderMgmt;

    @Column(name = "O_NOTE_DELIVERY")
    private String oNoteDelivery;

    @Column(name = "O_NOTE_DISPATCHING")
    private String oNoteDispatching;

    @Column(name = "O_NOTE_TRUCK_DRIVER")
    private String oNoteTruckDriver;

    @Column(name = "O_NOTE_RAILWAY_DISP")
    private String oNoteRailwayDisp;

    @Column(name = "O_CARRIER_DELIVERY_NOTE")
    private String oCarrierDeliveryNote;

    @Column(name = "O_CARRIER_TRUCK_DRIVER_NOTE")
    private String oCarrierTruckDriverNote;

    @Column(name = "O_CARRIER_DISPATCHING_NOTE")
    private String oCarrierDispatchingNote;

    @Column(name = "O_CUST_DELIVERY_NOTE_COMMENT")
    private String oCustDeliveryNoteComment;

    @Column(name = "O_DELIVERED_TRUCK_ID")
    private Long oDeliveredTruckId;

    @Column(name = "TRUCK_NAME")
    private String truckName;

    @Column(name = "TRUCK_PLATE_NO")
    private String truckPlateNo;

    @Column(name = "TRUCK_TRANSPORT_CAT_CD")
    private Long truckTransportCatCd;

    @Column(name = "TRUCK_TYPE_CD")
    private Long truckTypeCd;

    @Column(name = "TRUCK_MAX_GROSS_WEIGHT_T")
    private java.math.BigDecimal truckMaxGrossWeightT;

    @Column(name = "TRUCK_TARE_WEIGHT_T")
    private java.math.BigDecimal truckTareWeightT;

    @Column(name = "TRUCK_NR_CHAMBERS")
    private Long truckNrChambers;

    @Column(name = "TRUCK_MAX_NET_W_CHAMBER1_T")
    private java.math.BigDecimal truckMaxNetWChamber1T;

    @Column(name = "TRUCK_MAX_NET_W_CHAMBER2_T")
    private java.math.BigDecimal truckMaxNetWChamber2T;

    @Column(name = "TRUCK_SAP_INTERNAL_FLAG")
    private Boolean truckSapInternalFlag;

    @Column(name = "O_DELIVERED_WAGON_ID")
    private Long oDeliveredWagonId;

    @Column(name = "WAGON_NO")
    private String wagonNo;

    @Column(name = "WAGON_TYPE_CD")
    private Long wagonTypeCd;

    @Column(name = "WAGON_TYPE_NAME")
    private String wagonTypeName;

    @Column(name = "WAGON_UIC_CLASS_CD")
    private String wagonUicClassCd;

    @Column(name = "WAGON_AXIS_COUNT")
    private Long wagonAxisCount;

    @Column(name = "WAGON_TRSP_CAT_CD")
    private Long wagonTrspCatCd;

    @Column(name = "WAGON_LIMIT_CREDIT_NOTE_T")
    private java.math.BigDecimal wagonLimitCreditNoteT;

    @Column(name = "WAGON_TARE_WEIGHT_T")
    private java.math.BigDecimal wagonTareWeightT;

    @Column(name = "WAGON_MAX_GROSS_WEIGHT_T")
    private java.math.BigDecimal wagonMaxGrossWeightT;

    @Column(name = "WAGON_VOLUME_M3")
    private java.math.BigDecimal wagonVolumeM3;

    @Column(name = "O_DELIVERED_SIMPLE_VEHICLE_ID")
    private Long oDeliveredSimpleVehicleId;

    @Column(name = "VEHICLE_NAME")
    private String vehicleName;

    @Column(name = "VEHICLE_REG_NO")
    private String vehicleRegNo;

    @Column(name = "VEHICLE_TRANSPORT_CAT_CD")
    private Long vehicleTransportCatCd;

    @Column(name = "VEHICLE_TYPE_CD")
    private Long vehicleTypeCd;

    @Column(name = "VEHICLE_MAX_GROSS_WEIGHT_T")
    private java.math.BigDecimal vehicleMaxGrossWeightT;

    @Column(name = "VEHICLE_TARE_WEIGHT_T")
    private java.math.BigDecimal vehicleTareWeightT;

    @Column(name = "VEHICLE_NR_CHAMBERS")
    private Long vehicleNrChambers;

    @Column(name = "O_DELIVERED_DRIVER_ID")
    private Long oDeliveredDriverId;

    @Column(name = "DRIVER_NAME")
    private String driverName;

    @Column(name = "DRIVER_FIRSTNAME")
    private String driverFirstname;

    @Column(name = "DRIVER_USER_TYPE_CD")
    private Long driverUserTypeCd;

    @Column(name = "DRIVER_SAP_BP_NO")
    private String driverSapBpNo;

    @Column(name = "DRIVER_SAP_VENDOR_NO")
    private String driverSapVendorNo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DRIVER_CERTIFIED_FROM_DAT")
    private Date driverCertifiedFromDat;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DRIVER_CERTIFIED_TO_DAT")
    private Date driverCertifiedToDat;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "O_SENT_SALESDOC_TO_SAP_TS")
    private Date oSentSalesdocToSapTs;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "O_SENT_SHIPMENTDOC_TO_SAP_TS")
    private Date oSentShipmentdocToSapTs;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ASSP_LOADING_PLANNED_START")
    private Date asspLoadingPlannedStart;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ASSP_LOADING_REAL_START")
    private Date asspLoadingRealStart;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ASSP_UNLOADING_PLANNED_START")
    private Date asspUnloadingPlannedStart;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ASSP_UNLOADING_REAL_START")
    private Date asspUnloadingRealStart;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BIZ_NEW_TS")
    private Date bizNewTs;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BIZ_TRANSM_POOL_TS")
    private Date bizTransmPoolTs;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BIZ_TRANSM_POOL_ACK_TS")
    private Date bizTransmPoolAckTs;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BIZ_TRANSM_COMM_CARRIER_TS")
    private Date bizTransmCommCarrierTs;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BIZ_TRANSM_COMM_CARRIER_ACK_TS")
    private Date bizTransmCommCarrierAckTs;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BIZ_TRANSM_EXEC_CARRIER_TS")
    private Date bizTransmExecCarrierTs;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BIZ_DISPATCHED_TS")
    private Date bizDispatchedTs;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BIZ_TRANSM_TRUCK_TS")
    private Date bizTransmTruckTs;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BIZ_TRANSM_TRUCK_ACK_TS")
    private Date bizTransmTruckAckTs;

    @Column(name = "BIZ_REACT_TIME_ORD_MNG_MIN")
    private Long bizReactTimeOrdMngMin;

    @Column(name = "BIZ_REACT_TIME_POOL_MNG_MIN")
    private Long bizReactTimePoolMngMin;

    @Column(name = "BIZ_REACT_TIME_DISPATCHER_MIN")
    private Long bizReactTimeDispatcherMin;

    @Column(name = "BIZ_REACT_TIME_TRUCK_DRV_MIN")
    private Long bizReactTimeTruckDrvMin;

    @Column(name = "BIZ_REACT_TIME_NET_WINDOW_MIN")
    private Long bizReactTimeNetWindowMin;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BIZ_ENTER_SP_TS")
    private Date bizEnterSpTs;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BIZ_START_LOAD_TS")
    private Date bizStartLoadTs;

    @Column(name = "BIZ_WAITINGTIME_REASON_SP_CD")
    private Long bizWaitingtimeReasonSpCd;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BIZ_END_LOAD_TS")
    private Date bizEndLoadTs;

    @Column(name = "BIZ_LOADING_TIME_MIN")
    private Long bizLoadingTimeMin;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BIZ_LEAVE_SP_TS")
    private Date bizLeaveSpTs;

    @Column(name = "BIZ_CYCLE_TIME_AT_SP_MIN")
    private Long bizCycleTimeAtSpMin;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BIZ_TRANS_RAIL_COMPANY_TS")
    private Date bizTransRailCompanyTs;

    @Column(name = "BIZ_TRAFFICJAM_COUNT")
    private Double bizTrafficjamCount;

    @Column(name = "BIZ_TRAFFICJAM_DURATION_MIN")
    private Long bizTrafficjamDurationMin;

    @Column(name = "BIZ_BREAK_DRIVING_COUNT")
    private Long bizBreakDrivingCount;

    @Column(name = "BIZ_BREAK_DRIVING_DURATION_MIN")
    private Long bizBreakDrivingDurationMin;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BIZ_ENTER_ST_TS")
    private Date bizEnterStTs;

    @Column(name = "BIZ_CALC_DRIVING_TIME_MIN")
    private Long bizCalcDrivingTimeMin;

    @Column(name = "BIZ_CALC_NET_DRIVING_TIME_MIN")
    private Long bizCalcNetDrivingTimeMin;

    @Column(name = "BIZ_ACTUAL_DISTANCE_M")
    private Long bizActualDistanceM;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BIZ_START_UNLOAD_TS")
    private Date bizStartUnloadTs;

    @Column(name = "BIZ_WAITINGTIME_REASON_ST_CD")
    private Long bizWaitingtimeReasonStCd;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BIZ_END_UNLOAD_TS")
    private Date bizEndUnloadTs;

    @Column(name = "BIZ_UNLOADING_MIN")
    private Long bizUnloadingMin;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BIZ_LEAVE_ST_TS")
    private Date bizLeaveStTs;

    @Column(name = "BIZ_CYCLE_TIME_AT_SHIPTO_MIN")
    private Long bizCycleTimeAtShiptoMin;

    @Column(name = "BIZ_GEOEVENT_COUNT")
    private Double bizGeoeventCount;

    @Column(name = "O_SHORT_TERM_ORDER_FLAG")
    private Boolean oShortTermOrderFlag;

    @Column(name = "O_TRIP_INC_IN_STATISTICS_FLAG")
    private Boolean oTripIncInStatisticsFlag;

    @Column(name = "O_RESCHEDULING_FLAG")
    private Boolean oReschedulingFlag;
    
    @Column( name ="O_ORDER_LIFEX_NO")
    private String oLifexNo;

	@Column(name = "O_LNF_COORD_LAT")
    private Double oLnfCoordLat;

    @Column(name = "O_LNF_COORD_LON")
    private Double oLnfCoordLon;

    @Column(name = "O_LNF_SITE_NAME")
    private String oLnfSiteName;

    @Column(name = "O_LNF_SITE_STREET_HOUSE_NO")
    private String oLnfSiteStreetHouseNo;

    @Column(name = "O_LNF_SITE_COUNTRY")
    private String oLnfSiteCountry;

    @Column(name = "O_LNF_SITE_POSTAL_CODE")
    private String oLnfSitePostalCode;

    @Column(name = "O_LNF_SITE_CITY")
    private String oLnfSiteCity;

    @Column(name = "O_LNF_SITE_VAT_NO")
    private String oLnfSiteVatNo;

    @Column(name = "O_ONE_TIME_SOLDTO_NAME")
    private String oOneTimeSoldtoName;

    @Column(name = "O_ONE_TIME_SOLDTO_STREET_HNO")
    private String oOneTimeSoldtoStreetHno;

    @Column(name = "O_ONE_TIME_SOLDTO_CITY")
    private String oOneTimeSoldtoCity;

    @Column(name = "O_ONE_TIME_SOLDTO_POSTAL_CODE")
    private String oOneTimeSoldtoPostalCode;

    @Column(name = "O_ONE_TIME_SOLDTO_COUNTRY")
    private String oOneTimeSoldtoCountry;

    @Column(name = "O_ONE_TIME_SOLDTO_VAT_NO")
    private String oOneTimeSoldtoVatNo;

    @Column(name = "REPORT_SVC_LEVEL_RELEVANT_FLAG")
    private Boolean reportSvcLevelRelevantFlag;

    @Column(name = "REPORT_NOTE_FROM_CRH")
    private String reportNoteFromCrh;

    @Column(name = "REPORT_NOTE_FROM_CARRIER")
    private String reportNoteFromCarrier;

    @Column(name = "REPORT_FINISHED_COMM_FLAG")
    private Boolean reportFinishedCommFlag;

    @Column(name = "O_CTL_CRE_UID")
    private Long oCtlCreUid;

    @Column(name = "O_CTL_CRE_UID_NAME")
    private String oCtlCreUidName;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "O_CTL_CRE_TS")
    private Date oCtlCreTs;

    @Column(name = "O_CTL_MOD_UID")
    private Long oCtlModUid;

    @Column(name = "O_CTL_MOD_UID_NAME")
    private String oCtlModUidName;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "O_CTL_MOD_TS")
    private Date oCtlModTs;

    @Column(name = "O_BP_CORRECTED_FLAG")
    private Boolean oBpCorrectedFlag;

    @Column(name = "EXTRA_COST_WAITING_TIME_MIN")
    private Long extraCostWaitingTimeMin;

    @Column(name = "EXTRA_COST_1")
    private String extraCost1;

    @Column(name = "EXTRA_COST_2")
    private String extraCost2;

    @Column(name = "EXTRA_COST_3")
    private String extraCost3;

    @Column(name = "EXTRA_COST_4")
    private String extraCost4;

    @Column(name = "O_DELIV_TYPE_PLAN_ACTUAL")
    private Boolean oDelivTypePlanActual;
    
    @Column(name = "O_FORCE_TO_SAP_FLAG")
    private Boolean oForceToSapFlag;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "O_ARRIVAL_TS")
    private Date oArrivalTs;
    
    @Column(name = "CTL_CRE_UID")
    private Long ctlCreUid;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CTL_CRE_TS")
    private Date ctlCreTs;

    @Column(name = "CTL_MOD_UID")
    private Long ctlModUid;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CTL_MOD_TS")
    private Date ctlModTs;

    @Column(name = "CTL_TCN")
    @Version
    private Long ctlTcn;
    
    @OneToMany(mappedBy="orderFinal", targetEntity=OrderItemFinalEntity.class, cascade = CascadeType.ALL)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<OrderItemFinalEntity> orderFinalItems;

    // Fieldnames
    public static final String ATTR_ORDER_FINAL_ID = "orderFinalId";
    public static final String ATTR_O_ORDER_ID = "oOrderId";
    public static final String ATTR_O_ORDER_NUMBER = "oOrderNumber";
    public static final String ATTR_O_ORDER_STATUS_CD = "oOrderStatusCd";
    public static final String ATTR_O_SHIPTO_SAP_BP_ID = "oShiptoSapBpId";
    public static final String ATTR_WE_SAP_ID = "weSapId";
    public static final String ATTR_WE_MARKED_FOR_DELETION = "weMarkedForDeletion";
    public static final String ATTR_WE_NAME1 = "weName1";
    public static final String ATTR_WE_NAME2 = "weName2";
    public static final String ATTR_WE_COUNTRY = "weCountry";
    public static final String ATTR_WE_POSTAL_CODE = "wePostalCode";
    public static final String ATTR_WE_CITY = "weCity";
    public static final String ATTR_WE_REGION = "weRegion";
    public static final String ATTR_WE_STREET = "weStreet";
    public static final String ATTR_WE_HOUSE_NUMBER = "weHouseNumber";
    public static final String ATTR_WE_LANG_CD = "weLangCd";
    public static final String ATTR_WE_CUSTOMER_CLASSIFICATION = "weCustomerClassification";
    public static final String ATTR_WE_RAILWAY_STATION_ID = "weRailwayStationId";
    public static final String ATTR_WE_RS_UIC_COUNTRY_CD = "weRsUicCountryCd";
    public static final String ATTR_WE_RS_STATION_NO = "weRsStationNo";
    public static final String ATTR_WE_RS_STATION_NAME = "weRsStationName";
    public static final String ATTR_WE_RS_COM_UIC_COUNTRY_CD = "weRsComUicCountryCd";
    public static final String ATTR_WE_RS_COM_STATION_NO = "weRsComStationNo";
    public static final String ATTR_WE_RS_COM_STATION_NAME = "weRsComStationName";
    public static final String ATTR_WE_TRAIN_ST_UNLOADING_POINT = "weTrainStUnloadingPoint";
    public static final String ATTR_WE_SBB_CUSTOMER_NUMBER = "weSbbCustomerNumber";
    public static final String ATTR_WE_BP_IS_SHIPTO = "weBpIsShipto";
    public static final String ATTR_WE_BP_IS_SOLDTO = "weBpIsSoldto";
    public static final String ATTR_WE_BP_IS_BILLTO = "weBpIsBillto";
    public static final String ATTR_WE_SAP_BP_SAREA_ID = "weSapBpSareaId";
    public static final String ATTR_WE_SAREA_MARKED_FOR_DELETION = "weSareaMarkedForDeletion";
    public static final String ATTR_WE_SALES_DISTRICT = "weSalesDistrict";
    public static final String ATTR_WE_SOFFICE_CD = "weSofficeCd";
    public static final String ATTR_WE_SOFFICE_NAME = "weSofficeName";
    public static final String ATTR_WE_SGROUP_CD = "weSgroupCd";
    public static final String ATTR_WE_SGROUP_NAME = "weSgroupName";
    public static final String ATTR_WE_SGROUP_EMAIL_ADDRESSES = "weSgroupEmailAddresses";
    public static final String ATTR_WE_CUSTOMER_GROUP = "weCustomerGroup";
    public static final String ATTR_WE_ABC_CLASS = "weAbcClass";
    public static final String ATTR_WE_CURRENCY_CD = "weCurrencyCd";
    public static final String ATTR_WE_DISTRIBUTION_DEST_CD = "weDistributionDestCd";
    public static final String ATTR_WE_CUSTOMER_ORDER_BLOCK = "weCustomerOrderBlock";
    public static final String ATTR_WE_CUSTOMER_DELIVERY_BLOCK = "weCustomerDeliveryBlock";
    public static final String ATTR_WE_CUSTOM_OFFICE = "weCustomOffice";
    public static final String ATTR_WE_CHROMAT_REDUCTION_MONTHS = "weChromatReductionMonths";
    public static final String ATTR_WE_DEFAULT_PLANT_CD = "weDefaultPlantCd";
    public static final String ATTR_WE_SHIPTO_ID = "weShiptoId";
    public static final String ATTR_WE_ACTIVE_FLAG = "weActiveFlag";
    public static final String ATTR_WE_SHIPTO_TYPE_CD = "weShiptoTypeCd";
    public static final String ATTR_WE_SIGNATURE_TYPE_CD = "weSignatureTypeCd";
    public static final String ATTR_WE_COORD_LAT = "weCoordLat";
    public static final String ATTR_WE_COORD_LON = "weCoordLon";
    public static final String ATTR_WE_COORD_FIX_FLAG = "weCoordFixFlag";
    public static final String ATTR_WE_GEOFENCE_RADIUS = "weGeofenceRadius";
    public static final String ATTR_WE_UNLOADING_TIME = "weUnloadingTime";
    public static final String ATTR_WE_RAIL_CHAIN_SHIPPINGPOINT_ID = "weRailChainShippingpointId";
    public static final String ATTR_WE_RAIL_CHAIN_SP_PLANT_CD = "weRailChainSpPlantCd";
    public static final String ATTR_WE_RAIL_CHAIN_SP_SAP_SHIPPT_NO = "weRailChainSpSapShipptNo";
    public static final String ATTR_WE_RAIL_CHAIN_SP_SHIPPT_NAME = "weRailChainSpShipptName";
    public static final String ATTR_WE_MAT_CHAIN_SHIPPINGPOINT_ID = "weMatChainShippingpointId";
    public static final String ATTR_WE_MAT_CHAIN_SP_PLANT_CD = "weMatChainSpPlantCd";
    public static final String ATTR_WE_MAT_CHAIN_SP_SAP_SHIPPT_NO = "weMatChainSpSapShipptNo";
    public static final String ATTR_WE_MAT_CHAIN_SP_SHIPPT_NAME = "weMatChainSpShipptName";
    public static final String ATTR_WE_RAIL_HOLD_BACK_RE_ORDER = "weRailHoldBackReOrder";
    public static final String ATTR_WE_OPTIMAL_TRUCK_TYPE_CD = "weOptimalTruckTypeCd";
    public static final String ATTR_WE_OPTIMAL_WAGON_TYPE_CD = "weOptimalWagonTypeCd";
    public static final String ATTR_WE_ST_SAREA_SHIPTO_SAREA_ID = "weStSareaShiptoSareaId";
    public static final String ATTR_WE_ST_SAREA_FREE_DELIVERY = "weStSareaFreeDelivery";
    public static final String ATTR_WE_ST_SAREA_RELEASE_SAP_OPT = "weStSareaReleaseSapOpt";
    public static final String ATTR_WE_ST_DISTRIBUTION_DEST_CD = "weStDistributionDestCd";
    public static final String ATTR_O_SOLDTO_SAP_BP_ID = "oSoldtoSapBpId";
    public static final String ATTR_AG_SAP_ID = "agSapId";
    public static final String ATTR_AG_MARKED_FOR_DELETION = "agMarkedForDeletion";
    public static final String ATTR_AG_NAME1 = "agName1";
    public static final String ATTR_AG_NAME2 = "agName2";
    public static final String ATTR_AG_COUNTRY = "agCountry";
    public static final String ATTR_AG_POSTAL_CODE = "agPostalCode";
    public static final String ATTR_AG_CITY = "agCity";
    public static final String ATTR_AG_REGION = "agRegion";
    public static final String ATTR_AG_STREET = "agStreet";
    public static final String ATTR_AG_HOUSE_NUMBER = "agHouseNumber";
    public static final String ATTR_AG_LANG_CD = "agLangCd";
    public static final String ATTR_AG_CUSTOMER_CLASSIFICATION = "agCustomerClassification";
    public static final String ATTR_AG_RAILWAY_STATION_ID = "agRailwayStationId";
    public static final String ATTR_AG_TRAIN_ST_UNLOADING_POINT = "agTrainStUnloadingPoint";
    public static final String ATTR_AG_SBB_CUSTOMER_NUMBER = "agSbbCustomerNumber";
    public static final String ATTR_AG_BP_IS_SHIPTO = "agBpIsShipto";
    public static final String ATTR_AG_BP_IS_SOLDTO = "agBpIsSoldto";
    public static final String ATTR_AG_BP_IS_BILLTO = "agBpIsBillto";
    public static final String ATTR_AG_SAP_BP_SAREA_ID = "agSapBpSareaId";
    public static final String ATTR_AG_SAREA_MARKED_FOR_DELETION = "agSareaMarkedForDeletion";
    public static final String ATTR_AG_SALES_DISTRICT = "agSalesDistrict";
    public static final String ATTR_AG_SOFFICE_CD = "agSofficeCd";
    public static final String ATTR_AG_SOFFICE_NAME = "agSofficeName";
    public static final String ATTR_AG_SGROUP_CD = "agSgroupCd";
    public static final String ATTR_AG_SGROUP_NAME = "agSgroupName";
    public static final String ATTR_AG_SGROUP_EMAIL_ADDRESSES = "agSgroupEmailAddresses";
    public static final String ATTR_AG_CUSTOMER_GROUP = "agCustomerGroup";
    public static final String ATTR_AG_ABC_CLASS = "agAbcClass";
    public static final String ATTR_AG_CURRENCY_CD = "agCurrencyCd";
    public static final String ATTR_AG_DISTRIBUTION_DEST_CD = "agDistributionDestCd";
    public static final String ATTR_AG_CUSTOMER_ORDER_BLOCK = "agCustomerOrderBlock";
    public static final String ATTR_AG_CUSTOMER_DELIVERY_BLOCK = "agCustomerDeliveryBlock";
    public static final String ATTR_AG_CUSTOM_OFFICE = "agCustomOffice";
    public static final String ATTR_AG_CHROMAT_REDUCTION_MONTHS = "agChromatReductionMonths";
    public static final String ATTR_AG_DEFAULT_PLANT_CD = "agDefaultPlantCd";
    public static final String ATTR_O_BILLTO_SAP_BP_ID = "oBilltoSapBpId";
    public static final String ATTR_RE_SAP_ID = "reSapId";
    public static final String ATTR_RE_MARKED_FOR_DELETION = "reMarkedForDeletion";
    public static final String ATTR_RE_NAME1 = "reName1";
    public static final String ATTR_RE_NAME2 = "reName2";
    public static final String ATTR_RE_COUNTRY = "reCountry";
    public static final String ATTR_RE_POSTAL_CODE = "rePostalCode";
    public static final String ATTR_RE_CITY = "reCity";
    public static final String ATTR_RE_REGION = "reRegion";
    public static final String ATTR_RE_STREET = "reStreet";
    public static final String ATTR_RE_HOUSE_NUMBER = "reHouseNumber";
    public static final String ATTR_RE_LANG_CD = "reLangCd";
    public static final String ATTR_RE_CUSTOMER_CLASSIFICATION = "reCustomerClassification";
    public static final String ATTR_RE_RAILWAY_STATION_ID = "reRailwayStationId";
    public static final String ATTR_RE_TRAIN_ST_UNLOADING_POINT = "reTrainStUnloadingPoint";
    public static final String ATTR_RE_SBB_CUSTOMER_NUMBER = "reSbbCustomerNumber";
    public static final String ATTR_RE_BP_IS_SHIPTO = "reBpIsShipto";
    public static final String ATTR_RE_BP_IS_SOLDTO = "reBpIsSoldto";
    public static final String ATTR_RE_BP_IS_BILLTO = "reBpIsBillto";
    public static final String ATTR_RE_SAP_BP_SAREA_ID = "reSapBpSareaId";
    public static final String ATTR_RE_SAREA_MARKED_FOR_DELETION = "reSareaMarkedForDeletion";
    public static final String ATTR_RE_SALES_DISTRICT = "reSalesDistrict";
    public static final String ATTR_RE_SOFFICE_CD = "reSofficeCd";
    public static final String ATTR_RE_SGROUP_CD = "reSgroupCd";
    public static final String ATTR_RE_CUSTOMER_GROUP = "reCustomerGroup";
    public static final String ATTR_RE_ABC_CLASS = "reAbcClass";
    public static final String ATTR_RE_CURRENCY_CD = "reCurrencyCd";
    public static final String ATTR_RE_DISTRIBUTION_DEST_CD = "reDistributionDestCd";
    public static final String ATTR_RE_CUSTOMER_ORDER_BLOCK = "reCustomerOrderBlock";
    public static final String ATTR_RE_CUSTOMER_DELIVERY_BLOCK = "reCustomerDeliveryBlock";
    public static final String ATTR_RE_CUSTOM_OFFICE = "reCustomOffice";
    public static final String ATTR_RE_CHROMAT_REDUCTION_MONTHS = "reChromatReductionMonths";
    public static final String ATTR_RE_DEFAULT_PLANT_CD = "reDefaultPlantCd";
    public static final String ATTR_O_SAREA_ID = "oSareaId";
    public static final String ATTR_SAREA_DISPLAY_LONGNAME = "sareaDisplayLongname";
    public static final String ATTR_SAREA_SORG_CD = "sareaSorgCd";
    public static final String ATTR_SAREA_DISTRIBUTION_CHANNEL = "sareaDistributionChannel";
    public static final String ATTR_SAREA_DIVISION_CD = "sareaDivisionCd";
    public static final String ATTR_SAREA_DIVISION_SEGMENT_CD = "sareaDivisionSegmentCd";
    public static final String ATTR_SAREA_COMPANYCODE_CD = "sareaCompanycodeCd";
    public static final String ATTR_SAREA_COUNTRY_CD = "sareaCountryCd";
    public static final String ATTR_O_POOL_ID = "oPoolId";
    public static final String ATTR_O_COMM_CARRIER_ID = "oCommCarrierId";
    public static final String ATTR_COMM_CARRIER_DISPLAY_NAME = "commCarrierDisplayName";
    public static final String ATTR_COMM_CARRIER_SAP_VENDOR_ID = "commCarrierSapVendorId";
    public static final String ATTR_COMM_CARRIER_V_SAP_ID = "commCarrierVSapId";
    public static final String ATTR_COMM_CARRIER_V_ACCOUNT_GROUP = "commCarrierVAccountGroup";
    public static final String ATTR_COMM_CARRIER_V_MARKED_FOR_DEL = "commCarrierVMarkedForDel";
    public static final String ATTR_COMM_CARRIER_V_NAME1 = "commCarrierVName1";
    public static final String ATTR_COMM_CARRIER_V_NAME2 = "commCarrierVName2";
    public static final String ATTR_COMM_CARRIER_V_COUNTRY_CD = "commCarrierVCountryCd";
    public static final String ATTR_COMM_CARRIER_V_POSTAL_CODE = "commCarrierVPostalCode";
    public static final String ATTR_COMM_CARRIER_V_CITY = "commCarrierVCity";
    public static final String ATTR_COMM_CARRIER_V_LOCATION_DIST = "commCarrierVLocationDist";
    public static final String ATTR_COMM_CARRIER_V_REGION = "commCarrierVRegion";
    public static final String ATTR_COMM_CARRIER_V_STREET = "commCarrierVStreet";
    public static final String ATTR_COMM_CARRIER_V_HOUSE_NUMBER = "commCarrierVHouseNumber";
    public static final String ATTR_COMM_CARRIER_V_LANG_CD = "commCarrierVLangCd";
    public static final String ATTR_COMM_CARRIER_V_VAT_REGIST_NO = "commCarrierVVatRegistNo";
    public static final String ATTR_COMM_CARRIER_IS_INTERNAL = "commCarrierIsInternal";
    public static final String ATTR_O_EXEC_CARRIER_ID = "oExecCarrierId";
    public static final String ATTR_EXEC_CARRIER_DISPLAY_NAME = "execCarrierDisplayName";
    public static final String ATTR_EXEC_CARRIER_SAP_VENDOR_ID = "execCarrierSapVendorId";
    public static final String ATTR_EXEC_CARRIER_V_SAP_ID = "execCarrierVSapId";
    public static final String ATTR_EXEC_CARRIER_V_ACCOUNT_GROUP = "execCarrierVAccountGroup";
    public static final String ATTR_EXEC_CARRIER_V_MARKED_FOR_DEL = "execCarrierVMarkedForDel";
    public static final String ATTR_EXEC_CARRIER_V_NAME1 = "execCarrierVName1";
    public static final String ATTR_EXEC_CARRIER_V_NAME2 = "execCarrierVName2";
    public static final String ATTR_EXEC_CARRIER_V_COUNTRY_CD = "execCarrierVCountryCd";
    public static final String ATTR_EXEC_CARRIER_V_POSTAL_CODE = "execCarrierVPostalCode";
    public static final String ATTR_EXEC_CARRIER_V_CITY = "execCarrierVCity";
    public static final String ATTR_EXEC_CARRIER_V_LOCATION_DIST = "execCarrierVLocationDist";
    public static final String ATTR_EXEC_CARRIER_V_REGION = "execCarrierVRegion";
    public static final String ATTR_EXEC_CARRIER_V_STREET = "execCarrierVStreet";
    public static final String ATTR_EXEC_CARRIER_V_HOUSE_NUMBER = "execCarrierVHouseNumber";
    public static final String ATTR_EXEC_CARRIER_V_LANG_CD = "execCarrierVLangCd";
    public static final String ATTR_EXEC_CARRIER_V_VAT_REGIST_NO = "execCarrierVVatRegistNo";
    public static final String ATTR_EXEC_CARRIER_IS_INTERNAL = "execCarrierIsInternal";
    public static final String ATTR_O_SHIPPINGPOINT_ID = "oShippingpointId";
    public static final String ATTR_SENT_KPI_TO_SAP_BW_TS = "sentKpiToSapBwTs";
    public static final String ATTR_SP_PLANT_CD = "spPlantCd";
    public static final String ATTR_SP_PLANT_NAME = "spPlantName";
    public static final String ATTR_SP_SAP_SHIPPINGPOINT_NO = "spSapShippingpointNo";
    public static final String ATTR_SP_SHIPPINGPOINT_NAME = "spShippingpointName";
    public static final String ATTR_SP_SHIPPPOINT_TYPE_CD = "spShipppointTypeCd";
    public static final String ATTR_SP_SAP_SHIPPING_COND_CD = "spSapShippingCondCd";
    public static final String ATTR_SP_STORAGE_LOCATION = "spStorageLocation";
    public static final String ATTR_SP_RAILWAY_STATION_ID = "spRailwayStationId";
    public static final String ATTR_SP_RS_UIC_COUNTRY_CD = "spRsUicCountryCd";
    public static final String ATTR_SP_RS_RAILWAY_STATION_NO = "spRsRailwayStationNo";
    public static final String ATTR_SP_RS_RAILWAY_STATION_NAME = "spRsRailwayStationName";
    public static final String ATTR_SP_RAILWAY_STATION_LP = "spRailwayStationLp";
    public static final String ATTR_SP_WEIGH_SYSTEM_SP_CD = "spWeighSystemSpCd";
    public static final String ATTR_SP_LDS_SERVER = "spLdsServer";
    public static final String ATTR_SP_SHIPPINGPOINT_COMMENT = "spShippingpointComment";
    public static final String ATTR_SP_COORD_LAT = "spCoordLat";
    public static final String ATTR_SP_COORD_LON = "spCoordLon";
    public static final String ATTR_SP_COORD_FIX_FLAG = "spCoordFixFlag";
    public static final String ATTR_SP_GEOFENCE_RADIUS = "spGeofenceRadius";
    public static final String ATTR_SP_FVL_MIX_SHIPTO_PER_WAGON_FL = "spFvlMixShiptoPerWagonFl";
    public static final String ATTR_SP_SEND_SAP_SHIPPINGPOINT_ID = "spSendSapShippingpointId";
    public static final String ATTR_SP_SEND_SAP_SP_PLANT_CD = "spSendSapSpPlantCd";
    public static final String ATTR_SP_SEND_SAP_SP_SAP_SHIPPT_NO = "spSendSapSpSapShipptNo";
    public static final String ATTR_SP_SEND_SAP_SP_SHIPPT_NAME = "spSendSapSpShipptName";
    public static final String ATTR_O_ACT_DEP_RAILWAY_STATION_ID = "oActDepRailwayStationId";
    public static final String ATTR_O_ACT_DEP_RS_UIC_COUNTRY_CD = "oActDepRsUicCountryCd";
    public static final String ATTR_O_ACT_DEP_RS_STATION_NO = "oActDepRsStationNo";
    public static final String ATTR_O_ACT_DEP_RS_STATION_NAME = "oActDepRsStationName";
    public static final String ATTR_O_ACT_DEP_RS_COM_UIC_CNTRY_CD = "oActDepRsComUicCntryCd";
    public static final String ATTR_O_ACT_DEP_RS_COM_STATION_NO = "oActDepRsComStationNo";
    public static final String ATTR_O_ACT_DEP_RS_COM_STATION_NAME = "oActDepRsComStationName";
    public static final String ATTR_O_ORDER_CREATION_TYPE_CD = "oOrderCreationTypeCd";
    public static final String ATTR_O_DELIVERY_TYPE_CD = "oDeliveryTypeCd";
    public static final String ATTR_O_DISTRIBUTION_DEST_CD = "oDistributionDestCd";
    public static final String ATTR_O_CALC_START_LOAD_FROM_DAT = "oCalcStartLoadFromDat";
    public static final String ATTR_O_CALC_START_LOAD_TO_DAT = "oCalcStartLoadToDat";
    public static final String ATTR_O_PLAN_LOADING_TIME_MIN = "oPlanLoadingTimeMin";
    public static final String ATTR_O_CALC_END_LOAD_FROM_DAT = "oCalcEndLoadFromDat";
    public static final String ATTR_O_CALC_END_LOAD_TO_DAT = "oCalcEndLoadToDat";
    public static final String ATTR_O_ACTUAL_START_LOAD_DAT = "oActualStartLoadDat";
    public static final String ATTR_O_ACTUAL_END_LOAD_DAT = "oActualEndLoadDat";
    public static final String ATTR_O_CALC_LATE_START_LOAD_DAT = "oCalcLateStartLoadDat";
    public static final String ATTR_O_CALC_LATE_END_LOAD_DAT = "oCalcLateEndLoadDat";
    public static final String ATTR_O_DELIVERY_FROM_DAT = "oDeliveryFromDat";
    public static final String ATTR_O_DELIVERY_TO_DAT = "oDeliveryToDat";
    public static final String ATTR_O_DELIVERY_NET_TIME_WINDOW = "oDeliveryNetTimeWindow";
    public static final String ATTR_O_ACTUAL_DELIVERY_DAT = "oActualDeliveryDat";
    public static final String ATTR_O_CALC_LATE_DELIVERY_FLAG = "oCalcLateDeliveryFlag";
    public static final String ATTR_O_TRANSP_DUR_PLAN_GOOGLE_MIN = "oTranspDurPlanGoogleMin";
    public static final String ATTR_O_TRANSP_DUR_PLAN_MIN = "oTranspDurPlanMin";
    public static final String ATTR_O_TRANSP_DISTANCE_PLAN_M = "oTranspDistancePlanM";
    public static final String ATTR_O_CUSTOMS_NOTIFICATION_NUMBER = "oCustomsNotificationNumber";
    public static final String ATTR_O_DELIVERY_TRSP_ZONE_CD = "oDeliveryTrspZoneCd";
    public static final String ATTR_O_SPI_CD = "oSpiCd";
    public static final String ATTR_O_CONTACT_PERSON_TXT = "oContactPersonTxt";
    public static final String ATTR_O_CONTACT_PERSON_SIGNATURE_TXT = "oContactPersonSignatureTxt";
    public static final String ATTR_O_NO_PDA_OR_RAIL_EVENTS_FLAG = "oNoPdaOrRailEventsFlag";
    public static final String ATTR_O_RELEASE_SAP_FLAG = "oReleaseSapFlag";
    public static final String ATTR_O_FORCE_TO_SAP_FLAG = "oForceToSapFlag";
    public static final String ATTR_O_FREE_DELIVERY_FLAG = "oFreeDeliveryFlag";
    public static final String ATTR_O_SAP_SUPPRESS_MIN_CHARGE_FLAG = "oSapSuppressMinChargeFlag";
    public static final String ATTR_O_ORDER_CONF_EMAIL_FLAG = "oOrderConfEmailFlag";
    public static final String ATTR_O_ORDER_CONF_EMAIL_ADRESSES = "oOrderConfEmailAdresses";
    public static final String ATTR_O_ORDER_CONF_FAX_FLAG = "oOrderConfFaxFlag";
    public static final String ATTR_O_ORDER_CONF_FAX_NUMBER = "oOrderConfFaxNumber";
    public static final String ATTR_O_ORDER_CONF_SMS_FLAG = "oOrderConfSmsFlag";
    public static final String ATTR_O_ORDER_CONF_SMS_NUMBER = "oOrderConfSmsNumber";
    public static final String ATTR_O_DELIVNOTE_EMAIL_FLAG = "oDelivnoteEmailFlag";
    public static final String ATTR_O_DELIVNOTE_FAX_FLAG = "oDelivnoteFaxFlag";
    public static final String ATTR_O_DELIVNOTE_SINGLE_PAPER_FLAG = "oDelivnoteSinglePaperFlag";
    public static final String ATTR_O_CUST_WEBORDER_NOTE = "oCustWeborderNote";
    public static final String ATTR_O_NOTE_ORDER_MGMT = "oNoteOrderMgmt";
    public static final String ATTR_O_NOTE_DELIVERY = "oNoteDelivery";
    public static final String ATTR_O_NOTE_DISPATCHING = "oNoteDispatching";
    public static final String ATTR_O_NOTE_TRUCK_DRIVER = "oNoteTruckDriver";
    public static final String ATTR_O_NOTE_RAILWAY_DISP = "oNoteRailwayDisp";
    public static final String ATTR_O_CARRIER_DELIVERY_NOTE = "oCarrierDeliveryNote";
    public static final String ATTR_O_CARRIER_TRUCK_DRIVER_NOTE = "oCarrierTruckDriverNote";
    public static final String ATTR_O_CARRIER_DISPATCHING_NOTE = "oCarrierDispatchingNote";
    public static final String ATTR_O_CUST_DELIVERY_NOTE_COMMENT = "oCustDeliveryNoteComment";
    public static final String ATTR_O_DELIVERED_TRUCK_ID = "oDeliveredTruckId";
    public static final String ATTR_TRUCK_NAME = "truckName";
    public static final String ATTR_TRUCK_PLATE_NO = "truckPlateNo";
    public static final String ATTR_TRUCK_TRANSPORT_CAT_CD = "truckTransportCatCd";
    public static final String ATTR_TRUCK_TYPE_CD = "truckTypeCd";
    public static final String ATTR_TRUCK_MAX_GROSS_WEIGHT_T = "truckMaxGrossWeightT";
    public static final String ATTR_TRUCK_TARE_WEIGHT_T = "truckTareWeightT";
    public static final String ATTR_TRUCK_NR_CHAMBERS = "truckNrChambers";
    public static final String ATTR_TRUCK_MAX_NET_W_CHAMBER1_T = "truckMaxNetWChamber1T";
    public static final String ATTR_TRUCK_MAX_NET_W_CHAMBER2_T = "truckMaxNetWChamber2T";
    public static final String ATTR_TRUCK_SAP_INTERNAL_FLAG = "truckSapInternalFlag";
    public static final String ATTR_O_DELIVERED_WAGON_ID = "oDeliveredWagonId";
    public static final String ATTR_WAGON_NO = "wagonNo";
    public static final String ATTR_WAGON_TYPE_CD = "wagonTypeCd";
    public static final String ATTR_WAGON_TYPE_NAME = "wagonTypeName";
    public static final String ATTR_WAGON_UIC_CLASS_CD = "wagonUicClassCd";
    public static final String ATTR_WAGON_AXIS_COUNT = "wagonAxisCount";
    public static final String ATTR_WAGON_TRSP_CAT_CD = "wagonTrspCatCd";
    public static final String ATTR_WAGON_LIMIT_CREDIT_NOTE_T = "wagonLimitCreditNoteT";
    public static final String ATTR_WAGON_TARE_WEIGHT_T = "wagonTareWeightT";
    public static final String ATTR_WAGON_MAX_GROSS_WEIGHT_T = "wagonMaxGrossWeightT";
    public static final String ATTR_WAGON_VOLUME_M3 = "wagonVolumeM3";
    public static final String ATTR_O_DELIVERED_SIMPLE_VEHICLE_ID = "oDeliveredSimpleVehicleId";
    public static final String ATTR_VEHICLE_NAME = "vehicleName";
    public static final String ATTR_VEHICLE_REG_NO = "vehicleRegNo";
    public static final String ATTR_VEHICLE_TRANSPORT_CAT_CD = "vehicleTransportCatCd";
    public static final String ATTR_VEHICLE_TYPE_CD = "vehicleTypeCd";
    public static final String ATTR_VEHICLE_MAX_GROSS_WEIGHT_T = "vehicleMaxGrossWeightT";
    public static final String ATTR_VEHICLE_TARE_WEIGHT_T = "vehicleTareWeightT";
    public static final String ATTR_VEHICLE_NR_CHAMBERS = "vehicleNrChambers";
    public static final String ATTR_O_DELIVERED_DRIVER_ID = "oDeliveredDriverId";
    public static final String ATTR_DRIVER_NAME = "driverName";
    public static final String ATTR_DRIVER_FIRSTNAME = "driverFirstname";
    public static final String ATTR_DRIVER_USER_TYPE_CD = "driverUserTypeCd";
    public static final String ATTR_DRIVER_SAP_BP_NO = "driverSapBpNo";
    public static final String ATTR_DRIVER_SAP_VENDOR_NO = "driverSapVendorNo";
    public static final String ATTR_DRIVER_CERTIFIED_FROM_DAT = "driverCertifiedFromDat";
    public static final String ATTR_DRIVER_CERTIFIED_TO_DAT = "driverCertifiedToDat";
    public static final String ATTR_O_SENT_SALESDOC_TO_SAP_TS = "oSentSalesdocToSapTs";
    public static final String ATTR_O_SENT_SHIPMENTDOC_TO_SAP_TS = "oSentShipmentdocToSapTs";
    public static final String ATTR_ASSP_LOADING_PLANNED_START = "asspLoadingPlannedStart";
    public static final String ATTR_ASSP_LOADING_REAL_START = "asspLoadingRealStart";
    public static final String ATTR_ASSP_UNLOADING_PLANNED_START = "asspUnloadingPlannedStart";
    public static final String ATTR_ASSP_UNLOADING_REAL_START = "asspUnloadingRealStart";
    public static final String ATTR_BIZ_NEW_TS = "bizNewTs";
    public static final String ATTR_BIZ_TRANSM_POOL_TS = "bizTransmPoolTs";
    public static final String ATTR_BIZ_TRANSM_POOL_ACK_TS = "bizTransmPoolAckTs";
    public static final String ATTR_BIZ_TRANSM_COMM_CARRIER_TS = "bizTransmCommCarrierTs";
    public static final String ATTR_BIZ_TRANSM_COMM_CARRIER_ACK_TS = "bizTransmCommCarrierAckTs";
    public static final String ATTR_BIZ_TRANSM_EXEC_CARRIER_TS = "bizTransmExecCarrierTs";
    public static final String ATTR_BIZ_DISPATCHED_TS = "bizDispatchedTs";
    public static final String ATTR_BIZ_TRANSM_TRUCK_TS = "bizTransmTruckTs";
    public static final String ATTR_BIZ_TRANSM_TRUCK_ACK_TS = "bizTransmTruckAckTs";
    public static final String ATTR_BIZ_REACT_TIME_ORD_MNG_MIN = "bizReactTimeOrdMngMin";
    public static final String ATTR_BIZ_REACT_TIME_POOL_MNG_MIN = "bizReactTimePoolMngMin";
    public static final String ATTR_BIZ_REACT_TIME_DISPATCHER_MIN = "bizReactTimeDispatcherMin";
    public static final String ATTR_BIZ_REACT_TIME_TRUCK_DRV_MIN = "bizReactTimeTruckDrvMin";
    public static final String ATTR_BIZ_REACT_TIME_NET_WINDOW_MIN = "bizReactTimeNetWindowMin";
    public static final String ATTR_BIZ_ENTER_SP_TS = "bizEnterSpTs";
    public static final String ATTR_BIZ_START_LOAD_TS = "bizStartLoadTs";
    public static final String ATTR_BIZ_WAITINGTIME_REASON_SP_CD = "bizWaitingtimeReasonSpCd";
    public static final String ATTR_BIZ_END_LOAD_TS = "bizEndLoadTs";
    public static final String ATTR_BIZ_LOADING_TIME_MIN = "bizLoadingTimeMin";
    public static final String ATTR_BIZ_LEAVE_SP_TS = "bizLeaveSpTs";
    public static final String ATTR_BIZ_CYCLE_TIME_AT_SP_MIN = "bizCycleTimeAtSpMin";
    public static final String ATTR_BIZ_TRANS_RAIL_COMPANY_TS = "bizTransRailCompanyTs";
    public static final String ATTR_BIZ_TRAFFICJAM_COUNT = "bizTrafficjamCount";
    public static final String ATTR_BIZ_TRAFFICJAM_DURATION_MIN = "bizTrafficjamDurationMin";
    public static final String ATTR_BIZ_BREAK_DRIVING_COUNT = "bizBreakDrivingCount";
    public static final String ATTR_BIZ_BREAK_DRIVING_DURATION_MIN = "bizBreakDrivingDurationMin";
    public static final String ATTR_BIZ_ENTER_ST_TS = "bizEnterStTs";
    public static final String ATTR_BIZ_CALC_DRIVING_TIME_MIN = "bizCalcDrivingTimeMin";
    public static final String ATTR_BIZ_CALC_NET_DRIVING_TIME_MIN = "bizCalcNetDrivingTimeMin";
    public static final String ATTR_BIZ_ACTUAL_DISTANCE_M = "bizActualDistanceM";
    public static final String ATTR_BIZ_START_UNLOAD_TS = "bizStartUnloadTs";
    public static final String ATTR_BIZ_WAITINGTIME_REASON_ST_CD = "bizWaitingtimeReasonStCd";
    public static final String ATTR_BIZ_END_UNLOAD_TS = "bizEndUnloadTs";
    public static final String ATTR_BIZ_UNLOADING_MIN = "bizUnloadingMin";
    public static final String ATTR_BIZ_LEAVE_ST_TS = "bizLeaveStTs";
    public static final String ATTR_BIZ_CYCLE_TIME_AT_SHIPTO_MIN = "bizCycleTimeAtShiptoMin";
    public static final String ATTR_BIZ_GEOEVENT_COUNT = "bizGeoeventCount";
    public static final String ATTR_O_SHORT_TERM_ORDER_FLAG = "oShortTermOrderFlag";
    public static final String ATTR_O_TRIP_INC_IN_STATISTICS_FLAG = "oTripIncInStatisticsFlag";
    public static final String ATTR_O_RESCHEDULING_FLAG = "oReschedulingFlag";
    public static final String ATTR_O_ORDER_LIFEX_NO = "oLifexNo";
    public static final String ATTR_O_LNF_COORD_LAT = "oLnfCoordLat";
    public static final String ATTR_O_LNF_COORD_LON = "oLnfCoordLon";
    public static final String ATTR_O_LNF_SITE_NAME = "oLnfSiteName";
    public static final String ATTR_O_LNF_SITE_STREET_HOUSE_NO = "oLnfSiteStreetHouseNo";
    public static final String ATTR_O_LNF_SITE_COUNTRY = "oLnfSiteCountry";
    public static final String ATTR_O_LNF_SITE_POSTAL_CODE = "oLnfSitePostalCode";
    public static final String ATTR_O_LNF_SITE_CITY = "oLnfSiteCity";
    public static final String ATTR_O_LNF_SITE_VAT_NO = "oLnfSiteVatNo";
    public static final String ATTR_O_ONE_TIME_SOLDTO_NAME = "oOneTimeSoldtoName";
    public static final String ATTR_O_ONE_TIME_SOLDTO_STREET_HNO = "oOneTimeSoldtoStreetHno";
    public static final String ATTR_O_ONE_TIME_SOLDTO_CITY = "oOneTimeSoldtoCity";
    public static final String ATTR_O_ONE_TIME_SOLDTO_POSTAL_CODE = "oOneTimeSoldtoPostalCode";
    public static final String ATTR_O_ONE_TIME_SOLDTO_COUNTRY = "oOneTimeSoldtoCountry";
    public static final String ATTR_O_ONE_TIME_SOLDTO_VAT_NO = "oOneTimeSoldtoVatNo";
    public static final String ATTR_REPORT_SVC_LEVEL_RELEVANT_FLAG = "reportSvcLevelRelevantFlag";
    public static final String ATTR_REPORT_NOTE_FROM_CRH = "reportNoteFromCrh";
    public static final String ATTR_REPORT_NOTE_FROM_CARRIER = "reportNoteFromCarrier";
    public static final String ATTR_REPORT_FINISHED_COMM_FLAG = "reportFinishedCommFlag";
    public static final String ATTR_O_CTL_CRE_UID = "oCtlCreUid";
    public static final String ATTR_O_CTL_CRE_UID_NAME = "oCtlCreUidName";
    public static final String ATTR_O_CTL_CRE_TS = "oCtlCreTs";
    public static final String ATTR_O_CTL_MOD_UID = "oCtlModUid";
    public static final String ATTR_O_CTL_MOD_UID_NAME = "oCtlModUidName";
    public static final String ATTR_O_CTL_MOD_TS = "oCtlModTs";
    public static final String ATTR_O_BP_CORRECTED_FLAG = "oBpCorrectedFlag";
    public static final String ATTR_EXTRA_COST_WAITING_TIME_MIN = "extraCostWaitingTimeMin";
    public static final String ATTR_EXTRA_COST_1 = "extraCost1";
    public static final String ATTR_EXTRA_COST_2 = "extraCost2";
    public static final String ATTR_EXTRA_COST_3 = "extraCost3";
    public static final String ATTR_EXTRA_COST_4 = "extraCost4";
    public static final String ATTR_O_DELIV_TYPE_PLAN_ACTUAL = "oDelivTypePlanActual";
    public static final String ATTR_O_ARRIVAL_TS = "oArrivalTs";
    public static final String ATTR_CTL_CRE_UID = "ctlCreUid";
    public static final String ATTR_CTL_CRE_TS = "ctlCreTs";
    public static final String ATTR_CTL_MOD_UID = "ctlModUid";
    public static final String ATTR_CTL_MOD_TS = "ctlModTs";
    public static final String ATTR_CTL_TCN = "ctlTcn";
    public static final String ATTR_ACTUAL_DRIVING_TIME_MIN = "actualDrivingTimeMin";

    // Field length on DB
    public static final long DBSIZE_ORDER_FINAL_ID = 12;
    public static final long DBSIZE_O_ORDER_ID = 12;
    public static final long DBSIZE_O_ORDER_NUMBER = 12;
    public static final long DBSIZE_O_ORDER_STATUS_CD = 2;
    public static final long DBSIZE_O_SHIPTO_SAP_BP_ID = 12;
    public static final long DBSIZE_WE_SAP_ID = 10;
    public static final long DBSIZE_WE_MARKED_FOR_DELETION = 1;
    public static final long DBSIZE_WE_NAME1 = 35;
    public static final long DBSIZE_WE_NAME2 = 35;
    public static final long DBSIZE_WE_COUNTRY = 3;
    public static final long DBSIZE_WE_POSTAL_CODE = 10;
    public static final long DBSIZE_WE_CITY = 35;
    public static final long DBSIZE_WE_REGION = 3;
    public static final long DBSIZE_WE_STREET = 35;
    public static final long DBSIZE_WE_HOUSE_NUMBER = 10;
    public static final long DBSIZE_WE_LANG_CD = 2;
    public static final long DBSIZE_WE_CUSTOMER_CLASSIFICATION = 2;
    public static final long DBSIZE_WE_RAILWAY_STATION_ID = 12;
    public static final long DBSIZE_WE_RS_UIC_COUNTRY_CD = 2;
    public static final long DBSIZE_WE_RS_STATION_NO = 6;
    public static final long DBSIZE_WE_RS_STATION_NAME = 50;
    public static final long DBSIZE_WE_RS_COM_UIC_COUNTRY_CD = 2;
    public static final long DBSIZE_WE_RS_COM_STATION_NO = 6;
    public static final long DBSIZE_WE_RS_COM_STATION_NAME = 50;
    public static final long DBSIZE_WE_TRAIN_ST_UNLOADING_POINT = 4;
    public static final long DBSIZE_WE_SBB_CUSTOMER_NUMBER = 15;
    public static final long DBSIZE_WE_BP_IS_SHIPTO = 1;
    public static final long DBSIZE_WE_BP_IS_SOLDTO = 1;
    public static final long DBSIZE_WE_BP_IS_BILLTO = 1;
    public static final long DBSIZE_WE_SAP_BP_SAREA_ID = 12;
    public static final long DBSIZE_WE_SAREA_MARKED_FOR_DELETION = 1;
    public static final long DBSIZE_WE_SALES_DISTRICT = 6;
    public static final long DBSIZE_WE_SOFFICE_CD = 4;
    public static final long DBSIZE_WE_SOFFICE_NAME = 30;
    public static final long DBSIZE_WE_SGROUP_CD = 4;
    public static final long DBSIZE_WE_SGROUP_NAME = 30;
    public static final long DBSIZE_WE_SGROUP_EMAIL_ADDRESSES = 200;
    public static final long DBSIZE_WE_CUSTOMER_GROUP = 2;
    public static final long DBSIZE_WE_ABC_CLASS = 2;
    public static final long DBSIZE_WE_CURRENCY_CD = 5;
    public static final long DBSIZE_WE_DISTRIBUTION_DEST_CD = 4;
    public static final long DBSIZE_WE_CUSTOMER_ORDER_BLOCK = 1;
    public static final long DBSIZE_WE_CUSTOMER_DELIVERY_BLOCK = 1;
    public static final long DBSIZE_WE_CUSTOM_OFFICE = 20;
    public static final long DBSIZE_WE_CHROMAT_REDUCTION_MONTHS = 1;
    public static final long DBSIZE_PRECISION_WE_CHROMAT_REDUCTION_MONTHS = 0;
    public static final long DBSIZE_WE_DEFAULT_PLANT_CD = 4;
    public static final long DBSIZE_WE_SHIPTO_ID = 12;
    public static final long DBSIZE_WE_ACTIVE_FLAG = 1;
    public static final long DBSIZE_WE_SHIPTO_TYPE_CD = 2;
    public static final long DBSIZE_WE_SIGNATURE_TYPE_CD = 2;
    public static final long DBSIZE_WE_COORD_LAT = 22;
    public static final long DBSIZE_WE_COORD_LON = 22;
    public static final long DBSIZE_WE_COORD_FIX_FLAG = 1;
    public static final long DBSIZE_WE_GEOFENCE_RADIUS = 10;
    public static final long DBSIZE_WE_UNLOADING_TIME = 10;
    public static final long DBSIZE_WE_RAIL_CHAIN_SHIPPINGPOINT_ID = 12;
    public static final long DBSIZE_WE_RAIL_CHAIN_SP_PLANT_CD = 4;
    public static final long DBSIZE_WE_RAIL_CHAIN_SP_SAP_SHIPPT_NO = 4;
    public static final long DBSIZE_WE_RAIL_CHAIN_SP_SHIPPT_NAME = 80;
    public static final long DBSIZE_WE_MAT_CHAIN_SHIPPINGPOINT_ID = 12;
    public static final long DBSIZE_WE_MAT_CHAIN_SP_PLANT_CD = 4;
    public static final long DBSIZE_WE_MAT_CHAIN_SP_SAP_SHIPPT_NO = 4;
    public static final long DBSIZE_WE_MAT_CHAIN_SP_SHIPPT_NAME = 80;
    public static final long DBSIZE_WE_RAIL_HOLD_BACK_RE_ORDER = 1;
    public static final long DBSIZE_WE_OPTIMAL_TRUCK_TYPE_CD = 3;
    public static final long DBSIZE_WE_OPTIMAL_WAGON_TYPE_CD = 2;
    public static final long DBSIZE_WE_ST_SAREA_SHIPTO_SAREA_ID = 12;
    public static final long DBSIZE_WE_ST_SAREA_FREE_DELIVERY = 1;
    public static final long DBSIZE_WE_ST_SAREA_RELEASE_SAP_OPT = 1;
    public static final long DBSIZE_WE_ST_DISTRIBUTION_DEST_CD = 4;
    public static final long DBSIZE_O_SOLDTO_SAP_BP_ID = 12;
    public static final long DBSIZE_AG_SAP_ID = 10;
    public static final long DBSIZE_AG_MARKED_FOR_DELETION = 1;
    public static final long DBSIZE_AG_NAME1 = 35;
    public static final long DBSIZE_AG_NAME2 = 35;
    public static final long DBSIZE_AG_COUNTRY = 3;
    public static final long DBSIZE_AG_POSTAL_CODE = 10;
    public static final long DBSIZE_AG_CITY = 35;
    public static final long DBSIZE_AG_REGION = 3;
    public static final long DBSIZE_AG_STREET = 35;
    public static final long DBSIZE_AG_HOUSE_NUMBER = 10;
    public static final long DBSIZE_AG_LANG_CD = 2;
    public static final long DBSIZE_AG_CUSTOMER_CLASSIFICATION = 2;
    public static final long DBSIZE_AG_RAILWAY_STATION_ID = 12;
    public static final long DBSIZE_AG_TRAIN_ST_UNLOADING_POINT = 4;
    public static final long DBSIZE_AG_SBB_CUSTOMER_NUMBER = 15;
    public static final long DBSIZE_AG_BP_IS_SHIPTO = 1;
    public static final long DBSIZE_AG_BP_IS_SOLDTO = 1;
    public static final long DBSIZE_AG_BP_IS_BILLTO = 1;
    public static final long DBSIZE_AG_SAP_BP_SAREA_ID = 12;
    public static final long DBSIZE_AG_SAREA_MARKED_FOR_DELETION = 1;
    public static final long DBSIZE_AG_SALES_DISTRICT = 6;
    public static final long DBSIZE_AG_SOFFICE_CD = 4;
    public static final long DBSIZE_AG_SOFFICE_NAME = 30;
    public static final long DBSIZE_AG_SGROUP_CD = 4;
    public static final long DBSIZE_AG_SGROUP_NAME = 30;
    public static final long DBSIZE_AG_SGROUP_EMAIL_ADDRESSES = 200;
    public static final long DBSIZE_AG_CUSTOMER_GROUP = 2;
    public static final long DBSIZE_AG_ABC_CLASS = 2;
    public static final long DBSIZE_AG_CURRENCY_CD = 5;
    public static final long DBSIZE_AG_DISTRIBUTION_DEST_CD = 4;
    public static final long DBSIZE_AG_CUSTOMER_ORDER_BLOCK = 1;
    public static final long DBSIZE_AG_CUSTOMER_DELIVERY_BLOCK = 1;
    public static final long DBSIZE_AG_CUSTOM_OFFICE = 20;
    public static final long DBSIZE_AG_CHROMAT_REDUCTION_MONTHS = 1;
    public static final long DBSIZE_PRECISION_AG_CHROMAT_REDUCTION_MONTHS = 0;
    public static final long DBSIZE_AG_DEFAULT_PLANT_CD = 4;
    public static final long DBSIZE_O_BILLTO_SAP_BP_ID = 12;
    public static final long DBSIZE_RE_SAP_ID = 10;
    public static final long DBSIZE_RE_MARKED_FOR_DELETION = 1;
    public static final long DBSIZE_RE_NAME1 = 35;
    public static final long DBSIZE_RE_NAME2 = 35;
    public static final long DBSIZE_RE_COUNTRY = 3;
    public static final long DBSIZE_RE_POSTAL_CODE = 10;
    public static final long DBSIZE_RE_CITY = 35;
    public static final long DBSIZE_RE_REGION = 3;
    public static final long DBSIZE_RE_STREET = 35;
    public static final long DBSIZE_RE_HOUSE_NUMBER = 10;
    public static final long DBSIZE_RE_LANG_CD = 2;
    public static final long DBSIZE_RE_CUSTOMER_CLASSIFICATION = 2;
    public static final long DBSIZE_RE_RAILWAY_STATION_ID = 12;
    public static final long DBSIZE_RE_TRAIN_ST_UNLOADING_POINT = 4;
    public static final long DBSIZE_RE_SBB_CUSTOMER_NUMBER = 15;
    public static final long DBSIZE_RE_BP_IS_SHIPTO = 1;
    public static final long DBSIZE_RE_BP_IS_SOLDTO = 1;
    public static final long DBSIZE_RE_BP_IS_BILLTO = 1;
    public static final long DBSIZE_RE_SAP_BP_SAREA_ID = 12;
    public static final long DBSIZE_RE_SAREA_MARKED_FOR_DELETION = 1;
    public static final long DBSIZE_RE_SALES_DISTRICT = 6;
    public static final long DBSIZE_RE_SOFFICE_CD = 4;
    public static final long DBSIZE_RE_SGROUP_CD = 4;
    public static final long DBSIZE_RE_CUSTOMER_GROUP = 2;
    public static final long DBSIZE_RE_ABC_CLASS = 2;
    public static final long DBSIZE_RE_CURRENCY_CD = 5;
    public static final long DBSIZE_RE_DISTRIBUTION_DEST_CD = 4;
    public static final long DBSIZE_RE_CUSTOMER_ORDER_BLOCK = 1;
    public static final long DBSIZE_RE_CUSTOMER_DELIVERY_BLOCK = 1;
    public static final long DBSIZE_RE_CUSTOM_OFFICE = 20;
    public static final long DBSIZE_RE_CHROMAT_REDUCTION_MONTHS = 1;
    public static final long DBSIZE_PRECISION_RE_CHROMAT_REDUCTION_MONTHS = 0;
    public static final long DBSIZE_RE_DEFAULT_PLANT_CD = 4;
    public static final long DBSIZE_O_SAREA_ID = 12;
    public static final long DBSIZE_SAREA_DISPLAY_LONGNAME = 30;
    public static final long DBSIZE_SAREA_SORG_CD = 4;
    public static final long DBSIZE_SAREA_DISTRIBUTION_CHANNEL = 2;
    public static final long DBSIZE_SAREA_DIVISION_CD = 2;
    public static final long DBSIZE_SAREA_DIVISION_SEGMENT_CD = 1;
    public static final long DBSIZE_SAREA_COMPANYCODE_CD = 4;
    public static final long DBSIZE_SAREA_COUNTRY_CD = 3;
    public static final long DBSIZE_O_POOL_ID = 12;
    public static final long DBSIZE_O_COMM_CARRIER_ID = 12;
    public static final long DBSIZE_COMM_CARRIER_DISPLAY_NAME = 40;
    public static final long DBSIZE_COMM_CARRIER_SAP_VENDOR_ID = 12;
    public static final long DBSIZE_COMM_CARRIER_V_SAP_ID = 10;
    public static final long DBSIZE_COMM_CARRIER_V_ACCOUNT_GROUP = 4;
    public static final long DBSIZE_COMM_CARRIER_V_MARKED_FOR_DEL = 1;
    public static final long DBSIZE_COMM_CARRIER_V_NAME1 = 35;
    public static final long DBSIZE_COMM_CARRIER_V_NAME2 = 35;
    public static final long DBSIZE_COMM_CARRIER_V_COUNTRY_CD = 3;
    public static final long DBSIZE_COMM_CARRIER_V_POSTAL_CODE = 10;
    public static final long DBSIZE_COMM_CARRIER_V_CITY = 35;
    public static final long DBSIZE_COMM_CARRIER_V_LOCATION_DIST = 40;
    public static final long DBSIZE_COMM_CARRIER_V_REGION = 3;
    public static final long DBSIZE_COMM_CARRIER_V_STREET = 35;
    public static final long DBSIZE_COMM_CARRIER_V_HOUSE_NUMBER = 10;
    public static final long DBSIZE_COMM_CARRIER_V_LANG_CD = 2;
    public static final long DBSIZE_COMM_CARRIER_V_VAT_REGIST_NO = 20;
    public static final long DBSIZE_COMM_CARRIER_IS_INTERNAL = 1;
    public static final long DBSIZE_O_EXEC_CARRIER_ID = 12;
    public static final long DBSIZE_EXEC_CARRIER_DISPLAY_NAME = 40;
    public static final long DBSIZE_EXEC_CARRIER_SAP_VENDOR_ID = 12;
    public static final long DBSIZE_EXEC_CARRIER_V_SAP_ID = 10;
    public static final long DBSIZE_EXEC_CARRIER_V_ACCOUNT_GROUP = 4;
    public static final long DBSIZE_EXEC_CARRIER_V_MARKED_FOR_DEL = 1;
    public static final long DBSIZE_EXEC_CARRIER_V_NAME1 = 35;
    public static final long DBSIZE_EXEC_CARRIER_V_NAME2 = 35;
    public static final long DBSIZE_EXEC_CARRIER_V_COUNTRY_CD = 3;
    public static final long DBSIZE_EXEC_CARRIER_V_POSTAL_CODE = 10;
    public static final long DBSIZE_EXEC_CARRIER_V_CITY = 35;
    public static final long DBSIZE_EXEC_CARRIER_V_LOCATION_DIST = 40;
    public static final long DBSIZE_EXEC_CARRIER_V_REGION = 3;
    public static final long DBSIZE_EXEC_CARRIER_V_STREET = 35;
    public static final long DBSIZE_EXEC_CARRIER_V_HOUSE_NUMBER = 10;
    public static final long DBSIZE_EXEC_CARRIER_V_LANG_CD = 2;
    public static final long DBSIZE_EXEC_CARRIER_V_VAT_REGIST_NO = 20;
    public static final long DBSIZE_EXEC_CARRIER_IS_INTERNAL = 1;
    public static final long DBSIZE_O_SHIPPINGPOINT_ID = 12;
    public static final long DBSIZE_SP_PLANT_CD = 4;
    public static final long DBSIZE_SP_PLANT_NAME = 80;
    public static final long DBSIZE_SP_SAP_SHIPPINGPOINT_NO = 4;
    public static final long DBSIZE_SP_SHIPPINGPOINT_NAME = 80;
    public static final long DBSIZE_SP_SHIPPPOINT_TYPE_CD = 2;
    public static final long DBSIZE_SP_SAP_SHIPPING_COND_CD = 2;
    public static final long DBSIZE_SP_STORAGE_LOCATION = 4;
    public static final long DBSIZE_SP_RAILWAY_STATION_ID = 12;
    public static final long DBSIZE_SP_RS_UIC_COUNTRY_CD = 2;
    public static final long DBSIZE_SP_RS_RAILWAY_STATION_NO = 6;
    public static final long DBSIZE_SP_RS_RAILWAY_STATION_NAME = 50;
    public static final long DBSIZE_SP_RAILWAY_STATION_LP = 4;
    public static final long DBSIZE_SP_WEIGH_SYSTEM_SP_CD = 2;
    public static final long DBSIZE_SP_LDS_SERVER = 30;
    public static final long DBSIZE_SP_SHIPPINGPOINT_COMMENT = 255;
    public static final long DBSIZE_SP_COORD_LAT = 22;
    public static final long DBSIZE_SP_COORD_LON = 22;
    public static final long DBSIZE_SP_COORD_FIX_FLAG = 1;
    public static final long DBSIZE_SP_GEOFENCE_RADIUS = 10;
    public static final long DBSIZE_SP_FVL_MIX_SHIPTO_PER_WAGON_FL = 1;
    public static final long DBSIZE_SP_SEND_SAP_SHIPPINGPOINT_ID = 12;
    public static final long DBSIZE_SP_SEND_SAP_SP_PLANT_CD = 4;
    public static final long DBSIZE_SP_SEND_SAP_SP_SAP_SHIPPT_NO = 4;
    public static final long DBSIZE_SP_SEND_SAP_SP_SHIPPT_NAME = 80;
    public static final long DBSIZE_O_ACT_DEP_RAILWAY_STATION_ID = 12;
    public static final long DBSIZE_O_ACT_DEP_RS_UIC_COUNTRY_CD = 2;
    public static final long DBSIZE_O_ACT_DEP_RS_STATION_NO = 6;
    public static final long DBSIZE_O_ACT_DEP_RS_STATION_NAME = 50;
    public static final long DBSIZE_O_ACT_DEP_RS_COM_UIC_CNTRY_CD = 2;
    public static final long DBSIZE_O_ACT_DEP_RS_COM_STATION_NO = 6;
    public static final long DBSIZE_O_ACT_DEP_RS_COM_STATION_NAME = 50;
    public static final long DBSIZE_O_ORDER_CREATION_TYPE_CD = 2;
    public static final long DBSIZE_O_DELIVERY_TYPE_CD = 2;
    public static final long DBSIZE_O_DISTRIBUTION_DEST_CD = 4;
    public static final long DBSIZE_O_PLAN_LOADING_TIME_MIN = 10;
    public static final long DBSIZE_O_CALC_LATE_START_LOAD_DAT = 1;
    public static final long DBSIZE_O_CALC_LATE_END_LOAD_DAT = 1;
    public static final long DBSIZE_O_DELIVERY_NET_TIME_WINDOW = 10;
    public static final long DBSIZE_O_CALC_LATE_DELIVERY_FLAG = 1;
    public static final long DBSIZE_O_TRANSP_DUR_PLAN_GOOGLE_MIN = 5;
    public static final long DBSIZE_O_TRANSP_DUR_PLAN_MIN = 5;
    public static final long DBSIZE_O_TRANSP_DISTANCE_PLAN_M = 10;
    public static final long DBSIZE_O_CUSTOMS_NOTIFICATION_NUMBER = 4;
    public static final long DBSIZE_O_DELIVERY_TRSP_ZONE_CD = 10;
    public static final long DBSIZE_O_SPI_CD = 10;
    public static final long DBSIZE_O_CONTACT_PERSON_TXT = 80;
    public static final long DBSIZE_O_CONTACT_PERSON_SIGNATURE_TXT = 40;
    public static final long DBSIZE_O_NO_PDA_OR_RAIL_EVENTS_FLAG = 1;
    public static final long DBSIZE_O_RELEASE_SAP_FLAG = 1;
    public static final long DBSIZE_O_FORCE_TO_SAP_FLAG = 1;
    public static final long DBSIZE_O_FREE_DELIVERY_FLAG = 1;
    public static final long DBSIZE_O_SAP_SUPPRESS_MIN_CHARGE_FLAG = 1;
    public static final long DBSIZE_O_ORDER_CONF_EMAIL_FLAG = 1;
    public static final long DBSIZE_O_ORDER_CONF_EMAIL_ADRESSES = 200;
    public static final long DBSIZE_O_ORDER_CONF_FAX_FLAG = 1;
    public static final long DBSIZE_O_ORDER_CONF_FAX_NUMBER = 80;
    public static final long DBSIZE_O_ORDER_CONF_SMS_FLAG = 1;
    public static final long DBSIZE_O_ORDER_CONF_SMS_NUMBER = 80;
    public static final long DBSIZE_O_DELIVNOTE_EMAIL_FLAG = 1;
    public static final long DBSIZE_O_DELIVNOTE_FAX_FLAG = 1;
    public static final long DBSIZE_O_DELIVNOTE_SINGLE_PAPER_FLAG = 1;
    public static final long DBSIZE_O_CUST_WEBORDER_NOTE = 1000;
    public static final long DBSIZE_O_NOTE_ORDER_MGMT = 256;
    public static final long DBSIZE_O_NOTE_DELIVERY = 300;
    public static final long DBSIZE_O_NOTE_DISPATCHING = 256;
    public static final long DBSIZE_O_NOTE_TRUCK_DRIVER = 256;
    public static final long DBSIZE_O_NOTE_RAILWAY_DISP = 256;
    public static final long DBSIZE_O_CARRIER_DELIVERY_NOTE = 600;
    public static final long DBSIZE_O_CARRIER_TRUCK_DRIVER_NOTE = 256;
    public static final long DBSIZE_O_CARRIER_DISPATCHING_NOTE = 256;
    public static final long DBSIZE_O_CUST_DELIVERY_NOTE_COMMENT = 600;
    public static final long DBSIZE_O_DELIVERED_TRUCK_ID = 12;
    public static final long DBSIZE_TRUCK_NAME = 30;
    public static final long DBSIZE_TRUCK_PLATE_NO = 20;
    public static final long DBSIZE_TRUCK_TRANSPORT_CAT_CD = 2;
    public static final long DBSIZE_TRUCK_TYPE_CD = 3;
    public static final long DBSIZE_TRUCK_MAX_GROSS_WEIGHT_T = 12;
    public static final long DBSIZE_PRECISION_TRUCK_MAX_GROSS_WEIGHT_T = 4;
    public static final long DBSIZE_TRUCK_TARE_WEIGHT_T = 12;
    public static final long DBSIZE_PRECISION_TRUCK_TARE_WEIGHT_T = 4;
    public static final long DBSIZE_TRUCK_NR_CHAMBERS = 1;
    public static final long DBSIZE_TRUCK_MAX_NET_W_CHAMBER1_T = 12;
    public static final long DBSIZE_PRECISION_TRUCK_MAX_NET_W_CHAMBER1_T = 4;
    public static final long DBSIZE_TRUCK_MAX_NET_W_CHAMBER2_T = 12;
    public static final long DBSIZE_PRECISION_TRUCK_MAX_NET_W_CHAMBER2_T = 4;
    public static final long DBSIZE_TRUCK_SAP_INTERNAL_FLAG = 1;
    public static final long DBSIZE_O_DELIVERED_WAGON_ID = 12;
    public static final long DBSIZE_WAGON_NO = 12;
    public static final long DBSIZE_WAGON_TYPE_CD = 2;
    public static final long DBSIZE_WAGON_TYPE_NAME = 20;
    public static final long DBSIZE_WAGON_UIC_CLASS_CD = 10;
    public static final long DBSIZE_WAGON_AXIS_COUNT = 2;
    public static final long DBSIZE_WAGON_TRSP_CAT_CD = 2;
    public static final long DBSIZE_WAGON_LIMIT_CREDIT_NOTE_T = 7;
    public static final long DBSIZE_PRECISION_WAGON_LIMIT_CREDIT_NOTE_T = 3;
    public static final long DBSIZE_WAGON_TARE_WEIGHT_T = 12;
    public static final long DBSIZE_PRECISION_WAGON_TARE_WEIGHT_T = 4;
    public static final long DBSIZE_WAGON_MAX_GROSS_WEIGHT_T = 12;
    public static final long DBSIZE_PRECISION_WAGON_MAX_GROSS_WEIGHT_T = 4;
    public static final long DBSIZE_WAGON_VOLUME_M3 = 12;
    public static final long DBSIZE_PRECISION_WAGON_VOLUME_M3 = 4;
    public static final long DBSIZE_O_DELIVERED_SIMPLE_VEHICLE_ID = 12;
    public static final long DBSIZE_VEHICLE_NAME = 30;
    public static final long DBSIZE_VEHICLE_REG_NO = 20;
    public static final long DBSIZE_VEHICLE_TRANSPORT_CAT_CD = 2;
    public static final long DBSIZE_VEHICLE_TYPE_CD = 2;
    public static final long DBSIZE_VEHICLE_MAX_GROSS_WEIGHT_T = 12;
    public static final long DBSIZE_PRECISION_VEHICLE_MAX_GROSS_WEIGHT_T = 4;
    public static final long DBSIZE_VEHICLE_TARE_WEIGHT_T = 12;
    public static final long DBSIZE_PRECISION_VEHICLE_TARE_WEIGHT_T = 4;
    public static final long DBSIZE_VEHICLE_NR_CHAMBERS = 2;
    public static final long DBSIZE_O_DELIVERED_DRIVER_ID = 12;
    public static final long DBSIZE_DRIVER_NAME = 40;
    public static final long DBSIZE_DRIVER_FIRSTNAME = 40;
    public static final long DBSIZE_DRIVER_USER_TYPE_CD = 2;
    public static final long DBSIZE_DRIVER_SAP_BP_NO = 10;
    public static final long DBSIZE_DRIVER_SAP_VENDOR_NO = 10;
    public static final long DBSIZE_BIZ_REACT_TIME_ORD_MNG_MIN = 12;
    public static final long DBSIZE_BIZ_REACT_TIME_POOL_MNG_MIN = 12;
    public static final long DBSIZE_BIZ_REACT_TIME_DISPATCHER_MIN = 12;
    public static final long DBSIZE_BIZ_REACT_TIME_TRUCK_DRV_MIN = 12;
    public static final long DBSIZE_BIZ_REACT_TIME_NET_WINDOW_MIN = 12;
    public static final long DBSIZE_BIZ_WAITINGTIME_REASON_SP_CD = 2;
    public static final long DBSIZE_BIZ_LOADING_TIME_MIN = 12;
    public static final long DBSIZE_BIZ_CYCLE_TIME_AT_SP_MIN = 12;
    public static final long DBSIZE_BIZ_TRAFFICJAM_COUNT = 22;
    public static final long DBSIZE_BIZ_TRAFFICJAM_DURATION_MIN = 6;
    public static final long DBSIZE_BIZ_BREAK_DRIVING_COUNT = 12;
    public static final long DBSIZE_BIZ_BREAK_DRIVING_DURATION_MIN = 12;
    public static final long DBSIZE_BIZ_CALC_DRIVING_TIME_MIN = 12;
    public static final long DBSIZE_BIZ_CALC_NET_DRIVING_TIME_MIN = 12;
    public static final long DBSIZE_BIZ_ACTUAL_DISTANCE_M = 10;
    public static final long DBSIZE_BIZ_WAITINGTIME_REASON_ST_CD = 2;
    public static final long DBSIZE_BIZ_UNLOADING_MIN = 12;
    public static final long DBSIZE_BIZ_CYCLE_TIME_AT_SHIPTO_MIN = 12;
    public static final long DBSIZE_BIZ_GEOEVENT_COUNT = 22;
    public static final long DBSIZE_O_SHORT_TERM_ORDER_FLAG = 1;
    public static final long DBSIZE_O_TRIP_INC_IN_STATISTICS_FLAG = 1;
    public static final long DBSIZE_O_RESCHEDULING_FLAG = 1;
    public static final long DBSIZE_O_ORDER_LIFEX_NO = 12;
    public static final long DBSIZE_O_LNF_COORD_LAT = 22;
    public static final long DBSIZE_O_LNF_COORD_LON = 22;
    public static final long DBSIZE_O_LNF_SITE_NAME = 80;
    public static final long DBSIZE_O_LNF_SITE_STREET_HOUSE_NO = 45;
    public static final long DBSIZE_O_LNF_SITE_COUNTRY = 3;
    public static final long DBSIZE_O_LNF_SITE_POSTAL_CODE = 10;
    public static final long DBSIZE_O_LNF_SITE_CITY = 35;
    public static final long DBSIZE_O_LNF_SITE_VAT_NO = 20;
    public static final long DBSIZE_O_ONE_TIME_SOLDTO_NAME = 80;
    public static final long DBSIZE_O_ONE_TIME_SOLDTO_STREET_HNO = 45;
    public static final long DBSIZE_O_ONE_TIME_SOLDTO_CITY = 35;
    public static final long DBSIZE_O_ONE_TIME_SOLDTO_POSTAL_CODE = 10;
    public static final long DBSIZE_O_ONE_TIME_SOLDTO_COUNTRY = 3;
    public static final long DBSIZE_O_ONE_TIME_SOLDTO_VAT_NO = 20;
    public static final long DBSIZE_REPORT_SVC_LEVEL_RELEVANT_FLAG = 1;
    public static final long DBSIZE_REPORT_NOTE_FROM_CRH = 256;
    public static final long DBSIZE_REPORT_NOTE_FROM_CARRIER = 256;
    public static final long DBSIZE_REPORT_FINISHED_COMM_FLAG = 1;
    public static final long DBSIZE_O_CTL_CRE_UID = 12;
    public static final long DBSIZE_O_CTL_CRE_UID_NAME = 80;
    public static final long DBSIZE_O_CTL_MOD_UID = 12;
    public static final long DBSIZE_O_CTL_MOD_UID_NAME = 80;
    public static final long DBSIZE_O_BP_CORRECTED_FLAG = 1;
    public static final long DBSIZE_EXTRA_COST_WAITING_TIME_MIN = 4;
    public static final long DBSIZE_EXTRA_COST_1 = 20;
    public static final long DBSIZE_EXTRA_COST_2 = 20;
    public static final long DBSIZE_EXTRA_COST_3 = 20;
    public static final long DBSIZE_EXTRA_COST_4 = 20;
    public static final long DBSIZE_O_DELIV_TYPE_PLAN_ACTUAL = 1;
    public static final long DBZISE_O_ARRIVAL_TS = 6;
    public static final long DBSIZE_CTL_CRE_UID = 12;
    public static final long DBSIZE_CTL_MOD_UID = 12;
    public static final long DBSIZE_CTL_TCN = 12;
    public static final long DBSIZE_ACTUAL_DRIVING_TIME_MIN = 12;

    /**
     * ORDER_FINAL_ID is NOT nullable
     * Field length on DB: 12
     */
    public void setOrderFinalId(Long orderFinalId) {
        this.orderFinalId = orderFinalId;
    }

    /**
     * ORDER_FINAL_ID is NOT nullable
     * Field length on DB: 12
     */
    public Long getOrderFinalId() {
        return orderFinalId;
    }


    /**
     * O_ORDER_ID is NOT nullable
     * Field length on DB: 12
     * tlgnc_order.order_id. Unique. If an order changes after the original 'finish' operation (Examples: Update delivery quantities due to credit note, updated costs, changed Billto, ...) the original order_final record is updated.
     */
    public void setOOrderId(Long oOrderId) {
        this.oOrderId = oOrderId;
    }

    /**
     * O_ORDER_ID is NOT nullable
     * Field length on DB: 12
     * tlgnc_order.order_id. Unique. If an order changes after the original 'finish' operation (Examples: Update delivery quantities due to credit note, updated costs, changed Billto, ...) the original order_final record is updated.
     */
    public Long getOOrderId() {
        return oOrderId;
    }

    /**
     * O_ORDER_NUMBER is nullable
     * Field length on DB: 12
     * tlgnc_order.order_number.
     */
    public void setOOrderNumber(Long oOrderNumber) {
        this.oOrderNumber = oOrderNumber;
    }

    /**
     * O_ORDER_NUMBER is nullable
     * Field length on DB: 12
     * tlgnc_order.order_number.
     */
    public Long getOOrderNumber() {
        return oOrderNumber;
    }

    /**
     * O_ORDER_STATUS_CD is NOT nullable
     * Field length on DB: 2
     * tlgnc_order.order_status_cd.
     */
    public void setOOrderStatusCd(Long oOrderStatusCd) {
        this.oOrderStatusCd = oOrderStatusCd;
    }

    /**
     * O_ORDER_STATUS_CD is NOT nullable
     * Field length on DB: 2
     * tlgnc_order.order_status_cd.
     */
    public Long getOOrderStatusCd() {
        return oOrderStatusCd;
    }

    /**
     * O_SHIPTO_SAP_BP_ID is NOT nullable
     * Field length on DB: 12
     * tlgnc_order.shipto_sap_bp_id. This is the Waren-Empfaenger, all following columns WE_... belong to this.
     */
    public void setOShiptoSapBpId(Long oShiptoSapBpId) {
        this.oShiptoSapBpId = oShiptoSapBpId;
    }

    /**
     * O_SHIPTO_SAP_BP_ID is NOT nullable
     * Field length on DB: 12
     * tlgnc_order.shipto_sap_bp_id. This is the Waren-Empfaenger, all following columns WE_... belong to this.
     */
    public Long getOShiptoSapBpId() {
        return oShiptoSapBpId;
    }

    /**
     * WE_SAP_ID is nullable
     * Field length on DB: 10
     * tlgns_sap_bp.sap_id, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public void setWeSapId(String weSapId) {
        this.weSapId = weSapId;
    }

    /**
     * WE_SAP_ID is nullable
     * Field length on DB: 10
     * tlgns_sap_bp.sap_id, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public String getWeSapId() {
        return weSapId;
    }

    /**
     * WE_MARKED_FOR_DELETION is nullable
     * Field length on DB: 1
     * tlgns_sap_bp.marked_for_deletion, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public void setWeMarkedForDeletion(String weMarkedForDeletion) {
        this.weMarkedForDeletion = weMarkedForDeletion;
    }

    /**
     * WE_MARKED_FOR_DELETION is nullable
     * Field length on DB: 1
     * tlgns_sap_bp.marked_for_deletion, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public String getWeMarkedForDeletion() {
        return weMarkedForDeletion;
    }

    /**
     * WE_NAME1 is nullable
     * Field length on DB: 35
     * tlgns_sap_bp.name1, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public void setWeName1(String weName1) {
        this.weName1 = weName1;
    }

    /**
     * WE_NAME1 is nullable
     * Field length on DB: 35
     * tlgns_sap_bp.name1, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public String getWeName1() {
        return weName1;
    }

    /**
     * WE_NAME2 is nullable
     * Field length on DB: 35
     * tlgns_sap_bp.name2, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public void setWeName2(String weName2) {
        this.weName2 = weName2;
    }

    /**
     * WE_NAME2 is nullable
     * Field length on DB: 35
     * tlgns_sap_bp.name2, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public String getWeName2() {
        return weName2;
    }

    /**
     * WE_COUNTRY is nullable
     * Field length on DB: 3
     * tlgns_sap_bp.country, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public void setWeCountry(String weCountry) {
        this.weCountry = weCountry;
    }

    /**
     * WE_COUNTRY is nullable
     * Field length on DB: 3
     * tlgns_sap_bp.country, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public String getWeCountry() {
        return weCountry;
    }

    /**
     * WE_POSTAL_CODE is nullable
     * Field length on DB: 10
     * tlgns_sap_bp.postal_code, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public void setWePostalCode(String wePostalCode) {
        this.wePostalCode = wePostalCode;
    }

    /**
     * WE_POSTAL_CODE is nullable
     * Field length on DB: 10
     * tlgns_sap_bp.postal_code, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public String getWePostalCode() {
        return wePostalCode;
    }

    /**
     * WE_CITY is nullable
     * Field length on DB: 35
     * tlgns_sap_bp.city, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public void setWeCity(String weCity) {
        this.weCity = weCity;
    }

    /**
     * WE_CITY is nullable
     * Field length on DB: 35
     * tlgns_sap_bp.city, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public String getWeCity() {
        return weCity;
    }

    /**
     * WE_REGION is nullable
     * Field length on DB: 3
     * tlgns_sap_bp.region, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public void setWeRegion(String weRegion) {
        this.weRegion = weRegion;
    }

    /**
     * WE_REGION is nullable
     * Field length on DB: 3
     * tlgns_sap_bp.region, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public String getWeRegion() {
        return weRegion;
    }

    /**
     * WE_STREET is nullable
     * Field length on DB: 35
     * tlgns_sap_bp.street, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public void setWeStreet(String weStreet) {
        this.weStreet = weStreet;
    }

    /**
     * WE_STREET is nullable
     * Field length on DB: 35
     * tlgns_sap_bp.street, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public String getWeStreet() {
        return weStreet;
    }

    /**
     * WE_HOUSE_NUMBER is nullable
     * Field length on DB: 10
     * tlgns_sap_bp.house_number, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public void setWeHouseNumber(String weHouseNumber) {
        this.weHouseNumber = weHouseNumber;
    }

    /**
     * WE_HOUSE_NUMBER is nullable
     * Field length on DB: 10
     * tlgns_sap_bp.house_number, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public String getWeHouseNumber() {
        return weHouseNumber;
    }

    /**
     * WE_LANG_CD is nullable
     * Field length on DB: 2
     * tlgns_sap_bp.lang_cd, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public void setWeLangCd(String weLangCd) {
        this.weLangCd = weLangCd;
    }

    /**
     * WE_LANG_CD is nullable
     * Field length on DB: 2
     * tlgns_sap_bp.lang_cd, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public String getWeLangCd() {
        return weLangCd;
    }

    /**
     * WE_CUSTOMER_CLASSIFICATION is nullable
     * Field length on DB: 2
     * tlgns_sap_bp.customer_classification, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public void setWeCustomerClassification(String weCustomerClassification) {
        this.weCustomerClassification = weCustomerClassification;
    }

    /**
     * WE_CUSTOMER_CLASSIFICATION is nullable
     * Field length on DB: 2
     * tlgns_sap_bp.customer_classification, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public String getWeCustomerClassification() {
        return weCustomerClassification;
    }

    /**
     * WE_RAILWAY_STATION_ID is nullable
     * Field length on DB: 12
     * tlgns_sap_bp.railway_station_id, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public void setWeRailwayStationId(Long weRailwayStationId) {
        this.weRailwayStationId = weRailwayStationId;
    }

    /**
     * WE_RAILWAY_STATION_ID is nullable
     * Field length on DB: 12
     * tlgns_sap_bp.railway_station_id, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public Long getWeRailwayStationId() {
        return weRailwayStationId;
    }

    /**
     * WE_RS_UIC_COUNTRY_CD is nullable
     * Field length on DB: 2
     * tlgnc_railway_station.uic_country_cd referenced by tlgns_sap_bp.railway_station_id->tlgnc_railway_station_comm.op_railway_station_id -> tlgnc_railway_station
     */
    public void setWeRsUicCountryCd(Long weRsUicCountryCd) {
        this.weRsUicCountryCd = weRsUicCountryCd;
    }

    /**
     * WE_RS_UIC_COUNTRY_CD is nullable
     * Field length on DB: 2
     * tlgnc_railway_station.uic_country_cd referenced by tlgns_sap_bp.railway_station_id->tlgnc_railway_station_comm.op_railway_station_id -> tlgnc_railway_station
     */
    public Long getWeRsUicCountryCd() {
        return weRsUicCountryCd;
    }

    /**
     * WE_RS_STATION_NO is nullable
     * Field length on DB: 6
     * tlgnc_railway_station.railway_station_no referenced by tlgns_sap_bp.railway_station_id->tlgnc_railway_station_comm.op_railway_station_id -> tlgnc_railway_station
     */
    public void setWeRsStationNo(String weRsStationNo) {
        this.weRsStationNo = weRsStationNo;
    }

    /**
     * WE_RS_STATION_NO is nullable
     * Field length on DB: 6
     * tlgnc_railway_station.railway_station_no referenced by tlgns_sap_bp.railway_station_id->tlgnc_railway_station_comm.op_railway_station_id -> tlgnc_railway_station
     */
    public String getWeRsStationNo() {
        return weRsStationNo;
    }

    /**
     * WE_RS_STATION_NAME is nullable
     * Field length on DB: 50
     * tlgnc_railway_station.railway_station_name referenced by tlgns_sap_bp.railway_station_id->tlgnc_railway_station_comm.op_railway_station_id -> tlgnc_railway_station
     */
    public void setWeRsStationName(String weRsStationName) {
        this.weRsStationName = weRsStationName;
    }

    /**
     * WE_RS_STATION_NAME is nullable
     * Field length on DB: 50
     * tlgnc_railway_station.railway_station_name referenced by tlgns_sap_bp.railway_station_id->tlgnc_railway_station_comm.op_railway_station_id -> tlgnc_railway_station
     */
    public String getWeRsStationName() {
        return weRsStationName;
    }

    /**
     * WE_RS_COM_UIC_COUNTRY_CD is nullable
     * Field length on DB: 2
     * tlgnc_railway_station.uic_country_cd referenced by tlgns_sap_bp.railway_station_id->tlgnc_railway_station_comm.comm_railway_station_id -> tlgnc_railway_station
     */
    public void setWeRsComUicCountryCd(Long weRsComUicCountryCd) {
        this.weRsComUicCountryCd = weRsComUicCountryCd;
    }

    /**
     * WE_RS_COM_UIC_COUNTRY_CD is nullable
     * Field length on DB: 2
     * tlgnc_railway_station.uic_country_cd referenced by tlgns_sap_bp.railway_station_id->tlgnc_railway_station_comm.comm_railway_station_id -> tlgnc_railway_station
     */
    public Long getWeRsComUicCountryCd() {
        return weRsComUicCountryCd;
    }

    /**
     * WE_RS_COM_STATION_NO is nullable
     * Field length on DB: 6
     * tlgnc_railway_station.railway_station_no referenced by tlgns_sap_bp.railway_station_id->tlgnc_railway_station_comm.comm_railway_station_id -> tlgnc_railway_station
     */
    public void setWeRsComStationNo(String weRsComStationNo) {
        this.weRsComStationNo = weRsComStationNo;
    }

    /**
     * WE_RS_COM_STATION_NO is nullable
     * Field length on DB: 6
     * tlgnc_railway_station.railway_station_no referenced by tlgns_sap_bp.railway_station_id->tlgnc_railway_station_comm.comm_railway_station_id -> tlgnc_railway_station
     */
    public String getWeRsComStationNo() {
        return weRsComStationNo;
    }

    /**
     * WE_RS_COM_STATION_NAME is nullable
     * Field length on DB: 50
     * tlgnc_railway_station.railway_station_name referenced by tlgns_sap_bp.railway_station_id->tlgnc_railway_station_comm.comm_railway_station_id -> tlgnc_railway_station
     */
    public void setWeRsComStationName(String weRsComStationName) {
        this.weRsComStationName = weRsComStationName;
    }

    /**
     * WE_RS_COM_STATION_NAME is nullable
     * Field length on DB: 50
     * tlgnc_railway_station.railway_station_name referenced by tlgns_sap_bp.railway_station_id->tlgnc_railway_station_comm.comm_railway_station_id -> tlgnc_railway_station
     */
    public String getWeRsComStationName() {
        return weRsComStationName;
    }

    /**
     * WE_TRAIN_ST_UNLOADING_POINT is nullable
     * Field length on DB: 4
     * tlgns_sap_bp.train_station_unloading_point, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public void setWeTrainStUnloadingPoint(String weTrainStUnloadingPoint) {
        this.weTrainStUnloadingPoint = weTrainStUnloadingPoint;
    }

    /**
     * WE_TRAIN_ST_UNLOADING_POINT is nullable
     * Field length on DB: 4
     * tlgns_sap_bp.train_station_unloading_point, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public String getWeTrainStUnloadingPoint() {
        return weTrainStUnloadingPoint;
    }

    /**
     * WE_SBB_CUSTOMER_NUMBER is nullable
     * Field length on DB: 15
     * tlgns_sap_bp.sbb_customer_number, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public void setWeSbbCustomerNumber(String weSbbCustomerNumber) {
        this.weSbbCustomerNumber = weSbbCustomerNumber;
    }

    /**
     * WE_SBB_CUSTOMER_NUMBER is nullable
     * Field length on DB: 15
     * tlgns_sap_bp.sbb_customer_number, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public String getWeSbbCustomerNumber() {
        return weSbbCustomerNumber;
    }

    /**
     * WE_BP_IS_SHIPTO is NOT nullable
     * 
     * tlgns_sap_bp.bp_is_shipto, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public void setWeBpIsShipto(Boolean weBpIsShipto) {
        this.weBpIsShipto = weBpIsShipto;
    }

    /**
     * WE_BP_IS_SHIPTO is NOT nullable
     * 
     * tlgns_sap_bp.bp_is_shipto, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public Boolean getWeBpIsShipto() {
        return weBpIsShipto;
    }

    /**
     * WE_BP_IS_SOLDTO is NOT nullable
     * 
     * tlgns_sap_bp.bp_is_soldto, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public void setWeBpIsSoldto(Boolean weBpIsSoldto) {
        this.weBpIsSoldto = weBpIsSoldto;
    }

    /**
     * WE_BP_IS_SOLDTO is NOT nullable
     * 
     * tlgns_sap_bp.bp_is_soldto, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public Boolean getWeBpIsSoldto() {
        return weBpIsSoldto;
    }

    /**
     * WE_BP_IS_BILLTO is NOT nullable
     * 
     * tlgns_sap_bp.bp_is_billto, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public void setWeBpIsBillto(Boolean weBpIsBillto) {
        this.weBpIsBillto = weBpIsBillto;
    }

    /**
     * WE_BP_IS_BILLTO is NOT nullable
     * 
     * tlgns_sap_bp.bp_is_billto, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp
     */
    public Boolean getWeBpIsBillto() {
        return weBpIsBillto;
    }

    /**
     * WE_SAP_BP_SAREA_ID is nullable
     * Field length on DB: 12
     * tlgns_sap_bp_sarea.sap_bp_sarea_id, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setWeSapBpSareaId(Long weSapBpSareaId) {
        this.weSapBpSareaId = weSapBpSareaId;
    }

    /**
     * WE_SAP_BP_SAREA_ID is nullable
     * Field length on DB: 12
     * tlgns_sap_bp_sarea.sap_bp_sarea_id, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public Long getWeSapBpSareaId() {
        return weSapBpSareaId;
    }

    /**
     * WE_SAREA_MARKED_FOR_DELETION is nullable
     * Field length on DB: 1
     * tlgns_sap_bp_sarea.marked_for_deletion, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setWeSareaMarkedForDeletion(String weSareaMarkedForDeletion) {
        this.weSareaMarkedForDeletion = weSareaMarkedForDeletion;
    }

    /**
     * WE_SAREA_MARKED_FOR_DELETION is nullable
     * Field length on DB: 1
     * tlgns_sap_bp_sarea.marked_for_deletion, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getWeSareaMarkedForDeletion() {
        return weSareaMarkedForDeletion;
    }

    /**
     * WE_SALES_DISTRICT is nullable
     * Field length on DB: 6
     * tlgns_sap_bp_sarea.sales_district, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setWeSalesDistrict(String weSalesDistrict) {
        this.weSalesDistrict = weSalesDistrict;
    }

    /**
     * WE_SALES_DISTRICT is nullable
     * Field length on DB: 6
     * tlgns_sap_bp_sarea.sales_district, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getWeSalesDistrict() {
        return weSalesDistrict;
    }

    /**
     * WE_SOFFICE_CD is nullable
     * Field length on DB: 4
     * tlgns_sap_bp_sarea.soffice_cd, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setWeSofficeCd(String weSofficeCd) {
        this.weSofficeCd = weSofficeCd;
    }

    /**
     * WE_SOFFICE_CD is nullable
     * Field length on DB: 4
     * tlgns_sap_bp_sarea.soffice_cd, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getWeSofficeCd() {
        return weSofficeCd;
    }

    /**
     * WE_SOFFICE_NAME is nullable
     * Field length on DB: 30
     * tlgnr_soffice_bs.soffice_name, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea -> tlgnr_soffice_bs
     */
    public void setWeSofficeName(String weSofficeName) {
        this.weSofficeName = weSofficeName;
    }

    /**
     * WE_SOFFICE_NAME is nullable
     * Field length on DB: 30
     * tlgnr_soffice_bs.soffice_name, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea -> tlgnr_soffice_bs
     */
    public String getWeSofficeName() {
        return weSofficeName;
    }

    /**
     * WE_SGROUP_CD is nullable
     * Field length on DB: 4
     * tlgns_sap_bp_sarea.sgroup_cd, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setWeSgroupCd(String weSgroupCd) {
        this.weSgroupCd = weSgroupCd;
    }

    /**
     * WE_SGROUP_CD is nullable
     * Field length on DB: 4
     * tlgns_sap_bp_sarea.sgroup_cd, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getWeSgroupCd() {
        return weSgroupCd;
    }

    /**
     * WE_SGROUP_NAME is nullable
     * Field length on DB: 30
     * tlgnr_sgroup_bs.sgroup_name referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea ->tlgnr_sgroup_bs
     */
    public void setWeSgroupName(String weSgroupName) {
        this.weSgroupName = weSgroupName;
    }

    /**
     * WE_SGROUP_NAME is nullable
     * Field length on DB: 30
     * tlgnr_sgroup_bs.sgroup_name referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea ->tlgnr_sgroup_bs
     */
    public String getWeSgroupName() {
        return weSgroupName;
    }

    /**
     * WE_SGROUP_EMAIL_ADDRESSES is nullable
     * Field length on DB: 200
     * tlgnr_sgroup_bs.email_addresses referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea ->tlgnr_sgroup_bs
     */
    public void setWeSgroupEmailAddresses(String weSgroupEmailAddresses) {
        this.weSgroupEmailAddresses = weSgroupEmailAddresses;
    }

    /**
     * WE_SGROUP_EMAIL_ADDRESSES is nullable
     * Field length on DB: 200
     * tlgnr_sgroup_bs.email_addresses referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea ->tlgnr_sgroup_bs
     */
    public String getWeSgroupEmailAddresses() {
        return weSgroupEmailAddresses;
    }

    /**
     * WE_CUSTOMER_GROUP is nullable
     * Field length on DB: 2
     * tlgns_sap_bp_sarea.customer_group, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setWeCustomerGroup(String weCustomerGroup) {
        this.weCustomerGroup = weCustomerGroup;
    }

    /**
     * WE_CUSTOMER_GROUP is nullable
     * Field length on DB: 2
     * tlgns_sap_bp_sarea.customer_group, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getWeCustomerGroup() {
        return weCustomerGroup;
    }

    /**
     * WE_ABC_CLASS is nullable
     * Field length on DB: 2
     * tlgns_sap_bp_sarea.abc_class, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setWeAbcClass(String weAbcClass) {
        this.weAbcClass = weAbcClass;
    }

    /**
     * WE_ABC_CLASS is nullable
     * Field length on DB: 2
     * tlgns_sap_bp_sarea.abc_class, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getWeAbcClass() {
        return weAbcClass;
    }

    /**
     * WE_CURRENCY_CD is nullable
     * Field length on DB: 5
     * tlgns_sap_bp_sarea.currency_cd, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setWeCurrencyCd(String weCurrencyCd) {
        this.weCurrencyCd = weCurrencyCd;
    }

    /**
     * WE_CURRENCY_CD is nullable
     * Field length on DB: 5
     * tlgns_sap_bp_sarea.currency_cd, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getWeCurrencyCd() {
        return weCurrencyCd;
    }

    /**
     * WE_DISTRIBUTION_DEST_CD is nullable
     * Field length on DB: 4
     * tlgns_sap_bp_sarea.distribution_dest_cd, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setWeDistributionDestCd(String weDistributionDestCd) {
        this.weDistributionDestCd = weDistributionDestCd;
    }

    /**
     * WE_DISTRIBUTION_DEST_CD is nullable
     * Field length on DB: 4
     * tlgns_sap_bp_sarea.distribution_dest_cd, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getWeDistributionDestCd() {
        return weDistributionDestCd;
    }

    /**
     * WE_CUSTOMER_ORDER_BLOCK is nullable
     * 
     * tlgns_sap_bp_sarea.customer_order_block, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setWeCustomerOrderBlock(Boolean weCustomerOrderBlock) {
        this.weCustomerOrderBlock = weCustomerOrderBlock;
    }

    /**
     * WE_CUSTOMER_ORDER_BLOCK is nullable
     * 
     * tlgns_sap_bp_sarea.customer_order_block, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public Boolean getWeCustomerOrderBlock() {
        return weCustomerOrderBlock;
    }

    /**
     * WE_CUSTOMER_DELIVERY_BLOCK is nullable
     * 
     * tlgns_sap_bp_sarea.customer_delivery_block, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setWeCustomerDeliveryBlock(Boolean weCustomerDeliveryBlock) {
        this.weCustomerDeliveryBlock = weCustomerDeliveryBlock;
    }

    /**
     * WE_CUSTOMER_DELIVERY_BLOCK is nullable
     * 
     * tlgns_sap_bp_sarea.customer_delivery_block, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public Boolean getWeCustomerDeliveryBlock() {
        return weCustomerDeliveryBlock;
    }

    /**
     * WE_CUSTOM_OFFICE is nullable
     * Field length on DB: 20
     * tlgns_sap_bp_sarea.custom_office, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setWeCustomOffice(String weCustomOffice) {
        this.weCustomOffice = weCustomOffice;
    }

    /**
     * WE_CUSTOM_OFFICE is nullable
     * Field length on DB: 20
     * tlgns_sap_bp_sarea.custom_office, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getWeCustomOffice() {
        return weCustomOffice;
    }

    /**
     * WE_CHROMAT_REDUCTION_MONTHS is nullable
     * 
     * tlgns_sap_bp_sarea.chromat_reduction_months, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setWeChromatReductionMonths(java.math.BigDecimal weChromatReductionMonths) {
        this.weChromatReductionMonths = weChromatReductionMonths;
    }

    /**
     * WE_CHROMAT_REDUCTION_MONTHS is nullable
     * 
     * tlgns_sap_bp_sarea.chromat_reduction_months, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public java.math.BigDecimal getWeChromatReductionMonths() {
        return weChromatReductionMonths;
    }

    /**
     * WE_DEFAULT_PLANT_CD is nullable
     * Field length on DB: 4
     * tlgns_sap_bp_sarea.default_plant_cd, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setWeDefaultPlantCd(String weDefaultPlantCd) {
        this.weDefaultPlantCd = weDefaultPlantCd;
    }

    /**
     * WE_DEFAULT_PLANT_CD is nullable
     * Field length on DB: 4
     * tlgns_sap_bp_sarea.default_plant_cd, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getWeDefaultPlantCd() {
        return weDefaultPlantCd;
    }

    /**
     * WE_SHIPTO_ID is nullable
     * Field length on DB: 12
     * tlgnc_shipto.shipto_id, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_sap_bp_id.
     */
    public void setWeShiptoId(Long weShiptoId) {
        this.weShiptoId = weShiptoId;
    }

    /**
     * WE_SHIPTO_ID is nullable
     * Field length on DB: 12
     * tlgnc_shipto.shipto_id, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_sap_bp_id.
     */
    public Long getWeShiptoId() {
        return weShiptoId;
    }

    /**
     * WE_ACTIVE_FLAG is nullable
     * 
     * tlgnc_shipto.active_flag, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_sap_bp_id.
     */
    public void setWeActiveFlag(Boolean weActiveFlag) {
        this.weActiveFlag = weActiveFlag;
    }

    /**
     * WE_ACTIVE_FLAG is nullable
     * 
     * tlgnc_shipto.active_flag, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_sap_bp_id.
     */
    public Boolean getWeActiveFlag() {
        return weActiveFlag;
    }

    /**
     * WE_SHIPTO_TYPE_CD is nullable
     * Field length on DB: 2
     * tlgnc_shipto.shipto_type_cd, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_sap_bp_id.
     */
    public void setWeShiptoTypeCd(Long weShiptoTypeCd) {
        this.weShiptoTypeCd = weShiptoTypeCd;
    }

    /**
     * WE_SHIPTO_TYPE_CD is nullable
     * Field length on DB: 2
     * tlgnc_shipto.shipto_type_cd, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_sap_bp_id.
     */
    public Long getWeShiptoTypeCd() {
        return weShiptoTypeCd;
    }

    /**
     * WE_SIGNATURE_TYPE_CD is nullable
     * Field length on DB: 2
     * tlgnc_shipto.signature_type_cd, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_sap_bp_id.
     */
    public void setWeSignatureTypeCd(Long weSignatureTypeCd) {
        this.weSignatureTypeCd = weSignatureTypeCd;
    }

    /**
     * WE_SIGNATURE_TYPE_CD is nullable
     * Field length on DB: 2
     * tlgnc_shipto.signature_type_cd, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_sap_bp_id.
     */
    public Long getWeSignatureTypeCd() {
        return weSignatureTypeCd;
    }

    /**
     * WE_COORD_LAT is nullable
     * 
     * tlgnc_shipto.coord_lat, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_sap_bp_id.
     */
    public void setWeCoordLat(Double weCoordLat) {
        this.weCoordLat = weCoordLat;
    }

    /**
     * WE_COORD_LAT is nullable
     * 
     * tlgnc_shipto.coord_lat, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_sap_bp_id.
     */
    public Double getWeCoordLat() {
        return weCoordLat;
    }

    /**
     * WE_COORD_LON is nullable
     * 
     * tlgnc_shipto.coord_lon, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_sap_bp_id.
     */
    public void setWeCoordLon(Double weCoordLon) {
        this.weCoordLon = weCoordLon;
    }

    /**
     * WE_COORD_LON is nullable
     * 
     * tlgnc_shipto.coord_lon, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_sap_bp_id.
     */
    public Double getWeCoordLon() {
        return weCoordLon;
    }

    /**
     * WE_COORD_FIX_FLAG is nullable
     * 
     * tlgnc_shipto.coord_fix_flag, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_sap_bp_id.
     */
    public void setWeCoordFixFlag(Boolean weCoordFixFlag) {
        this.weCoordFixFlag = weCoordFixFlag;
    }

    /**
     * WE_COORD_FIX_FLAG is nullable
     * 
     * tlgnc_shipto.coord_fix_flag, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_sap_bp_id.
     */
    public Boolean getWeCoordFixFlag() {
        return weCoordFixFlag;
    }

    /**
     * WE_GEOFENCE_RADIUS is nullable
     * Field length on DB: 10
     * tlgnc_shipto.geofence_radius, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_sap_bp_id.
     */
    public void setWeGeofenceRadius(Long weGeofenceRadius) {
        this.weGeofenceRadius = weGeofenceRadius;
    }

    /**
     * WE_GEOFENCE_RADIUS is nullable
     * Field length on DB: 10
     * tlgnc_shipto.geofence_radius, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_sap_bp_id.
     */
    public Long getWeGeofenceRadius() {
        return weGeofenceRadius;
    }

    /**
     * WE_UNLOADING_TIME is nullable
     * Field length on DB: 10
     * tlgnc_shipto.unloading_time, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_sap_bp_id.
     */
    public void setWeUnloadingTime(Long weUnloadingTime) {
        this.weUnloadingTime = weUnloadingTime;
    }

    /**
     * WE_UNLOADING_TIME is nullable
     * Field length on DB: 10
     * tlgnc_shipto.unloading_time, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_sap_bp_id.
     */
    public Long getWeUnloadingTime() {
        return weUnloadingTime;
    }

    /**
     * WE_RAIL_CHAIN_SHIPPINGPOINT_ID is nullable
     * Field length on DB: 12
     * tlgnc_shipto.rail_chain_shippingpoint_id, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_sap_bp_id.
     */
    public void setWeRailChainShippingpointId(Long weRailChainShippingpointId) {
        this.weRailChainShippingpointId = weRailChainShippingpointId;
    }

    /**
     * WE_RAIL_CHAIN_SHIPPINGPOINT_ID is nullable
     * Field length on DB: 12
     * tlgnc_shipto.rail_chain_shippingpoint_id, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_sap_bp_id.
     */
    public Long getWeRailChainShippingpointId() {
        return weRailChainShippingpointId;
    }

    /**
     * WE_RAIL_CHAIN_SP_PLANT_CD is nullable
     * Field length on DB: 4
     * tlgnc_shippingpoint.plant_cd, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.rail_chain_shippingpoint_id -> tlgnc_shippingpoint
     */
    public void setWeRailChainSpPlantCd(String weRailChainSpPlantCd) {
        this.weRailChainSpPlantCd = weRailChainSpPlantCd;
    }

    /**
     * WE_RAIL_CHAIN_SP_PLANT_CD is nullable
     * Field length on DB: 4
     * tlgnc_shippingpoint.plant_cd, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.rail_chain_shippingpoint_id -> tlgnc_shippingpoint
     */
    public String getWeRailChainSpPlantCd() {
        return weRailChainSpPlantCd;
    }

    /**
     * WE_RAIL_CHAIN_SP_SAP_SHIPPT_NO is nullable
     * Field length on DB: 4
     * tlgnc_shippingpoint.sap_shippingpoint_no, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.rail_chain_shippingpoint_id -> tlgnc_shippingpoint
     */
    public void setWeRailChainSpSapShipptNo(String weRailChainSpSapShipptNo) {
        this.weRailChainSpSapShipptNo = weRailChainSpSapShipptNo;
    }

    /**
     * WE_RAIL_CHAIN_SP_SAP_SHIPPT_NO is nullable
     * Field length on DB: 4
     * tlgnc_shippingpoint.sap_shippingpoint_no, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.rail_chain_shippingpoint_id -> tlgnc_shippingpoint
     */
    public String getWeRailChainSpSapShipptNo() {
        return weRailChainSpSapShipptNo;
    }

    /**
     * WE_RAIL_CHAIN_SP_SHIPPT_NAME is nullable
     * Field length on DB: 80
     * tlgnc_shippingpoint.shippingpoint_name, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.rail_chain_shippingpoint_id -> tlgnc_shippingpoint
     */
    public void setWeRailChainSpShipptName(String weRailChainSpShipptName) {
        this.weRailChainSpShipptName = weRailChainSpShipptName;
    }

    /**
     * WE_RAIL_CHAIN_SP_SHIPPT_NAME is nullable
     * Field length on DB: 80
     * tlgnc_shippingpoint.shippingpoint_name, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.rail_chain_shippingpoint_id -> tlgnc_shippingpoint
     */
    public String getWeRailChainSpShipptName() {
        return weRailChainSpShipptName;
    }

    /**
     * WE_MAT_CHAIN_SHIPPINGPOINT_ID is nullable
     * Field length on DB: 12
     * tlgnc_shipto.rail_chain_shippingpoint_id, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_sap_bp_id.
     */
    public void setWeMatChainShippingpointId(Long weMatChainShippingpointId) {
        this.weMatChainShippingpointId = weMatChainShippingpointId;
    }

    /**
     * WE_MAT_CHAIN_SHIPPINGPOINT_ID is nullable
     * Field length on DB: 12
     * tlgnc_shipto.rail_chain_shippingpoint_id, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_sap_bp_id.
     */
    public Long getWeMatChainShippingpointId() {
        return weMatChainShippingpointId;
    }

    /**
     * WE_MAT_CHAIN_SP_PLANT_CD is nullable
     * Field length on DB: 4
     * tlgnc_shippingpoint.plant_cd, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.mat_chain_shippingpoint_id -> tlgnc_shippingpoint
     */
    public void setWeMatChainSpPlantCd(String weMatChainSpPlantCd) {
        this.weMatChainSpPlantCd = weMatChainSpPlantCd;
    }

    /**
     * WE_MAT_CHAIN_SP_PLANT_CD is nullable
     * Field length on DB: 4
     * tlgnc_shippingpoint.plant_cd, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.mat_chain_shippingpoint_id -> tlgnc_shippingpoint
     */
    public String getWeMatChainSpPlantCd() {
        return weMatChainSpPlantCd;
    }

    /**
     * WE_MAT_CHAIN_SP_SAP_SHIPPT_NO is nullable
     * Field length on DB: 4
     * tlgnc_shippingpoint.sap_shippingpoint_no, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.mat_chain_shippingpoint_id -> tlgnc_shippingpoint
     */
    public void setWeMatChainSpSapShipptNo(String weMatChainSpSapShipptNo) {
        this.weMatChainSpSapShipptNo = weMatChainSpSapShipptNo;
    }

    /**
     * WE_MAT_CHAIN_SP_SAP_SHIPPT_NO is nullable
     * Field length on DB: 4
     * tlgnc_shippingpoint.sap_shippingpoint_no, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.mat_chain_shippingpoint_id -> tlgnc_shippingpoint
     */
    public String getWeMatChainSpSapShipptNo() {
        return weMatChainSpSapShipptNo;
    }

    /**
     * WE_MAT_CHAIN_SP_SHIPPT_NAME is nullable
     * Field length on DB: 80
     * tlgnc_shippingpoint.shippingpoint_name, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.mat_chain_shippingpoint_id -> tlgnc_shippingpoint
     */
    public void setWeMatChainSpShipptName(String weMatChainSpShipptName) {
        this.weMatChainSpShipptName = weMatChainSpShipptName;
    }

    /**
     * WE_MAT_CHAIN_SP_SHIPPT_NAME is nullable
     * Field length on DB: 80
     * tlgnc_shippingpoint.shippingpoint_name, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.mat_chain_shippingpoint_id -> tlgnc_shippingpoint
     */
    public String getWeMatChainSpShipptName() {
        return weMatChainSpShipptName;
    }

    /**
     * WE_RAIL_HOLD_BACK_RE_ORDER is nullable
     * 
     * tlgnc_shipto.rail_hold_back_re_order_flag, referenced by tlgnc_order.shipto_sap_bp_id -> tlgnc_shipto.shipto_sap_bp_id.
     */
    public void setWeRailHoldBackReOrder(Boolean weRailHoldBackReOrder) {
        this.weRailHoldBackReOrder = weRailHoldBackReOrder;
    }

    /**
     * WE_RAIL_HOLD_BACK_RE_ORDER is nullable
     * 
     * tlgnc_shipto.rail_hold_back_re_order_flag, referenced by tlgnc_order.shipto_sap_bp_id -> tlgnc_shipto.shipto_sap_bp_id.
     */
    public Boolean getWeRailHoldBackReOrder() {
        return weRailHoldBackReOrder;
    }

    /**
     * WE_OPTIMAL_TRUCK_TYPE_CD is nullable
     * Field length on DB: 3
     * tlgnc_shipto_truck_type.truck_type_cd, referenced by tlgnc_shipto_truck_type.shipto_id -> tlgnc_shipto.
     */
    public void setWeOptimalTruckTypeCd(Long weOptimalTruckTypeCd) {
        this.weOptimalTruckTypeCd = weOptimalTruckTypeCd;
    }

    /**
     * WE_OPTIMAL_TRUCK_TYPE_CD is nullable
     * Field length on DB: 3
     * tlgnc_shipto_truck_type.truck_type_cd, referenced by tlgnc_shipto_truck_type.shipto_id -> tlgnc_shipto.
     */
    public Long getWeOptimalTruckTypeCd() {
        return weOptimalTruckTypeCd;
    }

    /**
     * WE_OPTIMAL_WAGON_TYPE_CD is nullable
     * Field length on DB: 2
     * tlgnc_shipto_wagon_type.wagon_type_cd, referenced by tlgnc_shipto_wagon_type.shipto_id -> tlgnc_shipto.
     */
    public void setWeOptimalWagonTypeCd(Long weOptimalWagonTypeCd) {
        this.weOptimalWagonTypeCd = weOptimalWagonTypeCd;
    }

    /**
     * WE_OPTIMAL_WAGON_TYPE_CD is nullable
     * Field length on DB: 2
     * tlgnc_shipto_wagon_type.wagon_type_cd, referenced by tlgnc_shipto_wagon_type.shipto_id -> tlgnc_shipto.
     */
    public Long getWeOptimalWagonTypeCd() {
        return weOptimalWagonTypeCd;
    }

    /**
     * WE_ST_SAREA_SHIPTO_SAREA_ID is nullable
     * Field length on DB: 12
     * tlgnc_shipto_sarea.shipto_sarea_id, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_id -> tlgnc_shipto_sarea.shipto_id and tlgnc_order.sarea_id -> tlgnc_shipto_sarea
     */
    public void setWeStSareaShiptoSareaId(Long weStSareaShiptoSareaId) {
        this.weStSareaShiptoSareaId = weStSareaShiptoSareaId;
    }

    /**
     * WE_ST_SAREA_SHIPTO_SAREA_ID is nullable
     * Field length on DB: 12
     * tlgnc_shipto_sarea.shipto_sarea_id, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_id -> tlgnc_shipto_sarea.shipto_id and tlgnc_order.sarea_id -> tlgnc_shipto_sarea
     */
    public Long getWeStSareaShiptoSareaId() {
        return weStSareaShiptoSareaId;
    }

    /**
     * WE_ST_SAREA_FREE_DELIVERY is nullable
     * 
     * tlgnc_shipto_sarea.free_delivery_flag, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_id -> tlgnc_shipto_sarea.shipto_id and tlgnc_order.sarea_id -> tlgnc_shipto_sarea
     */
    public void setWeStSareaFreeDelivery(Boolean weStSareaFreeDelivery) {
        this.weStSareaFreeDelivery = weStSareaFreeDelivery;
    }

    /**
     * WE_ST_SAREA_FREE_DELIVERY is nullable
     * 
     * tlgnc_shipto_sarea.free_delivery_flag, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_id -> tlgnc_shipto_sarea.shipto_id and tlgnc_order.sarea_id -> tlgnc_shipto_sarea
     */
    public Boolean getWeStSareaFreeDelivery() {
        return weStSareaFreeDelivery;
    }

    /**
     * WE_ST_SAREA_RELEASE_SAP_OPT is nullable
     * 
     * tlgnc_shipto_sarea.release_sap_oprtional_flag, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_id -> tlgnc_shipto_sarea.shipto_id and tlgnc_order.sarea_id -> tlgnc_shipto_sarea
     */
    public void setWeStSareaReleaseSapOpt(Boolean weStSareaReleaseSapOpt) {
        this.weStSareaReleaseSapOpt = weStSareaReleaseSapOpt;
    }

    /**
     * WE_ST_SAREA_RELEASE_SAP_OPT is nullable
     * 
     * tlgnc_shipto_sarea.release_sap_oprtional_flag, referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_id -> tlgnc_shipto_sarea.shipto_id and tlgnc_order.sarea_id -> tlgnc_shipto_sarea
     */
    public Boolean getWeStSareaReleaseSapOpt() {
        return weStSareaReleaseSapOpt;
    }

    /**
     * WE_ST_DISTRIBUTION_DEST_CD is nullable
     * Field length on DB: 4
     * tlgnc_shipto_sarea.distribution_dest_cd,  referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_id -> tlgnc_shipto_sarea.shipto_id and tlgnc_order.sarea_id -> tlgnc_shipto_sarea
     */
    public void setWeStDistributionDestCd(String weStDistributionDestCd) {
        this.weStDistributionDestCd = weStDistributionDestCd;
    }

    /**
     * WE_ST_DISTRIBUTION_DEST_CD is nullable
     * Field length on DB: 4
     * tlgnc_shipto_sarea.distribution_dest_cd,  referenced by tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_id -> tlgnc_shipto_sarea.shipto_id and tlgnc_order.sarea_id -> tlgnc_shipto_sarea
     */
    public String getWeStDistributionDestCd() {
        return weStDistributionDestCd;
    }

    /**
     * O_SOLDTO_SAP_BP_ID is NOT nullable
     * Field length on DB: 12
     * tlgnc_order.soldto_sap_bp_id. This is the Auftrag-Geber, all following columns AG_... belong to this.
     */
    public void setOSoldtoSapBpId(Long oSoldtoSapBpId) {
        this.oSoldtoSapBpId = oSoldtoSapBpId;
    }

    /**
     * O_SOLDTO_SAP_BP_ID is NOT nullable
     * Field length on DB: 12
     * tlgnc_order.soldto_sap_bp_id. This is the Auftrag-Geber, all following columns AG_... belong to this.
     */
    public Long getOSoldtoSapBpId() {
        return oSoldtoSapBpId;
    }

    /**
     * AG_SAP_ID is nullable
     * Field length on DB: 10
     * tlgns_sap_bp.sap_id, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public void setAgSapId(String agSapId) {
        this.agSapId = agSapId;
    }

    /**
     * AG_SAP_ID is nullable
     * Field length on DB: 10
     * tlgns_sap_bp.sap_id, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public String getAgSapId() {
        return agSapId;
    }

    /**
     * AG_MARKED_FOR_DELETION is nullable
     * Field length on DB: 1
     * tlgns_sap_bp.marked_for_deletion, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public void setAgMarkedForDeletion(String agMarkedForDeletion) {
        this.agMarkedForDeletion = agMarkedForDeletion;
    }

    /**
     * AG_MARKED_FOR_DELETION is nullable
     * Field length on DB: 1
     * tlgns_sap_bp.marked_for_deletion, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public String getAgMarkedForDeletion() {
        return agMarkedForDeletion;
    }

    /**
     * AG_NAME1 is nullable
     * Field length on DB: 35
     * tlgns_sap_bp.name1, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public void setAgName1(String agName1) {
        this.agName1 = agName1;
    }

    /**
     * AG_NAME1 is nullable
     * Field length on DB: 35
     * tlgns_sap_bp.name1, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public String getAgName1() {
        return agName1;
    }

    /**
     * AG_NAME2 is nullable
     * Field length on DB: 35
     * tlgns_sap_bp.name2, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public void setAgName2(String agName2) {
        this.agName2 = agName2;
    }

    /**
     * AG_NAME2 is nullable
     * Field length on DB: 35
     * tlgns_sap_bp.name2, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public String getAgName2() {
        return agName2;
    }

    /**
     * AG_COUNTRY is nullable
     * Field length on DB: 3
     * tlgns_sap_bp.country, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public void setAgCountry(String agCountry) {
        this.agCountry = agCountry;
    }

    /**
     * AG_COUNTRY is nullable
     * Field length on DB: 3
     * tlgns_sap_bp.country, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public String getAgCountry() {
        return agCountry;
    }

    /**
     * AG_POSTAL_CODE is nullable
     * Field length on DB: 10
     * tlgns_sap_bp.postal_code, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public void setAgPostalCode(String agPostalCode) {
        this.agPostalCode = agPostalCode;
    }

    /**
     * AG_POSTAL_CODE is nullable
     * Field length on DB: 10
     * tlgns_sap_bp.postal_code, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public String getAgPostalCode() {
        return agPostalCode;
    }

    /**
     * AG_CITY is nullable
     * Field length on DB: 35
     * tlgns_sap_bp.city, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public void setAgCity(String agCity) {
        this.agCity = agCity;
    }

    /**
     * AG_CITY is nullable
     * Field length on DB: 35
     * tlgns_sap_bp.city, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public String getAgCity() {
        return agCity;
    }

    /**
     * AG_REGION is nullable
     * Field length on DB: 3
     * tlgns_sap_bp.region, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public void setAgRegion(String agRegion) {
        this.agRegion = agRegion;
    }

    /**
     * AG_REGION is nullable
     * Field length on DB: 3
     * tlgns_sap_bp.region, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public String getAgRegion() {
        return agRegion;
    }

    /**
     * AG_STREET is nullable
     * Field length on DB: 35
     * tlgns_sap_bp.street, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public void setAgStreet(String agStreet) {
        this.agStreet = agStreet;
    }

    /**
     * AG_STREET is nullable
     * Field length on DB: 35
     * tlgns_sap_bp.street, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public String getAgStreet() {
        return agStreet;
    }

    /**
     * AG_HOUSE_NUMBER is nullable
     * Field length on DB: 10
     * tlgns_sap_bp.house_number, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public void setAgHouseNumber(String agHouseNumber) {
        this.agHouseNumber = agHouseNumber;
    }

    /**
     * AG_HOUSE_NUMBER is nullable
     * Field length on DB: 10
     * tlgns_sap_bp.house_number, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public String getAgHouseNumber() {
        return agHouseNumber;
    }

    /**
     * AG_LANG_CD is nullable
     * Field length on DB: 2
     * tlgns_sap_bp.lang_cd, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public void setAgLangCd(String agLangCd) {
        this.agLangCd = agLangCd;
    }

    /**
     * AG_LANG_CD is nullable
     * Field length on DB: 2
     * tlgns_sap_bp.lang_cd, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public String getAgLangCd() {
        return agLangCd;
    }

    /**
     * AG_CUSTOMER_CLASSIFICATION is nullable
     * Field length on DB: 2
     * tlgns_sap_bp.customer_classification, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public void setAgCustomerClassification(String agCustomerClassification) {
        this.agCustomerClassification = agCustomerClassification;
    }

    /**
     * AG_CUSTOMER_CLASSIFICATION is nullable
     * Field length on DB: 2
     * tlgns_sap_bp.customer_classification, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public String getAgCustomerClassification() {
        return agCustomerClassification;
    }

    /**
     * AG_RAILWAY_STATION_ID is nullable
     * Field length on DB: 12
     * tlgns_sap_bp.railway_station_id, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public void setAgRailwayStationId(Long agRailwayStationId) {
        this.agRailwayStationId = agRailwayStationId;
    }

    /**
     * AG_RAILWAY_STATION_ID is nullable
     * Field length on DB: 12
     * tlgns_sap_bp.railway_station_id, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public Long getAgRailwayStationId() {
        return agRailwayStationId;
    }

    /**
     * AG_TRAIN_ST_UNLOADING_POINT is nullable
     * Field length on DB: 4
     * tlgns_sap_bp.train_station_unloading_point, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public void setAgTrainStUnloadingPoint(String agTrainStUnloadingPoint) {
        this.agTrainStUnloadingPoint = agTrainStUnloadingPoint;
    }

    /**
     * AG_TRAIN_ST_UNLOADING_POINT is nullable
     * Field length on DB: 4
     * tlgns_sap_bp.train_station_unloading_point, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public String getAgTrainStUnloadingPoint() {
        return agTrainStUnloadingPoint;
    }

    /**
     * AG_SBB_CUSTOMER_NUMBER is nullable
     * Field length on DB: 15
     * tlgns_sap_bp.sbb_customer_number, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public void setAgSbbCustomerNumber(String agSbbCustomerNumber) {
        this.agSbbCustomerNumber = agSbbCustomerNumber;
    }

    /**
     * AG_SBB_CUSTOMER_NUMBER is nullable
     * Field length on DB: 15
     * tlgns_sap_bp.sbb_customer_number, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public String getAgSbbCustomerNumber() {
        return agSbbCustomerNumber;
    }

    /**
     * AG_BP_IS_SHIPTO is NOT nullable
     * 
     * tlgns_sap_bp.bp_is_shipto, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public void setAgBpIsShipto(Boolean agBpIsShipto) {
        this.agBpIsShipto = agBpIsShipto;
    }

    /**
     * AG_BP_IS_SHIPTO is NOT nullable
     * 
     * tlgns_sap_bp.bp_is_shipto, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public Boolean getAgBpIsShipto() {
        return agBpIsShipto;
    }

    /**
     * AG_BP_IS_SOLDTO is NOT nullable
     * 
     * tlgns_sap_bp.bp_is_soldto, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public void setAgBpIsSoldto(Boolean agBpIsSoldto) {
        this.agBpIsSoldto = agBpIsSoldto;
    }

    /**
     * AG_BP_IS_SOLDTO is NOT nullable
     * 
     * tlgns_sap_bp.bp_is_soldto, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public Boolean getAgBpIsSoldto() {
        return agBpIsSoldto;
    }

    /**
     * AG_BP_IS_BILLTO is NOT nullable
     * 
     * tlgns_sap_bp.bp_is_billto, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public void setAgBpIsBillto(Boolean agBpIsBillto) {
        this.agBpIsBillto = agBpIsBillto;
    }

    /**
     * AG_BP_IS_BILLTO is NOT nullable
     * 
     * tlgns_sap_bp.bp_is_billto, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp
     */
    public Boolean getAgBpIsBillto() {
        return agBpIsBillto;
    }

    /**
     * AG_SAP_BP_SAREA_ID is nullable
     * Field length on DB: 12
     * tlgns_sap_bp_sarea.sap_bp_sarea_id, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setAgSapBpSareaId(Long agSapBpSareaId) {
        this.agSapBpSareaId = agSapBpSareaId;
    }

    /**
     * AG_SAP_BP_SAREA_ID is nullable
     * Field length on DB: 12
     * tlgns_sap_bp_sarea.sap_bp_sarea_id, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public Long getAgSapBpSareaId() {
        return agSapBpSareaId;
    }

    /**
     * AG_SAREA_MARKED_FOR_DELETION is nullable
     * Field length on DB: 1
     * tlgns_sap_bp_sarea.marked_for_deletion, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setAgSareaMarkedForDeletion(String agSareaMarkedForDeletion) {
        this.agSareaMarkedForDeletion = agSareaMarkedForDeletion;
    }

    /**
     * AG_SAREA_MARKED_FOR_DELETION is nullable
     * Field length on DB: 1
     * tlgns_sap_bp_sarea.marked_for_deletion, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getAgSareaMarkedForDeletion() {
        return agSareaMarkedForDeletion;
    }

    /**
     * AG_SALES_DISTRICT is nullable
     * Field length on DB: 6
     * tlgns_sap_bp_sarea.sales_district, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setAgSalesDistrict(String agSalesDistrict) {
        this.agSalesDistrict = agSalesDistrict;
    }

    /**
     * AG_SALES_DISTRICT is nullable
     * Field length on DB: 6
     * tlgns_sap_bp_sarea.sales_district, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getAgSalesDistrict() {
        return agSalesDistrict;
    }

    /**
     * AG_SOFFICE_CD is nullable
     * Field length on DB: 4
     * tlgns_sap_bp_sarea.soffice_cd, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setAgSofficeCd(String agSofficeCd) {
        this.agSofficeCd = agSofficeCd;
    }

    /**
     * AG_SOFFICE_CD is nullable
     * Field length on DB: 4
     * tlgns_sap_bp_sarea.soffice_cd, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getAgSofficeCd() {
        return agSofficeCd;
    }

    /**
     * AG_SOFFICE_NAME is nullable
     * Field length on DB: 30
     * tlgnr_soffice_bs.soffice_name, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea -> tlgnr_soffice_bs
     */
    public void setAgSofficeName(String agSofficeName) {
        this.agSofficeName = agSofficeName;
    }

    /**
     * AG_SOFFICE_NAME is nullable
     * Field length on DB: 30
     * tlgnr_soffice_bs.soffice_name, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea -> tlgnr_soffice_bs
     */
    public String getAgSofficeName() {
        return agSofficeName;
    }

    /**
     * AG_SGROUP_CD is nullable
     * Field length on DB: 4
     * tlgns_sap_bp_sarea.sgroup_cd, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setAgSgroupCd(String agSgroupCd) {
        this.agSgroupCd = agSgroupCd;
    }

    /**
     * AG_SGROUP_CD is nullable
     * Field length on DB: 4
     * tlgns_sap_bp_sarea.sgroup_cd, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getAgSgroupCd() {
        return agSgroupCd;
    }

    /**
     * AG_SGROUP_NAME is nullable
     * Field length on DB: 30
     * tlgnr_sgroup_bs.sgroup_name referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea ->tlgnr_sgroup_bs
     */
    public void setAgSgroupName(String agSgroupName) {
        this.agSgroupName = agSgroupName;
    }

    /**
     * AG_SGROUP_NAME is nullable
     * Field length on DB: 30
     * tlgnr_sgroup_bs.sgroup_name referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea ->tlgnr_sgroup_bs
     */
    public String getAgSgroupName() {
        return agSgroupName;
    }

    /**
     * AG_SGROUP_EMAIL_ADDRESSES is nullable
     * Field length on DB: 200
     * tlgnr_sgroup_bs.email_addresses referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea ->tlgnr_sgroup_bs
     */
    public void setAgSgroupEmailAddresses(String agSgroupEmailAddresses) {
        this.agSgroupEmailAddresses = agSgroupEmailAddresses;
    }

    /**
     * AG_SGROUP_EMAIL_ADDRESSES is nullable
     * Field length on DB: 200
     * tlgnr_sgroup_bs.email_addresses referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea ->tlgnr_sgroup_bs
     */
    public String getAgSgroupEmailAddresses() {
        return agSgroupEmailAddresses;
    }

    /**
     * AG_CUSTOMER_GROUP is nullable
     * Field length on DB: 2
     * tlgns_sap_bp_sarea.customer_group, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setAgCustomerGroup(String agCustomerGroup) {
        this.agCustomerGroup = agCustomerGroup;
    }

    /**
     * AG_CUSTOMER_GROUP is nullable
     * Field length on DB: 2
     * tlgns_sap_bp_sarea.customer_group, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getAgCustomerGroup() {
        return agCustomerGroup;
    }

    /**
     * AG_ABC_CLASS is nullable
     * Field length on DB: 2
     * tlgns_sap_bp_sarea.abc_class, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setAgAbcClass(String agAbcClass) {
        this.agAbcClass = agAbcClass;
    }

    /**
     * AG_ABC_CLASS is nullable
     * Field length on DB: 2
     * tlgns_sap_bp_sarea.abc_class, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getAgAbcClass() {
        return agAbcClass;
    }

    /**
     * AG_CURRENCY_CD is nullable
     * Field length on DB: 5
     * tlgns_sap_bp_sarea.currency_cd, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setAgCurrencyCd(String agCurrencyCd) {
        this.agCurrencyCd = agCurrencyCd;
    }

    /**
     * AG_CURRENCY_CD is nullable
     * Field length on DB: 5
     * tlgns_sap_bp_sarea.currency_cd, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getAgCurrencyCd() {
        return agCurrencyCd;
    }

    /**
     * AG_DISTRIBUTION_DEST_CD is nullable
     * Field length on DB: 4
     * tlgns_sap_bp_sarea.distribution_dest_cd, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setAgDistributionDestCd(String agDistributionDestCd) {
        this.agDistributionDestCd = agDistributionDestCd;
    }

    /**
     * AG_DISTRIBUTION_DEST_CD is nullable
     * Field length on DB: 4
     * tlgns_sap_bp_sarea.distribution_dest_cd, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getAgDistributionDestCd() {
        return agDistributionDestCd;
    }

    /**
     * AG_CUSTOMER_ORDER_BLOCK is nullable
     * 
     * tlgns_sap_bp_sarea.customer_order_block, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setAgCustomerOrderBlock(Boolean agCustomerOrderBlock) {
        this.agCustomerOrderBlock = agCustomerOrderBlock;
    }

    /**
     * AG_CUSTOMER_ORDER_BLOCK is nullable
     * 
     * tlgns_sap_bp_sarea.customer_order_block, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public Boolean getAgCustomerOrderBlock() {
        return agCustomerOrderBlock;
    }

    /**
     * AG_CUSTOMER_DELIVERY_BLOCK is nullable
     * 
     * tlgns_sap_bp_sarea.customer_delivery_block, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setAgCustomerDeliveryBlock(Boolean agCustomerDeliveryBlock) {
        this.agCustomerDeliveryBlock = agCustomerDeliveryBlock;
    }

    /**
     * AG_CUSTOMER_DELIVERY_BLOCK is nullable
     * 
     * tlgns_sap_bp_sarea.customer_delivery_block, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public Boolean getAgCustomerDeliveryBlock() {
        return agCustomerDeliveryBlock;
    }

    /**
     * AG_CUSTOM_OFFICE is nullable
     * Field length on DB: 20
     * tlgns_sap_bp_sarea.custom_office, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setAgCustomOffice(String agCustomOffice) {
        this.agCustomOffice = agCustomOffice;
    }

    /**
     * AG_CUSTOM_OFFICE is nullable
     * Field length on DB: 20
     * tlgns_sap_bp_sarea.custom_office, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getAgCustomOffice() {
        return agCustomOffice;
    }

    /**
     * AG_CHROMAT_REDUCTION_MONTHS is nullable
     * 
     * tlgns_sap_bp_sarea.chromat_reduction_months, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setAgChromatReductionMonths(java.math.BigDecimal agChromatReductionMonths) {
        this.agChromatReductionMonths = agChromatReductionMonths;
    }

    /**
     * AG_CHROMAT_REDUCTION_MONTHS is nullable
     * 
     * tlgns_sap_bp_sarea.chromat_reduction_months, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public java.math.BigDecimal getAgChromatReductionMonths() {
        return agChromatReductionMonths;
    }

    /**
     * AG_DEFAULT_PLANT_CD is nullable
     * Field length on DB: 4
     * tlgns_sap_bp_sarea.default_plant_cd, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setAgDefaultPlantCd(String agDefaultPlantCd) {
        this.agDefaultPlantCd = agDefaultPlantCd;
    }

    /**
     * AG_DEFAULT_PLANT_CD is nullable
     * Field length on DB: 4
     * tlgns_sap_bp_sarea.default_plant_cd, referenced by tlgnc_order.soldto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getAgDefaultPlantCd() {
        return agDefaultPlantCd;
    }

    /**
     * O_BILLTO_SAP_BP_ID is nullable
     * Field length on DB: 12
     * tlgnc_order.billto_sap_bp_id. This is the Rechnungs-Empfaenger, all following columns RE_... belong to this.
     */
    public void setOBilltoSapBpId(Long oBilltoSapBpId) {
        this.oBilltoSapBpId = oBilltoSapBpId;
    }

    /**
     * O_BILLTO_SAP_BP_ID is nullable
     * Field length on DB: 12
     * tlgnc_order.billto_sap_bp_id. This is the Rechnungs-Empfaenger, all following columns RE_... belong to this.
     */
    public Long getOBilltoSapBpId() {
        return oBilltoSapBpId;
    }

    /**
     * RE_SAP_ID is nullable
     * Field length on DB: 10
     * tlgns_sap_bp.sap_id, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public void setReSapId(String reSapId) {
        this.reSapId = reSapId;
    }

    /**
     * RE_SAP_ID is nullable
     * Field length on DB: 10
     * tlgns_sap_bp.sap_id, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public String getReSapId() {
        return reSapId;
    }

    /**
     * RE_MARKED_FOR_DELETION is nullable
     * Field length on DB: 1
     * tlgns_sap_bp.marked_for_deletion, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public void setReMarkedForDeletion(String reMarkedForDeletion) {
        this.reMarkedForDeletion = reMarkedForDeletion;
    }

    /**
     * RE_MARKED_FOR_DELETION is nullable
     * Field length on DB: 1
     * tlgns_sap_bp.marked_for_deletion, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public String getReMarkedForDeletion() {
        return reMarkedForDeletion;
    }

    /**
     * RE_NAME1 is nullable
     * Field length on DB: 35
     * tlgns_sap_bp.name1, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public void setReName1(String reName1) {
        this.reName1 = reName1;
    }

    /**
     * RE_NAME1 is nullable
     * Field length on DB: 35
     * tlgns_sap_bp.name1, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public String getReName1() {
        return reName1;
    }

    /**
     * RE_NAME2 is nullable
     * Field length on DB: 35
     * tlgns_sap_bp.name2, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public void setReName2(String reName2) {
        this.reName2 = reName2;
    }

    /**
     * RE_NAME2 is nullable
     * Field length on DB: 35
     * tlgns_sap_bp.name2, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public String getReName2() {
        return reName2;
    }

    /**
     * RE_COUNTRY is nullable
     * Field length on DB: 3
     * tlgns_sap_bp.country, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public void setReCountry(String reCountry) {
        this.reCountry = reCountry;
    }

    /**
     * RE_COUNTRY is nullable
     * Field length on DB: 3
     * tlgns_sap_bp.country, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public String getReCountry() {
        return reCountry;
    }

    /**
     * RE_POSTAL_CODE is nullable
     * Field length on DB: 10
     * tlgns_sap_bp.postal_code, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public void setRePostalCode(String rePostalCode) {
        this.rePostalCode = rePostalCode;
    }

    /**
     * RE_POSTAL_CODE is nullable
     * Field length on DB: 10
     * tlgns_sap_bp.postal_code, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public String getRePostalCode() {
        return rePostalCode;
    }

    /**
     * RE_CITY is nullable
     * Field length on DB: 35
     * tlgns_sap_bp.city, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public void setReCity(String reCity) {
        this.reCity = reCity;
    }

    /**
     * RE_CITY is nullable
     * Field length on DB: 35
     * tlgns_sap_bp.city, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public String getReCity() {
        return reCity;
    }

    /**
     * RE_REGION is nullable
     * Field length on DB: 3
     * tlgns_sap_bp.region, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public void setReRegion(String reRegion) {
        this.reRegion = reRegion;
    }

    /**
     * RE_REGION is nullable
     * Field length on DB: 3
     * tlgns_sap_bp.region, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public String getReRegion() {
        return reRegion;
    }

    /**
     * RE_STREET is nullable
     * Field length on DB: 35
     * tlgns_sap_bp.street, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public void setReStreet(String reStreet) {
        this.reStreet = reStreet;
    }

    /**
     * RE_STREET is nullable
     * Field length on DB: 35
     * tlgns_sap_bp.street, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public String getReStreet() {
        return reStreet;
    }

    /**
     * RE_HOUSE_NUMBER is nullable
     * Field length on DB: 10
     * tlgns_sap_bp.house_number, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public void setReHouseNumber(String reHouseNumber) {
        this.reHouseNumber = reHouseNumber;
    }

    /**
     * RE_HOUSE_NUMBER is nullable
     * Field length on DB: 10
     * tlgns_sap_bp.house_number, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public String getReHouseNumber() {
        return reHouseNumber;
    }

    /**
     * RE_LANG_CD is nullable
     * Field length on DB: 2
     * tlgns_sap_bp.lang_cd, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public void setReLangCd(String reLangCd) {
        this.reLangCd = reLangCd;
    }

    /**
     * RE_LANG_CD is nullable
     * Field length on DB: 2
     * tlgns_sap_bp.lang_cd, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public String getReLangCd() {
        return reLangCd;
    }

    /**
     * RE_CUSTOMER_CLASSIFICATION is nullable
     * Field length on DB: 2
     * tlgns_sap_bp.customer_classification, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public void setReCustomerClassification(String reCustomerClassification) {
        this.reCustomerClassification = reCustomerClassification;
    }

    /**
     * RE_CUSTOMER_CLASSIFICATION is nullable
     * Field length on DB: 2
     * tlgns_sap_bp.customer_classification, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public String getReCustomerClassification() {
        return reCustomerClassification;
    }

    /**
     * RE_RAILWAY_STATION_ID is nullable
     * Field length on DB: 12
     * tlgns_sap_bp.railway_station_id, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public void setReRailwayStationId(Long reRailwayStationId) {
        this.reRailwayStationId = reRailwayStationId;
    }

    /**
     * RE_RAILWAY_STATION_ID is nullable
     * Field length on DB: 12
     * tlgns_sap_bp.railway_station_id, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public Long getReRailwayStationId() {
        return reRailwayStationId;
    }

    /**
     * RE_TRAIN_ST_UNLOADING_POINT is nullable
     * Field length on DB: 4
     * tlgns_sap_bp.train_station_unloading_point, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public void setReTrainStUnloadingPoint(String reTrainStUnloadingPoint) {
        this.reTrainStUnloadingPoint = reTrainStUnloadingPoint;
    }

    /**
     * RE_TRAIN_ST_UNLOADING_POINT is nullable
     * Field length on DB: 4
     * tlgns_sap_bp.train_station_unloading_point, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public String getReTrainStUnloadingPoint() {
        return reTrainStUnloadingPoint;
    }

    /**
     * RE_SBB_CUSTOMER_NUMBER is nullable
     * Field length on DB: 15
     * tlgns_sap_bp.sbb_customer_number, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public void setReSbbCustomerNumber(String reSbbCustomerNumber) {
        this.reSbbCustomerNumber = reSbbCustomerNumber;
    }

    /**
     * RE_SBB_CUSTOMER_NUMBER is nullable
     * Field length on DB: 15
     * tlgns_sap_bp.sbb_customer_number, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public String getReSbbCustomerNumber() {
        return reSbbCustomerNumber;
    }

    /**
     * RE_BP_IS_SHIPTO is nullable
     * 
     * tlgns_sap_bp.bp_is_shipto, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public void setReBpIsShipto(Boolean reBpIsShipto) {
        this.reBpIsShipto = reBpIsShipto;
    }

    /**
     * RE_BP_IS_SHIPTO is nullable
     * 
     * tlgns_sap_bp.bp_is_shipto, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public Boolean getReBpIsShipto() {
        return reBpIsShipto;
    }

    /**
     * RE_BP_IS_SOLDTO is nullable
     * 
     * tlgns_sap_bp.bp_is_soldto, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public void setReBpIsSoldto(Boolean reBpIsSoldto) {
        this.reBpIsSoldto = reBpIsSoldto;
    }

    /**
     * RE_BP_IS_SOLDTO is nullable
     * 
     * tlgns_sap_bp.bp_is_soldto, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public Boolean getReBpIsSoldto() {
        return reBpIsSoldto;
    }

    /**
     * RE_BP_IS_BILLTO is nullable
     * 
     * tlgns_sap_bp.bp_is_billto, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public void setReBpIsBillto(Boolean reBpIsBillto) {
        this.reBpIsBillto = reBpIsBillto;
    }

    /**
     * RE_BP_IS_BILLTO is nullable
     * 
     * tlgns_sap_bp.bp_is_billto, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp
     */
    public Boolean getReBpIsBillto() {
        return reBpIsBillto;
    }

    /**
     * RE_SAP_BP_SAREA_ID is nullable
     * Field length on DB: 12
     * tlgns_sap_bp_sarea.sap_bp_sarea_id, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setReSapBpSareaId(Long reSapBpSareaId) {
        this.reSapBpSareaId = reSapBpSareaId;
    }

    /**
     * RE_SAP_BP_SAREA_ID is nullable
     * Field length on DB: 12
     * tlgns_sap_bp_sarea.sap_bp_sarea_id, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public Long getReSapBpSareaId() {
        return reSapBpSareaId;
    }

    /**
     * RE_SAREA_MARKED_FOR_DELETION is nullable
     * Field length on DB: 1
     * tlgns_sap_bp_sarea.marked_for_deletion, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setReSareaMarkedForDeletion(String reSareaMarkedForDeletion) {
        this.reSareaMarkedForDeletion = reSareaMarkedForDeletion;
    }

    /**
     * RE_SAREA_MARKED_FOR_DELETION is nullable
     * Field length on DB: 1
     * tlgns_sap_bp_sarea.marked_for_deletion, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getReSareaMarkedForDeletion() {
        return reSareaMarkedForDeletion;
    }

    /**
     * RE_SALES_DISTRICT is nullable
     * Field length on DB: 6
     * tlgns_sap_bp_sarea.sales_district, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setReSalesDistrict(String reSalesDistrict) {
        this.reSalesDistrict = reSalesDistrict;
    }

    /**
     * RE_SALES_DISTRICT is nullable
     * Field length on DB: 6
     * tlgns_sap_bp_sarea.sales_district, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getReSalesDistrict() {
        return reSalesDistrict;
    }

    /**
     * RE_SOFFICE_CD is nullable
     * Field length on DB: 4
     * tlgns_sap_bp_sarea.soffice_cd, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setReSofficeCd(String reSofficeCd) {
        this.reSofficeCd = reSofficeCd;
    }

    /**
     * RE_SOFFICE_CD is nullable
     * Field length on DB: 4
     * tlgns_sap_bp_sarea.soffice_cd, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getReSofficeCd() {
        return reSofficeCd;
    }

    /**
     * RE_SGROUP_CD is nullable
     * Field length on DB: 4
     * tlgns_sap_bp_sarea.sgroup_cd, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setReSgroupCd(String reSgroupCd) {
        this.reSgroupCd = reSgroupCd;
    }

    /**
     * RE_SGROUP_CD is nullable
     * Field length on DB: 4
     * tlgns_sap_bp_sarea.sgroup_cd, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getReSgroupCd() {
        return reSgroupCd;
    }

    /**
     * RE_CUSTOMER_GROUP is nullable
     * Field length on DB: 2
     * tlgns_sap_bp_sarea.customer_group, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setReCustomerGroup(String reCustomerGroup) {
        this.reCustomerGroup = reCustomerGroup;
    }

    /**
     * RE_CUSTOMER_GROUP is nullable
     * Field length on DB: 2
     * tlgns_sap_bp_sarea.customer_group, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getReCustomerGroup() {
        return reCustomerGroup;
    }

    /**
     * RE_ABC_CLASS is nullable
     * Field length on DB: 2
     * tlgns_sap_bp_sarea.abc_class, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea.sarea_id
     */
    public void setReAbcClass(String reAbcClass) {
        this.reAbcClass = reAbcClass;
    }

    /**
     * RE_ABC_CLASS is nullable
     * Field length on DB: 2
     * tlgns_sap_bp_sarea.abc_class, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea.sarea_id
     */
    public String getReAbcClass() {
        return reAbcClass;
    }

    /**
     * RE_CURRENCY_CD is nullable
     * Field length on DB: 5
     * tlgns_sap_bp_sarea.currency_cd, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setReCurrencyCd(String reCurrencyCd) {
        this.reCurrencyCd = reCurrencyCd;
    }

    /**
     * RE_CURRENCY_CD is nullable
     * Field length on DB: 5
     * tlgns_sap_bp_sarea.currency_cd, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getReCurrencyCd() {
        return reCurrencyCd;
    }

    /**
     * RE_DISTRIBUTION_DEST_CD is nullable
     * Field length on DB: 4
     * tlgns_sap_bp_sarea.distribution_dest_cd, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setReDistributionDestCd(String reDistributionDestCd) {
        this.reDistributionDestCd = reDistributionDestCd;
    }

    /**
     * RE_DISTRIBUTION_DEST_CD is nullable
     * Field length on DB: 4
     * tlgns_sap_bp_sarea.distribution_dest_cd, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getReDistributionDestCd() {
        return reDistributionDestCd;
    }

    /**
     * RE_CUSTOMER_ORDER_BLOCK is nullable
     * 
     * tlgns_sap_bp_sarea.customer_order_block, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setReCustomerOrderBlock(Boolean reCustomerOrderBlock) {
        this.reCustomerOrderBlock = reCustomerOrderBlock;
    }

    /**
     * RE_CUSTOMER_ORDER_BLOCK is nullable
     * 
     * tlgns_sap_bp_sarea.customer_order_block, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public Boolean getReCustomerOrderBlock() {
        return reCustomerOrderBlock;
    }

    /**
     * RE_CUSTOMER_DELIVERY_BLOCK is nullable
     * 
     * tlgns_sap_bp_sarea.customer_delivery_block, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setReCustomerDeliveryBlock(Boolean reCustomerDeliveryBlock) {
        this.reCustomerDeliveryBlock = reCustomerDeliveryBlock;
    }

    /**
     * RE_CUSTOMER_DELIVERY_BLOCK is nullable
     * 
     * tlgns_sap_bp_sarea.customer_delivery_block, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public Boolean getReCustomerDeliveryBlock() {
        return reCustomerDeliveryBlock;
    }

    /**
     * RE_CUSTOM_OFFICE is nullable
     * Field length on DB: 20
     * tlgns_sap_bp_sarea.custom_office, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setReCustomOffice(String reCustomOffice) {
        this.reCustomOffice = reCustomOffice;
    }

    /**
     * RE_CUSTOM_OFFICE is nullable
     * Field length on DB: 20
     * tlgns_sap_bp_sarea.custom_office, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getReCustomOffice() {
        return reCustomOffice;
    }

    /**
     * RE_CHROMAT_REDUCTION_MONTHS is nullable
     * 
     * tlgns_sap_bp_sarea.chromat_reduction_months, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setReChromatReductionMonths(java.math.BigDecimal reChromatReductionMonths) {
        this.reChromatReductionMonths = reChromatReductionMonths;
    }

    /**
     * RE_CHROMAT_REDUCTION_MONTHS is nullable
     * 
     * tlgns_sap_bp_sarea.chromat_reduction_months, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public java.math.BigDecimal getReChromatReductionMonths() {
        return reChromatReductionMonths;
    }

    /**
     * RE_DEFAULT_PLANT_CD is nullable
     * Field length on DB: 4
     * tlgns_sap_bp_sarea.default_plant_cd, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public void setReDefaultPlantCd(String reDefaultPlantCd) {
        this.reDefaultPlantCd = reDefaultPlantCd;
    }

    /**
     * RE_DEFAULT_PLANT_CD is nullable
     * Field length on DB: 4
     * tlgns_sap_bp_sarea.default_plant_cd, referenced by tlgnc_order.billto_sap_bp_id -> tlgns_sap_bp_sarea.sap_bp_id and tlgnc_order.sarea_id -> tlgns_sap_bp_sarea
     */
    public String getReDefaultPlantCd() {
        return reDefaultPlantCd;
    }

    /**
     * O_SAREA_ID is NOT nullable
     * Field length on DB: 12
     * tlgnc_order.sarea_id. This is the Sales Area of the Order, all following columns SAREA_... belong to this.
     */
    public void setOSareaId(Long oSareaId) {
        this.oSareaId = oSareaId;
    }

    /**
     * O_SAREA_ID is NOT nullable
     * Field length on DB: 12
     * tlgnc_order.sarea_id. This is the Sales Area of the Order, all following columns SAREA_... belong to this.
     */
    public Long getOSareaId() {
        return oSareaId;
    }

    /**
     * SAREA_DISPLAY_LONGNAME is nullable
     * Field length on DB: 30
     * TLGNC_SAREA.sarea_display_longname, referenced by tlgnc_order.sarea_id.
     */
    public void setSareaDisplayLongname(String sareaDisplayLongname) {
        this.sareaDisplayLongname = sareaDisplayLongname;
    }

    /**
     * SAREA_DISPLAY_LONGNAME is nullable
     * Field length on DB: 30
     * TLGNC_SAREA.sarea_display_longname, referenced by tlgnc_order.sarea_id.
     */
    public String getSareaDisplayLongname() {
        return sareaDisplayLongname;
    }

    /**
     * SAREA_SORG_CD is NOT nullable
     * Field length on DB: 4
     * tlgnr_sarea.sorg_cd, referenced by tlgnc_order.sarea_id -> tlgnr_sarea
     */
    public void setSareaSorgCd(String sareaSorgCd) {
        this.sareaSorgCd = sareaSorgCd;
    }

    /**
     * SAREA_SORG_CD is NOT nullable
     * Field length on DB: 4
     * tlgnr_sarea.sorg_cd, referenced by tlgnc_order.sarea_id -> tlgnr_sarea
     */
    public String getSareaSorgCd() {
        return sareaSorgCd;
    }

    /**
     * SAREA_DISTRIBUTION_CHANNEL is NOT nullable
     * Field length on DB: 2
     * tlgnr_sarea.distribution_channel, referenced by tlgnc_order.sarea_id -> tlgnr_sarea
     */
    public void setSareaDistributionChannel(String sareaDistributionChannel) {
        this.sareaDistributionChannel = sareaDistributionChannel;
    }

    /**
     * SAREA_DISTRIBUTION_CHANNEL is NOT nullable
     * Field length on DB: 2
     * tlgnr_sarea.distribution_channel, referenced by tlgnc_order.sarea_id -> tlgnr_sarea
     */
    public String getSareaDistributionChannel() {
        return sareaDistributionChannel;
    }

    /**
     * SAREA_DIVISION_CD is NOT nullable
     * Field length on DB: 2
     * tlgnr_sarea.division, referenced by tlgnc_order.sarea_id -> tlgnr_sarea
     */
    public void setSareaDivisionCd(String sareaDivisionCd) {
        this.sareaDivisionCd = sareaDivisionCd;
    }

    /**
     * SAREA_DIVISION_CD is NOT nullable
     * Field length on DB: 2
     * tlgnr_sarea.division, referenced by tlgnc_order.sarea_id -> tlgnr_sarea
     */
    public String getSareaDivisionCd() {
        return sareaDivisionCd;
    }

    /**
     * SAREA_DIVISION_SEGMENT_CD is nullable
     * Field length on DB: 1
     * tlgnr_sarea.division, referenced by tlgnc_order.sarea_id -> tlgnr_sarea
     */
    public void setSareaDivisionSegmentCd(String sareaDivisionSegmentCd) {
        this.sareaDivisionSegmentCd = sareaDivisionSegmentCd;
    }

    /**
     * SAREA_DIVISION_SEGMENT_CD is nullable
     * Field length on DB: 1
     * tlgnr_sarea.division, referenced by tlgnc_order.sarea_id -> tlgnr_sarea
     */
    public String getSareaDivisionSegmentCd() {
        return sareaDivisionSegmentCd;
    }
    
    /**
     * SAREA_COMPANYCODE_CD is nullable
     * Field length on DB: 4
     * tlgnr_sarea.division, referenced by tlgnc_order.sarea_id -> tlgnr_sarea
     */
    public void setSareaCompanycodeCd(String sareaCompanycodeCd) {
        this.sareaCompanycodeCd = sareaCompanycodeCd;
    }

    /**
     * SAREA_COMPANYCODE_CD is nullable
     * Field length on DB: 4
     * tlgnr_sarea.division, referenced by tlgnc_order.sarea_id -> tlgnr_sarea
     */
    public String getSareaCompanycodeCd() {
        return sareaCompanycodeCd;
    }
    
    /**
     * SAREA_COUNTRY_CD is nullable
     * Field length on DB: 3
     * tlgnc_sarea.sorg_cd -> tlgnr_sorg_bs.companycode_cd -> tlgnr_companycode_bs.country_cd
     */
    public void setSareaCountryCd(String sareaCountryCd) {
        this.sareaCountryCd = sareaCountryCd;
    }

    /**
     * SAREA_COUNTRY_CD is nullable
     * Field length on DB: 3
     * tlgnc_sarea.sorg_cd -> tlgnr_sorg_bs.companycode_cd -> tlgnr_companycode_bs.country_cd
     */
    public String getSareaCountryCd() {
        return sareaCountryCd;
    }
    
    /**
     * O_POOL_ID is nullable
     * Field length on DB: 12
     * tlgnc_order.pool_id.
     */
    public void setOPoolId(Long oPoolId) {
        this.oPoolId = oPoolId;
    }

    /**
     * O_POOL_ID is nullable
     * Field length on DB: 12
     * tlgnc_order.pool_id.
     */
    public Long getOPoolId() {
        return oPoolId;
    }

    /**
     * O_COMM_CARRIER_ID is nullable
     * Field length on DB: 12
     * tlgnc_order.comm_carrier_id. This is the Carrier/Vendor of the order, all following columns COMM_CARRIER_... or COMM_CARRIER_V_... belong to this.
     */
    public void setOCommCarrierId(Long oCommCarrierId) {
        this.oCommCarrierId = oCommCarrierId;
    }

    /**
     * O_COMM_CARRIER_ID is nullable
     * Field length on DB: 12
     * tlgnc_order.comm_carrier_id. This is the Carrier/Vendor of the order, all following columns COMM_CARRIER_... or COMM_CARRIER_V_... belong to this.
     */
    public Long getOCommCarrierId() {
        return oCommCarrierId;
    }

    /**
     * COMM_CARRIER_DISPLAY_NAME is nullable
     * Field length on DB: 40
     * tlgnr_sarea.display_name, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier
     */
    public void setCommCarrierDisplayName(String commCarrierDisplayName) {
        this.commCarrierDisplayName = commCarrierDisplayName;
    }

    /**
     * COMM_CARRIER_DISPLAY_NAME is nullable
     * Field length on DB: 40
     * tlgnr_sarea.display_name, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier
     */
    public String getCommCarrierDisplayName() {
        return commCarrierDisplayName;
    }

    /**
     * COMM_CARRIER_SAP_VENDOR_ID is nullable
     * Field length on DB: 12
     * tlgnr_sarea.sap_vendor_id, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier
     */
    public void setCommCarrierSapVendorId(Long commCarrierSapVendorId) {
        this.commCarrierSapVendorId = commCarrierSapVendorId;
    }

    /**
     * COMM_CARRIER_SAP_VENDOR_ID is nullable
     * Field length on DB: 12
     * tlgnr_sarea.sap_vendor_id, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier
     */
    public Long getCommCarrierSapVendorId() {
        return commCarrierSapVendorId;
    }

    /**
     * COMM_CARRIER_V_SAP_ID is nullable
     * Field length on DB: 10
     * tlgns_sap_vendor.sap_id, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setCommCarrierVSapId(String commCarrierVSapId) {
        this.commCarrierVSapId = commCarrierVSapId;
    }

    /**
     * COMM_CARRIER_V_SAP_ID is nullable
     * Field length on DB: 10
     * tlgns_sap_vendor.sap_id, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getCommCarrierVSapId() {
        return commCarrierVSapId;
    }

    /**
     * COMM_CARRIER_V_ACCOUNT_GROUP is nullable
     * Field length on DB: 4
     * tlgns_sap_vendor.account_group, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setCommCarrierVAccountGroup(String commCarrierVAccountGroup) {
        this.commCarrierVAccountGroup = commCarrierVAccountGroup;
    }

    /**
     * COMM_CARRIER_V_ACCOUNT_GROUP is nullable
     * Field length on DB: 4
     * tlgns_sap_vendor.account_group, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getCommCarrierVAccountGroup() {
        return commCarrierVAccountGroup;
    }

    /**
     * COMM_CARRIER_V_MARKED_FOR_DEL is nullable
     * Field length on DB: 1
     * tlgns_sap_vendor.marked_for_deletion, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setCommCarrierVMarkedForDel(String commCarrierVMarkedForDel) {
        this.commCarrierVMarkedForDel = commCarrierVMarkedForDel;
    }

    /**
     * COMM_CARRIER_V_MARKED_FOR_DEL is nullable
     * Field length on DB: 1
     * tlgns_sap_vendor.marked_for_deletion, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getCommCarrierVMarkedForDel() {
        return commCarrierVMarkedForDel;
    }

    /**
     * COMM_CARRIER_V_NAME1 is nullable
     * Field length on DB: 35
     * tlgns_sap_vendor.name1, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setCommCarrierVName1(String commCarrierVName1) {
        this.commCarrierVName1 = commCarrierVName1;
    }

    /**
     * COMM_CARRIER_V_NAME1 is nullable
     * Field length on DB: 35
     * tlgns_sap_vendor.name1, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getCommCarrierVName1() {
        return commCarrierVName1;
    }

    /**
     * COMM_CARRIER_V_NAME2 is nullable
     * Field length on DB: 35
     * tlgns_sap_vendor.name2, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setCommCarrierVName2(String commCarrierVName2) {
        this.commCarrierVName2 = commCarrierVName2;
    }

    /**
     * COMM_CARRIER_V_NAME2 is nullable
     * Field length on DB: 35
     * tlgns_sap_vendor.name2, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getCommCarrierVName2() {
        return commCarrierVName2;
    }

    /**
     * COMM_CARRIER_V_COUNTRY_CD is nullable
     * Field length on DB: 3
     * tlgns_sap_vendor.country_cd, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setCommCarrierVCountryCd(String commCarrierVCountryCd) {
        this.commCarrierVCountryCd = commCarrierVCountryCd;
    }

    /**
     * COMM_CARRIER_V_COUNTRY_CD is nullable
     * Field length on DB: 3
     * tlgns_sap_vendor.country_cd, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getCommCarrierVCountryCd() {
        return commCarrierVCountryCd;
    }

    /**
     * COMM_CARRIER_V_POSTAL_CODE is nullable
     * Field length on DB: 10
     * tlgns_sap_vendor.postal_code, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setCommCarrierVPostalCode(String commCarrierVPostalCode) {
        this.commCarrierVPostalCode = commCarrierVPostalCode;
    }

    /**
     * COMM_CARRIER_V_POSTAL_CODE is nullable
     * Field length on DB: 10
     * tlgns_sap_vendor.postal_code, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getCommCarrierVPostalCode() {
        return commCarrierVPostalCode;
    }

    /**
     * COMM_CARRIER_V_CITY is nullable
     * Field length on DB: 35
     * tlgns_sap_vendor.city, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setCommCarrierVCity(String commCarrierVCity) {
        this.commCarrierVCity = commCarrierVCity;
    }

    /**
     * COMM_CARRIER_V_CITY is nullable
     * Field length on DB: 35
     * tlgns_sap_vendor.city, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getCommCarrierVCity() {
        return commCarrierVCity;
    }

    /**
     * COMM_CARRIER_V_LOCATION_DIST is nullable
     * Field length on DB: 40
     * tlgns_sap_vendor.loacation_district, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setCommCarrierVLocationDist(String commCarrierVLocationDist) {
        this.commCarrierVLocationDist = commCarrierVLocationDist;
    }

    /**
     * COMM_CARRIER_V_LOCATION_DIST is nullable
     * Field length on DB: 40
     * tlgns_sap_vendor.loacation_district, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getCommCarrierVLocationDist() {
        return commCarrierVLocationDist;
    }

    /**
     * COMM_CARRIER_V_REGION is nullable
     * Field length on DB: 3
     * tlgns_sap_vendor.region, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setCommCarrierVRegion(String commCarrierVRegion) {
        this.commCarrierVRegion = commCarrierVRegion;
    }

    /**
     * COMM_CARRIER_V_REGION is nullable
     * Field length on DB: 3
     * tlgns_sap_vendor.region, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getCommCarrierVRegion() {
        return commCarrierVRegion;
    }

    /**
     * COMM_CARRIER_V_STREET is nullable
     * Field length on DB: 35
     * tlgns_sap_vendor.street, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setCommCarrierVStreet(String commCarrierVStreet) {
        this.commCarrierVStreet = commCarrierVStreet;
    }

    /**
     * COMM_CARRIER_V_STREET is nullable
     * Field length on DB: 35
     * tlgns_sap_vendor.street, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getCommCarrierVStreet() {
        return commCarrierVStreet;
    }

    /**
     * COMM_CARRIER_V_HOUSE_NUMBER is nullable
     * Field length on DB: 10
     * tlgns_sap_vendor.house_number, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setCommCarrierVHouseNumber(String commCarrierVHouseNumber) {
        this.commCarrierVHouseNumber = commCarrierVHouseNumber;
    }

    /**
     * COMM_CARRIER_V_HOUSE_NUMBER is nullable
     * Field length on DB: 10
     * tlgns_sap_vendor.house_number, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getCommCarrierVHouseNumber() {
        return commCarrierVHouseNumber;
    }

    /**
     * COMM_CARRIER_V_LANG_CD is nullable
     * Field length on DB: 2
     * tlgns_sap_vendor.lang_cd, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setCommCarrierVLangCd(String commCarrierVLangCd) {
        this.commCarrierVLangCd = commCarrierVLangCd;
    }

    /**
     * COMM_CARRIER_V_LANG_CD is nullable
     * Field length on DB: 2
     * tlgns_sap_vendor.lang_cd, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getCommCarrierVLangCd() {
        return commCarrierVLangCd;
    }

    /**
     * COMM_CARRIER_V_VAT_REGIST_NO is nullable
     * Field length on DB: 20
     * tlgns_sap_vendor.registration_number, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setCommCarrierVVatRegistNo(String commCarrierVVatRegistNo) {
        this.commCarrierVVatRegistNo = commCarrierVVatRegistNo;
    }

    /**
     * COMM_CARRIER_V_VAT_REGIST_NO is nullable
     * Field length on DB: 20
     * tlgns_sap_vendor.registration_number, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getCommCarrierVVatRegistNo() {
        return commCarrierVVatRegistNo;
    }

    /**
     * COMM_CARRIER_IS_INTERNAL is nullable
     * 
     * tlgns_sap_vendor.vn_is_internal, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setCommCarrierIsInternal(Boolean commCarrierIsInternal) {
        this.commCarrierIsInternal = commCarrierIsInternal;
    }

    /**
     * COMM_CARRIER_IS_INTERNAL is nullable
     * 
     * tlgns_sap_vendor.vn_is_internal, referenced by tlgnc_order.comm_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public Boolean getCommCarrierIsInternal() {
        return commCarrierIsInternal;
    }

    /**
     * O_EXEC_CARRIER_ID is nullable
     * Field length on DB: 12
     * tlgnc_order.exec_carrier_id. This is the Carrier/Vendor of the order, all following columns EXEC_CARRIER_... or EXEC_CARRIER_V_... belong to this.
     */
    public void setOExecCarrierId(Long oExecCarrierId) {
        this.oExecCarrierId = oExecCarrierId;
    }

    /**
     * O_EXEC_CARRIER_ID is nullable
     * Field length on DB: 12
     * tlgnc_order.exec_carrier_id. This is the Carrier/Vendor of the order, all following columns EXEC_CARRIER_... or EXEC_CARRIER_V_... belong to this.
     */
    public Long getOExecCarrierId() {
        return oExecCarrierId;
    }

    /**
     * EXEC_CARRIER_DISPLAY_NAME is nullable
     * Field length on DB: 40
     * tlgnr_sarea.display_name, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier
     */
    public void setExecCarrierDisplayName(String execCarrierDisplayName) {
        this.execCarrierDisplayName = execCarrierDisplayName;
    }

    /**
     * EXEC_CARRIER_DISPLAY_NAME is nullable
     * Field length on DB: 40
     * tlgnr_sarea.display_name, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier
     */
    public String getExecCarrierDisplayName() {
        return execCarrierDisplayName;
    }

    /**
     * EXEC_CARRIER_SAP_VENDOR_ID is nullable
     * Field length on DB: 12
     * tlgnr_sarea.sap_vendor_id, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier
     */
    public void setExecCarrierSapVendorId(Long execCarrierSapVendorId) {
        this.execCarrierSapVendorId = execCarrierSapVendorId;
    }

    /**
     * EXEC_CARRIER_SAP_VENDOR_ID is nullable
     * Field length on DB: 12
     * tlgnr_sarea.sap_vendor_id, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier
     */
    public Long getExecCarrierSapVendorId() {
        return execCarrierSapVendorId;
    }

    /**
     * EXEC_CARRIER_V_SAP_ID is nullable
     * Field length on DB: 10
     * tlgns_sap_vendor.sap_id, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setExecCarrierVSapId(String execCarrierVSapId) {
        this.execCarrierVSapId = execCarrierVSapId;
    }

    /**
     * EXEC_CARRIER_V_SAP_ID is nullable
     * Field length on DB: 10
     * tlgns_sap_vendor.sap_id, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getExecCarrierVSapId() {
        return execCarrierVSapId;
    }

    /**
     * EXEC_CARRIER_V_ACCOUNT_GROUP is nullable
     * Field length on DB: 4
     * tlgns_sap_vendor.account_group, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setExecCarrierVAccountGroup(String execCarrierVAccountGroup) {
        this.execCarrierVAccountGroup = execCarrierVAccountGroup;
    }

    /**
     * EXEC_CARRIER_V_ACCOUNT_GROUP is nullable
     * Field length on DB: 4
     * tlgns_sap_vendor.account_group, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getExecCarrierVAccountGroup() {
        return execCarrierVAccountGroup;
    }

    /**
     * EXEC_CARRIER_V_MARKED_FOR_DEL is nullable
     * Field length on DB: 1
     * tlgns_sap_vendor.marked_for_deletion, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setExecCarrierVMarkedForDel(String execCarrierVMarkedForDel) {
        this.execCarrierVMarkedForDel = execCarrierVMarkedForDel;
    }

    /**
     * EXEC_CARRIER_V_MARKED_FOR_DEL is nullable
     * Field length on DB: 1
     * tlgns_sap_vendor.marked_for_deletion, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getExecCarrierVMarkedForDel() {
        return execCarrierVMarkedForDel;
    }

    /**
     * EXEC_CARRIER_V_NAME1 is nullable
     * Field length on DB: 35
     * tlgns_sap_vendor.name1, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setExecCarrierVName1(String execCarrierVName1) {
        this.execCarrierVName1 = execCarrierVName1;
    }

    /**
     * EXEC_CARRIER_V_NAME1 is nullable
     * Field length on DB: 35
     * tlgns_sap_vendor.name1, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getExecCarrierVName1() {
        return execCarrierVName1;
    }

    /**
     * EXEC_CARRIER_V_NAME2 is nullable
     * Field length on DB: 35
     * tlgns_sap_vendor.name2, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setExecCarrierVName2(String execCarrierVName2) {
        this.execCarrierVName2 = execCarrierVName2;
    }

    /**
     * EXEC_CARRIER_V_NAME2 is nullable
     * Field length on DB: 35
     * tlgns_sap_vendor.name2, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getExecCarrierVName2() {
        return execCarrierVName2;
    }

    /**
     * EXEC_CARRIER_V_COUNTRY_CD is nullable
     * Field length on DB: 3
     * tlgns_sap_vendor.country_cd, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setExecCarrierVCountryCd(String execCarrierVCountryCd) {
        this.execCarrierVCountryCd = execCarrierVCountryCd;
    }

    /**
     * EXEC_CARRIER_V_COUNTRY_CD is nullable
     * Field length on DB: 3
     * tlgns_sap_vendor.country_cd, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getExecCarrierVCountryCd() {
        return execCarrierVCountryCd;
    }

    /**
     * EXEC_CARRIER_V_POSTAL_CODE is nullable
     * Field length on DB: 10
     * tlgns_sap_vendor.postal_code, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setExecCarrierVPostalCode(String execCarrierVPostalCode) {
        this.execCarrierVPostalCode = execCarrierVPostalCode;
    }

    /**
     * EXEC_CARRIER_V_POSTAL_CODE is nullable
     * Field length on DB: 10
     * tlgns_sap_vendor.postal_code, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getExecCarrierVPostalCode() {
        return execCarrierVPostalCode;
    }

    /**
     * EXEC_CARRIER_V_CITY is nullable
     * Field length on DB: 35
     * tlgns_sap_vendor.city, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setExecCarrierVCity(String execCarrierVCity) {
        this.execCarrierVCity = execCarrierVCity;
    }

    /**
     * EXEC_CARRIER_V_CITY is nullable
     * Field length on DB: 35
     * tlgns_sap_vendor.city, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getExecCarrierVCity() {
        return execCarrierVCity;
    }

    /**
     * EXEC_CARRIER_V_LOCATION_DIST is nullable
     * Field length on DB: 40
     * tlgns_sap_vendor.loacation_district, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setExecCarrierVLocationDist(String execCarrierVLocationDist) {
        this.execCarrierVLocationDist = execCarrierVLocationDist;
    }

    /**
     * EXEC_CARRIER_V_LOCATION_DIST is nullable
     * Field length on DB: 40
     * tlgns_sap_vendor.loacation_district, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getExecCarrierVLocationDist() {
        return execCarrierVLocationDist;
    }

    /**
     * EXEC_CARRIER_V_REGION is nullable
     * Field length on DB: 3
     * tlgns_sap_vendor.region, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setExecCarrierVRegion(String execCarrierVRegion) {
        this.execCarrierVRegion = execCarrierVRegion;
    }

    /**
     * EXEC_CARRIER_V_REGION is nullable
     * Field length on DB: 3
     * tlgns_sap_vendor.region, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getExecCarrierVRegion() {
        return execCarrierVRegion;
    }

    /**
     * EXEC_CARRIER_V_STREET is nullable
     * Field length on DB: 35
     * tlgns_sap_vendor.street, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setExecCarrierVStreet(String execCarrierVStreet) {
        this.execCarrierVStreet = execCarrierVStreet;
    }

    /**
     * EXEC_CARRIER_V_STREET is nullable
     * Field length on DB: 35
     * tlgns_sap_vendor.street, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getExecCarrierVStreet() {
        return execCarrierVStreet;
    }

    /**
     * EXEC_CARRIER_V_HOUSE_NUMBER is nullable
     * Field length on DB: 10
     * tlgns_sap_vendor.house_number, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setExecCarrierVHouseNumber(String execCarrierVHouseNumber) {
        this.execCarrierVHouseNumber = execCarrierVHouseNumber;
    }

    /**
     * EXEC_CARRIER_V_HOUSE_NUMBER is nullable
     * Field length on DB: 10
     * tlgns_sap_vendor.house_number, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getExecCarrierVHouseNumber() {
        return execCarrierVHouseNumber;
    }

    /**
     * EXEC_CARRIER_V_LANG_CD is nullable
     * Field length on DB: 2
     * tlgns_sap_vendor.lang_cd, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setExecCarrierVLangCd(String execCarrierVLangCd) {
        this.execCarrierVLangCd = execCarrierVLangCd;
    }

    /**
     * EXEC_CARRIER_V_LANG_CD is nullable
     * Field length on DB: 2
     * tlgns_sap_vendor.lang_cd, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getExecCarrierVLangCd() {
        return execCarrierVLangCd;
    }

    /**
     * EXEC_CARRIER_V_VAT_REGIST_NO is nullable
     * Field length on DB: 20
     * tlgns_sap_vendor.registration_number, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setExecCarrierVVatRegistNo(String execCarrierVVatRegistNo) {
        this.execCarrierVVatRegistNo = execCarrierVVatRegistNo;
    }

    /**
     * EXEC_CARRIER_V_VAT_REGIST_NO is nullable
     * Field length on DB: 20
     * tlgns_sap_vendor.registration_number, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getExecCarrierVVatRegistNo() {
        return execCarrierVVatRegistNo;
    }

    /**
     * EXEC_CARRIER_IS_INTERNAL is nullable
     * 
     * tlgns_sap_vendor.vn_is_internal, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setExecCarrierIsInternal(Boolean execCarrierIsInternal) {
        this.execCarrierIsInternal = execCarrierIsInternal;
    }

    /**
     * EXEC_CARRIER_IS_INTERNAL is nullable
     * 
     * tlgns_sap_vendor.vn_is_internal, referenced by tlgnc_order.exec_carrier_id -> tlgnc_carrier.sap_vendor_id -> tlgns_sap_vendor
     */
    public Boolean getExecCarrierIsInternal() {
        return execCarrierIsInternal;
    }

    /**
     * O_SHIPPINGPOINT_ID is nullable
     * Field length on DB: 12
     * tlgnc_order.shippingpoint_id
     */
    public void setOShippingpointId(Long oShippingpointId) {
        this.oShippingpointId = oShippingpointId;
    }

    /**
     * O_SHIPPINGPOINT_ID is nullable
     * Field length on DB: 12
     * tlgnc_order.shippingpoint_id
     */
    public Long getOShippingpointId() {
        return oShippingpointId;
    }
    
	/** 
	*SENT_KPI_TO_SAP_BW_TS IS NULLABLE
	*/
	public void setSentKpiToSapBwTs(Date sentKpiToSapBwTs) {
		this.sentKpiToSapBwTs = sentKpiToSapBwTs;
	}

	/**
	*SENT_KPI_TO_SAP_BW_TS
	*/
	public Date getSentKpiToSapBwTs() {
		return sentKpiToSapBwTs;
	}

    /**
     * SP_PLANT_CD is nullable
     * Field length on DB: 4
     * tlgnc_shippingpoint.plant_cd, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public void setSpPlantCd(String spPlantCd) {
        this.spPlantCd = spPlantCd;
    }

    /**
     * SP_PLANT_CD is nullable
     * Field length on DB: 4
     * tlgnc_shippingpoint.plant_cd, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public String getSpPlantCd() {
        return spPlantCd;
    }

    /**
     * SP_PLANT_NAME is nullable
     * Field length on DB: 80
     * tlgnr_plant_bs.plant_name, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint -> tlgnr_plant_bs
     */
    public void setSpPlantName(String spPlantName) {
        this.spPlantName = spPlantName;
    }

    /**
     * SP_PLANT_NAME is nullable
     * Field length on DB: 80
     * tlgnr_plant_bs.plant_name, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint -> tlgnr_plant_bs
     */
    public String getSpPlantName() {
        return spPlantName;
    }

    /**
     * SP_SAP_SHIPPINGPOINT_NO is nullable
     * Field length on DB: 4
     * tlgnc_shippingpoint.shippingpoint_no, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public void setSpSapShippingpointNo(String spSapShippingpointNo) {
        this.spSapShippingpointNo = spSapShippingpointNo;
    }

    /**
     * SP_SAP_SHIPPINGPOINT_NO is nullable
     * Field length on DB: 4
     * tlgnc_shippingpoint.shippingpoint_no, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public String getSpSapShippingpointNo() {
        return spSapShippingpointNo;
    }

    /**
     * SP_SHIPPINGPOINT_NAME is nullable
     * Field length on DB: 80
     * tlgnc_shippingpoint.shippingpoint_name, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public void setSpShippingpointName(String spShippingpointName) {
        this.spShippingpointName = spShippingpointName;
    }

    /**
     * SP_SHIPPINGPOINT_NAME is nullable
     * Field length on DB: 80
     * tlgnc_shippingpoint.shippingpoint_name, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public String getSpShippingpointName() {
        return spShippingpointName;
    }

    /**
     * SP_SHIPPPOINT_TYPE_CD is nullable
     * Field length on DB: 2
     * tlgnc_shippingpoint.shippingpoint_type_cd, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public void setSpShipppointTypeCd(Long spShipppointTypeCd) {
        this.spShipppointTypeCd = spShipppointTypeCd;
    }

    /**
     * SP_SHIPPPOINT_TYPE_CD is nullable
     * Field length on DB: 2
     * tlgnc_shippingpoint.shippingpoint_type_cd, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public Long getSpShipppointTypeCd() {
        return spShipppointTypeCd;
    }

    /**
     * SP_SAP_SHIPPING_COND_CD is nullable
     * Field length on DB: 2
     * tlgnc_shippingpoint.sap_shippingpoint_cond_cd, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public void setSpSapShippingCondCd(String spSapShippingCondCd) {
        this.spSapShippingCondCd = spSapShippingCondCd;
    }

    /**
     * SP_SAP_SHIPPING_COND_CD is nullable
     * Field length on DB: 2
     * tlgnc_shippingpoint.sap_shippingpoint_cond_cd, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public String getSpSapShippingCondCd() {
        return spSapShippingCondCd;
    }

    /**
     * SP_STORAGE_LOCATION is nullable
     * Field length on DB: 4
     * tlgnc_shippingpoint.storage_location, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public void setSpStorageLocation(String spStorageLocation) {
        this.spStorageLocation = spStorageLocation;
    }

    /**
     * SP_STORAGE_LOCATION is nullable
     * Field length on DB: 4
     * tlgnc_shippingpoint.storage_location, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public String getSpStorageLocation() {
        return spStorageLocation;
    }

    /**
     * SP_RAILWAY_STATION_ID is nullable
     * Field length on DB: 12
     * tlgnc_shippingpoint.railway_station_id, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public void setSpRailwayStationId(Long spRailwayStationId) {
        this.spRailwayStationId = spRailwayStationId;
    }

    /**
     * SP_RAILWAY_STATION_ID is nullable
     * Field length on DB: 12
     * tlgnc_shippingpoint.railway_station_id, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public Long getSpRailwayStationId() {
        return spRailwayStationId;
    }

    /**
     * SP_RS_UIC_COUNTRY_CD is nullable
     * Field length on DB: 2
     * tlgnc_railway_station.uic_country_cd, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint.railway_station_id -> tlgnc_railway_station
     */
    public void setSpRsUicCountryCd(Long spRsUicCountryCd) {
        this.spRsUicCountryCd = spRsUicCountryCd;
    }

    /**
     * SP_RS_UIC_COUNTRY_CD is nullable
     * Field length on DB: 2
     * tlgnc_railway_station.uic_country_cd, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint.railway_station_id -> tlgnc_railway_station
     */
    public Long getSpRsUicCountryCd() {
        return spRsUicCountryCd;
    }

    /**
     * SP_RS_RAILWAY_STATION_NO is nullable
     * Field length on DB: 6
     * tlgnc_railway_station.railway_station_no, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint.railway_station_id -> tlgnc_railway_station
     */
    public void setSpRsRailwayStationNo(String spRsRailwayStationNo) {
        this.spRsRailwayStationNo = spRsRailwayStationNo;
    }

    /**
     * SP_RS_RAILWAY_STATION_NO is nullable
     * Field length on DB: 6
     * tlgnc_railway_station.railway_station_no, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint.railway_station_id -> tlgnc_railway_station
     */
    public String getSpRsRailwayStationNo() {
        return spRsRailwayStationNo;
    }

    /**
     * SP_RS_RAILWAY_STATION_NAME is nullable
     * Field length on DB: 50
     * tlgnc_railway_station.railway_station_name, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint.railway_station_id -> tlgnc_railway_station
     */
    public void setSpRsRailwayStationName(String spRsRailwayStationName) {
        this.spRsRailwayStationName = spRsRailwayStationName;
    }

    /**
     * SP_RS_RAILWAY_STATION_NAME is nullable
     * Field length on DB: 50
     * tlgnc_railway_station.railway_station_name, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint.railway_station_id -> tlgnc_railway_station
     */
    public String getSpRsRailwayStationName() {
        return spRsRailwayStationName;
    }

    /**
     * SP_RAILWAY_STATION_LP is nullable
     * Field length on DB: 4
     * tlgnc_shippingpoint.railway_station_loading_point, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public void setSpRailwayStationLp(String spRailwayStationLp) {
        this.spRailwayStationLp = spRailwayStationLp;
    }

    /**
     * SP_RAILWAY_STATION_LP is nullable
     * Field length on DB: 4
     * tlgnc_shippingpoint.railway_station_loading_point, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public String getSpRailwayStationLp() {
        return spRailwayStationLp;
    }

    /**
     * SP_WEIGH_SYSTEM_SP_CD is nullable
     * Field length on DB: 2
     * tlgnc_shippingpoint.weigh_system_sp_cd, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public void setSpWeighSystemSpCd(Long spWeighSystemSpCd) {
        this.spWeighSystemSpCd = spWeighSystemSpCd;
    }

    /**
     * SP_WEIGH_SYSTEM_SP_CD is nullable
     * Field length on DB: 2
     * tlgnc_shippingpoint.weigh_system_sp_cd, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public Long getSpWeighSystemSpCd() {
        return spWeighSystemSpCd;
    }

    /**
     * SP_LDS_SERVER is nullable
     * Field length on DB: 30
     * tlgnc_shippingpoint.lds_server, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public void setSpLdsServer(String spLdsServer) {
        this.spLdsServer = spLdsServer;
    }

    /**
     * SP_LDS_SERVER is nullable
     * Field length on DB: 30
     * tlgnc_shippingpoint.lds_server, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public String getSpLdsServer() {
        return spLdsServer;
    }

    /**
     * SP_SHIPPINGPOINT_COMMENT is nullable
     * Field length on DB: 255
     * tlgnc_shippingpoint.shippingpoiint_comment, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public void setSpShippingpointComment(String spShippingpointComment) {
        this.spShippingpointComment = spShippingpointComment;
    }

    /**
     * SP_SHIPPINGPOINT_COMMENT is nullable
     * Field length on DB: 255
     * tlgnc_shippingpoint.shippingpoiint_comment, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public String getSpShippingpointComment() {
        return spShippingpointComment;
    }

    /**
     * SP_COORD_LAT is nullable
     * 
     * tlgnc_shippingpoint.coord_lat, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public void setSpCoordLat(Double spCoordLat) {
        this.spCoordLat = spCoordLat;
    }

    /**
     * SP_COORD_LAT is nullable
     * 
     * tlgnc_shippingpoint.coord_lat, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public Double getSpCoordLat() {
        return spCoordLat;
    }

    /**
     * SP_COORD_LON is nullable
     * 
     * tlgnc_shippingpoint.coord_lon, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public void setSpCoordLon(Double spCoordLon) {
        this.spCoordLon = spCoordLon;
    }

    /**
     * SP_COORD_LON is nullable
     * 
     * tlgnc_shippingpoint.coord_lon, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public Double getSpCoordLon() {
        return spCoordLon;
    }

    /**
     * SP_COORD_FIX_FLAG is nullable
     * 
     * tlgnc_shippingpoint.coord_fix_flag, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public void setSpCoordFixFlag(Boolean spCoordFixFlag) {
        this.spCoordFixFlag = spCoordFixFlag;
    }

    /**
     * SP_COORD_FIX_FLAG is nullable
     * 
     * tlgnc_shippingpoint.coord_fix_flag, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public Boolean getSpCoordFixFlag() {
        return spCoordFixFlag;
    }

    /**
     * SP_GEOFENCE_RADIUS is nullable
     * Field length on DB: 10
     * tlgnc_shippingpoint.geofence_radius, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public void setSpGeofenceRadius(Long spGeofenceRadius) {
        this.spGeofenceRadius = spGeofenceRadius;
    }

    /**
     * SP_GEOFENCE_RADIUS is nullable
     * Field length on DB: 10
     * tlgnc_shippingpoint.geofence_radius, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public Long getSpGeofenceRadius() {
        return spGeofenceRadius;
    }

    /**
     * SP_FVL_MIX_SHIPTO_PER_WAGON_FL is nullable
     * 
     * tlgnc_shippingpoint.fvl_mix_shipto_per_wagon_flag, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public void setSpFvlMixShiptoPerWagonFl(Boolean spFvlMixShiptoPerWagonFl) {
        this.spFvlMixShiptoPerWagonFl = spFvlMixShiptoPerWagonFl;
    }

    /**
     * SP_FVL_MIX_SHIPTO_PER_WAGON_FL is nullable
     * 
     * tlgnc_shippingpoint.fvl_mix_shipto_per_wagon_flag, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public Boolean getSpFvlMixShiptoPerWagonFl() {
        return spFvlMixShiptoPerWagonFl;
    }

    /**
     * SP_SEND_SAP_SHIPPINGPOINT_ID is nullable
     * Field length on DB: 12
     * tlgnc_shippingpoint.send_sap_shippingpoint_id, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public void setSpSendSapShippingpointId(Long spSendSapShippingpointId) {
        this.spSendSapShippingpointId = spSendSapShippingpointId;
    }

    /**
     * SP_SEND_SAP_SHIPPINGPOINT_ID is nullable
     * Field length on DB: 12
     * tlgnc_shippingpoint.send_sap_shippingpoint_id, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint
     */
    public Long getSpSendSapShippingpointId() {
        return spSendSapShippingpointId;
    }

    /**
     * SP_SEND_SAP_SP_PLANT_CD is nullable
     * Field length on DB: 4
     * tlgnc_shippingpoint.plant_cd, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint.send_sap_shippingpoint_id -> tlgnc_shippingpoint
     */
    public void setSpSendSapSpPlantCd(String spSendSapSpPlantCd) {
        this.spSendSapSpPlantCd = spSendSapSpPlantCd;
    }

    /**
     * SP_SEND_SAP_SP_PLANT_CD is nullable
     * Field length on DB: 4
     * tlgnc_shippingpoint.plant_cd, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint.send_sap_shippingpoint_id -> tlgnc_shippingpoint
     */
    public String getSpSendSapSpPlantCd() {
        return spSendSapSpPlantCd;
    }

    /**
     * SP_SEND_SAP_SP_SAP_SHIPPT_NO is nullable
     * Field length on DB: 4
     * tlgnc_shippingpoint.sap_shippingpoint_no, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint.send_sap_shippingpoint_id -> tlgnc_shippingpoint
     */
    public void setSpSendSapSpSapShipptNo(String spSendSapSpSapShipptNo) {
        this.spSendSapSpSapShipptNo = spSendSapSpSapShipptNo;
    }

    /**
     * SP_SEND_SAP_SP_SAP_SHIPPT_NO is nullable
     * Field length on DB: 4
     * tlgnc_shippingpoint.sap_shippingpoint_no, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint.send_sap_shippingpoint_id -> tlgnc_shippingpoint
     */
    public String getSpSendSapSpSapShipptNo() {
        return spSendSapSpSapShipptNo;
    }

    /**
     * SP_SEND_SAP_SP_SHIPPT_NAME is nullable
     * Field length on DB: 80
     * tlgnc_shippingpoint.shippingpoint_name, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint.send_sap_shippingpoint_id -> tlgnc_shippingpoint
     */
    public void setSpSendSapSpShipptName(String spSendSapSpShipptName) {
        this.spSendSapSpShipptName = spSendSapSpShipptName;
    }

    /**
     * SP_SEND_SAP_SP_SHIPPT_NAME is nullable
     * Field length on DB: 80
     * tlgnc_shippingpoint.shippingpoint_name, referenced by tlgnc_order.shippingpoint_id -> tlgnc_shippingpoint.send_sap_shippingpoint_id -> tlgnc_shippingpoint
     */
    public String getSpSendSapSpShipptName() {
        return spSendSapSpShipptName;
    }

    /**
     * O_ACT_DEP_RAILWAY_STATION_ID is nullable
     * Field length on DB: 12
     * tlgnc_order.actual_dep_railway_station_id
     */
    public void setOActDepRailwayStationId(Long oActDepRailwayStationId) {
        this.oActDepRailwayStationId = oActDepRailwayStationId;
    }

    /**
     * O_ACT_DEP_RAILWAY_STATION_ID is nullable
     * Field length on DB: 12
     * tlgnc_order.actual_dep_railway_station_id
     */
    public Long getOActDepRailwayStationId() {
        return oActDepRailwayStationId;
    }

    /**
     * O_ACT_DEP_RS_UIC_COUNTRY_CD is nullable
     * Field length on DB: 2
     * tlgnc_railway_station.uic_country_cd, referenced by tlgnc_order.actual_dep_railway_station_id -> tlgnc_railway_station
     */
    public void setOActDepRsUicCountryCd(Long oActDepRsUicCountryCd) {
        this.oActDepRsUicCountryCd = oActDepRsUicCountryCd;
    }

    /**
     * O_ACT_DEP_RS_UIC_COUNTRY_CD is nullable
     * Field length on DB: 2
     * tlgnc_railway_station.uic_country_cd, referenced by tlgnc_order.actual_dep_railway_station_id -> tlgnc_railway_station
     */
    public Long getOActDepRsUicCountryCd() {
        return oActDepRsUicCountryCd;
    }

    /**
     * O_ACT_DEP_RS_STATION_NO is nullable
     * Field length on DB: 6
     * tlgnc_railway_station.railway_station_no, referenced by tlgnc_order.actual_dep_railway_station_id -> tlgnc_railway_station
     */
    public void setOActDepRsStationNo(String oActDepRsStationNo) {
        this.oActDepRsStationNo = oActDepRsStationNo;
    }

    /**
     * O_ACT_DEP_RS_STATION_NO is nullable
     * Field length on DB: 6
     * tlgnc_railway_station.railway_station_no, referenced by tlgnc_order.actual_dep_railway_station_id -> tlgnc_railway_station
     */
    public String getOActDepRsStationNo() {
        return oActDepRsStationNo;
    }

    /**
     * O_ACT_DEP_RS_STATION_NAME is nullable
     * Field length on DB: 50
     * tlgnc_railway_station.railway_station_name, referenced by tlgnc_order.actual_dep_railway_station_id -> tlgnc_railway_station
     */
    public void setOActDepRsStationName(String oActDepRsStationName) {
        this.oActDepRsStationName = oActDepRsStationName;
    }

    /**
     * O_ACT_DEP_RS_STATION_NAME is nullable
     * Field length on DB: 50
     * tlgnc_railway_station.railway_station_name, referenced by tlgnc_order.actual_dep_railway_station_id -> tlgnc_railway_station
     */
    public String getOActDepRsStationName() {
        return oActDepRsStationName;
    }

    /**
     * O_ACT_DEP_RS_COM_UIC_CNTRY_CD is nullable
     * Field length on DB: 2
     * tlgnc_railway_station.uic_country_cd referenced by tlgnc_order.actual_dep_railway_station_id -> tlgnc_railway_station ->tlgnc_railway_station_comm.comm_railway_station_id -> tlgnc_railway_station
     */
    public void setOActDepRsComUicCntryCd(Long oActDepRsComUicCntryCd) {
        this.oActDepRsComUicCntryCd = oActDepRsComUicCntryCd;
    }

    /**
     * O_ACT_DEP_RS_COM_UIC_CNTRY_CD is nullable
     * Field length on DB: 2
     * tlgnc_railway_station.uic_country_cd referenced by tlgnc_order.actual_dep_railway_station_id -> tlgnc_railway_station ->tlgnc_railway_station_comm.comm_railway_station_id -> tlgnc_railway_station
     */
    public Long getOActDepRsComUicCntryCd() {
        return oActDepRsComUicCntryCd;
    }

    /**
     * O_ACT_DEP_RS_COM_STATION_NO is nullable
     * Field length on DB: 6
     * tlgnc_railway_station.railway_station_no referenced by tlgnc_order.actual_dep_railway_station_id -> tlgnc_railway_station ->tlgnc_railway_station_comm.comm_railway_station_id -> tlgnc_railway_station
     */
    public void setOActDepRsComStationNo(String oActDepRsComStationNo) {
        this.oActDepRsComStationNo = oActDepRsComStationNo;
    }

    /**
     * O_ACT_DEP_RS_COM_STATION_NO is nullable
     * Field length on DB: 6
     * tlgnc_railway_station.railway_station_no referenced by tlgnc_order.actual_dep_railway_station_id -> tlgnc_railway_station ->tlgnc_railway_station_comm.comm_railway_station_id -> tlgnc_railway_station
     */
    public String getOActDepRsComStationNo() {
        return oActDepRsComStationNo;
    }

    /**
     * O_ACT_DEP_RS_COM_STATION_NAME is nullable
     * Field length on DB: 50
     * tlgnc_railway_station.railway_station_name referenced by tlgnc_order.actual_dep_railway_station_id -> tlgnc_railway_station ->tlgnc_railway_station_comm.comm_railway_station_id -> tlgnc_railway_station
     */
    public void setOActDepRsComStationName(String oActDepRsComStationName) {
        this.oActDepRsComStationName = oActDepRsComStationName;
    }

    /**
     * O_ACT_DEP_RS_COM_STATION_NAME is nullable
     * Field length on DB: 50
     * tlgnc_railway_station.railway_station_name referenced by tlgnc_order.actual_dep_railway_station_id -> tlgnc_railway_station ->tlgnc_railway_station_comm.comm_railway_station_id -> tlgnc_railway_station
     */
    public String getOActDepRsComStationName() {
        return oActDepRsComStationName;
    }

    /**
     * O_ORDER_CREATION_TYPE_CD is NOT nullable
     * Field length on DB: 2
     * tlgnc_order.order_creation_type_cd
     */
    public void setOOrderCreationTypeCd(Long oOrderCreationTypeCd) {
        this.oOrderCreationTypeCd = oOrderCreationTypeCd;
    }

    /**
     * O_ORDER_CREATION_TYPE_CD is NOT nullable
     * Field length on DB: 2
     * tlgnc_order.order_creation_type_cd
     */
    public Long getOOrderCreationTypeCd() {
        return oOrderCreationTypeCd;
    }

    /**
     * O_DELIVERY_TYPE_CD is NOT nullable
     * Field length on DB: 2
     * tlgnc_order.delivery_type_cd
     */
    public void setODeliveryTypeCd(Long oDeliveryTypeCd) {
        this.oDeliveryTypeCd = oDeliveryTypeCd;
    }

    /**
     * O_DELIVERY_TYPE_CD is NOT nullable
     * Field length on DB: 2
     * tlgnc_order.delivery_type_cd
     */
    public Long getODeliveryTypeCd() {
        return oDeliveryTypeCd;
    }

    /**
     * O_DISTRIBUTION_DEST_CD is NOT nullable
     * Field length on DB: 4
     * tlgnc_order.distribution_dest_cd
     */
    public void setODistributionDestCd(String oDistributionDestCd) {
        this.oDistributionDestCd = oDistributionDestCd;
    }

    /**
     * O_DISTRIBUTION_DEST_CD is NOT nullable
     * Field length on DB: 4
     * tlgnc_order.distribution_dest_cd
     */
    public String getODistributionDestCd() {
        return oDistributionDestCd;
    }

    /**
     * O_CALC_START_LOAD_FROM_DAT is nullable
     * 
     * o_calc_start_load_from_dat : o_calc_end_load_from_dat - o_plan_loading_time_min: Latest start load to arrive at begin of delivery window
     */
    public void setOCalcStartLoadFromDat(Date oCalcStartLoadFromDat) {
        this.oCalcStartLoadFromDat = oCalcStartLoadFromDat;
    }

    /**
     * O_CALC_START_LOAD_FROM_DAT is nullable
     * 
     * o_calc_start_load_from_dat : o_calc_end_load_from_dat - o_plan_loading_time_min: Latest start load to arrive at begin of delivery window
     */
    public Date getOCalcStartLoadFromDat() {
        return oCalcStartLoadFromDat;
    }

    /**
     * O_CALC_START_LOAD_TO_DAT is nullable
     * 
     * o_calc_end_load_to_dat - o_plan_loading_time_min: Latest start load to arrive at end of delivery window
     */
    public void setOCalcStartLoadToDat(Date oCalcStartLoadToDat) {
        this.oCalcStartLoadToDat = oCalcStartLoadToDat;
    }

    /**
     * O_CALC_START_LOAD_TO_DAT is nullable
     * 
     * o_calc_end_load_to_dat - o_plan_loading_time_min: Latest start load to arrive at end of delivery window
     */
    public Date getOCalcStartLoadToDat() {
        return oCalcStartLoadToDat;
    }

    /**
     * O_PLAN_LOADING_TIME_MIN is nullable
     * Field length on DB: 10
     * if 1 item only and lp.loading_time_min not null: take that. otherwise, app.default.loadingpoint.LoadingTime_min
     */
    public void setOPlanLoadingTimeMin(Long oPlanLoadingTimeMin) {
        this.oPlanLoadingTimeMin = oPlanLoadingTimeMin;
    }

    /**
     * O_PLAN_LOADING_TIME_MIN is nullable
     * Field length on DB: 10
     * if 1 item only and lp.loading_time_min not null: take that. otherwise, app.default.loadingpoint.LoadingTime_min
     */
    public Long getOPlanLoadingTimeMin() {
        return oPlanLoadingTimeMin;
    }

    /**
     * O_CALC_END_LOAD_FROM_DAT is nullable
     * 
     * o_delivery_from_dat - o_transport_duration_plan_min
     */
    public void setOCalcEndLoadFromDat(Date oCalcEndLoadFromDat) {
        this.oCalcEndLoadFromDat = oCalcEndLoadFromDat;
    }

    /**
     * O_CALC_END_LOAD_FROM_DAT is nullable
     * 
     * o_delivery_from_dat - o_transport_duration_plan_min
     */
    public Date getOCalcEndLoadFromDat() {
        return oCalcEndLoadFromDat;
    }

    /**
     * O_CALC_END_LOAD_TO_DAT is nullable
     * 
     * o_delivery_to_dat - o_transport_duration_plan_min
     */
    public void setOCalcEndLoadToDat(Date oCalcEndLoadToDat) {
        this.oCalcEndLoadToDat = oCalcEndLoadToDat;
    }

    /**
     * O_CALC_END_LOAD_TO_DAT is nullable
     * 
     * o_delivery_to_dat - o_transport_duration_plan_min
     */
    public Date getOCalcEndLoadToDat() {
        return oCalcEndLoadToDat;
    }

    /**
     * O_ACTUAL_START_LOAD_DAT is nullable
     * 
     * Bizevent loading.
     */
    public void setOActualStartLoadDat(Date oActualStartLoadDat) {
        this.oActualStartLoadDat = oActualStartLoadDat;
    }

    /**
     * O_ACTUAL_START_LOAD_DAT is nullable
     * 
     * Bizevent loading.
     */
    public Date getOActualStartLoadDat() {
        return oActualStartLoadDat;
    }

    /**
     * O_ACTUAL_END_LOAD_DAT is nullable
     * 
     * get from tlgnc_load.LOADING_TS
     */
    public void setOActualEndLoadDat(Date oActualEndLoadDat) {
        this.oActualEndLoadDat = oActualEndLoadDat;
    }

    /**
     * O_ACTUAL_END_LOAD_DAT is nullable
     * 
     * get from tlgnc_load.LOADING_TS
     */
    public Date getOActualEndLoadDat() {
        return oActualEndLoadDat;
    }

    /**
     * O_CALC_LATE_START_LOAD_DAT is nullable
     * 
     * true if o_actual_start_load_dat > o_calc_start_load_to_dat
     */
    public void setOCalcLateStartLoadDat(Boolean oCalcLateStartLoadDat) {
        this.oCalcLateStartLoadDat = oCalcLateStartLoadDat;
    }

    /**
     * O_CALC_LATE_START_LOAD_DAT is nullable
     * 
     * true if o_actual_start_load_dat > o_calc_start_load_to_dat
     */
    public Boolean getOCalcLateStartLoadDat() {
        return oCalcLateStartLoadDat;
    }

    /**
     * O_CALC_LATE_END_LOAD_DAT is nullable
     * 
     * true if o_actual_end_load_dat > o_calc_end_load_to_dat
     */
    public void setOCalcLateEndLoadDat(Boolean oCalcLateEndLoadDat) {
        this.oCalcLateEndLoadDat = oCalcLateEndLoadDat;
    }

    /**
     * O_CALC_LATE_END_LOAD_DAT is nullable
     * 
     * true if o_actual_end_load_dat > o_calc_end_load_to_dat
     */
    public Boolean getOCalcLateEndLoadDat() {
        return oCalcLateEndLoadDat;
    }

    /**
     * O_DELIVERY_FROM_DAT is NOT nullable
     * 
     * tlgnc_order.delivery_from_dat
     */
    public void setODeliveryFromDat(Date oDeliveryFromDat) {
        this.oDeliveryFromDat = oDeliveryFromDat;
    }

    /**
     * O_DELIVERY_FROM_DAT is NOT nullable
     * 
     * tlgnc_order.delivery_from_dat
     */
    public Date getODeliveryFromDat() {
        return oDeliveryFromDat;
    }

    /**
     * O_DELIVERY_TO_DAT is NOT nullable
     * 
     * tlgnc_order.delivery_to_dat
     */
    public void setODeliveryToDat(Date oDeliveryToDat) {
        this.oDeliveryToDat = oDeliveryToDat;
    }

    /**
     * O_DELIVERY_TO_DAT is NOT nullable
     * 
     * tlgnc_order.delivery_to_dat
     */
    public Date getODeliveryToDat() {
        return oDeliveryToDat;
    }

    /**
     * O_DELIVERY_NET_TIME_WINDOW is nullable
     * Field length on DB: 10
     * The delivery net time is in fact the time what crh had to deliver the product. This does not include weekends, and non working hours (non working hours are those, which are not in the time interval defined by open1 and open2 of the shipto.
     */
    public void setODeliveryNetTimeWindow(Long oDeliveryNetTimeWindow) {
        this.oDeliveryNetTimeWindow = oDeliveryNetTimeWindow;
    }

    /**
     * O_DELIVERY_NET_TIME_WINDOW is nullable
     * Field length on DB: 10
     * The delivery net time is in fact the time what crh had to deliver the product. This does not include weekends, and non working hours (non working hours are those, which are not in the time interval defined by open1 and open2 of the shipto.
     */
    public Long getODeliveryNetTimeWindow() {
        return oDeliveryNetTimeWindow;
    }

    /**
     * O_ACTUAL_DELIVERY_DAT is nullable
     * 
     * tlgnc_order.actual_delivery_dat
     */
    public void setOActualDeliveryDat(Date oActualDeliveryDat) {
        this.oActualDeliveryDat = oActualDeliveryDat;
    }

    /**
     * O_ACTUAL_DELIVERY_DAT is nullable
     * 
     * tlgnc_order.actual_delivery_dat
     */
    public Date getOActualDeliveryDat() {
        return oActualDeliveryDat;
    }

    /**
     * O_CALC_LATE_DELIVERY_FLAG is nullable
     * 
     * true, if ACTUAL_DELIVERY after O_DELIVERY_TO_DAT
     */
    public void setOCalcLateDeliveryFlag(Boolean oCalcLateDeliveryFlag) {
        this.oCalcLateDeliveryFlag = oCalcLateDeliveryFlag;
    }

    /**
     * O_CALC_LATE_DELIVERY_FLAG is nullable
     * 
     * true, if ACTUAL_DELIVERY after O_DELIVERY_TO_DAT
     */
    public Boolean getOCalcLateDeliveryFlag() {
        return oCalcLateDeliveryFlag;
    }

    /**
     * O_TRANSP_DUR_PLAN_GOOGLE_MIN is nullable
     * Field length on DB: 5
     * tlgnc_order.transport_duration_min
     */
    public void setOTranspDurPlanGoogleMin(Long oTranspDurPlanGoogleMin) {
        this.oTranspDurPlanGoogleMin = oTranspDurPlanGoogleMin;
    }

    /**
     * O_TRANSP_DUR_PLAN_GOOGLE_MIN is nullable
     * Field length on DB: 5
     * tlgnc_order.transport_duration_min
     */
    public Long getOTranspDurPlanGoogleMin() {
        return oTranspDurPlanGoogleMin;
    }

    /**
     * O_TRANSP_DUR_PLAN_MIN is nullable
     * Field length on DB: 5
     * tlgnc_order.transport_duration_min
     */
    public void setOTranspDurPlanMin(Long oTranspDurPlanMin) {
        this.oTranspDurPlanMin = oTranspDurPlanMin;
    }

    /**
     * O_TRANSP_DUR_PLAN_MIN is nullable
     * Field length on DB: 5
     * tlgnc_order.transport_duration_min
     */
    public Long getOTranspDurPlanMin() {
        return oTranspDurPlanMin;
    }

    /**
     * O_TRANSP_DISTANCE_PLAN_M is nullable
     * Field length on DB: 10
     * tlgnc_order.transport_distance_m
     */
    public void setOTranspDistancePlanM(Long oTranspDistancePlanM) {
        this.oTranspDistancePlanM = oTranspDistancePlanM;
    }

    /**
     * O_TRANSP_DISTANCE_PLAN_M is nullable
     * Field length on DB: 10
     * tlgnc_order.transport_distance_m
     */
    public Long getOTranspDistancePlanM() {
        return oTranspDistancePlanM;
    }

    /**
     * O_CUSTOMS_NOTIFICATION_NUMBER is nullable
     * Field length on DB: 4
     * tlgnc_order.customs_notification_number
     */
    public void setOCustomsNotificationNumber(Long oCustomsNotificationNumber) {
        this.oCustomsNotificationNumber = oCustomsNotificationNumber;
    }

    /**
     * O_CUSTOMS_NOTIFICATION_NUMBER is nullable
     * Field length on DB: 4
     * tlgnc_order.customs_notification_number
     */
    public Long getOCustomsNotificationNumber() {
        return oCustomsNotificationNumber;
    }

    /**
     * O_DELIVERY_TRSP_ZONE_CD is nullable
     * Field length on DB: 10
     * tlgnc_order.delivery_trsp_zone_cd
     */
    public void setODeliveryTrspZoneCd(String oDeliveryTrspZoneCd) {
        this.oDeliveryTrspZoneCd = oDeliveryTrspZoneCd;
    }

    /**
     * O_DELIVERY_TRSP_ZONE_CD is nullable
     * Field length on DB: 10
     * tlgnc_order.delivery_trsp_zone_cd
     */
    public String getODeliveryTrspZoneCd() {
        return oDeliveryTrspZoneCd;
    }

    /**
     * O_SPI_CD is nullable
     * Field length on DB: 10
     * tlgnc_order.spi_cd special process indicator code.
     */
    public void setOSpiCd(String oSpiCd) {
        this.oSpiCd = oSpiCd;
    }

    /**
     * O_SPI_CD is nullable
     * Field length on DB: 10
     * tlgnc_order.spi_cd special process indicator code.
     */
    public String getOSpiCd() {
        return oSpiCd;
    }

    /**
     * O_CONTACT_PERSON_TXT is nullable
     * Field length on DB: 80
     * tlgnc_order.contanct_person_txt
     */
    public void setOContactPersonTxt(String oContactPersonTxt) {
        this.oContactPersonTxt = oContactPersonTxt;
    }

    /**
     * O_CONTACT_PERSON_TXT is nullable
     * Field length on DB: 80
     * tlgnc_order.contanct_person_txt
     */
    public String getOContactPersonTxt() {
        return oContactPersonTxt;
    }

    /**
     * O_CONTACT_PERSON_SIGNATURE_TXT is nullable
     * Field length on DB: 40
     * tlgnc_order.contact_person_signature_txt
     */
    public void setOContactPersonSignatureTxt(String oContactPersonSignatureTxt) {
        this.oContactPersonSignatureTxt = oContactPersonSignatureTxt;
    }

    /**
     * O_CONTACT_PERSON_SIGNATURE_TXT is nullable
     * Field length on DB: 40
     * tlgnc_order.contact_person_signature_txt
     */
    public String getOContactPersonSignatureTxt() {
        return oContactPersonSignatureTxt;
    }

    /**
     * O_NO_PDA_OR_RAIL_EVENTS_FLAG is NOT nullable
     * 
     * tlgnc_order.no_pda_or_rail_events_flag
     */
    public void setONoPdaOrRailEventsFlag(Boolean oNoPdaOrRailEventsFlag) {
        this.oNoPdaOrRailEventsFlag = oNoPdaOrRailEventsFlag;
    }

    /**
     * O_NO_PDA_OR_RAIL_EVENTS_FLAG is NOT nullable
     * 
     * tlgnc_order.no_pda_or_rail_events_flag
     */
    public Boolean getONoPdaOrRailEventsFlag() {
        return oNoPdaOrRailEventsFlag;
    }

    /**
     * O_RELEASE_SAP_FLAG is NOT nullable
     * 
     * tlgnc_order.release_sap_flag
     */
    public void setOReleaseSapFlag(Boolean oReleaseSapFlag) {
        this.oReleaseSapFlag = oReleaseSapFlag;
    }

    /**
     * O_RELEASE_SAP_FLAG is NOT nullable
     * 
     * tlgnc_order.release_sap_flag
     */
    public Boolean getOReleaseSapFlag() {
        return oReleaseSapFlag;
    }

    /**
     * O_FREE_DELIVERY_FLAG is NOT nullable
     * 
     * tlgnc_order.free_delivery_flag
     */
    public void setOFreeDeliveryFlag(Boolean oFreeDeliveryFlag) {
        this.oFreeDeliveryFlag = oFreeDeliveryFlag;
    }

    /**
     * O_FREE_DELIVERY_FLAG is NOT nullable
     * 
     * tlgnc_order.free_delivery_flag
     */
    public Boolean getOFreeDeliveryFlag() {
        return oFreeDeliveryFlag;
    }

    /**
     * O_SAP_SUPPRESS_MIN_CHARGE_FLAG is nullable
     * 
     * tlgnc_order.o_sap_suppress_min_charge_flag
     */
    public void setOSapSuppressMinChargeFlag(Boolean oSapSuppressMinChargeFlag) {
        this.oSapSuppressMinChargeFlag = oSapSuppressMinChargeFlag;
    }

    /**
     * O_SAP_SUPPRESS_MIN_CHARGE_FLAG is nullable
     * 
     * tlgnc_order.o_sap_suppress_min_charge_flag
     */
    public Boolean getOSapSuppressMinChargeFlag() {
        return oSapSuppressMinChargeFlag;
    }

    /**
     * O_ORDER_CONF_EMAIL_FLAG is NOT nullable
     * 
     * tlgnc_order.order_conf_email_flag
     */
    public void setOOrderConfEmailFlag(Boolean oOrderConfEmailFlag) {
        this.oOrderConfEmailFlag = oOrderConfEmailFlag;
    }

    /**
     * O_ORDER_CONF_EMAIL_FLAG is NOT nullable
     * 
     * tlgnc_order.order_conf_email_flag
     */
    public Boolean getOOrderConfEmailFlag() {
        return oOrderConfEmailFlag;
    }

    /**
     * O_ORDER_CONF_EMAIL_ADRESSES is nullable
     * Field length on DB: 200
     * tlgnc_order.order_conf_email_adresses
     */
    public void setOOrderConfEmailAdresses(String oOrderConfEmailAdresses) {
        this.oOrderConfEmailAdresses = oOrderConfEmailAdresses;
    }

    /**
     * O_ORDER_CONF_EMAIL_ADRESSES is nullable
     * Field length on DB: 200
     * tlgnc_order.order_conf_email_adresses
     */
    public String getOOrderConfEmailAdresses() {
        return oOrderConfEmailAdresses;
    }

    /**
     * O_ORDER_CONF_FAX_FLAG is NOT nullable
     * 
     * tlgnc_order.order_conf_fax_flag
     */
    public void setOOrderConfFaxFlag(Boolean oOrderConfFaxFlag) {
        this.oOrderConfFaxFlag = oOrderConfFaxFlag;
    }

    /**
     * O_ORDER_CONF_FAX_FLAG is NOT nullable
     * 
     * tlgnc_order.order_conf_fax_flag
     */
    public Boolean getOOrderConfFaxFlag() {
        return oOrderConfFaxFlag;
    }

    /**
     * O_ORDER_CONF_FAX_NUMBER is nullable
     * Field length on DB: 80
     * tlgnc_order.order_conf_fax_number
     */
    public void setOOrderConfFaxNumber(String oOrderConfFaxNumber) {
        this.oOrderConfFaxNumber = oOrderConfFaxNumber;
    }

    /**
     * O_ORDER_CONF_FAX_NUMBER is nullable
     * Field length on DB: 80
     * tlgnc_order.order_conf_fax_number
     */
    public String getOOrderConfFaxNumber() {
        return oOrderConfFaxNumber;
    }

    /**
     * O_ORDER_CONF_SMS_FLAG is NOT nullable
     * 
     * tlgnc_order.order_conf_sms_flag
     */
    public void setOOrderConfSmsFlag(Boolean oOrderConfSmsFlag) {
        this.oOrderConfSmsFlag = oOrderConfSmsFlag;
    }

    /**
     * O_ORDER_CONF_SMS_FLAG is NOT nullable
     * 
     * tlgnc_order.order_conf_sms_flag
     */
    public Boolean getOOrderConfSmsFlag() {
        return oOrderConfSmsFlag;
    }

    /**
     * O_ORDER_CONF_SMS_NUMBER is nullable
     * Field length on DB: 80
     * tlgnc_order.order_conf_sms_number
     */
    public void setOOrderConfSmsNumber(String oOrderConfSmsNumber) {
        this.oOrderConfSmsNumber = oOrderConfSmsNumber;
    }

    /**
     * O_ORDER_CONF_SMS_NUMBER is nullable
     * Field length on DB: 80
     * tlgnc_order.order_conf_sms_number
     */
    public String getOOrderConfSmsNumber() {
        return oOrderConfSmsNumber;
    }

    /**
     * O_DELIVNOTE_EMAIL_FLAG is NOT nullable
     * 
     * tlgnc_order.delivnote_email_flag
     */
    public void setODelivnoteEmailFlag(Boolean oDelivnoteEmailFlag) {
        this.oDelivnoteEmailFlag = oDelivnoteEmailFlag;
    }

    /**
     * O_DELIVNOTE_EMAIL_FLAG is NOT nullable
     * 
     * tlgnc_order.delivnote_email_flag
     */
    public Boolean getODelivnoteEmailFlag() {
        return oDelivnoteEmailFlag;
    }

    /**
     * O_DELIVNOTE_FAX_FLAG is NOT nullable
     * 
     * tlgnc_order.delivnote_fax_flag
     */
    public void setODelivnoteFaxFlag(Boolean oDelivnoteFaxFlag) {
        this.oDelivnoteFaxFlag = oDelivnoteFaxFlag;
    }

    /**
     * O_DELIVNOTE_FAX_FLAG is NOT nullable
     * 
     * tlgnc_order.delivnote_fax_flag
     */
    public Boolean getODelivnoteFaxFlag() {
        return oDelivnoteFaxFlag;
    }

    /**
     * O_DELIVNOTE_SINGLE_PAPER_FLAG is NOT nullable
     * 
     * tlgnc_order.delivnote_single_paper_flag
     */
    public void setODelivnoteSinglePaperFlag(Boolean oDelivnoteSinglePaperFlag) {
        this.oDelivnoteSinglePaperFlag = oDelivnoteSinglePaperFlag;
    }

    /**
     * O_DELIVNOTE_SINGLE_PAPER_FLAG is NOT nullable
     * 
     * tlgnc_order.delivnote_single_paper_flag
     */
    public Boolean getODelivnoteSinglePaperFlag() {
        return oDelivnoteSinglePaperFlag;
    }

    /**
     * O_CUST_WEBORDER_NOTE is nullable
     * Field length on DB: 1000
     * tlgnc_order.cust_weborder_note
     */
    public void setOCustWeborderNote(String oCustWeborderNote) {
        this.oCustWeborderNote = oCustWeborderNote;
    }

    /**
     * O_CUST_WEBORDER_NOTE is nullable
     * Field length on DB: 1000
     * tlgnc_order.cust_weborder_note
     */
    public String getOCustWeborderNote() {
        return oCustWeborderNote;
    }

    /**
     * O_NOTE_ORDER_MGMT is nullable
     * Field length on DB: 256
     * tlgnc_order.note_order_mgmt
     */
    public void setONoteOrderMgmt(String oNoteOrderMgmt) {
        this.oNoteOrderMgmt = oNoteOrderMgmt;
    }

    /**
     * O_NOTE_ORDER_MGMT is nullable
     * Field length on DB: 256
     * tlgnc_order.note_order_mgmt
     */
    public String getONoteOrderMgmt() {
        return oNoteOrderMgmt;
    }

    /**
     * O_NOTE_DELIVERY is nullable
     * Field length on DB: 300
     * tlgnc_order.note_delivery
     */
    public void setONoteDelivery(String oNoteDelivery) {
        this.oNoteDelivery = oNoteDelivery;
    }

    /**
     * O_NOTE_DELIVERY is nullable
     * Field length on DB: 300
     * tlgnc_order.note_delivery
     */
    public String getONoteDelivery() {
        return oNoteDelivery;
    }

    /**
     * O_NOTE_DISPATCHING is nullable
     * Field length on DB: 256
     * tlgnc_order.note_dispatching
     */
    public void setONoteDispatching(String oNoteDispatching) {
        this.oNoteDispatching = oNoteDispatching;
    }

    /**
     * O_NOTE_DISPATCHING is nullable
     * Field length on DB: 256
     * tlgnc_order.note_dispatching
     */
    public String getONoteDispatching() {
        return oNoteDispatching;
    }

    /**
     * O_NOTE_TRUCK_DRIVER is nullable
     * Field length on DB: 256
     * tlgnc_order.note_truck_driver
     */
    public void setONoteTruckDriver(String oNoteTruckDriver) {
        this.oNoteTruckDriver = oNoteTruckDriver;
    }

    /**
     * O_NOTE_TRUCK_DRIVER is nullable
     * Field length on DB: 256
     * tlgnc_order.note_truck_driver
     */
    public String getONoteTruckDriver() {
        return oNoteTruckDriver;
    }

    /**
     * O_NOTE_RAILWAY_DISP is nullable
     * Field length on DB: 256
     * tlgnc_order.note_railway_disp
     */
    public void setONoteRailwayDisp(String oNoteRailwayDisp) {
        this.oNoteRailwayDisp = oNoteRailwayDisp;
    }

    /**
     * O_NOTE_RAILWAY_DISP is nullable
     * Field length on DB: 256
     * tlgnc_order.note_railway_disp
     */
    public String getONoteRailwayDisp() {
        return oNoteRailwayDisp;
    }

    /**
     * O_CARRIER_DELIVERY_NOTE is nullable
     * Field length on DB: 600
     * tlgnc_order.carrier_delivery_note
     */
    public void setOCarrierDeliveryNote(String oCarrierDeliveryNote) {
        this.oCarrierDeliveryNote = oCarrierDeliveryNote;
    }

    /**
     * O_CARRIER_DELIVERY_NOTE is nullable
     * Field length on DB: 600
     * tlgnc_order.carrier_delivery_note
     */
    public String getOCarrierDeliveryNote() {
        return oCarrierDeliveryNote;
    }

    /**
     * O_CARRIER_TRUCK_DRIVER_NOTE is nullable
     * Field length on DB: 256
     * tlgnc_order.carrier_truck_driver_note
     */
    public void setOCarrierTruckDriverNote(String oCarrierTruckDriverNote) {
        this.oCarrierTruckDriverNote = oCarrierTruckDriverNote;
    }

    /**
     * O_CARRIER_TRUCK_DRIVER_NOTE is nullable
     * Field length on DB: 256
     * tlgnc_order.carrier_truck_driver_note
     */
    public String getOCarrierTruckDriverNote() {
        return oCarrierTruckDriverNote;
    }

    /**
     * O_CARRIER_DISPATCHING_NOTE is nullable
     * Field length on DB: 256
     * tlgnc_order.carrier_dispatching_note
     */
    public void setOCarrierDispatchingNote(String oCarrierDispatchingNote) {
        this.oCarrierDispatchingNote = oCarrierDispatchingNote;
    }

    /**
     * O_CARRIER_DISPATCHING_NOTE is nullable
     * Field length on DB: 256
     * tlgnc_order.carrier_dispatching_note
     */
    public String getOCarrierDispatchingNote() {
        return oCarrierDispatchingNote;
    }

    /**
     * O_CUST_DELIVERY_NOTE_COMMENT is nullable
     * Field length on DB: 600
     * tlgnc_order.cust_delivery_note_comment
     */
    public void setOCustDeliveryNoteComment(String oCustDeliveryNoteComment) {
        this.oCustDeliveryNoteComment = oCustDeliveryNoteComment;
    }

    /**
     * O_CUST_DELIVERY_NOTE_COMMENT is nullable
     * Field length on DB: 600
     * tlgnc_order.cust_delivery_note_comment
     */
    public String getOCustDeliveryNoteComment() {
        return oCustDeliveryNoteComment;
    }

    /**
     * O_DELIVERED_TRUCK_ID is nullable
     * Field length on DB: 12
     * tlgnc_order.delivered_truck_id
     */
    public void setODeliveredTruckId(Long oDeliveredTruckId) {
        this.oDeliveredTruckId = oDeliveredTruckId;
    }

    /**
     * O_DELIVERED_TRUCK_ID is nullable
     * Field length on DB: 12
     * tlgnc_order.delivered_truck_id
     */
    public Long getODeliveredTruckId() {
        return oDeliveredTruckId;
    }
    
    /**
	 * @return the actualDrivingTimeMin
	 */
	public Long getActualDrivingTimeMin() {
		return actualDrivingTimeMin;
	}

	/**
	 * @param actualDrivingTimeMin the actualDrivingTimeMin to set
	 */
	public void setActualDrivingTimeMin(Long actualDrivingTimeMin) {
		this.actualDrivingTimeMin = actualDrivingTimeMin;
	}

    /**
     * TRUCK_NAME is nullable
     * Field length on DB: 30
     * tlgnc_truck.truck_name, referenced by tlgnc_order.delivered_truck_id -> tlgnc_truck
     */
    public void setTruckName(String truckName) {
        this.truckName = truckName;
    }

    /**
     * TRUCK_NAME is nullable
     * Field length on DB: 30
     * tlgnc_truck.truck_name, referenced by tlgnc_order.delivered_truck_id -> tlgnc_truck
     */
    public String getTruckName() {
        return truckName;
    }

    /**
     * TRUCK_PLATE_NO is nullable
     * Field length on DB: 20
     * tlgnc_truck.truck_plate_no, referenced by tlgnc_order.delivered_truck_id -> tlgnc_truck
     */
    public void setTruckPlateNo(String truckPlateNo) {
        this.truckPlateNo = truckPlateNo;
    }

    /**
     * TRUCK_PLATE_NO is nullable
     * Field length on DB: 20
     * tlgnc_truck.truck_plate_no, referenced by tlgnc_order.delivered_truck_id -> tlgnc_truck
     */
    public String getTruckPlateNo() {
        return truckPlateNo;
    }

    /**
     * TRUCK_TRANSPORT_CAT_CD is nullable
     * Field length on DB: 2
     * tlgnc_truck.truck_transport_cat_cd, referenced by tlgnc_order.delivered_truck_id -> tlgnc_truck
     */
    public void setTruckTransportCatCd(Long truckTransportCatCd) {
        this.truckTransportCatCd = truckTransportCatCd;
    }

    /**
     * TRUCK_TRANSPORT_CAT_CD is nullable
     * Field length on DB: 2
     * tlgnc_truck.truck_transport_cat_cd, referenced by tlgnc_order.delivered_truck_id -> tlgnc_truck
     */
    public Long getTruckTransportCatCd() {
        return truckTransportCatCd;
    }

    /**
     * TRUCK_TYPE_CD is nullable
     * Field length on DB: 3
     * tlgnc_truck.truck_type_cd, referenced by tlgnc_order.delivered_truck_id -> tlgnc_truck
     */
    public void setTruckTypeCd(Long truckTypeCd) {
        this.truckTypeCd = truckTypeCd;
    }

    /**
     * TRUCK_TYPE_CD is nullable
     * Field length on DB: 3
     * tlgnc_truck.truck_type_cd, referenced by tlgnc_order.delivered_truck_id -> tlgnc_truck
     */
    public Long getTruckTypeCd() {
        return truckTypeCd;
    }

    /**
     * TRUCK_MAX_GROSS_WEIGHT_T is nullable
     * 
     * tlgnc_truck.max_gross_weight_t, referenced by tlgnc_order.delivered_truck_id -> tlgnc_truck
     */
    public void setTruckMaxGrossWeightT(java.math.BigDecimal truckMaxGrossWeightT) {
        this.truckMaxGrossWeightT = truckMaxGrossWeightT;
    }

    /**
     * TRUCK_MAX_GROSS_WEIGHT_T is nullable
     * 
     * tlgnc_truck.max_gross_weight_t, referenced by tlgnc_order.delivered_truck_id -> tlgnc_truck
     */
    public java.math.BigDecimal getTruckMaxGrossWeightT() {
        return truckMaxGrossWeightT;
    }

    /**
     * TRUCK_TARE_WEIGHT_T is nullable
     * 
     * tlgnc_truck.tare_weight_t, referenced by tlgnc_order.delivered_truck_id -> tlgnc_truck
     */
    public void setTruckTareWeightT(java.math.BigDecimal truckTareWeightT) {
        this.truckTareWeightT = truckTareWeightT;
    }

    /**
     * TRUCK_TARE_WEIGHT_T is nullable
     * 
     * tlgnc_truck.tare_weight_t, referenced by tlgnc_order.delivered_truck_id -> tlgnc_truck
     */
    public java.math.BigDecimal getTruckTareWeightT() {
        return truckTareWeightT;
    }

    /**
     * TRUCK_NR_CHAMBERS is nullable
     * Field length on DB: 1
     * tlgnc_truck.nr_chambers, referenced by tlgnc_order.delivered_truck_id -> tlgnc_truck
     */
    public void setTruckNrChambers(Long truckNrChambers) {
        this.truckNrChambers = truckNrChambers;
    }

    /**
     * TRUCK_NR_CHAMBERS is nullable
     * Field length on DB: 1
     * tlgnc_truck.nr_chambers, referenced by tlgnc_order.delivered_truck_id -> tlgnc_truck
     */
    public Long getTruckNrChambers() {
        return truckNrChambers;
    }

    /**
     * TRUCK_MAX_NET_W_CHAMBER1_T is nullable
     * 
     * tlgnc_truck.max_net_weight_chamber1_t, referenced by tlgnc_order.delivered_truck_id -> tlgnc_truck
     */
    public void setTruckMaxNetWChamber1T(java.math.BigDecimal truckMaxNetWChamber1T) {
        this.truckMaxNetWChamber1T = truckMaxNetWChamber1T;
    }

    /**
     * TRUCK_MAX_NET_W_CHAMBER1_T is nullable
     * 
     * tlgnc_truck.max_net_weight_chamber1_t, referenced by tlgnc_order.delivered_truck_id -> tlgnc_truck
     */
    public java.math.BigDecimal getTruckMaxNetWChamber1T() {
        return truckMaxNetWChamber1T;
    }

    /**
     * TRUCK_MAX_NET_W_CHAMBER2_T is nullable
     * 
     * tlgnc_truck.max_net_weight_chamber2_t, referenced by tlgnc_order.delivered_truck_id -> tlgnc_truck
     */
    public void setTruckMaxNetWChamber2T(java.math.BigDecimal truckMaxNetWChamber2T) {
        this.truckMaxNetWChamber2T = truckMaxNetWChamber2T;
    }

    /**
     * TRUCK_MAX_NET_W_CHAMBER2_T is nullable
     * 
     * tlgnc_truck.max_net_weight_chamber2_t, referenced by tlgnc_order.delivered_truck_id -> tlgnc_truck
     */
    public java.math.BigDecimal getTruckMaxNetWChamber2T() {
        return truckMaxNetWChamber2T;
    }

    /**
     * TRUCK_SAP_INTERNAL_FLAG is nullable
     * 
     * tlgnc_truck.sap_vehicle_is_internal_flag, referenced by tlgnc_order.delivered_truck_id -> tlgnc_truck
     */
    public void setTruckSapInternalFlag(Boolean truckSapInternalFlag) {
        this.truckSapInternalFlag = truckSapInternalFlag;
    }

    /**
     * TRUCK_SAP_INTERNAL_FLAG is nullable
     * 
     * tlgnc_truck.sap_vehicle_is_internal_flag, referenced by tlgnc_order.delivered_truck_id -> tlgnc_truck
     */
    public Boolean getTruckSapInternalFlag() {
        return truckSapInternalFlag;
    }

    /**
     * O_DELIVERED_WAGON_ID is nullable
     * Field length on DB: 12
     * tlgnc_order.delivered_wagon_id
     */
    public void setODeliveredWagonId(Long oDeliveredWagonId) {
        this.oDeliveredWagonId = oDeliveredWagonId;
    }

    /**
     * O_DELIVERED_WAGON_ID is nullable
     * Field length on DB: 12
     * tlgnc_order.delivered_wagon_id
     */
    public Long getODeliveredWagonId() {
        return oDeliveredWagonId;
    }

    /**
     * WAGON_NO is nullable
     * Field length on DB: 12
     * tlgnc_wagon.wagon_no, referenced by tlgnc_order.delivered_wagon_id -> tlgnc_wagon
     */
    public void setWagonNo(String wagonNo) {
        this.wagonNo = wagonNo;
    }

    /**
     * WAGON_NO is nullable
     * Field length on DB: 12
     * tlgnc_wagon.wagon_no, referenced by tlgnc_order.delivered_wagon_id -> tlgnc_wagon
     */
    public String getWagonNo() {
        return wagonNo;
    }

    /**
     * WAGON_TYPE_CD is nullable
     * Field length on DB: 2
     * tlgnc_wagon.wagon_type_cd, referenced by tlgnc_order.delivered_wagon_id -> tlgnc_wagon
     */
    public void setWagonTypeCd(Long wagonTypeCd) {
        this.wagonTypeCd = wagonTypeCd;
    }

    /**
     * WAGON_TYPE_CD is nullable
     * Field length on DB: 2
     * tlgnc_wagon.wagon_type_cd, referenced by tlgnc_order.delivered_wagon_id -> tlgnc_wagon
     */
    public Long getWagonTypeCd() {
        return wagonTypeCd;
    }

    /**
     * WAGON_TYPE_NAME is nullable
     * Field length on DB: 20
     * tlgnr_wagon_type_bs.wagon_type_name, referenced by tlgnc_order.delivered_wagon_id -> tlgnc_wagon.wagon_type_cd -> tlgnr_wagon_type_bs
     */
    public void setWagonTypeName(String wagonTypeName) {
        this.wagonTypeName = wagonTypeName;
    }

    /**
     * WAGON_TYPE_NAME is nullable
     * Field length on DB: 20
     * tlgnr_wagon_type_bs.wagon_type_name, referenced by tlgnc_order.delivered_wagon_id -> tlgnc_wagon.wagon_type_cd -> tlgnr_wagon_type_bs
     */
    public String getWagonTypeName() {
        return wagonTypeName;
    }

    /**
     * WAGON_UIC_CLASS_CD is nullable
     * Field length on DB: 10
     * tlgnr_wagon_type_bs.wagon_uic_class_cd, referenced by tlgnc_order.delivered_wagon_id -> tlgnc_wagon.wagon_type_cd -> tlgnr_wagon_type_bs
     */
    public void setWagonUicClassCd(String wagonUicClassCd) {
        this.wagonUicClassCd = wagonUicClassCd;
    }

    /**
     * WAGON_UIC_CLASS_CD is nullable
     * Field length on DB: 10
     * tlgnr_wagon_type_bs.wagon_uic_class_cd, referenced by tlgnc_order.delivered_wagon_id -> tlgnc_wagon.wagon_type_cd -> tlgnr_wagon_type_bs
     */
    public String getWagonUicClassCd() {
        return wagonUicClassCd;
    }

    /**
     * WAGON_AXIS_COUNT is nullable
     * Field length on DB: 2
     * tlgnr_wagon_type_bs.axis_count, referenced by tlgnc_order.delivered_wagon_id -> tlgnc_wagon.wagon_type_cd -> tlgnr_wagon_type_bs
     */
    public void setWagonAxisCount(Long wagonAxisCount) {
        this.wagonAxisCount = wagonAxisCount;
    }

    /**
     * WAGON_AXIS_COUNT is nullable
     * Field length on DB: 2
     * tlgnr_wagon_type_bs.axis_count, referenced by tlgnc_order.delivered_wagon_id -> tlgnc_wagon.wagon_type_cd -> tlgnr_wagon_type_bs
     */
    public Long getWagonAxisCount() {
        return wagonAxisCount;
    }

    /**
     * WAGON_TRSP_CAT_CD is nullable
     * Field length on DB: 2
     * tlgnr_wagon_type_bs.wagon_trsp_cat_cd, referenced by tlgnc_order.delivered_wagon_id -> tlgnc_wagon.wagon_type_cd -> tlgnr_wagon_type_bs
     */
    public void setWagonTrspCatCd(Long wagonTrspCatCd) {
        this.wagonTrspCatCd = wagonTrspCatCd;
    }

    /**
     * WAGON_TRSP_CAT_CD is nullable
     * Field length on DB: 2
     * tlgnr_wagon_type_bs.wagon_trsp_cat_cd, referenced by tlgnc_order.delivered_wagon_id -> tlgnc_wagon.wagon_type_cd -> tlgnr_wagon_type_bs
     */
    public Long getWagonTrspCatCd() {
        return wagonTrspCatCd;
    }

    /**
     * WAGON_LIMIT_CREDIT_NOTE_T is nullable
     * 
     * tlgnr_wagon_type_bs.limit_credit_note_t, referenced by tlgnc_order.delivered_wagon_id -> tlgnc_wagon.wagon_type_cd -> tlgnr_wagon_type_bs
     */
    public void setWagonLimitCreditNoteT(java.math.BigDecimal wagonLimitCreditNoteT) {
        this.wagonLimitCreditNoteT = wagonLimitCreditNoteT;
    }

    /**
     * WAGON_LIMIT_CREDIT_NOTE_T is nullable
     * 
     * tlgnr_wagon_type_bs.limit_credit_note_t, referenced by tlgnc_order.delivered_wagon_id -> tlgnc_wagon.wagon_type_cd -> tlgnr_wagon_type_bs
     */
    public java.math.BigDecimal getWagonLimitCreditNoteT() {
        return wagonLimitCreditNoteT;
    }

    /**
     * WAGON_TARE_WEIGHT_T is nullable
     * 
     * tlgnc_wagon.tare_weight_t, referenced by tlgnc_order.delivered_wagon_id -> tlgnc_wagon
     */
    public void setWagonTareWeightT(java.math.BigDecimal wagonTareWeightT) {
        this.wagonTareWeightT = wagonTareWeightT;
    }

    /**
     * WAGON_TARE_WEIGHT_T is nullable
     * 
     * tlgnc_wagon.tare_weight_t, referenced by tlgnc_order.delivered_wagon_id -> tlgnc_wagon
     */
    public java.math.BigDecimal getWagonTareWeightT() {
        return wagonTareWeightT;
    }

    /**
     * WAGON_MAX_GROSS_WEIGHT_T is nullable
     * 
     * tlgnc_wagon.max_gross_weight_t, referenced by tlgnc_order.delivered_wagon_id -> tlgnc_wagon. If null, use tlgnr_wagon_type_bs.max_gross_weight_t, referenced by tlgnc_order.delivered_wagon_id -> tlgnc_wagon.wagon_type_cd -> tlgnr_wagon_type_bs
     */
    public void setWagonMaxGrossWeightT(java.math.BigDecimal wagonMaxGrossWeightT) {
        this.wagonMaxGrossWeightT = wagonMaxGrossWeightT;
    }

    /**
     * WAGON_MAX_GROSS_WEIGHT_T is nullable
     * 
     * tlgnc_wagon.max_gross_weight_t, referenced by tlgnc_order.delivered_wagon_id -> tlgnc_wagon. If null, use tlgnr_wagon_type_bs.max_gross_weight_t, referenced by tlgnc_order.delivered_wagon_id -> tlgnc_wagon.wagon_type_cd -> tlgnr_wagon_type_bs
     */
    public java.math.BigDecimal getWagonMaxGrossWeightT() {
        return wagonMaxGrossWeightT;
    }

    /**
     * WAGON_VOLUME_M3 is nullable
     * 
     * tlgnc_wagon.volume_m3, referenced by tlgnc_order.delivered_wagon_id -> tlgnc_wagon. If null, use tlgnr_wagon_type_bs.volume_m3, referenced by tlgnc_order.delivered_wagon_id -> tlgnc_wagon.wagon_type_cd -> tlgnr_wagon_type_bs
     */
    public void setWagonVolumeM3(java.math.BigDecimal wagonVolumeM3) {
        this.wagonVolumeM3 = wagonVolumeM3;
    }

    /**
     * WAGON_VOLUME_M3 is nullable
     * 
     * tlgnc_wagon.volume_m3, referenced by tlgnc_order.delivered_wagon_id -> tlgnc_wagon. If null, use tlgnr_wagon_type_bs.volume_m3, referenced by tlgnc_order.delivered_wagon_id -> tlgnc_wagon.wagon_type_cd -> tlgnr_wagon_type_bs
     */
    public java.math.BigDecimal getWagonVolumeM3() {
        return wagonVolumeM3;
    }

    /**
     * O_DELIVERED_SIMPLE_VEHICLE_ID is nullable
     * Field length on DB: 12
     * tlgnc_order.delivered_truck_id
     */
    public void setODeliveredSimpleVehicleId(Long oDeliveredSimpleVehicleId) {
        this.oDeliveredSimpleVehicleId = oDeliveredSimpleVehicleId;
    }

    /**
     * O_DELIVERED_SIMPLE_VEHICLE_ID is nullable
     * Field length on DB: 12
     * tlgnc_order.delivered_truck_id
     */
    public Long getODeliveredSimpleVehicleId() {
        return oDeliveredSimpleVehicleId;
    }

    /**
     * VEHICLE_NAME is nullable
     * Field length on DB: 30
     * tlgnc_simple_vehicle.vehicle_name, referenced by tlgnc_order.delivered_simple_vehicle_id -> tlgnc_simple_vehicle
     */
    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    /**
     * VEHICLE_NAME is nullable
     * Field length on DB: 30
     * tlgnc_simple_vehicle.vehicle_name, referenced by tlgnc_order.delivered_simple_vehicle_id -> tlgnc_simple_vehicle
     */
    public String getVehicleName() {
        return vehicleName;
    }

    /**
     * VEHICLE_REG_NO is nullable
     * Field length on DB: 20
     * tlgnc_simple_vehicle.truck_plate_no, referenced by tlgnc_order.delivered_simple_vehicle_id -> tlgnc_simple_vehicle
     */
    public void setVehicleRegNo(String vehicleRegNo) {
        this.vehicleRegNo = vehicleRegNo;
    }

    /**
     * VEHICLE_REG_NO is nullable
     * Field length on DB: 20
     * tlgnc_simple_vehicle.truck_plate_no, referenced by tlgnc_order.delivered_simple_vehicle_id -> tlgnc_simple_vehicle
     */
    public String getVehicleRegNo() {
        return vehicleRegNo;
    }

    /**
     * VEHICLE_TRANSPORT_CAT_CD is nullable
     * Field length on DB: 2
     * tlgnc_simple_vehicle.truck_transport_cat_cd, referenced by tlgnc_order.delivered_simple_vehicle_id -> tlgnc_simple_vehicle
     */
    public void setVehicleTransportCatCd(Long vehicleTransportCatCd) {
        this.vehicleTransportCatCd = vehicleTransportCatCd;
    }

    /**
     * VEHICLE_TRANSPORT_CAT_CD is nullable
     * Field length on DB: 2
     * tlgnc_simple_vehicle.truck_transport_cat_cd, referenced by tlgnc_order.delivered_simple_vehicle_id -> tlgnc_simple_vehicle
     */
    public Long getVehicleTransportCatCd() {
        return vehicleTransportCatCd;
    }

    /**
     * VEHICLE_TYPE_CD is nullable
     * Field length on DB: 2
     * tlgnc_simple_vehicle.vehicle_type_cd, referenced by tlgnc_order.delivered_simple_vehicle_id -> tlgnc_simple_vehicle
     */
    public void setVehicleTypeCd(Long vehicleTypeCd) {
        this.vehicleTypeCd = vehicleTypeCd;
    }

    /**
     * VEHICLE_TYPE_CD is nullable
     * Field length on DB: 2
     * tlgnc_simple_vehicle.vehicle_type_cd, referenced by tlgnc_order.delivered_simple_vehicle_id -> tlgnc_simple_vehicle
     */
    public Long getVehicleTypeCd() {
        return vehicleTypeCd;
    }

    /**
     * VEHICLE_MAX_GROSS_WEIGHT_T is nullable
     * 
     * tlgnc_simple_vehicle.max_gross_weight_t, referenced by tlgnc_order.delivered_simple_vehicle_id -> tlgnc_simple_vehicle
     */
    public void setVehicleMaxGrossWeightT(java.math.BigDecimal vehicleMaxGrossWeightT) {
        this.vehicleMaxGrossWeightT = vehicleMaxGrossWeightT;
    }

    /**
     * VEHICLE_MAX_GROSS_WEIGHT_T is nullable
     * 
     * tlgnc_simple_vehicle.max_gross_weight_t, referenced by tlgnc_order.delivered_simple_vehicle_id -> tlgnc_simple_vehicle
     */
    public java.math.BigDecimal getVehicleMaxGrossWeightT() {
        return vehicleMaxGrossWeightT;
    }

    /**
     * VEHICLE_TARE_WEIGHT_T is nullable
     * 
     * tlgnc_simple_vehicle.tare_weight_t, referenced by tlgnc_order.delivered_simple_vehicle_id -> tlgnc_simple_vehicle
     */
    public void setVehicleTareWeightT(java.math.BigDecimal vehicleTareWeightT) {
        this.vehicleTareWeightT = vehicleTareWeightT;
    }

    /**
     * VEHICLE_TARE_WEIGHT_T is nullable
     * 
     * tlgnc_simple_vehicle.tare_weight_t, referenced by tlgnc_order.delivered_simple_vehicle_id -> tlgnc_simple_vehicle
     */
    public java.math.BigDecimal getVehicleTareWeightT() {
        return vehicleTareWeightT;
    }

    /**
     * VEHICLE_NR_CHAMBERS is nullable
     * Field length on DB: 2
     * tlgnc_simple_vehicle.nr_chambers, referenced by tlgnc_order.delivered_simple_vehicle_id -> tlgnc_simple_vehicle
     */
    public void setVehicleNrChambers(Long vehicleNrChambers) {
        this.vehicleNrChambers = vehicleNrChambers;
    }

    /**
     * VEHICLE_NR_CHAMBERS is nullable
     * Field length on DB: 2
     * tlgnc_simple_vehicle.nr_chambers, referenced by tlgnc_order.delivered_simple_vehicle_id -> tlgnc_simple_vehicle
     */
    public Long getVehicleNrChambers() {
        return vehicleNrChambers;
    }

    /**
     * O_DELIVERED_DRIVER_ID is nullable
     * Field length on DB: 12
     * tlgnc_order.delivered_driver_id
     */
    public void setODeliveredDriverId(Long oDeliveredDriverId) {
        this.oDeliveredDriverId = oDeliveredDriverId;
    }

    /**
     * O_DELIVERED_DRIVER_ID is nullable
     * Field length on DB: 12
     * tlgnc_order.delivered_driver_id
     */
    public Long getODeliveredDriverId() {
        return oDeliveredDriverId;
    }

    /**
     * DRIVER_NAME is nullable
     * Field length on DB: 40
     * tlgnc_person.name, referenced by tlgnc_order.delivered_driver_id -> tlgnc_driver.ueser_id -> tlgnu_user.person_id -> tlgnc_person
     */
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    /**
     * DRIVER_NAME is nullable
     * Field length on DB: 40
     * tlgnc_person.name, referenced by tlgnc_order.delivered_driver_id -> tlgnc_driver.ueser_id -> tlgnu_user.person_id -> tlgnc_person
     */
    public String getDriverName() {
        return driverName;
    }

    /**
     * DRIVER_FIRSTNAME is nullable
     * Field length on DB: 40
     * tlgnc_person.firstname, referenced by tlgnc_order.delivered_driver_id -> tlgnc_driver.ueser_id -> tlgnu_user.person_id -> tlgnc_person
     */
    public void setDriverFirstname(String driverFirstname) {
        this.driverFirstname = driverFirstname;
    }

    /**
     * DRIVER_FIRSTNAME is nullable
     * Field length on DB: 40
     * tlgnc_person.firstname, referenced by tlgnc_order.delivered_driver_id -> tlgnc_driver.ueser_id -> tlgnu_user.person_id -> tlgnc_person
     */
    public String getDriverFirstname() {
        return driverFirstname;
    }

    /**
     * DRIVER_USER_TYPE_CD is nullable
     * Field length on DB: 2
     * tlgnc_user.user_type_cd, referenced by tlgnc_order.delivered_driver_id -> tlgnc_driver.user_id -> tlgnu_user
     */
    public void setDriverUserTypeCd(Long driverUserTypeCd) {
        this.driverUserTypeCd = driverUserTypeCd;
    }

    /**
     * DRIVER_USER_TYPE_CD is nullable
     * Field length on DB: 2
     * tlgnc_user.user_type_cd, referenced by tlgnc_order.delivered_driver_id -> tlgnc_driver.user_id -> tlgnu_user
     */
    public Long getDriverUserTypeCd() {
        return driverUserTypeCd;
    }

    /**
     * DRIVER_SAP_BP_NO is nullable
     * Field length on DB: 10
     * tlgns_sap_bp.sap_id, referenced by tlgnc_order.delivered_driver_id -> tlgnc_driver.user_id -> tlgnu_user.sap_bp_id -> tlgns_sap_bp
     */
    public void setDriverSapBpNo(String driverSapBpNo) {
        this.driverSapBpNo = driverSapBpNo;
    }

    /**
     * DRIVER_SAP_BP_NO is nullable
     * Field length on DB: 10
     * tlgns_sap_bp.sap_id, referenced by tlgnc_order.delivered_driver_id -> tlgnc_driver.user_id -> tlgnu_user.sap_bp_id -> tlgns_sap_bp
     */
    public String getDriverSapBpNo() {
        return driverSapBpNo;
    }

    /**
     * DRIVER_SAP_VENDOR_NO is nullable
     * Field length on DB: 10
     * tlgns_sap_vendor.sap_id, referenced by tlgnc_order.delivered_driver_id -> tlgnc_driver.user_id -> tlgnu_user.sap_vendor_id -> tlgns_sap_vendor
     */
    public void setDriverSapVendorNo(String driverSapVendorNo) {
        this.driverSapVendorNo = driverSapVendorNo;
    }

    /**
     * DRIVER_SAP_VENDOR_NO is nullable
     * Field length on DB: 10
     * tlgns_sap_vendor.sap_id, referenced by tlgnc_order.delivered_driver_id -> tlgnc_driver.user_id -> tlgnu_user.sap_vendor_id -> tlgns_sap_vendor
     */
    public String getDriverSapVendorNo() {
        return driverSapVendorNo;
    }

    /**
     * DRIVER_CERTIFIED_FROM_DAT is nullable
     * 
     * tlgnc_driver.certified_from_dat, referenced by tlgnc_order.delivered_driver_id -> tlgnc_driver
     */
    public void setDriverCertifiedFromDat(Date driverCertifiedFromDat) {
        this.driverCertifiedFromDat = driverCertifiedFromDat;
    }

    /**
     * DRIVER_CERTIFIED_FROM_DAT is nullable
     * 
     * tlgnc_driver.certified_from_dat, referenced by tlgnc_order.delivered_driver_id -> tlgnc_driver
     */
    public Date getDriverCertifiedFromDat() {
        return driverCertifiedFromDat;
    }

    /**
     * DRIVER_CERTIFIED_TO_DAT is nullable
     * 
     * tlgnc_driver.certified_to_dat, referenced by tlgnc_order.delivered_driver_id -> tlgnc_driver
     */
    public void setDriverCertifiedToDat(Date driverCertifiedToDat) {
        this.driverCertifiedToDat = driverCertifiedToDat;
    }

    /**
     * DRIVER_CERTIFIED_TO_DAT is nullable
     * 
     * tlgnc_driver.certified_to_dat, referenced by tlgnc_order.delivered_driver_id -> tlgnc_driver
     */
    public Date getDriverCertifiedToDat() {
        return driverCertifiedToDat;
    }

    /**
     * O_SENT_SALESDOC_TO_SAP_TS is nullable
     * 
     * tlgnc_order.sent_salesdoc_to_sap_ts
     */
    public void setOSentSalesdocToSapTs(Date oSentSalesdocToSapTs) {
        this.oSentSalesdocToSapTs = oSentSalesdocToSapTs;
    }

    /**
     * O_SENT_SALESDOC_TO_SAP_TS is nullable
     * 
     * tlgnc_order.sent_salesdoc_to_sap_ts
     */
    public Date getOSentSalesdocToSapTs() {
        return oSentSalesdocToSapTs;
    }

    /**
     * O_SENT_SHIPMENTDOC_TO_SAP_TS is nullable
     * 
     * tlgnc_order.sent_shipmentdoc_to_sap_ts
     */
    public void setOSentShipmentdocToSapTs(Date oSentShipmentdocToSapTs) {
        this.oSentShipmentdocToSapTs = oSentShipmentdocToSapTs;
    }

    /**
     * O_SENT_SHIPMENTDOC_TO_SAP_TS is nullable
     * 
     * tlgnc_order.sent_shipmentdoc_to_sap_ts
     */
    public Date getOSentShipmentdocToSapTs() {
        return oSentShipmentdocToSapTs;
    }

    /**
     * ASSP_LOADING_PLANNED_START is nullable
     * 
     * tlgnc_assignment_part.planned_start where assignment_part_type_cd=1, referenced by tlgnc_assignment_part.order_id -> tlgnc_order
     */
    public void setAsspLoadingPlannedStart(Date asspLoadingPlannedStart) {
        this.asspLoadingPlannedStart = asspLoadingPlannedStart;
    }

    /**
     * ASSP_LOADING_PLANNED_START is nullable
     * 
     * tlgnc_assignment_part.planned_start where assignment_part_type_cd=1, referenced by tlgnc_assignment_part.order_id -> tlgnc_order
     */
    public Date getAsspLoadingPlannedStart() {
        return asspLoadingPlannedStart;
    }

    /**
     * ASSP_LOADING_REAL_START is nullable
     * 
     * tlgnc_assignment_part.real_start where assignment_part_type_cd=1, referenced by tlgnc_assignment_part.order_id -> tlgnc_order
     */
    public void setAsspLoadingRealStart(Date asspLoadingRealStart) {
        this.asspLoadingRealStart = asspLoadingRealStart;
    }

    /**
     * ASSP_LOADING_REAL_START is nullable
     * 
     * tlgnc_assignment_part.real_start where assignment_part_type_cd=1, referenced by tlgnc_assignment_part.order_id -> tlgnc_order
     */
    public Date getAsspLoadingRealStart() {
        return asspLoadingRealStart;
    }

    /**
     * ASSP_UNLOADING_PLANNED_START is nullable
     * 
     * tlgnc_assignment_part.planned_start where assignment_part_type_cd=3, referenced by tlgnc_assignment_part.order_id -> tlgnc_order
     */
    public void setAsspUnloadingPlannedStart(Date asspUnloadingPlannedStart) {
        this.asspUnloadingPlannedStart = asspUnloadingPlannedStart;
    }

    /**
     * ASSP_UNLOADING_PLANNED_START is nullable
     * 
     * tlgnc_assignment_part.planned_start where assignment_part_type_cd=3, referenced by tlgnc_assignment_part.order_id -> tlgnc_order
     */
    public Date getAsspUnloadingPlannedStart() {
        return asspUnloadingPlannedStart;
    }

    /**
     * ASSP_UNLOADING_REAL_START is nullable
     * 
     * tlgnc_assignment_part.real_start where assignment_part_type_cd=3, referenced by tlgnc_assignment_part.order_id -> tlgnc_order
     */
    public void setAsspUnloadingRealStart(Date asspUnloadingRealStart) {
        this.asspUnloadingRealStart = asspUnloadingRealStart;
    }

    /**
     * ASSP_UNLOADING_REAL_START is nullable
     * 
     * tlgnc_assignment_part.real_start where assignment_part_type_cd=3, referenced by tlgnc_assignment_part.order_id -> tlgnc_order
     */
    public Date getAsspUnloadingRealStart() {
        return asspUnloadingRealStart;
    }

    /**
     * BIZ_NEW_TS is nullable
     * 
     * tlgnc_bizevent.EVENT_TS of the last entry with bizevent_type BIZEVENT_TYPE_NEW
     */
    public void setBizNewTs(Date bizNewTs) {
        this.bizNewTs = bizNewTs;
    }

    /**
     * BIZ_NEW_TS is nullable
     * 
     * tlgnc_bizevent.EVENT_TS of the last entry with bizevent_type BIZEVENT_TYPE_NEW
     */
    public Date getBizNewTs() {
        return bizNewTs;
    }

    /**
     * BIZ_TRANSM_POOL_TS is nullable
     * 
     * tlgnc_bizevent.EVENT_TS of the last entry with bizevent_type BIZEVENT_TYPE_TRANSMITTED_TO_POOL
     */
    public void setBizTransmPoolTs(Date bizTransmPoolTs) {
        this.bizTransmPoolTs = bizTransmPoolTs;
    }

    /**
     * BIZ_TRANSM_POOL_TS is nullable
     * 
     * tlgnc_bizevent.EVENT_TS of the last entry with bizevent_type BIZEVENT_TYPE_TRANSMITTED_TO_POOL
     */
    public Date getBizTransmPoolTs() {
        return bizTransmPoolTs;
    }

    /**
     * BIZ_TRANSM_POOL_ACK_TS is nullable
     * 
     * tlgnc_bizevent.EVENT_TS of the last entry with bizevent_type BIZEVENT_TYPE_TRANSMITTED_TO_POOL_ACK
     */
    public void setBizTransmPoolAckTs(Date bizTransmPoolAckTs) {
        this.bizTransmPoolAckTs = bizTransmPoolAckTs;
    }

    /**
     * BIZ_TRANSM_POOL_ACK_TS is nullable
     * 
     * tlgnc_bizevent.EVENT_TS of the last entry with bizevent_type BIZEVENT_TYPE_TRANSMITTED_TO_POOL_ACK
     */
    public Date getBizTransmPoolAckTs() {
        return bizTransmPoolAckTs;
    }

    /**
     * BIZ_TRANSM_COMM_CARRIER_TS is nullable
     * 
     * tlgnc_bizevent.EVENT_TS of the last entry with bizevent_type BIZEVENT_TYPE_TRANSMITTED_TO_COMM_CARRIER
     */
    public void setBizTransmCommCarrierTs(Date bizTransmCommCarrierTs) {
        this.bizTransmCommCarrierTs = bizTransmCommCarrierTs;
    }

    /**
     * BIZ_TRANSM_COMM_CARRIER_TS is nullable
     * 
     * tlgnc_bizevent.EVENT_TS of the last entry with bizevent_type BIZEVENT_TYPE_TRANSMITTED_TO_COMM_CARRIER
     */
    public Date getBizTransmCommCarrierTs() {
        return bizTransmCommCarrierTs;
    }

    /**
     * BIZ_TRANSM_COMM_CARRIER_ACK_TS is nullable
     * 
     * tlgnc_bizevent.EVENT_TS of the last entry with bizevent_type BIZEVENT_TYPE_TRANSMITTED_TO_COMM_CARRIER_ACK
     */
    public void setBizTransmCommCarrierAckTs(Date bizTransmCommCarrierAckTs) {
        this.bizTransmCommCarrierAckTs = bizTransmCommCarrierAckTs;
    }

    /**
     * BIZ_TRANSM_COMM_CARRIER_ACK_TS is nullable
     * 
     * tlgnc_bizevent.EVENT_TS of the last entry with bizevent_type BIZEVENT_TYPE_TRANSMITTED_TO_COMM_CARRIER_ACK
     */
    public Date getBizTransmCommCarrierAckTs() {
        return bizTransmCommCarrierAckTs;
    }

    /**
     * BIZ_TRANSM_EXEC_CARRIER_TS is nullable
     * 
     * tlgnc_bizevent.EVENT_TS of the last entry with bizevent_type BIZEVENT_TYPE_TRANSMITTED_TO_EXEC_CARRIER
     */
    public void setBizTransmExecCarrierTs(Date bizTransmExecCarrierTs) {
        this.bizTransmExecCarrierTs = bizTransmExecCarrierTs;
    }

    /**
     * BIZ_TRANSM_EXEC_CARRIER_TS is nullable
     * 
     * tlgnc_bizevent.EVENT_TS of the last entry with bizevent_type BIZEVENT_TYPE_TRANSMITTED_TO_EXEC_CARRIER
     */
    public Date getBizTransmExecCarrierTs() {
        return bizTransmExecCarrierTs;
    }

    /**
     * BIZ_DISPATCHED_TS is nullable
     * 
     * tlgnc_bizevent.EVENT_TS of the last entry with bizevent_type BIZEVENT_TYPE_DISPATCHED
     */
    public void setBizDispatchedTs(Date bizDispatchedTs) {
        this.bizDispatchedTs = bizDispatchedTs;
    }

    /**
     * BIZ_DISPATCHED_TS is nullable
     * 
     * tlgnc_bizevent.EVENT_TS of the last entry with bizevent_type BIZEVENT_TYPE_DISPATCHED
     */
    public Date getBizDispatchedTs() {
        return bizDispatchedTs;
    }

    /**
     * BIZ_TRANSM_TRUCK_TS is nullable
     * 
     * tlgnc_bizevent.EVENT_TS of the last entry with bizevent_type BIZEVENT_TYPE_TRANSMITTED_TO_TRUCK
     */
    public void setBizTransmTruckTs(Date bizTransmTruckTs) {
        this.bizTransmTruckTs = bizTransmTruckTs;
    }

    /**
     * BIZ_TRANSM_TRUCK_TS is nullable
     * 
     * tlgnc_bizevent.EVENT_TS of the last entry with bizevent_type BIZEVENT_TYPE_TRANSMITTED_TO_TRUCK
     */
    public Date getBizTransmTruckTs() {
        return bizTransmTruckTs;
    }

    /**
     * BIZ_TRANSM_TRUCK_ACK_TS is nullable
     * 
     * tlgnc_bizevent.EVENT_TS of the last entry with bizevent_type BIZEVENT_TYPE_TRANSMITTED_TO_TRUCK_ACK
     */
    public void setBizTransmTruckAckTs(Date bizTransmTruckAckTs) {
        this.bizTransmTruckAckTs = bizTransmTruckAckTs;
    }

    /**
     * BIZ_TRANSM_TRUCK_ACK_TS is nullable
     * 
     * tlgnc_bizevent.EVENT_TS of the last entry with bizevent_type BIZEVENT_TYPE_TRANSMITTED_TO_TRUCK_ACK
     */
    public Date getBizTransmTruckAckTs() {
        return bizTransmTruckAckTs;
    }

    /**
     * BIZ_REACT_TIME_ORD_MNG_MIN is nullable
     * Field length on DB: 12
     * Reaction Time Order Management
     */
    public void setBizReactTimeOrdMngMin(Long bizReactTimeOrdMngMin) {
        this.bizReactTimeOrdMngMin = bizReactTimeOrdMngMin;
    }

    /**
     * BIZ_REACT_TIME_ORD_MNG_MIN is nullable
     * Field length on DB: 12
     * Reaction Time Order Management
     */
    public Long getBizReactTimeOrdMngMin() {
        return bizReactTimeOrdMngMin;
    }

    /**
     * BIZ_REACT_TIME_POOL_MNG_MIN is nullable
     * Field length on DB: 12
     * Reaction Time Pool Manager
     */
    public void setBizReactTimePoolMngMin(Long bizReactTimePoolMngMin) {
        this.bizReactTimePoolMngMin = bizReactTimePoolMngMin;
    }

    /**
     * BIZ_REACT_TIME_POOL_MNG_MIN is nullable
     * Field length on DB: 12
     * Reaction Time Pool Manager
     */
    public Long getBizReactTimePoolMngMin() {
        return bizReactTimePoolMngMin;
    }

    /**
     * BIZ_REACT_TIME_DISPATCHER_MIN is nullable
     * Field length on DB: 12
     * Reaction Time Dispatcher
     */
    public void setBizReactTimeDispatcherMin(Long bizReactTimeDispatcherMin) {
        this.bizReactTimeDispatcherMin = bizReactTimeDispatcherMin;
    }

    /**
     * BIZ_REACT_TIME_DISPATCHER_MIN is nullable
     * Field length on DB: 12
     * Reaction Time Dispatcher
     */
    public Long getBizReactTimeDispatcherMin() {
        return bizReactTimeDispatcherMin;
    }

    /**
     * BIZ_REACT_TIME_TRUCK_DRV_MIN is nullable
     * Field length on DB: 12
     * Reaction Time Truck Driver
     */
    public void setBizReactTimeTruckDrvMin(Long bizReactTimeTruckDrvMin) {
        this.bizReactTimeTruckDrvMin = bizReactTimeTruckDrvMin;
    }

    /**
     * BIZ_REACT_TIME_TRUCK_DRV_MIN is nullable
     * Field length on DB: 12
     * Reaction Time Truck Driver
     */
    public Long getBizReactTimeTruckDrvMin() {
        return bizReactTimeTruckDrvMin;
    }

    /**
     * BIZ_REACT_TIME_NET_WINDOW_MIN is nullable
     * Field length on DB: 12
     * Delivery Time Window (net)
     */
    public void setBizReactTimeNetWindowMin(Long bizReactTimeNetWindowMin) {
        this.bizReactTimeNetWindowMin = bizReactTimeNetWindowMin;
    }

    /**
     * BIZ_REACT_TIME_NET_WINDOW_MIN is nullable
     * Field length on DB: 12
     * Delivery Time Window (net)
     */
    public Long getBizReactTimeNetWindowMin() {
        return bizReactTimeNetWindowMin;
    }

    /**
     * BIZ_ENTER_SP_TS is nullable
     * 
     * tlgnc_bizevent.event_ts, referenced by tlgnc_bizevent.order_id -> tlgnc_order.order_id
     */
    public void setBizEnterSpTs(Date bizEnterSpTs) {
        this.bizEnterSpTs = bizEnterSpTs;
    }

    /**
     * BIZ_ENTER_SP_TS is nullable
     * 
     * tlgnc_bizevent.event_ts, referenced by tlgnc_bizevent.order_id -> tlgnc_order.order_id
     */
    public Date getBizEnterSpTs() {
        return bizEnterSpTs;
    }

    /**
     * BIZ_START_LOAD_TS is nullable
     * 
     * tlgnc_bizevent.event_ts, referenced by tlgnc_bizevent.order_id -> tlgnc_order.order_id
     */
    public void setBizStartLoadTs(Date bizStartLoadTs) {
        this.bizStartLoadTs = bizStartLoadTs;
    }

    /**
     * BIZ_START_LOAD_TS is nullable
     * 
     * tlgnc_bizevent.event_ts, referenced by tlgnc_bizevent.order_id -> tlgnc_order.order_id
     */
    public Date getBizStartLoadTs() {
        return bizStartLoadTs;
    }

    /**
     * BIZ_WAITINGTIME_REASON_SP_CD is nullable
     * Field length on DB: 2
     * tlgnr_driver_waitingreason.waitingtime_reason_code, resolved by tlgnc_order.delivered_driver_id + tlgnc_order.shippingpoint_id
     */
    public void setBizWaitingtimeReasonSpCd(Long bizWaitingtimeReasonSpCd) {
        this.bizWaitingtimeReasonSpCd = bizWaitingtimeReasonSpCd;
    }

    /**
     * BIZ_WAITINGTIME_REASON_SP_CD is nullable
     * Field length on DB: 2
     * tlgnr_driver_waitingreason.waitingtime_reason_code, resolved by tlgnc_order.delivered_driver_id + tlgnc_order.shippingpoint_id
     */
    public Long getBizWaitingtimeReasonSpCd() {
        return bizWaitingtimeReasonSpCd;
    }

    /**
     * BIZ_END_LOAD_TS is nullable
     * 
     * tlgnc_bizevent.event_ts, referenced by tlgnc_bizevent.order_id -> tlgnc_order.order_id
     */
    public void setBizEndLoadTs(Date bizEndLoadTs) {
        this.bizEndLoadTs = bizEndLoadTs;
    }

    /**
     * BIZ_END_LOAD_TS is nullable
     * 
     * tlgnc_bizevent.event_ts, referenced by tlgnc_bizevent.order_id -> tlgnc_order.order_id
     */
    public Date getBizEndLoadTs() {
        return bizEndLoadTs;
    }

    /**
     * BIZ_LOADING_TIME_MIN is nullable
     * Field length on DB: 12
     * Loading Time
     */
    public void setBizLoadingTimeMin(Long bizLoadingTimeMin) {
        this.bizLoadingTimeMin = bizLoadingTimeMin;
    }

    /**
     * BIZ_LOADING_TIME_MIN is nullable
     * Field length on DB: 12
     * Loading Time
     */
    public Long getBizLoadingTimeMin() {
        return bizLoadingTimeMin;
    }

    /**
     * BIZ_LEAVE_SP_TS is nullable
     * 
     * tlgnc_bizevent.event_ts, referenced by tlgnc_bizevent.order_id -> tlgnc_order.order_id
     */
    public void setBizLeaveSpTs(Date bizLeaveSpTs) {
        this.bizLeaveSpTs = bizLeaveSpTs;
    }

    /**
     * BIZ_LEAVE_SP_TS is nullable
     * 
     * tlgnc_bizevent.event_ts, referenced by tlgnc_bizevent.order_id -> tlgnc_order.order_id
     */
    public Date getBizLeaveSpTs() {
        return bizLeaveSpTs;
    }

    /**
     * BIZ_CYCLE_TIME_AT_SP_MIN is nullable
     * Field length on DB: 12
     * Cycle Time at Ship.Station
     */
    public void setBizCycleTimeAtSpMin(Long bizCycleTimeAtSpMin) {
        this.bizCycleTimeAtSpMin = bizCycleTimeAtSpMin;
    }

    /**
     * BIZ_CYCLE_TIME_AT_SP_MIN is nullable
     * Field length on DB: 12
     * Cycle Time at Ship.Station
     */
    public Long getBizCycleTimeAtSpMin() {
        return bizCycleTimeAtSpMin;
    }

    /**
     * BIZ_TRANS_RAIL_COMPANY_TS is nullable
     * 
     * tlgnc_bizevent.event_ts, referenced by tlgnc_bizevent.order_id -> tlgnc_order.order_id with bizevent_type BIZEVENT_TYPE_TRANSMITTED_TO_RAIL
     */
    public void setBizTransRailCompanyTs(Date bizTransRailCompanyTs) {
        this.bizTransRailCompanyTs = bizTransRailCompanyTs;
    }

    /**
     * BIZ_TRANS_RAIL_COMPANY_TS is nullable
     * 
     * tlgnc_bizevent.event_ts, referenced by tlgnc_bizevent.order_id -> tlgnc_order.order_id with bizevent_type BIZEVENT_TYPE_TRANSMITTED_TO_RAIL
     */
    public Date getBizTransRailCompanyTs() {
        return bizTransRailCompanyTs;
    }

    /**
     * BIZ_TRAFFICJAM_COUNT is nullable
     * 
     * Count of all bizevents (tlgnc_bizevent) of type traffic jam (210 and 211).
     */
    public void setBizTrafficjamCount(Double bizTrafficjamCount) {
        this.bizTrafficjamCount = bizTrafficjamCount;
    }

    /**
     * BIZ_TRAFFICJAM_COUNT is nullable
     * 
     * Count of all bizevents (tlgnc_bizevent) of type traffic jam (210 and 211).
     */
    public Double getBizTrafficjamCount() {
        return bizTrafficjamCount;
    }

    /**
     * BIZ_TRAFFICJAM_DURATION_MIN is nullable
     * Field length on DB: 6
     * Total duration of all trafficjams. SUM(traffic_jam_end - traffic_jam_start). Time is taken from tlgnc_bizevent.event_ts.
     */
    public void setBizTrafficjamDurationMin(Long bizTrafficjamDurationMin) {
        this.bizTrafficjamDurationMin = bizTrafficjamDurationMin;
    }

    /**
     * BIZ_TRAFFICJAM_DURATION_MIN is nullable
     * Field length on DB: 6
     * Total duration of all trafficjams. SUM(traffic_jam_end - traffic_jam_start). Time is taken from tlgnc_bizevent.event_ts.
     */
    public Long getBizTrafficjamDurationMin() {
        return bizTrafficjamDurationMin;
    }

    /**
     * BIZ_BREAK_DRIVING_COUNT is nullable
     * Field length on DB: 12
     * FIXME: This field is currently not used at all.
     */
    public void setBizBreakDrivingCount(Long bizBreakDrivingCount) {
        this.bizBreakDrivingCount = bizBreakDrivingCount;
    }

    /**
     * BIZ_BREAK_DRIVING_COUNT is nullable
     * Field length on DB: 12
     * FIXME: This field is currently not used at all.
     */
    public Long getBizBreakDrivingCount() {
        return bizBreakDrivingCount;
    }

    /**
     * BIZ_BREAK_DRIVING_DURATION_MIN is nullable
     * Field length on DB: 12
     * FIXME: This field is currently not used at all.
     */
    public void setBizBreakDrivingDurationMin(Long bizBreakDrivingDurationMin) {
        this.bizBreakDrivingDurationMin = bizBreakDrivingDurationMin;
    }

    /**
     * BIZ_BREAK_DRIVING_DURATION_MIN is nullable
     * Field length on DB: 12
     * FIXME: This field is currently not used at all.
     */
    public Long getBizBreakDrivingDurationMin() {
        return bizBreakDrivingDurationMin;
    }

    /**
     * BIZ_ENTER_ST_TS is nullable
     * 
     * tlgnc_bizevent.event_ts, referenced by tlgnc_bizevent.order_id -> tlgnc_order.order_id
     */
    public void setBizEnterStTs(Date bizEnterStTs) {
        this.bizEnterStTs = bizEnterStTs;
    }

    /**
     * BIZ_ENTER_ST_TS is nullable
     * 
     * tlgnc_bizevent.event_ts, referenced by tlgnc_bizevent.order_id -> tlgnc_order.order_id
     */
    public Date getBizEnterStTs() {
        return bizEnterStTs;
    }

    /**
     * BIZ_CALC_DRIVING_TIME_MIN is nullable
     * Field length on DB: 12
     * FIXME: This field is currently not used at all.
     */
    public void setBizCalcDrivingTimeMin(Long bizCalcDrivingTimeMin) {
        this.bizCalcDrivingTimeMin = bizCalcDrivingTimeMin;
    }

    /**
     * BIZ_CALC_DRIVING_TIME_MIN is nullable
     * Field length on DB: 12
     * FIXME: This field is currently not used at all.
     */
    public Long getBizCalcDrivingTimeMin() {
        return bizCalcDrivingTimeMin;
    }

    /**
     * BIZ_CALC_NET_DRIVING_TIME_MIN is nullable
     * Field length on DB: 12
     * Calculated the driving net time, respecting the driver breaks.
     */
    public void setBizCalcNetDrivingTimeMin(Long bizCalcNetDrivingTimeMin) {
        this.bizCalcNetDrivingTimeMin = bizCalcNetDrivingTimeMin;
    }

    /**
     * BIZ_CALC_NET_DRIVING_TIME_MIN is nullable
     * Field length on DB: 12
     * Calculated the driving net time, respecting the driver breaks.
     */
    public Long getBizCalcNetDrivingTimeMin() {
        return bizCalcNetDrivingTimeMin;
    }

    /**
     * BIZ_ACTUAL_DISTANCE_M is nullable
     * Field length on DB: 10
     * DEPRECATED - FIXME - still used in reports
     */
    public void setBizActualDistanceM(Long bizActualDistanceM) {
        this.bizActualDistanceM = bizActualDistanceM;
    }

    /**
     * BIZ_ACTUAL_DISTANCE_M is nullable
     * Field length on DB: 10
     * DEPRECATED - FIXME - still used in reports
     */
    public Long getBizActualDistanceM() {
        return bizActualDistanceM;
    }

    /**
     * BIZ_START_UNLOAD_TS is nullable
     * 
     * tlgnc_bizevent.event_ts, referenced by tlgnc_bizevent.order_id -> tlgnc_order.order_id
     */
    public void setBizStartUnloadTs(Date bizStartUnloadTs) {
        this.bizStartUnloadTs = bizStartUnloadTs;
    }

    /**
     * BIZ_START_UNLOAD_TS is nullable
     * 
     * tlgnc_bizevent.event_ts, referenced by tlgnc_bizevent.order_id -> tlgnc_order.order_id
     */
    public Date getBizStartUnloadTs() {
        return bizStartUnloadTs;
    }

    /**
     * BIZ_WAITINGTIME_REASON_ST_CD is nullable
     * Field length on DB: 2
     * tlgnr_driver_waitingreason.waitingtime_reason_code, resolved by tlgnc_order.delivered_driver_id + tlgnc_shipto.shipto_id tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_sap_bp_id
     */
    public void setBizWaitingtimeReasonStCd(Long bizWaitingtimeReasonStCd) {
        this.bizWaitingtimeReasonStCd = bizWaitingtimeReasonStCd;
    }

    /**
     * BIZ_WAITINGTIME_REASON_ST_CD is nullable
     * Field length on DB: 2
     * tlgnr_driver_waitingreason.waitingtime_reason_code, resolved by tlgnc_order.delivered_driver_id + tlgnc_shipto.shipto_id tlgnc_order.shipto_sap_bp_id -> tlgns_shipto.shipto_sap_bp_id
     */
    public Long getBizWaitingtimeReasonStCd() {
        return bizWaitingtimeReasonStCd;
    }

    /**
     * BIZ_END_UNLOAD_TS is nullable
     * 
     * tlgnc_bizevent.event_ts, referenced by tlgnc_bizevent.order_id -> tlgnc_order.order_id
     */
    public void setBizEndUnloadTs(Date bizEndUnloadTs) {
        this.bizEndUnloadTs = bizEndUnloadTs;
    }

    /**
     * BIZ_END_UNLOAD_TS is nullable
     * 
     * tlgnc_bizevent.event_ts, referenced by tlgnc_bizevent.order_id -> tlgnc_order.order_id
     */
    public Date getBizEndUnloadTs() {
        return bizEndUnloadTs;
    }

    /**
     * BIZ_UNLOADING_MIN is nullable
     * Field length on DB: 12
     * Unloading Time
     */
    public void setBizUnloadingMin(Long bizUnloadingMin) {
        this.bizUnloadingMin = bizUnloadingMin;
    }

    /**
     * BIZ_UNLOADING_MIN is nullable
     * Field length on DB: 12
     * Unloading Time
     */
    public Long getBizUnloadingMin() {
        return bizUnloadingMin;
    }

    /**
     * BIZ_LEAVE_ST_TS is nullable
     * 
     * tlgnc_bizevent.event_ts, referenced by tlgnc_bizevent.order_id -> tlgnc_order.order_id
     */
    public void setBizLeaveStTs(Date bizLeaveStTs) {
        this.bizLeaveStTs = bizLeaveStTs;
    }

    /**
     * BIZ_LEAVE_ST_TS is nullable
     * 
     * tlgnc_bizevent.event_ts, referenced by tlgnc_bizevent.order_id -> tlgnc_order.order_id
     */
    public Date getBizLeaveStTs() {
        return bizLeaveStTs;
    }

    /**
     * BIZ_CYCLE_TIME_AT_SHIPTO_MIN is nullable
     * Field length on DB: 12
     * Cycle Time at Ship-To
     */
    public void setBizCycleTimeAtShiptoMin(Long bizCycleTimeAtShiptoMin) {
        this.bizCycleTimeAtShiptoMin = bizCycleTimeAtShiptoMin;
    }

    /**
     * BIZ_CYCLE_TIME_AT_SHIPTO_MIN is nullable
     * Field length on DB: 12
     * Cycle Time at Ship-To
     */
    public Long getBizCycleTimeAtShiptoMin() {
        return bizCycleTimeAtShiptoMin;
    }

    /**
     * BIZ_GEOEVENT_COUNT is nullable
     * 
     * Count of all bizevents (tlgnc_bizevent) of type position (118).
     */
    public void setBizGeoeventCount(Double bizGeoeventCount) {
        this.bizGeoeventCount = bizGeoeventCount;
    }

    /**
     * BIZ_GEOEVENT_COUNT is nullable
     * 
     * Count of all bizevents (tlgnc_bizevent) of type position (118).
     */
    public Double getBizGeoeventCount() {
        return bizGeoeventCount;
    }

    /**
     * O_SHORT_TERM_ORDER_FLAG is NOT nullable
     * 
     * FIXME
     */
    public void setOShortTermOrderFlag(Boolean oShortTermOrderFlag) {
        this.oShortTermOrderFlag = oShortTermOrderFlag;
    }

    /**
     * O_SHORT_TERM_ORDER_FLAG is NOT nullable
     * 
     * FIXME
     */
    public Boolean getOShortTermOrderFlag() {
        return oShortTermOrderFlag;
    }

    /**
     * O_TRIP_INC_IN_STATISTICS_FLAG is NOT nullable
     * 
     * DEPRECATED - FIXME - still used in reports
     */
    public void setOTripIncInStatisticsFlag(Boolean oTripIncInStatisticsFlag) {
        this.oTripIncInStatisticsFlag = oTripIncInStatisticsFlag;
    }

    /**
     * O_TRIP_INC_IN_STATISTICS_FLAG is NOT nullable
     * 
     * DEPRECATED - FIXME - still used in reports
     */
    public Boolean getOTripIncInStatisticsFlag() {
        return oTripIncInStatisticsFlag;
    }

    /**
     * O_RESCHEDULING_FLAG is NOT nullable
     * 
     * FIXME
     */
    public void setOReschedulingFlag(Boolean oReschedulingFlag) {
        this.oReschedulingFlag = oReschedulingFlag;
    }

    /**
     * O_RESCHEDULING_FLAG is NOT nullable
     * 
     * FIXME
     */
    public Boolean getOReschedulingFlag() {
        return oReschedulingFlag;
    }
    
    /**
	 * @return the oLifexNo
	 */
	public String getoLifexNo() {
		return oLifexNo;
	}

	/**
	 * @param oLifexNo the oLifexNo to set
	 */
	public void setoLifexNo(String oLifexNo) {
		this.oLifexNo = oLifexNo;
	}

    /**
     * O_LNF_COORD_LAT is nullable
     * 
     * tlgnc_order.lnf_coord_lat
     */
    public void setOLnfCoordLat(Double oLnfCoordLat) {
        this.oLnfCoordLat = oLnfCoordLat;
    }

    /**
     * O_LNF_COORD_LAT is nullable
     * 
     * tlgnc_order.lnf_coord_lat
     */
    public Double getOLnfCoordLat() {
        return oLnfCoordLat;
    }

    /**
     * O_LNF_COORD_LON is nullable
     * 
     * tlgnc_order.lnf_coord_lat
     */
    public void setOLnfCoordLon(Double oLnfCoordLon) {
        this.oLnfCoordLon = oLnfCoordLon;
    }

    /**
     * O_LNF_COORD_LON is nullable
     * 
     * tlgnc_order.lnf_coord_lat
     */
    public Double getOLnfCoordLon() {
        return oLnfCoordLon;
    }

    /**
     * O_LNF_SITE_NAME is nullable
     * Field length on DB: 80
     * tlgnc_order.lnf_site_name
     */
    public void setOLnfSiteName(String oLnfSiteName) {
        this.oLnfSiteName = oLnfSiteName;
    }

    /**
     * O_LNF_SITE_NAME is nullable
     * Field length on DB: 80
     * tlgnc_order.lnf_site_name
     */
    public String getOLnfSiteName() {
        return oLnfSiteName;
    }

    /**
     * O_LNF_SITE_STREET_HOUSE_NO is nullable
     * Field length on DB: 45
     * tlgnc_order.lnf_site_name.
     */
    public void setOLnfSiteStreetHouseNo(String oLnfSiteStreetHouseNo) {
        this.oLnfSiteStreetHouseNo = oLnfSiteStreetHouseNo;
    }

    /**
     * O_LNF_SITE_STREET_HOUSE_NO is nullable
     * Field length on DB: 45
     * tlgnc_order.lnf_site_name.
     */
    public String getOLnfSiteStreetHouseNo() {
        return oLnfSiteStreetHouseNo;
    }

    /**
     * O_LNF_SITE_COUNTRY is nullable
     * Field length on DB: 3
     * tlgnc_order.lnf_site_name.
     */
    public void setOLnfSiteCountry(String oLnfSiteCountry) {
        this.oLnfSiteCountry = oLnfSiteCountry;
    }

    /**
     * O_LNF_SITE_COUNTRY is nullable
     * Field length on DB: 3
     * tlgnc_order.lnf_site_name.
     */
    public String getOLnfSiteCountry() {
        return oLnfSiteCountry;
    }

    /**
     * O_LNF_SITE_POSTAL_CODE is nullable
     * Field length on DB: 10
     * tlgnc_order.lnf_site_name.
     */
    public void setOLnfSitePostalCode(String oLnfSitePostalCode) {
        this.oLnfSitePostalCode = oLnfSitePostalCode;
    }

    /**
     * O_LNF_SITE_POSTAL_CODE is nullable
     * Field length on DB: 10
     * tlgnc_order.lnf_site_name.
     */
    public String getOLnfSitePostalCode() {
        return oLnfSitePostalCode;
    }

    /**
     * O_LNF_SITE_CITY is nullable
     * Field length on DB: 35
     * tlgnc_order.lnf_site_name.
     */
    public void setOLnfSiteCity(String oLnfSiteCity) {
        this.oLnfSiteCity = oLnfSiteCity;
    }

    /**
     * O_LNF_SITE_CITY is nullable
     * Field length on DB: 35
     * tlgnc_order.lnf_site_name.
     */
    public String getOLnfSiteCity() {
        return oLnfSiteCity;
    }

    /**
     * O_LNF_SITE_VAT_NO is nullable
     * Field length on DB: 20
     * tlgnc_order.lnf_site_vat_no.
     */
    public void setOLnfSiteVatNo(String oLnfSiteVatNo) {
        this.oLnfSiteVatNo = oLnfSiteVatNo;
    }

    /**
     * O_LNF_SITE_VAT_NO is nullable
     * Field length on DB: 20
     * tlgnc_order.lnf_site_vat_no.
     */
    public String getOLnfSiteVatNo() {
        return oLnfSiteVatNo;
    }

    /**
     * O_ONE_TIME_SOLDTO_NAME is nullable
     * Field length on DB: 80
     * tlgnc_order.one_time_soldto_name
     */
    public void setOOneTimeSoldtoName(String oOneTimeSoldtoName) {
        this.oOneTimeSoldtoName = oOneTimeSoldtoName;
    }

    /**
     * O_ONE_TIME_SOLDTO_NAME is nullable
     * Field length on DB: 80
     * tlgnc_order.one_time_soldto_name
     */
    public String getOOneTimeSoldtoName() {
        return oOneTimeSoldtoName;
    }

    /**
     * O_ONE_TIME_SOLDTO_STREET_HNO is nullable
     * Field length on DB: 45
     * tlgnc_order.one_time_soldto_street_houseno
     */
    public void setOOneTimeSoldtoStreetHno(String oOneTimeSoldtoStreetHno) {
        this.oOneTimeSoldtoStreetHno = oOneTimeSoldtoStreetHno;
    }

    /**
     * O_ONE_TIME_SOLDTO_STREET_HNO is nullable
     * Field length on DB: 45
     * tlgnc_order.one_time_soldto_street_houseno
     */
    public String getOOneTimeSoldtoStreetHno() {
        return oOneTimeSoldtoStreetHno;
    }

    /**
     * O_ONE_TIME_SOLDTO_CITY is nullable
     * Field length on DB: 35
     * tlgnc_order.one_time_soldto_city
     */
    public void setOOneTimeSoldtoCity(String oOneTimeSoldtoCity) {
        this.oOneTimeSoldtoCity = oOneTimeSoldtoCity;
    }

    /**
     * O_ONE_TIME_SOLDTO_CITY is nullable
     * Field length on DB: 35
     * tlgnc_order.one_time_soldto_city
     */
    public String getOOneTimeSoldtoCity() {
        return oOneTimeSoldtoCity;
    }

    /**
     * O_ONE_TIME_SOLDTO_POSTAL_CODE is nullable
     * Field length on DB: 10
     * tlgnc_order.one_time_soldto_postal_code
     */
    public void setOOneTimeSoldtoPostalCode(String oOneTimeSoldtoPostalCode) {
        this.oOneTimeSoldtoPostalCode = oOneTimeSoldtoPostalCode;
    }

    /**
     * O_ONE_TIME_SOLDTO_POSTAL_CODE is nullable
     * Field length on DB: 10
     * tlgnc_order.one_time_soldto_postal_code
     */
    public String getOOneTimeSoldtoPostalCode() {
        return oOneTimeSoldtoPostalCode;
    }

    /**
     * O_ONE_TIME_SOLDTO_COUNTRY is nullable
     * Field length on DB: 3
     * tlgnc_order.one_time_soldto_country
     */
    public void setOOneTimeSoldtoCountry(String oOneTimeSoldtoCountry) {
        this.oOneTimeSoldtoCountry = oOneTimeSoldtoCountry;
    }

    /**
     * O_ONE_TIME_SOLDTO_COUNTRY is nullable
     * Field length on DB: 3
     * tlgnc_order.one_time_soldto_country
     */
    public String getOOneTimeSoldtoCountry() {
        return oOneTimeSoldtoCountry;
    }

    /**
     * O_ONE_TIME_SOLDTO_VAT_NO is nullable
     * Field length on DB: 20
     * tlgnc_order.one_time_soldto_vat_no.
     */
    public void setOOneTimeSoldtoVatNo(String oOneTimeSoldtoVatNo) {
        this.oOneTimeSoldtoVatNo = oOneTimeSoldtoVatNo;
    }

    /**
     * O_ONE_TIME_SOLDTO_VAT_NO is nullable
     * Field length on DB: 20
     * tlgnc_order.one_time_soldto_vat_no.
     */
    public String getOOneTimeSoldtoVatNo() {
        return oOneTimeSoldtoVatNo;
    }

    /**
     * REPORT_SVC_LEVEL_RELEVANT_FLAG is NOT nullable
     * 
     * EDITABLE Flag in Reporting tab
     */
    public void setReportSvcLevelRelevantFlag(Boolean reportSvcLevelRelevantFlag) {
        this.reportSvcLevelRelevantFlag = reportSvcLevelRelevantFlag;
    }

    /**
     * REPORT_SVC_LEVEL_RELEVANT_FLAG is NOT nullable
     * 
     * EDITABLE Flag in Reporting tab
     */
    public Boolean getReportSvcLevelRelevantFlag() {
        return reportSvcLevelRelevantFlag;
    }

    /**
     * REPORT_NOTE_FROM_CRH is nullable
     * Field length on DB: 256
     * EDITABLE text area, similar to the order note fields
     */
    public void setReportNoteFromCrh(String reportNoteFromCrh) {
        this.reportNoteFromCrh = reportNoteFromCrh;
    }

    /**
     * REPORT_NOTE_FROM_CRH is nullable
     * Field length on DB: 256
     * EDITABLE text area, similar to the order note fields
     */
    public String getReportNoteFromCrh() {
        return reportNoteFromCrh;
    }

    /**
     * REPORT_NOTE_FROM_CARRIER is nullable
     * Field length on DB: 256
     * EDITABLE text area, similar to the order note fields
     */
    public void setReportNoteFromCarrier(String reportNoteFromCarrier) {
        this.reportNoteFromCarrier = reportNoteFromCarrier;
    }

    /**
     * REPORT_NOTE_FROM_CARRIER is nullable
     * Field length on DB: 256
     * EDITABLE text area, similar to the order note fields
     */
    public String getReportNoteFromCarrier() {
        return reportNoteFromCarrier;
    }

    /**
     * REPORT_FINISHED_COMM_FLAG is NOT nullable
     * 
     * 1/true = the commercial aspects (cost, netprice, margin) of this order (and maybe other orders it depends on) are complete and OK. Data is meant to be included in reports where monetary aspects are relevant. Value is initially set during 'finish' operation. It can only be edited/set indirectly via later cost updates/recalculations of the order, triggered by netprice import interface or by manual recalculations called by the user.
     */
    public void setReportFinishedCommFlag(Boolean reportFinishedCommFlag) {
        this.reportFinishedCommFlag = reportFinishedCommFlag;
    }

    /**
     * REPORT_FINISHED_COMM_FLAG is NOT nullable
     * 
     * 1/true = the commercial aspects (cost, netprice, margin) of this order (and maybe other orders it depends on) are complete and OK. Data is meant to be included in reports where monetary aspects are relevant. Value is initially set during 'finish' operation. It can only be edited/set indirectly via later cost updates/recalculations of the order, triggered by netprice import interface or by manual recalculations called by the user.
     */
    public Boolean getReportFinishedCommFlag() {
        return reportFinishedCommFlag;
    }

    /**
     * O_CTL_CRE_UID is NOT nullable
     * Field length on DB: 12
     * Technical field of order (tlgnc_order.ctl_cre_uid). Non-enforced foreign key to the UserEntity who created the tlgnc_order record.
     */
    public void setOCtlCreUid(Long oCtlCreUid) {
        this.oCtlCreUid = oCtlCreUid;
    }

    /**
     * O_CTL_CRE_UID is NOT nullable
     * Field length on DB: 12
     * Technical field of order (tlgnc_order.ctl_cre_uid). Non-enforced foreign key to the UserEntity who created the tlgnc_order record.
     */
    public Long getOCtlCreUid() {
        return oCtlCreUid;
    }

    /**
     * O_CTL_CRE_UID_NAME is nullable
     * Field length on DB: 80
     * tlgnc_person.firstname + tlgnc_person.name resolved by tlgnc_order.ctl_cre_uid -> tlgnu_user -> tlgnc_person
     */
    public void setOCtlCreUidName(String oCtlCreUidName) {
        this.oCtlCreUidName = oCtlCreUidName;
    }

    /**
     * O_CTL_CRE_UID_NAME is nullable
     * Field length on DB: 80
     * tlgnc_person.firstname + tlgnc_person.name resolved by tlgnc_order.ctl_cre_uid -> tlgnu_user -> tlgnc_person
     */
    public String getOCtlCreUidName() {
        return oCtlCreUidName;
    }

    /**
     * O_CTL_CRE_TS is NOT nullable
     * 
     * Technical field (tlgnc_order.ctl_cre_ts). The time when the record got created.
     */
    public void setOCtlCreTs(Date oCtlCreTs) {
        this.oCtlCreTs = oCtlCreTs;
    }

    /**
     * O_CTL_CRE_TS is NOT nullable
     * 
     * Technical field (tlgnc_order.ctl_cre_ts). The time when the record got created.
     */
    public Date getOCtlCreTs() {
        return oCtlCreTs;
    }

    /**
     * O_CTL_MOD_UID is NOT nullable
     * Field length on DB: 12
     * Technical field of order (tlgnc_order.ctl_mod_uid). Non-enforced foreign key to the UserEntity who last modified the tlgnc_order record.
     */
    public void setOCtlModUid(Long oCtlModUid) {
        this.oCtlModUid = oCtlModUid;
    }

    /**
     * O_CTL_MOD_UID is NOT nullable
     * Field length on DB: 12
     * Technical field of order (tlgnc_order.ctl_mod_uid). Non-enforced foreign key to the UserEntity who last modified the tlgnc_order record.
     */
    public Long getOCtlModUid() {
        return oCtlModUid;
    }

    /**
     * O_CTL_MOD_UID_NAME is nullable
     * Field length on DB: 80
     * tlgnc_person.firstname + tlgnc_person.name resolved by tlgnc_order.ctl_cre_uid -> tlgnu_user -> tlgnc_person
     */
    public void setOCtlModUidName(String oCtlModUidName) {
        this.oCtlModUidName = oCtlModUidName;
    }

    /**
     * O_CTL_MOD_UID_NAME is nullable
     * Field length on DB: 80
     * tlgnc_person.firstname + tlgnc_person.name resolved by tlgnc_order.ctl_cre_uid -> tlgnu_user -> tlgnc_person
     */
    public String getOCtlModUidName() {
        return oCtlModUidName;
    }

    /**
     * O_CTL_MOD_TS is NOT nullable
     * 
     * Technical field.(tlgnc_order.ctl_mod_ts) The time when the record was last modified.
     */
    public void setOCtlModTs(Date oCtlModTs) {
        this.oCtlModTs = oCtlModTs;
    }

    /**
     * O_CTL_MOD_TS is NOT nullable
     * 
     * Technical field.(tlgnc_order.ctl_mod_ts) The time when the record was last modified.
     */
    public Date getOCtlModTs() {
        return oCtlModTs;
    }

    /**
     * O_BP_CORRECTED_FLAG is nullable
     * 
     * This flag is set to 1 if the business partner (soldto, shipto, billto, sarea) is modified after the order is in status delivered
     */
    public void setOBpCorrectedFlag(Boolean oBpCorrectedFlag) {
        this.oBpCorrectedFlag = oBpCorrectedFlag;
    }

    /**
     * O_BP_CORRECTED_FLAG is nullable
     * 
     * This flag is set to 1 if the business partner (soldto, shipto, billto, sarea) is modified after the order is in status delivered
     */
    public Boolean getOBpCorrectedFlag() {
        return oBpCorrectedFlag;
    }

    /**
     * EXTRA_COST_WAITING_TIME_MIN is nullable
     * Field length on DB: 4
     * Waiting time, only refunded to carrier.
     */
    public void setExtraCostWaitingTimeMin(Long extraCostWaitingTimeMin) {
        this.extraCostWaitingTimeMin = extraCostWaitingTimeMin;
    }

    /**
     * EXTRA_COST_WAITING_TIME_MIN is nullable
     * Field length on DB: 4
     * Waiting time, only refunded to carrier.
     */
    public Long getExtraCostWaitingTimeMin() {
        return extraCostWaitingTimeMin;
    }

    /**
     * EXTRA_COST_1 is nullable
     * Field length on DB: 20
     * 1. extra cost SAP Code of the order.
     */
    public void setExtraCost1(String extraCost1) {
        this.extraCost1 = extraCost1;
    }

    /**
     * EXTRA_COST_1 is nullable
     * Field length on DB: 20
     * 1. extra cost SAP Code of the order.
     */
    public String getExtraCost1() {
        return extraCost1;
    }

    /**
     * EXTRA_COST_2 is nullable
     * Field length on DB: 20
     * 2. extra cost SAP Code of the order.
     */
    public void setExtraCost2(String extraCost2) {
        this.extraCost2 = extraCost2;
    }

    /**
     * EXTRA_COST_2 is nullable
     * Field length on DB: 20
     * 2. extra cost SAP Code of the order.
     */
    public String getExtraCost2() {
        return extraCost2;
    }

    /**
     * EXTRA_COST_3 is nullable
     * Field length on DB: 20
     * 3. extra cost SAP Code of the order.
     */
    public void setExtraCost3(String extraCost3) {
        this.extraCost3 = extraCost3;
    }

    /**
     * EXTRA_COST_3 is nullable
     * Field length on DB: 20
     * 3. extra cost SAP Code of the order.
     */
    public String getExtraCost3() {
        return extraCost3;
    }

    /**
     * EXTRA_COST_4 is nullable
     * Field length on DB: 20
     * 4. extra cost SAP Code of the order.
     */
    public void setExtraCost4(String extraCost4) {
        this.extraCost4 = extraCost4;
    }

    /**
     * EXTRA_COST_4 is nullable
     * Field length on DB: 20
     * 4. extra cost SAP Code of the order.
     */
    public String getExtraCost4() {
        return extraCost4;
    }
    
    /**
     * O_DELIV_TYPE_PLAN_ACTUAL is nullable
     * Field length on DB: 1
     * True if the orders delivery type matches the dispo card
     */
    public Boolean getODelivTypePlanActual() {
    	return oDelivTypePlanActual;
    }
    
    /**
     * O_DELIV_TYPE_PLAN_ACTUAL is nullable
     * Field length on DB: 1
     * True if the orders delivery type matches the dispo card
     */
    public void setODelivTypePlanActual(Boolean oDelivTypePlanActual) {
    	this.oDelivTypePlanActual = oDelivTypePlanActual;
    }
    
    /**
     * O_FORCE_TO_SAP_FLAG is not nullable
     * Field length on DB: 1
     * True if the orders force to SAP flag is true
     */
	public Boolean getoForceToSapFlag() {
		return oForceToSapFlag;
	}

    /**
     * O_FORCE_TO_SAP_FLAG is not nullable
     * Field length on DB: 1
     * True if the orders force to SAP flag is true
     */
	public void setoForceToSapFlag(Boolean oForceToSapFlag) {
		this.oForceToSapFlag = oForceToSapFlag;
	}

	/**O_ARRIVAL_TS is nullable
	 * Field length on DB: 6
	 * Timestamp used for further calculate the O_CALC_LATE_DELIVERY_FLAG
	 */
	public Date getOArrivalTs() {
		return oArrivalTs;
	}

	/**O_ARRIVAL_TS is nullable
	 * Field length on DB: 6
	 * Timestamp used for further calculate the O_CALC_LATE_DELIVERY_FLAG
	 */
	public void setOArrivalTs(Date oArrivalTs) {
		this.oArrivalTs = oArrivalTs;
	}

    /**
     * CTL_CRE_UID is NOT nullable
     * Field length on DB: 12
     * Technical field. The principal name (corresponding field of javax.security.Principal) of the user who created the record.
     */
    public Long getCtlCreUid() {
        return ctlCreUid;
    }

    /**
     * CTL_CRE_TS is NOT nullable
     * 
     * Technical field. The time when the record got created.
     */
    public Date getCtlCreTs() {
        return ctlCreTs;
    }

    /**
     * CTL_MOD_UID is NOT nullable
     * Field length on DB: 12
     * Technical field. The principal name (corresponding field of javax.security.Principal) of the user who last modified the record.
     */
    public Long getCtlModUid() {
        return ctlModUid;
    }

    /**
     * CTL_MOD_TS is NOT nullable
     * 
     * Technical field. The time when the record was last modified.
     */
    public Date getCtlModTs() {
        return ctlModTs;
    }



    public void setOrderFinalItems(List<OrderItemFinalEntity> orderFinalItems) {
        this.orderFinalItems = orderFinalItems;
    }

    public List<OrderItemFinalEntity> getOrderFinalItems() {
        return orderFinalItems;
    }
    

    public void initialize(){
    	Hibernate.initialize(orderFinalItems);
    }

    @PrePersist
    void prePersist() {
    	ctlCreTs = new Date();
    	ctlCreUid = ThreadLocalUserInfo.getUserId();

    	ctlModTs = ctlCreTs;
    	ctlModUid = ctlCreUid;
    }

    @PreUpdate
    void preUpdate() {
    	ctlModTs = new Date();
    	ctlModUid = ThreadLocalUserInfo.getUserId();
    }
    
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this);
        tsb.append(super.toString());
        if (LOG_ENTITIES.isDebugEnabled()) {
            tsb.append("orderFinalId", orderFinalId);
            tsb.append("oOrderId", "oOrderId");
            tsb.append("oOrderNumber", "oOrderNumber");
            tsb.append("oOrderStatusCd", "oOrderStatusCd");
            tsb.append("oShiptoSapBpId", "oShiptoSapBpId");
            tsb.append("weSapId", "weSapId");
            tsb.append("weMarkedForDeletion", "weMarkedForDeletion");
            tsb.append("weName1", "weName1");
            tsb.append("weName2", "weName2");
            tsb.append("weCountry", "weCountry");
            tsb.append("wePostalCode", "wePostalCode");
            tsb.append("weCity", "weCity");
            tsb.append("weRegion", "weRegion");
            tsb.append("weStreet", "weStreet");
            tsb.append("weHouseNumber", "weHouseNumber");
            tsb.append("weLangCd", "weLangCd");
            tsb.append("weCustomerClassification", "weCustomerClassification");
            tsb.append("weRailwayStationId", "weRailwayStationId");
            tsb.append("weRsUicCountryCd", "weRsUicCountryCd");
            tsb.append("weRsStationNo", "weRsStationNo");
            tsb.append("weRsStationName", "weRsStationName");
            tsb.append("weRsComUicCountryCd", "weRsComUicCountryCd");
            tsb.append("weRsComStationNo", "weRsComStationNo");
            tsb.append("weRsComStationName", "weRsComStationName");
            tsb.append("weTrainStUnloadingPoint", "weTrainStUnloadingPoint");
            tsb.append("weSbbCustomerNumber", "weSbbCustomerNumber");
            tsb.append("weBpIsShipto", "weBpIsShipto");
            tsb.append("weBpIsSoldto", "weBpIsSoldto");
            tsb.append("weBpIsBillto", "weBpIsBillto");
            tsb.append("weSapBpSareaId", "weSapBpSareaId");
            tsb.append("weSareaMarkedForDeletion", "weSareaMarkedForDeletion");
            tsb.append("weSalesDistrict", "weSalesDistrict");
            tsb.append("weSofficeCd", "weSofficeCd");
            tsb.append("weSofficeName", "weSofficeName");
            tsb.append("weSgroupCd", "weSgroupCd");
            tsb.append("weSgroupName", "weSgroupName");
            tsb.append("weSgroupEmailAddresses", "weSgroupEmailAddresses");
            tsb.append("weCustomerGroup", "weCustomerGroup");
            tsb.append("weAbcClass", "weAbcClass");
            tsb.append("weCurrencyCd", "weCurrencyCd");
            tsb.append("weDistributionDestCd", "weDistributionDestCd");
            tsb.append("weCustomerOrderBlock", "weCustomerOrderBlock");
            tsb.append("weCustomerDeliveryBlock", "weCustomerDeliveryBlock");
            tsb.append("weCustomOffice", "weCustomOffice");
            tsb.append("weChromatReductionMonths", "weChromatReductionMonths");
            tsb.append("weDefaultPlantCd", "weDefaultPlantCd");
            tsb.append("weShiptoId", "weShiptoId");
            tsb.append("weActiveFlag", "weActiveFlag");
            tsb.append("weShiptoTypeCd", "weShiptoTypeCd");
            tsb.append("weSignatureTypeCd", "weSignatureTypeCd");
            tsb.append("weCoordLat", "weCoordLat");
            tsb.append("weCoordLon", "weCoordLon");
            tsb.append("weCoordFixFlag", "weCoordFixFlag");
            tsb.append("weGeofenceRadius", "weGeofenceRadius");
            tsb.append("weUnloadingTime", "weUnloadingTime");
            tsb.append("weRailChainShippingpointId", "weRailChainShippingpointId");
            tsb.append("weRailChainSpPlantCd", "weRailChainSpPlantCd");
            tsb.append("weRailChainSpSapShipptNo", "weRailChainSpSapShipptNo");
            tsb.append("weRailChainSpShipptName", "weRailChainSpShipptName");
            tsb.append("weMatChainShippingpointId", "weMatChainShippingpointId");
            tsb.append("weMatChainSpPlantCd", "weMatChainSpPlantCd");
            tsb.append("weMatChainSpSapShipptNo", "weMatChainSpSapShipptNo");
            tsb.append("weMatChainSpShipptName", "weMatChainSpShipptName");
            tsb.append("weRailHoldBackReOrder", "weRailHoldBackReOrder");
            tsb.append("weOptimalTruckTypeCd", "weOptimalTruckTypeCd");
            tsb.append("weOptimalWagonTypeCd", "weOptimalWagonTypeCd");
            tsb.append("weStSareaShiptoSareaId", "weStSareaShiptoSareaId");
            tsb.append("weStSareaFreeDelivery", "weStSareaFreeDelivery");
            tsb.append("weStSareaReleaseSapOpt", "weStSareaReleaseSapOpt");
            tsb.append("weStDistributionDestCd", "weStDistributionDestCd");
            tsb.append("oSoldtoSapBpId", "oSoldtoSapBpId");
            tsb.append("agSapId", "agSapId");
            tsb.append("agMarkedForDeletion", "agMarkedForDeletion");
            tsb.append("agName1", "agName1");
            tsb.append("agName2", "agName2");
            tsb.append("agCountry", "agCountry");
            tsb.append("agPostalCode", "agPostalCode");
            tsb.append("agCity", "agCity");
            tsb.append("agRegion", "agRegion");
            tsb.append("agStreet", "agStreet");
            tsb.append("agHouseNumber", "agHouseNumber");
            tsb.append("agLangCd", "agLangCd");
            tsb.append("agCustomerClassification", "agCustomerClassification");
            tsb.append("agRailwayStationId", "agRailwayStationId");
            tsb.append("agTrainStUnloadingPoint", "agTrainStUnloadingPoint");
            tsb.append("agSbbCustomerNumber", "agSbbCustomerNumber");
            tsb.append("agBpIsShipto", "agBpIsShipto");
            tsb.append("agBpIsSoldto", "agBpIsSoldto");
            tsb.append("agBpIsBillto", "agBpIsBillto");
            tsb.append("agSapBpSareaId", "agSapBpSareaId");
            tsb.append("agSareaMarkedForDeletion", "agSareaMarkedForDeletion");
            tsb.append("agSalesDistrict", "agSalesDistrict");
            tsb.append("agSofficeCd", "agSofficeCd");
            tsb.append("agSofficeName", "agSofficeName");
            tsb.append("agSgroupCd", "agSgroupCd");
            tsb.append("agSgroupName", "agSgroupName");
            tsb.append("agSgroupEmailAddresses", "agSgroupEmailAddresses");
            tsb.append("agCustomerGroup", "agCustomerGroup");
            tsb.append("agAbcClass", "agAbcClass");
            tsb.append("agCurrencyCd", "agCurrencyCd");
            tsb.append("agDistributionDestCd", "agDistributionDestCd");
            tsb.append("agCustomerOrderBlock", "agCustomerOrderBlock");
            tsb.append("agCustomerDeliveryBlock", "agCustomerDeliveryBlock");
            tsb.append("agCustomOffice", "agCustomOffice");
            tsb.append("agChromatReductionMonths", "agChromatReductionMonths");
            tsb.append("agDefaultPlantCd", "agDefaultPlantCd");
            tsb.append("oBilltoSapBpId", "oBilltoSapBpId");
            tsb.append("reSapId", "reSapId");
            tsb.append("reMarkedForDeletion", "reMarkedForDeletion");
            tsb.append("reName1", "reName1");
            tsb.append("reName2", "reName2");
            tsb.append("reCountry", "reCountry");
            tsb.append("rePostalCode", "rePostalCode");
            tsb.append("reCity", "reCity");
            tsb.append("reRegion", "reRegion");
            tsb.append("reStreet", "reStreet");
            tsb.append("reHouseNumber", "reHouseNumber");
            tsb.append("reLangCd", "reLangCd");
            tsb.append("reCustomerClassification", "reCustomerClassification");
            tsb.append("reRailwayStationId", "reRailwayStationId");
            tsb.append("reTrainStUnloadingPoint", "reTrainStUnloadingPoint");
            tsb.append("reSbbCustomerNumber", "reSbbCustomerNumber");
            tsb.append("reBpIsShipto", "reBpIsShipto");
            tsb.append("reBpIsSoldto", "reBpIsSoldto");
            tsb.append("reBpIsBillto", "reBpIsBillto");
            tsb.append("reSapBpSareaId", "reSapBpSareaId");
            tsb.append("reSareaMarkedForDeletion", "reSareaMarkedForDeletion");
            tsb.append("reSalesDistrict", "reSalesDistrict");
            tsb.append("reSofficeCd", "reSofficeCd");
            tsb.append("reSgroupCd", "reSgroupCd");
            tsb.append("reCustomerGroup", "reCustomerGroup");
            tsb.append("reAbcClass", "reAbcClass");
            tsb.append("reCurrencyCd", "reCurrencyCd");
            tsb.append("reDistributionDestCd", "reDistributionDestCd");
            tsb.append("reCustomerOrderBlock", "reCustomerOrderBlock");
            tsb.append("reCustomerDeliveryBlock", "reCustomerDeliveryBlock");
            tsb.append("reCustomOffice", "reCustomOffice");
            tsb.append("reChromatReductionMonths", "reChromatReductionMonths");
            tsb.append("reDefaultPlantCd", "reDefaultPlantCd");
            tsb.append("oSareaId", "oSareaId");
            tsb.append("sareaDisplayLongname", "sareaDisplayLongname");
            tsb.append("sareaSorgCd", "sareaSorgCd");
            tsb.append("sareaDistributionChannel", "sareaDistributionChannel");
            tsb.append("sareaDivisionCd", "sareaDivisionCd");
            tsb.append("sareaDivisionSegmentCd", "sareaDivisionSegmentCd");
            tsb.append("sareaCompanycodeCd", "sareaCompanycodeCd");
            tsb.append("sareaCountryCd", "sareaCountryCd");
            tsb.append("oPoolId", "oPoolId");
            tsb.append("oCommCarrierId", "oCommCarrierId");
            tsb.append("commCarrierDisplayName", "commCarrierDisplayName");
            tsb.append("commCarrierSapVendorId", "commCarrierSapVendorId");
            tsb.append("commCarrierVSapId", "commCarrierVSapId");
            tsb.append("commCarrierVAccountGroup", "commCarrierVAccountGroup");
            tsb.append("commCarrierVMarkedForDel", "commCarrierVMarkedForDel");
            tsb.append("commCarrierVName1", "commCarrierVName1");
            tsb.append("commCarrierVName2", "commCarrierVName2");
            tsb.append("commCarrierVCountryCd", "commCarrierVCountryCd");
            tsb.append("commCarrierVPostalCode", "commCarrierVPostalCode");
            tsb.append("commCarrierVCity", "commCarrierVCity");
            tsb.append("commCarrierVLocationDist", "commCarrierVLocationDist");
            tsb.append("commCarrierVRegion", "commCarrierVRegion");
            tsb.append("commCarrierVStreet", "commCarrierVStreet");
            tsb.append("commCarrierVHouseNumber", "commCarrierVHouseNumber");
            tsb.append("commCarrierVLangCd", "commCarrierVLangCd");
            tsb.append("commCarrierVVatRegistNo", "commCarrierVVatRegistNo");
            tsb.append("commCarrierIsInternal", "commCarrierIsInternal");
            tsb.append("oExecCarrierId", "oExecCarrierId");
            tsb.append("execCarrierDisplayName", "execCarrierDisplayName");
            tsb.append("execCarrierSapVendorId", "execCarrierSapVendorId");
            tsb.append("execCarrierVSapId", "execCarrierVSapId");
            tsb.append("execCarrierVAccountGroup", "execCarrierVAccountGroup");
            tsb.append("execCarrierVMarkedForDel", "execCarrierVMarkedForDel");
            tsb.append("execCarrierVName1", "execCarrierVName1");
            tsb.append("execCarrierVName2", "execCarrierVName2");
            tsb.append("execCarrierVCountryCd", "execCarrierVCountryCd");
            tsb.append("execCarrierVPostalCode", "execCarrierVPostalCode");
            tsb.append("execCarrierVCity", "execCarrierVCity");
            tsb.append("execCarrierVLocationDist", "execCarrierVLocationDist");
            tsb.append("execCarrierVRegion", "execCarrierVRegion");
            tsb.append("execCarrierVStreet", "execCarrierVStreet");
            tsb.append("execCarrierVHouseNumber", "execCarrierVHouseNumber");
            tsb.append("execCarrierVLangCd", "execCarrierVLangCd");
            tsb.append("execCarrierVVatRegistNo", "execCarrierVVatRegistNo");
            tsb.append("execCarrierIsInternal", "execCarrierIsInternal");
            tsb.append("oShippingpointId", "oShippingpointId");
            tsb.append("spPlantCd", "spPlantCd");
            tsb.append("spPlantName", "spPlantName");
            tsb.append("spSapShippingpointNo", "spSapShippingpointNo");
            tsb.append("spShippingpointName", "spShippingpointName");
            tsb.append("spShipppointTypeCd", "spShipppointTypeCd");
            tsb.append("spSapShippingCondCd", "spSapShippingCondCd");
            tsb.append("spStorageLocation", "spStorageLocation");
            tsb.append("spRailwayStationId", "spRailwayStationId");
            tsb.append("spRsUicCountryCd", "spRsUicCountryCd");
            tsb.append("spRsRailwayStationNo", "spRsRailwayStationNo");
            tsb.append("spRsRailwayStationName", "spRsRailwayStationName");
            tsb.append("spRailwayStationLp", "spRailwayStationLp");
            tsb.append("spWeighSystemSpCd", "spWeighSystemSpCd");
            tsb.append("spLdsServer", "spLdsServer");
            tsb.append("spShippingpointComment", "spShippingpointComment");
            tsb.append("spCoordLat", "spCoordLat");
            tsb.append("spCoordLon", "spCoordLon");
            tsb.append("spCoordFixFlag", "spCoordFixFlag");
            tsb.append("spGeofenceRadius", "spGeofenceRadius");
            tsb.append("spFvlMixShiptoPerWagonFl", "spFvlMixShiptoPerWagonFl");
            tsb.append("spSendSapShippingpointId", "spSendSapShippingpointId");
            tsb.append("spSendSapSpPlantCd", "spSendSapSpPlantCd");
            tsb.append("spSendSapSpSapShipptNo", "spSendSapSpSapShipptNo");
            tsb.append("spSendSapSpShipptName", "spSendSapSpShipptName");
            tsb.append("oActDepRailwayStationId", "oActDepRailwayStationId");
            tsb.append("oActDepRsUicCountryCd", "oActDepRsUicCountryCd");
            tsb.append("oActDepRsStationNo", "oActDepRsStationNo");
            tsb.append("oActDepRsStationName", "oActDepRsStationName");
            tsb.append("oActDepRsComUicCntryCd", "oActDepRsComUicCntryCd");
            tsb.append("oActDepRsComStationNo", "oActDepRsComStationNo");
            tsb.append("oActDepRsComStationName", "oActDepRsComStationName");
            tsb.append("oOrderCreationTypeCd", "oOrderCreationTypeCd");
            tsb.append("oDeliveryTypeCd", "oDeliveryTypeCd");
            tsb.append("oDistributionDestCd", "oDistributionDestCd");
            tsb.append("oCalcStartLoadFromDat", "oCalcStartLoadFromDat");
            tsb.append("oCalcStartLoadToDat", "oCalcStartLoadToDat");
            tsb.append("oPlanLoadingTimeMin", "oPlanLoadingTimeMin");
            tsb.append("oCalcEndLoadFromDat", "oCalcEndLoadFromDat");
            tsb.append("oCalcEndLoadToDat", "oCalcEndLoadToDat");
            tsb.append("oActualStartLoadDat", "oActualStartLoadDat");
            tsb.append("oActualEndLoadDat", "oActualEndLoadDat");
            tsb.append("oCalcLateStartLoadDat", "oCalcLateStartLoadDat");
            tsb.append("oCalcLateEndLoadDat", "oCalcLateEndLoadDat");
            tsb.append("oDeliveryFromDat", "oDeliveryFromDat");
            tsb.append("oDeliveryToDat", "oDeliveryToDat");
            tsb.append("oDeliveryNetTimeWindow", "oDeliveryNetTimeWindow");
            tsb.append("oActualDeliveryDat", "oActualDeliveryDat");
            tsb.append("oCalcLateDeliveryFlag", "oCalcLateDeliveryFlag");
            tsb.append("oTranspDurPlanGoogleMin", "oTranspDurPlanGoogleMin");
            tsb.append("oTranspDurPlanMin", "oTranspDurPlanMin");
            tsb.append("oTranspDistancePlanM", "oTranspDistancePlanM");
            tsb.append("oCustomsNotificationNumber", "oCustomsNotificationNumber");
            tsb.append("oDeliveryTrspZoneCd", "oDeliveryTrspZoneCd");
            tsb.append("oSpiCd", "oSpiCd");
            tsb.append("oContactPersonTxt", "oContactPersonTxt");
            tsb.append("oContactPersonSignatureTxt", "oContactPersonSignatureTxt");
            tsb.append("oNoPdaOrRailEventsFlag", "oNoPdaOrRailEventsFlag");
            tsb.append("oReleaseSapFlag", "oReleaseSapFlag");
            tsb.append("oForceToSapFlag", "oForceToSapFlag");
            tsb.append("oFreeDeliveryFlag", "oFreeDeliveryFlag");
            tsb.append("oSapSuppressMinChargeFlag", "oSapSuppressMinChargeFlag");
            tsb.append("oOrderConfEmailFlag", "oOrderConfEmailFlag");
            tsb.append("oOrderConfEmailAdresses", "oOrderConfEmailAdresses");
            tsb.append("oOrderConfFaxFlag", "oOrderConfFaxFlag");
            tsb.append("oOrderConfFaxNumber", "oOrderConfFaxNumber");
            tsb.append("oOrderConfSmsFlag", "oOrderConfSmsFlag");
            tsb.append("oOrderConfSmsNumber", "oOrderConfSmsNumber");
            tsb.append("oDelivnoteEmailFlag", "oDelivnoteEmailFlag");
            tsb.append("oDelivnoteFaxFlag", "oDelivnoteFaxFlag");
            tsb.append("oDelivnoteSinglePaperFlag", "oDelivnoteSinglePaperFlag");
            tsb.append("oCustWeborderNote", "oCustWeborderNote");
            tsb.append("oNoteOrderMgmt", "oNoteOrderMgmt");
            tsb.append("oNoteDelivery", "oNoteDelivery");
            tsb.append("oNoteDispatching", "oNoteDispatching");
            tsb.append("oNoteTruckDriver", "oNoteTruckDriver");
            tsb.append("oNoteRailwayDisp", "oNoteRailwayDisp");
            tsb.append("oCarrierDeliveryNote", "oCarrierDeliveryNote");
            tsb.append("oCarrierTruckDriverNote", "oCarrierTruckDriverNote");
            tsb.append("oCarrierDispatchingNote", "oCarrierDispatchingNote");
            tsb.append("oCustDeliveryNoteComment", "oCustDeliveryNoteComment");
            tsb.append("oDeliveredTruckId", "oDeliveredTruckId");
            tsb.append("truckName", "truckName");
            tsb.append("truckPlateNo", "truckPlateNo");
            tsb.append("truckTransportCatCd", "truckTransportCatCd");
            tsb.append("truckTypeCd", "truckTypeCd");
            tsb.append("truckMaxGrossWeightT", "truckMaxGrossWeightT");
            tsb.append("truckTareWeightT", "truckTareWeightT");
            tsb.append("truckNrChambers", "truckNrChambers");
            tsb.append("truckMaxNetWChamber1T", "truckMaxNetWChamber1T");
            tsb.append("truckMaxNetWChamber2T", "truckMaxNetWChamber2T");
            tsb.append("truckSapInternalFlag", "truckSapInternalFlag");
            tsb.append("oDeliveredWagonId", "oDeliveredWagonId");
            tsb.append("wagonNo", "wagonNo");
            tsb.append("wagonTypeCd", "wagonTypeCd");
            tsb.append("wagonTypeName", "wagonTypeName");
            tsb.append("wagonUicClassCd", "wagonUicClassCd");
            tsb.append("wagonAxisCount", "wagonAxisCount");
            tsb.append("wagonTrspCatCd", "wagonTrspCatCd");
            tsb.append("wagonLimitCreditNoteT", "wagonLimitCreditNoteT");
            tsb.append("wagonTareWeightT", "wagonTareWeightT");
            tsb.append("wagonMaxGrossWeightT", "wagonMaxGrossWeightT");
            tsb.append("wagonVolumeM3", "wagonVolumeM3");
            tsb.append("oDeliveredSimpleVehicleId", "oDeliveredSimpleVehicleId");
            tsb.append("vehicleName", "vehicleName");
            tsb.append("vehicleRegNo", "vehicleRegNo");
            tsb.append("vehicleTransportCatCd", "vehicleTransportCatCd");
            tsb.append("vehicleTypeCd", "vehicleTypeCd");
            tsb.append("vehicleMaxGrossWeightT", "vehicleMaxGrossWeightT");
            tsb.append("vehicleTareWeightT", "vehicleTareWeightT");
            tsb.append("vehicleNrChambers", "vehicleNrChambers");
            tsb.append("oDeliveredDriverId", "oDeliveredDriverId");
            tsb.append("driverName", "driverName");
            tsb.append("driverFirstname", "driverFirstname");
            tsb.append("driverUserTypeCd", "driverUserTypeCd");
            tsb.append("driverSapBpNo", "driverSapBpNo");
            tsb.append("driverSapVendorNo", "driverSapVendorNo");
            tsb.append("driverCertifiedFromDat", "driverCertifiedFromDat");
            tsb.append("driverCertifiedToDat", "driverCertifiedToDat");
            tsb.append("oSentSalesdocToSapTs", "oSentSalesdocToSapTs");
            tsb.append("oSentShipmentdocToSapTs", "oSentShipmentdocToSapTs");
            tsb.append("asspLoadingPlannedStart", "asspLoadingPlannedStart");
            tsb.append("asspLoadingRealStart", "asspLoadingRealStart");
            tsb.append("asspUnloadingPlannedStart", "asspUnloadingPlannedStart");
            tsb.append("asspUnloadingRealStart", "asspUnloadingRealStart");
            tsb.append("bizNewTs", "bizNewTs");
            tsb.append("bizTransmPoolTs", "bizTransmPoolTs");
            tsb.append("bizTransmPoolAckTs", "bizTransmPoolAckTs");
            tsb.append("bizTransmCommCarrierTs", "bizTransmCommCarrierTs");
            tsb.append("bizTransmCommCarrierAckTs", "bizTransmCommCarrierAckTs");
            tsb.append("bizTransmExecCarrierTs", "bizTransmExecCarrierTs");
            tsb.append("bizDispatchedTs", "bizDispatchedTs");
            tsb.append("bizTransmTruckTs", "bizTransmTruckTs");
            tsb.append("bizTransmTruckAckTs", "bizTransmTruckAckTs");
            tsb.append("bizReactTimeOrdMngMin", "bizReactTimeOrdMngMin");
            tsb.append("bizReactTimePoolMngMin", "bizReactTimePoolMngMin");
            tsb.append("bizReactTimeDispatcherMin", "bizReactTimeDispatcherMin");
            tsb.append("bizReactTimeTruckDrvMin", "bizReactTimeTruckDrvMin");
            tsb.append("bizReactTimeNetWindowMin", "bizReactTimeNetWindowMin");
            tsb.append("bizEnterSpTs", "bizEnterSpTs");
            tsb.append("bizStartLoadTs", "bizStartLoadTs");
            tsb.append("bizWaitingtimeReasonSpCd", "bizWaitingtimeReasonSpCd");
            tsb.append("bizEndLoadTs", "bizEndLoadTs");
            tsb.append("bizLoadingTimeMin", "bizLoadingTimeMin");
            tsb.append("bizLeaveSpTs", "bizLeaveSpTs");
            tsb.append("bizCycleTimeAtSpMin", "bizCycleTimeAtSpMin");
            tsb.append("bizTransRailCompanyTs", "bizTransRailCompanyTs");
            tsb.append("bizTrafficjamCount", "bizTrafficjamCount");
            tsb.append("bizTrafficjamDurationMin", "bizTrafficjamDurationMin");
            tsb.append("bizBreakDrivingCount", "bizBreakDrivingCount");
            tsb.append("bizBreakDrivingDurationMin", "bizBreakDrivingDurationMin");
            tsb.append("bizEnterStTs", "bizEnterStTs");
            tsb.append("bizCalcDrivingTimeMin", "bizCalcDrivingTimeMin");
            tsb.append("bizCalcNetDrivingTimeMin", "bizCalcNetDrivingTimeMin");
            tsb.append("bizActualDistanceM", "bizActualDistanceM");
            tsb.append("bizStartUnloadTs", "bizStartUnloadTs");
            tsb.append("bizWaitingtimeReasonStCd", "bizWaitingtimeReasonStCd");
            tsb.append("bizEndUnloadTs", "bizEndUnloadTs");
            tsb.append("bizUnloadingMin", "bizUnloadingMin");
            tsb.append("bizLeaveStTs", "bizLeaveStTs");
            tsb.append("bizCycleTimeAtShiptoMin", "bizCycleTimeAtShiptoMin");
            tsb.append("bizGeoeventCount", "bizGeoeventCount");
            tsb.append("oShortTermOrderFlag", "oShortTermOrderFlag");
            tsb.append("oTripIncInStatisticsFlag", "oTripIncInStatisticsFlag");
            tsb.append("oReschedulingFlag", "oReschedulingFlag");
            tsb.append("oLnfCoordLat", "oLnfCoordLat");
            tsb.append("oLnfCoordLon", "oLnfCoordLon");
            tsb.append("oLnfSiteName", "oLnfSiteName");
            tsb.append("oLnfSiteStreetHouseNo", "oLnfSiteStreetHouseNo");
            tsb.append("oLnfSiteCountry", "oLnfSiteCountry");
            tsb.append("oLnfSitePostalCode", "oLnfSitePostalCode");
            tsb.append("oLnfSiteCity", "oLnfSiteCity");
            tsb.append("oLnfSiteVatNo", "oLnfSiteVatNo");
            tsb.append("oOneTimeSoldtoName", "oOneTimeSoldtoName");
            tsb.append("oOneTimeSoldtoStreetHno", "oOneTimeSoldtoStreetHno");
            tsb.append("oOneTimeSoldtoCity", "oOneTimeSoldtoCity");
            tsb.append("oOneTimeSoldtoPostalCode", "oOneTimeSoldtoPostalCode");
            tsb.append("oOneTimeSoldtoCountry", "oOneTimeSoldtoCountry");
            tsb.append("oOneTimeSoldtoVatNo", "oOneTimeSoldtoVatNo");
            tsb.append("reportSvcLevelRelevantFlag", "reportSvcLevelRelevantFlag");
            tsb.append("reportNoteFromCrh", "reportNoteFromCrh");
            tsb.append("reportNoteFromCarrier", "reportNoteFromCarrier");
            tsb.append("reportFinishedCommFlag", "reportFinishedCommFlag");
            tsb.append("oCtlCreUid", "oCtlCreUid");
            tsb.append("oCtlCreUidName", "oCtlCreUidName");
            tsb.append("oCtlCreTs", "oCtlCreTs");
            tsb.append("oCtlModUid", "oCtlModUid");
            tsb.append("oCtlModUidName", "oCtlModUidName");
            tsb.append("oCtlModTs", "oCtlModTs");
            tsb.append("oBpCorrectedFlag", "oBpCorrectedFlag");
            tsb.append("extraCostWaitingTimeMin", "extraCostWaitingTimeMin");
            tsb.append("extraCost1", "extraCost1");
            tsb.append("extraCost2", "extraCost2");
            tsb.append("extraCost3", "extraCost3");
            tsb.append("extraCost4", "extraCost4");
            tsb.append("oDelivTypePlanActual", "oDelivTypePlanActual");
            tsb.append("oArrivalTs", "oArrivalTs");
            tsb.append("ctlCreUid", "ctlCreUid");
            tsb.append("ctlCreTs", "ctlCreTs");
            tsb.append("ctlModUid", "ctlModUid");
            tsb.append("ctlModTs", "ctlModTs");
            tsb.append("ctlTcn", "ctlTcn");
        }
        else if (LOG_ENTITIES.isInfoEnabled()) {
            tsb.append("orderFinalId", orderFinalId); // ID
        }
        return tsb.toString();
    }

    /* convenience:
        orderFinalEntity.getOOrderId();
        orderFinalEntity.getOOrderNumber();
        orderFinalEntity.getOOrderStatusCd();
        orderFinalEntity.getOShiptoSapBpId();
        orderFinalEntity.getWeSapId();
        orderFinalEntity.getWeMarkedForDeletion();
        orderFinalEntity.getWeName1();
        orderFinalEntity.getWeName2();
        orderFinalEntity.getWeCountry();
        orderFinalEntity.getWePostalCode();
        orderFinalEntity.getWeCity();
        orderFinalEntity.getWeRegion();
        orderFinalEntity.getWeStreet();
        orderFinalEntity.getWeHouseNumber();
        orderFinalEntity.getWeLangCd();
        orderFinalEntity.getWeCustomerClassification();
        orderFinalEntity.getWeRailwayStationId();
        orderFinalEntity.getWeRsUicCountryCd();
        orderFinalEntity.getWeRsStationNo();
        orderFinalEntity.getWeRsStationName();
        orderFinalEntity.getWeRsComUicCountryCd();
        orderFinalEntity.getWeRsComStationNo();
        orderFinalEntity.getWeRsComStationName();
        orderFinalEntity.getWeTrainStUnloadingPoint();
        orderFinalEntity.getWeSbbCustomerNumber();
        orderFinalEntity.getWeBpIsShipto();
        orderFinalEntity.getWeBpIsSoldto();
        orderFinalEntity.getWeBpIsBillto();
        orderFinalEntity.getWeSapBpSareaId();
        orderFinalEntity.getWeSareaMarkedForDeletion();
        orderFinalEntity.getWeSalesDistrict();
        orderFinalEntity.getWeSofficeCd();
        orderFinalEntity.getWeSofficeName();
        orderFinalEntity.getWeSgroupCd();
        orderFinalEntity.getWeSgroupName();
        orderFinalEntity.getWeSgroupEmailAddresses();
        orderFinalEntity.getWeCustomerGroup();
        orderFinalEntity.getWeAbcClass();
        orderFinalEntity.getWeCurrencyCd();
        orderFinalEntity.getWeDistributionDestCd();
        orderFinalEntity.getWeCustomerOrderBlock();
        orderFinalEntity.getWeCustomerDeliveryBlock();
        orderFinalEntity.getWeCustomOffice();
        orderFinalEntity.getWeChromatReductionMonths();
        orderFinalEntity.getWeDefaultPlantCd();
        orderFinalEntity.getWeShiptoId();
        orderFinalEntity.getWeActiveFlag();
        orderFinalEntity.getWeShiptoTypeCd();
        orderFinalEntity.getWeSignatureTypeCd();
        orderFinalEntity.getWeCoordLat();
        orderFinalEntity.getWeCoordLon();
        orderFinalEntity.getWeCoordFixFlag();
        orderFinalEntity.getWeGeofenceRadius();
        orderFinalEntity.getWeUnloadingTime();
        orderFinalEntity.getWeRailChainShippingpointId();
        orderFinalEntity.getWeRailChainSpPlantCd();
        orderFinalEntity.getWeRailChainSpSapShipptNo();
        orderFinalEntity.getWeRailChainSpShipptName();
        orderFinalEntity.getWeMatChainShippingpointId();
        orderFinalEntity.getWeMatChainSpPlantCd();
        orderFinalEntity.getWeMatChainSpSapShipptNo();
        orderFinalEntity.getWeMatChainSpShipptName();
        orderFinalEntity.getWeRailHoldBackReOrder();
        orderFinalEntity.getWeOptimalTruckTypeCd();
        orderFinalEntity.getWeOptimalWagonTypeCd();
        orderFinalEntity.getWeStSareaShiptoSareaId();
        orderFinalEntity.getWeStSareaFreeDelivery();
        orderFinalEntity.getWeStSareaReleaseSapOpt();
        orderFinalEntity.getWeStDistributionDestCd();
        orderFinalEntity.getOSoldtoSapBpId();
        orderFinalEntity.getAgSapId();
        orderFinalEntity.getAgMarkedForDeletion();
        orderFinalEntity.getAgName1();
        orderFinalEntity.getAgName2();
        orderFinalEntity.getAgCountry();
        orderFinalEntity.getAgPostalCode();
        orderFinalEntity.getAgCity();
        orderFinalEntity.getAgRegion();
        orderFinalEntity.getAgStreet();
        orderFinalEntity.getAgHouseNumber();
        orderFinalEntity.getAgLangCd();
        orderFinalEntity.getAgCustomerClassification();
        orderFinalEntity.getAgRailwayStationId();
        orderFinalEntity.getAgTrainStUnloadingPoint();
        orderFinalEntity.getAgSbbCustomerNumber();
        orderFinalEntity.getAgBpIsShipto();
        orderFinalEntity.getAgBpIsSoldto();
        orderFinalEntity.getAgBpIsBillto();
        orderFinalEntity.getAgSapBpSareaId();
        orderFinalEntity.getAgSareaMarkedForDeletion();
        orderFinalEntity.getAgSalesDistrict();
        orderFinalEntity.getAgSofficeCd();
        orderFinalEntity.getAgSofficeName();
        orderFinalEntity.getAgSgroupCd();
        orderFinalEntity.getAgSgroupName();
        orderFinalEntity.getAgSgroupEmailAddresses();
        orderFinalEntity.getAgCustomerGroup();
        orderFinalEntity.getAgAbcClass();
        orderFinalEntity.getAgCurrencyCd();
        orderFinalEntity.getAgDistributionDestCd();
        orderFinalEntity.getAgCustomerOrderBlock();
        orderFinalEntity.getAgCustomerDeliveryBlock();
        orderFinalEntity.getAgCustomOffice();
        orderFinalEntity.getAgChromatReductionMonths();
        orderFinalEntity.getAgDefaultPlantCd();
        orderFinalEntity.getOBilltoSapBpId();
        orderFinalEntity.getReSapId();
        orderFinalEntity.getReMarkedForDeletion();
        orderFinalEntity.getReName1();
        orderFinalEntity.getReName2();
        orderFinalEntity.getReCountry();
        orderFinalEntity.getRePostalCode();
        orderFinalEntity.getReCity();
        orderFinalEntity.getReRegion();
        orderFinalEntity.getReStreet();
        orderFinalEntity.getReHouseNumber();
        orderFinalEntity.getReLangCd();
        orderFinalEntity.getReCustomerClassification();
        orderFinalEntity.getReRailwayStationId();
        orderFinalEntity.getReTrainStUnloadingPoint();
        orderFinalEntity.getReSbbCustomerNumber();
        orderFinalEntity.getReBpIsShipto();
        orderFinalEntity.getReBpIsSoldto();
        orderFinalEntity.getReBpIsBillto();
        orderFinalEntity.getReSapBpSareaId();
        orderFinalEntity.getReSareaMarkedForDeletion();
        orderFinalEntity.getReSalesDistrict();
        orderFinalEntity.getReSofficeCd();
        orderFinalEntity.getReSgroupCd();
        orderFinalEntity.getReCustomerGroup();
        orderFinalEntity.getReAbcClass();
        orderFinalEntity.getReCurrencyCd();
        orderFinalEntity.getReDistributionDestCd();
        orderFinalEntity.getReCustomerOrderBlock();
        orderFinalEntity.getReCustomerDeliveryBlock();
        orderFinalEntity.getReCustomOffice();
        orderFinalEntity.getReChromatReductionMonths();
        orderFinalEntity.getReDefaultPlantCd();
        orderFinalEntity.getOSareaId();
        orderFinalEntity.getSareaDisplayLongname();
        orderFinalEntity.getSareaSorgCd();
        orderFinalEntity.getSareaDistributionChannel();
        orderFinalEntity.getSareaDivisionCd();
        orderFinalEntity.getOPoolId();
        orderFinalEntity.getOCommCarrierId();
        orderFinalEntity.getCommCarrierDisplayName();
        orderFinalEntity.getCommCarrierSapVendorId();
        orderFinalEntity.getCommCarrierVSapId();
        orderFinalEntity.getCommCarrierVAccountGroup();
        orderFinalEntity.getCommCarrierVMarkedForDel();
        orderFinalEntity.getCommCarrierVName1();
        orderFinalEntity.getCommCarrierVName2();
        orderFinalEntity.getCommCarrierVCountryCd();
        orderFinalEntity.getCommCarrierVPostalCode();
        orderFinalEntity.getCommCarrierVCity();
        orderFinalEntity.getCommCarrierVLocationDist();
        orderFinalEntity.getCommCarrierVRegion();
        orderFinalEntity.getCommCarrierVStreet();
        orderFinalEntity.getCommCarrierVHouseNumber();
        orderFinalEntity.getCommCarrierVLangCd();
        orderFinalEntity.getCommCarrierVVatRegistNo();
        orderFinalEntity.getCommCarrierIsInternal();
        orderFinalEntity.getOExecCarrierId();
        orderFinalEntity.getExecCarrierDisplayName();
        orderFinalEntity.getExecCarrierSapVendorId();
        orderFinalEntity.getExecCarrierVSapId();
        orderFinalEntity.getExecCarrierVAccountGroup();
        orderFinalEntity.getExecCarrierVMarkedForDel();
        orderFinalEntity.getExecCarrierVName1();
        orderFinalEntity.getExecCarrierVName2();
        orderFinalEntity.getExecCarrierVCountryCd();
        orderFinalEntity.getExecCarrierVPostalCode();
        orderFinalEntity.getExecCarrierVCity();
        orderFinalEntity.getExecCarrierVLocationDist();
        orderFinalEntity.getExecCarrierVRegion();
        orderFinalEntity.getExecCarrierVStreet();
        orderFinalEntity.getExecCarrierVHouseNumber();
        orderFinalEntity.getExecCarrierVLangCd();
        orderFinalEntity.getExecCarrierVVatRegistNo();
        orderFinalEntity.getExecCarrierIsInternal();
        orderFinalEntity.getOShippingpointId();
        orderFinalEntity.getSpPlantCd();
        orderFinalEntity.getSpPlantName();
        orderFinalEntity.getSpSapShippingpointNo();
        orderFinalEntity.getSpShippingpointName();
        orderFinalEntity.getSpShipppointTypeCd();
        orderFinalEntity.getSpSapShippingCondCd();
        orderFinalEntity.getSpStorageLocation();
        orderFinalEntity.getSpRailwayStationId();
        orderFinalEntity.getSpRsUicCountryCd();
        orderFinalEntity.getSpRsRailwayStationNo();
        orderFinalEntity.getSpRsRailwayStationName();
        orderFinalEntity.getSpRailwayStationLp();
        orderFinalEntity.getSpWeighSystemSpCd();
        orderFinalEntity.getSpLdsServer();
        orderFinalEntity.getSpShippingpointComment();
        orderFinalEntity.getSpCoordLat();
        orderFinalEntity.getSpCoordLon();
        orderFinalEntity.getSpCoordFixFlag();
        orderFinalEntity.getSpGeofenceRadius();
        orderFinalEntity.getSpFvlMixShiptoPerWagonFl();
        orderFinalEntity.getSpSendSapShippingpointId();
        orderFinalEntity.getSpSendSapSpPlantCd();
        orderFinalEntity.getSpSendSapSpSapShipptNo();
        orderFinalEntity.getSpSendSapSpShipptName();
        orderFinalEntity.getOActDepRailwayStationId();
        orderFinalEntity.getOActDepRsUicCountryCd();
        orderFinalEntity.getOActDepRsStationNo();
        orderFinalEntity.getOActDepRsStationName();
        orderFinalEntity.getOActDepRsComUicCntryCd();
        orderFinalEntity.getOActDepRsComStationNo();
        orderFinalEntity.getOActDepRsComStationName();
        orderFinalEntity.getOOrderCreationTypeCd();
        orderFinalEntity.getODeliveryTypeCd();
        orderFinalEntity.getODistributionDestCd();
        orderFinalEntity.getOCalcStartLoadFromDat();
        orderFinalEntity.getOCalcStartLoadToDat();
        orderFinalEntity.getOPlanLoadingTimeMin();
        orderFinalEntity.getOCalcEndLoadFromDat();
        orderFinalEntity.getOCalcEndLoadToDat();
        orderFinalEntity.getOActualStartLoadDat();
        orderFinalEntity.getOActualEndLoadDat();
        orderFinalEntity.getOCalcLateStartLoadDat();
        orderFinalEntity.getOCalcLateEndLoadDat();
        orderFinalEntity.getODeliveryFromDat();
        orderFinalEntity.getODeliveryToDat();
        orderFinalEntity.getODeliveryNetTimeWindow();
        orderFinalEntity.getOActualDeliveryDat();
        orderFinalEntity.getOCalcLateDeliveryFlag();
        orderFinalEntity.getOTranspDurPlanGoogleMin();
        orderFinalEntity.getOTranspDurPlanMin();
        orderFinalEntity.getOTranspDistancePlanM();
        orderFinalEntity.getOCustomsNotificationNumber();
        orderFinalEntity.getODeliveryTrspZoneCd();
        orderFinalEntity.getOSpiCd();
        orderFinalEntity.getOContactPersonTxt();
        orderFinalEntity.getOContactPersonSignatureTxt();
        orderFinalEntity.getONoPdaOrRailEventsFlag();
        orderFinalEntity.getOReleaseSapFlag();
        orderFinalEntity.getOFreeDeliveryFlag();
        orderFinalEntity.getOSapSuppressMinChargeFlag();
        orderFinalEntity.getOOrderConfEmailFlag();
        orderFinalEntity.getOOrderConfEmailAdresses();
        orderFinalEntity.getOOrderConfFaxFlag();
        orderFinalEntity.getOOrderConfFaxNumber();
        orderFinalEntity.getOOrderConfSmsFlag();
        orderFinalEntity.getOOrderConfSmsNumber();
        orderFinalEntity.getODelivnoteEmailFlag();
        orderFinalEntity.getODelivnoteFaxFlag();
        orderFinalEntity.getODelivnoteSinglePaperFlag();
        orderFinalEntity.getOCustWeborderNote();
        orderFinalEntity.getONoteOrderMgmt();
        orderFinalEntity.getONoteDelivery();
        orderFinalEntity.getONoteDispatching();
        orderFinalEntity.getONoteTruckDriver();
        orderFinalEntity.getONoteRailwayDisp();
        orderFinalEntity.getOCarrierDeliveryNote();
        orderFinalEntity.getOCarrierTruckDriverNote();
        orderFinalEntity.getOCarrierDispatchingNote();
        orderFinalEntity.getOCustDeliveryNoteComment();
        orderFinalEntity.getODeliveredTruckId();
        orderFinalEntity.getTruckName();
        orderFinalEntity.getTruckPlateNo();
        orderFinalEntity.getTruckTransportCatCd();
        orderFinalEntity.getTruckTypeCd();
        orderFinalEntity.getTruckMaxGrossWeightT();
        orderFinalEntity.getTruckTareWeightT();
        orderFinalEntity.getTruckNrChambers();
        orderFinalEntity.getTruckMaxNetWChamber1T();
        orderFinalEntity.getTruckMaxNetWChamber2T();
        orderFinalEntity.getTruckSapInternalFlag();
        orderFinalEntity.getODeliveredWagonId();
        orderFinalEntity.getWagonNo();
        orderFinalEntity.getWagonTypeCd();
        orderFinalEntity.getWagonTypeName();
        orderFinalEntity.getWagonUicClassCd();
        orderFinalEntity.getWagonAxisCount();
        orderFinalEntity.getWagonTrspCatCd();
        orderFinalEntity.getWagonLimitCreditNoteT();
        orderFinalEntity.getWagonTareWeightT();
        orderFinalEntity.getWagonMaxGrossWeightT();
        orderFinalEntity.getWagonVolumeM3();
        orderFinalEntity.getODeliveredSimpleVehicleId();
        orderFinalEntity.getVehicleName();
        orderFinalEntity.getVehicleRegNo();
        orderFinalEntity.getVehicleTransportCatCd();
        orderFinalEntity.getVehicleTypeCd();
        orderFinalEntity.getVehicleMaxGrossWeightT();
        orderFinalEntity.getVehicleTareWeightT();
        orderFinalEntity.getVehicleNrChambers();
        orderFinalEntity.getODeliveredDriverId();
        orderFinalEntity.getDriverName();
        orderFinalEntity.getDriverFirstname();
        orderFinalEntity.getDriverUserTypeCd();
        orderFinalEntity.getDriverSapBpNo();
        orderFinalEntity.getDriverSapVendorNo();
        orderFinalEntity.getDriverCertifiedFromDat();
        orderFinalEntity.getDriverCertifiedToDat();
        orderFinalEntity.getOSentSalesdocToSapTs();
        orderFinalEntity.getOSentShipmentdocToSapTs();
        orderFinalEntity.getAsspLoadingPlannedStart();
        orderFinalEntity.getAsspLoadingRealStart();
        orderFinalEntity.getAsspUnloadingPlannedStart();
        orderFinalEntity.getAsspUnloadingRealStart();
        orderFinalEntity.getBizNewTs();
        orderFinalEntity.getBizTransmPoolTs();
        orderFinalEntity.getBizTransmPoolAckTs();
        orderFinalEntity.getBizTransmCommCarrierTs();
        orderFinalEntity.getBizTransmCommCarrierAckTs();
        orderFinalEntity.getBizTransmExecCarrierTs();
        orderFinalEntity.getBizDispatchedTs();
        orderFinalEntity.getBizTransmTruckTs();
        orderFinalEntity.getBizTransmTruckAckTs();
        orderFinalEntity.getBizReactTimeOrdMngMin();
        orderFinalEntity.getBizReactTimePoolMngMin();
        orderFinalEntity.getBizReactTimeDispatcherMin();
        orderFinalEntity.getBizReactTimeTruckDrvMin();
        orderFinalEntity.getBizReactTimeNetWindowMin();
        orderFinalEntity.getBizEnterSpTs();
        orderFinalEntity.getBizStartLoadTs();
        orderFinalEntity.getBizWaitingtimeReasonSpCd();
        orderFinalEntity.getBizEndLoadTs();
        orderFinalEntity.getBizLoadingTimeMin();
        orderFinalEntity.getBizLeaveSpTs();
        orderFinalEntity.getBizCycleTimeAtSpMin();
        orderFinalEntity.getBizTransRailCompanyTs();
        orderFinalEntity.getBizTrafficjamCount();
        orderFinalEntity.getBizTrafficjamDurationMin();
        orderFinalEntity.getBizBreakDrivingCount();
        orderFinalEntity.getBizBreakDrivingDurationMin();
        orderFinalEntity.getBizEnterStTs();
        orderFinalEntity.getBizCalcDrivingTimeMin();
        orderFinalEntity.getBizCalcNetDrivingTimeMin();
        orderFinalEntity.getBizActualDistanceM();
        orderFinalEntity.getBizStartUnloadTs();
        orderFinalEntity.getBizWaitingtimeReasonStCd();
        orderFinalEntity.getBizEndUnloadTs();
        orderFinalEntity.getBizUnloadingMin();
        orderFinalEntity.getBizLeaveStTs();
        orderFinalEntity.getBizCycleTimeAtShiptoMin();
        orderFinalEntity.getBizGeoeventCount();
        orderFinalEntity.getOShortTermOrderFlag();
        orderFinalEntity.getOTripIncInStatisticsFlag();
        orderFinalEntity.getOReschedulingFlag();
        orderFinalEntity.getOLnfCoordLat();
        orderFinalEntity.getOLnfCoordLon();
        orderFinalEntity.getOLnfSiteName();
        orderFinalEntity.getOLnfSiteStreetHouseNo();
        orderFinalEntity.getOLnfSiteCountry();
        orderFinalEntity.getOLnfSitePostalCode();
        orderFinalEntity.getOLnfSiteCity();
        orderFinalEntity.getOLnfSiteVatNo();
        orderFinalEntity.getOOneTimeSoldtoName();
        orderFinalEntity.getOOneTimeSoldtoStreetHno();
        orderFinalEntity.getOOneTimeSoldtoCity();
        orderFinalEntity.getOOneTimeSoldtoPostalCode();
        orderFinalEntity.getOOneTimeSoldtoCountry();
        orderFinalEntity.getOOneTimeSoldtoVatNo();
        orderFinalEntity.getReportSvcLevelRelevantFlag();
        orderFinalEntity.getReportNoteFromCrh();
        orderFinalEntity.getReportNoteFromCarrier();
        orderFinalEntity.getReportFinishedCommFlag();
        orderFinalEntity.getOCtlCreUid();
        orderFinalEntity.getOCtlCreUidName();
        orderFinalEntity.getOCtlCreTs();
        orderFinalEntity.getOCtlModUid();
        orderFinalEntity.getOCtlModUidName();
        orderFinalEntity.getOCtlModTs();
        orderFinalEntity.getOBpCorrectedFlag();
        orderFinalEntity.getExtraCostWaitingTimeMin();
        orderFinalEntity.getExtraCost1();
        orderFinalEntity.getExtraCost2();
        orderFinalEntity.getExtraCost3();
        orderFinalEntity.getExtraCost4();
        orderFinalEntity.getCtlCreUid();
        orderFinalEntity.getCtlCreTs();
        orderFinalEntity.getCtlModUid();
        orderFinalEntity.getCtlModTs();
        orderFinalEntity.getCtlTcn();

        orderFinalEntity.setOOrderId(oOrderId); // NOT nullable
        orderFinalEntity.setOOrderNumber(oOrderNumber); // nullable
        orderFinalEntity.setOOrderStatusCd(oOrderStatusCd); // NOT nullable
        orderFinalEntity.setOShiptoSapBpId(oShiptoSapBpId); // NOT nullable
        orderFinalEntity.setWeSapId(weSapId); // nullable
        orderFinalEntity.setWeMarkedForDeletion(weMarkedForDeletion); // nullable
        orderFinalEntity.setWeName1(weName1); // nullable
        orderFinalEntity.setWeName2(weName2); // nullable
        orderFinalEntity.setWeCountry(weCountry); // nullable
        orderFinalEntity.setWePostalCode(wePostalCode); // nullable
        orderFinalEntity.setWeCity(weCity); // nullable
        orderFinalEntity.setWeRegion(weRegion); // nullable
        orderFinalEntity.setWeStreet(weStreet); // nullable
        orderFinalEntity.setWeHouseNumber(weHouseNumber); // nullable
        orderFinalEntity.setWeLangCd(weLangCd); // nullable
        orderFinalEntity.setWeCustomerClassification(weCustomerClassification); // nullable
        orderFinalEntity.setWeRailwayStationId(weRailwayStationId); // nullable
        orderFinalEntity.setWeRsUicCountryCd(weRsUicCountryCd); // nullable
        orderFinalEntity.setWeRsStationNo(weRsStationNo); // nullable
        orderFinalEntity.setWeRsStationName(weRsStationName); // nullable
        orderFinalEntity.setWeRsComUicCountryCd(weRsComUicCountryCd); // nullable
        orderFinalEntity.setWeRsComStationNo(weRsComStationNo); // nullable
        orderFinalEntity.setWeRsComStationName(weRsComStationName); // nullable
        orderFinalEntity.setWeTrainStUnloadingPoint(weTrainStUnloadingPoint); // nullable
        orderFinalEntity.setWeSbbCustomerNumber(weSbbCustomerNumber); // nullable
        orderFinalEntity.setWeBpIsShipto(weBpIsShipto); // NOT nullable
        orderFinalEntity.setWeBpIsSoldto(weBpIsSoldto); // NOT nullable
        orderFinalEntity.setWeBpIsBillto(weBpIsBillto); // NOT nullable
        orderFinalEntity.setWeSapBpSareaId(weSapBpSareaId); // nullable
        orderFinalEntity.setWeSareaMarkedForDeletion(weSareaMarkedForDeletion); // nullable
        orderFinalEntity.setWeSalesDistrict(weSalesDistrict); // nullable
        orderFinalEntity.setWeSofficeCd(weSofficeCd); // nullable
        orderFinalEntity.setWeSofficeName(weSofficeName); // nullable
        orderFinalEntity.setWeSgroupCd(weSgroupCd); // nullable
        orderFinalEntity.setWeSgroupName(weSgroupName); // nullable
        orderFinalEntity.setWeSgroupEmailAddresses(weSgroupEmailAddresses); // nullable
        orderFinalEntity.setWeCustomerGroup(weCustomerGroup); // nullable
        orderFinalEntity.setWeAbcClass(weAbcClass); // nullable
        orderFinalEntity.setWeCurrencyCd(weCurrencyCd); // nullable
        orderFinalEntity.setWeDistributionDestCd(weDistributionDestCd); // nullable
        orderFinalEntity.setWeCustomerOrderBlock(weCustomerOrderBlock); // nullable
        orderFinalEntity.setWeCustomerDeliveryBlock(weCustomerDeliveryBlock); // nullable
        orderFinalEntity.setWeCustomOffice(weCustomOffice); // nullable
        orderFinalEntity.setWeChromatReductionMonths(weChromatReductionMonths); // nullable
        orderFinalEntity.setWeDefaultPlantCd(weDefaultPlantCd); // nullable
        orderFinalEntity.setWeShiptoId(weShiptoId); // nullable
        orderFinalEntity.setWeActiveFlag(weActiveFlag); // nullable
        orderFinalEntity.setWeShiptoTypeCd(weShiptoTypeCd); // nullable
        orderFinalEntity.setWeSignatureTypeCd(weSignatureTypeCd); // nullable
        orderFinalEntity.setWeCoordLat(weCoordLat); // nullable
        orderFinalEntity.setWeCoordLon(weCoordLon); // nullable
        orderFinalEntity.setWeCoordFixFlag(weCoordFixFlag); // nullable
        orderFinalEntity.setWeGeofenceRadius(weGeofenceRadius); // nullable
        orderFinalEntity.setWeUnloadingTime(weUnloadingTime); // nullable
        orderFinalEntity.setWeRailChainShippingpointId(weRailChainShippingpointId); // nullable
        orderFinalEntity.setWeRailChainSpPlantCd(weRailChainSpPlantCd); // nullable
        orderFinalEntity.setWeRailChainSpSapShipptNo(weRailChainSpSapShipptNo); // nullable
        orderFinalEntity.setWeRailChainSpShipptName(weRailChainSpShipptName); // nullable
        orderFinalEntity.setWeMatChainShippingpointId(weMatChainShippingpointId); // nullable
        orderFinalEntity.setWeMatChainSpPlantCd(weMatChainSpPlantCd); // nullable
        orderFinalEntity.setWeMatChainSpSapShipptNo(weMatChainSpSapShipptNo); // nullable
        orderFinalEntity.setWeMatChainSpShipptName(weMatChainSpShipptName); // nullable
        orderFinalEntity.setWeRailHoldBackReOrder(weRailHoldBackReOrder); // nullable
        orderFinalEntity.setWeOptimalTruckTypeCd(weOptimalTruckTypeCd); // nullable
        orderFinalEntity.setWeOptimalWagonTypeCd(weOptimalWagonTypeCd); // nullable
        orderFinalEntity.setWeStSareaShiptoSareaId(weStSareaShiptoSareaId); // nullable
        orderFinalEntity.setWeStSareaFreeDelivery(weStSareaFreeDelivery); // nullable
        orderFinalEntity.setWeStSareaReleaseSapOpt(weStSareaReleaseSapOpt); // nullable
        orderFinalEntity.setWeStDistributionDestCd(weStDistributionDestCd); // nullable
        orderFinalEntity.setOSoldtoSapBpId(oSoldtoSapBpId); // NOT nullable
        orderFinalEntity.setAgSapId(agSapId); // nullable
        orderFinalEntity.setAgMarkedForDeletion(agMarkedForDeletion); // nullable
        orderFinalEntity.setAgName1(agName1); // nullable
        orderFinalEntity.setAgName2(agName2); // nullable
        orderFinalEntity.setAgCountry(agCountry); // nullable
        orderFinalEntity.setAgPostalCode(agPostalCode); // nullable
        orderFinalEntity.setAgCity(agCity); // nullable
        orderFinalEntity.setAgRegion(agRegion); // nullable
        orderFinalEntity.setAgStreet(agStreet); // nullable
        orderFinalEntity.setAgHouseNumber(agHouseNumber); // nullable
        orderFinalEntity.setAgLangCd(agLangCd); // nullable
        orderFinalEntity.setAgCustomerClassification(agCustomerClassification); // nullable
        orderFinalEntity.setAgRailwayStationId(agRailwayStationId); // nullable
        orderFinalEntity.setAgTrainStUnloadingPoint(agTrainStUnloadingPoint); // nullable
        orderFinalEntity.setAgSbbCustomerNumber(agSbbCustomerNumber); // nullable
        orderFinalEntity.setAgBpIsShipto(agBpIsShipto); // NOT nullable
        orderFinalEntity.setAgBpIsSoldto(agBpIsSoldto); // NOT nullable
        orderFinalEntity.setAgBpIsBillto(agBpIsBillto); // NOT nullable
        orderFinalEntity.setAgSapBpSareaId(agSapBpSareaId); // nullable
        orderFinalEntity.setAgSareaMarkedForDeletion(agSareaMarkedForDeletion); // nullable
        orderFinalEntity.setAgSalesDistrict(agSalesDistrict); // nullable
        orderFinalEntity.setAgSofficeCd(agSofficeCd); // nullable
        orderFinalEntity.setAgSofficeName(agSofficeName); // nullable
        orderFinalEntity.setAgSgroupCd(agSgroupCd); // nullable
        orderFinalEntity.setAgSgroupName(agSgroupName); // nullable
        orderFinalEntity.setAgSgroupEmailAddresses(agSgroupEmailAddresses); // nullable
        orderFinalEntity.setAgCustomerGroup(agCustomerGroup); // nullable
        orderFinalEntity.setAgAbcClass(agAbcClass); // nullable
        orderFinalEntity.setAgCurrencyCd(agCurrencyCd); // nullable
        orderFinalEntity.setAgDistributionDestCd(agDistributionDestCd); // nullable
        orderFinalEntity.setAgCustomerOrderBlock(agCustomerOrderBlock); // nullable
        orderFinalEntity.setAgCustomerDeliveryBlock(agCustomerDeliveryBlock); // nullable
        orderFinalEntity.setAgCustomOffice(agCustomOffice); // nullable
        orderFinalEntity.setAgChromatReductionMonths(agChromatReductionMonths); // nullable
        orderFinalEntity.setAgDefaultPlantCd(agDefaultPlantCd); // nullable
        orderFinalEntity.setOBilltoSapBpId(oBilltoSapBpId); // nullable
        orderFinalEntity.setReSapId(reSapId); // nullable
        orderFinalEntity.setReMarkedForDeletion(reMarkedForDeletion); // nullable
        orderFinalEntity.setReName1(reName1); // nullable
        orderFinalEntity.setReName2(reName2); // nullable
        orderFinalEntity.setReCountry(reCountry); // nullable
        orderFinalEntity.setRePostalCode(rePostalCode); // nullable
        orderFinalEntity.setReCity(reCity); // nullable
        orderFinalEntity.setReRegion(reRegion); // nullable
        orderFinalEntity.setReStreet(reStreet); // nullable
        orderFinalEntity.setReHouseNumber(reHouseNumber); // nullable
        orderFinalEntity.setReLangCd(reLangCd); // nullable
        orderFinalEntity.setReCustomerClassification(reCustomerClassification); // nullable
        orderFinalEntity.setReRailwayStationId(reRailwayStationId); // nullable
        orderFinalEntity.setReTrainStUnloadingPoint(reTrainStUnloadingPoint); // nullable
        orderFinalEntity.setReSbbCustomerNumber(reSbbCustomerNumber); // nullable
        orderFinalEntity.setReBpIsShipto(reBpIsShipto); // nullable
        orderFinalEntity.setReBpIsSoldto(reBpIsSoldto); // nullable
        orderFinalEntity.setReBpIsBillto(reBpIsBillto); // nullable
        orderFinalEntity.setReSapBpSareaId(reSapBpSareaId); // nullable
        orderFinalEntity.setReSareaMarkedForDeletion(reSareaMarkedForDeletion); // nullable
        orderFinalEntity.setReSalesDistrict(reSalesDistrict); // nullable
        orderFinalEntity.setReSofficeCd(reSofficeCd); // nullable
        orderFinalEntity.setReSgroupCd(reSgroupCd); // nullable
        orderFinalEntity.setReCustomerGroup(reCustomerGroup); // nullable
        orderFinalEntity.setReAbcClass(reAbcClass); // nullable
        orderFinalEntity.setReCurrencyCd(reCurrencyCd); // nullable
        orderFinalEntity.setReDistributionDestCd(reDistributionDestCd); // nullable
        orderFinalEntity.setReCustomerOrderBlock(reCustomerOrderBlock); // nullable
        orderFinalEntity.setReCustomerDeliveryBlock(reCustomerDeliveryBlock); // nullable
        orderFinalEntity.setReCustomOffice(reCustomOffice); // nullable
        orderFinalEntity.setReChromatReductionMonths(reChromatReductionMonths); // nullable
        orderFinalEntity.setReDefaultPlantCd(reDefaultPlantCd); // nullable
        orderFinalEntity.setOSareaId(oSareaId); // NOT nullable
        orderFinalEntity.setSareaDisplayLongname(sareaDisplayLongname); // nullable
        orderFinalEntity.setSareaSorgCd(sareaSorgCd); // NOT nullable
        orderFinalEntity.setSareaDistributionChannel(sareaDistributionChannel); // NOT nullable
        orderFinalEntity.setSareaDivisionCd(sareaDivisionCd); // NOT nullable
        orderFinalEntity.setOPoolId(oPoolId); // nullable
        orderFinalEntity.setOCommCarrierId(oCommCarrierId); // nullable
        orderFinalEntity.setCommCarrierDisplayName(commCarrierDisplayName); // nullable
        orderFinalEntity.setCommCarrierSapVendorId(commCarrierSapVendorId); // nullable
        orderFinalEntity.setCommCarrierVSapId(commCarrierVSapId); // nullable
        orderFinalEntity.setCommCarrierVAccountGroup(commCarrierVAccountGroup); // nullable
        orderFinalEntity.setCommCarrierVMarkedForDel(commCarrierVMarkedForDel); // nullable
        orderFinalEntity.setCommCarrierVName1(commCarrierVName1); // nullable
        orderFinalEntity.setCommCarrierVName2(commCarrierVName2); // nullable
        orderFinalEntity.setCommCarrierVCountryCd(commCarrierVCountryCd); // nullable
        orderFinalEntity.setCommCarrierVPostalCode(commCarrierVPostalCode); // nullable
        orderFinalEntity.setCommCarrierVCity(commCarrierVCity); // nullable
        orderFinalEntity.setCommCarrierVLocationDist(commCarrierVLocationDist); // nullable
        orderFinalEntity.setCommCarrierVRegion(commCarrierVRegion); // nullable
        orderFinalEntity.setCommCarrierVStreet(commCarrierVStreet); // nullable
        orderFinalEntity.setCommCarrierVHouseNumber(commCarrierVHouseNumber); // nullable
        orderFinalEntity.setCommCarrierVLangCd(commCarrierVLangCd); // nullable
        orderFinalEntity.setCommCarrierVVatRegistNo(commCarrierVVatRegistNo); // nullable
        orderFinalEntity.setCommCarrierIsInternal(commCarrierIsInternal); // nullable
        orderFinalEntity.setOExecCarrierId(oExecCarrierId); // nullable
        orderFinalEntity.setExecCarrierDisplayName(execCarrierDisplayName); // nullable
        orderFinalEntity.setExecCarrierSapVendorId(execCarrierSapVendorId); // nullable
        orderFinalEntity.setExecCarrierVSapId(execCarrierVSapId); // nullable
        orderFinalEntity.setExecCarrierVAccountGroup(execCarrierVAccountGroup); // nullable
        orderFinalEntity.setExecCarrierVMarkedForDel(execCarrierVMarkedForDel); // nullable
        orderFinalEntity.setExecCarrierVName1(execCarrierVName1); // nullable
        orderFinalEntity.setExecCarrierVName2(execCarrierVName2); // nullable
        orderFinalEntity.setExecCarrierVCountryCd(execCarrierVCountryCd); // nullable
        orderFinalEntity.setExecCarrierVPostalCode(execCarrierVPostalCode); // nullable
        orderFinalEntity.setExecCarrierVCity(execCarrierVCity); // nullable
        orderFinalEntity.setExecCarrierVLocationDist(execCarrierVLocationDist); // nullable
        orderFinalEntity.setExecCarrierVRegion(execCarrierVRegion); // nullable
        orderFinalEntity.setExecCarrierVStreet(execCarrierVStreet); // nullable
        orderFinalEntity.setExecCarrierVHouseNumber(execCarrierVHouseNumber); // nullable
        orderFinalEntity.setExecCarrierVLangCd(execCarrierVLangCd); // nullable
        orderFinalEntity.setExecCarrierVVatRegistNo(execCarrierVVatRegistNo); // nullable
        orderFinalEntity.setExecCarrierIsInternal(execCarrierIsInternal); // nullable
        orderFinalEntity.setOShippingpointId(oShippingpointId); // nullable
        orderFinalEntity.setSpPlantCd(spPlantCd); // nullable
        orderFinalEntity.setSpPlantName(spPlantName); // nullable
        orderFinalEntity.setSpSapShippingpointNo(spSapShippingpointNo); // nullable
        orderFinalEntity.setSpShippingpointName(spShippingpointName); // nullable
        orderFinalEntity.setSpShipppointTypeCd(spShipppointTypeCd); // nullable
        orderFinalEntity.setSpSapShippingCondCd(spSapShippingCondCd); // nullable
        orderFinalEntity.setSpStorageLocation(spStorageLocation); // nullable
        orderFinalEntity.setSpRailwayStationId(spRailwayStationId); // nullable
        orderFinalEntity.setSpRsUicCountryCd(spRsUicCountryCd); // nullable
        orderFinalEntity.setSpRsRailwayStationNo(spRsRailwayStationNo); // nullable
        orderFinalEntity.setSpRsRailwayStationName(spRsRailwayStationName); // nullable
        orderFinalEntity.setSpRailwayStationLp(spRailwayStationLp); // nullable
        orderFinalEntity.setSpWeighSystemSpCd(spWeighSystemSpCd); // nullable
        orderFinalEntity.setSpLdsServer(spLdsServer); // nullable
        orderFinalEntity.setSpShippingpointComment(spShippingpointComment); // nullable
        orderFinalEntity.setSpCoordLat(spCoordLat); // nullable
        orderFinalEntity.setSpCoordLon(spCoordLon); // nullable
        orderFinalEntity.setSpCoordFixFlag(spCoordFixFlag); // nullable
        orderFinalEntity.setSpGeofenceRadius(spGeofenceRadius); // nullable
        orderFinalEntity.setSpFvlMixShiptoPerWagonFl(spFvlMixShiptoPerWagonFl); // nullable
        orderFinalEntity.setSpSendSapShippingpointId(spSendSapShippingpointId); // nullable
        orderFinalEntity.setSpSendSapSpPlantCd(spSendSapSpPlantCd); // nullable
        orderFinalEntity.setSpSendSapSpSapShipptNo(spSendSapSpSapShipptNo); // nullable
        orderFinalEntity.setSpSendSapSpShipptName(spSendSapSpShipptName); // nullable
        orderFinalEntity.setOActDepRailwayStationId(oActDepRailwayStationId); // nullable
        orderFinalEntity.setOActDepRsUicCountryCd(oActDepRsUicCountryCd); // nullable
        orderFinalEntity.setOActDepRsStationNo(oActDepRsStationNo); // nullable
        orderFinalEntity.setOActDepRsStationName(oActDepRsStationName); // nullable
        orderFinalEntity.setOActDepRsComUicCntryCd(oActDepRsComUicCntryCd); // nullable
        orderFinalEntity.setOActDepRsComStationNo(oActDepRsComStationNo); // nullable
        orderFinalEntity.setOActDepRsComStationName(oActDepRsComStationName); // nullable
        orderFinalEntity.setOOrderCreationTypeCd(oOrderCreationTypeCd); // NOT nullable
        orderFinalEntity.setODeliveryTypeCd(oDeliveryTypeCd); // NOT nullable
        orderFinalEntity.setODistributionDestCd(oDistributionDestCd); // NOT nullable
        orderFinalEntity.setOCalcStartLoadFromDat(oCalcStartLoadFromDat); // nullable
        orderFinalEntity.setOCalcStartLoadToDat(oCalcStartLoadToDat); // nullable
        orderFinalEntity.setOPlanLoadingTimeMin(oPlanLoadingTimeMin); // nullable
        orderFinalEntity.setOCalcEndLoadFromDat(oCalcEndLoadFromDat); // nullable
        orderFinalEntity.setOCalcEndLoadToDat(oCalcEndLoadToDat); // nullable
        orderFinalEntity.setOActualStartLoadDat(oActualStartLoadDat); // nullable
        orderFinalEntity.setOActualEndLoadDat(oActualEndLoadDat); // nullable
        orderFinalEntity.setOCalcLateStartLoadDat(oCalcLateStartLoadDat); // nullable
        orderFinalEntity.setOCalcLateEndLoadDat(oCalcLateEndLoadDat); // nullable
        orderFinalEntity.setODeliveryFromDat(oDeliveryFromDat); // NOT nullable
        orderFinalEntity.setODeliveryToDat(oDeliveryToDat); // NOT nullable
        orderFinalEntity.setODeliveryNetTimeWindow(oDeliveryNetTimeWindow); // nullable
        orderFinalEntity.setOActualDeliveryDat(oActualDeliveryDat); // nullable
        orderFinalEntity.setOCalcLateDeliveryFlag(oCalcLateDeliveryFlag); // nullable
        orderFinalEntity.setOTranspDurPlanGoogleMin(oTranspDurPlanGoogleMin); // nullable
        orderFinalEntity.setOTranspDurPlanMin(oTranspDurPlanMin); // nullable
        orderFinalEntity.setOTranspDistancePlanM(oTranspDistancePlanM); // nullable
        orderFinalEntity.setOCustomsNotificationNumber(oCustomsNotificationNumber); // nullable
        orderFinalEntity.setODeliveryTrspZoneCd(oDeliveryTrspZoneCd); // nullable
        orderFinalEntity.setOSpiCd(oSpiCd); // nullable
        orderFinalEntity.setOContactPersonTxt(oContactPersonTxt); // nullable
        orderFinalEntity.setOContactPersonSignatureTxt(oContactPersonSignatureTxt); // nullable
        orderFinalEntity.setONoPdaOrRailEventsFlag(oNoPdaOrRailEventsFlag); // NOT nullable
        orderFinalEntity.setOReleaseSapFlag(oReleaseSapFlag); // NOT nullable
        orderFinalEntity.setOFreeDeliveryFlag(oFreeDeliveryFlag); // NOT nullable
        orderFinalEntity.setOSapSuppressMinChargeFlag(oSapSuppressMinChargeFlag); // nullable
        orderFinalEntity.setOOrderConfEmailFlag(oOrderConfEmailFlag); // NOT nullable
        orderFinalEntity.setOOrderConfEmailAdresses(oOrderConfEmailAdresses); // nullable
        orderFinalEntity.setOOrderConfFaxFlag(oOrderConfFaxFlag); // NOT nullable
        orderFinalEntity.setOOrderConfFaxNumber(oOrderConfFaxNumber); // nullable
        orderFinalEntity.setOOrderConfSmsFlag(oOrderConfSmsFlag); // NOT nullable
        orderFinalEntity.setOOrderConfSmsNumber(oOrderConfSmsNumber); // nullable
        orderFinalEntity.setODelivnoteEmailFlag(oDelivnoteEmailFlag); // NOT nullable
        orderFinalEntity.setODelivnoteFaxFlag(oDelivnoteFaxFlag); // NOT nullable
        orderFinalEntity.setODelivnoteSinglePaperFlag(oDelivnoteSinglePaperFlag); // NOT nullable
        orderFinalEntity.setOCustWeborderNote(oCustWeborderNote); // nullable
        orderFinalEntity.setONoteOrderMgmt(oNoteOrderMgmt); // nullable
        orderFinalEntity.setONoteDelivery(oNoteDelivery); // nullable
        orderFinalEntity.setONoteDispatching(oNoteDispatching); // nullable
        orderFinalEntity.setONoteTruckDriver(oNoteTruckDriver); // nullable
        orderFinalEntity.setONoteRailwayDisp(oNoteRailwayDisp); // nullable
        orderFinalEntity.setOCarrierDeliveryNote(oCarrierDeliveryNote); // nullable
        orderFinalEntity.setOCarrierTruckDriverNote(oCarrierTruckDriverNote); // nullable
        orderFinalEntity.setOCarrierDispatchingNote(oCarrierDispatchingNote); // nullable
        orderFinalEntity.setOCustDeliveryNoteComment(oCustDeliveryNoteComment); // nullable
        orderFinalEntity.setODeliveredTruckId(oDeliveredTruckId); // nullable
        orderFinalEntity.setTruckName(truckName); // nullable
        orderFinalEntity.setTruckPlateNo(truckPlateNo); // nullable
        orderFinalEntity.setTruckTransportCatCd(truckTransportCatCd); // nullable
        orderFinalEntity.setTruckTypeCd(truckTypeCd); // nullable
        orderFinalEntity.setTruckMaxGrossWeightT(truckMaxGrossWeightT); // nullable
        orderFinalEntity.setTruckTareWeightT(truckTareWeightT); // nullable
        orderFinalEntity.setTruckNrChambers(truckNrChambers); // nullable
        orderFinalEntity.setTruckMaxNetWChamber1T(truckMaxNetWChamber1T); // nullable
        orderFinalEntity.setTruckMaxNetWChamber2T(truckMaxNetWChamber2T); // nullable
        orderFinalEntity.setTruckSapInternalFlag(truckSapInternalFlag); // nullable
        orderFinalEntity.setODeliveredWagonId(oDeliveredWagonId); // nullable
        orderFinalEntity.setWagonNo(wagonNo); // nullable
        orderFinalEntity.setWagonTypeCd(wagonTypeCd); // nullable
        orderFinalEntity.setWagonTypeName(wagonTypeName); // nullable
        orderFinalEntity.setWagonUicClassCd(wagonUicClassCd); // nullable
        orderFinalEntity.setWagonAxisCount(wagonAxisCount); // nullable
        orderFinalEntity.setWagonTrspCatCd(wagonTrspCatCd); // nullable
        orderFinalEntity.setWagonLimitCreditNoteT(wagonLimitCreditNoteT); // nullable
        orderFinalEntity.setWagonTareWeightT(wagonTareWeightT); // nullable
        orderFinalEntity.setWagonMaxGrossWeightT(wagonMaxGrossWeightT); // nullable
        orderFinalEntity.setWagonVolumeM3(wagonVolumeM3); // nullable
        orderFinalEntity.setODeliveredSimpleVehicleId(oDeliveredSimpleVehicleId); // nullable
        orderFinalEntity.setVehicleName(vehicleName); // nullable
        orderFinalEntity.setVehicleRegNo(vehicleRegNo); // nullable
        orderFinalEntity.setVehicleTransportCatCd(vehicleTransportCatCd); // nullable
        orderFinalEntity.setVehicleTypeCd(vehicleTypeCd); // nullable
        orderFinalEntity.setVehicleMaxGrossWeightT(vehicleMaxGrossWeightT); // nullable
        orderFinalEntity.setVehicleTareWeightT(vehicleTareWeightT); // nullable
        orderFinalEntity.setVehicleNrChambers(vehicleNrChambers); // nullable
        orderFinalEntity.setODeliveredDriverId(oDeliveredDriverId); // nullable
        orderFinalEntity.setDriverName(driverName); // nullable
        orderFinalEntity.setDriverFirstname(driverFirstname); // nullable
        orderFinalEntity.setDriverUserTypeCd(driverUserTypeCd); // nullable
        orderFinalEntity.setDriverSapBpNo(driverSapBpNo); // nullable
        orderFinalEntity.setDriverSapVendorNo(driverSapVendorNo); // nullable
        orderFinalEntity.setDriverCertifiedFromDat(driverCertifiedFromDat); // nullable
        orderFinalEntity.setDriverCertifiedToDat(driverCertifiedToDat); // nullable
        orderFinalEntity.setOSentSalesdocToSapTs(oSentSalesdocToSapTs); // nullable
        orderFinalEntity.setOSentShipmentdocToSapTs(oSentShipmentdocToSapTs); // nullable
        orderFinalEntity.setAsspLoadingPlannedStart(asspLoadingPlannedStart); // nullable
        orderFinalEntity.setAsspLoadingRealStart(asspLoadingRealStart); // nullable
        orderFinalEntity.setAsspUnloadingPlannedStart(asspUnloadingPlannedStart); // nullable
        orderFinalEntity.setAsspUnloadingRealStart(asspUnloadingRealStart); // nullable
        orderFinalEntity.setBizNewTs(bizNewTs); // nullable
        orderFinalEntity.setBizTransmPoolTs(bizTransmPoolTs); // nullable
        orderFinalEntity.setBizTransmPoolAckTs(bizTransmPoolAckTs); // nullable
        orderFinalEntity.setBizTransmCommCarrierTs(bizTransmCommCarrierTs); // nullable
        orderFinalEntity.setBizTransmCommCarrierAckTs(bizTransmCommCarrierAckTs); // nullable
        orderFinalEntity.setBizTransmExecCarrierTs(bizTransmExecCarrierTs); // nullable
        orderFinalEntity.setBizDispatchedTs(bizDispatchedTs); // nullable
        orderFinalEntity.setBizTransmTruckTs(bizTransmTruckTs); // nullable
        orderFinalEntity.setBizTransmTruckAckTs(bizTransmTruckAckTs); // nullable
        orderFinalEntity.setBizReactTimeOrdMngMin(bizReactTimeOrdMngMin); // nullable
        orderFinalEntity.setBizReactTimePoolMngMin(bizReactTimePoolMngMin); // nullable
        orderFinalEntity.setBizReactTimeDispatcherMin(bizReactTimeDispatcherMin); // nullable
        orderFinalEntity.setBizReactTimeTruckDrvMin(bizReactTimeTruckDrvMin); // nullable
        orderFinalEntity.setBizReactTimeNetWindowMin(bizReactTimeNetWindowMin); // nullable
        orderFinalEntity.setBizEnterSpTs(bizEnterSpTs); // nullable
        orderFinalEntity.setBizStartLoadTs(bizStartLoadTs); // nullable
        orderFinalEntity.setBizWaitingtimeReasonSpCd(bizWaitingtimeReasonSpCd); // nullable
        orderFinalEntity.setBizEndLoadTs(bizEndLoadTs); // nullable
        orderFinalEntity.setBizLoadingTimeMin(bizLoadingTimeMin); // nullable
        orderFinalEntity.setBizLeaveSpTs(bizLeaveSpTs); // nullable
        orderFinalEntity.setBizCycleTimeAtSpMin(bizCycleTimeAtSpMin); // nullable
        orderFinalEntity.setBizTransRailCompanyTs(bizTransRailCompanyTs); // nullable
        orderFinalEntity.setBizTrafficjamCount(bizTrafficjamCount); // nullable
        orderFinalEntity.setBizTrafficjamDurationMin(bizTrafficjamDurationMin); // nullable
        orderFinalEntity.setBizBreakDrivingCount(bizBreakDrivingCount); // nullable
        orderFinalEntity.setBizBreakDrivingDurationMin(bizBreakDrivingDurationMin); // nullable
        orderFinalEntity.setBizEnterStTs(bizEnterStTs); // nullable
        orderFinalEntity.setBizCalcDrivingTimeMin(bizCalcDrivingTimeMin); // nullable
        orderFinalEntity.setBizCalcNetDrivingTimeMin(bizCalcNetDrivingTimeMin); // nullable
        orderFinalEntity.setBizActualDistanceM(bizActualDistanceM); // nullable
        orderFinalEntity.setBizStartUnloadTs(bizStartUnloadTs); // nullable
        orderFinalEntity.setBizWaitingtimeReasonStCd(bizWaitingtimeReasonStCd); // nullable
        orderFinalEntity.setBizEndUnloadTs(bizEndUnloadTs); // nullable
        orderFinalEntity.setBizUnloadingMin(bizUnloadingMin); // nullable
        orderFinalEntity.setBizLeaveStTs(bizLeaveStTs); // nullable
        orderFinalEntity.setBizCycleTimeAtShiptoMin(bizCycleTimeAtShiptoMin); // nullable
        orderFinalEntity.setBizGeoeventCount(bizGeoeventCount); // nullable
        orderFinalEntity.setOShortTermOrderFlag(oShortTermOrderFlag); // NOT nullable
        orderFinalEntity.setOTripIncInStatisticsFlag(oTripIncInStatisticsFlag); // NOT nullable
        orderFinalEntity.setOReschedulingFlag(oReschedulingFlag); // NOT nullable
        orderFinalEntity.setOLnfCoordLat(oLnfCoordLat); // nullable
        orderFinalEntity.setOLnfCoordLon(oLnfCoordLon); // nullable
        orderFinalEntity.setOLnfSiteName(oLnfSiteName); // nullable
        orderFinalEntity.setOLnfSiteStreetHouseNo(oLnfSiteStreetHouseNo); // nullable
        orderFinalEntity.setOLnfSiteCountry(oLnfSiteCountry); // nullable
        orderFinalEntity.setOLnfSitePostalCode(oLnfSitePostalCode); // nullable
        orderFinalEntity.setOLnfSiteCity(oLnfSiteCity); // nullable
        orderFinalEntity.setOLnfSiteVatNo(oLnfSiteVatNo); // nullable
        orderFinalEntity.setOOneTimeSoldtoName(oOneTimeSoldtoName); // nullable
        orderFinalEntity.setOOneTimeSoldtoStreetHno(oOneTimeSoldtoStreetHno); // nullable
        orderFinalEntity.setOOneTimeSoldtoCity(oOneTimeSoldtoCity); // nullable
        orderFinalEntity.setOOneTimeSoldtoPostalCode(oOneTimeSoldtoPostalCode); // nullable
        orderFinalEntity.setOOneTimeSoldtoCountry(oOneTimeSoldtoCountry); // nullable
        orderFinalEntity.setOOneTimeSoldtoVatNo(oOneTimeSoldtoVatNo); // nullable
        orderFinalEntity.setReportSvcLevelRelevantFlag(reportSvcLevelRelevantFlag); // NOT nullable
        orderFinalEntity.setReportNoteFromCrh(reportNoteFromCrh); // nullable
        orderFinalEntity.setReportNoteFromCarrier(reportNoteFromCarrier); // nullable
        orderFinalEntity.setReportFinishedCommFlag(reportFinishedCommFlag); // NOT nullable
        orderFinalEntity.setOCtlCreUid(oCtlCreUid); // NOT nullable
        orderFinalEntity.setOCtlCreUidName(oCtlCreUidName); // nullable
        orderFinalEntity.setOCtlCreTs(oCtlCreTs); // NOT nullable
        orderFinalEntity.setOCtlModUid(oCtlModUid); // NOT nullable
        orderFinalEntity.setOCtlModUidName(oCtlModUidName); // nullable
        orderFinalEntity.setOCtlModTs(oCtlModTs); // NOT nullable
        orderFinalEntity.setOBpCorrectedFlag(oBpCorrectedFlag); // nullable
        orderFinalEntity.setExtraCostWaitingTimeMin(extraCostWaitingTimeMin); // nullable
        orderFinalEntity.setExtraCost1(extraCost1); // nullable
        orderFinalEntity.setExtraCost2(extraCost2); // nullable
        orderFinalEntity.setExtraCost3(extraCost3); // nullable
        orderFinalEntity.setExtraCost4(extraCost4); // nullable
        orderFinalEntity.setCtlCreUid(ctlCreUid); // NOT nullable
        orderFinalEntity.setCtlCreTs(ctlCreTs); // NOT nullable
        orderFinalEntity.setCtlModUid(ctlModUid); // NOT nullable
        orderFinalEntity.setCtlModTs(ctlModTs); // NOT nullable
        orderFinalEntity.setCtlTcn(ctlTcn); // NOT nullable
    */
}
