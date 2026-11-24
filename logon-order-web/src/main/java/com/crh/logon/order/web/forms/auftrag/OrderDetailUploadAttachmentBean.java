/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.auftrag;

import java.io.Serializable;

import org.apache.struts.upload.FormFile;


public class OrderDetailUploadAttachmentBean implements Serializable {

	private static final long serialVersionUID = -8910104051337644556L;

	
	private String type;
	private FormFile file;
	private Long index;
	
	private Long orderRepAttId;
	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}


	public Long getOrderRepAttId() {
		return orderRepAttId;
	}

	public void setOrderRepAttId(Long orderRepAttId) {
		this.orderRepAttId = orderRepAttId;
	}

	public Long getIndex() {
		return index;
	}

	public void setIndex(Long index) {
		this.index = index;
	}
	
}
