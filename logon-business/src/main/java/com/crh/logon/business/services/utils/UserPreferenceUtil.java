/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Date;

import java.math.BigDecimal;

import com.crh.logon.persistence.entities.admin.PreferencesEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * Utility class for the User Preferences feature.
 * 
 * @author AdNovum Informatik AG
 */
public class UserPreferenceUtil {

	/**
	 * Returns the corresponding value for the given preferencesKey from the preferencesMap as String.
	 * Handles the case when the value is null.
	 * 
	 * @param preferencesMap This stores the user setting key-value pairs.
	 * @param preferencesKey This is the key which is used to retrieve the value.
	 * 
	 * @return String value is String. Null otherwise.
	 */
	public static String getString(Map<String, Object> preferencesMap, PreferencesKey preferencesKey) {
		if (preferencesMap == null || preferencesKey == null) {
			return null;
		}
		
		final Object value = preferencesMap.get(preferencesKey.name());
		if (value == null) {
			return null;
		}
		
		if (!(value instanceof String)) {
			throw new LogonException("invalid type of attribute found (String expected)", Reason.RES_IMPL_BUG);
		}
		else {
			return (String)value;
		}
	}

	/**
	 * Returns the corresponding value for the given preferencesKey from the preferencesMap as Long.
	 * Handles the case when the value is null.
	 * 
	 * @param preferencesMap This stores the user setting key-value pairs.
	 * @param preferencesKey This is the key which is used to retrieve the value.
	 * 
	 * @return Long value if the value can be parsed as Long. Null otherwise.
	 */
	public static Long getLong(Map<String, Object> preferencesMap, PreferencesKey preferencesKey) {
		if (preferencesMap == null || preferencesKey == null) {
			return null;
		}
		
		final Object value = preferencesMap.get(preferencesKey.name());
		if (value == null) {
			return null;
		}
		
		if (!(value instanceof BigDecimal)) {
			throw new LogonException("invalid type of attribute found (BigDecimal expected)", Reason.RES_IMPL_BUG);
		}
		else {
			return ((BigDecimal) value).longValue();
		}

	}	
	
	/**
	 * Returns the corresponding value for the given preferencesKey from the preferencesMap as Integer.
	 * Handles the case when the value is null.
	 * 
	 * @param preferencesMap This stores the user setting key-value pairs.
	 * @param preferencesKey This is the key which is used to retrieve the value.
	 * 
	 * @return Integer value if the value can be parsed as Integer. Null otherwise.
	 */
	public static Integer getInteger(Map<String, Object> preferencesMap, PreferencesKey preferencesKey) {
		if (preferencesMap == null || preferencesKey == null) {
			return null;
		}
		
		final Object value = preferencesMap.get(preferencesKey.name());
		if (value == null) {
			return null;
		}
		
		if (!(value instanceof BigDecimal)) {
			throw new LogonException("invalid type of attribute found (BigDecimal expected)", Reason.RES_IMPL_BUG);
		}
		else {
			return ((BigDecimal) value).intValue();
		}
	}
	
	/**
	 * Returns the corresponding value for the given preferencesKey from the preferencesMap as Boolean.
	 * Handles the case when the value is null.
	 * 
	 * @param preferencesMap This stores the user setting key-value pairs.
	 * @param preferencesKey This is the key which is used to retrieve the value.
	 * 
	 * @return Boolean value if the value can be parsed as Long. Null otherwise.
	 */
	public static Boolean getBoolean(Map<String, Object> preferencesMap, PreferencesKey preferencesKey) {

		if (preferencesMap == null || preferencesKey == null) {
			return null;
		}
		
		final Object value = preferencesMap.get(preferencesKey.name());
		if (value == null) {
			return null;
		}
		
		if (!(value instanceof BigDecimal)) {
			throw new LogonException("invalid type of attribute found (BigDecimal expected)", Reason.RES_IMPL_BUG);
		}
		else {
			BigDecimal dValue = (BigDecimal)value;
			if (dValue.intValue() != 0 && dValue.intValue() != 1) {
				throw new LogonException("invalid value of attribute found (1 or 0 expected)", Reason.RES_IMPL_BUG);
			}
			return (dValue.intValue() == 1);
		}
		
	}	
	
	/**
	 * Creates a Map (key to value association) from the list of Preferences entities.
	 * Never returns null.
	 * 
	 * @param preferencesList A list of PreferencesEntity objects for the Map creation.
	 * 
	 * @return A map with keys as Strings as declared in PreferencesKey and 
	 * values as Objects that could be one of:
	 * - String,
	 * - Long,
	 * - String (concatenated values like this: "1,2.3.4"),
	 * - List<Long> (after the parse of concatenated values).
	 */
	public static Map<String, Object> createPreferencesMapfromList(List<PreferencesEntity> preferencesList) {
		final Map<String, Object> preferencesMap = new HashMap<String, Object>();
			
		if (preferencesList == null || preferencesList.isEmpty()) {
			return preferencesMap;
		}
			
		for (PreferencesEntity preference : preferencesList) {
			final String preferenceKey = preference.getPreferenceKey();
			
			final Date date = preference.getValDat();
			final String string = preference.getValStr();
			final BigDecimal num = preference.getValNum();
			
			if (date != null){
				preferencesMap.put(preferenceKey, date);
			}
			
			if (string != null){
				preferencesMap.put(preferenceKey, string);
			}
			
			if (num != null){
				preferencesMap.put(preferenceKey, num);
			}
		}
		return preferencesMap;
		
	}
	
}
