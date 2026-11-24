package com.crh.logon.pdf.aspose;

import com.aspose.words.DocumentVisitor;
import com.aspose.words.StructuredDocumentTag;
import com.aspose.words.VisitorAction;

/**
 * Removes all "@" content controls of a word document.
 */
class CleanVisitor extends DocumentVisitor {

	public CleanVisitor() {
	}

	@Override
	public int visitStructuredDocumentTagStart(StructuredDocumentTag sdt) {

		String tagValue = sdt.getTag();
		if (!tagValue.startsWith("@")) {
			return VisitorAction.CONTINUE;
		}

		sdt.removeSelfOnly();

		return VisitorAction.CONTINUE;
	}

}
