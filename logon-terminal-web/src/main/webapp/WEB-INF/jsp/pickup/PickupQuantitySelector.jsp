<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: PickupQuantitySelector.jsp,v $ --%>
<%@ page session="true"%>
<%@ page import="com.crh.logon.terminal.web.actions.pickup.PickupAction" %>
<%@ page import="com.crh.logon.terminal.web.forms.pickup.PickupForm"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@ taglib uri="http://com.crh.logon/tags/terminal-taglib.tld" prefix="crh-terminal" %>

<%@ include file="../includeTLDs.jsp"%>
    
<%@page import="java.math.BigDecimal"%><link rel="stylesheet" type="text/css" href="<crh-common:url value="/css/crh_touchscreen_style.css"/>">
<c:set var="FORM_NAME"><%=PickupForm.FORM_NAME %></c:set>
<c:set var="ACTION_URL"><%=PickupAction.ACTION_URL %></c:set>

<title><fmt:message key="pickup.create.steps.selectquantity"/></title>
<% pageContext.setAttribute("FORM", session.getAttribute(PickupForm.FORM_NAME));%>
<script type="text/javascript">
var actions = {
		SELECT_QUANTITY: '<%=PickupAction.ACTION_SELECT_QUANTITY%>',
		CANCEL_ADDED_ITEM: '<%=PickupAction.ACTION_CANCEL_ADDED_ITEM%>',
		CANCEL_CREATE: '<%=PickupAction.ACTION_CANCEL_CREATE%>'
	};

// input value must be less than the limit
var inputLimit = 1000;

// max number of decimal places
var decimalPlaceLimit = 3;

function selectQuantity(){

	var quantity = document.getElementById("quantity").value;
	document.getElementById('inputQuantity').value=quantity;
	document.getElementById("inputAction").value=actions.SELECT_QUANTITY;
	document.forms[0].submit();
}


function selectNumber(value){
	// pre-edit checks
	var oldQuantity = document.getElementById("quantity").value;

	// handle decimal point, disable continue
	if (value == '.' && oldQuantity.indexOf('.') == -1) {
		document.getElementById("quantity").value = oldQuantity.toString() + value.toString();
		document.getElementById("continueButton").disabled=true;
		document.getElementById("precision").value++;
		return true;
	}
	
	// valid input
	if (isNaN(value)) {
		return false;
	}

	// check decimal places
	if (document.getElementById("precision").value > decimalPlaceLimit) {
		return false;
	}

	// append and check
	var newQuantity = parseFloat(oldQuantity.toString() + value.toString());
	if (newQuantity >= inputLimit) {
		return false;
	}

	document.getElementById("quantity").value = newQuantity.toFixed(document.getElementById("precision").value);
	
	// increase precision by one if there is a decimal point
	if (document.getElementById("precision").value > 0) {
		document.getElementById("precision").value++;
	}
	
	enableIfNotZero();
}
function enableIfNotZero(){	
	var quantity = document.getElementById("quantity").value;
	if (quantity != 0){
		document.getElementById("continueButton").disabled=false;
	}
}

function deleteNumbers(){
	document.getElementById("continueButton").disabled=true;
	document.getElementById("quantity").value = 0;
	document.getElementById("precision").value = 0;
}

</script>

<script type="text/javascript" src="<crh-common:url value='/js/pickup/Pickup.js'/>"></script>
	
<crh-common:authorize function="<%= Authorization.PICKUP_ORDER_TERMINAL_W %>" function2="<%=Authorization.PICKUP_ORDER_TERMINAL_CREATE_NEW_ORDER%>">
	<script type="text/javascript" src="<crh-common:url value='/js/pickup/PickupCommon.js'/>"></script>
</crh-common:authorize>

