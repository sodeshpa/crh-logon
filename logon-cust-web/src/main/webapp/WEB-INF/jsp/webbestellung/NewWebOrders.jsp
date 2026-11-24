<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: NewWebOrders.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp"%>
<%@page import="com.crh.logon.constants.Constant"%>


<%@page import="com.crh.logon.cust.web.forms.webbestellung.WebBestellungForm"%>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>
<c:set var="ORDER_STATUS_NEW"><%=Constant.ORDER_STATUS_NEW%></c:set>

<c:choose>
	<c:when test="${cust_WebBestellungForm.deliverySettingCompositeId eq '-1'}">
		<c:set scope="page" var="isEditorDisabled" value="true" />
		<c:set scope="page" var="isEditorDisabledStr" value=" disabled " />
	</c:when>
	<c:otherwise>
		<c:set scope="page" var="isEditorDisabled" value="false" />
		<c:set scope="page" var="isEditorDisabledStr" value="" />
	</c:otherwise>
</c:choose>

<c:set var="displayShipToTimezone" scope="request" value="${cust_WebBestellungForm.displayShipToTimezone}"/>
<c:set var="shipToTimeZoneFmtOffset" scope="request" value="${cust_WebBestellungForm.shipToTimeZone.formattedAsUTCOffset}"/>

<style type="text/css">
.hiddenDiv {
	display: none;
	visibility: hidden
}
</style>
<script type="text/javascript">
	//# sourceURL=NewWebOrders.jsp.js

<%-- Initialize the helper with the constants set from here ( we did NOT hardcode these in the .js itself) --%>
logonPackageHandlingHelper.init('<%=Constant.PACKAGE_TYPE_BULK_POWDER%>', '<%=Constant.PACKAGE_TYPE_BULK_DUMPED%>', '<%=Constant.PACKAGE_TYPE_BBAG%>', '<%=Constant.PACKAGE_TYPE_SERVICE%>');

function disableSave() {
	Ext.getDom('saveNewButton').disabled = true;
}
function disableAdd() {
	Ext.getDom('addRowButton').disabled = true;
}
function enableAdd() {
	Ext.getDom('addRowButton').disabled = false;
}
function enableSave() {
	Ext.getDom('saveNewButton').disabled = false;
}
function enableAdjustDeliveries() {
	Ext.getDom('adjustDeliveriesBtn').disabled = false;
}
function disableAdjustDeliveries() {
	Ext.getDom('adjustDeliveriesBtn').disabled = true;
}

function handleQuantityOnChangeAdjustDeliveries(){

	<%-- nothing to do if adjust deliveries popup is disabled --%>
	if (!controllAdjustDeliveries()) {
		return;
	}

	triggerAdjustDeliveriesPopUp(true);
}

function triggerAdjustDeliveriesPopUp(triggeredAutomatically) {
	submitWithAjax('prepareAdjustDeliveries1', triggeredAutomatically);
	enableSave();
}

function showDiv(id) {
	Ext.fly(id).removeClass('hiddenDiv');
}

function hideDiv(id) {
	Ext.fly(id).addClass('hiddenDiv');
}
var additionalItemCombos = new Array();
var gmapEnabled = false;

var selectedSapMatRec;
var billToCombo;
var payerCombo;
var orderAttributesCombinedCombo;

function logonPopupHideCallback (){
	refreshPage();
}

Ext.override(Ext.form.ComboBox, {
    getSelectedRecord: function(idx) {
		if (Ext.isEmpty(this.getValue())) {
			return null;
		}
		else {
			return this.store.getAt(this.store.find(this.valueField, this.getValue()));
		}
    }
});

Ext.override(Ext.form.ComboBox, {
    setSelectedIndex: function(idx) {
    	if (this.store.getCount() > 0) {
			this.setValue(this.store.getAt(idx).get(this.valueField));
		}
		else {
			this.clearValue();
		}
    }
});

Ext.override(Ext.form.ComboBox, {
    preselectValue: function(preval, force) {
        if (!Ext.isEmpty(preval) && this.store.find(this.valueField, preval) != -1) {
            this.setValue(preval);
        } 
        else if (!force) {
            this.clearValue();
        } 
        else {
        	this.setSelectedIndex(0);
        }
        
    }
});

Ext.apply(Ext.form.VTypes, {
    daterange : function(val, field) {
        var date = field.parseDate(val);

        if(!date){
            return;
        }
        /*
         * Always return true since we're only using this vtype to set the
         * min/max allowed values (these are tested for after the vtype test)
         */
        return true;
    }
});
var sapMatCombo;
var uomCombo;
var delFromDat;
GMapAPILoader.onAPIReady(function(){

gmapEnabled = true;
	
});

var invalidMaterialStore = new Ext.data.JsonStore({
	fields: ['compositeIdentifier'
			],
	data : <c:out value="${cust_WebBestellungForm.json['invalidMaterialList']}" escapeXml="false"/> 
});

//Custom rendering Template
var materialComboTemplate = new Ext.XTemplate(	    
	'<table class="classicblue" style="width:100%"><thead><tr>',
	'<th><fmt:message key="bestellung.materialcontractcombo.material"/></th>',
	'<th><fmt:message key="bestellung.materialcontractcombo.contract"/></th>',
	'<th><fmt:message key="bestellung.materialcontractcombo.uom"/></th>',
	'<th><fmt:message key="bestellung.materialcontractcombo.salesarea"/></th>',
	<c:if test="${cust_WebBestellungForm.json['showProductionPlantName']}" >
	'<th><fmt:message key="bestellung.materialcontractcombo.supplyPlant"/></th>',
	</c:if>
	<c:if test="${cust_WebBestellungForm.json['showNetPriceOnWebForm']}" >
	'<th><fmt:message key="bestellung.materialcontractcombo.netprice"/></th>',
	'<th><fmt:message key="bestellung.materialcontractcombo.currency"/></th>',
	'<th><fmt:message key="bestellung.materialcontractcombo.vat"/></th>',
	</c:if>
	'</tr></thead><tbody>',
    '<tpl for=".">',
        	'<tr class="listitem zebra{[xindex % 2]}">',
        		'<td><tpl if="!this.isNotAvailable(sapMaterialId)">{txt} ({sapMaterialNo})</tpl></td>',
        		'<td><tpl if="!this.isNotAvailable(sapContractId)">{sapContractNo} ({contractItemNo})</tpl></td>',
        		'<td><tpl if="!this.isNotAvailable(sapContractId)">{uomTxt}</tpl></td>',
        		'<td><tpl if="!this.isNotAvailable(sapContractId)">{sareaDisplayName}</tpl></td>',
        		<c:if test="${cust_WebBestellungForm.json['showProductionPlantName']}" >
        		'<td><tpl if="!this.isNotAvailable(sapContractId) && showDeliveryPlantWebOrderFlag">{deliveryPlantName}</tpl></td>',
        		</c:if>
        		<c:if test="${cust_WebBestellungForm.json['showNetPriceOnWebForm']}" >
        		'<td><tpl if="!this.isNotAvailable(sapContractId) && showPriceInWebOrderFlag">{netPrice}</tpl></td>',
        		'<td><tpl if="!this.isNotAvailable(sapContractId) && showPriceInWebOrderFlag">{currency}</tpl></td>',
        		'<td><tpl if="!this.isNotAvailable(sapContractId) && showPriceInWebOrderFlag">{VAT}</tpl></td>',
        		</c:if>
	        '</tr>', 
    '</tpl>',
    '</tbody></table>',
    {
    			        
        // XTemplate configuration:
        compiled: true,
        disableFormats: true,
        isNotAvailable: function(val){	                
            return val == null; 
        }
    }	        
);

