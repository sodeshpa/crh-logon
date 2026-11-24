<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>
<%-- BEGIN $RCSfile: ShowCreateSpecialOrderPopupTile.jsp,v $ --%>
<%@ include file="../../includeTLDs.jsp"%>

<c:set var="form" value="${sessionScope.admin_WagonDetailForm}"/>

<script type="text/javascript">
	Ext.onReady(function() {
	
		<c:if test="${not showCreateSpecialOrderPopupFailed}">
			showPopup('/logon/order/auftrag/OrderSpecialEntry.do?wagonid=<c:out value="${form.wagonId}"/>');
		</c:if>
	
	});
</script>
