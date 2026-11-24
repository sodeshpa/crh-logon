/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Expression;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.UserActivityServiceDispatcher;
import com.crh.logon.business.services.utils.SearchCriteriaUtils;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UserActivityServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UserAdminServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UserServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.admin.UserActivityEntity;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.refdata.UserActivityActionBsEntity;
import com.crh.logon.persistence.entities.refdata.UserActivityActionTxEntity;
import com.crh.logon.persistence.entities.views.UserActivityLogEntity;
import com.crh.logon.persistence.entities.views.UtilLastLoginEntity;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class UserActivityServiceBean extends UserActivityServiceDispatcher implements UserActivityServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(UserActivityServiceBean.class);
	
	public static final Long INT_LOGIN_SUCCESSFUL	= 3L;
	public static final Long INT_LOGIN_FAILED		= 4L;
	public static final Long INT_LOGOUT			= 5L;
	public static final Long INT_LOGOUT_EXPIRED	= 6L;

	public static final Long EXT_LOGIN_SUCCESSFUL		= 22L;
	public static final Long EXT_LOGIN_FAILED			= 23L;
	public static final Long EXT_PWD_CHANGED			= 30L;
	public static final Long EXT_PWD_UNLOCK_SUCCESSFUL= 26L;
	public static final Long EXT_PWD_UNLOCK_FAILED	= 29L;
	public static final Long EXT_LOGOUT				= 24L;
	public static final Long EXT_LOGOUT_EXPIRED		= 25L;
	
	public static final Long TERMINAL_LOGIN_SUCCESSFUL= 42L;
	public static final Long TERMINAL_LOGIN_FAILED	= 43L;
	public static final Long TERMINAL_LOGOUT			= 44L;
	public static final Long TERMINAL_LOGOUT_EXPIRED	= 45L;
	
	public static final Long PDA_LOGIN_SUCCESSFUL	= 62L;
	public static final Long PDA_LOGIN_FAILED		= 63L;
	public static final Long PDA_LOGOUT			= 64L;
	public static final Long PDA_LOGOUT_EXPIRED	= 65L;
	
	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;
	@EJB private UserServiceLocalInterface userService;
	@EJB private UserAdminServiceLocalInterface userAdminService;


	public List<UserActivityActionTxEntity> fetchAllUserActivityActionTx() {
		// [[
		List<UserActivityActionTxEntity> result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAllUserActivityActionTx");
		}
		// ]]
		try {
			result = (List<UserActivityActionTxEntity>) referentialDataService.findTxEntityFiltered(UserActivityActionTxEntity.class, (Filter) null);
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
	
	public ListRange fetchUserActivity(int skip, int num, String sort, boolean asc,
									   HashMap searchCrit, TimeZone timezone) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"sort", sort},
							{"asc", asc + ""},
							{"searchCrit", searchCrit},
							{"timezone", timezone}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchUserActivity", params);
		}
		// ]]
		try {
			result = new ListRange();
			
			ReadOnlyDao<UserActivityLogEntity> dao = getDaoProvider().getUserActivityLogEntityDao();
			Filter<UserActivityLogEntity> filter = dao.createFilter();
			
			// filter from date/time
			Date filterFromDate = SearchCriteriaUtils.parseDateTimeCriteriaInDMYHMFormat(
					timezone,
					(String) searchCrit.get("filterFromDate"),
					(String) searchCrit.get("filterFromTime"),
					"00:00");
			if (filterFromDate != null) {
				filter.addExpression(Restrictions.ge(UserActivityLogEntity.ATTR_TIMESTAMP, filterFromDate));
			}

			// filter to date/time
			Date filterToDate = SearchCriteriaUtils.parseDateTimeCriteriaInDMYHMFormat(
					timezone,
					(String) searchCrit.get("filterToDate"),
					(String) searchCrit.get("filterToTime"),
					"23:59");
			if (filterToDate != null) {
				filter.addExpression(Restrictions.le(UserActivityLogEntity.ATTR_TIMESTAMP, filterToDate));
			}
			
			// filter userId
			String userIdStr = (String) searchCrit.get(UserActivityLogEntity.ATTR_USER_ID);
			if (!StringUtils.empty(userIdStr)) {
				try {
					Long userId = Long.valueOf(userIdStr);
					filter.addExpression(Restrictions.eq(UserActivityLogEntity.ATTR_USER_ID, userId));
				}
				catch (NumberFormatException e) {
					LOG.error("could not parse userIdStr: " + userIdStr);
				}
			}
			
			// filter userFirstName
			String userFirstName = (String) searchCrit.get(UserActivityLogEntity.ATTR_USER_FIRSTNAME);
			if (!StringUtils.empty(userFirstName)) {
				filter.addExpression(Restrictions.eq(UserActivityLogEntity.ATTR_USER_FIRSTNAME, userFirstName));
			}
			
			// filter userName
			String userName = (String) searchCrit.get(UserActivityLogEntity.ATTR_USER_NAME);
			if (!StringUtils.empty(userName)) {
				filter.addExpression(Restrictions.eq(UserActivityLogEntity.ATTR_USER_NAME, userName));
			}
			
			// filter userHolspace
			String userHolspace = (String) searchCrit.get(UserActivityLogEntity.ATTR_USER_HOLSPACE);
			if (!StringUtils.empty(userHolspace)) {
				filter.addExpression(Restrictions.eq(UserActivityLogEntity.ATTR_USER_HOLSPACE, userHolspace));
			}
			
			// filter userExtLogin
			String userExtLogin = (String) searchCrit.get(UserActivityLogEntity.ATTR_USER_EXTLOGIN);
			if (!StringUtils.empty(userExtLogin)) {
				filter.addExpression(Restrictions.eq(UserActivityLogEntity.ATTR_USER_EXTLOGIN, userExtLogin));
			}
			
			// filter userPdauser
			String userPdauserStr = (String) searchCrit.get(UserActivityLogEntity.ATTR_USER_PDAUSER);
			if (!StringUtils.empty(userPdauserStr)) {
				try {
					Long userPdauser = Long.valueOf(userPdauserStr);
					filter.addExpression(Restrictions.eq(UserActivityLogEntity.ATTR_USER_PDAUSER, userPdauser));
				}
				catch (NumberFormatException e) {
					LOG.error("could not parse userPdauserStr: " + userPdauserStr);
				}
			}
			
			// filter executorId
			String executorIdStr = (String) searchCrit.get(UserActivityLogEntity.ATTR_EXECUTOR_ID);
			if (!StringUtils.empty(executorIdStr)) {
				try {
					Long executorId = Long.valueOf(executorIdStr);
					filter.addExpression(Restrictions.eq(UserActivityLogEntity.ATTR_EXECUTOR_ID, executorId));
				}
				catch (NumberFormatException e) {
					LOG.error("could not parse executorIdStr: " + executorIdStr);
				}
			}
			
			// filter ipAddress
			String ipAddress = (String) searchCrit.get(UserActivityLogEntity.ATTR_IP_ADDRESS);
			if (!StringUtils.empty(ipAddress)) {
				filter.addExpression(Restrictions.eq(UserActivityLogEntity.ATTR_IP_ADDRESS, ipAddress));
			}
			
			// sort by
			if (!StringUtils.empty(sort)) {
				filter.addExpression(asc ? Order.asc(sort) : Order.desc(sort));
			}
			else {
				// order by timestamp
				filter.addExpression(Order.desc(UserActivityLogEntity.ATTR_TIMESTAMP));
			}
			
			List<UserActivityLogEntity> resultList = dao.findNRecs(skip, num, filter);
			
			result.setTotalSize(dao.count(filter));
			result.setData(resultList.toArray());
			
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
	
	public ListRange fetchLastLogin(int skip, int num, String sort, boolean asc, HashMap searchCrit, TimeZone timezone) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"sort", sort},
							{"asc", asc + ""},
							{"searchCrit", searchCrit},
							{"timezone", timezone}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchLastLogin", params);
		}
		// ]]
		try {
			result = new ListRange();
			
			ReadOnlyDao<UtilLastLoginEntity> dao = getDaoProvider().getUtilLastLoginEntityDao();
			Filter<UtilLastLoginEntity> filter = dao.createFilter();
			
			// filter by last login date
			Date lastLoginBeforeDate = SearchCriteriaUtils.parseDateTimeCriteriaInDMYHMFormat(
					timezone,
					(String) searchCrit.get("lastLoginBefore"),
					"00:00",
					"00:00");
			if (lastLoginBeforeDate != null) {
				Expression lowerThan = Restrictions.lt(UtilLastLoginEntity.ATTR_LAST_LOGIN, lastLoginBeforeDate);
				Expression isNull = Restrictions.isNull(UtilLastLoginEntity.ATTR_LAST_LOGIN);
				filter.addExpression(Restrictions.or(lowerThan, isNull));
			}
			
			// sort by
			if (!StringUtils.empty(sort)) {
				filter.addExpression(asc ? Order.asc(sort) : Order.desc(sort));
			}
			else {
				// order by userId
				filter.addExpression(Order.desc(UtilLastLoginEntity.ATTR_USER_ID));
			}
			
			List<UtilLastLoginEntity> resultList = dao.findNRecs(skip, num, filter);
			
			result.setTotalSize(dao.count(filter));
			result.setData(resultList.toArray());
			
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
	 * Fetch user activity log entries
	 * @param userId results are filtered only to the defined user, if null then not filtered by user
	 * @param maxNumOfRecords maximum size of the result list if it is bigger then 0, otherwise no limit for size (fetch all)
	 * @param isDescendingByTimestamp should be the result list ordered descending or ascending by their timestamp
	 * @return list of user activity log entries according to the filter criteria defined by the other input parameters, never null
	 */
	public List<UserActivityLogEntity> fetchUserActivityLog(final Long userId, final int maxNumOfRecords, final boolean isDescendingByTimestamp) {
		// [[
		List<UserActivityLogEntity> result = null;
		Object[][] params = {{"userId", userId},
							{"maxNumOfRecords", maxNumOfRecords + ""},
							{"isDescendingByTimestamp", isDescendingByTimestamp + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchUserActivityLog", params);
		}
		// ]]
		try {
			ReadOnlyDao<UserActivityLogEntity> dao = getDaoProvider().getUserActivityLogEntityDao();
			Filter<UserActivityLogEntity> filter = dao.createFilter();
			
			// filter by user ID
			if (userId != null) {
				filter.addExpression(Restrictions.eq(UserActivityLogEntity.ATTR_USER_ID, userId));
			}
			
			// order by timestamp
			if (isDescendingByTimestamp) {
				filter.addExpression(Order.desc(UserActivityLogEntity.ATTR_TIMESTAMP));
			}
			else {
				filter.addExpression(Order.asc(UserActivityLogEntity.ATTR_TIMESTAMP));
			}
			
			// max. number of returned records
			if (maxNumOfRecords < 1) {
				result = dao.find(filter);
			}
			else {
				result = dao.findNRecs(0, maxNumOfRecords, filter);
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
	 * Fetch the last user activity log entry filtered by the input parameters.
	 * @param executor the user ID of the executor, could be null
	 * @param userId the user ID of the "target" user, could be null
	 * @param actionCd the activity action ID, could be null
	 * @return the latest user activity log entry according to the criteria, or null
	 */
	public UserActivityLogEntity fetchLastUserActivityLog(final Long executor, final Long userId, final Long actionCd) {
		// [[
		UserActivityLogEntity result = null;
		Object[][] params = {{"executor", executor},
							{"userId", userId},
							{"actionCd", actionCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchLastUserActivityLog", params);
		}
		// ]]
		try {
			ReadOnlyDao<UserActivityLogEntity> dao = getDaoProvider().getUserActivityLogEntityDao();
			Filter<UserActivityLogEntity> filter = dao.createFilter();
			
			// filter by executor
			if (executor != null) {
				filter.addExpression(Restrictions.eq(UserActivityLogEntity.ATTR_EXECUTOR_ID, executor));
			}
			
			// filter by user ID
			if (userId != null) {
				filter.addExpression(Restrictions.eq(UserActivityLogEntity.ATTR_USER_ID, userId));
			}
			
			// filter by action
			if (actionCd != null) {
				filter.addExpression(Restrictions.eq(UserActivityLogEntity.ATTR_ACTION_CD, actionCd));
			}
			
			// order by timestamp to get the latest entry
			filter.addExpression(Order.desc(UserActivityLogEntity.ATTR_TIMESTAMP));
			
			List<UserActivityLogEntity> entries = dao.find(filter);
			if (entries != null && !entries.isEmpty()) {
				return result = entries.get(0);
			}
			else {
				return result = null;
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
	
	/**
	 * Records (stores) an internal login attempt.
	 * It may trigger the locking of the external user password according to the password policy.
	 * @param holspace the holspace name of the user
	 * @param isSuccessful true if the login attempt was successful otherwise false
	 * @throws IllegalArgumentException if the extLogin is null
	 * @throws LogonException if the user with extLogin could not be found
	 */
	public void recordIntLogInAttempt(final String holspace, final boolean isSuccessful) {
		// [[
		Object[][] params = {{"holspace", holspace},
							{"isSuccessful", isSuccessful + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "recordIntLogInAttempt", params);
		}
		// ]]
		try {
			if (holspace == null) {
				String msg = "holspace is null";
				LOG.error(msg);
				throw new IllegalArgumentException(msg);
			}
			
			UserEntity user = userService.fetchUserByHolspace(holspace, null);
			if (user == null) {
				String msg = "user not found, holspace: " + holspace;
				LOG.error(msg);
				throw new LogonException(msg, Reason.RES_ERROR_USER_NOT_FOUND);
			}
			
			UserActivityActionBsEntity userActivityAction;
			if (isSuccessful) {
				userActivityAction = referentialDataService.findBsEntity(UserActivityActionBsEntity.class, INT_LOGIN_SUCCESSFUL);
			}
			else {
				userActivityAction = referentialDataService.findBsEntity(UserActivityActionBsEntity.class, INT_LOGIN_FAILED);
			}
			
			String description = referentialDataService.findTxEntity(UserActivityActionTxEntity.class, Locale.getDefault(), userActivityAction).getTxt();
			
			persistUserActivity(user, userActivityAction, description);
			
			return;
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
	 * @param extLogin the external user login name of the user
	 * @param isSuccessful true if the login attempt was successful otherwise false
	 * @throws IllegalArgumentException if the extLogin is null
	 * @throws LogonException if the user with extLogin could not be found
	 */
	public void recordExtLogInAttempt(final String extLogin, final boolean isSuccessful) {
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
				String msg = "extLogin is null";
				LOG.error(msg);
				throw new IllegalArgumentException(msg);
			}
			
			UserEntity user = userService.fetchUserByExtLogin(extLogin, null);
			if (user == null) {
				String msg = "external user not found, extLogin: " + extLogin;
				LOG.error(msg);
				throw new LogonException(msg, Reason.RES_ERROR_USER_EXT_USER_NOT_FOUND);
			}
			
			UserActivityActionBsEntity userActivityAction;
			if (isSuccessful) {
				userActivityAction = referentialDataService.findBsEntity(UserActivityActionBsEntity.class, EXT_LOGIN_SUCCESSFUL);
			}
			else {
				userActivityAction = referentialDataService.findBsEntity(UserActivityActionBsEntity.class, EXT_LOGIN_FAILED);
			}
			
			String description = referentialDataService.findTxEntity(UserActivityActionTxEntity.class, Locale.getDefault(), userActivityAction).getTxt();
			
			persistUserActivity(user, userActivityAction, description);
			
			if (!isSuccessful) {
				lockExtUserPwdIfNecessary(user.getExtLogin());
			}
			
			return;
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
	 * Records (stores) a terminal login attempt.
	 * @param userId the user ID of the user
	 * @param isSuccessful true if the login attempt was successful otherwise false
	 * @throws IllegalArgumentException if the userId is null
	 * @throws LogonException if the user with userId could not be found
	 */
	public void recordTerminalLogInAttempt(final Long userId, final boolean isSuccessful) {
		// [[
		Object[][] params = {{"userId", userId},
							{"isSuccessful", isSuccessful + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "recordTerminalLogInAttempt", params);
		}
		// ]]
		try {
			if (userId == null) {
				String msg = "userId is null";
				LOG.error(msg);
				throw new IllegalArgumentException(msg);
			}
			
			UserEntity user = userService.fetchUserEntity(userId);
			if (user == null) {
				String msg = "user not found, userId: " + userId;
				LOG.error(msg);
				throw new LogonException(msg, Reason.RES_ERROR_USER_NOT_FOUND);
			}
			
			UserActivityActionBsEntity userActivityAction;
			if (isSuccessful) {
				userActivityAction = referentialDataService.findBsEntity(UserActivityActionBsEntity.class, TERMINAL_LOGIN_SUCCESSFUL);
			}
			else {
				userActivityAction = referentialDataService.findBsEntity(UserActivityActionBsEntity.class, TERMINAL_LOGIN_FAILED);
			}
			
			String description = referentialDataService.findTxEntity(UserActivityActionTxEntity.class, Locale.getDefault(), userActivityAction).getTxt();
			
			persistUserActivity(user, userActivityAction, description);
			
			return;
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
	 * Records (stores) a PDA login attempt.
	 * @param pdaUserNumber the PDA user number of the user
	 * @param isSuccessful true if the login attempt was successful otherwise false
	 * @throws IllegalArgumentException if the pdaUserNumber is null
	 * @throws LogonException if the user with the given PDA user number could not be found
	 */
	public void recordPdaLogInAttempt(final Long pdaUserNumber, final boolean isSuccessful) {
		// [[
		Object[][] params = {{"pdaUserNumber", pdaUserNumber},
							{"isSuccessful", isSuccessful + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "recordPdaLogInAttempt", params);
		}
		// ]]
		try {
			if (pdaUserNumber == null) {
				String msg = "pdaUserNumber is null";
				LOG.error(msg);
				throw new IllegalArgumentException(msg);
			}
			
			UserEntity user = userService.fetchUserByPdaUserNumber(pdaUserNumber, null);
			if (user == null) {
				String msg = "user not found, pdaUserNumber: " + pdaUserNumber;
				LOG.error(msg);
				throw new LogonException(msg, Reason.RES_ERROR_USER_NOT_FOUND);
			}
			
			UserActivityActionBsEntity userActivityAction;
			if (isSuccessful) {
				userActivityAction = referentialDataService.findBsEntity(UserActivityActionBsEntity.class, PDA_LOGIN_SUCCESSFUL);
			}
			else {
				userActivityAction = referentialDataService.findBsEntity(UserActivityActionBsEntity.class, PDA_LOGIN_FAILED);
			}
			
			String description = referentialDataService.findTxEntity(UserActivityActionTxEntity.class, Locale.getDefault(), userActivityAction).getTxt();
			
			persistUserActivity(user, userActivityAction, description);
			
			return;
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
	 * Records (stores) a log out user activity event.
	 * @param userId the user ID of the user who logged out
	 * @param loginType the type of the login (internal, external, etc.) see {@link Constant} LOGIN_TYPE_*
	 * @param isExpired true if the reason of the log out was that the session expired, use false at explicit log out
	 * @throws IllegalArgumentException is userId and/or loginType is null
	 * @throws LogonException if the user could not be found by the userId
	 */
	public void recordLogOut(Long userId, String loginType, boolean isExpired) {
		recordLogOut(userId, loginType, isExpired, null);
		
	}
	public void recordLogOut(final Long userId, final String loginType, final boolean isExpired, String ipAddress ) {
		// [[
		Object[][] params = {{"userId", userId},
							{"loginType", loginType},
							{"isExpired", isExpired + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "recordLogOut", params);
		}
		// ]]
		try {
			if (userId == null) {
				String msg = "userId is null";
				LOG.error(msg);
				throw new IllegalArgumentException(msg);
			}
			else if (loginType == null) {
				String msg = "loginType is null";
				LOG.error(msg);
				throw new IllegalArgumentException(msg);
			}
			
			UserEntity user = userService.fetchUserEntity(userId);
			if (user == null) {
				String msg = "user not found, userId: " + userId;
				LOG.error(msg);
				throw new LogonException(msg, Reason.RES_ERROR_USER_NOT_FOUND);
			}
			
			UserActivityActionBsEntity userActivityAction;
			if (Constant.LOGIN_TYPE_INTERNAL.equals(loginType)) {
				if (isExpired) {
					userActivityAction = referentialDataService.findBsEntity(UserActivityActionBsEntity.class, INT_LOGOUT_EXPIRED);
				}
				else {
					userActivityAction = referentialDataService.findBsEntity(UserActivityActionBsEntity.class, INT_LOGOUT);
				}
			}
			else if (Constant.LOGIN_TYPE_EXTERNAL.equals(loginType)) {
				if (isExpired) {
					userActivityAction = referentialDataService.findBsEntity(UserActivityActionBsEntity.class, EXT_LOGOUT_EXPIRED);
				}
				else {
					userActivityAction = referentialDataService.findBsEntity(UserActivityActionBsEntity.class, EXT_LOGOUT);
				}
			}
			else if (Constant.LOGIN_TYPE_TERMINAL.equals(loginType)) {
				if (isExpired) {
					userActivityAction = referentialDataService.findBsEntity(UserActivityActionBsEntity.class, TERMINAL_LOGOUT_EXPIRED);
				}
				else {
					userActivityAction = referentialDataService.findBsEntity(UserActivityActionBsEntity.class, TERMINAL_LOGOUT);
				}
			}
			else if (Constant.LOGIN_TYPE_PDA.equals(loginType)) {
				if (isExpired) {
					userActivityAction = referentialDataService.findBsEntity(UserActivityActionBsEntity.class, PDA_LOGOUT_EXPIRED);
				}
				else {
					userActivityAction = referentialDataService.findBsEntity(UserActivityActionBsEntity.class, PDA_LOGOUT);
				}
			}
			else {
				String msg = "unknown login type: " + loginType;
				LOG.error(msg);
				throw new IllegalArgumentException(msg);
			}
			
			String description = referentialDataService.findTxEntity(UserActivityActionTxEntity.class, Locale.getDefault(), userActivityAction).getTxt();
			
			persistUserActivity(user, userActivityAction, description, ipAddress);
			
			return;
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
	 * Records (stores) an external password unlock attempt of the external login.
	 * @param extLogin the external login name which was the target of the unlock attempt
	 * @param isSuccessful true if the unlock attempt was successful, false otherwise
	 * @throws IllegalArgumentException if the extLogin is null
	 * @throws LogonException if the user with extLogin could not be found
	 */
	public void recordExtLoginPwdUnlockAttempt(final String extLogin, final boolean isSuccessful) {
		// [[
		Object[][] params = {{"extLogin", extLogin},
							{"isSuccessful", isSuccessful + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "recordExtLoginPwdUnlockAttempt", params);
		}
		// ]]
		try {
			if (extLogin == null) {
				String msg = "extLogin is null";
				LOG.error(msg);
				throw new IllegalArgumentException(msg);
			}
			
			UserEntity user = userService.fetchUserByExtLogin(extLogin, null);
			if (user == null) {
				String msg = "external user not found, extLogin: " + extLogin;
				LOG.error(msg);
				throw new LogonException(msg, Reason.RES_ERROR_USER_EXT_USER_NOT_FOUND);
			}
			
			UserActivityActionBsEntity userActivityAction;
			if (isSuccessful) {
				userActivityAction = referentialDataService.findBsEntity(UserActivityActionBsEntity.class, EXT_PWD_UNLOCK_SUCCESSFUL);
			}
			else {
				userActivityAction = referentialDataService.findBsEntity(UserActivityActionBsEntity.class, EXT_PWD_UNLOCK_FAILED);
			}
			
			String description = referentialDataService.findTxEntity(UserActivityActionTxEntity.class, Locale.getDefault(), userActivityAction).getTxt();
			
			persistUserActivity(user, userActivityAction, description);
			
			return;
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
	 * Records (stores) an external login password change event
	 * @param extLogin the external login name whose external password was changed
	 * @throws IllegalArgumentException if the extLogin is null
	 * @throws LogonException if the user with extLogin could not be found
	 */
	public void recordExLoginPwdChanged(final String extLogin) {
		// [[
		Object[][] params = {{"extLogin", extLogin}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "recordExLoginPwdChanged", params);
		}
		// ]]
		try {
			if (extLogin == null) {
				String msg = "extLogin is null";
				LOG.error(msg);
				throw new IllegalArgumentException(msg);
			}
			
			UserEntity user = userService.fetchUserByExtLogin(extLogin, null);
			if (user == null) {
				String msg = "external user not found, extLogin: " + extLogin;
				LOG.error(msg);
				throw new LogonException(msg, Reason.RES_ERROR_USER_EXT_USER_NOT_FOUND);
			}
			
			UserActivityActionBsEntity userActivityAction = referentialDataService.findBsEntity(UserActivityActionBsEntity.class, EXT_PWD_CHANGED);
			
			String description = referentialDataService.findTxEntity(UserActivityActionTxEntity.class, Locale.getDefault(), userActivityAction).getTxt();
			
			persistUserActivity(user, userActivityAction, description);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	private void lockExtUserPwdIfNecessary(final String extLogin) {
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
			
			UserEntity user = userService.fetchUserByExtLogin(extLogin, null);
			if (user == null) {
				String msg = "external user not found, extLogin: " + extLogin;
				LOG.error(msg);
				throw new LogonException(msg, Reason.RES_ERROR_USER_EXT_USER_NOT_FOUND);
			}
			
			Date lock = user.getExtLoginPwdLockedTs();
			if (lock != null) {	
				LOG.warn("external user password is already locked, extLogin: " + user.getExtLogin());
				return;
			}
			else {
				int maxLoginAttempt = getConfigBean().getAppPolicyPasswordMaxLoginAttempt();
				
				String queryFailedLogins =
					"SELECT ual " +
					"FROM " + UserActivityLogEntity.class.getSimpleName() + " ual " +
					"WHERE ual.userId = ?1 and ual.actionCd = ?2 and ual.timestamp > ( " +
						"SELECT COALESCE(max(ual2.timestamp), (to_date('1970-01-01','YYYY-MM-DD'))) " +
						"FROM " + UserActivityLogEntity.class.getSimpleName() + " ual2 " +
						"WHERE ual2.userId = ?1 and ual2.actionCd in (?3, ?4) " +
					") " +
					"ORDER BY ual.timestamp DESC";
				
				List<Object> args = new ArrayList<Object>();
				args.add(user.getUserId());
				args.add(EXT_LOGIN_FAILED);
				args.add(EXT_LOGIN_SUCCESSFUL);
				args.add(EXT_PWD_UNLOCK_SUCCESSFUL);
				
				List<UserActivityLogEntity> failedLogins = getDaoProvider().getDirectJpaDao().find(queryFailedLogins, args.toArray());
				
				if (failedLogins != null && failedLogins.size() >= maxLoginAttempt) {	// lock ext user pwd
					UserActivityActionBsEntity userActivityExtPwdLock = referentialDataService.findBsEntity(UserActivityActionBsEntity.class, 28L);
					String description = referentialDataService.findTxEntity(UserActivityActionTxEntity.class, Locale.getDefault(), userActivityExtPwdLock).getTxt();
					UserActivityEntity uae = persistUserActivity(user, userActivityExtPwdLock, description);
					
					// millisec part reset due to conversion between different DB data types. TIMESTAMP(6) -> DATE (TLGNU_USER_ACTIVITY.CTL_CRE_TS -> TLGNU_USER.EXT_LOGIN_PWD_LOCKED_TS)
					Calendar cal = new GregorianCalendar();
					cal.setTime(uae.getCtlCreTs());
					cal.set(Calendar.MILLISECOND, 0);
					
					user.setExtLoginPwdLockedTs(cal.getTime());
					user = getDaoProvider().getUserEntityDao().merge(user);
					
					userAdminService.generateNewPasswordAndSendUnlockPasswordMail(user.getExtLogin());
				}
			}
			
			return;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	private UserActivityEntity persistUserActivity(final UserEntity user, final UserActivityActionBsEntity userActivityAction, String description) {
		return persistUserActivity(user, userActivityAction, description, null);
	}
	
	private UserActivityEntity persistUserActivity(final UserEntity user, final UserActivityActionBsEntity userActivityAction, String description, String ipAddressContext) {
		// [[
		UserActivityEntity result = null;
		Object[][] params = {{"user", user},
							{"userActivityAction", userActivityAction},
							{"description", description}};
		final boolean isEnteringExitingEnabled = LOG.isEnteringExitingEnabled();
		if (isEnteringExitingEnabled) {
			LOG.entering(this, "persistUserActivity", params);
		}
		// ]]
		try {
			if(ipAddressContext == null){
				ipAddressContext = LogonUtil.getClientIP();
			}
			if (ipAddressContext != null && ipAddressContext.length() > UserActivityEntity.DBSIZE_IP_ADDRESS) {
				LOG.error("ipAddress is too long: " + ipAddressContext + ", will be truncated!");
				ipAddressContext = org.apache.commons.lang.StringUtils.substring(ipAddressContext, 0, (int)UserActivityEntity.DBSIZE_IP_ADDRESS);
			}
			
			if (description != null && description.length() > UserActivityEntity.DBSIZE_DESCRIPTION) {
				LOG.error("description is too long: " + description + ", will be abbreviated!");
				description = org.apache.commons.lang.StringUtils.abbreviate(description, (int)UserActivityEntity.DBSIZE_DESCRIPTION);
			}
			
			// UserActivityEntity
			UserActivityEntity userActivity = new UserActivityEntity();
			userActivity.setUser(user);
			userActivity.setUserActivityAction(userActivityAction);
			userActivity.setIpAddress(ipAddressContext);
			userActivity.setDescription(description);
			userActivity = getDaoProvider().getUserActivityEntityDao().persist(userActivity);
	
			return result = userActivity;
		}
		finally {
		// [[
			if (isEnteringExitingEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
}
