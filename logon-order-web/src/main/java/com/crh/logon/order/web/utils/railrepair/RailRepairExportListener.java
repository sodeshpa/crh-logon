/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils.railrepair;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.crh.logon.business.services.utils.TimeZone;
import org.apache.struts.util.MessageResources;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.reporting.SqlDataExportListener;

/**
 * Used at the "Wagon Maintenance export" to load the language dependent column names and values.
 * @author AdNovum Informatik AG
 */
public class RailRepairExportListener implements SqlDataExportListener {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailRepairExportListener.class);

	private MessageResources messageResources;
	
	/**
	 * Constructor 
	 * 
	 * @param messageResources
	 */
	public RailRepairExportListener(MessageResources messageResources) {
		// [[
		Object[][] params = {{"messageResources", messageResources}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "DispocardSearchExportListener", params);
		}
		// ]]
		try {
			this.messageResources = messageResources;
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
	 * From the DB object field names, load the header column names based on the users {@link Locale}.
	 * 
	 * @param columnNames
	 * 			The DB field names.
	 * @param locale
	 * 			The users language.
	 */
	public String[] exportHeader(String[] columnNames, Locale locale) {
		// [[
		String[] result = null;
		Object[][] params = {{"columnNames", columnNames},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "exportHeader", params);
		}
		// ]]
		try {
			List<String> resultCols = new ArrayList<String>();
			for (int i = 0; i < columnNames.length; ++i) {
				String resourcesKey = RailRepairExportMetaData.columnNamesMap.get(columnNames[i]);
				resultCols.add(getMessageResources().getMessage(locale, resourcesKey));
			}
			
			return result = (String[])resultCols.toArray(new String[0]);
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
	 * 
	 */
	public void exportRow(String[] columnNames, Object[] rowData, Map data, Locale locale, TimeZone timezone) {
		// [[
		Object[][] params = {{"columnNames", columnNames},
							{"rowData", rowData},
							{"data", data},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "exportRow", params);
		}
		// ]]
		try {
			String wagonNoRegex = "(\\d{4})(\\d{4})(\\d{3})(\\d{1})";
			Pattern wagonNoPattern =  Pattern.compile(wagonNoRegex);

			for (int i = 0; i < columnNames.length; i++) {
				String columnName = columnNames[i];
				Object value = data.get(columnName);
				if (value == null) {
					continue; 
				}
				
				if (RailRepairExportMetaData.QUERY_COLUMN_1.equals(columnName.toUpperCase())) {
					Matcher matcher = wagonNoPattern.matcher((String)value);
					if(matcher.matches()) {
						StringBuilder builder = new StringBuilder();
						builder.append(matcher.group(1));
						builder.append(" ");
						builder.append(matcher.group(2));
						builder.append(" ");
						builder.append(matcher.group(3));
						builder.append("-");
						builder.append(matcher.group(4));
						data.put(columnName, builder.toString());
					}
				}
				
				if (RailRepairExportMetaData.QUERY_COLUMN_5.equals(columnName.toUpperCase())) {
					data.put(columnName, timezone.fmtAsDayMonthYearHourMinute((Date) value));
				}
				
				if (RailRepairExportMetaData.QUERY_COLUMN_6.equals(columnName.toUpperCase())) {
					data.put(columnName, timezone.fmtAsDayMonthYearHourMinute((Date) value));
				}
				
				if (RailRepairExportMetaData.QUERY_COLUMN_7.equals(columnName.toUpperCase())) {
					double dbVal = ((BigDecimal)value).doubleValue();
					double days = Math.floor(dbVal / (24 * 60));
					double hours = Math.floor((dbVal - (days * 60 * 24)) / 60);
					double minutes = dbVal - (days * 60 * 24) - (hours * 60);

					value = (int)days + "d " + (int)hours + "h " + (int)minutes + "m";
					data.put(columnName, value);
				}
				
				if (RailRepairExportMetaData.QUERY_COLUMN_11.equals(columnName.toUpperCase())) {
					data.put(columnName, getMessageResources().getMessage(locale, RailRepairExportMetaData.columnValKeyMap.get(value)));
				}
				
				if (RailRepairExportMetaData.QUERY_COLUMN_12.equals(columnName.toUpperCase())) {
					data.put(columnName, getMessageResources().getMessage(locale, RailRepairExportMetaData.columnValKeyMap.get(value)));
				}
				
				if (RailRepairExportMetaData.QUERY_COLUMN_13.equals(columnName.toUpperCase())) {
					data.put(columnName, getMessageResources().getMessage(locale, RailRepairExportMetaData.columnValKeyMap.get(value)));
				}
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public MessageResources getMessageResources() {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("getMessageResources(): '" + messageResources + "'");
		}
		// ]]
		return this.messageResources;
	}
}
