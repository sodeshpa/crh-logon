package com.crh.logon.admin.web.beans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;


public class TableBeanUtils {

    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(TableBeanUtils.class);

	
	/**
	 * Returns a comma-separated list of all fieds of the table.
	 * Used to compose native language queries;
	 * 
	 * @param tb The TableBean
	 * @return List of all fields
	 */
	public static String allFields(TableBean tb){
		// [[
		String result = null;
		Object[][] params = {{"tb", tb}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(TableBeanUtils.class, "allFields", params);
		}
		// ]]
		try {
			Iterator i = tb.getFields().iterator();
			String separator = "";
			StringBuffer res = new StringBuffer();
			while (i.hasNext()){
				res.append(separator);
				res.append(((FieldBean)i.next()).getName());
				separator = ", ";
			}
			return result = res.toString();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Returns a comma-separated list of all fieds of the table.
	 * Used to compose native language queries;
	 * 
	 * @param tb The TableBean
	 * @return List of all fields
	 */
	public static String[] allRWFields(TableBean tb){
		// [[
		String[] result = null;
		Object[][] params = {{"tb", tb}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(TableBeanUtils.class, "allRWFields", params);
		}
		// ]]
		try {
			Iterator i = tb.getFields().iterator();
			List fields = new ArrayList();
			while (i.hasNext()){
				FieldBean fb = (FieldBean) i.next();
				if (! (fb.getType() instanceof ReadOnlyTypeBean)) {
					fields.add(fb.getName());
				}
			}
			
			return result = (String[])fields.toArray(new String[0]);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * Wraps an object array as returned by a native query into a 
	 * list of ValueBeans 
	 * 
	 * @param objects
	 * @param table
	 * @return
	 */
	public static List wrapResultRow(Object[] objects, TableBean table) {
		// [[
		List result = null;
		Object[][] params = {{"objects", objects},
							{"table", table}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(TableBeanUtils.class, "wrapResultRow", params);
		}
		// ]]
		try {
			LinkedList newRow = new LinkedList();
			Iterator fields = table.getFields().iterator();
			int cnt = 0;
			while (fields.hasNext()){
				FieldBean fb = (FieldBean)fields.next();
				newRow.add(new ValueBean(objects[cnt], fb.getType()));
				cnt++;
			}
			return result = newRow;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Wraps an object array as returned by a native query into a 
	 * list of ValueBeans 
	 * 
	 * @param objects
	 * @param table
	 * @return
	 * @throws Exception 
	 */
	public static List wrapRowForCSVImport(Object[] objects, TableBean table) throws Exception {
		// [[
		List result = null;
		Object[][] params = {{"objects", objects},
							{"table", table}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(TableBeanUtils.class, "wrapRowForCSVImport", params);
		}
		// ]]
		try {
			LinkedList newRow = new LinkedList();
			Iterator fields = table.getFields().iterator();
			int cnt = 0;
			while (fields.hasNext()){
				FieldBean fb = (FieldBean)fields.next();
				if (fb.getType() instanceof TimestampTypeBean) {
					ValueBean timestamp = new ValueBean("", fb.getType());
					timestamp.setWebValue(objects[cnt]);
					newRow.add(timestamp);
				}
				else {
					newRow.add(new ValueBean(objects[cnt], fb.getType()));
				}
				cnt++;
			}
			return result = newRow;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Wraps values in a list of rows as returned by a native query
	 * with ValueBeans
	 *  
	 * @param rows List of Object[]
	 * @param table TableBean of the corresponding table
	 * @return List of Lists ov ValueBeans
	 */
	public static List wrapResultList(List rows, TableBean table){
		// [[
		List result = null;
		Object[][] params = {{"rows", rows},
							{"table", table}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(TableBeanUtils.class, "wrapResultList", params);
		}
		// ]]
		try {
			LinkedList res = new LinkedList();
			Iterator i = rows.iterator();
			while (i.hasNext()){
				res.add(wrapResultRow((Object[])i.next(), table));
			}
			return result = res;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * Returns the where clause that selects the given row using the pk fields
	 * from table
	 *  
	 * @param tb Table to retrieve PK from
	 * @return Strin of the form "field1 = ?1 and field2 = ?2"
	 */
	public static String getPkWhere(TableBean tb) {
		Iterator i = tb.getKeys().iterator();
		String separator = "";
		StringBuffer res = new StringBuffer();
		int cnt = 1;
		while (i.hasNext()){
			res.append(separator);
			res.append(((FieldBean)i.next()).getName());
			res.append(" = ?");
			res.append(cnt);
			separator = " and ";
			cnt++;
		}
		return res.toString();
	}
	
	/**
	 * Returns the where clause that selects the given row using the pk fields and tcn field
	 * from table
	 *  
	 * @param tb Table to retrieve PK from
	 * @return Strin of the form "field1 = ?1 and field2 = ?2"
	 */
	public static String getPkAndTcnWhere(TableBean tb) {
		Iterator i = tb.getKeys().iterator();
		String separator = "";
		StringBuffer res = new StringBuffer();
		int cnt = 1;
		while (i.hasNext()){
			res.append(separator);
			res.append(((FieldBean)i.next()).getName());
			res.append(" = ?");
			res.append(cnt);
			separator = " and ";
			cnt++;
		}
		res.append(separator);
		res.append("CTL_TCN");
		res.append(" = ?");
		res.append(cnt);
		
		return res.toString();
	}
	
	/**
	 * Returns a list of the raw values of the pk fields from 
	 * a row that has been wrapped by wrapResultList
	 * @param row Result row, as wrapped by wrapResultList
	 * @param tb Table from which teh results were selected
	 * @return Array of raw Object as used by the native query interface
	 */
	public static Object[] getPkValues(List row, TableBean tb) {
		Object[] res = new Object[tb.getKeys().size()];
		Iterator fields = tb.getFields().iterator();
		Iterator values = row.iterator();
		int cnt = 0;
		while (fields.hasNext()){
			FieldBean fb = (FieldBean)fields.next();
			ValueBean vb = (ValueBean)values.next();
			if (fb.isKey()){
				res[cnt] = vb.getValue();
				cnt++;
			}
		}
		return res;
	}
	
	   /**
     * Returns a comma-separated list of all fieds of the table.
     * Used to compose native language queries;
     * 
     * @param tb The TableBean
     * @return List of all fields
     */
    public static String getPkValuesAsString(List row, TableBean tb){
		// [[
		String result = null;
		Object[][] params = {{"row", row},
							{"tb", tb}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(TableBeanUtils.class, "getPkValuesAsString", params);
		}
		// ]]
		try {
            Object[] pks = getPkValues(row, tb);
            String separator = "";
            StringBuffer res = new StringBuffer();
            for (Object object : pks) {
                res.append(separator);
                res.append(object.toString());
                separator = ", ";
                
            }
            return result = res.toString();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }

	/**
	 * Returns a list of the raw values of the pk fields and the tcn field from 
	 * a row that has been wrapped by wrapResultList
	 * @param row Result row, as wrapped by wrapResultList
	 * @param tb Table from which teh results were selected
	 * @return Array of raw Object as used by the native query interface
	 */
	public static Object[] getPkValuesAdndTcnValues(List row, TableBean tb) {
		Object[] res = new Object[tb.getKeys().size()+1];
		Iterator fields = tb.getFields().iterator();
		Iterator values = row.iterator();
		Object tcn = null;
		int cnt = 0;
		while (fields.hasNext()){
			FieldBean fb = (FieldBean)fields.next();
			ValueBean vb = (ValueBean)values.next();
			if (StringUtils.equals("CTL_TCN", fb.getName())) {
				tcn = vb.getValue();
			}
			if (fb.isKey()){
				res[cnt] = vb.getValue();
				cnt++;
			}
		}
		res[res.length-1] = tcn;
		return res;
	}

	public static String getSetString(TableBean tb) {
		Iterator i = tb.getFields().iterator();
		String separator = "";
		StringBuffer res = new StringBuffer();
		int cnt = tb.getKeys().size() + 1;
		while (i.hasNext()){
			FieldBean fb = (FieldBean)i.next();
			if (!fb.isKey()){
				res.append(separator);
				res.append(fb.getName());
				res.append(" = ?");
				res.append(cnt);
				separator = ", ";
				cnt++;
			}
		}
		return res.toString();
	}
	
	public static String getSetRWString(TableBean tb) {
		Iterator i = tb.getFields().iterator();
		String separator = "";
		StringBuffer res = new StringBuffer();
		int cnt = tb.getKeys().size() + 2;
		while (i.hasNext()){
			FieldBean fb = (FieldBean)i.next();
			if (!fb.isKey() && !(fb.getType() instanceof ReadOnlyTypeBean)){
				res.append(separator);
				res.append(fb.getName());
				res.append(" = ?");
				res.append(cnt);
				separator = ", ";
				cnt++;
			}
			
		}
		return res.toString();
	}
	
	public static String getSetRWStringWithoutTcn(TableBean tb) {
		Iterator i = tb.getFields().iterator();
		String separator = "";
		StringBuffer res = new StringBuffer();
		int cnt = tb.getKeys().size() + 1;
		while (i.hasNext()){
			FieldBean fb = (FieldBean)i.next();
			if (!fb.isKey() && !(fb.getType() instanceof ReadOnlyTypeBean) && !"CTL_TCN".equals(fb.getName())){
				res.append(separator);
				res.append(fb.getName());
				res.append(" = ?");
				res.append(cnt);
				separator = ", ";
				cnt++;
			}
			
		}
		return res.toString();
	}

	public static Object[] getSetVals(List row, TableBean tb) {
		Object[] res = new Object[tb.getFields().size() - tb.getKeys().size()];
		Iterator fields = tb.getFields().iterator();
		Iterator values = row.iterator();
		int cnt = 0;
		while (fields.hasNext()){
			FieldBean fb = (FieldBean)fields.next();
			ValueBean vb = (ValueBean)values.next();
			if (!fb.isKey()){
				res[cnt] = vb.getValue();
				cnt++;
			}
		}
		return res;
	}
	
	public static Object[] getSetRWVals(List row, TableBean tb) {
//		Object[] res = new Object[tb.getFields().size() - tb.getKeys().size()];
		List resList = new ArrayList();
		Iterator fields = tb.getFields().iterator();
		Iterator values = row.iterator();
//		int cnt = 0;
		while (fields.hasNext()){
			FieldBean fb = (FieldBean)fields.next();
			ValueBean vb = (ValueBean)values.next();
			if (!fb.isKey() && !(fb.getType() instanceof ReadOnlyTypeBean)){
				resList.add(vb.getValue());
//				res[cnt] = vb.getValue();
//				cnt++;
			}
		}
		return resList.toArray();
	}
	
	public static Object[] getSetRWValsWithoutTcn(List row, TableBean tb) {
//		Object[] res = new Object[tb.getFields().size() - tb.getKeys().size()];
		List resList = new ArrayList();
		Iterator fields = tb.getFields().iterator();
		Iterator values = row.iterator();
//		int cnt = 0;
		while (fields.hasNext()){
			FieldBean fb = (FieldBean)fields.next();
			ValueBean vb = (ValueBean)values.next();
			if (!fb.isKey() && !(fb.getType() instanceof ReadOnlyTypeBean) && ! "CTL_TCN".equals(fb.getName())){
				resList.add(vb.getValue());
//				res[cnt] = vb.getValue();
//				cnt++;
			}
		}
		return resList.toArray();
	}
	
	public static BigDecimal getTcnValue(List row, TableBean tb) {
		// [[
		BigDecimal result = null;
		Object[][] params = {{"row", row},
							{"tb", tb}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(TableBeanUtils.class, "getTcnValue", params);
		}
		// ]]
		try {
			// TODO (istvan May 11, 2009): why does valuebean contain bigdecimal for tcn ?
			BigDecimal tcn = null;
			Iterator fields = tb.getFields().iterator();
			Iterator values = row.iterator();
			while (fields.hasNext()){
				FieldBean fb = (FieldBean)fields.next();
				ValueBean vb = (ValueBean)values.next();
				if (StringUtils.equals("CTL_TCN", fb.getName())){
					if (vb.getValue() instanceof String) {
						//for csv import
						tcn = new BigDecimal((String) vb.getValue());
					}
					else {
						tcn = (BigDecimal) vb.getValue();
					}
				}
			}
			return result = tcn;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public static Object[] getAllValues(List row) {
		Object[] res = new Object[row.size()];
		Iterator values = row.iterator();
		int cnt = 0;
		while (values.hasNext()){
			ValueBean vb = (ValueBean)values.next();
			res[cnt] = vb.getValue();
			cnt++;
		}
		return res;
	}
	
	public static Object[] getAllRWValues(List row) {
		Iterator values = row.iterator();
		List resList = new ArrayList();
		while (values.hasNext()){
			ValueBean vb = (ValueBean)values.next();
			if(!(vb.getType() instanceof ReadOnlyTypeBean)) {
				if(!vb.isEditable()){
					resList.add("");
				}
				else {
					resList.add(vb.getValue());	
				}
			}
		}
		return resList.toArray();
	}

	
	/**
	 * Method is the same as getSetVals but check if we need to add blobs
	 * @param row current fields
	 * @param tb TableBean
	 * @param deleteBlob true if we want to delete blob
	 * @return values to be updated
	 */
	public static Object[] getSetBlobVals(List row, TableBean tb, boolean deleteBlob) {
		// [[
		Object[] result = null;
		Object[][] params = {{"row", row},
							{"tb", tb},
							{"deleteBlob", deleteBlob + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(TableBeanUtils.class, "getSetBlobVals", params);
		}
		// ]]
		try {
			List valuesList = new ArrayList();
			Iterator fields = tb.getFields().iterator();
			Iterator values = row.iterator();
			while (fields.hasNext()) {
				FieldBean fb = (FieldBean) fields.next();
				ValueBean vb = (ValueBean) values.next();
				if (!fb.isKey()  && !(fb.getType() instanceof ReadOnlyTypeBean)) {
					if ((fb.getType() instanceof BlobTypeBean) && (deleteBlob)) {
						valuesList.add(null);
					}
					else if ((fb.getType() instanceof BlobTypeBean)
							&& !(vb.getValue() instanceof byte[])) {
						if (LOG.isDebugEnabled()) {
							LOG.debug("DO NOT UPDATE BLOB FIELD!");
						}
					}
					else {
						valuesList.add(vb.getValue());
					}
				}
			}
			
			return result = valuesList.toArray();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	
	
	/**
	 *  Method is the same as getSetString but check if we need to add blobs
	 * @param row current fields
	 * @param tb TableBean
	 * @param deleteBlob true if we want to delete blob
	 * @return fields to be updated
	 */
	public static String getSetBlobString(List row, TableBean tb, boolean deleteBlob) {
		// [[
		String result = null;
		Object[][] params = {{"row", row},
							{"tb", tb},
							{"deleteBlob", deleteBlob + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(TableBeanUtils.class, "getSetBlobString", params);
		}
		// ]]
		try {
			Iterator i = tb.getFields().iterator();
			String separator = "";
			StringBuffer res = new StringBuffer();
			int cnt = tb.getKeys().size() + 2;
			Iterator values = row.iterator();
			while (i.hasNext()) {
				FieldBean fb = (FieldBean) i.next();
				ValueBean vb = (ValueBean) values.next();
				if (!fb.isKey() && !(fb.getType() instanceof ReadOnlyTypeBean)) {
					if ((fb.getType() instanceof BlobTypeBean) && (deleteBlob)) {
						res.append(separator);
						res.append(fb.getName());
						res.append(" = ?");
						res.append(cnt);
						separator = ", ";
						cnt++;
					}
					else if ((fb.getType() instanceof BlobTypeBean)
							&& !(vb.getValue() instanceof byte[])) {
						if (LOG.isDebugEnabled()) {
							LOG.debug("DO NOT UPDATE BLOB FIELD!");
						}
					}
					else {
						res.append(separator);
						res.append(fb.getName());
						res.append(" = ?");
						res.append(cnt);
						separator = ", ";
						cnt++;
					}
				}
			}
			return result = res.toString();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public static boolean tableHasTcnField (TableBean table) {
		// [[
		boolean result = false;
		Object[][] params = {{"table", table}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(TableBeanUtils.class, "tableHasTcnField", params);
		}
		// ]]
		try {
			Iterator i = table.getFields().iterator();
			
			boolean tcnFieldFound = false;
			
			while (i.hasNext()) {
				FieldBean fb = (FieldBean) i.next();
				if ("CTL_TCN".equals(fb.getName())) {
					tcnFieldFound = true;
					break;
				}
			}
			return result = tcnFieldFound;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
}
