package com.crh.logon.admin.web.forms.tablebrowser;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_FileImportForm"
 */
public class FileImportForm extends ActionForm {

	private static final long serialVersionUID = 1L;

	private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(FileImportForm.class);
	
	private FormFile csvImportFile;
	
	/**
     * Default constructor.
     */
    public FileImportForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "FileImportForm");
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
	    	csvImportFile = null;
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
	        
	        if (errors == null) {
	        	errors = new ActionErrors();
	        }
	        if (request.getParameter("import") != null) {
				if(csvImportFile == null || csvImportFile.getFileSize() == -1 || csvImportFile.getFileSize() == 0){
					errors.add(ActionMessages.GLOBAL_MESSAGE,
    	                    new ActionMessage("errors.missingCSV"));
				}
	        }
			
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
    
	public FormFile getCsvImportFile() {
		return csvImportFile;
	}

	public void setCsvImportFile(FormFile csvImportFile) {
		this.csvImportFile = csvImportFile;
	}
}
