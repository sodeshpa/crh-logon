/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.persistence.entities.refdata;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.Version;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import ch.nevis.gdi.jpa.BaseEntity;
import ch.nevis.gdi.jpa.ThreadLocalUserInfo;

import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;


@SuppressWarnings("unused")
@Entity
@Table(name = "TLGNR_OUTPUTDOC_TYPE_BS")
public class OutputDocumentTypeBsEntity  implements BaseEntity {
    private static final JcanLogger LOG_ENTITIES = JcanLoggerFactory.getLogger("persistence.entities");
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "TLGNR_OUTPUTDOC_TYPE_BS";


    @Id
    @SequenceGenerator(name = "slgnr_outputdoc_type_bs", allocationSize = 1, sequenceName = "slgnr_outputdoc_type_bs")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "slgnr_outputdoc_type_bs")
    @Column(name = "OUTPUTDOC_TYPE_CD")
    private Long outputdocTypeCd;

    @Column(name = "TYPENAME")
    private String typeName;

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
            tsb.append("outputdocTypeCd", outputdocTypeCd);
            tsb.append("typeName", "typeName");
            tsb.append("ctlCreUid", "ctlCreUid");
            tsb.append("ctlCreTs", "ctlCreTs");
            tsb.append("ctlModUid", "ctlModUid");
            tsb.append("ctlModTs", "ctlModTs");
        }
        else if (LOG_ENTITIES.isInfoEnabled()) {
            tsb.append("outputdocTypeCd", outputdocTypeCd); // ID
        }
        return tsb.toString();
    }

}