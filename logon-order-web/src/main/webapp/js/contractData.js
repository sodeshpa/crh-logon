var dateTimeFormat = 'd.m.y H:i:s';
var dateFormat = 'd.m.Y';
var contractDataRecordType = Ext.data.Record.create([
	{name: "sapContractId"},		
	{name: "sapContractNo"},
	{name: "sapContractTypeCd"},
	{name: "sareaDisplayLongname"},
	{name: "soldtoSapId"},
	{name: "soldtoName"},
	{name: "shiptoSapId"},
	{name: "shiptoSapBpId"},
	{name: "shiptoName"},
	{name: "billtoSapId"},
	{name: "billtoName"},
	{name: "payerSapId"},
	{name: "payerName"},
	{name: "validFromDatInStringWithOffset"},
	{name: "validUntilDatInStringWithOffset"},
	{name: "validUntilIgnoreFlag"},
	{name: "sapShippingCondCd"},
	{name: "custPurchaseOrderNo"},
	{name: "quantityControlFlag"},
	{name: "currencyCd"},
	{name: "creditControlAreaCd"},
	{name: "creditLimitAccountNo"},
	{name: "customerCreditGroup"},
	{name: "creditManagementGroup"},
	{name: "creditRiskCategory"},
	{name: "creditCurrencyCd"},
	{name: "salesDistrictCd"},
	{name: "validationCode"},
	{name: "incotermsCd"},
	{name: "ctlCreTs", type: 'date', dateFormat: dateTimeFormat},
	{name: "ctlModTs", type: 'date', dateFormat: dateTimeFormat}
]);


