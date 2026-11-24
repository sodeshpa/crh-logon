/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.ecmservice;

import java.util.Calendar;
import java.util.Date;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;

/**
 * @author AdNovum Informatik AG
 */
public class PdfData {

	private Date creationDate;
	private String fileName;
	private byte[] data;
	private String mimeType;


	public Calendar getCreationDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(creationDate);
		return calendar;
	}

	public String getFileName() {
		return fileName;
	}

	public DataHandler getData() {
		return new DataHandler(new ByteArrayDataSource(data, mimeType));
	}

	public void setCreationDate(Date docTs) {
		this.creationDate = docTs;
		
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public void setMimeType(String type) {
		this.mimeType = type;
	}

	@Override
	public String toString() {
		return "PdfData [creationDate=" + creationDate + ", fileName="
				+ fileName + ", mimeType=" + mimeType + "]";
	}
	
}
