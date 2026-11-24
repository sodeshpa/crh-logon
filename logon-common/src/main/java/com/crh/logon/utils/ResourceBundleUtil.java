package com.crh.logon.utils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.constants.Constant;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.text.MessageFormat;
import java.util.*;

/**
 * Utility class used by both {@link com.crh.logon.business.services.utils.Message} and
 * {@link com.crh.logon.taglib.fmt.BundleSupport} classes for handling ResourceBundles.
 * <p/>
 * We need the special support because ResourceBundles are read in using ISO-8859-1 encoding and our
 * LitDict files are always in UTF-8. It would be possible to circumvent this problem by converting our
 * LitDict property files to ASCII using the tool <code>native2ascii</code> (the tool encodes non-ASCII
 * characters into \\uXXXX). While this works it increases maintenance efforts since the generated LitDict
 * files no longer contain the text as reported by the customer, but rather, text using the Java Unicode escaping
 * format. So unless you have the hex mapping in your head, comparing the generated LitDict files with the
 * customer input is a pain.
 * <p/>
 * Using this approach we hope to circumvent these problems by keeping the LitDict files in UTF-8.
 *
 * @author AdNovum Informatik AG
 *
 * @see com.crh.logon.business.services.utils.Message
 * @see com.crh.logon.taglib.fmt.BundleSupport
 */
public class ResourceBundleUtil {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ResourceBundleUtil.class);

	private static final String RESOURCE = "LitDict";

	/*
	 * Copied from ResourceBundle.Control and adapted to read in property files as UTF-8.
	 * See the line with new InputStreamReader(stream, ENCODING) - the only modification
	 * to the original code.
	 *
	 * Also note that by default, our ResourceBundles are set not to expire.
	 */
	private static class Utf8Control extends ResourceBundle.Control {
		public ResourceBundle newBundle(String baseName, Locale locale, String format,
										ClassLoader loader, boolean reload)
				throws IllegalAccessException, InstantiationException, IOException {
			String bundleName = toBundleName(baseName, locale);
			ResourceBundle bundle = null;
			if (format.equals("java.class")) {
				try {
					Class<? extends ResourceBundle> bundleClass
							= (Class<? extends ResourceBundle>) loader.loadClass(bundleName);

					// If the class isn't a ResourceBundle subclass, throw a
					// ClassCastException.
					if (ResourceBundle.class.isAssignableFrom(bundleClass)) {
						bundle = bundleClass.newInstance();
					}
					else {
						throw new ClassCastException(bundleClass.getName()
															 + " cannot be cast to ResourceBundle");
					}
				}
				catch (ClassNotFoundException e) {
				}
			}
			else if (format.equals("java.properties")) {
				final String resourceName = toResourceName(bundleName, "properties");
				final ClassLoader classLoader = loader;
				final boolean reloadFlag = reload;
				InputStream stream = null;
				try {
					stream = AccessController.doPrivileged(
							new PrivilegedExceptionAction<InputStream>() {
								public InputStream run() throws IOException {
									InputStream is = null;
									if (reloadFlag) {
										URL url = classLoader.getResource(resourceName);
										if (url != null) {
											URLConnection connection = url.openConnection();
											if (connection != null) {
												// Disable caches to get fresh data for
												// reloading.
												connection.setUseCaches(false);
												is = connection.getInputStream();
											}
										}
									}
									else {
										is = classLoader.getResourceAsStream(resourceName);
									}
									return is;
								}
							});
				}
				catch (PrivilegedActionException e) {
					throw (IOException) e.getException();
				}
				if (stream != null) {
					try {
						bundle = new PropertyResourceBundle(new InputStreamReader(stream, Constant.DEFAULT_ENCODING));
					}
					finally {
						stream.close();
					}
				}
			}
			else {
				throw new IllegalArgumentException("unknown format: " + format);
			}
			return bundle;
		}
	}

	private ResourceBundleUtil() {
	}

	public static synchronized ResourceBundle getBundle(Locale locale) {
		return ResourceBundle.getBundle(RESOURCE, locale, new Utf8Control());
	}

	public static synchronized ResourceBundle getBundle(String baseName, Locale locale, ClassLoader loader) {
		return ResourceBundle.getBundle(baseName, locale, loader, new Utf8Control());
	}

	public static boolean messageExists(Locale locale, String key) {
		return lookupByKey(key, locale) != null;
	}

	public static String lookup(Locale locale, String key) {
		String message = lookupByKey(key, locale);
		return StringUtils.empty(message) ? ("???" + key + "???") : message;
	}

	public static String lookup(Locale locale, String key, Object... messageArguments) {
		String pattern = lookupByKey(key, locale);

		if (StringUtils.notEmpty(pattern)) {
			MessageFormat formatter = new MessageFormat("");
			formatter.setLocale(locale);
			formatter.applyPattern(pattern);

			return formatter.format(messageArguments);
		}
		else {
			return "???" + key + "???";
		}
	}

	private static String lookupByKey(String key, Locale locale) {
		ResourceBundle bundle = getBundle(locale);
		try {
			return bundle.getString(key);
		}
		catch (MissingResourceException e) {
			LOG.error("Missing key [" + key + "] in resource bundles", e);
			return null;
		}
	}
}
