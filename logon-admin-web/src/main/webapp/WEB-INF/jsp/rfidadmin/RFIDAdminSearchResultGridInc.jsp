<%@ include file="../includeTLDs.jsp" %>

<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"><script type="text/javascript"></c:if>

    var dateTimeRenderer = Ext.util.Format.dateRenderer('m/d/y H:i');
    
    // ------ PAGER ---------
    var pagingBar = new Ext.PagingToolbar({
        pageSize: searchArea.pageSize,
        store: rfidtagSearchStore,
        displayInfo: true,
        displayMsg: 'Displaying RFID Tags {0} - {1} of {2}',
        emptyMsg: "No results found..."
    });

    var sm = new Ext.grid.RowSelectionModel();
    sm.on("rowselect", function(){
		var rec = sm.getSelected();
		modifyRFIDTag(rec.data.rfidtagId);
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
        title:'RFID Tag search results',
        store: rfidtagSearchStore,
        pageSize: searchArea.pageSize,
        loadMask: true,
		selModel: sm,
        // grid columns
        columns:[
		{
            id: 'rfidIdentity', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header: '<fmt:message key="rfidadmin.header.rfidIdentity"/>', // TODO LitDict
            dataIndex: 'rfidIdentity',
            width: 150,
            sortable: true
        },{
            id: 'name', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header: '<fmt:message key="rfidadmin.header.name"/>', // TODO LitDict
            dataIndex: 'name',
            width: 150,
            sortable: true
        },{
            id: 'objectLabel', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header: '<fmt:message key="rfidadmin.header.objectLabel"/>', // TODO LitDict
            dataIndex: 'objectLabel',
            width: 150,
            sortable: true
        },{
            id: 'rfidObjectType', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header: '<fmt:message key="rfidadmin.header.rfidobjectTypeCd"/>', // TODO LitDict
            dataIndex: 'rfidObjectType',
            width: 150,
            sortable: true
        },{
            id: 'activeFlag', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
            header: '<fmt:message key="rfidadmin.header.activeFlag"/>', // TODO LitDict
            dataIndex: 'activeFlag',
            sortable: true,
            renderer: function(v, p, record){
        	if(record.data.activeFlag == true){
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
    