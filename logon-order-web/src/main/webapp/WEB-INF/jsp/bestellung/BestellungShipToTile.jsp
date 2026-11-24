<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: BestellungShipToTile.jsp,v $ --%>

<%@ page session="true" %>
<%@ page import="com.crh.logon.web.utils.Authorization"%>
<%@page import="com.crh.logon.order.web.actions.bestellung.BestellungShipToEntryAction"%>

<%@ include file="../includeTLDs.jsp" %>
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="fmtx" %>
<c:if test="false"><%-- for preview in MyEclipse --%><link rel="stylesheet" href="css/crh_style.css" type="text/css"></c:if>

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/OrderAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.CheckComboBox.js'/>"></script>
<script type="text/javascript">

var custWithoutOrderToday = false;
var isCemActiveFlag = "CEM";
var isAggActiveFlag = "AGG";

var actions = {
	ACTION_SAVE_PREFERENCES: '<%=BestellungShipToEntryAction.ACTION_SAVE_PREFERENCES%>',
	ACTION_RESET_PREFERENCES:  '<%=BestellungShipToEntryAction.ACTION_RESET_PREFERENCES%>'
};
var withActiveCemContracts = <c:out value="${order_bestellung_BestellungForm.withActiveCemContracts}"/>;
var withActiveAggContracts = <c:out value="${order_bestellung_BestellungForm.withActiveAggContracts}"/>;

