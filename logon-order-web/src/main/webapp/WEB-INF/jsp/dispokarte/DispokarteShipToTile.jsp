<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: DispokarteShipToTile.jsp,v $ --%>

<%@ page session="true" %>

<%@ include file="../includeTLDs.jsp" %>
<%@page import="com.crh.logon.order.web.actions.dispokarte.DispokarteShipToEntryAction"%>
<c:if test="false">
    <%-- for preview in MyEclipse --%>
    <link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/OrderAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.CheckComboBox.js'/>"></script>
<html:form action="dispokarte/DispokarteShipTo.do" styleId="ShipToForm">
<jsp:include page="../SubTab9Tile.jsp"/>

<!-- Begin table 3 rows -->
<table class="big_form2">
      
      <tr class="classicblue">
      <th class="classicblue"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.1"/></th>
      </tr>
      <tr>
    
      <td>
<table class="big_form2"style="width: 99%;">
	<colgroup>
		<col width="1%">
		<col width="79%>
		<col width="19%">
		<col width="1%">
	</colgroup>
	<tr>
  <tbody>
  	<tr>
		<td class='label'><nobr><fmt:message key="dispokarte.texte.shipto"/>:</nobr></td>
	    <td>
	   		<input type="text" name="searchtext" value="" size="50" class="textfield" id="search">
	    </td>
		<td class="buttons" style="text-align: left">
			<input type="button" id="buttonSavePreferences" class="ViewButtonStyle" style="margin-bottom: 6px"
				   value="<fmt:message key="buttons.save"/>"/>
			<input type="button" id="buttonResetField" class="ViewButtonStyle" style="margin-bottom: 6px" value="<fmt:message key="buttons.reset"/>"/>
			<br/>

			<input type="text" id="activeContractDummy"/>
		</td>
		<td class="buttons">
			<input type="hidden" name="shipToId" id="shipToId" value=""/>
			<input type="hidden" name="soldToId" id="soldToId" value=""/>
		</td>
    </tr>
  </tbody>
</table>
            </td>
      </tr>
</table>
</html:form>

<script type="text/javascript">

var isCemActiveFlag = "CEM";
var isAggActiveFlag = "AGG";

var actions = {
	ACTION_SAVE_PREFERENCES: '<%=DispokarteShipToEntryAction.ACTION_SAVE_PREFERENCES%>',
	ACTION_RESET_PREFERENCES:  '<%=DispokarteShipToEntryAction.ACTION_RESET_PREFERENCES%>'
};
var withActiveCemContracts = <c:out value="${order_dispokarte_DispokarteForm.withActiveCemContracts}"/>;
var withActiveAggContracts = <c:out value="${order_dispokarte_DispokarteForm.withActiveAggContracts}"/>;
        
