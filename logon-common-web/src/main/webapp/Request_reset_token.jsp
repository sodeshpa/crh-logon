<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>

<%@ page import="com.crh.logon.web.utils.ConfigBeanProvider" %>
<%@ page import="com.crh.logon.web.utils.ServiceLocatorProvider" %>
<%@ page import="com.crh.logon.business.services.utils.Message" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://com.crh.logon/tags/fmt.tld" prefix="fmt" %>
<fmt:setBundle basename="LitDict"/>

<jsp:include page="/Page_header.jsp"/>

<%-- BEGIN $RCSfile: $ --%>
		<tr>    
			<td align="center">
<% 
final String SUBMIT_BUTTON_NAME = "requestreset";
final int MIN_RESET_TIME = ConfigBeanProvider.getConfigBean().getAppPolicyPasswordMinResetIntervalS();

// Case 1:  User clicked cancel -> redirect to standard login page
if (request.getParameter("cancel") != null) {
%>
	<c:redirect url=""/>
<%
}

// Case 2: User clicked Send -> call business service to reset password and display message.
else if (request.getParameter(SUBMIT_BUTTON_NAME) != null) {
	// Optionally: could check if the username is not empty, but for efficiency's sake I'm not implementing that right now.
	ServiceLocatorProvider.getServiceLocator().getUserAdminService().requestPasswordResetToken(request.getParameter("username"));
			
	// marius/2012-10-10: We have to use this "hand stitched" method to get the message.
	// We cannot call the version Message.lookup(Locale, String, ...) because it turned out
	// that the JSP compiler used in the nightly build doesn't support varargs.
	// So we stay on the safe side. 
	final String condition3MessageRaw = Message.lookup(request.getLocale(), "label.login.reset.extPassword.confirmation.condition3");
	final String condition3Message = condition3MessageRaw.replace("{0}", Integer.toString(MIN_RESET_TIME));
%>
					<table width="20%" height="100" align="center" cellpadding="3" cellspacing="1" border="0" class="border">
						<tr>
       						<td class="frmTitel" colspan="2">
                    	<p>
                        	<fmt:message key="label.login.reset.extPassword.confirmation.intro" /> <br/>
                        	<br/>
                            <fmt:message key="label.login.reset.extPassword.confirmation.condition1" /> <br/>
                            <fmt:message key="label.login.reset.extPassword.confirmation.condition2" /> <br/>
                            <%= condition3Message %> <br/>
                            <br/>                                
                            <fmt:message key="label.login.reset.extPassword.confirmation.outro" />
                        </p>
                            </td>
       					</tr>
       				</table>
<%
}

// Case 3: Initial call of this jsp -> Display the form where the username can be entered.
else {
%>
	<form name="ForgotPasswordForm" method="post" action="<%= request.getRequestURI() %>" autocomplete="off">
		<input type="hidden" name="loginUrl" value="<c:out value='${param.loginUrl}'/>"/>
			<table width="20%" height="100" align="center" cellpadding="3" cellspacing="1" border="0" class="border">
				<tr>
    				<td class="frmTitel" colspan="2" style="font-weight: bold;">
                    	<fmt:message key="label.login.newPassword.pwdForgot" />
                    </td>
    			</tr>
    			<tr>
    				<td style="align: left;" colspan="2">
                    	<p>
                        	<fmt:message key="label.login.newPassword.info.line1" /> <br/>
                            <fmt:message key="label.login.newPassword.info.line2" /> <br/>
                            <fmt:message key="label.login.newPassword.info.line3" /> <br/>
                            <fmt:message key="label.login.newPassword.info.line4" /> <br/>
                            <fmt:message key="label.login.newPassword.info.line5" /> <br/>
                            <br/>
                            <br/>                                
                        </p>
			</tr>
			<tr>
     					<td class="frmLabel" nowrap="">
                             <fmt:message key="label.login.newPassword.benutzer" />
                         </td>
    						<td class="frmInput"><input type="text" name="username" size="20" value="" autocomplete="off"></td>
    					</tr>
		</table>
					<br>
				<table width="20%" align="center" cellpadding="0" cellspacing="1" border="0">
    					<tr>
    						<td width="100%" class="frmSubmit">
    							<input class="submit" type="submit" name="requestreset" value="<fmt:message key="buttons.ok" />" />
    						</td>
    						<td width="100%" class="frmSubmit">
    							<input class="submit" type="submit" name="cancel" value="<fmt:message key="buttons.cancel" />" />
    						</td>
    					</tr>
    				</table>
    			</form>
<% } %>
       		</td>
       		<td height="100%"></td>
       	</tr>
<%-- END $RCSfile: $ --%>

<jsp:include page="/Page_footer.jsp"/>
