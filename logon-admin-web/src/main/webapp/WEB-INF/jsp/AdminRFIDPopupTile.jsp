<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: AdminRFIDPopupTile.jsp,v $ --%>
<%@ page session="true"%>

<%@ page import="com.crh.logon.admin.web.forms.AdminRFIDPopupForm" %>
<%@ include file="includeTLDs.jsp"%>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/RFIDAdminAjaxService.js'/>"></script>

<script type="text/javascript">
	onPopupReady(function() {

		// --- Common properties for all filter-comboboxes ---
		Ext.form.ComboBox.prototype.shadow = false;
		Ext.form.ComboBox.prototype.emptyText = "Select...";
		Ext.form.ComboBox.prototype.minChars = 1;
		Ext.form.ComboBox.prototype.allowBlank = true;
		Ext.form.ComboBox.prototype.forceSelection = true;
		Ext.menu.Menu.prototype.shadow = false;
		
		// --- Data record and DataStore definitions ---
		var rfidRec = Ext.data.Record.create([
			{name: "rfidtagId", mapping: "rfidtagId"},
			{name: "name", mapping: "name"}
		]);

		var rfidStoreLoader = function(skip, count, sort, query, callback) { 
			if (count) {
				RFIDAdminAjaxService.fetchRfidList(skip, count, query, callback);
			}
		}
		
		var rfidTagStore = new Ext.data.Store({
			proxy: new Ext.data.DWRProxyForPager(rfidStoreLoader, true),
			reader: new Ext.data.ListRangeReader({id:'rfidtagId', totalProperty:'totalSize'}, rfidRec)
		});

		// --- RFID Tag Comboboxes
		rfidTagCombo = new Ext.ux.TwinCombo({
			id: 'rfidtagId',
			applyTo: 'rfidTagIdDummy',
			store: rfidTagStore,
			displayField: 'name',
			valueField: 'rfidtagId',
			pageSize: 20,
			width: 180,
			listWidth: 250,
			typeAhead: false,
			triggerAction: 'all' ,
			selectOnFocus: true
		});

		rfidTagCombo.on('select', function(combo, record) {
			var buttSave = document.getElementById("rfIdSaveButton");
			buttSave.disabled = false;
		});

		rfidTagCombo.focus();
	});

	function hide(){ 
		hidePopupCallFunction();
	}
	
	function saveRfid() {
		document.getElementById('rfidTagId').value = rfidTagCombo.getValue();
		actionName = {
			"save" :"save"
		};
		addFormElements(actionName, "<%=AdminRFIDPopupForm.SESSION_RFIDFORM_NAME%>");
		var divToReload = Ext.get("<%=AdminRFIDPopupForm.NAME_OF_HTML_DIV_TO_RELOAD%>");
		divToReload.load({
			url: '<%=request.getContextPath() %>/AdminRFIDPopup.do',
			text: '<fmt:message key="shippingpoint.text.exjsloading"/>',
			scripts: true,
			params: actionName,
			callback: hidePopup()
		});
	}
</script>

<html:form action="AdminRFIDPopup" styleId="<%=AdminRFIDPopupForm.SESSION_RFIDFORM_NAME%>">
<table class="classicblue" style="border:none; width:auto">
	<tr>
		<th colspan="2">
			<fmt:message key="shippingpoint.popup.title"/>
		</th>
	</tr>
	<tr>
		<td class="popupcontent">
			<table width="100%">
				<tr>
					<td class="label right"><fmt:message
						key="shippingpoint.popup.rfid" />:</td>
					<td>
					<input type="text" id="rfidTagIdDummy" value="" />
					<html:hidden property="rfidTagId" styleId="rfidTagId" />
					</td>
				</tr>
				<tr>
					<td colspan="2" class="buttons right">
						<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.cancel"/>" onclick="hide()"/>
						<input id="rfIdSaveButton" disabled type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.hinzufuegen"/>" onclick="saveRfid()"/>
		
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

</html:form>
