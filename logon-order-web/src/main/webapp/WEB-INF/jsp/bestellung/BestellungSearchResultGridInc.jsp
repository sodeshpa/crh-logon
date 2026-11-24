<%@ page import="com.crh.logon.constants.ITrackStatus" %>
<%@ include file="../includeTLDs.jsp" %>

<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"><script type="text/javascript"></c:if>

<c:if test="${mode == 'monitoring'}">
	var orderStatusTxList = <c:out value="${order_bestellung_BestellungSearchForm.json['orderStatusTxList']}" escapeXml="false"/>;
	var orderStatusWarnText = new Array();
	for (var i=0;i<orderStatusTxList.length;i++) {
		orderStatusWarnText['' + orderStatusTxList[i].orderStatusTxPK.orderStatusBs.orderStatusCd] = orderStatusTxList[i].warnTxt;
	}
	var onHoldWarnText = '<fmt:message key="ordersearch.table.warn.onhold"/>';
</c:if>

	var defaultVisibleColumns = [
		"timeToRedLine",
		"orderNumber",
		"soldtoName1",
		"shiptoCity",
		"status",
		"materialTxt",
		"orderedQuantity",
		"orderedUomTxt",
		"deliveredQuantity",
		"deliveredUomTxt",
		"shippingpointName",
		"deliveryFromDat",
		"deliveryToDat",
		"plannedDeliveryDat",
		"commCarrierName",
		"vehicleName"
	];
	
	var defaultColumnWidths = {
		timeToRedLine: 65,
		orderNumber: 90,
		distributionDestCd: 90,
		soldtoName1: 120,
		shiptoName1: 150,
		
		shiptoCity: 80,
		status: 150,
		materialTxt: 110,
		orderedQuantity: 30,
		orderedUomTxt: 50,
		
		loadedQuantity: 30,
		loadedUomTxt: 50,
		returnedQuantity: 30,
		returnedUomTxt: 50,
		deliveredQuantity: 30,
		
		deliveredUomTxt: 50,
		deliveryTypeTxt: 130,
		shippingpointName: 150,
		deliveryFromDat: 110,
		deliveryToDat: 110,
		
		plannedDeliveryDat: 110,
		poolName: 80,
		commCarrierName: 110,
		execCarrierName: 110,
		billtoName1: 160,
		
		driverFullName: 170,
		vehicleName: 110,
		orderValidationCode: 30,
	    noteOrderMgmt: 50,
	    noteDelivery: 50,
	    noteDispatching: 50,
	    noteTruckDriver: 50,
	    noteRailwayDisp: 50,
	    orderNumberFromSeqUsage: 50,
	    shiptoAddress: 50,
	    loadingTs: 50,
	    ldsDeliveryNoteNo: 50,
		itrackStatusCd: 50
	}
	
	var defaultColumnIndexes = {
			timeToRedLine: 1,
			orderNumber: 2,
			distributionDestCd: 3,
			soldtoName1: 4,
			shiptoName1: 5,
			
			shiptoCity: 6,
			status: 7,
			materialTxt: 8,
			orderedQuantity: 9,
			orderedUomTxt: 10,
			
			loadedQuantity: 11,
			loadedUomTxt: 12,
			returnedQuantity: 13,
			returnedUomTxt: 14,
			deliveredQuantity: 15,
			
			deliveredUomTxt: 16,
			deliveryTypeTxt: 17,
			shippingpointName: 18,
			deliveryFromDat: 19,
			deliveryToDat: 20,
			
			plannedDeliveryDat: 21,
			poolName: 22,
			commCarrierName: 23,
			execCarrierName: 24,
			billtoName1: 25,
			
			driverFullName: 26,
			vehicleName: 27,
			orderValidationCode: 28,
			noteOrderMgmt: 29,
		    noteDelivery: 30,
		    noteDispatching: 31,
		    noteTruckDriver: 32,
		    noteRailwayDisp: 33,
		    orderNumberFromSeqUsage: 34,
		    shiptoAddress: 35,
		    loadingTs: 36,
		    ldsDeliveryNoteNo: 37,
			itrackStatusCd: 38
		}
	
	function getColumnIndex(colName,customIndex) {
		if (!customIndex) {
	    	return defaultColumnIndexes[colName];
	    }
	    return customIndex;
	}
	
	var defaultColumns = [
					<c:if test="${mode == 'monitoring'}">
					{
						id: 'timeToRedLine',
						header: "Monitoring", // TODO LitDict
						dataIndex: 'timeToRedLine',
						width: defaultColumnWidths.timeToRedLine,
						sortable: true,
						customIndex: getColumnIndex('timeToRedLine','<c:out value="${order_bestellung_BestellungSearchForm.idxTimeToRedLine}"/>'),
						renderer: function(val, meta, rec) {
							var result = '<div style="float: left; width: 20px;">';
							if (rec.data.warnStatusCode == 3) {
								result += '<img src="' + crhCommonUrl('../images/silk/clock.png') + '" title="' + onHoldWarnText + '"/>';
							} 
							else if (rec.data.warnStatusCode == 2) {
								result += '<img src="' + crhCommonUrl('../images/silk/flag_red.png') + '" title="' + orderStatusWarnText['' + rec.data.orderStatusCd] + '"/>';
							} 
							else if (rec.data.warnStatusCode == 1) {
								result += '<img src="' + crhCommonUrl('../images/silk/flag_yellow.png') + '" title="' + orderStatusWarnText['' + rec.data.orderStatusCd] + '"/>';
							}
							else {
								result += '<img hieght=16 width=16 src="' + crhCommonUrl('../images/0.gif') + '"/>';
							}
					
							result += '</div>' + (rec.data.timeToRedLine > 0 ? '+' : '') + rec.data.timeToRedLine;
							return result;	
						}
					},
					</c:if>
					{
						id: 'orderNumber',
						header: '<fmt:message key="ordersearch.table.header.orderNumber"/>',
						dataIndex: 'orderNumber',
						align: 'right',
						width: defaultColumnWidths.orderNumber,
						sortable: true,
						customIndex:  getColumnIndex('orderNumber','<c:out value="${order_bestellung_BestellungSearchForm.idxOrderNumber}"/>'),
						renderer: function(val, meta, rec) {
								return '<a href="javascript:showPopup(\'<c:url value="/auftrag/OrderDetailEntry.do"/>?orderid=' + rec.data.orderId + '\')">' + val + '</a> <span style="font-size: 10px; color: #AAAAAA">('+rec.data.orderItemNo+ ')</span>';			
						}
					},{
					
						id: 'distributionDestCd',
						header: '<fmt:message key="ordersearch.table.header.distributionDestCd"/>',
						dataIndex: 'distributionDestCd',
						align: 'right',
						width: defaultColumnWidths.distributionDestCd,
						sortable: true,
						customIndex: getColumnIndex('distributionDestCd','<c:out value="${order_bestellung_BestellungSearchForm.idxDistributionDestCd}"/>'),
						hidden: true
					},{
						id: 'soldtoName1',
						header: '<fmt:message key="ordersearch.table.header.soldTo"/>',
						dataIndex: 'soldtoName1',
						width: defaultColumnWidths.soldtoName1,
						customIndex: getColumnIndex('soldtoName1','<c:out value="${order_bestellung_BestellungSearchForm.idxSoldtoName1}"/>'),
						sortable: true
					},{
						id: 'shiptoName1',
						header: '<fmt:message key="ordersearch.table.header.shipTo"/>',
						dataIndex: 'shiptoName1',
						width: defaultColumnWidths.shiptoName1,
						sortable: true,
						customIndex: getColumnIndex('shiptoName1','<c:out value="${order_bestellung_BestellungSearchForm.idxShiptoName1}"/>'),
						hidden: true
					},{
						id: 'shiptoCity',
						header: '<fmt:message key="ordersearch.table.header.city"/>',
						dataIndex: 'shiptoCity',
						width: defaultColumnWidths.shiptoCity,
						customIndex: getColumnIndex('shiptoCity','<c:out value="${order_bestellung_BestellungSearchForm.idxShiptoCity}"/>'),
						sortable: true
					},{
						id: 'status',
						header: '<fmt:message key="ordersearch.table.header.status"/>',
						dataIndex: 'statusTxt',
						width: defaultColumnWidths.status,
						customIndex: getColumnIndex('status','<c:out value="${order_bestellung_BestellungSearchForm.idxStatus}"/>'),
						sortable: true
					},{
						id: 'materialTxt',
						header: '<fmt:message key="ordersearch.table.header.materialTxt"/>',
						dataIndex: 'materialTxt',
						width: defaultColumnWidths.materialTxt,
						customIndex: getColumnIndex('materialTxt','<c:out value="${order_bestellung_BestellungSearchForm.idxMaterialTxt}"/>'),
						sortable: true
					},{
						id: 'orderedQuantity',
						header: '<fmt:message key="ordersearch.table.header.orderedQuantity"/>',
						dataIndex: 'orderedQuantity',
						width: defaultColumnWidths.orderedQuantity,
						align: 'right',
						customIndex: getColumnIndex('orderedQuantity','<c:out value="${order_bestellung_BestellungSearchForm.idxOrderedQuantity}"/>'),
						sortable: true
					},{
						id: 'orderedUomTxt',
						header: '<fmt:message key="ordersearch.table.header.orderedUomTxt"/>',
						dataIndex: 'orderedUomTxt',
						width: defaultColumnWidths.orderedUomTxt,
						align: 'right',
						customIndex: getColumnIndex('orderedUomTxt','<c:out value="${order_bestellung_BestellungSearchForm.idxOrderedUomTxt}"/>'),
						sortable: true
					},{
						id: 'loadedQuantity',
						header: '<fmt:message key="ordersearch.table.header.loadedQuantity"/>',
						dataIndex: 'loadedQuantity',
						width: defaultColumnWidths.loadedQuantity,
						align: 'right',
						sortable: true,
						customIndex: getColumnIndex('loadedQuantity','<c:out value="${order_bestellung_BestellungSearchForm.idxLoadedQuantity}"/>'),
						hidden: true
					},{
						id: 'loadedUomTxt',
						header: '<fmt:message key="ordersearch.table.header.loadedUomTxt"/>',
						dataIndex: 'loadedUomTxt',
						width: defaultColumnWidths.loadedUomTxt,
						align: 'right',
						sortable: true,
						customIndex: getColumnIndex('loadedUomTxt','<c:out value="${order_bestellung_BestellungSearchForm.idxLoadedUomTxt}"/>'),
						hidden: true
					},{
						id: 'returnedQuantity',
						header: '<fmt:message key="ordersearch.table.header.returnedQuantity"/>',
						dataIndex: 'returnedQuantity',
						width: defaultColumnWidths.returnedQuantity,
						align: 'right',
						sortable: true,
						customIndex: getColumnIndex('returnedQuantity','<c:out value="${order_bestellung_BestellungSearchForm.idxReturnedQuantity}"/>'),
						hidden: true
					},{
						id: 'returnedUomTxt',
						header: '<fmt:message key="ordersearch.table.header.returnedUomTxt"/>',
						dataIndex: 'returnedUomTxt',
						width: defaultColumnWidths.returnedUomTxt,
						align: 'right',
						sortable: true,
						customIndex: getColumnIndex('returnedUomTxt','<c:out value="${order_bestellung_BestellungSearchForm.idxReturnedUomTxt}"/>'),
						hidden: true
					},{
						id: 'deliveredQuantity',
						header: '<fmt:message key="ordersearch.table.header.deliveredQuantity"/>',
						dataIndex: 'deliveredQuantity',
						width: defaultColumnWidths.deliveredQuantity,
						align: 'right',
						customIndex: getColumnIndex('deliveredQuantity','<c:out value="${order_bestellung_BestellungSearchForm.idxDeliveredQuantity}"/>'),
						sortable: true
					},{
						id: 'deliveredUomTxt',
						header: '<fmt:message key="ordersearch.table.header.deliveredUomTxt"/>',
						dataIndex: 'deliveredUomTxt',
						width: defaultColumnWidths.deliveredUomTxt,
						align: 'right',
						customIndex: getColumnIndex('deliveredUomTxt','<c:out value="${order_bestellung_BestellungSearchForm.idxDeliveredUomTxt}"/>'),
						sortable: true
					},{
						id: 'deliveryTypeTxt',
						header: '<fmt:message key="ordersearch.table.header.deliveryTypeTxt"/>',
						dataIndex: 'deliveryTypeTxt',
						sortable: true,
						width: defaultColumnWidths.deliveryTypeTxt,
						customIndex: getColumnIndex('deliveryTypeTxt','<c:out value="${order_bestellung_BestellungSearchForm.idxDeliveryTypeTxt}"/>'),
						hidden: true
					},{
						id: 'shippingpointName',
						header: '<fmt:message key="ordersearch.table.header.shippingpointName"/>',
						dataIndex: 'shippingpointName',
						width: defaultColumnWidths.shippingpointName,
						customIndex: getColumnIndex('shippingpointName','<c:out value="${order_bestellung_BestellungSearchForm.idxShippingpointName}"/>'),
						sortable: true
					},{
						id: 'deliveryFromDat',
						header: '<fmt:message key="ordersearch.table.header.deliveryFromDat"/>',
						dataIndex: 'deliveryFrom',
						width: defaultColumnWidths.deliveryFromDat,
						customIndex: getColumnIndex('deliveryFromDat','<c:out value="${order_bestellung_BestellungSearchForm.idxDeliveryFromDat}"/>'),
						sortable: true
					},{
						id: 'deliveryToDat',
						header: '<fmt:message key="ordersearch.table.header.deliveryToDat"/>',
						dataIndex: 'deliveryTo',
						width: defaultColumnWidths.deliveryToDat,
						customIndex: getColumnIndex('deliveryToDat','<c:out value="${order_bestellung_BestellungSearchForm.idxDeliveryToDat}"/>'),
						sortable: true
					},{
						id: 'plannedDeliveryDat',
						header: '<fmt:message key="ordersearch.table.header.plannedDeliveryDat"/>',
						dataIndex: 'plannedDelivery',
						width: defaultColumnWidths.plannedDeliveryDat,
						customIndex: getColumnIndex('plannedDeliveryDat','<c:out value="${order_bestellung_BestellungSearchForm.idxPlannedDeliveryDat}"/>'),
						sortable: true
					},{
						id: 'poolName',
						header: '<fmt:message key="ordersearch.table.header.poolName"/>',
						dataIndex: 'poolName',
						width: defaultColumnWidths.poolName,
						sortable: true,
						customIndex: getColumnIndex('poolName','<c:out value="${order_bestellung_BestellungSearchForm.idxPoolName}"/>'),
						hidden: true
					},{
						id: 'commCarrierName',
						header: '<fmt:message key="ordersearch.table.header.commCarrierName"/>',
						dataIndex: 'commCarrierName',
						width: defaultColumnWidths.commCarrierName,
						customIndex: getColumnIndex('commCarrierName','<c:out value="${order_bestellung_BestellungSearchForm.idxCommCarrierName}"/>'),
						sortable: true
					},{
						id: 'execCarrierName',
						header: '<fmt:message key="ordersearch.table.header.execCarrierName"/>',
						dataIndex: 'execCarrierName',
						width: defaultColumnWidths.execCarrierName,
						sortable: true,
						customIndex: getColumnIndex('execCarrierName','<c:out value="${order_bestellung_BestellungSearchForm.idxExecCarrierName}"/>'),
						hidden: true			
					},{
						id: 'billtoName1',
						header: '<fmt:message key="ordersearch.table.header.billtoName1"/>',
						dataIndex: 'billtoName1',
						width: defaultColumnWidths.billtoName1,
						sortable: true,
						customIndex: getColumnIndex('billtoName1','<c:out value="${order_bestellung_BestellungSearchForm.idxBilltoName1}"/>'),
						hidden: true
					},{
						id: 'driverFullName',
						header: '<fmt:message key="ordersearch.table.header.driverFullName"/>',
						dataIndex: 'driverFullName',
						width: defaultColumnWidths.driverFullName,
						sortable: true,
						customIndex: getColumnIndex('driverFullName','<c:out value="${order_bestellung_BestellungSearchForm.idxDriverFullName}"/>'),
						hidden: true
					},{
						id: 'vehicleName',
						header: '<fmt:message key="ordersearch.table.header.vehicleName"/>',
						dataIndex: 'vehicleName',
						width: defaultColumnWidths.vehicleName,
						sortable: true,
						customIndex: getColumnIndex('vehicleName','<c:out value="${order_bestellung_BestellungSearchForm.idxVehicleName}"/>'),
						renderer: function(v, p, record){
							var rendered = record.data.vehicleName;
							if ((record.data.wagonId != null) && (rendered != null)) {
								var re = new RegExp("(\\d{4})(\\d{4})(\\d{3})(\\d{1})");
							  	var m = re.exec(rendered);
							  	if (m != null) {
									rendered = "";
									rendered = m[1]+" "+m[2]+" "+m[3]+"-"+m[4];
								}
							}
							return rendered;
						}
					},{
						id: 'orderValidationCode',
						header: '<fmt:message key="ordersearch.table.header.orderValidationCode"/>',
						dataIndex: 'orderValidationCode',
						width: defaultColumnWidths.orderValidationCode,
						sortable: true,
						customIndex: getColumnIndex('orderValidationCode','<c:out value="${order_bestellung_BestellungSearchForm.idxOrderValidationCode}"/>'),
						hidden: true
					},
					{
						id: 'shiptoAddress',
						header: '<fmt:message key="ordersearch.table.header.shiptoAddress"/>',
						dataIndex: 'shiptoAddress',
						width: defaultColumnWidths.shiptoAddress,
						sortable: true,
						customIndex: getColumnIndex('shiptoAddress','<c:out value="${order_bestellung_BestellungSearchForm.idxShiptoAddress}"/>'),
						hidden: true
					},
					{
						id: 'noteOrderMgmt',
						header: '<fmt:message key="ordersearch.table.header.noteOrderMgmt"/>',
						dataIndex: 'noteOrderMgmt',
						width: defaultColumnWidths.noteOrderMgmt,
						sortable: true,
						customIndex: getColumnIndex('noteOrderMgmt','<c:out value="${order_bestellung_BestellungSearchForm.idxNoteOrderMgmt}"/>'),
						hidden: true
					},{
						id: 'orderNumberFromSeqUsage',
						header: '<fmt:message key="ordersearch.table.header.orderNumberFromSeqUsage"/>',
						dataIndex: 'orderNumberFromSeqUsage',
						width: defaultColumnWidths.orderNumberFromSeqUsage,
						sortable: true,
						customIndex: getColumnIndex('orderNumberFromSeqUsage','<c:out value="${order_bestellung_BestellungSearchForm.idxOrderNumberFromSeqUsage}"/>'),
						hidden: true
					},{
						id: 'noteRailwayDisp',
						header: '<fmt:message key="ordersearch.table.header.noteRailwayDisp"/>',
						dataIndex: 'noteRailwayDisp',
						width: defaultColumnWidths.noteRailwayDisp,
						sortable: true,
						customIndex: getColumnIndex('orderValidationCode','<c:out value="${order_bestellung_BestellungSearchForm.idxNoteRailwayDisp}"/>'),
						hidden: true
					},{
						id: 'noteTruckDriver',
						header: '<fmt:message key="ordersearch.table.header.noteTruckDriver"/>',
						dataIndex: 'noteTruckDriver',
						width: defaultColumnWidths.noteTruckDriver,
						sortable: true,
						customIndex: getColumnIndex('noteTruckDriver','<c:out value="${order_bestellung_BestellungSearchForm.idxNoteTruckDriver}"/>'),
						hidden: true
					},{
						id: 'noteDispatching',
						header: '<fmt:message key="ordersearch.table.header.noteDispatching"/>',
						dataIndex: 'noteDispatching',
						width: defaultColumnWidths.noteDispatching,
						sortable: true,
						customIndex: getColumnIndex('noteDispatching','<c:out value="${order_bestellung_BestellungSearchForm.idxNoteDispatching}"/>'),
						hidden: true
					},{
						id: 'noteDelivery',
						header: '<fmt:message key="ordersearch.table.header.noteDelivery"/>',
						dataIndex: 'noteDelivery',
						width: defaultColumnWidths.noteDelivery,
						sortable: true,
						customIndex: getColumnIndex('noteDelivery','<c:out value="${order_bestellung_BestellungSearchForm.idxNoteDelivery}"/>'),
						hidden: true
					},{
						id: 'loadingTs',
						header: '<fmt:message key="ordersearch.table.header.loadingTs"/>',
						dataIndex: 'loadingStr',
						width: defaultColumnWidths.loadingTs,
						sortable: true,
						customIndex: getColumnIndex('loadingTs','<c:out value="${order_bestellung_BestellungSearchForm.idxLoadingTs}"/>'),
						hidden: true
					},{
						id: 'ldsDeliveryNoteNo',
						header: '<fmt:message key="ordersearch.table.header.ldsDeliveryNoteNo"/>',
						dataIndex: 'ldsDeliveryNoteNo',
						width: defaultColumnWidths.ldsDeliveryNoteNo,
						sortable: true,
						customIndex: getColumnIndex('ldsDeliveryNoteNo','<c:out value="${order_bestellung_BestellungSearchForm.idxLdsDeliveryNoteNo}"/>'),
						hidden: true
					},{
						id: 'itrackStatusCd',
						header: '<fmt:message key="ordersearch.table.header.checkUnloadFlag"/>',
						dataIndex: 'itrackStatusCd',
						width: defaultColumnWidths.itrackStatusCd,
						sortable: true,
						customIndex: getColumnIndex('itrackStatusCd','<c:out value="${order_bestellung_BestellungSearchForm.idxItrackStatusCd}"/>'),
						renderer: function(val) {
							if(val == <%=ITrackStatus.DELIVERED_WRONG_LOCATION.getStatusCode()%>) {
								return '<span style="color: red;">true</span>';
							} else {
								return 'false';
							}
						},
						hidden: true
					}
					];
	
	defaultColumns.sort(function(a, b) {
	    return a.customIndex - b.customIndex;
	});
	
	var grid = new Ext.grid.GridPanel({
		id: 'searchGrid',
		el:'divSearchResults',
		width: Ext.get('divSimpleSearch').getWidth(),
<c:choose>
	<c:when test="${mode == 'monitoring'}">
		height:685,
	</c:when>
	<c:otherwise>
		height:602,
	</c:otherwise>
</c:choose>
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
		title:'<fmt:message key="ordersearch.table.header.title"/>',
		store: orderSearchStore,
		pageSize: searchArea.pageSize,
		loadMask: true,
		selModel: new Ext.grid.RowSelectionModel({singleSelect:true}),
		columns: defaultColumns,

		bbar: pagingBar
		
	});
	
	grid.render();

<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"></script></c:if>
	