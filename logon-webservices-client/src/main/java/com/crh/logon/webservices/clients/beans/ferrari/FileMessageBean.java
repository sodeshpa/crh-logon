/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.ferrari;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public final class FileMessageBean implements Serializable {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(FileMessageBean.class);

	private final String number;
	private final String mimeType;
	private final String fileName;
	private final byte[] fileContent;

	/**
	 * Default constructor.
	 * 
	 * @param number
	 * @param mimeType
	 * @param filename
	 * @param fileContent
	 */
	public FileMessageBean(String number, String mimeType, String fileName,
			byte[] fileContent) {
		// [[
		Object[][] params = {{"number", number},
							{"mimeType", mimeType},
							{"fileName", fileName},
							{"fileContent", fileContent}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "FileMessageBean", params);
		}
		// ]]
		try {
			this.number = number;
			this.mimeType = mimeType;
			this.fileName = fileName;
			this.fileContent = fileContent;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * @return
	 */
	byte[] getFileContent() {
		return this.fileContent;
	}

	/**
	 * @return
	 */
	String getFileName() {
		return this.fileName;
	}

	/**
	 * @return
	 */
	String getMimeType() {
		return this.mimeType;
	}

	/**
	 * @return
	 */
	String getNumber() {
		return this.number;
	}

	/* (non-Javadoc)
	 * @see com.crh.logon.webservices.clients.beans.BaseBean#toString()
	 */
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);

		builder.append("number", this.number);
		builder.append("mimeType", this.mimeType);
		builder.append("fileName", this.fileName);
		builder.append("fileContent", (this.fileContent == null ? "null"
				: this.fileContent.hashCode()));

		return builder.toString();
	}

}