Ext.onReady(function(){

	Ext.form.ComboBox.prototype.shadow = false;

	var soldtoShiptoBestRecord = Ext.data.Record.create([
	    {name: 'compositeId', mapping: 'compositeId'},    
	    {name: 'soldtoSapId', mapping: 'soldtoSapId'},    
	    {name: 'soldtoName', mapping: 'soldtoName'},
	    {name: 'soldtoMarkedForDeletion', mapping: 'soldtoMarkedForDeletion'},    
	    {name: 'shiptoMarkedForDeletion', mapping: 'shiptoMarkedForDeletion'},
	    {name: 'shiptoSapId', mapping: 'shiptoSapId'},    
	    {name: 'shiptoName', mapping: 'shiptoName'},
	    {name: 'postalCode', mapping: 'postalCode'},
	    {name: 'city', mapping: 'city'},
	    {name: 'address', mapping: 'address'},
	    {name: 'dailyOrderStatusCd', mapping: 'dailyOrderStatusCd'},
	    {name: 'dailyOrderStatusTxt', mapping: 'dailyOrderStatusTxt'},
	    {name: 'cemValidUntilDat', mapping: 'cemValidUntilDat', type: 'date'},
	    {name: 'aggValidUntilDat', mapping: 'aggValidUntilDat', type: 'date'}
	]);

	var searchObj = {
		custWithoutOrderToday: function() {
			return custWithoutOrderToday;
		},
		queryStr: function() {
			return Ext.getCmp('searchCombo').getValue();
		},
		getCemActiveFlag: function() {
			return activeContractCheckComboBox.getCheckedValues().join().includes(isCemActiveFlag);
		},
		getAggActiveFlag: function() {
			return activeContractCheckComboBox.getCheckedValues().join().includes(isAggActiveFlag);
		},
		soldtoShiptoBestListStore:  new Ext.data.Store({
		    	proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) {
			    	OrderAjaxService.fetchSoldtoShiptoBestList(skip, count, sort, searchObj.queryStr(), custWithoutOrderToday,
						searchObj.getCemActiveFlag(), searchObj.getAggActiveFlag(), callback)
			    }, true),
		    	reader: new Ext.data.ListRangeReader({id: 'compositeId', totalProperty: 'totalSize'}, soldtoShiptoBestRecord),
			    remoteSort: true
		}),
		toggleDailyOrder: function() {
			disableButton('buttonNoDailyOrder');
			custWithoutOrderToday = !custWithoutOrderToday;
			Ext.getCmp('searchCombo').focus(); 
			Ext.getCmp('searchCombo').expand();
		    searchObj.soldtoShiptoBestListStore.load({params:{start: 0, limit: 20, sort: '', query: ''}, callback: function() {
				Ext.getDom('buttonNoDailyOrder').value = custWithoutOrderToday ? '<fmt:message key="daily.order.customers.all"/>' : '<fmt:message key="daily.order.customers.without.order"/>';
				enableButton('buttonNoDailyOrder');
		    }});
		},
		toggleActiveContract: function() {
			Ext.getCmp('searchCombo').focus();
			Ext.getCmp('searchCombo').expand();
			searchObj.soldtoShiptoBestListStore.load({params:{start: 0, limit: 20, sort: '', query: ''}});
		}
	}

	function changeActiveFilter() {
		if (searchObj.getCemActiveFlag() !== withActiveCemContracts) {
			withActiveCemContracts = !withActiveCemContracts;
			OrderAjaxService.setWithActiveCemFilterForBestellung(withActiveCemContracts, function(){
				searchObj.toggleActiveContract();
			});
		}
		if (searchObj.getAggActiveFlag() !== withActiveAggContracts ) {
			withActiveAggContracts = !withActiveAggContracts;
			OrderAjaxService.setWithActiveAggFilterForBestellung(withActiveAggContracts, function(){
				searchObj.toggleActiveContract();
			});
		}
	}
	

    // Custom rendering Template
    var resultTpl = new Ext.XTemplate(
    	'<table class="classic"><thead><tr>',
    	'<th><fmt:message key="fields.kunde.id"/></th>',
    	'<th><fmt:message key="fields.kunde.name"/></th>',
    	'<th><fmt:message key="fields.shipto.id"/></th>',
    	'<th><fmt:message key="fields.shipto.name"/></th>',
    	'<th><fmt:message key="fields.shipto.plz"/></th>',
    	'<th><fmt:message key="fields.shipto.ort"/></th>',
    	'<th><fmt:message key="fields.address"/></th>',
    	'<tpl if="custWithoutOrderToday">',
	    	'<th><fmt:message key="fields.shipto.daily.order.status"/></th>',
    	'</tpl>',
		'<tpl if="withActiveCemContracts">',
			'<th><fmt:message key="fields.cem.valid.until.date"/></th>',
		'</tpl>',
		'<tpl if="withActiveAggContracts">',
			'<th><fmt:message key="fields.agg.valid.until.date"/></th>',
		'</tpl>',
    	'</tr></thead><tbody>',
        '<tpl for=".">',
	        	'<tr class="listitem zebra{[xindex % 2]}">',
			        '<tpl if=\"soldtoMarkedForDeletion == \'0\'\">',
			        '<td>{soldtoSapId}</td>',
			        '</tpl>',
			        '<tpl if=\"soldtoMarkedForDeletion == \'1\'\">',
			        '<td><strike>{soldtoSapId}</strike></td>',
			        '</tpl>',
			        '<td>{soldtoName}</td>',
			        '<tpl if=\"shiptoMarkedForDeletion == \'0\'\">',
			        '<td>{shiptoSapId}</td>',
			        '</tpl>',
			        '<tpl if=\"shiptoMarkedForDeletion == \'1\'\">',
			        '<td><strike>{shiptoSapId}</strike></td>',
			        '</tpl>',
			        '<td>{shiptoName}</td>',
			        '<td>{postalCode}</td>',
			        '<td>{city}</td>',
			        '<td>{address}</td>',
			    	'<tpl if="custWithoutOrderToday">',
				    	'<tpl if="dailyOrderStatusCd != \'1\'">',
					        '<td><nobr><span style="color: #999999">{dailyOrderStatusTxt}</span></nobr></td>',
				        '</tpl>',
				    	'<tpl if="dailyOrderStatusCd == 1">',
					        '<td><nobr><span style="color: #000000">{dailyOrderStatusTxt}</span></nobr></td>',
				        '</tpl>',
			        '</tpl>',
					'<tpl if="withActiveCemContracts">',
						'<td>{cemValidUntilDat:date("d.m.Y")}</td>',
					'</tpl>',
					'<tpl if="withActiveAggContracts">',
						'<td>{aggValidUntilDat:date("d.m.Y")}</td>',
					'</tpl>',
		        '</tr>', 
        '</tpl>',
        '</tbody></table>'
    );
    
    var searchCombo = new Ext.form.ComboBox({
    	id: 'searchCombo',
    	msgTarget: 'side',
    	validate: function() { return false },
        store: searchObj.soldtoShiptoBestListStore,
        displayField: 'shiptoNr',
        typeAhead: false,
        loadingText: '<fmt:message key="dispokarte.texte.shiptosearching"/>',
        maxHeight: 800,
        width: Ext.fly('searchCell').getWidth() - 10,
        listWidth: Ext.isIE ? Ext.fly('searchCell').getWidth() - 13 : Ext.fly('searchCell').getWidth() - 10,
        minChars: 3,
        pageSize: 20,
        hideTrigger: true,
        tpl: resultTpl,
        applyTo: 'search',
        triggerAction: 'all',
        itemSelector: 'tr.listitem',
        onSelect: function(record){
            searchCombo.collapse();
	        	Ext.getDom('shiptoSapId').value = record.data.shiptoSapId;
	        	Ext.getDom('soldtoSapId').value = record.data.soldtoSapId;
	            Ext.getDom('ShipToForm').submit();
        }
    });
    Ext.fly('search').setTop("0px");
    
	Ext.fly('buttonNewSearch').on('click', function() {
		searchCombo.clearValue();
		searchCombo.collapse(); 
	});
    
	Ext.fly('buttonNoDailyOrder').on('click', searchObj.toggleDailyOrder);

	<c:if test="${param.referrer == 'changeDailyOrderStatus'}">
		searchObj.toggleDailyOrder();
	</c:if>
    
	searchCombo.focus();

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
			url: 'BestellungShipToEntry.do',
			params: params
		});
	});

	Ext.get('buttonResetField').on('click', function() {
		var params = { "formAction": actions.ACTION_RESET_PREFERENCES };

		Ext.Ajax.request({
			url : 'BestellungShipToEntry.do',
			params : params,
			success: function() {
				// Refresh the whole page to refresh the filters
				window.location = document.URL;
			}
		});
	});
});

