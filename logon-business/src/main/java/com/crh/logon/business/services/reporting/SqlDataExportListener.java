/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.reporting;

import com.crh.logon.business.services.utils.TimeZone;

import java.util.Locale;
import java.util.Map;

/**
 * @author AdNovum Informatik AG
 */
public interface SqlDataExportListener {

	public String[] exportHeader(String[] columnNames, Locale locale);

	public void exportRow(String[] columnNames, Object[] rowData, Map<String, Object> data,
			Locale locale, TimeZone timezone);
}
