<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: PickupDeliveryNote.jsp,v $ --%>
<%@ page session="true"%>
<%@ page import="com.crh.logon.terminal.web.actions.pickup.PickupAction" %>
<%@ page import="com.crh.logon.terminal.web.forms.pickup.PickupForm"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@page import="com.crh.logon.persistence.entities.common.OrderEntity"%>
<%--<%@page import="java.util.List"%>--%>
<%--<%@page import="java.util.ArrayList"%>--%>


<%@ include file="../includeTLDs.jsp"%>
    <link rel="stylesheet" type="text/css" href="<crh-common:url value="/css/crh_touchscreen_style.css"/>">
<c:set var="FORM_NAME"><%=PickupForm.FORM_NAME %></c:set>
<c:set var="ACTION_URL"><%=PickupAction.ACTION_URL %></c:set>
<%--
List result = new ArrayList();
for (char ch = 'a'; ch <= 'z'; ch++){
    result.add(Character.valueOf(ch).toString());
}
for (char ch = '0'; ch <= '9'; ch++){
    result.add(Character.valueOf(ch).toString());
}
pageContext.setAttribute("alphabet", result);
--%>

<title><fmt:message key="pickup.column.deliverynote"/></title>
<script type="text/javascript">
var actions = {
		SET_DELIVERY_NOTE: '<%=PickupAction.ACTION_SET_DELIVERY_NOTE%>',
		CANCEL_DELIVERY_NOTE: '<%=PickupAction.ACTION_CANCEL_DELIVERY_NOTE%>'
	};

var upperCase = false;

function selectChar(value){
	if (document.getElementById("inputDeliveryNote").value.length==<%=OrderEntity.DBSIZE_NOTE_DELIVERY + ""%>){
		return false;
	}
	
	document.getElementById("continueButton").disabled=false;
	var current = document.getElementById("inputDeliveryNote").value;
	if (!upperCase){		
		document.getElementById("inputDeliveryNote").value = current + value;
	}
	else if (value != undefined){
		document.getElementById("inputDeliveryNote").value = current + value.toUpperCase();
	}
}

function backSp(){
	var current = document.getElementById("inputDeliveryNote").value;
	if (current.length > 1) {
		current = current.substring(0,current.length-1);
		document.getElementById("inputDeliveryNote").value = current;
	}
	else {
		current = "";
		document.getElementById("inputDeliveryNote").value = current;
	}
}

function setDeliveryNote(){
	document.getElementById("inputAction").value=actions.SET_DELIVERY_NOTE;
	document.forms[0].submit();
	disableAll();
}
function cancelDeliveryNote(){
	document.getElementById("inputAction").value=actions.CANCEL_DELIVERY_NOTE;
	document.forms[0].submit();
	disableAll();
}

function deleteText(){
	document.getElementById("inputDeliveryNote").value = "";	
}

function setUpperCase(el){
	if (!upperCase) {
		upperCase = true;
		el.className="ViewButtonStyleDisabled";
	}
	else{
		upperCase = false;
		el.className="ViewButtonStyle";
	}
}
</script>

<script type="text/javascript" src="<crh-common:url value='/js/pickup/Pickup.js'/>"></script>
	
<crh-common:authorize function="<%= Authorization.PICKUP_ORDER_TERMINAL_W %>" function2="<%=Authorization.PICKUP_ORDER_TERMINAL_CREATE_NEW_ORDER%>">
	<script type="text/javascript" src="<crh-common:url value='/js/pickup/PickupCommon.js'/>"></script>
</crh-common:authorize>

<html:form action="${ACTION_URL }" styleId="${FORM_NAME}">
<input type="hidden" name="inputAction" id="inputAction" value=""/>

		<div align="center">
	<table class="centered"  cellpadding="0" cellspacing="0" border="0" width="100%" style="padding: 0px; margin: 0px;">	

		<table class="centered"  cellpadding="0" cellspacing="0" border="0" height="100%" width="100%"
		style="padding: 0px; margin: 0px;">
		<tr style="height:20%">
		<td colspan="2">
		<table>
		<tr>
		<td>
				<b>				<fmt:message key="pickup.column.deliverynote"/></b>: 
		</td>
