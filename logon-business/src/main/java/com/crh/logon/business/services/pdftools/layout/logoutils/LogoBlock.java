/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout.logoutils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.Validate;

import ch.nevis.jcan.print.base.Color;
import ch.nevis.jcan.print.base.OutputGen;
import ch.nevis.jcan.print.base.PrinterException;
import ch.nevis.jcan.print.fonts.Font;
import ch.nevis.jcan.print.fonts.FontFactory;
import ch.nevis.jcan.print.images.Image;
import ch.nevis.jcan.print.layout.Container;
import ch.nevis.jcan.print.layout.Paragraph;

import com.crh.logon.business.services.localinterfaces.GenericBlobServiceLocalInterface;
import com.crh.logon.business.services.pdftools.layout.GlyphWidthProvider;
import com.crh.logon.business.services.pdftools.layout.LineBreaker;
import com.crh.logon.business.services.pdftools.layout.utils.GenericGlyphWidthProvider;
import com.crh.logon.business.services.reporting.PDFUtils;
import com.crh.logon.pdftool.PdfDocumentWrapper;
import com.crh.logon.persistence.dto.BlobDTO;

/**
 * Defines a block of Material Logos to be rendered onto the Delivery Note
 */
public class LogoBlock {

	/**
	 * Whitespace to be appended at the end of each line (except the last one) of logo text when merged to a single String 
	 */
	private static final String LOGO_TEXT_LINES_SEPARATOR_STRING_WHITESPACE = " ";

	/**
	 * Sum of logical widths of the not displayed logo images
	 */
	private double ballast;
	
	/**
	 * Key vale for loading logo image 
	 */
	private static final String IMAGE_NAME = "imageName";

	/**
	 * Entity name for loading logo image
	 */
	private static final String IMAGE_ENTITY = "ImageEntity";

	/**
	 * Change to true to display debug lines for Logo area/Logo Image/Text bounding boxes
	 */
	private static final boolean DEBUG_PDF = false;

	/**
	 * Thickness of the rectangle rendered when {@link #DEBUG_PDF} is true
	 */
	private static final double DEBUG_PDF_IMAGE_BOUNDING_BOX_LINE_THICKNESS = 0.5;

	/**
	 * This constant is used to multiply the height of the font to get the real vertical space occupied by the line of text rendered with the font.
	 */
	private static final double FONT_SIZE_REAL_HEIGHT_SCALE_FACTOR = 1.2;

	/**
	 * Thickness of the rectangle for the bounding box of the Logo image and text when {@link #DEBUG_PDF} is enabled
	 */
	private static final int LOGO_IMAGE_AND_TEXT_BOUNDING_BOX_RECTANGLE_THICKNESS = 1;

	/**
	 * The space under the logo image is calculated by multiplying this constant with the height of one line
	 */
	private static final double SPACE_BETWEEN_LOGO_AND_TEXT_PROPORTIONALLY_TO_HEIGHT_OF_ONE_LINE = 0.5;

	/**
	 * The vertical space between lines of text under the logo images
	 */
	private static final int VERTICAL_SPACE_BETWEEN_LINES_OF_TEXT = 0;

	/**
	 * The {@link GenericBlobServiceLocalInterface}
	 */
	private GenericBlobServiceLocalInterface blobService;

	/**
	 * The document for getting the position of the start and end Y coordinate of the logo section
	 */
	private PdfDocumentWrapper document;

	/**
	 * Name of the font for rendering the text below the logo images
	 */
	private String fontName;

	/**
	 * Size of the font for rendering the text below the logo images
	 */
	private double fontSize;

	/**
	 * Height of one text line in points
	 */
	private double heightOfOneLineInPoints;

	/**
	 * The language code from the locale for selecting language code dependent logo images
	 */
	private String languageCode;

	/**
	 * The Y coordinate of the bottom of the logo area
	 */
	private double logoBlockBottomYCoordinateInPoints;

	/**
	 * The X coordinate of the left side of the logo area in Points
	 */
	private double logoBlockLeftXCoordinateInPoints;

	/**
	 * The X coordinate of the right side of the logo area in Points
	 */
	private double logoBlockRightXCoordinateInPoints;

