<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: SingleDeliveryOrderPopup1Tile.jsp,v $ --%>

<%@ page session="true"%>
<%@ include file="../includeTLDs.jsp"%>
<%@page import="com.crh.logon.constants.Constant"%>

<%@page import="com.crh.logon.web.utils.Authorization"%>

<c:set var="form" scope="page" value="${order_bestellung_BestellungForm}" />
<c:set var="orderQuantity" value="${form.editorList[0].custOrderQuantity}"/>
<c:set var="orderUom" value="${form.editorList[0].orderItems[0].uomCd}"/>
<c:set var="deliveryFrom" value="${form.deliveryFromDatStr} ${form.deliveryFromTime}"/>
<c:set var="deliveryTo" value="${form.deliveryToDatStr} ${form.deliveryToTime}"/>

<script>

Ext.onReady(function() {
});

Ext.get('buttonCancel').on('click', function() {
	cancelCustOrdCreation();
});

Ext.get('buttonGenerate').on('click', function() {
	var params = {};
	addShiptoAndNewOrderFormData(params, { isCustomerOrder: true });
	addFormElements(params, 'singleDeliveryOrderSplit1Form');
	params.action = 'showSinDelOrdSplitPopup2';
	var loadingMessage = '<fmt:message key="extjs.processing"/>'; 
	showPopup('../bestellung/Bestellung.do', loadingMessage, params);
});

</script>

<div id="SingleDeliveryOrderSplit1Div" class="customer_max" style="width: 500px;" >

<html:form action="bestellung/Bestellung.do" styleId="singleDeliveryOrderSplit1Form">

	<table class="big_form2">
		<tr>
			<th class="classicblue" colspan="4">Split Customer (test for v2.0.1.3)</th>
		</tr>
		<tr>
			<td>
				delivery from
			</td>
			<td>
				<html:text	property="editorList[0].cosDeliveryFromStr"  styleId="cosDeliveryFromStr"  value='${deliveryFrom}' style="width: 150; margin-top: 2px; padding: 0px; font-size: 11px"/>
			</td>
		</tr>
		<tr>
			<td>
				delivery to
			</td>
			<td>
				<html:text	property="editorList[0].cosDeliveryToStr"  styleId="cosDeliveryToStr"  value='${deliveryTo}' style="width: 150; margin-top: 2px; padding: 0px; font-size: 11px"/>
			</td>
		</tr>
		<tr>
			<td>
				ordered quantity (number)
			</td>
			<td>
				<html:text	property="editorList[0].cosOrderedQty"  styleId="cosOrderedQty"  value='${orderQuantity}' style="width: 30; margin-top: 2px; padding: 0px; font-size: 11px">
				</html:text>
				<html:text	property="editorList[0].cosOrderedUom"  styleId="cosOrderedUom"  value='${orderUom}' style="width: 30; margin-top: 2px; padding: 0px; font-size: 11px">
				</html:text>
				(TO or LKW)
			</td>
		</tr>
		<tr>
			<td>
				exact custorder quantity
			</td>
			<td>
				<html:checkbox property="editorList[0].cosExactCustorderQtyFlag" styleId="cosExactCustorderQtyFlag" />
			</td>
		</tr>
		<tr>
			<td>
				per single delivery order quantity (number)
			</td>
			<td>
				<html:text	property="editorList[0].cosPerSdOrderQty"  styleId="cosPerSdOrderQty"  style="width: 30; margin-top: 2px; padding: 0px; font-size: 11px">
				</html:text>
				<html:text	property="editorList[0].cosPerSdOrderUom"  styleId="cosPerSdOrderUom"  style="width: 30; margin-top: 2px; padding: 0px; font-size: 11px">
				</html:text> (e.g. TO)
			</td>
		</tr>
		<tr>
			<td>
				exact single delivery quantity
			</td>
			<td>
				<html:checkbox property="editorList[0].cosExactSdOrderQtyFlag" styleId="cosExactSdOrderQtyFlag" />
			</td>
		</tr>
		<tr>
			<td>
				vehicle max. capacity in tons (number)
			</td>
			<td>
				<html:text	property="editorList[0].cosVehicleMaxCapacityInTons"  styleId="cosVehicleMaxCapacityInTons"  style="width: 30; margin-top: 2px; padding: 0px; font-size: 11px">
				</html:text>TO
			</td>
		</tr>
		<tr>
			<td>
				delivery frequency every time (number)
			</td>
			<td>
				<html:text	property="editorList[0].cosDeliveryFreqEveryTime"  styleId="cosDeliveryFreqEveryTime"  style="width: 30; margin-top: 2px; padding: 0px; font-size: 11px">
				</html:text>
				<html:text	property="editorList[0].cosDeliveryFreqEveryTimeUom"  styleId="cosDeliveryFreqEveryTimeUom"  style="width: 30; margin-top: 2px; padding: 0px; font-size: 11px">
				</html:text>(MIN or HRE)
			</td>
		</tr>
		<tr>
			<td>
				delivery frequency quantity/hour (number)
			</td>
			<td>
				<html:text	property="editorList[0].cosDeliveryFqyQtyPerHre"  styleId="cosDeliveryFqyQtyPerHre"  style="width: 30; margin-top: 2px; padding: 0px; font-size: 11px">
				</html:text>
				<html:text	property="editorList[0].cosDeliveryFqyQtyPerHreUom"  styleId="cosDeliveryFqyQtyPerHreUom"  style="width: 30; margin-top: 2px; padding: 0px; font-size: 11px">
				</html:text>(TO or LKW)
			</td>
		</tr>
		<tr>
			<td>
				delivery time tolerance (number)
			</td>
			<td>
				<html:text	property="editorList[0].cosDeliveryTimeTolerance"  styleId="cosDeliveryTimeTolerance"  style="width: 30; margin-top: 2px; padding: 0px; font-size: 11px">
				</html:text>
				<html:text	property="editorList[0].cosDeliveryTimeToleranceUom"  styleId="cosDeliveryTimeToleranceUom"  style="width: 30; margin-top: 2px; padding: 0px; font-size: 11px">
				</html:text>(MIN or HRE)
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<input id="buttonCancel" type="button" class="ViewButtonStyle"
						value="<fmt:message key="buttons.cancel" />" />&nbsp;
			</td>
			<td colspan="2">
				<input id="buttonGenerate" type="button" class="ViewButtonStyle"
						value="<fmt:message key="bestellung.custorder.popup1.button.generateproposals" />"
						/>&nbsp;
			</td>
		</tr>
	</table>

	<div id="divSplitResults"></div> 
	
</html:form>

</div>



<%-- END $RCSfile: SingleDeliveryOrderPopup1Tile.jsp,v $ --%>
