/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.UserServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.UserServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.admin.PreferencesEntity;
import com.crh.logon.persistence.entities.admin.RoleEntity;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.admin.UserRoleEntity;
import com.crh.logon.persistence.entities.admin.UserSapVendorAccEntity;
import com.crh.logon.persistence.entities.admin.UserSoldtoBpAccEntity;
import com.crh.logon.persistence.entities.refdata.UserTypeBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapVendorEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.PasswordUtil;
import com.crh.logon.utils.Reason;

/**
 * This service retrieves a user information
 * 
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
@AdnTrace
public class UserServiceBean extends UserServiceDispatcher implements UserServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(UserServiceBean.class);

	public UserEntity fetchUserEntity(Long userId) {
		ReadWriteDao<UserEntity> dao = getDaoProvider().getUserEntityDao();
		Filter<UserEntity> filter = new FilterImpl<UserEntity>();
		filter.addExpression(Restrictions.eq(UserEntity.ATTR_USER_ID, userId));
		return dao.findSingle(filter);
	}
	
	public RoleEntity fetchRole(UserEntity user) {
		ReadWriteDao<UserRoleEntity> userRoleEntityDao = getDaoProvider().getUserRoleEntityDao();
		Filter<UserRoleEntity> filter = new FilterImpl<UserRoleEntity>();
		filter.addExpression(Restrictions.eq(UserRoleEntity.ATTR_USER, user));
		List<UserRoleEntity> userRoleList = userRoleEntityDao.findNRecs(0, 1, filter);
		if (userRoleList != null && !userRoleList.isEmpty()) {
			UserRoleEntity userRoleEntity = userRoleList.get(0);
			return userRoleEntity.getRole();
		}
		return null;
	}
	
	/**
	 * Fetches a list of Preferences entities for the given user.
	 * 
	 * @param user A user to which filter the preferences entities.
	 * @param screen see 
	 * Constant.USER_PREFERENCE_SCREEN_ORDER_SEARCH,
	 * Constant.USER_PREFERENCE_SCREEN_TRUCK_SCHEDULING.
	 * 
	 * @return a list of Preferences entities for the given user.
	 */
	private List<PreferencesEntity> fetchPreferences(UserEntity user, String screen) {
		final ReadWriteDao<PreferencesEntity> preferencesEntityDao = getDaoProvider().getPreferencesEntityDao();
		final Filter<PreferencesEntity> filter = new FilterImpl<PreferencesEntity>();
		filter.addExpression(Restrictions.eq(PreferencesEntity.ATTR_USER, user));
		filter.addExpression(Restrictions.ilike(PreferencesEntity.ATTR_PREFERENCE_KEY, screen
				+ "_%"));
		final List<PreferencesEntity> preferencesList = preferencesEntityDao.find(filter);
		return preferencesList;
	}
	
	/**
	 * Fetches the UI preferences for the given user.
	 * Never returns null.
	 * 
	 * @param screen see 
	 * Constant.USER_PREFERENCE_SCREEN_ORDER_SEARCH,
	 * Constant.USER_PREFERENCE_SCREEN_TRUCK_SCHEDULING.
	 * 
	 * @return A map with keys as Strings as declared in PreferencesKey and 
	 * values as Objects that could be one of:
	 * - String,
	 * - Long,
	 * - String (concatenated values like this: "1,2.3.4"),
	 * - List<Long> (after the parse of concatenated values).
	 */
	public List<PreferencesEntity> fetchPreferencesList(UserEntity user, String screen) {
		return fetchPreferences(user, screen);
	}
	
	public UserEntity fetchUserByHolspace(final String holspace, final Boolean isActive) {
		ReadOnlyDao<UserEntity> dao = getDaoProvider().getUserEntityDao();
		Filter<UserEntity> filter = new FilterImpl<UserEntity>();
		filter.addExpression(Restrictions.eq(UserEntity.ATTR_LDAP_HOLSPACE, holspace));
		if (isActive != null) {
			filter.addExpression(Restrictions.eq(UserEntity.ATTR_EXT_LOGIN_ACTIVE_FLAG, isActive));
		}
		return dao.findSingle(filter);
	}
	
	public UserEntity fetchUserByExtLogin(final String extLogin, final Boolean isActive) {
		ReadOnlyDao<UserEntity> dao = getDaoProvider().getUserEntityDao();
		Filter<UserEntity> filter = new FilterImpl<UserEntity>();
		filter.addExpression(Restrictions.eq(UserEntity.ATTR_EXT_LOGIN, extLogin));
		if (isActive != null) {
			filter.addExpression(Restrictions.eq(UserEntity.ATTR_EXT_LOGIN_ACTIVE_FLAG, isActive));
		}
		return dao.findSingle(filter);
	}
	
	/**
	 * Returns the unique user whose stored reset token hash is equal to the hash of the
	 * specified token
	 * @param token a password reset token
	 * @return the found user, or <code>null</code> if no user was found
	 */
	public UserEntity fetchUserByExtPwdResetToken(final String token) {
		if (token == null) {
			throw new IllegalArgumentException("token must not be null");
		}
		final String tokenHash = PasswordUtil.createHashFromEncodedTokenData(token);

		LOG.debug("Trying to load a user using the token hash '{}'", new Object[] { tokenHash });

		ReadOnlyDao<UserEntity> dao = getDaoProvider().getUserEntityDao();
		Filter<UserEntity> filter = new FilterImpl<UserEntity>();
		filter.addExpression(Restrictions.eq(UserEntity.ATTR_EXT_PWD_RESETTOKEN_HASH, tokenHash));
		UserEntity userFound = dao.findSingle(filter);

		if (userFound == null) {
			LOG.debug("Result is null");
		}

		return userFound;
	}

	public UserEntity fetchUserByPdaUserNumber(final Long pdaUserNumber, final Boolean isActive) {
		ReadOnlyDao<UserEntity> dao = getDaoProvider().getUserEntityDao();
		Filter<UserEntity> filter = new FilterImpl<UserEntity>();
		filter.addExpression(Restrictions.eq(UserEntity.ATTR_PDA_USER_NO, pdaUserNumber));
		if (isActive != null) {
			filter.addExpression(Restrictions.eq(UserEntity.ATTR_EXT_LOGIN_ACTIVE_FLAG, isActive));
		}
		return dao.findSingle(filter);
	}

	/**
	 * like 'hasBpRestrictions(Long userId)' but uses currently authorized user
	 * 
	 * @return true or false depending on if there are bp restrictions
	 */
	public boolean hasBpRestrictions() {
		Long userId = getUserId();
		return hasBpRestrictions(userId);
	}

	/**
	 * the idea of this function is to find out if a user has any BP restrictions. 
	 * if a use doesn't have any, then the user restruictions most probably were set up
	 * in a wrong way and we deny access to the customer application.
	 * 
	 * @return true or false depending on if there are bp restrictions
	 */
	public boolean hasBpRestrictions(Long userId) {
		UserEntity userEntity = fetchUserEntity(userId);
		if (userEntity == null) {
			throw new LogonException("userEntity == null" + " (userid = '" + userId + "')",
					Reason.RES_ERROR_SECURITY_NOT_ALLOWED);
		}
		UserTypeBsEntity userType = userEntity.getUserType();

		SapBpEntity sapBp = userEntity.getSapBp();
		SapVendorEntity sapVendor = userEntity.getSapVendor();

		int user_type_cd = userType.getUserTypeCd().intValue();

		Long sap_bp_id = null;
		Long sapVendorId = null;

		if (sapBp != null) {
			sap_bp_id = userEntity.getSapBp().getSapBpId();
		}

		if (sapVendor != null) {
			sapVendorId = userEntity.getSapVendor().getSapVendorId();
		}
		long count = 0;
		switch (user_type_cd) {
		case Constant.USER_TYPE_CRH:
			break;
		case Constant.USER_TYPE_CUSTOMER:
			LOG.debug("CUSTOMER");
			if (sap_bp_id == null) {
				LOG.debug("sap_bp_id == null");
				throw new LogonException("user is of type customer, but no sap_bp_id is set"
						+ " (userid = '" + userId + "')", Reason.RES_ERROR_SECURITY_NOT_ALLOWED);
			}

			count = sapBpAccCount(userEntity, sapBp);
			if (count == 0) {
				LOG.debug("cnt == 0");
				throw new LogonException(
						"user is of type customer, but the restriction for its customer "
								+ "(sap_bp_id) does not exist in tlgnu_user_soldto_bp_acc"
								+ " (userid = '" + userId + "')",
						Reason.RES_ERROR_SECURITY_NOT_ALLOWED);
			}
			LOG.debug("CUSTOMER OK!");
			break;
		case Constant.USER_TYPE_VENDOR:
			LOG.debug("VENDOR");
			if (sapVendorId == null) {
				LOG.debug("sapVendorId == null");
				throw new LogonException("user is of type vendor, but no sap_vendor_id is set"
						+ " (userid = '" + userId + "')", Reason.RES_ERROR_SECURITY_NOT_ALLOWED);
			}
			count = vendorAccCount(userEntity, sapVendor);
			if (count == 0) {
				LOG.debug("cnt == 0");
				throw new LogonException(
						"user is of type vendor, but the restriction for its vendor "
								+ "(sap_vendor_id) does not exist in tlgnu_user_sap_vendor_acc"
								+ " (userid = '" + userId + "')",
						Reason.RES_ERROR_SECURITY_NOT_ALLOWED);
			}
			LOG.debug("VENDOR OK");
			break;
		default:
			LOG.error("unknown user type");
			throw new LogonException("error, unknown user type" + " (userid = '" + userId + "')",
					Reason.RES_ERROR_SECURITY_NOT_ALLOWED);
		}

		return true;
	}
	
	private long sapBpAccCount(UserEntity userEntity, SapBpEntity sapBp) {
		ReadWriteDao<UserSoldtoBpAccEntity> userSoldtoBpAccEntityDao = getDaoProvider().getUserSoldtoBpAccEntityDao();
		Filter<UserSoldtoBpAccEntity> filter = new FilterImpl<UserSoldtoBpAccEntity>();
		filter.addExpression(Restrictions.eq(UserSoldtoBpAccEntity.ATTR_USER, userEntity));
		filter.addExpression(Restrictions.eq(UserSoldtoBpAccEntity.ATTR_SOLDTO_SAP_BP, sapBp));
		return userSoldtoBpAccEntityDao.count(filter);
	}
	
	private long vendorAccCount(UserEntity userEntity, SapVendorEntity sapVendor) {
		ReadWriteDao<UserSapVendorAccEntity> userSapVendorAccEntityDao = getDaoProvider().getUserSapVendorAccEntityDao();
		Filter<UserSapVendorAccEntity> filter = new FilterImpl<UserSapVendorAccEntity>();
		filter.addExpression(Restrictions.eq(UserSapVendorAccEntity.ATTR_USER, userEntity));
		filter.addExpression(Restrictions.eq(UserSapVendorAccEntity.ATTR_SAP_VENDOR, sapVendor));
		return userSapVendorAccEntityDao.count(filter);
	}
	
	/**
	 * Saves the user preferences to the given user. 
	 * If the user already had a preference for the same key, it updates it.
	 * 
	 * @param screen see 
	 * Constant.USER_PREFERENCE_SCREEN_ORDER_SEARCH,
	 * Constant.USER_PREFERENCE_SCREEN_TRUCK_SCHEDULING.
	 */
	public void savePreferences(UserEntity user, String screen, List<PreferencesEntity> preferencesList) {
		if (user == null) {
			throw new IllegalArgumentException("user is mandatory for saving preferences");
		}
		if (preferencesList == null) {
			throw new IllegalArgumentException("preferences is mandatory for saving preferences");
		}
		final ReadWriteDao<PreferencesEntity> preferencesDao = getDaoProvider().getPreferencesEntityDao();

		final List<PreferencesEntity> oldPreferencesList = fetchPreferences(user, screen);

		for (PreferencesEntity newPreference : preferencesList) {
			final String newPreferenceKey = newPreference.getPreferenceKey();
			newPreference.setUser(user);
			
			boolean oldPreferenceFound = false;
			if (oldPreferencesList != null && !oldPreferencesList.isEmpty()) {
				for (PreferencesEntity oldPreference : oldPreferencesList) {
					final String oldPreferenceKey = oldPreference.getPreferenceKey();

					if (oldPreferenceKey.equals(newPreferenceKey)) {
						// Update the old preference of the user if any.

						oldPreference.setValStr(newPreference.getValStr());
						oldPreference.setValDat(newPreference.getValDat());
						oldPreference.setValNum(newPreference.getValNum());
						
						oldPreferenceFound = true;
						break;
					}
				}
			}

			if (!oldPreferenceFound) {
				// Save the new preference entity					
				preferencesDao.persist(newPreference);
			}
		}
	}
	
	/**
	 * Removes the preferences for the given user and for the given screen.
	 * 
	 * @param user
	 * @param screen see 
	 * Constant.USER_PREFERENCE_SCREEN_ORDER_SEARCH,
	 * Constant.USER_PREFERENCE_SCREEN_TRUCK_SCHEDULING.
	 */
	public void removePreferences(UserEntity user, String screen) {
		final List<PreferencesEntity> preferencesList = fetchPreferences(user, screen);
		for (PreferencesEntity preferencesEntity : preferencesList) {
			getDaoProvider().getPreferencesEntityDao().remove(preferencesEntity);
		}
	}
}
