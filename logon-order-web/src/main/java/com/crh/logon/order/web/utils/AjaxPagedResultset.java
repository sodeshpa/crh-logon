/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils;

import java.util.List;

import flexjson.JSON;

/**
 * @author AdNovum Informatik AG
 */
public class AjaxPagedResultset {

	List rows;
	long results;
	
	public AjaxPagedResultset(List rows, long results) {
		this.rows = rows;
		this.results = results;
	}

	@JSON
	public List getRows() {
		return rows;
	}

	public void setRows(List rows) {
		this.rows = rows;
	}

	public long getResults() {
		return results;
	}

	public void setResults(long results) {
		this.results = results;
	}
}
