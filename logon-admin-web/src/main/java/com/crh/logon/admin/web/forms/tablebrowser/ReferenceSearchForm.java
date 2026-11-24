package com.crh.logon.admin.web.forms.tablebrowser;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;


/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_ReferenceSearchForm"
 */
public class ReferenceSearchForm extends TableViewForm {

	private static final long serialVersionUID = 1L;


	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ReferenceSearchForm.class);

	 
	private String refTable;
	private String setter;

	public String getRefTable() {
		return refTable;
	}

	public void setRefTable(String refTable) {
		this.refTable = refTable;
	}

	public String getSetter() {
		return setter;
	}

	public void setSetter(String setter) {
		this.setter = setter;
	}

	public boolean isDeleteAllowed() {
		// [[
		LOG.debug("isDeleteAllowed()");
		// ]]
		return false;
	}

	public boolean isInsertAllowed() {
		// [[
		LOG.debug("isInsertAllowed()");
		// ]]
		return false;
	}

	public boolean isUpdateAllowed() {
		// [[
		LOG.debug("isUpdateAllowed()");
		// ]]
		return false;
	}

	public void clear() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "clear");
		}
		// ]]
		try {
			super.clear();
			refTable = null;
			setter = null;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

}