var conrtactColumns = [
	{id: 'sapContractNo',		header: texts.sapContractNo,			dataIndex: 'sapContractNo',						sortable: true, width: 40},
	{id: 'sapContractTypeCd',	header: texts.sapContractTypeCd,		dataIndex: 'sapContractTypeCd',					sortable: true, width: 30},
	{id: 'sareaDisplayLongname',header: texts.sareaDisplayLongname,		dataIndex: 'sareaDisplayLongname',				sortable: true, width: 30},
	{id: 'soldtoSapId',			header: texts.soldtoSapId,				dataIndex: 'soldtoSapId',						sortable: true, width: 30, hidden: true},
	{id: 'soldtoName',			header: texts.soldtoName,				dataIndex: 'soldtoName',						sortable: true, width: 30},
	{id: 'shiptoSapId',			header: texts.shiptoSapId,				dataIndex: 'shiptoSapId',						sortable: true, width: 30, hidden: true},
	{id: 'shiptoName',			header: texts.shiptoName,				dataIndex: 'shiptoName',						sortable: true, width: 30},
	{id: 'billtoSapId',			header: texts.billtoSapId,				dataIndex: 'billtoSapId',						sortable: true, width: 30, hidden: true},
	{id: 'billtoName',			header: texts.billtoName,				dataIndex: 'billtoName',						sortable: true, width: 30},
	{id: 'payerSapId',			header: texts.payerSapId,				dataIndex: 'payerSapId',						sortable: true, width: 30, hidden: true},
	{id: 'payerName',			header: texts.payerName,				dataIndex: 'payerName',							sortable: true, width: 30},
	{id: 'validFromDat',		header: texts.validFromDat,				dataIndex: 'validFromDatInStringWithOffset',	sortable: true, width: 30},
	{id: 'validUntilDat',		header: texts.validUntilDat,			dataIndex: 'validUntilDatInStringWithOffset',	sortable: true, width: 30},
	{id: 'validUntilIgnoreFlag',header: texts.validUntilIgnoreFlag,		dataIndex: 'validUntilIgnoreFlag',				sortable: true, width: 30, hidden: true},
	{id: 'sapShippingCondCd',	header: texts.sapShippingCondCd,		dataIndex: 'sapShippingCondCd',					sortable: true, width: 30},
	{id: 'custPurchaseOrderNo',	header: texts.custPurchaseOrderNo,		dataIndex: 'custPurchaseOrderNo',				sortable: true, width: 30, hidden: true},
	{id: 'quantityControlFlag',	header: texts.quantityControlFlag,		dataIndex: 'quantityControlFlag',				sortable: false, width: 30}, //sorting is disabled as this flag is transformed to true/false from 4 different values. 0, 2 and 3 = true , 1 = false. This mapping would break the ordering and would cause confusion. LOGON-8039 
	{id: 'currencyCd',			header: texts.currencyCd,				dataIndex: 'currencyCd',						sortable: true, width: 30},
	{id: 'creditControlAreaCd',	header: texts.creditControlAreaCd,		dataIndex: 'creditControlAreaCd',				sortable: true, width: 30, hidden: true},
	{id: 'creditLimitAccountNo',header: texts.creditLimitAccountNo,		dataIndex: 'creditLimitAccountNo',				sortable: true, width: 30, hidden: true},
	{id: 'customerCreditGroup',	header: texts.customerCreditGroup,		dataIndex: 'customerCreditGroup',				sortable: true, width: 30, hidden: true},
	{id: 'creditManagementGroup',header: texts.customerCreditGroup,		dataIndex: 'customerCreditGroup',				sortable: true, width: 30, hidden: true},
	{id: 'creditRiskCategory',	header: texts.creditRiskCategory,		dataIndex: 'creditRiskCategory',				sortable: true, width: 30, hidden: true},
	{id: 'creditCurrencyCd',	header: texts.creditCurrencyCd,			dataIndex: 'creditCurrencyCd',					sortable: true, width: 30, hidden: true},
	{id: 'salesDistrictCd',		header: texts.salesDistrictCd,			dataIndex: 'salesDistrictCd',					sortable: true, width: 30},
	{id: 'validationCode',		header: texts.validationCode,			dataIndex: 'validationCode',					sortable: true, width: 30, hidden: true},
	{id: 'incotermsCd',			header: texts.incotermsCd,				dataIndex: 'incotermsCd',						sortable: true, width: 30},
	{id: 'ctlCreTs',			header: texts.ctlCreTs,					dataIndex: 'ctlCreTs',							sortable: true, width: 50, hidden: true, renderer: Ext.util.Format.dateRenderer(dateTimeFormat)},
	{id: 'ctlModTs',			header: texts.ctlModTs,					dataIndex: 'ctlModTs',							sortable: true, width: 50, hidden: true, renderer: Ext.util.Format.dateRenderer(dateTimeFormat)}
					];

var contractItemGrid;
var contractDataGrid;

var contractDataStore = new Ext.data.Store({
	proxy: new Ext.data.DWRProxyForPager(
			function(skip, count, sort, query, callback) { 
				var showOnlyActiveContracts = document.getElementById("showOnlyActiveContracts").checked;
				ContractDataAjaxService.searchContractData(skip, count, sort, contractData.searchFilters, showOnlyActiveContracts, callback); 
			}, true),
	reader: new Ext.data.ListRangeReader({id: 'sapContractId', totalProperty: 'totalSize'}, contractDataRecordType), 
	remoteSort: true
});

var contractData = {
	pageSize: 10,
	searchFilters: {},
	searchContractData: function(filters) {
		this.searchFilters = filters;
		contractDataStore.load({params: {start: 0, limit: this.pageSize, sort: '', query: filters} });
	}
};

var pagingBar = new Ext.ux.grid.PagingToolbar({
	pageSize: contractData.pageSize,
	store: contractDataStore,
	displayInfo: true,
	displayMsg: texts.pagerDisplayMsg,
	emptyMsg: texts.pagerEmptyMsg
});

