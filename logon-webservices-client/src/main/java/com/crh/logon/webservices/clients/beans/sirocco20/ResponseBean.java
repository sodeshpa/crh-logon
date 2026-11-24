/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.sirocco20;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class ResponseBean implements Serializable {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ResponseBean.class);

	private final boolean successful;
	private final String appId;
	private final String guid;
	private final String errorMessage;

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
			appId = null;
			guid = null;
			errorMessage = null;
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
	public ResponseBean(boolean successful, String appId, String guid,  String errorMessage) {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "ResponseBean");
		}
		// ]]
		try {
			this.successful = successful;
			this.appId = appId;
			this.guid = guid;
			this.errorMessage = errorMessage;
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
	public String getAppId() {
		return this.appId;
	}
	
	/**
	 * @return
	 */
	public String getGuid() {
		return this.guid;
	}
	
	/**
	 * @return
	 */
	public String getErrorMessage() {
		return this.errorMessage;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("successful", successful);
		builder.append("appId", appId);
		builder.append("guid", guid);
		builder.append("errorMessage", errorMessage);
		return builder.toString();
	}
}
