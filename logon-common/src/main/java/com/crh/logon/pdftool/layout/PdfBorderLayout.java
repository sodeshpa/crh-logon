/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdftool.layout;

import com.crh.logon.pdftool.objects.PdfObject;


/**
 * If a {@link PdfObject} has multiple object within, the number of columns, spacing
 * and equal width support should be defined in an object of this class.
 * 
 * @author AdNovum Informatik AG
 */
public class PdfBorderLayout {
	
	private int numberOfColumns;
	private int horizontalSpacing;
	private int verticalSpacing;
	private boolean makeColumsEqualWidth;
	
	/**
	 * Default constructor. Number of columns is one
	 */
	public PdfBorderLayout() {
		this(1);
	}
	/**
	 * The number of columns is set to 1, if an invalid value entered (<1)
	 */
	public PdfBorderLayout(int numberOfColumns) {
		// silently set number of columns to 1 if value invalid (bellow one)
		this.numberOfColumns = numberOfColumns < 1 ? 1 : numberOfColumns;
		horizontalSpacing = 0;
		verticalSpacing = 0;
		makeColumsEqualWidth= false;
	}

	/**
	 * @return the numberOfColumns
	 */
	public int getNumberOfColumns() {
		return numberOfColumns;
	}

	/**
	 * @param numberOfColumns the numberOfColumns to set
	 */
	public void setNumberOfColumns(int numberOfColumns) {
		this.numberOfColumns = numberOfColumns;
	}

	/**
	 * @return the horizontalSpacing
	 */
	public int getHorizontalSpacing() {
		return horizontalSpacing;
	}

	/**
	 * @param horizontalSpacing the horizontalSpacing to set
	 */
	public void setHorizontalSpacing(int horizontalSpacing) {
		this.horizontalSpacing = horizontalSpacing;
	}

	/**
	 * @return the verticalSpacing
	 */
	public int getVerticalSpacing() {
		return verticalSpacing;
	}

	/**
	 * @param verticalSpacing the verticalSpacing to set
	 */
	public void setVerticalSpacing(int verticalSpacing) {
		this.verticalSpacing = verticalSpacing;
	}


	/**
	 * @return the makeColumsEqualWidth
	 */
	public boolean isMakeColumsEqualWidth() {
		return makeColumsEqualWidth;
	}

	/**
	 * @param makeColumsEqualWidth the makeColumsEqualWidth to set
	 */
	public void setMakeColumsEqualWidth(boolean makeColumsEqualWidth) {
		this.makeColumsEqualWidth = makeColumsEqualWidth;
	}

}
