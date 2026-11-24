/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.utils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class CheckboxDropdownEntity extends DropdownEntity {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(CheckboxDropdownEntity.class);

    /**
     * Use the 'checked' property for the Checkbox dropdown
     */
    private boolean checked;

    public CheckboxDropdownEntity(String name, String value) 
    {
    	super(name, value);
    	checked = false;
    }
    
    public CheckboxDropdownEntity(String name, String value, boolean checked) 
    {
    	super(name, value);
    	this.checked = checked;
    }

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}
    
	/**
	 * return the hasCode for this object
	 */
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (null == this.getName() ? 0 : this.getName().hashCode());
		hash = 31 * hash + (null == this.getValue() ? 0 : this.getValue().hashCode());
		hash = 31 * hash + (checked ? 1231 : 1237);

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
			if (obj instanceof CheckboxDropdownEntity) {
				boolean equals = true;
				CheckboxDropdownEntity entity = (CheckboxDropdownEntity) obj;
				equals &= super.equals(obj);
				equals &= entity.getChecked().equals(this.getChecked());
				
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
