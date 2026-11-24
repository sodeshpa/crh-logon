package com.crh.logon.persistence.entities.views;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.gdi.jpa.BaseEntity;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;



@SuppressWarnings("unused")
@Entity
@Table(name = "VLGNC_COMM_EXEC_CARR_PF_ACTIVE")
public class CommExecCarrPfActiveEntity implements BaseEntity {
	private static final String GDI_JPA_GENERATOR_MARKER_VIEW = "VIEW_MARKER";
    private static final JcanLogger LOG_ENTITIES = JcanLoggerFactory.getLogger("persistence.entities");
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "VLGNC_COMM_EXEC_CARR_PF_ACTIVE";


    @Id
    @Column(name = "COMPOSITE_ID")
    private String compositeId;

    @Column(name = "COMM_CARRIER_ID")
    private Long commCarrierId;

    @Column(name = "EXEC_CARRIER_ID")
    private Long execCarrierId;

    @Column(name = "EXEC_CARRIER_DISPLAY_NAME")
    private String execCarrierDisplayName;
    
    @Column(name = "EXEC_SAP_VENDOR_ID")
    private Long execSapVendorId;


    // Fieldnames
    public static final String ATTR_COMPOSITE_ID = "compositeId";
    public static final String ATTR_COMM_CARRIER_ID = "commCarrierId";
    public static final String ATTR_EXEC_CARRIER_ID = "execCarrierId";
    public static final String ATTR_EXEC_CARRIER_DISPLAY_NAME = "execCarrierDisplayName";
    public static final String ATTR_EXEC_SAP_VENDOR_ID = "execSapVendorId";

    // Field length on DB
    public static final long DBSIZE_COMPOSITE_ID = 81;
    public static final long DBSIZE_COMM_CARRIER_ID = 12;
    public static final long DBSIZE_EXEC_CARRIER_ID = 12;
    public static final long DBSIZE_EXEC_CARRIER_DISPLAY_NAME = 40;
    public static final long DBSIZE_EXEC_SAP_VENDOR_ID = 12;

    /**
     * COMPOSITE_ID is nullable
     * Field length on DB: 81
     */
    public void setCompositeId(String compositeId) {
        this.compositeId = compositeId;
    }

    /**
     * COMPOSITE_ID is nullable
     * Field length on DB: 81
     */
    public String getCompositeId() {
        return compositeId;
    }


    /**
     * COMM_CARRIER_ID is NOT nullable
     * Field length on DB: 12
     */
    public void setCommCarrierId(Long commCarrierId) {
        this.commCarrierId = commCarrierId;
    }

    /**
     * COMM_CARRIER_ID is NOT nullable
     * Field length on DB: 12
     */
    public Long getCommCarrierId() {
        return commCarrierId;
    }

    /**
     * EXEC_CARRIER_ID is NOT nullable
     * Field length on DB: 12
     */
    public void setExecCarrierId(Long execCarrierId) {
        this.execCarrierId = execCarrierId;
    }

    /**
     * EXEC_CARRIER_ID is NOT nullable
     * Field length on DB: 12
     */
    public Long getExecCarrierId() {
        return execCarrierId;
    }

    /**
     * EXEC_CARRIER_DISPLAY_NAME is NOT nullable
     * Field length on DB: 40
     */
    public void setExecCarrierDisplayName(String execCarrierDisplayName) {
        this.execCarrierDisplayName = execCarrierDisplayName;
    }

    /**
     * EXEC_CARRIER_DISPLAY_NAME is NOT nullable
     * Field length on DB: 40
     */
    public String getExecCarrierDisplayName() {
        return execCarrierDisplayName;
    }


    /**
     * SAP_VENDOR_ID is NOT nullable
     * Field length on DB: 12
     */
    public void setExecSapVendorId(Long sapVendorId) {
        this.execSapVendorId = sapVendorId;
    }

    /**
     * SAP_VENDOR_ID is NOT nullable
     * Field length on DB: 12
     */
    public Long getExecSapVendorId() {
        return execSapVendorId;
    }

    
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this);
        tsb.append(super.toString());
        if (LOG_ENTITIES.isDebugEnabled()) {
            tsb.append("compositeId", "compositeId");
            tsb.append("commCarrierId", "commCarrierId");
            tsb.append("execCarrierId", "execCarrierId");
            tsb.append("execSapVendorId", "execSapVendorId");            
            tsb.append("execCarrierDisplayName", "execCarrierDisplayName");
        }
        else if (LOG_ENTITIES.isInfoEnabled()) {
            tsb.append("compositeId", compositeId); // ID
        }
        return tsb.toString();
    }

}
