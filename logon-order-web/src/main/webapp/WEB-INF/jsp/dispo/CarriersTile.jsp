<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%><%-- 
BEGIN $RCSfile: CarriersTile.jsp,v $ 
--%><%@ include file="../includeTLDs.jsp" %>
<c:set var="selectedCarriersString" value='${order_dispo_DispoForm.selectedCarriersString}'/>
<c:choose>
	<c:when test="${selectedCarriersString == 'all'}">
carrierList.setValue("<fmt:message key="dispo.button.check" />");
	</c:when>
	<c:when test="${selectedCarriersString == 'none'}">
carrierList.setValue("<fmt:message key="dispo.button.uncheck" />");
	</c:when>
	<c:otherwise>
carrierList.setValue("<c:out value='${selectedCarriersString}'/>");
	</c:otherwise>
</c:choose>
<c:choose>
<c:when test="${order_dispo_DispoForm.numberOfSelectedCarriers == 1}">
carrierList.singleCarrierSelected = true;
</c:when>
<c:otherwise>
carrierList.singleCarrierSelected = false;
</c:otherwise>
</c:choose>