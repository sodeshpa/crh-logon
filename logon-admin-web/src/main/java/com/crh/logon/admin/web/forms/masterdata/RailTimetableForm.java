/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.masterdata;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_RailTimetableForm"
 */
public class RailTimetableForm  extends ActionForm {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailTimetableForm.class);
	
	private FormFile timetableImportFile;
	
	private String formAction;
	
	public RailTimetableForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "RailTimetableForm");
		}
		// ]]
		try {
			
			init();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			
			clear();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public void clear() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "clear");
		}
		// ]]
		try {
			
			timetableImportFile = null;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	public FormFile getTimetableImportFile() {
		return timetableImportFile;
	}

	public void setTimetableImportFile(FormFile timetableImportFile) {
		this.timetableImportFile = timetableImportFile;
	}

	/**
	 * @return the formAction
	 */
	public String getFormAction() {
		return formAction;
	}

	/**
	 * @param formAction the formAction to set
	 */
	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}
}
