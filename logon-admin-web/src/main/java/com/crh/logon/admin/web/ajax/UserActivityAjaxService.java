/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.ajax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.beans.UserActivityLogBean;
import com.crh.logon.business.services.interfaces.UserActivityService;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.views.UserActivityLogEntity;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.UserProvider;


/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to 
 * AJAX components. 
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="UserActivityAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="hibernate3"
 *                     match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.admin.web.beans.UserActivityLogBean"
 */
public class UserActivityAjaxService extends DwrServiceSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(UserActivityAjaxService.class);
	
	public ListRange fetchUserActivity(int skip, int count, String sort, HashMap<String, String> searchCrits, HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"count", count + ""},
							{"sort", sort},
							{"searchCrits", searchCrits},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchUserActivity", params);
		}
		// ]]
		try {
			result = new ListRange();
			
			Authorization.authorize(Authorization.PUBLIC_FUNCTION);
			
			UserEntity loggedInUser = UserProvider.getUser();
			String langCd = loggedInUser.getApplicationLang().getLangCd();
			
			String sortBy = null;
			boolean sortAsc = true;
			if (!StringUtils.empty(sort)) {
				String[] sortArr = sort.split(" ");
				sortBy = sortArr[0];
				sortAsc = "ASC".equals(sortArr[1]);
			}
			
			// force userId filter for normal users
			boolean isAdminViewAllowed = UserActivityLogBean.isAdminViewAllowed();
			if (!isAdminViewAllowed) {
				if (searchCrits != null) {
					searchCrits.clear();
				}
				else {
					searchCrits = new HashMap<String, String>();
				}
				
				Long userId = UserProvider.getUser().getUserId();
				if (userId == null) {	// to be sure not to allow full search in a strange situation
					LOG.error("could not force userId filter, userId is null");
					throw new IllegalArgumentException("userId is null");
				}
				searchCrits.put("userId", userId.toString());
			}

			UserActivityService userActivityService = getServiceLocator().getUserActivityService();
			ListRange userActivityLogEntityList = userActivityService.fetchUserActivity(
					skip, count, sortBy, sortAsc, searchCrits, getUserTimeZone());
			
			List<UserActivityLogBean> userActivityLogBeanList = new ArrayList<UserActivityLogBean>();
			if (userActivityLogEntityList != null) {
				TimeZone tz = UserProvider.getTimezone();
				for (Object userActivityLogEntity : userActivityLogEntityList.getData()) {
					UserActivityLogBean bean = new UserActivityLogBean((UserActivityLogEntity)userActivityLogEntity, langCd, isAdminViewAllowed, tz);
					userActivityLogBeanList.add(bean);
				}
			}
			
			result.setTotalSize(userActivityLogEntityList.getTotalSize());
			result.setData(userActivityLogBeanList.toArray());
			
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
	
	public ListRange fetchLastLogin(int skip, int count, String sort, HashMap<String, String> searchCrits, HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"count", count + ""},
							{"sort", sort},
							{"searchCrits", searchCrits},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchLastLogin", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.USER_ADMIN_LOGON);
			
			if (UserActivityLogBean.isAdminViewAllowed()) {
				String sortBy = null;
				boolean sortAsc = true;
				if (!StringUtils.empty(sort)) {
					String[] sortArr = sort.split(" ");
					sortBy = sortArr[0];
					sortAsc = "ASC".equals(sortArr[1]);
				}

				TimeZone userTimeZone = UserProvider.getTimezone();

				UserActivityService userActivityService = getServiceLocator().getUserActivityService();
				result = userActivityService.fetchLastLogin(
						skip, count, sortBy, sortAsc, searchCrits, userTimeZone);
			}
			else {
				result = new ListRange();
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
}