Ext.ux.generalUomCombo = Ext.extend( Ext.form.ComboBox,{
    typeAhead: false,
    mode: 'local',
    editable: false,
    shadow: false,
    triggerAction: 'all',
    forceSelection:true,
    emptyText: '...',
    style: 'margin-bottom: 2px',
    store: new Ext.data.JsonStore({
    	fields: [
               		{name: 'uomCd', mapping: 'uomTxPK.uomBs.uomCd'},
             		{name: 'txt', mapping: 'txt'}
         			]
    }),
    displayField: 'txt',
    valueField: 'uomCd',
    loadAndSelectSingleUom: function(data) {
		var r = new this.store.recordType({'uomCd': data.uomCd,'txt': data.uomTxt}, data.uomCd);
		this.store.insert(0, r);
		this.preselectValue(data.uomCd, false);	
	}

});

var productStore = new Ext.data.JsonStore({
	fields: [
             'compositeIdentifier',
             'sapMaterialId', 
  			'txt', 
  			'productionPlantCd', 
  			'billtoSapBpId', 
  			'payerSapBpId',
  			'poolId', 
  			'uomCd', 
  			'shiptoSiloId',
  			'shippingpointId',
  			'loadingpointId',
  			'autoPoolAssignmentFlag',
  			'autoCarrierAssignmentFlag',
  			'noteOrderMgmt',
  			'noteDelivery',
  			'noteDispatching',
  			'noteTruckDriver',
  			'shiptoSareaRestrId',
  			'noteRailwayDisp',
  			'sapMaterialNo',
  			'quantity',
 			'sapContractNo',
  			'billtoRuletypeCd',
  			'contractItemNo',         			
  			'materialPackageCd',
  			'deliveryTypeCd',
  			'logonPackageTypeCd',
 			'sapContractId',
 			'uomTxt',
 			'searchByText',
 			'payerIgnored',
 			'deliveryPlantName',
 			'sareaDisplayName',
 			'showDeliveryPlantWebOrderFlag',
 			'showPriceInWebOrderFlag',
 			'netPrice',
 			'currency',
 			'VAT'
  			],
	data : <c:out value="${cust_WebBestellungForm.json['productList']}" escapeXml="false"/>
});

