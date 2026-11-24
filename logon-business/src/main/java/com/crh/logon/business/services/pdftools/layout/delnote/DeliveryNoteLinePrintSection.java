/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout.delnote;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
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
 * Custom section for printing the lines.
 * The coordinate arithmetic is the following:
 * For the x coordinate the zero coordinate is in the left side of the page and the coordinate values increase to the right,
 * for the y coordinate the zero is on the bottom of the page and increase to the top. 
 * @author AdNovum Informatik AG
 */
public class DeliveryNoteLinePrintSection implements CustomPrintSection {

	/**
	 * Column index of Commercial Carrier's cell
	 */
	private static final int COLUMN_INDEX_COMMERCIAL_CARRIER = 1;

	/**
	 * Column index of Executing carrier's cell
	 */
	private static final int COLUMN_INDEX_EXECUTING_CARRIER = 2;

	/**
	 * Column index of Payer's cell
	 */
	private static final int COLUMN_INDEX_PAYER = 2;

	/**
	 * Column Index of Ship-to's cell
	 */
	private static final int COLUMN_INDEX_SHIP_TO = 0;

	/**
	 * Logger
	 */
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DeliveryNoteLinePrintSection.class);

	/**
	 * top y coordinate of first address block
	 */
	private double addressBlock1TopYCoordinate;

	/**
	 * top y coordinate of second address block
	 */
	private double addressBlock2TopYCoordinate;

	/**
	 * PDF Document
	 */
	private PdfDocumentWrapper document;

	/**
	 * top y coordinate of material block
	 */
	private double materialBlockTopYCoordinate;

	/**
	 * Top Y coordinate of remark block
	 */
	private double remarkBlockTopYCoordinate;

	/**
	 * First row containing ship to, sold to, payer
	 */
	private PdfRow row1;

	/**
	 * Second row containing sold to, comm carrier, exec carrier
	 */
	private PdfRow row2;

	/**
	 * Constructor
	 * @param document {@link PdfDocumentWrapper}
	 */
	public DeliveryNoteLinePrintSection(PdfDocumentWrapper document) {
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
	 * Returns the y coordinate of the line under the "Materials" label
	 * @return the y coordinate of the line under the "Materials" label
	 */
	private double getMaterialLineY() {

		return getDocumentTopY() - this.materialBlockTopYCoordinate;
	}

	/**
	 * Returns the y coordinate of the line under the "Remark" label
	 * @return the y coordinate of the line under the "Remark" label
	 */
	public double getRemarkLineY() {

		return getDocumentTopY() - remarkBlockTopYCoordinate;
	}

	/**
	 * Returns the y coordinate of the first row's underline (ship-to, bill-to, payer)
	 * @return the y coordinate of the first row's underline
	 */
	private double getRow1LineY() {

		return getDocumentTopY() - this.addressBlock1TopYCoordinate - DeliveryNoteLinesSectionUtil.getAddressBlockLineHeight(this.row1, COLUMN_INDEX_SHIP_TO);
	}

	/**
	 * Returns the y coordinate of the lines under sold to, comm. carrier, exec. carrier
	 * @return the y coordinate of the lines under sold to, comm. carrier, exec. carrier
	 */
	private double getRow2LineY() {
		return getDocumentTopY() - this.addressBlock2TopYCoordinate - DeliveryNoteLinesSectionUtil.getAddressBlockLineHeight(this.row1, COLUMN_INDEX_SHIP_TO);
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

			double line1Y = getRow1LineY();

			// 230 lines
			// shipto
			output.drawLine(Color.BLACK, 40, line1Y, 200, line1Y, 1.0);
			// billto
			output.drawLine(Color.BLACK, 218, line1Y, 378, line1Y, 1.0);
			// payer - conditional
			if (DeliveryNoteLinesSectionUtil.cellHasContent(this.row1, COLUMN_INDEX_PAYER)) {
				output.drawLine(Color.BLACK, 396, line1Y, 556, line1Y, 1.0);
			}

			// soldto
			double line2Y = getRow2LineY();

			output.drawLine(Color.BLACK, 40, line2Y, 200, line2Y, 1.0);
			// comm. carrier - conditional

			if (DeliveryNoteLinesSectionUtil.cellHasContent(this.row2, COLUMN_INDEX_COMMERCIAL_CARRIER)) {
				output.drawLine(Color.BLACK, 218, line2Y, 378, line2Y, 1.0);
			}
			// exec. carrier - conditional
			if (DeliveryNoteLinesSectionUtil.cellHasContent(this.row2, COLUMN_INDEX_EXECUTING_CARRIER)) {
				output.drawLine(Color.BLACK, 396, line2Y, 556, line2Y, 1.0);
			}

			double materialLineY = getMaterialLineY();

			// 520 lines
			// before material table
			output.drawLine(Color.BLACK, 40, materialLineY, 560, materialLineY, 1.0);

			double remarkLineY = getRemarkLineY();

			// before remarks
			output.drawLine(Color.BLACK, 40, remarkLineY, 560, remarkLineY, 1.0);
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

}
