/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdf.templating;

/**
 * A simple string value.
 */
public class TextValue extends Value {
	public static final String Type = "text";

	private final String value;

	public TextValue(String value) {
		if (value == null) {
			throw new IllegalArgumentException("'value' can not be null.");
		}
		this.value = value;
	}

	public String getText() {
		return value;
	}

	@Override
	public String getType() {
		return Type;
	}

	@Override
	public String toString() {
		return this.value;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof TextValue)) {
			return false;
		}

		TextValue textObj = (TextValue) obj;
		return value.equals(textObj.getText());
	}
}
