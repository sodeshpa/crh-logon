<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>
<%-- BEGIN $RCSfile: PartLayout.jsp,v $ --%>
<%@ include file="includeTLDs.jsp" %>

<%@ page session="true" %>

<c:set var="commonDatePattern" value="dd/MM/yyyy" scope="request"/> 
<c:set var="commonTimePattern" value="HH:mm" scope="request"/>
 
<c:if test="${not empty LITDICT_INFO_FLAG}">
	<script type="text/javascript">
	Ext.onReady(function() {
		cleanButtonLabels();
	});
	</script>
</c:if>

<tiles:insert attribute="body" />
<tiles:insert attribute="errors" />
<%-- END $RCSfile: PartLayout.jsp,v $ --%>
 