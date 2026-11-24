<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: SimpleVehicleUnloadPopupTile.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp" %>

<c:set var="simpledispoUrl">
	/../order/simpledispo/SimpleDispo.do
</c:set>

<script type="text/javascript">
	var errortexts = {
		deliveredQuantityNegative : '<fmt:message key="simpledispo.message.unload.deliveredQuantityNegative"/>'
	};

	function unLoad() {
		if (validate()) {
			var params = {
				'inputAction' : 'unloadVehicle'
			};

			addFormElements(params, "order_simpledispo_SimpleDispoForm");

			showPopup('<c:url value="${simpledispoUrl}"/>',
					'<fmt:message key="contadmin.text.loading"/>', params);
		}
	};

	function validate() {
		var validationOk = true;

		// Loaded qty must not be negative.
		var els = Ext.query('.deliveredqty');
		for (i = 0; i < els.length; i++) {
			var value = els[i].value;
			if (value < 0) {
				Logon.message.warning(errortexts.deliveredQuantityNegative);
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

<c:set var="form" scope="page" value="${order_simpledispo_SimpleDispoForm}" />

<html:form action="simpledispo/SimpleDispo.do" styleId='order_simpledispo_SimpleDispoForm'>
	<table class="classicblue" style="border:none; width:auto">
		<tr>
			<th>
				<fmt:message key="simpledispo.unloadpopup.header" />
			</th>
		</tr>
		<tr>
			<td class="popupcontent">
				<table width="100%">
					<thead>
						<tr>
							<th><fmt:message key="simpledispo.unloadpopup.columns.orderNo"/></th>
							<th><fmt:message key="simpledispo.unloadpopup.columns.pos"/></th>
							<th><fmt:message key="simpledispo.unloadpopup.columns.material"/></th>
							<th><fmt:message key="simpledispo.unloadpopup.columns.loadedQty"/></th>
							<th><fmt:message key="simpledispo.unloadpopup.columns.loadedUom"/></th>
							<th><fmt:message key="simpledispo.unloadpopup.columns.deliveredQty"/></th>
							<th><fmt:message key="simpledispo.unloadpopup.columns.deliveredUom"/></th>
						</tr>
					</thead>
					<c:forEach var="item" varStatus="status" items="${form.simpleVehicleUnloadBean.items}">
						<tr>
							<td><c:out value="${item.orderNumber}"/></td>
							<td style="text-align:right;"><c:out value="${item.orderItemNo}"/></td>
							<td><c:out value="${item.txt}"/></td>
							<td style="text-align:right;"><c:out value="${item.loadedQuantity}"/></td>
							<td><c:out value="${item.loadedUomCd}"/></td>
							<td>
								<%-- 
									LOGON-5956 Unloading Popup: functionality to set unloaded quantity different from loaded quantity
									This textfield is disabled for now, until the implementation of unloading service is done.
								 --%>
								<html:text
									property="simpleVehicleUnloadBean.deliveredQtyIndexed[${status.count-1}]"
									size="4" maxlength="16" styleClass="deliveredqty" disabled="true"/>
							</td>
							<td><c:out value="${item.loadedUomCd}"/></td>
						</tr>
					</c:forEach>
					<tr>
						<td colspan="5" valign="bottom" class="label" style="white-space:nowrap;border:0px;">
							<html:checkbox property="simpleVehicleUnloadBean.initialReleaseToSAPFlag" styleId="finalizeToSap"/>
							<label for="finalizeToSap"><fmt:message key="simpledispo.unloadpopup.label.finalizeToSAP"/></label>
						</td>
					</tr>
				</table>
			</td>
		</tr>
			<td class="buttons right">
				<nobr>
					<input type="button" class="ViewButtonStyle" onClick="cancelFunction()"
					value="<fmt:message key="buttons.cancel"/>">
					<input type="button" class="ViewButtonStyle" onClick="unLoad()"
					value="<fmt:message key="simpledispo.unloadpopup.button.unloadOrders"/>">
				</nobr>
			</td>
		</tr>
	</table>
</html:form>

<%-- END $RCSfile: SimpleVehicleUnloadPopupTile.jsp,v $ --%>