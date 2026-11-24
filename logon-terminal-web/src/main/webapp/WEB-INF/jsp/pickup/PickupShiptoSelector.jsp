<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: PickupShiptoSelector.jsp,v $ --%>
<%@ page session="true"%>
<%@ page import="com.crh.logon.terminal.web.actions.pickup.PickupAction" %>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@ page import="com.crh.logon.terminal.web.forms.pickup.PickupForm"%>

<%@ include file="../includeTLDs.jsp"%>
<title><fmt:message key="pickup.create.steps.selectshipto"/></title>
<link rel="stylesheet" type="text/css" href="<crh-common:url value="/css/crh_touchscreen_style.css"/>">
<c:set var="FORM_NAME"><%=PickupForm.FORM_NAME %></c:set>
<c:set var="ACTION_URL"><%=PickupAction.ACTION_URL %></c:set>
<% pageContext.setAttribute("FORM", session.getAttribute(PickupForm.FORM_NAME));%>

<script type="text/javascript">
var actions = {
		SELECT_SHIPTO: '<%=PickupAction.ACTION_SELECT_SHIPTO%>',
		CANCEL_CREATE: '<%=PickupAction.ACTION_CANCEL_CREATE%>'
	};

function doOnSelect(){
	enableElement("continueButton");
}
</script>
<crh-common:authorize
	function="<%=Authorization.PICKUP_ORDER_TERMINAL_W %>" 
	function2="<%=Authorization.PICKUP_ORDER_TERMINAL_CREATE_NEW_ORDER%>">
	<script type="text/javascript" src="<crh-common:url value='/js/pickup/PickupCommon.js'/>"></script>
</crh-common:authorize>

<script type="text/javascript" src="<crh-common:url value='/js/pickup/Pickup.js'/>"></script>
	
<crh-common:authorize function="<%= Authorization.PICKUP_ORDER_TERMINAL_CREATE_NEW_ORDER %>">
	<script type="text/javascript" src="<crh-common:url value='/js/pickup/PickupCreate.js'/>"></script>
</crh-common:authorize>
<crh-common:authorize function="<%= Authorization.PICKUP_ORDER_TERMINAL_W %>">
	<script type="text/javascript" src="<crh-common:url value='/js/pickup/PickupEdit.js'/>"></script>
</crh-common:authorize>

<html:form action="${ACTION_URL }" styleId="${FORM_NAME}">
	<input type="hidden" name="inputAction" id="inputAction" value="" />
	<table height="100%" class="centered"  cellpadding="0" cellspacing="0" border="0" width="100%"
		style="padding: 0px; margin: 0px;">
		<tr style="height:20%">
		<td>
				<b><fmt:message key="pickup.create.steps.selectshipto"/></b>
		</td>
		</tr>
		<tr style="height:60%">
			<td>
		<c:if test="${!empty FORM.shiptoList }">
			<html:select onchange='javascript:doOnSelect()' size="8" property="shiptoSoldtoCompositeId">
				<html:optionsCollection property="shiptoList" label="shiptoName" value="compositeId" />
			</html:select>
		</c:if>
		</td>
		</tr style="height:20%">
		<tr >
			<td>
	<input onclick='javascript:cancelCreateOrder()' type="button" class="ViewButtonStyle"  value=" <fmt:message key="pickup.button.cancelcreateorder"/> ">
	<input id="continueButton" disabled onclick='javascript:selectShipto()' type="button" class="ViewButtonStyle"  value=" <fmt:message key="pickup.create.steps.button.continue"/> ">
			</td>
		</tr>
	</table>
</html:form>