	/**
	 * The top Y coordinate of the Logo Area in Points
	 */
	private double logoBlockTopYCoordinateInPoints;

	/**
	 * The width of the logo area in logical units
	 */
	private double logoBlockTotalLogicalWidth;

	/**
	 * List of {@link LogoWithText}s to be rendered
	 */
	private List<LogoWithText> logoDefinitions;

	/**
	 * The logical units are multiplied with this number to get the Point units
	 */
	private double scaleFactor;

	/**
	 * The vertical space between the logo image and the text in Points
	 */
	private double spaceBetweenLogoImageAndTextInPoints;

	/**
	 * The horizontal space between the logos in logical units
	 */
	private double spaceBetweenLogosLogicalWidth;

	/**
	 * The {@link Font} for rendering the text under the Logo images
	 */
	private Font textFont;

	/**
	 * Height of the text under the logo images
	 */
	private double textHeightInPoints;

	/**
	 * Constructor
	 * @param document the {@link PdfDocumentWrapper}, should be non-null
	 * @param languageCode the language code of the {@link Locale} for finding the logo images, could be null
	 * @param blobService the {@link GenericBlobServiceLocalInterface}, should be non-null
	 * @param logoBlockLeftXCoordinateInPoints the X coordinate of the left side of the area for displaying logos in the pdf.
	 * @param logoBlockRightXCoordinateInPoints the X coordinate of the right side of the area for displaying logos in the pdf.
	 * @param spaceLogicalWidth The width of the space between logo images in logical units
	 * @param totalLogicalWidth The width of the area for displaying logo images (including the spaces) in logical units
	 * @param logoBlockTopYCoordinateInPoints the top Y coordinate of the area for displaying logos
	 * @param logoBlockBottomYCoordinateInPoints  the bottom Y coordinate of the area for displaying logos
	 * @param fontName the name of the font for rendering the text lines under the logo images (e.g.: "Helvetica"), should be non-null
	 * @param fontSize the size of the font fot rendering the text lines under the logo images (e.g.: 7)
	 */
	public LogoBlock(PdfDocumentWrapper document, String languageCode,
			GenericBlobServiceLocalInterface blobService, double logoBlockLeftXCoordinateInPoints,
			double logoBlockRightXCoordinateInPoints, double spaceLogicalWidth,
			double logoBlockTopYCoordinateInPoints,
			double logoBlockBottomYCoordinateInPoints, String fontName, double fontSize) {
		super();

		Validate.notNull(document, "document should be non null");
		this.document = document;

		Validate.notNull(blobService, "blobService should not be null");
		Validate.notNull(fontName, "fontName should be non null");
		Validate.notEmpty(fontName, "fontName should be non empty");

		if (languageCode != null) {
			Validate.notEmpty(languageCode, "If countryCode is not null, it should not be empty");
		}

		if (logoBlockLeftXCoordinateInPoints >= logoBlockRightXCoordinateInPoints) {
			throw new IllegalArgumentException(
					"logoBlockLeftXCoordinateInPoints should be < than logoBlockRightXCoordinateInPoints");
		}

		this.logoDefinitions = new ArrayList<LogoWithText>();
		this.logoBlockLeftXCoordinateInPoints = logoBlockLeftXCoordinateInPoints;
		this.logoBlockRightXCoordinateInPoints = logoBlockRightXCoordinateInPoints;
		this.spaceBetweenLogosLogicalWidth = spaceLogicalWidth;
		
		this.logoBlockTopYCoordinateInPoints = logoBlockTopYCoordinateInPoints;
		this.logoBlockBottomYCoordinateInPoints = logoBlockBottomYCoordinateInPoints;
		this.fontName = fontName;
		this.fontSize = fontSize;
		this.textFont = FontFactory.getInstance().createFont(this.fontName, (float) this.fontSize);
		this.heightOfOneLineInPoints = (textFont.getSize() * FONT_SIZE_REAL_HEIGHT_SCALE_FACTOR);
		this.spaceBetweenLogoImageAndTextInPoints = heightOfOneLineInPoints
				* SPACE_BETWEEN_LOGO_AND_TEXT_PROPORTIONALLY_TO_HEIGHT_OF_ONE_LINE;
		
		this.blobService = blobService;
		this.languageCode = languageCode;

	}
	
