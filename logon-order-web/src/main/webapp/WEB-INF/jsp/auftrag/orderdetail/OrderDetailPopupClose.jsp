<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: OrderDetailPopupClose.jsp,v $ --%>
<%@ include file="../../includeTLDs.jsp"%>

<%@page import="com.crh.logon.order.web.actions.bestellung.BestellungAction"%>

<c:set var="RELOAD_OPEN_ORDERS"><%= BestellungAction.RELOAD_OPEN_ORDERS %></c:set>
<c:set var="RELOAD_OPEN_CUSTORDERS"><%= BestellungAction.RELOAD_OPEN_CUSTORDERS %></c:set>
<c:set var="RELOAD_CLOSED_CUSTORDERS"><%= BestellungAction.RELOAD_CLOSED_CUSTORDERS %></c:set>
<c:set var="RELOAD_CLOSED_ORDERS"><%= BestellungAction.RELOAD_CLOSED_ORDERS %></c:set>
<c:set var="RELOAD_ALL"><%= BestellungAction.RELOAD_ALL %></c:set>

<script type="text/javascript">
	if (typeof onOrderDetailCloseForward == 'undefined') {
		onOrderDetailCloseForward = function(){
			window.location = document.URL;
		}
	}
	function closePopup() {
		hidePopup();
		if (typeof(onOrderDetailClose) == 'function') {
			onOrderDetailClose();
		}
	}
	<c:if test="${not empty reload}">
		<%-- NOTE (romek Jul 28, 2011): actually not all the reload types are used (yet?) --%>
		<c:choose>
			<c:when test="${reload == RELOAD_OPEN_ORDERS}">
				if (typeof openOrders != 'undefined' && !Ext.isEmpty(openOrders)) {
					openOrders.reload(true);
					closePopup();
				}
			</c:when>
			<c:when test="${reload == RELOAD_OPEN_CUSTORDERS}">
				if (typeof openCustorders != 'undefined' && !Ext.isEmpty(openCustorders)) {
					openCustorders.reload(true);
					closePopup();
				}
			</c:when>
			<c:when test="${reload == RELOAD_CLOSED_CUSTORDERS}">
				if (typeof closedCustorders != 'undefined' && !Ext.isEmpty(closedCustorders)) {
					closedCustorders.reload(true);
					closePopup();
				}
			</c:when>
			<c:when test="${reload == RELOAD_CLOSED_ORDERS}">
				if (typeof closedOrders != 'undefined' && !Ext.isEmpty(closedOrders)) {
					closedOrders.reload(true);
					closePopup();
				}
			</c:when>
			<c:when test="${reload == RELOAD_ALL}">
				if (typeof allOrders != 'undefined' && !Ext.isEmpty(allOrders)) {
					allOrders.reload(true);
					closePopup();
				}
			</c:when>
			<c:otherwise>
				<%-- Possible bug in the implementation. --%>
				alert('Internal Error: unknown reload type \'<c:out value="${reload}"/>\'');
			</c:otherwise>
		</c:choose>
		else {
			onOrderDetailCloseForward();
		}
	</c:if>	
	<c:if test="${empty reload}">
		closePopup();
	</c:if>
</script>

<%-- END $RCSfile: OrderDetailPopupClose.jsp,v $ --%>
