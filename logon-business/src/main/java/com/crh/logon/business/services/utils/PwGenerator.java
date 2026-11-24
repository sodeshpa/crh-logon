/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.Ostermiller.util.RandPass;
import com.crh.logon.constants.Constant;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;


/**
 * Generates initial/new passwords for external users.
 * 
 * @author AdNovum Informatik AG
 */
public class PwGenerator {

	private static final class SessionIdentifierGenerator
	{
		  private final static int TOKEN_SIZE_BITS = 130;
		  private final static int TOKEN_STRING_RADIX = 32;

		  private SecureRandom random = new SecureRandom();

		  public String nextSessionId()
		  {
		    return new BigInteger(TOKEN_SIZE_BITS, random).toString(TOKEN_STRING_RADIX);
		  }
	}
	
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PwGenerator.class);
	
	private static SessionIdentifierGenerator sessionIdentifierGenerator = new SessionIdentifierGenerator();
    
	/**
	 * Generates a new external password according to the configured external password policies.
	 * @param configBean a config bean instance containing the up-to-date external password policies
	 * @return generated password which is conform with the configured external password policies
	 */
	public static String generateNewExternalPassword(ConfigBean configBean) {
		// ### !!! DO NOT LOG PASSWORD !!! ###
		// [[
		String result = null;
		Object[][] params = {{"configBean", configBean}};
		final boolean isEnteringExitingEnabled = LOG.isEnteringExitingEnabled();
		if (isEnteringExitingEnabled) {
			LOG.entering(PwGenerator.class, "generateNewExternalPassword", params);
		}
		// ]]
		// ### !!! DO NOT LOG PASSWORD !!! ###
		try {
			if (configBean == null) {
				LOG.error("configBean is null");
				throw new IllegalArgumentException("configBean is null");
			}
			
			int minLength = configBean.getAppPolicyPasswordLengthMinimal();
			int minLowercase = configBean.getAppPolicyPasswordLowercaseMinimal();
			int minUppercase = configBean.getAppPolicyPasswordUppercaseMinimal();
			int minNumeric = configBean.getAppPolicyPasswordNumericMinimal();
			int minSpecial = configBean.getAppPolicyPasswordSpecialMinimal();
			
			String lowercaseStr = configBean.getAppPolicyPasswordGeneratedLowercaseCharset();
			String uppercaseStr = configBean.getAppPolicyPasswordGeneratedUppercaseCharset();
			String numericStr = configBean.getAppPolicyPasswordGeneratedNumericCharset();
			String specialStr = configBean.getAppPolicyPasswordSpecialSetOfChars();
			
			if (minLength < 8) {
				String msg = "invalid app.policy.password configuration. configured minimum pwd length is: " + minLength + ", min. expected is 8";
				LOG.error(msg);
				throw new LogonException(msg, Reason.RES_ERROR_CONFIG_NOT_VALID);
			}
			else if (lowercaseStr == null || lowercaseStr.length() < minLowercase) {
				String msg = "invalid app.policy.password configuration. configured lowercase set is: " + lowercaseStr + ", min. expected: " + minLowercase;
				LOG.error(msg);
				throw new LogonException(msg, Reason.RES_ERROR_CONFIG_NOT_VALID);
			}
			else if (uppercaseStr == null || uppercaseStr.length() < minUppercase) {
				String msg = "invalid app.policy.password configuration. configured uppercase set is : " + uppercaseStr + ", min. expected: " + minUppercase;
				LOG.error(msg);
				throw new LogonException(msg, Reason.RES_ERROR_CONFIG_NOT_VALID);
			}
			else if (numericStr == null || numericStr.length() < minNumeric) {
				String msg = "invalid app.policy.password configuration. configured numeric set is: " + numericStr + ", min. expected: " + minNumeric;
				LOG.error(msg);
				throw new LogonException(msg, Reason.RES_ERROR_CONFIG_NOT_VALID);
			}
			else if (specialStr == null || specialStr.length() < minSpecial) {
				String msg = "invalid app.policy.password configuration. configured special set is: " + specialStr + " min. expected: " + minSpecial;
				LOG.error(msg);
				throw new LogonException(msg, Reason.RES_ERROR_CONFIG_NOT_VALID);
			}
			
			char[] lowercase = lowercaseStr.toCharArray();
			char[] uppercase = uppercaseStr.toCharArray();
			char[] numeric = numericStr.toCharArray();
			char[] special = specialStr.toCharArray();
			
			char[] allChars = StringUtils.concat(lowercase, uppercase, numeric, special);
			
			RandPass gen = new RandPass(SecureRandom.getInstance(Constant.SECURE_RANDOM_ALGORITHM), allChars);
			
			// set all requirements
			gen.addRequirement(lowercase, minLowercase);
			gen.addRequirement(uppercase, minUppercase);
			gen.addRequirement(numeric, minNumeric);
			gen.addRequirement(special, minSpecial);
			
			result = gen.getPass(minLength);
			
			return result;
		}
		catch (NoSuchAlgorithmException ex) {
			String msg = "Instantiation of secure random generator failed, algorithm: " + Constant.SECURE_RANDOM_ALGORITHM;
			LOG.error(msg);
			throw new LogonException(msg, Reason.RES_ERROR_IMPL);
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

	/**
	 * @return A random string usable as password reset token.
	 */
	public static String generatePasswordResetToken()
	{
		// [[
		String result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(PwGenerator.class, "generatePasswordResetToken");
		}
		// ]]
		try {
			return result = sessionIdentifierGenerator.nextSessionId();
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
