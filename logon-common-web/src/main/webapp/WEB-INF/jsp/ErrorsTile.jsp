<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: ErrorsTile.jsp,v $ --%>

<%@ page session="true" %>

<%@ include file="includeTLDs.jsp" %>
<%@page import="com.crh.logon.web.utils.ConfigBeanProvider"%>

<% if (ConfigBeanProvider.getConfigBean().getLogonProperties().getDevmode()) { %>
<!-- ERRORS -->
<logic:present name="org.apache.struts.action.ERROR" scope="request">
<div id="errorDiv">
<c:out value="${org.apache.struts.action.ERROR}"/>
<div id="errorId">
    <%
       java.util.Date errorDateTime = new java.util.Date(System.currentTimeMillis());
       pageContext.setAttribute("errorDateTime", errorDateTime);
    %>
    errid: <c:out value="${sessionScope.FatalErrorForm.errorId}" />&nbsp;
    <fmt:formatDate value="${pageScope.errorDateTime}" pattern="dd.MM.yyyy HH:mm" var="formattedDate" />
    <c:out value="${pageScope.formattedDate}" />&nbsp;
    ver.&nbsp;<c:out value='${applicationScope.properties.buildVersion}' />
</div>
</div> <!-- errorDiv -->
</logic:present>
<% } %>

<script type="text/javascript">
<% if (ConfigBeanProvider.getConfigBean().getLogonProperties().getDevmode()) { %>
	var errorTplNullRes = new Ext.XTemplate(
	'<table cellpadding="0" cellspacing="1" width="100%" class="errorBorder">',
	    '<tr>',
	        '<td width="100%">',
	            '<div class="error">',
	                '<ul class="error">',
	                	'<li>Ajax response is not defined</li>',
	                '</ul>',
	            '</div>',
	        '</td>',
	    '</tr>',
	'</table>'
	);
	var errorTpl = new Ext.XTemplate(
	'<table cellpadding="0" cellspacing="1" width="100%" class="errorBorder">',
	    '<tr>',
	        '<td width="100%">',
	            '<div class="error">',
	                '<ul class="error">',
	                	'<fmt:message key="fatalerror.devmodeonly"/>',
	                	'<li>{error}</li>',
	                '</ul>',
	            '</div>',
	        '</td>',
	    '</tr>',
	'</table>',
	'<div class="errorId">',
		'<fmt:message key="fatalerror.devmodeonly"/>',
	    'errid: ',
	    '<tpl for="exception">{id}&nbsp;',
		    '{datetime}&nbsp;',
		    'ver.&nbsp;<c:out value='${applicationScope.properties.buildVersion}' /><br/><br/>',
		    '<pre><b>{javaClassName}: {message}</b><br/>',
		    '<tpl for="stackTrace">',
		    	'&nbsp;&nbsp;&nbsp;&nbsp;at {className}.{methodName}({fileName}<tpl if="lineNumber &gt; 0">:{lineNumber}</tpl>)<br/>',
		    '</tpl>',
		    '<tpl if="cause">',
			    '<tpl for="cause">',
				    '<b>Caused by: {javaClassName}</b><br/>',
				    '<tpl for="stackTrace">',
				    	'&nbsp;&nbsp;&nbsp;&nbsp;at {className}.{methodName}({fileName}<tpl if="lineNumber &gt; 0">:{lineNumber}</tpl>)<br/>',
				    '</tpl>',
			    '</tpl>', 
		    '</tpl>', 
		    '</pre>',
		    '<br/>',
	    '</tpl>',
	'</div>'
	);
<% } %>
	
	Ext.onReady(function() {
	
		<c:set var="spacer"><br/><hr></c:set>
	
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
	
</script>
<%-- END $RCSfile: ErrorsTile.jsp,v $ --%>
