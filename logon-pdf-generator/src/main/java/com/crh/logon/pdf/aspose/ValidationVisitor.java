package com.crh.logon.pdf.aspose;

import com.crh.logon.pdf.templating.TemplateTag;
import com.crh.logon.pdf.templating.ParsingException;

import com.aspose.words.DocumentVisitor;
import com.aspose.words.StructuredDocumentTag;
import com.aspose.words.VisitorAction;

/**
 * Checks if all Content Controls' tags can be parsed
 */
class ValidationVisitor extends DocumentVisitor {

	@Override
	public int visitStructuredDocumentTagStart(StructuredDocumentTag sdt)
			throws ParsingException {

		String tagValue = sdt.getTag();
		if (!tagValue.startsWith("@")) {
			return VisitorAction.CONTINUE;
		}

		TemplateTag.Parse(tagValue);
		return VisitorAction.CONTINUE;
	}
}