function createGrid() {
	contractDataGrid = new Ext.grid.GridPanel({
		id: 'contractDataGrid',
		renderTo:'divContracts',
		height: 300,
		enableColumnHide: true,
		viewConfig: {autoFill: true, 
					 forceFit: true,
					 getRowClass: function(row, index){
						if(row.data.shiptoSapBpId != selectedShipTo){
							return 'x-grid3-row-italic';		
						}
					 }
		},
		stripeRows: true,
		title: texts.contractDataGridTitle,
		store: contractDataStore,
		pageSize: contractData.pageSize,
		frame: true,
		loadMask: true,
		// grid columns		
		columns:conrtactColumns,
		bbar: pagingBar,
		sm: new Ext.grid.RowSelectionModel({
			singleSelect: true,
			listeners: {
				rowselect: function(smObj, rowIndex, record){
					contractItemSearch.searchContractDataItems({sapContractId : record.data.sapContractId});
				},
				selectionchange: function(smObj, rowIndex) {
					
					if(!smObj.hasSelection()){
						contractItemGrid.hide();
						contractItemStore.removeAll();
					}
					else{
						if (!contractItemSearch.isRendered){
							createItemGrid();
							contractItemSearch.isRendered = true;
						}
						else{
							contractItemGrid.show();
						}
					}
					
					showContractItemDataPanel(null, false);
				}
			}
		})
		
	});
	
	contractDataGrid.render();
}

