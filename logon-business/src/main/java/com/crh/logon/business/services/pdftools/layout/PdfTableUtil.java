/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout;

import java.util.List;

import ch.nevis.jcan.print.fonts.Font;

import com.crh.logon.business.services.pdftools.layout.utils.GenericGlyphWidthProvider;
import com.crh.logon.pdftool.labelprovider.TableLabelProvider;
import com.crh.logon.pdftool.objects.PdfTable;
import com.crh.logon.pdftool.objects.SimplePdfText;

/**
 * Utility class to calculate the height of the material table in the delivery note
 */
public class PdfTableUtil {

	/**
	 * Returns the height of the table in Points
	 * @return the height of the table in Points
	 */
	public static int getHeightInPoints(PdfTable table) {

		if (table == null) {
			throw new IllegalArgumentException("table should not be null");
		}

		int allLinesInTable = 0;
		List<SimplePdfText> headerLabels = table.getHeaders();
		List<?> rows = table.getInput();
		int numberOfColumns = table.getNumberOfColumns();
		TableLabelProvider labelProvider = table.getLabelProvider();
		int fontHeight = table.getFontHeight();

		if (headerLabels == null || headerLabels.size() == 0 || numberOfColumns == 0) {
			// if no header return since header contains the width information for columns
			return allLinesInTable * fontHeight;
		}

		// one line for header
		allLinesInTable++;

		if (rows == null || rows.size() == 0) {
			// if no table data, return
			return allLinesInTable * fontHeight;
		}

		for (Object row : rows) {

			int maxNumberOfLinesInRow = 0;

			for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {

				int columnWidth = headerLabels.get(columnIndex).getWidth();

				Object o = labelProvider.getTextObject(row, columnIndex);

				// The labelProvider returns only SimplePdfText items
				if (o instanceof SimplePdfText) {

					SimplePdfText simplePdfText = (SimplePdfText) o;

					String text = simplePdfText.getText();
					
					final Font font = JcanPdfRenderer.getFont(simplePdfText.getFont());

					GenericGlyphWidthProvider glyphWidthProvider = new GenericGlyphWidthProvider(font);
					List<String> lines = LineBreaker.fitToWidth(columnWidth, text, null,
							glyphWidthProvider);

					int numberOfBrokenLines = lines.size();
					int numberOfLinesInCell = numberOfBrokenLines;
					if (numberOfLinesInCell > maxNumberOfLinesInRow) {
						maxNumberOfLinesInRow = numberOfLinesInCell;
					}

				}

			}

			allLinesInTable += maxNumberOfLinesInRow;

		}

		return allLinesInTable * fontHeight;
	}
}
