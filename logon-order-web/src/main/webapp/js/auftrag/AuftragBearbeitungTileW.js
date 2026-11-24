function resetRow(loadId, nettoMeasure, taraMeasure, loadingpointId, uom){
	
	if (document.getElementById(loadId + 'inputNettoMeasure') != null) {
		document.getElementById(loadId + 'inputNettoMeasure').value = nettoMeasure;		
	}
	if (document.getElementById(loadId + 'inputTaraMeasure')!== null){
		document.getElementById(loadId + 'inputTaraMeasure').value = taraMeasure;
	}
	if (document.getElementById(loadId + 'inputLoadingPointId') !== null){		
		document.getElementById(loadId + 'inputLoadingPointId').value = loadingpointId;
		changeWeighSystemButton(loadId);
	}
}

function cancelAnWeighSystem(loadId){
	var params = {"formAction":actions.CANCEL_ON_WEIGH_SYSTEM,"inputLoadId" : loadId};
	var diva = Ext.get('updateJS');
	diva.load({
		url: 'AuftragBearbeitung.do', 
		params: params,
		text: texts.extjsProcessing,
		scripts: true
	});
}

function generatePdf(orderId) {
	//renders message 
	var params = {"formAction":actions.GENERATE_PDF,"inputOrderId" : orderId};
	addFormElements(params, "AuftragBearbeitungForm");
	var diva = Ext.get('updateJS');
	diva.load({
		url: 'AuftragBearbeitung.do', 
		params: params,
		text: texts.extjsProcessing,
		scripts: true
	});
	}
	
function selectContainer(orderItemId) {
	var path = 'AuftragBearbeitung.do?formAction='+ actions.CHANGE_CONTAINER + '&' + constants.ORDER_ITEM_ID + '=' +orderItemId;
	showPopup(path);
}

function setLoadedAsPlanned(loadId, type, baseUomCd, plannedLoadingTime){

		Ext.MessageBox.confirm(LitDict['auftragbearbeitung.messagebox.confirm.title'],
			LitDict.fmt('auftragbearbeitung.messagebox.confirm.load.as.planned',plannedLoadingTime),
			function(btn) {
				if (btn == 'yes') {
					checkAndSave(loadId, type, baseUomCd, actions.SET_LOADED_AS_PLANNED);
				}
			});

}

function confirmLoading(loadId, type, baseUomCd, loadingOnCurrentDay){
	if (!loadingOnCurrentDay){
		Ext.MessageBox.confirm(LitDict['auftragbearbeitung.messagebox.confirm.title'],
			LitDict.fmt('auftragbearbeitung.messagebox.confirm.load.not.on.current.day'),
			function(btn) {
				if (btn == 'yes') {
					checkAndSave(loadId, type, baseUomCd);
				}
			});
	}
	else{
		checkAndSave(loadId, type, baseUomCd);
	}

}

