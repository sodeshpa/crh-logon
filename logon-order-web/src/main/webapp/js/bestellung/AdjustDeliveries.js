Ext.onReady(function() {
	
	var addRemoveEnabled = false;
	
	for ( var editorIndex = 0; editorIndex < jsonSplittedOrders.length; editorIndex++ ) {
		
		splittedRecords[editorIndex] = []
		
		jsonSplittedOrder = jsonSplittedOrders[editorIndex]; 
		
		// Splitted order array
		var splittedOrders = jsonSplittedOrder.ordersListFromCustOrders;
	
		// Build SDO and SDOI UI part
		for ( var idx = 0; idx < splittedOrders.length; idx++ ) {
	
			var splittedOrder = splittedOrders[idx];
			
			// Container object responsible to group controls per row
			splittedRecord = {
					
				// --- Delivery date from ---
				delFromDatField: new Ext.form.DateField({
					format: dateFormat,
					altFormats: altDateFormat,
					id: 'splittedOrder['+idx+'].deliveryFromDat',
					applyTo: 'editor_'+editorIndex+'_ordersListFromCustOrders['+idx+'].deliveryFromDatStr',
					allowBlank: false,
					width: 78,
					changedByPicker: false,
					blankText: 'Error',
					editorIndex: editorIndex,
					listeners: {
						'change': saveSelectionsAjax
					}
				}),
				
				// --- Delivery date to ---
				delToDatField: new Ext.form.DateField({
					format: dateFormat,
					altFormats: altDateFormat,
					id: 'splittedOrder['+idx+'].deliveryToDat',
					applyTo: 'editor_'+editorIndex+'_ordersListFromCustOrders['+idx+'].deliveryToDatStr',
					allowBlank: false,
					width: 78,
					changedByPicker: false,
					blankText: 'Error',
					editorIndex: editorIndex,
					listeners: {
						'change': saveSelectionsAjax
					}
				}),
		
				// --- Delivery time from ---
				delFromTimeField: new Ext.form.TimeField({
					index: splittedOrder.index,
					id: 'splittedOrder['+idx+'].delFromTime',
					increment: 15,
					format: 'H:i',
					applyTo: 'editor_'+editorIndex+'_ordersListFromCustOrders['+idx+'].deliveryFromTime',
					width: 78,
					editorIndex: editorIndex,
					listeners: {
						'change': createDeliveryTimeFromChangeListener()
					}			
				}),
		
				// --- Delivery time to ---
				delToTimeField: new Ext.form.TimeField({
					id: 'splittedOrder['+idx+'].delToTime',
					increment: 15,
					format: 'H:i',
					applyTo: 'editor_'+editorIndex+'_ordersListFromCustOrders['+idx+'].deliveryToTime',
					width: 78,
					disabled: splittedOrder.onTime,
					editorIndex: editorIndex,
					listeners: {
						'change': saveSelectionsAjax
					}			
				}),
				
				// --- OnTime ---
				onTimeCheckbox: new Ext.form.Checkbox({
					index: splittedOrder.index,
					id: 'splittedOrder['+idx+'].onTime',
					applyTo: 'editor_'+editorIndex+'_ordersListFromCustOrders['+idx+'].onTime',
					checked: splittedOrder.onTime,
					editorIndex: editorIndex,
					handler: createOnTimeCheckBoxHandler()
				}),
				
				// --- OnHold ---
				onHoldCheckbox: new Ext.form.Checkbox({
					id: 'splittedOrder['+idx+'].onHold',
					applyTo: 'editor_'+editorIndex+'_ordersListFromCustOrders['+idx+'].onHold',
					checked: splittedOrder.onHoldOrderAndLockedFlag,
					editorIndex: editorIndex,
					handler:  createOnHoldCheckboxHandler()
				}),

				// --- SP Indicator ---
				serviceProcessIndicatorList: new Ext.form.ComboBox({
					mode: 'local',
					editable: false,
					forceSelection: true,
					id: 'editor_'+editorIndex+'_ordersListFromCustOrders' + idx ,
					name: 'editor_'+editorIndex+'_ordersListFromCustOrders' + idx,
					applyTo:'editor_'+editorIndex+'_ordersListFromCustOrders[' + idx + '].serviceProcessIndicatorList',				
					allowBlank: true,
					triggerAction: 'all',
					editorIndex: editorIndex,
					listeners: {
						'select': saveSelectionsAjax
					},
					store: new Ext.data.JsonStore({
						data : jsonData[editorIndex].serviceProcessIndicatorList,
						fields: [
							{
								name: 'name2',
								type: 'auto',
								mapping: 'orderSpiTxPK',
								convert: getOrderSpiTxtFromValue()
							},
							{
								name: 'serviceProcessIndicatorId',
								type: 'auto',
								mapping: 'orderSpiTxPK',
								convert: getOrderSpiCdFromValue()
							}
						],
						emptyItem: {
							'name2' : '...&nbsp;',
							'serviceProcessIndicatorId' : '-1;'
						}
					}),
					valueField: 'serviceProcessIndicatorId',
					displayField: 'name2',
					tpl: new Ext.XTemplate(
							'<tpl for=".">',
								'<div class="x-combo-list-item">',
									'{name2} ',
								'</div>',
							'</tpl>'),
					width: 100,
					listWidth: 250,
					listAlign: 'tr-br'
				}),
				
				splittedRecordItems: new Array({
					// This is filled later dynamically
				}),
			
				index: splittedOrder.index
			
			};
			
			for ( var odx = 0; odx < splittedOrder.splittedOrderItemDatas.length; odx++ ) {
				
				var splittedOrderItemData = splittedOrder.splittedOrderItemDatas[odx];
				
				var qtyEdit = quantityEditDisabled(splittedOrder.deliveryType, splittedOrderItemData.materialType, splittedOrder.exactCustorderQtyFlag);
				
				var splittedRecordItem = {
					
					sapMaterialId: splittedOrderItemData.sapMaterialId,
					materialType: splittedOrderItemData.materialType,
					index: odx,
					
					orderedQty: new Ext.form.NumberField({
						orderIndex: idx,
						orderItemIndex: odx,
						id: 'splittedOrder['+idx+'].splittedOrderItem['+odx+'].sapMatId_'+splittedOrderItemData.sapMaterialId+'.orderedQty',
						applyTo: 'ordersListFromCustOrders['+idx+'].splittedOrderItemData['+odx+'].sapMatId_'+splittedOrderItemData.sapMaterialId+'.orderedQty',
						allowBlank: false,
						allowNegative: false,
						allowDecimals: true,
						decimalPrecision: 3,
						maxLength: 9,
						selectOnFocus: true,
						enableKeyEvents: true,
						style: 'text-align: right',
						width: 45,
						disabled: qtyEdit,
						editorIndex: editorIndex,
						listeners: {
							'change': createOrderedQtyChangeListener()
						}
					}) 
					
				};
				
				splittedRecord.splittedRecordItems[odx] = splittedRecordItem;
				
			}
			
			addRemoveEnabled = !quantityEditDisabled(splittedOrder.deliveryType, custorderMaterialType, splittedOrder.exactCustorderQtyFlag);
			
			if (addRemoveEnabled) {
				Ext.getDom('editor_'+editorIndex+'_deleteButton_'+idx).style.display='block'
			}
			
			splittedRecord.serviceProcessIndicatorList.preselectValue(splittedOrder.serviceProcessIndicatorId, true);
			splittedRecords[editorIndex][idx] = splittedRecord;
			
		}
		
		var lessThanMaxSDO = splittedOrders.length < maxSDO;
		
		if (addRemoveEnabled && lessThanMaxSDO) {
			 Ext.getDom('editor_'+editorIndex+'_addButton').style.display='block'
		}
		
		// --- OnTime Header  (toggles all) ---
		onTimeHeaderCheckbox = new Ext.form.Checkbox({
			id: 'editor_'+editorIndex+'_header.onTime',
			applyTo: 'editor_'+editorIndex+'_header.onTime',
			style: 'padding-left: 30px',
			checked: false,
			editorIndex: editorIndex,
			handler: createOnTimeHeaderCheckboxHandler()
		});
		
		// --- OnHold Header (toggles all) ---
		onHoldHeaderCheckbox = new Ext.form.Checkbox({
			id: 'editor_'+editorIndex+'_header.onHold',
			applyTo: 'editor_'+editorIndex+'_header.onHold',
			checked: custorderOnHoldEnabled,
			editorIndex: editorIndex,
			handler: createOnHoldHeaderCheckboxHandler()
		});

		// --- SP Indicator ---
		serviceProcessIndicatorListHeader = new Ext.form.ComboBox({
			mode: 'local',
			editable: false,
			forceSelection: true,
			id: 'editor_'+editorIndex+'_header.serviceProcessIndicatorList',
			name: 'editor_'+editorIndex+'_header.serviceProcessIndicatorList',
			applyTo:'editor_'+editorIndex+'_header.serviceProcessIndicatorList',				
			allowBlank: true,
			triggerAction: 'all',
			editorIndex: editorIndex,
			listeners: {
				'select': createSPIListHeaderSelectListener()
			},
			store: new Ext.data.JsonStore({
				data : jsonData[editorIndex].serviceProcessIndicatorList,
				fields: [
					{name: 'name2', type: 'auto', mapping: 'orderSpiTxPK', convert: getOrderSpiTxtFromValue()},
					{name: 'serviceProcessIndicatorId', type: 'auto', mapping: 'orderSpiTxPK', convert: getOrderSpiCdFromValue()}
				],
				emptyItem: {
					'name2' : '...&nbsp;',
					'serviceProcessIndicatorId' : '-1;'
				}
			}),
			valueField: 'serviceProcessIndicatorId',
			displayField: 'name2',
			tpl: new Ext.XTemplate(
					'<tpl for=".">',
						'<div class="x-combo-list-item">',
							'{name2} ',
						'</div>',
					'</tpl>'),
			width: 100,
			listWidth: 250,
			listAlign: 'tr-br'
		});
		
		// Bind to adjust times UI controls
		open1From = new Ext.form.TimeField({
			id: 'editor_'+editorIndex+'_open1From',
			increment: 15,
			format: 'H:i',
			applyTo: 'editor_'+editorIndex+'_open1From',
			disabled: false,
			width: 60		
		});
	
		open1To = new Ext.form.TimeField({
			id: 'editor_'+editorIndex+'_open1To',
			increment: 15,
			format: 'H:i',
			applyTo: 'editor_'+editorIndex+'_open1To',
			disabled: false,
			width: 60		
		});
	
		open2From = new Ext.form.TimeField({
			id: 'editor_'+editorIndex+'_open2From',
			increment: 15,
			format: 'H:i',
			applyTo: 'editor_'+editorIndex+'_open2From',
			disabled: false,
			width: 60		
		});
	
		open2To = new Ext.form.TimeField({
			id: 'editor_'+editorIndex+'_open2To',
			increment: 15,
			format: 'H:i',
			applyTo: 'editor_'+editorIndex+'_open2To',
			disabled: false,
			width: 60		
		});
	
		Ext.get('editor_'+editorIndex+'_buttonApplyOpeningTimes').on('click', createButtonApplyOpeningTimesClickHandler());

		// AdjustDeliveries and Save button should only be displayed for the last editor 
		if (editorIndex == jsonSplittedOrders.length - 1) {
			var div = document.getElementById('editor_'+editorIndex+'_buttonPanel');
			div.innerHTML += '<input type="button" class="ViewButtonStyle" value="'+cancelButtonLabel+'" id="buttonCancel">';
			div.innerHTML += ' ';
			div.innerHTML += '<input type="button" class="ViewButtonStyle" value="'+saveButtonLabel+'" id="buttonSave">';
		}
		
	}
	
	Ext.get('buttonSave').on('click', function() {
	
		var materialWarning = []; 
		
		var qtyAlright = true;
		
		// First line of warning message
		var qtySumCheckWarningMsg = qtySumCheckWarning1;
		
		qtySumCheckWarningMsg = qtySumCheckWarningMsg + "<table>";
		
		for (var editorIndex = 0; editorIndex < splittedRecords.length; editorIndex++ ) {
		
			for ( var idx = 0; idx < splittedRecords[editorIndex].length; idx++ ) {
				
				var splittedRecord = splittedRecords[editorIndex][idx];
				
				for ( var odx = 0; odx < splittedRecord.splittedRecordItems.length; odx++ ) {
					
					var splittedRecordItem = splittedRecord.splittedRecordItems[odx];
					
					// Collecting the id 
					var sumCurrentOrderedQtyInBase = Ext.get("sumCurrentOrderedQtyInBase_"+splittedRecordItem.sapMaterialId).dom.childNodes[0].data.trim();
					var sumOriginalOrderedQtyInBase = Ext.get("sumOriginalOrderedQtyInBase_"+splittedRecordItem.sapMaterialId).dom.childNodes[0].data.trim();
					var sumOrderedQtyMaterial = Ext.getDom("sumOrderedQtyMaterial_"+splittedRecordItem.sapMaterialId).value;
					var sumOrderedQtyMaterialUom = Ext.getDom("sumOrderedQtyMaterialUom_"+splittedRecordItem.sapMaterialId).value;
					var qtyHeader = {
							sapMaterialId: splittedRecordItem.sapMaterialId,
							sapMaterialName: sumOrderedQtyMaterial,
							sapMaterialUom: sumOrderedQtyMaterialUom,
							current: sumCurrentOrderedQtyInBase,
							original: sumOriginalOrderedQtyInBase,
							materialType: splittedRecordItem.materialType
					};
					
					if (materialWarning.indexOf(qtyHeader.sapMaterialId) == -1) {
					
						// If this quantity column is not consistent and not service material
						if (qtyHeader.materialType != "SERVICE" && sumCurrentOrderedQtyInBase != sumOriginalOrderedQtyInBase) {
							
							qtySumCheckWarningMsg = qtySumCheckWarningMsg + "<tr><td>";
							
							qtyAlright = false;
							
							// Replace place holders 
							var qtySumCheckResult = qtySumCheckWarning2;
							// Quantity
							qtySumCheckResult = qtySumCheckResult.replace("{0}", qtyHeader.current);
							qtySumCheckResult = qtySumCheckResult.replace("{3}", qtyHeader.original);
							
							// Material Name => need to call twice
							qtySumCheckResult = qtySumCheckResult.replace("{2}", qtyHeader.sapMaterialName);
							qtySumCheckResult = qtySumCheckResult.replace("{5}", qtyHeader.sapMaterialName);
							
							// Material Uom => need to call twice
							qtySumCheckResult = qtySumCheckResult.replace("{1}", qtyHeader.sapMaterialUom);
							qtySumCheckResult = qtySumCheckResult.replace("{4}", qtyHeader.sapMaterialUom);
							
							qtySumCheckWarningMsg = qtySumCheckWarningMsg + '<br />' + qtySumCheckResult;
							
							qtySumCheckWarningMsg = qtySumCheckWarningMsg + "</td></tr>";
							
							materialWarning.push(qtyHeader.sapMaterialId); 
							
						}
					
					}
					
				}
				
			}
		
		}
		
		qtySumCheckWarningMsg = qtySumCheckWarningMsg + "</table>";
		
		qtySumCheckWarningMsg = qtySumCheckWarningMsg + qtySumCheckWarning3;
		
		
		if (qtyAlright) {
			saveOrders();
		}
		else {
			
			Ext.Msg.show({
				title:qtySumCheckWarningTitle,
				msg: qtySumCheckWarningMsg,
				buttons: {cancel: confirmCancelButtonText, ok: confirmSaveButtonText},
				fn : function(btn) {
					if (btn == 'ok'){
						saveOrders();
					}
				}
			});
			
		}
		
	});
	
	Ext.get('buttonCancel').on('click', function() {
		cancelCustOrdCreation();
	} );
	
});

