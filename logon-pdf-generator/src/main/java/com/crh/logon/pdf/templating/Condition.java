/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdf.templating;

import org.apache.commons.lang.ObjectUtils;

/**
 * Represents a condition that can evaluate to true or false depending on input
 * data. Null and empty string are equal.
 */
public class Condition {
	private final Path path;
	private final boolean isNegation;

	public Condition(Path path, boolean isNegation) {
		this.path = path;
		this.isNegation = isNegation;
	}

	public boolean isNegation() {
		return isNegation;
	}

	public Path getPath() {
		return path;
	}

	public boolean IsTrue(DataProvider dataProvider) throws ParsingException {
		Value value = dataProvider.getValue(path);

		boolean bool = Boolean.parseBoolean(ObjectUtils.toString(value));
		return isNegation ? !bool : bool;
	}
}
