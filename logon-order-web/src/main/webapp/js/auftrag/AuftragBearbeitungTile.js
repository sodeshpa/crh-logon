// AuftragBearbeitungTile.jsp

var t;
var callback;

//For the usage of the "Order id" filter field, all other filters and fields must be disabled.
var blockOtherFilters = false;

function selectStatusAll(under){
	if (!blockOtherFilters) {
		var el = document.getElementById("selectedStatus");
		for (var i=0; i<el.length; i++){
			el[i].selected=false;
			if (el[i].value < under){
				el[i].selected=true;
			}
		}
	}
}

function selectShippingPoint() {
	refreshStatistic();		
}

function loadShippingPoints() {
	var selected = dwr.util.getValue('selectedPlantCd');
	var params = {"selectedPlantCd":selected, "formAction":actions.LOAD_SHIPPINGPOINTS};
	addFormElements(params, "AuftragBearbeitungForm");
	var diva = Ext.get('shippingPointSelection');
	diva.load({
		url: 'AuftragBearbeitung.do',
		params: params,
		text: texts.extjsLoading,
		scripts: true
	});
}

function setOrderBy(colname) {
	if (document.getElementById("selectedOrderBy").value == colname){
		var bool = document.getElementById("sortAsc").value;
		if (bool == 'true'){
			document.getElementById("sortAsc").value = false;
		}
		else{
			document.getElementById("sortAsc").value = true;
		}
	}
	else{
		document.getElementById("selectedOrderBy").value = colname;
	}
}

function loadOrders(keepPaging) {
	
	Ext.getDom('orderListSearchButton').disabled = true;
	var jsdiv = document.getElementById("updateJS");
	if (jsdiv !== null){		
		jsdiv.innerHTML="";
	}
	var selected="";
	var selShip = document.getElementById('selectedShippingPointId');
	if (selShip){
		selected = selShip.value;
	}
	
	var statuses = [];
	var selSt = document.getElementById('selectedStatus');
	if (selSt){
		statuses = dwr.util.getValue('selectedStatus');
	}
	
	var searchOrderNo = document.getElementById('searchOrderNo').value;
	var params = {"formAction": actions.LOAD_ORDERS, "selectedShippingPointId":selected, 
			"searchOrderNo":searchOrderNo, "selStatus":statuses};
	addFormElements(params, "AuftragBearbeitungForm");
	
	Ext.Ajax.request({
		url: 'AuftragBearbeitung.do',
		params: params,
		callback: function() {
			refreshOrderList(keepPaging);
			refreshStatistic();
		}
	});
	
	callback=undefined;
}

function setSelectedDate(param) {
	if (!blockOtherFilters) {
		var params = {"selectedDateOption":param, "formAction":actions.SELECT_DATE_INTERVAL};
		addFormElements(params, "AuftragBearbeitungForm");
		
		var diva = Ext.get('dateSelection');
		diva.load({
			url: 'AuftragBearbeitung.do', 
			params: params,
			text: texts.extjsLoading,
			scripts: true
		});
	}
}

//Searching based on orderId.
function searchOrderByNo() {
	var searchOrderNo = document.getElementById('searchOrderNo').value;
	if (searchOrderNo === "") {
		enableNonOrderNoFilters();
	}
	else {
		disableNonOrderNoFilters();
	}
}

function disableNonOrderNoFilters() {
	setNonOrderNoFilterStatus(true);
}

function enableNonOrderNoFilters() {
	setNonOrderNoFilterStatus(false);
}

function setNonOrderNoFilterStatus(value) {
	document.getElementById('selectedPlantCd').disabled = value;
	document.getElementById('selectedShippingPointId').disabled = value;
	document.getElementById('selectedTransportCat').disabled = value;
	document.getElementById('selectedStatus').disabled = value;
	document.getElementById('searchtext').disabled = value;
	document.getElementById('searchFromDate').disabled = value;
	document.getElementById('searchToDate').disabled = value;
	blockOtherFilters = value;
}

// LOGON-6715 treat searchtext ('Filter:') like orderNo in terms of other search criteria
function searchOrderBySearchText() {
	var searchSearchText = document.getElementById('searchtext').value;
	if (searchSearchText === "") {
		enableNonSearchTextFilters();
	}
	else {
		disableNonSearchTextFilters();
	}
}

function disableNonSearchTextFilters() {
	setNonSearchTextFilterStatus(true);
}

function enableNonSearchTextFilters() {
	setNonSearchTextFilterStatus(false);
}

function setNonSearchTextFilterStatus(value) {
	document.getElementById('selectedPlantCd').disabled = value;
	document.getElementById('selectedShippingPointId').disabled = value;
	document.getElementById('selectedTransportCat').disabled = value;
	document.getElementById('selectedStatus').disabled = value;
	document.getElementById('searchOrderNo').disabled = value;
	document.getElementById('searchFromDate').disabled = value;
	document.getElementById('searchToDate').disabled = value;
	blockOtherFilters = value;
}

//Statistics

var statistic1Enabled = false;
var statistic2Enabled = false;

function refreshStatistic(){
	refreshStatistic1();
	refreshStatistic2();
}

function enableStatistic1() {
	statistic1Enabled = true;
	refreshStatistic1();
}

function enableStatistic2() {
	statistic2Enabled = true;
	refreshStatistic2();
}

function refreshStatistic1() {
	if (statistic1Enabled) {
		var divb = Ext.get('infoStatistic');
		var params1 = {"formAction":actions.GET_STATISTIC};
		addFormElements(params1, "AuftragBearbeitungForm");		
		divb.load({
			url: 'AuftragBearbeitung.do', 
			params: params1,
			text: texts.extjsLoading,
			scripts: true
		});
	}
}

function refreshStatistic2() {
	if (statistic2Enabled) {
		var divc = Ext.get('infoStatistic2');
		var params1 = {"formAction":actions.GET_STATISTIC2};
		addFormElements(params1, "AuftragBearbeitungForm");		
		divc.load({
			url: 'AuftragBearbeitung.do', 
			params: params1,
			text: texts.extjsLoading,
			scripts: true
		});
	}
}

///////////////////
//ORDER LIST GRID//
///////////////////

var sumTareWeights = []; //used to calculate the tare measure for once, truck' weight is calculated by masterdata
var selectedRowIndexes = []; //used to prevent multiple evaluation of the same row for each click
var loadIds = []; //used to identify the shared loads and activate all the shared assignments.

var isSharedLoad = []; //used to find shared loads (Tour case) only show the save button for one row and open them all by clicking on any of them.
var uniqueLoadIds = []; //used to find shared loads (Tour case) only show the save button for one row and open them all by clicking on any of them.

var rowColors = []; //used to find shared assignments and use the same color for it.
var uniqueAssignmentIds = []; //used to find shared assignments and use the same color for it.

//Div IDs for inner HTML update
var GRID_LOADABLE_DIV_ID_PREFIX = 'gridLADiv_';
var GRID_LP_DIV_ID_PREFIX = 'gridLPDiv_';
var GRID_LP_BIMODAL_DIV_ID_PREFIX = 'gridLPBMDiv_';
var GRID_TARE_MEASURE_WEIGHTED_DIV_ID_PREFIX = 'gridTMWDiv_';
var GRID_NET_WEIGHT_DIV_ID_PREFIX = 'gridNWDiv_';
var GRID_GROSS_WEIGHT_DIV_ID_PREFIX = 'gridGWDiv_';
var GRID_CONTROL_DIV_ID_PREFIX = 'gridControlDiv_';
var GRID_ORDER_NR_DIV_ID_PREFIX = 'gridONrDiv_';
var GRID_LDS_INFO_BTN_DIV_ID_PREFIX = 'gridLdsInfoBtnDiv_';

