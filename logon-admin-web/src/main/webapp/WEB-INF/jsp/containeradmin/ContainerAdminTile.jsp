<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: ContainerAdminTile.jsp,v $ --%>
<%@ include file="../includeTLDs.jsp"%>
<%@page
	import="com.crh.logon.admin.web.actions.containeradmin.ContainerAdminAction"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@page
	import="com.crh.logon.persistence.entities.common.ContainerEntity"%>
<%@ page session="true"%>

<c:if test="false">
	<%-- for preview in MyEclipse --%>
	<link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>
<script type="text/javascript"
	src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript"
	src="<crh-common:url value='/dwr/interface/AdminRFIDPopupAjaxService.js'/>"></script>
<script type="text/javascript"><!--
var forwards = {
		SELECT_CARRIER: '<%=ContainerAdminAction.FORWARD_SELECT_CARRIER%>',
		SAVE_NEW: '<%=ContainerAdminAction.FORWARD_SAVE_NEW%>',
		SAVE: '<%=ContainerAdminAction.FORWARD_SAVE%>',
		RESET: '<%=ContainerAdminAction.FORWARD_RESET%>',
		CREATE_CONTAINER: '<%=ContainerAdminAction.FORWARD_CREATE_CONTAINER%>',
		SHOW_ONLY_ACTIVE_CONTAINERS: '<%=ContainerAdminAction.FORWARD_SHOW_ONLY_ACTIVE_CONTAINERS%>'
	};

	var texts = {
		buttonsSave :'<fmt:message key="buttons.save"/>',
		errorsCommentLong :'<fmt:message key="roadmasterdata.errors.commentlong"/>',
		exjsloading	: '<fmt:message key="contadmin.text.loading"/>'
	};


	function selectCarrier(){
		document.getElementById("inputAction").value=forwards.SELECT_CARRIER;
	    Ext.get('admin_ContainerAdminForm').dom.submit();
	}

	function selectContainer(id){
		document.getElementById("containerId").value = id;
		document.getElementById("inputAction").value = forwards.RESET;
	    Ext.get('admin_ContainerAdminForm').dom.submit();
	}

	function updateContNoShowRFIDPopup(){
		var inputContainerNumber = document.getElementById("containerNoId").value;
		AdminRFIDPopupAjaxService.selectContainerByNumber(inputContainerNumber, function() {
			showPopup('../AdminRFIDPopup.do');
		});
	}

	function resetContainer(id){		
		document.getElementById("inputAction").value=forwards.RESET;
		document.getElementById("containerId").value = id;
		Ext.get('admin_ContainerAdminForm').dom.submit();
	}
	
	function saveContainer(){
		var length = <%=ContainerEntity.DBSIZE_CONTAINER_COMMENT + ""%>;
		var bool = checkCommentLength("inputCommentId", '<fmt:message key="confirm.trim.textarealength"/>','<fmt:message key="contadmin.label.comment"/>', length);
		if (!bool){
			return false;
		}

		document.getElementById("inputAction").value=forwards.SAVE;
		Ext.get('admin_ContainerAdminForm').dom.submit();
	}

	function addNewContainer(){
		showPopup('ContainerAdmin.do?inputAction='+ forwards.CREATE_CONTAINER);
	}

	function filterContainers() { 
		document.getElementById("inputAction").value=forwards.SHOW_ONLY_ACTIVE_CONTAINERS;
		Ext.get('admin_ContainerAdminForm').dom.submit();
	} 
	
	function saveNewContainer(){

		var inputContNameIdPopup = document.getElementById("inputContNameIdPopup").value;
		var inputContNoIdPopup = document.getElementById("inputContNoIdPopup").value;
		var inputContTrspCatIdPopup = document.getElementById("inputContTrspCatIdPopup").value;
		var inputContTypeIdPopup = document.getElementById("inputContTypeIdPopup").value;
	
		if (inputContNameIdPopup == "") {
			Logon.message.warning('<fmt:message key="contadmin.errors.required.name"/>');
			return false;
		}
		if (inputContNoIdPopup == "") {
			Logon.message.warning('<fmt:message key="contadmin.errors.required.no"/>');
			return false;
		}
		if (inputContTrspCatIdPopup == <%=Constant.INVALID_OBJECT_ID_STR%>) {
			Logon.message.warning('<fmt:message key="contadmin.errors.required.trsp.cat"/>');
			return false;
		}
		if (inputContTypeIdPopup == <%=Constant.INVALID_OBJECT_ID_STR%>) {
			Logon.message.warning('<fmt:message key="contadmin.errors.required.type"/>');
			return false;
		}

		<%--document.getElementById("inputAction").value=forwards.SAVE_NEW;
		document.getElementById("containerNameId").value = inputContNameIdPopup;
		document.getElementById("containerNoId").value = inputContNoIdPopup;
		document.getElementById("containerTrspCatId").value = inputContTrspCatIdPopup;
		document.getElementById("containerTypeId").value = inputContTypeIdPopup;--%>
		
		var diva = Ext.get('logonPopup');
		var params = {"inputAction":forwards.SAVE_NEW,"inputContainerName":inputContNameIdPopup,"inputContainerNo":inputContNoIdPopup,"inputContainerTrspCat":inputContTrspCatIdPopup,"inputContainerType":inputContTypeIdPopup};
		diva.load({
			url: 'ContainerAdmin.do', 
			params: params,
			text: texts.exjsloading,
			scripts: true
		});
	}
	
