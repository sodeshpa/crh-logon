/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;

import java.util.Iterator;

/**
 * Wrops a list iterator from a native query, returning 
 * each row als a list of Objects wrapped in ValeBeans
 * (-> TableBeanUtils.wrapResultRow)
 * 
 * @author AdNovum Informatik AG
 */
public class RawRowsIterator implements Iterator {
	
	Iterator iterator;
	TableBean table;
	
	public RawRowsIterator(Iterator iterator, TableBean table){
		this.iterator = iterator;
		this.table = table;
	}

	public boolean hasNext() {
		return iterator.hasNext();
	}

	public Object next() {
		return TableBeanUtils.wrapResultRow((Object[])iterator.next(), table);
	}

	public void remove() {
		iterator.remove();
	}

}
