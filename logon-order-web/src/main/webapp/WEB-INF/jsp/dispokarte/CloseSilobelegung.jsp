<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: CloseSilobelegung.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp" %>

<script type="text/javascript">
	hidePopup();
	var diva = Ext.get('DivSilobelegung');
	diva.load({
		url: '../dispokarte/Silobelegung.do?referrer=<c:out value="${param.referrer}"/>', 
		text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
		scripts: true
	});
</script>