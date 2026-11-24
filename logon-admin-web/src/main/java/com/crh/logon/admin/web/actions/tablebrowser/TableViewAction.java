/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.tablebrowser;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJBException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.beans.FieldBean;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.beans.ReferenceTypeBean;
import com.crh.logon.admin.web.beans.TableBean;
import com.crh.logon.admin.web.beans.TableBeanUtils;
import com.crh.logon.admin.web.beans.ValueBean;
import com.crh.logon.admin.web.forms.tablebrowser.TableViewForm;
import com.crh.logon.business.services.localinterfaces.SqlServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;


/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_TableViewForm" path="/tablebrowser/TableView" scope="session"
 *  validate="true" input="TableBrowserShowTile.def"
 *
 * @struts.action-forward name="input" path="TableBrowserShowTile.def"
 * @struts.action-forward name="table" path="TableBrowserShowTableTile.def"
 * @struts.action-forward name="edit" path="/tablebrowser/RowEditEntry.do"
 */
public class TableViewAction extends AdminActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(TableViewAction.class);

    private static final String FORWARD_INPUT = "input";
    private static final String FORWARD_TABLE = "table";
    private static final String FORWARD_EDIT = "edit";
    
	public ActionForward execute(
		ActionMapping mapping, ActionForm form,
		HttpServletRequest request,	HttpServletResponse response)
		throws IOException, ServletException
	{
		// [[
		ActionForward result = null;
		Object[][] params = {{"mapping", mapping},
							{"form", form},
							{"request", request},
							{"response", response}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "execute", params);
		}
		// ]]
		try {
			ensureInt(); // tablebrowser can only be used in int-deployment!
			Authorization.authorize(Authorization.TABLE_BROWSER);
			TableViewForm helpForm;
			String forwardName = FORWARD_INPUT;
			
			// Get the struts form.
			helpForm = (TableViewForm) form;
			try {
			    LOG.debug("SELECTED TABLE:" + helpForm.getSelectedTable());
				
			    // Check if reset button clicked.
			    if (isCancelled(request)) {
			        // Clear all fields and discard search results.
			        helpForm.clear();
			    }
			    // Check if submit button clicked.
			    else if (request.getParameter("next") != null) {
			    	LOG.debug("next");
			    	if (!helpForm.isLastPage()){
			    		helpForm.setPageNr(helpForm.getPageNr()+ 1);
			    	}
			    }
			    else if (request.getParameter("prev") != null) {
			    	LOG.debug("prev");
			    	if (helpForm.getPageNr() > 0){
			    		helpForm.setPageNr(helpForm.getPageNr()- 1);
			    	}
			    }
			    else if (request.getParameter("new") != null) {
			    	;//PMD
			/*					helpForm.clear(); */
			    }
			    else if (request.getParameter("editRow") != null) {
			    	if (!helpForm.isUpdateAllowed()) {
			    		
			    		throw new LogonException(Reason.RES_ERROR_SECURITY_TAMPERING);
			    	}
			    	List row = helpForm.getSelectedRow();
			    	if (row != null){
			    		forwardName = FORWARD_EDIT;
			    		return result = mapping.findForward(forwardName);
			    	}
			    }
			    else if (request.getParameter("addRow") != null) {
			    	helpForm.setRowNr(-1);
			    	if (!helpForm.isInsertAllowed()) {
			    		
			    		throw new LogonException(Reason.RES_ERROR_SECURITY_TAMPERING);
			    	}
		    		forwardName = FORWARD_EDIT;
			    }
			    else if (request.getParameter("clearFilter") != null) {
			    	helpForm.setWhereClause("");
			    }
			    else if (request.getParameter("deleteRow") != null) {
			    	if (!helpForm.isDeleteAllowed()) {
			    		
			    		throw new LogonException(Reason.RES_ERROR_SECURITY_TAMPERING);
			    	}
			    	List row = helpForm.getSelectedRow();
			    	if (row != null){
			    	    TableBean table = helpForm.getTable();
			    		String where = TableBeanUtils.getPkWhere(table);
			    		Object[] pkVals = TableBeanUtils.getPkValues(row, table);
			    		getServiceLocator().getTablebrowserService().deleteGeneric(
			    				pkVals[0], table.getName(), getUser() , where, pkVals);
			    		
			    		MessageUtils.addActionMessage(request, "tablebrowser.success.delete", TableBeanUtils.getPkValuesAsString(row, table));
			    	}
			    	forwardName = FORWARD_TABLE; 
			    }
			    else if (request.getParameter("export") != null) {
				    if (helpForm.getTable() != null){					
				    	setDLHeaders(request, response, null, helpForm.getTable().getName() + ".csv");
				    	PrintWriter writer = new PrintWriter(response.getWriter());
				    	SqlServiceLocalInterface sqlService = null;
				    	try {
					    	// header line
					    	List fields = helpForm.getTable().getFields();
					    	for (int i=0;i<fields.size();i++) {
					    		FieldBean fieldBean = (FieldBean)fields.get(i);
					    		writer.print("\"" + fieldBean.getName() + "\"");
					    		if (i < (fields.size()-1)) {
					    			writer.print(";");
					    		}
					    	}
					    	writer.println();
					    	// data
					    	String query = buildQuery(helpForm,request);
					    	sqlService = getServiceLocator().getLocalSqlService(); // always get a new one
					    	Long qSessionId = sqlService.openQuerySession(query, null);
					    	List rows = sqlService.fetchRows(qSessionId, 100);
					    	while (rows != null) {
					    		for (Iterator it=rows.iterator();it.hasNext();) {
					    			Object[] row = (Object[])it.next();
					    			List values = TableBeanUtils.wrapResultRow(row, helpForm.getTable());
					    			for (int i=0;i<values.size();i++) {
					    				ValueBean valueBean = (ValueBean)values.get(i);
					    				String value = "";
				    					if (valueBean.getType() instanceof ReferenceTypeBean) {
				    						value = valueBean.getValue() == null ? "" : valueBean.getValue().toString(); 
				    					}
				    					else {
				    						value = valueBean.getWebValue();
				    					}
					    				writer.print("\"" + (value!=null?value:"") + "\"");
							    		if (i < (fields.size()-1)) {
							    			writer.print(";");
							    		}
					    			}
					    			writer.println();
					    		}
					    		rows = sqlService.fetchRows(qSessionId, 100);
					    	}
				    	} 
				    	catch (Throwable t) {
				    		ExceptionUtil.errException(LOG, t, params);
				    		String errorDesc = MessageUtils.getMessage(request, request.getLocale(), "fatalerror.unknownerror");
				    		String errorId = null;
				    		if (t instanceof LogonException) {
								errorId = ((LogonException)t).getId();
							}
				    		if (errorId == null) {
				    			errorId = StringUtils.generateErrorId();
								LOG.error("The following errorId is generated for a non-LogonException: " + errorId);
				    		}
				    		String errMsg = MessageUtils.getMessage(request, request.getLocale(), "fatalerror.errormessage", new Object[] { errorDesc, errorId });
				    		writer.println(errMsg);
				    	}
				    	finally {
				    		if (sqlService != null) {
				    			sqlService.remove();
				    		}
				    		if (writer != null) {
				    			writer.flush();
				    		}
				    	}
				    	
				    	return result = null;
				    }
			    }
			    
			    else if(helpForm.getSelectedTable() != null){
			    	String selectedTable = helpForm.getSelectedTable();
		    		if (helpForm.getTable() != null) {
		    			if(! StringUtils.empty(selectedTable)){
		    				if(!helpForm.getTable().getName().equals(selectedTable)){
		    					//in case if we click on a link of a field reference, (not table), than keep filter
		    					if (request.getParameter("referenceLink") == null) {
		    						helpForm.setWhereClause("");
		    					}
					    		helpForm.setSelectedField(null);
					    		helpForm.setLastField(null);
		    				}
		    			}
		    		}
		    		TableBean table = helpForm.getTablesBean().get(selectedTable); 
		    		if(table != null){
		    			table.extendTableInfo(getServiceLocator().getTablebrowserService());
		    			table.loadTableAdditionalInfo(getTablebrowserService());
			    		helpForm.setTable(table);
		    		}
		    	}
			}
			catch (EJBException e){
				ExceptionUtil.errException(LOG, e, params);
				addActionException(request,"error.tablebrowser.sqlexec","",e);
			}
			String query = "";
			try{
			    if (helpForm.getTable() != null){					
			    	query = buildQuery(helpForm, request);
			    	List rows = getServiceLocator().getLocalTablebrowserService().fetchGenericUserQuery(TableViewForm.PAGESIZE * helpForm.getPageNr(), TableViewForm.PAGESIZE + 1, query, null);
			    	helpForm.setRows(rows);
			    }
			}
			catch (EJBException e){
				LOG.info("Error in tablebrowser user query '" + query + "' " + e.getMessage());
				MessageUtils.addActionError(request, "error.tablebrowser.filter");
			}
			return result = mapping.findForward(forwardName);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	
	protected static String buildQuery(TableViewForm helpForm, HttpServletRequest request) {
		String query = "select "+  TableBeanUtils.allFields(helpForm.getTable()) + " from " + Constant.SCHEMA_USER_PREPEND + 
			helpForm.getTable().getName();
		
		if (helpForm.getWhereClause().trim().length() > 0) {
			query += " where " + helpForm.getWhereClause().trim();
			if(! StringUtils.empty(helpForm.getSelectedTable())) {
				helpForm.setPageNr(0);
			}
		}
		
		if((helpForm.getSelectedField()!=null)) {
			if(helpForm.getLastField()!=null ) {
				if((helpForm.getLastField().equals(helpForm.getSelectedField())))
				{
					if((request.getParameter("next")==null) &&(request.getParameter("prev")==null)) {
						if((helpForm.getLastOrder().equals("asc")))
						{
								helpForm.setLastOrder("desc");
							
						}
						else if (helpForm.getLastOrder().equals("desc"))
						{
								helpForm.setLastOrder("asc");
							
						}
					}
					query+=" order by "+ helpForm.getSelectedField() + " "+ helpForm.getLastOrder();
				}
				else {
					query+=" order by "+ helpForm.getSelectedField() + " asc";
				}
			}
			else {
				query+=" order by "+ helpForm.getSelectedField() + " asc";
				
				
			}
			helpForm.setLastField(helpForm.getSelectedField());
		}
		
		return query;
	}
}
