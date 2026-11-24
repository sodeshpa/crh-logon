/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout;

/**
 * Value Object to store the result of LineBreaker.getWordLength()
 */
class WordLengthResult {

	/**
	 * True, if the word's width is less than equal to the specified maximum width
	 */
	private boolean fit;

	/**
	 * The first part (or the whole) word which fits into the specified maximum width
	 */
	private String fitWord;

	/**
	 * If fit == false, this contains the second part of the word, which does not fit into the specified width
	 */
	private String remainingWord;

	/**
	 * The width of the word, if fit == true
	 */
	private double width;

	/**
	 * Constructor
	 * @param fit true, if the entire word is fit into the specified width
	 * @param width the width of the word.
	 * @param fitWord contains the {@link String} which fits into the specified width, 
	 * or if the word does not fit, contains the fitting part
	 * @param remainingWord if the word does not fit, contains the remaining part
	 */
	public WordLengthResult(boolean fit, double width, String fitWord, String remainingWord) {
		super();
		this.fit = fit;
		this.width = width;
		this.fitWord = fitWord;
		this.remainingWord = remainingWord;
	}

	/**
	 * @return the part of the word which fits into the specified maximum width
	 */
	public String getFitWord() {
		return fitWord;
	}

	/**
	 * @return the part of the word which does not fit into the specified maximum width or an empty {@link String}, 
	 * if the whole word fits.
	 */
	public String getRemainingWord() {
		return remainingWord;
	}

	/**
	 * @return the width of the word, if the entire word fits into the specified maximum width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * @return true, if the entire word's width is less or equal than the specified maximum width, false otherwise
	 */
	public boolean isFit() {
		return fit;
	}
}