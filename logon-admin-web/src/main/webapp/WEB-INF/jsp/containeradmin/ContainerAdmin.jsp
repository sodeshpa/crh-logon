<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: ContainerAdmin.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp"%>
<%@ page import="com.crh.logon.admin.web.forms.AdminRFIDPopupForm"%>
<%@page
	import="com.crh.logon.persistence.entities.common.ContainerEntity"%>

<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@page import="com.crh.logon.constants.Constant"%>

<c:if test="false">
	<%-- for preview in MyEclipse --%>
	<link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>
<script type="text/javascript"
	src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript"
	src="<crh-common:url value='/dwr/interface/AdminRFIDPopupAjaxService.js'/>"></script>

				<c:set var="inputContainerNameDis" value="true" />
				<crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_CONTAINER_CREATE %>">
				 	<c:set var="inputContainerNameDis" value="false" />
				</crh-common:authorize>

<table class="classicblue" style="width: 100%;">
	<colgroup>
		<col width="13%">
		<col width="29%">
		<col width="29%">
		<col width="29%">
	</colgroup>
	<tr>
		<th height="21" colspan="4"><fmt:message key="tabitem.masterdata" />
		/ <fmt:message key="tabsubitem.9.5" /> / <fmt:message
			key="tabsubitem.9.5.5" /></th>
	</tr>
	<tr>
		<%--------------------------------------------------------------%>
		<%------------------- Column 1 - Container list-------------------%>
		<%--------------------------------------------------------------%>
		<td><div class="subtitle1"> <fmt:message
			key="contadmin.label.container" /></div>
			
			<html:checkbox styleId="showOnlyActiveContainers" property="showOnlyActiveContainers" onclick="filterContainers()"/>
            <fmt:message key="roadmasterdata.label.show.only.active.containers"/>
			
			
		<div class="scrollist"
			style="margin: 4px 0px 4px 0px; height: 410px; width: 100%;">
		<table cellpadding="0" cellspacing="0" border="0"
			style="border: medium none; width: 100%;">
			<tbody>
				<c:set var="zebracount" value="1" />
				<c:forEach var="entry"
					items="${admin_ContainerAdminForm.containerMap}">
					<c:set var="zebracount" value="${(zebracount + 1)%2}" />
					<tr
						<c:if test="${entry.key eq admin_ContainerAdminForm.containerId }">class="selected"</c:if>
						<c:if test="${entry.key ne admin_ContainerAdminForm.containerId }"> class="zebra<c:out value="${zebracount}" />" </c:if>>
						<td><a href="#"
							onclick='selectContainer(<c:out value="${entry.key}"/>)'><c:out
							value="${entry.value.containerName}" /></a></td>
					</tr>
				</c:forEach>

			</tbody>
		</table>
		</div>
		</td>
		<%--------------------------------------------------------------%>
		<%------------------- Column 2 - Container main data------------%>
		<%--------------------------------------------------------------%>
		<td><span class="subtitle1"> <fmt:message
			key="contadmin.label.masterdata" /></span>

		<div class="subform"
			style="margin: 4px 0px 4px 0px; padding: 0px; height: 410px;">
		<table border="0" cellspacing="0" cellpadding="0">
			<colgroup>
				<col width="45%">
				<col width="55%">
			</colgroup>
			<tr>
				<td valign="bottom" class="label"
					style="white-space: nowrap; border: 0px;"><fmt:message
					key="contadmin.label.name" />:</td>
				<td class="content" style="white-space: nowrap; border: 0px;">
				

				
				<html:text style="width:100%;" styleId="containerNameId" property="inputContainerName" 
				disabled="${inputContainerNameDis}"  maxlength="<%=ContainerEntity.DBSIZE_CONTAINER_NAME + ""%>" /></td>
			</tr>
			<tr>
				<td valign="bottom" class="label"
					style="white-space: nowrap; border: 0px;"><fmt:message
					key="contadmin.label.no" />:</td>
				<td class="content" style="white-space: nowrap; border: 0px;">
				<html:text style="width:100%;" styleId="containerNoId"
					property="inputContainerNo" disabled="${inputContainerNameDis}"
					maxlength="<%=ContainerEntity.DBSIZE_CONTAINER_NO + ""%>" /></td>
			</tr>
			<tr>
				<td valign="bottom" class="label"
					style="white-space: nowrap; border: 0px;"><fmt:message
					key="contadmin.label.active" />:</td>
				<td class="content" style="white-space: nowrap; border: 0px;">
				<html:checkbox property="inputActive" /></td>
			</tr>
			<tr>
				<td valign="bottom" class="label"
					style="white-space: nowrap; border: 0px;"><fmt:message
					key="contadmin.label.uic.profile" />:</td>
				<td class="content" style="white-space: nowrap; border: 0px;">
				<html:text style="width:100%;" property="inputUicProfile"
					maxlength="<%=ContainerEntity.DBSIZE_UIC_PROFILE + ""%>" /></td>
			</tr>
			<tr>
				<td valign="bottom" class="label"
					style="white-space: nowrap; border: 0px;"><fmt:message
					key="contadmin.label.tara.weight" />:</td>
				<td class="content" style="white-space: nowrap; border: 0px;">
				<html:text style="width:100%;" property="inputTareWeight"
					maxlength="<%=ContainerEntity.DBSIZE_TARE_WEIGHT_T + ""%>" /></td>
			</tr>
			<tr>
				<td valign="bottom" class="label"
					style="white-space: nowrap; border: 0px;"><fmt:message
					key="contadmin.label.net.weight" />:</td>
				<td class="content" style="white-space: nowrap; border: 0px;">
				<html:text style="width:100%;" property="inputMaxNetWeight"
					maxlength="<%=ContainerEntity.DBSIZE_MAX_NET_WEIGHT_T + ""%>" /></td>
			</tr>			
			<tr>
				<td valign="bottom" class="label"
					style="white-space: nowrap; border: 0px;"><fmt:message
					key="contadmin.label.trsp.cat" />:</td>
				<td class="content" style="white-space: nowrap; border: 0px;"><html:select
					disabled="${inputContainerNameDis}" styleId="containerTrspCatId" property="inputContainerTrspCat"
					style="width:100%; margin:0px; padding:0px;">
					<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">-</html:option>
					<c:forEach var="entry"
						items="${admin_ContainerAdminForm.containerTrspCatMap}">
						<html:option value="${entry.key}">
							<c:out value="${entry.value.txt}" />
						</html:option>
					</c:forEach>
				</html:select></td>
			</tr>
			<tr>
				<td valign="bottom" class="label"
					style="white-space: nowrap; border: 0px;"><fmt:message
					key="contadmin.label.trsp.type" />:</td>
				<td class="content" style="white-space: nowrap; border: 0px;"><html:select
					styleId="containerTypeId" property="inputContainerType"
					style="width:100%; margin:0px; padding:0px;">
					<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">-</html:option>
					<c:forEach var="entry"
						items="${admin_ContainerAdminForm.containerTypeMap}">
						<html:option value="${entry.key}">
							<c:out value="${entry.value.txt}" />
						</html:option>
					</c:forEach>
				</html:select></td>
			</tr>
			<tr>
				<td valign="bottom" class="label"
					style="white-space: nowrap; border: 0px;"><fmt:message
					key="contadmin.label.length" />:</td>
				<td class="content" style="white-space: nowrap; border: 0px;">
				<html:text style="width:100%;" property="inputLength"
					maxlength="<%=ContainerEntity.DBSIZE_LENGTH_FT + ""%>" /></td>
			</tr>
			<tr>
				<td valign="bottom" class="label"
					style="white-space: nowrap; border: 0px;"><fmt:message
					key="contadmin.label.width" />:</td>
				<td class="content" style="white-space: nowrap; border: 0px;">
				<html:text style="width:100%;" property="inputWidth"
					maxlength="<%=ContainerEntity.DBSIZE_WIDTH_MM + ""%>" /></td>
			</tr>
			<tr>
				<td valign="bottom" class="label"
					style="white-space: nowrap; border: 0px;"><fmt:message
					key="contadmin.label.height" />:</td>
				<td class="content" style="white-space: nowrap; border: 0px;">
				<html:text style="width:100%;" property="inputHeight"
					maxlength="<%=ContainerEntity.DBSIZE_HEIGHT_MM + ""%>" /></td>
			</tr>
			<tr>
				<td valign="bottom" class="label"
					style="white-space: nowrap; border: 0px;"><fmt:message
					key="contadmin.label.volumeM3" />:</td>
				<td class="content" style="white-space: nowrap; border: 0px;">
				<html:text style="width:100%;" property="inputVolumeM3"
					maxlength="<%=ContainerEntity.DBSIZE_VOLUME_M3 + ""%>" /></td>
			</tr>
			<tr>
				<td valign="bottom" class="label"
					style="white-space: nowrap; border: 0px;"><fmt:message
					key="contadmin.label.manufacturer" />:</td>
				<td class="content" style="white-space: nowrap; border: 0px;">
				<html:text style="width:100%;" property="inputManufacturer"
					maxlength="<%=ContainerEntity.DBSIZE_MANUFACTURER + ""%>" /></td>
			</tr>
			<tr>
				<td valign="bottom" class="label"
					style="white-space: nowrap; border: 0px;"><fmt:message
					key="contadmin.label.branding" />:</td>
				<td class="content" style="white-space: nowrap; border: 0px;">
				<html:select styleId="inputBrandingFlagId"
					property="inputBrandingFlag"
					style="width:100%; margin:0px; padding:0px;">
					<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>"><fmt:message key="option.notappl"/></html:option>
					<html:option value="0"><fmt:message key="contadmin.value.no" /></html:option>
					<html:option value="1"><fmt:message key="contadmin.value.yes" /></html:option>
				</html:select></td>
			</tr>
		</table>
		</div>
		</td>
		<%--------------------------------------------------------------%>
		<%------------------- Column 3 - history list and comment-------%>
		<%--------------------------------------------------------------%>
		<td><span class="subtitle1"> <fmt:message
			key="contadmin.label.history" /></span>

		<div class="subformlist"
			style="margin: 4px 0px 4px 4px; padding: 0px; height: 125px; width: 98%">
		<table class="noborder" cellpadding="0" cellspacing="0" border="0"
			style="width: 100%">
			<colgroup>
				<col width="100%">
			</colgroup>
			<tbody>
				<c:set var="zebracount" value="1" />
				<c:forEach var="entry"
					items="${admin_ContainerAdminForm.contHistoryList}">
					<c:set var="zebracount" value="${(zebracount + 1)%2}" />
					<tr class="zebra<c:out value="${zebracount}" />">
						<td><c:out value="${entry}" /></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		</div>
		<br>
		<!-- beginn special title with button aligned to the right -->
		<div
			style="margin-bottom: 0px; padding-left: 4px; padding-bottom: 4px;">
		<span class="subtitle1"> <fmt:message key="contadmin.label.rfid" /></span><br>
		</div>

		<!-- beginn RFID -->
		<div class="subformlist" id="<%=AdminRFIDPopupForm.NAME_OF_HTML_DIV_TO_RELOAD%>"
			style="clear: both; font-size: 12px; margin: 0px 4px 0px 4px;">
		<jsp:include page="../AdminRfidsTile.jsp" /></div>
		<div align="right"><input
			onclick="javascript:updateContNoShowRFIDPopup();" type="button"
			class="SmallButtonStyle" value=" <fmt:message key="buttons.add"/> ">
		</div>
		<!-- End RFID -->
		<div style="padding: 4px; width: 100%;"><span class="label"><fmt:message
			key="contadmin.label.comment" /></span><br>
			<c:set var="textarealength"><%=ContainerEntity.DBSIZE_CONTAINER_COMMENT + ""%></c:set>
		<html:textarea onkeypress="return checkMaxLength(event, this, ${textarealength})" styleId="inputCommentId" property="inputComment"
			rows="3" style="width: 98%;"></html:textarea></div>

		</td>
		<%--------------------------------------------------------------%>
		<%------------------- Column 4 - Map-----------------------------%>
		<%--------------------------------------------------------------%>
		<td><span class="subtitle1"> <fmt:message
			key="contadmin.label.map" /></span>
		<div class="subform"
			style="margin: 4px 0px 4px 0px; padding: 0px; height: 410px;">
		<jsp:include page="ContainerAdminMap.jsp" /></div>
		</td>
	</tr>

	<tr>
		<td><crh-common:authorize
			function="<%= Authorization.MASTER_DATA_ROAD_CONTAINER_CREATE %>">
			<div align="right"><input onclick="javascript:addNewContainer();"
				type="button" class="SmallButtonStyle"
				value=" <fmt:message key="contadmin.button.copy"/> " alt="Container" /></div>
		</crh-common:authorize></td>
		<td colspan="3">
		<div align="right"
			style="margin: 4px 0px 4px 0px; padding: 0px; height: 7%"><crh-common:authorize
			function="<%= Authorization.MASTER_DATA_ROAD_CONTAINER %>">
			<input onclick="javascript:resetContainer('<c:out value="${admin_ContainerAdminForm.containerId }" />')" type="button"
				class="ViewButtonStyle" style="margin-bottom: 4px;"
				value="<fmt:message key="buttons.reset"/>">
                      &nbsp;&nbsp;
		<input id="saveButton" name="saveButton"
				onclick="javascript:saveContainer()" type="button"
				class="ViewButtonStyle" style="margin-bottom: 4px;"
				value="<fmt:message key="buttons.save"/>">

		</crh-common:authorize></div>
		<br>
		</td>
	</tr>


</table>
