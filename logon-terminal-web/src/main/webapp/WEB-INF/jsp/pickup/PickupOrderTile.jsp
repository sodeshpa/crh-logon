<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: PickupOrderTile.jsp,v $ --%>
<%@ page session="true"%>
<%@ page import="com.crh.logon.terminal.web.actions.pickup.PickupAction" %>
<%@ page import="com.crh.logon.terminal.web.forms.pickup.PickupForm"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@ taglib uri="http://com.crh.logon/tags/terminal-taglib.tld" prefix="crh-terminal" %>
<%@ include file="../includeTLDs.jsp"%>
<%@ page import="java.text.DecimalFormat" %>
<c:set var="context"><%=request.getContextPath() %></c:set>

<c:if test="false">
	<%-- for preview in MyEclipse --%>
</c:if>
	<link rel="stylesheet" type="text/css" href="<crh-common:url value="/css/crh_touchscreen_style.css"/>">

<c:set var="FORM_NAME"><%=PickupForm.FORM_NAME %></c:set>
<c:set var="ACTION_URL"><%=PickupAction.ACTION_URL %></c:set>

<c:set var="MATERIAL_PACKAGE_BAG"><%=Constant.MATERIAL_PACKAGE_BAG %></c:set>

<crh-common:authorize function="<%= Authorization.PICKUP_ORDER_TERMINAL_W %>" function2="<%=Authorization.PICKUP_ORDER_TERMINAL_CREATE_NEW_ORDER%>">
	<script type="text/javascript" src="<crh-common:url value='/js/pickup/PickupCommon.js'/>"></script>
</crh-common:authorize>
	
<script type="text/javascript">
var actions = {
		ADD_ITEM_ENTRY: '<%=PickupAction.ACTION_ADD_ITEM%>',
		CANCEL_CREATE: '<%=PickupAction.ACTION_CANCEL_CREATE%>',
		REMOVE_ITEM: '<%=PickupAction.ACTION_REMOVE_ITEM%>',
		SAVE_NEW: '<%=PickupAction.ACTION_SAVE_NEW_PICKUP_AND_PRINT%>',
		CANCEL_EDIT: '<%=PickupAction.ACTION_CANCEL_EDIT%>',
		SAVE: '<%=PickupAction.ACTION_SAVE_PICKUP_AND_PRINT%>',
		CHANGE_RETURN_PALLET_QUANTITY: '<%=PickupAction.ACTION_CHANGE_RETURN_PALET_QUANTITY%>',
		CHANGE_PALET_QUANTITY: '<%=PickupAction.ACTION_CHANGE_PALET_QUANTITY%>',
		DELIVERY_NOTE: '<%=PickupAction.ACTION_DELIVERY_NOTE%>'
};
<%-- This is needed to set the max height of the items div called from css--%>
function getMaxDiv() {
	return document.getElementById("items").scrollHeight > 300 ? "300px" : "auto";
}
		
var logoutHref = '<html:rewrite action="Logout.do"/>';

</script>

<script type="text/javascript" src="<crh-common:url value='/js/pickup/Pickup.js'/>"></script>

<crh-common:authorize function="<%= Authorization.PICKUP_ORDER_TERMINAL_CREATE_NEW_ORDER %>">
	<script type="text/javascript" src="<crh-common:url value='/js/pickup/PickupCreate.js'/>"></script>
</crh-common:authorize>
<crh-common:authorize function="<%= Authorization.PICKUP_ORDER_TERMINAL_W %>">
	<script type="text/javascript" src="<crh-common:url value='/js/pickup/PickupEdit.js'/>"></script>
</crh-common:authorize>

<html:form styleId = "${FORM_NAME}" action="${ACTION_URL}">
	<!-- beginn Border-Table -->
