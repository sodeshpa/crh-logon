/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;

public class IndexedObject implements Serializable, Comparable<IndexedObject> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5498153478436736366L;
	
	private Integer index;	
	private Object object;
	
	public IndexedObject(Object object, Integer index) {
		this.object = object;
		this.index = index;
	}
	
	/**
	 * @return the index
	 */
	public Integer getIndex() {
		return index;
	}
	
	/**
	 * @param index the index to set
	 */
	public void setIndex(Integer index) {
		this.index = index;
	}
	
	/**
	 * @return the object
	 */
	public Object getObject() {
		return object;
	}
	
	/**
	 * @param object the object to set
	 */
	public void setObject(Object object) {
		this.object = object;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((index == null) ? 0 : index.hashCode());
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
		if (getClass() != obj.getClass())
			return false;
		IndexedObject other = (IndexedObject) obj;
		if (index == null) {
			if (other.index != null)
				return false;
		}
		else if (!index.equals(other.index))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("IndexedObject [index=");
		builder.append(index);
		builder.append(", object=");
		builder.append(object);
		builder.append("]");
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(IndexedObject o) {
		return (o == null || o.index == null) ? 1 : (index == null ? -1 : index.compareTo(o.index));
	}
	
}
