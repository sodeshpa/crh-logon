Ext.onReady(function() {
	
	var addRemoveEnabled = false;

	//for ( var editorIndex = 0; editorIndex < jsonSplittedOrders.length; editorIndex++ ) {
		var editorIndex = 0;
		splittedRecords[editorIndex] = []
		
		//jsonSplittedOrder = jsonSplittedOrders[editorIndex];

		// Splitted order array
		var splittedOrders = jsonSplittedOrders;
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
						'change': saveSelectionsAjax
					}			
				}),
		
				// --- Delivery time to ---
				delToTimeField: new Ext.form.TimeField({
					id: 'splittedOrder['+idx+'].delToTime',
					increment: 15,
					format: 'H:i',
					applyTo: 'editor_'+editorIndex+'_ordersListFromCustOrders['+idx+'].deliveryToTime',
					width: 78,
					editorIndex: editorIndex,
					listeners: {
						'change': saveSelectionsAjax
					}			
				}),

				// --- OnHold ---
				onHoldCheckbox: new Ext.form.Checkbox({
					id: 'splittedOrder['+idx+'].onHold',
					applyTo: 'editor_'+editorIndex+'_ordersListFromCustOrders['+idx+'].onHold',
					checked: splittedOrder.onHoldOrderAndLockedFlag,
					editorIndex: editorIndex,
					handler:  createOnHoldCheckboxHandler()
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
			
			splittedRecords[editorIndex][idx] = splittedRecord;
			
		}
		
		var lessThanMaxSDO = splittedOrders.length < maxSDO;
		
		if (addRemoveEnabled && lessThanMaxSDO) {
			 Ext.getDom('editor_'+editorIndex+'_addButton').style.display='block'
		}
		
		// --- OnHold Header (toggles all) ---
		onHoldHeaderCheckbox = new Ext.form.Checkbox({
			id: 'editor_'+editorIndex+'_header.onHold',
			applyTo: 'editor_'+editorIndex+'_header.onHold',
			checked: custorderOnHoldEnabled,
			editorIndex: editorIndex,
			handler: createOnHoldHeaderCheckboxHandler()
		});
	//}

	function checkOrderedQuantityChanges(materialWarning, qtyAlright) {
		// First line of warning message
		var qtySumCheckWarningMsg = qtySumCheckWarning1; //Sum of planned deliveries is less (or more) then the Customer Order Item quantity:

		qtySumCheckWarningMsg = qtySumCheckWarningMsg + "<table>";

		for (var editorIndex = 0; editorIndex < splittedRecords.length; editorIndex++) {

			for (var idx = 0; idx < splittedRecords[editorIndex].length; idx++) {

				var splittedRecord = splittedRecords[editorIndex][idx];

				for (var odx = 0; odx < splittedRecord.splittedRecordItems.length; odx++) {

					var splittedRecordItem = splittedRecord.splittedRecordItems[odx];

					// Collecting the id
					var sumCurrentOrderedQtyInBase = Ext.get("sumCurrentOrderedQtyInBase_" +
						splittedRecordItem.sapMaterialId).dom.childNodes[0].data.trim();
					var sumOriginalOrderedQtyInBase = Ext.get("sumOriginalOrderedQtyInBase_" +
						splittedRecordItem.sapMaterialId).dom.childNodes[0].data.trim();
					var sumOrderedQtyMaterial = Ext.getDom("sumOrderedQtyMaterial_" + splittedRecordItem.sapMaterialId).value;
					var sumOrderedQtyMaterialUom = Ext.getDom("sumOrderedQtyMaterialUom_" +
						splittedRecordItem.sapMaterialId).value;
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
		return {qtySumCheckWarningMsg: qtySumCheckWarningMsg, qtyAlright: qtyAlright};
	}

	Ext.get('buttonSave').on('click', function() {




		var materialWarning = [];

		var qtyAlright = true;

		qtyAlright = validateAllOrderedQuantityAmount();

		if (!qtyAlright){
			return;
		}

		var result = checkOrderedQuantityChanges(materialWarning, qtyAlright);
		var qtySumCheckWarningMsg = result.qtySumCheckWarningMsg;
		qtyAlright = result.qtyAlright;


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

/*
 * Workaround for the adjust deliveries popup for web orders (CRHLOGON-127).
 * See comment in WebOrderAjaxService.cancelWebAdjustDeliveriesPopUp()
 */
function cancelCustOrdCreation() {
	WebOrderAjaxService.cancelWebAdjustDeliveriesPopUp(function() {
		hidePopup();
		enableAdjustDeliveries();
		enableSave();
		enableAdd();
		// In some cases, the mask is the child of divNewOrders directy rather of divOrderEditor
		Ext.get('divNewOrders').unmask();
	});
}

/**
 The selected editor will be masked. This is used when multiple bulk order editors are visible
 Item divs can be missing when items are deleted from the list. In this cases we have wholes.
 This code checks for those.
 */
function unMaskEditor(idx) {

	unMaskDiv('divOrderEditor' + idx);

	for (var itemIdx = 1; itemIdx < editorList[idx].orderItemCnt; itemIdx++) {

		unMaskDiv('divOrderItem_' + idx + '_' + itemIdx);
	}
}

/**
 Takes the divId and looks up the corresponding div object. Then it will look up
 all the ExtJS mask divs and delete them.
 !!! This solution is used instead of div.unmask() because of it's bugs
 */
function unMaskDiv(divId) {

	var div = Ext.get(divId);

	if (div && div._mask) {
		div.unmask();
	}
}

/**
 Repaint all editors because of IE behavior
 */
function repaintEditors() {
	var div = Ext.get('bestellungForm2PerEditor0');
	div.parent().parent().repaint();
}

function createOnHoldCheckboxHandler(){
	return function(element, checked){
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

function validateOrderedQuantityAmount(quantityValue){
	var errorMessage = '';
	if (quantityValue > vehicleMaxLoadInOrderedUom){
		// Replace place holders
		errorMessage = qtySumCheckWarning4;
		errorMessage = errorMessage.replace("{0}", vehicleMaxLoadInOrderedUom);
		errorMessage = errorMessage.replace("{1}", orderedUomCd);
		Ext.MessageBox.alert(qtySumCheckWarningTitle,errorMessage);
		return true;
	}
	return false;
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

		validateOrderedQuantityAmount(newValue);

		var sumQty;
		var sapMaterialId = splittedRecords[this.editorIndex][this.orderIndex]
								.splittedRecordItems[this.orderItemIndex].sapMaterialId;
		
		var orders = collectOrdersFromSelection(this.editorIndex);

		WebOrderAjaxService.sumOrderedQtyInBase(
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
		
		Ext.get('divNewOrders').mask('Processing...', 'x-mask-loading');
		
		WebOrderAjaxService.sendSplittedOrderData(ordersPerEditors);
		
		WebOrderAjaxService.clearZeroOrders(function() {
			
			var params = {};
			addFormElements(params, "cust_WebBestellungForm");
			params.formAction = 'save';
			params.saveLocation = 'savedOnAdjustDeliveries';
			
			var divOrderProcessing = Ext.get('updateDiv');
			divOrderProcessing.load({
				url: 'WebBestellung.do',
				params: params,
				text: 'Saving...', // TODO litdict
				scripts: true,
				nocache: true,
				callback:	function(records, options, success) {
					Ext.get('divNewOrders').unmask();
				}
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

function validateAllOrderedQuantityAmount() {

	for ( var idx = 0; idx < splittedRecords[0].length; idx++ ) {

		var splittedRecord = splittedRecords[0][idx];

		for ( var odx = 0; odx < splittedRecord.splittedRecordItems.length; odx++ ) {

			var splittedRecordItem = splittedRecord.splittedRecordItems[odx];

			var orderedQuantity = splittedRecordItem.orderedQty.getValue();

			/*
				When exactCustOrderQuantity flag is turned on quantity is not editable.
				Skip quantity validation in this special case.
			 */
			var isQuantityEditDisable = splittedRecordItem.orderedQty.disabled;
				if (!isQuantityEditDisable && validateOrderedQuantityAmount(orderedQuantity)){
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
			onHoldOrderAndLockedFlag: splittedRecord.onHoldCheckbox.checked,
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
		WebOrderAjaxService.deleteSplittedOrderData(editorIndex, index, function() {
			reloadPopup();			
		});
	}
	
}

/**
 * Adds a SplittedOrderData record to the given editor
 */
function addSplittedOrderData(editorIndex) {
	
	WebOrderAjaxService.addSplittedOrderData(editorIndex, function() {
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
	
	WebOrderAjaxService.sendSplittedOrderData(ordersPerEditors);

}

/**
 * Reloads the full adjust deliveries popup using the modell available on the
 * server side. Hence saving the UI state (Ajax) is crucial.
 */
function reloadPopup() {
	
	Ext.get('divNewOrders').unmask();
	var params = {};
	addFormElements(params, "cust_WebBestellungForm");
	params.action = 'prepareAdjustDeliveries2';
	params.skipSplit = 'true';
	var loadingMessage = '<fmt:message key="extjs.processing"/>'; 
	showPopup('WebBestellung.do', loadingMessage, params);
	
}
