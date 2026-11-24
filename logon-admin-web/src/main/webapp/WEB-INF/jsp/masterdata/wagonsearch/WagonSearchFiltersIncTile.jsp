<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.admin.web.ajax.WagonAdminAjaxService"%>
<%@page import="com.crh.logon.admin.web.forms.masterdata.WagonSearchForm"%>
<%@page import="com.crh.logon.persistence.entities.views.WagonSearchEntity"%>

<%-- For JS syntax highlighting in Eclipse --%>
<%@page import="com.crh.logon.admin.web.forms.masterdata.WagonSearchForm"%><c:if test="false"><script type="text/javascript"></c:if>

	// --- Common properties for all filter-comboboxes ---
	Ext.form.ComboBox.prototype.shadow = false;
	Ext.form.ComboBox.prototype.emptyText = "Select...";
	Ext.form.ComboBox.prototype.minChars = 1;
	Ext.form.ComboBox.prototype.allowBlank = true;
	Ext.form.ComboBox.prototype.forceSelection = true;
	Ext.menu.Menu.prototype.shadow = false;

	// --- Data record and DataStore definitions ---
	var wagonSearchRec = Ext.data.Record.create([
		{name: "compositeId"},
		{name: "wagonId"},
		{name: "wagonNo"},
		{name: "wagonTypeName"},
		{name: "lastKnownStationName"},
		{name: "wagonIdleTimeMin"},
		{name: "arrivalLastKnownStation"},
		{name: "plantTrackTxt"},
		{name: "currentTrackPositionNo"},
		{name: "destRailwayStationName"},
		{name: "returnShiptoName1"},
		{name: "orderId"},
		{name: "orderNumber"},
		{name: "orderItemNo"},
		{name: "orderStatusTxt"},
		{name: "distributionDestTxt"},
		{name: "soldtoName1"},
		{name: "shiptoName1"},
		{name: "sapMaterialTxt"},
		{name: "loadedQuantity"},
		{name: "loadedUom"},
		{name: "wagonKeeperName"},
		{name: "wagonNoteRailwaySpedition"},
		{name: "loadedQuantity"},
		{name: "activeFlag"}
	]);
	
	var wagonComboSearchRec = Ext.data.Record.create([
	     {name: "wagonId"},
	     {name: "wagonNo"}
	]);	
	
	var locationComboSearchRec = Ext.data.Record.create([
	     {name: "railwayStationId"},
	     {name: "railwayStationName"}
	]);

	var rwsSearchRec = Ext.data.Record.create([
   	     {name: "railwayStationId"},
   	     {name: "railwayStationName"}
   	]);

    var wagonNoStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { WagonAdminAjaxService.getWagonNoListForPart(skip, count, sort, query, callback)}, true),
		reader: new Ext.data.ListRangeReader({id:'wagonNo', totalProperty:'totalSize'}, wagonComboSearchRec)
    });

    var locationStationStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { WagonAdminAjaxService.getLocationStationsForPart(skip, count, sort, query, callback)}, true),
		reader: new Ext.data.ListRangeReader({id:'railwayStationId', totalProperty:'totalSize'}, locationComboSearchRec)
    });    

    var destRwsStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { WagonAdminAjaxService.getRailwayStations(skip, count, sort, query, callback)}, true),
		reader: new Ext.data.ListRangeReader({id:'railwayStationId', totalProperty:'totalSize'}, rwsSearchRec)
    });

    var wagonSearchStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { WagonAdminAjaxService.searchWagons(skip, count, sort, searchArea.searchFilters, callback) }, true),
		reader: new Ext.data.ListRangeReader({id:'compositeId', totalProperty:'totalSize'}, wagonSearchRec), 
		remoteSort: true
    });

    // --- Search Area Handler Obj ---
	var searchArea = {
		pageSize: 25,
		searchFilters: {},
		getSearchCrits: function() {
			var inactiveReasons = [];
			var activeFlag = false;
			for (var i=0;i<wagonInactiveReasonCheckCombo.getCheckedValues().length;i++) {
				var reason = wagonInactiveReasonCheckCombo.getCheckedValues()[i];
				if (reason == '-1') {
					activeFlag = true;
				}
				else {
					inactiveReasons.push(reason);
				}
			}
			
			return {
				// When adding stuff here, please make sure to adjust the export-function too (WagonSearchAction.java, WagonSearchTile.jsp)
				<%=WagonSearchEntity.ATTR_WAGON_TYPE_CD%>: {'id' : wagonTypeCheckCombo.getCheckedValues().join(','),'value':  wagonTypeCheckCombo.getCheckedValues().join(',') },
				<%=WagonSearchEntity.ATTR_WAGON_NO%>: {'id': wagonNoCombo.getValue(),'value': wagonNoCombo.getValue() != '' ? document.getElementById("wagonNoCombo").value : ''},
				<%=WagonSearchEntity.ATTR_LAST_KNOWN_STATION_ID%>: {'id':locationCombo.getValue(),'value': locationCombo.getValue() != '' ? document.getElementById("locationCombo").value : ''},
				<%=WagonSearchEntity.ATTR_WAGON_KEEPER_ID%>: {'id':keeperCombo.getValue(),'value': keeperCombo.getValue() != '' ? document.getElementById("keeperCombo").value : ''},
				<%=WagonSearchEntity.ATTR_ORDER_STATUS_CD%>: {'id': orderStatusCheckCombo.getCheckedValues().join(','),'value': orderStatusCheckCombo.getCheckedValues().join(',') },
				<%=WagonSearchEntity.ATTR_DISTRIBUTION_DEST_CD%>: {'id': distributionDestCheckComboBox.getCheckedValues().join(','),'value': distributionDestCheckComboBox.getCheckedValues().join(',') },
				<%=WagonSearchEntity.ATTR_DEST_RAILWAY_STATION_ID%>: {'id':destRwsCombo.getValue(),'value': destRwsCombo.getValue() != '' ? document.getElementById("destRwsCombo").value : ''},
				<%=WagonSearchEntity.ATTR_WAGON_INACTIVE_REASON_CD%>: { 'id': inactiveReasons.join(','),'value': inactiveReasons.join(',') },
				<%=WagonSearchEntity.ATTR_ACTIVE_FLAG%>: {'id' : activeFlag,'value':  activeFlag }
			}
		},
		resetSearchCrits: function() {
				orderStatusCheckCombo.selectAll();
				wagonTypeCheckCombo.selectAll();
				wagonNoCombo.reset();
				locationCombo.reset();
				keeperCombo.reset();
				wagonInactiveReasonCheckCombo.selectAll();
				//wagonInactiveReasonCheckCombo.setChecked('-1', true);
				distributionDestCheckComboBox.selectAll();
				destRwsCombo.reset();
		},		
		searchWagons: function() {
			var filters = this.getSearchCrits();
			this.searchFilters = this.getSearchCrits();
		    wagonSearchStore.load({params:{start: 0, limit: this.pageSize, sort: '', query: filters}});
		},
		storeSearchSettings: function(wagonId) {
			var filters = this.getSearchCrits();
			this.searchFilters = this.getSearchCrits();
			var start = 0;
			var limit = this.pageSize;
			var sort = '';
			var query = filters;
			WagonAdminAjaxService.storeSearchSettings(start, limit , sort,query, enterDetail(wagonId) );
		}		
	}

	function enterDetail(wagonId){
		window.location = "./WagonDetailEntry.do?wagonid="+wagonId;
	}

	var tpl = new Ext.XTemplate(
		    '<tpl for="."><div class="x-combo-list-item">{[this.render(values.wagonNo)]}</div></tpl>',		    
		    {
		        // XTemplate configuration:
		        compiled: true,
		        disableFormats: true,
		        // member functions:
		        render:function(value){
		        	var re = new RegExp("(\\d{4})(\\d{4})(\\d{3})(\\d{1})");
		        	var rendered = value;
		        	  var m = re.exec(value);
		        	  if (m != null) {
		    				rendered = "";
		    				rendered = m[1]+" "+m[2]+" "+m[3]+"-"+m[4];
		        	    }
		    			return rendered;
		        	}
		    }
		);
		

    var wagonNoCombo = new Ext.ux.TwinCombo({
    	id: 'wagonNoCombo',
        applyTo: 'wagonNoCombo',
        store: wagonNoStore,
        displayField: 'wagonNo',
        valueField: 'wagonNo',
        pageSize: 20,
        minChars:2,
        forceSelection: false,
        width: 150,
        editable: true,
        listWidth: 250,
        typeAhead: false,
        triggerAction: 'all',
        selectOnFocus: true,
        tpl:tpl
    });

	var locationCombo = new Ext.ux.TwinCombo({
    	id: 'locationCombo',
        applyTo: 'locationCombo',
        store: locationStationStore,
        displayField: 'railwayStationName',
        valueField: 'railwayStationId',
        pageSize: 20,
        width: 150,
        listWidth: 250,
        typeAhead: false,
        triggerAction: 'all',
        selectOnFocus: true
	});
	
	var wagonTypeCheckCombo = new Ext.ux.CheckComboBox({
		store: new Ext.data.JsonStore({
        	fields: [
        		{name: 'wagonTypeCd', mapping: 'wagonTypeCd'}, 
        		{name: 'wagonTypeName', mapping: 'wagonTypeName'} 
        	],
        	data : <c:out value="${admin_WagonSearchForm.json['wagonTypeList']}" escapeXml="false"/> 
    	}),
    	displayField: 'wagonTypeName',
	    checkedField: 'checked',
	    valueField: 'wagonTypeCd',
	    typeAhead: true,
	    mode: 'local',
	    triggerAction: 'all',
	    selectOnFocus: true,
	    width: 150,
	    listWidth: 150
	});
	wagonTypeCheckCombo.applyToMarkup('wagonTypeCheckCombo');
	
	var wagonInactiveReasonCheckCombo = new Ext.ux.CheckComboBox({
		store: new Ext.data.JsonStore({
        	fields: [
        		{name: 'cd', mapping: 'wagonInactiveReasonTxPK.wagonInactiveReasonBs.wagonInactiveReasonCd' },{name: 'txt'} 
        	],
        	data : [{"txt":"Active","wagonInactiveReasonTxPK":{"wagonInactiveReasonBs":{"wagonInactiveReasonCd":'-1'}}}].concat(<c:out value="${admin_WagonSearchForm.json['wagonInactiveReasonList']}" escapeXml="false"/>)
    	}),
    	displayField: 'txt',
	    checkedField: 'checked',
	    valueField: 'cd',
	    typeAhead: true,
	    mode: 'local',
	    triggerAction: 'all',
	    selectOnFocus: true,
	    width: 150,
	    listWidth: 150
	});
	wagonInactiveReasonCheckCombo.applyToMarkup('wagonInactiveReasonCheckCombo');
	
	var keeperCombo = new Ext.ux.TwinCombo({
		id: 'keeperCombo',
	    applyTo: 'keeperCombo',
	    typeAhead: false,
	    editable: false,
	    triggerAction: 'all',
	    mode: 'local',
	    store: new Ext.data.JsonStore({
        	fields: [
        		{name: 'wagonKeeperId', mapping: 'wagonKeeperId'}, 
        		{name: 'wagonKeeperName', mapping: 'wagonKeeperName'} 
        	],
        	data : <c:out value="${admin_WagonSearchForm.json['wagonKeeperList']}" escapeXml="false"/> 
    	}),
	    width: 150,
	    valueField: 'wagonKeeperId',
	    displayField: 'wagonKeeperName'
	});

	var orderStatusCheckCombo = new Ext.ux.CheckComboBox({
		store: new Ext.data.JsonStore({
        	fields: [
        		{name: 'cd', mapping: 'orderStatusTxPK.orderStatusBs.orderStatusCd' },{name: 'txt'} 
        	],
        	data : <c:out value="${admin_WagonSearchForm.json['orderStatusList']}" escapeXml="false"/>
    	}),
    	displayField: 'txt',
	    checkedField: 'checked',
	    valueField: 'cd',
	    typeAhead: true,
	    mode: 'local',
	    triggerAction: 'all',
	    selectOnFocus: true,
	    width: 150,
	    listWidth: 150
	});
	orderStatusCheckCombo.applyToMarkup('orderStatusCheckCombo');

	var distributionDestCheckComboBox = new Ext.ux.CheckComboBox({
	    store: new Ext.data.JsonStore({
        	fields: [{name: 'distributionDestCd', mapping: 'distributionDestTxPK.distributionDestBs.distributionDestCd' }, {name: 'txt'}],
        	data : <c:out value="${admin_WagonSearchForm.json['distributionDestList']}" escapeXml="false"/>
    	}),
	    displayField: 'txt',
	    checkedField: 'checked',
	    valueField: 'distributionDestCd',
	    typeAhead: true,
	    mode: 'local',
	    triggerAction: 'all',
	    emptyText:'none',
	    selectOnFocus: true,
	    width: 150,
	    listWidth: 180
	});
	distributionDestCheckComboBox.applyToMarkup('distributionDestCombo');

	var destRwsCombo = new Ext.ux.TwinCombo({
    	id: 'destRwsCombo',
        applyTo: 'destRwsCombo',
        store: destRwsStore,
        displayField: 'railwayStationName',
        valueField: 'railwayStationId',
        pageSize: 20,
        width: 150,
        listWidth: 250,
        typeAhead: false,
        triggerAction: 'all',
        selectOnFocus: true
	});

	logonPopupHideCallback = searchArea.searchWagons.createDelegate(searchArea, []);
	
	
	<%--Ext.get('divAdvancedSearch').setDisplayed(false);--%>

<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"></script></c:if>
	