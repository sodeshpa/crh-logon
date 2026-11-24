Ext.onReady(function() {

// --- Data record and DataStore definitions ---
var manPoolAssignmentSearchRec = Ext.data.Record.create([
		{name: "manPoolAssignmentListId"},
		{name: "orderId"},
		{name: "orderNumber"},
		{name: "soldtoName1"},
		{name: "shiptoName1"},
		{name: "materialTxt"},
		{name: "deliveryTypeTxt"},	    
		{name: "shippingpointName"},
		{name: "deliveryFrom"},
		{name: "deliveryTo"},
		{name: "autoPoolAssignmentFlag"},
		{name: "poolId"},
		{name: "poolName"},
		{name: "autoCarrierAssignmentFlag"},
		{name: "commCarrierId"},
		{name: "commCarrierName"}
	]);

var manPoolAssignmentSearchStore = new Ext.data.Store({
	proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { ManualPoolAssignmentAjaxService.searchPoolAssignments(skip, count, sort, searchArea.searchFilters, callback) }, true),
	reader: new Ext.data.ListRangeReader({id:'manPoolAssignmentListId', totalProperty:'totalSize'}, manPoolAssignmentSearchRec), 
	remoteSort: true
});

var poolRec = Ext.data.Record.create([
		{name: 'poolId', mapping: 'poolTxPK.poolId'},
		{name: "name"}
	]);

var assignablePoolStore = new Ext.data.Store({
	proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { ManualPoolAssignmentAjaxService.fetchAssignablePoolList(skip, count, query, callback) }, true),
	reader: new Ext.data.ListRangeReader({id:'poolId', totalProperty:'totalSize'}, poolRec)
});

var carrierRec = Ext.data.Record.create([
		{name: "carrierId"},
		{name: "carrierName"}
	]);

var assignableCarrierStore = new Ext.data.Store({
	proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { ManualPoolAssignmentAjaxService.fetchAssignableCarrierList(skip, count, sort, query, callback) }, true),
	reader: new Ext.data.ListRangeReader({id:'carrierId', totalProperty:'totalSize'}, carrierRec)
});

// --- Search Area Handler Obj ---
var searchArea = {
	pageSize: 25,
	searchFilters: {},
	search: function(filters) {
		this.searchFilters = filters;
		manPoolAssignmentSearchStore.load({params:{start: 0, limit: this.pageSize, sort: '', query: filters}});
	}
}

var operationsBar = new Ext.Toolbar([
		{id: 'saveButton', text: texts.buttonSave},
		{xtype: 'tbseparator'},
		{id: 'resetButton', text: texts.buttonReset}
]);

// ------ PAGER ---------
var pagingBar = new Ext.ux.grid.PagingToolbar({
	pageSize: searchArea.pageSize,
	store: manPoolAssignmentSearchStore,
	displayInfo: true,
	displayMsg: texts.pagerDisplayMsg,
	emptyMsg: texts.pagerEmptyMsg
});

// ------ GRID ----------
Ext.ns("Ext.ux.renderer");

Ext.ux.renderer.ComboRenderer = function(options) {
	var value = options.value;
	var combo = options.combo;
	var record = options.record;
	var defaultDataIndex = options.defaultDataIndex;

	var returnValue;
	var valueField = combo.valueField;
	
	var idx = combo.store.findBy(function(record) {
		if(record.get(valueField) == value) {
			returnValue = record.get(combo.displayField);
			return true;
		}
	});
	
	if(!returnValue) {
		if (value == null) {
			return '...';
		}
		else {
			return record.get(defaultDataIndex);
		}
	}
	
	return returnValue;
};

Ext.ux.renderer.Combo = function(combo, defaultDataIndex) {
	return function(value, meta, record) {
		return Ext.ux.renderer.ComboRenderer({value: value, meta: meta, record: record, combo: combo, defaultDataIndex: defaultDataIndex});
	};
}

