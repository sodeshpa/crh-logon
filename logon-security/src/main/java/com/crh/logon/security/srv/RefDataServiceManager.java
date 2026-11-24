/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.security.srv;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.dao.jdbc.ConfigLoginModuleDAOJDBCImpl;
import com.crh.logon.persistence.dao.jdbc.TextTxDAOJDBCImpl;
import com.crh.logon.persistence.dao.jdbc.UserActivityActionBsDAOJDBCImpl;
import com.crh.logon.persistence.dao.jdbc.UserActivityActionTxDAOJDBCImpl;
import com.crh.logon.persistence.dao.to.ConfigLoginModuleValue;
import com.crh.logon.persistence.dao.to.ConfigValue;
import com.crh.logon.persistence.dao.to.TextTxValue;
import com.crh.logon.persistence.dao.to.UserActivityActionBsValue;
import com.crh.logon.persistence.dao.to.UserActivityActionTxValue;
import com.crh.logon.persistence.dao.util.ColumnRestriction;
import com.crh.logon.persistence.dao.util.DAOException;
import com.crh.logon.persistence.dao.util.DAOExpression;
import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
public class RefDataServiceManager extends ServiceManagerBase {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RefDataServiceManager.class);
   
	public static final String CFG_APPPOLICYPASSWORDMAXLOGINATTEMPT = "app.policy.password.max_login_attempt";
    
	private String instance;
    /**
     * Constructor. Visible only in the package.
     * NOTE: No public constructor for this manager
     * 
     * @param conn
     * 			A connection (session) with a specific database. (Cannot be null).
     */
    protected RefDataServiceManager(Connection conn, String instance) {
    	super(conn);
		// [[
		Object[][] params = {{"conn", conn}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "RefDataServiceManager", params);
		}
		// ]]
		try {
			this.instance = instance; 	
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
     * 
     * @param key
     * @param instance
     * @param conn
     * @return
     * @throws LoginException
     */
	public ConfigLoginModuleValue getConfigByKeyAndInstance(String key, String instance) throws DAOException {
		// [[
		ConfigLoginModuleValue result = null;
		Object[][] params = {{"key", key},
							{"instance", instance}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getConfigByKeyAndInstance", params);
		}
		// ]]
		try {
			if (StringUtils.empty(key)) {
				String errorMsg = "The key: '" + key + "' is null!";
				LOG.error(errorMsg);
				throw new IllegalArgumentException(errorMsg);
			}
			
			Map<String, ColumnRestriction> restriction = new HashMap<String, ColumnRestriction>();
			ColumnRestriction columnRestriction = new ColumnRestriction(key, ConfigValue.CN_NAME_KEY, DAOExpression.EQUAL);
			restriction.put(ConfigValue.CN_NAME_KEY, columnRestriction);
			if (instance == null) {
				columnRestriction = new ColumnRestriction("null", ConfigValue.CN_NAME_INSTANCE, DAOExpression.IS);
				restriction.put(ConfigValue.CN_NAME_INSTANCE, columnRestriction);
			}
			else {
				columnRestriction = new ColumnRestriction(instance, ConfigValue.CN_NAME_INSTANCE, DAOExpression.EQUAL);
				restriction.put(ConfigValue.CN_NAME_INSTANCE, columnRestriction);				
			}

			ConfigLoginModuleDAOJDBCImpl dao = new ConfigLoginModuleDAOJDBCImpl(getConnection());
			List<ConfigLoginModuleValue> list = dao.findRecords((short)0, (short)0, restriction);
			
			if (list == null || list.isEmpty()) {
				return result =  null;
			}
			
			if (list.size() > 1) {
				String errorMsg = "More than one config value found with key: '" + key + "' for the instance:'" + instance + "'!";
				LOG.error(errorMsg);
				throw new DAOException(errorMsg);				
			}
			
			return result = list.get(0);
		}
		catch (DAOException e) {
			String errorMsg = "Couldn't load config value found with key: '" + key + "' for the instance:'" + instance + "'!";
			LOG.error(errorMsg, e);
			throw e;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * 
	 * @param userActivityActionCd
	 * @param conn
	 * @return
	 * @throws DAOException
	 */
	public UserActivityActionBsValue findUserActivityActionBsValue(Long userActivityActionCd) throws DAOException {
		// [[
		UserActivityActionBsValue result = null;
		Object[][] params = {{"userActivityActionCd", userActivityActionCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(RefDataServiceManager.class, "findUserActivityActionBsValue", params);
		}
		// ]]
		try {
			Map<String, ColumnRestriction> restriction = new HashMap<String, ColumnRestriction>();
			ColumnRestriction columnRestriction = new ColumnRestriction(userActivityActionCd, UserActivityActionBsValue.CN_NAME_USER_ACTIVITY_ACTION_CD, DAOExpression.EQUAL);
			restriction.put(UserActivityActionBsValue.CN_NAME_USER_ACTIVITY_ACTION_CD, columnRestriction);
			columnRestriction = new ColumnRestriction(new Boolean(true), UserActivityActionBsValue.CN_NAME_CTL_ACT_CD, DAOExpression.EQUAL);
			restriction.put(UserActivityActionBsValue.CN_NAME_CTL_ACT_CD, columnRestriction);
			
			UserActivityActionBsDAOJDBCImpl dao = new UserActivityActionBsDAOJDBCImpl(getConnection());
			List list = dao.findRecords((short)0, (short)0, restriction);
			
			if (list == null || list.isEmpty()) {
				return result = null;
			}
			
			if (list.size() > 1) {
				String errorMsg = "More than one active userActivityActionBs found for userActivityActionCd: '" + userActivityActionCd + "'!";
				LOG.error(errorMsg);
				throw new DAOException(errorMsg);				
			}
			
			return result = (UserActivityActionBsValue)list.get(0);
		}
		catch (DAOException e) {
			String errorMsg = "Couldn't load the userActivityActionBs for userActivityActionCd: '" + userActivityActionCd + "' !";
			LOG.error(errorMsg, e);
			throw e;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * 
	 * @param value
	 * @param langCd
	 * @param conn
	 * @return
	 * @throws DAOException
	 */
	public UserActivityActionTxValue findUserActivityActionTxValue(UserActivityActionBsValue value, String langCd) throws DAOException {
		// [[
		UserActivityActionTxValue result = null;
		Object[][] params = {{"value", value},
							{"langCd", langCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findUserActivityActionTxValue", params);
		}
		// ]]
		try {
			if (value == null) {
				LOG.error("The User Activity Action bs type is null --> cannot load the text!");
				return result = null;
			}
			
			Map<String, ColumnRestriction> restriction = new HashMap<String, ColumnRestriction>();
			ColumnRestriction columnRestriction = new ColumnRestriction(value.getUserActivityActionCd(), UserActivityActionTxValue.CN_NAME_USER_ACTIVITY_ACTION_CD, DAOExpression.EQUAL);
			restriction.put(UserActivityActionTxValue.CN_NAME_USER_ACTIVITY_ACTION_CD, columnRestriction);
			columnRestriction = new ColumnRestriction(langCd, UserActivityActionTxValue.CN_NAME_LANG_CD, DAOExpression.EQUAL);
			restriction.put(UserActivityActionTxValue.CN_NAME_LANG_CD, columnRestriction);
			
			UserActivityActionTxDAOJDBCImpl dao = new UserActivityActionTxDAOJDBCImpl(getConnection());
			List list = dao.findRecords((short)0, (short)0, restriction);
			
			if (list == null || list.isEmpty()) {
				return result = null;
			}
			
			if (list.size() > 1) {
				String errorMsg = "More than one active userActivityActionTx found for userActivityActionCd: '" + value.getUserActivityActionCd() + "' and language langCd: '" + langCd + "'!";
				LOG.error(errorMsg);
				throw new DAOException(errorMsg);				
			}
			
			return result = (UserActivityActionTxValue)list.get(0);
		}
		catch (DAOException e) {
			String errorMsg = "Couldn't load the userActivityActionBs for userActivityActionCd: '" + value.getUserActivityActionCd() + "' and language langCd: '" + langCd + "'!";
			LOG.error(errorMsg, e);
			throw e;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * 
	 * @param textCd
	 * @param langCd
	 * @param conn
	 * @return
	 * @throws DAOException
	 */
	public TextTxValue findTextTxValue(String textCd, String langCd) throws DAOException {
		// [[
		TextTxValue result = null;
		Object[][] params = {{"textCd", textCd},
							{"langCd", langCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findTextTxValue", params);
		}
		// ]]
		try {
			if (textCd == null) {
				LOG.error("The textCd is null --> cannot load the text!");
				return result = null;
			}
			
			Map<String, ColumnRestriction> restriction = new HashMap<String, ColumnRestriction>();
			ColumnRestriction columnRestriction = new ColumnRestriction(textCd, TextTxValue.CN_NAME_TEXT_CD, DAOExpression.EQUAL);
			restriction.put(TextTxValue.CN_NAME_TEXT_CD, columnRestriction);
			columnRestriction = new ColumnRestriction(langCd, TextTxValue.CN_NAME_LANG_CD, DAOExpression.EQUAL);
			restriction.put(TextTxValue.CN_NAME_LANG_CD, columnRestriction);
			
			TextTxDAOJDBCImpl dao = new TextTxDAOJDBCImpl(getConnection());
			List<TextTxValue> list = dao.findRecords((short)0, (short)0, restriction);
			
			if (list == null || list.isEmpty()) {
				return result = null;
			}
			
			if (list.size() > 1) {
				String errorMsg = "More than one text found for textCd: '" + textCd + "' and language langCd: '" + langCd + "'!";
				LOG.error(errorMsg);
				throw new DAOException(errorMsg);				
			}
			
			return result = list.get(0);
		}
		catch (DAOException e) {
			String errorMsg = "Couldn't load the userActivityActionBs for userActivityActionCd: '" + textCd + "' and language langCd: '" + langCd + "'!";
			LOG.error(errorMsg, e);
			throw e;
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