Ext.onReady(function(){

	var kundeRecordType = Ext.data.Record.create([
	    {name: 'kunde_id', mapping: 'soldtoNr'},    
	    {name: 'kunde_name', mapping: 'soldtoName'},    
	    {name: 'kunde_marked_for_deletion', mapping: 'soldtoMarkedForDeletion'},
	    {name: 'id', mapping: 'shiptoNr'},    
	    {name: 'name', mapping: 'shiptoName'},
	    {name: 'postalCode', mapping: 'postalCode'},
	    {name: 'city', mapping: 'city'},
	    {name: 'address', mapping: 'shiptoAddress'},
	    {name: 'shipto_marked_for_deletion', mapping: 'shiptoMarkedForDeletion'},
		{name: 'cemValidUntilDat', mapping: 'cemValidUntilDat', type: 'date'},
		{name: 'aggValidUntilDat', mapping: 'aggValidUntilDat', type: 'date'}
	]);

	function queryStr() {
		return Ext.get('search').dom.value;
	}

	function getCemActiveFlag() {
		return activeContractCheckComboBox.getCheckedValues().join().includes(isCemActiveFlag);
	}

	function getAggActiveFlag() {
		return activeContractCheckComboBox.getCheckedValues().join().includes(isAggActiveFlag);
	}
	
	var shipToKundeListStore = new Ext.data.Store({
    	proxy: new Ext.data.DWRProxyForPager(function(start, count, orderBy, query, callback) {
				OrderAjaxService.fetchShipToKundeListWithCemOrAggFilter(start, count, queryStr(), getCemActiveFlag(), getAggActiveFlag(), callback)
		}, true),
    	reader: new Ext.data.ListRangeReader({id:'id', totalProperty:'totalSize'}, kundeRecordType),
	    remoteSort: true
	});



    // Custom rendering Template
    var resultTpl = new Ext.XTemplate(
    	'<table class="classic"><thead><tr>',
    	'<th width="100"><fmt:message key="fields.kunde.id"/></th>',
    	'<th width="300"><fmt:message key="fields.kunde.name"/></th>',
    	'<th width="100"><fmt:message key="fields.shipto.id"/></th>',
    	'<th width="300"><fmt:message key="fields.shipto.name"/></th>',
    	'<th width="100"><fmt:message key="fields.shipto.plz"/></th>',
    	'<th width="150"><fmt:message key="fields.shipto.ort"/></th>',
    	'<th width="50"><fmt:message key="fields.address"/></th>',
		'<tpl if="withActiveCemContracts">',
		'<th><fmt:message key="fields.cem.valid.until.date"/></th>',
		'</tpl>',
		'<tpl if="withActiveAggContracts">',
		'<th><fmt:message key="fields.agg.valid.until.date"/></th>',
		'</tpl>',
    	'</tr></thead><tbody>',
        '<tpl for="."><tr  class="listitem zebra{[xindex % 2]}">',
		'<tpl if=\"kunde_marked_for_deletion == \'0\'\">',
        '<td>{kunde_id}</td>',
        '</tpl>',
        '<tpl if=\"kunde_marked_for_deletion == \'1\'\">',
        '<td><strike>{kunde_id}</strike></td>',
        '</tpl>',
        '<td>{kunde_name}</td>',
        '<tpl if=\"shipto_marked_for_deletion == \'0\'\">',
        '<td>{id}</td>',
        '</tpl>',
        '<tpl if=\"shipto_marked_for_deletion == \'1\'\">',
        '<td><strike>{id}</strike></td>',
        '</tpl>',
        '<td>{name}</td>',
        '<td>{postalCode}</td>',
        '<td>{city}</td>',
        '<td>{address}</td>',
		'<tpl if="withActiveCemContracts">',
			'<td>{cemValidUntilDat:date("d.m.Y")}</td>',
		'</tpl>',
		'<tpl if="withActiveAggContracts">',
			'<td>{aggValidUntilDat:date("d.m.Y")}</td>',
		'</tpl>',
        '</tr></tpl>',
        '</tbody></table>'
    );
    
    var search = new Ext.form.ComboBox({
        store: shipToKundeListStore,
        displayField:'id',
        typeAhead: false,
        loadingText: '<fmt:message key="dispokarte.texte.shiptosearching"/>',
        maxHeight:800,
        minChars:3,
        pageSize:20,
        hideTrigger:true,
        tpl: resultTpl,
        applyTo: 'search',
        itemSelector: 'tr.listitem',
        onSelect: function(record){
        	Ext.get('shipToId').dom.value = record.data.id;
        	Ext.get('soldToId').dom.value = record.data.kunde_id;
            search.collapse();
            Ext.get('ShipToForm').dom.submit();
        }
    });
    Ext.get("search").setTop("0px");

	var activeContractStore = new Ext.data.SimpleStore({
		id: 0,
		fields: ['activeContractFlag', 'activeContractName', 'checked'],
		data : [
			[
				isCemActiveFlag,
				'<fmt:message key="dispokarte.texte.activecem"/>',
				withActiveCemContracts
			],
			[
				isAggActiveFlag,
				'<fmt:message key="dispokarte.texte.activeagg"/>',
				withActiveAggContracts
			]
		]
	});

	var activeContractCheckComboBox = new Ext.ux.CheckComboBox({
		store: activeContractStore,
		displayField: 'activeContractName',
		applyTo: 'activeContractDummy',
		checkedField: 'checked',
		valueField: 'activeContractFlag',
		typeAhead: true,
		autoSelect: true,
		mode: 'local',
		triggerAction: 'all',
		emptyText:'multiple',
		allLabel: '<fmt:message key="checkbox.label.activecontracts.both"/>',
		noneLabel: '<fmt:message key="checkbox.label.activecontracts.none"/>',
		forceSelection: true,
		selectOnFocus: true,
		width: 180,
		listWidth: 200,
		listeners: {
			change: function () { changeActiveFilter(); }
		}
	});

	Ext.get('buttonSavePreferences').on('click', function() {
		var params = { "formAction": actions.ACTION_SAVE_PREFERENCES,
			"withActiveAggContracts": withActiveAggContracts,
			"withActiveCemContracts": withActiveCemContracts
		};

		addFormElements(params, "ShipToForm"); // add form data
		Ext.Ajax.request({
			url: 'DispokarteShipToEntry.do',
			params: params
		});
	});

	Ext.get('buttonResetField').on('click', function() {
		var params = { "formAction": actions.ACTION_RESET_PREFERENCES };

		Ext.Ajax.request({
			url : 'DispokarteShipToEntry.do',
			params : params,
			success: function() {
				// Refresh the whole page to refresh the filters
				window.location = document.URL;
			}
		});
	});

	function toggleActiveContract() {
		search.focus();
		search.expand();
		shipToKundeListStore.load({params:{start: 0, limit: 20, sort: '', query: ''}});
	}

	function changeActiveFilter() {
		if (getCemActiveFlag() !== withActiveCemContracts) {
			withActiveCemContracts = !withActiveCemContracts;
			OrderAjaxService.setWithActiveCemFilterForDispokarte(withActiveCemContracts, function(){
				toggleActiveContract();
			});
		}
		if (getAggActiveFlag() !== withActiveAggContracts ) {
			withActiveAggContracts = !withActiveAggContracts;
			OrderAjaxService.setWithActiveAggFilterForDispokarte(withActiveAggContracts, function(){
				toggleActiveContract();
			});
		}
	}

    search.focus();
});

</script>

<%-- END $RCSfile: DispokarteShipToTile.jsp,v $ --%>
