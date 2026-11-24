<%@ taglib uri="http://pd4ml.com/tlds/pd4ml/2.6" prefix="pd4ml" %>
<%@ taglib uri="http://com.crh.logon/tags/fmt.tld" prefix="fmt" %>
<%@ taglib uri="http://com.crh.logon/tags/common-taglib.tld" prefix="crh-common" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page trimDirectiveWhitespaces="true" %>
<%@ page contentType="text/html charset=UTF-8" %>

<%@ page import="com.crh.logon.business.factories.PDFPageFactory" %>
<%@ page import="com.crh.logon.business.services.beans.pdf.deliverynote.*" %>
<%@ page import="com.crh.logon.business.services.pdftools.layout.*" %>

<pd4ml:transform screenWidth="595" pageFormat="A4" pageOrientation="portrait" pageInsets="0,0,0,0,points" debug="true" enable="true">
<pd4ml:usettf from="java:fonts" serif="Times New Roman" sansserif="Arial" monospace="Courier New"/>  
<fmt:setBundle basename="LitDict"/>

<%
	DeliveryNotePdfDataSource data = (DeliveryNotePdfDataSource) request.getAttribute(PDFPageFactory.REPORT_DATASOURCE);
	BodyData body = data.getBody();
%>

<html>
<head>
	<link type="text/css" rel="stylesheet" href="<crh-common:url value="/css/deliverynote-template.css"/>">
</head>
<body>

<table class="page-container">
<tr>
	<td class="align-top">
		<jsp:include page="deliverynote-title.jsp" />
		<jsp:include page="deliverynote-address.jsp" />
		<div class="ruler"></div>
		<jsp:include page="deliverynote-orderitem.jsp" />
	</td>
</tr>

<tr>
	<td class="align-bottom">
		<% pageContext.include("includes/logolayout-" + body.getMaterialLogo().getLogoLayout().getName() + ".jsp"); %>
		<jsp:include page="deliverynote-bottom.jsp" />
	</td>
</tr>
</table>

</body>
</html>
</pd4ml:transform>