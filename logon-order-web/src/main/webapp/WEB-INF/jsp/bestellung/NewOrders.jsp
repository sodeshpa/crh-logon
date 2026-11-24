<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: NewOrders.jsp,v $ --%> 

<%@ include file="../includeTLDs.jsp" %>

<%@page import="com.crh.logon.order.web.ajax.OrderAjaxService"%>
<%@page import="com.crh.logon.constants.Constant" %>
<%@page import="com.crh.logon.persistence.entities.common.OrderEntity"%>
<%@page import="com.crh.logon.order.web.forms.bestellung.BestellungForm"%>
<%@page import="com.crh.logon.business.ordercreation.dto.DeliverySettingDTO"%>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>

<c:set var="form" scope="page" value="${order_bestellung_BestellungForm}" />
<c:set var="displayShipToTimezone" scope="request" value="${form.displayShipToTimezone}"/>
<c:set var="shipToTimeZoneFmtOffset" scope="request" value="${form.shipToTimeZone.formattedAsUTCOffset}"/>

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

<c:set var="editorList" value="${form.editorList}"/>
<c:set var="editor" value="${editorList[idx]}"/>
<c:set var="item" value="${editor.orderItems[itemIdx]}"/>
<c:set var="textareaCls" value="x-form-field comments" scope="request"/>
<c:set var="allowFutureContracts" value="${form.allowFutureContracts}"/>

<c:set var="DELIVERY_SETTING_BASED_ON_CONTRACT"><%=DeliverySettingDTO.DELIVERY_SETTING_BASED_ON_CONTRACT%></c:set>
<c:set var="DELIVERY_SETTING_BASED_ON_CONTRACT_COMBINED_RESTRICTION"><%=DeliverySettingDTO.DELIVERY_SETTING_BASED_ON_CONTRACT_COMBINED_RESTRICTION%></c:set>
<c:set var="DELIVERY_SETTING_BASED_ON_RESTRICTION"><%=DeliverySettingDTO.DELIVERY_SETTING_BASED_ON_RESTRICTION%></c:set>
<c:set var="DELIVERY_SETTING_NOT_DEFINED"><%=DeliverySettingDTO.DELIVERY_SETTING_NOT_DEFINED%></c:set>

<c:set var="RESOLUTION_FROM_PRICING_GROUP_TYPE_GENERIC_MATERIAL"><%=DeliverySettingDTO.RESOLUTION_FROM_PRICING_GROUP_TYPE_GENERIC_MATERIAL%></c:set>
<c:set var="RESOLUTION_EXPLICIT_IN_CONTRACT"><%=DeliverySettingDTO.RESOLUTION_EXPLICIT_IN_CONTRACT%></c:set>
<c:set var="RESOLUTION_FROM_DIVISION_TYPE_GENERIC_MATERIAL"><%=DeliverySettingDTO.RESOLUTION_FROM_DIVISION_TYPE_GENERIC_MATERIAL%></c:set>

<script type="text/javascript">

jsonData = <c:out value="${form.jsonData}" escapeXml="false"/>;

function futureContracts (isAllowFuture){
	var params = {};
	var form = Ext.getDom('bestellungForm4');
	Ext.query("input[name=action]", form)[0].value = 'reset';
	Ext.query("input[name=allowFutureContracts]", form)[0].value = isAllowFuture;
	form.submit();
}

function convertMapToList(map) {
	var i = 0;	
	var list=[];
	for (prop in map) { 
	    list[i]={};
	    list[i]['key']= prop;
	    list[i]['value']= map[prop];
	    i++;
	}
	return list;
}

function isPerformanceOrder(editor) {
	if (isRoadDirect(editor.delTypeCombo.getValue()) || isPickupOrder(editor.delTypeCombo.getValue()) || isShipOrBlockTrainPickupOrder(editor.delTypeCombo.getValue()) || isSimpleVehicleOrder(editor.delTypeCombo.getValue())) {
		if (!Ext.isEmpty(editor.deliveryFqyEveryTimeStr.getValue()) || !Ext.isEmpty(editor.deliveryFqyQtyPerHreStr.getValue())) {
			return true;
		}
	}
	return false;
}

function toggleDeliveryFqyCustorderMode(editorIdx) {
	
	var everyTimeDivId = 'deliveryFqyEveryTimeDiv' + editorIdx;
	var qtyPerHreDivId = 'deliveryFqyQtyPerHreDiv' + editorIdx;
	
	var everyTimeDiv = Ext.get(everyTimeDivId);
	var qtyPerHreDiv = Ext.get(qtyPerHreDivId);
	
	var isEveryTimeDivVisible = everyTimeDiv.isVisible();
	var isQtyPerHreDivVisible = qtyPerHreDiv.isVisible();
	
	if (!isEveryTimeDivVisible && isQtyPerHreDivVisible) {
		everyTimeDiv.setDisplayed('inline');
		qtyPerHreDiv.setDisplayed('none');
	}
	else if (isEveryTimeDivVisible && !isQtyPerHreDivVisible) {
		everyTimeDiv.setDisplayed('none');
		qtyPerHreDiv.setDisplayed('inline');
	}
	else {
		everyTimeDiv.setDisplayed('inline');
		qtyPerHreDiv.setDisplayed('none');
	}
}

function setExactQuantityFlags(editorIdx, itemIdx) {
	
	var editor = editorList[editorIdx];
	var item = editor.orderItems[itemIdx];
	
	var isBulk = false;
	var isBag = false;
	var selectedSapMaterial = item.sapMatCombo.getSelectedRecord();
	if (selectedSapMaterial) {
		isBulk = logonPackageHandlingHelper.isBulk(selectedSapMaterial.data.logonPackageTypeCd);
		isBag = logonPackageHandlingHelper.isBag(selectedSapMaterial.data.logonPackageTypeCd);
	}
	
	if ( (isBag 
		|| isSimpleVehicleOrder(editor.delTypeCombo.getValue())
		|| isShipOrBlockTrainPickupOrder(editor.delTypeCombo.getValue())
		)
		&& !isLandfillOnlyMode(editor)) {
		    editor.exactCustorderQtyCheckbox.setValue(false);
		    editor.exactCustorderQtyCheckbox.disable();
		    editor.exactQtyPerSdoCheckbox.setValue(true);
		    editor.exactQtyPerSdoCheckbox.disable();
	}
	else if (isBulk && !isLandfillOnlyMode(editor)) {
		var isRoadDirectOrPickup = isRoadDirect(editor.delTypeCombo.getValue()) || isPickupOrder(editor.delTypeCombo.getValue()) || isShipOrBlockTrainPickupOrder(editor.delTypeCombo.getValue());
		
		var isFuzzy = false;
		if (item.uomCombo && item.uomCombo.isFuzzy()) {
			isFuzzy = true;
		}
		
		if (!isFuzzy && isRoadDirectOrPickup) {
			editor.exactCustorderQtyCheckbox.enable();
			editor.exactQtyPerSdoCheckbox.enable();
		}
		else {
			editor.exactCustorderQtyCheckbox.setValue(false);
			editor.exactQtyPerSdoCheckbox.setValue(false);
			editor.exactCustorderQtyCheckbox.disable();
			editor.exactQtyPerSdoCheckbox.disable();
		}
	}
	else {	// for example Service Material Only or Landfill order
		editor.exactCustorderQtyCheckbox.setValue(false);
		editor.exactCustorderQtyCheckbox.disable();
		editor.exactQtyPerSdoCheckbox.setValue(false);
		editor.exactQtyPerSdoCheckbox.disable();
	}
}

function isSpecialEditorNeeded(delTypeCd) {
	if (isTruckOrder(delTypeCd)
			|| isPickupOrder(delTypeCd)
			|| isReturnDeliveredOrder(delTypeCd)
			|| isShipOrBlockTrainPickupOrder(delTypeCd)
			|| isSimpleVehicleOrder(delTypeCd)) {
		return true;
	}
	else {
		return false;
	}
}

function isMaterialNoLanfillOrAgg(selectedSapMaterialNo) {
	return selectedSapMaterialNo == '<%= Constant.SAP_MATERIAL_NO_LANDFILL_PLANNING %>' || 
			selectedSapMaterialNo == '<%= Constant.SAP_MATERIAL_NO_AGG_PLANNING %>'
}

function setSpecialEditorActive(editorIdx, itemIdx, isActive) {	
	
	var editor = editorList[editorIdx];
	var item = editor.orderItems[itemIdx];
	
	var selectedSapMaterial = item.sapMatCombo.getSelectedRecord();
	var selectedSapMaterialNo;
	if (!selectedSapMaterial) {
		isActive = false;
	}
	else {
		selectedSapMaterialNo = item.sapMatCombo.getSelectedRecord().data.sapMaterialNo;
	}
	
	editor.deliveryFqyEveryTimeStr.disable();
	editor.deliveryFqyEveryTimeUomCombo.disable();
	editor.deliveryFqyQtyPerHreStr.disable();
	editor.deliveryFqyQtyPerHreUomCombo.disable();
	editor.deliveryTimeToleranceStr.disable();
	editor.deliveryTimeToleranceUomCombo.disable();
	
	if (isActive) {
		// only for bulk
		if (logonPackageHandlingHelper.isBulk(selectedSapMaterial.data.logonPackageTypeCd)) {
			if (editor.delTypeCombo && editor.delTypeCombo.getValue() && editor.delTypeCombo.getValue() != -1) {
				// not allowed for bimodal road-part
				if (isRoadDirect(editor.delTypeCombo.getValue()) || isPickupOrder(editor.delTypeCombo.getValue()) || 
						isShipOrBlockTrainPickupOrder(editor.delTypeCombo.getValue()) || isSimpleVehicleOrder(editor.delTypeCombo.getValue())) {	
					editor.deliveryFqyEveryTimeStr.enable();
					editor.deliveryFqyEveryTimeUomCombo.enable();
					editor.deliveryFqyQtyPerHreStr.enable();
					editor.deliveryFqyQtyPerHreUomCombo.enable();
					editor.deliveryTimeToleranceStr.enable();
					editor.deliveryTimeToleranceUomCombo.enable();
				}
			}
		}
		var sapMaterialId = nvl(item.sapMatCombo.getValue(), null);
		
		if (item.uomCombo && item.uomCombo.isFuzzy()) {
			if (editor.truckTypeCombo) {
				editor.truckTypeCombo.updateMaxCapacity();
			}
			editor.quantityPerSdoStr.disable();
		}
		else {
			editor.quantityPerSdoStr.enable();
		}
		
		editor.numberOfDeliveriesLabel.setVisible(true);
		
		setExactQuantityFlags(editorIdx, itemIdx);
		
		addSpecialEditorListeners(editorIdx)
	}
	else {
		editor.quantityPerSdoStr.disable();
		editor.numberOfDeliveriesLabel.setVisible(false);
		
		setExactQuantityFlags(editorIdx, itemIdx);
		
		removeSpecialEditorListeners(editorIdx);
	}
}

function addSpecialEditorListeners(editorIdx) {
	
	var editor = editorList[editorIdx];
	editor.isSpecialEditorListenersActive = true;
	
	var config = {
			buffer: 300
	};
	
	for (var i=0; i < editor.orderItems.length; i++) {
		var actItem = editor.orderItems[i];
		if (actItem) {
			actItem.quantityStr.addListener('keypress', updateCustorderSplitMetaData, actItem.quantityStr, config);
			actItem.uomCombo.addListener('select', updateCustorderSplitMetaData, actItem.uomCombo, config);
			actItem.sapMatCombo.addListener('select', updateCustorderSplitMetaData, actItem.sapMatCombo, config);
		}
	}
	
	editor.delFromDat.addListener('select', updateCustorderSplitMetaData, editor.delFromDat, config);
	editor.delFromDat.addListener('change', updateCustorderSplitMetaData, editor.delFromDat, config);
	editor.delFromTime.addListener('select', updateCustorderSplitMetaData, editor.delFromTime, config);
	editor.delFromTime.addListener('change', updateCustorderSplitMetaData, editor.delFromTime, config);
	editor.delToDat.addListener('select', updateCustorderSplitMetaData, editor.delToDat, config);
	editor.delToDat.addListener('change', updateCustorderSplitMetaData, editor.delToDat, config);
	editor.delToTime.addListener('select', updateCustorderSplitMetaData, editor.delToTime, config);
	editor.delToTime.addListener('change', updateCustorderSplitMetaData, editor.delToTime, config);
	
	editor.truckTypeCombo.addListener('select', updateCustorderSplitMetaData, editor.truckTypeCombo, config);
	editor.truckTypeCombo.addListener('change', updateCustorderSplitMetaData, editor.truckTypeCombo, config);
	editor.quantityPerSdoStr.addListener('keypress', updateCustorderSplitMetaData, editor.quantityPerSdoStr, config);
	editor.quantityPerSdoStr.addListener('change', updateCustorderSplitMetaData, editor.quantityPerSdoStr, config);
	editor.deliveryFqyEveryTimeStr.addListener('keypress', updateCustorderSplitMetaData, editor.deliveryFqyEveryTimeStr, config);
	editor.deliveryFqyEveryTimeUomCombo.addListener('select', updateCustorderSplitMetaData, editor.deliveryFqyEveryTimeUomCombo, config);
	editor.deliveryFqyQtyPerHreStr.addListener('keypress', updateCustorderSplitMetaData, editor.deliveryFqyQtyPerHreStr, config);
	editor.deliveryFqyQtyPerHreUomCombo.addListener('select', updateCustorderSplitMetaData, editor.deliveryFqyQtyPerHreUomCombo, config);
	editor.deliveryTimeToleranceStr.addListener('keypress', updateCustorderSplitMetaData, editor.deliveryTimeToleranceStr, config);
	editor.deliveryTimeToleranceUomCombo.addListener('select', updateCustorderSplitMetaData, editor.deliveryTimeToleranceUomCombo, config);
	editor.exactCustorderQtyCheckbox.addListener('check', updateCustorderSplitMetaData, editor.exactCustorderQtyCheckbox, config);
	editor.exactQtyPerSdoCheckbox.addListener('check', updateCustorderSplitMetaData, editor.exactQtyPerSdoCheckbox, config);
}

function removeSpecialEditorListeners(editorIdx) {
	
	var editor = editorList[editorIdx];
	editor.isSpecialEditorListenersActive = false;

	for (var i=0; i < editor.orderItems.length; i++) {
		var actItem = editor.orderItems[i];
		if (actItem) {
			actItem.quantityStr.removeListener('keypress', updateCustorderSplitMetaData, actItem.quantityStr);
			actItem.uomCombo.removeListener('select', updateCustorderSplitMetaData, actItem.uomCombo);
			actItem.sapMatCombo.removeListener('select', updateCustorderSplitMetaData, actItem.sapMatCombo);
		}
	}
	
	editor.delFromDat.removeListener('select', updateCustorderSplitMetaData, editor.delFromDat);
	editor.delFromDat.removeListener('change', updateCustorderSplitMetaData, editor.delFromDat);
	editor.delFromTime.removeListener('select', updateCustorderSplitMetaData, editor.delFromTime);
	editor.delFromTime.removeListener('change', updateCustorderSplitMetaData, editor.delFromTime);
	editor.delToDat.removeListener('select', updateCustorderSplitMetaData, editor.delToDat);
	editor.delToDat.removeListener('change', updateCustorderSplitMetaData, editor.delToDat);
	editor.delToTime.removeListener('select', updateCustorderSplitMetaData, editor.delToTime);
	editor.delToTime.removeListener('change', updateCustorderSplitMetaData, editor.delToTime);
	
	editor.truckTypeCombo.removeListener('select', updateCustorderSplitMetaData, editor.truckTypeCombo);
	editor.truckTypeCombo.removeListener('change', updateCustorderSplitMetaData, editor.truckTypeCombo);
	editor.quantityPerSdoStr.removeListener('keypress', updateCustorderSplitMetaData, editor.quantityPerSdoStr);
	editor.quantityPerSdoStr.removeListener('change', updateCustorderSplitMetaData, editor.quantityPerSdoStr);
	editor.deliveryFqyEveryTimeStr.removeListener('keypress', updateCustorderSplitMetaData, editor.deliveryFqyEveryTimeStr);
	editor.deliveryFqyEveryTimeUomCombo.removeListener('select', updateCustorderSplitMetaData, editor.deliveryFqyEveryTimeUomCombo);
	editor.deliveryFqyQtyPerHreStr.removeListener('keypress', updateCustorderSplitMetaData, editor.deliveryFqyQtyPerHreStr);
	editor.deliveryFqyQtyPerHreUomCombo.removeListener('select', updateCustorderSplitMetaData, editor.deliveryFqyQtyPerHreUomCombo);
	editor.deliveryTimeToleranceStr.removeListener('keypress', updateCustorderSplitMetaData, editor.deliveryTimeToleranceStr);
	editor.deliveryTimeToleranceUomCombo.removeListener('select', updateCustorderSplitMetaData, editor.deliveryTimeToleranceUomCombo);
	editor.exactCustorderQtyCheckbox.removeListener('check', updateCustorderSplitMetaData, editor.exactCustorderQtyCheckbox);
	editor.exactQtyPerSdoCheckbox.removeListener('check', updateCustorderSplitMetaData, editor.exactQtyPerSdoCheckbox);
}

function updateSpecialEditorListeners(editorIdx) {
	var editor = editorList[editorIdx];
	if (editor) {
		if (editor.isSpecialEditorListenersActive == true) {
			addSpecialEditorListeners(editorIdx);
		}
		else {
			removeSpecialEditorListeners(editorIdx);
		}
	}
}

function setPerformanceOrderActive(editor, item, editorIdx, itemIdx, isActive) {	
	if (isActive) {
		if (item.quantityStr.getValue() && !item.quantityStr.disabled && item.uomCombo.getValue() && item.uomCombo.getValue() != -1) {
			onOrderedQuantityEditModeSwitch(editorIdx, itemIdx);
		}
		else if (editor.delToDat.getValue() && !editor.delToDat.disabled && editor.delToTime.getValue() && !editor.delToTime.disabled) {
			onDeliveryToDatEditModeSwitch(editorIdx, itemIdx);
		}
		else {
			onOrderedQuantityEditModeSwitch(editorIdx, itemIdx);
		}
		
		selectActiveDeliveryFqyCustorderMode(editorIdx);
	}
	else {
		editor.performanceOrderMode = undefined;
		Ext.getDom('editorList['+editorIdx+'].performanceOrderCalculationMode').value = '';
		
		item.quantityStr.enable();
		item.quantityStr.resumeEvents();
		
		item.uomCombo.enable();
		item.uomCombo.resumeEvents();
		
		editor.delToDat.enable();
		editor.delToDat.resumeEvents();
		
		editor.delToTime.enable();
		editor.delToTime.resumeEvents();
		
		updateCustorderSplitMetaData();
		
		Ext.get('orderedQuantityEditModeSwitch' + editorIdx).setVisible(false);
		Ext.get('deliveryToDatEditModeSwitch' + editorIdx).setVisible(false);
	}
}


function onOrderedQuantityEditModeSwitch(editorIdx, itemIdx) {
	var editor = editorList[editorIdx];
	var item = editor.orderItems[itemIdx];
	
	editor.performanceOrderMode = '<%= Constant.PERFORMANCE_ORDER_CALCULATE_DELIVERYTO %>';
	Ext.getDom('editorList['+editorIdx+'].performanceOrderCalculationMode').value = '<%= Constant.PERFORMANCE_ORDER_CALCULATE_DELIVERYTO %>';
	
	item.quantityStr.resumeEvents();
	item.quantityStr.enable();
	
	item.uomCombo.resumeEvents();
	item.uomCombo.enable();
	
	editor.delToDat.disable();
	editor.delToDat.suspendEvents();
	
	editor.delToTime.disable();
	editor.delToTime.suspendEvents();
	
	updateCustorderSplitMetaData();
	
	Ext.get('orderedQuantityEditModeSwitch' + editorIdx).setVisible(false);
	Ext.get('deliveryToDatEditModeSwitch' + editorIdx).setVisible(true);
}


