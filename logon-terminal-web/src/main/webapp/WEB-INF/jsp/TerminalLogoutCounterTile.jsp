<%@ include file="includeTLDs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@page
	import="com.crh.logon.terminal.web.filters.TerminalIdentificationFilter"%>
<%@page
	import="com.crh.logon.terminal.web.utils.LoadingpointProvider"%><html>

<script language="javascript">
	<%-- Send back to the same loadingpoint where it was initially called--%>
	function goBack() {
		window.location.href="<c:out value='${pageContext.request.contextPath}'/>/TerminalEntry.do?<%=TerminalIdentificationFilter.REQUEST_PARAMETER_LOADINGPOINT_IDENFITIER%>=<%=LoadingpointProvider.getPickupLoadingpoint().getClientIdentifier()%>"
	}
		
	function logout(){
		window.location.href = "<html:rewrite action="Logout.do"/>"; 
	}
</script>
<div align="center">
	<table border="0" cellpadding="0" cellspacing="0" width="100%">
		<tbody>
			<tr align="center">
				<td><c:choose>
						<c:when test="${requestScope.loadableTerminal}">
							<fmt:message key="terminal.common.logout.deliverynoteprinted" />
							<br>
							<br>
						</c:when>
						<c:otherwise>
							<fmt:message
								key="terminal.common.logout.withoutdeliverynoteprinted" />
							<br>
							<br>
						</c:otherwise>
					</c:choose> <input onclick="javascript:goBack()" style="width: 250px"
					type="button" class="ViewButtonStyleSmall"
					value=" <fmt:message key="option.no"/> "> <input
					onclick="javascript:logout()" style="width: 250px" type="button"
					class="ViewButtonStyleSmall"
					value=" <fmt:message key="option.yes"/> "></td>
			</tr>
			<c:if test="${not empty requestScope.orderNumber and 
					not empty requestScope.orderValidationCode}">
			<tr height="40px" align="center">
				<td><br>
					<fmt:message key="pickup.column.orderno"/>:
					<c:out value="${requestScope.orderNumber}"/>
				</td>
			</tr>
			<tr height="40px" align="center">
				<td><fmt:message key="terminal.column.deliverypin"/>:
					<c:out value="${requestScope.orderValidationCode}"/>
				</td>
			</tr>
			</c:if>	
			<tr height="40px" align="center">
				<td id="counterTd">(Automatic logout in 10 seconds)</td>
			</tr>
		</tbody>
	</table>
</div>
<script language="javascript">

var c=10;
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
	logout();
	return false;
}

window.onload=doTimer;
</script>