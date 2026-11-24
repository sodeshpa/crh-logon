/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Prints out the body in case the parent OrderSimpleChangeFunctionDisabledTag says that 
 * the functionality is enabled.
 */
public class OrderSimpleChangeFunctionEnabledBodyTag extends TagSupport { 

	public int doStartTag() throws JspException {
		int result = 0;
		Tag tagNotCasted = findAncestorWithClass(this, OrderSimpleChangeFunctionDisabledTag.class);
		OrderSimpleChangeFunctionDisabledTag tag = (OrderSimpleChangeFunctionDisabledTag) tagNotCasted;
		boolean disabled = tag.isDisabled();
		return result = disabled ? SKIP_BODY : EVAL_BODY_INCLUDE ;
	}
}
