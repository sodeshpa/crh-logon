package com.crh.logon.pdf.templating;

/**
 * Represents a constant string value. Immutable.
 */
public class Constant implements Term {
	private final String value;

	public Constant(String value) {
		this.value = value;
	}
	
	@Override
	public Value getValue(DataProvider dataProvider) {
		String string = value.substring(1,value.length()-1).replace("\\\\", "\\").replace("\\\"", "\"");
		return new TextValue(string);
	}

	@Override
	public String toString() {
		return this.value;
	}

	@Override
	public Term replacePath(Path find, Path replacement) {
		return this;
	}
}
