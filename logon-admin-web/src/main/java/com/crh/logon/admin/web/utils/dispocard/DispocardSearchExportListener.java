/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.utils.dispocard;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.crh.logon.business.services.utils.TimeZone;
import org.apache.struts.util.MessageResources;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.reporting.SqlDataExportListener;
import com.crh.logon.constants.Constant;
import com.crh.logon.utils.StringUtils;

/**
 * Used at the "Dispocard Search export" to load the language dependent column names and values.
 * @author AdNovum Informatik AG
 */
public class DispocardSearchExportListener implements SqlDataExportListener {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DispocardSearchExportListener.class);

	private MessageResources messageResources;
	
	/**
	 * Constructor 
	 * 
	 * @param messageResources
	 */
	public DispocardSearchExportListener(MessageResources messageResources) {
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
				String resourcesKey = DispocardSearchExportMetaData.columnNamesMap.get(columnNames[i]);
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
			for (int i = 0; i < columnNames.length; i++) {
				String columnName = columnNames[i];
				Object value = data.get(columnName);
				
				// handle "ENABLE_FLAG" column value
				if (DispocardSearchExportMetaData.QUERY_COLUMN_5.equals(columnName.toUpperCase())) {
					String stringVal = String.valueOf(value);
					
					if (StringUtils.empty(stringVal)) { // should never happen
						data.put(columnName, " ");	
						continue;
					}
					
					int intVal = Integer.valueOf(stringVal);
					if (Constant.ENABLED_FLAG_YES == intVal) {
						data.put(columnName, getMessageResources().getMessage(locale, "label.yes"));
					}
					else {
						data.put(columnName, getMessageResources().getMessage(locale, "label.no"));
					}
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
