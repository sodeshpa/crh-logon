/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.reporting;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import au.com.bytecode.opencsv.CSVWriter;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.SqlService;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.time.DateTimeFormat;

/**
 * @author AdNovum Informatik AG
 */
public class SqlDataExporter {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SqlDataExporter.class);

	private int batchSize = 1000;
	private int maxRows = -1;
	private List<SqlDataExportListener> exportListeners = new ArrayList<SqlDataExportListener>();
	private ServiceLocator serviceLocator;
	
	public SqlDataExporter(ServiceLocator serviceLocator) {
		
		this.serviceLocator = serviceLocator;
		this.addExportListener(new SqlDataExportListener() {
			public String[] exportHeader(String[] columnNames, Locale locale) {
				return columnNames;
			}

			public void exportRow(String[] columnNames, Object[] rowData, Map<String, Object> data, Locale locale, TimeZone timezone) {
				for (int i=0;i<columnNames.length;i++) {
					String columnName = columnNames[i];
					Object value = data.get(columnName);
					if (value instanceof Date) {
						data.put(columnName,
								 DateTimeFormat.asLogonDayMonthYear(
										 (Date) value,
										 locale,
										 timezone.getId()));
					}
				}
			}
		});
	}
	
	public void addExportListener(SqlDataExportListener listener) {
		exportListeners.add(listener);
	}
	
	public void removeExportListener(SqlDataExportListener listener) {
		exportListeners.remove(listener);
	}
	
	public void setExportListener(SqlDataExportListener listener) {
		exportListeners.clear();
		exportListeners.add(listener);
	}

	public Integer fetchRowCount(String query, Object[] qParameters, Locale locale, TimeZone timezone) {
		SqlService sqlService = null;
		try {
			sqlService = serviceLocator.fetchSqlService(); // always get a new one
			Long qSessionId = sqlService.openQuerySession(query, qParameters);
			List result = sqlService.fetchRows(qSessionId, 0);
			Object[] row = (result == null || result.size() == 0) ? new Object[]{BigDecimal.ZERO} : (Object[])result.get(0);  
			BigDecimal rowCount = (row.length == 0 ? BigDecimal.ZERO : (BigDecimal)row[0]);
			return rowCount.intValue();
		}
		catch (Exception exception) {
			String errMsg = getFatalErrorMessage(locale, exception);
			throw new LogonException(errMsg, exception);
		}
		finally {
			try {
				if (sqlService != null) {
					sqlService.remove();
				}
			}
			catch (Exception exception) {
				LOG.info("Non-critical: " + exception);
			}
		}
	}
	
	public void exportSqlData(Writer writer, String query, Object[] qParameters, Locale locale, TimeZone timezone) {
		exportSqlData(writer, query, qParameters, locale, CSVWriter.DEFAULT_QUOTE_CHARACTER, true, timezone);
	}
	
	public void exportSqlData(Writer writer,
							  String query,
							  Object[] qParameters,
							  Locale locale,
							  char quoteChar,
							  boolean addColumnNames,
							  TimeZone timezone) {

		CSVWriter csvWriter =  new CSVWriter(writer, ';', quoteChar);
		SqlService sqlService = null;
		try {
			sqlService = serviceLocator.fetchSqlService(); // always get a new one
			Long qSessionId = sqlService.openQuerySession(query, qParameters);
			String[] sqlColumnNames = sqlService.fetchColumnNames(qSessionId);
			String[] columnNames = sqlColumnNames;

			// header line
			for (int i = exportListeners.size() - 1; i >= 0; i--) {
				SqlDataExportListener listener = exportListeners.get(i);
				columnNames = listener.exportHeader(columnNames, locale);
			}
			Map<String, String> columnMapping = null;
			if (columnNames != null) {
				if (columnNames.length != sqlColumnNames.length) {

					throw new LogonException("Column definitions are of different lengths",
							Reason.RES_ERROR_IMPL);
				}
				columnMapping = new HashMap<String, String>();
				List<String> valueList = new ArrayList<String>();
				for (int i = 0; i < sqlColumnNames.length; i++) {
					columnMapping.put(sqlColumnNames[i], columnNames[i]);
					if (columnNames[i] != null) {
						valueList.add(columnNames[i]);
					}
				}
				if (addColumnNames) {
					csvWriter.writeNext(valueList.toArray(new String[0]));
				}
			}

			// data
			int rowsLeft = maxRows;
			List<Object> rows = null;
			do {
				int limit = ((rowsLeft > 0) && (rowsLeft < batchSize)) ? rowsLeft : batchSize;
				rows = sqlService.fetchRows(qSessionId, limit);
				if (rows == null) {
					continue;
				}
				for (Object object : rows) {
					Object[] row = (Object[]) object;
					// Prepare data by calling listeners
					if (row.length != sqlColumnNames.length) {

						throw new LogonException(
								"Error mapping column data, number of data elements != number of columns",
								Reason.RES_ERROR_IMPL);
					}
					Map<String, Object> data = new HashMap<String, Object>();
					for (int i = 0; i < sqlColumnNames.length; i++) {
						data.put(sqlColumnNames[i], row[i]);
					}
					for (int i = exportListeners.size() - 1; i >= 0; i--) {
						SqlDataExportListener listener = exportListeners.get(i);
						listener.exportRow(sqlColumnNames, row, data, locale, timezone);
					}

					// Export data
					List<String> valueList = new ArrayList<String>();
					for (int i = 0; i < sqlColumnNames.length; i++) {
						if ((columnMapping == null)
								|| (columnMapping.get(sqlColumnNames[i]) != null)) {
							Object element = data.get(sqlColumnNames[i]);
							valueList.add(element != null ? element.toString() : null);
						}
					}
					csvWriter.writeNext(valueList.toArray(new String[0]));
				}
				rowsLeft = (rowsLeft > 0) ? rowsLeft - rows.size() : rowsLeft;
			} while ((rows != null) && (rowsLeft != 0));
		}
		catch (Throwable throwable) {
			String errMsg = getFatalErrorMessage(locale, throwable);			
			try {
				writer.append(errMsg);
			}
			catch (IOException e) {
				// ignore
			}
		}
		finally {
			try {
				csvWriter.flush();
				if (writer != null) {
					writer.flush();
				}
				if (sqlService != null) {
					sqlService.remove();
				}
			}
			catch (Exception exception) {
				LOG.info("Non-critical: " + exception);
			}
		}
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public int getMaxRows() {
		return maxRows;
	}

	public void setMaxRows(int maxRows) {
		this.maxRows = maxRows;
	}

	private String getFatalErrorMessage(Locale locale, Throwable throwable) {
		ExceptionUtil.errException(LOG, throwable, null);
		String errorDesc = Message.lookup(locale, "fatalerror.unknownerror");
		String errorId = null;
		if (throwable instanceof LogonException) {
			errorId = ((LogonException) throwable).getId();
		}
		if (errorId == null) {
			errorId = StringUtils.generateErrorId();
			LOG.error("The following errorId is generated for a non-LogonException: " + errorId);
		}
		return Message.lookup(locale, "fatalerror.errormessage", new Object[] { errorDesc, errorId });		
	}
	
}
