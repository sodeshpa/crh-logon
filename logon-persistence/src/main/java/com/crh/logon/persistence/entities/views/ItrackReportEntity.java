package com.crh.logon.persistence.entities.views;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import ch.nevis.gdi.jpa.BaseEntity;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import org.apache.commons.lang.builder.ToStringBuilder;


@Entity
@Table(name = "VLGNC_ITRACK_REPORT")
@SuppressWarnings("unused")  // too many imports => suppress eclipse warnings
public class ItrackReportEntity implements BaseEntity {
    private static final String GDI_JPA_GENERATOR_MARKER_VIEW = "VIEW_MARKER";
    private static final JcanLogger LOG_ENTITIES = JcanLoggerFactory.getLogger("persistence.entities");
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "VLGNC_ITRACK_REPORT";

    @Id
    @Column(name = "ORDER_NUMBER", insertable=false, updatable=false)
    private Long orderNumber;

    @Column(name = "SAREA_ID", insertable=false, updatable=false)
    private Long sareaId;

    @Column(name = "SAREA_SAREA_DISPLAY_LONGNAME", insertable=false, updatable=false)
    private String sareaSareaDisplayLongname;

    @Column(name = "SOLDTO_SAP_BP_ID", insertable=false, updatable=false)
    private Long soldtoSapBpId;

    @Column(name = "SOLDTO_SAP_ID", insertable=false, updatable=false)
    private String soldtoSapId;

    @Column(name = "SOLDTO_NAME1", insertable=false, updatable=false)
    private String soldtoName1;

    @Column(name = "SHIPTO_SAP_BP_ID", insertable=false, updatable=false)
    private Long shiptoSapBpId;

    @Column(name = "SHIPTO_SAP_ID", insertable=false, updatable=false)
    private String shiptoSapId;

    @Column(name = "SHIPTO_NAME1", insertable=false, updatable=false)
    private String shiptoName1;

    @Column(name = "SP_PLANT_CD", insertable=false, updatable=false)
    private String spPlantCd;

    @Column(name = "SP_PLANT_NAME", insertable=false, updatable=false)
    private String spPlantName;

    @Column(name = "SP_SHIPPINGPOINT_ID", insertable=false, updatable=false)
    private Long spShippingpointId;

    @Column(name = "SP_SAP_SHIPPINGPOINT_NO", insertable=false, updatable=false)
    private String spSapShippingpointNo;

    @Column(name = "SP_SHIPPINGPOINT_NAME", insertable=false, updatable=false)
    private String spShippingpointName;

    @Column(name = "COMM_CARRIER_ID", insertable=false, updatable=false)
    private Long commCarrierId;

    @Column(name = "COMM_CARRIER_V_SAP_ID", insertable=false, updatable=false)
    private String commCarrierVSapId;

    @Column(name = "COMM_CARRIER_DISPLAY_NAME", insertable=false, updatable=false)
    private String commCarrierDisplayName;

    @Column(name = "EXEC_CARRIER_ID", insertable=false, updatable=false)
    private Long execCarrierId;

    @Column(name = "EXEC_CARRIER_V_SAP_ID", insertable=false, updatable=false)
    private String execCarrierVSapId;

    @Column(name = "EXEC_CARRIER_DISPLAY_NAME", insertable=false, updatable=false)
    private String execCarrierDisplayName;

    @Column(name = "TRUCK_PLATE_NO", insertable=false, updatable=false)
    private String truckPlateNo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DELIVERY_FROM_DAT", insertable=false, updatable=false)
    private Date deliveryFromDat;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DELIVERY_TO_DAT", insertable=false, updatable=false)
    private Date deliveryToDat;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UNLOADING_TIME", insertable=false, updatable=false)
    private Date unloadingTime;

    @Column(name = "DIFF_VS_DEL_FROM", insertable=false, updatable=false)
    private Long diffVsDelFrom;

    @Column(name = "DIFF_VS_DEL_TO", insertable=false, updatable=false)
    private Long diffVsDelTo;

    @Column(name = "TOLL_FEE", insertable=false, updatable=false)
    private java.math.BigDecimal tollFee;

    @Column(name = "ITRACK_DISTANCE_KM", insertable=false, updatable=false)
    private Long itrackDistanceKm;

    @Column(name = "FLOW_TYPE", insertable=false, updatable=false)
    private String flowType;

