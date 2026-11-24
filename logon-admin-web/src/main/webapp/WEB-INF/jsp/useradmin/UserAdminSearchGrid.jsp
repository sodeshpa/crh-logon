<%@ include file="../includeTLDs.jsp" %>

<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"><script type="text/javascript"></c:if>

	var tableRecordType = Ext.data.Record.create([
      {name: "userId", type: "string"},
      {name: "name", type: "string"},
      {name: "firstname", type: "string"},
      {name: "ldapHolspace", type: "string"},
      {name: "extLogin", type: "string"},
      {name: "pdaUserNo", type: "string"},
      {name: "customer", type: "string"},
      {name: "carrier", type: "string"},
      {name: "activeFlag", type: "bool"},
      {name: "roleCount", type: "string"},
      {name: "roleNames", type: "string"},
      {name: "certifiedFromDat", type: "date"},
      {name: "certifiedToDat", type: "date"},
      {name: "readOnly", type: "bool"}
    ]);

	var tableStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { UserAdminAjaxService.fetchUserList(skip, count, sort, searchArea.searchFilters, callback) }, true),
		reader: new Ext.data.ListRangeReader({id:'userId', totalProperty:'totalSize'}, tableRecordType), 
		remoteSort: true
	});
	
	Ext.override(Ext.grid.RowSelectionModel, {
	    onRefresh : function(){
	        this.suspendEvents();
	        this.clearSelections();
	        this.resumeEvents();
	    }
	});
	
	var selectionModel = new Ext.grid.RowSelectionModel({
	    singleSelect: true,
	    listeners: {
	        rowselect: {
	            fn: function(sm, index, record) {
					if (grid != null) {
						grid.hide();
					}
	        		var rec = selectionModel.getSelected();
	        		useradmin.modifyUser(rec.data.userId);
	            }
	    	},
        	beforerowselect: {
	            fn: function(sm, index, keepExisting, record) { 
	            	
	            	if (record.data.readOnly) {
    	            	Logon.message.error('<fmt:message key="useradmin.errors.cannot.modify.user"/>');
	            		return false;
	            	}
	            	return true;
	            }
        	}
		}
	});


	// create the Grid
	var grid = new Ext.grid.GridPanel({
		id: 'user-search-results-grid',
		el:'user-search-results-div',
		store: tableStore,
		sm: selectionModel,
		title:'<fmt:message key="useradmin.grid.userSearchResults"/>',
		viewConfig: {
		forceFit:true
	},
	//trackMouseOver:false,
	//loadMask: true,
	stripeRows: true,
	bbar: new Ext.PagingToolbar({
		store: tableStore,
		displayInfo: true,
		displayMsg: '<fmt:message key="useradmin.grid.userSearchDisplayingResults"/>',
		emptyMsg: '<fmt:message key="useradmin.grid.userSearchNoResults"/>'
	}),
	 autoHeight:true,
	 width:Ext.get('user-search-results-div').getWidth(),
	 columns:	[
	             {
		            id:'firstname',
		            header: '<fmt:message key="useradmin.field.firstName"/>', 
		            sortable: true, 
		            dataIndex: 'firstname',
		            width: 50
	             },
	             {
		            header: '<fmt:message key="useradmin.field.name"/>', 
		            sortable: true, 
		            dataIndex: 'name',
		            width: 50
	             },
	             {
		            header: '<fmt:message key="useradmin.field.holspace"/>', 
		            sortable: true, 
		            dataIndex: 'ldapHolspace',
		            width: 50
	             },
	             {
		            header: '<fmt:message key="useradmin.field.extLogin"/>', 
		            sortable: true, 
		            dataIndex: 'extLogin',
		            width: 40 
	             },
	             {
		            header: '<fmt:message key="useradmin.field.pdaUserNo"/>', 
		            sortable: true, 
		            dataIndex: 'pdaUserNo',
		            width: 30
	             },
	             {
		            header: '<fmt:message key="useradmin.field.customer"/>', 
		            sortable: true, 
		            dataIndex: 'customer',
		            width: 50 
	             },
	             {
		            header: '<fmt:message key="useradmin.field.carrier"/>', 
		            sortable: true, 
		            dataIndex: 'carrier',
		            width: 50 
	             },
	             {
		             header: '<fmt:message key="useradmin.field.roles"/>',
		             sortable: true, 
		             dataIndex: 'roleNames',
		             renderer:function renderRoleNames(value, p, record){
		            	 p.attr = 'title="' + value + '"';
		            	 var st = '<label">' + value + '</label>';
		             	 return st;
	                 },
	                 width: 100
		         },
	             {
		            header: '<fmt:message key="useradmin.field.certifiedfromdate"/>', 
		            sortable: true, 
		            dataIndex: 'certifiedFromDat',
		            renderer: function(value) {
		            	if(value) {
		            		return value.getDate() + "-" + (value.getMonth() + 1) + "-" + value.getFullYear();
		            	} else {
		            		return value;
		            	}
	             	},
		            width: 50
		         },
		         {
		            header: '<fmt:message key="useradmin.field.certifiedtodate"/>', 
		            sortable: true, 
		            dataIndex: 'certifiedToDat',
		            renderer: function(value) {
		            	if(value) {
		            		return value.getDate() + "-" + (value.getMonth() + 1) + "-" + value.getFullYear();
		            	} else {
		            		return value;
		            	}
		         	},
		            width: 50
			     },
	             { 
		            width: 15, 
		            header: '<fmt:message key="useradmin.field.active"/>',
		            sortable: true,
		            dataIndex: 'activeFlag',
		            renderer: function(v, p, record){
		             	if(record.data.activeFlag == true){
		             		return '<img width="16" height="16" alt="activated" src="' + crhCommonUrl('../images/active.png') + '"/>' ;
		             	}
		             	return '<img width="16" height="16" alt="activated" src="' + crhCommonUrl('../images/deactivated.png') + '"/>' ;
		             }
	             }
	         ]
	});

	grid.render();


// --- Search Area Handler Obj ---
searchArea = {
	pageSize: 20,
	searchFilters: {},
	searchUsers: function(doFilter){
		hideDetailsDiv();
		if(grid != null && ! grid.isVisible()) {
			grid.show();
		}
		
		var filters = this.getSearchCrits(doFilter);
		this.searchFilters = this.getSearchCrits(doFilter);
		tableStore.load({params:{start: 0, limit: this.pageSize, sort: '', query: filters}});
	},
	getSearchCrits: function(doFilter) {
		return {
			doFilter: doFilter,
			firstNameOrName: document.getElementsByName("searchFirstNameOrName")[0].value,
			holspaceOrExtLogin: document.getElementsByName("searchHolspaceOrExtLogin")[0].value,
			customerOrCarrier: document.getElementsByName("searchCustomerOrCarrier")[0].value, 
			activeFlag: document.getElementsByName("searchStatus")[0].checked,
			roleId: document.getElementsByName("searchSelRoleId")[0].value
		}
	}
}

<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"></script></c:if>