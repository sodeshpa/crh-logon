/*
* Author            : AdNovum Informatik AG
* Version Number    : $Revision$
* Date of last edit : $Date$
*/

package com.crh.logon.pdf.templating;

import ch.nevis.jcan.commons.util.Base64;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;

/**
 * A binary image.
 */
public class ImageValue extends Value {
	public static final String Type = "image";
	
	private final byte[] value;

	public ImageValue(byte[] value) {
		this.value = value;
	}
	
	public byte[] getBytes() {
		return value;
	}

	@Override
	public String getType() {
		return Type;
	}

	/**
	 * Deserialize a base64-encoded image.
	 * @throws ParsingException 
	 */
	public static Value Deserialize(String value) throws ParsingException {
		byte[] bytes = Base64.decode(value);
		return new ImageValue(bytes);
	}

	public static Value Load(InputStream stream) throws ParsingException {
		try {
			byte[] bytes = IOUtils.toByteArray(stream);
			return new ImageValue(bytes);
		}
		catch (Exception e) {
			throw new ParsingException("Loading stream failed", e);
		}
	}
}