var dateTimeRenderer = Ext.util.Format.dateRenderer('d.m.y H:i');

var lineBreak = '&#13;';
	
var shippingRecord = Ext.data.Record.create([
	{name: "compositeId", mapping: 'loadRow.compositeId'},
	
	//Truck name + container
	{name: "truckName", mapping: 'loadRow.truckName'},
	{name: "containerName", mapping: 'loadRow.containerName'},
	{name: "simpleVehicleName", mapping: 'loadRow.simpleVehicleName'},
	
	//Truck plate no + driver
	{name: "truckPlateNo", mapping: 'loadRow.truckPlateNo'},
	{name: "driverName", mapping: 'loadRow.driverName'},
	
	//Pool + Carrier
	{name: "poolName", mapping: 'loadRow.poolName'},
	{name: "commCarrierName", mapping: 'loadRow.commCarrierName'},
	{name: "execCarrierName", mapping: 'loadRow.execCarrierName'},
	{name: "simpleVehicleRegNo", mapping: 'loadRow.simpleVehicleRegNo'},
	
	//Loading starts
	{name: "loadingPlannedStart"},
	{name: "loadingRealStart"},
	
	//Material + prod plant 
	{name: "materialTxt", mapping: 'loadRow.materialTxt'},
	{name: "materialNo", mapping: 'loadRow.materialNo'},
	{name: "productionPlantName", mapping: 'loadRow.productionPlantName'},
	
	//Tare Weight master Data
	{name: "tareWeightT", mapping: 'loadRow.tareWeightT'},
	{name: "trailerTareWeightT", mapping: 'loadRow.trailerTareWeightT'},
	{name: "containerTareWeightT", mapping: 'loadRow.containerTareWeightT'},
	{name: "simpleVehicleTareWeightT", mapping: 'loadRow.simpleVehicleTareWeightT'},
	
	//Tare weight weighted
	{name: "logonPackageTypeCd", mapping: 'loadRow.logonPackageTypeCd'},
	
	//Net weight + Gross Weight
	{name: "loadedQuantity", mapping: 'loadRow.loadedQuantity'},
	{name: "loadUnitTxt", mapping: 'loadRow.loadUnitTxt'},
	{name: "loadedNetMeasure", mapping: 'loadRow.loadedNetMeasure'},
	{name: "loadUomDimensionTxt", mapping: 'loadRow.loadUomDimensionTxt'},
	{name: "orderedUomFuzzy", mapping: 'loadRow.orderedUomFuzzy'},
	{name: "baseUomCd", mapping: 'loadRow.baseUomCd'},
	{name: "orderedUomCd", mapping: 'loadRow.orderedUomCd'},
	{name: "loadedUomCd", mapping: 'loadRow.loadedUomCd'},
	
	//Used for loading point display and selection for loading
	{name: "loadingpointName", mapping: 'loadRow.loadingpointName'},
	{name: "loadOrderId", mapping: 'loadRow.loadOrderId'},
	{name: "shippingpointId", mapping: 'loadRow.shippingpointId'},
	{name: "sapMaterialId", mapping: 'loadRow.sapMaterialId'},
	{name: "loadId", mapping: 'loadRow.loadId'},
	{name: "assignmentId", mapping: 'loadRow.assignmentId'},
	{name: "orderItemId", mapping: 'loadRow.orderItemId'},
	
	//Chamber number + Gross weight
	{name: "chamberNumber", mapping: 'loadRow.chamberNumber'},
	
	//Ordered Qty
	{name: "orderedQuantity", mapping: 'loadRow.orderedQuantity'},
	{name: "orderUnitTxt", mapping: 'loadRow.orderUnitTxt'},
	{name: "orderedMeasure", mapping: 'loadRow.orderedMeasure'},
	{name: "truckMaxNetWeightT", mapping: 'loadRow.truckMaxNetWeightT'},
	{name: "containerMaxNetWeightT", mapping: 'loadRow.containerMaxNetWeightT'},
	{name: "orderUomDimensionTxt", mapping: 'loadRow.orderUomDimensionTxt'},
	
	//Order Nr + Order Status
	{name: "orderNumber", mapping: 'loadRow.orderNumber'},
	{name: "statusTxt", mapping: 'loadRow.statusTxt'},
	
	//Soldto + Shipto
	{name: "soldtoBpName", mapping: 'loadRow.soldtoBpName'},
	{name: "shiptoBpName", mapping: 'loadRow.shiptoBpName'},
	
	//last column, generate PDF, save, reset
	{name: "orderId", mapping: 'loadRow.orderId'},
	{name: "netQuantity", mapping: 'loadRow.netQuantity'},
	{name: "loadTareMeasure", mapping: 'loadRow.loadTareMeasure'},
	{name: "loadingpointId", mapping: 'loadRow.loadingpointId'},
	
	//Title on PDF
	{name: "noteOrderMgmt", mapping: 'loadRow.noteOrderMgmt'},
	{name: "noteDelivery", mapping: 'loadRow.noteDelivery'},
	{name: "noteDispatching", mapping: 'loadRow.noteDispatching'},
	{name: "noteTruckDriver", mapping: 'loadRow.noteTruckDriver'},
	{name: "noteRailwayDisp", mapping: 'loadRow.noteRailwayDisp'},
	{name: "custWeborderNote", mapping: 'loadRow.custWeborderNote'},
	{name: "deliverynoteOutputdocId", mapping: 'loadRow.deliverynoteOutputdocId'},
	
	//Precheck loading conditions
	{name: "enableUpdateNettoMeasureData"},
	{name: "enableUpdateOrderItemLoadedQuantity"},
	{name: "loadingOnCurrentDay"}
]);

var proxy = new Ext.data.DWRProxyForPager(
	function(skip, count, sort, callback) { 
		initializeListVars();
		OrderAjaxService.loadShippingOrders(skip, count, sort, callback); 
	}, true
);

var shippingStore = new Ext.data.Store({
	proxy: proxy,
	reader: new Ext.data.ListRangeReader({id:'loadRow.compositeId', totalProperty:'totalSize'}, shippingRecord),
	remoteSort: true
});

// ------ PAGER ---------
var shippingPagingBar = new Ext.PagingToolbar({
	id: 'shippingPagingBar',
	pageSize: MAX_ROW_COUNT,
	store: shippingStore,
	displayInfo: true,
	displayMsg: 'Displaying orders {0} - {1} of {2}',
	emptyMsg: "No results found..."
});

var selectionModel = new Ext.grid.RowSelectionModel({singleSelect:true});

var greenColumnCss = 'background-color: #CCFFCC;';

// LOGON-6715 Point 4.5
// Don't repeat common data entries for same-assignment entries
// Criteria for "merge" is matching assignment ID
var repetitionPlaceholder = '';
function belongsToPreviousAssignment(v, p, record, rowIndex, colIndex) {
	var prevRecord = rowIndex > 0 ? record.store.getAt(rowIndex-1) : null;
	var retVal = false;
	if (prevRecord) {
		retVal = prevRecord.get('assignmentId') === record.get('assignmentId');
		if(!retVal) {
			retVal = prevRecord.get('orderNumber') === record.get('orderNumber');
		}
	}
	
	return retVal;
}

function hasSameOrderNumber(record, rowIndex) {
	var prevRecord = rowIndex > 0 ? record.store.getAt(rowIndex - 1) : null;
	if(prevRecord) {
		return prevRecord.get('orderNumber') === record.get('orderNumber');
	}
	return false;
}


