<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%><%-- 
BEGIN $RCSfile: RailCarOptimizationShippingpointsTile.jsp,v $ 
--%><%@ include file="../includeTLDs.jsp" %>
<c:set var="selectedShippingpointsString" value='${order_RailCarOptimizationForm.selectedShippingpointsString}'/>
<c:choose>
	<c:when test="${selectedShippingpointsString == 'all'}">
shippingpointList.setValue("<fmt:message key="dispo.button.check" />");
	</c:when>
	<c:when test="${selectedShippingpointsString == 'inactive'}">
shippingpointList.setValue("<fmt:message key="dispo.button.uncheck" />");
	</c:when>
	<c:otherwise>
shippingpointList.setValue("<c:out value='${selectedShippingpointsString}'/>");
	</c:otherwise>
</c:choose>