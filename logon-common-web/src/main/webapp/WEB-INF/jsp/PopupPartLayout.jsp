<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>
<%-- BEGIN $RCSfile: PopupPartLayout.jsp,v $ --%>
<%@ include file="includeTLDs.jsp" %>
<%@ page session="true" %>

<c:if test="${not empty LITDICT_INFO_FLAG}">
	<script type="text/javascript">
	Ext.onReady(function() {
		cleanButtonLabels();
	});
	</script>
</c:if>
<tiles:insert attribute="body" />
<tiles:insert attribute="errors" />
<%-- END $RCSfile: PopupPartLayout.jsp,v $ --%>
 