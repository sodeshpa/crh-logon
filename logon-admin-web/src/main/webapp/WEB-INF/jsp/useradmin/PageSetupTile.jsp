<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: PageSetupTile.jsp,v $ --%>

<%@ include file="/WEB-INF/jsp/includeTLDs.jsp" %>

<tiles:importAttribute name="focus" ignore="true"/>
  
<script type="text/javascript"> 

document.onkeydown = function(e) {
	var targ;
	var code;
	if (!e) var e = window.event;

	if (e.keyCode) code = e.keyCode;
	else if (e.which) code = e.which;


	if (code == 13) {// 13 = the code for pressing ENTER
		if (e.target) targ = e.target;
		else if (e.srcElement) targ = e.srcElement;
		if (targ.nodeType == 3) // defeat Safari bug
			targ = targ.parentNode;

		if (targ != null) {
				if(targ.type == "text") {
					if (targ.className != null && (targ.className.substring(0,2) == 'x-' || targ.className.substring(0,3) == ' x-')) { // defeat IE 'bug' that string starts at index 1
						return true;//do nothing for ext text boxes
					}
					else {
						return false;
					}
				}
				if (targ.type == "checkbox") {
					return false;
				}
		}
	}
}
<c:if test="${!empty focus}">
var focusElement = document.getElementById('<c:out value="${focus}"/>');
if (focusElement) {
	focusElement.focus();
}
</c:if>
</script>