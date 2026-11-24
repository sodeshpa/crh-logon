/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout.utils;

import java.util.List;

import com.crh.logon.business.services.pdftools.layout.TextListPart;
import com.crh.logon.pdftool.objects.PdfObject;
import com.crh.logon.pdftool.objects.PdfRowBuilder;
import com.crh.logon.pdftool.objects.PdfRowBuilder.PdfRow;
import com.crh.logon.pdftool.objects.SimplePdfText;

/**
 * Utilities for Delivery Note and Collective Delivery Notes Lines Sections
 */
public class DeliveryNoteLinesSectionUtil {

	/**
	 * Returns true, if the cell is not empty (the cell contains a non-placeholder {@link TextListPart})
	 * @param row {@link PdfRow}
	 * @param index index of the cell 
	 * @return true if the cell is not empty
	 */
	public static boolean cellHasContent(PdfRow row, int index) {
		boolean result = false;
	
		TextListPart textList = DeliveryNoteLinesSectionUtil.getCellContent(row, index);
	
		if (textList != null) {
	
			result = !textList.isEmpty();
		}
	
		return result;
	}

	/**
	 * Returns the underline position of one line in the cells (Row1/Row2). 
	 * <i>It is assumed that "ship to" cell has always at least one line and the fonts are the same in
	 * all of the cells.</i>
	 * @param row the {@link PdfRow}
	 * @param columnIndex the index of the column
	 * @return the height of one line in row1/row2 in points.
	 * @throws IllegalArgumentException if the cell does not contain a {@link TextListPart}
	 */
	public static double getAddressBlockLineHeight(PdfRow row, int columnIndex) {
		double result = 0;
		TextListPart shipTo = DeliveryNoteLinesSectionUtil.getCellContent(row, columnIndex);
		if (shipTo != null) {
			result = shipTo.getUnderlinePosition();
		}
		else {
			throw new IllegalArgumentException("Ship-To cell should contain a TextListPart");
		}
		return result;
	}

	/**
	 * Returns the {@link TextListPart} the specified cell contains, or <code>null</code> otherwise.
	 * @param row {@link PdfRow}
	 * @param index index of the cell 
	 * @return true if the cell is not empty
	 */
	public static TextListPart getCellContent(PdfRow row, int index) {
		TextListPart result = null;
		if (hasChildren(row)) {
			PdfObject cell = row.getChildren().get(index);
			if (isTextListPart(cell)) {
				TextListPart textList = (TextListPart) cell;
				result = textList;
			}
		}
		return result;
	}

	/**
	 * Returns true, if <code>cell</code> is not null and has a type of {@link TextListPart} 
	 * @param cell the {@link PdfObject} to be checked
	 * @return true, if <code>cell</code> is not null and has a type of {@link TextListPart} 
	 */
	private static boolean isTextListPart(PdfObject cell) {
		return cell != null && cell instanceof TextListPart;
	}

	/**
	 * Returns true, if the <code>row</code> is not null and the {@link PdfRow#getChildren()} return a not null value.
	 * @param row the PdfRow to be checked.
	 * @return true, if the <code>row</code> is not null and the {@link PdfRow#getChildren()} return a not null value.
	 */
	private static boolean hasChildren(PdfRow row) {
		return row != null && row.getChildren() != null;
	}

	/**
	 * Adds an empty vertical space to the content with the specified height
	 * @param content the {@link List} of {@link PdfObject}
	 * @param height the height of the space in points
	 */
	public static void addSpace(List<PdfObject> content, int height) {
		content.add(new PdfRowBuilder(1, height).addElement(
				SimplePdfText.create("")).build());
	}

}
