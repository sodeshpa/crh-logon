/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.ferrari;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public final class StationeriesBean implements Serializable {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(StationeriesBean.class);

	private final boolean successful;
	private final String[] stationeries;

	public StationeriesBean() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "StationeriesBean");
		}
		// ]]
		try {
			this.successful = false;
			this.stationeries = null;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public StationeriesBean(boolean successful, String[] stationeries) {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "StationeriesBean");
		}
		// ]]
		try {
			this.successful = successful;
			this.stationeries = stationeries;
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
	public String[] getStationeries() {
		return this.stationeries;
	}

	/**
	 * @return
	 */
	public boolean isSuccessful() {
		return this.successful;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("successful", successful);
		builder.append("stationeries", Arrays.toString(stationeries));
		return builder.toString();
	}
}
