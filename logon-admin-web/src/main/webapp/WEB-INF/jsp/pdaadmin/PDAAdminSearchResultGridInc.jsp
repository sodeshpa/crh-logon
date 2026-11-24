<%@ include file="../includeTLDs.jsp" %>

<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"><script type="text/javascript"></c:if>

    var dateTimeRenderer = Ext.util.Format.dateRenderer('m/d/y H:i');
    
    // ------ PAGER ---------
    var pagingBar = new Ext.PagingToolbar({
        pageSize: searchArea.pageSize,
        store: pdaSearchStore,
        displayInfo: true,
        displayMsg: 'Displaying PDA devices {0} - {1} of {2}',
        emptyMsg: "No results found..."
    });

    var sm = new Ext.grid.RowSelectionModel();
    sm.on("rowselect", function(){
		var rec = sm.getSelected();
		modifyPDADevice(rec.data.pdaDeviceId);
	});
	
    var grid = new Ext.grid.GridPanel({
    	id: 'searchGrid',
        el:'divSearchResults',
        width: Ext.get('search').getWidth(),
        height:450,
        //enableColumnHide: false,
        //autoExpandColumn: 'billtoName1',
		viewConfig: {autoFill: true, forceFit: true},        
		stripeRows: true,
        title:'PDA device search results',
        store: pdaSearchStore,
        pageSize: searchArea.pageSize,
        loadMask: true,
		selModel: sm,
        // grid columns
        columns:[
		{
            id: 'pdaDeviceNo', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header: '<fmt:message key="pdaadmin.field.pdaDeviceNo" />',
            dataIndex: 'pdaDeviceNo',
            width: 150,
            sortable: true
        },{
            id: 'inventoryNumber', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header: '<fmt:message key="pdaadmin.field.inventoryNumber" />',
            dataIndex: 'inventoryNumber',
            width: 150,
            sortable: true
        },{
            id: 'typeDescription', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header: '<fmt:message key="pdaadmin.field.typeDescription" />',
            dataIndex: 'typeDescription',
            width: 150,
            sortable: true
        },{
            id: 'softwareVersion', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header: '<fmt:message key="pdaadmin.field.softwareVersion" />',
            dataIndex: 'softwareVersion',
            width: 150,
            sortable: true
        },{
            id: 'carrierId', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header: "Carrier Id", // TODO LitDict
            dataIndex: 'carrierId',
            width: 150,
            sortable: true
        },{
            id: 'carrierDisplayName', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header: "Carrier Name", // TODO LitDict
            dataIndex: 'carrierDisplayName',
            width: 150,
            sortable: true
        },{
            id: 'pdaDeviceActiveFlag', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header: "Active", // TODO LitDict
            dataIndex: 'pdaDeviceActiveFlag',
            sortable: true,
            renderer: function(v, p, record){
        	if(record.data.pdaDeviceActiveFlag == true){
        		return '<img width="16" height="16" alt="activated" src="' + crhCommonUrl('../images/active.png') + '"/>' ;
        	}
        	return '<img width="16" height="16" alt="deactivated" src="' + crhCommonUrl('../images/deactivated.png') + '"/>' ;
        	}
        }
        ],

        // paging bar on the bottom
        bbar: pagingBar
        
    });
    
    grid.render(); // render the grid

<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"></script></c:if>
    