function createOnHoldCheckboxHandler(){
	return function(element, checked){
		saveSelectionsAjax();
	}
}

function createButtonApplyOpeningTimesClickHandler(){
	return function() {
		
		hidePopup();
		
		// We need to parse the editorIndex for the *actual* (n-th) button
		var buttonId =this.id;
		var editorIndex = buttonId.split("_")[1];
		
		OrderAjaxService.applyOpeningTimes(
			editorIndex,
			open1From.value,
			open1To.value,
			open2From.value,
			open2To.value,
			// when returned we have to reload the page
			function() {
				reloadPopup();
			}
		);
		
	}
}

function createOnTimeCheckBoxHandler(){
	return function(element, checked) {
		// Set the "Time to" field to the value of the "Time from" field if "OnTime" is set.
		var thisSplittedRecord = splittedRecords[this.editorIndex][this.index];
		thisSplittedRecord.delToTimeField.setValue(thisSplittedRecord.delFromTimeField.getValue());
		thisSplittedRecord.delToTimeField.setDisabled(checked);			
		saveSelectionsAjax();
	}
}

function createOnHoldHeaderCheckboxHandler(){
	return function(element, checked) {
		for ( var idx = 0; idx < splittedRecords[this.editorIndex].length; idx++ ) {
			var splittedRecord = splittedRecords[this.editorIndex][idx];
			splittedRecord.onHoldCheckbox.setValue(checked);
		}
	}
}


