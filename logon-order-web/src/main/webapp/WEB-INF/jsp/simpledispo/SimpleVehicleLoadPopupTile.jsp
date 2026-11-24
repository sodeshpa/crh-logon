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
	var errortexts = {
		loadedQuantityNegative : '<fmt:message key="simpledispo.message.load.loadedQuantityNegative"/>',
		loadingPointsMustBeSelected : '<fmt:message key="simpledispo.message.load.loadingPointsMustBeSelected"/>'
	};

	function loadVehicle() {

		if (validate()) {
			var params = {
				'inputAction' : 'loadVehicle'
			};
			addFormElements(params, "order_simpledispo_SimpleDispoForm");

			showPopup('<c:url value="${simpledispoUrl}"/>',
					'<fmt:message key="contadmin.text.loading"/>', params);
		}
	}

	function validate() {
		var validationOk = true;

		// Loaded qty must not be negative.
		var els = Ext.query('.loadedqty');
		for (i = 0; i < els.length; i++) {
			var value = els[i].value;
			if (value < 0) {
				Logon.message.warning(errortexts.loadedQuantityNegative);
				validationOk = false;
				break;
			}
		}

		// Loadingpoint has to be selected for all order items.
		els = Ext.query('.loadingpoint_dropdown');
		for (i = 0; i < els.length; i++) {
			value = els[i].value;
			if (value === "") {
				Logon.message.warning(errortexts.loadingPointsMustBeSelected);
				validationOk = false;
				break;
			}
		}

		return validationOk;
	}

	function cancelFunction() {
		hidePopup();
	}
</script>

<html:form action="simpledispo/SimpleDispo.do" styleId='order_simpledispo_SimpleDispoForm'>
	
	<table class="classicblue" style="border: 1px solid lightgrey;">
		<tr>
			<th colspan="2">
				<fmt:message key="simpledispo.loadpopup.title"/>
			</th>
		</tr>
		<tr>
			<td>
				<span class="label">
					<fmt:message key="simpledispo.loadpopup.orders"/>
				</span>
				<table class="classicblue" style="border: none;">
					<tr>
						<th><fmt:message key="simpledispo.loadpopup.orderno"/></th>
						<th><fmt:message key="simpledispo.loadpopup.pos"/></th>
						<th><fmt:message key="simpledispo.loadpopup.material"/></th>
						<th><fmt:message key="simpledispo.loadpopup.loadingpoint"/></th>
						<th><fmt:message key="simpledispo.loadpopup.orderedqty"/></th>
						<th><fmt:message key="simpledispo.loadpopup.ordereduom"/></th>
						<th><fmt:message key="simpledispo.loadpopup.loadedqty"/></th>
						<th><fmt:message key="simpledispo.loadpopup.loadeduom"/></th>
					</tr>
					<c:forEach var="loadItem" varStatus="status" items="${order_simpledispo_SimpleDispoForm.simpleVehicleLoadBean.loadableItems}">
						<tr>
							<td>
								<c:out value="${loadItem.loadOrderItem.orderNumber}"/>
							</td>
							<td>
								<c:out value="${loadItem.loadOrderItem.orderItemNo}"/>
							</td>
							<td>
								<c:out value="${loadItem.loadOrderItem.materialDescription}"/>
							</td>
							<td>
								<html:select property="simpleVehicleLoadBean.loadingPointIdIndexed[${status.count-1}]" styleClass="loadingpoint_dropdown">
									<html:option value=""><fmt:message key="simpledispo.loadpopup.selectloadingpoint"/></html:option>
									<c:forEach var="loadingpoint" items="${loadItem.loadingPoints}">	
										<html:option value="${loadingpoint.loadingpointId}">
											<c:out value="${loadingpoint.loadingpointName}"/>
										</html:option>
									</c:forEach>
								</html:select>
							</td>
							<td>
								<c:out value="${loadItem.loadOrderItem.orderedQuantity}"/>
							</td>
							<td>
								<c:out value="${loadItem.loadOrderItem.orderedUomCd}"/>
							</td>
							<td>
								<html:text property="simpleVehicleLoadBean.loadedQtyIndexed[${status.count-1}]" size="4" maxlength="16" styleClass="loadedqty"/>
							</td>
							<td>
								<c:out value="${loadItem.loadOrderItem.loadedUomCd}"/>
							</td>
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
					<input style="vertical-align: middle" onclick="loadVehicle();" type="button" class="ViewButtonStyle"
						value=" <fmt:message key="simpledispo.loadpopup.loadvehicle"/> ">
				</div>
			</td>
		</tr>
	</table>
</html:form>
