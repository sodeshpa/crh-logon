<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ include file="../includeTLDs.jsp"%>

<c:set var="editHomePlantUrl">
	/../order/dispo/EditHomePlant.do
</c:set>

<script type="text/javascript">

	Ext.onReady(function() {
		<c:if test="${callReloadTrucksJs}">
			refreshTrucks();
		</c:if>
	});
	
	function setSelectedPlant() {
		a = {'setPlant' : ''};
		addFormElements(a, "EditHomePlantForm");
		showPopup('<c:url value="${editHomePlantUrl}"/>', null, a);
	}

	function save(param) {
		a = param;
		addFormElements(a, "EditHomePlantForm");
		showPopup('<c:url value="${editHomePlantUrl}"/>', null, a);
	}

	function saveAndClose() {
		var param = {
			'saveClose' : ''
		};
		save(param);
	}

	function cancelFunction() {
		<c:if test="${callReloadTrucksJs}">
		if (logonPopupHideCallback) {
			logonPopupHideCallback();
		}
		</c:if>
		hidePopup();
	}
</script>

<html:form action="dispo/EditHomePlant.do"
	styleId='EditHomePlantForm'>

	<table class="classicblue" style="border: 1px solid lightgrey;">
		<tr>
			<th colspan="2"><fmt:message key="dispo.truckhomeplant.popuptitle"/></th>
		</tr>
		<tr>
			<td><span class="label"><fmt:message key="dispo.truckhomeplant.labels.plant"/></span></td>

			<td>
				<html:select property="selectedPlant" style="width:230px;" styleId="plants" onchange="setSelectedPlant();">
					<html:option value="-1">-</html:option>
					<c:forEach var="plant"
						items="${order_dispo_EditHomePlantForm.plants}">
						<html:option value="${plant.value}">
							<c:out value="${plant.name}"/>
						</html:option>
					</c:forEach>
				</html:select>
			</td>
		</tr>
		<tr>
			<td><span class="label"><fmt:message key="dispo.truckhomeplant.labels.shippingpoint"/></span></td>
			<td>
				<html:select property="selectedShippingpoint" style="width:230px;" styleId="shippingPoints">
					<html:option value="-1">-</html:option>
					<c:forEach var="sp" items="${order_dispo_EditHomePlantForm.shippingpoints}">
						<html:option value="${sp.shippingpointId}">
							<c:out value="${sp.shippingpointName}" />
						</html:option>
					</c:forEach>
				</html:select>
			</td>
		</tr>
		<!-- buttons -->
		<tr>
			<td colspan="2">
				<div align="right">
					<input onclick="cancelFunction();" type="button" class="ViewButtonStyle"
						value=" <fmt:message key="dispo.truckhomeplant.button.cancel"/> ">
					<input style="vertical-align: middle" onclick="saveAndClose();" type="button" class="ViewButtonStyle"
						value=" <fmt:message key="dispo.truckhomeplant.button.save.close"/> ">
				</div>
			</td>
		</tr>
	</table>
</html:form>
