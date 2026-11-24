/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdftool.objects;
import java.util.List;

import com.crh.logon.pdftool.labelprovider.TableLabelProvider;

/**
 * A table object. Has a reference to a {@link TableLabelProvider} and has multiple
 * input objects.
 * @author AdNovum Informatik AG
 */
public class PdfTable extends AbstractPdfObject {
	
	private List<?> input;
	
	private TableLabelProvider labelProvider;
	
	private List<SimplePdfText> headers;
	
	private int numberOfColumns;
	
	/**
	 * Only used for calculating the table height in the material list of Delivery Note
	 */
	private int fontHeight = 11;

	public PdfTable(int numberOfColumns) {
		headers = null;
		this.numberOfColumns = numberOfColumns;
	}
	
	public PdfTable(List<SimplePdfText> headers, int fontHeight) {
		this.headers = headers;
		this.numberOfColumns = headers.size();
		this.fontHeight = fontHeight;
	}
	
	
	/**
	 * Returns the font size
	 * @return the fontSize
	 */
	public int getFontHeight() {
		return fontHeight;
	}

	public List<SimplePdfText> getHeaders() {
		return headers;
	}

	/**
	 * @return the input
	 */
	public List<?> getInput() {
		return input;
	}

	/**
	 * @param input the input to set
	 */
	public void setInput(List<?> input) {
		this.input = input;
	}

	/**
	 * @return the labelProvider
	 */
	public TableLabelProvider getLabelProvider() {
		return labelProvider;
	}

	/**
	 * @param labelProvider the labelProvider to set
	 */
	public void setLabelProvider(TableLabelProvider labelProvider) {
		this.labelProvider = labelProvider;
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

}
