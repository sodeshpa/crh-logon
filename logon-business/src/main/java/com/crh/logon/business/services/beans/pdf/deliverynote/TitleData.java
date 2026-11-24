/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans.pdf.deliverynote;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
public class TitleData implements Serializable {
	private static final long serialVersionUID = -7549499661675703278L;

	private String orderNumber;
	
	private String altOrderNumber;
	
	private String altOrderNumber2;
	
	private String deliveryNoteDate;
	
	private String shippingPoint;
	
	private String deliveryPlant;
	
	private String label_deliveryNote;
	
	private String label_altOrderNumber;
	
	private String label_altOrderNumber2;
	
	public boolean renderAltOrderNumber() {
		return StringUtils.isNotEmpty(altOrderNumber);
	}
	
	public boolean renderAltOrderNumber2() {
		return StringUtils.isNotEmpty(altOrderNumber2);
	}

	public String getAltOrderNumber() {
		return altOrderNumber;
	}

	public void setAltOrderNumber(String altOrderNumber) {
		this.altOrderNumber = altOrderNumber;
	}

	public String getAltOrderNumber2() {
		return altOrderNumber2;
	}

	public void setAltOrderNumber2(String altOrderNumber2) {
		this.altOrderNumber2 = altOrderNumber2;
	}

	public String getLabel_altOrderNumber() {
		return label_altOrderNumber;
	}

	public void setLabel_altOrderNumber(String label_altOrderNumber) {
		this.label_altOrderNumber = label_altOrderNumber;
	}

	public String getLabel_altOrderNumber2() {
		return label_altOrderNumber2;
	}

	public void setLabel_altOrderNumber2(String label_altOrderNumber2) {
		this.label_altOrderNumber2 = label_altOrderNumber2;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getWsDeliveryNoteNumber() {
		return altOrderNumber;
	}

	public void setWsDeliveryNoteNumber(String wsDeliveryNoteNumber) {
		this.altOrderNumber = wsDeliveryNoteNumber;
	}

	public String getDeliveryNoteDate() {
		return deliveryNoteDate;
	}

	public void setDeliveryNoteDate(String deliveryNoteDate) {
		this.deliveryNoteDate = deliveryNoteDate;
	}

	public String getShippingPoint() {
		return shippingPoint;
	}

	public void setShippingPoint(String shippingPoint) {
		this.shippingPoint = shippingPoint;
	}

	public String getDeliveryPlant() {
		return deliveryPlant;
	}

	public void setDeliveryPlant(String deliveryPlant) {
		this.deliveryPlant = deliveryPlant;
	}

	public String getLabel_deliveryNote() {
		return label_deliveryNote;
	}

	public void setLabel_deliveryNote(String label_deliveryNote) {
		this.label_deliveryNote = label_deliveryNote;
	}
	
	
}
