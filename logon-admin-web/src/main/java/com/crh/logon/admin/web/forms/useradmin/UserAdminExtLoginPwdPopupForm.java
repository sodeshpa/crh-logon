/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.useradmin;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.UserActivityService;
import com.crh.logon.utils.PasswordUtil;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.beans.PwdPolicyBean;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.ServiceLocatorProvider;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_UserAdminExtLoginPwdPopupForm"
 */
public class UserAdminExtLoginPwdPopupForm extends ActionForm {

	private static final long serialVersionUID = -8910104051337644556L;
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(UserAdminExtLoginPwdPopupForm.class);
	
	public static final int FORMTYPE_EXTPWD = 0;
	public static final int FORMTYPE_PDAPIN = 1;
	public static final int PDA_PIN_MIN_LENGTH = 4;

	private String extLoginPwdOld;
	private String extLoginPwdNew;
	private String extLoginPwdNew2;
	private String specialSetOfChars;
	private boolean self = false;
	private boolean accessChange = false;
	private int formType;
	private List<PwdPolicyBean> extLoginPwdPolicies = null;
	private boolean extLoginPwdPolicyVisible = false;

	/**
	 * Default constructor.
	 */
	public UserAdminExtLoginPwdPopupForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "UserAdminExtLoginPwdPopupForm");
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
			extLoginPwdNew = "";
			extLoginPwdNew2 = "";
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
			
			// check old password (only at self admin)
		    if (self) {
                UserAdminForm uaForm = (UserAdminForm) request.getSession().getAttribute(
                		"admin_UserAdminForm");
                
                
                if (formType == FORMTYPE_EXTPWD) {
                	
                	// check external password lock (e.g. multiple sessions)
                	if (uaForm.getUserEntity().getExtLoginPwdLockedTs() != null) {
                		LOG.error("external login password is locked, could not be changed by user, extLogin: " + uaForm.getUserEntity().getExtLogin());
                		Authorization.authorize(Authorization.PUBLIC_FUNCTION);
                		UserActivityService uas = ServiceLocatorProvider.getServiceLocator().getUserActivityService();
                		uas.recordExtLoginPwdUnlockAttempt(uaForm.getUserEntity().getExtLogin(), false);
                		errors.add(ActionMessages.GLOBAL_MESSAGE,
                                new ActionMessage("application.ext.login.pwd.locked"));
                	}
                	
                	// check external password change date
                	if (!PwdPolicyBean.validateExtPwdChangeDate(uaForm.getUserEntity().getExtLogin())) {
                		Date notAllowedToChangeBefore = PwdPolicyBean.getAllowedExtPwdChangeDate(uaForm.getUserEntity().getExtLogin());
                		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", new Locale(uaForm.getUserEntity().getApplicationLang().getLangCd()));
                		String dateStr = sdf.format(notAllowedToChangeBefore);
                		errors.add(ActionMessages.GLOBAL_MESSAGE,
                                new ActionMessage("useradmin.errors.password.not.allowed.to.change.before", new String [] {dateStr}));
                	}
                }
                
                // check old external pwd/PIN is not null or empty
                if (StringUtils.empty(extLoginPwdOld)) {
                    if (formType == FORMTYPE_PDAPIN) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE,
                                new ActionMessage("useradmin.errors.fieldEmpty", new String [] {"Altes PIN"}));
                    }
                    else if (formType == FORMTYPE_EXTPWD) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE,
                                new ActionMessage("useradmin.errors.fieldEmpty", new String [] {"Altes Passwort"}));
                    }
                }
                // check old external pwd/PIN is correct
                else {
                	// PIN
                	if (formType == FORMTYPE_PDAPIN) {
                		// check old PIN correct
                		if (!PasswordUtil.validatePassword(extLoginPwdOld, uaForm.getUserEntity().getPdaPin())) {
                    		errors.add(ActionMessages.GLOBAL_MESSAGE,
                                    new ActionMessage("useradmin.errors.pin_not_correct"));
                		}
                    }
                	// external pwd
                    else if (formType == FORMTYPE_EXTPWD) {
                    	// check old external pwd correct
                    	if (!PasswordUtil.validatePassword(extLoginPwdOld, uaForm.getUserEntity().getExtLoginPwd())) {
                    		errors.add(ActionMessages.GLOBAL_MESSAGE,
                    				new ActionMessage("useradmin.errors.password_not_correct"));
                    	}
                    }
                }
                
                // if the old external pwd/PIN is incorrect
                if (!errors.isEmpty()) {
                	extLoginPwdOld = "";
                }
		    }
			
			// check new and repeated (new2) external pwd/PIN are not null or empty
			if (StringUtils.empty(extLoginPwdNew) || StringUtils.empty(extLoginPwdNew2)) {
                if (formType == FORMTYPE_PDAPIN) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE,
                            new ActionMessage("useradmin.errors.fieldEmpty", new String [] {"PIN"}));
                }
                else if (formType == FORMTYPE_EXTPWD) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE,
                            new ActionMessage("useradmin.errors.fieldEmpty", new String [] {"Passwort"}));
                    extLoginPwdPolicies = PwdPolicyBean.createExtPwdPolicyBeans();
                }
			}
			// check new and repeated (new2) external pwd/PIN are the same
			else if (!extLoginPwdNew.equals(extLoginPwdNew2)) {
				extLoginPwdNew = "";
				extLoginPwdNew2 = "";
			    if (formType == FORMTYPE_PDAPIN) {
	                errors.add(ActionMessages.GLOBAL_MESSAGE,
	                        new ActionMessage("useradmin.errors.pinsMustBeTheSame"));
			    }
			    else if (formType == FORMTYPE_EXTPWD) {
			        errors.add(ActionMessages.GLOBAL_MESSAGE,
                            new ActionMessage("useradmin.errors.pwdsMustBeTheSame"));
			        extLoginPwdPolicies = PwdPolicyBean.createExtPwdPolicyBeans();
			    }
			}
			else {
				// check new password must have minimum 4 characters
				if (formType == FORMTYPE_PDAPIN) {
					if (extLoginPwdNew.length() < PDA_PIN_MIN_LENGTH) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
								"useradmin.errors.pdapin_less_than_n_characters",
								new String[] { String.valueOf(PDA_PIN_MIN_LENGTH) }));
					}
				}
				
				// check external pwd password policy
				if (formType == FORMTYPE_EXTPWD) {
			    	extLoginPwdPolicies = PwdPolicyBean.validateExtPwdPolicy(extLoginPwdNew);
			        for (PwdPolicyBean extPwdPolicy : extLoginPwdPolicies) {
			        	if (Boolean.FALSE.equals(extPwdPolicy.getValid())) {
		                    errors.add(ActionMessages.GLOBAL_MESSAGE,
		                            new ActionMessage(PwdPolicyBean.EXT_PWD_POLICY_ERROR));
			        		extLoginPwdPolicyVisible = true;
			        		break;
			        	}
			        }
				}
				
				// check external password history
				if (formType == FORMTYPE_EXTPWD) {
					UserAdminForm uaForm = (UserAdminForm) request.getSession().getAttribute("admin_UserAdminForm");
					String extLogin = uaForm.getUserEntity().getExtLogin();
					if (StringUtils.notEmpty(extLogin)) {		// might be null when a new user is being created
						boolean isValid = PwdPolicyBean.validateExtPwdHistory(extLogin, extLoginPwdNew);
						if (!isValid) {
							errors.add(ActionMessages.GLOBAL_MESSAGE,
									new ActionMessage("useradmin.errors.password.in.history"));
						}
					}
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

    public String getExtLoginPwdOld() {
            return extLoginPwdOld;
    }

    public void setExtLoginPwdOld(String extLoginPwdOld) {
            this.extLoginPwdOld = extLoginPwdOld;
    }

    public String getExtLoginPwdNew() {
            return extLoginPwdNew;
    }

    public void setExtLoginPwdNew(String extLoginPwdNew) {
            this.extLoginPwdNew = extLoginPwdNew;
    }

	public String getExtLoginPwdNew2() {
		return extLoginPwdNew2;
	}

	public void setExtLoginPwdNew2(String extLoginPwdNew2) {
		this.extLoginPwdNew2 = extLoginPwdNew2;
	}

	public boolean isSelf() {
		return self;
	}

	public void setSelf(boolean self) {
		this.self = self;
	}

	public boolean isAccessChange() {
		return accessChange;
	}

	public void setAccessChange(boolean accessChange) {
		this.accessChange = accessChange;
	}

	public int getFormType() {
		return formType;
	}

	public void setFormType(int formType) {
		this.formType = formType;
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
	
}