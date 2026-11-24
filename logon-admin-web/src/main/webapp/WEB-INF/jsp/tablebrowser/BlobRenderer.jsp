<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: BlobRenderer.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp"%>

<c:if test="${not empty value.webValue}">

	<c:set var="mimeType" value="application/octet-stream" />

	<c:if test="${not empty value.typedValue}">
		<c:if test="${not empty row[value.typedValue].webValue}">
			<c:set var="mimeType"
				value="${mimeMap[row[value.typedValue].webValue]}" />
		</c:if>
		<c:if test="${empty row[value.typedValue].webValue}">
			<c:set var="mimeType"
				value="${table.fields[valueNr].columnMeta.mimeType}" />
		</c:if>
	</c:if>
	<c:if test="${empty value.typedValue}">
		<c:set var="mimeType"
			value="${table.fields[valueNr].columnMeta.mimeType}" />
	</c:if>
		<c:choose>
		<c:when
			test="${mimeType == 'image/gif' || mimeType == 'image/jpeg' || mimeType == 'image/png'}">
			<a
			href=<c:url value="/image" context="/logon/admin">
					<c:param name="entity" value="${tableForm.table.className}"/>
					<c:param name="keyName" value="${tableForm.table.idAttr}"/>
					<c:param name="keyValue" value="${row[0].webValue}"/>
					<c:param name="nocache" value="true"/>
					<c:param name="modTS" value="${row[7].webValue}"/> <%-- CTL_MOD_TS --%>
				</c:url>
			target="_blank">
			<img src=<c:url value="/image" context="/logon/admin">
					<c:param name="entity" value="${tableForm.table.className}"/>
					<c:param name="keyName" value="${tableForm.table.idAttr}"/>
					<c:param name="keyValue" value="${row[0].webValue}"/>
					<c:param name="nocache" value="true"/>
					<c:param name="modTS" value="${row[7].webValue}"/> <%-- CTL_MOD_TS --%>
				</c:url>
				style="max-height: 50; height = expression(this.height > 50 ? '50px' : true);"/>
			</a>
		</c:when>
		<c:otherwise>
		<a
			href="/logon/admin/blob?keyValue=<c:out value="${row[0].webValue}"/>&entity=OutputdocEntity&keyName=outputdocId"
			target="_blank">
			Link to BLOB
		</a>
		</c:otherwise>
	</c:choose>
</c:if>
