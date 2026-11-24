<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: CloseExternalOrderPopup.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp" %>

<script type="text/javascript">
	hidePopup();
	if (logonPopupHideCallback) {
		logonPopupHideCallback();
	}
	<c:if test="${callReloadOrdersJs}">
	refreshOrders();	
	if(typeof refreshTrucks !== 'undefined') {
		refreshTrucks();
	}
	</c:if>
</script>
