<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page"
	value="${order_auftrag_OrderDetailForm}" />

<c:set var="isDispatched">
	<c:out value="${order_auftrag_OrderDetailForm.dispatchedFlag}" />
</c:set>

<%----------------------------------------------------------------------------------------------%>
<%--------------------START AUTHORIZATION DRIVEN DISABLED SETTINGS -----------------------------%>
<%----------------------------------------------------------------------------------------------%>
<c:set var="isMaterialDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_MATERIAL_W %>">
<c:set var="isMaterialDisabled">false</c:set>
</crh-common:authorize>
<%----------------------------------------------------------------------------------------------%>
<%--------------------END AUTHORIZATION DRIVEN DISABLED SETTINGS -------------------------------%>
<%----------------------------------------------------------------------------------------------%> 

<html:select styleClass="textfield" size="1" disabled="${isDispatched || isMaterialDisabled}" style="padding: 0px; width: 100%;"
	property="materialBean.orderItemList[${order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex}].loadingpointId" 
	styleId="orderBean.loadingPointCombo">
	<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">-</html:option>
		<c:forEach var="entity" varStatus="status"
		items="${order_auftrag_OrderDetailForm.ldPointMap}">
		<html:option value="${entity.key}">
			<c:out value="${entity.value.loadingpointName}" />
		</html:option></c:forEach>
</html:select>