    @Column(name = "SAREA_SORG_CD", insertable=false, updatable=false)
    private String sareaSorgCd;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LOAD_START_DATE", insertable=false, updatable=false)
    private Date loadStartDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LOAD_END_DATE", insertable=false, updatable=false)
    private Date loadEndDate;



    // Fieldnames
    public static final String ATTR_ORDER_NUMBER = "orderNumber";
    public static final String ATTR_SAREA_ID = "sareaId";
    public static final String ATTR_SAREA_SAREA_DISPLAY_LONGNAME = "sareaSareaDisplayLongname";
    public static final String ATTR_SOLDTO_SAP_BP_ID = "soldtoSapBpId";
    public static final String ATTR_SOLDTO_SAP_ID = "soldtoSapId";
    public static final String ATTR_SOLDTO_NAME1 = "soldtoName1";
    public static final String ATTR_SHIPTO_SAP_BP_ID = "shiptoSapBpId";
    public static final String ATTR_SHIPTO_SAP_ID = "shiptoSapId";
    public static final String ATTR_SHIPTO_NAME1 = "shiptoName1";
    public static final String ATTR_SP_PLANT_CD = "spPlantCd";
    public static final String ATTR_SP_PLANT_NAME = "spPlantName";
    public static final String ATTR_SP_SHIPPINGPOINT_ID = "spShippingpointId";
    public static final String ATTR_SP_SAP_SHIPPINGPOINT_NO = "spSapShippingpointNo";
    public static final String ATTR_SP_SHIPPINGPOINT_NAME = "spShippingpointName";
    public static final String ATTR_COMM_CARRIER_ID = "commCarrierId";
    public static final String ATTR_COMM_CARRIER_V_SAP_ID = "commCarrierVSapId";
    public static final String ATTR_COMM_CARRIER_DISPLAY_NAME = "commCarrierDisplayName";
    public static final String ATTR_EXEC_CARRIER_ID = "execCarrierId";
    public static final String ATTR_EXEC_CARRIER_V_SAP_ID = "execCarrierVSapId";
    public static final String ATTR_EXEC_CARRIER_DISPLAY_NAME = "execCarrierDisplayName";
    public static final String ATTR_TRUCK_PLATE_NO = "truckPlateNo";
    public static final String ATTR_DELIVERY_FROM_DAT = "deliveryFromDat";
    public static final String ATTR_DELIVERY_TO_DAT = "deliveryToDat";
    public static final String ATTR_UNLOADING_TIME = "unloadingTime";
    public static final String ATTR_DIFF_VS_DEL_FROM = "diffVsDelFrom";
    public static final String ATTR_DIFF_VS_DEL_TO = "diffVsDelTo";
    public static final String ATTR_TOLL_FEE = "tollFee";
    public static final String ATTR_ITRACK_DISTANCE_KM = "itrackDistanceKm";
    public static final String ATTR_FLOW_TYPE = "flowType";
    public static final String ATTR_SAREA_SORG_CD = "sareaSorgCd";
    public static final String ATTR_LOAD_START_DATE = "loadStartDate";
    public static final String ATTR_LOAD_END_DATE = "loadEndDate";

    // Field length on DB
    public static final long DBSIZE_ORDER_NUMBER = 12;
    public static final long DBSIZE_SAREA_ID = 12;
    public static final long DBSIZE_SAREA_SAREA_DISPLAY_LONGNAME = 30;
    public static final long DBSIZE_SOLDTO_SAP_BP_ID = 12;
    public static final long DBSIZE_SOLDTO_SAP_ID = 10;
    public static final long DBSIZE_SOLDTO_NAME1 = 35;
    public static final long DBSIZE_SHIPTO_SAP_BP_ID = 12;
    public static final long DBSIZE_SHIPTO_SAP_ID = 10;
    public static final long DBSIZE_SHIPTO_NAME1 = 35;
    public static final long DBSIZE_SP_PLANT_CD = 4;
    public static final long DBSIZE_SP_PLANT_NAME = 80;
    public static final long DBSIZE_SP_SHIPPINGPOINT_ID = 12;
    public static final long DBSIZE_SP_SAP_SHIPPINGPOINT_NO = 4;
    public static final long DBSIZE_SP_SHIPPINGPOINT_NAME = 80;
    public static final long DBSIZE_COMM_CARRIER_ID = 12;
    public static final long DBSIZE_COMM_CARRIER_V_SAP_ID = 10;
    public static final long DBSIZE_COMM_CARRIER_DISPLAY_NAME = 40;
    public static final long DBSIZE_EXEC_CARRIER_ID = 12;
    public static final long DBSIZE_EXEC_CARRIER_V_SAP_ID = 10;
    public static final long DBSIZE_EXEC_CARRIER_DISPLAY_NAME = 40;
    public static final long DBSIZE_TRUCK_PLATE_NO = 20;
    public static final long DBSIZE_DIFF_VS_DEL_FROM = 12;
    public static final long DBSIZE_DIFF_VS_DEL_TO = 12;
    public static final long DBSIZE_TOLL_FEE = 12;
    public static final long DBSIZE_PRECISION_TOLL_FEE = 4;
    public static final long DBSIZE_ITRACK_DISTANCE_KM = 12;
    public static final long DBSIZE_FLOW_TYPE = 2;
    public static final long DBSIZE_SAREA_SORG_CD = 4;

