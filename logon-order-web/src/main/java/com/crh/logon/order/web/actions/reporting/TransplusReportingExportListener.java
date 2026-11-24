/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.reporting;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.utils.time.DateTimeFormat;
import com.crh.logon.utils.time.DateTimeZoneFactory;

/**
 * Converts specific date columns to specific timezones based on the Transplus requirements.
 * @author AdNovum Informatik AG
 */
public class TransplusReportingExportListener extends ReportingExportListener{

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(TransplusReportingExportListener.class);

	private String appDefaultTimezoneCd;
	private String userTimeZoneCd;
	private Map<String, DateTimeZone> timezoneCache = new HashMap<String, DateTimeZone>();
	
	public TransplusReportingExportListener(String appDefaultTimezoneCd, String userTimeZoneCd) {
		this.appDefaultTimezoneCd = appDefaultTimezoneCd;
		this.userTimeZoneCd = userTimeZoneCd;
	}

	@Override
	public void exportRow(String[] columnNames, Object[] rowData, Map data, Locale locale, TimeZone timezone) {
		// [[
		Object[][] params = {{"columnNames", columnNames},
							{"rowData", rowData},
							{"data", data},
							{"locale", locale},
							{"timezone", timezone}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "exportRow", params);
		}
		// ]]
		try {

			convertDateFieldToTargetTimeZone
				(data, locale, null, "O_DELIVERY_FROM_DAT", "O_DELIVERY_FROM_TIME", "O_DELIVERY_FROM_TIMEZONE");

			convertDateFieldToTargetTimeZone
				(data, locale, null, "O_DELIVERY_TO_DAT", "O_DELIVERY_TO_TIME", "O_DELIVERY_TO_TIMEZONE");

			convertDateFieldToTargetTimeZone
				(data, locale, null, "LOADING_PLANNED_DAT", "LOADING_PLANNED_TIME", "LOADING_PLANNED_TIMEZONE");

			convertDateFieldToTargetTimeZone
				(data, locale, null, "UNLOADING_PLANNED_DAT", "UNLOADING_PLANNED_TIME", "UNLOADING_PLANNED_TIMEZONE");

			convertDateFieldToTargetTimeZone
				(data, locale, userTimeZoneCd, "O_CTL_CREATION_DAT", "O_CTL_CREATION_TIME", "O_CTL_CREATION_TIMEZONE");

			formatTransportDurationColumn(data, "TRANSPORT_DURATION");

		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * Replaces the {@code Long} transportDurationMin value with a formatted string version.
	 * Example: 75 => 01:15:00 
	 * @param data - the map containing the values
	 * @param fieldName - column name
	 */
	public void formatTransportDurationColumn(Map data, String fieldName) {
		if (data.get(fieldName) == null) {
			return;
		}
		Long transportDurationMin = ((BigDecimal) data.get(fieldName)).longValue();
		long hours = transportDurationMin / 60;
		long min = transportDurationMin % 60;
		String transportDuration = String.format("%02d:%02d:00", hours, min);
		data.put(fieldName, transportDuration);
	}

	/**
	 * Converts the given datetime field of the map to a timezone. This timezone can be the targetTimezone
	 * if specified. If not the timezoneFieldName will be used to indentify the target timezone. If this however
	 * is null the appDefaultTimezone will be used.
	 * @param data - map containing the _DAT _TIME and _TIMEZONE entries
	 * @param locale - user locale
	 * @param targetTimezone - if specified this will be the target for the timezone conversion 
	 * @param dateFieldName - *_DAT field to be found in the data map 
	 * @param timeFieldName - *_TIME field to be found in the data map
	 * @param timezoneFieldName - *_TIMEZONE field to be found in the data map
	 */
	private void convertDateFieldToTargetTimeZone(Map data, Locale locale, String targetTimezoneCd, String dateFieldName, String timeFieldName, String timezoneFieldName) {
		// [[
		Object[][] params = {{"data", data},
							{"locale", locale},
							{"targetTimezoneCd", targetTimezoneCd},
							{"dateFieldName", dateFieldName},
							{"timeFieldName", timeFieldName},
							{"timezoneFieldName", timezoneFieldName}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "convertDateFieldToTargetTimeZone", params);
		}
		// ]]
		try {

			Timestamp originalDate = (Timestamp) data.get(dateFieldName); // 21.01.2013

			// Initialize to empty string in order to clean fields from the map if the conversion
			// cannot be done
			String shiftedDatePart = "";
			String shiftedTimePart = "";
			String shiftedTimezonePart = "";

			// Cannot do conversion
			if (originalDate != null) {

				String timezoneField = (String) data.get(timezoneFieldName);

				// If not specified the targetTimezone is based on the field 'timezoneFieldName'
				if (targetTimezoneCd == null) {
					if (timezoneField == null) {
						// If for example the shipto timezone is undefined we fallback to appDefault
						targetTimezoneCd = appDefaultTimezoneCd;
					} 
					else {
						targetTimezoneCd = timezoneField;
					}
				}

				DateTimeZone fromDateTimeZone = getTimeZone(appDefaultTimezoneCd);
				DateTimeZone targetTimeZone = getTimeZone(targetTimezoneCd);
				DateTime fromDate = new DateTime(originalDate, fromDateTimeZone);
				DateTime toDate = fromDate.withZone(targetTimeZone);

				String[] splitDateTimeAsStringParts = DateTimeFormat.splitDateTimeAsStringParts(toDate);

				shiftedDatePart = splitDateTimeAsStringParts[0];
				shiftedTimePart = splitDateTimeAsStringParts[1];
				shiftedTimezonePart = splitDateTimeAsStringParts[2];

			}

			data.put(dateFieldName, shiftedDatePart);
			data.put(timeFieldName, shiftedTimePart);
			data.put(timezoneFieldName, shiftedTimezonePart);

		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	public DateTimeZone getTimeZone(String timeZoneCd) {
		DateTimeZone timezone = timezoneCache.get(timeZoneCd);
		if (timezone == null) {
			timezone = DateTimeZoneFactory.forID(timeZoneCd);
			timezoneCache.put(timeZoneCd, timezone);
		}
		return timezone;
	}

}