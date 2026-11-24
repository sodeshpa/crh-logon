<%@ include file="../../includeTLDs.jsp" %>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"><script type="text/javascript"></c:if>

<%-- Text constants coming from litdict --%>
var textConstants={
		"keeper" :"<fmt:message key="wagonsearch.text.keeper"/>",
		"locationStation" :"<fmt:message key="wagonsearch.text.locationstation"/>",
		"returnStation" :"<fmt:message key="wagonsearch.text.returnstation"/>",
		"activeFlag" :"<fmt:message key="common.flags.active"/>",
		"wagonno" :"<fmt:message key="wagonsearch.text.wagonno"/>",
		"type" :"<fmt:message key="wagonsearch.text.type"/>",
		"displaymsg":"<fmt:message key="wagonsearch.text.displayingwagons"/>",
		"nowagons":"<fmt:message key="wagonsearch.text.nowagons"/>",
		"arrived":"<fmt:message key="wagonsearch.text.arrived"/>",
		"currenttrack":"<fmt:message key="wagonsearch.text.currenttrack"/>",
		"currentpos":"<fmt:message key="wagonsearch.text.currentpos"/>",
		"searchresults":"<fmt:message key="wagonsearch.text.searchresults"/>",
		"destRailwayStation":"<fmt:message key="wagonsearch.text.dest_railway_station"/>",
		"wagonIdleTime":"<fmt:message key="wagonsearch.text.wagon_idle_time"/>",
		"returnShipto":"<fmt:message key="wagonsearch.text.return_shipto"/>",
		"orderNumber":"<fmt:message key="wagonsearch.text.order_number"/>",
		"orderItemNo":"<fmt:message key="wagonsearch.text.order_item_no"/>",
		"orderStatus":"<fmt:message key="wagonsearch.text.order_status"/>",
		"distributionDest":"<fmt:message key="wagonsearch.text.distribution_dest"/>",
		"soldto":"<fmt:message key="wagonsearch.text.soldto"/>",
		"shipto":"<fmt:message key="wagonsearch.text.shipto"/>",
		"sapMaterial":"<fmt:message key="wagonsearch.text.sap_material"/>",
		"loadedQuantity":"<fmt:message key="wagonsearch.text.loaded_quantity"/>",
		"loadedUom":"<fmt:message key="wagonsearch.text.loaded_uom"/>",
		"wagonNoteRailwaySpedition":"<fmt:message key="wagonsearch.text.wagon_note_railway_spedition"/>"
		};
    // ------ PAGER ---------
    var pagingBar = new Ext.PagingToolbar({
        pageSize: searchArea.pageSize,
        store: wagonSearchStore,
        displayInfo: true,
        displayMsg: textConstants.displaymsg,
        emptyMsg: textConstants.nowagons
    });

	var selectionModel = new Ext.grid.CellSelectionModel({
	    singleSelect: true,
	    listeners: {
	        selectionchange: {
	            fn: function(col, record) {
    				if (this.getSelectedCell()) {
        				
    	<crh-common:authorize function="<%= Authorization.RAILCARS_FREIMELDEN %>">
    	<%--Only show the order detail link in case the user is authorized to call Set delivered on order detail--%>
        				var index = this.getSelectedCell()[1];
						var id = this.grid.getColumnModel().getColumnId(index);
						var orderId = record.record.data.orderId;
						if (id ==='orderNumber' ){
							if (orderId) {
								showPopup('<c:url value="../../logon/order/auftrag/OrderDetailEntry.do"/>?orderid=' + orderId);	
							}
							else {
								return;
							}
						}
						else {        
		</crh-common:authorize>
		
		<%--Otherwise the order number cell will also just end up on wagon detail - in case on has the right to see wagon detail--%>
    						<crh-common:authorize function="<%= Authorization.RAILCAR_DETAIL_BASE_R %>">
		        				searchArea.storeSearchSettings(record.record.data.wagonId);
		        			</crh-common:authorize>
		        			return;
		<crh-common:authorize function="<%= Authorization.RAILCARS_FREIMELDEN %>">
						}
		</crh-common:authorize>
								        			
    				}
	            }
	    	}
		}
	}); 
		
    var grid = new Ext.grid.GridPanel({
    	id: 'searchGrid',
        el:'divSearchResults',
        sm: selectionModel,
        width: Ext.get('divSimpleSearch').getWidth(),
        height:602,
        //enableColumnHide: false,
        //autoExpandColumn: 'billtoName1',
		viewConfig: {autoFill: true, forceFit: true},        
		stripeRows: true,
        title:textConstants.searchresults,
        store: wagonSearchStore,
        pageSize: searchArea.pageSize,
        loadMask: true,
		selModel: new Ext.grid.CellSelectionModel({singleSelect:true}),
        // grid columns
        columns:[
        {
            id: 'wagonNo', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header: textConstants.wagonno,
            dataIndex: 'wagonNo',
            renderer: function(v, p, record){
            	var re = new RegExp("(\\d{4})(\\d{4})(\\d{3})(\\d{1})");
            	var rendered = record.data.wagonNo;
            	  var m = re.exec(record.data.wagonNo);
            	  if (m != null) {
						rendered = "";
						rendered = m[1]+" "+m[2]+" "+m[3]+"-"+m[4];
            	    }
       			return rendered;
           },                
            
            //width: 100,
            sortable: true
        },
        {
            id: 'wagonTypeName', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header: textConstants.type,
            dataIndex: 'wagonTypeName',
            //width: 100,
            sortable: true,
            hidden: true
        },
        {
            id: 'lastKnownStationName', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header:textConstants.locationStation,
            dataIndex: 'lastKnownStationName',
            //width: 100,
            sortable: true
        },
        {
            id: 'wagonIdleTimeMin', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header:textConstants.wagonIdleTime,
            dataIndex: 'wagonIdleTimeMin',
            //width: 100,
            renderer: function(v, p, record) {
        		var min = record.data.wagonIdleTimeMin;

        		if (min) {
	        		var days = Math.floor(min/(24*60));
	        		var hours = Math.floor((min-(days*60*24))/60);
	        		var minutes = min-(days*60*24)-(hours*60);
	        		
	        	  	return days + "d " + hours + "h " + minutes + "m";
        		}

        		return;
       		},
       		sortable: true
        },
        {
            id: 'arrivalLastKnownStation', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header: textConstants.arrived,
            dataIndex: 'arrivalLastKnownStation',
            //width: 100,
            sortable: true
        },
        {
            id: 'plantTrackTxt', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header: textConstants.currenttrack,
            dataIndex: 'plantTrackTxt',
            //width: 100,
            sortable: true,
            hidden: true
        },
        {
            id: 'currentTrackPositionNo', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header: textConstants.currentpos,
            dataIndex: 'currentTrackPositionNo',
            //width: 100,
            sortable: true,
            hidden: true
        },
        {
            id: 'destRailwayStationName',
            header:textConstants.destRailwayStation,
            dataIndex: 'destRailwayStationName',
            //width: 100,
            sortable: true
        },        
        {
            id: 'returnShiptoName1',
            header:textConstants.returnShipto,
            dataIndex: 'returnShiptoName1',
            //width: 100,
            sortable: true,
            hidden: true
        },
        {
            id: 'orderNumber',
            header:textConstants.orderNumber,
            dataIndex: 'orderNumber',
            //width: 100,
            sortable: true
        },
        {
            id: 'orderItemNo',
            header:textConstants.orderItemNo,
            dataIndex: 'orderItemNo',
            //width: 100,
            sortable: true
        },
        {
            id: 'orderStatusTxt',
            header:textConstants.orderStatus,
            dataIndex: 'orderStatusTxt',
            //width: 100,
            sortable: true
        },
        {
            id: 'distributionDestTxt',
            header:textConstants.distributionDest,
            dataIndex: 'distributionDestTxt',
            //width: 100,
            sortable: true,
            hidden: true
        },
        {
            id: 'soldtoName1',
            header:textConstants.soldto,
            dataIndex: 'soldtoName1',
            //width: 100,
            sortable: true
        },
        {
            id: 'shiptoName1',
            header:textConstants.shipto,
            dataIndex: 'shiptoName1',
            //width: 100,
            sortable: true
        },
        {
            id: 'sapMaterialTxt',
            header:textConstants.sapMaterial,
            dataIndex: 'sapMaterialTxt',
            //width: 100,
            sortable: true
        },
        {
            id: 'loadedQuantity',
            header:textConstants.loadedQuantity,
            dataIndex: 'loadedQuantity',
            //width: 100,
            sortable: true
        },
        {
            id: 'loadedUom',
            header:textConstants.loadedUom,
            dataIndex: 'loadedUom',
            //width: 100,
            sortable: true,
            hidden: true
        },
        {
            id: 'wagonKeeperName',
            header:textConstants.keeper,
            dataIndex: 'wagonKeeperName',
            //width: 100,
            sortable: true
        },
        {
            id: 'wagonNoteRailwaySpedition',
            header:textConstants.wagonNoteRailwaySpedition,
            dataIndex: 'wagonNoteRailwaySpedition',
            //width: 100,
            sortable: true
        },
        {
            id: 'activeFlag', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header: textConstants.activeFlag,
            sortable: true,
            dataIndex: 'activeFlag',
            renderer: function(v, p, record){
             	if(record.data.activeFlag == true){
             		return '<img width="16" height="16" alt="activated" src="' + crhCommonUrl('./images/active.png') + '"/>' ;
             	}
             	return '<img width="16" height="16" alt="activated" src="' + crhCommonUrl('./images/deactivated.png') + '"/>' ;
             },
             hidden: true
        }
        ],

        // paging bar on the bottom
        bbar: pagingBar
        
    });
    
    grid.render(); // render the grid

<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"></script></c:if>
    