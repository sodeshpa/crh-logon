/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 

package com.crh.logon.taglib.fmt;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import com.crh.logon.utils.ResourceBundleUtil;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;

/**
 * <p>A handler for &lt;message&gt; that accepts attributes as Strings
 * and evaluates them as expressions at runtime.</p>
 *
 * @author Jan Luehe
 */

/**
 * !!! WARNING: THIS FILE IS COPIED (AND ALMOST  UNTOUCHED) FROM THE JSTL DISTRIBUTION !!!
 * !!! IT IS JUST A DEPENDENCY FOR THE CUSTOM MESSAGE TAG. DO NOT REFORMAT!!!          !!!         
 */
public class MessageTag extends MessageSupport {

    //*********************************************************************
    // Private state (implementation details)

    private String key_;                         // stores EL-based property
    private String bundle_;		         // stores EL-based property


    //*********************************************************************
    // Constructor

    /**
     * Constructs a new MessageTag.  As with TagSupport, subclasses
     * should not provide other constructors and are expected to call
     * the superclass constructor
     */
    public MessageTag() {
        super();
        init();
    }


    //*********************************************************************
    // Tag logic

    // evaluates expression and chains to parent
    public int doStartTag() throws JspException {

        // evaluate any expressions we were passed, once per invocation
        evaluateExpressions();

	// chain to the parent implementation
	return super.doStartTag();
    }

    // Releases any resources we may have (or inherit)
    public void release() {
        super.release();
        init();
    }


    //*********************************************************************
    // Accessor methods

    // for EL-based attribute
    public void setKey(String key_) {
	this.key_ = key_;
	this.keySpecified = true;
    }

    // for EL-based attribute
    public void setBundle(String bundle_) {
	this.bundle_ = bundle_;
        this.bundleSpecified = true;
    }


    //*********************************************************************
    // Private (utility) methods

    // (re)initializes state (during release() or construction)
    private void init() {
        // null implies "no expression"
	key_ = bundle_ = null;
    }

    // Evaluates expressions as necessary
    private void evaluateExpressions() throws JspException {
        /* 
         * Note: we don't check for type mismatches here; we assume
         * the expression evaluator will return the expected type
         * (by virtue of knowledge we give it about what that type is).
         * A ClassCastException here is truly unexpected, so we let it
         * propagate up.
         */

	if (keySpecified) {
	    keyAttributeValue = (String) ExpressionEvaluatorManager.evaluate(
	        "key", key_, String.class, this, pageContext);
	}

	if (bundleSpecified) {
	    bundleAttributeValue = (LocalizationContext)
		ExpressionEvaluatorManager.evaluate(
	            "bundle", bundle_, LocalizationContext.class, this,
		    pageContext);
	} 
    }
    
    
    private static ResourceBundle findMatch(String basename, Locale pref) {
    	ResourceBundle match = null;

    	try {
    	    ResourceBundle bundle =
    		ResourceBundleUtil.getBundle(basename, pref,
										 Thread.currentThread().getContextClassLoader());
    	    Locale avail = bundle.getLocale();
    	    if (pref.equals(avail)) {
    		// Exact match
    		match = bundle;
    	    } else {
                    /*
                     * We have to make sure that the match we got is for
                     * the specified locale. The way ResourceBundle.getBundle()
                     * works, if a match is not found with (1) the specified locale,
                     * it tries to match with (2) the current default locale as 
                     * returned by Locale.getDefault() or (3) the root resource 
                     * bundle (basename).
                     * We must ignore any match that could have worked with (2) or (3).
                     * So if an exact match is not found, we make the following extra
                     * tests:
                     *     - avail locale must be equal to preferred locale
                     *     - avail country must be empty or equal to preferred country
                     *       (the equality match might have failed on the variant)
    		 */
                    if (pref.getLanguage().equals(avail.getLanguage())
    		    && ("".equals(avail.getCountry()) || pref.getCountry().equals(avail.getCountry()))) {
    		    /*
    		     * Language match.
    		     * By making sure the available locale does not have a 
    		     * country and matches the preferred locale's language, we
    		     * rule out "matches" based on the container's default
    		     * locale. For example, if the preferred locale is 
    		     * "en-US", the container's default locale is "en-UK", and
    		     * there is a resource bundle (with the requested base
    		     * name) available for "en-UK", ResourceBundle.getBundle()
    		     * will return it, but even though its language matches
    		     * that of the preferred locale, we must ignore it,
    		     * because matches based on the container's default locale
    		     * are not portable across different containers with
    		     * different default locales.
    		     */
    		    match = bundle;
    		}
    	    }
    	} catch (MissingResourceException mre) {
    	}

    	return match;
        }
    
}
