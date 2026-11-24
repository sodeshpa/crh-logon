<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%><%-- 
BEGIN $RCSfile: BimodalRailcarMonitoringShiptosPartTile.jsp,v $ 
--%><%@ include file="../includeTLDs.jsp" %>
<c:set var="selectedShiptosString" value='${order_BimodalRailcarMonitoringForm.selectedShiptosString}'/>
<c:choose>
	<c:when test="${selectedShiptosString == 'all'}">
shiptoList.setValue("<fmt:message key="dispo.button.check" />");
	</c:when>
	<c:when test="${selectedShiptosString == 'none'}">
shiptoList.setValue("<fmt:message key="dispo.button.inactive" />");
	</c:when>
	<c:otherwise>
shiptoList.setValue("<c:out value='${selectedShiptosString}'/>");
	</c:otherwise>
</c:choose>