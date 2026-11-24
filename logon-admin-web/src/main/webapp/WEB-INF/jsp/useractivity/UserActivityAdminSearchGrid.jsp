<%@ include file="../includeTLDs.jsp" %>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>
<%@ page import="com.crh.logon.utils.time.DateTimePatterns" %>

<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"><script type="text/javascript"></c:if>

var activitySearchTableRecordType = Ext.data.Record.create([
	{name: "userActivityId", type: "string"},
	{name: "timestamp", type: "string"},
	{name: "userId", type: "string"},
	{name: "userFirstname", type: "string"},
	{name: "userName", type: "string"},
	{name: "userHolspace", type: "string"},
	{name: "userExtlogin", type: "string"},
	{name: "userPdauser", type: "string"},
	{name: "executorId", type: "string"},
	{name: "executorGuiLabel", type: "string"},
	{name: "actionCd", type: "string"},
	{name: "actionGuiLabel", type: "string"},
	{name: "ipAddress", type: "string"},
	{name: "description", type: "string"}
]);

var lastLoginSearchTableRecordType = Ext.data.Record.create([
	{name: "userId", type: "string"},
	{name: "lastLogin", type: "date"},
	{name: "ldapHolspace", type: "string"},
	{name: "extLogin", type: "string"},
	{name: "pdaUserNo", type: "string"}
]);


var activitySearchTableStore = new Ext.data.Store({
    proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { UserActivityAjaxService.fetchUserActivity(skip, count, sort, activitySearchArea.searchFilters, callback) }, true),
	reader: new Ext.data.ListRangeReader({id:'userActivityId', totalProperty:'totalSize'}, activitySearchTableRecordType), 
	remoteSort: true
});

var lastLoginSearchTableStore = new Ext.data.Store({
    proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { UserActivityAjaxService.fetchLastLogin(skip, count, sort, lastLoginSearchArea.searchFilters, callback) }, true),
	reader: new Ext.data.ListRangeReader({id:'userId', totalProperty:'totalSize'}, lastLoginSearchTableRecordType), 
	remoteSort: true
});

Ext.override(Ext.grid.RowSelectionModel, {
    onRefresh : function(){
        this.suspendEvents();
        this.clearSelections();
        this.resumeEvents();
    }
});

// create activity grid
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
				width: 60 
			},
			{
				header: '<fmt:message key="useractivity.col.actionid"/>', 
				sortable: true, 
				dataIndex: 'actionCd',
				width: 30
			},
			{
				header: '<fmt:message key="useractivity.col.action"/>', 
				sortable: false, 
				dataIndex: 'actionGuiLabel',
				width: 100
			},
			{
				header: '<fmt:message key="useractivity.col.userid"/>', 
				sortable: true, 
				dataIndex: 'userId',
				width: 30
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
				header: '<fmt:message key="useractivity.col.executorid"/>', 
				sortable: true, 
				dataIndex: 'executorId',
				width: 30
			},
			{
				header: '<fmt:message key="useractivity.col.executor"/>', 
				sortable: false, 
				dataIndex: 'executorGuiLabel',
				width: 40
			},
			{
				header: '<fmt:message key="useractivity.col.description"/>', 
				sortable: true, 
				dataIndex: 'description',
				width: 50
			}
		]
});

// create last login grid
var lastLoginSearchGrid = new Ext.grid.GridPanel({
	id: 'last-login-search-results-grid',
	el: 'last-login-search-results-div',
	store: lastLoginSearchTableStore,
	title:'<fmt:message key="useractivity.grid.title.lastloginsearch"/>',
	viewConfig: {
		forceFit:true
	},
	//trackMouseOver:false,
	//loadMask: true,
	stripeRows: true,
	bbar: new Ext.PagingToolbar({
		store: lastLoginSearchTableStore,
		displayInfo: true,
		displayMsg: '<fmt:message key="useradmin.grid.userSearchDisplayingResults"/>',
		emptyMsg: '<fmt:message key="useradmin.grid.userSearchNoResults"/>'
	}),
	autoHeight: true,
	width: Ext.get('last-login-search-results-div').getWidth(),
	columns: [
	  		{
	  			id:'userId',
	  			header: '<fmt:message key="useractivity.col.userid"/>', 
	  			sortable: true, 
	  			dataIndex: 'userId',
	  			width: 25
	  		},
	  		{
	  			header: '<fmt:message key="useractivity.col.lastlogin"/>',
	  			sortable: true, 
	  			dataIndex: 'lastLogin',
	  			renderer: Ext.util.Format.dateRenderer('<%=DateTimePatterns.JS_USER_ACTIVITY_TIMESTAMP %>'),
	  			width: 100
	  		},
	  		{
	  			header: '<fmt:message key="useractivity.col.userholspace"/>', 
	  			sortable: true, 
	  			dataIndex: 'ldapHolspace',
	  			width: 60 
	  		},
	  		{
	  			header: '<fmt:message key="useractivity.col.userextlogin"/>', 
	  			sortable: true, 
	  			dataIndex: 'extLogin',
	  			width: 60
	  		},
	  		{
	  			header: '<fmt:message key="useractivity.col.userpdauser"/>', 
	  			sortable: true, 
	  			dataIndex: 'pdaUserNo',
	  			width: 60
	  		}
	  	]
});