var poolCombo = new Ext.form.ComboBox({
	mode: 'local',
	id: 'poolCombo',
	store: assignablePoolStore,
	displayField: 'name',
	valueField: 'poolId',
	pageSize: 20,
	width: 180,
	listWidth: 250,
	typeAhead: false,
	triggerAction: 'all',
	editable: false,
	allowBlank: true,
	forceSelection: true,
	selectOnFocus: true,
	lazyInit: false
});
poolCombo.store.load({params:{start: 0, limit: poolCombo.pageSize, sort: '', query: { }}});

var carrierCombo = new Ext.form.ComboBox({
	mode: 'local',
	valueNotFoundText: '',
	id: 'carrierCombo',
	store: assignableCarrierStore,
	displayField: 'carrierName',
	valueField: 'carrierId',
	pageSize: 20,
	width: 180,
	listWidth: 250,
	typeAhead: false,
	triggerAction: 'all',
	editable: false,
	allowBlank: true,
	forceSelection: true,
	lazyInit: false
});

var grid;
var autoPoolAssignmentColumn = new Ext.ux.grid.CheckColumn({
	id: 'autoPoolAssignmentFlag',
	header:	texts.autoPoolAssignmentFlag, 
	dataIndex: 'autoPoolAssignmentFlag', 
	sortable: false,
	editable: true,
	width: 80
});
var autoCarrierAssignmentColumn = new Ext.ux.grid.CheckColumn({
	id: 'autoCarrierAssignmentFlag',
	header:	texts.autoCarrierAssignmentFlag, 
	dataIndex: 'autoCarrierAssignmentFlag',
	sortable: false,
	editable: true,
	width: 80
});

var colModel = new Ext.grid.ColumnModel({
	// grid columns
	columns:[
	{
		id: 'soldtoName1', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
		header: texts.soldtoName,
		dataIndex: 'soldtoName1',
		sortable: false
	},{
		id: 'orderNumber', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
		header: texts.orderNumber,
		dataIndex: 'orderNumber',
		sortable: false,
		renderer: function(val, meta, rec) {
			return '<a href="javascript:showPopup(\'' + orderDetailBaseUrl + '?orderid=' + rec.data.orderId + '\')">' + val + '</a>';            
		}
	},{
		id: 'shiptoName1', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
		header: texts.shiptoName,
		dataIndex: 'shiptoName1',
		sortable: false        
	},{
		id: 'materialTxt', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
		header: texts.materialTxt,
		dataIndex: 'materialTxt',
		sortable: false
	},{
		id: 'deliveryTypeTxt',
		header: texts.deliveryTypeTxt,
		dataIndex: 'deliveryTypeTxt',
		sortable: false
	},{
		id: 'shippingpointName', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
		header: texts.shippingpointName,
		dataIndex: 'shippingpointName',
		sortable: false
	},{
		id: 'deliveryFrom', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
		header: texts.deliveryFrom,
		dataIndex: 'deliveryFrom',
		sortable: false,
		width: 80
	},{
		id: 'deliveryTo', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
		header: texts.deliveryTo,
		dataIndex: 'deliveryTo',
		sortable: false,
		width: 80
	},
	autoPoolAssignmentColumn
	,{
		id: 'poolId', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
		header: texts.poolName,
		dataIndex: 'poolId',
		editor: poolCombo,
		renderer: Ext.ux.renderer.Combo(poolCombo, 'poolName'),
		sortable: false
	},
	autoCarrierAssignmentColumn
	,{
		id: 'carrierId', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
		header: texts.carrierName,
		dataIndex: 'carrierId',
		editor: carrierCombo,
		renderer: Ext.ux.renderer.Combo(carrierCombo, 'carrierName'),
		sortable: false
	}
	],
	
	// please see http://extjs.com/forum/showthread.php?t=43576 for a more elegant solution (without a reference to our grid here).
	isCellEditable: function(colIndex, rowIndex) {
	
		var record = grid.store.getAt(rowIndex);
		var field = this.getDataIndex(colIndex);
		if ( (field == 'poolId') || (field == 'carrierId') || (field == 'autoCarrierAssignmentFlag') ) {
			if (!record.get('autoPoolAssignmentFlag')) {
				return false;
			}
			else if ( (!record.get('autoCarrierAssignmentFlag')) && (field == 'carrierId') ) {
				return false;
			}				
			else if (!record.get('poolId') && (field == 'autoCarrierAssignmentFlag')) {
				return false;
			}
		}
		return Ext.grid.ColumnModel.prototype.isCellEditable.call(this, colIndex, rowIndex);
	}
});

