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
@Table(name = "VLGNC_ITRACK_SOLDTO_SAP_BP")
@SuppressWarnings("unused")  // too many imports => suppress eclipse warnings
public class ItrackSoldtoSapBpEntity implements BaseEntity {
	private static final String GDI_JPA_GENERATOR_MARKER_VIEW = "VIEW_MARKER";
    private static final JcanLogger LOG_ENTITIES = JcanLoggerFactory.getLogger("persistence.entities");
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "VLGNC_ITRACK_SOLDTO_SAP_BP";

    @Id
    @Column(name = "SOLDTO_SAP_BP_ID", insertable=false, updatable=false)
    private Long soldtoSapBpId;

    @Column(name = "SOLDTO_SAP_ID", insertable=false, updatable=false)
    private String soldtoSapId;

    @Column(name = "SOLDTO_NAME1", insertable=false, updatable=false)
    private String soldtoName1;

    @Column(name = "SOLDTO_NAME2", insertable=false, updatable=false)
    private String soldtoName2;



    // Fieldnames
    public static final String ATTR_SOLDTO_SAP_BP_ID = "soldtoSapBpId";
    public static final String ATTR_SOLDTO_SAP_ID = "soldtoSapId";
    public static final String ATTR_SOLDTO_NAME1 = "soldtoName1";
    public static final String ATTR_SOLDTO_NAME2 = "soldtoName2";

    // Field length on DB
    public static final long DBSIZE_SOLDTO_SAP_BP_ID = 12;
    public static final long DBSIZE_SOLDTO_SAP_ID = 10;
    public static final long DBSIZE_SOLDTO_NAME1 = 35;
    public static final long DBSIZE_SOLDTO_NAME2 = 35;


    /**
     * SOLDTO_SAP_BP_ID is NOT nullable
     * Field length on DB: 12
     */
    public Long getSoldtoSapBpId() {
        return soldtoSapBpId;
    }

    /**
     * SOLDTO_SAP_ID is NOT nullable
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
     * SOLDTO_NAME2 is nullable
     * Field length on DB: 35
     */
    public String getSoldtoName2() {
        return soldtoName2;
    }

    
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this);
        tsb.append(super.toString());
        if (LOG_ENTITIES.isDebugEnabled()) {
            tsb.append("soldtoSapBpId", soldtoSapBpId);
            tsb.append("soldtoSapId", soldtoSapId);
            tsb.append("soldtoName1", soldtoName1);
            tsb.append("soldtoName2", soldtoName2);
        }
        else if (LOG_ENTITIES.isInfoEnabled()) {
            tsb.append("soldtoSapBpId", soldtoSapBpId); // ID
        }
        return tsb.toString();
    }

    /* convenience:
        itrackSoldtoSapBpEntity.getSoldtoSapBpId();
        itrackSoldtoSapBpEntity.getSoldtoSapId();
        itrackSoldtoSapBpEntity.getSoldtoName1();
        itrackSoldtoSapBpEntity.getSoldtoName2();

        itrackSoldtoSapBpEntity.setSoldtoSapBpId(soldtoSapBpId); // NOT nullable
        itrackSoldtoSapBpEntity.setSoldtoSapId(soldtoSapId); // NOT nullable
        itrackSoldtoSapBpEntity.setSoldtoName1(soldtoName1); // nullable
        itrackSoldtoSapBpEntity.setSoldtoName2(soldtoName2); // nullable
    */
}
