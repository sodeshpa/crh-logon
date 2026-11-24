/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Breaks a String into one or more lines fit to the specified width
 */
public class LineBreaker {

	/**
	 * Regular Expression String for {@link String#split(String)} to break words on white space
	 */
	private static final String REGEXP_BREAK_ON_WHITE_SPACE = "[\\s]";
	
	/**
	 *  Space character
	 */
	private static final char SPACE = ' ';

	/**
	 * Returns the width of the word using the {@link GlyphWidthProvider}.
	 * @param maxWidth the maximum width of the word. If the word is wider than the maximum width, 
	 * it is split into two parts: one which fits into maxWidth and the rest. If the first character is wider than
	 * maxWidth, the first character will be returned as the fitting part of the word (to avoid endless loop)
	 * @param text the {@link String} containing the text. Should not be null or empty
	 * @param glyphWidthProvider the {@link GlyphWidthProvider}
	 * @return {@link WordLengthResult} the result of the method.
	 */
	private static WordLengthResult getWordLength(String text, double maxWidth,
			GlyphWidthProvider glyphWidthProvider) {

		// collects the characters for the part of the word which fits into the specified width
		StringBuilder fittingWord = new StringBuilder();

		WordLengthResult result = null;

		double width = 0;

		for (int i = 0; i < text.length(); i++) {

			char glyph = text.charAt(i);

			double glyphWidth = glyphWidthProvider.glyphWidth(glyph);

			// check if the current set of characters are fit to the specified width
			if (width + glyphWidth <= maxWidth) {
				width += glyphWidth;
				fittingWord.append(glyph);
			}
			// entire word does not fit into the specified width
			else {
				int startFrom = i;

				// in case the first character is too wide to fit into the line, this would cause endless loop.
				// to avoid that, the first character is always considered to be fit to the line
				if (startFrom == 0) {
					startFrom = 1;
					fittingWord.append(text.charAt(0));
				}
				String remainingWord = text.substring(startFrom);

				result = new WordLengthResult(false, width, fittingWord.toString(), remainingWord);

				return result;
			}
		}

		// entire word fits to maxLength
		result = new WordLengthResult(true, width, text, "");

		return result;
	}

	/**
	 * Breaks the text into lines whose lengths are less or equal than maxWidth.
	 * @param maxWidth the maximum width of the lines. Should be > 0
	 * @param text the text to be broken into lines. If null or empty, an empty {@link List} is returned
	 * @param regexp the regular expression used to split the text into words using {@link String#split(String)}.
	 * If null or empty, the default "[\\s]" is used (break on white spaces)
	 * @param glyphWidthProvider the implementor of the interface {@link GlyphWidthProvider} 
	 * provides the width information for Glyphs(characters)
	 * @return {@link List} of {@link String}s which will fit into lines whose width are less or equal than maxWidth.
	 * If a character is wider than the provided maxWidth, the character will be put into a new line (partial fit)
	 * @throw {@link IllegalArgumentException} if maxWidth <= 0, or {@link GlyphWidthProvider} is null
	 */
	public static List<String> fitToWidth(double maxWidth, String text, String regexp,
			GlyphWidthProvider glyphWidthProvider) {

		// if regular expression is not provided , defaults to white spaces
		if (regexp == null || regexp.length() == 0) {
			regexp = REGEXP_BREAK_ON_WHITE_SPACE;
		}

		// test if glyphWidthProvider is not null
		if (glyphWidthProvider == null) {
			throw new IllegalArgumentException("glyphWidthProvider should not be null");
		}

		// test if maxWidth is valid
		if (maxWidth <= 0) {
			throw new IllegalArgumentException("maxWidth should be > 0");
		}

		// result of the method
		List<String> result = new ArrayList<String>();

		// if the text is null or empty, return an empty list
		if (text == null || text.trim().length() == 0) {
			return result;
		}

		// the current line to work on
		StringBuilder currentLine = new StringBuilder();

		// width of the currentLine
		double currentLineWidth = 0;

		// width of the space character
		double spaceWidth = glyphWidthProvider.glyphWidth(SPACE);

		// break the text to "words"
		List<String> words = new LinkedList<String>(Arrays.asList(text.split(regexp)));

		// loop until the list of words are not empty
		while (words.size() > 0) {

			// get next word
			String nextWord = words.remove(0);

			// check the width of the next word and check if the whole word is too long for maxWidth
			WordLengthResult fitWidth = getWordLength(nextWord, maxWidth, glyphWidthProvider);

			// check if fit into one line 
			if (fitWidth.isFit()) {
				// check if the current line plus the new word is fit into the specified width

				// calculate the width of the line with the new word appended
				if (currentLineWidth > 0) {
					// if the line is not empty, add the width of space to the width of line
					currentLineWidth += spaceWidth;
				}
				double lineWidthWithNewWord = currentLineWidth + fitWidth.getWidth();

				if (lineWidthWithNewWord <= maxWidth) {
					// the line with the new word appended is shorter than the maxWidth
					if (currentLine.length() > 0) {
						// only append space, if the currentLine is not empty
						currentLine.append(SPACE);
					}
					currentLine.append(nextWord);
					currentLineWidth = lineWidthWithNewWord;
				}
				else {
					// the line with the new word appended is too long
					// put current line into result
					result.add(currentLine.toString());

					// clear current line
					currentLine.setLength(0);
					// append the next word into current line
					currentLine.append(nextWord);
					// set current line width to the 
					currentLineWidth = fitWidth.getWidth();
				}
			}
			// the word is to long to fit into a single line.
			else {
				// if there is any character in currentLine, flush it
				if (currentLine.length() > 0) {
					result.add(currentLine.toString());
					currentLine.setLength(0);
					currentLineWidth = 0;
				}

				// the first part of the word is put into a new line, the rest is put back into the list of words
				result.add(fitWidth.getFitWord());
				words.add(0, fitWidth.getRemainingWord());
			}

		}

		// at the end if there is remaining characters in the currentLine, flush them
		if (currentLine.length() > 0) {
			// flush currentLine
			result.add(currentLine.toString());
		}

		return result;
	}

}
