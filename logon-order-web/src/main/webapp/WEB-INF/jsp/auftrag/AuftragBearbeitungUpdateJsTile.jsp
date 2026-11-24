<%@ page import="com.crh.logon.order.web.forms.auftrag.AuftragBearbeitungForm" %>
<%@ page import="com.crh.logon.business.services.beans.OrderChangeLogic"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.List"%>
<%@ taglib uri="http://com.crh.logon/tags/order-taglib.tld" prefix="crh-order" %>

<%@ include file="../includeTLDs.jsp" %>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<crh-common:authorize function="<%= Authorization.SHIPPING_W %>">
<script type="text/javascript">
	function updateWeighSystemButton(){
		<% 
		Long loadId = ((AuftragBearbeitungForm) session.getAttribute("order_auftrag_AuftragBearbeitungForm")).getInputLoadId();
		List orderList = ((AuftragBearbeitungForm) session.getAttribute("order_auftrag_AuftragBearbeitungForm")).getOrderList(); 
		%>
		var link = null;
		<crh-order:disableSimpleChangeFunctionality loadId = "<%=loadId%>" orderList = "<%=orderList%>" functionality="<%=OrderChangeLogic.FUNCTIONALITY_CANCEL_LOADING%>">
		<crh-order:simpleChangeEnabledBody>
			link = document.getElementById('<c:out value="${order_auftrag_AuftragBearbeitungForm.inputLoadId}"/>cancelLoading');
			link.parentNode.innerHTML = '<a id=\'<c:out value="${order_auftrag_AuftragBearbeitungForm.inputLoadId}"/>cancelLoading\' ' + 
				'onclick=\'cancelLoading("<c:out value="${order_auftrag_AuftragBearbeitungForm.inputLoadId}"/>");\'>' + 
				'<img alt="' + CANCEL_LOADING_TITLE + '" src="' + CANCEL_LOADING_IMAGE_URL + '"/>' + 
				'</a>';
			link = document.getElementById('<c:out value="${order_auftrag_AuftragBearbeitungForm.inputLoadId}"/>cancelLoading');
		</crh-order:simpleChangeEnabledBody>
		</crh-order:disableSimpleChangeFunctionality>
		<crh-order:disableSimpleChangeFunctionality loadId = "<%=loadId%>" orderList = "<%=orderList%>" functionality="<%=OrderChangeLogic.FUNCTIONALITY_TRANSMIT_TO_WEIGH_SYSTEM%>">
		<crh-order:simpleChangeEnabledBody>
			var transmitOrderHtml = '<a id=\'<c:out value="${order_auftrag_AuftragBearbeitungForm.inputLoadId}"/>transmitOrder\' onclick=\'transmitToWeighSystem("<c:out value="${order_auftrag_AuftragBearbeitungForm.inputLoadId}"/>");\'><img alt="<fmt:message key="auftragbearbeitung.button.transmitweighsystem"/>" src="<crh-common:url value="/images/balance.png"/>"/></a>';
			if (link === null) {
				link = document.getElementById(<c:out value="${order_auftrag_AuftragBearbeitungForm.inputLoadId}"/>+'transmitOrder');
				link.parentNode.innerHTML = transmitOrderHtml;
			}
			else {
				link.parentNode.innerHTML += transmitOrderHtml;
			}
		</crh-order:simpleChangeEnabledBody>
		<crh-order:simpleChangeDisabledBody>
			var transmitOrderHtml = '<a id=\'<c:out value="${order_auftrag_AuftragBearbeitungForm.inputLoadId}"/>transmitOrder\' onclick=\'function(){return false;};\'><img alt="<fmt:message key="auftragbearbeitung.button.transmitweighsystem"/>" src="<crh-common:url value="/images/balance_grey.png"/>"/></a>';
			if (link === null) {
				link = document.getElementById(<c:out value="${order_auftrag_AuftragBearbeitungForm.inputLoadId}"/>+'transmitOrder');
				link.parentNode.innerHTML = transmitOrderHtml;
			}
			else {
				link.parentNode.innerHTML += transmitOrderHtml;
			}
		</crh-order:simpleChangeDisabledBody>
		</crh-order:disableSimpleChangeFunctionality>
	}
	updateWeighSystemButton();
</script>
</crh-common:authorize>