<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: BimodalRailPart.jsp,v $ --%> 

<%@ include file="../includeTLDs.jsp" %>

<%@page import="com.crh.logon.order.web.ajax.OrderAjaxService"%>
<%@page import="com.crh.logon.constants.Constant" %>
<%@page import="com.crh.logon.persistence.entities.common.OrderEntity"%>
<%@page import="com.crh.logon.order.web.forms.bestellung.BestellungForm"%>

<c:set var="form" scope="page" value="${order_bestellung_BestellungForm}" />
 
<c:if test="${invalidBimodalTruckPart}">
	<script type="text/javascript">
		bimodalRailPartActive = false;
		saveOrderBtn.enable();
		Ext.get('divEditors').unmask();
	</script>
</c:if>

<c:if test="${!invalidBimodalTruckPart}">

<%
	long idx = 1l;
	long itemIdx = 0l;
	
	String editorStr = "editorList[" + idx + "]";
	String itemStr = editorStr + ".orderItems[" + itemIdx + "]";
%>

<c:set var="idx" value="1"/>
<c:set var="itemIdx" value="0"/>
<c:set var="isBimodalRail" value="${idx == 1}"/>
<c:set var="editorList" value="${form.editorList}"/>
<c:set var="editor" value="${editorList[idx]}"/>
<c:set var="isEditorDisabled" value="false"/>
<c:set var="textareaCls" value="x-form-field comments" scope="request"/>

<script type="text/javascript">

