<%@ page session="true"%>

<%@ include file="../includeTLDs.jsp"%>
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="fmtx"%>
<%@ page import="com.crh.logon.admin.web.ajax.PoolAdminAjaxService"%>
<%@page import="com.crh.logon.admin.web.beans.ForwardingAgentBean"%>

<%@page import="com.crh.logon.admin.web.actions.truckadmin.PoolAdminAction"%><c:if test="false">
	<%-- for preview in MyEclipse --%>
	<link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>

<style type="text/css">

	div, td, th {
		font-size: 12px;
	}
	
	.margintop8 {
		margin-top: 8px;
	}
	 
	.smallfont th {
		font-size: 12px;
	}
	
	.smallfont td {
		font-size: 12px;
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

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/PoolAdminAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.grid.CheckColumn.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.grid.PagingToolbar.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.data.OriginalRecordStore.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.valid.ValidationMessages.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.grid.EditableList.js'/>"></script>

<script type="text/javascript">
<%--
Ext.onReady(function(){

   // var dateTimeRenderer = Ext.util.Format.dateRenderer('m/d/y H:i');

	// --- Data record and store definitions ---
	var fwaRecord = Ext.data.Record.create([
		{name: 'sapId', mapping: 'sapId'},
		{name: 'name', mapping: 'name'},
		{name: 'street', mapping: 'street'},
		{name: 'postalCode', mapping: 'postalCode'},
		{name: 'city', mapping: 'city'},
		{name: 'edited', mapping: 'edited'}
	]);

    var fwaStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(
        	function(skip, count, sort, query, callback) {
        		PoolAdminAjaxService.fetchForwardingAgents(
        			skip, count, sort, query, callback)
        	}, 
        	true
        ),
		reader: new Ext.data.ListRangeReader(
			{id: 'sapId', totalProperty:'totalSize'}, 
			fwaRecord
		),
		remoteSort: true
    });

    // ------ Toolbars ---------
    var operationsBar = new Ext.Toolbar([
        	{id: 'saveButton', text: '<fmt:message key="creditnote.button.sap.uebermitteln"/>'},
	    	{xtype: 'tbseparator'},
	    	{id: 'resetButton', text: '<fmt:message key="creditnote.button.flags.entfernen"/>'}
	]);

    var pagingBar = new Ext.ux.grid.PagingToolbar({
    	id:				'fwaPager',
		store:			fwaStore,
        pageSize:		Ext.ux.grid.EditableList.DEFAULT_PAGE_SIZE,
        displayInfo:	true,
        displayMsg:		'<fmt:message key="pooladmin.pager.displayMsg"/>',
        emptyMsg:		'<fmt:message key="pooladmin.pager.emptyMsg"/>'
    });

	// ------ Components ---------
/*    var yesColumn = new Ext.ux.grid.CheckColumn({
		header:		'<fmt:message key="creditnote.header.yesFlag"/>', 
		sortable:	false, 
		dataIndex:	'yesFlag', 
		width:		55
    });
*/
    var editColumn = new Ext.ux.grid.CheckColumn({
		header:		'<fmt:message key="pooladmin.header.edited"/>', 
		sortable:	true, 
		dataIndex:	'edited', 
		width:		55
    });

	var selectionModel = new Ext.grid.RowSelectionModel();
	selectionModel.on("rowselect", function(){
		var rec = selectionModel.getSelected();
		editFwa(rec.data.sapId);
	});

	var columnModel = new Ext.grid.ColumnModel([
		{header: '<fmt:message key="pooladmin.header.sapId"/>', sortable: true, dataIndex: 'sapId'}, 
		{header: '<fmt:message key="pooladmin.header.name"/>', sortable: true, dataIndex: 'name'},
		{header: '<fmt:message key="pooladmin.header.street"/>', sortable: true, dataIndex: 'street'},
		{header: '<fmt:message key="pooladmin.header.postalCode"/>', sortable: false, dataIndex: 'postalCode'},
		{header: '<fmt:message key="pooladmin.header.city"/>', sortable: true, dataIndex: 'city'}, 
		editColumn 
    ]);

	var fwaGrid = new Ext.grid.EditorGridPanel({
	    id:					'fwaGrid',
	    renderTo:			'fwaDiv',
	    width:				Ext.get('fwaDiv').getWidth(),
		autoHeight:			true,
		viewConfig:			{autoFill: true, forceFit: true},
		stripeRows:			true,
		enableColumnHide:	false,
		title:				'Forwarding Agent',
		frame:				true,
        loadMask:			true,
        tbar:				operationsBar,
        bbar:				pagingBar,
        plugins:			[editColumn],
		store:				fwaStore,
		cm:					columnModel,
		sm:					selectionModel,
		clicksToEdit:		1
	});

	// --- Editor Handler Obj ---
	FwaList = Ext.extend(Ext.ux.grid.EditableList, {
/*
		callServiceForSave : function(changedRecords, callbackForSave) {
			CreditNoteAjaxService.saveCreditNotes(
				changedRecords, {callback: callbackForSave});
		}, 

		validateRecords: function(pRecords, pMessages) {
			for (var index = 0; index < pRecords.length; index++) {
				var mRecord = pRecords[index];
				var yesFlag = mRecord.get('yesFlag');
				var noFlag = mRecord.get('noFlag');

				if ((yesFlag === true) && (noFlag === true)) {
					pMessages.add(
						new Ext.ux.valid.ValidationMessage(
                            Ext.ux.valid.ValidationMessage.TYPE_ERROR, 
                            null, 
                            null, 
                            "<fmt:message key="creditnote.list.valid.error.yesno"/>"));
				}
			}
		}
*/
	});

	var editor = new FwaList();

    // ------ MAIN ---------
    editor.init({
        grid:               fwaGrid,
        pageSize:           pagingBar.getPageSize()
 /*       saveButtonId:       'saveButton',
        resetButtonId:      'resetButton',
        pageChangeTitle:    '<fmt:message key="creditnote.list.pageChangeTitle"/>',
        pageChangeMessage:  '<fmt:message key="creditnote.list.pageChangeMessage"/>',
        maskMessage:        '<fmt:message key="creditnote.list.maskMessage"/>',
        noChangesMessage:   '<fmt:message key="creditnote.list.noChangesMessage"/>',
        buttonYes:          '<fmt:message key="creditnote.list.buttonYes"/>',
        buttonNo:           '<fmt:message key="creditnote.list.buttonNo"/>',
        buttonCancel:       '<fmt:message key="creditnote.list.buttonCancel"/>'
*/
    });
});
function editFwa(fwaId) {
	document.getElementById("inputAction").value="";
	var params = {"inputAction":document.getElementById("inputAction").value,"sapId": + fwaId, "ajaxCall":"ajaxCall"};
		var diva = Ext.get('editFwa');
		diva.load({
			url: 'PoolAdmin.do', 
			params: params,
			text: '<fmt:message key="roadmasterdata.loading.exjsloading"/>',
			scripts: true
		});	
}

