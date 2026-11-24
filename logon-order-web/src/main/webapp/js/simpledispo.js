/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

/*
 * JavaScript for the Simple Vehicle Scheduling screen. 
 */

/**
 * Object to collect all the possible actions, with which the server could be called. 
 */
var actions = {
		INPUT : 'input',
		SELECT_POOL : 'selectPool',
		SELECT_CARRIER : 'selectCarrier',
		ASSIGN_ORDER_TO_VEHICLE : 'assignOrderToVehicle',
		CHANGE_VEHICLE_PRIORITY : 'changeVehiclePriority',
		RELEASE_ASSIGNMENT : 'releaseAssignment',
		SHOW_LOAD_DIALOG : 'showLoadDialog',
		SHOW_UNLOAD_DIALOG : 'showUnloadDialog',
		SHOW_DEALLOCATE_DIALOG : 'showDeallocateDialog',
		DEALLOCATE_ONE_ORDER : 'deallocateOneOrder'
};

/**
 * Sets the selected pool and loads the related carriers.
 */
function setSelectedPool() {
	document.getElementById("inputAction").value = actions.SELECT_POOL;
	Ext.get('order_simpledispo_SimpleDispoForm').dom.submit();
}

/**
 * Assigns the order with the given ID to the vehicle with the given ID.
 * @param orderId - the ID of the order to assign .
 * @param simpleVehicleId - the ID of the simple vehicle to assign.
 */
function assignOrderToVehicle(orderId, simpleVehicleId) {
	document.getElementById("simpleVehicleId").value = simpleVehicleId;
	document.getElementById("orderId").value = orderId;
	document.getElementById("inputAction").value = actions.ASSIGN_ORDER_TO_VEHICLE;
	Ext.get('order_simpledispo_SimpleDispoForm').dom.submit();
}

function changeTruckPriority(simpleVehicleId, priority) {
	document.getElementById("simpleVehicleId").value = simpleVehicleId;
	document.getElementById("priority").value = priority;
	document.getElementById("inputAction").value = actions.CHANGE_VEHICLE_PRIORITY;
	Ext.get('order_simpledispo_SimpleDispoForm').dom.submit();
}

function releaseAssignment(assignmentId) {
	document.getElementById("assignmentId").value = assignmentId;
	document.getElementById("inputAction").value = actions.RELEASE_ASSIGNMENT;
	Ext.get('order_simpledispo_SimpleDispoForm').dom.submit();
}

function showLoadDialog(simpleVehicleId) {
	var params = {
		'simpleVehicleId' : simpleVehicleId,
		'inputAction' : actions.SHOW_LOAD_DIALOG
	};
	showPopup('SimpleDispo.do', texts.loading,
			params);
}

function showDeallocateDialog(simpleVehicleId) {
	var params = {
		'simpleVehicleId' : simpleVehicleId,
		'inputAction' : actions.SHOW_DEALLOCATE_DIALOG
	};
	showPopup('SimpleDispo.do', texts.loading,
			params);
}

/**
 * Calls the server to deallocate one order 
 * from the selected simple vehicle.
 * 
 * @param simpleVehicleId - the selected simple vehicle, 
 *  from which the order should be deallocated
 */
function deallocateOrder(simpleVehicleId) {
	document.getElementById("simpleVehicleId").value = simpleVehicleId;
	document.getElementById("inputAction").value = actions.DEALLOCATE_ONE_ORDER;
	Ext.get('order_simpledispo_SimpleDispoForm').dom.submit();
}

function showUnloadDialog(orderId) {
	var params = {
		'orderId' : orderId,
		'inputAction' : actions.SHOW_UNLOAD_DIALOG
	};
	showPopup('SimpleDispo.do', texts.loading,
			params);
}

function showAssignmentEditDialog(assignmentId) {
	var params = {
		'assignmentId' : assignmentId
	};	
	showPopup('../dispo/AssignmentEditEntry.do', texts.loading, params);
}

function showOrderDetailPopup(orderId) {
	var params = {
		'orderid' : orderId
	};
	showPopup('../auftrag/OrderDetailEntry.do',
			texts.loading, params);
}

/** 
 * Creates the material popup html for the given list of material infos
 * and sets into the quick tip.
 */
function buildOrderMaterialInfo(qt, matInfoList) {
	var materialTableRows = [];
	
	for (var i = 0; i < matInfoList.length; i++) {
		var sumQtyEntity = matInfoList[i].orderItemOrderedQtyEntity;
		
		var materialName = matInfoList[i].materialName;
		var materialNr = sumQtyEntity.sapMaterialNo;
		var orderedQuantity = sumQtyEntity.orderedQuantity + ' ' + sumQtyEntity.orderedUomTxt;
		
		materialTableRows[i] = {tag: 'tr', children: [
		    {tag: 'td', html: materialNr},
			{tag: 'td', html: materialName},
			{tag: 'td', html: orderedQuantity}
		]};
	}
	
	var materialTable = {tag: 'table', cls: 'materialTable', children: [
		{tag: 'thead', children: [
			{tag: 'tr', children: [
				{tag: 'th', html: texts.materialNr},
				{tag: 'th', html: texts.materialDesc},
				{tag: 'th', html: texts.orderedQty}
			]},
			{tag: 'body', children: materialTableRows}
		]}
	]};
	
	qt.materialPopup = Ext.get(Ext.DomHelper.append(qt.getLayoutTarget().up('div'), materialTable));
	qt.materialPopup.dom.style.height = 20 + (i * 18) + 'px';
	qt.setWidth('340px');
	qt.materialPopup.dom.style.position = 'relative';
	qt.materialPopup.show();
}