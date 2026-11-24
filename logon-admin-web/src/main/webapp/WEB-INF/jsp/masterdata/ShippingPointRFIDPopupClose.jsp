<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: ShippingPointRFIDPopupClose.jsp,v $ --%>


<%@ include file="../includeTLDs.jsp" %>

<script type="text/javascript">
	hidePopup();
	var diva = Ext.get('ldpointRfidDiv');
	diva.load( {
		url :'ShippingPoint.do',
		params : {
			'addRfid' :''
		},
		text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
		scripts :true
	});
</script>


