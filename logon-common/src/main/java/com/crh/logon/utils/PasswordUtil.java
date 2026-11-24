/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.text.MessageFormat;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import org.apache.commons.codec.binary.Base64;

/**
 * A collection of cryptography functions.
 *
 * @author AdNovum Informatik AG
 */
@AdnTrace
public final class PasswordUtil {

	private static final JcanLogger LOG =
			JcanLoggerFactory.getLogger(PasswordUtil.class);

	private static final String SEPARATOR = ":";

	private static final String SHA_1_ALGORITHM = "SHA-1";

	private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";

	private static final String SHA_1_PREFIX = SHA_1_ALGORITHM;

	private static final String PBKDF2_PREFIX = "HASH";

	private static final String EXC_MESSAGE_TMPL = "Hash algorithm ''{0}'' or the current configuration is not supported, "
			+ "verify crypto provider setup.";

	/**
	 * Parameters used in creating PBKDF2 hash, they are modifiable without breaking the previously generated and stored hashes.
	 */
	private static final int SALT_BYTES_LENGTH = 128;

	private static final int HASH_BYTES_LENGTH = 128;

	private static final int PBKDF2_ITERATIONS = 2000;

	private PasswordUtil() {
	}

	/**
	 * Creates a Base64 encoded SHA-1 hash of the given 'toHash' string.
	 *
	 * @param toHash the string to hash
	 * @return hash value of 'toHash', PREFIXED WITH "SHA-1:".
	 */
	@AdnTrace(excludeArgument = { "toHash" })
	private static String createSHA1Hash(String toHash) {
		try {
			byte[] bytesToHash = toHash.getBytes();
			MessageDigest md = MessageDigest.getInstance(SHA_1_ALGORITHM);
			md.update(bytesToHash);
			byte[] hashBytes = md.digest();

			return SHA_1_PREFIX + SEPARATOR + toBase64String(hashBytes);
		}
		catch (NoSuchAlgorithmException nsae) {
			String msg = MessageFormat.format(EXC_MESSAGE_TMPL, SHA_1_ALGORITHM);
			LOG.error(msg, nsae);
			throw new LogonException(msg, nsae);
		}
	}

	private static String toBase64String(byte[] bytes) {
		byte[] encodedBytes = Base64.encodeBase64(bytes);
		return new String(encodedBytes);
	}

	private static byte[] fromBase64String(String base64String) {
		byte[] encodedBytes = base64String.getBytes();
		return Base64.decodeBase64(encodedBytes);
	}

	/**
	 * Creates a Base64 encoded PBKDF2 hash of the given 'toHash' string.
	 *
	 * @param toHash the string to hash
	 * @return hash value of 'toHash' extended with the required parameters for reproducing the hash value from plain text,
	 * PREFIXED WITH "HASH:".
	 */
	@AdnTrace(excludeArgument = { "toHash" })
	public static String createHash(String toHash) {
		try {
			SecureRandom random = new SecureRandom();
			byte[] salt = new byte[SALT_BYTES_LENGTH];
			random.nextBytes(salt);
			char[] plain = toHash.toCharArray();

			return createHash(plain, salt, PBKDF2_ITERATIONS, HASH_BYTES_LENGTH);
		}
		catch (Exception e) {
			String msg = MessageFormat.format(EXC_MESSAGE_TMPL, PBKDF2_ALGORITHM);
			LOG.error(msg, e);
			throw new LogonException(msg, e);
		}
	}

	@AdnTrace(excludeArgument = { "plain" })
	private static String createHash(char[] plain, byte[] salt, int iterationCount, int hashBytesLength)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		int keyLength = hashBytesLength * 8;

		PBEKeySpec spec = new PBEKeySpec(plain, salt, iterationCount, keyLength);
		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
		byte[] hashBytes = secretKeyFactory.generateSecret(spec).getEncoded();

