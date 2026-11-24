/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdf.templating;

/**
 * {@link Path} with additional attributes. 
 */
public class ExtendedPath {
	private final Path path;
	private final String valueType;

	public ExtendedPath(Path path, String valueType) {
		this.path = path;
		this.valueType = valueType;		
	}

	public Path getPath() {
		return path;
	}

	/**
	 * {@link Value.getType}
	 */
	public String getValueType() {
		return valueType;
	}
}
