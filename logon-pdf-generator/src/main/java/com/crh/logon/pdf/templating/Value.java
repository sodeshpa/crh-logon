/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdf.templating;

import java.io.IOException;

/**
 * A value that can be included into a template.
 */
public abstract class Value {
	/**
	 * The type of the implementation/value. Something like string, image, ... 
	 */
	public abstract String getType();

	/**
	 * Converts a base64 encoded string to the corresponding binary value. Strings are not converted.
	 * @throws ParsingException 
	 */
	public static Value Deserialize(String type, String value) throws ParsingException, IOException {
		if (TextValue.Type.equals(type)) {
			return new TextValue(value);
		}
		if (ImageValue.Type.equals(type)) {
			return ImageValue.Deserialize(value);
		}
		if ((DocumentValue.Type + "-file").equals(type)) {
			return DocumentValue.Load(value);
		}
		if (DocumentValue.Type.equals(type)) {
			return DocumentValue.Deserialize(value);
		}
		throw new ParsingException("Unknown value type: " + type);
	}
}
