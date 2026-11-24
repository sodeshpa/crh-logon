<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.admin.web.actions.masterdata.WagonDetailAction"%>
<jsp:include page="TabBase.jsp"></jsp:include>
<html:hidden property="activeTab" value="<%=WagonDetailAction.FORWARD_TYPE_TAB%>"/>

<div id="wagonTypeInfoId">
	<jsp:include page="WagonTypeInfo.jsp"/>
</div>
