/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout;

import java.util.List;
import java.util.Locale;

import ch.nevis.jcan.print.base.OutputGen;
import ch.nevis.jcan.print.base.PrinterException;

import com.crh.logon.business.services.localinterfaces.GenericBlobServiceLocalInterface;
import com.crh.logon.business.services.pdftools.layout.logoutils.LogoBlock;
import com.crh.logon.business.services.pdftools.layout.logoutils.LogoUtils;
import com.crh.logon.business.services.pdftools.store.DeliveryNotePdfDataStore;
import com.crh.logon.constants.Constant;
import com.crh.logon.pdftool.PdfDocumentWrapper;
import com.crh.logon.pdftool.renderer.CustomPrintSection;
import com.crh.logon.pdftool.store.AbstractPdfDataStore;
import com.crh.logon.persistence.entities.common.MatPlantEntity;
import com.crh.logon.persistence.entities.sap.SapMatPlantNormEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.StringUtils;

/**
 * Custom section for printing logos
 * @author AdNovum Informatik AG
 */
public class LogoPrintSection implements CustomPrintSection {

	/**
	 * Change to true to display debug lines for Logo area/Logo Image/Text bounding boxes
	 */
	private static final boolean DEBUG_PDF = false;

	/**
	 * 10 Points of left "internal" margin for Logo Block, to be aligned with the left side of the delivery note
	 */
	private static final double LOGO_BLOCK_LEFT_INDENT_FROM_LEFT_MARGIN = 10.0;

	/**
	 *  10 Points of right "internal" margin for Logo Block, to be aligned to the right side of the delivery note
	 */
	private static final double LOGO_BLOCK_RIGHT_INDENT_FROM_RIGHT_MARGIN = 10.0;

	/**
	 * Font name for logo texts 
	 */
	private static final String MAT_PLANT_FONT_NAME = "Helvetica";

	/**
	 * Font size for logo texts
	 */
	private static final int MAT_PLANT_FONT_SIZE = 7;

	/**
	 * Generic Logo width in logical units
	 */
	private static final double MAT_PLANT_GENERIC_LOGO_WIDTH_LOGICAL_UNITS = 2.6;

	/**
	 * Horizontal space between logo images in logical units
	 */
	private static final double MAT_PLANT_LOGO_HORIZONTAL_SPACING_LOGICAL_UNITS = 0.2;

	/**
	 * Width of the first logo in logical units
	 */
	private static final double MAT_PLANT_LOGO1_WIDTH_LOGICAL_UNITS = 2.3;

	/**
	 * Logo width in logical units (Except the first and the generic logo)
	 */
	private static final double MAT_PLANT_OTHER_LOGO_WIDTH_LOGICAL_UNITS = 1.8;

	/**
	 * Font name for text under the logo images
	 */
	private static final String SAP_MAT_PLANT_FONT_NAME = MAT_PLANT_FONT_NAME;

	/**
	 * Font size for text rendering under logo images
	 */
	private static final int SAP_MAT_PLANT_FONT_SIZE = 7;

	/**
	 * Generic logo width in logical units
	 */
	private static final double SAP_MAT_PLANT_GENERIC_LOGO_WIDTH_LOGICAL_UNITS = 2.9;

	/**
	 * Width of the logo in logical units
	 */
	private static final double SAP_MAT_PLANT_LOGO_WIDTH_LOGICAL_UNITS = 2.6;

	/**
	 * Maximum number of logos for SAP_MAT_PLANT...
	 */
	private final static int SAP_MAT_PLANT_MAXIMUM_NUMBER_OF_LOGOS = 4;

	/**
	 * Index of the second logo for SAP_MAT_PLANT...
	 */
	private static final int SAP_MAT_PLANT_SECOND_LOGO = 1;

	/**
	 * Space between the logos in logical units
	 */
	private static final double SAP_MAT_PLANT_SPACE_BETWEEN_LOGOS_LOGICAL_UNITS = 0.3;

	/**
	 * Upper margin for the Logo area
	 */
	private static final double LOGO_SECTION_UPPER_MARGIN_HEIGHT_IN_POINTS = MAT_PLANT_FONT_SIZE * 2;

	/**
	 * {@link GenericBlobServiceLocalInterface}
	 */
	private GenericBlobServiceLocalInterface blobService = null;

	/**
	 * The document for getting the position of the start and end Y coordinate of the logo section
	 */
	private PdfDocumentWrapper document;

	/**
	 * End Y coordinate of the logo section
	 */
	private double bottomYCoordinate;

	/**
	 * Show ZCode1-2 flag
	 */
	private boolean expShowZcode12Flag;

	/**
	 * Locale for getting the language code for finding the localized version of a logo image
	 */
	private final Locale locale;

	/**
	 * X coordinate of the left side of the logo area in Points
	 */
	private double logoBlockLeftX;

	/**
	 * X coordinate of the right side of the logo area in Points
	 */
	private double logoBlockRightX;

