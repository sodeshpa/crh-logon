<%@ include file="../includeTLDs.jsp" %>

<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"><script type="text/javascript"><!--</c:if>

    // ------ PAGER ---------
    var pagingBar = new Ext.PagingToolbar({
        pageSize: searchArea.pageSize,
        store: dispocardSearchStore,
        displayInfo: true,
        displayMsg: '<fmt:message key="dispocardsearch.displayMsgParametered"/>',
        emptyMsg: '<fmt:message key="dispocardsearch.emptyMsg"/>'
    });

    var grid = new Ext.grid.GridPanel({
    	id: 'searchGrid',
        el:'divSearchResults',
        width: Ext.get('divSimpleSearch').getWidth(),
    	height:602,
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
        title:'<fmt:message key="dispocardsearch.table.header.title"/>',
        store: dispocardSearchStore,
        pageSize: searchArea.pageSize,
        loadMask: true,
		selModel: new Ext.grid.RowSelectionModel({singleSelect:true}),
        // grid columns
        columns:[
        {
            id: 'shiptoNr', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header: '<fmt:message key="dispocardsearch.table.header.shiptoNumber"/>',
            dataIndex: 'shiptoNr',
            width: 90,
            sortable: true
        },{

            id: 'shiptoName', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header: '<fmt:message key="dispocardsearch.table.header.shiptoName"/>',
            dataIndex: 'shiptoName',
            width: 90,
            sortable: true
        },{
            id: 'sareaDisplayLongname', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header: '<fmt:message key="dispocardsearch.table.header.sareaName"/>',
            dataIndex: 'sareaDisplayLongname',
            width: 120,
            sortable: true
        },{
            id: 'sapContractNo', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header: '<fmt:message key="dispocardsearch.table.header.contractNumber"/>',
            dataIndex: 'sapContractNo',
            width: 150,
            sortable: true
        },{
            id: 'enabledFlag',
            header: '<fmt:message key="dispocardsearch.table.header.active"/>',
            dataIndex: 'enabledFlag',
            width: 80,
            sortable: true
        },{
            id: 'sapMaterialNo', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header: '<fmt:message key="dispocardsearch.table.header.materialNumber"/>',
            dataIndex: 'sapMaterialNo',
            width: 100,
            sortable: true
        },{
            id: 'materialTxt', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header: '<fmt:message key="dispocardsearch.table.header.materialName"/>',
            dataIndex: 'materialTxt',
            width: 100,
            sortable: true
        }
        ],

        // paging bar on the bottom
        bbar: pagingBar
        
    });
    
    grid.render(); // render the grid

<%-- For JS syntax highlighting in Eclipse --%><c:if test="false">--></script></c:if>
    