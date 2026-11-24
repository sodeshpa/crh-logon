<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: TerminalLogin.jsp,v $ --%>

<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" session="true" %>

<%@page import="com.crh.logon.web.utils.ConfigBeanProvider"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://com.crh.logon/tags/fmt.tld" prefix="fmt" %>
<fmt:setBundle basename="LitDict"/>

<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean-el" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic-el" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>

<%@ taglib uri="http://nevis.ch/espace2/taglib/espacecore.tld" prefix="espace" %>
<%@ taglib uri="http://nevis.ch/espace2/taglib/espacewidget.tld" prefix="widget" %>

<%@ taglib uri="http://com.crh.logon/tags/common-taglib.tld" prefix="crh-common" %>
<%@page import="com.crh.logon.terminal.web.filters.TerminalIdentificationFilter"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html> 
    <head>
        <title><%=ConfigBeanProvider.getConfigBean().getAppSystemBrowserwindowtitle() %></title>
        <meta name="robots" content="all">
        <%-- FIXME, probably not the contextpath what has to be checked--%>
        <link rel="stylesheet" type="text/css" href="<crh-common:url value="/css/crh_touchscreen_style.css"/>">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
        <meta http-equiv="PRAGMA" content="NO-CACHE">
		<meta name="keywords" content="Cement, Aggregates, Concrete, World Leading Supplier, Building Industry, Construction Materials,">
		<meta name="pageId" content="LOGIN_PAGE474c6ecd1b22f9236780b5bb2d3b9944"/>
		<meta http-equiv="REFRESH" content="<c:out value="${pageContext.session.maxInactiveInterval}"/>" />
<script language="javascript">
<%--------------------------------------------------------------------------------------------------------------------------------------------------------------------------%>
<%--The terminal has a request parameter - which is REQUIRED and must be there always in order to be able to identify the loadingpoint. This request parameter is in the----%>
<%--request URL. In case the login fails, the login module throws this parameter away, and there is no chance to put the loadingpoint parameter back to the original url-----%>
<%--unless we store it in this cookie, and read back when login fails. The cookie is created when the login screen is visible, and the URL has the parameter.
<%--------------------------------------------------------------------------------------------------------------------------------------------------------------------------%>
var href = window.location.href;
var index = href.indexOf("<%=TerminalIdentificationFilter.REQUEST_PARAMETER_LOADINGPOINT_IDENFITIER%>=");
if (index != -1) {	
	var lp = href.substr(index + "<%=TerminalIdentificationFilter.REQUEST_PARAMETER_LOADINGPOINT_IDENFITIER%>=".length);
	document.cookie = 'loadingpoint=' + lp;
}

function resetAndFocus() {
	document.getElementById('j_username').value="";
	document.getElementById('j_password').value="";
	var el = document.getElementById('j_username');
	el.focus();
}
</script>

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
                       
<td width="100%" valign="top" class="applicationheader">
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
                          </tr>
                      </table>
</td>

                       <td height="55" align="right" valign="top" class="applicationheader" style="width:100%;"><img src="<crh-common:url value='/images/img_logo_logon.gif'/>" alt="LOGON" style="margin: 10px 0px 0px 0px;"></td>
                       <td width="10"     align="right" valign="top" class="applicationheader" style="width:10px;"><div style="width:10px;">&nbsp;</div></td>
                  </tr>
                  <tr>
                       <td height="18" valign="top" class="applicationheader">&nbsp;</td>
                       <td colspan="2" valign="top" class="applicationheader">&nbsp;</td>
                  </tr>
                  <tr>
                       <td height="15" colspan="3" class="applicationheader">
        
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
        				<form name="LoginForm" method="post" action="j_security_check">
        <div align="center" class="customer_max" style="background-color:#f0f0f0;">
        <br><br>
                    <br>
                    Loadingpoint Terminal
                    <br><br><br>
            <!-- begin Border-Table      -->
            <table align="center" cellpadding="0" cellspacing="0" border="0" style="padding:0px; margin:0px;">
                <tr>
                    <td align="center" width="10" height="1">&nbsp;</td>
                </tr>
                <tr>    
                    <td align="center">
        					<table style="width:70%" align="center" cellpadding="3" cellspacing="15" border="0" class="border">
        						<tr>
        							<td class="frmTitel" colspan="2">Anmeldung / Inscription / Registrazione:</td>
        						</tr>
        						<tr>
        							<td class="frmLabel" nowrap="">Benutzer / Client / Utente</td>
        							<td align="right" class="frmInput"><input type="text" id="j_username" name="j_username"  style="font-size:36px;width:380px" value=""></td>
        						</tr>
        						<tr>
        							<td class="frmLabel" nowrap="">Passwort / Mot de passe / Codice</td>
        							<td align="right" class="frmInput"><input type="password"  id="j_password" name="j_password"  style="font-size:36px;width:380px"  value="">
        							</td>
        						</tr>
        						
        					</table>
        					<br>
        					<br><br>
                    <br>
        					<table width="20%" align="center" cellpadding="0" cellspacing="1" border="0">
        						<tr>
        							<td width="100%" class="frmSubmit">
        							<input type="submit" name="submit" style="width:570px" type="button" class="ViewButtonStyle"  value=" Anmelden / Connecter / Registrare">
        							</td>
        							<td>&nbsp;&nbsp;&nbsp;&nbsp;
        							
        							</td>
        							<td width="100%" class="frmSubmit">
        								<input style="width:570px" onclick="resetAndFocus()" type="button" class="ViewButtonStyle"  value="Reset">        							
        							</td>
        						</tr>
        					</table>
        			</td>
        			<td height="100%">
        			</td>
        		</tr>
            </table>
            <!-- End Border Table -->
            <br>
        </div>
        				</form>
    </body>
    <script language="javascript">
		resetAndFocus();
    </script>
</html:html>
