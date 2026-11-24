<%@ include file="../includeTLDs.jsp"%>

<%@page import="com.crh.logon.admin.web.ajax.PDAAdminAjaxService"%>


<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"><script type="text/javascript"></c:if>

	// --- Data record and DataStore definitions ---
	var rfidtagSearchRec = Ext.data.Record.create([
		{name: "rfidtagId"},
		{name: "rfidIdentity"},
	    {name: "name"},
	    {name: "objectLabel"},
	    {name: "rfidObjectType"},
	    {name: "activeFlag"}
	]);

    var rfidtagSearchStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { RFIDAdminAjaxService.searchRFIDTags(skip, count, sort, searchArea.searchFilters, callback) }, true),
		reader: new Ext.data.ListRangeReader({id:'rfidtagId', totalProperty:'totalSize'}, rfidtagSearchRec), 
		remoteSort: true
    });

    
    // --- Search Area Handler Obj ---
	var searchArea = {
		pageSize: 25,
		searchFilters: {},
		searchRFIDTags: function(filters) {
			this.searchFilters = filters;
			rfidtagSearchStore.load({params:{start: 0, limit: this.pageSize, sort: '', query: filters}});
		}
	}

<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"></script></c:if>
	