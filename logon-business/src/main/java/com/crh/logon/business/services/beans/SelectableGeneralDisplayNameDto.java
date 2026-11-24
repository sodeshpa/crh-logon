/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans;

/**
 * Extending the {@code GeneralDisplayNameDto} with the ability to store selected state.
 * Can come handy when it comes to checked comboboxes.
 * 
 * @author AdNovum Informatik AG
 */
public class SelectableGeneralDisplayNameDto extends GeneralDisplayNameDto {

	private static final long serialVersionUID = 1L;

	private boolean selected;

	public SelectableGeneralDisplayNameDto(Long id, String name, boolean selected) {
		super(id, name);
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String toString() {
		return "SelectableGeneralDisplayNameDto [id=" + getId() + ", name=" + getName() + ", selected=" + selected + "]";
	}

}
