<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ include file="../includeTLDs.jsp" %>


<script type="text/javascript">
	hidePopup();
	if (logonPopupHideCallback) {
		logonPopupHideCallback();
	}
	// Refresh the tables by refreshing the Simple Vehicle Scheduling screen
	Ext.get('order_simpledispo_SimpleDispoForm').dom.submit();
</script>
