<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.admin.web.actions.masterdata.WagonDetailAction"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<c:set var="wagonDetailType" scope="page" value="${admin_WagonDetailForm.wagonDetailType}" />
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page" value="${admin_WagonDetailForm}" />
<c:set var="form" scope="page" value="${admin_WagonDetailForm}" />
<c:set var="INVALID_OBJECT_ID"><%=com.crh.logon.constants.Constant.INVALID_OBJECT_ID%></c:set>
<c:set var="readonly" value="${true}"/>
<crh-common:authorize function="<%= Authorization.RAILCAR_DETAIL_BASE_W %>">
	<c:set var="readonly" value="${false}"/>
</crh-common:authorize>
<c:set var="defaultTextfieldStyleClass"><c:choose><c:when test="${readonly}">textfielddisabled</c:when><c:otherwise>textfield</c:otherwise></c:choose></c:set>

<script type="text/javascript">
Ext.onReady(function() {

	Ext.get('wagonTypeSelectId').on('change', function() {
		requestInfo('<%=WagonDetailAction.FORWARD_WAGON_TYPE_INFO%>', 'wagonTypeInfoId');
	});
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
	  					<col width="60%"></col>
					</colgroup>
					<tbody>
						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.wagontype"/>
							</td>
							<td>
								<c:set var="wagonTypeBsList" scope="page" value="${wagonDetailType.wagonTypeBsList}"/>
								<c:set var="styleClass"><c:choose><c:when test="${readonly || form.wagonTypeIsLocked}">textfielddisabled</c:when><c:otherwise>textfield</c:otherwise></c:choose></c:set>
								<html:select disabled="${readonly || form.wagonTypeIsLocked}" size="1" property="wagonDetailType.wagonTypeCd" style="width: 100%;" styleClass="${styleClass}" styleId="wagonTypeSelectId">
									<c:if test="${wagonDetailType.wagonTypeCd == null or wagonDetailType.wagonTypeCd == INVALID_OBJECT_ID}">
										<html:option value="${INVALID_OBJECT_ID}">-</html:option>
									</c:if>
									<html:options collection="wagonTypeBsList" property="wagonTypeCd" labelProperty="wagonTypeName" />
								</html:select>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.uic_class"/>
							</td>
							<td>
								<html:text disabled="true" property="wagonDetailType.wagonTypeWagonUicClassCd" style="width: 100%;" styleClass="textfielddisabled"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.category"/>
							</td>
							<td>
								<html:text disabled="true" property="wagonDetailType.wagonTrspCatTx.txt" style="width: 100%;" styleClass="textfielddisabled"/>
							</td>
						</tr>
						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.nr_containers"/>
							</td>
							<td>
								<html:text disabled="true" property="wagonDetailType.wagonType.nrContainers" style="width: 100%;" styleClass="textfielddisabled"/>
							</td>
						</tr>
				<tr>
				<td colspan="2" style="white-space: nowrap; border: 0px; vertical-align: bottom;">&nbsp;</td>
				</tr>
							<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.nhm_empty"/>
							</td>
								<c:if test="${!empty wagonDetailType.nhmEmpty}">
							<td>								
									<html:text disabled="true" property="wagonDetailType.nhmEmpty" style="width: 100%;" styleClass="textfielddisabled"/>
							</td>									
								</c:if>
								<c:if test="${empty wagonDetailType.nhmEmpty}">
							<td style="color:#FF0000;">
									<fmt:message key="wagon.nhmcodeproblem"/>
							</td>
								</c:if>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.nhm_maintenance"/>
							</td>
								<c:if test="${!empty wagonDetailType.nhmMaintenance}">
							<td>
									<html:text disabled="true" property="wagonDetailType.nhmMaintenance" style="width: 100%;" styleClass="textfielddisabled"/>
							</td>									
								</c:if>
								<c:if test="${empty wagonDetailType.nhmMaintenance}">
							<td style="color:#FF0000;">								
									<fmt:message key="wagon.nhmcodeproblem"/>
							</td>
								</c:if>															
						</tr>
						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.nhm_repair"/>
							</td>
								<c:if test="${!empty wagonDetailType.nhmRepair}">
							<td>
								<html:text disabled="true" property="wagonDetailType.nhmRepair" style="width: 100%;" styleClass="textfielddisabled"/>
							</td>								
								</c:if>
								<c:if test="${empty wagonDetailType.nhmRepair}">
							<td style="color:#FF0000;">								
									<fmt:message key="wagon.nhmcodeproblem"/>
							</td>									
								</c:if>							
							</td>
						</tr>						
					</tbody>
				</table>
			</td>
			<td>
				<table cellspacing="0" cellpadding="0" style="border: 0px; padding: 0px; width: 98%;">
					<colgroup>
						<col width="40%"></col>
	  					<col width="60%"></col>
					</colgroup>
					<tbody>
						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.maxload_def"/>
							</td>
							<td>
								<html:text disabled="true" property="wagonDetailType.wagonTypeMaxLoadT" style="width: 100%;" styleClass="textfielddisabled"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.volume_def"/>
							</td>
							<td>
								<html:text disabled="true" property="wagonDetailType.wagonTypeVolumeM3" style="width: 100%;" styleClass="textfielddisabled"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.limit_credit_note_def"/>
							</td>
							<td>
								<html:text disabled="true" property="wagonDetailType.wagonTypeLimitCreditNoteT" style="width: 100%;" styleClass="textfielddisabled"/>
							</td>
						</tr>

						<tr>
							<td colspan="2" style="white-space: nowrap; border: 0px; vertical-align: bottom;">&nbsp;</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.axis_count"/>
							</td>
							<td>
								<html:text disabled="true" property="wagonDetailType.wagonTypeAxisCount" style="width: 100%;" styleClass="textfielddisabled"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.nhm_mat_empty"/>
							</td>
								<c:if test="${!empty wagonDetailType.nhmEmptyMat}">
							<td>
								<c:out value="${wagonDetailType.nhmEmptyMat.txt}"/> (<c:out value="${wagonDetailType.nhmEmptyMat.sapMaterialNo}"/>)
							</td>
								</c:if>
								<c:if test="${empty wagonDetailType.nhmEmptyMat}">
							<td style="color:#FF0000;">
								<c:out value="${wagonDetailType.wagonType.emptySapMaterialNo}"/>	
							</td>
								</c:if>							
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.nhm_mat_maintenance"/>
							</td>
								<c:if test="${!empty wagonDetailType.nhmMaintenanceMat}">
							<td>								
								<c:out value="${wagonDetailType.nhmMaintenanceMat.txt}"/> (<c:out value="${wagonDetailType.nhmMaintenanceMat.sapMaterialNo}"/>)
							</td>
								</c:if>
								<c:if test="${empty wagonDetailType.nhmMaintenanceMat}">
							<td style="color:#FF0000;">
								<c:out value="${wagonDetailType.wagonType.maintenanceSapMaterialNo}"/>	
							</td>
								</c:if>	
						</tr>
						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.nhm_mat_repair"/>
							</td>
								<c:if test="${!empty wagonDetailType.nhmRepairMat}">
							<td>
								<c:out value="${wagonDetailType.nhmRepairMat.txt}"/> (<c:out value="${wagonDetailType.nhmRepairMat.sapMaterialNo}"/>)
							</td>
								</c:if>
								<c:if test="${empty wagonDetailType.nhmRepairMat}">
							<td style="color:#FF0000;">
								<c:out value="${wagonDetailType.wagonType.repairSapMaterialNo}"/>	
							</td>
								</c:if>								
						</tr>
					</tbody>
				</table>
			</td>
		</tr>
	</tbody>
</table>
