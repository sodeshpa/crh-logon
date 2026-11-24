/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.taglib.fmt;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import com.crh.logon.constants.Constant;
import com.crh.logon.utils.ResourceBundleUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.taglibs.standard.tag.common.core.Util;
import org.apache.taglibs.standard.tag.common.fmt.BundleSupport;

/**
 * Support for tag handlers for &lt;message&gt;, the message formatting tag
 * in JSTL 1.0.
 *
 */

public abstract class MessageSupport extends BodyTagSupport {

	public static final String UNDEFINED_KEY = "???";
	public static final String LITDICT_INFO_FLAG_KEY = "LITDICT_INFO_FLAG";

	protected String keyAttributeValue;

	protected boolean keySpecified;

	protected LocalizationContext bundleAttributeValue;

	protected boolean bundleSpecified;

	private String variableName;

	private int scope;

	private List<String> messageParameters;

	public MessageSupport() {
		super();
		messageParameters = new ArrayList<String>();
		init();
	}

	private void init() {
		variableName = null;
		scope = PageContext.PAGE_SCOPE;
		keyAttributeValue = null;
		keySpecified = false;
		bundleAttributeValue = null;
		bundleSpecified = false;
	}

	public void setScope(String scope) {
		this.scope = Util.getScope(scope);
	}

	/**
	 * Adds an argument (for parametric replacement) to this tag's message.
	 *
	 * @see ParamSupport
	 */
	public void addParam(String arg) {
		messageParameters.add(arg);
	}

	public int doStartTag() throws JspException {
		messageParameters.clear();
		return EVAL_BODY_BUFFERED;
	}

	public int doEndTag() throws JspException {

		String key = null;
		LocalizationContext localizationContext = null;

		// determine the message key by...
		if (keySpecified) {
			// ... reading 'key' attribute
			key = keyAttributeValue;
		}
		else {
			// ... retrieving and trimming our body
			if (bodyContent != null && bodyContent.getString() != null)
				key = bodyContent.getString().trim();
		}

		if ((key == null) || key.equals("")) {
			try {
				pageContext.getOut().print("??????");
			}
			catch (IOException ioe) {
				throw new JspTagException(ioe.getMessage());
			}
			return EVAL_PAGE;
		}

		String prefix = null;
		if (!bundleSpecified) {
			Tag parentTag = findAncestorWithClass(this, BundleSupport.class);
			if (parentTag != null) {
				// use resource bundle from parent <bundle> tag
				BundleSupport parent = (BundleSupport) parentTag;
				localizationContext = parent.getLocalizationContext();
				prefix = parent.getPrefix();
			}
			else {
				localizationContext = BundleSupport.getLocalizationContext(pageContext);
			}
		}
		else {
			localizationContext = bundleAttributeValue;
			if (localizationContext.getLocale() != null) {
				SetLocaleSupport.setResponseLocale(pageContext, localizationContext.getLocale());
			}
		}

		String message = UNDEFINED_KEY + key + UNDEFINED_KEY;
		if (localizationContext != null) {
			ResourceBundle bundle = localizationContext.getResourceBundle();
			if (bundle != null) {
				try {
					// prepend 'prefix' attribute from parent bundle
					if (prefix != null) {
						key = prefix + key;
					}

					message = bundle.getString(key);

					// Perform parametric replacement if required
					if (!messageParameters.isEmpty()) {
						Object[] messageArgs = messageParameters.toArray();
						MessageFormat formatter = new MessageFormat("");
						if (localizationContext.getLocale() != null) {
							formatter.setLocale(localizationContext.getLocale());
						}
						formatter.applyPattern(message);
						message = formatter.format(messageArgs);
					}
					HttpSession session = pageContext.getSession();
					if (session.getAttribute(MessageSupport.LITDICT_INFO_FLAG_KEY) != null) {

						StringBuilder toolTipBuilder = new StringBuilder();
						StringBuilder toolTipContentBuilder = new StringBuilder();

						for (Locale locale : Constant.AVAILABLE_LOCALES) {
							addTranslationToToolTip(key, toolTipContentBuilder, locale);
						}

						toolTipBuilder.append("<span ext:qtitle=");
						toolTipBuilder.append(key);
						toolTipBuilder.append(" ext:qtip=");
						toolTipBuilder.append(StringEscapeUtils.escapeHtml(toolTipContentBuilder.toString()));
						toolTipBuilder.append(">");
						toolTipBuilder.append(StringEscapeUtils.escapeHtml(message));
						toolTipBuilder.append("</span>");

						message = toolTipBuilder.toString();
					}

				}
				catch (MissingResourceException missingResourceException) {
					message = UNDEFINED_KEY + key + UNDEFINED_KEY;
				}
			}
		}

		message = StringUtils.replace(message, "\'", "&#39;");
		message = StringUtils.replace(message, "\"", "&#33;");

		if (variableName != null) {
			pageContext.setAttribute(variableName, message, scope);
		}
		else {
			try {
				pageContext.getOut().print(message);
			}
			catch (IOException ioException) {
				throw new JspTagException(ioException.getMessage());
			}
		}

		return EVAL_PAGE;
	}

	public void addTranslationToToolTip(String key, StringBuilder toolTipContentBuilder,
			Locale locale) {
		String language = locale.getLanguage();
		toolTipContentBuilder.append("<b>" + language.toUpperCase() + ":&nbsp;</b>");
		toolTipContentBuilder.append(getFormattedMessageForLocale(key, messageParameters, locale));
		toolTipContentBuilder.append("<br/>");
	}

	/**
	 *  Releases any resources we may have (or inherit)
	 */
	public void release() {
		init();
	}

	private static ResourceBundle findMatch(String baseName, Locale locale) {
		ResourceBundle match = null;

		try {
			ResourceBundle possibleMatch = ResourceBundleUtil.getBundle(baseName, locale,
					Thread.currentThread().getContextClassLoader());
			Locale localeOfPossibleMatch = possibleMatch.getLocale();
			if (locale.equals(localeOfPossibleMatch)) {
				match = possibleMatch;
			}
			else {
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
				String language = locale.getLanguage();
				String languageOfPossibleMatch = localeOfPossibleMatch.getLanguage();
				String countryOfPossibleMatch = localeOfPossibleMatch.getCountry();
				String country = locale.getCountry();
				if (language.equals(languageOfPossibleMatch)
						&& (StringUtils.isEmpty(countryOfPossibleMatch) || country.equals(
								countryOfPossibleMatch))) {
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
					match = possibleMatch;
				}
			}
		}
		catch (MissingResourceException mre) {
		}

		return match;
	}

	protected String getFormattedMessageForLocale(String key, List<String> parameters, Locale loc) {
		String message = findMatch("LitDict", loc).getString(key);

		if (!parameters.isEmpty()) {
			Object[] messageArgs = parameters.toArray();
			MessageFormat formatter = new MessageFormat("");
			if (loc != null) {
				formatter.setLocale(loc);
			}
			formatter.applyPattern(message);
			message = formatter.format(messageArgs);
		}

		message = StringUtils.replace(message, " ", "&nbsp;");
		message = StringUtils.replace(message, "\'", "&#39;");
		message = StringUtils.replace(message, "\"", "&#34;");

		return message;
	}

}