<input type="hidden" name="inputAction" id="inputAction" value=""/>
<input type="hidden" name="selectedCustorderItemBeanIndex" id="selectedCustorderItemBeanIndex" value=""/>
<input type="hidden" name="selectedPalletItemBeanIndex" id="selectedPalletItemBeanIndex" value=""/>
	<table cellpadding="0" cellspacing="0" border="0" height="100%"style="padding:0px; margin:0px;">
	<tr style="height:15%;">
	<td>
	<table>
	<tr>
		<td style="vertical-align:middle;">
			<span class="label"><fmt:message key="pickup.column.shipto"/>:</span>
		</td>
		<td>
			&nbsp;<c:out value="${terminal_PickupForm.custorderBean.custorderEntity.shiptoSapBp.name1}"/> <c:out value="${terminal_PickupForm.custorderBean.custorderEntity.shiptoSapBp.name2}"/>
		</td>
	</tr>
	<tr>
		<td style="vertical-align:middle;">
			<span class="label"><fmt:message key="pickup.column.billto"/>:</span>
		</td>
		<td>
			&nbsp;<c:out value="${terminal_PickupForm.custorderBean.custorderEntity.billtoSapBp.name1}"/> <c:out value="${terminal_PickupForm.custorderBean.custorderEntity.billtoSapBp.name2}"/>
		</td>
	</tr>
	<tr>
		<td style="vertical-align:middle;">
			<span class="label"><fmt:message key="pickup.column.soldto"/>:</span>
		</td>
		<td>
		 	&nbsp;<c:out value="${terminal_PickupForm.custorderBean.custorderEntity.soldtoSapBp.name1}"/> <c:out value="${terminal_PickupForm.custorderBean.custorderEntity.soldtoSapBp.name2}"/>
		</td>
	</tr>
	<tr>
		<td style="vertical-align:middle;">
			<span class="label"><fmt:message key="pickup.column.deliverynote"/>:</span>
		</td>
		<td>
		 	&nbsp;<c:out value="${terminal_PickupForm.custorderBean.custorderEntity.noteDelivery}"/>
		</td>
	</tr>
	<tr>
		<td style="vertical-align:middle;">
			<span class="label"><fmt:message key="terminal.column.deliverypin"/>:</span>
		</td>
		<td>
		 	&nbsp;<c:out value="${terminal_PickupForm.orderValidationCode}"/>
		</td>
	</tr>
	</table>
	</td>
	</tr>
	<tr style="height:65%;">
	<td style="vertical-align:top;">
<table cellpadding="0" cellspacing="0" style="table-layout:fixed;padding:0px;spacing:0px;">
<tr>
<td>
<table style="table-layout:fixed" class="classicblue" >
	<thead>
		<colgroup>
			<col width="260px">
			<col width="150px">
			<col width="130px">
			<col width="120px">
			<col width="120px">
			<col width="210px">
			<col width="210px">
		</colgroup>
		<tr>
					<th id="th1"><fmt:message key="pickup.column.material"/></th>
					<th id="th2"><fmt:message key="pickup.column.quantity"/></th>
					<th id="th3"><fmt:message key="pickup.column.uom"/></th>
					<th id="th4"><fmt:message key="pickup.column.quantitybags"/></th>
					<th id="th5"><fmt:message key="pickup.column.tons"/></th>
					<th id="th6">&nbsp;</th>
					<th id="th7">&nbsp;</th>
		</tr>
		</table>
		</td>
		</tr>
		<tr>
		<td>
<div id="items" style="width:1236px;padding:0px;max-height:300px;height: expression( getMaxDiv() );overflow:auto;">
<table id="pickupOrderItemTable" style="table-layout:fixed" class="classicblue">
	<colgroup>
		<col width="260px">
		<col width="150px">
		<col width="130px">
		<col width="120px">
		<col width="120px">
		<col id="smallerCol1" width="210px">
		<col id="smallerCol2" width="210px">
	</colgroup>
<c:set var="zebracount" value="0"/>
<c:if test="${!empty terminal_PickupForm.custorderBean.custorderItemBeanList }">
<logic:iterate id="item" name="terminal_PickupForm" property="custorderBean.custorderItemBeanList" indexId="i">
			<c:set var="zebracount" value="${(zebracount + 1)%2}"/>
		<tr id="<c:out value="${item.deliverySetting.compositeIdentifier}"/>" class="zebra<c:out value="${zebracount}" />">
	<%-------------------------------------------------------------------------%>
	<%------------------------------MATERIAL-----------------------------------%>
	<%-------------------------------------------------------------------------%>
	<td>
		<c:out value="${item.deliverySetting.txt}"/>
	</td>
	<%-------------------------------------------------------------------------%>
	<%------------------------------QUANTITY-----------------------------------%>
	<%-------------------------------------------------------------------------%>
	<td align="right">
		<c:out value="${item.itemEntity.orderedQuantity}"/><br>
	</td>
	<%-------------------------------------------------------------------------%>
	<%------------------------------UNIT-- ------------------------------------%>
	<%-------------------------------------------------------------------------%>
	<td>
	<c:set var="curruomCd"><c:out value="${item.itemEntity.orderedUom.uomCd}"/></c:set>
	<crh-terminal:uomName uomCd="<%=(String)pageContext.getAttribute("curruomCd") %>"/>
	</td>
	<%-------------------------------------------------------------------------%>
	<%------------------------------BAGS --------------------------------------%>
	<%-------------------------------------------------------------------------%>
	<td align="right" >
