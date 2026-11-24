package com.crh.logon.persistence.entities.views.stagingarea;

import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import ch.nevis.gdi.jpa.BaseEntity;

import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;



@SuppressWarnings("unused")
@MappedSuperclass
public abstract class StagingAreaNewDataAvailableBaseEntity implements BaseEntity {
    private static final JcanLogger LOG_ENTITIES = JcanLoggerFactory.getLogger("persistence.entities");
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "HJID")
    private Long hjid;

    @Temporal(TemporalType.TIMESTAMP)    
    @Column(name = "NEW_DATA_AVAILABLE_TS")
    private Date newDataAvailableTs;

    // Fieldnames
    public static final String ATTR_HJID = "hjid";
    public static final String ATTR_NEW_DATA_AVAILABLE_TS = "newDataAvailableTs";


    // Field length on DB
    public static final long DBSIZE_HJID = 19;

    
    /**
	 * @return the hjid
	 */
	public Long getHjid() {
		return hjid;
	}


	/**
	 * @param hjid the hjid to set
	 */
	public void setHjid(Long hjid) {
		this.hjid = hjid;
	}


	/**
	 * @return the newDataAvailableTs
	 */
	public Date getNewDataAvailableTs() {
		return newDataAvailableTs;
	}


	/**
	 * @param newDataAvailableTs the newDataAvailableTs to set
	 */
	public void setNewDataAvailableTs(Date newDataAvailableTs) {
		this.newDataAvailableTs = newDataAvailableTs;
	}


	public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this);
        tsb.append(super.toString());
        if (LOG_ENTITIES.isDebugEnabled()) {
            tsb.append("hjid", "hjid");
            tsb.append("newDataAvailableTs", "newDataAvailableTs");
        }
        else if (LOG_ENTITIES.isInfoEnabled()) {
            tsb.append("hjid", hjid); // ID
        }
        return tsb.toString();
    }
}
