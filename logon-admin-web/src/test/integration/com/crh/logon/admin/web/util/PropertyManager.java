/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Assume;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.pages.PropertyKey;

/**
 * Class dealing with test properties.
 * @author AdNovum Informatik AG
 */
public class PropertyManager {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PropertyManager.class);

	private final Map<String, Properties> propertiesMap = new HashMap<String, Properties>();

	public final Properties loadProperties(String path) {
		final Properties properties = new Properties();

		try {

			properties.load(new FileInputStream(path));

		}
		catch (FileNotFoundException e) {
			LOG.error(String.format("Unable to load properties from %s.", path), e);
			Assume.assumeNoException(e);
		}
		catch (IOException e) {
			LOG.error(String.format("Unable to load properties from %s.", path), e);
			Assume.assumeNoException(e);
		}

		propertiesMap.put(path, properties);
		return properties;
	}

	public final Properties getPropertiesForPath(String path) {
		return propertiesMap.get(path);
	}

	public final String getTestProperty(String path, String key) {
		if (!propertiesMap.containsKey(path)) {
			loadProperties(path);
		}

		final String value = propertiesMap.get(path).getProperty(key);
		Assert.assertNotNull(String.format("No property value found for key %s.", key), value);

		return value;
	}

	public final String addTestProperty(String path, PropertyKey key, String value) {
		if (!propertiesMap.containsKey(path)) {
			loadProperties(path);
		}

		propertiesMap.get(path).put(key.getKeyString(), value);
		Assert.assertNotNull(
				String.format("No property value found for key %s.", key.getKeyString()), value);

		return value;
	}

	/**
	 * Copy the properties to other one
	 * @param pathFromCopy source properties
	 * @param pathToCopy target properties
	 */
	public final void copyProperties(String pathFromCopy, String pathToCopy) {
		propertiesMap.put(pathToCopy, (Properties) propertiesMap.get(pathFromCopy).clone());
		LOG.info("Copied all properties from " + pathFromCopy + "  to " + pathToCopy + ".["
				+ propertiesMap.get(pathToCopy).toString() + "]");

		return;
	}

	/**
	 * add all properties of <code>pathFromMerge</code> that isnot contained in <code>pathToMerge</code>
	 * to <code>pathToMerge</code>.
	 * @param pathFromMerge properties want to merge
	 * @param pathToMerge properties will be merged to
	 */
	public final void mergeProperties(String pathFromMerge, String pathToMerge) {
		loadProperties(pathFromMerge);
		loadProperties(pathToMerge);
		Properties mergedProperties = (Properties) propertiesMap.get(pathFromMerge).clone();
		Properties propToMerge = (Properties) propertiesMap.get(pathToMerge).clone();
		mergedProperties.putAll(propToMerge);
		propertiesMap.put(pathToMerge, mergedProperties);
	}

	/**
	 * Stores the properties in the file given in path. The tests extend this class, the policy could be: load the clean properties and
	 * add everything to that, no need to send as parameter the map.
	 * @param map
	 * @param path
	 * @param append in case true if the file already exists we use that, in case false it is going to be DELETED first!
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public final void storeTestRelatedProperties(Map<String, String> map, String path,
			boolean append) throws FileNotFoundException, IOException {
		final Properties properties = new Properties();
		properties.putAll(map);

		final FileOutputStream fos = createParentDirAndGetOutputStream(path, append);
		properties.store(fos, null);

		fos.close();
	}

	public final void storeTestRelatedProperties(String path, boolean append)
			throws FileNotFoundException, IOException {
		LOG.info(propertiesMap.get(path).toString());
		
		final FileOutputStream fos = createParentDirAndGetOutputStream(path, append);
		propertiesMap.get(path).store(fos, null);

		fos.close();
	}

	/**
	 * Creates the parent directory before opening the stream, if the file does not exist.
	 */
	private FileOutputStream createParentDirAndGetOutputStream(String path, boolean appendRequested) throws FileNotFoundException {
		File file = new File(path);
		boolean append = appendRequested;

		if (!file.exists()) {
			LOG.info("File " + path + " does not exists, though append was asked for. Creating...");
			append = false;
		}

		if (!append) {
			File parentDir = file.getParentFile();
			parentDir.mkdirs();
		}

		return new FileOutputStream(file, append);
	}

	/**
	 * Retrieves stored properties from a property file.
	 * Reads out the parameters which were stored by storeTestRelatedProperties.
	 * 
	 * @param path The property file name.
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public final Properties getTestRelatedStoredProperties(String path)
			throws FileNotFoundException, IOException {
		final Properties properties = new Properties();
		properties.load(new FileInputStream(new File(path)));

		return properties;
	}
}