</script>

<html:form action="bestellung/BestellungShipTo.do" styleId="ShipToForm">

<input type="hidden" name="shiptoSapId" id="shiptoSapId" value=""/>
<input type="hidden" name="soldtoSapId" id="soldtoSapId" value=""/>

<div class="box">
		<table style="width: 99%;">
		<colgroup>
			<col width="1%">
			<col width="79%>
			<col width="20%">
		</colgroup>
		<tr>
			<td class='label'>
				<nobr><fmt:message key="dispokarte.texte.shipto"/>:</nobr>
			</td>
		    <td id="searchCell">
		   		<input type="text" name="searchtext" value="" size="50" id="search">
		    </td>
			<td class="buttons" style="text-align: left">
				<input id="buttonNewSearch" type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.search.new"/>" />
				<input type="button" id="buttonSavePreferences" class="ViewButtonStyle" value="<fmt:message key="buttons.save"/>"/>
				<input type="button" id="buttonResetField" class="ViewButtonStyle" value="<fmt:message key="buttons.reset"/>"/>
				<br/>

				<input id="buttonNoDailyOrder" type="button" class="SmallButtonStyle" value="<fmt:message
				key="daily.order.customers.without.order"/>" style="width: 100%; margin-top: 6px; margin-bottom: 6px"/><br/>

				<input type="text" id="activeContractDummy"/>
			</td>
	    </tr>
	    </table>
</div>
<br/>

</html:form>

<%-- END $RCSfile: BestellungShipToTile.jsp,v $ --%>
