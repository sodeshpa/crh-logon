/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.nevis.jcan.print.fonts.Font;

import com.crh.logon.pdftool.layout.PdfBorderLayout;
import com.crh.logon.pdftool.objects.AbstractPdfObject;
import com.crh.logon.pdftool.objects.PdfRowBuilder;
import com.crh.logon.pdftool.objects.PdfRowBuilder.PdfRow;
import com.crh.logon.pdftool.objects.SimplePdfText;
import com.crh.logon.pdftool.renderer.PdfFont;
import com.crh.logon.pdftool.renderer.PdfFontStore;
import com.crh.logon.pdftool.store.AbstractPdfDataStore;

/**
 * The address part of a delivery note.
 * @author AdNovum Informatik AG
 */
public class AddressPart extends AbstractPdfObject {

	/**
	 * {@link PdfFont} to be used for displaying the Address Part (Helvetica 7pt)
	 */
	private final static PdfFont FONT = new PdfFont(PdfFontStore.Helvetica, PdfFontStore.SIZE_7);

	/**
	 * Height of one row in Points
	 */
	private final static int HEIGHT = 10;

	/**
	 * Number of columns in the Address Part:
	 * <ol>
	 * <li> The place of the Crh Logo
	 * <li> The Company Name/Address/Email
	 * <li> Telephone, Fax, WWW address
	 * </ol>
	 */
	private final static int COLUMNS = 3;

	/**
	 * Width of the columns.
	 * @see #COLUMNS
	 */
	private final static int[] COLUMN_WIDTHS = new int[] { 315, 120, 80 };

	/**
	 * Empty String constant
	 */
	private final static String EMPTY_STRING = "";

	/**
	 * The Pdf Data Store
	 */
	private AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store;

	/**
	 * Stores the height of the AddressPart 
	 */
	private int heightInPoints;
	
	/**
	 * {@link GlyphWidthProvider} for breaking too long text into multiple lines 
	 */
	private final class AddressTextGlyphWidthProvider implements GlyphWidthProvider {
		@Override
		public double glyphWidth(char glyph) {

			Font font = JcanPdfRenderer.getFont(FONT);

			// return the width of the specified glyph (character)
			return font.getWidth(glyph);
		}
	}

	/**
	 * Returns the height of AddressPart in Points, calculated by using the number of rows * constant HEIGHT (10).
	 * The number of rows dependent on the length of the text lines, since longer text will be broken into multiple lines. 
	 * @return the heightInPoints the height of thge {@link AddressPart} in Points.
	 */
	public int getHeightInPoints() {
		return heightInPoints;
	}

	/**
	 * Constructor.
	 * @param store the {@link AbstractPdfDataStore}
	 */
	public AddressPart(AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store) {
		super();
		this.store = store;
		// generate the PDFObject hierarchy and return the number of rows
		int numberOfRows = generateAddressPart();
		this.heightInPoints = numberOfRows * HEIGHT;
	}

	/**
	 * Generates the rows for the {@link AddressPart}
	 * @param store the {@link AbstractPdfDataStore}
	 * @return number of rows created
	 */
	private int generateAddressPart() {
		setLayout(new PdfBorderLayout(1));

		ArrayList<List<String>> data = new ArrayList<List<String>>();

		List<String> row1 = new ArrayList<String>(Arrays.asList("",
				store.getData(DeliveryNoteData.DEST_ADDRESS1),
				store.getData(DeliveryNoteData.DEST_TEL)));

		List<String> row2 = new ArrayList<String>(Arrays.asList("",
				store.getData(DeliveryNoteData.DEST_ADDRESS2),
				store.getData(DeliveryNoteData.DEST_FAX)));

		List<String> row3 = new ArrayList<String>(Arrays.asList("",
				store.getData(DeliveryNoteData.DEST_ADDRESS3),
				store.getData(DeliveryNoteData.DEST_TAX)));

		List<String> row4 = new ArrayList<String>(Arrays.asList("",
				store.getData(DeliveryNoteData.DEST_EMAIL), ""));

		data.add(row1);
		data.add(row2);
		data.add(row3);
		data.add(row4);

		// create list of column widths
		List<Integer> columnWidths = new ArrayList<Integer>();
		for (int i = 0; i < COLUMN_WIDTHS.length; i++) {
			columnWidths.add(COLUMN_WIDTHS[i]);
		}
		// GlyphWidthProvider
		GlyphWidthProvider glyphWidthProvider = new AddressTextGlyphWidthProvider();

		int rowCount = 0;

		for (List<String> row : data) {
			rowCount += breakTooLongRowsIntoMultipleLines(row, columnWidths, glyphWidthProvider);
		}

		return rowCount;
	}

