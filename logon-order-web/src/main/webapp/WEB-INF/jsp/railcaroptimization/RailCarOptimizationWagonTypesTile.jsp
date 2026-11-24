<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%><%-- 
BEGIN $RCSfile: RailCarOptimizationWagonTypesTile.jsp,v $ 
--%><%@ include file="../includeTLDs.jsp" %>
<c:set var="selectedWagonTypeString" value='${order_RailCarOptimizationForm.selectedWagonTypesString}'/>
<c:choose>
	<c:when test="${selectedWagonTypeString == 'all'}">
wagonTypeList.setValue("<fmt:message key="dispo.button.check" />");
	</c:when>
	<c:when test="${selectedWagonTypeString == 'inactive'}">
wagonTypeList.setValue("<fmt:message key="dispo.button.uncheck" />");
	</c:when>
	<c:otherwise>
wagonTypeList.setValue("<c:out value='${selectedWagonTypeString}'/>");
	</c:otherwise>
</c:choose>