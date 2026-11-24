/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.security.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.security.auth.login.LoginException;

import org.apache.commons.codec.binary.Base64;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * A collection of cryptography functions.
 * 
 * @author AdNovum Informatik AG
 */
public class PasswordUtil {
    
	private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(PasswordUtil.class);
    
	private static final String HASH_ALGORITHM = "SHA-1"; // TODO (gerard Jul 28, 2009): make this configurable?
	
	private static final String HASH_PREFIX = HASH_ALGORITHM + ':';

	/**
	 * Creates a Base64 encoded SHA-1 hash of the given 'tohash' string.
	 * @param tohash the string to hash
	 * @return hash value of 'tohash', PREFIXED WITH "SHA-1:".
	 */
	public static String createHash(String tohash) throws LoginException {
		String result = null;
		final boolean debugEnabled = LOG.isDebugEnabled();

		if (debugEnabled) {
			Object[][] params = { {"tohash", "****"} }; // can be a secret, do not trace value
			LOG.entering(PasswordUtil.class, "createHash", params);
		}
		try {
			try {
				byte bytesToHash[] = tohash.getBytes();
				MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
				md.update(bytesToHash);
				byte hashBytes[] = md.digest();
			
				String hash = HASH_PREFIX + new String(Base64.encodeBase64(hashBytes));
			
				return result = hash;
			}
			catch (NoSuchAlgorithmException nsae) {
				String msg = "Hash algorithm '"+HASH_ALGORITHM+"' not supported, verify crypto provider setup.";
				LOG.error(msg, nsae);
				throw new LoginException(msg);
			}
		}
		finally {
			if (debugEnabled) {
				LOG.exiting(result);
			}
		}
	}
	
	public static String createHashWithoutPrefix(String tohash) throws LoginException {
		// [[
		String result = null;
		Object[][] params = {{"tohash", "****"} }; // can be a secret, do not trace value
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(PasswordUtil.class, "createHashWithoutPrefix", params);
		}
		// ]]
		try {
			result = createHash(tohash);
			if (result.startsWith(HASH_PREFIX)) {
				result = result.substring(HASH_PREFIX.length());
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
	
	public static boolean isHashedValue(String str) {
		boolean result = false;
		final boolean debugEnabled = LOG.isDebugEnabled();

		if (debugEnabled) {
			Object[][] params = { { "str", "****", } }; // can be a secret, do not trace value
			LOG.entering(PasswordUtil.class, "isHashedValue", params);
		}
		try {
			return result = ( (str != null) && (str.startsWith(HASH_PREFIX)) );
		}
		finally {
			if (debugEnabled) {
				LOG.exiting(result);
			}
		}
	}

    public static boolean validatePassword(String inputPassword, String expectedPassword) {
    	final boolean debugEnabled = LOG.isDebugEnabled();
    	boolean ret = false;
		
		if (debugEnabled) {
			LOG.entering(PasswordUtil.class, "validatePassword");
		}
		try {
			if (inputPassword == null || expectedPassword == null) {
				return ret = false;
			}
			return ret = inputPassword.equals(expectedPassword);
		}
		finally {
			if (debugEnabled) {
				LOG.exiting(ret);
			}
		}
    }

}
