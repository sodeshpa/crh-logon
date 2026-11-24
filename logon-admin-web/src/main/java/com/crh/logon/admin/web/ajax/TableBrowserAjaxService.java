/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.ajax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpSession;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.tablebrowser.TableViewForm;
import com.crh.logon.admin.web.beans.TableNameBean;
import com.crh.logon.admin.web.beans.TablesBean;
import com.crh.logon.web.ajax.DwrServiceSupport;

/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to 
 * AJAX components. 
 * 
 * This is used by the table browser.
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="TableBrowserAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.admin.web.beans.TableNameBean"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 */
public class TableBrowserAjaxService extends DwrServiceSupport {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(TableBrowserAjaxService.class);

    public List<TableNameBean> tableList(String contains, HttpSession session) {
		// [[
		List<TableNameBean> result = null;
		Object[][] params = {{"contains", contains},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "tableList", params);
		}
		// ]]
		try {
			
			TableViewForm tableViewForm = (TableViewForm)session.getAttribute("admin_TableViewForm");
			TablesBean tablesBean = null;
			if (tableViewForm.getTablesBean() != null) {
				tablesBean = tableViewForm.getTablesBean();
			}
			else {
				tablesBean = new TablesBean();
			}
			final List<TableNameBean> res = new ArrayList<TableNameBean>();
			final Collection<String> col = tablesBean.getTableNames();
			for (String tableName : col) {
				if (contains == null) {
					TableNameBean tnb = new TableNameBean(tableName);
					res.add(tnb);
				}
				else if (tableName.toLowerCase().contains(contains.toLowerCase())) {
					TableNameBean tnb = new TableNameBean(tableName);
					res.add(tnb);
				}
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
}
