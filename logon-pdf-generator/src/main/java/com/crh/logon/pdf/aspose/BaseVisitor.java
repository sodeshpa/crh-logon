/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdf.aspose;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.aspose.words.Document;
import com.aspose.words.DocumentVisitor;
import com.aspose.words.Node;
import com.aspose.words.StructuredDocumentTag;
import com.aspose.words.VisitorAction;
import com.crh.logon.pdf.templating.DataProvider;
import com.crh.logon.pdf.templating.ParsingException;

import java.util.ArrayList;

public class BaseVisitor extends DocumentVisitor {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(BaseVisitor.class);

	private ArrayList<StructuredDocumentTag> path = new ArrayList<StructuredDocumentTag>();
	protected final DataProvider dataProvider;
	protected boolean modifiedTree;

	protected BaseVisitor(DataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	@Override
	public int visitDocumentStart(com.aspose.words.Document doc) throws java.lang.Exception {
		modifiedTree = false;
		return super.visitDocumentStart(doc);
	}

	@Override
	public int visitStructuredDocumentTagStart(StructuredDocumentTag sdt) throws ParsingException, ProcessingException {
		path.add(sdt);
		return VisitorAction.CONTINUE;
	}

	@Override
	public int visitStructuredDocumentTagEnd(StructuredDocumentTag sdt) {
		if (!path.remove(sdt)) {
			throw new RuntimeException(
					"Structured document tags are not balanced. This is impossible! Tag: " + sdt.getTag());
		}
		return VisitorAction.CONTINUE;
	}

	public void visit(Document document) throws Exception {
		// [[
		Object[][] params = {{"document", document}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "visit", params);
		}
		// ]]
		try {
			do {
				document.accept(this);
			} while (isTreeModified());
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	protected boolean isTreeModified() {
		return modifiedTree;
	}

	protected void markTreeModified() {
		modifiedTree = true;
	}

	protected boolean removeSDTAndFollowingNewline(Node node) {
		if (node.getParentNode() == null) {
			return false;
		}

		Node sibling = node.getNextSibling();
		node.remove();

		return true;
	}

}
