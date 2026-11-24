/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.pdftool.store;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.crh.logon.pdftool.labelprovider.DTOMarker;
import com.crh.logon.utils.text.Transliterator;
import org.apache.commons.lang.ArrayUtils;

/**
 * Stores labels and data for a pdf document. Generic class, the first type
 * represents the keys to the labels, the second one to the data. These keys
 * should be public enums in the concrete implementations.
 * 
 *  All the business related data handling should happen here.
 *  
 * @author AdNovum Informatik AG
 */
public abstract class AbstractPdfDataStore<T, K> {
	
	private final Map<T, String> labelMap = new HashMap<T, String>();
	
	private final Map<K, String> dataMap = new HashMap<K, String>();
	
	private final Map<K, Byte[]> byteStreamMap = new HashMap<K, Byte[]>();
	
	private final Map<K, Object> objectMap = new HashMap<K, Object>();
	
	protected Locale locale;

	private Transliterator transliterator;
	
	public AbstractPdfDataStore(Locale locale) {
		this(locale, null);
	}

	public AbstractPdfDataStore(Locale locale, Transliterator transliterator) {
		initStore(locale, transliterator);
	}
	
	protected void initStore(Locale locale, Transliterator transliterator) {
		this.locale = locale;
		this.transliterator = transliterator != null ? transliterator : new Transliterator() {
			public String transliterate(String input) {
				return input;
			}
		};

		initializeLabels();				
	}
	
	/**
	 * Implementations of this function should call add(key,value) multiple
	 * times to fill the store map with labels
	 */
	protected abstract void initializeLabels();
	
	/**
	 * Adds a key/value pair to the map
	 * @param key
	 * @param value
	 */
	public void addLabel(T key, String value) {
		String transliteratedValue = transliterate(value);
		labelMap.put(key, transliteratedValue);
	}
	
	/**
	 * Returns the value for the key or an empty string if value doesn't exist
	 * TODO: isn't it a bit ambiguous to silently handle this?
	 * @param key
	 * @return
	 */
	public String getLabel(T key) {
		/* use get null check instead of contains. HashMap allows null values and
		 * using null as return value is not allowed.*/
		return labelMap.get(key) != null ? labelMap.get(key) : "";
	}

	/**
	 * Adds/Updates a key/value pair to/in the map
	 * @param key
	 * @param value
	 */
	public void addData(K key, String value) {
		String transliteratedValue = transliterate(value);
		dataMap.put(key, transliteratedValue);
	}
	
	/**
	 * Returns the value for the key or an empty string if value doesn't exist
	 * @param key
	 * @return
	 */
	public String getData(K key) {
		/* use get null check instead of contains. HashMap allows null values and
		 * using null as return value is not allowed.*/
		return dataMap.get(key) != null ? dataMap.get(key) : "";
	}
	
	/**
	 * Adds/Updates a key/value pair to/in the map
	 * @param key
	 * @param value
	 */
	public void addObjectData(K key, Object value) {
		objectMap.put(key, value);
	}
	
	/**
	 * Returns the value for the key or null if value doesn't exist
	 * @param key
	 * @return
	 */
	public Object getObjectData(K key) {
		/* use get null check instead of contains. HashMap allows null values and
		 * using null as return value is not allowed.*/
		return objectMap.get(key);
	}
	
	/**
	 * Returns the byteStream for the given key or an empty array if none had been found
	 * @param key
	 * @return
	 */
	public byte[] getByteStream(K key) {
		if (byteStreamMap.containsKey(key)) {
			return ArrayUtils.toPrimitive(byteStreamMap.get(key));
		}
		return new byte[0];
	}
	
	/**
	 * Returns the locale of this store
	 * 
	 * @return
	 */
	public Locale getLocale() {
		return locale;
	}
	
	/**
	 * Sets the byte stream for the given key
	 * 
	 * @param key
	 * @param input
	 */
	public void addByteStream(K key, byte[] input) {
		byteStreamMap.put(key, ArrayUtils.toObject(input));
	}
	
	/**
	 * Get a list of DTOs (table input) from the corresponding class. Implementations should return
	 * an empty array if no instances have found.
	 *  
	 * @param clazz
	 * @return
	 */
	public abstract<E extends DTOMarker> List<E> getDTOList(Class<E> clazz);

	/**
	 * Transliterates the given string using the configured Transliterator.
	 *
	 * @param input the String to transliterate
	 *
	 * @return the transliterated string
	 */
	protected String transliterate(String input) {
		return transliterator.transliterate(input);
	}
	
	/**
	 * @param locale the locale to set
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * @param transliterator the transliterator to set
	 */
	public void setTransliterator(Transliterator transliterator) {
		this.transliterator = transliterator;
	}
	
}
