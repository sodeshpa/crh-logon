/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.utils;

import java.io.Serializable;

import org.apache.commons.lang.ObjectUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class BoxListEntity implements Serializable {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(BoxListEntity.class);

    private String name;
    private Object value;
    private Boolean canRemove;

    /** 
     * default constructor 
     */
    public BoxListEntity() { 
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "BoxListEntity");
		}
		// ]]
		try {
			; // avoid PMD	  
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }

    public BoxListEntity(Object value, String name) {
		this(value, name, null);
    }
    
    public BoxListEntity(Object value, String name, Boolean canRemove) {
		// [[
		Object[][] params = {{"value", value},
							{"name", name},
							{"canRemove", canRemove}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "BoxListEntity", params);
		}
		// ]]
		try {
			this.value = value;
			this.name = name;
			this.canRemove = canRemove;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

	public Boolean getCanRemove() {
		return canRemove;
	}

	public void setCanRemove(Boolean canRemove) {
		this.canRemove = canRemove;
	}

	/**
	 * return the hasCode for this object
	 */
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + ObjectUtils.hashCode(getValue());
		hash = 31 * hash + ObjectUtils.hashCode(getName());
		hash = 31 * hash + ObjectUtils.hashCode(getCanRemove());
		
		return hash;
	}
	
	/**
	 *  Returns <code>true</code> if and only if the argument is not 
     * <code>null</code> and all properties are identical.
     * 
     * NOTE: At String compare:
     * 	<code>null</code> is equals with "" and " " and ... (String.trim() is used)  
	 */
	public boolean equals(Object obj) {
		// [[
		boolean result = false;
		Object[][] params = {{"obj", obj}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "equals", params);
		}
		// ]]
		try {
			if (obj instanceof BoxListEntity) {
				boolean equals = true;
				BoxListEntity entity = (BoxListEntity) obj;
				equals &= ObjectUtils.equals(this.getValue(),entity.getValue());
				equals &= ObjectUtils.equals(this.getName(),entity.getName());
				equals &= ObjectUtils.equals(this.getCanRemove(),entity.getCanRemove());
				
				return result = equals;
			}
			return result = false;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
}
