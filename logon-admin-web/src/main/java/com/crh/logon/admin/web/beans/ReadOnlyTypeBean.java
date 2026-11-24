/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;


import java.io.Serializable;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.TablebrowserService;

/**
 * @author AdNovum Informatik AG
 */
public class ReadOnlyTypeBean extends TypeBean implements Serializable{


	private static final long serialVersionUID = 1L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ReadOnlyTypeBean.class);


	/**
	 * Formats the value recieved from the native query into a string to
	 * be displayed in a result table
	 * @param value, as returned by the native query
	 * @return renderable string
	 */
	public String webFormat(Object value){
		// [[
		String result = null;
		Object[][] params = {{"value", value}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "webFormat", params);
		}
		// ]]
		try {
			if (value == null){
				return result = null;
			}
			return result = value.toString();
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
	 * Returns the path/name of a jsp include file that contains
	 * a specific renderer for the data type  
	 * @return
	 */
	public String getJspRenderer(){
		return null;
	}

	/**
	 * Returns the path/name of a jsp include file that contains
	 * a specific editor for the data type  
	 * @return
	 */
	public String getJspEditor() {
		return "ReadOnlyEditor.jsp";
	}

	/**
	 * returns a type-specific bean containing values used by the renderer
	 * @param value a value, as returned by the native query
	 * @return a type-specific bean
	 */
	public Object getTypedValue(Object value) {
		return null;
	}

	/**
	 * Returns the Object representation as needed by the native query
	 * from the representation of the value (user input).
	 * This is basically the inverse transformation to webFormat
	 * @param value
	 * @return
	 */
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
			return result = value;
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
	 * Extends type information with Metainformation from DB
	 * @param tbs
	 */
	public void extendTypeInfo(TablebrowserService tbs) {
		// nothing to be done for default types
	}

}