function createOnTimeHeaderCheckboxHandler(){
	return function(element, checked) {
		for ( var idx = 0; idx < splittedRecords[this.editorIndex].length; idx++ ) {
			var splittedRecord = splittedRecords[this.editorIndex][idx];
			splittedRecord.onTimeCheckbox.setValue(checked);
		}
	}
}

function createSPIListHeaderSelectListener(){
	return function(combo, record){
		var currentValue = record.data.serviceProcessIndicatorId;
		for ( var idx = 0; idx < splittedRecords[this.editorIndex].length; idx++ ) {
			var splittedRecord = splittedRecords[this.editorIndex][idx];
			splittedRecord.serviceProcessIndicatorList.preselectValue(currentValue);
		}
	}
}

/**
 * Sets the Time to field to be equal to Time from, than call saveSelectionsAjax
 * @returns {Function}
 */
function createDeliveryTimeFromChangeListener(){
	return function(){
		
		var thisSplittedRecord = splittedRecords[this.editorIndex][this.index];
		
		// Set the "Time to" field to the value of the "Time from" field
		if (thisSplittedRecord.onTimeCheckbox.checked) {
			thisSplittedRecord.delToTimeField.setValue(this.value);
		}
		
		saveSelectionsAjax();
	}
}

function getOrderSpiTxtFromValue(){
	return function(value, rec) {
		var returnValue = value;
		if (value.orderSpiBs !== undefined && value.orderSpiBs !== null) {
			returnValue = value.orderSpiBs.orderSpiTxt;
		}
		else{
			if (returnValue === ""){
				returnValue = "-";
			}
		}
		return Ext.util.Format.htmlDecode(returnValue);
	}
}

