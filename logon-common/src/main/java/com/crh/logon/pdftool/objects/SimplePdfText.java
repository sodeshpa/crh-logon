/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdftool.objects;

import com.crh.logon.pdftool.renderer.PdfFont;
import com.crh.logon.pdftool.renderer.PdfFontStore;

/**
 * The most basic of the concrete elements, contains a {@link PdfFont} and a text.
 * 
 * @author AdNovum Informatik AG
 */
public class SimplePdfText extends AbstractPdfObject {
	
	private PdfFont font;
	private String text;
	
	public static SimplePdfText create(String text) {
		return new SimplePdfText(text);
	}
	
	public static SimplePdfText create(String text, int height) {
		return new SimplePdfText(text, -1, height);
	}

	public SimplePdfText(String text) {
		this(text, -1, -1);
	}
	
	/**
	 * 
	 */
	public SimplePdfText(String text, int width, int height) {
		super(width, height);
		this.text = text == null ? "" : text;
		font = new PdfFont(PdfFontStore.Helvetica, PdfFontStore.SIZE_9);
	}
	
	/**
	 * @return the font
	 */
	public PdfFont getFont() {
		return font;
	}
	

	/**
	 * @param font the font to set
	 */
	public SimplePdfText setFont(PdfFont font) {
		this.font = font;
		return this;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public SimplePdfText setText(String text) {
		this.text = text;
		return this;
	}

}
