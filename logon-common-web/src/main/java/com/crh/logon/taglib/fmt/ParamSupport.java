/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.taglib.fmt;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import org.apache.taglibs.standard.resources.Resources;

/**
 * Support for tag handlers for &lt;param&gt;, the message argument
 * subtag in JSTL 1.0 which supplies an argument for parametric replacement
 * to its parent &lt;message&gt; tag.
 *
 * @see MessageSupport
 */

public abstract class ParamSupport extends BodyTagSupport {

	protected String value;

	protected boolean valueSpecified;

	public ParamSupport() {
		super();
		init();
	}

	private void init() {
		value = null;
		valueSpecified = false;
	}

	/**
	 *  Supply our value to our parent <fmt:message> tag
	 */
	public int doEndTag() throws JspException {
		Tag parentTag = findAncestorWithClass(this, MessageSupport.class);
		if (parentTag == null) {
			throw new JspTagException(Resources.getMessage("PARAM_OUTSIDE_MESSAGE"));
		}
		MessageSupport parentMessage = (MessageSupport) parentTag;

		/*
		 * Get argument from 'value' attribute or body, as appropriate, and
		 * add it to enclosing <fmt:message> tag, even if it is null or equal
		 * to "".
		 */
		String parameterValue = null;
		if (valueSpecified) {
			parameterValue = value;
		}
		else {
			parameterValue = bodyContent.getString().trim();
		}
		parentMessage.addParam(parameterValue);

		return EVAL_PAGE;
	}

	/**
	 *  Releases any resources we may have (or inherit)
	 */
	public void release() {
		init();
	}
}
