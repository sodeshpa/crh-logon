/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package ch.nevis.gdi.jpa;

/**
 * @author AdNovum Informatik AG
 */
public class ThreadLocalUserInfo {

	public static final boolean USE_ACCESS_RESTRICTIONS = true;
	public static final boolean DO_NOT_DISABLE_NEXT_ACCESS_RESTRICTIONS = false;
	public static final Long UNKNOWN_USER = null;

	private static ThreadLocal<Long> userId = new ThreadLocal<Long>();
	private static ThreadLocal<String> userIdString = new ThreadLocal<String>();
	private static ThreadLocal<Boolean> accessRestrictions = new ThreadLocal<Boolean>();
	private static ThreadLocal<Boolean> disableNextAccessRestrictions = new ThreadLocal<Boolean>();
	
	public static void setUserIdString(String uid) {
		userIdString.set(uid);
	}
	
	public static String getUserIdString() {
		return userIdString.get();
	}
	
	public static void setUserId(Long uid) {
		userId.set(uid);
	}
	
	public static Long getUserId() {
		return userId.get();
	}
	
	public static void setAccessRestrictions(Boolean accRestrictions) {
		accessRestrictions.set(accRestrictions);
	}
	public static Boolean getAccessRestrictions() {
		return accessRestrictions.get();
	}
	
	public static void setDisableNextAccessRestrictions(Boolean val) {
		disableNextAccessRestrictions.set(val);
	}
	
	public static Boolean getDisableNextAccessRestrictions() {
		return disableNextAccessRestrictions.get();
	}
	
	
}