//contract Item table
var contractItemRecordType = Ext.data.Record.create([
			{name: "compositeId"},
			{name: "sapContractItemId"},
			{name: "contractItemNo"},
			{name: "materialTxt"},
			{name: "prodPlantName"},
			{name: "supplyPlantName"},
			{name: "materialGroup1"},
			{name: "materialGroup2"},
			{name: "materialGroup3"},
			{name: "materialGroup4"},
			{name: "materialGroup5"},
			{name: "itemCategory"},
			{name: "targetQuantity"},
			{name: "targetQuantityUomCd"},
			{name: "apGroupLeaderId"},
			/*
			 * LOGON-6393 Remove net price field (or content) in contract tab 
			 * Commented out for the quick solution. Later on we will display it again with authorization. 
			 */ 
//			{name: "netValue"},
			{name: "markedForDeletion"},
			{name: "ctlCreTs"},
			{name: "ctlModTs"}
		 ]);

 var conrtactItemColumns = [
					 {
						id: 'contractItemNo',
						header: texts.contractItemNo,
						dataIndex: 'contractItemNo',
						sortable: true,
						width: 30
					},{
						id: 'materialTxt',
						header: texts.materialTxt,
						dataIndex: 'materialTxt',
						sortable: true,
						width: 30
					},{
						id: 'prodPlantName',
						header: texts.prodPlantName,
						dataIndex: 'prodPlantName',
						sortable: true,
						width: 30
					},{
						id : 'supplyPlantName',
						header: texts.supplyPlantName,
						dataIndex: 'supplyPlantName',
						sortable: true,
						width: 30
					},{
						id: 'materialGroup1',
						header: texts.materialGroup1,
						dataIndex: 'materialGroup1',
						sortable: true,
						width: 30
					},{
						id: 'materialGroup2',
						header: texts.materialGroup2,
						dataIndex: 'materialGroup2',
						sortable: true,
						width: 30
					},{
						id: 'materialGroup3',
						header: texts.materialGroup3,
						dataIndex: 'materialGroup3',
						sortable: true,
						width: 30
					},{
						id: 'materialGroup4',
						header: texts.materialGroup4,
						dataIndex: 'materialGroup4',
						sortable: true,
						width: 30
					},{
						id: 'materialGroup5',
						header: texts.materialGroup5,
						dataIndex: 'materialGroup5',
						sortable: true,
						width: 30
					},{
						id: 'itemCategory',
						header: texts.itemCategory,
						dataIndex: 'itemCategory',
						sortable: true,
						width: 30
					},{
						id: 'targetQuantity',
						header: texts.targetQuantity,
						dataIndex: 'targetQuantity',
						sortable: true,
						width: 30
					},{
						id: 'targetQuantityUomCd',
						header: texts.targetQuantityUomCd,
						dataIndex: 'targetQuantityUomCd',
						sortable: true,
						width: 30
				/*
				 * LOGON-6393 Remove net price field (or content) in contract tab 
				 * Commented out for the quick solution. Later on we will display it again with authorization. 
				 */
//					},{
//						id: 'netValue',
//						header: texts.netValue,
//						dataIndex: 'netValue',
//						sortable: true,
//						width: 30
					},{
						id: 'ctlCreTs',
						header: texts.ctlCreTs,
						dataIndex: 'ctlCreTs',
						sortable: true,
						width: 50,
						renderer: Ext.util.Format.dateRenderer(dateTimeFormat)
					},{
						id: 'ctlModTs',
						header: texts.ctlModTs,
						dataIndex: 'ctlModTs',
						sortable: true,
						width: 50,
						renderer: Ext.util.Format.dateRenderer(dateTimeFormat)
					}
					]

 var contractItemStore = new Ext.data.Store({
	proxy: new Ext.data.DWRProxyForPager(
			function(skip, count, sort, query, callback) { 
				ContractDataAjaxService.searchContractItems(skip, count, sort, contractItemSearch.searchFilters, callback) 
			}, true),
	reader: new Ext.data.ListRangeReader({id: 'sapContractItemId', totalProperty: 'totalSize'}, contractItemRecordType), 
	remoteSort: true
 });

 var contractItemSearch = {
	pageSize: 10,
	isRendered:false,
	searchFilters: {},
	searchContractDataItems: function(filters) {
		this.searchFilters = filters;
		contractItemStore.load({params: {start: 0, limit: this.pageSize, sort: '', query: filters} });
	}
 }

 var pagingBarForItems = new Ext.ux.grid.PagingToolbar({
	 pageSize: contractItemSearch.pageSize,
	 store: contractItemStore,
	 displayInfo: true,
	 displayMsg: texts.pagerDisplayMsg,
	 emptyMsg: texts.pagerEmptyMsg
 });

 function createItemGrid() {
	contractItemGrid = new Ext.grid.GridPanel({
		id: 'contractItemGrid',
		renderTo:'divContractItems',
		height: 300,
		enableColumnHide: true,
		viewConfig: {
				autoFill: true,
				forceFit: true,
				getRowClass: function(row, index){
					if(row.data.markedForDeletion == 1){
						return 'x-grid3-row-linethrough';		
					}
				}
			},
		stripeRows: true,
		title: texts.contractItemGridTitle,
		store: contractItemStore,
		pageSize: contractItemSearch.pageSize,
		frame: true,
		loadMask: true,
		clicksToEdit: 1,
		// grid columns		
		columns:conrtactItemColumns,
		bbar: pagingBarForItems,
		sm: new Ext.grid.RowSelectionModel({
			singleSelect: true,
			listeners: {
				rowselect: function(smObj, rowIndex, record) {
					showContractItemDataPanel(record, true);
				},
				selectionchange: function(smObj, rowIndex) {
					if (!smObj.hasSelection()) {
						showContractItemDataPanel(null, false);
					}
				}
			}
		})
	});
	contractItemGrid.render();
 }

function showContractItemDataPanel(contractItemRecord, showPanel) {
	var divContractItemData = Ext.get('divContractItemData');
	
	if (showPanel) {
		divContractItemData.load({
			url: '../dispokarte/Dispokarte.do',
			params: {
				loadContractItemData: true,
				sapContractItemId: contractItemRecord.data.sapContractItemId
			},
			text: loadingMsgText,
			scripts: true,
			nocache: true
		});
	}
	else {
		divContractItemData.select('*').remove();
	}
}

function changeOnlyActiveContractCheckboxState() {
	lastOptions = contractDataStore.lastOptions;
	lastOptions.params.start = 0;
	contractDataStore.reload(lastOptions);
}

 Ext.onReady( function() {
	createGrid(); 
	
	contractData.searchContractData({selectedShipTo : selectedShipTo, selectedSoldTo : selectedSoldTo});
 } );
