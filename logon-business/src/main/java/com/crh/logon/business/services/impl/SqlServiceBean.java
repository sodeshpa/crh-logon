/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Remove;
import javax.ejb.Stateful;

import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.SqlServiceDispatcher;
import com.crh.logon.business.services.localinterfaces.SqlServiceLocalInterface;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 * 
 * This service provides methods to execute SELECT statements (and select 
 * statements only) which retrieve the results in chunks, without executing the
 * query more than once.
 * 
 * Make sure to call remove() to end the session.
 */
@Stateful
@AdnTrace
public class SqlServiceBean extends SqlServiceDispatcher 
	implements SqlServiceLocalInterface {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SqlServiceBean.class);

	private long lastQSessionId = 0;
	private class QSession {
		private Long qSessionId;
		private ResultSet resultSet;
				
		public QSession(Long qSessionId) {
			this.qSessionId = qSessionId;
		}
		
		public Long getQSessionId() {
			return qSessionId;
		}
		public ResultSet getResultSet() {
			return resultSet;
		}
		public void setResultSet(ResultSet resultSet) {
			this.resultSet = resultSet;
		}
	}

	private transient Map<Long, QSession> qSessions;
	private transient ConnectionProvider connectionProvider;
	private transient Connection connection;
	
	@SuppressWarnings("unused")
	@PostConstruct
	private void postConstruct() {
		qSessions = new HashMap<Long, QSession>();
		connectionProvider = null;
		connection = null;
	}
	
	private Connection getConnection() {
		if (connection == null) {
			try {
				Object delegate = em.getDelegate();
				Session delegateSession = (Session) delegate;
				SessionFactoryImplementor sessionFactory = (SessionFactoryImplementor) delegateSession.getSessionFactory();
				connectionProvider = sessionFactory.getConnectionProvider();
				connection = connectionProvider.getConnection();
				LOG.debug("new connection retrieved");
			}
			catch (SQLException e) {
				throw new LogonException(e);
			}
		}
		if (connection == null) {
			throw new LogonException("Unable to obtain a connection", Reason.RES_ERROR_IMPL);
		}

		return connection;
	}
	
	private PreparedStatement prepareStatement(String query) {
		try {
			return getConnection().prepareStatement(query);
		} catch (SQLException e) {
			throw new LogonException(e);
		}
	}
	
	
	private QSession getQSession(Long qSessionId) {
		if (!qSessions.containsKey(qSessionId)) {
			throw new LogonException("Invalid qSessionId " + qSessionId, Reason.RES_ERROR_IMPL);
		}
		return qSessions.get(qSessionId);
	}
	
	
	private QSession createQSession(String query, Object[] qParameters) {
		Long qSessionId = new Long(++lastQSessionId);
		QSession qSession = new QSession(qSessionId);
		PreparedStatement statement = prepareStatement(query);
		try {
			if (qParameters != null) {
				for (int i=0;i<qParameters.length;i++) {
					statement.setObject(i+1, qParameters[i]);
				}
			}
			qSession.setResultSet(statement.executeQuery());
		} catch (SQLException e) {
			
			throw new LogonException("Error executing query " + query + " (" + Arrays.toString(qParameters) + ")", e);
		}
		qSessions.put(qSessionId, qSession);
		
		return qSession;
	}
	
	
	/**
	 * Opens a query session to allow subsequent calls to fetchRows
	 * @param query
	 * @return
	 */
	public Long openQuerySession(String query, Object[] qParameters) {
		LOG.debug("qParameters: " + Arrays.toString(qParameters));
		QSession qSession = createQSession(query, qParameters);

		return qSession.getQSessionId();
	}
	
	
	public String[] fetchColumnNames(Long qSessionId) {
		List<String> columnNames = new ArrayList<String>();
		QSession qSession = getQSession(qSessionId);
		ResultSet resultSet = qSession.getResultSet();
		if (resultSet != null) {
			try {
				ResultSetMetaData metaData = resultSet.getMetaData();
				int columnCount = metaData.getColumnCount();
				for (int i=1;i<columnCount+1;i++) {
					columnNames.add(metaData.getColumnName(i));
				}
			} catch (SQLException e) {
				
				throw new LogonException(e);
			}
			return columnNames.toArray(new String[0]);
		}
		
		return null;
	}
	
	/**
	 * Fetch the next set of rows from the DB.
	 * @param qSessionId A QSessionId returned by openQuerySession
	 * @param maxRows The maximum number of rows to retrieve
	 * @return A list of Object[] representing the rows or null if no more rows are available.
	 * If null is returned, the session is automatically closed.
	 * 
	 * @adnlogger.notrace
	 */
	public List fetchRows(Long qSessionId, int maxRows) {
		List<Object[]> resultList = null;
		QSession qSession = getQSession(qSessionId);
		ResultSet resultSet = qSession.getResultSet();
		try {
			if (!resultSet.isAfterLast()) {
				ResultSetMetaData metaData = resultSet.getMetaData();
				int columnCount = metaData.getColumnCount();
				while (((resultList == null) || (resultList.size() < maxRows))
						&& (resultSet.next())) {
					Object[] resultRow = new Object[columnCount];
					for (int i = 0; i < columnCount; i++) {
						int columnIndex = i + 1;
						Object obj = null;
						switch (metaData.getColumnType(columnIndex)) {
						// http://www.oracle.com/technology/tech/java/sqlj_jdbc/htdocs/jdbc_faq.html#08_00
						case Types.TIMESTAMP:
							obj = resultSet.getTimestamp(columnIndex);
							break;
						default:
							obj = resultSet.getObject(columnIndex);
							break;
						}
						if (!resultSet.wasNull()) {
							resultRow[i] = obj;
						}
					}
					if (resultList == null) {
						resultList = new ArrayList<Object[]>();
					}
					resultList.add(resultRow);
				}
				LOG.debug("Returning " + (resultList != null ? resultList.size() : "no") + " rows");
			}
			else {
				resultSet.getStatement().close();
				qSessions.remove(qSessionId);
			}
		}
		catch (SQLException e) {
			LOG.error("SQLException", e);
			try {
				qSession.getResultSet().getStatement().close();
			}
			catch (SQLException f) {
				LOG.error("An error occurred closing statement for qSessionId " + qSessionId, f);
			}
			finally {
				qSessions.remove(qSessionId);
			}
			throw new LogonException(e);
		}
		return resultList;
	}
	
	
	/**
	 * Closes an open qSession.
	 * @param qSessionId
	 */
	public void closeQuerySession(Long qSessionId) {
		QSession qSession = getQSession(qSessionId);
		if (qSession != null) {
			try {
				qSession.getResultSet().getStatement().close();
			}
			catch (SQLException e) {
				LOG.error("An error occurred closing statement for qSessionId " + qSessionId, e);
			}
			qSessions.remove(qSessionId);
		}
	}
	
	
	/**
	 * Ends the EJB lifecycle for this session bean.
	 */
	@Remove
	public void remove() {
		Long[] openQSessionIds = new Long[qSessions.size()];
		int c = 0;
		for (QSession qSession : qSessions.values()) {
			openQSessionIds[c++] = qSession.getQSessionId();
		}

		for (Long qSessionId : openQSessionIds) {
			closeQuerySession(qSessionId);
		}

		// Somehow preDestroy is not automatically called by the container after this method is invoked (it is when an exception is thrown though).
		// So, resources are cleaned up here (as well).
		try {
			connectionProvider.closeConnection(connection);
			connection = null;
			LOG.debug("Connection closed");
		}
		catch (SQLException e) {
			LOG.error("An error occurred closing connection", e);
		}
	}
	
	@SuppressWarnings("unused")
	@PreDestroy
	private void preDestroy() {
		if ((connectionProvider != null) && (connection != null)) {
			try {
				connectionProvider.closeConnection(connection);
				connection = null;
				LOG.debug("Connection closed");
			}
			catch (SQLException e) {
				LOG.error("An error occurred closing connection", e);
			}
		}
	}
}
