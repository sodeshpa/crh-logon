/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.test;

import java.util.List;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.web.forms.AbstractActionForm;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_CronJobsForm"
 */
public class CronJobsForm extends AbstractActionForm {

	private static final long serialVersionUID = 1L;
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(CronJobsForm.class);

	private String selectedJob;
	private List<String> cronJobsList;
	
	
	public String getSelectedJob() {
		return selectedJob;
	}

	public void setSelectedJob(String selectedJob) {
		this.selectedJob = selectedJob;
	}

	public List<String> getCronJobsList() {
		return cronJobsList;
	}

	public void setCronJobsList(List<String> cronJobsList) {
		this.cronJobsList = cronJobsList;
	}

	/**
     * Default constructor.
     */
    public CronJobsForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "CronJobsForm");
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

    /**
     * Initializes the form. This has the effect of clearing all the input
     * fields on the web form.
     */
    private void init() {
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



	/**
     * Clears all the input fields on the web form, discards any existing
     * search results, and resets any existing pages. Called in response to
     * clicking the reset button on the web form.
     */
    public void clear() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "clear");
		}
		// ]]
		try {
			//cronJobsList = null;
			selectedJob = null;
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