	/**
	 * Break the too long lines into multiple lines to avoid clipping/disappearing of text.
	 * The parameter list is equivalent of {@link #addPdfRow(List)}
	 * @param columnContent {@link List} of {@link String}s containing the content for the columns.
	 * @param columnWidths {@link List} of {@link Integer}s containing the widths (in points) of the columns.
	 * @return number of rows created
	 */
	private int breakTooLongRowsIntoMultipleLines(List<String> columnContent,
			List<Integer> columnWidths, GlyphWidthProvider glyphWidthProvider) {

		if (columnContent == null || columnWidths == null || glyphWidthProvider == null) {
			throw new IllegalArgumentException("parameters should not be null");
		}
		if (columnContent.size() != columnWidths.size()) {
			throw new IllegalArgumentException(
					"size of columnContent and columnsWidths should be the same");
		}
		if (columnContent.size() == 0) {
			throw new IllegalArgumentException("columnContent should not be empty");
		}

		// initialize columns List
		List<List<String>> columns = new ArrayList<List<String>>();

		int maxNumberOfLines = 0;

		// break text into multiple lines
		for (int i = 0; i < columnContent.size(); i++) {

			String originalLine = columnContent.get(i);
			int columnWidth = columnWidths.get(i);

			List<String> resultLines = LineBreaker.fitToWidth(columnWidth, originalLine, null,
					glyphWidthProvider);

			columns.add(resultLines);

			if (resultLines.size() > maxNumberOfLines) {
				maxNumberOfLines = resultLines.size();
			}
		}

		// add empty lines for columns (if needed) to have equal number of lines 
		for (List<String> column : columns) {
			if (column.size() < maxNumberOfLines) {
				addEmptlyLines(column, maxNumberOfLines - column.size());
			}
		}

		// collect row data
		for (int row = 0; row < maxNumberOfLines; row++) {

			List<String> rowData = new ArrayList<String>(columns.size());

			for (int column = 0; column < columnContent.size(); column++) {

				// get cell content
				String cellContent = columns.get(column).get(row);

				// add to row
				rowData.add(cellContent);
			}

			// add the row data to the PDF output
			addPdfRow(rowData);
		}

		return maxNumberOfLines;
	}

	/**
	 * Add empty {@link String}s to the column {@link List}
	 * @param column the {@link List} of {@link String}s
	 * @param numberOfEmptyLinesToAdd the number of empty lines
	 */
	private void addEmptlyLines(List<String> column, int numberOfEmptyLinesToAdd) {

		for (int i = 0; i < numberOfEmptyLinesToAdd; i++) {
			column.add(EMPTY_STRING);
		}

	}

	/**
	 * Adds a row to the output
	 * @param row the {@link List} of {@link String}s containing the content for the columns.
	 * The length of the {@link List} should be equal to the constant COLUMNS (3, the first 
	 * column is the space for the Crh logo). The height of the row
	 * will use the constant value of HEIGHT (10pt) 
	 */
	private void addPdfRow(List<String> row) {
		PdfRowBuilder rowb = new PdfRowBuilder(COLUMNS, HEIGHT);
		for (int i = 0; i < row.size(); i++) {
			SimplePdfText pdfText = getPdfText(row.get(i), COLUMN_WIDTHS[i]);
			rowb.addElement(pdfText);
		}
		PdfRow pdfRow = rowb.build();
		pdfRow.setLayout(new PdfBorderLayout(COLUMNS));
		getChildren().add(pdfRow);
	}

	/**
	 * Returns a {@link SimplePdfText} instance with the given <code>text</code>, 
	 * <code>width</code> and FONT constant (Helvetica 7pt) attribute
	 * @param text the text
	 * @param width the width of the text
	 * @return the {@link SimplePdfText}
	 */
	private SimplePdfText getPdfText(String text, int width) {
		SimplePdfText result = SimplePdfText.create(text);
		result.setWidth(width);
		result.setFont(FONT);
		return result;
	}
}