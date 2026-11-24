<%@ include file="../includeTLDs.jsp"%>
<%@page import="com.crh.logon.order.web.ajax.OrderAjaxService"%>
<%@page import="com.crh.logon.utils.time.DateTimeFormat"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat"%>

<%-- For JS syntax highlighting in Eclipse --%>
<c:if test="false">
	<script type="text/javascript"></c:if>
	var initiallySelectedDistributionDestCds={
		<%=Constant.DISTRIBUTION_DEST_INTERUNIT_PROD%>: true,
		<%=Constant.DISTRIBUTION_DEST_INTERUNIT_DIST%>: true,
		<%=Constant.DISTRIBUTION_DEST_OUTBOUND%>: true
		};
		
	// --- Common properties for all filter-comboboxes ---
	Ext.form.ComboBox.prototype.shadow = false;
	Ext.form.ComboBox.prototype.emptyText = "Select...";
	Ext.form.ComboBox.prototype.minChars = 1;
	Ext.form.ComboBox.prototype.allowBlank = true;
	Ext.form.ComboBox.prototype.forceSelection = true;
	Ext.menu.Menu.prototype.shadow = false;
	
	// --- Data record and DataStore definitions ---
	var sapBpRec = Ext.data.Record.create([
	    {name: "sapId", mapping: "sapId"},
	    {name: "sapBpId", mapping: "sapBpId"},
	    {name: "name1", mapping: "name1"}
	]);
	var sapMatRec = Ext.data.Record.create([
	    {name: "sapMaterialId", mapping: "sapMaterialId"},
	    {name: "display", mapping: "display"}
	]);
	var vehicleRec = Ext.data.Record.create([
   	    {name: "compositeId" },
   	    {name: "vehicleId"},
   	 	{name: "vehicleType"},
   	 	{name: "vehicleName"}
   	]);
	var orderSearchRec = Ext.data.Record.create([
		{name: "compositeId"},
		{name: "shiptoSofficeCd"},
		{name: "shiptoSgroupCd"},
		{name: "orderId"},
	    {name: "soldtoName1"},
	    {name: "shiptoName1"},
	    {name: "shiptoCity"},
	    {name: "billtoName1"},
	    {name: "orderNumber"},
	    {name: "ldsDeliveryNoteNo"},
	    {name: "orderItemNo"},
	    {name: "materialTxt"},
	    {name: "orderedUomCd"},
	    {name: "orderedUomTxt"},
	    {name: "orderedQuantity"},
	    {name: "loadedUomCd"},
	    {name: "loadedUomTxt"},
	    {name: "loadedQuantity"},
	    {name: "deliveredUomCd"},
	    {name: "deliveredUomTxt"},
	    {name: "deliveredQuantity"},
	    {name: "returnedUomCd"},
	    {name: "returnedUomTxt"},
	    {name: "returnedQuantity"},
	    {name: "plannedDelivery"},
	    {name: "deliveryTypeTxt"},
	    {name: "distributionDestCd"},
	    {name: "shippingpointName"},
	    {name: "deliveryFrom"},
	    {name: "deliveryTo"},
	    {name: "orderStatusCd"},
	    {name: "statusTxt"},
	    {name: "warnStatusCode"},
	    {name: "timeToRedLine"},
	    {name: "poolName"},
	    {name: "commCarrierName"},
	    {name: "execCarrierName"},
	    {name: "sareaId"},
	    {name: "plantCd"},
	    {name: "plantName"},
	    {name: "sorgCd"},
	    {name: "driverFullName"},
	    {name: "vehicleName"},
	    {name: "orderValidationCode"},
	    {name: "wagonId"},
	    {name: "noteOrderMgmt"},
	    {name: "noteDelivery"},
	    {name: "noteDispatching"},
	    {name: "noteTruckDriver"},
	    {name: "noteRailwayDisp"},
	    {name: "orderNumberFromSeqUsage"},
	    {name: "shiptoAddress"},
	    {name: "loadingStr"},
		{name: "itrackStatusCd"}
	]);

    var orderSearchStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { 
        	OrderAjaxService.searchOrders(skip, count, sort, searchArea.searchFilters, searchArea.monitoring, function(data){
        		pagingBar.totalOrderCount = data.totalSize;
        		pagingBar.activePageUniqueOrdersCount = data.orderCount;
        		pagingBar.activePageOrderItemCount = data.data.length;
        		callback(data);
        	});
        	}, true),
		reader: new Ext.data.ListRangeReader({id:'compositeId', totalProperty:'totalSize'}, orderSearchRec), 
		remoteSort: true
    });

    var grid;
    // --- Search Area Handler Obj ---
	var searchArea = {
		pageSize: 25,
		monitoring: <c:out value="${mode == 'monitoring'}"/>,
		searchFilters: {},
		initSearchCrits: function (forceSystemDefault, grid) {
			this.grid = grid;
			
			var defaultSetType = whichDefaultSetToLoad(forceSystemDefault);
			if (defaultSetType == 'user') {
				applyUserDefaultValues();
			}
			else if (defaultSetType == 'system') {
				applySystemDefaultValues();
			}
		},
		getVisibleColumnWidths: function() {
			var widths = {};
			var columnModel = grid.getColumnModel();
			for (colName in defaultColumnWidths) {
				var index = columnModel.getIndexById(colName);
				if (index != -1 && !columnModel.isHidden(index)) {
					widths[colName] = columnModel.getColumnWidth(index);
				}
			}
			return widths;
		},
		getVisibleColumnIndexes: function() {
			var indexes = {};
			var columnModel = grid.getColumnModel();
			for (colName in defaultColumnIndexes) {
				var index = columnModel.getIndexById(colName);
				if (index != -1 && !columnModel.isHidden(index)) {
					indexes[colName] = index;
				}
			}
			return indexes;
		},
		getSearchCrits: function() {
			// Validation
			if ( (deliveryFromDat.getValue()) && (deliveryToDat.getValue()) && (deliveryFromDat.getValue() > deliveryToDat.getValue()) ) {
				Logon.message.warning('<fmt:message key="ordersearch.error.invalid.delivery.earlier.later"/>');
				<%-- don't return undifned, that could lead to NPE's later--%>
				deliveryFromDat.setValue(null);
				deliveryToDat.setValue(null);
			}
			// Searching by order number ==> unset all other fields.
			if (orderNumberText.getValue()) {
				resetValues(true);
			}
			
			if (ldsDeliveryNoteNoText.getValue()) {
				resetValues(false);
			}

			return {
				<%--  Note: when adding new filters, make sure to adjust the export function: see: BestellungSearchEntryAction --%>
				orderNumber: orderNumberText.getValue(),
				ldsDeliveryNoteNo: ldsDeliveryNoteNoText.getValue(),
				soldtoSapBpId: soldtoCombo.getValue(),
				shiptoSapBpId: shiptoCombo.getValue(),
				billtoSapBpId: billtoCombo.getValue(),
				sapMaterialId: sapMatCombo.getValue(),
				deliveryFromDat: deliveryFromDat.getValue() ? deliveryFromDat.value:  undefined,
				deliveryToDat: deliveryToDat.getValue() ? deliveryToDat.value : undefined,
				deliveryTypeCdList: deliveryTypeCheckCombo.getCheckedValues() ? deliveryTypeCheckCombo.getCheckedValues().join(',') : null,
				destinationCdList: toCommaDelimitedStr(distributionDestCheckComboBox.getCheckedValues()),
				shippingpointId: spCombo.getValue(),
				poolId: poolCombo.getValue(),
				execCarrierId: execCarrierCombo.getValue(),
				carrierIdList: toCommaDelimitedStr(carrierCombo.getCheckedValues()),
				statusList: toCommaDelimitedStr(orderStatusCheckComboBox.getCheckedValues()),
				creationTypeList: toCommaDelimitedStr(orderCreationTypeCheckComboBox.getCheckedValues()),
				sareaId: sareaCombo.getValue(),
				shiptoSofficeCd: sofficeCombo.getValue(),
				shiptoSgroupCd: sgroupCombo.getValue(),
				plantCdList: toCommaDelimitedStr(plantCombo.getCheckedValues()),
				vehicleCompositeId: vehicleCombo.getValue(),
				vehicleName: vehicleCombo.lastSelectionText,
				matPackagingType: matPackagingTypeCombo.getValue()
<c:if test="${mode == 'monitoring'}">
				,
				liveFlag: autoRefreshField.getValue()
</c:if>
			}
		},
		searchOrders: function(startRecord) {
			var filters = this.getSearchCrits();
			this.searchFilters = this.getSearchCrits();
			startRecord = 0;
		    orderSearchStore.load({params:{start: startRecord, limit: this.pageSize, sort: '', query: filters}});
		    pagingBar.setSearchArea(this);
		    <c:if test="${mode != 'monitoring'}">
		    var d = new Date();
			d.setHours(0);
			d.setMilliseconds(0);
			d.setMinutes(0);
			d.setSeconds(0);
		    if (deliveryFromDat.getValue() && (deliveryFromDat.getValue().getTime() == d.getTime())) {
		    	calcQuantities();
		    }
		    else {
			    clearQuantities();
		    }
		    </c:if>
		},
		updatePagingBar: function(startRecord){			
			OrderAjaxService.getOrderSearchInformations(startRecord, this.pageSize, '', this.searchFilters, this.monitoring, function(res) {
				pagingBar.update(res);
				<c:if test="${mode != 'monitoring'}">
				orderItemCount.setValue(res.totalOrderCount);
				</c:if>
		    });
		}, 
		
		searchOrdersKeepPaging: function(){
			var startRecord = 0;
			if (!(pagingBar === undefined) && !(pagingBar.cursor === undefined) && !(pagingBar.pageSize === undefined)) {
				var activePage = Math.ceil((pagingBar.cursor + pagingBar.pageSize) / pagingBar.pageSize);
				startRecord = activePage * pagingBar.pageSize - pagingBar.pageSize;
				if (isNaN(startRecord)){
					startRecord = 0;
				}
			}
			this.searchOrders(startRecord);
		},		
		advancedSearch: false,
		toggleAdvancedSearch : function() {
			if (this.advancedSearch) {
				Ext.get("buttonToggleAdvanced").dom.value = '<fmt:message key="buttons.expand"/>';
				Ext.get("buttonToggleAdvanced").removeClass('SmallButtonStyleUp');
				Ext.get("buttonToggleAdvanced").addClass('SmallButtonStyleDown');
				Ext.get("divAdvancedSearch").slideOut('t', {
				    useDisplay: true
				});
			}
			else {
				Ext.get("buttonToggleAdvanced").dom.value = '<fmt:message key="buttons.collapse"/>';
				Ext.get("buttonToggleAdvanced").removeClass('SmallButtonStyleDown');
				Ext.get("buttonToggleAdvanced").addClass('SmallButtonStyleUp');
				Ext.get("divAdvancedSearch").show();
			}
			this.advancedSearch = !this.advancedSearch; 
		}
	}
	
	function whichDefaultSetToLoad(forceSystemDefault) {
		var applyUserDefaultSettings = false;
		<c:if test="${order_bestellung_BestellungSearchForm.applyUserDefaultSettings}">
			applyUserDefaultSettings = true;
		</c:if>
	
		if (forceSystemDefault == true) {
			return "system";
		}
		else if (applyUserDefaultSettings == true) {
			return "user";
		}
		else {
			return "system";
		}
	}

    // ------ PAGER ---------
    var pagingBar = new Ext.ux.grid.OrderCountingPagingToolbar({
        pageSize: searchArea.pageSize,
        store: orderSearchStore,
        displayInfo: true,
        displayMsg: '<fmt:message key="ordersearch.displayOrderMsgParametered"/>',
        emptyMsg: '<fmt:message key="ordersearch.emptyMsg"/>'
    });
    
	// --- Filters: Comboboxes, datepickets, etc. Part 1: SIMPLE search
	var soldtoCombo = new Ext.ux.TwinCombo({
		id: 'soldtoCombo',
		applyTo: 'soldtoCombo',
		store: new Ext.data.SimpleStore({
			fields: ['id', 'name'],
			data : [
				<c:if test="${!empty order_bestellung_BestellungSearchForm.soldtoDto}">
					[<c:out value="${order_bestellung_BestellungSearchForm.soldtoDto.id}" escapeXml="false"/>,
					'<c:out value="${order_bestellung_BestellungSearchForm.soldtoDto.name}" escapeXml="false"/>'
					]
				</c:if>
			]
		}),
		displayField: 'name',
		valueField: 'id',
		pageSize: 20,
		width: 180,
		listWidth: 250,
		typeAhead: false,
		triggerAction: 'all',
		selectOnFocus: true
	});
	
	var shiptoCombo = new Ext.ux.TwinCombo({
		id: 'shiptoCombo',
		applyTo: 'shiptoCombo',
		store: new Ext.data.SimpleStore({
			fields: ['id', 'name'],
			data : [
				<c:if test="${!empty order_bestellung_BestellungSearchForm.shiptoDto}">
					[<c:out value="${order_bestellung_BestellungSearchForm.shiptoDto.id}" escapeXml="false"/>,
					'<c:out value="${order_bestellung_BestellungSearchForm.shiptoDto.name}" escapeXml="false"/>'
					]
				</c:if>
			]
		}),
		displayField: 'name',
		valueField: 'id',
		pageSize: 20,
		width: 180,
		listWidth: 250,
		typeAhead: false,
		triggerAction: 'all',
		selectOnFocus: true
	});
	
	var billtoCombo = new Ext.ux.TwinCombo({
		id: 'billtoCombo',
		applyTo: 'billtoCombo',
		store: new Ext.data.SimpleStore({
			fields: ['id', 'name'],
			data : [
				<c:if test="${!empty order_bestellung_BestellungSearchForm.billtoDto}">
					[<c:out value="${order_bestellung_BestellungSearchForm.billtoDto.id}" escapeXml="false"/>,
					'<c:out value="${order_bestellung_BestellungSearchForm.billtoDto.name}" escapeXml="false"/>'
					]
				</c:if>
			]
		}),
		displayField: 'name',
		valueField: 'id',
		pageSize: 20,
		width: 180,
		listWidth: 250,
		typeAhead: false,
		triggerAction: 'all',
		selectOnFocus: true
	});
	
	var sapMatCombo = new Ext.ux.TwinCombo({
		id: 'sapMatCombo',
		applyTo: 'sapMatCombo',
		store: new Ext.data.SimpleStore({
			fields: ['id', 'name'],
			data : [
				<c:if test="${!empty order_bestellung_BestellungSearchForm.sapMaterialDto}">
					[<c:out value="${order_bestellung_BestellungSearchForm.sapMaterialDto.id}" escapeXml="false"/>,
					'<c:out value="${order_bestellung_BestellungSearchForm.sapMaterialDto.name}" escapeXml="false"/>'
					]
				</c:if>
			]
		}),
		displayField: 'name',
		valueField: 'id',
		pageSize: 20,
		width: 180,
		listWidth: 300,
		typeAhead: false,
		triggerAction: 'all',
		selectOnFocus: true
	});

	var matPackagingTypeStore = new Ext.data.SimpleStore({
		fields: ['cd', 'txt'],
		data : [ 
			<c:forEach var="materialPackageTypeBean" varStatus="status" items="${order_bestellung_BestellungSearchForm.materialPackageTypeBean}">
				[
					'<c:out value="${materialPackageTypeBean.materialPackageCd}"/>',
					'<c:out value="${materialPackageTypeBean.materialPackageTxt}"/>'
				]
				<c:if test="${!status.last}">,</c:if>
			</c:forEach>
		]
	});
	
	var matPackagingTypeCombo = new Ext.ux.TwinCombo({
		id: 'matPackagingTypeCombo',
		applyTo:'matPackagingTypeCombo',
		selectOnFocus: true,
		editable: false,
		typeAhead: false,
		triggerAction: 'all',
		store: matPackagingTypeStore,
		mode: 'local',
		width: 180,
		listWidth: 250,
		valueField: 'cd',
		displayField: 'txt'
	});

	// ---- Delivery from / to ----
	var deliveryFromDat = new Ext.form.DateField({
		id: 'deliveryFromDat',
    	format: '<%=DateTimeFormat.getDateFormat4JS(pageContext
						.getRequest().getLocale())%>',
    	altFormats: '<%=DateTimeFormat.getDateAltFormats4JS(pageContext
						.getRequest().getLocale())%>',
    	applyTo: 'deliveryFromDatStr',
    	width: 100
	});
	
	var deliveryToDat = new Ext.form.DateField({
		id: 'deliveryToDat',
    	format: '<%=DateTimeFormat.getDateFormat4JS(pageContext
						.getRequest().getLocale())%>',
    	altFormats: '<%=DateTimeFormat.getDateAltFormats4JS(pageContext
						.getRequest().getLocale())%>',
    	applyTo: 'deliveryToDatStr',
    	width: 100
	});
    
	// --- Filters: Comboboxes, datepickets, etc. Part 2: ADVANCED search
    
	// --- Delivery type ---
	var deliveryTypeCheckCombo = new Ext.ux.CheckComboBox({
		store: new Ext.data.JsonStore({
        	fields: [
        		{name: 'deliveryTypeCd', mapping: 'deliveryTypeTxPK.deliveryTypeBs.deliveryTypeCd'}, 
        		{name: 'txt', mapping: 'txt'},
        		{name: 'checked', mapping: 'checked'}
        	],
        	data : <c:out value="${order_bestellung_BestellungSearchForm.json['deliveryTypeList']}" escapeXml="false"/> 
    	}),
	    displayField: 'txt',
	    checkedField: 'checked',
	    valueField: 'deliveryTypeCd',
	    typeAhead: true,
	    mode: 'local',
	    triggerAction: 'all',
	    emptyText:'inactive',
	    selectOnFocus: true,
	    width: 180,
	    listWidth: 200
	});
	deliveryTypeCheckCombo.applyToMarkup('deliveryTypeCd');
	
	var distributionDestStore = new Ext.data.SimpleStore({
	    fields: ['cd', 'txt', 'checked'],
	    data : [ 
	<c:forEach var="distributionDest" varStatus="status" items="${order_bestellung_BestellungSearchForm.distributionDestCdList}">
		[
	'<c:out value="${distributionDest.distributionDestTxPK.distributionDestBs.distributionDestCd}"/>',
			'<c:out value="${distributionDest.txt}"/>',
			true
		]
		<c:if test="${!status.last}">,</c:if>
	</c:forEach>
	    ]
	});

	var distributionDestCheckComboBox = new Ext.ux.CheckComboBox({
	    store: distributionDestStore,
	    displayField: 'txt',
	    checkedField: 'checked',
	    valueField: 'cd',
	    typeAhead: true,
	    mode: 'local',
	    triggerAction: 'all',
	    emptyText:'inactive',
	    selectOnFocus: true,
	    width: 180,
	    listWidth: 200
	});
	distributionDestCheckComboBox.applyToMarkup('distributionDestCdField');

	// --- Shipping point ---
	var spCombo = new Ext.ux.TwinCombo({
		id: 'spCombo',
	    applyTo:'shippingpointId',
		selectOnFocus: true,
		editable: false,
	    typeAhead: false,
	    triggerAction: 'all',
	    store: new Ext.data.JsonStore({
        	fields: ['shippingpointId', 'shippingpointName'],
        	data : <c:out value="${order_bestellung_BestellungSearchForm.json['shippingpointList']}" escapeXml="false"/>
    	}),
	    mode: 'local',
	    width: 180,
	    listWidth: 250,
	    valueField: 'shippingpointId',
	    displayField: 'shippingpointName'
	});
	
	var carrierComboDefaultValues = <c:out value="${order_bestellung_BestellungSearchForm.json['commCarrierList']}" escapeXml="false"/>;
	var execCarrierComboDefaultValues = <c:out value="${order_bestellung_BestellungSearchForm.json['execCarrierList']}" escapeXml="false"/>;
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
        	data : <c:out value="${order_bestellung_BestellungSearchForm.json['poolList']}" escapeXml="false"/> 
    	}),
	    mode: 'local',
	    width: 180,
	    listWidth: 250,
	    valueField: 'poolTxPK.poolId',
	    displayField: 'name',
	    onTrigger2Click: function() {
	    	if(this.getValue()) {
	    		this.clearValue();
	    		carrierCombo.selectNone();
	    		carrierCombo.store.loadData(carrierComboDefaultValues);
	    		carrierCombo.initList();
	    	}
	    }
	});
	
	poolCombo.on('select', function(combo, record) {
		loadCarrierComboByPool(record.data['poolTxPK.poolId']);
	});
	
	// --- Carrier ---
	var carrierCombo = new Ext.ux.CheckComboBox({
		id: 'carrierCombo',
	    applyTo:'carrierId',
		selectOnFocus: true,
		checkedField: 'checked',
		typeAhead: true,
	    triggerAction: 'all',
	    useBulkButtons: false,
	    emptyText:'inactive',
	    store: new Ext.data.JsonStore({
	    	fields: [{name: 'carrierName', 
	    				convert: function(v,rec) {
	    					if(!v.trim()){
	    						return rec.displayName;
	    					} else {
	    						return v;
	    					}
	    				} 
	    			},
	    	        {name : 'id', mapping: 'carrierId'}
	    	         ],
        	data : carrierComboDefaultValues 
    	}),
	    mode: 'local',
	    width: 180,
	    listWidth: 250,
	    valueField: 'id',
	    displayField: 'carrierName'
	});
	
	carrierCombo.on('change', function(combo, record) {
		execCarrierCombo.clearValue();
		loadExecCarrierComboByCommCarrier(toCommaDelimitedStr(combo.getCheckedValues()));
	});
	
	var execCarrierCombo = new Ext.ux.TwinCombo({
		id: 'execCarrierCombo',
	    applyTo:'execCarrierId',
	    typeAhead: false,
		triggerAction: 'all',
		selectOnFocus: true,
	    store: new Ext.data.JsonStore({
	    	fields: [{name: 'displayName', 
	    				convert: function(v,rec) {
	    					if(rec.execCarrierDisplayName){
	    						return rec.execCarrierDisplayName;
	    					} else {
	    						return v;
	    					}
	    				} 
	    			},
	    			{name: 'carrierId', 
	    				convert: function(v,rec) {
	    					if(rec.execCarrierId){
	    						return rec.execCarrierId;
	    					} else {
	    						return v;
	    					}
	    				} 
	    			}
	    	         ],
        	data : execCarrierComboDefaultValues
    	}),
	    mode: 'local',
	    width: 180,
	    listWidth: 250,
	    valueField: 'carrierId',
	    displayField: 'displayName'
	});
	
	function loadCarrierComboByPool(poolId) {
		carrierCombo.selectNone();
		OrderAjaxService.getCarrierListByPoolId(poolId, function(res) {
			carrierCombo.store.loadData(res);
			carrierCombo.initList();
		});
	}
	
	function loadExecCarrierComboByCommCarrier(carrierIds) {
		if(!carrierIds.trim()){
			execCarrierCombo.store.loadData(execCarrierComboDefaultValues);
		} else { 
			OrderAjaxService.getExecCarrierListByPoolId(carrierIds, function(res) {
				execCarrierCombo.store.loadData(res);
			});
		}
	}
	
	var orderNumberText = new Ext.form.TextField({
		id: 'orderNumberText',
		applyTo: 'orderNumber',
	    width: 180,
		maskRe: /\d/i 
	});
	
	var ldsDeliveryNoteNoText = new Ext.form.TextField({
		id: 'ldsDeliveryNoteNoText',
		applyTo: 'ldsDeliveryNoteNo',
	    width: 180
	});

