<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%><%-- 
BEGIN $RCSfile: ContainerMonitoringShippingPointsPartTile.jsp,v $ 
--%><%@ include file="../includeTLDs.jsp" %>
<c:set var="selectedShippingPointsString" value='${order_ContainerMonitoringForm.selectedShippingPointsString}'/>
<c:choose>
	<c:when test="${selectedShippingPointsString == 'all'}">
shippingPointList.setValue("<fmt:message key="dispo.button.check" />");
	</c:when>
	<c:when test="${selectedShippingPointsString == 'inactive'}">
shippingPointList.setValue("<fmt:message key="dispo.button.uncheck" />");
	</c:when>
	<c:otherwise>
shippingPointList.setValue("<c:out value='${selectedShippingPointsString}'/>");
	</c:otherwise>
</c:choose>