<%
DecimalFormat df = new DecimalFormat();
df.setMinimumFractionDigits(0);
df.setMaximumFractionDigits(0);
%>
		<c:set var="quantityInSalesUom" value="${item.quantityInSalesUom}"/>
		<%=pageContext.getAttribute("quantityInSalesUom") != null ? df.format(pageContext.getAttribute("quantityInSalesUom")) : ""%>
	</td>
	<%-------------------------------------------------------------------------%>
	<%------------------------------TONS-- ------------------------------------%>
	<%-------------------------------------------------------------------------%>
	<td align="right" >
		<c:set var="quantityInTons" value="${item.quantityInTons}"/>
<%
df = new DecimalFormat();
df.setMinimumFractionDigits(1);
df.setMaximumFractionDigits(1);
%>
		<%=pageContext.getAttribute("quantityInTons") != null ? df.format(pageContext.getAttribute("quantityInTons")) : ""%>
	</td>
	<%-------------------------------------------------------------------------%>
	<%------------------------------REMOVE BUTTONS-----------------------------%>
	<%-------------------------------------------------------------------------%>
	<td align="left">
		<c:if test="${terminal_PickupForm.createMode }">
			<input style="width:190px" onclick='removeItem("<c:out value="${i}"/>")' type="button" class="ViewButtonStyleSmall"value=" <fmt:message key="pickup.button.removeitem"/> "/>
		</c:if>
	</td>
	<%-------------------------------------------------------------------------%>
	<%------------------------------CHANGE BUTTONS-----------------------------%>
	<%-------------------------------------------------------------------------%>
	<td align="left">
		&nbsp;
	</td>
</tr>
</logic:iterate>

	<%--------------------------------------------------------------------------------------------------------------------------------------------------%>
	<%--------------------------------------------------------------------------------------------------------------------------------------------------%>
	<%---------------------------------------------------------------START PALLET MATERIALS-------------------------------------------------------------%>
	<%--------------------------------------------------------------------------------------------------------------------------------------------------%>
	<%--------------------------------------------------------------------------------------------------------------------------------------------------%>
	<%--------------------------------------------------------------------------------------------------------------------------------------------------%>
	<%--------------------------------------------------------------------------------------------------------------------------------------------------%>
	<%--------------------------------------------------------------------------------------------------------------------------------------------------%>

<logic:iterate id="item" name="terminal_PickupForm" property="custorderBean.palletOrderItemBeanList" indexId="i">
<c:set var="zebracount" value="${(zebracount + 1)%2}"/>
		<tr id="<c:out value="${item.deliverySetting.compositeIdentifier}"/>" class="zebra<c:out value="${zebracount}" />">
	<%-------------------------------------------------------------------------%>
	<%------------------------------MATERIAL-----------------------------------%>
	<%-------------------------------------------------------------------------%>
	<td>
			<c:out value="${item.deliverySetting.txt}"/>
	</td>
	<%-------------------------------------------------------------------------%>
	<%------------------------------QUANTITY-----------------------------------%>
	<%-------------------------------------------------------------------------%>
	<td align="right">
		<fmt:message key="pickup.grid.loaded.palets"/>:
<%
DecimalFormat df = new DecimalFormat();
df.setMinimumFractionDigits(0);
df.setMaximumFractionDigits(0);
%>
		<c:set var="loadedPalletQuantity" value="${item.itemEntity.orderedQuantity}"/>
		<%=pageContext.getAttribute("loadedPalletQuantity") != null ? df.format(pageContext.getAttribute("loadedPalletQuantity")) : ""%>
	</td>
	<%-------------------------------------------------------------------------%>
	<%------------------------------UNIT-- ------------------------------------%>
	<%-------------------------------------------------------------------------%>
	<td>
	<c:set var="curruomCd"><c:out value="${item.itemEntity.orderedUom.uomCd}"/></c:set>
	<crh-terminal:uomName uomCd="<%=(String)pageContext.getAttribute("curruomCd") %>"/>
	</td>
	<%-------------------------------------------------------------------------%>
	<%------------------------------BAGS --------------------------------------%>
	<%-------------------------------------------------------------------------%>
	<td align="right" >
		&nbsp;
	</td>
	<%-------------------------------------------------------------------------%>
	<%------------------------------TONS-- ------------------------------------%>
	<%-------------------------------------------------------------------------%>
	<td align="right" >
		&nbsp;
	</td>
	<%-------------------------------------------------------------------------%>
	<%------------------------------REMOVE BUTTONS-----------------------------%>
	<%-------------------------------------------------------------------------%>
	<td align="left">
		&nbsp;
	</td>
	<%-------------------------------------------------------------------------%>
	<%------------------------------CHANGE BUTTONS-----------------------------%>
	<%-------------------------------------------------------------------------%>
	<td align="left">
			<input onclick="javascript:changePalletNumber('<c:out value="${i}"/>')" style="width:190px" type="button" class="ViewButtonStyleSmall"value=" <fmt:message key="pickup.button.changereturnpalletquantity"/> ">
	</td>
