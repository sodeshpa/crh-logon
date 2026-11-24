/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdftool.renderer;

import java.util.ArrayList;
import java.util.List;

import com.crh.logon.pdftool.PdfDocumentWrapper;
import com.crh.logon.pdftool.layout.PdfBorderLayout;
import com.crh.logon.pdftool.objects.AbstractPdfObject;
import com.crh.logon.pdftool.objects.PdfObject;

/**
 * Class responsible for rendering the pdf document.
 * 
 * @author AdNovum Informatik AG
 */
public abstract class AbstractPdfRenderer {
	
	protected List<CustomPrintSection> customSections = new ArrayList<CustomPrintSection>();
	
	/**
	 * Adds a custom section. Used to do very renderer specific tasks (images, lines etc..) 
	 * @param section
	 */
	public void addCustomSection(CustomPrintSection section) {
		customSections.add(section);
	}
	
	/**
	 * Creates a root object that contains all the contents from the document, calls {@link AbstractPdfRenderer#displayObject(PdfObject)} 
	 * to build for the whole document and finally, {@link AbstractPdfRenderer#print()} is called to do the rendering. This renders a single
	 * page, for a new page create another {@link PdfDocumentWrapper} instance.
	 * @param document
	 */
	public void renderPage(PdfDocumentWrapper document) {
		// get the layout and the content
		PdfBorderLayout layout = document.getLayout();
		List<PdfObject> content = document.getContent();
		// create a root object
		PdfObject rootObject = new AbstractPdfObject() {};
		rootObject.getChildren().addAll(content);
		rootObject.setLayout(layout);
		// call the render function, which recursively renders the whole document
		displayObject(rootObject);
		print();
	}

	/**
	 * Initializing the document. Implementations are responsible to prepare the document from the {@link PdfDocumentWrapper} object.
	 * @param document
	 */
	public abstract void initializeDocument(PdfDocumentWrapper document);
	
	/**
	 * Display the root object. In the abstract superclass, this will be called only once,
	 * for the root element.
	 * @param object
	 */
	protected abstract void displayObject(PdfObject object);
	
	
	/**
	 * Renders a page.
	 */
	protected abstract void print();
	
	/**
	 * Closes the rendering process. Should be called after all the pages had been rendered.
	 */
	public abstract void close();

}
