/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.localinterfaces.ConfigServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DelivnoteCleanupServiceLocalInterface;
import com.crh.logon.business.services.dispatchers.DelivnoteCleanupServiceDispatcher;
import com.crh.logon.business.services.impl.cron.ScheduledTask;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonException;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class DelivnoteCleanupServiceBean extends DelivnoteCleanupServiceDispatcher implements DelivnoteCleanupServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DelivnoteCleanupServiceBean.class);
    
	@EJB private ConfigServiceLocalInterface configService;
	
	private Connection getConnection() {
		try {
			DataSource sapdataDS;
			sapdataDS = (DataSource)new InitialContext().lookup("java:jboss/datasources/sapdataDS");
			
			return sapdataDS.getConnection();
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
    
	@ScheduledTask(name = "delivnote_cleanup", second = "0", minute = "0", hour = "1-5", configurationMethodName = "getDelivnoteCleanupCronEnabled")	
	@TransactionAttribute(TransactionAttributeType.NEVER)
    public void cleanUpDelivnotes() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "cleanUpDelivnotes");
		}
		// ]]
		try {
			final String cleanProcName = "logon_log.delivnote_cleanup";
			ConfigBean cfgBean = configService.getConfigBean();
			String owner = cfgBean.getWsserverLogonSchemaOwner();
			String cfgString = getConfig(cfgBean);
			String sql = "CALL " + owner + "." + cleanProcName + "(" + cfgString + ")";
	
			if (logDebugEnabled) {
				LOG.debug("logon db schema owner: " + owner);
				LOG.debug("sql: " + sql);
			}
			
			Connection connection = null;
			Statement createdStatement = null;
			try {
				connection = getConnection();
				createdStatement = connection.createStatement();
				createdStatement.execute(sql);
				if (logDebugEnabled) {
					LOG.debug(DelivnoteCleanupServiceBean.class.getSimpleName()
							+ ".cleanUpDelivnotes has been successfully executed.");
				}				
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
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
    
	private String getConfig(ConfigBean cfgBean) {
		// [[
		String result = null;
		Object[][] params = {{"cfgBean", cfgBean}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getConfig", params);
		}
		// ]]
		try {
			String minLoglevel = cfgBean.getDelivnoteCleanupMinLoglevel();
			int deleteDelivnoteAfterDays = cfgBean.getDelivnoteCleanupDeleteDelivnoteAfterDays();
			String cfg = "'" + minLoglevel + "'" + ", " + deleteDelivnoteAfterDays;
			return result = cfg;
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
