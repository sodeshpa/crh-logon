<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.admin.web.actions.masterdata.WagonDetailAction"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>

<%@page import="com.crh.logon.persistence.entities.common.WagonEntity"%><c:set var="org.apache.struts.taglib.html.BEAN" scope="page" value="${admin_WagonDetailForm}" />
<jsp:include page="TabBase.jsp"></jsp:include>
<html:hidden property="activeTab" value="<%=WagonDetailAction.FORWARD_WHEELSET_TAB%>"/>
<c:set var="readonly" value="${true}"/>
<crh-common:authorize function="<%= Authorization.RAILCAR_DETAIL_BASE_W %>">
	<c:set var="readonly" value="${false}"/>
</crh-common:authorize>
<c:set var="defaultTextfieldStyleClass"><c:choose><c:when test="${readonly}">textfielddisabled</c:when><c:otherwise>textfield</c:otherwise></c:choose></c:set>

<table class="classicblue" style="border: 0px; padding: 0px; width: 99%;">
	<colgroup>
		<col width="20%"></col>
		<col width="20%"></col>
		<col width="20%"></col>
		<col width="20%"></col>
		<col width="20%"></col>
	</colgroup>
	<tbody>
		<tr>
			<td/>
			<td class="label" style="vertical-align: bottom;">
				<fmt:message key="wagon.wheelset_no"/>
			</td>
			<td class="label" style="vertical-align: bottom;">
				<fmt:message key="wagon.wheelset_diameter"/>
			</td>
			<td class="label" style="vertical-align: bottom;">
				<fmt:message key="wagon.wheelset_build"/>
			</td>
			<td class="label" style="vertical-align: bottom;">
				<fmt:message key="wagon.wheelset_year"/>
			</td>
		</tr>
		<tr>
			<td class="label" style="vertical-align: bottom;">
				<fmt:message key="wagon.wheelset1"/>
			</td>
			<td>
				<html:text disabled="${readonly}" property="wagonDetailWheelset.wheelset1NbsNo" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}"  maxlength="<%=String.valueOf(WagonEntity.DBSIZE_WHEELSET_1_NBS_NO)%>"/>
			</td>
			<td>
				<html:text disabled="${readonly}" property="wagonDetailWheelset.wheelset1NbsDiameterMm" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}"  maxlength="<%=String.valueOf(WagonEntity.DBSIZE_WHEELSET_1_NBS_DIAMETER_MM)%>"/>
			</td>
			<td>
				<html:text disabled="${readonly}" property="wagonDetailWheelset.wheelset1NbsBuild" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}"  maxlength="<%=String.valueOf(WagonEntity.DBSIZE_WHEELSET_1_NBS_BUILD)%>"/>
			</td>
			<td>
				<html:text disabled="${readonly}" property="wagonDetailWheelset.wheelset1NbsYear" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}"/>
			</td>
		</tr>

		<tr>
			<td class="label" style="vertical-align: bottom;">
				<fmt:message key="wagon.wheelset2"/>
			</td>
			<td>
				<html:text disabled="${readonly}" property="wagonDetailWheelset.wheelset2NbsNo" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=String.valueOf(WagonEntity.DBSIZE_WHEELSET_2_NBS_NO) %>"/>
			</td>
			<td>
				<html:text disabled="${readonly}" property="wagonDetailWheelset.wheelset2NbsDiameterMm" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=String.valueOf(WagonEntity.DBSIZE_WHEELSET_2_NBS_DIAMETER_MM)%>"/>
			</td>
			<td>
				<html:text disabled="${readonly}" property="wagonDetailWheelset.wheelset2NbsBuild" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=String.valueOf(WagonEntity.DBSIZE_WHEELSET_2_NBS_BUILD)%>"/>
			</td>
			<td>
				<html:text disabled="${readonly}" property="wagonDetailWheelset.wheelset2NbsYear" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}"/>
			</td>
		</tr>

		<tr>
			<td class="label" style="vertical-align: bottom;">
				<fmt:message key="wagon.wheelset3"/>
			</td>
			<td>
				<html:text disabled="${readonly}" property="wagonDetailWheelset.wheelset3BsNo" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=String.valueOf(WagonEntity.DBSIZE_WHEELSET_3_BS_NO)%>"/>
			</td>
			<td>
				<html:text disabled="${readonly}" property="wagonDetailWheelset.wheelset3BsDiameterMm" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=String.valueOf(WagonEntity.DBSIZE_WHEELSET_3_BS_DIAMETER_MM)%>"/>
			</td>
			<td>
				<html:text disabled="${readonly}" property="wagonDetailWheelset.wheelset3BsBuild" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=String.valueOf(WagonEntity.DBSIZE_WHEELSET_3_BS_BUILD)%>"/>
			</td>
			<td>
				<html:text disabled="${readonly}" property="wagonDetailWheelset.wheelset3BsYear" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}"/>
			</td>
		</tr>

		<tr>
			<td class="label" style="vertical-align: bottom;">
				<fmt:message key="wagon.wheelset4"/>
			</td>
			<td>
				<html:text disabled="${readonly}" property="wagonDetailWheelset.wheelset4BsNo" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%= String.valueOf(WagonEntity.DBSIZE_WHEELSET_4_BS_NO)%>"/>
			</td>
			<td>
				<html:text disabled="${readonly}" property="wagonDetailWheelset.wheelset4BsDiameterMm" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=String.valueOf(WagonEntity.DBSIZE_WHEELSET_4_BS_DIAMETER_MM)%>"/>
			</td>
			<td>
				<html:text disabled="${readonly}" property="wagonDetailWheelset.wheelset4BsBuild" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" maxlength="<%=String.valueOf(WagonEntity.DBSIZE_WHEELSET_4_BS_BUILD)%>"/>
			</td>
			<td>
				<html:text disabled="${readonly}" property="wagonDetailWheelset.wheelset4BsYear" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}"/>
			</td>
		</tr>
	</tbody>
</table>
