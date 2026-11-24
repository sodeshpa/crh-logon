/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.persistence.entities.tablebrowser;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import ch.nevis.gdi.jpa.BaseEntity;

/**
 * Contains metainformation about fields in the DB, that is used
 * by the generic table browser. This metainformation completes
 * the hardwired information contained in generated field beans. 
 * @author AdNovum Informatik AG
 */
@Entity 
@Table(name = "TLGNT_COLUMN_META")
public class ColumnMeta implements BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * Composite primary key made of a reference to a table
	 * an a unique field id inside that table.
	 */
	@Id
	@Column(name = "COLUMN_META_ID")
	private ColumnMetaKey pk;
	
	/**
	 * Physical field name. 
	 * Used as search parameter for retrieving the metainformation about the field
	 */	
	@Column(name = "COLUMN_NAME", length = 100)
	private String name;
	
	/**
	 * '1' or '0', '1' if the generic table browser is allowed to update this field.
	 * Forced '0' for primary key fields
	 * Default '1'
	 */
	@Column(name = "UPDATE_ALLOWED")
	private Boolean updateAllowed;
	
	@Column(name = "MIME_TYPE")
	private String mimeType;
	
	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public ColumnMetaKey getPk() {
		return pk;
	}

	public void setPk(ColumnMetaKey pk) {
		this.pk = pk;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getUpdateAllowed() {
		return updateAllowed;
	}

	public void setUpdateAllowed(Boolean updateAllowed) {
		this.updateAllowed = updateAllowed;
	}
}