var distanceValidationPopUpText;
var distanceForTrack;
var sapMatCombo;
var pickupDeliveryCombo;
function doOnready(){

	GMapAPILoader.load();
	
	var isEditorDisabled = <c:out value="${isEditorDisabled}"/>;
	
	// LOGON-6062: Reset note and ref fields
	Ext.getDom("custWeborderNote").value = "";
	Ext.getDom("custWeborderRefNo").value = "";

	
	// ---
	// Checkboxes of BB and LS flags
	// ---
	Ext.get('custorder.orderConfFaxNumber').on('change', function() {
		var isEmpty = Ext.isEmpty(Ext.getDom('custorder.orderConfFaxNumber').value); 
		Ext.getDom('custorder.orderConfFaxFlag').disabled = isEmpty;
		Ext.getDom('custorder.loadConfFaxFlag').disabled = isEmpty;
		Ext.getDom('custorder.delivnoteFaxFlag').disabled = isEmpty;
	});

	Ext.get('custorder.orderConfEmailAdresses').on('change', function() {
		var isEmpty = Ext.isEmpty(Ext.getDom('custorder.orderConfEmailAdresses').value); 
		Ext.getDom('custorder.orderConfEmailFlag').disabled = isEmpty;
		Ext.getDom('custorder.loadConfEmailFlag').disabled = isEmpty;
		Ext.getDom('custorder.delivnoteEmailFlag').disabled = isEmpty;
	});
	
	// ----
	// Delivery from / to
	// ----
	
	delFromDat = new Ext.form.DateField({
		id: 'delFromDat',
    	format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
    	altFormats: '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>',
    	applyTo: 'deliveryFromDatStr',
        allowBlank: false,
	    disabled: isEditorDisabled,
		endDateField: 'delToDat',	    
    	width: 82,
		listeners: {
    		select: function(obj, date) {
    			obj.changedByPicker = true;
	    		if (date > delToDat.getValue()) {
	    			delToDat.setValue(date);
	    		}
	    		billToCombo.sapMaterialDependentChange(selectedSapMatRec);
			    payerCombo.sapMaterialDependentChange(selectedSapMatRec);
		    	sapMatCombo.toggleActualDeliveryDat(selectedSapMatRec);
			},
    		change: function(obj, newVal, oldVal) {
	    		if (!obj.changedByPicker) {
	    			if (Ext.isEmpty(newVal)) {
	    				obj.setValue(oldVal);
	    			}
	    			else {
						if (newVal > delToDat.getValue()) {
							delToDat.setValue(newVal);
		    			}
		    			billToCombo.sapMaterialDependentChange(selectedSapMatRec);
		    			payerCombo.sapMaterialDependentChange(selectedSapMatRec);
		    			sapMatCombo.toggleActualDeliveryDat(selectedSapMatRec);
		    			if (selectedSapMatRec.data.deliveryTypeCd == <%=Constant.DELIVERY_TYPE_BIMODAL_ROAD%>){
		    				submitWithAjax("addBimodalRailPart", false);
		    			}
	    			}
	    		}
	    		else {
	    			obj.changedByPicker = false;		    		
	    		}		    		
    		}
    	}
	});	

	var delFromTime = new Ext.form.TimeField({
		id: 'delFromTime',
    	minValue: '<c:out value="${cust_WebBestellungForm.deliveryFromTime}"/>',
	    maxValue: '<c:out value="${cust_WebBestellungForm.deliveryToTime}"/>',
    	increment: 15,
    	format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
    	applyTo: 'deliveryFromTime',
	    disabled: isEditorDisabled,
    	width: 82
	});

	var delToDat = new Ext.form.DateField({
		id: 'delToDat',
    	format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
    	altFormats: '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>',
    	applyTo: 'deliveryToDatStr',
        allowBlank: false,
	    disabled: isEditorDisabled,
		startDateField: 'delFromDat',	    
    	width: 82,
    	listeners: {
    		select: function(obj, date) {
    			obj.changedByPicker = true;
	    		// --- Auto adjust deliv. to. ---
	    		if (date < delFromDat.getValue()) {
	    			delFromDat.setValue(date);
	    		}
	    		sapMatCombo.toggleActualDeliveryDat(selectedSapMatRec);
			},
    		change: function(obj, newVal, oldVal) {
	    		if (!obj.changedByPicker) {
	    			if (Ext.isEmpty(newVal)) {
	    				obj.setValue(oldVal);
	    			}
	    			else {
	    	    		if (newVal < delFromDat.getValue()) {
	    	    			delFromDat.setValue(newVal);
	    	    		}
	    	    		sapMatCombo.toggleActualDeliveryDat(selectedSapMatRec);
	    				if (selectedSapMatRec.data.deliveryTypeCd == <%=Constant.DELIVERY_TYPE_BIMODAL_ROAD%>){
	    					submitWithAjax("addBimodalRailPart", false);
	    				}
	    			}
	    		}
	    		else {
	    			obj.changedByPicker = false;		    		
	    		}		    		
    		}
    	}
	});

	var delToTime = new Ext.form.TimeField({
		id: 'delToTime',
    	minValue: '<c:out value="${cust_WebBestellungForm.deliveryFromTime}"/>',
	    maxValue: '<c:out value="${cust_WebBestellungForm.deliveryToTime}"/>',
    	increment: 15,
    	format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
    	applyTo: 'deliveryToTime',
	    disabled: isEditorDisabled,
    	width: 82
	});

	// -----------------------------------------------------------------------------------
	// Material
	// -----------------------------------------------------------------------------------
	
	Ext.ux.WebOrderMaterialCombo = Ext.extend(Ext.ux.MaterialCombo,{
		emptyText: '<fmt:message key="webbestellung.text.chooseproduct"/>',
	    minChars: 2,
		width : 475,
		tpl: materialComboTemplate,
	    showIfItemsAdded:function(){
	    	if(this.moreItemsPresent){
	    		Logon.message.warning('<fmt:message key="webbestellung.information.item.firstmaterial"/>');
	    		return false;
    		}
    		return true;
		}
	});

	<%-- Loads the bill to list and selects the default bill to --%>
	function updateBillToCombo(webOrderTakingEditorDTO, selectedRestriction) {
		if (webOrderTakingEditorDTO && webOrderTakingEditorDTO.billTos && webOrderTakingEditorDTO.billTos.length > 0) {
			billToCombo.store.loadData(webOrderTakingEditorDTO.billTos);
		} 
		else {
			Logon.message.warning('<fmt:message key="webbestellung.error.billtos.not.available"/>');
			return false;
		}
		
		billToCombo.enable();
		
		if (selectedRestriction.billtoSapBpId && !Ext.isEmpty(selectedRestriction.billtoSapBpId)) {
			billToCombo.preselectValue(selectedRestriction.billtoSapBpId, true);
		}
		else {
			billToCombo.clearValue();
		}
		return true;
	};

	<%-- Loads the payer list and selects the default payer --%>
	function updatePayerCombo(webOrderTakingEditorDTO, selectedRestriction) {
		if (webOrderTakingEditorDTO && webOrderTakingEditorDTO.payers && webOrderTakingEditorDTO.payers.length > 0) {
			payerCombo.store.loadData(webOrderTakingEditorDTO.payers);
		} 
		else {
			Logon.message.warning('<fmt:message key="webbestellung.error.payers.not.available"/>');
			return false;
		}
		if (selectedRestriction.payerIgnored) {
			payerCombo.disable();
		}
		else {
			payerCombo.enable();
		}
	
		if (!Ext.isEmpty(selectedRestriction.payerSapBpId)) {
			payerCombo.preselectValue(selectedRestriction.payerSapBpId, true);
		}
		else {
			payerCombo.clearValue();
		}
		
		return true;
	};

	function selectMaterialCallback(webOrderTakingEditorDTO, selectedRestriction) {
		if (!webOrderTakingEditorDTO) {
			return;
		}

		var necessaryDataWasReceivedAndProcessed = updateBillToCombo(webOrderTakingEditorDTO, selectedRestriction);
		necessaryDataWasReceivedAndProcessed &= updatePayerCombo(webOrderTakingEditorDTO, selectedRestriction);
        orderAttributesCombinedCombo.initChecked('onHoldOrderAndLockedFlag', webOrderTakingEditorDTO.onHoldOrderAndLockedFlag);
        orderAttributesCombinedCombo.updateList();
        orderAttributesCombinedCombo.enable();
		Ext.each(Ext.query('.copyCustomerCommentOn'), function (element) {
			var extElement = Ext.get(element);
			extElement.setVisibilityMode(Ext.Element.DISPLAY);
			if (webOrderTakingEditorDTO.copyCustomerComment) {
				extElement.show();
			}
			else {
				extElement.hide();
			}
		});
		
		Ext.each(Ext.query('.copyCustomerCommentOff'), function (element) {
			var extElement = Ext.get(element);
			extElement.setVisibilityMode(Ext.Element.DISPLAY);
			if (webOrderTakingEditorDTO.copyCustomerComment) {
				extElement.hide();
			}
			else {
				extElement.show();
			}
		});
		
		if (necessaryDataWasReceivedAndProcessed) {
			enableSave();
		}
	};
	
	sapMatCombo = new Ext.ux.WebOrderMaterialCombo({
		id: 'sapMatCombo',
	    valueField: 'compositeIdentifier',
		name: 'deliverySettingCompositeId',
	    transform: 'deliverySettingCompositeId',
	    moreItemsPresent: false,
		isBulk : false,
		store: productStore,
		uomCombo: uomCombo,
		//custom functions only for the special first material dropdown
	    toggleActualDeliveryDat: function (record) {
    		var idx=0;
    		removeWarnRow();
    		if (record.data.deliveryTypeCd == '<%=Constant.DELIVERY_TYPE_RAIL_DIRECT%>'){
    			delFromTime.hide();
    			delToTime.hide();
    			WebOrderAjaxService.getNextTrainArr(record.data.compositeIdentifier, function (actualDeliveryDat){
    				addWarnRow();
    				showDiv('divActualDeliveryDat'+idx);
    				hideDiv('divActualDeliveryDatWarn'+idx);
    				var valid = true;
    			if (!Ext.isEmpty(actualDeliveryDat)) {
    				Ext.getDom('divActualDeliveryDatTxt'+idx).innerHTML = '<b><fmt:message key="webbestellung.rail.order.actual.delivery.date.abbreviated"/>:</b> ' + actualDeliveryDat.format('d.m.y H:i');
    				if (actualDeliveryDat < delFromDat.getValue()) {
    					showDiv('divActualDeliveryDatWarn'+idx);
    					Logon.message.warning('<fmt:message key="webbestellung.error.early.arrival"/>');
    				}
    				if (actualDeliveryDat > Date.parseDate(Ext.getDom('deliveryToDatStr').value + ' 23:59', 'd.m.y H:i')) { 
    					showDiv('divActualDeliveryDatWarn'+idx);
    					Logon.message.warning('<fmt:message key="webbestellung.error.rail.earliest.too.late"/>'); 
    					valid = false;
    				}
    			}
    			else {
    				showDiv('divActualDeliveryDatWarn'+idx);
    				Ext.getDom('divActualDeliveryDatTxt'+idx).innerHTML = 'No scheduled train';
    				Logon.message.warning('<fmt:message key="webbestellung.error.no.railwaystation.timetable"/>');
    				valid = false;
    			}
    			if (!valid) {
    				disableSave();
    			}
    			else{
    				enableSave();
    			}
    		});
    		return;
    	}
    	delFromTime.show();
    	delToTime.show();
    	var idx = 0;
    	removeWarnRow();
    },
    updateCorrFactorAndArrivalTime : function updateCorrFactorAndArrivalTime(record) {
    	if (gmapEnabled) {			
    		WebOrderAjaxService.getShippingPointById(record.data.shippingpointId, function (res) {
    			var res1 = res;
    			if (record.data.deliveryTypeCd != null && (record.data.deliveryTypeCd == '<%=Constant.DELIVERY_TYPE_ROAD_DIRECT%>' 
						|| record.data.deliveryTypeCd == '<%=Constant.DELIVERY_TYPE_PICKUP%>'
						|| record.data.deliveryTypeCd == '<%=Constant.DELIVERY_TYPE_SHIP_PICKUP%>'
						|| record.data.deliveryTypeCd == '<%=Constant.DELIVERY_TYPE_BLOCKTRAIN_PICKUP%>')) {
	   				WebOrderAjaxService.getDurationCorrectionFactor(record.data.shippingpointId, "<c:out value="${cust_WebBestellungForm.shipTo.shiptoId}"/>", "<%=Constant.DELIVERY_TYPE_ROAD_DIRECT%>", function(corrFactor){
						updateDurationTimeAndMap(res1, corrFactor, record.data.deliveryTypeCd); 
    				});
    			}
    			else if (record.data.deliveryTypeCd != null && (record.data.deliveryTypeCd == '<%=Constant.DELIVERY_TYPE_SHIP%>' 
						|| record.data.deliveryTypeCd == '<%=Constant.DELIVERY_TYPE_BLOCK_TRAIN%>')) {
   					WebOrderAjaxService.getDurationCorrectionFactor(record.data.shippingpointId, "<c:out value="${cust_WebBestellungForm.shipTo.shiptoId}"/>", record.data.deliveryTypeCd, function(corrFactor){
						updateDurationTimeAndMap(res1, corrFactor, record.data.deliveryTypeCd); 
					});
				}
    			else if (record.data.deliveryTypeCd != null && record.data.deliveryTypeCd == '<%=Constant.DELIVERY_TYPE_RAIL_DIRECT%>'){
    				updateDurationTimeAndMap(res1, 1, record.data.deliveryTypeCd);
    			}
    			else if (record.data.deliveryTypeCd != null && record.data.deliveryTypeCd == '<%=Constant.DELIVERY_TYPE_BIMODAL_ROAD%>'){
	    			WebOrderAjaxService.getDurationCorrectionFactor(record.data.shippingpointId, "<c:out value="${cust_WebBestellungForm.shipTo.shiptoId}"/>", "<%=Constant.DELIVERY_TYPE_ROAD_DIRECT%>", function(corrFactor){
		    			if (corrFactor != undefined){
		    				Ext.getDom('durationCorrFactor').value = corrFactor; 
		    			}
		    			else {
	    					Ext.getDom('durationCorrFactor').value = 1; 
		    			}
						updateDurationTimeAndMap(res1, corrFactor, record.data.deliveryTypeCd); 
    				});
    			}
    			else{
    				resetMapAndRelatedData();
    			}
    		});
    	}
    },
		getDistanceValidationResultGuiBean : function getDistanceValidationResultGuiBean(record) {
			var distanceMetersValue = Ext.getDom('distanceMeters').value;
			WebOrderAjaxService.getDeliveryTimesAndRules(distanceMetersValue, record.data.shippingpointId,
				record.data.deliveryTypeCd, record.data.compositeIdentifier,
				function(res){

					<%-- popupText should always arrive --%>
					distanceValidationPopUpText = res.distanceValidationPopUpText;

					<%-- finish here, if we do not got back values --%>
					if(!res.earliestPossibleDeliveryFromDateString) {
						return;
					}
						var earliestPossibleDeliveryFromDate = Date.parseDate(res.earliestPossibleDeliveryFromDateString + ' 00:00',
							'd.m.y H:i');
						delFromDat.setValue(earliestPossibleDeliveryFromDate);

					delFromTime.setValue(res.earliestPossibleDeliveryFromTimeString);


					var earliestPossibleDeliveryToDate =  Date.parseDate(res.earliestPossibleDeliveryToDateString + ' 00:00',
						'd.m.y H:i');
					delToDat.setValue(earliestPossibleDeliveryToDate);

					delToTime.setValue(res.earliestPossibleDeliveryToTimeString);
					distanceForTrack = res.distanceMeters;
				});
		}

	});


	sapMatCombo.on('expand', function(combo, record) {
		sapMatCombo.showIfItemsAdded();
	});
    
    sapMatCombo.on('beforeSelect', function(combo, record) {
    	Ext.getDom('orderQuantityStr').title = "";

		sapMatCombo.isBulk = logonPackageHandlingHelper.isBulk(record.data.logonPackageTypeCd);

		controllAdjustDeliveries();

    	var picRec = invalidMaterialStore.find("compositeIdentifier",record.data.compositeIdentifier);
    	if (picRec != -1) {
			Logon.message.warning('<fmt:message key="webbestellung.error.invalidrestriction"/>');
			disableSave();
			return false;        	
    	}
    	
    	if (!sapMatCombo.showIfItemsAdded()){
        	return false;
    	}
    	if (logonPackageHandlingHelper.isService(record.data.logonPackageTypeCd)){
    		Logon.message.warning('<fmt:message key="webbestellung.error.service.first.not.possible"/>');
    		return false;
    	}
		if (Ext.isEmpty(record.data.logonPackageTypeCd)){
			Logon.message.warning('<fmt:message key="webbestellung.error.no.logon.package.cd"/>');
			return false;
		}    	
    	<%--These are already checked if they are in the invalidMaterialStore--%>
<%--		if (Ext.isEmpty(record.data.materialPackageCd) || record.data.materialPackageCd == 'NULL') {
			Logon.message.warning('<fmt:message key="webbestellung.error.no.material.package.cd"/>');
    		disableSave();
    		return false;
		}--%>

		if (record.data.shippingpointId) {
			sapMatCombo.updateCorrFactorAndArrivalTime(record);
		}

		sapMatCombo.getDistanceValidationResultGuiBean(record);

		if (record.data.shippingpointId) {
			sapMatCombo.toggleActualDeliveryDat(record);
		}

		enableSave();	
		Ext.getDom('orderQuantityStr').disabled = false;
		Ext.getDom('orderQuantityStr').value = record.data.quantity != undefined ? record.data.quantity : '1';
		
		if (record.data.deliveryTypeCd == '<%=Constant.DELIVERY_TYPE_BIMODAL_ROAD%>'){
			Ext.getDom('orderQuantityStr').disabled = true;
			Ext.getDom('orderQuantityStr').title = "<fmt:message key="webbestellung.information.bimodaldisabledfields"/>";
		}
		
		selectedSapMatRec = record;

		disableSave();
		WebOrderAjaxService.selectMaterial(record.data.compositeIdentifier, function (response) {
    		selectMaterialCallback(response, record.data);
    	});
		
		uomCombo.reset();
		
		if (record.data.uomCd) {
			uomCombo.loadAndSelectSingleUom(record.data);
		}
		
		delFromDat.enable();
		delToDat.enable();
		delFromTime.enable();
		delToTime.enable();
		enableAdd();

		pickupDeliveryCombo.disabled = false;
		pickupDeliveryCombo.enable();
		if(record.data.deliveryTypeCd != null && (record.data.deliveryTypeCd == '<%=Constant.DELIVERY_TYPE_PICKUP%>'
		   || record.data.deliveryTypeCd == '<%=Constant.DELIVERY_TYPE_SHIP_PICKUP%>'
		   || record.data.deliveryTypeCd == '<%=Constant.DELIVERY_TYPE_BLOCKTRAIN_PICKUP%>') ){			
			pickupDeliveryCombo.preselectValue("<%=WebBestellungForm.PICKUP_SELECTION_PICKUP%>");
		}
		else{
			pickupDeliveryCombo.preselectValue("<%=WebBestellungForm.PICKUP_SELECTION_DELIVERY%>");
		}
		var picRec = pickupDeliveryCombo.store.getAt(pickupDeliveryCombo.store.find("pickupSelect",pickupDeliveryCombo.getValue()));
		pickupDeliveryCombo.fireEvent("select", pickupDeliveryCombo, picRec);
		
	});
	
 // ------------------------------------
	// UoM / Transport Category 
	// ------------------------------------
	
	uomCombo = new Ext.ux.generalUomCombo({
		id: 'uomCombo',
	    transform:'orderedUomCd',
	    disabled: isEditorDisabled
	});
	
	

    orderAttributesCombinedCombo = new Ext.ux.CheckComboBox({
        transform:'orderAttributes',
        store: new Ext.data.SimpleStore({
            fields: [ 'txt', 'type', 'checked'],
            data : [
                ['<fmt:message key="dispokarte.texte.onHoldOrderAndLockedFlag"/>','onHoldOrderAndLockedFlag',false]
            ]
        }),
        displayField: 'txt',
        checkedField: 'checked',
        valueField: 'type',
        typeAhead: true,
        mode: 'local',
        triggerAction: 'all',
        emptyText:'all',
        selectOnFocus: true,
        useBulkButtons: false,
        width: 100,
        listWidth: 200,
        disabled: true,
        listeners: {
            change: function(combo, rec) {
                var params= {};
                var checkedFlags = combo.getCheckedValuesAsParams();
                for (prop in checkedFlags) {
                    params[prop] = checkedFlags[prop];
                }
                WebOrderAjaxService.getCollectedEditorData(params);
            }
        },
        initComp: function(onHoldOrderAndLockedFlag){
            this.initChecked('onHoldOrderAndLockedFlag',onHoldOrderAndLockedFlag);
        }
    });

    orderAttributesCombinedCombo.initList();

	// ------------------------------------
	// Bill to
	// ------------------------------------

	billToCombo = new Ext.form.ComboBox({
		id: 'billToCombo',
		name: 'billtoSapBpId',
	    transform:'billtoSapBpId',
		allowBlank: true,
		emptyText: '...',
		selectOnFocus: true,
		editable: false,
		shadow: false,
	    typeAhead: false,
	    minChars: 2,
	    triggerAction: 'all',
	    forceSelection: false,
	    store: new Ext.data.JsonStore({
        	fields: [
        		{name: 'name1', mapping: 'billtoSapBp.name1'},
        		{name: 'sapId', mapping: 'billtoSapBp.sapId'},
        		{name: 'name2', mapping: 'billtoSapBp.name2'},
        		{name: 'sapBpId', mapping: 'billtoSapBp.sapBpId'}
        	] 
    	}),
	    mode: 'local',
	    disabled: true,
	    valueField: 'sapBpId',
	    tpl: '<tpl for="."><div class="x-combo-list-item">{name1} {name2}<tpl if="name1 != \'...\'"><span class="dynlabel">({sapId})</span></tpl></div></tpl>',
	    displayField: 'name1',
	    listWidth: 300,
        width: 130,
	    sapMaterialDependentChange: function(recordData) {
	    	if (!recordData || !recordData.data) {
	    		return;
	    	}
			if (recordData.data.billtoSapBpId != null && !Ext.isEmpty(recordData.data.billtoSapBpId)) {
				this.reset();
				this.preselectValue(recordData.data.billtoSapBpId, false);
				this.enable();
			}
			if (!Ext.isEmpty(recordData.data.shiptoSareaRestrId)){
				WebOrderAjaxService.findBilltoByRhythm(delFromDat.getValue(), recordData.data.shiptoSareaRestrId, function(billtoSapBpId) {
					if (billtoSapBpId != null && !Ext.isEmpty(billtoSapBpId)){
						billToCombo.reset();
						billToCombo.preselectValue(billtoSapBpId, false);
						billToCombo.enable();
					}
				});
			}
		}
	});
	
	// ------------------------------------
	// Payer
	// ------------------------------------
	
	payerCombo = new Ext.form.ComboBox({
		mode: 'local',
		editable: false,
		forceSelection: true,
		emptyText: '...',
		id: 'payerCombo',
		name: 'payerSapBpId',
		transform:'payerSapBpId',
		allowBlank: true,
		triggerAction: 'all',
		store: new Ext.data.JsonStore({
			fields: [
				{ name: 'name1', mapping: 'name1' },
				{ name: 'name2', mapping: 'name2' },
				{ name: 'sapBpId', mapping: 'sapBpId' },
				{ name: 'sapId', mapping: 'sapId' }
			]
		}),
		disabled: true,
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

		listWidth: 250,
        width: 130,
		listAlign: 'tr-br',
		sapMaterialDependentChange: function(recordData) {
			if (!recordData || !recordData.data) {
	    		return;
	    	}
			if (recordData.data.payerSapBpId != null && !Ext.isEmpty(recordData.data.payerSapBpId)) {
				this.reset();
				this.preselectValue(recordData.data.payerSapBpId, false);				
			}		
			if (recordData.data.payerIgnored) {
				this.disable();
			}
			else {
				this.enable();
			}
		}
	});



	pickupDeliveryCombo = new Ext.form.ComboBox({
		id: 'pickupTypeCombo',
		name: 'pickupTypeSelect',
	    transform:'pickupTypeSelect',
		allowBlank: false,
		selectOnFocus: true,
		editable: false,
		shadow: false,
	    typeAhead: true,
	    minChars: 2,
	    triggerAction: 'all',
	    forceSelection: true,
	    mode: 'local',
	    width:100,
	    disabled:isEditorDisabled,
	    store: new Ext.data.JsonStore({
	    	fields: [
	    	     	'pickupSelect',
	    	     	'display'
	    	     	],
	    	    	data : [
	    	    	    	{"pickupSelect":"<%=WebBestellungForm.PICKUP_SELECTION_DELIVERY%>","display":"<fmt:message key="webbestellung.columns.deliver"/>"},
	        	    		{"pickupSelect":"<%=WebBestellungForm.PICKUP_SELECTION_PICKUP%>","display":"<fmt:message key="webbestellung.columns.pickup"/>"}
	        	    		]
	    }),
	    valueField: 'pickupSelect',
	    displayField: 'display'
	});
	pickupDeliveryCombo.preselectValue("<%=WebBestellungForm.PICKUP_SELECTION_DELIVERY%>");

	pickupDeliveryCombo.on("beforeselect",function(combo, record){		
		if (selectedSapMatRec.data.deliveryTypeCd == '<%=Constant.DELIVERY_TYPE_PICKUP%>'
			|| selectedSapMatRec.data.deliveryTypeCd == '<%=Constant.DELIVERY_TYPE_SHIP_PICKUP%>'
			|| selectedSapMatRec.data.deliveryTypeCd == '<%=Constant.DELIVERY_TYPE_BLOCKTRAIN_PICKUP%>'){
			Logon.message.info('<fmt:message key="webbestellung.information.notchangeablepickup"/>');
			//in this case there should be no possibility to select delivery, since we would not know what kind of delivery that would be
			pickupDeliveryCombo.collapse();
			return false;
		}
	});
}
Ext.onReady(doOnready);


