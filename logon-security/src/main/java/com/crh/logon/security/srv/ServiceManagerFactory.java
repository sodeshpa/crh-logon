/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.security.srv;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.login.LoginException;
import javax.sql.DataSource;

import com.crh.logon.security.utils.LogonProperties;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public final class ServiceManagerFactory {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ServiceManagerFactory.class);
	private static final String C_NAME = "ServiceManagerFactory";
	
	private final String jndiName;

	private Connection connection;
	
	private final EmailServiceManager emailServiceManager;
	private final PersonServiceManager personServiceManager;
	private final RefDataServiceManager refDataServiceManager;
	private final UserAdminServiceManager userServiceManager;

	/**
	 * @param jndiName
	 */
	public ServiceManagerFactory(String jndiName) throws LoginException {
		// [[
		Object[][] params = {{"jndiName", jndiName}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "ServiceManagerFactory", params);
		}
		// ]]
		try {
			this.jndiName = jndiName;
			this.connection = getConnection();
			
			// init service managers
			String instance = LogonProperties.getInstance();
			LOG.info("Instance: " + instance);
			this.refDataServiceManager = new RefDataServiceManager(connection, instance);
			this.emailServiceManager = new EmailServiceManager(connection, instance,
					this.refDataServiceManager);
			this.personServiceManager = new PersonServiceManager(connection);
			this.userServiceManager = new UserAdminServiceManager(connection, instance,
					this.refDataServiceManager, this.emailServiceManager, this.personServiceManager);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	public <T> T transaction(Callable<T> callable) throws LoginException {
		// [[
		T result = null;
		Object[][] params = {{"callable", callable}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "transaction", params);
		}
		// ]]
		try {
			result = callable.call();
			
			try {
				connection.commit();
			}
			catch (Exception e) {
				String errorMsg = "Couldn't save changes!";
				LOG.error(errorMsg);
				throw e;
			}
		}
		catch (Exception e) {
			LOG.error("Exception caught:", e);
			rollback("callable");
			cleanupConnection("callable");
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
		return result;
	}
	
	@Override
	protected void finalize() throws Throwable { 
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "finalize");
		}
		// ]]
		try {
			cleanupConnection("finalize");
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
	 * Cleanup {@link Connection} resources.
	 * 
	 * @param connection to be closed
	 * @param method
	 */
	public void cleanupConnection(String method)
			throws LoginException {
		// [[
		Object[][] params = {{"method", method}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "cleanup", params);
		}
		// ]]
		try {
			try {
				if (connection != null) {
					connection.close();
				}
			}
			catch (SQLException se) {
				logError(se, (C_NAME + "." + method + ": connection.close() failed"));
				throw new LoginException( C_NAME + "." + method + ": conn.close() failed");
			}
			connection = null;
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
	 *  Undoes all changes made in the current transaction and releases any database locks 
	 *  currently held by this Connection object.
	 * 
	 * @param conn
	 * @param method
	 */
	private void rollback(String method)
			throws LoginException {
		// [[
		Object[][] params = {{"method", method}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "rollback", params);
		}
		// ]]
		try {
			if (connection != null) {
				connection.rollback();
			}
		}
		catch (SQLException se) {
			logError(se, (C_NAME + "." + method + ": conn.rollback() failed"));
			throw new LoginException(C_NAME + "." + method + ": conn.rollback() failed");
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	private Connection getConnection() throws LoginException {
		Connection result = null;
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "cleanup");
		}
		// ]]
		try {          
            InitialContext ctx = new InitialContext();
            DataSource dataSource = null;
            
            dataSource = (DataSource)ctx.lookup(getJndiName());
            
            if (dataSource == null) {
				String info = "Unable to get connection for datasource '" + getJndiName() + "'";
				LOG.error(info);
				throw new LoginException(info);
			}
            else if (logDebugEnabled) {
                LOG.debug("datasource lookup successful");
            }
            result = dataSource.getConnection();
			result.setAutoCommit(false);
			return result;
            
    	}
    	catch (NamingException ne) {
			String info = "datasource lookup failed for datasource '" + getJndiName() + "'";
			LOG.error(info);
			throw new LoginException(info);
        }
    	catch (SQLException e) {
			logError(e, (C_NAME + ".getConnection"));
			throw new LoginException(C_NAME + "getConnection: dataSource.getConnection() failed");
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}
	
	public PersonServiceManager getPersonServiceManager() {
		return personServiceManager;
	}

	public RefDataServiceManager getRefDataServiceManager() {
		return refDataServiceManager;
	}

	public UserAdminServiceManager getUserServiceManager() {
		return userServiceManager;
	}

	public EmailServiceManager getEmailServiceManager() {
		return emailServiceManager;
	}
	
	public String getJndiName() {
		return jndiName;
	}
	
	private static void logError(SQLException e, String where) {
		LOG.error(where + ": SQLException caught: SQLState='" + e.getSQLState() + "', ErrorCode='" + e.getErrorCode() + "', nextException='" + e.getNextException() + "'");
		LOG.error(where + ": SQLException caught: " + e, e);
	}
}