function getOrderSpiCdFromValue(){
	return function(value, rec) {
		var returnValue = value;
		if (value.orderSpiBs !== undefined && value.orderSpiBs !== null) {
			returnValue = value.orderSpiBs.orderSpiCd;
		}
		else{
			if (returnValue === ""){
				returnValue = "-1";
			}
		}
		return Ext.util.Format.htmlDecode(returnValue);
	}
}

/**
 * Creates a change listener for OrderedQty object
 * @returns {Function}
 */
function createOrderedQtyChangeListener(){
	return function(){
		
		// Check for correct input
		var newValue = this.getValue();
		if (newValue == null || newValue === "") {
			this.setValue(0);
		}
		
		var sumQty;
		var sapMaterialId = splittedRecords[this.editorIndex][this.orderIndex]
								.splittedRecordItems[this.orderItemIndex].sapMaterialId;
		
		var orders = collectOrdersFromSelection(this.editorIndex);
		
		OrderAjaxService.sumOrderedQtyInBase(
			this.editorIndex,
			orders,
			sapMaterialId,
			// when returned we have to reload the page
			function(result) {
				sumQty = result;
				var sumCurrentOrderedQtyInBase = Ext.getDom("sumCurrentOrderedQtyInBase_" + sapMaterialId);
				sumCurrentOrderedQtyInBase.innerHTML = '' + sumQty;
			}
		);
		
		saveSelectionsAjax();
		
	}
}

