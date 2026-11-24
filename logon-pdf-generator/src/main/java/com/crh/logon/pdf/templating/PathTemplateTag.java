/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdf.templating;

/**
 * Represents a content control that references a value.
 */
public class PathTemplateTag extends TemplateTag {

	private final Path path;

	public PathTemplateTag(Path path) {
		if (path == null) {
			throw new IllegalArgumentException("Argument is null: path");
		}
		this.path = path;
	}

	public Path getPath() {
		return path;
	}

	@Override
	public String toString() {
		return "@" + path;
	}

}
