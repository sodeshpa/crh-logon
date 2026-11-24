<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: OrderRfidsTile.jsp,v $ --%>
<%@page import="com.crh.logon.business.services.utils.RFIDBean.AbstractRFIDBean"%>
<%@ page import="com.crh.logon.order.web.forms.OrderRFIDPopupForm" %>
<%@ page import="com.crh.logon.persistence.entities.common.RfidobjectEntity"%>
<%@ page import="com.crh.logon.business.services.utils.RFIDBean"%>

<%@ include file="includeTLDs.jsp"%>
<%
AbstractRFIDBean rfidObjectSelection = (AbstractRFIDBean) session.getAttribute(RFIDBean.SESSION_KEY);
pageContext.setAttribute("rfidObjectSelection", rfidObjectSelection);
%>

<c:if test="${rfidObjectSelection != null && rfidObjectSelection.rfidObject != null && rfidObjectSelection.rfidObject.rfids != null }">
<table class="classic">
	<c:set var="rfidTagsOfSelectedObject" value="${rfidObjectSelection.rfidObject.rfids}"/>
	<c:forEach var="rfidTag" varStatus="status" items="${rfidTagsOfSelectedObject}">
		<c:if test="${rfidTag != null && rfidTag.rfidtagId != null && rfidTag.name != null }">
		<tr class="zebra<c:out value="${status.count % 2}"/>">
			<logic:present name="rfidTag" property="rfidobject">
				<td style="width: 100%; overflow: hidden;">
					<c:out value="${rfidTag.name}" />
				</td>
				<td valign="middle" style="width: 1px">
					<nobr>
						<a href="javascript:removeRFID(<c:out value="${rfidTag.rfidtagId}"/>,
						'<c:out value="${rfidTag.name}"/>')">
							<img src="<crh-common:url value='/images/delete2.png'/>" />
						</a>
					</nobr>
				</td>
			</logic:present>
		</tr>
		</c:if>
	</c:forEach>
</table>
</c:if>
