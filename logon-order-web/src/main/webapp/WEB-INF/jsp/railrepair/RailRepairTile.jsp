<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: RailRepairTile.jsp,v $ --%>

<%@ page session="true"
	import="com.crh.logon.web.utils.Authorization"
%>

<%@ include file="../includeTLDs.jsp" %>
<c:set var="form" scope="page" value="${order_RailRepairForm}" />

<c:if test="false">
    <%-- for preview in MyEclipse --%>
    <link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>

<%@page import="java.util.Map"%>
<%@page import="com.crh.logon.order.web.forms.railrepair.RailRepairForm"%>
<%@page import="com.crh.logon.persistence.entities.views.WagonSearchEntity"%>
<%@page import="com.crh.logon.order.web.ajax.RailRepairAjaxService"%>

<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@page import="com.crh.logon.web.utils.ConfigBeanProvider"%>

<c:set var="mode" value="search" scope="request"/>
<c:set var="defaultSort" value="arrived"/>
<c:set var="defaultSortDir" value="DESC"/>

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/RailRepairAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.CheckComboBox.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>

<script type="text/javascript">

Ext.onReady(function() {
	
	// --- Common properties for all filter-comboboxes ---
	Ext.form.ComboBox.prototype.shadow = false;
	Ext.form.ComboBox.prototype.emptyText = "Select...";
	Ext.form.ComboBox.prototype.minChars = 1;
	Ext.form.ComboBox.prototype.allowBlank = true;
	Ext.form.ComboBox.prototype.forceSelection = true;
	Ext.menu.Menu.prototype.shadow = false;
	
	// --- Data record and DataStore definitions ---//
    // Record Type: com.crh.logon.order.web.beans.RailRepairBean
	var railRepairRec = Ext.data.Record.create([
			{name: "railRepCompositeId",        mapping: "entity.railRepCompositeId" },
			{name: "orderRepId",                mapping: "entity.orderRepId" },
			{name: "orderId",                   mapping: "entity.orderId" },
			{name: "orderStatus",               mapping: "entity.orderStatus" },
			{name: "orderStatusTxt",            mapping: "entity.orderStatusTxt" },
			{name: "wagonId",                   mapping: "entity.wagonId" },
			{name: "wagonNo",                   mapping: "entity.wagonNo" },
			{name: "wagonKeeperId",             mapping: "entity.wagonKeeperId" },
			{name: "wagonKeeperName",           mapping: "entity.wagonKeeperName" },
			{name: "shiptoId",                  mapping: "entity.shiptoId" },
			{name: "shiptoName",                mapping: "entity.shiptoName" },
			{name: "orderNo",                   mapping: "entity.orderNo" },
			{name: "created"},
			{name: "arrived"},
			{name: "plantEnter"},
			{name: "plantLeave"},
			{name: "wagonIdleTimeMin",          mapping: "entity.wagonIdleTimeMin" },
			{name: "damagecode",                mapping: "entity.damagecode" },
			{name: "damagecodeRiskclass",       mapping: "entity.damagecodeRiskclass" },
			{name: "maintG10MainTypeTxt",       mapping: "entity.maintG10MainTypeTxt" },
			{name: "maintG20MainTypeTxt",       mapping: "entity.maintG20MainTypeTxt" },
			{name: "maintG30MainTypeTxt",       mapping: "entity.maintG30MainTypeTxt" },
			{name: "maintG40MainTypeTxt",       mapping: "entity.maintG40MainTypeTxt" },
			{name: "maintG42MainTypeTxt",       mapping: "entity.maintG42MainTypeTxt" },
			{name: "maintG43MainTypeTxt",       mapping: "entity.maintG43MainTypeTxt" },
			{name: "maintG48MainTypeTxt",       mapping: "entity.maintG48MainTypeTxt" },
			{name: "maintP3mMainTypeTxt",       mapping: "entity.maintP3mMainTypeTxt" },
			{name: "maintP5MainTypeTxt",        mapping: "entity.maintP5MainTypeTxt" },
			{name: "maintCMainTypeTxt",         mapping: "entity.maintCMainTypeTxt" },
			{name: "remarks",                   mapping: "entity.remarks" },
			{name: "offerDesiredFlag",          mapping: "entity.offerDesiredFlag" },
			{name: "offerAcceptDat",            mapping: "entity.offerAcceptDat" },
			{name: "invoicePaidFlag",           mapping: "entity.invoicePaidFlag" },
			{name: "documentationAcceptFlag",   mapping: "entity.documentationAcceptFlag" },
			{name: "hasOfferAttachmentFlag",    mapping: "entity.hasOfferAttachmentFlag" },
			{name: "hasDokuAttachmentFlag",     mapping: "entity.hasDokuAttachmentFlag" },
			{name: "hasInvoiceAttachmentFlag",  mapping: "entity.hasInvoiceAttachmentFlag" },
			{name: "combinedOrderStatusFlag",   mapping: "entity.combinedOrderStatusFlag" }
	]);
	
    var railRepairStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { RailRepairAjaxService.getInstandhaltungs(skip, count, sort, query, callback) }, true),
		reader: new Ext.data.ListRangeReader({id:'railRepCompositeId', totalProperty:'totalSize'}, railRepairRec), 
		remoteSort: true
    });
    railRepairStore.setDefaultSort('<c:out value="${defaultSort}"/>', '<c:out value="${defaultSortDir}"/>');

	var wagonKeeperStore = new Ext.data.SimpleStore({
	    fields: ['wagonKeeperId', 'wagonKeeperName', 'checked'],
	    data : [ 
	<c:forEach var="wagonKeeper" varStatus="status" items="${order_RailRepairForm.wagonKeeperList}">
		[
			'<c:out value="${wagonKeeper.wagonKeeperId}"/>',
			'<c:out value="${wagonKeeper.wagonKeeperName}"/>',
			true
		]
		<c:if test="${!status.last}">,</c:if>
	</c:forEach>
	    ]
	});

	var wagonKeeperCheckComboBox = new Ext.ux.CheckComboBox({
	    store: wagonKeeperStore,
	    displayField: 'wagonKeeperName',
        applyTo: 'wagonKeeperComboDummy',
	    checkedField: 'checked',
	    valueField: 'wagonKeeperId',
	    typeAhead: true,
	    mode: 'local',
	    triggerAction: 'all',
	    emptyText:'multiple',
	    selectOnFocus: true,
	    width: 180,
	    listWidth: 200
	});
	
	var wagonComboSearchRec = Ext.data.Record.create(
			[{name: "value"}, {name: "label"}]
	);	
	
    var wagonNoStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { RailRepairAjaxService.getWagonNoListForPart(skip, count, sort, query, callback)}, true),
		reader: new Ext.data.ListRangeReader({id:'value', totalProperty:'totalSize'}, wagonComboSearchRec),
		remoteSort: true
    });
	
	var tpl = new Ext.XTemplate(
		    '<tpl for="."><div class="x-combo-list-item">{[this.render(values.label)]}</div></tpl>',		    
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
    	id: 'wagonNoComboDummy',
        applyTo: 'wagonNoComboDummy',
        store: wagonNoStore,
        displayField: 'label',
        valueField: 'label',
        pageSize: 20,
        minChars: 2,
        forceSelection: false,
        width: 150,
        editable: true,
        listWidth: 250,
        typeAhead: false,
        triggerAction: 'all',
        selectOnFocus: true,
        tpl: tpl
    });

	var repairShiptoStore = new Ext.data.SimpleStore({
	    fields: ['repairShiptoId', 'repairShiptoSapBpName', 'checked'],
	    data : [ 
	<c:forEach var="repairShipto" varStatus="status" items="${order_RailRepairForm.repairShiptoList}">
		[
			'<c:out value="${repairShipto.shipto.shiptoSapBp.sapBpId}"/>',
			'<c:out value="${repairShipto.shipto.shiptoSapBp.name1}"/>',
			true
		]
		<c:if test="${!status.last}">,</c:if>
	</c:forEach>
	    ]
	});

	var repairShiptoCheckComboBox = new Ext.ux.CheckComboBox({
	    store: repairShiptoStore,
	    displayField: 'repairShiptoSapBpName',
        applyTo: 'repairShiptoComboDummy',
	    checkedField: 'checked',
	    valueField: 'repairShiptoId',
	    typeAhead: true,
	    mode: 'local',
	    triggerAction: 'all',
	    emptyText:'multiple',
	    selectOnFocus: true,
	    width: 180,
	    listWidth: 200
	});

	var combinedOrderStatusStore = new Ext.data.SimpleStore({
		id: 0,
	    fields: ['combinedOrderStatusFlag', 'combinedOrderStatusName', 'checked'],
	    data : [ 
	<c:forEach var="combinedOrderStatus" varStatus="status" items="${order_RailRepairForm.combinedOrderStatusList}">
		[
			'<c:out value="${status.index}"/>',
			'<fmt:message key="railrepair.combinedOrderStatusName.${combinedOrderStatus}"/>',
			'true'
		]
		<c:if test="${!status.last}">,</c:if>
	</c:forEach>
	    ]
	});

	var combinedOrderStatusCheckComboBox = new Ext.ux.CheckComboBox({
	    store: combinedOrderStatusStore,
	    displayField: 'combinedOrderStatusName',
        applyTo: 'combinedOrderStatusComboDummy',
	    checkedField: 'checked',
	    valueField: 'combinedOrderStatusFlag',
	    typeAhead: true,
	    mode: 'local',
	    triggerAction: 'all',
	    emptyText:'multiple',
	    selectOnFocus: true,
	    width: 180,
	    listWidth: 200
	});
	
    var railRepairPageSize = <%= ConfigBeanProvider.getConfigBean().getAppDefaultRailrepairNumOfDisplayedRepairOrders() %>;

    // --- Search Area Handler Obj ---
	var searchArea = {
		pageSize: railRepairPageSize,
		searchFilters: {},
		getSearchCrits: function() {
			return {
				shiptoSapBpId: repairShiptoCheckComboBox.getCheckedValues().join(),
				wagonKeeperId: wagonKeeperCheckComboBox.getCheckedValues().join(),
				wagonNo: wagonNoCombo.getValue(),
				combinedOrderStatusFlag: combinedOrderStatusCheckComboBox.getCheckedValues().join()
			}
		},
		getInstandhaltungs: function(startRecord) {
			var filters = this.getSearchCrits();
			this.searchFilters = this.getSearchCrits();
			if (startRecord == undefined) {
				startRecord = 0;
			}
			railRepairStore.load({params:{start: startRecord, limit: this.pageSize, sort: '', query: filters }});
		},
		getInstandhaltungsKeepPaging: function() {
			var startRecord = 0;
			if (!(pagingBar === undefined) && !(pagingBar.cursor === undefined) && !(pagingBar.pageSize === undefined)) {
				var activePage = Math.ceil((pagingBar.cursor + pagingBar.pageSize) / pagingBar.pageSize);
				startRecord = activePage * pagingBar.pageSize - pagingBar.pageSize;
				if (isNaN(startRecord)){
					startRecord = 0;
				}
			}
			this.getInstandhaltungs(startRecord);
		}
	}

	logonPopupHideCallback = searchArea.getInstandhaltungsKeepPaging.createDelegate(searchArea, []);
    
	// --- Result list ---
	var textConstants={
			"wagonNo":"<fmt:message key="railrepair.column.wagonNo"/>",
			"wagonKeeperName":"<fmt:message key="railrepair.column.wagonKeeperName"/>",
			"shiptoName":"<fmt:message key="railrepair.column.shiptoName"/>",
			"orderNo":"<fmt:message key="railrepair.column.orderNo"/>",
			"orderStatusTxt":"<fmt:message key="railrepair.column.orderStatus"/>",
			"created":"<fmt:message key="railrepair.column.created"/>",
			"arrived":"<fmt:message key="railrepair.column.arrived"/>",
			"plantEnter":"<fmt:message key="railrepair.column.plant.enter"/>",
			"plantLeave":"<fmt:message key="railrepair.column.plant.leave"/>",
			"wagonIdleTime":"<fmt:message key="railrepair.column.wagonIdleTime"/>",
			"damagecode":"<fmt:message key="railrepair.column.damagecode"/>",
			"maintType":"<fmt:message key="railrepair.column.mainttype"/>",
			"remarks":"<fmt:message key="railrepair.column.remarks"/>",
			"offer":"<fmt:message key="railrepair.column.offer"/>",
			"document":"<fmt:message key="railrepair.column.document"/>",
			"invoice":"<fmt:message key="railrepair.column.invoice"/>",
			"displayMsg":"<fmt:message key="railrepair.pager.displayMsg"/>",
			"emptyMsg":"<fmt:message key="railrepair.pager.emptyMsg"/>",

			"nein":"<fmt:message key="railrepair.value.nein"/>",
			"offen":"<fmt:message key="railrepair.value.offen"/>",
			"erhalten":"<fmt:message key="railrepair.value.erhalten"/>",
			"akzeptiert":"<fmt:message key="railrepair.value.akzeptiert"/>"
		};
    // ------ PAGER ---------
    var pagingBar = new Ext.PagingToolbar({
        pageSize: searchArea.pageSize,
        store: railRepairStore,               
        displayInfo: true,
        displayMsg: textConstants.displayMsg,
        emptyMsg: textConstants.emptyMsg
    });

	var selectionModel = new Ext.grid.CellSelectionModel({
	    singleSelect: true,
	    listeners: {
	        selectionchange: {
	            fn: function(col, record) {
    				if (this.getSelectedCell()) {
        				var index = this.getSelectedCell()[1];
						var id = this.grid.getColumnModel().getColumnId(index);
						if (id ==='orderNo' ) {
							var orderId = record.record.data.orderId;
							if (orderId) {
								showPopup('<c:url value="/auftrag/OrderDetailEntry.do?orderid=' + orderId + '"/>');
							}
							return;										
						}									        			
    				}
	            }
	    	}
		}
	}); 
		
    var grid = new Ext.grid.GridPanel({
    	id: 'searchGrid',
        el:'divSearchResults',
        sm: selectionModel,
		autoHeight: true,
		minHeight: 500,
		viewConfig: {
			autoFill: true, 
			forceFit: true,
			getRowClass: function(record, index) {
	            if (record.get('orderStatusCd') == 16) {
	                return 'x-grid3-row-italic';
	            }
			}
		},
		stripeRows: true,
        title:textConstants.searchresults,
        store: railRepairStore,
        pageSize: searchArea.pageSize,
        loadMask: false,
		selModel: new Ext.grid.CellSelectionModel({singleSelect:true}),
        // grid columns
        columns:[
        {
            id: 'wagonNo', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header: textConstants.wagonNo,
            dataIndex: 'wagonNo',
            renderer: function(v, p, record){
            	var re = new RegExp("(\\d{4})(\\d{4})(\\d{3})(\\d{1})");
            	var m = re.exec(v);
        	   	if (m != null) {
        	   		var rendered = '';
        	    <crh-common:authorize function="<%= Authorization.RAILCAR_DETAIL_BASE_R %>">
        	    <%--Only show the wagon detail link in case the user is authorized --%>
        	    	rendered += '<a href="<c:url value="../../admin/WagonDetailEntry.do?wagonid=' + record.data.wagonId + '"/>">';			
				</crh-common:authorize>
					rendered += m[1] + " " + m[2] + " " + m[3] + "-" + m[4];
	        	<crh-common:authorize function="<%= Authorization.RAILCAR_DETAIL_BASE_R %>">
	        	    rendered += '</a>';			
				</crh-common:authorize>
					return rendered;
            	}
           }, 
            width: 100,
            sortable: true
        },
	    {
	        	id: "wagonKeeperName",
	            header: textConstants.wagonKeeperName,
	            dataIndex: "wagonKeeperName",
	            sortable: true
	    },
	    {
	            id: "shiptoName",
	            header: textConstants.shiptoName,
	            dataIndex: "shiptoName",
	            width: 125,
	            sortable: true
	    },
	    {
	            id: "orderNo",
	            header: textConstants.orderNo,
	            dataIndex: "orderNo",
	            width: 65,
	            renderer: function(v, p, record) {
	            	return '<a href="javascript:showPopup(\'<c:url value="/auftrag/OrderDetailEntry.do?orderid=' + record.data.orderId + '"/>\')">' + v + '</a>';
	       		},
	            sortable: true
	    },
		{
		        id: "combinedOrderStatusFlag",
		        header: textConstants.orderStatusTxt,
		        dataIndex: "combinedOrderStatusFlag",
	            renderer: function(v, p, record) {
	            	var rendered = combinedOrderStatusStore.getById(record.data.combinedOrderStatusFlag).data.combinedOrderStatusName;
	            	rendered += " (" + record.data.orderStatusTxt + ")";
	            	return rendered;
	       		},
		        width: 55,
		        sortable: true
		},
	    {
	            id: "created",
	            header: textConstants.created,
	            dataIndex: "created",
	            width: 60,
	            sortable: true
	    },
	    {
	            id: "arrived",
	            header: textConstants.arrived,
	            dataIndex: "arrived",
	            width: 60,
	            sortable: true
	    },
	    {
		        id: "plantEnter",
		        header: textConstants.plantEnter,
		        dataIndex: "plantEnter",
		        width: 60,
		        sortable: true
		},
		{
			    id: "plantLeave",
			    header: textConstants.plantLeave,
			    dataIndex: "plantLeave",
			    width: 60,
			    sortable: true
		},
        {
            id: 'wagonIdleTime', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header:textConstants.wagonIdleTime,
            dataIndex: 'wagonIdleTimeMin',
            //width: 100,
            renderer: function(v, p, record) {
        		if (v) {
	        		var days = Math.floor(v/(24*60));
	        		var hours = Math.floor((v-(days*60*24))/60);
	        		var minutes = v-(days*60*24)-(hours*60);
	        		
	        	  	return days + "d " + hours + "h " + minutes + "m";
        		}
       		},
       		sortable: true
        },
	    {
	            id: "damagecode",
	            header: textConstants.damagecode,
	            dataIndex: "damagecode",
	            width: 75,
	            sortable: true
	    },
	    {
	            id: "maintType",
	            header: textConstants.maintType,
	            dataIndex: "maintType",
	            width: 115,
	            sortable: true,
	            renderer: function(v, p, record) {
	            	var rendered = [];
	            	if (record.data.maintG10MainTypeTxt != null) {
	            		rendered.push(record.data.maintG10MainTypeTxt);
	            	}
	            	if (record.data.maintG20MainTypeTxt != null) {
	            		rendered.push(record.data.maintG20MainTypeTxt);
	            	}
	            	if (record.data.maintG30MainTypeTxt != null) {
	            		rendered.push(record.data.maintG30MainTypeTxt);
	            	}
	            	if (record.data.maintG40MainTypeTxt != null) {
	            		rendered.push(record.data.maintG40MainTypeTxt);
	            	}
	            	if (record.data.maintG42MainTypeTxt != null) {
	            		rendered.push(record.data.maintG42MainTypeTxt);
	            	}
	            	if (record.data.maintG43MainTypeTxt != null) {
	            		rendered.push(record.data.maintG43MainTypeTxt);
	            	}
	            	if (record.data.maintG48MainTypeTxt != null) {
	            		rendered.push(record.data.maintG48MainTypeTxt);
	            	}
	            	if (record.data.maintP3mMainTypeTxt != null) {
	            		rendered.push(record.data.maintP3mMainTypeTxt);
	            	}
	            	if (record.data.maintP5MainTypeTxt != null) {
	            		rendered.push(record.data.maintP5MainTypeTxt);
	            	}
	            	if (record.data.maintCMainTypeTxt != null) {
	            		rendered.push(record.data.maintCMainTypeTxt);
	            	}
	            	return  rendered.join('<br>');
	           }
	    },
	    {
	            id: "remarks",
	            header: textConstants.remarks,
	            dataIndex: "remarks",
	            sortable: true
	    },
	    {
	            id: "offer",
	            header: textConstants.offer,
	            dataIndex: "offer",
	            width: 65,
	            sortable: true,
	            renderer: function(v, p, record) {
	            	var rendered;
	            	if (record.data.offerDesiredFlag) {
	            		rendered = textConstants.offen;
	            	}
	            	else {
	            		rendered = textConstants.nein;
	            	}
					if (record.data.hasOfferAttachmentFlag) {
						rendered = textConstants.erhalten;
					}
					if (record.data.offerAcceptDat != null) {
						rendered = textConstants.akzeptiert;
					}
	       			return rendered;
	           }
	    },
	    {
	            id: "document",
	            header: textConstants.document,
	            dataIndex: "document",
	            width: 65,
	            sortable: true,
	            renderer: function(v, p, record) {
	            	var rendered = textConstants.offen;
	            	
					if (record.data.hasDocuAttachmentFlag) {
						rendered = textConstants.erhalten;
					}
					if (record.data.documentationAcceptFlag) {
						rendered = textConstants.akzeptiert;
					}
	       			return rendered;
	           }
	    },
	    {
	            id: "invoice",
	            header: textConstants.invoice,
	            dataIndex: "invoice",
	            width: 65,
	            sortable: true,
	            renderer: function(v, p, record) {
	            	var rendered = textConstants.offen;
	            	
					if (record.data.hasInvoiceAttachmentFlag) {
						rendered = textConstants.erhalten;
					}
					if (record.data.invoicePaidFlag) {
						rendered = textConstants.akzeptiert;
					}
	       			return rendered;
	           }
	    }
        ],

        // paging bar on the bottom
        bbar: pagingBar
        
    });
    
    grid.render(); // render the grid

    // --- Initial data load ---
   	searchArea.getInstandhaltungs();

 	// --- Buttons ---  ;  	
	Ext.get('buttonSearch').on('click', function() {
		searchArea.getInstandhaltungs();
	});
	
	Ext.get('buttonExport').on('click', function() {
		var url = 'RailRepair.do?export=true';
		
		var crits = searchArea.getSearchCrits();

		if (crits.shiptoSapBpId) {
			url += '&shiptoSapBpId=' + crits.shiptoSapBpId;
		}
		if (crits.wagonKeeperId) {
			url += '&wagonKeeperId=' + crits.wagonKeeperId;
		}
		if (crits.wagonNo) {
			url += '&wagonNo=' + crits.wagonNo;
		}
		if (crits.combinedOrderStatusFlag) {
			url += '&combinedOrderStatusFlag=' + crits.combinedOrderStatusFlag;
		}
		
		window.open(url);
	})
});

