/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.persistence.entities.tablebrowser;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import ch.nevis.gdi.jpa.BaseEntity;


/**
 * Contains metainformation about the tables in the DB, that is used
 * by the generic table browser. This metainformation completes 
 * the hardwired information contained in generated table beans.
 * @author AdNovum Informatik AG
 */
@Entity
@NamedQueries({
    @NamedQuery(name="allTableMeta",
        query="SELECT x FROM TableMeta x"),
    @NamedQuery(name="tableMetaByName",
                query="SELECT x FROM TableMeta x WHERE x.name = ?1")
})
@Table(name = "TLGNT_TABLE_META")
public class TableMeta implements BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
    @Column(name = "TABLE_META_ID")
	private long id;

	/**
	 * Physical table name. 
	 * Used as search parameter for retrieving the metainformation about the table
	 */
	@Column(name = "TABLE_NAME", unique = true, length = 100)
	private String name;
	
	/**
	 * '1' or '0', '1' if the generic table browser is allowed to insert rows in this table
	 * Default '0'
	 */
	@Column(name = "INSERT_ALLOWED")
	private Boolean insertAllowed;

	/**
	 * '1' or '0', '1' if the generic table browser is allowed to update rows in this table
	 * Default '0'
	 */
	@Column(name = "UPDATE_ALLOWED")
	private Boolean updateAllowed;

	/**
	 * '1' or '0', '1' if the generic table browser is allowed to delete rows in this table
	 * Default '0'
	 */
	@Column(name = "DELETE_ALLOWED")
	private Boolean deleteAllowed;
	
	/**
	 * Name of the field to be diplayed instead of the primary key in a table that
	 * references records of this table as a primary key.
	 * The field needs to have a basic type (String or Number).
	 * Default = Primary Key
	 */
	@Column(name = "REFERENCED_FIELD", length = 100)
	private String referencedField;
	
	/**
	 * '1' or '0', '1' means that a dropdown box is displayed in the edit dialog of
	 * a table referencing this table, '0' means that a search popup will be displayed.
	 * Set to '1' only if the table contains few values (not too much for a dropdown box)
	 * and if referencedField is set. 
	 * Default '0'
	 */
	@Column(name = "USE_DROPDOWN")
	private Boolean useDropdown;
	
	
    @OneToMany(fetch=FetchType.EAGER, mappedBy="pk.tableMeta", targetEntity=ColumnMeta.class)
    @MapKey(name="name")
    private Map<String,ColumnMeta> columns;


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getInsertAllowed() {
		return insertAllowed;
	}

	public void setInsertAllowed(Boolean insertAllowed) {
		this.insertAllowed = insertAllowed;
	}

	public Boolean getUpdateAllowed() {
		return updateAllowed;
	}

	public void setUpdateAllowed(Boolean updateAllowed) {
		this.updateAllowed = updateAllowed;
	}

	public Boolean getDeleteAllowed() {
		return deleteAllowed;
	}

	public void setDeleteAllowed(Boolean deleteAllowed) {
		this.deleteAllowed = deleteAllowed;
	}

	public Map getColumns() {
		return columns;
	}

	public void setColumns(Map columns) {
		this.columns = columns;
	}

	public String getReferencedField() {
		return referencedField;
	}

	public void setReferencedField(String referencedField) {
		this.referencedField = referencedField;
	}

	public Boolean getUseDropdown() {
		return useDropdown;
	}
	
	public void setUseDropdown(Boolean useDropdown) {
		this.useDropdown = useDropdown;
	}

	
}
