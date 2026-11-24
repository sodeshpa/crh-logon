<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: TerminalMainLayout.jsp,v $ --%>

<%@ include file="./includeTLDs.jsp" %>

<%@ page session="true"%>

<%@page import="com.crh.logon.web.actions.ActionSupport"%>
<%@page import="com.crh.logon.web.utils.UserProvider"%>
<%@page import="com.crh.logon.web.utils.ConfigBeanProvider"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@page import="com.crh.logon.terminal.web.actions.TerminalActionSupport" %>

<style type="text/css">
html,body {
	overflow: auto;
}
</style>


<%@page
	import="com.crh.logon.terminal.web.utils.LoadingpointProvider"%><html:html>
<head>
<title><%=ConfigBeanProvider.getConfigBean().getAppSystemBrowserwindowtitle() %></title>
<meta name="robots" content="all">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<c:url var="companyFavicon" value="/imagename">
	<c:param name="keyValue" value="<%=ConfigBeanProvider.getConfigBean().getAppMainFavicon()%>"/>
	<c:param name="favicon" value="true"/>
</c:url>
<link rel="shortcut icon" type="image/x-icon" href="<c:out value="${companyFavicon}" />">
<link rel="stylesheet" type="text/css"
	href="<crh-common:url value="/css/crh_touchscreen_style.css"/>">
</head>
<body>
	<div id="grayout" class="grayout"></div>
	<table height="100%" align="center" border="0" cellpadding="0"
		cellspacing="0" width="100%">
		<tbody>
			<tr height="60px">
				<td class="applicationheader">
					<table align="center" border="0" cellpadding="0" cellspacing="0"
						width="100%" class="navigation">
						<tbody>
							<tr>
								<td class="applicationheader" width="10"></td>
								<td class="applicationheader" style="vertical-align: bottom;">
								<c:url var="companyLogoUrl" value="/imagename">
			        				<c:param name="keyValue" value="<%=ConfigBeanProvider.getConfigBean().getAppMainLogoCompany()%>"/>
			        			</c:url>
									<img src="<c:out value="${companyLogoUrl}" />"
									alt="Company" height="55" border="0" style="margin-left: 6px">
								</td>
								<td style="padding-left: 10px"
									style="vertical-align:bottom;padding-bottom:5px;" width="100%">
									<table border="0" width="100%" cellpadding="0" cellspacing="0">
										<tr>
											<td style="vertical-align: bottom;" height="30">
												<%= LoadingpointProvider.getLoadingpoint().getShippingpoint().getShippingpointName() %>&nbsp;
												- &nbsp;<%= LoadingpointProvider.getLoadingpoint().getLoadingpointName() %>
											</td>
										</tr>
									</table>
								</td>
								<td width="61" style="vertical-align: bottom;"
									class="applicationheader">
									<tiles:insert attribute="terminalheader" />
								</td>
							</tr>
						</tbody>
					</table>
				</td>
			</tr>
			<tr>
				<td>
					<div class="customer_max">
						<!-- begin Border-Table   -->
						<table height="100%" cellpadding="0" cellspacing="0" border="0"
							width="100%" style="padding: 0px; margin: 0px">
							<tr>
								<td width="5">&nbsp;</td>
								<td>
									<!-- START CONTENT --> 
									<tiles:insert attribute="body" />
									<!-- END CONTENT -->
									<tiles:insert attribute="errors" />
								</td>
								<td width="5">&nbsp;</td>
							</tr>
						</table>
						<!-- End Border Table -->
					</div>
				</td>
			</tr>
		</tbody>
	</table>
</body>
</html:html>
<%-- END $RCSfile: TerminalMainLayout.jsp,v $ --%>