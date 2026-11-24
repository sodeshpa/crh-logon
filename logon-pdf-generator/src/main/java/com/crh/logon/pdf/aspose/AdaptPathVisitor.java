/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdf.aspose;

import com.crh.logon.pdf.templating.TemplateTag;
import com.crh.logon.pdf.templating.Condition;
import com.crh.logon.pdf.templating.ConditionTemplateTag;
import com.crh.logon.pdf.templating.ParsingException;
import com.crh.logon.pdf.templating.Path;
import com.crh.logon.pdf.templating.PathTemplateTag;

import com.aspose.words.DocumentVisitor;
import com.aspose.words.StructuredDocumentTag;
import com.aspose.words.VisitorAction;

/**
 * Replaces all occurrences of {@link originalPath} with {@link fixedPath} in
 * the tag of all nested content controls.
 */
class AdaptPathVisitor extends DocumentVisitor {
	private final Path findPath;
	private final Path replacementPath;

	public AdaptPathVisitor(Path findPath, Path replacementPath) {
		this.findPath = findPath;
		this.replacementPath = replacementPath;
	}

	@Override
	public int visitStructuredDocumentTagStart(StructuredDocumentTag sdt)
			throws ParsingException {
		String tagValue = sdt.getTag();
		if (!tagValue.startsWith("@")) {
			return VisitorAction.CONTINUE;
		}

		TemplateTag tag = TemplateTag.Parse(tagValue);
		if (tag instanceof PathTemplateTag) {
			processValueTag((PathTemplateTag) tag, sdt);
		}

		if (tag instanceof ConditionTemplateTag) {
			processConditionTag((ConditionTemplateTag) tag, sdt);
		}

		// TODO: handle nested for loops.
		return VisitorAction.CONTINUE;
	}

	private void processConditionTag(ConditionTemplateTag tag, StructuredDocumentTag sdt) {
		Condition condition = tag.getCondition();
		Path newPath = condition.getPath().replacePath(findPath, replacementPath);
		sdt.setTag(new ConditionTemplateTag(newPath, condition.isNegation()).toString());
	}

	private void processValueTag(PathTemplateTag tag, StructuredDocumentTag sdt) {
		Path path = tag.getPath().replacePath(findPath, replacementPath);
		sdt.setTag(new PathTemplateTag(path).toString());
	}
}
