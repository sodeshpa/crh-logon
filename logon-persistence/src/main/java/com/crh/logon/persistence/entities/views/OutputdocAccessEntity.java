package com.crh.logon.persistence.entities.views;

import java.util.Date;

import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import ch.nevis.gdi.jpa.BaseEntity;

import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;



@SuppressWarnings("unused")
@Entity
@Table(name = "VLGNC_OUTPUTDOC_ACCESS")
public class OutputdocAccessEntity implements BaseEntity {

	private static final String GDI_JPA_GENERATOR_MARKER_VIEW = "VIEW_MARKER";
    private static final JcanLogger LOG_ENTITIES = JcanLoggerFactory.getLogger("persistence.entities");
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "VLGNC_OUTPUTDOC_ACC";
    
    @Id
    @Column(name = "OUTPUTDOC_ID")
    private Long outputdocId;
    
    @Column(name = "OUTPUTDOC_NAME")
    private String outputdocName;
    
    @Column(name = "OUTPUTDOC_TYPE_CD")
    private Long outputdocTypeCd;
    
    @Column(name = "DATA_MIME_TYPE_CD")
    private Long dataMimeTypeCd;    

    @Column(name = "ORDER_ID")
    private Long orderId;
    
    @Column(name = "CUSTORDER_ID")
    private Long custorderId;

    @Column(name = "SHIPTO_SAP_BP_ID")
    private Long shiptoSapBpId;

    @Column(name = "SOLDTO_SAP_BP_ID")
    private Long soldtoSapBpId;

    @Column(name = "SAREA_ID")
    private Long sareaId;

    @Column(name = "SHIPPINGPOINT_ID")
    private Long shippingpointId;

    @Column(name = "POOL_ID")
    private Long poolId;

    @Column(name = "COMM_SAP_VENDOR_ID")
    private Long commSapVendorId;

    @Column(name = "EXEC_SAP_VENDOR_ID")
    private Long execSapVendorId;

    @Column(name = "SGROUP_CD")
    private String sgroupCd;

    @Column(name = "PLANT_CD")
    private String plantCd;

    @Column(name = "COMPANYCODE_CD")
    private String companycodeCd;


    // Fieldnames
    public static final String ATTR_OUTPUTDOC_ID = "outputdocId";
    public static final String ATTR_OUTPUTDOC_NAME = "outputdocName";    
    public static final String ATTR_OUTPUTDOC_TYPE_CD = "outputdocTypeCd";
    public static final String ATTR_DATA_MIME_TYPE_CD = "dataMimeTypeCd";
    public static final String ATTR_ORDER_ID = "orderId";
    public static final String ATTR_CUSTORDER_ID = "custorderId";
    public static final String ATTR_SHIPTO_SAP_BP_ID = "shiptoSapBpId";
    public static final String ATTR_SOLDTO_SAP_BP_ID = "soldtoSapBpId";
    public static final String ATTR_SAREA_ID = "sareaId";
    public static final String ATTR_SHIPPINGPOINT_ID = "shippingpointId";
    public static final String ATTR_POOL_ID = "poolId";
    public static final String ATTR_COMM_SAP_VENDOR_ID = "commSapVendorId";
    public static final String ATTR_EXEC_SAP_VENDOR_ID = "execSapVendorId";
    public static final String ATTR_SGROUP_CD = "sgroupCd";
    public static final String ATTR_PLANT_CD = "plantCd";
    public static final String ATTR_COMPANYCODE_CD = "companycodeCd";
    
    // Field length on DB
    public static final long DBSIZE_OUTPUTDOC_ID = 12;
    public static final long DBSIZE_OUTPUTDOC_NAME = 100;
    public static final long DBSIZE_DATA_MIME_TYPE_CD = 12;
    public static final long DBSIZE_OUTPUTDOC_TYPE_CD = 12;
    public static final long DBSIZE_ORDER_ID = 12;
    public static final long DBSIZE_CUSTORDER_ID = 12;
    public static final long DBSIZE_SHIPTO_SAP_BP_ID = 12;
    public static final long DBSIZE_SOLDTO_SAP_BP_ID = 12;
    public static final long DBSIZE_SAREA_ID = 12;
    public static final long DBSIZE_SHIPPINGPOINT_ID = 12;
    public static final long DBSIZE_POOL_ID = 12;
    public static final long DBSIZE_COMM_SAP_VENDOR_ID = 12;
    public static final long DBSIZE_EXEC_SAP_VENDOR_ID = 12;
    public static final long DBSIZE_SGROUP_CD = 4;
    public static final long DBSIZE_PLANT_CD = 4;
    public static final long DBSIZE_COMPANYCODE_CD = 4;

    
    /**
     * OUTPUTDOC_ID is NOT nullable
     * Field length on DB: 12
     */
    public void setOutputdocId(Long outputdocId) {
        this.outputdocId = outputdocId;
    }