</tr>
<%-------------------------------------------------------------------------%>
<%------------------------------RETURN PALLETS-----------------------------%>
<%-------------------------------------------------------------------------%>
	<c:if test="${!terminal_PickupForm.returnProductAvailable}">
		<tr id="<c:out value="${deliverySetting.compositeIdentifier}"/>" class="zebra<c:out value="${zebracount}" />">
			<%---Material empty ---%>
			<td align="left"/>
			<%---Quantity ---%>
			<td align="right">
				<fmt:message key="pickup.grid.returned.palets"/>:<c:out value="${item.itemEntity.returnedQuantity}"/>
			</td>
			<%---UoM ---%>
			<td align="left">
				<%--<crh-terminal:uomName uomCd="<%=(String)pageContext.getAttribute("curruomCd") %>"/> --%>
			</td>
			<%---Bags empty ---%>
			<td align="left"/>
			<%---Tons empty ---%>
			<td align="left"/>
			<%---No delete button ---%>
			<td align="left"/>
			<%---Change button ---%>
			<td align="left">
				<input onclick="javascript:changeReturnPalletNumber('<c:out value="${i}"/>')" style="width:190px" type="button" class="ViewButtonStyleSmall"value=" <fmt:message key="pickup.button.changereturnpalletquantity"/> ">
			</td>
		</tr>
	</c:if>
</logic:iterate>
</c:if>
</table>
</div>
</td>
</tr>
</table>
</td>
</tr>
<tr style="height:20%;">
<td>

	<c:choose>
		<c:when test="${terminal_PickupForm.ordersLoadableTerminalFlag && !terminal_PickupForm.bulkOrderSelected}">
			<c:set var="saveButtonLabel" scope="request">
				<fmt:message key='pickup.button.saveorderandprint'/>
			</c:set>
		</c:when>
		<c:otherwise>
			<c:set var="saveButtonLabel" scope="request">
				<fmt:message key='pickup.button.saveorderonly'/>
			</c:set>
		</c:otherwise>
	</c:choose>

	<c:if test="${terminal_PickupForm.createMode}">
		<c:choose>
			<c:when test="${terminal_PickupForm.productTypeBulkSelected}">
				<c:set var="addNewItemButtonLabel" scope="request">
					<fmt:message key="bestellung.button.addservicenotincontract"/>
				</c:set>
			</c:when>
			<c:otherwise>
				<c:set var="addNewItemButtonLabel" scope="request">
					<fmt:message key="pickup.button.addnewitem"/>
				</c:set>
			</c:otherwise>
		</c:choose>
		<input onclick='javascript:addNewItem();' type='button' class='ViewButtonStyle'value=' <c:out value="${addNewItemButtonLabel}" />'>
		<input onclick='javascript:cancelCreateOrder()' type='button' class='ViewButtonStyle'value=' <fmt:message key="pickup.button.cancelcreateorder"/> '>
		<input onclick='javascript:editDeliveryNote()' type='button' class='ViewButtonStyle'value=' <fmt:message key="pickup.button.editdeliverynote"/> '> 
		<input onclick='javascript:saveNewOrder();' type='button' class='ViewButtonStyle'value=' <c:out value="${saveButtonLabel} "/> ' <c:if test = "${terminal_PickupForm.custorderBean.custorderItemBeanListSize <= 0 }">disabled</c:if>>
	</c:if>
	
	<c:if test="${!terminal_PickupForm.createMode}">
		<input onclick='javascript:cancelEditOrder()' type='button' class='ViewButtonStyle'value=' <fmt:message key="pickup.button.canceleditorder"/> '>
		<input onclick='javascript:editDeliveryNote()' type='button' class='ViewButtonStyle'value=' <fmt:message key="pickup.button.editdeliverynote"/> '>
		<input onclick='javascript:saveOrder();' type='button' class='ViewButtonStyle'value=' <c:out value="${saveButtonLabel} "/> ' <c:if test = "${terminal_PickupForm.custorderBean.custorderItemBeanListSize <= 0 }">disabled</c:if>>
	</c:if>

	<input onclick='javascript:logout()' type='button' class='ViewButtonStyle'value=' <fmt:message key="pickup.button.logout"/> '>

</td>
</tr>
</table>

<script type="text/javascript">
function resizeTable(){
	//this is the minimim width which is needed for header text, but min-width property is not usable in IE
	if (getMaxDiv() == "auto"){
		document.getElementById("smallerCol1").style.width = "210px";
		document.getElementById("smallerCol2").style.width = "210px";
	}
}
window.onload=resizeTable;
</script>

<%-- END $RCSfile: PickupOrderTile.jsp,v $ --%>
</html:form>
