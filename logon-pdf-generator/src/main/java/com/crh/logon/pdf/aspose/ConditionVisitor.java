/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdf.aspose;

import com.aspose.words.StructuredDocumentTag;
import com.aspose.words.VisitorAction;
import com.crh.logon.pdf.templating.TemplateTag;
import com.crh.logon.pdf.templating.Condition;
import com.crh.logon.pdf.templating.ConditionTemplateTag;
import com.crh.logon.pdf.templating.DataProvider;
import com.crh.logon.pdf.templating.ParsingException;

/**
 * Verifies and removes or keeps all Condition content controls of a word
 * document.
 */
class ConditionVisitor extends BaseVisitor {
	public ConditionVisitor(DataProvider dataProvider) {
		super(dataProvider);
	}

	@Override
	public int visitStructuredDocumentTagStart(StructuredDocumentTag sdt)
			throws ParsingException {

		String tagValue = sdt.getTag();
		if (!tagValue.startsWith("@")) {
			return VisitorAction.CONTINUE;
		}

		TemplateTag tag = TemplateTag.Parse(tagValue);
		if (tag instanceof ConditionTemplateTag) {
			processCondition(sdt, (ConditionTemplateTag) tag);
			markTreeModified();
		}

		return VisitorAction.CONTINUE;
	}

	private void processCondition(StructuredDocumentTag node,
								  ConditionTemplateTag tag) throws ParsingException {

		Condition condition = tag.getCondition();
		if (condition.IsTrue(dataProvider)) {
			node.removeSelfOnly();
		}
		else {
			node.remove();
		}
	}
}