function updateDurationTimeAndMap(spRec, corrfactor, deliveryType) {
	resetMapAndRelatedData();
	
	if (!Ext.isEmpty(spRec.coordLon) && !Ext.isEmpty(spRec.coordLat) && Ext.type(spRec.coordLon) == 'number' && Ext.type(spRec.coordLat) == 'number') {
		if (corrfactor != undefined) {
			Ext.getDom('durationCorrFactor').value = corrfactor; 
		}
		else{
			Ext.getDom('durationCorrFactor').value = 1;
		}
		var railDuration;
		var railDistance;
		if (deliveryType == <%=Constant.DELIVERY_TYPE_RAIL_DIRECT%>){
			var shiptoRsId = "<c:out value="${cust_WebBestellungForm.shipTo.shiptoSapBp.railwayStation.railwayStationId}"/>"
			var shippRsId = (!Ext.isEmpty(spRec.railwayStation)) ? spRec.railwayStation.railwayStationId : "";
			if (!Ext.isEmpty(shiptoRsId) && !Ext.isEmpty(shippRsId) ){
				WebOrderAjaxService.calculateDurDistBetweenRailwayStations(shippRsId, shiptoRsId, function(res){
					if (res) {
						railDistance = res[1]; 
						railDuration = res[0];
						var valid = true;
						if (railDuration == -1) {
							disableSave();
							Logon.message.warning('<fmt:message key="webbestellung.error.railway.not.possible"/>');
							valid = false;
						}
						else if (railDuration == -2) {
							disableSave();
							Logon.message.warning('<fmt:message key="webbestellung.error.no.railway.station.for.sp"/>');
							valid = false;
						}
						
						if (railDistance == -1) {
							valid = false;
						}
						if (valid){							
							logonGmap.updateRoute({
								spLat: spRec.coordLat,
								spLng: spRec.coordLon,
								shiptoLat: '<c:out value="${cust_WebBestellungForm.shipTo.coordLat}"/>',
								shiptoLng: '<c:out value="${cust_WebBestellungForm.shipTo.coordLon}"/>',
								durationCorrFactor: corrfactor,
								durationFieldId: 'durationTime',
								distanceFieldId: 'distanceMeters',
								delTypeCd: deliveryType,
								railDistanceMeters: railDistance,
								railDurationMinutes: railDuration,
								callback: function() {
								}			
							});
						}
					}
				});
			}
		}
		else{
			logonGmap.updateRoute({
				spLat: spRec.coordLat,
				spLng: spRec.coordLon,
				shiptoLat: '<c:out value="${cust_WebBestellungForm.shipTo.coordLat}"/>',
				shiptoLng: '<c:out value="${cust_WebBestellungForm.shipTo.coordLon}"/>',
				durationCorrFactor: corrfactor,
					durationFieldId: 'durationTime',
				distanceFieldId: 'distanceMeters',
				delTypeCd: deliveryType,
				callback: function() {
					if (deliveryType == <%=Constant.DELIVERY_TYPE_BIMODAL_ROAD%>){
    					submitWithAjax("addBimodalRailPart", false);
					}
				}			
			})
		}
	}
}

