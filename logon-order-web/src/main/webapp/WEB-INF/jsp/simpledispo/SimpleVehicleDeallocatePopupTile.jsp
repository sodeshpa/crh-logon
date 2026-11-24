<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ include file="../includeTLDs.jsp" %>

<c:set var="simpledispoUrl">
	/../order/simpledispo/SimpleDispo.do
</c:set>

<script type="text/javascript">
	
	function save() {
		var params = {'inputAction' : 'deallocate'};
		addFormElements(params, "order_simpledispo_SimpleDispoForm");

		Ext.Ajax.request({
			url : '<c:url value="${simpledispoUrl}"/>',
			params : params,
			success: function() {
				// Refresh the page to refresh the vehicle list
				Ext.get('order_simpledispo_SimpleDispoForm').dom.submit();
				hidePopup();
			}
		});
	}

	function cancelFunction() {
		hidePopup();
	}
</script>

<html:form action="simpledispo/SimpleDispo.do" styleId='order_simpledispo_SimpleDispoForm'>
	
	<table class="classicblue" style="border: 1px solid lightgrey;">
		<tr>
			<th colspan="2">
				<fmt:message key="simpledispo.deallocate.title"/>
			</th>
		</tr>
		<tr>
			<td>
				<span class="label">
					<fmt:message key="simpledispo.loadpopup.orders"/>
				</span>
				<table class="classicblue" style="border: none;">
					<tr>
						<th> </th>
						<th><fmt:message key="simpledispo.loadpopup.orderno"/></th>
						<th><fmt:message key="simpledispo.deallocate.shipto"/></th>
						<th><fmt:message key="simpledispo.loadpopup.material"/></th>
						<th><fmt:message key="simpledispo.deallocate.orderstatus"/></th>
					</tr>
					<c:set var="vehicle" value="${order_simpledispo_SimpleDispoForm.simpleVehicleDeallocateBean.simpleVehicle}"/>
					<c:set var="disabled" value="${vehicle.released and vehicle.ldsDefined}"/>
					<c:forEach var="part" varStatus="status" items="${vehicle.parts}">
						<tr>
							<td>
								<html:checkbox
									property="simpleVehicleDeallocateBean.selectedOrderIdIndexed[${status.count-1}]" 
									disabled="${disabled}"/>
							</td>
							<td><c:out value="${part.orderNumber}"/></td>
							<td><c:out value="${part.shiptoName}"/></td>
							<td><c:out value="${part.materialDescTxt}"/></td>
							<td><c:out value="${part.orderStatusTxt}"/></td>
						</tr>
					</c:forEach>
				</table>
			</td>
		</tr>
		<!-- buttons -->
		<tr>
			<td colspan="2">
				<div align="right">
					<input onclick="cancelFunction();" type="button" class="ViewButtonStyle"
						value=" <fmt:message key="simpledispo.loadpopup.cancel"/> ">
					<input style="vertical-align: middle" onclick="save();" type="button" class="ViewButtonStyle"
						value=" <fmt:message key="simpledispo.deallocate.save"/> ">
				</div>
			</td>
		</tr>
	</table>
</html:form>