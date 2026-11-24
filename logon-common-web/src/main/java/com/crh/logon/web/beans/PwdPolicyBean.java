/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.ExtLoginPwdHistoryService;
import com.crh.logon.business.services.interfaces.UserAdminService;
import com.crh.logon.web.utils.PasswordCharactersUtil;
import com.crh.logon.web.utils.ServiceLocatorProvider;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.ConfigBeanProvider;


/**
 * @author AdNovum Informatik AG
 */
public class PwdPolicyBean implements Serializable {
	
	private static final long serialVersionUID = -6243060241752378996L;
	private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(PwdPolicyBean.class);
	
    public static final String EXT_PWD_POLICY_ERROR = "useradmin.error.ext.pwd.policy";
    
    public static final String EXT_PWD_POLICY_MIN_LENGTH		= "useradmin.ext.pwd.policy.min.length";
    public static final String EXT_PWD_POLICY_MIN_LOWERCASE	= "useradmin.ext.pwd.policy.min.lowercase";
    public static final String EXT_PWD_POLICY_MIN_UPPERCASE	= "useradmin.ext.pwd.policy.min.uppercase";
    public static final String EXT_PWD_POLICY_MIN_NUMERIC		= "useradmin.ext.pwd.policy.min.numeric";
    public static final String EXT_PWD_POLICY_MIN_SPECIAL		= "useradmin.ext.pwd.policy.min.special";
    
    public static final List<String> EXT_PWD_POLICIES = new ArrayList<String>();
    static {
    	EXT_PWD_POLICIES.add(EXT_PWD_POLICY_MIN_LENGTH);
    	EXT_PWD_POLICIES.add(EXT_PWD_POLICY_MIN_LOWERCASE);
    	EXT_PWD_POLICIES.add(EXT_PWD_POLICY_MIN_UPPERCASE);
    	EXT_PWD_POLICIES.add(EXT_PWD_POLICY_MIN_NUMERIC);
    	EXT_PWD_POLICIES.add(EXT_PWD_POLICY_MIN_SPECIAL);
    }
	
	private String key = null;
	private Boolean valid = null;
	private Integer expected = null;
	private Integer actual = null;

	public PwdPolicyBean() {
		this(null, null, null, null);
	}
	
	public PwdPolicyBean(String key, Boolean valid, Integer expected, Integer actual) {
		this.key = key;
		this.valid = valid;
		this.expected = expected;
		this.actual= actual;
	}
	
