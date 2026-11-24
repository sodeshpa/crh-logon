/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;

import java.util.Iterator;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.TablebrowserService;

/**
 * @author AdNovum Informatik AG
 */
public class BlobTypeBean extends TypeBean {

	private static final long serialVersionUID = 1L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(BlobTypeBean.class);

	private TableBean tableBean;

	private String blobColumnName;

	public BlobTypeBean(TableBean tableBean, String blobColumnName) {
		super();
		this.blobColumnName = blobColumnName;
		this.tableBean = tableBean;
	}

	/**
	 * Formats the value received from the native query into a string to
	 * be displayed in a result table
	 * @param value, as returned by the native query
	 * @return renderable string
	 */
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
	public String getJspRenderer() {
		return "BlobRenderer.jsp";
	}

	/**
	 * Returns the path/name of a jsp include file that contains
	 * a specific editor for the data type  
	 * @return
	 */
	public String getJspEditor() {
		return "BlobEditor.jsp";
	}

	/**
	 * returns a type-specific bean containing values used by the renderer
	 * @param value a value, as returned by the native query
	 * @return a type-specific bean
	 */
	public Object getTypedValue(Object value) {
		int count = 0;
		for (Iterator iterator = tableBean.getFields().iterator(); iterator.hasNext();) {
			FieldBean field = (FieldBean) iterator.next();

			if (field.getName().equalsIgnoreCase(blobColumnName + "_MIME_TYPE_CD")) {
				return (count + "");
			}

			count++;
		}
		return null;
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
			if ((value == null)) {
				return result = null;
			}
			return result = (byte[]) value;
			
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
