/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.reporting;

import java.util.Locale;
import java.util.Map;

import com.crh.logon.business.services.reporting.SqlDataExportListener;
import com.crh.logon.business.services.utils.TimeZone;

/**
 * Intented for general use when no special modification should be done for the
 * report data. 
 * @author AdNovum Informatik AG
 */
public class ReportingExportListener implements SqlDataExportListener{

	@Override
	public String[] exportHeader(String[] columnNames, Locale locale) {
		String[] filteredColumnNames = new String[columnNames.length];
		for (int i=0;i<columnNames.length;i++) {
			if (!columnNames[i].equals("O_SOLDTO_SAP_BP_ID")) {
				filteredColumnNames[i] = columnNames[i];
			}
		}
		
		return filteredColumnNames;
	}

	@Override
	public void exportRow(String[] columnNames, Object[] rowData, Map data, Locale locale,
			TimeZone timezone) {
		// no adjustments to data
	}

}