</script> 

<table id="searchTable" cellpadding="0" cellspacing="0" class="big_form2" style="margin: 0px; border-collapse: collapse;">
<tr>
	<td class="border" style="background-color: F9F9F9; padding: 2px 0 0 10px;">
		<div id="divSimpleSearch">
			<table style="width: 100%;">
				<colgroup>
					<col width="10%">
					<col width="10%">
					<col width="10%">
					<col width="10%">
					<col width="60%">
				</colgroup>
				<tr>					
					<td class="tinylabel">
						<fmt:message key="railrepair.filter.plants"/>
					</td>
					<td class="tinylabel">
						<fmt:message key="railrepair.filter.wagonKeeperName"/>
					</td>
					<td class="tinylabel">
						<fmt:message key="wagonsearch.text.wagonno" />
					</td>
					<td class="tinylabel">
						<fmt:message key="railrepair.filter.orderStatus"/>
					</td>
					<td rowspan="2" class="tinylabel">
						<table cellspacing="0" cellpadding="0" style="margin: 0px; padding:0px; border: 0px;">
							<tr>
								<td>
									<input id="buttonSearch" type="button" class="SmallButtonStyle" value="<fmt:message key="buttons.search"/>"/><br/>
								</td>
								<td>
									<input id="buttonExport" type="button" class="SmallButtonStyle" value="<fmt:message key="buttons.export"/>"/><br/>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
					  	<input type="text" id="repairShiptoComboDummy"/>
					</td>
					<td>
					  	<input type="text" id="wagonKeeperComboDummy"/>
					</td>
					<td>
						<input type="text" id="wagonNoComboDummy"/>
					</td>
					<td>
					  	<input type="text" id="combinedOrderStatusComboDummy"/>
					</td>
				</tr>
			</table>
		</div>
	</td>
</tr>
</table>

<div id="divSearchResults" style="margin-top: 10px">

</div>
<%-- END $RCSfile: RailRepairTile.jsp,v $ --%>
