/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import com.crh.logon.utils.ResourceBundleUtil;

import java.util.Locale;

/**
 * Struts' PropertyMessageResources is supposed to read properties the same way as 
 * java.util.PropertyResourceBundle. Unfortunately, this is not the case 
 * (see http://issues.apache.org/struts/browse/STR-2077) in v1.2.9. However, Logon always uses a 
 * known, exisiting Locale as set in the user's preferences. Therefore, PropertyResourceBundle can 
 * be used and no reference to struts is needed in the business tier.
 * 
 * Also note that the JSTL uses a third method when a non-sepcified locale is used, see:
 * http://struts.apache.org/1.x/apidocs/org/apache/struts/util/PropertyMessageResources.html
 * 
 * @author AdNovum Informatik AG
 */
public class Message {

	public static boolean messageExists(Locale locale, String key) {
		return ResourceBundleUtil.messageExists(locale, key);
	}
	
	public static String lookup(Locale locale, String key) {
		return ResourceBundleUtil.lookup(locale, key);
	}
	
	public static String lookup(Locale locale, String key, Object... messageArguments) {
		return ResourceBundleUtil.lookup(locale, key, messageArguments);
	}
}
