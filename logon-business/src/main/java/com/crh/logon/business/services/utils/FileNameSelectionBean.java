/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;

/**
 * @author AdNovum Informatik AG
 */
public class FileNameSelectionBean implements Serializable {

	private String name;
	private Boolean isDirectory;
	
	public FileNameSelectionBean(String name, Boolean isDirectory) {
		
		this.name = name;
		this.isDirectory = isDirectory;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getIsDirectory() {
		return isDirectory;
	}

	public void setIsDirectory(Boolean isDirectory) {
		this.isDirectory = isDirectory;
	}
}