var grid = new Ext.grid.GridPanel({
	id: 'shippingSearchGrid',
	el:'divShippingSearchResults',
	autoHeight: true,
	viewConfig: {autoFill: true, forceFit: true},
	trackMouseOver: false, //Shared assignment group coloring does not work well with hover effect.
	title:'<fmt:message key="ordersearch.table.header.title"/>',
	//frame: true,
	loadMask: true,
	bbar: shippingPagingBar,
	store: shippingStore,
	// grid columns
	// LOGON-6715 Point 4.3
	// reorderer columns according to new specification
	columns:[
	{
		id: 'loadable',
		header: shippingHeaders.loadable,
		sortable: false,
		dataIndex: 'enableUpdateNettoMeasureData',
		width: 40,
		renderer: function(v, p, record) {
			var index = shippingStore.indexOf(record);
			var divId = GRID_LOADABLE_DIV_ID_PREFIX + index;
			return "<div id=" + divId + "></div>";
		}
	},{
		id: 'soldtoBpName',
		header: shippingHeaders.soldtoBpName + '<br/>' + shippingHeaders.shiptoBpName,
		sortable: false,
		dataIndex: 'soldtoBpName',
		hidden: true,
		renderer: function(v, p, record) {
			return getRecordProperties(record, ['soldtoBpName', 'shiptoBpName']);
		}
	},{
		id: 'poolName',
		header: shippingHeaders.poolName + '<br/>' + shippingHeaders.commCarrierName + '<br/>' + shippingHeaders.execCarrierName,
		sortable: true,
		dataIndex: 'poolName',
		renderer: function(v, p, record, rowIndex, colIndex) {
			if(belongsToPreviousAssignment(v, p, record, rowIndex, colIndex)) {
				return repetitionPlaceholder;
			}
			return getRecordProperties(record, ['poolName', 'commCarrierName', 'execCarrierName']);
		}
	},{
		id: 'orderNumber',
		header: shippingHeaders.orderNumber + '<br/>' + shippingHeaders.statusTxt,
		sortable: true,
		dataIndex: 'orderNumber',
		renderer: function(v, p, record, rowIndex, colIndex) {
			if(belongsToPreviousAssignment(v, p, record, rowIndex, colIndex) && hasSameOrderNumber(record, rowIndex)) {
				return repetitionPlaceholder;
			}
			var index = shippingStore.indexOf(record);
			var divId = GRID_ORDER_NR_DIV_ID_PREFIX + index;
			var orderNotes = getOrderNotes(record);
			return '<div style="text-align: right" id=' + divId + ' title="' + orderNotes + '">' + getOrderNo(record, index) + '</div>';
		}
	},{
		id: 'truckPlateNo',
		header: shippingHeaders.truckPlateNo + '<br/>' + shippingHeaders.driverName,
		sortable: true,
		dataIndex: 'truckPlateNo',
		renderer: function(v, p, record, rowIndex, colIndex) {
			if(belongsToPreviousAssignment(v, p, record, rowIndex, colIndex)) {
				return repetitionPlaceholder;
			}
			var returnValue = getRecordProperties(record, ['simpleVehicleRegNo']);
			if (returnValue === '') {
				returnValue =  getRecordProperties(record, ['truckPlateNo', 'driverName']); 
			}
			return returnValue;
		}
	},{
		id: 'truckName',
		header: shippingHeaders.truckid + '<br/>' + shippingHeaders.containerName,
		sortable: true,
		dataIndex: 'truckName',
		renderer: function(v, p, record, rowIndex, colIndex) {
			if(belongsToPreviousAssignment(v, p, record, rowIndex, colIndex)) {
				return repetitionPlaceholder;
			}
			var returnValue = getRecordProperties(record, ['simpleVehicleName']);
			if (returnValue === '') {
				returnValue = getRecordProperties(record, ['truckName', 'containerName']); 
			}
			return returnValue; 
		}
	},{
		id: 'loadingPlannedStart',
		header: shippingHeaders.loadingPlannedStart + '<br/>' + shippingHeaders.loadingRealStart,
		sortable: false,
		dataIndex: 'loadingPlannedStart',
		width: 80,
		renderer: function(v, p, record) {
			return getArrivalTimes(record);
		}
	},{
		id: 'tareWeightT',
		header: shippingHeaders.tareWeightT,
		sortable: false,
		dataIndex: 'tareWeightT',
		width: 60,
		renderer: function(v, p, record, rowIndex, colIndex) {
			if(belongsToPreviousAssignment(v, p, record, rowIndex, colIndex)) {
				return repetitionPlaceholder;
			}
			return "<div style='text-align: right'>" + getTareWeightMasterData(record) + "</div>";	
		}
	},{
		id: 'materialTxt',
		header: shippingHeaders.materialTxt + '<br/>' + shippingHeaders.materialNo + '<br/>' + shippingHeaders.productionPlantName,
		sortable: false,
		dataIndex: 'materialTxt',
		renderer: function(v, p, record) {
			return getRecordProperties(record, ['materialTxt', 'materialNo', 'productionPlantName']);
		}
	},{
		id: 'loadingpointName',
		header: shippingHeaders.loadingpointName + '<br/>' + shippingHeaders.loadOrderId + '<br/>' + shippingHeaders.additionalLoadInfo,
		sortable: false,
		dataIndex: 'loadingpointName',
		renderer: function(v, p, record) {
			var index = shippingStore.indexOf(record);
			var bimodalDivId = GRID_LP_BIMODAL_DIV_ID_PREFIX + index;
			var divId = GRID_LP_DIV_ID_PREFIX + index;
			var ldsInfoBtnDiv = GRID_LDS_INFO_BTN_DIV_ID_PREFIX + index;
			return '<div id='+ bimodalDivId + '></div><div id='+ divId + '>' + getLoadingPointColumnDefault(record, index) + '</div><div id='+ldsInfoBtnDiv+'></div>';
		}
	},{
		id: 'orderedQuantity',
		header: shippingHeaders.orderedQuantity,
		sortable: false,
		dataIndex: 'orderedQuantity',
		renderer: function(v, p, record) {
			return "<div style='text-align: right'>" + getOrderQuantity(record) + "</div>";
		}
	},{
		id: 'tareMeasure',
		header: shippingHeaders.tareMeasure,
		sortable: false,
		dataIndex: 'tareMeasure',
		width: 60,
		renderer: function(v, p, record) {
			var index = shippingStore.indexOf(record);
			var divId = GRID_TARE_MEASURE_WEIGHTED_DIV_ID_PREFIX + index;
			var loadTareMeasure = getNonNullProperty(record, 'loadTareMeasure');
			return "<div style='text-align: right' id=" + divId + ">" + loadTareMeasure + "</div>";
		}

	},{
		id: 'nettoWeight',
		header: shippingHeaders.netQty,
		sortable: false,
		dataIndex: 'nettoWeight',
		width: 120,
		css: greenColumnCss,
		renderer: function(v, p, record) {
			var index = shippingStore.indexOf(record);
			var divId = GRID_NET_WEIGHT_DIV_ID_PREFIX + index;
			var nettoWeight = getDefaultNetWeight(record);
			return "<div style='text-align: right' id=" + divId + ">" + nettoWeight + "</div>";
		}
	},{
		id: 'chamberNumber',
		header: shippingHeaders.grossWeight + '<br/>' + shippingHeaders.chamberNumber,
		sortable: false,
		dataIndex: 'chamberNumber',
		width: 80,
		css: greenColumnCss,
		renderer: function(v, p, record) {
			var chamberNumberText = getChamberNumber(record);
			var index = shippingStore.indexOf(record);
			var divId = GRID_GROSS_WEIGHT_DIV_ID_PREFIX + index;
			var grossWeight = getGrossWeight(record);
			return "<div style='text-align: right' id=" + divId + ">" + grossWeight + "</div><div style='text-align: right'>" + chamberNumberText + "</div>";
		}
	},{
		id: 'save',
		header: 'PDF',
		width: 40,
		sortable: false,
		dataIndex: 'compositeId',
		renderer: function(v, p, record) {
			var index = shippingStore.indexOf(record);
			var divId = GRID_CONTROL_DIV_ID_PREFIX + index;
			return '<div id=' + divId + '>' + getGeneratePdfAnchor(record) + '</div>';
		}
	}],
	selModel: selectionModel
});

