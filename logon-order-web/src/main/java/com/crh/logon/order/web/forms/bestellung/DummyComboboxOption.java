/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.bestellung;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author AdNovum Informatik AG
 */
public class DummyComboboxOption implements Serializable {

	private String id;
	private String label;
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	public DummyComboboxOption(String id, String label) {
		this.id = id;
		this.label = label + " - " + id;
		if (!label.startsWith("Dummy")) {
			this.label = "Dummy " + this.label;
		}
	}
	
	public static List getNDummyComboboxOption(int n, String label) {
		ArrayList list = new ArrayList();
		for(int i=0; i<n; i++) {
			DummyComboboxOption opt = new DummyComboboxOption(i + "", label);
			list.add(opt);
		}
		return list;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
}