function saveOrders() {
	
	if (validateDeliveryDate()) {
		
		var ordersPerEditors = [];
		
		for (var editorIndex = 0; editorIndex < splittedRecords.length; editorIndex++ ) {
		
			var orders = collectOrdersFromSelection(editorIndex);
			
			ordersPerEditors[editorIndex] = orders;
			
		}
		
		hidePopup();
		
		Ext.get('divEditors').mask('Processing...', 'x-mask-loading');
		
		OrderAjaxService.sendSplittedOrderData(ordersPerEditors);
		
		OrderAjaxService.clearZeroOrders(function() {
			
			var params = {};
			addShiptoAndNewOrderFormData(params, { isCustomerOrder: true });
			params.action = 'saveCustomerOrder';
			
			var divOrderProcessing = Ext.get('divOrderProcessing');
			divOrderProcessing.load({
				url: '../bestellung/Bestellung.do',
				params: params,
				text: 'Saving...', // TODO litdict
				scripts: true,
				nocache: true
			});
			
		});
		
	}
	
}

/**
 * Validate Delivery date/time for all splitted records.
 * Valid delivery date: "delivery date/time From" > "delivery date/time To"
 * 
 * @returns {Boolean}
 */
function validateDeliveryDate() {
	
	for (var editorIndex = 0; editorIndex < splittedRecords.length; editorIndex++ ) {
		
		for (var idx = 0; idx < splittedRecords[editorIndex].length; idx++ ) {
			
			var splittedRecord = splittedRecords[editorIndex][idx];
			
			var delFromDateTime = Date.parseDate(splittedRecord.delFromDatField.value + splittedRecord.delFromTimeField.getValue(), 'd.m.yH:i');
			var delToDateTime = Date.parseDate(splittedRecord.delToDatField.value + splittedRecord.delToTimeField.getValue(), 'd.m.yH:i');
			
			if (delFromDateTime === undefined || delToDateTime === undefined || delFromDateTime > delToDateTime) {
				Logon.message.warning(deliveryDateWarningMsg.replace('{0}', editorLocatorMsg.replace('@', editorIndex+1)));
				return false;
			}
		
		}
	
	}
	return true;
}

