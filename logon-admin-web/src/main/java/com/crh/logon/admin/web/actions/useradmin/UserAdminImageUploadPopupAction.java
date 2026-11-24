/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.useradmin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.forms.useradmin.UserAdminForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.useradmin.UserAdminImageUploadPopupForm;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_UserAdminImageUploadForm" path="/useradmin/UserAdminImageUploadPopup" scope="request"
 *  validate="true" input="UserAdminImageUploadPopupTile.def"
 *
 * @struts.action-forward name="img"  path="UserAdminImageUploadPopupTile.def"
 * @struts.action-forward name="upload" path="UserAdminImageUploadPopupClose.def"
 */
public class UserAdminImageUploadPopupAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(UserAdminImageUploadPopupAction.class);

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
			// Get the struts form(s).
			
			UserAdminImageUploadPopupForm imgForm = (UserAdminImageUploadPopupForm) form;
			
			
			String forwardName = "img";
			
			if ("upload".equals(imgForm.getInputAction())) {
				if (logDebugEnabled) {
					LOG.debug("Action: upload button is pressed");
				}
				UserAdminForm userAdminForm = (UserAdminForm) request.getSession().getAttribute("admin_UserAdminForm");
				
				FormFile file = imgForm.getImage();
				String type = imgForm.getType();
				if (Integer.toString(UserAdminForm.IMG_TYPE_PHOTOIMAGE).equals(type)) {
					userAdminForm.setPhotoImage(file.getFileData());
					userAdminForm.setPhotoImageContentType(file.getContentType());
					userAdminForm.setPhotoImageFilename(file.getFileName());
				}
				else if (Integer.toString(UserAdminForm.IMG_TYPE_SIGNATURE).equals(type)) {
					userAdminForm.setSignature(file.getFileData());
					userAdminForm.setSignatureContentType(file.getContentType());
					userAdminForm.setSignatureFilename(file.getFileName());
				}
				else {
					MessageUtils.addActionError(request,"useradmin.errors.unknownimagetype");
				}				
			
				forwardName = null;
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
