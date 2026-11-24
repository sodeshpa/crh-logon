/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdf.aspose;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.aspose.words.DocumentBase;
import com.aspose.words.DrawingML;
import com.aspose.words.MarkupLevel;
import com.aspose.words.NodeType;
import com.aspose.words.Paragraph;
import com.aspose.words.Run;
import com.aspose.words.SdtType;
import com.aspose.words.StructuredDocumentTag;
import com.aspose.words.VisitorAction;
import com.crh.logon.pdf.templating.ImageValue;
import com.crh.logon.pdf.templating.TemplateTag;
import com.crh.logon.pdf.templating.DataProvider;
import com.crh.logon.pdf.templating.ParsingException;
import com.crh.logon.pdf.templating.Path;
import com.crh.logon.pdf.templating.PathTemplateTag;
import com.crh.logon.pdf.templating.TextValue;
import com.crh.logon.pdf.templating.Value;

class UpdateValueVisitor extends BaseVisitor {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(UpdateValueVisitor.class);

	public UpdateValueVisitor(DataProvider dataProvider) {
		super(dataProvider);
	}

	@Override
	public int visitStructuredDocumentTagStart(StructuredDocumentTag sdt) throws ParsingException, ProcessingException {
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

			process(sdt);
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

	private void process(StructuredDocumentTag sdt) throws ParsingException, ProcessingException {
		// [[
		Object[][] params = {{"sdt", sdt}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "process", params);
		}
		// ]]
		try {
			String tagValue = sdt.getTag();
			if (logDebugEnabled) {
				LOG.debug(String.format("Processing %s", tagValue));
			}

			if (!tagValue.startsWith("@")) {
				return;
			}

			TemplateTag tag = TemplateTag.Parse(tagValue);
			if (tag instanceof PathTemplateTag) {
				updateValue(sdt, ((PathTemplateTag) tag).getPath());
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	private void updateValue(StructuredDocumentTag node, Path path) throws ProcessingException {
		// [[
		Object[][] params = {{"node", node},
							 {"path", path}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateValue", params);
		}
		// ]]
		try {
			Value value = dataProvider.getValue(path);
			if (logDebugEnabled) {
				LOG.debug(String.format("%s => %s",path, value));
			}

			if (value instanceof TextValue) {
				updateTextValue(node, (TextValue) value, path);
			}
			else if (value instanceof ImageValue) {
				updateImageValue(node, (ImageValue) value, path);
			}
			else if (value == null) {
				updateMissingValue(node, path);
			}
			else {
				throw new ProcessingException(
						String.format("Node: %s, Path: %s => Unsupported value type: %s [value: %s]",
									  node.getTag(), path, value.getType(), value));
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	private void updateMissingValue(StructuredDocumentTag node, Path path) {
		// TODO 2013-10-25/tito: Make this configurable via some sort of Options class

		if (LOG.isDebugEnabled()) {
			LOG.debug("Removing node " + node + " since no value given for " + path);
		}

		if (removeSDTAndFollowingNewline(node)) {
			markTreeModified();
		}
	}

	private void updateImageValue(StructuredDocumentTag node, ImageValue value, Path path) throws ProcessingException {
		// [[
		Object[][] params = {{"node", node},
							 {"path", path}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateImageValue", params);
		}
		// ]]
		try {
			if (node.getSdtType() != SdtType.PICTURE) {
				throw new ProcessingException(
						String.format("Node: %s, Path: %s: "
											  + "Template references a Content Control of type '%s' "
											  + " instead of a Picture Content Control",
									  node.getTag(), path, node.getSdtType()));
			}

			// TODO check if child really exists
			DrawingML child = (DrawingML) node.getChild(NodeType.DRAWING_ML, 0, true);
			try {
				child.getImageData().setImageBytes(value.getBytes());
			}
			catch (Exception e) {
				LOG.error(String.format("%s %s: Loading image failed", node.getTag(), path), e);
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	/**
	 * Replaces the content of a "Text Content Control" or "Rich Text Content Control" with a string.
	 */
	private void updateTextValue(StructuredDocumentTag node, TextValue value, Path path) throws ProcessingException {
		// [[
		Object[][] params = {{"node", node},
							 {"path", path}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateTextValue", params);
		}
		// ]]
		try {
			if (node.getSdtType() != SdtType.PLAIN_TEXT && node.getSdtType() != SdtType.RICH_TEXT) {
				throw new ProcessingException(
						String.format("Node: %s, Path: %s : "
											  + "Template references a Content Control of type '%s' instead "
											  + "of a Text or Rich Text Content Control",
									  node.getTag(), path, node.getSdtType()));
			}

			DocumentBase doc = node.getDocument();
			Run run = new Run(doc, value.getText());

			String styleName = node.getContentsFont().getStyleName();
			run.getFont().setStyleName(styleName);

			if (node.getLevel() == MarkupLevel.BLOCK) {
				Paragraph paragraph = new Paragraph(doc);
				paragraph.appendChild(run);

				node.removeAllChildren();
				node.appendChild(paragraph);
			}
			else if (node.getLevel() == MarkupLevel.INLINE) {
				node.removeAllChildren();
				node.appendChild(run);
			}
			else {
				throw new ProcessingException(
						String.format("Node: %s, Path: %s : "
											  + "Template references a Text Content Control with an "
											  + "unsupported markup level %s. "
											  + "Only BLOCK or INLINE are currently supported",
									  node.getTag(), path, node.getLevel()));
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}
}