/**
 * => Edit Service Material quantities per Single Delivery Order of orders where order splitting is enabled, 
 *	 e.g. Road direct and Pickup (must have)
 * => Edit Order Quantities of bag materials (must have)
 * => Edit Order Quantities of bulk materials, where the order splitting is enabled, 
 *	 e.g. Road direct and Pickup and the Customer Order Item Exact Quantity Flag is set to false.
 * 
 * => Add/remove Single Delivery Order Proposals if the Customer Order is a Bag Order (must have)
 * => Add/remove Single Delivery Order Proposals if the Customer Order is a Bulk Order and the order 
	splitting is enabled, e.g. Road direct and Pickup and Exact Customer Order is set to false (nice to have).
*/
function quantityEditDisabled(deliveryType, materialType, exactCustorderQtyFlag) {
	
	if ("BULK" == materialType) {
		if ("PICKUP" == deliveryType || "ROAD" == deliveryType) {
			return exactCustorderQtyFlag;
		}
		return true;
	}
	else if ("BAG" == materialType) {
		return false;
	}
	else if ("SERVICE" == materialType) {
		if ("PICKUP" == deliveryType || "ROAD" == deliveryType) {
			return false;
		}
		return true;
	}
	
	return true;
	
}

/**
 * Iterates over the splittedRecords array which groups the controls per row
 * @returns the collection of javascript objects which reflects the SplittedOrderData java class
 */