function checkAndSave(loadId, type, baseUomCd, inputAction){
		initializeListVars();
		
		//this js will render the AuftragBearbeitungMessageTile in case there was an error, the page is reloaded in case no error happened
		//reloading will be started by the messageTile
		var loadingPointId = -1;
		if ( document.getElementById(loadId + 'inputLoadingPointId') !== null ){			
			 loadingPointId = document.getElementById(loadId + 'inputLoadingPointId').value;
		}

		var params;
		var action = inputAction ? inputAction : actions.SAVE_MEASURE_DATA;
		
		if(type  == "bulk") {
			var inputNettoMeasureValue;
			var inputTaraMeasureValue;
			
			if (baseUomCd == UOM_MTQ) {
				//Special case loading in cubic meters
				inputNettoMeasureValue = document.getElementById(loadId + 'inputOrderItemLoadedQuantity').value;
				baseUomCd = document.getElementById(loadId + 'inputOrderItemLoadedUomCd').value;
				
			} 
			else if (baseUomCd == UOM_TONS) {
				//Normal case loading in tons
				
				// LOGON-6715
				// make sure field is ton
				var uomSelect = Ext.get(loadId + 'selectNetQtyUom');
				if(uomSelect.dom.value !== UOM_TONS) {
					// LOGON-7093
					// save load quantity in the UoM selected by the user, don't convert client side
					baseUomCd = uomSelect.dom.value;
				}
				inputNettoMeasureValue = document.getElementById(loadId + 'inputNettoMeasure').value;
				if ( document.getElementById(loadId + 'inputTaraMeasure') !== null) {
					inputTaraMeasureValue = document.getElementById(loadId + 'inputTaraMeasure').value;		
				}	
			}
			
			params = {
					"formAction":action, 
					"inputLoadingPointId" : loadingPointId, 
					"inputLoadId" : loadId,
					"baseUomCd" : baseUomCd,
					"inputNettoMeasureValue": (inputNettoMeasureValue !== undefined)? inputNettoMeasureValue : "",
					"inputTaraMeasureValue": (inputTaraMeasureValue !== undefined ) ? inputTaraMeasureValue : ""
			};
		}
		else if (type == "bag") {
			params = {"formAction":action, "inputLoadingPointId" : loadingPointId, "inputLoadId" : loadId};			
			addBagParamsForLoad(loadId, params);
		}
		var diva = Ext.get('updateJS');
		diva.load({
			url: 'AuftragBearbeitung.do', 
			params: params,
			text: texts.extjsProcessing,
			scripts: true
		});
}
	
	function addBagParamsForLoad(loadid, params){
		var inputs = document.getElementsByTagName("input");
		var val;
		for (var i=0; i<inputs.length;i++) {
			if(inputs[i].name.indexOf(loadid+"inputOrderItemLoadedQuantity") != -1){
				val = inputs[i].value;
				params[inputs[i].name]=val;
			}
			if(inputs[i].name.indexOf(loadid+"inputOrderItemLoadedUomCd") != -1){
				val = inputs[i].value;
				params[inputs[i].name]=val;
			}
		}		
	}

	function editLoadingpoint(shippingPoint, material, loadId){
		var params = {"orderShippingId": shippingPoint , "formAction":actions.CHANGE_LOADINGPOINT, "orderProductId": material,  "inputLoadId" : loadId};
		var diva = Ext.get("loadingpointDiv"+loadId);
		diva.load({
			url: 'AuftragBearbeitung.do', 
			params: params,
			text: texts.extjsProcessing,
			scripts: true
		});
	}

	function cancelLoading(loadId) {
		var action = actions.ACTION_CANCEL_LOADING;
		var params = {
			"formAction" : action,
			"inputLoadId" : loadId
		};
		var diva = Ext.get('updateJS');
		diva.load({
			url: 'AuftragBearbeitung.do', 
			params: params,
			text: texts.extjsLoading,
			scripts: true
		});
	}
	
	function transmitToWeighSystem(id){
		var loadingPointId = -1;
		if ( document.getElementById(id + 'inputLoadingPointId') !== null ){
			loadingPointId = document.getElementById(id + 'inputLoadingPointId').value;
		}		
		var action = actions.TRANSMIT_TO_WEIGH_SYSTEM;
		var params = {"formAction":action, "inputLoadId" : id, "inputLoadingPointId" : loadingPointId};
		var diva = Ext.get('updateJS');
		diva.load({
			url: 'AuftragBearbeitung.do', 
			params: params,
			text: texts.extjsLoading,
			scripts: true
		});
	}
	
	function openLdsInformationPopup(id) {
		var params = {"inputLoadId" : id};
	    var diva = Ext.get('logonPopup');
	    diva.hide();
	    diva.load( {
	        url:'LdsInformationPopupEntry.do',
	        params: params,
	        text: texts.extjsLoading,
	        scripts: true
	    });
	    
	    var greyLayer = Ext.get('grayout');
	    grayoutScreen(greyLayer);
	}
	
	function changeWeighSystemButton(loadId){
		//is called when changing the loadingpoint Id changes the state fo the an WeighSystem button (disabled/enabled)
		var loadingPointId = -1;
		if ( document.getElementById(loadId + 'inputLoadingPointId') !== null ){
			loadingPointId = document.getElementById(loadId + 'inputLoadingPointId').value;
		}		
		var params = {"formAction":actions.SELECT_LOADINGPOINT,"inputLoadingPointId" : loadingPointId, "inputLoadId" : loadId};
		var diva = Ext.get('updateJS');
		diva.load({
			url: 'AuftragBearbeitung.do', 
			params: params,
			text: texts.extjsLoading,
			scripts: true
		});
	}
	
	
	function updateGrossOrNetto(loadId, source, value) {
		if (document.getElementById(loadId + 'inputTaraMeasure')=== null){
			return false;
		}
		if(document.getElementById(loadId + 'inputNettoMeasure') === null){
			return false;
		}
		if(document.getElementById(loadId + 'grossMeasure') === null){
			return false;
		}
		var tare = document.getElementById(loadId + 'inputTaraMeasure').value;
		//go for masterdata, and fill to tare
		if (tare === "" && value !=="" ) {
			tare = document.getElementById(loadId + 'tareMasterDataMeasure').value;
			document.getElementById(loadId + 'inputTaraMeasure').value = tare;
		}
		tare = tare.replace(/,/,".");
		tare = parseFloat(tare);
		var result;
		var uomSelect, selectedOption, factor;
		if (source=='gross'){
			value = value.replace(/,/,".");
			value = parseFloat(value);
			if (isNaN(tare) || isNaN(value)){
				document.getElementById(loadId + 'inputNettoMeasure').value = "";
				return false;
			}
			result = value - tare;
			// LOGON-6715 alternative UoMs
			uomSelect = Ext.get(loadId + 'selectNetQtyUom');
			selectedOption = uomSelect.select('option[value=' + uomSelect.dom.value + ']')
			factor = parseFloat(selectedOption.elements[0].getAttribute('data-tonfactor'));
			result = result / factor;
			result = roundToDecimalAndTrimEndZeros(result,4);
			document.getElementById(loadId + 'inputNettoMeasure').value = result;
			
		}
		else if (source=='net'){
			value = value.replace(/,/,".");
			value = parseFloat(value);
			if (isNaN(tare) || isNaN(value)){
				document.getElementById(loadId + 'grossMeasure').value = "";
				return false;
			}
			// LOGON-6715 alternative UoMs
			uomSelect = Ext.get(loadId + 'selectNetQtyUom');
			selectedOption = uomSelect.select('option[value=' + uomSelect.dom.value + ']')
			factor = parseFloat(selectedOption.elements[0].getAttribute('data-tonfactor'));
			value = value * factor;
			result = tare + value;
			result = roundToDecimalAndTrimEndZeros(result,4);
			document.getElementById(loadId + 'grossMeasure').value = result;
		}
		else if (source == "tare"){
			value = document.getElementById(loadId + 'grossMeasure').value;
			value = value.replace(/,/,".");
			value = parseFloat(value);
			if (isNaN(tare) || isNaN(value)){
				document.getElementById(loadId + 'inputNettoMeasure').value = "";
				return false;
			}
			result = value - tare;
			// LOGON-6715 alternative UoMs
			uomSelect = Ext.get(loadId + 'selectNetQtyUom');
			selectedOption = uomSelect.select('option[value=' + uomSelect.dom.value + ']')
			factor = parseFloat(selectedOption.elements[0].getAttribute('data-tonfactor'));
			result = result / factor;
			result = roundToDecimalAndTrimEndZeros(result,4);
			document.getElementById(loadId + 'inputNettoMeasure').value = result;
		}
	}