Ext.onReady(function() {
		    			
	var trEditor = editorList[<%=(idx - 1)%>];

	trEditor.orderItems[0].sapMatCombo.disable();
	trEditor.orderItems[0].prodPlantCombo.disable();
	//trEditor.orderItems[0].quantityStr.disable(); TODO clarify if it should remain editable or not
	//trEditor.orderItems[0].uomCombo.disable(); TODO clarify if it should remain editable or not
	trEditor.delTypeCombo.disable();
	trEditor.supplyPlantCombo.disable();
	trEditor.spCombo.disable();
	trEditor.biRailItemCombo.disable();

	jsonData = <c:out value="${form.jsonData}" escapeXml="false"/>;
	
	var editor = {};
	var item = {};

	editor.orderItems = Array();
	editor.orderItems[<%=itemIdx%>] = item;

	// --- QUANTITY (cp) ---
	var invalidQuantityStr = '<fmt:message key="bestellung.error.invalid.quantity"/>'.replace('{0}', editorLocatorMsg.replace('@', 1));
	item.quantityStr = new Ext.form.NumberField({
		invalidText: invalidQuantityStr,
		id: '<%=itemStr%>.quantityStr',
		applyTo: '<%=itemStr%>.quantityStr',
		allowBlank: true,
		allowNegative: false,
		allowDecimals: true,
		decimalPrecision: 3,
		minValue: '<%= Constant.CUSTORDER_ORDERED_QTY_MIN_VALUE_STR %>',
		minText: invalidQuantityStr,
		maxValue: '<%= Constant.CUSTORDER_ORDERED_QTY_MAX_VALUE_STR %>',
		maxText: invalidQuantityStr,
		maxLength: 9,
		maxLengthText: invalidQuantityStr,
		validator: function(value) {
			if (getDecimalPrecision(value) > this.decimalPrecision) {
				return '<fmt:message key="bestellung.error.invalid.decimal.precision"/>'.replace('{0}', this.decimalPrecision);
			}
			return true;
		},
		selectOnFocus: true,
		disabled: false,
		style: 'width: 35px; text-align: right'
	});
	
	// --- Supply plant ---
	editor.supplyPlantCombo = new Ext.form.ComboBox({
		mode: 'local',
		editable: false,
		forceSelection: true,
		id: 'supplyPlantCombo<%=idx%>',
		name: 'editorList[<%=idx%>].supplyPlantCd',
	    transform:'editorList[<%=idx%>].supplyPlantCd',
	    triggerAction: 'all',
	    store: new Ext.data.JsonStore({
        	fields: [
        		{name: 'plantCd', mapping: 'plantCd'}, 
        		{name: 'plantName', mapping: 'plantName'}, 
        		{name: 'productionPlant', mapping:'productionPlant'}],
        	emptyItem: {'plant' : {'plantName': '...', 'plantCd': '-1'}},
        	data: jsonData[<%=idx%>].supplyPlantList
    	}),
	    tpl: '<tpl for="."><div class="x-combo-list-item">{plantName} <tpl if="plantName != \'...\'"><span class="dynlabel">({plantCd})</span></tpl></div></tpl>',
	    width: 100,
	    listWidth: 180,
	    valueField: 'plantCd',
	    displayField: 'plantName',
	    disabled: false,
	    value: '<c:out value="${editor.supplyPlantCd}"/>'
	});
	

	// --- When the Supply plant is changed ---
	editor.supplyPlantCombo.on('select', function(combo, record) {
		
		saveOrderBtn.disable();
		
		// --- Reset SP list ---
		editor.spCombo.disable();
		editor.spCombo.clearValue();
		
		// --- Reset LP list ---
		item.lpCombo.disable();
		item.lpCombo.clearValue();

		// --- Reset Carrier list ---
		var commercialCarrierValue = editor.commercialCarrierCombo.getValue();
		editor.commercialCarrierCombo.disable();
		editor.commercialCarrierCombo.clearValue();

    	OrderAjaxService.getCollectedEditorData({
    		'editorIdx': <%=idx%>, 
			'orderItemIdx': <%=itemIdx%>,
    		'sapMatId': null, 
    		'supplyPlantCd': (record.data.plantCd || '-1'), 
    		'prodPlantCd': null, 
    		'spId': -1, 
    		'poolId': null, 
    		'quantity': editor.orderItems[<%=itemIdx%>].quantityStr.getValue(), 
    		'delFromDat': editorList[0].getDelFromDateTime(), 
    		'delToDat': editorList[0].getDelToDateTime(), 
    		'delTypeCd': editor.delTypeCombo.getValue(),
    		'uomCd': null}, 
    	function(res) {
    		editor.spCombo.store.loadData(res.spList);
    		item.lpCombo.store.loadData(res.lpList);
    		editor.spCombo.preselectValue(null, true);
    		editor.commercialCarrierCombo.store.loadData(res.commercialCarrierList);
    		editor.commercialCarrierCombo.preselectValue(commercialCarrierValue, true);
			if (!Ext.isEmpty(res.supplyPlantCd)) {
				editor.spCombo.enable();
				item.lpCombo.enable();
			}
			editor.commercialCarrierCombo.enable();

			toggleRailDeliveryData(1, {});
			
			jsonData[<%=idx%>] = res;
    	});
	});
	
	// -------------------------------------------------------------------------
	// Shipping point
	// -------------------------------------------------------------------------
	// cloned
	editor.spCombo = new Ext.form.ComboBox({
		mode: 'local',
		editable: false,
		forceSelection: true,
		id: 'spCombo<%=idx%>',
		name: 'editorList[<%=idx%>].orderHelper.shippingpointId',
	    transform:'editorList[<%=idx%>].orderHelper.shippingpointId',
	    triggerAction: 'all',
	    store: new Ext.data.JsonStore({
	    	fields: [{name: 'shippingpointId', mapping: 'shippingpointId'}, 
	    			{name: 'shippingpointName', mapping: 'shippingpointName'}, 
	    			{name: 'coordLat', mapping: 'coordLat'}, 
	    			{name: 'coordLon', mapping: 'coordLon'},
	    			{name: 'plantCd', mapping: 'plantCd'},
	    			{name: 'railwayStation'}
	    			],
	    	emptyItem: { 'shippingpointId': -1, 'shippingpointName' : '...', plant: {}, railwayStation: {}},
	    	data : jsonData[<%=idx%>].spList
		}),
	    disabled: false,
	    width: 130,
	    listWidth: 200,
	    tpl: '<tpl for="."><div class="x-combo-list-item">{shippingpointName} <tpl if="shippingpointId != -1"><span class="dynlabel">(LKW<tpl if="railwayStation != null">,BW</tpl>)</span></tpl></div></tpl>',
	    valueField: 'shippingpointId',
	    displayField: 'shippingpointName'
	});
	
	// Event handler(s)
	editor.spCombo.on('select', function(combo, record) {
		
		saveOrderBtn.disable();
		
		item.lpCombo.disable();
		item.lpCombo.clearValue();
	    
		editor.commercialCarrierCombo.disable();
		var commercialCarrierValue = editor.commercialCarrierCombo.getValue();
	    
		editor.supplyPlantCombo.preselectValue(record.data.plantCd, true);
		
		OrderAjaxService.getCollectedEditorData({
			'editorIdx': <%=idx%>, 
			'orderItemIdx': <%=itemIdx%>,
			'sapMatId': null, 
			'supplyPlantCd': null, 
			'prodPlantCd': null, 
			'spId': (record.data.shippingpointId || -1), 
			'poolId': null, 
			'quantity': editor.orderItems[<%=itemIdx%>].quantityStr.getValue(), 
			'delTypeCd': editor.delTypeCombo.getValue(), 
    		'delFromDat': editorList[0].getDelFromDateTime(), 
    		'delToDat': editorList[0].getDelToDateTime(), 
			'uomCd': null}, 
		function(res) {

			toggleRailDeliveryData(1, res.railData);
			
			item.lpCombo.store.loadData(res.lpList);
			item.lpCombo.enable();
			
			editor.commercialCarrierCombo.store.loadData(res.commercialCarrierList);
			editor.commercialCarrierCombo.preselectValue(commercialCarrierValue, true);
			editor.commercialCarrierCombo.enable();

			// Allow saving only when SP is selected
			if (combo.getSelectedRecord() != null) {
				saveOrderBtn.enable();
			}
			
			jsonData[<%=idx%>] = res;
		});
		
	});
	editor.spCombo.preselectValue('<c:out value="${editor.orderHelper.shippingpointId}"/>', true);

<%-- how to handle for bimodal? 
	// Default value and default Google Map Transport Duration
	if (!editor.spCombo.isEmptySelection()) {
		GMapAPILoader.onAPIReady(function() {
			updateDurationTimeAndMap(editor.spCombo.getSelectedRecord(), editor.delTypeCombo.getValue(), jsonData[0].railData.durationMinutes, jsonData[0].railData.distanceMeters, jsonData[0].durationCorrectionFactor) // TODO
		});
	}
--%>
	
	// --- LOADING POINT ---
	item.lpCombo = new Ext.form.ComboBox({
		mode: 'local',
		editable: false,
		forceSelection: true,
		id: '<%=itemStr%>.lpCombo',
		name: '<%=itemStr%>.loadingpointId',
	    transform:'<%=itemStr%>.loadingpointId',
		allowBlank: true,
	    triggerAction: 'all',
	    store: new Ext.data.JsonStore({
	    	fields: ['loadingpointId', 'loadingpointName'],
		    emptyItem: {'loadingpointName' : '...'},
	    	data : jsonData[<%=idx%>].lpList 
		}),
	    disabled: false,
	    width: 130,
	    listWidth: 200,
	    valueField: 'loadingpointId',
	    displayField: 'loadingpointName'
	});
	item.lpCombo.preselectValue('<c:out value="${editor.orderItems[itemIdx].loadingpointId}"/>', true);
	
	// --- DELIVERY TYPE ---
	editor.delTypeCombo = new Ext.form.ComboBox({
		mode: 'local',
		editable: false,
		forceSelection: true,
		id: 'delTypeCombo<%=idx%>',
	    triggerAction: 'all',
	    transform: 'editorList[<%=idx%>].orderHelper.deliveryTypeCd',
	    disabled: true,
	    width: 100,
	    listWidth: 160
	});
	
	// --- UoM ---
	item.uomCombo = new Ext.form.ComboBox({
		mode: 'local',
		editable: false,
		forceSelection: true,
		id: '<%=itemStr%>.uomCombo',
	    transform: '<%=itemStr%>.uomCd',
	    triggerAction: 'all',
	    store: new Ext.data.JsonStore({
        	fields: [
        		{name: 'uomCd', mapping: 'uomTxPK.uomBs.uomCd'}, 
        		{name: 'txt', mapping: 'txt'}],
        		emptyItem: {'txt': '...', 'uomTxPK': {'uomBs': {'uomCd': '-1'}}},
        	data : jsonData[<%=idx%>].uomList 
    	}),	    
	    valueField: 'uomCd',
	    displayField: 'txt',	    	
	    disabled: true,
	    width: 75
	});
	item.uomCombo.preselectValue('<c:out value="${editor.orderItems[itemIdx].uomCd}"/>', true);

	// --- POOL ---
	// cloned
	editor.poolCombo = new Ext.form.ComboBox({
		mode: 'local',
		editable: false,
		forceSelection: true,
		id: 'poolCombo<%=idx%>',
		name: 'editorList[<%=idx%>].orderHelper.poolId',
	    transform:'editorList[<%=idx%>].orderHelper.poolId',
		allowBlank: true,
	    triggerAction: 'all',
	    store: new Ext.data.JsonStore({
        	fields: [
        		{name: 'poolId', mapping: 'poolTxPK.poolId'}, 
        		{name: 'name', mapping: 'name'}
        	],
        	emptyItem: {'name': '...', 'poolTxPK' : { 'poolId': -1}},
        	data : jsonData[<%=idx%>].poolList 
    	}),
	    disabled: false,
	    valueField: 'poolId',
	    displayField: 'name',
	    width: 130,
	    listwidth: 150
	});
	editor.poolCombo.preselectValue('<c:out value="${editor.orderHelper.poolId}"/>', true);
	
	// cloned
	editor.poolCombo.on('select', function(combo, record) {
		
		saveOrderBtn.disable();
		
		editor.commercialCarrierCombo.disable();
		editor.commercialCarrierCombo.clearValue();
		
    	OrderAjaxService.getCollectedEditorData({
    		'editorIdx': <%=idx%>, 
			'orderItemIdx': <%=itemIdx%>,
    		'sapMatId': null, 
    		'supplyPlantCd': null,
    		'prodPlantCd': null,
    		'spId': null, 
    		'poolId': (record.data.poolId || -1), 
    		'quantity': editor.orderItems[<%=itemIdx%>].quantityStr.getValue(), 
    		'delFromDat': editorList[0].getDelFromDateTime(), 
    		'delToDat': editorList[0].getDelToDateTime(), 
    		'delTypeCd': editor.delTypeCombo.getValue(),
    		'uomCd': null}, 
    	function(res) {
    		editor.commercialCarrierCombo.store.loadData(res.commercialCarrierList);
    		editor.commercialCarrierCombo.enable();
    		
			jsonData[<%=idx%>] = res;
			
			updateAutoFlags(record, <%=idx%>);
			saveOrderBtn.enable();
    	});
	});
	
	// --- CARRIER (Transporteur) ---
	// cloned
	editor.commercialCarrierCombo = new Ext.form.ComboBox({
		mode: 'local',
		forceSelection: true,
		id: 'commercialCarrierCombo<%=idx%>',
		name: 'editorList[<%=idx%>].orderHelper.commCarrierId',
		transform:'editorList[<%=idx%>].orderHelper.commCarrierId',
		allowBlank: true,
		selectOnFocus: true,
		editable: true,
	    minChars: 2,
	    triggerAction: 'all',
	    store: new Ext.data.JsonStore({
        	fields: ['carrierId', 'displayName'],
		    emptyItem: {'displayName' : '...'},
        	data : jsonData[<%=idx%>].commercialCarrierList
    	}),
	    disabled: false,
	    valueField: 'carrierId',
	    displayField: 'displayName',
	    width: 130,
	    listwidth: 150
	});
	editor.commercialCarrierCombo.preselectValue('<c:out value="${editor.orderHelper.commCarrierId}"/>', true);
	editor.commercialCarrierCombo.on('select', function(combo, record) {
		updateAutoFlags(record, <%=idx%>);
	});
	
	editor.billToCombo = new Ext.form.ComboBox({
		mode: 'local',
		editable: false,
		forceSelection: true,
		id: 'billToCombo<%=idx%>',
		name: 'editorList[<%=idx%>].orderHelper.billtoSapBpId',
	    transform:'editorList[<%=idx%>].orderHelper.billtoSapBpId',
		allowBlank: true,
	    triggerAction: 'all',
	    store: new Ext.data.JsonStore({
        	fields: [
        		{name: 'name1', mapping: 'billtoSapBp.name1'},
        		{name: 'sapBpId', mapping: 'billtoSapBp.sapBpId'},
        		{name: 'name2', mapping: 'billtoSapBp.name2'},
        		{name: 'sapId', mapping: 'billtoSapBp.sapId'}
        	],
        	emptyItem: {'billtoSapBp' : {'name1' : '...', 'name2' : '', 'sapId' : '-'}},
        	data : jsonData[<%=idx%>].billtoList 
    	}),
	    disabled: false,
	    valueField: 'sapBpId',
	    displayField: 'name1',
	    tpl: '<tpl for="."><div class="x-combo-list-item">{name1} <tpl if="name2 != null">{name2}</tpl><tpl if="name1 != \'...\'"><span class="dynlabel">({sapId})</span></tpl></div></tpl>',
	    width: 100,
	    listWidth: 200
	});
	editor.billToCombo.preselectValue('<c:out value="${editor.orderHelper.billtoSapBpId}"/>', true);

	editor.payerCombo = new Ext.form.ComboBox({
		mode: 'local',
		editable: false,
		forceSelection: true,
		id: 'payerCombo<%=idx%>',
		name: 'editorList[<%=idx%>].orderHelper.payerSapBpId',
		transform:'editorList[<%=idx%>].orderHelper.payerSapBpId',
		allowBlank: true,
		triggerAction: 'all',
		store: new Ext.data.JsonStore({
			data : jsonData[<%=idx%>].payerList,
			fields: [
				{ name: 'name1', mapping: 'name1' },
				{ name: 'name2', mapping: 'name2' },
				{ name: 'sapBpId', mapping: 'sapBpId' },
				{ name: 'sapId', mapping: 'sapId' }
			],
			emptyItem: {
				'name1' : '...',
				'name2' : '',
				'sapBpId' : '',
				'sapId' : ''
			}
		}),
		disabled: false,
		valueField: 'sapBpId',
		displayField: 'name1',
		tpl: new Ext.XTemplate(
				'<tpl for=".">',
					'<div class="x-combo-list-item">',
						'{name1} ',
						'<tpl if="name2 != null">{name2}</tpl> ',
						'<tpl if="name1 != \'...\'">',
							'<span class="dynlabel">({sapId})</span>',
						'</tpl>',
					'</div>',
				'</tpl>'),
		width: 100,
		listWidth: 200
	});
	editor.payerCombo.preselectValue('<c:out value="${editor.orderHelper.payerSapBpId}"/>', true);
	
	editorList[<%=idx%>] = editor;
	
	updateByDeliveryDate(trEditor.delFromDat.getValue(), trEditor.delToDat.getValue());
	
	saveOrderBtn.changeLabelTo('<fmt:message key="bestellung.button.savebimodalorder"/>');
	saveOrderBtn.enable();
	
	Ext.DomHelper.insertAfter(
		saveOrderBtn.id,
		{
			tag: 'input',
			type: 'button',
			id: 'cancelBimodalButton',
			cls: 'ViewButtonStyle',
			style: 'margin-left: 4px',
			value: '<fmt:message key="buttons.cancel"/>',
			onclick: 'cancelBimodalRailPart()'
		}
	);
	
	Ext.get('divEditors').unmask();	
});