////////////////////////
//ORDER LIST GRID INIT//
////////////////////////

function initializeOrderList() {
	grid.render(); // render the grid
	
	//applies "shared load (Bag tour case) multirow opening + saving" function
	grid.getSelectionModel().on('rowselect', function(sm, rowIdx, record) {
		handleRowClick(rowIdx, record);
	});
	
	//applies "shared assignment group coloring" function
	grid.getView().on('refresh', function(view, firstRow, lastRow) {
		handleRefresh(view);
	});
}

//main function to refresh the grid content.
function refreshOrderList(keepPaging) {
	initializeListVars();
	if (!keepPaging) {		
		shippingStore.load({params:{start: 0, limit: MAX_ROW_COUNT, sort: ''}});
	}
	else {
		shippingStore.reload();
	}
}

//Has to be called at each loading or paging, initializes the page depending status variables to handle special cases.
function initializeListVars() {
	//The previously selected row mus be deselected, otherwise it is instantly selected after reloading the list
	deselectGridRow();
	
	//prevention of activating a row multiple by clicking on it multiple times (client side optimization)
	selectedRowIndexes = [];
	
	//one time tare weight calculation (client side optimization)
	sumTareWeights = [];
	
	//shared load (Bag tour case) multirow opening + saving
	loadIds = [];
	uniqueLoadIds = [];
	isSharedLoad = [];
	
	//shared assignment group coloring
	rowColors = [];
	uniqueAssignmentIds = [];
}

//Deselects the currently selected row.
function deselectGridRow() {
	var record = selectionModel.getSelected();
	var index = shippingStore.indexOf(record);
	selectionModel.deselectRow(index);
}

/////////////////////////////
//GRID GROUPING COLORING   //
/////////////////////////////

var COLORS = ['White', 'WhiteSmoke'];
var isFirstColorActive = true;

function getCurrentRowColor() {
	if (isFirstColorActive) {
		return COLORS[0];
	}
	else {
		return COLORS[1];
	}
}

function switchRowColor() {
	isFirstColorActive = !isFirstColorActive;
}

function handleRefresh(view) {
	for (i = 0; i < MAX_ROW_COUNT + 1; i++) {
		var row = view.getRow(i);
		if (row != null) {
			paintOnRefresh(row);
			preCheckLoadabilityOnRefresh(i);
		}
	}
	Ext.getDom('orderListSearchButton').disabled = false;
}

function paintOnRefresh(row) {
	row.style.backgroundColor = rowColors[i];
}

//This method has to be called initially for all rows but only for once!!!
//This method is also responsible to determine the background colors for each row.
function findSharedAssignment(record, index) {
	var isSharedAssignmentCase = isSharedAssignmentNonFirstItem(record, index);
	//In the shared assignment case we keep the previous row's color
	if (!isSharedAssignmentCase) {
		//otherwise we switch color
		switchRowColor();
	}
	if (record.get('materialNo') == SAP_MATERIAL_NO_LANDFILL_PLANNING
			|| record.get('materialNo') == SAP_MATERIAL_NO_AGG_PLANNING) {
		rowColors[index] = "#F6CEAE";
	}
	else {
		rowColors[index] = getCurrentRowColor();
	}
}

//Method to check if a record is only a part of a shared assignment (Group Coloring)
//This method has to be called initially for all rows but only for once!!!
function isSharedAssignmentNonFirstItem(record, index) {
	var assignmentId = record.get('assignmentId');
	
	if (assignmentId == null) {
		// If assignmentId is empty then it cannot be a shared load at all.
		// LOGON-6715: it actually can, if it is a service item. For this case, we
		// check if the order number matches to assign it to the previous group.
		if(index > 0){
			if (record.get('orderNumber') == record.store.getAt(index-1).get('orderNumber')) {
				return true;
			}
		}
		return false;
	}
	else {
		//If assignmentId is not empty then it can still be grouped or non-grouped coloring case
		var saveAbleAssignmentIdIndex = uniqueAssignmentIds.indexOf(assignmentId);
		
		if (saveAbleAssignmentIdIndex == -1) {
			//If the loadId is new, then this is the first activation on this LoadId in the actual list
			uniqueAssignmentIds.push(assignmentId);
			return false;
		}
		else {
			//IF the loadId is already contained in 'uniqueLoadIds' -> Shared assignment IDs
			return true;
		}
	}
}

////////////////////////////////////////////
//INITIAL CONDITION CHECK (LOADABLE ICON) //
////////////////////////////////////////////

function preCheckLoadabilityOnRefresh(rowIdx) {
	var record = shippingStore.data.items[rowIdx];
	preCheck(record, rowIdx);
}

//Responsible to check the preconditions
function preCheck(record, rowIdx) {
	//Gets the flags for initial status display,
	var enableUpdateNettoMeasureData = record.get('enableUpdateNettoMeasureData'); //loadable bulk case
	var enableUpdateOrderItemLoadedQuantity = record.get('enableUpdateOrderItemLoadedQuantity'); //loadable bag case
	
	if (enableUpdateNettoMeasureData || enableUpdateOrderItemLoadedQuantity) {
		drawLoadableIcon(rowIdx);	
	}
}

function drawLoadableIcon(rowIdx) {
	var innerHTML = getLoadableIcon();
	setInnerHTMLOfField(rowIdx, innerHTML, GRID_LOADABLE_DIV_ID_PREFIX);
}

/////////////////////////////////////
//GRID SHARED LOAD (TOUR) HANDLING //
/////////////////////////////////////

//This method has to be called initially for all rows but only for once!!!
function findSharedLoad(record, index) {
	var isSharedLoadCase = isSharedLoadNonFirstItem(record);
	isSharedLoad[index] = isSharedLoadCase;
}

//Method to check if a record is only a part of a shared load (Tour case)
//This method has to be called initially for all rows but only for once!!!
function isSharedLoadNonFirstItem(record) {
	var loadId = record.get('loadId');
	
	if (loadId == null) {
		//If loadId is empty then it cannot be a shared load at all.
		return false;
	}
	else {
		//If loadId is not empty then it can still be shared or non-shared load case
		var saveAbleLoadIdIndex = uniqueLoadIds.indexOf(loadId);
		
		if (saveAbleLoadIdIndex == -1) {
			//If the loadId is new, then this is the first activation on this LoadId in the actual list
			uniqueLoadIds.push(loadId);
			return false;
		}
		else {
			//IF the loadId is already contained in 'uniqueLoadIds' -> TOUR CASE shared load
			return true;
		}
	}
}

/////////////////////////////
//INITIAL PROPERTY READING //
/////////////////////////////

// Record properties multiline
function getNonNullProperty(record, propertyName){
	var value = record.get(propertyName);
	if (value != null) {
		return value;
	}
	else {
		return '';
	}
}

