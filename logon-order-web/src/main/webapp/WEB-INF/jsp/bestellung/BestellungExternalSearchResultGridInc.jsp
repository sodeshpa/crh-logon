<%@ include file="../includeTLDs.jsp" %>

<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"><script type="text/javascript"></c:if>

    // ------ PAGER ---------
    var pagingBar = new Ext.PagingToolbar({
        pageSize: searchArea.pageSize,
        store: orderSearchStore,
        displayInfo: true,
        displayMsg: 'Displaying orders {0} - {1} of {2}',
        emptyMsg: "No results found..."
    });

	var defaultVisibleColumns = [
		"extOrderNumber",
		"extDeliveryNote",
		"shiptoName",
		"shiptoLoadUnload",
		"shiptoContactPersonTxt",
		"shiptoStreetHouseNo",
		"shiptoCountry",
		"shiptoPostalCode",
		"shiptoCity",
		"statusTxt",
		"materialName",
		"orderedQuantity",
		"orderedUomName",
		"deliveredQuantity",
		"deliveredUomName",
		"shippingpointName",
		"spLoadUnload",
		"spContactPersonTxt",
		"spStreetHouseNo",
		"spCountry",
		"spPostalCode",
		"spCity",
		"deliveryFrom",
		"deliveryTo",
		"truckName"
	];
	
	function getColumnIndex(colName,customIndex) {
		if (!customIndex) {
	    	return defaultColumnIndexes[colName];
	    }
	    return customIndex;
	}
	
	//width constants
	var defaultColumnWidths = {
		extOrderNumber: 80,
		extDeliveryNote: 80,
		shiptoName: 150,
		shiptoLoadUnload: 80,
		shiptoContactPersonTxt: 80,
		
		shiptoStreetHouseNo: 80,
		shiptoCountry: 40,
		shiptoPostalCode: 30,
		shiptoCity: 80,
		statusTxt: 120,
		
		materialName: 100,
		orderedQuantity: 30,
		orderedUomName: 50,
		deliveredQuantity: 30,
		deliveredUomName: 50,
		
		shippingpointName: 150,
		spLoadUnload: 80,
		spContactPersonTxt: 80,
		spStreetHouseNo: 80,
		spCountry: 40,
		
		spPostalCode: 30,
		spCity: 80,
		deliveryFrom: 100,
		deliveryTo: 100,
		poolName: 80,
		
		carrierName: 80,
		truckName: 100
	}
	
	var defaultColumnIndexes = {
			extOrderNumber: 1,
			extDeliveryNote: 2,
			shiptoName: 3,
			shiptoLoadUnload: 4,
			shiptoContactPersonTxt: 5,
			
			shiptoStreetHouseNo: 6,
			shiptoCountry: 7,
			shiptoPostalCode: 8,
			shiptoCity: 9,
			statusTxt: 10,
			
			materialName: 11,
			orderedQuantity: 12,
			orderedUomName: 13,
			deliveredQuantity: 14,
			deliveredUomName: 15,
			
			shippingpointName: 16,
			spLoadUnload: 17,
			spContactPersonTxt: 18,
			spStreetHouseNo: 19,
			spCountry: 20,
			
			spPostalCode: 21,
			spCity: 22,
			deliveryFrom: 22,
			deliveryTo: 23,
			poolName: 24,
			
			carrierName: 25,
			truckName: 26
		}
	
	var defaultColumns = [
	                      {
	                          id: 'extOrderNumber', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
	                          header: "Ord. Nr.", // TODO LitDict
	                          dataIndex: 'extOrderNumber',
	                          align: 'right',
	                          width: defaultColumnWidths.extOrderNumber,
	                          customIndex: getColumnIndex('extOrderNumber','<c:out value="${order_bestellung_BestellungExternalSearchForm.idxExtOrderNumber}"/>'),
	                          sortable: true
                              <crh-common:authorize function="<%= com.crh.logon.web.utils.Authorization.DISPATCHING_W %>">
            ,
                              renderer: function(val, meta, rec) {
  				               return '<a href="javascript:showPopup(\'<c:url value="/dispo/ExternalOrderEntry.do"/>?edit&extorderid=' + rec.data.extOrderId + '\')">' + val + '</a>';            
                              }
                             </crh-common:authorize>
	                      },{
	                          id: 'extDeliveryNote',
	                          header: '<fmt:message key="externalordersearch.table.header.dnComment"/>',
	                          dataIndex: 'extDeliveryNote',
	                          width: defaultColumnWidths.extDeliveryNote,
	                          customIndex: getColumnIndex('extDeliveryNote','<c:out value="${order_bestellung_BestellungExternalSearchForm.idxExtDeliveryNote}"/>'),
	                          sortable: true
	                      },{
	                          id: 'shiptoName', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
	                          header: "Ship-To", // TODO LitDict
	                          dataIndex: 'shiptoName',
	                          width: defaultColumnWidths.shiptoName,
	                          customIndex: getColumnIndex('shiptoName','<c:out value="${order_bestellung_BestellungExternalSearchForm.idxShiptoName}"/>'),
	                          sortable: true
	                      },{
	                          id: 'shiptoLoadUnload',
	                          header: "Load/Unload",
	                          dataIndex: 'shiptoLoadUnload',
	                          width: defaultColumnWidths.shiptoLoadUnload,
	                          customIndex: getColumnIndex('shiptoLoadUnload','<c:out value="${order_bestellung_BestellungExternalSearchForm.idxShiptoLoadUnload}"/>'),
	                          sortable: true
	                      },{
	                          id: 'shiptoContactPersonTxt',
	                          header: "Contact person",
	                          dataIndex: 'shiptoContactPersonTxt',
	                          width: defaultColumnWidths.shiptoContactPersonTxt,
	                          customIndex: getColumnIndex('shiptoContactPersonTxt','<c:out value="${order_bestellung_BestellungExternalSearchForm.idxShiptoContactPersonTxt}"/>'),
	                          sortable: true
	                      },{
	                          id: 'shiptoStreetHouseNo',
	                          header: "Street/House nr.",
	                          dataIndex: 'shiptoStreetHouseNo',
	                          width: defaultColumnWidths.shiptoStreetHouseNo,
	                          customIndex: getColumnIndex('shiptoStreetHouseNo','<c:out value="${order_bestellung_BestellungExternalSearchForm.idxShiptoStreetHouseNo}"/>'),
	                          sortable: true
	                      },{
	                          id: 'shiptoCountry',
	                          header: "Country",
	                          dataIndex: 'shiptoCountry',
	                          width: defaultColumnWidths.shiptoCountry,
	                          customIndex: getColumnIndex('shiptoCountry','<c:out value="${order_bestellung_BestellungExternalSearchForm.idxShiptoCountry}"/>'),
	                          sortable: true
	                      },{
	                          id: 'shiptoPostalCode',
	                          header: "ZIP",
	                          dataIndex: 'shiptoPostalCode',
	                          width: defaultColumnWidths.shiptoPostalCode,
	                          customIndex: getColumnIndex('shiptoPostalCode','<c:out value="${order_bestellung_BestellungExternalSearchForm.idxShiptoPostalCode}"/>'),
	                          sortable: true
	                      },{
	                          id: 'shiptoCity',
	                          header: "City",
	                          dataIndex: 'shiptoCity',
	                          width: defaultColumnWidths.shiptoCity,
	                          customIndex: getColumnIndex('shiptoCity','<c:out value="${order_bestellung_BestellungExternalSearchForm.idxShiptoCity}"/>'),
	                          sortable: true
	                      },{
	                      	id: 'statusTxt',
	                          header: "Status",
	                          dataIndex: 'statusTxt',
	                          width: defaultColumnWidths.statusTxt,
	                          customIndex: getColumnIndex('statusTxt','<c:out value="${order_bestellung_BestellungExternalSearchForm.idxStatusTxt}"/>'),
	                          sortable: true
	                      },{
	                          id: 'materialName', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
	                          header: "Product", // TODO LitDict
	                          dataIndex: 'materialName',
	                          width: defaultColumnWidths.materialName,
	                          customIndex: getColumnIndex('materialName','<c:out value="${order_bestellung_BestellungExternalSearchForm.idxMaterialName}"/>'),
	                          sortable: true
	                      },{
	                          id: 'orderedQuantity', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
	                          header: "O#", // TODO LitDict
	                          dataIndex: 'orderedQuantity',
	                          width: defaultColumnWidths.orderedQuantity,
	                          align: 'right',
	                          customIndex: getColumnIndex('orderedQuantity','<c:out value="${order_bestellung_BestellungExternalSearchForm.idxOrderedQuantity}"/>'),
	                          sortable: true
	                      },{
	                          id: 'orderedUomName', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
	                          header: "O-UoM", // TODO LitDict
	                          dataIndex: 'orderedUomName',
	                          width: defaultColumnWidths.orderedUomName,
	                          align: 'right',
	                          customIndex: getColumnIndex('orderedUomName','<c:out value="${order_bestellung_BestellungExternalSearchForm.idxOrderedUomName}"/>'),
	                          sortable: true
	                      },{
	                          id: 'deliveredQuantity', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
	                          header: "D#", // TODO LitDict
	                          dataIndex: 'deliveredQuantity',
	                          width: defaultColumnWidths.deliveredQuantity,
	                          align: 'right',
	                          customIndex: getColumnIndex('deliveredQuantity','<c:out value="${order_bestellung_BestellungExternalSearchForm.idxDeliveredQuantity}"/>'),
	                          sortable: true
	                      },{
	                          id: 'deliveredUomName', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
	                          header: "D-UoM", // TODO LitDict
	                          dataIndex: 'deliveredUomName',
	                          width: defaultColumnWidths.deliveredUomName,
	                          align: 'right',
	                          customIndex: getColumnIndex('deliveredUomName','<c:out value="${order_bestellung_BestellungExternalSearchForm.idxDeliveredUomName}"/>'),
	                          sortable: true
	                      },{
	                          id: 'shippingpointName', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
	                          header: "Shipping Point", // TODO LitDict
	                          dataIndex: 'shippingpointName',
	                          width: defaultColumnWidths.shippingpointName,
	                          customIndex: getColumnIndex('shippingpointName','<c:out value="${order_bestellung_BestellungExternalSearchForm.idxShippingpointName}"/>'),
	                          sortable: true
	                      },{
	                          id: 'spLoadUnload',
	                          header: "Load/Unload",
	                          dataIndex: 'spLoadUnload',
	                          width: defaultColumnWidths.spLoadUnload,
	                          customIndex: getColumnIndex('spLoadUnload','<c:out value="${order_bestellung_BestellungExternalSearchForm.idxSpLoadUnload}"/>'),
	                          sortable: true
	                      },{
	                          id: 'spContactPersonTxt',
	                          header: "Contact person",
	                          dataIndex: 'spContactPersonTxt',
	                          width: defaultColumnWidths.spContactPersonTxt,
	                          customIndex: getColumnIndex('spContactPersonTxt','<c:out value="${order_bestellung_BestellungExternalSearchForm.idxSpContactPersonTxt}"/>'),
	                          sortable: true
	                      },{
	                          id: 'spStreetHouseNo',
	                          header: "Street/House nr.",
	                          dataIndex: 'spStreetHouseNo',
	                          width: defaultColumnWidths.spStreetHouseNo,
	                          customIndex: getColumnIndex('spStreetHouseNo','<c:out value="${order_bestellung_BestellungExternalSearchForm.idxSpStreetHouseNo}"/>'),
	                          sortable: true
	                      },{
	                          id: 'spCountry',
	                          header: "Country",
	                          dataIndex: 'spCountry',
	                          width: defaultColumnWidths.spCountry,
	                          customIndex: getColumnIndex('spCountry','<c:out value="${order_bestellung_BestellungExternalSearchForm.idxSpCountry}"/>'),
	                          sortable: true
	                      },{
	                          id: 'spPostalCode',
	                          header: "ZIP",
	                          dataIndex: 'spPostalCode',
	                          width: defaultColumnWidths.spPostalCode,
	                          customIndex: getColumnIndex('spPostalCode','<c:out value="${order_bestellung_BestellungExternalSearchForm.idxSpPostalCode}"/>'),
	                          sortable: true
	                      },{
	                          id: 'spCity',
	                          header: "City",
	                          dataIndex: 'spCity',
	                          width: defaultColumnWidths.spCity,
	                          customIndex: getColumnIndex('spCity','<c:out value="${order_bestellung_BestellungExternalSearchForm.idxSpCity}"/>'),
	                          sortable: true
	                      },{
	                          id: 'deliveryFrom', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
	                          header: "Del. From", // TODO LitDict
	                          dataIndex: 'deliveryFrom',
	                          width: defaultColumnWidths.deliveryFrom,
	                          customIndex: getColumnIndex('deliveryFrom','<c:out value="${order_bestellung_BestellungExternalSearchForm.idxDeliveryFrom}"/>'),
	                          sortable: true
	                      },{
	                          id: 'deliveryTo', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
	                          header: "Del. To", // TODO LitDict
	                          dataIndex: 'deliveryTo',
	                          width: defaultColumnWidths.deliveryTo,
	                          customIndex: getColumnIndex('deliveryTo','<c:out value="${order_bestellung_BestellungExternalSearchForm.idxDeliveryTo}"/>'),
	                          sortable: true
	                      },{
	                          id: 'poolName', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
	                          header: "Pool", // TODO LitDict
	                          dataIndex: 'poolName',
	                          width: defaultColumnWidths.poolName,
	                          customIndex: getColumnIndex('poolName','<c:out value="${order_bestellung_BestellungExternalSearchForm.idxPoolName}"/>'),
	                          sortable: true,
	                          hidden: true
	                      },{
	                          id: 'carrierName', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
	                          header: "Carrier", // TODO LitDict
	                          dataIndex: 'carrierName',
	                          width: defaultColumnWidths.carrierName,
	                          customIndex: getColumnIndex('carrierName','<c:out value="${order_bestellung_BestellungExternalSearchForm.idxCarrierName}"/>'),
	                          sortable: true,
	                          hidden: true
	                      },{
	                          id: 'truckName', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
	                          header: "Vehicle", // TODO LitDict
	                          dataIndex: 'truckName',
	                          width: defaultColumnWidths.truckName,
	                          customIndex: getColumnIndex('truckName','<c:out value="${order_bestellung_BestellungExternalSearchForm.idxTruckName}"/>'),
	                          sortable: true
	                      }
	                      ];
	
	defaultColumns.sort(function(a, b) {
	    return a.customIndex - b.customIndex;
	});
		
    var grid = new Ext.grid.GridPanel({
    	id: 'searchGrid',
        el:'divSearchResults',
        width: Ext.get('divSimpleSearch').getWidth(),
    	height:602,
        //enableColumnHide: false,
        //autoExpandColumn: 'billtoName1',
		viewConfig: {autoFill: true, forceFit: true},        
		stripeRows: true,
        title:'<fmt:message key="ordersearch.table.header.title"/>',
        store: orderSearchStore,
        pageSize: searchArea.pageSize,
        loadMask: true,
		selModel: new Ext.grid.RowSelectionModel({singleSelect:true}),
        // grid columns
        columns: defaultColumns,

        // paging bar on the bottom
        bbar: pagingBar
        
    });
    
    grid.render(); // render the grid

<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"></script></c:if>
    