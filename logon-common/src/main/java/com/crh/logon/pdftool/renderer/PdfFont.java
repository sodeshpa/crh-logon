/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdftool.renderer;

import com.crh.logon.pdftool.labelprovider.DTOMarker;

/**
 * Renderer independent Font container.
 * 
 * @author AdNovum Informatik AG
 */
public class PdfFont implements DTOMarker {
	
	private final String style;
	private final String size;
	
	/**
	 * Use values from {@link PdfFontStore}
	 */
	public PdfFont(String style, String size) {
		this.style = style;
		this.size = size;
	}
	
	public String getStyle() {
		return style;
	}
	
	public String getSize() {
		return size;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		result = prime * result + ((style == null) ? 0 : style.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PdfFont other = (PdfFont) obj;
		if (size == null) {
			if (other.size != null)
				return false;
		}
		else if (!size.equals(other.size))
			return false;
		if (style == null) {
			if (other.style != null)
				return false;
		}
		else if (!style.equals(other.style))
			return false;
		return true;
	}
	
	
	
}
