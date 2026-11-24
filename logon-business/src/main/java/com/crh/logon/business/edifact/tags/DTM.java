/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.tags;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.TimeZone;

import com.crh.logon.business.services.utils.EdifactException;

/**
 * @author AdNovum Informatik AG
 */
public class DTM extends Tag {
	
	private static final Map<String, DateFormat> DATE_FORMAT_LOOKUP_TABLE = new Hashtable<String, DateFormat>();
	
	private DataElementMeta functionCodeQualifier = new DataElementMeta(0, 0, true);
	private DataElementMeta value = new DataElementMeta(0, 1, false);
	private DataElementMeta formatCode = new DataElementMeta(0, 2, false);
	private DataElementMeta[] dataElementMetas = { functionCodeQualifier, value, formatCode };
	
	static {
		DateFormat df;
		df = new SimpleDateFormat("yyyyMMdd");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		DATE_FORMAT_LOOKUP_TABLE.put("102", df);
		df = new SimpleDateFormat("yyyyMMddHHmmss");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		DATE_FORMAT_LOOKUP_TABLE.put("204", df);
		df = new SimpleDateFormat("yyyyMMddHHmm");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		DATE_FORMAT_LOOKUP_TABLE.put("203", df);
	}
	
	public DTM() {
		
		super("DTM", "Date/time/period");
	}

	private DateFormat getDateFormat(String dateTimeFormatCode) {
		
		DateFormat df = DATE_FORMAT_LOOKUP_TABLE.get(dateTimeFormatCode);
		if (df == null) {
			
			throw new EdifactException("Unknown date format " + dateTimeFormatCode);
		}
		
		return df;
	}		

	public String getFunctionCodeQualifier() {
		return functionCodeQualifier.getValue();
	}

	public void setFunctionCodeQualifier(String functionCodeQualifier) {
		this.functionCodeQualifier.setValue(functionCodeQualifier);
	}

	public String getValue() {
		return value.getValue();
	}

	public void setValue(String value) {
		this.value.setValue(value);
	}
	
	public Date getValueAsDate() {
		
		Date date = null;
		if (value.getValue() != null) {
			DateFormat df = getDateFormat(formatCode.getValue());
			synchronized (df) {
				try {
					date = df.parse(value.getValue());
				} catch (ParseException e) {
					throw new EdifactException(e);
				}
			}
			if (date == null) {
				
				throw new EdifactException("Error parsing date " + value + " with format " + formatCode);
			}
		}
		
		return date;
	}
	
	public void setValue(Date date) {
		
		if (formatCode == null) {
			
			throw new EdifactException("Cannot set date value, please set the format code first");
		}
		DateFormat df = getDateFormat(formatCode.getValue());
		synchronized (df) {
			value.setValue(df.format(date));
		}
	}

	public String getFormatCode() {
		return formatCode.getValue();
	}

	public void setFormatCode(String formatCode) {
		this.formatCode.setValue(formatCode);
	}

	@Override
	protected DataElementMeta[] getDataElementMetas() {
		return dataElementMetas;
	}
}
