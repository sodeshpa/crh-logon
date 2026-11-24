<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $ $
 Date of last edit : $ $
--%>

<%@ include file="../includeTLDs.jsp"%>
<%@page import="com.crh.logon.constants.Constant"%>

<c:set var="org.apache.struts.taglib.html.BEAN" scope="page"
	value="${admin_ShippingPointForm}" />

<html:select styleClass="textfield" size="1"
	style="padding: 0px; width: 100%;" property="shippingPoint.ldsServer">
	<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">-</html:option>
	<c:forEach var="server" varStatus="status"
		items="${admin_ShippingPointForm.ldsServerList}">
		<html:option value="${server.name}">
			<c:out value="${server.name}" />
		</html:option>
	</c:forEach>
</html:select>
	