--></script>
<div class="customer_max"><!-- beginn Border-Table   --> <jsp:include
	page="../SubTab9Tile.jsp" /> <!-- Begin table 3 rows -->
<table class="big_form2">

	<tr class="classicblue">
		<th height="21" colspan="4" class="classicblue"><fmt:message
			key="tabitem.masterdata" /> / <fmt:message key="tabsubitem.9.5" /></th>
	</tr>
	<tr>

		<td><!-- Beginn Submenu 2-2-1-1 --> <jsp:include
			page="../Sub5Tab9Tile.jsp" /> <html:form
			action="containeradmin/ContainerAdmin.do"
			styleId="admin_ContainerAdminForm">

			<input type="hidden" name="inputAction" id="inputAction" value="" />
			<html:hidden styleId="containerId" property="containerId" />

			<div class="subform" style="border-top: 0px;"><!-- Beginn Search Table -->
			<table cellpadding="0" cellspacing="0" border="0" class="big_form2"
				style="padding: 0px;">
				<colgroup>
					<col width="20%">
					<col width="80%">
				</colgroup>

				<tr>
					<td valign="top" style="border: 0px;"><span class="labelblue"><fmt:message
						key="contadmin.label.carrier" /></span><br>

					<table class="noborder" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td valign="bottom"
								style="white-space: nowrap; border: 0px; padding-right: 4px;">
							</td>
							<td style="white-space: nowrap; border: 0px;"><span
								class="content" style="border: 0px;"> <html:select
								onchange="selectCarrier()" styleId="carrierId"
								property="carrierId"
								style="margin:0px; padding:0px; width:auto;">
								<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">
									<fmt:message key="contadmin.label.carrierchoose" />
								</html:option>
								<c:forEach var="entry"
									items="${admin_ContainerAdminForm.carrierMap}">
									<html:option value="${entry.key}">
										<c:out value="${entry.value.displayName}" />
									</html:option>
								</c:forEach>
							</html:select> </span></td>
						</tr>
					</table>

					</td>
					<td style="border: 0px;"><span class="labelblue"><fmt:message
						key="contadmin.label.address" /></span><br>
					<c:out value="${admin_ContainerAdminForm.carrierName}" /> <br>
					<c:out value="${admin_ContainerAdminForm.carrierStreet}" /> <br>
					<c:out value="${admin_ContainerAdminForm.carrierCity}" /></td>
				</tr>
			</table>



			<c:if test="${!empty admin_ContainerAdminForm.container}">
				<jsp:include page="ContainerAdmin.jsp"></jsp:include>
			</c:if>
			 <c:if test="${(admin_ContainerAdminForm.carrierId != -1) && (empty admin_ContainerAdminForm.container)}">
			 	<html:hidden styleId="containerNameId" property="inputContainerName" />
			 	<html:hidden styleId="containerNoId" property="inputContainerNo" />
			 	<html:hidden styleId="containerTrspCatId" property="inputContainerTrspCat" />
			 	<html:hidden styleId="containerTypeId" property="inputContainerType" />
			 				 					
				<table class="classicblue" style="width: 100%;">
					<tr>
						<td><fmt:message
							key="contadmin.info.notrailer" /> <crh-common:authorize
							function="<%= Authorization.MASTER_DATA_ROAD_CONTAINER_CREATE %>">
							<div align="right"><input
								onclick="javascript:addNewContainer();" type="button"
								class="SmallButtonStyle"
								value=" <fmt:message key="buttons.add"/> "></div>
						</crh-common:authorize></td>
					</tr>
				</table>
			</c:if></div>

		</html:form></td>


	</tr>
</table>
</div>
<!-- End Border Table -->

