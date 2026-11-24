/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.ferrari;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public final class TextMessageBean implements Serializable {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(TextMessageBean.class);

	private final String number;
	private final String message;

	/**
	 * Default constructor.
	 * 
	 * @param number
	 * @param message
	 */
	public TextMessageBean(String number, String message) {
		// [[
		Object[][] params = {{"number", number},
							{"message", message}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "TextMessageBean", params);
		}
		// ]]
		try {
			this.number = number;
			this.message = message;
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
	String getMessage() {
		return this.message;
	}

	/**
	 * @return
	 */
	String getNumber() {
		return this.number;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("number", number);
		builder.append("message", message);
		return builder.toString();
	}

}
