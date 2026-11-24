/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;


import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.DmzServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.DmzServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UserActivityServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.admin.RoleEntity;
import com.crh.logon.persistence.entities.admin.RoleFuncEntity;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.admin.UserRoleEntity;
import com.crh.logon.persistence.entities.admin.UserSapVendorAccEntity;
import com.crh.logon.persistence.entities.admin.UserSoldtoBpAccEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapVendorEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.PasswordUtil;
import com.crh.logon.utils.Reason;

/**
 * This service provides functions for the Dmz-loginmodule.
 * 
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class DmzServiceBean extends DmzServiceDispatcher implements DmzServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DmzServiceBean.class);

    @EJB private UserActivityServiceLocalInterface userActivityService;
    
    /**
     * Validates the provided password for this user in the database
     */
    public boolean validateUsersPassword(Long userid, String extPassword) {
    	LOG.debug("Checking userid " + userid);
    	
        // Check for empty extLogin
        if (userid == null) {
        	LOG.info("no userid provided");
        	
            return false;
        }
        
        ReadWriteDao<UserEntity> dao = getDaoProvider().getUserEntityDao();
        UserEntity userEntity = dao.findById(userid);
        
        if ( (userEntity == null) || 
        		(!userEntity.getExtLoginActiveFlag().booleanValue()) ||
        		(!userEntity.getPerson().getActiveFlag().booleanValue()) ) {

        	// the user 'userid' should come from getUserFromExtLogin and is validated already. 
        	throw new LogonException("No active user with id " + userid + " found", Reason.RES_ERROR_IMPL);
        }
        
        // check external login password lock
        if (userEntity.getExtLoginPwdLockedTs() != null) {
        	LOG.error("external login password is locked, extLogin: " + userEntity.getExtLogin());
        	userActivityService.recordExtLogInAttempt(userEntity.getExtLogin(), false);
        	return false;
        }
        
        // Check userid and password
        String expectedPassword = userEntity.getExtLoginPwd();
        if(extPassword == null || expectedPassword == null) {
        	if (extPassword == null) {
        		LOG.info("No password provided with login");
        	}
        	if (expectedPassword == null) {
        		LOG.info("No password known for user");
        	}
        	
        	userActivityService.recordExtLogInAttempt(userEntity.getExtLogin(), false);
        	
            return false;
        }
        
        boolean isValid = PasswordUtil.validatePassword(extPassword, expectedPassword);
        
        if (isValid) {
        	userActivityService.recordExtLogInAttempt(userEntity.getExtLogin(), true);
        }
        else {
        	userActivityService.recordExtLogInAttempt(userEntity.getExtLogin(), false);
        }
        
        return isValid;
    }
    
    /**
     * Validates the provided password for this user in the database
     */
    public boolean validatePickupBarcodePasscode(Long userid, String passCode) {
    	LOG.debug("Checking userid " + userid);
    	
        // Check for empty extLogin
        if (userid == null) {
        	LOG.info("no userid provided");
        	
            return false;
        }
        
        ReadWriteDao<UserEntity> dao = getDaoProvider().getUserEntityDao();
        UserEntity userEntity = dao.findById(userid);
        
        if ( (userEntity == null) || 
        		(!userEntity.getPerson().getActiveFlag().booleanValue()) ) {

        	// the user 'userid' should come from getUserFromPdaNumber and is validated already. 
        	throw new LogonException("No active user with id " + userid + " found", Reason.RES_ERROR_IMPL);
        }
                
        // Check userid and password
        String expectedPassCode = userEntity.getPickupBarcodePasscode();
        if(passCode == null || expectedPassCode == null) {
        	if (passCode == null) {
        		LOG.info("No passcode provided with login");
        	}
        	if (expectedPassCode == null) {
        		LOG.error("No pickup_barcode_passcode known for user. userId=" + "'" + userid +"'" + "");
        	}
        	userActivityService.recordTerminalLogInAttempt(userEntity.getUserId(), false);
        	
            return false;
        }
        
        boolean isValid = PasswordUtil.validatePassword(passCode, expectedPassCode);
        if (isValid) {
        	userActivityService.recordTerminalLogInAttempt(userEntity.getUserId(), true);
        }
        else {
        	userActivityService.recordTerminalLogInAttempt(userEntity.getUserId(), false);
        }
        
        return isValid;
    }
    
    /**
     * Returns the userid for the user that has the provided external login
     */
    public Long getUserFromExtLogin(String extLogin) {
        // Check for empty extLogin
        if (extLogin == null) {
            return null;
        }

        UserEntity userEntity = (UserEntity)getDaoProvider().getDirectJpaDao().findSingle("SELECT u FROM UserEntity u JOIN u.person p WHERE UPPER(u.extLogin) = UPPER(?1) AND u.person = p AND u.extLoginActiveFlag = true AND p.activeFlag = true", extLogin);
        if (userEntity == null) {
            
        	return null;
        }
        
        return userEntity.getUserId();
    }
    
    /**
     * Returns the userid for the user that has the provided external login
     */
    public Long getUserFromPdaNumber(String pdaNumber) {
        // Check for empty extLogin
    	Long pdaNo = null;
    	try{
    		pdaNo = Long.valueOf(pdaNumber);
    	}
    	catch (Exception e) {
    		return null;
		}
        if (pdaNo == null) {
            return null;
        }

        UserEntity userEntity = (UserEntity)getDaoProvider().getDirectJpaDao().findSingle("SELECT u FROM UserEntity u JOIN u.person p WHERE u.pdaUserNo = ?1 AND u.person = p AND p.activeFlag = true", pdaNo);
        if (userEntity == null) {
            
        	return null;
        }
        
        return userEntity.getUserId();
    }
 
    /**
     * Returns a set of all roles of this user
     */
    public Set getRoleSets(String userid, boolean requireExtLoginActive) {
        // Check for empty extLogin
        if (userid == null) {
            return null;
        }
        Long uid = null;
        try {
            uid = new Long(userid);
        }
        catch (NumberFormatException nfe) {
            String msg = "Provided userid '"+userid+"' cannot be converted to type 'Long'";
            LOG.error(msg, nfe);
            return null;
        }
        
        ReadWriteDao<UserEntity> userEntityDao = getDaoProvider().getUserEntityDao();
        Filter filter = new FilterImpl();
        filter.addExpression(Restrictions.eq(UserEntity.ATTR_USER_ID, uid));
        if ( requireExtLoginActive ) {        	
        	filter.addExpression(Restrictions.eq(UserEntity.ATTR_EXT_LOGIN_ACTIVE_FLAG, true));
        }
        UserEntity userEntity = userEntityDao.findSingle(filter);
        if (userEntity == null) {
            return null;
        }
        
        if (!isUserValid(userEntity)) { // if the vendor/costumer are not valid 
        	return null;
        }
        
        // Fetch attached roles
        Set functionsSet = new HashSet();
        
        ReadWriteDao<UserRoleEntity> userRoleEntityDao = getDaoProvider().getUserRoleEntityDao();
        filter = new FilterImpl();
        filter.addExpression(Restrictions.eq(UserRoleEntity.ATTR_USER, userEntity));
        
        List userRoleList = userRoleEntityDao.find(filter);
        for (Iterator itUserRole = userRoleList.iterator(); itUserRole.hasNext();) {
            UserRoleEntity ure = (UserRoleEntity) itUserRole.next();
            if (ure != null && ure.getRole() != null) {
                RoleEntity role = ure.getRole();
                ReadWriteDao<RoleFuncEntity> roleFuncEntityDao = getDaoProvider().getRoleFuncEntityDao();
                filter = new FilterImpl();
                filter.addExpression(Restrictions.eq(RoleFuncEntity.ATTR_ROLE, role));
                List roleFuncList = roleFuncEntityDao.find(filter);
                for (Iterator itRoleFuncList = roleFuncList.iterator(); itRoleFuncList.hasNext();) {
                    RoleFuncEntity rfe = (RoleFuncEntity) itRoleFuncList.next();
                    if (rfe != null) {
                        functionsSet.add(rfe.getFunc().getFuncId().toString());
                    }
                }
            }
        }
        
        return functionsSet;
    }
    
    /**
     * Check if the user type: "costumer"/"vendor" are valid.
     * 
     * Valid means: i.e.  costumer --> a company is set, and has restriction in the ACC table 
     * @param userEntity
     * @return
     */
    private boolean isUserValid(UserEntity userEntity) {
		// [[
		boolean result = false;
		Object[][] params = {{"userEntity", userEntity}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "isUserValid", params);
		}
		// ]]
		try {
			if (userEntity != null) {
				long userType = userEntity.getUserType().getUserTypeCd();
				
				if (Constant.USER_TYPE_CRH == userType) { // nothing to check
					return result = true;
				}
				
				DaoProvider provider = getDaoProvider();
				ReadOnlyDao<UserSapVendorAccEntity> uservendorEntityDao = provider.getUserSapVendorAccEntityDao();
				ReadOnlyDao<UserSoldtoBpAccEntity> customerEntityDao = provider.getUserSoldtoBpAccEntityDao();
				
				if (Constant.USER_TYPE_VENDOR == userType) { // check vendor
					SapVendorEntity sapVendor = userEntity.getSapVendor();
					
					final Long vendorSapId = sapVendor == null ? null : sapVendor.getSapVendorId();
					if (vendorSapId == null) { // cannot be empty/null (not nullable in DB)
						return result = false;
					}
					
					Filter<UserSapVendorAccEntity> filter = new FilterImpl<UserSapVendorAccEntity>();
			        filter.addExpression(Restrictions.eq(UserSapVendorAccEntity.ATTR_USER, userEntity));
			        filter.addExpression(Restrictions.eq(UserSapVendorAccEntity.ATTR_SAP_VENDOR_ID, vendorSapId));
			        
			        UserSapVendorAccEntity resultEntity = uservendorEntityDao.findSingle(filter);
			        
			        return result = (resultEntity != null);
				}			
				else if (Constant.USER_TYPE_CUSTOMER == userType) { // check costumer
					SapBpEntity sapBp = userEntity.getSapBp();
					
					final Long bpSapId = sapBp == null ? null : sapBp.getSapBpId();
					
					if (bpSapId == null) { // cannot be empty/null
						return result = false;
					}
					
					Filter<UserSoldtoBpAccEntity> filter = new FilterImpl<UserSoldtoBpAccEntity>();
			        filter.addExpression(Restrictions.eq(UserSoldtoBpAccEntity.ATTR_USER, userEntity));
			        filter.addExpression(Restrictions.eq(UserSoldtoBpAccEntity.ATTR_SOLDTO_SAP_BP_ID, bpSapId));
			        
			        UserSoldtoBpAccEntity resultEntity = customerEntityDao.findSingle(filter);
			        
			        return result = (resultEntity != null);
				}
			}
			
			return result = false; // should never happen
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