		return PBKDF2_PREFIX + SEPARATOR + iterationCount + SEPARATOR + toBase64String(salt) + SEPARATOR + toBase64String(
				hashBytes);
	}

	@AdnTrace(excludeArgument = { "plain" })
	private static String createHash(char[] plain, PBKDF2Params params) throws NoSuchAlgorithmException,
			InvalidKeySpecException {
		return createHash(plain, params.getSalt(), params.getIterations(), params.getHashBytesLength());
	}

	@AdnTrace(excludeArgument = { "encodedTokenData" })
	public static String createHashFromEncodedTokenData(String encodedTokenData) {
		try {
			byte[] tokenDataBytes = fromBase64String(encodedTokenData);
			String tokenData = new String(tokenDataBytes);
			PBKDF2Params params = getPBKDF2ParamsFromTokenData(tokenData);
			String token = tokenData.split(SEPARATOR)[3];
			char[] plain = token.toCharArray();
			return createHash(plain, params);
		}
		catch (Exception e) {
			String msg = MessageFormat.format(EXC_MESSAGE_TMPL, PBKDF2_ALGORITHM);
			LOG.error(msg, e);
			throw new LogonException(msg, e);
		}
	}

	private static PBKDF2Params getPBKDF2ParamsFromTokenData(String tokenData) {
		PBKDF2Params params = null;
		String[] splitToken = tokenData.split(SEPARATOR);
		if (splitToken.length == 4) {
			int iterations = Integer.parseInt(splitToken[0]);
			byte[] salt = fromBase64String(splitToken[1]);
			int hashBytesLength = Integer.parseInt(splitToken[2]);
			params = new PBKDF2Params(iterations, salt, hashBytesLength);
		}
		return params;
	}

	@AdnTrace(excludeArgument = { "token" })
	public static String createEncodedTokenData(String tokenHash, String token) {
		try {
			PBKDF2Params pbkdf2Params = PasswordUtil.getPBKDF2ParamsFromHash(tokenHash);
			if (pbkdf2Params == null) {
				throw new IllegalArgumentException("param 'tokenHash' was invalid: " + tokenHash);
			}
			String iterationsPart = String.valueOf(pbkdf2Params.getIterations());
			String saltPart = toBase64String(pbkdf2Params.getSalt());
			String hashBytesLengthPart = String.valueOf(pbkdf2Params.getHashBytesLength());
			String tokenData = iterationsPart + SEPARATOR + saltPart + SEPARATOR + hashBytesLengthPart + SEPARATOR + token;
			byte[] tokenDataBytes = tokenData.getBytes();
			return toBase64String(tokenDataBytes);
		}
		catch (Exception e) {
			String msg = MessageFormat.format(EXC_MESSAGE_TMPL, PBKDF2_ALGORITHM);
			LOG.error(msg, e);
			throw new LogonException(msg, e);
		}
	}

	@AdnTrace(excludeArgument = { "toHash" })
	public static String hashValueIfNotHashed(String toHash) {
		String hashedValue = null;
		if (toHash != null) {
			if (PBKDF2_ALGORITHM.equals(hashType(toHash))) {
				hashedValue = toHash;
			}
			else {
				hashedValue = createHash(toHash);
			}
		}
		return hashedValue;
	}

	private static String hashType(String str) {
		String hashType = "NONE";
		if (str != null) {
			if (str.startsWith(SHA_1_PREFIX)) {
				hashType = SHA_1_ALGORITHM;
			}
			else if (str.startsWith(PBKDF2_PREFIX)) {
				hashType = PBKDF2_ALGORITHM;
			}
		}
		return hashType;
	}

	@AdnTrace(excludeArgument = { "inputPassword" })
	public static boolean validatePassword(String inputPassword, String expectedPassword) {
		boolean valid = false;
		String hashType = null;
		try {
			if (inputPassword != null && expectedPassword != null) {
				String hashedInputPassword = inputPassword;
				hashType = hashType(expectedPassword);
				if (PBKDF2_ALGORITHM.equals(hashType)) {
					PBKDF2Params params = getPBKDF2ParamsFromHash(expectedPassword);
					char[] plain = inputPassword.toCharArray();
					hashedInputPassword = createHash(plain, params);
				}
				else if (SHA_1_ALGORITHM.equals(hashType)) {
					hashedInputPassword = createSHA1Hash(inputPassword);
				}
				valid = expectedPassword.equals(hashedInputPassword);
			}
			return valid;
		}
		catch (Exception e) {
			String msg = MessageFormat.format(EXC_MESSAGE_TMPL, hashType);
			LOG.error(msg, e);
			throw new LogonException(msg, e);
		}
	}

	private static PBKDF2Params getPBKDF2ParamsFromHash(String hash) {
		PBKDF2Params params = null;
		String hashType = hashType(hash);
		if (PBKDF2_ALGORITHM.equals(hashType)) {
			String[] splitHash = hash.split(SEPARATOR);
			if (splitHash.length == 4) {
				int iterations = Integer.parseInt(splitHash[1]);
				byte[] salt = fromBase64String(splitHash[2]);
				byte[] rawHash = fromBase64String(splitHash[3]);
				int hashBytesLength = rawHash.length;
				params = new PBKDF2Params(iterations, salt, hashBytesLength);
			}
		}
		return params;
	}
}