function collectOrdersFromSelection(editorIndex) {
	
	var orders = [];
	
	for ( var idx = 0; idx < splittedRecords[editorIndex].length; idx++ ) {
		
		var splittedRecord = splittedRecords[editorIndex][idx];
		
		// Creating orders that will be mapped to SplittedOrderData objects
		var order = {
			deliveryFromDatStr: splittedRecord.delFromDatField.value,
			deliveryToDatStr: splittedRecord.delToDatField.value,
			deliveryFromTime:  splittedRecord.delFromTimeField.value,
			deliveryToTime:  splittedRecord.delToTimeField.value,
			index: splittedRecord.index,
			onTime: splittedRecord.onTimeCheckbox.checked,
			onHoldOrderAndLockedFlag: splittedRecord.onHoldCheckbox.checked,
			serviceProcessIndicatorId: splittedRecord.serviceProcessIndicatorList.value,
			splittedOrderItemDatas: []
		};
		
		for ( var odx = 0; odx < splittedRecord.splittedRecordItems.length; odx++ ) {
		
			var splittedRecordItem = splittedRecord.splittedRecordItems[odx];
			
			var splittedOrderItemData = {
			
				orderedQuantity: splittedRecordItem.orderedQty.getValue(),
				sapMaterialId: splittedRecordItem.sapMaterialId,
				index: splittedRecordItem.index
			
			};
			
			order.splittedOrderItemDatas[odx] = splittedOrderItemData; 
			
		}
		
		orders[idx] = order;
		
	}
	
	return orders;
	
}

/**
 * Deletes the record based on the index using an Ajax call.
 * The screen will be reloaded
 * @param index
 */
function deleteSplittedOrderData(editorIndex, index) {

	// Can only delete record if we have more than one
	if (splittedRecords[editorIndex].length > 1) {
		OrderAjaxService.deleteSplittedOrderData(editorIndex, index, function() {
			reloadPopup();			
		});
	}
	
}

/**
 * Adds a SplittedOrderData record to the given editor
 */
function addSplittedOrderData(editorIndex) {
	
	OrderAjaxService.addSplittedOrderData(editorIndex, function() {
		reloadPopup();
	});
	
}

/**
 * Saves the UI state using an Ajax call.
 */
function saveSelectionsAjax() {
	
	var ordersPerEditors = [];
	
	for (var editorIndex = 0; editorIndex < splittedRecords.length; editorIndex++ ) {
	
		var orders = collectOrdersFromSelection(editorIndex);
		
		ordersPerEditors[editorIndex] = orders;
		
	}
	
	OrderAjaxService.sendSplittedOrderData(ordersPerEditors);

}

/**
 * Reloads the full adjust deliveries popup using the modell available on the
 * server side. Hence saving the UI state (Ajax) is crucial.
 */
function reloadPopup() {
	
	Ext.get('divEditors').unmask();
	var params = {};
	addShiptoAndNewOrderFormData(params, { isCustomerOrder: true });
	params.action = 'showSinDelOrdSplitPopup2';
	params.skipSplit = 'true';
	var loadingMessage = '<fmt:message key="extjs.processing"/>'; 
	showPopup('../bestellung/Bestellung.do', loadingMessage, params);
	
}