--%>
</script>

<jsp:include page="../SubTab9Tile.jsp"/>   

<html:form action="/truckadmin/PoolAdmin.do" styleId="admin_PoolAdminForm">
<input type="hidden" name="inputAction" id="inputAction" value=""/>
<!-- Begin table 3 rows -->
<table class="big_form2">
			
      <tr class="classicblue">
      <th height="21" colspan="4" class="classicblue"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.5"/></th>
      </tr>
      <tr>
    
      <td>
			<jsp:include page="../Sub5Tab9Tile.jsp"/>
			
			
		<table class="classicblue" style="margin-top:4px">
			<tr>
      			<th class="classicblue"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.5"/> / <fmt:message key="tabsubitem.9.5.1"/></th>
            </tr>
            <tbody>
            <tr>
            <td>
		<table class="classicblue" style="margin-top:4px">
		      	
              <tr>			
              	<th>
					<fmt:message key="pooladmin.header.sapId"/>
					<br>
				</th>
				<th>
					<fmt:message key="pooladmin.header.name"/>
					<br>
				</th>
				<th>
					<fmt:message key="pooladmin.header.postalCode"/>
					<br>
				</th>
				<th>
					<fmt:message key="pooladmin.header.street"/>
					<br>
				</th>
				<th>
					<fmt:message key="pooladmin.header.city"/>
				</th>
				<th>
					<fmt:message key="pooladmin.header.logonName"/>
				</th>
				<th>
					<fmt:message key="pooladmin.header.selectedPools"/>
				</th>
				<th>
					<fmt:message key="pooladmin.header.storeEdit"/>
				</th>
			</tr>
			<c:set var="zebracount" value="0"/>
			<c:forEach var="fwa" varStatus="status" items="${admin_PoolAdminForm.fwaMap}">
				<c:set var="zebracount" value="${(zebracount + 1)%2}"/>
				<tr class="zebra<c:out value="${zebracount}" />">
					<td>
						<c:out value="${fwa.value.sapId}"/>
					</td>
					<td>
						<c:out value="${fwa.value.name}"/>
					</td>
					<td>
						<c:out value="${fwa.value.street}"/>
					</td>
					<td>
						<c:out value="${fwa.value.postalCode}"/>
					</td>
					<td>
						<c:out value="${fwa.value.city}"/>
					</td>
					<td>
					<html:text property="fwaMapEntry(${fwa.key}).carrier.displayName"/>
					</td>
					<td>
						<div class="scrollist" id="DivPools" style="height: 70px; width:250px;">
				<table class="classic" style="border:none">
				<c:forEach var="pool" varStatus="status" items="${fwa.value.possiblePools}">
					<tr class="zebra<c:out value="${status.count % 2}"/>">
						<td valign="middle" style="width:1px">
							<html:checkbox property="fwaMapEntry(${fwa.key}).selectedPoolMapEntry(${pool.key})" value="true" onchange=""/>
							<%-- 
							fwarListEntry[5].selectedPoolMapEntry(jani)=on 
							<html:checkbox property="${entry}.selectedPoolMapEntry('${pool.key}')" value="true" onchange=""/>
							--%>
						</td> 
					
						<td><c:out value="${pool.value.name}"/></td>
					</tr>
					<c:set var="lastStatusCount" value="${status.count}" />
				</c:forEach>
				</tr>
			</table>
			</div>
			</td>
			<td>
				<html:checkbox property="fwaMapEntry(${fwa.key}).edited"/>
			</td>
			</tr>
		</c:forEach>
		</table>
		<table class="big_form2" style="margin-top: 8px;">
			<tr>
				<td class="buttons right">
					<input class="ViewButtonStyle" value="<fmt:message key="buttons.reset"/>" type="submit" name="save" type="submit"/>
					<input class="ViewButtonStyle" value="<fmt:message key="buttons.save"/>" type="submit" name="save" type="submit"/>
				</td>
			</tr>
		</table>  
			<!-- <div id="fwaDiv"><img height="1px" width="1px" src="<c:out value="${pageContext.request.contextPath}"/>/images/default/s.gif" /></div> -->
			</td>
			</tr>
			</tbody>
			</table>
      </td>
      <!-- <td>
			<div id="editFwa" class="subform"></div>
	  </td> -->
      </tr>
</table>
			</html:form> 
