/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.persistence.tools;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import au.com.bytecode.opencsv.CSVReader;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class OraDbDiff {
	
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(OraDbDiff.class);

	private static final String DEFAULT_CHARSET = "UTF-8";

    private TreeSet<RowBean> extractedSchemaXML;
    private TreeSet<RowBean> extractedSchemaDB;
    
    public static void main(String[] args) throws Exception {
		// [[
		Object[][] params = {{"args", args}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(OraDbDiff.class, "main", params);
		}
		// ]]
		try {
			OraDbDiff oraDbDiff = new OraDbDiff();
			oraDbDiff.execute();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

    private void execute() throws Exception {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "execute");
		}
		// ]]
		try {
			extractedSchemaDB = fetchDBSchema();
			extractedSchemaXML = fetchXMLSchema();
			
			TreeSet<RowBean> differenceDB = new TreeSet<RowBean>(extractedSchemaDB);
			differenceDB.removeAll(extractedSchemaXML);
			
			TreeSet<RowBean> differenceXML = new TreeSet<RowBean>(extractedSchemaXML);
			differenceXML.removeAll(extractedSchemaDB);
			if (!differenceDB.equals(differenceXML)) {
				if (LOG.isInfoEnabled()) {
					LOG.info("Not present in the XML mappings: " + differenceDB);
					LOG.info("Not present on the database: " + differenceXML);
				}
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
	 * @adnlog.exclude return
	 */
	private TreeSet<RowBean> fetchXMLSchema() throws Exception {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchXMLSchema");
		}
		// ]]
		try {
			TreeSet<RowBean> set = new TreeSet<RowBean>(); 
			
			InputStreamReader inputStreamReader = new InputStreamReader(
					new FileInputStream("build/generated/gdi/xmlschema2csv/simple_schema.csv"),
							Charset.forName(DEFAULT_CHARSET));
			CSVReader reader = new CSVReader(inputStreamReader, ';');
			List lines = reader.readAll();
			
			for (Iterator<String[]> iterator = lines.iterator(); iterator.hasNext();) {
				String[] line = iterator.next();
				
				if (line == null || line.length != 4) {
					continue;
				}
				
				if (line[0] != null && line[0].startsWith("#")) {
					continue;
				}
				
				RowBean rowBean = new RowBean();
				rowBean.setTableName(line[0].toUpperCase(Locale.ENGLISH));
				rowBean.setColumnName(line[1].toUpperCase(Locale.ENGLISH));
				rowBean.setDataType(line[2].toUpperCase(Locale.ENGLISH));
				rowBean.setNullable(Boolean.valueOf(line[3]));
				set.add(rowBean);
			}
			return  set; 
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
	 * @adnlog.exclude return
	 */
	private TreeSet<RowBean> fetchDBSchema() throws Exception {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchDBSchema");
		}
		// ]]
		try {
			TreeSet<RowBean> set = new TreeSet<RowBean>(); 
			
			InputStreamReader inputStreamReader = new InputStreamReader(
					new FileInputStream("build/generated/gdi/dbschema2csv/simple_schema.csv"),
					Charset.forName(DEFAULT_CHARSET));
			CSVReader reader = new CSVReader(inputStreamReader, ';');
			List lines = reader.readAll();
			
			for (Iterator<String[]> iterator = lines.iterator(); iterator.hasNext();) {
				String[] line = iterator.next();
				
				if (line == null || line.length != 4) {
					continue;
				}
				
				if (line[0] != null && line[0].startsWith("#")) {
					continue;
				}
				
				RowBean rowBean = new RowBean();
				rowBean.setTableName(line[0].toUpperCase(Locale.ENGLISH));
				rowBean.setColumnName(line[1].toUpperCase(Locale.ENGLISH));
				rowBean.setDataType(line[2].toUpperCase(Locale.ENGLISH));
				rowBean.setNullable(Boolean.valueOf(line[3]));
				set.add(rowBean);
			}
			return  set; 
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	private class RowBean implements Comparable<RowBean> {
		
		private String tableName;
		private String columnName;
		private String dataType;
		private boolean nullable;
		
		@Override
		public int hashCode() {
			final int prime = 37;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((columnName == null) ? 0 : columnName.hashCode());
			result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
			result = prime * result + (nullable ? 1231 : 1237);
			result = prime * result + ((tableName == null) ? 0 : tableName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			RowBean other = (RowBean) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (columnName == null) {
				if (other.columnName != null) {
					return false;
				}
			}
			else if (!columnName.equals(other.columnName)) {
				return false;
			}
			if (dataType == null) {
				if (other.dataType != null) {
					return false;
				}
			}
			else if (!dataType.equals(other.dataType)) {
				return false;
			}
			if (nullable != other.nullable) {
				return false;
			}
			if (tableName == null) {
				if (other.tableName != null) {
					return false;
				}
			}
			else if (!tableName.equals(other.tableName)) {
				return false;
			}
			return true;
		}
		
		public String getTableName() {
			return tableName;
		}

		public void setTableName(String tableName) {
			this.tableName = tableName;
		}

		public String getColumnName() {
			return columnName;
		}

		public void setColumnName(String columnName) {
			this.columnName = columnName;
		}

		public String getDataType() {
			return dataType;
		}

		public void setDataType(String dataType) {
			this.dataType = dataType;
		}

		public boolean isNullable() {
			return nullable;
		}

		public void setNullable(boolean nullable) {
			this.nullable = nullable;
		}

		private OraDbDiff getOuterType() {
			return OraDbDiff.this;
		}

		public int compareTo(RowBean other) {
			String key = this.tableName + this.columnName;
			String otherKey = other.getTableName() + other.getColumnName();
			return key.compareTo(otherKey);
		}
		
		public String toString() {
	        StringBuilder sb = new StringBuilder();
	        sb.append("\n\"").append(tableName).append(",");
	        sb.append(columnName).append(",");
	        sb.append(dataType).append(",");
	        sb.append(nullable).append("\"");
	        return sb.toString();
	    }
		
	}
	
}
