<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: Rfids.jsp,v $ --%>


<%@ include file="../includeTLDs.jsp"%>

<table class="classic">
	<c:forEach var="rfid" varStatus="status"
		items="${admin_ShippingPointForm.loadingPointWrapperList[admin_ShippingPointForm.currLoadingPointIndex].lpEntity.rfidobject.rfids}">
		<c:if test="${not empty rfid.rfidobject}">
			<tr class="zebra<c:out value="${status.count % 2}"/>">
				<td style="width: 100%; overflow: hidden;"><c:out
					value="${rfid.name}" /></td>
				<td valign="middle" style="width: 1px"><nobr> <a
					href="javascript:removeRFID(<c:out value="${status.count-1}"/>)"><img
					src="<crh-common:url value='/images/delete2.png'/>" /></a> </nobr></td>
			</tr>
		</c:if>
	</c:forEach>
</table>