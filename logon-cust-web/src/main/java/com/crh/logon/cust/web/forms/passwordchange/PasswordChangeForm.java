/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.cust.web.forms.passwordchange;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.utils.PasswordUtil;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.beans.PwdPolicyBean;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.UserProvider;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="cust_PasswordChangeForm"
 */
public class PasswordChangeForm extends ActionForm {

	private static final long serialVersionUID = -8910104051337644556L;
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PasswordChangeForm.class);

	private String extLoginPwdOld;
	private String extLoginPwd;
	private String extLoginPwd2;
	private String hashedExtLoginPwd;

	private String originalUrl;
	private String specialSetOfChars;
	private List<PwdPolicyBean> extLoginPwdPolicies = null;
	private boolean extLoginPwdPolicyVisible = false;
	
	/**
	 * Default constructor.
	 */
	public PasswordChangeForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "PasswordChangeForm");
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
			extLoginPwdOld = "";
			extLoginPwd = "";
			extLoginPwd2 = "";
			hashedExtLoginPwd = "";
			extLoginPwdPolicies = PwdPolicyBean.createExtPwdPolicyBeans();
			extLoginPwdPolicyVisible = false;
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
	 * Note: this is not associated with or invoked by the reset button in the
	 * web form, but rather is called on every request before the struts action
	 * method gets control and, more importantly, before the request parameters
	 * are automatically copied into the form. The primary use is to reset
	 * checkboxes and struts multiboxes and the like for which the browser only
	 * sends the form parameters when they are set.
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// [[
		Object[][] params = {{"mapping", mapping},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "reset", params);
		}
		// ]]
		try {
			
			;
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
			
			// prepare new and old password for check
			final String oldPwd = UserProvider.getUser().getExtLoginPwd();
			if (extLoginPwdOld == null || extLoginPwdOld.isEmpty()) {
				errors.add(ActionMessages.GLOBAL_MESSAGE,
						new ActionMessage("passwordchange.errors.fieldEmpty", new String [] {MessageUtils.getMessage(request, "passwordchange.field.oldpwd")}));
				return result = errors;
			}
			
			// check old password
			if (!PasswordUtil.validatePassword(extLoginPwdOld, oldPwd)) {
				errors.add(ActionMessages.GLOBAL_MESSAGE,
						new ActionMessage("passwordchange.errors.oldExtLoginPwdNotValid"));
				return result = errors;
			}
			
			// check new password(s)
			if (StringUtils.empty(extLoginPwd)){
				errors.add(ActionMessages.GLOBAL_MESSAGE,
						new ActionMessage("passwordchange.errors.fieldEmpty", new String [] {MessageUtils.getMessage(request, "passwordchange.field.newpwd")}));
				return result = errors;
			}
			else if (StringUtils.empty(extLoginPwd2)) {
				errors.add(ActionMessages.GLOBAL_MESSAGE,
						new ActionMessage("passwordchange.errors.fieldEmpty", new String [] {MessageUtils.getMessage(request, "passwordchange.field.repeatenewpwd")}));
				return result = errors;
			}
			else if (!extLoginPwd.equals(extLoginPwd2)) {
				errors.add(ActionMessages.GLOBAL_MESSAGE,
						new ActionMessage("passwordchange.errors.pwdsMustBeTheSame"));
				return result = errors;
			}
			
			// check new pwd != old pwd
			if (extLoginPwdOld.equals(extLoginPwd)) {
				errors.add(ActionMessages.GLOBAL_MESSAGE,
						new ActionMessage("useradmin.errors.password.in.history"));
				return result = errors;
			}
			
			// check external pwd password policy
	    	extLoginPwdPolicies = PwdPolicyBean.validateExtPwdPolicy(extLoginPwd);
	        for (PwdPolicyBean extPwdPolicy : extLoginPwdPolicies) {
	        	if (Boolean.FALSE.equals(extPwdPolicy.getValid())) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE,
                            new ActionMessage(PwdPolicyBean.EXT_PWD_POLICY_ERROR));
	        		extLoginPwdPolicyVisible = true;
	        		break;
	        	}
	        }
			
			String hashedNewExtPwd = PasswordUtil.hashValueIfNotHashed(extLoginPwd);
			setHashedExtLoginPwd(hashedNewExtPwd);			
			// check external password history
			boolean isValid = PwdPolicyBean.validateExtPwdHistory(getExtLogin(), hashedNewExtPwd);
			if (!isValid) {
				errors.add(ActionMessages.GLOBAL_MESSAGE,
						new ActionMessage("useradmin.errors.password.in.history"));
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

	public String getExtLoginPwd() {
		return extLoginPwd;
	}

	public void setExtLoginPwd(String extLoginPwd) {
		this.extLoginPwd = extLoginPwd;
	}

	public String getExtLoginPwd2() {
		return extLoginPwd2;
	}

	public void setExtLoginPwd2(String extLoginPwd2) {
		this.extLoginPwd2 = extLoginPwd2;
	}

	public String getExtLoginPwdOld() {
		return extLoginPwdOld;
	}

	public void setExtLoginPwdOld(String extLoginPwdOld) {
		this.extLoginPwdOld = extLoginPwdOld;
	}

	public String getExtLogin() {
		return UserProvider.getUser().getExtLogin();
	}

	public String getOriginalUrl() {
		return originalUrl;
	}

	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}

	public List<PwdPolicyBean> getExtLoginPwdPolicies() {
		return extLoginPwdPolicies;
	}
	
	public void setExtLoginPwdPolicies(List<PwdPolicyBean> extLoginPwdPolicies) {
		this.extLoginPwdPolicies = extLoginPwdPolicies;
	}

	public boolean isExtLoginPwdPolicyVisible() {
		return getExtLoginPwdPolicyVisible();
	}
	
	public boolean getExtLoginPwdPolicyVisible() {
		return extLoginPwdPolicyVisible;
	}

	public void setExtLoginPwdPolicyVisible(boolean extLoginPwdPolicyVisible) {
		this.extLoginPwdPolicyVisible = extLoginPwdPolicyVisible;
	}

	public String getSpecialSetOfChars() {
		return specialSetOfChars;
	}

	public void setSpecialSetOfChars(String specialSetOfChars) {
		this.specialSetOfChars = specialSetOfChars;
	}
	
	public String getHashedExtLoginPwd() {
		return hashedExtLoginPwd;
	}

	public void setHashedExtLoginPwd(String hashedExtLoginPwd) {
		this.hashedExtLoginPwd = hashedExtLoginPwd;
	}
	
}