/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdf.templating;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Identifier {
	private final static Pattern identifierPattern = Pattern.compile("(\\w+)(?:\\[(-?\\d+)\\])?");

	private final String key;

	private int index;
	private boolean indexed = false;


	public Identifier(String key) {
		if (!key.matches("\\w+")) {
			throw new IllegalArgumentException(
					"Key can only contain normal characters. '" + key
							+ "' contains other stuff.");
		}
		this.key = key;
	}

	public Identifier(String key, int index) {
		this(key);
		this.index = index;
		this.indexed = true;
	}

	public String getKey() {
		return key;
	}

	public boolean hasIndex() {
		return indexed;
	}

	public int getIndex() {
		if (!hasIndex()) {
			throw new UnsupportedOperationException();
		}
		return index;
	}

	@Override
	public String toString() {
		if (indexed) {
			return String.format("%s[%s]", this.getKey(), this.getIndex());
		}
		else {
			return getKey();
		}
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !other.getClass().equals(Identifier.class)) {
			return false;
		}

		Identifier identifier = (Identifier) other;

		return this.key.equals(identifier.key)
				&& this.index == identifier.index
				&& this.indexed == identifier.indexed;
	}

	@Override
	public int hashCode() {
		return index + key.hashCode() * 31;
	}

	public static Identifier parse(String part) throws ParsingException {
		Matcher matcher = identifierPattern.matcher(part);
		if (!matcher.matches()) {
			throw new ParsingException("String '" + part + "' is not a valid identifier.");
		}

		String key = matcher.group(1);
		String index = matcher.group(2);
		if (index != null) {
			return new Identifier(key, Integer.parseInt(index));
		}
		else {
			return new Identifier(key);
		}
	}

}
