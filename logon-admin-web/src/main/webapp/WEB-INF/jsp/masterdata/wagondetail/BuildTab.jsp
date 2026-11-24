<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.admin.web.actions.masterdata.WagonDetailAction"%>
<%@page import="com.crh.logon.persistence.entities.common.WagonEntity"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page" value="${admin_WagonDetailForm}" />
<jsp:include page="TabBase.jsp"></jsp:include>
<html:hidden property="activeTab" value="<%=WagonDetailAction.FORWARD_BUILD_TAB%>"/>
<c:set var="readonly" value="${true}"/>
<crh-common:authorize function="<%= Authorization.RAILCAR_DETAIL_BASE_W %>">
	<c:set var="readonly" value="${false}"/>
</crh-common:authorize>
<c:set var="defaultTextfieldStyleClass"><c:choose><c:when test="${readonly}">textfielddisabled</c:when><c:otherwise>textfield</c:otherwise></c:choose></c:set>

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
								<fmt:message key="wagon.brake_type"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailBuild.brakeType" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=WagonEntity.DBSIZE_BRAKE_TYPE + ""%>"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.brake_blocks_count"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailBuild.brakeBlocksCount" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=WagonEntity.DBSIZE_BRAKE_BLOCKS_COUNT + ""%>"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.brake_blocks_type"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailBuild.brakeBlocksType" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=WagonEntity.DBSIZE_BRAKE_BLOCKS_TYPE + ""%>"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.brake_rigging_no"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailBuild.brakeRiggingNo" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=WagonEntity.DBSIZE_BRAKE_RIGGING_NO + ""%>"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.brake_cylinder_no"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailBuild.brakeCylinderNo" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=WagonEntity.DBSIZE_BRAKE_CYLINDER_NO + ""%>"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.brake_hosepipe_no"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailBuild.brakeHosepipeNo" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=WagonEntity.DBSIZE_BRAKE_HOSEPIPE_NO + ""%>"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.brake_hosepipe_nbs"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailBuild.brakeHosepipeNbs" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=WagonEntity.DBSIZE_BRAKE_HOSEPIPE_NBS + ""%>"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.brake_hosepipe_bs"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailBuild.brakeHosepipeBs" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=WagonEntity.DBSIZE_BRAKE_HOSEPIPE_BS + ""%>"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.triple_valve_type"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailBuild.tripleValveType" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=WagonEntity.DBSIZE_TRIPLE_VALVE_TYPE + ""%>"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.triple_valve_no"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailBuild.tripleValveNo" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=WagonEntity.DBSIZE_TRIPLE_VALVE_NO + ""%>"/>
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
								<fmt:message key="wagon.air_shut_off_cock_type"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailBuild.airShutOffCockType" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=WagonEntity.DBSIZE_AIR_SHUT_OFF_COCK_TYPE + ""%>"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.weighing_valve_type"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailBuild.weighingValveType" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=WagonEntity.DBSIZE_WEIGHING_VALVE_TYPE + ""%>"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.weighing_valve_no"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailBuild.weighingValveNo" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=WagonEntity.DBSIZE_WEIGHING_VALVE_NO + ""%>"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.mechanical_load_change"/>
							</td>

							<td>
								<html:select disabled="${readonly}" size="1" property="wagonDetailBuild.mechanicalLoadChange" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}">
									<html:option value="">-</html:option>
									<html:option value="true"><fmt:message key="option.yes"/></html:option>
									<html:option value="false"><fmt:message key="option.no"/></html:option>
								</html:select>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.pressure_exchanger_type"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailBuild.pressureExchangerType" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=WagonEntity.DBSIZE_PRESSURE_EXCHANGER_TYPE + ""%>"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.pressure_exchanger_no"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailBuild.pressureExchangerNo" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=WagonEntity.DBSIZE_PRESSURE_EXCHANGER_NO + ""%>"/>
							</td>
						</tr>
					</tbody>
				</table>
				<table cellspacing="0" cellpadding="0" style="border: 0px; padding: 0px; width: 98%;">
					<colgroup>
						<col width="40%"></col>
						<col width="40%"></col>
	  					<col width="20%"></col>
					</colgroup>
					<tbody>
						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.buffer_type"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailBuild.bufferType" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=WagonEntity.DBSIZE_BUFFER_TYPE + ""%>"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailBuild.bufferConstYear" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=WagonEntity.DBSIZE_BUFFER_CONST_YEAR + ""%>"/>
							</td>
						</tr>
					</tbody>
				</table>
				<table cellspacing="0" cellpadding="0" style="border: 0px; padding: 0px; width: 98%;">
					<colgroup>
						<col width="40%"></col>
	  					<col width="60%"></col>
					</colgroup>
					<tbody>
						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.bogie_type"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailBuild.bogieType" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=WagonEntity.DBSIZE_BOGIE_TYPE + ""%>"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.bogie_no"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailBuild.bogieNo" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=WagonEntity.DBSIZE_BOGIE_NO + ""%>"/>
							</td>
						</tr>

						<tr>
							<td class="label" style="vertical-align: bottom;">
								<fmt:message key="wagon.spring_type"/>
							</td>
							<td>
								<html:text disabled="${readonly}" property="wagonDetailBuild.springType" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=WagonEntity.DBSIZE_SPRING_TYPE + ""%>"/>
							</td>
						</tr>
					</tbody>
				</table>
			</td>
		</tr>
	</tbody>
</table>
