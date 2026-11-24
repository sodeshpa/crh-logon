<%@ include file="../includeTLDs.jsp"%>
<%@ page import="com.crh.logon.admin.web.ajax.DispocardViewAjaxService"%>

<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"><script type="text/javascript"></c:if>

	// --- Common properties for all filter-comboboxes ---
	Ext.form.ComboBox.prototype.shadow = false;
	Ext.form.ComboBox.prototype.emptyText = "Select...";
	Ext.form.ComboBox.prototype.minChars = 1;
	Ext.form.ComboBox.prototype.allowBlank = true;
	Ext.form.ComboBox.prototype.forceSelection = true;
	Ext.menu.Menu.prototype.shadow = false;

	// --- Data record and DataStore definitions ---
	var sapMatRec = Ext.data.Record.create([
	    {name: "sapMaterialId", mapping: "sapMaterialId"},
	    {name: "display", mapping: "display"}
	]);
    
	var carrierRec = Ext.data.Record.create([
	    {name: "carrierId", mapping: "carrierId"},
	    {name: "displayName", mapping: "displayName"}
	]);

    var dispocartSearchRec = Ext.data.Record.create([
        {name: "compositeId"},
        {name: "sapMaterialId"},
        {name: "poolId"},
        {name: "commCarrierId"},
        {name: "deliveryTypeCd"},
        {name: "materialTxt"},
        {name: "poolName"},
        {name: "carrierName"},
        {name: "plantCd"},
        {name: "plantName"},
        {name: "deliveryTypeTxt"},
        {name: "shiptoNr"},
        {name: "shiptoName"},
        {name: "sapContractNo"},
        {name: "sareaDisplayLongname"},
        {name: "enabledFlag"},
        {name: "sapMaterialNo"}
    ]);
    
    var sapMatStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { DispocardViewAjaxService.fetchSapMaterialList(skip, count, sort, query, callback) }, true),
		reader: new Ext.data.ListRangeReader({id:'sapMaterialId', totalProperty:'totalSize'}, sapMatRec)
    });
    
    var carrierStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { DispocardViewAjaxService.fetchCarrierList(skip, count, sort, query, callback) }, true),
		reader: new Ext.data.ListRangeReader({id:'carrierId', totalProperty:'totalSize'}, carrierRec)
    });

    var dispocardSearchStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { DispocardViewAjaxService.searchDispocard(skip, count, sort, searchArea.searchFilters, callback) }, true),
		reader: new Ext.data.ListRangeReader({id:'compositeId', totalProperty:'totalSize'}, dispocartSearchRec), 
		remoteSort: true
    });

    var lastUpdateDat;
    // --- Search Area Handler Obj ---
	var searchArea = {
		pageSize: 25,
		searchFilters: {},
		getSearchCrits: function() {		

			return {
				<%--  Note: when adding new filters, make sure to adjust the export function. --%>
				sapMaterialId: sapMatCombo.getValue(),
				poolId: poolCombo.getValue(),
				commCarrierId: carrierCombo.getValue(),
				plantCd: plantCombo.getValue(),
				deliveryTypeCdList: deliveryTypeCheckCombo.getCheckedValues()?deliveryTypeCheckCombo.getCheckedValues().join(','):null	                
			}
		},
		searchDefaults: function(startRecord) {
			var filters = this.getSearchCrits();
			this.searchFilters = this.getSearchCrits();
			if (startRecord == undefined) {
				startRecord = 0;
			}
			dispocardSearchStore.load({params:{start: startRecord, limit: this.pageSize, sort: '', query: filters}});
		    lastUpdateDat = new Date();
		},
		searchDefaultsKeepPaging: function(){
			var startRecord = 0;
			if (!(pagingBar === undefined) && !(pagingBar.cursor === undefined) && !(pagingBar.pageSize === undefined)) {
				var activePage = Math.ceil((pagingBar.cursor + pagingBar.pageSize) / pagingBar.pageSize);
				startRecord = activePage * pagingBar.pageSize - pagingBar.pageSize;
				if (isNaN(startRecord)){
					startRecord = 0;
				}
			}
			this.searchDefaults(startRecord);
		}
	}
	
	// --- Filters:
    // --- Material ---
    var sapMatCombo = new Ext.ux.TwinCombo({
    	id: 'sapMatCombo',
        applyTo: 'sapMatCombo',
        store: sapMatStore,
        displayField: 'display',
        valueField: 'sapMaterialId',
        pageSize: 20,
        width: 180,
        listWidth: 300,
        typeAhead: false,
        triggerAction: 'all',
        selectOnFocus: true
    });   
    
    // --- Pool ---
    var poolCombo = new Ext.ux.TwinCombo({
        id: 'poolCombo',
        applyTo:'poolId',
        selectOnFocus: true,
        editable: false,
        typeAhead: false,
        triggerAction: 'all',
        store: new Ext.data.JsonStore({
            fields: ['poolTxPK.poolId', 'name'],
            data : <c:out value="${admin_DispocardViewForm.json['poolList']}" escapeXml="false"/> 
        }),
        mode: 'local',
        width: 180,
        listWidth: 250,
        valueField: 'poolTxPK.poolId',
        displayField: 'name'
    });
	
	// --- Carrier ---
	var carrierCombo = new Ext.ux.TwinCombo({
		id: 'carrierCombo',
	    applyTo:'carrierId',
	    store: carrierStore,
	    valueField: 'carrierId',
	    displayField: 'displayName',
	    pageSize: 20,
		selectOnFocus: true,
	    typeAhead: false,
	    triggerAction: 'all',
	    width: 180,
	    listWidth: 250
	});

    // --- Plant ---
	var plantCombo = new Ext.ux.TwinCombo({
		id: 'plantCombo',
	    applyTo:'plantCd',
		selectOnFocus: true,
		editable: false,
	    typeAhead: false,
	    triggerAction: 'all',
	    store: new Ext.data.JsonStore({
	    	fields: ['value', 'name'],
	    	data : <c:out value="${admin_DispocardViewForm.json['plantList']}" escapeXml="false"/>,
	    	sortInfo: {field: "name", direction: "ASC"} 
		}),
	    mode: 'local',
	    width: 180,
	    listWidth: 250,
	    valueField: 'value',
	    displayField: 'name'
	});
    
	// --- Delivery type ---
	var deliveryTypeCheckCombo = new Ext.ux.CheckComboBox({
		store: new Ext.data.JsonStore({
        	fields: [
        		{name: 'deliveryTypeCd', mapping: 'deliveryTypeTxPK.deliveryTypeBs.deliveryTypeCd'}, 
        		{name: 'txt', mapping: 'txt'} 
        	],
        	data : <c:out value="${admin_DispocardViewForm.json['deliveryTypeList']}" escapeXml="false"/> 
    	}),
	    displayField: 'txt',
	    checkedField: 'checked',
	    valueField: 'deliveryTypeCd',
	    typeAhead: true,
	    mode: 'local',
	    triggerAction: 'all',
	    emptyText:'none',
	    selectOnFocus: true,
	    width: 180,
	    listWidth: 200
	});
	deliveryTypeCheckCombo.selectAll();
	deliveryTypeCheckCombo.applyToMarkup('deliveryTypeCd');

<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"></script></c:if>
	