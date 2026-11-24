/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdftool.layout;

import com.crh.logon.pdftool.objects.PdfObject;

/**
 * The layout data. This determines the position of a {@link PdfObject} implementation.
 * See the constructors for the default values.
 * 
 * @author AdNovum Informatik AG
 */
public class PdfBorderData {
	
	public enum HOrientation {
		LEFT,
		RIGHT,
		CENTER;
	}
	
	public enum VOrientation {
		TOP,
		BOTTOM;
	}
	
	private final HOrientation horizontalOrientation;
	private final VOrientation verticalOrientation;
	
	/**
	 * 
	 */
	public PdfBorderData(HOrientation hOrientation, VOrientation vOrientation) {
		this.horizontalOrientation = hOrientation;
		this.verticalOrientation = vOrientation;
	}
	
	/**
	 * 
	 */
	public PdfBorderData(HOrientation hOrientation) {
		this(hOrientation, VOrientation.TOP);
	}
	
	/**
	 * 
	 */
	public PdfBorderData() {
		this(HOrientation.LEFT, VOrientation.TOP);
	}

	/**
	 * @return the horizontalOrientation
	 */
	public HOrientation getHorizontalOrientation() {
		return horizontalOrientation;
	}

	/**
	 * @return the verticalOrientation
	 */
	public VOrientation getVerticalOrientation() {
		return verticalOrientation;
	}
	
	

}
