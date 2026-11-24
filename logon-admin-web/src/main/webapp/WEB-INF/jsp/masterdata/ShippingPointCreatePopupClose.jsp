<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true" %>

<%@ include file="../includeTLDs.jsp" %>

<c:set var="formBean" value="${sessionScope.admin_ShippingPointCreatePopupForm.shippingPointCreateBean}"/>

<script type="text/javascript">
	hidePopup();
	loadSpointCombo('<c:out value="${formBean.plantCd}" />');

	
	spointCombo.setValue('<c:out value="${formBean.shippingpointName}" />');

	form = Ext.getDom('ShippingPointForm');
	p = {'select': '', 'spid': '<c:out value="${formBean.shippingpointId}" />'};
	addFormElements(p, form);

	var diva = Ext.get('ShippingPointDiv');
	diva.load( {
		url :'ShippingPoint.do',
		params: p,
		text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
		scripts :true
	} );
	
</script>
