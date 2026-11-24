/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout.colldelnote;

import ch.nevis.jcan.print.base.Color;
import ch.nevis.jcan.print.base.OutputGen;
import ch.nevis.jcan.print.base.PrinterException;

import com.crh.logon.business.services.pdftools.layout.PdfLayoutGenerationTool;
import com.crh.logon.business.services.pdftools.layout.utils.DeliveryNoteLinesSectionUtil;
import com.crh.logon.pdftool.PdfDocumentWrapper;
import com.crh.logon.pdftool.objects.PdfRowBuilder.PdfRow;
import com.crh.logon.pdftool.renderer.CustomPrintSection;
import com.crh.logon.utils.LogonException;

/**
 * Custom section for printing the lines for Collective Delivery Note.
 * The coordinate arithmetic is the following:
 * For the x coordinate the zero coordinate is in the left side of the page and the coordinate values increase to the right,
 * for the y coordinate the zero is on the bottom of the page and increase to the top. 
 * @author AdNovum Informatik AG
 */
public class CollectiveDeliveryNoteLinePrintSection implements CustomPrintSection {

	/**
	 * Bill-to column's left side's x coordinate
	 */
	private static final int BILLTO_LEFT_X_COORDINATE = 396;

	/**
	 * Bill-to column's right side's x coordinate
	 */
	private static final int BILLTO_RIGHT_X_COORDINATE = 556;

	/**
	 * Column Index of Ship-to's cell
	 */
	private static final int COLUMN_INDEX_SHIP_TO = 0;

	/**
	 * Line color 
	 */
	private static final Color LINE_COLOR = Color.BLACK;

	/**
	 * Line thickness
	 */
	private static final double LINE_THICKNESS = 1.0;

	/**
	 * X coordinate of the left side of the margin
	 */
	private static final int MARGIN_LEFT_X_COORDINATE = 40;

	/**
	 * X coordinate of the right side of the margin
	 */
	private static final int MARGIN_RIGHT_X_COORDINATE = 560;

	/**
	 * Ship-to column's left side's x coordinate
	 */
	private static final int SHIP_TO_LEFT_X_COORDINATE = 40;

	/**
	 * Ship-to column's right side's x coordinate
	 */
	private static final int SHIPTO_RIGHT_X_COORDINATE = 200;

	/**
	 * Sold-to column's left side's x coordinate
	 */
	private static final int SOLD_TO_LEFT_X_COORDINATE = 218;

	/**
	 * Sold-to column's right side's x coordinate
	 */
	private static final int SOLDTO_RIGHT_X_COORDINATE = 378;

	/**
	 * top y coordinate of first address block
	 */
	private Double addressBlock1TopYCoordinate;

	/**
	 * top y coordinate of second address block
	 */
	private Double addressBlock2TopYCoordinate;

	/**
	 * PDF Document
	 */
	private PdfDocumentWrapper document;

	/**
	 * top y coordinate of material block
	 */
	private Double materialBlockTopYCoordinate;

	/**
	 * Top Y coordinate of remark block
	 */
	private Double remarkBlockTopYCoordinate;

	/**
	 * First row containing ship to, sold to, payer
	 */
	private PdfRow row1;

	/**
	 * Second row containing sold to, comm carrier, exec carrier
	 */
	private PdfRow row2;

	/**
	 * Services block top y coordinate
	 */
	private Double servicesBlockTopYCoordinate;

	/**
	 * Constructor
	 * @param document {@link PdfDocumentWrapper}
	 */
	public CollectiveDeliveryNoteLinePrintSection(PdfDocumentWrapper document) {
		this.document = document;
	}

	/**
	 * Return the top y coordinate of the document
	 * @return the top y coordinate of the document
	 */
	private double getDocumentTopY() {
		return document.getHeight();
	}

	/**
	 * Returns the y coordinate of the line under the "Materials" label, if set. Returns null, if not set.
	 * @return the y coordinate of the line under the "Materials" label
	 */
	private Double getMaterialLineY() {

		if (this.materialBlockTopYCoordinate != null) {

			return getDocumentTopY() - this.materialBlockTopYCoordinate;
		}
		else {
			return null;
		}
	}

	/**
	 * Returns the y coordinate of the line under the "Remark" label
	 * @return the y coordinate of the line under the "Remark" label
	 */
	public Double getRemarkLineY() {
		if (this.remarkBlockTopYCoordinate != null) {
			return getDocumentTopY() - remarkBlockTopYCoordinate;
		}
		else {
			return null;
		}
	}

