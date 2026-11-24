/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.mop.util;

import java.util.HashMap;

import com.crh.logon.mop.webservices.Field_type0;
import com.crh.logon.mop.webservices.MOPData_type0;
import com.crh.logon.mop.webservices.MOPEvent_type0;


/**
 * @author AdNovum Informatik AG
 */
public class EventBeansUtil {
	public static final long STATUS_CODE_APPLICATION_ERROR_CD = 3;
	public static final long STATUS_CODE_MAND_FIELD_ERROR_CD = 2;
	public static final long STATUS_CODE_MAND_FIELD_MISSING_CD = 1;
	public static final long STATUS_CODE_OK_CD = 0;
	public static final long STATUS_CODE_TECHNICAL_ERROR_CD = -1;
	public static final long STATUS_CODE_RETRY_CD = -2;
	public static final long STATUS_CODE_WRONG_BEAN_CD = -3;
	public static final long STATUS_CODE_OUTDATED_PIN_CD = -4;
	public static final long STATUS_CODE_UNKNOWN_USER_CD = -5;
	public static final long STATUS_CODE_UNKNOWN_TRUCK_CD = -6;
	public static final long STATUS_CODE_UNKNOWN_PDA_CD = -7;
	public static final long STATUS_CODE_CARRIERMISSMATCH_CD = -8;

	/**
	 * Fill all key-value pairs into a map. key is lower cased and
	 * the map will be used by the LogonEventBeanFactory to fill
	 * the data into the corresponding event bean.
	 * 
	 * @param mopEventConsume
	 * @return
	 */
	public static HashMap data2Map(MOPEvent_type0 mopEvent) {
		MOPData_type0 mopData = mopEvent.getMOPData();
		HashMap map = new HashMap();
		if (mopData != null) {
			Field_type0[] t = mopData.getField();
			if (t == null) {
				return map;
			}
	        for (int i = 0; i < t.length; i++) {
	           	String name = t[i].getName();
	            String value = t[i].getValue();
	            map.put(name.toLowerCase(), value);
	        }
		}
        
        return map;
	}
}
