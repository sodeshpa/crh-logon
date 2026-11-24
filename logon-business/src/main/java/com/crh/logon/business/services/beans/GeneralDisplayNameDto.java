/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans;

import java.io.Serializable;

/**
 * This class is used to transfer data between business and web tier. It can be
 * used in case of a typical dropdown, where the id is a long value (comes from
 * an entity.id), and the name is a string to display on the gui.
 * 
 * Don't extend the member list of this class! This class is intended to be used
 * to transfer small amount of data.
 * 
 * @author AdNovum Informatik AG
 */
public class GeneralDisplayNameDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	
	public GeneralDisplayNameDto(Long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeneralDisplayNameDto other = (GeneralDisplayNameDto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GeneralDisplayNameDto [id=" + id + ", name=" + name + "]";
	}

}