	/**
	 * Returns the y coordinate of the first row's underline (ship-to, bill-to, payer)
	 * @return the y coordinate of the first row's underline
	 */
	private Double getRow1LineY() {

		if (row1 != null && this.addressBlock1TopYCoordinate != null) {

			return getDocumentTopY()
					- this.addressBlock1TopYCoordinate
					- DeliveryNoteLinesSectionUtil.getAddressBlockLineHeight(this.row1,
							COLUMN_INDEX_SHIP_TO);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns the y coordinate of the lines under sold to, comm. carrier, exec. carrier
	 * @return the y coordinate of the lines under sold to, comm. carrier, exec. carrier
	 */
	private Double getRow2LineY() {

		if (row2 != null && this.addressBlock2TopYCoordinate != null) {

			return getDocumentTopY()
					- this.addressBlock2TopYCoordinate
					- DeliveryNoteLinesSectionUtil.getAddressBlockLineHeight(this.row1,
							COLUMN_INDEX_SHIP_TO);
		}
		else {
			return null;

		}

	}

	/**
	 * Returns the Y coordinate of the line above "Services" block, if set. Returns null if not set.
	 * @return the Y coordinate of the line above "Services" block.
	 */
	private Double getServicesLineY() {
		if (this.servicesBlockTopYCoordinate != null) {
			return getDocumentTopY() - this.servicesBlockTopYCoordinate;
		}
		else {
			return null;
		}
	}

	/**
	 * @see CustomPrintSection#print(java.lang.Object)
	 */
	public void print(Object outputObject) {
		if (!(outputObject instanceof OutputGen)) {
			throw new UnsupportedOperationException(
					"Output gens other than jcan-print are not supported");
		}
		OutputGen output = (OutputGen) outputObject;

		try {

			Double line1Y = getRow1LineY();

			if (line1Y != null) {

				output.drawLine(LINE_COLOR, SHIP_TO_LEFT_X_COORDINATE, line1Y,
						SHIPTO_RIGHT_X_COORDINATE, line1Y, LINE_THICKNESS);

				output.drawLine(LINE_COLOR, SOLD_TO_LEFT_X_COORDINATE, line1Y,
						SOLDTO_RIGHT_X_COORDINATE, line1Y, LINE_THICKNESS);

				output.drawLine(LINE_COLOR, BILLTO_LEFT_X_COORDINATE, line1Y,
						BILLTO_RIGHT_X_COORDINATE, line1Y, LINE_THICKNESS);

			}

			// Payer (if set)
			Double line2Y = getRow2LineY();
			if (line2Y != null) {

				output.drawLine(LINE_COLOR, BILLTO_LEFT_X_COORDINATE, line2Y,
						BILLTO_RIGHT_X_COORDINATE, line2Y, LINE_THICKNESS);

			}

			Double materialLineY = getMaterialLineY();
			if (materialLineY != null) {
				// before material table
				output.drawLine(LINE_COLOR, MARGIN_LEFT_X_COORDINATE, materialLineY,
						MARGIN_RIGHT_X_COORDINATE, materialLineY, LINE_THICKNESS);
			}

			Double servicesLineY = getServicesLineY();
			if (servicesLineY != null) {
				output.drawLine(LINE_COLOR, MARGIN_LEFT_X_COORDINATE, servicesLineY,
						MARGIN_RIGHT_X_COORDINATE, servicesLineY, LINE_THICKNESS);
			}

			Double remarkLineY = getRemarkLineY();
			if (remarkLineY != null) {
				output.drawLine(LINE_COLOR, MARGIN_LEFT_X_COORDINATE, remarkLineY,
						MARGIN_RIGHT_X_COORDINATE, remarkLineY, LINE_THICKNESS);
			}
		}
		catch (PrinterException e) {
			throw new LogonException(e);
		}
	}

	/**
	 * To be called before the address block is added to the document
	 * @param row1 the first row containing ship-to, bill-to. payer
	 */
	public void saveAddressBlock1YCoordinate(PdfRow row1) {
		this.addressBlock1TopYCoordinate = PdfLayoutGenerationTool.getCurrentYCoordinate(this.document);
		this.row1 = row1;
	}

	/**
	 * To be called before the address block is added to the document
	 * @param row2 the second row containing sold-to, comm. carrier, exec. carrier	
	 */
	public void saveAddressBlock2YCoordinate(PdfRow row2) {
		this.addressBlock2TopYCoordinate = PdfLayoutGenerationTool.getCurrentYCoordinate(this.document);
		this.row2 = row2;
	}

	/**
	 * To be called before the material block is added to the document
	 */
	public void saveMaterialBlockYCoordinate() {
		this.materialBlockTopYCoordinate = PdfLayoutGenerationTool.getCurrentYCoordinate(this.document);
	}

	/**
	 * To be called before the remark block is added to the document
	 */
	public void saveRemarkBlockYCoordinate() {
		this.remarkBlockTopYCoordinate = PdfLayoutGenerationTool.getCurrentYCoordinate(this.document);
	}

	/**
	 * To be called before the services block is added to the document
	 */
	public void saveServicesBlockYCoordinate() {
		this.servicesBlockTopYCoordinate = PdfLayoutGenerationTool.getCurrentYCoordinate(this.document);
	}

}
