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
@Table(name = "VLGNC_POOL_COMM_CARRIER")
public class PoolCommCarrierEntity implements BaseEntity {
	private static final String GDI_JPA_GENERATOR_MARKER_VIEW = "VIEW_MARKER";
    private static final JcanLogger LOG_ENTITIES = JcanLoggerFactory.getLogger("persistence.entities");
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "VLGNC_POOL_COMM_CARRIER";

    @Id
    @Column(name = "POOL_CARRIER_ID")
    private Long poolCarrierId;
    
    @Column(name = "CARRIER_ID")
    private Long carrierId;

    @Column(name = "DISPLAY_NAME")
    private String displayName;

    @Column(name = "CARRIER_REQUIRES_PLANNING_FLAG")
    private Boolean carrierRequiresPlanningFlag;
    
    @Column(name = "SAP_TRSP_MODE_CD")
    private String sapTrspModeCd;
    
    @Column(name = "POOL_ID")
    private Long poolId;
    
    // Fieldnames
    public static final String ATTR_POOL_CARRIER_ID = "poolCarrierId";    
    public static final String ATTR_CARRIER_ID = "carrierId";
    public static final String ATTR_DISPLAY_NAME = "displayName";
    public static final String ATTR_CARRIER_REQUIRES_PLANNING_FLAG = "carrierRequiresPlanningFlag";
    public static final String ATTR_POOL_ID = "poolId";
    
    // Field length on DB
    public static final long DBSIZE_POOL_CARRIER_ID = 12;
    public static final long DBSIZE_CARRIER_ID = 12;
    public static final long DBSIZE_DISPLAY_NAME = 40;
    public static final long DBSIZE_CARRIER_REQUIRES_PLANNING_FLAG = 1;
    public static final long DBSIZE_POOL_ID = 12;
    
    public void setPoolCarrierId(Long poolCarrierId) {
        this.poolCarrierId = poolCarrierId;
    }

    public Long getPoolCarrierId() {
        return poolCarrierId;
    }
    
    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

    public Long getCarrierId() {
        return carrierId;
    }

    public void setPoolId(Long poolId) {
        this.poolId = poolId;
    }

    public Long getPoolId() {
        return poolId;
    }


    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setCarrierRequiresPlanningFlag(Boolean carrierRequiresPlanningFlag) {
        this.carrierRequiresPlanningFlag = carrierRequiresPlanningFlag;
    }

    public Boolean getCarrierRequiresPlanningFlag() {
        return carrierRequiresPlanningFlag;
    }
    
    public void setSapTrspModeCd(String sapTrspModeCd) {
        this.sapTrspModeCd = sapTrspModeCd;
    }
    public String getSapTrspModeCd() {
        return this.sapTrspModeCd;
    }

    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this);
        tsb.append(super.toString());
        if (LOG_ENTITIES.isDebugEnabled()) {
            tsb.append("poolCarrierId", poolCarrierId);
            tsb.append("carrierId", carrierId);
            tsb.append("displayName", "displayName");
            tsb.append("carrierRequiresPlanningFlag", "carrierRequiresPlanningFlag");
            tsb.append("sapTrspModeCd", "sapTrspModeCd");
        }
        else if (LOG_ENTITIES.isInfoEnabled()) {
            tsb.append("poolCarrierId", poolCarrierId);
        }
        return tsb.toString();
    }
}
