/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.persistence.dto;

import java.io.Serializable;


/**
 * @author AdNovum Informatik AG
 */
public class BlobDTO implements Serializable {

	private String mime;
	private String name;
	private String extension;
	private byte[] byteStream;

	public String getMime() {
		return mime;
	}
	public void setMime(String mime) {
		this.mime = mime;
	}
	public byte[] getStream() {
		return byteStream;
	}
	public void setStream(byte[] byteStream) {
		this.byteStream = byteStream;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	
	@Override
	public String toString() {
		return "blob of MIME type: " + getMime();
	}

}
