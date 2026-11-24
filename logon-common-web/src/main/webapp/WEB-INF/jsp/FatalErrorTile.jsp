<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: FatalErrorTile.jsp,v $ --%>

<%@ page session="true" %>

<%@ include file="includeTLDs.jsp" %>
<c:if test="false">
	<%-- for preview in MyEclipse --%>
	<link rel="stylesheet" href="<c:url value="/css/crh_style.css"/>" type="text/css">
</c:if>

<%@page import="com.crh.logon.web.utils.ConfigBeanProvider"%>
<div class="error">
   <ul class="error">
		<bean:message key="fatalerror.fatalError"/>
		<bean:message key="fatalerror.helpmsg"/>
<% if (ConfigBeanProvider.getConfigBean().getLogonProperties().getDevmode()) { %>
		<bean:message key="fatalerror.devmodeonly"/>
<% } %>
   </ul>
</div>

<% if (ConfigBeanProvider.getConfigBean().getLogonProperties().getDevmode()) { %>
<table cellpadding="0" cellspacing="1">
    <tr>
        <td width="10%" valign="top"><b><bean:message key="fatalerror.errorId"/></b></td>
        <td width="90%">
            <b><c:out value="${sessionScope.FatalErrorForm.errorId}"/></b>
        </td>
    </tr>
    <tr>
        <td valign="top"><b>Reason: </b></td>
        <td width="80%">
            <b><c:out value="${sessionScope.FatalErrorForm.reason}"/></b>
        </td>
    </tr>
	<tr>
		<td colspan="2">
        <pre>
<c:out value="${sessionScope.FatalErrorForm.stackTrace}"/>
        </pre>
		</td>
	</tr>
</table>
<% } %>


<%-- END $RCSfile: FatalErrorTile.jsp,v $ --%>
