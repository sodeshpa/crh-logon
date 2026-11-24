package com.crh.logon.pdf.aspose;

import java.util.ArrayList;

import com.crh.logon.pdf.templating.ForeachTemplateTag;
import com.crh.logon.pdf.templating.Path;
import com.crh.logon.pdf.templating.TemplateTag;
import com.crh.logon.pdf.templating.DataProvider;
import com.crh.logon.pdf.templating.ParsingException;

import com.aspose.words.CompositeNode;
import com.aspose.words.DocumentVisitor;
import com.aspose.words.Node;
import com.aspose.words.StructuredDocumentTag;
import com.aspose.words.VisitorAction;

/**
 * Expands all ForEach content controls of a word document.
 */
class ForeachVisitor extends DocumentVisitor {
	private ArrayList<StructuredDocumentTag> path = new ArrayList<StructuredDocumentTag>();
	private final DataProvider dataProvider;

	public ForeachVisitor(DataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	@Override
	public int visitStructuredDocumentTagStart(StructuredDocumentTag sdt)
			throws ParsingException, ProcessingException {
		path.add(sdt);

		String tagValue = sdt.getTag();
		if (!tagValue.startsWith("@")) {
			return VisitorAction.CONTINUE;
		}

		TemplateTag tag = TemplateTag.Parse(tagValue);
		if (tag instanceof ForeachTemplateTag) {
			processForeach(sdt, (ForeachTemplateTag) tag);
		}

		return VisitorAction.CONTINUE;
	}

	public int visitStructuredDocumentTagEnd(StructuredDocumentTag sdt) {
		if (!path.remove(sdt)) {
			throw new RuntimeException(
					"Structured document tags are not balanced. This is impossible!");
		}

		return VisitorAction.CONTINUE;
	}

	private void processForeach(StructuredDocumentTag node,
			ForeachTemplateTag tag) throws ParsingException,
			ProcessingException {
		int itemCount = dataProvider.count(tag.getPath());

		CompositeNode<?> container = node.getParentNode();

		Node[] childNodes = node.getChildNodes().toArray();
		Node refChild = node;

		for (int index = 0; index < itemCount; index++) {
			Path indexedPath = Path.parse(String.format("%s[%s]",
					tag.getPath(), index));
			DocumentVisitor adaptPathVisitor = new AdaptPathVisitor(
					tag.getPath(), indexedPath);

			// StructuredDocumentTag clone = (StructuredDocumentTag) node
			// .deepClone(true);

			for (Node child : childNodes) {
				Node clone = child.deepClone(true);

				// copy all contents
				container.insertAfter(clone, refChild);

				// change paths of nested controls (make them indexed).
				try {
					clone.accept(adaptPathVisitor);
				} catch (ProcessingException e) {
					throw e;
				} catch (ParsingException e) {
					throw e;
				} catch (Exception e) {
					throw new ProcessingException(e);
				}

				refChild = clone;
			}

		}

		// Remove SDT from document.
		node.remove();
	}
}
