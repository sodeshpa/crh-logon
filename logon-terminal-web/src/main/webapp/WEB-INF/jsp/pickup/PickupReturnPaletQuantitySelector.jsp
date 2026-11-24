<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: PickupReturnPaletQuantitySelector.jsp,v $ --%>
<%@ page session="true"%>
<%@ page import="com.crh.logon.terminal.web.actions.pickup.PickupAction" %>
<%@ page import="com.crh.logon.terminal.web.forms.pickup.PickupForm"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>

<%@ include file="../includeTLDs.jsp"%>
    <link rel="stylesheet" type="text/css" href="<crh-common:url value="/css/crh_touchscreen_style.css"/>">
<c:set var="FORM_NAME"><%=PickupForm.FORM_NAME %></c:set>
<c:set var="ACTION_URL"><%=PickupAction.ACTION_URL %></c:set>


<title><fmt:message key="pickup.create.steps.selectquantity"/></title>
<% pageContext.setAttribute("FORM", session.getAttribute(PickupForm.FORM_NAME));%>
<script type="text/javascript">
var actions = {
		SELECT_RETURN_PALET_QUANTITY: '<%=PickupAction.ACTION_SELECT_RETURN_PALET_QUANTITY%>',
		CANCEL_DEFINE_RETURN: '<%=PickupAction.ACTION_CANCEL_DEFINE_RETURN_PALLET%>'
	};

function selectReturnPalletQuantity(){

	var quantity = getQuantity();
	document.getElementById('inputReturnPalets').value=quantity;
	document.getElementById("inputAction").value=actions.SELECT_RETURN_PALET_QUANTITY;
	document.forms[0].submit();
	disableAll();
}

function getQuantity(){
	var number1 = document.getElementById("number1").value;
	var number2 = document.getElementById("number2").value;
	var number3 = document.getElementById("number3").value;
	var quantity = 0;

	if (number3 && !isNaN(number3)){
		quantity = 100 * number1;
		quantity = parseFloat(quantity) + 10 * parseFloat(number2);
		quantity = parseFloat(quantity) + parseFloat(number3);		
	}
	else if (number2 && !isNaN(number2)){
		quantity = 10 * number1;
		quantity = parseFloat(quantity) + parseFloat(number2);		
	}
	else{
		quantity = number1;
	}
	return quantity;
}

function selectNumber(value){
	var current100 = document.getElementById("number3").value;
	if (current100 && !isNaN(current100)){
		//nothing to be done, only delete is possible
		return false;
	}
	var current10 = document.getElementById("number2").value;
	var current = document.getElementById("number1").value;
	if (!current || isNaN(current)){		
		document.getElementById("number1").value=value;
	}
	else if (!current10 || isNaN(current10)){		
		document.getElementById("number2").value=value;
	}	
	else{
		document.getElementById("number3").value=value;
	}
	document.getElementById("quantity").value=getQuantity();
}

function deleteNumbers(){
	document.getElementById("continueButton").disabled=true;
	document.getElementById("number1").value="";
	document.getElementById("number2").value="";
	document.getElementById("number3").value="";
	document.getElementById("quantity").value=getQuantity();
	
}
</script>

<script type="text/javascript" src="<crh-common:url value='/js/pickup/Pickup.js'/>"></script>
	
<crh-common:authorize function="<%= Authorization.PICKUP_ORDER_TERMINAL_W %>" function2="<%=Authorization.PICKUP_ORDER_TERMINAL_CREATE_NEW_ORDER%>">
	<script type="text/javascript" src="<crh-common:url value='/js/pickup/PickupCommon.js'/>"></script>
</crh-common:authorize>

<html:form action="${ACTION_URL }" styleId="${FORM_NAME}">
<input type="hidden" name="inputAction" id="inputAction" value=""/>
<input type="hidden" name="inputReturnPalets" id="inputReturnPalets" value=""/>

<input type="hidden" name="<c:out value="number"/>" id="number" value=""/>
<input type="hidden" name="<c:out value="10number"/>" id="10number" value=""/>
		<div align="center">
	<table class="centered"  cellpadding="0" cellspacing="0" border="0" width="100%" style="padding: 0px; margin: 0px;">	

		<table class="centered"  cellpadding="0" cellspacing="0" border="0" height="100%" width="100%"
		style="padding: 0px; margin: 0px;">
		<tr style="height:20%">
		<td>
		<table>
		<tr>
		<td>
				<b>				<fmt:message key="pickup.create.steps.selectreturnpaletquantity"/></b>: 
		</td>
<td>
<input type="hidden" name="<c:out value="number1"/>" id="number1" value=""/>
<input type="hidden" name="<c:out value="number2"/>" id="number2" value=""/>
<input type="hidden" name="<c:out value="number3"/>" id="number3" value=""/>
	<input style="font-size:36px;width:75px" disabled id="quantity"/>
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
<td colspan="2">
<input onclick='javascript:selectNumber(0)' style="width:218px" type="button" class="ViewButtonStyle"  value="0">
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
<tr  class="buttonrow">
<td>
	<input onclick='javascript:cancelDefineReturn()' type="button" class="ViewButtonStyle"  value=" <fmt:message key="buttons.cancel"/> ">
	<input id="continueButton" onclick='javascript:selectReturnPalletQuantity()' type="button" class="ViewButtonStyle"  value=" <fmt:message key="buttons.ok"/> ">	
			</td>
		</tr>
	</table>
	
		</td>
	</tr>
	</table>
					                              </div>
					                              
</html:form>
