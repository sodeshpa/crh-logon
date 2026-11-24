/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author AdNovum Informatik AG
 */
public class UomConversionResult implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final BigDecimal quantity;
	private final Long numerator;
	private final Long denominator;
	
	/**
	 * @param quantity
	 * @param numerator
	 * @param denominator
	 */
	public UomConversionResult(BigDecimal quantity, Long numerator, Long denominator) {
		this.quantity = quantity;
		this.numerator = numerator;
		this.denominator = denominator;
	}
	
	public BigDecimal getQuantity() {
		return quantity;
	}
	
	public Long getNumerator() {
		return numerator;
	}
	
	public Long getDenominator() {
		return denominator;
	}

	@Override
	public String toString() {
		return "UomConversionResult [quantity=" + quantity + ", numerator=" + numerator
				+ ", denominator=" + denominator + "]";
	}
	
}
