<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: LKWStammdatenChangeFields.jsp,v $ --%>

<%@page import="com.crh.logon.admin.web.forms.truckadmin.TruckAdminForm"%>
<%@ page import="com.crh.logon.admin.web.utils.TruckAdminChangeLogic" %>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page import="com.crh.logon.persistence.entities.refdata.TruckTransportCatTxEntity"%>
<%@ taglib uri="http://com.crh.logon/tags/admin-taglib.tld" prefix="crh-admin" %>
<%@ include file="../includeTLDs.jsp" %>
<%-- See details in main tile: LKWStammdatenTile --%>
<script type="text/javascript">
function changeFields(){

	var tkat =  document.getElementById("inputTkategorie"); 
	if (tkat){
	<crh-admin:disableTruckChange formKey="admin_TruckAdminForm" functionality="<%=TruckAdminChangeLogic.FUNCTIONALITY_CHANGE_KATEGORIE%>">tkat.disabled=true</crh-admin:disableTruckChange>
	tkat.value="<c:out value="${admin_TruckAdminForm.inputTkategorie}"/>";
	}
	
	var type = document.getElementById("inputLkwType");
	if (type){
	type.disabled=false;
	<crh-admin:disableTruckChange formKey="admin_TruckAdminForm" functionality="<%=TruckAdminChangeLogic.FUNCTIONALITY_CHANGE_TRUCK_TYPE%>">type.disabled=true</crh-admin:disableTruckChange>
	document.getElementById("inputLkwType").innerHTML="";	
		<c:forEach var="truckTypeEntity" varStatus="st" items="${admin_TruckAdminForm.truckTypeMap}">
			document.getElementById("inputLkwType").options[<c:out value="${st.index}"/>] = new Option("<c:out value="${truckTypeEntity.value.txt}"/>", "<c:out value="${truckTypeEntity.key}"/>"); 
		</c:forEach>
	type.value="<c:out value="${admin_TruckAdminForm.inputLkwType}"/>";
	if (!type.value){
		type.value=type.options[0].value;
	}
	}
	
	/* 
	 * LOGON-4999: Truck Home Plant
	 * Loading the shippingpoints related to the selected plant.
	 */
	var shippingpoint = document.getElementById("selectedShippingpoint");
	if (shippingpoint) {
		shippingpoint.innerHTML = "";
		shippingpoint.options[0] = new Option("-", "-1");
		<c:forEach var="shippingpoint" varStatus="st" items="${admin_TruckAdminForm.shippingpoints}">
			shippingpoint.options[<c:out value="${st.index}"/>] = new Option("<c:out value="${shippingpoint.shippingpointName}"/>", "<c:out value="${shippingpoint.shippingpointId}"/>");
		</c:forEach>
		shippingpoint.value = "<c:out value="${admin_TruckAdminForm.selectedShippingpoint}"/>";	
		if (!shippingpoint.value){
			shippingpoint.value = shippingpoint.options[0].value;
		}
	}

	var dustFilter = document.getElementById("inputDustfilter");
	if (dustFilter){
	dustFilter.disabled=false;
	<crh-admin:disableTruckChange formKey="admin_TruckAdminForm" functionality="<%=TruckAdminChangeLogic.FUNCTIONALITY_CHANGE_DUSTFILTER%>">dustFilter.disabled=true</crh-admin:disableTruckChange>
	dustFilter.value="<c:out value="${admin_TruckAdminForm.inputDustfilter}"/>";
	}

	var volumen = document.getElementById("inputVolumeM3");
	if (volumen){
	volumen.disabled=false;
	<crh-admin:disableTruckChange formKey="admin_TruckAdminForm" functionality="<%=TruckAdminChangeLogic.FUNCTIONALITY_CHANGE_VOLUMEN%>">volumen.disabled=true</crh-admin:disableTruckChange>
	volumen.value="<c:out value="${admin_TruckAdminForm.inputVolumeM3}"/>";
	}
	
	var trailer = document.getElementById("inputTrailerSelect");
	if (trailer){
	trailer.disabled=false;
	<crh-admin:disableTruckChange formKey="admin_TruckAdminForm" functionality="<%=TruckAdminChangeLogic.FUNCTIONALITY_CHANGE_TRAILER%>">trailer.disabled=true;</crh-admin:disableTruckChange>
	trailer.value="<c:out value="${admin_TruckAdminForm.inputTrailerSelect}"/>";
	}

	var container = document.getElementById("inputContainerSelect");
	if (container){
	container.disabled=false;
	<crh-admin:disableTruckChange formKey="admin_TruckAdminForm" functionality="<%=TruckAdminChangeLogic.FUNCTIONALITY_CHANGE_CONTAINER%>">container.disabled=true;</crh-admin:disableTruckChange>
	container.value="<c:out value="${admin_TruckAdminForm.inputContainerSelect}"/>";
	}

	var selectChamber = document.getElementById("inputNrChambers");
	if (selectChamber){
		selectChamber.disabled=false;
	<crh-admin:disableTruckChange formKey="admin_TruckAdminForm" functionality="<%=TruckAdminChangeLogic.FUNCTIONALITY_SET_CHAMBERS%>">selectChamber.disabled=true;</crh-admin:disableTruckChange>
	selectChamber.value="<c:out value="${admin_TruckAdminForm.inputNrChambers}"/>";
	}

	var chamber1T = document.getElementById("inputMaxNetWeightChamber1T");
	if (chamber1T){
	chamber1T.disabled=false;
	<crh-admin:disableTruckChange formKey="admin_TruckAdminForm" functionality="<%=TruckAdminChangeLogic.FUNCTIONALITY_SET_CHAMBER_1_WEIGHT%>">chamber1T.disabled=true;</crh-admin:disableTruckChange>
	chamber1T.value="<c:out value="${admin_TruckAdminForm.inputMaxNetWeightChamber1T}"/>";
	}

	var chamber2T = document.getElementById("inputMaxNetWeightChamber2T");
	if (chamber2T){
	chamber2T.disabled=false;
	<crh-admin:disableTruckChange formKey="admin_TruckAdminForm" functionality="<%=TruckAdminChangeLogic.FUNCTIONALITY_SET_CHAMBER_2_WEIGHT%>">chamber2T.disabled=true;</crh-admin:disableTruckChange>
	chamber2T.value="<c:out value="${admin_TruckAdminForm.inputMaxNetWeightChamber2T}"/>";
	}
}
changeFields();
</script>


