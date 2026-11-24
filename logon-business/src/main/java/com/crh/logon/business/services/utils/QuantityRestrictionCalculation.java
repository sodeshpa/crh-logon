/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores the details of the open quantity calculation for a contract item.
 * 
 * @author AdNovum Informatik AG
 */
public class QuantityRestrictionCalculation implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Contains the target quantity minus all the quantities of items in delivered and open orders belonging
	 * to the same contract item that this bean is used for.  After the calculation, this field should never
	 * be null.
	 */
	private BigDecimal openQuantity;

	private List<String> deliveries = new ArrayList<String>();
	private BigDecimal sumDeliveries = BigDecimal.ZERO;

	private List<String> nonDeliveries = new ArrayList<String>();
	private BigDecimal sumNonDeliveries = BigDecimal.ZERO;

	private List<String> conversionFailures = new ArrayList<String>();

	private BigDecimal sumLoaded = BigDecimal.ZERO;

	public BigDecimal getOpenQuantity() {
		return openQuantity;
	}

	public void setOpenQuantity(BigDecimal openQuantity) {
		this.openQuantity = openQuantity;
	}

	public List<String> getDeliveries() {
		return deliveries;
	}

	public void setDeliveries(List<String> deliveries) {
		this.deliveries = deliveries;
	}

	public BigDecimal getSumDeliveries() {
		return sumDeliveries;
	}

	public void setSumDeliveries(BigDecimal sumDeliveries) {
		this.sumDeliveries = sumDeliveries;
	}

	public List<String> getNonDeliveries() {
		return nonDeliveries;
	}

	public void setNonDeliveries(List<String> nonDeliveries) {
		this.nonDeliveries = nonDeliveries;
	}

	public BigDecimal getSumNonDeliveries() {
		return sumNonDeliveries;
	}

	public void setSumNonDeliveries(BigDecimal sumNonDeliveries) {
		this.sumNonDeliveries = sumNonDeliveries;
	}

	public List<String> getConversionFailures() {
		return conversionFailures;
	}

	public void setConversionFailures(List<String> conversionFailures) {
		this.conversionFailures = conversionFailures;
	}

	public BigDecimal getSumLoaded() {
		return sumLoaded;
	}
	
	public void setSumLoaded(BigDecimal sumLoaded) {
		this.sumLoaded = sumLoaded;
	}
	
	public String toString() {
		String str = "";
		str += "][openQuantity=" + openQuantity;
		str += "][sumDeliveries=" + sumDeliveries;
		str += "][sumNonDeliveries=" + sumNonDeliveries;
		str += "][sumLoaded=" + sumLoaded;
		return str;
	}

}
