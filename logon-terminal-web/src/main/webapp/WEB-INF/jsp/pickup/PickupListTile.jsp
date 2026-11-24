<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: PickupListTile.jsp,v $ --%>

<%@ page session="true" %>
<%@ page import="com.crh.logon.terminal.web.actions.pickup.PickupAction" %>
<%@ page import="com.crh.logon.terminal.web.forms.pickup.PickupForm"%>
<%@ include file="../includeTLDs.jsp" %>
<%@ page import="com.crh.logon.constants.Constant" %>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@ taglib uri="http://com.crh.logon/tags/common-taglib.tld" prefix="crh-common" %>
<%@ taglib uri="http://com.crh.logon/tags/terminal-taglib.tld" prefix="crh-terminal" %>

<c:if test="false">
    <%-- for preview in MyEclipse --%>
</c:if>
    <link rel="stylesheet" type="text/css" href="<crh-common:url value="/css/crh_touchscreen_style.css"/>">

<script type="text/javascript" src="<crh-common:url value='/js/pickup/Pickup.js'/>"></script>
	
<crh-common:authorize function="<%= Authorization.PICKUP_ORDER_TERMINAL_CREATE_NEW_ORDER %>">
	<script type="text/javascript" src="<crh-common:url value='/js/pickup/PickupCreate.js'/>"></script>
</crh-common:authorize>
<crh-common:authorize function="<%= Authorization.PICKUP_ORDER_TERMINAL_W %>">
	<script type="text/javascript" src="<crh-common:url value='/js/pickup/PickupEdit.js'/>"></script>
</crh-common:authorize>

<script type="text/javascript">

var actions = {
		CREATE_NEW_PICKUP: '<%=PickupAction.ACTION_CREATE_NEW_PICKUP%>',
		PRINT_AND_FINISH: '<%=PickupAction.ACTION_PRINT_DELIVERY_NOTE_AND_FINISH%>',
		EDIT_PICKUP: '<%=PickupAction.ACTION_EDIT_PICKUP%>'		
	};

var logoutHref = '<html:rewrite action="Logout.do"/>';

<crh-common:authorize function="<%= Authorization.PICKUP_ORDER_TERMINAL_DELIVERY_NOTE %>">
function printAndFnish(){
	document.getElementById("inputAction").value=actions.PRINT_AND_FINISH;
	document.forms[0].submit();
	disableAll();
}
</crh-common:authorize>

function doOnSelectRow(custorderId, statusCd, row, materialPackageTypeCd){

	disableElement("printButton");
	disableElement("editButton");

	selectOrder(custorderId,row);
	
	<%-- Enable the print button of open materials if the terminal is loadable --%>
	<%
	for (int i=0;i<PickupForm.getOpenStatuses().length;i++){
	%>
	if (statusCd == "<%=PickupForm.getOpenStatuses()[i]%>"){
		enableElement("editButton");
	<c:if test="${terminal_PickupForm.ordersLoadableTerminalFlag}">
		enableElement("printButton");
	</c:if>
	}
	<%	
	}
	%>
	
	<%-- Disable the print button of open bulk pickup orders even if terminal is loadable, see LOGON-5073 --%>
	<%
	for (String packageTypeCd : Constant.bulkOrderMaterialPackageCds) {
	%>
	if (materialPackageTypeCd == "<%=packageTypeCd%>") {
		disableElement("printButton");
	}
	<%
	}
	%>
	
	<%-- Enable the print button for already loaded orders (even if  the terminal is not loadable) --%>		
	<c:if test="${not terminal_PickupForm.ordersLoadableTerminalFlag}">
		if (statusCd >= 11) {
			enableElement("printButton");	
		}
	</c:if>

}
</script>



<c:set var="FORM_NAME"><%=PickupForm.FORM_NAME %></c:set>
<c:set var="ACTION_URL"><%=PickupAction.ACTION_URL %></c:set>
<c:set var="displayingShippingPointTimeZoneNeeded" scope="request" value="${terminal_PickupForm.displayingShippingPointTimeZoneNeeded}"/>

<html:form styleId = "${FORM_NAME}" action="${ACTION_URL}">   

<input type="hidden" name="inputAction" id="inputAction" value=""/>
<input type="hidden" name="inputCustOrderId" id="inputCustOrderId" value=""/>

<table style="height:100%;" width="100%">
<tr style="height:80%;">
<td style="vertical-align:top">
<table class="classicblue" >
                            <colgroup>
                            <col width="120px">
                            <col width="200px">
                            <col width="110px">
                            <col width="130px">
                            <col width="190px">
                           	<col width="200px">
                           	<col width="160px">
                           	<col width="80px">
                            </colgroup>
	<thead>
        <tr> 
                    <th id="th1"><fmt:message key="pickup.column.orderno"/></th>
                    <th id="th2"><fmt:message key="pickup.column.shipto"/></th>
                    <th id="th3"><fmt:message key="pickup.column.quantity"/></th>
                    <th id="th4"><fmt:message key="pickup.column.uom"/></th>
                    <th id="th5"><fmt:message key="pickup.column.material"/></th>
                    <th id="th6"><fmt:message key="pickup.column.loadingtime"/>
                    	<c:if test="${displayingShippingPointTimeZoneNeeded}">
                    		<c:out value="${terminal_PickupForm.shippingPointTimeZone.formattedAsUTCOffset}"/>
						</c:if>
                    </th>
                    <th id="th7"><fmt:message key="pickup.column.status"/></th>                    
                    <th id="th8"><fmt:message key="terminal.column.pin"/></th>                    
        </tr>

        </thead>
        </table>
        <div id="orders" style="height:520px;overflow:auto">