<c:if test="${mode == 'monitoring'}">
	// ---- Delivery from / to ----
	var autoRefreshField = new Ext.form.Checkbox({
		id: 'autoRefreshField',
		applyTo: 'autoRefresh',
		handler: function(element, checked) {
			orderMonitor.stop();
			if (checked) {
				orderMonitor.start();
			}
		},
		width: 40
	});
</c:if>

<c:if test="${mode == 'monitoring'}">
var orderStatusOffByDefault = new Array('13', '14'); 
</c:if>

function applyColumnWidths(colName, width, typeOfDefaults) {
	var columnModel = grid.getColumnModel();
	var index = columnModel.getIndexById(colName);
	if (index != -1) {
		var visible;
		if (typeOfDefaults == 'user') {
			visible = (width && width > 0);
		}
		else if (typeOfDefaults == 'system') {
			visible = (defaultVisibleColumns.indexOf(colName) != -1);
		}
		if (visible == true) {
			columnModel.setColumnWidth(index, width);
		}
		columnModel.setHidden(index, !visible);
	}
}

function applyColumnIndexes() {
	
	for (var column in defaultColumns) {
		var col = defaultColumns[column];
		var id = col.id;
		col["customIndex"] = defaultColumnIndexes[id];
	}
	defaultColumns.sort(function(a, b) {
	    return a.customIndex - b.customIndex;
	});
	var cm = new Ext.grid.ColumnModel(defaultColumns);
	grid.reconfigure(grid.getStore(), cm);
}

