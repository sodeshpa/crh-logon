/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout.colldelnote;

import java.util.Map;

import com.crh.logon.pdftool.labelprovider.DTOMarker;
import com.crh.logon.pdftool.renderer.PdfFont;
import com.crh.logon.pdftool.renderer.PdfFontStore;

/**
 * Class for holding column labels for a table
 */
public class LabelDTO implements DTOMarker {
	
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -6129570206010443567L;

	/**
	 * {@link Map} for assigning a column index and a {@link String} label
	 */
	private final Map<Integer, String> map;
	
	/**
	 * The {@link PdfFont} for rendering the labels
	 */
	private final PdfFont font;
	
	/**
	 * Constructor.
	 * The font defaults to Helvetica 9pt.
	 * @param map {@link Map} for assigning a column index and a {@link String} label
	 */
	public LabelDTO(Map<Integer, String> map) {
		this.map = map;
		this.font = new PdfFont(PdfFontStore.Helvetica, PdfFontStore.SIZE_9);
	}

	/**
	 * Constructor
	 * @param map {@link Map} for assigning a column index and a {@link String} label
	 * @param font The {@link PdfFont} for rendering the labels
	 */
	public LabelDTO(Map<Integer, String> map, PdfFont font) {
		this.map = map;
		this.font = font;
	}
	
	/**
	 * Returns the label for the column index
	 * @param column the column index
	 * @return the label for the column index or an empty {@link String} if no value found
	 */
	public String getColumnValue(Integer column) {
		return map.containsKey(column) ? map.get(column) : "";
	}

	/**
	 * @return the font for rendering the label
	 */
	public PdfFont getFont() {
		return font;
	}
	
}
