<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: AddedOrderItem.jsp,v $ --%> 

<%@ include file="../includeTLDs.jsp" %>

<%@page import="com.crh.logon.order.web.ajax.OrderAjaxService"%>
<%@page import="com.crh.logon.constants.Constant" %>
<%@page import="com.crh.logon.persistence.entities.common.OrderEntity"%>
<%@page import="com.crh.logon.order.web.forms.bestellung.BestellungForm"%>
<%@page import="com.crh.logon.business.ordercreation.dto.DeliverySettingDTO"%>

<c:set var="form" scope="page" value="${order_bestellung_BestellungForm}" />

<%

	Object idxObj = pageContext.getAttribute("editorIdx", PageContext.REQUEST_SCOPE);
	Object itemIdxObj = pageContext.getAttribute("orderItemIdx", PageContext.REQUEST_SCOPE);
	
	long idx = idxObj == null ? 0 : ((Long)idxObj).longValue();
	long itemIdx = itemIdxObj == null ? 0 : ((Long)itemIdxObj).longValue(); 
	
	String editorStr = "editorList[" + idx + "]";
	String itemStr = editorStr + ".orderItems[" + itemIdx + "]";
	
	pageContext.setAttribute("idx", idx);
	pageContext.setAttribute("itemIdx", itemIdx);
	
%>

<c:set var="isBimodalRail" value="false"/>
<c:set var="editorList" value="${form.editorList}"/>
<c:set var="editor" value="${editorList[idx]}"/>
<c:set var="item" value="${editor.orderItems[itemIdx]}"/>
<c:set var="textareaCls" value="x-form-field comments" scope="request"/>

<c:set var="DELIVERY_SETTING_BASED_ON_CONTRACT"><%=DeliverySettingDTO.DELIVERY_SETTING_BASED_ON_CONTRACT%></c:set>
<c:set var="DELIVERY_SETTING_BASED_ON_CONTRACT_COMBINED_RESTRICTION"><%=DeliverySettingDTO.DELIVERY_SETTING_BASED_ON_CONTRACT_COMBINED_RESTRICTION%></c:set>
<c:set var="DELIVERY_SETTING_BASED_ON_RESTRICTION"><%=DeliverySettingDTO.DELIVERY_SETTING_BASED_ON_RESTRICTION%></c:set>
<c:set var="DELIVERY_SETTING_NOT_DEFINED"><%=DeliverySettingDTO.DELIVERY_SETTING_NOT_DEFINED%></c:set>

<c:set var="RESOLUTION_FROM_PRICING_GROUP_TYPE_GENERIC_MATERIAL"><%=DeliverySettingDTO.RESOLUTION_FROM_PRICING_GROUP_TYPE_GENERIC_MATERIAL%></c:set>
<c:set var="RESOLUTION_EXPLICIT_IN_CONTRACT"><%=DeliverySettingDTO.RESOLUTION_EXPLICIT_IN_CONTRACT%></c:set>
<c:set var="RESOLUTION_FROM_DIVISION_TYPE_GENERIC_MATERIAL"><%=DeliverySettingDTO.RESOLUTION_FROM_DIVISION_TYPE_GENERIC_MATERIAL%></c:set>


<html:form action="bestellung/Bestellung.do" styleId="bestellungForm2Item${itemIdx}">

<script type="text/javascript">
	
