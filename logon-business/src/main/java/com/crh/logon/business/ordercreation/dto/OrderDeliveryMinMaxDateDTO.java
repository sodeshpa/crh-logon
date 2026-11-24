package com.crh.logon.business.ordercreation.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

public class OrderDeliveryMinMaxDateDTO implements Serializable{
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(OrderDeliveryMinMaxDateDTO.class);
    private static final long serialVersionUID = 1L;

    private Date deliveryFromDat;
    private Date deliveryToDat;

    /**
	 * @param deliveryFromDat
	 * @param deliveryToDat
	 */
	public OrderDeliveryMinMaxDateDTO(Date deliveryFromDat, Date deliveryToDat) {
		super();
		this.deliveryFromDat = deliveryFromDat;
		this.deliveryToDat = deliveryToDat;
	}
    
    /**
	 * @return the deliveryFromDat
	 */
	public Date getDeliveryFromDat() {
		return deliveryFromDat;
	}

	/**
	 * @param deliveryFromDat the deliveryFromDat to set
	 */
	public void setDeliveryFromDat(Date deliveryFromDat) {
		this.deliveryFromDat = deliveryFromDat;
	}

	/**
	 * @return the deliveryToDat
	 */
	public Date getDeliveryToDat() {
		return deliveryToDat;
	}

	/**
	 * @param deliveryToDat the deliveryToDat to set
	 */
	public void setDeliveryToDat(Date deliveryToDat) {
		this.deliveryToDat = deliveryToDat;
	}


	public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this);
        tsb.append(super.toString());
        if (LOG.isDebugEnabled()) {
            tsb.append("deliveryFromDat", deliveryFromDat);
            tsb.append("deliveryFromDat", "deliveryFromDat");         
        }
        return tsb.toString();
    }
}
