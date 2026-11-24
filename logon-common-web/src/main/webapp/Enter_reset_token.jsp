<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="com.crh.logon.web.utils.ServiceLocatorProvider" %>
<%@ page import="com.crh.logon.utils.LogonException" %>
<%@ page import="com.crh.logon.business.services.utils.Message" %>
<%@ page import="com.crh.logon.utils.StringUtils" %>
<%@ page import="com.crh.logon.web.beans.PwdPolicyBean" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.LinkedList" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.Iterator" %>

<fmt:setBundle basename="LitDict"/>

<jsp:include page="/Page_header.jsp"/>

<script type="text/javascript" src="<crh-common:url value="/js/adapter/ext/ext-base.js"/>"></script>
<script type="text/javascript" src="<crh-common:url value="/js/ext-all.js"/>"></script>
<script type="text/javascript" src="<crh-common:url value="/js/logon-password.js"/>"></script>

<%-- BEGIN $RCSfile: $ --%>
		<tr>    
			<td align="center">
<%
final Locale locale = request.getLocale();
final String token = request.getParameter("token") != null ? request.getParameter("token") : "";
final String password = request.getParameter("password");
final String confirm = request.getParameter("confirm");


final boolean isSubmitClicked = request.getParameter("submit") != null;
final List validationErrors = new LinkedList();
final List<String> someList = new LinkedList<String>();
boolean serviceSuccessful = false;

// Case 1:  User clicked submit -> check password
if (isSubmitClicked)
{
	// TODO/marius 5.10.2012: Implement password policy verification
	
	//////// VALIDATION ///////////
	VALIDATION: 
	do {
		if (StringUtils.empty(password)) {
			validationErrors.add(Message.lookup(locale, "passwordchange.errors.fieldEmpty", new Object[] {Message.lookup(locale, "passwordchange.field.newpwd")}));
		}
		if (StringUtils.empty(confirm)) {
			validationErrors.add(Message.lookup(locale, "passwordchange.errors.fieldEmpty", new Object[] {Message.lookup(locale, "passwordchange.field.repeatenewpwd")}));
		}
		
		// We don't do any more validation if not both passwords are set.
		if (!validationErrors.isEmpty()) {
			break VALIDATION;
		}

		if (!password.equals(confirm)) {
			validationErrors.add(Message.lookup(locale, "passwordchange.errors.pwdsMustBeTheSame"));
			// Stop validating, makes no sense to check policies.
			break VALIDATION;
		}
		
		for (Iterator i = PwdPolicyBean.validateExtPwdPolicy(password).iterator(); i.hasNext(); ) 
		{
			PwdPolicyBean extPwdPolicy = (PwdPolicyBean) i.next();
			if (Boolean.FALSE.equals(extPwdPolicy.getValid())) {
				validationErrors.add(Message.lookup(locale, PwdPolicyBean.EXT_PWD_POLICY_ERROR));
				break VALIDATION;
			}
		}

		// Make sure the password is not in the history
		/* TODO marius/5.4.2012:
	     * Enable password history check.
	     * However, currently the following problem occurs:
	     * The method validateExtPwdHistory() calls Authorization.authorize(), which fails
	     * with an NPE if no user is logged in. In the use case of a password reset, we will
	     * never have a logged in user, so this will always fail.
	     * What can we do? Is it allowed just to remove the authorization check from validateExtPwdHistory()?
	     */
/* 		UserEntity user = ServiceLocatorProvider.getServiceLocator().getUserService().fetchUserByExtPwdResetToken(token);
		if (user != null) {
			final String extLogin = user.getExtLogin();
			if (!PwdPolicyBean.validateExtPwdHistory(extLogin, password)) {
				validationErrors.add(Message.lookup(locale, "useradmin.errors.password.in.history"));
			}
		} */
		
	} while (false);

	if (validationErrors.isEmpty())
	{
		// Call service
		try {
			ServiceLocatorProvider.getServiceLocator().getUserAdminService().resetExtPasswordWithToken(token, password);
			serviceSuccessful = true;
%>
		<table width="20%" height="100" align="center" cellpadding="3" cellspacing="1" border="0" class="border">
			<tr>
				<td class="frmTitel" colspan="2">
            		<p>
                		<fmt:message key="label.login.reset.extPassword.successful" />
                	</p>
				</td>
				</tr>
			</table>
<%
		}
		catch (Exception e) 
		{
			final String msgKey;
			
			if (e.getCause() instanceof LogonException) {
				LogonException le = (LogonException) e.getCause();
				msgKey = le.getReason().getReason();
			}
			else {
				msgKey = "errors.login.newPassword.contactSysadm";
			}
			
			final String msg = Message.lookup(request.getLocale(), msgKey);
%>
		<div align="center" style="background-color:#f0f0f0;">
			<table cellpadding="0" cellspacing="0">
				<tr>
					<td>
						<div class="error">
							<%= msg %>
						</div>
					</td>
				</tr>
			</table>
		</div>
<%
		}
	}
}

// We allow the user to keep entering as long as there is an error. 
if (!serviceSuccessful) {
	for (Iterator i = validationErrors.iterator(); i.hasNext(); )
	{
		String errorMsg = (String) i.next();
%>
				<div align="center" style="background-color:#f0f0f0;">
					<table cellpadding="0" cellspacing="0">
						<tr>
							<td>
								<div class="error"><%= errorMsg %></div>
							</td>
						</tr>
					</table>
				</div>
<%
	}
%>

				<form name="ResetPasswordForm" method="post" action="<%= request.getRequestURI() %>" autocomplete="off">
					<input type="hidden" name="loginUrl" value="<c:out value='${param.loginUrl}'/>"/>
					<table width="20%" height="100" align="center" cellpadding="3" cellspacing="1" border="0" class="border">
						<tr>
    						<td class="frmTitel" colspan="2" style="font-weight: bold;">
	                    		<fmt:message key="label.login.reset.extPassword" />
        	            	</td>
		    			</tr>
						<tr>
							<td class="frmLabel" nowrap=""><fmt:message key="label.login.reset.extPassword.token" /></td>
							<td class="frmInput"><input type="text" name="token" style="width: 100%" maxlength="50" value="<%= token %>" autocomplete="off"></td>
						</tr>
						<tr>
			     			<td class="frmLabel" nowrap=""><fmt:message key="passwordchange.field.newpwd" /></td>
			    			<td class="frmInput"><input name="password" type="password" style="width: 100%" maxlength="100"/></td>
			    		</tr>
						<tr>
			     			<td class="frmLabel" nowrap=""><fmt:message key="passwordchange.field.repeatenewpwd" /></td>
			    			<td class="frmInput"><input name="confirm" type="password" style="width: 100%" maxlength="100"/></td>
			    		</tr>
					</table>
					<br>
					<table width="20%" align="center" cellpadding="0" cellspacing="1" border="0">
    					<tr>
    						<td width="100%" class="frmSubmit">
    							<input class="submit" type="submit" name="submit" value="<fmt:message key="buttons.ok" />" />
    						</td>
    						<td width="100%" class="frmSubmit">
    							<input class="submit" type="submit" name="cancel" value="<fmt:message key="buttons.cancel" />" />
    						</td>
    					</tr>
    				</table>
    			</form>
<% 
}
%>
       		</td>
       		<td height="100%"></td>
       	</tr>
<%-- END $RCSfile: $ --%>

<jsp:include page="/Page_footer.jsp"/>