	/**
	 * <p>Calculates the {@link #textHeightInPoints} variable. This is the space below the logos which is big enough for the longest text.
	 * <p><b>Should be called after {@link #calculateScaleFactor()}</b>
	 * <p>This method does the followings:
	 * <ul>
	 * <li>Concatenates the text lines for the Logo into one single line
	 * <li>Breaks the singe line text into multiple lines in a way that 
	 * each line will fit to the available space (the scaled logo width). Modifies the {@link LogoWithText#textLines} according to the new layout.
	 * <li>At the end finds the logo with the most number of text lines and calculates the {@link #textHeightInPoints} variable.
	 * </ul>
	 */
	private void calculateTextHeightInPoints() {
		if (this.scaleFactor == 0) {
			throw new IllegalStateException(
					"scaleFactor should be calculated before calling this method. Use calculateScaleFactor() !");
		}

		GlyphWidthProvider glyphWidthProvider = new GenericGlyphWidthProvider(this.textFont);

		// iterate over all logos
		for (LogoWithText logo : getLogoDefinitions()) {

			boolean fitInLine = true;

			List<String> originalLines = logo.getTexts();

			if (originalLines != null) {
				for (String line : originalLines) {

					List<String> fittingLines = LineBreaker.fitToWidth(logo.getLogoWidthInPoints(),
							line, null, glyphWidthProvider);

					if (fittingLines.size() > 1) {
						// does not fit in one single line
						fitInLine = false;
						break;
					}

				}

				if (!fitInLine) {
					// rearrange text
					String concatenatedLines = concatenate(originalLines);
					List<String> rearrangedLines = LineBreaker.fitToWidth(logo.getLogoWidthInPoints(),
							concatenatedLines, null, glyphWidthProvider);
					logo.getTexts().clear();
					logo.getTexts().addAll(rearrangedLines);
				}
			}
		}
		
		int maximumNumberOfLines = 0;
		
		// second iteration to get the number of lines
		for (LogoWithText logo : getLogoDefinitions()) { 
			if (logo.getTexts() != null) {
				if (logo.getTexts().size() > maximumNumberOfLines) {
					maximumNumberOfLines = logo.getTexts().size();
				}
			}
			
		}
		
		// calculate the height of the text block
		this.textHeightInPoints = heightOfOneLineInPoints * maximumNumberOfLines + 
				VERTICAL_SPACE_BETWEEN_LINES_OF_TEXT * (maximumNumberOfLines - 1) 
				+ spaceBetweenLogoImageAndTextInPoints;
		
	}
	
	/**
	 * Merges multiple lines of text into one String.
	 * @param originalLines the {@link List} of {@link Strings}
	 * @return the {@link String} containing all lines.
	 */
	private String concatenate(List<String> originalLines) {
		StringBuilder sb = new StringBuilder();
		int actualLineIndex = 0;

		List <String> normalizedLines = new ArrayList<String>();
		
		// create a normalized list which contains only non-null, non-empty, trimmed lines
		for (String line: originalLines) {
			if (line != null) {
				line = line.trim();
				if (line.length() > 0) {
					normalizedLines.add(line);
				}
			}
		}

		int numberOfLines = normalizedLines.size();
		for (String line : normalizedLines) {

			sb.append(line);

			// avoid adding separator for the last line
			if (actualLineIndex < numberOfLines - 1) {
				
				// append space at the end
				sb.append(LOGO_TEXT_LINES_SEPARATOR_STRING_WHITESPACE);
				

			}
			actualLineIndex++;
		}
		return sb.toString();
	}

	/**
	 * Calculates the total logical width of the Logo block and the scale factor.
	 * Should be called after all Logo definitions has been added.
	 */
	private void calculateScaleFactor() {
		double totalLogicalWidth = ballast;
		for (LogoWithText logo: getLogoDefinitions()) {
			totalLogicalWidth += logo.getLogicalWidth();
		}
		totalLogicalWidth += this.spaceBetweenLogosLogicalWidth * (getLogoDefinitions().size() - 1);
		
		this.logoBlockTotalLogicalWidth = totalLogicalWidth;
		this.scaleFactor = getLogoBlockWidthInPoints() / logoBlockTotalLogicalWidth;
	}