function resetMapAndRelatedData(){
		Ext.getDom('durationTime').value = 0;
		Ext.getDom('distanceMeters').value = 0;
		Ext.getDom('durationCorrFactor').value = 1; 
		logonGmap.loadMap();
	}

	<%--Enable 'Adjust deliveries' button only and only if both 2 conditions fit:--%>
	<%-- - only one material dfropdown exist on the page--%>
	<%-- - only bulk material is selected--%>
function controllAdjustDeliveries(){
	var nbItems = Ext.query('.newItem').length;
	if (nbItems == 0 && sapMatCombo.isBulk) {
		enableAdjustDeliveries();
		return true;
	}
	else {
		disableAdjustDeliveries();
		return false;
	}
}

function deleteItemRow(element, comboindex){
	var nbItems = Ext.query('.newItem').length;
	//remove the last new item	
	if (nbItems == 1){
		// Removing this item will make the first one editable again
		sapMatCombo.moreItemsPresent=false;
		hideDiv("lockDiv");
	}
	deleteThis(element);
	additionalItemCombos[comboindex]=undefined;

	controllAdjustDeliveries();
}

	function deleteThis(element){
		element.parentNode.removeChild(element);
	}
	
function addItemRow(){
	sapMatCombo.moreItemsPresent=true;
	showDiv("lockDiv");
	var index = document.getElementById("maxNumberOfAdditionalItems").value;
	var deleteItemAlt="<fmt:message key="webbestellung.tooltip.delete.item"/>";
	var deleteItemImgUrl="<crh-common:url value='/images/delete2.png'/>";
	var cellFuncs = [
	                 function(data) { return '<div><div style="float:left;"><a><img src="' +deleteItemImgUrl +'" alt="'+deleteItemAlt+'" onclick="deleteItemRow(this.parentNode.parentNode.parentNode.parentNode.parentNode,'+ index + ')" width="16" height="16"></a></div><div><input type="hidden" name="compositeIdentifier'+ index +'" styleClass="textfield" id="compositeIdentifier' + index + '" /></div></div>'; },
	                 function(data) { return '<input name="orderQuantityStr'+ index +'" class="textfield" style="width:30px" id="orderQuantityStr' + index + '" />'; },
	                 function(data) { return '<input type="hidden" name="orderedUomCd'+ index +'" styleClass="textfield" id="orderedUomCd' + index + '" />'; },
	                 function(data) { return ''; },
	                 function(data) { return ''; },
	                 function(data) { return ''; },
	                 function(data) { return ''; },
	                 function(data) { return ''; }	                 
	                 ];

	dwr.util.addRows( "newOrder", [""], cellFuncs, { 
		  rowCreator:function(options) {
	    var row = document.createElement("tr");
	    row.className="zebra1 newItem";
	    return row;
	  },
	 escapeHtml:false });

	var nthUomCombo = new Ext.ux.generalUomCombo({
		id: 'uomCombo' + index,
	    transform:'orderedUomCd' + index
	});
	
	 var nthMatCombo = new Ext.ux.WebOrderMaterialCombo({
			id: 'sapMatCombo'+index,
			name: 'compositeIdentifier'+index,
		    transform:'compositeIdentifier'+index,
			store:productStore
		 });
	 
	additionalItemCombos[index] = nthMatCombo;
	
	document.getElementById("maxNumberOfAdditionalItems").value = parseFloat(index) + 1;
	
	nthMatCombo.on('beforeSelect', function(combo, record) {
		if (Ext.isEmpty(record.data.logonPackageTypeCd)){
			Logon.message.warning('<fmt:message key="webbestellung.error.no.logon.package.cd"/>');
			return false;
		}
		var i=-1;
		var logonPackageTypeCd = null;
		while ( i < additionalItemCombos.length ){
			var currentComboRec;
			if ( i == -1 ) {
				currentCombo = sapMatCombo;
				currentComboRec = selectedSapMatRec;
			}
			else {
				if ( Ext.isEmpty(additionalItemCombos[i])) {
					i++;
					continue;
				}
				//in the case of the actual combo consider the NEW selection
				currentCombo = additionalItemCombos[i];
				currentComboRec = additionalItemCombos[i].id != combo.id ? additionalItemCombos[i].getSelectedRecord() : record;				
			}
			if ( currentComboRec && currentCombo != combo && currentComboRec.data.sapMaterialId == record.data.sapMaterialId){
					Logon.message.warning('<fmt:message key="webbestellung.error.alreadyselected"/>');
					return false;					
			}
							
			if (  currentComboRec && !logonPackageHandlingHelper.isService(currentComboRec.data.logonPackageTypeCd) ) {
				if ( Ext.isEmpty(logonPackageTypeCd) ) {
					logonPackageTypeCd = currentComboRec.data.logonPackageTypeCd;
				}
				else if (logonPackageTypeCd != currentComboRec.data.logonPackageTypeCd) {
					Logon.message.warning('<fmt:message key="webbestellung.error.combineddelivery"/>');
					return false;	
				}
				else if (logonPackageHandlingHelper.isBulk(currentComboRec.data.logonPackageTypeCd)) {
					Logon.message.warning('<fmt:message key="webbestellung.error.cannotaddmorebulk"/>');
					return false;
				}
			}
			i++;
		}
		if (Ext.isEmpty(record.data.materialPackageCd) || record.data.materialPackageCd == 'NULL') {
			Logon.message.warning('<fmt:message key="webbestellung.error.no.material.package.cd"/>');
    		return false;
		}
		if (!record.data.shippingpointId) {
			Logon.message.warning('<fmt:message key="webbestellung.error.missing.shippingpoint"/>');
			return false;
		}
		else if (record.data.shippingpointId != selectedSapMatRec.data.shippingpointId){
			Logon.message.warning('<fmt:message key="webbestellung.error.notcorrespondingshippingpoint"/>');
			return false;
		}
		if (record.data.sapMaterialId == selectedSapMatRec.data.sapMaterialId){
			//this is the first material
			Logon.message.warning('<fmt:message key="webbestellung.error.alreadyselected"/>');
			return false;
		}

		var pickupSelect = document.getElementById("pickupTypeSelect").value;
		if (pickupSelect == "<%=WebBestellungForm.PICKUP_SELECTION_DELIVERY %>" &&  record.data.deliveryTypeCd != selectedSapMatRec.data.deliveryTypeCd){
			Logon.message.warning('<fmt:message key="webbestellung.error.deliverytypemustmatch"/>');
			return false;
		}
		
		if (record.data.uomCd){
			nthUomCombo.loadAndSelectSingleUom(record.data);
		}
		Ext.getDom('orderQuantityStr'+index).value = record.data.quantity != undefined ? record.data.quantity : '1';
	});

	<%--Do not sshow Adjust deliveries button when more than one material row exist on the screen--%>
	controllAdjustDeliveries();

}

