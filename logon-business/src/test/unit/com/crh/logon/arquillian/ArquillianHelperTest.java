/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.arquillian;

import java.util.Arrays;

import junit.framework.Assert;

import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.junit.Test;

/**
 * @author AdNovum Informatik AG
 */
public class ArquillianHelperTest {

	public static final boolean SHOULD_INCLUDE = true;
	public static final boolean SHOULD_NOT_INCLUDE = false;

	private static final String PLACE_HOLDER = "$"; 

	private static final String[] NAMES_SHOULD_BE_INCLUDED = new String[] {
		createClassNameUsingPattern("$Service", ArquillianHelper.TEST_CLASS_NAME_TAG),
		createClassNameUsingPattern("com/crh/logon/business/services/interfaces/$ServiceClass", ArquillianHelper.TEST_CLASS_NAME_TAG),
		createClassNameUsingPattern("Logon$SimulationException", ArquillianHelper.TEST_CLASS_NAME_TAG)
	};

	private static final String[] NAMES_SHOULD_NOT_BE_INCLUDED = new String[] {
		createClassNameUsingPattern("My$", ArquillianHelper.TEST_CLASS_NAME_TAG),
		createClassNameUsingPattern("My$Suite", ArquillianHelper.TEST_CLASS_NAME_TAG),
		createClassNameUsingPattern("$Service$", ArquillianHelper.TEST_CLASS_NAME_TAG, ArquillianHelper.TEST_CLASS_NAME_TAG)
	};

	@Test
	public void testNamesShouldBeIncluded() {
		for (String name : NAMES_SHOULD_BE_INCLUDED) {
			Assert.assertTrue(String.format("Name [%s] should be included!", name), assertFilterIncludesName(name));
		}
	}

	@Test
	public void testNamesNotShouldBeIncluded() {
		for (String name : NAMES_SHOULD_NOT_BE_INCLUDED) {
			Assert.assertFalse(String.format("Name [%s] should not be included!", name), assertFilterIncludesName(name));
		}
	}

	@Test
	public void testClassNameCreationForSinglePlaceHolder() {

		String key = "Test";
		String body = "Service";
		String pattern = PLACE_HOLDER + body;
		String tags[] = new String[]{key};

		String result = createClassNameUsingPattern(pattern, tags);
		String expected = key+body;

		Assert.assertEquals(expected, result);

	}

	@Test
	public void testClassNameCreationForMultiplePlaceHolder() {

		String keyOne = "One";
		String keyOther = "Other";
		String body = "Service";

		String pattern = PLACE_HOLDER + body + PLACE_HOLDER;
		String tags[] = new String[]{keyOne, keyOther};

		String result = createClassNameUsingPattern(pattern, tags);
		String expected = keyOne + body + keyOther;

		Assert.assertEquals(expected, result);

	}

	private boolean assertFilterIncludesName( String name) {
		BasicPath basicPath = new BasicPath(name);
		return ArquillianHelper.TEST_FILTER.include(basicPath);
	}

	/**
	 * Returns a String where all place holders are replaced with the corresponding tag
	 * Example input:
	 *   pattern => "$SomeText$"
	 *   tags => ["Dog", "Cat"]
	 * Example output:
	 *   => "DogSomeTextCat"
	 * @param pattern - pattern which is formed using n place holder and the n tags
	 * @param tags - the replace values
	 * @return replaced string
	 */
	private static String createClassNameUsingPattern(String pattern, String... tags) {
		int indexOfPlaceHolder = pattern.indexOf(PLACE_HOLDER);
		if (indexOfPlaceHolder == -1) {
			return pattern;
		}
		else if (tags.length == 0) {
			throw new IllegalArgumentException("tags array should not be empty at this point!");
		}
		else if (tags.length == 1) {
			return pattern.replace(PLACE_HOLDER, tags[0]);
		}
		String slice = pattern.substring(0, indexOfPlaceHolder + 1);
		String replacedSlice = slice.replace(PLACE_HOLDER, tags[0]);
		String patternRest = pattern.substring(indexOfPlaceHolder + 1);
		String[] tagSubSet = Arrays.copyOfRange(tags, 1, tags.length);
		return replacedSlice + createClassNameUsingPattern(patternRest, tagSubSet);
	}	

}