	/**
	 * Adds a Logo with text to the Logo block, if the logoImageName is not null 
	 * @param logoImageName the name of the logo image without language code, if null no logo is added to the logo block 
	 * @param logicalWidth the logical width of the logo image 
	 * @param texts the lines of text to be displayed under the logo image, should be non-null. Use {@link #addLogoWithoutText(String, double)} when no text should be added.
	 */
	public void addLogo(String logoImageName, double logicalWidth, List<String> texts) {
		if (logoImageName != null) {
			Validate.notNull(texts, "Texts should be non null");
			getLogoDefinitions().add(new LogoWithText(this, logoImageName, logicalWidth, texts));
		}
		else {
			ballast += logicalWidth;
		}
	}
	
	/**
	 * Adds a placeholder to the Logo block
	 * @param logicalWidth the width of the placeholder in logical units (the logical width of the logo image
	 * which is not displayed)
	 */
	public void addBallast(double logicalWidth) {
		ballast+= logicalWidth;
	}

	/**
	 * Adds Logo without text to the Logo block, if the logoImageName is not null 
	 * @param logoImageName  the name of the logo image without language code, if null no logo is added to the logo block  
	 * @param logicalWidth the logical width of the logo image 
	 */
	public void addLogoWithoutText(String logoImageName, double logicalWidth) {
		if (logoImageName != null) {
			getLogoDefinitions().add(new LogoWithText(this, logoImageName, logicalWidth, null));
		}
		else {
			ballast += logicalWidth;
		}
	}

	/**
	 * Converts the Y coordinate of the Lines to text boxes
	 * @param output {@link OutputGen}
	 * @param y the Y coordinate according to the line drawing coordinate system (y is zero at the bottom edge of the paper)
	 * @return the Y coordinate according to the text box coordinate system (y is zero at the top margin)
	 */
	private double convertLineCoordinateToContainerCoordinate(OutputGen output, double y) {
		return getDocumentTopY() - y - output.getTopMargin();
	}

	/**
	 * Return the top y coordinate of the document (height of the pdf in Points)
	 * @return the top y coordinate of the document
	 */
	private double getDocumentTopY() {
		return document.getHeight();
	}

	/**
	 * Returns the font name for rendering the text under the logos
	 * @return  the font name for rendering the text under the logos
	 */
	public String getFontName() {
		return fontName;
	}

	/**
	 * Returns the font size for rendering the text under the logos
	 * @return the font size for rendering the text under the logos
	 */
	public double getFontSize() {
		return fontSize;
	}

	/**
	 * Returns the language code of the Locale
	 * @return the language code of the Locale
	 */
	public String getLanguageCode() {
		return languageCode;
	}

	/**
	 * Returns the Y coordinate of the bottom of the Logo area
	 * @return the Y coordinate of the bottom of the Logo area
	 */
	private double getLogoBlockBottomYCoordinateInPoints() {
		return logoBlockBottomYCoordinateInPoints;
	}

	/**
	 * Returns the height of the Logo block area in Points
	 * @return the height of the Logo block area in Points
	 */
	private double getLogoBlockHeightInPoints() {
		return  getLogoBlockTopYCoordinateInPoints() - getLogoBlockBottomYCoordinateInPoints();
	}

	/**
	 * Returns the left X coordinate of the logo block in Points
	 * @return the left X coordinate of the logo block in Points
	 */
	private double getLogoBlockLeftXCoordinateInPoints() {
		return logoBlockLeftXCoordinateInPoints;
	}

	/**
	 * Returns the top Y coordinate of the logo block in Points
	 * @return the top Y coordinate of the logo block in Points
	 */
	private double getLogoBlockTopYCoordinateInPoints() {
		return logoBlockTopYCoordinateInPoints;
	}

