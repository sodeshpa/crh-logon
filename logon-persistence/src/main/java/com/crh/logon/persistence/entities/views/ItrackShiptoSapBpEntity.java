package com.crh.logon.persistence.entities.views;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import ch.nevis.gdi.jpa.BaseEntity;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import org.apache.commons.lang.builder.ToStringBuilder;


@Entity
@Table(name = "VLGNC_ITRACK_SHIPTO_SAP_BP")
@SuppressWarnings("unused")  // too many imports => suppress eclipse warnings
public class ItrackShiptoSapBpEntity implements BaseEntity {
	private static final String GDI_JPA_GENERATOR_MARKER_VIEW = "VIEW_MARKER";
    private static final JcanLogger LOG_ENTITIES = JcanLoggerFactory.getLogger("persistence.entities");
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "VLGNC_ITRACK_SHIPTO_SAP_BP";

    @Id
    @Column(name = "SHIPTO_SAP_BP_ID", insertable=false, updatable=false)
    private Long shiptoSapBpId;

    @Column(name = "SHIPTO_SAP_ID", insertable=false, updatable=false)
    private String shiptoSapId;

    @Column(name = "SHIPTO_NAME1", insertable=false, updatable=false)
    private String shiptoName1;

    @Column(name = "SHIPTO_NAME2", insertable=false, updatable=false)
    private String shiptoName2;

    @Column(name = "SHIPTO_ID", insertable=false, updatable=false)
    private Long shiptoId;

    // Fieldnames
    public static final String ATTR_SHIPTO_SAP_BP_ID = "shiptoSapBpId";
    public static final String ATTR_SHIPTO_SAP_ID = "shiptoSapId";
    public static final String ATTR_SHIPTO_NAME1 = "shiptoName1";
    public static final String ATTR_SHIPTO_NAME2 = "shiptoName2";
    public static final String ATTR_SHIPTO_ID = "shiptoId";

    // Field length on DB
    public static final long DBSIZE_SHIPTO_SAP_BP_ID = 12;
    public static final long DBSIZE_SHIPTO_SAP_ID = 10;
    public static final long DBSIZE_SHIPTO_NAME1 = 35;
    public static final long DBSIZE_SHIPTO_NAME2 = 35;
    public static final long DBSIZE_SHIPTO_ID = 12;

    /**
     * SHIPTO_SAP_BP_ID is NOT nullable
     * Field length on DB: 12
     */
    public Long getShiptoSapBpId() {
        return shiptoSapBpId;
    }

    /**
     * SHIPTO_SAP_ID is NOT nullable
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
     * SHIPTO_NAME2 is nullable
     * Field length on DB: 35
     */
    public String getShiptoName2() {
        return shiptoName2;
    }

    /**
     * SHIPTO_ID is NOT nullable
     * Field length on DB: 12
     */
    public Long getShiptoId() {
        return shiptoId;
    }

    
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this);
        tsb.append(super.toString());
        if (LOG_ENTITIES.isDebugEnabled()) {
            tsb.append("shiptoSapBpId", shiptoSapBpId);
            tsb.append("shiptoSapId", shiptoSapId);
            tsb.append("shiptoName1", shiptoName1);
            tsb.append("shiptoName2", shiptoName2);
            tsb.append("shiptoId", shiptoId);
        }
        else if (LOG_ENTITIES.isInfoEnabled()) {
            tsb.append("shiptoSapBpId", shiptoSapBpId); // ID
        }
        return tsb.toString();
    }

    /* convenience:
        itrackShiptoSapBpEntity.getShiptoSapBpId();
        itrackShiptoSapBpEntity.getShiptoSapId();
        itrackShiptoSapBpEntity.getShiptoName1();
        itrackShiptoSapBpEntity.getShiptoName2();
        itrackShiptoSapBpEntity.getShiptoId();


        itrackShiptoSapBpEntity.setShiptoSapBpId(shiptoSapBpId); // NOT nullable
        itrackShiptoSapBpEntity.setShiptoSapId(shiptoSapId); // NOT nullable
        itrackShiptoSapBpEntity.setShiptoName1(shiptoName1); // nullable
        itrackShiptoSapBpEntity.setShiptoName2(shiptoName2); // nullable
        itrackShiptoSapBpEntity.setShiptoId(shiptoId); // NOT nullable
    */
}
