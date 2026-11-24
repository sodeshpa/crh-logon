/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.tablebrowser;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.beans.TableBean;
import com.crh.logon.admin.web.beans.TableBeanUtils;
import com.crh.logon.admin.web.beans.TablesBean;
import com.crh.logon.persistence.entities.tablebrowser.TableMeta;


/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_TableViewForm"
 */
public class TableViewForm extends ActionForm {

	private static final long serialVersionUID = 1L;
	
	public static final int PAGESIZE = 20;

	private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(TableViewForm.class);

	// input from the user
	private int pageNr = 0;
	private boolean lastPage = true;
	private String whereClause = "";
	private int rowNr;
	
    // db state
    private List rows = new LinkedList();
    
    private TablesBean tables;
    private TableBean table = null;
	private HashMap mimeMap;
    private String selectedTable;
    private String selectedField;
    private String lastField;
    private String lastOrder = "asc";
    private int noOfFields;
	
	public String getLastOrder() {
		return lastOrder;
	}

	public void setLastOrder(String lastOrder) {
		this.lastOrder = lastOrder;
	}

	public String getLastField() {
		return lastField;
	}

	public void setLastField(String lastField) {
		this.lastField = lastField;
	}

	public String getSelectedField() {
		return selectedField;
	}

	public void setSelectedField(String selectedField) {
		this.selectedField = selectedField;
	}

	/**
     * Default constructor.
     */
    public TableViewForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "TableViewForm");
		}
		// ]]
		try {
	        init();
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
     * Initializes the form. This has the effect of clearing all the input
     * fields on the web form.
     */
    private void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
	    	selectedTable = null; // fill in your code or remove the method.\
	    	selectedField = null;
	    	lastField = null;
	    	whereClause = "";
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
     * Clears all the input fields on the web form, discards any existing
     * search results, and resets any existing pages. Called in response to
     * clicking the reset button on the web form.
     */
    public void clear() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "clear");
		}
		// ]]
		try {
		   rows = new LinkedList();
		   table = null;
		   pageNr = 0;
		   lastPage = true;
		   rowNr = -1;
	       init();
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
     * Override struts method.
     * Note: this is not associated with or invoked by the reset button in the
     * web form, but rather is called on every request before the struts action
     * method gets control and, more importantly, before the request parameters
     * are automatically copied into the form. The primary use is to reset
     * checkboxes and struts multiboxes and the like for which the browser only
     * sends the form parameters when they are set.
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
    }

    /**
     * Override struts method.
     * Perform form-specific validation of input parameters.
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		// [[
		ActionErrors result = null;
		Object[][] params = {{"mapping", mapping},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validate", params);
		}
		// ]]
		try {
			
	        ActionErrors errors = super.validate(mapping, request);
	        
			
	        /* no need to validate if 'delete' button was pressed */
	        if (request.getParameter("delete") != null) {
	            return result = null;
	        }
	        if (errors == null) {
	        	errors = new ActionErrors();
	        }
			/*
	        if (name.trim().equals("")) {
	            errors.add(ActionMessages.GLOBAL_MESSAGE,
	                    new ActionMessage("errors.nameNotNull"));
	        }
			*/
	        return result = errors;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }

	public List getRows() {
		return rows;
	}

	public void setRows(List rows) {
		if (rows.size() > PAGESIZE){
			rows.remove(PAGESIZE);
			lastPage = false;
		}
		else {
			lastPage = true;
		}
		
		this.rows = TableBeanUtils.wrapResultList(rows, table);
	}

	public TableBean getTable() {
		
		return table;
	}

	public void setTable(TableBean table) {
		setPageNr(0);
		this.table = table;
		if(table != null){
			List fields = table.getFields();
			if(fields != null){
				this.noOfFields = fields.size();
			}
		}
	}
    public List getKeyIds() {
        return table.getKeyIds();
    }

	public int getPageNr() {
		return pageNr;
	}

	public void setPageNr(int pageNr) {
		this.pageNr = pageNr;
	}

	public boolean isLastPage() {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("isLastPage(): '" + lastPage + "'");
		}
		// ]]
		return lastPage;
	}

	public void setLastPage(boolean lastPage) {
		this.lastPage = lastPage;
	}

	public String getWhereClause() {
		return whereClause;
	}

	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}

    public TableMeta getTableMeta() {
		return table.getTableMeta();
	}

	
	public boolean isUpdateAllowed(){
		return (table.getTableMeta() != null) && table.getTableMeta().getUpdateAllowed();
	}

	public boolean isDeleteAllowed(){
		return (table.getTableMeta() != null) && table.getTableMeta().getDeleteAllowed();
	}

	public boolean isInsertAllowed(){
		return (table.getTableMeta() != null) && table.getTableMeta().getInsertAllowed();
	}

	public List getSelectedRow(){
		List res = null;
		if (rows == null){
			return res;
		}
		if ((rowNr < 0) || (rowNr >= rows.size())){
			return res;
		}
		return res = (List)rows.get(rowNr);
	}

	public int getRowNr() {
		return rowNr;
	}

	public void setRowNr(int rowNr) {
		this.rowNr = rowNr;
	}
	
	public HashMap getMimeMap() {
		return mimeMap;
	}

	public TablesBean getTablesBean() {
		return tables;
	}
	
	public void setTablesBean(TablesBean tables) {
		this.tables = tables;
	}	
	
	public String getSelectedTable() {
		return selectedTable;
	}

	public void setSelectedTable(String selectedTable) {
		this.selectedTable = selectedTable;
	}

	public void setMimeMap(HashMap mimeMap) {
		this.mimeMap = mimeMap;
	}

	public int getNoOfFields() {
		return noOfFields;
	}
	
	public int getTableCount() {
		
		return tables.getTableNames().size();
	}
	
	public Object getTableNames() {
		
		return tables.getTableNames().toArray();
	}
	
	public int getTableListRowCount() {
		// JSP EL doesn't let you cast to integer
		int count = getTableCount();
		if (count % 5 == 0) {
			count = (count / 5) - 1;
		}
		else {
			count = (count / 5);
		}
		
		return count==0?1:count;
	}
}
