/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.persistence.entities.views;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.gdi.jpa.BaseEntity;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

@Entity
@Table(name = "VLGNR_INCOTERM_DELIV_TYPE")
public class IncotermDelivTypeEntity implements BaseEntity {
    private static final JcanLogger LOG_ENTITIES = JcanLoggerFactory.getLogger("persistence.entities");
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "INCOTERM_DELIV_TYPE_CD")
    private Long incotermDelivTypeCd;
    
    @Column(name = "SAREA_ID")
    private Long sareaId;
    
    @Column(name = "SAP_INCOTERM_CD")
    private String sapIncotermCd;
    
    @Column(name = "DELIVERY_TYPE_CD")
    private Long deliveryTypeCd;
    
    public static final String ATTR_INCOTERM_DELIV_TYPE_CD = "incotermDelivTypeCd";
    public static final String ATTR_SAREA_ID = "sareaId";
    public static final String ATTR_SAP_INCOTERM_CD = "sapIncotermCd";
    public static final String ATTR_DELIVERY_TYPE_CD = "deliveryTypeCd";
    
    /**
     * PK. Surrogate key.
     * @return
     */
	public Long getIncotermDelivTypeCd() {
		return incotermDelivTypeCd;
	}
	
	/**
	 * FK tlgnc_sarea. LOGON Sales Area type.
	 * @return
	 */
	public Long getSareaId() {
		return sareaId;
	}
	
	/**
	 * FK tlgnr_sap_incoterm_bs. SAP contract incoterm type.
	 * @return
	 */
	public String getSapIncotermCd() {
		return sapIncotermCd;
	}
	
	/**
	 * FK tlgnr_delivery_type_bs. LOGON delivery type.
	 * @return
	 */
	public Long getDeliveryTypeCd() {
		return deliveryTypeCd;
	}  
	
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this);
		tsb.append(super.toString());
		if (LOG_ENTITIES.isDebugEnabled()) {
			tsb.append("incotermDelivTypeCd", incotermDelivTypeCd);
			tsb.append("sareaId", sareaId);
			tsb.append("sapIncotermCd", sapIncotermCd);
			tsb.append("deliveryTypeCd", deliveryTypeCd);
		}
		else if (LOG_ENTITIES.isInfoEnabled()) {
			tsb.append("incotermDelivTypeCd", incotermDelivTypeCd);
		}
		return tsb.toString();
	}
}
