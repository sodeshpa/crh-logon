<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: WideLayout.jsp,v $ --%>

<%@ include file="includeTLDs.jsp" %>

<%@ page session="true" %>

<%@page import="com.crh.logon.web.utils.ConfigBeanProvider"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><%=ConfigBeanProvider.getConfigBean().getAppSystemBrowserwindowtitle() %></title>
		<link rel="stylesheet" href="<crh-common:url value='/css/crh_style.css'/>" type="text/css">
		<link rel="shortcut ico" type="image/x-icon" href="<crh-common:url value='/images/favicon.ico'/>">
		<script language="JavaScript" type="text/javascript" src="<crh-common:url value='js/ua.js'/>"></script>
		<script language="JavaScript" type="text/javascript" src="<crh-common:url value='js/ftiens4.js'/>"></script>
		<script language="JavaScript" type="text/javascript" src="<crh-common:url value='js/menuNodes.js'/>"></script>
	</head>
	<body>
		<table style="height: 100%; width:100%; border:none; border-collapse:collapse;" cellpadding="0" cellspacing="0">
			<tr>
				<td colspan="2" class="applicationheader">
					<!-- HEADER -->
					<tiles:insert attribute="header"/>
				</td>
			</tr>
			<tr>
				<td class="customer_max">
					<table width="100%" cellpadding="10" cellspacing="0">
						<tr>
							<td>
								<tiles:insert attribute="errors" />
							</td>
						</tr>
						<tr>
							<td>
								<!-- START CONTENT -->
								<tiles:insert attribute="body" />
								<!-- END CONTENT -->
							</td>
						</tr>
					</table>
				</td>
				<td height="100%"></td>
			</tr>
		</table>
		<tiles:insert attribute="footer" ignore="true" />
	</body>
</html:html>

<%-- END $RCSfile: WideLayout.jsp,v $ --%>
