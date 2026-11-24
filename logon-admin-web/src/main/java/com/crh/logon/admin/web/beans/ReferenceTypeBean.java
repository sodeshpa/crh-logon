package com.crh.logon.admin.web.beans;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.TablebrowserService;
import com.crh.logon.constants.Constant;


/**
 * TypeBean that handles DB references (foreign keys)
 * @author cosmin
 *
 */
public class ReferenceTypeBean extends TypeBean {

	private static final long serialVersionUID = 1L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ReferenceTypeBean.class);

	private static final String KEY_COLUMN = "KEY_COLUMN";
	private static final String VALUE_COLUMN = "VALUE_COLUMN";

	/**
	 * Referenced table
	 */
	private TableBean reference;
	private TableBean refTxTable;
	
	transient private TablebrowserService tbs;

	public ReferenceTypeBean(TableBean reference) {
		// [[
		Object[][] params = {{"reference", reference}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "ReferenceTypeBean", params);
		}
		// ]]
		try {
			
			this.reference = reference;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	private TableBean getRefTxTable() {
		
		if (this.refTxTable == null) {
			this.refTxTable = reference.getTablesBean().get(this.reference.getName().substring(0, this.reference.getName().length()-2).concat("TX"));
		}
		
		return this.refTxTable;
	}
	
	@Override
	public String webFormat(Object value) {
		if (value == null){
			return "";
		}
		
		TableBean refTxTable = getRefTxTable();
		if (refTxTable == null) {
			//falling back to _bs
			if ((reference.getTableMeta() != null) && (reference.getTableMeta().getReferencedField() != null) &&
					(!reference.getTableMeta().getReferencedField().equals(((FieldBean)reference.getKeys().get(0)).getName())) ){
				String query = "select " + reference.getTableMeta().getReferencedField() 
				+ " from " + Constant.SCHEMA_USER_PREPEND + reference.getName() + " where " + 
				((FieldBean)reference.getKeys().get(0)).getName() + "= ?1";
				Object[] values =  { value.toString()};
				List res = tbs.fetchGeneric(0, 1, query, values); 
				if (res.size() != 1) {
					return value.toString();
				}
				String strValue = "";
				if (!res.get(0).getClass().isArray()) {
					strValue = res.get(0).toString();
				}
				else if (((Object[])res.get(0)).length > 0) {
					strValue = ((Object[])res.get(0))[0].toString();
				}
				
				return strValue;
			}
			else {
				return value.toString();
			}
		}
		else {
			//getting info from tx table
			if ((reference.getTableMeta() != null) && (reference.getTableMeta().getReferencedField() != null)){
				
				Object[] values =  { value.toString()};
				if (refTxTable.containsField(reference.getTableMeta().getReferencedField())) {
					// Please see 52204 for why this looks so weird.
					// TODO boon, 20100412: Refactor this stuff to handle BS/TX tables properly, possibly by extending the metadata.
					String query = "select " + reference.getTableMeta().getReferencedField() 
					+ (refTxTable.containsField("txt")?",txt":"") + " from " + Constant.SCHEMA_USER_PREPEND + refTxTable.getName() + " where " + 
					((FieldBean)reference.getKeys().get(0)).getName() + "= ?1";
					try{
						List res = tbs.fetchGeneric(0, 1, query, values); 
						String strValue = value.toString();
						if (res.size() == 1) {
							if (!res.get(0).getClass().isArray()) {
								strValue = res.get(0).toString();
							}
							else if (((Object[])res.get(0)).length > 0) {
								strValue = ((Object[])res.get(0))[0].toString();
							}
							if (refTxTable.containsField("txt")) {
								strValue += " (" + ((Object[])res.get(0))[1].toString() + ")";
							}
						}
						
						return strValue;
					}
					catch (RuntimeException re) {
						LOG.debug("error during _TX query by obtaining webValue, falling back to _BS query");
					}
				}
				//if something goes wrong during _TX query, fall back to _BS query
				String query = "select " + reference.getTableMeta().getReferencedField() 
					+ " from " + Constant.SCHEMA_USER_PREPEND + reference.getName() + " where " + 
					((FieldBean)reference.getKeys().get(0)).getName() + "= ?1";
				List res = tbs.fetchGeneric(0, 1, query, values); 
				String strValue = value.toString();
				if (res.size() == 1) {
					if (!res.get(0).getClass().isArray()) {
						strValue = res.get(0).toString();
					}
					else if (((Object[])res.get(0)).length > 0) {
						strValue = ((Object[])res.get(0))[0].toString();
					}
				}
				
				return strValue;
			}
			else {

				return value.toString();
			}
		}
		
	}

	@Override
	public String getJspRenderer() {
		return "ReferenceRenderer.jsp";
	}

	@Override
	public String getJspEditor() {
		return "ReferenceEditor.jsp";
	}
	
	@Override
	public Object getTypedValue(Object value) {
		HashMap res = new HashMap();
		res.put("selectedTable", reference.getName());
		res.put("whereClause", ((FieldBean)reference.getKeys().get(0)).getName() + "='" + value + "'");
		res.put("referenceLink", "referenceLink");
		return res;
	}

	public TableBean getReference() {
		return reference;
	}

	@Override
	public void extendTypeInfo(TablebrowserService tbs) {
		this.tbs = tbs;
		reference.extendTableInfo(tbs);
	}

	/**
	 * Returns a list of Object[] as returned from a native query containing in each row
	 * a possible value for the reference.
	 * The row contains at index 0 the primary key of the referenced table and at index 1
	 * the value of the referenced field
	 *
	 * @return
	 */
	public List getOptionValues(Locale locale) {
		// [[
		List result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getOptionValues");
		}
		// ]]
		try {
			
			String query = null;
			List resList;
			
			TableBean refTxTable = getRefTxTable();
			if (refTxTable != null) {
				try {
					String keyColumn = ((FieldBean) reference.getKeys().get(0)).getName();
					String txtField = refTxTable.containsField("txt") ? " || ' - ' || txt" : "";
					String txTableName = refTxTable.getName();
					String bsTableName = reference.getName();
					String schema = Constant.SCHEMA_USER_PREPEND;
					String langCdColumn = txTableName.equalsIgnoreCase("tlgnr_lang_tx") ? "lang1_cd" : "lang_cd";
					String lang = locale.getLanguage().toLowerCase();
					StringBuilder sb = new StringBuilder();
					/* Example how the below query should look like
					   The result is used to fill up the drop down list when inserting any TX entity with Table Browser
					   
						SELECT 
							bs.<key>, bs.<key> || - || tx.txt
						FROM 
							ulgn01.<bsTable> bs
						LEFT OUTER JOIN 
							ulgn01.<txTable> tx ON bs.<key> = tx.<key> AND (lang_cd = '<locale>' OR lang_cd IS NULL)
						ORDER BY 
							bs.<key>
					*/
					append(sb, "select bs.", keyColumn, ", bs.", keyColumn, txtField);
					append(sb, " from ", schema, bsTableName, " bs ");
					append(sb, " left outer join ", schema, txTableName, " tx on ");
					append(sb, " bs.", keyColumn, " = tx.", keyColumn, " and ");
					append(sb, "(", langCdColumn, "='", lang, "'");
					append(sb, " or ", langCdColumn, " is null )");
					append(sb, " order by bs.", keyColumn);

					query = sb.toString();

					resList = tbs.fetchAllRowsGeneric(query, null);
				}
				//if something goes wrong during _TX query, fall back to _BS query
				catch (RuntimeException re) {
					LOG.debug("error during _TX query by obtaining optionValues, falling back to _BS query");
					query = createOptionFieldReferenceQueryForByEntity();
					resList = tbs.fetchAllRowsGeneric(query, null);
				}
			}
			else {
				query = createOptionFieldReferenceQueryForByEntity();
				resList = tbs.fetchAllRowsGeneric(query, null);
			}
			return result = resList;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private StringBuilder append(StringBuilder sb,String... params) {
		for (String str : params) {
			sb.append(str);
		}
		return sb;
	}

	/**
	 * Creates a {@code String} query formatted to select the id of a BS_ENTITY twice. 
	 * This is for those where TX entity sibling not available we cannot display a description text but the numeric value will be displayed in the dropdown directly.
	 * @return formatted query
	 */
	private String createOptionFieldReferenceQueryForByEntity() {
		String keyColumn = ((FieldBean)reference.getKeys().get(0)).getName();
		String valueColumn = reference.getTableMeta().getReferencedField();
		String query = String.format("select %s as %s, %s as %s from %s%s", keyColumn, KEY_COLUMN, valueColumn, VALUE_COLUMN, Constant.SCHEMA_USER_PREPEND, reference.getName());
		return query;
	}

}