/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.taglib.fmt;

import javax.servlet.jsp.JspException;

import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;

/**
 * <p>A handler for &lt;param&gt; that accepts message arguments as
 * Strings and evaluates them as expressions at runtime.</p>
 *
 */

public class ParamTag extends ParamSupport {

	private String value_; // stores EL-based property

	/**
	 * Constructs a new ParamTag.  As with TagSupport, subclasses
	 * should not provide other constructors and are expected to call
	 * the superclass constructor
	 */
	public ParamTag() {
		super();
		init();
	}

	/**
	 * Evaluates expression and chains to parent
	 */
	public int doStartTag() throws JspException {

		// evaluate any expressions we were passed, once per invocation
		evaluateExpressions();

		// chain to the parent implementation
		return super.doStartTag();
	}

	/**
	 * Releases any resources we may have (or inherit)
	 */
	public void release() {
		super.release();
		init();
	}

	/**
	 * For EL-based attribute
	 */
	public void setValue(String value_) {
		this.value_ = value_;
		this.valueSpecified = true;
	}

	/**
	 * (Re)initializes state (during release() or construction)
	 */
	private void init() {
		// null implies "no expression"
		value_ = null;
	}

	/**
	 * Evaluates expressions as necessary
	 */
	private void evaluateExpressions() throws JspException {
		/* 
		 * Note: we don't check for type mismatches here; we assume
		 * the expression evaluator will return the expected type
		 * (by virtue of knowledge we give it about what that type is).
		 * A ClassCastException here is truly unexpected, so we let it
		 * propagate up.
		 */

		if (value_ != null) {
			value = (String) ExpressionEvaluatorManager.evaluate("value", value_, String.class,
					this,
					pageContext);
		}
	}
}
