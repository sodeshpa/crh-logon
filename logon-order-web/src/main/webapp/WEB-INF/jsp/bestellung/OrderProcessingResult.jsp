<%@ include file="../includeTLDs.jsp" %>
<%@page import="com.crh.logon.order.web.actions.bestellung.BestellungAction"%>

<script type="text/javascript">

function reloadEditorsAndOrders() {
	Ext.onReady(function() {

		editorList = null; // Will be recreated when DIV below is reloaded
		logonGmap.loadMap();

		// --- Reload the editor row and all the order lists ---
		var divEditorsAllOrders = Ext.get('divEditorsAllOrders');

		divEditorsAllOrders.load({
			url: '../bestellung/Bestellung.do', 
			params: { 'action': 'reloadEditorsAllOrders' },
			text: '<fmt:message key="bestellung.indicator.reloading.order.list"/>...',
			scripts: true,
			nocache: true,
			callback: function() {
				<c:if test="${saveCustomerOrderSuccess}">
					if (addToCustorderMode.enabled) {
						addToCustorderMode.toggle(null);
					}
				</c:if>
			}
		});

		refreshChartArea({selectFirstDay : true,
			 'isOnCallCenter': 'true'});

	});
}

<c:choose>

	<%-- Order(s) have been saved successfully, reload the editor and ongoing order list --%>
	<c:when test="${orderSaveSuccess}">
		reloadEditorsAndOrders();
	</c:when>

	<%-- First step of Customer Order Creation was successful --%>
	<c:when test="${createCustomerOrderStep1Success && !saveCustomerOrderFailed}">
		Ext.get('divEditors').unmask();
		var params = {};
		addShiptoAndNewOrderFormData(params, { isCustomerOrder: true });
		params.action = 'showSinDelOrdSplitPopup1';
		var loadingMessage = '<fmt:message key="extjs.processing"/>'; 
		showPopup('../bestellung/Bestellung.do', loadingMessage, params);
	</c:when>

	<%-- After setting a customer order to finished --%>
	<c:when test="${setCustorderToFinished}">
		<c:if test="${setCustorderToFinishedSuccess}">
			reloadEditorsAndOrders();
		</c:if>
	</c:when>
	
	<c:when test="${showCloseCustorderPopup}">
		<jsp:include page="CustOrderConf.jsp"></jsp:include>
	</c:when>

	<%-- Other errors (tipically validation error) --%>
	<c:otherwise>
		
		isEditorDisabled = false;
		Ext.get('divEditors').unmask();
		saveOrderBtn.update();
		adjustDeliveriesBtn.update();
		addOrderItemBtn.handleAfterValidationError();
		addServiceOrderItemBtn.handleAfterValidationError();
		
		<c:if test="${saveCustomerOrderFailed}">
			hidePopup();
		</c:if>
	</c:otherwise>

</c:choose>

blockChartRefresh = false;

</script>
