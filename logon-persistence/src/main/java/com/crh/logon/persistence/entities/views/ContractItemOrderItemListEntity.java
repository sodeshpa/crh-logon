/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.persistence.entities.views;

import java.math.BigDecimal;

import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import ch.nevis.gdi.jpa.BaseEntity;

import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;



@SuppressWarnings("unused")
@Entity
@Table(name = "VLGNC_CONTRACTITEM_OI_LST")
public class ContractItemOrderItemListEntity implements BaseEntity {
	private static final String GDI_JPA_GENERATOR_MARKER_VIEW = "VIEW_MARKER";
    private static final JcanLogger LOG_ENTITIES = JcanLoggerFactory.getLogger("persistence.entities");
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "VLGNC_CONTRACTITEM_OI_LST";


    @Id
    @Column(name = "COMPOSITE_ID")
    private String compositeId;

    @Column(name = "SAP_CONTRACT_ITEM_ID")
    private Long sapContractItemId;

    @Column(name = "DELIVERED_QUANTITY")
    private BigDecimal deliveredQuantity;

    @Column(name = "SENT_ORDER_ITEM_NO")
    private Long sentOrderItemNo;

    @Column(name = "DELETED_FLAG")
    private Boolean deletedFlag;

    @Column(name = "ORDER_ITEM_NO")
    private Long orderItemNo;

    @Column(name = "ORDER_NUMBER")
    private Long orderNumber;

    // Fieldnames
    public static final String ATTR_COMPOSITE_ID = "compositeId";
    public static final String ATTR_SAP_CONTRACT_ITEM_ID = "sapContractItemId";
    public static final String ATTR_DELIVERED_QUANTITY = "deliveredQuantity";
    public static final String ATTR_DELETED_FLAG = "deletedFlag";
    public static final String ATTR_SENT_ORDER_ITEM_NO = "sentOrderItemNo";
    public static final String ATTR_ORDER_ITEM_NO = "orderItemNo";
    public static final String ATTR_ORDER_NUMBER = "orderNumber";

    // Field length on DB
    public static final long DBSIZE_COMPOSITE_ID = 43;    
    public static final long DBSIZE_SAP_CONTRACT_ITEM_ID = 12;
    public static final long DBSIZE_DELIVERED_QUANTITY = 12;
    public static final long DBSIZE_DELETED_FLAG = 1;
    public static final long DBSIZE_SENT_ORDER_ITEM_NO = 3;
    public static final long DBSIZE_ORDER_ITEM_NO = 3;
    public static final long DBSIZE_ORDER_NUMBER = 12;
    

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
     * DELIVERED_QUANTITY is nullable
     * 
     * Delivered quantity per order item / contract item entry stored in the TARGET_QUANTITY_UOM_CD units of the SAP contract item.
     */
    public void setDeliveredQuantity(java.math.BigDecimal deliveredQuantity) {
        this.deliveredQuantity = deliveredQuantity;
    }

    /**
     * DELIVERED_QUANTITY is nullable
     * 
     * Delivered quantity per order item / contract item entry stored in the TARGET_QUANTITY_UOM_CD units of the SAP contract item.
     */
    public java.math.BigDecimal getDeliveredQuantity() {
        return deliveredQuantity;
    }

    /**
     * DELETED_FLAG is NOT nullable
     * 
     * Indicator if order item / contract item entry has been deleted.
     */
    public void setDeletedFlag(Boolean deletedFlag) {
        this.deletedFlag = deletedFlag;
    }

    /**
     * DELETED_FLAG is NOT nullable
     * 
     * Indicator if order item / contract item entry has been deleted.
     */
    public Boolean getDeletedFlag() {
        return deletedFlag;
    }
    
    /**
     * SENT_ORDER_ITEM_NO is NOT nullable
     * Field length on DB: 3
     * Order item number as being sent to SAP in the outgoing interfaces.
     */
    public void setSentOrderItemNo(Long sentOrderItemNo) {
        this.sentOrderItemNo = sentOrderItemNo;
    }

    /**
     * SENT_ORDER_ITEM_NO is NOT nullable
     * Field length on DB: 3
     * Order item number as being sent to SAP in the outgoing interfaces.
     */
    public Long getSentOrderItemNo() {
        return sentOrderItemNo;
    }

    /**
     * ORDER_ITEM_NO is NOT nullable
     * Field length on DB: 3
     * The order item number, human readable: Start with 10, and use increments of 10: 10, 20, 30, ... Together with tlgnc_order.order_number this identifies a certain order item, for customers and external systems like SAP. Even more importantly, the lowest ORDER_ITEM_NO defines the FIRST order item, not the order given by the DB nor the ORDER_ID.
     */
    public void setOrderItemNo(Long orderItemNo) {
        this.orderItemNo = orderItemNo;
    }

    /**
     * ORDER_ITEM_NO is NOT nullable
     * Field length on DB: 3
     * The order item number, human readable: Start with 10, and use increments of 10: 10, 20, 30, ... Together with tlgnc_order.order_number this identifies a certain order item, for customers and external systems like SAP. Even more importantly, the lowest ORDER_ITEM_NO defines the FIRST order item, not the order given by the DB nor the ORDER_ID.
     */
    public Long getOrderItemNo() {
        return orderItemNo;
    }
    
    /**
     * ORDER_NUMBER is nullable
     * Field length on DB: 12
     * The order number, used for external systems (output, SAP). Generated independently of ORDER_ID, based on sequence SLGNC_ORDER_NUMBER.
     */
    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * ORDER_NUMBER is nullable
     * Field length on DB: 12
     * The order number, used for external systems (output, SAP). Generated independently of ORDER_ID, based on sequence SLGNC_ORDER_NUMBER.
     */
    public Long getOrderNumber() {
        return orderNumber;
    }
    
    /**
     * COMPOSITE_ID is nullable
     * Field length on DB: 43
     */
    public void setCompositeId(String compositeId) {
        this.compositeId = compositeId;
    }

    /**
     * COMPOSITE_ID is nullable
     * Field length on DB: 43
     */
    public String getCompositeId() {
        return compositeId;
    }
    
    
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this);
        tsb.append(super.toString());
        if (LOG_ENTITIES.isDebugEnabled()) {
            tsb.append("compositeId", compositeId);
            tsb.append("sapContractItemId", sapContractItemId);
            tsb.append("deliveredQuantity", "deliveredQuantity");
            tsb.append("deletedFlag", "deletedFlag");
            tsb.append("sentOrderItemNo", "sentOrderItemNo");
            tsb.append("orderItemNo", "orderItemNo");
            tsb.append("orderNumber", orderNumber);
        }
        else if (LOG_ENTITIES.isInfoEnabled()) {
            tsb.append("compositeId", compositeId); // ID
        }
        return tsb.toString();
    }
    
}
