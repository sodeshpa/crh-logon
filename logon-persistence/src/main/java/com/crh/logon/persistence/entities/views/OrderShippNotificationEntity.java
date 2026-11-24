package com.crh.logon.persistence.entities.views;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import ch.nevis.gdi.jpa.BaseEntity;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;


@SuppressWarnings("unused")
@Entity
@Table(name = "VLGNC_ORDER_SHIPP_NOTIFICATION")
public class OrderShippNotificationEntity implements BaseEntity {
	
	private static final String GDI_JPA_GENERATOR_MARKER_VIEW = "VIEW_MARKER";
	private static final JcanLogger LOG_ENTITIES = JcanLoggerFactory.getLogger("persistence.entities");
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "VLGNC_ORDER_SHIPP_NOTIFICATION";
	
	
	public static final String ATTR_ORDER__ITEM_ID = "orderItemId";
	public static final String ATTR_ORDER_ID = "orderId";
	public static final String ATTR_DELIVERY_TYPE_CD = "deliveryTypeCd";
	public static final String ATTR_DISTRIBUTION_DEST_CD = "distributionDestCd";
	public static final String ATTR_ORDER_STATUS_CD = "orderStatusCd";
	public static final String ATTR_PLANNED_START = "plannedStart";
	public static final String ATT_PREV_LATE_DELIVERY ="prevLateDelivery";
	public static final String ATTR_TRANSMIT_TO_CARRIER_FLAG = "transmitToCarrierFlag";
	
	public static final long DBSIZE_ORDER_ITEM_ID = 12;
	public static final long DBSIZE_ORDER_ID = 12;
	public static final long DBSIZE_DELIVERY_TYPE_CD = 2;
	public static final long DBSIZE_DISTRIBUTION_DEST_CD = 4;
	public static final long DBSIZE_ORDER_STATUS_CD = 2;
	public static final long DBSIZE_PREV_LATE_DELIVERY = 4;
	
	
	@Id
	@Column(name = "ORDER_ID")
	private Long orderId;
	
	@Column(name = "DELIVERY_TYPE_CD")
	private Long deliveryTypeCd;
	
	@Column(name = "DISTRIBUTION_DEST_CD")
	private String distributionDestCd;
	
	@Column(name = "ORDER_STATUS_CD")
	private Long orderStatusCd;
	
	@Column(name="PLANNED_START")
	private Date plannedStart;
	
	@Column(name="PREV_LATE_DELIVERY")
	private Long prevLateDelivery;
	
	@Column(name = "TRANSMIT_TO_CARRIER_FLAG")
	private Boolean transmitToCarrierFlag;
	
	
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getDeliveryTypeCd() {
		return deliveryTypeCd;
	}

	public void setDeliveryTypeCd(Long deliveryTypeCd) {
		this.deliveryTypeCd = deliveryTypeCd;
	}

	public String getDistributionDestCd() {
		return distributionDestCd;
	}

	public void setDistributionDestCd(String distributionDestCd) {
		this.distributionDestCd = distributionDestCd;
	}

	public Long getOrderStatusCd() {
		return orderStatusCd;
	}

	public void setOrderStatusCd(Long orderStatusCd) {
		this.orderStatusCd = orderStatusCd;
	}

	public Date getPlannedStart() {
		return plannedStart;
	}

	public void setPlannedStart(Date plannedStart) {
		this.plannedStart = plannedStart;
	}

	public Long getPrevLateDelivery() {
		return prevLateDelivery;
	}

	public void setPrevLateDelivery(Long prevLateDelivery) {
		this.prevLateDelivery = prevLateDelivery;
	}

	public Boolean getTransmitToCarrierFlag() {
		return transmitToCarrierFlag;
	}

	public void setTransmitToCarrierFlag(Boolean transmitToCarrierFlag) {
		this.transmitToCarrierFlag = transmitToCarrierFlag;
	}
}
