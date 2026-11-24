/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdf.aspose;

import com.crh.logon.pdf.templating.TemplateTag;
import com.crh.logon.pdf.templating.TextValue;
import com.crh.logon.pdf.templating.DataProvider;
import com.crh.logon.pdf.templating.ParsingException;
import com.crh.logon.pdf.templating.PathTemplateTag;

import com.aspose.words.DocumentVisitor;
import com.aspose.words.StructuredDocumentTag;
import com.aspose.words.VisitorAction;

/**
 * Visits all Content Controls and loads their value from a {@link DataProvider} into the control.
 */
class ExtractValueVisitor extends DocumentVisitor {
	private final KeyValueDataProvider dataProvider;

	public ExtractValueVisitor() {
		this.dataProvider = new KeyValueDataProvider();
	}

	@Override
	public int visitStructuredDocumentTagStart(StructuredDocumentTag sdt)
			throws ParsingException {
		String tagValue = sdt.getTag();
		if (!tagValue.startsWith("@")) {
			return VisitorAction.CONTINUE;
		}

		TemplateTag tag = TemplateTag.Parse(tagValue);
		if (!(tag instanceof PathTemplateTag)) {
			return VisitorAction.CONTINUE;
		}

		extractValue(sdt, (PathTemplateTag) tag);
		return VisitorAction.CONTINUE;
	}

	private void extractValue(StructuredDocumentTag node, PathTemplateTag tag) {
		String value = node.getText();
		dataProvider.put(tag.getPath(), new TextValue(value));
	}

	public KeyValueDataProvider getDataProvider() {
		return dataProvider;
	}
}
