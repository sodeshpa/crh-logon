<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: EditorsOngoingOrders.jsp,v $ --%> 

<%@ include file="../includeTLDs.jsp" %>

<%@page import="com.crh.logon.order.web.actions.bestellung.BestellungAction"%>

<c:set var="textareaCls" value="x-form-field comments" scope="request"/>
<c:set var="form" scope="page" value="${order_bestellung_BestellungForm}" />
<c:set var="RELOAD_OPEN_ORDERS"><%= BestellungAction.RELOAD_OPEN_ORDERS %></c:set>
<c:set var="RELOAD_OPEN_CUSTORDERS"><%= BestellungAction.RELOAD_OPEN_CUSTORDERS %></c:set>
<c:set var="RELOAD_CLOSED_CUSTORDERS"><%= BestellungAction.RELOAD_CLOSED_CUSTORDERS %></c:set>
<c:set var="RELOAD_CLOSED_ORDERS"><%= BestellungAction.RELOAD_CLOSED_ORDERS %></c:set>
<c:set var="RELOAD_ALL"><%= BestellungAction.RELOAD_ALL %></c:set>

<script type="text/javascript">
	
	var jsonData = <c:out value="${form.jsonData}" escapeXml="false"/>;
	var editorList = new Array();
	var shiptoId = <c:out value="${form.shipTo.shiptoId}"/>;
	var durationCorrFactor = <c:out value="${form.editorList[0].durationCorrFactor}"/>;
	var shiptoHasRailwayStation = <c:out value="${not empty form.shipTo.shiptoSapBp.railwayStation}"/>
	
	Ext.onReady(function() {
		// This field does not get reset automatically.
		var custorderCustrefNoField = Ext.getDom('editorList[0].custorderCustrefNo');
		custorderCustrefNoField.value = '';
		
		<%--
			Disable both the "Adjust Deliveries" button and the "Save Order" button by default
			(due to empty New Order editor data).
		--%>
		adjustDeliveriesBtn.disable();
		saveOrderBtn.disable();
	});
	
</script>

<div id="divEditors" style="width: 1250px; min-height: 55px; border: 1px solid #CCCCCC; border-top: 0px; margin-left: 5px">

<div id="divOrderEditor0">
	<jsp:include page="NewOrders.jsp"/>
</div>

</div>

<table style="width: 1230px; margin-left: 5px; padding-bottom: 5px">
<tr>
	<td>
		<div id="divAddOrderItem" style="margin-left: 4px">
			<a id="linkAddOrderItem" onclick="addOrderItem()" style="color: #BBBBBB"><img id="imgAddOrderItem" src="<crh-common:url value='/images/silk/basket_add_disabled.png'/>"/> <b><fmt:message key="bestellung.button.additem"/></b></a>
		</div>
		<div id="divAddServiceOrderItem" style="margin-left: 4px">
			<a id="linkAddServiceOrderItem" onclick="addOrderItemService()" style="color: #BBBBBB"><img id="imgAddServiceOrderItem" src="<crh-common:url value='/images/silk/basket_add_disabled.png'/>"/> <b><fmt:message key="bestellung.button.addservicenotincontract"/></b></a>
		</div>
	</td>
	<td class="right">
		<input type="button" class="ViewButtonStyle" id="adjustDeliveriesBtn"
				value='<fmt:message key="bestellung.button.adjustdeliveries"/>'
				onclick="saveOrderWithAjaxStep1(true)"/>&nbsp;
		
		<input type="button" class="ViewButtonStyle" id="saveOrderBtn"
				value="<fmt:message key="bestellung.button.saveorder"/>"
				onclick="saveOrderWithAjaxStep1(false)"/>&nbsp;
		
		<%--
			The hidden fields below are for storing temporarily the fact that saveOrderBtn must be
			shown or not.
		--%>
		<input type="hidden" value="" id="crSDOPack"/>
		<input type="hidden" value="" id="crSDODeliv"/>
		<input type="hidden" value="" id="crSDOSize"/>
	</td>
</tr>
</table>

<!-- ------------------------------------- -->
<!-- Open/Completed Orders/Customer Orders -->
<!-- ------------------------------------- -->

	<div id="divOrdersList">
		<jsp:include page="OrdersListTile.jsp"/>
	</div>

	<script type="text/javascript">

	var openOrders = {
		reload: function(refresh) {
			Ext.get('divOpenOrders').load({
				url: 'Bestellung.do', 
				params: {
					'action': '<c:out value="${RELOAD_OPEN_ORDERS}"/>',
					'refresh': (Ext.isEmpty(refresh) || refresh === false) ? 'false' : 'true'
				},
				text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
				scripts: true
			});
		}
	};

	var openCustorders = {
		reload: function(refresh) {
			Ext.get('divOpenCustorders').load({
				url: 'Bestellung.do', 
				params: {
					'action': '<c:out value="${RELOAD_OPEN_CUSTORDERS}"/>',
					'refresh': (Ext.isEmpty(refresh) || refresh === false) ? 'false' : 'true'
				},
				text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
				scripts: true
			});
		}
	};

	var closedCustorders = {
		reload: function(refresh) {
			Ext.get('divClosedCustorders').load({
				url: 'Bestellung.do', 
				params: {
					'action': '<c:out value="${RELOAD_CLOSED_CUSTORDERS}"/>',
					'refresh': (Ext.isEmpty(refresh) || refresh === false) ? 'false' : 'true'
				},
				text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
				scripts: true
			});
		}
	};

	var closedOrders = {
		//sort: function(field) {
		//	reload();
		//},
		//toggleSortDir: function() {},
		//filter: function(field, value) {},
		reload: function(refresh) {
			Ext.get('divClosedOrders').load({
				url: 'Bestellung.do', 
				params: {
					'action': '<c:out value="${RELOAD_CLOSED_ORDERS}"/>',
					'refresh': (Ext.isEmpty(refresh) || refresh === false) ? 'false' : 'true'
				},
				text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
				scripts: true
			});
		}
	};

	var allOrders = {
		reload: function(refresh, callback) {
			Ext.get('divOrdersList').load({
				url: 'Bestellung.do', 
				params: {
					'action': '<c:out value="${RELOAD_ALL}"/>',
					'refresh': (Ext.isEmpty(refresh) || refresh === false) ? 'false' : 'true'
				},
				text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
				scripts: true,
				callback: function() {
					if(callback){
						callback();
					}
				}
			});
		}
	};

	
	var toReloadIfOrderDetailClosed = false;
	
	function onOrderDetailClose() {
		if (toReloadIfOrderDetailClosed && toReloadIfOrderDetailClosed == true) {
			allOrders.reload();
		}
		setToReloadIfOrderDetailClosed(false);
	}
	
	function setToReloadIfOrderDetailClosed(isReload) {
		toReloadIfOrderDetailClosed = isReload;
	}
	
	</script>