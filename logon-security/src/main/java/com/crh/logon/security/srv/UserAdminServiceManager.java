/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.security.srv;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.security.auth.login.LoginException;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.persistence.dao.jdbc.UserActivityDAOJDBCImpl;
import com.crh.logon.persistence.dao.jdbc.UserActivityLogDAOJDBCImpl;
import com.crh.logon.persistence.dao.jdbc.UserDAOJDBCImpl;
import com.crh.logon.persistence.dao.to.ConfigLoginModuleValue;
import com.crh.logon.persistence.dao.to.PersonValue;
import com.crh.logon.persistence.dao.to.TextTxValue;
import com.crh.logon.persistence.dao.to.UserActivityActionBsValue;
import com.crh.logon.persistence.dao.to.UserActivityActionTxValue;
import com.crh.logon.persistence.dao.to.UserActivityLogValue;
import com.crh.logon.persistence.dao.to.UserActivityValue;
import com.crh.logon.persistence.dao.to.UserValue;
import com.crh.logon.persistence.dao.util.ColumnRestriction;
import com.crh.logon.persistence.dao.util.DAOException;
import com.crh.logon.persistence.dao.util.DAOExpression;
import com.crh.logon.security.utils.PasswordUtil;
import com.crh.logon.security.utils.PwGenerator;

/**
 * @author AdNovum Informatik AG
 */
public class UserAdminServiceManager extends ServiceManagerBase {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(UserAdminServiceManager.class);
	
	private static final Long EXT_LOGIN_SUCCESSFUL = 22L;
	private static final Long EXT_LOGIN_FAILED = 23L;
	private static final Long EXT_PWD_UNLOCK_SUCCESSFUL = 26L;
	
	private static final String CFG_APPPOLICYPASSWORDRECOVERYURL = "app.policy.password.recovery_url";
    private static final String CFG_APPSYSTEMNAME = "app.system.name";
    
    private String instance;
    private final RefDataServiceManager refDataSrv;
    private final PersonServiceManager personSrv;
    private final EmailServiceManager emailSrv;

