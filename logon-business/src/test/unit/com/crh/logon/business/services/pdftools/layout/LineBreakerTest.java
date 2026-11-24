/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout;

import static org.hamcrest.CoreMatchers.anything;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

public class LineBreakerTest {
	/**
	 * Test method for {@link 
	 * LineBreaker#fitToWidth(int, java.lang.String, java.lang.String, GlyphWidthProvider)}.
	 */
	@Test
	public void testFitToWidthParameterCheck() {
		
		// test maxWidth
		try {
			LineBreaker.fitToWidth(-1, "", "", new GlyphWidthProvider() {
				
				@Override
				public double glyphWidth(char glyph) {
					// TODO Auto-generated method stub
					return 0;
				}
			});
			fail("Should throw IllegalArgumentException since maxWidth is < 0");
		}
		catch (IllegalArgumentException e) {
			anything("maxWidth is < 0 so IllegalArgumentException is thrown");
		}
		
		// test glyphProvider is null
		try {
			LineBreaker.fitToWidth(1, "text", "regexp", null);
			
			fail("Should throw IllegalArgumentException since glyphWidthProvider is null");
		}
		catch (IllegalArgumentException e) {
			anything("glyphWidthProvider is null so IllegalArgumentException is thrown");
		}
		
		
	}
	
	/**
	 * Test method for {@link 
	 * LineBreaker#fitToWidth(int, java.lang.String, java.lang.String, GlyphWidthProvider)}.
	 */
	@Test
	public void testFitToWidthWordDoesNotFitIntoLines() {
		
		String text = "1234567890";
		
		List<String> result = LineBreaker.fitToWidth(5, text, null, new GlyphWidthProvider() {
			
			@Override
			public double glyphWidth(char glyph) {
				// TODO Auto-generated method stub
				return 1;
			}
		});
		
		assertTrue("long word should be split into two lines", result.size() == 2);
		
	}

	/**
	 * Test method for {@link 
	 * LineBreaker#fitToWidth(int, java.lang.String, java.lang.String, GlyphWidthProvider)}.
	 */
	@Test
	public void testFitToWidthEmptyText() {
		
		String text = "";
		
		List<String> result = LineBreaker.fitToWidth(5, text, null, new GlyphWidthProvider() {
			
			@Override
			public double glyphWidth(char glyph) {
				// TODO Auto-generated method stub
				return 1;
			}
		});
		
		assertTrue("empty text results in empty list", result.size() == 0);
		
	}

	/**
	 * Test method for {@link 
	 * LineBreaker#fitToWidth(int, java.lang.String, java.lang.String, GlyphWidthProvider)}.
	 */
	@Test
	public void testFitToWidthBreakingWordsIntoLines() {
		
		String text = "word1 word2 word3 word4 word5";
		
		List<String> result = LineBreaker.fitToWidth(6, text, null, new GlyphWidthProvider() {
			
			@Override
			public double glyphWidth(char glyph) {
				// TODO Auto-generated method stub
				return 1;
			}
		});
		
		assertTrue("The text should be arranged into 5 lines", result.size() == 5);
		
	}
	
	/**
	 * Test method for {@link 
	 * LineBreaker#fitToWidth(int, java.lang.String, java.lang.String, GlyphWidthProvider)}.
	 */
	@Test
	public void testFitToWidthLoremIpsumStress() {
		
		String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
		
		List<String> result = LineBreaker.fitToWidth(1, text, null, new GlyphWidthProvider() {
			
			@Override
			public double glyphWidth(char glyph) {
				// TODO Auto-generated method stub
				return 10;
			}
		});
		
		assertTrue("Should finish", result.size() > 0);
		
		
		
	}
	
}
