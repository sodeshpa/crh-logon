/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout;

import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import ch.nevis.jcan.print.base.Color;
import ch.nevis.jcan.print.base.Footer;
import ch.nevis.jcan.print.base.OutputGen;
import ch.nevis.jcan.print.base.Points;
import ch.nevis.jcan.print.base.PrinterException;
import ch.nevis.jcan.print.fonts.Font;
import ch.nevis.jcan.print.fonts.FontFactory;
import ch.nevis.jcan.print.images.Image;
import ch.nevis.jcan.print.layout.Alignment;
import ch.nevis.jcan.print.layout.Cell;
import ch.nevis.jcan.print.layout.Container;
import ch.nevis.jcan.print.layout.Paragraph;
import ch.nevis.jcan.print.layout.Table;
import ch.nevis.jcan.print.pdfgen.PdfGen;
import ch.nevis.jcan.print.psgen.PsGen;

import com.crh.logon.business.services.reporting.PDFUtils;
import com.crh.logon.business.services.utils.MultiOutputGenWrapper;
import com.crh.logon.pdftool.PdfDocumentWrapper;
import com.crh.logon.pdftool.layout.PdfBorderData;
import com.crh.logon.pdftool.layout.PdfBorderLayout;
import com.crh.logon.pdftool.objects.PdfObject;
import com.crh.logon.pdftool.objects.PdfTable;
import com.crh.logon.pdftool.objects.SimplePdfText;
import com.crh.logon.pdftool.renderer.AbstractPdfRenderer;
import com.crh.logon.pdftool.renderer.CustomPrintSection;
import com.crh.logon.pdftool.renderer.PdfFont;
import com.crh.logon.pdftool.renderer.PdfFontStore;
import com.crh.logon.persistence.dto.BlobDTO;
import com.crh.logon.utils.LogonException;

/**
 * The jcan implementation of the {@link AbstractPdfRenderer}. It handles all
 * the concrete drawing, layouting etc... For detailed description see the superclass.
 * @author AdNovum Informatik AG
 */
public class JcanPdfRenderer extends AbstractPdfRenderer {
	
	/**
	 * Used to store images. All the rendering happens when {@link AbstractPdfRenderer#renderPdfDocument(PdfDocumentWrapper)} had been called.
	 * Images must be saved and the actual render function will be called at rendering time
	 * @author AdNovum Informatik AG
	 */
	private class ImageWrapper {
		BlobDTO blobDTO;
		String imageName;
		double x;
		double y;
		double width;
		double height;
		byte[] imgData;
		
		/**
		 * @param blobDTO
		 * @param imageName
		 * @param x
		 * @param y
		 * @param width
		 * @param height
		 */
		public ImageWrapper(BlobDTO blobDTO, String imageName, double x, double y, double width,
				double height) {
			super();
			this.blobDTO = blobDTO;
			this.imageName = imageName;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.imgData = new byte[0];
		}
		
		/**
		 * @param blobDTO
		 * @param imageName
		 * @param x
		 * @param y
		 * @param width
		 * @param height
		 */
		public ImageWrapper(byte[] imgData, double x, double y, double width,
				double height) {
			super();
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.imgData = imgData;
		}
		
	}
	
	private List<ImageWrapper> images = new ArrayList<JcanPdfRenderer.ImageWrapper>();
	
	private MultiOutputGenWrapper output = null;
	
	private Stack<Table> tables = new Stack<Table>();
	
	private static Map<PdfFont, Font> fontMap;
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(JcanPdfRenderer.class);
	
	static {
		fontMap = new HashMap<PdfFont, Font>();
		fontMap.put(new PdfFont(PdfFontStore.Helvetica, PdfFontStore.SIZE_7), FontFactory.getInstance().createFont("Helvetica", 7));
		fontMap.put(new PdfFont(PdfFontStore.Helvetica, PdfFontStore.SIZE_8), FontFactory.getInstance().createFont("Helvetica", 8));
		fontMap.put(new PdfFont(PdfFontStore.Helvetica, PdfFontStore.SIZE_9), FontFactory.getInstance().createFont("Helvetica", 9));
		fontMap.put(new PdfFont(PdfFontStore.Helvetica, PdfFontStore.SIZE_10), FontFactory.getInstance().createFont("Helvetica", 10));
		fontMap.put(new PdfFont(PdfFontStore.Helvetica_Bold, PdfFontStore.SIZE_9), FontFactory.getInstance().createFont("Helvetica-Bold", 9));
		fontMap.put(new PdfFont(PdfFontStore.Helvetica_Bold, PdfFontStore.SIZE_14), FontFactory.getInstance().createFont("Helvetica-Bold", 14));
	}
	
