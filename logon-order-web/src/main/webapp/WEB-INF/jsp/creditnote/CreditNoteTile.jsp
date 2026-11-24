<%@ page session="true"%>

<%@ include file="../includeTLDs.jsp"%>
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="fmtx"%>
<%@ page import="com.crh.logon.order.web.ajax.CreditNoteAjaxService"%>
<%@page import="com.crh.logon.persistence.entities.views.CreditNoteProposalsEntity"%>
<%@page import="com.crh.logon.order.web.utils.CreditNoteBean"%>

<c:if test="false">
	<%-- for preview in MyEclipse --%>
	<link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>

<c:set var="odUrl">
	<%= request.getContextPath()%>/../order/auftrag/OrderDetailEntry.do
</c:set> 

<style type="text/css">

	.margintop8 {
		margin-top: 8px;
	}
	 
	.cmid {
		horizontal-align: center;
		vertical-align: middle;
	}
	
	.dynlabel {
		font-size: 12px;
		color: #666666; 	
	}
	
	.listclass {
		overflow: hidden;
	}
	
</style>

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/CreditNoteAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.grid.CheckColumn.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.grid.PagingToolbar.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.data.OriginalRecordStore.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.valid.ValidationMessages.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.grid.EditableList.js'/>"></script>

<script type="text/javascript">

