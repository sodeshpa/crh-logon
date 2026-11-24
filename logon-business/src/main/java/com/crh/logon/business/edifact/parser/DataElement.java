/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.parser;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.edifact.tags.UNA;
import com.crh.logon.business.services.utils.EdifactException;

/**
 * @author AdNovum Informatik AG
 */

//----------------------------------------
//|TAG |+|SIMPLE       |+|COMPOSITE    |'|
//|    | |DATA ELEMENT | |DATA ELEMENT | |
//---|--------------|----------|-----|----
//   |              |          |     |
//--------------   -------   -------------
//|Code|:|Value|   |Value|   |COMP|:|COMP|
//--------------   -------   |D/E | |D/E |
//                           --|------|---
//                             |       |
//                          ------- -------
//                          |Value| |Value|
//                          ------- -------

//A SIMPLE DATA ELEMENT contains:
//	- A single data element value
//	A COMPOSITE DATA ELEMENT contains:
//	- Component data elements
//	A COMPONENT DATA ELEMENT contains:
//	- A single data element value
public class DataElement {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DataElement.class);
	private static final String RELEASE_EXPRESSION = "\\" + UNA.RELEASE_CHARACTER;
	private static final Pattern RELEASED_RELEASE_CHARACTER_PATTERN = Pattern.compile(RELEASE_EXPRESSION + RELEASE_EXPRESSION);
	
	private static final String REQUIRES_RELEASE_EXPRESSION = "([" + UNA.COMPONENT_DATA_ELEMENT_SEPERATOR + UNA.DATA_ELEMENT_SEPERATOR + UNA.RELEASE_CHARACTER + UNA.SEGMENT_TERMINATOR + "])";
	private static final String RELEASED_CHARACTER_EXPRESSION = UNA.RELEASE_CHARACTER + "$1";
	
	private String rawDataElement; // used for parsing only, holds the unparsed string.
	private List<String> values;
	
	public void parse(String dataElementDesc) {
		
		this.rawDataElement = dataElementDesc;
		values = new ArrayList<String>();
		if (rawDataElement != null) {
			String[] components = EdifactParser.split(rawDataElement, UNA.COMPONENT_DATA_ELEMENT_SEPERATOR); 
			for (String c: components) {
				values.add(RELEASED_RELEASE_CHARACTER_PATTERN.matcher(c).replaceAll(UNA.RELEASE_CHARACTER + ""));
			}
		}
	}
	
	public String getValue(int pos) {
		
		String result = null;
		
		if (pos < values.size()) {
			result = values.get(pos);
		}
		
		return result;
	}
	
	public String getValue() {
		
		if (values.size() > 1) {
			
			throw new EdifactException("getValue() invoked for composite data element '" + rawDataElement + "'");
		}
		
		return getValue(0); 
	}
	
	public List<String> getValues() {
		if (values == null) {
			values = new ArrayList<String>();
		}
		
		return values;
	}
	
	public void setValue(int index, String value) {
	
		List<String> values = getValues();
		if (index < values.size()) {
			values.set(index, value);
		}
		else {
			for (int i=values.size();i<=index;i++) {
				if (i != index) {
					values.add("");
				}
				else {
					values.add(value);
				}
			}
		}
	}

	public void write(Writer w) throws IOException {
		
		if (values != null) {
			for (Iterator<String> it=values.iterator();it.hasNext();) {
				String value = it.next();
				if (value != null) {
					// escape the value
					w.write(value.replaceAll(REQUIRES_RELEASE_EXPRESSION, RELEASED_CHARACTER_EXPRESSION));
				}
				if (it.hasNext()) {
					w.write(UNA.COMPONENT_DATA_ELEMENT_SEPERATOR);
				}
			}
		}
	}
}
