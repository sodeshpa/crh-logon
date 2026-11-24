/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout;

import java.util.List;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.pdftool.layout.PdfBorderData;
import com.crh.logon.pdftool.layout.PdfBorderData.HOrientation;
import com.crh.logon.pdftool.layout.PdfBorderLayout;
import com.crh.logon.pdftool.objects.AbstractPdfObject;
import com.crh.logon.pdftool.objects.PdfObject;
import com.crh.logon.pdftool.objects.PdfRowBuilder;
import com.crh.logon.pdftool.objects.SimplePdfText;
import com.crh.logon.pdftool.renderer.PdfFont;
import com.crh.logon.pdftool.renderer.PdfFontStore;

/**
 * A simple container type UI element. Header goes to the upper left corner,
 * header data to the upper right. One new line for each of the content.
 * 
 * @author AdNovum Informatik AG
 */
public class TextListPart extends AbstractPdfObject {

	/**
	 * Logger
	 */
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(TextListPart.class);

	/**
	 * True if the text list is empty
	 */
	private boolean empty;

	/**
	 * Defaults to Helvetica 9pt
	 */
	private PdfFont font = new PdfFont(PdfFontStore.Helvetica, PdfFontStore.SIZE_9);
	
	/**
	 * Default height of Helvetica 9pt
	 */
	private int fontHeight = 11;

	/**
	 * Creates a placeholder {@link TextListPart}
	 */
	public TextListPart() {
		setLayout(new PdfBorderLayout(1));
		this.empty = true;
		setPlannedWidth(fontHeight);
		// add an empty SimplePdfText to avoid problems in JcanPdfRenderer
		getChildren().add(new SimplePdfText("", this.fontHeight, this.fontHeight).setFont(font));
	}

	/**
	 * Constructor for a text list without header.
	 * @param width Width of text in Points
	 * @param fontHeight Height of text in Points
	 * @param font {@link PdfFont} to be used for rendering
	 * @param content {@link List} of {@link String}s for content
	 */
	public TextListPart(int width, int fontHeight, PdfFont font, List<String> content) {

		this.empty = false;
		this.font = font;
		this.fontHeight = fontHeight;

		setLayout(new PdfBorderLayout(1));

		for (String string : content) {
			getChildren().add(new SimplePdfText(string, width, fontHeight).setFont(font));
		}

		setPlannedWidth(width);
	}

	/**
	 * Constructor for TextListPart with header.
	 * @param width Width of the {@link TextListPart} in Points
	 * @param fontHeight the height of the <code>font</font>
	 * @param font {@link PdfFont} to be used for rendering
	 * @param headerLabel Left aligned text for header
	 * @param headerData Right aligned text for header
	 * @param content {@link List} of {@link String}s for content
	 */
	public TextListPart(int width, int fontHeight, PdfFont font, String headerLabel, String headerData,
			List<String> content) {

		this.font = font;
		this.fontHeight = fontHeight;
		this.empty = false;

		setLayout(new PdfBorderLayout(1));

		// set ratio based on width
		double left = headerLabel.length();
		double right = headerData.length();
		double leftWidth = -1;
		double rightWidth = -1;
		double total = left + right;
		if (total > 0) {
			leftWidth = (left / total) * width;
			rightWidth = (right / total) * width;
			// to avoid jcan-print table layout null pointer exception...
			leftWidth = leftWidth <= 0 ? 1 : leftWidth;
			rightWidth = rightWidth <= 0 ? 1 : rightWidth;
		}
		PdfObject rightHeader = new SimplePdfText(headerData, (int) rightWidth, this.fontHeight).setFont(font);
		rightHeader.setLayoutData(new PdfBorderData(HOrientation.RIGHT));

		// adds the header
		getChildren().add(
				new PdfRowBuilder(2, this.fontHeight).addElement(
						new SimplePdfText(headerLabel, (int) leftWidth, this.fontHeight).setFont(font)).addElement(
						rightHeader).build());

		// adds content
		for (String string : content) {
			getChildren().add(SimplePdfText.create(string).setFont(font));
		}

		setPlannedWidth(width);
	}

	/**
	 * Returns the position of the underline in Points for the font for rendering {@link TextListPart}
	 * @return the position of the underline in Points.
	 */
	public double getUnderlinePosition() {
		return JcanPdfRenderer.getFontHeight(this.font);
	}

	/**
	 * Returns the height of the list (calculated by the font height * number of lines)
	 * @return the height of the list
	 */
	public double getListHeight() {
		return getNumberOfLines() * this.fontHeight;
	}

	/**
	 * Returns the number of lines in the text list (header counts as one line) 
	 * @return the number of lines in the text list
	 */
	public int getNumberOfLines() {
		List<PdfObject> children = getChildren();

		int result = 0;

		if (children != null) {
			return children.size();
		}

		return result;
	}

	/**
	 * Returns true, if the Text List is empty (just a placeholder)
	 * @return the empty
	 */
	public boolean isEmpty() {
		return empty;
	}
}
