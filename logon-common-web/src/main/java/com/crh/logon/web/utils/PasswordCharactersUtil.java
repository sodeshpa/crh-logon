/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.nevis.jcan.log.AdnTrace;

/**
 * A collection of cryptography functions.
 * 
 * @author AdNovum Informatik AG
 */
@AdnTrace
public class PasswordCharactersUtil {

    /**
     * Counts the special characters in the input string
     * The set of special characters are configured under app.policy.password.special_set_of_chars
     * @param inputStr the input string to be analyzed
     * @return the number of special characters in the input string
     */
	@AdnTrace(excludeArgument={"inputStr"})
    public static int countSpecialChars(final String inputStr) {
		List<Character> inputChars = PasswordCharactersUtil.string2CharacterList(inputStr);
		int result = PasswordCharactersUtil.countSpecialChars(inputChars);
		return result;
    }
    
    /**
     * Counts the special characters in the input character list
     * The set of special characters are configured under app.policy.password.special_set_of_chars
     * @param inputChars the character list to be analyzed
     * @return the number of special characters in the in
    public void setExtLoginPwd(String extLoginPwd) {put character list
     */
	@AdnTrace(excludeArgument={"inputChars"})
    public static int countSpecialChars(final List<Character> inputChars) {
		int result = 0;
		if (inputChars != null) {
			String specialStr = ConfigBeanProvider.getConfigBean().getAppPolicyPasswordSpecialSetOfChars();
			List<Character> specialChars = PasswordCharactersUtil.string2CharacterList(specialStr);
			if (specialChars != null && !specialChars.isEmpty()) {
				for (char ch : inputChars) {
					if (specialChars.contains(ch)) {
						++result;
					}
				}
			}
		}
		return result;
    }
    
    /**
     * Converts a string into a character list
     * @param inputStr the string to be converted
     * @return the character list containing the characters of the input string (order is kept) or null
     */
	@AdnTrace(excludeArgument={"inputStr"})
    public static List<Character> string2CharacterList(final String inputStr) {
		List<Character> result = null;
		if (inputStr != null) {
			result = new ArrayList<Character>();
			for (char ch : inputStr.toCharArray()) {
				result.add(ch);
			}
		}
		return result;
    }
    
    /**
     * Counts the character types (see @link{java.lang.Character}) of the input character list
     * @param inputChars the character list to be analyzed
     * @return map with character types (as key) and the number of occurrences (as value)
     */
	@AdnTrace(excludeArgument={"inputChars"})
    public static Map<Integer, Integer> countCharacterTypes(final List<Character> inputChars) {
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		if (inputChars != null && !inputChars.isEmpty()) {
			for (Character ch : inputChars) {
				int chType = Character.getType(ch);
				Integer numOfChType = result.get(chType);
				if (numOfChType == null) {
					numOfChType = 0;
				}
				result.put(chType, ++numOfChType);
			}
		}
		return result;
    }
}