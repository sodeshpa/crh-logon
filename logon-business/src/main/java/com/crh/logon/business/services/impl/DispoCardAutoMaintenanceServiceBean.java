package com.crh.logon.business.services.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.DispoCardAutoMaintenanceServiceDispatcher;
import com.crh.logon.business.services.impl.cron.ScheduledTask;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.DispoCardAutoMaintenanceServiceLocalInterface;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonException;


@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
@AdnTrace
public class DispoCardAutoMaintenanceServiceBean extends DispoCardAutoMaintenanceServiceDispatcher implements DispoCardAutoMaintenanceServiceLocalInterface {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DispoCardAutoMaintenanceServiceBean.class);
	private static final String MAIN_FUNCTION = "main";
	/**
	 * Short names for the sql files to be called
	 */
	private static final String AUTO_INIT_SHIPTO_SAREA_RESTR_SHORTNAME = "auto_init_shipto_sarea_restr";
	private static final String DELETE_INVALID_SHIPTO_SAREA_RESTR_SHORTNAME = "delete_invalid_restrictions";
		
	@ScheduledTask(name = "auto_initialize_shipto_sarea_restr", second="00", minute="*/30", hour="*", configurationMethodName = "getShiptoSareaRestrMaintenanceAutoInitializeShiptoSareaRestrCronEnabled")
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void autoMaintDispocards() {
		updateRestrictions();
	} 
	
	private void updateRestrictions() {		
		ConfigBean configuration = getConfigBean();
		String owner = configuration.getWsserverLogonSchemaOwner();
		String minLogLevel = configuration.getShiptoSareaRestrMaintenanceLogMinLoglevel();
		String sql = "CALL " + owner + "." + AUTO_INIT_SHIPTO_SAREA_RESTR_SHORTNAME + "." + MAIN_FUNCTION + "('" + minLogLevel +"')";
		executeSql(sql);	
	}
	
	@ScheduledTask(name = "delete_invalid_shipto_sarea_restr", second="45", minute="00", hour="03", configurationMethodName = "getShiptoSareaRestrMaintenanceDeleteInvalidShiptoSareaRestrCronEnabled")
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void deleteExpiredDispocards() {
		ConfigBean configBean = getConfigBean();
		deleteInvalidShiptoSareaRestrictions(configBean.getShiptoSareaRestrMaintenanceDeleteInvalidShiptoSareaRestrDeleteAfterExpiringDays());
	}
	
	private void deleteInvalidShiptoSareaRestrictions(int maxDaysOfExpire) {
		ConfigBean configuration = getConfigBean();
		String owner = configuration.getWsserverLogonSchemaOwner();
		String minLogLevel = configuration.getShiptoSareaRestrMaintenanceLogMinLoglevel();
		String sql = "CALL " + owner + "." + DELETE_INVALID_SHIPTO_SAREA_RESTR_SHORTNAME + "." + MAIN_FUNCTION + "('" + minLogLevel +"', " + maxDaysOfExpire + ")";
		executeSql(sql);
	}
	
	private Connection getConnection() {
		
		try {
			DataSource logonDS;
			logonDS = (DataSource)new InitialContext().lookup("java:jboss/datasources/logonDs");
			
			return logonDS.getConnection();
		}
		catch(NamingException e) {
			LOG.error("Exception occurred during data source lookup.", e);
			throw new LogonException(e);
		}
		catch (SQLException e) {
			LOG.error("Exception occurred during establishing the connection.", e);
			throw new LogonException(e);
		}
		catch (Exception e) {
			LOG.error("Unexpectd exception occurred.", e);
			throw new LogonException(e);
		}
	}
	
	private void executeSql(String sql) {
		Connection connection = null;
		Statement createdStatement = null;
		try {
			connection = getConnection();
			createdStatement = connection.createStatement();
			createdStatement.execute(sql);
			
		}
		catch (SQLException sqlException) {
			LOG.error("Failed to execute: '" + sql + "'");
			throw new LogonException(sqlException);
		}
		finally {
			if (createdStatement != null) {
				try {
					createdStatement.close();
					createdStatement = null;
				}
				catch (SQLException sqlException) {
					LOG.error("Error closing createdStatement", sqlException);
				}
			}
			if (connection != null) {
				try {
					connection.close();
					connection = null;
				}
				catch (SQLException sqlException) {
					LOG.error("Error closing connection", sqlException);
				}
			}
		}
	}

}
