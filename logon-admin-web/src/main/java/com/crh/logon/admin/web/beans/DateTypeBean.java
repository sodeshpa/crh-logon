/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;

import java.text.DateFormat;
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
public class DateTypeBean extends TypeBean {

	private static final long serialVersionUID = 1L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DateTypeBean.class);

	private DateFormat formatter;
	
	public DateTypeBean(Locale locale) {
		init(locale);
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
			formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
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
			if (value == null) {
				return result = "";
			}
			
			return result = formatter.format((Date)value);
			
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
				return result = null;
			}
			return result = formatter.parse((String)value);
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
	
	public String getJspEditor() {
		return "DateEditor.jsp";
	}
}
