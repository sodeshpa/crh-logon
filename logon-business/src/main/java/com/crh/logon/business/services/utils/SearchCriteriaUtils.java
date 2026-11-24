package com.crh.logon.business.services.utils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.utils.StringUtils;

import java.text.ParseException;
import java.util.Date;

/**
 * @author AdNovum Informatik AG
 */
public final class SearchCriteriaUtils {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SearchCriteriaUtils.class);

	private SearchCriteriaUtils() { }

	/**
	 * Expects that the given date/time filters give the pattern: "Day.Month.Year4 Hour:Minute"
	 */
	public static Date parseDateTimeCriteriaInDMYHMFormat(
			TimeZone tz,
			String filterDate,
			String filterTime,
			String defaultFilterTime) {
		// [[
		Date result = null;
		Object[][] params = {{"tz", tz},
							 {"filterDate", filterDate},
							 {"filterTime", filterTime},
							 {"defaultFilterTime", defaultFilterTime}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SearchCriteriaUtils.class, "parseDateTimeCriteriaInDMYHMFormat", params);
		}
		// ]]
		try {
			if (StringUtils.notEmpty(filterDate)) {
				try {
					result = tz.parseDayMonthYear4HourMinute(
							filterDate,
							StringUtils.empty(filterTime) ? defaultFilterTime : filterTime);

					LOG.debug("Parsed date/time filters: " + result, params);
				}
				catch (ParseException exc) {
					LOG.error("Could not parse date/time filters", params);
					// continue
				}
			}

			return result;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	/**
	 * Expects that the given date/time filters give the pattern: "Day.Month.Year4 Hour:Minute:Second"
	 */
	public static Date parseDateTimeCriteriaInDMYHMSFormat(
			TimeZone tz,
			String filterDate,
			String filterTime,
			String defaultFilterTime) {
		// [[
		Date result = null;
		Object[][] params = {{"tz", tz},
							 {"filterDate", filterDate},
							 {"filterTime", filterTime},
							 {"defaultFilterTime", defaultFilterTime}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SearchCriteriaUtils.class, "parseDateTimeCriteriaInDMYHMSFormat", params);
		}
		// ]]
		try {
			if (StringUtils.notEmpty(filterDate)) {
				try {
					result = tz.parseDayMonthYear4HourMinuteSecond(
							filterDate,
							StringUtils.empty(filterTime) ? defaultFilterTime : filterTime);

					LOG.debug("Parsed date/time filters: " + result, params);
				}
				catch (ParseException exc) {
					LOG.error("Could not parse date/time filters", params);
					// continue
				}
			}

			return result;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}
}
