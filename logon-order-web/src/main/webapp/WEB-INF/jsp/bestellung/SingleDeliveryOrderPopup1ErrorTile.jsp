<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: SingleDeliveryOrderPopup1ErrorTile.jsp,v $ --%>

<%@ page session="true" %>
<%@ include file="../includeTLDs.jsp"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>

<c:set var="form" scope="page" value="${order_bestellung_BestellungForm}" />

<script type="text/javascript">
	<%--  --%>
	Ext.onReady(function() {
		cancelCustOrdCreation();
	});
</script>

<%-- END $RCSfile: SingleDeliveryOrderPopup1ErrorTile.jsp,v $ --%>
