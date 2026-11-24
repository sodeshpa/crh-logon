<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: OrderRFIDPopupTile.jsp,v $ --%>
<%@ page session="true"%>

<%@ page import="com.crh.logon.order.web.forms.OrderRFIDPopupForm" %>
<%@ page import="com.crh.logon.persistence.entities.common.RfidobjectEntity"%>
<%@ include file="includeTLDs.jsp"%>

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/OrderAjaxService.js'/>"></script>

<script type="text/javascript">
var rfidTagCombo;

onPopupReady(function() {
	Ext.form.ComboBox.prototype.shadow = false;
	Ext.form.ComboBox.prototype.emptyText = "Select...";
	Ext.form.ComboBox.prototype.minChars = 1;
	Ext.form.ComboBox.prototype.allowBlank = true;
	Ext.form.ComboBox.prototype.forceSelection = true;
	Ext.menu.Menu.prototype.shadow = false;
	
	var rfidRec = Ext.data.Record.create([
		{name: "rfidtagId", mapping: "rfidtagId"},
		{name: "name", mapping: "name"}
	]);

	var rfidStoreLoader = function(skip, count, sort, query, callback) {
		if (count) {
			OrderAjaxService.fetchRfidList(skip, count, query, callback);
		}
	}
	var rfidTagStore = new Ext.data.Store({
		proxy: new Ext.data.DWRProxyForPager(rfidStoreLoader, true),
		reader: new Ext.data.ListRangeReader({id:'rfidtagId', totalProperty:'totalSize'}, rfidRec)
	});

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

function saveRfid() {
	document.getElementById('rfidTagId').value = rfidTagCombo.getValue();
	actionName = {
		"save" :"save"
	};
	addFormElements(actionName, "<%=OrderRFIDPopupForm.SESSION_RFIDFORM_NAME%>");

	var divToReload = Ext.get("<%=OrderRFIDPopupForm.NAME_OF_HTML_DIV_TO_RELOAD%>");
	divToReload.load({
		url: '<%=request.getContextPath() %>/OrderRFIDPopup.do',
		text: '<fmt:message key="shippingpoint.text.exjsloading"/>',
		scripts: true,
		params: actionName,
		callback: function() {
			var rfidStore = rfidTagCombo.store;
			rfidTagCombo.store.reload();
		}
	});
}

function closePopup() {
	OrderAjaxService.closeRfidPopup(hidePopup);
}

function removeRFID(rfTagId, rfname) {
	var divToReload = Ext.get('rfids');
	divToReload.load({
		url :'<%=request.getContextPath() %>/OrderRFIDPopup.do',
		params : {
			'removeRfid' :'',
			'rfidTagId':rfTagId
		},
		text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
		scripts : true,
		callback: function() {
			var rfidStore = rfidTagCombo.store;
			rfidTagCombo.store.reload();
		}
	});
}

</script>

<html:form action="OrderRFIDPopup" styleId="<%=OrderRFIDPopupForm.SESSION_RFIDFORM_NAME%>">
<table class="classicblue" style="border:none; width:auto">
	<tr>
		<th colspan="2">
			<fmt:message key="shippingpoint.popup.title"/>
		</th>
	</tr>
	<tr>
		<td class="popupcontent">
			<div class="subformlist" id="<%=OrderRFIDPopupForm.NAME_OF_HTML_DIV_TO_RELOAD%>" style="clear:both;font-size:12px; margin:0px 4px 0px 4px;">
				<jsp:include page="OrderRfidsTile.jsp"/>    
			</div>
			<table width="100%">
				<tr>
					<td class="label right" >
						<fmt:message key="shippingpoint.popup.rfid" />:
					</td>
					<td>
						<input type="text" id="rfidTagIdDummy" value="" />
						<html:hidden property="rfidTagId" styleId="rfidTagId" />
					</td>
				</tr>
				<tr>
					<td colspan="2" class="buttons right">
						<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.back"/>" onclick="closePopup()"/>
						<input id="rfIdSaveButton" disabled type="button" class="ViewButtonStyle" onclick="saveRfid()" value="<fmt:message key="buttons.hinzufuegen"/>" />
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</html:form>
