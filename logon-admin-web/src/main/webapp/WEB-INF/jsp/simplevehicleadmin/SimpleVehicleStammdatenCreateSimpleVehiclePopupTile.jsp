<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: SimpleVehicleStammdatenCreateSimpleVehiclePopupTile.jsp,v $ --%>


<%@ include file="../includeTLDs.jsp"%>
<%@ page import="com.crh.logon.constants.Constant" %>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<% if (request.getAttribute("reload")!= null){%>
<script>
	hidePopup();
	resetSimpleVehicle(<c:out value="${admin_SimpleVehicleAdminForm.simpleVehicleId}"/>);
</script>
<%}

else{
%>

<logic:present name="org.apache.struts.action.ACTION_WARNINGS" scope="request">
<script>
	Ext.onReady(function(){
<%-- TODO 2012-Nov-18/awe: where is this used? we start with empty values, its a new vehicle, not a copy --%> 
		document.getElementById("popupTaraMeasure").value = "<c:out value="${admin_SimpleVehicleAdminForm.inputTaraMeasure}"/>";
		document.getElementById("popupRegNo").value = "<c:out value="${admin_SimpleVehicleAdminForm.inputVehicleRegNo}"/>";
		document.getElementById("popupSapVehicleNo").value = "<c:out value="${admin_SimpleVehicleAdminForm.inputSapVehicleNo}"/>";
		document.getElementById("popupVehicleName").value = "<c:out value="${admin_SimpleVehicleAdminForm.inputVehicleName}"/>";
		initialize();
	});
</script>
</logic:present>
<script type="text/javascript">
	onPopupReady(function() {
		var el = document.getElementById('popupRegNo');
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

	function initialize() {
		// --- Data record and DataStore definitions ---
	}
	
</script>

<!--  <%-- Leave this commented this way please! Needed because of struts. --%> <html:form action="simplevehicleadmin/SimpleVehicleAdmin.do" styleId="admin_SimpleVehicleAdminForm"> -->
<table class="classicblue" style="border:none; width:auto">
<tr>
	<th>
		<fmt:message key="simplevehicleadmin.popup.createnew"/>
	</th>
</tr>
<tr>
	<td class="popupcontent">
	<table width="100%"> 
        <tr>
  			<td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.simplevehicletype"/></td>
        	<td colspan="2" class="content" style="border:0px;">   
                <div id="simpleVehicletypeselect">                         
                    <select style="width:100%" onchange="" name="popupSimpleVehicleTypeCd" id="popupSimpleVehicleTypeCd">        
						<c:forEach var="simpleVehicleTypeEntity" varStatus="st" items="${admin_SimpleVehicleAdminForm.simpleVehicleTypeMap}">
							<option <c:if test="${simpleVehicleTypeEntity.key eq admin_SimpleVehicleAdminForm.inputSimpleVehicleTypeCd}" >selected</c:if> value="<c:out value="${simpleVehicleTypeEntity.key}"/>"><c:out value="${simpleVehicleTypeEntity.value.txt}"/></option>
						</c:forEach>
					</select>
				</div>
             </td>
         </tr>
		<tr>
			<td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.label.sapnumber"/></td>
			<td class="content"  style="white-space:nowrap;border:0px;">
				<input type="text" id="popupSapVehicleNo" value=""/>
			</td>
		</tr>
		<tr>
			<td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.simplevehicleid"/></td>
			<td class="content"  style="white-space:nowrap;border:0px;">
				<input type="text" style="width:100%" name="popupVehicleName" id="popupVehicleName" value="<c:out value="${admin_SimpleVehicleAdminForm.inputVehicleName}"/>" maxlength="30"/>
			</td>
		</tr>
		<tr>
			<td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.vehicleregno"/></td>
			<td class="content"  style="white-space:nowrap;border:0px;">
				<input type="text" style="width:100%" name="popupRegNo" id="popupRegNo" maxlength="20"/>
			</td>
		</tr>
		<tr>
			<td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.tarameasure"/></td>
			<td class="content">
				<input type="text" style="width:100%" name="popupTaraMeasure" id="popupTaraMeasure" maxlength='13'/>
			</td>
		</tr>     
        <tr>
            <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.nrchambers"/></td>
            <td class="content" style="white-space:nowrap;border:0px;">
				<select style="width:100%" name="popupNrChambers" id="popupNrChambers">
					<c:forEach var="nrChambers" items="${admin_SimpleVehicleAdminForm.validNrChambers}">
						<option <c:if test="${nrChambers eq admin_SimpleVehicleAdminForm.inputNrChambers}" >selected</c:if>  value="<c:out value="${nrChambers}"/>"><c:out value="${nrChambers}"/></option>
					</c:forEach>	
				</select>
			</td>	
        </tr>                          
		<tr>
			<td colspan="2" class="buttons right">
				<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.cancel"/>" onclick="hidePopup()"/>
				<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.save"/>" onclick="saveNewSimpleVehicle()"/>
			</td>
		</tr>
	</table>
	</td>
</table>
<!--</html:form>-->
<%} %>