	/**
	 * {@link MatPlantEntity} 
	 */
	private MatPlantEntity matPlantEntity = null;

	/**
	 * List of {@link SapMatPlantNormEntity} for SAP_MAT_PLANT... logo rendering
	 */
	private List<SapMatPlantNormEntity> sapMatPlantNormList = null;

	/**
	 * top Y coordinate of the logo section
	 */
	private double topYCoordinate;

	/**
	 * Constructor
	 * @param document the {@link PdfDocumentWrapper}
	 * @param store the {@link AbstractPdfDataStore}
	 */
	public LogoPrintSection(PdfDocumentWrapper document,
			AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store) {

		this.document = document;

		locale = store.getLocale();
		// logo information can only be accessed in a non-mock working environment
		if (store instanceof DeliveryNotePdfDataStore) {
			DeliveryNotePdfDataStore delStore = (DeliveryNotePdfDataStore) store;
			blobService = delStore.getGenericBlobService();
			matPlantEntity = delStore.getMatPlantEntity();
			sapMatPlantNormList = delStore.getMatPlantNormList();
			expShowZcode12Flag = Boolean.valueOf(delStore.getData(DeliveryNoteData.EXPORT_INVOICE_SHOW_ZCODE1_2_FLAG));
		}
	}

	/**
	 * Return the top y coordinate of the document
	 * @return the top y coordinate of the document
	 */
	private double getDocumentTopY() {
		return document.getHeight();
	}

	/**
	 * Returns the end Y coordinate 
	 * @return the end Y coordinate 
	 */
	public double getEndYCoordinate() {
		return getDocumentTopY() - bottomYCoordinate;
	}

	/**
	 * Returns the start Y coordinate 
	 * @return the start Y coordinate 
	 */
	public double getStartYCoordinate() {
		return getDocumentTopY() - topYCoordinate;
	}

	/**
	 * @see CustomPrintSection#print()
	 */
	public void print(Object outputObject) {
		if (!(outputObject instanceof OutputGen)) {
			throw new UnsupportedOperationException(
					"Output gens other than jcan-print are not supported");
		}

		try {

			OutputGen output = (OutputGen) outputObject;
			logoBlockLeftX = output.getLeftMargin() + LOGO_BLOCK_LEFT_INDENT_FROM_LEFT_MARGIN;
			logoBlockRightX = output.getPageWidth() - output.getRightMargin()
					- LOGO_BLOCK_RIGHT_INDENT_FROM_RIGHT_MARGIN;

			if (DEBUG_PDF) {
				LogoUtils.renderLogoAreaBoundingBox(output, this);
			}

			if (matPlantEntity != null) {

				if (matPlantEntity.getPrioOverSapMatNormFlag()) {
					printMatPlant(output);
				}
				else {
					if (sapMatPlantNormList != null) {
						printSapMatPlantNormList(output);
					}
				}
			}
		}
		catch (PrinterException pe) {
			throw new LogonException(pe);
		}

	}

	/**
	 * @return the logoBlockLeftX
	 */
	public double getLogoBlockLeftX() {
		return logoBlockLeftX;
	}

	/**
	 * @return the logoBlockRightX
	 */
	public double getLogoBlockRightX() {
		return logoBlockRightX;
	}

	/** 
	 * Print logos based on matPlantEntity.
	 * @throws PrinterException if pdf generation failed
	 */
	public void printMatPlant(OutputGen output) throws PrinterException {

		if (matPlantEntity != null) {

			String languageCode = null;
			if (locale != null && !Constant.LANGUAGE_CODE_GERMAN.equals(locale.getLanguage())) {
				languageCode = locale.getLanguage();
			}

			LogoBlock logoBlock = new LogoBlock(this.document, languageCode, this.blobService,
					logoBlockLeftX, logoBlockRightX,
					MAT_PLANT_LOGO_HORIZONTAL_SPACING_LOGICAL_UNITS,
					getStartYCoordinate(),
					getEndYCoordinate(), MAT_PLANT_FONT_NAME, MAT_PLANT_FONT_SIZE);

			logoBlock.addLogo(matPlantEntity.getLogo1(), MAT_PLANT_LOGO1_WIDTH_LOGICAL_UNITS,
					LogoUtils.getList(matPlantEntity.getLogo1Z1(), matPlantEntity.getLogo1Z2(),
							matPlantEntity.getNationalNorm()));

			logoBlock.addLogoWithoutText(matPlantEntity.getLogoGeneric(),
					MAT_PLANT_GENERIC_LOGO_WIDTH_LOGICAL_UNITS);

			logoBlock.addLogo(matPlantEntity.getLogo2(), MAT_PLANT_OTHER_LOGO_WIDTH_LOGICAL_UNITS,
					LogoUtils.getList(matPlantEntity.getLogo2Z1(), matPlantEntity.getLogo2Z2(),
							matPlantEntity.getLogo2Z3()));
			logoBlock.addLogo(matPlantEntity.getLogo3(), MAT_PLANT_OTHER_LOGO_WIDTH_LOGICAL_UNITS,
					LogoUtils.getList(matPlantEntity.getLogo3Z1(), matPlantEntity.getLogo3Z2(),
							matPlantEntity.getLogo3Z3()));
			logoBlock.addLogo(matPlantEntity.getLogo4(), MAT_PLANT_OTHER_LOGO_WIDTH_LOGICAL_UNITS,
					LogoUtils.getList(matPlantEntity.getLogo4Z1(), matPlantEntity.getLogo4Z2(),
							matPlantEntity.getLogo4Z3()));
			logoBlock.addLogo(matPlantEntity.getLogo5(), MAT_PLANT_OTHER_LOGO_WIDTH_LOGICAL_UNITS,
					LogoUtils.getList(matPlantEntity.getLogo5Z1(), matPlantEntity.getLogo5Z2(),
							matPlantEntity.getLogo5Z3()));
			logoBlock.addLogo(matPlantEntity.getLogo6(), MAT_PLANT_OTHER_LOGO_WIDTH_LOGICAL_UNITS,
					LogoUtils.getList(matPlantEntity.getLogo6Z1(), matPlantEntity.getLogo6Z2(),
							matPlantEntity.getLogo6Z3()));

			logoBlock.render(output);

		}

	}

