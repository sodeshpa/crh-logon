<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: ReferenceRenderer.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp" %>
<html:link action="/tablebrowser/TableView" name="value" property="typedValue">
<c:out value="${value.webValue}"/>
</html:link>