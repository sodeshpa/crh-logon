<%@ include file="../includeTLDs.jsp"%>
<%@ page import="com.crh.logon.business.services.beans.OrderChangeLogic"%>

<crh-order:disableSimpleChangeFunctionality orderKey="order"
	functionality="<%=OrderChangeLogic.FUNCTIONALITY_LDS_INFORMATION%>">
	<crh-order:simpleChangeEnabledBody>
		<a id='<c:out value="${order.loadId}"/>ldsInformation'
			onclick='openLdsInformationPopup("<c:out value="${order.loadId}"/>");'><img
			alt="<fmt:message key="auftragbearbeitung.button.ldsinfo"/>"
			title="<fmt:message key="auftragbearbeitung.button.ldsinfo"/>"
			src="<crh-common:url value='/images/silk/pencil.png'/>" /></a>
	</crh-order:simpleChangeEnabledBody>
</crh-order:disableSimpleChangeFunctionality>