Ext.onReady(function(){

    var dateTimeRenderer = Ext.util.Format.dateRenderer('m/d/y H:i');

	// --- Data record and store definitions ---
	var creditNoteRecord = Ext.data.Record.create([
		{name: '<%=CreditNoteBean.ATTR_ORDER_NUMBER%>'},
		{name: '<%=CreditNoteBean.ATTR_ORDER_ITEM_NO%>', type: 'float'},
		{name: '<%=CreditNoteBean.ATTR_PLANT_NAME%>'},
		{name: '<%=CreditNoteBean.ATTR_WAGON_NO%>'},
		{name: '<%=CreditNoteBean.ATTR_SOLDTO_NAME%>'},
		{name: '<%=CreditNoteBean.ATTR_SHIPTO_NAME%>'},
		{name: '<%=CreditNoteBean.ATTR_BILLTO_NAME%>'},
		{name: '<%=CreditNoteBean.ATTR_SAP_MATERIAL_NO%>'},
		{name: '<%=CreditNoteBean.ATTR_MATERIAL_TXT%>'},
		{name: '<%=CreditNoteBean.ATTR_DELIVERED_QUANTITY%>'},
		{name: '<%=CreditNoteBean.ATTR_RETURNED_QUANTITY%>'},
	    {name: '<%=CreditNoteBean.ATTR_YES_FLAG%>', type: 'bool'},
	    {name: '<%=CreditNoteBean.ATTR_NO_FLAG%>', type: 'bool'},
	    {name: '<%=CreditNoteBean.ATTR_ORDER_ID%>', type: 'float'},
	    {name: '<%=CreditNoteBean.ATTR_ORDER_ITEM_ID%>', type: 'float'}
	]);

    var creditNoteStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(
        	function(skip, count, sort, query, callback) {
        		var orderNumber = Ext.get('searchField').getValue().toString();
        		CreditNoteAjaxService.fetchCreditNotes(
        			skip, count, sort, orderNumber, callback)
        	}, 
        	true
        ),
		reader: new Ext.data.ListRangeReader(
			// {id: 'auftragsNr', totalProperty:'totalSize', messagesProperty: 'messages'}, 
			{id: 'orderItemId', totalProperty:'totalSize'}, 
			creditNoteRecord
		),
		remoteSort: true
    });

    // ------ Toolbars ---------
    var operationsBar = new Ext.Toolbar([
        	{id: 'saveButton', text: '<fmt:message key="creditnote.button.sap.uebermitteln"/>'},
	    	{xtype: 'tbseparator'},
	    	{id: 'resetButton', text: '<fmt:message key="creditnote.button.flags.entfernen"/>'},
			{xtype: 'tbseparator'},
			{id: 'searchFieldLabel', xtype: 'label', forid: 'searchField', text: LitDict['creditnote.header.auftragsnummer'] +
					':'},
			{id: 'searchField', xtype: 'textfield', name: 'searchField', fieldLabel: LitDict['creditnote.header.auftragsnummer']},
			{id: 'searchButton', text: LitDict['creditnote.button.search'],
				handler: function(){ cnGrid.store.reload({params: {start: 0, limit: pagingBar.pageSize}}); }
			}
	]);

    var pagingBar = new Ext.ux.grid.PagingToolbar({
    	id:				'creditNotePager',
		store:			creditNoteStore,
        pageSize:		Ext.ux.grid.EditableList.DEFAULT_PAGE_SIZE,
        displayInfo:	true,
        displayMsg:		'<fmt:message key="creditnote.pager.displayMsg"/>',
        emptyMsg:		'<fmt:message key="creditnote.pager.emptyMsg"/>'
    });

	// ------ Components ---------
    var yesColumn = new Ext.ux.grid.CheckColumn({
		header:		'<fmt:message key="creditnote.header.yesFlag"/>', 
		sortable:	false, 
		dataIndex:	'<%=CreditNoteBean.ATTR_YES_FLAG%>',
		width:		55,
		editable:	true
    });

    var noColumn = new Ext.ux.grid.CheckColumn({
		header:		'<fmt:message key="creditnote.header.noFlag"/>', 
		sortable:	false, 
		dataIndex:	'<%=CreditNoteBean.ATTR_NO_FLAG%>',
		width:		55,
		editable:	true
    });

	var orderDetailBaseUrl = '<c:out value="${odUrl}" />';

	var columnModel = new Ext.grid.ColumnModel([
		{
			header: 	'<fmt:message key="creditnote.header.auftragsnummer"/>', 
			sortable: 	true, 
			dataIndex:	'<%=CreditNoteBean.ATTR_ORDER_NUMBER%>',
			renderer:	function(cellValue, cellMetaData, recordOfCell) {
				var orderDetailUrl = orderDetailBaseUrl + '?orderid=' + recordOfCell.get('orderId');
				var orderDetailLink = '<a href="javascript:showPopup(\'' + orderDetailUrl + '\')">';

				orderDetailLink += cellValue;
				orderDetailLink += '</a>';

				return orderDetailLink;
			}}, 
		{header: '<fmt:message key="creditnote.header.position"/>', sortable: true, dataIndex: '<%=CreditNoteBean.ATTR_ORDER_ITEM_NO%>'},
		{header: '<fmt:message key="creditnote.header.lieferwerk"/>', sortable: true, dataIndex: '<%=CreditNoteBean.ATTR_PLANT_NAME%>'},
		{header: '<fmt:message key="creditnote.header.bahnwagenNr"/>', sortable: false, dataIndex: '<%=CreditNoteBean.ATTR_WAGON_NO%>'},
		{header: '<fmt:message key="creditnote.header.kunde"/>', sortable: true, dataIndex: '<%=CreditNoteBean.ATTR_SOLDTO_NAME%>'},
		{header: '<fmt:message key="creditnote.header.shipTo"/>', sortable: true, dataIndex: '<%=CreditNoteBean.ATTR_SHIPTO_NAME%>'},
		{header: '<fmt:message key="creditnote.header.rechnungsempfaenger"/>', sortable: true, dataIndex: '<%=CreditNoteBean.ATTR_BILLTO_NAME%>'},
		{header: '<fmt:message key="creditnote.header.produktnummer"/>', sortable: true, dataIndex: '<%=CreditNoteBean.ATTR_SAP_MATERIAL_NO%>'},
		{header: '<fmt:message key="creditnote.header.produktname"/>', sortable: true, dataIndex: '<%=CreditNoteBean.ATTR_MATERIAL_TXT%>'},
			<%-- editor: new Ext.form.TextField({allowBlank: false})}, --%>
		{header: '<fmt:message key="creditnote.header.liefermenge"/>', align: 'right', sortable: false, dataIndex: '<%=CreditNoteBean.ATTR_DELIVERED_QUANTITY%>', type: 'float'},
		{header: '<fmt:message key="creditnote.header.rueckwaegemenge"/>', align: 'right', sortable: false, dataIndex: '<%=CreditNoteBean.ATTR_RETURNED_QUANTITY%>', type: 'float'},
		yesColumn, 
		noColumn
    ]);

	var cnGrid = new Ext.grid.EditorGridPanel({
	    id:					'creditNoteGrid',
	    renderTo:			'creditNotesDiv',
	    width:				Ext.get('creditNotesDiv').getWidth(),
		autoHeight:			true,
		viewConfig:			{autoFill: true, forceFit: true},
		stripeRows:			true,
		enableColumnHide:	false,
		title:				'Credit notes',
		frame:				true,
        loadMask:			true,
        tbar:				operationsBar,
        bbar:				pagingBar,
        plugins:			[yesColumn, noColumn],
		store:				creditNoteStore,
		cm:					columnModel,
		clicksToEdit:		1
	});
	
	// --- Editor Handler Obj ---
	CreditNoteList = Ext.extend(Ext.ux.grid.EditableList, {

		callServiceForSave : function(changedRecords, callbackForSave) {
			CreditNoteAjaxService.saveCreditNotes(
				changedRecords, {callback: callbackForSave});
		}, 

		validateRecords: function(pRecords, pMessages) {
			var yesNoMsg = '<fmt:message key="creditnote.list.valid.error.yesno"/>';

			for (var index = 0; index < pRecords.length; index++) {
				var mRecord = pRecords[index];
				var yesFlag = mRecord.get('yesFlag');
				var noFlag = mRecord.get('noFlag');

				if ((yesFlag === true) && (noFlag === true)) {
					var message = yesNoMsg.replace(/\{0\}/, mRecord.get('<%=CreditNoteBean.ATTR_ORDER_NUMBER%>'));

					message = message.replace(/\{1\}/, mRecord.get('<%=CreditNoteBean.ATTR_ORDER_ITEM_NO%>'));

					pMessages.add(
						new Ext.ux.valid.ValidationMessage(
                            Ext.ux.valid.ValidationMessage.TYPE_ERROR, 
                            null, 
                            null, 
                            message));
				}
			}
		}
	});

	var editor = new CreditNoteList();

    // ------ MAIN ---------
    editor.init({
        grid:               cnGrid,
        pageSize:           pagingBar.getPageSize(),
        saveButtonId:       'saveButton',
        resetButtonId:      'resetButton',
        pageChangeTitle:    '<fmt:message key="creditnote.list.pageChangeTitle"/>',
        pageChangeMessage:  '<fmt:message key="creditnote.list.pageChangeMessage"/>',
        maskMessage:        '<fmt:message key="creditnote.list.maskMessage"/>',
        noChangesMessage:   '<fmt:message key="creditnote.list.noChangesMessage"/>',
        buttonYes:          '<fmt:message key="creditnote.list.buttonYes"/>',
        buttonNo:           '<fmt:message key="creditnote.list.buttonNo"/>',
        buttonCancel:       '<fmt:message key="creditnote.list.buttonCancel"/>'
    });

    logonPopupHideCallback = null;
});

</script>

<jsp:include page="../SubTab7Tile.jsp"/>   

<!-- Begin table 3 rows -->
<table class="big_form2">
      
      <tr class="classicblue">
      <th class="classicblue"><fmt:message key="tabitem.monitoring"/> / <fmt:message key="tabsubitem.7.2"/></th>
      </tr>
      <tr>
    
      <td>
			<jsp:include page="../Sub2Tab7Tile.jsp"/>   
			<div id="creditNotesDiv"><img height="1px" width="1px" src="<crh-common:url value='/images/default/s.gif'/>"/></div>
      </td>
      </tr>
</table>
