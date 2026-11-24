<%@ include file="../includeTLDs.jsp" %>

<script type="text/javascript">
	closePopup();
	var diva = Ext.get('railwayStationDiv');
	p = {
			'selectStation' :''
		};
	diva.load( {
		url :'ShippingPoint.do',
		params : p,
		text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
		scripts :true
	});
</script>
