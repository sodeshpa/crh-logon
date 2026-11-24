<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ include file="../includeTLDs.jsp" %>

<script type="text/javascript">
	hidePopup();
	if (logonPopupHideCallback) {
		logonPopupHideCallback();
	}
	<c:if test="${callReloadTrucksJs}">
	refreshTrucks();
	</c:if>
</script>