function removeWarnRow(){
	if (document.getElementById("warnRow")){
		document.getElementById("newOrder").removeChild(document.getElementById("warnRow"));
	}
}
function addWarnRow(){
	var warnSource = "<crh-common:url value='/images/warning2.png'/>";
	var cellFuncs = [
	                 function(data) { return '<div id="divActualDeliveryDat0" class="hiddenDiv"><div id="divActualDeliveryDatWarn0" class="hiddenDiv"><img src="' + warnSource + '" /></div><div id="divActualDeliveryDatTxt0">n.a.</div></div>'; }
	                 ];

	dwr.util.addRows( "newOrder", [""], cellFuncs, { 
	cellCreator:function(options){
	    var td = document.createElement("td");
	    td.colSpan=8;
	    td.style.textAlign="center";
	    return td;
	},
		  rowCreator:function(options) {
	    var row = document.createElement("tr");
	    row.id="warnRow";
	    row.className="zebra1";
	    return row;
	  },
	 escapeHtml:false });
}

function distanceRulesPopUp(){
	var message;
	if (distanceValidationPopUpText == null) {
		message = '<fmt:message key="application.errors.custordervalidation.distance.rules.popup.CHOOSE_FIRST" />';
	}
	else {
			message = distanceValidationPopUpText;
	}
	Ext.MessageBox.alert('<fmt:message key="application.errors.custordervalidation.distance.rules.popup.TITLE" />',message);
}

