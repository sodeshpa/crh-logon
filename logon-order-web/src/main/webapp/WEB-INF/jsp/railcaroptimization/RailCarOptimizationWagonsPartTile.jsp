
<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: RailCarOptimizationWagonsPartTile.jsp,v $ --%>

<%@ page session="true" %>
<%@ include file="../includeTLDs.jsp" %>
<link rel="stylesheet" type="text/css" href="<crh-common:url value="/css/dispo.css"/>">
<%@ taglib uri="http://com.crh.logon/tags/order-taglib.tld" prefix="crh-order" %>

<%@page import="com.crh.logon.persistence.entities.views.RailcarOptLoadedCarsEntity"%>
<c:if test="false">
    <%-- for preview in MyEclipse --%>
    <link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>


<table class="dispotable" style="width:100%;overflow-y:scroll;">
	<tr>
		<th><fmt:message key="railcaroptimization.texte.RailcarNr" /></th>
		<th><fmt:message key="railcaroptimization.texte.RailcarType" /></th>
		<th><fmt:message key="railcaroptimization.texte.NetWeight" /></th>
		<th><fmt:message key="railcaroptimization.texte.Product" /></th>
		<th><fmt:message key="railcaroptimization.texte.Nr" /></th>
		<th><fmt:message key="railcaroptimization.texte.State" /></th>
		<th><fmt:message key="railcaroptimization.texte.soldto" /></th>
		<th><fmt:message key="railcaroptimization.texte.Shipto" /></th>
	</tr>
	<logic:iterate id="wagon" name="order_RailCarOptimizationForm" property="wagonList"  indexId="i">
	<tr class="wagon <crh-order:wagonClass wagon="<%=(RailcarOptLoadedCarsEntity)pageContext.getAttribute("wagon") %>"/>">
		<td><c:set var="wagonNo" value="${wagon.wagonNo}"/><crh-common:wagonNumberFormat wagonNumber="<%=(String)(pageContext.getAttribute("wagonNo")) %>"/></td>
		<td><c:out value="${wagon.wagonTypeName}"/><br>(<c:out value="${wagon.wagonTypeNameTx}"/>)</td>
		<td><c:out value="${wagon.netQuantity}"/></td>
		<td><c:out value="${wagon.materialName}"/></td>
		<td><c:out value="${wagon.orderNumber}"/></td>
		<td><c:out value="${wagon.orderStatusTxt}"/></td>
		<td><c:out value="${wagon.soldtoBpName}"/></td>
		<td><c:out value="${wagon.shiptoBpName}"/></td>
	</tr>
	</logic:iterate>
	<tr>	
	</tr>
</table>
