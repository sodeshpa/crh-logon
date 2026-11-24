/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout.utils;

import ch.nevis.jcan.print.fonts.Font;

import com.crh.logon.business.services.pdftools.layout.GlyphWidthProvider;

/**
 * Class to implement a {@link GlyphWidthProvider} with constructor specified {@link Font}
 */
public class GenericGlyphWidthProvider implements GlyphWidthProvider {
	
	private Font font;
	
	/**
	 * Constructor
	 * @param font {@link Font} to use to return the width of glyphs
	 */
	public GenericGlyphWidthProvider(Font font) {
		this.font = font;
	}

	/**
	 * @see GlyphWidthProvider#glyphWidth(char)
	 */
	@Override
	public double glyphWidth(char glyph) {
		
		// return the width of the specified glyph (character)
		return font.getWidth(glyph);
	}
	
	
	
}