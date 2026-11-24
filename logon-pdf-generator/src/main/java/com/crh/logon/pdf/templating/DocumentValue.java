/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdf.templating;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import ch.nevis.jcan.commons.util.Base64;
import org.apache.commons.io.IOUtils;


/**
 * A whole word document that can be included into a second document.
 */
public class DocumentValue extends Value {
	public static final String Type = "document";

	private final byte[] value;

	public DocumentValue(byte[] value) {
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
	public static DocumentValue Deserialize(String value) throws ParsingException {
		byte[] bytes = Base64.decode(value);
		return new DocumentValue(bytes);
	}

	/**
	 * Load from file.
	 * @throws ParsingException 
	 */
	public static DocumentValue Load(String filePath) throws IOException {
		FileInputStream stream = new FileInputStream(filePath);
		return Load(stream);
	}

	public static DocumentValue Load(InputStream stream) throws IOException {
		byte[] bytes = IOUtils.toByteArray(stream);
		return new DocumentValue(bytes);
	}
}