<td>
<textarea id="inputDeliveryNote" name="inputDeliveryNote" rows="3" cols="1" style="font-size:36px;overflow:hidden;width: 1000px;"><c:out value="${terminal_PickupForm.custorderBean.custorderEntity.noteDelivery}" /></textarea>
</td>
</tr>	
</table>
</td>
	</tr>
	<tr style="height:60%">
<td>
	<table cellpadding="0" cellspacing="0" border="0" style="padding: 0px; margin: 0px;">		
		<%--
					<c:forEach var="entry" items="${alphabet}">
			<c:if test="${itemcounter mod 10 eq 0}">					
			<tr>
			</c:if>
			<td>
					<c:set var="itemcounter"><c:out value="${itemcounter+1 }"/></c:set>
						<input onclick='javascript:selectChar("<c:out value="${entry}"/>")' style="width:100px" type="button" class="ViewButtonStyle"  value="<c:out value="${entry}"/>"></td>
			<c:if test="${itemcounter mod 10 eq 0}">
			</tr>
			</c:if>
					</c:forEach>--%>
	<tr>		
			<td><input onclick='javascript:selectChar("a")' style="width:100px" type="button" class="ViewButtonStyle"  value="a"></td>
			<td><input onclick='javascript:selectChar("b")' style="width:100px" type="button" class="ViewButtonStyle"  value="b"></td>
			<td><input onclick='javascript:selectChar("c")' style="width:100px" type="button" class="ViewButtonStyle"  value="c"></td>
			<td><input onclick='javascript:selectChar("d")' style="width:100px" type="button" class="ViewButtonStyle"  value="d"></td>
			<td><input onclick='javascript:selectChar("e")' style="width:100px" type="button" class="ViewButtonStyle"  value="e"></td>
			<td colspan = "2"><input onclick='javascript:backSp()' style="width:218px" type="button" class="ViewButtonStyle"  value="<fmt:message key="pickup.button.backspace"/>"></td>
	</tr>			
	<tr>		
			<td><input onclick='javascript:selectChar("f")' style="width:100px" type="button" class="ViewButtonStyle"  value="f"></td>
			<td><input onclick='javascript:selectChar("g")' style="width:100px" type="button" class="ViewButtonStyle"  value="g"></td>
			<td><input onclick='javascript:selectChar("h")' style="width:100px" type="button" class="ViewButtonStyle"  value="h"></td>
			<td><input onclick='javascript:selectChar("i")' style="width:100px" type="button" class="ViewButtonStyle"  value="i"></td>
			<td><input onclick='javascript:selectChar("j")' style="width:100px" type="button" class="ViewButtonStyle"  value="j"></td>
			<td><input onclick='javascript:selectChar("k")' style="width:100px" type="button" class="ViewButtonStyle"  value="k"></td>
			<td><input onclick='javascript:selectChar("l")' style="width:100px" type="button" class="ViewButtonStyle"  value="l"></td>
		</tr>	
		<tr>
			<td><input onclick='javascript:selectChar("m")' style="width:100px" type="button" class="ViewButtonStyle"  value="m"></td>
			<td><input onclick='javascript:selectChar("n")' style="width:100px" type="button" class="ViewButtonStyle"  value="n"></td>
			<td><input onclick='javascript:selectChar("o")' style="width:100px" type="button" class="ViewButtonStyle"  value="o"></td>
			<td><input onclick='javascript:selectChar("p")' style="width:100px" type="button" class="ViewButtonStyle"  value="p"></td>
			<td><input onclick='javascript:selectChar("q")' style="width:100px" type="button" class="ViewButtonStyle"  value="q"></td>
			<td><input onclick='javascript:selectChar("r")' style="width:100px" type="button" class="ViewButtonStyle"  value="r"></td>
			<td><input onclick='javascript:selectChar("s")' style="width:100px" type="button" class="ViewButtonStyle"  value="s"></td>
		</tr>
		<tr>
			<td><input onclick='javascript:selectChar("t")' style="width:100px" type="button" class="ViewButtonStyle"  value="t"></td>
			<td><input onclick='javascript:selectChar("u")' style="width:100px" type="button" class="ViewButtonStyle"  value="u"></td>
			<td><input onclick='javascript:selectChar("v")' style="width:100px" type="button" class="ViewButtonStyle"  value="v"></td>
			<td><input onclick='javascript:selectChar("w")' style="width:100px" type="button" class="ViewButtonStyle"  value="w"></td>
			<td><input onclick='javascript:selectChar("x")' style="width:100px" type="button" class="ViewButtonStyle"  value="x"></td>
			<td><input onclick='javascript:selectChar("y")' style="width:100px" type="button" class="ViewButtonStyle"  value="y"></td>
			<td><input onclick='javascript:selectChar("z")' style="width:100px" type="button" class="ViewButtonStyle"  value="z"></td>
			</tr>
		<tr>
			<td colspan = "5"><input onclick='javascript:selectChar(" ")' style="width:572px" type="button" class="ViewButtonStyle"  value="<fmt:message key="pickup.button.space"/>"></td>
			<td colspan = "2"><input onclick='javascript:setUpperCase(this)' style="width:218px" type="button" class="ViewButtonStyle"  value="Shift"></td>		</tr>
	</table>
	</td>
	<td  style="vertical-align: top;">
	<table cellpadding="0" cellspacing="0" border="0" style="padding: 0px; margin: 0px;">		
	<tr>		
			<td><input onclick='javascript:selectChar("1")' style="width:100px" type="button" class="ViewButtonStyle"  value="1"></td>
			<td><input onclick='javascript:selectChar("2")' style="width:100px" type="button" class="ViewButtonStyle"  value="2"></td>
			<td><input onclick='javascript:selectChar("3")' style="width:100px" type="button" class="ViewButtonStyle"  value="3"></td>			
	</tr>			
	<tr>		
			<td><input onclick='javascript:selectChar("4")' style="width:100px" type="button" class="ViewButtonStyle"  value="4"></td>
			<td><input onclick='javascript:selectChar("5")' style="width:100px" type="button" class="ViewButtonStyle"  value="5"></td>
			<td><input onclick='javascript:selectChar("6")' style="width:100px" type="button" class="ViewButtonStyle"  value="6"></td>
		</tr>	
		<tr>
			<td><input onclick='javascript:selectChar("7")' style="width:100px" type="button" class="ViewButtonStyle"  value="7"></td>
			<td><input onclick='javascript:selectChar("8")' style="width:100px" type="button" class="ViewButtonStyle"  value="8"></td>
			<td><input onclick='javascript:selectChar("9")' style="width:100px" type="button" class="ViewButtonStyle"  value="9"></td>
		</tr>
		<tr>
			<td><input onclick='javascript:selectChar(":")' style="width:100px" type="button" class="ViewButtonStyle"  value=":"></td>			
			<td><input onclick='javascript:selectChar("0")' style="width:100px" type="button" class="ViewButtonStyle"  value="0"></td>
			<td><input onclick='javascript:selectChar(".")' style="width:100px" type="button" class="ViewButtonStyle"  value="."></td>
		</tr>
		<tr>
			<td><input onclick='javascript:selectChar(",")' style="width:100px" type="button" class="ViewButtonStyle"  value=","></td>
			<td colspan="2"><input onclick='javascript:deleteText()' style="width:218px" type="button" class="ViewButtonStyle"  value="C"></td>
		</tr>		
	</table>	
	
	</td>
</tr>
<tr style="height:20%">
<td colspan="2">
<table  border="0" style="padding: 0px; margin: 0px;text-align:center;">
<tr  class="buttonrow">
<td >
	<input onclick='javascript:cancelDeliveryNote()' type="button" class="ViewButtonStyle"  value=" <fmt:message key="pickup.button.canceldelnote"/> ">&nbsp;&nbsp;&nbsp;
	<input id="continueButton" onclick='javascript:setDeliveryNote()' type="button" class="ViewButtonStyle"  value=" <fmt:message key="buttons.ok"/> ">	
			</td>
		</tr>
	</table>
		</td>
	</tr>
	</table>
	
			          </div>
</html:form>
