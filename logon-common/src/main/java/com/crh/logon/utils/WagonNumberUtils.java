/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author AdNovum Informatik AG
 */
public class WagonNumberUtils {

	private static final Pattern WAGON_NO_PATTERN = Pattern.compile("^(\\d{4})(\\d{4})(\\d{3})(\\d{1})$");
	private static final Pattern WAGON_NO_DISPLAY_PATTERN = Pattern.compile("^(\\d{4})\\s(\\d{4})\\s(\\d{3})-(\\d{1})$");

	public static String toDisplayNo(String no) {
		Matcher m = WAGON_NO_PATTERN.matcher(no);
		if ( (m.matches()) && (m.groupCount() == 4) ) {
			return m.group(1) + " " + m.group(2) + " " + m.group(3) + "-" + m.group(4);  
		}
		else {
			return no;
		}
	}
	
	public static String toDbNo(String no) {
		Matcher m = WAGON_NO_DISPLAY_PATTERN.matcher(no.trim());
		if ( (m.matches()) && (m.groupCount() == 4) ) {
			return m.group(1) +  m.group(2) + m.group(3) + m.group(4);  
		}
		else {
			return no;
		}
	}
}