<table id="pickupOrderTable" class="classicblue">
                            <colgroup>
                            <col width="120px">
                            <col width="200px">
                            <col width="110px">
                            <col width="130px">
                            <col width="190px">
                           	<col width="200px">
                           	<col id="smallerCol" width="160px">
                           	<col width="80px">
                            </colgroup>
        <tbody>
         
<c:set var="zebracount" value="0"/>
<c:if test="${!empty terminal_PickupForm.orderDTOList}">
<logic:iterate id="orderDTO" name="terminal_PickupForm" property="orderDTOList"  indexId="i">
		<c:set var="order" value="${orderDTO.terminalOrderEntity}" />
		<crh-terminal:terminalRowspan printTdTag="false">
			<c:set var="zebracount" value="${(zebracount + 1)%2}"/>
		</crh-terminal:terminalRowspan>
	<tr id="<c:out value="${order.custorderId}"/>" 
	onclick='doOnSelectRow("<c:out value="${order.custorderId}"/>","<c:out value="${order.orderStatusCd}"/>",this,"<c:out value="${order.materialPackageCd}"/>")' 
	class="zebra<c:out value="${zebracount}" />">
		<crh-terminal:terminalRowspan printInTd="style=\"vertical-align:middle;\"">
			<c:out value="${order.orderNumber}"/>
		</crh-terminal:terminalRowspan>
	<crh-terminal:terminalRowspan printInTd="style=\"vertical-align:middle;\"">
            <c:out value="${order.shiptoBpName}"/>
	</crh-terminal:terminalRowspan>
    <td style="vertical-align:middle;" align="right">
     		<c:out value="${order.orderedQuantity}"/>
	</td>
	<td style="vertical-align:middle;">
     		<c:out value="${order.orderUnitTxt}"/>
	</td>
	<td style="vertical-align:middle;">
			<c:out value="${order.materialTxt}"/>
	</td>
	<crh-terminal:terminalRowspan printInTd="style=\"vertical-align:middle;\"">
	<c:if test="${empty orderDTO.fmtActualDeliveryDat}">
		<c:out value="${orderDTO.fmtDeliveryFromDat}" /> - <c:out value="${orderDTO.fmtDeliveryToDat}" />
	</c:if>
	<c:if test="${!empty orderDTO.fmtActualDeliveryDat}">
		<c:out value="${orderDTO.fmtActualDeliveryDat}" />
	</c:if>
	</crh-terminal:terminalRowspan>
	<crh-terminal:terminalRowspan printInTd="style=\"vertical-align:middle;\"">
	<c:set var="status"><c:out value="${order.orderStatusCd}"/></c:set>
	<%if(PickupForm.getOpenStatusSet().contains(Long.valueOf((String)pageContext.getAttribute("status")))) { %>
			<fmt:message key="pickup.main.open"/>
	<%}else if(Long.valueOf((String)pageContext.getAttribute("status")).equals(Long.valueOf(Constant.ORDER_STATUS_DELETED))){ %>
		<fmt:message key="pickup.main.deleted"/>	
	<%}else{ %>
			<fmt:message key="terminal.deliverynotegenerated"/>	
	<%} %>
	</crh-terminal:terminalRowspan>
	<td style="vertical-align:middle;">
			<c:out value="${order.orderValidationCode}"/>
	</td>
  </tr>
  </logic:iterate>
  </c:if>
  </tbody>
  </table>
  </div>
  </td>
  </tr>
  <tr style="height:20%;">
  <td> 
	<input id = "createButton" onclick="javascript:createNewPickup();" type="button" class="ViewButtonStyle"  value=" <fmt:message key="pickup.button.createnewpickuporder"/> " <c:if test="${empty terminal_PickupForm.shiptoList }">disabled</c:if>>	
	<input id = "editButton" disabled onclick="javascript:editPickup();" type="button" class="ViewButtonStyle"  value=" <fmt:message key="pickup.button.editpickuporder"/> ">
	<input id = "printButton" disabled onclick="javascript:printAndFnish();" type="button" class="ViewButtonStyle"  value=" <fmt:message key="pickup.button.pdf"/> ">	
	<input onclick='javascript:logout()' type="button" class="ViewButtonStyle"  value=" <fmt:message key="pickup.button.logout"/> ">
</td>
</tr>
</table>
<c:if test="${!empty terminal_PickupForm.orderDTOList }">
<script type="text/javascript">
function resizeTable(){
	if( parseFloat(document.getElementById("orders").scrollHeight) <  parseFloat(document.getElementById("orders").style.height)){
		
		document.getElementById("smallerCol").style.width = "160px";
	}
}
window.onload=resizeTable;
</script>
</c:if>
<%-- END $RCSfile: PickupListTile.jsp,v $ --%>
  </html:form>
