<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: BagTerminalNewTruckSelector.jsp,v $ --%>
<%@ page session="true"%>
<%@ page import="com.crh.logon.terminal.web.actions.bagterminal.BagTerminalAction" %>
<%@ page import="com.crh.logon.terminal.web.forms.bagterminal.BagTerminalForm"%>
<%@ include file="../includeTLDs.jsp"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<link rel="stylesheet" type="text/css" href="<crh-common:url value="/css/crh_touchscreen_style.css"/>">

<crh-common:authorize function="<%= Authorization.BAG_ORDER_TERMINAL_CHANGE_TRUCK %>">
	<script type="text/javascript" src="<crh-common:url value='/js/bagterminal/BagTerminalChangeTruck.js'/>"></script>
</crh-common:authorize>

<c:set var="FORM_NAME"><%=BagTerminalForm.FORM_NAME %></c:set>
<c:set var="ACTION_URL"><%=BagTerminalAction.ACTION_URL %></c:set>
<% pageContext.setAttribute("FORM", session.getAttribute(BagTerminalForm.FORM_NAME));%>
<title><fmt:message key="pickup.create.steps.selectbillto"/></title>
<script type="text/javascript">

var actions = {
		CANCEL_CHANGE_TRUCK:'<%=BagTerminalAction.ACTION_CANCEL_CHANGE_TRUCK%>',
		CHANGE_TRUCK:'<%=BagTerminalAction.ACTION_CHANGE_TRUCK%>'
	};

function doOnSelect(){
	enableElement("okButton");
}
</script>

<script type="text/javascript" src="<crh-common:url value='/js/pickup/Pickup.js'/>"></script>
	
<crh-common:authorize function="<%= Authorization.BAG_ORDER_TERMINAL_CHANGE_TRUCK %>">
	<script type="text/javascript" src="<crh-common:url value='/js/pickup/PickupChangeTruck.js'/>"></script>
</crh-common:authorize>

<html:form action="${ACTION_URL }" styleId="${FORM_NAME}">
	<input type="hidden" name="inputAction" id="inputAction" value="" />
	<table class="centered"  cellpadding="0" cellspacing="0" border="0" width="100%"
		style="padding: 0px; margin: 0px;">
		<tr class="steprow">
			<td>
				<fmt:message key="bagterminal.popup.changetruck.title"/>
				</td>
				</tr>
				<tr class="textrow">
				<td>
					<select onchange="javascript:doOnSelect();" size="8" name = "inputNewTruckId" id="inputNewTruckId"> 				
					<c:forEach var="entry" items="${FORM.freeTruckList}">											
						<option value="<c:out value="${entry.truckId}"/>"><c:out value="${entry.truckName}"/>&nbsp;(<c:out value="${entry.truckPlateNo}"/>)</option>
					</c:forEach>
					</select>
					 			
		</td>
	</tr>
	<tr class="buttonrow">
		<td>
	<input onclick='javascript:cancelSelectTruck()' type="button" class="ViewButtonStyle"  value=" <fmt:message key="buttons.cancel"/> ">			
	<input id="okButton" disabled onclick='javascript:changeTruck()' type="button" class="ViewButtonStyle"  value=" <fmt:message key="buttons.ok"/> ">
			</td>
		</tr>
	</table>
</html:form>