    /**
     * OUTPUTDOC_ID is NOT nullable
     * Field length on DB: 12
     */
    public Long getOutputdocId() {
        return outputdocId;
    }


    /**
     * OUTPUTDOC_NAME is NOT nullable
     * Field length on DB: 100
     * Human readable name/description of the document. Never used as a reference and therefore changeable.
     */
    public void setOutputdocName(String outputdocName) {
        this.outputdocName = outputdocName;
    }

	public Long getOutputdocTypeCd() {
		return outputdocTypeCd;
	}

	public void setOutputdocTypeCd(Long outputdocTypeCd) {
		this.outputdocTypeCd = outputdocTypeCd;
	}
	
    /**
     * OUTPUTDOC_NAME is NOT nullable
     * Field length on DB: 100
     * Human readable name/description of the document. Never used as a reference and therefore changeable.
     */
    public String getOutputdocName() {
        return outputdocName;
    }
    
    /**
     * DATA_MIME_TYPE_CD is NOT nullable
     * Field length on DB: 12
     */
    public void setDataMimeTypeCd(Long dataMimeTypeCd) {
        this.dataMimeTypeCd = dataMimeTypeCd;
    }

    /**
     * DATA_MIME_TYPE_CD is NOT nullable
     * Field length on DB: 12
     */
    public Long getDataMimeTypeCd() {
        return dataMimeTypeCd;
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
     * CUSTORDER_ID is NOT nullable
     * Field length on DB: 12
     */
    public void setCustorderId(Long custorderId) {
        this.custorderId = custorderId;
    }

    /**
     * CUSTORDER_ID is NOT nullable
     * Field length on DB: 12
     */
    public Long getCustorderId() {
        return custorderId;
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
     * COMM_SAP_VENDOR_ID is nullable
     * Field length on DB: 12
     */
    public void setCommSapVendorId(Long commSapVendorId) {
        this.commSapVendorId = commSapVendorId;
    }

    /**
     * COMM_SAP_VENDOR_ID is nullable
     * Field length on DB: 12
     */
    public Long getCommSapVendorId() {
        return commSapVendorId;
    }


    /**
     * EXEC_SAP_VENDOR_ID is nullable
     * Field length on DB: 12
     */
    public void setExecSapVendorId(Long execSapVendorId) {
        this.execSapVendorId = execSapVendorId;
    }

    /**
     * EXEC_SAP_VENDOR_ID is nullable
     * Field length on DB: 12
     */
    public Long getExecSapVendorId() {
        return execSapVendorId;
    }


    /**
     * SGROUP_CD is nullable
     * Field length on DB: 4
     */
    public void setSgroupCd(String sgroupCd) {
        this.sgroupCd = sgroupCd;
    }

    /**
     * SGROUP_CD is nullable
     * Field length on DB: 4
     */
    public String getSgroupCd() {
        return sgroupCd;
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


	public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this);
        tsb.append(super.toString());
        if (LOG_ENTITIES.isDebugEnabled()) {        	
            tsb.append("outputdocId", outputdocId);
            tsb.append("dataMimeTypeCd", dataMimeTypeCd);
            tsb.append("outputdocName", outputdocName);
            tsb.append("outputdocTypeCd", outputdocTypeCd);            
            tsb.append("orderId", orderId);
            tsb.append("custorderId",custorderId);
            tsb.append("shiptoSapBpId", shiptoSapBpId);
            tsb.append("soldtoSapBpId", soldtoSapBpId);
            tsb.append("sareaId", sareaId);
            tsb.append("shippingpointId", shippingpointId);
            tsb.append("poolId", poolId);
            tsb.append("commSapVendorId", commSapVendorId);
            tsb.append("execSapVendorId", execSapVendorId);
            tsb.append("sgroupCd", sgroupCd);
            tsb.append("plantCd", plantCd);
            tsb.append("companycodeCd", companycodeCd);
        }
        else if (LOG_ENTITIES.isInfoEnabled()) {
            tsb.append("outputdocId", outputdocId);
        }
        return tsb.toString();
    }

}
