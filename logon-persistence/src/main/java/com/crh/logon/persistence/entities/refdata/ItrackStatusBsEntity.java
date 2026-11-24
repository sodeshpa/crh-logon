package com.crh.logon.persistence.entities.refdata;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import ch.nevis.gdi.jpa.BaseEntity;
import ch.nevis.gdi.jpa.ThreadLocalUserInfo;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import org.apache.commons.lang.builder.ToStringBuilder;

@SuppressWarnings("unused")
@Entity
@Table(name = "TLGNR_ITRACK_STATUS_BS")
public class ItrackStatusBsEntity implements BaseEntity {
	private static final String GDI_JPA_GENERATOR_MARKER_REFDATA = "REFDATA_MARKER";
    private static final JcanLogger LOG_ENTITIES = JcanLoggerFactory.getLogger("persistence.entities");
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "TLGNR_ITRACK_STATUS_BS";


    @Id
    @Column(name = "ITRACK_STATUS_CD")
    private Long itrackStatusCd;

    @Column(name = "ITRACK_STATUS_NAME")
    private String itrackStatusName;

    @Column(name = "CTL_ACT_CD")
    private Boolean ctlActCd;

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


    // Fieldnames
    public static final String ATTR_ITRACK_STATUS_NAME = "itrackStatusName";
    public static final String ATTR_ITRACK_STATUS_CD = "itrackStatusCd";
    public static final String ATTR_CTL_ACT_CD = "ctlActCd";
    public static final String ATTR_CTL_CRE_UID = "ctlCreUid";
    public static final String ATTR_CTL_CRE_TS = "ctlCreTs";
    public static final String ATTR_CTL_MOD_UID = "ctlModUid";
    public static final String ATTR_CTL_MOD_TS = "ctlModTs";

    // Field length on DB
    public static final long DBSIZE_ITRACK_STATUS_NAME = 30;
    public static final long DBSIZE_ITRACK_STATUS_CD =  3;
    public static final long DBSIZE_CTL_ACT_CD = 1;
    public static final long DBSIZE_CTL_CRE_UID = 12;
    public static final long DBSIZE_CTL_MOD_UID = 12;

       /**
     * ITRACK_STATUS_NAME is nullable
     * Field length on DB: 40
     * Full name of the Itrack Status
     */
    public void setItrackStatusName(String itrackStatusName) {
        this.itrackStatusName = itrackStatusName;
    }

    /**
     * ITRACK_STATUS_NAME is nullable
     * Field length on DB: 40
     * Full name of the Itrack Status
     */
    public String getItrackStatusName() {
        return itrackStatusName;
    }
    
    /**
     * ITRACK_STATUS_CD is NOT nullable
     * Field length on DB: 1
     * Itrack Status Code
     */
    public void setItrackStatusCd(Long itrackStatusCd) {
        this.itrackStatusCd = itrackStatusCd;
    }

    /**
     * ITRACK_STATUS_CD is NOT nullable
     * Field length on DB: 1
     * Itrack Status Code
     */
    public Long getItrackStatusCd() {
        return itrackStatusCd;
    }

    /**
     * CTL_ACT_CD is NOT nullable
     * 
     * Active Flag. When new data is entered, only reference data with active flag are available for selection normally.
     */
    public void setCtlActCd(Boolean ctlActCd) {
        this.ctlActCd = ctlActCd;
    }

    /**
     * CTL_ACT_CD is NOT nullable
     * 
     * Active Flag. When new data is entered, only reference data with active flag are available for selection normally.
     */
    public Boolean getCtlActCd() {
        return ctlActCd;
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
            tsb.append("itrackStatusName", "itrackStatusName");
            tsb.append("itrackStatusCd", itrackStatusCd);
            tsb.append("ctlActCd", "ctlActCd");
            tsb.append("ctlCreUid", "ctlCreUid");
            tsb.append("ctlCreTs", "ctlCreTs");
            tsb.append("ctlModUid", "ctlModUid");
            tsb.append("ctlModTs", "ctlModTs");
        }
        else if (LOG_ENTITIES.isInfoEnabled()) {
            tsb.append("itrackStatusCd", itrackStatusCd); // ID
        }
        return tsb.toString();
    }
}
