<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: CarrierTile.jsp,v $ --%>
<%@ page session="true"%>

<%@ include file="../includeTLDs.jsp"%>
<%@ page import="com.crh.logon.admin.web.ajax.masterdata.CarrierAjaxService"%>
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/CarrierAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/logon-validation.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.CheckComboBox.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.form.PeriodSelector.js'/>"></script>

<script type="text/javascript">

Ext.onReady(function(){
	// --- Common properties for all filter-comboboxes ---
	Ext.form.ComboBox.prototype.shadow = false;
	Ext.form.ComboBox.prototype.emptyText = "Select...";
	Ext.form.ComboBox.prototype.minChars = 1;
	Ext.form.ComboBox.prototype.allowBlank = true;
	Ext.form.ComboBox.prototype.forceSelection = true;
	Ext.menu.Menu.prototype.shadow = false;

	//--- Data record and DataStore definitions ---
	var commCarrierRec = Ext.data.Record.create( [ 
	    { name: "displayName", mapping: "displayName"}, 
	    { name: "carrierId", mapping: "carrierId"} ]);

	var commCarrierStore = new Ext.data.Store({
	    proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { CarrierAjaxService.fetchCommercialCarrierList(skip, count, sort, query, callback) }, true),
	    reader: new Ext.data.ListRangeReader({id:'carrierId', totalProperty:'totalSize'}, commCarrierRec)
	});

	var errorRec = Ext.data.Record.create( [ 
	    { name: "vendorName", mapping: "name1"}, 
	    { name: "vendorId", mapping: "sapVendorId"} ]);

	var errorStore = new Ext.data.Store({
	    proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { CarrierAjaxService.fetchErrorVendorList(skip, count, sort, query, callback) }, true),
	    reader: new Ext.data.ListRangeReader({id:'vendorId', totalProperty:'totalSize'}, errorRec)
	});

	var carrierSearchRec = Ext.data.Record.create([
	    {name: "carrierId"},
	    {name: "sapId"},
	    {name: "updateBlockedByHjid"},
	    {name: "accountGroup"},
	    {name: "name1"},
	    {name: "name2"},
	    {name: "countryCd"},
	    {name: "city"},
	    {name: "street"},
	    {name: "vnIsCommCarrier"},
	    {name: "vnIsExecCarrier"},
	    {name: "vnIsInternal"},
	    {name: "displayName"},
	    {name: "transmitToCarrierFlag"},
	    {name: "freightRevenueCurrencyCd"},
	    {name: "carrierRequiresPlanningFlag"},
	    {name: "bagStockSp"}
	]);

	var carrierSearchStore = new Ext.data.Store({
	    proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { CarrierAjaxService.searchCarrier(skip, count, sort, searchArea.searchFilters, callback) }, true),
	    reader: new Ext.data.ListRangeReader({id:'carrierId', totalProperty:'totalSize'}, carrierSearchRec), 
	    remoteSort: true
	});

	// --- Search Area Handler Obj ---
	var searchArea = {
	    pageSize: 30,
	    searchFilters: {},
	    getSearchCrits: function() {        

	        return {
	            carrierId: commCarrierCombo.getValue(),                
	            sapVendorId: errorCombo.getValue()                  
	        }
	    },
	    searchDefaults: function(startRecord) {
	        var filters = this.getSearchCrits();
	        this.searchFilters = this.getSearchCrits();
	        if (startRecord == undefined) {
	            startRecord = 0;
	        }
	        carrierSearchStore.load({params:{start: startRecord, limit: this.pageSize, sort: '', query: filters}});
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
	// --- Carrier ---
	var commCarrierCombo = new Ext.ux.TwinCombo({
	    id: 'commCarrierCombo',
	    applyTo:'commCarrierId',
	    store: commCarrierStore,
	    valueField: 'carrierId',
	    displayField: 'displayName',
	    pageSize: 20,
	    selectOnFocus: true,
	    typeAhead: false,
	    triggerAction: 'all',
	    width: 180,
	    listWidth: 250
	});
	// --- error ---
	var errorCombo = new Ext.ux.TwinCombo({
	    id: 'errorCombo',
	    applyTo:'errorVendorId',
	    store: errorStore,
	    valueField: 'vendorId',
	    displayField: 'vendorName',
	    pageSize: 20,
	    selectOnFocus: true,
	    typeAhead: false,
	    triggerAction: 'all',
	    width: 180,
	    listWidth: 250
	});

	// ------ PAGER ---------
	var pagingBar = new Ext.PagingToolbar({
	    pageSize: searchArea.pageSize,
	    store: carrierSearchStore,
	    displayInfo: true,
	    displayMsg: '<fmt:message key="carriersearch.displayMsgParametered"/>',
	    emptyMsg: '<fmt:message key="carriersearch.emptyMsg"/>'
	});

	var grid = new Ext.grid.GridPanel({
	    id: 'searchGrid',
	    el:'divCarrierSearchResults',
	    width: Ext.get('searchTable').getWidth(),
	    height:602,
	    viewConfig: {
	        autoFill: true, 
	        forceFit: true,
	        getRowClass: function(record, index) {
	            if (record.get('warnStatusCode') == 3) {
	                return 'x-grid3-row-italic';
	            }
	        }
	    },        
	    stripeRows: true,
	    title:'<fmt:message key="carriersearch.table.header.title"/>',
	    store: carrierSearchStore,
	    pageSize: searchArea.pageSize,
	    loadMask: true,
	    selModel: new Ext.grid.RowSelectionModel({singleSelect:true}),
	    // grid columns
	    columns:[
  	    {
	        id: 'function',
	        header: '<fmt:message key="carriersearch.table.header.error"/>',
			renderer: function(val, meta, rec) {
				var result = '<div style="float: left; width: 20px;">';
				if (rec.data.updateBlockedByHjid != null && rec.data.updateBlockedByHjid != '') {
					<%-- A hack: we cannot directly use a javascript variable in the parameter
						replacement of bean:message. There is an issue with single quotes. The
						main issue is that <bean:message> cannot filter special HTML characters
						like <bean:write> can. The message in question can contain apostrophes
						(e.g. in French), so we need to load it in double quotes.
					--%>
					var sapErrorMsg = "<bean:message key="carriersearch.table.header.error.title" arg0="__REPLACE__"/>";
					sapErrorMsg.replace('__REPLACE__', rec.data.updateBlockedByHjid);
					result += '<img src="' + crhCommonUrl('./images/silk/error.png') + '" title="' + sapErrorMsg + '"/>';
				} 
				result += '</div>';
				return result;	
            },
	        sortable: false,
	        width: 20
	    },{
	        id: 'name1', 
	        header: '<fmt:message key="carriersearch.table.header.name1"/>',
	        dataIndex: 'name1',
	        width: 90,
	        sortable: true
	    },{
	        id: 'name2', 
	        header: '<fmt:message key="carriersearch.table.header.name2"/>',
	        dataIndex: 'name2',
	        width: 120,
	        hidden: true,
	        sortable: true
	    },{
	        id: 'street', 
	        header: '<fmt:message key="carriersearch.table.header.street"/>',
	        dataIndex: 'street',
	        width: 150,
	        hidden: true,
	        sortable: true
	    },{
	        id: 'city',
	        header: '<fmt:message key="carriersearch.table.header.city"/>',
	        dataIndex: 'city',
	        hidden: true,
	        width: 80,
	        sortable: true
	    },{
	        id: 'countryCd', 
	        header: '<fmt:message key="carriersearch.table.header.country"/>',
	        dataIndex: 'countryCd',
	        hidden: true,
	        width: 100,
	        sortable: true
	    },{
	        id: 'sapId', 
	        header: '<fmt:message key="carriersearch.table.header.sapID"/>',
	        dataIndex: 'sapId',
	        width: 100,
	        sortable: true
	    },{
	        id: 'accountGroup', 
	        header: '<fmt:message key="carriersearch.table.header.grp"/>',
	        dataIndex: 'accountGroup',
	        hidden: true,
	        width: 100,
	        sortable: true
	    },{
	        id: 'displayName', 
	        header: '<fmt:message key="carriersearch.table.header.display"/>',
	        dataIndex: 'displayName',
	        width: 100,
	        sortable: true
	    },{
	        id: 'vnIsCommCarrier', 
	        header: '<fmt:message key="carriersearch.table.header.comm"/>',
	        dataIndex: 'vnIsCommCarrier',
	        width: 100,
	        sortable: true
	    },{
	        id: 'vnIsExecCarrier', 
	        header: '<fmt:message key="carriersearch.table.header.exec"/>',
	        dataIndex: 'vnIsExecCarrier',
	        width: 100,
	        sortable: true
	    },{
	        id: 'vnIsInternal', 
	        header: '<fmt:message key="carriersearch.table.header.int"/>',
	        dataIndex: 'vnIsInternal',
	        width: 100,
	        sortable: true
	    },{
	        id: 'bagStockSp', 
	        header: '<fmt:message key="carriersearch.table.header.bagStockSP"/>',
	        dataIndex: 'bagStockSp',
	        hidden: true,
	        width: 100,
	        sortable: true
	    },{	
	        id: 'transmitToCarrierFlag', 
	        header: '<fmt:message key="carriersearch.table.header.ttc"/>',
	        dataIndex: 'transmitToCarrierFlag',
	        width: 100,
	        sortable: true
	    },{
	        id: 'freightRevenueCurrencyCd', 
	        header: '<fmt:message key="carriersearch.table.header.currency"/>',
	        dataIndex: 'freightRevenueCurrencyCd',
	        width: 100,
	        sortable: true
	    },{
	        id: 'carrierRequiresPlanningFlag', 
	        header: '<fmt:message key="carriersearch.table.header.planning"/>',
	        dataIndex: 'carrierRequiresPlanningFlag',
	        width: 100,
	        sortable: true
	    }
	    ],

	    // paging bar on the bottom
	    bbar: pagingBar
	    
	});

	grid.render(); // render the grid
	
    // --- Initial data load ---
    carrierSearchStore.setDefaultSort('<c:out value="${defaultSort}"/>', '<c:out value="${defaultSortDir}"/>');
    searchArea.searchDefaults();
    
    // --- Buttons ---   
    Ext.get('buttonSearch').on('click', searchArea.searchDefaults.createDelegate(searchArea, []));
});
</script>

