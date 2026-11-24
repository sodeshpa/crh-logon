<%@ include file="includeTLDs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%--------------------------------------------------------------------------------------------------------------------------------------------------------------------------%>
<%--This is a shared tile which is shown in case one was directed here after a successful workflow, or when a fatal error happened.-----------------------------------------%>
<%--In both cases the user will be logged out after 5 seconds unless even before if he clicks the logout button, which will log him out.
<%--------------------------------------------------------------------------------------------------------------------------------------------------------------------------%>

<%@page import="com.crh.logon.terminal.web.filters.TerminalIdentificationFilter"%>
<%@page import="com.crh.logon.terminal.web.utils.LoadingpointProvider"%>
<%@page import="com.crh.logon.terminal.web.actions.TerminalActionSupport"%>
<html>
<head>
<meta name="robots" content="all">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<link rel="stylesheet" type="text/css" href="./css/crh_touchscreen_style.css">
<title>Crh Ltd - Global supplier of cement, aggregates and concrete</title>
<meta name="abstract" content="Crh is one of the world's le">
<meta name="keywords" content="Cement, Aggregates, Concrete, World Leading Supplier, Building Industry, Construction Materials,">
<meta name="description" content="">
</head>
<body>
<table align="center" border="0" cellpadding="0" cellspacing="0" width="100%">
     <tbody>
          <tr>
               <td height="55" align="right" valign="top" class="applicationheader" style="width:100%;"><img src="./images/img_logo_logon.gif" alt="LOGON" width="73" height="50" style="margin: 10px 0px 0px 0px;"></td>
               <td width="10"     align="right" valign="top" class="applicationheader" style="width:10px;"><div style="width:10px;">&nbsp;</div></td>
          </tr>
          <tr>
               <td height="18" valign="top" class="applicationheader">&nbsp;</td>
               <td valign="top" class="applicationheader">&nbsp;</td>
          </tr>
          <tr>
               <td height="15" colspan="2" class="applicationheader">

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
     	<c:choose>
     	<c:when test="${empty sessionScope.FatalErrorForm.errorId}">
		 	<div class="info"><fmt:message key="terminal.common.successlogout"/></div>
     	</c:when>
     	<c:otherwise>
     		<div class="error"><bean:message key="terminal.common.error"/></div>
     	</c:otherwise>
     	</c:choose>
     	
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
<script language="javascript">

function toLogin(){
	
	var str = document.cookie;
	if (str != undefined) {
		var index = str.indexOf("<%=TerminalIdentificationFilter.REQUEST_PARAMETER_LOADINGPOINT_IDENFITIER%>=");
		if (index != -1) {	
			var indexOfComma = str.indexOf(';');
			var lp = str.substring(index + "<%=TerminalIdentificationFilter.REQUEST_PARAMETER_LOADINGPOINT_IDENFITIER%>=".length, indexOfComma);
			<%-- Send back to the same loadingpoint where it was initially called--%>
			window.location.href="<c:out value='${pageContext.request.contextPath}'/>/TerminalEntry.do?<%=TerminalIdentificationFilter.REQUEST_PARAMETER_LOADINGPOINT_IDENFITIER%>=<%=LoadingpointProvider.getPickupLoadingpoint().getClientIdentifier()%>"
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

</body>
</html>
