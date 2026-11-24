<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: PickupBilltoSelector.jsp,v $ --%>
<%@ page session="true"%>
<%@ page import="com.crh.logon.terminal.web.actions.pickup.PickupAction" %>
<%@ page import="com.crh.logon.terminal.web.forms.pickup.PickupForm"%>
<%@ include file="../includeTLDs.jsp"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<link rel="stylesheet" type="text/css" href="<crh-common:url value="/css/crh_touchscreen_style.css"/>">

<c:set var="FORM_NAME"><%=PickupForm.FORM_NAME %></c:set>
<c:set var="ACTION_URL"><%=PickupAction.ACTION_URL %></c:set>
<% pageContext.setAttribute("FORM", session.getAttribute(PickupForm.FORM_NAME));%>
<title><fmt:message key="pickup.create.steps.selectbillto"/></title>
<script type="text/javascript">

var actions = {
		SELECT_BILLTO: '<%=PickupAction.ACTION_SELECT_BILLTO%>',
		CANCEL_ADD_ITEM: '<%=PickupAction.ACTION_CANCEL_ADDITEM%>',
		CANCEL_CREATE: '<%=PickupAction.ACTION_CANCEL_CREATE%>'
	};

function doOnSelect(){
	enableElement("continueButton");
}
</script>

<script type="text/javascript" src="<crh-common:url value='/js/pickup/Pickup.js'/>"></script>
	
<crh-common:authorize function="<%= Authorization.PICKUP_ORDER_TERMINAL_W %>" function2="<%=Authorization.PICKUP_ORDER_TERMINAL_CREATE_NEW_ORDER%>">
	<script type="text/javascript" src="<crh-common:url value='/js/pickup/PickupCommon.js'/>"></script>
</crh-common:authorize>

<html:form action="${ACTION_URL }" styleId="${FORM_NAME}">
	<input type="hidden" name="inputAction" id="inputAction" value="" />
	<table class="centered"  cellpadding="0" cellspacing="0" border="0" width="100%"
		style="padding: 0px; margin: 0px;">
	<tr style="height:20%">
			<td>
				<b><fmt:message key="pickup.create.steps.selectbillto"/></b>
				</td>
	</tr>
	<tr style="height:60%">
				<td>
				<c:if test="${!empty FORM.custorderBean.billtoList}">
					<select onclick='javascript:doOnSelect();' size="8"  name = "inputBilltoId" id="inputBilltoId"> 				
					<c:forEach var="entry" items="${FORM.custorderBean.billtoList}">											
						<option value="<c:out value="${entry.billtoSapBp.sapBpId}"/>"><c:out value="${entry.billtoSapBp.name1}"/>&nbsp;<c:out value="${entry.billtoSapBp.name2}"/></option>
					</c:forEach>
					</select>
				</c:if>
				<c:if test="${empty FORM.custorderBean.billtoList}">
				<fmt:message key="pickup.create.nobilltosavailable"/>
				</c:if>
		</td>
	</tr>
	<tr style="height:20%">
		<td>	
		<c:if test="${terminal_PickupForm.createMode }">
		<input onclick='javascript:cancelCreateOrder()' type="button" class="ViewButtonStyle"  value=" <fmt:message key="pickup.button.cancelcreateorder"/> ">
	</c:if>
	<c:if test="${!terminal_PickupForm.createMode }">
		<input onclick='javascript:cancelEditOrder()' type="button" class="ViewButtonStyle"  value=" <fmt:message key="pickup.button.canceleditorder"/> ">
	</c:if>	
	<input id ="continueButton" disabled onclick='javascript:selectBillto()' type="button" class="ViewButtonStyle"  value=" <fmt:message key="pickup.create.steps.button.continue"/> ">
			</td>
		</tr>
	</table>
</html:form>
