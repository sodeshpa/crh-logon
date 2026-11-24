/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.security.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.security.auth.login.LoginException;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.Ostermiller.util.RandPass;
import com.crh.logon.persistence.dao.to.ConfigLoginModuleValue;
import com.crh.logon.persistence.dao.util.DAOException;
import com.crh.logon.security.srv.RefDataServiceManager;
import com.crh.logon.utils.StringUtils;


/**
 * Generates initial/new passwords for external users.
 * 
 * @author AdNovum Informatik AG
 */
public class PwGenerator {

    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PwGenerator.class);
    
    private static final String CFG_APPPOLICYPASSWORDLENGTHMINIMAL = "app.policy.password.length_minimal";
    private static final String CFG_APPPOLICYPASSWORDLOWERCASEMINIMAL = "app.policy.password.lowercase_minimal";
    private static final String CFG_APPPOLICYPASSWORDUPPERCASEMINIMAL = "app.policy.password.uppercase_minimal";
    private static final String CFG_APPPOLICYPASSWORDNUMERICMINIMAL = "app.policy.password.numeric_minimal";
    private static final String CFG_APPPOLICYPASSWORDSPECIALMINIMAL = "app.policy.password.special_minimal";
    private static final String CFG_APPPOLICYPASSWORDGENERATEDLOWERCASECHARSET = "app.policy.password.generated.lowercase_charset";
    private static final String CFG_APPPOLICYPASSWORDGENERATEDUPPERCASECHARSET = "app.policy.password.generated.uppercase_charset";
    private static final String CFG_APPPOLICYPASSWORDGENERATEDNUMERICCHARSET = "app.policy.password.generated.numeric_charset";
    private static final String CFG_APPPOLICYPASSWORDSPECIALSETOFCHARS = "app.policy.password.special_set_of_chars";
    
	private static final String SECURERANDOM_ALGORITHM = "SHA1PRNG";
    
	/**
	 * Generates a new external password according to the configured external password policies.
	 * 
	 * @param conn 
				A connection (session) with a specific database. (Cannot be null).
	 * @param instance
	 * 			The active DB instance.
	 * @return 
	 * 			Generated password which is conform with the configured external password policies.
	 */
	public static String generateNewExternalPassword(RefDataServiceManager refDataSrv, String instance) throws DAOException, LoginException {
		// ### !!! DO NOT LOG PASSWORD !!! ###
		// [[
		String result = null;
		Object[][] params = {{"refDataSrv", refDataSrv}};
		final boolean isEnteringExitingEnabled = LOG.isEnteringExitingEnabled();
		if (isEnteringExitingEnabled) {
			LOG.entering(PwGenerator.class, "generateNewExternalPassword", params);
		}
		// ]]
		// ### !!! DO NOT LOG PASSWORD !!! ###
		try {
			if (refDataSrv == null) {
				LOG.error("refDataSrv is null");
				throw new IllegalArgumentException("refDataSrv is null");
			}
			
			int minLength = getRefDataValueInt(CFG_APPPOLICYPASSWORDLENGTHMINIMAL, instance, refDataSrv);
			int minLowercase = getRefDataValueInt(CFG_APPPOLICYPASSWORDLOWERCASEMINIMAL, instance, refDataSrv);
			int minUppercase = getRefDataValueInt(CFG_APPPOLICYPASSWORDUPPERCASEMINIMAL, instance, refDataSrv);
			int minNumeric = getRefDataValueInt(CFG_APPPOLICYPASSWORDNUMERICMINIMAL, instance, refDataSrv);
			int minSpecial = getRefDataValueInt(CFG_APPPOLICYPASSWORDSPECIALMINIMAL, instance, refDataSrv);
			
			String lowercaseStr = getRefDataValue(CFG_APPPOLICYPASSWORDGENERATEDLOWERCASECHARSET, instance, refDataSrv);
			String uppercaseStr = getRefDataValue(CFG_APPPOLICYPASSWORDGENERATEDUPPERCASECHARSET, instance, refDataSrv);
			String numericStr = getRefDataValue(CFG_APPPOLICYPASSWORDGENERATEDNUMERICCHARSET, instance, refDataSrv);
			String specialStr = getRefDataValue(CFG_APPPOLICYPASSWORDSPECIALSETOFCHARS, instance, refDataSrv);
			
			if (minLength < 8) {
				String msg = "invalid app.policy.password configuration. configured minimum pwd length is: " + minLength + ", min. expected is 8";
				LOG.error(msg);
				throw new LoginException(msg);
			}
			else if (lowercaseStr == null || lowercaseStr.length() < minLowercase) {
				String msg = "invalid app.policy.password configuration. configured lowercase set is: " + lowercaseStr + ", min. expected: " + minLowercase;
				LOG.error(msg);
				throw new LoginException(msg);
			}
			else if (uppercaseStr == null || uppercaseStr.length() < minUppercase) {
				String msg = "invalid app.policy.password configuration. configured uppercase set is : " + uppercaseStr + ", min. expected: " + minUppercase;
				LOG.error(msg);
				throw new LoginException(msg);
			}
			else if (numericStr == null || numericStr.length() < minNumeric) {
				String msg = "invalid app.policy.password configuration. configured numeric set is: " + numericStr + ", min. expected: " + minNumeric;
				LOG.error(msg);
				throw new LoginException(msg);
			}
			else if (specialStr == null || specialStr.length() < minSpecial) {
				String msg = "invalid app.policy.password configuration. configured special set is: " + specialStr + " min. expected: " + minSpecial;
				LOG.error(msg);
				throw new LoginException(msg);
			}
			
			char[] lowercase = lowercaseStr.toCharArray();
			char[] uppercase = uppercaseStr.toCharArray();
			char[] numeric = numericStr.toCharArray();
			char[] special = specialStr.toCharArray();
			
			char[] allChars = StringUtils.concat(lowercase, uppercase, numeric, special);
			
			RandPass gen = new RandPass(SecureRandom.getInstance(SECURERANDOM_ALGORITHM), allChars);
			
			// set all requirements
			gen.addRequirement(lowercase, minLowercase);
			gen.addRequirement(uppercase, minUppercase);
			gen.addRequirement(numeric, minNumeric);
			gen.addRequirement(special, minSpecial);
			
			result = gen.getPass(minLength);
			
			return result;
		}
		catch (NoSuchAlgorithmException ex) {
			String msg = "Instantiation of secure random generator failed, algorithm: " + SECURERANDOM_ALGORITHM;
			LOG.error(msg);
			throw new LoginException(msg);
		}
		finally {
			// ### !!! DO NOT LOG PASSWORD !!! ###
		// [[
			if (isEnteringExitingEnabled) {
				LOG.exiting("********");
			}
		// ]]
			// ### !!! DO NOT LOG PASSWORD !!! ###
		}
	}
	
	private static String getRefDataValue(String key, String instance, RefDataServiceManager refDataSrv) throws DAOException, LoginException {
		// [[
		String result = null;
		Object[][] params = {{"key", key},
							{"instance", instance},
							{"refDataSrv", refDataSrv}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(PwGenerator.class, "getRefDataValue", params);
		}
		// ]]
		try {
			ConfigLoginModuleValue configVal = refDataSrv.getConfigByKeyAndInstance(key, instance);
			if (configVal == null) {
				String errorMsg = "Couldn't load config value found with key: '" + key + "' for the instance:'" + instance + "'!";
				LOG.error(errorMsg);
				//throw new LoginException(errorMsg);
				return null;
			}
			
			return result = configVal.getValue();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private static int getRefDataValueInt(String key, String instance, RefDataServiceManager refDataSrv) throws DAOException, LoginException {
		// [[
		int result = 0;
		Object[][] params = {{"key", key},
							{"instance", instance},
							{"refDataSrv", refDataSrv}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(PwGenerator.class, "getRefDataValueInt", params);
		}
		// ]]
		try {
			String value = getRefDataValue(key, instance, refDataSrv);
			int val = -1;
			try {
				val = Integer.valueOf(value);
			}
			catch (NumberFormatException e) {
				String errorMsg = "The referenece data value is wrong (not a number): '" + value + "'!";
				LOG.error(errorMsg, e);
				throw new LoginException(errorMsg);				
			}
			return result = val;
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
