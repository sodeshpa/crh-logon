/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.UserActivityService;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.refdata.UserActivityActionTxEntity;
import com.crh.logon.persistence.entities.refdata.UserActivityActionTxPK;
import com.crh.logon.persistence.entities.views.UserActivityLogEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.ConfigBeanProvider;
import com.crh.logon.web.utils.ServiceLocatorProvider;
import com.crh.logon.web.utils.UserProvider;

/**
 * @author AdNovum Informatik AG
 */
public class UserActivityLogBean implements Serializable {

	private static final long serialVersionUID = -4670892122506218928L;
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(UserActivityLogBean.class);
	private static final String SEP = "_";

	private static Map<String, String> userActivityActionTxMap = null;
	
	private Long userActivityId = null;
	private String timestamp = null;
	private Long userId = null;
	private String userFirstname = null;
	private String userName = null;
	private String userHolspace = null;
	private String userExtlogin = null;
	private Long userPdauser = null;
	private Long executorId = null;
	private String executorGuiLabel = null;
	private Long actionCd = null;
	private String actionGuiLabel = null;
	private String ipAddress = null;
	private String description = null;

	
	public UserActivityLogBean() {	
	}
	
	public UserActivityLogBean(UserActivityLogEntity entity, String lang, boolean isAdminView, TimeZone tz) {
		// [[
		Object[][] params = {{"entity", entity},
							{"lang", lang},
							{"isAdminView", isAdminView + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "UserActivityLogBean", params);
		}
		// ]]
		try {
			if (entity != null) {
				// id
				this.userActivityId = entity.getUserActivityId();
				
				if (tz.isSame(ConfigBeanProvider.getConfigBean().getAppDefaultTimezone())) {
					this.timestamp = tz.fmtForUserActivity(entity.getTimestamp());
				}
				else {
					this.timestamp = tz.fmtForUserActivityWithUTCOffset(entity.getTimestamp());
				}
				
				// user related
				this.userName = entity.getUserName();
				this.userFirstname = entity.getUserFirstname();
				this.userHolspace = entity.getUserHolspace();
				this.userExtlogin = entity.getUserExtlogin();
				this.userPdauser = entity.getUserPdauser();
				
				// action
				String userActivityActionTxMapKey = generateUserActivityActionTxMapKey(entity.getActionCd(), lang);
				if (userActivityActionTxMap == null) {	// init actin map if needed
					initUserActivityActionTxMap();
				}
				this.actionGuiLabel = userActivityActionTxMap.get(userActivityActionTxMapKey);
				
				// other
				this.ipAddress = entity.getIpAddress();
				this.description = entity.getDescription();
				
				// only for admins
				if (isAdminView) {
					// executor related
					this.executorId = entity.getExecutorId();
					if (!StringUtils.empty(entity.getExecutorHolspace())) {
						this.executorGuiLabel = entity.getExecutorHolspace();
					}
					else if (!StringUtils.empty(entity.getExecutorExtlogin())) {
						this.executorGuiLabel = entity.getExecutorExtlogin();
					}
					else if (!StringUtils.empty(entity.getExecutorName())) {
						this.executorGuiLabel = entity.getExecutorName();
					}
					else if (entity.getExecutorId() != null) {
						this.executorGuiLabel = entity.getExecutorId().toString();
					}
					
					// action CD
					this.actionCd = entity.getActionCd();
					
					// userId
					this.userId = entity.getUserId();
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
	
	public static boolean isAdminViewAllowed() {
		// [[
		boolean result = false;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(UserActivityLogBean.class, "isAdminViewAllowed");
		}
		// ]]
		try {
			// check user admin right
			if (Authorization.isAllowed(Authorization.USER_ADMIN_LOGON)) {
				UserEntity loggedInUser = UserProvider.getUser();
				// check restricted user admin
				if (loggedInUser.getSapBp() == null && loggedInUser.getSapVendor() == null) {
					result = true;
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
	
	private String generateUserActivityActionTxMapKey(Long id, String langKey) {
		// [[
		String result = null;
		Object[][] params = {{"id", id},
							{"langKey", langKey}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "generateUserActivityActionTxMapKey", params);
		}
		// ]]
		try {
			if (id == null || langKey == null) {
				String msg = "id and/or langKey is null";
				LOG.error(msg);
				throw new IllegalArgumentException(msg);
			}
			
			langKey = langKey.toLowerCase();
			
			StringBuilder sb = new StringBuilder(id.toString());
			sb.append(SEP);
			sb.append(langKey);
			
			return result = sb.toString();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private void initUserActivityActionTxMap() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initUserActivityActionTxMap");
		}
		// ]]
		try {
			userActivityActionTxMap = new HashMap<String, String>();
			
			ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator();
			UserActivityService userActivityService = serviceLocator.getUserActivityService();
			List<UserActivityActionTxEntity> userActivityactionTxList = userActivityService.fetchAllUserActivityActionTx();
			
			if (userActivityactionTxList != null) {
				for (UserActivityActionTxEntity entity : userActivityactionTxList) {
					try {
						UserActivityActionTxPK txPk = entity.getUserActivityActionTxPK();
						Long id = txPk.getUserActivityActionBs().getUserActivityActionCd();
						String lang = txPk.getLangCd();
						
						String key = generateUserActivityActionTxMapKey(id, lang);
						
						userActivityActionTxMap.put(key, entity.getTxt());
					}
					catch (Exception e) {
						LOG.error("exception occurred during", e);
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

	public Long getUserActivityId() {
		return userActivityId;
	}

	public void setUserUserActivityId(Long userUserActivityId) {
		this.userActivityId = userUserActivityId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserFirstname() {
		return userFirstname;
	}

	public void setUserFirstname(String userFirstname) {
		this.userFirstname = userFirstname;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserHolspace() {
		return userHolspace;
	}

	public void setUserHolspace(String userHolspace) {
		this.userHolspace = userHolspace;
	}

	public String getUserExtlogin() {
		return userExtlogin;
	}

	public void setUserExtlogin(String userExtlogin) {
		this.userExtlogin = userExtlogin;
	}

	public Long getUserPdauser() {
		return userPdauser;
	}

	public void setUserPdauser(Long userPdauser) {
		this.userPdauser = userPdauser;
	}

	public Long getExecutorId() {
		return executorId;
	}

	public void setExecutorId(Long executorId) {
		this.executorId = executorId;
	}

	public String getExecutorGuiLabel() {
		return executorGuiLabel;
	}

	public void setExecutorGuiLabel(String executorGuiLabel) {
		this.executorGuiLabel = executorGuiLabel;
	}

	public Long getActionCd() {
		return actionCd;
	}

	public void setActionCd(Long actionCd) {
		this.actionCd = actionCd;
	}

	public String getActionGuiLabel() {
		return actionGuiLabel;
	}

	public void setActionGuiLabel(String actionGuiLabel) {
		this.actionGuiLabel = actionGuiLabel;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTimestamp() {
		return timestamp;
	}
}