	/**
	 * Returns the width of the Logo block in Points
	 * @return  the width of the Logo block in Points
	 */
	private double getLogoBlockWidthInPoints() {
		return logoBlockRightXCoordinateInPoints - logoBlockLeftXCoordinateInPoints;
	}

	/**
	 * @return the logoDefinitions
	 */
	private List<LogoWithText> getLogoDefinitions() {
		return logoDefinitions;
	}

	/**
	 * Returns the scale factor for the logical units to be converted to Points
	 * @return the scale factor for the logical units to be converted to Points
	 */
	protected double getScaleFactor() {
		return scaleFactor;
	}

	/**
	 * Returns the scale factor for the image to fit to a space given by maxWidth and maxHeight.
	 * 
	 * @param image the image to scale
	 * @param maxWidth the available space in horizontal direction
	 * @param maxHeight the available space in vertical direction
	 * @return the scale factor, which scales the image to fit the available width or height (or both)
	 */
	private double getScaleMultiplier(Image image, double maxWidth, double maxHeight) {
		int width = image.getWidth();
		int height = image.getHeight();

		double scaleWidth = maxWidth / width;
		double scaleHeight = maxHeight / height;

		return Math.min(scaleWidth, scaleHeight);
	}

	/**
	 * @return the spaceLogicalWidth
	 */
	private double getSpaceBetweenLogosInPoints() {
		return spaceBetweenLogosLogicalWidth * getScaleFactor();
	}

	/**
	 * Returns the {@link Font} for text rendering
	 * @return the {@link Font} for text rendering
	 */
	private Font getTextFont() {
		return textFont;
	}

	/**
	 * Renders the Logos previously added to this block 
	 * @param output the {@link OutputGen}
	 * @throws PrinterException if pdf generation failed
	 */
	public void render(OutputGen output) throws PrinterException {

		calculateScaleFactor();
		calculateTextHeightInPoints();
		
		double logoX = getLogoBlockLeftXCoordinateInPoints();

		for (LogoWithText logoBlock : getLogoDefinitions()) {

			logoX = renderLogoAndText(output, logoBlock, logoX);
		}
	}

	/**
	 * Renders the image into the pdf
	 * @param output the {@link OutputGen}
	 * @param blobDTO the {@link BlobDTO}
	 * @param logoXPosition the X coordinate of the left side of the image in Points
	 * @param availableHorizontalSpace the width of the Image in the pdf in Points
	 * @param availableVerticalSpace the height of the available space for rendering the image, including the text height in the pdf in Points (height of the Logo Area)
	 * @param textHeight the height of the text (including space between the logo image and the first text line) in Points

	 * @throws PrinterException
	 */
	private void renderImage(OutputGen output, BlobDTO blobDTO, double logoXPosition,
			double availableHorizontalSpace, double availableVerticalSpace, double textHeight)
			throws PrinterException {

		byte[] imageData = blobDTO.getStream();
		Image image = PDFUtils.createImage(imageData);
		if (image == null) {
			return;
		}

		double scale = getScaleMultiplier(image, availableHorizontalSpace, availableVerticalSpace
				- textHeight);

		double y = getLogoBlockBottomYCoordinateInPoints() + textHeight;

		output.showImage(image, logoXPosition, y, scale, scale);

		if (DEBUG_PDF) {
			output.drawRect(Color.RED, logoXPosition, y, 
					image.getWidth() * scale, image.getHeight() * scale,
					DEBUG_PDF_IMAGE_BOUNDING_BOX_LINE_THICKNESS);
		}
	}

