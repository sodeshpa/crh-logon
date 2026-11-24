<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.admin.web.actions.masterdata.WagonDetailAction"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@page import="com.crh.logon.persistence.entities.common.WagonEntity"%>
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page" value="${admin_WagonDetailForm}" />
<c:set var="wagonDetailControl" scope="page" value="${admin_WagonDetailForm.wagonDetailControl}" />
<jsp:include page="TabBase.jsp"></jsp:include>
<html:hidden property="activeTab" value="<%=WagonDetailAction.FORWARD_CONTROL_TAB%>"/>
<c:set var="INVALID_OBJECT_ID"><%=com.crh.logon.constants.Constant.INVALID_OBJECT_ID%></c:set>
<c:set var="readonly" value="${true}"/>
<crh-common:authorize function="<%= Authorization.RAILCAR_DETAIL_BASE_W %>">
	<c:set var="readonly" value="${false}"/>
</crh-common:authorize>
<c:set var="defaultTextfieldStyleClass"><c:choose><c:when test="${readonly}">textfielddisabled</c:when><c:otherwise>textfield</c:otherwise></c:choose></c:set>

<script type="text/javascript">
Ext.onReady(function() {

	Ext.get('fromDefaultMaxLoadTId').on('click', function(event, el, obj) {
		Ext.getDom('maxLoadTId').disabled = el.checked;
		if (el.checked){			
			Ext.getDom('maxLoadTId').value = '<c:out value="${admin_WagonDetailForm.wagonDetailType.wagonTypeMaxLoadT}"/>';
		}
		Ext.getDom('maxLoadTId').className = !el.checked?'textfield':'textfielddisabled';
	});
	Ext.get('fromDefaultVolumeM3Id').on('click', function(event, el, obj) {
		Ext.getDom('volumeM3Id').disabled = el.checked;
		if (el.checked){			
			Ext.getDom('volumeM3Id').value = '<c:out value="${admin_WagonDetailForm.wagonDetailType.wagonTypeVolumeM3}"/>';
		}		
		Ext.getDom('volumeM3Id').className = !el.checked?'textfield':'textfielddisabled';
	});
	Ext.get('fromDefaultLimitCreditNoteTId').on('click', function(event, el, obj) {
		Ext.getDom('limitCreditNoteTId').disabled = el.checked;
		if (el.checked){			
			Ext.getDom('limitCreditNoteTId').value = '<c:out value="${admin_WagonDetailForm.wagonDetailType.wagonTypeLimitCreditNoteT}"/>';
		}			
		Ext.getDom('limitCreditNoteTId').className = !el.checked?'textfield':'textfielddisabled';
	});

	Ext.get('activeFlagId').on('click', function(event, el, obj) {
		Ext.getDom('wagonInactiveReasonSelectCd').disabled = el.checked;
		Ext.getDom('wagonInactiveReasonSelectCd').value = '<c:out value="${INVALID_OBJECT_ID}"/>';
		Ext.getDom('wagonInactiveReasonSelectCd').className = !el.checked?'textfield':'textfielddisabled';
	});
	
	document.getElementById("wagonDetailControl.returnShiptoSapBpId").value = '<c:out value="${wagonDetailControl.returnShiptoSapBpId}"/>';
});
</script>

