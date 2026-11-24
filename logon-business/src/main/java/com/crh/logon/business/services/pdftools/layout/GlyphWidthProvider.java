/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout;

/**
 * The provider of this interface should implement the
 * {@link #glyphWidth(char)} method to provide the width
 * of the glyphs(character) in points.
 */
public interface GlyphWidthProvider {

	/**
	 * Returns the width of the specified glyph(character)
	 * @param glyph the character to return the width of
	 * @return the width of the glyph(character) in points
	 */
	public double glyphWidth(char glyph);
	
}
