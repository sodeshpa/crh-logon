/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.tablebrowser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.crh.logon.admin.web.beans.FieldBean;
import com.crh.logon.admin.web.beans.TableBean;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.beans.ValueBean;
import com.crh.logon.admin.web.beans.ValueConversionException;
import com.crh.logon.persistence.entities.tablebrowser.ColumnMeta;
import com.crh.logon.persistence.entities.tablebrowser.TableMeta;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_RowEditForm"
 */
public class RowEditForm extends ActionForm {

	private static final long serialVersionUID = 1L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RowEditForm.class);

	// input from the user
	private boolean editMode;

	// db state
	private List row = null;

	private TableBean table = null;

	private TableMeta tableMeta = null;
	
	private List optionValues;

	private HashMap mimeMap;

	//internal fields
	private ActionErrors fieldValidationErrors;
	
	private boolean updateAllowed;
	private boolean insertAllowed;
	private boolean deleteAllowed;

	/**
	 * Default constructor.
	 */
	public RowEditForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "RowEditForm");
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
			row = new LinkedList();
			table = null;
			tableMeta = null;
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
			if (fieldValidationErrors != null) {
				errors.add(fieldValidationErrors);
				fieldValidationErrors = null;
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

	public List getRow() {
		return row;
	}

	public void setRow(List row) {
		this.row = row;
		recalcRow();
	}

	public TableBean getTable() {
		return table;
	}

	public void setTable(TableBean table) {
		this.table = table;
		recalcRow();
	}

	public List getOptionValues() {
		return optionValues;
	}

	public void setOptionValues(List optionValues) {
		this.optionValues = optionValues;
	}

	public TableMeta getTableMeta() {
		return tableMeta;
	}

	public void setTableMeta(TableMeta tableMeta) {
		this.tableMeta = tableMeta;
		recalcRow();
	}

	private void recalcRow() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "recalcRow");
		}
		// ]]
		try {
			if ((table == null) || (row == null)) {
				return;
			}
			Iterator fields = table.getFields().iterator();
			Iterator values = row.iterator();
			while (fields.hasNext()) {
				FieldBean fb = (FieldBean) fields.next();
				ValueBean vb = (ValueBean) values.next();
				boolean tmedit = true;
				if (tableMeta != null) {
					ColumnMeta cm = fb.getColumnMeta();
					tmedit = (cm == null) || cm.getUpdateAllowed();
				}
				vb.setEditable(tmedit && (!fb.isKey() || !editMode));
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	public boolean isEditMode() {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("isEditMode(): '" + editMode + "'");
		}
		// ]]
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
		recalcRow();
	}

	public void setValue(int index, String value) throws Exception {
		try {
			ValueBean vb = (ValueBean) row.get(index);
			vb.setWebValue(value);
		}
		catch (Exception e) {
			String field = ((FieldBean) table.getFields().get(index)).getName();
			LOG.error("Error validating field " + field + ": '" + e.getMessage());
			if (fieldValidationErrors == null) {
				fieldValidationErrors = new ActionErrors();
			}
			if (e instanceof ValueConversionException) {
				ValueConversionException vce = (ValueConversionException) e;
				fieldValidationErrors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"error.field.validation", field));
				fieldValidationErrors.add(ActionMessages.GLOBAL_MESSAGE, vce.getActionMessage());
			}
			else {
				fieldValidationErrors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"error.unexpected", e.getMessage()));
			}
		}
	}

	public void setBlobValue(int index, Object value) throws Exception {
		try {
			if (value instanceof FormFile) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Value instanceof FormFile");
				}
				FormFile formFile = (FormFile) value;

				ValueBean vb = (ValueBean) row.get(index);
				byte[] buf = formFile.getFileData();
				
				if (buf.length != 0){
					vb.setWebValue(buf);	
				}
			}
		}
		catch (Exception e) {
			String field = ((FieldBean) table.getFields().get(index)).getName();
			LOG.error("Error validating field " + field, e);
			if (fieldValidationErrors == null) {
				fieldValidationErrors = new ActionErrors();
			}
			if (e instanceof ValueConversionException) {
				ValueConversionException vce = (ValueConversionException) e;
				fieldValidationErrors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"error.field.validation", field));
				fieldValidationErrors.add(ActionMessages.GLOBAL_MESSAGE, vce.getActionMessage());
			}
			else {
				fieldValidationErrors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"error.unexpected", e.getMessage()));
			}
		}
	}
	
	public HashMap getMimeMap() {
		return mimeMap;
	}

	public void setMimeMap(HashMap mimeMap) {
		this.mimeMap = mimeMap;
	}

	public boolean isUpdateAllowed() {
		return updateAllowed;
	}

	public void setUpdateAllowed(boolean updateAllowed) {
		this.updateAllowed = updateAllowed;
	}

	public boolean isInsertAllowed() {
		return insertAllowed;
	}

	public void setInsertAllowed(boolean insertAllowed) {
		this.insertAllowed = insertAllowed;
	}

	public boolean isDeleteAllowed() {
		return deleteAllowed;
	}

	public void setDeleteAllowed(boolean deleteAllowed) {
		this.deleteAllowed = deleteAllowed;
	}

}
