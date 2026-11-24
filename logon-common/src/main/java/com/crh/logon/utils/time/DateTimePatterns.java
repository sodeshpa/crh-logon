/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.utils.time;

/**
 * @author AdNovum Informatik AG
 */
public interface DateTimePatterns {
	
	/*
	 *  Note: After adding a new pattern you should add it to the 
	 *  DateTimeFormat.customDateTimeFormatters Map.
	 */
	
	public static String JS_DATE = "d.m.Y";
	public static String JS_HOUR_MINUTE = "H:i";

	public static String JS_USER_ACTIVITY_DATE = "d.m.Y";
	public static String JS_USER_ACTIVITY_TIMESTAMP = "d.m.Y H:i:s .u";
	public static String JS_USER_ACTIVITY_TIME = JS_HOUR_MINUTE;

	public static String UTC_OFFSET_FULL = "'UTC'ZZ";

	public static String HOUR_MINUTE = "HH:mm";

	public static String HOUR_MINUTE_SECOND = "HH:mm:ss";

	public static String DAY_MONTH = "dd.MM";
	public static String DAY_MONTH_WITH_DOT = "dd.MM.";
	public static String LOGON_DAY_MONTH_YEAR = "dd.MM.yy";
	public static String LOGON_DAY_MONTH_YEAR4 = "dd.MM.yyyy";

	public static String LOGON_DAY_MONTH_HOUR_MINUTE = "dd.MM. " + HOUR_MINUTE;
	public static String LOGON_DAY_MONTH_YEAR_HOUR_MINUTE = LOGON_DAY_MONTH_YEAR + " " + HOUR_MINUTE;
	
	/**
	 * Format for easy JS Date object creation
	 */
	public static String JS_YEAR_MONTH_DAY_HOUR_MINUTE = "yyyy,MM,dd,hh,mm";
	
	public static String JS_YEAR_MONTH_DAY_24HOUR_MINUTE = "yyyy,MM,dd,HH,mm";

	public static String LOGON_DAY_MONTH_YEAR4_HOUR_MINUTE = LOGON_DAY_MONTH_YEAR4 + " " + HOUR_MINUTE;
	public static String LOGON_DAY_MONTH_YEAR4_HOUR_MINUTE_SECOND = LOGON_DAY_MONTH_YEAR4 + " " + HOUR_MINUTE_SECOND;

	public static String DATETIME_NO_TZ = "yyyy-MM-dd'T'HH:mm:ss";
	public static String DATE_NO_TZ = "yyyy-MM-dd";

	public static String LOGON_DETAILED_DATE_TIME = "EEE MMM dd HH:mm:ss zzz yyyy";

	public static String USER_ACTIVITY = "dd.MM.yyyy HH:mm:ss.SSS";
	
	public static String DATETIME_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";

	public static String EXPORT_REPORT_TITLE_DATE = "EEEE, MMMM d, yyyy";
}