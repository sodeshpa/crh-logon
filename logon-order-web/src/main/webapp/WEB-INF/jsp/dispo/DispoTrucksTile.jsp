<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%><%-- 
BEGIN $RCSfile: DispoTrucksTile.jsp,v $ 
--%><%@ include file="../includeTLDs.jsp" %>
<%@page import="com.crh.logon.constants.Constant"%>

<c:set var="dispoForm" value="${order_dispo_DispoForm}"/>

<c:set var="FILTER_FOR_TRUCK_ALL"><%= Constant.FILTER_FOR_TRUCK_ALL %></c:set>
<c:set var="FILTER_FOR_TRUCK_PLANNING_ONLY"><%= Constant.FILTER_FOR_TRUCK_PLANNING_ONLY %></c:set>
<c:set var="FILTER_FOR_TRUCK_REAL_ONLY"><%= Constant.FILTER_FOR_TRUCK_REAL_ONLY %></c:set>

var trucksFieldValue = "";

<c:choose>
	<c:when test="${dispoForm.selectedFilterForTruck == FILTER_FOR_TRUCK_PLANNING_ONLY}">
		trucksFieldValue += "<fmt:message key="dispo.texte.trucks.summary.planning"/>";
	</c:when>
	<c:when test="${dispoForm.selectedFilterForTruck == FILTER_FOR_TRUCK_REAL_ONLY}">
		trucksFieldValue += "<fmt:message key="dispo.texte.trucks.summary.real"/>";
	</c:when>
	<c:otherwise>
		trucksFieldValue += "<fmt:message key="dispo.texte.trucks.summary.all"/>";
	</c:otherwise>
</c:choose>	

trucksFieldValue += " ";

<c:choose>
	<c:when test="${dispoForm.showOnlyTrucksWithOrders}">
		trucksFieldValue += " -";
	</c:when>
	<c:otherwise>
		trucksFieldValue += " +";
	</c:otherwise>
</c:choose>	

trucksField.setValue(trucksFieldValue);
