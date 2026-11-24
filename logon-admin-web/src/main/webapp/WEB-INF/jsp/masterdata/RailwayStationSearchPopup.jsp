<%@ page session="true"%>

<%@page import="com.crh.logon.admin.web.forms.RailwayStationPopupForm"%>
<%@ include file="../includeTLDs.jsp"%>

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/ShippingPointAjaxService.js'/>"></script>

<script type="text/javascript">

//	Ext.onReady(function(){
	var rfidTagCombo;

	onPopupReady(function() {
	
		// --- Common properties for all filter-comboboxes ---
		Ext.form.ComboBox.prototype.shadow = false;
		Ext.form.ComboBox.prototype.emptyText = "Select...";
		Ext.form.ComboBox.prototype.minChars = 1;
		Ext.form.ComboBox.prototype.allowBlank = true;
		Ext.form.ComboBox.prototype.forceSelection = true;
		Ext.menu.Menu.prototype.shadow = false;
		
		// --- Data record and DataStore definitions ---
		var railwayStationRec = Ext.data.Record.create([
		    {name: "railwayStationId"},
		    {name: "railwayStationName"},
		    {name: "railwayStationNo"},
		    {name: "railwayStationCountryCode", mapping: "uicCountry.uicCountryAlphaCd"}
		]);

	    var railwayStationStore = new Ext.data.Store({
	        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { ShippingPointAjaxService.fetchRailwayStationList(skip, count, sort, query, callback) }, true),
			reader: new Ext.data.ListRangeReader({id:'railwayStationId', totalProperty:'totalSize'}, railwayStationRec)
	    });

		stationTagCombo = new Ext.ux.TwinCombo({
	    	id: 'railwayStationId',
	        applyTo: 'railwayStationDummy',
	        store: railwayStationStore,
	        displayField: 'railwayStationName',
	        tpl: '<tpl for="."><div class="x-combo-list-item">{railwayStationName} {railwayStationNo} {railwayStationCountryCode}</div></tpl>',
	        valueField: 'railwayStationId',
	        pageSize: 20,
	        width: 180,
	        listWidth: 250,
	        typeAhead: false,
	        triggerAction: 'all' ,
	        selectOnFocus: true
		});

		stationTagCombo.on('select', function(combo, record) {
			var buttSave = document.getElementById("stationSaveButton");
	    	buttSave.disabled = false;
		});

		stationTagCombo.focus();
	});


	function closePopup(){
		hidePopup();
	}
	
	function saveStation() {
		document.getElementById('railwayStationId').value = stationTagCombo.getValue();
		
		a = {
			"save" :"save"
		};
		addFormElements(a, "RailwayStationPopupForm");
		var diva = Ext.get('railwayStationDiv');
		diva.load({
		url: 'RailwayStationPopup.do', 
		params: a,
		text: texts.exjsloading,
		scripts: true
	});
	}
	
	function removeStation() {
		document.getElementById('railwayStationId').value = stationTagCombo.getValue();
		
		a = {
			"remove" :"remove"
		};
		addFormElements(a, "RailwayStationPopupForm");
		var diva = Ext.get('railwayStationDiv');
		diva.load({
		url: 'RailwayStationPopup.do', 
		params: a,
		text: texts.exjsloading,
		scripts: true
	});
	}
	
</script>


<html:form action="RailwayStationPopup.do" styleId="admin_RailwayStationPopupForm">
<table class="classicblue" style="border:none; width:auto">
<tr>
	<th colspan="2">
		<fmt:message key="railwaystation.popup.title"/>
	</th>
</tr>
<tr>
	<td class="popupcontent">
		<%
			Object obj = null;//request.getSession().getAttribute( RailwayStationPopupForm.SESSION_RAILWAYSTATION_NAME );
			pageContext.setAttribute("obj", obj);
		%>

		<table class="classic">
				<c:set var="rsList" value="${obj.rsList }"/>
				<c:forEach var="station" varStatus="status" items="${rsList}">
					<tr class="zebra<c:out value="${status.count % 2}"/>">
						<td style="width: 600%; overflow: hidden;"><c:out
							value="${station.railwayStationName}" /></td>
						<td style="width: 40%; overflow: hidden;"><c:out
							value="${station.railwayStationNo}" /></td>
					</tr>
			</c:forEach>
		</table>
	

			<table width="100%">
				<tr>
					<td>
						<input type="text" id="railwayStationDummy" value="" />
						<html:hidden property="railwayStationId" styleId="railwayStationId" />
					</td>
				</tr>
				<tr>
			<td colspan="2" class="buttons right">
				<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.back"/>" onclick="closePopup()"/>
				<input id="stationRemoveButton" type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.delete"/>" onclick="removeStation()"/>
				<input id="stationSaveButton" disabled type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.hinzufuegen"/>" onclick="saveStation()"/>
			</td>
		</tr>
	</table>
	</td>
</table>

</html:form>