	/**
	 * 
	 */
	public JcanPdfRenderer(ByteArrayOutputStream pdfOutStream, ByteArrayOutputStream psOutStream) {
		// [[
		Object[][] params = {{"pdfOutStream", pdfOutStream},
							{"psOutStream", psOutStream}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "JcanPdfRenderer", params);
		}
		// ]]
		try {
			try {
				PdfGen pdfGen = new PdfGen(pdfOutStream);
				PsGen psGen = new PsGen(psOutStream);
				output = new MultiOutputGenWrapper();
				output.setGenerator(PdfGen.class.getSimpleName(), pdfGen);
				output.setGenerator(PsGen.class.getSimpleName(), psGen);
				output.setPrimaryGenerator(pdfGen);
				
				
			} catch (Exception e) {
				LOG.error("Can't initialize PdfGen: " + e);
				throw new LogonException(e);
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
	
	/* (non-Javadoc)
	 * @see AbstractPdfRenderer#renderPage(PdfDocumentWrapper)
	 */
	@Override
	public void renderPage(PdfDocumentWrapper document) {
		try {
			output.newPage();
			Container container = new Container();
			tables.push(new Table(1));
			tables.get(0).attachTo(container);
			super.renderPage(document);
		}
		catch (PrinterException e) {
			LOG.error("Can't initialize new page: " + e);
			throw new LogonException(e);
		}
	}
	
	public void addImage(BlobDTO blobDTO, String imageName, double x, double y, double width, double height) {
		images.add(new ImageWrapper(blobDTO, imageName, x, y, width, height));
	}
	
	/**
	 * @param imgData
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void addImage(byte[] imgData, double x, double y, double width, double height) {
		images.add(new ImageWrapper(imgData, x, y, width, height));
	}
	
	/**
	 * ONLY FOR TESTING PURPOSES. Static test cases can't access DB to fetch image
	 * info. This method adds a "surrogate" image: a black box
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void addTestImage(final double x, final double y, final double width, 
			final double height) {
		addCustomSection(new CustomPrintSection() {
			
			public void print(Object outputObject) {
				if (!(outputObject instanceof OutputGen)) {
					throw new UnsupportedOperationException(
							"Output gens other than jcan-print are not supported");
				}
				OutputGen output = (OutputGen) outputObject;
				try {
					output.drawRect(Color.BLACK, x, y, width, height, 1);
				}
				catch (PrinterException e) {
					throw new LogonException(e);
				}
			}
		});
	}
	
	/*
	 * (non-Javadoc)
	 * @see AbstractPdfRenderer#initializeDocument(PdfDocumentWrapper)
	 */
	@Override
	public void initializeDocument(PdfDocumentWrapper document) {
		// [[
		Object[][] params = {{"document", document}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initializeDocument", params);
		}
		// ]]
		try {
			try {
				output.setPaperSize(new Points(document.getWidth()), new Points(document.getHeight()));
				output.setMargins(new Points(document.getMarginLeft()), new Points(document.getMarginRight()), 
						new Points(document.getMarginTop()), new Points(document.getMarginBottom()));
				output.setLineThickness((float) 0.1);
				// overwrite first page margins
				output.setFirstMargins(new Points(document.getMarginTop()), new Points(document.getMarginBottom()));
			} catch (Exception e) {
				LOG.error("Can't initialize PdfGen: " + e);
				throw new LogonException(e);
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
	
	/* (non-Javadoc)
	 * @see AbstractPdfRenderer#close()
	 */
	@Override
	public void close() {
		try {
			output.end();
		}
		catch (PrinterException e) {
			LOG.error("Can't finish print process: " + e);
			throw new LogonException(e);
		}
	}
	
	/**
	 * Method to access the jcan footer
	 * @param currentPage
	 */
	public void setPageFooter(int currentPage, int total) {
		if (currentPage == 1) {			
			output.setFirstFooter(new Footer("", "", MessageFormat.format("{0}/{1}", currentPage, total), new Points(10)));
		}
		else {
			output.setFooter(new Footer("", "", MessageFormat.format("{0}/{1}", currentPage, total), new Points(10)));
		}
	}

	/* (non-Javadoc)
	 * @see com.crh.logon.pdftool.renderer.AbstractPdfRender#displayObject(com.crh.logon.pdftool.objects.PdfObjectInterface)
	 */
	@Override
	protected void displayObject(PdfObject object) {
		// get the table to insert the element
		Table table = tables.lastElement();
		handleDraw(table, null, object, 0);
	}

	private void pushContainer(List<Integer> columnWidths, int numberOfColumns, int horizontalSpacing,
			boolean makeColumsEqualWidth) {
		
		Table table;
		if (columnWidths != null) {
			double widths[] = new double[columnWidths.size()];
			int counter = 0;
			for (Integer i : columnWidths) {
				widths[counter++] = i;
			}
			table = new Table(widths);
		}
		else {
			table = new Table(numberOfColumns);
		}
		
		tables.push(table);
	}

	private Container popContainer() {
		return tables.pop().getContainer();
	}
	
	/* (non-Javadoc)
	 * @see com.crh.logon.pdftool.renderer.AbstractPdfRender#print()
	 */
	@Override
	protected void print() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "print");
		}
		// ]]
		try {
			try {
				popContainer().render(output);
				// print all the images
				for (ImageWrapper wrapper : images) {
					showImage(wrapper);
				}
				// handle custom sections
				for (CustomPrintSection section : customSections) {
					section.print(output);
				}
				// remove all images and custom sections
				images.clear();
				customSections.clear();
			}
			catch (PrinterException e) {
				LOG.error("Can't finish print process: " + e);
				throw new LogonException(e);
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
	
	private void showImage(ImageWrapper wrapper) {
		try {
			byte[] data = wrapper.imgData;
			if (data.length == 0) {				
				PDFUtils.checkImageIntegrity(wrapper.blobDTO, wrapper.imageName);
				data = wrapper.blobDTO.getStream();
			}
			Image image = PDFUtils.createImage(data);				
			if (image != null) {
				//scale the image
				double xScale = wrapper.width  / image.getWidth();
				double yScale = wrapper.height / image.getHeight();
				double scale = (xScale >= yScale) ? yScale : xScale;
				output.showImage(image, wrapper.x, wrapper.y, scale, scale);
			}
		}
		catch (Exception e) {
			LOG.error("Can't print image: " + e);
			throw new LogonException(e);
		}
	}
	
	private void handleDraw(Table table, PdfObject parent, PdfObject object, int hSpacing) {
		Cell cell = null;
		int width = object.getWidth();
		int height = object.getHeight();
		PdfBorderData layoutData = object.getLayoutData();
		Alignment halignment = getHorizontalAlignment(layoutData);
		// object is a table. in this case, don't call the method
		// recursively again, just let the next check handle it
		if (object instanceof PdfTable) {
			// create a layout with the number of headers
			PdfTable tableObject = (PdfTable) object;
			int numberOfColumns = tableObject.getNumberOfColumns();
			// width of headers determines column width
			if (tableObject.getHeaders() != null) {				
				for (PdfObject header : tableObject.getHeaders()) {
					tableObject.getChildren().add(header);
				}
			}
			tableObject.setLayout(new PdfBorderLayout(numberOfColumns));
			// call label provider getText method to fill table.
			if (tableObject.getInput() != null) {
				for (Object input : tableObject.getInput()) {
					for (int i = 0; i < numberOfColumns; i++) {
						PdfObject textObject = tableObject.getLabelProvider().getTextObject(input, i);
						tableObject.getChildren().add(textObject);					
					}
				}
			}
		} 
		// the object has a layout. It's a complex object, a new table has to be added
		if (object.getLayout() != null) {
			int numberOfColumns = object.getLayout().getNumberOfColumns();
			// get the fist n object, to determine width
			List<Integer> columnWidths = new ArrayList<Integer>();
			if (object.getChildren().size() >= numberOfColumns) {
				for (int i = 0; i < numberOfColumns; i++) {
					PdfObject child = object.getChildren().get(i);
					int elementWidth = child.getWidth();
					columnWidths.add(elementWidth);
				}
			}
			// check all column width value. Set only, if all the values are valid (>0)
			boolean valid = columnWidths.size() > 0;
			for (Integer childWidth : columnWidths) {
				valid = childWidth > 0;
			}
			pushContainer(valid ? columnWidths : null, numberOfColumns, object.getLayout().getHorizontalSpacing(), object.getLayout().isMakeColumsEqualWidth());
			for (PdfObject element : object.getChildren()) {
				handleDraw(tables.lastElement(), object, element, object.getLayout().getHorizontalSpacing());
			}
			int numberOfElements = object.getChildren().size();
			while (numberOfElements++ % numberOfColumns != 0) {
				handleDraw(tables.lastElement(), null, new SimplePdfText(""), object.getLayout().getHorizontalSpacing());
			}
			Container container = new Container();
			tables.lastElement().attachTo(container);
			cell = new Cell(container);
			popContainer();
		}
		else if (object instanceof SimplePdfText) {
			// just write out the simple object
			SimplePdfText pdfText = (SimplePdfText) object;
			
			Font font = getFont(pdfText.getFont());
			String text = pdfText.getText().trim();
			if (parent != null) {
				double plannedWidth = parent.getPlannedWidth();
				text = cutTextToWidth(text, font, plannedWidth);
			}
			Paragraph paragraph = new Paragraph(halignment, font, text);
			
			cell = new Cell(paragraph);
			
			// add default padding
			cell.setHorizontalPadding(20);
		}
		else {
			throw new UnsupportedOperationException("Can't display object, not supported: " + object.getClass().getName());
		}
		if (height > 0) {
			cell.setHeight(height);
		}
		if (width > 0) {
			cell.setWidth(width);
		}
		cell.setHorizontalPadding(hSpacing);
		table.add(cell);
	}
	
	private Alignment getHorizontalAlignment(PdfBorderData borderData) {
		switch (borderData.getHorizontalOrientation()) {
		case LEFT:
			return Alignment.LEFT;
		case CENTER:
			return Alignment.CENTER;
		case RIGHT:
			return Alignment.RIGHT;
		default:
			return Alignment.LEFT;
		}
	}
	
	/**
	 * Returns the {@link Font} for the {@link PdfFont}
	 * @param font the {@link PdfFont}
	 * @return the {@link Font} or <code>null</code>, if no {@link Font} is found for {@link PdfFont}
	 */
	public static Font getFont(PdfFont font) {
		return fontMap.get(font);
	}

	/**
	 * Returns the font height in Points
	 * @param pdfFont the {@link PdfFont}
	 * @return the height of the {@link PdfFont} in Points.
	 * @throws IllegalArgumentException if the pdfFont is <code>null</code> or
	 * if the logical {@link PdfFont} is not found int the font map
	 */
	public static float getFontHeight(PdfFont pdfFont) {
		
		if (pdfFont == null) {
			throw new IllegalArgumentException("pdfFont argument cannot be null");
		}
		
		Font font = fontMap.get(pdfFont);
		
		if (font == null) {
			throw new IllegalArgumentException("PdfFont with style = '" + pdfFont.getStyle() + "', size = '" + pdfFont.getSize() + "' cannot be found");
		}
		
		return font.getSize();
		
	}
	
	/**
	 * @param text - to be cut
	 * @param font - will be used to check widt
	 * @param width - target width including ending
	 * @return a String which contains the cut text plus the ending and has length <= width 
	 */
	public static String cutTextToWidth(String text, Font font, double width) {
		// [[
		String result = null;
		Object[][] params = {{"text", text},
							{"font", font},
							{"width", width + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(JcanPdfRenderer.class, "cutTextToWidth", params);
		}
		// ]]
		try {
			if (width < 0) {
				return result = text;
			}
			else if (font.getWidth(text + " ") <= width) {
				return result = text;
			}
			return result = cutTextToWidth(text.substring(0, text.length() - 1), font, width);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public static SimplePdfText cutTextToWidth(SimplePdfText input, double width){
		// [[
		SimplePdfText result = null;
		Object[][] params = {{"input", input},
							{"width", width + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(JcanPdfRenderer.class, "cutTextToWidth", params);
		}
		// ]]
		try {			
			boolean widthNotSet = (width < -0.0001); // if width is below zero, we deal it like it's not set.
			
			if (widthNotSet){
				return result = input;
			}
			
			Font font = fontMap.get(input.getFont());				
			String newText = cutTextToWidth(input.getText(), font, width);
			
			return result = input.setText(newText);
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
