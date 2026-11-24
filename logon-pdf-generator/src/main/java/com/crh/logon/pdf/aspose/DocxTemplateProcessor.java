/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdf.aspose;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.aspose.words.CompositeNode;
import com.aspose.words.Document;
import com.aspose.words.DocumentVisitor;
import com.aspose.words.Node;
import com.aspose.words.Run;
import com.crh.logon.pdf.templating.DataProvider;
import com.crh.logon.pdf.templating.ParsingException;

/**
 * Main entry point to template processing.
 */
public class DocxTemplateProcessor {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DocxTemplateProcessor.class);

	private final DataProvider dataProvider;

	public DocxTemplateProcessor(DataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	public void process(Document document) throws ParsingException, ProcessingException {
		// [[
		Object[][] params = {{"document", document}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "process", params);
		}
		// ]]
		try {
			preprocess(document);
			updateValues(document);
		}
		catch (ParsingException e) {
			LOG.error("Parsing document failed", e);
			throw e;
		}
		catch (ProcessingException e) {
			LOG.error("Parsing document failed", e);
			throw e;
		}
		catch (Exception e) {
			LOG.error("Parsing document failed", e);
			throw new ProcessingException(e);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	private void preprocess(Document document) throws Exception {
		// [[
		Object[][] params = {{"document", document}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "preprocess", params);
		}
		// ]]
		try {
			PreprocessorVisitor visitor = new PreprocessorVisitor(dataProvider);
			visitor.visit(document);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public void updateValues(Document document) throws Exception {
		// [[
		Object[][] params = {{"document", document}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateValues", params);
		}
		// ]]
		try {
			UpdateValueVisitor visitor = new UpdateValueVisitor(dataProvider);
			visitor.visit(document);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public static DataProvider extract(Document document) throws ParsingException, ProcessingException {
		ExtractValueVisitor visitor = new ExtractValueVisitor();
		try {
			document.accept(visitor);
			return visitor.getDataProvider();
		}
		catch (ParsingException e) {
			throw e;
		}
		catch (ProcessingException e) {
			throw e;
		}
		catch (Exception e) {
			throw new ProcessingException(e);
		}
	}

	public void clean(Document document) throws ParsingException,
			ProcessingException {
		DocumentVisitor visitor = new CleanVisitor();
		try {
			document.accept(visitor);
		}
		catch (ParsingException e) {
			throw e;
		}
		catch (ProcessingException e) {
			throw e;
		}
		catch (Exception e) {
			throw new ProcessingException(e);
		}
	}

	public static void validate(Document document) throws ParsingException,
			ProcessingException {
		DocumentVisitor visitor = new ValidationVisitor();
		try {
			document.accept(visitor);
		}
		catch (ParsingException e) {
			throw e;
		}
		catch (ProcessingException e) {
			throw e;
		}
		catch (Exception e) {
			throw new ProcessingException(e);
		}
	}

	public void dump(Node node, String indent) {
		if (node instanceof Run) {
			System.out.println(indent + "Run: " + node.getText());
		}
		else {
			System.out.println(indent + node);
		}
		if (!node.isComposite()) {
			return;
		}
		for (Object child : ((CompositeNode<?>) node).getChildNodes()) {
			dump((Node) child, indent + "  ");
		}
	}

}