	/**
     * Constructor. Visible only in the package.
     * NOTE: No public constructor for this manager
     * 
     * @param conn
     * 			A connection (session) with a specific database. (Cannot be null).
     */
    protected UserAdminServiceManager(Connection conn, String instance, RefDataServiceManager refDataSrv,
    		EmailServiceManager emailSrv, PersonServiceManager personSrv) {
    	super(conn);
		// [[
		Object[][] params = {{"conn", conn}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "UserAdminServiceManager", params);
		}
		// ]]
		try {
			this.refDataSrv = refDataSrv;
			this.emailSrv = emailSrv;
			this.personSrv = personSrv;
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
	 * Records (stores) an external login attempt.
	 * It may trigger the locking of the external user password according to the password policy.
	 * 
	 * @param extLogin 
	 * 				The external user login name
	 * @param isSuccessful 
	 * 				<code>true</code> if the login attempt was successful, 
	 * 				<code>false</code> otherwise
	 * 
	 * @throws IllegalArgumentException if the extLogin is null
	 */
	public void recordExtLogInAttempt(String extLogin, boolean isSuccessful) throws DAOException, LoginException {
		// [[
		Object[][] params = {{"extLogin", extLogin},
							{"isSuccessful", isSuccessful + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "recordExtLogInAttempt", params);
		}
		// ]]
		try {
			if (extLogin == null) {
				String msg = "'extLogin' is null";
				LOG.error(msg);
				throw new IllegalArgumentException(msg);
			}
			
			UserValue user = fetchUserByExtLogin(extLogin);
			if (user == null) {
				String errorMsg = "Couldn't load the User details for extLogin: '" + extLogin + "'!";
				LOG.error(errorMsg);
				throw new LoginException(errorMsg);
			}

			UserActivityActionBsValue userActivityAction = null;
			long userActivityActionCd;
			if (isSuccessful) {
				userActivityActionCd = EXT_LOGIN_SUCCESSFUL;
			}
			else {
				userActivityActionCd = EXT_LOGIN_FAILED;
			}
			
			userActivityAction = getRefDataSrv().findUserActivityActionBsValue(userActivityActionCd);
			if (userActivityAction == null) {
				String errorMsg = "Couldn't load the userActivityActionBs for userActivityActionCd: '" + userActivityActionCd + "' !";
				LOG.error(errorMsg);
				throw new LoginException(errorMsg);
			}
			
			UserActivityActionTxValue userActivityActionTx = null;
			String langCd = Locale.getDefault().getLanguage();
			userActivityActionTx = getRefDataSrv().findUserActivityActionTxValue(userActivityAction, langCd);
			if (userActivityActionTx == null) {
				String errorMsg = "Couldn't load the userActivityActionTx for userActivityActionCd: '" + userActivityActionCd + "' and langCd: '" + langCd + "!";
				LOG.error(errorMsg);
				throw new LoginException(errorMsg);
			}
			
			String description = userActivityActionTx.getTxt();
			
			persistUserActivity(user, userActivityAction, description);
			
			if (!isSuccessful) {
				lockExtUserPwdIfNecessary(user.getExtLogin());
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
	
	/**
	 * Check if the external user password is locked. (TLGNU_USER.EXT_LOGIN_PWD_LOCKED_TS != null).
	 * 
	 * @param extLogin
	 * 			The external login name to check.
	 * @return
	 * 			<code>true</code> when TLGNU_USER.EXT_LOGIN_PWD_LOCKED_TS != null, 
	 * 			<code>false</code> otherwise.
	 * 
	 * @throws DAOException
	 */
	public boolean isExtLoginPwdLocked(String extLogin) throws DAOException {
		// [[
		boolean result = false;
		Object[][] params = {{"extLogin", extLogin}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "isExtLoginPwdLocked", params);
		}
		// ]]
		try {
			result = true;
			UserValue user = fetchUserByExtLogin(extLogin);
			if (user != null) {
				if (user.getExtLoginPwdLockedTs() == null) {
					result = false;
				}
			}
			return result;
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
	 * Insert a new 'TLGNU_USER_ACTIVITY' record with the given action.
	 * 
	 * @param user
	 * 				Object of {@link UserValue}.
	 * @param userActivityAction
	 * 				Object of {@link UserActivityActionBsValue}.
	 * @param description
	 * 				Activity description.
	 * @param conn
	 * 				A connection (session) with a specific database. (Cannot be null).
	 * @return
	 * @throws DAOException
	 */
	private UserActivityValue persistUserActivity(UserValue user, 
			UserActivityActionBsValue userActivityAction, 
			String description) throws DAOException {
		// [[
		UserActivityValue result = null;
		Object[][] params = {{"user", user},
							{"userActivityAction", userActivityAction},
							{"description", description}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "persistUserActivity", params);
		}
		// ]]
		UserActivityValue userActivity = null;
		try {
			String ipAddress = LogonUtil.getClientIP();
			if (ipAddress != null && ipAddress.length() > UserActivityValue.DBSIZE_IP_ADDRESS) {
				LOG.error("ipAddress is too long: " + ipAddress + ", will be truncated!");
				ipAddress = org.apache.commons.lang.StringUtils.substring(ipAddress, 0, (int)UserActivityValue.DBSIZE_IP_ADDRESS);
			}
			
			if (description != null && description.length() > UserActivityValue.DBSIZE_DESCRIPTION) {
				LOG.error("description is too long: " + description + ", will be abbreviated!");
				description = org.apache.commons.lang.StringUtils.abbreviate(description, (int)UserActivityValue.DBSIZE_DESCRIPTION);
			}
			
			// prepare the UserActivityValue
			userActivity = new UserActivityValue();
			userActivity.setUserActivityActionCd(userActivityAction.getUserActivityActionCd());
			userActivity.setIpAddress(ipAddress);
			userActivity.setDescription(description);
			userActivity.setUserId(user.getUserId());
			
			UserActivityDAOJDBCImpl dao = new UserActivityDAOJDBCImpl(getConnection());
			userActivity = dao.insert(userActivity, user.getUserId());
	
			return result = userActivity;
		}
		catch (DAOException e) {
			LOG.error("Couldn't insert the UserActivityValue, userActivity:" + userActivity, e);
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
	 * @param extLogin
	 * @param conn
	 * @throws DAOException
	 * @throws LoginException
	 */
	private void lockExtUserPwdIfNecessary(String extLogin) throws DAOException, LoginException {
		// [[
		Object[][] params = {{"extLogin", extLogin}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "lockExtUserPwdIfNecessary", params);
		}
		// ]]
		try {
			if (extLogin == null) {
				String msg = "extLogin is null";
				LOG.error(msg);
				throw new IllegalArgumentException(msg);
			}
			
			UserValue user = fetchUserByExtLogin(extLogin);
			
			Date lock = user.getExtLoginPwdLockedTs();
			if (lock != null) {	
				LOG.warn("external user password is already locked, extLogin: " + user.getExtLogin());
				return;
			}
			else {
				ConfigLoginModuleValue configValue = getRefDataSrv().getConfigByKeyAndInstance(
						RefDataServiceManager.CFG_APPPOLICYPASSWORDMAXLOGINATTEMPT, getInstance());
				if (configValue == null) {
					String errorMsg = "Couldn't load the Config value for key: '" + RefDataServiceManager.CFG_APPPOLICYPASSWORDMAXLOGINATTEMPT +
							"' and instance: '" + getInstance() + "' !";
					LOG.error(errorMsg);
					throw new LoginException(errorMsg);
				}
				long maxLoginAttempt = toLong(configValue.getValue());

				// generate where SQL script to load the number of failed logins
				StringBuilder sqlScriptBuilder = new StringBuilder();
				sqlScriptBuilder.append(" WHERE ");
				sqlScriptBuilder.append(UserActivityLogValue.CN_NAME_USER_ID);
				sqlScriptBuilder.append(" = ? and ");
				sqlScriptBuilder.append(UserActivityLogValue.CN_NAME_ACTION_CD);
				sqlScriptBuilder.append(" = ? and timestamp > ( SELECT COALESCE(max(ual.timestamp), " +
						"(to_date('1970-01-01','YYYY-MM-DD'))) FROM VLGNU_USER_ACTIVITY_LOG ual WHERE ual.");
				sqlScriptBuilder.append(UserActivityLogValue.CN_NAME_USER_ID);
				sqlScriptBuilder.append(" = ? and ual.");
				sqlScriptBuilder.append(UserActivityLogValue.CN_NAME_ACTION_CD);
				sqlScriptBuilder.append(" in(?,?) ) ORDER BY ");
				sqlScriptBuilder.append(UserActivityLogValue.CN_NAME_TIMESTAMP);
				sqlScriptBuilder.append(" DESC");

				List valueList = new ArrayList();
				valueList.add(user.getUserId());
				valueList.add(new Long(EXT_LOGIN_FAILED));
				valueList.add(user.getUserId());
				valueList.add(new Long(EXT_LOGIN_SUCCESSFUL));
				valueList.add(new Long(EXT_PWD_UNLOCK_SUCCESSFUL));
				
				List failedLogins = null;
				UserActivityLogDAOJDBCImpl dao = new UserActivityLogDAOJDBCImpl(getConnection());
				try {
					failedLogins = dao.findRecords((short)0, (short)0, sqlScriptBuilder.toString(), valueList);
				}
				catch (DAOException e) {
					String errorMsg = "Couldn't load the the number of failed logins!";
					LOG.error(errorMsg, e);
					throw e;
				}

				if (failedLogins != null && failedLogins.size() >= maxLoginAttempt) {	// lock ext user pwd
					UserActivityActionBsValue userActivityExtPwdLock = getRefDataSrv().findUserActivityActionBsValue(28L);
					String description = getRefDataSrv().findUserActivityActionTxValue(userActivityExtPwdLock, Locale.getDefault().getLanguage()).getTxt();
					UserActivityValue uae = persistUserActivity(user, userActivityExtPwdLock, description);
					
					// millisec part reset due to conversion between different DB data types. TIMESTAMP(6) -> DATE (TLGNU_USER_ACTIVITY.CTL_CRE_TS -> TLGNU_USER.EXT_LOGIN_PWD_LOCKED_TS)
					Calendar cal = new GregorianCalendar();
					cal.setTime(uae.getCtlCreTs());
					
					generateNewPasswordAndSendUnlockPasswordMail(user.getExtLogin());
				}
			}
		}
		catch (DAOException e) {
			LOG.error("A DAOException cought:", e);
			throw e;
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
	 * @param extLogin
	 * @param conn
	 * @throws DAOException
	 * @throws LoginException
	 */
	private void generateNewPasswordAndSendUnlockPasswordMail(String extLogin)
			throws DAOException, LoginException {
		// [[
		Object[][] params = {{"extLogin", extLogin}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "generateNewPasswordAndSendUnlockPasswordMail", params);
		}
		// ]]
		try {
			LOG.info("UserAdminServiceManager - locking user and creating reset token");
			if (extLogin == null) {
				String msg = "extLogin is null";
				LOG.error(msg);
				throw new IllegalArgumentException(msg);
			}
			
			UserValue user = fetchUserByExtLogin(extLogin);
			if (user == null) {
				String msg = "external user not found, extLogin: " + extLogin;
				LOG.error(msg);
				throw new LoginException(msg);
			}
			
			PersonValue personValue = getPerson(user.getPersonId());
			if (personValue == null || personValue.getEmail() == null) {
				String msg = "no email address found, extLogin: " + extLogin;
				LOG.error(msg);
				throw new LoginException(msg);
			}
			String email = personValue.getEmail();

			LOG.debug("Creating a password reset token");
			final String resetToken = com.crh.logon.business.services.utils.PwGenerator.generatePasswordResetToken();

			// update user
			Date now = new Date();
			String newPassword = PwGenerator.generateNewExternalPassword(getRefDataSrv(), getInstance());
			user.setExtLoginPwd(PasswordUtil.createHash(newPassword));
			user.setExtLoginPwdValidToDat(now);
			user.setExtPwdResettokenHash(PasswordUtil.createHash(resetToken));
			user.setExtLoginPwdLockedTs(now);
			updateUserValue(user);
			
			String langCd = user.getApplicationLangCd();
			Locale locale = new Locale(user.getApplicationLangCd());
			
			SimpleDateFormat sdfDate = new SimpleDateFormat("d MMM yyyy", locale);
			SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm aaa", locale);
			
			StringBuilder link = new StringBuilder();
			link.append(getConfigValue(CFG_APPPOLICYPASSWORDRECOVERYURL));
			link.append("?extlogin=").append(URLEncoder.encode(extLogin, "UTF-8"));
			link.append("&lock=").append(
					URLEncoder.encode(resetToken, "UTF-8"));
			
			String appSystemName = getConfigValue(CFG_APPSYSTEMNAME);
			String announcementSubject = formatText("unlock_password.email.subject", langCd,
					new Object[] { appSystemName });
			String announcementText = formatText(
					"unlock_password.email.text",
					langCd,
					new Object[] { user.getExtLogin(), appSystemName, sdfDate.format(now),
							sdfTime.format(now), link.toString() });
			
			String passwordSubject = formatText("new_password2.email.subject", langCd, null);
			String passwordText = formatText("new_password2.email.text", langCd,
					new Object[] { newPassword });
			
			getEmailSrv().send(email, announcementSubject, announcementText);
			getEmailSrv().send(email, passwordSubject, passwordText);
		}
		catch (UnsupportedEncodingException e) {
			LOG.error("could not encode URL", e);
			throw new LoginException("could not encode URL");
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
	 * Load the {@link UserValue} by the given <code>extLogin</code> parameter.
	 * 
	 * @param extLogin
	 * 			The external login name 
	 * @param conn
	 * 			A connection (session) with a specific database. (Cannot be null).
	 * @return
	 * 			{@link UserValue}
	 * 
	 * @throws DAOException
	 * @throws IllegalArgumentException 
	 * 			When Connection (<code>conn</code> parameter) is null.
	 */
	@AdnTrace
	private UserValue fetchUserByExtLogin(String extLogin) throws DAOException {
		
		UserValue result = null;

		try {
			Map<String, ColumnRestriction> restriction = new HashMap<String, ColumnRestriction>();
			ColumnRestriction columnRestriction = new ColumnRestriction(extLogin, UserValue.CN_NAME_EXT_LOGIN, DAOExpression.EQUAL);
			restriction.put(UserValue.CN_NAME_EXT_LOGIN, columnRestriction);
			
			String whereClause = " WHERE UPPER(" + UserValue.CN_NAME_EXT_LOGIN + ") = UPPER(?) ";
						
			UserDAOJDBCImpl dao = new UserDAOJDBCImpl(getConnection());
			List usersList = dao.findRecords((short)0, (short)0, whereClause, Arrays.asList(extLogin.toUpperCase()));
			
			if (usersList == null || usersList.isEmpty()) {
				return result;
			}
			
			if (usersList.size() > 1) {
				String errorMsg = "More than one user found for extLogin: '" + extLogin + "'!";
				LOG.error(errorMsg);
				throw new DAOException(errorMsg);				
			}
			
			return result = (UserValue)usersList.get(0);
		}
		catch (DAOException e) {
			String errorMsg = "Couldn't load the User details for extLogin: '" + extLogin + "'!";
			LOG.error(errorMsg, e);
			throw e;
		}
	}
	
	private PersonValue getPerson(Long personId) throws DAOException {
		// [[
		PersonValue result = null;
		Object[][] params = {{"personId", personId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getPerson", params);
		}
		// ]]
		try {
			return result = getPersonSrv().getPersonValueByPersonId(personId);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private String getConfigValue(String key) throws DAOException, LoginException {
		// [[
		String result = null;
		Object[][] params = {{"key", key}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getConfigValue", params);
		}
		// ]]
		try {
			ConfigLoginModuleValue configVal = getRefDataSrv().getConfigByKeyAndInstance(key, getInstance());
			if (configVal == null) {
				String errorMsg = "Couldn't load config value found with key: '" + key + "' for the instance:'" + getInstance() + "'!";
				LOG.error(errorMsg);
				return result = null;
				//throw new LoginException(errorMsg);
			}
			return result = configVal.getValue();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private UserValue updateUserValue(UserValue user) throws DAOException {
		// [[
		UserValue result = null;
		Object[][] params = {{"user", user}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateUserValue", params);
		}
		// ]]
		try {
			UserDAOJDBCImpl dao = new UserDAOJDBCImpl(getConnection());
			return result = dao.update(user, user.getUserId());
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private String formatText(String textCd, String langCd,
			Object[] messageArguments) throws DAOException {
		// [[
		String result = null;
		Object[][] params = {{"textCd", textCd},
							{"langCd", langCd},
							{"messageArguments", messageArguments}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "formatText", params);
		}
		// ]]
		try {		
			TextTxValue text = getRefDataSrv().findTextTxValue(textCd,
					langCd);
			if (text == null) {
				LOG.error("Could not find text in TextEntity for textCd = "
						+ textCd + " and lang = " + langCd);
				return result = "";
			}
			if ((messageArguments != null) && (messageArguments.length > 0)) {
				MessageFormat formatter = new MessageFormat("");
				formatter.setLocale(new Locale(langCd));
				String pattern = text.getTxt();
				formatter.applyPattern(pattern);
			
				return result = formatter.format(messageArguments);
			} 
			else {			
				return result = text.getTxt();
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}	
    
    public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

	public RefDataServiceManager getRefDataSrv() {
		return refDataSrv;
	}

	public PersonServiceManager getPersonSrv() {
		return personSrv;
	}

	public EmailServiceManager getEmailSrv() {
		return emailSrv;
	}
}
