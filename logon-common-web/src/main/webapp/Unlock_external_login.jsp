<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>

<%@ page import="com.crh.logon.utils.AuthUtils" %>
<%@ page import="com.crh.logon.beans.UnlockExtLoginBean" %>
<%@ page import="com.crh.logon.web.utils.ServiceLocatorProvider" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://com.crh.logon/tags/fmt.tld" prefix="fmt" %>

<fmt:setBundle basename="LitDict"/>

<jsp:useBean id="unlockBean" class="com.crh.logon.beans.UnlockExtLoginBean" scope="page"/>

<%		
	try {
		unlockBean.setExtLogin(request.getParameter("extlogin"));
		unlockBean.setLock(request.getParameter("lock"));
		unlockBean.setPassword(request.getParameter("password"));
		
		if (unlockBean.getExtLogin() != null && unlockBean.getLock() != null && unlockBean.getPassword() != null) {
			String result = ServiceLocatorProvider.getServiceLocator().getUserAdminService().unlockExtLogin(unlockBean);
			unlockBean.setResult(result);
		}
	}
	catch (Exception e) {
		unlockBean.setResult(UnlockExtLoginBean.UNLOCK_FAILED);
	}
	finally {
		AuthUtils.deauthorize();
	}
%>

<jsp:include page="/Page_header.jsp"/>
<script type="text/javascript" src="<crh-common:url value="/js/adapter/ext/ext-base.js"/>"></script>
<script type="text/javascript" src="<crh-common:url value="/js/ext-all.js"/>"></script>
<script type="text/javascript" src="<crh-common:url value="/js/logon-password.js"/>"></script>
<%-- BEGIN $RCSfile: $ --%>
		<tr>
			<td>
				<c:choose>
					<c:when test="${unlockBean.result eq 'application.unlock.ext.login.unsuccessful'}">
						<div class="error">
				    		<fmt:message key="${unlockBean.result}" />
						</div>
					</c:when>
					<c:when test="${unlockBean.result eq 'application.unlock.ext.login.successful'}">
						<div class="info">
					    	<fmt:message key="${unlockBean.result}" />
					    	<br/>
					    	<a href="<c:url value='/'/>">
					    		<fmt:message key="application.loginform.button.login" />
					    	</a>
						</div>
					</c:when>
					<c:otherwise>
						&nbsp;
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<c:if test="${unlockBean.result ne 'application.unlock.ext.login.successful'}">
			<tr>
				<td align="center">
					<form name="UnlockExtLoginForm" method="post">
						<table width="20%" height="100" align="center" cellpadding="3" cellspacing="1" border="0" class="border">
							<tr>
	       						<td class="frmTitel" colspan="2">
									<fmt:message key="application.unlock.ext.login.title" />
								</td>
	       					</tr>
							<tr>
								<td class="frmLabel" nowrap="">
									<fmt:message key="application.unlock.ext.login.extlogin" />
								</td>
								<td>
									<c:out value="${unlockBean.extLogin}"/>
								</td>
							</tr>
							<tr>
								<td>
									<fmt:message key="application.unlock.ext.login.password" />
								</td>
								<td>
									<input type="password" name="password" size="20" value="">
								</td>
							</tr>
						</table>
						<br>
	    				<table width="20%" align="center" cellpadding="0" cellspacing="1" border="0">
	   						<tr>
	   							<td class="frmSubmit">
	   								<input class="submit" type="submit" name="submit" value="<fmt:message key="application.unlock.ext.login.button.unlock" />">
	   							</td>
	   							<td class="frmSubmit">
	   								<input class="submit" type="reset" value="<fmt:message key="application.loginform.button.cancel" />"/>
	   							</td>
	   						</tr>
	    				</table>
					</form>
				</td>
			</tr>
		</c:if>
<%-- END $RCSfile: $ --%>

<jsp:include page="/Page_footer.jsp"/>