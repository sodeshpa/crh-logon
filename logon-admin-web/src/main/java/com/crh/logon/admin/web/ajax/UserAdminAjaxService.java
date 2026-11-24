/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.ajax;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.useradmin.UserAdminForm;
import com.crh.logon.business.services.interfaces.UserAdminService;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.common.PersonEntity;
import com.crh.logon.persistence.entities.refdata.UserTypeBsEntity;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;

/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to 
 * AJAX components. 
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="UserAdminAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.services.beans.UserAdminSrchBean"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 */
public class UserAdminAjaxService extends DwrServiceSupport {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(UserAdminAjaxService.class);
    
    public ListRange fetchUserList(
    		int skip, 
    		int count, 
    		String sort,
    		HashMap<String, String> searchCrits,
    		HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"count", count + ""},
							{"sort", sort},
							{"searchCrits", searchCrits},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchUserList", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.USER_ADMIN_LOGON);
			String sortBy = null;
			
			boolean sortAsc = true;
			if (!StringUtils.empty(sort)) {
				String[] sortArr = sort.split(" ");
				sortBy = sortArr[0];
				sortAsc = "ASC".equals(sortArr[1]);
			}
			
			UserAdminForm adminForm = (UserAdminForm)session.getAttribute("admin_UserAdminForm");
			
			//get service
			UserEntity user = adminForm.getUserLoggedIn();
			
			UserAdminService userAdminService = getServiceLocator().getUserAdminService();
			ListRange resultList = userAdminService.fetchUserList(skip, count, sortBy, sortAsc, searchCrits, user);
			
			return result = resultList;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
    
    public boolean canUserModify(long id, HttpServletRequest request) { 
		// [[
		boolean result = false;
		Object[][] params = {{"id", id + ""},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "canUserModify", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.USER_ADMIN_LOGON);
			
			UserAdminService userAdminService = getServiceLocator().getUserAdminService();
			
			return result = userAdminService.canModifyUser(id);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }
    
