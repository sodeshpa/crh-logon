<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: OrderDetailPopupReload.jsp,v $ --%>

<%@ include file="../../includeTLDs.jsp" %>

	
<script type="text/javascript">
	orderId = <c:out value="${order_auftrag_OrderDetailForm.reloadOrderId}"/>;
	reloadOrderDirectly(orderId);
		
</script>

<%-- END $RCSfile: OrderDetailPopupReload.jsp,v $ --%>
