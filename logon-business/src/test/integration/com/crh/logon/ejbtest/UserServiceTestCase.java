/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.ejbtest;

import java.util.HashMap;
import java.util.List;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.beans.UserAdminSrchBean;
import com.crh.logon.business.services.interfaces.UserAdminService;
import com.crh.logon.business.services.interfaces.UserService;
import com.crh.logon.persistence.entities.admin.RoleEntity;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.admin.UserRoleEntity;
import com.crh.logon.utils.ListRange;

/**
 * @author AdNovum Informatik AG
 */
@AdnTrace
public class UserServiceTestCase extends JunitSupport {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(
			UserServiceTestCase.class.getName());
	
	public UserServiceTestCase(String testCaseName) {
		super(testCaseName);
	}
	
	/**
	 * Tests the sample service
	 */
	public void testHasBpRestrictions() {
		UserAdminService userAdminService = serviceLocator.getUserAdminService();
		UserService userService = serviceLocator.getUserService();
		UserEntity userEntity = userService.fetchUserEntity(1L);

		HashMap<String, String> searchCrit = new HashMap<String, String>();
		searchCrit.put("doFiltering", "false");
		searchCrit.put("activeFlag", "true");

		ListRange<UserAdminSrchBean> fetchedUsers = userAdminService.fetchUserList(0, 100, "",
				true, searchCrit, userEntity);

		for (UserAdminSrchBean userAdminSrchBean : fetchedUsers.getData()) {
			Long userId = userAdminSrchBean.getUserId();
			LOG.info("'" + userId + "' hasBpRestrictions: " + userService.hasBpRestrictions(userId));
		}
	}
	
	public void testGrantableRoleList() {
		final long USER_ID = 1200L;
		
		UserAdminService userAdminService = serviceLocator.getUserAdminService();

		List<RoleEntity> roles = userAdminService.fetchRoleList(null, true);
		
		LOG.info("All roles:");
		for (RoleEntity role : roles) {
			LOG.info(role.getRoleName());
		}
		
		List<UserRoleEntity> userRoles = userAdminService.fetchAssignedRoleList(USER_ID);
		
		LOG.info("Asigned roles:");
		for (UserRoleEntity role : userRoles) {
			LOG.info(role.getRole().getRoleName());
		}

		List<RoleEntity> grantableRoles = userAdminService.fetchGrantableRoleList(USER_ID);

		LOG.info("GrantableRoleList roles:");
		for (RoleEntity role : grantableRoles) {
			LOG.info(role.getRoleName());
		}
		
	}
	
}






