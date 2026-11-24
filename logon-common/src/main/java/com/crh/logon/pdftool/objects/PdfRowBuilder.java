/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdftool.objects;

import com.crh.logon.pdftool.layout.PdfBorderLayout;

/**
 * Helper class to build rows. Provides height definition, and a convenient way to add
 * and build rows for pdf-s.
 * 
 * @author AdNovum Informatik AG
 */
public class PdfRowBuilder {
	
	public class PdfRow extends AbstractPdfObject {
		
		/**
		 * Constructor
		 * @param numberOfColumns number of columns in the row
		 * @param height height of the row
		 * @param horizontalSpacing space between cells in horizontal direction
		 * @param verticalSpacing space between cell in vertical direction
		 */
		public PdfRow(int numberOfColumns, int height, int horizontalSpacing, int verticalSpacing) {
			super(-1, height);
			PdfBorderLayout layout = new PdfBorderLayout(numberOfColumns);
			layout.setHorizontalSpacing(horizontalSpacing);
			layout.setVerticalSpacing(verticalSpacing);
			setLayout(layout);
		}

	}
	
	PdfRow row = null;
	
	/**
	 * Constructor
	 * @param numberOfColumns number of columns in the row
	 * @param height height of the row
	 */
	public PdfRowBuilder(int numberOfColumns, int height) {
		this(numberOfColumns, height, 0, 0);
	}
	
	/**
	 * Adds a row to the page
	 * @param numberOfColumns number of columns in the row
	 * @param height height of the row
	 * @param horizontalSpacing horizontal space between cells?
	 * @param verticalSpacing vertiacal space between cells?
	 */
	public PdfRowBuilder(int numberOfColumns, int height, int horizontalSpacing, int verticalSpacing) {
		row = new PdfRow(numberOfColumns, height, horizontalSpacing, verticalSpacing);
	}
	
	/**
	 * Sets the {@link PdfObject} as the content of the next cell in the row. 
	 * If the content to be added would not fit to the row, throws RuntimeException.
	 * @param object the {@link PdfObject}
	 * @return the {@link PdfRowBuilder}
	 */
	public PdfRowBuilder addElement(PdfObject object) {
		if (row.getChildren().size() >= row.getLayout().getNumberOfColumns()) {
			throw new RuntimeException(
					"Number of elements cannot exceed the number of columns defined");
		}
		row.getChildren().add(object);
		return this;
	}
	
	/**
	 * Adds empty text nodes to the cells not defined in the row.
	 * @return the {@link PdfRow}
	 */
	public PdfRow build() {
		// Make sure all cells are added. Missing cells are added as empty strings.
		while (row.getChildren().size() < row.getLayout().getNumberOfColumns()) {
			row.getChildren().add(new SimplePdfText(""));
		}
		return row;
	}

}
