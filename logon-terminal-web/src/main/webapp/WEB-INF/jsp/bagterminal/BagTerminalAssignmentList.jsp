<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: BagTerminalAssignmentList.jsp,v $ --%>

<%@ page session="true" %>
<%@ page import="com.crh.logon.terminal.web.actions.bagterminal.BagTerminalAction" %>
<%@ page import="com.crh.logon.terminal.web.forms.bagterminal.BagTerminalForm"%>
<%@ include file="../includeTLDs.jsp" %>
<%@ page import="com.crh.logon.constants.Constant" %>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@ taglib uri="http://com.crh.logon/tags/common-taglib.tld" prefix="crh-common" %>
<%@ taglib uri="http://com.crh.logon/tags/terminal-taglib.tld" prefix="crh-terminal" %>

<c:if test="false">
    <%-- for preview in MyEclipse --%>
</c:if>
    <link rel="stylesheet" type="text/css" href="<crh-common:url value="/css/crh_touchscreen_style.css"/>">

<script type="text/javascript" src="<crh-common:url value='/js/bagterminal/BagTerminal.js'/>"></script>
	
<crh-common:authorize function="<%= Authorization.BAG_ORDER_TERMINAL_CHANGE_TRUCK %>">
	<script type="text/javascript" src="<crh-common:url value='/js/bagterminal/BagTerminalChangeTruck.js'/>"></script>
</crh-common:authorize>


<script type="text/javascript">

var actions = {
		SELECT_NEW_TRUCK: '<%=BagTerminalAction.ACTION_SELECT_NEW_TRUCK%>',
		PRINT_AND_FINISH: '<%=BagTerminalAction.ACTION_PRINT_DELIVERY_NOTE_AND_FINISH%>'		
	};

var logoutHref = '<html:rewrite action="Logout.do"/>';

<crh-common:authorize function="<%=Authorization.BAG_ORDER_TERMINAL_DELIVERY_NOTE %>">
function printAndFnish(){
	document.getElementById("inputAction").value=actions.PRINT_AND_FINISH;
	document.forms[0].submit();
	disableAll();
}
</crh-common:authorize>

