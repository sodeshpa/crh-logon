<%@ include file="../includeTLDs.jsp"%>

<%@page import="com.crh.logon.admin.web.ajax.PDAAdminAjaxService"%>


<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"><script type="text/javascript"></c:if>

	// --- Common properties for all filter-comboboxes ---
	Ext.form.ComboBox.prototype.shadow = false;
	Ext.form.ComboBox.prototype.emptyText = "Select...";
	Ext.form.ComboBox.prototype.minChars = 1;
	Ext.form.ComboBox.prototype.allowBlank = true;
	Ext.form.ComboBox.prototype.forceSelection = true;
	Ext.menu.Menu.prototype.shadow = false;

	// --- Data record and DataStore definitions ---
	var pdaSearchRec = Ext.data.Record.create([
		{name: "pdaDeviceId"},
		{name: "pdaDeviceNo"},
		{name: "inventoryNumber"},
		{name: "typeDescription"},
		{name: "softwareVersion"},
	    {name: "carrierId"},
	    {name: "carrierDisplayName"},
	    {name: "pdaDeviceActiveFlag"}
	]);

    var pdaSearchStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { PDAAdminAjaxService.searchPDAs(skip, count, sort, searchArea.searchFilters, callback) }, true),
		reader: new Ext.data.ListRangeReader({id:'pdaDeviceId', totalProperty:'totalSize'}, pdaSearchRec), 
		remoteSort: true
    });

    
    // --- Search Area Handler Obj ---
	var searchArea = {
		pageSize: 25,
		searchFilters: {},
		searchPDAs: function(filters) {
			this.searchFilters = filters;
		    pdaSearchStore.load({params:{start: 0, limit: this.pageSize, sort: '', query: filters}});
		}
	}

	// --- Carrier ---
	var carrierCombo = new Ext.ux.TwinCombo({
		id: 'carrierCombo',
	    applyTo:'carrierId',
		selectOnFocus: true,
		editable: false,
	    typeAhead: false,
	    triggerAction: 'all',
	    store: new Ext.data.JsonStore({
        	fields: ['carrierId', 'displayName'],
        	data : <c:out value="${admin_PDAAdminForm.json['carrierList']}" escapeXml="false"/> 
    	}),
	    mode: 'local',
	    width: 180,
	    listWidth: 250,
	    valueField: 'carrierId',
	    displayField: 'displayName'
	});


<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"></script></c:if>
	