package com.crh.logon.admin.web.actions.tablebrowser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJBException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import au.com.bytecode.opencsv.CSVReader;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.beans.FieldBean;
import com.crh.logon.admin.web.beans.TableBean;
import com.crh.logon.admin.web.beans.TableBeanUtils;
import com.crh.logon.admin.web.forms.tablebrowser.FileImportForm;
import com.crh.logon.admin.web.forms.tablebrowser.TableViewForm;
import com.crh.logon.business.services.utils.TableBrowserSequenceWrapper;
import com.crh.logon.constants.Constant;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.springframework.orm.hibernate.HibernateOptimisticLockingFailureException;

/**
 * @author AdNovum Informatik AG
 * @version $Revision$ $Date$
 * @struts.action name="admin_FileImportForm" path="/tablebrowser/FileImport" scope="request" validate="true"
 * input="TableBrowserShowTile.def"
 * @struts.action-forward name="input" path="TableBrowserShowTile.def"
 */
public class FileImportAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(FileImportAction.class);

	private static final String FORWARD_INPUT = "input";

	@Override
	public ActionForward execute(
			ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		// [[
		ActionForward result = null;
		Object[][] params = { { "mapping", mapping },
				{ "form", form },
				{ "request", request },
				{ "response", response } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "execute", params);
		}
		// ]]
		try {
			ensureInt(); // tablebrowser can only be used in int-deployment!
			Authorization.authorize(Authorization.TABLE_BROWSER);
			FileImportForm helpForm;
			String forwardName = FORWARD_INPUT;

			// Get the struts form.
			helpForm = (FileImportForm) form;
			TableViewForm tableViewForm = (TableViewForm) request.getSession().getAttribute("admin_TableViewForm");
			try {
				LOG.debug("SELECTED TABLE:" + tableViewForm.getSelectedTable());

				// Check if reset button clicked.
				if (isCancelled(request)) {
					// Clear all fields and discard search results.
					helpForm.clear();
				}
				// Check if submit button clicked.
				if (request.getParameter("import") != null) {
					if ((!tableViewForm.isInsertAllowed()) || (!tableViewForm.isUpdateAllowed())) {

						throw new LogonException(Reason.RES_ERROR_SECURITY_NOT_ALLOWED);
					}
					if (tableViewForm.getTable() != null) {
						importCSV(request, tableViewForm, helpForm);
					}
					forwardName = FORWARD_INPUT;
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
			catch (Exception e) {
				ExceptionUtil.errException(LOG, e, params);
			}
			try {
				if (tableViewForm.getTable() != null) {
					String query = TableViewAction.buildQuery(tableViewForm, request);
					List rows = getServiceLocator().getTablebrowserService()
							.fetchGeneric(TableViewForm.PAGESIZE * tableViewForm.getPageNr(), TableViewForm.PAGESIZE + 1, query,
									null);
					tableViewForm.setRows(rows);
				}
			}
			catch (EJBException e) {
				ExceptionUtil.errException(LOG, e, params);
				MessageUtils.addActionError(request, "error.tablebrowser.filter");
			}
			result = mapping.findForward(forwardName);
			return result;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private void importCSV(HttpServletRequest request, TableViewForm tableViewForm, FileImportForm helpForm) throws Exception {
		// [[
		Object[][] params = { { "request", request },
				{ "tableViewForm", tableViewForm },
				{ "helpForm", helpForm } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "importCSV", params);
		}
		// ]]
		TableBean currentTable = tableViewForm.getTable();
		FormFile inputCSV = helpForm.getCsvImportFile();
		try (InputStream inputCSVFIS = inputCSV.getInputStream(); InputStreamReader istr = new InputStreamReader(inputCSVFIS,
				StandardCharsets.UTF_8)) {

			CSVReader csvReader = new CSVReader(istr, ';');
			String[] line = csvReader.readNext();

			if (line.length != currentTable.getFields().size()) {
				MessageUtils.addActionError(request, "errors.csvContentError");

				return;
			}
			List<String> fields = new ArrayList<>();

			for (String currentField : line) {
				if (!("CTL_CRE_UID".equals(currentField) || "CTL_CRE_TS".equals(currentField) || "CTL_MOD_UID"
						.equals(currentField) || "CTL_MOD_TS".equals(currentField) || "CTL_TCN".equals(currentField))) {
					fields.add(currentField);
				}
			}

			do {
				line = csvReader.readNext();
				if (line == null) {
					continue;
				}
				if (line.length != currentTable.getFields().size()) {
					MessageUtils.addActionError(request, "errors.csvContentError");

					return;
				}
				else {
					//insert or update
					//fetch
					List wrappedCSVLine = TableBeanUtils.wrapRowForCSVImport(line, tableViewForm.getTable());

					String where = TableBeanUtils.getPkWhere(tableViewForm.getTable());
					Object[] pkVals = TableBeanUtils.getPkValues(wrappedCSVLine,
							tableViewForm.getTable());
					Object[] setVals;
					String set;

					String query = "select " + TableBeanUtils.allFields(tableViewForm.getTable()) + " from "
							+ Constant.SCHEMA_USER_PREPEND +
							tableViewForm.getTable().getName() + " where " + where;

					List rows = getServiceLocator().getTablebrowserService()
							.fetchGeneric(TableViewForm.PAGESIZE * tableViewForm.getPageNr(), TableViewForm.PAGESIZE + 1, query,
									pkVals);
					if (rows != null && rows.size() == 1) {
						//update
						where = TableBeanUtils.getPkWhere(tableViewForm.getTable());
						set = TableBeanUtils.getSetRWStringWithoutTcn(tableViewForm.getTable());
						setVals = TableBeanUtils.getSetRWValsWithoutTcn(wrappedCSVLine, tableViewForm.getTable());
						getServiceLocator().getTablebrowserService()
								.updateGeneric(pkVals[0], tableViewForm.getTable().getName(), getUser(), set, setVals, where,
										pkVals);
					}
					else if (rows != null && rows.isEmpty()) {
						//insert
						Object[] values = TableBeanUtils.getAllRWValues(wrappedCSVLine);

						if (!tableViewForm.getTable().isRefTable()) {
							boolean isFirstPk = true;
							for (int i = 0; i < fields.size(); i++) {
								for (Object o : tableViewForm.getTable().getFields()) {
									FieldBean fieldBean = (FieldBean) o;
									if ((fieldBean.getName().equals(fields.get(i))) && (fieldBean.isKey()) && (
											fieldBean.getSequenceName() != null)) {
										if ((values[i] == null) || (StringUtils.isEmpty(values[i].toString()))) {
											values[i] = new TableBrowserSequenceWrapper(fieldBean.getSequenceName(), isFirstPk);
											isFirstPk = false;
										}
										else {
											MessageUtils.addActionError(request,
													"error.tablebrowser.insert.only.allowed.with.sequence");
											return;
										}
									}
								}
							}
						}
						boolean hasTcnField = TableBeanUtils.tableHasTcnField(tableViewForm.getTable());
						getServiceLocator().getTablebrowserService()
								.insertGeneric(pkVals[0], tableViewForm.getTable().getName(), getUser(),
										StringUtils.join(fields, ','), values, hasTcnField);
					}
				}

			} while (line != null);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}
}
