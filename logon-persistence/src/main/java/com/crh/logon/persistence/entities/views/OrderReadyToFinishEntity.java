/*
 * Author : AdNovum Informatik AG
 * database vendor: oracle
 * database application id: LGN
 */
package com.crh.logon.persistence.entities.views;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.gdi.jpa.BaseEntity;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;



@SuppressWarnings("unused")
@Entity
@Table(name = "VLGNC_ORDER_READY_TO_FINISH")
public class OrderReadyToFinishEntity implements BaseEntity {
	private static final String GDI_JPA_GENERATOR_MARKER_VIEW = "VIEW_MARKER";
    private static final JcanLogger LOG_ENTITIES = JcanLoggerFactory.getLogger("persistence.entities");
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "VLGNC_ORDER_READY_TO_FINISH";


    @Id
    @Column(name = "ORDER_ID")
    private Long orderId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ACTUAL_DELIVERY_DAT")
    private Date actualDeliveryDat;

    @Column(name = "FORCE_TO_SAP_FLAG")
    private Boolean forceToSapFlag;

    // Fieldnames
    public static final String ATTR_ORDER_ID = "orderId";
    public static final String ATTR_ACTUAL_DELIVERY_DAT = "actualDeliveryDat";

    // Field length on DB
    public static final long DBSIZE_ORDER_ID = 12;

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
     * ACTUAL_DELIVERY_DAT is nullable
     * 
     */
    public void setActualDeliveryDat(Date actualDeliveryDat) {
        this.actualDeliveryDat = actualDeliveryDat;
    }

    /**
     * ACTUAL_DELIVERY_DAT is nullable
     * 
     */
    public Date getActualDeliveryDat() {
        return actualDeliveryDat;
    }

    /**
     * FORCE_TO_SAP_FLAG is NOT nullable
     * 
     */
	public Boolean getForceToSapFlag() {
		return forceToSapFlag;
	}

	/**
     * FORCE_TO_SAP_FLAG is NOT nullable
     * 
     */
	public void setForceToSapFlag(Boolean forceToSapFlag) {
		this.forceToSapFlag = forceToSapFlag;
	}

	public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this);
        tsb.append(super.toString());
        if (LOG_ENTITIES.isDebugEnabled()) {
            tsb.append("orderId", "orderId");
            tsb.append("actualDeliveryDat", "actualDeliveryDat");
            tsb.append("forceToSapFlag", "forceToSapFlag");
        }
        else if (LOG_ENTITIES.isInfoEnabled()) {
            tsb.append("orderId", orderId); // ID
        }
        return tsb.toString();
    }

    /* convenience:
        orderReadyToFinishEntity.getActualDeliveryDat();

        orderReadyToFinishEntity.setActualDeliveryDat(actualDeliveryDat); // nullable
    */
}
