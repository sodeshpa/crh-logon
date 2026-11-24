<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: ReportingPreviewTile.jsp,v $ --%>
<%@ include file="../includeTLDs.jsp"%>

<c:set var="reportForm" value="${order_reporting_ReportingForm}" scope="request"/>
<table class="classicblue">
	<c:forEach var="row" items="${reportForm.previewData}" varStatus="status">
		<c:choose>
			<c:when test="${status.first}">
				<tr>
			</c:when>
			<c:otherwise>
				<c:set var="rowclass">zebra1</c:set>
				<c:if test="${status.count % 2 == 0}">
					<c:set var="rowclass">zebra0</c:set>
				</c:if>
				<tr class="<c:out value='${rowclass}'/>">
			</c:otherwise>
		</c:choose>
		<c:forEach var="val" items="${row}">
			<c:choose>
				<c:when test="${status.first}">
					<th>
				</c:when>
				<c:otherwise>
					<td>
				</c:otherwise>
			</c:choose>
			<c:out value="${val}"/>
			<c:choose>
				<c:when test="${status.first}">
					</th>
				</c:when>
				<c:otherwise>
					</td>
				</c:otherwise>
			</c:choose>
		</c:forEach>
		</tr>
	</c:forEach>
</table>