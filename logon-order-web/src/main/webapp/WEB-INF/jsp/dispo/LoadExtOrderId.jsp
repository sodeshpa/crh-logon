<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: LoadExtOrderId.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp" %>

<script type="text/javascript">

	function onExtOrderClose(){
		if (typeof refreshOrders != 'undefined') {
			onExtOrderClose = refreshOrders;
		}
		showPopup('../dispo/AssignmentEdit.do', null, {reload:true});
	}
	
	showPopup('../dispo/ExternalOrderEntry.do', null, {edit: '', extorderid: <c:out value='${order_dispo_AssignmentEditForm.extOrderId}'/> });
</script>
