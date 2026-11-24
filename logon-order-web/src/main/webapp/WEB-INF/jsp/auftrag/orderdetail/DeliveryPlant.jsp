<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page" value="${order_auftrag_OrderDetailForm}"/>
<c:set var="ORDER_STATUS_DISPATCHED"><%=Constant.ORDER_STATUS_DISPATCHED%></c:set>
<c:set var="DELIVERY_TYPE_ROAD_DIRECT"><%=Constant.DELIVERY_TYPE_ROAD_DIRECT%></c:set>

<c:set var="isDispatched">
	<c:if test="${(order_auftrag_OrderDetailForm.order.orderStatus.orderStatusCd < ORDER_STATUS_DISPATCHED)}" >
	false
	</c:if>
	<c:if test="${(order_auftrag_OrderDetailForm.order.orderStatus.orderStatusCd >= ORDER_STATUS_DISPATCHED)}" >
	true
	</c:if>	
</c:set>

<%----------------------------------------------------------------------------------------------%>
<%--------------------START AUTHORIZATION DRIVEN DISABLED SETTINGS -----------------------------%>
<%----------------------------------------------------------------------------------------------%>

<c:set var="isSpecialDetailDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_W %>">
<c:set var="isSpecialDetailDisabled">false</c:set>
</crh-common:authorize>

<%----------------------------------------------------------------------------------------------%>
<%--------------------END AUTHORIZATION DRIVEN DISABLED SETTINGS -------------------------------%>
<%----------------------------------------------------------------------------------------------%>
<html:select
	styleClass="textfield" size="1" style="width: 135px;"
	property="orderBean.plantCd"
	onchange="javascript:changeShPoint(this.options[this.selectedIndex].value)"
	disabled="${isDispatched || isSpecialDetailDisabled || (order_auftrag_OrderDetailForm.orderBean.deliveryTypeCd != DELIVERY_TYPE_ROAD_DIRECT)}">
	<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">-</html:option>
	<c:forEach var="entity" varStatus="status"
		items="${order_auftrag_OrderDetailForm.plantList}">
		<html:option value="${entity.plantCd}">
			<c:out value="${entity.plantName}" />
		</html:option>
	</c:forEach>
</html:select>
