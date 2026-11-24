<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: LKWStammdatenCreateTruckPopupTile.jsp,v $ --%>


<%@ include file="../includeTLDs.jsp"%>
<%@ page import="com.crh.logon.constants.Constant" %>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<script type="text/javascript" src="<crh-common:url value='../js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/TruckAdminCreateAjaxService.js'/>"></script>
<% if (request.getAttribute("reload")!= null){%>
<script>
	hidePopup();
	resetTruck(<c:out value="${admin_TruckAdminForm.truckId}"/>);
</script>
<%}

else{
%>

<logic:present name="org.apache.struts.action.ACTION_WARNINGS" scope="request">
<script>
	Ext.onReady(function(){
		document.getElementById("popupTaraMeasure").value = "<c:out value="${admin_TruckAdminForm.inputTaraMeasure}"/>";
		document.getElementById("popupPlateNr").value = "<c:out value="${admin_TruckAdminForm.inputPlateNr}"/>";

		initialize();
	});
</script>
</logic:present>
<script type="text/javascript">
	onPopupReady(function() {
		var el = document.getElementById('popupPlateNr');
		el.focus();

		// --- Common properties for all filter-comboboxes ---
		Ext.form.ComboBox.prototype.shadow = false;
		Ext.form.ComboBox.prototype.emptyText = "Select...";
		Ext.form.ComboBox.prototype.minChars = 1;
		Ext.form.ComboBox.prototype.allowBlank = true;
		Ext.form.ComboBox.prototype.forceSelection = true;
		Ext.menu.Menu.prototype.shadow = false;

	    initialize();
	});

	function characteristicTypeSelect() {
		var characteristicValue = document.getElementById('characteristic').value;

		if (characteristicValue == <%=Constant.VEHICLE_CHARACTERISTIC_GENERIC%>) {
			sapVehicleIdCombo.enable();
		}
		else {
			sapVehicleIdCombo.clearValue();
			sapVehicleIdCombo.disable();			
		}	
	}

	function initialize() {
		// --- Data record and DataStore definitions ---
		var truckRec = Ext.data.Record.create([
		    {name: "inputSapVehicleNo", mapping: "sapVehicleNo"},
		    {name: "sapVehicleName", mapping: "sapVehicleName"}
		]);

	    var truckStore = new Ext.data.Store({
	        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { TruckAdminCreateAjaxService.fetchGenericTruckList(skip, count, sort, query, callback) }, true),
			reader: new Ext.data.ListRangeReader({id:'inputSapVehicleNo', totalProperty:'totalSize'}, truckRec)
	    });
	    
		// --- RFID Tag Comboboxes
		sapVehicleIdCombo = new Ext.ux.TwinCombo({
	    	id: 'inputSapVehicleNo',
	        applyTo: 'inputSapVehicleNoDummy',
	        store: truckStore,
	        displayField: 'inputSapVehicleNo',
	        valueField: 'inputSapVehicleNo',
	        pageSize: 20,
	        width: 180,
	        listWidth: 250,
	        typeAhead: false,
	        triggerAction: 'all' ,
	        selectOnFocus: true
		});

		characteristicTypeSelect();
	}
	
</script>

<!--  <%-- Leave this commented this way please! Needed because of struts. --%> <html:form action="truckadmin/TruckAdmin.do" styleId="admin_TruckAdminForm"> -->
<table class="classicblue" style="border:none; width:auto">
<tr>
	<th>
		<fmt:message key="truckadmin.popup.createnew"/>
	</th>
</tr>
<tr>
	<td class="popupcontent">
	<table width="100%"> 
		<tr>
			<td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.label.characteristic"/></td>
			<td class="content">
				<select onchange="characteristicTypeSelect()" style="width:100%" name="characteristic" id="characteristic" property="characteristic" formKey="admin_TruckAdminForm">
					<crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_GENERIC_TRUCK_CREATE %>"><option value="<%=Constant.VEHICLE_CHARACTERISTIC_GENERIC%>"><fmt:message key="roadmasterdata.characteristic.truck.generic"/></option></crh-common:authorize>
					<crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_PLANNING_TRUCK_CREATE %>"><option value="<%=Constant.VEHICLE_CHARACTERISTIC_PLANNING%>"><fmt:message key="roadmasterdata.characteristic.truck.planning"/></option></crh-common:authorize>
				</select>
			<td>
		</tr>
		<tr>
			<td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.label.sapnumber"/></td>
			<td>
				<input type="text" id="inputSapVehicleNoDummy" value=""/>
				<html:hidden property="inputSapVehicleNo" styleId="inputSapVehicleNo" />
			</td>
		</tr>
		<tr>
			<td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.truckid"/></td>
			<td class="content"  style="white-space:nowrap;border:0px;">
				<input type="text" style="width:100%" name="inputTruckName" id="inputTruckName" value="<c:out value="${admin_TruckAdminForm.inputTruckName}"/>" maxlength="30"/>
			</td>
		</tr>
		<tr>
			<td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.plateno"/></td>
			<td class="content"  style="white-space:nowrap;border:0px;">
				<input type="text" style="width:100%" name="popupPlateNr" id="popupPlateNr" maxlength="20"/>
			</td>
		</tr>
		<tr>
			<td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.tarameasure"/></td>
			<td class="content">
				<input type="text" style="width:100%" name="popupTaraMeasure" id="popupTaraMeasure" maxlength='13'/>
			</td>
		</tr>                              
		<tr>
			<td colspan="2" class="buttons right">
				<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.cancel"/>" onclick="hidePopup()"/>
				<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.save"/>" onclick="saveNewTruck()"/>
			</td>
		</tr>
	</table>
	</td>
</table>
<!--</html:form>-->
<%} %>