/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdftool.renderer;

/**
 * Stores font related information. Concrete renderer implementation should
 * map all these values to the real values of the renderer. If new values
 * added here, all the renderer implementation must be prepared to handle them.
 * 
 * @author AdNovum Informatik AG
 */
public class PdfFontStore {
	
	public static final String Helvetica = "pdf_tool_font_helvetica";
	public static final String Helvetica_Bold = "pdf_tool_font_helvetica_bold";
	
	public static final String SIZE_7 = "pdf_tool_fontsize_7";
	public static final String SIZE_8 = "pdf_tool_fontsize_8"; 
	public static final String SIZE_9 = "pdf_tool_fontsize_9"; 
	public static final String SIZE_10 = "pdf_tool_fontsize_10"; 
	public static final String SIZE_14 = "pdf_tool_fontsize_14"; 

}
