/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.tablebrowser;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;

import javax.ejb.EJBException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.beans.FieldBean;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.orm.hibernate.HibernateOptimisticLockingFailureException;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.beans.BlobTypeBean;
import com.crh.logon.admin.web.beans.TableBeanUtils;
import com.crh.logon.admin.web.forms.tablebrowser.RowEditForm;
import com.crh.logon.business.services.utils.TableBrowserSequenceWrapper;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_RowEditForm" path="/tablebrowser/RowEdit" scope="session"
 *  validate="true" input="TableBrowserEditTile.def"
 *
 * @struts.action-forward name="input" path="TableBrowserEditTile.def"
 * @struts.action-forward name="view" path="/tablebrowser/TableView.do"
 */
public class RowEditAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RowEditAction.class);
	public static final String GENERATED_VALUE = "(generated)";

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
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
			String forwardName = "input";
			
			try {
				// Get the struts form.
				helpForm = (RowEditForm) form;
			
				// Check if reset button clicked.
				if (isCancelled(request)) {
					// Clear all fields and discard search results.
					helpForm.clear();
				}
				// Check if delete blob icon is clicked.
				else if (request.getParameter("deleteBlob") != null) {
					if (!helpForm.isDeleteAllowed()) {
						
						throw new LogonException(Reason.RES_ERROR_SECURITY_TAMPERING);
					}
			
					String where = TableBeanUtils.getPkAndTcnWhere(helpForm.getTable());
					Object[] pkVals = TableBeanUtils.getPkValues(helpForm.getRow(),
							helpForm.getTable());
			
					String set = TableBeanUtils.getSetBlobString(helpForm.getRow(),
							helpForm.getTable(), true);
					Object[] setVals = TableBeanUtils.getSetBlobVals(helpForm.getRow(),
							helpForm.getTable(), true);
					
					BigDecimal tcn = TableBeanUtils.getTcnValue(helpForm.getRow(), helpForm.getTable());
					
					getServiceLocator().getTablebrowserService().updateGenericWithOL(
							pkVals[0], helpForm.getTable().getName(), getUser(), set, setVals, where, pkVals, tcn);
			
					forwardName = "view";
				}
				// Check if submit button clicked.
				else if (request.getParameter("save") != null) {
			
					if (helpForm.isEditMode()) {
						if (!helpForm.isUpdateAllowed()) {
							
							throw new LogonException(Reason.RES_ERROR_SECURITY_TAMPERING);
						}
			
						// Check if one of the fields has blob type
						boolean isBlob = false;
						Iterator i = helpForm.getTable().getFields().iterator();
						while (i.hasNext()) {
							if (((FieldBean) i.next()).getType() instanceof BlobTypeBean) {
								if (LOG.isDebugEnabled()) {
									LOG.debug("Object is instance of BlobTypeBean");
								}
								isBlob = true;
							}
						}
						BigDecimal tcn = TableBeanUtils.getTcnValue(helpForm.getRow(), helpForm.getTable());
						String where = null;
						if (tcn == null) {
							where = TableBeanUtils.getPkWhere(helpForm.getTable());
							
						}
						else {
							where = TableBeanUtils.getPkAndTcnWhere(helpForm.getTable());
						}
			
						Object[] pkVals = TableBeanUtils.getPkValues(helpForm.getRow(),
								helpForm.getTable());
			
						String set;
						Object[] setVals;
			
						if (isBlob) {
							set = TableBeanUtils.getSetBlobString(helpForm.getRow(),
									helpForm.getTable(), false);
							setVals = TableBeanUtils.getSetBlobVals(helpForm.getRow(),
									helpForm.getTable(), false);
						}
						else {
							
							if (tcn == null) {
								set = TableBeanUtils.getSetRWStringWithoutTcn(helpForm.getTable());
								setVals = TableBeanUtils.getSetRWValsWithoutTcn(helpForm.getRow(),
										helpForm.getTable());
							}
							else {
								set = TableBeanUtils.getSetRWString(helpForm.getTable());
								setVals = TableBeanUtils.getSetRWVals(helpForm.getRow(),
										helpForm.getTable());
								
							}
						}
						if (tcn == null) {
							getServiceLocator().getTablebrowserService().updateGeneric(
									pkVals[0], helpForm.getTable().getName(), getUser(), set, setVals, where, pkVals);
							
						}
						else {
							getServiceLocator().getTablebrowserService().updateGenericWithOL(
									pkVals[0], helpForm.getTable().getName(), getUser(), set, setVals, where, pkVals, tcn);
						}
			
					}
					else {
						if (!helpForm.isInsertAllowed()) {
							
							throw new LogonException(Reason.RES_ERROR_SECURITY_TAMPERING);
						}
						String[] fields = TableBeanUtils.allRWFields(helpForm.getTable());
						Object[] values = TableBeanUtils.getAllRWValues(helpForm.getRow());
						Object[] pkVals = TableBeanUtils.getPkValues(helpForm.getRow(),
								helpForm.getTable());
						
						if (!helpForm.getTable().isRefTable()) {
							boolean isFirstPk = true;
							for (int i=0;i<fields.length;i++) {
								for (Iterator it=helpForm.getTable().getFields().iterator();it.hasNext();) {
									FieldBean fieldBean = (FieldBean)it.next();
									if ( (fieldBean.getName().equals(fields[i])) && (fieldBean.isKey()) && (fieldBean.getSequenceName() != null) ) {
										if ( (values[i] == null) || (StringUtils.isEmpty(values[i].toString())) || (values[i].toString().equals(GENERATED_VALUE)) ) {
											values[i] = new TableBrowserSequenceWrapper(fieldBean.getSequenceName(), isFirstPk);
											isFirstPk = false;
										}
									}
								}							
							}
						}
						boolean hasTcnField = TableBeanUtils.tableHasTcnField(helpForm.getTable());
						getServiceLocator().getTablebrowserService().insertGeneric(
								pkVals[0], helpForm.getTable().getName(), getUser(), StringUtils.join(fields, ','), values, hasTcnField);			
					}
					forwardName = "view";
				}
				else if (request.getParameter("cancel") != null) {
					forwardName = "view";
				}
			}
			catch (HibernateOptimisticLockingFailureException ex) {
				/*
				 * this is just to show how to handle a particular exception
				 * inside an action. However, exceptions like this are usually
				 * handled in the handlers package.
				 */
				MessageUtils.addActionError(request, "errors.optimisticLocking");
				forwardName = "fetchfirst";
			}
			catch (EJBException e) {
				ExceptionUtil.errException(LOG, e, params);
				addActionException(request, "error.tablebrowser.sqlexec", "", e);
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
