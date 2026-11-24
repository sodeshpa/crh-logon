/*
 * Author : AdNovum Informatik AG
 *
 */
package com.crh.logon.persistence.entities.views;

import java.util.Date;

import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import ch.nevis.gdi.jpa.BaseEntity;

import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;



@SuppressWarnings("unused")
@Entity
@Table(name = "VLGNC_CONTRACT_ITEM_QTY")
public class ContractItemQtyEntity implements BaseEntity {
	private static final String GDI_JPA_GENERATOR_MARKER_VIEW = "VIEW_MARKER";
    private static final JcanLogger LOG_ENTITIES = JcanLoggerFactory.getLogger("persistence.entities");
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "VLGNC_CONTRACT_ITEM_QTY";


    @Id
    @Column(name = "SAP_CONTRACT_ITEM_ID")
    private Long sapContractItemId;

    @Column(name = "CONTRACT_ITEM_NO")
    private Long contractItemNo;

    @Column(name = "AP_GROUP_LEADER_ID")
    private Long apGroupLeaderId;

    @Column(name = "TARGET_QUANTITY")
    private java.math.BigDecimal targetQuantity;

    @Column(name = "REMAINING_QUANTITY")
    private java.math.BigDecimal remainingQuantity;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CONTRACT_ITEM_CRE_TS")
    private Date contractItemCreTs;


    // Fieldnames
    public static final String ATTR_SAP_CONTRACT_ITEM_ID = "sapContractItemId";
    public static final String ATTR_CONTRACT_ITEM_NO = "contractItemNo";
    public static final String ATTR_AP_GROUP_LEADER_ID = "apGroupLeaderId";
    public static final String ATTR_TARGET_QUANTITY = "targetQuantity";
    public static final String ATTR_REMAINING_QUANTITY = "remainingQuantity";
    public static final String ATTR_CONTRACT_ITEM_CRE_TS = "contractItemCreTs";

    // Field length on DB
    public static final long DBSIZE_SAP_CONTRACT_ITEM_ID = 12;
    public static final long DBSIZE_CONTRACT_ITEM_NO = 6;
    public static final long DBSIZE_AP_GROUP_LEADER_ID = 12;
    public static final long DBSIZE_TARGET_QUANTITY = 17;
    public static final long DBSIZE_PRECISION_TARGET_QUANTITY = 3;
    public static final long DBSIZE_REMAINING_QUANTITY = 17;
    public static final long DBSIZE_PRECISION_REMAINING_QUANTITY = 4;

    /**
     * SAP_CONTRACT_ITEM_ID is NOT nullable
     * Field length on DB: 12
     */
    public void setSapContractItemId(Long sapContractItemId) {
        this.sapContractItemId = sapContractItemId;
    }

    /**
     * SAP_CONTRACT_ITEM_ID is NOT nullable
     * Field length on DB: 12
     */
    public Long getSapContractItemId() {
        return sapContractItemId;
    }


    /**
     * CONTRACT_ITEM_NO is NOT nullable
     * Field length on DB: 6
     */
    public void setContractItemNo(Long contractItemNo) {
        this.contractItemNo = contractItemNo;
    }

    /**
     * CONTRACT_ITEM_NO is NOT nullable
     * Field length on DB: 6
     */
    public Long getContractItemNo() {
        return contractItemNo;
    }

    /**
     * AP_GROUP_LEADER_ID is nullable
     * Field length on DB: 12
     */
    public void setApGroupLeaderId(Long apGroupLeaderId) {
        this.apGroupLeaderId = apGroupLeaderId;
    }

    /**
     * AP_GROUP_LEADER_ID is nullable
     * Field length on DB: 12
     */
    public Long getApGroupLeaderId() {
        return apGroupLeaderId;
    }

    /**
     * TARGET_QUANTITY is nullable
     * 
     */
    public void setTargetQuantity(java.math.BigDecimal targetQuantity) {
        this.targetQuantity = targetQuantity;
    }

    /**
     * TARGET_QUANTITY is nullable
     * 
     */
    public java.math.BigDecimal getTargetQuantity() {
        return targetQuantity;
    }

    /**
     * REMAINING_QUANTITY is nullable
     * 
     */
    public void setRemainingQuantity(java.math.BigDecimal remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    /**
     * REMAINING_QUANTITY is nullable
     * 
     */
    public java.math.BigDecimal getRemainingQuantity() {
        return remainingQuantity;
    }

    /**
     * CONTRACT_ITEM_CRE_TS is nullable
     * 
     */
    public void setContractItemCreTs(Date contractItemCreTs) {
        this.contractItemCreTs = contractItemCreTs;
    }

    /**
     * CONTRACT_ITEM_CRE_TS is nullable
     * 
     */
    public Date getContractItemCreTs() {
        return contractItemCreTs;
    }




    
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this);
        tsb.append(super.toString());
        if (LOG_ENTITIES.isDebugEnabled()) {
            tsb.append("sapContractItemId", "sapContractItemId");
            tsb.append("contractItemNo", "contractItemNo");
            tsb.append("apGroupLeaderId", "apGroupLeaderId");
            tsb.append("targetQuantity", "targetQuantity");
            tsb.append("remainingQuantity", "remainingQuantity");
            tsb.append("contractItemCreTs", "contractItemCreTs");
        }
        else if (LOG_ENTITIES.isInfoEnabled()) {
            tsb.append("sapContractItemId", sapContractItemId); // ID
        }
        return tsb.toString();
    }

    /* convenience:
        contractItemQtyEntity.getContractItemNo();
        contractItemQtyEntity.getApGroupLeaderId();
        contractItemQtyEntity.getTargetQuantity();
        contractItemQtyEntity.getRemainingQuantity();
        contractItemQtyEntity.getContractItemCreTs();

        contractItemQtyEntity.setContractItemNo(contractItemNo); // NOT nullable
        contractItemQtyEntity.setApGroupLeaderId(apGroupLeaderId); // nullable
        contractItemQtyEntity.setTargetQuantity(targetQuantity); // nullable
        contractItemQtyEntity.setRemainingQuantity(remainingQuantity); // nullable
        contractItemQtyEntity.setContractItemCreTs(contractItemCreTs); // nullable
    */
}