	/**
	 * Creates a list of external password policy beans initialized with the external policies to be checked
	 * @return list of external password policy beans
	 */
	public static List<PwdPolicyBean> createExtPwdPolicyBeans() {
		// [[
		List<PwdPolicyBean> result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(PwdPolicyBean.class, "createExtPwdPolicyBeans");
		}
		// ]]
		try {
			result = new ArrayList<PwdPolicyBean>();
			
			int minLength = ConfigBeanProvider.getConfigBean().getAppPolicyPasswordLengthMinimal();
			PwdPolicyBean lengt = new PwdPolicyBean(EXT_PWD_POLICY_MIN_LENGTH, null, minLength, 0);
			result.add(lengt);
			
			int minLowercase = ConfigBeanProvider.getConfigBean().getAppPolicyPasswordLowercaseMinimal();
			PwdPolicyBean lowercase = new PwdPolicyBean(EXT_PWD_POLICY_MIN_LOWERCASE, null, minLowercase, 0);
			result.add(lowercase);
			
			int minUppercase = ConfigBeanProvider.getConfigBean().getAppPolicyPasswordUppercaseMinimal();
			PwdPolicyBean uppercase = new PwdPolicyBean(EXT_PWD_POLICY_MIN_UPPERCASE, null, minUppercase, 0);
			result.add(uppercase);
			
			int minNumeric = ConfigBeanProvider.getConfigBean().getAppPolicyPasswordNumericMinimal();
			PwdPolicyBean numeric = new PwdPolicyBean(EXT_PWD_POLICY_MIN_NUMERIC, null, minNumeric, 0);
			result.add(numeric);
			
			int minSpecial = ConfigBeanProvider.getConfigBean().getAppPolicyPasswordSpecialMinimal();
			PwdPolicyBean special = new PwdPolicyBean(EXT_PWD_POLICY_MIN_SPECIAL, null, minSpecial, 0);
			result.add(special);
			
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
	 * Validates the new external password against the actual external password policies
	 * @param newExtPwd the new external password to be validated
	 * @return list of password policy beans containing the validation results for all actual external password policies
	 */
	public static List<PwdPolicyBean> validateExtPwdPolicy(final String newExtPwd) {
		// ### !!! DO NOT LOG PASSWORD !!! ###
		// [[
		List<PwdPolicyBean> result = null;
		Object[][] params = {{"newExtPwd", "********"}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(PwdPolicyBean.class, "validateExtPwdPolicy", params);
		}
		// ]]
		// ### !!! DO NOT LOG PASSWORD !!! ###
		try {
			
			result = PwdPolicyBean.createExtPwdPolicyBeans();
			
			boolean isValidationEnabled = ConfigBeanProvider.getConfigBean().getAppPolicyPasswordStrengthValidationEnabled();
			if (!isValidationEnabled) {
				return result;
			}
			
			PwdPolicyBean.setAllPolicyTo(Boolean.FALSE, result);
			
			if (newExtPwd == null) {
				LOG.error("input newExtPwd is null");
				return result;
			}
			
			List<Character> charsOfNewExtPwd = PasswordCharactersUtil.string2CharacterList(newExtPwd);
			Map<Integer, Integer> charTypeMap = PasswordCharactersUtil.countCharacterTypes(charsOfNewExtPwd);
			
			for (PwdPolicyBean pwdPolicyBean : result) {
				String key = pwdPolicyBean.getKey();
				
				int actual;
				int expected;
				
				if (EXT_PWD_POLICY_MIN_LENGTH.equals(key)) {
					actual = charsOfNewExtPwd.size();
					expected = ConfigBeanProvider.getConfigBean().getAppPolicyPasswordLengthMinimal();
				}
				else if (EXT_PWD_POLICY_MIN_LOWERCASE.equals(key)) {
					actual = PwdPolicyBean.getNvlIntValue(charTypeMap.get((int)Character.LOWERCASE_LETTER));
					expected = ConfigBeanProvider.getConfigBean().getAppPolicyPasswordLowercaseMinimal();
				}
				else if (EXT_PWD_POLICY_MIN_UPPERCASE.equals(key)) {
					actual = PwdPolicyBean.getNvlIntValue(charTypeMap.get((int)Character.UPPERCASE_LETTER));
					expected = ConfigBeanProvider.getConfigBean().getAppPolicyPasswordUppercaseMinimal();
				}
				else if (EXT_PWD_POLICY_MIN_NUMERIC.equals(key)) {
					actual = PwdPolicyBean.getNvlIntValue(charTypeMap.get((int)Character.DECIMAL_DIGIT_NUMBER));
					expected = ConfigBeanProvider.getConfigBean().getAppPolicyPasswordNumericMinimal();
				}
				else if (EXT_PWD_POLICY_MIN_SPECIAL.equals(key)) {
					actual = PasswordCharactersUtil.countSpecialChars(charsOfNewExtPwd);
					expected = ConfigBeanProvider.getConfigBean().getAppPolicyPasswordSpecialMinimal();
				}
				else {
					String errMsg = "unknown extPwdPolicy key";
					LOG.error(errMsg);
					throw new LogonException(errMsg, Reason.RES_ERROR_NOT_IMPL);
				}
				
				pwdPolicyBean.setActual(actual);
				pwdPolicyBean.setExpected(expected);
				
				if (actual >= expected) {
					pwdPolicyBean.setValid(Boolean.TRUE);
				}
				else {
					pwdPolicyBean.setValid(Boolean.FALSE);
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
	
	/**
	 * Validates the new password of the external user against the password history.
	 * A configured number (app.policy.password.history_size) of old passwords could not be reused as a new one.
	 * The validation could be enabled/disabled by the app.policy.password.history_validation.enabled configuration key.
	 * @param extLogin the external users login name
	 * @param hashedNewExtPwd the new hashed password of the external user
	 * @return true if the new password was not used by this external user recently (see @link{ExtLoginPwdHistoryServiceBean} for details) 
	 * @throws IllegalArgumentException if the extLogin or newExtPwd is null
	 */
	public static boolean validateExtPwdHistory(final String extLogin, final String hashedNewExtPwd) {
		// ### !!! DO NOT LOG PASSWORD !!! ###
		// [[
		boolean result = false;
		Object[][] params = {{"extLogin", extLogin},
							{"newExtPwd", "********"}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(PwdPolicyBean.class, "validateExtPwdHistory", params);
		}
		// ]]
		// ### !!! DO NOT LOG PASSWORD !!! ###
		try {
			boolean isValidationEnabled = ConfigBeanProvider.getConfigBean().getAppPolicyPasswordHistoryValidationEnabled();
			if (!isValidationEnabled) {
				return result = true;
			}
			
			if (extLogin == null || hashedNewExtPwd == null) {
				String msg = "extLogin and/or newExtPwd is null";
				LOG.error(msg);
				throw new IllegalArgumentException(msg);
			}
			
			Authorization.authorize(Authorization.PUBLIC_FUNCTION);
			ExtLoginPwdHistoryService extLoginPwdHistoryService = ServiceLocatorProvider.getServiceLocator().getExtLoginPwdHistoryService();
			List<String> pwdHistoryEntryList = extLoginPwdHistoryService.fetchAllExtLoginPwdHistoryOfUser(extLogin);
			if (pwdHistoryEntryList != null) {
				for (String pwdHistoryEntry : pwdHistoryEntryList) {
					if (hashedNewExtPwd.equals(pwdHistoryEntry)) {
						return result = false;
					}
				}
			}
			
			return result = true;
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
	 * Validates whether the external user is allowed to change her password according to the minimal password life time policy.
	 * The minimal password lifetime is configured via the app.policy.password.min_password_lifetime_minutes configuration key.
	 * @param extLogin the external users login name
	 * @return true if the user is allowed to change her password according to the configured minimal password life time policy, false otherwise
	 */
	public static boolean validateExtPwdChangeDate(final String extLogin) {
		// [[
		boolean result = false;
		Object[][] params = {{"extLogin", extLogin}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(PwdPolicyBean.class, "validateExtPwdChangeDate", params);
		}
		// ]]
		try {
			Date allowedEarliestChangeDate = getAllowedExtPwdChangeDate(extLogin);
			Date now = new Date();
			
			return result = now.after(allowedEarliestChangeDate);
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
	 * Returns the earliest date when the external user is allowed to change (again) her password.
	 * The minimal password lifetime is configured via the app.policy.password.min_password_lifetime_minutes configuration key.
	 * @param extLogin the external users login name
	 * @return the date when the external user is allowed to change (again) her password
	 */
	public static Date getAllowedExtPwdChangeDate(final String extLogin) {
		// [[
		Date result = null;
		Object[][] params = {{"extLogin", extLogin}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(PwdPolicyBean.class, "getAllowedExtPwdChangeDate", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.PUBLIC_FUNCTION);
			UserAdminService userAdminService = ServiceLocatorProvider.getServiceLocator().getUserAdminService();
			result = userAdminService.getAllowedExtPwdChangeDate(extLogin);
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
	
	
	private static void setAllPolicyTo(Boolean valid, List<PwdPolicyBean> pwdPolicies) {
		// [[
		Object[][] params = {{"valid", valid},
							{"pwdPolicies", pwdPolicies}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(PwdPolicyBean.class, "setAllPolicyTo", params);
		}
		// ]]
		try {
			if (pwdPolicies != null) {
				for (PwdPolicyBean pwdPolicyBean : pwdPolicies) {
					if (pwdPolicyBean != null) {
						pwdPolicyBean.setValid(valid);
					}
					else {
						LOG.error("pwdPolicyBean is null");
					}
				}
			}
			else {
				LOG.error("pwdPolicies is null");
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
	
    private static int getNvlIntValue(final Integer input) {
		// [[
		int result = 0;
		Object[][] params = {{"input", input}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(PasswordCharactersUtil.class, "getNvlIntValue", params);
		}
		// ]]
		try {
			if (input == null) {
				result = 0;
			}
			else {
				result = input.intValue();
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
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public Integer getExpected() {
		return expected;
	}

	public void setExpected(Integer expected) {
		this.expected = expected;
	}

	public Integer getActual() {
		return actual;
	}

	public void setActual(Integer actual) {
		this.actual = actual;
	}
}