	/** 
	 * Print logos based on sapMatPlantNormList.
	 * @param output {@link OutputGen}
	 * @throws PrinterException if pdf generation failed
	 */
	public void printSapMatPlantNormList(OutputGen output) throws PrinterException {

		String languageCode = null;
		if (locale != null && !Constant.LANGUAGE_CODE_GERMAN.equals(locale.getLanguage())) {
			languageCode = locale.getLanguage();
		}

		LogoBlock logoBlock = new LogoBlock(this.document, languageCode, this.blobService,
				this.logoBlockLeftX, this.logoBlockRightX,
				SAP_MAT_PLANT_SPACE_BETWEEN_LOGOS_LOGICAL_UNITS,
				getStartYCoordinate(),
				getEndYCoordinate(), SAP_MAT_PLANT_FONT_NAME, SAP_MAT_PLANT_FONT_SIZE);

		int listIndex = 0;

		//Up to 4 logos are shown
		for (int i = 0; i < SAP_MAT_PLANT_MAXIMUM_NUMBER_OF_LOGOS; i++) {

			//At the second position the generic logo is displayed, if exists.
			if (i == SAP_MAT_PLANT_SECOND_LOGO && matPlantEntity.getLogoGeneric() != null) {
				logoBlock.addLogoWithoutText(matPlantEntity.getLogoGeneric(),
						SAP_MAT_PLANT_GENERIC_LOGO_WIDTH_LOGICAL_UNITS);
			}
			else {

				//Otherwise the logos are displayed from the list.

				if (listIndex < sapMatPlantNormList.size()) {

					SapMatPlantNormEntity sapMatPlantNormEntity = sapMatPlantNormList.get(listIndex);

					final String text1;
					final String text2;
					final String text3;

					if (expShowZcode12Flag) {
						// trim is called in order to ensure we don't forward a String of " " when all zcodes are null => which would be not nice
						String zCode1 = StringUtils.nvl(sapMatPlantNormEntity.getZcode1());
						String zCode2 = StringUtils.nvl(sapMatPlantNormEntity.getZcode2());

						text1 = (zCode1 + " " + zCode2).trim();
						text2 = sapMatPlantNormEntity.getText1();
						text3 = sapMatPlantNormEntity.getText2();
					}
					else {
						text1 = sapMatPlantNormEntity.getText1();
						text2 = sapMatPlantNormEntity.getText2();
						text3 = sapMatPlantNormEntity.getText3();
					}

					logoBlock.addLogo(sapMatPlantNormEntity.getDeliveryNoteLogoImageName(),
							SAP_MAT_PLANT_LOGO_WIDTH_LOGICAL_UNITS,
							LogoUtils.getList(text1, text2, text3));

					listIndex++;
				}
				else {
					logoBlock.addBallast(SAP_MAT_PLANT_LOGO_WIDTH_LOGICAL_UNITS);
				}
			}
		}

		logoBlock.render(output);

	}

	/**
	 * Saves the position of the bottom Y coordinate of the Logo Section
	 */
	public void setLogoSectionEndYCoordinate(float space) {
		bottomYCoordinate = PdfLayoutGenerationTool.getCurrentYCoordinate(this.document) - space;
	}

	/**
	 * Saves the position of the top Y coordinate of the Logo Section
	 */
	public void setLogoSectionStartYCoordinate() {
		topYCoordinate = PdfLayoutGenerationTool.getCurrentYCoordinate(this.document) + LOGO_SECTION_UPPER_MARGIN_HEIGHT_IN_POINTS;
	}

}