	/**
	 * Renders the Logo image and text into the pdf
	 * @param output the {@link OutputGen}
	 * @param logo the {@link LogoWithText}
	 * @param logoXCoordinate the X coordinate of the left side of the Logo image and the text
	 * @return the width of the logo image and the horizontal space between logo images
	 * @throws PrinterException if pdf generation failed
	 */
	private double renderLogoAndText(OutputGen output, LogoWithText logo, double logoXCoordinate)
			throws PrinterException {

		if (DEBUG_PDF) {
			output.drawRect(Color.AQUA, 
					logoXCoordinate, 
					getLogoBlockTopYCoordinateInPoints(),
					logo.getLogoWidthInPoints(), 
					-getLogoBlockHeightInPoints(),
					LOGO_IMAGE_AND_TEXT_BOUNDING_BOX_RECTANGLE_THICKNESS);
		}

		if (logo.getLogoImageName() != null) {

			double heightOfTextForLogo = 0;

			if (logo.hasText()) {
				heightOfTextForLogo = textHeightInPoints;

				double textXCoordinate = logoXCoordinate - output.getLeftMargin();

				renderText(output, textXCoordinate, logo);
			}

			renderLogoImage(output, logo, logoXCoordinate, heightOfTextForLogo);

			logoXCoordinate += logo.getLogoWidthInPoints() + getSpaceBetweenLogosInPoints();
		}

		return logoXCoordinate;
	}

	/**
	 * Renders the Logo image into the pdf
	 * @param output the {@link OutputGen}
	 * @param logo the {@link LogoWithText}
	 * @param x the X coordinate of the left side of the Logo image in Points in the Pdf sheet
	 * @param textHeight the height of the text to be rendered below the logo image (or 0 if no text is rendered)
	 * @throws PrinterException if pdf generation failed
	 */
	private void renderLogoImage(OutputGen output, LogoWithText logo, double x, double textHeight)
			throws PrinterException {

		String localizedName = logo.getLogoImageName(true);

		BlobDTO logoImageBinaryData = blobService.fetchBlobWithStringKeyValue(IMAGE_ENTITY,
				IMAGE_NAME, localizedName, true);

		PDFUtils.checkImageIntegrity(logoImageBinaryData, localizedName);

		if (logoImageBinaryData.getStream() != null) {
			renderImage(output, logoImageBinaryData, x, logo.getLogoWidthInPoints(),
					getLogoBlockHeightInPoints(), textHeight);
		}
		else {

			String nonLocalizedImageName = logo.getLogoImageName(false);

			// if not found try to load the non localized version of image
			logoImageBinaryData = blobService.fetchBlobWithStringKeyValue(IMAGE_ENTITY,
					IMAGE_NAME, nonLocalizedImageName, true);
			PDFUtils.checkImageIntegrity(logoImageBinaryData, nonLocalizedImageName);
			if (logoImageBinaryData.getStream() != null) {
				renderImage(output, logoImageBinaryData, x, logo.getLogoWidthInPoints(),
						getLogoBlockHeightInPoints(), textHeight);
			}

		}
	}

	/**
	 * Renders the text lines to the pdf
	 * @param output the {@link OutputGen}
	 * @param textXPosition the X coordinate of the left side of the text
	 * @param logo the {@link LogoWithText}
	 * @throws PrinterException if pdf generation failed
	 */
	private void renderText(OutputGen output, double textXPosition, LogoWithText logo)
			throws PrinterException {

		Container labelContainer = new Container();

		double textYPosition = convertLineCoordinateToContainerCoordinate(output,
				getLogoBlockBottomYCoordinateInPoints() + textHeightInPoints);

		labelContainer.setAbsPos(textXPosition, textYPosition);

		Paragraph labelParagraph = null;

		boolean firstLineOfText = true;
		double spaceBeforeText = 0;

		for (String text : logo.getTexts()) {

			labelParagraph = addParagraph(text);
			if (labelParagraph != null) {

				if (firstLineOfText) {
					spaceBeforeText = spaceBetweenLogoImageAndTextInPoints;
					firstLineOfText = false;
				}
				else {
					spaceBeforeText = VERTICAL_SPACE_BETWEEN_LINES_OF_TEXT;
				}

				labelParagraph.setRelPos(0, spaceBeforeText);

				labelParagraph.attachTo(labelContainer);
			}

		}

		labelContainer.render(output);
	}

	/**
	 * Constructs a new {@link Paragraph} and returns it. If the argument is null, returns null.
	 * @param text the {@link String} to be contained by the new {@link Paragraph}
	 * @return the created {@link Paragraph}
	 */
	private Paragraph addParagraph(String text) {
		if (text != null) {
			return new Paragraph(getTextFont(), text);
		}
		else return null;
	}

}