function createGrid() {
	grid = new Ext.grid.EditorGridPanel({
		id: 'searchGrid',
		renderTo:'divSearchResults',
		width: Ext.get('divSearchResults').getWidth(),
		autoHeight: true,
		enableColumnHide: true,
		viewConfig: {autoFill: true, forceFit: true},        
		stripeRows: true,
		title: texts.gridtitle,
		store: manPoolAssignmentSearchStore,
		pageSize: searchArea.pageSize,
		frame: true,
		loadMask: true,
		clicksToEdit: 1,
		colModel: colModel,
		// paging bar on the bottom
		bbar: pagingBar,
		tbar: operationsBar,
		plugins: [autoPoolAssignmentColumn, autoCarrierAssignmentColumn]
	});
	
	// --- Editor Handler Obj ---
	ManPoolAssignmentList = Ext.extend(Ext.ux.grid.EditableList, {
		callServiceForSave : function(changedRecords, callbackForSave) {
			ManualPoolAssignmentAjaxService.saveOrders(
				changedRecords, {callback: callbackForSave});
		}, 

		validateRecords: function(pRecords, pMessages) {
			for (var index = 0; index < pRecords.length; index++) {
				var mRecord = pRecords[index];
				var autoPoolAssignmentFlag = mRecord.get('autoPoolAssignmentFlag');
				var poolId = mRecord.get('poolId');
				var autoCarrierAssignmentFlag = mRecord.get('autoCarrierAssignmentFlag');
				var carrierId = mRecord.get('carrierId');
				var recordDesc = mRecord.get("soldtoName1") + '/' + mRecord.get("shiptoName1") + '/' + mRecord.get("materialTxt") + '/' + mRecord.get("deliveryTypeTxt") + '/' + mRecord.get("shippingpointName") + '/' + mRecord.get("deliveryFrom") + '/' + mRecord.get("deliveryTo");
				
				if ( (autoPoolAssignmentFlag) && (!poolId) ) {
					pMessages.add(new Ext.ux.valid.ValidationMessage(Ext.ux.valid.ValidationMessage.TYPE_ERROR, null, null, "Pool not set for " + recordDesc));
				}
				if ( (autoCarrierAssignmentFlag) && (!carrierId) ) {
					pMessages.add(new Ext.ux.valid.ValidationMessage(Ext.ux.valid.ValidationMessage.TYPE_ERROR, null, null, "Carrier not set for " + recordDesc));
				}
			}
		}
	});

	var editor = new ManPoolAssignmentList();
	
	editor.init({
		grid:				grid, 
		pageSize:			pagingBar.getPageSize(), 
		saveButtonId:		'saveButton', 
		resetButtonId:		'resetButton', 
		pageChangeTitle:    texts.listPageChangeTitle,
		pageChangeMessage:  texts.listPageChangeMessage,
		maskMessage:        texts.listMaskMessage,
		noChangesMessage:   texts.listNoChangesMessage,
		buttonYes:          texts.listButtonYes,
		buttonNo:           texts.listButtonNo,
		buttonCancel:       texts.listButtonCancel
	});

	grid.on('beforeedit', function(e) {
		var record = e.record;
		var field = e.field;

		if (field == 'carrierId') { // edit the carrier
			var currentPoolId = record.get('poolId');
			if (currentPoolId) {
				carrierCombo.store.load({params:{start: 0, limit: carrierCombo.pageSize, sort: '', query: { poolId: currentPoolId }}});
			}
		}
	});
	
	grid.on('afteredit', function(e) {
		var record = e.record;
		var field = e.field;
		
		if (field == 'poolId') {
			record.set('carrierId', null);
		}
		if (field == 'autoPoolAssignmentFlag') {
			if (!record.get('autoPoolAssignmentFlag')) {
				record.set('autoCarrierAssignmentFlag', false);
			}
		}
	});    

	grid.render();
}


	createGrid();
	searchArea.search({});
});