function orderStatusDefault(orderStatusCd) {
<c:choose>
	<c:when test="${mode == 'monitoring'}">
		return orderStatusOffByDefault.indexOf(orderStatusCd) < 0;
	</c:when>
	<c:otherwise>
		return	true;
	</c:otherwise>
</c:choose>
}

var orderStatusStore = new Ext.data.SimpleStore({
    fields: ['cd', 'txt', 'checked'],
    data : [ 
<c:forEach var="orderStatus" varStatus="status" items="${order_bestellung_BestellungSearchForm.orderStatusList}">
	[
		'<c:out value="${orderStatus.orderStatusTxPK.orderStatusBs.orderStatusCd}"/>',
		'<c:out value="${orderStatus.txt}"/>',
		orderStatusDefault('<c:out value="${orderStatus.orderStatusTxPK.orderStatusBs.orderStatusCd}"/>')
	]
	<c:if test="${!status.last}">,</c:if>
</c:forEach>
    ]
});

var orderStatusCheckComboBox = new Ext.ux.CheckComboBox({
    store: orderStatusStore,
    displayField: 'txt',
    checkedField: 'checked',
    valueField: 'cd',
    typeAhead: true,
    mode: 'local',
    triggerAction: 'all',
    emptyText:'inactive',
    selectOnFocus: true,
    width: 180,
    listWidth: 200
});
orderStatusCheckComboBox.applyToMarkup('orderStatusField');

