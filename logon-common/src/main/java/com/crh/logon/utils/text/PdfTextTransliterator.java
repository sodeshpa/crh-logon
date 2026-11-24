/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.utils.text;

import com.crh.logon.utils.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The <code>PdfTextTransliterator</code> class is used by the Delivery Note PDF generation
 * to transliterate Latin-2 based characters for use in a Latin-1 environment. Specifically,
 * since the PDF specification only supports Latin-1, it is not possible to fully and completely
 * represent Latin-2 based languages (which include all of the Eastern European languages supported
 * by Logon). For this reason, we need to transliterate the unsupported characters to a
 * character set which is representable in Latin-1.
 * <p>
 * The current implementation will only attempt to map those characters which have no corresponding
 * Latin-1 representation. That is, if we were simply going for a Latin => ASCII transliteration,
 * it probably would have been more efficient (at least simpler) to have used the ICU4J classes
 * as follows:
 * <code>
 *     com.ibm.icu.text.Transliterator t =
 *         com.ibm.icu.text.Transliterator.getInstance("Latin-ASCII");
 *
 *     String input = "AÁČEÉIÍOÓÖŐŘŠUÚÜŰŽ";
 *     String output = t.transliterate(input);
 * </code>
 * <p>
 *
 * @author AdNovum Informatik AG
 *
 * @reference https://en.wikipedia.org/wiki/ISO/IEC_8859-1
 * @reference https://en.wikipedia.org/wiki/Czech_alphabet
 * @reference https://en.wikipedia.org/wiki/Slovak_alphabet
 * @reference https://en.wikipedia.org/wiki/Hungarian_alphabet
 * @reference https://en.wikipedia.org/wiki/Romanian_alphabet
 * @reference https://en.wikipedia.org/wiki/Romanian_alphabet
 * @reference https://en.wikipedia.org/wiki/Polish_alphabet
 * @reference https://en.wikipedia.org/wiki/Gaj%27s_Latin_alphabet
 * @reference https://en.wikipedia.org/wiki/Croatian_language
 * @reference https://en.wikipedia.org/wiki/Serbian_language
 * @reference https://en.wikipedia.org/wiki/Azerbaijani_alphabet
 * @reference https://github.com/norman/babosa
 * @reference http://camendesign.com/code/transliteration
 */
public class PdfTextTransliterator implements Transliterator {

	// for better code readability
	private static class CodepointCharacterMap extends HashMap<Integer, String> {
		private static final long serialVersionUID = 1L;
	}

	// global map of codepoints to replacement characters - used as the basis by all languages
	private static final CodepointCharacterMap GLOBAL_LANG_CODEPOINT_MAP;

	// maps ISO-639-1 language code (e.g. "de") to a map of codepoints and replacement characters
	private static final Map<String, CodepointCharacterMap> LANG_SPECIFIC_CODEPOINT_MAP;

