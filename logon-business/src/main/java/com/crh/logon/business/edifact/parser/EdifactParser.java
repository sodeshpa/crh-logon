/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.parser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.EdifactException;
import com.crh.logon.business.services.utils.EdifactParseException;
import com.crh.logon.business.edifact.tags.BGM;
import com.crh.logon.business.edifact.tags.CNI;
import com.crh.logon.business.edifact.tags.DTM;
import com.crh.logon.business.edifact.tags.EQA;
import com.crh.logon.business.edifact.tags.EQD;
import com.crh.logon.business.edifact.tags.FTX;
import com.crh.logon.business.edifact.tags.LOC;
import com.crh.logon.business.edifact.tags.RFF;
import com.crh.logon.business.edifact.tags.STS;
import com.crh.logon.business.edifact.tags.TDT;
import com.crh.logon.business.edifact.tags.Tag;
import com.crh.logon.business.edifact.tags.UNA;
import com.crh.logon.business.edifact.tags.UNB;
import com.crh.logon.business.edifact.tags.UNH;
import com.crh.logon.business.edifact.tags.UNT;
import com.crh.logon.business.edifact.tags.UNZ;

/**
 * @author AdNovum Informatik AG
 */
public class EdifactParser {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(EdifactParser.class);
	private static final Map<String, Class<? extends Tag>> TAG_LOOKUP_TABLE = new HashMap<String, Class<? extends Tag>>();
	private boolean firstSegment = true;
			
	static {
		registerTag(BGM.class);
		registerTag(CNI.class);
		registerTag(DTM.class);
		registerTag(EQA.class);
		registerTag(EQD.class);
		registerTag(FTX.class);
		registerTag(LOC.class);
		registerTag(RFF.class);
		registerTag(STS.class);
		registerTag(TDT.class);
		registerTag(UNB.class);
		registerTag(UNH.class);
		registerTag(UNT.class);
		registerTag(UNZ.class);
	}
	
	
	private static void registerTag(Class<? extends Tag> tagClass) {
		
		// [[
		Object[][] params = {{"tagClass", tagClass}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(EdifactParser.class, "registerTag", params);
		}
		// ]]
		try {
			
			if (TAG_LOOKUP_TABLE.containsKey(tagClass.getSimpleName())) {
				LOG.info("The tag " + tagClass.getSimpleName() + " was registered already, overwriting previous entry");
			}
			TAG_LOOKUP_TABLE.put(tagClass.getSimpleName(), tagClass);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	
	private String readSegment(InputStreamReader reader) throws IOException, EdifactException {
		
		// [[
		String result = null;
		Object[][] params = {{"reader", reader}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "readSegment", params);
		}
		// ]]
		try {
			StringBuilder sb = new StringBuilder();
			int c = reader.read();
			while ( (c != -1) && (Character.isWhitespace((char)c)) ) {
				c = reader.read();
			}
			boolean escaping = false;
			while (c != -1) {
				if ( ((char)c == UNA.RELEASE_CHARACTER) && (!escaping) ) {
					escaping = true;
					c = reader.read();
					continue;
				}
				if ( ((char)c == UNA.SEGMENT_TERMINATOR) && (!escaping) ) {
					break;
				}
				if ( ((char)c != UNA.SEGMENT_TERMINATOR) && (escaping) ) {
					// escape character was not escaping the current seperator; put the escape char back.
					sb.append(UNA.RELEASE_CHARACTER);
				}
				sb.append((char)c);
				escaping = false;
				
				if ( (firstSegment) && (sb.toString().equals(UNA.NAME)) ) {
					// special "UNA" tag
					for (int i=0;i<UNA.LENGTH - UNA.NAME.length();i++) {
						c = reader.read();
						if (c == -1) {
			
							throw new EdifactParseException("EOF encountered parsing UNA " + sb.toString());
						}
						sb.append((char)c);
					}
				}
				else {
					// other tags
					c = reader.read();		
				}
			}
			String s = sb.toString();
			if ( (s.length() > 0) && (c == -1) ) {
				
				throw new EdifactParseException("Segment not terminated " + s);
			}
						
			return result = s.length()>0?s:null;
		}
		finally {
			firstSegment = false;
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	

	public void parse(InputStreamReader reader, Handler handler) {
		
		// [[
		Object[][] params = {{"reader", reader},
							{"handler", handler}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "parse", params);
		}
		// ]]
		try {
			MsgContext ctxt = new MsgContext();
			try {
				firstSegment = true;
				String segment = readSegment(reader);
				if (segment.startsWith(UNA.NAME)) {
					LOG.debug("'" + UNA.NAME + "' tag not supported, ignoring and using defaults");
					segment = readSegment(reader);
				}
				while (segment != null) {
					Segment parsedSegment = new Segment();
					parsedSegment.parse(segment);
					Class<? extends Tag> tagClass = TAG_LOOKUP_TABLE.get(parsedSegment.getTagName());
					if (tagClass != null) {
						Tag tag = tagClass.newInstance();
						tag.parse(parsedSegment, ctxt);
						ctxt.tag(tag);
						handler.tag(tag);
					}
					else {
						EdifactException warning = new EdifactException("No parser for tag '" + parsedSegment.getTagName() + "'  found");
						ctxt.warning(warning);
						handler.warning(warning);
					}
					segment = readSegment(reader);
				}
			}
			catch (EdifactException ex) {
				throw ex;
			}
			catch (Exception ex) {
				throw new EdifactParseException(ex);
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
	
	
	public static String[] split(String in, char seperator) {
		
		List<String> components = new ArrayList<String>();
		if (in != null) {
			StringBuilder sb = new StringBuilder();
			boolean escaping = false;
			for (int i=0;i<in.length();i++) {
				if ( (in.charAt(i) == UNA.RELEASE_CHARACTER) && (!escaping) ) {
					escaping = true;
					continue;
				}
				if ( (in.charAt(i) == seperator) && (!escaping) ) {
					// split
					components.add(sb.toString());
					sb = new StringBuilder();
				}
				else {
					if ( (in.charAt(i) != seperator) && (escaping) ) {
						// escape character was not escaping the current seperator; put the escape char back.
						sb.append(UNA.RELEASE_CHARACTER);
					}
					sb.append(in.charAt(i));
				}
				escaping = false;
			}
			components.add(sb.toString());
		}
		
		return components.toArray(new String[0]);
	}
}
