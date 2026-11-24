<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: TerminalErrorsTile.jsp,v $ --%>

<%@ page session="true" %>
<%@ include file="./includeTLDs.jsp" %>
<script language="javascript">
var errors;
function showAllMessages(){
	if (errors != undefined){
	var greyLayer =document.getElementById('grayout');
	greyLayer.style.display='block';
	greyLayer.style.textAlign='center';
	greyLayer.style.visibility='hidden';
	greyLayer.innerHTML="<table style=\"width:100%;height:100%;\"><tr style=\"vertical-align:middle;\"><td align=\"center\"><div class=\"error\" style=\"width:50%;\">" +
	errors.join('\n') + "<br><br>" + 
	"<input style=\"width:120px\" onclick=\"javascript:ungrayoutScreen()\" type=\"button\" value=\"" +
	'<fmt:message key="buttons.ok"/>' + 
	"\" class=\"ViewButtonStyle\" >" +
	"</div></td></tr></table>";
	greyLayer.style.visibility='visible';
	}
}

function ungrayoutScreen(){
	var greyLayer =document.getElementById('grayout');
	greyLayer.style.width='0px';
	greyLayer.style.height='0px';
	greyLayer.style.display='none';
	greyLayer.style.visibility='hidden';
	greyLayer.innerHTML="";
}
</script>

<%@ include file="./includeTLDs.jsp" %>
  <script type="text/javascript">
<logic:present name="org.apache.struts.action.ERROR" scope="request">
	var errors = new Array();
	<html:messages id="msg">
		errors.push('<c:out value="${pageScope.msg}"/>');
	</html:messages>
</logic:present>

<logic:present name="org.apache.struts.action.ACTION_WARNINGS" scope="request">
	if (errors == undefined) {
		errors = new Array();
	} 
	<html:messages id="warning" name="org.apache.struts.action.ACTION_WARNINGS">
		errors.push('<c:out value="${pageScope.warning}"/>');
	</html:messages>
</logic:present>

<logic:present name="org.apache.struts.action.EXCEPTION" scope="request">
	if (errors == undefined) {
		errors = new Array();
	} 
	<html:messages id="msg" name="org.apache.struts.action.ACTION_EXCEPTION">
		errors.push('<c:out value="${msg}"/>');
	</html:messages>
</logic:present>
	if (errors != undefined){
    	showAllMessages();
	}
</script>
