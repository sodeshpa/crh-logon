/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.persistence.entities.refdata;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.gdi.jpa.BaseEntity;
import ch.nevis.gdi.jpa.ThreadLocalUserInfo;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * Reference table for the different types of order archiving status
 */

@SuppressWarnings("unused")
@Entity
@Table(name = "TLGNR_ARCHIVING_STATUS_BS")
public class ArchivingStatusBsEntity implements BaseEntity {
	private static final String GDI_JPA_GENERATOR_MARKER_REFDATA = "REFDATA_MARKER";
    private static final JcanLogger LOG_ENTITIES = JcanLoggerFactory.getLogger("persistence.entities");
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "TLGNR_ARCHIVING_STATUS_BS";
    
    @Id
    @Column(name = "ARCHIVING_STATUS_CD")
    private Long archivingStatusCd;
    
    @Column(name="DESCRIPTION")
    private String description;
    
    @Column(name="DISPLAY_FLAG")
    private Boolean displayFlag;

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
    public static final String ATTR_ARCHIVING_STATUS_CD = "archivingStatusCd";
    public static final String ATTR_DESCRIPTION = "description";
    public static final String ATTR_DISPLAY_FLAG = "displayFlag";
    public static final String ATTR_CTL_CRE_UID = "ctlCreUid";
    public static final String ATTR_CTL_CRE_TS = "ctlCreTs";
    public static final String ATTR_CTL_MOD_UID = "ctlModUid";
    public static final String ATTR_CTL_MOD_TS = "ctlModTs";

    // Field length on DB
    public static final long DBSIZE_ARCHIVING_STATUS_CD = 2;
    public static final long DBSIZE_DESCRIPTION_CD = 2;
    public static final long DBSIZE_DISPLAY_FLAG = 1;
    public static final long DBSIZE_CTL_CRE_UID = 12;
    public static final long DBSIZE_CTL_MOD_UID = 12;

	/**
	 * ARCHIVING_STATUS_CD is NOT nullable
     * Field length on DB: 2
	 * @return the archivingStatusCd
	 */
	public Long getArchivingStatusCd() {
		return archivingStatusCd;
	}

	/**
	 * ARCHIVING_STATUS_CD is NOT nullable
     * Field length on DB: 2
	 * @param archivingStatusCd the archivingStatusCd to set
	 */
	public void setArchivingStatusCd(Long archivingStatusCd) {
		this.archivingStatusCd = archivingStatusCd;
	}

	/**
	 * DESCRIPTION is nullable
     * Field length on DB: 50
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * DESCRIPTION is nullable
     * Field length on DB: 50
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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
            tsb.append("archivingStatusCd", archivingStatusCd);
            tsb.append("description", "description");
            tsb.append("displayFlag","displayFlag");
            tsb.append("ctlActCd", "ctlActCd");
            tsb.append("ctlCreUid", "ctlCreUid");
            tsb.append("ctlCreTs", "ctlCreTs");
            tsb.append("ctlModUid", "ctlModUid");
            tsb.append("ctlModTs", "ctlModTs");
        }
        else if (LOG_ENTITIES.isInfoEnabled()) {
            tsb.append("archivingStatusCd", archivingStatusCd); // ID
        }
        return tsb.toString();
    }

	/**
	 * DISPLAY_FLAG not nullable
	 * When this is false then the order is not visible.
	 * Please do NOT use for business decisions, this is only for the decision about displaying or hiding the order.
	 * @return the displayFlag
	 */
	public Boolean getDisplayFlag() {
		return displayFlag;
	}

	/**
	 * DISPLAY_FLAG not nullable
	 * When this is false then the order is not visible. 
	 * Please do NOT use for business decisions, this is only for the decision about displaying or hiding the order.
	 * @param displayFlag the displayFlag to set
	 */
	public void setDisplayFlag(Boolean displayFlag) {
		this.displayFlag = displayFlag;
	}
}