activitySearchGrid.render();
lastLoginSearchGrid.render();

//--- activity search area handler obj ---
activitySearchArea = {
	pageSize: 20,
	searchFilters: {},
	search: function() {
		if(lastLoginSearchGrid != null && lastLoginSearchGrid.isVisible()) {
			lastLoginSearchGrid.hide();
		}
		if(activitySearchGrid != null && !activitySearchGrid.isVisible()) {
			activitySearchGrid.show();
		}
		var filters = this.getSearchCrits();
		this.searchFilters = this.getSearchCrits();
		activitySearchTableStore.load({params:{start: 0, limit: this.pageSize, sort: '', query: filters}});
	},
	getSearchCrits: function() {
		return {
			filterFromDate: document.getElementsByName("filterFromDate")[0].value,
			filterFromTime: document.getElementsByName("filterFromTime")[0].value,
			filterToDate: document.getElementsByName("filterToDate")[0].value,
			filterToTime: document.getElementsByName("filterToTime")[0].value,
			userId: document.getElementsByName("filterUserId")[0].value, 
			userName: document.getElementsByName("filterUserName")[0].value, 
			userHolspace: document.getElementsByName("filterUserHolspace")[0].value,
			userPdauser: document.getElementsByName("filterUserPdauser")[0].value, 
			executorId: document.getElementsByName("filterExecutorId")[0].value, 
			userFirstname: document.getElementsByName("filterUserFirstname")[0].value, 
			userExtlogin: document.getElementsByName("filterUserExtlogin")[0].value, 
			ipAddress: document.getElementsByName("filterIpAddress")[0].value
		}
	}
}

//--- last login search area handler obj ---
lastLoginSearchArea = {
	pageSize: 20,
	searchFilters: {},
	search: function() {
		if(activitySearchGrid != null && activitySearchGrid.isVisible()) {
			activitySearchGrid.hide();
		}
		if(lastLoginSearchGrid != null && !lastLoginSearchGrid.isVisible()) {
			lastLoginSearchGrid.show();
		}
		var filters = this.getSearchCrits();
		this.searchFilters = this.getSearchCrits();
		lastLoginSearchTableStore.load({params:{start: 0, limit: this.pageSize, sort: '', query: filters}});
	},
	getSearchCrits: function() {
		return {
			lastLoginBefore: document.getElementsByName("lastLoginBefore")[0].value
		}
	}
}

var lastLoginBefore = new Ext.form.DateField({
	id: 'lastLoginBefore',
	format: '<%=DateTimePatterns.JS_USER_ACTIVITY_DATE %>',
	applyTo: 'lastLoginBefore',
    allowBlank: false,
    selectOnFocus: true
});

var filterFromDate = new Ext.form.DateField({
	id: 'filterFromDate',
	format: '<%=DateTimePatterns.JS_USER_ACTIVITY_DATE %>',
	applyTo: 'filterFromDate',
    allowBlank: true,
    selectOnFocus: true
});

var filterFromTime = new Ext.form.TimeField({
	id: 'filterFromTime',
	increment: 15,
	format: '<%=DateTimePatterns.JS_USER_ACTIVITY_TIME %>',
	applyTo: 'filterFromTime',
	allowBlank: true,
	selectOnFocus: true
});

var filterToDate = new Ext.form.DateField({
	id: 'filterToDate',
	format: '<%=DateTimePatterns.JS_USER_ACTIVITY_DATE %>',
	applyTo: 'filterToDate',
    allowBlank: true,
    selectOnFocus: true
});

var filterToTime = new Ext.form.TimeField({
	id: 'filterToTime',
	increment: 15,
	format: '<%=DateTimePatterns.JS_USER_ACTIVITY_TIME %>',
	applyTo: 'filterToTime',
	allowBlank: true,
	selectOnFocus: true
});

var filterUserId = new Ext.form.TextField( {
	allowBlank :true,
	applyTo :'filterUserId',
	selectOnFocus :true
});

var filterUserName = new Ext.form.TextField( {
	allowBlank :true,
	applyTo :'filterUserName',
	selectOnFocus :true
});

var filterUserHolspace = new Ext.form.TextField( {
	allowBlank :true,
	applyTo :'filterUserHolspace',
	selectOnFocus :true
});

var filterUserPdauser = new Ext.form.TextField( {
	allowBlank :true,
	applyTo :'filterUserPdauser',
	selectOnFocus :true
});

var filterExecutorId = new Ext.form.TextField( {
	allowBlank :true,
	applyTo :'filterExecutorId',
	selectOnFocus :true
});

var filterUserFirstname = new Ext.form.TextField( {
	allowBlank :true,
	applyTo :'filterUserFirstname',
	selectOnFocus :true
});

var filterUserExtlogin = new Ext.form.TextField( {
	allowBlank :true,
	applyTo :'filterUserExtlogin',
	selectOnFocus :true
});

var filterIpAddress = new Ext.form.TextField( {
	allowBlank :true,
	applyTo :'filterIpAddress',
	selectOnFocus :true
});

Ext.get('dormantSearchButton').on('click', function() {
	lastLoginSearchArea.search();
});

Ext.get('searchButton').on('click', function() {
	activitySearchArea.search();
});

Ext.get('resetFilterButton').on('click', function() {
	resetSearchFilters();
	activitySearchArea.search();
});

<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"></script></c:if>