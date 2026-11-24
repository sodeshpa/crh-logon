/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout.logoutils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;

/**
 * Data for defining a Logo image and text to be rendered onto the Delivery Note
 */
public class LogoWithText {

	/**
	 * Text lines displayed under the logo image
	 */
	private List<String> textLines;

	/**
	 * width of the logo in logical units
	 */
	private double logicalWidth;

	/**
	 * Reference to the LogoBlock (parent)
	 */
	private LogoBlock logoBlock;

	/**
	 * Name of the logo image without the language code (e.g.: pdf_ce_logo)
	 */
	private String logoImageName;

	/**
	 * Constructor
	 * @param logoBlock the parent {@link LogoBlock}, should be non-null
	 * @param logoImageName the name of the logo image (e.g.: "pdf_ce_logo") without language code, should be non-null
	 * @param logicalWidth the width of the logo when displayed in logical units
	 * @param texts the lines of texts
	 */
	protected LogoWithText(LogoBlock logoBlock, String logoImageName, double logicalWidth,
			List<String> texts) {
		super();
		this.textLines = new ArrayList<String>();
		Validate.notNull(logoBlock, "logoBlock should be non-null");
		this.logoBlock = logoBlock;
		Validate.notNull(logoImageName, "logoImageName should be non-null");
		this.logoImageName = logoImageName;

		if (texts != null) {
			for (String line: texts) {
				// do not add null lines
				if (line != null) {
					this.textLines.add(line);
				}
			}
		}
		this.logicalWidth = logicalWidth;
	}

	/**
	 * Returns the list of Strings for the text under the logo images
	 * @return the list of Strings for the text under the logo images
	 */
	protected List<String> getTexts() {
		return textLines;
	}

	/**
	 * Returns the logical width of the logo image
	 * @return the logical width of the logo image
	 */
	protected double getLogicalWidth() {
		return logicalWidth;
	}

	/**
	 * Returns the parent {@link LogoBlock}
	 * @return the parent {@link LogoBlock}
	 */
	protected LogoBlock getLogoBlock() {
		return logoBlock;
	}

	/**
	 * Returns the name of the logo image without language code
	 * @return the name of the logo image without language code
	 */
	protected String getLogoImageName() {
		return logoImageName;
	}

	/**
	 * Returns the name of the logo image with or without the language code
	 * @param withLanguageCode if true, the language code (if not null) will be 
	 * appended at the end of the logo image name ("pdf_ce_logo" + "de" become "pdf_ce_logo_de")
	 * @return the name of the logo image
	 */
	protected String getLogoImageName(boolean withLanguageCode) {

		String result = getLogoImageName();

		if (withLanguageCode) {
			String countryCode = getLogoBlock().getLanguageCode();
			if (countryCode != null) {
				result += "_" + countryCode;
			}
		}

		return result;
	}

	/**
	 * Returns the width of the Logo image in Points 
	 * @return the width of the Logo image in Points
	 */
	protected double getLogoWidthInPoints() {
		return logicalWidth * getLogoBlock().getScaleFactor();
	}

	/**
	 * Returns true, if the logo has text lines
	 * @return true, if the logo has text lines
	 */
	protected boolean hasText() {
		return textLines.size() > 0;
	}
}