//Returns the properties with default linebreak '<br/>'
function getRecordProperties(record, propertyNames){
	var result = '';
	var found = false;
	
	for (i = 0; i < propertyNames.length; i++) {
		var recordValue = getNonNullProperty(record, propertyNames[i]);
		if (recordValue !== '') {
			if (found) {
				result += '<br/>';
			}
			result += recordValue;
			found = true;
		}
	}
	return result;
}

function getArrivalTimes(record){
	var result = '';
	
	var loadingPlannedStart = getNonNullProperty(record, 'loadingPlannedStart');
	var loadingRealStart = getNonNullProperty(record, 'loadingRealStart');
	
	if (loadingPlannedStart != null) {
		result += loadingPlannedStart;
	}
	
	if (loadingRealStart != null) {
		if (loadingPlannedStart != null) {
			result += '<br/>';
		}
		result += loadingRealStart;
	}
	return result;
}

//This method is called initially for all rows.
function getLoadingPointColumnDefault(record, index){
	var loadId = getNonNullProperty(record, 'loadId');
	var loadingpointName = record.get('loadingpointName');
	var loadOrderId = record.get('loadOrderId');
	
	if (loadingpointName == null) {
		loadingpointName = '-';
	}
	if (loadOrderId == null) {
		loadOrderId = '-';
	}
	
	loadIds[index] = loadId;
	
	//These methods has to be called initially for all rows but only for once!!!
	findSharedLoad(record, index); //"shared load (Bag tour case) multirow opening + saving" detection.
	findSharedAssignment(record, index); //"shared assignment group coloring" detection.
	
	return loadingpointName + '<br/>' + loadOrderId;
}

//This method is called initially for all rows.
function getTareWeightMasterData(record){
	var tareWeightT = record.get('tareWeightT');
	var simpleVehicleTareWeightT = record.get('simpleVehicleTareWeightT');
	var trailerTareWeightT = record.get('trailerTareWeightT');
	var containerTareWeightT = record.get('containerTareWeightT');
	var complexSum = false;
	
	if (simpleVehicleTareWeightT != null){
		tareWeightT = simpleVehicleTareWeightT;
	}
	
	var sumTareWeight = getSumTareWeight(record);
	var textSum = tareWeightT;
	
	
	if (sumTareWeight != null && trailerTareWeightT != null){ 
		textSum += ' + ' + trailerTareWeightT;
		complexSum = true;
	}
	if (sumTareWeight != null && containerTareWeightT != null){ 
		textSum += ' + ' + containerTareWeightT;
		complexSum = true;
	}
	
	if (complexSum) {
		return sumTareWeight + '<br/>' + '(' + textSum + ')';
	}
	else {
		if (sumTareWeight == null) {
			return '';
		}
		else {
			return sumTareWeight;
		}
	}
}

//This method is called initially for all rows.
function getDefaultNetWeight(record) {
	if (isBag(record)) {
		return getDefaultNetWeightBag(record);
	}
	else {
		return getDefaultNetWeightBulk(record);
	}
}

function getDefaultNetWeightBag(record) {
	var loadedQuantity = record.get('loadedQuantity');
	var loadUnitTxt = record.get('loadUnitTxt');
	
	if (loadedQuantity != null) {
		return loadedQuantity + '&nbsp;' + loadUnitTxt;
	}
	else {
		return '';
	}
}

function getDefaultNetWeightBulk(record) {
	var netQuantity = getNonNullProperty(record, 'netQuantity');
	var loadUnitTxt = record.get('loadUnitTxt');
	if (netQuantity !== '') {
		return netQuantity + '&nbsp;' + loadUnitTxt;
	}
	else {
		return '';
	}
}

//Returns with the tare weight of Truck + Trailer + Container
function getSumTareWeight(record) {
	var index = shippingStore.indexOf(record);
	
	var sumTareWeight = null;
	
	if (sumTareWeights[index] == null) {
		var tareWeightT = record.get('tareWeightT');
		var simpleVehicleTareWeightT = record.get('simpleVehicleTareWeightT');
		var trailerTareWeightT = record.get('trailerTareWeightT');
		var containerTareWeightT = record.get('containerTareWeightT');

		if (simpleVehicleTareWeightT != null){
			tareWeightT = simpleVehicleTareWeightT;
		}
		
		sumTareWeight = tareWeightT;
		
		if (sumTareWeight != null && trailerTareWeightT != null){ 
			sumTareWeight += trailerTareWeightT;
		}
		if (sumTareWeight != null && containerTareWeightT != null){ 
			sumTareWeight += containerTareWeightT;
		}
		
		sumTareWeights[index] = sumTareWeight;		
	}
	else {
		sumTareWeight = sumTareWeights[index];
	}
	
	// Round the result to two digits
	sumTareWeight = Math.round(sumTareWeight*100)/100;
	
	return sumTareWeight;
}

//This method is called initially for all rows.
function getGrossWeight(record) {
	var loadTareMeasure = record.get('loadTareMeasure');
	var loadedNetMeasure = record.get('loadedNetMeasure');

	if (loadedNetMeasure == null) {
		//before loading state the gross weight should not be displayed
		return '';
	}
	else {
		//after it was loaded we display the gross weight
		return loadTareMeasure + loadedNetMeasure;
	}
}

//This method is called initially for all rows.
function getChamberNumber(record){
	var chamberNumber = record.get('chamberNumber');
	
	if (chamberNumber != null) {
		return CHAMBER_TITLE + ' ' + chamberNumber;
	}
	else {
		return '';
	}
}

//This method is called initially for all rows.
function getOrderQuantity(record){
	
	var orderedQuantity = getNonNullProperty(record, 'orderedQuantity');
	var orderUnitTxt = getNonNullProperty(record, 'orderUnitTxt');
	var baseUomCd = getNonNullProperty(record, 'baseUomCd');
	var orderedUomCd = getNonNullProperty(record, 'orderedUomCd');
	
	var result = orderedQuantity + '&nbsp;' + orderUnitTxt;
	
	if (isBulk(record) && baseUomCd != orderedUomCd) {

		var orderedMeasure = record.get('orderedMeasure');
		
		//hint must me added	
		var orderUomDimensionTxt = record.get('orderUomDimensionTxt');
		
		result += '<br/>(' + orderedMeasure  + '&nbsp;' + orderUomDimensionTxt + ')';
	}

	return result;
}

