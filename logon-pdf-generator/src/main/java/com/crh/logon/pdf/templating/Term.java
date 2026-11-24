package com.crh.logon.pdf.templating;

/**
 * Immutable.
 */
public interface Term {
	/**
	 * @return Can return null.
	 */
	Value getValue(DataProvider dataProvider);

	/**
	 * Replaces the occurrence of {@code find} with {@code replacement}
	 * and returns a new instance. Returns the same instance if paths do not match.
	 */
	Term replacePath(Path find, Path replacement);
}
