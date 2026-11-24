/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.TablebrowserService;
import com.crh.logon.persistence.entities.tablebrowser.TableMeta;
import com.crh.logon.persistence.entities.views.TableInfoEntity;


/**
 * This is tha base class for generated schema infomation used for
 * the generic table browser.
 * 
 * @author AdNovum Informatik AG
 */
public abstract class TableBean implements Serializable {

    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(TableBean.class);

	/**
	 * List of all fields of the table
	 */
	private final List fields = new ArrayList();
	/**
	 * Lists the key fields of the table. This information is redundant
	 * as it can be obtained using field.isKey()
	 */
	private final List keys = new ArrayList();
    /**
     * Lists the ids of key fields of the table. This information is redundant
     * as it can be obtained using field.isKey()
     */
    private final List keyIds = new ArrayList();
	/**
	 * Physical table name
	 */
	private final String name; 
	/**
	 * Entity class name 
	 */
	private final String className;
	
	private TableInfoEntity tableInfo;
	/**
	 * TableMeta information from DB
	 */
	private TableMeta tableMeta;
	
	/**
	 * Index in the fields array of the "referenced" field. 
	 * That is either the field with the name TableMeta.referencedField
	 * or the first PK field if no TableMeta.referencedFiled is defined
	 * or 0, if no PK is defined.
	 * Not valid until extendTableInfo is called.
	 */
	private int refFieldIdx;
	
	private final boolean isRefTable;
	
	private TablesBean tablesBean;
	
	private boolean initialized;
	
	protected TableBean(String name, String className, boolean isRefTable) {
		// [[
		Object[][] params = {{"name", name},
							{"className", className},
							{"isRefTable", isRefTable + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "TableBean", params);
		}
		// ]]
		try {
			this.name = name;
			this.className = className;
			refFieldIdx = 0;
			this.isRefTable = isRefTable;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public void initialize(TablesBean tablesBean) {
		
		this.tablesBean = tablesBean;
		this.initialized = true;
	}
	
	public boolean isInitialized() {
	
		return this.initialized;
	}
	
	public TablesBean getTablesBean() {
		
		return this.tablesBean;
	}
	
	/**
	 * Adds a field to the field list and to the keys list if necessary
	 * @param field
	 */
	protected void addField(FieldBean field) {
		// [[
		Object[][] params = {{"field", field}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addField", params);
		}
		// ]]
		try {
			fields.add(field);
			if (field.isKey()){
				keys.add(field);
				keyIds.add(fields.size() - 1);
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	/**
	 * Adds all fields of the table to this table. This is used 
	 * to handle embedded entities from the JPA mappings. 
	 * 
	 * @param table
	 * @param isKey Overwrites the isKey property for each field
	 */
	protected void addFields(TableBean table, boolean isKey){
		// [[
		Object[][] params = {{"table", table},
							{"isKey", isKey + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addFields", params);
		}
		// ]]
		try {
			Iterator i = table.getFields().iterator();
			while (i.hasNext()){
				FieldBean fb = (FieldBean)i.next();
				fb.setKey(isKey);
				addField(fb);
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * Extends the table information with metainformationb from the DB.
	 * The TableMeta information can only be available after this function has bee called. 
	 * 
	 * @param tbs
	 */
	public void extendTableInfo(TablebrowserService tbs){
		
		if (tableMeta == null) {
			tableMeta = tbs.fetchTable(name);
			refFieldIdx = -1;
			Iterator i = fields.iterator();
			int idx = 0;
			while(i.hasNext()){
				FieldBean fb = (FieldBean)i.next();
				if ((refFieldIdx == -1) && fb.isKey()){
					refFieldIdx = idx;
				}
				if ((tableMeta != null) && (tableMeta.getReferencedField() != null) && (tableMeta.getReferencedField().equals(fb.getName()))){
					refFieldIdx = idx;
				}
				fb.extendFieldInfo(tbs, tableMeta);
				idx++;
			}
			if (refFieldIdx == -1){
				refFieldIdx = 0;
			}
		}
	}
	
	public void loadColumnAdditionalInfo(TablebrowserService tbs){
		Iterator i = fields.iterator();
		while(i.hasNext()){
    		FieldBean fb = (FieldBean)i.next();
    		fb.loadColumnAdditionalInfo(tbs, name);
		}
	}
	
	public void loadTableAdditionalInfo(TablebrowserService tbs){
		if (tableInfo == null) {
			tableInfo = tbs.loadTableAdditionalInfo(name);
		}
	}
	
	public List getFields() {
		return fields;
	}
	
	public boolean containsField(String fieldName){
		Iterator i = fields.iterator();
		while(i.hasNext()){
    		FieldBean fb = (FieldBean)i.next();
    		if (fb.getName().equalsIgnoreCase(fieldName)) {
    			
    			return true;
    		}
		}
		
		return false;
	}

    public List getKeys() {
        return keys;
    }

    public List getKeyIds() {
        return keyIds;
    }

	public String getName() {
		return name;
	}

	public String getClassName() {
		return className;
	}
	
	public TableInfoEntity getTableInfo() {
		return tableInfo;
	}

	/**
	 * Return TableMeta informatio from DB if it is available. ExtentTableInfo has to be called 
	 * before this information is available
	 * 
	 * @return
	 */
	public TableMeta getTableMeta() {
		return tableMeta;
	}

	public int getRefFieldIdx() {
		return refFieldIdx;
	}

	public boolean isRefTable() {
		return isRefTable;
	}
}