Ext.onReady(function() {

	var item = {};
	var editor = editorList[<%=idx%>];	
	var productList = <c:out value="${item.jsonProductList}" escapeXml="false"/>;

	// -----------------------------------------------------------------------------------
	// SAP Material
	// -----------------------------------------------------------------------------------
	
	 var materialComboTemplate = new Ext.XTemplate(	    
	    	'<table class="classicblue" style="width:100%"><thead><tr>',
	    	'<th><fmt:message key="bestellung.materialcontractcombo.material"/></th>',
	    	'<th><fmt:message key="bestellung.materialcontractcombo.contract"/></th>',	    	
	    	'<th><fmt:message key="bestellung.materialcontractcombo.salesarea"/></th>',
	    	'<th><fmt:message key="bestellung.materialcontractcombo.productionPlant"/></th>',
	    	'<th><fmt:message key="bestellung.materialcontractcombo.packagetype"/></th>',
	    	'<th><fmt:message key="bestellung.materialcontractcombo.quantity"/></th>',
	    	'<th><fmt:message key="bestellung.materialcontractcombo.uom"/></th>',
	    	'<th><fmt:message key="bestellung.materialcontractcombo.contractMaterial"/></th>',
	    	'<th><fmt:message key="bestellung.materialcontractcombo.basedOn"/></th>',	    	
	    	'</tr></thead><tbody>',
	        '<tpl for=".">',
		        	'<tr class="listitem zebra{[xindex % 2]}">',
		        		'<td><tpl if="!this.isNotAvailable(sapMaterialId)">{txt} ({sapMaterialNo})</tpl></td>',
		        		'<td><tpl if="!this.isNotAvailable(sapContractId)">{sapContractNo} ({contractItemNo})</tpl></td>',
		        		'<td><tpl if="!this.isNotAvailable(sareaId)">{sareaDisplayName}</tpl></td>',
		        		'<td><tpl if="!this.isNotAvailable(productionPlantCd)">{productionPlantName} ({productionPlantCd})</tpl></td>',		        		
		        		'<td><tpl if="!this.isNotAvailable(materialPackageCd)">{materialPackageTxt}</tpl></td>',
		        		'<td><tpl if="!this.isNotAvailable(quantity)">{quantity}</tpl></td>',
		        		'<td><tpl if="!this.isNotAvailable(uomCd)">{uomCd}</tpl></td>',
		        		'<td>{[this.getResolutionOfMaterialText(values.resolutionType)]}<tpl if="!this.isNotAvailable(contractSapMaterialTxt)"> ({contractSapMaterialTxt})</tpl> </td>',
		        		'<td>{[this.getBasedOnText(values.deliverySettingType)]}</td>',		        		
			        '</tr>', 
	        '</tpl>',
	        '</tbody></table>',
	        {
	            // XTemplate configuration:
	            compiled: true,
	            disableFormats: true,
	            isNotAvailable: function(val){	                
	                return val == null; 
	            },
	            getBasedOnText:function(val){
		            if (val == <c:out value="${DELIVERY_SETTING_BASED_ON_CONTRACT}"/>) {
		            	return '<fmt:message key="bestellung.materialcontractcombo.deliverySettingBasedOnContract"/>';
		            }
		            else if (val == <c:out value="${DELIVERY_SETTING_BASED_ON_CONTRACT_COMBINED_RESTRICTION}"/>) {
		            	return '<fmt:message key="bestellung.materialcontractcombo.deliverySettingBasedContractCombinedRestriction"/>';
		            }
		            else if (val == <c:out value="${DELIVERY_SETTING_BASED_ON_RESTRICTION}"/>) {
		            	return '<fmt:message key="bestellung.materialcontractcombo.deliverySettingBasedOnRestriction"/>';
		            }
		            else {
			            return '';
		            }
	            },
	            getResolutionOfMaterialText:function(val){
		            if (val == <c:out value="${RESOLUTION_FROM_PRICING_GROUP_TYPE_GENERIC_MATERIAL}"/>) {
		            	return '<fmt:message key="bestellung.materialcontractcombo.materialResolvedFromPricingGroup"/>';
		            }
		            else if (val == <c:out value="${RESOLUTION_EXPLICIT_IN_CONTRACT}"/>) {
		            	return '<fmt:message key="bestellung.materialcontractcombo.materialExplicitelyDefined"/>';
		            }
		            else if (val == <c:out value="${RESOLUTION_FROM_DIVISION_TYPE_GENERIC_MATERIAL}"/>) {
		            	return '<fmt:message key="bestellung.materialcontractcombo.materialResolvedFromDivision"/>';
		            }
		            else {
			            return '';
		            }
	            },	            
	            notAvailableText:'<fmt:message key="webbestellung.text.chooseproduct"/>'
	        }	        
	    );
	    
	item.sapMatCombo = new Ext.ux.MaterialCombo({
		id: 'sapMatCombo[<%=idx%>]',
		name: '<%=itemStr%>.sapMatId',
	    transform:'<%=itemStr%>.sapMatId',
		emptyText: 'Choose product...',
	    tpl: materialComboTemplate,
	    minChars: 2,	    
	    valueField: 'sapMaterialId',
	    displayField: 'txt',
	    store: new Ext.data.JsonStore({
	    	fields: ['sapMaterialId', 
	        			'txt', 
	        			'productionPlantCd', 
	        			'billtoSapBpId', 
	        			'payerSapBpId',
	        			'poolId', 
	        			'deliveryTypeCd',
	        			'quantity', 
	        			'uomCd', 
	        			'shiptoSiloId',
	        			'shippingpointId',
	        			'loadingpointId',
	        			'autoPoolAssignmentFlag',
	        			'autoCarrierAssignmentFlag',
	        			'releaseSapFlag',
	        			'noteOrderMgmt',
	        			'noteDelivery',
	        			'noteDispatching',
	        			'noteTruckDriver',
	        			'noteRailwayDisp',
	        			'shiptoSareaRestrId',
	        			'billtoRuletypeCd',
	        			'sapMaterialNo',
	        			'freeDeliveryFlag',
	        			'logonPackageTypeCd',
	        			'materialPackageCd',
	        			'materialPackageTxt',
	        			'composite_id',
	        			'supplyPlantName',
	        			'supplyPlantCd',
	        			'siloName',
	        			'billtoName',
	        			'payerName',
	        			'productionPlantName',
	        			'shippingpointName',
	        			'sareaId',
	        			'sapContractId',
	        			'sapContractItemId',
	        			'shiptoSareaRestrId',
	        			'sapContractNo',
	        			'contractItemNo',
	        			'sareaDisplayName',
	        			'compositeIdentifier' ,
	        			'contractSapMaterialTxt',
	        			'deliverySettingType',
	        			'resolutionType',
	        			'searchByText'                 		
	        			],
        	data: productList,
			sortInfo :{
			field:'txt',
			direction:'ASC'
			}          	
    	}),
	    width: 120,
	    listWidth: 800
	});



    item.sapMatCombo.on('beforeSelect', sapMatBeforeSelectFn);
    
	item.sapMatCombo.on('select', function(combo, record) {

		spinner.show(<%=itemIdx%>);
		saveOrderBtn.disable();
		
		var sapMatId = record.data.sapMaterialId;
		
		Ext.getDom('<%=itemStr%>.itemNoteOrderMgmt').value = nvl(record.data.noteOrderMgmt);
		Ext.getDom('<%=itemStr%>.itemNoteDelivery').value = nvl(record.data.noteDelivery);
		Ext.getDom('<%=itemStr%>.itemNoteDispatching').value = nvl(record.data.noteDispatching);
		Ext.getDom('<%=itemStr%>.itemNoteTruckDriver').value = nvl(record.data.noteTruckDriver);
		Ext.getDom('<%=itemStr%>.itemNoteRailway').value = nvl(record.data.noteRailwayDisp);
		
		// --- Quantity ---
		item.quantityStr.enable();
		item.quantityStr.setValue(record.data.quantity || 1);
		
		// --- Uom ---
		item.uomCombo.enable();
		item.uomCombo.preselectValue(record.data.uomCd, true);

		// --- Checking pallet quantity ---
		checkQuantityOfPalletsFn();
		
		// --- Reset the loading point combo ---
		//item.lpCombo.disable();
		//item.lpCombo.clearValue();
		
		// --- Reset for the production plant and supply plant list --- 
		item.prodPlantCombo.disable();
		item.prodPlantCombo.clearValue();
		
    	OrderAjaxService.getCollectedEditorData({
    		'editorIdx': <%=idx%>,
			'orderItemIdx': <%=itemIdx%>,
    		'sapMatId': sapMatId, 
    		'supplyPlantCd': null, 
    		'spId': (editor.spCombo.getValue() || -1), // Defined by the first item 
    		'poolId': (record.data.poolId || -1), 
    		'quantity': (record.data.quantity || 1), 
    		'delTypeCd': (editor.delTypeCombo.getValue() || -1), // Defined by the first item 
    		'prodPlantCd': (record.data.productionPlantCd || -1),
    		'delToDat': editor.getDelToDateTime(), // Defined by the first item
    		'uomCd': (record.data.uomCd || -1),
    		'sapContractId': (record.data.sapContractId || -1),
    		'sapContractItemId': (record.data.sapContractItemId || -1),
    		'shiptoSareaRestrId': (record.data.shiptoSareaRestrId || -1),
    		'compositeIdentifier': record.data.compositeIdentifier
    		}, 
    	function(res) {

    		// --- Price ---
			updateMaterialInfo(<%=idx%>, <%=itemIdx%>, res.orderItems[<%=itemIdx%>]);

			// --- UOM ---
			item.uomCombo.store.loadData(res.uomList);
			item.uomCombo.enable();
			item.uomCombo.preselectValue(record.data.uomCd, true);
			
    		// --- Production plant ---
			item.prodPlantCombo.store.loadData(res.prodPlantList);
			item.prodPlantCombo.preselectValue(record.data.productionPlantCd, true);

			if (record.data.productionPlantByContract) {
				item.prodPlantCombo.disable();
			}
			else {
				item.prodPlantCombo.enable();
			}

			// --- LP ---
    		//item.lpCombo.store.loadData(res.lpList);
    		//item.lpCombo.preselectValue(record.data.loadingpointId, true);
    		//item.lpCombo.enable();
			
			jsonData[<%=idx%>] = res;
			jsonData[<%=idx%>].supplyPlantCd = editor.supplyPlantCombo.getValue(); // Derived from the Shipping Point 
			
			spinner.hide(<%=itemIdx%>);
			saveOrderBtn.enable();
			addOrderItemBtn.enable();
			addServiceOrderItemBtn.enable();
			
		});
				
	});

	item.prodPlantCombo = new Ext.form.ComboBox({
		mode: 'local',
		editable: false,
		forceSelection: true,
		id: '<%=itemStr%>.prodPlantCombo',
		name: '<%=itemStr%>.prodPlantCd',
	    transform:'<%=itemStr%>.prodPlantCd',
	    triggerAction: 'all',
	    store: new Ext.data.JsonStore({
        	fields: [
        		{name: 'plantCd', mapping: 'plantCd'}, 
        		{name: 'plantName', mapping: 'plantName'}, 
        		{name: 'productionPlant', mapping:'productionPlant'}],
            	emptyItem: {'plant' : {'plantName': '...', 'plantCd': '-1'}},
        	data : jsonData[<%=idx%>].prodPlantList 
    	}),
	    listeners: {
		    select: function(combo, rec) {

		    	if (rec.data.plantCd && rec.data.plantCd != -1) {
		    		addOrderItemBtn.enable();
		    		addServiceOrderItemBtn.enable();
			    	checkQuantityOfPalletsFn();
		    	}
		    	else {
		    		addOrderItemBtn.disable();
		    		addServiceOrderItemBtn.disable();
		    	}
		    	
				// --- In case of bimodal order, refresh the rail order list ---		    
	        	if (isBimodalTruckPartOrder(editor.delTypeCombo.getValue())) {
		        	
	        		loadingMessage = '<fmt:message key="bestellung.indicator.loading.bimodal.rail.order.list"/>...';
	        		
					editor.biRailItemCombo.disable();
					editor.biRailItemCombo.clearValue();
		        	
			    	OrderAjaxService.getCollectedEditorData({
			    		'editorIdx': <%=idx%>, 
						'orderItemIdx': <%=itemIdx%>,
			    		'sapMatId': null, 
			    		'supplyPlantCd': null, 
			    		'prodPlantCd': rec.data.plantCd, 
			    		'spId': editor.spCombo.getValue(), 
			    		'poolId': null, 
			    		'quantity': null, 
			    		'delTypeCd': null,
			    		'delToDat': editor.getDelToDateTime(),
			    		'uomCd': null}, 
			    	function(res) {
	
		    			toggleRailDeliveryData(0, res.railData);
			        	
			    		editor.biRailItemCombo.store.loadData(res.biRailItemList);
			    		editor.biRailItemCombo.enable();
			    		
						jsonData[<%=idx%>] = res;
			    	});
	        	}
			}		
		},
	    tpl: '<tpl for="."><div class="x-combo-list-item">{plantName} <tpl if="plantName != \'...\'"><span class="dynlabel">({plantCd})</span></tpl></div></tpl>',
	    width: 110,
	    listWidth: 180,
	    valueField: 'plantCd',
	    displayField: 'plantName',
		disabled: true
	});
	
	
	// --- QUANTITY ---
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
		style: 'width: 35px; text-align: right',
		enableKeyEvents: true,
		disabled: true,
		listeners: {
			change: function(textField, newValue, oldValue) {
				
				if (Ext.isEmpty(newValue)) {
		    		addOrderItemBtn.disable();
		    		addServiceOrderItemBtn.disable();
				}
				else {
		    		addOrderItemBtn.enable();
		    		addServiceOrderItemBtn.enable();
			    	checkQuantityOfPalletsFn();
				}
			}
		}
	});
	

	// --- UoM --- 
	item.uomCombo = new Ext.form.ComboBox({
		mode: 'local',
		editable: false,
		forceSelection: true,
		id: 'uomCombo<%=itemIdx%>',
	    triggerAction: 'all',
	    transform: '<%=itemStr%>.uomCd',
	    store: new Ext.data.JsonStore({
        	fields: [
        		{name: 'uomCd', mapping: 'uomTxPK.uomBs.uomCd'}, 
        		{name: 'txt', mapping: 'txt'}],
        	emptyItem: {'txt': '...', 'uomTxPK': {'uomBs': {'uomCd': '-1'}}},
        	data : jsonData[<%=idx%>].uomList 
    	}),
	    valueField: 'uomCd',
	    displayField: 'txt',	        		  	    
	    width: 75,
		disabled: true,
	    listeners: {
		    select: function(combo, rec) {
		    	if (rec.data.uomCd && rec.data.uomCd != -1) {
		    		addOrderItemBtn.enable();
		    		addServiceOrderItemBtn.enable();
			    	checkQuantityOfPalletsFn();
		    	}
		    	else {
		    		addOrderItemBtn.disable();
		    		addServiceOrderItemBtn.disable();
		    	}
	    	}
	    }
	});
	
	// Find a whole if there is one already
	var newItemIdx = null;
	
	for (var itemIdx = 1; itemIdx < editor.orderItems.length; itemIdx++) {
		if (editor.orderItems[itemIdx] == null) {
			editor.orderItems[itemIdx] = item;
			newItemIdx = itemIdx;
			break;
		}
	}
	
	if (newItemIdx == null) {
		editor.orderItems.push(item);
	}
	
	updateSpecialEditorListeners(<%=idx%>);
	updateServiceOnlyMode(<%=idx%>, 0);	// always use the first item in the editor
	saveOrderBtn.enable();
	
});

