<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: BlobEditor.jsp,v $ --%>

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

	<a
		href=<c:url value="/image" context="/logon/admin">
							<c:param name="entity" value="${admin_RowEditForm.table.className}"/>
					<c:param name="keyName" value="${admin_RowEditForm.table.idAttr}"/>
					<c:param name="keyValue" value="${row[0].webValue}"/>
					<c:param name="nocache" value="true"/>
					<c:param name="modTS" value="${row[7].webValue}"/> <%-- CTL_MOD_TS --%>
				</c:url>
		target="_blank"><c:choose>
		<c:when
			test="${mimeType == 'image/gif' || mimeType == 'image/jpeg' || mimeType == 'image/png'}">
			<img
				src=<c:url value="/image" context="/logon/admin">
									<c:param name="entity" value="${admin_RowEditForm.table.className}"/>
					<c:param name="keyName" value="${admin_RowEditForm.table.idAttr}"/>
					<c:param name="keyValue" value="${row[0].webValue}"/>
					<c:param name="nocache" value="true"/>
					<c:param name="modTS" value="${row[7].webValue}"/> <%-- CTL_MOD_TS --%>
				</c:url>
				height="50" />
		</c:when>
		<c:otherwise>
			Link to BLOB
		</c:otherwise>
	</c:choose></a>&nbsp;&nbsp;<html:link page="/tablebrowser/RowEdit.do?deleteBlob">
		<img src="<crh-common:url value='/images/delete2.png'/>" border="0" alt="Delete BLOB"/>
	</html:link>
	<br />
</c:if>

<html:file property="blobValue[${valueNr}]" value="" />
