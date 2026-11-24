/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.order.web.dto;

public class FileTransfer {

	private final String name;
	private final String mimeType;
	private final byte[] content;

	public FileTransfer(String name, String mimeType, byte[] content) {
		this.name = name;
		this.mimeType = mimeType;
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public String getMimeType() {
		return mimeType;
	}

	public byte[] getContent() {
		return content;
	}
}
