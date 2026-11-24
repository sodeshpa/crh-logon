<%-- This popup is shown when an Scheduling screen / Re-route button is clicked, when an assignment is chosen before.
It shows the possible orders, which can be used for rerouting the assignment with. --%>
<%@ page session="true"%>

<%@ include file="../includeTLDs.jsp"%>

<%@page import="com.crh.logon.order.web.forms.dispo.AssignmentEditForm"%>
<%@page import="com.crh.logon.constants.Constant"%>

<c:set var="form" scope="page" value="${order_dispo_AssignmentEditForm}" />

<html:form action="dispo/AssignmentEdit.do" onsubmit="return false;" styleId="AssignmentEditForm">

<script type="text/javascript">
	var checkedOrderIds = {};
	var REROUTING_SUCCESSFUL = <%=Constant.REROUTING_SUCCESSFUL%>;
	var REROUTING_UNSUCCESSFUL_UNKNOWN = <%=Constant.REROUTING_UNSUCCESSFUL_UNKNOWN%>;
	var REROUTING_ORDERITEMS_NOMATCHING_WEIGHT_LESS_IN_LOAD = <%=Constant.REROUTING_ORDERITEMS_NOMATCHING_WEIGHT_LESS_IN_LOAD%>;
	var REROUTING_ORDERITEMS_NOMATCHING_WEIGHT_MORE_IN_LOAD = <%=Constant.REROUTING_ORDERITEMS_NOMATCHING_WEIGHT_MORE_IN_LOAD%>;
	var REROUTING_ORDERITEMS_NOMATCHING_MATERIAL_DIFFERENT = <%=Constant.REROUTING_ORDERITEMS_NOMATCHING_MATERIAL_DIFFERENT%>;
	var REROUTING_ORDERITEMS_NOMATCHING_PRODPLANT_DIFFERENT = <%=Constant.REROUTING_ORDERITEMS_NOMATCHING_PRODPLANT_DIFFERENT%>;
	var REROUTING_ORDERITEMS_NOMATCHING_STRUCTURE_DIFFERENT = <%=Constant.REROUTING_ORDERITEMS_NOMATCHING_STRUCTURE_DIFFERENT%>;
	var REROUTING_ORDERS_NOMATCHING_IN_TOUR = <%=Constant.REROUTING_ORDERS_NOMATCHING_IN_TOUR%>;
</script>

<table class="classicblue" style="width: 597px; margin-top: 4px">
	<thead>
		<tr>
			<th>
				<fmt:message key="dispo.reroute.fields.replacewith"/>
			</th>
			
			<th>
				<fmt:message key="dispo.reroute.fields.orderno"/>
			</th>
			
			<th>
				<fmt:message key="dispo.reroute.fields.shipto"/>
			</th>
			
			<th>
				<fmt:message key="dispo.reroute.fields.material"/>
			</th>
			
			<th>
				<fmt:message key="dispo.reroute.fields.orderedqty"/>
			</th>
			
			<th>
				<fmt:message key="dispo.reroute.fields.uom"/>
			</th>
		</tr>
	</thead>
	<tbody>
		<c:set var="rowCnt" value="${0}"/>
		<c:forEach var="reroutingOrder" varStatus="status" items="${form.reroutingOrderWrapperList}">
			<c:set var="rowCnt" value="${rowCnt + 1}"/>
			<tr class="zebra<c:out value="${rowCnt % 2}"/>">
				<td>
					<nobr>
						<c:if test="${reroutingOrder.isFirstItem}">
							<input type="checkbox"
								name = "checkedOrderIds" 
								value=<c:out value="${reroutingOrder.orderId}"/>
								onclick="if ('<c:out value="${reroutingOrder.isFirstItem}"/>') {toggleOtherOptionsDisabling(this);}">
						</c:if>
					</nobr>
				</td>
				
				<td>
					<nobr>
						<c:out value="${reroutingOrder.orderNumber}"/>
					</nobr>
				</td>
				
				<td>
					<nobr>
						<c:out value="${reroutingOrder.shipToName}"/>
					</nobr>
				</td>
				
				<td>
					<nobr>
						<c:out value="${reroutingOrder.materialName}"/>
					</nobr>
				</td>
				
				<td>
					<nobr>
						<c:out value="${reroutingOrder.orderedQuantity}"/>
					</nobr>
				</td>
				
				<td>
					<nobr>
						<c:out value="${reroutingOrder.orderedUomName}"/>
					</nobr>
				</td>
			</tr>
		</c:forEach>
	</tbody>
	
	<table>
		<tr>
			<td colspan="2" class="buttons right" style="vertical-align: top">
				<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.cancel"/>" onclick="hideLoad(dispo.selection[0]); hidePopup()"/>
				<input id="buttonSave" type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.save"/>" onclick="rerouteAssignment()"/>
			</td>
		</tr>
	</table>
	
</table>

</html:form>
