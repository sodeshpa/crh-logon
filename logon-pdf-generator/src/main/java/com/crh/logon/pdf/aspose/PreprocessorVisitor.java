/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdf.aspose;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.aspose.words.Body;
import com.aspose.words.CompositeNode;
import com.aspose.words.Document;
import com.aspose.words.DocumentBase;
import com.aspose.words.DocumentVisitor;
import com.aspose.words.ImportFormatMode;
import com.aspose.words.Node;
import com.aspose.words.NodeImporter;
import com.aspose.words.NodeType;
import com.aspose.words.SdtType;
import com.aspose.words.StructuredDocumentTag;
import com.aspose.words.VisitorAction;
import com.crh.logon.pdf.templating.ForeachTemplateTag;
import com.crh.logon.pdf.templating.TemplateTag;
import com.crh.logon.pdf.templating.Condition;
import com.crh.logon.pdf.templating.ConditionTemplateTag;
import com.crh.logon.pdf.templating.DataProvider;
import com.crh.logon.pdf.templating.DocumentValue;
import com.crh.logon.pdf.templating.IncludeTemplateTag;
import com.crh.logon.pdf.templating.ParsingException;
import com.crh.logon.pdf.templating.Path;

import java.io.ByteArrayInputStream;

public class PreprocessorVisitor extends BaseVisitor {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PreprocessorVisitor.class);

	public PreprocessorVisitor(DataProvider dataProvider) {
		super(dataProvider);
	}

	@Override
	public int visitStructuredDocumentTagStart(StructuredDocumentTag sdt)
			throws ParsingException, ProcessingException {
		// [[
		int result = VisitorAction.STOP;
		Object[][] params = {{"sdt", sdt}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "visitStructuredDocumentTagStart", params);
		}
		// ]]
		try {
			super.visitStructuredDocumentTagStart(sdt);

			return result = preprocess(sdt);
		}
		catch (ParsingException exc) {
			throw exc;
		}
		catch (ProcessingException exc) {
			throw exc;
		}
		catch (Exception exc) {
			throw new ProcessingException("Processing node " + sdt.getTag(), exc);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private int preprocess(StructuredDocumentTag sdt) throws Exception {
		// [[
		int result = VisitorAction.STOP;
		Object[][] params = {{"sdt", sdt}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "preprocess", params);
		}
		// ]]
		try {
			String tagValue = sdt.getTag();
			if (logDebugEnabled) {
				LOG.debug("Tag.value: " + tagValue);
			}

			if (tagValue.startsWith("@")) {
				boolean isTreeModified = false;

				TemplateTag tag = TemplateTag.Parse(tagValue);
				if (tag instanceof ForeachTemplateTag) {
					isTreeModified = processForeach(sdt, (ForeachTemplateTag) tag);
				}
				else if (tag instanceof IncludeTemplateTag) {
					isTreeModified = processInclude(sdt, (IncludeTemplateTag) tag);
				}
				else if (tag instanceof ConditionTemplateTag) {
					isTreeModified = processCondition(sdt, (ConditionTemplateTag) tag);
				}

				if (isTreeModified) {
					markTreeModified();
				}

				if (logDebugEnabled) {
					LOG.debug(String.format("Template: %s, isTreeModified: %s", tag, isTreeModified));
				}
			}

			return result = VisitorAction.CONTINUE;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private boolean processCondition(StructuredDocumentTag node, ConditionTemplateTag tag) throws Exception {
		// [[
		boolean result = false;
		Object[][] params = {{"node", node},
							 {"tag", tag}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "processCondition", params);
		}
		// ]]
		try {
			Condition condition = tag.getCondition();
			boolean condValue = condition.IsTrue(dataProvider);

			if (logDebugEnabled) {
				LOG.debug(String.format("%s => %s", tag, condValue));
			}

			if (condValue) {
				node.removeSelfOnly();
				result = true;
			}
			else {
				result = removeSDTAndFollowingNewline(node);
			}

			return result;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private boolean processForeach(StructuredDocumentTag node, ForeachTemplateTag tag) throws Exception {
		// [[
		boolean result = false;
		Object[][] params = {{"node", node},
							 {"tag", tag}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "processForeach", params);
		}
		// ]]
		try {
			int itemCount = dataProvider.count(tag.getPath());
			if (logDebugEnabled) {
				LOG.debug(String.format("Found %s item(s) for path '%s'", itemCount, tag.getPath()));
			}

			CompositeNode container = node.getParentNode();

			Node refChild = node;

			for (int index = 0; index < itemCount; index++) {
				Path indexedPath = Path.parse(String.format("%s[%s]", tag.getPath(), index));
				if (logDebugEnabled) {
					LOG.debug(String.format("Adapting path %s => %s", tag.getPath(), indexedPath));
				}

				DocumentVisitor adaptPathVisitor = new AdaptPathVisitor(tag.getPath(), indexedPath);

				Node[] childNodes = node.getChildNodes().toArray();
				for (Node child : childNodes) {
					Node clone = child.deepClone(true);

					// copy all contents
					container.insertAfter(clone, refChild);

					// change paths of nested controls (make them indexed).
					clone.accept(adaptPathVisitor);

					refChild = clone;
				}
			}

			// Remove SDT from document.
			node.remove();

			return result = (refChild == node);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private boolean processInclude(StructuredDocumentTag node, IncludeTemplateTag tag) throws Exception {
		//[[
		boolean result = false;
		Object[][] params = {{"node", node},
							 {"tag", tag}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "processInclude", params);
		}
		// ]]
		try {
			if (node.getSdtType() != SdtType.RICH_TEXT) {
				throw new ProcessingException(String.format(
						"The template references the value of key '%s' "
								+ "into a Content Control of type '%s' "
								+ "instead of a Rich Text Content Control",
						tag.getPath(), node.getSdtType()));
			}

			DocumentBase doc = node.getDocument();

			DocumentValue value = dataProvider.getInclude(tag.getPath());
			ByteArrayInputStream stream = new ByteArrayInputStream(value.getBytes());

			// XXX: consider caching the document for improved performance.
			Document source = new Document(stream);
			NodeImporter importer = new NodeImporter(source, doc, ImportFormatMode.KEEP_SOURCE_FORMATTING);

			// Get content of existing template. Ignore header/footer. Only first section is imported.
			Node sourceBody = source.getChild(NodeType.BODY, 0, true);
			Body body = (Body) importer.importNode(sourceBody, true);

			// remove existing content from Content Control
			CompositeNode container = node.getParentNode();

			// copy to local list because the getChildNodes list is updated on-the-fly
			Node refChild = node;
			for (Node child : body.getChildNodes().toArray()) {
				container.insertAfter(child, refChild);
				refChild = child;
			}

			removeSDTAndFollowingNewline(node);

			return result = true;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}
}