<!-- Sub menu 1 -->
<jsp:include page="../SubTab9Tile.jsp"/>
<table class="big_form2">
    <tr class="classicblue">
        <th class="classicblue"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.9"/></th>
    </tr>
    <tr>
        <td>
            <table id="searchTable" cellpadding="0" cellspacing="0" class="big_form2" style="margin: 0px; border-collapse: collapse;>
                <tr>
                    <td class="border" style="background-color: F9F9F9; padding: 2px 0 0 10px;">
                        <div id="divSimpleSearch">
                            <table style="width: 100%;">
                                <colgroup>
                                    <col width="2%">
                                    <col width="20%">
                                    <col width="2%">
                                    <col width="21%">
                                    <col width="55%">
                                </colgroup>
                                <tr>
                                    <td valign="bottom" style="white-space: nowrap; border: 0px; padding-right: 4px; vertical-align: middle;" class="label">
                                        <fmt:message key="carriersearch.error.label" />:
                                    </td>
                                    <td style="border: 0px;">
                                        <input type="text" name="errorVendorId" value="" id="errorVendorId" style="width: 95%" />
                                    </td>
                                    <td valign="bottom" style="white-space: nowrap; border: 0px; padding-right: 4px; vertical-align: middle;" class="label">
                                        <fmt:message key="carriersearch.commcarrier.label" />:
                                    </td>
                                    <td style="border: 0px;">
                                        <input type="text" name="commCarrierId" value="" id="commCarrierId" />
                                    </td>
                                    <td style="border: 0px; vertical-align: middle;">
                                        <input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.search"/>" id="buttonSearch" />
                                   </td>
                                </tr>
                            </table>
                        </div>
                    </td>
                </tr>
            </table>
            <div id="divCarrierSearchResults"></div>
        </td>
    </tr>
</table>

<%-- END $RCSfile: CarrierTile.jsp,v $ --%>
