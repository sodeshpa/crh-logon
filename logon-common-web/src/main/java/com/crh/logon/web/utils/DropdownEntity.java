/*
 * Author            : AdNovum Informatik AG
 * Version Number    : \$Revision$
 * Date of last edit : \$Date$
 */

package com.crh.logon.web.utils;

import com.crh.logon.utils.StringUtils;



/**
 *
 * @author AdNovum
 */
public class DropdownEntity implements java.io.Serializable 
{
    private static final long serialVersionUID = 1L;

    // Fields
    final private String name;
    final private String value;

    /**
     * Create a new dropdown entry element
     * @param name The name of the element (to be shown in the dropdown list)
     * @param value The value of the element (used in the HTML "value" attribute)
     */
    public DropdownEntity(String name, String value) {
        this.name = name;
        this.value = value;
    }
    
    /**
     * Returns the name of the dropdown entry.
     * @return the name of the dropdown entry
     */
    final public String getName() {
        return name;
    }

    /**
     * Returns the value of the dropdown entry.
     * @return the value of the dropdown entry
     */
    final public String getValue() {
        return value;
    }
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DropdownEntity))
			return false;
		DropdownEntity other = (DropdownEntity) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		}
		else if (!value.equals(other.value))
			return false;
		return true;
	}
}