<table class="classicblue" style="border: 0px; padding: 0px; width: 99%;">
	<colgroup>
		<col width="50%">
		<col width="50%">
	</colgroup>
	<tbody>
		<tr>
			<td>
				<table cellspacing="0" cellpadding="0" style="border: 0px; padding: 0px; width: 98%;">
					<colgroup>
						<col width="40%"></col>
						<col width="10%"></col>
						<col width="50%"></col>
					</colgroup>
					<tbody>
						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.wagonno"/>
							</td>
							<td colspan="2">
								<html:text disabled="${readonly}" property="wagonDetailControl.wagonNo" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.sapnumber"/>
							</td>
							<td colspan="2">
								<html:text disabled="${readonly}"
									property="wagonDetailControl.sapVehicleNo"
									style="width: 100%;" styleClass="${defaultTextfieldStyleClass}"
									maxlength="<%= String.valueOf(
											WagonEntity.DBSIZE_SAP_VEHICLE_NO) %>"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.active"/>
							</td>
							<td>
								<html:checkbox disabled="${readonly}" property="wagonDetailControl.activeFlag" styleId="activeFlagId"/>
							</td>
							<td>
								<c:set var="wagonInactiveReasonTxList" scope="page" value="${wagonDetailControl.wagonInactiveReasonTxList}"/>
								<html:select disabled="${readonly or wagonDetailControl.activeFlag}" size="1" property="wagonDetailControl.wagonInactiveReasonCd" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" styleId="wagonInactiveReasonSelectCd">
									<html:option value="${INVALID_OBJECT_ID}"><fmt:message key="common.flags.active"/></html:option>
									<html:options collection="wagonInactiveReasonTxList" property="wagonInactiveReasonTxPK.wagonInactiveReasonBs.wagonInactiveReasonCd" labelProperty="txt" />
								</html:select>
							</td>
						</tr>
		
						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.tare_weight"/>
							</td>
							<td colspan="2">
								<html:text disabled="${readonly}" property="wagonDetailControl.tareWeightT" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.return_shipto"/>
							</td>

							<td colspan="2">
								<c:set var="shiptoList" scope="page" value="${wagonDetailControl.shiptoSapBpList}"/>
								<html:select disabled="${readonly}" size="1" styleId="wagonDetailControl.returnShiptoSapBpId" property="wagonDetailControl.returnShiptoSapBpId" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}">
									<html:option value="${INVALID_OBJECT_ID}">-</html:option>
									<c:forEach var="entry" items="${wagonDetailControl.shiptoSapBpList}">                                 
	                                    <option title = "<c:out value="${entry.shipto.shiptoSapBp.railwayStation.railwayStationName}" /> (<c:out value="${entry.shipto.shiptoSapBp.name1}" /> -  <c:out value="${entry.shipto.shiptoSapBp.sapId}" />)" id='<c:out value="${entry.shipto.shiptoSapBp.sapBpId}" />' value = '<c:out value="${entry.shipto.shiptoSapBp.sapBpId}" />'><c:out value="${entry.shipto.shiptoSapBp.railwayStation.railwayStationName}" /> (<c:out value="${entry.shipto.shiptoSapBp.name1}" /> -  <c:out value="${entry.shipto.shiptoSapBp.sapId}" />)</option>
	                             	</c:forEach>
								</html:select>
							</td>
						</tr>

						<tr>
							<td colspan="3" style="white-space: nowrap; border: 0px; vertical-align: bottom;">&nbsp;</td>
						</tr>
					</tbody>
				</table>
				<div id="wagonKeeperInfoId">
					<jsp:include page="WagonKeeperInfo.jsp"></jsp:include>
				</div>
				<table cellspacing="0" cellpadding="0" style="border: 0px; padding: 0px; width: 98%;">
					<colgroup>
						<col width="40%"></col>
						<col width="40%"></col>
	  					<col width="20%"></col>
					</colgroup>
					<tbody>
						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.underc_manufacturer"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailControl.undercarriageManufacturer" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailControl.undercarriageConstYear" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}"/>
							</td>
						</tr>
					
						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.supers_manufacturer"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailControl.superstructureManufacturer" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailControl.superstructureConstYear" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}"/>
							</td>
						</tr>
					
						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.unloading_system"/>
							</td>
							<td colspan="2">
								<html:text disabled="${readonly}" property="wagonDetailControl.unloadingSystem" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}"/>
							</td>
						</tr>
					
						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.drawing_type"/>
							</td>
							<td colspan="2">
								<html:text disabled="${readonly}" property="wagonDetailControl.drawingType" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}"/>
							</td>
						</tr>
					</tbody>
				</table>
			</td>
			<td>
				<table cellspacing="0" cellpadding="0" style="border: 0px; padding: 0px; width: 98%;">
					<tbody>
						<colgroup>
							<col width="40%"></col>
							<col width="10%"></col>
							<col width="50%"></col>
						</colgroup>
						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.maxload"/>
							</td>
							<td>
								<html:checkbox disabled="${readonly}" property="wagonDetailControl.fromDefaultMaxLoadT" styleId="fromDefaultMaxLoadTId"/>
							</td>
							<td>
								<c:set var="styleClass"><c:choose><c:when test="${readonly || wagonDetailControl.fromDefaultMaxLoadT}">textfielddisabled</c:when><c:otherwise>textfield</c:otherwise></c:choose></c:set>
								<html:text disabled="${readonly || wagonDetailControl.fromDefaultMaxLoadT}" property="wagonDetailControl.maxGrossWeightT" style="width: 100%;" styleClass="${styleClass}" styleId="maxLoadTId"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.volume"/>
							</td>
							<td>
								<html:checkbox property="wagonDetailControl.fromDefaultVolumeM3" styleId="fromDefaultVolumeM3Id"/>
							</td>
							<td>
								<c:set var="styleClass"><c:choose><c:when test="${readonly || wagonDetailControl.fromDefaultVolumeM3}">textfielddisabled</c:when><c:otherwise>textfield</c:otherwise></c:choose></c:set>
								<html:text disabled="${readonly || wagonDetailControl.fromDefaultVolumeM3}" property="wagonDetailControl.volumeM3" maxlength="<%=WagonEntity.DBSIZE_VOLUME_M3 + ""%>" style="width: 100%;" styleClass="${styleClass}" styleId="volumeM3Id"/>
							</td>
						</tr>
						
						<tr>
							<td class="label" colspan="2" style="vertical-align: bottom;">
								<fmt:message key="wagon.written_tare_weight"/>
							</td>
							<td>
								<html:text property="wagonDetailControl.writtenTareWeightT" maxlength="<%=WagonEntity.DBSIZE_WRITTEN_TARE_WEIGHT_T + ""%>" style="width: 100%;" styleClass="textfield" styleId="writtenTareWeightTId"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.limit_credit_note"/>
							</td>
							<td>
								<html:checkbox property="wagonDetailControl.fromDefaultLimitCreditNoteT" styleId="fromDefaultLimitCreditNoteTId"/>
							</td>
							<td>
								<c:set var="styleClass"><c:choose><c:when test="${readonly || wagonDetailControl.fromDefaultLimitCreditNoteT}">textfielddisabled</c:when><c:otherwise>textfield</c:otherwise></c:choose></c:set>
								<html:text disabled="${readonly || wagonDetailControl.fromDefaultLimitCreditNoteT}" property="wagonDetailControl.limitCreditNoteT" style="width: 100%;" styleClass="${styleClass}" styleId="limitCreditNoteTId"/>
							</td>
						</tr>
						
						<tr>
							<td colspan="2" style="white-space: nowrap; border: 0px; vertical-align: bottom;">&nbsp;</td>
						</tr>
				</table>
				<table cellspacing="0" cellpadding="0" style="border: 0px; padding: 0px; width: 98%;">
					<tbody>
						<colgroup>
							<col width="40%"></col>
							<col width="60%"></col>
						</colgroup>
						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.load_limit_a"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailControl.loadingLimitAT" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.load_limit_b"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailControl.loadingLimitBT" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.load_limit_c"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailControl.loadingLimitCT" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.load_limit_d"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailControl.loadingLimitDT" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}"/>
							</td>
						</tr>

						<tr>
							<td colspan="2" valign="bottom" style="white-space: nowrap; border: 0px;">&nbsp;</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.buffer_length"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailControl.bufferLengthMm" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.wheelbase"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailControl.wheelbaseMm" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.wheelbase_bpp"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailControl.wheelbaseBppMm" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}"/>
							</td>
						</tr>
					</tbody>
				</table>
			</td>
		</tr>
	</tbody>
</table>
