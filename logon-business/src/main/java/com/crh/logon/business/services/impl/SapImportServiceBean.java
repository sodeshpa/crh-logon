/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.localinterfaces.LocalServices;
import com.crh.logon.business.services.dispatchers.SapImportServiceDispatcher;
import com.crh.logon.business.services.impl.cron.ScheduledTask;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.ConfigServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.CronJobServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.CronstatusServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.FrozenTablesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.SapImportServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.views.RequireFreezingListEntity;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonException;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
@AdnTrace
public class SapImportServiceBean extends SapImportServiceDispatcher implements SapImportServiceLocalInterface {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SapImportServiceBean.class);
    private static final String IMPORT_FUNCTION = "main";
	@EJB private ConfigServiceLocalInterface configService;
	@EJB private CronstatusServiceLocalInterface cronstatusService;
	@EJB private FrozenTablesServiceLocalInterface frozenTablesService;
	
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
	
	@ScheduledTask(name = "sapimport_clean_log", second="45", minute="0", hour="*", configurationMethodName = "getSapimportCleanLogCronEnabled")
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void cleanUpLogs() {
			final String cleanProcName = "logon_log.log_cleanup";
			ConfigBean configuration = configService.getConfigBean();
			String owner = configuration.getWsserverLogonSchemaOwner();
			String cfgString = getConfig(configuration);
			String sql = "CALL " + owner + "." + cleanProcName + "(" + cfgString + ")";
			
			Connection connection = null;
			Statement createdStatement = null;
			try {
				connection = getConnection();
				createdStatement= connection.createStatement();
				createdStatement.execute(sql);
			}
			catch (SQLException e) {
				LOG.error("SQLException occurred during acquiring connection OR creating statement OR executing statement", e);
				throw new LogonException(e);
			}
			finally {
				if (createdStatement != null) {
					try {
						createdStatement.close();
						createdStatement = null;
					}
					catch (SQLException e) {
						LOG.error("Error closing createdStatement", e);
					}
				}
				if (connection != null) {
					try {
						connection.close();
						connection = null;
					} catch (SQLException sqle) {
						LOG.error("Error closing connection", sqle);
					}
				}
			}
	}
	
	public boolean shouldImportJobRun(String cronstatusName) {
		final String enabledProp = cronstatusName + ".cron_enabled";
		String enabledDesc = configService.getConfigBean().getProperty(enabledProp);
		boolean isEnabled = Boolean.parseBoolean(enabledDesc);
		
		return isEnabled && cronstatusService.isNewDataAvailable(cronstatusName);
	}
	
	@ScheduledTask(name = "total_sap_import", second = "5", minute = "1/12", hour = "*")	
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void doFullSapImport() {
		
		CronJobServiceLocalInterface cronJobService = null;
		try {
			cronJobService = (CronJobServiceLocalInterface) LocalServices.getLocalInterfaceFor("CronJobService", CronJobServiceLocalInterface.class.getName());
		} catch (NamingException e) {
			String errorMsg = "Error when retrieving CronJobService.";
			LOG.error(errorMsg);
			throw new LogonException(errorMsg, e);
		}
		cronJobService.callCronJob(Constant.CRONSTATUS_SAPIMPORT_MN);
		cronJobService.callCronJob(Constant.CRONSTATUS_SAPIMPORT_MNN);
		cronJobService.callCronJob(Constant.CRONSTATUS_SAPIMPORT_BPN);
		cronJobService.callCronJob(Constant.CRONSTATUS_SAPIMPORT_VN);
		cronJobService.callCronJob(Constant.CRONSTATUS_SAPIMPORT_SDN);
		cronJobService.callCronJob(Constant.CRONSTATUS_SAPIMPORT_VEHN);
	}
	
	@ScheduledTask(name = "sapimport_mn", schedule = false)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void doSapImportMN() {
		final Optional<Integer> queryTimeout = Optional.ofNullable(getConfigBean().getSapimportMnCronJobTimeout());
		importToLogonDb(Constant.CRONSTATUS_SAPIMPORT_MN, queryTimeout);
	}
	
	@ScheduledTask(name = "sapimport_mnn", schedule = false)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void doSapImportMNN() {
		final Optional<Integer> queryTimeout = Optional.ofNullable(getConfigBean().getSapimportMnnCronJobTimeout());
		importToLogonDb(Constant.CRONSTATUS_SAPIMPORT_MNN, queryTimeout);
	}

	@ScheduledTask(name = "sapimport_bpn", schedule = false)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void doSapImportBPN() {
		final Optional<Integer> queryTimeout = Optional.ofNullable(getConfigBean().getSapimportBpnCronJobTimeout());
		importToLogonDb(Constant.CRONSTATUS_SAPIMPORT_BPN, queryTimeout);
	}
	
	@ScheduledTask(name = "sapimport_vn", schedule = false)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void doSapImportVN() {
		final Optional<Integer> queryTimeout = Optional.ofNullable(getConfigBean().getSapimportVnCronJobTimeout());
		importToLogonDb(Constant.CRONSTATUS_SAPIMPORT_VN, queryTimeout);
	}
	
	@ScheduledTask(name = "sapimport_sdn", schedule = false)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void doSapImportSDN() {
		final Optional<Integer> queryTimeout = Optional.ofNullable(getConfigBean().getSapimportSdnCronJobTimeout());
		importToLogonDb(Constant.CRONSTATUS_SAPIMPORT_SDN, queryTimeout);
	}

	@ScheduledTask(name = "sapimport_vehn", schedule = false)	
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void doSapImportVEHN() {
		final Optional<Integer> queryTimeout = Optional.ofNullable(getConfigBean().getSapimportVehnCronJobTimeout());
		importToLogonDb(Constant.CRONSTATUS_SAPIMPORT_VEHN, queryTimeout);
	}
	
	public void _freezeOrder(RequireFreezingListEntity rfe) {
		OrderEntity order = getDaoProvider().getOrderEntityDao().findById(rfe.getOrderId());
		LOG.info("Freezing order after sap_netprice import: " + rfe.getOrderId());
		frozenTablesService.freezeOrder(order);
		em.flush();
	}
	
	/**
	 * @param cfgBean
	 * @return
	 */
	private String getConfig(ConfigBean configuration) {
		int dd = configuration.getSapimportLogDeleteDebugAfterDays();
		int di = configuration.getSapimportLogDeleteInfoAfterDays();
		int de = configuration.getSapimportLogDeleteErrorAfterDays();
		int delbizevent = configuration.getSapimportLogDeleteBizeventAfterDays();
		String level = configuration.getSapimportLogMinLoglevel();
		String result = "'" + level + "'" + ", " + dd + ", " + di + ", " + de + ", " + delbizevent;
		
		return result;
	}
	
	/**
	 * @param cronJobName
	 * Called from triggerCronJobs.
	 * @param queryTimeout
	 */
	private void importToLogonDb(String cronJobName, Optional<Integer> queryTimeout) {
		if (shouldImportJobRun(cronJobName)) {
			
			LOG.info(cronJobName + " has to run.");
			cronstatusService._startCronJob(cronJobName);
			LOG.info(cronJobName + " last_start_ts updated");
			
			ConfigBean configuration = configService.getConfigBean();
			String owner = configuration.getWsserverLogonSchemaOwner();
			String minLogLevel = configuration.getSapimportLogMinLoglevel();
			int abandonDays = configuration.getSapimportAbandonEntryDay();
			
			String importerFunction = cronJobName + "." + IMPORT_FUNCTION;
			
			String sql = "CALL " + owner + "." + importerFunction + "('" + minLogLevel + "', " + abandonDays + ")";
			
			Connection connection = null;
			Statement createdStatement = null;
			try {
				connection = getConnection();
				createdStatement = connection.createStatement();
				if (queryTimeout.isPresent()){
					createdStatement.setQueryTimeout(queryTimeout.get().intValue()*60);
				}
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
			cronstatusService._finishCronJob(cronJobName);
			LOG.info(cronJobName + " last_end_ts updated");
		}
	}

	@AdnTrace
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void doSapImport(String cronstatusName) {
		importToLogonDb(cronstatusName, Optional.empty());
	}
	
	@Deprecated
	private void sendMessageIntoPlanningMaterialMaintenanceQueue() {
		MessageProducer messageProducer = null;
		javax.jms.Connection queueConnection = null;
		Session session = null;
		try {
			InitialContext ctx = new InitialContext();
			ConnectionFactory connectionFactory = (ConnectionFactory) ctx.lookup("java:/ConnectionFactory");
			queueConnection = connectionFactory.createConnection();
			session = queueConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			Destination destination = (Destination) ctx.lookup("queue/PlanningMaterialMaintenanceQueue");
			messageProducer = session.createProducer(destination);
			TextMessage message = session.createTextMessage("NO");
			
			messageProducer.send(message);
		}
		catch (JMSException exception) {
			LOG.error("Cannot initiate the planninMaterialMaintenance!!!", exception);
			exception.printStackTrace();
		} 
		catch (NamingException e) {
			LOG.error("Queue/planninMaterialMaintenanceQueue or java:/ConnectionFactory not found!!!", e);
			e.printStackTrace();
		}
		finally {
			try {
				if (messageProducer != null) {				
					messageProducer.close();
				}
				if (session != null) {					
					session.close();
				}
				if (queueConnection != null) {				
					queueConnection.close();		
				}
			}
			catch (JMSException e) {
				String errorMessage = "Error closing message resources";
				LOG.error(errorMessage);
				throw new LogonException(errorMessage, e);
			}
		}
	}
}

