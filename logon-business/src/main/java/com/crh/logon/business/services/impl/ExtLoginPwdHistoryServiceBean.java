/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.ExtLoginPwdHistoryServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.ExtLoginPwdHistoryServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UserServiceLocalInterface;
import com.crh.logon.persistence.entities.admin.ExtLoginPwdHistoryEntity;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.PasswordUtil;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class ExtLoginPwdHistoryServiceBean extends ExtLoginPwdHistoryServiceDispatcher implements ExtLoginPwdHistoryServiceLocalInterface {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ExtLoginPwdHistoryServiceBean.class);
	
	@EJB private UserServiceLocalInterface userService;
	
	/**
	 * Creates a new external password history entry and triggers the housekeeping of the older entries related to the external user.
	 * If an exception occurs then it is swallowed (but logged) to do not brake other business functions.
	 * @param extLogin the related external user login name
	 * @param hashedPwd the new hashed password of the external user (hash will be created if not hashed)
	 * @return the new and persisted external login password history entity or null if a problem occurred
	 */
	public ExtLoginPwdHistoryEntity createExtLoginPwdHistory(String extLogin, String hashedPwd) {
		// [[
		ExtLoginPwdHistoryEntity result = null;
		Object[][] params = {{"extLogin", extLogin},
							{"hashedPwd", hashedPwd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createExtLoginPwdHistory", params);
		}
		// ]]
		try {
			if (extLogin == null) {
				throw new LogonException("extLogin is null", Reason.RES_ERROR_IMPL);
			}
			else if (hashedPwd == null) {
				throw new LogonException("hashedPwd is null", Reason.RES_ERROR_IMPL);
			}
			
			UserEntity extUser = userService.fetchUserByExtLogin(extLogin, null);
			if (extUser == null) {
				throw new LogonException("external user not found, extLogin: " + extLogin, Reason.RES_ERROR_USER_EXT_USER_NOT_FOUND);
			}
			
			hashedPwd = PasswordUtil.hashValueIfNotHashed(hashedPwd);
			
			result = new ExtLoginPwdHistoryEntity();
			result.setUser(extUser);
			result.setExtLoginPwd(hashedPwd);
			
			result = getDaoProvider().getExtLoginPwdHistoryEntityDao().persist(result);
			
			// trigger housekeeping
			housekeepExtLoginPwdHistoryOfUser(extLogin);
			
			return result;
		}
		catch (Exception e) {
			LOG.error("exception occurred during ext pwd history creation", e);
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
	 * Fetches all stored (and hashed) external password entry of the external user.
	 * @param extLogin the external user login name whose password entries should be fetched
	 * @return a list of the hashed external user password entries, never returns null
	 */
	public List<String> fetchAllExtLoginPwdHistoryOfUser(String extLogin) {
		// [[
		List<String> result = null;
		Object[][] params = {{"extLogin", extLogin}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAllExtLoginPwdHistoryOfUser", params);
		}
		// ]]
		try {
			result = new ArrayList<String>();
			
			List<ExtLoginPwdHistoryEntity> entityList = null;
			try {
				entityList = fetchAllExtLoginPwdHistoryEntityOfUser(extLogin, true);
			}
			catch (LogonException e) {
				if (Reason.RES_ERROR_USER_EXT_USER_NOT_FOUND.equals(e.getReason())) {
					LOG.info("external user not found, extLogin: " + extLogin);
				}
				else {
					LOG.error("LOGON exception occurred", e);
				}
			}
			catch (Exception e) {
				LOG.error("unexpected exception occurred during fetch", e);
			}
			
			if (entityList != null) {
				for (ExtLoginPwdHistoryEntity entity : entityList) {
					result.add(entity.getExtLoginPwd());
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
	
	private List<ExtLoginPwdHistoryEntity> fetchAllExtLoginPwdHistoryEntityOfUser(String extLogin, boolean isAscending) {
		// [[
		List<ExtLoginPwdHistoryEntity> result = null;
		Object[][] params = {{"extLogin", extLogin},
							{"isAscending", isAscending + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAllExtLoginPwdHistoryEntityOfUser", params);
		}
		// ]]
		try {
			UserEntity extUser = userService.fetchUserByExtLogin(extLogin, null);
			if (extUser == null) {
				String msg = "external user not found, extLogin: " + extLogin;
				LOG.error(msg);
				throw new LogonException(msg, Reason.RES_ERROR_USER_EXT_USER_NOT_FOUND);
			}
			
			ReadOnlyDao<ExtLoginPwdHistoryEntity> dao = getDaoProvider().getExtLoginPwdHistoryEntityDao();
			Filter<ExtLoginPwdHistoryEntity> filter = new FilterImpl<ExtLoginPwdHistoryEntity>();
			filter.addExpression(Restrictions.eq(ExtLoginPwdHistoryEntity.ATTR_USER_ID, extUser.getUserId()));
			if (isAscending) {
				filter.addExpression(Order.asc(ExtLoginPwdHistoryEntity.ATTR_CTL_CRE_TS));
			}
			else {
				filter.addExpression(Order.desc(ExtLoginPwdHistoryEntity.ATTR_CTL_CRE_TS));
			}
			
			result = dao.find(filter);
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
	
	private void housekeepExtLoginPwdHistoryOfUser(String extLogin) {
		// [[
		Object[][] params = {{"extLogin", extLogin}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "housekeepExtLoginPwdHistoryOfUser", params);
		}
		// ]]
		try {
			ConfigBean configBean = getConfigBean();
			int maxPwdHistorySize = configBean.getAppPolicyPasswordHistorySize();
			
			if (maxPwdHistorySize > 0) {
				List<ExtLoginPwdHistoryEntity> extLoginPwdHistoryList = fetchAllExtLoginPwdHistoryEntityOfUser(extLogin, false);
				if (extLoginPwdHistoryList != null && extLoginPwdHistoryList.size() > maxPwdHistorySize) {
					List<ExtLoginPwdHistoryEntity> toDelete = extLoginPwdHistoryList.subList(maxPwdHistorySize, extLoginPwdHistoryList.size());
					for (ExtLoginPwdHistoryEntity extLoginPwdHistoryEntity : toDelete) {
						deleteExtLoginPwdHistory(extLoginPwdHistoryEntity);
					}
				}
			}
			else {
				LOG.warn("external password history size configured to: " + maxPwdHistorySize);
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
	
	private void deleteExtLoginPwdHistory(ExtLoginPwdHistoryEntity toDelete) {
		// [[
		Object[][] params = {{"toDelete", toDelete}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "deleteExtLoginPwdHistory", params);
		}
		// ]]
		try {
			ReadWriteDao<ExtLoginPwdHistoryEntity> dao = getDaoProvider().getExtLoginPwdHistoryEntityDao();
			dao.remove(toDelete);
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
	
}