function doOnSelectRow(assignmentId, statusCd, row, materialPackageTypeCd) {
	
	disableElement("printButton");
	selectAssignment(assignmentId,row);
	
	<c:if test="${terminal_BagTerminalForm.ordersLoadableTerminalFlag}">
		<%-- Enable the print button of open pickup orders if the terminal is loadable --%>
		<%
		for (int i=0;i<BagTerminalForm.getEditableStatuses().length;i++){
		%>
		if (statusCd == "<%=BagTerminalForm.getEditableStatuses()[i]%>"){
			enableElement("printButton");
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
	</c:if>
	
	<%-- Enable the print button when order is loaded already even if  the terminal is not loadable --%>		
	<c:if test="${not terminal_BagTerminalForm.ordersLoadableTerminalFlag}">
		if (statusCd >= 11) {
			enableElement("printButton");	
		}
	</c:if>

}

</script>

<c:set var="FORM_NAME"><%=BagTerminalForm.FORM_NAME %></c:set>
<c:set var="ACTION_URL"><%=BagTerminalAction.ACTION_URL %></c:set>
<c:set var="displayingShippingPointTimeZoneNeeded" scope="request" value="${terminal_BagTerminalForm.displayingShippingPointTimeZoneNeeded}"/>

<html:form styleId = "${FORM_NAME}" action="${ACTION_URL}">   

<input type="hidden" name="inputAction" id="inputAction" value=""/>
<input type="hidden" name="inputAssignmentId" id="inputAssignmentId" value=""/>

<table style="height:100%;" >
<tr style="height:80%;">
<td style="vertical-align:top">
<c:choose>
	<c:when test="${ !empty terminal_BagTerminalForm.driver && 
		!empty terminal_BagTerminalForm.driver.currentTruck}">
<table class="classicblue" >
                            <colgroup>
                            <col width="60px">
                            <col width="120px">
                            <col width="220px">
                            <col width="110px">
                            <col width="100px">
                            <col width="140px">
                           	<col width="150px">
                           	<col width="160px">
                           	<col width="80px">
                            </colgroup>
	<thead>
        <tr> 
        			<th id="th1"><fmt:message key="bagterminal.column.assignment"/></th>
                    <th id="th1"><fmt:message key="bagterminal.column.orderno"/></th>
                    <th id="th2"><fmt:message key="bagterminal.column.shipto"/></th>
                    <th id="th3"><fmt:message key="bagterminal.column.quantity"/></th>
                    <th id="th4"><fmt:message key="bagterminal.column.uom"/></th>
                    <th id="th5"><fmt:message key="bagterminal.column.material"/></th>
                    <th id="th6"><fmt:message key="bagterminal.column.loadingtime"/>
                    	<c:if test="${displayingShippingPointTimeZoneNeeded}">
									<c:out value="${terminal_BagTerminalForm.shippingPointTimeZone.formattedAsUTCOffset}"/>
						</c:if>
                    </th>
                    <th id="th7"><fmt:message key="bagterminal.column.status"/></th>
                    <th id="th8"><fmt:message key="terminal.column.pin"/></th>
        </tr>

        </thead>
        </table>
        <div id="orders" style="height:520px;overflow:auto">
<table id="bagOrderTable" class="classicblue">
                            <colgroup>
                            <col width="60px">
                            <col width="120px">
                            <col width="220px">
                            <col width="110px">
                            <col width="100px">
                            <col width="140px">
                           	<col width="150px">
                           	<col id="smallerCol" width="160px">
                           	<col width="80px">
                            </colgroup>
        <tbody>
         
<c:set var="zebracount" value="0"/>
<c:if test="${!empty terminal_BagTerminalForm.orderDTOList}">
<logic:iterate id="orderDTO" name="terminal_BagTerminalForm" property="orderDTOList"  indexId="i">
		<c:set var="order" value="${orderDTO.terminalOrderEntity}" />
		<crh-terminal:bagterminalRowspan printTdTag="false">
			<c:set var="zebracount" value="${(zebracount + 1)%2}"/>
		</crh-terminal:bagterminalRowspan>
		<tr id="<c:out value="${order.assignmentId}"/>" onclick='doOnSelectRow("<c:out value="${order.assignmentId}"/>","<c:out value="${order.orderStatusCd}"/>", this,"<c:out value="${order.materialPackageCd}"/>")' class="zebra<c:out value="${zebracount}" />">
			<crh-terminal:bagterminalRowspan printInTd="style=\"vertical-align:middle;\""><c:out value="${order.assignmentId}"/></crh-terminal:bagterminalRowspan>
	<td style="vertical-align:middle;">
            <c:out value="${order.orderNumber}"/>
	</td>            	
	<td style="vertical-align:middle;">
            <c:out value="${order.shiptoBpName}"/>
	</td>            
    <td style="vertical-align:middle;">
     		<c:out value="${order.orderedQuantity}"/>
	</td>
	<td style="vertical-align:middle;">
     		<c:out value="${order.orderUnitTxt}"/>
	</td>
	<td style="vertical-align:middle;">
			<c:out value="${order.materialTxt}"/>
	</td>
	<td style="vertical-align:middle;">
	<c:if test="${empty orderDTO.fmtActualDeliveryDat}">
		<c:out value="${orderDTO.fmtDeliveryFromDat}" /> - <c:out value="${orderDTO.fmtDeliveryToDat}" />
	</c:if>
	<c:if test="${!empty orderDTO.fmtActualDeliveryDat}">
		<c:out value="${orderDTO.fmtActualDeliveryDat}" />
	</c:if>
	</td>
	<td style="vertical-align:middle;">
		<c:set var="status"><c:out value="${order.orderStatusCd}"/></c:set>
	<%if(BagTerminalForm.getOpenStatusSet().contains(Long.valueOf((String)pageContext.getAttribute("status")))) { %>
			<fmt:message key="pickup.main.open"/>
	<%}else if(Long.valueOf((String)pageContext.getAttribute("status")).equals(Long.valueOf(Constant.ORDER_STATUS_DELETED))){ %>
			<fmt:message key="pickup.main.deleted"/>	
	<%}else{ %>
			<fmt:message key="terminal.deliverynotegenerated"/>	
	<%} %>
	</td>
	<td style="vertical-align:middle;">
		<c:out value="${order.orderValidationCode}"/>
	</td>	
  </tr>
  </logic:iterate>
  </c:if>
  </tbody>
  </table>
  </c:when>
  <c:otherwise>
  	<fmt:message key="bagterminal.notruck"/>
  </c:otherwise>
</c:choose>
  </td>
  </tr>
  <tr style="height:20%;">
  <td> 
	<c:if test="${!empty terminal_BagTerminalForm.carrier && !empty terminal_BagTerminalForm.driver }">
		<input id = "printButton" disabled onclick="javascript:printAndFnish();" type="button" class="ViewButtonStyle"  value=" <fmt:message key="bagterminal.button.pdf"/> ">
		<input onclick="javascript:selectNewTruck();" type="button" class="ViewButtonStyle"  value=" <fmt:message key="bagterminal.button.changetruck"/> " <c:if test="${terminal_BagTerminalForm.driverOnline }">disabled</c:if>>
	</c:if>
	<input onclick='javascript:logout()' type="button" class="ViewButtonStyle"  value=" <fmt:message key="bagterminal.button.logout"/> ">
</td>
</tr>
</table>
<c:if test="${!empty terminal_BagTerminalForm.orderDTOList }">
<script type="text/javascript">
function resizeTable(){
	if( parseFloat(document.getElementById("orders").scrollHeight) <  parseFloat(document.getElementById("orders").style.height)){
		document.getElementById("smallerCol").style.width = "160px";
	}
}
window.onload=resizeTable;
</script>
</c:if>

<%-- END $RCSfile: BagTerminalAssignmentList.jsp,v $ --%>
  </html:form>