<html:form action="${ACTION_URL }" styleId="${FORM_NAME}">
<input type="hidden" name="inputAction" id="inputAction" value=""/>
<input type="hidden" name="inputQuantity" id="inputQuantity" value=""/>
<input type="hidden" name="selectedCustorderItemBeanIndex" id="selectedCustorderItemBeanIndex" value="<c:out value="${terminal_PickupForm.selectedCustorderItemBeanIndex}"/>"/>


		<div align="center">
	<table class="centered"  cellpadding="0" cellspacing="0" border="0" width="100%" style="padding: 0px; margin: 0px;">	
		<tr style="height:20%">
		<td>
		<table><tr>
		<td colspan="2">
				<b><fmt:message key="pickup.create.steps.selectquantity"/></b> 
		</td>
		</tr>
		<tr>
		<td>
				<c:set var="curruomCd"><c:out value="${terminal_PickupForm.custorderBean.custorderItemBeanList[terminal_PickupForm.selectedCustorderItemBeanIndex].itemEntity.orderedUom.uomCd}"/></c:set>
				<fmt:message key="pickup.create.orderednumber"/>&nbsp;[<crh-terminal:uomName uomCd="<%=(String)pageContext.getAttribute("curruomCd") %>"/>]:
		</td>
<td>
	<input style="font-size:36px;width:150px" disabled id="quantity"/>
	<input type="hidden" name="<c:out value="precision"/>" id="precision" value=""/>
</td>
</tr>	
</table>
</td>
	</tr>
		<tr style="height:60%">
		<td>
	<table cellpadding="0" cellspacing="0" border="0" style="padding: 0px; margin: 0px;">		
		<tr>
		<td>			

<input onclick='javascript:selectNumber(1)' style="width:100px" type="button" class="ViewButtonStyle"  value="1">
</td>
<td>
<input onclick='javascript:selectNumber(2)' style="width:100px" type="button" class="ViewButtonStyle"  value="2">
</td>
<td>
<input onclick='javascript:selectNumber(3)' style="width:100px" type="button" class="ViewButtonStyle"  value="3">
</td>

</tr>
<tr>
<td>
<input onclick='javascript:selectNumber(4)' style="width:100px" type="button" class="ViewButtonStyle"  value="4">
</td>
<td>
<input onclick='javascript:selectNumber(5)' style="width:100px" type="button" class="ViewButtonStyle"  value="5">
</td>
<td>
<input onclick='javascript:selectNumber(6)' style="width:100px" type="button" class="ViewButtonStyle"  value="6">
</td>
</tr>
<tr>
<td>
<input onclick='javascript:selectNumber(7)' style="width:100px" type="button" class="ViewButtonStyle"  value="7">
</td>
<td>
<input onclick='javascript:selectNumber(8)' style="width:100px" type="button" class="ViewButtonStyle"  value="8">
</td>
<td>
<input onclick='javascript:selectNumber(9)' style="width:100px" type="button" class="ViewButtonStyle"  value="9">
</td>
</tr>
<tr>
<td>
<input onclick='javascript:selectNumber(".")' style="width:100px" type="button" class="ViewButtonStyle"  value=".">
</td>
<td>
<input onclick='javascript:selectNumber(0)' style="width:100px" type="button" class="ViewButtonStyle"  value="0">
</td>
<td>
<input onclick='javascript:deleteNumbers()' style="width:100px" type="button" class="ViewButtonStyle"  value="C">
</td>
</tr>
</table>
</td>
</tr>
<tr style="height:20%">
<td>
<table  border="0" style="padding: 0px; margin: 0px;text-align:center;">
<tr>
<td>
<c:choose>
	<c:when test="${empty FORM.custorderBean.custorderItemBeanList }">
	<c:if test="${empty terminal_PickupForm.custorderBean.custorderEntity.custorderId }">
		<input onclick='javascript:cancelCreateOrder()' type="button" class="ViewButtonStyle"  value=" <fmt:message key="pickup.button.cancelcreateorder"/> ">
	</c:if>
	<c:if test="${!empty terminal_PickupForm.custorderBean.custorderEntity.custorderId }">
		<input onclick='javascript:cancelEditOrder()' type="button" class="ViewButtonStyle"  value=" <fmt:message key="pickup.button.canceleditorder"/> ">
	</c:if>	
	</c:when>
	<c:otherwise>
		<input onclick='javascript:cancelAddedItem()' type="button" class="ViewButtonStyle"  value=" <fmt:message key="pickup.button.canceladditem"/> ">	
	</c:otherwise>
</c:choose>
	<input disabled id="continueButton" onclick='javascript:selectQuantity()' type="button" class="ViewButtonStyle"  value=" <fmt:message key="pickup.create.steps.button.continue"/> ">	
			</td>
		</tr>
	</table>
	</td>
	</tr>
	</table>
					                              </div>
</html:form>
