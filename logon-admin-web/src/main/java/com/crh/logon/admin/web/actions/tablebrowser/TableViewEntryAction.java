/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.tablebrowser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.beans.TablesBean;
import com.crh.logon.admin.web.forms.tablebrowser.TableViewForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.refdata.MimeTypeBsEntity;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_TableViewForm" path="/tablebrowser/TableViewEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="TableBrowserShowTile.def"
 * @struts.action-forward name="failure" path="TableBrowserShowTile.def"
 */
public class TableViewEntryAction extends AdminActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(TableViewEntryAction.class);

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
			setSelectedTab("admin", request);
			setSelectedSub1Tab("tablebrowser", request);
			
			TableViewForm helpForm;
			String forwardName = "success";
			
		    // Get the struts form.
		    helpForm = (TableViewForm) form;
		    helpForm.clear();
		    if (helpForm.getTablesBean() == null) {
		    	helpForm.setTablesBean(new TablesBean());
		    }
		    
		 	// populate mime hashMap 
	    	HashMap mimeRefMap = new HashMap(); 
	    	List mimeRefList = getServiceLocator().getRefDataGenericsService().findBsEntityList(MimeTypeBsEntity.class);
	    	for (Iterator iterator = mimeRefList.iterator(); iterator.hasNext();) {
	    		MimeTypeBsEntity mimeTypeBs = (MimeTypeBsEntity) iterator.next();
	    		mimeRefMap.put(mimeTypeBs.getMimeTypeCd()+"", mimeTypeBs.getMimeType());
	    	}
	    	helpForm.setMimeMap(mimeRefMap);
	    	helpForm.setPageNr(0);
		    
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
