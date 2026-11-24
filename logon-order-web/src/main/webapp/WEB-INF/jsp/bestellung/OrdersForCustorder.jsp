<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: OrdersForCustorder.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp" %>

<%-- Note: There will always be at least one Single-Delivery Order for a Customer Order. --%>

<c:set var="form" scope="page" value="${order_bestellung_BestellungForm}" />

<c:if test="${not empty form.openOrdersForCustorder}">
	<div id="divOpenOrdersForCustorder">
		<jsp:include page="OpenOrdersForCustorder.jsp"/>
	</div>
</c:if>

<c:if test="${not empty form.closedOrdersForCustorder}">
	<div id="divClosedOrdersForCustorder">
		<jsp:include page="ClosedOrdersForCustorder.jsp"/>
	</div>
</c:if>
