<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: AufliegerCreateTrailerPopupTile.jsp,v $ --%>


<%@ include file="../includeTLDs.jsp"%>
<%@ page import="com.crh.logon.constants.Constant" %>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/TrailerAdminCreateAjaxService.js'/>"></script>
<% if (request.getAttribute("reload")!= null){%>
<script>
	window.location.href = '<html:rewrite action="/traileradmin/TrailerAdmin.do?inputAction=reset"/>'; 
</script>
<%}

else{
%>

<logic:present name="org.apache.struts.action.ACTION_WARNINGS" scope="request">
<script>
	Ext.onReady(function(){
		document.getElementById("popupMaxMeasure").value = "<c:out value="${admin_TrailerAdminForm.inputMaxMeasure}"/>";
		document.getElementById("popupTaraMeasure").value = "<c:out value="${admin_TrailerAdminForm.inputTaraMeasure}"/>";
		document.getElementById("popupPlateNr").value = "<c:out value="${admin_TrailerAdminForm.inputPlateNr}"/>";

		initialize();
	});
</script>
</logic:present>

<script>
	onPopupReady(function() {
		var el = document.getElementById('popupPlateNr');
		el.focus();

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
		var trailerRec = Ext.data.Record.create([
		    {name: "inputSapVehicleNo", mapping: "sapVehicleNo"},
		    {name: "sapVehicleName", mapping: "sapVehicleName"}
		]);

	    var trailerStore = new Ext.data.Store({
	        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { TrailerAdminCreateAjaxService.fetchGenericTrailerList(skip, count, sort, query, callback) }, true),
			reader: new Ext.data.ListRangeReader({id:'inputSapVehicleNo', totalProperty:'totalSize'}, trailerRec)
	    });

		// --- RFID Tag Comboboxes
		sapVehicleIdCombo = new Ext.ux.TwinCombo({
	    	id: 'inputSapVehicleNo',
	        applyTo: 'inputSapVehicleNoDummy',
	        store: trailerStore,
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

<!--  <%-- Leave this commented this way please! Needed because of struts. --%> <html:form action="traileradmin/TrailerAdmin.do" styleId="admin_TrailerAdminForm"> -->
<table class="classicblue" style="border:none; width:auto">
<tr>
	<th>
		<fmt:message key="traileradmin.popup.createnew"/>
	</th>
</tr>
<tr>
	<td class="popupcontent">
	<table width="100%">
		<tr>	
			<td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.label.characteristic"/></td>
			<td class="content">
				<select onchange="characteristicTypeSelect()" style="width:100%" name="characteristic" id="characteristic" property="characteristic" formKey=""admin_TrailerAdminForm"">
					<crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_GENERIC_TRAILER_CREATE %>"><option value="<%=Constant.VEHICLE_CHARACTERISTIC_GENERIC%>"><fmt:message key="roadmasterdata.characteristic.trailer.generic"/></option></crh-common:authorize>
					<crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_PLANNING_TRAILER_CREATE %>"><option value="<%=Constant.VEHICLE_CHARACTERISTIC_PLANNING%>"><fmt:message key="roadmasterdata.characteristic.trailer.planning"/></option></crh-common:authorize>
				</select>
			</td>
		</tr>
		<tr>
			<td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.label.sapnumber"/></td>
			<td>
				<input type="text" id="inputSapVehicleNoDummy" value=""/>
				<html:hidden property="inputSapVehicleNo" styleId="inputSapVehicleNo" />
			</td>
		</tr>
		<tr>
			<td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.trailerid"/></td>
			<td class="content"  style="white-space:nowrap;border:0px;">
				<input type="text" name="inputTrailerName" id="inputTrailerName" value="<c:out value="${admin_TrailerAdminForm.inputTrailerName}"/>" maxlength="30"/>
			</td>
		</tr>
		<tr>
			<td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.plateno"/></td>
			<td class="content"  style="white-space:nowrap;border:0px;">
				<input type="text" name="popupPlateNr" id="popupPlateNr" maxlength="20"/>
			</td>
		</tr>
		<tr>
			<td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.maxmeasure"/></td>
			<td class="content"><input type="text" name="popupMaxMeasure" id="popupMaxMeasure" maxlength='13'/></td>
		</tr>
		<tr>
			<td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.tarameasure"/></td>
			<td class="content"><input type="text" name="popupTaraMeasure" id="popupTaraMeasure" maxlength='13'/></td>
		</tr>
		<tr>
			<td colspan="2" class="buttons right">
				<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.cancel"/>" onclick="hidePopup()"/>
				<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.save"/>" onclick="saveNewTrailer()"/>
			</td>
		</tr>
	</table>
	</td>
</table>
<!--</html:form>-->
<%} %>