/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdftool.objects;

import java.util.List;

import com.crh.logon.pdftool.layout.PdfBorderData;
import com.crh.logon.pdftool.layout.PdfBorderLayout;

/**
 * Interface for the UI elements used in PDF documents. All elements
 * must implement it.
 * @author AdNovum Informatik AG
 */
public interface PdfObject {
	
	/**
	 * Returns the parent of this object
	 * @return
	 */
	public PdfObject getParent();
	
	/**
	 * Sets the parent for this object
	 * @param parent
	 */
	public void setParent(PdfObject parent);
	
	/**
	 * Get the children of this object. Layout information should be provided
	 * @return
	 */
	public List<PdfObject> getChildren();
	
	/**
	 * For complex elements, returns the inner layout of the object.
	 * @return
	 */
	public PdfBorderLayout getLayout();
	
	/**
	 * For complex elements, defines the inner layout of the object.
	 * @param layout
	 */
	public void setLayout(PdfBorderLayout layout);
	
	/**
	 * Never <code>null</code>. Returns the layout dta, which defines the
	 * position within the parent.
	 * @return
	 */
	public PdfBorderData getLayoutData();
	
	/**
	 * Never <code>null</code>. Define the position within parent.
	 * @param data
	 */
	public void setLayoutData(PdfBorderData data);

	/**
	 * Returns the width (for some renderer: -1 means dynamic and is default) 
	 * @return
	 */
	public int getWidth();
	
	/**
	 * Sets the width.
	 * @param width
	 */
	public void setWidth(int width);
	
	/**
	 * This is a pixel width that might not be actually used but is valuable for planned text cutting  
	 */
	public double getPlannedWidth();
	
	public void setPlannedWidth(double targetWidth);	
	/**
	 * Returns the height (for some renderer: -1 means dynamic and is default) 
	 * @return
	 */
	public int getHeight();
	
	/**
	 * Sets the height.
	 * @param height
	 */
	public void setHeight(int height);

}