var orderCreationTypeStore = new Ext.data.SimpleStore({
    fields: ['cd', 'txt', 'checked'],
    data : [ 
<c:forEach var="orderCreationTx" varStatus="status" items="${order_bestellung_BestellungSearchForm.orderCreationTypeList}">
	[
'<c:out value="${orderCreationTx.orderCreationTypeTxPK.orderCreationTypeBs.orderCreationTypeCd}"/>',
		'<c:out value="${orderCreationTx.txt}"/>',
		true
	]
	<c:if test="${!status.last}">,</c:if>
</c:forEach>
    ]
});

var orderCreationTypeCheckComboBox = new Ext.ux.CheckComboBox({
    store: orderCreationTypeStore,
    displayField: 'txt',
    checkedField: 'checked',
    valueField: 'cd',
    typeAhead: true,
    mode: 'local',
    triggerAction: 'all',
    emptyText:'inactive',
    selectOnFocus: true,
    width: 180,
    listWidth: 200,
    getCheckedValues: function() {
    	//if all is selected then do not send the filter parameter at all, there is no such as none, just all
    	var res = [];
    	var count = 0;
    	this.store.each(function(record) {
    		if (record.data[this.checkedField]) {
    			res[count++] = record.data[this.valueField];
    		}
			return true
		}, this);
		if (count == this.store.getTotalCount()) {
			return [];
		}    	
		else{
    		return res;
		}
    }
});
orderCreationTypeCheckComboBox.applyToMarkup('orderCreationTypeField');


