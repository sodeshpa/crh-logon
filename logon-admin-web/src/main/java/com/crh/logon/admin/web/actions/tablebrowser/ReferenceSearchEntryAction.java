/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.tablebrowser;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.beans.TableBean;
import com.crh.logon.admin.web.beans.TablesBean;
import com.crh.logon.admin.web.forms.tablebrowser.ReferenceSearchForm;
import com.crh.logon.admin.web.forms.tablebrowser.TableViewForm;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_ReferenceSearchForm" path="/tablebrowser/ReferenceSearchEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="/tablebrowser/ReferenceSearch.do"
 */
public class ReferenceSearchEntryAction extends AdminActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(ReferenceSearchEntryAction.class);

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
			ReferenceSearchForm helpForm;
			String forwardName = "success";
			
		    // Get the struts form.
		    helpForm = (ReferenceSearchForm) form;
		    helpForm.setWhereClause("");
		    helpForm.setLastField(null);
		    helpForm.setSelectedField(null);
		    TablesBean tablesBean = helpForm.getTablesBean();
		    if (tablesBean == null) {
		    	// The tables bean should be available in the table view form
			    TableViewForm tableViewForm = (TableViewForm)request.getSession().getAttribute("admin_TableViewForm");
				if (tableViewForm.getTablesBean() != null) {
					tablesBean = tableViewForm.getTablesBean();
				}
				else {
					// ...if not in the tableview form, then create a new one
					tablesBean = new TablesBean();
					tableViewForm.setTablesBean(tablesBean);
				}
				helpForm.setTablesBean(tablesBean);
		    }
		    TableBean currentTable = tablesBean.get(helpForm.getRefTable());
		    if (currentTable != null){
		    	helpForm.setTable(currentTable);
		    	currentTable.extendTableInfo(getServiceLocator().getTablebrowserService());
		    	currentTable.loadTableAdditionalInfo(getServiceLocator().getTablebrowserService());
		    }		    
		    request.setAttribute("referenceSearch", Boolean.TRUE);
		    
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
}
