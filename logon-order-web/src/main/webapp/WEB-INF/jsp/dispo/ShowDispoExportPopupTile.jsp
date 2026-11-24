<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>
<%-- BEGIN $RCSfile: ShowDispoExportPopupTile.jsp,v $ --%>
<%@ include file="../includeTLDs.jsp" %>
<%-- Struts needs this fake form tag, fond no better way to do it. --%> 
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page" value="${order_dispo_DispoForm}"/>

<script type="text/javascript">
	Ext.onReady(function() {
	
		<c:if test="${not showDispoExportPopupFailed}">
			showPopup('<c:url value="/dispo/DispoExportPopupEntry.do"/>');
		</c:if>
	
	});
</script>
