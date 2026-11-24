/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class CSVReader {
	
	 private static final JcanLogger LOG =
	        JcanLoggerFactory.getLogger(CSVReader.class);
	 
	private BufferedReader reader;
	private String line;
	private static final char SEPARATOR = ';';
	private static final String DOUBLE_QUOTE = "\"";
	private static final String COMMENT_SYMBOL = "#";
	private static final char stateINIT = 'S';
	private static final char stateCOMMENT = '#';
	private static final char stateQUOTED_DATA = 'q';
	private static final char stateQUOTE_IN_QUOTED_DATA = 'Q';
	private static final char stateDATA = 'D';
	private static final char stateNEW_TOKEN = 'N';
	private static final char stateWHITESPACE = 'W';
	
	
	public CSVReader(BufferedReader reader) throws IOException {
		// [[
		Object[][] params = {{"reader", reader}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "CSVReader", params);
		}
		// ]]
		try {
			this.reader = reader;
			loadNextNonCommentLine();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public List next() throws IOException {
		// [[
		List result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "next");
		}
		// ]]
		try {
			if (line == null)
				throw new IOException("Read past end of file");
			List columns = columnsFromCSVRecord(line);
			loadNextNonCommentLine();
			return result = columns;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public boolean hasNext() {
		// [[
		boolean result = false;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "hasNext");
		}
		// ]]
		try {
			return result = line != null;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	void loadNextNonCommentLine() throws IOException {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadNextNonCommentLine");
		}
		// ]]
		try {
			do
				line = reader.readLine();
			while (line != null && line.startsWith(COMMENT_SYMBOL));
			if (line == null)
				reader.close();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	List columnsFromCSVRecord(String line) throws IOException {
		// [[
		List result = null;
		Object[][] params = {{"line", line}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "columnsFromCSVRecord", params);
		}
		// ]]
		try {
			char state = stateINIT;
			char ch;
			int i = 0;
			List tokens = new java.util.ArrayList();
			StringBuffer buffer = new StringBuffer();
			char[] charArray = line.toCharArray();
			while (i < charArray.length) {
				ch = charArray[i++];
				switch (state) {
				case stateINIT:
					switch (ch) {
					case '"':
						buffer.append(ch);
			
						state = stateQUOTED_DATA;
						break;
					case SEPARATOR :
						state = stateNEW_TOKEN;
						tokens.add(clean(buffer));
						buffer = new StringBuffer();
						break;
					case '\t': case ' ':
						break;
					case '#':
						state = stateCOMMENT;
						break;
					default:
						state = stateDATA;
					buffer.append(ch);
					break;
					}
					break;
				case stateCOMMENT:
					break;
				case stateQUOTED_DATA:
					switch (ch) {
					case '"':
						buffer.append(ch);
						state = stateQUOTE_IN_QUOTED_DATA;
						break;
					default:
						buffer.append(ch);
					break;
					}
					break;
				case stateQUOTE_IN_QUOTED_DATA:
					switch (ch) {
					case '"':
						state = stateQUOTED_DATA;
						break;
					case SEPARATOR :
						state = stateNEW_TOKEN;
						tokens.add(clean(buffer));
						buffer = new StringBuffer();
						break;
					case ' ': case '\t':
						break;
					case '#':
						tokens.add(clean(buffer));
						state = stateCOMMENT;
						break;
					default:
						throw new IOException(
								"badly formed CSV record:" + line);
					}
					break;
				case stateDATA:
					switch (ch) {
					case '#':
			
						tokens.add(clean(buffer));
						state = stateCOMMENT;
						break;
					case SEPARATOR:
						state = stateNEW_TOKEN;
						tokens.add(clean(buffer));
						buffer = new StringBuffer();
						break;
					default:
						buffer.append(ch);
					break;
					}
					break;
				case stateNEW_TOKEN:
					switch (ch) {
					case '#':
						tokens.add(clean(buffer));
						state = stateCOMMENT;
						break;
					case SEPARATOR:
						tokens.add(clean(buffer));
						buffer = new StringBuffer();
						break;
					case ' ': case '\t':
						state = stateWHITESPACE;
						break;
					case '"':
						buffer.append(ch);
						state = stateQUOTED_DATA;
						break;
					default:
						state = stateDATA;
					buffer.append(ch);
					break;
					}
					break;
				case stateWHITESPACE:
					switch (ch) {
					case '#':
						state = stateCOMMENT;
						break;
					case SEPARATOR:
						state = stateNEW_TOKEN;
						// ACCEPT NEW EMPTY COLUMN HERE??
						break;
					case '"':
						buffer.append(ch);
						state = stateQUOTED_DATA;
						break;
					case ' ': case '\t':
						break;
					default:
						state = stateDATA;
					buffer.append(ch);
					break;
					}
					break;
				default:
					break;
				}
			}
			if (state == stateQUOTED_DATA)
				throw new IOException("Unmatched quotes in line:\n" + line);
			if (state != stateCOMMENT)
				tokens.add(clean(buffer));
			return result = tokens;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	String clean(StringBuffer buffer) {
		// [[
		String result = null;
		Object[][] params = {{"buffer", buffer}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "clean", params);
		}
		// ]]
		try {
			String string = buffer.toString().trim();
			if (string.startsWith(DOUBLE_QUOTE))
				return result = string.substring(1, string.length() - 1);
			return result = string;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public void close(){
		
	}
}
