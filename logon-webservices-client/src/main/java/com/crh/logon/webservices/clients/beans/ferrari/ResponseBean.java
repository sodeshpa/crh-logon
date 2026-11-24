/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.ferrari;

import java.io.Serializable;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class ResponseBean implements Serializable {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ResponseBean.class);

	private final boolean successful;
	private final String jobId;
	private final String errorText;

	/**
	 * Default constructor.
	 */
	public ResponseBean() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "ResponseBean");
		}
		// ]]
		try {
			successful = false;
			jobId = null;
			errorText = null;
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
	 * Default constructor.
	 */
	public ResponseBean(boolean successful, String jobId, String errorText) {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "ResponseBean");
		}
		// ]]
		try {
			this.successful = successful;
			this.jobId = jobId;
			this.errorText = errorText;
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
	 * @return
	 */
	public boolean isSuccessful() {
		return this.successful;
	}
	
	/**
	 * @return
	 */
	public String getJobId() {
		return this.jobId;
	}
	
	/**
	 * @return
	 */
	public String getErrorText() {
		return this.errorText;
	}

}