var sareaCombo = new Ext.ux.TwinCombo({
	id: 'sareaCombo',
    applyTo:'sareaId',
	selectOnFocus: true,
	editable: false,
    typeAhead: false,
    triggerAction: 'all',
    store: new Ext.data.JsonStore({
    	fields: ['sareaId', 'sareaDisplayLongname'],
    	data : <c:out value="${order_bestellung_BestellungSearchForm.json['sareaList']}" escapeXml="false"/>,
    	sortInfo: {field: "sareaDisplayLongname", direction: "ASC"} 
	}),
    mode: 'local',
    width: 180,
    listWidth: 250,
    valueField: 'sareaId',
    displayField: 'sareaDisplayLongname'
});

var sofficeCombo = new Ext.ux.TwinCombo({
	id: 'sofficeCombo',
    applyTo:'sofficeCd',
	selectOnFocus: true,
	editable: false,
    typeAhead: false,
    triggerAction: 'all',
    store: new Ext.data.JsonStore({
    	fields: ['sofficeCd', 'sofficeName'],
    	data : <c:out value="${order_bestellung_BestellungSearchForm.json['sofficeList']}" escapeXml="false"/>,
    	sortInfo: {field: "sofficeName", direction: "ASC"} 
	}),
    mode: 'local',
    width: 180,
    listWidth: 250,
    valueField: 'sofficeCd',
    displayField: 'sofficeName'
});

var sgroupCombo = new Ext.ux.TwinCombo({
	id: 'sgroupCombo',
    applyTo:'sgroupCd',
	selectOnFocus: true,
	editable: false,
    typeAhead: false,
    triggerAction: 'all',
    store: new Ext.data.JsonStore({
    	fields: ['sgroupCd', 'sgroupName'],
    	data : <c:out value="${order_bestellung_BestellungSearchForm.json['sgroupList']}" escapeXml="false"/>,
    	sortInfo: {field: "sgroupName", direction: "ASC"} 
	}),
    mode: 'local',
    width: 180,
    listWidth: 250,
    valueField: 'sgroupCd',
    displayField: 'sgroupName'
});

var plantCombo = new Ext.ux.CheckComboBox({
	id: 'plantCombo',
    applyTo:'plantCd',
	selectOnFocus: true,
	typeAhead: true,
    triggerAction: 'all',
    emptyText:'inactive',
    store: new Ext.data.JsonStore({
    	fields: ['value', 'name'],
    	data : <c:out value="${order_bestellung_BestellungSearchForm.json['plantList']}" escapeXml="false"/>,
    	sortInfo: {field: "name", direction: "ASC"} 
	}),
    mode: 'local',
    width: 180,
    listWidth: 250,
    valueField: 'value',
    displayField: 'name'
});

var vehicleCombo = new Ext.ux.TwinCombo({
	id: 'vehicleCombo',
	applyTo: 'vehicleCompositeId',
	store: new Ext.data.SimpleStore({
		fields: ['compositeId', 'vehicleName'],
		data : [
			<c:if test="${!empty order_bestellung_BestellungSearchForm.vehicleCompositeId}">
				['<c:out value="${order_bestellung_BestellungSearchForm.vehicleCompositeId}" escapeXml="false"/>',
				'<c:out value="${order_bestellung_BestellungSearchForm.vehicleName}" escapeXml="false"/>'
				]
			</c:if>
		]
	}),
	displayField: 'vehicleName',
	valueField: 'compositeId',
	pageSize: 20,
	width: 180,
	listWidth: 250,
	typeAhead: false,
	triggerAction: 'all',
	selectOnFocus: true
});

// Period selection, TODO boon 20091126: if the button with popup is going to be re-used, please make a component out of it.
var periodSelector = new Ext.ux.PeriodSelector({
	fromDateComponent: deliveryFromDat,
	toDateComponent: deliveryToDat
});
periodSelector.yesterday.text = '<fmt:message key="auftragbearbeitung.texte.gestern"/>'; 
periodSelector.today.text = '<fmt:message key="auftragbearbeitung.texte.heute"/>';
periodSelector.tomorrow.text = '<fmt:message key="auftragbearbeitung.texte.morgen"/>';
periodSelector.week.text = '<fmt:message key="auftragbearbeitung.texte.w"/>';
periodSelector.month.text = '<fmt:message key="auftragbearbeitung.texte.m"/>';
periodSelector.year.text = '<fmt:message key="auftragbearbeitung.texte.YTD"/>';

var el = document.createElement("div");
var periodPanel = new Ext.Panel({
	renderTo: el,
	layout: 'fit',
	border: true,
	items: [ periodSelector ]
});

var periodPanelLayer = new Ext.Layer();
periodPanelLayer.appendChild(periodPanel.getEl());

periodSelectorButton = new Ext.Button({
	text: '<img src="../images/add.gif"/>',
	template: new Ext.Template(
            '<table border="0" cellpadding="0" cellspacing="0"><tbody><tr>',
            '<td><em unselectable="on"><button type="{1}">{0}</button></em></td>',
            '</tr></tbody></table>'),
	handler: function() {
		periodPanelLayer.toggle();
		if (periodPanelLayer.isVisible()) {
			periodPanelLayer.alignTo('periodSelectorTriggerId');
		}
	},
	renderTo: 'periodSelectorTriggerId'
});

Ext.getDoc().on("mousedown", function(e) {
	if ( (periodPanelLayer.isVisible()) && 
			(!e.within(periodPanel.getEl())) &&
			(!e.within(periodSelectorButton.getEl())) ) { 
		periodPanelLayer.hide();
	}
});
<%-- <jsp:include page="CarriersTile.jsp"/> --%>
		
	logonPopupHideCallback = searchArea.searchOrdersKeepPaging.createDelegate(searchArea, []);
	
	//only purpose is to hide the popup and resend all present selections (do not reload completely)
	onOrderDetailCloseForward = hidePopupCallFunction;
	
	//carrierCombo.on('disable', function() { this.repaint(); });
	
	Ext.get('divAdvancedSearch').setDisplayed(false);

<c:if test="${mode != 'monitoring'}">
	var summedQuantity = new Ext.form.TextField({
		id: 'summedQuantityText',
		applyTo: 'summedQuantity',
		readOnly: true
	});

	var orderItemCount = new Ext.form.TextField({
		id: 'orderItemCountText',
		applyTo: 'orderItemCount',
		readOnly: true
	});

	function calcQuantities() {
		summedQuantity.setValue(null);
		OrderAjaxService.getSummedQuantities(searchArea.getSearchCrits(), {
			callback: function(sum) {
				summedQuantity.setValue(sum + ' t');
			},
			requestAttributes: 'dontserialize'
		});
	}

	function clearQuantities() {

		summedQuantity.setValue(null);
		orderItemCount.setValue(null);
	}
	