    /**
     * ORDER_NUMBER is nullable
     * Field length on DB: 12
     */
    public Long getOrderNumber() {
        return orderNumber;
    }


    /**
     * SAREA_ID is NOT nullable
     * Field length on DB: 12
     */
    public Long getSareaId() {
        return sareaId;
    }

    /**
     * SAREA_SAREA_DISPLAY_LONGNAME is NOT nullable
     * Field length on DB: 30
     */
    public String getSareaSareaDisplayLongname() {
        return sareaSareaDisplayLongname;
    }

    /**
     * SOLDTO_SAP_BP_ID is NOT nullable
     * Field length on DB: 12
     */
    public Long getSoldtoSapBpId() {
        return soldtoSapBpId;
    }

    /**
     * SOLDTO_SAP_ID is nullable
     * Field length on DB: 10
     */
    public String getSoldtoSapId() {
        return soldtoSapId;
    }

    /**
     * SOLDTO_NAME1 is nullable
     * Field length on DB: 35
     */
    public String getSoldtoName1() {
        return soldtoName1;
    }

    /**
     * SHIPTO_SAP_BP_ID is NOT nullable
     * Field length on DB: 12
     */
    public Long getShiptoSapBpId() {
        return shiptoSapBpId;
    }

    /**
     * SHIPTO_SAP_ID is nullable
     * Field length on DB: 10
     */
    public String getShiptoSapId() {
        return shiptoSapId;
    }

    /**
     * SHIPTO_NAME1 is nullable
     * Field length on DB: 35
     */
    public String getShiptoName1() {
        return shiptoName1;
    }

    /**
     * SP_PLANT_CD is nullable
     * Field length on DB: 4
     */
    public String getSpPlantCd() {
        return spPlantCd;
    }

    /**
     * SP_PLANT_NAME is nullable
     * Field length on DB: 80
     */
    public String getSpPlantName() {
        return spPlantName;
    }

    /**
     * SP_SHIPPINGPOINT_ID is nullable
     * Field length on DB: 12
     */
    public Long getSpShippingpointId() {
        return spShippingpointId;
    }

    /**
     * SP_SAP_SHIPPINGPOINT_NO is nullable
     * Field length on DB: 4
     */
    public String getSpSapShippingpointNo() {
        return spSapShippingpointNo;
    }

    /**
     * SP_SHIPPINGPOINT_NAME is nullable
     * Field length on DB: 80
     */
    public String getSpShippingpointName() {
        return spShippingpointName;
    }

    /**
     * COMM_CARRIER_ID is nullable
     * Field length on DB: 12
     */
    public Long getCommCarrierId() {
        return commCarrierId;
    }

    /**
     * COMM_CARRIER_V_SAP_ID is nullable
     * Field length on DB: 10
     */
    public String getCommCarrierVSapId() {
        return commCarrierVSapId;
    }

    /**
     * COMM_CARRIER_DISPLAY_NAME is nullable
     * Field length on DB: 40
     */
    public String getCommCarrierDisplayName() {
        return commCarrierDisplayName;
    }

    /**
     * EXEC_CARRIER_ID is nullable
     * Field length on DB: 12
     */
    public Long getExecCarrierId() {
        return execCarrierId;
    }

