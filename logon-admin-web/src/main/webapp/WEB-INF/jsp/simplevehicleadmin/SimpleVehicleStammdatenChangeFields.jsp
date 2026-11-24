<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: SimpleVehicleStammdatenChangeFields.jsp,v $ --%>

<%@page import="com.crh.logon.admin.web.forms.simplevehicleadmin.SimpleVehicleAdminForm"%>
<%@ page import="com.crh.logon.admin.web.utils.SimpleVehicleAdminChangeLogic" %>
<%@page import="com.crh.logon.constants.Constant"%>
<%@ taglib uri="http://com.crh.logon/tags/admin-taglib.tld" prefix="crh-admin" %>
<%@ include file="../includeTLDs.jsp" %>
<%-- See details in main tile: SimpleVehicleStammdatenTile --%>
<script type="text/javascript">
function changeFields(){
	var type = document.getElementById("inputSimpleVehicleTypeCd");
	if (type){
	type.disabled=false;
	<crh-admin:disableSimpleVehicleChange formKey="admin_SimpleVehicleAdminForm" functionality="<%=SimpleVehicleAdminChangeLogic.FUNCTIONALITY_CHANGE_VEHICLE_TYPE%>">type.disabled=true</crh-admin:disableSimpleVehicleChange>
	document.getElementById("inputSimpleVehicleTypeCd").innerHTML="";	
		<c:forEach var="simpleVehicleTypeEntity" varStatus="st" items="${admin_SimpleVehicleAdminForm.simpleVehicleTypeMap}">
			document.getElementById("inputSimpleVehicleTypeCd").options[<c:out value="${st.index}"/>] = new Option("<c:out value="${simpleVehicleTypeEntity.value.txt}"/>", "<c:out value="${simpleVehicleTypeEntity.key}"/>"); 
		</c:forEach>
	type.value="<c:out value="${admin_SimpleVehicleAdminForm.inputSimpleVehicleTypeCd}"/>";
	if (!type.value){
		type.value=type.options[0].value;
	}
	}
	var volume = document.getElementById("inputVolumeM3");
	if (volume){
	volume.disabled=false;
	<crh-admin:disableSimpleVehicleChange formKey="admin_SimpleVehicleAdminForm" functionality="<%=SimpleVehicleAdminChangeLogic.FUNCTIONALITY_CHANGE_VOLUME%>">volume.disabled=true</crh-admin:disableSimpleVehicleChange>
	volume.value="<c:out value="${admin_SimpleVehicleAdminForm.inputVolumeM3}"/>";
	}

	var selectChamber = document.getElementById("inputNrChambers");
	if (selectChamber){
		selectChamber.disabled=false;
	<crh-admin:disableSimpleVehicleChange formKey="admin_SimpleVehicleAdminForm" functionality="<%=SimpleVehicleAdminChangeLogic.FUNCTIONALITY_SET_NR_CHAMBERS%>">selectChamber.disabled=true;</crh-admin:disableSimpleVehicleChange>
	selectChamber.value="<c:out value="${admin_SimpleVehicleAdminForm.inputNrChambers}"/>";
	}
	var selectUnitCount = document.getElementById("inputUnitCount");
	if (selectUnitCount){
		selectUnitCount.disabled=false;
	<crh-admin:disableSimpleVehicleChange formKey="admin_SimpleVehicleAdminForm" functionality="<%=SimpleVehicleAdminChangeLogic.FUNCTIONALITY_SET_UNIT_COUNT%>">selectUnitCount.disabled=true;</crh-admin:disableSimpleVehicleChange>
	selectUnitCount.value="<c:out value="${admin_SimpleVehicleAdminForm.inputUnitCount}"/>";
	}
}
changeFields();
</script>


