/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;

import java.io.Serializable;

/**
 * @author AdNovum Informatik AG
 */
public class WagonDetailLoadBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String wagonNoteDisposition;
    private String wagonNoteRailwaySpedition;
    
	public String getWagonNoteDisposition() {
		return wagonNoteDisposition;
	}
	public void setWagonNoteDisposition(String wagonNoteDisposition) {
		this.wagonNoteDisposition = wagonNoteDisposition;
	}
	public String getWagonNoteRailwaySpedition() {
		return wagonNoteRailwaySpedition;
	}
	public void setWagonNoteRailwaySpedition(String wagonNoteRailwaySpedition) {
		this.wagonNoteRailwaySpedition = wagonNoteRailwaySpedition;
	}
}
