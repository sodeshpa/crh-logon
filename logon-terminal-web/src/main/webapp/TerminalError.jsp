<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: TerminalError.jsp,v $ --%>

<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" session="true" %>
<%@page import="com.crh.logon.utils.AuthUtils"%>
<%@page import="com.crh.logon.web.utils.ConfigBeanProvider"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://com.crh.logon/tags/fmt.tld" prefix="fmt" %>
<fmt:setBundle basename="LitDict"/>
<%@page import="com.crh.logon.terminal.web.filters.TerminalIdentificationFilter"%>
<%@page import="com.crh.logon.terminal.web.utils.LoadingpointProvider"%>
<%@page import="com.crh.logon.terminal.web.actions.TerminalActionSupport"%>
<html>

<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean-el" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic-el" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>

<%@ taglib uri="http://nevis.ch/espace2/taglib/espacecore.tld" prefix="espace" %>
<%@ taglib uri="http://nevis.ch/espace2/taglib/espacewidget.tld" prefix="widget" %>

<%@ taglib uri="http://com.crh.logon/tags/common-taglib.tld" prefix="crh-common" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html> 
    <head>
        <title><%=ConfigBeanProvider.getConfigBean().getAppSystemBrowserwindowtitle() %></title>
        <meta name="robots" content="all">
        <%  Long userId = AuthUtils.getUserId();
        	if ( userId != 1){
		%>
         <c:url var="companyFavicon" value="/image">
        				<c:param name="entity" value="ImageEntity"/>
        				<c:param name="keyName" value="imageName"/>
        				<c:param name="keyValue" value="<%=ConfigBeanProvider.getConfigBean().getAppMainFavicon()%>"/>
        				<c:param name="favicon" value="true"/>
        			</c:url>
        <link rel="shortcut icon" type="image/x-icon" href="<c:out value="${companyFavicon}" />">
        <% } %>
        <%-- FIXME, probably not the contextpath what has to be checked--%>
        <link rel="stylesheet" type="text/css" href="<crh-common:url value="/css/crh_touchscreen_style.css"/>">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
        <meta http-equiv="PRAGMA" content="NO-CACHE">
        <meta name="keywords" content="Cement, Aggregates, Concrete, World Leading Supplier, Building Industry, Construction Materials,">
    </head>
    <body scroll="no">
<style type="text/css">
div.infoheader {
	color: <%= ConfigBeanProvider.getConfigBean().getAppDefaultHeaderColor() %>;
}
</style>


        <table align="center" border="0" cellpadding="0" cellspacing="0" width="100%">
             <tbody>
                  <tr>
                  	<%	if ( userId != 1){	%>
        			<c:url var="companyLogoUrl" value="/image">
        				<c:param name="entity" value="ImageEntity"/>
        				<c:param name="keyValue" value="<%=ConfigBeanProvider.getConfigBean().getAppMainLogoCompany()%>"/>
        				<c:param name="keyName" value="imageName"/>
        			</c:url>
                    <td height="55" class="applicationheader"><img src="<c:out value="${companyLogoUrl}" />" alt="Company" border="0"></td>
					<% } %>	
<td valign="top" class="applicationheader">
                      <table width="100%">
                          <tr>
                            <td valign="top" class="applicationheader">
                            <nobr>
                                <div class="infoheader" style="text-align:left;"> 
	                                    <logic:present name="org.apache.struts.action.ERROR" scope="request"><c:out value="${sessionScope.FatalErrorForm.errorId}" /> | </logic:present><c:out value='${applicationScope.properties.instance}' /> v<c:out value='${applicationScope.properties.buildVersion}' />
                                    <span>&nbsp;|&nbsp;</span> 
										<%
									    java.util.Date date = new java.util.Date();
									    out.println( date );
										%>
                                </div>
                              </nobr>
                            </td>
                            <td width="10" align="right" valign="top" class="applicationheader">
                              &nbsp;
                            </td>
                          </tr>
                      </table>
