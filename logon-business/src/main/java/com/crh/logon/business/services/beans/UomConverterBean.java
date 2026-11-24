/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans;

import java.io.Serializable;
import java.math.BigDecimal;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.constants.Constant;


/**
 * @author AdNovum Informatik AG
 */
final public class UomConverterBean implements Serializable {

	private static final long serialVersionUID = 2244114456748290306L;
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(UomConverterBean.class);
	private static final String SEP = "<->";
	
	private final String uomACd;
	private final String uomBCd;
	private final Long numeratorA2B;
	private final Long denominatorA2B;
	private final String key;
	
	public UomConverterBean(String uomACd, String uomBCd, Long numeratorA2B, Long denominatorA2B) {
		// [[
		Object[][] params = {{"uomACd", uomACd},
							{"uomBCd", uomBCd},
							{"numeratorA2B", numeratorA2B},
							{"denominatorA2B", denominatorA2B}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "UomConverterBean", params);
		}
		// ]]
		try {
			this.uomACd = uomACd;
			this.uomBCd = uomBCd;
			this.numeratorA2B = new Long(numeratorA2B);
			this.denominatorA2B = new Long(denominatorA2B);
			this.key = UomConverterBean.generateKey(this.uomACd, this.uomBCd);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public UomConversionResult convertQuantity(final String fromUomCd, final String toUomCd, final BigDecimal quantity) {
		// [[
		UomConversionResult result = null;
		Object[][] params = {{"fromUomCd", fromUomCd},
							{"toUomCd", toUomCd},
							{"quantity", quantity}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "convertQuantity", params);
		}
		// ]]
		try {
			String generatedKey = generateKey(fromUomCd, toUomCd);
			if (!this.key.equals(generatedKey)) {
				String errMsg = "invalid input Uom(s), can convert only: " + this.key;
				LOG.error(errMsg);
				throw new IllegalArgumentException(errMsg);
			}
			
			Long numeratorForConversion = null;
			Long denominatorForConversion = null;
			
			if (this.uomACd.equals(fromUomCd)) {
				numeratorForConversion = this.numeratorA2B;
				denominatorForConversion = this.denominatorA2B;
			}
			else {
				numeratorForConversion = this.denominatorA2B;
				denominatorForConversion = this.numeratorA2B;
			}
			
			final BigDecimal tmpQuantity = quantity.multiply(new BigDecimal(numeratorForConversion)).divide(new BigDecimal(denominatorForConversion), Constant.SCALE, BigDecimal.ROUND_HALF_UP);
			return result = new UomConversionResult(tmpQuantity, numeratorForConversion, denominatorForConversion);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public static String generateKey(String uomACd, String uomBCd) {
		// [[
		String result = null;
		Object[][] params = {{"uomACd", uomACd},
							{"uomBCd", uomBCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(UomConverterBean.class, "generateKey", params);
		}
		// ]]
		try {
			if (uomACd.compareTo(uomBCd) <= 0) {
				return result = uomACd + SEP + uomBCd;
			}
			else {
				return result = uomBCd + SEP + uomACd;
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public static String getSep() {
		return SEP;
	}

	public String getUomACd() {
		return uomACd;
	}

	public String getUomBCd() {
		return uomBCd;
	}

	public Long getNumeratorA2B() {
		return numeratorA2B;
	}

	public Long getDenominatorA2B() {
		return denominatorA2B;
	}

	public String getKey() {
		return key;
	}
}