    public ListRange fetchSoldToListById(
    		int start, 
    		int count, 
    		String orderBy,
    		String query,
    		HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSoldToListById", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.USER_ADMIN_LOGON);
			ListRange listRange = new ListRange();
			
			UserAdminService uAService = getServiceLocator().fetchUserAdminService();
			listRange.setData(uAService.fetchSoldToList(start, count, query, true).toArray());
			listRange.setTotalSize(uAService.countSoldToList(query, true));
			
			return result = listRange;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
    
    public ListRange fetchSoldToListByName(
    		int start, 
    		int count, 
    		String orderBy,
    		String query,
    		HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSoldToListByName", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.USER_ADMIN_LOGON);
			ListRange listRange = new ListRange();
			
			UserAdminService uAService = getServiceLocator().fetchUserAdminService();
			listRange.setData(uAService.fetchSoldToList(start, count, query, false).toArray());
			listRange.setTotalSize(uAService.countSoldToList(query, false));
			
			return result = listRange;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
    
    public ListRange fetchVendorListById(
    		int start, 
    		int count, 
    		String orderBy,
    		String query,
    		HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchVendorListById", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.USER_ADMIN_LOGON);
			ListRange listRange = new ListRange();
			
			UserAdminService uAService = getServiceLocator().fetchUserAdminService();
			listRange.setData(uAService.fetchCarrierVendorList(start, count, query, true).toArray());
			listRange.setTotalSize(uAService.countCarrierVendorList(query, true));
			
			return result = listRange;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
    
    public ListRange fetchVendorListByName(
    		int start, 
    		int count, 
    		String orderBy,
    		String query,
    		HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchVendorListByName", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.USER_ADMIN_LOGON);
			ListRange listRange = new ListRange();
			
			UserAdminService uAService = getServiceLocator().fetchUserAdminService();
			listRange.setData(uAService.fetchCarrierVendorList(start, count, query, false).toArray());
			listRange.setTotalSize(uAService.countCarrierVendorList(query, false));
			
			return result = listRange;
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
     * setting up an enmpty userentity, and sets it to the form. this method will be redundant as the handling of crh internal typed users will be replaced from this extjs based approach to the normal one
     * @param session - Http session
     * @return the newly initialized empty userentity
     */
    public UserEntity getEmptyUser(HttpSession session) {
		// [[
		UserEntity result = null;
		Object[][] params = {{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getEmptyUser", params);
		}
		// ]]
		try {
			
			UserEntity userEntity = new UserEntity();
			PersonEntity personEntity = new PersonEntity();
			userEntity.setPerson(personEntity);
			UserTypeBsEntity userTypeBsEntity = new UserTypeBsEntity();
			userTypeBsEntity.setCtlActCd(Boolean.TRUE);
			userTypeBsEntity.setUserTypeCd(Long.valueOf(Constant.USER_TYPE_CRH));
			userEntity.setUserType(userTypeBsEntity);
			UserAdminForm adminForm = (UserAdminForm)session.getAttribute("admin_UserAdminForm");
			userEntity.setSendDailyReportsFlag(Boolean.FALSE);
			adminForm.setUserEntity(userEntity);
			return result = userEntity;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }
    
    
    public UserEntity getStoredUser(HttpSession session) {
		// [[
		UserEntity result = null;
		Object[][] params = {{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getStoredUser", params);
		}
		// ]]
		try {
			UserAdminForm adminForm = (UserAdminForm)session.getAttribute("admin_UserAdminForm");
			return result = adminForm.getUserEntity();
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
     * Gets the detailed information about the selected user, which is going to be lay out on the UI's lower part.
     * @param id
     * @param status
     * @return
     */
    public UserEntity getUserDetail(long id, HttpSession session) {
		// [[
		UserEntity result1 = null;
		Object[][] params = {{"id", id + ""},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getUserDetail", params);
		}
		// ]]
		try {
			LOG.debug("Getting infor of user [ID:" + id + "]");
			UserAdminService userAdminService = getServiceLocator().getUserAdminService();
			UserEntity userEntity = userAdminService.loadUserDetail(id);
			UserAdminForm adminForm = (UserAdminForm)session.getAttribute("admin_UserAdminForm");
			adminForm.setUserEntity(userEntity);
			return result1 = userEntity;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result1);
			}
		// ]]
		}
    }
    
//	public UserEntity storeExternalAccess(String ldapHolSpace, String extPwd, HttpSession session) { 
//		// [[
//		UserEntity result = null;
//		Object[][] params = {{"ldapHolSpace", ldapHolSpace},
//							{"extPwd", extPwd},
//							{"session", session}};
//		boolean logDebugEnabled = LOG.isDebugEnabled();
//		if (logDebugEnabled) {
//			LOG.entering(this, "storeExternalAccess", params);
//		}
//		// ]]
//		try {
//			UserAdminForm adminForm = (UserAdminForm)session.getAttribute("admin_UserAdminForm");
//			UserEntity userEntity = adminForm.getUserEntity();
//			userEntity.getPerson().setActiveFlag(true);
//			userEntity.setExtLoginActiveFlag(true);
//			userEntity.setExtLoginPwd(extPwd);
//			String extLogin = adminForm.EXT_LOGIN_PREFIX_EMPLOYEE + "_" + ldapHolSpace;
//			userEntity.setExtLogin(extLogin);
//			return result = userEntity;
//		}
//		finally {
//		// [[
//			if (logDebugEnabled) {
//				LOG.exiting(result);
//			}
//		// ]]
//		}
//	}

	public UserEntity setAccessFlags(boolean isActiveFlag, HttpSession session) { 
		// [[
		UserEntity result = null;
		Object[][] params = {{"isActiveFlag", isActiveFlag + ""},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setAccessFlags", params);
		}
		// ]]
		try {
			UserAdminForm adminForm = (UserAdminForm)session.getAttribute("admin_UserAdminForm");
			UserEntity userEntity = adminForm.getUserEntity();
			
			userEntity.getPerson().setActiveFlag(isActiveFlag);
			if(!isActiveFlag){
				userEntity.setExtLoginActiveFlag(false);
			}
			
			return result = userEntity;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public UserEntity updateStoredUser(boolean isAdding, List details, HttpSession session) { 
		// [[
		UserEntity result = null;
		Object[][] params = {{"isAdding", isAdding + ""},
							{"details", details},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateStoredUser", params);
		}
		// ]]
		try {
			UserAdminForm adminForm = (UserAdminForm)session.getAttribute("admin_UserAdminForm");
			UserEntity userEntity = adminForm.getUserEntity();
			PersonEntity personEntity = userEntity.getPerson();
			
			if (details != null && details.size() != 0) {
				personEntity.setActiveFlag(Boolean.valueOf((String)details.get(0)));
				personEntity.setFirstname(StringUtils.nvl((String)details.get(1)));
				personEntity.setName(StringUtils.nvl((String)details.get(2)));
			
			//				userEntity.setIsActiveFlag(Boolean.valueOf((String)details.get(0)));
			//				userEntity.setFirstname(StringUtils.nvl((String)details.get(1)));
			//				userEntity.setName(StringUtils.nvl((String)details.get(2)));
				if(isAdding){
					userEntity.setLdapHolspace(StringUtils.nvl((String)details.get(3)));
				}
				personEntity.setEmail(StringUtils.nvl((String)details.get(4)));
				personEntity.setPhoneNr(StringUtils.nvl((String)details.get(5)));
				personEntity.setMobileNr(StringUtils.nvl((String)details.get(6)));
				userEntity.setExtLoginActiveFlag(Boolean.valueOf((String) details.get(7)));
				userEntity.setExtLogin(StringUtils.nvl((String)details.get(8)));
				userEntity.setExtLoginPwd(StringUtils.nvl((String)details.get(9)));
			//				
			//				userEntity.setEmail(StringUtils.nvl((String)details.get(4)));
			//				userEntity.setPhoneNr(StringUtils.nvl((String)details.get(5)));
			//				userEntity.setMobileNr(StringUtils.nvl((String)details.get(6)));
			//				userEntity.setExtLoginActiveFlag(Boolean.valueOf((String) details.get(7)));
			//				userEntity.setExtLogin(StringUtils.nvl((String)details.get(8)));
			//				userEntity.setExtLoginPwd(StringUtils.nvl((String)details.get(9)));
			}
			
			return result = userEntity;
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
