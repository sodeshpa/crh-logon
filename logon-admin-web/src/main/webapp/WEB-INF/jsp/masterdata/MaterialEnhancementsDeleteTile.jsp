<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true" %>

<%@ include file="../includeTLDs.jsp" %>

Ext.onReady(function() {
	<c:set var="spacer"><br/><img src="<crh-common:url value='/images/default/s.gif'/>" width="10" height="5"/><br/></c:set>

	// --- ERRORS (red) ---
	<logic:present name="org.apache.struts.action.ERROR" scope="request">
		var errors = new Array();
		<html:messages id="msg">
			<% 
			pageContext.setAttribute("msgLines", String.valueOf(pageContext.getAttribute("msg")).split("\n"));
			%>
			errors.push('<c:forEach var="msgLine" items="${msgLines}" varStatus="status"><c:out value="${msgLine}"/><c:if test="${not status.last}"><br/></c:if></c:forEach>');
		</html:messages>
	    Logon.message.error(errors.join('<c:out value="${spacer}" escapeXml="false"/>'));
	</logic:present>
	
	// --- MESSAGES (green) ---
	<logic:present name="org.apache.struts.action.ACTION_MESSAGE" scope="request">
		var messages = new Array(); 
		<html:messages id="msg" name="org.apache.struts.action.ACTION_MESSAGE">
			<% 
			pageContext.setAttribute("msgLines", String.valueOf(pageContext.getAttribute("msg")).split("\n"));
			%>
			messages.push('<c:forEach var="msgLine" items="${msgLines}" varStatus="status"><c:out value="${msgLine}"/><c:if test="${not status.last}"><br/></c:if></c:forEach>');
		</html:messages>
	    Logon.message.success(messages.join('<c:out value="${spacer}" escapeXml="false"/>'));
	</logic:present>
	
	// --- WARNINGS (yellow) ---
	<logic:present name="org.apache.struts.action.ACTION_WARNINGS" scope="request">
		var warnings = new Array(); 
		<html:messages id="msg" name="org.apache.struts.action.ACTION_WARNINGS">
			<% 
			pageContext.setAttribute("msgLines", String.valueOf(pageContext.getAttribute("msg")).split("\n"));
			%>
			warnings.push('<c:forEach var="msgLine" items="${msgLines}" varStatus="status"><c:out value="${msgLine}"/><c:if test="${not status.last}"><br/></c:if></c:forEach>');
		</html:messages>
   		Logon.message.warning(warnings.join('<c:out value="${spacer}" escapeXml="false"/>'));
	</logic:present>

	// --- EXCEPTIONS (red) ---
	<%--
		org.apache.struts.action.ACTION_EXCEPTION holds an ActionMessages object
		org.apache.struts.action.EXCEPTION contains the actual exception
	--%>
	<logic:present name="org.apache.struts.action.EXCEPTION" scope="request">
		var errors = new Array();
		<html:messages id="msg" name="org.apache.struts.action.ACTION_EXCEPTION">
			<% 
			pageContext.setAttribute("msgLines", String.valueOf(pageContext.getAttribute("msg")).split("\n"));
			%>
			errors.push('<c:forEach var="msgLine" items="${msgLines}" varStatus="status"><c:out value="${msgLine}"/><c:if test="${not status.last}"><br/></c:if></c:forEach>');
		</html:messages>
	    Logon.message.error(errors.join('<br/>'));
	</logic:present>
});
