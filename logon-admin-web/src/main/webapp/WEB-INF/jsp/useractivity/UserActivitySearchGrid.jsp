<%@ include file="../includeTLDs.jsp" %>
<%@ page import="com.crh.logon.utils.time.DateTimePatterns" %>

<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"><script type="text/javascript"></c:if>

var activitySearchTableRecordType = Ext.data.Record.create([
	{name: "userActivityId", type: "string"},
	{name: "timestamp", type: "string"},
	{name: "userFirstname", type: "string"},
	{name: "userName", type: "string"},
	{name: "userHolspace", type: "string"},
	{name: "userExtlogin", type: "string"},
	{name: "userPdauser", type: "string"},
	{name: "actionGuiLabel", type: "string"},
	{name: "ipAddress", type: "string"},
	{name: "description", type: "string"}
]);


var activitySearchTableStore = new Ext.data.Store({
    proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { UserActivityAjaxService.fetchUserActivity(skip, count, sort, activitySearchArea.searchFilters, callback) }, true),
	reader: new Ext.data.ListRangeReader({id:'userActivityId', totalProperty:'totalSize'}, activitySearchTableRecordType), 
	remoteSort: true
});

Ext.override(Ext.grid.RowSelectionModel, {
    onRefresh : function(){
        this.suspendEvents();
        this.clearSelections();
        this.resumeEvents();
    }
});

// create the Grid
var activitySearchGrid = new Ext.grid.GridPanel({
	id: 'search-results-grid',
	el: 'search-results-div',
	store: activitySearchTableStore,
	title:'<fmt:message key="useractivity.grid.title.activitysearch"/>',
	viewConfig: {
		forceFit:true
	},
	//trackMouseOver:false,
	//loadMask: true,
	stripeRows: true,
	bbar: new Ext.PagingToolbar({
		store: activitySearchTableStore,
		displayInfo: true,
		displayMsg: '<fmt:message key="useradmin.grid.userSearchDisplayingResults"/>',
		emptyMsg: '<fmt:message key="useradmin.grid.userSearchNoResults"/>'
	}),
	autoHeight: true,
	width: Ext.get('search-results-div').getWidth(),
 	columns:
 		[
			{
				id:'userActivityId',
				header: '#', 
				sortable: true, 
				dataIndex: 'userActivityId',
				width: 25
			},
			{
				header: '<fmt:message key="useractivity.col.timestamp"/>', 
				sortable: true, 
				dataIndex: 'timestamp',
				width: 100
			},
			{
				header: '<fmt:message key="useractivity.col.ipaddress"/>', 
				sortable: true, 
				dataIndex: 'ipAddress',
				width: 40 
			},
			{
				header: '<fmt:message key="useractivity.col.action"/>', 
				sortable: false, 
				dataIndex: 'actionGuiLabel',
				width: 100
			},
			{
				header: '<fmt:message key="useractivity.col.username"/>', 
				sortable: true, 
				dataIndex: 'userName',
				width: 60
			},
			{
				header: '<fmt:message key="useractivity.col.userfirstname"/>', 
				sortable: true, 
				dataIndex: 'userFirstname',
				width: 60
			},
			{
				header: '<fmt:message key="useractivity.col.userholspace"/>', 
				sortable: true, 
				dataIndex: 'userHolspace',
				width: 60
			},
			{
				header: '<fmt:message key="useractivity.col.userextlogin"/>', 
				sortable: true, 
				dataIndex: 'userExtlogin',
				width: 60
			},
			{
				header: '<fmt:message key="useractivity.col.userpdauser"/>', 
				sortable: true, 
				dataIndex: 'userPdauser',
				width: 40
			},
			{
				header: '<fmt:message key="useractivity.col.description"/>', 
				sortable: true, 
				dataIndex: 'description',
				width: 100
			}
		]
});

activitySearchGrid.render();

//--- Search Area Handler Obj ---
activitySearchArea = {
	pageSize: 20,
	searchFilters: {},
	search: function() {
		if(activitySearchGrid != null && !activitySearchGrid.isVisible()) {
			activitySearchGrid.show();
		}
		var filters = this.getSearchCrits();
		this.searchFilters = this.getSearchCrits();
		activitySearchTableStore.load({params:{start: 0, limit: this.pageSize, sort: '', query: filters}});
	},
	getSearchCrits: function() {
		return {
		}
	}
}

<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"></script></c:if>