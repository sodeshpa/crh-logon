/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.utils;

import java.util.HashMap;
import java.util.Locale;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.persistence.entities.admin.RoleEntity;
import com.crh.logon.persistence.entities.admin.UserEntity;

/**
 * store the current user inside a threadlocal. be careful: since 
 * containers do threadpooling, a threadlocal might be dirty if not 
 * properly used. this class' setUser() should only be used by the 
 * UserFilter.
 * 
 * AuthUtils.getUser() can be used everywhere inside the webapp.
 * 
 * @author AdNovum Informatik AG
 */
@SuppressWarnings("unchecked")
public class UserProvider {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(UserProvider.class);
	
	private static ThreadLocal<UserEntity> user = new ThreadLocal<UserEntity>();
	private static ThreadLocal role = new ThreadLocal();
	private static ThreadLocal groups = new ThreadLocal();
    private static ThreadLocal<Locale> locales = new ThreadLocal<Locale>();
    private static ThreadLocal<TimeZone> timezones = new ThreadLocal<TimeZone>();
	
	public static void setUser(UserEntity userEntity) {
		user.set(userEntity);
        setLocale();
	}
	
	public static UserEntity getUser() {
		return user.get();
	}
	
	public static void setRole(RoleEntity roleEntity) {
		role.set(roleEntity);
	}
	
	public static RoleEntity getRole() {
		return (RoleEntity) role.get();
	}
	
	public static void setGroups(HashMap groupsMap) {
		groups.set(groupsMap);
	}
	
	public static HashMap getGroups() {
		return (HashMap) groups.get();
	}

    public static Locale getLocale() {
        return locales.get();
    }

    public static TimeZone getTimezone() {
        return timezones.get();
    }

    public static void setTimezone(TimeZone timezone) {
        timezones.set(timezone);
    }

    private static void setLocale() {
        UserEntity userEntity = user.get();

        if (userEntity == null) {
            locales.set(null);
        }
        else {
            locales.set(new Locale(userEntity.getApplicationLang().getLangCd()));
        }
    }
}
