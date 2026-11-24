/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.apache.commons.lang.StringUtils;

/**
 * Util class for helping with generated pdf data formatting and display
 * 
 * @author AdNovum Informatik AG
 */
public class PdfUtil {
	public static final DecimalFormat WEIGHT_FORMAT = new DecimalFormat("0.00");
	public static final String DASH = "-";
	
	public static String getWtihMaxLength(String value, int maxLength) {
		if (value != null && value.length() > maxLength) {
			return value.substring(0, maxLength);
		}
	
		return value;
	}
	
	public static String getValueOrEmptyStringWeight(BigDecimal decimal, String uom) {
		if (decimal == null) {
			return DASH;
		}
		return WEIGHT_FORMAT.format(decimal) + " " + uom;
	}
	
	public static String getValueOrDash(String value) {
		return StringUtils.defaultString(value, DASH);
	}
}
