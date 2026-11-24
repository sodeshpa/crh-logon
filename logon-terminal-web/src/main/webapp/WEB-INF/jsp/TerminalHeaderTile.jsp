<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: TerminalHeaderTile.jsp,v $ --%>

<%@ page session="true" %>

<style type="text/css">
div.infoheader {
	color: <%= ConfigBeanProvider.getConfigBean().getAppDefaultHeaderColor() %>;
}
</style>
<%@page import="com.crh.logon.utils.AuthUtils"%>
<%@page import="com.crh.logon.web.utils.UserProvider"%>
<%@page import="com.crh.logon.web.utils.ConfigBeanProvider"%>
<%@ include file="./includeTLDs.jsp"%>
<table width="100%">
	<tr>
		<td style="vertical-align: bottom; padding-bottom: 5px;" align="right"
			class="applicationheader">
			<nobr>
				<div class="infoheader">
					<logic:present name="org.apache.struts.action.ERROR" scope="request">
						<c:out value="${sessionScope.FatalErrorForm.errorId}" />
					</logic:present>
					<%if ( UserProvider.getUser() != null ) {%>
					<%= UserProvider.getUser().getPerson().getFirstname() %>
					<%= UserProvider.getUser().getPerson().getName() %>
					<c:if test="${!empty driver}">
						<span>&nbsp;|&nbsp;</span>
						<c:out value="${driver.currentTruck.truckName}" />
						&nbsp;
						(<c:out value="${driver.currentTruck.truckPlateNo}" />)
					</c:if>
					<span>&nbsp;&nbsp;</span>
					<%} %>
				</div>
			</nobr>
		</td>
		<td height="55">
					   <%  Long userId = AuthUtils.getUserId();   %>
                       <%	if ( userId != 1){ %>
						<c:url var="logonLogoUrl" value="/imagename">
							<c:param name="entity" value="ImageEntity"/>
	        				<c:param name="keyValue" value="<%=ConfigBeanProvider.getConfigBean().getAppMainLogoLogon()%>"/>
	        				<c:param name="keyName" value="imageName"/>
	        			</c:url>
						<td height="55" align="right" valign="top" class="applicationheader" style="width:100%;"><img src="<c:out value="${logonLogoUrl}" />" alt="LOGON" style="margin: 10px 0px 0px 0px;"></td>
						<% } else { %>
							<td height="55" align="right" valign="top" class="applicationheader" style="width:100%;"><img src="<crh-common:url value='/images/img_logo_logon.gif'/>" alt="LOGON" style="margin: 10px 0px 0px 0px;"></td>
						<% } %>
		</td>
	</tr>
</table>
<%-- END $RCSfile: TerminalHeaderTile.jsp,v $ --%>