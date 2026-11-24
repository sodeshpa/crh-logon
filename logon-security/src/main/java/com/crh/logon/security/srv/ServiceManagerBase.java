/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.security.srv;

import java.sql.Connection;
import java.sql.SQLException;

import javax.security.auth.login.LoginException;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class ServiceManagerBase {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ServiceManagerBase.class);
	
	protected final Connection connection;

	protected ServiceManagerBase(Connection conn) {
		// [[
		Object[][] params = {{"conn", conn}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "ServiceManagerBase", params);
		}
		// ]]
		try {
			this.connection = conn;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	protected static void logError(SQLException e, String where) {
		LOG.error(where + ": SQLException caught: SQLState='" + e.getSQLState() + "', ErrorCode='" + e.getErrorCode() + "', nextException='" + e.getNextException() + "'");
		LOG.error(where + ": SQLException caught: " + e, e);
	}
	
	protected long toLong(String value) throws LoginException {
		// [[
		long result = 0L;
		Object[][] params = {{"value", value}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "toLong", params);
		}
		// ]]
		try {
			return result = Long.parseLong(value);
		}
		catch (NumberFormatException e) {
			throw new LoginException("Wrong number format: '" +  value + "'!");
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
}