</c:if>
	
	function applySystemDefaultValues() {
		resetValues();
	
		var d = new Date();
		d.setHours(0);
		d.setMilliseconds(0);
		d.setMinutes(0);
		d.setSeconds(0);
		deliveryFromDat.setValue(d);
		// Prepare other stuff
		//default setting - mustn't add filtering if ordernumber is given
		orderStatusCheckComboBox.selectAll();
		
		orderCreationTypeCheckComboBox.selectAll();
		
		plantCombo.selectNone();
		
		applyColumnIndexes();
		
		for (var col in defaultColumnWidths) {
			var width = defaultColumnWidths[col];
			applyColumnWidths(col, width, 'system');
		}
		
		grid.width = Ext.get('divSimpleSearch').getWidth();
		grid.render();				
	}
	
	function applyUserDefaultValues() {
		//Live field; boolean type
		//autoRefreshField.checked shouldn't be set here, as it would trigger orderMonitor.start,
		//however orderMonitor might still be uninitialized here
		<c:if test="${mode == 'monitoring'}">
			<c:choose>
				<c:when test="${order_bestellung_BestellungSearchForm.liveFlag}">
					autoRefresh = true;
				</c:when>
				<c:otherwise>
					autoRefresh = false;
				</c:otherwise>
			</c:choose>
		</c:if>
		
		//Delivery From field; date type
		<c:if test="${!empty order_bestellung_BestellungSearchForm.defaultDeliveryFromString}">
			deliveryFromDat.setValue('<c:out value="${order_bestellung_BestellungSearchForm.defaultDeliveryFromString}"/>');
		</c:if>
		
		//Delivery To field; date type
		if (!deliveryToDat.getValue()) {
			<c:if test="${!empty order_bestellung_BestellungSearchForm.defaultDeliveryToString}">
				deliveryToDat.setValue('<c:out value="${order_bestellung_BestellungSearchForm.defaultDeliveryToString}"/>');
			</c:if>
		}

		//Type field; Ext.ux.CheckComboBox type with object
		<c:if test="${!empty order_bestellung_BestellungSearchForm.distributionDestCdList}">
			var selectedDistributionDestCds = [];
			<c:forEach var="defaultDestinationCd" varStatus="status" items="${order_bestellung_BestellungSearchForm.defaultDestinationCdList}">
				selectedDistributionDestCds.push('<c:out value="${defaultDestinationCd}"/>');
			</c:forEach>
			storeData = [ 
				<c:forEach var="distributionDest" varStatus="status" items="${order_bestellung_BestellungSearchForm.distributionDestCdList}">
					[
						'<c:out value="${distributionDest.distributionDestTxPK.distributionDestBs.distributionDestCd}"/>',
						'<c:out value="${distributionDest.txt}"/>',
						selectedDistributionDestCds.indexOf('<c:out value="${distributionDest.distributionDestTxPK.distributionDestBs.distributionDestCd}"/>') != -1 ? true : false
					]
					<c:if test="${!status.last}">,</c:if>
				</c:forEach>
			];
			distributionDestStore = new Ext.data.SimpleStore({
				fields: ['cd', 'txt', 'checked'],
				data : storeData
			});
			distributionDestCheckComboBox.store = distributionDestStore;
			distributionDestCheckComboBox.refreshLabel(); //just by setting the store above, the label on the unopened combobox won't be set correctly
		</c:if>
		
		//Transportation mode field; Ext.ux.CheckComboBox with Long
		<c:forEach var="defaultDeliveryTypeCd" varStatus="status" items="${order_bestellung_BestellungSearchForm.defaultDeliveryTypeCdList}">
			deliveryTypeCheckCombo.store.each(function(record) {
				if (record.data[deliveryTypeCheckCombo.valueField] === <c:out value="${defaultDeliveryTypeCd}"/>) {
					deliveryTypeCheckCombo.setChecked(record.data[deliveryTypeCheckCombo.valueField], true);
				}
				return true
			}, this);
		</c:forEach>
		
		//Order status field; Ext.ux.CheckComboBox with Long
		<c:if test="${!empty order_bestellung_BestellungSearchForm.defaultOrderStatusList}">
			orderStatusCheckComboBox.selectNone();
			<c:forEach var="defaultOrderStatus" varStatus="status" items="${order_bestellung_BestellungSearchForm.defaultOrderStatusList}">
				orderStatusCheckComboBox.setChecked(<c:out value="${defaultOrderStatus}"/>, true);
			</c:forEach>
		</c:if>
		
		//Mat. Packaging Type field; Ext.ux.CheckComboBox with object
		<c:if test="${!empty order_bestellung_BestellungSearchForm.matPackagingType}">
			matPackagingTypeCombo.setValue('<c:out value="${order_bestellung_BestellungSearchForm.matPackagingType}"/>');
		</c:if>
		
		//Order Creation Type field; Ext.ux.CheckComboBox with object
		<c:if test="${!empty order_bestellung_BestellungSearchForm.defaultOrderCreationTypeList}">
			orderCreationTypeCheckComboBox.store.each(function(record) {
				orderCreationTypeCheckComboBox.setChecked(record.data[orderCreationTypeCheckComboBox.valueField], false);
			}, this);
			<c:forEach var="defaultOrderCreationType" varStatus="status" items="${order_bestellung_BestellungSearchForm.defaultOrderCreationTypeList}">
				orderCreationTypeCheckComboBox.store.each(function(record) {
					if (record.data[orderCreationTypeCheckComboBox.displayField] === '<c:out value="${defaultOrderCreationType.txt}"/>') {
						orderCreationTypeCheckComboBox.setChecked(record.data[orderCreationTypeCheckComboBox.valueField], true);
					}
					return true
				}, this);
			</c:forEach>
		</c:if>
		
		//fields of type TwinCombo, jsonstore
		//Pool
		<c:if test="${!empty order_bestellung_BestellungSearchForm.poolId}">
		poolCombo.store.each(function(record) {
				if (record.data[poolCombo.valueField] == <c:out value="${order_bestellung_BestellungSearchForm.poolId}"/>) {
					poolCombo.preselectValue(record.data[poolCombo.valueField], true);
				}
				return true
			});
		</c:if>
		
		//executing carrier
		<c:if test="${!empty order_bestellung_BestellungSearchForm.execCarrierId}">
		execCarrierCombo.store.each(function(record) {
				if (record.data[execCarrierCombo.valueField] == <c:out value="${order_bestellung_BestellungSearchForm.execCarrierId}"/>) {
					execCarrierCombo.preselectValue(record.data[execCarrierCombo.valueField], true);
				}
				return true
			});
		</c:if>
		<c:if test="${!empty order_bestellung_BestellungSearchForm.defaultCarrierIdList}">
		
		//commercial carrier multiselect
		<c:forEach var="carrierId" varStatus="status" items="${order_bestellung_BestellungSearchForm.defaultCarrierIdList}">
			carrierCombo.store.each(function(record) {
				if (record.data[carrierCombo.valueField] == <c:out value="${carrierId}"/>) {
					carrierCombo.setChecked(record.data[carrierCombo.valueField], true);
				}
				return true
				});
		</c:forEach>
			loadExecCarrierComboByCommCarrier(toCommaDelimitedStr(carrierCombo.getCheckedValues()));
		</c:if>
		<c:if test="${!empty order_bestellung_BestellungSearchForm.sareaId}">
		sareaCombo.store.each(function(record) {
				if (record.data[sareaCombo.valueField] == <c:out value="${order_bestellung_BestellungSearchForm.sareaId}"/>) {
					sareaCombo.preselectValue(record.data[sareaCombo.valueField], true);
				}
				return true
			});
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.shiptoSofficeCd}">
		sofficeCombo.store.each(function(record) {
				if (record.data[sofficeCombo.valueField] == '<c:out value="${order_bestellung_BestellungSearchForm.shiptoSofficeCd}"/>') {
					sofficeCombo.preselectValue(record.data[sofficeCombo.valueField], true);
				}
				return true
			});
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.shiptoSgroupCd}">
		sgroupCombo.store.each(function(record) {
				if (record.data[sgroupCombo.valueField] == '<c:out value="${order_bestellung_BestellungSearchForm.shiptoSgroupCd}"/>') {
					sgroupCombo.preselectValue(record.data[sgroupCombo.valueField], true);
				}
				return true
			});
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.defaultPlantCdList}">
		plantCombo.selectNone();
			<c:forEach var="plantCd" varStatus="status" items="${order_bestellung_BestellungSearchForm.defaultPlantCdList}">
			plantCombo.store.each(function(record) {
					if (record.data[plantCombo.valueField] == '<c:out value="${plantCd}"/>') {
						plantCombo.setChecked(record.data[plantCombo.valueField], true);
					}
					return true
					});
			</c:forEach>
		</c:if>
		
		//Shipping point field; TwinCombo, jsonstore
		<c:if test="${!empty order_bestellung_BestellungSearchForm.shippingpointId}">
			spCombo.store.each(function(record) {
				if (record.data[spCombo.valueField] == <c:out value="${order_bestellung_BestellungSearchForm.shippingpointId}"/>) {
					spCombo.preselectValue(record.data.shippingpointId, true);
				}
				return true
			});
		</c:if>

		//fields of type TwinCombo, with ajax dwr proxy
		<c:if test="${!empty order_bestellung_BestellungSearchForm.soldtoSapBpId}">
			var soldtoSapBpId = <c:out value="${order_bestellung_BestellungSearchForm.soldtoSapBpId}"/>;
			soldtoCombo.preselectValue(soldtoSapBpId, true);
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.shiptoSapBpId}">
			var shiptoSapBpId = <c:out value="${order_bestellung_BestellungSearchForm.shiptoSapBpId}"/>;
			shiptoCombo.preselectValue(shiptoSapBpId, true);
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.billtoSapBpId}">
			var billtoSapBpId = <c:out value="${order_bestellung_BestellungSearchForm.billtoSapBpId}"/>;
			billtoCombo.preselectValue(billtoSapBpId, true);
		</c:if>

		<c:if test="${!empty order_bestellung_BestellungSearchForm.sapMaterialId}">
			var sapMaterialId = <c:out value="${order_bestellung_BestellungSearchForm.sapMaterialId}"/>;
			sapMatCombo.preselectValue(sapMaterialId, true);
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.vehicleCompositeId}">
			var vehicleCompositeId = '<c:out value="${order_bestellung_BestellungSearchForm.vehicleCompositeId}"/>';
			vehicleCombo.preselectValue(vehicleCompositeId, true);
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wTimeToRedLine}">
			applyColumnWidths("timeToRedLine", <c:out value="${order_bestellung_BestellungSearchForm.wTimeToRedLine}"/>, 'user');
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wOrderNumber}">
			applyColumnWidths("orderNumber", <c:out value="${order_bestellung_BestellungSearchForm.wOrderNumber}"/>, 'user');
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wLdsDeliveryNoteNo}">
			applyColumnWidths("ldsDeliveryNoteNo", <c:out value="${order_bestellung_BestellungSearchForm.wLdsDeliveryNoteNo}"/>, 'user');
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wDistributionDestCd}">
			applyColumnWidths("distributionDestCd", <c:out value="${order_bestellung_BestellungSearchForm.wDistributionDestCd}"/>, 'user');
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wSoldtoName1}">
			applyColumnWidths("soldtoName1", <c:out value="${order_bestellung_BestellungSearchForm.wSoldtoName1}"/>, 'user');
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wShiptoName1}">
			applyColumnWidths("shiptoName1", <c:out value="${order_bestellung_BestellungSearchForm.wShiptoName1}"/>, 'user');
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wShiptoCity}">
			applyColumnWidths("shiptoCity", <c:out value="${order_bestellung_BestellungSearchForm.wShiptoCity}"/>, 'user');
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wStatus}">
			applyColumnWidths("status", <c:out value="${order_bestellung_BestellungSearchForm.wStatus}"/>, 'user');
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wMaterialTxt}">
			applyColumnWidths("materialTxt", <c:out value="${order_bestellung_BestellungSearchForm.wMaterialTxt}"/>, 'user');
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wOrderedQuantity}">
			applyColumnWidths("orderedQuantity", <c:out value="${order_bestellung_BestellungSearchForm.wOrderedQuantity}"/>, 'user');
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wOrderedUomTxt}">
			applyColumnWidths("orderedUomTxt", <c:out value="${order_bestellung_BestellungSearchForm.wOrderedUomTxt}"/>, 'user');
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wLoadedQuantity}">
			applyColumnWidths("loadedQuantity", <c:out value="${order_bestellung_BestellungSearchForm.wLoadedQuantity}"/>, 'user');
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wLoadedUomTxt}">
			applyColumnWidths("loadedUomTxt", <c:out value="${order_bestellung_BestellungSearchForm.wLoadedUomTxt}"/>, 'user');
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wReturnedQuantity}">
			applyColumnWidths("returnedQuantity", <c:out value="${order_bestellung_BestellungSearchForm.wReturnedQuantity}"/>, 'user');
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wReturnedUomTxt}">
			applyColumnWidths("returnedUomTxt", <c:out value="${order_bestellung_BestellungSearchForm.wReturnedUomTxt}"/>, 'user');
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wDeliveredQuantity}">
			applyColumnWidths("deliveredQuantity", <c:out value="${order_bestellung_BestellungSearchForm.wDeliveredQuantity}"/>, 'user');
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wDeliveredUomTxt}">
			applyColumnWidths("deliveredUomTxt", <c:out value="${order_bestellung_BestellungSearchForm.wDeliveredUomTxt}"/>, 'user');
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wDeliveryTypeTxt}">
			applyColumnWidths("deliveryTypeTxt", <c:out value="${order_bestellung_BestellungSearchForm.wDeliveryTypeTxt}"/>, 'user');
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wShippingpointName}">
			applyColumnWidths("shippingpointName", <c:out value="${order_bestellung_BestellungSearchForm.wShippingpointName}"/>, 'user');
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wDeliveryFromDat}">
			applyColumnWidths("deliveryFromDat", <c:out value="${order_bestellung_BestellungSearchForm.wDeliveryFromDat}"/>, 'user');
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wDeliveryToDat}">
			applyColumnWidths("deliveryToDat", <c:out value="${order_bestellung_BestellungSearchForm.wDeliveryToDat}"/>, 'user');
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wPlannedDeliveryDat}">
			applyColumnWidths("plannedDeliveryDat", <c:out value="${order_bestellung_BestellungSearchForm.wPlannedDeliveryDat}"/>, 'user');
		</c:if>
	
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wPoolName}">
			applyColumnWidths("poolName", <c:out value="${order_bestellung_BestellungSearchForm.wPoolName}"/>, 'user');
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wCommCarrierName}">
			applyColumnWidths("commCarrierName", <c:out value="${order_bestellung_BestellungSearchForm.wCommCarrierName}"/>, 'user');
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wExecCarrierName}">
			applyColumnWidths("execCarrierName", <c:out value="${order_bestellung_BestellungSearchForm.wExecCarrierName}"/>, 'user');
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wBilltoName1}">
			applyColumnWidths("billtoName1", <c:out value="${order_bestellung_BestellungSearchForm.wBilltoName1}"/>, 'user');
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wDriverFullName}">
			applyColumnWidths("driverFullName", <c:out value="${order_bestellung_BestellungSearchForm.wDriverFullName}"/>, 'user');
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wVehicleName}">
			applyColumnWidths("vehicleName", <c:out value="${order_bestellung_BestellungSearchForm.wVehicleName}"/>, 'user');
		</c:if>
	
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wOrderValidationCode}">
			applyColumnWidths("orderValidationCode", <c:out value="${order_bestellung_BestellungSearchForm.wOrderValidationCode}"/>, 'user');
		</c:if>

		<c:if test="${!empty order_bestellung_BestellungSearchForm.wNoteOrderMgmt}">
			applyColumnWidths("noteOrderMgmt", <c:out value="${order_bestellung_BestellungSearchForm.wNoteOrderMgmt}"/>, 'user');
		</c:if>

		<c:if test="${!empty order_bestellung_BestellungSearchForm.wNoteDelivery}">
			applyColumnWidths("noteDelivery", <c:out value="${order_bestellung_BestellungSearchForm.wNoteDelivery}"/>, 'user');
		</c:if>

		<c:if test="${!empty order_bestellung_BestellungSearchForm.wNoteDispatching}">
			applyColumnWidths("noteDispatching", <c:out value="${order_bestellung_BestellungSearchForm.wNoteDispatching}"/>, 'user');
		</c:if>

		<c:if test="${!empty order_bestellung_BestellungSearchForm.wNoteTruckDriver}">
			applyColumnWidths("noteTruckDriver", <c:out value="${order_bestellung_BestellungSearchForm.wNoteTruckDriver}"/>, 'user');
		</c:if>

		<c:if test="${!empty order_bestellung_BestellungSearchForm.wNoteRailwayDisp}">
			applyColumnWidths("noteRailwayDisp", <c:out value="${order_bestellung_BestellungSearchForm.wNoteRailwayDisp}"/>, 'user');
		</c:if>
		
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wOrderNumberFromSeqUsage}">
			applyColumnWidths("orderNumberFromSeqUsage", <c:out value="${order_bestellung_BestellungSearchForm.wOrderNumberFromSeqUsage}"/>, 'user');
		</c:if>

		<c:if test="${!empty order_bestellung_BestellungSearchForm.wShiptoAddress}">
			applyColumnWidths("shiptoAddress", <c:out value="${order_bestellung_BestellungSearchForm.wShiptoAddress}"/>, 'user');
		</c:if>

		<c:if test="${!empty order_bestellung_BestellungSearchForm.wLoadingTs}">
			applyColumnWidths("loadingTs", <c:out value="${order_bestellung_BestellungSearchForm.wLoadingTs}"/>, 'user');
		</c:if>
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wLdsDeliveryNoteNo}">
			applyColumnWidths("ldsDeliveryNoteNo", <c:out value="${order_bestellung_BestellungSearchForm.wLdsDeliveryNoteNo}"/>, 'user');
		</c:if>
		<c:if test="${!empty order_bestellung_BestellungSearchForm.wItrackStatusCd}">
			applyColumnWidths("itrackStatusCd", <c:out value="${order_bestellung_BestellungSearchForm.wItrackStatusCd}"/>,
			'user');
		</c:if>
		grid.width = Ext.get('divSimpleSearch').getWidth();
		grid.render();
		
	}
	
	function resetValues(isOrderNumber) {
		deliveryFromDat.setValue(null);
		deliveryToDat.setValue(null);
		if(isOrderNumber){
			ldsDeliveryNoteNoText.setValue(null);
		}else{
			orderNumberText.setValue(null);
		}
		soldtoCombo.setValue(null);
		shiptoCombo.setValue(null);
		billtoCombo.setValue(null);
		sapMatCombo.setValue(null);
		matPackagingTypeCombo.setValue(null);
		<c:if test="${mode == 'monitoring'}">
			autoRefreshField.setValue(false);
		</c:if>
		deliveryTypeCheckCombo.selectAll();
		distributionDestCheckComboBox.selectAll();
		spCombo.setValue(null);
		poolCombo.setValue(null);
		carrierCombo.selectNone();
		execCarrierCombo.setValue(null);
		orderStatusCheckComboBox.selectAll();
		orderCreationTypeCheckComboBox.selectAll();
		sareaCombo.setValue(null);
		sofficeCombo.setValue(null);
		sgroupCombo.setValue(null);
		vehicleCombo.setValue(null);
		plantCombo.selectNone();
	}
	
	function initStores() {
		var soldtoStore = new Ext.data.Store({
			proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { OrderAjaxService.fetchSapBpList(skip, count, sort, query, true, false, false, true, callback) }, true),
			reader: new Ext.data.ListRangeReader({id:'sapBpId', totalProperty:'totalSize'}, sapBpRec)
		});
		var shiptoStore = new Ext.data.Store({
			proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { OrderAjaxService.fetchSapBpList(skip, count, sort, query, false, true, false, true, callback) }, true),
			reader: new Ext.data.ListRangeReader({id:'sapBpId', totalProperty:'totalSize'}, sapBpRec)
		});
		var billtoStore = new Ext.data.Store({
			proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { OrderAjaxService.fetchSapBpList(skip, count, sort, query, false, false, true, true, callback) }, true),
			reader: new Ext.data.ListRangeReader({id:'sapBpId', totalProperty:'totalSize'}, sapBpRec)
		});
		var sapMatStore = new Ext.data.Store({
			proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { OrderAjaxService.fetchSapMaterialList(skip, count, sort, query, callback) }, true),
			reader: new Ext.data.ListRangeReader({id:'sapMaterialId', totalProperty:'totalSize'}, sapMatRec)
		});
		var vehicleStore = new Ext.data.Store({
			proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { OrderAjaxService.fetchVehicleList(skip, count, sort, query, callback) }, true),
			reader: new Ext.data.ListRangeReader({id:'compositeId', totalProperty:'totalSize'}, vehicleRec)
		});
		
		soldtoCombo.store = soldtoStore;
		soldtoCombo.displayField = 'name1';
		soldtoCombo.valueField = 'sapBpId';
		
		shiptoCombo.store = shiptoStore;
		shiptoCombo.displayField = 'name1';
		shiptoCombo.valueField = 'sapBpId';
		
		billtoCombo.store = billtoStore;
		billtoCombo.displayField = 'name1';
		billtoCombo.valueField = 'sapBpId';
		
		sapMatCombo.store = sapMatStore;
		sapMatCombo.displayField = 'display';
		sapMatCombo.valueField = 'sapMaterialId';
		
		vehicleCombo.store = vehicleStore;
	}

<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"></script>
</c:if>

