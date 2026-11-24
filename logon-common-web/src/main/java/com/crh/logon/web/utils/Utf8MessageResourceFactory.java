package com.crh.logon.web.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Properties;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.MessageResourcesFactory;
import org.apache.struts.util.PropertyMessageResources;
import org.apache.struts.util.PropertyMessageResourcesFactory;

/**
 * Our own <code>MessageResourceFactory</code> implementation capable of loading property files that are UTF-8
 * encoded.
 * <p/>
 * We need the special support because the default Struts implementation reads the files in using ISO-8859-1 and our
 * LitDict files are always in UTF-8. It would be possible to circumvent this problem by converting our
 * LitDict property files to ASCII using the tool <code>native2ascii</code> (the tool encodes non-ASCII
 * characters into \\uXXXX). While this works it increases maintenance efforts since the generated LitDict
 * files no longer contain the text as reported by the customer, but rather, test using the Java Unicode escaping
 * format. So unless you have the hex mapping in your head, comparing the generated LitDict files with the
 * customer input is a pain.
 * <p/>
 * Using this approach we hope to circumvent these problems by keeping the LitDict files in UTF-8.
 * <p/>
 * This class is configured in the etc/gdi/webconfig/merge/struts-message-resources.xml files for all web apps.
 * Furthermore, we use this classes explicitly in the com.crh.logon.web.utils.MessagesUtil class.
 * <p/>
 * IMPLEMENTATION NOTE: We did not place this class in logon-common to avoid pulling in the struts classes.
 *
 * @author AdNovum Informatik AG
 *
 * @see MessageUtils
 */
public class Utf8MessageResourceFactory extends PropertyMessageResourcesFactory {

	private static final long serialVersionUID = -6771751414012328742L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(Utf8MessageResourceFactory.class);

	private static final String RESOURCE = "LitDict";

	/*
	 * The loadLocale() is copied directly from PropertyMessageResources and adapted to load the properties
	 * from a UTF-8 stream. See the line with new InputStreamReader(stream, ENCODING). In addition we changed
	 * the logger statements to use our JcanLogger.
	 */
	private static class UtfPropertyMessageResources extends PropertyMessageResources {
		private static final long serialVersionUID = 8332795620929027267L;

		public UtfPropertyMessageResources(MessageResourcesFactory factory,
										   String config, boolean returnNull) {
			super(factory, config, returnNull);
		}

		@Override
		protected synchronized void loadLocale(String localeKey) {
			// [[
			Object[][] params = {{"localeKey", localeKey}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "loadLocale", params);
			}
			// ]]
			try {
				// Have we already attempted to load messages for this locale?
				if (locales.get(localeKey) != null) {
					LOG.debug("Messages already loaded for locale " + localeKey);
					return;
				}

				locales.put(localeKey, localeKey);

				// Set up to load the property resource for this locale key, if we can
				String name = config.replace('.', '/');
				if (localeKey.length() > 0) {
					name += "_" + localeKey;
				}

				name += ".properties";
				InputStream is = null;
				Properties props = new Properties();

				// Load the specified property resource
				LOG.debug("  Loading resource '" + name + "'");

				ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
				if (classLoader == null) {
					classLoader = this.getClass().getClassLoader();
				}

				is = classLoader.getResourceAsStream(name);
				if (is != null) {
					try {
						props.load(new InputStreamReader(is, StandardCharsets.UTF_8));
					}
					catch (IOException e) {
						LOG.error("Loading properties failed", e);
					}
					finally {
						try {
							is.close();
						}
						catch (IOException e) {
							LOG.info("Closing stream to properties failed", e);
						}
					}
				}

				// Copy the corresponding values into our cache
				if (props.size() < 1) {
					LOG.info("No properties read for file: " + name);
					return;
				}

				synchronized (messages) {
					Iterator names = props.keySet().iterator();
					while (names.hasNext()) {
						String key = (String) names.next();
						
						messages.put(messageKey(localeKey, key), props.getProperty(key));
					}
				}
			}
			finally {
				// [[
				if (logDebugEnabled) {
					LOG.exiting();
				}
				// ]]
			}
		}
	}

	public MessageResources createResources() {
		return createResources(RESOURCE);
	}

	public MessageResources createResources(String config) {
		return new UtfPropertyMessageResources(this, config, this.returnNull);
	}
}