function cancelBimodalRailPart() {
	OrderAjaxService.cancelBimodalOrderPartEditor(function(res) {
		saveOrderBtn.changeLabelBack();
		saveOrderBtn.enable();
		
		Ext.get('cancelBimodalButton').remove();
		
		var trEditor = editorList[0]; 
		
		trEditor.orderItems[0].sapMatCombo.enable();
		trEditor.orderItems[0].prodPlantCombo.enable();
		trEditor.delTypeCombo.enable();
		trEditor.supplyPlantCombo.enable();
		trEditor.spCombo.enable();
		trEditor.biRailItemCombo.enable();
		
		bimodalRailPartActive = false;

		Ext.get('divOrderEditor1').remove();
		
		editorList.pop();
	});
}

</script>

<html:form action="bestellung/Bestellung.do" styleId="bestellungForm3">

        <!-- NEW(ER) ORDER FORM BEGIN -->
<table style="width:1224px; border-spacing: 0px; margin: 0px 2px 2px 2px; border-top: 1px solid #FFFFFF" cellspacing="0">

		<tr style="background-color: #BBBBBB">
        	<td class="itemHeaderCell" style="border-right: 2px solid #FFFFFF">&nbsp;</td>
        	<td class="itemHeaderCell" style="border-right: 1px solid #FFFFFF"><fmt:message key="fields.produkt.produkt"/></td>
        	<td class="itemHeaderCell" style="border-right: 1px solid #FFFFFF"><fmt:message key="fields.konditionen.produktionswerk"/></td>
        	<td class="itemHeaderCell" style="border-right: 1px solid #FFFFFF; text-align: right">#</td>
        	<td class="itemHeaderCell" style="border-right: 1px solid #FFFFFF"><fmt:message key="fields.uom.uom"/></td>
        	<td class="itemHeaderCell" style="border-right: 1px solid #FFFFFF"><fmt:message key="fields.order.delivery.time"/></td>
        	<td class="itemHeaderCell" style="border-right: 1px solid #FFFFFF"><fmt:message key="fields.konditionen.liefertyp"/></td>
        	<td class="itemHeaderCell" style="border-right: 1px solid #FFFFFF"><fmt:message key="fields.defaults.ladestrasse"/></td>
        	<td class="itemHeaderCell" style="border-right: 1px solid #FFFFFF">Pool / Carrier</td>
        	<td class="itemHeaderCell">
			<fmt:message key="fields.billto.billto"/> / <fmt:message key="fields.payer.payer"/>
        	</td>
		</tr>

        <tr style="background-color: #EEEEEE">
        	<td style="width: 22px; border-right: 2px solid #FFFFFF">
        		<img src="<crh-common:url value='/images/gray/s.gif'/>" style="width: 16px; margin-top: 3px; margin-left: 3px"/>
        	</td>
            <td style="width: 142px">
            	<nobr>
					&nbsp;<span style="font-size: 12px"><c:out value="${biRailMaterialDesc}"/></span><br/>
	           		<html:text property="editorList[${idx}].order.contactPersonTxt" styleId="editorList[${idx}].order.contactPersonTxt" styleClass="x-form-text" style="width: 120px; margin-top: 6px"/>
            	</nobr>
            </td>
            <td style="width: 115px">
            	<nobr>
            		<span style="font-size: 12px"><c:out value="${editorList[idx].orderHelper.productionPlantName}"/></span>
	           		<html:hidden property="editorList[${idx}].orderItems[${itemIdx}].sapMatId" styleId="editorList[${idx}].orderItems[${itemIdx}].sapMatId"/>
					<html:hidden property="editorList[${idx}].orderItems[${itemIdx}].prodPlantCd" styleId="editorList[${idx}].orderItems[${itemIdx}].prodPlantCd"/>
            	</nobr>
            </td>
           	<!-- QUANTITY -->
            <td style="width: 40px; text-align: center">
            	<html:text property="editorList[${idx}].orderItems[${itemIdx}].quantityStr" styleId="editorList[${idx}].orderItems[${itemIdx}].quantityStr" style="margin-bottom: 6px"/><br/>
      	    	<html:checkbox property="editorList[${idx}].order.onHoldOrderAndLockedFlag" styleId="editorList[${idx}].order.onHoldOrderAndLockedFlag"/>
            </td>
           	<!-- UOM + SILO -->
            <td style="width: 85px">
            	<div class="marginbottom">
					<html:hidden property="editorList[${idx}].orderItems[${itemIdx}].uomCd" styleId="editorList[${idx}].orderItems[${itemIdx}].uomCd"/>            					
				</div>
            </td>
            <!-- DELIVERY FROM/TO -->
            <td style="width: 170px">
            	<div id="divRailDeliveryData1" style="margin-left: 2px; margin-top: 5px">
	   				<div id="divActualDeliveryDatWarn1" class="hiddenDiv" style="float: left; width: 20px"><img src="<crh-common:url value='/images/warning2.png'/>"/></div>
	   				<div id="divActualDeliveryDatOk1" class="hiddenDiv" style="float: left; width: 20px"><img src="<crh-common:url value='/images/silk/accept.png'/>"/></div>
	   				<div id="divActualDeliveryDatTxt1">n.a.</div>
	   				<div id="divEarliestDeliveryDatTxt1">n.a.</div>
   				</div>
            </td>
			<%-- DELIVERY TYPE --%>
            <td style="width: 105px">
            	<div class="marginbottom">
				<html:select size="1" property="editorList[${idx}].orderHelper.deliveryTypeCd" styleId="editorList[${idx}].orderHelper.deliveryTypeCd">
            		<html:option value="-1">...</html:option>
					<c:forEach var="deliveryType" varStatus="status" items="${form.deliveryTypeList}">
						<html:option value="${deliveryType.deliveryTypeTxPK.deliveryTypeBs.deliveryTypeCd}"><c:out value="${deliveryType.txt}"/></html:option>
					</c:forEach>
				</html:select>
				</div>
				<html:hidden property="editorList[${idx}].supplyPlantCd" styleId="editorList[${idx}].supplyPlantCd"/>
            </td>
            <%-- SHIPPING POINT / LOADING POINT --%>
            <td style="width: 135px">
            	<nobr>
            	<div class="marginbottom">
            		<html:hidden property="editorList[${idx}].orderHelper.shippingpointId" styleId="editorList[${idx}].orderHelper.shippingpointId"/>
            	</div>
            	<html:hidden property="editorList[${idx}].orderItems[${itemIdx}].loadingpointId" styleId="editorList[${idx}].orderItems[${itemIdx}].loadingpointId"/>
            	</nobr>
            </td>
            <%-- POOL / CARRIER --%>
      	    <td style="width: 152px">
      	    	<table cellpadding="0" cellspacing="0" style="padding: 0px; border-spacing: 0px">
      	    	<tr>
      	    		<td style="padding: 0px; border-spacing: 0px">
						<html:checkbox onclick="updateAutoFlags(this, ${idx});" property="editorList[${idx}].autoPoolAssignmentFlag" styleId="editorList[${idx}].autoPoolAssignmentFlag" style="margin-top: 4px; margin-right: 3px" value="true" disabled="${isEditorDisabled || (empty editor.orderHelper.poolId) || editor.orderHelper.poolId == 0 || editor.orderHelper.poolId == -1}"/>
	      	    	</td>
	      	    	<td width="0" style="padding: 0px; border-spacing: 0px">
	            		<div class="marginbottom">
      	    				<html:hidden property="editorList[${idx}].orderHelper.poolId" styleId="editorList[${idx}].orderHelper.poolId" style="margin-bottom: 2px"/>
      	    			</div>
	      	    	</td>
      	    	</tr>
      	    	<tr>
      	    		<td width="0" style="padding: 0px; border-spacing: 0px">
						<html:checkbox onclick="updateAutoFlags(this, ${idx});" property="editorList[${idx}].autoCarrierAssignmentFlag" styleId="editorList[${idx}].autoCarrierAssignmentFlag" style="margin-top: 4px; margin-right: 3px" value="true" disabled="${isEditorDisabled || (empty editor.orderHelper.commCarrierId) || editor.orderHelper.commCarrierId == 0 || editor.orderHelper.commCarrierId == -1}"/>
	      	    	</td>
	      	    	<td width="0" style="padding: 0px; border-spacing: 0px">
	      	    		<html:hidden property="editorList[${idx}].orderHelper.commCarrierId" styleId="editorList[${idx}].orderHelper.commCarrierId"/>
	      	    	</td>
      	    	</tr>
      	    	</table>
           	 </td>
      	    <td style="width: 105px; text-align: center">
				<div class="marginbottom">
					<html:hidden property="editorList[${idx}].orderHelper.billtoSapBpId" styleId="editorList[${idx}].orderHelper.billtoSapBpId"/>
				</div>
				<div style="margin-bottom: 6px">
					<html:hidden property="editorList[${idx}].orderHelper.payerSapBpId" styleId="editorList[${idx}].orderHelper.payerSapBpId"/>
				</div>
      	    	<html:checkbox property="editorList[${idx}].order.releaseSapFlag" styleId="editorList[${idx}].order.releaseSapFlag" disabled="true"/>
				&nbsp;
      	    	<html:checkbox property="editorList[${idx}].order.freeDeliveryFlag" styleId="editorList[${idx}].order.freeDeliveryFlag" disabled="true"/>
      	    </td>
		</tr>
		
		<!-- Comments -->
		<tr style="background-color: #EEEEEE">
        	<td style="width: 22px; border-right: 2px solid #FFFFFF">
        		<img src="<crh-common:url value='/images/gray/s.gif'/>" style="width: 16px; margin-top: 3px; margin-left: 3px"/>
        	</td>
			<td colspan="10" style="padding-top: 0px">
				<table class="compact" style="width: 100%; border-spacing: 0px">
					<tr>
						<td style="width: 20%; padding: 0px 7px 0px 0px">
							<p class="itemCommentLabel"><fmt:message key="dispokarte.texte.auftragsmanagement"/></p>
							<div id="divItemNoteOrderMgmt<%=itemIdx%>" style="background: #FFFFFF url(<crh-common:url value='/images/default/form/text-bg.gif'/>) repeat-x scroll 0 0; border: 1px solid #B5B8C8; height: 42px; overflow: auto; color: #888888; margin-top: 2px">
								<c:out value="${editor.orderItems[itemIdx].itemNoteOrderMgmt}"/>
							</div>						
						</td>
						<td style="width: 20%; padding: 0px 7px 0px 0px">
							<c:set var="textarealength"><%=OrderEntity.DBSIZE_NOTE_DELIVERY + ""%></c:set>
							<p class="itemCommentLabel"><fmt:message key="dispokarte.texte.zusatzlieferschein"/></p>
							<html:textarea	property="editorList[${idx}].orderItems[${itemIdx}].itemNoteDelivery" onkeypress="return checkMaxLength(event, this, ${textarealength})" styleId="editorList[${idx}].orderItems[${itemIdx}].itemNoteDelivery" styleClass="${textareaCls}" style="width: 100%; margin-top: 2px; padding: 0px; font-size: 11px"></html:textarea>
						</td>
						<td style="width: 20%; padding: 0px 7px 0px 0px">
							<c:set var="textarealength"><%=OrderEntity.DBSIZE_NOTE_DISPATCHING + ""%></c:set>
							<p class="itemCommentLabel"><fmt:message key="dispokarte.texte.poolspediteur"/></p>
							<html:textarea	property="editorList[${idx}].orderItems[${itemIdx}].itemNoteDispatching" onkeypress="return checkMaxLength(event, this, ${textarealength})" styleId="editorList[${idx}].orderItems[${itemIdx}].itemNoteDispatching" rows="2" styleClass="${textareaCls}" style="width: 100%; margin-top: 2px; padding: 0px; font-size: 11px"></html:textarea>
						</td>
						<td style="width: 20%; padding: 0px 7px 0px 0px">
							<p class="itemCommentLabel"><fmt:message key="dispokarte.texte.chauffeur"/></p>
							<c:set var="textarealength"><%=OrderEntity.DBSIZE_NOTE_TRUCK_DRIVER + ""%></c:set>
							<html:textarea	property="editorList[${idx}].orderItems[${itemIdx}].itemNoteTruckDriver" onkeypress="return checkMaxLength(event, this, ${textarealength})" styleId="editorList[${idx}].orderItems[${itemIdx}].itemNoteTruckDriver" rows="2" styleClass="${textareaCls}" style="width: 100%; margin-top: 2px; padding: 0px; font-size: 11px"></html:textarea>
						</td>
						<td style="width: 20%; padding: 0px">
							<p class="itemCommentLabel"><fmt:message key="dispokarte.texte.bahndisposition"/></p>
							<c:set var="textarealength"><%=OrderEntity.DBSIZE_NOTE_RAILWAY_DISP + ""%></c:set>
							<html:textarea	property="editorList[${idx}].orderItems[${itemIdx}].itemNoteRailway" onkeypress="return checkMaxLength(event, this, ${textarealength})" styleId="editorList[${idx}].orderItems[${itemIdx}].itemNoteRailway" rows="2" styleClass="${textareaCls}" style="width: 100%; margin-top: 2px; padding: 0px; font-size: 11px"></html:textarea>
						</td>
					</tr>
				</table>
			</td>
				
	</table>

		</html:form>
		
</c:if>	