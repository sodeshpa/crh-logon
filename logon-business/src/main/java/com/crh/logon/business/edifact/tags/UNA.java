/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.tags;

import java.io.IOException;
import java.io.Writer;

import com.crh.logon.business.edifact.parser.MsgContext;
import com.crh.logon.business.edifact.parser.Segment;
import com.crh.logon.business.services.utils.EdifactException;

/**
 * @author AdNovum Informatik AG
 */
public class UNA extends Tag {

	public static final String NAME = "UNA";
	public static final int LENGTH = 9;
	public static final char COMPONENT_DATA_ELEMENT_SEPERATOR = ':';
	public static final char DATA_ELEMENT_SEPERATOR = '+';
	public static final char DECIMAL_NOTATION = '.';
	public static final char RELEASE_CHARACTER = '?';
	public static final char SEGMENT_TERMINATOR = '\'';
	
	public UNA() {

		super(NAME, "Service String Advice");
	}
	
	@Override
	public void parse(Segment segment, MsgContext ctxt) throws EdifactException {
		
		throw new EdifactException("UNA tag is not supported");
	}
	
	@Override
	public void write(Writer w) throws IOException {

		w.write(getName() + COMPONENT_DATA_ELEMENT_SEPERATOR + DATA_ELEMENT_SEPERATOR + DECIMAL_NOTATION + RELEASE_CHARACTER + " " + SEGMENT_TERMINATOR);
	}

	@Override
	protected DataElementMeta[] getDataElementMetas() {

		return null;
	}
}
