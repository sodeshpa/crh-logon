/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.struts.action.ActionMessage;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;


/**
 * @author AdNovum Informatik AG
 */
public class TimestampTypeBean extends TypeBean {

	private static final long serialVersionUID = 1L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(TimestampTypeBean.class);

//	private Locale locale;

//	private DateFormat formatter;
	
	private SimpleDateFormat sFormatter;
	
	public TimestampTypeBean(Locale locale) {
		init(locale);
	}
	
	public TimestampTypeBean() {
		init(Locale.ENGLISH);
	}
	
	private void init (Locale locale) {
		// [[
		Object[][] params = {{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init", params);
		}
		// ]]
		try {
			
			sFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS", locale);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	@Override
	public String webFormat(Object value) {
		// [[
		String result = null;
		Object[][] params = {{"value", value}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "webFormat", params);
		}
		// ]]
		try {
			if (value == null || "".equals(value)) {
				return result = "";
			}
			
			return result = sFormatter.format((Timestamp)value);
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	@Override
	public Object webToDbObjet(Object value) throws ValueConversionException {
		// [[
		Object result = null;
		Object[][] params = {{"value", value}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "webToDbObjet", params);
		}
		// ]]
		try {
			
			if ((value == null) || (((String)value).length() == 0)) {
				return result = "";
			}
			Date date = sFormatter.parse((String)value);
			Timestamp ts = new Timestamp(date.getTime());
			return result = ts;
		}
		catch (ParseException e) {
			ActionMessage am = new ActionMessage("error.field.validation.date", value);
			ValueConversionException vce = new ValueConversionException(am, e);
			throw vce;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	@Override
	public String getJspEditor() {
		return "TimestampEditor.jsp";
	}
}
