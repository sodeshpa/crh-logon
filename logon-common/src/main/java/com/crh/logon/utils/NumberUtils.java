/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author AdNovum Informatik AG
 */
public class NumberUtils {
	
	private final static Pattern MOBILE_NR_PATTERN = Pattern.compile("^\\+{1}[\\d]{2}[\\d\\s]{5,}$");

	private static final DecimalFormatSymbols decSymbols = new DecimalFormatSymbols();
		
	private static final DecimalFormat numFormatter = new DecimalFormat("###,##0.00", decSymbols);
	private static final DecimalFormat intNumFormatter = new DecimalFormat("###,##0", decSymbols);

	static {
		decSymbols.setGroupingSeparator('\'');
	}
	
	/**
	 * Validate the given mobile number.
	 * 
	 * @param mobileNr
	 * @return
	 * 			<code>true</code> if the given mobile number is not null and valid, <code>false</code>
	 * 			otherwise.
	 */
	public static boolean validateMobileNr(String mobileNr) {
		if (StringUtils.empty(mobileNr)) {
			return false;
		}
		else {
			final Matcher matcher = MOBILE_NR_PATTERN.matcher(mobileNr);
			return matcher.matches();			
		}
	}
	

	public static String formatNumber(BigDecimal number) {
		if (number == null) { 
			return "";
		}
		else {
			synchronized (numFormatter) {
				return numFormatter.format(number);
			}			
		}
	}
	
	public static String formatNumber(Double number) {
		if (number == null) { 
			return "";
		}
		else {
			synchronized(numFormatter) {
				return numFormatter.format(number);
			}			
		}
	}
	
	public static String formatInteger(Integer i) {
		if (i == null) { 
			return "";
		}
		else {
			synchronized (intNumFormatter) {
				return intNumFormatter.format(i);
			}			
		}
	}
}
