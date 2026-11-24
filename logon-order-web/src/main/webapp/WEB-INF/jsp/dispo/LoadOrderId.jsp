<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: LoadOrderId.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp" %>

<script type="text/javascript">

	function onOrderDetailClose(){
		if (typeof refreshOrders != 'undefined') {
			onOrderDetailClose = refreshOrders;
		}
		showPopup('../dispo/AssignmentEdit.do', null, {reload:true});
	}
	
	showPopup('../auftrag/OrderDetailEntry.do', null, {orderid: <c:out value='${order_dispo_AssignmentEditForm.orderId}'/> });
</script>
