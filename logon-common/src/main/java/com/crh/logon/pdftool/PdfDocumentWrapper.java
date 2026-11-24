/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdftool;

import java.util.ArrayList;
import java.util.List;

import com.crh.logon.pdftool.layout.PdfBorderLayout;
import com.crh.logon.pdftool.objects.PdfObject;
import com.crh.logon.pdftool.objects.PdfRowBuilder;

/**
 * A document wrapper for the pdf files. Contains all the necessary information
 * for a pdf document as dimensions, headers, etc.. To build ad document, new elements
 * have to be added to the list, provided by {@link PdfDocumentWrapper#getContent()}
 * The elements must implement the {@link PdfObject} interface.
 * 
 * If the renderer is jcan-print, for building the content a {@link PdfRowBuilder} is
 * highly recommended. Because of the limitations of jcan print, each element must
 * define it's height explicitly at least here when adding to the document itself.
 * The RowBuilder has a convenient constructor for that and the height of a line
 * could set manually.
 * 
 * @author AdNovum Informatik AG
 */
public class PdfDocumentWrapper {
	
	public static final int A4_WIDTH = 595; // 210 mm
	public static final int A4_HEIGHT = 842; // 297 mm
	
	private double marginTop = 56.693; // 20 mm
	private double marginBottom = 0;//56.693; // 20 mm
	private double marginLeft = 32.5; // 15mm; -10 pix
	private double marginRight = 0; // 10 mm; -10 pix
	
	private double width = A4_WIDTH;
	private double height = A4_HEIGHT;
	
	private PdfBorderLayout layout;
	private final List<PdfObject> content = new ArrayList<PdfObject>();
	
	/**
	 * Create a new document wrapper with the default layout options.
	 */
	public PdfDocumentWrapper() {
		layout = new PdfBorderLayout(1);
	}
	
	public void setMargins (int left, int right, int top, int bottom) {
		marginTop = top;
		marginBottom = bottom;
		marginLeft = left;
		marginRight = right;
	}
	
	/**
	 * @return the marginTop
	 */
	public double getMarginTop() {
		return marginTop;
	}
	/**
	 * @param marginTop the marginTop to set
	 */
	public void setMarginTop(double marginTop) {
		this.marginTop = marginTop;
	}
	/**
	 * @return the marginBottom
	 */
	public double getMarginBottom() {
		return marginBottom;
	}
	/**
	 * @param marginBottom the marginBottom to set
	 */
	public void setMarginBottom(int marginBottom) {
		this.marginBottom = marginBottom;
	}
	/**
	 * @return the marginLeft
	 */
	public double getMarginLeft() {
		return marginLeft;
	}
	/**
	 * @param marginLeft the marginLeft to set
	 */
	public void setMarginLeft(double marginLeft) {
		this.marginLeft = marginLeft;
	}
	/**
	 * @return the marginRight
	 */
	public double getMarginRight() {
		return marginRight;
	}
	/**
	 * @param marginRight the marginRight to set
	 */
	public void setMarginRight(double marginRight) {
		this.marginRight = marginRight;
	}
	/**
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}
	/**
	 * @param width the width to set
	 */
	public void setWidth(double width) {
		this.width = width;
	}
	/**
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}
	/**
	 * @param height the height to set
	 */
	public void setHeight(double height) {
		this.height = height;
	}
	/**
	 * @return the layout
	 */
	public PdfBorderLayout getLayout() {
		return layout;
	}
	/**
	 * @param layout the layout to set
	 */
	public void setLayout(PdfBorderLayout layout) {
		this.layout = layout;
	}
	/**
	 * @return the content
	 */
	public List<PdfObject> getContent() {
		return content;
	}
	
}