</script>

<table style="width:1224px; border-spacing: 0px; margin: 1px 2px 2px 2px; border-top: 1px solid #FFFFFF" cellspacing="0">

		<tr style="background-color: #BBBBBB">
        	<td class="itemHeaderCell" style="border-right: 2px solid #FFFFFF">&nbsp;</td>
        	<td class="itemHeaderCell" style="border-right: 1px solid #FFFFFF"><fmt:message key="fields.produkt.produkt"/></td>
        	<td class="itemHeaderCell" style="border-right: 1px solid #FFFFFF"><fmt:message key="fields.konditionen.produktionswerk"/></td>
        	<td class="itemHeaderCell" style="border-right: 1px solid #FFFFFF"><fmt:message key="fields.product.openquantity"/></td>
        	<td class="itemHeaderCell" style="border-right: 1px solid #FFFFFF; text-align: right">#</td>
        	<td class="itemHeaderCell" style="border-right: 1px solid #FFFFFF"><fmt:message key="fields.uom.uom"/></td>
        	<td class="itemHeaderCell">&nbsp;</td>
        	<td class="itemHeaderCell">&nbsp;</td>
        	<td class="itemHeaderCell">&nbsp;</td>
        	<td class="itemHeaderCell">&nbsp;</td>
        	<td class="itemHeaderCell">&nbsp;</td>
		</tr>

        <tr style="background-color: #DDDDDD">
        	<td style="width: 22px; border-right: 2px solid #FFFFFF">
				<a href="#" onclick="removeOrderItem(<%=itemIdx%>)"><img title="Remove order item" src="<crh-common:url value='/images/silk/basket_delete.png'/>" style="margin: 3px 0px 0px 3px"/></a>
        	</td>
            <td style="width: 142px">
            	<table cellpadding="0" cellspacing="0" style="padding: 0px;margin:0px;border-spacing: 0px">
            		<tr>
            			<td style="padding: 0px;border-spacing: 0px">
            				<div class="marginbottom">
			            		<html:hidden property="editorList[${idx}].orderItems[${itemIdx}].sapMatId" styleId="editorList[${idx}].orderItems[${itemIdx}].sapMatId"/>
			            	</div>
            			</td>
            			<td style="padding: 0px;border-spacing: 0px"><img id="sapMatSpinner<%=itemIdx%>" src="<crh-common:url value='/images/gray/s.gif'/>" style="width: 16px; margin-top: 3px; margin-left: 3px"></td>
            		</tr>
            	</table>
            </td>
            <td style="width: 115px">
				<html:hidden property="editorList[${idx}].orderItems[${itemIdx}].prodPlantCd" styleId="editorList[${idx}].orderItems[${itemIdx}].prodPlantCd"/>
            </td>
            <td style="width: 60px; text-align: right">
				<div id="editorList[<%=idx%>].orderItems[<%=itemIdx%>].label.openQuantity" class="dynlabel" style="text-align: center">
					<%= BestellungForm.NO_OPEN_QUANTITY %>
				</div>
            </td>
           	<!-- QUANTITY -->
            <td style="width: 40px; text-align: right">
            	<html:text property="editorList[${idx}].orderItems[${itemIdx}].quantityStr" styleId="editorList[${idx}].orderItems[${itemIdx}].quantityStr"/>
            </td>
           	<!-- UOM + SILO: silo only for 1st item -->
            <td style="width: 85px">
				<html:hidden property="editorList[${idx}].orderItems[${itemIdx}].uomCd" styleId="editorList[${idx}].orderItems[${itemIdx}].uomCd"/>            					
            </td>
            <!--  DELIVERY FROM / TO -->
            <td style="width: 170px">&nbsp;</td>
			<%-- DELIVERY TYPE --%>
            <td style="width: 105px">&nbsp;</td>
            <%-- LOADING POINT --%>
            <td style="width: 135px">&nbsp;
            	<%-- TODO remove, not needed anymore 
            	<html:hidden property="editorList[${idx}].orderItems[${itemIdx}].loadingpointId" styleId="editorList[${idx}].orderItems[${itemIdx}].loadingpointId"/>
	           	--%>
            </td>
            <%-- POOL / CARRIER --%>
      	    <td style="width: 152px">&nbsp;</td>
      	    <%-- Bill-to --%>
      	    <td style="width: 105px; text-align: center">&nbsp;</td>
		</tr>
		
		<!-- Comments -->
		<tr style="background-color: #DDDDDD">
        	<td style="width: 22px; border-right: 2px solid #FFFFFF">
        		<img src="<crh-common:url value='/images/gray/s.gif'/>" style="width: 16px; margin-top: 3px; margin-left: 3px"/>
        	</td>
			<td colspan="10" style="padding-top: 0px">
				<table class="compact" style="width: 100%; border-spacing: 0px">
					<tr>
						<td style="width: 20%; padding: 0px 7px 0px 0px">
							<p class="itemCommentLabel"><fmt:message key="dispokarte.texte.auftragsmanagement"/></p>
							<html:textarea	property="editorList[${idx}].orderItems[${itemIdx}].itemNoteOrderMgmt" styleId="editorList[${idx}].orderItems[${itemIdx}].itemNoteOrderMgmt" styleClass="${textareaCls}" style="width: 100%; margin-top: 2px; padding: 0px; font-size: 11px" disabled="true"></html:textarea>
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
		</tr>
</table>

</html:form>
<%--
</div>
--%>