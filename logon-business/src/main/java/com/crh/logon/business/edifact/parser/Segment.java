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

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.edifact.tags.UNA;
import com.crh.logon.business.services.utils.EdifactException;

/**
 * @author AdNovum Informatik AG
 */
//-----------------------------------------
//|UNG |'|Message |MESSAGE |Message |UNE|'|
//--------------------|--------------------
//                    |
//+-------------------+-------------------+
//|                                       |
//-----------------------------------------
//|UNH |'|Data    |DATA    |Data    |UNT|'|
//|    | |segment |SEGMENT |segment |   | |
//-------------------|---------------------

//A SEGMENT contains:
//	- A Segment TAG
//	- Simple data elements or
//	- Composite data elements or both as
//	  applicable
public class Segment {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(Segment.class);
		
	private String rawSegment; // used when parsing, holds the unparsed string.
	private List<DataElement> dataElements;
	private String tagName;
		
	public void parse(String segmentDesc) {
		
		this.rawSegment = segmentDesc;
		dataElements = new ArrayList<DataElement>();
		String[] components = EdifactParser.split(rawSegment, UNA.DATA_ELEMENT_SEPERATOR);
		if ( (components == null) || (components.length == 0) ) {
			
			throw new EdifactException("Error parsing segment '" + rawSegment + "'");
		}
		try {
			DataElement de = new DataElement();
			de.parse(components[0]);
			tagName = de.getValue();
		} catch (EdifactException e) {
			
			throw new EdifactException("repeating is not supported");
		}
		for (int i=1;i<components.length;i++) {
			DataElement de = new DataElement();
			de.parse(components[i]);
			dataElements.add(de);
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Tag '" + tagName + "'");
			for (int i=0;i<dataElements.size();i++) {
				StringBuilder indentation = new StringBuilder();
				for (int k=0;k<i;k++) { 
					indentation.append(" ");
				}
				for (int j=0;j<dataElements.get(i).getValues().size();j++) {
					LOG.debug(indentation + "[" + i + "/" + j + "] " + dataElements.get(i).getValues().get(j));
				}
			}			
		}
	}
	
	public String getTagName() {
		
		return tagName;
	}
	
	public void setTagName(String tagName) {
		
		this.tagName = tagName;
	}
	
	public DataElement getDataElement(int pos) {
		
		DataElement result = null;
		List<DataElement> dataElements = getDataElements();
		if (pos < dataElements.size()) {
			result = dataElements.get(pos);
		}
		
		return result;
	}
	
	public void setDataElement(int pos, DataElement de) {
		
		List<DataElement> dataElements = getDataElements();
		if (pos < dataElements.size()) {
			dataElements.set(pos, de);
		}
		else {
			for (int i=dataElements.size();i<=pos;i++) {
				if (i != pos) {
					dataElements.add(new DataElement());
				}
				else {
					dataElements.add(de);
				}
			}
		}
	}
	
	public List<DataElement> getDataElements() {
		
		if (dataElements == null) {
			
			dataElements = new ArrayList<DataElement>();
		}
		
		return dataElements;
	}
	
	public void write(Writer w) throws IOException {
		
		w.write(tagName);
		if ( (dataElements != null) && (!dataElements.isEmpty()) ) {
			w.write(UNA.DATA_ELEMENT_SEPERATOR);
			for (Iterator<DataElement> it=dataElements.iterator();it.hasNext();) {
				DataElement de = it.next();
				de.write(w);
				if (it.hasNext()) {
					w.write(UNA.DATA_ELEMENT_SEPERATOR);
				}
			}
		}
		w.write(UNA.SEGMENT_TERMINATOR);
	}
}