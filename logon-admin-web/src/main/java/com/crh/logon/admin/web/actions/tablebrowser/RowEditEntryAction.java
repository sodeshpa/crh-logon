/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.tablebrowser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.beans.FieldBean;
import com.crh.logon.admin.web.forms.tablebrowser.TableViewForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.beans.ReferenceTypeBean;
import com.crh.logon.admin.web.beans.TableBean;
import com.crh.logon.admin.web.beans.TableBeanUtils;
import com.crh.logon.admin.web.forms.tablebrowser.RowEditForm;
import com.crh.logon.persistence.entities.refdata.MimeTypeBsEntity;
import com.crh.logon.web.utils.Authorization;


/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_RowEditForm" path="/tablebrowser/RowEditEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="/tablebrowser/RowEdit.do"
 * @struts.action-forward name="failure" path="TableBrowserEditTile.def"
 */
public class RowEditEntryAction extends AdminActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(RowEditEntryAction.class);

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
			RowEditForm helpForm;
			TableViewForm viewForm = (TableViewForm)request.getSession().getAttribute("admin_TableViewForm");
			String forwardName = "success";
			
		    // Get the struts form.
		    helpForm = (RowEditForm) form;
		    
		    helpForm.clear();
		    TableBean table = viewForm.getTable();
		    if (table != null){
		    	//loading additional column informations like type, nullable....
		    	table.loadColumnAdditionalInfo(getTablebrowserService());
		    	helpForm.setEditMode(true);
		    	List row = viewForm.getSelectedRow();
		    	if (row == null){
		    		helpForm.setEditMode(false);
		    		row = TableBeanUtils.wrapResultRow(new Object[table.getFields().size()], table);
		    	}
		    	helpForm.setTableMeta(viewForm.getTableMeta());				
		    	helpForm.setRow(row);
		    	helpForm.setTable(table);
		    	List optionValues = new ArrayList();
		    	for (Iterator it=table.getFields().iterator();it.hasNext();) {
		    		FieldBean fb = (FieldBean)it.next();
		    		List fbOptionValues = null;
		    		if (fb.getType() instanceof ReferenceTypeBean) {
		    			ReferenceTypeBean rtb = (ReferenceTypeBean)fb.getType();
		    			if (rtb.getReference().getTableMeta().getUseDropdown()) {
		    				fbOptionValues = rtb.getOptionValues(request.getLocale());
		    			}
		    		}
		    		optionValues.add(fbOptionValues);
		    	}
		    	helpForm.setOptionValues(optionValues);
		    	
		    	// populate mime hashMap 
		    	HashMap mimeRefMap = new HashMap(); 
		    	List mimeRefList = getServiceLocator().getRefDataGenericsService().findBsEntityList(MimeTypeBsEntity.class);
		    	for (Iterator iterator = mimeRefList.iterator(); iterator.hasNext();) {
		    		MimeTypeBsEntity mimeTypeBs = (MimeTypeBsEntity) iterator.next();
		    		mimeRefMap.put(mimeTypeBs.getMimeTypeCd()+"", mimeTypeBs.getMimeType());
		    	}
		    	helpForm.setMimeMap(mimeRefMap);
		    } 
		    helpForm.setUpdateAllowed(viewForm.isUpdateAllowed());
		    helpForm.setInsertAllowed(viewForm.isInsertAllowed());
		    helpForm.setDeleteAllowed(viewForm.isDeleteAllowed());
		    
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
