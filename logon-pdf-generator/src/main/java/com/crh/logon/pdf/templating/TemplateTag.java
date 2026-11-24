/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdf.templating;

/**
 * Represents the parsed 'tag' property of a Microsoft Word 2010+ content control. 
 */
public abstract class TemplateTag {
	private static final String TAG_FOREACH = "@foreach ";
	private static final String TAG_IF		= "@if ";
	private static final String TAG_IFNOT	= "@if-not ";
	private static final String TAG_INCLUDE	= "@include ";

	public static TemplateTag Parse(String tag) throws ParsingException {
		String lcTag = tag.toLowerCase();

		if (lcTag.startsWith(TAG_FOREACH)) {
			String path = tag.substring(TAG_FOREACH.length());
			return new ForeachTemplateTag(Path.parse(path));
		}
		else if (lcTag.startsWith(TAG_IF)) {
			String path = tag.substring(TAG_IF.length());
			return new ConditionTemplateTag(Path.parse(path), false);
		}
		else if (lcTag.startsWith(TAG_IFNOT)) {
			String path = tag.substring(TAG_IFNOT.length());
			return new ConditionTemplateTag(Path.parse(path), true);
		}
		else if (lcTag.startsWith(TAG_INCLUDE)) {
			String path = tag.substring(TAG_INCLUDE.length());
			return new IncludeTemplateTag(Path.parse(path));
		}
		else if (lcTag.startsWith("@")){
			return new PathTemplateTag(Path.parse(tag.substring(1)));
		}

		throw new ParsingException("Cannot parse " + tag);
	}	
}
