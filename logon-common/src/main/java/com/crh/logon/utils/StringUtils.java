/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * Static string manipulation utility methods.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 */
public class StringUtils {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(StringUtils.class);

    private static Random random = new Random();

    /**
     * Returns the stack trace for an exception as a string.
     *
     * @param ex Exception some exception.
     * @return String the stack trace.
     */
    public static String getStackTrace(Exception ex) {
		// [[
		String result = null;
		Object[][] params = {{"ex", ex}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(StringUtils.class, "getStackTrace", params);
		}
		// ]]
		try {
	        ByteArrayOutputStream buf = new ByteArrayOutputStream();
	        ex.printStackTrace(new PrintStream(buf));
	        return result = buf.toString();
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
     * Returns the stack trace for a throwable as a string.
     *
     * @param thr Throwable some throwable.
     * @return String the stack trace.
     */
    public static String getStackTrace(Throwable thr) {
    	ByteArrayOutputStream buf = new ByteArrayOutputStream();
    	thr.printStackTrace(new PrintStream(buf));
    	return buf.toString();
    }

    /**
     * Returns <code>true</code> if a string is null, empty, or merely
     * whitespace.
     *
     * @param s String
     * @return boolean
     *
     * @adnlog.notrace
     */
    public static boolean empty(String s) {
        return s == null || s.trim().length() == 0;
    }

    /**
     * Returns <code>true</code> if a string is not null, not empty, and not
     * merely whitespace.
     *
     * @param s String
     * @return boolean
     * 
     * @adnlog.notrace
     */
    public static boolean notEmpty(String s) {
        return s != null && s.trim().length() > 0;
    }

    /**
     * Returns the passed-in string unchanged if it is a non-null string,
     * otherwise returns an empty string.
     *
     * This is used by methods in struts forms which are mapped to object
     * properties, where it is not guaranteed that the property will not
     * be null, and the JSP which uses the form might throw a null-pointer
     * exception when attempting to display the property.
     *
     * @param s String
     * @return s, or an empty string, if s was null.
     */
    public static String nvl(String s) {
		// [[
		String result = null;
		Object[][] params = {{"s", s}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(StringUtils.class, "nvl", params);
		}
		// ]]
		try {
	        if (s != null) {
	            return result = s;
	        }
	        return result = "";
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
     * Returns a long value for a string. Returns 0 if the string is null or
     * empty. Throws a <code>NumberFormatException</code> if the string is not
     * empty, but fails to parse to a long.
     *
     * @param s String a string of decimal digits
     * @return long
     * @throws NumberFormatException
     */
    public static long toLongValue(String s) {
		// [[
		long result = 0L;
		Object[][] params = {{"s", s}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(StringUtils.class, "toLongValue", params);
		}
		// ]]
		try {
	        if (s == null || s.length() == 0) {
	            return result = 0L;
	        }
	        return result = Long.parseLong(s);
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
     * Returns a int value for a string. Returns 0 if the string is null or
     * empty. Throws a <code>NumberFormatException</code> if the string is not
     * empty, but fails to parse to a long.
     *
     * @param s String a string of decimal digits
     * @return int
     * @throws NumberFormatException
     */
    public static int toIntValue(String s) {
		// [[
		int result = 0;
		Object[][] params = {{"s", s}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(StringUtils.class, "toIntValue", params);
		}
		// ]]
		try {
	        if (s == null || s.length() == 0) {
	            return result = 0;
	        }
	        return result = Integer.parseInt(s);
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
     * Returns a string of the specified
     * number, or an empty string, if <code>null</code> is passed in.
     *
     * @param number Number
     * @return String
     */
    public static String toString(Number number) {
        if (number != null) {
        	if (number instanceof BigDecimal) {
        		// overload resolution is based on the static type, so having a toString(BigDecimal)
        		// would not get cases where a BigDecimal is assigned to a Number variable
        		// and having a toString for each number subclass (but not for Number) is too much code
        		return ((BigDecimal) number).toPlainString();
        	}
            return number.toString();
        }
        return "";
    }
    
    /**
     * Returns a string of the specified
     * number, or <code>null</code>, if <code>null</code> is passed in
     * (since booleans are not used for direct user display).
     *
     * @param bool Boolean
     * @return String
     */
    public static String toString(Boolean bool) {
        if (bool != null) {
            return bool.toString();
        }
        return null;
    }

    /**
     * converts first character to uppercase, e.g. adressen -> Adressen.
     * @param inputString
     */
    public static String toUpperFirstChar(String inputString) {
		// [[
		String result = null;
		Object[][] params = {{"inputString", inputString}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(StringUtils.class, "toUpperFirstChar", params);
		}
		// ]]
		try {
			
	        String retString = inputString;
	        char cLower = inputString.charAt(0);
	        if (Character.isLowerCase(cLower)) {
	            char cUpper = Character.toUpperCase(cLower);
	            char[] testChars = inputString.toCharArray();
	            testChars[0] = cUpper;
	            retString = new String(testChars);
	        }
	        return result = retString;
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
	 * Joins the elements of the provided Collection into a single String containing the provided elements.
	 * 
	 * @param collection
	 * @param separator
	 * @return
	 */
	public static String join(Collection collection, String separator) {
		return org.apache.commons.lang.StringUtils.join(collection, separator);
	}

	public static String generateErrorId() {
		// [[
		String result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(StringUtils.class, "generateErrorId");
		}
		// ]]
		try {
			String res;
			synchronized (random) {
				String a = (char) ('a' + random.nextInt(26)) + ""
						+ (char) ('a' + random.nextInt(26)) + ""
						+ (char) ('a' + random.nextInt(26)) + ""
						+ (char) ('a' + random.nextInt(26)) + ""
						+ (char) ('a' + random.nextInt(26));
				java.text.DecimalFormat df = new java.text.DecimalFormat("00000.#");
				res = a + "-" + df.format(random.nextInt(99999)) + "-"
						+ df.format(random.nextInt(99999));
			}
			return result = res;
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
	 * @adnlog.notrace
	 * @param o
	 * @return
	 */
    public static int objectSize(Object o) {
    	try {
    		ByteArrayOutputStream out = null;
    		out = new ByteArrayOutputStream();
    		ObjectOutputStream stream = new ObjectOutputStream(out);
    		stream.writeObject(o);
    		stream.flush();
    		return out.size();
    	}
    	catch (IOException e) {
    		LOG.error("exception while serializing object '" + o +  "' " + e);
    		return 0;
    	}
    }
    
	/**
	 * @param noteDelivery
	 * @param itemNoteDelivery
	 * @return
	 */
	public static String appendWithNewLine(String base, String suffix) {
		// null guards
		String checkedBase = nvl(base);
		String checkedSuffix = nvl(suffix);
		
		// get system dependent separator if needed
		String separator = checkedBase.isEmpty() || checkedSuffix.isEmpty() ? "" : String.format("%n");
		
		return checkedBase + separator + checkedSuffix;
	}
    
	/**
	 * Concatenates the input character arrays into a new one whit keeping the original order of chars
	 * @param charArrays variable number of char arrays to concatenate
	 * @return the concatenated char arrays
	 */
	public static char[] concat(char[]... charArrays) {
		StringBuilder sb = new StringBuilder();
		for (char[] charArray : charArrays) {
			if (charArray != null) {
				sb.append(charArray);
			}
		}
		
		return sb.toString().toCharArray();
	}

	/**
	 * compares two String object.
	 * 
	 * null and "" considered equals!
	 * 
	 * @param value1
	 * @param value2
	 * @return true if the two string equals
	 */
	public final static boolean areEqual(String value1, String value2) {
		final boolean debugEnabled = LOG.isDebugEnabled();

		if (debugEnabled) {
			Object[][] params = { { "value1", value1 }, { "value2", value2 } };
			LOG.entering(StringUtils.class, "areEqual", params);
		}

		boolean result = false;

		try {
			if (value1 == null || empty(value1)) {
				return result = (value2 == null || empty(value2));
			}

			return result = value1.equals(value2);
		} 
		finally {
			if (debugEnabled) {
				LOG.exiting(result);
			}
		}
	}
	
	/**
	 * Helper method for safely converting String to java.math.BigDecimal.
	 * 
	 * @param number the number as String
	 * @return the new java.math.BigDecimal instance
	 * @throws ParseException on invalid input
	 */
	public static BigDecimal toBigDecimal(String number) throws ParseException {
		if(number == null) {
			throw new IllegalArgumentException();
		}
		
		DecimalFormat decimalformat = new DecimalFormat();
		decimalformat.setParseBigDecimal(true);
		
		BigDecimal result = (BigDecimal) decimalformat.parse(number);		
		
		return result;
	}
	
    /**
     * Create a list of {@link Long} from the String parameter.
     * 
     * NOTE: the value separator inside the values parameter is <code>,</code>
     * 
     * @param values
     * @return
     */
	public static List<Long> stringToArrayList(String values) {
		// [[
		List<Long> result = null;
		Object[][] params = {{"values", values}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(StringUtils.class, "stringToArrayList", params);
		}
		// ]]
		try {
			if (StringUtils.empty(values)) {
				return result = null;
			}
			
			List<String> helperList =  Arrays.asList(values.split(","));
			
			if (helperList == null || helperList.isEmpty()) {
				return result = null;
			}
			
			result = new ArrayList<Long>();
			for (String id : helperList) {
				result.add(Long.valueOf(id));
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
	 * Create a list of {@link Integer} from the String parameter.
	 * 
	 * NOTE: the value separator inside the values parameter is <code>,</code>
	 * 
	 * @param values
	 * @return
	 */
	public static List<Integer> stringToIntegerArrayList(String values) {
		// [[
		List<Integer> result = null;
		Object[][] params = {{"values", values}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(StringUtils.class, "stringToIntegerArrayList", params);
		}
		// ]]
		try {
			if (StringUtils.empty(values)) {
				return result = null;
			}
			
			List<String> helperList =  Arrays.asList(values.split(","));
			
			if (helperList == null || helperList.isEmpty()) {
				return result = null;
			}
			
			result = new ArrayList<Integer>();
			for (String id : helperList) {
				result.add(Integer.valueOf(id));
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
	 * Removes leading zeros, but will leave one if there are only zeros in the input.
	 * E.g. 00000 will become 0.
	 * @param s
	 * @return
	 */
	public static String removeLeadingZeros(String s){
		// An explanation for the regex
		// The ^ anchor will make sure that the 0+ being matched is at the beginning of the input. 
		// The (?!$) negative lookahead ensures that not the entire string will be matched
		return s.replaceFirst("^0+(?!$)", "");
	}
	
	public static String cutStringWithDots(String string, int maxLength) {
		if (string.length() > maxLength) {
			String dots = "..";
			if (maxLength < 3) {
				dots = "";
			} else {
				maxLength -= 2;
			}
			string = string.substring(0, maxLength);
			string += dots;
		}
		return string;
	}
	
	public static int getDecimalFractionLength(final String valueStr) {
		// [[
		int result = 0;
		Object[][] params = {{"valueStr", valueStr}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(StringUtils.class, "getDecimalFractionLength", params);
		}
		// ]]
		try {
			if (valueStr != null) {
				BigDecimal bd;
				try {
					bd = new BigDecimal(valueStr);
				}
				catch (NumberFormatException e) {
					LOG.error("not a number: " + valueStr);
					return result = -1;
				}
				
				bd = bd.stripTrailingZeros();
				return result = bd.scale();
			}
			
			return result = -1;
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
	 * Formats long (min) to HHHHHHMM format
	 */
	public static String formatHreMinString(Long string) {
		// [[
		String result = null;
		Object[][] params = {{"string", string}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(StringUtils.class, "formatString", params);
		}
		// ]]
		try {
			long hours = string / 60;
			long min = string % 60;
			return result = String.format("%06d%02d", hours, min);
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
	 * Text wrapper method.
	 */
	public static List<String> wrapText(String text, int len) {
		if (text == null || len <= 0 || text.length() <= len) {
			return null;
		}
		char [] chars = text.toCharArray();
		List<String> lines = new ArrayList<String>();
		StringBuffer line = new StringBuffer();
		StringBuffer word = new StringBuffer();

		for (int i = 0; i < chars.length; i++) {
			word.append(chars[i]);

			if (chars[i] == ' ') {
				if ((len < line.length() + word.length())) {
					lines.add(line.toString());
					line.delete(0, line.length());
				}

				line.append(word);
				word.delete(0, word.length());
			}
		}

		// handle any extra chars in current word
		if (0 < word.length()) {
			if (len < (line.length() + word.length())) {
				lines.add(line.toString());
				line.delete(0, line.length());
			}
			line.append(word);
		}

		// handle extra line
		if (0 < line.length()) {
			lines.add(line.toString());
		}

		return lines;
	}
	
	public static String concatenate(String separator, String... array) {
		if(array == null || array.length == 0) {
			return org.apache.commons.lang.StringUtils.EMPTY;
		} else {
			List<String> list = new ArrayList<String>();
			for (String word : array) {
				if(org.apache.commons.lang.StringUtils.isEmpty(word)) {
					continue;
				}
				list.add(word);
			}
			return org.apache.commons.lang.StringUtils.join(list, separator);			
		}
	}

		
	public static String concatenateAndCutStringWithDots(String separator, int maxLength, String... array) {
		return cutStringWithDots(concatenate(separator, array), maxLength);
	}
}
