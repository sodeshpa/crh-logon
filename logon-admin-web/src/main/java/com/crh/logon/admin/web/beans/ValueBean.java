/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;


import java.io.Serializable;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class ValueBean implements Serializable{

	private static final long serialVersionUID = 1L;


	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ValueBean.class);

	
	Object value;
	TypeBean type;
	boolean editable;
	
	public ValueBean(Object value, TypeBean type) {
		super();
		// [[
		Object[][] params = {{"value", value},
							{"type", type}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "ValueBean", params);
		}
		// ]]
		try {
			this.value = value;
			this.type = type;
			editable = true;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public String getWebValue(){
		return type.webFormat(value);
	}
	
	public TypeBean getType(){
		return type;
	}
	
	public boolean isEditable() {
		// [[
		LOG.debug("isEditable()");
		// ]]
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/**
	 * Calls the type's getTypedValue method.
	 * @return
	 */
	public Object getTypedValue(){
		return type.getTypedValue(value);
	}

	public Object getValue() {
		return value;
	}

	public void setWebValue(Object value) throws Exception {
		this.value = type.webToDbObjet(value);
	}

}
