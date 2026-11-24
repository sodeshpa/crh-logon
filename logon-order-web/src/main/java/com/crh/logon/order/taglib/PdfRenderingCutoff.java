/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * Cut off text to given length
 * @author AdNovum Informatik AG
 */
public class PdfRenderingCutoff extends BodyTagSupport {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PdfRenderingCutoff.class);
	private static final String CUTOFF_CSS = "line-height: 1.1; overflow: hidden; white-space: nowrap;";
	
	private int width = -1;
	private String classes = null;
   
	public int doAfterBody() throws JspException {
		BodyContent bodyContent = getBodyContent();  
		String body = bodyContent.getString();
		JspWriter out = bodyContent.getEnclosingWriter();
		
		String widthData = width > 0 ? "width: " + this.width + "px;" : "";
		String classData = classes != null ? "class='" + classes + "'" : "";
		
		try {
			out.println("<div style='" + CUTOFF_CSS + widthData + "' " + classData + ">");
			out.print(body);
			out.println("</div>");
		}
		catch (IOException e) {
			LOG.error("Output error with jsp tag.", e);
		}
	   
		return EVAL_BODY_INCLUDE;
	}

	public int getWidth() {
		return width;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}

	public String getClasses() {
		return classes;
	}

	public void setClasses(String classes) {
		this.classes = classes;
	}
}

