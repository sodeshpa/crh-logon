<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://com.crh.logon/tags/fmt.tld" prefix="fmt" %>
<fmt:setBundle basename="LitDict"/>

<jsp:include page="/Page_header.jsp"/>

<%-- BEGIN $RCSfile: Login_new_password.jsp,v $ --%>
		<tr>    
			<td align="center">
<% 
if (request.getParameter("cancel") != null) {
%>
	<c:redirect url=""/>
<%
}
if (request.getParameter("sendnewpassword") != null) {
%>
    <c:set var="divMsg"><fmt:message key="info.login.newPassword.updated" /></c:set>
    <c:set var="divClass" value="info"/>
<%
	try {
		com.crh.logon.web.utils.ServiceLocatorProvider.getServiceLocator().getUserAdminService().sendNewPassword(request.getParameter("userName"));
	} catch (Exception e) {
%>
    <c:set var="divClass" value="error"/>
    <c:set var="divMsg"><fmt:message key="errors.login.newPassword.contactSysadm" /></c:set>
<%	
	}
%>
<div align="center" style="background-color:#f0f0f0;">
	<table cellpadding="0" cellspacing="0">
		<tr>
			<td>
				<div class="<c:out value="divClass" />"><c:out value="${divMsg}" /></div>
			</td>
		</tr>
	</table>
	<a href="<c:out value='${pageContext.request.contextPath}'/>"><fmt:message key="label.login.newPassword.login" /></a>
</div>
<%} else {%>
				<form name="ForgotPasswordForm" method="post" action="<%= request.getRequestURI() %>" autocomplete="off">
					<input type="hidden" name="loginUrl" value="<c:out value='${param.loginUrl}'/>"/>
					<table width="20%" height="100" align="center" cellpadding="3" cellspacing="1" border="0" class="border">
<% 		boolean formCreated = false;
		if ( (request.getParameter("getuserinfo") != null) && (request.getParameter("username") != null) ) {
			String userName = request.getParameter("username"); 
			String emailAddress = com.crh.logon.web.utils.ServiceLocatorProvider.getServiceLocator().getUserAdminService().getEmailAddress(userName);
			boolean isExtLoginPwdLocked = com.crh.logon.web.utils.ServiceLocatorProvider.getServiceLocator().getUserAdminService().isExtLoginPwdLocked(userName);
			if (emailAddress == null) {
%>
						<tr>
							<td colspan="2"><div class="error">
                                <fmt:message key="errors.login.newPassword.contactSysadm2" />
                            </td>
						</tr>
						
<%			} else if (isExtLoginPwdLocked) { %>
						<tr>
							<td colspan="2"><div class="error">
			                    <fmt:message key="errors.login.newPassword.contactSysadm3" />
			                </td>
						</tr>
<%			} else {
				formCreated = true;%>
						<!-- DO NOT SHOW PRIVACY DATA (User enumeration attack)
						<tr>
       						<td class="frmTitel" colspan="2" style="font-weight: bold;"><fmt:message key="label.login.newPassword.enterData" /></td>
       					</tr>
       					<tr>
       						<td style="align: left;" colspan="2">
	       						<p>
	       							<fmt:message key="label.login.newPassword.username" /> <%= userName %><br/>
	       							<fmt:message key="label.login.newPassword.email" /> <%= emailAddress %>
		       					</p>
		       					<p>
                                    <fmt:message key="label.login.newPassword.checkEmail" />
								</p>
							</td>
						</tr>
       					-->
					</table>
   					<br>
  					<table width="20%" align="center" cellpadding="0" cellspacing="1" border="0">
       					<tr>
       						<td width="100%" class="frmSubmit">
       							<input class="submit" type="submit" name="sendnewpassword" value="<fmt:message key="button.login.newPassword.newPassword" />" />
       						</td>
       						<td width="100%" class="frmSubmit">
       							<input class="submit" type="submit" name="cancel" value="<fmt:message key="buttons.cancel" />" />
       						</td>
       					</tr>
       				</table>
       				<input type="hidden" name="userName" value="<%= userName %>">
<%	 		}  
		}
		if (!formCreated) { %>
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
                                </p>
                                <p>
                                    <fmt:message key="label.login.newPassword.info.line4" /> <br/>
                                    <fmt:message key="label.login.newPassword.info.line5" /> <br/>
                                    <fmt:message key="label.login.newPassword.info.line6" /> <br/>
                                    <fmt:message key="label.login.newPassword.info.line7" /> <br/>
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
       							<input class="submit" type="submit" name="getuserinfo" value="<fmt:message key="buttons.ok" />" />
       						</td>
       						<td width="100%" class="frmSubmit">
       							<input class="submit" type="submit" name="cancel" value="<fmt:message key="buttons.cancel" />" />
       						</td>
       					</tr>
       				</table>
<% 		} %>
       			</form>
<% } %>
       		</td>
       		<td height="100%"></td>
       	</tr>

<%-- END $RCSfile: Login_new_password.jsp,v $ --%>

<jsp:include page="/Page_footer.jsp"/>