function onDeliveryToDatEditModeSwitch(editorIdx, itemIdx) {
	var editor = editorList[editorIdx];
	var item = editor.orderItems[itemIdx];
	
	editor.performanceOrderMode = '<%= Constant.PERFORMANCE_ORDER_CALCULATE_QUANTITY %>';
	Ext.getDom('editorList['+editorIdx+'].performanceOrderCalculationMode').value = '<%= Constant.PERFORMANCE_ORDER_CALCULATE_QUANTITY %>';
	
	item.quantityStr.disable();
	item.quantityStr.suspendEvents();
	
	item.uomCombo.disable();
	item.uomCombo.suspendEvents();
	
	editor.delToDat.enable();
	editor.delToDat.resumeEvents();
	
	editor.delToTime.enable();
	editor.delToTime.resumeEvents();
	
	updateCustorderSplitMetaData();
	
	Ext.get('orderedQuantityEditModeSwitch' + editorIdx).setVisible(true);
	Ext.get('deliveryToDatEditModeSwitch' + editorIdx).setVisible(false);
}

function selectActiveDeliveryFqyCustorderMode(editorIdx) {
	
	var editor = editorList[editorIdx];
	
	var everyTimeDivId = 'deliveryFqyEveryTimeDiv' + editorIdx;
	var qtyPerHreDivId = 'deliveryFqyQtyPerHreDiv' + editorIdx;
	
	var everyTimeDiv = Ext.get(everyTimeDivId);
	var qtyPerHreDiv = Ext.get(qtyPerHreDivId);
	
	var isEveryTimeDivVisible = everyTimeDiv.isVisible();
	var isQtyPerHreDivVisible = qtyPerHreDiv.isVisible();
	
	if (editor.deliveryFqyEveryTimeStr.getValue() && !isEveryTimeDivVisible) {
		everyTimeDiv.setDisplayed('inline');
		qtyPerHreDiv.setDisplayed('none');
	}
	else if (editor.deliveryFqyQtyPerHreStr.getValue() && !isQtyPerHreDivVisible) {
		everyTimeDiv.setDisplayed('none');
		qtyPerHreDiv.setDisplayed('inline');
	}
}

function isLandfillOnlyMode(editor) {
	var FIRST = 0;
	if (editor) {
		var item = editor.orderItems[FIRST];
		var selectedSapMaterial = item.sapMatCombo.getSelectedRecord();
			
		if (selectedSapMaterial) {
			return selectedSapMaterial.data.isLandfillMaterial;
		}
		else {
			return false;
		}
	}
	else{
		return true;
	}
}


function isServiceOnlyMode(editorIdx) {
	var editor = editorList[editorIdx];
	if (editor && editor.serviceOnlyOrderCheckbox) {
		return editor.serviceOnlyOrderCheckbox.getValue();
	}
	else {
		return false;
	}
}

function updateServiceOnlyMode(editorIdx, itemIdx) {
	
	var isActive = isServiceOnlyMode(editorIdx);
	
	var editor = editorList[editorIdx];
	var item = editor.orderItems[itemIdx];
	
	setExactQuantityFlags(editorIdx, itemIdx);
	
	if (isActive) {
		if (editor) {
			
			editor.delTypeCombo.disable();
			editor.truckTypeCombo.disable();
			editor.siloCombo.disable();
			editor.quantityPerSdoStr.disable();
			editor.quantityPerSdoUomCd.disable();
			editor.deliveryFqyEveryTimeUomCombo.disable();
			editor.deliveryFqyQtyPerHreUomCombo.disable();
			editor.deliveryTimeToleranceUomCombo.disable();
			editor.poolCombo.disable();
			editor.commercialCarrierCombo.disable();
			editor.executingCarrierCombo.disable();
		}
		
		if (item) {
			if (item.mixDesignCombo || isLandfillOnlyMode(editor)) {
				item.mixDesignCombo.disable();
			}
			if (item.lpCombo) {
				item.lpCombo.disable();
			}	
			
			Ext.getDom('editorList['+ editorIdx +'].orderItems['+ itemIdx +'].itemNoteDispatching').disabled = 'true';
			Ext.getDom('editorList['+ editorIdx +'].orderItems['+ itemIdx +'].itemNoteTruckDriver').disabled = 'true';
			Ext.getDom('editorList['+ editorIdx +'].orderItems['+ itemIdx +'].itemNoteRailway').disabled = 'true';	
		}
		
	}
	else {
		if (editor) {
			editor.delTypeCombo.enable();
			editor.truckTypeCombo.enable();
			
			if (item && item.uomCombo && item.uomCombo.isFuzzy()) {
				editor.quantityPerSdoStr.disable();
			}
			else {
				editor.quantityPerSdoStr.enable();
			}
			
			editor.quantityPerSdoUomCd.enable();
			editor.deliveryFqyEveryTimeUomCombo.enable();
			editor.deliveryFqyQtyPerHreUomCombo.enable();
			editor.deliveryTimeToleranceUomCombo.enable();
			editor.supplyPlantCombo.enable();
			editor.spCombo.enable();

			var selectedSapMaterial = item.sapMatCombo.getSelectedRecord();
			var selectedSapMaterialNo;
			if (selectedSapMaterial) {
				selectedSapMaterialNo = item.sapMatCombo.getSelectedRecord().data.sapMaterialNo;
				if (isMaterialNoLanfillOrAgg(selectedSapMaterialNo)) {
					editor.deliveryFqyEveryTimeStr.disable();
					editor.deliveryFqyEveryTimeUomCombo.disable();
					editor.deliveryFqyQtyPerHreStr.disable();
					editor.deliveryFqyQtyPerHreUomCombo.disable();
					editor.deliveryTimeToleranceStr.disable();
					editor.deliveryTimeToleranceUomCombo.disable();
				}
			}
			
			var deliveryTypeCd = nvl(editor.delTypeCombo.getValue(), null);
			if (isShipOrBlockTrainPickupOrder(deliveryTypeCd)) {
				editor.poolCombo.disable();
				editor.commercialCarrierCombo.disable();
				editor.executingCarrierCombo.disable();
				editor.siloCombo.enable();
			}
			else
			{

				editor.poolCombo.enable();
				editor.commercialCarrierCombo.enable();
				editor.executingCarrierCombo.enable();

				if (isReturnPickupOrder(deliveryTypeCd) || isLandfillOnlyMode(editor)) {
					editor.siloCombo.disable();
				}
				else {
					editor.siloCombo.enable();
				}

			}
		}
		
		if (item) {
			if (item.mixDesignCombo && !isLandfillOnlyMode(editor)) {
				item.mixDesignCombo.enable();
			}
			if (item.lpCombo) {
				item.lpCombo.enable();
			}
			Ext.getDom('editorList['+ editorIdx +'].orderItems['+ itemIdx +'].itemNoteDispatching').removeAttribute('disabled');
			Ext.getDom('editorList['+ editorIdx +'].orderItems['+ itemIdx +'].itemNoteTruckDriver').removeAttribute('disabled');
			Ext.getDom('editorList['+ editorIdx +'].orderItems['+ itemIdx +'].itemNoteRailway').removeAttribute('disabled');
		}
	}
}

function isShiptoSiloToEnable(editorIdx) {
	var editor = editorList[editorIdx];
	
	if (isServiceOnlyMode(editorIdx) || isLandfillOnlyMode(editor)) {
		return false;
	}
	else if (editor && editor.delTypeCombo && isReturnPickupOrder(nvl(editor.delTypeCombo.getValue(), null))) {
		return false;
	}
	else {
		return true;
	}
}

splitSpinner = {
		show: function(idx) {
			if (Ext.get('splitSpinner' + idx)) {
				Ext.get('splitSpinner' + idx).setDisplayed('inline');
			}
		},
		hide: function(idx) {
			if (Ext.get('splitSpinner' + idx)) {
				Ext.get('splitSpinner' + idx).setDisplayed('none');
			}
		}
	};
	

function updateCustorderSplitMetaData() {
	
	var editor = editorList[<%=idx%>];
	if (!editor || !editor.orderItems) {
		return;
	}
	
	editor.numberOfDeliveriesLabel.setText('');
	
	var numOfItems = editor.orderItemCnt;
	if (numOfItems < 1) {
		return;
	}
	
	var sapMatIds = [];
	var orderedQuantities = [];
	var orderedUomCds = [];
	
	for (var i=0; i < editor.orderItems.length; i++) {
		
		var actItem = editor.orderItems[i];
		if (!actItem) {
			continue;
		}
		
		if (!actItem.sapMatCombo.getSelectedRecord()) {
			continue;
		}
		
		var sapMaterialId = nvl(actItem.sapMatCombo.getValue(), null);
		if (sapMaterialId == null) {
			return;
		}
		sapMatIds.push(sapMaterialId);
		
		var orderedQty = nvl(actItem.quantityStr.getValue(), null);
		if (!actItem.quantityStr.validate()) {
			return;
		}
		orderedQuantities.push(orderedQty);
		
		var orderedUomCd = nvl(actItem.uomCombo.getValue(), null);
		orderedUomCds.push(orderedUomCd);
	}
	
	adjustDeliveriesBtn.disable();
	
	if (!editor.quantityPerSdoStr.validate() || !editor.deliveryFqyEveryTimeStr.validate()
		|| !editor.deliveryFqyQtyPerHreStr.validate() || !editor.deliveryTimeToleranceStr.validate()) {
			return;
	}
	
	var maxPrioSelectedTruck = editor.truckTypeCombo.getMaxPrioTruckRecord();
	if (!maxPrioSelectedTruck) {
		return;
	}
	
	var input = {
   		'sapMatIds': sapMatIds,
   		'maxPrioTruckTypeCd': nvl(maxPrioSelectedTruck.truckTypeCd, null),
   		'deliveryTypeCd': nvl(editor.delTypeCombo.getValue(), null),
   		'orderedQuantities': orderedQuantities,
   		'orderedUomCds': orderedUomCds,
   		'exactCustorderQtyFlag': nvl(editor.exactCustorderQtyCheckbox.getValue(), false),
   		'quantityPerSdo': nvl(editor.quantityPerSdoStr.getValue(), null),
   		'quantityPerSdoUomCd': nvl(editor.quantityPerSdoUomCd.getValue(), null),
   		'exactQtyPerSdoFlag': nvl(editor.exactQtyPerSdoCheckbox.getValue(), false),
   		'deliveryFqyEveryTime': nvl(editor.deliveryFqyEveryTimeStr.getValue(), null),
   		'deliveryFqyEveryTimeUomCd': nvl(editor.deliveryFqyEveryTimeUomCombo.getValue(), null),
   		'deliveryFqyQtyPerHre': nvl(editor.deliveryFqyQtyPerHreStr.getValue(), null),
   		'deliveryFqyQtyPerHreUomCd': nvl(editor.deliveryFqyQtyPerHreUomCombo.getValue(), null),
   		'delFromDat': nvl(editor.getDelFromDateTime(), null), 
   		'delToDat': nvl(editor.getDelToDateTime(), null),
   		'deliveryTimeTolerance': nvl(editor.deliveryTimeToleranceStr.getValue(), null),
   		'deliveryTimeToleranceUomCd': nvl(editor.deliveryTimeToleranceUomCombo.getValue(), null),
   		'numDeliveriesStr': null
	};
	
	if (isPerformanceOrder(editor)) {
		if (editor.performanceOrderMode && editor.performanceOrderMode == '<%=Constant.PERFORMANCE_ORDER_CALCULATE_QUANTITY%>') {
			input.orderedQuantities[0] = undefined;
			input.orderedUomCds[0] = undefined;
		}
		else {
			input.delToDat = undefined;
		}
	}
	
	splitSpinner.show(<%=idx%>);

   	OrderAjaxService.getCustorderSplitMetaData(input, 
    	function(res) {
   			if (res) {
    			if (isPerformanceOrder(editor)) {
    				if (editor.performanceOrderMode && editor.performanceOrderMode == '<%= Constant.PERFORMANCE_ORDER_CALCULATE_QUANTITY %>') {
		    			// update ordered quantity and UoM
		    			editor.orderItems[0].quantityStr.setValue(res.orderedQuantities[0]);
		    			editor.orderItems[0].uomCombo.setValue(res.orderedUomCds[0]);
    				}
    				else {
		    			// update delivery to date and time
		    			var deliveryToDateTime = res.delToDat;
		    			editor.delToDat.setValue(deliveryToDateTime.format('<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>'));
		    			editor.delToTime.setValue(deliveryToDateTime.format('<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>'));
    				}
    			}
    			
    			editor.quantityPerSdoStr.setValue(res.quantityPerSdo);
    			editor.quantityPerSdoUomCd.setValue(res.quantityPerSdoUomCd);
    			editor.uomPerDeliveryLabel.setText(res.quantityPerSdoStr);
    			
    			editor.numberOfDeliveriesLabel.setText(res.numDeliveriesStr);
    			if (res.numDeliveries > res.maxNumOfOrders) {
    				editor.numberOfDeliveriesLabel.getEl().setStyle({'color': '#FF0000'});
    			}
    			else {
    				editor.numberOfDeliveriesLabel.getEl().setStyle({'color': '#000000'});
    			}

				
    			var deliveryTypeCd = res.deliveryTypeCd;
    			if (isShipOrBlockTrainPickupOrder(deliveryTypeCd)) {
    				editor.poolCombo.disable();
    				editor.commercialCarrierCombo.disable();
    				editor.executingCarrierCombo.disable();
   				}
    			else{
    				editor.poolCombo.enable();
    				editor.commercialCarrierCombo.enable();
    				editor.executingCarrierCombo.enable();
    			}
   			}
   			else {
   				editor.numberOfDeliveriesLabel.setText('');
   				editor.numberOfDeliveriesLabel.getEl().setStyle({'color': '#000000'});
   			}
   			
   			splitSpinner.hide(<%=idx%>);
    		saveOrderBtn.update();
   			adjustDeliveriesBtn.update();
    	});
}