    /**
     * EXEC_CARRIER_V_SAP_ID is nullable
     * Field length on DB: 10
     */
    public String getExecCarrierVSapId() {
        return execCarrierVSapId;
    }

    /**
     * EXEC_CARRIER_DISPLAY_NAME is nullable
     * Field length on DB: 40
     */
    public String getExecCarrierDisplayName() {
        return execCarrierDisplayName;
    }

    /**
     * TRUCK_PLATE_NO is nullable
     * Field length on DB: 20
     */
    public String getTruckPlateNo() {
        return truckPlateNo;
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
    public Date getDeliveryToDat() {
        return deliveryToDat;
    }

    /**
     * UNLOADING_TIME is nullable
     *
     */
    public Date getUnloadingTime() {
        return unloadingTime;
    }

    /**
     * DIFF_VS_DEL_FROM is nullable
     * Field length on DB: 12
     */
    public Long getDiffVsDelFrom() {
        return diffVsDelFrom;
    }

    /**
     * DIFF_VS_DEL_TO is nullable
     * Field length on DB: 12
     */
    public Long getDiffVsDelTo() {
        return diffVsDelTo;
    }

    /**
     * TOLL_FEE is nullable
     *
     */
    public java.math.BigDecimal getTollFee() {
        return tollFee;
    }

    /**
     * ITRACK_DISTANCE_KM is nullable
     * Field length on DB: 12
     */
    public Long getItrackDistanceKm() {
        return itrackDistanceKm;
    }

    /**
     * FLOW_TYPE is NOT nullable
     * Field length on DB: 2
     */
    public String getFlowType() {
        return flowType;
    }

    /**
     * SAREA_SORG_CD is NOT nullable
     * Field length on DB: 4
     */
    public String getSareaSorgCd() {
        return sareaSorgCd;
    }

    /**
     * LOAD_START_DATE is nullable
     *
     */
    public Date getLoadStartDate() {
        return loadStartDate;
    }

    /**
     * LOAD_END_DATE is nullable
     *
     */
    public Date getLoadEndDate() {
        return loadEndDate;
    }





    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this);
        tsb.append(super.toString());
        if (LOG_ENTITIES.isDebugEnabled()) {
            tsb.append("orderNumber", orderNumber);
            tsb.append("sareaId", sareaId);
            tsb.append("sareaSareaDisplayLongname", sareaSareaDisplayLongname);
            tsb.append("soldtoSapBpId", soldtoSapBpId);
            tsb.append("soldtoSapId", soldtoSapId);
            tsb.append("soldtoName1", soldtoName1);
            tsb.append("shiptoSapBpId", shiptoSapBpId);
            tsb.append("shiptoSapId", shiptoSapId);
            tsb.append("shiptoName1", shiptoName1);
            tsb.append("spPlantCd", spPlantCd);
            tsb.append("spPlantName", spPlantName);
            tsb.append("spShippingpointId", spShippingpointId);
            tsb.append("spSapShippingpointNo", spSapShippingpointNo);
            tsb.append("spShippingpointName", spShippingpointName);
            tsb.append("commCarrierId", commCarrierId);
            tsb.append("commCarrierVSapId", commCarrierVSapId);
            tsb.append("commCarrierDisplayName", commCarrierDisplayName);
            tsb.append("execCarrierId", execCarrierId);
            tsb.append("execCarrierVSapId", execCarrierVSapId);
            tsb.append("execCarrierDisplayName", execCarrierDisplayName);
            tsb.append("truckPlateNo", truckPlateNo);
            tsb.append("deliveryFromDat", deliveryFromDat);
            tsb.append("deliveryToDat", deliveryToDat);
            tsb.append("unloadingTime", unloadingTime);
            tsb.append("diffVsDelFrom", diffVsDelFrom);
            tsb.append("diffVsDelTo", diffVsDelTo);
            tsb.append("tollFee", tollFee);
            tsb.append("itrackDistanceKm", itrackDistanceKm);
            tsb.append("flowType", flowType);
            tsb.append("sareaSorgCd", sareaSorgCd);
            tsb.append("loadStartDate", loadStartDate);
            tsb.append("loadEndDate", loadEndDate);
        }
        else if (LOG_ENTITIES.isInfoEnabled()) {
            tsb.append("orderNumber", orderNumber); // ID
        }
        return tsb.toString();
    }

    /* convenience:
        itrackReportEntity.getSareaId();
        itrackReportEntity.getSareaSareaDisplayLongname();
        itrackReportEntity.getSoldtoSapBpId();
        itrackReportEntity.getSoldtoSapId();
        itrackReportEntity.getSoldtoName1();
        itrackReportEntity.getShiptoSapBpId();
        itrackReportEntity.getShiptoSapId();
        itrackReportEntity.getShiptoName1();
        itrackReportEntity.getSpPlantCd();
        itrackReportEntity.getSpPlantName();
        itrackReportEntity.getSpShippingpointId();
        itrackReportEntity.getSpSapShippingpointNo();
        itrackReportEntity.getSpShippingpointName();
        itrackReportEntity.getCommCarrierId();
        itrackReportEntity.getCommCarrierVSapId();
        itrackReportEntity.getCommCarrierDisplayName();
        itrackReportEntity.getExecCarrierId();
        itrackReportEntity.getExecCarrierVSapId();
        itrackReportEntity.getExecCarrierDisplayName();
        itrackReportEntity.getTruckPlateNo();
        itrackReportEntity.getDeliveryFromDat();
        itrackReportEntity.getDeliveryToDat();
        itrackReportEntity.getUnloadingTime();
        itrackReportEntity.getDiffVsDelFrom();
        itrackReportEntity.getDiffVsDelTo();
        itrackReportEntity.getTollFee();
        itrackReportEntity.getItrackDistanceKm();
        itrackReportEntity.getFlowType();
        itrackReportEntity.getSareaSorgCd();
        itrackReportEntity.getLoadStartDate();
        itrackReportEntity.getLoadEndDate();

        itrackReportEntity.setSareaId(sareaId); // NOT nullable
        itrackReportEntity.setSareaSareaDisplayLongname(sareaSareaDisplayLongname); // NOT nullable
        itrackReportEntity.setSoldtoSapBpId(soldtoSapBpId); // NOT nullable
        itrackReportEntity.setSoldtoSapId(soldtoSapId); // nullable
        itrackReportEntity.setSoldtoName1(soldtoName1); // nullable
        itrackReportEntity.setShiptoSapBpId(shiptoSapBpId); // NOT nullable
        itrackReportEntity.setShiptoSapId(shiptoSapId); // nullable
        itrackReportEntity.setShiptoName1(shiptoName1); // nullable
        itrackReportEntity.setSpPlantCd(spPlantCd); // nullable
        itrackReportEntity.setSpPlantName(spPlantName); // nullable
        itrackReportEntity.setSpShippingpointId(spShippingpointId); // nullable
        itrackReportEntity.setSpSapShippingpointNo(spSapShippingpointNo); // nullable
        itrackReportEntity.setSpShippingpointName(spShippingpointName); // nullable
        itrackReportEntity.setCommCarrierId(commCarrierId); // nullable
        itrackReportEntity.setCommCarrierVSapId(commCarrierVSapId); // nullable
        itrackReportEntity.setCommCarrierDisplayName(commCarrierDisplayName); // nullable
        itrackReportEntity.setExecCarrierId(execCarrierId); // nullable
        itrackReportEntity.setExecCarrierVSapId(execCarrierVSapId); // nullable
        itrackReportEntity.setExecCarrierDisplayName(execCarrierDisplayName); // nullable
        itrackReportEntity.setTruckPlateNo(truckPlateNo); // nullable
        itrackReportEntity.setDeliveryFromDat(deliveryFromDat); // NOT nullable
        itrackReportEntity.setDeliveryToDat(deliveryToDat); // NOT nullable
        itrackReportEntity.setUnloadingTime(unloadingTime); // nullable
        itrackReportEntity.setDiffVsDelFrom(diffVsDelFrom); // nullable
        itrackReportEntity.setDiffVsDelTo(diffVsDelTo); // nullable
        itrackReportEntity.setTollFee(tollFee); // nullable
        itrackReportEntity.setItrackDistanceKm(itrackDistanceKm); // nullable
        itrackReportEntity.setFlowType(flowType); // NOT nullable
        itrackReportEntity.setSareaSorgCd(sareaSorgCd); // NOT nullable
        itrackReportEntity.setLoadStartDate(loadStartDate); // nullable
        itrackReportEntity.setLoadEndDate(loadEndDate); // nullable
    */
}
