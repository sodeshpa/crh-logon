/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.tablebrowser;

import java.io.IOException;
import java.util.List;

import javax.ejb.EJBException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.forms.tablebrowser.ReferenceSearchForm;
import com.crh.logon.admin.web.forms.tablebrowser.TableViewForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.orm.hibernate.HibernateOptimisticLockingFailureException;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.beans.TableBeanUtils;
import com.crh.logon.constants.Constant;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_ReferenceSearchForm" path="/tablebrowser/ReferenceSearch" scope="session"
 *  validate="true" input="ReferenceSearchTile.def"
 *
 * @struts.action-forward name="input" path="ReferenceSearchTile.def"
 * @struts.action-forward name="fetchfirst" path="/tablebrowser/ReferenceSearchEntry.do"
 */
public class ReferenceSearchAction extends AdminActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(ReferenceSearchAction.class);

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
			String forwardName = "input";
			
			try {
			    // Get the struts form.
			    helpForm = (ReferenceSearchForm) form;
			    request.setAttribute("referenceSearch", Boolean.TRUE);
			    
			    // Check if submit button clicked.
			    if (request.getParameter("next") != null) {
			    	LOG.debug("next");
			    	if (!helpForm.isLastPage()){
			    		helpForm.setPageNr(helpForm.getPageNr()+ 1);
			    	}
				    forwardName = "input";
			    }
			    else if (request.getParameter("prev") != null) {
			    	LOG.debug("prev");
			    	if (helpForm.getPageNr() > 0){
			    		helpForm.setPageNr(helpForm.getPageNr()- 1);
			    	}
				    forwardName = "input";
			    }
			    else if (request.getParameter("clearFilter") != null) {
			    	helpForm.setWhereClause("");
		    		forwardName = "input";
			    }
			
			    if (helpForm.getTable() != null){					
			    	String query = "select "+  TableBeanUtils.allFields(helpForm.getTable()) + " from " + Constant.SCHEMA_USER_PREPEND + 
			    		helpForm.getTable().getName();
			    	if (helpForm.getWhereClause().trim().length() > 0) {
			    		query += " where " + helpForm.getWhereClause().trim();
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
			    	
			    	List rows = getServiceLocator().getTablebrowserService()
			    		.fetchGeneric(TableViewForm.PAGESIZE * helpForm.getPageNr(), TableViewForm.PAGESIZE + 1, query, null);
			    	helpForm.setRows(rows);
			    }
			    
				
			}
			catch (HibernateOptimisticLockingFailureException ex) {
			    /*
			     * this is just to show how to handle a particular exception
			     * inside an action. However, exceptions like this are usually
			     * handled in the handlers package.
			     */
				MessageUtils.addActionError(request, "errors.optimisticLocking");
			    forwardName = "input";
			}
			catch (EJBException e){
				ExceptionUtil.errException(LOG, e, params);
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
}