//This method is called initially for all rows.
function getOrderNo(record, rowIdx) {
	var deliverynoteOutputdocId = record.get('deliverynoteOutputdocId');
	var orderNumber = record.get('orderNumber');
	var statusTxt = record.get('statusTxt');
	
	var innerHTMLContent = orderNumber; 
	if (deliverynoteOutputdocId != null) {
		innerHTMLContent += getDownloadPdfAnchor(deliverynoteOutputdocId);
	}
	innerHTMLContent += '<br/>' + statusTxt;
	
	return innerHTMLContent;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//ORDER LIST GRID ROW SELECTION																					//
//																												//
//Selecting an item by on-click populates an ajax call to see the possible actions by filling the innerHTMLs	//
//with dropdowns and input fields. A field is handled "active" if it has been clicked or activated by clicking	//
//on another row which have the same loadId (Tour case).														//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//Responsible to make the "selection + activation" on the clicked row and additionally on all which shares the same loadId.
function handleRowClick(rowIdx, record) {
	//Activate the clicked row
	clickRow(rowIdx);
	
	//Activate the rest of theshared load rows
	var currentLoadId = loadIds[rowIdx];
	if (currentLoadId !== "") {
		//Empty loadId cannot have shared loads
		for (var i = 0; i < loadIds.length; i++) {
			//iterate on all the rest if the items
			if (i != rowIdx && loadIds[i] == currentLoadId) {
				//if the item is not the one which was already activated
				//but has the same loadId, then it's a shared load item
				clickRow(i);
			}
		}
	}
}

//Responsible to make the "selection + activation"
function clickRow(rowIdx) {
	var sharedLoadRecord = shippingStore.data.items[rowIdx];
	if (notActive(rowIdx)) {
		//forces to only call on new rows ...
		activateAjaxCall(sharedLoadRecord, rowIdx);
	}
}

//Responsible to return with the "ShippingOrderLoadBean" for the record to determine the possible actions after row selection
function activateAjaxCall(record, rowIdx) {
	OrderAjaxService.getShippingOrderLoadBean(
		record.get('compositeId'), //parameter for ajax call 
		true, //forces to also check for bimodal. 
		function updateClickedRow(bean) { //callback using caller parameter
			handleUpdateClickedRow(bean, rowIdx, record);
		}
	);
}

//Central On-click event handler, it is called after the service returned with the "ShippingOrderLoadBean" for the record and determines the possible actions.
function handleUpdateClickedRow(bean, rowIdx, record) {		
	var isSharedLoadCase = isSharedLoad[rowIdx];
	
	var enableUpdateLoadingPoint = bean.enableUpdateLoadingPoint;
	var enableLoadContainer = bean.enableLoadContainer;
	var enableCancelWeighSystem = bean.enableCancelWeighSystem;
	var enableTransmitToWeighSystem = bean.enableTransmitToWeighSystem;
	
	//LP Column
	if (bean.wagonNumber != null) {
		editBimodalInfo(bean, rowIdx);
	}
	if (enableUpdateLoadingPoint) {
		if (!isSharedLoadCase) {
			editGridLoadingpoint(record, rowIdx);
		}
	}
	if (enableLoadContainer) {
		editGridContainer(record, rowIdx);
	}
	if (enableCancelWeighSystem) {
		editCancelWeightSystem(record, rowIdx);
	}
	if (enableTransmitToWeighSystem) {
		editTransmitToWeighSystem(record, rowIdx);
	}
	
	//LDS Information edit button
	editLdsInformation(bean, record, rowIdx);
	
	//Tare measure weighted
	editTareMeasureWeighted(bean, record, rowIdx);
	
	//Net Weight
	editNetWeight(bean, record, rowIdx);
	
	//Gross Weight
	editGrossWeight(bean, record, rowIdx);
	
	//Last "Control column"
	updateControlColumn(bean, record, rowIdx, isSharedLoadCase);
	
	activateRow(rowIdx);
}

function notActive(rowIdx) {
	var indexOfRowIdx = selectedRowIndexes.indexOf(rowIdx);
	return indexOfRowIdx == -1;
}

function activateRow(rowIdx) {
	selectedRowIndexes.push(rowIdx);
}

function editLdsInformation(bean, record, rowIdx) {
	var enableUpdateNettoMeasureData = bean.enableUpdateNettoMeasureData;
	var enableUpdateOrderItemLoadedQuantity = bean.enableUpdateOrderItemLoadedQuantity;
	
	if (enableUpdateNettoMeasureData || enableUpdateOrderItemLoadedQuantity) {
		var inputLoadId = record.get('loadId');
		var params = {"formAction":actions.ACTION_EDIT_LDS_INFORMATION, "inputLoadId" : inputLoadId};
		var divId = GRID_LDS_INFO_BTN_DIV_ID_PREFIX + rowIdx;
		var divLP = Ext.get(divId);
		divLP.load({
			url: 'AuftragBearbeitung.do', 
			params: params,
			text: texts.extjsProcessing,
			scripts: true
		});
	}
}

//Returns with the extra content for the LoadingPoint column in case of bimodal order.
function editBimodalInfo(bean, rowIdx) {
	var innerHTML = bean.wagonNumber + '<br/>' + "available: " + bean.availableSpaceInfoBimodal;
	setInnerHTMLOfField(rowIdx, innerHTML, GRID_LP_BIMODAL_DIV_ID_PREFIX);
}

//Returns with the content for the LoadingPoint column
function editGridLoadingpoint(record, rowIdx){
	var orderShippingId = record.get('shippingpointId');
	var orderProductId = record.get('sapMaterialId');
	var inputLoadId = record.get('loadId');
	var selected = dwr.util.getValue('selectedPlantCd');
	var params = {"formAction":actions.CHANGE_LOADINGPOINT, "orderShippingId": orderShippingId, "orderProductId": orderProductId,  "inputLoadId" : inputLoadId, "selectedPlantCd":selected};
	var divId = GRID_LP_DIV_ID_PREFIX + rowIdx;
	var divLP = Ext.get(divId);
	divLP.load({
		url: 'AuftragBearbeitung.do', 
		params: params,
		text: texts.extjsProcessing,
		scripts: true,
		callback: function(el) {
			// LOGON-6715 (Point 5.8)
			// If only one loading point is available, preselect it.
			// This is the case if the number of options is 2 ("Select..." + the single loading point)
			var options = el.select('select[name=loadingpointId] > option');
			if (options.getCount() == 2) {
				options.elements[1].selected = true;
			}
		}
	});
}

//Returns with the content for the LoadingPoint column
function editGridContainer(record, rowIdx){
	var innerHTML = getLoadContainerAnchor(record);
	setLPDivInnerHTML(rowIdx, innerHTML);
}

//Returns with the content for the LoadingPoint column
function editCancelWeightSystem(record, rowIdx){
	var innerHTML = getCancelAnWeighSystemAnchor(record);
	setLPDivInnerHTML(rowIdx, innerHTML);
}

//Returns with the content for the LoadingPoint column
function editTransmitToWeighSystem(record, rowIdx){
	var innerHTML = getTransmitToWeighSystemAnchor(record);
	setLPDivInnerHTML(rowIdx, innerHTML);
}

//Returns with the content for the Tara measure column
function editTareMeasureWeighted(bean, record, rowIdx){
	var innerHTML = '';
	var sumTareWeight = getSumTareWeight(record);
	var loadTareMeasure = getNonNullProperty(record, 'loadTareMeasure');
	
	if (isBulk(record) && !isOrderedUomMTQ(record)) {
		//If bulk with non-MTQ input
		innerHTML = editTareMeasureWeightedInputCase(bean, record, sumTareWeight, loadTareMeasure);
	}
	else {
		innerHTML = editTareMeasureWeightedLabelCase(sumTareWeight, loadTareMeasure);
	}
	setInnerHTMLOfField(rowIdx, innerHTML, GRID_TARE_MEASURE_WEIGHTED_DIV_ID_PREFIX);
}

//Input case is for bulk orders with non MTQ orderedUomCd
function editTareMeasureWeightedInputCase(bean, record, sumTareWeight, loadTareMeasure) {
	var innerHTML = '';
	var loadId = record.get('loadId');
	
	innerHTML = '<input name="tareMasterDataMeasure" id="' + loadId + 'tareMasterDataMeasure" type="hidden" value="' + sumTareWeight + '"/>';
	
	var enableUpdateTareMeasureData = bean.enableUpdateTareMeasureData;
	
	if (enableUpdateTareMeasureData) {
		innerHTML += getUpdateGrossOrNettoField(record, "inputTaraMeasure", "tare", loadTareMeasure);
	}
	else {
		innerHTML += loadTareMeasure;
	}
	return innerHTML;
}

//Label case is for bag and for MTQ orderedUomCd bulk orders
function editTareMeasureWeightedLabelCase(sumTareWeight, loadTareMeasure) {
	var innerHTML = '';
	if (loadTareMeasure === '') {
		if (sumTareWeight == null) {
			innerHTML = "";
		}
		else {
			innerHTML = sumTareWeight;
		}
	}
	else {
		innerHTML = loadTareMeasure;
	}
	return innerHTML;
}

// LOGON-6715 point 5.2 UOM 
//Returns with the content for the Net Weight column
function customCallToNetUpdate(loadId, elementId) {
	var netValue = Ext.get(elementId).prev().dom.value;
	updateGrossOrNetto(loadId, 'net', netValue);
	
}

function bagChangeUomSelect(elemid, select) {
	var hInput = Ext.get(elemid).dom;
	hInput.value = select.value;
}

function editNetWeight(bean, record, rowIdx) {
	var innerHTML = '', _select, enableUpdateNettoMeasureData, i, loadedUomCd, selectedInfo, baseUomCd;
	var loadId = record.get('loadId');
	var preselectedUom = '';
	if (isBulk(record)) {
		if (isOrderedUomMTQ(record)) {
			//Bulk in cubic meters
			enableUpdateNettoMeasureData = bean.enableUpdateNettoMeasureData;
			if(enableUpdateNettoMeasureData) {
				var loadedQuantity = getNonNullProperty(record, 'loadedQuantity');
				var orderItemId = getNonNullProperty(record, 'orderItemId');
				loadedUomCd = getNonNullProperty(record, 'loadedUomCd');
				
				var fieldId = loadId + 'inputOrderItemLoadedQuantity';
				innerHTML = '<input style="width:50px" name="' + fieldId + '" id="' + fieldId +  '" type="text" value="' + loadedQuantity + '" maxlength="' + LOADED_QUANTITY_MAX_SIZE + '"/>';// + UOM_MTQ_TXT;
				
				var hiddenFieldId = loadId + 'inputOrderItemLoadedUomCd';
				
				baseUomCd = getNonNullProperty(record, 'baseUomCd');
				_select = '<select id="' + loadId + 'mtqChooseUomSelect" onchange="bagChangeUomSelect(\'' + hiddenFieldId + '\', this)" style="width: 66px;">';
				
				for(i = 0; i < bean.unitsOfMeasurement.length; i++){
					selectedInfo = '';					
					if(bean.unitsOfMeasurement[i][3]) {
						selectedInfo = 'selected';
						preselectedUom = bean.unitsOfMeasurement[i][0];
					}
					_select += '<option value="' + bean.unitsOfMeasurement[i][0] + '" data-tonfactor="' + bean.unitsOfMeasurement[i][1] + '" ' + selectedInfo + '>' + bean.unitsOfMeasurement[i][0] + '</option>';
				}
				_select += '</select>';
				innerHTML += _select;
				innerHTML += '<input name="' + hiddenFieldId + '" id="' + hiddenFieldId + '" type="hidden" value="' + preselectedUom + '" maxlength="' + LOADED_UOM_CD_MAX_SIZE + '"/>';	
			}
		}
		else {
			enableUpdateNettoMeasureData = bean.enableUpdateNettoMeasureData;
			
			if (enableUpdateNettoMeasureData) {
				var netQuantity = getNonNullProperty(record, 'netQuantity');
				loadedUomCd = getNonNullProperty(record, 'loadedUomCd');
				baseUomCd = getNonNullProperty(record, 'baseUomCd');
				innerHTML = getUpdateGrossOrNettoField(record, "inputNettoMeasure", "net", netQuantity);
				var _selectId = loadId + 'selectNetQtyUom';
				_select = '<select id="' + _selectId + '" onchange="customCallToNetUpdate(\'' + loadId + '\', \'' + _selectId + '\');" style="width: 50px;" name="myUom">';
				for(i = 0; i < bean.unitsOfMeasurement.length; i++){
					selectedInfo = '';
					if(bean.unitsOfMeasurement[i][3]) {
						selectedInfo = 'selected';
						selectedUom = bean.unitsOfMeasurement[i][0];
					}
					_select += '<option value="' + bean.unitsOfMeasurement[i][0] + '" data-tonfactor="' + bean.unitsOfMeasurement[i][1] + '" ' + selectedInfo + '>' + bean.unitsOfMeasurement[i][0] + '</option>';
				}
				_select += '</select>';
				innerHTML += _select;
			}
			else {
				innerHTML = getDefaultNetWeightBulk(record);
			}			
		}
		
	}
	
	if (isBag(record)) {
		var enableUpdateOrderItemLoadedQuantity = bean.enableUpdateOrderItemLoadedQuantity;
		
		if (enableUpdateOrderItemLoadedQuantity) {
			var loadedQuantityBag = getNonNullProperty(record, 'loadedQuantity');
			var orderItemIdBag = getNonNullProperty(record, 'orderItemId');
			var loadIdBag = record.get('loadId');
			var fieldIdBag = loadIdBag + 'inputOrderItemLoadedQuantity' + orderItemIdBag;
			
			innerHTML = '<input style="width:50px" name="' + fieldIdBag + '" id="' + fieldIdBag +  '" type="text" value="' + loadedQuantityBag + '" maxlength="' + LOADED_QUANTITY_MAX_SIZE + '"/>';
		
			var orderedUomFuzzy = record.get('orderedUomFuzzy');
			
			var hiddenfieldIdBag = loadIdBag + 'inputOrderItemLoadedUomCd' + orderItemIdBag;
			
			if (orderedUomFuzzy){
				var baseUnitTxt = getNonNullProperty(record, 'baseUnitTxt');
				innerHTML += baseUnitTxt;
				var baseUomCd2 = getNonNullProperty(record, 'baseUomCd');
				innerHTML += '<input name="' + hiddenfieldIdBag + '" id="' + hiddenfieldIdBag + '" type="hidden" value="' + baseUomCd2 + '" maxlength="' + LOADED_UOM_CD_MAX_SIZE + '"/>';
			}
			else {
				var orderedUomCd = getNonNullProperty(record, 'orderedUomCd');
				
				loadedUomCd = getNonNullProperty(record, 'loadedUomCd');
				baseUomCd = getNonNullProperty(record, 'baseUomCd');
				_select = '<select id="bagChooseUomSelect" name="bagChooseUomSelect" onchange="bagChangeUomSelect(\'' + hiddenfieldIdBag + '\', this)" style="width: 66px;">';
				for(i = 0; i < bean.unitsOfMeasurement.length; i++){
					selectedInfo = '';					
					if(bean.unitsOfMeasurement[i][3]) {
						selectedInfo = 'selected';
						preselectedUom = bean.unitsOfMeasurement[i][0];
					}
					_select += '<option value="' + bean.unitsOfMeasurement[i][0] + '" data-tonfactor="' + bean.unitsOfMeasurement[i][1] + '" ' + selectedInfo + '>' + bean.unitsOfMeasurement[i][0] + '</option>';
				}
				_select += '</select>';
				innerHTML += _select;
				innerHTML += '<input name="' + hiddenfieldIdBag + '" id="' + hiddenfieldIdBag + '" type="hidden" value="' + preselectedUom + '" maxlength="' + LOADED_UOM_CD_MAX_SIZE + '"/>';
			}
		}
		else {
			innerHTML = getDefaultNetWeightBag(record);
		}
	}
	
	setInnerHTMLOfField(rowIdx, innerHTML, GRID_NET_WEIGHT_DIV_ID_PREFIX);
}

//Returns with the content for the Gross Weight column
function editGrossWeight(bean, record, rowIdx) {
	var innerHTML = '';
	
	var loadBruttoMeasure = getGrossWeight(record);
	var enableUpdateTareMeasureData = bean.enableUpdateTareMeasureData;
	
	if (isBulk(record) && enableUpdateTareMeasureData && !isOrderedUomMTQ(record)) {
		//If loadable bulk with non-MTQ input
		innerHTML = getUpdateGrossOrNettoField(record, "grossMeasure", "gross", loadBruttoMeasure);
	}
	else {
		innerHTML = loadBruttoMeasure;
	}
	setInnerHTMLOfField(rowIdx, innerHTML, GRID_GROSS_WEIGHT_DIV_ID_PREFIX);
}

//Sets the inner HTML of a DIV element ot the grid 
function setInnerHTMLOfField(rowIdx, innerHTML, prefix) {
	var divId = prefix + rowIdx;
	var div = Ext.getDom(divId);
	div.innerHTML = innerHTML;
}

//Sets the inner HTML of the Loading point DIV element ot the grid
function setLPDivInnerHTML(rowIdx, innerHTML) {
	setInnerHTMLOfField(rowIdx, innerHTML, GRID_LP_DIV_ID_PREFIX);
	var divId = GRID_LP_DIV_ID_PREFIX + rowIdx;
}

//Generates the content for the last column
function updateControlColumn(bean, record, rowIdx, isSharedLoadCase) {
	var innerHTML = getControlColumnContent(record, bean, rowIdx, isSharedLoadCase);
	setInnerHTMLOfField(rowIdx, innerHTML, GRID_CONTROL_DIV_ID_PREFIX);
}

function getControlColumnContent(record, bean, rowIdx, isSharedLoadCase) {
	var enableUpdateNettoMeasureData = bean.enableUpdateNettoMeasureData;
	var enableUpdateOrderItemLoadedQuantity = bean.enableUpdateOrderItemLoadedQuantity;
	
	if (enableUpdateNettoMeasureData || enableUpdateOrderItemLoadedQuantity) {

		if (enableUpdateNettoMeasureData) {
			return getControlContentByType(record, 'bulk', rowIdx, isSharedLoadCase);
		}
		
		if (enableUpdateOrderItemLoadedQuantity) {
			return getControlContentByType(record, 'bag', rowIdx, isSharedLoadCase);
		}
	}
	else {
		return getGeneratePdfAnchor(record);
	}
}

function getControlContentByType(record, type, rowIdx, isSharedLoadCase) {
	if (isSharedLoadCase) {
		return getResetRowAnchor(record) + '<br/>' + getGeneratePdfAnchor(record, rowIdx) + getSetLoadedAsPlannedAnchor(record, type);
	}
	else {
		return getResetRowAnchor(record) + getSaveRowAnchor(record, type) + '<br/>' + getGeneratePdfAnchor(record, rowIdx) + getSetLoadedAsPlannedAnchor(record, type);		
	}
}

//////////////////////////////////
//ORDER LIST LINKS AND UTILITIES//
//////////////////////////////////

function isBulk(record) {
	var logonPackageTypeCd = record.get('logonPackageTypeCd');
	return logonPackageHandlingHelper.isBulk(logonPackageTypeCd);
}

function isBag(record) {
	var logonPackageTypeCd = record.get('logonPackageTypeCd');
	return logonPackageHandlingHelper.isBag(logonPackageTypeCd);
}

function isOrderedUomMTQ(record) {
	var baseUomCd = record.get('baseUomCd');
	return baseUomCd == UOM_MTQ;
}

function getOrderNotes(record) {
	return ORDER_NOTES_TITLE + lineBreak + getNonNullProperty(record, 'noteRailwayDisp');
}

function getLoadContainerAnchor(record) {
	var orderItemId = record.get('orderItemId');
	return '<img onclick="selectContainer(' + orderItemId + ');" src="' + LOAD_CONTAINER_IMAGE_URL + '" title="' + LOAD_CONTAINER_TITLE + '">';
}

function getCancelAnWeighSystemAnchor(record) {
	var loadId = record.get('loadId');
	return '<img onclick="cancelAnWeighSystem(' + loadId + ');" src="' + CANCEL_WEIGHT_SYSTEM_IMAGE_URL + '" title="' + CANCEL_WEIGHT_SYSTEM_TITLE + '">';
}

function getTransmitToWeighSystemAnchor(record) {
	var loadId = record.get('loadId');
	return '<a id="' + loadId + 'transmitOrder" onclick="transmitToWeighSystem(\'' + loadId + '\');"><img src="' + TRANSMIT_TO_WEIGHT_SYSTEM_IMAGE_URL + '" title="' + TRANSMIT_TO_WEIGHT_SYSTEM_TITLE + '"</a>';
}

function getUpdateGrossOrNettoField(record, name, type, value) {
	var loadId = record.get('loadId');
	return '<input style="width:50px" onchange="updateGrossOrNetto(\'' + loadId + '\', \'' + type + '\',this.value);" name="' + name + '" id="' + loadId + name + '" type="text" value="' + value + '" maxlength="' + NET_WEIGHT_MAX_SIZE + '"/>';
}

function getResetRowAnchor(record) {
	var loadId = record.get('loadId');
	var netQuantity = getNonNullProperty(record, 'netQuantity');
	var loadTareMeasure = getNonNullProperty(record, 'loadTareMeasure');
	var loadingpointId = record.get('loadingpointId');
	return '<img onclick="resetRow(' + loadId + ',\''  + netQuantity + '\',\'' + loadTareMeasure + '\',\'' +  loadingpointId + '\');" src="' + RESET_ROW_IMAGE_URL + '" title="' + RESET_ROW_TITLE + '">';
}

function getSaveRowAnchor(record, type) {
	var baseUomCd = record.get('baseUomCd');
	var loadId = record.get('loadId');
	var loadingOnCurrentDay = record.get('loadingOnCurrentDay');
	return '<img onclick="confirmLoading(' + loadId + ',\'' + type + '\',\'' + baseUomCd + '\',' + loadingOnCurrentDay + ');" src="' + SAVE_ROW_IMAGE_URL + '" title="' + SAVE_ROW_TITLE + '">';
}

//This method is called initially for all rows.
function getGeneratePdfAnchor(record, rowIdx) {
	var orderId = record.get('orderId');
	return '<img onclick="generatePdf(\'' + orderId + '\',\'' + GENERATE_PDF_PARAMETER + '\');" src="' + GENERATE_PDF_IMAGE_URL + '" title="' + GENERATE_PDF_TITLE + '">';
}

function getSetLoadedAsPlannedAnchor(record, type) {
	var baseUomCd = record.get('baseUomCd');
	var loadId = record.get('loadId');
	var plannedLoadingTime = record.get('loadingPlannedStart');
	return '<img onclick="setLoadedAsPlanned(' + loadId + ',\'' + type + '\',\'' + baseUomCd + '\',\'' + plannedLoadingTime + '\');" src="' + CALENDAR_IMAGE_URL + '" title="' + CALENDAR_TITLE + '">';
}

function getDownloadPdfAnchor(deliverynoteOutputdocId) {
	return '<a href="/logon/order/blob?keyValue=' + deliverynoteOutputdocId + '&entity=OutputdocEntity&keyName=outputdocId" target="_blank"><img src="' + GENERATE_PDF_IMAGE_URL + '" title="' + DOWNLOAD_PDF_TITLE + '"></a>';
}

function getLoadableIcon() {
	return '<img src="' + LOADABLE_ICON_IMAGE_URL + '" title="' + shippingHeaders.loadable + '">';
}