	static {

		// the base mapping used across all languages. if a specific language chooses to
		// have a different transliteration for a certain character, then we can clone the map
		// and overwrite the specific letters for that language.
		CodepointCharacterMap mapping = new CodepointCharacterMap();

		//-- FRENCH (FR) ---//
		mapping.put(0x152 /* Œ */, "OE");
		mapping.put(0x153 /* œ */, "oe");
		mapping.put(0x178 /* Ÿ */, "Y");

		//--- CZECH (CS) ---//
		mapping.put(0x10c /* Č */, "Ch");
		mapping.put(0x10d /* č */, "ch");
		mapping.put(0x10e /* Ď */, "D");
		mapping.put(0x10f /* ď */, "d");
		mapping.put(0x11a /* Ě */, "E");
		mapping.put(0x11b /* ě */, "e");
		mapping.put(0x147 /* Ň */, "N");
		mapping.put(0x148 /* ň */, "n");
		mapping.put(0x158 /* Ř */, "Rzh");
		mapping.put(0x159 /* ř */, "rzh");
		mapping.put(0x160 /* Š */, "Sh");
		mapping.put(0x161 /* š */, "sh");
		mapping.put(0x164 /* Ť */, "T");
		mapping.put(0x165 /* ť */, "t");
		mapping.put(0x16e /* Ů */, "U");
		mapping.put(0x16f /* ů */, "u");
		mapping.put(0x17d /* Ž */, "Zh");
		mapping.put(0x17e /* ž */, "zh");

		//--- SLOVAK (SK) ---//
		// letters covered by CZECH [Č, č, Ď, ď , Ň, ň, Š, š, Ť, ť, Ž, ž]
		mapping.put(0x139 /* Ĺ */, "L");
		mapping.put(0x13a /* ĺ */, "l");
		mapping.put(0x13d /* Ľ */, "L");
		mapping.put(0x13e /* ľ */, "l");
		mapping.put(0x154 /* Ŕ */, "R");
		mapping.put(0x155 /* ŕ */, "r");

		//-- HUNGARIAN (HU) ---//
		mapping.put(0x150 /* Ő */, "Õ");
		mapping.put(0x151 /* ő */, "õ");
		mapping.put(0x170 /* Ű */, "Û");
		mapping.put(0x171 /* ű */, "û");

		//--- ROMANIAN (RO) ---//
		mapping.put(0x102 /* Ă */, "A");
		mapping.put(0x103 /* ă */, "a");
		mapping.put(0x218 /* Ș */, "S");
		mapping.put(0x219 /* ș */, "s");
		mapping.put(0x21a /* Ț */, "T");
		mapping.put(0x21b /* ț */, "t");

		//--- POLISH (PL) ---//
		mapping.put(0x104 /* Ą */, "A");
		mapping.put(0x105 /* ą */, "a");
		mapping.put(0x106 /* Ć */, "C");
		mapping.put(0x107 /* ć */, "c");
		mapping.put(0x118 /* Ę */, "E");
		mapping.put(0x119 /* ę */, "e");
		mapping.put(0x141 /* Ł */, "L");
		mapping.put(0x142 /* ł */, "l");
		mapping.put(0x143 /* Ń */, "N");
		mapping.put(0x144 /* ń */, "n");
		mapping.put(0x15a /* Ś */, "S");
		mapping.put(0x15b /* ś */, "s");
		mapping.put(0x179 /* Ź */, "Z");
		mapping.put(0x17a /* ź */, "z");
		mapping.put(0x17b /* Ż */, "Z");
		mapping.put(0x17c /* ż */, "z");

		//--- AZERBAIJANI (AZ) ---//
		// according to the wiki page when the Latin script was first introduced Ä was
		// selected to represent the sound /ae/, however, it was changed later to the
		// historic character schwa (Ə). for this reason, we decided to map the schwa
		// character back to Ä
		mapping.put(0x18f /* Ə */, "Ä");
		mapping.put(0x259 /* ə */, "ä");
		mapping.put(0x0c7 /* Ç */, "C");
		mapping.put(0x0e7 /* ç */, "c");
		mapping.put(0x11e /* Ğ */, "G");
		mapping.put(0x11f /* ğ */, "g");
		mapping.put(0x130 /* İ */, "I");
		mapping.put(0x131 /* ı */, "i");
		mapping.put(0x15e /* Ş */, "S");
		mapping.put(0x15f /* ş */, "s");

		//--- CROATIAN (CR) ---//
		// letters covered by CZECH [Č, č, Š, š, Ž, ž]
		// letters covered by POLISH [Ć, ć]

		//--- SERBIAN (SR) ---//
		// letters covered by CZECH [Č, č, Š, š, Ž, ž]
		// letters covered by POLISH [Ć, ć]
		mapping.put(0x111 /*đ*/, "d");
		mapping.put(0x110 /*Đ*/, "D");
		
		// the base mapping used across all languages
		GLOBAL_LANG_CODEPOINT_MAP = mapping;

		// currently, none of the languages deviate from the current transliteration
		// mapping, so we use the same mapping for all languages. if, however, we need
		// to make language specific changes (e.g. Ç is mapped differently for HR than
		// for SR), then we can simply clone the 'mapping' table and adjust the value
		// for the specific language.
		LANG_SPECIFIC_CODEPOINT_MAP = Collections.emptyMap();
	}

	private final CodepointCharacterMap mappings;

	public PdfTextTransliterator(Locale locale) {
		CodepointCharacterMap map = LANG_SPECIFIC_CODEPOINT_MAP.get(locale.getLanguage());
		if (map == null) {
			map = GLOBAL_LANG_CODEPOINT_MAP;
		}

		mappings = map;
	}

	public String transliterate(String input) {
		if (mappings == null || mappings.isEmpty() || StringUtils.empty(input)) {
			return input;
		}

		StringBuilder buf = new StringBuilder(input);

		int i=0;
		while (i < buf.length()) {
			int value = buf.charAt(i);
			if (value < 128) {
				// ASCII - nothing to map
				i++;
				continue;
			}

			String mapping = mappings.get(value);
			if (mapping == null) {
				// no mapping configured
				i++;
				continue;
			}

			buf.replace(i, i+1, mapping);
			i += mapping.length();
		}

		return buf.toString();
	}
}
