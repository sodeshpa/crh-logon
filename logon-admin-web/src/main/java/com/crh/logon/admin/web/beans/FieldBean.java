/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;


import java.io.Serializable;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.TablebrowserService;
import com.crh.logon.persistence.entities.tablebrowser.ColumnMeta;
import com.crh.logon.persistence.entities.tablebrowser.TableMeta;
import com.crh.logon.persistence.entities.views.ColumnInfoEntity;

/**
 * @author AdNovum Informatik AG
 */
public class FieldBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(FieldBean.class);

	private String name;
	private String classFieldName;
	private boolean isKey;
	private String sequenceName;
	private TypeBean type;
	
	private ColumnInfoEntity columnInfo;
	private ColumnMeta columnMeta; 

	public FieldBean(String name, String classFieldName, boolean isKey, String sequenceName, TypeBean type) {
		// [[
		Object[][] params = {{"name", name},
							{"classFieldName", classFieldName},
							{"isKey", isKey + ""},
							{"sequenceName", sequenceName},
							{"type", type}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "FieldBean", params);
		}
		// ]]
		try {
			this.name = name;
			this.classFieldName = classFieldName;
			this.isKey = isKey;
			this.sequenceName = sequenceName;
			this.type = type;
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
	 * Extends Field information with ColumnMeta information from DB.
	 * 
	 * @param tbs
	 * @param tableMeta
	 */
	public void extendFieldInfo(TablebrowserService tbs, TableMeta tableMeta) {
		if ( (tableMeta != null) && (columnMeta == null) ) {
			columnMeta = (ColumnMeta)tableMeta.getColumns().get(name);
		}
		type.extendTypeInfo(tbs);
	}
	
	public void loadColumnAdditionalInfo(TablebrowserService tbs, String tableName){
		if (columnInfo == null) {
			columnInfo = tbs.loadColumnAdditionalInfo(tableName, name);
		}
	}
	
	public boolean isKey() {
		// [[
		LOG.debug("isKey()");
		// ]]
		return isKey;
	}

	public void setKey(boolean isKey) {
		this.isKey = isKey;
	}

	public String getSequenceName() {
		return sequenceName;
	}

	public String getName() {
		return name;
	}

	public String getClassFieldName() {
		return classFieldName;
	}
	
	public TypeBean getType() {
		return type;
	}

	/**
	 * returns ColumnMeta information, if available. 
	 * Information is never available before extendFieldInfo has been called.
	 * @return
	 */
	public ColumnMeta getColumnMeta() {
		return columnMeta;
	}

	public ColumnInfoEntity getColumnInfo() {
		return columnInfo;
	}
}
