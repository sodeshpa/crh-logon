/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans;

public class ConversionFactor {
	
	private final Long numerator;
	private final Long denominator;
	
	public ConversionFactor(Long numerator, Long denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
	}
	
	public Long getNumerator() {
		return numerator;
	}
	
	public Long getDenominator() {
		return denominator;
	}
	
}