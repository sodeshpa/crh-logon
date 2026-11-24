<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.admin.web.actions.masterdata.WagonDetailAction"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page" value="${admin_WagonDetailForm}" />
<c:set var="wagonDetailControl" scope="page" value="${admin_WagonDetailForm.wagonDetailControl}" />
<c:set var="INVALID_OBJECT_ID"><%=com.crh.logon.constants.Constant.INVALID_OBJECT_ID%></c:set>
<c:set var="readonly" value="${true}"/>
<crh-common:authorize function="<%= Authorization.RAILCAR_DETAIL_BASE_W %>">
	<c:set var="readonly" value="${false}"/>
</crh-common:authorize>
<c:set var="defaultTextfieldStyleClass"><c:choose><c:when test="${readonly}">textfielddisabled</c:when><c:otherwise>textfield</c:otherwise></c:choose></c:set>

<script type="text/javascript">
Ext.onReady(function() {

	Ext.get('wagonKeeperSelectId').on('change', function() {
		requestInfo('<%=WagonDetailAction.FORWARD_WAGON_KEEPER_INFO%>', 'wagonKeeperInfoId');
	});
});
</script>

<table cellspacing="0" cellpadding="0" style="border: 0px; padding: 0px; width: 98%;">
	<colgroup>
		<col width="40%"></col>
		<col width="10%"></col>
		<col width="50%"></col>
	</colgroup>
	<tr>
		<td class="label" style="vertical-align: bottom;">
			<fmt:message key="wagon.keeper_name"/>
		</td>
		<td colspan="2">
			<c:set var="wagonKeeperList" scope="page" value="${wagonDetailControl.wagonKeeperList}"/>
			<html:select disabled="${readonly}" size="1" property="wagonDetailControl.wagonKeeperId" style="width: 100%;" styleClass="${defaultTextfieldStyleClass}" styleId="wagonKeeperSelectId">
				<c:if test="${wagonDetailControl.wagonKeeperId == null or wagonDetailControl.wagonKeeperId == INVALID_OBJECT_ID}">
					<html:option value="${INVALID_OBJECT_ID}">-</html:option>
				</c:if>
				<html:options collection="wagonKeeperList" property="wagonKeeperId" labelProperty="wagonKeeperName" />
			</html:select>
		</td>
	</tr>
	<tr>
		<td class="label" style="vertical-align: bottom;">
			<fmt:message key="wagon.keeper_email"/>
		</td>
		<td>
			<html:checkbox disabled="true" property="wagonDetailControl.wagonKeeperMaintenanceEmailFlag"/>
		</td>
		<td>
			<html:text disabled="true" property="wagonDetailControl.wagonKeeperEmailAddresses" style="width: 100%;" styleClass="textfielddisabled"/>
		</td>
	</tr>
</table>
