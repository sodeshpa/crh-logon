/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdf.templating;

/**
 * Represents a content control which contents will be repeated.
 */
public class ForeachTemplateTag extends TemplateTag {

	private final Path path;

	public ForeachTemplateTag(Path path) {
		this.path = path;
	}

	public Path getPath() {
		return path;
	}

	@Override
	public String toString() {
		return "@foreach " + path;
	}
}