</script>

<table class="big_form2" cellpadding="0" cellspacing="0"
	style="padding: 0px; margin: 0px; border-collapse: collapse;">
	<tr style="border-bottom: 2px solid #599ade;">
		<td class="labelblue_bg"><span class="labelbluebig"><fmt:message
			key="webbestellung.columns.newordercreate" /></span></td>
	</tr>

	<tr>
		<td>
		<input type="hidden" id="maxNumberOfAdditionalItems" name="maxNumberOfAdditionalItems"  value="0">
		<table id="newOrderTable" class="classicblue" style="width: 100%; margin-top: 4px;">
			<colgroup>
				<col width="20%">
				<col width="6%">
				<col width="8%">
				<col width="10%">
				<col width="10%">
				<col width="5%">
				<col width="10%"> <!-- Bill to -->
				<col width="10%"> <!-- Payer -->
				<col width="21%" class="newOrderNotes">
				<col width="5%"  style="display:none"  class="copyCustomerCommentOn">
			</colgroup>
			<tbody id="newOrder">
				<tr>
					<th rowspan="2"><fmt:message key="fields.produkt.produkt" /></th>
					<th rowspan="2">#</th>
					<th rowspan="2"><fmt:message key="fields.uom.uom" /></th>
					<th colspan="2">
						<fmt:message key="fields.order.delivery.time"/>
					</th>
					<th rowspan="2" title="<fmt:message key="webbestellung.columns.alt.pickuptype" />"><fmt:message key="fields.type"/></th>
                    <th rowspan="2"><fmt:message key="fields.order.specorderindicators" /></th>
                    <th rowspan="2"><fmt:message key="fields.billto.billto" /></br>
                                    <fmt:message key="fields.payer.payer"/></th>
					<th><fmt:message key="fields.remarks" /></th>
					<th rowspan="2" style="display:none" class="copyCustomerCommentOn"><fmt:message key="fields.informdriver" /></th>
				</tr>
				<tr>
				  <td class="cell_gray">
					  <fmt:message key="fields.time.from" />
					  <c:if test="${displayShipToTimezone}">
						  <c:out value="${shipToTimeZoneFmtOffset}"/>
					  </c:if>
				  </td>
				  <td class="cell_gray">
					  <fmt:message key="fields.time.until" />
					  <c:if test="${displayShipToTimezone}">
						  <c:out value="${shipToTimeZoneFmtOffset}"/>
					  </c:if>
				  </td>
				  <td class="cell_gray"><fmt:message key="fields.customerreferencenumber.short" /></td>
				</tr>
				<tr  class="zebra1">			
				<td style="white-space:nowrap;">
					<div style="float:left;" class="hiddenDiv" id="lockDiv">
					<img src="<crh-common:url value='/images/lock_edit.png'/>" width="16" height="16">
					</div>
					<div>
					<html:hidden property="deliverySettingCompositeId" styleClass="textfield"
						styleId="deliverySettingCompositeId" />
					</div>
				</td>
				<td><html:text styleClass="textfield"
						property="orderQuantityStr" style="width:45px"
						styleId="orderQuantityStr" disabled="${isEditorDisabled}"
						onchange="handleQuantityOnChangeAdjustDeliveries();" /></td>
				<td>
					<html:select size="1"
						property="orderedUomCd" disabled="${isEditorDisabled}" styleClass="textfield"
						styleId="orderedUomCd">
					</html:select>
				</td>		
				<td style="padding:0px;">
					<table  border="0">
						<tr>
							<td><html:text property="deliveryFromDatStr"  styleClass="textfield"
								styleId="deliveryFromDatStr" /></td>
							<td><html:text property="deliveryFromTime"  styleClass="textfield"
								styleId="deliveryFromTime" /></td>
						</tr>
						<tr>
							<td colspan="2">
								<input title="<fmt:message key="application.errors.custordervalidation.distance.rules.popup.TITLE" />" type="button"
										name="validationRules" id="validationRules"
										value="<fmt:message key="application.errors.custordervalidation.distance.rules.popup.TITLE" />"
										class="ViewButtonStyle" onClick="distanceRulesPopUp();">
							</td>
						</tr>
					</table>
					</td>
					<td style="padding:0px;">
					<table class="noborder">
						<tr>
							<td><html:text property="deliveryToDatStr" styleClass="textfield"
								styleId="deliveryToDatStr" /></td>
							<td><html:text property="deliveryToTime"  styleClass="textfield"
								styleId="deliveryToTime" /></td>
						</tr>
					</table>
					</td>
					<td><input type="hidden" id="pickupTypeSelect" disabled="${isEditorDisabled}" name="pickupTypeSelect"></td>
                    <td>
                        <input type="hidden" name="orderAttributes"
                        id="orderAttributes"/>
                    </td>
                    <td>
                    <table class="noborder">
                        <tr>
                            <td><html:hidden styleClass="textfield"
                                property="billtoSapBpId"
                                styleId="billtoSapBpId" />
                            </td>
                        </tr>
                        <tr>
                            <td><html:hidden styleClass="textfield"
                            property="payerSapBpId"
                            styleId="payerSapBpId" />
                            </td>
                        </tr>
                    </table>
                    </td>
					<td>
						<html:textarea	
							property="custWeborderNote" 
							onkeypress="return checkMaxLength(event, this, 1000)" 
							styleId="custWeborderNote" 
							styleClass="${textareaCls}" 
							style="width: 100%; margin-top: 2px; padding: 0px; font-size: 11px">
						</html:textarea>
						<span
							style="white-space: nowrap; border: 0px;"> <html:text
							styleClass="textfield" size="10" maxlength="15"
							property="custWeborderRefNo"
							styleId="custWeborderRefNo" />
						</span>
					</td>
					<td style="display:none" class="copyCustomerCommentOn">
						<html:checkbox property="copyCustomerRemarkToDriver"></html:checkbox>
					</td>
				</tr>
			</tbody>
		</table>
		</td>
	</tr>

	<tr>
		<td colspan="12" class="right">
			<input disabled title="<fmt:message key="webbestellung.information.additem"/>" type="button"
					name="addRowButton" id="addRowButton"
					value="<fmt:message key="webbestellung.button.additem"/>"
					class="ViewButtonStyle"
					onClick="addItemRow()">
			<input disabled type="button" class="ViewButtonStyle" id="adjustDeliveriesBtn"
					value='<fmt:message key="bestellung.button.adjustdeliveries"/>'
					onclick="triggerAdjustDeliveriesPopUp(false)"/>&nbsp;
			<input type="button"
					name="saveNewButton" id="saveNewButton"
					value="<fmt:message key="webbestellung.button.saveorder"/>"
			<c:out value="${isEditorDisabledStr}"/> class="ViewButtonStyle"
					onClick="submitWithAjax('save', false)">
		</td>
	</tr>
</table>
