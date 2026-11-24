<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: $ --%>
<%@page import="com.crh.logon.utils.AuthUtils"%>
<%@ page import="com.crh.logon.web.utils.ConfigBeanProvider" %>
<%@ page import="com.crh.logon.web.utils.ConfigUtil" %>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://com.crh.logon/tags/fmt.tld" prefix="fmt" %>
<fmt:setBundle basename="LitDict"/>

<%@ taglib uri="http://com.crh.logon/tags/common-taglib.tld" prefix="crh-common" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html> 
    <head>
        <title><%=ConfigBeanProvider.getConfigBean().getAppSystemBrowserwindowtitle() %></title>
        <meta name="robots" content="all">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%  Long userId = AuthUtils.getUserId();
            if ( userId != 1){
        %>
        <c:url var="companyFavicon" value="/blob">
        				<c:param name="entity" value="ImageEntity"/>
        				<c:param name="keyName" value="imageName"/>
        				<c:param name="keyValue" value="<%=ConfigBeanProvider.getConfigBean().getAppMainFavicon()%>"/>
        				<c:param name="favicon" value="true"/>
        			</c:url>
        <link rel="shortcut icon" type="image/x-icon" href="<c:out value="${companyFavicon}" />">
        <% } %>
        <link rel="stylesheet" type="text/css" href="<crh-common:url value="/css/crh_style.css"/>">
        <meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
        <meta http-equiv="PRAGMA" content="NO-CACHE">
		<meta name="keywords" content="Cement, Aggregates, Concrete, World Leading Supplier, Building Industry, Construction Materials,">
		<meta name="pageId" content="<%= request.getParameter("pageId") %>"/>
		<c:if test="${param.reloadInactiveSession eq 'true'}">
			<meta http-equiv="REFRESH" content="<c:out value="${pageContext.session.maxInactiveInterval}"/>" />
		</c:if>
    </head>
    <body>
<style type="text/css">
div.infoheader {
	color: <%= ConfigBeanProvider.getConfigBean().getAppDefaultHeaderColor() %>;
}
</style>
<script type="text/javascript">
function showHelp() {
	document.getElementById("helpDiv").style.display = "block";
}
</script>
        <table align="center" border="0" cellpadding="0" cellspacing="0" width="100%">
             <tbody>
                  <tr>
                   <%	if ( userId != 1){ %>
        			<c:url var="companyLogoUrl" value="/image">
        				<c:param name="entity" value="ImageEntity"/>
        				<c:param name="keyName" value="imageName"/>
        				<c:param name="keyValue" value="<%=ConfigBeanProvider.getConfigBean().getAppMainLogoCompany()%>"/>
        			</c:url>
                       <td height="55" class="applicationheader"><img src="<c:out value="${companyLogoUrl}" />" alt="Company" border="0"></td>
					<% } %>
<td width="100%" valign="top" class="applicationheader">
                      <table width="100%">
                          <tr>
                            <td valign="top" class="applicationheader">
                            <nobr>
                                <div class="infoheader" style="text-align:left;">
	                                <%
	                                	out.println(ConfigUtil.getApplicationString());
	                                %>
									<span>&nbsp;|&nbsp;</span>
										<%
											java.util.Date now = new java.util.Date();

											Locale locale = pageContext.getRequest().getLocale();
											String timezone = ConfigBeanProvider.getConfigBean().getAppDefaultTimezone();

											out.println(DateTimeFormat.asLogonDetailedDateTime(now, locale, timezone));
										%>
                                </div>
                              </nobr>
                            </td>
	                       <td valign="top" class="applicationheader">
	                           <nobr>
			                       <div id="helplink" class="infoheader" style="text-align:right;">
	        	                       <a href="javascript:showHelp()"><fmt:message key="application.help"/></a>
	                               </div>
	                           </nobr>
	                       </td>
                          </tr>
                      </table>
</td>

                       <td height="55" align="right" valign="top" class="applicationheader" style="width:100%;">
                       <% if ( userId != 1 ) { %>
	                    <c:url var="logonLogoUrl" value="/blob">
	                    	<c:param name="entity" value="ImageEntity"/>
	        				<c:param name="keyName" value="imageName"/>
	        				<c:param name="keyValue" value="<%=ConfigBeanProvider.getConfigBean().getAppMainLogoLogon()%>"/>
	        			</c:url>
	                       
                       		<img src="<c:out value="${logonLogoUrl}" />" alt="LOGON" style="margin: 10px 0px 0px 0px;">
                       <%} else { %>
                   	 		<img src="<crh-common:url value='/images/img_logo_logon.gif'/>" alt="LOGON" style="margin: 10px 0px 0px 0px;">
                       <% } %>	
                       </td>
                       <td width="10"     align="right" valign="top" class="applicationheader" style="width:10px;"><div style="width:10px;">&nbsp;</div></td>
                  </tr>
                  <tr>
                       <td height="18" class="applicationheader">&nbsp;</td>
                       <td height="18" valign="top" class="applicationheader">&nbsp;</td>
                       <td colspan="2" valign="top" class="applicationheader">&nbsp;</td>
                  </tr>
                  <tr>
                       <td height="15" colspan="4" class="applicationheader">
        
                       <table width="100%" border="0" cellpadding="0" cellspacing="0" class="navigation">
                                 <tr>
                                           <td width="100%" style="padding-left:10px">&nbsp;                                        
                                           </td>
                                 </tr>
                       </table>
        
                       </td>
        
                  </tr>
             </tbody>
        </table>

        <div align="center" class="customer_max" style="background-color:#f0f0f0;">

            <!-- begin Border-Table      -->
            <table align="center" cellpadding="0" cellspacing="0" border="0" style="padding:0px; margin:0px;">
<%-- END $RCSfile: $ --%>