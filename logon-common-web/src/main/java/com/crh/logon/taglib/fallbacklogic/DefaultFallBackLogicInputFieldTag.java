/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.taglib.fallbacklogic;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.ExceptionUtil;

/**
 * Only usable INSIDE a DefaultFallBackLogicTag. The values are retrieved from there, this tag
 * renders only the input field according to the values stored in the DefaultFallBackLogicTag. The input field's
 * value is the propertyValue of the DefaultFallBackLogicTag, and is disabled, if it was not present in the
 * object, but it had to be retrieved through a fallback logic.
 * 
 * 
 * @author AdNovum Informatik AG
 */
public class DefaultFallBackLogicInputFieldTag extends TagSupport { 
	
	private static final String C_NAME = DefaultFallBackLogicInputFieldTag.class.getName();

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(C_NAME);
	
	private String style;
	private String maxLength;
	private String styleClass;

	public int doStartTag() throws JspException {
		// [[
		int result = 0;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "doStartTag");
		}
		// ]]
		try {
			
			Tag tagNotCasted = findAncestorWithClass(this, DefaultFallBackLogicTag.class);
			DefaultFallBackLogicTag tag = (DefaultFallBackLogicTag) tagNotCasted;
			boolean disabled = tag.isChangeDisabled();
			String inputFieldString = "";
			if (!tag.isReadOnly()){
				inputFieldString = "<input type=\"text\" name=\"" + tag.getPropertyInputFieldName() + "\" " + (disabled ? "disabled" : "") + " id=\""+ tag.getPropertyInputFieldName()+ "\" value=\"" + tag.getPropertyValue()+ "\" " +
				(StringUtils.notEmpty(style) ? " style=\"" + style + "\" " : "" )+
				(StringUtils.notEmpty(maxLength) ? "  maxlength=\"" + maxLength + "\" " : "" ) +
				(StringUtils.notEmpty(styleClass) ? " class=\"" + styleClass + "\" " : "" )+			
				"/>";
			}
			else{
				inputFieldString = tag.getPropertyValue();
			}
			JspWriter out = pageContext.getOut();				
			out.write(inputFieldString);
		}
		
		catch (IOException e) {
			ExceptionUtil.errException(LOG, e, null);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
		return result = SKIP_BODY;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}


}