Ext.onReady(function() {

	var editor = {};
	var item = {};
	var productList = <c:out value="${item.jsonProductList}" escapeXml="false"/>;
	var serviceOnlyProductList = <c:out value="${item.jsonServiceOnlyProductList}" escapeXml="false"/>;
	// TODO (balakirev) verify; maybe AND with original?
	var isEditorDisabled = selectedEditorIdx == <%=idx%>; //<c:out value="${isEditorDisabled}"/>;

	editor.orderItems = Array();
	editor.orderItems[<%=itemIdx%>] = item;
	editor.orderItemCnt = 1;

	// --- DELIVERY FROM ---
	editor.delFromDat = new Ext.form.DateField({
		id: 'delFromDat[<%=idx%>]',
		applyTo: 'deliveryFromDatStr[<%=idx%>]',
    	format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
    	altFormats: '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>',
        allowBlank: false,
	    disabled: isEditorDisabled,
    	width: 78,
		changedByPicker: false,
		listeners: {
    		select: function(obj, date) {
    			obj.changedByPicker = true;
	    		if (date > editor.delToDat.getValue()) {
	    			editor.delToDat.setValue(date);
	    		}
    			updateByDeliveryDate();	    			
			},
    		change: function(obj, newVal, oldVal) {
	    		if (!obj.changedByPicker) {
	    			if (Ext.isEmpty(newVal)) {
	    				obj.setValue(oldVal);
	    			}
	    			else {
		    			if (newVal > editor.delToDat.getValue()) {
			    			editor.delToDat.setValue(newVal);
		    			}
		    			updateByDeliveryDate();	    			
	    			}
	    		}
	    		else {
	    			obj.changedByPicker = false;		    		
	    		}		    		
    		}
    	}
	});

	editor.delFromTime = new Ext.form.TimeField({
		id: 'delFromTime[<%=idx%>]',
    	applyTo: 'deliveryFromTime[<%=idx%>]',
		minValue: '<c:out value="${form.dummyFields.delTimeMin}"/>',
		maxValue: '<c:out value="${form.dummyFields.delTimeMax}"/>',
    	increment: 15,
    	format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
	    disabled: isEditorDisabled,
    	width: 78,
		listeners: {
    		select: function(obj, time) {
				obj.changedByPicker = true;
				updateByDeliveryDate();	    			
			},
    		change: function(obj, newVal, oldVal) {
	    		if (!obj.changedByPicker) {
					if (Ext.isEmpty(newVal)) {
		    			obj.setValue(oldVal);
					}
					else {
	        			updateByDeliveryDate();	    			
					}
	    		}
	    		else {
	    			obj.changedByPicker = false;		    		
	    		}		    		
	    	}
		}
	});

	// --- DELIVERY TO ---
	editor.delToDat = new Ext.form.DateField({
		id: 'delToDat[<%=idx%>]',
    	applyTo: 'deliveryToDatStr[<%=idx%>]',
    	format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
    	altFormats: '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>',
        allowBlank: false,
	    disabled: isEditorDisabled,
    	width: 78,
		changedByPicker: false,
		listeners: {
    		select: function(obj, date) {
    			obj.changedByPicker = true;
	    		// --- Auto adjust deliv. to. ---
	    		if (date < editor.delFromDat.getValue()) {
	    			editor.delFromDat.setValue(date);
	    		}
    			updateByDeliveryDate();	    			
			},
    		change: function(obj, newVal, oldVal) {
	    		if (!obj.changedByPicker) {
	    			if (Ext.isEmpty(newVal)) {
	    				obj.setValue(oldVal);
	    			}
	    			else {
	    	    		if (newVal < editor.delFromDat.getValue()) {
	    	    			editor.delFromDat.setValue(newVal);
	    	    		}
	        			updateByDeliveryDate();	    			
	    			}
	    		}
	    		else {
	    			obj.changedByPicker = false;		    		
	    		}		    		
    		}
    	}
	});
	
	editor.delToTime = new Ext.form.TimeField({
		id: 'delToTime[<%=idx%>]',
    	applyTo: 'deliveryToTime[<%=idx%>]',
		minValue: '<c:out value="${form.dummyFields.delTimeMin}"/>',
		maxValue: '<c:out value="${form.dummyFields.delTimeMax}"/>',
    	increment: 15,
    	format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
	    disabled: isEditorDisabled,
    	width: 78,
		//validator: deliveryFromToValidator	
		changedByPicker: false,
    	listeners: {
    		select: function(obj, time) {
				obj.changedByPicker = true;
				updateByDeliveryDate();	    			
			},
    		change: function(obj, newVal, oldVal) {
	    		if (!obj.changedByPicker) {
					if (Ext.isEmpty(newVal)) {
		    			obj.setValue(oldVal);
					}
					else {
	        			updateByDeliveryDate();	    			
					}
	    		}
	    		else {
	    			obj.changedByPicker = false;		    		
	    		}
	    	}
		}
	});

	editor.getDelFromDateTime = function() { 
		if (isRailDirectOrder(editor.delTypeCombo.getValue())) {
			return editor.delFromDat.getValue();
		}
		return Date.parseDate(editorList[0].delFromDat.value + ' ' + editorList[0].delFromTime.value, 'd.m.y H:i');
	}
    
	editor.getDelToDateTime = function() { 
		if (isRailDirectOrder(editor.delTypeCombo.getValue())) {
			return editor.delToDat.getValue();
		}
		return Date.parseDate(editorList[0].delToDat.value + ' ' + editorList[0].delToTime.value, 'd.m.y H:i');
	}
    
	var deliveryFromToValidator = function() {
		if (Ext.getCmp('delFromDat[<%=idx%>]').getValue() <= Ext.getCmp('delToDat[<%=idx%>]').getValue()) {
			if (Ext.getCmp('delFromDat[<%=idx%>]').getValue() < Ext.getCmp('delToDat[<%=idx%>]').getValue()) {
				return true;
			}
			else if (Ext.getCmp('delFromTime[<%=idx%>]').getValue() <= Ext.getCmp('delToTime[<%=idx%>]').getValue()) {
				return true;
			}
		}
		return false;
	}
		
	// -----------------------------------------------------------------------------------
	// SAP Material: only for the first entry
	// -----------------------------------------------------------------------------------

	
		 // Custom rendering Template
	    var materialComboTemplate = new Ext.XTemplate(	    
	    	'<table class="classicblue" style="width:100%"><thead><tr>',
	    	'<th><fmt:message key="bestellung.materialcontractcombo.material"/></th>',
	    	'<th><fmt:message key="bestellung.materialcontractcombo.contractValidityFromDate"/></th>',
	    	'<th><fmt:message key="bestellung.materialcontractcombo.contractValidityToDate"/></th>',
	    	'<th><fmt:message key="bestellung.materialcontractcombo.contract"/></th>',
	    	'<th><fmt:message key="bestellung.materialcontractcombo.contractTypeTxt"/></th>',
	    	'<th><fmt:message key="bestellung.materialcontractcombo.salesarea"/></th>',
	    	'<th><fmt:message key="bestellung.materialcontractcombo.productionPlant"/></th>',
	    	'<th><fmt:message key="bestellung.materialcontractcombo.packagetype"/></th>',
	    	'<th><fmt:message key="bestellung.materialcontractcombo.quantity"/></th>',
	    	'<th><fmt:message key="bestellung.materialcontractcombo.uom"/></th>',
	    	'<th><fmt:message key="bestellung.materialcontractcombo.deliveryType"/></th>',
	    	'<th><fmt:message key="bestellung.materialcontractcombo.supplyPlant"/></th>',
	    	'<th><fmt:message key="bestellung.materialcontractcombo.shippingPoint"/></th>',
	    	'<th><fmt:message key="bestellung.materialcontractcombo.pool"/></th>',
	    	'<th><fmt:message key="bestellung.materialcontractcombo.carrier.commercial"/></th>',
	    	'<th><fmt:message key="bestellung.materialcontractcombo.carrier.executing"/></th>',
	    	'<th><fmt:message key="bestellung.materialcontractcombo.billto"/></th>',
	    	'<th><fmt:message key="bestellung.materialcontractcombo.payer"/></th>',
	    	'<th><fmt:message key="bestellung.materialcontractcombo.contractMaterial"/></th>',
	    	'</tr></thead><tbody>',
	        '<tpl for=".">',
		        	'<tr class="listitem zebra{[xindex % 2]}">',
		        		'<td><tpl if="!this.isNotAvailable(sapMaterialId)">{txt} ({sapMaterialNo})</tpl></td>',
		        		'<td><tpl if="!this.isNotAvailable(contractValidityFromDate)">{contractValidityFromDate}</tpl></td>',
		        		'<td><tpl if="!this.isNotAvailable(contractValidityToDate)">{contractValidityToDate}</tpl></td>',
		        		'<td><tpl if="!this.isNotAvailable(sapContractId)">{sapContractNo} ({contractItemNo}) {sapContractIncoterms}</tpl></td>',
	 					'<td><tpl if="!this.isNotAvailable(contractTypeTxt)">{contractTypeTxt}</tpl></td>',
		        		'<td><tpl if="!this.isNotAvailable(sareaId)">{sareaDisplayName}</tpl></td>',
		        		'<td><tpl if="!this.isNotAvailable(productionPlantCd)">{productionPlantName} ({productionPlantCd})</tpl></td>',
		        		'<td><tpl if="!this.isNotAvailable(materialPackageTxt)">{materialPackageTxt}</tpl></td>',
		        		'<td><tpl if="!this.isNotAvailable(quantity)">{quantity}</tpl></td>',
		        		'<td><tpl if="!this.isNotAvailable(uomCd)">{uomCd}</tpl></td>',
		        		'<td><tpl if="!this.isNotAvailable(deliveryTypeTxt)">{deliveryTypeTxt}</tpl></td>',
		        		'<td><tpl if="!this.isNotAvailable(supplyPlantCd)">{supplyPlantName}({supplyPlantCd})</tpl></td>',
		        		'<td><tpl if="!this.isNotAvailable(shippingpointId)">{shippingpointName}</tpl></td>',
		        		'<td><tpl if="!this.isNotAvailable(poolId)">{poolName}</tpl></td>',
		        		'<td><tpl if="!this.isNotAvailable(commercialCarrierId)">{commercialCarrierName}</tpl></td>',
		        		'<td><tpl if="!this.isNotAvailable(executingCarrierId)">{executingCarrierName}</tpl></td>',
		        		'<td><tpl if="!this.isNotAvailable(billtoSapBpId)">{billtoName}</tpl></td>',
		        		'<td><tpl if="!this.isNotAvailable(payerSapBpId)">{payerName}</tpl></td>',
		        		'<td>{[this.getResolutionOfMaterialText(values.resolutionType)]}<tpl if="!this.isNotAvailable(contractSapMaterialTxt)"> ({contractSapMaterialTxt})</tpl> </td>',
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
		            //FIXME this is not used, shoulf be used to show the source
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
	    
	    Ext.ux.materialCombo = Ext.extend(Ext.ux.MaterialCombo, {
			id: 'sapMatCombo[<%=idx%>]',
			name: '<%=itemStr%>.sapMatId',
		    transform:'<%=itemStr%>.sapMatId',
		    width: 120,
		    listWidth: 1300,
			emptyText: '<fmt:message key="bestellung.materialcontractcombo.choose"/>',
		    tpl: materialComboTemplate,
		    valueField: 'sapMaterialId',
		    displayField: 'txt'
		});
		item.sapMatCombo = new Ext.ux.materialCombo({
		    store: new Ext.data.JsonStore({
	        	fields: [
		     	        'sapMaterialId', 
						'compositeIdentifier',
	        			'txt', 
	        			'productionPlantCd',
						'productionPlantByContract',
	        			'billtoSapBpId', 
	        			'payerSapBpId',
	        			'poolId', 
	        			'commercialCarrierId',
	        			'executingCarrierId',
	        			'contractTypeTxt',
	        			'deliveryTypeCd',
	        			'deliveryTypeTxt',
	        			'quantity', 
	        			'uomCd', 
	        			'shiptoSiloId',
	        			'shippingpointId',
	        			'loadingpointId',
	        			'autoPoolAssignmentFlag',
	        			'autoCarrierAssignmentFlag',
	        			'autoReleaseFlag',
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
	        			'supplyPlantName',
	        			'supplyPlantCd',
	        			'siloName',
	        			'poolName',
	        			'commercialCarrierName',
	        			'executingCarrierName',
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
	        			'contractSapMaterialTxt',
	        			'deliverySettingType',
	        			'resolutionType',
	        			'sapContractIncoterms',
	        			'mixDesignId',
	        			'searchByText',
	        			'companyCode',
	        			'isLandfillMaterial',
	        			'contractValidityFromDate',
	        			'contractValidityToDate'
	        			],
	        	data: productList,
				sortInfo :{
				field:'txt',
				direction:'ASC'
				} 
	    	})
		});
		
		function findBilltoByRhythmCallBack(billtoSapBpId) {
			editor.billToCombo.enable();
			if (billtoSapBpId) {
				editor.billToCombo.preselectValue(billtoSapBpId, true);
			}
			saveButtonWaitQueueSize--;
			if (saveButtonWaitQueueSize < 1) {
				saveOrderBtn.update();
			}
		}
		// Loads the Bill To list and selects the default Bill To
		editor.updateBilltoCombo = function(params) {
			var storeData = params.storeData;
			var restrictionData = params.restrictionData;
			
			editor.billToCombo.enable();
	
			if (!Ext.isEmpty(storeData)) {
				editor.billToCombo.clearValue();
				editor.billToCombo.store.loadData(storeData);
			}
			
			if (!Ext.isEmpty(restrictionData.billtoSapBpId)) {
				editor.billToCombo.preselectValue(restrictionData.billtoSapBpId, true);
			}
			
			if (!Ext.isEmpty(restrictionData.billtoRuletypeCd)) {
				saveButtonWaitQueueSize++;
				editor.billToCombo.disable();
				loadingMessage = '<fmt:message key="bestellung.indicator.loading.bill.to.rules"/>';
				OrderAjaxService.findBilltoByRhythm(
						editor.delFromDat.getValue(),
						restrictionData.shiptoSareaRestrId,
						findBilltoByRhythmCallBack
				);
			}
		}

		// Loads the Payer list and selects the default Payer
		editor.updatePayerCombo = function(params) {
			var storeData = params.storeData;
			var restrictionData = params.restrictionData;

			if (storeData) {
				editor.payerCombo.store.loadData(storeData);
			}

			if (params.payerIgnored) {
				editor.payerCombo.disable();
			}
			else {
				editor.payerCombo.enable();
			}

			if (!Ext.isEmpty(restrictionData.payerSapBpId)) {
				editor.payerCombo.preselectValue(restrictionData.payerSapBpId, true);
			}
			else {
				editor.payerCombo.clearValue();
			}
		}

    item.sapMatCombo.on('beforeSelect', sapMatBeforeSelectFn);
    
	/** This function sends the selected shiptosilo's id to the server.*/
	var shiptoSiloSelectFn = function(combo, record) {
		// --- START DWR call batching ---
		dwr.engine.beginBatch();
		var firstEditor = editorList[selectedEditorIdx];
		var firstOrderItem = firstEditor.orderItems[0];
		var sapMatCombo = firstOrderItem.sapMatCombo;
		var sapMatId = sapMatCombo.getValue();
		OrderAjaxService.getCollectedEditorData({
			'editorIdx': <%=idx%>,
			'orderItemIdx': <%=itemIdx%>,
			'sapMatId': sapMatId,
			'shiptoSiloId': record.data.shiptoSiloId
			},
		function(res) {
			;
		});
		// --- END DWR call batching ---
		dwr.engine.endBatch();
	}
	
	// "on select" handler for the material selection combo box
	var sapMatSelectFn = function(combo, record) {
    	spinner.show(<%=itemIdx%>);
    	
		saveOrderBtn.disable();
		adjustDeliveriesBtn.disable();

		editor.delFromDat.disable();		
		editor.delToDat.disable();		
		editor.delFromTime.disable();		
		editor.delToTime.disable();		

		// --- START DWR call batching ---
		dwr.engine.beginBatch();
		
		var sapMatId = record.data.sapMaterialId;

		Ext.getDom('<%=itemStr%>.itemNoteOrderMgmt').value = nvl(record.data.noteOrderMgmt);
		Ext.getDom('<%=itemStr%>.itemNoteDelivery').value = nvl(record.data.noteDelivery);
		Ext.getDom('<%=itemStr%>.itemNoteDispatching').value = nvl(record.data.noteDispatching);
		Ext.getDom('<%=itemStr%>.itemNoteTruckDriver').value = nvl(record.data.noteTruckDriver);
		Ext.getDom('<%=itemStr%>.itemNoteRailway').value = nvl(record.data.noteRailwayDisp);
		
		// --- Pool ---
		editor.poolCombo.disable();
		editor.poolCombo.clearValue();
		
		// --- Carrier: load the values by Ajax, depending on Pool ---
		editor.commercialCarrierCombo.disable();
		editor.commercialCarrierCombo.clearValue();

		editor.executingCarrierCombo.disable();
		editor.executingCarrierCombo.clearValue();

		// --- Silo: for Pallet 40 and 48 it is not relevant ---
		if (record.data.uomCd != 'P40' && record.data.uomCd != 'P48') { // TODO constant
			editor.siloCombo.enable();
			editor.siloCombo.preselectValue(record.data.shiptoSiloId, true);
		}

		// --- Quantity ---
		item.quantityStr.enable();
		item.quantityStr.setValue(record.data.quantity || 1);
		
		// --- Uom ---
		item.uomCombo.enable();
		item.uomCombo.preselectValue(record.data.uomCd, true);

		// --- Truck Type
		editor.truckTypeCombo.disable();
		editor.truckTypeCombo.updateDataAndMeta({});
			
		// --- Checking pallet quantity ---
		checkQuantityOfPalletsFn();
		
		// --- Reset the shipping point combo ---
		editor.spCombo.disable();
		editor.spCombo.clearValue();
		
		// --- Reset the loading point combo ---
		item.lpCombo.disable();
		item.lpCombo.clearValue();
		
		// --- Reset bimodal rail order list ---
		editor.biRailItemCombo.disable();
		editor.biRailItemCombo.clearValue();
		
		// --- Reset for the production plant and supply plant list --- 
		item.prodPlantCombo.disable();
		item.prodPlantCombo.clearValue();
		
		editor.supplyPlantCombo.disable();
		editor.supplyPlantCombo.clearValue();
		
		// --- Reset the Bill To combo ---
		editor.billToCombo.disable();
		editor.billToCombo.clearValue();
		
		// --- Reset the Payer combo ---
		editor.payerCombo.disable();
		editor.payerCombo.clearValue();

		setCustomeDwrEngineExceptionHandler(function(err, exc, c) {
	        setCustomeDwrEngineExceptionHandler(null);
	        spinner.hide(<%=itemIdx%>);
	  });
		
		OrderAjaxService.getCollectedEditorData({
    		'editorIdx': <%=idx%>, 
			'orderItemIdx': <%=itemIdx%>,
    		'sapMatId': sapMatId, 
    		'supplyPlantCd': -1, // derived from shipping point
    		'spId': (record.data.shippingpointId || -1), 
    		'poolId': (record.data.poolId || -1), 
    		'quantity': (record.data.quantity || 1), 
    		'delTypeCd': (record.data.deliveryTypeCd || -1), 
    		'prodPlantCd': (record.data.productionPlantCd || -1),
    		'delFromDat': editor.getDelToDateTime(), 
    		'delToDat': editor.getDelToDateTime(), 
    		'trspZoneCd': trspZoneCd, 
    		'uomCd': (record.data.uomCd || -1),
    		'sareaId': (record.data.sareaId || -1),
    		'sapContractId': (record.data.sapContractId || -1),
    		'sapContractItemId': (record.data.sapContractItemId || -1),
    		'shiptoSareaRestrId': (record.data.shiptoSareaRestrId || -1),
    		'compositeIdentifier': record.data.compositeIdentifier,
    		'shiptoSiloId': record.data.shiptoSiloId
    		}, 
    	function(res) {
    		setCustomeDwrEngineExceptionHandler(null);
    		
   			// Setting print delivery note defaults from DB state depending on the selected material 
    		printSingleDeliveryNoteFlag = res.printSingleDeliveryNoteFlag;
    		printCollDeliveryNoteFlag = res.printCollDeliveryNoteFlag;
    		Ext.getDom('orderCommon.delivnoteSinglePaperFlag').checked = printSingleDeliveryNoteFlag;
    		<%-- LOGON-5630: DeliveryNoteCollFlag is no longer saved on indiviadual Order but on CustOrder--%>
    		Ext.getDom('delivnoteCollPaperFlag').checked = printCollDeliveryNoteFlag;
			
			
  			// --- Price ---
			updateMaterialInfo(<%=idx%>, <%=itemIdx%>, res.orderItems[<%=itemIdx%>]);
    	
			// on delivery from / to ---
			editor.delFromDat.enable();
			editor.delToDat.enable();
			editor.delFromTime.enable();
			editor.delToTime.enable();

			
			// --- Delivery type ---
			editor.delTypeCombo.store.loadData(res.orderItems[<%=itemIdx%>].deliverySetting.deliveryOptionConfiguration.deliveryTypeList);
			editor.delTypeCombo.enable();
			editor.delTypeCombo.preselectValue(record.data.deliveryTypeCd);
			
			// --- ShipTo Silo
			if (isReturnPickupOrder(editor.delTypeCombo.getValue()) || isLandfillOnlyMode(editor)) {
				editor.siloCombo.disable();
			}
			else {
				editor.siloCombo.enable();
			}
			
			// --- UOM ---
			item.uomCombo.store.loadData(res.uomList);
			item.uomCombo.enable();
			item.uomCombo.preselectValue(record.data.uomCd, true);
			
			// --- Truck Type
			editor.truckTypeCombo.updateDataAndMeta(res.vehicleTypeWrapperList);
			editor.truckTypeCombo.enable();
			
			// LOGON-5561: enable only these vehicles in the checkCombobox that
			// are actually set in the order.
			var theDeliverySetting = res.orderItems[<%=itemIdx%>].deliverySetting;
 			if (theDeliverySetting.vehicleTypeCds && theDeliverySetting.vehicleTypeCds.length > 0)
 			{
				for (var i = 0; i < theDeliverySetting.vehicleTypeCds.length; i++) {
					
					var recordId = theDeliverySetting.vehicleTypeCds[i];
					
  					var idx = editor.truckTypeCombo.store.find(editor.truckTypeCombo.valueField, new RegExp('^' + recordId + '$'));
  					var daRecord = editor.truckTypeCombo.store.getAt(idx);
					
 					// Only trying to check if record available => JS error otherwise
 					if (daRecord) {  // don't call this variable 'record' or it will f--- everything up ;-)
 						editor.truckTypeCombo.setChecked(recordId, true);
 					} 
				}
 			}
			else {
				editor.truckTypeCombo.selectAll();
			}
			editor.truckTypeCombo.updateList();
			
			// --- Delivery Fqy Every Time UOM
			editor.deliveryFqyEveryTimeUomCombo.store.loadData(convertMapToList(res.timeUomMap));
			
			// --- Delivery Fqy Quantity Per Hre UOM
			editor.deliveryFqyQtyPerHreUomCombo.store.loadData(res.uomList);
			
			// --- Delivery Time Tolerance UOM
			editor.deliveryTimeToleranceUomCombo.store.loadData(convertMapToList(res.timeToleranceUomMap));
			
			// --- Special Editor Elements
			if (isSpecialEditorNeeded(editor.delTypeCombo.getValue())) {
				setSpecialEditorActive(<%=idx%>, <%=itemIdx%>, true);
			}
			else {
				setSpecialEditorActive(<%=idx%>, <%=itemIdx%>, false);
			}
			
    		// --- Production plant ---
			item.prodPlantCombo.store.loadData(res.prodPlantList);
			item.prodPlantCombo.preselectValue(record.data.productionPlantCd, true);
			
			item.mixDesignCombo.store.loadData(res.mixDesignList);
			item.mixDesignCombo.preselectValue(record.data.mixDesignId, true);

			if (!res.orderItems[<%=itemIdx%>].deliverySetting.productionPlantEditable) {
				item.prodPlantCombo.disable();
			}
			else {
				item.prodPlantCombo.enable();
			}

			// --- SP ---
			editor.spCombo.store.loadData(res.spList);
			editor.spCombo.preselectValue(record.data.shippingpointId, true);
			editor.spCombo.enable();

			toggleRailDeliveryData(0, res.railData);

			if (isBimodalTruckPartOrder(editor.delTypeCombo.getValue())) {
				// --- Bimodal Rail Order Item list ---
	    		editor.biRailItemCombo.store.loadData(res.biRailItemList);
	    		editor.biRailItemCombo.enable();
				toggleBiRailItemCombo(<%=idx%>, true);
			}
			else {
				// --- LP ---
	    		item.lpCombo.store.loadData(res.lpList);
	    		item.lpCombo.preselectValue(record.data.loadingpointId, true);
    			if (!isServiceOnlyMode(<%=idx%>)) {
    				item.lpCombo.enable();
	    		}
				toggleBiRailItemCombo(<%=idx%>, false);
			}
			
			// --- Supply Plant ---
			editor.supplyPlantCombo.store.loadData(res.supplyPlantList);
			editor.supplyPlantCombo.preselectValue(editor.spCombo.getSelectedRecord() != null ? editor.spCombo.getSelectedRecord().data.plantCd : null, true);
			editor.supplyPlantCombo.enable();		
			
			// --- Pool and carrier: only for non-pickup ---
			if (! (isPickupOrder(record.data.deliveryTypeCd) || isShipOrBlockTrainPickupOrder(record.data.deliveryTypeCd) )) {

				editor.poolCombo.store.loadData(res.poolList);
				editor.poolCombo.preselectValue(record.data.poolId, true);
				editor.poolCombo.enable();

				var commercialCarrierId = record.data.commercialCarrierId;
			
				editor.commercialCarrierCombo.store.loadData(res.commercialCarrierList);
				editor.commercialCarrierCombo.preselectValue(commercialCarrierId, true);
				editor.commercialCarrierCombo.enable();
				
				if (commercialCarrierId != null){
					refreshChartArea({
						 'selectCarrier': commercialCarrierId,
						 'isOnCallCenter': 'true',
						 'isChartDisplayable' : 'true',
						 'isLandfill' : isLandfillOnlyMode(editor)
					});			
				}
				else{
					refreshChartArea({
						 'isOnCallCenter': 'true',
						 'isChartDisplayable' : 'false',
						 'isLandfill' : isLandfillOnlyMode(editor)
					});	
				}
			}
			else{
				refreshChartArea({
					 'isOnCallCenter': 'true',
					 'isChartDisplayable' : 'false',
					 'isLandfill' : isLandfillOnlyMode(editor)
				});	
			}

			var executingCarrierId = record.data.executingCarrierId;

			editor.executingCarrierCombo.store.loadData(res.executingCarrierList);
			editor.executingCarrierCombo.preselectValue(executingCarrierId, true);
			editor.executingCarrierCombo.enable();

			if (isShipOrBlockTrainPickupOrder(record.data.deliveryTypeCd)) {
				editor.poolCombo.disable();
				editor.commercialCarrierCombo.disable();
				editor.executingCarrierCombo.disable();
			}
			else
			{
				editor.poolCombo.enable();
				editor.commercialCarrierCombo.enable();
				editor.executingCarrierCombo.enable();
			}
			
			editor.quantityPerSdoStr.setValue(res.quantityPerSdoStr);
			editor.quantityPerSdoUomCd.setValue(res.quantityPerSdoUomCd);
			editor.uomPerDeliveryLabel.setText(res.quantityPerSdoText);
			editor.exactCustorderQtyCheckbox.setValue(res.exactCustorderQtyFlag);
			editor.exactQtyPerSdoCheckbox.setValue(res.exactQtyPerSdoFlag);
			editor.deliveryFqyEveryTimeStr.setValue(res.deliveryFqyEveryTimeStr);
			editor.deliveryFqyEveryTimeUomCombo.setValue(res.deliveryFqyEveryTimeUomCd);
			editor.deliveryFqyQtyPerHreUomCombo.setValue(res.deliveryFqyQtyPerHreUomCd);
			editor.deliveryFqyQtyPerHreStr.setValue(res.deliveryFqyQtyPerHreStr);
			editor.deliveryTimeToleranceStr.setValue(res.deliveryTimeToleranceStr);
			editor.deliveryTimeToleranceUomCombo.setValue(res.deliveryTimeToleranceUomCd);
			
			if (isLandfillOnlyMode(editor)) {
				item.mixDesignCombo.disable();
			}
			else{
				item.mixDesignCombo.enable();
			}
			
			if (!Ext.isEmpty(res.mixDesignId)) {
				item.mixDesignCombo.preselectValue(res.mixDesignId,true);
			}
			
			<%--COMBINED COMBO CONTAINING VARIOUS FLAG--%>
			editor.combinedCombo.initChecked('onHoldOrderAndLockedFlag',res.onHoldOrderAndLockedFlag);
			editor.combinedCombo.initChecked('sapSuppressMinChargeFlag',res.sapSuppressMinChargeFlag);
			editor.combinedCombo.initChecked('openCustorderFlag',res.openCustorderFlag);
			editor.combinedCombo.initChecked('releaseSapFlag',res.releaseSapFlag);
			editor.combinedCombo.initChecked('freeDeliveryFlag',res.freeDeliveryFlag);
			editor.combinedCombo.initChecked('autoReleaseFlag',res.autoReleaseFlag);

  			editor.combinedCombo.updateList();
   			editor.combinedCombo.setDisabled('releaseSapFlag',!res.orderItems[<%=itemIdx%>].deliverySetting.releaseSapFlagEditable);
   			editor.combinedCombo.setDisabled('freeDeliveryFlag',!res.orderItems[<%=itemIdx%>].deliverySetting.freeOfChargeEditable);
   			editor.combinedCombo.setDisabled('autoReleaseFlag',!res.orderItems[<%=itemIdx%>].deliverySetting.autoReleaseFlagEditable);
			// 28-Jan-2015/toan: add function to disable on hold order checkbox if the delivery type is pickup
   			editor.combinedCombo.setDisabled('onHoldOrderAndLockedFlag',!res.orderItems[<%=itemIdx%>].deliverySetting.onHoldOrderAndLockedFlagEditable);
   			editor.combinedCombo.setDisabled('openCustorderFlag',!res.orderItems[<%=itemIdx%>].deliverySetting.openCustorderFlagEditable);
   			editor.combinedCombo.setDisabled('sapSuppressMinChargeFlag',!res.orderItems[<%=itemIdx%>].deliverySetting.sapSuppressMinChargeEditable);

			editor.serviceProcessIndicatorList.store.loadData(res.serviceProcessIndicatorList);
			editor.serviceProcessIndicatorList.preselectValue(res.serviceProcessIndicatorId, true);
			editor.serviceProcessIndicatorList.enable();
			
  			
			// --- Auto Pool/Carrier assignment flags ---
			Ext.getDom('editorList[<%=idx%>].autoPoolAssignmentFlag').checked = (record.data.autoPoolAssignmentFlag && !editor.poolCombo.isEmptySelection());
			Ext.getDom('editorList[<%=idx%>].autoCarrierAssignmentFlag').checked = (record.data.autoCarrierAssignmentFlag && !editor.commercialCarrierCombo.isEmptySelection());
			
			updateAutoFlags('', <%=idx%>);

			// --- Enable and set default value for bill-to ---
			editor.updateBilltoCombo({
				storeData: res.billtoList,
				restrictionData: record.data
			});

			// --- Enable and set default value for payer ---
			editor.updatePayerCombo({
				storeData: res.payerList,
				restrictionData: record.data,
				payerIgnored: res.payerIgnored
			});

			// --- Duration correction factor---
			editor.durationCorrFactor = res.durationCorrectionFactor;
			durationCorrFactor = res.durationCorrectionFactor; // FIXME should be handled per editor
			
			jsonData[<%=idx%>] = res;
			jsonData[<%=idx%>].supplyPlantCd = editor.supplyPlantCombo.getValue(); // Derived from the Shipping Point 
			
			// --- Update Google map, depending on the SP ---
			if (!editor.spCombo.isEmptySelection()) {
				updateDurationTimeAndMap(editor.spCombo.getSelectedRecord(), record.data.deliveryTypeCd, res.railData.durationMinutes, res.railData.distanceMeters, res.durationCorrectionFactor);
			}
			else {
				logonGmap.loadMap();
				updateTravelTimeFields('map');
				if (!editor.delTypeCombo.isEmptySelection() && editor.spCombo.store.data.length < 2 && !isServiceOnlyOrder(editor.delTypeCombo.getValue())) {
					Logon.message.warning('<fmt:message key="bestellung.error.no.sp.for.product"/>');
				}
			}							
			
			// -- Update visibility of special editor fields
			if (isSpecialEditorNeeded(editor.delTypeCombo.getValue())) {
				setSpecialEditorActive(<%=idx%>, <%=itemIdx%>, true);
				if (editor.deliveryFqyEveryTimeStr.getValue() || editor.deliveryFqyQtyPerHreStr.getValue()) {
					setPerformanceOrderActive(editor, item, <%=idx%>, <%=itemIdx%>, true);
				}
				else {
					setPerformanceOrderActive(editor, item, <%=idx%>, <%=itemIdx%>, false);
				}
			}
			else {
				setSpecialEditorActive(<%=idx%>, <%=itemIdx%>, false);
				setPerformanceOrderActive(editor, item, <%=idx%>, <%=itemIdx%>, false);
			}
			
			// enable/disable service only order mode
			updateServiceOnlyMode(<%=idx%>, <%=itemIdx%>);
			
			spinner.hide(<%=itemIdx%>);
			addOrderItemBtn.enable(); // In case of bulk product, disable (make it gray) the "Add other product" button
			addServiceOrderItemBtn.enable();
		});		
		adjustDeliveriesBtn.update();
		
		// --- END DWR call batching ---
		dwr.engine.endBatch();
	}
	
	item.sapMatCombo.on('select', sapMatSelectFn);

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
			    		'delFromDat': editor.getDelToDateTime(),
			    		'delToDat': editor.getDelToDateTime(),
			    		'trspZoneCd': trspZoneCd,
			    		'uomCd': null
			    		}, 
			    	function(res) {
	
		    			toggleRailDeliveryData(0, res.railData);
			        	
			    		editor.biRailItemCombo.store.loadData(res.biRailItemList);
			    		editor.biRailItemCombo.enable();
			    		
						jsonData[<%=idx%>] = res;
			    	});
	        	}
	        	else {
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
			    		'delFromDat': editor.getDelToDateTime(),
			    		'delToDat': editor.getDelToDateTime(),
			    		'trspZoneCd': trspZoneCd,
			    		'uomCd': null
			    		}, 
			    	function(res) {
				    		item.mixDesignCombo.store.loadData(res.mixDesignList);	        		
							jsonData[<%=idx%>] = res;
				    });			    			
	        	}
	        	addOrderItemBtn.enable();
				addServiceOrderItemBtn.enable();
			}		
		},
	    tpl: '<tpl for="."><div class="x-combo-list-item">{plantName} <tpl if="plantName != \'...\'"><span class="dynlabel">({plantCd})</span></tpl></div></tpl>',
	    width: 120,
	    listWidth: 180,
	    valueField: 'plantCd',
	    displayField: 'plantName',
	    disabled: isEditorDisabled
	});
	
	
	// --- SERVICE ONLY ORDER ---
	editor.serviceOnlyOrderCheckbox = new Ext.form.Checkbox({
		id: '<%=editorStr%>.serviceOnlyOrderFlag',
		applyTo: '<%=editorStr%>.serviceOnlyOrderFlag',
    	checked: false,
    	prodList: productList,
    	soProdList: serviceOnlyProductList,
    	disabled: <%=(idx!=0)%>,
    	handler: function(element, checked) {

    		item.sapMatCombo.disable();
    		item.sapMatCombo.clearValue();

    		if (checked) {
    			item.sapMatCombo.store.loadData(element.soProdList);
    		}
    		else {
    			item.sapMatCombo.store.loadData(element.prodList);
    		}

    		item.sapMatCombo.enable();
    		
    		addOrderItemBtn.enable();

    	}
	});
	
	// --- SERVICE ONLY ORDER ---
	<c:if test="${idx == 0}" >
	editor.allowFutureContractCheckbox = new Ext.form.Checkbox({
		id: 'allowFutureContracts',
		applyTo: 'allowFutureContracts',
    	checked: ${allowFutureContracts},
    	handler: function(element, checked) {
    		futureContracts(checked);
    	}
	});
	</c:if>
	
	// --- MIX DESIGN ---
	item.mixDesignCombo = new Ext.form.ComboBox({
		mode: 'local',
		editable: false,
		forceSelection: true,
		id: '<%=itemStr%>.mixDesignCombo',
		name: '<%=itemStr%>.mixDesignId',
	    transform:'<%=itemStr%>.mixDesignId',
	    triggerAction: 'all',
	    store: new Ext.data.JsonStore({
        	fields: [
        		{name: 'mixDesignId', mapping: 'mixDesignId'},
        		{name: 'mixDesignCd', mapping: 'mixDesignCd'},
        		{name: 'aggRmxCode', mapping: 'aggRmxCode'}], 
            emptyItem: {'mixDesign' : {'aggRmxCode': '...', 'mixDesignId': '-1'}},
        	data : jsonData[<%=idx%>].mixDesignList 
    	}),
	    tpl: '<tpl for="."><div class="x-combo-list-item">{aggRmxCode} <tpl if="aggRmxCode != \'...\'"><span class="dynlabel">({mixDesignCd})</span></tpl></div></tpl>',
	    width: 120,
	    listWidth: 180,
	    valueField: 'mixDesignId',
	    displayField: 'aggRmxCode',
	    disabled: isEditorDisabled || isLandfillOnlyMode(editor),
	    listeners: {
		    select: function(combo, rec) {		     
			    	OrderAjaxService.getCollectedEditorData({
			    		'editorIdx': <%=idx%>, 
						'orderItemIdx': <%=itemIdx%>,
			    		'sapMatId': null, 
			    		'supplyPlantCd': null, 
			    		'prodPlantCd': rec.data.plantCd, 
			    		'spId': null, 
			    		'poolId': null, 
			    		'quantity': null, 
			    		'delTypeCd': null,
			    		'delFromDat': null,
			    		'delToDat': null,
			    		'trspZoneCd': null,
			    		'uomCd': null,
			    		'mixDesignId': rec.data.mixDesignId,
			    		'mixDesignCd': rec.data.mixDesignCd
			    		});
			}		
		}
	});
	
	// --- CUST. REF. NO. ---
	editor.custorderCustrefNo = new Ext.form.TextField({
		id: '<%=editorStr%>.custorderCustrefNo',
		allowBlank: true,
		applyTo: '<%=editorStr%>.custorderCustrefNo',
		selectOnFocus: true,
		enableKeyEvents: true,
		maxLength: 35,
		maxLengthText: '<fmt:message key="bestellung.error.invalid.customer.reference.number"/>',
		style: 'text-align: right',
		width: 100
	});
	

	// --- TRUCK ---
	editor.truckTypeCombo = new Ext.ux.TableStyleCheckComboBox({
		transform:'editorList[<%=idx%>].truckTypes',
		store: new Ext.data.JsonStore({
			fields: [
				{name: 'truckTypeCd', mapping: 'typeCd'},
				{name: 'priority', mapping: 'priority'},
				{name: 'truckTypeName', mapping: 'typeName'},
				{name: 'maxGrossWeightT', mapping: 'maxGrossWeightT'}
			],
			data: jsonData[<%=idx%>].vehicleTypeWrapperList
		}),
		emptyText: '<fmt:message key="extjs.select"/>',
		displayField: 'truckTypeName',
		checkedField: 'checked',
		valueField: 'truckTypeCd',
		additionalColumns: ['priority', 'maxGrossWeightT'],
		checkboxColumnTitle: '<fmt:message key="bestellung.texts.trucktype.type"/>',
		additionalColumnTitles: [
			'<fmt:message key="bestellung.texts.trucktype.prio"/>',
			'<fmt:message key="bestellung.texts.trucktype.maxgrossweight"/>'
		],
		mode: 'local',
		triggerAction: 'all',
		selectOnFocus: true,
		width: 100,
		listWidth: 350,
		maxHeight: 400, // LOGON-5682: Usability of truck type list
		checkboxColumnWidth: 150,
		additionalColumnWidths: [50, 150],
		disabled: isEditorDisabled,
		listeners: {
			change: function(checkcombo) {
				checkcombo.updateMaxCapacity();
			}
		},
		
		getSelectedTruckTypes: function() {
			return this.getCheckedValues() ? this.getCheckedValues().join(',') : '';
		},
		getMaxPrioTruckRecord: function() {
			var result;
			this.store.each(function(record) {
				if (record.data[this.checkedField]) {	
					if (result) {
						var actualMaxPriority = result.priority;
						var tmp = record.data;
						var tmpMaxPriority = tmp.priority;
						
						if (actualMaxPriority == -1 && tmpMaxPriority > -1) {
							result = tmp;
						}
						else if (!((actualMaxPriority > -1 && tmpMaxPriority == -1)
								|| (actualMaxPriority > 0 && tmpMaxPriority == 0))) {
							if (actualMaxPriority > tmpMaxPriority) {
								result = tmp;
							}							
							else if (actualMaxPriority == tmpMaxPriority) {
								var actualMaxGrossWeightT = result.maxGrossWeightT;
								var tmpMaxGrossWeightT = tmp.maxGrossWeightT;
								
								if (actualMaxGrossWeightT < tmpMaxGrossWeightT) {
									result = tmp;
								}
							}
						}
					}
					else {
						result = record.data;
					}
				}
			}, this);
			
			return result;
		},
		updateMaxCapacity: function() {
			var maxPrioSelectedTruck = this.getMaxPrioTruckRecord();
			if (maxPrioSelectedTruck) {
				editor.quantityPerSdoStr.setValue(maxPrioSelectedTruck.maxGrossWeightT);
				editor.quantityPerSdoUomCd.setValue('<%= Constant.UOM_TONS %>');
			}
			else {
				editor.quantityPerSdoStr.setValue('');
				editor.quantityPerSdoUomCd.setValue('<%= Constant.UOM_TONS %>');
			}
			var selectedOnes = this.getSelectedTruckTypes();
			Ext.getDom('<%=editorStr%>.selectedTruckTypes').value = selectedOnes;
		},
		updateDataAndMeta: function(vehicleTypeWrapperList) {
			this.store.loadData(vehicleTypeWrapperList, false);
			this.initList();
			this.updateMaxCapacity();
		}
	});
	
	
	// --- EXACT CUSTORDER QUANTITY
	editor.exactCustorderQtyCheckbox = new Ext.form.Checkbox({
		id: '<%=editorStr%>.exactCustorderQtyFlag',
		applyTo: '<%=editorStr%>.exactCustorderQtyFlag',
    	checked: false,
    	handler: function(element, checked) {
    		updateCustorderSplitMetaData();
    	},
    	disabled: isLandfillOnlyMode(editor)
	});
	
	
	// --- EXACT QUANTITY PER SINGLE DELIVERY ORDER
	editor.exactQtyPerSdoCheckbox = new Ext.form.Checkbox({
		id: '<%=editorStr%>.exactQtyPerSdoFlag',
		applyTo: '<%=editorStr%>.exactQtyPerSdoFlag',
    	checked: false,
    	handler: function(element, checked) {
    		updateCustorderSplitMetaData();
    	},
    	disabled: isLandfillOnlyMode(editor)
	});

	// --- QUANTITY PER SINGLE DELIVERY ---
	var invalidQuantityPerSdoStr = '<fmt:message key="bestellung.error.invalid.quantity.per.delivery"/>'.replace('{0}', '<%= Constant.CUSTORDER_QTY_PER_SDO_MIN_VALUE_STR %>').replace('{1}', '<%= Constant.CUSTORDER_QTY_PER_SDO_MAX_VALUE_STR %>').replace('{2}', editorLocatorMsg.replace('@', '<%=(idx+1)%>'));
	editor.quantityPerSdoStr = new Ext.form.NumberField({
		invalidText: invalidQuantityPerSdoStr,
		id: '<%=editorStr%>.quantityPerSdoStr',
		applyTo: '<%=editorStr%>.quantityPerSdoStr',
		allowBlank: true,
		allowNegative: false,
		allowDecimals: true,
		decimalPrecision: 3,
		minValue: '<%= Constant.CUSTORDER_QTY_PER_SDO_MIN_VALUE_STR %>',
		minText: invalidQuantityPerSdoStr,
		maxValue: '<%= Constant.CUSTORDER_QTY_PER_SDO_MAX_VALUE_STR %>',
		maxText: invalidQuantityPerSdoStr,
		maxLength: 9,
		maxLengthText: invalidQuantityPerSdoStr, 
		validator: function(value) {
			if (getDecimalPrecision(value) > this.decimalPrecision) {
				return '<fmt:message key="bestellung.error.invalid.decimal.precision"/>'.replace('{0}', this.decimalPrecision);
			}
			return true;
		},
		selectOnFocus: true,
		enableKeyEvents: true,
		style: 'text-align: right',
		width: 45,
		disabled: isEditorDisabled
	});

	// --- QUANTITY PER SINGLE DELIVERY UOM ---
	editor.quantityPerSdoUomCd = new Ext.form.Hidden({
		id: '<%=editorStr%>.quantityPerSdoUomCd',
		applyTo: '<%=editorStr%>.quantityPerSdoUomCd'
	});
	
	// --- UOM PER DELIVERY LABEL ---
	editor.uomPerDeliveryLabel = new Ext.form.Label({
		id: 'editorList[<%=idx%>].uomPerDeliveryLabel',
		applyTo: 'editorList[<%=idx%>].uomPerDeliveryText'
	});

	
	// --- DELIVERY FREQ EVERY TIME ---
	var invalidDeliveryFqyEveryTimeStr = '<fmt:message key="bestellung.error.invalid.delivery.frequency"/>'.replace('{0}', '<%= Constant.CUSTORDER_DELIVERY_FQY_EVERY_TIME_MIN_VALUE_STR %>').replace('{1}', '<%= Constant.CUSTORDER_DELIVERY_FQY_EVERY_TIME_MAX_VALUE_STR %>').replace('{2}', editorLocatorMsg.replace('@', '<%=(idx+1)%>'));
	editor.deliveryFqyEveryTimeStr = new Ext.form.NumberField({
		invalidText: invalidDeliveryFqyEveryTimeStr,
		id: '<%=editorStr%>.deliveryFqyEveryTimeStr',
		applyTo: '<%=editorStr%>.deliveryFqyEveryTimeStr',
		allowBlank: true,
		allowNegative: false,
		allowDecimals: true,
		decimalPrecision: 2,
		minValue: '<%= Constant.CUSTORDER_DELIVERY_FQY_EVERY_TIME_MIN_VALUE_STR %>',
		minText: invalidDeliveryFqyEveryTimeStr,
		maxValue: '<%= Constant.CUSTORDER_DELIVERY_FQY_EVERY_TIME_MAX_VALUE_STR %>',
		maxText: invalidDeliveryFqyEveryTimeStr,
		maxLength: 7,
		maxLengthText: invalidDeliveryFqyEveryTimeStr,
		validator: function(value) {
			if (getDecimalPrecision(value) > this.decimalPrecision) {
				return '<fmt:message key="bestellung.error.invalid.decimal.precision"/>'.replace('{0}', this.decimalPrecision);
			}
			return true;
		},
		selectOnFocus: true,
		enableKeyEvents: true,
		style: 'text-align: right',
		width: 35,
		disabled: isEditorDisabled,
		listeners : {
			keyup: function(textField, event) {
				if (textField.getValue()) {
					if (!editor.deliveryFqyEveryTimeUomCombo.getValue() || editor.deliveryFqyEveryTimeUomCombo.getValue() == -1) {
						editor.deliveryFqyEveryTimeUomCombo.setValue('<%= Constant.UOM_MIN %>');
					}
					editor.deliveryFqyQtyPerHreStr.setValue(undefined);
					setPerformanceOrderActive(editor, item, <%=idx%>, <%=itemIdx%>, true);
				}
				else {
					editor.deliveryFqyEveryTimeUomCombo.reset();
					if (!editor.deliveryFqyQtyPerHreStr.getValue()) {
						setPerformanceOrderActive(editor, item, <%=idx%>, <%=itemIdx%>, false);
					}
				}
			}
		}
	});
	
	// --- DELIVERY FREQ EVERY TIME UOM ---
	editor.deliveryFqyEveryTimeUomCombo = new Ext.form.ComboBox({
		mode: 'local',
		editable: false,
		forceSelection: true,
		id: '<%=editorStr%>.deliveryFqyEveryTimeUomCombo',
	    transform:'<%=editorStr%>.deliveryFqyEveryTimeUomCd',
	    triggerAction: 'all',
	    store: new Ext.data.JsonStore({
        	fields: [
        		{name: 'deliveryFqyEveryTimeUomCd', mapping: 'key'},
        		{name: 'deliveryFqyEveryTimeUomStr', mapping: 'value'}
        	],
        	data: jsonData[<%=idx%>].timeUomMap,
    		emptyItem: {'key': '-1', 'value': '...'}
    	}),
	    valueField: 'deliveryFqyEveryTimeUomCd',
	    displayField: 'deliveryFqyEveryTimeUomStr',
	    width: 75,
	    disabled: isEditorDisabled
	});
	
	// --- DELIVERY FREQ QTY PER HRE ---
	var invalidDeliveryFqyQtyPerHreStr = '<fmt:message key="bestellung.error.invalid.delivery.frequency"/>'.replace('{0}', '<%= Constant.CUSTORDER_DELIVERY_FQY_QTY_PER_HRE_MIN_VALUE_STR %>').replace('{1}', '<%= Constant.CUSTORDER_DELIVERY_FQY_QTY_PER_HRE_MAX_VALUE_STR %>').replace('{2}', editorLocatorMsg.replace('@', '<%=(idx+1)%>'));
	editor.deliveryFqyQtyPerHreStr = new Ext.form.NumberField({
		invalidText: invalidDeliveryFqyQtyPerHreStr,
		id: '<%=editorStr%>.deliveryFqyQtyPerHreStr',
		applyTo: '<%=editorStr%>.deliveryFqyQtyPerHreStr',
		allowBlank: true,
		allowBlank: true,
		allowNegative: false,
		allowDecimals: true,
		decimalPrecision: 2,
		minValue: '<%= Constant.CUSTORDER_DELIVERY_FQY_QTY_PER_HRE_MIN_VALUE_STR %>',
		minText: invalidDeliveryFqyQtyPerHreStr,
		maxValue: '<%= Constant.CUSTORDER_DELIVERY_FQY_QTY_PER_HRE_MAX_VALUE_STR %>',
		maxText: invalidDeliveryFqyQtyPerHreStr,
		maxLength: 9,
		maxLengthText: invalidDeliveryFqyQtyPerHreStr,
		validator: function(value) {
			if (getDecimalPrecision(value) > this.decimalPrecision) {
				return '<fmt:message key="bestellung.error.invalid.decimal.precision"/>'.replace('{0}', this.decimalPrecision);
			}
			return true;
		},
		selectOnFocus: true,
		enableKeyEvents: true,
		style: 'text-align: right',
		width: 45,
		disabled: isEditorDisabled,
		listeners : {
			keyup: function(textField, event) {
				if (textField.getValue()) {
					if (!editor.deliveryFqyQtyPerHreUomCombo.getValue() || editor.deliveryFqyQtyPerHreUomCombo.getValue() == -1) {
						var orderedUom = item.uomCombo.getValue();
						if (orderedUom && orderedUom != -1) {
							editor.deliveryFqyQtyPerHreUomCombo.setValue(orderedUom);
						}
						else {
							editor.deliveryFqyQtyPerHreUomCombo.setValue('<%= Constant.UOM_TONS %>');
						}
					}
					editor.deliveryFqyEveryTimeStr.setValue(undefined);
					setPerformanceOrderActive(editor, item, <%=idx%>, <%=itemIdx%>, true);
				}
				else {
					editor.deliveryFqyQtyPerHreUomCombo.reset();
					if (!editor.deliveryFqyEveryTimeStr.getValue()) {
						setPerformanceOrderActive(editor, item, <%=idx%>, <%=itemIdx%>, false);
					}
				}
			}
		}
	});
	
	// --- DELIVERY FREQ QTY PER HRE UoM ---
	editor.deliveryFqyQtyPerHreUomCombo = new Ext.form.ComboBox({
		mode: 'local',
		editable: false,
		forceSelection: true,
		id: '<%=editorStr%>.deliveryFqyQtyPerHreUomCombo',
	    transform: '<%=editorStr%>.deliveryFqyQtyPerHreUomCd',
	    triggerAction: 'all',
	    store: new Ext.data.JsonStore({
        	fields: [
        		{name: 'uomCd', mapping: 'uomTxPK.uomBs.uomCd'}, 
        		{name: 'txt', mapping: 'txt'}
        	],
        	data : jsonData[<%=idx%>].uomList 
    	}),
	    valueField: 'uomCd',
	    displayField: 'txt',
	    width: 75,
	    disabled: isEditorDisabled
	});
	
	// --- DELIVERY TIME TOLERANCE ---
	var invalidDeliveryTimeToleranceStr = '<fmt:message key="bestellung.error.invalid.delivery.time.tolerance"/>'.replace('{0}', '<%= Constant.CUSTORDER_DELIVERY_TIME_TOLERANCE_MIN_VALUE_STR %>').replace('{1}', '<%= Constant.CUSTORDER_DELIVERY_TIME_TOLERANCE_MAX_VALUE_STR %>').replace('{2}', editorLocatorMsg.replace('@', '<%=(idx+1)%>'));
	editor.deliveryTimeToleranceStr = new Ext.form.NumberField({
		invalidText: invalidDeliveryTimeToleranceStr,
		id: '<%=editorStr%>.deliveryTimeToleranceStr',
		applyTo: '<%=editorStr%>.deliveryTimeToleranceStr',
		allowBlank: true,
		allowNegative: false,
		allowDecimals: true,
		decimalPrecision: 2,
		minValue: '<%= Constant.CUSTORDER_DELIVERY_TIME_TOLERANCE_MIN_VALUE_STR %>',
		minText: invalidDeliveryTimeToleranceStr,
		maxValue: '<%= Constant.CUSTORDER_DELIVERY_TIME_TOLERANCE_MAX_VALUE_STR %>',
		maxText: invalidDeliveryTimeToleranceStr,
		maxLength: 7,
		maxLengthText: invalidDeliveryTimeToleranceStr,
		validator: function(value) {
			if (getDecimalPrecision(value) > this.decimalPrecision) {
				return '<fmt:message key="bestellung.error.invalid.decimal.precision"/>'.replace('{0}', this.decimalPrecision);
			}
			return true;
		},
		selectOnFocus: true,
		enableKeyEvents: true,
		style: 'text-align: right',
		width: 30,
		disabled: isEditorDisabled,
		listeners : {
			keyup: function(textField, event) {
				if (textField.getValue()) {
					if (!editor.deliveryTimeToleranceUomCombo.getValue() || editor.deliveryTimeToleranceUomCombo.getValue() == -1) {
						editor.deliveryTimeToleranceUomCombo.setValue('<%= Constant.UOM_MIN %>');
					}
				}
				else {
					editor.deliveryTimeToleranceUomCombo.reset();
				}
			}
		}
	});
	
	// --- DELIVERY TIME TOLERANCE UOM ---
	editor.deliveryTimeToleranceUomCombo = new Ext.form.ComboBox({
		mode: 'local',
		editable: false,
		forceSelection: true,
		id: '<%=editorStr%>.deliveryTimeToleranceUomCombo',
	    transform:'editorList[<%=idx%>].deliveryTimeToleranceUomCd',
	    triggerAction: 'all',
	    store: new Ext.data.JsonStore({
        	fields: [
        		{name: 'deliveryTimeToleranceUomCd', mapping: 'key'},
        		{name: 'deliveryTimeToleranceUomStr', mapping: 'value'}
        	],
        	data: jsonData[<%=idx%>].timeToleranceUomMap,
    		emptyItem: {'key': '-1', 'value': '...'}
    	}),
	    width: 50,
	    valueField: 'deliveryTimeToleranceUomCd',
	    displayField: 'deliveryTimeToleranceUomStr',
	    disabled: isEditorDisabled
	});
	
	
	// --- NUMBER OF DELIVERIES
	editor.numberOfDeliveriesLabel = new Ext.form.Label({
		id: 'editorList[<%=idx%>].numberOfDeliveriesLabel',
		applyTo: 'editorList[<%=idx%>].numberOfDeliveriesText',
		style: 'text-align: right'
	});

	// --- QUANTITY ---
	var invalidQuantityStr = '<fmt:message key="bestellung.error.invalid.quantity"/>'.replace('{0}', editorLocatorMsg.replace('@', '<%=(idx+1)%>'));
	item.quantityStr = new Ext.form.NumberField({
		invalidText: invalidQuantityStr,
		id: '<%=itemStr%>.quantityStr',
		allowBlank: true,
		allowNegative: false,
		allowDecimals: true,
		decimalPrecision: 3,
		decimalSeparator: '.',
		minValue: '<%= Constant.CUSTORDER_ORDERED_QTY_MIN_VALUE_STR %>',
		minText: invalidQuantityStr,
		maxValue: '<%= Constant.CUSTORDER_ORDERED_QTY_MAX_VALUE_STR %>',
		maxText: invalidQuantityStr,
		maxLength: 9,
		maxLengthText: invalidQuantityStr,
		applyTo: '<%=itemStr%>.quantityStr',
		selectOnFocus: true,
		disabled: isEditorDisabled,
		style: 'text-align: right',
		width: 45,
		enableKeyEvents: true,
		validator: function(value) {
			if (getDecimalPrecision(value) > this.decimalPrecision) {
				return '<fmt:message key="bestellung.error.invalid.decimal.precision"/>'.replace('{0}', this.decimalPrecision);
			}
			return true;
		},
		listeners: {
			change: function(textField, newValue, oldValue) {
				checkQuantityOfPalletsFn();
	        	addOrderItemBtn.enable();
				addServiceOrderItemBtn.enable();
			}
		}
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
	    width: 130,
	    listWidth: 180,
	    valueField: 'plantCd',
	    displayField: 'plantName',
	    disabled: isEditorDisabled
	});
	
	// --- When the Supply plant is changed ---
	function onSupplyPlantComboSelect(combo, record) {
		//if (record.data.plantCd != jsonData[<%=idx%>].supplyPlantCd) {
			saveOrderBtn.disable();
			adjustDeliveriesBtn.disable();
			resetTravelTimeFields();
			
			Ext.getDom('durationTime').value = 0;	
			Ext.getDom('distanceMeters').value = 0;
			
			editor.durationTime = '';
			editor.distanceMeters = '';
			
			// --- Reset SP list ---
			editor.spCombo.disable();
			editor.spCombo.clearValue();
			
			// --- Reset LP list ---
			item.lpCombo.disable();
			item.lpCombo.clearValue();

			// --- Reset bimodal rail order list ---
			editor.biRailItemCombo.disable();
			editor.biRailItemCombo.clearValue();
			
			// --- Reset Carrier list ---
			var commercialCarrierValue = editor.commercialCarrierCombo.getValue();
			editor.commercialCarrierCombo.disable();
			editor.commercialCarrierCombo.clearValue();

			var executingCarrierValue = editor.executingCarrierCombo.getValue();
			editor.executingCarrierCombo.disable();
			editor.executingCarrierCombo.clearValue();

			// --- Reset the Map ---
			logonGmap.loadMap();

	    	OrderAjaxService.getCollectedEditorData({
	    		'editorIdx': <%=idx%>, 
				'orderItemIdx': <%=itemIdx%>,
	    		'sapMatId': null, 
	    		'supplyPlantCd': (record.data.plantCd || '-1'), 
	    		'prodPlantCd': null, 
	    		'spId': -1, 
	    		'poolId': null, 
	    		'quantity': item.quantityStr.getValue(), 
	    		'delFromDat': editor.getDelFromDateTime(), 
	    		'delToDat': editor.getDelToDateTime(), 
	    		'delTypeCd': editor.delTypeCombo.getValue(),
	    		'trspZoneCd': trspZoneCd,
	    		'uomCd': null}, 
	    	function(res) {

        	    // --- Price ---
				updateMaterialInfo(<%=idx%>, <%=itemIdx%>, res.orderItems[<%=itemIdx%>],
						{ skipOpenQuantity: true });
	    	    	
	    		editor.spCombo.store.loadData(res.spList);
	    		item.lpCombo.store.loadData(res.lpList);
	    		editor.spCombo.preselectValue(null, true);

	    		editor.commercialCarrierCombo.store.loadData(res.commercialCarrierList);
	    		editor.commercialCarrierCombo.preselectValue(commercialCarrierValue, true);
	    		editor.executingCarrierCombo.store.loadData(res.executingCarrierList);
	    		editor.executingCarrierCombo.preselectValue(executingCarrierValue, true);

				if (!Ext.isEmpty(res.supplyPlantCd)) {
					editor.spCombo.enable();
					if (!isServiceOnlyMode(<%=idx%>)) {
						item.lpCombo.enable();					
					}
				}
				var deliveryTypeCd = nvl(editor.delTypeCombo.getValue(), null);
				if (isShipOrBlockTrainPickupOrder(deliveryTypeCd)) {					
					editor.commercialCarrierCombo.disable();
					editor.executingCarrierCombo.disable();
				}
				else{
					editor.commercialCarrierCombo.enable();
					editor.executingCarrierCombo.enable();
				}
				
				toggleRailDeliveryData(0, res.railData);
						
				jsonData[<%=idx%>] = res;
	    	});
		//}
	}
	editor.supplyPlantCombo.on('select', onSupplyPlantComboSelect);
	
	// --- SHIPPING POINT ---
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
	    disabled: isEditorDisabled,
	    width: 130,
	    listWidth: 200,
	    tpl: '<tpl for="."><div class="x-combo-list-item">{shippingpointName} <tpl if="shippingpointId != -1"><span class="dynlabel">(LKW<tpl if="railwayStation != null">,BW</tpl>)</span></tpl></div></tpl>',
	    valueField: 'shippingpointId',
	    displayField: 'shippingpointName'
	});
	
	// Event handler(s)
	function onSpComboSelect(combo, record) {
		
		saveOrderBtn.disable();
		adjustDeliveriesBtn.disable();
		resetTravelTimeFields();
		
		editor.durationTime = '';
		editor.distanceMeters = '';
		editor.durationCorrFactor = 1;
		
		Ext.getDom('durationTime').value = 0;
		Ext.getDom('distanceMeters').value = 0;
		
		item.lpCombo.disable();
		item.lpCombo.clearValue();
		
		editor.biRailItemCombo.disable();
		editor.biRailItemCombo.clearValue();

		editor.commercialCarrierCombo.disable();
		var commercialCarrierValue = editor.commercialCarrierCombo.getValue();

		editor.executingCarrierCombo.disable();
		var executingCarrierValue = editor.executingCarrierCombo.getValue();

		editor.supplyPlantCombo.preselectValue(record.data.plantCd, true);

		loadingMessage = (isBimodalTruckPartOrder(editor.delTypeCombo.getValue())) ? '<fmt:message key="bestellung.indicator.loading.bimodal.rail.order.list"/>...' : '<fmt:message key="bestellung.indicator.refreshing.loadingpoint.list"/>...';
		
    	OrderAjaxService.getCollectedEditorData({
    		'editorIdx': <%=idx%>, 
			'orderItemIdx': <%=itemIdx%>,
    		'sapMatId': null, 
    		'supplyPlantCd': null, 
    		'prodPlantCd': item.prodPlantCombo.getValue(), 
    		'spId': (record.data.shippingpointId || -1), 
    		'poolId': null, 
    		'quantity': item.quantityStr.getValue(), 
    		'delTypeCd': editor.delTypeCombo.getValue(), 
    		'delFromDat': editor.getDelFromDateTime(), 
    		'delToDat': editor.getDelToDateTime(),
    		'trspZoneCd': trspZoneCd,
    		'shiptoSiloId': editor.siloCombo.getValue(),
    		'uomCd': null}, 
    	function(res) {

       	    // --- Price ---
			updateMaterialInfo(<%=idx%>, <%=itemIdx%>, res.orderItems[<%=itemIdx%>],
					{ skipOpenQuantity: true });

   			toggleRailDeliveryData(0, res.railData);
        	
        	if (isBimodalTruckPartOrder(editor.delTypeCombo.getValue())) {
	    		editor.biRailItemCombo.store.loadData(res.biRailItemList);
	    		editor.biRailItemCombo.enable();
	    		toggleBiRailItemCombo(<%=idx%>, true);
        	}
        	else {
	    		if (!isServiceOnlyMode(<%=idx%>)) {
	    			item.lpCombo.store.loadData(res.lpList);
	    			item.lpCombo.enable();
	    		}
	    		toggleBiRailItemCombo(<%=idx%>, false);
    		}

    		if (!isPickupOrder(editor.delTypeCombo.getValue()) && !isShipOrBlockTrainPickupOrder(editor.delTypeCombo.getValue())) { // No pool/carrier for pickup

	    		editor.commercialCarrierCombo.store.loadData(res.commercialCarrierList);
	    		editor.commercialCarrierCombo.preselectValue(commercialCarrierValue, true);
	    		editor.commercialCarrierCombo.enable();

	    		editor.executingCarrierCombo.store.loadData(res.executingCarrierList);
	    		editor.executingCarrierCombo.preselectValue(executingCarrierValue, true);
	    		editor.executingCarrierCombo.enable();

    		}

    		if (editor.delTypeCombo.getValue()) {
    			editor.durationCorrFactor = res.durationCorrectionFactor; 
				durationCorrFactor = res.durationCorrectionFactor; // FIXME should be handled per editor
				updateDurationTimeAndMap(record, editor.delTypeCombo.getValue(), res.railData.durationMinutes, res.railData.distanceMeters, res.durationCorrectionFactor); //
    		}
    		 					
			jsonData[<%=idx%>] = res;
    	});
	}
	editor.spCombo.on('select', onSpComboSelect); 
	
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
	    disabled: isEditorDisabled,
	    width: 130,
	    listWidth: 200,
	    valueField: 'loadingpointId',
	    displayField: 'loadingpointName'
	});

	// --- BIMODAL RAIL ORDER ITEM ---
    var biRailItemComboTpl = new Ext.XTemplate(
    	'<table class="classic"><thead><tr>',
			'<th><nobr><fmt:message key="bestellung.rail.order.list.wagon.no"/></nobr></th>',
    		'<th><nobr><fmt:message key="bestellung.rail.order.list.wagon.type"/></nobr></th>',
	    	'<th><nobr>#</nobr></th>',
	    	'<th><nobr><fmt:message key="bestellung.rail.order.list.wagon.remaining.load"/></nobr></th>',
	    	'<th><nobr><fmt:message key="bestellung.rail.order.list.wagon.arrival"/></nobr></th>',
	    	'<th><nobr><fmt:message key="bestellung.rail.order.list.order.no"/></nobr></th>',
	    	'<th><nobr><fmt:message key="bestellung.rail.order.list.sp"/></nobr></th>',
    	'</tr></thead><tbody>',
        '<tpl for=".">',
	        '<tpl if="orderItemId == -1">',
	    	'<tr class="listitem zebra{[xindex % 2]}">',
		        '<td colspan="7">...</td>',
	        '</tr>', 
	        '</tpl>',
	        '<tpl if="orderItemId != -1">',
		        '<tpl if="bimodalShiptoSareaRestrId != null">',
		            '<tr class="listitem" style="background-color: #CCE6FF">',
				        '<td colspan="7"><nobr><img src="' + crhCommonUrl('../images/silk/bullet_add.png') + '"/><fmt:message key="bestellung.rail.order.list.create.new.order"/> <b>{bimodalSoldtoName} / {bimodalShiptoName}</b></nobr></td>',
			        '</tr>', 
		        '</tpl>',
		        '<tpl if="bimodalShiptoSareaRestrId == null">',
		        	'<tr class="listitem zebra{[xindex % 2]}">',
				        '<td class="smallfont"><nobr>{wagonNo}</nobr></td>',
				        '<td class="smallfont"><nobr>{wagonType}</nobr></td>',
				        '<td class="smallfont"><nobr>{depTruckOrderDelivery}</nobr></td>',
				        '<td class="smallfont" style="text-align: right"><nobr>{remainingLoad}</nobr></td>',
				        '<td class="smallfont"><nobr>{actualDeliveryDat:this.formattedDate}</nobr></td>',
				        '<td class="smallfont"><nobr>{orderNumber} <span style="color: #888888">({orderItemNo})</span></nobr></td>',
				        '<td class="smallfont"><nobr>{shippingpointName}</nobr></td>',
			        '</tr>', 
		        '</tpl>',
	        '</tpl>',
        '</tpl>',
        '</tpl>',
        '</tbody></table>'
    );

    biRailItemComboTpl.formattedDate = function(value) {
        return value.format('d.m.y H:i');
    }

    bimodalRailPartActive = false; // FIXME

	editor.biRailItemCombo = new Ext.form.ComboBox({
		mode: 'local',
		editable: false,
		forceSelection: true,
		id: 'biRailItemCombo<%=idx%>',
		name: 'editorList[<%=idx%>].orderHelper.bimodalRailOrderItemId',
	    transform:'editorList[<%=idx%>].orderHelper.bimodalRailOrderItemId',
		allowBlank: true,
	    triggerAction: 'all',
	    store: new Ext.data.JsonStore({
			fields: [
				{ name: 'actualDeliveryDat', mapping: 'entity.actualDeliveryDat' },
				{ name: 'compositeId', mapping: 'entity.compositeId' },
				{ name: 'orderId', mapping: 'entity.orderId' },
				{ name: 'orderItemId', mapping: 'entity.orderItemId' },
				{ name: 'orderItemNo', mapping: 'entity.orderItemNo' },
				{ name: 'orderNumber', mapping: 'entity.orderNumber' },
				{ name: 'shippingpointName', mapping: 'entity.shippingpointName' },
				{ name: 'shiptoBpName', mapping: 'entity.shiptoBpName' },
				{ name: 'wagonNo', mapping: 'entity.wagonNo' },
				{ name: 'wagonType', mapping: 'entity.wagonType' },
				'bimodalShiptoName',
				'bimodalShiptoSareaRestrId',
				'bimodalSoldtoName',
				'bimodalSoldtoSapId',
				'depTruckOrderDelivery',
				'remainingLoad',
				'sapContractItemId'
			],
			emptyItem: {
				'entity': { 'orderNumber': '...' },
				'entity': { 'orderItemId': -1 }
			},
        	data : jsonData[<%=idx%>].biRailItemList 
    	}),
	    disabled: isEditorDisabled,
	    width: 130,
	    listWidth: 550,
	    listAlign: 'tr-br',
	    hidden: true,
	    valueField: 'orderItemId',
	    displayField: 'wagonNo',
	    tpl: biRailItemComboTpl,
        itemSelector: 'tr.listitem',
        onSelect: function(record) {
	        editor.biRailItemCombo.collapse();
	    	if (!Ext.isEmpty(record.data.bimodalShiptoSareaRestrId) && !bimodalRailPartActive) {
		    	
	    		Ext.get('divEditors').mask('<fmt:message key="bestellung.indicator.validating.bimodal.truck"/>...', 'x-mask-loading');
				saveOrderBtn.disable();
				adjustDeliveriesBtn.disable();
				
				// --- Append DIV with new editor ---
				bimodalRailPartActive = true;
				loadingMessage = '<fmt:message key="bestellung.indicator.validating.bimodal.truck"/>...';
				
				if (Ext.getDom('divOrderEditor1') == null) {
		    		Ext.DomHelper.insertAfter('divOrderEditor0', {tag: 'div', id: 'divOrderEditor1'});
				}

		    	params = {};
				addFormElements(params, 'bestellungForm1');
				addFormElements(params, 'bestellungForm2');
				addFormElements(params, 'bestellungForm3');
				
				params.action = 'addBimodalRailPart';
				params.bimodalShiptoSareaRestrId = record.data.bimodalShiptoSareaRestrId;
				params.bimodalSoldtoSapId = record.data.bimodalSoldtoSapId;

				var divBimodalRailPart = Ext.get('divOrderEditor1');
				
		    	this.clearValue();
		    	
				divBimodalRailPart.load({
					url: '../bestellung/Bestellung.do', 
					params: params,
					text: '',
					scripts: true,
					nocache: true
				});
		    	
	    	}
	    	else if (record.data.orderItemId > -2) {
		    	this.setValue(record.data.orderItemId);
	        }
	    }
	});
	
	// --- DELIVERY TYPE ---
	editor.delTypeCombo = new Ext.form.ComboBox({
		mode: 'local',
		editable: false,
		forceSelection: true,
		id: 'delTypeCombo<%=idx%>',
	    triggerAction: 'all',
	    transform:'editorList[<%=idx%>].orderHelper.deliveryTypeCd',
	    disabled: isEditorDisabled,
	    width: 100,
	    listWidth: 160,
    	store: new Ext.data.JsonStore({
        	fields: [
        		{name: 'deliveryTypeCd', mapping: 'deliveryTypeTxPK.deliveryTypeBs.deliveryTypeCd'}, 
        		{name: 'txt', mapping: 'txt'}],
		    data:[],		        		
        	emptyItem: {'txt': '...', 'deliveryTypeTxPK': {'deliveryTypeBs': {'deliveryTypeCd': '-1'}}}
    	}),    
	    valueField: 'deliveryTypeCd',
	    displayField: 'txt',
	    listeners: {
		    beforeSelect: function(combo, rec) {
				if (isRailOrder(rec.data.value) && !shiptoHasRailwayStation) {
					Logon.message.warning('<fmt:message key="bestellung.error.shipto.without.railway.station"/>');
					return false;
				}
			},
			select: function(combo, rec) {
				saveOrderBtn.disable();
				adjustDeliveriesBtn.disable();
				editor.supplyPlantCombo.clearValue();
				editor.spCombo.clearValue();
				item.lpCombo.clearValue();
				spIdToSend = -1;
				supplyPlantCdToSend = -1;

				if (editor.spCombo.isEmptySelection()) {

					editor.durationTime = '';
					editor.distanceMeters = '';
					
					Ext.getDom('durationTime').value = 0;
					Ext.getDom('distanceMeters').value = 0;
					
					logonGmap.loadMap();
					
				}
		
    			item.uomCombo.disable();
    			item.uomCombo.clearValue();
				editor.poolCombo.disable();
				editor.poolCombo.clearValue();
				editor.commercialCarrierCombo.disable();
				editor.commercialCarrierCombo.clearValue();
				editor.executingCarrierCombo.disable();
				editor.executingCarrierCombo.clearValue();
				editor.supplyPlantCombo.disable();
				editor.spCombo.disable();
				item.lpCombo.disable();
				editor.biRailItemCombo.disable();
				editor.truckTypeCombo.disable();
				editor.truckTypeCombo.updateDataAndMeta({});
		
		    	OrderAjaxService.getCollectedEditorData({
			    		'editorIdx': <%=idx%>, 
						'orderItemIdx': <%=itemIdx%>,
			    		'sapMatId': null, 
			    		'supplyPlantCd': supplyPlantCdToSend,
			    		'prodPlantCd': null, 
			    		'spId': spIdToSend, 
			    		'poolId': -1, 
			    		'quantity': item.quantityStr.getValue(), 
			    		'delTypeCd': rec.data.deliveryTypeCd,
			    		'delFromDat': editor.getDelFromDateTime(), 
			    		'delToDat': editor.getDelToDateTime(),
			    		'trspZoneCd': trspZoneCd,
			    		'shiptoSiloId': editor.siloCombo.getValue(),
			    		'uomCd': item.uomCombo.getValue()}, 
			    	function(res) {
		    			item.uomCombo.store.loadData(res.uomList);
		    			item.uomCombo.enable();
		    			item.uomCombo.preselectValue(res.orderItems[<%=itemIdx%>].uomCd, true);	
			    		
			    		editor.combinedCombo.initChecked('releaseSapFlag', res.releaseSapFlag);
			    		editor.combinedCombo.initChecked('freeDeliveryFlag', res.freeDeliveryFlag);
			    		// 28-Jan-2015/toan: add function to disable on hold order checkbox if the delivery type is pickup
			    		editor.combinedCombo.initChecked('onHoldOrderAndLockedFlag', res.onHoldOrderAndLockedFlag);

						editor.combinedCombo.setDisabled('releaseSapFlag', !res.orderItems[<%=itemIdx%>].deliverySetting.releaseSapFlagEditable);						
			   			editor.combinedCombo.setDisabled('freeDeliveryFlag',!res.orderItems[<%=itemIdx%>].deliverySetting.freeOfChargeEditable);
			   			// 28-Jan-2015/toan: add function to disable on hold order checkbox if the delivery type is pickup
			   			editor.combinedCombo.setDisabled('onHoldOrderAndLockedFlag',!res.orderItems[<%=itemIdx%>].deliverySetting.onHoldOrderAndLockedFlagEditable);

			   			editor.combinedCombo.setDisabled('autoReleaseFlag',!res.orderItems[<%=itemIdx%>].deliverySetting.autoReleaseFlagEditable);
			   			
						updateMaterialInfo(<%=idx%>, <%=itemIdx%>, res.orderItems[<%=itemIdx%>],
								{ skipOpenQuantity: true });
			
			    		editor.spCombo.store.loadData(res.spList);
			    		editor.spCombo.enable();
						
						// --- For rail order, hide the time selector ---
						toggleRailDeliveryData(0, res.railData);
			    		
			    		// --- Activate the rail editor ---
						if (isBimodalTruckPartOrder(rec.data.value)) {
							toggleBiRailItemCombo(<%=idx%>, true);
						}
						else {
				    		item.lpCombo.store.loadData(res.lpList);
				    		item.lpCombo.enable();
							toggleBiRailItemCombo(<%=idx%>, false);
						}

						// --- Pool and carrier: only for non-pickup ---
						if (!isPickupOrder(rec.data.deliveryTypeCd) && !isShipOrBlockTrainPickupOrder(rec.data.deliveryTypeCd)) {

							editor.poolCombo.store.loadData(res.poolList);
							editor.poolCombo.enable();

							editor.commercialCarrierCombo.store.loadData(res.commercialCarrierList);
							editor.commercialCarrierCombo.enable();

							editor.executingCarrierCombo.store.loadData(res.executingCarrierList);
							editor.executingCarrierCombo.enable();

						}

						// --- ShipTo Silo
						if (isReturnPickupOrder(rec.data.deliveryTypeCd)) {
							editor.siloCombo.disable();
						}
						else {
							editor.siloCombo.enable();
						}
						
						// --- Truck type combo for truck, pickup, return delivered and simple vehicles (ship and block-train)
						if (isSpecialEditorNeeded(res.delTypeCd)) {
							editor.truckTypeCombo.updateDataAndMeta(res.vehicleTypeWrapperList);
							editor.truckTypeCombo.enable();
							editor.truckTypeCombo.selectAll();

							setSpecialEditorActive(<%=idx%>, <%=itemIdx%>, true);
						}
						else {
							editor.truckTypeCombo.updateDataAndMeta({});
							editor.truckTypeCombo.disable();
							setSpecialEditorActive(<%=idx%>, <%=itemIdx%>, false);
						}
			
			    		editor.supplyPlantCombo.store.loadData(res.supplyPlantList);
				    	editor.supplyPlantCombo.enable();
						jsonData[<%=idx%>] = res;
			    	}
			    );
			
				document.getElementById('editorList[<%=idx%>].autoPoolAssignmentFlag').disabled = true;
				document.getElementById('editorList[<%=idx%>].autoPoolAssignmentFlag').checked = false;
				document.getElementById('editorList[<%=idx%>].autoCarrierAssignmentFlag').disabled = true;
				document.getElementById('editorList[<%=idx%>].autoCarrierAssignmentFlag').checked = false;

				if (rec.data.deliveryTypeCd == <%=Constant.DELIVERY_TYPE_BLOCKTRAIN_PICKUP%> 
					|| rec.data.deliveryTypeCd == <%=Constant.DELIVERY_TYPE_SHIP_PICKUP%>){

						editor.poolCombo.disable();
						editor.commercialCarrierCombo.disable();
						editor.executingCarrierCombo.disable();

						refreshChartArea({
							'selectDelTypeCd': rec.data.deliveryTypeCd,
							'isOnCallCenter': 'true',
							'isLandfill' : isLandfillOnlyMode(editor)
						});

				}
				
				combo.prevValue = rec.data.value;
				adjustDeliveriesBtn.update();
				addOrderItemBtn.enable();
				addServiceOrderItemBtn.enable();
			}
		}
	});

	// --- SILO ALLOCATION ---
	editor.siloCombo = new Ext.form.ComboBox({
		mode: 'local',
		editable: false,
		forceSelection: true,
		id: 'siloCombo<%=idx%>',
	    name: 'editorList[<%=idx%>].orderHelper.shiptoSiloId',
	    transform: 'editorList[<%=idx%>].orderHelper.shiptoSiloId',
	    triggerAction: 'all',
	    store: new Ext.data.JsonStore({
        	fields: ['shiptoSiloId', 'siloName'],
		    emptyItem: {'siloName' : '...'},
			data: <c:out value="${form.json['siloList']}" escapeXml="false"/>			
    	}),
	    disabled: isEditorDisabled,
	    width: 100,
	    listWidth: 160,
	    displayField: 'siloName',
	    valueField: 'shiptoSiloId'
	});
	
	editor.siloCombo.on('select', shiptoSiloSelectFn);

	// --- UoM --- 
	item.uomCombo = new Ext.form.ComboBox({
		mode: 'local',
		editable: false,
		forceSelection: true,
		id: '<%=itemStr%>.uomCombo',
	    triggerAction: 'all',
	    store: new Ext.data.JsonStore({
        	fields: [
        		{name: 'uomCd', mapping: 'uomTxPK.uomBs.uomCd'}, 
        		{name: 'txt', mapping: 'txt'},
        		{name: 'fuzzy', mapping: 'uomTxPK.uomBs.fuzzyQuantityFlag'}
        	],
        	emptyItem: {'txt': '...', 'uomTxPK': {'uomBs': {'uomCd': '-1', 'fuzzy': false}}},
        	data : jsonData[<%=idx%>].uomList,
        	listeners: {
        		load: function (store, records) {
        			var editor = editorList[<%=idx%>];
        			if (records && editor && editor.deliveryFqyQtyPerHreUomCombo) {
        				var toLoad = [];
        				for (var i = 0; i < records.length; i++) {
        					var rec = records[i];
        					if (rec && rec.json) {
	        					toLoad.push(rec.json);
        					}
        				}
               			editor.deliveryFqyQtyPerHreUomCombo.clearValue();
               			editor.deliveryFqyQtyPerHreUomCombo.store.removeAll();
               			editor.deliveryFqyQtyPerHreUomCombo.store.loadData(toLoad);
        			}
        		}
        	}
    	}),	    
	    transform: '<%=itemStr%>.uomCd',
	    disabled: isEditorDisabled,
	    valueField: 'uomCd',
	    displayField: 'txt',	    
	    width: 75,
	    listeners: {
		    select: function(combo, rec) {
		    	if (rec.data.fuzzy) {
		    		editor.truckTypeCombo.updateMaxCapacity();
		    		editor.quantityPerSdoStr.disable();
		    	}
		    	else {
		    		if (!isServiceOnlyMode(<%=idx%>) ) {
		    			editor.quantityPerSdoStr.enable();
		    		}
		    	}
		    	
		    	setExactQuantityFlags(<%=idx%>, <%=itemIdx%>);
		    	
		    	if (rec.data.uomCd == 'P40' || rec.data.uomCd == 'P48') { // TODO constant
			    	editorList[0].siloCombo.clearValue();
			    	editorList[0].siloCombo.disable();
			    	checkQuantityOfPalletsFn();
		    	}
		    	else {
		    		if (isShiptoSiloToEnable(<%=idx%>)) {
				    	editorList[0].siloCombo.enable();
		    		}
		    		else {
		    			editorList[0].siloCombo.disable();
		    		}
		    	}
				addOrderItemBtn.enable();
				addServiceOrderItemBtn.enable();		   
	    	}
	    },
	    isFuzzy: function() {
	    	if (this.getSelectedRecord() && this.getSelectedRecord().data && this.getSelectedRecord().data.fuzzy) {
	    		return this.getSelectedRecord().data.fuzzy;
	    	}
	    	else {
	    		return false;
	    	}
	    }
	});

	// --- POOL ---
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
	    disabled: isEditorDisabled,
	    valueField: 'poolId',
	    displayField: 'name',
	    width: 130,
	    listWidth: 250
	});
	
	editor.poolCombo.on('select', function(combo, record) {

		saveOrderBtn.disable();
		adjustDeliveriesBtn.disable();

		editor.commercialCarrierCombo.disable();
		editor.commercialCarrierCombo.clearValue();

		editor.executingCarrierCombo.disable();
		editor.executingCarrierCombo.clearValue();

    	OrderAjaxService.getCollectedEditorData({
    		'editorIdx': <%=idx%>, 
			'orderItemIdx': <%=itemIdx%>,
    		'sapMatId': null, 
    		'supplyPlantCd': null,
    		'prodPlantCd': null,
    		'spId': null, 
    		'poolId': (record.data.poolId || -1), 
    		'quantity': item.quantityStr.getValue(), 
    		'delFromDat': editor.getDelFromDateTime(), 
    		'delToDat': editor.getDelToDateTime(), 
    		'delTypeCd': editor.delTypeCombo.getValue(),
    		'trspZoneCd': trspZoneCd,
    		'shiptoSiloId': editor.siloCombo.getValue(),
    		'uomCd': null}, 
    	function(res) {

			editor.commercialCarrierCombo.store.loadData(res.commercialCarrierList);
			editor.commercialCarrierCombo.enable();

			editor.executingCarrierCombo.store.loadData(res.executingCarrierList);
			editor.executingCarrierCombo.enable();

			jsonData[<%=idx%>] = res;

			updateAutoFlags(record, <%=idx%>);

			saveOrderBtn.update();
			adjustDeliveriesBtn.update();

		});
	});

	// --- COMMERCIAL CARRIER ---
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
        	fields: ['carrierId', 'displayName', 'carrierRequiresPlanningFlag'],
		    emptyItem: {'displayName' : '...'},
        	data : jsonData[<%=idx%>].commercialCarrierList 
    	}),
	    disabled: isEditorDisabled,
	    valueField: 'carrierId',
	    displayField: 'displayName',
	    width: 130,
	    listWidth: 250
	});

	editor.commercialCarrierCombo.on('select', function(combo, record) {

		updateAutoFlags(record, <%=idx%>);

		// --- Update chart area ---
		refreshChartArea({
			'selectCarrier': record.data.carrierId,
			'isOnCallCenter': 'true',
			'isLandfill' : isLandfillOnlyMode(editor)
		});

		editor.executingCarrierCombo.disable();
		editor.executingCarrierCombo.clearValue();

		OrderAjaxService.getCollectedEditorData({
			'editorIdx': <%=idx%>, 
			'orderItemIdx': <%=itemIdx%>,
			'sapMatId': null, 
			'supplyPlantCd': null,
			'prodPlantCd': null,
			'spId': null, 
			'commercialCarrierId': (record.data.carrierId || -1), 
			'quantity': item.quantityStr.getValue(), 
			'delFromDat': editor.getDelFromDateTime(), 
			'delToDat': editor.getDelToDateTime(), 
			'delTypeCd': editor.delTypeCombo.getValue(),
			'trspZoneCd': trspZoneCd,
			'shiptoSiloId': editor.siloCombo.getValue(),
			'uomCd': null}, 
		function(res) {

			editor.executingCarrierCombo.store.loadData(res.executingCarrierList);
			editor.executingCarrierCombo.enable();

			jsonData[<%=idx%>] = res;

			saveOrderBtn.update();
			adjustDeliveriesBtn.update();

		});

	});

	// --- EXECUTING CARRIER ---
	editor.executingCarrierCombo = new Ext.form.ComboBox({
		mode: 'local',
		forceSelection: true,
		id: 'executingCarrierCombo<%=idx%>',
		name: 'editorList[<%=idx%>].orderHelper.execCarrierId',
	    transform:'editorList[<%=idx%>].orderHelper.execCarrierId',
		allowBlank: true,
		selectOnFocus: true,
		editable: true,
	    minChars: 2,
	    triggerAction: 'all',
	    store: new Ext.data.JsonStore({
        	fields: ['carrierId', 'displayName', 'carrierRequiresPlanningFlag'],
		    emptyItem: {'displayName' : '...'},
        	data : jsonData[<%=idx%>].executingCarrierList 
    	}),
	    disabled: isEditorDisabled,
	    valueField: 'carrierId',
	    displayField: 'displayName',
	    width: 130,
	    listWidth: 250
	});

	editor.combinedCombo = new Ext.ux.CheckComboBox({
	    transform:'editorList[<%=idx%>].checkboxCombo',
	    store: new Ext.data.SimpleStore({
		    fields: [ 'txt', 'type', 'checked'],
		    data : [ 
	['<fmt:message key="dispokarte.texte.freeOfCharge"/>', 'freeDeliveryFlag',false],
	['<fmt:message key="dispokarte.texte.releaseSAP"/>','releaseSapFlag',true],
	['<fmt:message key="dispokarte.texte.onHoldOrderAndLockedFlag"/>','onHoldOrderAndLockedFlag',false],
	['<fmt:message key="dispokarte.texte.suppressMinChargeSapFlag"/>','sapSuppressMinChargeFlag',false],
	['<fmt:message key="dispokarte.texte.openCustorderFlag"/>','openCustorderFlag',false],
	['<fmt:message key="dispokarte.texte.title.automaticRelease"/>','autoReleaseFlag',false]	
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
	    width: 130,
	    listWidth: 200,
	    listeners: {
		    change: function(combo, rec) {
		    	var params = {
		    		'editorIdx': <%=idx%>, 
					'orderItemIdx': <%=itemIdx%>,
		    		'sapMatId': null, 
		    		'supplyPlantCd': null, 
		    		'prodPlantCd': null, 
		    		'spId': null, 
		    		'poolId': null, 
		    		'quantity': null, 
		    		'delTypeCd': null,
		    		'delFromDat': null,
		    		'delToDat': null,
		    		'trspZoneCd': null,
		    		'uomCd': null
		    		};
		    	
		    	var checkedFlags = combo.getCheckedValuesAsParams();
				for (prop in checkedFlags) { 
					params[prop] = checkedFlags[prop];
				}
			    OrderAjaxService.getCollectedEditorData(params, 
			    	function(res) {
			    			editorList[0].combinedCombo.setDisabled('autoReleaseFlag',!res.orderItems[<%=itemIdx%>].deliverySetting.autoReleaseFlagEditable);
							editorList[0].combinedCombo.initChecked('autoReleaseFlag',res.orderItems[<%=itemIdx%>].deliverySetting.autoReleaseFlag);    	
			    	});

	        	addOrderItemBtn.enable();
				addServiceOrderItemBtn.enable();
			}		
		}
	});
	
	editor.combinedCombo.initList();
	
	// --- BILL-TO: The combobox is not rendered for Dummy ship-to since there is no bill-to list ---
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
        		{name: 'name1', mapping: 'billtoSapBp.name1', convert: function(value, rec) {return Ext.util.Format.htmlDecode(value);}},
        		{name: 'sapBpId', mapping: 'billtoSapBp.sapBpId'},
        		{name: 'name2', mapping: 'billtoSapBp.name2', convert: function(value, rec) {return Ext.util.Format.htmlDecode(value);}},
        		{name: 'sapId', mapping: 'billtoSapBp.sapId'}
        	],
        	emptyItem: {'billtoSapBp' : {'name1' : '...', 'name2' : '', 'sapId' : '-'}},
        	data : jsonData[<%=idx%>].billtoList
    	}),
	    disabled: isEditorDisabled,
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
	    listWidth: 250,
	    listAlign: 'tr-br'
	});

	// --- PAYER (not rendered for Dummy Ship-To either to make it consistent to Bill-To) ---
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
				{ name: 'name1', mapping: 'name1', convert: function(value, rec) {return Ext.util.Format.htmlDecode(value);}},
				{ name: 'name2', mapping: 'name2', convert: function(value, rec) {return Ext.util.Format.htmlDecode(value);}},
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
		disabled: isEditorDisabled,
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
		listWidth: 250,
		listAlign: 'tr-br'
	});

	// --- SP Indicator
	editor.serviceProcessIndicatorList = new Ext.form.ComboBox({
		mode: 'local',
		editable: false,
		forceSelection: true,
		id: 'serviceProcessIndicatorCombo<%=idx%>',
		name: 'editorList[<%=idx%>].serviceProcessIndicatorId',
		transform:'editorList[<%=idx%>].serviceProcessIndicatorId',
		allowBlank: true,
		triggerAction: 'all',
		store: new Ext.data.JsonStore({
			data : jsonData[<%=idx%>].serviceProcessIndicatorList,
			fields: [
        		{
        			name: 'name2', mapping: 'txt', 
        			convert: function(value, rec) {
			        			var returnValue = value;
			        			if (value.orderSpiBs != undefined) {
			        				returnValue = value.orderSpiBs.orderSpiTxt;
			        			}
			        			else{
			        				if (returnValue == ""){
			        					returnValue = "-";
			        				}
			        			}
			        			return Ext.util.Format.htmlDecode(returnValue);
        			}
        		},
				{ 
        			name: 'serviceProcessIndicatorId', mapping: 'orderSpiTxPK', 
        			convert: function(value, rec) {
			        			var returnValue = value;
			        			if (value.orderSpiBs != undefined) {        			
			        				returnValue = value.orderSpiBs.orderSpiCd;
			        			}
			        			else{
			        				if (returnValue == ""){
			        					returnValue = "-1";
			        				}
			        			}
			        			return Ext.util.Format.htmlDecode(returnValue);
					}
        		}				
			],
			emptyItem: {
				'name2' : '...&nbsp;',
				'serviceProcessIndicatorId' : '-1'
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
	
	editorList.push(editor);
	
	// -- Update visibility of special editor fields
	toggleDeliveryFqyCustorderMode(<%=idx%>);
	if (isSpecialEditorNeeded(editor.delTypeCombo.getValue())) {
		setSpecialEditorActive(<%=idx%>, <%=itemIdx%>, true);
		if (editor.deliveryFqyEveryTimeStr.getValue() || editor.deliveryFqyQtyPerHreStr.getValue()) {
			setPerformanceOrderActive(editor, item, <%=idx%>, <%=itemIdx%>, true);
		}
	}
	else {
		setSpecialEditorActive(<%=idx%>, <%=itemIdx%>, false);
		setPerformanceOrderActive(editor, item, <%=idx%>, <%=itemIdx%>, false);
	}
	
	// If we have a _multiple_ _bulk_ editors we need masking capabilities
	
	var div = Ext.get('divOrderEditor' + <%=idx%>);
    
	div.addListener("click", function (evt, el, o) {
		
		if (editorList.length <= 1) {

			// If there is only one editor we make sure the first and only editor is unmasked
			// Can happen after deletion also
			selectedEditorIdx = 0;
			unMaskEditor(selectedEditorIdx);
			
			// We should not go on if only one editor is available
			return;
		}
		
		// When clicking on already selected editor we do nothing
		if (<%=idx%> == selectedEditorIdx) {
			return;
		}
		
		var newSelectedEditorIdx = parseInt(this.dom.id.substr('divOrderEditor'.length));
		
		applyMaskingForEditors(newSelectedEditorIdx);
		
		// After successful change we save the selected editor index
		selectedEditorIdx = newSelectedEditorIdx;		
		
		// Update the google map and chart based on the selected editor
		// ------------------------------------------------------------
		loadingMessage = '';
		
		Ext.getDom('cchDrivetoTime').value = '';
		Ext.getDom('cchReturnTime').value = '';
		Ext.getDom('cchCycleTime').value = '';
		
		editor.durationTime = '';
		editor.distanceMeters = '';
		
		Ext.getDom('durationTime').value = 0;
		Ext.getDom('distanceMeters').value = 0;
		
		editor.durationCorrFactor = 1; 
		
		var record = editor.spCombo.getSelectedRecord();
		
		if (record == null) {
			return;
		}
		
		var shippingpointId = record.data ? record.data.shippingpointId : -1;
		
    	OrderAjaxService.getCollectedEditorData({
    		'editorIdx': selectedEditorIdx, 
			'orderItemIdx': <%=itemIdx%>,
    		'sapMatId': null, 
    		'supplyPlantCd': null, 
    		'prodPlantCd': item.prodPlantCombo.getValue(), 
    		'spId': shippingpointId, 
    		'poolId': null, 
    		'quantity': item.quantityStr.getValue(), 
    		'delTypeCd': editor.delTypeCombo.getValue(), 
    		'delFromDat': editor.getDelFromDateTime(), 
    		'delToDat': editor.getDelToDateTime(),
    		'trspZoneCd': trspZoneCd,
    		'shiptoSiloId': editor.siloCombo.getValue(),
    		'uomCd': null}, 
    	function(res) {

    		if (editor.delTypeCombo.getValue()) {
    			editor.durationCorrFactor = res.durationCorrectionFactor; 
				durationCorrFactor = res.durationCorrectionFactor;
				updateDurationTimeAndMap(record, editor.delTypeCombo.getValue(), res.railData.durationMinutes, res.railData.distanceMeters, res.durationCorrectionFactor); //
    		}
    		
    		var commercialCarrierId = editor.commercialCarrierCombo.getValue();
    		
    		var values = carrierCheckCombo.getCheckedValues();
    		
    		// We should only update the chart if the carrier is different
    		if (values.length > 1 || values.indexOf(''+commercialCarrierId) == -1) {
	    		refreshChartArea({
					'selectCarrier': commercialCarrierId,
					'isOnCallCenter': 'true',
					'isLandfill' : isLandfillOnlyMode(editor)
				});
    		}
    		
			jsonData[selectedEditorIdx] = res;
			
    	});
		
		adjustDeliveriesBtn.update();
		saveOrderBtn.update();
		
	});
	
}); // End Ext.onReady

</script>

<html:form action="bestellung/Bestellung.do" styleId="bestellungForm2PerEditor${idx}">

<%-- NEW HEADER --%>
<table class="classicblue" style="width:1250px; border-bottom: 0px; border-left: 0px; border-right: 0px;">
	<tr>
		<th rowspan="2" style="width: 22px;">
			&nbsp;
		</th>
		<th rowspan="2" style="width: 150px">
			<fmt:message key="fields.produkt.produkt"/>
			</br>
			<fmt:message key="fields.order.materialfilter"/>
			</br>
			<fmt:message key="fields.konditionen.produktionswerk"/>
			</br>
			<fmt:message key="fields.order.mixdesignid"/>
		</th>
		<th rowspan="2" style="width: 100px">
			<fmt:message key="bestellung.orderingPerson"/>
			</br>
			<fmt:message key="fields.order.custrefno"/>
			</br>
			<fmt:message key="fields.order.openqty"/>
		</th>
		<th rowspan="2" style="width: 100px">
			<fmt:message key="fields.konditionen.liefertyp"/>
			</br>
			<fmt:message key="fields.order.vehicletype"/>
			</br>
			<fmt:message key="fields.defaults.kundensilo"/>
		</th>
		<th rowspan="2" style="width: 200px">
			<fmt:message key="fields.order.orderquantity"/>
			</br>
			<fmt:message key="fields.order.qtyperdelivery"/>
			</br>
			<fmt:message key="fields.order.deliveryfrequency"/>
		</th>
		<th rowspan="2" style="width: 180x">
			<fmt:message key="fields.order.deliveryfrom_datetime"/>
			<c:if test="${displayShipToTimezone}">
				<c:out value="${shipToTimeZoneFmtOffset}"/>
			</c:if>
			</br>
			<fmt:message key="fields.order.deliveryto_datetime"/>
			<c:if test="${displayShipToTimezone}">
				<c:out value="${shipToTimeZoneFmtOffset}"/>
			</c:if>
			</br>
			<fmt:message key="fields.order.tolerance_numdeliveries"/>
		</th>
		<th rowspan="2" style="width: 150px">
			<fmt:message key="fields.konditionen.lieferwerk"/>
			</br>
			<fmt:message key="fields.defaults.versandstelle"/>
			</br>
			<fmt:message key="fields.defaults.ladestrasse"/>
		</th>
		<th style="width: 150px">
			<fmt:message key="fields.pool.pool"/>
			<br/>
			<fmt:message key="fields.carrier.commercial"/>
			<br/>
			<fmt:message key="fields.carrier.executing"/>
		</th>
		<th style="width: 100px">
			<fmt:message key="fields.billto.billto"/>
			<br/>
			<fmt:message key="fields.payer.payer"/>
		</th>
	</tr>
	<tr>
		<th colspan="2" style="text-align: center">
	 		<fmt:message key="fields.order.specorderindicators"/>
	 	</th>
	</tr>
</table>

<table style="width:1246px; border-spacing: 0px; margin-left: 2px; margin-right: 2px; margin-bottom: 2px;" cellspacing="0">

    <!-- NEW(ER) ORDER FORM BEGIN -->
	<tr style="background-color: #DDDDDD">
	
		<td style="width:170px;">
			<table cellpadding="0" cellspacing="0" style="padding: 0px; margin: 0px; border-spacing: 0px; empty-cells:show;">
           		<tr>
           			<!-- We only put the delete button if the idx != 0 => which means it's not the first order -->
					<td style="width: 22px;" rowspan="4">
						<c:if test="${idx==0}">
							<span style="width: 22px;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
						</c:if>
						<c:if test="${idx!=0}">
							<a href="#" onclick="removeBulkOrderEditor()"><img title="Remove order" src="<crh-common:url value='/images/silk/basket_delete.png'/>" style="margin: 3px 0px 0px 3px"/></a>
						</c:if>
					</td>
           			<td  colspan="2">
           				<!-- Material -->
		            	<html:hidden property="editorList[${idx}].orderItems[${itemIdx}].sapMatId" styleId="editorList[${idx}].orderItems[${itemIdx}].sapMatId"/>
           			</td>
           			<td>
           				<!-- Material Spinner -->
           				<img id="sapMatSpinner<%=itemIdx%>" src="<crh-common:url value='/images/gray/s.gif'/>" style="width: 16px; margin-top: 3px; margin-left: 3px">
           			</td>
           		</tr>
           		<tr>
           		<td colspan=2>
	           		<table cellpadding="0" cellspacing="0" style="padding: 0px; margin:0px; border-spacing: 0px">
						<td width="20px" title="<fmt:message key="dispokarte.texte.title.serviceonlyorder"/>">
	           				<!-- Service Only Order Flag -->
	           				<input type="checkbox" value="true" name="editorList[<%=idx%>].serviceOnlyOrderFlag" id="editorList[<%=idx%>].serviceOnlyOrderFlag"/>
	           			</td>
	           			<td style="vertical-align: middle">
	           				<fmt:message key="dispokarte.texte.label.serviceonlyorder"/>
	           			</td>
           				<c:if test="${idx==0}">
           					<td width="20px" title="<fmt:message key="dispokarte.texte.title.futurecontracts"/>">
	           				<!-- Service Only Order Flag -->
	           					<input type="checkbox" value="true" name="allowFutureContracts" id="allowFutureContracts"/>
	           				</td>
           					<td style="vertical-align: middle">
	           					<fmt:message key="dispokarte.texte.label.futurecontracts"/>
	           				</td>
           				</c:if>
						<c:if test="${idx!=0}">
							<td width="20px" title="<fmt:message key="dispokarte.texte.title.futurecontracts"/>">
	           					<span style="width: 20px;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
		           			</td>
						</c:if>
           			</table>
           		</td>
           		</tr>
           		<tr>
           			<td colspan="2">
						<!-- Production Plant -->
						<html:hidden property="editorList[${idx}].orderItems[${itemIdx}].prodPlantCd" styleId="editorList[${idx}].orderItems[${itemIdx}].prodPlantCd"/>
					</td>
           		</tr>
           		<tr>
           			<td colspan="2">
            			<!-- Mix Design -->
						<html:hidden property="editorList[${idx}].orderItems[${itemIdx}].mixDesignId" styleId="editorList[${idx}].orderItems[${itemIdx}].mixDesignId"/>
            		</td>
           		</tr>
           	</table>
        </td>
           
        <td style="width:100px;">
           	<table cellpadding="0" cellspacing="0" style="padding: 0px; margin:0px; border-spacing: 0px">
           		<tr>
           			<td colspan="3">
            			<!-- Contact Person -->
            			<html:text property="editorList[${idx}].order.contactPersonTxt" styleId="editorList[${idx}].order.contactPersonTxt" style="width: 100px"/>
           			</td>
           		</tr>
           		<tr>
           			<td colspan="3">
           				<!-- Custorder Reference No. -->
           				<html:text property="editorList[${idx}].custorderCustrefNo" styleId="editorList[${idx}].custorderCustrefNo"/>
           			</td>
           		</tr>
           		<tr>
           			<td>
           				<!-- Open Quantity -->
						<div id="editorList[<%=idx%>].orderItems[<%=itemIdx%>].label.openQuantity" class="dynlabel" style="text-align: right">
							<c:choose>
								<c:when test="${not empty item.openQuantity}">
									<c:out value="${item.openQuantity}"/>
								</c:when>
								<c:otherwise>
									<%= BestellungForm.NO_OPEN_QUANTITY %>
								</c:otherwise>
							</c:choose>
						</div>
           			</td>
           		</tr>
           	</table>
           </td>
           
           <td style="width:100px;">
           	<table cellpadding="0" cellspacing="0" style="padding: 0px; margin: 0px; border-spacing: 0px">
           		<tr>
           			<td>
           			    <!-- Delivery Type -->
		                <div class="marginbottom">
							<c:set var="stTypeCd" value="${form.shipTo.shiptoType.shiptoTypeCd}"/>
							<c:set var="pickupPossible" value="${form.shipTo.selfServiceFlag}"/>
           					<html:hidden property="editorList[${idx}].orderHelper.deliveryTypeCd" styleId="editorList[${idx}].orderHelper.deliveryTypeCd"/>								
						</div>
           			</td>
           		</tr>
           		<tr>
           			<td>
           				<!-- Vehicle Type -->
						<html:text property="editorList[${idx}].truckTypes" styleId="editorList[${idx}].truckTypes"/>
           				<html:hidden property="editorList[${idx}].selectedTruckTypes" styleId="editorList[${idx}].selectedTruckTypes"/>
           			</td>
           		</tr>
           		<tr>
           			<td>
           			    <!-- ShipTo Silo -->
           				<html:hidden property="editorList[${idx}].orderHelper.shiptoSiloId" styleId="editorList[${idx}].orderHelper.shiptoSiloId"/>
           			</td>
           		</tr>
           	</table>
           </td>
           
           <td style="width:200px; padding: 0px; margin:0px; border-spacing: 0px">
           	<table cellpadding="0" cellspacing="0" style="padding: 0px; margin:0px; border-spacing: 0px">
           		<tr>
           			<td width="20px">
           				<!-- Ordered Quantity Edit Mode Switch (Pencil Icon) -->
           				<a title="<fmt:message key="dispokarte.texte.switchCustomerOrderMode"/>" id="orderedQuantityEditModeSwitch<%=idx%>" name="orderedQuantityEditModeSwitch<%=idx%>" style="visibility: hidden;" href="javascript:onOrderedQuantityEditModeSwitch(<%=idx%>, <%=itemIdx%>)">
	            			<img width="16px" height="16px" src="<crh-common:url value='/images/silk/pencil.png'/>"/>
	            		</a>
	            		<!-- Performance Order Calculation Mode -->
	            		<html:hidden property="editorList[${idx}].performanceOrderCalculationMode" styleId="editorList[${idx}].performanceOrderCalculationMode"/>
           			</td>
           			<td width="45px">
           			    <!-- Order Quantity -->
           				<html:text property="editorList[${idx}].orderItems[${itemIdx}].quantityStr" styleId="editorList[${idx}].orderItems[${itemIdx}].quantityStr"/>
           			</td>
           			<td width="115px">
           				<!-- Ordered UoM -->
						<html:hidden property="editorList[${idx}].orderItems[${itemIdx}].uomCd" styleId="editorList[${idx}].orderItems[${itemIdx}].uomCd"/>
           			</td>
           			<td width="20px" title="<fmt:message key="dispokarte.texte.exactCustorderQtyFlag"/>">
           				<!-- Exact Customer Order Flag -->
           				<input type="checkbox" value="true" name="editorList[<%=idx%>].exactCustorderQtyFlag" id="editorList[<%=idx%>].exactCustorderQtyFlag"/>
           			</td>
           		</tr>
           		<tr>
           			<td width="20px">
           			</td>
           			<td width="45px">
           		        <!-- Quantity Per Delivery -->
           				<html:text titleKey="dispokarte.texte.perSdorderQuantity" property="editorList[${idx}].quantityPerSdoStr" styleId="editorList[${idx}].quantityPerSdoStr"/>
           			</td>
           			<td width="115px" style="text-align: left; vertical-align: middle;">
           				<!-- UoM Per Delivery Label -->
           				<span id="editorList[<%=idx%>].uomPerDeliveryText" styleId="editorList[${idx}].uomPerDeliveryText"/>
           				<html:hidden property="editorList[${idx}].quantityPerSdoUomCd" styleId="editorList[${idx}].quantityPerSdoUomCd"/>     				
           			</td>
           			<td width="20px" title="<fmt:message key="dispokarte.texte.exactSdorderQtyFlag"/>">
           			    <!-- Exact Quantity Per Delivery Flag -->
           				<input type="checkbox" value="true" name="editorList[<%=idx%>].exactQtyPerSdoFlag" id="editorList[<%=idx%>].exactQtyPerSdoFlag"/>
           			</td>
           		</tr>
           		<tr>
           			<td colspan="4">
           				<table cellpadding="0" cellspacing="0" style="padding: 0px; margin:0px; border-spacing: 0px">
            				<!-- Delivery Frequency Every Time -->
            				<tr id="deliveryFqyEveryTimeDiv<%=idx%>" style="display: inline;">
            					<td width="20px">
            						<!-- Delivery Frequency Mode Selection -->
            						<a id="deliveryFqyCustorderModeSwitchEveryTime<%=idx%>" name="deliveryFqyCustorderModeSwitchEveryTime<%=idx%>" title="<fmt:message key="dispokarte.texte.switchCustomerOrderMode"/>" href="javascript:toggleDeliveryFqyCustorderMode(<%=idx%>)">
            							<img width="16px" height="16px" src="<crh-common:url value='/images/special_customer_order_button.png'/>"/>
            						</a>
            					</td>
								<td width="45px" style="vertical-align: middle">
									<!--Delivert Frequency Every Time Label -->
									<fmt:message key="dispokarte.texte.title.every"/>
								</td>
								<td width="35px" title="<fmt:message key="dispokarte.texte.deliveryFrequency"/>">
									<!-- Delivery Frequency Every Time -->
            						<html:text property="editorList[${idx}].deliveryFqyEveryTimeStr" styleId="editorList[${idx}].deliveryFqyEveryTimeStr"/>
								</td>
								<td width="100px">
									<!-- Delivery Frequency Every Time UoM -->
            						<html:hidden property="editorList[${idx}].deliveryFqyEveryTimeUomCd" styleId="editorList[${idx}].deliveryFqyEveryTimeUomCd"/>
								</td>
							</tr>
	            			<!-- Delivery Frequency Quantity Per Hour -->
           					<tr id="deliveryFqyQtyPerHreDiv<%=idx%>" style="display: inline;">
           						<td width="20px">
           							<!-- Delivery Frequency Mode Selection -->
            						<a id="deliveryFqyCustorderModeSwitchQtyPerHre<%=idx%>" name="deliveryFqyCustorderModeSwitchQtyPerHre<%=idx%>" title="<fmt:message key="dispokarte.texte.switchCustomerOrderMode"/>" href="javascript:toggleDeliveryFqyCustorderMode(<%=idx%>)">
            							<img width="16px" height="16px" src="<crh-common:url value='/images/special_customer_order_button.png'/>"/>
            						</a>
            					</td>
								<td width="45px" title="<fmt:message key="dispokarte.texte.title.deliveryFreqQtyperhre"/>">
		            			    <!-- Delivery Frequency Quantity Per Hour -->
		            				<html:text property="editorList[${idx}].deliveryFqyQtyPerHreStr" styleId="editorList[${idx}].deliveryFqyQtyPerHreStr"/>
								</td>
								<td width="75px" title="<fmt:message key="dispokarte.texte.title.deliveryFreqQtyperhreUom"/>">
		            			    <!-- Delivery Frequency Quantity Per Hour UoM -->
		            				<html:hidden property="editorList[${idx}].deliveryFqyQtyPerHreUomCd" styleId="editorList[${idx}].deliveryFqyQtyPerHreUomCd"/>
								</td>
								<td width="60px" style="vertical-align: middle">
		            				<!-- Delivert Frequency Quantity Per Hour Label -->
		            				<fmt:message key="dispokarte.texte.title.perhour"/>
								</td>
							</tr>
            			</table>
           			</td>
           		</tr>
           	</table>
           </td>
           
           <td style="width:200px;">
           	<table cellpadding="0" cellspacing="0" style="padding: 0px; margin:0px; border-spacing: 0px">
           		<tr>
           			<td width="15px">
           			</td>
           			<td width="100px">
           				<!-- Delivery From Date -->
           				<html:text property="editorList[${idx}].deliveryFromDatStr" styleId="deliveryFromDatStr[${idx}]"/>
           			</td>
           			<td width="80px">
           				<!-- Delivery From Time -->
           				<html:text property="editorList[${idx}].deliveryFromTime" styleId="deliveryFromTime[${idx}]"/>
           			</td>
           		</tr>
           		<tr>
           			<td width="15px">
           				<!-- Delivery To Date Edit Mode Switch (Pencil Icon) -->
           				<a id="deliveryToDatEditModeSwitch<%=idx%>" name="deliveryToDatEditModeSwitch<%=idx%>" style="visibility: hidden;" href="javascript:onDeliveryToDatEditModeSwitch(<%=idx%>, <%=itemIdx%>)">
	            			<img width="16px" height="16px" src="<crh-common:url value='/images/silk/pencil.png'/>"/>
	            		</a>
           			</td>
           			<td width="100px">
                    <!-- Delivery To Date -->
                    <html:text property="editorList[${idx}].deliveryToDatStr" styleId="deliveryToDatStr[${idx}]"/>
                    </td>
                    <td width="80px">
                        <!-- Delivery To Time -->
                        <html:text property="editorList[${idx}].deliveryToTime" styleId="deliveryToTime[${idx}]"/>
                    </td>
           		</tr>
           		<tr>
           			<td colspan="3">
           			    <!-- Rail Delivery To -->
		            	<div id="divRailDeliveryData0" class="hiddenDiv" style="margin-left: 2px; margin-top: 2px">
			   				<div id="divActualDeliveryDatWarn0" class="hiddenDiv" style="float: left; width: 20px"><img src="<crh-common:url value='/images/warning2.png'/>"/></div>
			   				<div id="divActualDeliveryDatOk0" class="hiddenDiv" style="float: left; width: 20px"><img src="<crh-common:url value='/images/silk/accept.png'/>"/></div>
			   				<div id="divActualDeliveryDatTxt0">n.a.</div>
			   				<div id="divEarliestDeliveryDatTxt0">n.a.</div>
		   				</div>
           			</td>
           		</tr>
           		<tr>
           			<td width="115px" colspan="2">
           				<table cellpadding="0" cellspacing="0" style="padding: 0px; margin:0px; border-spacing: 0px">
           					<tr>
		            			<td style="vertical-align: middle;">
		            				<!-- Plus/Minus Sign for Delivery Time tolerance -->
		            				<c:out value="&#177;" escapeXml="false"/>
		            			</td>
		            			<td title="<fmt:message key="dispokarte.texte.title.deliveryTimeTolerance"/>">
		            			   	<!-- Delivery Time Tolerance -->
  										<html:text property="editorList[${idx}].deliveryTimeToleranceStr" styleId="editorList[${idx}].deliveryTimeToleranceStr"/>
		            			</td>
		            			<td>
		            			   	<!-- Delivery Time Tolerance UoM -->
           							<html:hidden property="editorList[${idx}].deliveryTimeToleranceUomCd" styleId="editorList[${idx}].deliveryTimeToleranceUomCd"/> 
		            			</td>
           					</tr>
           				</table>
           			</td>
           			<td width="80px" style="vertical-align: middle;">
           				<!-- Split Spinner -->
           				<img id="splitSpinner<%=idx%>" src="<crh-common:url value='/images/default/grid/loading.gif'/>" style="display: none; width: 16px; margin-top: 3px; margin-left: 3px">
           			    <!-- Number of Deliveries -->
           				<span id="editorList[<%=idx%>].numberOfDeliveriesText" styleId="editorList[<%=idx%>].numberOfDeliveriesText"/>
           			</td>
           		</tr>
           	</table>
           </td>
           
           <td style="width:150px;">
           	<table cellpadding="0" cellspacing="0" style="padding: 0px; margin:0px; border-spacing: 0px">
           		<tr>
           			<td>
           			    <!-- Delivery Plant -->
           				<html:hidden property="editorList[${idx}].supplyPlantCd" styleId="editorList[${idx}].supplyPlantCd"/>
           			</td>
           		</tr>
            		<tr>
            		    <td>
           				<!-- Shipping Point -->
           				<html:hidden property="editorList[${idx}].orderHelper.shippingpointId" styleId="editorList[${idx}].orderHelper.shippingpointId"/>
           			</td>
           		</tr>
           		<tr>
           		    <td>
           				<!-- Loading Point -->
           				<html:hidden property="editorList[${idx}].orderItems[${itemIdx}].loadingpointId" styleId="editorList[${idx}].orderItems[${itemIdx}].loadingpointId"/>
           			    <!-- Bimodal Rail Order Item -->
           				<html:hidden property="editorList[${idx}].orderHelper.bimodalRailOrderItemId" styleId="editorList[${idx}].orderHelper.bimodalRailOrderItemId"/>
           			</td>
           		</tr>      		
           	</table>
           </td>
           
           <td style="width:230px;">
           	<table cellpadding="0" cellspacing="0" style="padding: 0px; border-spacing: 0px">
      	    	<tr>
      	    		<!-- Auto Pool Assignment Flag -->
      	    		<td style="width: 15px; vertical-align: middle;">
						<html:checkbox onclick="updateAutoFlags(this, ${idx});" property="editorList[${idx}].autoPoolAssignmentFlag" styleId="editorList[${idx}].autoPoolAssignmentFlag" value="true" disabled="${isEditorDisabled || (empty editor.orderHelper.poolId) || editor.orderHelper.poolId == 0 || editor.orderHelper.poolId == -1}"/>
	      	    	</td>
	      	    	<!-- Pool -->
	      	    	<td style="width: 115px">
      	    			<html:hidden property="editorList[${idx}].orderHelper.poolId" styleId="editorList[${idx}].orderHelper.poolId"/>
	      	    	</td>
	      	    	<!-- Bill To -->
	      	    	<td style="width: 100px">
	      	    		<html:hidden property="editorList[${idx}].orderHelper.billtoSapBpId" styleId="editorList[${idx}].orderHelper.billtoSapBpId"/>
	      	    	</td>
      	    	</tr>
      	    	<tr>
      	    		<!-- Auto Carrier Assignment Flag -->
      	    		<td style="width: 15px; vertical-align: middle;">
						<html:checkbox onclick="updateAutoFlags(this, ${idx});" property="editorList[${idx}].autoCarrierAssignmentFlag" styleId="editorList[${idx}].autoCarrierAssignmentFlag" value="true" disabled="${isEditorDisabled || (empty editor.orderHelper.commCarrierId) || editor.orderHelper.commCarrierId == 0 || editor.orderHelper.commCarrierId == -1}"/>
	      	    	</td>
	      	    	<!-- Commercial Carrier -->
	      	    	<td style="width: 115px">
	      	    		<html:hidden property="editorList[${idx}].orderHelper.commCarrierId" styleId="editorList[${idx}].orderHelper.commCarrierId"/>
	      	    	</td>
	      	    	<!-- Payer -->
	      	    	<td style="width: 100px">
	      	    		<html:hidden property="editorList[${idx}].orderHelper.payerSapBpId" styleId="editorList[${idx}].orderHelper.payerSapBpId"/>
	      	    	</td>
      	    	</tr>
	      	    <tr>
	      	    	<td/>
	      	    	<!-- Executing Carrier -->
	      	    	<td style="width: 115px">
	      	    		<html:hidden property="editorList[${idx}].orderHelper.execCarrierId" styleId="editorList[${idx}].orderHelper.execCarrierId"/>
	      	    	</td>
	      	    	<td/>
	      	    </tr>
				<tr>
	      	    	<td/>
	      	    	<!-- Release SAP Indicators -->
	      	    	<td style="width: 115px">
	      	    		<input type="hidden" name="editorList[<c:out value="${idx}"/>].checkboxCombo" id="editorList[<c:out value="${idx}"/>].checkboxCombo"/>
	      	    	</td>
	      	    	<!-- Special Order Indicators -->
	      	    	<td style="width: 100px">
	      	    		<input type="hidden" name="editorList[<c:out value="${idx}"/>].serviceProcessIndicatorId" id="editorList[<c:out value="${idx}"/>].serviceProcessIndicatorId"/>
	      	    	</td>
	      	    </tr>
     	    	</table>
           </td>
	</tr>

	<!-- Comments -->
	<tr style="background-color: #DDDDDD">
		<td colspan="9" style="padding-top: 0px">
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