</td>
					<%	if ( userId != 1){ %>
						<c:url var="logonLogoUrl" value="/image">
							<c:param name="entity" value="ImageEntity"/>
	        				<c:param name="keyValue" value="<%=ConfigBeanProvider.getConfigBean().getAppMainLogoLogon()%>"/>
	        				<c:param name="keyName" value="imageName"/>
	        			</c:url>
						<td height="55" align="right" valign="top" class="applicationheader" style="width:100%;"><img src="<c:out value="${logonLogoUrl}" />" alt="LOGON" style="margin: 10px 0px 0px 0px;"></td>
					<% } else { %>
						<td height="55" align="right" valign="top" class="applicationheader" style="width:100%;"><img src="<crh-common:url value='/images/img_logo_logon.gif'/>" alt="LOGON" style="margin: 10px 0px 0px 0px;"></td>
					<% } %>
                       <td width="10"     align="right" valign="top" class="applicationheader" style="width:10px;"><div style="width:10px;">&nbsp;</div></td>
                  </tr>
                  <tr>
           		<%	if ( userId != 1){ %>
               		 <td     height="18" class="applicationheader">&nbsp;</td>
                 <% } %>
                 	   <td height="18" valign="top" class="applicationheader">&nbsp;</td>
                       <td colspan="2" valign="top" class="applicationheader">&nbsp;</td>
                  </tr>
                  <tr>
                  		<%	if ( userId != 1){ %>
                       		<td height="15" colspan="4" class="applicationheader">
                       	<% } else { %>
        					<td height="15" colspan="3" class="applicationheader">
        					<% } %>
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
        <br>
<div align="center" style="background-color:#f0f0f0;">
<table align="center" cellpadding="0" cellspacing="0" border="0" width="40%" style="padding:0px; margin:0px;">
	<tr>
		<td>
		 <table border="0" cellpadding="0" cellspacing="0" width="100%">
     <tbody>
     <tr align="center">
     	<td>
     	<br>
     	<br>
     	
		 <div class="error"><bean:message key="errors.login"/></div>
     	<br>
     	<br>
     	<br>
     	<br>
     	<input onclick="javascript:toLogin()" style="width:250px" type="button" class="ViewButtonStyle"  value=" Go to login ">
     	<br><br>
                    <br>
     	</td>
     </tr>
     <tr height="20px" align="center">
     <td>
     &nbsp;
     </td>
     </tr>
     <tr align="center">
          <td id="counterTd">
          <fmt:message key="terminal.common.loginerrorbacktologininsec"/>&nbsp; &#13; <%=TerminalActionSupport.COUNT_DOWN_SECONDS_ERROR_LOGIN%> &#13; seconds
          </td>
          </tr>
     </tbody>
</table>
		</td>
	</tr>
</table>
      </div>
<%--------------------------------------------------------------------------------------------------------------------------------------------------------------------------%>
<%--The terminal has a request parameter - which is REQUIRED and must be there always in order to be able to identify the loadingpoint. This request parameter is in the----%>
<%--request URL. In case the login fails, the login module throws this parameter away, and there is no chance to put the loadingpoint parameter back to the original url-----%>
<%--unless we store it in this cookie, and read back when login fails. The cookie is created when the login screen is visible, and the URL has the parameter.
<%--------------------------------------------------------------------------------------------------------------------------------------------------------------------------%>
<script language="javascript">

function toLogin(){
	
	var str = document.cookie;
	if (str != undefined) {
		var index = str.indexOf("<%=TerminalIdentificationFilter.REQUEST_PARAMETER_LOADINGPOINT_IDENFITIER%>=");
		if (index != -1) {	
			var indexOfComma = str.indexOf(';');
			var lp = str.substring(index + "<%=TerminalIdentificationFilter.REQUEST_PARAMETER_LOADINGPOINT_IDENFITIER%>=".length, indexOfComma);
			<%-- Send back to the same loadingpoint where it was initially called--%>
			window.location.href="<c:out value='${pageContext.request.contextPath}'/>/TerminalEntry.do?<%=TerminalIdentificationFilter.REQUEST_PARAMETER_LOADINGPOINT_IDENFITIER%>=" + lp;
		}
	}
	else{
		return false;
	}
}


var c=<%=TerminalActionSupport.COUNT_DOWN_SECONDS_ERROR_LOGIN%>;

var t;
var timer_is_on=0;

function timedCount(){
	var re = new RegExp("[\\d]{1,}", "g");
	var text = document.getElementById('counterTd').innerHTML;
	text = text.replace(re, c);
	document.getElementById('counterTd').innerHTML=text;
	if (c == 0 || c < 0){
		stopCount();
		return false;
	}
	c=c-1;
	t=setTimeout("timedCount()",1000);
}

function doTimer(){
	if (!timer_is_on){
  		timer_is_on=1;
  		timedCount();
  	}
}

function stopCount(){
	clearTimeout(t);
	timer_is_on=0;
	toLogin();
	return false;
}

window.onload=doTimer;
</script>

<br>
            <!-- End Border Table -->
            <br>
    </body>
</html:html>
