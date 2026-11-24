<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true" %>

<%@ include file="../includeTLDs.jsp" %>

<c:set var="shippingPointSendSapSpBean" value="${sessionScope.admin_ShippingPointSendSapSpPopupForm.shippingPointSendSapSpBean}"/>

<script type="text/javascript">
	hidePopup();

	Ext.getDom('sendSapSpShippingpointId').value = '<c:out value="${shippingPointSendSapSpBean.shippingpointId}" />';
	Ext.getDom('sendSapSpShippingpointName').value = '<c:out value="${shippingPointSendSapSpBean.shippingpointName}" />';
	Ext.getDom('sendSapSpShippingpointNo').value = '<c:out value="${shippingPointSendSapSpBean.sapShippingpointNo}" />';
	updateSpanSendSapSp();
</script>
