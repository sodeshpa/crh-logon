/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdf.templating;

public class IncludeTemplateTag extends TemplateTag {
	private final Path path;

	public IncludeTemplateTag(Path path) {
		this.path = path;
	}

	public Path getPath() {
		return path;
	}

	@Override
	public String toString() {
		return "@include " + path;
	}
}
