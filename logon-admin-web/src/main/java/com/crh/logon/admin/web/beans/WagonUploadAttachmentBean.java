/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;

import java.io.Serializable;

import org.apache.struts.upload.FormFile;

/**
 * Class that holds information about an attachment. Used in masters data->rail->maintenance tab
 * @author AdNovum Informatik AG
 */
public class WagonUploadAttachmentBean implements Serializable {
	
	private static final long serialVersionUID = 8935673873021510465L;

	private FormFile file;
	private Long index;
	
	private Long wagonRepAttId;
	
	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}


	public Long getWagonRepAttId() {
		return wagonRepAttId;
	}

	public void setWagonRepAttId(Long orderRepAttId) {
		this.wagonRepAttId = orderRepAttId;
	}

	public Long getIndex() {
		return index;
	}

	public void setIndex(Long index) {
		this.index = index;
	}

}
