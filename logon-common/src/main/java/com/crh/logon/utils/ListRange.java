package com.crh.logon.utils;

import java.io.Serializable;

/**
 * Represents a piece of result-data used by pagers
 */
public class ListRange<ElementType> implements Serializable {
	private static final long serialVersionUID = 1L;

	protected ElementType[] data;
	protected long totalSize;

	public ElementType[] getData() {
		return data;
	}

	public void setData(ElementType[] data) {
		this.data = data;
	}

	/**
	 * Returns the number of search results  
	 */
	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}
	
	public String toString() {
		return "totalSize: "+ totalSize + ", length: " + data.length; 
	}
}